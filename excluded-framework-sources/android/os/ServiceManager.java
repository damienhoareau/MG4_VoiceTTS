package android.os;

import android.util.Log;
import android.util.TimedRemoteCaller;
import com.android.internal.os.BinderInternal;
import com.android.internal.util.StatLogger;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public final class ServiceManager {
    private static final int SLOW_LOG_INTERVAL_MS = 5000;
    private static final int STATS_LOG_INTERVAL_MS = 5000;
    private static final String TAG = "ServiceManager";
    private static int sGetServiceAccumulatedCallCount;
    private static int sGetServiceAccumulatedUs;
    private static long sLastSlowLogActualTime;
    private static long sLastSlowLogUptime;
    private static long sLastStatsLogUptime;
    private static IServiceManager sServiceManager;
    private static final Object sLock = new Object();
    private static HashMap<String, IBinder> sCache = new HashMap<>();
    private static final long GET_SERVICE_SLOW_THRESHOLD_US_CORE = SystemProperties.getInt("debug.servicemanager.slow_call_core_ms", 10) * 1000;
    private static final long GET_SERVICE_SLOW_THRESHOLD_US_NON_CORE = SystemProperties.getInt("debug.servicemanager.slow_call_ms", 50) * 1000;
    private static final int GET_SERVICE_LOG_EVERY_CALLS_CORE = SystemProperties.getInt("debug.servicemanager.log_calls_core", 100);
    private static final int GET_SERVICE_LOG_EVERY_CALLS_NON_CORE = SystemProperties.getInt("debug.servicemanager.log_calls", 200);
    public static final StatLogger sStatLogger = new StatLogger(new String[]{"getService()"});

    interface Stats {
        public static final int COUNT = 1;
        public static final int GET_SERVICE = 0;
    }

    private static IServiceManager getIServiceManager() {
        IServiceManager iServiceManager = sServiceManager;
        if (iServiceManager != null) {
            return iServiceManager;
        }
        IServiceManager iServiceManagerAsInterface = ServiceManagerNative.asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
        sServiceManager = iServiceManagerAsInterface;
        return iServiceManagerAsInterface;
    }

    public static IBinder getService(String str) {
        try {
            IBinder iBinder = sCache.get(str);
            return iBinder != null ? iBinder : Binder.allowBlocking(rawGetService(str));
        } catch (RemoteException e) {
            Log.e(TAG, "error in getService", e);
            return null;
        }
    }

    public static IBinder getServiceOrThrow(String str) throws ServiceNotFoundException {
        IBinder service = getService(str);
        if (service != null) {
            return service;
        }
        throw new ServiceNotFoundException(str);
    }

    public static void addService(String str, IBinder iBinder) {
        addService(str, iBinder, false, 8);
    }

    public static void addService(String str, IBinder iBinder, boolean z) {
        addService(str, iBinder, z, 8);
    }

    public static void addService(String str, IBinder iBinder, boolean z, int i) {
        try {
            getIServiceManager().addService(str, iBinder, z, i);
        } catch (RemoteException e) {
            Log.e(TAG, "error in addService", e);
        }
    }

    public static IBinder checkService(String str) {
        try {
            IBinder iBinder = sCache.get(str);
            return iBinder != null ? iBinder : Binder.allowBlocking(getIServiceManager().checkService(str));
        } catch (RemoteException e) {
            Log.e(TAG, "error in checkService", e);
            return null;
        }
    }

    public static String[] listServices() {
        try {
            return getIServiceManager().listServices(15);
        } catch (RemoteException e) {
            Log.e(TAG, "error in listServices", e);
            return null;
        }
    }

    public static void initServiceCache(Map<String, IBinder> map) {
        if (sCache.size() != 0) {
            throw new IllegalStateException("setServiceCache may only be called once");
        }
        sCache.putAll(map);
    }

    public static class ServiceNotFoundException extends Exception {
        public ServiceNotFoundException(String str) {
            super("No service published for: " + str);
        }
    }

    private static IBinder rawGetService(String str) throws RemoteException {
        long j;
        int i;
        long time = sStatLogger.getTime();
        IBinder service = getIServiceManager().getService(str);
        int iLogDurationStat = (int) sStatLogger.logDurationStat(0, time);
        boolean zIsCore = UserHandle.isCore(Process.myUid());
        if (zIsCore) {
            j = GET_SERVICE_SLOW_THRESHOLD_US_CORE;
        } else {
            j = GET_SERVICE_SLOW_THRESHOLD_US_NON_CORE;
        }
        synchronized (sLock) {
            sGetServiceAccumulatedUs += iLogDurationStat;
            sGetServiceAccumulatedCallCount++;
            long jUptimeMillis = SystemClock.uptimeMillis();
            long j2 = iLogDurationStat;
            if (j2 >= j && (jUptimeMillis > sLastSlowLogUptime + TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS || sLastSlowLogActualTime < j2)) {
                EventLogTags.writeServiceManagerSlow(iLogDurationStat / 1000, str);
                sLastSlowLogUptime = jUptimeMillis;
                sLastSlowLogActualTime = j2;
            }
            if (zIsCore) {
                i = GET_SERVICE_LOG_EVERY_CALLS_CORE;
            } else {
                i = GET_SERVICE_LOG_EVERY_CALLS_NON_CORE;
            }
            if (sGetServiceAccumulatedCallCount >= i && jUptimeMillis >= sLastStatsLogUptime + TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS) {
                EventLogTags.writeServiceManagerStats(sGetServiceAccumulatedCallCount, sGetServiceAccumulatedUs / 1000, (int) (jUptimeMillis - sLastStatsLogUptime));
                sGetServiceAccumulatedCallCount = 0;
                sGetServiceAccumulatedUs = 0;
                sLastStatsLogUptime = jUptimeMillis;
            }
        }
        return service;
    }
}
