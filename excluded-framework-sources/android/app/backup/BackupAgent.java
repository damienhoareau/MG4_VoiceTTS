package android.app.backup;

import android.app.IBackupAgent;
import android.app.QueuedWork;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.ArraySet;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public abstract class BackupAgent extends ContextWrapper {
    private static final boolean DEBUG = false;
    public static final int FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED = 1;
    public static final int FLAG_DEVICE_TO_DEVICE_TRANSFER = 2;
    public static final int FLAG_FAKE_CLIENT_SIDE_ENCRYPTION_ENABLED = Integer.MIN_VALUE;
    private static final String TAG = "BackupAgent";
    public static final int TYPE_DIRECTORY = 2;
    public static final int TYPE_EOF = 0;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_SYMLINK = 3;
    private final IBinder mBinder;
    Handler mHandler;

    private boolean areIncludeRequiredTransportFlagsSatisfied(int i, int i2) {
        return (i2 & i) == i;
    }

    public abstract void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) throws IOException;

    public void onCreate() {
    }

    public void onDestroy() {
    }

    public void onQuotaExceeded(long j, long j2) {
    }

    public abstract void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException;

    public void onRestoreFinished() {
    }

    Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        return this.mHandler;
    }

    class SharedPrefsSynchronizer implements Runnable {
        public final CountDownLatch mLatch = new CountDownLatch(1);

        SharedPrefsSynchronizer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            QueuedWork.waitToFinish();
            this.mLatch.countDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitForSharedPrefs() {
        Handler handler = getHandler();
        SharedPrefsSynchronizer sharedPrefsSynchronizer = new SharedPrefsSynchronizer();
        handler.postAtFrontOfQueue(sharedPrefsSynchronizer);
        try {
            sharedPrefsSynchronizer.mLatch.await();
        } catch (InterruptedException unused) {
        }
    }

    public BackupAgent() {
        super(null);
        this.mHandler = null;
        this.mBinder = new BackupServiceBinder().asBinder();
    }

    public void onRestore(BackupDataInput backupDataInput, long j, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        onRestore(backupDataInput, (int) j, parcelFileDescriptor);
    }

    public void onFullBackup(FullBackupDataOutput fullBackupDataOutput) throws IOException {
        FullBackup.BackupScheme backupScheme = FullBackup.getBackupScheme(this);
        if (backupScheme.isFullBackupContentEnabled()) {
            try {
                Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> mapMaybeParseAndGetCanonicalIncludePaths = backupScheme.maybeParseAndGetCanonicalIncludePaths();
                ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> arraySetMaybeParseAndGetCanonicalExcludePaths = backupScheme.maybeParseAndGetCanonicalExcludePaths();
                String packageName = getPackageName();
                ApplicationInfo applicationInfo = getApplicationInfo();
                Context contextCreateCredentialProtectedStorageContext = createCredentialProtectedStorageContext();
                String canonicalPath = contextCreateCredentialProtectedStorageContext.getDataDir().getCanonicalPath();
                String canonicalPath2 = contextCreateCredentialProtectedStorageContext.getFilesDir().getCanonicalPath();
                String canonicalPath3 = contextCreateCredentialProtectedStorageContext.getNoBackupFilesDir().getCanonicalPath();
                String canonicalPath4 = contextCreateCredentialProtectedStorageContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
                String canonicalPath5 = contextCreateCredentialProtectedStorageContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
                String canonicalPath6 = contextCreateCredentialProtectedStorageContext.getCacheDir().getCanonicalPath();
                String canonicalPath7 = contextCreateCredentialProtectedStorageContext.getCodeCacheDir().getCanonicalPath();
                Context contextCreateDeviceProtectedStorageContext = createDeviceProtectedStorageContext();
                String canonicalPath8 = contextCreateDeviceProtectedStorageContext.getDataDir().getCanonicalPath();
                String canonicalPath9 = contextCreateDeviceProtectedStorageContext.getFilesDir().getCanonicalPath();
                String canonicalPath10 = contextCreateDeviceProtectedStorageContext.getNoBackupFilesDir().getCanonicalPath();
                String canonicalPath11 = contextCreateDeviceProtectedStorageContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
                String canonicalPath12 = contextCreateDeviceProtectedStorageContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
                String canonicalPath13 = contextCreateDeviceProtectedStorageContext.getCacheDir().getCanonicalPath();
                String canonicalPath14 = contextCreateDeviceProtectedStorageContext.getCodeCacheDir().getCanonicalPath();
                String canonicalPath15 = applicationInfo.nativeLibraryDir != null ? new File(applicationInfo.nativeLibraryDir).getCanonicalPath() : null;
                ArraySet<String> arraySet = new ArraySet<>();
                arraySet.add(canonicalPath2);
                arraySet.add(canonicalPath3);
                arraySet.add(canonicalPath4);
                arraySet.add(canonicalPath5);
                arraySet.add(canonicalPath6);
                arraySet.add(canonicalPath7);
                arraySet.add(canonicalPath9);
                arraySet.add(canonicalPath10);
                arraySet.add(canonicalPath11);
                arraySet.add(canonicalPath12);
                arraySet.add(canonicalPath13);
                arraySet.add(canonicalPath14);
                if (canonicalPath15 != null) {
                    arraySet.add(canonicalPath15);
                }
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.ROOT_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_ROOT_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath8);
                arraySet.remove(canonicalPath2);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.FILES_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath2);
                arraySet.remove(canonicalPath9);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_FILES_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath9);
                arraySet.remove(canonicalPath4);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DATABASE_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath4);
                arraySet.remove(canonicalPath11);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_DATABASE_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath11);
                arraySet.remove(canonicalPath5);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.SHAREDPREFS_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath5);
                arraySet.remove(canonicalPath12);
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
                arraySet.add(canonicalPath12);
                if (Process.myUid() == 1000 || getExternalFilesDir(null) == null) {
                    return;
                }
                applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.MANAGED_EXTERNAL_TREE_TOKEN, mapMaybeParseAndGetCanonicalIncludePaths, arraySetMaybeParseAndGetCanonicalExcludePaths, arraySet, fullBackupDataOutput);
            } catch (IOException | XmlPullParserException e) {
                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                    Log.v("BackupXmlParserLogging", "Exception trying to parse fullBackupContent xml file! Aborting full backup.", e);
                }
            }
        }
    }

    private void applyXmlFiltersAndDoFullBackupForDomain(String str, String str2, Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> map, ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> arraySet, ArraySet<String> arraySet2, FullBackupDataOutput fullBackupDataOutput) throws IOException {
        if (map == null || map.size() == 0) {
            fullBackupFileTree(str, str2, FullBackup.getBackupScheme(this).tokenToDirectoryPath(str2), arraySet, arraySet2, fullBackupDataOutput);
            return;
        }
        if (map.get(str2) != null) {
            for (FullBackup.BackupScheme.PathWithRequiredFlags pathWithRequiredFlags : map.get(str2)) {
                if (areIncludeRequiredTransportFlagsSatisfied(pathWithRequiredFlags.getRequiredFlags(), fullBackupDataOutput.getTransportFlags())) {
                    fullBackupFileTree(str, str2, pathWithRequiredFlags.getPath(), arraySet, arraySet2, fullBackupDataOutput);
                }
            }
        }
    }

    public final void fullBackupFile(File file, FullBackupDataOutput fullBackupDataOutput) {
        String str;
        String str2;
        String str3;
        String str4;
        ApplicationInfo applicationInfo = getApplicationInfo();
        try {
            Context contextCreateCredentialProtectedStorageContext = createCredentialProtectedStorageContext();
            String canonicalPath = contextCreateCredentialProtectedStorageContext.getDataDir().getCanonicalPath();
            String canonicalPath2 = contextCreateCredentialProtectedStorageContext.getFilesDir().getCanonicalPath();
            String canonicalPath3 = contextCreateCredentialProtectedStorageContext.getNoBackupFilesDir().getCanonicalPath();
            String canonicalPath4 = contextCreateCredentialProtectedStorageContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            String canonicalPath5 = contextCreateCredentialProtectedStorageContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            String canonicalPath6 = contextCreateCredentialProtectedStorageContext.getCacheDir().getCanonicalPath();
            String canonicalPath7 = contextCreateCredentialProtectedStorageContext.getCodeCacheDir().getCanonicalPath();
            Context contextCreateDeviceProtectedStorageContext = createDeviceProtectedStorageContext();
            String canonicalPath8 = contextCreateDeviceProtectedStorageContext.getDataDir().getCanonicalPath();
            String canonicalPath9 = contextCreateDeviceProtectedStorageContext.getFilesDir().getCanonicalPath();
            String canonicalPath10 = contextCreateDeviceProtectedStorageContext.getNoBackupFilesDir().getCanonicalPath();
            String canonicalPath11 = contextCreateDeviceProtectedStorageContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            String canonicalPath12 = contextCreateDeviceProtectedStorageContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            String canonicalPath13 = contextCreateDeviceProtectedStorageContext.getCacheDir().getCanonicalPath();
            String canonicalPath14 = contextCreateDeviceProtectedStorageContext.getCodeCacheDir().getCanonicalPath();
            try {
                String canonicalPath15 = null;
                String canonicalPath16 = applicationInfo.nativeLibraryDir == null ? null : new File(applicationInfo.nativeLibraryDir).getCanonicalPath();
                if (Process.myUid() != 1000) {
                    try {
                        File externalFilesDir = getExternalFilesDir(null);
                        if (externalFilesDir != null) {
                            canonicalPath15 = externalFilesDir.getCanonicalPath();
                        }
                    } catch (IOException unused) {
                        str = TAG;
                        Log.w(str, "Unable to obtain canonical paths");
                        return;
                    }
                }
                String canonicalPath17 = file.getCanonicalPath();
                if (canonicalPath17.startsWith(canonicalPath6) || canonicalPath17.startsWith(canonicalPath7) || canonicalPath17.startsWith(canonicalPath3) || canonicalPath17.startsWith(canonicalPath13) || canonicalPath17.startsWith(canonicalPath14) || canonicalPath17.startsWith(canonicalPath10) || canonicalPath17.startsWith(canonicalPath16)) {
                    Log.w(TAG, "lib, cache, code_cache, and no_backup files are not backed up");
                    return;
                }
                if (canonicalPath17.startsWith(canonicalPath4)) {
                    str2 = FullBackup.DATABASE_TREE_TOKEN;
                    str3 = canonicalPath4;
                } else if (canonicalPath17.startsWith(canonicalPath5)) {
                    str2 = FullBackup.SHAREDPREFS_TREE_TOKEN;
                    str3 = canonicalPath5;
                } else if (canonicalPath17.startsWith(canonicalPath2)) {
                    str2 = FullBackup.FILES_TREE_TOKEN;
                    str3 = canonicalPath2;
                } else if (canonicalPath17.startsWith(canonicalPath)) {
                    str2 = FullBackup.ROOT_TREE_TOKEN;
                    str3 = canonicalPath;
                } else if (canonicalPath17.startsWith(canonicalPath11)) {
                    str2 = FullBackup.DEVICE_DATABASE_TREE_TOKEN;
                    str3 = canonicalPath11;
                } else if (canonicalPath17.startsWith(canonicalPath12)) {
                    str2 = FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN;
                    str3 = canonicalPath12;
                } else {
                    String str5 = canonicalPath9;
                    if (canonicalPath17.startsWith(str5)) {
                        str4 = FullBackup.DEVICE_FILES_TREE_TOKEN;
                    } else {
                        str5 = canonicalPath8;
                        if (canonicalPath17.startsWith(str5)) {
                            str4 = FullBackup.DEVICE_ROOT_TREE_TOKEN;
                        } else {
                            if (canonicalPath15 == null || !canonicalPath17.startsWith(canonicalPath15)) {
                                Log.w(TAG, "File " + canonicalPath17 + " is in an unsupported location; skipping");
                                return;
                            }
                            str2 = FullBackup.MANAGED_EXTERNAL_TREE_TOKEN;
                            str3 = canonicalPath15;
                        }
                    }
                    str3 = str5;
                    str2 = str4;
                }
                FullBackup.backupToTar(getPackageName(), str2, null, str3, canonicalPath17, fullBackupDataOutput);
            } catch (IOException unused2) {
            }
        } catch (IOException unused3) {
            str = TAG;
        }
    }

    protected final void fullBackupFileTree(String str, String str2, String str3, ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> arraySet, ArraySet<String> arraySet2, FullBackupDataOutput fullBackupDataOutput) {
        File[] fileArrListFiles;
        String str4 = FullBackup.getBackupScheme(this).tokenToDirectoryPath(str2);
        if (str4 == null) {
            return;
        }
        File file = new File(str3);
        if (file.exists()) {
            LinkedList linkedList = new LinkedList();
            linkedList.add(file);
            while (linkedList.size() > 0) {
                File file2 = (File) linkedList.remove(0);
                try {
                    StructStat structStatLstat = Os.lstat(file2.getPath());
                    if (OsConstants.S_ISREG(structStatLstat.st_mode) || OsConstants.S_ISDIR(structStatLstat.st_mode)) {
                        String canonicalPath = file2.getCanonicalPath();
                        if (arraySet != null) {
                            try {
                                if (manifestExcludesContainFilePath(arraySet, canonicalPath)) {
                                }
                            } catch (ErrnoException e) {
                                e = e;
                                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                                    Log.v("BackupXmlParserLogging", "Error scanning file " + file2 + " : " + e);
                                }
                            } catch (IOException unused) {
                                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                                    Log.v("BackupXmlParserLogging", "Error canonicalizing path of " + file2);
                                }
                            }
                        }
                        if (arraySet2 == null || !arraySet2.contains(canonicalPath)) {
                            if (OsConstants.S_ISDIR(structStatLstat.st_mode) && (fileArrListFiles = file2.listFiles()) != null) {
                                for (File file3 : fileArrListFiles) {
                                    linkedList.add(0, file3);
                                }
                            }
                            FullBackup.backupToTar(str, str2, null, str4, canonicalPath, fullBackupDataOutput);
                        }
                    }
                } catch (ErrnoException e2) {
                    e = e2;
                } catch (IOException unused2) {
                }
            }
        }
    }

    private boolean manifestExcludesContainFilePath(ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> arraySet, String str) {
        Iterator<FullBackup.BackupScheme.PathWithRequiredFlags> it = arraySet.iterator();
        while (it.hasNext()) {
            String path = it.next().getPath();
            if (path != null && path.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, File file, int i, long j2, long j3) throws IOException {
        File file2 = file;
        if (!isFileEligibleForRestore(file)) {
            file2 = null;
        }
        FullBackup.restoreFile(parcelFileDescriptor, j, i, j2, j3, file2);
    }

    private boolean isFileEligibleForRestore(File file) throws IOException {
        FullBackup.BackupScheme backupScheme = FullBackup.getBackupScheme(this);
        if (!backupScheme.isFullBackupContentEnabled()) {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.v("BackupXmlParserLogging", "onRestoreFile \"" + file.getCanonicalPath() + "\" : fullBackupContent not enabled for " + getPackageName());
            }
            return false;
        }
        String canonicalPath = file.getCanonicalPath();
        try {
            Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> mapMaybeParseAndGetCanonicalIncludePaths = backupScheme.maybeParseAndGetCanonicalIncludePaths();
            ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> arraySetMaybeParseAndGetCanonicalExcludePaths = backupScheme.maybeParseAndGetCanonicalExcludePaths();
            if (arraySetMaybeParseAndGetCanonicalExcludePaths != null && isFileSpecifiedInPathList(file, arraySetMaybeParseAndGetCanonicalExcludePaths)) {
                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                    Log.v("BackupXmlParserLogging", "onRestoreFile: \"" + canonicalPath + "\": listed in excludes; skipping.");
                }
                return false;
            }
            if (mapMaybeParseAndGetCanonicalIncludePaths == null || mapMaybeParseAndGetCanonicalIncludePaths.isEmpty()) {
                return true;
            }
            Iterator<Set<FullBackup.BackupScheme.PathWithRequiredFlags>> it = mapMaybeParseAndGetCanonicalIncludePaths.values().iterator();
            boolean zIsFileSpecifiedInPathList = false;
            while (it.hasNext() && !((zIsFileSpecifiedInPathList = zIsFileSpecifiedInPathList | isFileSpecifiedInPathList(file, it.next())))) {
            }
            if (zIsFileSpecifiedInPathList) {
                return true;
            }
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.v("BackupXmlParserLogging", "onRestoreFile: Trying to restore \"" + canonicalPath + "\" but it isn't specified in the included files; skipping.");
            }
            return false;
        } catch (XmlPullParserException e) {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.v("BackupXmlParserLogging", "onRestoreFile \"" + canonicalPath + "\" : Exception trying to parse fullBackupContent xml file! Aborting onRestoreFile.", e);
            }
            return false;
        }
    }

    private boolean isFileSpecifiedInPathList(File file, Collection<FullBackup.BackupScheme.PathWithRequiredFlags> collection) throws IOException {
        Iterator<FullBackup.BackupScheme.PathWithRequiredFlags> it = collection.iterator();
        while (it.hasNext()) {
            String path = it.next().getPath();
            File file2 = new File(path);
            if (file2.isDirectory()) {
                if (file.isDirectory()) {
                    return file.equals(file2);
                }
                return file.getCanonicalPath().startsWith(path);
            }
            if (file.equals(file2)) {
                return true;
            }
        }
        return false;
    }

    protected void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3) throws IOException {
        String str3 = FullBackup.getBackupScheme(this).tokenToDirectoryPath(str);
        long j4 = str.equals(FullBackup.MANAGED_EXTERNAL_TREE_TOKEN) ? -1L : j2;
        if (str3 != null) {
            File file = new File(str3, str2);
            if (file.getCanonicalPath().startsWith(str3 + File.separatorChar)) {
                onRestoreFile(parcelFileDescriptor, j, file, i, j4, j3);
                return;
            }
        }
        FullBackup.restoreFile(parcelFileDescriptor, j, i, j4, j3, null);
    }

    public final IBinder onBind() {
        return this.mBinder;
    }

    public void attach(Context context) {
        attachBaseContext(context);
    }

    private class BackupServiceBinder extends IBackupAgent.Stub {
        private static final String TAG = "BackupServiceBinder";

        private BackupServiceBinder() {
        }

        @Override // android.app.IBackupAgent
        public void doBackup(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, long j, int i, IBackupManager iBackupManager, int i2) throws RemoteException {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    try {
                        BackupAgent.this.onBackup(parcelFileDescriptor, new BackupDataOutput(parcelFileDescriptor2.getFileDescriptor(), j, i2), parcelFileDescriptor3);
                        BackupAgent.this.waitForSharedPrefs();
                        Binder.restoreCallingIdentity(jClearCallingIdentity);
                        try {
                            iBackupManager.opComplete(i, 0L);
                        } catch (RemoteException unused) {
                        }
                        if (Binder.getCallingPid() != Process.myPid()) {
                            IoUtils.closeQuietly(parcelFileDescriptor);
                            IoUtils.closeQuietly(parcelFileDescriptor2);
                            IoUtils.closeQuietly(parcelFileDescriptor3);
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e2) {
                    Log.d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw e2;
                }
            } catch (Throwable th) {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                try {
                    iBackupManager.opComplete(i, 0L);
                } catch (RemoteException unused2) {
                }
                if (Binder.getCallingPid() == Process.myPid()) {
                    throw th;
                }
                IoUtils.closeQuietly(parcelFileDescriptor);
                IoUtils.closeQuietly(parcelFileDescriptor2);
                IoUtils.closeQuietly(parcelFileDescriptor3);
                throw th;
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestore(ParcelFileDescriptor parcelFileDescriptor, long j, ParcelFileDescriptor parcelFileDescriptor2, int i, IBackupManager iBackupManager) throws RemoteException {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            BackupAgent.this.waitForSharedPrefs();
            try {
                try {
                    BackupAgent.this.onRestore(new BackupDataInput(parcelFileDescriptor.getFileDescriptor()), j, parcelFileDescriptor2);
                    BackupAgent.this.reloadSharedPreferences();
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    try {
                        iBackupManager.opComplete(i, 0L);
                    } catch (RemoteException unused) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(parcelFileDescriptor);
                        IoUtils.closeQuietly(parcelFileDescriptor2);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + ") threw", e);
                    throw new RuntimeException(e);
                } catch (RuntimeException e2) {
                    Log.d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw e2;
                }
            } catch (Throwable th) {
                BackupAgent.this.reloadSharedPreferences();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                try {
                    iBackupManager.opComplete(i, 0L);
                } catch (RemoteException unused2) {
                }
                if (Binder.getCallingPid() == Process.myPid()) {
                    throw th;
                }
                IoUtils.closeQuietly(parcelFileDescriptor);
                IoUtils.closeQuietly(parcelFileDescriptor2);
                throw th;
            }
        }

        /* JADX WARN: Code duplicated, block: B:40:0x00d4  */
        /* JADX WARN: Code duplicated, block: B:57:? A[SYNTHETIC] */
        @Override // android.app.IBackupAgent
        public void doFullBackup(ParcelFileDescriptor parcelFileDescriptor, long j, int i, IBackupManager iBackupManager, int i2) throws Throwable {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            BackupAgent.this.waitForSharedPrefs();
            try {
                try {
                    try {
                        BackupAgent.this.onFullBackup(new FullBackupDataOutput(parcelFileDescriptor, j, i2));
                        BackupAgent.this.waitForSharedPrefs();
                        try {
                            new FileOutputStream(parcelFileDescriptor.getFileDescriptor()).write(new byte[4]);
                        } catch (IOException unused) {
                            Log.e(TAG, "Unable to finalize backup stream!");
                        }
                        Binder.restoreCallingIdentity(jClearCallingIdentity);
                        try {
                            iBackupManager.opComplete(i, 0L);
                        } catch (RemoteException unused2) {
                        }
                        if (Binder.getCallingPid() != Process.myPid()) {
                            IoUtils.closeQuietly(parcelFileDescriptor);
                        }
                    } catch (IOException e) {
                        e = e;
                        Log.d(TAG, "onFullBackup (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw new RuntimeException(e);
                    } catch (RuntimeException e2) {
                        e = e2;
                        Log.d(TAG, "onFullBackup (" + BackupAgent.this.getClass().getName() + ") threw", e);
                        throw e;
                    }
                } catch (Throwable th) {
                    th = th;
                    BackupAgent.this.waitForSharedPrefs();
                    try {
                        new FileOutputStream(parcelFileDescriptor.getFileDescriptor()).write(new byte[4]);
                    } catch (IOException unused3) {
                        Log.e(TAG, "Unable to finalize backup stream!");
                    }
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    try {
                        iBackupManager.opComplete(i, 0L);
                    } catch (RemoteException unused4) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        throw th;
                    }
                    IoUtils.closeQuietly(parcelFileDescriptor);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
            } catch (RuntimeException e4) {
                e = e4;
            } catch (Throwable th2) {
                th = th2;
                BackupAgent.this.waitForSharedPrefs();
                new FileOutputStream(parcelFileDescriptor.getFileDescriptor()).write(new byte[4]);
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                iBackupManager.opComplete(i, 0L);
                if (Binder.getCallingPid() != Process.myPid()) {
                    throw th;
                }
                IoUtils.closeQuietly(parcelFileDescriptor);
                throw th;
            }
        }

        @Override // android.app.IBackupAgent
        public void doMeasureFullBackup(long j, int i, IBackupManager iBackupManager, int i2) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            FullBackupDataOutput fullBackupDataOutput = new FullBackupDataOutput(j, i2);
            BackupAgent.this.waitForSharedPrefs();
            try {
                try {
                    BackupAgent.this.onFullBackup(fullBackupDataOutput);
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    try {
                        iBackupManager.opComplete(i, fullBackupDataOutput.getSize());
                    } catch (RemoteException unused) {
                    }
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    try {
                        iBackupManager.opComplete(i, fullBackupDataOutput.getSize());
                    } catch (RemoteException unused2) {
                    }
                    throw th;
                }
            } catch (IOException e) {
                Log.d(TAG, "onFullBackup[M] (" + BackupAgent.this.getClass().getName() + ") threw", e);
                throw new RuntimeException(e);
            } catch (RuntimeException e2) {
                Log.d(TAG, "onFullBackup[M] (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                throw e2;
            }
        }

        /* JADX WARN: Bottom block not found for handler: all -> 0x003d */
        @Override // android.app.IBackupAgent
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void doRestoreFile(android.os.ParcelFileDescriptor r20, long r21, int r23, java.lang.String r24, java.lang.String r25, long r26, long r28, int r30, android.app.backup.IBackupManager r31) throws android.os.RemoteException {
            /*
                r19 = this;
                r1 = r19
                r2 = r30
                r3 = r31
                long r4 = android.os.Binder.clearCallingIdentity()
                r6 = 0
                android.app.backup.BackupAgent r8 = android.app.backup.BackupAgent.this     // Catch: java.lang.Throwable -> L3d java.io.IOException -> L3f
                r9 = r20
                r10 = r21
                r12 = r23
                r13 = r24
                r14 = r25
                r15 = r26
                r17 = r28
                r8.onRestoreFile(r9, r10, r12, r13, r14, r15, r17)     // Catch: java.lang.Throwable -> L3d java.io.IOException -> L3f
                android.app.backup.BackupAgent r0 = android.app.backup.BackupAgent.this
                android.app.backup.BackupAgent.access$100(r0)
                android.app.backup.BackupAgent r0 = android.app.backup.BackupAgent.this
                r0.reloadSharedPreferences()
                android.os.Binder.restoreCallingIdentity(r4)
                r3.opComplete(r2, r6)     // Catch: android.os.RemoteException -> L2f
            L2f:
                int r0 = android.os.Binder.getCallingPid()
                int r2 = android.os.Process.myPid()
                if (r0 == r2) goto L3c
                libcore.io.IoUtils.closeQuietly(r20)
            L3c:
                return
            L3d:
                r0 = move-exception
                goto L6c
            L3f:
                r0 = move-exception
                java.lang.String r8 = "BackupServiceBinder"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L3d
                r9.<init>()     // Catch: java.lang.Throwable -> L3d
                java.lang.String r10 = "onRestoreFile ("
                r9.append(r10)     // Catch: java.lang.Throwable -> L3d
                android.app.backup.BackupAgent r10 = android.app.backup.BackupAgent.this     // Catch: java.lang.Throwable -> L3d
                java.lang.Class r10 = r10.getClass()     // Catch: java.lang.Throwable -> L3d
                java.lang.String r10 = r10.getName()     // Catch: java.lang.Throwable -> L3d
                r9.append(r10)     // Catch: java.lang.Throwable -> L3d
                java.lang.String r10 = ") threw"
                r9.append(r10)     // Catch: java.lang.Throwable -> L3d
                java.lang.String r9 = r9.toString()     // Catch: java.lang.Throwable -> L3d
                android.util.Log.d(r8, r9, r0)     // Catch: java.lang.Throwable -> L3d
                java.lang.RuntimeException r8 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L3d
                r8.<init>(r0)     // Catch: java.lang.Throwable -> L3d
                throw r8     // Catch: java.lang.Throwable -> L3d
            L6c:
                android.app.backup.BackupAgent r8 = android.app.backup.BackupAgent.this
                android.app.backup.BackupAgent.access$100(r8)
                android.app.backup.BackupAgent r8 = android.app.backup.BackupAgent.this
                r8.reloadSharedPreferences()
                android.os.Binder.restoreCallingIdentity(r4)
                r3.opComplete(r2, r6)     // Catch: android.os.RemoteException -> L7c
            L7c:
                int r2 = android.os.Binder.getCallingPid()
                int r3 = android.os.Process.myPid()
                if (r2 == r3) goto L89
                libcore.io.IoUtils.closeQuietly(r20)
            L89:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.backup.BackupAgent.BackupServiceBinder.doRestoreFile(android.os.ParcelFileDescriptor, long, int, java.lang.String, java.lang.String, long, long, int, android.app.backup.IBackupManager):void");
        }

        /* JADX WARN: Bottom block not found for handler: all -> 0x0017 */
        @Override // android.app.IBackupAgent
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void doRestoreFinished(int r9, android.app.backup.IBackupManager r10) {
            /*
                r8 = this;
                long r0 = android.os.Binder.clearCallingIdentity()
                r2 = 0
                android.app.backup.BackupAgent r4 = android.app.backup.BackupAgent.this     // Catch: java.lang.Throwable -> L17 java.lang.Exception -> L19
                r4.onRestoreFinished()     // Catch: java.lang.Throwable -> L17 java.lang.Exception -> L19
                android.app.backup.BackupAgent r4 = android.app.backup.BackupAgent.this
                android.app.backup.BackupAgent.access$100(r4)
                android.os.Binder.restoreCallingIdentity(r0)
                r10.opComplete(r9, r2)     // Catch: android.os.RemoteException -> L16
            L16:
                return
            L17:
                r4 = move-exception
                goto L41
            L19:
                r4 = move-exception
                java.lang.String r5 = "BackupServiceBinder"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L17
                r6.<init>()     // Catch: java.lang.Throwable -> L17
                java.lang.String r7 = "onRestoreFinished ("
                r6.append(r7)     // Catch: java.lang.Throwable -> L17
                android.app.backup.BackupAgent r7 = android.app.backup.BackupAgent.this     // Catch: java.lang.Throwable -> L17
                java.lang.Class r7 = r7.getClass()     // Catch: java.lang.Throwable -> L17
                java.lang.String r7 = r7.getName()     // Catch: java.lang.Throwable -> L17
                r6.append(r7)     // Catch: java.lang.Throwable -> L17
                java.lang.String r7 = ") threw"
                r6.append(r7)     // Catch: java.lang.Throwable -> L17
                java.lang.String r6 = r6.toString()     // Catch: java.lang.Throwable -> L17
                android.util.Log.d(r5, r6, r4)     // Catch: java.lang.Throwable -> L17
                throw r4     // Catch: java.lang.Throwable -> L17
            L41:
                android.app.backup.BackupAgent r5 = android.app.backup.BackupAgent.this
                android.app.backup.BackupAgent.access$100(r5)
                android.os.Binder.restoreCallingIdentity(r0)
                r10.opComplete(r9, r2)     // Catch: android.os.RemoteException -> L4c
            L4c:
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.backup.BackupAgent.BackupServiceBinder.doRestoreFinished(int, android.app.backup.IBackupManager):void");
        }

        @Override // android.app.IBackupAgent
        public void fail(String str) {
            BackupAgent.this.getHandler().post(new FailRunnable(str));
        }

        @Override // android.app.IBackupAgent
        public void doQuotaExceeded(long j, long j2) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onQuotaExceeded(j, j2);
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                } catch (Exception e) {
                    Log.d(TAG, "onQuotaExceeded(" + BackupAgent.this.getClass().getName() + ") threw", e);
                    throw e;
                }
            } catch (Throwable th) {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                throw th;
            }
        }
    }

    static class FailRunnable implements Runnable {
        private String mMessage;

        FailRunnable(String str) {
            this.mMessage = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            throw new IllegalStateException(this.mMessage);
        }
    }
}
