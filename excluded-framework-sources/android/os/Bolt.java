package android.os;

import android.util.Slog;

/* JADX INFO: loaded from: classes2.dex */
public class Bolt {
    public static boolean BOLT_BOOTANIM = false;
    public static boolean BOLT_EARLY_HOME = false;
    public static boolean BOLT_ENABLED = true;
    public static boolean BOLT_KEYGUARD = false;
    public static boolean BOLT_PERSIST_APP = false;
    public static boolean BOLT_PMS = false;
    public static boolean BOLT_PRELOAD_CLS = false;
    public static boolean BOLT_PRELOAD_RES = false;
    public static boolean BOLT_WALLPAPER = false;
    public static final String PROP_BOOTANIM = "persist.bolt.bootanim";
    public static final String PROP_EARLY_HOME = "persist.bolt.early_home";
    public static final String PROP_ENABLED = "persist.bolt.enabled";
    public static final String PROP_KEYGUARD = "persist.bolt.keyguard";
    public static final String PROP_PERSIST_APP = "persist.bolt.persist_app";
    public static final String PROP_PMS = "persist.bolt.pms";
    public static final String PROP_PRELOAD_CLS = "persist.bolt.preload.cls";
    public static final String PROP_PRELOAD_RES = "persist.bolt.preload.res";
    public static final String PROP_WALLPAPER = "persist.bolt.wallpaper";
    public static final String TAG = "BOLT";

    static {
        Slog.d(TAG, "Current timestamp(ms): " + SystemClock.elapsedRealtime());
        BOLT_ENABLED = getBolt(PROP_ENABLED);
        BOLT_PRELOAD_CLS = getBolt(PROP_PRELOAD_CLS);
        BOLT_PRELOAD_RES = getBolt(PROP_PRELOAD_RES);
        BOLT_PMS = getBolt(PROP_PMS);
        BOLT_WALLPAPER = getBolt(PROP_WALLPAPER);
        BOLT_BOOTANIM = getBolt(PROP_BOOTANIM);
        BOLT_EARLY_HOME = getBolt(PROP_EARLY_HOME);
        BOLT_PERSIST_APP = getBolt(PROP_PERSIST_APP);
        BOLT_KEYGUARD = getBolt(PROP_KEYGUARD);
        Slog.d(TAG, "persist.bolt.enabled = " + BOLT_ENABLED);
        Slog.d(TAG, "persist.bolt.preload.cls = " + BOLT_PRELOAD_CLS);
        Slog.d(TAG, "persist.bolt.preload.res = " + BOLT_PRELOAD_RES);
        Slog.d(TAG, "persist.bolt.pms = " + BOLT_PMS);
        Slog.d(TAG, "persist.bolt.wallpaper = " + BOLT_WALLPAPER);
        Slog.d(TAG, "persist.bolt.bootanim = " + BOLT_BOOTANIM);
        Slog.d(TAG, "persist.bolt.early_home = " + BOLT_EARLY_HOME);
        Slog.d(TAG, "persist.bolt.keyguard = " + BOLT_KEYGUARD);
    }

    public static boolean getBolt(String str) {
        if (BOLT_ENABLED) {
            return SystemProperties.getBoolean(str, false);
        }
        return false;
    }
}
