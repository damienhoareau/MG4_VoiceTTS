package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsConfigCallback;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class ImsConfigImplBase {
    public static final int CONFIG_RESULT_FAILED = 1;
    public static final int CONFIG_RESULT_SUCCESS = 0;
    public static final int CONFIG_RESULT_UNKNOWN = -1;
    private static final String TAG = "ImsConfigImplBase";
    private final RemoteCallbackList<IImsConfigCallback> mCallbacks = new RemoteCallbackList<>();
    ImsConfigStub mImsConfigStub = new ImsConfigStub(this);

    public int getConfigInt(int i) {
        return -1;
    }

    public String getConfigString(int i) {
        return null;
    }

    public int setConfig(int i, int i2) {
        return 1;
    }

    public int setConfig(int i, String str) {
        return 1;
    }

    public static class ImsConfigStub extends IImsConfig.Stub {
        WeakReference<ImsConfigImplBase> mImsConfigImplBaseWeakReference;
        private HashMap<Integer, Integer> mProvisionedIntValue = new HashMap<>();
        private HashMap<Integer, String> mProvisionedStringValue = new HashMap<>();

        public ImsConfigStub(ImsConfigImplBase imsConfigImplBase) {
            this.mImsConfigImplBaseWeakReference = new WeakReference<>(imsConfigImplBase);
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public void addImsConfigCallback(IImsConfigCallback iImsConfigCallback) throws RemoteException {
            getImsConfigImpl().addImsConfigCallback(iImsConfigCallback);
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public void removeImsConfigCallback(IImsConfigCallback iImsConfigCallback) throws RemoteException {
            getImsConfigImpl().removeImsConfigCallback(iImsConfigCallback);
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public synchronized int getConfigInt(int i) throws RemoteException {
            if (this.mProvisionedIntValue.containsKey(Integer.valueOf(i))) {
                return this.mProvisionedIntValue.get(Integer.valueOf(i)).intValue();
            }
            int configInt = getImsConfigImpl().getConfigInt(i);
            if (configInt != -1) {
                updateCachedValue(i, configInt, false);
            }
            return configInt;
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public synchronized String getConfigString(int i) throws RemoteException {
            if (this.mProvisionedIntValue.containsKey(Integer.valueOf(i))) {
                return this.mProvisionedStringValue.get(Integer.valueOf(i));
            }
            String configString = getImsConfigImpl().getConfigString(i);
            if (configString != null) {
                updateCachedValue(i, configString, false);
            }
            return configString;
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public synchronized int setConfigInt(int i, int i2) throws RemoteException {
            int config;
            this.mProvisionedIntValue.remove(Integer.valueOf(i));
            config = getImsConfigImpl().setConfig(i, i2);
            if (config == 0) {
                updateCachedValue(i, i2, true);
            } else {
                Log.d(ImsConfigImplBase.TAG, "Set provision value of " + i + " to " + i2 + " failed with error code " + config);
            }
            return config;
        }

        @Override // android.telephony.ims.aidl.IImsConfig
        public synchronized int setConfigString(int i, String str) throws RemoteException {
            int config;
            this.mProvisionedStringValue.remove(Integer.valueOf(i));
            config = getImsConfigImpl().setConfig(i, str);
            if (config == 0) {
                updateCachedValue(i, str, true);
            }
            return config;
        }

        private ImsConfigImplBase getImsConfigImpl() throws RemoteException {
            ImsConfigImplBase imsConfigImplBase = this.mImsConfigImplBaseWeakReference.get();
            if (imsConfigImplBase != null) {
                return imsConfigImplBase;
            }
            throw new RemoteException("Fail to get ImsConfigImpl");
        }

        private void notifyImsConfigChanged(int i, int i2) throws RemoteException {
            getImsConfigImpl().notifyConfigChanged(i, i2);
        }

        private void notifyImsConfigChanged(int i, String str) throws RemoteException {
            getImsConfigImpl().notifyConfigChanged(i, str);
        }

        protected synchronized void updateCachedValue(int i, int i2, boolean z) throws RemoteException {
            this.mProvisionedIntValue.put(Integer.valueOf(i), Integer.valueOf(i2));
            if (z) {
                notifyImsConfigChanged(i, i2);
            }
        }

        protected synchronized void updateCachedValue(int i, String str, boolean z) throws RemoteException {
            this.mProvisionedStringValue.put(Integer.valueOf(i), str);
            if (z) {
                notifyImsConfigChanged(i, str);
            }
        }
    }

    public static class Callback extends IImsConfigCallback.Stub {
        public void onConfigChanged(int i, int i2) {
        }

        public void onConfigChanged(int i, String str) {
        }

        @Override // android.telephony.ims.aidl.IImsConfigCallback
        public final void onIntConfigChanged(int i, int i2) throws RemoteException {
            onConfigChanged(i, i2);
        }

        @Override // android.telephony.ims.aidl.IImsConfigCallback
        public final void onStringConfigChanged(int i, String str) throws RemoteException {
            onConfigChanged(i, str);
        }
    }

    public ImsConfigImplBase(Context context) {
    }

    public ImsConfigImplBase() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addImsConfigCallback(IImsConfigCallback iImsConfigCallback) {
        this.mCallbacks.register(iImsConfigCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeImsConfigCallback(IImsConfigCallback iImsConfigCallback) {
        this.mCallbacks.unregister(iImsConfigCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void notifyConfigChanged(final int i, final int i2) {
        RemoteCallbackList<IImsConfigCallback> remoteCallbackList = this.mCallbacks;
        if (remoteCallbackList == null) {
            return;
        }
        remoteCallbackList.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsConfigImplBase$yL4863k-FoQyqg_FX2mWsLMqbyA
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsConfigImplBase.lambda$notifyConfigChanged$0(i, i2, (IImsConfigCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$notifyConfigChanged$0(int i, int i2, IImsConfigCallback iImsConfigCallback) {
        try {
            iImsConfigCallback.onIntConfigChanged(i, i2);
        } catch (RemoteException unused) {
            Log.w(TAG, "notifyConfigChanged(int): dead binder in notify, skipping.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyConfigChanged(final int i, final String str) {
        RemoteCallbackList<IImsConfigCallback> remoteCallbackList = this.mCallbacks;
        if (remoteCallbackList == null) {
            return;
        }
        remoteCallbackList.broadcast(new Consumer() { // from class: android.telephony.ims.stub.-$$Lambda$ImsConfigImplBase$GAuYvQ8qBc7KgCJhNp4Pt4j5t-0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsConfigImplBase.lambda$notifyConfigChanged$1(i, str, (IImsConfigCallback) obj);
            }
        });
    }

    static /* synthetic */ void lambda$notifyConfigChanged$1(int i, String str, IImsConfigCallback iImsConfigCallback) {
        try {
            iImsConfigCallback.onStringConfigChanged(i, str);
        } catch (RemoteException unused) {
            Log.w(TAG, "notifyConfigChanged(string): dead binder in notify, skipping.");
        }
    }

    public IImsConfig getIImsConfig() {
        return this.mImsConfigStub;
    }

    public final void notifyProvisionedValueChanged(int i, int i2) {
        try {
            this.mImsConfigStub.updateCachedValue(i, i2, true);
        } catch (RemoteException unused) {
            Log.w(TAG, "notifyProvisionedValueChanged(int): Framework connection is dead.");
        }
    }

    public final void notifyProvisionedValueChanged(int i, String str) {
        try {
            this.mImsConfigStub.updateCachedValue(i, str, true);
        } catch (RemoteException unused) {
            Log.w(TAG, "notifyProvisionedValueChanged(string): Framework connection is dead.");
        }
    }
}
