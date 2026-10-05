package androidx.core.content;

import android.accessibilityservice.AccessibilityService;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.job.JobScheduler;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.content.pm.LauncherApps;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.hardware.ConsumerIrManager;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.media.projection.MediaProjectionManager;
import android.media.session.MediaSessionManager;
import android.media.tv.TvInputManager;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.PowerManager;
import android.os.Process;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.print.PrintManager;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import java.io.File;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class ContextCompat {
    private static final String TAG = "ContextCompat";
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

    protected ContextCompat() {
    }

    public static boolean startActivities(Context context, Intent[] intentArr) {
        return startActivities(context, intentArr, null);
    }

    public static boolean startActivities(Context context, Intent[] intentArr, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 16) {
            context.startActivities(intentArr, bundle);
            return true;
        }
        context.startActivities(intentArr);
        return true;
    }

    public static void startActivity(Context context, Intent intent, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 16) {
            context.startActivity(intent, bundle);
        } else {
            context.startActivity(intent);
        }
    }

    public static File getDataDir(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.getDataDir();
        }
        String str = context.getApplicationInfo().dataDir;
        if (str != null) {
            return new File(str);
        }
        return null;
    }

    public static File[] getObbDirs(Context context) {
        return Build.VERSION.SDK_INT >= 19 ? context.getObbDirs() : new File[]{context.getObbDir()};
    }

    public static File[] getExternalFilesDirs(Context context, String str) {
        return Build.VERSION.SDK_INT >= 19 ? context.getExternalFilesDirs(str) : new File[]{context.getExternalFilesDir(str)};
    }

    public static File[] getExternalCacheDirs(Context context) {
        return Build.VERSION.SDK_INT >= 19 ? context.getExternalCacheDirs() : new File[]{context.getExternalCacheDir()};
    }

    private static File buildPath(File file, String... strArr) {
        for (String str : strArr) {
            if (file == null) {
                file = new File(str);
            } else if (str != null) {
                file = new File(file, str);
            }
        }
        return file;
    }

    public static Drawable getDrawable(Context context, int i) {
        int i2;
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(i);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            return context.getResources().getDrawable(i);
        }
        synchronized (sLock) {
            if (sTempValue == null) {
                sTempValue = new TypedValue();
            }
            context.getResources().getValue(i, sTempValue, true);
            i2 = sTempValue.resourceId;
        }
        return context.getResources().getDrawable(i2);
    }

    public static ColorStateList getColorStateList(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColorStateList(i);
        }
        return context.getResources().getColorStateList(i);
    }

    public static int getColor(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColor(i);
        }
        return context.getResources().getColor(i);
    }

    public static int checkSelfPermission(Context context, String str) {
        if (str == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return context.checkPermission(str, Process.myPid(), Process.myUid());
    }

    public static File getNoBackupFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getNoBackupFilesDir();
        }
        return createFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup"));
    }

    public static File getCodeCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getCodeCacheDir();
        }
        return createFilesDir(new File(context.getApplicationInfo().dataDir, "code_cache"));
    }

    private static synchronized File createFilesDir(File file) {
        if (file.exists() || file.mkdirs()) {
            return file;
        }
        if (file.exists()) {
            return file;
        }
        Log.w(TAG, "Unable to create files subdir " + file.getPath());
        return null;
    }

    public static Context createDeviceProtectedStorageContext(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.createDeviceProtectedStorageContext();
        }
        return null;
    }

    public static boolean isDeviceProtectedStorage(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.isDeviceProtectedStorage();
        }
        return false;
    }

    public static void startForegroundService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static <T> T getSystemService(Context context, Class<T> cls) {
        if (Build.VERSION.SDK_INT >= 23) {
            return (T) context.getSystemService(cls);
        }
        String systemServiceName = getSystemServiceName(context, cls);
        if (systemServiceName != null) {
            return (T) context.getSystemService(systemServiceName);
        }
        return null;
    }

    public static String getSystemServiceName(Context context, Class<?> cls) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getSystemServiceName(cls);
        }
        return LegacyServiceMapHolder.SERVICES.get(cls);
    }

    private static final class LegacyServiceMapHolder {
        static final HashMap<Class<?>, String> SERVICES = new HashMap<>();

        private LegacyServiceMapHolder() {
        }

        static {
            if (Build.VERSION.SDK_INT > 22) {
                SERVICES.put(SubscriptionManager.class, Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                SERVICES.put(UsageStatsManager.class, Context.USAGE_STATS_SERVICE);
            }
            if (Build.VERSION.SDK_INT > 21) {
                SERVICES.put(AppWidgetManager.class, Context.APPWIDGET_SERVICE);
                SERVICES.put(BatteryManager.class, Context.BATTERY_SERVICE);
                SERVICES.put(CameraManager.class, Context.CAMERA_SERVICE);
                SERVICES.put(JobScheduler.class, Context.JOB_SCHEDULER_SERVICE);
                SERVICES.put(LauncherApps.class, Context.LAUNCHER_APPS_SERVICE);
                SERVICES.put(MediaProjectionManager.class, Context.MEDIA_PROJECTION_SERVICE);
                SERVICES.put(MediaSessionManager.class, Context.MEDIA_SESSION_SERVICE);
                SERVICES.put(RestrictionsManager.class, Context.RESTRICTIONS_SERVICE);
                SERVICES.put(TelecomManager.class, Context.TELECOM_SERVICE);
                SERVICES.put(TvInputManager.class, Context.TV_INPUT_SERVICE);
            }
            if (Build.VERSION.SDK_INT > 19) {
                SERVICES.put(AppOpsManager.class, Context.APP_OPS_SERVICE);
                SERVICES.put(CaptioningManager.class, Context.CAPTIONING_SERVICE);
                SERVICES.put(ConsumerIrManager.class, Context.CONSUMER_IR_SERVICE);
                SERVICES.put(PrintManager.class, Context.PRINT_SERVICE);
            }
            if (Build.VERSION.SDK_INT > 18) {
                SERVICES.put(BluetoothManager.class, "bluetooth");
            }
            if (Build.VERSION.SDK_INT > 17) {
                SERVICES.put(DisplayManager.class, Context.DISPLAY_SERVICE);
                SERVICES.put(UserManager.class, "user");
            }
            if (Build.VERSION.SDK_INT > 16) {
                SERVICES.put(InputManager.class, "input");
                SERVICES.put(MediaRouter.class, Context.MEDIA_ROUTER_SERVICE);
                SERVICES.put(NsdManager.class, Context.NSD_SERVICE);
            }
            SERVICES.put(AccessibilityService.class, Context.ACCESSIBILITY_SERVICE);
            SERVICES.put(AccountManager.class, "account");
            SERVICES.put(ActivityManager.class, Context.ACTIVITY_SERVICE);
            SERVICES.put(AlarmManager.class, "alarm");
            SERVICES.put(AudioManager.class, "audio");
            SERVICES.put(ClipboardManager.class, Context.CLIPBOARD_SERVICE);
            SERVICES.put(ConnectivityManager.class, Context.CONNECTIVITY_SERVICE);
            SERVICES.put(DevicePolicyManager.class, Context.DEVICE_POLICY_SERVICE);
            SERVICES.put(DownloadManager.class, Context.DOWNLOAD_SERVICE);
            SERVICES.put(DropBoxManager.class, Context.DROPBOX_SERVICE);
            SERVICES.put(InputMethodManager.class, Context.INPUT_METHOD_SERVICE);
            SERVICES.put(KeyguardManager.class, Context.KEYGUARD_SERVICE);
            SERVICES.put(LayoutInflater.class, Context.LAYOUT_INFLATER_SERVICE);
            SERVICES.put(LocationManager.class, "location");
            SERVICES.put(NfcManager.class, "nfc");
            SERVICES.put(NotificationManager.class, Context.NOTIFICATION_SERVICE);
            SERVICES.put(PowerManager.class, Context.POWER_SERVICE);
            SERVICES.put(SearchManager.class, "search");
            SERVICES.put(SensorManager.class, Context.SENSOR_SERVICE);
            SERVICES.put(StorageManager.class, Context.STORAGE_SERVICE);
            SERVICES.put(TelephonyManager.class, "phone");
            SERVICES.put(TextServicesManager.class, Context.TEXT_SERVICES_MANAGER_SERVICE);
            SERVICES.put(UiModeManager.class, Context.UI_MODE_SERVICE);
            SERVICES.put(UsbManager.class, Context.USB_SERVICE);
            SERVICES.put(Vibrator.class, Context.VIBRATOR_SERVICE);
            SERVICES.put(WallpaperManager.class, Context.WALLPAPER_SERVICE);
            SERVICES.put(WifiP2pManager.class, Context.WIFI_P2P_SERVICE);
            SERVICES.put(WifiManager.class, "wifi");
            SERVICES.put(WindowManager.class, Context.WINDOW_SERVICE);
        }
    }
}
