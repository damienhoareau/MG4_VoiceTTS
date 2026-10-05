package android.hardware.radio;

import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
class UpgradeCallbackAdapter extends IUpgradeCallback.Stub {
    private static final String TAG = "UpgradeCallbackAdapter";
    private final RadioUpgrade.Callback mCallback;
    private final Handler mHandler;

    UpgradeCallbackAdapter(RadioUpgrade.Callback callback, Handler handler) {
        this.mCallback = callback;
        if (handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = handler;
        }
    }

    public /* synthetic */ void lambda$onUpgradeInfoChanged$0$UpgradeCallbackAdapter(RadioUpgradeInfo.UpgradeInfo upgradeInfo) {
        this.mCallback.onUpgradeInfoChanged(upgradeInfo);
    }

    @Override // android.hardware.radio.IUpgradeCallback
    public void onUpgradeInfoChanged(final RadioUpgradeInfo.UpgradeInfo upgradeInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$UpgradeCallbackAdapter$1I2OlMtVSQWTIlL6fSb8Yoitxk0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onUpgradeInfoChanged$0$UpgradeCallbackAdapter(upgradeInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onError$1$UpgradeCallbackAdapter(int i) {
        this.mCallback.onError(i);
    }

    @Override // android.hardware.radio.IUpgradeCallback
    public void onError(final int i) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$UpgradeCallbackAdapter$3ktPXymoYcT_1B3BLLlx7fnIWXk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onError$1$UpgradeCallbackAdapter(i);
            }
        });
    }
}
