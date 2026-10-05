package android.telephony;

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.SparseArray;
import com.android.internal.telephony.ITelephony;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
public final class TelephonyScanManager {
    public static final int CALLBACK_SCAN_COMPLETE = 3;
    public static final int CALLBACK_SCAN_ERROR = 2;
    public static final int CALLBACK_SCAN_RESULTS = 1;
    public static final String SCAN_RESULT_KEY = "scanResult";
    private static final String TAG = "TelephonyScanManager";
    private final Looper mLooper;
    private final Messenger mMessenger;
    private SparseArray<NetworkScanInfo> mScanInfo = new SparseArray<>();

    public static abstract class NetworkScanCallback {
        public void onComplete() {
        }

        public void onError(int i) {
        }

        public void onResults(List<CellInfo> list) {
        }
    }

    private static class NetworkScanInfo {
        private final NetworkScanCallback mCallback;
        private final Executor mExecutor;
        private final NetworkScanRequest mRequest;

        NetworkScanInfo(NetworkScanRequest networkScanRequest, Executor executor, NetworkScanCallback networkScanCallback) {
            this.mRequest = networkScanRequest;
            this.mExecutor = executor;
            this.mCallback = networkScanCallback;
        }
    }

    public TelephonyScanManager() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        this.mLooper = handlerThread.getLooper();
        this.mMessenger = new Messenger(new AnonymousClass1(this.mLooper));
    }

    /* JADX INFO: renamed from: android.telephony.TelephonyScanManager$1, reason: invalid class name */
    class AnonymousClass1 extends Handler {
        AnonymousClass1(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            NetworkScanInfo networkScanInfo;
            Preconditions.checkNotNull(message, "message cannot be null");
            synchronized (TelephonyScanManager.this.mScanInfo) {
                networkScanInfo = (NetworkScanInfo) TelephonyScanManager.this.mScanInfo.get(message.arg2);
            }
            if (networkScanInfo != null) {
                final NetworkScanCallback networkScanCallback = networkScanInfo.mCallback;
                Executor executor = networkScanInfo.mExecutor;
                if (networkScanCallback == null) {
                    throw new RuntimeException("Failed to find NetworkScanCallback with id " + message.arg2);
                }
                if (executor == null) {
                    throw new RuntimeException("Failed to find Executor with id " + message.arg2);
                }
                int i = message.what;
                if (i == 1) {
                    try {
                        Parcelable[] parcelableArray = message.getData().getParcelableArray(TelephonyScanManager.SCAN_RESULT_KEY);
                        final CellInfo[] cellInfoArr = new CellInfo[parcelableArray.length];
                        for (int i2 = 0; i2 < parcelableArray.length; i2++) {
                            cellInfoArr[i2] = (CellInfo) parcelableArray[i2];
                        }
                        executor.execute(new Runnable() { // from class: android.telephony.-$$Lambda$TelephonyScanManager$1$jmXulbd8FzO5Qb8_Hi-Z6s_nJWI
                            @Override // java.lang.Runnable
                            public final void run() {
                                TelephonyScanManager.AnonymousClass1.lambda$handleMessage$0(cellInfoArr, networkScanCallback);
                            }
                        });
                        return;
                    } catch (Exception e) {
                        Rlog.e(TelephonyScanManager.TAG, "Exception in networkscan callback onResults", e);
                        return;
                    }
                }
                if (i == 2) {
                    try {
                        final int i3 = message.arg1;
                        executor.execute(new Runnable() { // from class: android.telephony.-$$Lambda$TelephonyScanManager$1$X9SMshZoHjJ6SzCbmgVMwQip2Q0
                            @Override // java.lang.Runnable
                            public final void run() {
                                TelephonyScanManager.AnonymousClass1.lambda$handleMessage$1(i3, networkScanCallback);
                            }
                        });
                        return;
                    } catch (Exception e2) {
                        Rlog.e(TelephonyScanManager.TAG, "Exception in networkscan callback onError", e2);
                        return;
                    }
                }
                if (i == 3) {
                    try {
                        executor.execute(new Runnable() { // from class: android.telephony.-$$Lambda$TelephonyScanManager$1$tGSpVQaVhc4GKIxjcECV-jCGYw4
                            @Override // java.lang.Runnable
                            public final void run() {
                                TelephonyScanManager.AnonymousClass1.lambda$handleMessage$2(networkScanCallback);
                            }
                        });
                        TelephonyScanManager.this.mScanInfo.remove(message.arg2);
                        return;
                    } catch (Exception e3) {
                        Rlog.e(TelephonyScanManager.TAG, "Exception in networkscan callback onComplete", e3);
                        return;
                    }
                }
                Rlog.e(TelephonyScanManager.TAG, "Unhandled message " + Integer.toHexString(message.what));
                return;
            }
            throw new RuntimeException("Failed to find NetworkScanInfo with id " + message.arg2);
        }

        static /* synthetic */ void lambda$handleMessage$0(CellInfo[] cellInfoArr, NetworkScanCallback networkScanCallback) {
            Rlog.d(TelephonyScanManager.TAG, "onResults: " + cellInfoArr.toString());
            networkScanCallback.onResults(Arrays.asList(cellInfoArr));
        }

        static /* synthetic */ void lambda$handleMessage$1(int i, NetworkScanCallback networkScanCallback) {
            Rlog.d(TelephonyScanManager.TAG, "onError: " + i);
            networkScanCallback.onError(i);
        }

        static /* synthetic */ void lambda$handleMessage$2(NetworkScanCallback networkScanCallback) {
            Rlog.d(TelephonyScanManager.TAG, "onComplete");
            networkScanCallback.onComplete();
        }
    }

    public NetworkScan requestNetworkScan(int i, NetworkScanRequest networkScanRequest, Executor executor, NetworkScanCallback networkScanCallback) {
        try {
            ITelephony iTelephony = getITelephony();
            if (iTelephony == null) {
                return null;
            }
            int iRequestNetworkScan = iTelephony.requestNetworkScan(i, networkScanRequest, this.mMessenger, new Binder());
            saveScanInfo(iRequestNetworkScan, networkScanRequest, executor, networkScanCallback);
            return new NetworkScan(iRequestNetworkScan, i);
        } catch (RemoteException e) {
            Rlog.e(TAG, "requestNetworkScan RemoteException", e);
            return null;
        } catch (NullPointerException e2) {
            Rlog.e(TAG, "requestNetworkScan NPE", e2);
            return null;
        }
    }

    private void saveScanInfo(int i, NetworkScanRequest networkScanRequest, Executor executor, NetworkScanCallback networkScanCallback) {
        synchronized (this.mScanInfo) {
            this.mScanInfo.put(i, new NetworkScanInfo(networkScanRequest, executor, networkScanCallback));
        }
    }

    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
    }
}
