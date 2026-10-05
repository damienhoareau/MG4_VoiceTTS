package android.hardware.radio;

import android.os.RemoteException;
import android.util.Log;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class UpgradeAdapter extends RadioUpgrade {
    private static final String TAG = "BroadcastRadio.UpgradeAdapter";
    private final IUpgrade mUpgrade;

    UpgradeAdapter(IUpgrade iUpgrade) {
        this.mUpgrade = (IUpgrade) Objects.requireNonNull(iUpgrade);
    }

    @Override // android.hardware.radio.RadioUpgrade
    public void close() {
        try {
            this.mUpgrade.close();
        } catch (RemoteException e) {
            Log.e(TAG, "Exception trying to close upgrade", e);
        }
    }

    @Override // android.hardware.radio.RadioUpgrade
    public void prepare(String str) {
        try {
            this.mUpgrade.prepare(str);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't do prepere action", e2);
        }
    }

    @Override // android.hardware.radio.RadioUpgrade
    public void bootloader() {
        try {
            this.mUpgrade.bootloader();
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't do upgrade action", e2);
        }
    }

    @Override // android.hardware.radio.RadioUpgrade
    public void start() {
        try {
            this.mUpgrade.start();
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't do upgrade action", e2);
        }
    }
}
