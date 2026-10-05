package android.app;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.os.Handler;
import android.os.RemoteException;
import android.service.vr.IPersistentVrStateCallbacks;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.ArrayMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class VrManager {
    private Map<VrStateCallback, CallbackEntry> mCallbackMap = new ArrayMap();
    private final IVrManager mService;

    /* JADX INFO: Access modifiers changed from: private */
    static class CallbackEntry {
        final VrStateCallback mCallback;
        final Handler mHandler;
        final IVrStateCallbacks mStateCallback = new AnonymousClass1();
        final IPersistentVrStateCallbacks mPersistentStateCallback = new AnonymousClass2();

        /* JADX INFO: renamed from: android.app.VrManager$CallbackEntry$1, reason: invalid class name */
        class AnonymousClass1 extends IVrStateCallbacks.Stub {
            AnonymousClass1() {
            }

            public /* synthetic */ void lambda$onVrStateChanged$0$VrManager$CallbackEntry$1(boolean z) {
                CallbackEntry.this.mCallback.onVrStateChanged(z);
            }

            @Override // android.service.vr.IVrStateCallbacks
            public void onVrStateChanged(final boolean z) {
                CallbackEntry.this.mHandler.post(new Runnable() { // from class: android.app.-$$Lambda$VrManager$CallbackEntry$1$rgUBVVG1QhelpvAp8W3UQHDHJdU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onVrStateChanged$0$VrManager$CallbackEntry$1(z);
                    }
                });
            }
        }

        /* JADX INFO: renamed from: android.app.VrManager$CallbackEntry$2, reason: invalid class name */
        class AnonymousClass2 extends IPersistentVrStateCallbacks.Stub {
            AnonymousClass2() {
            }

            public /* synthetic */ void lambda$onPersistentVrStateChanged$0$VrManager$CallbackEntry$2(boolean z) {
                CallbackEntry.this.mCallback.onPersistentVrStateChanged(z);
            }

            @Override // android.service.vr.IPersistentVrStateCallbacks
            public void onPersistentVrStateChanged(final boolean z) {
                CallbackEntry.this.mHandler.post(new Runnable() { // from class: android.app.-$$Lambda$VrManager$CallbackEntry$2$KvHLIXm3-7igcOqTEl46YdjhHMk
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onPersistentVrStateChanged$0$VrManager$CallbackEntry$2(z);
                    }
                });
            }
        }

        CallbackEntry(VrStateCallback vrStateCallback, Handler handler) {
            this.mCallback = vrStateCallback;
            this.mHandler = handler;
        }
    }

    public VrManager(IVrManager iVrManager) {
        this.mService = iVrManager;
    }

    public void registerVrStateCallback(VrStateCallback vrStateCallback, Handler handler) {
        if (vrStateCallback == null || this.mCallbackMap.containsKey(vrStateCallback)) {
            return;
        }
        CallbackEntry callbackEntry = new CallbackEntry(vrStateCallback, handler);
        this.mCallbackMap.put(vrStateCallback, callbackEntry);
        try {
            this.mService.registerListener(callbackEntry.mStateCallback);
            this.mService.registerPersistentVrStateListener(callbackEntry.mPersistentStateCallback);
        } catch (RemoteException e) {
            try {
                unregisterVrStateCallback(vrStateCallback);
            } catch (Exception unused) {
                e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterVrStateCallback(VrStateCallback vrStateCallback) {
        CallbackEntry callbackEntryRemove = this.mCallbackMap.remove(vrStateCallback);
        if (callbackEntryRemove != null) {
            try {
                this.mService.unregisterListener(callbackEntryRemove.mStateCallback);
            } catch (RemoteException unused) {
            }
            try {
                this.mService.unregisterPersistentVrStateListener(callbackEntryRemove.mPersistentStateCallback);
            } catch (RemoteException unused2) {
            }
        }
    }

    public boolean getVrModeEnabled() {
        try {
            return this.mService.getVrModeState();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean getPersistentVrModeEnabled() {
        try {
            return this.mService.getPersistentVrModeEnabled();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public void setPersistentVrModeEnabled(boolean z) {
        try {
            this.mService.setPersistentVrModeEnabled(z);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setVr2dDisplayProperties(Vr2dDisplayProperties vr2dDisplayProperties) {
        try {
            this.mService.setVr2dDisplayProperties(vr2dDisplayProperties);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setAndBindVrCompositor(ComponentName componentName) {
        try {
            this.mService.setAndBindCompositor(componentName == null ? null : componentName.flattenToString());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setStandbyEnabled(boolean z) {
        try {
            this.mService.setStandbyEnabled(z);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void setVrInputMethod(ComponentName componentName) {
        try {
            this.mService.setVrInputMethod(componentName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }
}
