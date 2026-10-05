package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.net.Uri;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class ImsRegistrationImplBase {
    private static final String LOG_TAG = "ImsRegistrationImplBase";
    private static final int REGISTRATION_STATE_NOT_REGISTERED = 0;
    private static final int REGISTRATION_STATE_REGISTERED = 2;
    private static final int REGISTRATION_STATE_REGISTERING = 1;
    private static final int REGISTRATION_STATE_UNKNOWN = -1;
    public static final int REGISTRATION_TECH_IWLAN = 1;
    public static final int REGISTRATION_TECH_LTE = 0;
    public static final int REGISTRATION_TECH_NONE = -1;
    private final IImsRegistration mBinder = new IImsRegistration.Stub() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase.1
        @Override // android.telephony.ims.aidl.IImsRegistration
        public int getRegistrationTechnology() throws RemoteException {
            return ImsRegistrationImplBase.this.getConnectionType();
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void addRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException {
            ImsRegistrationImplBase.this.addRegistrationCallback(iImsRegistrationCallback);
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void removeRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException {
            ImsRegistrationImplBase.this.removeRegistrationCallback(iImsRegistrationCallback);
        }
    };
    private final RemoteCallbackList<IImsRegistrationCallback> mCallbacks = new RemoteCallbackList<>();
    private final Object mLock = new Object();
    private int mConnectionType = -1;
    private int mRegistrationState = -1;
    private ImsReasonInfo mLastDisconnectCause = new ImsReasonInfo();

    public static class Callback {
        public void onDeregistered(ImsReasonInfo imsReasonInfo) {
        }

        public void onRegistered(int i) {
        }

        public void onRegistering(int i) {
        }

        public void onSubscriberAssociatedUriChanged(Uri[] uriArr) {
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ImsRegistrationTech {
    }

    public final IImsRegistration getBinder() {
        return this.mBinder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException {
        this.mCallbacks.register(iImsRegistrationCallback);
        updateNewCallbackWithState(iImsRegistrationCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) {
        this.mCallbacks.unregister(iImsRegistrationCallback);
    }

    public final void onRegistered(final int i) {
        updateToState(i, 2);
        this.mCallbacks.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsRegistrationImplBase$cWwTXSDsk-bWPbsDJYI--DUBMnE
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onRegistered$0(i, (IImsRegistrationCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$onRegistered$0(int i, IImsRegistrationCallback iImsRegistrationCallback) {
        try {
            iImsRegistrationCallback.onRegistered(i);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, e + " onRegistrationConnected() - Skipping callback.");
        }
    }

    public final void onRegistering(final int i) {
        updateToState(i, 1);
        this.mCallbacks.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsRegistrationImplBase$sbjuTvW-brOSWMR74UInSZEIQB0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onRegistering$1(i, (IImsRegistrationCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$onRegistering$1(int i, IImsRegistrationCallback iImsRegistrationCallback) {
        try {
            iImsRegistrationCallback.onRegistering(i);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, e + " onRegistrationProcessing() - Skipping callback.");
        }
    }

    public final void onDeregistered(final ImsReasonInfo imsReasonInfo) {
        updateToDisconnectedState(imsReasonInfo);
        this.mCallbacks.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsRegistrationImplBase$s7PspXVbCf1Q_WSzodP2glP9TjI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onDeregistered$2(imsReasonInfo, (IImsRegistrationCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$onDeregistered$2(ImsReasonInfo imsReasonInfo, IImsRegistrationCallback iImsRegistrationCallback) {
        try {
            iImsRegistrationCallback.onDeregistered(imsReasonInfo);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, e + " onRegistrationDisconnected() - Skipping callback.");
        }
    }

    public final void onTechnologyChangeFailed(final int i, final ImsReasonInfo imsReasonInfo) {
        this.mCallbacks.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsRegistrationImplBase$wDtW65cPmn_jF6dfimhBTfdg1kI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onTechnologyChangeFailed$3(i, imsReasonInfo, (IImsRegistrationCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$onTechnologyChangeFailed$3(int i, ImsReasonInfo imsReasonInfo, IImsRegistrationCallback iImsRegistrationCallback) {
        try {
            iImsRegistrationCallback.onTechnologyChangeFailed(i, imsReasonInfo);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, e + " onRegistrationChangeFailed() - Skipping callback.");
        }
    }

    public final void onSubscriberAssociatedUriChanged(final Uri[] uriArr) {
        this.mCallbacks.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsRegistrationImplBase$wwtkoeOtGwMjG5I0-ZTfjNpGU-s
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onSubscriberAssociatedUriChanged$4(uriArr, (IImsRegistrationCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$onSubscriberAssociatedUriChanged$4(Uri[] uriArr, IImsRegistrationCallback iImsRegistrationCallback) {
        try {
            iImsRegistrationCallback.onSubscriberAssociatedUriChanged(uriArr);
        } catch (RemoteException e) {
            Log.w(LOG_TAG, e + " onSubscriberAssociatedUriChanged() - Skipping callback.");
        }
    }

    private void updateToState(int i, int i2) {
        synchronized (this.mLock) {
            this.mConnectionType = i;
            this.mRegistrationState = i2;
            this.mLastDisconnectCause = null;
        }
    }

    private void updateToDisconnectedState(ImsReasonInfo imsReasonInfo) {
        synchronized (this.mLock) {
            updateToState(-1, 0);
            if (imsReasonInfo != null) {
                this.mLastDisconnectCause = imsReasonInfo;
            } else {
                Log.w(LOG_TAG, "updateToDisconnectedState: no ImsReasonInfo provided.");
                this.mLastDisconnectCause = new ImsReasonInfo();
            }
        }
    }

    public final int getConnectionType() {
        int i;
        synchronized (this.mLock) {
            i = this.mConnectionType;
        }
        return i;
    }

    private void updateNewCallbackWithState(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException {
        int i;
        ImsReasonInfo imsReasonInfo;
        synchronized (this.mLock) {
            i = this.mRegistrationState;
            imsReasonInfo = this.mLastDisconnectCause;
        }
        if (i == 0) {
            iImsRegistrationCallback.onDeregistered(imsReasonInfo);
        } else if (i == 1) {
            iImsRegistrationCallback.onRegistering(getConnectionType());
        } else {
            if (i != 2) {
                return;
            }
            iImsRegistrationCallback.onRegistered(getConnectionType());
        }
    }
}
