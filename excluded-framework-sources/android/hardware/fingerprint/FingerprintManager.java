package android.hardware.fingerprint;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricFingerprintConstants;
import android.hardware.biometrics.IBiometricPromptReceiver;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.R;
import java.security.Signature;
import java.util.List;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.Mac;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class FingerprintManager implements BiometricFingerprintConstants {
    private static final boolean DEBUG = true;
    private static final int MSG_ACQUIRED = 101;
    private static final int MSG_AUTHENTICATION_FAILED = 103;
    private static final int MSG_AUTHENTICATION_SUCCEEDED = 102;
    private static final int MSG_ENROLL_RESULT = 100;
    private static final int MSG_ENUMERATED = 106;
    private static final int MSG_ERROR = 104;
    private static final int MSG_REMOVED = 105;
    private static final String TAG = "FingerprintManager";
    private BiometricAuthenticator.AuthenticationCallback mAuthenticationCallback;
    private Context mContext;
    private android.hardware.biometrics.CryptoObject mCryptoObject;
    private EnrollmentCallback mEnrollmentCallback;
    private EnumerateCallback mEnumerateCallback;
    private Executor mExecutor;
    private Handler mHandler;
    private RemovalCallback mRemovalCallback;
    private Fingerprint mRemovalFingerprint;
    private IFingerprintService mService;
    private IBinder mToken = new Binder();
    private IFingerprintServiceReceiver mServiceReceiver = new AnonymousClass2();

    public static abstract class EnrollmentCallback {
        public void onEnrollmentError(int i, CharSequence charSequence) {
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
        }

        public void onEnrollmentProgress(int i) {
        }
    }

    public static abstract class EnumerateCallback {
        public void onEnumerate(Fingerprint fingerprint) {
        }

        public void onEnumerateError(int i, CharSequence charSequence) {
        }
    }

    public static abstract class LockoutResetCallback {
        public void onLockoutReset() {
        }
    }

    public static abstract class RemovalCallback {
        public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
        }

        public void onRemovalSucceeded(Fingerprint fingerprint, int i) {
        }
    }

    private class OnEnrollCancelListener implements CancellationSignal.OnCancelListener {
        private OnEnrollCancelListener() {
        }

        /* synthetic */ OnEnrollCancelListener(FingerprintManager fingerprintManager, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.os.CancellationSignal.OnCancelListener
        public void onCancel() {
            FingerprintManager.this.cancelEnrollment();
        }
    }

    private class OnAuthenticationCancelListener implements CancellationSignal.OnCancelListener {
        private android.hardware.biometrics.CryptoObject mCrypto;

        public OnAuthenticationCancelListener(android.hardware.biometrics.CryptoObject cryptoObject) {
            this.mCrypto = cryptoObject;
        }

        @Override // android.os.CancellationSignal.OnCancelListener
        public void onCancel() {
            FingerprintManager.this.cancelAuthentication(this.mCrypto);
        }
    }

    @Deprecated
    public static final class CryptoObject extends android.hardware.biometrics.CryptoObject {
        public CryptoObject(Signature signature) {
            super(signature);
        }

        public CryptoObject(Cipher cipher) {
            super(cipher);
        }

        public CryptoObject(Mac mac) {
            super(mac);
        }

        @Override // android.hardware.biometrics.CryptoObject
        public Signature getSignature() {
            return super.getSignature();
        }

        @Override // android.hardware.biometrics.CryptoObject
        public Cipher getCipher() {
            return super.getCipher();
        }

        @Override // android.hardware.biometrics.CryptoObject
        public Mac getMac() {
            return super.getMac();
        }
    }

    @Deprecated
    public static class AuthenticationResult {
        private CryptoObject mCryptoObject;
        private Fingerprint mFingerprint;
        private int mUserId;

        public AuthenticationResult(CryptoObject cryptoObject, Fingerprint fingerprint, int i) {
            this.mCryptoObject = cryptoObject;
            this.mFingerprint = fingerprint;
            this.mUserId = i;
        }

        public CryptoObject getCryptoObject() {
            return this.mCryptoObject;
        }

        public Fingerprint getFingerprint() {
            return this.mFingerprint;
        }

        public int getUserId() {
            return this.mUserId;
        }
    }

    @Deprecated
    public static abstract class AuthenticationCallback extends BiometricAuthenticator.AuthenticationCallback {
        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationAcquired(int i) {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationError(int i, CharSequence charSequence) {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationFailed() {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationHelp(int i, CharSequence charSequence) {
        }

        public void onAuthenticationSucceeded(AuthenticationResult authenticationResult) {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationSucceeded(BiometricAuthenticator.AuthenticationResult authenticationResult) {
            onAuthenticationSucceeded(new AuthenticationResult((CryptoObject) authenticationResult.getCryptoObject(), (Fingerprint) authenticationResult.getId(), authenticationResult.getUserId()));
        }
    }

    @Deprecated
    public void authenticate(CryptoObject cryptoObject, CancellationSignal cancellationSignal, int i, AuthenticationCallback authenticationCallback, Handler handler) {
        authenticate(cryptoObject, cancellationSignal, i, authenticationCallback, handler, this.mContext.getUserId());
    }

    private void useHandler(Handler handler) {
        AnonymousClass1 anonymousClass1 = null;
        if (handler != null) {
            this.mHandler = new MyHandler(this, handler.getLooper(), anonymousClass1);
        } else if (this.mHandler.getLooper() != this.mContext.getMainLooper()) {
            this.mHandler = new MyHandler(this, this.mContext.getMainLooper(), anonymousClass1);
        }
    }

    public void authenticate(CryptoObject cryptoObject, CancellationSignal cancellationSignal, int i, AuthenticationCallback authenticationCallback, Handler handler, int i2) {
        if (authenticationCallback == null) {
            throw new IllegalArgumentException("Must supply an authentication callback");
        }
        if (cancellationSignal != null) {
            if (cancellationSignal.isCanceled()) {
                Slog.w(TAG, "authentication already canceled");
                return;
            }
            cancellationSignal.setOnCancelListener(new OnAuthenticationCancelListener(cryptoObject));
        }
        if (this.mService != null) {
            try {
                useHandler(handler);
                this.mAuthenticationCallback = authenticationCallback;
                this.mCryptoObject = cryptoObject;
                this.mService.authenticate(this.mToken, cryptoObject != null ? cryptoObject.getOpId() : 0L, i2, this.mServiceReceiver, i, this.mContext.getOpPackageName(), null, null);
            } catch (RemoteException e) {
                Slog.w(TAG, "Remote exception while authenticating: ", e);
                if (authenticationCallback != null) {
                    authenticationCallback.onAuthenticationError(1, getErrorString(1, 0));
                }
            }
        }
    }

    private void authenticate(int i, android.hardware.biometrics.CryptoObject cryptoObject, CancellationSignal cancellationSignal, Bundle bundle, Executor executor, IBiometricPromptReceiver iBiometricPromptReceiver, final BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        this.mCryptoObject = cryptoObject;
        if (cancellationSignal.isCanceled()) {
            Slog.w(TAG, "authentication already canceled");
            return;
        }
        cancellationSignal.setOnCancelListener(new OnAuthenticationCancelListener(cryptoObject));
        if (this.mService != null) {
            try {
                this.mExecutor = executor;
                this.mAuthenticationCallback = authenticationCallback;
                this.mService.authenticate(this.mToken, cryptoObject != null ? cryptoObject.getOpId() : 0L, i, this.mServiceReceiver, 0, this.mContext.getOpPackageName(), bundle, iBiometricPromptReceiver);
            } catch (RemoteException e) {
                Slog.w(TAG, "Remote exception while authenticating", e);
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$0Q_OnkqSSy_nQ9iUWqvqVi6QjNE
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$authenticate$0$FingerprintManager(authenticationCallback);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$authenticate$0$FingerprintManager(BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        authenticationCallback.onAuthenticationError(1, getErrorString(1, 0));
    }

    public void authenticate(CancellationSignal cancellationSignal, Bundle bundle, Executor executor, IBiometricPromptReceiver iBiometricPromptReceiver, BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        if (cancellationSignal == null) {
            throw new IllegalArgumentException("Must supply a cancellation signal");
        }
        if (bundle == null) {
            throw new IllegalArgumentException("Must supply a bundle");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must supply an executor");
        }
        if (iBiometricPromptReceiver == null) {
            throw new IllegalArgumentException("Must supply a receiver");
        }
        if (authenticationCallback == null) {
            throw new IllegalArgumentException("Must supply a calback");
        }
        authenticate(this.mContext.getUserId(), null, cancellationSignal, bundle, executor, iBiometricPromptReceiver, authenticationCallback);
    }

    public void authenticate(android.hardware.biometrics.CryptoObject cryptoObject, CancellationSignal cancellationSignal, Bundle bundle, Executor executor, IBiometricPromptReceiver iBiometricPromptReceiver, BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        if (cryptoObject == null) {
            throw new IllegalArgumentException("Must supply a crypto object");
        }
        if (cancellationSignal == null) {
            throw new IllegalArgumentException("Must supply a cancellation signal");
        }
        if (bundle == null) {
            throw new IllegalArgumentException("Must supply a bundle");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must supply an executor");
        }
        if (iBiometricPromptReceiver == null) {
            throw new IllegalArgumentException("Must supply a receiver");
        }
        if (authenticationCallback == null) {
            throw new IllegalArgumentException("Must supply a callback");
        }
        authenticate(this.mContext.getUserId(), cryptoObject, cancellationSignal, bundle, executor, iBiometricPromptReceiver, authenticationCallback);
    }

    public void enroll(byte[] bArr, CancellationSignal cancellationSignal, int i, int i2, EnrollmentCallback enrollmentCallback) {
        if (i2 == -2) {
            i2 = getCurrentUserId();
        }
        int i3 = i2;
        if (enrollmentCallback == null) {
            throw new IllegalArgumentException("Must supply an enrollment callback");
        }
        if (cancellationSignal != null) {
            if (cancellationSignal.isCanceled()) {
                Slog.w(TAG, "enrollment already canceled");
                return;
            }
            cancellationSignal.setOnCancelListener(new OnEnrollCancelListener(this, null));
        }
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                this.mEnrollmentCallback = enrollmentCallback;
                iFingerprintService.enroll(this.mToken, bArr, i3, this.mServiceReceiver, i, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Slog.w(TAG, "Remote exception in enroll: ", e);
                if (enrollmentCallback != null) {
                    enrollmentCallback.onEnrollmentError(1, getErrorString(1, 0));
                }
            }
        }
    }

    public long preEnroll() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService == null) {
            return 0L;
        }
        try {
            return iFingerprintService.preEnroll(this.mToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int postEnroll() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService == null) {
            return 0;
        }
        try {
            return iFingerprintService.postEnroll(this.mToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setActiveUser(int i) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                iFingerprintService.setActiveUser(i);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void remove(Fingerprint fingerprint, int i, RemovalCallback removalCallback) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                this.mRemovalCallback = removalCallback;
                this.mRemovalFingerprint = fingerprint;
                iFingerprintService.remove(this.mToken, fingerprint.getFingerId(), fingerprint.getGroupId(), i, this.mServiceReceiver);
            } catch (RemoteException e) {
                Slog.w(TAG, "Remote exception in remove: ", e);
                if (removalCallback != null) {
                    removalCallback.onRemovalError(fingerprint, 1, getErrorString(1, 0));
                }
            }
        }
    }

    public void enumerate(int i, EnumerateCallback enumerateCallback) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                this.mEnumerateCallback = enumerateCallback;
                iFingerprintService.enumerate(this.mToken, i, this.mServiceReceiver);
            } catch (RemoteException e) {
                Slog.w(TAG, "Remote exception in enumerate: ", e);
                if (enumerateCallback != null) {
                    enumerateCallback.onEnumerateError(1, getErrorString(1, 0));
                }
            }
        }
    }

    public void rename(int i, int i2, String str) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                iFingerprintService.rename(i, i2, str);
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.w(TAG, "rename(): Service not connected!");
    }

    public List<Fingerprint> getEnrolledFingerprints(int i) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService == null) {
            return null;
        }
        try {
            return iFingerprintService.getEnrolledFingerprints(i, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<Fingerprint> getEnrolledFingerprints() {
        return getEnrolledFingerprints(this.mContext.getUserId());
    }

    @Deprecated
    public boolean hasEnrolledFingerprints() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService == null) {
            return false;
        }
        try {
            return iFingerprintService.hasEnrolledFingerprints(this.mContext.getUserId(), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasEnrolledFingerprints(int i) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService == null) {
            return false;
        }
        try {
            return iFingerprintService.hasEnrolledFingerprints(i, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean isHardwareDetected() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                return iFingerprintService.isHardwareDetected(0L, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.w(TAG, "isFingerprintHardwareDetected(): Service not connected!");
        return false;
    }

    public long getAuthenticatorId() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                return iFingerprintService.getAuthenticatorId(this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.w(TAG, "getAuthenticatorId(): Service not connected!");
        return 0L;
    }

    public void resetTimeout(byte[] bArr) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                iFingerprintService.resetTimeout(bArr);
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.w(TAG, "resetTimeout(): Service not connected!");
    }

    public void addLockoutResetCallback(LockoutResetCallback lockoutResetCallback) {
        if (this.mService != null) {
            try {
                this.mService.addLockoutResetCallback(new AnonymousClass1((PowerManager) this.mContext.getSystemService(PowerManager.class), lockoutResetCallback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.w(TAG, "addLockoutResetCallback(): Service not connected!");
    }

    /* JADX INFO: renamed from: android.hardware.fingerprint.FingerprintManager$1, reason: invalid class name */
    class AnonymousClass1 extends IFingerprintServiceLockoutResetCallback.Stub {
        final /* synthetic */ LockoutResetCallback val$callback;
        final /* synthetic */ PowerManager val$powerManager;

        AnonymousClass1(PowerManager powerManager, LockoutResetCallback lockoutResetCallback) {
            this.val$powerManager = powerManager;
            this.val$callback = lockoutResetCallback;
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceLockoutResetCallback
        public void onLockoutReset(long j, IRemoteCallback iRemoteCallback) throws RemoteException {
            try {
                final PowerManager.WakeLock wakeLockNewWakeLock = this.val$powerManager.newWakeLock(1, "lockoutResetCallback");
                wakeLockNewWakeLock.acquire();
                Handler handler = FingerprintManager.this.mHandler;
                final LockoutResetCallback lockoutResetCallback = this.val$callback;
                handler.post(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$1$4i3tUU8mafgvA9HaB2UPD31L6UY
                    @Override // java.lang.Runnable
                    public final void run() {
                        FingerprintManager.AnonymousClass1.lambda$onLockoutReset$0(lockoutResetCallback, wakeLockNewWakeLock);
                    }
                });
            } finally {
                iRemoteCallback.sendResult(null);
            }
        }

        static /* synthetic */ void lambda$onLockoutReset$0(LockoutResetCallback lockoutResetCallback, PowerManager.WakeLock wakeLock) {
            try {
                lockoutResetCallback.onLockoutReset();
            } finally {
                wakeLock.release();
            }
        }
    }

    private class MyHandler extends Handler {
        /* synthetic */ MyHandler(FingerprintManager fingerprintManager, Context context, AnonymousClass1 anonymousClass1) {
            this(context);
        }

        /* synthetic */ MyHandler(FingerprintManager fingerprintManager, Looper looper, AnonymousClass1 anonymousClass1) {
            this(looper);
        }

        private MyHandler(Context context) {
            super(context.getMainLooper());
        }

        private MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 100:
                    sendEnrollResult((Fingerprint) message.obj, message.arg1);
                    break;
                case 101:
                    FingerprintManager.this.sendAcquiredResult(((Long) message.obj).longValue(), message.arg1, message.arg2);
                    break;
                case 102:
                    FingerprintManager.this.sendAuthenticatedSucceeded((Fingerprint) message.obj, message.arg1);
                    break;
                case 103:
                    FingerprintManager.this.sendAuthenticatedFailed();
                    break;
                case 104:
                    FingerprintManager.this.sendErrorResult(((Long) message.obj).longValue(), message.arg1, message.arg2);
                    break;
                case 105:
                    sendRemovedResult((Fingerprint) message.obj, message.arg1);
                    break;
                case 106:
                    sendEnumeratedResult(((Long) message.obj).longValue(), message.arg1, message.arg2);
                    break;
            }
        }

        private void sendRemovedResult(Fingerprint fingerprint, int i) {
            if (FingerprintManager.this.mRemovalCallback == null) {
                return;
            }
            if (fingerprint == null) {
                Slog.e(FingerprintManager.TAG, "Received MSG_REMOVED, but fingerprint is null");
                return;
            }
            int fingerId = fingerprint.getFingerId();
            int fingerId2 = FingerprintManager.this.mRemovalFingerprint.getFingerId();
            if (fingerId2 != 0 && fingerId != 0 && fingerId != fingerId2) {
                Slog.w(FingerprintManager.TAG, "Finger id didn't match: " + fingerId + " != " + fingerId2);
                return;
            }
            int groupId = fingerprint.getGroupId();
            int groupId2 = FingerprintManager.this.mRemovalFingerprint.getGroupId();
            if (groupId == groupId2) {
                FingerprintManager.this.mRemovalCallback.onRemovalSucceeded(fingerprint, i);
                return;
            }
            Slog.w(FingerprintManager.TAG, "Group id didn't match: " + groupId + " != " + groupId2);
        }

        private void sendEnumeratedResult(long j, int i, int i2) {
            if (FingerprintManager.this.mEnumerateCallback != null) {
                FingerprintManager.this.mEnumerateCallback.onEnumerate(new Fingerprint(null, i2, i, j));
            }
        }

        private void sendEnrollResult(Fingerprint fingerprint, int i) {
            if (FingerprintManager.this.mEnrollmentCallback != null) {
                FingerprintManager.this.mEnrollmentCallback.onEnrollmentProgress(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAuthenticatedSucceeded(Fingerprint fingerprint, int i) {
        if (this.mAuthenticationCallback != null) {
            this.mAuthenticationCallback.onAuthenticationSucceeded(new BiometricAuthenticator.AuthenticationResult(this.mCryptoObject, fingerprint, i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAuthenticatedFailed() {
        BiometricAuthenticator.AuthenticationCallback authenticationCallback = this.mAuthenticationCallback;
        if (authenticationCallback != null) {
            authenticationCallback.onAuthenticationFailed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAcquiredResult(long j, int i, int i2) {
        BiometricAuthenticator.AuthenticationCallback authenticationCallback = this.mAuthenticationCallback;
        if (authenticationCallback != null) {
            authenticationCallback.onAuthenticationAcquired(i);
        }
        String acquiredString = getAcquiredString(i, i2);
        if (acquiredString == null) {
            return;
        }
        if (i == 6) {
            i = i2 + 1000;
        }
        EnrollmentCallback enrollmentCallback = this.mEnrollmentCallback;
        if (enrollmentCallback != null) {
            enrollmentCallback.onEnrollmentHelp(i, acquiredString);
            return;
        }
        BiometricAuthenticator.AuthenticationCallback authenticationCallback2 = this.mAuthenticationCallback;
        if (authenticationCallback2 != null) {
            authenticationCallback2.onAuthenticationHelp(i, acquiredString);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendErrorResult(long j, int i, int i2) {
        int i3 = i == 8 ? i2 + 1000 : i;
        EnrollmentCallback enrollmentCallback = this.mEnrollmentCallback;
        if (enrollmentCallback != null) {
            enrollmentCallback.onEnrollmentError(i3, getErrorString(i, i2));
            return;
        }
        BiometricAuthenticator.AuthenticationCallback authenticationCallback = this.mAuthenticationCallback;
        if (authenticationCallback != null) {
            authenticationCallback.onAuthenticationError(i3, getErrorString(i, i2));
            return;
        }
        RemovalCallback removalCallback = this.mRemovalCallback;
        if (removalCallback != null) {
            removalCallback.onRemovalError(this.mRemovalFingerprint, i3, getErrorString(i, i2));
            return;
        }
        EnumerateCallback enumerateCallback = this.mEnumerateCallback;
        if (enumerateCallback != null) {
            enumerateCallback.onEnumerateError(i3, getErrorString(i, i2));
        }
    }

    public FingerprintManager(Context context, IFingerprintService iFingerprintService) {
        this.mContext = context;
        this.mService = iFingerprintService;
        if (iFingerprintService == null) {
            Slog.v(TAG, "FingerprintManagerService was null");
        }
        this.mHandler = new MyHandler(this, context, (AnonymousClass1) null);
    }

    private int getCurrentUserId() {
        try {
            return ActivityManager.getService().getCurrentUser().id;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelEnrollment() {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                iFingerprintService.cancelEnrollment(this.mToken);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAuthentication(android.hardware.biometrics.CryptoObject cryptoObject) {
        IFingerprintService iFingerprintService = this.mService;
        if (iFingerprintService != null) {
            try {
                iFingerprintService.cancelAuthentication(this.mToken, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String getErrorString(int i, int i2) {
        switch (i) {
            case 1:
                return this.mContext.getString(R.string.fingerprint_error_hw_not_available);
            case 2:
                return this.mContext.getString(R.string.fingerprint_error_unable_to_process);
            case 3:
                return this.mContext.getString(R.string.fingerprint_error_timeout);
            case 4:
                return this.mContext.getString(R.string.fingerprint_error_no_space);
            case 5:
                return this.mContext.getString(R.string.fingerprint_error_canceled);
            case 7:
                return this.mContext.getString(R.string.fingerprint_error_lockout);
            case 8:
                String[] stringArray = this.mContext.getResources().getStringArray(R.array.fingerprint_error_vendor);
                if (i2 < stringArray.length) {
                    return stringArray[i2];
                }
                break;
            case 9:
                return this.mContext.getString(R.string.fingerprint_error_lockout_permanent);
            case 10:
                return this.mContext.getString(R.string.fingerprint_error_user_canceled);
            case 11:
                return this.mContext.getString(R.string.fingerprint_error_no_fingerprints);
            case 12:
                return this.mContext.getString(R.string.fingerprint_error_hw_not_present);
        }
        Slog.w(TAG, "Invalid error message: " + i + ", " + i2);
        return null;
    }

    public String getAcquiredString(int i, int i2) {
        switch (i) {
            case 0:
                return null;
            case 1:
                return this.mContext.getString(R.string.fingerprint_acquired_partial);
            case 2:
                return this.mContext.getString(R.string.fingerprint_acquired_insufficient);
            case 3:
                return this.mContext.getString(R.string.fingerprint_acquired_imager_dirty);
            case 4:
                return this.mContext.getString(R.string.fingerprint_acquired_too_slow);
            case 5:
                return this.mContext.getString(R.string.fingerprint_acquired_too_fast);
            case 6:
                String[] stringArray = this.mContext.getResources().getStringArray(R.array.fingerprint_acquired_vendor);
                if (i2 < stringArray.length) {
                    return stringArray[i2];
                }
                break;
        }
        Slog.w(TAG, "Invalid acquired message: " + i + ", " + i2);
        return null;
    }

    /* JADX INFO: renamed from: android.hardware.fingerprint.FingerprintManager$2, reason: invalid class name */
    class AnonymousClass2 extends IFingerprintServiceReceiver.Stub {
        AnonymousClass2() {
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onEnrollResult(long j, int i, int i2, int i3) {
            FingerprintManager.this.mHandler.obtainMessage(100, i3, 0, new Fingerprint(null, i2, i, j)).sendToTarget();
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAcquired(final long j, final int i, final int i2) {
            if (FingerprintManager.this.mExecutor != null) {
                FingerprintManager.this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$-CkUh5EAfiFsfsEamQtkeaLZq6M
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onAcquired$0$FingerprintManager$2(j, i, i2);
                    }
                });
            } else {
                FingerprintManager.this.mHandler.obtainMessage(101, i, i2, Long.valueOf(j)).sendToTarget();
            }
        }

        public /* synthetic */ void lambda$onAcquired$0$FingerprintManager$2(long j, int i, int i2) {
            FingerprintManager.this.sendAcquiredResult(j, i, i2);
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAuthenticationSucceeded(long j, final Fingerprint fingerprint, final int i) {
            if (FingerprintManager.this.mExecutor != null) {
                FingerprintManager.this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$O5sigT8DLDwmCzdvD-k13MacOBU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onAuthenticationSucceeded$1$FingerprintManager$2(fingerprint, i);
                    }
                });
            } else {
                FingerprintManager.this.mHandler.obtainMessage(102, i, 0, fingerprint).sendToTarget();
            }
        }

        public /* synthetic */ void lambda$onAuthenticationSucceeded$1$FingerprintManager$2(Fingerprint fingerprint, int i) {
            FingerprintManager.this.sendAuthenticatedSucceeded(fingerprint, i);
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onAuthenticationFailed(long j) {
            if (FingerprintManager.this.mExecutor != null) {
                FingerprintManager.this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$ycpCnXGQKksU_rpxKvBm1XDbloE
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onAuthenticationFailed$2$FingerprintManager$2();
                    }
                });
            } else {
                FingerprintManager.this.mHandler.obtainMessage(103).sendToTarget();
            }
        }

        public /* synthetic */ void lambda$onAuthenticationFailed$2$FingerprintManager$2() {
            FingerprintManager.this.sendAuthenticatedFailed();
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onError(final long j, final int i, final int i2) {
            if (FingerprintManager.this.mExecutor == null) {
                FingerprintManager.this.mHandler.obtainMessage(104, i, i2, Long.valueOf(j)).sendToTarget();
            } else if (i == 10 || i == 5) {
                FingerprintManager.this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$iiSGvjInjtzVqJ-wXw-4RQIjKDs
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onError$3$FingerprintManager$2(j, i, i2);
                    }
                });
            } else {
                FingerprintManager.this.mHandler.postDelayed(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$n67wlbYWr0PNZwBB3xLLO4RgAq4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onError$5$FingerprintManager$2(j, i, i2);
                    }
                }, 2000L);
            }
        }

        public /* synthetic */ void lambda$onError$3$FingerprintManager$2(long j, int i, int i2) {
            FingerprintManager.this.sendErrorResult(j, i, i2);
        }

        public /* synthetic */ void lambda$onError$5$FingerprintManager$2(final long j, final int i, final int i2) {
            FingerprintManager.this.mExecutor.execute(new Runnable() { // from class: android.hardware.fingerprint.-$$Lambda$FingerprintManager$2$DBgvtkIDrK5T5S0UgEanHtoKmOA
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onError$4$FingerprintManager$2(j, i, i2);
                }
            });
        }

        public /* synthetic */ void lambda$onError$4$FingerprintManager$2(long j, int i, int i2) {
            FingerprintManager.this.sendErrorResult(j, i, i2);
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onRemoved(long j, int i, int i2, int i3) {
            FingerprintManager.this.mHandler.obtainMessage(105, i3, 0, new Fingerprint(null, i2, i, j)).sendToTarget();
        }

        @Override // android.hardware.fingerprint.IFingerprintServiceReceiver
        public void onEnumerated(long j, int i, int i2, int i3) {
            FingerprintManager.this.mHandler.obtainMessage(106, i, i2, Long.valueOf(j)).sendToTarget();
        }
    }
}
