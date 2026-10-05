package android.net.lowpan;

import android.content.Context;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Log;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class LowpanInterface {
    public static final String EMPTY_PARTITION_ID = "";
    public static final String NETWORK_TYPE_THREAD_V1 = "org.threadgroup.thread.v1";
    public static final String ROLE_COORDINATOR = "coordinator";
    public static final String ROLE_DETACHED = "detached";
    public static final String ROLE_END_DEVICE = "end-device";
    public static final String ROLE_LEADER = "leader";
    public static final String ROLE_ROUTER = "router";
    public static final String ROLE_SLEEPY_END_DEVICE = "sleepy-end-device";
    public static final String ROLE_SLEEPY_ROUTER = "sleepy-router";
    public static final String STATE_ATTACHED = "attached";
    public static final String STATE_ATTACHING = "attaching";
    public static final String STATE_COMMISSIONING = "commissioning";
    public static final String STATE_FAULT = "fault";
    public static final String STATE_OFFLINE = "offline";
    private static final String TAG = LowpanInterface.class.getSimpleName();
    private final ILowpanInterface mBinder;
    private final HashMap<Integer, ILowpanInterfaceListener> mListenerMap = new HashMap<>();
    private final Looper mLooper;

    public static abstract class Callback {
        public void onConnectedChanged(boolean z) {
        }

        public void onEnabledChanged(boolean z) {
        }

        public void onLinkAddressAdded(LinkAddress linkAddress) {
        }

        public void onLinkAddressRemoved(LinkAddress linkAddress) {
        }

        public void onLinkNetworkAdded(IpPrefix ipPrefix) {
        }

        public void onLinkNetworkRemoved(IpPrefix ipPrefix) {
        }

        public void onLowpanIdentityChanged(LowpanIdentity lowpanIdentity) {
        }

        public void onRoleChanged(String str) {
        }

        public void onStateChanged(String str) {
        }

        public void onUpChanged(boolean z) {
        }
    }

    public LowpanInterface(Context context, ILowpanInterface iLowpanInterface, Looper looper) {
        this.mBinder = iLowpanInterface;
        this.mLooper = looper;
    }

    public ILowpanInterface getService() {
        return this.mBinder;
    }

    public void form(LowpanProvision lowpanProvision) throws LowpanException {
        try {
            this.mBinder.form(lowpanProvision);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void join(LowpanProvision lowpanProvision) throws LowpanException {
        try {
            this.mBinder.join(lowpanProvision);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void attach(LowpanProvision lowpanProvision) throws LowpanException {
        try {
            this.mBinder.attach(lowpanProvision);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void leave() throws LowpanException {
        try {
            this.mBinder.leave();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public LowpanCommissioningSession startCommissioningSession(LowpanBeaconInfo lowpanBeaconInfo) throws LowpanException {
        try {
            this.mBinder.startCommissioningSession(lowpanBeaconInfo);
            return new LowpanCommissioningSession(this.mBinder, lowpanBeaconInfo, this.mLooper);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void reset() throws LowpanException {
        try {
            this.mBinder.reset();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public String getName() {
        try {
            return this.mBinder.getName();
        } catch (DeadObjectException unused) {
            return "";
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isEnabled() {
        try {
            return this.mBinder.isEnabled();
        } catch (DeadObjectException unused) {
            return false;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void setEnabled(boolean z) throws LowpanException {
        try {
            this.mBinder.setEnabled(z);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public boolean isUp() {
        try {
            return this.mBinder.isUp();
        } catch (DeadObjectException unused) {
            return false;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isConnected() {
        try {
            return this.mBinder.isConnected();
        } catch (DeadObjectException unused) {
            return false;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isCommissioned() {
        try {
            return this.mBinder.isCommissioned();
        } catch (DeadObjectException unused) {
            return false;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String getState() {
        try {
            return this.mBinder.getState();
        } catch (DeadObjectException unused) {
            return "fault";
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String getPartitionId() {
        try {
            return this.mBinder.getPartitionId();
        } catch (DeadObjectException unused) {
            return "";
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public LowpanIdentity getLowpanIdentity() {
        try {
            return this.mBinder.getLowpanIdentity();
        } catch (DeadObjectException unused) {
            return new LowpanIdentity();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String getRole() {
        try {
            return this.mBinder.getRole();
        } catch (DeadObjectException unused) {
            return "detached";
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public LowpanCredential getLowpanCredential() {
        try {
            return this.mBinder.getLowpanCredential();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String[] getSupportedNetworkTypes() throws LowpanException {
        try {
            return this.mBinder.getSupportedNetworkTypes();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public LowpanChannelInfo[] getSupportedChannels() throws LowpanException {
        try {
            return this.mBinder.getSupportedChannels();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    /* JADX INFO: renamed from: android.net.lowpan.LowpanInterface$1, reason: invalid class name */
    class AnonymousClass1 extends ILowpanInterfaceListener.Stub {
        private Handler mHandler;
        final /* synthetic */ Callback val$cb;
        final /* synthetic */ Handler val$handler;

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onReceiveFromCommissioner(byte[] bArr) {
        }

        AnonymousClass1(Handler handler, Callback callback) {
            this.val$handler = handler;
            this.val$cb = callback;
            Handler handler2 = this.val$handler;
            if (handler2 == null) {
                if (LowpanInterface.this.mLooper != null) {
                    this.mHandler = new Handler(LowpanInterface.this.mLooper);
                    return;
                } else {
                    this.mHandler = new Handler();
                    return;
                }
            }
            this.mHandler = handler2;
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onEnabledChanged(final boolean z) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$LMuYw1xVwTG7Wbs4COpO6TLHuQ0
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onEnabledChanged(z);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onConnectedChanged(final boolean z) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$Nidk8wBLJKibO6BNky-_lJftmGs
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onConnectedChanged(z);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onUpChanged(final boolean z) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$a1rvbSIFSC6J5j7aKUf1ekbmIIA
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onUpChanged(z);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onRoleChanged(final String str) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$9yiRqHwJmFc-LEKn1vk5rA75W0M
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onRoleChanged(str);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onStateChanged(final String str) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$5PUJBkKF3VANgkiEem5Oq8oyB6U
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onStateChanged(str);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLowpanIdentityChanged(final LowpanIdentity lowpanIdentity) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$rl_ENeH3C5Kvf22BOtLnz-Ehs5c
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onLowpanIdentityChanged(lowpanIdentity);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkNetworkAdded(final IpPrefix ipPrefix) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$oacwoIgJ4pmkBqVtGJfFzk7A35k
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onLinkNetworkAdded(ipPrefix);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkNetworkRemoved(final IpPrefix ipPrefix) {
            Handler handler = this.mHandler;
            final Callback callback = this.val$cb;
            handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$cH3X25eT4t6pHlLvzBjlSOMs2vc
                @Override // java.lang.Runnable
                public final void run() {
                    callback.onLinkNetworkRemoved(ipPrefix);
                }
            });
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkAddressAdded(String str) {
            try {
                final LinkAddress linkAddress = new LinkAddress(str);
                Handler handler = this.mHandler;
                final Callback callback = this.val$cb;
                handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$i2_6hzE6WEaUSOaaltxLebbf7-E
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.onLinkAddressAdded(linkAddress);
                    }
                });
            } catch (IllegalArgumentException e) {
                Log.e(LowpanInterface.TAG, "onLinkAddressAdded: Bad LinkAddress \"" + str + "\", " + e);
            }
        }

        @Override // android.net.lowpan.ILowpanInterfaceListener
        public void onLinkAddressRemoved(String str) {
            try {
                final LinkAddress linkAddress = new LinkAddress(str);
                Handler handler = this.mHandler;
                final Callback callback = this.val$cb;
                handler.post(new Runnable() { // from class: android.net.lowpan.-$$Lambda$LowpanInterface$1$bAiJozbLxVR9_EMESl7KCJxLARA
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.onLinkAddressRemoved(linkAddress);
                    }
                });
            } catch (IllegalArgumentException e) {
                Log.e(LowpanInterface.TAG, "onLinkAddressRemoved: Bad LinkAddress \"" + str + "\", " + e);
            }
        }
    }

    public void registerCallback(Callback callback, Handler handler) {
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(handler, callback);
        try {
            this.mBinder.addListener(anonymousClass1);
            synchronized (this.mListenerMap) {
                this.mListenerMap.put(Integer.valueOf(System.identityHashCode(callback)), anonymousClass1);
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void registerCallback(Callback callback) {
        registerCallback(callback, null);
    }

    public void unregisterCallback(Callback callback) {
        int iIdentityHashCode = System.identityHashCode(callback);
        synchronized (this.mListenerMap) {
            ILowpanInterfaceListener iLowpanInterfaceListener = this.mListenerMap.get(Integer.valueOf(iIdentityHashCode));
            if (iLowpanInterfaceListener != null) {
                this.mListenerMap.remove(Integer.valueOf(iIdentityHashCode));
                try {
                    this.mBinder.removeListener(iLowpanInterfaceListener);
                } catch (DeadObjectException unused) {
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            }
        }
    }

    public LowpanScanner createScanner() {
        return new LowpanScanner(this.mBinder);
    }

    public LinkAddress[] getLinkAddresses() throws LowpanException {
        try {
            String[] linkAddresses = this.mBinder.getLinkAddresses();
            LinkAddress[] linkAddressArr = new LinkAddress[linkAddresses.length];
            int length = linkAddresses.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                int i3 = i2 + 1;
                linkAddressArr[i2] = new LinkAddress(linkAddresses[i]);
                i++;
                i2 = i3;
            }
            return linkAddressArr;
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public IpPrefix[] getLinkNetworks() throws LowpanException {
        try {
            return this.mBinder.getLinkNetworks();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void addOnMeshPrefix(IpPrefix ipPrefix, int i) throws LowpanException {
        try {
            this.mBinder.addOnMeshPrefix(ipPrefix, i);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void removeOnMeshPrefix(IpPrefix ipPrefix) {
        try {
            this.mBinder.removeOnMeshPrefix(ipPrefix);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            Log.e(TAG, e2.toString());
        }
    }

    public void addExternalRoute(IpPrefix ipPrefix, int i) throws LowpanException {
        try {
            this.mBinder.addExternalRoute(ipPrefix, i);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw LowpanException.rethrowFromServiceSpecificException(e2);
        }
    }

    public void removeExternalRoute(IpPrefix ipPrefix) {
        try {
            this.mBinder.removeExternalRoute(ipPrefix);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            Log.e(TAG, e2.toString());
        }
    }
}
