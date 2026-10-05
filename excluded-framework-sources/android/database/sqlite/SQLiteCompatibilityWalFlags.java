package android.database.sqlite;

import android.app.ActivityThread;
import android.app.Application;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.KeyValueListParser;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class SQLiteCompatibilityWalFlags {
    private static final String TAG = "SQLiteCompatibilityWalFlags";
    private static volatile boolean sCallingGlobalSettings;
    private static volatile boolean sCompatibilityWalSupported;
    private static volatile boolean sFlagsSet;
    private static volatile boolean sInitialized;
    private static volatile String sWALSyncMode;

    public static boolean areFlagsSet() {
        initIfNeeded();
        return sFlagsSet;
    }

    public static boolean isCompatibilityWalSupported() {
        initIfNeeded();
        return sCompatibilityWalSupported;
    }

    public static String getWALSyncMode() {
        initIfNeeded();
        return sWALSyncMode;
    }

    private static void initIfNeeded() {
        if (sInitialized || sCallingGlobalSettings) {
            return;
        }
        ActivityThread activityThreadCurrentActivityThread = ActivityThread.currentActivityThread();
        String string = null;
        Application application = activityThreadCurrentActivityThread == null ? null : activityThreadCurrentActivityThread.getApplication();
        if (application == null) {
            Log.w(TAG, "Cannot read global setting sqlite_compatibility_wal_flags - Application state not available");
        } else {
            try {
                sCallingGlobalSettings = true;
                string = Settings.Global.getString(application.getContentResolver(), Settings.Global.SQLITE_COMPATIBILITY_WAL_FLAGS);
                sCallingGlobalSettings = false;
            } catch (Throwable th) {
                sCallingGlobalSettings = false;
                throw th;
            }
        }
        init(string);
    }

    public static void init(String str) {
        if (TextUtils.isEmpty(str)) {
            sInitialized = true;
            return;
        }
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(str);
            sCompatibilityWalSupported = keyValueListParser.getBoolean("compatibility_wal_supported", SQLiteGlobal.isCompatibilityWalSupported());
            sWALSyncMode = keyValueListParser.getString("wal_syncmode", SQLiteGlobal.getWALSyncMode());
            Log.i(TAG, "Read compatibility WAL flags: compatibility_wal_supported=" + sCompatibilityWalSupported + ", wal_syncmode=" + sWALSyncMode);
            sFlagsSet = true;
            sInitialized = true;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Setting has invalid format: " + str, e);
            sInitialized = true;
        }
    }

    public static void reset() {
        sInitialized = false;
        sFlagsSet = false;
        sCompatibilityWalSupported = false;
        sWALSyncMode = null;
    }
}
