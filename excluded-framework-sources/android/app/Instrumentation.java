package android.app;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.PerformanceCollector;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.TestLooperManager;
import android.os.UserHandle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.internal.content.ReferrerIntent;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Instrumentation {
    public static final String REPORT_KEY_IDENTIFIER = "id";
    public static final String REPORT_KEY_STREAMRESULT = "stream";
    private static final String TAG = "Instrumentation";
    private List<ActivityMonitor> mActivityMonitors;
    private Context mAppContext;
    private ComponentName mComponent;
    private Context mInstrContext;
    private PerformanceCollector mPerformanceCollector;
    private Thread mRunner;
    private UiAutomation mUiAutomation;
    private IUiAutomationConnection mUiAutomationConnection;
    private List<ActivityWaiter> mWaitingActivities;
    private IInstrumentationWatcher mWatcher;
    private final Object mSync = new Object();
    private ActivityThread mThread = null;
    private MessageQueue mMessageQueue = null;
    private boolean mAutomaticPerformanceSnapshots = false;
    private Bundle mPerfMetrics = new Bundle();

    @Retention(RetentionPolicy.SOURCE)
    public @interface UiAutomationFlags {
    }

    public void onCreate(Bundle bundle) {
    }

    public void onDestroy() {
    }

    public boolean onException(Object obj, Throwable th) {
        return false;
    }

    public void onStart() {
    }

    private void checkInstrumenting(String str) {
        if (this.mInstrContext != null) {
            return;
        }
        throw new RuntimeException(str + " cannot be called outside of instrumented processes");
    }

    public void start() {
        if (this.mRunner != null) {
            throw new RuntimeException("Instrumentation already started");
        }
        InstrumentationThread instrumentationThread = new InstrumentationThread("Instr: " + getClass().getName());
        this.mRunner = instrumentationThread;
        instrumentationThread.start();
    }

    public void sendStatus(int i, Bundle bundle) {
        IInstrumentationWatcher iInstrumentationWatcher = this.mWatcher;
        if (iInstrumentationWatcher != null) {
            try {
                iInstrumentationWatcher.instrumentationStatus(this.mComponent, i, bundle);
            } catch (RemoteException unused) {
                this.mWatcher = null;
            }
        }
    }

    public void addResults(Bundle bundle) {
        try {
            ActivityManager.getService().addInstrumentationResults(this.mThread.getApplicationThread(), bundle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void finish(int i, Bundle bundle) {
        if (this.mAutomaticPerformanceSnapshots) {
            endPerformanceSnapshot();
        }
        if (this.mPerfMetrics != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putAll(this.mPerfMetrics);
        }
        UiAutomation uiAutomation = this.mUiAutomation;
        if (uiAutomation != null && !uiAutomation.isDestroyed()) {
            this.mUiAutomation.disconnect();
            this.mUiAutomation = null;
        }
        this.mThread.finishInstrumentation(i, bundle);
    }

    public void setAutomaticPerformanceSnapshots() {
        this.mAutomaticPerformanceSnapshots = true;
        this.mPerformanceCollector = new PerformanceCollector();
    }

    public void startPerformanceSnapshot() {
        if (isProfiling()) {
            return;
        }
        this.mPerformanceCollector.beginSnapshot(null);
    }

    public void endPerformanceSnapshot() {
        if (isProfiling()) {
            return;
        }
        this.mPerfMetrics = this.mPerformanceCollector.endSnapshot();
    }

    public Context getContext() {
        return this.mInstrContext;
    }

    public ComponentName getComponentName() {
        return this.mComponent;
    }

    public Context getTargetContext() {
        return this.mAppContext;
    }

    public String getProcessName() {
        return this.mThread.getProcessName();
    }

    public boolean isProfiling() {
        return this.mThread.isProfiling();
    }

    public void startProfiling() {
        if (this.mThread.isProfiling()) {
            File file = new File(this.mThread.getProfileFilePath());
            file.getParentFile().mkdirs();
            Debug.startMethodTracing(file.toString(), 8388608);
        }
    }

    public void stopProfiling() {
        if (this.mThread.isProfiling()) {
            Debug.stopMethodTracing();
        }
    }

    public void setInTouchMode(boolean z) {
        try {
            IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)).setInTouchMode(z);
        } catch (RemoteException unused) {
        }
    }

    public void waitForIdle(Runnable runnable) {
        this.mMessageQueue.addIdleHandler(new Idler(runnable));
        this.mThread.getHandler().post(new EmptyRunnable());
    }

    public void waitForIdleSync() {
        validateNotAppThread();
        Idler idler = new Idler(null);
        this.mMessageQueue.addIdleHandler(idler);
        this.mThread.getHandler().post(new EmptyRunnable());
        idler.waitForIdle();
    }

    public void runOnMainSync(Runnable runnable) {
        validateNotAppThread();
        SyncRunnable syncRunnable = new SyncRunnable(runnable);
        this.mThread.getHandler().post(syncRunnable);
        syncRunnable.waitForComplete();
    }

    public Activity startActivitySync(Intent intent) {
        return startActivitySync(intent, null);
    }

    public Activity startActivitySync(Intent intent, Bundle bundle) {
        Activity activity;
        validateNotAppThread();
        synchronized (this.mSync) {
            Intent intent2 = new Intent(intent);
            ActivityInfo activityInfoResolveActivityInfo = intent2.resolveActivityInfo(getTargetContext().getPackageManager(), 0);
            if (activityInfoResolveActivityInfo == null) {
                throw new RuntimeException("Unable to resolve activity for: " + intent2);
            }
            String processName = this.mThread.getProcessName();
            if (!activityInfoResolveActivityInfo.processName.equals(processName)) {
                throw new RuntimeException("Intent in process " + processName + " resolved to different process " + activityInfoResolveActivityInfo.processName + ": " + intent2);
            }
            intent2.setComponent(new ComponentName(activityInfoResolveActivityInfo.applicationInfo.packageName, activityInfoResolveActivityInfo.name));
            ActivityWaiter activityWaiter = new ActivityWaiter(intent2);
            if (this.mWaitingActivities == null) {
                this.mWaitingActivities = new ArrayList();
            }
            this.mWaitingActivities.add(activityWaiter);
            getTargetContext().startActivity(intent2, bundle);
            do {
                try {
                    this.mSync.wait();
                } catch (InterruptedException unused) {
                }
            } while (this.mWaitingActivities.contains(activityWaiter));
            activity = activityWaiter.activity;
        }
        return activity;
    }

    public static class ActivityMonitor {
        private final boolean mBlock;
        private final String mClass;
        int mHits;
        private final boolean mIgnoreMatchingSpecificIntents;
        Activity mLastActivity;
        private final ActivityResult mResult;
        private final IntentFilter mWhich;

        public ActivityResult onStartActivity(Intent intent) {
            return null;
        }

        public ActivityMonitor(IntentFilter intentFilter, ActivityResult activityResult, boolean z) {
            this.mHits = 0;
            this.mLastActivity = null;
            this.mWhich = intentFilter;
            this.mClass = null;
            this.mResult = activityResult;
            this.mBlock = z;
            this.mIgnoreMatchingSpecificIntents = false;
        }

        public ActivityMonitor(String str, ActivityResult activityResult, boolean z) {
            this.mHits = 0;
            this.mLastActivity = null;
            this.mWhich = null;
            this.mClass = str;
            this.mResult = activityResult;
            this.mBlock = z;
            this.mIgnoreMatchingSpecificIntents = false;
        }

        public ActivityMonitor() {
            this.mHits = 0;
            this.mLastActivity = null;
            this.mWhich = null;
            this.mClass = null;
            this.mResult = null;
            this.mBlock = false;
            this.mIgnoreMatchingSpecificIntents = true;
        }

        final boolean ignoreMatchingSpecificIntents() {
            return this.mIgnoreMatchingSpecificIntents;
        }

        public final IntentFilter getFilter() {
            return this.mWhich;
        }

        public final ActivityResult getResult() {
            return this.mResult;
        }

        public final boolean isBlocking() {
            return this.mBlock;
        }

        public final int getHits() {
            return this.mHits;
        }

        public final Activity getLastActivity() {
            return this.mLastActivity;
        }

        public final Activity waitForActivity() {
            Activity activity;
            synchronized (this) {
                while (this.mLastActivity == null) {
                    try {
                        wait();
                    } catch (InterruptedException unused) {
                    }
                }
                activity = this.mLastActivity;
                this.mLastActivity = null;
            }
            return activity;
        }

        public final Activity waitForActivityWithTimeout(long j) {
            synchronized (this) {
                if (this.mLastActivity == null) {
                    try {
                        wait(j);
                    } catch (InterruptedException unused) {
                    }
                }
                if (this.mLastActivity == null) {
                    return null;
                }
                Activity activity = this.mLastActivity;
                this.mLastActivity = null;
                return activity;
            }
        }

        final boolean match(Context context, Activity activity, Intent intent) {
            if (this.mIgnoreMatchingSpecificIntents) {
                return false;
            }
            synchronized (this) {
                if (this.mWhich != null && this.mWhich.match(context.getContentResolver(), intent, true, Instrumentation.TAG) < 0) {
                    return false;
                }
                if (this.mClass != null) {
                    String className = null;
                    if (activity != null) {
                        className = activity.getClass().getName();
                    } else if (intent.getComponent() != null) {
                        className = intent.getComponent().getClassName();
                    }
                    if (className == null || !this.mClass.equals(className)) {
                        return false;
                    }
                }
                if (activity != null) {
                    this.mLastActivity = activity;
                    notifyAll();
                }
                return true;
            }
        }
    }

    public void addMonitor(ActivityMonitor activityMonitor) {
        synchronized (this.mSync) {
            if (this.mActivityMonitors == null) {
                this.mActivityMonitors = new ArrayList();
            }
            this.mActivityMonitors.add(activityMonitor);
        }
    }

    public ActivityMonitor addMonitor(IntentFilter intentFilter, ActivityResult activityResult, boolean z) {
        ActivityMonitor activityMonitor = new ActivityMonitor(intentFilter, activityResult, z);
        addMonitor(activityMonitor);
        return activityMonitor;
    }

    public ActivityMonitor addMonitor(String str, ActivityResult activityResult, boolean z) {
        ActivityMonitor activityMonitor = new ActivityMonitor(str, activityResult, z);
        addMonitor(activityMonitor);
        return activityMonitor;
    }

    public boolean checkMonitorHit(ActivityMonitor activityMonitor, int i) {
        waitForIdleSync();
        synchronized (this.mSync) {
            if (activityMonitor.getHits() < i) {
                return false;
            }
            this.mActivityMonitors.remove(activityMonitor);
            return true;
        }
    }

    public Activity waitForMonitor(ActivityMonitor activityMonitor) {
        Activity activityWaitForActivity = activityMonitor.waitForActivity();
        synchronized (this.mSync) {
            this.mActivityMonitors.remove(activityMonitor);
        }
        return activityWaitForActivity;
    }

    public Activity waitForMonitorWithTimeout(ActivityMonitor activityMonitor, long j) {
        Activity activityWaitForActivityWithTimeout = activityMonitor.waitForActivityWithTimeout(j);
        synchronized (this.mSync) {
            this.mActivityMonitors.remove(activityMonitor);
        }
        return activityWaitForActivityWithTimeout;
    }

    public void removeMonitor(ActivityMonitor activityMonitor) {
        synchronized (this.mSync) {
            this.mActivityMonitors.remove(activityMonitor);
        }
    }

    /* JADX INFO: renamed from: android.app.Instrumentation$1MenuRunnable, reason: invalid class name */
    class C1MenuRunnable implements Runnable {
        private final Activity activity;
        private final int flags;
        private final int identifier;
        boolean returnValue;

        public C1MenuRunnable(Activity activity, int i, int i2) {
            this.activity = activity;
            this.identifier = i;
            this.flags = i2;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.returnValue = this.activity.getWindow().performPanelIdentifierAction(0, this.identifier, this.flags);
        }
    }

    public boolean invokeMenuActionSync(Activity activity, int i, int i2) {
        C1MenuRunnable c1MenuRunnable = new C1MenuRunnable(activity, i, i2);
        runOnMainSync(c1MenuRunnable);
        return c1MenuRunnable.returnValue;
    }

    public boolean invokeContextMenuAction(Activity activity, int i, int i2) {
        validateNotAppThread();
        sendKeySync(new KeyEvent(0, 23));
        waitForIdleSync();
        try {
            Thread.sleep(ViewConfiguration.getLongPressTimeout());
            sendKeySync(new KeyEvent(1, 23));
            waitForIdleSync();
            C1ContextMenuRunnable c1ContextMenuRunnable = new C1ContextMenuRunnable(activity, i, i2);
            runOnMainSync(c1ContextMenuRunnable);
            return c1ContextMenuRunnable.returnValue;
        } catch (InterruptedException e) {
            Log.e(TAG, "Could not sleep for long press timeout", e);
            return false;
        }
    }

    /* JADX INFO: renamed from: android.app.Instrumentation$1ContextMenuRunnable, reason: invalid class name */
    class C1ContextMenuRunnable implements Runnable {
        private final Activity activity;
        private final int flags;
        private final int identifier;
        boolean returnValue;

        public C1ContextMenuRunnable(Activity activity, int i, int i2) {
            this.activity = activity;
            this.identifier = i;
            this.flags = i2;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.returnValue = this.activity.getWindow().performContextMenuIdentifierAction(this.identifier, this.flags);
        }
    }

    public void sendStringSync(String str) {
        KeyEvent[] events;
        if (str == null || (events = KeyCharacterMap.load(-1).getEvents(str.toCharArray())) == null) {
            return;
        }
        for (KeyEvent keyEvent : events) {
            sendKeySync(KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), 0));
        }
    }

    public void sendKeySync(KeyEvent keyEvent) {
        validateNotAppThread();
        long downTime = keyEvent.getDownTime();
        long eventTime = keyEvent.getEventTime();
        int action = keyEvent.getAction();
        int keyCode = keyEvent.getKeyCode();
        int repeatCount = keyEvent.getRepeatCount();
        int metaState = keyEvent.getMetaState();
        int deviceId = keyEvent.getDeviceId();
        int scanCode = keyEvent.getScanCode();
        int source = keyEvent.getSource();
        int flags = keyEvent.getFlags();
        if (source == 0) {
            source = 257;
        }
        int i = source;
        if (eventTime == 0) {
            eventTime = SystemClock.uptimeMillis();
        }
        if (downTime == 0) {
            downTime = eventTime;
        }
        InputManager.getInstance().injectInputEvent(new KeyEvent(downTime, eventTime, action, keyCode, repeatCount, metaState, deviceId, scanCode, flags | 8, i), 2);
    }

    public void sendKeyDownUpSync(int i) {
        sendKeySync(new KeyEvent(0, i));
        sendKeySync(new KeyEvent(1, i));
    }

    public void sendCharacterSync(int i) {
        sendKeySync(new KeyEvent(0, i));
        sendKeySync(new KeyEvent(1, i));
    }

    public void sendPointerSync(MotionEvent motionEvent) {
        validateNotAppThread();
        if ((motionEvent.getSource() & 2) == 0) {
            motionEvent.setSource(4098);
        }
        InputManager.getInstance().injectInputEvent(motionEvent, 2);
    }

    public void sendTrackballEventSync(MotionEvent motionEvent) {
        validateNotAppThread();
        if ((motionEvent.getSource() & 4) == 0) {
            motionEvent.setSource(65540);
        }
        InputManager.getInstance().injectInputEvent(motionEvent, 2);
    }

    public Application newApplication(ClassLoader classLoader, String str, Context context) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Application applicationInstantiateApplication = getFactory(context.getPackageName()).instantiateApplication(classLoader, str);
        applicationInstantiateApplication.attach(context);
        return applicationInstantiateApplication;
    }

    public static Application newApplication(Class<?> cls, Context context) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Application application = (Application) cls.newInstance();
        application.attach(context);
        return application;
    }

    public void callApplicationOnCreate(Application application) {
        application.onCreate();
    }

    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) throws IllegalAccessException, InstantiationException {
        Activity activity2 = (Activity) cls.newInstance();
        activity2.attach(context, null, this, iBinder, 0, application == null ? new Application() : application, intent, activityInfo, charSequence, activity, str, (Activity.NonConfigurationInstances) obj, new Configuration(), null, null, null, null);
        return activity2;
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return getFactory((intent == null || intent.getComponent() == null) ? null : intent.getComponent().getPackageName()).instantiateActivity(classLoader, str, intent);
    }

    private AppComponentFactory getFactory(String str) {
        if (str == null) {
            Log.e(TAG, "No pkg specified, disabling AppComponentFactory");
            return AppComponentFactory.DEFAULT;
        }
        ActivityThread activityThread = this.mThread;
        if (activityThread == null) {
            Log.e(TAG, "Uninitialized ActivityThread, likely app-created Instrumentation, disabling AppComponentFactory", new Throwable());
            return AppComponentFactory.DEFAULT;
        }
        LoadedApk loadedApkPeekPackageInfo = activityThread.peekPackageInfo(str, true);
        if (loadedApkPeekPackageInfo == null) {
            loadedApkPeekPackageInfo = this.mThread.getSystemContext().mPackageInfo;
        }
        return loadedApkPeekPackageInfo.getAppFactory();
    }

    private void prePerformCreate(Activity activity) {
        if (this.mWaitingActivities != null) {
            synchronized (this.mSync) {
                int size = this.mWaitingActivities.size();
                for (int i = 0; i < size; i++) {
                    ActivityWaiter activityWaiter = this.mWaitingActivities.get(i);
                    if (activityWaiter.intent.filterEquals(activity.getIntent())) {
                        activityWaiter.activity = activity;
                        this.mMessageQueue.addIdleHandler(new ActivityGoing(activityWaiter));
                    }
                }
            }
        }
    }

    private void postPerformCreate(Activity activity) {
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i = 0; i < size; i++) {
                    this.mActivityMonitors.get(i).match(activity, activity, activity.getIntent());
                }
            }
        }
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        prePerformCreate(activity);
        activity.performCreate(bundle);
        postPerformCreate(activity);
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        prePerformCreate(activity);
        activity.performCreate(bundle, persistableBundle);
        postPerformCreate(activity);
    }

    public void callActivityOnDestroy(Activity activity) {
        activity.performDestroy();
    }

    public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle) {
        activity.performRestoreInstanceState(bundle);
    }

    public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        activity.performRestoreInstanceState(bundle, persistableBundle);
    }

    public void callActivityOnPostCreate(Activity activity, Bundle bundle) {
        activity.onPostCreate(bundle);
    }

    public void callActivityOnPostCreate(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        activity.onPostCreate(bundle, persistableBundle);
    }

    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        activity.performNewIntent(intent);
    }

    public void callActivityOnNewIntent(Activity activity, ReferrerIntent referrerIntent) {
        String str = activity.mReferrer;
        if (referrerIntent != null) {
            try {
                activity.mReferrer = referrerIntent.mReferrer;
            } finally {
                activity.mReferrer = str;
            }
        }
        callActivityOnNewIntent(activity, referrerIntent != null ? new Intent(referrerIntent) : null);
    }

    public void callActivityOnStart(Activity activity) {
        activity.onStart();
    }

    public void callActivityOnRestart(Activity activity) {
        activity.onRestart();
    }

    public void callActivityOnResume(Activity activity) {
        activity.mResumed = true;
        activity.onResume();
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i = 0; i < size; i++) {
                    this.mActivityMonitors.get(i).match(activity, activity, activity.getIntent());
                }
            }
        }
    }

    public void callActivityOnStop(Activity activity) {
        activity.onStop();
    }

    public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle) {
        activity.performSaveInstanceState(bundle);
    }

    public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        activity.performSaveInstanceState(bundle, persistableBundle);
    }

    public void callActivityOnPause(Activity activity) {
        activity.performPause();
    }

    public void callActivityOnUserLeaving(Activity activity) {
        activity.performUserLeaving();
    }

    @Deprecated
    public void startAllocCounting() {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        Debug.resetAllCounts();
        Debug.startAllocCounting();
    }

    @Deprecated
    public void stopAllocCounting() {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        Debug.stopAllocCounting();
    }

    private void addValue(String str, int i, Bundle bundle) {
        if (bundle.containsKey(str)) {
            ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList(str);
            if (integerArrayList != null) {
                integerArrayList.add(Integer.valueOf(i));
                return;
            }
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(Integer.valueOf(i));
        bundle.putIntegerArrayList(str, arrayList);
    }

    public Bundle getAllocCounts() {
        Bundle bundle = new Bundle();
        bundle.putLong(PerformanceCollector.METRIC_KEY_GLOBAL_ALLOC_COUNT, Debug.getGlobalAllocCount());
        bundle.putLong(PerformanceCollector.METRIC_KEY_GLOBAL_ALLOC_SIZE, Debug.getGlobalAllocSize());
        bundle.putLong(PerformanceCollector.METRIC_KEY_GLOBAL_FREED_COUNT, Debug.getGlobalFreedCount());
        bundle.putLong(PerformanceCollector.METRIC_KEY_GLOBAL_FREED_SIZE, Debug.getGlobalFreedSize());
        bundle.putLong(PerformanceCollector.METRIC_KEY_GC_INVOCATION_COUNT, Debug.getGlobalGcInvocationCount());
        return bundle;
    }

    public Bundle getBinderCounts() {
        Bundle bundle = new Bundle();
        bundle.putLong(PerformanceCollector.METRIC_KEY_SENT_TRANSACTIONS, Debug.getBinderSentTransactions());
        bundle.putLong(PerformanceCollector.METRIC_KEY_RECEIVED_TRANSACTIONS, Debug.getBinderReceivedTransactions());
        return bundle;
    }

    public static final class ActivityResult {
        private final int mResultCode;
        private final Intent mResultData;

        public ActivityResult(int i, Intent intent) {
            this.mResultCode = i;
            this.mResultData = intent;
        }

        public int getResultCode() {
            return this.mResultCode;
        }

        public Intent getResultData() {
            return this.mResultData;
        }
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        Uri uriOnProvideReferrer = activity != null ? activity.onProvideReferrer() : null;
        if (uriOnProvideReferrer != null) {
            intent.putExtra(Intent.EXTRA_REFERRER, uriOnProvideReferrer);
        }
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i2);
                    ActivityResult activityResultOnStartActivity = activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intent) : null;
                    if (activityResultOnStartActivity != null) {
                        activityMonitor.mHits++;
                        return activityResultOnStartActivity;
                    }
                    if (activityMonitor.match(context, null, intent)) {
                        activityMonitor.mHits++;
                        if (!activityMonitor.isBlocking()) {
                            break;
                        }
                        return i >= 0 ? activityMonitor.getResult() : null;
                    }
                }
            }
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(context);
            checkStartActivityResult(ActivityManager.getService().startActivity(iApplicationThread, context.getBasePackageName(), intent, intent.resolveTypeIfNeeded(context.getContentResolver()), iBinder2, activity != null ? activity.mEmbeddedID : null, i, 0, null, bundle), intent);
            return null;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    public void execStartActivities(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent[] intentArr, Bundle bundle) {
        execStartActivitiesAsUser(context, iBinder, iBinder2, activity, intentArr, bundle, context.getUserId());
    }

    public int execStartActivitiesAsUser(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent[] intentArr, Bundle bundle, int i) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i2);
                    if ((activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intentArr[0]) : null) != null) {
                        activityMonitor.mHits++;
                        return -96;
                    }
                    if (activityMonitor.match(context, null, intentArr[0])) {
                        activityMonitor.mHits++;
                        if (!activityMonitor.isBlocking()) {
                            break;
                        }
                        return -96;
                    }
                }
            }
        }
        try {
            String[] strArr = new String[intentArr.length];
            for (int i3 = 0; i3 < intentArr.length; i3++) {
                intentArr[i3].migrateExtraStreamToClipData();
                intentArr[i3].prepareToLeaveProcess(context);
                strArr[i3] = intentArr[i3].resolveTypeIfNeeded(context.getContentResolver());
            }
            int iStartActivities = ActivityManager.getService().startActivities(iApplicationThread, context.getBasePackageName(), intentArr, strArr, iBinder2, bundle, i);
            checkStartActivityResult(iStartActivities, intentArr[0]);
            return iStartActivities;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, String str, Intent intent, int i, Bundle bundle) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i2);
                    ActivityResult activityResultOnStartActivity = activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intent) : null;
                    if (activityResultOnStartActivity != null) {
                        activityMonitor.mHits++;
                        return activityResultOnStartActivity;
                    }
                    if (activityMonitor.match(context, null, intent)) {
                        activityMonitor.mHits++;
                        if (!activityMonitor.isBlocking()) {
                            break;
                        }
                        return i >= 0 ? activityMonitor.getResult() : null;
                    }
                }
            }
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(context);
            checkStartActivityResult(ActivityManager.getService().startActivity(iApplicationThread, context.getBasePackageName(), intent, intent.resolveTypeIfNeeded(context.getContentResolver()), iBinder2, str, i, 0, null, bundle), intent);
            return null;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    public ActivityResult execStartActivity(Context context, IBinder iBinder, IBinder iBinder2, String str, Intent intent, int i, Bundle bundle, UserHandle userHandle) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i2);
                    ActivityResult activityResultOnStartActivity = activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intent) : null;
                    if (activityResultOnStartActivity != null) {
                        activityMonitor.mHits++;
                        return activityResultOnStartActivity;
                    }
                    if (activityMonitor.match(context, null, intent)) {
                        activityMonitor.mHits++;
                        if (!activityMonitor.isBlocking()) {
                            break;
                        }
                        return i >= 0 ? activityMonitor.getResult() : null;
                    }
                }
            }
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(context);
            checkStartActivityResult(ActivityManager.getService().startActivityAsUser(iApplicationThread, context.getBasePackageName(), intent, intent.resolveTypeIfNeeded(context.getContentResolver()), iBinder2, str, i, 0, null, bundle, userHandle.getIdentifier()), intent);
            return null;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    public ActivityResult execStartActivityAsCaller(Context context, IBinder iBinder, IBinder iBinder2, Activity activity, Intent intent, int i, Bundle bundle, boolean z, int i2) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i3 = 0; i3 < size; i3++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i3);
                    ActivityResult activityResultOnStartActivity = activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intent) : null;
                    if (activityResultOnStartActivity != null) {
                        activityMonitor.mHits++;
                        return activityResultOnStartActivity;
                    }
                    if (activityMonitor.match(context, null, intent)) {
                        activityMonitor.mHits++;
                        if (!activityMonitor.isBlocking()) {
                            break;
                        }
                        return i >= 0 ? activityMonitor.getResult() : null;
                    }
                }
            }
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(context);
            checkStartActivityResult(ActivityManager.getService().startActivityAsCaller(iApplicationThread, context.getBasePackageName(), intent, intent.resolveTypeIfNeeded(context.getContentResolver()), iBinder2, activity != null ? activity.mEmbeddedID : null, i, 0, null, bundle, z, i2), intent);
            return null;
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    public void execStartActivityFromAppTask(Context context, IBinder iBinder, IAppTask iAppTask, Intent intent, Bundle bundle) {
        IApplicationThread iApplicationThread = (IApplicationThread) iBinder;
        if (this.mActivityMonitors != null) {
            synchronized (this.mSync) {
                int size = this.mActivityMonitors.size();
                for (int i = 0; i < size; i++) {
                    ActivityMonitor activityMonitor = this.mActivityMonitors.get(i);
                    if ((activityMonitor.ignoreMatchingSpecificIntents() ? activityMonitor.onStartActivity(intent) : null) != null) {
                        activityMonitor.mHits++;
                        return;
                    } else {
                        if (activityMonitor.match(context, null, intent)) {
                            activityMonitor.mHits++;
                            if (!activityMonitor.isBlocking()) {
                                break;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(context);
            checkStartActivityResult(iAppTask.startActivity(iApplicationThread.asBinder(), context.getBasePackageName(), intent, intent.resolveTypeIfNeeded(context.getContentResolver()), bundle), intent);
        } catch (RemoteException e) {
            throw new RuntimeException("Failure from system", e);
        }
    }

    final void init(ActivityThread activityThread, Context context, Context context2, ComponentName componentName, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection) {
        this.mThread = activityThread;
        activityThread.getLooper();
        this.mMessageQueue = Looper.myQueue();
        this.mInstrContext = context;
        this.mAppContext = context2;
        this.mComponent = componentName;
        this.mWatcher = iInstrumentationWatcher;
        this.mUiAutomationConnection = iUiAutomationConnection;
    }

    final void basicInit(ActivityThread activityThread) {
        this.mThread = activityThread;
    }

    public static void checkStartActivityResult(int i, Object obj) {
        if (ActivityManager.isStartResultFatalError(i)) {
            switch (i) {
                case -100:
                    throw new IllegalStateException("Cannot start voice activity on a hidden session");
                case ActivityManager.START_VOICE_NOT_ACTIVE_SESSION /* -99 */:
                    throw new IllegalStateException("Session calling startVoiceActivity does not match active session");
                case ActivityManager.START_NOT_CURRENT_USER_ACTIVITY /* -98 */:
                default:
                    throw new AndroidRuntimeException("Unknown error code " + i + " when starting " + obj);
                case ActivityManager.START_NOT_VOICE_COMPATIBLE /* -97 */:
                    throw new SecurityException("Starting under voice control not allowed for: " + obj);
                case ActivityManager.START_CANCELED /* -96 */:
                    throw new AndroidRuntimeException("Activity could not be started for " + obj);
                case ActivityManager.START_NOT_ACTIVITY /* -95 */:
                    throw new IllegalArgumentException("PendingIntent is not an activity");
                case ActivityManager.START_PERMISSION_DENIED /* -94 */:
                    throw new SecurityException("Not allowed to start activity " + obj);
                case ActivityManager.START_FORWARD_AND_REQUEST_CONFLICT /* -93 */:
                    throw new AndroidRuntimeException("FORWARD_RESULT_FLAG used while also requesting a result");
                case ActivityManager.START_CLASS_NOT_FOUND /* -92 */:
                case ActivityManager.START_INTENT_NOT_RESOLVED /* -91 */:
                    if (obj instanceof Intent) {
                        Intent intent = (Intent) obj;
                        if (intent.getComponent() != null) {
                            throw new ActivityNotFoundException("Unable to find explicit activity class " + intent.getComponent().toShortString() + "; have you declared this activity in your AndroidManifest.xml?");
                        }
                    }
                    throw new ActivityNotFoundException("No Activity found to handle " + obj);
                case ActivityManager.START_ASSISTANT_HIDDEN_SESSION /* -90 */:
                    throw new IllegalStateException("Cannot start assistant activity on a hidden session");
                case ActivityManager.START_ASSISTANT_NOT_ACTIVE_SESSION /* -89 */:
                    throw new IllegalStateException("Session calling startAssistantActivity does not match active session");
            }
        }
    }

    private final void validateNotAppThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("This method can not be called from the main application thread");
        }
    }

    public UiAutomation getUiAutomation() {
        return getUiAutomation(0);
    }

    public UiAutomation getUiAutomation(int i) {
        UiAutomation uiAutomation = this.mUiAutomation;
        boolean z = uiAutomation == null || uiAutomation.isDestroyed();
        if (this.mUiAutomationConnection == null) {
            return null;
        }
        if (!z && this.mUiAutomation.getFlags() == i) {
            return this.mUiAutomation;
        }
        if (z) {
            this.mUiAutomation = new UiAutomation(getTargetContext().getMainLooper(), this.mUiAutomationConnection);
        } else {
            this.mUiAutomation.disconnect();
        }
        this.mUiAutomation.connect(i);
        return this.mUiAutomation;
    }

    public TestLooperManager acquireLooperManager(Looper looper) {
        checkInstrumenting("acquireLooperManager");
        return new TestLooperManager(looper);
    }

    private final class InstrumentationThread extends Thread {
        public InstrumentationThread(String str) {
            super(str);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Process.setThreadPriority(-8);
            } catch (RuntimeException e) {
                Log.w(Instrumentation.TAG, "Exception setting priority of instrumentation thread " + Process.myTid(), e);
            }
            if (Instrumentation.this.mAutomaticPerformanceSnapshots) {
                Instrumentation.this.startPerformanceSnapshot();
            }
            Instrumentation.this.onStart();
        }
    }

    private static final class EmptyRunnable implements Runnable {
        @Override // java.lang.Runnable
        public void run() {
        }

        private EmptyRunnable() {
        }
    }

    private static final class SyncRunnable implements Runnable {
        private boolean mComplete;
        private final Runnable mTarget;

        public SyncRunnable(Runnable runnable) {
            this.mTarget = runnable;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mTarget.run();
            synchronized (this) {
                this.mComplete = true;
                notifyAll();
            }
        }

        public void waitForComplete() {
            synchronized (this) {
                while (!this.mComplete) {
                    try {
                        wait();
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
    }

    private static final class ActivityWaiter {
        public Activity activity;
        public final Intent intent;

        public ActivityWaiter(Intent intent) {
            this.intent = intent;
        }
    }

    private final class ActivityGoing implements MessageQueue.IdleHandler {
        private final ActivityWaiter mWaiter;

        public ActivityGoing(ActivityWaiter activityWaiter) {
            this.mWaiter = activityWaiter;
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            synchronized (Instrumentation.this.mSync) {
                Instrumentation.this.mWaitingActivities.remove(this.mWaiter);
                Instrumentation.this.mSync.notifyAll();
            }
            return false;
        }
    }

    private static final class Idler implements MessageQueue.IdleHandler {
        private final Runnable mCallback;
        private boolean mIdle = false;

        public Idler(Runnable runnable) {
            this.mCallback = runnable;
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            Runnable runnable = this.mCallback;
            if (runnable != null) {
                runnable.run();
            }
            synchronized (this) {
                this.mIdle = true;
                notifyAll();
            }
            return false;
        }

        public void waitForIdle() {
            synchronized (this) {
                while (!this.mIdle) {
                    try {
                        wait();
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
    }
}
