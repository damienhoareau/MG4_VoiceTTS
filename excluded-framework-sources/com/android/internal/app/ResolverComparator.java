package com.android.internal.app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.resolver.IResolverRankerResult;
import android.service.resolver.IResolverRankerService;
import android.service.resolver.ResolverRankerService;
import android.service.resolver.ResolverTarget;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes3.dex */
class ResolverComparator implements Comparator<ResolverActivity.ResolvedComponentInfo> {
    private static final int CONNECTION_COST_TIMEOUT_MILLIS = 200;
    private static final boolean DEBUG = false;
    private static final int NUM_OF_TOP_ANNOTATIONS_TO_USE = 3;
    private static final float RECENCY_MULTIPLIER = 2.0f;
    private static final long RECENCY_TIME_PERIOD = 43200000;
    private static final int RESOLVER_RANKER_RESULT_TIMEOUT = 1;
    private static final int RESOLVER_RANKER_SERVICE_RESULT = 0;
    private static final String TAG = "ResolverComparator";
    private static final long USAGE_STATS_PERIOD = 604800000;
    private static final int WATCHDOG_TIMEOUT_MILLIS = 500;
    private String mAction;
    private AfterCompute mAfterCompute;
    private String[] mAnnotations;
    private final Collator mCollator;
    private CountDownLatch mConnectSignal;
    private ResolverRankerServiceConnection mConnection;
    private String mContentType;
    private Context mContext;
    private final long mCurrentTime;
    private final boolean mHttp;
    private final PackageManager mPm;
    private IResolverRankerService mRanker;
    private ComponentName mRankerServiceName;
    private final String mReferrerPackage;
    private ComponentName mResolvedRankerName;
    private final long mSinceTime;
    private final Map<String, UsageStats> mStats;
    private ArrayList<ResolverTarget> mTargets;
    private final UsageStatsManager mUsm;
    private final LinkedHashMap<ComponentName, ResolverTarget> mTargetsDict = new LinkedHashMap<>();
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.internal.app.ResolverComparator.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 0) {
                if (i == 1) {
                    ResolverComparator.this.mHandler.removeMessages(0);
                    ResolverComparator.this.mAfterCompute.afterCompute();
                    return;
                } else {
                    super.handleMessage(message);
                    return;
                }
            }
            if (ResolverComparator.this.mHandler.hasMessages(1)) {
                if (message.obj != null) {
                    List list = (List) message.obj;
                    if (list != null && ResolverComparator.this.mTargets != null && list.size() == ResolverComparator.this.mTargets.size()) {
                        int size = ResolverComparator.this.mTargets.size();
                        boolean z = false;
                        for (int i2 = 0; i2 < size; i2++) {
                            float selectProbability = ((ResolverTarget) list.get(i2)).getSelectProbability();
                            if (selectProbability != ((ResolverTarget) ResolverComparator.this.mTargets.get(i2)).getSelectProbability()) {
                                ((ResolverTarget) ResolverComparator.this.mTargets.get(i2)).setSelectProbability(selectProbability);
                                z = true;
                            }
                        }
                        if (z) {
                            ResolverComparator resolverComparator = ResolverComparator.this;
                            resolverComparator.mRankerServiceName = resolverComparator.mResolvedRankerName;
                        }
                    } else {
                        Log.e(ResolverComparator.TAG, "Sizes of sent and received ResolverTargets diff.");
                    }
                } else {
                    Log.e(ResolverComparator.TAG, "Receiving null prediction results.");
                }
                ResolverComparator.this.mHandler.removeMessages(1);
                ResolverComparator.this.mAfterCompute.afterCompute();
            }
        }
    };

    public interface AfterCompute {
        void afterCompute();
    }

    public ResolverComparator(Context context, Intent intent, String str, AfterCompute afterCompute) {
        this.mCollator = Collator.getInstance(context.getResources().getConfiguration().locale);
        String scheme = intent.getScheme();
        this.mHttp = IntentFilter.SCHEME_HTTP.equals(scheme) || IntentFilter.SCHEME_HTTPS.equals(scheme);
        this.mReferrerPackage = str;
        this.mAfterCompute = afterCompute;
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mUsm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mCurrentTime = jCurrentTimeMillis;
        long j = jCurrentTimeMillis - 604800000;
        this.mSinceTime = j;
        this.mStats = this.mUsm.queryAndAggregateUsageStats(j, jCurrentTimeMillis);
        this.mContentType = intent.getType();
        getContentAnnotations(intent);
        this.mAction = intent.getAction();
        this.mRankerServiceName = new ComponentName(this.mContext, getClass());
    }

    public void getContentAnnotations(Intent intent) {
        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(Intent.EXTRA_CONTENT_ANNOTATIONS);
        if (stringArrayListExtra != null) {
            int size = stringArrayListExtra.size();
            if (size > 3) {
                size = 3;
            }
            this.mAnnotations = new String[size];
            for (int i = 0; i < size; i++) {
                this.mAnnotations[i] = stringArrayListExtra.get(i);
            }
        }
    }

    public void setCallBack(AfterCompute afterCompute) {
        this.mAfterCompute = afterCompute;
    }

    public void compute(List<ResolverActivity.ResolvedComponentInfo> list) {
        long j;
        reset();
        long j2 = this.mCurrentTime - 43200000;
        float f = 1.0f;
        float f2 = 1.0f;
        float f3 = 1.0f;
        float f4 = 1.0f;
        for (ResolverActivity.ResolvedComponentInfo resolvedComponentInfo : list) {
            ResolverTarget resolverTarget = new ResolverTarget();
            this.mTargetsDict.put(resolvedComponentInfo.name, resolverTarget);
            UsageStats usageStats = this.mStats.get(resolvedComponentInfo.name.getPackageName());
            if (usageStats != null) {
                if (!resolvedComponentInfo.name.getPackageName().equals(this.mReferrerPackage) && !isPersistentProcess(resolvedComponentInfo)) {
                    float fMax = Math.max(usageStats.getLastTimeUsed() - j2, 0L);
                    resolverTarget.setRecencyScore(fMax);
                    if (fMax > f) {
                        f = fMax;
                    }
                }
                float totalTimeInForeground = usageStats.getTotalTimeInForeground();
                resolverTarget.setTimeSpentScore(totalTimeInForeground);
                if (totalTimeInForeground > f2) {
                    f2 = totalTimeInForeground;
                }
                float f5 = usageStats.mLaunchCount;
                resolverTarget.setLaunchScore(f5);
                if (f5 > f3) {
                    f3 = f5;
                }
                float fIntValue = 0.0f;
                if (usageStats.mChooserCounts != null && this.mAction != null && usageStats.mChooserCounts.get(this.mAction) != null) {
                    fIntValue = usageStats.mChooserCounts.get(this.mAction).getOrDefault(this.mContentType, 0).intValue();
                    String[] strArr = this.mAnnotations;
                    if (strArr != null) {
                        int length = strArr.length;
                        int i = 0;
                        while (i < length) {
                            fIntValue += usageStats.mChooserCounts.get(this.mAction).getOrDefault(this.mAnnotations[i], 0).intValue();
                            i++;
                            j2 = j2;
                        }
                    }
                }
                j = j2;
                resolverTarget.setChooserScore(fIntValue);
                if (fIntValue > f4) {
                    f4 = fIntValue;
                }
            } else {
                j = j2;
            }
            j2 = j;
        }
        ArrayList<ResolverTarget> arrayList = new ArrayList<>(this.mTargetsDict.values());
        this.mTargets = arrayList;
        for (ResolverTarget resolverTarget2 : arrayList) {
            float recencyScore = resolverTarget2.getRecencyScore() / f;
            setFeatures(resolverTarget2, recencyScore * recencyScore * RECENCY_MULTIPLIER, resolverTarget2.getLaunchScore() / f3, resolverTarget2.getTimeSpentScore() / f2, resolverTarget2.getChooserScore() / f4);
            addDefaultSelectProbability(resolverTarget2);
        }
        predictSelectProbabilities(this.mTargets);
    }

    @Override // java.util.Comparator
    public int compare(ResolverActivity.ResolvedComponentInfo resolvedComponentInfo, ResolverActivity.ResolvedComponentInfo resolvedComponentInfo2) {
        int iCompare;
        boolean zIsSpecificUriMatch;
        ResolveInfo resolveInfoAt = resolvedComponentInfo.getResolveInfoAt(0);
        ResolveInfo resolveInfoAt2 = resolvedComponentInfo2.getResolveInfoAt(0);
        if (resolveInfoAt.targetUserId != -2) {
            return resolveInfoAt2.targetUserId != -2 ? 0 : 1;
        }
        if (resolveInfoAt2.targetUserId != -2) {
            return -1;
        }
        if (this.mHttp && (zIsSpecificUriMatch = ResolverActivity.isSpecificUriMatch(resolveInfoAt.match)) != ResolverActivity.isSpecificUriMatch(resolveInfoAt2.match)) {
            return zIsSpecificUriMatch ? -1 : 1;
        }
        boolean zIsPinned = resolvedComponentInfo.isPinned();
        boolean zIsPinned2 = resolvedComponentInfo2.isPinned();
        if (zIsPinned && !zIsPinned2) {
            return -1;
        }
        if (!zIsPinned && zIsPinned2) {
            return 1;
        }
        if (!zIsPinned && !zIsPinned2 && this.mStats != null) {
            ResolverTarget resolverTarget = this.mTargetsDict.get(new ComponentName(resolveInfoAt.activityInfo.packageName, resolveInfoAt.activityInfo.name));
            ResolverTarget resolverTarget2 = this.mTargetsDict.get(new ComponentName(resolveInfoAt2.activityInfo.packageName, resolveInfoAt2.activityInfo.name));
            if (resolverTarget != null && resolverTarget2 != null && (iCompare = Float.compare(resolverTarget2.getSelectProbability(), resolverTarget.getSelectProbability())) != 0) {
                return iCompare > 0 ? 1 : -1;
            }
        }
        CharSequence charSequenceLoadLabel = resolveInfoAt.loadLabel(this.mPm);
        if (charSequenceLoadLabel == null) {
            charSequenceLoadLabel = resolveInfoAt.activityInfo.name;
        }
        CharSequence charSequenceLoadLabel2 = resolveInfoAt2.loadLabel(this.mPm);
        if (charSequenceLoadLabel2 == null) {
            charSequenceLoadLabel2 = resolveInfoAt2.activityInfo.name;
        }
        return this.mCollator.compare(charSequenceLoadLabel.toString().trim(), charSequenceLoadLabel2.toString().trim());
    }

    public float getScore(ComponentName componentName) {
        ResolverTarget resolverTarget = this.mTargetsDict.get(componentName);
        if (resolverTarget != null) {
            return resolverTarget.getSelectProbability();
        }
        return 0.0f;
    }

    public void updateChooserCounts(String str, int i, String str2) {
        UsageStatsManager usageStatsManager = this.mUsm;
        if (usageStatsManager != null) {
            usageStatsManager.reportChooserSelection(str, i, this.mContentType, this.mAnnotations, str2);
        }
    }

    public void updateModel(ComponentName componentName) {
        synchronized (this.mLock) {
            if (this.mRanker != null) {
                try {
                    int iIndexOf = new ArrayList(this.mTargetsDict.keySet()).indexOf(componentName);
                    if (iIndexOf >= 0 && this.mTargets != null) {
                        float score = getScore(componentName);
                        int i = 0;
                        Iterator<ResolverTarget> it = this.mTargets.iterator();
                        while (it.hasNext()) {
                            if (it.next().getSelectProbability() > score) {
                                i++;
                            }
                        }
                        logMetrics(i);
                        this.mRanker.train(this.mTargets, iIndexOf);
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in Train: " + e);
                }
            }
        }
    }

    public void destroy() {
        this.mHandler.removeMessages(0);
        this.mHandler.removeMessages(1);
        ResolverRankerServiceConnection resolverRankerServiceConnection = this.mConnection;
        if (resolverRankerServiceConnection != null) {
            this.mContext.unbindService(resolverRankerServiceConnection);
            this.mConnection.destroy();
        }
        AfterCompute afterCompute = this.mAfterCompute;
        if (afterCompute != null) {
            afterCompute.afterCompute();
        }
    }

    private void logMetrics(int i) {
        if (this.mRankerServiceName != null) {
            MetricsLogger metricsLogger = new MetricsLogger();
            LogMaker logMaker = new LogMaker(MetricsProto.MetricsEvent.ACTION_TARGET_SELECTED);
            logMaker.setComponentName(this.mRankerServiceName);
            logMaker.addTaggedData(MetricsProto.MetricsEvent.FIELD_IS_CATEGORY_USED, Integer.valueOf(this.mAnnotations == null ? 0 : 1));
            logMaker.addTaggedData(MetricsProto.MetricsEvent.FIELD_RANKED_POSITION, Integer.valueOf(i));
            metricsLogger.write(logMaker);
        }
    }

    private void initRanker(Context context) {
        synchronized (this.mLock) {
            if (this.mConnection == null || this.mRanker == null) {
                Intent intentResolveRankerService = resolveRankerService();
                if (intentResolveRankerService == null) {
                    return;
                }
                this.mConnectSignal = new CountDownLatch(1);
                ResolverRankerServiceConnection resolverRankerServiceConnection = new ResolverRankerServiceConnection(this.mConnectSignal);
                this.mConnection = resolverRankerServiceConnection;
                context.bindServiceAsUser(intentResolveRankerService, resolverRankerServiceConnection, 1, UserHandle.SYSTEM);
            }
        }
    }

    private Intent resolveRankerService() {
        Intent intent = new Intent(ResolverRankerService.SERVICE_INTERFACE);
        for (ResolveInfo resolveInfo : this.mPm.queryIntentServices(intent, 0)) {
            if (resolveInfo != null && resolveInfo.serviceInfo != null && resolveInfo.serviceInfo.applicationInfo != null) {
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.applicationInfo.packageName, resolveInfo.serviceInfo.name);
                try {
                    if (!"android.permission.BIND_RESOLVER_RANKER_SERVICE".equals(this.mPm.getServiceInfo(componentName, 0).permission)) {
                        Log.w(TAG, "ResolverRankerService " + componentName + " does not require permission android.permission.BIND_RESOLVER_RANKER_SERVICE - this service will not be queried for ResolverComparator. add android:permission=\"android.permission.BIND_RESOLVER_RANKER_SERVICE\" to the <service> tag for " + componentName + " in the manifest.");
                    } else {
                        if (this.mPm.checkPermission("android.permission.PROVIDE_RESOLVER_RANKER_SERVICE", resolveInfo.serviceInfo.packageName) == 0) {
                            this.mResolvedRankerName = componentName;
                            intent.setComponent(componentName);
                            return intent;
                        }
                        Log.w(TAG, "ResolverRankerService " + componentName + " does not hold permission android.permission.PROVIDE_RESOLVER_RANKER_SERVICE - this service will not be queried for ResolverComparator.");
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.e(TAG, "Could not look up service " + componentName + "; component name not found");
                }
            }
        }
        return null;
    }

    private void startWatchDog(int i) {
        if (this.mHandler == null) {
            Log.d(TAG, "Error: Handler is Null; Needs to be initialized.");
        }
        this.mHandler.sendEmptyMessageDelayed(1, i);
    }

    private class ResolverRankerServiceConnection implements ServiceConnection {
        private final CountDownLatch mConnectSignal;
        public final IResolverRankerResult resolverRankerResult = new IResolverRankerResult.Stub() { // from class: com.android.internal.app.ResolverComparator.ResolverRankerServiceConnection.1
            @Override // android.service.resolver.IResolverRankerResult
            public void sendResult(List<ResolverTarget> list) throws RemoteException {
                synchronized (ResolverComparator.this.mLock) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 0;
                    messageObtain.obj = list;
                    ResolverComparator.this.mHandler.sendMessage(messageObtain);
                }
            }
        };

        public ResolverRankerServiceConnection(CountDownLatch countDownLatch) {
            this.mConnectSignal = countDownLatch;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (ResolverComparator.this.mLock) {
                ResolverComparator.this.mRanker = IResolverRankerService.Stub.asInterface(iBinder);
                this.mConnectSignal.countDown();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (ResolverComparator.this.mLock) {
                destroy();
            }
        }

        public void destroy() {
            synchronized (ResolverComparator.this.mLock) {
                ResolverComparator.this.mRanker = null;
            }
        }
    }

    private void reset() {
        this.mTargetsDict.clear();
        this.mTargets = null;
        this.mRankerServiceName = new ComponentName(this.mContext, getClass());
        this.mResolvedRankerName = null;
        startWatchDog(500);
        initRanker(this.mContext);
    }

    private void predictSelectProbabilities(List<ResolverTarget> list) {
        if (this.mConnection != null) {
            try {
                this.mConnectSignal.await(200L, TimeUnit.MILLISECONDS);
                synchronized (this.mLock) {
                    try {
                        if (this.mRanker != null) {
                            this.mRanker.predict(list, this.mConnection.resolverRankerResult);
                            return;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error in Predict: " + e);
            } catch (InterruptedException unused) {
                Log.e(TAG, "Error in Wait for Service Connection.");
            }
        }
        AfterCompute afterCompute = this.mAfterCompute;
        if (afterCompute != null) {
            afterCompute.afterCompute();
        }
    }

    private void addDefaultSelectProbability(ResolverTarget resolverTarget) {
        resolverTarget.setSelectProbability((float) (1.0d / (Math.exp(1.6568f - ((((resolverTarget.getLaunchScore() * 2.5543f) + (resolverTarget.getTimeSpentScore() * 2.8412f)) + (resolverTarget.getRecencyScore() * 0.269f)) + (resolverTarget.getChooserScore() * 4.2222f))) + 1.0d)));
    }

    private void setFeatures(ResolverTarget resolverTarget, float f, float f2, float f3, float f4) {
        resolverTarget.setRecencyScore(f);
        resolverTarget.setLaunchScore(f2);
        resolverTarget.setTimeSpentScore(f3);
        resolverTarget.setChooserScore(f4);
    }

    static boolean isPersistentProcess(ResolverActivity.ResolvedComponentInfo resolvedComponentInfo) {
        return (resolvedComponentInfo == null || resolvedComponentInfo.getCount() <= 0 || (resolvedComponentInfo.getResolveInfoAt(0).activityInfo.applicationInfo.flags & 8) == 0) ? false : true;
    }
}
