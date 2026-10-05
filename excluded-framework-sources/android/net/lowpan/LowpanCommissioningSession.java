package android.net.lowpan;

import android.net.IpPrefix;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public class LowpanCommissioningSession {
    private final LowpanBeaconInfo mBeaconInfo;
    private final ILowpanInterface mBinder;
    private Handler mHandler;
    private final Looper mLooper;
    private final ILowpanInterfaceListener mInternalCallback = new InternalCallback();
    private Callback mCallback = null;
    private volatile boolean mIsClosed = false;

    public static abstract class Callback {
        public void onClosed() {
        }

        public void onReceiveFromCommissioner(byte[] bArr) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class InternalCallback extends ILowpanInterfaceListener.Stub {
        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onConnectedChanged(boolean z) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onEnabledChanged(boolean z) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkAddressAdded(String str) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkAddressRemoved(String str) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkNetworkAdded(IpPrefix ipPrefix) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkNetworkRemoved(IpPrefix ipPrefix) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLowpanIdentityChanged(LowpanIdentity lowpanIdentity) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onRoleChanged(String str) {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onUpChanged(boolean z) {
        }

        private InternalCallback() {
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onStateChanged(String str) {
            if (LowpanCommissioningSession.this.mIsClosed) {
                return;
            }
            byte b = -1;
            int iHashCode = str.hashCode();
            if (iHashCode != -1548612125) {
                if (iHashCode == 97204770 && str.equals("fault")) {
                    b = 1;
                }
            } else if (str.equals("offline")) {
                b = 0;
            }
            if (b == 0 || b == 1) {
                synchronized (LowpanCommissioningSession.this) {
                    LowpanCommissioningSession.this.lockedCleanup();
                }
            }
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onReceiveFromCommissioner(final byte[] bArr) {
            LowpanCommissioningSession.this.mHandler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanCommissioningSession$InternalCallback$TrrmDykqIWeXNdgrXO7t2-rqCTo
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onReceiveFromCommissioner$0$LowpanCommissioningSession$InternalCallback(bArr);
                }
            });
        }

        public /* synthetic */ void lambda$onReceiveFromCommissioner$0$LowpanCommissioningSession$InternalCallback(byte[] bArr) {
            synchronized (LowpanCommissioningSession.this) {
                if (!LowpanCommissioningSession.this.mIsClosed && LowpanCommissioningSession.this.mCallback != null) {
                    LowpanCommissioningSession.this.mCallback.onReceiveFromCommissioner(bArr);
                }
            }
        }
    }

    LowpanCommissioningSession(ILowpanInterface iLowpanInterface, LowpanBeaconInfo lowpanBeaconInfo, Looper looper) {
        this.mBinder = iLowpanInterface;
        this.mBeaconInfo = lowpanBeaconInfo;
        this.mLooper = looper;
        if (looper != null) {
            this.mHandler = new Handler(this.mLooper);
        } else {
            this.mHandler = new Handler();
        }
        try {
            this.mBinder.addListener(this.mInternalCallback);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void lockedCleanup() {
        if (!this.mIsClosed) {
            try {
                this.mBinder.removeListener(this.mInternalCallback);
            } catch (DeadObjectException unused) {
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
            if (this.mCallback != null) {
                this.mHandler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanCommissioningSession$jqpl-iUq-e7YuWqkG33P8PNe7Ag
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$lockedCleanup$0$LowpanCommissioningSession();
                    }
                });
            }
        }
        this.mCallback = null;
        this.mIsClosed = true;
    }

    public /* synthetic */ void lambda$lockedCleanup$0$LowpanCommissioningSession() {
        this.mCallback.onClosed();
    }

    public LowpanBeaconInfo getBeaconInfo() {
        return this.mBeaconInfo;
    }

    public void sendToCommissioner(byte[] bArr) {
        if (this.mIsClosed) {
            return;
        }
        try {
            this.mBinder.sendToCommissioner(bArr);
        } catch (DeadObjectException unused) {
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public synchronized void setCallback(Callback callback, Handler handler) {
        if (!this.mIsClosed) {
            if (handler != null) {
                this.mHandler = handler;
            } else if (this.mLooper != null) {
                this.mHandler = new Handler(this.mLooper);
            } else {
                this.mHandler = new Handler();
            }
            this.mCallback = callback;
        }
    }

    public synchronized void close() {
        if (!this.mIsClosed) {
            try {
                this.mBinder.closeCommissioningSession();
                lockedCleanup();
            } catch (DeadObjectException unused) {
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
    }
}
