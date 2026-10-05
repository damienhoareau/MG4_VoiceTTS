package android.hardware.radio;

/* JADX INFO: loaded from: classes.dex */
public abstract class RadioUpgrade {
    public static final int ERROR_CANCELLED = 2;
    public static final int ERROR_HARDWARE_FAILURE = 0;
    public static final int ERROR_SERVER_DIED = 1;
    public static final int RADIO_UPGRADE_STATUS_BOOTLOADER = 2;
    public static final int RADIO_UPGRADE_STATUS_ERROR = 5;
    public static final int RADIO_UPGRADE_STATUS_IDLE = 0;
    public static final int RADIO_UPGRADE_STATUS_LOADING = 3;
    public static final int RADIO_UPGRADE_STATUS_PREPAREED = 1;
    public static final int RADIO_UPGRADE_STATUS_SUCESS = 4;

    public static abstract class Callback {
        public void onError(int i) {
        }

        public void onUpgradeInfoChanged(RadioUpgradeInfo.UpgradeInfo upgradeInfo) {
        }
    }

    public abstract void bootloader();

    public abstract void close();

    public abstract void prepare(String str);

    public abstract void start();
}
