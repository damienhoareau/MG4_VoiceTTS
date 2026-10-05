package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothLeAdvertiser {
    private static final int FLAGS_FIELD_BYTES = 3;
    private static final int MANUFACTURER_SPECIFIC_DATA_LENGTH = 2;
    private static final int MAX_ADVERTISING_DATA_BYTES = 1650;
    private static final int MAX_LEGACY_ADVERTISING_DATA_BYTES = 31;
    private static final int OVERHEAD_BYTES_PER_FIELD = 2;
    private static final String TAG = "BluetoothLeAdvertiser";
    private final IBluetoothManager mBluetoothManager;
    private final Map<AdvertiseCallback, AdvertisingSetCallback> mLegacyAdvertisers = new HashMap();
    private final Map<AdvertisingSetCallback, IAdvertisingSetCallback> mCallbackWrappers = Collections.synchronizedMap(new HashMap());
    private final Map<Integer, AdvertisingSet> mAdvertisingSets = Collections.synchronizedMap(new HashMap());
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public BluetoothLeAdvertiser(IBluetoothManager iBluetoothManager) {
        this.mBluetoothManager = iBluetoothManager;
    }

    public void startAdvertising(AdvertiseSettings advertiseSettings, AdvertiseData advertiseData, AdvertiseCallback advertiseCallback) {
        startAdvertising(advertiseSettings, advertiseData, null, advertiseCallback);
    }

    public void startAdvertising(AdvertiseSettings advertiseSettings, AdvertiseData advertiseData, AdvertiseData advertiseData2, AdvertiseCallback advertiseCallback) {
        int i;
        synchronized (this.mLegacyAdvertisers) {
            BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
            if (advertiseCallback == null) {
                throw new IllegalArgumentException("callback cannot be null");
            }
            boolean zIsConnectable = advertiseSettings.isConnectable();
            int i2 = 1;
            if (totalBytes(advertiseData, zIsConnectable) <= 31 && totalBytes(advertiseData2, false) <= 31) {
                if (this.mLegacyAdvertisers.containsKey(advertiseCallback)) {
                    postStartFailure(advertiseCallback, 3);
                    return;
                }
                AdvertisingSetParameters.Builder builder = new AdvertisingSetParameters.Builder();
                builder.setLegacyMode(true);
                builder.setConnectable(zIsConnectable);
                builder.setScannable(true);
                if (advertiseSettings.getMode() == 0) {
                    builder.setInterval(AdvertisingSetParameters.INTERVAL_HIGH);
                } else if (advertiseSettings.getMode() == 1) {
                    builder.setInterval(400);
                } else if (advertiseSettings.getMode() == 2) {
                    builder.setInterval(160);
                }
                if (advertiseSettings.getTxPowerLevel() == 0) {
                    builder.setTxPowerLevel(-21);
                } else if (advertiseSettings.getTxPowerLevel() == 1) {
                    builder.setTxPowerLevel(-15);
                } else if (advertiseSettings.getTxPowerLevel() == 2) {
                    builder.setTxPowerLevel(-7);
                } else if (advertiseSettings.getTxPowerLevel() == 3) {
                    builder.setTxPowerLevel(1);
                }
                int timeout = advertiseSettings.getTimeout();
                if (timeout > 0) {
                    if (timeout >= 10) {
                        i2 = timeout / 10;
                    }
                    i = i2;
                } else {
                    i = 0;
                }
                AdvertisingSetCallback advertisingSetCallbackWrapOldCallback = wrapOldCallback(advertiseCallback, advertiseSettings);
                this.mLegacyAdvertisers.put(advertiseCallback, advertisingSetCallbackWrapOldCallback);
                startAdvertisingSet(builder.build(), advertiseData, advertiseData2, null, null, i, 0, advertisingSetCallbackWrapOldCallback);
                return;
            }
            postStartFailure(advertiseCallback, 1);
        }
    }

    AdvertisingSetCallback wrapOldCallback(final AdvertiseCallback advertiseCallback, final AdvertiseSettings advertiseSettings) {
        return new AdvertisingSetCallback() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.1
            @Override // android.bluetooth.le.AdvertisingSetCallback
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int i, int i2) {
                if (i2 != 0) {
                    BluetoothLeAdvertiser.this.postStartFailure(advertiseCallback, i2);
                } else {
                    BluetoothLeAdvertiser.this.postStartSuccess(advertiseCallback, advertiseSettings);
                }
            }

            @Override // android.bluetooth.le.AdvertisingSetCallback
            public void onAdvertisingEnabled(AdvertisingSet advertisingSet, boolean z, int i) {
                if (z) {
                    Log.e(BluetoothLeAdvertiser.TAG, "Legacy advertiser should be only disabled on timeout, but was enabled!");
                } else {
                    BluetoothLeAdvertiser.this.stopAdvertising(advertiseCallback);
                }
            }
        };
    }

    public void stopAdvertising(AdvertiseCallback advertiseCallback) {
        synchronized (this.mLegacyAdvertisers) {
            try {
                if (advertiseCallback == null) {
                    throw new IllegalArgumentException("callback cannot be null");
                }
                AdvertisingSetCallback advertisingSetCallback = this.mLegacyAdvertisers.get(advertiseCallback);
                if (advertisingSetCallback == null) {
                    return;
                }
                stopAdvertisingSet(advertisingSetCallback);
                this.mLegacyAdvertisers.remove(advertiseCallback);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void startAdvertisingSet(AdvertisingSetParameters advertisingSetParameters, AdvertiseData advertiseData, AdvertiseData advertiseData2, PeriodicAdvertisingParameters periodicAdvertisingParameters, AdvertiseData advertiseData3, AdvertisingSetCallback advertisingSetCallback) {
        startAdvertisingSet(advertisingSetParameters, advertiseData, advertiseData2, periodicAdvertisingParameters, advertiseData3, 0, 0, advertisingSetCallback, new Handler(Looper.getMainLooper()));
    }

    public void startAdvertisingSet(AdvertisingSetParameters advertisingSetParameters, AdvertiseData advertiseData, AdvertiseData advertiseData2, PeriodicAdvertisingParameters periodicAdvertisingParameters, AdvertiseData advertiseData3, AdvertisingSetCallback advertisingSetCallback, Handler handler) {
        startAdvertisingSet(advertisingSetParameters, advertiseData, advertiseData2, periodicAdvertisingParameters, advertiseData3, 0, 0, advertisingSetCallback, handler);
    }

    public void startAdvertisingSet(AdvertisingSetParameters advertisingSetParameters, AdvertiseData advertiseData, AdvertiseData advertiseData2, PeriodicAdvertisingParameters periodicAdvertisingParameters, AdvertiseData advertiseData3, int i, int i2, AdvertisingSetCallback advertisingSetCallback) {
        startAdvertisingSet(advertisingSetParameters, advertiseData, advertiseData2, periodicAdvertisingParameters, advertiseData3, i, i2, advertisingSetCallback, new Handler(Looper.getMainLooper()));
    }

    public void startAdvertisingSet(AdvertisingSetParameters advertisingSetParameters, AdvertiseData advertiseData, AdvertiseData advertiseData2, PeriodicAdvertisingParameters periodicAdvertisingParameters, AdvertiseData advertiseData3, int i, int i2, AdvertisingSetCallback advertisingSetCallback, Handler handler) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        if (advertisingSetCallback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        boolean zIsConnectable = advertisingSetParameters.isConnectable();
        if (advertisingSetParameters.isLegacy()) {
            if (totalBytes(advertiseData, zIsConnectable) > 31) {
                throw new IllegalArgumentException("Legacy advertising data too big");
            }
            if (totalBytes(advertiseData2, false) > 31) {
                throw new IllegalArgumentException("Legacy scan response data too big");
            }
        } else {
            boolean zIsLeCodedPhySupported = this.mBluetoothAdapter.isLeCodedPhySupported();
            boolean zIsLe2MPhySupported = this.mBluetoothAdapter.isLe2MPhySupported();
            int primaryPhy = advertisingSetParameters.getPrimaryPhy();
            int secondaryPhy = advertisingSetParameters.getSecondaryPhy();
            if (primaryPhy == 3 && !zIsLeCodedPhySupported) {
                throw new IllegalArgumentException("Unsupported primary PHY selected");
            }
            if ((secondaryPhy == 3 && !zIsLeCodedPhySupported) || (secondaryPhy == 2 && !zIsLe2MPhySupported)) {
                throw new IllegalArgumentException("Unsupported secondary PHY selected");
            }
            int leMaximumAdvertisingDataLength = this.mBluetoothAdapter.getLeMaximumAdvertisingDataLength();
            if (totalBytes(advertiseData, zIsConnectable) > leMaximumAdvertisingDataLength) {
                throw new IllegalArgumentException("Advertising data too big");
            }
            if (totalBytes(advertiseData2, false) > leMaximumAdvertisingDataLength) {
                throw new IllegalArgumentException("Scan response data too big");
            }
            if (totalBytes(advertiseData3, false) > leMaximumAdvertisingDataLength) {
                throw new IllegalArgumentException("Periodic advertising data too big");
            }
            boolean zIsLePeriodicAdvertisingSupported = this.mBluetoothAdapter.isLePeriodicAdvertisingSupported();
            if (periodicAdvertisingParameters != null && !zIsLePeriodicAdvertisingSupported) {
                throw new IllegalArgumentException("Controller does not support LE Periodic Advertising");
            }
        }
        if (i2 < 0 || i2 > 255) {
            throw new IllegalArgumentException("maxExtendedAdvertisingEvents out of range: " + i2);
        }
        if (i2 != 0 && !this.mBluetoothAdapter.isLePeriodicAdvertisingSupported()) {
            throw new IllegalArgumentException("Can't use maxExtendedAdvertisingEvents with controller that don't support LE Extended Advertising");
        }
        if (i < 0 || i > 65535) {
            throw new IllegalArgumentException("duration out of range: " + i);
        }
        try {
            IBluetoothGatt bluetoothGatt = this.mBluetoothManager.getBluetoothGatt();
            IAdvertisingSetCallback iAdvertisingSetCallbackWrap = wrap(advertisingSetCallback, handler);
            if (this.mCallbackWrappers.putIfAbsent(advertisingSetCallback, iAdvertisingSetCallbackWrap) != null) {
                throw new IllegalArgumentException("callback instance already associated with advertising");
            }
            try {
                bluetoothGatt.startAdvertisingSet(advertisingSetParameters, advertiseData, advertiseData2, periodicAdvertisingParameters, advertiseData3, i, i2, iAdvertisingSetCallbackWrap);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to start advertising set - ", e);
                postStartSetFailure(handler, advertisingSetCallback, 4);
            }
        } catch (RemoteException e2) {
            Log.e(TAG, "Failed to get Bluetooth gatt - ", e2);
            postStartSetFailure(handler, advertisingSetCallback, 4);
        }
    }

    public void stopAdvertisingSet(AdvertisingSetCallback advertisingSetCallback) {
        if (advertisingSetCallback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        IAdvertisingSetCallback iAdvertisingSetCallbackRemove = this.mCallbackWrappers.remove(advertisingSetCallback);
        if (iAdvertisingSetCallbackRemove == null) {
            return;
        }
        try {
            this.mBluetoothManager.getBluetoothGatt().stopAdvertisingSet(iAdvertisingSetCallbackRemove);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to stop advertising - ", e);
        }
    }

    public void cleanup() {
        this.mLegacyAdvertisers.clear();
        this.mCallbackWrappers.clear();
        this.mAdvertisingSets.clear();
    }

    private int totalBytes(AdvertiseData advertiseData, boolean z) {
        if (advertiseData == null) {
            return 0;
        }
        int iByteLength = z ? 3 : 0;
        if (advertiseData.getServiceUuids() != null) {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            for (ParcelUuid parcelUuid : advertiseData.getServiceUuids()) {
                if (BluetoothUuid.is16BitUuid(parcelUuid)) {
                    i++;
                } else if (BluetoothUuid.is32BitUuid(parcelUuid)) {
                    i2++;
                } else {
                    i3++;
                }
            }
            if (i != 0) {
                iByteLength += (i * 2) + 2;
            }
            if (i2 != 0) {
                iByteLength += (i2 * 4) + 2;
            }
            if (i3 != 0) {
                iByteLength += (i3 * 16) + 2;
            }
        }
        for (ParcelUuid parcelUuid2 : advertiseData.getServiceData().keySet()) {
            iByteLength += BluetoothUuid.uuidToBytes(parcelUuid2).length + 2 + byteLength(advertiseData.getServiceData().get(parcelUuid2));
        }
        for (int i4 = 0; i4 < advertiseData.getManufacturerSpecificData().size(); i4++) {
            iByteLength += byteLength(advertiseData.getManufacturerSpecificData().valueAt(i4)) + 4;
        }
        if (advertiseData.getIncludeTxPowerLevel()) {
            iByteLength += 3;
        }
        return (!advertiseData.getIncludeDeviceName() || this.mBluetoothAdapter.getName() == null) ? iByteLength : iByteLength + this.mBluetoothAdapter.getName().length() + 2;
    }

    private int byteLength(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        return bArr.length;
    }

    IAdvertisingSetCallback wrap(final AdvertisingSetCallback advertisingSetCallback, final Handler handler) {
        return new IAdvertisingSetCallback.Stub() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2
            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onAdvertisingSetStarted(final int i, final int i2, final int i3) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (i3 != 0) {
                            advertisingSetCallback.onAdvertisingSetStarted(null, 0, i3);
                            BluetoothLeAdvertiser.this.mCallbackWrappers.remove(advertisingSetCallback);
                        } else {
                            AdvertisingSet advertisingSet = new AdvertisingSet(i, BluetoothLeAdvertiser.this.mBluetoothManager);
                            BluetoothLeAdvertiser.this.mAdvertisingSets.put(Integer.valueOf(i), advertisingSet);
                            advertisingSetCallback.onAdvertisingSetStarted(advertisingSet, i2, i3);
                        }
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onOwnAddressRead(final int i, final int i2, final String str) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onOwnAddressRead((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2, str);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onAdvertisingSetStopped(final int i) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.3
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onAdvertisingSetStopped((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)));
                        BluetoothLeAdvertiser.this.mAdvertisingSets.remove(Integer.valueOf(i));
                        BluetoothLeAdvertiser.this.mCallbackWrappers.remove(advertisingSetCallback);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onAdvertisingEnabled(final int i, final boolean z, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.4
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onAdvertisingEnabled((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), z, i2);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onAdvertisingDataSet(final int i, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.5
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onAdvertisingDataSet((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onScanResponseDataSet(final int i, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.6
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onScanResponseDataSet((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onAdvertisingParametersUpdated(final int i, final int i2, final int i3) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.7
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onAdvertisingParametersUpdated((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2, i3);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onPeriodicAdvertisingParametersUpdated(final int i, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.8
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onPeriodicAdvertisingParametersUpdated((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onPeriodicAdvertisingDataSet(final int i, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.9
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onPeriodicAdvertisingDataSet((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), i2);
                    }
                });
            }

            @Override // android.bluetooth.le.IAdvertisingSetCallback
            public void onPeriodicAdvertisingEnabled(final int i, final boolean z, final int i2) {
                handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.2.10
                    @Override // java.lang.Runnable
                    public void run() {
                        advertisingSetCallback.onPeriodicAdvertisingEnabled((AdvertisingSet) BluetoothLeAdvertiser.this.mAdvertisingSets.get(Integer.valueOf(i)), z, i2);
                    }
                });
            }
        };
    }

    private void postStartSetFailure(Handler handler, final AdvertisingSetCallback advertisingSetCallback, final int i) {
        handler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.3
            @Override // java.lang.Runnable
            public void run() {
                advertisingSetCallback.onAdvertisingSetStarted(null, 0, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postStartFailure(final AdvertiseCallback advertiseCallback, final int i) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.4
            @Override // java.lang.Runnable
            public void run() {
                advertiseCallback.onStartFailure(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postStartSuccess(final AdvertiseCallback advertiseCallback, final AdvertiseSettings advertiseSettings) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeAdvertiser.5
            @Override // java.lang.Runnable
            public void run() {
                advertiseCallback.onStartSuccess(advertiseSettings);
            }
        });
    }
}
