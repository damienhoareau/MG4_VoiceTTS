package android.app;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.app.backup.BackupAgent;
import android.app.job.JobInfo;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ActivityRelaunchItem;
import android.app.servertransaction.ActivityResultItem;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.PendingTransactionActions;
import android.app.servertransaction.TransactionExecutor;
import android.app.servertransaction.TransactionExecutorHelper;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDebug;
import android.ddm.DdmHandleAppName;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.hardware.display.DisplayManagerGlobal;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.GraphicsEnvironment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.FontsContract;
import android.provider.Settings;
import android.renderscript.RenderScriptCacheDir;
import android.security.NetworkSecurityPolicy;
import android.security.net.config.NetworkSecurityConfigProvider;
import android.service.notification.ZenModeConfig;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.MergedConfiguration;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.SuperNotCalledException;
import android.util.proto.ProtoOutputStream;
import android.view.Choreographer;
import android.view.ContextThemeWrapper;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.webkit.WebView;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.function.pooled.PooledRunnable;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.android.org.conscrypt.TrustedCertificateStore;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.CloseGuard;
import dalvik.system.VMDebug;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import libcore.io.DropBox;
import libcore.io.EventLogger;
import libcore.io.IoUtils;
import libcore.net.event.NetworkEventDispatcher;
import org.apache.harmony.dalvik.ddmc.DdmVmInternal;

/* JADX INFO: loaded from: classes.dex */
public final class ActivityThread extends ClientTransactionHandler {
    private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 4;
    private static final boolean DEBUG_BACKUP = false;
    public static final boolean DEBUG_BROADCAST = false;
    public static final boolean DEBUG_CONFIGURATION = false;
    public static final boolean DEBUG_MEMORY_TRIM = false;
    static final boolean DEBUG_MESSAGES = false;
    public static final boolean DEBUG_ORDER = false;
    private static final boolean DEBUG_PROVIDER = false;
    private static final boolean DEBUG_RESULTS = false;
    private static final boolean DEBUG_SERVICE = false;
    private static final String HEAP_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s";
    private static final String HEAP_FULL_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s";
    public static final long INVALID_PROC_STATE_SEQ = -1;
    private static final long MIN_TIME_BETWEEN_GCS = 5000;
    private static final String ONE_COUNT_COLUMN = "%21s %8d";
    private static final String ONE_COUNT_COLUMN_HEADER = "%21s %8s";
    public static final String PROC_START_SEQ_IDENT = "seq=";
    private static final boolean REPORT_TO_ACTIVITY = true;
    public static final int SERVICE_DONE_EXECUTING_ANON = 0;
    public static final int SERVICE_DONE_EXECUTING_START = 1;
    public static final int SERVICE_DONE_EXECUTING_STOP = 2;
    private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
    public static final String TAG = "ActivityThread";
    private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
    static final boolean localLOGV = false;
    private static volatile ActivityThread sCurrentActivityThread;
    static volatile Handler sMainThreadHandler;
    static volatile IPackageManager sPackageManager;
    AppBindData mBoundApplication;
    Configuration mCompatConfiguration;
    Configuration mConfiguration;
    int mCurDefaultDisplayDpi;
    boolean mDensityCompatMode;
    Application mInitialApplication;
    Instrumentation mInstrumentation;
    private int mLastSessionId;
    Profiler mProfiler;
    private ContextImpl mSystemContext;
    private ContextImpl mSystemUiContext;
    private static final Bitmap.Config THUMBNAIL_FORMAT = Bitmap.Config.RGB_565;
    private static final ThreadLocal<Intent> sCurrentBroadcastIntent = new ThreadLocal<>();
    private final Object mNetworkPolicyLock = new Object();
    private long mNetworkBlockSeq = -1;
    final ApplicationThread mAppThread = new ApplicationThread();
    final Looper mLooper = Looper.myLooper();
    final H mH = new H();
    final Executor mExecutor = new HandlerExecutor(this.mH);
    final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
    ActivityClientRecord mNewActivities = null;
    int mNumVisibleActivities = 0;
    ArrayList<WeakReference<AssistStructure>> mLastAssistStructures = new ArrayList<>();
    final ArrayMap<IBinder, Service> mServices = new ArrayMap<>();
    final ArrayList<Application> mAllApplications = new ArrayList<>();
    final ArrayMap<String, BackupAgent> mBackupAgents = new ArrayMap<>();
    String mInstrumentationPackageName = null;
    String mInstrumentationAppDir = null;
    String[] mInstrumentationSplitAppDirs = null;
    String mInstrumentationLibDir = null;
    String mInstrumentedAppDir = null;
    String[] mInstrumentedSplitAppDirs = null;
    String mInstrumentedLibDir = null;
    boolean mSystemThread = false;
    boolean mJitEnabled = false;
    boolean mSomeActivitiesChanged = false;
    boolean mUpdatingSystemConfig = false;
    boolean mHiddenApiWarningShown = false;
    final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
    final ArrayMap<String, WeakReference<LoadedApk>> mResourcePackages = new ArrayMap<>();
    final ArrayList<ActivityClientRecord> mRelaunchingActivities = new ArrayList<>();
    Configuration mPendingConfiguration = null;
    private final TransactionExecutor mTransactionExecutor = new TransactionExecutor(this);
    final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderRefCount> mProviderRefCountMap = new ArrayMap<>();
    final ArrayMap<IBinder, ProviderClientRecord> mLocalProviders = new ArrayMap<>();
    final ArrayMap<ComponentName, ProviderClientRecord> mLocalProvidersByName = new ArrayMap<>();
    final ArrayMap<ProviderKey, Object> mGetProviderLocks = new ArrayMap<>();
    final ArrayMap<Activity, ArrayList<OnActivityPausedListener>> mOnPauseListeners = new ArrayMap<>();
    final GcIdler mGcIdler = new GcIdler();
    boolean mGcIdlerScheduled = false;
    Bundle mCoreSettings = null;
    private Configuration mMainThreadConfig = new Configuration();
    private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nDumpGraphicsInfo(FileDescriptor fileDescriptor);

    private static final class ProviderKey {
        final String authority;
        final int userId;

        public ProviderKey(String str, int i) {
            this.authority = str;
            this.userId = i;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ProviderKey)) {
                return false;
            }
            ProviderKey providerKey = (ProviderKey) obj;
            return Objects.equals(this.authority, providerKey.authority) && this.userId == providerKey.userId;
        }

        public int hashCode() {
            String str = this.authority;
            return (str != null ? str.hashCode() : 0) ^ this.userId;
        }
    }

    public static final class ActivityClientRecord {
        Activity activity;
        ActivityInfo activityInfo;
        CompatibilityInfo compatInfo;
        ViewRootImpl.ActivityConfigCallback configCallback;
        Configuration createdConfig;
        String embeddedID;
        boolean hideForNow;
        int ident;
        Intent intent;
        public final boolean isForward;
        Activity.NonConfigurationInstances lastNonConfigurationInstances;
        private int mLifecycleState;
        Window mPendingRemoveWindow;
        WindowManager mPendingRemoveWindowManager;
        boolean mPreserveWindow;
        Configuration newConfig;
        ActivityClientRecord nextIdle;
        Configuration overrideConfig;
        public LoadedApk packageInfo;
        Activity parent;
        boolean paused;
        int pendingConfigChanges;
        List<ReferrerIntent> pendingIntents;
        List<ResultInfo> pendingResults;
        PersistableBundle persistentState;
        ProfilerInfo profilerInfo;
        String referrer;
        boolean startsNotResumed;
        Bundle state;
        boolean stopped;
        private Configuration tmpConfig;
        public IBinder token;
        IVoiceInteractor voiceInteractor;
        Window window;

        public ActivityClientRecord() {
            this.tmpConfig = new Configuration();
            this.mLifecycleState = 0;
            this.isForward = false;
            init();
        }

        public ActivityClientRecord(IBinder iBinder, Intent intent, int i, ActivityInfo activityInfo, Configuration configuration, CompatibilityInfo compatibilityInfo, String str, IVoiceInteractor iVoiceInteractor, Bundle bundle, PersistableBundle persistableBundle, List<ResultInfo> list, List<ReferrerIntent> list2, boolean z, ProfilerInfo profilerInfo, ClientTransactionHandler clientTransactionHandler) {
            this.tmpConfig = new Configuration();
            this.mLifecycleState = 0;
            this.token = iBinder;
            this.ident = i;
            this.intent = intent;
            this.referrer = str;
            this.voiceInteractor = iVoiceInteractor;
            this.activityInfo = activityInfo;
            this.compatInfo = compatibilityInfo;
            this.state = bundle;
            this.persistentState = persistableBundle;
            this.pendingResults = list;
            this.pendingIntents = list2;
            this.isForward = z;
            this.profilerInfo = profilerInfo;
            this.overrideConfig = configuration;
            this.packageInfo = clientTransactionHandler.getPackageInfoNoCheck(activityInfo.applicationInfo, compatibilityInfo);
            init();
        }

        private void init() {
            this.parent = null;
            this.embeddedID = null;
            this.paused = false;
            this.stopped = false;
            this.hideForNow = false;
            this.nextIdle = null;
            this.configCallback = new ViewRootImpl.ActivityConfigCallback() { // from class: android.app.-$$Lambda$ActivityThread$ActivityClientRecord$HOrG1qglSjSUHSjKBn2rXtX0gGg
                @Override // android.view.ViewRootImpl.ActivityConfigCallback
                public final void onConfigurationChanged(Configuration configuration, int i) {
                    this.f$0.lambda$init$0$ActivityThread$ActivityClientRecord(configuration, i);
                }
            };
        }

        public /* synthetic */ void lambda$init$0$ActivityThread$ActivityClientRecord(Configuration configuration, int i) {
            Activity activity = this.activity;
            if (activity == null) {
                throw new IllegalStateException("Received config update for non-existing activity");
            }
            activity.mMainThread.handleActivityConfigurationChanged(this.token, configuration, i);
        }

        public int getLifecycleState() {
            return this.mLifecycleState;
        }

        public void setState(int i) {
            this.mLifecycleState = i;
            if (i == 1) {
                this.paused = true;
                this.stopped = true;
                return;
            }
            if (i == 2) {
                this.paused = true;
                this.stopped = false;
                return;
            }
            if (i == 3) {
                this.paused = false;
                this.stopped = false;
            } else if (i == 4) {
                this.paused = true;
                this.stopped = false;
            } else {
                if (i != 5) {
                    return;
                }
                this.paused = true;
                this.stopped = true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isPreHoneycomb() {
            Activity activity = this.activity;
            return activity != null && activity.getApplicationInfo().targetSdkVersion < 11;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isPreP() {
            Activity activity = this.activity;
            return activity != null && activity.getApplicationInfo().targetSdkVersion < 28;
        }

        public boolean isPersistable() {
            return this.activityInfo.persistableMode == 2;
        }

        public boolean isVisibleFromServer() {
            Activity activity = this.activity;
            return activity != null && activity.mVisibleFromServer;
        }

        public String toString() {
            Intent intent = this.intent;
            ComponentName component = intent != null ? intent.getComponent() : null;
            StringBuilder sb = new StringBuilder();
            sb.append("ActivityRecord{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" token=");
            sb.append(this.token);
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            sb.append(component == null ? "no component name" : component.toShortString());
            sb.append("}");
            return sb.toString();
        }

        public String getStateString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ActivityClientRecord{");
            sb.append("paused=");
            sb.append(this.paused);
            sb.append(", stopped=");
            sb.append(this.stopped);
            sb.append(", hideForNow=");
            sb.append(this.hideForNow);
            sb.append(", startsNotResumed=");
            sb.append(this.startsNotResumed);
            sb.append(", isForward=");
            sb.append(this.isForward);
            sb.append(", pendingConfigChanges=");
            sb.append(this.pendingConfigChanges);
            sb.append(", preserveWindow=");
            sb.append(this.mPreserveWindow);
            if (this.activity != null) {
                sb.append(", Activity{");
                sb.append("resumed=");
                sb.append(this.activity.mResumed);
                sb.append(", stopped=");
                sb.append(this.activity.mStopped);
                sb.append(", finished=");
                sb.append(this.activity.isFinishing());
                sb.append(", destroyed=");
                sb.append(this.activity.isDestroyed());
                sb.append(", startedActivity=");
                sb.append(this.activity.mStartedActivity);
                sb.append(", temporaryPause=");
                sb.append(this.activity.mTemporaryPause);
                sb.append(", changingConfigurations=");
                sb.append(this.activity.mChangingConfigurations);
                sb.append("}");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    final class ProviderClientRecord {
        final ContentProviderHolder mHolder;
        final ContentProvider mLocalProvider;
        final String[] mNames;
        final IContentProvider mProvider;

        ProviderClientRecord(String[] strArr, IContentProvider iContentProvider, ContentProvider contentProvider, ContentProviderHolder contentProviderHolder) {
            this.mNames = strArr;
            this.mProvider = iContentProvider;
            this.mLocalProvider = contentProvider;
            this.mHolder = contentProviderHolder;
        }
    }

    static final class ReceiverData extends BroadcastReceiver.PendingResult {
        CompatibilityInfo compatInfo;
        ActivityInfo info;
        Intent intent;

        public ReceiverData(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, IBinder iBinder, int i2) {
            super(i, str, bundle, 0, z, z2, iBinder, i2, intent.getFlags());
            this.intent = intent;
        }

        public String toString() {
            return "ReceiverData{intent=" + this.intent + " packageName=" + this.info.packageName + " resultCode=" + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(false) + "}";
        }
    }

    static final class CreateBackupAgentData {
        ApplicationInfo appInfo;
        int backupMode;
        CompatibilityInfo compatInfo;

        CreateBackupAgentData() {
        }

        public String toString() {
            return "CreateBackupAgentData{appInfo=" + this.appInfo + " backupAgent=" + this.appInfo.backupAgentName + " mode=" + this.backupMode + "}";
        }
    }

    static final class CreateServiceData {
        CompatibilityInfo compatInfo;
        ServiceInfo info;
        Intent intent;
        IBinder token;

        CreateServiceData() {
        }

        public String toString() {
            return "CreateServiceData{token=" + this.token + " className=" + this.info.name + " packageName=" + this.info.packageName + " intent=" + this.intent + "}";
        }
    }

    static final class BindServiceData {
        Intent intent;
        boolean rebind;
        IBinder token;

        BindServiceData() {
        }

        public String toString() {
            return "BindServiceData{token=" + this.token + " intent=" + this.intent + "}";
        }
    }

    static final class ServiceArgsData {
        Intent args;
        int flags;
        int startId;
        boolean taskRemoved;
        IBinder token;

        ServiceArgsData() {
        }

        public String toString() {
            return "ServiceArgsData{token=" + this.token + " startId=" + this.startId + " args=" + this.args + "}";
        }
    }

    static final class AppBindData {
        ApplicationInfo appInfo;
        boolean autofillCompatibilityEnabled;
        String buildSerial;
        CompatibilityInfo compatInfo;
        Configuration config;
        int debugMode;
        boolean enableBinderTracking;
        LoadedApk info;
        ProfilerInfo initProfilerInfo;
        Bundle instrumentationArgs;
        ComponentName instrumentationName;
        IUiAutomationConnection instrumentationUiAutomationConnection;
        IInstrumentationWatcher instrumentationWatcher;
        boolean persistent;
        String processName;
        List<ProviderInfo> providers;
        boolean restrictedBackupMode;
        boolean trackAllocation;

        AppBindData() {
        }

        public String toString() {
            return "AppBindData{appInfo=" + this.appInfo + "}";
        }
    }

    static final class Profiler {
        boolean autoStopProfiler;
        boolean handlingProfiling;
        ParcelFileDescriptor profileFd;
        String profileFile;
        boolean profiling;
        int samplingInterval;
        boolean streamingOutput;

        Profiler() {
        }

        public void setProfiler(ProfilerInfo profilerInfo) {
            ParcelFileDescriptor parcelFileDescriptor = profilerInfo.profileFd;
            if (this.profiling) {
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                        return;
                    } catch (IOException unused) {
                        return;
                    }
                }
                return;
            }
            ParcelFileDescriptor parcelFileDescriptor2 = this.profileFd;
            if (parcelFileDescriptor2 != null) {
                try {
                    parcelFileDescriptor2.close();
                } catch (IOException unused2) {
                }
            }
            this.profileFile = profilerInfo.profileFile;
            this.profileFd = parcelFileDescriptor;
            this.samplingInterval = profilerInfo.samplingInterval;
            this.autoStopProfiler = profilerInfo.autoStopProfiler;
            this.streamingOutput = profilerInfo.streamingOutput;
        }

        public void startProfiling() {
            if (this.profileFd == null || this.profiling) {
                return;
            }
            try {
                VMDebug.startMethodTracing(this.profileFile, this.profileFd.getFileDescriptor(), SystemProperties.getInt("debug.traceview-buffer-size-mb", 8) * 1024 * 1024, 0, this.samplingInterval != 0, this.samplingInterval, this.streamingOutput);
                this.profiling = true;
            } catch (RuntimeException e) {
                Slog.w(ActivityThread.TAG, "Profiling failed on path " + this.profileFile, e);
                try {
                    this.profileFd.close();
                    this.profileFd = null;
                } catch (IOException e2) {
                    Slog.w(ActivityThread.TAG, "Failure closing profile fd", e2);
                }
            }
        }

        public void stopProfiling() {
            if (this.profiling) {
                this.profiling = false;
                Debug.stopMethodTracing();
                ParcelFileDescriptor parcelFileDescriptor = this.profileFd;
                if (parcelFileDescriptor != null) {
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException unused) {
                    }
                }
                this.profileFd = null;
                this.profileFile = null;
            }
        }
    }

    static final class DumpComponentInfo {
        String[] args;
        ParcelFileDescriptor fd;
        String prefix;
        IBinder token;

        DumpComponentInfo() {
        }
    }

    static final class ContextCleanupInfo {
        ContextImpl context;
        String what;
        String who;

        ContextCleanupInfo() {
        }
    }

    static final class DumpHeapData {
        ParcelFileDescriptor fd;
        public boolean mallocInfo;
        public boolean managed;
        String path;
        public boolean runGc;

        DumpHeapData() {
        }
    }

    static final class UpdateCompatibilityData {
        CompatibilityInfo info;
        String pkg;

        UpdateCompatibilityData() {
        }
    }

    static final class RequestAssistContextExtras {
        IBinder activityToken;
        int flags;
        IBinder requestToken;
        int requestType;
        int sessionId;

        RequestAssistContextExtras() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ApplicationThread extends IApplicationThread.Stub {
        private static final String DB_INFO_FORMAT = "  %8s %8s %14s %14s  %s";
        private int mLastProcessState;

        private ApplicationThread() {
            this.mLastProcessState = -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updatePendingConfiguration(Configuration configuration) {
            synchronized (ActivityThread.this.mResourcesManager) {
                if (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(configuration)) {
                    ActivityThread.this.mPendingConfiguration = configuration;
                }
            }
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSleeping(IBinder iBinder, boolean z) {
            ActivityThread.this.sendMessage(137, iBinder, z ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleReceiver(Intent intent, ActivityInfo activityInfo, CompatibilityInfo compatibilityInfo, int i, String str, Bundle bundle, boolean z, int i2, int i3) {
            updateProcessState(i3, false);
            ReceiverData receiverData = new ReceiverData(intent, i, str, bundle, z, false, ActivityThread.this.mAppThread.asBinder(), i2);
            receiverData.info = activityInfo;
            receiverData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(113, receiverData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateBackupAgent(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, int i) {
            CreateBackupAgentData createBackupAgentData = new CreateBackupAgentData();
            createBackupAgentData.appInfo = applicationInfo;
            createBackupAgentData.compatInfo = compatibilityInfo;
            createBackupAgentData.backupMode = i;
            ActivityThread.this.sendMessage(128, createBackupAgentData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleDestroyBackupAgent(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
            CreateBackupAgentData createBackupAgentData = new CreateBackupAgentData();
            createBackupAgentData.appInfo = applicationInfo;
            createBackupAgentData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(129, createBackupAgentData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleCreateService(IBinder iBinder, ServiceInfo serviceInfo, CompatibilityInfo compatibilityInfo, int i) {
            updateProcessState(i, false);
            CreateServiceData createServiceData = new CreateServiceData();
            createServiceData.token = iBinder;
            createServiceData.info = serviceInfo;
            createServiceData.compatInfo = compatibilityInfo;
            ActivityThread.this.sendMessage(114, createServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleBindService(IBinder iBinder, Intent intent, boolean z, int i) {
            updateProcessState(i, false);
            BindServiceData bindServiceData = new BindServiceData();
            bindServiceData.token = iBinder;
            bindServiceData.intent = intent;
            bindServiceData.rebind = z;
            ActivityThread.this.sendMessage(121, bindServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleUnbindService(IBinder iBinder, Intent intent) {
            BindServiceData bindServiceData = new BindServiceData();
            bindServiceData.token = iBinder;
            bindServiceData.intent = intent;
            ActivityThread.this.sendMessage(122, bindServiceData);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleServiceArgs(IBinder iBinder, ParceledListSlice parceledListSlice) {
            List list = parceledListSlice.getList();
            for (int i = 0; i < list.size(); i++) {
                ServiceStartArgs serviceStartArgs = (ServiceStartArgs) list.get(i);
                ServiceArgsData serviceArgsData = new ServiceArgsData();
                serviceArgsData.token = iBinder;
                serviceArgsData.taskRemoved = serviceStartArgs.taskRemoved;
                serviceArgsData.startId = serviceStartArgs.startId;
                serviceArgsData.flags = serviceStartArgs.flags;
                serviceArgsData.args = serviceStartArgs.args;
                ActivityThread.this.sendMessage(115, serviceArgsData);
            }
        }

        @Override // android.app.IApplicationThread
        public final void scheduleStopService(IBinder iBinder) {
            ActivityThread.this.sendMessage(116, iBinder);
        }

        @Override // android.app.IApplicationThread
        public final void bindApplication(String str, ApplicationInfo applicationInfo, List<ProviderInfo> list, ComponentName componentName, ProfilerInfo profilerInfo, Bundle bundle, IInstrumentationWatcher iInstrumentationWatcher, IUiAutomationConnection iUiAutomationConnection, int i, boolean z, boolean z2, boolean z3, boolean z4, Configuration configuration, CompatibilityInfo compatibilityInfo, Map map, Bundle bundle2, String str2, boolean z5) {
            if (map != null) {
                ServiceManager.initServiceCache(map);
            }
            setCoreSettings(bundle2);
            AppBindData appBindData = new AppBindData();
            appBindData.processName = str;
            appBindData.appInfo = applicationInfo;
            appBindData.providers = list;
            appBindData.instrumentationName = componentName;
            appBindData.instrumentationArgs = bundle;
            appBindData.instrumentationWatcher = iInstrumentationWatcher;
            appBindData.instrumentationUiAutomationConnection = iUiAutomationConnection;
            appBindData.debugMode = i;
            appBindData.enableBinderTracking = z;
            appBindData.trackAllocation = z2;
            appBindData.restrictedBackupMode = z3;
            appBindData.persistent = z4;
            appBindData.config = configuration;
            appBindData.compatInfo = compatibilityInfo;
            appBindData.initProfilerInfo = profilerInfo;
            appBindData.buildSerial = str2;
            appBindData.autofillCompatibilityEnabled = z5;
            ActivityThread.this.sendMessage(110, appBindData);
        }

        @Override // android.app.IApplicationThread
        public final void runIsolatedEntryPoint(String str, String[] strArr) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = strArr;
            ActivityThread.this.sendMessage(158, someArgsObtain);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleExit() {
            ActivityThread.this.sendMessage(111, null);
        }

        @Override // android.app.IApplicationThread
        public final void scheduleSuicide() {
            ActivityThread.this.sendMessage(130, null);
        }

        @Override // android.app.IApplicationThread
        public void scheduleApplicationInfoChanged(ApplicationInfo applicationInfo) {
            ActivityThread.this.sendMessage(156, applicationInfo);
        }

        @Override // android.app.IApplicationThread
        public void updateTimeZone() {
            TimeZone.setDefault(null);
        }

        @Override // android.app.IApplicationThread
        public void clearDnsCache() {
            InetAddress.clearDnsCache();
            NetworkEventDispatcher.getInstance().onNetworkConfigurationChanged();
        }

        @Override // android.app.IApplicationThread
        public void setHttpProxy(String str, String str2, String str3, Uri uri) {
            ConnectivityManager connectivityManagerFrom = ConnectivityManager.from(ActivityThread.this.getApplication() != null ? ActivityThread.this.getApplication() : ActivityThread.this.getSystemContext());
            if (connectivityManagerFrom.getBoundNetworkForProcess() != null) {
                Proxy.setHttpProxySystemProperty(connectivityManagerFrom.getDefaultProxy());
            } else {
                Proxy.setHttpProxySystemProperty(str, str2, str3, uri);
            }
        }

        @Override // android.app.IApplicationThread
        public void processInBackground() {
            ActivityThread.this.mH.removeMessages(120);
            ActivityThread.this.mH.sendMessage(ActivityThread.this.mH.obtainMessage(120));
        }

        @Override // android.app.IApplicationThread
        public void dumpService(ParcelFileDescriptor parcelFileDescriptor, IBinder iBinder, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                try {
                    dumpComponentInfo.fd = parcelFileDescriptor.dup();
                    dumpComponentInfo.token = iBinder;
                    dumpComponentInfo.args = strArr;
                    ActivityThread.this.sendMessage(123, (Object) dumpComponentInfo, 0, 0, true);
                } catch (IOException e) {
                    Slog.w(ActivityThread.TAG, "dumpService failed", e);
                }
            } finally {
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleRegisteredReceiver(IIntentReceiver iIntentReceiver, Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2, int i3) throws RemoteException {
            updateProcessState(i3, false);
            iIntentReceiver.performReceive(intent, i, str, bundle, z, z2, i2);
        }

        @Override // android.app.IApplicationThread
        public void scheduleLowMemory() {
            ActivityThread.this.sendMessage(124, null);
        }

        @Override // android.app.IApplicationThread
        public void profilerControl(boolean z, ProfilerInfo profilerInfo, int i) {
            ActivityThread.this.sendMessage(127, profilerInfo, z ? 1 : 0, i);
        }

        @Override // android.app.IApplicationThread
        public void dumpHeap(boolean z, boolean z2, boolean z3, String str, ParcelFileDescriptor parcelFileDescriptor) {
            DumpHeapData dumpHeapData = new DumpHeapData();
            dumpHeapData.managed = z;
            dumpHeapData.mallocInfo = z2;
            dumpHeapData.runGc = z3;
            dumpHeapData.path = str;
            dumpHeapData.fd = parcelFileDescriptor;
            ActivityThread.this.sendMessage(135, (Object) dumpHeapData, 0, 0, true);
        }

        @Override // android.app.IApplicationThread
        public void attachAgent(String str) {
            ActivityThread.this.sendMessage(155, str);
        }

        @Override // android.app.IApplicationThread
        public void setSchedulingGroup(int i) {
            try {
                Process.setProcessGroup(Process.myPid(), i);
            } catch (Exception e) {
                Slog.w(ActivityThread.TAG, "Failed setting process group to " + i, e);
            }
        }

        @Override // android.app.IApplicationThread
        public void dispatchPackageBroadcast(int i, String[] strArr) {
            ActivityThread.this.sendMessage(133, strArr, i);
        }

        @Override // android.app.IApplicationThread
        public void scheduleCrash(String str) {
            ActivityThread.this.sendMessage(134, str);
        }

        @Override // android.app.IApplicationThread
        public void dumpActivity(ParcelFileDescriptor parcelFileDescriptor, IBinder iBinder, String str, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                try {
                    dumpComponentInfo.fd = parcelFileDescriptor.dup();
                    dumpComponentInfo.token = iBinder;
                    dumpComponentInfo.prefix = str;
                    dumpComponentInfo.args = strArr;
                    ActivityThread.this.sendMessage(136, (Object) dumpComponentInfo, 0, 0, true);
                } catch (IOException e) {
                    Slog.w(ActivityThread.TAG, "dumpActivity failed", e);
                }
            } finally {
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpProvider(ParcelFileDescriptor parcelFileDescriptor, IBinder iBinder, String[] strArr) {
            DumpComponentInfo dumpComponentInfo = new DumpComponentInfo();
            try {
                try {
                    dumpComponentInfo.fd = parcelFileDescriptor.dup();
                    dumpComponentInfo.token = iBinder;
                    dumpComponentInfo.args = strArr;
                    ActivityThread.this.sendMessage(141, (Object) dumpComponentInfo, 0, 0, true);
                } catch (IOException e) {
                    Slog.w(ActivityThread.TAG, "dumpProvider failed", e);
                }
            } finally {
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpMemInfo(ParcelFileDescriptor parcelFileDescriptor, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, String[] strArr) {
            FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
            try {
                dumpMemInfo(fastPrintWriter, memoryInfo, z, z2, z3, z4, z5);
            } finally {
                fastPrintWriter.flush();
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        private void dumpMemInfo(PrintWriter printWriter, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            long nativeHeapSize = Debug.getNativeHeapSize() / 1024;
            long nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize() / 1024;
            long nativeHeapFreeSize = Debug.getNativeHeapFreeSize() / 1024;
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long j = runtime.totalMemory() / 1024;
            long jFreeMemory = runtime.freeMemory() / 1024;
            long j2 = j - jFreeMemory;
            long[] jArrCountInstancesOfClasses = VMDebug.countInstancesOfClasses(new Class[]{ContextImpl.class, Activity.class, WebView.class, OpenSSLSocketImpl.class}, true);
            long j3 = jArrCountInstancesOfClasses[0];
            long j4 = jArrCountInstancesOfClasses[1];
            long j5 = jArrCountInstancesOfClasses[2];
            long j6 = jArrCountInstancesOfClasses[3];
            long viewInstanceCount = ViewDebug.getViewInstanceCount();
            long viewRootImplCount = ViewDebug.getViewRootImplCount();
            int globalAssetCount = AssetManager.getGlobalAssetCount();
            int globalAssetManagerCount = AssetManager.getGlobalAssetManagerCount();
            int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
            int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
            int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
            long globalAllocSize = Parcel.getGlobalAllocSize();
            long globalAllocCount = Parcel.getGlobalAllocCount();
            SQLiteDebug.PagerStats databaseInfo = SQLiteDebug.getDatabaseInfo();
            ActivityThread.dumpMemInfoTable(printWriter, memoryInfo, z, z2, z3, z4, Process.myPid(), ActivityThread.this.mBoundApplication != null ? ActivityThread.this.mBoundApplication.processName : "unknown", nativeHeapSize, nativeHeapAllocatedSize, nativeHeapFreeSize, j, j2, jFreeMemory);
            if (z) {
                printWriter.print(viewInstanceCount);
                printWriter.print(',');
                printWriter.print(viewRootImplCount);
                printWriter.print(',');
                printWriter.print(j3);
                printWriter.print(',');
                printWriter.print(j4);
                printWriter.print(',');
                printWriter.print(globalAssetCount);
                printWriter.print(',');
                printWriter.print(globalAssetManagerCount);
                printWriter.print(',');
                printWriter.print(binderLocalObjectCount);
                printWriter.print(',');
                printWriter.print(binderProxyObjectCount);
                printWriter.print(',');
                printWriter.print(binderDeathObjectCount);
                printWriter.print(',');
                printWriter.print(j6);
                printWriter.print(',');
                printWriter.print(databaseInfo.memoryUsed / 1024);
                printWriter.print(',');
                printWriter.print(databaseInfo.memoryUsed / 1024);
                printWriter.print(',');
                printWriter.print(databaseInfo.pageCacheOverflow / 1024);
                printWriter.print(',');
                printWriter.print(databaseInfo.largestMemAlloc / 1024);
                for (int i = 0; i < databaseInfo.dbStats.size(); i++) {
                    SQLiteDebug.DbStats dbStats = databaseInfo.dbStats.get(i);
                    printWriter.print(',');
                    printWriter.print(dbStats.dbName);
                    printWriter.print(',');
                    printWriter.print(dbStats.pageSize);
                    printWriter.print(',');
                    printWriter.print(dbStats.dbSize);
                    printWriter.print(',');
                    printWriter.print(dbStats.lookaside);
                    printWriter.print(',');
                    printWriter.print(dbStats.cache);
                    printWriter.print(',');
                    printWriter.print(dbStats.cache);
                }
                printWriter.println();
                return;
            }
            printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            printWriter.println(" Objects");
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "Views:", Long.valueOf(viewInstanceCount), "ViewRootImpl:", Long.valueOf(viewRootImplCount));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "AppContexts:", Long.valueOf(j3), "Activities:", Long.valueOf(j4));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "Assets:", Integer.valueOf(globalAssetCount), "AssetManagers:", Integer.valueOf(globalAssetManagerCount));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "Local Binders:", Integer.valueOf(binderLocalObjectCount), "Proxy Binders:", Integer.valueOf(binderProxyObjectCount));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "Parcel memory:", Long.valueOf(globalAllocSize / 1024), "Parcel count:", Long.valueOf(globalAllocCount));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "Death Recipients:", Integer.valueOf(binderDeathObjectCount), "OpenSSL Sockets:", Long.valueOf(j6));
            ActivityThread.printRow(printWriter, ActivityThread.ONE_COUNT_COLUMN, "WebViews:", Long.valueOf(j5));
            printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            printWriter.println(" SQL");
            ActivityThread.printRow(printWriter, ActivityThread.ONE_COUNT_COLUMN, "MEMORY_USED:", Integer.valueOf(databaseInfo.memoryUsed / 1024));
            ActivityThread.printRow(printWriter, ActivityThread.TWO_COUNT_COLUMNS, "PAGECACHE_OVERFLOW:", Integer.valueOf(databaseInfo.pageCacheOverflow / 1024), "MALLOC_SIZE:", Integer.valueOf(databaseInfo.largestMemAlloc / 1024));
            printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            int size = databaseInfo.dbStats.size();
            if (size > 0) {
                printWriter.println(" DATABASES");
                ActivityThread.printRow(printWriter, DB_INFO_FORMAT, "pgsz", "dbsz", "Lookaside(b)", "cache", "Dbname");
                for (int i2 = 0; i2 < size; i2++) {
                    SQLiteDebug.DbStats dbStats2 = databaseInfo.dbStats.get(i2);
                    Object[] objArr = new Object[5];
                    objArr[0] = dbStats2.pageSize > 0 ? String.valueOf(dbStats2.pageSize) : WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    objArr[1] = dbStats2.dbSize > 0 ? String.valueOf(dbStats2.dbSize) : WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    objArr[2] = dbStats2.lookaside > 0 ? String.valueOf(dbStats2.lookaside) : WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    objArr[3] = dbStats2.cache;
                    objArr[4] = dbStats2.dbName;
                    ActivityThread.printRow(printWriter, DB_INFO_FORMAT, objArr);
                }
            }
            String assetAllocations = AssetManager.getAssetAllocations();
            if (assetAllocations != null) {
                printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                printWriter.println(" Asset Allocations");
                printWriter.print(assetAllocations);
            }
            if (z5) {
                boolean z6 = !(ActivityThread.this.mBoundApplication == null || (2 & ActivityThread.this.mBoundApplication.appInfo.flags) == 0) || Build.IS_DEBUGGABLE;
                printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                printWriter.println(" Unreachable memory");
                printWriter.print(Debug.getUnreachableMemory(100, z6));
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpMemInfoProto(ParcelFileDescriptor parcelFileDescriptor, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, boolean z4, String[] strArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream(parcelFileDescriptor.getFileDescriptor());
            try {
                dumpMemInfo(protoOutputStream, memoryInfo, z, z2, z3, z4);
            } finally {
                protoOutputStream.flush();
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        private void dumpMemInfo(ProtoOutputStream protoOutputStream, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, boolean z4) {
            long nativeHeapSize = Debug.getNativeHeapSize() / 1024;
            long nativeHeapAllocatedSize = Debug.getNativeHeapAllocatedSize() / 1024;
            long nativeHeapFreeSize = Debug.getNativeHeapFreeSize() / 1024;
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long j = runtime.totalMemory() / 1024;
            long jFreeMemory = runtime.freeMemory() / 1024;
            long j2 = j - jFreeMemory;
            long[] jArrCountInstancesOfClasses = VMDebug.countInstancesOfClasses(new Class[]{ContextImpl.class, Activity.class, WebView.class, OpenSSLSocketImpl.class}, true);
            long j3 = jArrCountInstancesOfClasses[0];
            long j4 = jArrCountInstancesOfClasses[1];
            long j5 = jArrCountInstancesOfClasses[2];
            long j6 = jArrCountInstancesOfClasses[3];
            long viewInstanceCount = ViewDebug.getViewInstanceCount();
            long viewRootImplCount = ViewDebug.getViewRootImplCount();
            int globalAssetCount = AssetManager.getGlobalAssetCount();
            int globalAssetManagerCount = AssetManager.getGlobalAssetManagerCount();
            int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
            int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
            int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
            long globalAllocSize = Parcel.getGlobalAllocSize();
            long globalAllocCount = Parcel.getGlobalAllocCount();
            SQLiteDebug.PagerStats databaseInfo = SQLiteDebug.getDatabaseInfo();
            long jStart = protoOutputStream.start(1146756268033L);
            protoOutputStream.write(1120986464257L, Process.myPid());
            protoOutputStream.write(1138166333442L, ActivityThread.this.mBoundApplication != null ? ActivityThread.this.mBoundApplication.processName : "unknown");
            ActivityThread.dumpMemInfoTable(protoOutputStream, memoryInfo, z2, z3, nativeHeapSize, nativeHeapAllocatedSize, nativeHeapFreeSize, j, j2, jFreeMemory);
            protoOutputStream.end(jStart);
            long jStart2 = protoOutputStream.start(1146756268034L);
            protoOutputStream.write(1120986464257L, viewInstanceCount);
            long j7 = 1120986464258L;
            protoOutputStream.write(1120986464258L, viewRootImplCount);
            protoOutputStream.write(1120986464259L, j3);
            protoOutputStream.write(1120986464260L, j4);
            protoOutputStream.write(1120986464261L, globalAssetCount);
            protoOutputStream.write(1120986464262L, globalAssetManagerCount);
            protoOutputStream.write(1120986464263L, binderLocalObjectCount);
            protoOutputStream.write(1120986464264L, binderProxyObjectCount);
            protoOutputStream.write(1112396529673L, globalAllocSize / 1024);
            protoOutputStream.write(1120986464266L, globalAllocCount);
            protoOutputStream.write(1120986464267L, binderDeathObjectCount);
            protoOutputStream.write(1120986464268L, j6);
            protoOutputStream.write(1120986464269L, j5);
            protoOutputStream.end(jStart2);
            long jStart3 = protoOutputStream.start(1146756268035L);
            protoOutputStream.write(1120986464257L, databaseInfo.memoryUsed / 1024);
            protoOutputStream.write(1120986464258L, databaseInfo.pageCacheOverflow / 1024);
            protoOutputStream.write(1120986464259L, databaseInfo.largestMemAlloc / 1024);
            int size = databaseInfo.dbStats.size();
            int i = 0;
            while (i < size) {
                SQLiteDebug.DbStats dbStats = databaseInfo.dbStats.get(i);
                long jStart4 = protoOutputStream.start(2246267895812L);
                protoOutputStream.write(1138166333441L, dbStats.dbName);
                protoOutputStream.write(j7, dbStats.pageSize);
                protoOutputStream.write(1120986464259L, dbStats.dbSize);
                protoOutputStream.write(1120986464260L, dbStats.lookaside);
                protoOutputStream.write(1138166333445L, dbStats.cache);
                protoOutputStream.end(jStart4);
                i++;
                j7 = 1120986464258L;
            }
            protoOutputStream.end(jStart3);
            String assetAllocations = AssetManager.getAssetAllocations();
            if (assetAllocations != null) {
                protoOutputStream.write(1138166333444L, assetAllocations);
            }
            if (z4) {
                protoOutputStream.write(1138166333445L, Debug.getUnreachableMemory(100, ((ActivityThread.this.mBoundApplication == null ? 0 : ActivityThread.this.mBoundApplication.appInfo.flags) & 2) != 0 || Build.IS_DEBUGGABLE));
            }
        }

        @Override // android.app.IApplicationThread
        public void dumpGfxInfo(ParcelFileDescriptor parcelFileDescriptor, String[] strArr) {
            ActivityThread.this.nDumpGraphicsInfo(parcelFileDescriptor.getFileDescriptor());
            WindowManagerGlobal.getInstance().dumpGfxInfo(parcelFileDescriptor.getFileDescriptor(), strArr);
            IoUtils.closeQuietly(parcelFileDescriptor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dumpDatabaseInfo(ParcelFileDescriptor parcelFileDescriptor, String[] strArr) {
            FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(parcelFileDescriptor.getFileDescriptor()));
            SQLiteDebug.dump(new PrintWriterPrinter(fastPrintWriter), strArr);
            fastPrintWriter.flush();
        }

        @Override // android.app.IApplicationThread
        public void dumpDbInfo(ParcelFileDescriptor parcelFileDescriptor, final String[] strArr) {
            if (ActivityThread.this.mSystemThread) {
                try {
                    try {
                        final ParcelFileDescriptor parcelFileDescriptorDup = parcelFileDescriptor.dup();
                        IoUtils.closeQuietly(parcelFileDescriptor);
                        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: android.app.ActivityThread.ApplicationThread.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    ApplicationThread.this.dumpDatabaseInfo(parcelFileDescriptorDup, strArr);
                                } finally {
                                    IoUtils.closeQuietly(parcelFileDescriptorDup);
                                }
                            }
                        });
                        return;
                    } catch (IOException unused) {
                        Log.w(ActivityThread.TAG, "Could not dup FD " + parcelFileDescriptor.getFileDescriptor().getInt$());
                        IoUtils.closeQuietly(parcelFileDescriptor);
                        return;
                    }
                } catch (Throwable th) {
                    IoUtils.closeQuietly(parcelFileDescriptor);
                    throw th;
                }
            }
            dumpDatabaseInfo(parcelFileDescriptor, strArr);
            IoUtils.closeQuietly(parcelFileDescriptor);
        }

        @Override // android.app.IApplicationThread
        public void unstableProviderDied(IBinder iBinder) {
            ActivityThread.this.sendMessage(142, iBinder);
        }

        @Override // android.app.IApplicationThread
        public void requestAssistContextExtras(IBinder iBinder, IBinder iBinder2, int i, int i2, int i3) {
            RequestAssistContextExtras requestAssistContextExtras = new RequestAssistContextExtras();
            requestAssistContextExtras.activityToken = iBinder;
            requestAssistContextExtras.requestToken = iBinder2;
            requestAssistContextExtras.requestType = i;
            requestAssistContextExtras.sessionId = i2;
            requestAssistContextExtras.flags = i3;
            ActivityThread.this.sendMessage(143, requestAssistContextExtras);
        }

        @Override // android.app.IApplicationThread
        public void setCoreSettings(Bundle bundle) {
            ActivityThread.this.sendMessage(138, bundle);
        }

        @Override // android.app.IApplicationThread
        public void updatePackageCompatibilityInfo(String str, CompatibilityInfo compatibilityInfo) {
            UpdateCompatibilityData updateCompatibilityData = new UpdateCompatibilityData();
            updateCompatibilityData.pkg = str;
            updateCompatibilityData.info = compatibilityInfo;
            ActivityThread.this.sendMessage(139, updateCompatibilityData);
        }

        @Override // android.app.IApplicationThread
        public void scheduleTrimMemory(int i) {
            PooledRunnable pooledRunnableObtainRunnable = PooledLambda.obtainRunnable(new BiConsumer() { // from class: android.app.-$$Lambda$ActivityThread$ApplicationThread$tUGFX7CUhzB4Pg5wFd5yeqOnu38
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ActivityThread) obj).handleTrimMemory(((Integer) obj2).intValue());
                }
            }, ActivityThread.this, Integer.valueOf(i));
            Choreographer mainThreadInstance = Choreographer.getMainThreadInstance();
            if (mainThreadInstance != null) {
                mainThreadInstance.postCallback(3, pooledRunnableObtainRunnable, null);
            } else {
                ActivityThread.this.mH.post(pooledRunnableObtainRunnable);
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleTranslucentConversionComplete(IBinder iBinder, boolean z) {
            ActivityThread.this.sendMessage(144, iBinder, z ? 1 : 0);
        }

        @Override // android.app.IApplicationThread
        public void scheduleOnNewActivityOptions(IBinder iBinder, Bundle bundle) {
            ActivityThread.this.sendMessage(146, new Pair(iBinder, ActivityOptions.fromBundle(bundle)));
        }

        @Override // android.app.IApplicationThread
        public void setProcessState(int i) {
            updateProcessState(i, true);
        }

        public void updateProcessState(int i, boolean z) {
            synchronized (this) {
                if (this.mLastProcessState != i) {
                    this.mLastProcessState = i;
                    VMRuntime.getRuntime().updateProcessState(i <= 5 ? 0 : 1);
                }
            }
        }

        @Override // android.app.IApplicationThread
        public void setNetworkBlockSeq(long j) {
            synchronized (ActivityThread.this.mNetworkPolicyLock) {
                ActivityThread.this.mNetworkBlockSeq = j;
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleInstallProvider(ProviderInfo providerInfo) {
            ActivityThread.this.sendMessage(145, providerInfo);
        }

        @Override // android.app.IApplicationThread
        public final void updateTimePrefs(int i) {
            Boolean bool;
            if (i == 0) {
                bool = Boolean.FALSE;
            } else {
                bool = i == 1 ? Boolean.TRUE : null;
            }
            DateFormat.set24HourTimePref(bool);
        }

        @Override // android.app.IApplicationThread
        public void scheduleEnterAnimationComplete(IBinder iBinder) {
            ActivityThread.this.sendMessage(149, iBinder);
        }

        @Override // android.app.IApplicationThread
        public void notifyCleartextNetwork(byte[] bArr) {
            if (StrictMode.vmCleartextNetworkEnabled()) {
                StrictMode.onCleartextNetworkDetected(bArr);
            }
        }

        @Override // android.app.IApplicationThread
        public void startBinderTracking() {
            ActivityThread.this.sendMessage(150, null);
        }

        @Override // android.app.IApplicationThread
        public void stopBinderTrackingAndDump(ParcelFileDescriptor parcelFileDescriptor) {
            try {
                ActivityThread.this.sendMessage(151, parcelFileDescriptor.dup());
            } catch (IOException unused) {
            } finally {
                IoUtils.closeQuietly(parcelFileDescriptor);
            }
        }

        @Override // android.app.IApplicationThread
        public void scheduleLocalVoiceInteractionStarted(IBinder iBinder, IVoiceInteractor iVoiceInteractor) throws RemoteException {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = iBinder;
            someArgsObtain.arg2 = iVoiceInteractor;
            ActivityThread.this.sendMessage(154, someArgsObtain);
        }

        @Override // android.app.IApplicationThread
        public void handleTrustStorageUpdate() {
            NetworkSecurityPolicy.getInstance().handleTrustStorageUpdate();
        }

        @Override // android.app.IApplicationThread
        public void scheduleTransaction(ClientTransaction clientTransaction) throws RemoteException {
            ActivityThread.this.scheduleTransaction(clientTransaction);
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void updatePendingConfiguration(Configuration configuration) {
        this.mAppThread.updatePendingConfiguration(configuration);
    }

    @Override // android.app.ClientTransactionHandler
    public void updateProcessState(int i, boolean z) {
        this.mAppThread.updateProcessState(i, z);
    }

    class H extends Handler {
        public static final int APPLICATION_INFO_CHANGED = 156;
        public static final int ATTACH_AGENT = 155;
        public static final int BIND_APPLICATION = 110;
        public static final int BIND_SERVICE = 121;
        public static final int CLEAN_UP_CONTEXT = 119;
        public static final int CONFIGURATION_CHANGED = 118;
        public static final int CREATE_BACKUP_AGENT = 128;
        public static final int CREATE_SERVICE = 114;
        public static final int DESTROY_BACKUP_AGENT = 129;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;
        public static final int DUMP_ACTIVITY = 136;
        public static final int DUMP_HEAP = 135;
        public static final int DUMP_PROVIDER = 141;
        public static final int DUMP_SERVICE = 123;
        public static final int ENABLE_JIT = 132;
        public static final int ENTER_ANIMATION_COMPLETE = 149;
        public static final int EXECUTE_TRANSACTION = 159;
        public static final int EXIT_APPLICATION = 111;
        public static final int GC_WHEN_IDLE = 120;
        public static final int INSTALL_PROVIDER = 145;
        public static final int LOCAL_VOICE_INTERACTION_STARTED = 154;
        public static final int LOW_MEMORY = 124;
        public static final int ON_NEW_ACTIVITY_OPTIONS = 146;
        public static final int PROFILER_CONTROL = 127;
        public static final int RECEIVER = 113;
        public static final int RELAUNCH_ACTIVITY = 160;
        public static final int REMOVE_PROVIDER = 131;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int RUN_ISOLATED_ENTRY_POINT = 158;
        public static final int SCHEDULE_CRASH = 134;
        public static final int SERVICE_ARGS = 115;
        public static final int SET_CORE_SETTINGS = 138;
        public static final int SLEEPING = 137;
        public static final int START_BINDER_TRACKING = 150;
        public static final int STOP_BINDER_TRACKING_AND_DUMP = 151;
        public static final int STOP_SERVICE = 116;
        public static final int SUICIDE = 130;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        public static final int UNBIND_SERVICE = 122;
        public static final int UNSTABLE_PROVIDER_DIED = 142;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;

        H() {
        }

        String codeToString(int i) {
            return Integer.toString(i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 110:
                    Trace.traceBegin(64L, "bindApplication");
                    ActivityThread.this.handleBindApplication((AppBindData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 111:
                    if (ActivityThread.this.mInitialApplication != null) {
                        ActivityThread.this.mInitialApplication.onTerminate();
                    }
                    Looper.myLooper().quit();
                    break;
                case 113:
                    Trace.traceBegin(64L, "broadcastReceiveComp");
                    ActivityThread.this.handleReceiver((ReceiverData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 114:
                    Trace.traceBegin(64L, "serviceCreate: " + String.valueOf(message.obj));
                    ActivityThread.this.handleCreateService((CreateServiceData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 115:
                    Trace.traceBegin(64L, "serviceStart: " + String.valueOf(message.obj));
                    ActivityThread.this.handleServiceArgs((ServiceArgsData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 116:
                    Trace.traceBegin(64L, "serviceStop");
                    ActivityThread.this.handleStopService((IBinder) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 118:
                    ActivityThread.this.handleConfigurationChanged((Configuration) message.obj);
                    break;
                case 119:
                    ContextCleanupInfo contextCleanupInfo = (ContextCleanupInfo) message.obj;
                    contextCleanupInfo.context.performFinalCleanup(contextCleanupInfo.who, contextCleanupInfo.what);
                    break;
                case 120:
                    ActivityThread.this.scheduleGcIdler();
                    break;
                case 121:
                    Trace.traceBegin(64L, "serviceBind");
                    ActivityThread.this.handleBindService((BindServiceData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 122:
                    Trace.traceBegin(64L, "serviceUnbind");
                    ActivityThread.this.handleUnbindService((BindServiceData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 123:
                    ActivityThread.this.handleDumpService((DumpComponentInfo) message.obj);
                    break;
                case 124:
                    Trace.traceBegin(64L, "lowMemory");
                    ActivityThread.this.handleLowMemory();
                    Trace.traceEnd(64L);
                    break;
                case 127:
                    ActivityThread.this.handleProfilerControl(message.arg1 != 0, (ProfilerInfo) message.obj, message.arg2);
                    break;
                case 128:
                    Trace.traceBegin(64L, "backupCreateAgent");
                    ActivityThread.this.handleCreateBackupAgent((CreateBackupAgentData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 129:
                    Trace.traceBegin(64L, "backupDestroyAgent");
                    ActivityThread.this.handleDestroyBackupAgent((CreateBackupAgentData) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 130:
                    Process.killProcess(Process.myPid());
                    break;
                case 131:
                    Trace.traceBegin(64L, "providerRemove");
                    ActivityThread.this.completeRemoveProvider((ProviderRefCount) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 132:
                    ActivityThread.this.ensureJitEnabled();
                    break;
                case 133:
                    Trace.traceBegin(64L, "broadcastPackage");
                    ActivityThread.this.handleDispatchPackageBroadcast(message.arg1, (String[]) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 134:
                    throw new RemoteServiceException((String) message.obj);
                case 135:
                    ActivityThread.handleDumpHeap((DumpHeapData) message.obj);
                    break;
                case 136:
                    ActivityThread.this.handleDumpActivity((DumpComponentInfo) message.obj);
                    break;
                case 137:
                    Trace.traceBegin(64L, "sleeping");
                    ActivityThread.this.handleSleeping((IBinder) message.obj, message.arg1 != 0);
                    Trace.traceEnd(64L);
                    break;
                case 138:
                    Trace.traceBegin(64L, "setCoreSettings");
                    ActivityThread.this.handleSetCoreSettings((Bundle) message.obj);
                    Trace.traceEnd(64L);
                    break;
                case 139:
                    ActivityThread.this.handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData) message.obj);
                    break;
                case 141:
                    ActivityThread.this.handleDumpProvider((DumpComponentInfo) message.obj);
                    break;
                case 142:
                    ActivityThread.this.handleUnstableProviderDied((IBinder) message.obj, false);
                    break;
                case 143:
                    ActivityThread.this.handleRequestAssistContextExtras((RequestAssistContextExtras) message.obj);
                    break;
                case 144:
                    ActivityThread.this.handleTranslucentConversionComplete((IBinder) message.obj, message.arg1 == 1);
                    break;
                case 145:
                    ActivityThread.this.handleInstallProvider((ProviderInfo) message.obj);
                    break;
                case 146:
                    Pair pair = (Pair) message.obj;
                    ActivityThread.this.onNewActivityOptions((IBinder) pair.first, (ActivityOptions) pair.second);
                    break;
                case 149:
                    ActivityThread.this.handleEnterAnimationComplete((IBinder) message.obj);
                    break;
                case 150:
                    ActivityThread.this.handleStartBinderTracking();
                    break;
                case 151:
                    ActivityThread.this.handleStopBinderTrackingAndDump((ParcelFileDescriptor) message.obj);
                    break;
                case 154:
                    ActivityThread.this.handleLocalVoiceInteractionStarted((IBinder) ((SomeArgs) message.obj).arg1, (IVoiceInteractor) ((SomeArgs) message.obj).arg2);
                    break;
                case 155:
                    Application application = ActivityThread.this.getApplication();
                    ActivityThread.handleAttachAgent((String) message.obj, application != null ? application.mLoadedApk : null);
                    break;
                case 156:
                    ActivityThread.this.mUpdatingSystemConfig = true;
                    try {
                        ActivityThread.this.handleApplicationInfoChanged((ApplicationInfo) message.obj);
                        ActivityThread.this.mUpdatingSystemConfig = false;
                    } catch (Throwable th) {
                        ActivityThread.this.mUpdatingSystemConfig = false;
                        throw th;
                    }
                    break;
                case 158:
                    ActivityThread.this.handleRunIsolatedEntryPoint((String) ((SomeArgs) message.obj).arg1, (String[]) ((SomeArgs) message.obj).arg2);
                    break;
                case 159:
                    ClientTransaction clientTransaction = (ClientTransaction) message.obj;
                    ActivityThread.this.mTransactionExecutor.execute(clientTransaction);
                    if (ActivityThread.isSystem()) {
                        clientTransaction.recycle();
                    }
                    break;
                case 160:
                    ActivityThread.this.handleRelaunchActivityLocally((IBinder) message.obj);
                    break;
            }
            Object obj = message.obj;
            if (obj instanceof SomeArgs) {
                ((SomeArgs) obj).recycle();
            }
        }
    }

    private class Idler implements MessageQueue.IdleHandler {
        private Idler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ActivityClientRecord activityClientRecord = ActivityThread.this.mNewActivities;
            boolean z = (ActivityThread.this.mBoundApplication == null || ActivityThread.this.mProfiler.profileFd == null || !ActivityThread.this.mProfiler.autoStopProfiler) ? false : true;
            if (activityClientRecord != null) {
                ActivityThread.this.mNewActivities = null;
                IActivityManager service = ActivityManager.getService();
                while (true) {
                    if (activityClientRecord.activity != null && !activityClientRecord.activity.mFinished) {
                        try {
                            service.activityIdle(activityClientRecord.token, activityClientRecord.createdConfig, z);
                            activityClientRecord.createdConfig = null;
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                    ActivityClientRecord activityClientRecord2 = activityClientRecord.nextIdle;
                    activityClientRecord.nextIdle = null;
                    if (activityClientRecord2 == null) {
                        break;
                    }
                    activityClientRecord = activityClientRecord2;
                }
            }
            if (z) {
                ActivityThread.this.mProfiler.stopProfiling();
            }
            ActivityThread.this.ensureJitEnabled();
            return false;
        }
    }

    final class GcIdler implements MessageQueue.IdleHandler {
        GcIdler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public final boolean queueIdle() {
            ActivityThread.this.doGcIfNeeded();
            return false;
        }
    }

    public static ActivityThread currentActivityThread() {
        return sCurrentActivityThread;
    }

    public static boolean isSystem() {
        if (sCurrentActivityThread != null) {
            return sCurrentActivityThread.mSystemThread;
        }
        return false;
    }

    public static String currentOpPackageName() {
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread == null || activityThreadCurrentActivityThread.getApplication() == null) {
            return null;
        }
        return activityThreadCurrentActivityThread.getApplication().getOpPackageName();
    }

    public static String currentPackageName() {
        AppBindData appBindData;
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread == null || (appBindData = activityThreadCurrentActivityThread.mBoundApplication) == null) {
            return null;
        }
        return appBindData.appInfo.packageName;
    }

    public static String currentProcessName() {
        AppBindData appBindData;
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread == null || (appBindData = activityThreadCurrentActivityThread.mBoundApplication) == null) {
            return null;
        }
        return appBindData.processName;
    }

    public static Application currentApplication() {
        ActivityThread activityThreadCurrentActivityThread = currentActivityThread();
        if (activityThreadCurrentActivityThread != null) {
            return activityThreadCurrentActivityThread.mInitialApplication;
        }
        return null;
    }

    public static IPackageManager getPackageManager() {
        if (sPackageManager != null) {
            return sPackageManager;
        }
        sPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        return sPackageManager;
    }

    Configuration applyConfigCompatMainThread(int i, Configuration configuration, CompatibilityInfo compatibilityInfo) {
        if (configuration == null) {
            return null;
        }
        if (compatibilityInfo.supportsScreen()) {
            return configuration;
        }
        this.mMainThreadConfig.setTo(configuration);
        Configuration configuration2 = this.mMainThreadConfig;
        compatibilityInfo.applyToConfiguration(i, configuration2);
        return configuration2;
    }

    Resources getTopLevelResources(String str, String[] strArr, String[] strArr2, String[] strArr3, int i, LoadedApk loadedApk) {
        return this.mResourcesManager.getResources(null, str, strArr, strArr2, strArr3, i, null, loadedApk.getCompatibilityInfo(), loadedApk.getClassLoader());
    }

    final Handler getHandler() {
        return this.mH;
    }

    public final LoadedApk getPackageInfo(String str, CompatibilityInfo compatibilityInfo, int i) {
        return getPackageInfo(str, compatibilityInfo, i, UserHandle.myUserId());
    }

    public final LoadedApk getPackageInfo(String str, CompatibilityInfo compatibilityInfo, int i, int i2) {
        WeakReference<LoadedApk> weakReference;
        boolean z = UserHandle.myUserId() != i2;
        synchronized (this.mResourcesManager) {
            try {
                if (z) {
                    weakReference = null;
                } else if ((i & 1) != 0) {
                    weakReference = this.mPackages.get(str);
                } else {
                    weakReference = this.mResourcePackages.get(str);
                }
                LoadedApk loadedApk = weakReference != null ? weakReference.get() : null;
                if (loadedApk != null && (loadedApk.mResources == null || loadedApk.mResources.getAssets().isUpToDate())) {
                    if (loadedApk.isSecurityViolation() && (i & 2) == 0) {
                        throw new SecurityException("Requesting code from " + str + " to be run in process " + this.mBoundApplication.processName + "/" + this.mBoundApplication.appInfo.uid);
                    }
                    return loadedApk;
                }
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(str, 268436480, i2);
                    if (applicationInfo != null) {
                        return getPackageInfo(applicationInfo, compatibilityInfo, i);
                    }
                    return null;
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final LoadedApk getPackageInfo(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, int i) {
        boolean z = (i & 1) != 0;
        boolean z2 = z && applicationInfo.uid != 0 && applicationInfo.uid != 1000 && (this.mBoundApplication == null || !UserHandle.isSameApp(applicationInfo.uid, this.mBoundApplication.appInfo.uid));
        boolean z3 = z && (1073741824 & i) != 0;
        if ((i & 3) == 1 && z2) {
            String str = "Requesting code from " + applicationInfo.packageName + " (with uid " + applicationInfo.uid + ")";
            if (this.mBoundApplication != null) {
                str = str + " to be run in process " + this.mBoundApplication.processName + " (with uid " + this.mBoundApplication.appInfo.uid + ")";
            }
            throw new SecurityException(str);
        }
        return getPackageInfo(applicationInfo, compatibilityInfo, null, z2, z, z3);
    }

    @Override // android.app.ClientTransactionHandler
    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
        return getPackageInfo(applicationInfo, compatibilityInfo, null, false, true, false);
    }

    public final LoadedApk peekPackageInfo(String str, boolean z) {
        WeakReference<LoadedApk> weakReference;
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            try {
                if (z) {
                    weakReference = this.mPackages.get(str);
                } else {
                    weakReference = this.mResourcePackages.get(str);
                }
                loadedApk = weakReference != null ? weakReference.get() : null;
            } catch (Throwable th) {
                throw th;
            }
        }
        return loadedApk;
    }

    private LoadedApk getPackageInfo(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo, ClassLoader classLoader, boolean z, boolean z2, boolean z3) {
        WeakReference<LoadedApk> weakReference;
        LoadedApk loadedApk;
        boolean z4 = UserHandle.myUserId() != UserHandle.getUserId(applicationInfo.uid);
        synchronized (this.mResourcesManager) {
            try {
                if (z4) {
                    weakReference = null;
                } else if (z2) {
                    weakReference = this.mPackages.get(applicationInfo.packageName);
                } else {
                    weakReference = this.mResourcePackages.get(applicationInfo.packageName);
                }
                loadedApk = weakReference != null ? weakReference.get() : null;
                if (loadedApk == null || (loadedApk.mResources != null && !loadedApk.mResources.getAssets().isUpToDate())) {
                    LoadedApk loadedApk2 = new LoadedApk(this, applicationInfo, compatibilityInfo, classLoader, z, z2 && (applicationInfo.flags & 4) != 0, z3);
                    if (this.mSystemThread && ZenModeConfig.SYSTEM_AUTHORITY.equals(applicationInfo.packageName)) {
                        loadedApk2.installSystemApplicationInfo(applicationInfo, getSystemContext().mPackageInfo.getClassLoader());
                    }
                    if (!z4) {
                        if (z2) {
                            this.mPackages.put(applicationInfo.packageName, new WeakReference<>(loadedApk2));
                        } else {
                            this.mResourcePackages.put(applicationInfo.packageName, new WeakReference<>(loadedApk2));
                        }
                    }
                    loadedApk = loadedApk2;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return loadedApk;
    }

    ActivityThread() {
    }

    public ApplicationThread getApplicationThread() {
        return this.mAppThread;
    }

    public Instrumentation getInstrumentation() {
        return this.mInstrumentation;
    }

    public boolean isProfiling() {
        Profiler profiler = this.mProfiler;
        return (profiler == null || profiler.profileFile == null || this.mProfiler.profileFd != null) ? false : true;
    }

    public String getProfileFilePath() {
        return this.mProfiler.profileFile;
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    public Application getApplication() {
        return this.mInitialApplication;
    }

    public String getProcessName() {
        return this.mBoundApplication.processName;
    }

    public ContextImpl getSystemContext() {
        ContextImpl contextImpl;
        synchronized (this) {
            if (this.mSystemContext == null) {
                this.mSystemContext = ContextImpl.createSystemContext(this);
            }
            contextImpl = this.mSystemContext;
        }
        return contextImpl;
    }

    public ContextImpl getSystemUiContext() {
        ContextImpl contextImpl;
        synchronized (this) {
            if (this.mSystemUiContext == null) {
                this.mSystemUiContext = ContextImpl.createSystemUiContext(getSystemContext());
            }
            contextImpl = this.mSystemUiContext;
        }
        return contextImpl;
    }

    public void installSystemApplicationInfo(ApplicationInfo applicationInfo, ClassLoader classLoader) {
        synchronized (this) {
            getSystemContext().installSystemApplicationInfo(applicationInfo, classLoader);
            getSystemUiContext().installSystemApplicationInfo(applicationInfo, classLoader);
            this.mProfiler = new Profiler();
        }
    }

    void ensureJitEnabled() {
        if (this.mJitEnabled) {
            return;
        }
        this.mJitEnabled = true;
        VMRuntime.getRuntime().startJitCompilation();
    }

    void scheduleGcIdler() {
        if (!this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = true;
            Looper.myQueue().addIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void unscheduleGcIdler() {
        if (this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = false;
            Looper.myQueue().removeIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(120);
    }

    void doGcIfNeeded() {
        this.mGcIdlerScheduled = false;
        if (BinderInternal.getLastGcTime() + 5000 < SystemClock.uptimeMillis()) {
            BinderInternal.forceGc("bg");
        }
    }

    static void printRow(PrintWriter printWriter, String str, Object... objArr) {
        printWriter.println(String.format(str, objArr));
    }

    /* JADX WARN: Code duplicated, block: B:108:0x06fb A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:109:0x06fd  */
    /* JADX WARN: Code duplicated, block: B:111:0x0733  */
    /* JADX WARN: Code duplicated, block: B:113:0x0748  */
    /* JADX WARN: Code duplicated, block: B:115:0x076e  */
    /* JADX WARN: Code duplicated, block: B:61:0x04c0 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:62:0x04c2  */
    /* JADX WARN: Code duplicated, block: B:64:0x04fa  */
    /* JADX WARN: Code duplicated, block: B:65:0x04fd  */
    /* JADX WARN: Code duplicated, block: B:67:0x0513  */
    /* JADX WARN: Code duplicated, block: B:69:0x0537  */
    /* JADX WARN: Code duplicated, block: B:70:0x053a  */
    public static void dumpMemInfoTable(PrintWriter printWriter, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, boolean z3, boolean z4, int i, String str, long j, long j2, long j3, long j4, long j5, long j6) {
        Object obj;
        int i2;
        int i3;
        if (z) {
            printWriter.print(4);
            printWriter.print(',');
            printWriter.print(i);
            printWriter.print(',');
            printWriter.print(str);
            printWriter.print(',');
            printWriter.print(j);
            printWriter.print(',');
            printWriter.print(j4);
            printWriter.print(',');
            printWriter.print("N/A,");
            printWriter.print(j + j4);
            printWriter.print(',');
            printWriter.print(j2);
            printWriter.print(',');
            printWriter.print(j5);
            printWriter.print(',');
            printWriter.print("N/A,");
            printWriter.print(j2 + j5);
            printWriter.print(',');
            printWriter.print(j3);
            printWriter.print(',');
            printWriter.print(j6);
            printWriter.print(',');
            printWriter.print("N/A,");
            printWriter.print(j3 + j6);
            printWriter.print(',');
            printWriter.print(memoryInfo.nativePss);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikPss);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherPss);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalPss());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativeSwappablePss);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikSwappablePss);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherSwappablePss);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalSwappablePss());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativeSharedDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikSharedDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherSharedDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalSharedDirty());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativeSharedClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikSharedClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherSharedClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalSharedClean());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativePrivateDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikPrivateDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherPrivateDirty);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalPrivateDirty());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativePrivateClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikPrivateClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherPrivateClean);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalPrivateClean());
            printWriter.print(',');
            printWriter.print(memoryInfo.nativeSwappedOut);
            printWriter.print(',');
            printWriter.print(memoryInfo.dalvikSwappedOut);
            printWriter.print(',');
            printWriter.print(memoryInfo.otherSwappedOut);
            printWriter.print(',');
            printWriter.print(memoryInfo.getTotalSwappedOut());
            printWriter.print(',');
            if (memoryInfo.hasSwappedOutPss) {
                printWriter.print(memoryInfo.nativeSwappedOutPss);
                printWriter.print(',');
                printWriter.print(memoryInfo.dalvikSwappedOutPss);
                printWriter.print(',');
                printWriter.print(memoryInfo.otherSwappedOutPss);
                printWriter.print(',');
                printWriter.print(memoryInfo.getTotalSwappedOutPss());
                printWriter.print(',');
            } else {
                printWriter.print("N/A,");
                printWriter.print("N/A,");
                printWriter.print("N/A,");
                printWriter.print("N/A,");
            }
            for (int i4 = 0; i4 < 17; i4++) {
                printWriter.print(Debug.MemoryInfo.getOtherLabel(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherPss(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherSwappablePss(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherSharedDirty(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherSharedClean(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherPrivateDirty(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherPrivateClean(i4));
                printWriter.print(',');
                printWriter.print(memoryInfo.getOtherSwappedOut(i4));
                printWriter.print(',');
                if (memoryInfo.hasSwappedOutPss) {
                    printWriter.print(memoryInfo.getOtherSwappedOutPss(i4));
                    printWriter.print(',');
                } else {
                    printWriter.print("N/A,");
                }
            }
            return;
        }
        if (z4) {
            obj = "------";
        } else {
            if (z2) {
                Object[] objArr = new Object[11];
                objArr[0] = "";
                objArr[1] = "Pss";
                objArr[2] = "Pss";
                objArr[3] = "Shared";
                objArr[4] = "Private";
                objArr[5] = "Shared";
                objArr[6] = "Private";
                objArr[7] = memoryInfo.hasSwappedOutPss ? "SwapPss" : "Swap";
                objArr[8] = "Heap";
                objArr[9] = "Heap";
                objArr[10] = "Heap";
                printRow(printWriter, HEAP_FULL_COLUMN, objArr);
                printRow(printWriter, HEAP_FULL_COLUMN, "", "Total", "Clean", "Dirty", "Dirty", "Clean", "Clean", "Dirty", "Size", "Alloc", "Free");
                printRow(printWriter, HEAP_FULL_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------", "------", "------");
                Object[] objArr2 = new Object[11];
                objArr2[0] = "Native Heap";
                objArr2[1] = Integer.valueOf(memoryInfo.nativePss);
                objArr2[2] = Integer.valueOf(memoryInfo.nativeSwappablePss);
                objArr2[3] = Integer.valueOf(memoryInfo.nativeSharedDirty);
                objArr2[4] = Integer.valueOf(memoryInfo.nativePrivateDirty);
                objArr2[5] = Integer.valueOf(memoryInfo.nativeSharedClean);
                objArr2[6] = Integer.valueOf(memoryInfo.nativePrivateClean);
                objArr2[7] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.nativeSwappedOutPss : memoryInfo.nativeSwappedOut);
                objArr2[8] = Long.valueOf(j);
                objArr2[9] = Long.valueOf(j2);
                objArr2[10] = Long.valueOf(j3);
                printRow(printWriter, HEAP_FULL_COLUMN, objArr2);
                Object[] objArr3 = new Object[11];
                objArr3[0] = "Dalvik Heap";
                objArr3[1] = Integer.valueOf(memoryInfo.dalvikPss);
                objArr3[2] = Integer.valueOf(memoryInfo.dalvikSwappablePss);
                objArr3[3] = Integer.valueOf(memoryInfo.dalvikSharedDirty);
                objArr3[4] = Integer.valueOf(memoryInfo.dalvikPrivateDirty);
                objArr3[5] = Integer.valueOf(memoryInfo.dalvikSharedClean);
                objArr3[6] = Integer.valueOf(memoryInfo.dalvikPrivateClean);
                objArr3[7] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.dalvikSwappedOutPss : memoryInfo.dalvikSwappedOut);
                objArr3[8] = Long.valueOf(j4);
                objArr3[9] = Long.valueOf(j5);
                objArr3[10] = Long.valueOf(j6);
                printRow(printWriter, HEAP_FULL_COLUMN, objArr3);
            } else {
                Object[] objArr4 = new Object[8];
                objArr4[0] = "";
                objArr4[1] = "Pss";
                objArr4[2] = "Private";
                objArr4[3] = "Private";
                objArr4[4] = memoryInfo.hasSwappedOutPss ? "SwapPss" : "Swap";
                objArr4[5] = "Heap";
                objArr4[6] = "Heap";
                objArr4[7] = "Heap";
                printRow(printWriter, HEAP_COLUMN, objArr4);
                printRow(printWriter, HEAP_COLUMN, "", "Total", "Dirty", "Clean", "Dirty", "Size", "Alloc", "Free");
                printRow(printWriter, HEAP_COLUMN, "", "------", "------", "------", "------", "------", "------", "------", "------");
                Object[] objArr5 = new Object[8];
                objArr5[0] = "Native Heap";
                objArr5[1] = Integer.valueOf(memoryInfo.nativePss);
                objArr5[2] = Integer.valueOf(memoryInfo.nativePrivateDirty);
                objArr5[3] = Integer.valueOf(memoryInfo.nativePrivateClean);
                objArr5[4] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.nativeSwappedOutPss : memoryInfo.nativeSwappedOut);
                objArr5[5] = Long.valueOf(j);
                objArr5[6] = Long.valueOf(j2);
                objArr5[7] = Long.valueOf(j3);
                printRow(printWriter, HEAP_COLUMN, objArr5);
                Object[] objArr6 = new Object[8];
                objArr6[0] = "Dalvik Heap";
                objArr6[1] = Integer.valueOf(memoryInfo.dalvikPss);
                objArr6[2] = Integer.valueOf(memoryInfo.dalvikPrivateDirty);
                objArr6[3] = Integer.valueOf(memoryInfo.dalvikPrivateClean);
                objArr6[4] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.dalvikSwappedOutPss : memoryInfo.dalvikSwappedOut);
                objArr6[5] = Long.valueOf(j4);
                objArr6[6] = Long.valueOf(j5);
                objArr6[7] = Long.valueOf(j6);
                printRow(printWriter, HEAP_COLUMN, objArr6);
            }
            int i5 = memoryInfo.otherPss;
            int i6 = memoryInfo.otherSwappablePss;
            int i7 = memoryInfo.otherSharedDirty;
            int i8 = memoryInfo.otherPrivateDirty;
            int i9 = memoryInfo.otherSharedClean;
            int i10 = memoryInfo.otherPrivateClean;
            int i11 = memoryInfo.otherSwappedOut;
            obj = "------";
            int i12 = memoryInfo.otherSwappedOutPss;
            int i13 = i5;
            int i14 = i7;
            int i15 = i6;
            for (int i16 = 0; i16 < 17; i16++) {
                int otherPss = memoryInfo.getOtherPss(i16);
                int otherSwappablePss = memoryInfo.getOtherSwappablePss(i16);
                int otherSharedDirty = memoryInfo.getOtherSharedDirty(i16);
                int otherPrivateDirty = memoryInfo.getOtherPrivateDirty(i16);
                int otherSharedClean = memoryInfo.getOtherSharedClean(i16);
                int otherPrivateClean = memoryInfo.getOtherPrivateClean(i16);
                int otherSwappedOut = memoryInfo.getOtherSwappedOut(i16);
                int otherSwappedOutPss = memoryInfo.getOtherSwappedOutPss(i16);
                if (otherPss != 0 || otherSharedDirty != 0 || otherPrivateDirty != 0 || otherSharedClean != 0 || otherPrivateClean != 0) {
                    if (z2) {
                        Object[] objArr7 = new Object[11];
                        objArr7[0] = Debug.MemoryInfo.getOtherLabel(i16);
                        objArr7[1] = Integer.valueOf(otherPss);
                        objArr7[2] = Integer.valueOf(otherSwappablePss);
                        objArr7[3] = Integer.valueOf(otherSharedDirty);
                        objArr7[4] = Integer.valueOf(otherPrivateDirty);
                        objArr7[5] = Integer.valueOf(otherSharedClean);
                        objArr7[6] = Integer.valueOf(otherPrivateClean);
                        if (memoryInfo.hasSwappedOutPss) {
                            i3 = otherSwappedOutPss;
                        } else {
                            i3 = otherSwappedOut;
                        }
                        objArr7[7] = Integer.valueOf(i3);
                        objArr7[8] = "";
                        objArr7[9] = "";
                        objArr7[10] = "";
                        printRow(printWriter, HEAP_FULL_COLUMN, objArr7);
                    } else {
                        Object[] objArr8 = new Object[8];
                        objArr8[0] = Debug.MemoryInfo.getOtherLabel(i16);
                        objArr8[1] = Integer.valueOf(otherPss);
                        objArr8[2] = Integer.valueOf(otherPrivateDirty);
                        objArr8[3] = Integer.valueOf(otherPrivateClean);
                        if (memoryInfo.hasSwappedOutPss) {
                            i2 = otherSwappedOutPss;
                        } else {
                            i2 = otherSwappedOut;
                        }
                        objArr8[4] = Integer.valueOf(i2);
                        objArr8[5] = "";
                        objArr8[6] = "";
                        objArr8[7] = "";
                        printRow(printWriter, HEAP_COLUMN, objArr8);
                    }
                    i13 -= otherPss;
                    i15 -= otherSwappablePss;
                    i14 -= otherSharedDirty;
                    i8 -= otherPrivateDirty;
                    i9 -= otherSharedClean;
                    i10 -= otherPrivateClean;
                    i11 -= otherSwappedOut;
                    i12 -= otherSwappedOutPss;
                } else if ((memoryInfo.hasSwappedOutPss ? otherSwappedOutPss : otherSwappedOut) != 0) {
                    if (z2) {
                        Object[] objArr9 = new Object[11];
                        objArr9[0] = Debug.MemoryInfo.getOtherLabel(i16);
                        objArr9[1] = Integer.valueOf(otherPss);
                        objArr9[2] = Integer.valueOf(otherSwappablePss);
                        objArr9[3] = Integer.valueOf(otherSharedDirty);
                        objArr9[4] = Integer.valueOf(otherPrivateDirty);
                        objArr9[5] = Integer.valueOf(otherSharedClean);
                        objArr9[6] = Integer.valueOf(otherPrivateClean);
                        if (memoryInfo.hasSwappedOutPss) {
                            i3 = otherSwappedOutPss;
                        } else {
                            i3 = otherSwappedOut;
                        }
                        objArr9[7] = Integer.valueOf(i3);
                        objArr9[8] = "";
                        objArr9[9] = "";
                        objArr9[10] = "";
                        printRow(printWriter, HEAP_FULL_COLUMN, objArr9);
                    } else {
                        Object[] objArr10 = new Object[8];
                        objArr10[0] = Debug.MemoryInfo.getOtherLabel(i16);
                        objArr10[1] = Integer.valueOf(otherPss);
                        objArr10[2] = Integer.valueOf(otherPrivateDirty);
                        objArr10[3] = Integer.valueOf(otherPrivateClean);
                        if (memoryInfo.hasSwappedOutPss) {
                            i2 = otherSwappedOutPss;
                        } else {
                            i2 = otherSwappedOut;
                        }
                        objArr10[4] = Integer.valueOf(i2);
                        objArr10[5] = "";
                        objArr10[6] = "";
                        objArr10[7] = "";
                        printRow(printWriter, HEAP_COLUMN, objArr10);
                    }
                    i13 -= otherPss;
                    i15 -= otherSwappablePss;
                    i14 -= otherSharedDirty;
                    i8 -= otherPrivateDirty;
                    i9 -= otherSharedClean;
                    i10 -= otherPrivateClean;
                    i11 -= otherSwappedOut;
                    i12 -= otherSwappedOutPss;
                }
            }
            if (z2) {
                Object[] objArr11 = new Object[11];
                objArr11[0] = "Unknown";
                objArr11[1] = Integer.valueOf(i13);
                objArr11[2] = Integer.valueOf(i15);
                objArr11[3] = Integer.valueOf(i14);
                objArr11[4] = Integer.valueOf(i8);
                objArr11[5] = Integer.valueOf(i9);
                objArr11[6] = Integer.valueOf(i10);
                if (memoryInfo.hasSwappedOutPss) {
                    i11 = i12;
                }
                objArr11[7] = Integer.valueOf(i11);
                objArr11[8] = "";
                objArr11[9] = "";
                objArr11[10] = "";
                printRow(printWriter, HEAP_FULL_COLUMN, objArr11);
                Object[] objArr12 = new Object[11];
                objArr12[0] = "TOTAL";
                objArr12[1] = Integer.valueOf(memoryInfo.getTotalPss());
                objArr12[2] = Integer.valueOf(memoryInfo.getTotalSwappablePss());
                objArr12[3] = Integer.valueOf(memoryInfo.getTotalSharedDirty());
                objArr12[4] = Integer.valueOf(memoryInfo.getTotalPrivateDirty());
                objArr12[5] = Integer.valueOf(memoryInfo.getTotalSharedClean());
                objArr12[6] = Integer.valueOf(memoryInfo.getTotalPrivateClean());
                objArr12[7] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.getTotalSwappedOutPss() : memoryInfo.getTotalSwappedOut());
                objArr12[8] = Long.valueOf(j + j4);
                objArr12[9] = Long.valueOf(j2 + j5);
                objArr12[10] = Long.valueOf(j3 + j6);
                printRow(printWriter, HEAP_FULL_COLUMN, objArr12);
            } else {
                Object[] objArr13 = new Object[8];
                objArr13[0] = "Unknown";
                objArr13[1] = Integer.valueOf(i13);
                objArr13[2] = Integer.valueOf(i8);
                objArr13[3] = Integer.valueOf(i10);
                if (memoryInfo.hasSwappedOutPss) {
                    i11 = i12;
                }
                objArr13[4] = Integer.valueOf(i11);
                objArr13[5] = "";
                objArr13[6] = "";
                objArr13[7] = "";
                printRow(printWriter, HEAP_COLUMN, objArr13);
                Object[] objArr14 = new Object[8];
                objArr14[0] = "TOTAL";
                objArr14[1] = Integer.valueOf(memoryInfo.getTotalPss());
                objArr14[2] = Integer.valueOf(memoryInfo.getTotalPrivateDirty());
                objArr14[3] = Integer.valueOf(memoryInfo.getTotalPrivateClean());
                objArr14[4] = Integer.valueOf(memoryInfo.hasSwappedOutPss ? memoryInfo.getTotalSwappedOutPss() : memoryInfo.getTotalSwappedOut());
                objArr14[5] = Long.valueOf(j + j4);
                objArr14[6] = Long.valueOf(j2 + j5);
                objArr14[7] = Long.valueOf(j3 + j6);
                printRow(printWriter, HEAP_COLUMN, objArr14);
            }
            if (z3) {
                printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                printWriter.println(" Dalvik Details");
                for (int i17 = 17; i17 < 31; i17++) {
                    int otherPss2 = memoryInfo.getOtherPss(i17);
                    int otherSwappablePss2 = memoryInfo.getOtherSwappablePss(i17);
                    int otherSharedDirty2 = memoryInfo.getOtherSharedDirty(i17);
                    int otherPrivateDirty2 = memoryInfo.getOtherPrivateDirty(i17);
                    int otherSharedClean2 = memoryInfo.getOtherSharedClean(i17);
                    int otherPrivateClean2 = memoryInfo.getOtherPrivateClean(i17);
                    int otherSwappedOut2 = memoryInfo.getOtherSwappedOut(i17);
                    int otherSwappedOutPss2 = memoryInfo.getOtherSwappedOutPss(i17);
                    if (otherPss2 == 0 && otherSharedDirty2 == 0 && otherPrivateDirty2 == 0 && otherSharedClean2 == 0 && otherPrivateClean2 == 0) {
                        if ((memoryInfo.hasSwappedOutPss ? otherSwappedOutPss2 : otherSwappedOut2) != 0) {
                            if (z2) {
                                Object[] objArr15 = new Object[11];
                                objArr15[0] = Debug.MemoryInfo.getOtherLabel(i17);
                                objArr15[1] = Integer.valueOf(otherPss2);
                                objArr15[2] = Integer.valueOf(otherSwappablePss2);
                                objArr15[3] = Integer.valueOf(otherSharedDirty2);
                                objArr15[4] = Integer.valueOf(otherPrivateDirty2);
                                objArr15[5] = Integer.valueOf(otherSharedClean2);
                                objArr15[6] = Integer.valueOf(otherPrivateClean2);
                                if (memoryInfo.hasSwappedOutPss) {
                                    otherSwappedOut2 = otherSwappedOutPss2;
                                }
                                objArr15[7] = Integer.valueOf(otherSwappedOut2);
                                objArr15[8] = "";
                                objArr15[9] = "";
                                objArr15[10] = "";
                                printRow(printWriter, HEAP_FULL_COLUMN, objArr15);
                            } else {
                                Object[] objArr16 = new Object[8];
                                objArr16[0] = Debug.MemoryInfo.getOtherLabel(i17);
                                objArr16[1] = Integer.valueOf(otherPss2);
                                objArr16[2] = Integer.valueOf(otherPrivateDirty2);
                                objArr16[3] = Integer.valueOf(otherPrivateClean2);
                                if (memoryInfo.hasSwappedOutPss) {
                                    otherSwappedOut2 = otherSwappedOutPss2;
                                }
                                objArr16[4] = Integer.valueOf(otherSwappedOut2);
                                objArr16[5] = "";
                                objArr16[6] = "";
                                objArr16[7] = "";
                                printRow(printWriter, HEAP_COLUMN, objArr16);
                            }
                        }
                    } else if (z2) {
                        Object[] objArr17 = new Object[11];
                        objArr17[0] = Debug.MemoryInfo.getOtherLabel(i17);
                        objArr17[1] = Integer.valueOf(otherPss2);
                        objArr17[2] = Integer.valueOf(otherSwappablePss2);
                        objArr17[3] = Integer.valueOf(otherSharedDirty2);
                        objArr17[4] = Integer.valueOf(otherPrivateDirty2);
                        objArr17[5] = Integer.valueOf(otherSharedClean2);
                        objArr17[6] = Integer.valueOf(otherPrivateClean2);
                        if (memoryInfo.hasSwappedOutPss) {
                            otherSwappedOut2 = otherSwappedOutPss2;
                        }
                        objArr17[7] = Integer.valueOf(otherSwappedOut2);
                        objArr17[8] = "";
                        objArr17[9] = "";
                        objArr17[10] = "";
                        printRow(printWriter, HEAP_FULL_COLUMN, objArr17);
                    } else {
                        Object[] objArr18 = new Object[8];
                        objArr18[0] = Debug.MemoryInfo.getOtherLabel(i17);
                        objArr18[1] = Integer.valueOf(otherPss2);
                        objArr18[2] = Integer.valueOf(otherPrivateDirty2);
                        objArr18[3] = Integer.valueOf(otherPrivateClean2);
                        if (memoryInfo.hasSwappedOutPss) {
                            otherSwappedOut2 = otherSwappedOutPss2;
                        }
                        objArr18[4] = Integer.valueOf(otherSwappedOut2);
                        objArr18[5] = "";
                        objArr18[6] = "";
                        objArr18[7] = "";
                        printRow(printWriter, HEAP_COLUMN, objArr18);
                    }
                }
            }
        }
        printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        printWriter.println(" App Summary");
        printRow(printWriter, ONE_COUNT_COLUMN_HEADER, "", "Pss(KB)");
        printRow(printWriter, ONE_COUNT_COLUMN_HEADER, "", obj);
        printRow(printWriter, ONE_COUNT_COLUMN, "Java Heap:", Integer.valueOf(memoryInfo.getSummaryJavaHeap()));
        printRow(printWriter, ONE_COUNT_COLUMN, "Native Heap:", Integer.valueOf(memoryInfo.getSummaryNativeHeap()));
        printRow(printWriter, ONE_COUNT_COLUMN, "Code:", Integer.valueOf(memoryInfo.getSummaryCode()));
        printRow(printWriter, ONE_COUNT_COLUMN, "Stack:", Integer.valueOf(memoryInfo.getSummaryStack()));
        printRow(printWriter, ONE_COUNT_COLUMN, "Graphics:", Integer.valueOf(memoryInfo.getSummaryGraphics()));
        printRow(printWriter, ONE_COUNT_COLUMN, "Private Other:", Integer.valueOf(memoryInfo.getSummaryPrivateOther()));
        printRow(printWriter, ONE_COUNT_COLUMN, "System:", Integer.valueOf(memoryInfo.getSummarySystem()));
        printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        if (memoryInfo.hasSwappedOutPss) {
            printRow(printWriter, TWO_COUNT_COLUMNS, "TOTAL:", Integer.valueOf(memoryInfo.getSummaryTotalPss()), "TOTAL SWAP PSS:", Integer.valueOf(memoryInfo.getSummaryTotalSwapPss()));
        } else {
            printRow(printWriter, TWO_COUNT_COLUMNS, "TOTAL:", Integer.valueOf(memoryInfo.getSummaryTotalPss()), "TOTAL SWAP (KB):", Integer.valueOf(memoryInfo.getSummaryTotalSwap()));
        }
    }

    private static void dumpMemoryInfo(ProtoOutputStream protoOutputStream, long j, String str, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, str);
        protoOutputStream.write(1120986464258L, i);
        protoOutputStream.write(1120986464259L, i2);
        protoOutputStream.write(1120986464260L, i3);
        protoOutputStream.write(1120986464261L, i4);
        protoOutputStream.write(1120986464262L, i5);
        protoOutputStream.write(1120986464263L, i6);
        if (z) {
            protoOutputStream.write(1120986464265L, i8);
        } else {
            protoOutputStream.write(1120986464264L, i7);
        }
        protoOutputStream.end(jStart);
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0164  */
    /* JADX WARN: Code duplicated, block: B:38:0x0265  */
    public static void dumpMemInfoTable(ProtoOutputStream protoOutputStream, Debug.MemoryInfo memoryInfo, boolean z, boolean z2, long j, long j2, long j3, long j4, long j5, long j6) {
        ProtoOutputStream protoOutputStream2;
        Debug.MemoryInfo memoryInfo2;
        long j7;
        if (z2) {
            protoOutputStream2 = protoOutputStream;
            memoryInfo2 = memoryInfo;
            j7 = 1120986464258L;
        } else {
            long jStart = protoOutputStream.start(1146756268035L);
            dumpMemoryInfo(protoOutputStream, 1146756268033L, "Native Heap", memoryInfo.nativePss, memoryInfo.nativeSwappablePss, memoryInfo.nativeSharedDirty, memoryInfo.nativePrivateDirty, memoryInfo.nativeSharedClean, memoryInfo.nativePrivateClean, memoryInfo.hasSwappedOutPss, memoryInfo.nativeSwappedOut, memoryInfo.nativeSwappedOutPss);
            protoOutputStream2 = protoOutputStream;
            protoOutputStream2.write(1120986464258L, j);
            protoOutputStream2.write(1120986464259L, j2);
            protoOutputStream2.write(1120986464260L, j3);
            protoOutputStream2.end(jStart);
            long jStart2 = protoOutputStream2.start(1146756268036L);
            dumpMemoryInfo(protoOutputStream, 1146756268033L, "Dalvik Heap", memoryInfo.dalvikPss, memoryInfo.dalvikSwappablePss, memoryInfo.dalvikSharedDirty, memoryInfo.dalvikPrivateDirty, memoryInfo.dalvikSharedClean, memoryInfo.dalvikPrivateClean, memoryInfo.hasSwappedOutPss, memoryInfo.dalvikSwappedOut, memoryInfo.dalvikSwappedOutPss);
            protoOutputStream2.write(1120986464258L, j4);
            protoOutputStream2.write(1120986464259L, j5);
            protoOutputStream2.write(1120986464260L, j6);
            protoOutputStream2.end(jStart2);
            Debug.MemoryInfo memoryInfo3 = memoryInfo;
            int i = memoryInfo3.otherPss;
            int i2 = memoryInfo3.otherSwappablePss;
            int i3 = memoryInfo3.otherSharedDirty;
            int i4 = memoryInfo3.otherPrivateDirty;
            int i5 = i2;
            int i6 = i3;
            int i7 = i4;
            int i8 = memoryInfo3.otherSharedClean;
            int i9 = memoryInfo3.otherPrivateClean;
            int i10 = memoryInfo3.otherSwappedOut;
            int i11 = memoryInfo3.otherSwappedOutPss;
            int i12 = 0;
            int i13 = i;
            while (i12 < 17) {
                int otherPss = memoryInfo3.getOtherPss(i12);
                int otherSwappablePss = memoryInfo3.getOtherSwappablePss(i12);
                int otherSharedDirty = memoryInfo3.getOtherSharedDirty(i12);
                int otherPrivateDirty = memoryInfo3.getOtherPrivateDirty(i12);
                int otherSharedClean = memoryInfo3.getOtherSharedClean(i12);
                int otherPrivateClean = memoryInfo3.getOtherPrivateClean(i12);
                int otherSwappedOut = memoryInfo3.getOtherSwappedOut(i12);
                int otherSwappedOutPss = memoryInfo3.getOtherSwappedOutPss(i12);
                if (otherPss == 0 && otherSharedDirty == 0 && otherPrivateDirty == 0 && otherSharedClean == 0 && otherPrivateClean == 0) {
                    if ((memoryInfo3.hasSwappedOutPss ? otherSwappedOutPss : otherSwappedOut) != 0) {
                        dumpMemoryInfo(protoOutputStream, 2246267895813L, Debug.MemoryInfo.getOtherLabel(i12), otherPss, otherSwappablePss, otherSharedDirty, otherPrivateDirty, otherSharedClean, otherPrivateClean, memoryInfo3.hasSwappedOutPss, otherSwappedOut, otherSwappedOutPss);
                        i13 -= otherPss;
                        i5 -= otherSwappablePss;
                        i6 -= otherSharedDirty;
                        i7 -= otherPrivateDirty;
                        i8 -= otherSharedClean;
                        i9 -= otherPrivateClean;
                        i10 -= otherSwappedOut;
                        i11 -= otherSwappedOutPss;
                    }
                } else {
                    dumpMemoryInfo(protoOutputStream, 2246267895813L, Debug.MemoryInfo.getOtherLabel(i12), otherPss, otherSwappablePss, otherSharedDirty, otherPrivateDirty, otherSharedClean, otherPrivateClean, memoryInfo3.hasSwappedOutPss, otherSwappedOut, otherSwappedOutPss);
                    i13 -= otherPss;
                    i5 -= otherSwappablePss;
                    i6 -= otherSharedDirty;
                    i7 -= otherPrivateDirty;
                    i8 -= otherSharedClean;
                    i9 -= otherPrivateClean;
                    i10 -= otherSwappedOut;
                    i11 -= otherSwappedOutPss;
                }
                i12++;
                memoryInfo3 = memoryInfo;
            }
            dumpMemoryInfo(protoOutputStream, 1146756268038L, "Unknown", i13, i5, i6, i7, i8, i9, memoryInfo.hasSwappedOutPss, i10, i11);
            long jStart3 = protoOutputStream2.start(1146756268039L);
            dumpMemoryInfo(protoOutputStream, 1146756268033L, "TOTAL", memoryInfo.getTotalPss(), memoryInfo.getTotalSwappablePss(), memoryInfo.getTotalSharedDirty(), memoryInfo.getTotalPrivateDirty(), memoryInfo.getTotalSharedClean(), memoryInfo.getTotalPrivateClean(), memoryInfo.hasSwappedOutPss, memoryInfo.getTotalSwappedOut(), memoryInfo.getTotalSwappedOutPss());
            j7 = 1120986464258L;
            protoOutputStream2.write(1120986464258L, j + j4);
            memoryInfo2 = memoryInfo;
            protoOutputStream2.write(1120986464259L, j2 + j5);
            protoOutputStream2.write(1120986464260L, j3 + j6);
            protoOutputStream2.end(jStart3);
            if (z) {
                for (int i14 = 17; i14 < 31; i14++) {
                    int otherPss2 = memoryInfo2.getOtherPss(i14);
                    int otherSwappablePss2 = memoryInfo2.getOtherSwappablePss(i14);
                    int otherSharedDirty2 = memoryInfo2.getOtherSharedDirty(i14);
                    int otherPrivateDirty2 = memoryInfo2.getOtherPrivateDirty(i14);
                    int otherSharedClean2 = memoryInfo2.getOtherSharedClean(i14);
                    int otherPrivateClean2 = memoryInfo2.getOtherPrivateClean(i14);
                    int otherSwappedOut2 = memoryInfo2.getOtherSwappedOut(i14);
                    int otherSwappedOutPss2 = memoryInfo2.getOtherSwappedOutPss(i14);
                    if (otherPss2 == 0 && otherSharedDirty2 == 0 && otherPrivateDirty2 == 0 && otherSharedClean2 == 0 && otherPrivateClean2 == 0) {
                        if ((memoryInfo2.hasSwappedOutPss ? otherSwappedOutPss2 : otherSwappedOut2) != 0) {
                            dumpMemoryInfo(protoOutputStream, 2246267895816L, Debug.MemoryInfo.getOtherLabel(i14), otherPss2, otherSwappablePss2, otherSharedDirty2, otherPrivateDirty2, otherSharedClean2, otherPrivateClean2, memoryInfo2.hasSwappedOutPss, otherSwappedOut2, otherSwappedOutPss2);
                        }
                    } else {
                        dumpMemoryInfo(protoOutputStream, 2246267895816L, Debug.MemoryInfo.getOtherLabel(i14), otherPss2, otherSwappablePss2, otherSharedDirty2, otherPrivateDirty2, otherSharedClean2, otherPrivateClean2, memoryInfo2.hasSwappedOutPss, otherSwappedOut2, otherSwappedOutPss2);
                    }
                }
            }
        }
        long jStart4 = protoOutputStream2.start(1146756268041L);
        protoOutputStream2.write(1120986464257L, memoryInfo.getSummaryJavaHeap());
        protoOutputStream2.write(j7, memoryInfo.getSummaryNativeHeap());
        protoOutputStream2.write(1120986464259L, memoryInfo.getSummaryCode());
        protoOutputStream2.write(1120986464260L, memoryInfo.getSummaryStack());
        protoOutputStream2.write(1120986464261L, memoryInfo.getSummaryGraphics());
        protoOutputStream2.write(1120986464262L, memoryInfo.getSummaryPrivateOther());
        protoOutputStream2.write(1120986464263L, memoryInfo.getSummarySystem());
        if (memoryInfo2.hasSwappedOutPss) {
            protoOutputStream2.write(1120986464264L, memoryInfo.getSummaryTotalSwapPss());
        } else {
            protoOutputStream2.write(1120986464264L, memoryInfo.getSummaryTotalSwap());
        }
        protoOutputStream2.end(jStart4);
    }

    public void registerOnActivityPausedListener(Activity activity, OnActivityPausedListener onActivityPausedListener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> arrayList = this.mOnPauseListeners.get(activity);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mOnPauseListeners.put(activity, arrayList);
            }
            arrayList.add(onActivityPausedListener);
        }
    }

    public void unregisterOnActivityPausedListener(Activity activity, OnActivityPausedListener onActivityPausedListener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> arrayList = this.mOnPauseListeners.get(activity);
            if (arrayList != null) {
                arrayList.remove(onActivityPausedListener);
            }
        }
    }

    public final ActivityInfo resolveActivityInfo(Intent intent) {
        ActivityInfo activityInfoResolveActivityInfo = intent.resolveActivityInfo(this.mInitialApplication.getPackageManager(), 1024);
        if (activityInfoResolveActivityInfo == null) {
            Instrumentation.checkStartActivityResult(-92, intent);
        }
        return activityInfoResolveActivityInfo;
    }

    public final Activity startActivityNow(Activity activity, String str, Intent intent, ActivityInfo activityInfo, IBinder iBinder, Bundle bundle, Activity.NonConfigurationInstances nonConfigurationInstances) {
        ActivityClientRecord activityClientRecord = new ActivityClientRecord();
        activityClientRecord.token = iBinder;
        activityClientRecord.ident = 0;
        activityClientRecord.intent = intent;
        activityClientRecord.state = bundle;
        activityClientRecord.parent = activity;
        activityClientRecord.embeddedID = str;
        activityClientRecord.activityInfo = activityInfo;
        activityClientRecord.lastNonConfigurationInstances = nonConfigurationInstances;
        return performLaunchActivity(activityClientRecord, null);
    }

    public final Activity getActivity(IBinder iBinder) {
        return this.mActivities.get(iBinder).activity;
    }

    @Override // android.app.ClientTransactionHandler
    public ActivityClientRecord getActivityClient(IBinder iBinder) {
        return this.mActivities.get(iBinder);
    }

    public final void sendActivityResult(IBinder iBinder, String str, int i, int i2, Intent intent) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ResultInfo(str, i, i2, intent));
        ClientTransaction clientTransactionObtain = ClientTransaction.obtain(this.mAppThread, iBinder);
        clientTransactionObtain.addCallback(ActivityResultItem.obtain(arrayList));
        try {
            this.mAppThread.scheduleTransaction(clientTransactionObtain);
        } catch (RemoteException unused) {
        }
    }

    @Override // android.app.ClientTransactionHandler
    TransactionExecutor getTransactionExecutor() {
        return this.mTransactionExecutor;
    }

    @Override // android.app.ClientTransactionHandler
    void sendMessage(int i, Object obj) {
        sendMessage(i, obj, 0, 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2) {
        sendMessage(i, obj, i2, 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2, int i3) {
        sendMessage(i, obj, i2, i3, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMessage(int i, Object obj, int i2, int i3, boolean z) {
        Message messageObtain = Message.obtain();
        messageObtain.what = i;
        messageObtain.obj = obj;
        messageObtain.arg1 = i2;
        messageObtain.arg2 = i3;
        if (z) {
            messageObtain.setAsynchronous(true);
        }
        this.mH.sendMessage(messageObtain);
    }

    private void sendMessage(int i, Object obj, int i2, int i3, int i4) {
        Message messageObtain = Message.obtain();
        messageObtain.what = i;
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.arg1 = obj;
        someArgsObtain.argi1 = i2;
        someArgsObtain.argi2 = i3;
        someArgsObtain.argi3 = i4;
        messageObtain.obj = someArgsObtain;
        this.mH.sendMessage(messageObtain);
    }

    final void scheduleContextCleanup(ContextImpl contextImpl, String str, String str2) {
        ContextCleanupInfo contextCleanupInfo = new ContextCleanupInfo();
        contextCleanupInfo.context = contextImpl;
        contextCleanupInfo.who = str;
        contextCleanupInfo.what = str2;
        sendMessage(119, contextCleanupInfo);
    }

    /* JADX WARN: Code duplicated, block: B:82:0x01af  */
    /* JADX WARN: Instruction removed from duplicated block: B:82:0x01af, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v24, types: [android.app.Instrumentation] */
    /* JADX WARN: Type inference failed for: r0v27, types: [android.app.Instrumentation] */
    /* JADX WARN: Type inference failed for: r1v7, types: [android.app.Instrumentation] */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.app.ContextImpl, android.content.Context] */
    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v15 */
    /* JADX WARN: Type inference failed for: r2v16, types: [android.app.Activity] */
    /* JADX WARN: Type inference failed for: r2v17, types: [android.app.Activity] */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v7, types: [android.app.Activity] */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v16 */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v30 */
    /* JADX WARN: Type inference failed for: r3v31 */
    /* JADX WARN: Type inference failed for: r3v32 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6, types: [android.app.ActivityThread] */
    /* JADX WARN: Type inference failed for: r8v0, types: [android.app.Activity, android.content.Context] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private Activity performLaunchActivity(ActivityClientRecord activityClientRecord, Intent intent) {
        Activity activityNewActivity;
        ?? r3;
        String str;
        boolean zOnException;
        ?? r2;
        ?? r4;
        ActivityClientRecord activityClientRecord2;
        ActivityThread activityThread;
        int i;
        Window window;
        ActivityInfo activityInfo = activityClientRecord.activityInfo;
        if (activityClientRecord.packageInfo == null) {
            activityClientRecord.packageInfo = getPackageInfo(activityInfo.applicationInfo, activityClientRecord.compatInfo, 1);
        }
        ComponentName component = activityClientRecord.intent.getComponent();
        if (component == null) {
            component = activityClientRecord.intent.resolveActivity(this.mInitialApplication.getPackageManager());
            activityClientRecord.intent.setComponent(component);
        }
        if (activityClientRecord.activityInfo.targetActivity != null) {
            component = new ComponentName(activityClientRecord.activityInfo.packageName, activityClientRecord.activityInfo.targetActivity);
        }
        ComponentName componentName = component;
        ?? CreateBaseContextForActivity = createBaseContextForActivity(activityClientRecord);
        try {
            ClassLoader classLoader = CreateBaseContextForActivity.getClassLoader();
            activityNewActivity = this.mInstrumentation.newActivity(classLoader, componentName.getClassName(), activityClientRecord.intent);
            try {
                StrictMode.incrementExpectedActivityCount(activityNewActivity.getClass());
                activityClientRecord.intent.setExtrasClassLoader(classLoader);
                activityClientRecord.intent.prepareToEnterProcess();
                Bundle bundle = activityClientRecord.state;
                r3 = bundle;
                if (bundle != null) {
                    Bundle bundle2 = activityClientRecord.state;
                    bundle2.setClassLoader(classLoader);
                    r3 = bundle2;
                }
            } catch (Exception e) {
                e = e;
                boolean zOnException2 = this.mInstrumentation.onException(activityNewActivity, e);
                r3 = zOnException2;
                if (!zOnException2) {
                    throw new RuntimeException("Unable to instantiate activity " + componentName + ": " + e.toString(), e);
                }
            }
        } catch (Exception e2) {
            e = e2;
            activityNewActivity = null;
        }
        CreateBaseContextForActivity = activityNewActivity;
        try {
            try {
                Application applicationMakeApplication = activityClientRecord.packageInfo.makeApplication(false, this.mInstrumentation);
                try {
                    if (CreateBaseContextForActivity != 0) {
                        CharSequence charSequenceLoadLabel = activityClientRecord.activityInfo.loadLabel(CreateBaseContextForActivity.getPackageManager());
                        Configuration configuration = new Configuration(this.mCompatConfiguration);
                        if (activityClientRecord.overrideConfig != null) {
                            configuration.updateFrom(activityClientRecord.overrideConfig);
                        }
                        if (activityClientRecord.mPendingRemoveWindow == null || !activityClientRecord.mPreserveWindow) {
                            window = null;
                        } else {
                            Window window2 = activityClientRecord.mPendingRemoveWindow;
                            activityClientRecord.mPendingRemoveWindow = null;
                            activityClientRecord.mPendingRemoveWindowManager = null;
                            window = window2;
                        }
                        CreateBaseContextForActivity.setOuterContext(CreateBaseContextForActivity);
                        try {
                            try {
                                try {
                                    componentName = componentName;
                                    str = ": ";
                                    try {
                                        CreateBaseContextForActivity.attach(CreateBaseContextForActivity, this, getInstrumentation(), activityClientRecord.token, activityClientRecord.ident, applicationMakeApplication, activityClientRecord.intent, activityClientRecord.activityInfo, charSequenceLoadLabel, activityClientRecord.parent, activityClientRecord.embeddedID, activityClientRecord.lastNonConfigurationInstances, configuration, activityClientRecord.referrer, activityClientRecord.voiceInteractor, window, activityClientRecord.configCallback);
                                        if (intent != null) {
                                            CreateBaseContextForActivity = CreateBaseContextForActivity;
                                            try {
                                                CreateBaseContextForActivity.mIntent = intent;
                                                CreateBaseContextForActivity = CreateBaseContextForActivity;
                                            } catch (Exception e3) {
                                                e = e3;
                                                r3 = this;
                                                zOnException = r3.mInstrumentation.onException(CreateBaseContextForActivity, e);
                                                r2 = CreateBaseContextForActivity;
                                                if (!zOnException) {
                                                    throw new RuntimeException("Unable to start activity " + componentName + str + e.toString(), e);
                                                }
                                            }
                                        } else {
                                            CreateBaseContextForActivity = CreateBaseContextForActivity;
                                        }
                                        activityClientRecord2 = activityClientRecord;
                                        activityClientRecord2.lastNonConfigurationInstances = null;
                                        checkAndBlockForNetworkAccess();
                                        CreateBaseContextForActivity.mStartedActivity = false;
                                        int themeResource = activityClientRecord2.activityInfo.getThemeResource();
                                        if (themeResource != 0) {
                                            CreateBaseContextForActivity.setTheme(themeResource);
                                        }
                                        CreateBaseContextForActivity.mCalled = false;
                                        if (activityClientRecord.isPersistable()) {
                                            activityThread = this;
                                            activityThread.mInstrumentation.callActivityOnCreate(CreateBaseContextForActivity, activityClientRecord2.state, activityClientRecord2.persistentState);
                                        } else {
                                            activityThread = this;
                                            activityThread.mInstrumentation.callActivityOnCreate(CreateBaseContextForActivity, activityClientRecord2.state);
                                        }
                                        if (!CreateBaseContextForActivity.mCalled) {
                                            throw new SuperNotCalledException("Activity " + activityClientRecord2.intent.getComponent().toShortString() + " did not call through to super.onCreate()");
                                        }
                                        activityClientRecord2.activity = CreateBaseContextForActivity;
                                        i = 1;
                                        r4 = CreateBaseContextForActivity;
                                    } catch (Exception e4) {
                                        e = e4;
                                        r3 = this;
                                        CreateBaseContextForActivity = CreateBaseContextForActivity;
                                    }
                                } catch (Exception e5) {
                                    e = e5;
                                    r3 = this;
                                    str = ": ";
                                    zOnException = r3.mInstrumentation.onException(CreateBaseContextForActivity, e);
                                    r2 = CreateBaseContextForActivity;
                                    if (!zOnException) {
                                        throw new RuntimeException("Unable to start activity " + componentName + str + e.toString(), e);
                                    }
                                    return r2;
                                }
                            } catch (SuperNotCalledException e6) {
                                throw e6;
                            }
                        } catch (Exception e7) {
                            e = e7;
                            r3 = this;
                        }
                    } else {
                        r4 = CreateBaseContextForActivity;
                        activityClientRecord2 = activityClientRecord;
                        activityThread = this;
                        i = 1;
                    }
                    activityClientRecord2.setState(i);
                    activityThread.mActivities.put(activityClientRecord2.token, activityClientRecord2);
                    r2 = r4;
                } catch (SuperNotCalledException e8) {
                    throw e8;
                } catch (Exception e9) {
                    e = e9;
                }
            } catch (Exception e10) {
                e = e10;
                CreateBaseContextForActivity = CreateBaseContextForActivity;
                componentName = componentName;
                str = ": ";
                r3 = this;
            }
            return r2;
        } catch (SuperNotCalledException e11) {
            throw e11;
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleStartActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions) {
        Activity activity = activityClientRecord.activity;
        if (activityClientRecord.activity == null) {
            return;
        }
        if (!activityClientRecord.stopped) {
            throw new IllegalStateException("Can't start activity that is not stopped.");
        }
        if (activityClientRecord.activity.mFinished) {
            return;
        }
        activity.performStart("handleStartActivity");
        activityClientRecord.setState(2);
        if (pendingTransactionActions == null) {
            return;
        }
        if (pendingTransactionActions.shouldRestoreInstanceState()) {
            if (activityClientRecord.isPersistable()) {
                if (activityClientRecord.state != null || activityClientRecord.persistentState != null) {
                    this.mInstrumentation.callActivityOnRestoreInstanceState(activity, activityClientRecord.state, activityClientRecord.persistentState);
                }
            } else if (activityClientRecord.state != null) {
                this.mInstrumentation.callActivityOnRestoreInstanceState(activity, activityClientRecord.state);
            }
        }
        if (pendingTransactionActions.shouldCallOnPostCreate()) {
            activity.mCalled = false;
            if (activityClientRecord.isPersistable()) {
                this.mInstrumentation.callActivityOnPostCreate(activity, activityClientRecord.state, activityClientRecord.persistentState);
            } else {
                this.mInstrumentation.callActivityOnPostCreate(activity, activityClientRecord.state);
            }
            if (activity.mCalled) {
                return;
            }
            throw new SuperNotCalledException("Activity " + activityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPostCreate()");
        }
    }

    private void checkAndBlockForNetworkAccess() {
        synchronized (this.mNetworkPolicyLock) {
            if (this.mNetworkBlockSeq != -1) {
                try {
                    ActivityManager.getService().waitForNetworkStateUpdate(this.mNetworkBlockSeq);
                    this.mNetworkBlockSeq = -1L;
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private ContextImpl createBaseContextForActivity(ActivityClientRecord activityClientRecord) {
        try {
            ContextImpl contextImplCreateActivityContext = ContextImpl.createActivityContext(this, activityClientRecord.packageInfo, activityClientRecord.activityInfo, activityClientRecord.token, ActivityManager.getService().getActivityDisplayId(activityClientRecord.token), activityClientRecord.overrideConfig);
            DisplayManagerGlobal displayManagerGlobal = DisplayManagerGlobal.getInstance();
            String str = SystemProperties.get("debug.second-display.pkg");
            if (str == null || str.isEmpty() || !activityClientRecord.packageInfo.mPackageName.contains(str)) {
                return contextImplCreateActivityContext;
            }
            for (int i : displayManagerGlobal.getDisplayIds()) {
                if (i != 0) {
                    return (ContextImpl) contextImplCreateActivityContext.createDisplayContext(displayManagerGlobal.getCompatibleDisplay(i, contextImplCreateActivityContext.getResources()));
                }
            }
            return contextImplCreateActivityContext;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.app.ClientTransactionHandler
    public Activity handleLaunchActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions, Intent intent) {
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        if (activityClientRecord.profilerInfo != null) {
            this.mProfiler.setProfiler(activityClientRecord.profilerInfo);
            this.mProfiler.startProfiling();
        }
        handleConfigurationChanged(null, null);
        if (!ThreadedRenderer.sRendererDisabled) {
            GraphicsEnvironment.earlyInitEGL();
        }
        WindowManagerGlobal.initialize();
        Activity activityPerformLaunchActivity = performLaunchActivity(activityClientRecord, intent);
        if (activityPerformLaunchActivity != null) {
            activityClientRecord.createdConfig = new Configuration(this.mConfiguration);
            reportSizeConfigurations(activityClientRecord);
            if (!activityClientRecord.activity.mFinished && pendingTransactionActions != null) {
                pendingTransactionActions.setOldState(activityClientRecord.state);
                pendingTransactionActions.setRestoreInstanceState(true);
                pendingTransactionActions.setCallOnPostCreate(true);
            }
        } else {
            try {
                ActivityManager.getService().finishActivity(activityClientRecord.token, 0, null, 0);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return activityPerformLaunchActivity;
    }

    private void reportSizeConfigurations(ActivityClientRecord activityClientRecord) {
        Configuration[] sizeConfigurations = activityClientRecord.activity.getResources().getSizeConfigurations();
        if (sizeConfigurations == null) {
            return;
        }
        SparseIntArray sparseIntArray = new SparseIntArray();
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        SparseIntArray sparseIntArray3 = new SparseIntArray();
        for (int length = sizeConfigurations.length - 1; length >= 0; length--) {
            Configuration configuration = sizeConfigurations[length];
            if (configuration.screenHeightDp != 0) {
                sparseIntArray2.put(configuration.screenHeightDp, 0);
            }
            if (configuration.screenWidthDp != 0) {
                sparseIntArray.put(configuration.screenWidthDp, 0);
            }
            if (configuration.smallestScreenWidthDp != 0) {
                sparseIntArray3.put(configuration.smallestScreenWidthDp, 0);
            }
        }
        try {
            ActivityManager.getService().reportSizeConfigurations(activityClientRecord.token, sparseIntArray.copyKeys(), sparseIntArray2.copyKeys(), sparseIntArray3.copyKeys());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private void deliverNewIntents(ActivityClientRecord activityClientRecord, List<ReferrerIntent> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ReferrerIntent referrerIntent = list.get(i);
            referrerIntent.setExtrasClassLoader(activityClientRecord.activity.getClassLoader());
            referrerIntent.prepareToEnterProcess();
            activityClientRecord.activity.mFragments.noteStateNotSaved();
            this.mInstrumentation.callActivityOnNewIntent(activityClientRecord.activity, referrerIntent);
        }
    }

    void performNewIntents(IBinder iBinder, List<ReferrerIntent> list, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            return;
        }
        boolean z2 = !activityClientRecord.paused;
        if (z2) {
            activityClientRecord.activity.mTemporaryPause = true;
            this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
        }
        checkAndBlockForNetworkAccess();
        deliverNewIntents(activityClientRecord, list);
        if (z2) {
            activityClientRecord.activity.performResume(false, "performNewIntents");
            activityClientRecord.activity.mTemporaryPause = false;
        }
        if (activityClientRecord.paused && z) {
            performResumeActivity(iBinder, false, "performNewIntents");
            performPauseActivityIfNeeded(activityClientRecord, "performNewIntents");
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleNewIntent(IBinder iBinder, List<ReferrerIntent> list, boolean z) {
        performNewIntents(iBinder, list, z);
    }

    public void handleRequestAssistContextExtras(RequestAssistContextExtras requestAssistContextExtras) {
        Uri uriOnProvideReferrer;
        boolean z = requestAssistContextExtras.requestType == 2;
        if (this.mLastSessionId != requestAssistContextExtras.sessionId) {
            this.mLastSessionId = requestAssistContextExtras.sessionId;
            for (int size = this.mLastAssistStructures.size() - 1; size >= 0; size--) {
                AssistStructure assistStructure = this.mLastAssistStructures.get(size).get();
                if (assistStructure != null) {
                    assistStructure.clearSendChannel();
                }
                this.mLastAssistStructures.remove(size);
            }
        }
        Bundle bundle = new Bundle();
        AssistStructure assistStructure2 = null;
        AssistContent assistContent = z ? null : new AssistContent();
        long jUptimeMillis = SystemClock.uptimeMillis();
        ActivityClientRecord activityClientRecord = this.mActivities.get(requestAssistContextExtras.activityToken);
        if (activityClientRecord != null) {
            if (z) {
                uriOnProvideReferrer = null;
            } else {
                activityClientRecord.activity.getApplication().dispatchOnProvideAssistData(activityClientRecord.activity, bundle);
                activityClientRecord.activity.onProvideAssistData(bundle);
                uriOnProvideReferrer = activityClientRecord.activity.onProvideReferrer();
            }
            if (requestAssistContextExtras.requestType == 1 || z) {
                assistStructure2 = new AssistStructure(activityClientRecord.activity, z, requestAssistContextExtras.flags);
                Intent intent = activityClientRecord.activity.getIntent();
                boolean z2 = activityClientRecord.window == null || (activityClientRecord.window.getAttributes().flags & 8192) == 0;
                if (intent == null || !z2) {
                    if (!z) {
                        assistContent.setDefaultIntent(new Intent());
                    }
                } else if (!z) {
                    Intent intent2 = new Intent(intent);
                    intent2.setFlags(intent2.getFlags() & (-67));
                    intent2.removeUnsafeExtras();
                    assistContent.setDefaultIntent(intent2);
                }
                if (!z) {
                    activityClientRecord.activity.onProvideAssistContent(assistContent);
                }
            }
        } else {
            uriOnProvideReferrer = null;
        }
        AssistStructure assistStructure3 = assistStructure2 == null ? new AssistStructure() : assistStructure2;
        assistStructure3.setAcquisitionStartTime(jUptimeMillis);
        assistStructure3.setAcquisitionEndTime(SystemClock.uptimeMillis());
        this.mLastAssistStructures.add(new WeakReference<>(assistStructure3));
        try {
            ActivityManager.getService().reportAssistContextExtras(requestAssistContextExtras.requestToken, bundle, assistStructure3, assistContent, uriOnProvideReferrer);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void handleTranslucentConversionComplete(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            activityClientRecord.activity.onTranslucentConversionComplete(z);
        }
    }

    public void onNewActivityOptions(IBinder iBinder, ActivityOptions activityOptions) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            activityClientRecord.activity.onNewActivityOptions(activityOptions);
        }
    }

    public void handleInstallProvider(ProviderInfo providerInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            installContentProviders(this.mInitialApplication, Arrays.asList(providerInfo));
        } finally {
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEnterAnimationComplete(IBinder iBinder) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            activityClientRecord.activity.dispatchEnterAnimationComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStartBinderTracking() {
        Binder.enableTracing();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopBinderTrackingAndDump(ParcelFileDescriptor parcelFileDescriptor) {
        try {
            Binder.disableTracing();
            Binder.getTransactionTracker().writeTracesToFile(parcelFileDescriptor);
        } finally {
            IoUtils.closeQuietly(parcelFileDescriptor);
            Binder.getTransactionTracker().clearTraces();
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleMultiWindowModeChanged(IBinder iBinder, boolean z, Configuration configuration) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            Configuration configuration2 = new Configuration(this.mConfiguration);
            if (configuration != null) {
                configuration2.updateFrom(configuration);
            }
            activityClientRecord.activity.dispatchMultiWindowModeChanged(z, configuration2);
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handlePictureInPictureModeChanged(IBinder iBinder, boolean z, Configuration configuration) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            Configuration configuration2 = new Configuration(this.mConfiguration);
            if (configuration != null) {
                configuration2.updateFrom(configuration);
            }
            activityClientRecord.activity.dispatchPictureInPictureModeChanged(z, configuration2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLocalVoiceInteractionStarted(IBinder iBinder, IVoiceInteractor iVoiceInteractor) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            activityClientRecord.voiceInteractor = iVoiceInteractor;
            activityClientRecord.activity.setVoiceInteractor(iVoiceInteractor);
            if (iVoiceInteractor == null) {
                activityClientRecord.activity.onLocalVoiceInteractionStopped();
            } else {
                activityClientRecord.activity.onLocalVoiceInteractionStarted();
            }
        }
    }

    private static boolean attemptAttachAgent(String str, ClassLoader classLoader) {
        try {
            VMDebug.attachAgent(str, classLoader);
            return true;
        } catch (IOException unused) {
            Slog.e(TAG, "Attaching agent with " + classLoader + " failed: " + str);
            return false;
        }
    }

    static void handleAttachAgent(String str, LoadedApk loadedApk) {
        ClassLoader classLoader = loadedApk != null ? loadedApk.getClassLoader() : null;
        if (attemptAttachAgent(str, classLoader) || classLoader == null) {
            return;
        }
        attemptAttachAgent(str, null);
    }

    public static Intent getIntentBeingBroadcast() {
        return sCurrentBroadcastIntent.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleReceiver(ReceiverData receiverData) {
        unscheduleGcIdler();
        String className = receiverData.intent.getComponent().getClassName();
        LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(receiverData.info.applicationInfo, receiverData.compatInfo);
        IActivityManager service = ActivityManager.getService();
        try {
            ContextImpl contextImpl = (ContextImpl) packageInfoNoCheck.makeApplication(false, this.mInstrumentation).getBaseContext();
            if (receiverData.info.splitName != null) {
                contextImpl = (ContextImpl) contextImpl.createContextForSplit(receiverData.info.splitName);
            }
            ClassLoader classLoader = contextImpl.getClassLoader();
            receiverData.intent.setExtrasClassLoader(classLoader);
            receiverData.intent.prepareToEnterProcess();
            receiverData.setExtrasClassLoader(classLoader);
            BroadcastReceiver broadcastReceiverInstantiateReceiver = packageInfoNoCheck.getAppFactory().instantiateReceiver(classLoader, receiverData.info.name, receiverData.intent);
            try {
                try {
                    sCurrentBroadcastIntent.set(receiverData.intent);
                    broadcastReceiverInstantiateReceiver.setPendingResult(receiverData);
                    broadcastReceiverInstantiateReceiver.onReceive(contextImpl.getReceiverRestrictedContext(), receiverData.intent);
                } catch (Exception e) {
                    receiverData.sendFinished(service);
                    if (!this.mInstrumentation.onException(broadcastReceiverInstantiateReceiver, e)) {
                        throw new RuntimeException("Unable to start receiver " + className + ": " + e.toString(), e);
                    }
                }
                sCurrentBroadcastIntent.set(null);
                if (broadcastReceiverInstantiateReceiver.getPendingResult() != null) {
                    receiverData.finish();
                }
            } catch (Throwable th) {
                sCurrentBroadcastIntent.set(null);
                throw th;
            }
        } catch (Exception e2) {
            receiverData.sendFinished(service);
            throw new RuntimeException("Unable to instantiate receiver " + className + ": " + e2.toString(), e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void handleCreateBackupAgent(CreateBackupAgentData createBackupAgentData) {
        IBinder iBinderOnBind;
        try {
            if (getPackageManager().getPackageInfo(createBackupAgentData.appInfo.packageName, 0, UserHandle.myUserId()).applicationInfo.uid != Process.myUid()) {
                Slog.w(TAG, "Asked to instantiate non-matching package " + createBackupAgentData.appInfo.packageName);
                return;
            }
            unscheduleGcIdler();
            LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(createBackupAgentData.appInfo, createBackupAgentData.compatInfo);
            String str = packageInfoNoCheck.mPackageName;
            if (str == null) {
                Slog.d(TAG, "Asked to create backup agent for nonexistent package");
                return;
            }
            String str2 = createBackupAgentData.appInfo.backupAgentName;
            if (str2 == null && (createBackupAgentData.backupMode == 1 || createBackupAgentData.backupMode == 3)) {
                str2 = "android.app.backup.FullBackupAgent";
            }
            IBinder iBinderOnBind2 = null;
            try {
                BackupAgent backupAgent = this.mBackupAgents.get(str);
                if (backupAgent != null) {
                    iBinderOnBind = backupAgent.onBind();
                } else {
                    try {
                        BackupAgent backupAgent2 = (BackupAgent) packageInfoNoCheck.getClassLoader().loadClass(str2).newInstance();
                        ContextImpl contextImplCreateAppContext = ContextImpl.createAppContext(this, packageInfoNoCheck);
                        contextImplCreateAppContext.setOuterContext(backupAgent2);
                        backupAgent2.attach(contextImplCreateAppContext);
                        backupAgent2.onCreate();
                        iBinderOnBind2 = backupAgent2.onBind();
                        this.mBackupAgents.put(str, backupAgent2);
                    } catch (Exception e) {
                        Slog.e(TAG, "Agent threw during creation: " + e);
                        if (createBackupAgentData.backupMode != 2 && createBackupAgentData.backupMode != 3) {
                            throw e;
                        }
                    }
                    iBinderOnBind = iBinderOnBind2;
                }
                try {
                    ActivityManager.getService().backupAgentCreated(str, iBinderOnBind);
                } catch (RemoteException e2) {
                    throw e2.rethrowFromSystemServer();
                }
            } catch (Exception e3) {
                throw new RuntimeException("Unable to create BackupAgent " + str2 + ": " + e3.toString(), e3);
            }
        } catch (RemoteException e4) {
            throw e4.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyBackupAgent(CreateBackupAgentData createBackupAgentData) {
        String str = getPackageInfoNoCheck(createBackupAgentData.appInfo, createBackupAgentData.compatInfo).mPackageName;
        BackupAgent backupAgent = this.mBackupAgents.get(str);
        if (backupAgent != null) {
            try {
                backupAgent.onDestroy();
            } catch (Exception e) {
                Slog.w(TAG, "Exception thrown in onDestroy by backup agent of " + createBackupAgentData.appInfo);
                e.printStackTrace();
            }
            this.mBackupAgents.remove(str);
            return;
        }
        Slog.w(TAG, "Attempt to destroy unknown backup agent " + createBackupAgentData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCreateService(CreateServiceData createServiceData) {
        unscheduleGcIdler();
        LoadedApk packageInfoNoCheck = getPackageInfoNoCheck(createServiceData.info.applicationInfo, createServiceData.compatInfo);
        Service serviceInstantiateService = null;
        try {
            serviceInstantiateService = packageInfoNoCheck.getAppFactory().instantiateService(packageInfoNoCheck.getClassLoader(), createServiceData.info.name, createServiceData.intent);
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(null, e)) {
                throw new RuntimeException("Unable to instantiate service " + createServiceData.info.name + ": " + e.toString(), e);
            }
        }
        try {
            ContextImpl contextImplCreateAppContext = ContextImpl.createAppContext(this, packageInfoNoCheck);
            contextImplCreateAppContext.setOuterContext(serviceInstantiateService);
            serviceInstantiateService.attach(contextImplCreateAppContext, this, createServiceData.info.name, createServiceData.token, packageInfoNoCheck.makeApplication(false, this.mInstrumentation), ActivityManager.getService());
            serviceInstantiateService.onCreate();
            this.mServices.put(createServiceData.token, serviceInstantiateService);
            try {
                ActivityManager.getService().serviceDoneExecuting(createServiceData.token, 0, 0, 0);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        } catch (Exception e3) {
            if (this.mInstrumentation.onException(serviceInstantiateService, e3)) {
                return;
            }
            throw new RuntimeException("Unable to create service " + createServiceData.info.name + ": " + e3.toString(), e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBindService(BindServiceData bindServiceData) {
        Service service = this.mServices.get(bindServiceData.token);
        if (service != null) {
            try {
                bindServiceData.intent.setExtrasClassLoader(service.getClassLoader());
                bindServiceData.intent.prepareToEnterProcess();
                try {
                    if (!bindServiceData.rebind) {
                        ActivityManager.getService().publishService(bindServiceData.token, bindServiceData.intent, service.onBind(bindServiceData.intent));
                    } else {
                        service.onRebind(bindServiceData.intent);
                        ActivityManager.getService().serviceDoneExecuting(bindServiceData.token, 0, 0, 0);
                    }
                    ensureJitEnabled();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(service, e2)) {
                    return;
                }
                throw new RuntimeException("Unable to bind to service " + service + " with " + bindServiceData.intent + ": " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUnbindService(BindServiceData bindServiceData) {
        Service service = this.mServices.get(bindServiceData.token);
        if (service != null) {
            try {
                bindServiceData.intent.setExtrasClassLoader(service.getClassLoader());
                bindServiceData.intent.prepareToEnterProcess();
                boolean zOnUnbind = service.onUnbind(bindServiceData.intent);
                try {
                    if (zOnUnbind) {
                        ActivityManager.getService().unbindFinished(bindServiceData.token, bindServiceData.intent, zOnUnbind);
                    } else {
                        ActivityManager.getService().serviceDoneExecuting(bindServiceData.token, 0, 0, 0);
                    }
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(service, e2)) {
                    return;
                }
                throw new RuntimeException("Unable to unbind to service " + service + " with " + bindServiceData.intent + ": " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpService(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            Service service = this.mServices.get(dumpComponentInfo.token);
            if (service != null) {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                service.dump(dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpActivity(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            ActivityClientRecord activityClientRecord = this.mActivities.get(dumpComponentInfo.token);
            if (activityClientRecord != null && activityClientRecord.activity != null) {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                activityClientRecord.activity.dump(dumpComponentInfo.prefix, dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDumpProvider(DumpComponentInfo dumpComponentInfo) {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            ProviderClientRecord providerClientRecord = this.mLocalProviders.get(dumpComponentInfo.token);
            if (providerClientRecord != null && providerClientRecord.mLocalProvider != null) {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(new FileOutputStream(dumpComponentInfo.fd.getFileDescriptor()));
                providerClientRecord.mLocalProvider.dump(dumpComponentInfo.fd.getFileDescriptor(), fastPrintWriter, dumpComponentInfo.args);
                fastPrintWriter.flush();
            }
        } finally {
            IoUtils.closeQuietly(dumpComponentInfo.fd);
            StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceArgs(ServiceArgsData serviceArgsData) {
        int iOnStartCommand;
        Service service = this.mServices.get(serviceArgsData.token);
        if (service != null) {
            try {
                if (serviceArgsData.args != null) {
                    serviceArgsData.args.setExtrasClassLoader(service.getClassLoader());
                    serviceArgsData.args.prepareToEnterProcess();
                }
                if (!serviceArgsData.taskRemoved) {
                    iOnStartCommand = service.onStartCommand(serviceArgsData.args, serviceArgsData.flags, serviceArgsData.startId);
                } else {
                    service.onTaskRemoved(serviceArgsData.args);
                    iOnStartCommand = 1000;
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManager.getService().serviceDoneExecuting(serviceArgsData.token, 1, serviceArgsData.startId, iOnStartCommand);
                    ensureJitEnabled();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(service, e2)) {
                    return;
                }
                throw new RuntimeException("Unable to start service " + service + " with " + serviceArgsData.args + ": " + e2.toString(), e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopService(IBinder iBinder) {
        Service serviceRemove = this.mServices.remove(iBinder);
        if (serviceRemove != null) {
            try {
                serviceRemove.onDestroy();
                serviceRemove.detachAndCleanUp();
                Context baseContext = serviceRemove.getBaseContext();
                if (baseContext instanceof ContextImpl) {
                    ((ContextImpl) baseContext).scheduleFinalCleanup(serviceRemove.getClassName(), "Service");
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManager.getService().serviceDoneExecuting(iBinder, 2, 0, 0);
                    return;
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(serviceRemove, e2)) {
                    throw new RuntimeException("Unable to stop service " + serviceRemove + ": " + e2.toString(), e2);
                }
                Slog.i(TAG, "handleStopService: exception for " + iBinder, e2);
                return;
            }
        }
        Slog.i(TAG, "handleStopService: token=" + iBinder + " not found.");
    }

    public ActivityClientRecord performResumeActivity(IBinder iBinder, boolean z, String str) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null || activityClientRecord.activity.mFinished) {
            return null;
        }
        if (activityClientRecord.getLifecycleState() == 3) {
            if (!z) {
                IllegalStateException illegalStateException = new IllegalStateException("Trying to resume activity which is already resumed");
                Slog.e(TAG, illegalStateException.getMessage(), illegalStateException);
                Slog.e(TAG, activityClientRecord.getStateString());
            }
            return null;
        }
        if (z) {
            activityClientRecord.hideForNow = false;
            activityClientRecord.activity.mStartedActivity = false;
        }
        try {
            activityClientRecord.activity.onStateNotSaved();
            activityClientRecord.activity.mFragments.noteStateNotSaved();
            checkAndBlockForNetworkAccess();
            if (activityClientRecord.pendingIntents != null) {
                deliverNewIntents(activityClientRecord, activityClientRecord.pendingIntents);
                activityClientRecord.pendingIntents = null;
            }
            if (activityClientRecord.pendingResults != null) {
                deliverResults(activityClientRecord, activityClientRecord.pendingResults, str);
                activityClientRecord.pendingResults = null;
            }
            activityClientRecord.activity.performResume(activityClientRecord.startsNotResumed, str);
            activityClientRecord.state = null;
            activityClientRecord.persistentState = null;
            activityClientRecord.setState(3);
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                throw new RuntimeException("Unable to resume activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
            }
        }
        return activityClientRecord;
    }

    static final void cleanUpPendingRemoveWindows(ActivityClientRecord activityClientRecord, boolean z) {
        if (!activityClientRecord.mPreserveWindow || z) {
            if (activityClientRecord.mPendingRemoveWindow != null) {
                activityClientRecord.mPendingRemoveWindowManager.removeViewImmediate(activityClientRecord.mPendingRemoveWindow.getDecorView());
                IBinder windowToken = activityClientRecord.mPendingRemoveWindow.getDecorView().getWindowToken();
                if (windowToken != null) {
                    WindowManagerGlobal.getInstance().closeAll(windowToken, activityClientRecord.activity.getClass().getName(), "Activity");
                }
            }
            activityClientRecord.mPendingRemoveWindow = null;
            activityClientRecord.mPendingRemoveWindowManager = null;
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleResumeActivity(IBinder iBinder, boolean z, boolean z2, String str) {
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        ActivityClientRecord activityClientRecordPerformResumeActivity = performResumeActivity(iBinder, z, str);
        if (activityClientRecordPerformResumeActivity == null) {
            return;
        }
        Activity activity = activityClientRecordPerformResumeActivity.activity;
        int i = z2 ? 256 : 0;
        boolean zWillActivityBeVisible = !activity.mStartedActivity;
        if (!zWillActivityBeVisible) {
            try {
                zWillActivityBeVisible = ActivityManager.getService().willActivityBeVisible(activity.getActivityToken());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        if (activityClientRecordPerformResumeActivity.window == null && !activity.mFinished && zWillActivityBeVisible) {
            activityClientRecordPerformResumeActivity.window = activityClientRecordPerformResumeActivity.activity.getWindow();
            View decorView = activityClientRecordPerformResumeActivity.window.getDecorView();
            decorView.setVisibility(4);
            WindowManager windowManager = activity.getWindowManager();
            WindowManager.LayoutParams attributes = activityClientRecordPerformResumeActivity.window.getAttributes();
            activity.mDecor = decorView;
            attributes.type = 1;
            attributes.softInputMode |= i;
            if (activityClientRecordPerformResumeActivity.mPreserveWindow) {
                activity.mWindowAdded = true;
                activityClientRecordPerformResumeActivity.mPreserveWindow = false;
                ViewRootImpl viewRootImpl = decorView.getViewRootImpl();
                if (viewRootImpl != null) {
                    viewRootImpl.notifyChildRebuilt();
                }
            }
            if (activity.mVisibleFromClient) {
                if (!activity.mWindowAdded) {
                    activity.mWindowAdded = true;
                    windowManager.addView(decorView, attributes);
                } else {
                    activity.onWindowAttributesChanged(attributes);
                }
            }
        } else if (!zWillActivityBeVisible) {
            activityClientRecordPerformResumeActivity.hideForNow = true;
        }
        cleanUpPendingRemoveWindows(activityClientRecordPerformResumeActivity, false);
        if (!activityClientRecordPerformResumeActivity.activity.mFinished && zWillActivityBeVisible && activityClientRecordPerformResumeActivity.activity.mDecor != null && !activityClientRecordPerformResumeActivity.hideForNow) {
            if (activityClientRecordPerformResumeActivity.newConfig != null) {
                performConfigurationChangedForActivity(activityClientRecordPerformResumeActivity, activityClientRecordPerformResumeActivity.newConfig);
                activityClientRecordPerformResumeActivity.newConfig = null;
            }
            WindowManager.LayoutParams attributes2 = activityClientRecordPerformResumeActivity.window.getAttributes();
            if ((256 & attributes2.softInputMode) != i) {
                attributes2.softInputMode = i | (attributes2.softInputMode & (-257));
                if (activityClientRecordPerformResumeActivity.activity.mVisibleFromClient) {
                    activity.getWindowManager().updateViewLayout(activityClientRecordPerformResumeActivity.window.getDecorView(), attributes2);
                }
            }
            activityClientRecordPerformResumeActivity.activity.mVisibleFromServer = true;
            this.mNumVisibleActivities++;
            if (activityClientRecordPerformResumeActivity.activity.mVisibleFromClient) {
                activityClientRecordPerformResumeActivity.activity.makeVisible();
            }
        }
        activityClientRecordPerformResumeActivity.nextIdle = this.mNewActivities;
        this.mNewActivities = activityClientRecordPerformResumeActivity;
        Looper.myQueue().addIdleHandler(new Idler());
    }

    @Override // android.app.ClientTransactionHandler
    public void handlePauseActivity(IBinder iBinder, boolean z, boolean z2, int i, PendingTransactionActions pendingTransactionActions, String str) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            if (z2) {
                performUserLeavingActivity(activityClientRecord);
            }
            Activity activity = activityClientRecord.activity;
            activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
            performPauseActivity(activityClientRecord, z, str, pendingTransactionActions);
            if (activityClientRecord.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            this.mSomeActivitiesChanged = true;
        }
    }

    final void performUserLeavingActivity(ActivityClientRecord activityClientRecord) {
        this.mInstrumentation.callActivityOnUserLeaving(activityClientRecord.activity);
    }

    final Bundle performPauseActivity(IBinder iBinder, boolean z, String str, PendingTransactionActions pendingTransactionActions) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            return performPauseActivity(activityClientRecord, z, str, pendingTransactionActions);
        }
        return null;
    }

    private Bundle performPauseActivity(ActivityClientRecord activityClientRecord, boolean z, String str, PendingTransactionActions pendingTransactionActions) {
        ArrayList<OnActivityPausedListener> arrayListRemove;
        if (activityClientRecord.paused) {
            if (activityClientRecord.activity.mFinished) {
                return null;
            }
            RuntimeException runtimeException = new RuntimeException("Performing pause of activity that is not resumed: " + activityClientRecord.intent.getComponent().toShortString());
            Slog.e(TAG, runtimeException.getMessage(), runtimeException);
        }
        if (z) {
            activityClientRecord.activity.mFinished = true;
        }
        boolean z2 = !activityClientRecord.activity.mFinished && activityClientRecord.isPreHoneycomb();
        if (z2) {
            callActivityOnSaveInstanceState(activityClientRecord);
        }
        performPauseActivityIfNeeded(activityClientRecord, str);
        synchronized (this.mOnPauseListeners) {
            arrayListRemove = this.mOnPauseListeners.remove(activityClientRecord.activity);
        }
        int size = arrayListRemove != null ? arrayListRemove.size() : 0;
        for (int i = 0; i < size; i++) {
            arrayListRemove.get(i).onPaused(activityClientRecord.activity);
        }
        Bundle oldState = pendingTransactionActions != null ? pendingTransactionActions.getOldState() : null;
        if (oldState != null && activityClientRecord.isPreHoneycomb()) {
            activityClientRecord.state = oldState;
        }
        if (z2) {
            return activityClientRecord.state;
        }
        return null;
    }

    private void performPauseActivityIfNeeded(ActivityClientRecord activityClientRecord, String str) {
        if (activityClientRecord.paused) {
            return;
        }
        try {
            activityClientRecord.activity.mCalled = false;
            this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
            if (!activityClientRecord.activity.mCalled) {
                throw new SuperNotCalledException("Activity " + safeToComponentShortString(activityClientRecord.intent) + " did not call through to super.onPause()");
            }
            activityClientRecord.setState(4);
        } catch (SuperNotCalledException e) {
            throw e;
        } catch (Exception e2) {
            if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                throw new RuntimeException("Unable to pause activity " + safeToComponentShortString(activityClientRecord.intent) + ": " + e2.toString(), e2);
            }
        }
    }

    final void performStopActivity(IBinder iBinder, boolean z, String str) {
        performStopActivityInner(this.mActivities.get(iBinder), null, false, z, false, str);
    }

    private static final class ProviderRefCount {
        public final ProviderClientRecord client;
        public final ContentProviderHolder holder;
        public boolean removePending;
        public int stableCount;
        public int unstableCount;

        ProviderRefCount(ContentProviderHolder contentProviderHolder, ProviderClientRecord providerClientRecord, int i, int i2) {
            this.holder = contentProviderHolder;
            this.client = providerClientRecord;
            this.stableCount = i;
            this.unstableCount = i2;
        }
    }

    private void performStopActivityInner(ActivityClientRecord activityClientRecord, PendingTransactionActions.StopInfo stopInfo, boolean z, boolean z2, boolean z3, String str) {
        if (activityClientRecord != null) {
            if (!z && activityClientRecord.stopped) {
                if (activityClientRecord.activity.mFinished) {
                    return;
                }
                if (!z3) {
                    RuntimeException runtimeException = new RuntimeException("Performing stop of activity that is already stopped: " + activityClientRecord.intent.getComponent().toShortString());
                    Slog.e(TAG, runtimeException.getMessage(), runtimeException);
                    Slog.e(TAG, activityClientRecord.getStateString());
                }
            }
            performPauseActivityIfNeeded(activityClientRecord, str);
            if (stopInfo != null) {
                try {
                    stopInfo.setDescription(activityClientRecord.activity.onCreateDescription());
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                        throw new RuntimeException("Unable to save state of activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
            }
            if (z) {
                return;
            }
            callActivityOnStop(activityClientRecord, z2, str);
        }
    }

    private void callActivityOnStop(ActivityClientRecord activityClientRecord, boolean z, String str) {
        boolean z2 = z && !activityClientRecord.activity.mFinished && activityClientRecord.state == null && !activityClientRecord.isPreHoneycomb();
        boolean zIsPreP = activityClientRecord.isPreP();
        if (z2 && zIsPreP) {
            callActivityOnSaveInstanceState(activityClientRecord);
        }
        try {
            activityClientRecord.activity.performStop(false, str);
        } catch (SuperNotCalledException e) {
            throw e;
        } catch (Exception e2) {
            if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                throw new RuntimeException("Unable to stop activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
            }
        }
        activityClientRecord.setState(5);
        if (!z2 || zIsPreP) {
            return;
        }
        callActivityOnSaveInstanceState(activityClientRecord);
    }

    private void updateVisibility(ActivityClientRecord activityClientRecord, boolean z) {
        View view = activityClientRecord.activity.mDecor;
        if (view != null) {
            if (z) {
                if (!activityClientRecord.activity.mVisibleFromServer) {
                    activityClientRecord.activity.mVisibleFromServer = true;
                    this.mNumVisibleActivities++;
                    if (activityClientRecord.activity.mVisibleFromClient) {
                        activityClientRecord.activity.makeVisible();
                    }
                }
                if (activityClientRecord.newConfig != null) {
                    performConfigurationChangedForActivity(activityClientRecord, activityClientRecord.newConfig);
                    activityClientRecord.newConfig = null;
                    return;
                }
                return;
            }
            if (activityClientRecord.activity.mVisibleFromServer) {
                activityClientRecord.activity.mVisibleFromServer = false;
                this.mNumVisibleActivities--;
                view.setVisibility(4);
            }
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleStopActivity(IBinder iBinder, boolean z, int i, PendingTransactionActions pendingTransactionActions, boolean z2, String str) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        Activity activity = activityClientRecord.activity;
        activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
        PendingTransactionActions.StopInfo stopInfo = new PendingTransactionActions.StopInfo();
        performStopActivityInner(activityClientRecord, stopInfo, z, true, z2, str);
        updateVisibility(activityClientRecord, z);
        if (!activityClientRecord.isPreHoneycomb()) {
            QueuedWork.waitToFinish();
        }
        stopInfo.setActivity(activityClientRecord);
        stopInfo.setState(activityClientRecord.state);
        stopInfo.setPersistentState(activityClientRecord.persistentState);
        pendingTransactionActions.setStopInfo(stopInfo);
        this.mSomeActivitiesChanged = true;
    }

    @Override // android.app.ClientTransactionHandler
    public void reportStop(PendingTransactionActions pendingTransactionActions) {
        this.mH.post(pendingTransactionActions.getStopInfo());
    }

    @Override // android.app.ClientTransactionHandler
    public void performRestartActivity(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord.stopped) {
            activityClientRecord.activity.performRestart(z, "performRestartActivity");
            if (z) {
                activityClientRecord.setState(2);
            }
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleWindowVisibility(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            Log.w(TAG, "handleWindowVisibility: no activity for token " + iBinder);
            return;
        }
        if (!z && !activityClientRecord.stopped) {
            performStopActivityInner(activityClientRecord, null, z, false, false, "handleWindowVisibility");
        } else if (z && activityClientRecord.stopped) {
            unscheduleGcIdler();
            activityClientRecord.activity.performRestart(true, "handleWindowVisibility");
            activityClientRecord.setState(2);
        }
        if (activityClientRecord.activity.mDecor != null) {
            updateVisibility(activityClientRecord, z);
        }
        this.mSomeActivitiesChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSleeping(IBinder iBinder, boolean z) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            Log.w(TAG, "handleSleeping: no activity for token " + iBinder);
            return;
        }
        if (z) {
            if (!activityClientRecord.stopped && !activityClientRecord.isPreHoneycomb()) {
                callActivityOnStop(activityClientRecord, true, "sleeping");
            }
            if (!activityClientRecord.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManager.getService().activitySlept(activityClientRecord.token);
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        if (activityClientRecord.stopped && activityClientRecord.activity.mVisibleFromServer) {
            activityClientRecord.activity.performRestart(true, "handleSleeping");
            activityClientRecord.setState(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetCoreSettings(Bundle bundle) {
        synchronized (this.mResourcesManager) {
            this.mCoreSettings = bundle;
        }
        onCoreSettingsChange();
    }

    private void onCoreSettingsChange() {
        boolean z = this.mCoreSettings.getInt(Settings.Global.DEBUG_VIEW_ATTRIBUTES, 0) != 0;
        if (z != View.mDebugViewAttributes) {
            View.mDebugViewAttributes = z;
            relaunchAllActivities();
        }
    }

    private void relaunchAllActivities() {
        for (Map.Entry<IBinder, ActivityClientRecord> entry : this.mActivities.entrySet()) {
            if (!entry.getValue().activity.mFinished) {
                scheduleRelaunchActivity(entry.getKey());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdatePackageCompatibilityInfo(UpdateCompatibilityData updateCompatibilityData) {
        LoadedApk loadedApkPeekPackageInfo = peekPackageInfo(updateCompatibilityData.pkg, false);
        if (loadedApkPeekPackageInfo != null) {
            loadedApkPeekPackageInfo.setCompatibilityInfo(updateCompatibilityData.info);
        }
        LoadedApk loadedApkPeekPackageInfo2 = peekPackageInfo(updateCompatibilityData.pkg, true);
        if (loadedApkPeekPackageInfo2 != null) {
            loadedApkPeekPackageInfo2.setCompatibilityInfo(updateCompatibilityData.info);
        }
        handleConfigurationChanged(this.mConfiguration, updateCompatibilityData.info);
        WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
    }

    private void deliverResults(ActivityClientRecord activityClientRecord, List<ResultInfo> list, String str) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ResultInfo resultInfo = list.get(i);
            try {
                if (resultInfo.mData != null) {
                    resultInfo.mData.setExtrasClassLoader(activityClientRecord.activity.getClassLoader());
                    resultInfo.mData.prepareToEnterProcess();
                }
                activityClientRecord.activity.dispatchActivityResult(resultInfo.mResultWho, resultInfo.mRequestCode, resultInfo.mResultCode, resultInfo.mData, str);
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                    throw new RuntimeException("Failure delivering result " + resultInfo + " to activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleSendResult(IBinder iBinder, List<ResultInfo> list, String str) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            boolean z = !activityClientRecord.paused;
            if (!activityClientRecord.activity.mFinished && activityClientRecord.activity.mDecor != null && activityClientRecord.hideForNow && z) {
                updateVisibility(activityClientRecord, true);
            }
            if (z) {
                try {
                    activityClientRecord.activity.mCalled = false;
                    activityClientRecord.activity.mTemporaryPause = true;
                    this.mInstrumentation.callActivityOnPause(activityClientRecord.activity);
                    if (!activityClientRecord.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + activityClientRecord.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            checkAndBlockForNetworkAccess();
            deliverResults(activityClientRecord, list, str);
            if (z) {
                activityClientRecord.activity.performResume(false, str);
                activityClientRecord.activity.mTemporaryPause = false;
            }
        }
    }

    ActivityClientRecord performDestroyActivity(IBinder iBinder, boolean z, int i, boolean z2, String str) {
        Class<?> cls;
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord != null) {
            cls = activityClientRecord.activity.getClass();
            Activity activity = activityClientRecord.activity;
            activity.mConfigChangeFlags = i | activity.mConfigChangeFlags;
            if (z) {
                activityClientRecord.activity.mFinished = true;
            }
            performPauseActivityIfNeeded(activityClientRecord, "destroy");
            if (!activityClientRecord.stopped) {
                callActivityOnStop(activityClientRecord, false, "destroy");
            }
            if (z2) {
                try {
                    activityClientRecord.lastNonConfigurationInstances = activityClientRecord.activity.retainNonConfigurationInstances();
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(activityClientRecord.activity, e)) {
                        throw new RuntimeException("Unable to retain activity " + activityClientRecord.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
            }
            try {
                activityClientRecord.activity.mCalled = false;
                this.mInstrumentation.callActivityOnDestroy(activityClientRecord.activity);
                if (!activityClientRecord.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(activityClientRecord.intent) + " did not call through to super.onDestroy()");
                }
                if (activityClientRecord.window != null) {
                    activityClientRecord.window.closeAllPanels();
                }
                activityClientRecord.setState(6);
            } catch (SuperNotCalledException e2) {
                throw e2;
            } catch (Exception e3) {
                if (!this.mInstrumentation.onException(activityClientRecord.activity, e3)) {
                    throw new RuntimeException("Unable to destroy activity " + safeToComponentShortString(activityClientRecord.intent) + ": " + e3.toString(), e3);
                }
            }
        } else {
            cls = null;
        }
        this.mActivities.remove(iBinder);
        StrictMode.decrementExpectedActivityCount(cls);
        return activityClientRecord;
    }

    private static String safeToComponentShortString(Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? "[Unknown]" : component.toShortString();
    }

    @Override // android.app.ClientTransactionHandler
    public void handleDestroyActivity(IBinder iBinder, boolean z, int i, boolean z2, String str) {
        ActivityClientRecord activityClientRecordPerformDestroyActivity = performDestroyActivity(iBinder, z, i, z2, str);
        if (activityClientRecordPerformDestroyActivity != null) {
            cleanUpPendingRemoveWindows(activityClientRecordPerformDestroyActivity, z);
            WindowManager windowManager = activityClientRecordPerformDestroyActivity.activity.getWindowManager();
            View view = activityClientRecordPerformDestroyActivity.activity.mDecor;
            if (view != null) {
                if (activityClientRecordPerformDestroyActivity.activity.mVisibleFromServer) {
                    this.mNumVisibleActivities--;
                }
                IBinder windowToken = view.getWindowToken();
                if (activityClientRecordPerformDestroyActivity.activity.mWindowAdded) {
                    if (activityClientRecordPerformDestroyActivity.mPreserveWindow) {
                        activityClientRecordPerformDestroyActivity.mPendingRemoveWindow = activityClientRecordPerformDestroyActivity.window;
                        activityClientRecordPerformDestroyActivity.mPendingRemoveWindowManager = windowManager;
                        activityClientRecordPerformDestroyActivity.window.clearContentView();
                    } else {
                        windowManager.removeViewImmediate(view);
                    }
                }
                if (windowToken != null && activityClientRecordPerformDestroyActivity.mPendingRemoveWindow == null) {
                    WindowManagerGlobal.getInstance().closeAll(windowToken, activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
                } else if (activityClientRecordPerformDestroyActivity.mPendingRemoveWindow != null) {
                    WindowManagerGlobal.getInstance().closeAllExceptView(iBinder, view, activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
                }
                activityClientRecordPerformDestroyActivity.activity.mDecor = null;
            }
            if (activityClientRecordPerformDestroyActivity.mPendingRemoveWindow == null) {
                WindowManagerGlobal.getInstance().closeAll(iBinder, activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
            }
            Context baseContext = activityClientRecordPerformDestroyActivity.activity.getBaseContext();
            if (baseContext instanceof ContextImpl) {
                ((ContextImpl) baseContext).scheduleFinalCleanup(activityClientRecordPerformDestroyActivity.activity.getClass().getName(), "Activity");
            }
        }
        if (z) {
            try {
                ActivityManager.getService().activityDestroyed(iBinder);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        this.mSomeActivitiesChanged = true;
    }

    @Override // android.app.ClientTransactionHandler
    public ActivityClientRecord prepareRelaunchActivity(IBinder iBinder, List<ResultInfo> list, List<ReferrerIntent> list2, int i, MergedConfiguration mergedConfiguration, boolean z) {
        boolean z2;
        ActivityClientRecord activityClientRecord;
        synchronized (this.mResourcesManager) {
            z2 = false;
            int i2 = 0;
            while (true) {
                if (i2 >= this.mRelaunchingActivities.size()) {
                    activityClientRecord = null;
                    break;
                }
                activityClientRecord = this.mRelaunchingActivities.get(i2);
                if (activityClientRecord.token == iBinder) {
                    if (list != null) {
                        if (activityClientRecord.pendingResults != null) {
                            activityClientRecord.pendingResults.addAll(list);
                        } else {
                            activityClientRecord.pendingResults = list;
                        }
                    }
                    if (list2 == null) {
                        break;
                    }
                    if (activityClientRecord.pendingIntents != null) {
                        activityClientRecord.pendingIntents.addAll(list2);
                        break;
                    }
                    activityClientRecord.pendingIntents = list2;
                    break;
                }
                i2++;
            }
            if (activityClientRecord == null) {
                activityClientRecord = new ActivityClientRecord();
                activityClientRecord.token = iBinder;
                activityClientRecord.pendingResults = list;
                activityClientRecord.pendingIntents = list2;
                activityClientRecord.mPreserveWindow = z;
                this.mRelaunchingActivities.add(activityClientRecord);
                z2 = true;
            }
            activityClientRecord.createdConfig = mergedConfiguration.getGlobalConfiguration();
            activityClientRecord.overrideConfig = mergedConfiguration.getOverrideConfiguration();
            activityClientRecord.pendingConfigChanges |= i;
        }
        if (z2) {
            return activityClientRecord;
        }
        return null;
    }

    @Override // android.app.ClientTransactionHandler
    public void handleRelaunchActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions) {
        Configuration configuration;
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        synchronized (this.mResourcesManager) {
            int size = this.mRelaunchingActivities.size();
            IBinder iBinder = activityClientRecord.token;
            int i = 0;
            int i2 = 0;
            ActivityClientRecord activityClientRecord2 = null;
            while (i2 < size) {
                ActivityClientRecord activityClientRecord3 = this.mRelaunchingActivities.get(i2);
                if (activityClientRecord3.token == iBinder) {
                    i |= activityClientRecord3.pendingConfigChanges;
                    this.mRelaunchingActivities.remove(i2);
                    i2--;
                    size--;
                    activityClientRecord2 = activityClientRecord3;
                }
                i2++;
            }
            if (activityClientRecord2 == null) {
                return;
            }
            if (this.mPendingConfiguration != null) {
                configuration = this.mPendingConfiguration;
                this.mPendingConfiguration = null;
            } else {
                configuration = null;
            }
            if (activityClientRecord2.createdConfig != null && ((this.mConfiguration == null || (activityClientRecord2.createdConfig.isOtherSeqNewer(this.mConfiguration) && this.mConfiguration.diff(activityClientRecord2.createdConfig) != 0)) && (configuration == null || activityClientRecord2.createdConfig.isOtherSeqNewer(configuration)))) {
                configuration = activityClientRecord2.createdConfig;
            }
            if (configuration != null) {
                this.mCurDefaultDisplayDpi = configuration.densityDpi;
                updateDefaultDensity();
                handleConfigurationChanged(configuration, null);
            }
            ActivityClientRecord activityClientRecord4 = this.mActivities.get(activityClientRecord2.token);
            if (activityClientRecord4 == null) {
                return;
            }
            activityClientRecord4.activity.mConfigChangeFlags |= i;
            activityClientRecord4.mPreserveWindow = activityClientRecord2.mPreserveWindow;
            activityClientRecord4.activity.mChangingConfigurations = true;
            try {
                if (activityClientRecord4.mPreserveWindow) {
                    WindowManagerGlobal.getWindowSession().prepareToReplaceWindows(activityClientRecord4.token, true);
                }
                handleRelaunchActivityInner(activityClientRecord4, i, activityClientRecord2.pendingResults, activityClientRecord2.pendingIntents, pendingTransactionActions, activityClientRecord2.startsNotResumed, activityClientRecord2.overrideConfig, "handleRelaunchActivity");
                if (pendingTransactionActions != null) {
                    pendingTransactionActions.setReportRelaunchToWindowManager(true);
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    void scheduleRelaunchActivity(IBinder iBinder) {
        sendMessage(160, iBinder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRelaunchActivityLocally(IBinder iBinder) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null) {
            Log.w(TAG, "Activity to relaunch no longer exists");
            return;
        }
        int lifecycleState = activityClientRecord.getLifecycleState();
        if (lifecycleState < 3 || lifecycleState > 5) {
            Log.w(TAG, "Activity state must be in [ON_RESUME..ON_STOP] in order to be relaunched,current state is " + lifecycleState);
            return;
        }
        ActivityRelaunchItem activityRelaunchItemObtain = ActivityRelaunchItem.obtain(null, null, 0, new MergedConfiguration(activityClientRecord.createdConfig != null ? activityClientRecord.createdConfig : this.mConfiguration, activityClientRecord.overrideConfig), activityClientRecord.mPreserveWindow);
        ActivityLifecycleItem lifecycleRequestForCurrentState = TransactionExecutorHelper.getLifecycleRequestForCurrentState(activityClientRecord);
        ClientTransaction clientTransactionObtain = ClientTransaction.obtain(this.mAppThread, activityClientRecord.token);
        clientTransactionObtain.addCallback(activityRelaunchItemObtain);
        clientTransactionObtain.setLifecycleStateRequest(lifecycleRequestForCurrentState);
        executeTransaction(clientTransactionObtain);
    }

    private void handleRelaunchActivityInner(ActivityClientRecord activityClientRecord, int i, List<ResultInfo> list, List<ReferrerIntent> list2, PendingTransactionActions pendingTransactionActions, boolean z, Configuration configuration, String str) {
        Intent intent = activityClientRecord.activity.mIntent;
        if (!activityClientRecord.paused) {
            performPauseActivity(activityClientRecord, false, str, (PendingTransactionActions) null);
        }
        if (!activityClientRecord.stopped) {
            callActivityOnStop(activityClientRecord, true, str);
        }
        handleDestroyActivity(activityClientRecord.token, false, i, true, str);
        activityClientRecord.activity = null;
        activityClientRecord.window = null;
        activityClientRecord.hideForNow = false;
        activityClientRecord.nextIdle = null;
        if (list != null) {
            if (activityClientRecord.pendingResults == null) {
                activityClientRecord.pendingResults = list;
            } else {
                activityClientRecord.pendingResults.addAll(list);
            }
        }
        if (list2 != null) {
            if (activityClientRecord.pendingIntents == null) {
                activityClientRecord.pendingIntents = list2;
            } else {
                activityClientRecord.pendingIntents.addAll(list2);
            }
        }
        activityClientRecord.startsNotResumed = z;
        activityClientRecord.overrideConfig = configuration;
        handleLaunchActivity(activityClientRecord, pendingTransactionActions, intent);
    }

    @Override // android.app.ClientTransactionHandler
    public void reportRelaunch(IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        try {
            ActivityManager.getService().activityRelaunched(iBinder);
            ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
            if (!pendingTransactionActions.shouldReportRelaunchToWindowManager() || activityClientRecord == null || activityClientRecord.window == null) {
                return;
            }
            activityClientRecord.window.reportActivityRelaunched();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private void callActivityOnSaveInstanceState(ActivityClientRecord activityClientRecord) {
        activityClientRecord.state = new Bundle();
        activityClientRecord.state.setAllowFds(false);
        if (activityClientRecord.isPersistable()) {
            activityClientRecord.persistentState = new PersistableBundle();
            this.mInstrumentation.callActivityOnSaveInstanceState(activityClientRecord.activity, activityClientRecord.state, activityClientRecord.persistentState);
        } else {
            this.mInstrumentation.callActivityOnSaveInstanceState(activityClientRecord.activity, activityClientRecord.state);
        }
    }

    ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean z, Configuration configuration) {
        int i;
        ArrayList<ComponentCallbacks2> arrayList = new ArrayList<>();
        synchronized (this.mResourcesManager) {
            int size = this.mAllApplications.size();
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(this.mAllApplications.get(i2));
            }
            int size2 = this.mActivities.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ActivityClientRecord activityClientRecordValueAt = this.mActivities.valueAt(i3);
                Activity activity = activityClientRecordValueAt.activity;
                if (activity != null) {
                    Configuration configurationApplyConfigCompatMainThread = applyConfigCompatMainThread(this.mCurDefaultDisplayDpi, configuration, activityClientRecordValueAt.packageInfo.getCompatibilityInfo());
                    if (!activityClientRecordValueAt.activity.mFinished && (z || !activityClientRecordValueAt.paused)) {
                        arrayList.add(activity);
                    } else if (configurationApplyConfigCompatMainThread != null) {
                        activityClientRecordValueAt.newConfig = configurationApplyConfigCompatMainThread;
                    }
                }
            }
            int size3 = this.mServices.size();
            for (int i4 = 0; i4 < size3; i4++) {
                arrayList.add(this.mServices.valueAt(i4));
            }
        }
        synchronized (this.mProviderMap) {
            int size4 = this.mLocalProviders.size();
            for (i = 0; i < size4; i++) {
                arrayList.add(this.mLocalProviders.valueAt(i).mLocalProvider);
            }
        }
        return arrayList;
    }

    private void performConfigurationChangedForActivity(ActivityClientRecord activityClientRecord, Configuration configuration) {
        performConfigurationChangedForActivity(activityClientRecord, configuration, activityClientRecord.activity.getDisplay().getDisplayId(), false);
    }

    private Configuration performConfigurationChangedForActivity(ActivityClientRecord activityClientRecord, Configuration configuration, int i, boolean z) {
        activityClientRecord.tmpConfig.setTo(configuration);
        if (activityClientRecord.overrideConfig != null) {
            activityClientRecord.tmpConfig.updateFrom(activityClientRecord.overrideConfig);
        }
        Configuration configurationPerformActivityConfigurationChanged = performActivityConfigurationChanged(activityClientRecord.activity, activityClientRecord.tmpConfig, activityClientRecord.overrideConfig, i, z);
        freeTextLayoutCachesIfNeeded(activityClientRecord.activity.mCurrentConfig.diff(activityClientRecord.tmpConfig));
        return configurationPerformActivityConfigurationChanged;
    }

    private static Configuration createNewConfigAndUpdateIfNotNull(Configuration configuration, Configuration configuration2) {
        if (configuration2 == null) {
            return configuration;
        }
        Configuration configuration3 = new Configuration(configuration);
        configuration3.updateFrom(configuration2);
        return configuration3;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void performConfigurationChanged(ComponentCallbacks2 componentCallbacks2, Configuration configuration) {
        componentCallbacks2.onConfigurationChanged(createNewConfigAndUpdateIfNotNull(configuration, componentCallbacks2 instanceof ContextThemeWrapper ? ((ContextThemeWrapper) componentCallbacks2).getOverrideConfiguration() : null));
    }

    private Configuration performActivityConfigurationChanged(Activity activity, Configuration configuration, Configuration configuration2, int i, boolean z) {
        int iDiffPublicOnly;
        if (activity == null) {
            throw new IllegalArgumentException("No activity provided.");
        }
        IBinder activityToken = activity.getActivityToken();
        if (activityToken == null) {
            throw new IllegalArgumentException("Activity token not set. Is the activity attached?");
        }
        boolean z2 = true;
        if (activity.mCurrentConfig != null && (((iDiffPublicOnly = activity.mCurrentConfig.diffPublicOnly(configuration)) == 0 && this.mResourcesManager.isSameResourcesOverrideConfig(activityToken, configuration2)) || (this.mUpdatingSystemConfig && (iDiffPublicOnly & (~activity.mActivityInfo.getRealConfigChanged())) != 0))) {
            z2 = false;
        }
        if (!z2 && !z) {
            return null;
        }
        Configuration overrideConfiguration = activity.getOverrideConfiguration();
        this.mResourcesManager.updateResourcesForActivity(activityToken, createNewConfigAndUpdateIfNotNull(configuration2, overrideConfiguration), i, z);
        activity.mConfigChangeFlags = 0;
        activity.mCurrentConfig = new Configuration(configuration);
        Configuration configurationCreateNewConfigAndUpdateIfNotNull = createNewConfigAndUpdateIfNotNull(configuration, overrideConfiguration);
        if (z) {
            activity.dispatchMovedToDisplay(i, configurationCreateNewConfigAndUpdateIfNotNull);
        }
        if (z2) {
            activity.mCalled = false;
            activity.onConfigurationChanged(configurationCreateNewConfigAndUpdateIfNotNull);
            if (!activity.mCalled) {
                throw new SuperNotCalledException("Activity " + activity.getLocalClassName() + " did not call through to super.onConfigurationChanged()");
            }
        }
        return configurationCreateNewConfigAndUpdateIfNotNull;
    }

    public final void applyConfigurationToResources(Configuration configuration) {
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, null);
        }
    }

    final Configuration applyCompatConfiguration(int i) {
        Configuration configuration = this.mConfiguration;
        if (this.mCompatConfiguration == null) {
            this.mCompatConfiguration = new Configuration();
        }
        this.mCompatConfiguration.setTo(this.mConfiguration);
        return this.mResourcesManager.applyCompatConfigurationLocked(i, this.mCompatConfiguration) ? this.mCompatConfiguration : configuration;
    }

    @Override // android.app.ClientTransactionHandler
    public void handleConfigurationChanged(Configuration configuration) {
        Trace.traceBegin(64L, "configChanged");
        this.mCurDefaultDisplayDpi = configuration.densityDpi;
        this.mUpdatingSystemConfig = true;
        try {
            handleConfigurationChanged(configuration, null);
            this.mUpdatingSystemConfig = false;
            Trace.traceEnd(64L);
        } catch (Throwable th) {
            this.mUpdatingSystemConfig = false;
            throw th;
        }
    }

    private void handleConfigurationChanged(Configuration configuration, CompatibilityInfo compatibilityInfo) {
        Configuration configuration2;
        boolean z = (configuration == null || (configuration2 = this.mConfiguration) == null || configuration2.diffPublicOnly(configuration) != 0) ? false : true;
        Resources.Theme theme = getSystemContext().getTheme();
        Resources.Theme theme2 = getSystemUiContext().getTheme();
        synchronized (this.mResourcesManager) {
            if (this.mPendingConfiguration != null) {
                if (!this.mPendingConfiguration.isOtherSeqNewer(configuration)) {
                    configuration = this.mPendingConfiguration;
                    this.mCurDefaultDisplayDpi = configuration.densityDpi;
                    updateDefaultDensity();
                }
                this.mPendingConfiguration = null;
            }
            if (configuration == null) {
                return;
            }
            this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, compatibilityInfo);
            updateLocaleListFromAppContext(this.mInitialApplication.getApplicationContext(), this.mResourcesManager.getConfiguration().getLocales());
            if (this.mConfiguration == null) {
                this.mConfiguration = new Configuration();
            }
            if (this.mConfiguration.isOtherSeqNewer(configuration) || compatibilityInfo != null) {
                int iUpdateFrom = this.mConfiguration.updateFrom(configuration);
                Configuration configurationApplyCompatConfiguration = applyCompatConfiguration(this.mCurDefaultDisplayDpi);
                if ((theme.getChangingConfigurations() & iUpdateFrom) != 0) {
                    theme.rebase();
                }
                if ((theme2.getChangingConfigurations() & iUpdateFrom) != 0) {
                    theme2.rebase();
                }
                ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(false, configurationApplyCompatConfiguration);
                freeTextLayoutCachesIfNeeded(iUpdateFrom);
                if (arrayListCollectComponentCallbacks != null) {
                    int size = arrayListCollectComponentCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ComponentCallbacks2 componentCallbacks2 = arrayListCollectComponentCallbacks.get(i);
                        if (componentCallbacks2 instanceof Activity) {
                            performConfigurationChangedForActivity(this.mActivities.get(((Activity) componentCallbacks2).getActivityToken()), configurationApplyCompatConfiguration);
                        } else if (!z) {
                            performConfigurationChanged(componentCallbacks2, configurationApplyCompatConfiguration);
                        }
                    }
                }
            }
        }
    }

    void handleApplicationInfoChanged(ApplicationInfo applicationInfo) {
        LoadedApk loadedApk;
        LoadedApk loadedApk2;
        synchronized (this.mResourcesManager) {
            WeakReference<LoadedApk> weakReference = this.mPackages.get(applicationInfo.packageName);
            loadedApk = weakReference != null ? weakReference.get() : null;
            WeakReference<LoadedApk> weakReference2 = this.mResourcePackages.get(applicationInfo.packageName);
            loadedApk2 = weakReference2 != null ? weakReference2.get() : null;
        }
        if (loadedApk != null) {
            ArrayList arrayList = new ArrayList();
            LoadedApk.makePaths(this, loadedApk.getApplicationInfo(), arrayList);
            loadedApk.updateApplicationInfo(applicationInfo, arrayList);
        }
        if (loadedApk2 != null) {
            ArrayList arrayList2 = new ArrayList();
            LoadedApk.makePaths(this, loadedApk2.getApplicationInfo(), arrayList2);
            loadedApk2.updateApplicationInfo(applicationInfo, arrayList2);
        }
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyNewResourceDirsLocked(applicationInfo.sourceDir, applicationInfo.resourceDirs);
        }
        ApplicationPackageManager.configurationChanged();
        Configuration configuration = new Configuration();
        Configuration configuration2 = this.mConfiguration;
        configuration.assetsSeq = (configuration2 != null ? configuration2.assetsSeq : 0) + 1;
        handleConfigurationChanged(configuration, null);
        relaunchAllActivities();
    }

    static void freeTextLayoutCachesIfNeeded(int i) {
        if (i != 0) {
            if ((i & 4) != 0) {
                Canvas.freeTextLayoutCaches();
            }
        }
    }

    @Override // android.app.ClientTransactionHandler
    public void handleActivityConfigurationChanged(IBinder iBinder, Configuration configuration, int i) {
        ActivityClientRecord activityClientRecord = this.mActivities.get(iBinder);
        if (activityClientRecord == null || activityClientRecord.activity == null) {
            return;
        }
        boolean z = (i == -1 || i == activityClientRecord.activity.getDisplay().getDisplayId()) ? false : true;
        activityClientRecord.overrideConfig = configuration;
        ViewRootImpl viewRootImpl = activityClientRecord.activity.mDecor != null ? activityClientRecord.activity.mDecor.getViewRootImpl() : null;
        if (z) {
            Configuration configurationPerformConfigurationChangedForActivity = performConfigurationChangedForActivity(activityClientRecord, this.mCompatConfiguration, i, true);
            if (viewRootImpl != null) {
                viewRootImpl.onMovedToDisplay(i, configurationPerformConfigurationChangedForActivity);
            }
        } else {
            performConfigurationChangedForActivity(activityClientRecord, this.mCompatConfiguration);
        }
        if (viewRootImpl != null) {
            viewRootImpl.updateConfiguration(i);
        }
        this.mSomeActivitiesChanged = true;
    }

    final void handleProfilerControl(boolean z, ProfilerInfo profilerInfo, int i) {
        try {
            if (z) {
                try {
                    this.mProfiler.setProfiler(profilerInfo);
                    this.mProfiler.startProfiling();
                } catch (RuntimeException unused) {
                    Slog.w(TAG, "Profiling failed on path " + profilerInfo.profileFile + " -- can the process access this path?");
                }
                return;
            }
            this.mProfiler.stopProfiling();
        } finally {
            profilerInfo.closeFd();
        }
    }

    public void stopProfiling() {
        Profiler profiler = this.mProfiler;
        if (profiler != null) {
            profiler.stopProfiling();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.app.IActivityManager] */
    /* JADX WARN: Type inference failed for: r4v6, types: [java.lang.String] */
    static void handleDumpHeap(DumpHeapData dumpHeapData) {
        String service = "Failure closing profile fd";
        if (dumpHeapData.runGc) {
            System.gc();
            System.runFinalization();
            System.gc();
        }
        try {
            try {
                if (dumpHeapData.managed) {
                    try {
                        Debug.dumpHprofData(dumpHeapData.path, dumpHeapData.fd.getFileDescriptor());
                        dumpHeapData.fd.close();
                    } catch (IOException unused) {
                        Slog.w(TAG, "Managed heap dump failed on path " + dumpHeapData.path + " -- can the process access this path?");
                        dumpHeapData.fd.close();
                    }
                } else if (dumpHeapData.mallocInfo) {
                    Debug.dumpNativeMallocInfo(dumpHeapData.fd.getFileDescriptor());
                } else {
                    Debug.dumpNativeHeap(dumpHeapData.fd.getFileDescriptor());
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failure closing profile fd", e);
            }
            try {
                service = ActivityManager.getService();
                dumpHeapData = dumpHeapData.path;
                service.dumpHeapFinished(dumpHeapData);
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        } catch (Throwable th) {
            try {
                dumpHeapData.fd.close();
            } catch (IOException e3) {
                Slog.w(TAG, service, e3);
            }
            throw th;
        }
    }

    final void handleDispatchPackageBroadcast(int i, String[] strArr) {
        WeakReference<LoadedApk> weakReference;
        WeakReference<LoadedApk> weakReference2;
        boolean z = false;
        if (i == 0 || i == 2) {
            boolean z2 = i == 0;
            if (strArr != null) {
                synchronized (this.mResourcesManager) {
                    for (int length = strArr.length - 1; length >= 0; length--) {
                        if (!z && (((weakReference = this.mPackages.get(strArr[length])) != null && weakReference.get() != null) || ((weakReference2 = this.mResourcePackages.get(strArr[length])) != null && weakReference2.get() != null))) {
                            z = true;
                        }
                        if (z2) {
                            this.mPackages.remove(strArr[length]);
                            this.mResourcePackages.remove(strArr[length]);
                        }
                    }
                }
            }
        } else if (i == 3 && strArr != null) {
            synchronized (this.mResourcesManager) {
                for (int length2 = strArr.length - 1; length2 >= 0; length2--) {
                    WeakReference<LoadedApk> weakReference3 = this.mPackages.get(strArr[length2]);
                    LoadedApk loadedApk = weakReference3 != null ? weakReference3.get() : null;
                    if (loadedApk != null) {
                        z = true;
                    } else {
                        WeakReference<LoadedApk> weakReference4 = this.mResourcePackages.get(strArr[length2]);
                        LoadedApk loadedApk2 = weakReference4 != null ? weakReference4.get() : null;
                        if (loadedApk2 != null) {
                            z = true;
                        }
                        loadedApk = loadedApk2;
                    }
                    if (loadedApk != null) {
                        try {
                            String str = strArr[length2];
                            ApplicationInfo applicationInfo = sPackageManager.getApplicationInfo(str, 1024, UserHandle.myUserId());
                            if (this.mActivities.size() > 0) {
                                for (ActivityClientRecord activityClientRecord : this.mActivities.values()) {
                                    if (activityClientRecord.activityInfo.applicationInfo.packageName.equals(str)) {
                                        activityClientRecord.activityInfo.applicationInfo = applicationInfo;
                                        activityClientRecord.packageInfo = loadedApk;
                                    }
                                }
                            }
                            ArrayList arrayList = new ArrayList();
                            LoadedApk.makePaths(this, loadedApk.getApplicationInfo(), arrayList);
                            loadedApk.updateApplicationInfo(applicationInfo, arrayList);
                        } catch (RemoteException unused) {
                        }
                    }
                }
            }
        }
        ApplicationPackageManager.handlePackageBroadcast(i, strArr, z);
    }

    final void handleLowMemory() {
        ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(true, null);
        int size = arrayListCollectComponentCallbacks.size();
        for (int i = 0; i < size; i++) {
            arrayListCollectComponentCallbacks.get(i).onLowMemory();
        }
        if (Process.myUid() != 1000) {
            EventLog.writeEvent(SQLITE_MEM_RELEASED_EVENT_LOG_TAG, SQLiteDatabase.releaseMemory());
        }
        Canvas.freeCaches();
        Canvas.freeTextLayoutCaches();
        BinderInternal.forceGc("mem");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTrimMemory(int i) {
        Trace.traceBegin(64L, "trimMemory");
        ArrayList<ComponentCallbacks2> arrayListCollectComponentCallbacks = collectComponentCallbacks(true, null);
        int size = arrayListCollectComponentCallbacks.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayListCollectComponentCallbacks.get(i2).onTrimMemory(i);
        }
        WindowManagerGlobal.getInstance().trimMemory(i);
        Trace.traceEnd(64L);
    }

    private void setupGraphicsSupport(Context context) {
        Trace.traceBegin(64L, "setupGraphicsSupport");
        if (!ZenModeConfig.SYSTEM_AUTHORITY.equals(context.getPackageName())) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null) {
                System.setProperty("java.io.tmpdir", cacheDir.getAbsolutePath());
            } else {
                Log.v(TAG, "Unable to initialize \"java.io.tmpdir\" property due to missing cache directory");
            }
            File codeCacheDir = context.createDeviceProtectedStorageContext().getCodeCacheDir();
            if (codeCacheDir != null) {
                try {
                    if (getPackageManager().getPackagesForUid(Process.myUid()) != null) {
                        ThreadedRenderer.setupDiskCache(codeCacheDir);
                        RenderScriptCacheDir.setupDiskCache(codeCacheDir);
                    }
                } catch (RemoteException e) {
                    Trace.traceEnd(64L);
                    throw e.rethrowFromSystemServer();
                }
            } else {
                Log.w(TAG, "Unable to use shader/script cache: missing code-cache directory");
            }
        }
        GraphicsEnvironment.getInstance().setup(context);
        Trace.traceEnd(64L);
    }

    private void updateDefaultDensity() {
        int i = this.mCurDefaultDisplayDpi;
        if (this.mDensityCompatMode || i == 0 || i == DisplayMetrics.DENSITY_DEVICE) {
            return;
        }
        DisplayMetrics.DENSITY_DEVICE = i;
        Bitmap.setDefaultDensity(i);
    }

    private String getInstrumentationLibrary(ApplicationInfo applicationInfo, InstrumentationInfo instrumentationInfo) {
        if (applicationInfo.primaryCpuAbi != null && applicationInfo.secondaryCpuAbi != null && applicationInfo.secondaryCpuAbi.equals(instrumentationInfo.secondaryCpuAbi)) {
            String instructionSet = VMRuntime.getInstructionSet(applicationInfo.secondaryCpuAbi);
            String str = SystemProperties.get("ro.dalvik.vm.isa." + instructionSet);
            if (!str.isEmpty()) {
                instructionSet = str;
            }
            if (VMRuntime.getRuntime().vmInstructionSet().equals(instructionSet)) {
                return instrumentationInfo.secondaryNativeLibraryDir;
            }
        }
        return instrumentationInfo.nativeLibraryDir;
    }

    private void updateLocaleListFromAppContext(Context context, LocaleList localeList) {
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        int size = localeList.size();
        for (int i = 0; i < size; i++) {
            if (locale.equals(localeList.get(i))) {
                LocaleList.setDefault(localeList, i);
                return;
            }
        }
        LocaleList.setDefault(new LocaleList(locale, localeList));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:10:0x0069  */
    public void handleBindApplication(AppBindData appBindData) {
        String str;
        Boolean bool;
        InstrumentationInfo instrumentationInfo;
        ApplicationInfo applicationInfo;
        int i;
        VMRuntime.registerSensitiveThread();
        if (appBindData.trackAllocation) {
            DdmVmInternal.enableRecentAllocations(true);
        }
        Process.setStartTimes(SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
        this.mBoundApplication = appBindData;
        this.mConfiguration = new Configuration(appBindData.config);
        this.mCompatConfiguration = new Configuration(appBindData.config);
        this.mProfiler = new Profiler();
        if (appBindData.initProfilerInfo != null) {
            this.mProfiler.profileFile = appBindData.initProfilerInfo.profileFile;
            this.mProfiler.profileFd = appBindData.initProfilerInfo.profileFd;
            this.mProfiler.samplingInterval = appBindData.initProfilerInfo.samplingInterval;
            this.mProfiler.autoStopProfiler = appBindData.initProfilerInfo.autoStopProfiler;
            this.mProfiler.streamingOutput = appBindData.initProfilerInfo.streamingOutput;
            if (appBindData.initProfilerInfo.attachAgentDuringBind) {
                str = appBindData.initProfilerInfo.agent;
            } else {
                str = null;
            }
        } else {
            str = null;
        }
        Process.setArgV0(appBindData.processName);
        DdmHandleAppName.setAppName(appBindData.processName, UserHandle.myUserId());
        VMRuntime.setProcessPackageName(appBindData.appInfo.packageName);
        if (this.mProfiler.profileFd != null) {
            this.mProfiler.startProfiling();
        }
        if (appBindData.appInfo.targetSdkVersion <= 12) {
            AsyncTask.setDefaultExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        Message.updateCheckRecycle(appBindData.appInfo.targetSdkVersion);
        ImageDecoder.sApiLevel = appBindData.appInfo.targetSdkVersion;
        TimeZone.setDefault(null);
        LocaleList.setDefault(appBindData.config.getLocales());
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyConfigurationToResourcesLocked(appBindData.config, appBindData.compatInfo);
            int i2 = appBindData.config.densityDpi;
            this.mCurDefaultDisplayDpi = i2;
            applyCompatConfiguration(i2);
        }
        appBindData.info = getPackageInfoNoCheck(appBindData.appInfo, appBindData.compatInfo);
        if (str != null) {
            handleAttachAgent(str, appBindData.info);
        }
        if ((appBindData.appInfo.flags & 8192) == 0) {
            this.mDensityCompatMode = true;
            Bitmap.setDefaultDensity(160);
        }
        updateDefaultDensity();
        String string = this.mCoreSettings.getString(Settings.System.TIME_12_24);
        if (string != null) {
            bool = "24".equals(string) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            bool = null;
        }
        DateFormat.set24HourTimePref(bool);
        View.mDebugViewAttributes = this.mCoreSettings.getInt(Settings.Global.DEBUG_VIEW_ATTRIBUTES, 0) != 0;
        StrictMode.initThreadDefaults(appBindData.appInfo);
        StrictMode.initVmDefaults(appBindData.appInfo);
        try {
            Field declaredField = Build.class.getDeclaredField("SERIAL");
            declaredField.setAccessible(true);
            declaredField.set(Build.class, appBindData.buildSerial);
        } catch (IllegalAccessException | NoSuchFieldException unused) {
        }
        if (appBindData.debugMode != 0) {
            Debug.changeDebugPort(8100);
            if (appBindData.debugMode == 2) {
                Slog.w(TAG, "Application " + appBindData.info.getPackageName() + " is waiting for the debugger on port 8100...");
                IActivityManager service = ActivityManager.getService();
                try {
                    service.showWaitingForDebugger(this.mAppThread, true);
                    Debug.waitForDebugger();
                    try {
                        service.showWaitingForDebugger(this.mAppThread, false);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                } catch (RemoteException e2) {
                    throw e2.rethrowFromSystemServer();
                }
            } else {
                Slog.w(TAG, "Application " + appBindData.info.getPackageName() + " can be debugged on port 8100...");
            }
        }
        boolean z = (appBindData.appInfo.flags & 2) != 0;
        Trace.setAppTracingAllowed(z);
        ThreadedRenderer.setDebuggingEnabled(z || Build.IS_DEBUGGABLE);
        if (z && appBindData.enableBinderTracking) {
            Binder.enableTracing();
        }
        Trace.traceBegin(64L, "Setup proxies");
        IBinder service2 = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
        if (service2 != null) {
            try {
                Proxy.setHttpProxySystemProperty(IConnectivityManager.Stub.asInterface(service2).getProxyForNetwork(null));
            } catch (RemoteException e3) {
                Trace.traceEnd(64L);
                throw e3.rethrowFromSystemServer();
            }
        }
        Trace.traceEnd(64L);
        if (appBindData.instrumentationName != null) {
            try {
                instrumentationInfo = new ApplicationPackageManager(null, getPackageManager()).getInstrumentationInfo(appBindData.instrumentationName, 0);
                if (!Objects.equals(appBindData.appInfo.primaryCpuAbi, instrumentationInfo.primaryCpuAbi) || !Objects.equals(appBindData.appInfo.secondaryCpuAbi, instrumentationInfo.secondaryCpuAbi)) {
                    Slog.w(TAG, "Package uses different ABI(s) than its instrumentation: package[" + appBindData.appInfo.packageName + "]: " + appBindData.appInfo.primaryCpuAbi + ", " + appBindData.appInfo.secondaryCpuAbi + " instrumentation[" + instrumentationInfo.packageName + "]: " + instrumentationInfo.primaryCpuAbi + ", " + instrumentationInfo.secondaryCpuAbi);
                }
                this.mInstrumentationPackageName = instrumentationInfo.packageName;
                this.mInstrumentationAppDir = instrumentationInfo.sourceDir;
                this.mInstrumentationSplitAppDirs = instrumentationInfo.splitSourceDirs;
                this.mInstrumentationLibDir = getInstrumentationLibrary(appBindData.appInfo, instrumentationInfo);
                this.mInstrumentedAppDir = appBindData.info.getAppDir();
                this.mInstrumentedSplitAppDirs = appBindData.info.getSplitAppDirs();
                this.mInstrumentedLibDir = appBindData.info.getLibDir();
            } catch (PackageManager.NameNotFoundException unused2) {
                throw new RuntimeException("Unable to find instrumentation info for: " + appBindData.instrumentationName);
            }
        } else {
            instrumentationInfo = null;
        }
        Context contextCreateAppContext = ContextImpl.createAppContext(this, appBindData.info);
        updateLocaleListFromAppContext(contextCreateAppContext, this.mResourcesManager.getConfiguration().getLocales());
        if (!Process.isIsolated()) {
            int iAllowThreadDiskWritesMask = StrictMode.allowThreadDiskWritesMask();
            try {
                setupGraphicsSupport(contextCreateAppContext);
                StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
            } catch (Throwable th) {
                StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
                throw th;
            }
        } else {
            ThreadedRenderer.setIsolatedProcess(true);
        }
        if (SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false)) {
            BaseDexClassLoader.setReporter(DexLoadReporter.getInstance());
        }
        Trace.traceBegin(64L, "NetworkSecurityConfigProvider.install");
        NetworkSecurityConfigProvider.install(contextCreateAppContext);
        Trace.traceEnd(64L);
        if (instrumentationInfo != null) {
            try {
                applicationInfo = getPackageManager().getApplicationInfo(instrumentationInfo.packageName, 0, UserHandle.myUserId());
            } catch (RemoteException unused3) {
                applicationInfo = null;
            }
            if (applicationInfo == null) {
                applicationInfo = new ApplicationInfo();
            }
            ApplicationInfo applicationInfo2 = applicationInfo;
            instrumentationInfo.copyTo(applicationInfo2);
            applicationInfo2.initForUser(UserHandle.myUserId());
            ContextImpl contextImplCreateAppContext = ContextImpl.createAppContext(this, getPackageInfo(applicationInfo2, appBindData.compatInfo, contextCreateAppContext.getClassLoader(), false, true, false));
            try {
                this.mInstrumentation = (Instrumentation) contextImplCreateAppContext.getClassLoader().loadClass(appBindData.instrumentationName.getClassName()).newInstance();
                this.mInstrumentation.init(this, contextImplCreateAppContext, contextCreateAppContext, new ComponentName(instrumentationInfo.packageName, instrumentationInfo.name), appBindData.instrumentationWatcher, appBindData.instrumentationUiAutomationConnection);
                if (this.mProfiler.profileFile != null && !instrumentationInfo.handleProfiling && this.mProfiler.profileFd == null) {
                    this.mProfiler.handlingProfiling = true;
                    File file = new File(this.mProfiler.profileFile);
                    file.getParentFile().mkdirs();
                    Debug.startMethodTracing(file.toString(), 8388608);
                }
            } catch (Exception e4) {
                throw new RuntimeException("Unable to instantiate instrumentation " + appBindData.instrumentationName + ": " + e4.toString(), e4);
            }
        } else {
            Instrumentation instrumentation = new Instrumentation();
            this.mInstrumentation = instrumentation;
            instrumentation.basicInit(this);
        }
        if ((appBindData.appInfo.flags & 1048576) != 0) {
            VMRuntime.getRuntime().clearGrowthLimit();
        } else {
            VMRuntime.getRuntime().clampGrowthLimit();
        }
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            Application applicationMakeApplication = appBindData.info.makeApplication(appBindData.restrictedBackupMode, null);
            applicationMakeApplication.setAutofillCompatibilityEnabled(appBindData.autofillCompatibilityEnabled);
            this.mInitialApplication = applicationMakeApplication;
            if (!appBindData.restrictedBackupMode && !ArrayUtils.isEmpty(appBindData.providers)) {
                installContentProviders(applicationMakeApplication, appBindData.providers);
                this.mH.sendEmptyMessageDelayed(132, JobInfo.MIN_BACKOFF_MILLIS);
            }
            try {
                this.mInstrumentation.onCreate(appBindData.instrumentationArgs);
                try {
                    this.mInstrumentation.callApplicationOnCreate(applicationMakeApplication);
                } catch (Exception e5) {
                    if (!this.mInstrumentation.onException(applicationMakeApplication, e5)) {
                        throw new RuntimeException("Unable to create application " + applicationMakeApplication.getClass().getName() + ": " + e5.toString(), e5);
                    }
                }
                if (appBindData.appInfo.targetSdkVersion < 27 || StrictMode.getThreadPolicy().equals(threadPolicy)) {
                    StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
                }
                FontsContract.setApplicationContextForResources(contextCreateAppContext);
                if (Process.isIsolated()) {
                    return;
                }
                try {
                    ApplicationInfo applicationInfo3 = getPackageManager().getApplicationInfo(appBindData.appInfo.packageName, 128, UserHandle.myUserId());
                    if (applicationInfo3.metaData == null || (i = applicationInfo3.metaData.getInt(ApplicationInfo.METADATA_PRELOADED_FONTS, 0)) == 0) {
                        return;
                    }
                    appBindData.info.getResources().preloadFonts(i);
                } catch (RemoteException e6) {
                    throw e6.rethrowFromSystemServer();
                }
            } catch (Exception e7) {
                throw new RuntimeException("Exception thrown in onCreate() of " + appBindData.instrumentationName + ": " + e7.toString(), e7);
            }
        } catch (Throwable th2) {
            if (appBindData.appInfo.targetSdkVersion < 27 || StrictMode.getThreadPolicy().equals(threadPolicy)) {
                StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
            }
            throw th2;
        }
    }

    final void finishInstrumentation(int i, Bundle bundle) {
        IActivityManager service = ActivityManager.getService();
        if (this.mProfiler.profileFile != null && this.mProfiler.handlingProfiling && this.mProfiler.profileFd == null) {
            Debug.stopMethodTracing();
        }
        try {
            service.finishInstrumentation(this.mAppThread, i, bundle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private void installContentProviders(Context context, List<ProviderInfo> list) {
        ArrayList arrayList = new ArrayList();
        Iterator<ProviderInfo> it = list.iterator();
        while (it.hasNext()) {
            ContentProviderHolder contentProviderHolderInstallProvider = installProvider(context, null, it.next(), false, true, true);
            if (contentProviderHolderInstallProvider != null) {
                contentProviderHolderInstallProvider.noReleaseNeeded = true;
                arrayList.add(contentProviderHolderInstallProvider);
            }
        }
        try {
            ActivityManager.getService().publishContentProviders(getApplicationThread(), arrayList);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final IContentProvider acquireProvider(Context context, String str, int i, boolean z) {
        ContentProviderHolder contentProvider;
        IContentProvider iContentProviderAcquireExistingProvider = acquireExistingProvider(context, str, i, z);
        if (iContentProviderAcquireExistingProvider != null) {
            return iContentProviderAcquireExistingProvider;
        }
        try {
            synchronized (getGetProviderLock(str, i)) {
                contentProvider = ActivityManager.getService().getContentProvider(getApplicationThread(), str, i, z);
            }
            if (contentProvider == null) {
                Slog.e(TAG, "Failed to find provider info for " + str);
                return null;
            }
            return installProvider(context, contentProvider, contentProvider.info, true, contentProvider.noReleaseNeeded, z).provider;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private Object getGetProviderLock(String str, int i) {
        Object providerKey = new ProviderKey(str, i);
        synchronized (this.mGetProviderLocks) {
            Object obj = this.mGetProviderLocks.get(providerKey);
            if (obj == null) {
                this.mGetProviderLocks.put((ProviderKey) providerKey, providerKey);
            } else {
                providerKey = obj;
            }
        }
        return providerKey;
    }

    private final void incProviderRefLocked(ProviderRefCount providerRefCount, boolean z) {
        int i = 0;
        try {
            if (z) {
                providerRefCount.stableCount++;
                if (providerRefCount.stableCount != 1) {
                    return;
                }
                if (providerRefCount.removePending) {
                    providerRefCount.removePending = false;
                    this.mH.removeMessages(131, providerRefCount);
                    i = -1;
                }
                ActivityManager.getService().refContentProvider(providerRefCount.holder.connection, 1, i);
            } else {
                providerRefCount.unstableCount++;
                if (providerRefCount.unstableCount != 1) {
                    return;
                }
                if (providerRefCount.removePending) {
                    providerRefCount.removePending = false;
                    this.mH.removeMessages(131, providerRefCount);
                    return;
                }
                ActivityManager.getService().refContentProvider(providerRefCount.holder.connection, 0, 1);
            }
        } catch (RemoteException unused) {
        }
    }

    public final IContentProvider acquireExistingProvider(Context context, String str, int i, boolean z) {
        synchronized (this.mProviderMap) {
            ProviderClientRecord providerClientRecord = this.mProviderMap.get(new ProviderKey(str, i));
            if (providerClientRecord == null) {
                return null;
            }
            IContentProvider iContentProvider = providerClientRecord.mProvider;
            IBinder iBinderAsBinder = iContentProvider.asBinder();
            if (!iBinderAsBinder.isBinderAlive()) {
                Log.i(TAG, "Acquiring provider " + str + " for user " + i + ": existing object's process dead");
                handleUnstableProviderDiedLocked(iBinderAsBinder, true);
                return null;
            }
            ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinderAsBinder);
            if (providerRefCount != null) {
                incProviderRefLocked(providerRefCount, z);
            }
            return iContentProvider;
        }
    }

    public final boolean releaseProvider(IContentProvider iContentProvider, boolean z) {
        boolean z2 = false;
        z2 = false;
        if (iContentProvider == null) {
            return false;
        }
        IBinder iBinderAsBinder = iContentProvider.asBinder();
        synchronized (this.mProviderMap) {
            ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinderAsBinder);
            if (providerRefCount == null) {
                return false;
            }
            try {
                if (z) {
                    if (providerRefCount.stableCount == 0) {
                        return false;
                    }
                    providerRefCount.stableCount--;
                    if (providerRefCount.stableCount == 0) {
                        z = providerRefCount.unstableCount == 0;
                        ActivityManager.getService().refContentProvider(providerRefCount.holder.connection, -1, z ? 1 : 0);
                        z2 = z;
                    }
                } else {
                    if (providerRefCount.unstableCount == 0) {
                        return false;
                    }
                    providerRefCount.unstableCount--;
                    if (providerRefCount.unstableCount == 0) {
                        z = providerRefCount.stableCount == 0;
                        if (!z) {
                            ActivityManager.getService().refContentProvider(providerRefCount.holder.connection, 0, -1);
                        }
                        z2 = z;
                    }
                }
            } catch (RemoteException unused) {
            }
            if (z2) {
                if (!providerRefCount.removePending) {
                    providerRefCount.removePending = true;
                    this.mH.sendMessage(this.mH.obtainMessage(131, providerRefCount));
                } else {
                    Slog.w(TAG, "Duplicate remove pending of provider " + providerRefCount.holder.info.name);
                }
            }
            return true;
        }
    }

    final void completeRemoveProvider(ProviderRefCount providerRefCount) {
        synchronized (this.mProviderMap) {
            if (providerRefCount.removePending) {
                providerRefCount.removePending = false;
                IBinder iBinderAsBinder = providerRefCount.holder.provider.asBinder();
                if (this.mProviderRefCountMap.get(iBinderAsBinder) == providerRefCount) {
                    this.mProviderRefCountMap.remove(iBinderAsBinder);
                }
                for (int size = this.mProviderMap.size() - 1; size >= 0; size--) {
                    if (this.mProviderMap.valueAt(size).mProvider.asBinder() == iBinderAsBinder) {
                        this.mProviderMap.removeAt(size);
                    }
                }
                try {
                    ActivityManager.getService().removeContentProvider(providerRefCount.holder.connection, false);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    final void handleUnstableProviderDied(IBinder iBinder, boolean z) {
        synchronized (this.mProviderMap) {
            handleUnstableProviderDiedLocked(iBinder, z);
        }
    }

    final void handleUnstableProviderDiedLocked(IBinder iBinder, boolean z) {
        ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinder);
        if (providerRefCount != null) {
            this.mProviderRefCountMap.remove(iBinder);
            for (int size = this.mProviderMap.size() - 1; size >= 0; size--) {
                ProviderClientRecord providerClientRecordValueAt = this.mProviderMap.valueAt(size);
                if (providerClientRecordValueAt != null && providerClientRecordValueAt.mProvider.asBinder() == iBinder) {
                    Slog.i(TAG, "Removing dead content provider:" + providerClientRecordValueAt.mProvider.toString());
                    this.mProviderMap.removeAt(size);
                }
            }
            if (z) {
                try {
                    ActivityManager.getService().unstableProviderDied(providerRefCount.holder.connection);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    final void appNotRespondingViaProvider(IBinder iBinder) {
        synchronized (this.mProviderMap) {
            ProviderRefCount providerRefCount = this.mProviderRefCountMap.get(iBinder);
            if (providerRefCount != null) {
                try {
                    ActivityManager.getService().appNotRespondingViaProvider(providerRefCount.holder.connection);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    private ProviderClientRecord installProviderAuthoritiesLocked(IContentProvider iContentProvider, ContentProvider contentProvider, ContentProviderHolder contentProviderHolder) {
        String[] strArrSplit = contentProviderHolder.info.authority.split(";");
        int userId = UserHandle.getUserId(contentProviderHolder.info.applicationInfo.uid);
        if (iContentProvider != null) {
            for (String str : strArrSplit) {
                switch (str) {
                    case "com.android.contacts":
                    case "call_log":
                    case "call_log_shadow":
                    case "com.android.blockednumber":
                    case "com.android.calendar":
                    case "downloads":
                    case "telephony":
                        Binder.allowBlocking(iContentProvider.asBinder());
                        break;
                }
            }
        }
        ProviderClientRecord providerClientRecord = new ProviderClientRecord(strArrSplit, iContentProvider, contentProvider, contentProviderHolder);
        for (String str2 : strArrSplit) {
            ProviderKey providerKey = new ProviderKey(str2, userId);
            if (this.mProviderMap.get(providerKey) != null) {
                Slog.w(TAG, "Content provider " + providerClientRecord.mHolder.info.name + " already published as " + str2);
            } else {
                this.mProviderMap.put(providerKey, providerClientRecord);
            }
        }
        return providerClientRecord;
    }

    private ContentProviderHolder installProvider(Context context, ContentProviderHolder contentProviderHolder, ProviderInfo providerInfo, boolean z, boolean z2, boolean z3) {
        IContentProvider iContentProvider;
        ProviderRefCount providerRefCount;
        ContentProviderHolder contentProviderHolder2;
        ContentProvider contentProvider = null;
        if (contentProviderHolder == null || contentProviderHolder.provider == null) {
            if (z) {
                Slog.d(TAG, "Loading provider " + providerInfo.authority + ": " + providerInfo.name);
            }
            ApplicationInfo applicationInfo = providerInfo.applicationInfo;
            if (!context.getPackageName().equals(applicationInfo.packageName)) {
                Application application = this.mInitialApplication;
                if (application != null && application.getPackageName().equals(applicationInfo.packageName)) {
                    context = this.mInitialApplication;
                } else {
                    try {
                        context = context.createPackageContext(applicationInfo.packageName, 1);
                    } catch (PackageManager.NameNotFoundException unused) {
                        context = null;
                    }
                }
            }
            if (context == null) {
                Slog.w(TAG, "Unable to get context for package " + applicationInfo.packageName + " while loading content provider " + providerInfo.name);
                return null;
            }
            if (providerInfo.splitName != null) {
                try {
                    context = context.createContextForSplit(providerInfo.splitName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                ClassLoader classLoader = context.getClassLoader();
                LoadedApk loadedApkPeekPackageInfo = peekPackageInfo(applicationInfo.packageName, true);
                if (loadedApkPeekPackageInfo == null) {
                    loadedApkPeekPackageInfo = getSystemContext().mPackageInfo;
                }
                ContentProvider contentProviderInstantiateProvider = loadedApkPeekPackageInfo.getAppFactory().instantiateProvider(classLoader, providerInfo.name);
                IContentProvider iContentProvider2 = contentProviderInstantiateProvider.getIContentProvider();
                if (iContentProvider2 == null) {
                    Slog.e(TAG, "Failed to instantiate class " + providerInfo.name + " from sourceDir " + providerInfo.applicationInfo.sourceDir);
                    return null;
                }
                contentProviderInstantiateProvider.attachInfo(context, providerInfo);
                contentProvider = contentProviderInstantiateProvider;
                iContentProvider = iContentProvider2;
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(null, e2)) {
                    return null;
                }
                throw new RuntimeException("Unable to get provider " + providerInfo.name + ": " + e2.toString(), e2);
            }
        } else {
            iContentProvider = contentProviderHolder.provider;
        }
        synchronized (this.mProviderMap) {
            IBinder iBinderAsBinder = iContentProvider.asBinder();
            if (contentProvider != null) {
                ComponentName componentName = new ComponentName(providerInfo.packageName, providerInfo.name);
                ProviderClientRecord providerClientRecordInstallProviderAuthoritiesLocked = this.mLocalProvidersByName.get(componentName);
                if (providerClientRecordInstallProviderAuthoritiesLocked != null) {
                    IContentProvider iContentProvider3 = providerClientRecordInstallProviderAuthoritiesLocked.mProvider;
                } else {
                    ContentProviderHolder contentProviderHolder3 = new ContentProviderHolder(providerInfo);
                    contentProviderHolder3.provider = iContentProvider;
                    contentProviderHolder3.noReleaseNeeded = true;
                    providerClientRecordInstallProviderAuthoritiesLocked = installProviderAuthoritiesLocked(iContentProvider, contentProvider, contentProviderHolder3);
                    this.mLocalProviders.put(iBinderAsBinder, providerClientRecordInstallProviderAuthoritiesLocked);
                    this.mLocalProvidersByName.put(componentName, providerClientRecordInstallProviderAuthoritiesLocked);
                }
                contentProviderHolder2 = providerClientRecordInstallProviderAuthoritiesLocked.mHolder;
            } else {
                ProviderRefCount providerRefCount2 = this.mProviderRefCountMap.get(iBinderAsBinder);
                if (providerRefCount2 == null) {
                    ProviderClientRecord providerClientRecordInstallProviderAuthoritiesLocked2 = installProviderAuthoritiesLocked(iContentProvider, contentProvider, contentProviderHolder);
                    if (z2) {
                        providerRefCount2 = new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 1000, 1000);
                    } else {
                        if (z3) {
                            providerRefCount = new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 1, 0);
                        } else {
                            providerRefCount = new ProviderRefCount(contentProviderHolder, providerClientRecordInstallProviderAuthoritiesLocked2, 0, 1);
                        }
                        providerRefCount2 = providerRefCount;
                    }
                    this.mProviderRefCountMap.put(iBinderAsBinder, providerRefCount2);
                } else if (!z2) {
                    incProviderRefLocked(providerRefCount2, z3);
                    try {
                        ActivityManager.getService().removeContentProvider(contentProviderHolder.connection, z3);
                    } catch (RemoteException unused2) {
                    }
                }
                contentProviderHolder2 = providerRefCount2.holder;
            }
        }
        return contentProviderHolder2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRunIsolatedEntryPoint(String str, String[] strArr) {
        try {
            Class.forName(str).getMethod("main", String[].class).invoke(null, strArr);
            System.exit(0);
        } catch (ReflectiveOperationException e) {
            throw new AndroidRuntimeException("runIsolatedEntryPoint failed", e);
        }
    }

    private void attach(boolean z, long j) {
        sCurrentActivityThread = this;
        this.mSystemThread = z;
        if (!z) {
            ViewRootImpl.addFirstDrawHandler(new Runnable() { // from class: android.app.ActivityThread.1
                @Override // java.lang.Runnable
                public void run() {
                    ActivityThread.this.ensureJitEnabled();
                }
            });
            DdmHandleAppName.setAppName("<pre-initialized>", UserHandle.myUserId());
            RuntimeInit.setApplicationObject(this.mAppThread.asBinder());
            final IActivityManager service = ActivityManager.getService();
            try {
                service.attachApplication(this.mAppThread, j);
                BinderInternal.addGcWatcher(new Runnable() { // from class: android.app.ActivityThread.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ActivityThread.this.mSomeActivitiesChanged) {
                            Runtime runtime = Runtime.getRuntime();
                            if (runtime.totalMemory() - runtime.freeMemory() > (runtime.maxMemory() * 3) / 4) {
                                ActivityThread.this.mSomeActivitiesChanged = false;
                                try {
                                    service.releaseSomeActivities(ActivityThread.this.mAppThread);
                                } catch (RemoteException e) {
                                    throw e.rethrowFromSystemServer();
                                }
                            }
                        }
                    }
                });
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } else {
            DdmHandleAppName.setAppName("system_process", UserHandle.myUserId());
            try {
                Instrumentation instrumentation = new Instrumentation();
                this.mInstrumentation = instrumentation;
                instrumentation.basicInit(this);
                Application applicationMakeApplication = ContextImpl.createAppContext(this, getSystemContext().mPackageInfo).mPackageInfo.makeApplication(true, null);
                this.mInitialApplication = applicationMakeApplication;
                applicationMakeApplication.onCreate();
            } catch (Exception e2) {
                throw new RuntimeException("Unable to instantiate Application():" + e2.toString(), e2);
            }
        }
        DropBox.setReporter(new DropBoxReporter());
        ViewRootImpl.addConfigCallback(new ViewRootImpl.ConfigChangedCallback() { // from class: android.app.-$$Lambda$ActivityThread$ZXDWm3IBeFmLnFVblhB-IOZCr9o
            @Override // android.view.ViewRootImpl.ConfigChangedCallback
            public final void onConfigurationChanged(Configuration configuration) {
                this.f$0.lambda$attach$0$ActivityThread(configuration);
            }
        });
    }

    public /* synthetic */ void lambda$attach$0$ActivityThread(Configuration configuration) {
        synchronized (this.mResourcesManager) {
            if (this.mResourcesManager.applyConfigurationToResourcesLocked(configuration, null)) {
                updateLocaleListFromAppContext(this.mInitialApplication.getApplicationContext(), this.mResourcesManager.getConfiguration().getLocales());
                if (this.mPendingConfiguration == null || this.mPendingConfiguration.isOtherSeqNewer(configuration)) {
                    this.mPendingConfiguration = configuration;
                    sendMessage(118, configuration);
                }
            }
        }
    }

    public static ActivityThread systemMain() {
        if (!ActivityManager.isHighEndGfx()) {
            ThreadedRenderer.disable(true);
        } else {
            ThreadedRenderer.enableForegroundTrimming();
        }
        ActivityThread activityThread = new ActivityThread();
        activityThread.attach(true, 0L);
        return activityThread;
    }

    public final void installSystemProviders(List<ProviderInfo> list) {
        if (list != null) {
            installContentProviders(this.mInitialApplication, list);
        }
    }

    public int getIntCoreSetting(String str, int i) {
        synchronized (this.mResourcesManager) {
            if (this.mCoreSettings == null) {
                return i;
            }
            return this.mCoreSettings.getInt(str, i);
        }
    }

    private static class EventLoggingReporter implements EventLogger.Reporter {
        private EventLoggingReporter() {
        }

        public void report(int i, Object... objArr) {
            EventLog.writeEvent(i, objArr);
        }
    }

    private class DropBoxReporter implements DropBox.Reporter {
        private DropBoxManager dropBox;

        public DropBoxReporter() {
        }

        public void addData(String str, byte[] bArr, int i) {
            ensureInitialized();
            this.dropBox.addData(str, bArr, i);
        }

        public void addText(String str, String str2) {
            ensureInitialized();
            this.dropBox.addText(str, str2);
        }

        private synchronized void ensureInitialized() {
            if (this.dropBox == null) {
                this.dropBox = (DropBoxManager) ActivityThread.this.getSystemContext().getSystemService(Context.DROPBOX_SERVICE);
            }
        }
    }

    public static void main(String[] strArr) {
        Trace.traceBegin(64L, "ActivityThreadMain");
        CloseGuard.setEnabled(false);
        Environment.initForCurrentUser();
        EventLogger.setReporter(new EventLoggingReporter());
        TrustedCertificateStore.setDefaultUserDirectory(Environment.getUserConfigDirectory(UserHandle.myUserId()));
        Process.setArgV0("<pre-initialized>");
        Looper.prepareMainLooper();
        long j = 0;
        if (strArr != null) {
            for (int length = strArr.length - 1; length >= 0; length--) {
                if (strArr[length] != null && strArr[length].startsWith(PROC_START_SEQ_IDENT)) {
                    j = Long.parseLong(strArr[length].substring(4));
                }
            }
        }
        ActivityThread activityThread = new ActivityThread();
        activityThread.attach(false, j);
        if (sMainThreadHandler == null) {
            sMainThreadHandler = activityThread.getHandler();
        }
        Trace.traceEnd(64L);
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
}
