package android.content;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.backup.FullBackup;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.CrossProcessCursorWrapper;
import android.database.Cursor;
import android.database.IContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.DeadObjectException;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.MimeIconUtils;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentResolver {
    public static final String ANY_CURSOR_ITEM_TYPE = "vnd.android.cursor.item/*";
    public static final String CONTENT_SERVICE_NAME = "content";
    public static final String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir";
    public static final String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item";
    private static final boolean ENABLE_CONTENT_SAMPLE = false;
    public static final String EXTRA_HONORED_ARGS = "android.content.extra.HONORED_ARGS";
    public static final String EXTRA_REFRESH_SUPPORTED = "android.content.extra.REFRESH_SUPPORTED";
    public static final String EXTRA_SIZE = "android.content.extra.SIZE";
    public static final String EXTRA_TOTAL_COUNT = "android.content.extra.TOTAL_COUNT";
    public static final int NOTIFY_SKIP_NOTIFY_FOR_DESCENDANTS = 2;
    public static final int NOTIFY_SYNC_TO_NETWORK = 1;
    public static final String QUERY_ARG_LIMIT = "android:query-arg-limit";
    public static final String QUERY_ARG_OFFSET = "android:query-arg-offset";
    public static final String QUERY_ARG_SORT_COLLATION = "android:query-arg-sort-collation";
    public static final String QUERY_ARG_SORT_COLUMNS = "android:query-arg-sort-columns";
    public static final String QUERY_ARG_SORT_DIRECTION = "android:query-arg-sort-direction";
    public static final String QUERY_ARG_SQL_SELECTION = "android:query-arg-sql-selection";
    public static final String QUERY_ARG_SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args";
    public static final String QUERY_ARG_SQL_SORT_ORDER = "android:query-arg-sql-sort-order";
    public static final int QUERY_SORT_DIRECTION_ASCENDING = 0;
    public static final int QUERY_SORT_DIRECTION_DESCENDING = 1;
    public static final String SCHEME_ANDROID_RESOURCE = "android.resource";
    public static final String SCHEME_CONTENT = "content";
    public static final String SCHEME_FILE = "file";
    private static final int SLOW_THRESHOLD_MILLIS = 500;
    public static final int SYNC_ERROR_AUTHENTICATION = 2;
    public static final int SYNC_ERROR_CONFLICT = 5;
    public static final int SYNC_ERROR_INTERNAL = 8;
    public static final int SYNC_ERROR_IO = 3;
    public static final int SYNC_ERROR_PARSE = 4;
    public static final int SYNC_ERROR_SYNC_ALREADY_IN_PROGRESS = 1;
    public static final int SYNC_ERROR_TOO_MANY_DELETIONS = 6;
    public static final int SYNC_ERROR_TOO_MANY_RETRIES = 7;
    public static final int SYNC_EXEMPTION_NONE = 0;
    public static final int SYNC_EXEMPTION_PROMOTE_BUCKET = 1;
    public static final int SYNC_EXEMPTION_PROMOTE_BUCKET_WITH_TEMP = 2;

    @Deprecated
    public static final String SYNC_EXTRAS_ACCOUNT = "account";
    public static final String SYNC_EXTRAS_DISALLOW_METERED = "allow_metered";
    public static final String SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = "discard_deletions";
    public static final String SYNC_EXTRAS_DO_NOT_RETRY = "do_not_retry";
    public static final String SYNC_EXTRAS_EXPECTED_DOWNLOAD = "expected_download";
    public static final String SYNC_EXTRAS_EXPECTED_UPLOAD = "expected_upload";
    public static final String SYNC_EXTRAS_EXPEDITED = "expedited";

    @Deprecated
    public static final String SYNC_EXTRAS_FORCE = "force";
    public static final String SYNC_EXTRAS_IGNORE_BACKOFF = "ignore_backoff";
    public static final String SYNC_EXTRAS_IGNORE_SETTINGS = "ignore_settings";
    public static final String SYNC_EXTRAS_INITIALIZE = "initialize";
    public static final String SYNC_EXTRAS_MANUAL = "force";
    public static final String SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = "deletions_override";
    public static final String SYNC_EXTRAS_PRIORITY = "sync_priority";
    public static final String SYNC_EXTRAS_REQUIRE_CHARGING = "require_charging";
    public static final String SYNC_EXTRAS_UPLOAD = "upload";
    public static final int SYNC_OBSERVER_TYPE_ACTIVE = 4;
    public static final int SYNC_OBSERVER_TYPE_ALL = Integer.MAX_VALUE;
    public static final int SYNC_OBSERVER_TYPE_PENDING = 2;
    public static final int SYNC_OBSERVER_TYPE_SETTINGS = 1;
    public static final int SYNC_OBSERVER_TYPE_STATUS = 8;
    public static final String SYNC_VIRTUAL_EXTRAS_EXEMPTION_FLAG = "v_exemption";
    private static final String TAG = "ContentResolver";
    private static volatile IContentService sContentService;
    private final Context mContext;
    final String mPackageName;
    private final Random mRandom = new Random();
    final int mTargetSdkVersion;
    public static final Intent ACTION_SYNC_CONN_STATUS_CHANGED = new Intent("com.android.sync.SYNC_CONN_STATUS_CHANGED");
    private static final String[] SYNC_ERROR_NAMES = {"already-in-progress", "authentication-error", "io-error", "parse-error", "conflict", "too-many-deletions", "too-many-retries", "internal-error"};

    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryCollator {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SortDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SyncExemption {
    }

    private void maybeLogQueryToEventLog(long j, Uri uri, String[] strArr, Bundle bundle) {
    }

    private void maybeLogUpdateToEventLog(long j, Uri uri, String str, String str2) {
    }

    protected abstract IContentProvider acquireProvider(Context context, String str);

    protected abstract IContentProvider acquireUnstableProvider(Context context, String str);

    public abstract boolean releaseProvider(IContentProvider iContentProvider);

    public abstract boolean releaseUnstableProvider(IContentProvider iContentProvider);

    public abstract void unstableProviderDied(IContentProvider iContentProvider);

    public static String syncErrorToString(int i) {
        if (i >= 1) {
            String[] strArr = SYNC_ERROR_NAMES;
            if (i <= strArr.length) {
                return strArr[i - 1];
            }
        }
        return String.valueOf(i);
    }

    public static int syncErrorStringToInt(String str) {
        int length = SYNC_ERROR_NAMES.length;
        for (int i = 0; i < length; i++) {
            if (SYNC_ERROR_NAMES[i].equals(str)) {
                return i + 1;
            }
        }
        if (str != null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                Log.d(TAG, "error parsing sync error: " + str);
            }
        }
        return 0;
    }

    public ContentResolver(Context context) {
        context = context == null ? ActivityThread.currentApplication() : context;
        this.mContext = context;
        this.mPackageName = context.getOpPackageName();
        this.mTargetSdkVersion = this.mContext.getApplicationInfo().targetSdkVersion;
    }

    protected IContentProvider acquireExistingProvider(Context context, String str) {
        return acquireProvider(context, str);
    }

    public void appNotRespondingViaProvider(IContentProvider iContentProvider) {
        throw new UnsupportedOperationException("appNotRespondingViaProvider");
    }

    public final String getType(Uri uri) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireExistingProvider = acquireExistingProvider(uri);
        try {
            if (iContentProviderAcquireExistingProvider != null) {
                return iContentProviderAcquireExistingProvider.getType(uri);
            }
            if (!"content".equals(uri.getScheme())) {
                return null;
            }
            try {
                return ActivityManager.getService().getProviderMimeType(ContentProvider.getUriWithoutUserId(uri), resolveUserId(uri));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (Exception e2) {
                Log.w(TAG, "Failed to get type for: " + uri + " (" + e2.getMessage() + ")");
                return null;
            }
        } catch (RemoteException unused) {
            return null;
        } catch (Exception e3) {
            Log.w(TAG, "Failed to get type for: " + uri + " (" + e3.getMessage() + ")");
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireExistingProvider);
        }
    }

    public String[] getStreamTypes(Uri uri, String str) {
        Preconditions.checkNotNull(uri, "url");
        Preconditions.checkNotNull(str, "mimeTypeFilter");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            return null;
        }
        try {
            return iContentProviderAcquireProvider.getStreamTypes(uri, str);
        } catch (RemoteException unused) {
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return query(uri, strArr, str, strArr2, str2, null);
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return query(uri, strArr, createSqlQueryBundle(str, strArr2, str2), cancellationSignal);
    }

    /* JADX WARN: Code duplicated, block: B:51:0x00b1  */
    /* JADX WARN: Code duplicated, block: B:53:0x00b6  */
    /* JADX WARN: Code duplicated, block: B:55:0x00bb  */
    /* JADX WARN: Code duplicated, block: B:57:0x00c0  */
    /* JADX WARN: Code duplicated, block: B:61:0x00c8  */
    /* JADX WARN: Code duplicated, block: B:63:0x00cd  */
    /* JADX WARN: Code duplicated, block: B:65:0x00d2  */
    /* JADX WARN: Code duplicated, block: B:67:0x00d7  */
    public final Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) throws Throwable {
        Cursor cursorQuery;
        IContentProvider iContentProvider;
        ICancellationSignal iCancellationSignalCreateCancellationSignal;
        Preconditions.checkNotNull(uri, "uri");
        IContentProvider iContentProviderAcquireUnstableProvider = acquireUnstableProvider(uri);
        if (iContentProviderAcquireUnstableProvider == null) {
            return null;
        }
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (cancellationSignal != null) {
                cancellationSignal.throwIfCanceled();
                iCancellationSignalCreateCancellationSignal = iContentProviderAcquireUnstableProvider.createCancellationSignal();
                cancellationSignal.setRemote(iCancellationSignalCreateCancellationSignal);
            } else {
                iCancellationSignalCreateCancellationSignal = null;
            }
            try {
                cursorQuery = iContentProviderAcquireUnstableProvider.query(this.mPackageName, uri, strArr, bundle, iCancellationSignalCreateCancellationSignal);
                iContentProvider = null;
            } catch (DeadObjectException unused) {
                unstableProviderDied(iContentProviderAcquireUnstableProvider);
                IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
                if (iContentProviderAcquireProvider == null) {
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (iContentProviderAcquireUnstableProvider != null) {
                        releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    }
                    if (iContentProviderAcquireProvider != null) {
                        releaseProvider(iContentProviderAcquireProvider);
                    }
                    return null;
                }
                try {
                    iContentProvider = iContentProviderAcquireProvider;
                    cursorQuery = iContentProviderAcquireProvider.query(this.mPackageName, uri, strArr, bundle, iCancellationSignalCreateCancellationSignal);
                } catch (RemoteException unused2) {
                    iContentProvider = iContentProviderAcquireProvider;
                    cursorQuery = null;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (iContentProviderAcquireUnstableProvider != null) {
                        releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    }
                    if (iContentProvider != null) {
                        releaseProvider(iContentProvider);
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    iContentProvider = iContentProviderAcquireProvider;
                    cursorQuery = null;
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (iContentProviderAcquireUnstableProvider != null) {
                        releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    }
                    if (iContentProvider != null) {
                        releaseProvider(iContentProvider);
                    }
                    throw th;
                }
            }
            if (cursorQuery == null) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                if (cancellationSignal != null) {
                    cancellationSignal.setRemote(null);
                }
                if (iContentProviderAcquireUnstableProvider != null) {
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                }
                if (iContentProvider != null) {
                    releaseProvider(iContentProvider);
                }
                return null;
            }
            try {
                cursorQuery.getCount();
                maybeLogQueryToEventLog(SystemClock.uptimeMillis() - jUptimeMillis, uri, strArr, bundle);
                CursorWrapperInner cursorWrapperInner = new CursorWrapperInner(cursorQuery, iContentProvider != null ? iContentProvider : acquireProvider(uri));
                if (cancellationSignal != null) {
                    cancellationSignal.setRemote(null);
                }
                if (iContentProviderAcquireUnstableProvider != null) {
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                }
                return cursorWrapperInner;
            } catch (RemoteException unused3) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                if (cancellationSignal != null) {
                    cancellationSignal.setRemote(null);
                }
                if (iContentProviderAcquireUnstableProvider != null) {
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                }
                if (iContentProvider != null) {
                    releaseProvider(iContentProvider);
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                if (cancellationSignal != null) {
                    cancellationSignal.setRemote(null);
                }
                if (iContentProviderAcquireUnstableProvider != null) {
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                }
                if (iContentProvider != null) {
                    releaseProvider(iContentProvider);
                }
                throw th;
            }
        } catch (RemoteException unused4) {
            cursorQuery = null;
            iContentProvider = null;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            if (cancellationSignal != null) {
                cancellationSignal.setRemote(null);
            }
            if (iContentProviderAcquireUnstableProvider != null) {
                releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
            }
            if (iContentProvider != null) {
                releaseProvider(iContentProvider);
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            cursorQuery = null;
            iContentProvider = null;
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            if (cancellationSignal != null) {
                cancellationSignal.setRemote(null);
            }
            if (iContentProviderAcquireUnstableProvider != null) {
                releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
            }
            if (iContentProvider != null) {
                releaseProvider(iContentProvider);
            }
            throw th;
        }
    }

    public final Uri canonicalize(Uri uri) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            return null;
        }
        try {
            return iContentProviderAcquireProvider.canonicalize(this.mPackageName, uri);
        } catch (RemoteException unused) {
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final Uri uncanonicalize(Uri uri) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            return null;
        }
        try {
            return iContentProviderAcquireProvider.uncanonicalize(this.mPackageName, uri);
        } catch (RemoteException unused) {
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final boolean refresh(Uri uri, Bundle bundle, CancellationSignal cancellationSignal) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            return false;
        }
        ICancellationSignal iCancellationSignalCreateCancellationSignal = null;
        if (cancellationSignal != null) {
            try {
                cancellationSignal.throwIfCanceled();
                iCancellationSignalCreateCancellationSignal = iContentProviderAcquireProvider.createCancellationSignal();
                cancellationSignal.setRemote(iCancellationSignalCreateCancellationSignal);
            } catch (RemoteException unused) {
                return false;
            } finally {
                releaseProvider(iContentProviderAcquireProvider);
            }
        }
        return iContentProviderAcquireProvider.refresh(this.mPackageName, uri, bundle, iCancellationSignalCreateCancellationSignal);
    }

    public final InputStream openInputStream(Uri uri) throws Throwable {
        Preconditions.checkNotNull(uri, "uri");
        String scheme = uri.getScheme();
        if (SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            OpenResourceIdResult resourceId = getResourceId(uri);
            try {
                return resourceId.r.openRawResource(resourceId.id);
            } catch (Resources.NotFoundException unused) {
                throw new FileNotFoundException("Resource does not exist: " + uri);
            }
        }
        if (SCHEME_FILE.equals(scheme)) {
            return new FileInputStream(uri.getPath());
        }
        AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN, null);
        if (assetFileDescriptorOpenAssetFileDescriptor == null) {
            return null;
        }
        try {
            return assetFileDescriptorOpenAssetFileDescriptor.createInputStream();
        } catch (IOException unused2) {
            throw new FileNotFoundException("Unable to create stream");
        }
    }

    public final OutputStream openOutputStream(Uri uri) throws FileNotFoundException {
        return openOutputStream(uri, "w");
    }

    public final OutputStream openOutputStream(Uri uri, String str) throws Throwable {
        AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = openAssetFileDescriptor(uri, str, null);
        if (assetFileDescriptorOpenAssetFileDescriptor == null) {
            return null;
        }
        try {
            return assetFileDescriptorOpenAssetFileDescriptor.createOutputStream();
        } catch (IOException unused) {
            throw new FileNotFoundException("Unable to create stream");
        }
    }

    public final ParcelFileDescriptor openFileDescriptor(Uri uri, String str) throws FileNotFoundException {
        return openFileDescriptor(uri, str, null);
    }

    public final ParcelFileDescriptor openFileDescriptor(Uri uri, String str, CancellationSignal cancellationSignal) throws Throwable {
        AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = openAssetFileDescriptor(uri, str, cancellationSignal);
        if (assetFileDescriptorOpenAssetFileDescriptor == null) {
            return null;
        }
        if (assetFileDescriptorOpenAssetFileDescriptor.getDeclaredLength() < 0) {
            return assetFileDescriptorOpenAssetFileDescriptor.getParcelFileDescriptor();
        }
        try {
            assetFileDescriptorOpenAssetFileDescriptor.close();
        } catch (IOException unused) {
        }
        throw new FileNotFoundException("Not a whole file");
    }

    public final AssetFileDescriptor openAssetFileDescriptor(Uri uri, String str) throws FileNotFoundException {
        return openAssetFileDescriptor(uri, str, null);
    }

    /* JADX WARN: Code duplicated, block: B:78:0x0151  */
    /* JADX WARN: Code duplicated, block: B:80:0x0156  */
    /* JADX WARN: Code duplicated, block: B:82:0x015b  */
    public final AssetFileDescriptor openAssetFileDescriptor(Uri uri, String str, CancellationSignal cancellationSignal) throws Throwable {
        IContentProvider iContentProviderAcquireProvider;
        ICancellationSignal iCancellationSignalCreateCancellationSignal;
        AssetFileDescriptor assetFileDescriptorOpenAssetFile;
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(str, "mode");
        String scheme = uri.getScheme();
        if (SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            if (!FullBackup.ROOT_TREE_TOKEN.equals(str)) {
                throw new FileNotFoundException("Can't write resources: " + uri);
            }
            OpenResourceIdResult resourceId = getResourceId(uri);
            try {
                return resourceId.r.openRawResourceFd(resourceId.id);
            } catch (Resources.NotFoundException unused) {
                throw new FileNotFoundException("Resource does not exist: " + uri);
            }
        }
        if (SCHEME_FILE.equals(scheme)) {
            return new AssetFileDescriptor(ParcelFileDescriptor.open(new File(uri.getPath()), ParcelFileDescriptor.parseMode(str)), 0L, -1L);
        }
        if (FullBackup.ROOT_TREE_TOKEN.equals(str)) {
            return openTypedAssetFileDescriptor(uri, "*/*", null, cancellationSignal);
        }
        IContentProvider iContentProviderAcquireUnstableProvider = acquireUnstableProvider(uri);
        if (iContentProviderAcquireUnstableProvider == null) {
            throw new FileNotFoundException("No content provider: " + uri);
        }
        try {
            if (cancellationSignal != null) {
                try {
                    cancellationSignal.throwIfCanceled();
                    iCancellationSignalCreateCancellationSignal = iContentProviderAcquireUnstableProvider.createCancellationSignal();
                    cancellationSignal.setRemote(iCancellationSignalCreateCancellationSignal);
                } catch (RemoteException unused2) {
                    throw new FileNotFoundException("Failed opening content provider: " + uri);
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (Throwable th) {
                    th = th;
                    iContentProviderAcquireProvider = null;
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (iContentProviderAcquireProvider != null) {
                        releaseProvider(iContentProviderAcquireProvider);
                    }
                    if (iContentProviderAcquireUnstableProvider != null) {
                        releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    }
                    throw th;
                }
            } else {
                iCancellationSignalCreateCancellationSignal = null;
            }
            try {
                try {
                    assetFileDescriptorOpenAssetFile = iContentProviderAcquireUnstableProvider.openAssetFile(this.mPackageName, uri, str, iCancellationSignalCreateCancellationSignal);
                    if (assetFileDescriptorOpenAssetFile == null) {
                        if (cancellationSignal != null) {
                            cancellationSignal.setRemote(null);
                        }
                        if (iContentProviderAcquireUnstableProvider != null) {
                            releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                        }
                        return null;
                    }
                    iContentProviderAcquireProvider = null;
                    if (iContentProviderAcquireProvider == null) {
                        iContentProviderAcquireProvider = acquireProvider(uri);
                    }
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    try {
                        try {
                            AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(new ParcelFileDescriptorInner(assetFileDescriptorOpenAssetFile.getParcelFileDescriptor(), iContentProviderAcquireProvider), assetFileDescriptorOpenAssetFile.getStartOffset(), assetFileDescriptorOpenAssetFile.getDeclaredLength());
                            if (cancellationSignal != null) {
                                cancellationSignal.setRemote(null);
                            }
                            return assetFileDescriptor;
                        } catch (RemoteException unused3) {
                            throw new FileNotFoundException("Failed opening content provider: " + uri);
                        } catch (FileNotFoundException e2) {
                            throw e2;
                        } catch (Throwable th2) {
                            th = th2;
                            iContentProviderAcquireUnstableProvider = null;
                            iContentProviderAcquireProvider = null;
                            if (cancellationSignal != null) {
                                cancellationSignal.setRemote(null);
                            }
                            if (iContentProviderAcquireProvider != null) {
                                releaseProvider(iContentProviderAcquireProvider);
                            }
                            if (iContentProviderAcquireUnstableProvider != null) {
                                releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                            }
                            throw th;
                        }
                    } catch (RemoteException unused4) {
                    } catch (FileNotFoundException e3) {
                        throw e3;
                    } catch (Throwable th3) {
                        th = th3;
                        iContentProviderAcquireUnstableProvider = null;
                    }
                } catch (DeadObjectException unused5) {
                    unstableProviderDied(iContentProviderAcquireUnstableProvider);
                    iContentProviderAcquireProvider = acquireProvider(uri);
                    if (iContentProviderAcquireProvider == null) {
                        throw new FileNotFoundException("No content provider: " + uri);
                    }
                    assetFileDescriptorOpenAssetFile = iContentProviderAcquireProvider.openAssetFile(this.mPackageName, uri, str, iCancellationSignalCreateCancellationSignal);
                    if (assetFileDescriptorOpenAssetFile == null) {
                        if (cancellationSignal != null) {
                            cancellationSignal.setRemote(null);
                        }
                        if (iContentProviderAcquireProvider != null) {
                            releaseProvider(iContentProviderAcquireProvider);
                        }
                        if (iContentProviderAcquireUnstableProvider != null) {
                            releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                        }
                        return null;
                    }
                }
            } catch (RemoteException unused6) {
                throw new FileNotFoundException("Failed opening content provider: " + uri);
            } catch (FileNotFoundException e4) {
                throw e4;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri uri, String str, Bundle bundle) throws FileNotFoundException {
        return openTypedAssetFileDescriptor(uri, str, bundle, null);
    }

    /* JADX WARN: Code duplicated, block: B:60:0x00db  */
    /* JADX WARN: Code duplicated, block: B:62:0x00e0  */
    /* JADX WARN: Code duplicated, block: B:64:0x00e5  */
    public final AssetFileDescriptor openTypedAssetFileDescriptor(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws Throwable {
        IContentProvider iContentProviderAcquireProvider;
        ICancellationSignal iCancellationSignal;
        AssetFileDescriptor assetFileDescriptorOpenTypedAssetFile;
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(str, "mimeType");
        IContentProvider iContentProviderAcquireUnstableProvider = acquireUnstableProvider(uri);
        if (iContentProviderAcquireUnstableProvider == null) {
            throw new FileNotFoundException("No content provider: " + uri);
        }
        try {
            if (cancellationSignal != null) {
                try {
                    cancellationSignal.throwIfCanceled();
                    ICancellationSignal iCancellationSignalCreateCancellationSignal = iContentProviderAcquireUnstableProvider.createCancellationSignal();
                    cancellationSignal.setRemote(iCancellationSignalCreateCancellationSignal);
                    iCancellationSignal = iCancellationSignalCreateCancellationSignal;
                } catch (RemoteException unused) {
                    throw new FileNotFoundException("Failed opening content provider: " + uri);
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (Throwable th) {
                    th = th;
                    iContentProviderAcquireProvider = null;
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (iContentProviderAcquireProvider != null) {
                        releaseProvider(iContentProviderAcquireProvider);
                    }
                    if (iContentProviderAcquireUnstableProvider != null) {
                        releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    }
                    throw th;
                }
            } else {
                iCancellationSignal = null;
            }
            try {
                try {
                    assetFileDescriptorOpenTypedAssetFile = iContentProviderAcquireUnstableProvider.openTypedAssetFile(this.mPackageName, uri, str, bundle, iCancellationSignal);
                    if (assetFileDescriptorOpenTypedAssetFile == null) {
                        if (cancellationSignal != null) {
                            cancellationSignal.setRemote(null);
                        }
                        if (iContentProviderAcquireUnstableProvider != null) {
                            releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                        }
                        return null;
                    }
                    iContentProviderAcquireProvider = null;
                    if (iContentProviderAcquireProvider == null) {
                        iContentProviderAcquireProvider = acquireProvider(uri);
                    }
                    releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                    try {
                        try {
                            AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(new ParcelFileDescriptorInner(assetFileDescriptorOpenTypedAssetFile.getParcelFileDescriptor(), iContentProviderAcquireProvider), assetFileDescriptorOpenTypedAssetFile.getStartOffset(), assetFileDescriptorOpenTypedAssetFile.getDeclaredLength());
                            if (cancellationSignal != null) {
                                cancellationSignal.setRemote(null);
                            }
                            return assetFileDescriptor;
                        } catch (RemoteException unused2) {
                            throw new FileNotFoundException("Failed opening content provider: " + uri);
                        } catch (FileNotFoundException e2) {
                            throw e2;
                        } catch (Throwable th2) {
                            th = th2;
                            iContentProviderAcquireUnstableProvider = null;
                            iContentProviderAcquireProvider = null;
                            if (cancellationSignal != null) {
                                cancellationSignal.setRemote(null);
                            }
                            if (iContentProviderAcquireProvider != null) {
                                releaseProvider(iContentProviderAcquireProvider);
                            }
                            if (iContentProviderAcquireUnstableProvider != null) {
                                releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                            }
                            throw th;
                        }
                    } catch (RemoteException unused3) {
                    } catch (FileNotFoundException e3) {
                        throw e3;
                    } catch (Throwable th3) {
                        th = th3;
                        iContentProviderAcquireUnstableProvider = null;
                    }
                } catch (DeadObjectException unused4) {
                    unstableProviderDied(iContentProviderAcquireUnstableProvider);
                    iContentProviderAcquireProvider = acquireProvider(uri);
                    if (iContentProviderAcquireProvider == null) {
                        throw new FileNotFoundException("No content provider: " + uri);
                    }
                    assetFileDescriptorOpenTypedAssetFile = iContentProviderAcquireProvider.openTypedAssetFile(this.mPackageName, uri, str, bundle, iCancellationSignal);
                    if (assetFileDescriptorOpenTypedAssetFile == null) {
                        if (cancellationSignal != null) {
                            cancellationSignal.setRemote(null);
                        }
                        if (iContentProviderAcquireProvider != null) {
                            releaseProvider(iContentProviderAcquireProvider);
                        }
                        if (iContentProviderAcquireUnstableProvider != null) {
                            releaseUnstableProvider(iContentProviderAcquireUnstableProvider);
                        }
                        return null;
                    }
                }
            } catch (RemoteException unused5) {
                throw new FileNotFoundException("Failed opening content provider: " + uri);
            } catch (FileNotFoundException e4) {
                throw e4;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public class OpenResourceIdResult {
        public int id;
        public Resources r;

        public OpenResourceIdResult() {
        }
    }

    public OpenResourceIdResult getResourceId(Uri uri) throws FileNotFoundException {
        int identifier;
        String authority = uri.getAuthority();
        if (TextUtils.isEmpty(authority)) {
            throw new FileNotFoundException("No authority: " + uri);
        }
        try {
            Resources resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(authority);
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments == null) {
                throw new FileNotFoundException("No path: " + uri);
            }
            int size = pathSegments.size();
            if (size == 1) {
                try {
                    identifier = Integer.parseInt(pathSegments.get(0));
                } catch (NumberFormatException unused) {
                    throw new FileNotFoundException("Single path segment is not a resource ID: " + uri);
                }
            } else if (size == 2) {
                identifier = resourcesForApplication.getIdentifier(pathSegments.get(1), pathSegments.get(0), authority);
            } else {
                throw new FileNotFoundException("More than two path segments: " + uri);
            }
            if (identifier == 0) {
                throw new FileNotFoundException("No resource found for: " + uri);
            }
            OpenResourceIdResult openResourceIdResult = new OpenResourceIdResult();
            openResourceIdResult.r = resourcesForApplication;
            openResourceIdResult.id = identifier;
            return openResourceIdResult;
        } catch (PackageManager.NameNotFoundException unused2) {
            throw new FileNotFoundException("No package found for authority: " + uri);
        }
    }

    public final Uri insert(Uri uri, ContentValues contentValues) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            Uri uriInsert = iContentProviderAcquireProvider.insert(this.mPackageName, uri, contentValues);
            maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - jUptimeMillis, uri, "insert", null);
            return uriInsert;
        } catch (RemoteException unused) {
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public ContentProviderResult[] applyBatch(String str, ArrayList<ContentProviderOperation> arrayList) throws RemoteException, OperationApplicationException {
        Preconditions.checkNotNull(str, ContactsContract.Directory.DIRECTORY_AUTHORITY);
        Preconditions.checkNotNull(arrayList, "operations");
        ContentProviderClient contentProviderClientAcquireContentProviderClient = acquireContentProviderClient(str);
        if (contentProviderClientAcquireContentProviderClient == null) {
            throw new IllegalArgumentException("Unknown authority " + str);
        }
        try {
            return contentProviderClientAcquireContentProviderClient.applyBatch(arrayList);
        } finally {
            contentProviderClientAcquireContentProviderClient.release();
        }
    }

    public final int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        Preconditions.checkNotNull(uri, "url");
        Preconditions.checkNotNull(contentValuesArr, "values");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            int iBulkInsert = iContentProviderAcquireProvider.bulkInsert(this.mPackageName, uri, contentValuesArr);
            maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - jUptimeMillis, uri, "bulkinsert", null);
            return iBulkInsert;
        } catch (RemoteException unused) {
            return 0;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final int delete(Uri uri, String str, String[] strArr) {
        Preconditions.checkNotNull(uri, "url");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            int iDelete = iContentProviderAcquireProvider.delete(this.mPackageName, uri, str, strArr);
            maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - jUptimeMillis, uri, "delete", str);
            return iDelete;
        } catch (RemoteException unused) {
            return -1;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Preconditions.checkNotNull(uri, "uri");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            long jUptimeMillis = SystemClock.uptimeMillis();
            int iUpdate = iContentProviderAcquireProvider.update(this.mPackageName, uri, contentValues, str, strArr);
            maybeLogUpdateToEventLog(SystemClock.uptimeMillis() - jUptimeMillis, uri, "update", str);
            return iUpdate;
        } catch (RemoteException unused) {
            return -1;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final Bundle call(Uri uri, String str, String str2, Bundle bundle) {
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(str, CalendarContract.RemindersColumns.METHOD);
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Bundle bundleCall = iContentProviderAcquireProvider.call(this.mPackageName, str, str2, bundle);
            Bundle.setDefusable(bundleCall, true);
            return bundleCall;
        } catch (RemoteException unused) {
            return null;
        } finally {
            releaseProvider(iContentProviderAcquireProvider);
        }
    }

    public final IContentProvider acquireProvider(Uri uri) {
        String authority;
        if ("content".equals(uri.getScheme()) && (authority = uri.getAuthority()) != null) {
            return acquireProvider(this.mContext, authority);
        }
        return null;
    }

    public final IContentProvider acquireExistingProvider(Uri uri) {
        String authority;
        if ("content".equals(uri.getScheme()) && (authority = uri.getAuthority()) != null) {
            return acquireExistingProvider(this.mContext, authority);
        }
        return null;
    }

    public final IContentProvider acquireProvider(String str) {
        if (str == null) {
            return null;
        }
        return acquireProvider(this.mContext, str);
    }

    public final IContentProvider acquireUnstableProvider(Uri uri) {
        if ("content".equals(uri.getScheme()) && uri.getAuthority() != null) {
            return acquireUnstableProvider(this.mContext, uri.getAuthority());
        }
        return null;
    }

    public final IContentProvider acquireUnstableProvider(String str) {
        if (str == null) {
            return null;
        }
        return acquireUnstableProvider(this.mContext, str);
    }

    public final ContentProviderClient acquireContentProviderClient(Uri uri) {
        Preconditions.checkNotNull(uri, "uri");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(uri);
        if (iContentProviderAcquireProvider != null) {
            return new ContentProviderClient(this, iContentProviderAcquireProvider, true);
        }
        return null;
    }

    public final ContentProviderClient acquireContentProviderClient(String str) {
        Preconditions.checkNotNull(str, "name");
        IContentProvider iContentProviderAcquireProvider = acquireProvider(str);
        if (iContentProviderAcquireProvider != null) {
            return new ContentProviderClient(this, iContentProviderAcquireProvider, true);
        }
        return null;
    }

    public final ContentProviderClient acquireUnstableContentProviderClient(Uri uri) {
        Preconditions.checkNotNull(uri, "uri");
        IContentProvider iContentProviderAcquireUnstableProvider = acquireUnstableProvider(uri);
        if (iContentProviderAcquireUnstableProvider != null) {
            return new ContentProviderClient(this, iContentProviderAcquireUnstableProvider, false);
        }
        return null;
    }

    public final ContentProviderClient acquireUnstableContentProviderClient(String str) {
        Preconditions.checkNotNull(str, "name");
        IContentProvider iContentProviderAcquireUnstableProvider = acquireUnstableProvider(str);
        if (iContentProviderAcquireUnstableProvider != null) {
            return new ContentProviderClient(this, iContentProviderAcquireUnstableProvider, false);
        }
        return null;
    }

    public final void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver) {
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(contentObserver, "observer");
        registerContentObserver(ContentProvider.getUriWithoutUserId(uri), z, contentObserver, ContentProvider.getUserIdFromUri(uri, this.mContext.getUserId()));
    }

    public final void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver, int i) {
        try {
            getContentService().registerContentObserver(uri, z, contentObserver.getContentObserver(), i, this.mTargetSdkVersion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final void unregisterContentObserver(ContentObserver contentObserver) {
        Preconditions.checkNotNull(contentObserver, "observer");
        try {
            IContentObserver iContentObserverReleaseContentObserver = contentObserver.releaseContentObserver();
            if (iContentObserverReleaseContentObserver != null) {
                getContentService().unregisterContentObserver(iContentObserverReleaseContentObserver);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyChange(Uri uri, ContentObserver contentObserver) {
        notifyChange(uri, contentObserver, true);
    }

    public void notifyChange(Uri uri, ContentObserver contentObserver, boolean z) {
        Preconditions.checkNotNull(uri, "uri");
        notifyChange(ContentProvider.getUriWithoutUserId(uri), contentObserver, z, ContentProvider.getUserIdFromUri(uri, this.mContext.getUserId()));
    }

    public void notifyChange(Uri uri, ContentObserver contentObserver, int i) {
        Preconditions.checkNotNull(uri, "uri");
        notifyChange(ContentProvider.getUriWithoutUserId(uri), contentObserver, i, ContentProvider.getUserIdFromUri(uri, this.mContext.getUserId()));
    }

    public void notifyChange(Uri uri, ContentObserver contentObserver, boolean z, int i) {
        try {
            getContentService().notifyChange(uri, contentObserver == null ? null : contentObserver.getContentObserver(), contentObserver != null && contentObserver.deliverSelfNotifications(), z ? 1 : 0, i, this.mTargetSdkVersion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyChange(Uri uri, ContentObserver contentObserver, int i, int i2) {
        try {
            getContentService().notifyChange(uri, contentObserver == null ? null : contentObserver.getContentObserver(), contentObserver != null && contentObserver.deliverSelfNotifications(), i, i2, this.mTargetSdkVersion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void takePersistableUriPermission(Uri uri, int i) {
        Preconditions.checkNotNull(uri, "uri");
        try {
            ActivityManager.getService().takePersistableUriPermission(ContentProvider.getUriWithoutUserId(uri), i, null, resolveUserId(uri));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void takePersistableUriPermission(String str, Uri uri, int i) {
        Preconditions.checkNotNull(str, "toPackage");
        Preconditions.checkNotNull(uri, "uri");
        try {
            ActivityManager.getService().takePersistableUriPermission(ContentProvider.getUriWithoutUserId(uri), i, str, resolveUserId(uri));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releasePersistableUriPermission(Uri uri, int i) {
        Preconditions.checkNotNull(uri, "uri");
        try {
            ActivityManager.getService().releasePersistableUriPermission(ContentProvider.getUriWithoutUserId(uri), i, null, resolveUserId(uri));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<UriPermission> getPersistedUriPermissions() {
        try {
            return ActivityManager.getService().getPersistedUriPermissions(this.mPackageName, true).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<UriPermission> getOutgoingPersistedUriPermissions() {
        try {
            return ActivityManager.getService().getPersistedUriPermissions(this.mPackageName, false).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void startSync(Uri uri, Bundle bundle) {
        Account account;
        if (bundle != null) {
            String string = bundle.getString("account");
            account = !TextUtils.isEmpty(string) ? new Account(string, "com.google") : null;
            bundle.remove("account");
        } else {
            account = null;
        }
        requestSync(account, uri != null ? uri.getAuthority() : null, bundle);
    }

    public static void requestSync(Account account, String str, Bundle bundle) {
        requestSyncAsUser(account, str, UserHandle.myUserId(), bundle);
    }

    public static void requestSyncAsUser(Account account, String str, int i, Bundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Must specify extras.");
        }
        try {
            getContentService().syncAsUser(new SyncRequest.Builder().setSyncAdapter(account, str).setExtras(bundle).syncOnce().build(), i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void requestSync(SyncRequest syncRequest) {
        try {
            getContentService().sync(syncRequest);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void validateSyncExtrasBundle(Bundle bundle) {
        try {
            Iterator<String> it = bundle.keySet().iterator();
            while (it.hasNext()) {
                Object obj = bundle.get(it.next());
                if (obj != null && !(obj instanceof Long) && !(obj instanceof Integer) && !(obj instanceof Boolean) && !(obj instanceof Float) && !(obj instanceof Double) && !(obj instanceof String) && !(obj instanceof Account)) {
                    throw new IllegalArgumentException("unexpected value type: " + obj.getClass().getName());
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e2) {
            throw new IllegalArgumentException("error unparceling Bundle", e2);
        }
    }

    @Deprecated
    public void cancelSync(Uri uri) {
        cancelSync(null, uri != null ? uri.getAuthority() : null);
    }

    public static void cancelSync(Account account, String str) {
        try {
            getContentService().cancelSync(account, str, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void cancelSyncAsUser(Account account, String str, int i) {
        try {
            getContentService().cancelSyncAsUser(account, str, null, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static SyncAdapterType[] getSyncAdapterTypes() {
        try {
            return getContentService().getSyncAdapterTypes();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static SyncAdapterType[] getSyncAdapterTypesAsUser(int i) {
        try {
            return getContentService().getSyncAdapterTypesAsUser(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static String[] getSyncAdapterPackagesForAuthorityAsUser(String str, int i) {
        try {
            return getContentService().getSyncAdapterPackagesForAuthorityAsUser(str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean getSyncAutomatically(Account account, String str) {
        try {
            return getContentService().getSyncAutomatically(account, str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean getSyncAutomaticallyAsUser(Account account, String str, int i) {
        try {
            return getContentService().getSyncAutomaticallyAsUser(account, str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void setSyncAutomatically(Account account, String str, boolean z) {
        setSyncAutomaticallyAsUser(account, str, z, UserHandle.myUserId());
    }

    public static void setSyncAutomaticallyAsUser(Account account, String str, boolean z, int i) {
        try {
            getContentService().setSyncAutomaticallyAsUser(account, str, z, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void addPeriodicSync(Account account, String str, Bundle bundle, long j) {
        validateSyncExtrasBundle(bundle);
        if (invalidPeriodicExtras(bundle)) {
            throw new IllegalArgumentException("illegal extras were set");
        }
        try {
            getContentService().addPeriodicSync(account, str, bundle, j);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean invalidPeriodicExtras(Bundle bundle) {
        return bundle.getBoolean("force", false) || bundle.getBoolean(SYNC_EXTRAS_DO_NOT_RETRY, false) || bundle.getBoolean(SYNC_EXTRAS_IGNORE_BACKOFF, false) || bundle.getBoolean(SYNC_EXTRAS_IGNORE_SETTINGS, false) || bundle.getBoolean(SYNC_EXTRAS_INITIALIZE, false) || bundle.getBoolean("force", false) || bundle.getBoolean(SYNC_EXTRAS_EXPEDITED, false);
    }

    public static void removePeriodicSync(Account account, String str, Bundle bundle) {
        validateSyncExtrasBundle(bundle);
        try {
            getContentService().removePeriodicSync(account, str, bundle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void cancelSync(SyncRequest syncRequest) {
        if (syncRequest == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        try {
            getContentService().cancelRequest(syncRequest);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static List<PeriodicSync> getPeriodicSyncs(Account account, String str) {
        try {
            return getContentService().getPeriodicSyncs(account, str, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static int getIsSyncable(Account account, String str) {
        try {
            return getContentService().getIsSyncable(account, str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static int getIsSyncableAsUser(Account account, String str, int i) {
        try {
            return getContentService().getIsSyncableAsUser(account, str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void setIsSyncable(Account account, String str, int i) {
        try {
            getContentService().setIsSyncable(account, str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean getMasterSyncAutomatically() {
        try {
            return getContentService().getMasterSyncAutomatically();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean getMasterSyncAutomaticallyAsUser(int i) {
        try {
            return getContentService().getMasterSyncAutomaticallyAsUser(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void setMasterSyncAutomatically(boolean z) {
        setMasterSyncAutomaticallyAsUser(z, UserHandle.myUserId());
    }

    public static void setMasterSyncAutomaticallyAsUser(boolean z, int i) {
        try {
            getContentService().setMasterSyncAutomaticallyAsUser(z, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean isSyncActive(Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        if (str == null) {
            throw new IllegalArgumentException("authority must not be null");
        }
        try {
            return getContentService().isSyncActive(account, str, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public static SyncInfo getCurrentSync() {
        try {
            List<SyncInfo> currentSyncs = getContentService().getCurrentSyncs();
            if (currentSyncs.isEmpty()) {
                return null;
            }
            return currentSyncs.get(0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static List<SyncInfo> getCurrentSyncs() {
        try {
            return getContentService().getCurrentSyncs();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static List<SyncInfo> getCurrentSyncsAsUser(int i) {
        try {
            return getContentService().getCurrentSyncsAsUser(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static SyncStatusInfo getSyncStatus(Account account, String str) {
        try {
            return getContentService().getSyncStatus(account, str, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static SyncStatusInfo getSyncStatusAsUser(Account account, String str, int i) {
        try {
            return getContentService().getSyncStatusAsUser(account, str, null, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean isSyncPending(Account account, String str) {
        return isSyncPendingAsUser(account, str, UserHandle.myUserId());
    }

    public static boolean isSyncPendingAsUser(Account account, String str, int i) {
        try {
            return getContentService().isSyncPendingAsUser(account, str, null, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static Object addStatusChangeListener(int i, final SyncStatusObserver syncStatusObserver) {
        if (syncStatusObserver == null) {
            throw new IllegalArgumentException("you passed in a null callback");
        }
        try {
            ISyncStatusObserver.Stub stub = new ISyncStatusObserver.Stub() { // from class: android.content.ContentResolver.1
                @Override // android.content.ISyncStatusObserver
                public void onStatusChanged(int i2) throws RemoteException {
                    syncStatusObserver.onStatusChanged(i2);
                }
            };
            getContentService().addStatusChangeListener(i, stub);
            return stub;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void removeStatusChangeListener(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("you passed in a null handle");
        }
        try {
            getContentService().removeStatusChangeListener((ISyncStatusObserver.Stub) obj);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void putCache(Uri uri, Bundle bundle) {
        try {
            getContentService().putCache(this.mContext.getPackageName(), uri, bundle, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Bundle getCache(Uri uri) {
        try {
            Bundle cache = getContentService().getCache(this.mContext.getPackageName(), uri, this.mContext.getUserId());
            if (cache != null) {
                cache.setClassLoader(this.mContext.getClassLoader());
            }
            return cache;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getTargetSdkVersion() {
        return this.mTargetSdkVersion;
    }

    private int samplePercentForDuration(long j) {
        if (j >= 500) {
            return 100;
        }
        return ((int) ((j * 100) / 500)) + 1;
    }

    private final class CursorWrapperInner extends CrossProcessCursorWrapper {
        private final CloseGuard mCloseGuard;
        private final IContentProvider mContentProvider;
        private final AtomicBoolean mProviderReleased;

        CursorWrapperInner(Cursor cursor, IContentProvider iContentProvider) {
            super(cursor);
            this.mProviderReleased = new AtomicBoolean();
            CloseGuard closeGuard = CloseGuard.get();
            this.mCloseGuard = closeGuard;
            this.mContentProvider = iContentProvider;
            closeGuard.open("close");
        }

        @Override // android.database.CursorWrapper, android.database.Cursor, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.mCloseGuard.close();
            super.close();
            if (this.mProviderReleased.compareAndSet(false, true)) {
                ContentResolver.this.releaseProvider(this.mContentProvider);
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.mCloseGuard != null) {
                    this.mCloseGuard.warnIfOpen();
                }
                close();
            } finally {
                super.finalize();
            }
        }
    }

    private final class ParcelFileDescriptorInner extends ParcelFileDescriptor {
        private final IContentProvider mContentProvider;
        private final AtomicBoolean mProviderReleased;

        ParcelFileDescriptorInner(ParcelFileDescriptor parcelFileDescriptor, IContentProvider iContentProvider) {
            super(parcelFileDescriptor);
            this.mProviderReleased = new AtomicBoolean();
            this.mContentProvider = iContentProvider;
        }

        @Override // android.os.ParcelFileDescriptor
        public void releaseResources() {
            if (this.mProviderReleased.compareAndSet(false, true)) {
                ContentResolver.this.releaseProvider(this.mContentProvider);
            }
        }
    }

    public static IContentService getContentService() {
        if (sContentService != null) {
            return sContentService;
        }
        sContentService = IContentService.Stub.asInterface(ServiceManager.getService("content"));
        return sContentService;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int resolveUserId(Uri uri) {
        return ContentProvider.getUserIdFromUri(uri, this.mContext.getUserId());
    }

    public int getUserId() {
        return this.mContext.getUserId();
    }

    public Drawable getTypeDrawable(String str) {
        return MimeIconUtils.loadMimeIcon(this.mContext, str);
    }

    public static Bundle createSqlQueryBundle(String str, String[] strArr, String str2) {
        if (str == null && strArr == null && str2 == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        if (str != null) {
            bundle.putString(QUERY_ARG_SQL_SELECTION, str);
        }
        if (strArr != null) {
            bundle.putStringArray(QUERY_ARG_SQL_SELECTION_ARGS, strArr);
        }
        if (str2 != null) {
            bundle.putString(QUERY_ARG_SQL_SORT_ORDER, str2);
        }
        return bundle;
    }

    public static String createSqlSortClause(Bundle bundle) {
        String[] stringArray = bundle.getStringArray(QUERY_ARG_SORT_COLUMNS);
        if (stringArray == null || stringArray.length == 0) {
            throw new IllegalArgumentException("Can't create sort clause without columns.");
        }
        String strJoin = TextUtils.join(", ", stringArray);
        int i = bundle.getInt(QUERY_ARG_SORT_COLLATION, 3);
        if (i == 0 || i == 1) {
            strJoin = strJoin + " COLLATE NOCASE";
        }
        int i2 = bundle.getInt(QUERY_ARG_SORT_DIRECTION, Integer.MIN_VALUE);
        if (i2 == Integer.MIN_VALUE) {
            return strJoin;
        }
        if (i2 == 0) {
            return strJoin + " ASC";
        }
        if (i2 == 1) {
            return strJoin + " DESC";
        }
        throw new IllegalArgumentException("Unsupported sort direction value. See ContentResolver documentation for details.");
    }
}
