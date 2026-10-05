package android.bluetooth.le;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothLeScanner {
    private static final boolean DBG = true;
    public static final String EXTRA_CALLBACK_TYPE = "android.bluetooth.le.extra.CALLBACK_TYPE";
    public static final String EXTRA_ERROR_CODE = "android.bluetooth.le.extra.ERROR_CODE";
    public static final String EXTRA_LIST_SCAN_RESULT = "android.bluetooth.le.extra.LIST_SCAN_RESULT";
    private static final String TAG = "BluetoothLeScanner";
    private static final boolean VDBG = false;
    private final IBluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<ScanCallback, BleScanCallbackWrapper> mLeScanClients = new HashMap();

    public BluetoothLeScanner(IBluetoothManager iBluetoothManager) {
        this.mBluetoothManager = iBluetoothManager;
    }

    public void startScan(ScanCallback scanCallback) {
        startScan((List<ScanFilter>) null, new ScanSettings.Builder().build(), scanCallback);
    }

    public void startScan(List<ScanFilter> list, ScanSettings scanSettings, ScanCallback scanCallback) {
        startScan(list, scanSettings, null, scanCallback, null, null);
    }

    public int startScan(List<ScanFilter> list, ScanSettings scanSettings, PendingIntent pendingIntent) {
        if (scanSettings == null) {
            scanSettings = new ScanSettings.Builder().build();
        }
        return startScan(list, scanSettings, null, null, pendingIntent, null);
    }

    @SystemApi
    public void startScanFromSource(WorkSource workSource, ScanCallback scanCallback) {
        startScanFromSource(null, new ScanSettings.Builder().build(), workSource, scanCallback);
    }

    @SystemApi
    public void startScanFromSource(List<ScanFilter> list, ScanSettings scanSettings, WorkSource workSource, ScanCallback scanCallback) {
        startScan(list, scanSettings, workSource, scanCallback, null, null);
    }

    /* JADX WARN: Code duplicated, block: B:24:0x003d A[Catch: all -> 0x0036, TRY_ENTER, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:27:0x0043 A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:29:0x004a A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:32:0x0050 A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:34:0x0056 A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:37:0x005d A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:39:0x0063 A[Catch: all -> 0x0036, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:42:0x0069 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:43:0x006b A[Catch: all -> 0x0036, TRY_LEAVE, TryCatch #0 {, blocks: (B:12:0x0020, B:14:0x0028, B:15:0x002d, B:17:0x002f, B:24:0x003d, B:25:0x0041, B:27:0x0043, B:29:0x004a, B:30:0x004e, B:32:0x0050, B:34:0x0056, B:35:0x005b, B:37:0x005d, B:39:0x0063, B:40:0x0067, B:43:0x006b, B:46:0x0084, B:45:0x007d, B:49:0x0087), top: B:55:0x0020, inners: #2 }] */
    /* JADX WARN: Code duplicated, block: B:58:0x007d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private int startScan(List<ScanFilter> list, ScanSettings scanSettings, WorkSource workSource, ScanCallback scanCallback, PendingIntent pendingIntent, List<List<ResultStorageDescriptor>> list2) {
        IBluetoothGatt bluetoothGatt;
        IBluetoothGatt iBluetoothGatt;
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        if (scanCallback == null && pendingIntent == null) {
            throw new IllegalArgumentException("callback is null");
        }
        if (scanSettings == null) {
            throw new IllegalArgumentException("settings is null");
        }
        synchronized (this.mLeScanClients) {
            if (scanCallback != null) {
                if (this.mLeScanClients.containsKey(scanCallback)) {
                    return postCallbackErrorOrReturn(scanCallback, 1);
                }
                try {
                    bluetoothGatt = this.mBluetoothManager.getBluetoothGatt();
                } catch (RemoteException unused) {
                    bluetoothGatt = null;
                }
                iBluetoothGatt = bluetoothGatt;
                if (iBluetoothGatt == null) {
                    return postCallbackErrorOrReturn(scanCallback, 3);
                }
                if (!isSettingsConfigAllowedForScan(scanSettings)) {
                    return postCallbackErrorOrReturn(scanCallback, 4);
                }
                if (!isHardwareResourcesAvailableForScan(scanSettings)) {
                    return postCallbackErrorOrReturn(scanCallback, 5);
                }
                if (!isSettingsAndFilterComboAllowed(scanSettings, list)) {
                    return postCallbackErrorOrReturn(scanCallback, 4);
                }
                if (scanCallback != null) {
                    new BleScanCallbackWrapper(iBluetoothGatt, list, scanSettings, workSource, scanCallback, list2).startRegistration();
                } else {
                    try {
                        iBluetoothGatt.startScanForIntent(pendingIntent, scanSettings, list, ActivityThread.currentOpPackageName());
                    } catch (RemoteException unused2) {
                        return 3;
                    }
                }
                return 0;
            }
            bluetoothGatt = this.mBluetoothManager.getBluetoothGatt();
            iBluetoothGatt = bluetoothGatt;
            if (iBluetoothGatt == null) {
                return postCallbackErrorOrReturn(scanCallback, 3);
            }
            if (!isSettingsConfigAllowedForScan(scanSettings)) {
                return postCallbackErrorOrReturn(scanCallback, 4);
            }
            if (!isHardwareResourcesAvailableForScan(scanSettings)) {
                return postCallbackErrorOrReturn(scanCallback, 5);
            }
            if (!isSettingsAndFilterComboAllowed(scanSettings, list)) {
                return postCallbackErrorOrReturn(scanCallback, 4);
            }
            if (scanCallback != null) {
                new BleScanCallbackWrapper(iBluetoothGatt, list, scanSettings, workSource, scanCallback, list2).startRegistration();
            } else {
                iBluetoothGatt.startScanForIntent(pendingIntent, scanSettings, list, ActivityThread.currentOpPackageName());
            }
            return 0;
            throw th;
        }
    }

    public void stopScan(ScanCallback scanCallback) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        synchronized (this.mLeScanClients) {
            BleScanCallbackWrapper bleScanCallbackWrapperRemove = this.mLeScanClients.remove(scanCallback);
            if (bleScanCallbackWrapperRemove == null) {
                Log.d(TAG, "could not find callback wrapper");
            } else {
                bleScanCallbackWrapperRemove.stopLeScan();
            }
        }
    }

    public void stopScan(PendingIntent pendingIntent) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        try {
            this.mBluetoothManager.getBluetoothGatt().stopScanForIntent(pendingIntent, ActivityThread.currentOpPackageName());
        } catch (RemoteException unused) {
        }
    }

    public void flushPendingScanResults(ScanCallback scanCallback) {
        BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
        if (scanCallback == null) {
            throw new IllegalArgumentException("callback cannot be null!");
        }
        synchronized (this.mLeScanClients) {
            BleScanCallbackWrapper bleScanCallbackWrapper = this.mLeScanClients.get(scanCallback);
            if (bleScanCallbackWrapper == null) {
                return;
            }
            bleScanCallbackWrapper.flushPendingBatchResults();
        }
    }

    @SystemApi
    public void startTruncatedScan(List<TruncatedFilter> list, ScanSettings scanSettings, ScanCallback scanCallback) {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        ArrayList arrayList2 = new ArrayList(size);
        for (TruncatedFilter truncatedFilter : list) {
            arrayList.add(truncatedFilter.getFilter());
            arrayList2.add(truncatedFilter.getStorageDescriptors());
        }
        startScan(arrayList, scanSettings, null, scanCallback, null, arrayList2);
    }

    public void cleanup() {
        this.mLeScanClients.clear();
    }

    private class BleScanCallbackWrapper extends IScannerCallback.Stub {
        private static final int REGISTRATION_CALLBACK_TIMEOUT_MILLIS = 2000;
        private IBluetoothGatt mBluetoothGatt;
        private final List<ScanFilter> mFilters;
        private List<List<ResultStorageDescriptor>> mResultStorages;
        private final ScanCallback mScanCallback;
        private int mScannerId = 0;
        private ScanSettings mSettings;
        private final WorkSource mWorkSource;

        public BleScanCallbackWrapper(IBluetoothGatt iBluetoothGatt, List<ScanFilter> list, ScanSettings scanSettings, WorkSource workSource, ScanCallback scanCallback, List<List<ResultStorageDescriptor>> list2) {
            this.mBluetoothGatt = iBluetoothGatt;
            this.mFilters = list;
            this.mSettings = scanSettings;
            this.mWorkSource = workSource;
            this.mScanCallback = scanCallback;
            this.mResultStorages = list2;
        }

        public void startRegistration() {
            synchronized (this) {
                if (this.mScannerId == -1 || this.mScannerId == -2) {
                    return;
                }
                try {
                    this.mBluetoothGatt.registerScanner(this, this.mWorkSource);
                    wait(2000L);
                } catch (RemoteException | InterruptedException e) {
                    Log.e(BluetoothLeScanner.TAG, "application registeration exception", e);
                    BluetoothLeScanner.this.postCallbackError(this.mScanCallback, 3);
                }
                if (this.mScannerId > 0) {
                    BluetoothLeScanner.this.mLeScanClients.put(this.mScanCallback, this);
                } else {
                    if (this.mScannerId == 0) {
                        this.mScannerId = -1;
                    }
                    if (this.mScannerId == -2) {
                    } else {
                        BluetoothLeScanner.this.postCallbackError(this.mScanCallback, 2);
                    }
                }
            }
        }

        public void stopLeScan() {
            synchronized (this) {
                if (this.mScannerId <= 0) {
                    Log.e(BluetoothLeScanner.TAG, "Error state, mLeHandle: " + this.mScannerId);
                    return;
                }
                try {
                    this.mBluetoothGatt.stopScan(this.mScannerId);
                    this.mBluetoothGatt.unregisterScanner(this.mScannerId);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeScanner.TAG, "Failed to stop scan and unregister", e);
                }
                this.mScannerId = -1;
            }
        }

        void flushPendingBatchResults() {
            synchronized (this) {
                if (this.mScannerId <= 0) {
                    Log.e(BluetoothLeScanner.TAG, "Error state, mLeHandle: " + this.mScannerId);
                    return;
                }
                try {
                    this.mBluetoothGatt.flushPendingBatchResults(this.mScannerId);
                } catch (RemoteException e) {
                    Log.e(BluetoothLeScanner.TAG, "Failed to get pending scan results", e);
                }
            }
        }

        @Override // android.bluetooth.le.IScannerCallback
        public void onScannerRegistered(int i, int i2) {
            Log.d(BluetoothLeScanner.TAG, "onScannerRegistered() - status=" + i + " scannerId=" + i2 + " mScannerId=" + this.mScannerId);
            synchronized (this) {
                if (i == 0) {
                    try {
                        if (this.mScannerId == -1) {
                            this.mBluetoothGatt.unregisterClient(i2);
                        } else {
                            this.mScannerId = i2;
                            this.mBluetoothGatt.startScan(i2, this.mSettings, this.mFilters, this.mResultStorages, ActivityThread.currentOpPackageName());
                        }
                    } catch (RemoteException e) {
                        Log.e(BluetoothLeScanner.TAG, "fail to start le scan: " + e);
                        this.mScannerId = -1;
                    }
                } else if (i == 6) {
                    this.mScannerId = -2;
                } else {
                    this.mScannerId = -1;
                }
                notifyAll();
            }
        }

        @Override // android.bluetooth.le.IScannerCallback
        public void onScanResult(final ScanResult scanResult) {
            synchronized (this) {
                if (this.mScannerId <= 0) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        BleScanCallbackWrapper.this.mScanCallback.onScanResult(1, scanResult);
                    }
                });
            }
        }

        @Override // android.bluetooth.le.IScannerCallback
        public void onBatchScanResults(final List<ScanResult> list) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    BleScanCallbackWrapper.this.mScanCallback.onBatchScanResults(list);
                }
            });
        }

        @Override // android.bluetooth.le.IScannerCallback
        public void onFoundOrLost(final boolean z, final ScanResult scanResult) {
            synchronized (this) {
                if (this.mScannerId <= 0) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.BleScanCallbackWrapper.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (z) {
                            BleScanCallbackWrapper.this.mScanCallback.onScanResult(2, scanResult);
                        } else {
                            BleScanCallbackWrapper.this.mScanCallback.onScanResult(4, scanResult);
                        }
                    }
                });
            }
        }

        @Override // android.bluetooth.le.IScannerCallback
        public void onScanManagerErrorCallback(int i) {
            synchronized (this) {
                if (this.mScannerId <= 0) {
                    return;
                }
                BluetoothLeScanner.this.postCallbackError(this.mScanCallback, i);
            }
        }
    }

    private int postCallbackErrorOrReturn(ScanCallback scanCallback, int i) {
        if (scanCallback == null) {
            return i;
        }
        postCallbackError(scanCallback, i);
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postCallbackError(final ScanCallback scanCallback, final int i) {
        this.mHandler.post(new Runnable() { // from class: android.bluetooth.le.BluetoothLeScanner.1
            @Override // java.lang.Runnable
            public void run() {
                scanCallback.onScanFailed(i);
            }
        });
    }

    private boolean isSettingsConfigAllowedForScan(ScanSettings scanSettings) {
        if (this.mBluetoothAdapter.isOffloadedFilteringSupported()) {
            return true;
        }
        return scanSettings.getCallbackType() == 1 && scanSettings.getReportDelayMillis() == 0;
    }

    private boolean isSettingsAndFilterComboAllowed(ScanSettings scanSettings, List<ScanFilter> list) {
        if ((scanSettings.getCallbackType() & 6) == 0) {
            return true;
        }
        if (list == null) {
            return false;
        }
        Iterator<ScanFilter> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().isAllFieldsEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isHardwareResourcesAvailableForScan(ScanSettings scanSettings) {
        int callbackType = scanSettings.getCallbackType();
        if ((callbackType & 2) == 0 && (callbackType & 4) == 0) {
            return true;
        }
        return this.mBluetoothAdapter.isOffloadedFilteringSupported() && this.mBluetoothAdapter.isHardwareTrackingFiltersAvailable();
    }
}
