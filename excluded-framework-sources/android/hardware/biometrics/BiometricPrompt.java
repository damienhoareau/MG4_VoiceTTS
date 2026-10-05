package android.hardware.biometrics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import java.security.Signature;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.Mac;

/* JADX INFO: loaded from: classes.dex */
public class BiometricPrompt implements BiometricAuthenticator, BiometricConstants {
    public static final int DISMISSED_REASON_NEGATIVE = 2;
    public static final int DISMISSED_REASON_POSITIVE = 1;
    public static final int DISMISSED_REASON_USER_CANCEL = 3;
    public static final int HIDE_DIALOG_DELAY = 2000;
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NEGATIVE_TEXT = "negative_text";
    public static final String KEY_POSITIVE_TEXT = "positive_text";
    public static final String KEY_SUBTITLE = "subtitle";
    public static final String KEY_TITLE = "title";
    private Bundle mBundle;
    IBiometricPromptReceiver mDialogReceiver;
    private FingerprintManager mFingerprintManager;
    private ButtonInfo mNegativeButtonInfo;
    private PackageManager mPackageManager;
    private ButtonInfo mPositiveButtonInfo;

    /* synthetic */ BiometricPrompt(Context context, Bundle bundle, ButtonInfo buttonInfo, ButtonInfo buttonInfo2, AnonymousClass1 anonymousClass1) {
        this(context, bundle, buttonInfo, buttonInfo2);
    }

    private static class ButtonInfo {
        Executor executor;
        DialogInterface.OnClickListener listener;

        ButtonInfo(Executor executor, DialogInterface.OnClickListener onClickListener) {
            this.executor = executor;
            this.listener = onClickListener;
        }
    }

    public static class Builder {
        private final Bundle mBundle = new Bundle();
        private Context mContext;
        private ButtonInfo mNegativeButtonInfo;
        private ButtonInfo mPositiveButtonInfo;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mBundle.putCharSequence("title", charSequence);
            return this;
        }

        public Builder setSubtitle(CharSequence charSequence) {
            this.mBundle.putCharSequence(BiometricPrompt.KEY_SUBTITLE, charSequence);
            return this;
        }

        public Builder setDescription(CharSequence charSequence) {
            this.mBundle.putCharSequence("description", charSequence);
            return this;
        }

        public Builder setPositiveButton(CharSequence charSequence, Executor executor, DialogInterface.OnClickListener onClickListener) {
            if (TextUtils.isEmpty(charSequence)) {
                throw new IllegalArgumentException("Text must be set and non-empty");
            }
            if (executor == null) {
                throw new IllegalArgumentException("Executor must not be null");
            }
            if (onClickListener == null) {
                throw new IllegalArgumentException("Listener must not be null");
            }
            this.mBundle.putCharSequence(BiometricPrompt.KEY_POSITIVE_TEXT, charSequence);
            this.mPositiveButtonInfo = new ButtonInfo(executor, onClickListener);
            return this;
        }

        public Builder setNegativeButton(CharSequence charSequence, Executor executor, DialogInterface.OnClickListener onClickListener) {
            if (TextUtils.isEmpty(charSequence)) {
                throw new IllegalArgumentException("Text must be set and non-empty");
            }
            if (executor == null) {
                throw new IllegalArgumentException("Executor must not be null");
            }
            if (onClickListener == null) {
                throw new IllegalArgumentException("Listener must not be null");
            }
            this.mBundle.putCharSequence(BiometricPrompt.KEY_NEGATIVE_TEXT, charSequence);
            this.mNegativeButtonInfo = new ButtonInfo(executor, onClickListener);
            return this;
        }

        public BiometricPrompt build() {
            CharSequence charSequence = this.mBundle.getCharSequence("title");
            CharSequence charSequence2 = this.mBundle.getCharSequence(BiometricPrompt.KEY_NEGATIVE_TEXT);
            if (TextUtils.isEmpty(charSequence)) {
                throw new IllegalArgumentException("Title must be set and non-empty");
            }
            if (TextUtils.isEmpty(charSequence2)) {
                throw new IllegalArgumentException("Negative text must be set and non-empty");
            }
            return new BiometricPrompt(this.mContext, this.mBundle, this.mPositiveButtonInfo, this.mNegativeButtonInfo, null);
        }
    }

    /* JADX INFO: renamed from: android.hardware.biometrics.BiometricPrompt$1, reason: invalid class name */
    class AnonymousClass1 extends IBiometricPromptReceiver.Stub {
        AnonymousClass1() {
        }

        @Override // android.hardware.biometrics.IBiometricPromptReceiver
        public void onDialogDismissed(int i) {
            if (i == 1) {
                BiometricPrompt.this.mPositiveButtonInfo.executor.execute(new Runnable() { // from class: android.hardware.biometrics.-$$Lambda$BiometricPrompt$1$C3fuslKNv7eJTZG9_jFRfCo5_Y4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onDialogDismissed$0$BiometricPrompt$1();
                    }
                });
            } else if (i == 2) {
                BiometricPrompt.this.mNegativeButtonInfo.executor.execute(new Runnable() { // from class: android.hardware.biometrics.-$$Lambda$BiometricPrompt$1$J5PqpiT8xZNiNN1gy9VraVgknaQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onDialogDismissed$1$BiometricPrompt$1();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onDialogDismissed$0$BiometricPrompt$1() {
            BiometricPrompt.this.mPositiveButtonInfo.listener.onClick(null, -1);
        }

        public /* synthetic */ void lambda$onDialogDismissed$1$BiometricPrompt$1() {
            BiometricPrompt.this.mNegativeButtonInfo.listener.onClick(null, -2);
        }
    }

    private BiometricPrompt(Context context, Bundle bundle, ButtonInfo buttonInfo, ButtonInfo buttonInfo2) {
        this.mDialogReceiver = new AnonymousClass1();
        this.mBundle = bundle;
        this.mPositiveButtonInfo = buttonInfo;
        this.mNegativeButtonInfo = buttonInfo2;
        this.mFingerprintManager = (FingerprintManager) context.getSystemService(FingerprintManager.class);
        this.mPackageManager = context.getPackageManager();
    }

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

    public static class AuthenticationResult extends BiometricAuthenticator.AuthenticationResult {
        public AuthenticationResult(CryptoObject cryptoObject, BiometricAuthenticator.BiometricIdentifier biometricIdentifier, int i) {
            super(cryptoObject, biometricIdentifier, i);
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationResult
        public CryptoObject getCryptoObject() {
            return (CryptoObject) super.getCryptoObject();
        }
    }

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
            onAuthenticationSucceeded(new AuthenticationResult((CryptoObject) authenticationResult.getCryptoObject(), authenticationResult.getId(), authenticationResult.getUserId()));
        }
    }

    @Override // android.hardware.biometrics.BiometricAuthenticator
    public void authenticate(android.hardware.biometrics.CryptoObject cryptoObject, CancellationSignal cancellationSignal, Executor executor, BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        if (!(authenticationCallback instanceof AuthenticationCallback)) {
            throw new IllegalArgumentException("Callback cannot be casted");
        }
        authenticate(cryptoObject, cancellationSignal, executor, (AuthenticationCallback) authenticationCallback);
    }

    @Override // android.hardware.biometrics.BiometricAuthenticator
    public void authenticate(CancellationSignal cancellationSignal, Executor executor, BiometricAuthenticator.AuthenticationCallback authenticationCallback) {
        if (!(authenticationCallback instanceof AuthenticationCallback)) {
            throw new IllegalArgumentException("Callback cannot be casted");
        }
        authenticate(cancellationSignal, executor, (AuthenticationCallback) authenticationCallback);
    }

    public void authenticate(CryptoObject cryptoObject, CancellationSignal cancellationSignal, Executor executor, AuthenticationCallback authenticationCallback) {
        if (handlePreAuthenticationErrors(authenticationCallback, executor)) {
            return;
        }
        this.mFingerprintManager.authenticate(cryptoObject, cancellationSignal, this.mBundle, executor, this.mDialogReceiver, authenticationCallback);
    }

    public void authenticate(CancellationSignal cancellationSignal, Executor executor, AuthenticationCallback authenticationCallback) {
        if (handlePreAuthenticationErrors(authenticationCallback, executor)) {
            return;
        }
        this.mFingerprintManager.authenticate(cancellationSignal, this.mBundle, executor, this.mDialogReceiver, authenticationCallback);
    }

    private boolean handlePreAuthenticationErrors(AuthenticationCallback authenticationCallback, Executor executor) {
        if (!this.mPackageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            sendError(12, authenticationCallback, executor);
            return true;
        }
        if (!this.mFingerprintManager.isHardwareDetected()) {
            sendError(1, authenticationCallback, executor);
            return true;
        }
        if (this.mFingerprintManager.hasEnrolledFingerprints()) {
            return false;
        }
        sendError(11, authenticationCallback, executor);
        return true;
    }

    private void sendError(final int i, final AuthenticationCallback authenticationCallback, Executor executor) {
        executor.execute(new Runnable() { // from class: android.hardware.biometrics.-$$Lambda$BiometricPrompt$HqBGXtBUWNc-v8NoHYsj2gLfaRw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendError$0$BiometricPrompt(authenticationCallback, i);
            }
        });
    }

    public /* synthetic */ void lambda$sendError$0$BiometricPrompt(AuthenticationCallback authenticationCallback, int i) {
        authenticationCallback.onAuthenticationError(i, this.mFingerprintManager.getErrorString(i, 0));
    }
}
