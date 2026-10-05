package android.content;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.backup.FullBackup;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentProvider implements ComponentCallbacks2 {
    private static final String TAG = "ContentProvider";
    private String[] mAuthorities;
    private String mAuthority;
    private final ThreadLocal<String> mCallingPackage;
    private Context mContext;
    private boolean mExported;
    private int mMyUid;
    private boolean mNoPerms;
    private PathPermission[] mPathPermissions;
    private String mReadPermission;
    private boolean mSingleUser;
    private Transport mTransport;
    private String mWritePermission;

    public interface PipeDataWriter<T> {
        void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, T t);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        return null;
    }

    public Uri canonicalize(Uri uri) {
        return null;
    }

    public abstract int delete(Uri uri, String str, String[] strArr);

    public String[] getStreamTypes(Uri uri, String str) {
        return null;
    }

    public abstract String getType(Uri uri);

    public abstract Uri insert(Uri uri, ContentValues contentValues);

    protected boolean isTemporary() {
        return false;
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
    }

    public abstract boolean onCreate();

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
    }

    public abstract Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2);

    public boolean refresh(Uri uri, Bundle bundle, CancellationSignal cancellationSignal) {
        return false;
    }

    public Uri uncanonicalize(Uri uri) {
        return uri;
    }

    public abstract int update(Uri uri, ContentValues contentValues, String str, String[] strArr);

    public ContentProvider() {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
    }

    public ContentProvider(Context context, String str, String str2, PathPermission[] pathPermissionArr) {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
        this.mContext = context;
        this.mReadPermission = str;
        this.mWritePermission = str2;
        this.mPathPermissions = pathPermissionArr;
    }

    public static ContentProvider coerceToLocalContentProvider(IContentProvider iContentProvider) {
        if (iContentProvider instanceof Transport) {
            return ((Transport) iContentProvider).getContentProvider();
        }
        return null;
    }

    class Transport extends ContentProviderNative {
        AppOpsManager mAppOpsManager = null;
        int mReadOp = -1;
        int mWriteOp = -1;

        Transport() {
        }

        ContentProvider getContentProvider() {
            return ContentProvider.this;
        }

        @Override // android.content.ContentProviderNative
        public String getProviderName() {
            return getContentProvider().getClass().getName();
        }

        @Override // android.content.IContentProvider
        public Cursor query(String str, Uri uri, String[] strArr, Bundle bundle, ICancellationSignal iCancellationSignal) {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            if (enforceReadPermission(str, uriMaybeGetUriWithoutUserId, null) == 0) {
                String callingPackage = ContentProvider.this.setCallingPackage(str);
                try {
                    return ContentProvider.this.query(uriMaybeGetUriWithoutUserId, strArr, bundle, CancellationSignal.fromTransport(iCancellationSignal));
                } finally {
                    ContentProvider.this.setCallingPackage(callingPackage);
                }
            }
            if (strArr != null) {
                return new MatrixCursor(strArr, 0);
            }
            Cursor cursorQuery = ContentProvider.this.query(uriMaybeGetUriWithoutUserId, strArr, bundle, CancellationSignal.fromTransport(iCancellationSignal));
            if (cursorQuery == null) {
                return null;
            }
            return new MatrixCursor(cursorQuery.getColumnNames(), 0);
        }

        @Override // android.content.IContentProvider
        public String getType(Uri uri) {
            ContentProvider.this.validateIncomingUri(uri);
            return ContentProvider.this.getType(ContentProvider.this.maybeGetUriWithoutUserId(uri));
        }

        @Override // android.content.IContentProvider
        public Uri insert(String str, Uri uri, ContentValues contentValues) {
            ContentProvider.this.validateIncomingUri(uri);
            int userIdFromUri = ContentProvider.getUserIdFromUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            if (enforceWritePermission(str, uriMaybeGetUriWithoutUserId, null) == 0) {
                String callingPackage = ContentProvider.this.setCallingPackage(str);
                try {
                    return ContentProvider.maybeAddUserId(ContentProvider.this.insert(uriMaybeGetUriWithoutUserId, contentValues), userIdFromUri);
                } finally {
                    ContentProvider.this.setCallingPackage(callingPackage);
                }
            }
            return ContentProvider.this.rejectInsert(uriMaybeGetUriWithoutUserId, contentValues);
        }

        @Override // android.content.IContentProvider
        public int bulkInsert(String str, Uri uri, ContentValues[] contentValuesArr) {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            if (enforceWritePermission(str, uriMaybeGetUriWithoutUserId, null) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.bulkInsert(uriMaybeGetUriWithoutUserId, contentValuesArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ContentProviderResult[] applyBatch(String str, ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
            int size = arrayList.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                ContentProviderOperation contentProviderOperation = arrayList.get(i);
                Uri uri = contentProviderOperation.getUri();
                ContentProvider.this.validateIncomingUri(uri);
                iArr[i] = ContentProvider.getUserIdFromUri(uri);
                if (iArr[i] != -2) {
                    ContentProviderOperation contentProviderOperation2 = new ContentProviderOperation(contentProviderOperation, true);
                    arrayList.set(i, contentProviderOperation2);
                    contentProviderOperation = contentProviderOperation2;
                }
                if (contentProviderOperation.isReadOperation() && enforceReadPermission(str, uri, null) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
                if (contentProviderOperation.isWriteOperation() && enforceWritePermission(str, uri, null) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                ContentProviderResult[] contentProviderResultArrApplyBatch = ContentProvider.this.applyBatch(arrayList);
                if (contentProviderResultArrApplyBatch != null) {
                    for (int i2 = 0; i2 < contentProviderResultArrApplyBatch.length; i2++) {
                        if (iArr[i2] != -2) {
                            contentProviderResultArrApplyBatch[i2] = new ContentProviderResult(contentProviderResultArrApplyBatch[i2], iArr[i2]);
                        }
                    }
                }
                return contentProviderResultArrApplyBatch;
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public int delete(String str, Uri uri, String str2, String[] strArr) {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            if (enforceWritePermission(str, uriMaybeGetUriWithoutUserId, null) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.delete(uriMaybeGetUriWithoutUserId, str2, strArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public int update(String str, Uri uri, ContentValues contentValues, String str2, String[] strArr) {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            if (enforceWritePermission(str, uriMaybeGetUriWithoutUserId, null) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.update(uriMaybeGetUriWithoutUserId, contentValues, str2, strArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ParcelFileDescriptor openFile(String str, Uri uri, String str2, ICancellationSignal iCancellationSignal, IBinder iBinder) throws FileNotFoundException {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            enforceFilePermission(str, uriMaybeGetUriWithoutUserId, str2, iBinder);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openFile(uriMaybeGetUriWithoutUserId, str2, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openAssetFile(String str, Uri uri, String str2, ICancellationSignal iCancellationSignal) throws FileNotFoundException {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            enforceFilePermission(str, uriMaybeGetUriWithoutUserId, str2, null);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openAssetFile(uriMaybeGetUriWithoutUserId, str2, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public Bundle call(String str, String str2, String str3, Bundle bundle) {
            Bundle.setDefusable(bundle, true);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.call(str2, str3, bundle);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public String[] getStreamTypes(Uri uri, String str) {
            ContentProvider.this.validateIncomingUri(uri);
            return ContentProvider.this.getStreamTypes(ContentProvider.this.maybeGetUriWithoutUserId(uri), str);
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openTypedAssetFile(String str, Uri uri, String str2, Bundle bundle, ICancellationSignal iCancellationSignal) throws FileNotFoundException {
            Bundle.setDefusable(bundle, true);
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriMaybeGetUriWithoutUserId = ContentProvider.this.maybeGetUriWithoutUserId(uri);
            enforceFilePermission(str, uriMaybeGetUriWithoutUserId, FullBackup.ROOT_TREE_TOKEN, null);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openTypedAssetFile(uriMaybeGetUriWithoutUserId, str2, bundle, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ICancellationSignal createCancellationSignal() {
            return CancellationSignal.createTransport();
        }

        @Override // android.content.IContentProvider
        public Uri canonicalize(String str, Uri uri) {
            ContentProvider.this.validateIncomingUri(uri);
            int userIdFromUri = ContentProvider.getUserIdFromUri(uri);
            Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
            if (enforceReadPermission(str, uriWithoutUserId, null) != 0) {
                return null;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.maybeAddUserId(ContentProvider.this.canonicalize(uriWithoutUserId), userIdFromUri);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public Uri uncanonicalize(String str, Uri uri) {
            ContentProvider.this.validateIncomingUri(uri);
            int userIdFromUri = ContentProvider.getUserIdFromUri(uri);
            Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
            if (enforceReadPermission(str, uriWithoutUserId, null) != 0) {
                return null;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.maybeAddUserId(ContentProvider.this.uncanonicalize(uriWithoutUserId), userIdFromUri);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public boolean refresh(String str, Uri uri, Bundle bundle, ICancellationSignal iCancellationSignal) throws RemoteException {
            ContentProvider.this.validateIncomingUri(uri);
            Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
            if (enforceReadPermission(str, uriWithoutUserId, null) != 0) {
                return false;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.refresh(uriWithoutUserId, bundle, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        private void enforceFilePermission(String str, Uri uri, String str2, IBinder iBinder) throws SecurityException, FileNotFoundException {
            if (str2 != null && str2.indexOf(119) != -1) {
                if (enforceWritePermission(str, uri, iBinder) != 0) {
                    throw new FileNotFoundException("App op not allowed");
                }
            } else if (enforceReadPermission(str, uri, iBinder) != 0) {
                throw new FileNotFoundException("App op not allowed");
            }
        }

        private int enforceReadPermission(String str, Uri uri, IBinder iBinder) throws SecurityException {
            int iEnforceReadPermissionInner = ContentProvider.this.enforceReadPermissionInner(uri, str, iBinder);
            if (iEnforceReadPermissionInner != 0) {
                return iEnforceReadPermissionInner;
            }
            int i = this.mReadOp;
            if (i != -1) {
                return this.mAppOpsManager.noteProxyOp(i, str);
            }
            return 0;
        }

        private int enforceWritePermission(String str, Uri uri, IBinder iBinder) throws SecurityException {
            int iEnforceWritePermissionInner = ContentProvider.this.enforceWritePermissionInner(uri, str, iBinder);
            if (iEnforceWritePermissionInner != 0) {
                return iEnforceWritePermissionInner;
            }
            int i = this.mWriteOp;
            if (i != -1) {
                return this.mAppOpsManager.noteProxyOp(i, str);
            }
            return 0;
        }
    }

    boolean checkUser(int i, int i2, Context context) {
        return UserHandle.getUserId(i2) == context.getUserId() || this.mSingleUser || context.checkPermission(Manifest.permission.INTERACT_ACROSS_USERS, i, i2) == 0;
    }

    private int checkPermissionAndAppOp(String str, String str2, IBinder iBinder) {
        if (getContext().checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid(), iBinder) != 0) {
            return 2;
        }
        int iPermissionToOpCode = AppOpsManager.permissionToOpCode(str);
        if (iPermissionToOpCode != -1) {
            return this.mTransport.mAppOpsManager.noteProxyOp(iPermissionToOpCode, str2);
        }
        return 0;
    }

    protected int enforceReadPermissionInner(Uri uri, String str, IBinder iBinder) throws SecurityException {
        String str2;
        int i;
        String str3;
        int iMax;
        Context context = getContext();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, this.mMyUid)) {
            return 0;
        }
        String str4 = null;
        if (this.mExported && checkUser(callingPid, callingUid, context)) {
            String readPermission = getReadPermission();
            if (readPermission != null) {
                int iCheckPermissionAndAppOp = checkPermissionAndAppOp(readPermission, str, iBinder);
                if (iCheckPermissionAndAppOp == 0) {
                    return 0;
                }
                iMax = Math.max(0, iCheckPermissionAndAppOp);
                str4 = readPermission;
            } else {
                iMax = 0;
            }
            boolean z = readPermission == null;
            PathPermission[] pathPermissions = getPathPermissions();
            if (pathPermissions != null) {
                String path = uri.getPath();
                for (PathPermission pathPermission : pathPermissions) {
                    String readPermission2 = pathPermission.getReadPermission();
                    if (readPermission2 != null && pathPermission.match(path)) {
                        int iCheckPermissionAndAppOp2 = checkPermissionAndAppOp(readPermission2, str, iBinder);
                        if (iCheckPermissionAndAppOp2 == 0) {
                            return 0;
                        }
                        iMax = Math.max(iMax, iCheckPermissionAndAppOp2);
                        str4 = readPermission2;
                        z = false;
                    }
                }
            }
            if (z) {
                return 0;
            }
            str2 = str4;
            i = iMax;
        } else {
            str2 = null;
            i = 0;
        }
        if (context.checkUriPermission((!this.mSingleUser || UserHandle.isSameUser(this.mMyUid, callingUid)) ? uri : maybeAddUserId(uri, UserHandle.getUserId(callingUid)), callingPid, callingUid, 1, iBinder) == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        if (Manifest.permission.MANAGE_DOCUMENTS.equals(this.mReadPermission)) {
            str3 = " requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs";
        } else if (this.mExported) {
            str3 = " requires " + str2 + ", or grantUriPermission()";
        } else {
            str3 = " requires the provider be exported, or grantUriPermission()";
        }
        throw new SecurityException("Permission Denial: reading " + getClass().getName() + " uri " + uri + " from pid=" + callingPid + ", uid=" + callingUid + str3);
    }

    protected int enforceWritePermissionInner(Uri uri, String str, IBinder iBinder) throws SecurityException {
        String str2;
        int i;
        int iMax;
        Context context = getContext();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, this.mMyUid)) {
            return 0;
        }
        String str3 = null;
        if (this.mExported && checkUser(callingPid, callingUid, context)) {
            String writePermission = getWritePermission();
            if (writePermission != null) {
                int iCheckPermissionAndAppOp = checkPermissionAndAppOp(writePermission, str, iBinder);
                if (iCheckPermissionAndAppOp == 0) {
                    return 0;
                }
                iMax = Math.max(0, iCheckPermissionAndAppOp);
                str3 = writePermission;
            } else {
                iMax = 0;
            }
            boolean z = writePermission == null;
            PathPermission[] pathPermissions = getPathPermissions();
            if (pathPermissions != null) {
                String path = uri.getPath();
                for (PathPermission pathPermission : pathPermissions) {
                    String writePermission2 = pathPermission.getWritePermission();
                    if (writePermission2 != null && pathPermission.match(path)) {
                        int iCheckPermissionAndAppOp2 = checkPermissionAndAppOp(writePermission2, str, iBinder);
                        if (iCheckPermissionAndAppOp2 == 0) {
                            return 0;
                        }
                        iMax = Math.max(iMax, iCheckPermissionAndAppOp2);
                        z = false;
                        str3 = writePermission2;
                    }
                }
            }
            if (z) {
                return 0;
            }
            str2 = str3;
            i = iMax;
        } else {
            str2 = null;
            i = 0;
        }
        if (context.checkUriPermission(uri, callingPid, callingUid, 2, iBinder) == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        throw new SecurityException("Permission Denial: writing " + getClass().getName() + " uri " + uri + " from pid=" + callingPid + ", uid=" + callingUid + (this.mExported ? " requires " + str2 + ", or grantUriPermission()" : " requires the provider be exported, or grantUriPermission()"));
    }

    public final Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String setCallingPackage(String str) {
        String str2 = this.mCallingPackage.get();
        this.mCallingPackage.set(str);
        return str2;
    }

    public final String getCallingPackage() {
        String str = this.mCallingPackage.get();
        if (str != null) {
            this.mTransport.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        }
        return str;
    }

    protected final void setAuthorities(String str) {
        if (str != null) {
            if (str.indexOf(59) == -1) {
                this.mAuthority = str;
                this.mAuthorities = null;
            } else {
                this.mAuthority = null;
                this.mAuthorities = str.split(";");
            }
        }
    }

    protected final boolean matchesOurAuthorities(String str) {
        String str2 = this.mAuthority;
        if (str2 != null) {
            return str2.equals(str);
        }
        String[] strArr = this.mAuthorities;
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                if (this.mAuthorities[i].equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final void setReadPermission(String str) {
        this.mReadPermission = str;
    }

    public final String getReadPermission() {
        return this.mReadPermission;
    }

    protected final void setWritePermission(String str) {
        this.mWritePermission = str;
    }

    public final String getWritePermission() {
        return this.mWritePermission;
    }

    protected final void setPathPermissions(PathPermission[] pathPermissionArr) {
        this.mPathPermissions = pathPermissionArr;
    }

    public final PathPermission[] getPathPermissions() {
        return this.mPathPermissions;
    }

    public final void setAppOps(int i, int i2) {
        if (this.mNoPerms) {
            return;
        }
        this.mTransport.mReadOp = i;
        this.mTransport.mWriteOp = i2;
    }

    public AppOpsManager getAppOpsManager() {
        return this.mTransport.mAppOpsManager;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return query(uri, strArr, str, strArr2, str2);
    }

    public Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        if (bundle == null) {
            bundle = Bundle.EMPTY;
        }
        String string = bundle.getString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER);
        if (string == null && bundle.containsKey(ContentResolver.QUERY_ARG_SORT_COLUMNS)) {
            string = ContentResolver.createSqlSortClause(bundle);
        }
        return query(uri, strArr, bundle.getString(ContentResolver.QUERY_ARG_SQL_SELECTION), bundle.getStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS), string, cancellationSignal);
    }

    public Uri rejectInsert(Uri uri, ContentValues contentValues) {
        return uri.buildUpon().appendPath("0").build();
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        int length = contentValuesArr.length;
        for (ContentValues contentValues : contentValuesArr) {
            insert(uri, contentValues);
        }
        return length;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        throw new FileNotFoundException("No files supported by provider at " + uri);
    }

    public ParcelFileDescriptor openFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openFile(uri, str);
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String str) throws FileNotFoundException {
        ParcelFileDescriptor parcelFileDescriptorOpenFile = openFile(uri, str);
        if (parcelFileDescriptorOpenFile != null) {
            return new AssetFileDescriptor(parcelFileDescriptorOpenFile, 0L, -1L);
        }
        return null;
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openAssetFile(uri, str);
    }

    protected final ParcelFileDescriptor openFileHelper(Uri uri, String str) throws FileNotFoundException {
        Cursor cursorQuery = query(uri, new String[]{"_data"}, null, null, null);
        int count = cursorQuery != null ? cursorQuery.getCount() : 0;
        if (count == 1) {
            cursorQuery.moveToFirst();
            int columnIndex = cursorQuery.getColumnIndex("_data");
            String string = columnIndex >= 0 ? cursorQuery.getString(columnIndex) : null;
            cursorQuery.close();
            if (string == null) {
                throw new FileNotFoundException("Column _data not found.");
            }
            return ParcelFileDescriptor.open(new File(string), ParcelFileDescriptor.parseMode(str));
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
        if (count == 0) {
            throw new FileNotFoundException("No entry for " + uri);
        }
        throw new FileNotFoundException("Multiple items at " + uri);
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle) throws FileNotFoundException {
        if ("*/*".equals(str)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        String type = getType(uri);
        if (type != null && ClipDescription.compareMimeTypes(type, str)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        throw new FileNotFoundException("Can't open " + uri + " as type " + str);
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openTypedAssetFile(uri, str, bundle);
    }

    public <T> ParcelFileDescriptor openPipeHelper(final Uri uri, final String str, final Bundle bundle, final T t, final PipeDataWriter<T> pipeDataWriter) throws FileNotFoundException {
        try {
            final ParcelFileDescriptor[] parcelFileDescriptorArrCreatePipe = ParcelFileDescriptor.createPipe();
            new AsyncTask<Object, Object, Object>() { // from class: android.content.ContentProvider.1
                @Override // android.os.AsyncTask
                protected Object doInBackground(Object... objArr) {
                    pipeDataWriter.writeDataToPipe(parcelFileDescriptorArrCreatePipe[1], uri, str, bundle, t);
                    try {
                        parcelFileDescriptorArrCreatePipe[1].close();
                        return null;
                    } catch (IOException e) {
                        Log.w(ContentProvider.TAG, "Failure closing pipe", e);
                        return null;
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[]) null);
            return parcelFileDescriptorArrCreatePipe[0];
        } catch (IOException unused) {
            throw new FileNotFoundException("failure making pipe");
        }
    }

    public IContentProvider getIContentProvider() {
        return this.mTransport;
    }

    public void attachInfoForTesting(Context context, ProviderInfo providerInfo) {
        attachInfo(context, providerInfo, true);
    }

    public void attachInfo(Context context, ProviderInfo providerInfo) {
        attachInfo(context, providerInfo, false);
    }

    private void attachInfo(Context context, ProviderInfo providerInfo, boolean z) {
        this.mNoPerms = z;
        if (this.mContext == null) {
            this.mContext = context;
            if (context != null) {
                this.mTransport.mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            }
            this.mMyUid = Process.myUid();
            if (providerInfo != null) {
                setReadPermission(providerInfo.readPermission);
                setWritePermission(providerInfo.writePermission);
                setPathPermissions(providerInfo.pathPermissions);
                this.mExported = providerInfo.exported;
                this.mSingleUser = (providerInfo.flags & 1073741824) != 0;
                setAuthorities(providerInfo.authority);
            }
            onCreate();
        }
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
        int size = arrayList.size();
        ContentProviderResult[] contentProviderResultArr = new ContentProviderResult[size];
        for (int i = 0; i < size; i++) {
            contentProviderResultArr[i] = arrayList.get(i).apply(this, contentProviderResultArr, i);
        }
        return contentProviderResultArr;
    }

    public void shutdown() {
        Log.w(TAG, "implement ContentProvider shutdown() to make sure all database connections are gracefully shutdown");
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("nothing to dump");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateIncomingUri(Uri uri) throws SecurityException {
        String str;
        int userIdFromAuthority;
        String authority = uri.getAuthority();
        if (!this.mSingleUser && (userIdFromAuthority = getUserIdFromAuthority(authority, -2)) != -2 && userIdFromAuthority != this.mContext.getUserId()) {
            throw new SecurityException("trying to query a ContentProvider in user " + this.mContext.getUserId() + " with a uri belonging to user " + userIdFromAuthority);
        }
        if (matchesOurAuthorities(getAuthorityWithoutUserId(authority))) {
            return;
        }
        String str2 = "The authority of the uri " + uri + " does not match the one of the contentProvider: ";
        if (this.mAuthority != null) {
            str = str2 + this.mAuthority;
        } else {
            str = str2 + Arrays.toString(this.mAuthorities);
        }
        throw new SecurityException(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri maybeGetUriWithoutUserId(Uri uri) {
        return this.mSingleUser ? uri : getUriWithoutUserId(uri);
    }

    public static int getUserIdFromAuthority(String str, int i) {
        int iLastIndexOf;
        if (str == null || (iLastIndexOf = str.lastIndexOf(64)) == -1) {
            return i;
        }
        try {
            return Integer.parseInt(str.substring(0, iLastIndexOf));
        } catch (NumberFormatException e) {
            Log.w(TAG, "Error parsing userId.", e);
            return -10000;
        }
    }

    public static int getUserIdFromAuthority(String str) {
        return getUserIdFromAuthority(str, -2);
    }

    public static int getUserIdFromUri(Uri uri, int i) {
        return uri == null ? i : getUserIdFromAuthority(uri.getAuthority(), i);
    }

    public static int getUserIdFromUri(Uri uri) {
        return getUserIdFromUri(uri, -2);
    }

    public static String getAuthorityWithoutUserId(String str) {
        if (str == null) {
            return null;
        }
        return str.substring(str.lastIndexOf(64) + 1);
    }

    public static Uri getUriWithoutUserId(Uri uri) {
        if (uri == null) {
            return null;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        builderBuildUpon.authority(getAuthorityWithoutUserId(uri.getAuthority()));
        return builderBuildUpon.build();
    }

    public static boolean uriHasUserId(Uri uri) {
        if (uri == null) {
            return false;
        }
        return !TextUtils.isEmpty(uri.getUserInfo());
    }

    public static Uri maybeAddUserId(Uri uri, int i) {
        if (uri == null) {
            return null;
        }
        if (i == -2 || !"content".equals(uri.getScheme()) || uriHasUserId(uri)) {
            return uri;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        builderBuildUpon.encodedAuthority("" + i + "@" + uri.getEncodedAuthority());
        return builderBuildUpon.build();
    }
}
