package android.net.wifi.rtt;

import android.annotation.SystemApi;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.WorkSource;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class WifiRttManager {
    public static final String ACTION_WIFI_RTT_STATE_CHANGED = "android.net.wifi.rtt.action.WIFI_RTT_STATE_CHANGED";
    private static final String TAG = "WifiRttManager";
    private static final boolean VDBG = false;
    private final Context mContext;
    private final IWifiRttManager mService;

    public WifiRttManager(Context context, IWifiRttManager iWifiRttManager) {
        this.mContext = context;
        this.mService = iWifiRttManager;
    }

    public boolean isAvailable() {
        try {
            return this.mService.isAvailable();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startRanging(RangingRequest rangingRequest, Executor executor, RangingResultCallback rangingResultCallback) {
        startRanging(null, rangingRequest, executor, rangingResultCallback);
    }

    @SystemApi
    public void startRanging(WorkSource workSource, RangingRequest rangingRequest, Executor executor, RangingResultCallback rangingResultCallback) {
        if (executor == null) {
            throw new IllegalArgumentException("Null executor provided");
        }
        if (rangingResultCallback == null) {
            throw new IllegalArgumentException("Null callback provided");
        }
        try {
            this.mService.startRanging(new Binder(), this.mContext.getOpPackageName(), workSource, rangingRequest, new AnonymousClass1(executor, rangingResultCallback));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: renamed from: android.net.wifi.rtt.WifiRttManager$1, reason: invalid class name */
    class AnonymousClass1 extends IRttCallback.Stub {
        final /* synthetic */ RangingResultCallback val$callback;
        final /* synthetic */ Executor val$executor;

        AnonymousClass1(Executor executor, RangingResultCallback rangingResultCallback) {
            this.val$executor = executor;
            this.val$callback = rangingResultCallback;
        }

        @Override // android.net.wifi.rtt.IRttCallback
        public void onRangingFailure(final int i) throws RemoteException {
            clearCallingIdentity();
            Executor executor = this.val$executor;
            final RangingResultCallback rangingResultCallback = this.val$callback;
            executor.execute(new Runnable() { // from class: android.net.wifi.rtt.-$$Lambda$WifiRttManager$1$j3tVizFtxt_z0tTXfTNSFM4Loi8
                @Override // java.lang.Runnable
                public final void run() {
                    rangingResultCallback.onRangingFailure(i);
                }
            });
        }

        @Override // android.net.wifi.rtt.IRttCallback
        public void onRangingResults(final List<RangingResult> list) throws RemoteException {
            clearCallingIdentity();
            Executor executor = this.val$executor;
            final RangingResultCallback rangingResultCallback = this.val$callback;
            executor.execute(new Runnable() { // from class: android.net.wifi.rtt.-$$Lambda$WifiRttManager$1$3uT7vOEOvW11feiFUB6LWvcBJEk
                @Override // java.lang.Runnable
                public final void run() {
                    rangingResultCallback.onRangingResults(list);
                }
            });
        }
    }

    @SystemApi
    public void cancelRanging(WorkSource workSource) {
        try {
            this.mService.cancelRanging(workSource);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
