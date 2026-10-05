package android.bluetooth.le;

/* JADX INFO: loaded from: classes.dex */
public abstract class AdvertisingSetCallback {
    public static final int ADVERTISE_FAILED_ALREADY_STARTED = 3;
    public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1;
    public static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5;
    public static final int ADVERTISE_FAILED_INTERNAL_ERROR = 4;
    public static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2;
    public static final int ADVERTISE_SUCCESS = 0;

    public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int i) {
    }

    public void onAdvertisingEnabled(AdvertisingSet advertisingSet, boolean z, int i) {
    }

    public void onAdvertisingParametersUpdated(AdvertisingSet advertisingSet, int i, int i2) {
    }

    public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int i, int i2) {
    }

    public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
    }

    public void onOwnAddressRead(AdvertisingSet advertisingSet, int i, String str) {
    }

    public void onPeriodicAdvertisingDataSet(AdvertisingSet advertisingSet, int i) {
    }

    public void onPeriodicAdvertisingEnabled(AdvertisingSet advertisingSet, boolean z, int i) {
    }

    public void onPeriodicAdvertisingParametersUpdated(AdvertisingSet advertisingSet, int i) {
    }

    public void onScanResponseDataSet(AdvertisingSet advertisingSet, int i) {
    }
}
