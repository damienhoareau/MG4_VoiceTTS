package com.android.internal.os;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.impl.CacheValue;
import android.icu.text.DecimalFormatSymbols;
import android.icu.util.ULocale;
import android.opengl.EGL14;
import android.os.Build;
import android.os.Environment;
import android.os.IInstalld;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.ZygoteProcess;
import android.os.storage.StorageManager;
import android.provider.SettingsStringUtil;
import android.security.keystore.AndroidKeyStoreProvider;
import android.service.notification.ZenModeConfig;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructCapUserData;
import android.system.StructCapUserHeader;
import android.text.Hyphenator;
import android.util.EventLog;
import android.util.Log;
import android.util.TimingsTraceLog;
import android.webkit.WebViewFactory;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.Preconditions;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import dalvik.system.ZygoteHooks;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ZygoteInit {
    private static final String ABI_LIST_ARG = "--abi-list=";
    private static final int LOG_BOOT_PROGRESS_PRELOAD_END = 3030;
    private static final int LOG_BOOT_PROGRESS_PRELOAD_START = 3020;
    private static final String PRELOADED_CLASSES = "/system/etc/preloaded-classes";
    private static final int PRELOAD_GC_THRESHOLD = 50000;
    public static final boolean PRELOAD_RESOURCES = true;
    private static final String PROPERTY_DISABLE_OPENGL_PRELOADING = "ro.zygote.disable_gl_preload";
    private static final String PROPERTY_GFX_DRIVER = "ro.gfx.driver.0";
    private static final int ROOT_GID = 0;
    private static final int ROOT_UID = 0;
    private static final String SOCKET_NAME_ARG = "--socket-name=";
    private static final String TAG = "Zygote";
    private static final int UNPRIVILEGED_GID = 9999;
    private static final int UNPRIVILEGED_UID = 9999;
    private static Resources mResources = null;
    private static volatile boolean sFinishPreload = false;
    private static boolean sPreloadComplete;
    private static Thread tPreload = new Thread(new Runnable() { // from class: com.android.internal.os.ZygoteInit.1
        @Override // java.lang.Runnable
        public void run() {
            boolean unused = ZygoteInit.sFinishPreload = false;
            TimingsTraceLog timingsTraceLog = new TimingsTraceLog("ZygoteInitTiming_lazy", 16384L);
            timingsTraceLog.traceBegin("BeginIcuCachePinning");
            ZygoteInit.beginIcuCachePinning();
            timingsTraceLog.traceEnd();
            Trace.traceBegin(16384L, "PreloadAppProcessHALs");
            ZygoteInit.nativePreloadAppProcessHALs();
            Trace.traceEnd(16384L);
            Trace.traceBegin(16384L, "PreloadOpenGL");
            ZygoteInit.preloadOpenGL();
            Trace.traceEnd(16384L);
            ZygoteInit.preloadSharedLibraries();
            WebViewFactory.prepareWebViewInZygote();
            ZygoteInit.endIcuCachePinning();
            ZygoteInit.warmUpJcaProviders();
            boolean unused2 = ZygoteInit.sFinishPreload = true;
        }
    });

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativePreloadAppProcessHALs();

    private static final native void nativeZygoteInit();

    static void preload(TimingsTraceLog timingsTraceLog) {
        Log.d(TAG, "begin preload");
        tPreload.start();
        timingsTraceLog.traceBegin("PreloadClasses");
        preloadClasses();
        timingsTraceLog.traceEnd();
        Log.d(TAG, "end preload");
        sPreloadComplete = true;
    }

    public static void lazyPreload() {
        Preconditions.checkState(!sPreloadComplete);
        Log.i(TAG, "Lazily preloading resources.");
        preload(new TimingsTraceLog("ZygoteInitTiming_lazy", 16384L));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void beginIcuCachePinning() {
        Log.i(TAG, "Installing ICU cache reference pinning...");
        CacheValue.setStrength(CacheValue.Strength.STRONG);
        Log.i(TAG, "Preloading ICU data...");
        ULocale[] uLocaleArr = {ULocale.ROOT, ULocale.US, ULocale.getDefault()};
        for (int i = 0; i < 3; i++) {
            new DecimalFormatSymbols(uLocaleArr[i]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void endIcuCachePinning() {
        CacheValue.setStrength(CacheValue.Strength.SOFT);
        Log.i(TAG, "Uninstalled ICU cache reference pinning...");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void preloadSharedLibraries() {
        Log.i(TAG, "Preloading shared libraries...");
        System.loadLibrary(ZenModeConfig.SYSTEM_AUTHORITY);
        System.loadLibrary("compiler_rt");
        System.loadLibrary("jnigraphics");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void preloadOpenGL() {
        String str = SystemProperties.get(PROPERTY_GFX_DRIVER);
        if (SystemProperties.getBoolean(PROPERTY_DISABLE_OPENGL_PRELOADING, false)) {
            return;
        }
        if (str == null || str.isEmpty()) {
            EGL14.eglGetDisplay(0);
        }
    }

    private static void preloadTextResources() {
        Hyphenator.init();
        TextView.preloadFontCache();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void warmUpJcaProviders() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        Trace.traceBegin(16384L, "Starting installation of AndroidKeyStoreProvider");
        AndroidKeyStoreProvider.install();
        Log.i(TAG, "Installed AndroidKeyStoreProvider in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
        Trace.traceEnd(16384L);
        long jUptimeMillis2 = SystemClock.uptimeMillis();
        Trace.traceBegin(16384L, "Starting warm up of JCA providers");
        for (Provider provider : Security.getProviders()) {
            provider.warmUpServiceProvision();
        }
        Log.i(TAG, "Warmed up JCA providers in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms.");
        Trace.traceEnd(16384L);
    }

    private static void preloadClasses() {
        boolean z;
        long j;
        VMRuntime runtime = VMRuntime.getRuntime();
        try {
            FileInputStream fileInputStream = new FileInputStream(PRELOADED_CLASSES);
            Log.i(TAG, "Preloading classes...");
            long jUptimeMillis = SystemClock.uptimeMillis();
            int i = Os.getuid();
            int i2 = Os.getgid();
            boolean z2 = true;
            if (i == 0 && i2 == 0) {
                try {
                    Os.setregid(0, Process.NOBODY_UID);
                    Os.setreuid(0, Process.NOBODY_UID);
                    z = true;
                } catch (ErrnoException e) {
                    throw new RuntimeException("Failed to drop root", e);
                }
            } else {
                z = false;
            }
            float targetHeapUtilization = runtime.getTargetHeapUtilization();
            runtime.setTargetHeapUtilization(0.8f);
            long j2 = 16384;
            try {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream), 256);
                    int i3 = 0;
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        String strTrim = line.trim();
                        if (!strTrim.startsWith("#") && !strTrim.equals("")) {
                            Trace.traceBegin(j2, strTrim);
                            try {
                                Class.forName(strTrim, z2, null);
                                i3++;
                                j = j2;
                            } catch (ClassNotFoundException unused) {
                                Log.w(TAG, "Class not found for preloading: " + strTrim);
                                j = 16384;
                            } catch (UnsatisfiedLinkError e2) {
                                Log.w(TAG, "Problem preloading " + strTrim + ": " + e2);
                                j = 16384;
                            } catch (Throwable th) {
                                Log.e(TAG, "Error preloading " + strTrim + ".", th);
                                if (th instanceof Error) {
                                    throw ((Error) th);
                                }
                                if (th instanceof RuntimeException) {
                                    throw ((RuntimeException) th);
                                }
                                throw new RuntimeException(th);
                            }
                            Trace.traceEnd(j);
                        }
                        z2 = true;
                        j2 = 16384;
                    }
                    Log.i(TAG, "...preloaded " + i3 + " classes in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                    IoUtils.closeQuietly(fileInputStream);
                    runtime.setTargetHeapUtilization(targetHeapUtilization);
                    Trace.traceBegin(16384L, "PreloadDexCaches");
                    runtime.preloadDexCaches();
                    Trace.traceEnd(16384L);
                    if (z) {
                        try {
                            Os.setreuid(0, 0);
                            Os.setregid(0, 0);
                        } catch (ErrnoException e3) {
                            throw new RuntimeException("Failed to restore root", e3);
                        }
                    }
                } catch (IOException e4) {
                    Log.e(TAG, "Error reading /system/etc/preloaded-classes.", e4);
                    IoUtils.closeQuietly(fileInputStream);
                    runtime.setTargetHeapUtilization(targetHeapUtilization);
                    Trace.traceBegin(16384L, "PreloadDexCaches");
                    runtime.preloadDexCaches();
                    Trace.traceEnd(16384L);
                    if (z) {
                        try {
                            Os.setreuid(0, 0);
                            Os.setregid(0, 0);
                        } catch (ErrnoException e5) {
                            throw new RuntimeException("Failed to restore root", e5);
                        }
                    }
                }
            } catch (Throwable th2) {
                IoUtils.closeQuietly(fileInputStream);
                runtime.setTargetHeapUtilization(targetHeapUtilization);
                Trace.traceBegin(16384L, "PreloadDexCaches");
                runtime.preloadDexCaches();
                Trace.traceEnd(16384L);
                if (z) {
                    try {
                        Os.setreuid(0, 0);
                        Os.setregid(0, 0);
                    } catch (ErrnoException e6) {
                        throw new RuntimeException("Failed to restore root", e6);
                    }
                }
                throw th2;
            }
        } catch (FileNotFoundException unused2) {
            Log.e(TAG, "Couldn't find /system/etc/preloaded-classes.");
        }
    }

    private static void preloadResources() {
        VMRuntime.getRuntime();
        try {
            Resources system = Resources.getSystem();
            mResources = system;
            system.startPreloading();
            Log.i(TAG, "Preloading resources...");
            long jUptimeMillis = SystemClock.uptimeMillis();
            TypedArray typedArrayObtainTypedArray = mResources.obtainTypedArray(R.array.preloaded_drawables);
            int iPreloadDrawables = preloadDrawables(typedArrayObtainTypedArray);
            typedArrayObtainTypedArray.recycle();
            Log.i(TAG, "...preloaded " + iPreloadDrawables + " resources in " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
            long jUptimeMillis2 = SystemClock.uptimeMillis();
            TypedArray typedArrayObtainTypedArray2 = mResources.obtainTypedArray(R.array.preloaded_color_state_lists);
            int iPreloadColorStateLists = preloadColorStateLists(typedArrayObtainTypedArray2);
            typedArrayObtainTypedArray2.recycle();
            Log.i(TAG, "...preloaded " + iPreloadColorStateLists + " resources in " + (SystemClock.uptimeMillis() - jUptimeMillis2) + "ms.");
            if (mResources.getBoolean(R.bool.config_freeformWindowManagement)) {
                long jUptimeMillis3 = SystemClock.uptimeMillis();
                TypedArray typedArrayObtainTypedArray3 = mResources.obtainTypedArray(R.array.preloaded_freeform_multi_window_drawables);
                int iPreloadDrawables2 = preloadDrawables(typedArrayObtainTypedArray3);
                typedArrayObtainTypedArray3.recycle();
                Log.i(TAG, "...preloaded " + iPreloadDrawables2 + " resource in " + (SystemClock.uptimeMillis() - jUptimeMillis3) + "ms.");
            }
            mResources.finishPreloading();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure preloading resources", e);
        }
    }

    private static int preloadColorStateLists(TypedArray typedArray) {
        int length = typedArray.length();
        for (int i = 0; i < length; i++) {
            int resourceId = typedArray.getResourceId(i, 0);
            if (resourceId != 0 && mResources.getColorStateList(resourceId, null) == null) {
                throw new IllegalArgumentException("Unable to find preloaded color resource #0x" + Integer.toHexString(resourceId) + " (" + typedArray.getString(i) + ")");
            }
        }
        return length;
    }

    private static int preloadDrawables(TypedArray typedArray) {
        int length = typedArray.length();
        for (int i = 0; i < length; i++) {
            int resourceId = typedArray.getResourceId(i, 0);
            if (resourceId != 0 && mResources.getDrawable(resourceId, null) == null) {
                throw new IllegalArgumentException("Unable to find preloaded drawable resource #0x" + Integer.toHexString(resourceId) + " (" + typedArray.getString(i) + ")");
            }
        }
        return length;
    }

    static void gcAndFinalize() {
        VMRuntime runtime = VMRuntime.getRuntime();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
    }

    private static Runnable handleSystemServerProcess(ZygoteConnection.Arguments arguments) {
        String[] strArr;
        Os.umask(OsConstants.S_IRWXG | OsConstants.S_IRWXO);
        if (arguments.niceName != null) {
            Process.setArgV0(arguments.niceName);
        }
        String str = Os.getenv("SYSTEMSERVERCLASSPATH");
        if (str != null) {
            performSystemServerDexOpt(str);
            if (SystemProperties.getBoolean("dalvik.vm.profilesystemserver", false) && (Build.IS_USERDEBUG || Build.IS_ENG)) {
                try {
                    prepareSystemServerProfile(str);
                } catch (Exception e) {
                    Log.wtf(TAG, "Failed to set up system server profile", e);
                }
            }
        }
        if (arguments.invokeWith != null) {
            String[] strArr2 = arguments.remainingArgs;
            if (str != null) {
                String[] strArr3 = new String[strArr2.length + 2];
                strArr3[0] = "-cp";
                strArr3[1] = str;
                System.arraycopy(strArr2, 0, strArr3, 2, strArr2.length);
                strArr = strArr3;
            } else {
                strArr = strArr2;
            }
            WrapperInit.execApplication(arguments.invokeWith, arguments.niceName, arguments.targetSdkVersion, VMRuntime.getCurrentInstructionSet(), null, strArr);
            throw new IllegalStateException("Unexpected return from WrapperInit.execApplication");
        }
        ClassLoader classLoaderCreatePathClassLoader = null;
        if (str != null) {
            classLoaderCreatePathClassLoader = createPathClassLoader(str, arguments.targetSdkVersion);
            Thread.currentThread().setContextClassLoader(classLoaderCreatePathClassLoader);
        }
        return zygoteInit(arguments.targetSdkVersion, arguments.remainingArgs, classLoaderCreatePathClassLoader);
    }

    private static void prepareSystemServerProfile(String str) throws RemoteException {
        if (str.isEmpty()) {
            return;
        }
        String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
        IInstalld.Stub.asInterface(ServiceManager.getService("installd")).prepareAppProfile(ZenModeConfig.SYSTEM_AUTHORITY, 0, UserHandle.getAppId(1000), "primary.prof", strArrSplit[0], null);
        VMRuntime.registerAppInfo(new File(Environment.getDataProfilesDePackageDirectory(0, ZenModeConfig.SYSTEM_AUTHORITY), "primary.prof").getAbsolutePath(), strArrSplit);
    }

    public static void setApiBlacklistExemptions(String[] strArr) {
        VMRuntime.getRuntime().setHiddenApiExemptions(strArr);
    }

    public static void setHiddenApiAccessLogSampleRate(int i) {
        VMRuntime.getRuntime().setHiddenApiAccessLogSamplingRate(i);
    }

    static ClassLoader createPathClassLoader(String str, int i) {
        String property = System.getProperty("java.library.path");
        return ClassLoaderFactory.createClassLoader(str, property, property, ClassLoader.getSystemClassLoader(), i, true, null);
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 5 */
    private static void performSystemServerDexOpt(String str) {
        int dexOptNeeded;
        String str2;
        String str3;
        String[] strArrSplit = str.split(SettingsStringUtil.DELIMITER);
        IInstalld iInstalldAsInterface = IInstalld.Stub.asInterface(ServiceManager.getService("installd"));
        String strVmInstructionSet = VMRuntime.getRuntime().vmInstructionSet();
        int length = strArrSplit.length;
        String strEncodeSystemServerClassPath = "";
        int i = 0;
        while (i < length) {
            String str4 = strArrSplit[i];
            String str5 = SystemProperties.get("dalvik.vm.systemservercompilerfilter", "speed");
            try {
                dexOptNeeded = DexFile.getDexOptNeeded(str4, strVmInstructionSet, str5, null, false, false);
            } catch (FileNotFoundException unused) {
                i = i;
                length = length;
                Log.w(TAG, "Missing classpath element for system server: " + str4);
                strEncodeSystemServerClassPath = strEncodeSystemServerClassPath;
            } catch (IOException e) {
                Log.w(TAG, "Error checking classpath element for system server: " + str4, e);
                dexOptNeeded = 0;
            }
            if (dexOptNeeded != 0) {
                String str6 = strEncodeSystemServerClassPath;
                try {
                    iInstalldAsInterface.dexopt(str4, 1000, PhoneConstants.APN_TYPE_ALL, strVmInstructionSet, dexOptNeeded, null, 0, str5, StorageManager.UUID_PRIVATE_INTERNAL, getSystemServerClassLoaderContext(strEncodeSystemServerClassPath), null, false, 0, null, null, "server-dexopt");
                    str2 = str4;
                } catch (RemoteException e2) {
                    e = e2;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed compiling classpath element for system server: ");
                    str2 = str4;
                    sb.append(str2);
                    Log.w(TAG, sb.toString(), e);
                } catch (ServiceSpecificException e3) {
                    e = e3;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Failed compiling classpath element for system server: ");
                    str2 = str4;
                    sb2.append(str2);
                    Log.w(TAG, sb2.toString(), e);
                }
                str3 = str6;
            } else {
                str2 = str4;
                str3 = strEncodeSystemServerClassPath;
            }
            strEncodeSystemServerClassPath = encodeSystemServerClassPath(str3, str2);
            i++;
            length = length;
        }
    }

    private static String getSystemServerClassLoaderContext(String str) {
        if (str == null) {
            return "PCL[]";
        }
        return "PCL[" + str + "]";
    }

    private static String encodeSystemServerClassPath(String str, String str2) {
        if (str == null || str.isEmpty()) {
            return str2;
        }
        return str + SettingsStringUtil.DELIMITER + str2;
    }

    private static Runnable forkSystemServer(String str, String str2, ZygoteServer zygoteServer) {
        long jPosixCapabilitiesAsBits = posixCapabilitiesAsBits(OsConstants.CAP_IPC_LOCK, OsConstants.CAP_KILL, OsConstants.CAP_NET_ADMIN, OsConstants.CAP_NET_BIND_SERVICE, OsConstants.CAP_NET_BROADCAST, OsConstants.CAP_NET_RAW, OsConstants.CAP_SYS_MODULE, OsConstants.CAP_SYS_NICE, OsConstants.CAP_SYS_PTRACE, OsConstants.CAP_SYS_TIME, OsConstants.CAP_SYS_TTY_CONFIG, OsConstants.CAP_WAKE_ALARM, OsConstants.CAP_BLOCK_SUSPEND);
        try {
            StructCapUserData[] structCapUserDataArrCapget = Os.capget(new StructCapUserHeader(OsConstants._LINUX_CAPABILITY_VERSION_3, 0));
            long j = jPosixCapabilitiesAsBits & (((long) structCapUserDataArrCapget[0].effective) | (((long) structCapUserDataArrCapget[1].effective) << 32));
            try {
                ZygoteConnection.Arguments arguments = new ZygoteConnection.Arguments(new String[]{"--setuid=1000", "--setgid=1000", "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1021,1023,1024,1032,1065,3001,3002,3003,3006,3007,3009,3010", "--capabilities=" + j + "," + j, "--nice-name=system_server", "--runtime-args", "--target-sdk-version=10000", "com.android.server.SystemServer"});
                ZygoteConnection.applyDebuggerSystemProperty(arguments);
                ZygoteConnection.applyInvokeWithSystemProperty(arguments);
                if (SystemProperties.getBoolean("dalvik.vm.profilesystemserver", false)) {
                    arguments.runtimeFlags |= 16384;
                }
                if (Zygote.forkSystemServer(arguments.uid, arguments.gid, arguments.gids, arguments.runtimeFlags, null, arguments.permittedCapabilities, arguments.effectiveCapabilities) != 0) {
                    return null;
                }
                if (hasSecondZygote(str)) {
                    waitForSecondaryZygote(str2);
                }
                zygoteServer.closeServerSocket();
                return handleSystemServerProcess(arguments);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        } catch (ErrnoException e2) {
            throw new RuntimeException("Failed to capget()", e2);
        }
    }

    private static long posixCapabilitiesAsBits(int... iArr) {
        long j = 0;
        for (int i : iArr) {
            if (i < 0 || i > OsConstants.CAP_LAST_CAP) {
                throw new IllegalArgumentException(String.valueOf(i));
            }
            j |= 1 << i;
        }
        return j;
    }

    public static void main(String[] strArr) {
        Runnable runnableForkSystemServer;
        ZygoteServer zygoteServer = new ZygoteServer();
        ZygoteHooks.startZygoteNoThreadCreation();
        try {
            Os.setpgid(0, 0);
            try {
                String strSubstring = null;
                if (!"1".equals(SystemProperties.get("sys.boot_completed"))) {
                    MetricsLogger.histogram(null, "boot_zygote_init", (int) SystemClock.elapsedRealtime());
                }
                TimingsTraceLog timingsTraceLog = new TimingsTraceLog(Process.is64Bit() ? "Zygote64Timing" : "Zygote32Timing", 16384L);
                timingsTraceLog.traceBegin("ZygoteInit");
                RuntimeInit.enableDdms();
                String strSubstring2 = Process.ZYGOTE_SOCKET;
                boolean z = false;
                boolean z2 = false;
                for (int i = 1; i < strArr.length; i++) {
                    if ("start-system-server".equals(strArr[i])) {
                        z2 = true;
                    } else if ("--enable-lazy-preload".equals(strArr[i])) {
                        z = true;
                    } else if (strArr[i].startsWith(ABI_LIST_ARG)) {
                        strSubstring = strArr[i].substring(11);
                    } else if (strArr[i].startsWith(SOCKET_NAME_ARG)) {
                        strSubstring2 = strArr[i].substring(14);
                    } else {
                        throw new RuntimeException("Unknown command line argument: " + strArr[i]);
                    }
                }
                if (strSubstring == null) {
                    throw new RuntimeException("No ABI list supplied.");
                }
                zygoteServer.registerServerSocketFromEnv(strSubstring2);
                if (!z) {
                    timingsTraceLog.traceBegin("ZygotePreload");
                    EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_START, SystemClock.uptimeMillis());
                    preload(timingsTraceLog);
                    EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_END, SystemClock.uptimeMillis());
                    timingsTraceLog.traceEnd();
                } else {
                    Zygote.resetNicePriority();
                }
                timingsTraceLog.traceBegin("PreloadResources");
                preloadResources();
                timingsTraceLog.traceEnd();
                preloadTextResources();
                timingsTraceLog.traceBegin("PostZygoteInitGC");
                gcAndFinalize();
                timingsTraceLog.traceEnd();
                timingsTraceLog.traceEnd();
                Trace.setTracingEnabled(false, 0);
                Zygote.nativeSecurityInit();
                Zygote.nativeUnmountStorageOnInit();
                ZygoteHooks.stopZygoteNoThreadCreation();
                if (z2 && (runnableForkSystemServer = forkSystemServer(strSubstring, strSubstring2, zygoteServer)) != null) {
                    runnableForkSystemServer.run();
                    zygoteServer.closeServerSocket();
                    return;
                }
                Log.i(TAG, "Accepting command socket connections");
                Runnable runnableRunSelectLoop = zygoteServer.runSelectLoop(strSubstring);
                zygoteServer.closeServerSocket();
                if (runnableRunSelectLoop != null) {
                    runnableRunSelectLoop.run();
                }
            } catch (Throwable th) {
                try {
                    Log.e(TAG, "System zygote died with exception", th);
                    throw th;
                } catch (Throwable th2) {
                    zygoteServer.closeServerSocket();
                    throw th2;
                }
            }
        } catch (ErrnoException e) {
            throw new RuntimeException("Failed to setpgid(0,0)", e);
        }
    }

    private static boolean hasSecondZygote(String str) {
        return !SystemProperties.get("ro.product.cpu.abilist").equals(str);
    }

    private static void waitForSecondaryZygote(String str) {
        String str2 = Process.ZYGOTE_SOCKET;
        if (Process.ZYGOTE_SOCKET.equals(str)) {
            str2 = Process.SECONDARY_ZYGOTE_SOCKET;
        }
        ZygoteProcess.waitForConnectionToZygote(str2);
    }

    static boolean isPreloadComplete() {
        return sPreloadComplete;
    }

    private ZygoteInit() {
    }

    public static final Runnable zygoteInit(int i, String[] strArr, ClassLoader classLoader) {
        Trace.traceBegin(64L, "ZygoteInit");
        RuntimeInit.redirectLogStreams();
        RuntimeInit.commonInit();
        nativeZygoteInit();
        return RuntimeInit.applicationInit(i, strArr, classLoader);
    }

    static final Runnable childZygoteInit(int i, String[] strArr, ClassLoader classLoader) {
        RuntimeInit.Arguments arguments = new RuntimeInit.Arguments(strArr);
        return RuntimeInit.findStaticMain(arguments.startClass, arguments.startArgs, classLoader);
    }
}
