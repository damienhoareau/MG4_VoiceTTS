package android.media;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.database.SortCursor;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes.dex */
public class RingtoneManager {
    public static final String ACTION_RINGTONE_PICKER = "android.intent.action.RINGTONE_PICKER";
    public static final String EXTRA_RINGTONE_AUDIO_ATTRIBUTES_FLAGS = "android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS";
    public static final String EXTRA_RINGTONE_DEFAULT_URI = "android.intent.extra.ringtone.DEFAULT_URI";
    public static final String EXTRA_RINGTONE_EXISTING_URI = "android.intent.extra.ringtone.EXISTING_URI";

    @Deprecated
    public static final String EXTRA_RINGTONE_INCLUDE_DRM = "android.intent.extra.ringtone.INCLUDE_DRM";
    public static final String EXTRA_RINGTONE_PICKED_URI = "android.intent.extra.ringtone.PICKED_URI";
    public static final String EXTRA_RINGTONE_SHOW_DEFAULT = "android.intent.extra.ringtone.SHOW_DEFAULT";
    public static final String EXTRA_RINGTONE_SHOW_SILENT = "android.intent.extra.ringtone.SHOW_SILENT";
    public static final String EXTRA_RINGTONE_TITLE = "android.intent.extra.ringtone.TITLE";
    public static final String EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE";
    public static final int ID_COLUMN_INDEX = 0;
    private static final String[] INTERNAL_COLUMNS = {"_id", "title", "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\"", "title_key"};
    private static final String[] MEDIA_COLUMNS = {"_id", "title", "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\"", "title_key"};
    private static final String TAG = "RingtoneManager";
    public static final int TITLE_COLUMN_INDEX = 1;
    public static final int TYPE_ALARM = 4;
    public static final int TYPE_ALL = 7;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_RINGTONE = 1;
    public static final int URI_COLUMN_INDEX = 2;
    private final Activity mActivity;
    private final Context mContext;
    private Cursor mCursor;
    private final List<String> mFilterColumns;
    private boolean mIncludeParentRingtones;
    private Ringtone mPreviousRingtone;
    private boolean mStopPreviousRingtone;
    private int mType;

    private static String getSettingForType(int i) {
        if ((i & 1) != 0) {
            return Settings.System.RINGTONE;
        }
        if ((i & 2) != 0) {
            return Settings.System.NOTIFICATION_SOUND;
        }
        if ((i & 4) != 0) {
            return Settings.System.ALARM_ALERT;
        }
        return null;
    }

    @Deprecated
    public boolean getIncludeDrm() {
        return false;
    }

    public RingtoneManager(Activity activity) {
        this(activity, false);
    }

    public RingtoneManager(Activity activity, boolean z) {
        this.mType = 1;
        this.mFilterColumns = new ArrayList();
        this.mStopPreviousRingtone = true;
        this.mActivity = activity;
        this.mContext = activity;
        setType(this.mType);
        this.mIncludeParentRingtones = z;
    }

    public RingtoneManager(Context context) {
        this(context, false);
    }

    public RingtoneManager(Context context, boolean z) {
        this.mType = 1;
        this.mFilterColumns = new ArrayList();
        this.mStopPreviousRingtone = true;
        this.mActivity = null;
        this.mContext = context;
        setType(this.mType);
        this.mIncludeParentRingtones = z;
    }

    public void setType(int i) {
        if (this.mCursor != null) {
            throw new IllegalStateException("Setting filter columns should be done before querying for ringtones.");
        }
        this.mType = i;
        setFilterColumnsList(i);
    }

    public int inferStreamType() {
        int i = this.mType;
        if (i != 2) {
            return i != 4 ? 2 : 4;
        }
        return 5;
    }

    public void setStopPreviousRingtone(boolean z) {
        this.mStopPreviousRingtone = z;
    }

    public boolean getStopPreviousRingtone() {
        return this.mStopPreviousRingtone;
    }

    public void stopPreviousRingtone() {
        Ringtone ringtone = this.mPreviousRingtone;
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    @Deprecated
    public void setIncludeDrm(boolean z) {
        if (z) {
            Log.w(TAG, "setIncludeDrm no longer supported");
        }
    }

    public Cursor getCursor() {
        Cursor parentProfileRingtones;
        Cursor cursor = this.mCursor;
        if (cursor != null && cursor.requery()) {
            return this.mCursor;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(getInternalRingtones());
        arrayList.add(getMediaRingtones());
        if (this.mIncludeParentRingtones && (parentProfileRingtones = getParentProfileRingtones()) != null) {
            arrayList.add(parentProfileRingtones);
        }
        SortCursor sortCursor = new SortCursor((Cursor[]) arrayList.toArray(new Cursor[arrayList.size()]), "title_key");
        this.mCursor = sortCursor;
        return sortCursor;
    }

    private Cursor getParentProfileRingtones() {
        Context contextCreatePackageContextAsUser;
        UserInfo profileParent = UserManager.get(this.mContext).getProfileParent(this.mContext.getUserId());
        if (profileParent == null || profileParent.id == this.mContext.getUserId() || (contextCreatePackageContextAsUser = createPackageContextAsUser(this.mContext, profileParent.id)) == null) {
            return null;
        }
        return new ExternalRingtonesCursorWrapper(getMediaRingtones(contextCreatePackageContextAsUser), profileParent.id);
    }

    public Ringtone getRingtone(int i) {
        Ringtone ringtone;
        if (this.mStopPreviousRingtone && (ringtone = this.mPreviousRingtone) != null) {
            ringtone.stop();
        }
        Ringtone ringtone2 = getRingtone(this.mContext, getRingtoneUri(i), inferStreamType());
        this.mPreviousRingtone = ringtone2;
        return ringtone2;
    }

    public Uri getRingtoneUri(int i) {
        Cursor cursor = this.mCursor;
        if (cursor == null || !cursor.moveToPosition(i)) {
            return null;
        }
        return getUriFromCursor(this.mCursor);
    }

    private static Uri getExistingRingtoneUriFromPath(Context context, String str) throws Exception {
        Cursor cursorQuery = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{str}, null);
        if (cursorQuery != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    int i = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
                    if (i == -1) {
                        if (cursorQuery != null) {
                            $closeResource(null, cursorQuery);
                        }
                        return null;
                    }
                    Uri uriWithAppendedPath = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + i);
                    if (cursorQuery != null) {
                        $closeResource(null, cursorQuery);
                    }
                    return uriWithAppendedPath;
                }
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (cursorQuery != null) {
                        $closeResource(th, cursorQuery);
                    }
                    throw th2;
                }
            }
        }
        if (cursorQuery != null) {
            $closeResource(null, cursorQuery);
        }
        return null;
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    private static Uri getUriFromCursor(Cursor cursor) {
        return ContentUris.withAppendedId(Uri.parse(cursor.getString(2)), cursor.getLong(0));
    }

    public int getRingtonePosition(Uri uri) {
        if (uri == null) {
            return -1;
        }
        Cursor cursor = getCursor();
        int count = cursor.getCount();
        if (!cursor.moveToFirst()) {
            return -1;
        }
        Uri uri2 = null;
        int i = 0;
        String str = null;
        while (i < count) {
            String string = cursor.getString(2);
            if (uri2 == null || !string.equals(str)) {
                uri2 = Uri.parse(string);
            }
            if (uri.equals(ContentUris.withAppendedId(uri2, cursor.getLong(0)))) {
                return i;
            }
            cursor.move(1);
            i++;
            str = string;
        }
        return -1;
    }

    public static Uri getValidRingtoneUri(Context context) {
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        Uri validRingtoneUriFromCursorAndClose = getValidRingtoneUriFromCursorAndClose(context, ringtoneManager.getInternalRingtones());
        return validRingtoneUriFromCursorAndClose == null ? getValidRingtoneUriFromCursorAndClose(context, ringtoneManager.getMediaRingtones()) : validRingtoneUriFromCursorAndClose;
    }

    private static Uri getValidRingtoneUriFromCursorAndClose(Context context, Cursor cursor) {
        Uri uriFromCursor = null;
        if (cursor != null) {
            uriFromCursor = cursor.moveToFirst() ? getUriFromCursor(cursor) : null;
            cursor.close();
        }
        return uriFromCursor;
    }

    private Cursor getInternalRingtones() {
        return query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, INTERNAL_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key");
    }

    private Cursor getMediaRingtones() {
        return getMediaRingtones(this.mContext);
    }

    private Cursor getMediaRingtones(Context context) {
        if (context.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Process.myPid(), Process.myUid()) != 0) {
            Log.w(TAG, "No READ_EXTERNAL_STORAGE permission, ignoring ringtones on ext storage");
            return null;
        }
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED) || externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIA_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key", context);
        }
        return null;
    }

    private void setFilterColumnsList(int i) {
        List<String> list = this.mFilterColumns;
        list.clear();
        if ((i & 1) != 0) {
            list.add(MediaStore.Audio.AudioColumns.IS_RINGTONE);
        }
        if ((i & 2) != 0) {
            list.add(MediaStore.Audio.AudioColumns.IS_NOTIFICATION);
        }
        if ((i & 4) != 0) {
            list.add(MediaStore.Audio.AudioColumns.IS_ALARM);
        }
    }

    private static String constructBooleanTrueWhereClause(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int size = list.size() - 1; size >= 0; size--) {
            sb.append(list.get(size));
            sb.append("=1 or ");
        }
        if (list.size() > 0) {
            sb.setLength(sb.length() - 4);
        }
        sb.append(")");
        return sb.toString();
    }

    private Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return query(uri, strArr, str, strArr2, str2, this.mContext);
    }

    private Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, Context context) {
        Activity activity = this.mActivity;
        if (activity != null) {
            return activity.managedQuery(uri, strArr, str, strArr2, str2);
        }
        return context.getContentResolver().query(uri, strArr, str, strArr2, str2);
    }

    public static Ringtone getRingtone(Context context, Uri uri) {
        return getRingtone(context, uri, -1);
    }

    private static Ringtone getRingtone(Context context, Uri uri, int i) {
        try {
            Ringtone ringtone = new Ringtone(context, true);
            if (i >= 0) {
                ringtone.setStreamType(i);
            }
            ringtone.setUri(uri);
            return ringtone;
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ringtone " + uri + ": " + e);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0033  */
    private File getRingtonePathFromUri(Uri uri) throws Exception {
        String string;
        setFilterColumnsList(7);
        Cursor cursorQuery = query(uri, new String[]{"_data"}, constructBooleanTrueWhereClause(this.mFilterColumns), null, null);
        if (cursorQuery != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    string = cursorQuery.getString(cursorQuery.getColumnIndex("_data"));
                } else {
                    string = null;
                }
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (cursorQuery != null) {
                        $closeResource(th, cursorQuery);
                    }
                    throw th2;
                }
            }
        } else {
            string = null;
        }
        if (cursorQuery != null) {
            $closeResource(null, cursorQuery);
        }
        if (string != null) {
            return new File(string);
        }
        return null;
    }

    public static void disableSyncFromParent(Context context) {
        try {
            IAudioService.Stub.asInterface(ServiceManager.getService("audio")).disableRingtoneSync(context.getUserId());
        } catch (RemoteException unused) {
            Log.e(TAG, "Unable to disable ringtone sync.");
        }
    }

    public static void enableSyncFromParent(Context context) {
        Settings.Secure.putIntForUser(context.getContentResolver(), Settings.Secure.SYNC_PARENT_SOUNDS, 1, context.getUserId());
    }

    public static Uri getActualDefaultRingtoneUri(Context context, int i) {
        String settingForType = getSettingForType(i);
        if (settingForType == null) {
            return null;
        }
        String stringForUser = Settings.System.getStringForUser(context.getContentResolver(), settingForType, context.getUserId());
        Uri uri = stringForUser != null ? Uri.parse(stringForUser) : null;
        return (uri == null || ContentProvider.getUserIdFromUri(uri) != context.getUserId()) ? uri : ContentProvider.getUriWithoutUserId(uri);
    }

    public static void setActualDefaultRingtoneUri(Context context, int i, Uri uri) {
        String settingForType = getSettingForType(i);
        if (settingForType == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (Settings.Secure.getIntForUser(contentResolver, Settings.Secure.SYNC_PARENT_SOUNDS, 0, context.getUserId()) == 1) {
            disableSyncFromParent(context);
        }
        if (!isInternalRingtoneUri(uri)) {
            uri = ContentProvider.maybeAddUserId(uri, context.getUserId());
        }
        Settings.System.putStringForUser(contentResolver, settingForType, uri != null ? uri.toString() : null, context.getUserId());
        if (uri != null) {
            Uri cacheForType = getCacheForType(i, context.getUserId());
            try {
                InputStream inputStreamOpenRingtone = openRingtone(context, uri);
                try {
                    OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(cacheForType);
                    try {
                        FileUtils.copy(inputStreamOpenRingtone, outputStreamOpenOutputStream);
                        if (outputStreamOpenOutputStream != null) {
                            $closeResource(null, outputStreamOpenOutputStream);
                        }
                        if (inputStreamOpenRingtone != null) {
                            $closeResource(null, inputStreamOpenRingtone);
                        }
                    } catch (Throwable th) {
                        try {
                            throw th;
                        } catch (Throwable th2) {
                            if (outputStreamOpenOutputStream != null) {
                                $closeResource(th, outputStreamOpenOutputStream);
                            }
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    try {
                        throw th3;
                    } catch (Throwable th4) {
                        if (inputStreamOpenRingtone != null) {
                            $closeResource(th3, inputStreamOpenRingtone);
                        }
                        throw th4;
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, "Failed to cache ringtone: " + e);
            }
        }
    }

    private static boolean isInternalRingtoneUri(Uri uri) {
        return isRingtoneUriInStorage(uri, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private static boolean isExternalRingtoneUri(Uri uri) {
        return isRingtoneUriInStorage(uri, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    private static boolean isRingtoneUriInStorage(Uri uri, Uri uri2) {
        Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
        if (uriWithoutUserId == null) {
            return false;
        }
        return uriWithoutUserId.toString().startsWith(uri2.toString());
    }

    public boolean isCustomRingtone(Uri uri) {
        if (!isExternalRingtoneUri(uri)) {
            return false;
        }
        File ringtonePathFromUri = uri == null ? null : getRingtonePathFromUri(uri);
        File parentFile = ringtonePathFromUri != null ? ringtonePathFromUri.getParentFile() : null;
        if (parentFile == null) {
            return false;
        }
        String[] strArr = {Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_ALARMS};
        for (int i = 0; i < 3; i++) {
            if (parentFile.equals(Environment.getExternalStoragePublicDirectory(strArr[i]))) {
                return true;
            }
        }
        return false;
    }

    public Uri addCustomExternalRingtone(Uri uri, int i) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("External storage is not mounted. Unable to install ringtones.");
        }
        String type = this.mContext.getContentResolver().getType(uri);
        if (type == null || (!type.startsWith("audio/") && !type.equals("application/ogg"))) {
            throw new IllegalArgumentException("Ringtone file must have MIME type \"audio/*\". Given file has MIME type \"" + type + "\"");
        }
        String externalDirectoryForType = getExternalDirectoryForType(i);
        Context context = this.mContext;
        File uniqueExternalFile = Utils.getUniqueExternalFile(context, externalDirectoryForType, Utils.getFileDisplayNameFromUri(context, uri), type);
        InputStream inputStreamOpenInputStream = this.mContext.getContentResolver().openInputStream(uri);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(uniqueExternalFile);
            try {
                FileUtils.copy(inputStreamOpenInputStream, fileOutputStream);
                $closeResource(null, fileOutputStream);
                if (inputStreamOpenInputStream != null) {
                    $closeResource(null, inputStreamOpenInputStream);
                }
                try {
                    NewRingtoneScanner newRingtoneScanner = new NewRingtoneScanner(uniqueExternalFile);
                    try {
                        Uri uriTake = newRingtoneScanner.take();
                        $closeResource(null, newRingtoneScanner);
                        return uriTake;
                    } catch (Throwable th) {
                        try {
                            throw th;
                        } catch (Throwable th2) {
                            $closeResource(th, newRingtoneScanner);
                            throw th2;
                        }
                    }
                } catch (InterruptedException e) {
                    throw new IOException("Audio file failed to scan as a ringtone", e);
                }
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    $closeResource(th3, fileOutputStream);
                    throw th4;
                }
            }
        } catch (Throwable th5) {
            try {
                throw th5;
            } catch (Throwable th6) {
                if (inputStreamOpenInputStream != null) {
                    $closeResource(th5, inputStreamOpenInputStream);
                }
                throw th6;
            }
        }
    }

    private static final String getExternalDirectoryForType(int i) {
        if (i == 1) {
            return Environment.DIRECTORY_RINGTONES;
        }
        if (i == 2) {
            return Environment.DIRECTORY_NOTIFICATIONS;
        }
        if (i == 4) {
            return Environment.DIRECTORY_ALARMS;
        }
        throw new IllegalArgumentException("Unsupported ringtone type: " + i);
    }

    public boolean deleteExternalRingtone(Uri uri) {
        File ringtonePathFromUri;
        if (isCustomRingtone(uri) && (ringtonePathFromUri = getRingtonePathFromUri(uri)) != null) {
            try {
                if (this.mContext.getContentResolver().delete(uri, null, null) > 0) {
                    return ringtonePathFromUri.delete();
                }
            } catch (SecurityException e) {
                Log.d(TAG, "Unable to delete custom ringtone", e);
            }
        }
        return false;
    }

    private static InputStream openRingtone(Context context, Uri uri) throws IOException {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (IOException | SecurityException e) {
            Log.w(TAG, "Failed to open directly; attempting failover: " + e);
            try {
                return new ParcelFileDescriptor.AutoCloseInputStream(((AudioManager) context.getSystemService(AudioManager.class)).getRingtonePlayer().openRingtone(uri));
            } catch (Exception e2) {
                throw new IOException(e2);
            }
        }
    }

    public static Uri getCacheForType(int i) {
        return getCacheForType(i, UserHandle.getCallingUserId());
    }

    public static Uri getCacheForType(int i, int i2) {
        if ((i & 1) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.RINGTONE_CACHE_URI, i2);
        }
        if ((i & 2) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.NOTIFICATION_SOUND_CACHE_URI, i2);
        }
        if ((i & 4) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.ALARM_ALERT_CACHE_URI, i2);
        }
        return null;
    }

    public static boolean isDefault(Uri uri) {
        return getDefaultType(uri) != -1;
    }

    public static int getDefaultType(Uri uri) {
        Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
        if (uriWithoutUserId == null) {
            return -1;
        }
        if (uriWithoutUserId.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
            return 1;
        }
        if (uriWithoutUserId.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
            return 2;
        }
        return uriWithoutUserId.equals(Settings.System.DEFAULT_ALARM_ALERT_URI) ? 4 : -1;
    }

    public static Uri getDefaultUri(int i) {
        if ((i & 1) != 0) {
            return Settings.System.DEFAULT_RINGTONE_URI;
        }
        if ((i & 2) != 0) {
            return Settings.System.DEFAULT_NOTIFICATION_URI;
        }
        if ((i & 4) != 0) {
            return Settings.System.DEFAULT_ALARM_ALERT_URI;
        }
        return null;
    }

    private class NewRingtoneScanner implements Closeable, MediaScannerConnection.MediaScannerConnectionClient {
        private File mFile;
        private MediaScannerConnection mMediaScannerConnection;
        private LinkedBlockingQueue<Uri> mQueue = new LinkedBlockingQueue<>(1);

        public NewRingtoneScanner(File file) {
            this.mFile = file;
            MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(RingtoneManager.this.mContext, this);
            this.mMediaScannerConnection = mediaScannerConnection;
            mediaScannerConnection.connect();
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.mMediaScannerConnection.disconnect();
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient
        public void onMediaScannerConnected() {
            this.mMediaScannerConnection.scanFile(this.mFile.getAbsolutePath(), null);
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient, android.media.MediaScannerConnection.OnScanCompletedListener
        public void onScanCompleted(String str, Uri uri) {
            if (uri == null) {
                this.mFile.delete();
                return;
            }
            try {
                this.mQueue.put(uri);
            } catch (InterruptedException e) {
                Log.e(RingtoneManager.TAG, "Unable to put new ringtone Uri in queue", e);
            }
        }

        public Uri take() throws InterruptedException {
            return this.mQueue.take();
        }
    }

    private static Context createPackageContextAsUser(Context context, int i) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(i));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to create package context", e);
            return null;
        }
    }
}
