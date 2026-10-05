package android.os;

import android.app.backup.FullBackup;
import android.app.job.JobParameters;
import android.app.slice.Slice;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.hardware.contexthub.V1_0.HostEndPoint;
import android.location.LocationManager;
import android.media.TtmlUtils;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiScanner;
import android.provider.SettingsStringUtil;
import android.provider.Telephony;
import android.telephony.SignalStrength;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.MutableBoolean;
import android.util.Pair;
import android.util.Printer;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.telephony.PhoneConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BatteryStats implements Parcelable {
    private static final String AGGREGATED_WAKELOCK_DATA = "awl";
    public static final int AGGREGATED_WAKE_TYPE_PARTIAL = 20;
    private static final String APK_DATA = "apk";
    private static final String AUDIO_DATA = "aud";
    public static final int AUDIO_TURNED_ON = 15;
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final int BATTERY_STATS_CHECKIN_VERSION = 9;
    private static final String BLUETOOTH_CONTROLLER_DATA = "ble";
    private static final String BLUETOOTH_MISC_DATA = "blem";
    public static final int BLUETOOTH_SCAN_ON = 19;
    public static final int BLUETOOTH_UNOPTIMIZED_SCAN_ON = 21;
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final String CAMERA_DATA = "cam";
    public static final int CAMERA_TURNED_ON = 17;
    private static final String CELLULAR_CONTROLLER_NAME = "Cellular";
    private static final String CHARGE_STEP_DATA = "csd";
    private static final String CHARGE_TIME_REMAIN_DATA = "ctr";
    static final int CHECKIN_VERSION = 32;
    private static final String CPU_DATA = "cpu";
    private static final String CPU_TIMES_AT_FREQ_DATA = "ctf";
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    public static final int DATA_CONNECTION_NONE = 0;
    public static final int DATA_CONNECTION_OTHER = 20;
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    public static final int DEVICE_IDLE_MODE_DEEP = 2;
    public static final int DEVICE_IDLE_MODE_LIGHT = 1;
    public static final int DEVICE_IDLE_MODE_OFF = 0;
    private static final String DISCHARGE_STEP_DATA = "dsd";
    private static final String DISCHARGE_TIME_REMAIN_DATA = "dtr";
    public static final int DUMP_CHARGED_ONLY = 2;
    public static final int DUMP_DAILY_ONLY = 4;
    public static final int DUMP_DEVICE_WIFI_ONLY = 64;
    public static final int DUMP_HISTORY_ONLY = 8;
    public static final int DUMP_INCLUDE_HISTORY = 16;
    public static final int DUMP_VERBOSE = 32;
    private static final String FLASHLIGHT_DATA = "fla";
    public static final int FLASHLIGHT_TURNED_ON = 16;
    public static final int FOREGROUND_ACTIVITY = 10;
    private static final String FOREGROUND_ACTIVITY_DATA = "fg";
    public static final int FOREGROUND_SERVICE = 22;
    private static final String FOREGROUND_SERVICE_DATA = "fgs";
    public static final int FULL_WIFI_LOCK = 5;
    private static final String GLOBAL_BLUETOOTH_CONTROLLER_DATA = "gble";
    private static final String GLOBAL_CPU_FREQ_DATA = "gcf";
    private static final String GLOBAL_MODEM_CONTROLLER_DATA = "gmcd";
    private static final String GLOBAL_NETWORK_DATA = "gn";
    private static final String GLOBAL_WIFI_CONTROLLER_DATA = "gwfcd";
    private static final String GLOBAL_WIFI_DATA = "gwfl";
    private static final String HISTORY_DATA = "h";
    public static final String[] HISTORY_EVENT_CHECKIN_NAMES;
    public static final IntToString[] HISTORY_EVENT_INT_FORMATTERS;
    public static final String[] HISTORY_EVENT_NAMES;
    public static final BitDescription[] HISTORY_STATE2_DESCRIPTIONS;
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS;
    private static final String HISTORY_STRING_POOL = "hsp";
    public static final int JOB = 14;
    private static final String JOBS_DEFERRED_DATA = "jbd";
    private static final String JOB_COMPLETION_DATA = "jbc";
    private static final String JOB_DATA = "jb";
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final boolean LOCAL_LOGV = false;
    public static final int MAX_TRACKED_SCREEN_STATE = 4;
    private static final String MISC_DATA = "m";
    private static final String MODEM_CONTROLLER_DATA = "mcd";
    public static final int NETWORK_BT_RX_DATA = 4;
    public static final int NETWORK_BT_TX_DATA = 5;
    private static final String NETWORK_DATA = "nt";
    public static final int NETWORK_MOBILE_BG_RX_DATA = 6;
    public static final int NETWORK_MOBILE_BG_TX_DATA = 7;
    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_BG_RX_DATA = 8;
    public static final int NETWORK_WIFI_BG_TX_DATA = 9;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    public static final int NUM_DATA_CONNECTION_TYPES = 21;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 10;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    public static final int NUM_WIFI_SIGNAL_STRENGTH_BINS = 5;
    public static final int NUM_WIFI_STATES = 8;
    public static final int NUM_WIFI_SUPPL_STATES = 13;
    private static final String POWER_USE_ITEM_DATA = "pwi";
    private static final String POWER_USE_SUMMARY_DATA = "pws";
    private static final String PROCESS_DATA = "pr";
    public static final int PROCESS_STATE = 12;
    private static final String RESOURCE_POWER_MANAGER_DATA = "rpm";
    public static final String RESULT_RECEIVER_CONTROLLER_KEY = "controller_activity";
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    protected static final boolean SCREEN_OFF_RPM_STATS_ENABLED = false;
    public static final int SENSOR = 3;
    private static final String SENSOR_DATA = "sr";
    public static final String SERVICE_NAME = "batterystats";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    private static final String STATE_TIME_DATA = "st";
    public static final int STATS_CURRENT = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_SINCE_UNPLUGGED = 2;
    public static final long STEP_LEVEL_INITIAL_MODE_MASK = 71776119061217280L;
    public static final int STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
    public static final long STEP_LEVEL_LEVEL_MASK = 280375465082880L;
    public static final int STEP_LEVEL_LEVEL_SHIFT = 40;
    public static final int[] STEP_LEVEL_MODES_OF_INTEREST;
    public static final int STEP_LEVEL_MODE_DEVICE_IDLE = 8;
    public static final String[] STEP_LEVEL_MODE_LABELS;
    public static final int STEP_LEVEL_MODE_POWER_SAVE = 4;
    public static final int STEP_LEVEL_MODE_SCREEN_STATE = 3;
    public static final int[] STEP_LEVEL_MODE_VALUES;
    public static final long STEP_LEVEL_MODIFIED_MODE_MASK = -72057594037927936L;
    public static final int STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
    public static final long STEP_LEVEL_TIME_MASK = 1099511627775L;
    public static final int SYNC = 13;
    private static final String SYNC_DATA = "sy";
    private static final String TAG = "BatteryStats";
    private static final String UID_DATA = "uid";
    public static final String UID_TIMES_TYPE_ALL = "A";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String VERSION_DATA = "vers";
    private static final String VIBRATOR_DATA = "vib";
    public static final int VIBRATOR_ON = 9;
    private static final String VIDEO_DATA = "vid";
    public static final int VIDEO_TURNED_ON = 8;
    private static final String WAKELOCK_DATA = "wl";
    private static final String WAKEUP_ALARM_DATA = "wua";
    private static final String WAKEUP_REASON_DATA = "wr";
    public static final int WAKE_TYPE_DRAW = 18;
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int WIFI_AGGREGATE_MULTICAST_ENABLED = 23;
    public static final int WIFI_BATCHED_SCAN = 11;
    private static final String WIFI_CONTROLLER_DATA = "wfcd";
    private static final String WIFI_CONTROLLER_NAME = "WiFi";
    private static final String WIFI_DATA = "wfl";
    private static final String WIFI_MULTICAST_DATA = "wmc";
    public static final int WIFI_MULTICAST_ENABLED = 7;
    private static final String WIFI_MULTICAST_TOTAL_DATA = "wmct";
    public static final int WIFI_RUNNING = 4;
    public static final int WIFI_SCAN = 6;
    private static final String WIFI_SIGNAL_STRENGTH_COUNT_DATA = "wsgc";
    private static final String WIFI_SIGNAL_STRENGTH_TIME_DATA = "wsgt";
    private static final String WIFI_STATE_COUNT_DATA = "wsc";
    static final String[] WIFI_STATE_NAMES;
    public static final int WIFI_STATE_OFF = 0;
    public static final int WIFI_STATE_OFF_SCANNING = 1;
    public static final int WIFI_STATE_ON_CONNECTED_P2P = 5;
    public static final int WIFI_STATE_ON_CONNECTED_STA = 4;
    public static final int WIFI_STATE_ON_CONNECTED_STA_P2P = 6;
    public static final int WIFI_STATE_ON_DISCONNECTED = 3;
    public static final int WIFI_STATE_ON_NO_NETWORKS = 2;
    public static final int WIFI_STATE_SOFT_AP = 7;
    private static final String WIFI_STATE_TIME_DATA = "wst";
    public static final int WIFI_SUPPL_STATE_ASSOCIATED = 7;
    public static final int WIFI_SUPPL_STATE_ASSOCIATING = 6;
    public static final int WIFI_SUPPL_STATE_AUTHENTICATING = 5;
    public static final int WIFI_SUPPL_STATE_COMPLETED = 10;
    private static final String WIFI_SUPPL_STATE_COUNT_DATA = "wssc";
    public static final int WIFI_SUPPL_STATE_DISCONNECTED = 1;
    public static final int WIFI_SUPPL_STATE_DORMANT = 11;
    public static final int WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE = 8;
    public static final int WIFI_SUPPL_STATE_GROUP_HANDSHAKE = 9;
    public static final int WIFI_SUPPL_STATE_INACTIVE = 3;
    public static final int WIFI_SUPPL_STATE_INTERFACE_DISABLED = 2;
    public static final int WIFI_SUPPL_STATE_INVALID = 0;
    public static final int WIFI_SUPPL_STATE_SCANNING = 4;
    private static final String WIFI_SUPPL_STATE_TIME_DATA = "wsst";
    public static final int WIFI_SUPPL_STATE_UNINITIALIZED = 12;
    private static final IntToString sIntToString;
    private static final IntToString sUidToString;
    private final StringBuilder mFormatBuilder = new StringBuilder(32);
    private final Formatter mFormatter = new Formatter(this.mFormatBuilder);
    private static final String[] STAT_NAMES = {"l", FullBackup.CACHE_TREE_TOKEN, "u"};
    public static final long[] JOB_FRESHNESS_BUCKETS = {3600000, 7200000, 14400000, 28800000, Long.MAX_VALUE};
    static final String[] SCREEN_BRIGHTNESS_NAMES = {"dark", "dim", "medium", "light", "bright"};
    static final String[] SCREEN_BRIGHTNESS_SHORT_NAMES = {"0", "1", "2", "3", "4"};
    static final String[] DATA_CONNECTION_NAMES = {"none", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", "hspap", "gsm", "td_scdma", "iwlan", "lte_ca", "other"};
    static final String[] WIFI_SUPPL_STATE_NAMES = {"invalid", "disconn", "disabled", "inactive", "scanning", "authenticating", "associating", "associated", "4-way-handshake", "group-handshake", "completed", "dormant", "uninit"};
    static final String[] WIFI_SUPPL_STATE_SHORT_NAMES = {"inv", "dsc", "dis", "inact", "scan", "auth", "ascing", "asced", "4-way", WifiConfiguration.GroupCipher.varName, "compl", "dorm", "uninit"};

    public static abstract class ControllerActivityCounter {
        public abstract LongCounter getIdleTimeCounter();

        public abstract LongCounter getPowerCounter();

        public abstract LongCounter getRxTimeCounter();

        public abstract LongCounter getScanTimeCounter();

        public abstract LongCounter getSleepTimeCounter();

        public abstract LongCounter[] getTxTimeCounters();
    }

    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static final class DailyItem {
        public LevelStepTracker mChargeSteps;
        public LevelStepTracker mDischargeSteps;
        public long mEndTime;
        public ArrayList<PackageChange> mPackageChanges;
        public long mStartTime;
    }

    @FunctionalInterface
    public interface IntToString {
        String applyAsString(int i);
    }

    public static abstract class LongCounter {
        public abstract long getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static abstract class LongCounterArray {
        public abstract long[] getCountsLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static final class PackageChange {
        public String mPackageName;
        public boolean mUpdate;
        public long mVersionCode;
    }

    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public long getCurrentDurationMsLocked(long j) {
            return -1L;
        }

        public long getMaxDurationMsLocked(long j) {
            return -1L;
        }

        public Timer getSubTimer() {
            return null;
        }

        public abstract long getTimeSinceMarkLocked(long j);

        public long getTotalDurationMsLocked(long j) {
            return -1L;
        }

        public abstract long getTotalTimeLocked(long j, int i);

        public boolean isRunningLocked() {
            return false;
        }

        public abstract void logState(Printer printer, String str);
    }

    public static int mapToInternalProcessState(int i) {
        if (i == 19) {
            return 19;
        }
        if (i == 2) {
            return 0;
        }
        if (i == 3) {
            return 1;
        }
        if (i <= 5) {
            return 2;
        }
        if (i <= 10) {
            return 3;
        }
        if (i <= 11) {
            return 4;
        }
        return i <= 12 ? 5 : 6;
    }

    public abstract void commitCurrentHistoryBatchLocked();

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeBatteryScreenOffRealtime(long j, int i);

    public abstract long computeBatteryScreenOffUptime(long j, int i);

    public abstract long computeBatteryTimeRemaining(long j);

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeChargeTimeRemaining(long j);

    public abstract long computeRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public abstract void finishIteratingHistoryLocked();

    public abstract void finishIteratingOldHistoryLocked();

    public abstract long getBatteryRealtime(long j);

    public abstract long getBatteryUptime(long j);

    public abstract ControllerActivityCounter getBluetoothControllerActivity();

    public abstract long getBluetoothScanTime(long j, int i);

    public abstract long getCameraOnTime(long j, int i);

    public abstract LevelStepTracker getChargeLevelStepTracker();

    public abstract long[] getCpuFreqs();

    public abstract long getCurrentDailyStartTime();

    public abstract LevelStepTracker getDailyChargeLevelStepTracker();

    public abstract LevelStepTracker getDailyDischargeLevelStepTracker();

    public abstract DailyItem getDailyItemLocked(int i);

    public abstract ArrayList<PackageChange> getDailyPackageChanges();

    public abstract int getDeviceIdleModeCount(int i, int i2);

    public abstract long getDeviceIdleModeTime(int i, long j, int i2);

    public abstract int getDeviceIdlingCount(int i, int i2);

    public abstract long getDeviceIdlingTime(int i, long j, int i2);

    public abstract int getDischargeAmount(int i);

    public abstract int getDischargeAmountScreenDoze();

    public abstract int getDischargeAmountScreenDozeSinceCharge();

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeCurrentLevel();

    public abstract LevelStepTracker getDischargeLevelStepTracker();

    public abstract int getDischargeStartLevel();

    public abstract String getEndPlatformVersion();

    public abstract int getEstimatedBatteryCapacity();

    public abstract long getFlashlightOnCount(int i);

    public abstract long getFlashlightOnTime(long j, int i);

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract long getGpsBatteryDrainMaMs();

    public abstract long getGpsSignalQualityTime(int i, long j, int i2);

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract long getHistoryBaseTime();

    public abstract int getHistoryStringPoolBytes();

    public abstract int getHistoryStringPoolSize();

    public abstract String getHistoryTagPoolString(int i);

    public abstract int getHistoryTagPoolUid(int i);

    public abstract int getHistoryTotalSize();

    public abstract int getHistoryUsedSize();

    public abstract long getInteractiveTime(long j, int i);

    public abstract boolean getIsOnBattery();

    public abstract LongSparseArray<? extends Timer> getKernelMemoryStats();

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract long getLongestDeviceIdleModeTime(int i);

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract int getMaxLearnedBatteryCapacity();

    public abstract int getMinLearnedBatteryCapacity();

    public abstract long getMobileRadioActiveAdjustedTime(int i);

    public abstract int getMobileRadioActiveCount(int i);

    public abstract long getMobileRadioActiveTime(long j, int i);

    public abstract int getMobileRadioActiveUnknownCount(int i);

    public abstract long getMobileRadioActiveUnknownTime(int i);

    public abstract ControllerActivityCounter getModemControllerActivity();

    public abstract long getNetworkActivityBytes(int i, int i2);

    public abstract long getNetworkActivityPackets(int i, int i2);

    public abstract boolean getNextHistoryLocked(HistoryItem historyItem);

    public abstract long getNextMaxDailyDeadline();

    public abstract long getNextMinDailyDeadline();

    public abstract boolean getNextOldHistoryLocked(HistoryItem historyItem);

    public abstract int getNumConnectivityChange(int i);

    public abstract int getParcelVersion();

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract Timer getPhoneDataConnectionTimer(int i);

    public abstract int getPhoneOnCount(int i);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract Timer getPhoneSignalScanningTimer();

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    protected abstract Timer getPhoneSignalStrengthTimer(int i);

    public abstract int getPowerSaveModeEnabledCount(int i);

    public abstract long getPowerSaveModeEnabledTime(long j, int i);

    public abstract Map<String, ? extends Timer> getRpmStats();

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract Timer getScreenBrightnessTimer(int i);

    public abstract int getScreenDozeCount(int i);

    public abstract long getScreenDozeTime(long j, int i);

    public abstract Map<String, ? extends Timer> getScreenOffRpmStats();

    public abstract int getScreenOnCount(int i);

    public abstract long getScreenOnTime(long j, int i);

    public abstract long getStartClockTime();

    public abstract int getStartCount();

    public abstract String getStartPlatformVersion();

    public abstract long getUahDischarge(int i);

    public abstract long getUahDischargeDeepDoze(int i);

    public abstract long getUahDischargeLightDoze(int i);

    public abstract long getUahDischargeScreenDoze(int i);

    public abstract long getUahDischargeScreenOff(int i);

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract Map<String, ? extends Timer> getWakeupReasonStats();

    public abstract long getWifiActiveTime(long j, int i);

    public abstract ControllerActivityCounter getWifiControllerActivity();

    public abstract int getWifiMulticastWakelockCount(int i);

    public abstract long getWifiMulticastWakelockTime(long j, int i);

    public abstract long getWifiOnTime(long j, int i);

    public abstract int getWifiSignalStrengthCount(int i, int i2);

    public abstract long getWifiSignalStrengthTime(int i, long j, int i2);

    public abstract Timer getWifiSignalStrengthTimer(int i);

    public abstract int getWifiStateCount(int i, int i2);

    public abstract long getWifiStateTime(int i, long j, int i2);

    public abstract Timer getWifiStateTimer(int i);

    public abstract int getWifiSupplStateCount(int i, int i2);

    public abstract long getWifiSupplStateTime(int i, long j, int i2);

    public abstract Timer getWifiSupplStateTimer(int i);

    public abstract boolean hasBluetoothActivityReporting();

    public abstract boolean hasModemActivityReporting();

    public abstract boolean hasWifiActivityReporting();

    public void prepareForDumpLocked() {
    }

    public abstract boolean startIteratingHistoryLocked();

    public abstract boolean startIteratingOldHistoryLocked();

    public abstract void writeToParcelWithoutUids(Parcel parcel, int i);

    static {
        String[] strArr = DATA_CONNECTION_NAMES;
        HISTORY_STATE_DESCRIPTIONS = new BitDescription[]{new BitDescription(Integer.MIN_VALUE, "running", FullBackup.ROOT_TREE_TOKEN), new BitDescription(1073741824, "wake_lock", "w"), new BitDescription(8388608, Context.SENSOR_SERVICE, "s"), new BitDescription(536870912, LocationManager.GPS_PROVIDER, "g"), new BitDescription(268435456, "wifi_full_lock", "Wl"), new BitDescription(134217728, "wifi_scan", "Ws"), new BitDescription(65536, "wifi_multicast", "Wm"), new BitDescription(67108864, "wifi_radio", "Wr"), new BitDescription(33554432, "mobile_radio", "Pr"), new BitDescription(2097152, "phone_scanning", "Psc"), new BitDescription(4194304, "audio", FullBackup.APK_TREE_TOKEN), new BitDescription(1048576, "screen", "S"), new BitDescription(524288, BatteryManager.EXTRA_PLUGGED, "BP"), new BitDescription(262144, "screen_doze", "Sd"), new BitDescription(HistoryItem.STATE_DATA_CONNECTION_MASK, 9, "data_conn", "Pcn", strArr, strArr), new BitDescription(448, 6, "phone_state", "Pst", new String[]{"in", "out", PhoneConstants.APN_TYPE_EMERGENCY, "off"}, new String[]{"in", "out", "em", "off"}), new BitDescription(56, 3, "phone_signal_strength", "Pss", SignalStrength.SIGNAL_STRENGTH_NAMES, new String[]{"0", "1", "2", "3", "4"}), new BitDescription(7, 0, "brightness", "Sb", SCREEN_BRIGHTNESS_NAMES, SCREEN_BRIGHTNESS_SHORT_NAMES)};
        HISTORY_STATE2_DESCRIPTIONS = new BitDescription[]{new BitDescription(Integer.MIN_VALUE, "power_save", "ps"), new BitDescription(1073741824, "video", Telephony.BaseMmsColumns.MMS_VERSION), new BitDescription(536870912, "wifi_running", "Ww"), new BitDescription(268435456, "wifi", "W"), new BitDescription(134217728, "flashlight", "fl"), new BitDescription(HistoryItem.STATE2_DEVICE_IDLE_MASK, 25, "device_idle", "di", new String[]{"off", "light", "full", "???"}, new String[]{"off", "light", "full", "???"}), new BitDescription(16777216, "charging", "ch"), new BitDescription(262144, "usb_data", "Ud"), new BitDescription(8388608, "phone_in_call", "Pcl"), new BitDescription(4194304, "bluetooth", "b"), new BitDescription(112, 4, "wifi_signal_strength", "Wss", new String[]{"0", "1", "2", "3", "4"}, new String[]{"0", "1", "2", "3", "4"}), new BitDescription(15, 0, "wifi_suppl", "Wsp", WIFI_SUPPL_STATE_NAMES, WIFI_SUPPL_STATE_SHORT_NAMES), new BitDescription(2097152, Context.CAMERA_SERVICE, "ca"), new BitDescription(1048576, "ble_scan", "bles"), new BitDescription(524288, "cellular_high_tx_power", "Chtp"), new BitDescription(128, 7, "gps_signal_quality", "Gss", new String[]{"poor", "good"}, new String[]{"poor", "good"})};
        HISTORY_EVENT_NAMES = new String[]{"null", "proc", FOREGROUND_ACTIVITY_DATA, "top", "sync", "wake_lock_in", "job", "user", "userfg", "conn", "active", "pkginst", "pkgunin", "alarm", Context.STATS_MANAGER, "pkginactive", "pkgactive", "tmpwhitelist", "screenwake", "wakeupap", "longwake", "est_capacity"};
        HISTORY_EVENT_CHECKIN_NAMES = new String[]{"Enl", "Epr", "Efg", "Etp", "Esy", "Ewl", "Ejb", "Eur", "Euf", "Ecn", "Eac", "Epi", "Epu", "Eal", "Est", "Eai", "Eaa", "Etw", "Esw", "Ewa", "Elw", "Eec"};
        sUidToString = new IntToString() { // from class: android.os.-$$Lambda$IyvVQC-0mKtsfXbnO0kDL64hrk0
            @Override // android.os.BatteryStats.IntToString
            public final String applyAsString(int i) {
                return UserHandle.formatUid(i);
            }
        };
        $$Lambda$BatteryStats$q1UvBdLgHRZVzc68BxdksTmbuCw __lambda_batterystats_q1uvbdlghrzvzc68bxdkstmbucw = new IntToString() { // from class: android.os.-$$Lambda$BatteryStats$q1UvBdLgHRZVzc68BxdksTmbuCw
            @Override // android.os.BatteryStats.IntToString
            public final String applyAsString(int i) {
                return Integer.toString(i);
            }
        };
        sIntToString = __lambda_batterystats_q1uvbdlghrzvzc68bxdkstmbucw;
        IntToString intToString = sUidToString;
        HISTORY_EVENT_INT_FORMATTERS = new IntToString[]{intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, intToString, __lambda_batterystats_q1uvbdlghrzvzc68bxdkstmbucw};
        WIFI_STATE_NAMES = new String[]{"off", "scanning", "no_net", "disconn", "sta", "p2p", "sta_p2p", "soft_ap"};
        STEP_LEVEL_MODES_OF_INTEREST = new int[]{7, 15, 11, 7, 7, 7, 7, 7, 15, 11};
        STEP_LEVEL_MODE_VALUES = new int[]{0, 4, 8, 1, 5, 2, 6, 3, 7, 11};
        STEP_LEVEL_MODE_LABELS = new String[]{"screen off", "screen off power save", "screen off device idle", "screen on", "screen on power save", "screen doze", "screen doze power save", "screen doze-suspend", "screen doze-suspend power save", "screen doze-suspend device idle"};
    }

    public static abstract class Uid {
        public static final int NUM_PROCESS_STATE = 7;
        public static final int NUM_USER_ACTIVITY_TYPES = 4;
        public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
        public static final int PROCESS_STATE_BACKGROUND = 3;
        public static final int PROCESS_STATE_CACHED = 6;
        public static final int PROCESS_STATE_FOREGROUND = 2;
        public static final int PROCESS_STATE_FOREGROUND_SERVICE = 1;
        public static final int PROCESS_STATE_HEAVY_WEIGHT = 5;
        public static final int PROCESS_STATE_TOP = 0;
        public static final int PROCESS_STATE_TOP_SLEEPING = 4;
        static final String[] PROCESS_STATE_NAMES = {"Top", "Fg Service", "Foreground", "Background", "Top Sleeping", "Heavy Weight", "Cached"};
        public static final String[] UID_PROCESS_TYPES = {"T", "FS", "F", "B", "TS", "HW", "C"};
        public static final int[] CRITICAL_PROC_STATES = {0, 1, 2};
        static final String[] USER_ACTIVITY_TYPES = {"other", "button", "touch", Context.ACCESSIBILITY_SERVICE};

        public static abstract class Pkg {

            public static abstract class Serv {
                public abstract int getLaunches(int i);

                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);
            }

            public abstract ArrayMap<String, ? extends Serv> getServiceStats();

            public abstract ArrayMap<String, ? extends Counter> getWakeupAlarmStats();
        }

        public static abstract class Proc {

            public static class ExcessivePower {
                public static final int TYPE_CPU = 2;
                public static final int TYPE_WAKE = 1;
                public long overTime;
                public int type;
                public long usedTime;
            }

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);

            public abstract long getForegroundTime(int i);

            public abstract int getNumAnrs(int i);

            public abstract int getNumCrashes(int i);

            public abstract int getStarts(int i);

            public abstract long getSystemTime(int i);

            public abstract long getUserTime(int i);

            public abstract boolean isActive();
        }

        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorBackgroundTime();

            public abstract Timer getSensorTime();
        }

        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);
        }

        public abstract Timer getAggregatedPartialWakelockTimer();

        public abstract Timer getAudioTurnedOnTimer();

        public abstract ControllerActivityCounter getBluetoothControllerActivity();

        public abstract Timer getBluetoothScanBackgroundTimer();

        public abstract Counter getBluetoothScanResultBgCounter();

        public abstract Counter getBluetoothScanResultCounter();

        public abstract Timer getBluetoothScanTimer();

        public abstract Timer getBluetoothUnoptimizedScanBackgroundTimer();

        public abstract Timer getBluetoothUnoptimizedScanTimer();

        public abstract Timer getCameraTurnedOnTimer();

        public abstract long getCpuActiveTime();

        public abstract long[] getCpuClusterTimes();

        public abstract long[] getCpuFreqTimes(int i);

        public abstract long[] getCpuFreqTimes(int i, int i2);

        public abstract void getDeferredJobsCheckinLineLocked(StringBuilder sb, int i);

        public abstract void getDeferredJobsLineLocked(StringBuilder sb, int i);

        public abstract Timer getFlashlightTurnedOnTimer();

        public abstract Timer getForegroundActivityTimer();

        public abstract Timer getForegroundServiceTimer();

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract ArrayMap<String, SparseIntArray> getJobCompletionStats();

        public abstract ArrayMap<String, ? extends Timer> getJobStats();

        public abstract int getMobileRadioActiveCount(int i);

        public abstract long getMobileRadioActiveTime(int i);

        public abstract long getMobileRadioApWakeupCount(int i);

        public abstract ControllerActivityCounter getModemControllerActivity();

        public abstract Timer getMulticastWakelockStats();

        public abstract long getNetworkActivityBytes(int i, int i2);

        public abstract long getNetworkActivityPackets(int i, int i2);

        public abstract ArrayMap<String, ? extends Pkg> getPackageStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract long getProcessStateTime(int i, long j, int i2);

        public abstract Timer getProcessStateTimer(int i);

        public abstract ArrayMap<String, ? extends Proc> getProcessStats();

        public abstract long[] getScreenOffCpuFreqTimes(int i);

        public abstract long[] getScreenOffCpuFreqTimes(int i, int i2);

        public abstract SparseArray<? extends Sensor> getSensorStats();

        public abstract ArrayMap<String, ? extends Timer> getSyncStats();

        public abstract long getSystemCpuTimeUs(int i);

        public abstract long getTimeAtCpuSpeed(int i, int i2, int i3);

        public abstract int getUid();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract long getUserCpuTimeUs(int i);

        public abstract Timer getVibratorOnTimer();

        public abstract Timer getVideoTurnedOnTimer();

        public abstract ArrayMap<String, ? extends Wakelock> getWakelockStats();

        public abstract int getWifiBatchedScanCount(int i, int i2);

        public abstract long getWifiBatchedScanTime(int i, long j, int i2);

        public abstract ControllerActivityCounter getWifiControllerActivity();

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getWifiRadioApWakeupCount(int i);

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getWifiScanActualTime(long j);

        public abstract int getWifiScanBackgroundCount(int i);

        public abstract long getWifiScanBackgroundTime(long j);

        public abstract Timer getWifiScanBackgroundTimer();

        public abstract int getWifiScanCount(int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract Timer getWifiScanTimer();

        public abstract boolean hasNetworkActivity();

        public abstract boolean hasUserActivity();

        public abstract void noteActivityPausedLocked(long j);

        public abstract void noteActivityResumedLocked(long j);

        public abstract void noteFullWifiLockAcquiredLocked(long j);

        public abstract void noteFullWifiLockReleasedLocked(long j);

        public abstract void noteUserActivityLocked(int i);

        public abstract void noteWifiBatchedScanStartedLocked(int i, long j);

        public abstract void noteWifiBatchedScanStoppedLocked(long j);

        public abstract void noteWifiMulticastDisabledLocked(long j);

        public abstract void noteWifiMulticastEnabledLocked(long j);

        public abstract void noteWifiRunningLocked(long j);

        public abstract void noteWifiScanStartedLocked(long j);

        public abstract void noteWifiScanStoppedLocked(long j);

        public abstract void noteWifiStoppedLocked(long j);

        public class Pid {
            public int mWakeNesting;
            public long mWakeStartMs;
            public long mWakeSumMs;

            public Pid() {
            }
        }
    }

    public static final class LevelStepTracker {
        public long mLastStepTime = -1;
        public int mNumStepDurations;
        public final long[] mStepDurations;

        public LevelStepTracker(int i) {
            this.mStepDurations = new long[i];
        }

        public LevelStepTracker(int i, long[] jArr) {
            this.mNumStepDurations = i;
            long[] jArr2 = new long[i];
            this.mStepDurations = jArr2;
            System.arraycopy(jArr, 0, jArr2, 0, i);
        }

        public long getDurationAt(int i) {
            return this.mStepDurations[i] & BatteryStats.STEP_LEVEL_TIME_MASK;
        }

        public int getLevelAt(int i) {
            return (int) ((this.mStepDurations[i] & BatteryStats.STEP_LEVEL_LEVEL_MASK) >> 40);
        }

        public int getInitModeAt(int i) {
            return (int) ((this.mStepDurations[i] & BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK) >> 48);
        }

        public int getModModeAt(int i) {
            return (int) ((this.mStepDurations[i] & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56);
        }

        private void appendHex(long j, int i, StringBuilder sb) {
            boolean z = false;
            while (i >= 0) {
                int i2 = (int) ((j >> i) & 15);
                i -= 4;
                if (z || i2 != 0) {
                    z = true;
                    if (i2 >= 0 && i2 <= 9) {
                        sb.append((char) (i2 + 48));
                    } else {
                        sb.append((char) ((i2 + 97) - 10));
                    }
                }
            }
        }

        public void encodeEntryAt(int i, StringBuilder sb) {
            long j = this.mStepDurations[i];
            long j2 = BatteryStats.STEP_LEVEL_TIME_MASK & j;
            int i2 = (int) ((BatteryStats.STEP_LEVEL_LEVEL_MASK & j) >> 40);
            int i3 = (int) ((BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK & j) >> 48);
            int i4 = (int) ((j & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56);
            int i5 = (i3 & 3) + 1;
            if (i5 == 1) {
                sb.append('f');
            } else if (i5 == 2) {
                sb.append('o');
            } else if (i5 == 3) {
                sb.append(DateFormat.DATE);
            } else if (i5 == 4) {
                sb.append(DateFormat.TIME_ZONE);
            }
            if ((i3 & 4) != 0) {
                sb.append('p');
            }
            if ((i3 & 8) != 0) {
                sb.append('i');
            }
            int i6 = (i4 & 3) + 1;
            if (i6 == 1) {
                sb.append('F');
            } else if (i6 == 2) {
                sb.append('O');
            } else if (i6 == 3) {
                sb.append('D');
            } else if (i6 == 4) {
                sb.append('Z');
            }
            if ((i4 & 4) != 0) {
                sb.append('P');
            }
            if ((i4 & 8) != 0) {
                sb.append('I');
            }
            sb.append('-');
            appendHex(i2, 4, sb);
            sb.append('-');
            appendHex(j2, 36, sb);
        }

        public void decodeEntryAt(int i, String str) {
            int i2;
            int i3;
            char cCharAt;
            int i4;
            int i5;
            char cCharAt2;
            long j;
            int length = str.length();
            int i6 = 0;
            long j2 = 0;
            while (i6 < length && (cCharAt2 = str.charAt(i6)) != '-') {
                i6++;
                if (cCharAt2 != 'D') {
                    if (cCharAt2 != 'F') {
                        if (cCharAt2 == 'I') {
                            j = 576460752303423488L;
                        } else if (cCharAt2 == 'Z') {
                            j = 216172782113783808L;
                        } else if (cCharAt2 == 'd') {
                            j = 562949953421312L;
                        } else if (cCharAt2 != 'f') {
                            if (cCharAt2 == 'i') {
                                j = 2251799813685248L;
                            } else if (cCharAt2 == 'z') {
                                j = 844424930131968L;
                            } else if (cCharAt2 == 'O') {
                                j = 72057594037927936L;
                            } else if (cCharAt2 == 'P') {
                                j = 288230376151711744L;
                            } else if (cCharAt2 == 'o') {
                                j = 281474976710656L;
                            } else if (cCharAt2 == 'p') {
                                j = TrafficStats.PB_IN_BYTES;
                            }
                        }
                    }
                    j2 |= 0;
                } else {
                    j = 144115188075855872L;
                }
                j2 |= j;
            }
            int i7 = i6 + 1;
            long j3 = 0;
            while (i7 < length && (cCharAt = str.charAt(i7)) != '-') {
                i7++;
                j3 <<= 4;
                if (cCharAt < '0' || cCharAt > '9') {
                    if (cCharAt >= 'a' && cCharAt <= 'f') {
                        i4 = cCharAt - 'a';
                    } else if (cCharAt >= 'A' && cCharAt <= 'F') {
                        i4 = cCharAt - 'A';
                    }
                    i5 = i4 + 10;
                } else {
                    i5 = cCharAt - '0';
                }
                j3 += (long) i5;
            }
            int i8 = i7 + 1;
            long j4 = j2 | ((j3 << 40) & BatteryStats.STEP_LEVEL_LEVEL_MASK);
            long j5 = 0;
            while (i8 < length) {
                char cCharAt3 = str.charAt(i8);
                if (cCharAt3 == '-') {
                    break;
                }
                i8++;
                j5 <<= 4;
                if (cCharAt3 < '0' || cCharAt3 > '9') {
                    if (cCharAt3 >= 'a' && cCharAt3 <= 'f') {
                        i2 = cCharAt3 - 'a';
                    } else if (cCharAt3 >= 'A' && cCharAt3 <= 'F') {
                        i2 = cCharAt3 - 'A';
                    }
                    i3 = i2 + 10;
                } else {
                    i3 = cCharAt3 - '0';
                }
                j5 += (long) i3;
            }
            this.mStepDurations[i] = (j5 & BatteryStats.STEP_LEVEL_TIME_MASK) | j4;
        }

        public void init() {
            this.mLastStepTime = -1L;
            this.mNumStepDurations = 0;
        }

        public void clearTime() {
            this.mLastStepTime = -1L;
        }

        public long computeTimePerLevel() {
            long[] jArr = this.mStepDurations;
            int i = this.mNumStepDurations;
            if (i <= 0) {
                return -1L;
            }
            long j = 0;
            for (int i2 = 0; i2 < i; i2++) {
                j += jArr[i2] & BatteryStats.STEP_LEVEL_TIME_MASK;
            }
            return j / ((long) i);
        }

        public long computeTimeEstimate(long j, long j2, int[] iArr) {
            long[] jArr = this.mStepDurations;
            int i = this.mNumStepDurations;
            if (i <= 0) {
                return -1L;
            }
            long j3 = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                long j4 = (jArr[i3] & BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK) >> 48;
                if ((((jArr[i3] & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56) & j) == 0 && (j4 & j) == j2) {
                    i2++;
                    j3 += jArr[i3] & BatteryStats.STEP_LEVEL_TIME_MASK;
                }
            }
            if (i2 <= 0) {
                return -1L;
            }
            if (iArr != null) {
                iArr[0] = i2;
            }
            return (j3 / ((long) i2)) * 100;
        }

        public void addLevelSteps(int i, long j, long j2) {
            int length = this.mNumStepDurations;
            long j3 = this.mLastStepTime;
            if (j3 >= 0 && i > 0) {
                long[] jArr = this.mStepDurations;
                long j4 = j2 - j3;
                for (int i2 = 0; i2 < i; i2++) {
                    System.arraycopy(jArr, 0, jArr, 1, jArr.length - 1);
                    long j5 = j4 / ((long) (i - i2));
                    j4 -= j5;
                    if (j5 > BatteryStats.STEP_LEVEL_TIME_MASK) {
                        j5 = 1099511627775L;
                    }
                    jArr[0] = j5 | j;
                }
                length += i;
                if (length > jArr.length) {
                    length = jArr.length;
                }
            }
            this.mNumStepDurations = length;
            this.mLastStepTime = j2;
        }

        public void readFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            if (i > this.mStepDurations.length) {
                throw new ParcelFormatException("more step durations than available: " + i);
            }
            this.mNumStepDurations = i;
            for (int i2 = 0; i2 < i; i2++) {
                this.mStepDurations[i2] = parcel.readLong();
            }
        }

        public void writeToParcel(Parcel parcel) {
            int i = this.mNumStepDurations;
            parcel.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                parcel.writeLong(this.mStepDurations[i2]);
            }
        }
    }

    public static final class HistoryTag {
        public int poolIdx;
        public String string;
        public int uid;

        public void setTo(HistoryTag historyTag) {
            this.string = historyTag.string;
            this.uid = historyTag.uid;
            this.poolIdx = historyTag.poolIdx;
        }

        public void setTo(String str, int i) {
            this.string = str;
            this.uid = i;
            this.poolIdx = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.string);
            parcel.writeInt(this.uid);
        }

        public void readFromParcel(Parcel parcel) {
            this.string = parcel.readString();
            this.uid = parcel.readInt();
            this.poolIdx = -1;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            HistoryTag historyTag = (HistoryTag) obj;
            return this.uid == historyTag.uid && this.string.equals(historyTag.string);
        }

        public int hashCode() {
            return (this.string.hashCode() * 31) + this.uid;
        }
    }

    public static final class HistoryStepDetails {
        public int appCpuSTime1;
        public int appCpuSTime2;
        public int appCpuSTime3;
        public int appCpuUTime1;
        public int appCpuUTime2;
        public int appCpuUTime3;
        public int appCpuUid1;
        public int appCpuUid2;
        public int appCpuUid3;
        public int statIOWaitTime;
        public int statIdlTime;
        public int statIrqTime;
        public String statPlatformIdleState;
        public int statSoftIrqTime;
        public String statSubsystemPowerState;
        public int statSystemTime;
        public int statUserTime;
        public int systemTime;
        public int userTime;

        public HistoryStepDetails() {
            clear();
        }

        public void clear() {
            this.systemTime = 0;
            this.userTime = 0;
            this.appCpuUid3 = -1;
            this.appCpuUid2 = -1;
            this.appCpuUid1 = -1;
            this.appCpuSTime3 = 0;
            this.appCpuUTime3 = 0;
            this.appCpuSTime2 = 0;
            this.appCpuUTime2 = 0;
            this.appCpuSTime1 = 0;
            this.appCpuUTime1 = 0;
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.userTime);
            parcel.writeInt(this.systemTime);
            parcel.writeInt(this.appCpuUid1);
            parcel.writeInt(this.appCpuUTime1);
            parcel.writeInt(this.appCpuSTime1);
            parcel.writeInt(this.appCpuUid2);
            parcel.writeInt(this.appCpuUTime2);
            parcel.writeInt(this.appCpuSTime2);
            parcel.writeInt(this.appCpuUid3);
            parcel.writeInt(this.appCpuUTime3);
            parcel.writeInt(this.appCpuSTime3);
            parcel.writeInt(this.statUserTime);
            parcel.writeInt(this.statSystemTime);
            parcel.writeInt(this.statIOWaitTime);
            parcel.writeInt(this.statIrqTime);
            parcel.writeInt(this.statSoftIrqTime);
            parcel.writeInt(this.statIdlTime);
            parcel.writeString(this.statPlatformIdleState);
            parcel.writeString(this.statSubsystemPowerState);
        }

        public void readFromParcel(Parcel parcel) {
            this.userTime = parcel.readInt();
            this.systemTime = parcel.readInt();
            this.appCpuUid1 = parcel.readInt();
            this.appCpuUTime1 = parcel.readInt();
            this.appCpuSTime1 = parcel.readInt();
            this.appCpuUid2 = parcel.readInt();
            this.appCpuUTime2 = parcel.readInt();
            this.appCpuSTime2 = parcel.readInt();
            this.appCpuUid3 = parcel.readInt();
            this.appCpuUTime3 = parcel.readInt();
            this.appCpuSTime3 = parcel.readInt();
            this.statUserTime = parcel.readInt();
            this.statSystemTime = parcel.readInt();
            this.statIOWaitTime = parcel.readInt();
            this.statIrqTime = parcel.readInt();
            this.statSoftIrqTime = parcel.readInt();
            this.statIdlTime = parcel.readInt();
            this.statPlatformIdleState = parcel.readString();
            this.statSubsystemPowerState = parcel.readString();
        }
    }

    public static final class HistoryItem implements Parcelable {
        public static final byte CMD_CURRENT_TIME = 5;
        public static final byte CMD_NULL = -1;
        public static final byte CMD_OVERFLOW = 6;
        public static final byte CMD_RESET = 7;
        public static final byte CMD_SHUTDOWN = 8;
        public static final byte CMD_START = 4;
        public static final byte CMD_UPDATE = 0;
        public static final int EVENT_ACTIVE = 10;
        public static final int EVENT_ALARM = 13;
        public static final int EVENT_ALARM_FINISH = 16397;
        public static final int EVENT_ALARM_START = 32781;
        public static final int EVENT_COLLECT_EXTERNAL_STATS = 14;
        public static final int EVENT_CONNECTIVITY_CHANGED = 9;
        public static final int EVENT_COUNT = 22;
        public static final int EVENT_FLAG_FINISH = 16384;
        public static final int EVENT_FLAG_START = 32768;
        public static final int EVENT_FOREGROUND = 2;
        public static final int EVENT_FOREGROUND_FINISH = 16386;
        public static final int EVENT_FOREGROUND_START = 32770;
        public static final int EVENT_JOB = 6;
        public static final int EVENT_JOB_FINISH = 16390;
        public static final int EVENT_JOB_START = 32774;
        public static final int EVENT_LONG_WAKE_LOCK = 20;
        public static final int EVENT_LONG_WAKE_LOCK_FINISH = 16404;
        public static final int EVENT_LONG_WAKE_LOCK_START = 32788;
        public static final int EVENT_NONE = 0;
        public static final int EVENT_PACKAGE_ACTIVE = 16;
        public static final int EVENT_PACKAGE_INACTIVE = 15;
        public static final int EVENT_PACKAGE_INSTALLED = 11;
        public static final int EVENT_PACKAGE_UNINSTALLED = 12;
        public static final int EVENT_PROC = 1;
        public static final int EVENT_PROC_FINISH = 16385;
        public static final int EVENT_PROC_START = 32769;
        public static final int EVENT_SCREEN_WAKE_UP = 18;
        public static final int EVENT_SYNC = 4;
        public static final int EVENT_SYNC_FINISH = 16388;
        public static final int EVENT_SYNC_START = 32772;
        public static final int EVENT_TEMP_WHITELIST = 17;
        public static final int EVENT_TEMP_WHITELIST_FINISH = 16401;
        public static final int EVENT_TEMP_WHITELIST_START = 32785;
        public static final int EVENT_TOP = 3;
        public static final int EVENT_TOP_FINISH = 16387;
        public static final int EVENT_TOP_START = 32771;
        public static final int EVENT_TYPE_MASK = -49153;
        public static final int EVENT_USER_FOREGROUND = 8;
        public static final int EVENT_USER_FOREGROUND_FINISH = 16392;
        public static final int EVENT_USER_FOREGROUND_START = 32776;
        public static final int EVENT_USER_RUNNING = 7;
        public static final int EVENT_USER_RUNNING_FINISH = 16391;
        public static final int EVENT_USER_RUNNING_START = 32775;
        public static final int EVENT_WAKEUP_AP = 19;
        public static final int EVENT_WAKE_LOCK = 5;
        public static final int EVENT_WAKE_LOCK_FINISH = 16389;
        public static final int EVENT_WAKE_LOCK_START = 32773;
        public static final int MOST_INTERESTING_STATES = 1835008;
        public static final int MOST_INTERESTING_STATES2 = -1749024768;
        public static final int SETTLE_TO_ZERO_STATES = -1900544;
        public static final int SETTLE_TO_ZERO_STATES2 = 1748959232;
        public static final int STATE2_BLUETOOTH_ON_FLAG = 4194304;
        public static final int STATE2_BLUETOOTH_SCAN_FLAG = 1048576;
        public static final int STATE2_CAMERA_FLAG = 2097152;
        public static final int STATE2_CELLULAR_HIGH_TX_POWER_FLAG = 524288;
        public static final int STATE2_CHARGING_FLAG = 16777216;
        public static final int STATE2_DEVICE_IDLE_MASK = 100663296;
        public static final int STATE2_DEVICE_IDLE_SHIFT = 25;
        public static final int STATE2_FLASHLIGHT_FLAG = 134217728;
        public static final int STATE2_GPS_SIGNAL_QUALITY_MASK = 128;
        public static final int STATE2_GPS_SIGNAL_QUALITY_SHIFT = 7;
        public static final int STATE2_PHONE_IN_CALL_FLAG = 8388608;
        public static final int STATE2_POWER_SAVE_FLAG = Integer.MIN_VALUE;
        public static final int STATE2_USB_DATA_LINK_FLAG = 262144;
        public static final int STATE2_VIDEO_ON_FLAG = 1073741824;
        public static final int STATE2_WIFI_ON_FLAG = 268435456;
        public static final int STATE2_WIFI_RUNNING_FLAG = 536870912;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_MASK = 112;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE2_WIFI_SUPPL_STATE_MASK = 15;
        public static final int STATE2_WIFI_SUPPL_STATE_SHIFT = 0;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_BRIGHTNESS_MASK = 7;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_CPU_RUNNING_FLAG = Integer.MIN_VALUE;
        public static final int STATE_DATA_CONNECTION_MASK = 15872;
        public static final int STATE_DATA_CONNECTION_SHIFT = 9;
        public static final int STATE_GPS_ON_FLAG = 536870912;
        public static final int STATE_MOBILE_RADIO_ACTIVE_FLAG = 33554432;
        public static final int STATE_PHONE_SCANNING_FLAG = 2097152;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_MASK = 56;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_SHIFT = 3;
        public static final int STATE_PHONE_STATE_MASK = 448;
        public static final int STATE_PHONE_STATE_SHIFT = 6;
        private static final int STATE_RESERVED_0 = 16777216;
        public static final int STATE_SCREEN_DOZE_FLAG = 262144;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_SENSOR_ON_FLAG = 8388608;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 268435456;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 65536;
        public static final int STATE_WIFI_RADIO_ACTIVE_FLAG = 67108864;
        public static final int STATE_WIFI_SCAN_FLAG = 134217728;
        public int batteryChargeUAh;
        public byte batteryHealth;
        public byte batteryLevel;
        public byte batteryPlugType;
        public byte batteryStatus;
        public short batteryTemperature;
        public char batteryVoltage;
        public byte cmd;
        public long currentTime;
        public int eventCode;
        public HistoryTag eventTag;
        public final HistoryTag localEventTag;
        public final HistoryTag localWakeReasonTag;
        public final HistoryTag localWakelockTag;
        public HistoryItem next;
        public int numReadInts;
        public int states;
        public int states2;
        public HistoryStepDetails stepDetails;
        public long time;
        public HistoryTag wakeReasonTag;
        public HistoryTag wakelockTag;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean isDeltaData() {
            return this.cmd == 0;
        }

        public HistoryItem() {
            this.cmd = (byte) -1;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
        }

        public HistoryItem(long j, Parcel parcel) {
            this.cmd = (byte) -1;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
            this.time = j;
            this.numReadInts = 2;
            readFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.time);
            parcel.writeInt((this.cmd & 255) | ((this.batteryLevel << 8) & 65280) | ((this.batteryStatus << WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK) & SurfaceControl.FX_SURFACE_MASK) | ((this.batteryHealth << 20) & 15728640) | ((this.batteryPlugType << 24) & 251658240) | (this.wakelockTag != null ? 268435456 : 0) | (this.wakeReasonTag != null ? 536870912 : 0) | (this.eventCode != 0 ? 1073741824 : 0));
            parcel.writeInt((this.batteryTemperature & HostEndPoint.BROADCAST) | ((this.batteryVoltage << 16) & (-65536)));
            parcel.writeInt(this.batteryChargeUAh);
            parcel.writeInt(this.states);
            parcel.writeInt(this.states2);
            HistoryTag historyTag = this.wakelockTag;
            if (historyTag != null) {
                historyTag.writeToParcel(parcel, i);
            }
            HistoryTag historyTag2 = this.wakeReasonTag;
            if (historyTag2 != null) {
                historyTag2.writeToParcel(parcel, i);
            }
            int i2 = this.eventCode;
            if (i2 != 0) {
                parcel.writeInt(i2);
                this.eventTag.writeToParcel(parcel, i);
            }
            byte b = this.cmd;
            if (b == 5 || b == 7) {
                parcel.writeLong(this.currentTime);
            }
        }

        public void readFromParcel(Parcel parcel) {
            int iDataPosition = parcel.dataPosition();
            int i = parcel.readInt();
            this.cmd = (byte) (i & 255);
            this.batteryLevel = (byte) ((i >> 8) & 255);
            this.batteryStatus = (byte) ((i >> 16) & 15);
            this.batteryHealth = (byte) ((i >> 20) & 15);
            this.batteryPlugType = (byte) ((i >> 24) & 15);
            int i2 = parcel.readInt();
            this.batteryTemperature = (short) (i2 & 65535);
            this.batteryVoltage = (char) ((i2 >> 16) & 65535);
            this.batteryChargeUAh = parcel.readInt();
            this.states = parcel.readInt();
            this.states2 = parcel.readInt();
            if ((268435456 & i) != 0) {
                HistoryTag historyTag = this.localWakelockTag;
                this.wakelockTag = historyTag;
                historyTag.readFromParcel(parcel);
            } else {
                this.wakelockTag = null;
            }
            if ((536870912 & i) != 0) {
                HistoryTag historyTag2 = this.localWakeReasonTag;
                this.wakeReasonTag = historyTag2;
                historyTag2.readFromParcel(parcel);
            } else {
                this.wakeReasonTag = null;
            }
            if ((i & 1073741824) != 0) {
                this.eventCode = parcel.readInt();
                HistoryTag historyTag3 = this.localEventTag;
                this.eventTag = historyTag3;
                historyTag3.readFromParcel(parcel);
            } else {
                this.eventCode = 0;
                this.eventTag = null;
            }
            byte b = this.cmd;
            if (b == 5 || b == 7) {
                this.currentTime = parcel.readLong();
            } else {
                this.currentTime = 0L;
            }
            this.numReadInts += (parcel.dataPosition() - iDataPosition) / 4;
        }

        public void clear() {
            this.time = 0L;
            this.cmd = (byte) -1;
            this.batteryLevel = (byte) 0;
            this.batteryStatus = (byte) 0;
            this.batteryHealth = (byte) 0;
            this.batteryPlugType = (byte) 0;
            this.batteryTemperature = (short) 0;
            this.batteryVoltage = (char) 0;
            this.batteryChargeUAh = 0;
            this.states = 0;
            this.states2 = 0;
            this.wakelockTag = null;
            this.wakeReasonTag = null;
            this.eventCode = 0;
            this.eventTag = null;
        }

        public void setTo(HistoryItem historyItem) {
            this.time = historyItem.time;
            this.cmd = historyItem.cmd;
            setToCommon(historyItem);
        }

        public void setTo(long j, byte b, HistoryItem historyItem) {
            this.time = j;
            this.cmd = b;
            setToCommon(historyItem);
        }

        private void setToCommon(HistoryItem historyItem) {
            this.batteryLevel = historyItem.batteryLevel;
            this.batteryStatus = historyItem.batteryStatus;
            this.batteryHealth = historyItem.batteryHealth;
            this.batteryPlugType = historyItem.batteryPlugType;
            this.batteryTemperature = historyItem.batteryTemperature;
            this.batteryVoltage = historyItem.batteryVoltage;
            this.batteryChargeUAh = historyItem.batteryChargeUAh;
            this.states = historyItem.states;
            this.states2 = historyItem.states2;
            if (historyItem.wakelockTag != null) {
                HistoryTag historyTag = this.localWakelockTag;
                this.wakelockTag = historyTag;
                historyTag.setTo(historyItem.wakelockTag);
            } else {
                this.wakelockTag = null;
            }
            if (historyItem.wakeReasonTag != null) {
                HistoryTag historyTag2 = this.localWakeReasonTag;
                this.wakeReasonTag = historyTag2;
                historyTag2.setTo(historyItem.wakeReasonTag);
            } else {
                this.wakeReasonTag = null;
            }
            this.eventCode = historyItem.eventCode;
            if (historyItem.eventTag != null) {
                HistoryTag historyTag3 = this.localEventTag;
                this.eventTag = historyTag3;
                historyTag3.setTo(historyItem.eventTag);
            } else {
                this.eventTag = null;
            }
            this.currentTime = historyItem.currentTime;
        }

        public boolean sameNonEvent(HistoryItem historyItem) {
            return this.batteryLevel == historyItem.batteryLevel && this.batteryStatus == historyItem.batteryStatus && this.batteryHealth == historyItem.batteryHealth && this.batteryPlugType == historyItem.batteryPlugType && this.batteryTemperature == historyItem.batteryTemperature && this.batteryVoltage == historyItem.batteryVoltage && this.batteryChargeUAh == historyItem.batteryChargeUAh && this.states == historyItem.states && this.states2 == historyItem.states2 && this.currentTime == historyItem.currentTime;
        }

        public boolean same(HistoryItem historyItem) {
            if (!sameNonEvent(historyItem) || this.eventCode != historyItem.eventCode) {
                return false;
            }
            HistoryTag historyTag = this.wakelockTag;
            HistoryTag historyTag2 = historyItem.wakelockTag;
            if (historyTag != historyTag2 && (historyTag == null || historyTag2 == null || !historyTag.equals(historyTag2))) {
                return false;
            }
            HistoryTag historyTag3 = this.wakeReasonTag;
            HistoryTag historyTag4 = historyItem.wakeReasonTag;
            if (historyTag3 != historyTag4 && (historyTag3 == null || historyTag4 == null || !historyTag3.equals(historyTag4))) {
                return false;
            }
            HistoryTag historyTag5 = this.eventTag;
            HistoryTag historyTag6 = historyItem.eventTag;
            if (historyTag5 != historyTag6) {
                return (historyTag5 == null || historyTag6 == null || !historyTag5.equals(historyTag6)) ? false : true;
            }
            return true;
        }
    }

    public static final class HistoryEventTracker {
        private final HashMap<String, SparseIntArray>[] mActiveEvents = new HashMap[22];

        public boolean updateState(int i, String str, int i2, int i3) {
            SparseIntArray sparseIntArray;
            int iIndexOfKey;
            if ((32768 & i) == 0) {
                if ((i & 16384) == 0) {
                    return true;
                }
                HashMap<String, SparseIntArray> map = this.mActiveEvents[i & HistoryItem.EVENT_TYPE_MASK];
                if (map == null || (sparseIntArray = map.get(str)) == null || (iIndexOfKey = sparseIntArray.indexOfKey(i2)) < 0) {
                    return false;
                }
                sparseIntArray.removeAt(iIndexOfKey);
                if (sparseIntArray.size() > 0) {
                    return true;
                }
                map.remove(str);
                return true;
            }
            int i4 = i & HistoryItem.EVENT_TYPE_MASK;
            HashMap<String, SparseIntArray> map2 = this.mActiveEvents[i4];
            if (map2 == null) {
                map2 = new HashMap<>();
                this.mActiveEvents[i4] = map2;
            }
            SparseIntArray sparseIntArray2 = map2.get(str);
            if (sparseIntArray2 == null) {
                sparseIntArray2 = new SparseIntArray();
                map2.put(str, sparseIntArray2);
            }
            if (sparseIntArray2.indexOfKey(i2) >= 0) {
                return false;
            }
            sparseIntArray2.put(i2, i3);
            return true;
        }

        public void removeEvents(int i) {
            this.mActiveEvents[i & HistoryItem.EVENT_TYPE_MASK] = null;
        }

        public HashMap<String, SparseIntArray> getStateForEvent(int i) {
            return this.mActiveEvents[i];
        }
    }

    public static final class BitDescription {
        public final int mask;
        public final String name;
        public final int shift;
        public final String shortName;
        public final String[] shortValues;
        public final String[] values;

        public BitDescription(int i, String str, String str2) {
            this.mask = i;
            this.shift = -1;
            this.name = str;
            this.shortName = str2;
            this.values = null;
            this.shortValues = null;
        }

        public BitDescription(int i, int i2, String str, String str2, String[] strArr, String[] strArr2) {
            this.mask = i;
            this.shift = i2;
            this.name = str;
            this.shortName = str2;
            this.values = strArr;
            this.shortValues = strArr2;
        }
    }

    private static final void formatTimeRaw(StringBuilder sb, long j) {
        long j2 = j / 86400;
        if (j2 != 0) {
            sb.append(j2);
            sb.append("d ");
        }
        long j3 = j2 * 60 * 60 * 24;
        long j4 = (j - j3) / 3600;
        if (j4 != 0 || j3 != 0) {
            sb.append(j4);
            sb.append("h ");
        }
        long j5 = j3 + (j4 * 60 * 60);
        long j6 = (j - j5) / 60;
        if (j6 != 0 || j5 != 0) {
            sb.append(j6);
            sb.append("m ");
        }
        long j7 = j5 + (j6 * 60);
        if (j == 0 && j7 == 0) {
            return;
        }
        sb.append(j - j7);
        sb.append("s ");
    }

    public static final void formatTimeMs(StringBuilder sb, long j) {
        long j2 = j / 1000;
        formatTimeRaw(sb, j2);
        sb.append(j - (j2 * 1000));
        sb.append("ms ");
    }

    public static final void formatTimeMsNoSpace(StringBuilder sb, long j) {
        long j2 = j / 1000;
        formatTimeRaw(sb, j2);
        sb.append(j - (j2 * 1000));
        sb.append("ms");
    }

    public final String formatRatioLocked(long j, long j2) {
        if (j2 == 0) {
            return "--%";
        }
        this.mFormatBuilder.setLength(0);
        this.mFormatter.format("%.1f%%", Float.valueOf((j / j2) * 100.0f));
        return this.mFormatBuilder.toString();
    }

    final String formatBytesLocked(long j) {
        this.mFormatBuilder.setLength(0);
        if (j < 1024) {
            return j + "B";
        }
        if (j < 1048576) {
            this.mFormatter.format("%.2fKB", Double.valueOf(j / 1024.0d));
            return this.mFormatBuilder.toString();
        }
        if (j < 1073741824) {
            this.mFormatter.format("%.2fMB", Double.valueOf(j / 1048576.0d));
            return this.mFormatBuilder.toString();
        }
        this.mFormatter.format("%.2fGB", Double.valueOf(j / 1.073741824E9d));
        return this.mFormatBuilder.toString();
    }

    private static long roundUsToMs(long j) {
        return (j + 500) / 1000;
    }

    private static long computeWakeLock(Timer timer, long j, int i) {
        if (timer != null) {
            return (timer.getTotalTimeLocked(j, i) + 500) / 1000;
        }
        return 0L;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        if (timer != null) {
            long jComputeWakeLock = computeWakeLock(timer, j, i);
            int countLocked = timer.getCountLocked(i);
            if (jComputeWakeLock != 0) {
                sb.append(str2);
                formatTimeMs(sb, jComputeWakeLock);
                if (str != null) {
                    sb.append(str);
                    sb.append(' ');
                }
                sb.append('(');
                sb.append(countLocked);
                sb.append(" times)");
                long j2 = j / 1000;
                long maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
                if (maxDurationMsLocked >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMsLocked);
                }
                long totalDurationMsLocked = timer.getTotalDurationMsLocked(j2);
                if (totalDurationMsLocked > jComputeWakeLock) {
                    sb.append(" actual=");
                    sb.append(totalDurationMsLocked);
                }
                if (!timer.isRunningLocked()) {
                    return ", ";
                }
                long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
                if (currentDurationMsLocked >= 0) {
                    sb.append(" (running for ");
                    sb.append(currentDurationMsLocked);
                    sb.append("ms)");
                    return ", ";
                }
                sb.append(" (running)");
                return ", ";
            }
        }
        return str2;
    }

    private static final boolean printTimer(PrintWriter printWriter, StringBuilder sb, Timer timer, long j, int i, String str, String str2) {
        if (timer != null) {
            long totalTimeLocked = (timer.getTotalTimeLocked(j, i) + 500) / 1000;
            int countLocked = timer.getCountLocked(i);
            if (totalTimeLocked != 0) {
                sb.setLength(0);
                sb.append(str);
                sb.append("    ");
                sb.append(str2);
                sb.append(": ");
                formatTimeMs(sb, totalTimeLocked);
                sb.append("realtime (");
                sb.append(countLocked);
                sb.append(" times)");
                long j2 = j / 1000;
                long maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
                if (maxDurationMsLocked >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMsLocked);
                }
                if (timer.isRunningLocked()) {
                    long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
                    if (currentDurationMsLocked >= 0) {
                        sb.append(" (running for ");
                        sb.append(currentDurationMsLocked);
                        sb.append("ms)");
                    } else {
                        sb.append(" (running)");
                    }
                }
                printWriter.println(sb.toString());
                return true;
            }
        }
        return false;
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        int countLocked;
        long totalDurationMsLocked;
        long currentDurationMsLocked;
        long maxDurationMsLocked;
        String str3;
        long totalTimeLocked = 0;
        if (timer != null) {
            totalTimeLocked = timer.getTotalTimeLocked(j, i);
            countLocked = timer.getCountLocked(i);
            long j2 = j / 1000;
            currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
            maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
            totalDurationMsLocked = timer.getTotalDurationMsLocked(j2);
        } else {
            countLocked = 0;
            totalDurationMsLocked = 0;
            currentDurationMsLocked = 0;
            maxDurationMsLocked = 0;
        }
        sb.append(str2);
        sb.append((totalTimeLocked + 500) / 1000);
        sb.append(',');
        if (str != null) {
            str3 = str + ",";
        } else {
            str3 = "";
        }
        sb.append(str3);
        sb.append(countLocked);
        sb.append(',');
        sb.append(currentDurationMsLocked);
        sb.append(',');
        sb.append(maxDurationMsLocked);
        if (str != null) {
            sb.append(',');
            sb.append(totalDurationMsLocked);
        }
        return ",";
    }

    private static final void dumpLineHeader(PrintWriter printWriter, int i, String str, String str2) {
        printWriter.print(9);
        printWriter.print(',');
        printWriter.print(i);
        printWriter.print(',');
        printWriter.print(str);
        printWriter.print(',');
        printWriter.print(str2);
    }

    private static final void dumpLine(PrintWriter printWriter, int i, String str, String str2, Object... objArr) {
        dumpLineHeader(printWriter, i, str, str2);
        for (Object obj : objArr) {
            printWriter.print(',');
            printWriter.print(obj);
        }
        printWriter.println();
    }

    private static final void dumpTimer(PrintWriter printWriter, int i, String str, String str2, Timer timer, long j, int i2) {
        if (timer != null) {
            long jRoundUsToMs = roundUsToMs(timer.getTotalTimeLocked(j, i2));
            int countLocked = timer.getCountLocked(i2);
            if (jRoundUsToMs == 0 && countLocked == 0) {
                return;
            }
            dumpLine(printWriter, i, str, str2, Long.valueOf(jRoundUsToMs), Integer.valueOf(countLocked));
        }
    }

    private static void dumpTimer(ProtoOutputStream protoOutputStream, long j, Timer timer, long j2, int i) {
        if (timer == null) {
            return;
        }
        long jRoundUsToMs = roundUsToMs(timer.getTotalTimeLocked(j2, i));
        int countLocked = timer.getCountLocked(i);
        long j3 = j2 / 1000;
        long maxDurationMsLocked = timer.getMaxDurationMsLocked(j3);
        long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j3);
        long totalDurationMsLocked = timer.getTotalDurationMsLocked(j3);
        if (jRoundUsToMs == 0 && countLocked == 0 && maxDurationMsLocked == -1 && currentDurationMsLocked == -1 && totalDurationMsLocked == -1) {
            return;
        }
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1112396529665L, jRoundUsToMs);
        protoOutputStream.write(1112396529666L, countLocked);
        if (maxDurationMsLocked != -1) {
            protoOutputStream.write(1112396529667L, maxDurationMsLocked);
        }
        if (currentDurationMsLocked != -1) {
            protoOutputStream.write(1112396529668L, currentDurationMsLocked);
        }
        if (totalDurationMsLocked != -1) {
            protoOutputStream.write(1112396529669L, totalDurationMsLocked);
        }
        protoOutputStream.end(jStart);
    }

    private static boolean controllerActivityHasData(ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityCounter == null) {
            return false;
        }
        if (controllerActivityCounter.getIdleTimeCounter().getCountLocked(i) != 0 || controllerActivityCounter.getRxTimeCounter().getCountLocked(i) != 0 || controllerActivityCounter.getPowerCounter().getCountLocked(i) != 0) {
            return true;
        }
        for (LongCounter longCounter : controllerActivityCounter.getTxTimeCounters()) {
            if (longCounter.getCountLocked(i) != 0) {
                return true;
            }
        }
        return false;
    }

    private static final void dumpControllerActivityLine(PrintWriter printWriter, int i, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i2) {
        if (controllerActivityHasData(controllerActivityCounter, i2)) {
            dumpLineHeader(printWriter, i, str, str2);
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getIdleTimeCounter().getCountLocked(i2));
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getRxTimeCounter().getCountLocked(i2));
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getPowerCounter().getCountLocked(i2) / 3600000);
            LongCounter[] txTimeCounters = controllerActivityCounter.getTxTimeCounters();
            for (LongCounter longCounter : txTimeCounters) {
                printWriter.print(",");
                printWriter.print(longCounter.getCountLocked(i2));
            }
            printWriter.println();
        }
    }

    private static void dumpControllerActivityProto(ProtoOutputStream protoOutputStream, long j, ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityHasData(controllerActivityCounter, i)) {
            long jStart = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, controllerActivityCounter.getIdleTimeCounter().getCountLocked(i));
            protoOutputStream.write(1112396529666L, controllerActivityCounter.getRxTimeCounter().getCountLocked(i));
            protoOutputStream.write(1112396529667L, controllerActivityCounter.getPowerCounter().getCountLocked(i) / 3600000);
            LongCounter[] txTimeCounters = controllerActivityCounter.getTxTimeCounters();
            for (int i2 = 0; i2 < txTimeCounters.length; i2++) {
                LongCounter longCounter = txTimeCounters[i2];
                long jStart2 = protoOutputStream.start(2246267895812L);
                protoOutputStream.write(1120986464257L, i2);
                protoOutputStream.write(1112396529666L, longCounter.getCountLocked(i));
                protoOutputStream.end(jStart2);
            }
            protoOutputStream.end(jStart);
        }
    }

    private final void printControllerActivityIfInteresting(PrintWriter printWriter, StringBuilder sb, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityHasData(controllerActivityCounter, i)) {
            printControllerActivity(printWriter, sb, str, str2, controllerActivityCounter, i);
        }
    }

    private final void printControllerActivity(PrintWriter printWriter, StringBuilder sb, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i) {
        String str3;
        int i2;
        String[] strArr;
        long countLocked = controllerActivityCounter.getIdleTimeCounter().getCountLocked(i);
        long countLocked2 = controllerActivityCounter.getRxTimeCounter().getCountLocked(i);
        long countLocked3 = controllerActivityCounter.getPowerCounter().getCountLocked(i);
        long jComputeBatteryRealtime = computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, i) / 1000;
        LongCounter[] txTimeCounters = controllerActivityCounter.getTxTimeCounters();
        int i3 = 0;
        long countLocked4 = 0;
        for (int length = txTimeCounters.length; i3 < length; length = length) {
            countLocked4 += txTimeCounters[i3].getCountLocked(i);
            i3++;
        }
        if (str2.equals(WIFI_CONTROLLER_NAME)) {
            long countLocked5 = controllerActivityCounter.getScanTimeCounter().getCountLocked(i);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Scan time:  ");
            formatTimeMs(sb, countLocked5);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked5, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
            long j = jComputeBatteryRealtime - ((countLocked + countLocked2) + countLocked4);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            str3 = " Sleep time:  ";
            sb.append(str3);
            formatTimeMs(sb, j);
            sb.append("(");
            sb.append(formatRatioLocked(j, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        } else {
            str3 = " Sleep time:  ";
        }
        if (str2.equals(CELLULAR_CONTROLLER_NAME)) {
            i2 = i;
            long countLocked6 = controllerActivityCounter.getSleepTimeCounter().getCountLocked(i2);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(str3);
            formatTimeMs(sb, countLocked6);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked6, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        } else {
            i2 = i;
        }
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Idle time:   ");
        formatTimeMs(sb, countLocked);
        sb.append("(");
        sb.append(formatRatioLocked(countLocked, jComputeBatteryRealtime));
        sb.append(")");
        printWriter.println(sb.toString());
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Rx time:     ");
        formatTimeMs(sb, countLocked2);
        sb.append("(");
        sb.append(formatRatioLocked(countLocked2, jComputeBatteryRealtime));
        sb.append(")");
        printWriter.println(sb.toString());
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Tx time:     ");
        byte b = -1;
        if (str2.hashCode() == -851952246 && str2.equals(CELLULAR_CONTROLLER_NAME)) {
            b = 0;
        }
        if (b == 0) {
            strArr = new String[]{"   less than 0dBm: ", "   0dBm to 8dBm: ", "   8dBm to 15dBm: ", "   15dBm to 20dBm: ", "   above 20dBm: "};
        } else {
            strArr = new String[]{"[0]", "[1]", "[2]", "[3]", "[4]"};
        }
        int iMin = Math.min(controllerActivityCounter.getTxTimeCounters().length, strArr.length);
        if (iMin > 1) {
            printWriter.println(sb.toString());
            for (int i4 = 0; i4 < iMin; i4++) {
                long countLocked7 = controllerActivityCounter.getTxTimeCounters()[i4].getCountLocked(i2);
                sb.setLength(0);
                sb.append(str);
                sb.append("    ");
                sb.append(strArr[i4]);
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb, countLocked7);
                sb.append("(");
                sb.append(formatRatioLocked(countLocked7, jComputeBatteryRealtime));
                sb.append(")");
                printWriter.println(sb.toString());
            }
        } else {
            long countLocked8 = controllerActivityCounter.getTxTimeCounters()[0].getCountLocked(i2);
            formatTimeMs(sb, countLocked8);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked8, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        }
        if (countLocked3 > 0) {
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Battery drain: ");
            sb.append(BatteryStatsHelper.makemAh(countLocked3 / 3600000.0d));
            sb.append("mAh");
            printWriter.println(sb.toString());
        }
    }

    public final void dumpCheckinLocked(Context context, PrintWriter printWriter, int i, int i2) {
        dumpCheckinLocked(context, printWriter, i, i2, BatteryStatsHelper.checkWifiOnly(context));
    }

    /* JADX WARN: Code duplicated, block: B:225:0x0bce  */
    /* JADX WARN: Code duplicated, block: B:271:0x0d3e  */
    /* JADX WARN: Code duplicated, block: B:368:0x10e2  */
    /* JADX WARN: Code duplicated, block: B:391:0x1163  */
    public final void dumpCheckinLocked(Context context, PrintWriter printWriter, int i, int i2, boolean z) {
        char c;
        String str;
        long j;
        char c2;
        char c3;
        char c4;
        int i3;
        int i4;
        int i5;
        long[] jArr;
        StringBuilder sb;
        String str2;
        StringBuilder sb2;
        long[] jArr2;
        String str3;
        String str4;
        long j2;
        String str5;
        int i6;
        long[] cpuFreqTimes;
        long j3;
        long j4;
        long totalDurationMsLocked;
        String str6;
        char c5;
        String str7;
        int i7;
        int i8 = i2;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j5 = jElapsedRealtime * 1000;
        long batteryUptime = getBatteryUptime(jUptimeMillis);
        long jComputeBatteryUptime = computeBatteryUptime(jUptimeMillis, i);
        long jComputeBatteryRealtime = computeBatteryRealtime(j5, i);
        long jComputeBatteryScreenOffUptime = computeBatteryScreenOffUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffRealtime = computeBatteryScreenOffRealtime(j5, i);
        long jComputeRealtime = computeRealtime(j5, i);
        long jComputeUptime = computeUptime(jUptimeMillis, i);
        long screenOnTime = getScreenOnTime(j5, i);
        long screenDozeTime = getScreenDozeTime(j5, i);
        long interactiveTime = getInteractiveTime(j5, i);
        long powerSaveModeEnabledTime = getPowerSaveModeEnabledTime(j5, i);
        long deviceIdleModeTime = getDeviceIdleModeTime(1, j5, i);
        long deviceIdleModeTime2 = getDeviceIdleModeTime(2, j5, i);
        long deviceIdlingTime = getDeviceIdlingTime(1, j5, i);
        long deviceIdlingTime2 = getDeviceIdlingTime(2, j5, i);
        int numConnectivityChange = getNumConnectivityChange(i);
        long phoneOnTime = getPhoneOnTime(j5, i);
        long uahDischarge = getUahDischarge(i);
        long uahDischargeScreenOff = getUahDischargeScreenOff(i);
        long uahDischargeScreenDoze = getUahDischargeScreenDoze(i);
        long uahDischargeLightDoze = getUahDischargeLightDoze(i);
        long uahDischargeDeepDoze = getUahDischargeDeepDoze(i);
        StringBuilder sb3 = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        long j6 = jElapsedRealtime;
        int size = uidStats.size();
        String str8 = STAT_NAMES[i];
        StringBuilder sb4 = sb3;
        Object[] objArr = new Object[12];
        objArr[0] = i == 0 ? Integer.valueOf(getStartCount()) : "N/A";
        objArr[1] = Long.valueOf(jComputeBatteryRealtime / 1000);
        objArr[2] = Long.valueOf(jComputeBatteryUptime / 1000);
        objArr[3] = Long.valueOf(jComputeRealtime / 1000);
        objArr[4] = Long.valueOf(jComputeUptime / 1000);
        objArr[5] = Long.valueOf(getStartClockTime());
        objArr[6] = Long.valueOf(jComputeBatteryScreenOffRealtime / 1000);
        objArr[7] = Long.valueOf(jComputeBatteryScreenOffUptime / 1000);
        objArr[8] = Integer.valueOf(getEstimatedBatteryCapacity());
        objArr[9] = Integer.valueOf(getMinLearnedBatteryCapacity());
        objArr[10] = Integer.valueOf(getMaxLearnedBatteryCapacity());
        objArr[11] = Long.valueOf(screenDozeTime / 1000);
        dumpLine(printWriter, 0, str8, BATTERY_DATA, objArr);
        long totalTimeLocked = 0;
        long totalTimeLocked2 = 0;
        for (int i9 = 0; i9 < size; i9++) {
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uidStats.valueAt(i9).getWakelockStats();
            int i10 = 1;
            int size2 = wakelockStats.size() - 1;
            while (size2 >= 0) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size2);
                Timer wakeTime = wakelockValueAt.getWakeTime(i10);
                if (wakeTime != null) {
                    totalTimeLocked += wakeTime.getTotalTimeLocked(j5, i);
                }
                Timer wakeTime2 = wakelockValueAt.getWakeTime(0);
                if (wakeTime2 != null) {
                    totalTimeLocked2 += wakeTime2.getTotalTimeLocked(j5, i);
                }
                size2--;
                i10 = 1;
            }
        }
        dumpLine(printWriter, 0, str8, GLOBAL_NETWORK_DATA, Long.valueOf(getNetworkActivityBytes(0, i)), Long.valueOf(getNetworkActivityBytes(1, i)), Long.valueOf(getNetworkActivityBytes(2, i)), Long.valueOf(getNetworkActivityBytes(3, i)), Long.valueOf(getNetworkActivityPackets(0, i)), Long.valueOf(getNetworkActivityPackets(1, i)), Long.valueOf(getNetworkActivityPackets(2, i)), Long.valueOf(getNetworkActivityPackets(3, i)), Long.valueOf(getNetworkActivityBytes(4, i)), Long.valueOf(getNetworkActivityBytes(5, i)));
        long j7 = batteryUptime;
        dumpControllerActivityLine(printWriter, 0, str8, GLOBAL_MODEM_CONTROLLER_DATA, getModemControllerActivity(), i);
        dumpLine(printWriter, 0, str8, GLOBAL_WIFI_DATA, Long.valueOf(getWifiOnTime(j5, i) / 1000), Long.valueOf(getGlobalWifiRunningTime(j5, i) / 1000), 0, 0, 0);
        dumpControllerActivityLine(printWriter, 0, str8, GLOBAL_WIFI_CONTROLLER_DATA, getWifiControllerActivity(), i);
        dumpControllerActivityLine(printWriter, 0, str8, GLOBAL_BLUETOOTH_CONTROLLER_DATA, getBluetoothControllerActivity(), i);
        char c6 = '\b';
        dumpLine(printWriter, 0, str8, MISC_DATA, Long.valueOf(screenOnTime / 1000), Long.valueOf(phoneOnTime / 1000), Long.valueOf(totalTimeLocked / 1000), Long.valueOf(totalTimeLocked2 / 1000), Long.valueOf(getMobileRadioActiveTime(j5, i) / 1000), Long.valueOf(getMobileRadioActiveAdjustedTime(i) / 1000), Long.valueOf(interactiveTime / 1000), Long.valueOf(powerSaveModeEnabledTime / 1000), Integer.valueOf(numConnectivityChange), Long.valueOf(deviceIdleModeTime2 / 1000), Integer.valueOf(getDeviceIdleModeCount(2, i)), Long.valueOf(deviceIdlingTime2 / 1000), Integer.valueOf(getDeviceIdlingCount(2, i)), Integer.valueOf(getMobileRadioActiveCount(i)), Long.valueOf(getMobileRadioActiveUnknownTime(i) / 1000), Long.valueOf(deviceIdleModeTime / 1000), Integer.valueOf(getDeviceIdleModeCount(1, i)), Long.valueOf(deviceIdlingTime / 1000), Integer.valueOf(getDeviceIdlingCount(1, i)), Long.valueOf(getLongestDeviceIdleModeTime(1)), Long.valueOf(getLongestDeviceIdleModeTime(2)));
        Object[] objArr2 = new Object[5];
        for (int i11 = 0; i11 < 5; i11++) {
            objArr2[i11] = Long.valueOf(getScreenBrightnessTime(i11, j5, i) / 1000);
        }
        dumpLine(printWriter, 0, str8, "br", objArr2);
        Object[] objArr3 = new Object[5];
        int i12 = 0;
        for (int i13 = 5; i12 < i13; i13 = 5) {
            objArr3[i12] = Long.valueOf(getPhoneSignalStrengthTime(i12, j5, i) / 1000);
            i12++;
        }
        dumpLine(printWriter, 0, str8, SIGNAL_STRENGTH_TIME_DATA, objArr3);
        dumpLine(printWriter, 0, str8, SIGNAL_SCANNING_TIME_DATA, Long.valueOf(getPhoneSignalScanningTime(j5, i) / 1000));
        for (int i14 = 0; i14 < 5; i14++) {
            objArr3[i14] = Integer.valueOf(getPhoneSignalStrengthCount(i14, i));
        }
        dumpLine(printWriter, 0, str8, SIGNAL_STRENGTH_COUNT_DATA, objArr3);
        Object[] objArr4 = new Object[21];
        for (int i15 = 0; i15 < 21; i15++) {
            objArr4[i15] = Long.valueOf(getPhoneDataConnectionTime(i15, j5, i) / 1000);
        }
        dumpLine(printWriter, 0, str8, DATA_CONNECTION_TIME_DATA, objArr4);
        for (int i16 = 0; i16 < 21; i16++) {
            objArr4[i16] = Integer.valueOf(getPhoneDataConnectionCount(i16, i));
        }
        dumpLine(printWriter, 0, str8, DATA_CONNECTION_COUNT_DATA, objArr4);
        Object[] objArr5 = new Object[8];
        for (int i17 = 0; i17 < 8; i17++) {
            objArr5[i17] = Long.valueOf(getWifiStateTime(i17, j5, i) / 1000);
        }
        dumpLine(printWriter, 0, str8, WIFI_STATE_TIME_DATA, objArr5);
        for (int i18 = 0; i18 < 8; i18++) {
            objArr5[i18] = Integer.valueOf(getWifiStateCount(i18, i));
        }
        dumpLine(printWriter, 0, str8, WIFI_STATE_COUNT_DATA, objArr5);
        Object[] objArr6 = new Object[13];
        for (int i19 = 0; i19 < 13; i19++) {
            objArr6[i19] = Long.valueOf(getWifiSupplStateTime(i19, j5, i) / 1000);
        }
        dumpLine(printWriter, 0, str8, WIFI_SUPPL_STATE_TIME_DATA, objArr6);
        for (int i20 = 0; i20 < 13; i20++) {
            objArr6[i20] = Integer.valueOf(getWifiSupplStateCount(i20, i));
        }
        dumpLine(printWriter, 0, str8, WIFI_SUPPL_STATE_COUNT_DATA, objArr6);
        Object[] objArr7 = new Object[5];
        for (int i21 = 0; i21 < 5; i21++) {
            objArr7[i21] = Long.valueOf(getWifiSignalStrengthTime(i21, j5, i) / 1000);
        }
        dumpLine(printWriter, 0, str8, WIFI_SIGNAL_STRENGTH_TIME_DATA, objArr7);
        int i22 = 0;
        for (int i23 = 5; i22 < i23; i23 = 5) {
            objArr7[i22] = Integer.valueOf(getWifiSignalStrengthCount(i22, i));
            i22++;
        }
        dumpLine(printWriter, 0, str8, WIFI_SIGNAL_STRENGTH_COUNT_DATA, objArr7);
        dumpLine(printWriter, 0, str8, WIFI_MULTICAST_TOTAL_DATA, Long.valueOf(getWifiMulticastWakelockTime(j5, i) / 1000), Integer.valueOf(getWifiMulticastWakelockCount(i)));
        if (i == 2) {
            dumpLine(printWriter, 0, str8, BATTERY_LEVEL_DATA, Integer.valueOf(getDischargeStartLevel()), Integer.valueOf(getDischargeCurrentLevel()));
        }
        if (i == 2) {
            dumpLine(printWriter, 0, str8, BATTERY_DISCHARGE_DATA, Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()), Long.valueOf(uahDischarge / 1000), Long.valueOf(uahDischargeScreenOff / 1000), Integer.valueOf(getDischargeAmountScreenDoze()), Long.valueOf(uahDischargeScreenDoze / 1000), Long.valueOf(uahDischargeLightDoze / 1000), Long.valueOf(uahDischargeDeepDoze / 1000));
            c = 6;
        } else {
            c = 6;
            dumpLine(printWriter, 0, str8, BATTERY_DISCHARGE_DATA, Integer.valueOf(getLowDischargeAmountSinceCharge()), Integer.valueOf(getHighDischargeAmountSinceCharge()), Integer.valueOf(getDischargeAmountScreenOnSinceCharge()), Integer.valueOf(getDischargeAmountScreenOffSinceCharge()), Long.valueOf(uahDischarge / 1000), Long.valueOf(uahDischargeScreenOff / 1000), Integer.valueOf(getDischargeAmountScreenDozeSinceCharge()), Long.valueOf(uahDischargeScreenDoze / 1000), Long.valueOf(uahDischargeLightDoze / 1000), Long.valueOf(uahDischargeDeepDoze / 1000));
        }
        String str9 = "\"";
        if (i8 < 0) {
            Map<String, ? extends Timer> kernelWakelockStats = getKernelWakelockStats();
            if (kernelWakelockStats.size() > 0) {
                for (Map.Entry<String, ? extends Timer> entry : kernelWakelockStats.entrySet()) {
                    sb4.setLength(0);
                    String str10 = str9;
                    printWakeLockCheckin(sb4, entry.getValue(), j5, null, i, "");
                    dumpLine(printWriter, 0, str8, KERNEL_WAKELOCK_DATA, str10 + entry.getKey() + str10, sb4.toString());
                    c = c;
                    str9 = str10;
                    j5 = j5;
                    c6 = '\b';
                }
            }
            str = str9;
            long j8 = j5;
            c2 = 21;
            Map<String, ? extends Timer> wakeupReasonStats = getWakeupReasonStats();
            if (wakeupReasonStats.size() > 0) {
                for (Iterator<Map.Entry<String, ? extends Timer>> it = wakeupReasonStats.entrySet().iterator(); it.hasNext(); it = it) {
                    Map.Entry<String, ? extends Timer> next = it.next();
                    dumpLine(printWriter, 0, str8, WAKEUP_REASON_DATA, str + next.getKey() + str, Long.valueOf((next.getValue().getTotalTimeLocked(j8, i) + 500) / 1000), Integer.valueOf(next.getValue().getCountLocked(i)));
                }
            }
            j = j8;
        } else {
            str = "\"";
            j = j5;
            c2 = 21;
        }
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        Map<String, ? extends Timer> screenOffRpmStats = getScreenOffRpmStats();
        if (rpmStats.size() > 0) {
            for (Iterator<Map.Entry<String, ? extends Timer>> it2 = rpmStats.entrySet().iterator(); it2.hasNext(); it2 = it2) {
                Map.Entry<String, ? extends Timer> next2 = it2.next();
                StringBuilder sb5 = sb4;
                sb5.setLength(0);
                Timer value = next2.getValue();
                long totalTimeLocked3 = (value.getTotalTimeLocked(j, i) + 500) / 1000;
                int countLocked = value.getCountLocked(i);
                Timer timer = screenOffRpmStats.get(next2.getKey());
                if (timer != null) {
                    long totalTimeLocked4 = (timer.getTotalTimeLocked(j, i) + 500) / 1000;
                }
                if (timer != null) {
                    timer.getCountLocked(i);
                }
                dumpLine(printWriter, 0, str8, RESOURCE_POWER_MANAGER_DATA, str + next2.getKey() + str, Long.valueOf(totalTimeLocked3), Integer.valueOf(countLocked));
                sb4 = sb5;
            }
        }
        StringBuilder sb6 = sb4;
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, z);
        batteryStatsHelper.create(this);
        batteryStatsHelper.refreshStats(i, -1);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null && usageList.size() > 0) {
            dumpLine(printWriter, 0, str8, POWER_USE_SUMMARY_DATA, BatteryStatsHelper.makemAh(batteryStatsHelper.getPowerProfile().getBatteryCapacity()), BatteryStatsHelper.makemAh(batteryStatsHelper.getComputedPower()), BatteryStatsHelper.makemAh(batteryStatsHelper.getMinDrainedPower()), BatteryStatsHelper.makemAh(batteryStatsHelper.getMaxDrainedPower()));
            int i24 = 0;
            int uid = 0;
            while (i24 < usageList.size()) {
                BatterySipper batterySipper = usageList.get(i24);
                switch (AnonymousClass2.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[batterySipper.drainType.ordinal()]) {
                    case 1:
                        str7 = "ambi";
                        break;
                    case 2:
                        str7 = "idle";
                        break;
                    case 3:
                        str7 = "cell";
                        break;
                    case 4:
                        str7 = "phone";
                        break;
                    case 5:
                        str7 = "wifi";
                        break;
                    case 6:
                        str7 = "blue";
                        break;
                    case 7:
                        str7 = "scrn";
                        break;
                    case 8:
                        str7 = "flashlight";
                        break;
                    case 9:
                        uid = batterySipper.uidObj.getUid();
                        str7 = "uid";
                        break;
                    case 10:
                        i7 = 5;
                        uid = UserHandle.getUid(batterySipper.userId, 0);
                        str7 = "user";
                        continue;
                        Object[] objArr8 = new Object[i7];
                        objArr8[0] = str7;
                        objArr8[1] = BatteryStatsHelper.makemAh(batterySipper.totalPowerMah);
                        objArr8[2] = Integer.valueOf(batterySipper.shouldHide ? 1 : 0);
                        objArr8[3] = BatteryStatsHelper.makemAh(batterySipper.screenPowerMah);
                        objArr8[4] = BatteryStatsHelper.makemAh(batterySipper.proportionalSmearMah);
                        dumpLine(printWriter, uid, str8, POWER_USE_ITEM_DATA, objArr8);
                        i24++;
                        usageList = usageList;
                        j = j;
                        break;
                    case 11:
                        str7 = "unacc";
                        break;
                    case 12:
                        str7 = "over";
                        break;
                    case 13:
                        str7 = Context.CAMERA_SERVICE;
                        break;
                    case 14:
                        str7 = "memory";
                        break;
                    default:
                        str7 = "???";
                        break;
                }
                i7 = 5;
                Object[] objArr9 = new Object[i7];
                objArr9[0] = str7;
                objArr9[1] = BatteryStatsHelper.makemAh(batterySipper.totalPowerMah);
                objArr9[2] = Integer.valueOf(batterySipper.shouldHide ? 1 : 0);
                objArr9[3] = BatteryStatsHelper.makemAh(batterySipper.screenPowerMah);
                objArr9[4] = BatteryStatsHelper.makemAh(batterySipper.proportionalSmearMah);
                dumpLine(printWriter, uid, str8, POWER_USE_ITEM_DATA, objArr9);
                i24++;
                usageList = usageList;
                j = j;
            }
        }
        long j9 = j;
        long[] cpuFreqs = getCpuFreqs();
        String str11 = ",";
        if (cpuFreqs != null) {
            sb6.setLength(0);
            int i25 = 0;
            while (i25 < cpuFreqs.length) {
                StringBuilder sb7 = new StringBuilder();
                sb7.append(i25 == 0 ? "" : ",");
                sb7.append(cpuFreqs[i25]);
                sb6.append(sb7.toString());
                i25++;
            }
            dumpLine(printWriter, 0, str8, GLOBAL_CPU_FREQ_DATA, sb6.toString());
        }
        int i26 = 0;
        while (i26 < size) {
            int iKeyAt = uidStats.keyAt(i26);
            if (i8 < 0 || iKeyAt == i8) {
                Uid uidValueAt = uidStats.valueAt(i26);
                long networkActivityBytes = uidValueAt.getNetworkActivityBytes(0, i);
                long networkActivityBytes2 = uidValueAt.getNetworkActivityBytes(1, i);
                long networkActivityBytes3 = uidValueAt.getNetworkActivityBytes(2, i);
                long networkActivityBytes4 = uidValueAt.getNetworkActivityBytes(3, i);
                long networkActivityPackets = uidValueAt.getNetworkActivityPackets(0, i);
                long networkActivityPackets2 = uidValueAt.getNetworkActivityPackets(1, i);
                long mobileRadioActiveTime = uidValueAt.getMobileRadioActiveTime(i);
                int mobileRadioActiveCount = uidValueAt.getMobileRadioActiveCount(i);
                long mobileRadioApWakeupCount = uidValueAt.getMobileRadioApWakeupCount(i);
                long networkActivityPackets3 = uidValueAt.getNetworkActivityPackets(2, i);
                long networkActivityPackets4 = uidValueAt.getNetworkActivityPackets(3, i);
                long wifiRadioApWakeupCount = uidValueAt.getWifiRadioApWakeupCount(i);
                long networkActivityBytes5 = uidValueAt.getNetworkActivityBytes(4, i);
                long networkActivityBytes6 = uidValueAt.getNetworkActivityBytes(5, i);
                long networkActivityBytes7 = uidValueAt.getNetworkActivityBytes(6, i);
                long networkActivityBytes8 = uidValueAt.getNetworkActivityBytes(7, i);
                long networkActivityBytes9 = uidValueAt.getNetworkActivityBytes(8, i);
                long networkActivityBytes10 = uidValueAt.getNetworkActivityBytes(9, i);
                long networkActivityPackets5 = uidValueAt.getNetworkActivityPackets(6, i);
                long networkActivityPackets6 = uidValueAt.getNetworkActivityPackets(7, i);
                long networkActivityPackets7 = uidValueAt.getNetworkActivityPackets(8, i);
                long networkActivityPackets8 = uidValueAt.getNetworkActivityPackets(9, i);
                if (networkActivityBytes > 0 || networkActivityBytes2 > 0 || networkActivityBytes3 > 0 || networkActivityBytes4 > 0 || networkActivityPackets > 0 || networkActivityPackets2 > 0 || networkActivityPackets3 > 0 || networkActivityPackets4 > 0 || mobileRadioActiveTime > 0 || mobileRadioActiveCount > 0 || networkActivityBytes5 > 0 || networkActivityBytes6 > 0 || mobileRadioApWakeupCount > 0 || wifiRadioApWakeupCount > 0 || networkActivityBytes7 > 0 || networkActivityBytes8 > 0 || networkActivityBytes9 > 0 || networkActivityBytes10 > 0 || networkActivityPackets5 > 0 || networkActivityPackets6 > 0 || networkActivityPackets7 > 0 || networkActivityPackets8 > 0) {
                    Object[] objArr10 = new Object[22];
                    objArr10[0] = Long.valueOf(networkActivityBytes);
                    objArr10[1] = Long.valueOf(networkActivityBytes2);
                    objArr10[2] = Long.valueOf(networkActivityBytes3);
                    objArr10[3] = Long.valueOf(networkActivityBytes4);
                    objArr10[4] = Long.valueOf(networkActivityPackets);
                    objArr10[5] = Long.valueOf(networkActivityPackets2);
                    objArr10[6] = Long.valueOf(networkActivityPackets3);
                    objArr10[7] = Long.valueOf(networkActivityPackets4);
                    c3 = '\b';
                    objArr10[8] = Long.valueOf(mobileRadioActiveTime);
                    objArr10[9] = Integer.valueOf(mobileRadioActiveCount);
                    objArr10[10] = Long.valueOf(networkActivityBytes5);
                    objArr10[11] = Long.valueOf(networkActivityBytes6);
                    objArr10[12] = Long.valueOf(mobileRadioApWakeupCount);
                    c4 = '\r';
                    objArr10[13] = Long.valueOf(wifiRadioApWakeupCount);
                    objArr10[14] = Long.valueOf(networkActivityBytes7);
                    objArr10[15] = Long.valueOf(networkActivityBytes8);
                    objArr10[16] = Long.valueOf(networkActivityBytes9);
                    objArr10[17] = Long.valueOf(networkActivityBytes10);
                    objArr10[18] = Long.valueOf(networkActivityPackets5);
                    objArr10[19] = Long.valueOf(networkActivityPackets6);
                    objArr10[20] = Long.valueOf(networkActivityPackets7);
                    objArr10[c2] = Long.valueOf(networkActivityPackets8);
                    dumpLine(printWriter, iKeyAt, str8, NETWORK_DATA, objArr10);
                } else {
                    c4 = '\r';
                    c3 = '\b';
                }
                String str12 = str;
                long j10 = j9;
                dumpControllerActivityLine(printWriter, iKeyAt, str8, MODEM_CONTROLLER_DATA, uidValueAt.getModemControllerActivity(), i);
                long fullWifiLockTime = uidValueAt.getFullWifiLockTime(j10, i);
                long wifiScanTime = uidValueAt.getWifiScanTime(j10, i);
                int wifiScanCount = uidValueAt.getWifiScanCount(i);
                int wifiScanBackgroundCount = uidValueAt.getWifiScanBackgroundCount(i);
                long wifiScanActualTime = (uidValueAt.getWifiScanActualTime(j10) + 500) / 1000;
                long wifiScanBackgroundTime = (uidValueAt.getWifiScanBackgroundTime(j10) + 500) / 1000;
                long wifiRunningTime = uidValueAt.getWifiRunningTime(j10, i);
                if (fullWifiLockTime != 0 || wifiScanTime != 0 || wifiScanCount != 0 || wifiScanBackgroundCount != 0 || wifiScanActualTime != 0 || wifiScanBackgroundTime != 0 || wifiRunningTime != 0) {
                    Object[] objArr11 = new Object[10];
                    objArr11[0] = Long.valueOf(fullWifiLockTime);
                    objArr11[1] = Long.valueOf(wifiScanTime);
                    objArr11[2] = Long.valueOf(wifiRunningTime);
                    objArr11[3] = Integer.valueOf(wifiScanCount);
                    objArr11[4] = 0;
                    objArr11[5] = 0;
                    objArr11[6] = 0;
                    objArr11[7] = Integer.valueOf(wifiScanBackgroundCount);
                    objArr11[c3] = Long.valueOf(wifiScanActualTime);
                    objArr11[9] = Long.valueOf(wifiScanBackgroundTime);
                    dumpLine(printWriter, iKeyAt, str8, WIFI_DATA, objArr11);
                }
                dumpControllerActivityLine(printWriter, iKeyAt, str8, WIFI_CONTROLLER_DATA, uidValueAt.getWifiControllerActivity(), i);
                Timer bluetoothScanTimer = uidValueAt.getBluetoothScanTimer();
                if (bluetoothScanTimer != null) {
                    long totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                    if (totalTimeLocked5 != 0) {
                        int countLocked2 = bluetoothScanTimer.getCountLocked(i);
                        Timer bluetoothScanBackgroundTimer = uidValueAt.getBluetoothScanBackgroundTimer();
                        int countLocked3 = bluetoothScanBackgroundTimer != null ? bluetoothScanBackgroundTimer.getCountLocked(i) : 0;
                        long totalDurationMsLocked2 = bluetoothScanTimer.getTotalDurationMsLocked(j6);
                        long totalDurationMsLocked3 = bluetoothScanBackgroundTimer != null ? bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j6) : 0L;
                        int countLocked4 = uidValueAt.getBluetoothScanResultCounter() != null ? uidValueAt.getBluetoothScanResultCounter().getCountLocked(i) : 0;
                        int countLocked5 = uidValueAt.getBluetoothScanResultBgCounter() != null ? uidValueAt.getBluetoothScanResultBgCounter().getCountLocked(i) : 0;
                        Timer bluetoothUnoptimizedScanTimer = uidValueAt.getBluetoothUnoptimizedScanTimer();
                        long totalDurationMsLocked4 = bluetoothUnoptimizedScanTimer != null ? bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j6) : 0L;
                        long maxDurationMsLocked = bluetoothUnoptimizedScanTimer != null ? bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j6) : 0L;
                        Timer bluetoothUnoptimizedScanBackgroundTimer = uidValueAt.getBluetoothUnoptimizedScanBackgroundTimer();
                        long totalDurationMsLocked5 = bluetoothUnoptimizedScanBackgroundTimer != null ? bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j6) : 0L;
                        long maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer != null ? bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j6) : 0L;
                        Object[] objArr12 = new Object[11];
                        objArr12[0] = Long.valueOf(totalTimeLocked5);
                        objArr12[1] = Integer.valueOf(countLocked2);
                        objArr12[2] = Integer.valueOf(countLocked3);
                        objArr12[3] = Long.valueOf(totalDurationMsLocked2);
                        objArr12[4] = Long.valueOf(totalDurationMsLocked3);
                        objArr12[5] = Integer.valueOf(countLocked4);
                        objArr12[6] = Integer.valueOf(countLocked5);
                        objArr12[7] = Long.valueOf(totalDurationMsLocked4);
                        objArr12[c3] = Long.valueOf(totalDurationMsLocked5);
                        objArr12[9] = Long.valueOf(maxDurationMsLocked);
                        objArr12[10] = Long.valueOf(maxDurationMsLocked2);
                        dumpLine(printWriter, iKeyAt, str8, BLUETOOTH_MISC_DATA, objArr12);
                    } else {
                        sb6 = sb6;
                        j10 = j10;
                    }
                } else {
                    sb6 = sb6;
                    j10 = j10;
                }
                long j11 = j6;
                dumpControllerActivityLine(printWriter, iKeyAt, str8, BLUETOOTH_CONTROLLER_DATA, uidValueAt.getBluetoothControllerActivity(), i);
                if (uidValueAt.hasUserActivity()) {
                    Object[] objArr13 = new Object[4];
                    int i27 = 0;
                    boolean z2 = false;
                    for (int i28 = 4; i27 < i28; i28 = 4) {
                        int userActivityCount = uidValueAt.getUserActivityCount(i27, i);
                        objArr13[i27] = Integer.valueOf(userActivityCount);
                        if (userActivityCount != 0) {
                            z2 = true;
                        }
                        i27++;
                    }
                    if (z2) {
                        dumpLine(printWriter, iKeyAt, str8, USER_ACTIVITY_DATA, objArr13);
                    }
                }
                if (uidValueAt.getAggregatedPartialWakelockTimer() != null) {
                    Timer aggregatedPartialWakelockTimer = uidValueAt.getAggregatedPartialWakelockTimer();
                    long totalDurationMsLocked6 = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j11);
                    Timer subTimer = aggregatedPartialWakelockTimer.getSubTimer();
                    i3 = 1;
                    dumpLine(printWriter, iKeyAt, str8, AGGREGATED_WAKELOCK_DATA, Long.valueOf(totalDurationMsLocked6), Long.valueOf(subTimer != null ? subTimer.getTotalDurationMsLocked(j11) : 0L));
                } else {
                    i3 = 1;
                }
                ArrayMap<String, ? extends Uid.Wakelock> wakelockStats2 = uidValueAt.getWakelockStats();
                int size3 = wakelockStats2.size() - i3;
                while (size3 >= 0) {
                    Uid.Wakelock wakelockValueAt2 = wakelockStats2.valueAt(size3);
                    StringBuilder sb8 = sb6;
                    sb8.setLength(0);
                    long j12 = j10;
                    long j13 = j11;
                    String str13 = str11;
                    int i29 = size3;
                    String strPrintWakeLockCheckin = printWakeLockCheckin(sb8, wakelockValueAt2.getWakeTime(i3), j12, FullBackup.FILES_TREE_TOKEN, i, "");
                    Timer wakeTime3 = wakelockValueAt2.getWakeTime(0);
                    String strPrintWakeLockCheckin2 = printWakeLockCheckin(sb6, wakeTime3, j12, TtmlUtils.TAG_P, i, strPrintWakeLockCheckin);
                    long j14 = j10;
                    printWakeLockCheckin(sb6, wakelockValueAt2.getWakeTime(2), j14, "w", i, printWakeLockCheckin(sb6, wakeTime3 != null ? wakeTime3.getSubTimer() : null, j14, "bp", i, strPrintWakeLockCheckin2));
                    if (sb6.length() > 0) {
                        String strKeyAt = wakelockStats2.keyAt(i29);
                        if (strKeyAt.indexOf(44) >= 0) {
                            c5 = '_';
                            strKeyAt = strKeyAt.replace(',', '_');
                        } else {
                            c5 = '_';
                        }
                        if (strKeyAt.indexOf(10) >= 0) {
                            strKeyAt = strKeyAt.replace('\n', c5);
                        }
                        if (strKeyAt.indexOf(13) >= 0) {
                            strKeyAt = strKeyAt.replace('\r', c5);
                        }
                        dumpLine(printWriter, iKeyAt, str8, WAKELOCK_DATA, strKeyAt, sb6.toString());
                    }
                    size3 = i29 - 1;
                    str11 = str13;
                    j11 = j13;
                    i3 = 1;
                }
                long j15 = j11;
                String str14 = str11;
                Timer multicastWakelockStats = uidValueAt.getMulticastWakelockStats();
                long j16 = j10;
                if (multicastWakelockStats != null) {
                    long totalTimeLocked6 = multicastWakelockStats.getTotalTimeLocked(j16, i) / 1000;
                    int countLocked6 = multicastWakelockStats.getCountLocked(i);
                    if (totalTimeLocked6 > 0) {
                        i4 = 1;
                        dumpLine(printWriter, iKeyAt, str8, WIFI_MULTICAST_DATA, Long.valueOf(totalTimeLocked6), Integer.valueOf(countLocked6));
                    } else {
                        i4 = 1;
                    }
                } else {
                    i4 = 1;
                }
                ArrayMap<String, ? extends Timer> syncStats = uidValueAt.getSyncStats();
                int size4 = syncStats.size() - i4;
                while (size4 >= 0) {
                    Timer timerValueAt = syncStats.valueAt(size4);
                    long totalTimeLocked7 = (timerValueAt.getTotalTimeLocked(j16, i) + 500) / 1000;
                    int countLocked7 = timerValueAt.getCountLocked(i);
                    Timer subTimer2 = timerValueAt.getSubTimer();
                    if (subTimer2 != null) {
                        j4 = j15;
                        totalDurationMsLocked = subTimer2.getTotalDurationMsLocked(j4);
                    } else {
                        j4 = j15;
                        totalDurationMsLocked = -1;
                    }
                    int countLocked8 = subTimer2 != null ? subTimer2.getCountLocked(i) : -1;
                    if (totalTimeLocked7 != 0) {
                        StringBuilder sb9 = new StringBuilder();
                        str6 = str12;
                        sb9.append(str6);
                        sb9.append(syncStats.keyAt(size4));
                        sb9.append(str6);
                        dumpLine(printWriter, iKeyAt, str8, SYNC_DATA, sb9.toString(), Long.valueOf(totalTimeLocked7), Integer.valueOf(countLocked7), Long.valueOf(totalDurationMsLocked), Integer.valueOf(countLocked8));
                    } else {
                        str6 = str12;
                    }
                    size4--;
                    str12 = str6;
                    j15 = j4;
                    syncStats = syncStats;
                    str14 = str14;
                }
                String str15 = str14;
                String str16 = str12;
                long j17 = j15;
                ArrayMap<String, ? extends Timer> jobStats = uidValueAt.getJobStats();
                int size5 = jobStats.size() - 1;
                while (size5 >= 0) {
                    Timer timerValueAt2 = jobStats.valueAt(size5);
                    long totalTimeLocked8 = (timerValueAt2.getTotalTimeLocked(j16, i) + 500) / 1000;
                    int countLocked9 = timerValueAt2.getCountLocked(i);
                    Timer subTimer3 = timerValueAt2.getSubTimer();
                    long totalDurationMsLocked7 = subTimer3 != null ? subTimer3.getTotalDurationMsLocked(j17) : -1L;
                    int countLocked10 = subTimer3 != null ? subTimer3.getCountLocked(i) : -1;
                    if (totalTimeLocked8 != 0) {
                        dumpLine(printWriter, iKeyAt, str8, JOB_DATA, str16 + jobStats.keyAt(size5) + str16, Long.valueOf(totalTimeLocked8), Integer.valueOf(countLocked9), Long.valueOf(totalDurationMsLocked7), Integer.valueOf(countLocked10));
                    }
                    size5--;
                    jobStats = jobStats;
                    j16 = j16;
                }
                long j18 = j16;
                ArrayMap<String, SparseIntArray> jobCompletionStats = uidValueAt.getJobCompletionStats();
                for (int size6 = jobCompletionStats.size() - 1; size6 >= 0; size6--) {
                    SparseIntArray sparseIntArrayValueAt = jobCompletionStats.valueAt(size6);
                    if (sparseIntArrayValueAt != null) {
                        dumpLine(printWriter, iKeyAt, str8, JOB_COMPLETION_DATA, str16 + jobCompletionStats.keyAt(size6) + str16, Integer.valueOf(sparseIntArrayValueAt.get(0, 0)), Integer.valueOf(sparseIntArrayValueAt.get(1, 0)), Integer.valueOf(sparseIntArrayValueAt.get(2, 0)), Integer.valueOf(sparseIntArrayValueAt.get(3, 0)), Integer.valueOf(sparseIntArrayValueAt.get(4, 0)));
                    }
                }
                StringBuilder sb10 = sb6;
                uidValueAt.getDeferredJobsCheckinLineLocked(sb10, i);
                if (sb10.length() > 0) {
                    dumpLine(printWriter, iKeyAt, str8, JOBS_DEFERRED_DATA, sb10.toString());
                }
                long j19 = j18;
                String str17 = str16;
                dumpTimer(printWriter, iKeyAt, str8, FLASHLIGHT_DATA, uidValueAt.getFlashlightTurnedOnTimer(), j19, i);
                dumpTimer(printWriter, iKeyAt, str8, CAMERA_DATA, uidValueAt.getCameraTurnedOnTimer(), j19, i);
                dumpTimer(printWriter, iKeyAt, str8, VIDEO_DATA, uidValueAt.getVideoTurnedOnTimer(), j19, i);
                dumpTimer(printWriter, iKeyAt, str8, AUDIO_DATA, uidValueAt.getAudioTurnedOnTimer(), j19, i);
                SparseArray<? extends Uid.Sensor> sensorStats = uidValueAt.getSensorStats();
                int size7 = sensorStats.size();
                int i30 = 0;
                while (i30 < size7) {
                    Uid.Sensor sensorValueAt = sensorStats.valueAt(i30);
                    int iKeyAt2 = sensorStats.keyAt(i30);
                    Timer sensorTime = sensorValueAt.getSensorTime();
                    long j20 = j19;
                    if (sensorTime != null) {
                        long totalTimeLocked9 = (sensorTime.getTotalTimeLocked(j20, i) + 500) / 1000;
                        if (totalTimeLocked9 != 0) {
                            int countLocked11 = sensorTime.getCountLocked(i);
                            Timer sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            dumpLine(printWriter, iKeyAt, str8, SENSOR_DATA, Integer.valueOf(iKeyAt2), Long.valueOf(totalTimeLocked9), Integer.valueOf(countLocked11), Integer.valueOf(sensorBackgroundTime != null ? sensorBackgroundTime.getCountLocked(i) : 0), Long.valueOf(sensorTime.getTotalDurationMsLocked(j17)), Long.valueOf(sensorBackgroundTime != null ? sensorBackgroundTime.getTotalDurationMsLocked(j17) : 0L));
                        }
                    }
                    i30++;
                    j19 = j20;
                }
                long j21 = j19;
                dumpTimer(printWriter, iKeyAt, str8, VIBRATOR_DATA, uidValueAt.getVibratorOnTimer(), j21, i);
                dumpTimer(printWriter, iKeyAt, str8, FOREGROUND_ACTIVITY_DATA, uidValueAt.getForegroundActivityTimer(), j21, i);
                dumpTimer(printWriter, iKeyAt, str8, FOREGROUND_SERVICE_DATA, uidValueAt.getForegroundServiceTimer(), j21, i);
                Object[] objArr14 = new Object[7];
                long j22 = 0;
                int i31 = 0;
                for (int i32 = 7; i31 < i32; i32 = 7) {
                    long processStateTime = uidValueAt.getProcessStateTime(i31, j21, i);
                    j22 += processStateTime;
                    objArr14[i31] = Long.valueOf((processStateTime + 500) / 1000);
                    i31++;
                }
                long j23 = j21;
                if (j22 > 0) {
                    dumpLine(printWriter, iKeyAt, str8, "st", objArr14);
                }
                long userCpuTimeUs = uidValueAt.getUserCpuTimeUs(i);
                long systemCpuTimeUs = uidValueAt.getSystemCpuTimeUs(i);
                if (userCpuTimeUs > 0 || systemCpuTimeUs > 0) {
                    i5 = 0;
                    dumpLine(printWriter, iKeyAt, str8, CPU_DATA, Long.valueOf(userCpuTimeUs / 1000), Long.valueOf(systemCpuTimeUs / 1000), 0);
                } else {
                    i5 = 0;
                }
                if (cpuFreqs != 0) {
                    long[] cpuFreqTimes2 = uidValueAt.getCpuFreqTimes(i);
                    if (cpuFreqTimes2 != null) {
                        jArr = cpuFreqs;
                        if (cpuFreqTimes2.length == jArr.length) {
                            sb = sb10;
                            sb.setLength(i5);
                            int i33 = 0;
                            while (i33 < cpuFreqTimes2.length) {
                                StringBuilder sb11 = new StringBuilder();
                                sb11.append(i33 == 0 ? "" : str15);
                                sb11.append(cpuFreqTimes2[i33]);
                                sb.append(sb11.toString());
                                i33++;
                                j23 = j23;
                            }
                            j9 = j23;
                            long[] screenOffCpuFreqTimes = uidValueAt.getScreenOffCpuFreqTimes(i);
                            if (screenOffCpuFreqTimes != null) {
                                int i34 = 0;
                                while (i34 < screenOffCpuFreqTimes.length) {
                                    StringBuilder sb12 = new StringBuilder();
                                    String str18 = str15;
                                    sb12.append(str18);
                                    sb12.append(screenOffCpuFreqTimes[i34]);
                                    sb.append(sb12.toString());
                                    i34++;
                                    cpuFreqTimes2 = cpuFreqTimes2;
                                    str15 = str18;
                                }
                                str2 = str15;
                            } else {
                                str2 = str15;
                                for (int i35 = 0; i35 < cpuFreqTimes2.length; i35++) {
                                    sb.append(",0");
                                }
                            }
                            dumpLine(printWriter, iKeyAt, str8, CPU_TIMES_AT_FREQ_DATA, UID_TIMES_TYPE_ALL, Integer.valueOf(cpuFreqTimes2.length), sb.toString());
                        }
                        i6 = 0;
                        while (i6 < 7) {
                            cpuFreqTimes = uidValueAt.getCpuFreqTimes(i, i6);
                            if (cpuFreqTimes == null && cpuFreqTimes.length == jArr.length) {
                                sb.setLength(0);
                                int i36 = 0;
                                while (i36 < cpuFreqTimes.length) {
                                    StringBuilder sb13 = new StringBuilder();
                                    sb13.append(i36 == 0 ? "" : str2);
                                    sb13.append(cpuFreqTimes[i36]);
                                    sb.append(sb13.toString());
                                    i36++;
                                    j17 = j17;
                                }
                                j3 = j17;
                                long[] screenOffCpuFreqTimes2 = uidValueAt.getScreenOffCpuFreqTimes(i, i6);
                                if (screenOffCpuFreqTimes2 != null) {
                                    for (long j24 : screenOffCpuFreqTimes2) {
                                        sb.append(str2 + j24);
                                    }
                                } else {
                                    for (int i37 = 0; i37 < cpuFreqTimes.length; i37++) {
                                        sb.append(",0");
                                    }
                                }
                                dumpLine(printWriter, iKeyAt, str8, CPU_TIMES_AT_FREQ_DATA, Uid.UID_PROCESS_TYPES[i6], Integer.valueOf(cpuFreqTimes.length), sb.toString());
                            } else {
                                j3 = j17;
                            }
                            i6++;
                            j17 = j3;
                        }
                        j6 = j17;
                    } else {
                        jArr = cpuFreqs;
                    }
                    j9 = j23;
                    sb = sb10;
                    str2 = str15;
                    i6 = 0;
                    while (i6 < 7) {
                        cpuFreqTimes = uidValueAt.getCpuFreqTimes(i, i6);
                        if (cpuFreqTimes == null) {
                            j3 = j17;
                        } else {
                            j3 = j17;
                        }
                        i6++;
                        j17 = j3;
                    }
                    j6 = j17;
                } else {
                    jArr = cpuFreqs;
                    j9 = j23;
                    j6 = j17;
                    sb = sb10;
                    str2 = str15;
                }
                ArrayMap<String, ? extends Uid.Proc> processStats = uidValueAt.getProcessStats();
                int size8 = processStats.size() - 1;
                while (size8 >= 0) {
                    Uid.Proc procValueAt = processStats.valueAt(size8);
                    long userTime = procValueAt.getUserTime(i);
                    long systemTime = procValueAt.getSystemTime(i);
                    long foregroundTime = procValueAt.getForegroundTime(i);
                    int starts = procValueAt.getStarts(i);
                    int numCrashes = procValueAt.getNumCrashes(i);
                    int numAnrs = procValueAt.getNumAnrs(i);
                    if (userTime == 0 && systemTime == 0 && foregroundTime == 0 && starts == 0 && numAnrs == 0 && numCrashes == 0) {
                        str5 = str17;
                    } else {
                        StringBuilder sb14 = new StringBuilder();
                        str5 = str17;
                        sb14.append(str5);
                        sb14.append(processStats.keyAt(size8));
                        sb14.append(str5);
                        dumpLine(printWriter, iKeyAt, str8, PROCESS_DATA, sb14.toString(), Long.valueOf(userTime), Long.valueOf(systemTime), Long.valueOf(foregroundTime), Integer.valueOf(starts), Integer.valueOf(numAnrs), Integer.valueOf(numCrashes));
                    }
                    size8--;
                    jArr = jArr;
                    str17 = str5;
                    processStats = processStats;
                    sb = sb;
                    str2 = str2;
                }
                sb2 = sb;
                jArr2 = jArr;
                str3 = str2;
                str4 = str17;
                ArrayMap<String, ? extends Uid.Pkg> packageStats = uidValueAt.getPackageStats();
                int i38 = 1;
                int size9 = packageStats.size() - 1;
                while (size9 >= 0) {
                    Uid.Pkg pkgValueAt = packageStats.valueAt(size9);
                    ArrayMap<String, ? extends Counter> wakeupAlarmStats = pkgValueAt.getWakeupAlarmStats();
                    int i39 = 0;
                    for (int size10 = wakeupAlarmStats.size() - i38; size10 >= 0; size10--) {
                        int countLocked12 = wakeupAlarmStats.valueAt(size10).getCountLocked(i);
                        i39 += countLocked12;
                        dumpLine(printWriter, iKeyAt, str8, WAKEUP_ALARM_DATA, wakeupAlarmStats.keyAt(size10).replace(',', '_'), Integer.valueOf(countLocked12));
                    }
                    ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats = pkgValueAt.getServiceStats();
                    int size11 = serviceStats.size() - 1;
                    while (size11 >= 0) {
                        Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size11);
                        long j25 = j7;
                        long startTime = servValueAt.getStartTime(j25, i);
                        int starts2 = servValueAt.getStarts(i);
                        int launches = servValueAt.getLaunches(i);
                        if (startTime != 0 || starts2 != 0 || launches != 0) {
                            dumpLine(printWriter, iKeyAt, str8, APK_DATA, Integer.valueOf(i39), packageStats.keyAt(size9), serviceStats.keyAt(size11), Long.valueOf(startTime / 1000), Integer.valueOf(starts2), Integer.valueOf(launches));
                        }
                        size11--;
                        j7 = j25;
                    }
                    size9--;
                    i38 = 1;
                }
                j2 = j7;
            } else {
                jArr2 = cpuFreqs;
                str3 = str11;
                sb2 = sb6;
                str4 = str;
                j2 = j7;
            }
            i8 = i2;
            i26++;
            j7 = j2;
            uidStats = uidStats;
            size = size;
            sb6 = sb2;
            cpuFreqs = jArr2;
            str = str4;
            str11 = str3;
        }
    }

    /* JADX INFO: renamed from: android.os.BatteryStats$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$os$BatterySipper$DrainType;

        static {
            int[] iArr = new int[BatterySipper.DrainType.values().length];
            $SwitchMap$com$android$internal$os$BatterySipper$DrainType = iArr;
            try {
                iArr[BatterySipper.DrainType.AMBIENT_DISPLAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.IDLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.CELL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.PHONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.WIFI.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.BLUETOOTH.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.SCREEN.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.FLASHLIGHT.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.APP.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.USER.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.UNACCOUNTED.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.OVERCOUNTED.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.CAMERA.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[BatterySipper.DrainType.MEMORY.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
        }
    }

    static final class TimerEntry {
        final int mId;
        final String mName;
        final long mTime;
        final Timer mTimer;

        TimerEntry(String str, int i, Timer timer, long j) {
            this.mName = str;
            this.mId = i;
            this.mTimer = timer;
            this.mTime = j;
        }
    }

    private void printmAh(PrintWriter printWriter, double d) {
        printWriter.print(BatteryStatsHelper.makemAh(d));
    }

    private void printmAh(StringBuilder sb, double d) {
        sb.append(BatteryStatsHelper.makemAh(d));
    }

    public final void dumpLocked(Context context, PrintWriter printWriter, String str, int i, int i2) {
        dumpLocked(context, printWriter, str, i, i2, BatteryStatsHelper.checkWifiOnly(context));
    }

    /* JADX WARN: Code duplicated, block: B:278:0x100b  */
    /* JADX WARN: Code duplicated, block: B:281:0x101f A[LOOP:13: B:279:0x1019->B:281:0x101f, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:283:0x1073  */
    /* JADX WARN: Code duplicated, block: B:286:0x107f  */
    /* JADX WARN: Code duplicated, block: B:289:0x109a A[LOOP:14: B:287:0x1094->B:289:0x109a, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:293:0x10ce A[LOOP:15: B:291:0x10c8->B:293:0x10ce, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:379:0x1493  */
    /* JADX WARN: Code duplicated, block: B:382:0x155b  */
    /* JADX WARN: Code duplicated, block: B:387:0x15a0  */
    /* JADX WARN: Code duplicated, block: B:390:0x15ce  */
    /* JADX WARN: Code duplicated, block: B:392:0x15dc  */
    /* JADX WARN: Code duplicated, block: B:394:0x15e6  */
    /* JADX WARN: Code duplicated, block: B:395:0x15f3  */
    /* JADX WARN: Code duplicated, block: B:398:0x1600  */
    /* JADX WARN: Code duplicated, block: B:399:0x160b  */
    /* JADX WARN: Code duplicated, block: B:402:0x1617  */
    /* JADX WARN: Code duplicated, block: B:403:0x1622  */
    /* JADX WARN: Code duplicated, block: B:406:0x162b  */
    /* JADX WARN: Code duplicated, block: B:407:0x1636  */
    /* JADX WARN: Code duplicated, block: B:410:0x163f  */
    /* JADX WARN: Code duplicated, block: B:411:0x1646  */
    /* JADX WARN: Code duplicated, block: B:413:0x164a  */
    /* JADX WARN: Code duplicated, block: B:414:0x1653  */
    /* JADX WARN: Code duplicated, block: B:417:0x165d  */
    /* JADX WARN: Code duplicated, block: B:418:0x1664  */
    /* JADX WARN: Code duplicated, block: B:420:0x1668  */
    /* JADX WARN: Code duplicated, block: B:421:0x166f  */
    /* JADX WARN: Code duplicated, block: B:424:0x167b  */
    /* JADX WARN: Code duplicated, block: B:426:0x1697  */
    /* JADX WARN: Code duplicated, block: B:428:0x16a0  */
    /* JADX WARN: Code duplicated, block: B:431:0x16bc  */
    /* JADX WARN: Code duplicated, block: B:435:0x16ca  */
    /* JADX WARN: Code duplicated, block: B:437:0x16e2  */
    /* JADX WARN: Code duplicated, block: B:443:0x170e  */
    /* JADX WARN: Code duplicated, block: B:446:0x1715  */
    /* JADX WARN: Code duplicated, block: B:447:0x1718  */
    /* JADX WARN: Code duplicated, block: B:458:0x176b  */
    /* JADX WARN: Code duplicated, block: B:460:0x177b  */
    /* JADX WARN: Code duplicated, block: B:463:0x178e  */
    /* JADX WARN: Code duplicated, block: B:466:0x1793  */
    /* JADX WARN: Code duplicated, block: B:468:0x179d A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:469:0x179f  */
    /* JADX WARN: Code duplicated, block: B:470:0x17aa  */
    /* JADX WARN: Code duplicated, block: B:472:0x17bd  */
    /* JADX WARN: Code duplicated, block: B:476:0x17ce  */
    /* JADX WARN: Code duplicated, block: B:477:0x17d6  */
    /* JADX WARN: Code duplicated, block: B:480:0x17fa  */
    /* JADX WARN: Code duplicated, block: B:482:0x184c  */
    /* JADX WARN: Code duplicated, block: B:483:0x1851  */
    /* JADX WARN: Code duplicated, block: B:487:0x18e6  */
    /* JADX WARN: Code duplicated, block: B:489:0x18ec  */
    /* JADX WARN: Code duplicated, block: B:491:0x18fc  */
    /* JADX WARN: Code duplicated, block: B:492:0x1901  */
    /* JADX WARN: Code duplicated, block: B:494:0x190c  */
    /* JADX WARN: Code duplicated, block: B:509:0x193e  */
    /* JADX WARN: Code duplicated, block: B:510:0x1948  */
    /* JADX WARN: Code duplicated, block: B:513:0x194f A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:514:0x1951  */
    /* JADX WARN: Code duplicated, block: B:517:0x195f A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:518:0x1961  */
    /* JADX WARN: Code duplicated, block: B:522:0x1971 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:523:0x1973  */
    /* JADX WARN: Code duplicated, block: B:527:0x1983 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:528:0x1985  */
    /* JADX WARN: Code duplicated, block: B:532:0x1996 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:533:0x1998  */
    /* JADX WARN: Code duplicated, block: B:536:0x19b2  */
    /* JADX WARN: Code duplicated, block: B:540:0x19be  */
    /* JADX WARN: Code duplicated, block: B:542:0x19ce  */
    /* JADX WARN: Code duplicated, block: B:543:0x19f8  */
    /* JADX WARN: Code duplicated, block: B:544:0x19fb  */
    /* JADX WARN: Code duplicated, block: B:547:0x1a0f  */
    /* JADX WARN: Code duplicated, block: B:549:0x1a27  */
    /* JADX WARN: Code duplicated, block: B:550:0x1a2e  */
    /* JADX WARN: Code duplicated, block: B:553:0x1a36  */
    /* JADX WARN: Code duplicated, block: B:554:0x1a3b  */
    /* JADX WARN: Code duplicated, block: B:557:0x1a5b  */
    /* JADX WARN: Code duplicated, block: B:559:0x1a70  */
    /* JADX WARN: Code duplicated, block: B:560:0x1a82  */
    /* JADX WARN: Code duplicated, block: B:564:0x1aad  */
    /* JADX WARN: Code duplicated, block: B:566:0x1ac7  */
    /* JADX WARN: Code duplicated, block: B:567:0x1acc  */
    /* JADX WARN: Code duplicated, block: B:570:0x1ad4  */
    /* JADX WARN: Code duplicated, block: B:571:0x1ad9  */
    /* JADX WARN: Code duplicated, block: B:574:0x1af7  */
    /* JADX WARN: Code duplicated, block: B:576:0x1b0a  */
    /* JADX WARN: Code duplicated, block: B:577:0x1b1c  */
    /* JADX WARN: Code duplicated, block: B:581:0x1b42  */
    /* JADX WARN: Code duplicated, block: B:583:0x1b4a  */
    /* JADX WARN: Code duplicated, block: B:586:0x1b67 A[LOOP:25: B:584:0x1b61->B:586:0x1b67, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:588:0x1b94  */
    /* JADX WARN: Code duplicated, block: B:592:0x1bae  */
    /* JADX WARN: Code duplicated, block: B:595:0x1c22  */
    /* JADX WARN: Code duplicated, block: B:597:0x1c3f  */
    /* JADX WARN: Code duplicated, block: B:598:0x1c45  */
    /* JADX WARN: Code duplicated, block: B:601:0x1c51  */
    /* JADX WARN: Code duplicated, block: B:603:0x1c67  */
    /* JADX WARN: Code duplicated, block: B:604:0x1c72  */
    /* JADX WARN: Code duplicated, block: B:607:0x1c7d  */
    /* JADX WARN: Code duplicated, block: B:608:0x1c82  */
    /* JADX WARN: Code duplicated, block: B:611:0x1c88  */
    /* JADX WARN: Code duplicated, block: B:613:0x1c8c  */
    /* JADX WARN: Code duplicated, block: B:616:0x1ca9 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:619:0x1caf  */
    /* JADX WARN: Code duplicated, block: B:620:0x1cc3  */
    /* JADX WARN: Code duplicated, block: B:621:0x1ccd  */
    /* JADX WARN: Code duplicated, block: B:626:0x1d3a  */
    /* JADX WARN: Code duplicated, block: B:628:0x1d44  */
    /* JADX WARN: Code duplicated, block: B:629:0x1d73  */
    /* JADX WARN: Code duplicated, block: B:633:0x1d88  */
    /* JADX WARN: Code duplicated, block: B:636:0x1dae  */
    /* JADX WARN: Code duplicated, block: B:638:0x1db2  */
    /* JADX WARN: Code duplicated, block: B:641:0x1ddb  */
    /* JADX WARN: Code duplicated, block: B:644:0x1de8 A[LOOP:28: B:642:0x1de5->B:644:0x1de8, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:646:0x1e0b  */
    /* JADX WARN: Code duplicated, block: B:649:0x1e13  */
    /* JADX WARN: Code duplicated, block: B:652:0x1e20 A[LOOP:29: B:650:0x1e1d->B:652:0x1e20, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:657:0x1e42  */
    /* JADX WARN: Code duplicated, block: B:659:0x1e48  */
    /* JADX WARN: Code duplicated, block: B:662:0x1e6d A[LOOP:31: B:660:0x1e6a->B:662:0x1e6d, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:664:0x1e92  */
    /* JADX WARN: Code duplicated, block: B:667:0x1e9a  */
    /* JADX WARN: Code duplicated, block: B:670:0x1ebf A[LOOP:32: B:668:0x1ebc->B:670:0x1ebf, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:675:0x1ef1  */
    /* JADX WARN: Code duplicated, block: B:677:0x1f19  */
    /* JADX WARN: Code duplicated, block: B:678:0x1f20  */
    /* JADX WARN: Code duplicated, block: B:681:0x1f25  */
    /* JADX WARN: Code duplicated, block: B:691:0x1f3f  */
    /* JADX WARN: Code duplicated, block: B:693:0x1f7e A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:697:0x1f86  */
    /* JADX WARN: Code duplicated, block: B:699:0x1f95  */
    /* JADX WARN: Code duplicated, block: B:700:0x1fa0  */
    /* JADX WARN: Code duplicated, block: B:702:0x1fa3 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:703:0x1fa5  */
    /* JADX WARN: Code duplicated, block: B:705:0x1fb3  */
    /* JADX WARN: Code duplicated, block: B:707:0x1fb6 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:708:0x1fb8  */
    /* JADX WARN: Code duplicated, block: B:712:0x1fcf  */
    /* JADX WARN: Code duplicated, block: B:714:0x1fd7  */
    /* JADX WARN: Code duplicated, block: B:716:0x1fe4  */
    /* JADX WARN: Code duplicated, block: B:717:0x1fea  */
    /* JADX WARN: Code duplicated, block: B:720:0x200a  */
    /* JADX WARN: Code duplicated, block: B:721:0x2025  */
    /* JADX WARN: Code duplicated, block: B:722:0x202c  */
    /* JADX WARN: Code duplicated, block: B:728:0x2069  */
    /* JADX WARN: Code duplicated, block: B:730:0x2092 A[LOOP:36: B:729:0x2090->B:730:0x2092, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:733:0x20d1  */
    /* JADX WARN: Code duplicated, block: B:735:0x20e9 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:739:0x20f1  */
    /* JADX WARN: Code duplicated, block: B:743:0x2143  */
    /* JADX WARN: Code duplicated, block: B:747:0x215b  */
    /* JADX WARN: Code duplicated, block: B:792:0x2163 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:801:0x1a89 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:805:0x1b21 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:823:0x1edd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:835:0x214b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:88:0x05c7  */
    /* JADX WARN: Instruction removed from duplicated block: B:644:0x1de8, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:652:0x1e20, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:659:0x1e48, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:662:0x1e6d, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:667:0x1e9a, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:670:0x1ebf, please report this as an issue */
    public final void dumpLocked(Context context, PrintWriter printWriter, String str, int i, int i2, boolean z) {
        long j;
        long j2;
        String str2;
        String str3;
        String str4;
        long j3;
        long j4;
        String str5;
        String str6;
        long j5;
        String str7;
        String str8;
        String str9;
        long j6;
        long j7;
        String str10;
        String str11;
        int i3;
        int i4;
        long j8;
        StringBuilder sb;
        String str12;
        String str13;
        String str14;
        String str15;
        int i5;
        PrintWriter printWriter2;
        int i6;
        PrintWriter printWriter3;
        BatteryStats batteryStats;
        long j9;
        String str16;
        String str17;
        String str18;
        int i7;
        int i8;
        long j10;
        String str19;
        long j11;
        String str20;
        long j12;
        Timer bluetoothScanTimer;
        String str21;
        String str22;
        String str23;
        String str24;
        boolean z2;
        int i9;
        Uid uid;
        String str25;
        ArrayMap<String, ? extends Uid.Wakelock> wakelockStats;
        String str26;
        String str27;
        long jComputeWakeLock;
        long jComputeWakeLock2;
        long jComputeWakeLock3;
        long jComputeWakeLock4;
        int i10;
        int size;
        boolean z3;
        String str28;
        String str29;
        long j13;
        long j14;
        long j15;
        long j16;
        Timer multicastWakelockStats;
        String str30;
        int i11;
        long j17;
        ArrayMap<String, ? extends Timer> syncStats;
        int size2;
        boolean z4;
        String str31;
        long j18;
        String str32;
        ArrayMap<String, ? extends Timer> jobStats;
        int size3;
        boolean z5;
        long j19;
        ArrayMap<String, SparseIntArray> jobCompletionStats;
        int size4;
        Uid uid2;
        StringBuilder sb2;
        long j20;
        long j21;
        String str33;
        String str34;
        SparseArray<? extends Uid> sparseArray;
        String str35;
        String str36;
        String str37;
        String str38;
        String str39;
        SparseArray<? extends Uid.Sensor> sensorStats;
        int size5;
        boolean z6;
        int i12;
        String str40;
        String str41;
        PrintWriter printWriter4;
        boolean zPrintTimer;
        long j22;
        int i13;
        long j23;
        long userCpuTimeUs;
        long systemCpuTimeUs;
        long[] cpuFreqTimes;
        long[] screenOffCpuFreqTimes;
        int i14;
        ArrayMap<String, ? extends Uid.Proc> processStats;
        int size6;
        String str42;
        long j24;
        String str43;
        ArrayMap<String, ? extends Uid.Pkg> packageStats;
        int size7;
        boolean z7;
        String str44;
        int i15;
        ArrayMap<String, ? extends Counter> wakeupAlarmStats;
        int size8;
        boolean z8;
        ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats;
        int size9;
        long startTime;
        int starts;
        int launches;
        Uid.Proc procValueAt;
        long userTime;
        long systemTime;
        long foregroundTime;
        int starts2;
        int numCrashes;
        int numAnrs;
        int iCountExcessivePowers;
        Uid.Proc proc;
        String str45;
        boolean z9;
        boolean z10;
        int i16;
        String str46;
        boolean z11;
        Uid.Proc.ExcessivePower excessivePower;
        String str47;
        long[] cpuFreqTimes2;
        boolean z12;
        long[] screenOffCpuFreqTimes2;
        int i17;
        int i18;
        int i19;
        int i20;
        long processStateTime;
        Uid.Sensor sensorValueAt;
        int handle;
        Timer sensorTime;
        SparseArray<? extends Uid.Sensor> sparseArray2;
        long j25;
        String str48;
        String str49;
        long totalTimeLocked;
        int countLocked;
        Timer sensorBackgroundTime;
        int countLocked2;
        long totalDurationMsLocked;
        long totalDurationMsLocked2;
        SparseIntArray sparseIntArrayValueAt;
        int i21;
        long totalTimeLocked2;
        int countLocked3;
        Timer subTimer;
        long totalDurationMsLocked3;
        long j26;
        int countLocked4;
        long totalTimeLocked3;
        int countLocked5;
        Timer subTimer2;
        long totalDurationMsLocked4;
        long j27;
        int countLocked6;
        String str50;
        long totalTimeLocked4;
        int countLocked7;
        long j28;
        long j29;
        boolean z13;
        long j30;
        Timer subTimer3;
        long totalDurationMsLocked5;
        Timer wakeTime;
        Timer subTimer4;
        int i22;
        boolean z14;
        int userActivityCount;
        String str51;
        long totalTimeLocked5;
        int countLocked8;
        Timer bluetoothScanBackgroundTimer;
        int countLocked9;
        long totalDurationMsLocked6;
        long totalDurationMsLocked7;
        int countLocked10;
        int countLocked11;
        Timer bluetoothUnoptimizedScanTimer;
        long totalDurationMsLocked8;
        long maxDurationMsLocked;
        Timer bluetoothUnoptimizedScanBackgroundTimer;
        long totalDurationMsLocked9;
        long maxDurationMsLocked2;
        long j31;
        long j32;
        String str52;
        Map<String, ? extends Timer> wakeupReasonStats;
        ArrayList arrayList;
        int i23;
        ArrayList arrayList2;
        int i24;
        String str53;
        String str54;
        String str55;
        boolean z15;
        String str56;
        long j33;
        String str57;
        BatteryStats batteryStats2 = this;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long j34 = (jElapsedRealtime + 500) / 1000;
        long batteryUptime = batteryStats2.getBatteryUptime(jUptimeMillis);
        long jComputeBatteryUptime = batteryStats2.computeBatteryUptime(jUptimeMillis, i);
        long jComputeBatteryRealtime = batteryStats2.computeBatteryRealtime(jElapsedRealtime, i);
        long jComputeRealtime = batteryStats2.computeRealtime(jElapsedRealtime, i);
        long jComputeUptime = batteryStats2.computeUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffUptime = batteryStats2.computeBatteryScreenOffUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffRealtime = batteryStats2.computeBatteryScreenOffRealtime(jElapsedRealtime, i);
        long jComputeBatteryTimeRemaining = batteryStats2.computeBatteryTimeRemaining(jElapsedRealtime);
        long jComputeChargeTimeRemaining = batteryStats2.computeChargeTimeRemaining(jElapsedRealtime);
        long screenDozeTime = batteryStats2.getScreenDozeTime(jElapsedRealtime, i);
        StringBuilder sb3 = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size10 = uidStats.size();
        int estimatedBatteryCapacity = getEstimatedBatteryCapacity();
        SparseArray<? extends Uid> sparseArray3 = uidStats;
        if (estimatedBatteryCapacity > 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Estimated battery capacity: ");
            sb3.append(BatteryStatsHelper.makemAh(estimatedBatteryCapacity));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        int minLearnedBatteryCapacity = getMinLearnedBatteryCapacity();
        if (minLearnedBatteryCapacity > 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Min learned battery capacity: ");
            sb3.append(BatteryStatsHelper.makemAh(minLearnedBatteryCapacity / 1000));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        int maxLearnedBatteryCapacity = getMaxLearnedBatteryCapacity();
        if (maxLearnedBatteryCapacity > 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Max learned battery capacity: ");
            sb3.append(BatteryStatsHelper.makemAh(maxLearnedBatteryCapacity / 1000));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Time on battery: ");
        long j35 = jComputeBatteryRealtime / 1000;
        formatTimeMs(sb3, j35);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(jComputeBatteryRealtime, jComputeRealtime));
        sb3.append(") realtime, ");
        formatTimeMs(sb3, jComputeBatteryUptime / 1000);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(jComputeBatteryUptime, jComputeBatteryRealtime));
        sb3.append(") uptime");
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Time on battery screen off: ");
        formatTimeMs(sb3, jComputeBatteryScreenOffRealtime / 1000);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(jComputeBatteryScreenOffRealtime, jComputeBatteryRealtime));
        sb3.append(") realtime, ");
        formatTimeMs(sb3, jComputeBatteryScreenOffUptime / 1000);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(jComputeBatteryScreenOffUptime, jComputeBatteryRealtime));
        sb3.append(") uptime");
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Time on battery screen doze: ");
        formatTimeMs(sb3, screenDozeTime / 1000);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(screenDozeTime, jComputeBatteryRealtime));
        sb3.append(")");
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Total run time: ");
        formatTimeMs(sb3, jComputeRealtime / 1000);
        sb3.append("realtime, ");
        formatTimeMs(sb3, jComputeUptime / 1000);
        sb3.append("uptime");
        printWriter.println(sb3.toString());
        if (jComputeBatteryTimeRemaining >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Battery time remaining: ");
            formatTimeMs(sb3, jComputeBatteryTimeRemaining / 1000);
            printWriter.println(sb3.toString());
        }
        if (jComputeChargeTimeRemaining >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Charge time remaining: ");
            formatTimeMs(sb3, jComputeChargeTimeRemaining / 1000);
            printWriter.println(sb3.toString());
        }
        long uahDischarge = batteryStats2.getUahDischarge(i);
        if (uahDischarge >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(uahDischarge / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        long uahDischargeScreenOff = batteryStats2.getUahDischargeScreenOff(i);
        if (uahDischargeScreenOff >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Screen off discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(uahDischargeScreenOff / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        long uahDischargeScreenDoze = batteryStats2.getUahDischargeScreenDoze(i);
        if (uahDischargeScreenDoze >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Screen doze discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(uahDischargeScreenDoze / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        long j36 = uahDischarge - uahDischargeScreenOff;
        if (j36 >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Screen on discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(j36 / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        long uahDischargeLightDoze = batteryStats2.getUahDischargeLightDoze(i);
        if (uahDischargeLightDoze >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Device light doze discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(uahDischargeLightDoze / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        long uahDischargeDeepDoze = batteryStats2.getUahDischargeDeepDoze(i);
        if (uahDischargeDeepDoze >= 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Device deep doze discharge: ");
            sb3.append(BatteryStatsHelper.makemAh(uahDischargeDeepDoze / 1000.0d));
            sb3.append(" mAh");
            printWriter.println(sb3.toString());
        }
        printWriter.print("  Start clock time: ");
        printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getStartClockTime()).toString());
        long j37 = jElapsedRealtime;
        long screenOnTime = batteryStats2.getScreenOnTime(j37, i);
        long interactiveTime = batteryStats2.getInteractiveTime(j37, i);
        long powerSaveModeEnabledTime = batteryStats2.getPowerSaveModeEnabledTime(j37, i);
        long deviceIdleModeTime = batteryStats2.getDeviceIdleModeTime(1, j37, i);
        long deviceIdleModeTime2 = batteryStats2.getDeviceIdleModeTime(2, j37, i);
        long deviceIdlingTime = batteryStats2.getDeviceIdlingTime(1, j37, i);
        long deviceIdlingTime2 = batteryStats2.getDeviceIdlingTime(2, j37, i);
        long phoneOnTime = batteryStats2.getPhoneOnTime(j37, i);
        batteryStats2.getGlobalWifiRunningTime(j37, i);
        batteryStats2.getWifiOnTime(j37, i);
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Screen on: ");
        formatTimeMs(sb3, screenOnTime / 1000);
        sb3.append("(");
        long j38 = jComputeBatteryRealtime;
        sb3.append(batteryStats2.formatRatioLocked(screenOnTime, j38));
        sb3.append(") ");
        sb3.append(batteryStats2.getScreenOnCount(i));
        sb3.append("x, Interactive: ");
        formatTimeMs(sb3, interactiveTime / 1000);
        sb3.append("(");
        sb3.append(batteryStats2.formatRatioLocked(interactiveTime, j38));
        String str58 = ")";
        sb3.append(str58);
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Screen brightnesses:");
        int i25 = 0;
        boolean z16 = false;
        while (true) {
            j = j38;
            if (i25 >= 5) {
                break;
            }
            String str59 = str58;
            long screenBrightnessTime = batteryStats2.getScreenBrightnessTime(i25, j37, i);
            if (screenBrightnessTime == 0) {
                str58 = str59;
            } else {
                sb3.append("\n    ");
                sb3.append(str);
                sb3.append(SCREEN_BRIGHTNESS_NAMES[i25]);
                sb3.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb3, screenBrightnessTime / 1000);
                sb3.append("(");
                sb3.append(batteryStats2.formatRatioLocked(screenBrightnessTime, screenOnTime));
                str58 = str59;
                sb3.append(str58);
                z16 = true;
            }
            i25++;
            j38 = j;
        }
        if (!z16) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        if (powerSaveModeEnabledTime != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Power save mode enabled: ");
            formatTimeMs(sb3, powerSaveModeEnabledTime / 1000);
            sb3.append("(");
            j2 = j;
            sb3.append(batteryStats2.formatRatioLocked(powerSaveModeEnabledTime, j2));
            sb3.append(str58);
            printWriter.println(sb3.toString());
        } else {
            j2 = j;
        }
        if (deviceIdlingTime != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Device light idling: ");
            formatTimeMs(sb3, deviceIdlingTime / 1000);
            sb3.append("(");
            batteryStats2 = this;
            sb3.append(batteryStats2.formatRatioLocked(deviceIdlingTime, j2));
            str2 = ") ";
            sb3.append(str2);
            sb3.append(batteryStats2.getDeviceIdlingCount(1, i));
            sb3.append("x");
            printWriter.println(sb3.toString());
        } else {
            str2 = ") ";
        }
        if (deviceIdleModeTime != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Idle mode light time: ");
            formatTimeMs(sb3, deviceIdleModeTime / 1000);
            sb3.append("(");
            sb3.append(batteryStats2.formatRatioLocked(deviceIdleModeTime, j2));
            sb3.append(str2);
            sb3.append(batteryStats2.getDeviceIdleModeCount(1, i));
            sb3.append("x");
            sb3.append(" -- longest ");
            formatTimeMs(sb3, batteryStats2.getLongestDeviceIdleModeTime(1));
            printWriter.println(sb3.toString());
        }
        if (deviceIdlingTime2 != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Device full idling: ");
            formatTimeMs(sb3, deviceIdlingTime2 / 1000);
            sb3.append("(");
            sb3.append(batteryStats2.formatRatioLocked(deviceIdlingTime2, j2));
            sb3.append(str2);
            sb3.append(batteryStats2.getDeviceIdlingCount(2, i));
            sb3.append("x");
            printWriter.println(sb3.toString());
        }
        if (deviceIdleModeTime2 != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Idle mode full time: ");
            str3 = "x";
            formatTimeMs(sb3, deviceIdleModeTime2 / 1000);
            sb3.append("(");
            sb3.append(batteryStats2.formatRatioLocked(deviceIdleModeTime2, j2));
            sb3.append(str2);
            sb3.append(batteryStats2.getDeviceIdleModeCount(2, i));
            sb3.append(str3);
            sb3.append(" -- longest ");
            formatTimeMs(sb3, batteryStats2.getLongestDeviceIdleModeTime(2));
            printWriter.println(sb3.toString());
        } else {
            str3 = "x";
        }
        if (phoneOnTime != 0) {
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("  Active phone call: ");
            formatTimeMs(sb3, phoneOnTime / 1000);
            sb3.append("(");
            sb3.append(batteryStats2.formatRatioLocked(phoneOnTime, j2));
            sb3.append(str2);
            sb3.append(batteryStats2.getPhoneOnCount(i));
            sb3.append(str3);
        }
        int numConnectivityChange = batteryStats2.getNumConnectivityChange(i);
        if (numConnectivityChange != 0) {
            printWriter.print(str);
            printWriter.print("  Connectivity changes: ");
            printWriter.println(numConnectivityChange);
        }
        ArrayList arrayList3 = new ArrayList();
        long totalTimeLocked6 = 0;
        long j39 = 0;
        int i26 = size10;
        int i27 = 0;
        while (i27 < i26) {
            SparseArray<? extends Uid> sparseArray4 = sparseArray3;
            Uid uidValueAt = sparseArray4.valueAt(i27);
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats2 = uidValueAt.getWakelockStats();
            String str60 = str3;
            int size11 = wakelockStats2.size() - 1;
            while (size11 >= 0) {
                int i28 = i26;
                Uid.Wakelock wakelockValueAt = wakelockStats2.valueAt(size11);
                SparseArray<? extends Uid> sparseArray5 = sparseArray4;
                String str61 = str2;
                Timer wakeTime2 = wakelockValueAt.getWakeTime(1);
                long j40 = j2;
                long j41 = j37;
                if (wakeTime2 != null) {
                    totalTimeLocked6 += wakeTime2.getTotalTimeLocked(j41, i);
                }
                Timer wakeTime3 = wakelockValueAt.getWakeTime(0);
                if (wakeTime3 != null) {
                    long totalTimeLocked7 = wakeTime3.getTotalTimeLocked(j41, i);
                    if (totalTimeLocked7 > 0) {
                        if (i2 < 0) {
                            arrayList3.add(new TimerEntry(wakelockStats2.keyAt(size11), uidValueAt.getUid(), wakeTime3, totalTimeLocked7));
                        }
                        j39 += totalTimeLocked7;
                    }
                }
                size11--;
                str58 = str58;
                sparseArray4 = sparseArray5;
                i26 = i28;
                str2 = str61;
                j37 = j41;
                j2 = j40;
            }
            i27++;
            sparseArray3 = sparseArray4;
            str3 = str60;
            j37 = j37;
            j2 = j2;
        }
        long j42 = j2;
        String str62 = str3;
        int i29 = i26;
        String str63 = str2;
        long j43 = j37;
        SparseArray<? extends Uid> sparseArray6 = sparseArray3;
        String str64 = str58;
        long networkActivityBytes = batteryStats2.getNetworkActivityBytes(0, i);
        long networkActivityBytes2 = batteryStats2.getNetworkActivityBytes(1, i);
        long networkActivityBytes3 = batteryStats2.getNetworkActivityBytes(2, i);
        long networkActivityBytes4 = batteryStats2.getNetworkActivityBytes(3, i);
        long networkActivityPackets = batteryStats2.getNetworkActivityPackets(0, i);
        long networkActivityPackets2 = batteryStats2.getNetworkActivityPackets(1, i);
        long networkActivityPackets3 = batteryStats2.getNetworkActivityPackets(2, i);
        long networkActivityPackets4 = batteryStats2.getNetworkActivityPackets(3, i);
        long networkActivityBytes5 = batteryStats2.getNetworkActivityBytes(4, i);
        long networkActivityBytes6 = batteryStats2.getNetworkActivityBytes(5, i);
        if (totalTimeLocked6 != 0) {
            sb3.setLength(0);
            str4 = str;
            j4 = networkActivityBytes6;
            sb3.append(str4);
            sb3.append("  Total full wakelock time: ");
            j3 = networkActivityPackets3;
            formatTimeMsNoSpace(sb3, (totalTimeLocked6 + 500) / 1000);
            printWriter.println(sb3.toString());
        } else {
            str4 = str;
            j3 = networkActivityPackets3;
            j4 = networkActivityBytes6;
        }
        if (j39 != 0) {
            sb3.setLength(0);
            sb3.append(str4);
            sb3.append("  Total partial wakelock time: ");
            formatTimeMsNoSpace(sb3, (j39 + 500) / 1000);
            printWriter.println(sb3.toString());
        }
        long wifiMulticastWakelockTime = batteryStats2.getWifiMulticastWakelockTime(j43, i);
        int wifiMulticastWakelockCount = batteryStats2.getWifiMulticastWakelockCount(i);
        if (wifiMulticastWakelockTime != 0) {
            sb3.setLength(0);
            sb3.append(str4);
            sb3.append("  Total WiFi Multicast wakelock Count: ");
            sb3.append(wifiMulticastWakelockCount);
            printWriter.println(sb3.toString());
            sb3.setLength(0);
            sb3.append(str4);
            sb3.append("  Total WiFi Multicast wakelock time: ");
            formatTimeMsNoSpace(sb3, (wifiMulticastWakelockTime + 500) / 1000);
            printWriter.println(sb3.toString());
        }
        printWriter.println("");
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("  CONNECTIVITY POWER SUMMARY START");
        printWriter.println(sb3.toString());
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("  Logging duration for connectivity statistics: ");
        formatTimeMs(sb3, j35);
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("  Cellular Statistics:");
        printWriter.println(sb3.toString());
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("     Cellular kernel active time: ");
        long mobileRadioActiveTime = batteryStats2.getMobileRadioActiveTime(j43, i);
        long j44 = j43;
        formatTimeMs(sb3, mobileRadioActiveTime / 1000);
        sb3.append("(");
        long j45 = j42;
        sb3.append(batteryStats2.formatRatioLocked(mobileRadioActiveTime, j45));
        String str65 = str64;
        sb3.append(str65);
        printWriter.println(sb3.toString());
        printWriter.print("     Cellular data received: ");
        printWriter.println(batteryStats2.formatBytesLocked(networkActivityBytes));
        printWriter.print("     Cellular data sent: ");
        printWriter.println(batteryStats2.formatBytesLocked(networkActivityBytes2));
        printWriter.print("     Cellular packets received: ");
        printWriter.println(networkActivityPackets);
        printWriter.print("     Cellular packets sent: ");
        printWriter.println(networkActivityPackets2);
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("     Cellular Radio Access Technology:");
        int i30 = 0;
        boolean z17 = false;
        while (i30 < 21) {
            long j46 = j44;
            long j47 = mobileRadioActiveTime;
            String str66 = str65;
            long phoneDataConnectionTime = batteryStats2.getPhoneDataConnectionTime(i30, j46, i);
            if (phoneDataConnectionTime == 0) {
                str57 = str63;
            } else {
                sb3.append("\n       ");
                sb3.append(str4);
                sb3.append(DATA_CONNECTION_NAMES[i30]);
                sb3.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb3, phoneDataConnectionTime / 1000);
                sb3.append("(");
                sb3.append(batteryStats2.formatRatioLocked(phoneDataConnectionTime, j45));
                str57 = str63;
                sb3.append(str57);
                z17 = true;
            }
            i30++;
            str63 = str57;
            str65 = str66;
            mobileRadioActiveTime = j47;
            j44 = j46;
        }
        long j48 = j44;
        long j49 = mobileRadioActiveTime;
        String str67 = str63;
        String str68 = str65;
        if (!z17) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str4);
        sb3.append("     Cellular Rx signal strength (RSRP):");
        String[] strArr = {"very poor (less than -128dBm): ", "poor (-128dBm to -118dBm): ", "moderate (-118dBm to -108dBm): ", "good (-108dBm to -98dBm): ", "great (greater than -98dBm): "};
        int i31 = 0;
        boolean z18 = false;
        for (int iMin = Math.min(5, 5); i31 < iMin; iMin = iMin) {
            long j50 = j45;
            long phoneSignalStrengthTime = batteryStats2.getPhoneSignalStrengthTime(i31, j48, i);
            if (phoneSignalStrengthTime == 0) {
                str56 = str5;
                j33 = j50;
            } else {
                sb3.append("\n       ");
                sb3.append(str4);
                sb3.append(strArr[i31]);
                str56 = str5;
                sb3.append(str56);
                formatTimeMs(sb3, phoneSignalStrengthTime / 1000);
                sb3.append("(");
                j33 = j50;
                sb3.append(batteryStats2.formatRatioLocked(phoneSignalStrengthTime, j33));
                sb3.append(str67);
                z18 = true;
            }
            i31++;
            str5 = str56;
            j45 = j33;
            strArr = strArr;
        }
        long j51 = j45;
        String str69 = str5;
        if (!z18) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        String str70 = str67;
        String str71 = str69;
        long j52 = batteryUptime;
        String str72 = str4;
        String str73 = "(";
        long j53 = j34;
        String str74 = str68;
        printControllerActivity(printWriter, sb3, str, CELLULAR_CONTROLLER_NAME, getModemControllerActivity(), i);
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str72);
        sb3.append("  Wifi Statistics:");
        printWriter.println(sb3.toString());
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str72);
        sb3.append("     Wifi kernel active time: ");
        long wifiActiveTime = batteryStats2.getWifiActiveTime(j48, i);
        formatTimeMs(sb3, wifiActiveTime / 1000);
        sb3.append(str73);
        sb3.append(batteryStats2.formatRatioLocked(wifiActiveTime, j51));
        sb3.append(str74);
        printWriter.println(sb3.toString());
        printWriter.print("     Wifi data received: ");
        printWriter.println(batteryStats2.formatBytesLocked(networkActivityBytes3));
        printWriter.print("     Wifi data sent: ");
        printWriter.println(batteryStats2.formatBytesLocked(networkActivityBytes4));
        printWriter.print("     Wifi packets received: ");
        printWriter.println(j3);
        printWriter.print("     Wifi packets sent: ");
        printWriter.println(networkActivityPackets4);
        sb3.setLength(0);
        sb3.append(str72);
        sb3.append("     Wifi states:");
        int i32 = 0;
        boolean z19 = false;
        while (i32 < 8) {
            long wifiStateTime = batteryStats2.getWifiStateTime(i32, j48, i);
            if (wifiStateTime == 0) {
                z15 = z19;
                str54 = str71;
                str55 = str70;
            } else {
                sb3.append("\n       ");
                sb3.append(WIFI_STATE_NAMES[i32]);
                str54 = str71;
                sb3.append(str54);
                formatTimeMs(sb3, wifiStateTime / 1000);
                sb3.append(str73);
                sb3.append(batteryStats2.formatRatioLocked(wifiStateTime, j51));
                str55 = str70;
                sb3.append(str55);
                z15 = true;
            }
            i32++;
            str71 = str54;
            z19 = z15;
            str70 = str55;
            str74 = str74;
        }
        String str75 = str74;
        String str76 = str71;
        String str77 = str70;
        if (!z19) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str72);
        sb3.append("     Wifi supplicant states:");
        boolean z20 = false;
        for (int i33 = 0; i33 < 13; i33++) {
            long wifiSupplStateTime = batteryStats2.getWifiSupplStateTime(i33, j48, i);
            if (wifiSupplStateTime != 0) {
                sb3.append("\n       ");
                sb3.append(WIFI_SUPPL_STATE_NAMES[i33]);
                sb3.append(str76);
                formatTimeMs(sb3, wifiSupplStateTime / 1000);
                sb3.append(str73);
                sb3.append(batteryStats2.formatRatioLocked(wifiSupplStateTime, j51));
                sb3.append(str77);
                z20 = true;
            }
        }
        if (!z20) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("     Wifi Rx signal strength (RSSI):");
        String[] strArr2 = {"very poor (less than -88.75dBm): ", "poor (-88.75 to -77.5dBm): ", "moderate (-77.5dBm to -66.25dBm): ", "good (-66.25dBm to -55dBm): ", "great (greater than -55dBm): "};
        int i34 = 0;
        boolean z21 = false;
        for (int iMin2 = Math.min(5, 5); i34 < iMin2; iMin2 = iMin2) {
            String str78 = str73;
            long wifiSignalStrengthTime = batteryStats2.getWifiSignalStrengthTime(i34, j48, i);
            if (wifiSignalStrengthTime == 0) {
                str53 = str78;
            } else {
                sb3.append("\n    ");
                sb3.append(str);
                sb3.append("     ");
                sb3.append(strArr2[i34]);
                formatTimeMs(sb3, wifiSignalStrengthTime / 1000);
                str53 = str78;
                sb3.append(str53);
                sb3.append(batteryStats2.formatRatioLocked(wifiSignalStrengthTime, j51));
                sb3.append(str77);
                z21 = true;
            }
            i34++;
            str73 = str53;
            strArr2 = strArr2;
        }
        String str79 = str73;
        if (!z21) {
            sb3.append(" (no activity)");
        }
        printWriter.println(sb3.toString());
        String str80 = str79;
        printControllerActivity(printWriter, sb3, str, WIFI_CONTROLLER_NAME, getWifiControllerActivity(), i);
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  GPS Statistics:");
        printWriter.println(sb3.toString());
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("     GPS signal quality (Top 4 Average CN0):");
        String[] strArr3 = {"poor (less than 20 dBHz): ", "good (greater than 20 dBHz): "};
        int iMin3 = Math.min(2, 2);
        for (int i35 = 0; i35 < iMin3; i35++) {
            long gpsSignalQualityTime = batteryStats2.getGpsSignalQualityTime(i35, j48, i);
            sb3.append("\n    ");
            sb3.append(str);
            sb3.append("  ");
            sb3.append(strArr3[i35]);
            formatTimeMs(sb3, gpsSignalQualityTime / 1000);
            sb3.append(str80);
            sb3.append(batteryStats2.formatRatioLocked(gpsSignalQualityTime, j51));
            sb3.append(str77);
        }
        String str81 = str77;
        printWriter.println(sb3.toString());
        long gpsBatteryDrainMaMs = getGpsBatteryDrainMaMs();
        if (gpsBatteryDrainMaMs > 0) {
            printWriter.print(str);
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("     Battery Drain (mAh): ");
            sb3.append(Double.toString(gpsBatteryDrainMaMs / 3600000.0d));
            printWriter.println(sb3.toString());
        }
        printWriter.print(str);
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  CONNECTIVITY POWER SUMMARY END");
        printWriter.println(sb3.toString());
        printWriter.println("");
        printWriter.print(str);
        printWriter.print("  Bluetooth total received: ");
        printWriter.print(batteryStats2.formatBytesLocked(networkActivityBytes5));
        printWriter.print(", sent: ");
        printWriter.println(batteryStats2.formatBytesLocked(j4));
        long bluetoothScanTime = batteryStats2.getBluetoothScanTime(j48, i) / 1000;
        sb3.setLength(0);
        sb3.append(str);
        sb3.append("  Bluetooth scan time: ");
        formatTimeMs(sb3, bluetoothScanTime);
        printWriter.println(sb3.toString());
        long j54 = j48;
        long j55 = j51;
        printControllerActivity(printWriter, sb3, str, "Bluetooth", getBluetoothControllerActivity(), i);
        printWriter.println();
        if (i == 2) {
            if (getIsOnBattery()) {
                printWriter.print(str);
                printWriter.println("  Device is currently unplugged");
                printWriter.print(str);
                printWriter.print("    Discharge cycle start level: ");
                printWriter.println(getDischargeStartLevel());
                printWriter.print(str);
                printWriter.print("    Discharge cycle current level: ");
                printWriter.println(getDischargeCurrentLevel());
            } else {
                printWriter.print(str);
                printWriter.println("  Device is currently plugged into power");
                printWriter.print(str);
                printWriter.print("    Last discharge cycle start level: ");
                printWriter.println(getDischargeStartLevel());
                printWriter.print(str);
                printWriter.print("    Last discharge cycle end level: ");
                printWriter.println(getDischargeCurrentLevel());
            }
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen on: ");
            printWriter.println(getDischargeAmountScreenOn());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen off: ");
            printWriter.println(getDischargeAmountScreenOff());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen doze: ");
            printWriter.println(getDischargeAmountScreenDoze());
            str6 = str76;
            printWriter.println(str6);
        } else {
            str6 = str76;
            printWriter.print(str);
            printWriter.println("  Device battery use since last full charge");
            printWriter.print(str);
            printWriter.print("    Amount discharged (lower bound): ");
            printWriter.println(getLowDischargeAmountSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged (upper bound): ");
            printWriter.println(getHighDischargeAmountSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen on: ");
            printWriter.println(getDischargeAmountScreenOnSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen off: ");
            printWriter.println(getDischargeAmountScreenOffSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen doze: ");
            printWriter.println(getDischargeAmountScreenDozeSinceCharge());
            printWriter.println();
        }
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, z);
        batteryStatsHelper.create(batteryStats2);
        batteryStatsHelper.refreshStats(i, -1);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        String str82 = " (";
        String str83 = ": ";
        if (usageList == null || usageList.size() <= 0) {
            j5 = j55;
            str7 = str80;
        } else {
            printWriter.print(str);
            printWriter.println("  Estimated power use (mAh):");
            printWriter.print(str);
            printWriter.print("    Capacity: ");
            batteryStats2.printmAh(printWriter, batteryStatsHelper.getPowerProfile().getBatteryCapacity());
            printWriter.print(", Computed drain: ");
            batteryStats2.printmAh(printWriter, batteryStatsHelper.getComputedPower());
            printWriter.print(", actual drain: ");
            batteryStats2.printmAh(printWriter, batteryStatsHelper.getMinDrainedPower());
            if (batteryStatsHelper.getMinDrainedPower() != batteryStatsHelper.getMaxDrainedPower()) {
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                batteryStats2.printmAh(printWriter, batteryStatsHelper.getMaxDrainedPower());
            }
            printWriter.println();
            int i36 = 0;
            while (i36 < usageList.size()) {
                BatterySipper batterySipper = usageList.get(i36);
                printWriter.print(str);
                switch (AnonymousClass2.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[batterySipper.drainType.ordinal()]) {
                    case 1:
                        printWriter.print("    Ambient display: ");
                        break;
                    case 2:
                        printWriter.print("    Idle: ");
                        break;
                    case 3:
                        printWriter.print("    Cell standby: ");
                        break;
                    case 4:
                        printWriter.print("    Phone calls: ");
                        break;
                    case 5:
                        printWriter.print("    Wifi: ");
                        break;
                    case 6:
                        printWriter.print("    Bluetooth: ");
                        break;
                    case 7:
                        printWriter.print("    Screen: ");
                        break;
                    case 8:
                        printWriter.print("    Flashlight: ");
                        break;
                    case 9:
                        printWriter.print("    Uid ");
                        UserHandle.formatUid(printWriter, batterySipper.uidObj.getUid());
                        printWriter.print(": ");
                        break;
                    case 10:
                        printWriter.print("    User ");
                        printWriter.print(batterySipper.userId);
                        printWriter.print(": ");
                        break;
                    case 11:
                        printWriter.print("    Unaccounted: ");
                        break;
                    case 12:
                        printWriter.print("    Over-counted: ");
                        break;
                    case 13:
                        printWriter.print("    Camera: ");
                        break;
                    default:
                        printWriter.print("    ???: ");
                        break;
                }
                long j56 = j55;
                batteryStats2.printmAh(printWriter, batterySipper.totalPowerMah);
                String str84 = str80;
                if (batterySipper.usagePowerMah != batterySipper.totalPowerMah) {
                    printWriter.print(" (");
                    if (batterySipper.usagePowerMah != 0.0d) {
                        printWriter.print(" usage=");
                        batteryStats2.printmAh(printWriter, batterySipper.usagePowerMah);
                    }
                    if (batterySipper.cpuPowerMah != 0.0d) {
                        printWriter.print(" cpu=");
                        batteryStats2.printmAh(printWriter, batterySipper.cpuPowerMah);
                    }
                    if (batterySipper.wakeLockPowerMah != 0.0d) {
                        printWriter.print(" wake=");
                        batteryStats2.printmAh(printWriter, batterySipper.wakeLockPowerMah);
                    }
                    if (batterySipper.mobileRadioPowerMah != 0.0d) {
                        printWriter.print(" radio=");
                        batteryStats2.printmAh(printWriter, batterySipper.mobileRadioPowerMah);
                    }
                    if (batterySipper.wifiPowerMah != 0.0d) {
                        printWriter.print(" wifi=");
                        batteryStats2.printmAh(printWriter, batterySipper.wifiPowerMah);
                    }
                    if (batterySipper.bluetoothPowerMah != 0.0d) {
                        printWriter.print(" bt=");
                        batteryStats2.printmAh(printWriter, batterySipper.bluetoothPowerMah);
                    }
                    if (batterySipper.gpsPowerMah != 0.0d) {
                        printWriter.print(" gps=");
                        batteryStats2.printmAh(printWriter, batterySipper.gpsPowerMah);
                    }
                    if (batterySipper.sensorPowerMah != 0.0d) {
                        printWriter.print(" sensor=");
                        batteryStats2.printmAh(printWriter, batterySipper.sensorPowerMah);
                    }
                    if (batterySipper.cameraPowerMah != 0.0d) {
                        printWriter.print(" camera=");
                        batteryStats2.printmAh(printWriter, batterySipper.cameraPowerMah);
                    }
                    if (batterySipper.flashlightPowerMah != 0.0d) {
                        printWriter.print(" flash=");
                        batteryStats2.printmAh(printWriter, batterySipper.flashlightPowerMah);
                    }
                    printWriter.print(" )");
                }
                if (batterySipper.totalSmearedPowerMah != batterySipper.totalPowerMah) {
                    printWriter.print(" Including smearing: ");
                    batteryStats2.printmAh(printWriter, batterySipper.totalSmearedPowerMah);
                    printWriter.print(" (");
                    if (batterySipper.screenPowerMah != 0.0d) {
                        printWriter.print(" screen=");
                        batteryStats2.printmAh(printWriter, batterySipper.screenPowerMah);
                    }
                    if (batterySipper.proportionalSmearMah != 0.0d) {
                        printWriter.print(" proportional=");
                        batteryStats2.printmAh(printWriter, batterySipper.proportionalSmearMah);
                    }
                    printWriter.print(" )");
                }
                if (batterySipper.shouldHide) {
                    printWriter.print(" Excluded from smearing");
                }
                printWriter.println();
                i36++;
                str80 = str84;
                j55 = j56;
            }
            j5 = j55;
            str7 = str80;
            printWriter.println();
        }
        List<BatterySipper> mobilemsppList = batteryStatsHelper.getMobilemsppList();
        if (mobilemsppList == null || mobilemsppList.size() <= 0) {
            str8 = str7;
            str9 = str75;
            j6 = j5;
        } else {
            printWriter.print(str);
            printWriter.println("  Per-app mobile ms per packet:");
            long j57 = 0;
            for (int i37 = 0; i37 < mobilemsppList.size(); i37++) {
                BatterySipper batterySipper2 = mobilemsppList.get(i37);
                sb3.setLength(0);
                sb3.append(str);
                sb3.append("    Uid ");
                UserHandle.formatUid(sb3, batterySipper2.uidObj.getUid());
                sb3.append(": ");
                sb3.append(BatteryStatsHelper.makemAh(batterySipper2.mobilemspp));
                sb3.append(" (");
                sb3.append(batterySipper2.mobileRxPackets + batterySipper2.mobileTxPackets);
                sb3.append(" packets over ");
                formatTimeMsNoSpace(sb3, batterySipper2.mobileActive);
                sb3.append(str81);
                sb3.append(batterySipper2.mobileActiveCount);
                sb3.append(str62);
                printWriter.println(sb3.toString());
                j57 += batterySipper2.mobileActive;
            }
            sb3.setLength(0);
            sb3.append(str);
            sb3.append("    TOTAL TIME: ");
            formatTimeMs(sb3, j57);
            str8 = str7;
            sb3.append(str8);
            j6 = j5;
            sb3.append(batteryStats2.formatRatioLocked(j57, j6));
            str9 = str75;
            sb3.append(str9);
            printWriter.println(sb3.toString());
            printWriter.println();
        }
        Comparator<TimerEntry> comparator = new Comparator<TimerEntry>() { // from class: android.os.BatteryStats.1
            @Override // java.util.Comparator
            public int compare(TimerEntry timerEntry, TimerEntry timerEntry2) {
                long j58 = timerEntry.mTime;
                long j59 = timerEntry2.mTime;
                if (j58 < j59) {
                    return 1;
                }
                return j58 > j59 ? -1 : 0;
            }
        };
        if (i2 < 0) {
            Map<String, ? extends Timer> kernelWakelockStats = getKernelWakelockStats();
            if (kernelWakelockStats.size() > 0) {
                ArrayList arrayList4 = new ArrayList();
                Iterator<Map.Entry<String, ? extends Timer>> it = kernelWakelockStats.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ? extends Timer> next = it.next();
                    long j58 = j6;
                    Timer value = next.getValue();
                    Iterator<Map.Entry<String, ? extends Timer>> it2 = it;
                    long j59 = j54;
                    long jComputeWakeLock5 = computeWakeLock(value, j59, i);
                    if (jComputeWakeLock5 > 0) {
                        arrayList4.add(new TimerEntry(next.getKey(), 0, value, jComputeWakeLock5));
                    }
                    j54 = j59;
                    j6 = j58;
                    it = it2;
                }
                j7 = j6;
                long j60 = j54;
                if (arrayList4.size() > 0) {
                    Collections.sort(arrayList4, comparator);
                    printWriter.print(str);
                    printWriter.println("  All kernel wake locks:");
                    int i38 = 0;
                    while (i38 < arrayList4.size()) {
                        TimerEntry timerEntry = (TimerEntry) arrayList4.get(i38);
                        int i39 = i38;
                        sb3.setLength(0);
                        sb3.append(str);
                        sb3.append("  Kernel Wake lock ");
                        sb3.append(timerEntry.mName);
                        String str85 = str62;
                        String str86 = str82;
                        String str87 = str83;
                        long j61 = j60;
                        String str88 = str6;
                        ArrayList arrayList5 = arrayList4;
                        if (!printWakeLock(sb3, timerEntry.mTimer, j60, null, i, ": ").equals(str87)) {
                            sb3.append(" realtime");
                            printWriter.println(sb3.toString());
                        }
                        i38 = i39 + 1;
                        str83 = str87;
                        arrayList4 = arrayList5;
                        j60 = j61;
                        str62 = str85;
                        str82 = str86;
                        str6 = str88;
                    }
                    str82 = str82;
                    str83 = str83;
                    j54 = j60;
                    str10 = str62;
                    str52 = str6;
                    i3 = -1;
                    printWriter.println();
                } else {
                    j54 = j60;
                }
                if (arrayList3.size() > 0) {
                    arrayList2 = arrayList3;
                    Collections.sort(arrayList2, comparator);
                    printWriter.print(str);
                    printWriter.println("  All partial wake locks:");
                    i24 = 0;
                    while (i24 < arrayList2.size()) {
                        TimerEntry timerEntry2 = (TimerEntry) arrayList2.get(i24);
                        sb3.setLength(0);
                        sb3.append("  Wake lock ");
                        UserHandle.formatUid(sb3, timerEntry2.mId);
                        String str89 = str52;
                        sb3.append(str89);
                        sb3.append(timerEntry2.mName);
                        printWakeLock(sb3, timerEntry2.mTimer, j54, null, i, ": ");
                        sb3.append(" realtime");
                        printWriter.println(sb3.toString());
                        i24++;
                        str52 = str89;
                        arrayList2 = arrayList2;
                    }
                    str11 = str52;
                    arrayList2.clear();
                    printWriter.println();
                } else {
                    str11 = str52;
                }
                wakeupReasonStats = getWakeupReasonStats();
                if (wakeupReasonStats.size() > 0) {
                    printWriter.print(str);
                    printWriter.println("  All wakeup reasons:");
                    arrayList = new ArrayList();
                    for (Map.Entry<String, ? extends Timer> entry : wakeupReasonStats.entrySet()) {
                        Timer value2 = entry.getValue();
                        arrayList.add(new TimerEntry(entry.getKey(), 0, value2, value2.getCountLocked(i)));
                    }
                    Collections.sort(arrayList, comparator);
                    i23 = 0;
                    while (i23 < arrayList.size()) {
                        TimerEntry timerEntry3 = (TimerEntry) arrayList.get(i23);
                        sb3.setLength(0);
                        sb3.append(str);
                        sb3.append("  Wakeup reason ");
                        sb3.append(timerEntry3.mName);
                        printWakeLock(sb3, timerEntry3.mTimer, j54, null, i, ": ");
                        sb3.append(" realtime");
                        printWriter.println(sb3.toString());
                        i23++;
                        arrayList = arrayList;
                    }
                    printWriter.println();
                }
            } else {
                j7 = j6;
            }
            str10 = str62;
            str52 = str6;
            i3 = -1;
            if (arrayList3.size() > 0) {
                arrayList2 = arrayList3;
                Collections.sort(arrayList2, comparator);
                printWriter.print(str);
                printWriter.println("  All partial wake locks:");
                i24 = 0;
                while (i24 < arrayList2.size()) {
                    TimerEntry timerEntry4 = (TimerEntry) arrayList2.get(i24);
                    sb3.setLength(0);
                    sb3.append("  Wake lock ");
                    UserHandle.formatUid(sb3, timerEntry4.mId);
                    String str810 = str52;
                    sb3.append(str810);
                    sb3.append(timerEntry4.mName);
                    printWakeLock(sb3, timerEntry4.mTimer, j54, null, i, ": ");
                    sb3.append(" realtime");
                    printWriter.println(sb3.toString());
                    i24++;
                    str52 = str810;
                    arrayList2 = arrayList2;
                }
                str11 = str52;
                arrayList2.clear();
                printWriter.println();
            } else {
                str11 = str52;
            }
            wakeupReasonStats = getWakeupReasonStats();
            if (wakeupReasonStats.size() > 0) {
                printWriter.print(str);
                printWriter.println("  All wakeup reasons:");
                arrayList = new ArrayList();
                while (r0.hasNext()) {
                    Timer value3 = entry.getValue();
                    arrayList.add(new TimerEntry(entry.getKey(), 0, value3, value3.getCountLocked(i)));
                }
                Collections.sort(arrayList, comparator);
                i23 = 0;
                while (i23 < arrayList.size()) {
                    TimerEntry timerEntry5 = (TimerEntry) arrayList.get(i23);
                    sb3.setLength(0);
                    sb3.append(str);
                    sb3.append("  Wakeup reason ");
                    sb3.append(timerEntry5.mName);
                    printWakeLock(sb3, timerEntry5.mTimer, j54, null, i, ": ");
                    sb3.append(" realtime");
                    printWriter.println(sb3.toString());
                    i23++;
                    arrayList = arrayList;
                }
                printWriter.println();
            }
        } else {
            j7 = j6;
            str82 = " (";
            str83 = ": ";
            str10 = str62;
            str11 = str6;
            i3 = -1;
            i = i;
        }
        LongSparseArray<? extends Timer> kernelMemoryStats = getKernelMemoryStats();
        if (kernelMemoryStats.size() > 0) {
            printWriter.println("  Memory Stats");
            for (int i40 = 0; i40 < kernelMemoryStats.size(); i40++) {
                sb3.setLength(0);
                sb3.append("  Bandwidth ");
                sb3.append(kernelMemoryStats.keyAt(i40));
                sb3.append(" Time ");
                sb3.append(kernelMemoryStats.valueAt(i40).getTotalTimeLocked(j54, i));
                printWriter.println(sb3.toString());
            }
            i4 = 0;
            printWriter.println();
        } else {
            i4 = 0;
        }
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        if (rpmStats.size() > 0) {
            printWriter.print(str);
            printWriter.println("  Resource Power Manager Stats");
            if (rpmStats.size() > 0) {
                for (Map.Entry<String, ? extends Timer> entry2 : rpmStats.entrySet()) {
                    String key = entry2.getKey();
                    Timer value4 = entry2.getValue();
                    StringBuilder sb4 = sb3;
                    long j62 = j54;
                    printTimer(printWriter, sb4, value4, j62, i, str, key);
                    i4 = i4;
                    sb3 = sb4;
                    str8 = str8;
                    str9 = str9;
                    str81 = str81;
                    str10 = str10;
                    j54 = j62;
                }
            }
            j8 = j54;
            sb = sb3;
            str12 = str8;
            str13 = str9;
            str14 = str81;
            str15 = str10;
            i5 = i4;
            printWriter.println();
        } else {
            j8 = j54;
            sb = sb3;
            str12 = str8;
            str13 = str9;
            str14 = str81;
            str15 = str10;
            i5 = i4;
        }
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            sb.setLength(i5);
            sb.append("  CPU freqs:");
            for (int i41 = i5; i41 < cpuFreqs.length; i41++) {
                sb.append(str11 + cpuFreqs[i41]);
            }
            printWriter2 = printWriter;
            printWriter2.println(sb.toString());
            printWriter.println();
        } else {
            printWriter2 = printWriter;
        }
        int i42 = i5;
        while (i42 < i29) {
            SparseArray<? extends Uid> sparseArray7 = sparseArray6;
            int iKeyAt = sparseArray7.keyAt(i42);
            if (i2 < 0 || iKeyAt == i2 || iKeyAt == 1000) {
                Uid uidValueAt2 = sparseArray7.valueAt(i42);
                printWriter.print(str);
                printWriter2.print("  ");
                UserHandle.formatUid(printWriter2, iKeyAt);
                printWriter2.println(SettingsStringUtil.DELIMITER);
                int i43 = i29;
                long networkActivityBytes7 = uidValueAt2.getNetworkActivityBytes(i5, i);
                long networkActivityBytes8 = uidValueAt2.getNetworkActivityBytes(1, i);
                long networkActivityBytes9 = uidValueAt2.getNetworkActivityBytes(2, i);
                String str90 = str83;
                long networkActivityBytes10 = uidValueAt2.getNetworkActivityBytes(3, i);
                i6 = i42;
                long networkActivityBytes11 = uidValueAt2.getNetworkActivityBytes(4, i);
                long networkActivityBytes12 = uidValueAt2.getNetworkActivityBytes(5, i);
                long networkActivityPackets5 = uidValueAt2.getNetworkActivityPackets(0, i);
                long networkActivityPackets6 = uidValueAt2.getNetworkActivityPackets(1, i);
                String str91 = str11;
                long networkActivityPackets7 = uidValueAt2.getNetworkActivityPackets(2, i);
                long networkActivityPackets8 = uidValueAt2.getNetworkActivityPackets(3, i);
                long mobileRadioActiveTime2 = uidValueAt2.getMobileRadioActiveTime(i);
                int mobileRadioActiveCount = uidValueAt2.getMobileRadioActiveCount(i);
                long fullWifiLockTime = uidValueAt2.getFullWifiLockTime(j8, i);
                long wifiScanTime = uidValueAt2.getWifiScanTime(j8, i);
                int wifiScanCount = uidValueAt2.getWifiScanCount(i);
                int wifiScanBackgroundCount = uidValueAt2.getWifiScanBackgroundCount(i);
                long wifiScanActualTime = uidValueAt2.getWifiScanActualTime(j8);
                long wifiScanBackgroundTime = uidValueAt2.getWifiScanBackgroundTime(j8);
                long wifiRunningTime = uidValueAt2.getWifiRunningTime(j8, i);
                long j63 = j8;
                long mobileRadioApWakeupCount = uidValueAt2.getMobileRadioApWakeupCount(i);
                long wifiRadioApWakeupCount = uidValueAt2.getWifiRadioApWakeupCount(i);
                if (networkActivityBytes7 > 0 || networkActivityBytes8 > 0 || networkActivityPackets5 > 0 || networkActivityPackets6 > 0) {
                    printWriter.print(str);
                    printWriter3 = printWriter;
                    printWriter3.print("    Mobile network: ");
                    batteryStats = this;
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes7));
                    printWriter3.print(" received, ");
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes8));
                    printWriter3.print(" sent (packets ");
                    printWriter3.print(networkActivityPackets5);
                    printWriter3.print(" received, ");
                    printWriter3.print(networkActivityPackets6);
                    printWriter3.println(" sent)");
                } else {
                    batteryStats = this;
                    printWriter3 = printWriter;
                }
                if (mobileRadioActiveTime2 > 0 || mobileRadioActiveCount > 0) {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Mobile radio active: ");
                    long j64 = mobileRadioActiveTime2 / 1000;
                    formatTimeMs(sb, j64);
                    sb.append(str12);
                    j9 = j49;
                    sb.append(batteryStats.formatRatioLocked(mobileRadioActiveTime2, j9));
                    str16 = str14;
                    sb.append(str16);
                    sb.append(mobileRadioActiveCount);
                    str17 = str15;
                    sb.append(str17);
                    long j65 = networkActivityPackets5 + networkActivityPackets6;
                    if (j65 == 0) {
                        j65 = 1;
                    }
                    sb.append(" @ ");
                    sb.append(BatteryStatsHelper.makemAh(j64 / j65));
                    sb.append(" mspp");
                    printWriter3.println(sb.toString());
                } else {
                    j9 = j49;
                    str16 = str14;
                    str17 = str15;
                }
                if (mobileRadioApWakeupCount > 0) {
                    i7 = 0;
                    sb.setLength(0);
                    str18 = str;
                    sb.append(str18);
                    sb.append("    Mobile radio AP wakeups: ");
                    sb.append(mobileRadioApWakeupCount);
                    printWriter3.println(sb.toString());
                } else {
                    str18 = str;
                    i7 = 0;
                }
                i8 = i43;
                long j66 = j9;
                long j67 = j63;
                int i44 = i7;
                String str92 = str12;
                String str93 = str;
                String str94 = str16;
                StringBuilder sb5 = sb;
                printControllerActivityIfInteresting(printWriter, sb, str18 + "  ", CELLULAR_CONTROLLER_NAME, uidValueAt2.getModemControllerActivity(), i);
                if (networkActivityBytes9 > 0 || networkActivityBytes10 > 0) {
                    j10 = r38;
                } else {
                    j10 = networkActivityPackets7;
                    if (j10 > 0 || networkActivityPackets8 > 0) {
                    }
                    if (fullWifiLockTime != 0 && wifiScanTime == 0 && wifiScanCount == 0 && wifiScanBackgroundCount == 0 && wifiScanActualTime == 0 && wifiScanBackgroundTime == 0 && wifiRunningTime == 0) {
                        j11 = j7;
                        str19 = str94;
                    } else {
                        sb5.setLength(i44);
                        sb5.append(str93);
                        sb5.append("    Wifi Running: ");
                        formatTimeMs(sb5, wifiRunningTime / 1000);
                        sb5.append(str92);
                        long j68 = j7;
                        sb5.append(batteryStats.formatRatioLocked(wifiRunningTime, j68));
                        sb5.append(")\n");
                        sb5.append(str93);
                        sb5.append("    Full Wifi Lock: ");
                        formatTimeMs(sb5, fullWifiLockTime / 1000);
                        sb5.append(str92);
                        sb5.append(batteryStats.formatRatioLocked(fullWifiLockTime, j68));
                        sb5.append(")\n");
                        sb5.append(str93);
                        sb5.append("    Wifi Scan (blamed): ");
                        formatTimeMs(sb5, wifiScanTime / 1000);
                        sb5.append(str92);
                        sb5.append(batteryStats.formatRatioLocked(wifiScanTime, j68));
                        str19 = str94;
                        sb5.append(str19);
                        sb5.append(wifiScanCount);
                        sb5.append("x\n");
                        sb5.append(str93);
                        sb5.append("    Wifi Scan (actual): ");
                        formatTimeMs(sb5, wifiScanActualTime / 1000);
                        sb5.append(str92);
                        j11 = j68;
                        sb5.append(batteryStats.formatRatioLocked(wifiScanActualTime, batteryStats.computeBatteryRealtime(j67, i44)));
                        sb5.append(str19);
                        sb5.append(wifiScanCount);
                        sb5.append("x\n");
                        sb5.append(str93);
                        sb5.append("    Background Wifi Scan: ");
                        formatTimeMs(sb5, wifiScanBackgroundTime / 1000);
                        sb5.append(str92);
                        sb5.append(batteryStats.formatRatioLocked(wifiScanBackgroundTime, batteryStats.computeBatteryRealtime(j67, i44)));
                        sb5.append(str19);
                        sb5.append(wifiScanBackgroundCount);
                        sb5.append(str17);
                        printWriter3.println(sb5.toString());
                    }
                    if (wifiRadioApWakeupCount > 0) {
                        sb5.setLength(i44);
                        sb5.append(str93);
                        sb5.append("    WiFi AP wakeups: ");
                        sb5.append(wifiRadioApWakeupCount);
                        printWriter3.println(sb5.toString());
                    }
                    str20 = str19;
                    j12 = j11;
                    printControllerActivityIfInteresting(printWriter, sb5, str93 + "  ", WIFI_CONTROLLER_NAME, uidValueAt2.getWifiControllerActivity(), i);
                    if (networkActivityBytes11 <= 0 || networkActivityBytes12 > 0) {
                        printWriter.print(str);
                        printWriter3.print("    Bluetooth network: ");
                        printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                        printWriter3.print(" received, ");
                        printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                        printWriter3.println(" sent");
                    }
                    bluetoothScanTimer = uidValueAt2.getBluetoothScanTimer();
                    str21 = "\n";
                    str22 = " times)";
                    if (bluetoothScanTimer != null) {
                        totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j67, i) + 500) / 1000;
                        if (totalTimeLocked5 != 0) {
                            countLocked8 = bluetoothScanTimer.getCountLocked(i);
                            bluetoothScanBackgroundTimer = uidValueAt2.getBluetoothScanBackgroundTimer();
                            if (bluetoothScanBackgroundTimer != null) {
                                countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                            } else {
                                countLocked9 = 0;
                            }
                            totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j53);
                            if (bluetoothScanBackgroundTimer != null) {
                                totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j53);
                            } else {
                                totalDurationMsLocked7 = 0;
                            }
                            if (uidValueAt2.getBluetoothScanResultCounter() != null) {
                                countLocked10 = uidValueAt2.getBluetoothScanResultCounter().getCountLocked(i);
                            } else {
                                countLocked10 = 0;
                            }
                            if (uidValueAt2.getBluetoothScanResultBgCounter() != null) {
                                countLocked11 = uidValueAt2.getBluetoothScanResultBgCounter().getCountLocked(i);
                            } else {
                                countLocked11 = 0;
                            }
                            bluetoothUnoptimizedScanTimer = uidValueAt2.getBluetoothUnoptimizedScanTimer();
                            if (bluetoothUnoptimizedScanTimer != null) {
                                totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j53);
                            } else {
                                totalDurationMsLocked8 = 0;
                            }
                            if (bluetoothUnoptimizedScanTimer != null) {
                                maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j53);
                            } else {
                                maxDurationMsLocked = 0;
                            }
                            bluetoothUnoptimizedScanBackgroundTimer = uidValueAt2.getBluetoothUnoptimizedScanBackgroundTimer();
                            if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j53);
                            } else {
                                totalDurationMsLocked9 = 0;
                            }
                            if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j53);
                            } else {
                                maxDurationMsLocked2 = 0;
                            }
                            sb5.setLength(0);
                            if (totalDurationMsLocked6 != totalTimeLocked5) {
                                sb5.append(str93);
                                sb5.append("    Bluetooth Scan (total blamed realtime): ");
                                formatTimeMs(sb5, totalTimeLocked5);
                                str23 = str82;
                                sb5.append(str23);
                                sb5.append(countLocked8);
                                sb5.append(" times)");
                                if (bluetoothScanTimer.isRunningLocked()) {
                                    sb5.append(" (currently running)");
                                }
                                sb5.append("\n");
                            } else {
                                str23 = str82;
                            }
                            sb5.append(str93);
                            sb5.append("    Bluetooth Scan (total actual realtime): ");
                            formatTimeMs(sb5, totalDurationMsLocked6);
                            sb5.append(str23);
                            sb5.append(countLocked8);
                            sb5.append(" times)");
                            if (bluetoothScanTimer.isRunningLocked()) {
                                sb5.append(" (currently running)");
                            }
                            sb5.append("\n");
                            if (totalDurationMsLocked7 <= 0 || countLocked9 > 0) {
                                sb5.append(str93);
                                sb5.append("    Bluetooth Scan (background realtime): ");
                                formatTimeMs(sb5, totalDurationMsLocked7);
                                sb5.append(str23);
                                sb5.append(countLocked9);
                                sb5.append(" times)");
                                if (bluetoothScanBackgroundTimer != 0 && bluetoothScanBackgroundTimer.isRunningLocked()) {
                                    sb5.append(" (currently running in background)");
                                }
                                sb5.append("\n");
                            }
                            sb5.append(str93);
                            sb5.append("    Bluetooth Scan Results: ");
                            sb5.append(countLocked10);
                            sb5.append(str23);
                            sb5.append(countLocked11);
                            sb5.append(" in background)");
                            j31 = totalDurationMsLocked8;
                            if (j31 <= 0) {
                                j32 = totalDurationMsLocked9;
                                if (j32 > 0) {
                                    str24 = str13;
                                }
                                printWriter3 = printWriter;
                                printWriter3.println(sb5.toString());
                                z2 = true;
                            } else {
                                j32 = totalDurationMsLocked9;
                            }
                            sb5.append("\n");
                            sb5.append(str93);
                            sb5.append("    Unoptimized Bluetooth Scan (realtime): ");
                            formatTimeMs(sb5, j31);
                            sb5.append(" (max ");
                            formatTimeMs(sb5, maxDurationMsLocked);
                            str24 = str13;
                            sb5.append(str24);
                            if (bluetoothUnoptimizedScanTimer != 0 && bluetoothUnoptimizedScanTimer.isRunningLocked()) {
                                sb5.append(" (currently running unoptimized)");
                            }
                            if (bluetoothUnoptimizedScanBackgroundTimer != null && j32 > 0) {
                                sb5.append("\n");
                                sb5.append(str93);
                                sb5.append("    Unoptimized Bluetooth Scan (background realtime): ");
                                formatTimeMs(sb5, j32);
                                sb5.append(" (max ");
                                formatTimeMs(sb5, maxDurationMsLocked2);
                                sb5.append(str24);
                                if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                    sb5.append(" (currently running unoptimized in background)");
                                }
                            }
                            printWriter3 = printWriter;
                            printWriter3.println(sb5.toString());
                            z2 = true;
                        } else {
                            j67 = j67;
                            str92 = str92;
                            str17 = str17;
                            str23 = str82;
                            str24 = str13;
                            z2 = false;
                        }
                    } else {
                        j67 = j67;
                        str92 = str92;
                        str17 = str17;
                        str23 = str82;
                        str24 = str13;
                        z2 = false;
                    }
                    if (uidValueAt2.hasUserActivity()) {
                        i22 = 0;
                        z14 = false;
                        while (i22 < 4) {
                            Uid uid3 = uidValueAt2;
                            userActivityCount = uid3.getUserActivityCount(i22, i);
                            if (userActivityCount != 0) {
                                if (!z14) {
                                    sb5.setLength(0);
                                    sb5.append("    User activity: ");
                                    z14 = true;
                                } else {
                                    sb5.append(", ");
                                }
                                sb5.append(userActivityCount);
                                str51 = str91;
                                sb5.append(str51);
                                sb5.append(Uid.USER_ACTIVITY_TYPES[i22]);
                            } else {
                                str51 = str91;
                            }
                            i22++;
                            uidValueAt2 = uid3;
                            str91 = str51;
                        }
                        i9 = i;
                        uid = uidValueAt2;
                        str25 = str91;
                        if (z14) {
                            printWriter3.println(sb5.toString());
                        }
                    } else {
                        i9 = i;
                        uid = uidValueAt2;
                        str25 = str91;
                    }
                    wakelockStats = uid.getWakelockStats();
                    str26 = str25;
                    str27 = ", ";
                    jComputeWakeLock = 0;
                    jComputeWakeLock2 = 0;
                    jComputeWakeLock3 = 0;
                    jComputeWakeLock4 = 0;
                    i10 = 0;
                    boolean z22 = z2;
                    size = wakelockStats.size() - 1;
                    z3 = z22;
                    while (size >= 0) {
                        Uid.Wakelock wakelockValueAt2 = wakelockStats.valueAt(size);
                        sb5.setLength(0);
                        sb5.append(str93);
                        sb5.append("    Wake lock ");
                        sb5.append(wakelockStats.keyAt(size));
                        String str95 = str24;
                        ArrayMap<String, ? extends Uid.Wakelock> arrayMap = wakelockStats;
                        String str96 = str23;
                        int i45 = i10;
                        long j69 = j67;
                        int i46 = i9;
                        String str97 = str22;
                        String str98 = str21;
                        String strPrintWakeLock = printWakeLock(sb5, wakelockValueAt2.getWakeTime(1), j69, "full", i, ": ");
                        wakeTime = wakelockValueAt2.getWakeTime(0);
                        String strPrintWakeLock2 = printWakeLock(sb5, wakeTime, j69, Slice.HINT_PARTIAL, i, strPrintWakeLock);
                        if (wakeTime != null) {
                            subTimer4 = wakeTime.getSubTimer();
                        } else {
                            subTimer4 = null;
                        }
                        long j70 = j67;
                        printWakeLock(sb5, wakelockValueAt2.getWakeTime(18), j70, "draw", i, printWakeLock(sb5, wakelockValueAt2.getWakeTime(2), j70, Context.WINDOW_SERVICE, i, printWakeLock(sb5, subTimer4, j70, "background partial", i, strPrintWakeLock2)));
                        sb5.append(" realtime");
                        printWriter3.println(sb5.toString());
                        long j71 = j67;
                        jComputeWakeLock += computeWakeLock(wakelockValueAt2.getWakeTime(1), j71, i46);
                        jComputeWakeLock2 += computeWakeLock(wakelockValueAt2.getWakeTime(0), j71, i46);
                        jComputeWakeLock3 += computeWakeLock(wakelockValueAt2.getWakeTime(2), j71, i46);
                        jComputeWakeLock4 += computeWakeLock(wakelockValueAt2.getWakeTime(18), j71, i46);
                        size--;
                        i10 = i45 + 1;
                        i9 = i46;
                        wakelockStats = arrayMap;
                        str23 = str96;
                        str24 = str95;
                        str22 = str97;
                        str21 = str98;
                        z3 = true;
                        str93 = str;
                    }
                    boolean z23 = z3;
                    str28 = str22;
                    str29 = str21;
                    str13 = str24;
                    String str99 = str23;
                    j13 = j67;
                    j14 = jComputeWakeLock3;
                    j15 = jComputeWakeLock4;
                    j16 = jComputeWakeLock2;
                    if (i10 > 1) {
                        if (uid.getAggregatedPartialWakelockTimer() != null) {
                            Timer aggregatedPartialWakelockTimer = uid.getAggregatedPartialWakelockTimer();
                            j30 = j53;
                            long totalDurationMsLocked10 = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j30);
                            subTimer3 = aggregatedPartialWakelockTimer.getSubTimer();
                            if (subTimer3 != null) {
                                totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j30);
                            } else {
                                totalDurationMsLocked5 = 0;
                            }
                            j53 = j30;
                            j29 = totalDurationMsLocked10;
                            j28 = totalDurationMsLocked5;
                        } else {
                            j28 = 0;
                            j29 = 0;
                        }
                        if (j29 == 0 || j28 != 0 || jComputeWakeLock != 0 || j16 != 0 || j14 != 0) {
                            sb5.setLength(0);
                            sb5.append(str);
                            sb5.append("    TOTAL wake: ");
                            if (jComputeWakeLock != 0) {
                                formatTimeMs(sb5, jComputeWakeLock);
                                sb5.append("full");
                                z13 = true;
                            } else {
                                z13 = false;
                            }
                            str27 = str27;
                            if (j16 != 0) {
                                if (z13) {
                                    sb5.append(str27);
                                }
                                formatTimeMs(sb5, j16);
                                sb5.append("blamed partial");
                                z13 = true;
                            }
                            if (j29 != 0) {
                                if (z13) {
                                    sb5.append(str27);
                                }
                                formatTimeMs(sb5, j29);
                                sb5.append("actual partial");
                                z13 = true;
                            }
                            if (j28 != 0) {
                                if (z13) {
                                    sb5.append(str27);
                                }
                                formatTimeMs(sb5, j28);
                                sb5.append("actual background partial");
                                z13 = true;
                            }
                            if (j14 != 0) {
                                if (z13) {
                                    sb5.append(str27);
                                }
                                formatTimeMs(sb5, j14);
                                sb5.append(Context.WINDOW_SERVICE);
                                z13 = true;
                            }
                            if (j15 != 0) {
                                if (z13) {
                                    sb5.append(",");
                                }
                                formatTimeMs(sb5, j15);
                                sb5.append("draw");
                            }
                            sb5.append(" realtime");
                            printWriter3.println(sb5.toString());
                        }
                        multicastWakelockStats = uid.getMulticastWakelockStats();
                        if (multicastWakelockStats != null) {
                            i11 = i;
                            j17 = j13;
                            totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j17, i11);
                            countLocked7 = multicastWakelockStats.getCountLocked(i11);
                            if (totalTimeLocked4 > 0) {
                                sb5.setLength(0);
                                str30 = str;
                                sb5.append(str30);
                                sb5.append("    WiFi Multicast Wakelock");
                                sb5.append(" count = ");
                                sb5.append(countLocked7);
                                sb5.append(" time = ");
                                formatTimeMsNoSpace(sb5, (totalTimeLocked4 + 500) / 1000);
                                printWriter3.println(sb5.toString());
                            } else {
                                str30 = str;
                            }
                        } else {
                            str30 = str;
                            i11 = i;
                            j17 = j13;
                        }
                        syncStats = uid.getSyncStats();
                        size2 = syncStats.size() - 1;
                        z4 = z23;
                        while (size2 >= 0) {
                            Timer timerValueAt = syncStats.valueAt(size2);
                            totalTimeLocked3 = (timerValueAt.getTotalTimeLocked(j17, i11) + 500) / 1000;
                            countLocked5 = timerValueAt.getCountLocked(i11);
                            subTimer2 = timerValueAt.getSubTimer();
                            if (subTimer2 != null) {
                                totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j53);
                            } else {
                                totalDurationMsLocked4 = -1;
                            }
                            j27 = totalDurationMsLocked4;
                            if (subTimer2 != null) {
                                countLocked6 = subTimer2.getCountLocked(i11);
                            } else {
                                countLocked6 = i3;
                            }
                            sb5.setLength(0);
                            sb5.append(str30);
                            sb5.append("    Sync ");
                            sb5.append(syncStats.keyAt(size2));
                            String str100 = str90;
                            sb5.append(str100);
                            if (totalTimeLocked3 != 0) {
                                formatTimeMs(sb5, totalTimeLocked3);
                                sb5.append("realtime (");
                                sb5.append(countLocked5);
                                str50 = str28;
                                sb5.append(str50);
                                if (j27 > 0) {
                                    sb5.append(str27);
                                    formatTimeMs(sb5, j27);
                                    sb5.append("background (");
                                    sb5.append(countLocked6);
                                    sb5.append(str50);
                                }
                            } else {
                                str50 = str28;
                                sb5.append("(not used)");
                            }
                            printWriter3.println(sb5.toString());
                            size2--;
                            str28 = str50;
                            str90 = str100;
                            z4 = true;
                        }
                        str31 = str90;
                        j18 = j53;
                        str32 = str28;
                        jobStats = uid.getJobStats();
                        size3 = jobStats.size() - 1;
                        z5 = z4;
                        while (size3 >= 0) {
                            Timer timerValueAt2 = jobStats.valueAt(size3);
                            totalTimeLocked2 = (timerValueAt2.getTotalTimeLocked(j17, i11) + 500) / 1000;
                            long j72 = j17;
                            countLocked3 = timerValueAt2.getCountLocked(i11);
                            subTimer = timerValueAt2.getSubTimer();
                            if (subTimer != null) {
                                totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j18);
                            } else {
                                totalDurationMsLocked3 = -1;
                            }
                            long j73 = j18;
                            j26 = totalDurationMsLocked3;
                            if (subTimer != null) {
                                countLocked4 = subTimer.getCountLocked(i11);
                            } else {
                                countLocked4 = i3;
                            }
                            sb5.setLength(0);
                            sb5.append(str30);
                            sb5.append("    Job ");
                            sb5.append(jobStats.keyAt(size3));
                            sb5.append(str31);
                            if (totalTimeLocked2 != 0) {
                                formatTimeMs(sb5, totalTimeLocked2);
                                sb5.append("realtime (");
                                sb5.append(countLocked3);
                                sb5.append(str32);
                                if (j26 > 0) {
                                    sb5.append(str27);
                                    formatTimeMs(sb5, j26);
                                    sb5.append("background (");
                                    sb5.append(countLocked4);
                                    sb5.append(str32);
                                }
                            } else {
                                sb5.append("(not used)");
                            }
                            printWriter3.println(sb5.toString());
                            size3--;
                            j17 = j72;
                            j18 = j73;
                            z5 = true;
                        }
                        j19 = j17;
                        long j74 = j18;
                        jobCompletionStats = uid.getJobCompletionStats();
                        size4 = jobCompletionStats.size() - 1;
                        while (size4 >= 0) {
                            sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                            if (sparseIntArrayValueAt != null) {
                                printWriter.print(str);
                                printWriter3.print("    Job Completions ");
                                printWriter3.print(jobCompletionStats.keyAt(size4));
                                printWriter3.print(SettingsStringUtil.DELIMITER);
                                for (i21 = 0; i21 < sparseIntArrayValueAt.size(); i21++) {
                                    printWriter3.print(str26);
                                    printWriter3.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                    printWriter3.print(str92);
                                    printWriter3.print(sparseIntArrayValueAt.valueAt(i21));
                                    printWriter3.print("x)");
                                }
                                printWriter.println();
                            }
                            size4--;
                            str26 = str26;
                            str92 = str92;
                        }
                        uid2 = uid;
                        String str101 = str92;
                        String str102 = str26;
                        uid2.getDeferredJobsLineLocked(sb5, i11);
                        if (sb5.length() > 0) {
                            printWriter3.print("    Jobs deferred on launch ");
                            printWriter3.println(sb5.toString());
                        }
                        sb2 = sb5;
                        j20 = j74;
                        j21 = j66;
                        str33 = str101;
                        str34 = str99;
                        sparseArray = sparseArray7;
                        str35 = str32;
                        str36 = str31;
                        str37 = str27;
                        str38 = str102;
                        str39 = str17;
                        boolean zPrintTimer2 = z5 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j19, i, str, "Flashlight") | printTimer(printWriter, sb2, uid2.getCameraTurnedOnTimer(), j19, i, str, "Camera") | printTimer(printWriter, sb2, uid2.getVideoTurnedOnTimer(), j19, i, str, "Video") | printTimer(printWriter, sb2, uid2.getAudioTurnedOnTimer(), j19, i, str, "Audio");
                        sensorStats = uid2.getSensorStats();
                        size5 = sensorStats.size();
                        z6 = zPrintTimer2;
                        i12 = 0;
                        while (i12 < size5) {
                            sensorValueAt = sensorStats.valueAt(i12);
                            sensorStats.keyAt(i12);
                            sb2.setLength(0);
                            sb2.append(str30);
                            sb2.append("    Sensor ");
                            handle = sensorValueAt.getHandle();
                            if (handle == -10000) {
                                sb2.append("GPS");
                            } else {
                                sb2.append(handle);
                            }
                            sb2.append(str36);
                            sensorTime = sensorValueAt.getSensorTime();
                            if (sensorTime != null) {
                                j25 = j19;
                                totalTimeLocked = (sensorTime.getTotalTimeLocked(j25, i11) + 500) / 1000;
                                sparseArray2 = sensorStats;
                                countLocked = sensorTime.getCountLocked(i11);
                                sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                if (sensorBackgroundTime != null) {
                                    countLocked2 = sensorBackgroundTime.getCountLocked(i11);
                                } else {
                                    countLocked2 = 0;
                                }
                                totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j20);
                                if (sensorBackgroundTime != null) {
                                    totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j20);
                                } else {
                                    totalDurationMsLocked2 = 0;
                                }
                                if (totalTimeLocked != 0) {
                                    if (totalDurationMsLocked != totalTimeLocked) {
                                        formatTimeMs(sb2, totalTimeLocked);
                                        sb2.append("blamed realtime, ");
                                    }
                                    formatTimeMs(sb2, totalDurationMsLocked);
                                    sb2.append("realtime (");
                                    sb2.append(countLocked);
                                    str48 = str35;
                                    sb2.append(str48);
                                    if (totalDurationMsLocked2 == 0 || countLocked2 > 0) {
                                        str49 = str37;
                                        sb2.append(str49);
                                        formatTimeMs(sb2, totalDurationMsLocked2);
                                        sb2.append("background (");
                                        sb2.append(countLocked2);
                                        sb2.append(str48);
                                    } else {
                                        str49 = str37;
                                    }
                                } else {
                                    str48 = str35;
                                    str49 = str37;
                                    sb2.append("(not used)");
                                }
                            } else {
                                sparseArray2 = sensorStats;
                                str36 = str36;
                                size5 = size5;
                                j25 = j19;
                                str48 = str35;
                                str49 = str37;
                                sb2.append("(not used)");
                            }
                            printWriter.println(sb2.toString());
                            i12++;
                            size5 = size5;
                            str35 = str48;
                            str37 = str49;
                            j19 = j25;
                            z6 = true;
                            str36 = str36;
                            sensorStats = sparseArray2;
                            str30 = str;
                        }
                        str40 = str36;
                        str41 = str37;
                        printWriter4 = printWriter;
                        long j75 = j19;
                        zPrintTimer = z6 | printTimer(printWriter, sb2, uid2.getVibratorOnTimer(), j19, i, str, "Vibrator") | printTimer(printWriter, sb2, uid2.getForegroundActivityTimer(), j75, i, str, "Foreground activities") | printTimer(printWriter, sb2, uid2.getForegroundServiceTimer(), j75, i, str, "Foreground services");
                        j22 = 0;
                        i13 = 0;
                        while (i13 < 7) {
                            long j76 = j19;
                            processStateTime = uid2.getProcessStateTime(i13, j76, i11);
                            if (processStateTime > 0) {
                                j22 += processStateTime;
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("    ");
                                sb2.append(Uid.PROCESS_STATE_NAMES[i13]);
                                sb2.append(" for: ");
                                formatTimeMs(sb2, (processStateTime + 500) / 1000);
                                printWriter4.println(sb2.toString());
                                zPrintTimer = true;
                            }
                            i13++;
                            j19 = j76;
                            j20 = j20;
                        }
                        j53 = j20;
                        j23 = j19;
                        if (j22 > 0) {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Total running: ");
                            formatTimeMs(sb2, (j22 + 500) / 1000);
                            printWriter4.println(sb2.toString());
                        }
                        userCpuTimeUs = uid2.getUserCpuTimeUs(i11);
                        systemCpuTimeUs = uid2.getSystemCpuTimeUs(i11);
                        if (userCpuTimeUs <= 0 || systemCpuTimeUs > 0) {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Total cpu time: u=");
                            formatTimeMs(sb2, userCpuTimeUs / 1000);
                            sb2.append("s=");
                            formatTimeMs(sb2, systemCpuTimeUs / 1000);
                            printWriter4.println(sb2.toString());
                        }
                        cpuFreqTimes = uid2.getCpuFreqTimes(i11);
                        if (cpuFreqTimes != null) {
                            sb2.setLength(0);
                            sb2.append("    Total cpu time per freq:");
                            for (long j77 : cpuFreqTimes) {
                                sb2.append(str38 + j77);
                            }
                            printWriter4.println(sb2.toString());
                        }
                        screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i11);
                        if (screenOffCpuFreqTimes != null) {
                            sb2.setLength(0);
                            sb2.append("    Total screen-off cpu time per freq:");
                            for (long j78 : screenOffCpuFreqTimes) {
                                sb2.append(str38 + j78);
                            }
                            printWriter4.println(sb2.toString());
                        }
                        i14 = 0;
                        while (i14 < 7) {
                            cpuFreqTimes2 = uid2.getCpuFreqTimes(i11, i14);
                            if (cpuFreqTimes2 != null) {
                                sb2.setLength(0);
                                sb2.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                i18 = 0;
                                while (i18 < cpuFreqTimes2.length) {
                                    sb2.append(str38 + cpuFreqTimes2[i18]);
                                    i18++;
                                    zPrintTimer = zPrintTimer;
                                }
                                z12 = zPrintTimer;
                                printWriter4.println(sb2.toString());
                            } else {
                                z12 = zPrintTimer;
                            }
                            screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(i11, i14);
                            if (screenOffCpuFreqTimes2 != null) {
                                sb2.setLength(0);
                                sb2.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                for (long j79 : screenOffCpuFreqTimes2) {
                                    sb2.append(str38 + j79);
                                }
                                printWriter4.println(sb2.toString());
                            }
                            i14++;
                            zPrintTimer = z12;
                        }
                        processStats = uid2.getProcessStats();
                        size6 = processStats.size() - 1;
                        while (size6 >= 0) {
                            procValueAt = processStats.valueAt(size6);
                            userTime = procValueAt.getUserTime(i11);
                            boolean z24 = zPrintTimer;
                            long j80 = j23;
                            systemTime = procValueAt.getSystemTime(i11);
                            foregroundTime = procValueAt.getForegroundTime(i11);
                            String str103 = str38;
                            starts2 = procValueAt.getStarts(i11);
                            Uid uid4 = uid2;
                            numCrashes = procValueAt.getNumCrashes(i11);
                            numAnrs = procValueAt.getNumAnrs(i11);
                            if (i11 == 0) {
                                iCountExcessivePowers = procValueAt.countExcessivePowers();
                            } else {
                                iCountExcessivePowers = 0;
                            }
                            if (userTime != 0 && systemTime == 0 && foregroundTime == 0 && starts2 == 0 && iCountExcessivePowers == 0 && numCrashes == 0 && numAnrs == 0) {
                                z11 = z24;
                                printWriter4 = printWriter;
                                str46 = str34;
                            } else {
                                proc = procValueAt;
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("    Proc ");
                                sb2.append(processStats.keyAt(size6));
                                sb2.append(":\n");
                                sb2.append(str);
                                sb2.append("      CPU: ");
                                formatTimeMs(sb2, userTime);
                                sb2.append("usr + ");
                                formatTimeMs(sb2, systemTime);
                                sb2.append("krn ; ");
                                formatTimeMs(sb2, foregroundTime);
                                sb2.append(FOREGROUND_ACTIVITY_DATA);
                                if (starts2 != 0 && numCrashes == 0 && numAnrs == 0) {
                                    str45 = str29;
                                } else {
                                    str45 = str29;
                                    sb2.append(str45);
                                    sb2.append(str);
                                    sb2.append("      ");
                                    if (starts2 != 0) {
                                        sb2.append(starts2);
                                        sb2.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb2.append(str41);
                                        }
                                        sb2.append(numCrashes);
                                        sb2.append(" crashes");
                                        z10 = true;
                                    } else {
                                        z10 = z9;
                                    }
                                    if (numAnrs != 0) {
                                        if (z10) {
                                            sb2.append(str41);
                                        }
                                        sb2.append(numAnrs);
                                        sb2.append(" anrs");
                                    }
                                }
                                printWriter4 = printWriter;
                                printWriter4.println(sb2.toString());
                                i16 = 0;
                                while (i16 < iCountExcessivePowers) {
                                    proc = proc;
                                    excessivePower = proc.getExcessivePower(i16);
                                    if (excessivePower != null) {
                                        printWriter.print(str);
                                        printWriter4.print("      * Killed for ");
                                        if (excessivePower.type == 2) {
                                            printWriter4.print(CPU_DATA);
                                        } else {
                                            printWriter4.print("unknown");
                                        }
                                        printWriter4.print(" use: ");
                                        TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                        printWriter4.print(" over ");
                                        TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                        if (excessivePower.overTime != 0) {
                                            str47 = str34;
                                            printWriter4.print(str47);
                                            printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                            printWriter4.println("%)");
                                        } else {
                                            str47 = str34;
                                        }
                                    } else {
                                        str47 = str34;
                                    }
                                    i16++;
                                    str34 = str47;
                                    str45 = str45;
                                }
                                str29 = str45;
                                str46 = str34;
                                z11 = true;
                            }
                            size6--;
                            uid2 = uid4;
                            zPrintTimer = z11;
                            str34 = str46;
                            j23 = j80;
                            str38 = str103;
                            i11 = i;
                        }
                        str42 = str38;
                        j24 = j23;
                        str43 = str34;
                        packageStats = uid2.getPackageStats();
                        size7 = packageStats.size() - 1;
                        z7 = zPrintTimer;
                        while (size7 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("    Apk ");
                            printWriter4.print(packageStats.keyAt(size7));
                            printWriter4.println(SettingsStringUtil.DELIMITER);
                            Uid.Pkg pkgValueAt = packageStats.valueAt(size7);
                            wakeupAlarmStats = pkgValueAt.getWakeupAlarmStats();
                            size8 = wakeupAlarmStats.size() - 1;
                            z8 = false;
                            while (size8 >= 0) {
                                printWriter.print(str);
                                printWriter4.print("      Wakeup alarm ");
                                printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                printWriter4.print(str40);
                                printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                printWriter4.println(" times");
                                size8--;
                                z8 = true;
                            }
                            String str104 = str40;
                            serviceStats = pkgValueAt.getServiceStats();
                            for (size9 = serviceStats.size() - 1; size9 >= 0; size9--) {
                                Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size9);
                                j52 = j52;
                                startTime = servValueAt.getStartTime(j52, i);
                                starts = servValueAt.getStarts(i);
                                launches = servValueAt.getLaunches(i);
                                if (startTime == 0 || starts != 0 || launches != 0) {
                                    sb2.setLength(0);
                                    sb2.append(str);
                                    sb2.append("      Service ");
                                    sb2.append(serviceStats.keyAt(size9));
                                    sb2.append(":\n");
                                    sb2.append(str);
                                    sb2.append("        Created for: ");
                                    formatTimeMs(sb2, startTime / 1000);
                                    sb2.append("uptime\n");
                                    sb2.append(str);
                                    sb2.append("        Starts: ");
                                    sb2.append(starts);
                                    sb2.append(", launches: ");
                                    sb2.append(launches);
                                    printWriter4.println(sb2.toString());
                                    z8 = true;
                                }
                            }
                            if (!z8) {
                                printWriter.print(str);
                                printWriter4.println("      (nothing executed)");
                            }
                            size7--;
                            str40 = str104;
                            z7 = true;
                        }
                        str44 = str40;
                        i15 = 0;
                        if (!z7) {
                            printWriter.print(str);
                            printWriter4.println("    (nothing executed)");
                        }
                    } else {
                        j13 = j13;
                    }
                    multicastWakelockStats = uid.getMulticastWakelockStats();
                    if (multicastWakelockStats != null) {
                        i11 = i;
                        j17 = j13;
                        totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j17, i11);
                        countLocked7 = multicastWakelockStats.getCountLocked(i11);
                        if (totalTimeLocked4 > 0) {
                            sb5.setLength(0);
                            str30 = str;
                            sb5.append(str30);
                            sb5.append("    WiFi Multicast Wakelock");
                            sb5.append(" count = ");
                            sb5.append(countLocked7);
                            sb5.append(" time = ");
                            formatTimeMsNoSpace(sb5, (totalTimeLocked4 + 500) / 1000);
                            printWriter3.println(sb5.toString());
                        } else {
                            str30 = str;
                        }
                    } else {
                        str30 = str;
                        i11 = i;
                        j17 = j13;
                    }
                    syncStats = uid.getSyncStats();
                    size2 = syncStats.size() - 1;
                    z4 = z23;
                    while (size2 >= 0) {
                        Timer timerValueAt3 = syncStats.valueAt(size2);
                        totalTimeLocked3 = (timerValueAt3.getTotalTimeLocked(j17, i11) + 500) / 1000;
                        countLocked5 = timerValueAt3.getCountLocked(i11);
                        subTimer2 = timerValueAt3.getSubTimer();
                        if (subTimer2 != null) {
                            totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j53);
                        } else {
                            totalDurationMsLocked4 = -1;
                        }
                        j27 = totalDurationMsLocked4;
                        if (subTimer2 != null) {
                            countLocked6 = subTimer2.getCountLocked(i11);
                        } else {
                            countLocked6 = i3;
                        }
                        sb5.setLength(0);
                        sb5.append(str30);
                        sb5.append("    Sync ");
                        sb5.append(syncStats.keyAt(size2));
                        String str105 = str90;
                        sb5.append(str105);
                        if (totalTimeLocked3 != 0) {
                            formatTimeMs(sb5, totalTimeLocked3);
                            sb5.append("realtime (");
                            sb5.append(countLocked5);
                            str50 = str28;
                            sb5.append(str50);
                            if (j27 > 0) {
                                sb5.append(str27);
                                formatTimeMs(sb5, j27);
                                sb5.append("background (");
                                sb5.append(countLocked6);
                                sb5.append(str50);
                            }
                        } else {
                            str50 = str28;
                            sb5.append("(not used)");
                        }
                        printWriter3.println(sb5.toString());
                        size2--;
                        str28 = str50;
                        str90 = str105;
                        z4 = true;
                    }
                    str31 = str90;
                    j18 = j53;
                    str32 = str28;
                    jobStats = uid.getJobStats();
                    size3 = jobStats.size() - 1;
                    z5 = z4;
                    while (size3 >= 0) {
                        Timer timerValueAt4 = jobStats.valueAt(size3);
                        totalTimeLocked2 = (timerValueAt4.getTotalTimeLocked(j17, i11) + 500) / 1000;
                        long j710 = j17;
                        countLocked3 = timerValueAt4.getCountLocked(i11);
                        subTimer = timerValueAt4.getSubTimer();
                        if (subTimer != null) {
                            totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j18);
                        } else {
                            totalDurationMsLocked3 = -1;
                        }
                        long j711 = j18;
                        j26 = totalDurationMsLocked3;
                        if (subTimer != null) {
                            countLocked4 = subTimer.getCountLocked(i11);
                        } else {
                            countLocked4 = i3;
                        }
                        sb5.setLength(0);
                        sb5.append(str30);
                        sb5.append("    Job ");
                        sb5.append(jobStats.keyAt(size3));
                        sb5.append(str31);
                        if (totalTimeLocked2 != 0) {
                            formatTimeMs(sb5, totalTimeLocked2);
                            sb5.append("realtime (");
                            sb5.append(countLocked3);
                            sb5.append(str32);
                            if (j26 > 0) {
                                sb5.append(str27);
                                formatTimeMs(sb5, j26);
                                sb5.append("background (");
                                sb5.append(countLocked4);
                                sb5.append(str32);
                            }
                        } else {
                            sb5.append("(not used)");
                        }
                        printWriter3.println(sb5.toString());
                        size3--;
                        j17 = j710;
                        j18 = j711;
                        z5 = true;
                    }
                    j19 = j17;
                    long j712 = j18;
                    jobCompletionStats = uid.getJobCompletionStats();
                    size4 = jobCompletionStats.size() - 1;
                    while (size4 >= 0) {
                        sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                        if (sparseIntArrayValueAt != null) {
                            printWriter.print(str);
                            printWriter3.print("    Job Completions ");
                            printWriter3.print(jobCompletionStats.keyAt(size4));
                            printWriter3.print(SettingsStringUtil.DELIMITER);
                            while (i21 < sparseIntArrayValueAt.size()) {
                                printWriter3.print(str26);
                                printWriter3.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                printWriter3.print(str92);
                                printWriter3.print(sparseIntArrayValueAt.valueAt(i21));
                                printWriter3.print("x)");
                            }
                            printWriter.println();
                        }
                        size4--;
                        str26 = str26;
                        str92 = str92;
                    }
                    uid2 = uid;
                    String str106 = str92;
                    String str107 = str26;
                    uid2.getDeferredJobsLineLocked(sb5, i11);
                    if (sb5.length() > 0) {
                        printWriter3.print("    Jobs deferred on launch ");
                        printWriter3.println(sb5.toString());
                    }
                    sb2 = sb5;
                    j20 = j712;
                    j21 = j66;
                    str33 = str106;
                    str34 = str99;
                    sparseArray = sparseArray7;
                    str35 = str32;
                    str36 = str31;
                    str37 = str27;
                    str38 = str107;
                    str39 = str17;
                    boolean zPrintTimer3 = z5 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j19, i, str, "Flashlight") | printTimer(printWriter, sb2, uid2.getCameraTurnedOnTimer(), j19, i, str, "Camera") | printTimer(printWriter, sb2, uid2.getVideoTurnedOnTimer(), j19, i, str, "Video") | printTimer(printWriter, sb2, uid2.getAudioTurnedOnTimer(), j19, i, str, "Audio");
                    sensorStats = uid2.getSensorStats();
                    size5 = sensorStats.size();
                    z6 = zPrintTimer3;
                    i12 = 0;
                    while (i12 < size5) {
                        sensorValueAt = sensorStats.valueAt(i12);
                        sensorStats.keyAt(i12);
                        sb2.setLength(0);
                        sb2.append(str30);
                        sb2.append("    Sensor ");
                        handle = sensorValueAt.getHandle();
                        if (handle == -10000) {
                            sb2.append("GPS");
                        } else {
                            sb2.append(handle);
                        }
                        sb2.append(str36);
                        sensorTime = sensorValueAt.getSensorTime();
                        if (sensorTime != null) {
                            j25 = j19;
                            totalTimeLocked = (sensorTime.getTotalTimeLocked(j25, i11) + 500) / 1000;
                            sparseArray2 = sensorStats;
                            countLocked = sensorTime.getCountLocked(i11);
                            sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            if (sensorBackgroundTime != null) {
                                countLocked2 = sensorBackgroundTime.getCountLocked(i11);
                            } else {
                                countLocked2 = 0;
                            }
                            totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j20);
                            if (sensorBackgroundTime != null) {
                                totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j20);
                            } else {
                                totalDurationMsLocked2 = 0;
                            }
                            if (totalTimeLocked != 0) {
                                if (totalDurationMsLocked != totalTimeLocked) {
                                    formatTimeMs(sb2, totalTimeLocked);
                                    sb2.append("blamed realtime, ");
                                }
                                formatTimeMs(sb2, totalDurationMsLocked);
                                sb2.append("realtime (");
                                sb2.append(countLocked);
                                str48 = str35;
                                sb2.append(str48);
                                if (totalDurationMsLocked2 == 0) {
                                    str49 = str37;
                                    sb2.append(str49);
                                    formatTimeMs(sb2, totalDurationMsLocked2);
                                    sb2.append("background (");
                                    sb2.append(countLocked2);
                                    sb2.append(str48);
                                } else {
                                    str49 = str37;
                                    sb2.append(str49);
                                    formatTimeMs(sb2, totalDurationMsLocked2);
                                    sb2.append("background (");
                                    sb2.append(countLocked2);
                                    sb2.append(str48);
                                }
                            } else {
                                str48 = str35;
                                str49 = str37;
                                sb2.append("(not used)");
                            }
                        } else {
                            sparseArray2 = sensorStats;
                            str36 = str36;
                            size5 = size5;
                            j25 = j19;
                            str48 = str35;
                            str49 = str37;
                            sb2.append("(not used)");
                        }
                        printWriter.println(sb2.toString());
                        i12++;
                        size5 = size5;
                        str35 = str48;
                        str37 = str49;
                        j19 = j25;
                        z6 = true;
                        str36 = str36;
                        sensorStats = sparseArray2;
                        str30 = str;
                    }
                    str40 = str36;
                    str41 = str37;
                    printWriter4 = printWriter;
                    long j713 = j19;
                    zPrintTimer = z6 | printTimer(printWriter, sb2, uid2.getVibratorOnTimer(), j19, i, str, "Vibrator") | printTimer(printWriter, sb2, uid2.getForegroundActivityTimer(), j713, i, str, "Foreground activities") | printTimer(printWriter, sb2, uid2.getForegroundServiceTimer(), j713, i, str, "Foreground services");
                    j22 = 0;
                    i13 = 0;
                    while (i13 < 7) {
                        long j714 = j19;
                        processStateTime = uid2.getProcessStateTime(i13, j714, i11);
                        if (processStateTime > 0) {
                            j22 += processStateTime;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    ");
                            sb2.append(Uid.PROCESS_STATE_NAMES[i13]);
                            sb2.append(" for: ");
                            formatTimeMs(sb2, (processStateTime + 500) / 1000);
                            printWriter4.println(sb2.toString());
                            zPrintTimer = true;
                        }
                        i13++;
                        j19 = j714;
                        j20 = j20;
                    }
                    j53 = j20;
                    j23 = j19;
                    if (j22 > 0) {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total running: ");
                        formatTimeMs(sb2, (j22 + 500) / 1000);
                        printWriter4.println(sb2.toString());
                    }
                    userCpuTimeUs = uid2.getUserCpuTimeUs(i11);
                    systemCpuTimeUs = uid2.getSystemCpuTimeUs(i11);
                    if (userCpuTimeUs <= 0) {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total cpu time: u=");
                        formatTimeMs(sb2, userCpuTimeUs / 1000);
                        sb2.append("s=");
                        formatTimeMs(sb2, systemCpuTimeUs / 1000);
                        printWriter4.println(sb2.toString());
                    } else {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total cpu time: u=");
                        formatTimeMs(sb2, userCpuTimeUs / 1000);
                        sb2.append("s=");
                        formatTimeMs(sb2, systemCpuTimeUs / 1000);
                        printWriter4.println(sb2.toString());
                    }
                    cpuFreqTimes = uid2.getCpuFreqTimes(i11);
                    if (cpuFreqTimes != null) {
                        sb2.setLength(0);
                        sb2.append("    Total cpu time per freq:");
                        while (i20 < cpuFreqTimes.length) {
                            sb2.append(str38 + j77);
                        }
                        printWriter4.println(sb2.toString());
                    }
                    screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i11);
                    if (screenOffCpuFreqTimes != null) {
                        sb2.setLength(0);
                        sb2.append("    Total screen-off cpu time per freq:");
                        while (i19 < screenOffCpuFreqTimes.length) {
                            sb2.append(str38 + j78);
                        }
                        printWriter4.println(sb2.toString());
                    }
                    i14 = 0;
                    while (i14 < 7) {
                        cpuFreqTimes2 = uid2.getCpuFreqTimes(i11, i14);
                        if (cpuFreqTimes2 != null) {
                            sb2.setLength(0);
                            sb2.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            i18 = 0;
                            while (i18 < cpuFreqTimes2.length) {
                                sb2.append(str38 + cpuFreqTimes2[i18]);
                                i18++;
                                zPrintTimer = zPrintTimer;
                            }
                            z12 = zPrintTimer;
                            printWriter4.println(sb2.toString());
                        } else {
                            z12 = zPrintTimer;
                        }
                        screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(i11, i14);
                        if (screenOffCpuFreqTimes2 != null) {
                            sb2.setLength(0);
                            sb2.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            while (i17 < screenOffCpuFreqTimes2.length) {
                                sb2.append(str38 + j79);
                            }
                            printWriter4.println(sb2.toString());
                        }
                        i14++;
                        zPrintTimer = z12;
                    }
                    processStats = uid2.getProcessStats();
                    size6 = processStats.size() - 1;
                    while (size6 >= 0) {
                        procValueAt = processStats.valueAt(size6);
                        userTime = procValueAt.getUserTime(i11);
                        boolean z25 = zPrintTimer;
                        long j81 = j23;
                        systemTime = procValueAt.getSystemTime(i11);
                        foregroundTime = procValueAt.getForegroundTime(i11);
                        String str108 = str38;
                        starts2 = procValueAt.getStarts(i11);
                        Uid uid5 = uid2;
                        numCrashes = procValueAt.getNumCrashes(i11);
                        numAnrs = procValueAt.getNumAnrs(i11);
                        if (i11 == 0) {
                            iCountExcessivePowers = procValueAt.countExcessivePowers();
                        } else {
                            iCountExcessivePowers = 0;
                        }
                        if (userTime != 0) {
                            proc = procValueAt;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Proc ");
                            sb2.append(processStats.keyAt(size6));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("      CPU: ");
                            formatTimeMs(sb2, userTime);
                            sb2.append("usr + ");
                            formatTimeMs(sb2, systemTime);
                            sb2.append("krn ; ");
                            formatTimeMs(sb2, foregroundTime);
                            sb2.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 != 0) {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            } else {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            }
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                proc = proc;
                                excessivePower = proc.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        str47 = str34;
                                        printWriter4.print(str47);
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    } else {
                                        str47 = str34;
                                    }
                                } else {
                                    str47 = str34;
                                }
                                i16++;
                                str34 = str47;
                                str45 = str45;
                            }
                            str29 = str45;
                            str46 = str34;
                            z11 = true;
                        } else {
                            proc = procValueAt;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Proc ");
                            sb2.append(processStats.keyAt(size6));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("      CPU: ");
                            formatTimeMs(sb2, userTime);
                            sb2.append("usr + ");
                            formatTimeMs(sb2, systemTime);
                            sb2.append("krn ; ");
                            formatTimeMs(sb2, foregroundTime);
                            sb2.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 != 0) {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            } else {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            }
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                proc = proc;
                                excessivePower = proc.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        str47 = str34;
                                        printWriter4.print(str47);
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    } else {
                                        str47 = str34;
                                    }
                                } else {
                                    str47 = str34;
                                }
                                i16++;
                                str34 = str47;
                                str45 = str45;
                            }
                            str29 = str45;
                            str46 = str34;
                            z11 = true;
                        }
                        size6--;
                        uid2 = uid5;
                        zPrintTimer = z11;
                        str34 = str46;
                        j23 = j81;
                        str38 = str108;
                        i11 = i;
                    }
                    str42 = str38;
                    j24 = j23;
                    str43 = str34;
                    packageStats = uid2.getPackageStats();
                    size7 = packageStats.size() - 1;
                    z7 = zPrintTimer;
                    while (size7 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("    Apk ");
                        printWriter4.print(packageStats.keyAt(size7));
                        printWriter4.println(SettingsStringUtil.DELIMITER);
                        Uid.Pkg pkgValueAt2 = packageStats.valueAt(size7);
                        wakeupAlarmStats = pkgValueAt2.getWakeupAlarmStats();
                        size8 = wakeupAlarmStats.size() - 1;
                        z8 = false;
                        while (size8 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("      Wakeup alarm ");
                            printWriter4.print(wakeupAlarmStats.keyAt(size8));
                            printWriter4.print(str40);
                            printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                            printWriter4.println(" times");
                            size8--;
                            z8 = true;
                        }
                        String str109 = str40;
                        serviceStats = pkgValueAt2.getServiceStats();
                        while (size9 >= 0) {
                            Uid.Pkg.Serv servValueAt2 = serviceStats.valueAt(size9);
                            j52 = j52;
                            startTime = servValueAt2.getStartTime(j52, i);
                            starts = servValueAt2.getStarts(i);
                            launches = servValueAt2.getLaunches(i);
                            if (startTime == 0) {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("      Service ");
                                sb2.append(serviceStats.keyAt(size9));
                                sb2.append(":\n");
                                sb2.append(str);
                                sb2.append("        Created for: ");
                                formatTimeMs(sb2, startTime / 1000);
                                sb2.append("uptime\n");
                                sb2.append(str);
                                sb2.append("        Starts: ");
                                sb2.append(starts);
                                sb2.append(", launches: ");
                                sb2.append(launches);
                                printWriter4.println(sb2.toString());
                                z8 = true;
                            } else {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("      Service ");
                                sb2.append(serviceStats.keyAt(size9));
                                sb2.append(":\n");
                                sb2.append(str);
                                sb2.append("        Created for: ");
                                formatTimeMs(sb2, startTime / 1000);
                                sb2.append("uptime\n");
                                sb2.append(str);
                                sb2.append("        Starts: ");
                                sb2.append(starts);
                                sb2.append(", launches: ");
                                sb2.append(launches);
                                printWriter4.println(sb2.toString());
                                z8 = true;
                            }
                        }
                        if (!z8) {
                            printWriter.print(str);
                            printWriter4.println("      (nothing executed)");
                        }
                        size7--;
                        str40 = str109;
                        z7 = true;
                    }
                    str44 = str40;
                    i15 = 0;
                    if (!z7) {
                        printWriter.print(str);
                        printWriter4.println("    (nothing executed)");
                    }
                }
                printWriter.print(str);
                printWriter3.print("    Wi-Fi network: ");
                printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes9));
                printWriter3.print(" received, ");
                printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes10));
                printWriter3.print(" sent (packets ");
                printWriter3.print(j10);
                printWriter3.print(" received, ");
                printWriter3.print(networkActivityPackets8);
                printWriter3.println(" sent)");
                if (fullWifiLockTime != 0) {
                    sb5.setLength(i44);
                    sb5.append(str93);
                    sb5.append("    Wifi Running: ");
                    formatTimeMs(sb5, wifiRunningTime / 1000);
                    sb5.append(str92);
                    long j610 = j7;
                    sb5.append(batteryStats.formatRatioLocked(wifiRunningTime, j610));
                    sb5.append(")\n");
                    sb5.append(str93);
                    sb5.append("    Full Wifi Lock: ");
                    formatTimeMs(sb5, fullWifiLockTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(fullWifiLockTime, j610));
                    sb5.append(")\n");
                    sb5.append(str93);
                    sb5.append("    Wifi Scan (blamed): ");
                    formatTimeMs(sb5, wifiScanTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(wifiScanTime, j610));
                    str19 = str94;
                    sb5.append(str19);
                    sb5.append(wifiScanCount);
                    sb5.append("x\n");
                    sb5.append(str93);
                    sb5.append("    Wifi Scan (actual): ");
                    formatTimeMs(sb5, wifiScanActualTime / 1000);
                    sb5.append(str92);
                    j11 = j610;
                    sb5.append(batteryStats.formatRatioLocked(wifiScanActualTime, batteryStats.computeBatteryRealtime(j67, i44)));
                    sb5.append(str19);
                    sb5.append(wifiScanCount);
                    sb5.append("x\n");
                    sb5.append(str93);
                    sb5.append("    Background Wifi Scan: ");
                    formatTimeMs(sb5, wifiScanBackgroundTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(wifiScanBackgroundTime, batteryStats.computeBatteryRealtime(j67, i44)));
                    sb5.append(str19);
                    sb5.append(wifiScanBackgroundCount);
                    sb5.append(str17);
                    printWriter3.println(sb5.toString());
                } else {
                    sb5.setLength(i44);
                    sb5.append(str93);
                    sb5.append("    Wifi Running: ");
                    formatTimeMs(sb5, wifiRunningTime / 1000);
                    sb5.append(str92);
                    long j611 = j7;
                    sb5.append(batteryStats.formatRatioLocked(wifiRunningTime, j611));
                    sb5.append(")\n");
                    sb5.append(str93);
                    sb5.append("    Full Wifi Lock: ");
                    formatTimeMs(sb5, fullWifiLockTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(fullWifiLockTime, j611));
                    sb5.append(")\n");
                    sb5.append(str93);
                    sb5.append("    Wifi Scan (blamed): ");
                    formatTimeMs(sb5, wifiScanTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(wifiScanTime, j611));
                    str19 = str94;
                    sb5.append(str19);
                    sb5.append(wifiScanCount);
                    sb5.append("x\n");
                    sb5.append(str93);
                    sb5.append("    Wifi Scan (actual): ");
                    formatTimeMs(sb5, wifiScanActualTime / 1000);
                    sb5.append(str92);
                    j11 = j611;
                    sb5.append(batteryStats.formatRatioLocked(wifiScanActualTime, batteryStats.computeBatteryRealtime(j67, i44)));
                    sb5.append(str19);
                    sb5.append(wifiScanCount);
                    sb5.append("x\n");
                    sb5.append(str93);
                    sb5.append("    Background Wifi Scan: ");
                    formatTimeMs(sb5, wifiScanBackgroundTime / 1000);
                    sb5.append(str92);
                    sb5.append(batteryStats.formatRatioLocked(wifiScanBackgroundTime, batteryStats.computeBatteryRealtime(j67, i44)));
                    sb5.append(str19);
                    sb5.append(wifiScanBackgroundCount);
                    sb5.append(str17);
                    printWriter3.println(sb5.toString());
                }
                if (wifiRadioApWakeupCount > 0) {
                    sb5.setLength(i44);
                    sb5.append(str93);
                    sb5.append("    WiFi AP wakeups: ");
                    sb5.append(wifiRadioApWakeupCount);
                    printWriter3.println(sb5.toString());
                }
                str20 = str19;
                j12 = j11;
                printControllerActivityIfInteresting(printWriter, sb5, str93 + "  ", WIFI_CONTROLLER_NAME, uidValueAt2.getWifiControllerActivity(), i);
                if (networkActivityBytes11 <= 0) {
                    printWriter.print(str);
                    printWriter3.print("    Bluetooth network: ");
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                    printWriter3.print(" received, ");
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                    printWriter3.println(" sent");
                } else {
                    printWriter.print(str);
                    printWriter3.print("    Bluetooth network: ");
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                    printWriter3.print(" received, ");
                    printWriter3.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                    printWriter3.println(" sent");
                }
                bluetoothScanTimer = uidValueAt2.getBluetoothScanTimer();
                str21 = "\n";
                str22 = " times)";
                if (bluetoothScanTimer != null) {
                    totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j67, i) + 500) / 1000;
                    if (totalTimeLocked5 != 0) {
                        countLocked8 = bluetoothScanTimer.getCountLocked(i);
                        bluetoothScanBackgroundTimer = uidValueAt2.getBluetoothScanBackgroundTimer();
                        if (bluetoothScanBackgroundTimer != null) {
                            countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                        } else {
                            countLocked9 = 0;
                        }
                        totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j53);
                        if (bluetoothScanBackgroundTimer != null) {
                            totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j53);
                        } else {
                            totalDurationMsLocked7 = 0;
                        }
                        if (uidValueAt2.getBluetoothScanResultCounter() != null) {
                            countLocked10 = uidValueAt2.getBluetoothScanResultCounter().getCountLocked(i);
                        } else {
                            countLocked10 = 0;
                        }
                        if (uidValueAt2.getBluetoothScanResultBgCounter() != null) {
                            countLocked11 = uidValueAt2.getBluetoothScanResultBgCounter().getCountLocked(i);
                        } else {
                            countLocked11 = 0;
                        }
                        bluetoothUnoptimizedScanTimer = uidValueAt2.getBluetoothUnoptimizedScanTimer();
                        if (bluetoothUnoptimizedScanTimer != null) {
                            totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j53);
                        } else {
                            totalDurationMsLocked8 = 0;
                        }
                        if (bluetoothUnoptimizedScanTimer != null) {
                            maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j53);
                        } else {
                            maxDurationMsLocked = 0;
                        }
                        bluetoothUnoptimizedScanBackgroundTimer = uidValueAt2.getBluetoothUnoptimizedScanBackgroundTimer();
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j53);
                        } else {
                            totalDurationMsLocked9 = 0;
                        }
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j53);
                        } else {
                            maxDurationMsLocked2 = 0;
                        }
                        sb5.setLength(0);
                        if (totalDurationMsLocked6 != totalTimeLocked5) {
                            sb5.append(str93);
                            sb5.append("    Bluetooth Scan (total blamed realtime): ");
                            formatTimeMs(sb5, totalTimeLocked5);
                            str23 = str82;
                            sb5.append(str23);
                            sb5.append(countLocked8);
                            sb5.append(" times)");
                            if (bluetoothScanTimer.isRunningLocked()) {
                                sb5.append(" (currently running)");
                            }
                            sb5.append("\n");
                        } else {
                            str23 = str82;
                        }
                        sb5.append(str93);
                        sb5.append("    Bluetooth Scan (total actual realtime): ");
                        formatTimeMs(sb5, totalDurationMsLocked6);
                        sb5.append(str23);
                        sb5.append(countLocked8);
                        sb5.append(" times)");
                        if (bluetoothScanTimer.isRunningLocked()) {
                            sb5.append(" (currently running)");
                        }
                        sb5.append("\n");
                        if (totalDurationMsLocked7 <= 0) {
                            sb5.append(str93);
                            sb5.append("    Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb5, totalDurationMsLocked7);
                            sb5.append(str23);
                            sb5.append(countLocked9);
                            sb5.append(" times)");
                            if (bluetoothScanBackgroundTimer != 0) {
                                sb5.append(" (currently running in background)");
                            }
                            sb5.append("\n");
                        } else {
                            sb5.append(str93);
                            sb5.append("    Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb5, totalDurationMsLocked7);
                            sb5.append(str23);
                            sb5.append(countLocked9);
                            sb5.append(" times)");
                            if (bluetoothScanBackgroundTimer != 0) {
                                sb5.append(" (currently running in background)");
                            }
                            sb5.append("\n");
                        }
                        sb5.append(str93);
                        sb5.append("    Bluetooth Scan Results: ");
                        sb5.append(countLocked10);
                        sb5.append(str23);
                        sb5.append(countLocked11);
                        sb5.append(" in background)");
                        j31 = totalDurationMsLocked8;
                        if (j31 <= 0) {
                            j32 = totalDurationMsLocked9;
                            if (j32 > 0) {
                                str24 = str13;
                            }
                            printWriter3 = printWriter;
                            printWriter3.println(sb5.toString());
                            z2 = true;
                        } else {
                            j32 = totalDurationMsLocked9;
                        }
                        sb5.append("\n");
                        sb5.append(str93);
                        sb5.append("    Unoptimized Bluetooth Scan (realtime): ");
                        formatTimeMs(sb5, j31);
                        sb5.append(" (max ");
                        formatTimeMs(sb5, maxDurationMsLocked);
                        str24 = str13;
                        sb5.append(str24);
                        if (bluetoothUnoptimizedScanTimer != 0) {
                            sb5.append(" (currently running unoptimized)");
                        }
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            sb5.append("\n");
                            sb5.append(str93);
                            sb5.append("    Unoptimized Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb5, j32);
                            sb5.append(" (max ");
                            formatTimeMs(sb5, maxDurationMsLocked2);
                            sb5.append(str24);
                            if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                sb5.append(" (currently running unoptimized in background)");
                            }
                        }
                        printWriter3 = printWriter;
                        printWriter3.println(sb5.toString());
                        z2 = true;
                    } else {
                        j67 = j67;
                        str92 = str92;
                        str17 = str17;
                        str23 = str82;
                        str24 = str13;
                        z2 = false;
                    }
                } else {
                    j67 = j67;
                    str92 = str92;
                    str17 = str17;
                    str23 = str82;
                    str24 = str13;
                    z2 = false;
                }
                if (uidValueAt2.hasUserActivity()) {
                    i22 = 0;
                    z14 = false;
                    while (i22 < 4) {
                        Uid uid6 = uidValueAt2;
                        userActivityCount = uid6.getUserActivityCount(i22, i);
                        if (userActivityCount != 0) {
                            if (!z14) {
                                sb5.setLength(0);
                                sb5.append("    User activity: ");
                                z14 = true;
                            } else {
                                sb5.append(", ");
                            }
                            sb5.append(userActivityCount);
                            str51 = str91;
                            sb5.append(str51);
                            sb5.append(Uid.USER_ACTIVITY_TYPES[i22]);
                        } else {
                            str51 = str91;
                        }
                        i22++;
                        uidValueAt2 = uid6;
                        str91 = str51;
                    }
                    i9 = i;
                    uid = uidValueAt2;
                    str25 = str91;
                    if (z14) {
                        printWriter3.println(sb5.toString());
                    }
                } else {
                    i9 = i;
                    uid = uidValueAt2;
                    str25 = str91;
                }
                wakelockStats = uid.getWakelockStats();
                str26 = str25;
                str27 = ", ";
                jComputeWakeLock = 0;
                jComputeWakeLock2 = 0;
                jComputeWakeLock3 = 0;
                jComputeWakeLock4 = 0;
                i10 = 0;
                boolean z26 = z2;
                size = wakelockStats.size() - 1;
                z3 = z26;
                while (size >= 0) {
                    Uid.Wakelock wakelockValueAt3 = wakelockStats.valueAt(size);
                    sb5.setLength(0);
                    sb5.append(str93);
                    sb5.append("    Wake lock ");
                    sb5.append(wakelockStats.keyAt(size));
                    String str910 = str24;
                    ArrayMap<String, ? extends Uid.Wakelock> arrayMap2 = wakelockStats;
                    String str911 = str23;
                    int i47 = i10;
                    long j612 = j67;
                    int i48 = i9;
                    String str912 = str22;
                    String str913 = str21;
                    String strPrintWakeLock3 = printWakeLock(sb5, wakelockValueAt3.getWakeTime(1), j612, "full", i, ": ");
                    wakeTime = wakelockValueAt3.getWakeTime(0);
                    String strPrintWakeLock4 = printWakeLock(sb5, wakeTime, j612, Slice.HINT_PARTIAL, i, strPrintWakeLock3);
                    if (wakeTime != null) {
                        subTimer4 = wakeTime.getSubTimer();
                    } else {
                        subTimer4 = null;
                    }
                    long j715 = j67;
                    printWakeLock(sb5, wakelockValueAt3.getWakeTime(18), j715, "draw", i, printWakeLock(sb5, wakelockValueAt3.getWakeTime(2), j715, Context.WINDOW_SERVICE, i, printWakeLock(sb5, subTimer4, j715, "background partial", i, strPrintWakeLock4)));
                    sb5.append(" realtime");
                    printWriter3.println(sb5.toString());
                    long j716 = j67;
                    jComputeWakeLock += computeWakeLock(wakelockValueAt3.getWakeTime(1), j716, i48);
                    jComputeWakeLock2 += computeWakeLock(wakelockValueAt3.getWakeTime(0), j716, i48);
                    jComputeWakeLock3 += computeWakeLock(wakelockValueAt3.getWakeTime(2), j716, i48);
                    jComputeWakeLock4 += computeWakeLock(wakelockValueAt3.getWakeTime(18), j716, i48);
                    size--;
                    i10 = i47 + 1;
                    i9 = i48;
                    wakelockStats = arrayMap2;
                    str23 = str911;
                    str24 = str910;
                    str22 = str912;
                    str21 = str913;
                    z3 = true;
                    str93 = str;
                }
                boolean z27 = z3;
                str28 = str22;
                str29 = str21;
                str13 = str24;
                String str914 = str23;
                j13 = j67;
                j14 = jComputeWakeLock3;
                j15 = jComputeWakeLock4;
                j16 = jComputeWakeLock2;
                if (i10 > 1) {
                    if (uid.getAggregatedPartialWakelockTimer() != null) {
                        Timer aggregatedPartialWakelockTimer2 = uid.getAggregatedPartialWakelockTimer();
                        j30 = j53;
                        long totalDurationMsLocked11 = aggregatedPartialWakelockTimer2.getTotalDurationMsLocked(j30);
                        subTimer3 = aggregatedPartialWakelockTimer2.getSubTimer();
                        if (subTimer3 != null) {
                            totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j30);
                        } else {
                            totalDurationMsLocked5 = 0;
                        }
                        j53 = j30;
                        j29 = totalDurationMsLocked11;
                        j28 = totalDurationMsLocked5;
                    } else {
                        j28 = 0;
                        j29 = 0;
                    }
                    if (j29 == 0) {
                    }
                    sb5.setLength(0);
                    sb5.append(str);
                    sb5.append("    TOTAL wake: ");
                    if (jComputeWakeLock != 0) {
                        formatTimeMs(sb5, jComputeWakeLock);
                        sb5.append("full");
                        z13 = true;
                    } else {
                        z13 = false;
                    }
                    str27 = str27;
                    if (j16 != 0) {
                        if (z13) {
                            sb5.append(str27);
                        }
                        formatTimeMs(sb5, j16);
                        sb5.append("blamed partial");
                        z13 = true;
                    }
                    if (j29 != 0) {
                        if (z13) {
                            sb5.append(str27);
                        }
                        formatTimeMs(sb5, j29);
                        sb5.append("actual partial");
                        z13 = true;
                    }
                    if (j28 != 0) {
                        if (z13) {
                            sb5.append(str27);
                        }
                        formatTimeMs(sb5, j28);
                        sb5.append("actual background partial");
                        z13 = true;
                    }
                    if (j14 != 0) {
                        if (z13) {
                            sb5.append(str27);
                        }
                        formatTimeMs(sb5, j14);
                        sb5.append(Context.WINDOW_SERVICE);
                        z13 = true;
                    }
                    if (j15 != 0) {
                        if (z13) {
                            sb5.append(",");
                        }
                        formatTimeMs(sb5, j15);
                        sb5.append("draw");
                    }
                    sb5.append(" realtime");
                    printWriter3.println(sb5.toString());
                    multicastWakelockStats = uid.getMulticastWakelockStats();
                    if (multicastWakelockStats != null) {
                        i11 = i;
                        j17 = j13;
                        totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j17, i11);
                        countLocked7 = multicastWakelockStats.getCountLocked(i11);
                        if (totalTimeLocked4 > 0) {
                            sb5.setLength(0);
                            str30 = str;
                            sb5.append(str30);
                            sb5.append("    WiFi Multicast Wakelock");
                            sb5.append(" count = ");
                            sb5.append(countLocked7);
                            sb5.append(" time = ");
                            formatTimeMsNoSpace(sb5, (totalTimeLocked4 + 500) / 1000);
                            printWriter3.println(sb5.toString());
                        } else {
                            str30 = str;
                        }
                    } else {
                        str30 = str;
                        i11 = i;
                        j17 = j13;
                    }
                    syncStats = uid.getSyncStats();
                    size2 = syncStats.size() - 1;
                    z4 = z27;
                    while (size2 >= 0) {
                        Timer timerValueAt5 = syncStats.valueAt(size2);
                        totalTimeLocked3 = (timerValueAt5.getTotalTimeLocked(j17, i11) + 500) / 1000;
                        countLocked5 = timerValueAt5.getCountLocked(i11);
                        subTimer2 = timerValueAt5.getSubTimer();
                        if (subTimer2 != null) {
                            totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j53);
                        } else {
                            totalDurationMsLocked4 = -1;
                        }
                        j27 = totalDurationMsLocked4;
                        if (subTimer2 != null) {
                            countLocked6 = subTimer2.getCountLocked(i11);
                        } else {
                            countLocked6 = i3;
                        }
                        sb5.setLength(0);
                        sb5.append(str30);
                        sb5.append("    Sync ");
                        sb5.append(syncStats.keyAt(size2));
                        String str1010 = str90;
                        sb5.append(str1010);
                        if (totalTimeLocked3 != 0) {
                            formatTimeMs(sb5, totalTimeLocked3);
                            sb5.append("realtime (");
                            sb5.append(countLocked5);
                            str50 = str28;
                            sb5.append(str50);
                            if (j27 > 0) {
                                sb5.append(str27);
                                formatTimeMs(sb5, j27);
                                sb5.append("background (");
                                sb5.append(countLocked6);
                                sb5.append(str50);
                            }
                        } else {
                            str50 = str28;
                            sb5.append("(not used)");
                        }
                        printWriter3.println(sb5.toString());
                        size2--;
                        str28 = str50;
                        str90 = str1010;
                        z4 = true;
                    }
                    str31 = str90;
                    j18 = j53;
                    str32 = str28;
                    jobStats = uid.getJobStats();
                    size3 = jobStats.size() - 1;
                    z5 = z4;
                    while (size3 >= 0) {
                        Timer timerValueAt6 = jobStats.valueAt(size3);
                        totalTimeLocked2 = (timerValueAt6.getTotalTimeLocked(j17, i11) + 500) / 1000;
                        long j717 = j17;
                        countLocked3 = timerValueAt6.getCountLocked(i11);
                        subTimer = timerValueAt6.getSubTimer();
                        if (subTimer != null) {
                            totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j18);
                        } else {
                            totalDurationMsLocked3 = -1;
                        }
                        long j718 = j18;
                        j26 = totalDurationMsLocked3;
                        if (subTimer != null) {
                            countLocked4 = subTimer.getCountLocked(i11);
                        } else {
                            countLocked4 = i3;
                        }
                        sb5.setLength(0);
                        sb5.append(str30);
                        sb5.append("    Job ");
                        sb5.append(jobStats.keyAt(size3));
                        sb5.append(str31);
                        if (totalTimeLocked2 != 0) {
                            formatTimeMs(sb5, totalTimeLocked2);
                            sb5.append("realtime (");
                            sb5.append(countLocked3);
                            sb5.append(str32);
                            if (j26 > 0) {
                                sb5.append(str27);
                                formatTimeMs(sb5, j26);
                                sb5.append("background (");
                                sb5.append(countLocked4);
                                sb5.append(str32);
                            }
                        } else {
                            sb5.append("(not used)");
                        }
                        printWriter3.println(sb5.toString());
                        size3--;
                        j17 = j717;
                        j18 = j718;
                        z5 = true;
                    }
                    j19 = j17;
                    long j719 = j18;
                    jobCompletionStats = uid.getJobCompletionStats();
                    size4 = jobCompletionStats.size() - 1;
                    while (size4 >= 0) {
                        sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                        if (sparseIntArrayValueAt != null) {
                            printWriter.print(str);
                            printWriter3.print("    Job Completions ");
                            printWriter3.print(jobCompletionStats.keyAt(size4));
                            printWriter3.print(SettingsStringUtil.DELIMITER);
                            while (i21 < sparseIntArrayValueAt.size()) {
                                printWriter3.print(str26);
                                printWriter3.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                printWriter3.print(str92);
                                printWriter3.print(sparseIntArrayValueAt.valueAt(i21));
                                printWriter3.print("x)");
                            }
                            printWriter.println();
                        }
                        size4--;
                        str26 = str26;
                        str92 = str92;
                    }
                    uid2 = uid;
                    String str1011 = str92;
                    String str1012 = str26;
                    uid2.getDeferredJobsLineLocked(sb5, i11);
                    if (sb5.length() > 0) {
                        printWriter3.print("    Jobs deferred on launch ");
                        printWriter3.println(sb5.toString());
                    }
                    sb2 = sb5;
                    j20 = j719;
                    j21 = j66;
                    str33 = str1011;
                    str34 = str914;
                    sparseArray = sparseArray7;
                    str35 = str32;
                    str36 = str31;
                    str37 = str27;
                    str38 = str1012;
                    str39 = str17;
                    boolean zPrintTimer4 = z5 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j19, i, str, "Flashlight") | printTimer(printWriter, sb2, uid2.getCameraTurnedOnTimer(), j19, i, str, "Camera") | printTimer(printWriter, sb2, uid2.getVideoTurnedOnTimer(), j19, i, str, "Video") | printTimer(printWriter, sb2, uid2.getAudioTurnedOnTimer(), j19, i, str, "Audio");
                    sensorStats = uid2.getSensorStats();
                    size5 = sensorStats.size();
                    z6 = zPrintTimer4;
                    i12 = 0;
                    while (i12 < size5) {
                        sensorValueAt = sensorStats.valueAt(i12);
                        sensorStats.keyAt(i12);
                        sb2.setLength(0);
                        sb2.append(str30);
                        sb2.append("    Sensor ");
                        handle = sensorValueAt.getHandle();
                        if (handle == -10000) {
                            sb2.append("GPS");
                        } else {
                            sb2.append(handle);
                        }
                        sb2.append(str36);
                        sensorTime = sensorValueAt.getSensorTime();
                        if (sensorTime != null) {
                            j25 = j19;
                            totalTimeLocked = (sensorTime.getTotalTimeLocked(j25, i11) + 500) / 1000;
                            sparseArray2 = sensorStats;
                            countLocked = sensorTime.getCountLocked(i11);
                            sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            if (sensorBackgroundTime != null) {
                                countLocked2 = sensorBackgroundTime.getCountLocked(i11);
                            } else {
                                countLocked2 = 0;
                            }
                            totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j20);
                            if (sensorBackgroundTime != null) {
                                totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j20);
                            } else {
                                totalDurationMsLocked2 = 0;
                            }
                            if (totalTimeLocked != 0) {
                                if (totalDurationMsLocked != totalTimeLocked) {
                                    formatTimeMs(sb2, totalTimeLocked);
                                    sb2.append("blamed realtime, ");
                                }
                                formatTimeMs(sb2, totalDurationMsLocked);
                                sb2.append("realtime (");
                                sb2.append(countLocked);
                                str48 = str35;
                                sb2.append(str48);
                                if (totalDurationMsLocked2 == 0) {
                                    str49 = str37;
                                    sb2.append(str49);
                                    formatTimeMs(sb2, totalDurationMsLocked2);
                                    sb2.append("background (");
                                    sb2.append(countLocked2);
                                    sb2.append(str48);
                                } else {
                                    str49 = str37;
                                    sb2.append(str49);
                                    formatTimeMs(sb2, totalDurationMsLocked2);
                                    sb2.append("background (");
                                    sb2.append(countLocked2);
                                    sb2.append(str48);
                                }
                            } else {
                                str48 = str35;
                                str49 = str37;
                                sb2.append("(not used)");
                            }
                        } else {
                            sparseArray2 = sensorStats;
                            str36 = str36;
                            size5 = size5;
                            j25 = j19;
                            str48 = str35;
                            str49 = str37;
                            sb2.append("(not used)");
                        }
                        printWriter.println(sb2.toString());
                        i12++;
                        size5 = size5;
                        str35 = str48;
                        str37 = str49;
                        j19 = j25;
                        z6 = true;
                        str36 = str36;
                        sensorStats = sparseArray2;
                        str30 = str;
                    }
                    str40 = str36;
                    str41 = str37;
                    printWriter4 = printWriter;
                    long j7110 = j19;
                    zPrintTimer = z6 | printTimer(printWriter, sb2, uid2.getVibratorOnTimer(), j19, i, str, "Vibrator") | printTimer(printWriter, sb2, uid2.getForegroundActivityTimer(), j7110, i, str, "Foreground activities") | printTimer(printWriter, sb2, uid2.getForegroundServiceTimer(), j7110, i, str, "Foreground services");
                    j22 = 0;
                    i13 = 0;
                    while (i13 < 7) {
                        long j7111 = j19;
                        processStateTime = uid2.getProcessStateTime(i13, j7111, i11);
                        if (processStateTime > 0) {
                            j22 += processStateTime;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    ");
                            sb2.append(Uid.PROCESS_STATE_NAMES[i13]);
                            sb2.append(" for: ");
                            formatTimeMs(sb2, (processStateTime + 500) / 1000);
                            printWriter4.println(sb2.toString());
                            zPrintTimer = true;
                        }
                        i13++;
                        j19 = j7111;
                        j20 = j20;
                    }
                    j53 = j20;
                    j23 = j19;
                    if (j22 > 0) {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total running: ");
                        formatTimeMs(sb2, (j22 + 500) / 1000);
                        printWriter4.println(sb2.toString());
                    }
                    userCpuTimeUs = uid2.getUserCpuTimeUs(i11);
                    systemCpuTimeUs = uid2.getSystemCpuTimeUs(i11);
                    if (userCpuTimeUs <= 0) {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total cpu time: u=");
                        formatTimeMs(sb2, userCpuTimeUs / 1000);
                        sb2.append("s=");
                        formatTimeMs(sb2, systemCpuTimeUs / 1000);
                        printWriter4.println(sb2.toString());
                    } else {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Total cpu time: u=");
                        formatTimeMs(sb2, userCpuTimeUs / 1000);
                        sb2.append("s=");
                        formatTimeMs(sb2, systemCpuTimeUs / 1000);
                        printWriter4.println(sb2.toString());
                    }
                    cpuFreqTimes = uid2.getCpuFreqTimes(i11);
                    if (cpuFreqTimes != null) {
                        sb2.setLength(0);
                        sb2.append("    Total cpu time per freq:");
                        while (i20 < cpuFreqTimes.length) {
                            sb2.append(str38 + j77);
                        }
                        printWriter4.println(sb2.toString());
                    }
                    screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i11);
                    if (screenOffCpuFreqTimes != null) {
                        sb2.setLength(0);
                        sb2.append("    Total screen-off cpu time per freq:");
                        while (i19 < screenOffCpuFreqTimes.length) {
                            sb2.append(str38 + j78);
                        }
                        printWriter4.println(sb2.toString());
                    }
                    i14 = 0;
                    while (i14 < 7) {
                        cpuFreqTimes2 = uid2.getCpuFreqTimes(i11, i14);
                        if (cpuFreqTimes2 != null) {
                            sb2.setLength(0);
                            sb2.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            i18 = 0;
                            while (i18 < cpuFreqTimes2.length) {
                                sb2.append(str38 + cpuFreqTimes2[i18]);
                                i18++;
                                zPrintTimer = zPrintTimer;
                            }
                            z12 = zPrintTimer;
                            printWriter4.println(sb2.toString());
                        } else {
                            z12 = zPrintTimer;
                        }
                        screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(i11, i14);
                        if (screenOffCpuFreqTimes2 != null) {
                            sb2.setLength(0);
                            sb2.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            while (i17 < screenOffCpuFreqTimes2.length) {
                                sb2.append(str38 + j79);
                            }
                            printWriter4.println(sb2.toString());
                        }
                        i14++;
                        zPrintTimer = z12;
                    }
                    processStats = uid2.getProcessStats();
                    size6 = processStats.size() - 1;
                    while (size6 >= 0) {
                        procValueAt = processStats.valueAt(size6);
                        userTime = procValueAt.getUserTime(i11);
                        boolean z28 = zPrintTimer;
                        long j82 = j23;
                        systemTime = procValueAt.getSystemTime(i11);
                        foregroundTime = procValueAt.getForegroundTime(i11);
                        String str1013 = str38;
                        starts2 = procValueAt.getStarts(i11);
                        Uid uid7 = uid2;
                        numCrashes = procValueAt.getNumCrashes(i11);
                        numAnrs = procValueAt.getNumAnrs(i11);
                        if (i11 == 0) {
                            iCountExcessivePowers = procValueAt.countExcessivePowers();
                        } else {
                            iCountExcessivePowers = 0;
                        }
                        if (userTime != 0) {
                            proc = procValueAt;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Proc ");
                            sb2.append(processStats.keyAt(size6));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("      CPU: ");
                            formatTimeMs(sb2, userTime);
                            sb2.append("usr + ");
                            formatTimeMs(sb2, systemTime);
                            sb2.append("krn ; ");
                            formatTimeMs(sb2, foregroundTime);
                            sb2.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 != 0) {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            } else {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            }
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                proc = proc;
                                excessivePower = proc.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        str47 = str34;
                                        printWriter4.print(str47);
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    } else {
                                        str47 = str34;
                                    }
                                } else {
                                    str47 = str34;
                                }
                                i16++;
                                str34 = str47;
                                str45 = str45;
                            }
                            str29 = str45;
                            str46 = str34;
                            z11 = true;
                        } else {
                            proc = procValueAt;
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    Proc ");
                            sb2.append(processStats.keyAt(size6));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("      CPU: ");
                            formatTimeMs(sb2, userTime);
                            sb2.append("usr + ");
                            formatTimeMs(sb2, systemTime);
                            sb2.append("krn ; ");
                            formatTimeMs(sb2, foregroundTime);
                            sb2.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 != 0) {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            } else {
                                str45 = str29;
                                sb2.append(str45);
                                sb2.append(str);
                                sb2.append("      ");
                                if (starts2 != 0) {
                                    sb2.append(starts2);
                                    sb2.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numCrashes);
                                    sb2.append(" crashes");
                                    z10 = true;
                                } else {
                                    z10 = z9;
                                }
                                if (numAnrs != 0) {
                                    if (z10) {
                                        sb2.append(str41);
                                    }
                                    sb2.append(numAnrs);
                                    sb2.append(" anrs");
                                }
                            }
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                proc = proc;
                                excessivePower = proc.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        str47 = str34;
                                        printWriter4.print(str47);
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    } else {
                                        str47 = str34;
                                    }
                                } else {
                                    str47 = str34;
                                }
                                i16++;
                                str34 = str47;
                                str45 = str45;
                            }
                            str29 = str45;
                            str46 = str34;
                            z11 = true;
                        }
                        size6--;
                        uid2 = uid7;
                        zPrintTimer = z11;
                        str34 = str46;
                        j23 = j82;
                        str38 = str1013;
                        i11 = i;
                    }
                    str42 = str38;
                    j24 = j23;
                    str43 = str34;
                    packageStats = uid2.getPackageStats();
                    size7 = packageStats.size() - 1;
                    z7 = zPrintTimer;
                    while (size7 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("    Apk ");
                        printWriter4.print(packageStats.keyAt(size7));
                        printWriter4.println(SettingsStringUtil.DELIMITER);
                        Uid.Pkg pkgValueAt3 = packageStats.valueAt(size7);
                        wakeupAlarmStats = pkgValueAt3.getWakeupAlarmStats();
                        size8 = wakeupAlarmStats.size() - 1;
                        z8 = false;
                        while (size8 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("      Wakeup alarm ");
                            printWriter4.print(wakeupAlarmStats.keyAt(size8));
                            printWriter4.print(str40);
                            printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                            printWriter4.println(" times");
                            size8--;
                            z8 = true;
                        }
                        String str1014 = str40;
                        serviceStats = pkgValueAt3.getServiceStats();
                        while (size9 >= 0) {
                            Uid.Pkg.Serv servValueAt3 = serviceStats.valueAt(size9);
                            j52 = j52;
                            startTime = servValueAt3.getStartTime(j52, i);
                            starts = servValueAt3.getStarts(i);
                            launches = servValueAt3.getLaunches(i);
                            if (startTime == 0) {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("      Service ");
                                sb2.append(serviceStats.keyAt(size9));
                                sb2.append(":\n");
                                sb2.append(str);
                                sb2.append("        Created for: ");
                                formatTimeMs(sb2, startTime / 1000);
                                sb2.append("uptime\n");
                                sb2.append(str);
                                sb2.append("        Starts: ");
                                sb2.append(starts);
                                sb2.append(", launches: ");
                                sb2.append(launches);
                                printWriter4.println(sb2.toString());
                                z8 = true;
                            } else {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("      Service ");
                                sb2.append(serviceStats.keyAt(size9));
                                sb2.append(":\n");
                                sb2.append(str);
                                sb2.append("        Created for: ");
                                formatTimeMs(sb2, startTime / 1000);
                                sb2.append("uptime\n");
                                sb2.append(str);
                                sb2.append("        Starts: ");
                                sb2.append(starts);
                                sb2.append(", launches: ");
                                sb2.append(launches);
                                printWriter4.println(sb2.toString());
                                z8 = true;
                            }
                        }
                        if (!z8) {
                            printWriter.print(str);
                            printWriter4.println("      (nothing executed)");
                        }
                        size7--;
                        str40 = str1014;
                        z7 = true;
                    }
                    str44 = str40;
                    i15 = 0;
                    if (!z7) {
                        printWriter.print(str);
                        printWriter4.println("    (nothing executed)");
                    }
                } else {
                    j13 = j13;
                }
                multicastWakelockStats = uid.getMulticastWakelockStats();
                if (multicastWakelockStats != null) {
                    i11 = i;
                    j17 = j13;
                    totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j17, i11);
                    countLocked7 = multicastWakelockStats.getCountLocked(i11);
                    if (totalTimeLocked4 > 0) {
                        sb5.setLength(0);
                        str30 = str;
                        sb5.append(str30);
                        sb5.append("    WiFi Multicast Wakelock");
                        sb5.append(" count = ");
                        sb5.append(countLocked7);
                        sb5.append(" time = ");
                        formatTimeMsNoSpace(sb5, (totalTimeLocked4 + 500) / 1000);
                        printWriter3.println(sb5.toString());
                    } else {
                        str30 = str;
                    }
                } else {
                    str30 = str;
                    i11 = i;
                    j17 = j13;
                }
                syncStats = uid.getSyncStats();
                size2 = syncStats.size() - 1;
                z4 = z27;
                while (size2 >= 0) {
                    Timer timerValueAt7 = syncStats.valueAt(size2);
                    totalTimeLocked3 = (timerValueAt7.getTotalTimeLocked(j17, i11) + 500) / 1000;
                    countLocked5 = timerValueAt7.getCountLocked(i11);
                    subTimer2 = timerValueAt7.getSubTimer();
                    if (subTimer2 != null) {
                        totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j53);
                    } else {
                        totalDurationMsLocked4 = -1;
                    }
                    j27 = totalDurationMsLocked4;
                    if (subTimer2 != null) {
                        countLocked6 = subTimer2.getCountLocked(i11);
                    } else {
                        countLocked6 = i3;
                    }
                    sb5.setLength(0);
                    sb5.append(str30);
                    sb5.append("    Sync ");
                    sb5.append(syncStats.keyAt(size2));
                    String str1015 = str90;
                    sb5.append(str1015);
                    if (totalTimeLocked3 != 0) {
                        formatTimeMs(sb5, totalTimeLocked3);
                        sb5.append("realtime (");
                        sb5.append(countLocked5);
                        str50 = str28;
                        sb5.append(str50);
                        if (j27 > 0) {
                            sb5.append(str27);
                            formatTimeMs(sb5, j27);
                            sb5.append("background (");
                            sb5.append(countLocked6);
                            sb5.append(str50);
                        }
                    } else {
                        str50 = str28;
                        sb5.append("(not used)");
                    }
                    printWriter3.println(sb5.toString());
                    size2--;
                    str28 = str50;
                    str90 = str1015;
                    z4 = true;
                }
                str31 = str90;
                j18 = j53;
                str32 = str28;
                jobStats = uid.getJobStats();
                size3 = jobStats.size() - 1;
                z5 = z4;
                while (size3 >= 0) {
                    Timer timerValueAt8 = jobStats.valueAt(size3);
                    totalTimeLocked2 = (timerValueAt8.getTotalTimeLocked(j17, i11) + 500) / 1000;
                    long j7112 = j17;
                    countLocked3 = timerValueAt8.getCountLocked(i11);
                    subTimer = timerValueAt8.getSubTimer();
                    if (subTimer != null) {
                        totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j18);
                    } else {
                        totalDurationMsLocked3 = -1;
                    }
                    long j7113 = j18;
                    j26 = totalDurationMsLocked3;
                    if (subTimer != null) {
                        countLocked4 = subTimer.getCountLocked(i11);
                    } else {
                        countLocked4 = i3;
                    }
                    sb5.setLength(0);
                    sb5.append(str30);
                    sb5.append("    Job ");
                    sb5.append(jobStats.keyAt(size3));
                    sb5.append(str31);
                    if (totalTimeLocked2 != 0) {
                        formatTimeMs(sb5, totalTimeLocked2);
                        sb5.append("realtime (");
                        sb5.append(countLocked3);
                        sb5.append(str32);
                        if (j26 > 0) {
                            sb5.append(str27);
                            formatTimeMs(sb5, j26);
                            sb5.append("background (");
                            sb5.append(countLocked4);
                            sb5.append(str32);
                        }
                    } else {
                        sb5.append("(not used)");
                    }
                    printWriter3.println(sb5.toString());
                    size3--;
                    j17 = j7112;
                    j18 = j7113;
                    z5 = true;
                }
                j19 = j17;
                long j7114 = j18;
                jobCompletionStats = uid.getJobCompletionStats();
                size4 = jobCompletionStats.size() - 1;
                while (size4 >= 0) {
                    sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                    if (sparseIntArrayValueAt != null) {
                        printWriter.print(str);
                        printWriter3.print("    Job Completions ");
                        printWriter3.print(jobCompletionStats.keyAt(size4));
                        printWriter3.print(SettingsStringUtil.DELIMITER);
                        while (i21 < sparseIntArrayValueAt.size()) {
                            printWriter3.print(str26);
                            printWriter3.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                            printWriter3.print(str92);
                            printWriter3.print(sparseIntArrayValueAt.valueAt(i21));
                            printWriter3.print("x)");
                        }
                        printWriter.println();
                    }
                    size4--;
                    str26 = str26;
                    str92 = str92;
                }
                uid2 = uid;
                String str1016 = str92;
                String str1017 = str26;
                uid2.getDeferredJobsLineLocked(sb5, i11);
                if (sb5.length() > 0) {
                    printWriter3.print("    Jobs deferred on launch ");
                    printWriter3.println(sb5.toString());
                }
                sb2 = sb5;
                j20 = j7114;
                j21 = j66;
                str33 = str1016;
                str34 = str914;
                sparseArray = sparseArray7;
                str35 = str32;
                str36 = str31;
                str37 = str27;
                str38 = str1017;
                str39 = str17;
                boolean zPrintTimer5 = z5 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j19, i, str, "Flashlight") | printTimer(printWriter, sb2, uid2.getCameraTurnedOnTimer(), j19, i, str, "Camera") | printTimer(printWriter, sb2, uid2.getVideoTurnedOnTimer(), j19, i, str, "Video") | printTimer(printWriter, sb2, uid2.getAudioTurnedOnTimer(), j19, i, str, "Audio");
                sensorStats = uid2.getSensorStats();
                size5 = sensorStats.size();
                z6 = zPrintTimer5;
                i12 = 0;
                while (i12 < size5) {
                    sensorValueAt = sensorStats.valueAt(i12);
                    sensorStats.keyAt(i12);
                    sb2.setLength(0);
                    sb2.append(str30);
                    sb2.append("    Sensor ");
                    handle = sensorValueAt.getHandle();
                    if (handle == -10000) {
                        sb2.append("GPS");
                    } else {
                        sb2.append(handle);
                    }
                    sb2.append(str36);
                    sensorTime = sensorValueAt.getSensorTime();
                    if (sensorTime != null) {
                        j25 = j19;
                        totalTimeLocked = (sensorTime.getTotalTimeLocked(j25, i11) + 500) / 1000;
                        sparseArray2 = sensorStats;
                        countLocked = sensorTime.getCountLocked(i11);
                        sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                        if (sensorBackgroundTime != null) {
                            countLocked2 = sensorBackgroundTime.getCountLocked(i11);
                        } else {
                            countLocked2 = 0;
                        }
                        totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j20);
                        if (sensorBackgroundTime != null) {
                            totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j20);
                        } else {
                            totalDurationMsLocked2 = 0;
                        }
                        if (totalTimeLocked != 0) {
                            if (totalDurationMsLocked != totalTimeLocked) {
                                formatTimeMs(sb2, totalTimeLocked);
                                sb2.append("blamed realtime, ");
                            }
                            formatTimeMs(sb2, totalDurationMsLocked);
                            sb2.append("realtime (");
                            sb2.append(countLocked);
                            str48 = str35;
                            sb2.append(str48);
                            if (totalDurationMsLocked2 == 0) {
                                str49 = str37;
                                sb2.append(str49);
                                formatTimeMs(sb2, totalDurationMsLocked2);
                                sb2.append("background (");
                                sb2.append(countLocked2);
                                sb2.append(str48);
                            } else {
                                str49 = str37;
                                sb2.append(str49);
                                formatTimeMs(sb2, totalDurationMsLocked2);
                                sb2.append("background (");
                                sb2.append(countLocked2);
                                sb2.append(str48);
                            }
                        } else {
                            str48 = str35;
                            str49 = str37;
                            sb2.append("(not used)");
                        }
                    } else {
                        sparseArray2 = sensorStats;
                        str36 = str36;
                        size5 = size5;
                        j25 = j19;
                        str48 = str35;
                        str49 = str37;
                        sb2.append("(not used)");
                    }
                    printWriter.println(sb2.toString());
                    i12++;
                    size5 = size5;
                    str35 = str48;
                    str37 = str49;
                    j19 = j25;
                    z6 = true;
                    str36 = str36;
                    sensorStats = sparseArray2;
                    str30 = str;
                }
                str40 = str36;
                str41 = str37;
                printWriter4 = printWriter;
                long j7115 = j19;
                zPrintTimer = z6 | printTimer(printWriter, sb2, uid2.getVibratorOnTimer(), j19, i, str, "Vibrator") | printTimer(printWriter, sb2, uid2.getForegroundActivityTimer(), j7115, i, str, "Foreground activities") | printTimer(printWriter, sb2, uid2.getForegroundServiceTimer(), j7115, i, str, "Foreground services");
                j22 = 0;
                i13 = 0;
                while (i13 < 7) {
                    long j7116 = j19;
                    processStateTime = uid2.getProcessStateTime(i13, j7116, i11);
                    if (processStateTime > 0) {
                        j22 += processStateTime;
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    ");
                        sb2.append(Uid.PROCESS_STATE_NAMES[i13]);
                        sb2.append(" for: ");
                        formatTimeMs(sb2, (processStateTime + 500) / 1000);
                        printWriter4.println(sb2.toString());
                        zPrintTimer = true;
                    }
                    i13++;
                    j19 = j7116;
                    j20 = j20;
                }
                j53 = j20;
                j23 = j19;
                if (j22 > 0) {
                    sb2.setLength(0);
                    sb2.append(str);
                    sb2.append("    Total running: ");
                    formatTimeMs(sb2, (j22 + 500) / 1000);
                    printWriter4.println(sb2.toString());
                }
                userCpuTimeUs = uid2.getUserCpuTimeUs(i11);
                systemCpuTimeUs = uid2.getSystemCpuTimeUs(i11);
                if (userCpuTimeUs <= 0) {
                    sb2.setLength(0);
                    sb2.append(str);
                    sb2.append("    Total cpu time: u=");
                    formatTimeMs(sb2, userCpuTimeUs / 1000);
                    sb2.append("s=");
                    formatTimeMs(sb2, systemCpuTimeUs / 1000);
                    printWriter4.println(sb2.toString());
                } else {
                    sb2.setLength(0);
                    sb2.append(str);
                    sb2.append("    Total cpu time: u=");
                    formatTimeMs(sb2, userCpuTimeUs / 1000);
                    sb2.append("s=");
                    formatTimeMs(sb2, systemCpuTimeUs / 1000);
                    printWriter4.println(sb2.toString());
                }
                cpuFreqTimes = uid2.getCpuFreqTimes(i11);
                if (cpuFreqTimes != null) {
                    sb2.setLength(0);
                    sb2.append("    Total cpu time per freq:");
                    while (i20 < cpuFreqTimes.length) {
                        sb2.append(str38 + j77);
                    }
                    printWriter4.println(sb2.toString());
                }
                screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i11);
                if (screenOffCpuFreqTimes != null) {
                    sb2.setLength(0);
                    sb2.append("    Total screen-off cpu time per freq:");
                    while (i19 < screenOffCpuFreqTimes.length) {
                        sb2.append(str38 + j78);
                    }
                    printWriter4.println(sb2.toString());
                }
                i14 = 0;
                while (i14 < 7) {
                    cpuFreqTimes2 = uid2.getCpuFreqTimes(i11, i14);
                    if (cpuFreqTimes2 != null) {
                        sb2.setLength(0);
                        sb2.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        i18 = 0;
                        while (i18 < cpuFreqTimes2.length) {
                            sb2.append(str38 + cpuFreqTimes2[i18]);
                            i18++;
                            zPrintTimer = zPrintTimer;
                        }
                        z12 = zPrintTimer;
                        printWriter4.println(sb2.toString());
                    } else {
                        z12 = zPrintTimer;
                    }
                    screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(i11, i14);
                    if (screenOffCpuFreqTimes2 != null) {
                        sb2.setLength(0);
                        sb2.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        while (i17 < screenOffCpuFreqTimes2.length) {
                            sb2.append(str38 + j79);
                        }
                        printWriter4.println(sb2.toString());
                    }
                    i14++;
                    zPrintTimer = z12;
                }
                processStats = uid2.getProcessStats();
                size6 = processStats.size() - 1;
                while (size6 >= 0) {
                    procValueAt = processStats.valueAt(size6);
                    userTime = procValueAt.getUserTime(i11);
                    boolean z29 = zPrintTimer;
                    long j83 = j23;
                    systemTime = procValueAt.getSystemTime(i11);
                    foregroundTime = procValueAt.getForegroundTime(i11);
                    String str1018 = str38;
                    starts2 = procValueAt.getStarts(i11);
                    Uid uid8 = uid2;
                    numCrashes = procValueAt.getNumCrashes(i11);
                    numAnrs = procValueAt.getNumAnrs(i11);
                    if (i11 == 0) {
                        iCountExcessivePowers = procValueAt.countExcessivePowers();
                    } else {
                        iCountExcessivePowers = 0;
                    }
                    if (userTime != 0) {
                        proc = procValueAt;
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Proc ");
                        sb2.append(processStats.keyAt(size6));
                        sb2.append(":\n");
                        sb2.append(str);
                        sb2.append("      CPU: ");
                        formatTimeMs(sb2, userTime);
                        sb2.append("usr + ");
                        formatTimeMs(sb2, systemTime);
                        sb2.append("krn ; ");
                        formatTimeMs(sb2, foregroundTime);
                        sb2.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 != 0) {
                            str45 = str29;
                            sb2.append(str45);
                            sb2.append(str);
                            sb2.append("      ");
                            if (starts2 != 0) {
                                sb2.append(starts2);
                                sb2.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb2.append(str41);
                                }
                                sb2.append(numCrashes);
                                sb2.append(" crashes");
                                z10 = true;
                            } else {
                                z10 = z9;
                            }
                            if (numAnrs != 0) {
                                if (z10) {
                                    sb2.append(str41);
                                }
                                sb2.append(numAnrs);
                                sb2.append(" anrs");
                            }
                        } else {
                            str45 = str29;
                            sb2.append(str45);
                            sb2.append(str);
                            sb2.append("      ");
                            if (starts2 != 0) {
                                sb2.append(starts2);
                                sb2.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb2.append(str41);
                                }
                                sb2.append(numCrashes);
                                sb2.append(" crashes");
                                z10 = true;
                            } else {
                                z10 = z9;
                            }
                            if (numAnrs != 0) {
                                if (z10) {
                                    sb2.append(str41);
                                }
                                sb2.append(numAnrs);
                                sb2.append(" anrs");
                            }
                        }
                        printWriter4 = printWriter;
                        printWriter4.println(sb2.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            proc = proc;
                            excessivePower = proc.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    str47 = str34;
                                    printWriter4.print(str47);
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                } else {
                                    str47 = str34;
                                }
                            } else {
                                str47 = str34;
                            }
                            i16++;
                            str34 = str47;
                            str45 = str45;
                        }
                        str29 = str45;
                        str46 = str34;
                        z11 = true;
                    } else {
                        proc = procValueAt;
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    Proc ");
                        sb2.append(processStats.keyAt(size6));
                        sb2.append(":\n");
                        sb2.append(str);
                        sb2.append("      CPU: ");
                        formatTimeMs(sb2, userTime);
                        sb2.append("usr + ");
                        formatTimeMs(sb2, systemTime);
                        sb2.append("krn ; ");
                        formatTimeMs(sb2, foregroundTime);
                        sb2.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 != 0) {
                            str45 = str29;
                            sb2.append(str45);
                            sb2.append(str);
                            sb2.append("      ");
                            if (starts2 != 0) {
                                sb2.append(starts2);
                                sb2.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb2.append(str41);
                                }
                                sb2.append(numCrashes);
                                sb2.append(" crashes");
                                z10 = true;
                            } else {
                                z10 = z9;
                            }
                            if (numAnrs != 0) {
                                if (z10) {
                                    sb2.append(str41);
                                }
                                sb2.append(numAnrs);
                                sb2.append(" anrs");
                            }
                        } else {
                            str45 = str29;
                            sb2.append(str45);
                            sb2.append(str);
                            sb2.append("      ");
                            if (starts2 != 0) {
                                sb2.append(starts2);
                                sb2.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb2.append(str41);
                                }
                                sb2.append(numCrashes);
                                sb2.append(" crashes");
                                z10 = true;
                            } else {
                                z10 = z9;
                            }
                            if (numAnrs != 0) {
                                if (z10) {
                                    sb2.append(str41);
                                }
                                sb2.append(numAnrs);
                                sb2.append(" anrs");
                            }
                        }
                        printWriter4 = printWriter;
                        printWriter4.println(sb2.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            proc = proc;
                            excessivePower = proc.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    str47 = str34;
                                    printWriter4.print(str47);
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                } else {
                                    str47 = str34;
                                }
                            } else {
                                str47 = str34;
                            }
                            i16++;
                            str34 = str47;
                            str45 = str45;
                        }
                        str29 = str45;
                        str46 = str34;
                        z11 = true;
                    }
                    size6--;
                    uid2 = uid8;
                    zPrintTimer = z11;
                    str34 = str46;
                    j23 = j83;
                    str38 = str1018;
                    i11 = i;
                }
                str42 = str38;
                j24 = j23;
                str43 = str34;
                packageStats = uid2.getPackageStats();
                size7 = packageStats.size() - 1;
                z7 = zPrintTimer;
                while (size7 >= 0) {
                    printWriter.print(str);
                    printWriter4.print("    Apk ");
                    printWriter4.print(packageStats.keyAt(size7));
                    printWriter4.println(SettingsStringUtil.DELIMITER);
                    Uid.Pkg pkgValueAt4 = packageStats.valueAt(size7);
                    wakeupAlarmStats = pkgValueAt4.getWakeupAlarmStats();
                    size8 = wakeupAlarmStats.size() - 1;
                    z8 = false;
                    while (size8 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("      Wakeup alarm ");
                        printWriter4.print(wakeupAlarmStats.keyAt(size8));
                        printWriter4.print(str40);
                        printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                        printWriter4.println(" times");
                        size8--;
                        z8 = true;
                    }
                    String str1019 = str40;
                    serviceStats = pkgValueAt4.getServiceStats();
                    while (size9 >= 0) {
                        Uid.Pkg.Serv servValueAt4 = serviceStats.valueAt(size9);
                        j52 = j52;
                        startTime = servValueAt4.getStartTime(j52, i);
                        starts = servValueAt4.getStarts(i);
                        launches = servValueAt4.getLaunches(i);
                        if (startTime == 0) {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("      Service ");
                            sb2.append(serviceStats.keyAt(size9));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("        Created for: ");
                            formatTimeMs(sb2, startTime / 1000);
                            sb2.append("uptime\n");
                            sb2.append(str);
                            sb2.append("        Starts: ");
                            sb2.append(starts);
                            sb2.append(", launches: ");
                            sb2.append(launches);
                            printWriter4.println(sb2.toString());
                            z8 = true;
                        } else {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("      Service ");
                            sb2.append(serviceStats.keyAt(size9));
                            sb2.append(":\n");
                            sb2.append(str);
                            sb2.append("        Created for: ");
                            formatTimeMs(sb2, startTime / 1000);
                            sb2.append("uptime\n");
                            sb2.append(str);
                            sb2.append("        Starts: ");
                            sb2.append(starts);
                            sb2.append(", launches: ");
                            sb2.append(launches);
                            printWriter4.println(sb2.toString());
                            z8 = true;
                        }
                    }
                    if (!z8) {
                        printWriter.print(str);
                        printWriter4.println("      (nothing executed)");
                    }
                    size7--;
                    str40 = str1019;
                    z7 = true;
                }
                str44 = str40;
                i15 = 0;
                if (!z7) {
                    printWriter.print(str);
                    printWriter4.println("    (nothing executed)");
                }
            } else {
                i8 = i29;
                j24 = j8;
                i15 = i5;
                sb2 = sb;
                str44 = str83;
                sparseArray = sparseArray7;
                i6 = i42;
                printWriter4 = printWriter2;
                str42 = str11;
                str43 = str82;
                j12 = j7;
                str33 = str12;
                j21 = j49;
                str20 = str14;
                str39 = str15;
            }
            i42 = i6 + 1;
            sb = sb2;
            i5 = i15;
            printWriter2 = printWriter4;
            str83 = str44;
            str82 = str43;
            str14 = str20;
            j49 = j21;
            sparseArray6 = sparseArray;
            str12 = str33;
            j7 = j12;
            i29 = i8;
            str15 = str39;
            j8 = j24;
            str11 = str42;
        }
    }

    static void printBitDescriptions(StringBuilder sb, int i, int i2, HistoryTag historyTag, BitDescription[] bitDescriptionArr, boolean z) {
        int i3 = i ^ i2;
        if (i3 == 0) {
            return;
        }
        boolean z2 = false;
        for (BitDescription bitDescription : bitDescriptionArr) {
            if ((bitDescription.mask & i3) != 0) {
                sb.append(z ? WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER : ",");
                if (bitDescription.shift < 0) {
                    sb.append((bitDescription.mask & i2) != 0 ? "+" : NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    sb.append(z ? bitDescription.name : bitDescription.shortName);
                    if (bitDescription.mask == 1073741824 && historyTag != null) {
                        sb.append("=");
                        if (z) {
                            UserHandle.formatUid(sb, historyTag.uid);
                            sb.append(":\"");
                            sb.append(historyTag.string);
                            sb.append("\"");
                        } else {
                            sb.append(historyTag.poolIdx);
                        }
                        z2 = true;
                    }
                } else {
                    sb.append(z ? bitDescription.name : bitDescription.shortName);
                    sb.append("=");
                    int i4 = (bitDescription.mask & i2) >> bitDescription.shift;
                    if (bitDescription.values != null && i4 >= 0 && i4 < bitDescription.values.length) {
                        sb.append(z ? bitDescription.values[i4] : bitDescription.shortValues[i4]);
                    } else {
                        sb.append(i4);
                    }
                }
            }
        }
        if (z2 || historyTag == null) {
            return;
        }
        sb.append(z ? " wake_lock=" : ",w=");
        if (z) {
            UserHandle.formatUid(sb, historyTag.uid);
            sb.append(":\"");
            sb.append(historyTag.string);
            sb.append("\"");
            return;
        }
        sb.append(historyTag.poolIdx);
    }

    public static class HistoryPrinter {
        int oldState = 0;
        int oldState2 = 0;
        int oldLevel = -1;
        int oldStatus = -1;
        int oldHealth = -1;
        int oldPlug = -1;
        int oldTemp = -1;
        int oldVolt = -1;
        int oldChargeMAh = -1;
        long lastTime = -1;

        void reset() {
            this.oldState2 = 0;
            this.oldState = 0;
            this.oldLevel = -1;
            this.oldStatus = -1;
            this.oldHealth = -1;
            this.oldPlug = -1;
            this.oldTemp = -1;
            this.oldVolt = -1;
            this.oldChargeMAh = -1;
        }

        public void printNextItem(PrintWriter printWriter, HistoryItem historyItem, long j, boolean z, boolean z2) {
            printWriter.print(printNextItem(historyItem, j, z, z2));
        }

        public void printNextItem(ProtoOutputStream protoOutputStream, HistoryItem historyItem, long j, boolean z) {
            for (String str : printNextItem(historyItem, j, true, z).split("\n")) {
                protoOutputStream.write(2237677961222L, str);
            }
        }

        private String printNextItem(HistoryItem historyItem, long j, boolean z, boolean z2) {
            String[] strArr;
            StringBuilder sb = new StringBuilder();
            if (!z) {
                sb.append("  ");
                TimeUtils.formatDuration(historyItem.time - j, sb, 19);
                sb.append(" (");
                sb.append(historyItem.numReadInts);
                sb.append(") ");
            } else {
                sb.append(9);
                sb.append(',');
                sb.append(BatteryStats.HISTORY_DATA);
                sb.append(',');
                if (this.lastTime < 0) {
                    sb.append(historyItem.time - j);
                } else {
                    sb.append(historyItem.time - this.lastTime);
                }
                this.lastTime = historyItem.time;
            }
            if (historyItem.cmd == 4) {
                if (z) {
                    sb.append(SettingsStringUtil.DELIMITER);
                }
                sb.append("START\n");
                reset();
            } else {
                byte b = historyItem.cmd;
                String str = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                if (b == 5 || historyItem.cmd == 7) {
                    if (z) {
                        sb.append(SettingsStringUtil.DELIMITER);
                    }
                    if (historyItem.cmd == 7) {
                        sb.append("RESET:");
                        reset();
                    }
                    sb.append("TIME:");
                    if (z) {
                        sb.append(historyItem.currentTime);
                        sb.append("\n");
                    } else {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb.append(DateFormat.format("yyyy-MM-dd-HH-mm-ss", historyItem.currentTime).toString());
                        sb.append("\n");
                    }
                } else if (historyItem.cmd == 8) {
                    if (z) {
                        sb.append(SettingsStringUtil.DELIMITER);
                    }
                    sb.append("SHUTDOWN\n");
                } else if (historyItem.cmd == 6) {
                    if (z) {
                        sb.append(SettingsStringUtil.DELIMITER);
                    }
                    sb.append("*OVERFLOW*\n");
                } else {
                    if (!z) {
                        if (historyItem.batteryLevel < 10) {
                            sb.append("00");
                        } else if (historyItem.batteryLevel < 100) {
                            sb.append("0");
                        }
                        sb.append(historyItem.batteryLevel);
                        if (z2) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                            if (historyItem.states >= 0) {
                                if (historyItem.states < 16) {
                                    sb.append("0000000");
                                } else if (historyItem.states < 256) {
                                    sb.append("000000");
                                } else if (historyItem.states < 4096) {
                                    sb.append("00000");
                                } else if (historyItem.states < 65536) {
                                    sb.append("0000");
                                } else if (historyItem.states < 1048576) {
                                    sb.append("000");
                                } else if (historyItem.states < 16777216) {
                                    sb.append("00");
                                } else if (historyItem.states < 268435456) {
                                    sb.append("0");
                                }
                            }
                            sb.append(Integer.toHexString(historyItem.states));
                        }
                    } else if (this.oldLevel != historyItem.batteryLevel) {
                        this.oldLevel = historyItem.batteryLevel;
                        sb.append(",Bl=");
                        sb.append(historyItem.batteryLevel);
                    }
                    int i = this.oldStatus;
                    byte b2 = historyItem.batteryStatus;
                    String str2 = FullBackup.FILES_TREE_TOKEN;
                    String str3 = FullBackup.CACHE_TREE_TOKEN;
                    if (i != b2) {
                        this.oldStatus = historyItem.batteryStatus;
                        sb.append(z ? ",Bs=" : " status=");
                        int i2 = this.oldStatus;
                        if (i2 == 1) {
                            sb.append(z ? "?" : "unknown");
                        } else if (i2 == 2) {
                            sb.append(z ? FullBackup.CACHE_TREE_TOKEN : "charging");
                        } else if (i2 == 3) {
                            sb.append(z ? "d" : "discharging");
                        } else if (i2 == 4) {
                            sb.append(z ? "n" : "not-charging");
                        } else if (i2 == 5) {
                            sb.append(z ? FullBackup.FILES_TREE_TOKEN : "full");
                        } else {
                            sb.append(i2);
                        }
                    }
                    if (this.oldHealth != historyItem.batteryHealth) {
                        this.oldHealth = historyItem.batteryHealth;
                        sb.append(z ? ",Bh=" : " health=");
                        int i3 = this.oldHealth;
                        switch (i3) {
                            case 1:
                                sb.append(z ? "?" : "unknown");
                                break;
                            case 2:
                                sb.append(z ? "g" : "good");
                                break;
                            case 3:
                                sb.append(z ? BatteryStats.HISTORY_DATA : "overheat");
                                break;
                            case 4:
                                sb.append(z ? "d" : "dead");
                                break;
                            case 5:
                                sb.append(z ? Telephony.BaseMmsColumns.MMS_VERSION : "over-voltage");
                                break;
                            case 6:
                                if (!z) {
                                    str2 = "failure";
                                }
                                sb.append(str2);
                                break;
                            case 7:
                                if (!z) {
                                    str3 = "cold";
                                }
                                sb.append(str3);
                                break;
                            default:
                                sb.append(i3);
                                break;
                        }
                    }
                    if (this.oldPlug != historyItem.batteryPlugType) {
                        this.oldPlug = historyItem.batteryPlugType;
                        sb.append(z ? ",Bp=" : " plug=");
                        int i4 = this.oldPlug;
                        if (i4 == 0) {
                            sb.append(z ? "n" : "none");
                        } else if (i4 == 1) {
                            sb.append(z ? FullBackup.APK_TREE_TOKEN : "ac");
                        } else if (i4 == 2) {
                            sb.append(z ? "u" : Context.USB_SERVICE);
                        } else if (i4 == 4) {
                            sb.append(z ? "w" : "wireless");
                        } else {
                            sb.append(i4);
                        }
                    }
                    if (this.oldTemp != historyItem.batteryTemperature) {
                        this.oldTemp = historyItem.batteryTemperature;
                        sb.append(z ? ",Bt=" : " temp=");
                        sb.append(this.oldTemp);
                    }
                    if (this.oldVolt != historyItem.batteryVoltage) {
                        this.oldVolt = historyItem.batteryVoltage;
                        sb.append(z ? ",Bv=" : " volt=");
                        sb.append(this.oldVolt);
                    }
                    int i5 = historyItem.batteryChargeUAh / 1000;
                    if (this.oldChargeMAh != i5) {
                        this.oldChargeMAh = i5;
                        sb.append(z ? ",Bcc=" : " charge=");
                        sb.append(this.oldChargeMAh);
                    }
                    BatteryStats.printBitDescriptions(sb, this.oldState, historyItem.states, historyItem.wakelockTag, BatteryStats.HISTORY_STATE_DESCRIPTIONS, !z);
                    BatteryStats.printBitDescriptions(sb, this.oldState2, historyItem.states2, null, BatteryStats.HISTORY_STATE2_DESCRIPTIONS, !z);
                    if (historyItem.wakeReasonTag != null) {
                        if (z) {
                            sb.append(",wr=");
                            sb.append(historyItem.wakeReasonTag.poolIdx);
                        } else {
                            sb.append(" wake_reason=");
                            sb.append(historyItem.wakeReasonTag.uid);
                            sb.append(":\"");
                            sb.append(historyItem.wakeReasonTag.string);
                            sb.append("\"");
                        }
                    }
                    if (historyItem.eventCode != 0) {
                        if (z) {
                            str = ",";
                        }
                        sb.append(str);
                        if ((historyItem.eventCode & 32768) != 0) {
                            sb.append("+");
                        } else if ((historyItem.eventCode & 16384) != 0) {
                            sb.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        if (z) {
                            strArr = BatteryStats.HISTORY_EVENT_CHECKIN_NAMES;
                        } else {
                            strArr = BatteryStats.HISTORY_EVENT_NAMES;
                        }
                        int i6 = historyItem.eventCode & HistoryItem.EVENT_TYPE_MASK;
                        if (i6 >= 0 && i6 < strArr.length) {
                            sb.append(strArr[i6]);
                        } else {
                            sb.append(z ? "Ev" : "event");
                            sb.append(i6);
                        }
                        sb.append("=");
                        if (z) {
                            sb.append(historyItem.eventTag.poolIdx);
                        } else {
                            sb.append(BatteryStats.HISTORY_EVENT_INT_FORMATTERS[i6].applyAsString(historyItem.eventTag.uid));
                            sb.append(":\"");
                            sb.append(historyItem.eventTag.string);
                            sb.append("\"");
                        }
                    }
                    sb.append("\n");
                    if (historyItem.stepDetails != null) {
                        if (!z) {
                            sb.append("                 Details: cpu=");
                            sb.append(historyItem.stepDetails.userTime);
                            sb.append("u+");
                            sb.append(historyItem.stepDetails.systemTime);
                            sb.append("s");
                            if (historyItem.stepDetails.appCpuUid1 >= 0) {
                                sb.append(" (");
                                printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid1, historyItem.stepDetails.appCpuUTime1, historyItem.stepDetails.appCpuSTime1);
                                if (historyItem.stepDetails.appCpuUid2 >= 0) {
                                    sb.append(", ");
                                    printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid2, historyItem.stepDetails.appCpuUTime2, historyItem.stepDetails.appCpuSTime2);
                                }
                                if (historyItem.stepDetails.appCpuUid3 >= 0) {
                                    sb.append(", ");
                                    printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid3, historyItem.stepDetails.appCpuUTime3, historyItem.stepDetails.appCpuSTime3);
                                }
                                sb.append(')');
                            }
                            sb.append("\n");
                            sb.append("                          /proc/stat=");
                            sb.append(historyItem.stepDetails.statUserTime);
                            sb.append(" usr, ");
                            sb.append(historyItem.stepDetails.statSystemTime);
                            sb.append(" sys, ");
                            sb.append(historyItem.stepDetails.statIOWaitTime);
                            sb.append(" io, ");
                            sb.append(historyItem.stepDetails.statIrqTime);
                            sb.append(" irq, ");
                            sb.append(historyItem.stepDetails.statSoftIrqTime);
                            sb.append(" sirq, ");
                            sb.append(historyItem.stepDetails.statIdlTime);
                            sb.append(" idle");
                            int i7 = historyItem.stepDetails.statUserTime + historyItem.stepDetails.statSystemTime + historyItem.stepDetails.statIOWaitTime + historyItem.stepDetails.statIrqTime + historyItem.stepDetails.statSoftIrqTime;
                            int i8 = historyItem.stepDetails.statIdlTime + i7;
                            if (i8 > 0) {
                                sb.append(" (");
                                sb.append(String.format("%.1f%%", Float.valueOf((i7 / i8) * 100.0f)));
                                sb.append(" of ");
                                StringBuilder sb2 = new StringBuilder(64);
                                BatteryStats.formatTimeMsNoSpace(sb2, i8 * 10);
                                sb.append((CharSequence) sb2);
                                sb.append(")");
                            }
                            sb.append(", PlatformIdleStat ");
                            sb.append(historyItem.stepDetails.statPlatformIdleState);
                            sb.append("\n");
                            sb.append(", SubsystemPowerState ");
                            sb.append(historyItem.stepDetails.statSubsystemPowerState);
                            sb.append("\n");
                        } else {
                            sb.append(9);
                            sb.append(',');
                            sb.append(BatteryStats.HISTORY_DATA);
                            sb.append(",0,Dcpu=");
                            sb.append(historyItem.stepDetails.userTime);
                            sb.append(SettingsStringUtil.DELIMITER);
                            sb.append(historyItem.stepDetails.systemTime);
                            if (historyItem.stepDetails.appCpuUid1 >= 0) {
                                printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid1, historyItem.stepDetails.appCpuUTime1, historyItem.stepDetails.appCpuSTime1);
                                if (historyItem.stepDetails.appCpuUid2 >= 0) {
                                    printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid2, historyItem.stepDetails.appCpuUTime2, historyItem.stepDetails.appCpuSTime2);
                                }
                                if (historyItem.stepDetails.appCpuUid3 >= 0) {
                                    printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid3, historyItem.stepDetails.appCpuUTime3, historyItem.stepDetails.appCpuSTime3);
                                }
                            }
                            sb.append("\n");
                            sb.append(9);
                            sb.append(',');
                            sb.append(BatteryStats.HISTORY_DATA);
                            sb.append(",0,Dpst=");
                            sb.append(historyItem.stepDetails.statUserTime);
                            sb.append(',');
                            sb.append(historyItem.stepDetails.statSystemTime);
                            sb.append(',');
                            sb.append(historyItem.stepDetails.statIOWaitTime);
                            sb.append(',');
                            sb.append(historyItem.stepDetails.statIrqTime);
                            sb.append(',');
                            sb.append(historyItem.stepDetails.statSoftIrqTime);
                            sb.append(',');
                            sb.append(historyItem.stepDetails.statIdlTime);
                            sb.append(',');
                            if (historyItem.stepDetails.statPlatformIdleState != null) {
                                sb.append(historyItem.stepDetails.statPlatformIdleState);
                                if (historyItem.stepDetails.statSubsystemPowerState != null) {
                                    sb.append(',');
                                }
                            }
                            if (historyItem.stepDetails.statSubsystemPowerState != null) {
                                sb.append(historyItem.stepDetails.statSubsystemPowerState);
                            }
                            sb.append("\n");
                        }
                    }
                    this.oldState = historyItem.states;
                    this.oldState2 = historyItem.states2;
                }
            }
            return sb.toString();
        }

        private void printStepCpuUidDetails(StringBuilder sb, int i, int i2, int i3) {
            UserHandle.formatUid(sb, i);
            sb.append("=");
            sb.append(i2);
            sb.append("u+");
            sb.append(i3);
            sb.append("s");
        }

        private void printStepCpuUidCheckinDetails(StringBuilder sb, int i, int i2, int i3) {
            sb.append('/');
            sb.append(i);
            sb.append(SettingsStringUtil.DELIMITER);
            sb.append(i2);
            sb.append(SettingsStringUtil.DELIMITER);
            sb.append(i3);
        }
    }

    private void printSizeValue(PrintWriter printWriter, long j) {
        String str;
        float f = j;
        if (f >= 10240.0f) {
            f /= 1024.0f;
            str = "KB";
        } else {
            str = "";
        }
        if (f >= 10240.0f) {
            f /= 1024.0f;
            str = "MB";
        }
        if (f >= 10240.0f) {
            f /= 1024.0f;
            str = "GB";
        }
        if (f >= 10240.0f) {
            f /= 1024.0f;
            str = "TB";
        }
        if (f >= 10240.0f) {
            f /= 1024.0f;
            str = "PB";
        }
        printWriter.print((int) f);
        printWriter.print(str);
    }

    private static boolean dumpTimeEstimate(PrintWriter printWriter, String str, String str2, String str3, long j) {
        if (j < 0) {
            return false;
        }
        printWriter.print(str);
        printWriter.print(str2);
        printWriter.print(str3);
        StringBuilder sb = new StringBuilder(64);
        formatTimeMs(sb, j);
        printWriter.print(sb);
        printWriter.println();
        return true;
    }

    private static boolean dumpDurationSteps(PrintWriter printWriter, String str, String str2, LevelStepTracker levelStepTracker, boolean z) {
        int i;
        int i2;
        char c;
        char c2 = 0;
        if (levelStepTracker == null || (i = levelStepTracker.mNumStepDurations) <= 0) {
            return false;
        }
        if (!z) {
            printWriter.println(str2);
        }
        String[] strArr = new String[5];
        int i3 = 0;
        while (true) {
            char c3 = 1;
            if (i3 >= i) {
                return true;
            }
            long durationAt = levelStepTracker.getDurationAt(i3);
            int levelAt = levelStepTracker.getLevelAt(i3);
            long initModeAt = levelStepTracker.getInitModeAt(i3);
            long modModeAt = levelStepTracker.getModModeAt(i3);
            if (z) {
                strArr[c2] = Long.toString(durationAt);
                strArr[1] = Integer.toString(levelAt);
                if ((modModeAt & 3) == 0) {
                    i2 = i;
                    int i4 = ((int) (initModeAt & 3)) + 1;
                    if (i4 == 1) {
                        strArr[2] = "s-";
                    } else if (i4 == 2) {
                        strArr[2] = "s+";
                    } else if (i4 == 3) {
                        strArr[2] = "sd";
                    } else if (i4 != 4) {
                        strArr[2] = "?";
                    } else {
                        strArr[2] = "sds";
                    }
                } else {
                    i2 = i;
                    strArr[2] = "";
                }
                if ((modModeAt & 4) == 0) {
                    strArr[3] = (initModeAt & 4) != 0 ? "p+" : "p-";
                } else {
                    strArr[3] = "";
                }
                if ((modModeAt & 8) == 0) {
                    strArr[4] = (8 & initModeAt) != 0 ? "i+" : "i-";
                } else {
                    strArr[4] = "";
                }
                dumpLine(printWriter, 0, "i", str2, strArr);
                c2 = 0;
            } else {
                i2 = i;
                printWriter.print(str);
                printWriter.print("#");
                printWriter.print(i3);
                printWriter.print(": ");
                TimeUtils.formatDuration(durationAt, printWriter);
                printWriter.print(" to ");
                printWriter.print(levelAt);
                if ((modModeAt & 3) == 0) {
                    printWriter.print(" (");
                    int i5 = ((int) (initModeAt & 3)) + 1;
                    if (i5 == 1) {
                        printWriter.print("screen-off");
                    } else if (i5 == 2) {
                        printWriter.print("screen-on");
                    } else if (i5 == 3) {
                        printWriter.print("screen-doze");
                    } else if (i5 == 4) {
                        printWriter.print("screen-doze-suspend");
                    } else {
                        printWriter.print("screen-?");
                    }
                    c = 1;
                } else {
                    c = c2;
                }
                if ((modModeAt & 4) == 0) {
                    printWriter.print(c != 0 ? ", " : " (");
                    printWriter.print((initModeAt & 4) != 0 ? "power-save-on" : "power-save-off");
                    c = 1;
                }
                if ((modModeAt & 8) == 0) {
                    printWriter.print(c != 0 ? ", " : " (");
                    printWriter.print((initModeAt & 8) != 0 ? "device-idle-on" : "device-idle-off");
                } else {
                    c3 = c;
                }
                if (c3 != 0) {
                    printWriter.print(")");
                }
                printWriter.println();
            }
            i3++;
            i = i2;
        }
    }

    private static void dumpDurationSteps(ProtoOutputStream protoOutputStream, long j, LevelStepTracker levelStepTracker) {
        int i;
        if (levelStepTracker == null) {
            return;
        }
        int i2 = levelStepTracker.mNumStepDurations;
        for (int i3 = 0; i3 < i2; i3++) {
            long jStart = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, levelStepTracker.getDurationAt(i3));
            protoOutputStream.write(1120986464258L, levelStepTracker.getLevelAt(i3));
            long initModeAt = levelStepTracker.getInitModeAt(i3);
            long modModeAt = levelStepTracker.getModModeAt(i3);
            int i4 = 3;
            if ((modModeAt & 3) == 0) {
                int i5 = ((int) (3 & initModeAt)) + 1;
                if (i5 == 1) {
                    i = 2;
                } else if (i5 == 2) {
                    i = 1;
                } else if (i5 != 3) {
                    i = 4;
                    if (i5 != 4) {
                        i = 5;
                    }
                } else {
                    i = 3;
                }
            } else {
                i = 0;
            }
            protoOutputStream.write(1159641169923L, i);
            protoOutputStream.write(1159641169924L, (modModeAt & 4) == 0 ? (4 & initModeAt) != 0 ? 1 : 2 : 0);
            if ((modModeAt & 8) != 0) {
                i4 = 0;
            } else if ((initModeAt & 8) != 0) {
                i4 = 2;
            }
            protoOutputStream.write(1159641169925L, i4);
            protoOutputStream.end(jStart);
        }
    }

    private void dumpHistoryLocked(PrintWriter printWriter, int i, long j, boolean z) {
        HistoryPrinter historyPrinter = new HistoryPrinter();
        HistoryItem historyItem = new HistoryItem();
        long j2 = -1;
        long j3 = -1;
        boolean z2 = false;
        while (getNextHistoryLocked(historyItem)) {
            long j4 = historyItem.time;
            long j5 = j2 < 0 ? j4 : j2;
            if (historyItem.time >= j) {
                if (j >= 0 && !z2) {
                    if (historyItem.cmd == 5 || historyItem.cmd == 7 || historyItem.cmd == 4 || historyItem.cmd == 8) {
                        historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                        historyItem.cmd = (byte) 0;
                    } else if (historyItem.currentTime != 0) {
                        byte b = historyItem.cmd;
                        historyItem.cmd = (byte) 5;
                        historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                        historyItem.cmd = b;
                    }
                    z2 = true;
                }
                boolean z3 = z2;
                historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                z2 = z3;
            }
            j3 = j4;
            j2 = j5;
        }
        if (j >= 0) {
            commitCurrentHistoryBatchLocked();
            printWriter.print(z ? "NEXT: " : "  NEXT: ");
            printWriter.println(j3 + 1);
        }
    }

    private void dumpDailyLevelStepSummary(PrintWriter printWriter, String str, String str2, LevelStepTracker levelStepTracker, StringBuilder sb, int[] iArr) {
        if (levelStepTracker == null) {
            return;
        }
        long jComputeTimeEstimate = levelStepTracker.computeTimeEstimate(0L, 0L, iArr);
        if (jComputeTimeEstimate >= 0) {
            printWriter.print(str);
            printWriter.print(str2);
            printWriter.print(" total time: ");
            sb.setLength(0);
            formatTimeMs(sb, jComputeTimeEstimate);
            printWriter.print(sb);
            printWriter.print(" (from ");
            printWriter.print(iArr[0]);
            printWriter.println(" steps)");
        }
        int i = 0;
        while (true) {
            int[] iArr2 = STEP_LEVEL_MODES_OF_INTEREST;
            if (i >= iArr2.length) {
                return;
            }
            long jComputeTimeEstimate2 = levelStepTracker.computeTimeEstimate(iArr2[i], STEP_LEVEL_MODE_VALUES[i], iArr);
            if (jComputeTimeEstimate2 > 0) {
                printWriter.print(str);
                printWriter.print(str2);
                printWriter.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                printWriter.print(STEP_LEVEL_MODE_LABELS[i]);
                printWriter.print(" time: ");
                sb.setLength(0);
                formatTimeMs(sb, jComputeTimeEstimate2);
                printWriter.print(sb);
                printWriter.print(" (from ");
                printWriter.print(iArr[0]);
                printWriter.println(" steps)");
            }
            i++;
        }
    }

    private void dumpDailyPackageChanges(PrintWriter printWriter, String str, ArrayList<PackageChange> arrayList) {
        if (arrayList == null) {
            return;
        }
        printWriter.print(str);
        printWriter.println("Package changes:");
        for (int i = 0; i < arrayList.size(); i++) {
            PackageChange packageChange = arrayList.get(i);
            if (packageChange.mUpdate) {
                printWriter.print(str);
                printWriter.print("  Update ");
                printWriter.print(packageChange.mPackageName);
                printWriter.print(" vers=");
                printWriter.println(packageChange.mVersionCode);
            } else {
                printWriter.print(str);
                printWriter.print("  Uninstall ");
                printWriter.println(packageChange.mPackageName);
            }
        }
    }

    public void dumpLocked(Context context, PrintWriter printWriter, int i, int i2, long j) {
        boolean z;
        String str;
        boolean z2;
        prepareForDumpLocked();
        boolean z3 = (i & 14) != 0;
        if ((i & 8) != 0 || !z3) {
            long historyTotalSize = getHistoryTotalSize();
            long historyUsedSize = getHistoryUsedSize();
            if (startIteratingHistoryLocked()) {
                try {
                    printWriter.print("Battery History (");
                    printWriter.print((100 * historyUsedSize) / historyTotalSize);
                    printWriter.print("% used, ");
                    printSizeValue(printWriter, historyUsedSize);
                    printWriter.print(" used of ");
                    printSizeValue(printWriter, historyTotalSize);
                    printWriter.print(", ");
                    printWriter.print(getHistoryStringPoolSize());
                    printWriter.print(" strings using ");
                    printSizeValue(printWriter, getHistoryStringPoolBytes());
                    printWriter.println("):");
                    dumpHistoryLocked(printWriter, i, j, false);
                    printWriter.println();
                    finishIteratingHistoryLocked();
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            if (startIteratingOldHistoryLocked()) {
                try {
                    HistoryItem historyItem = new HistoryItem();
                    printWriter.println("Old battery History:");
                    HistoryPrinter historyPrinter = new HistoryPrinter();
                    long j2 = -1;
                    while (getNextOldHistoryLocked(historyItem)) {
                        if (j2 < 0) {
                            j2 = historyItem.time;
                        }
                        long j3 = j2;
                        historyPrinter.printNextItem(printWriter, historyItem, j3, false, (i & 32) != 0);
                        j2 = j3;
                    }
                    printWriter.println();
                    finishIteratingOldHistoryLocked();
                } catch (Throwable th2) {
                    finishIteratingOldHistoryLocked();
                    throw th2;
                }
            }
        }
        if (z3 && (i & 6) == 0) {
            return;
        }
        if (z3) {
            z = z3;
        } else {
            SparseArray<? extends Uid> uidStats = getUidStats();
            int size = uidStats.size();
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            int i3 = 0;
            boolean z4 = false;
            while (i3 < size) {
                SparseArray<? extends Uid.Pid> pidStats = uidStats.valueAt(i3).getPidStats();
                if (pidStats != null) {
                    int i4 = 0;
                    while (i4 < pidStats.size()) {
                        Uid.Pid pidValueAt = pidStats.valueAt(i4);
                        if (!z4) {
                            printWriter.println("Per-PID Stats:");
                            z4 = true;
                        }
                        long j4 = pidValueAt.mWakeSumMs + (pidValueAt.mWakeNesting > 0 ? jElapsedRealtime - pidValueAt.mWakeStartMs : 0L);
                        printWriter.print("  PID ");
                        printWriter.print(pidStats.keyAt(i4));
                        printWriter.print(" wake time: ");
                        TimeUtils.formatDuration(j4, printWriter);
                        printWriter.println("");
                        i4++;
                        z3 = z3;
                    }
                }
                i3++;
                z3 = z3;
            }
            z = z3;
            if (z4) {
                printWriter.println();
            }
        }
        if (!z || (i & 2) != 0) {
            if (dumpDurationSteps(printWriter, "  ", "Discharge step durations:", getDischargeLevelStepTracker(), false)) {
                long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (jComputeBatteryTimeRemaining >= 0) {
                    printWriter.print("  Estimated discharge time remaining: ");
                    TimeUtils.formatDuration(jComputeBatteryTimeRemaining / 1000, printWriter);
                    printWriter.println();
                }
                LevelStepTracker dischargeLevelStepTracker = getDischargeLevelStepTracker();
                int i5 = 0;
                while (true) {
                    int[] iArr = STEP_LEVEL_MODES_OF_INTEREST;
                    if (i5 >= iArr.length) {
                        break;
                    }
                    dumpTimeEstimate(printWriter, "  Estimated ", STEP_LEVEL_MODE_LABELS[i5], " time: ", dischargeLevelStepTracker.computeTimeEstimate(iArr[i5], STEP_LEVEL_MODE_VALUES[i5], null));
                    i5++;
                }
                printWriter.println();
            }
            if (dumpDurationSteps(printWriter, "  ", "Charge step durations:", getChargeLevelStepTracker(), false)) {
                long jComputeChargeTimeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (jComputeChargeTimeRemaining >= 0) {
                    printWriter.print("  Estimated charge time remaining: ");
                    TimeUtils.formatDuration(jComputeChargeTimeRemaining / 1000, printWriter);
                    printWriter.println();
                }
                printWriter.println();
            }
        }
        if (z && (i & 4) == 0) {
            z2 = false;
        } else {
            printWriter.println("Daily stats:");
            printWriter.print("  Current start time: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getCurrentDailyStartTime()).toString());
            printWriter.print("  Next min deadline: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMinDailyDeadline()).toString());
            printWriter.print("  Next max deadline: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMaxDailyDeadline()).toString());
            StringBuilder sb = new StringBuilder(64);
            int[] iArr2 = new int[1];
            LevelStepTracker dailyDischargeLevelStepTracker = getDailyDischargeLevelStepTracker();
            LevelStepTracker dailyChargeLevelStepTracker = getDailyChargeLevelStepTracker();
            ArrayList<PackageChange> dailyPackageChanges = getDailyPackageChanges();
            if (dailyDischargeLevelStepTracker.mNumStepDurations <= 0 && dailyChargeLevelStepTracker.mNumStepDurations <= 0 && dailyPackageChanges == null) {
                str = "    ";
            } else if ((i & 4) != 0 || !z) {
                str = "    ";
                if (dumpDurationSteps(printWriter, str, "  Current daily discharge step durations:", dailyDischargeLevelStepTracker, false)) {
                    dumpDailyLevelStepSummary(printWriter, "      ", "Discharge", dailyDischargeLevelStepTracker, sb, iArr2);
                }
                if (dumpDurationSteps(printWriter, str, "  Current daily charge step durations:", dailyChargeLevelStepTracker, false)) {
                    dumpDailyLevelStepSummary(printWriter, "      ", "Charge", dailyChargeLevelStepTracker, sb, iArr2);
                }
                dumpDailyPackageChanges(printWriter, str, dailyPackageChanges);
            } else {
                printWriter.println("  Current daily steps:");
                str = "    ";
                dumpDailyLevelStepSummary(printWriter, "    ", "Discharge", dailyDischargeLevelStepTracker, sb, iArr2);
                dumpDailyLevelStepSummary(printWriter, "    ", "Charge", dailyChargeLevelStepTracker, sb, iArr2);
            }
            int i6 = 0;
            while (true) {
                DailyItem dailyItemLocked = getDailyItemLocked(i6);
                if (dailyItemLocked == null) {
                    break;
                }
                int i7 = i6 + 1;
                int i8 = i & 4;
                if (i8 != 0) {
                    printWriter.println();
                }
                printWriter.print("  Daily from ");
                printWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dailyItemLocked.mStartTime).toString());
                printWriter.print(" to ");
                printWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dailyItemLocked.mEndTime).toString());
                printWriter.println(SettingsStringUtil.DELIMITER);
                if (i8 != 0 || !z) {
                    if (dumpDurationSteps(printWriter, "      ", "    Discharge step durations:", dailyItemLocked.mDischargeSteps, false)) {
                        dumpDailyLevelStepSummary(printWriter, "        ", "Discharge", dailyItemLocked.mDischargeSteps, sb, iArr2);
                    }
                    if (dumpDurationSteps(printWriter, "      ", "    Charge step durations:", dailyItemLocked.mChargeSteps, false)) {
                        dumpDailyLevelStepSummary(printWriter, "        ", "Charge", dailyItemLocked.mChargeSteps, sb, iArr2);
                    }
                    dumpDailyPackageChanges(printWriter, str, dailyItemLocked.mPackageChanges);
                } else {
                    dumpDailyLevelStepSummary(printWriter, "    ", "Discharge", dailyItemLocked.mDischargeSteps, sb, iArr2);
                    dumpDailyLevelStepSummary(printWriter, "    ", "Charge", dailyItemLocked.mChargeSteps, sb, iArr2);
                }
                i6 = i7;
            }
            z2 = false;
            printWriter.println();
        }
        if (z && (i & 2) == 0) {
            return;
        }
        printWriter.println("Statistics since last charge:");
        printWriter.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
        dumpLocked(context, printWriter, "", 0, i2, (i & 64) != 0 ? true : z2);
        printWriter.println();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void dumpCheckinLocked(Context context, PrintWriter printWriter, List<ApplicationInfo> list, int i, long j) {
        prepareForDumpLocked();
        dumpLine(printWriter, 0, "i", VERSION_DATA, 32, Integer.valueOf(getParcelVersion()), getStartPlatformVersion(), getEndPlatformVersion());
        getHistoryBaseTime();
        SystemClock.elapsedRealtime();
        if ((i & 24) != 0 && startIteratingHistoryLocked()) {
            for (int i2 = 0; i2 < getHistoryStringPoolSize(); i2++) {
                try {
                    printWriter.print(9);
                    printWriter.print(',');
                    printWriter.print(HISTORY_STRING_POOL);
                    printWriter.print(',');
                    printWriter.print(i2);
                    printWriter.print(",");
                    printWriter.print(getHistoryTagPoolUid(i2));
                    printWriter.print(",\"");
                    printWriter.print(getHistoryTagPoolString(i2).replace("\\", "\\\\").replace("\"", "\\\""));
                    printWriter.print("\"");
                    printWriter.println();
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            dumpHistoryLocked(printWriter, i, j, true);
            finishIteratingHistoryLocked();
        }
        if ((i & 8) != 0) {
            return;
        }
        if (list != null) {
            SparseArray sparseArray = new SparseArray();
            for (int i3 = 0; i3 < list.size(); i3++) {
                ApplicationInfo applicationInfo = list.get(i3);
                Pair pair = (Pair) sparseArray.get(UserHandle.getAppId(applicationInfo.uid));
                if (pair == null) {
                    pair = new Pair(new ArrayList(), new MutableBoolean(false));
                    sparseArray.put(UserHandle.getAppId(applicationInfo.uid), pair);
                }
                ((ArrayList) pair.first).add(applicationInfo.packageName);
            }
            SparseArray<? extends Uid> uidStats = getUidStats();
            int size = uidStats.size();
            String[] strArr = new String[2];
            for (int i4 = 0; i4 < size; i4++) {
                int appId = UserHandle.getAppId(uidStats.keyAt(i4));
                Pair pair2 = (Pair) sparseArray.get(appId);
                if (pair2 != null && !((MutableBoolean) pair2.second).value) {
                    ((MutableBoolean) pair2.second).value = true;
                    for (int i5 = 0; i5 < ((ArrayList) pair2.first).size(); i5++) {
                        strArr[0] = Integer.toString(appId);
                        strArr[1] = (String) ((ArrayList) pair2.first).get(i5);
                        dumpLine(printWriter, 0, "i", "uid", strArr);
                    }
                }
            }
        }
        if ((i & 4) == 0) {
            dumpDurationSteps(printWriter, "", DISCHARGE_STEP_DATA, getDischargeLevelStepTracker(), true);
            String[] strArr2 = new String[1];
            long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (jComputeBatteryTimeRemaining >= 0) {
                strArr2[0] = Long.toString(jComputeBatteryTimeRemaining);
                dumpLine(printWriter, 0, "i", DISCHARGE_TIME_REMAIN_DATA, strArr2);
            }
            dumpDurationSteps(printWriter, "", CHARGE_STEP_DATA, getChargeLevelStepTracker(), true);
            long jComputeChargeTimeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (jComputeChargeTimeRemaining >= 0) {
                strArr2[0] = Long.toString(jComputeChargeTimeRemaining);
                dumpLine(printWriter, 0, "i", CHARGE_TIME_REMAIN_DATA, strArr2);
            }
            dumpCheckinLocked(context, printWriter, 0, -1, (i & 64) != 0);
        }
    }

    public void dumpProtoLocked(Context context, FileDescriptor fileDescriptor, List<ApplicationInfo> list, int i, long j) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        prepareForDumpLocked();
        if ((i & 24) != 0) {
            dumpProtoHistoryLocked(protoOutputStream, i, j);
            protoOutputStream.flush();
            return;
        }
        long jStart = protoOutputStream.start(1146756268033L);
        protoOutputStream.write(1120986464257L, 32);
        protoOutputStream.write(1112396529666L, getParcelVersion());
        protoOutputStream.write(1138166333443L, getStartPlatformVersion());
        protoOutputStream.write(1138166333444L, getEndPlatformVersion());
        if ((i & 4) == 0) {
            BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, (i & 64) != 0);
            batteryStatsHelper.create(this);
            batteryStatsHelper.refreshStats(0, -1);
            dumpProtoAppsLocked(protoOutputStream, batteryStatsHelper, list);
            dumpProtoSystemLocked(protoOutputStream, batteryStatsHelper);
        }
        protoOutputStream.end(jStart);
        protoOutputStream.flush();
    }

    private void dumpProtoAppsLocked(ProtoOutputStream protoOutputStream, BatteryStatsHelper batteryStatsHelper, List<ApplicationInfo> list) {
        ArrayList<String> arrayList;
        long j;
        long j2;
        long[] jArr;
        SparseArray sparseArray;
        long[] cpuFreqTimes;
        ArrayMap<String, ? extends Uid.Pkg> arrayMap;
        ArrayList arrayList2;
        long j3;
        SparseArray sparseArray2;
        ProtoOutputStream protoOutputStream2 = protoOutputStream;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j4 = jElapsedRealtime * 1000;
        long batteryUptime = getBatteryUptime(jUptimeMillis);
        SparseArray sparseArray3 = new SparseArray();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ApplicationInfo applicationInfo = list.get(i);
                int appId = UserHandle.getAppId(applicationInfo.uid);
                ArrayList arrayList3 = (ArrayList) sparseArray3.get(appId);
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                    sparseArray3.put(appId, arrayList3);
                }
                arrayList3.add(applicationInfo.packageName);
            }
        }
        SparseArray sparseArray4 = new SparseArray();
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null) {
            for (int i2 = 0; i2 < usageList.size(); i2++) {
                BatterySipper batterySipper = usageList.get(i2);
                if (batterySipper.drainType == BatterySipper.DrainType.APP) {
                    sparseArray4.put(batterySipper.uidObj.getUid(), batterySipper);
                }
            }
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size = uidStats.size();
        int i3 = 0;
        while (i3 < size) {
            int i4 = size;
            long jStart = protoOutputStream2.start(2246267895813L);
            Uid uidValueAt = uidStats.valueAt(i3);
            int iKeyAt = uidStats.keyAt(i3);
            SparseArray sparseArray5 = sparseArray4;
            SparseArray<? extends Uid> sparseArray6 = uidStats;
            protoOutputStream2.write(1120986464257L, iKeyAt);
            ArrayList arrayList4 = (ArrayList) sparseArray3.get(UserHandle.getAppId(iKeyAt));
            if (arrayList4 == null) {
                arrayList4 = new ArrayList();
            }
            int i5 = i3;
            ArrayMap<String, ? extends Uid.Pkg> packageStats = uidValueAt.getPackageStats();
            int size2 = packageStats.size() - 1;
            while (true) {
                arrayList = arrayList4;
                if (size2 < 0) {
                    break;
                }
                String strKeyAt = packageStats.keyAt(size2);
                ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats = packageStats.valueAt(size2).getServiceStats();
                if (serviceStats.size() == 0) {
                    arrayMap = packageStats;
                    arrayList2 = arrayList;
                    j3 = batteryUptime;
                    sparseArray2 = sparseArray3;
                } else {
                    long jStart2 = protoOutputStream2.start(2246267895810L);
                    protoOutputStream2.write(1138166333441L, strKeyAt);
                    arrayList.remove(strKeyAt);
                    int size3 = serviceStats.size() - 1;
                    while (size3 >= 0) {
                        Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size3);
                        long jRoundUsToMs = roundUsToMs(servValueAt.getStartTime(batteryUptime, 0));
                        ArrayMap<String, ? extends Uid.Pkg> arrayMap2 = packageStats;
                        int starts = servValueAt.getStarts(0);
                        int launches = servValueAt.getLaunches(0);
                        if (jRoundUsToMs == 0 && starts == 0 && launches == 0) {
                            protoOutputStream2 = protoOutputStream;
                        } else {
                            protoOutputStream2 = protoOutputStream;
                            long jStart3 = protoOutputStream2.start(2246267895810L);
                            protoOutputStream2.write(1138166333441L, serviceStats.keyAt(size3));
                            protoOutputStream2.write(1112396529666L, jRoundUsToMs);
                            protoOutputStream2.write(1120986464259L, starts);
                            protoOutputStream2.write(1120986464260L, launches);
                            protoOutputStream2.end(jStart3);
                        }
                        size3--;
                        packageStats = arrayMap2;
                        batteryUptime = batteryUptime;
                        arrayList = arrayList;
                        sparseArray3 = sparseArray3;
                    }
                    arrayMap = packageStats;
                    arrayList2 = arrayList;
                    j3 = batteryUptime;
                    sparseArray2 = sparseArray3;
                    protoOutputStream2.end(jStart2);
                }
                size2--;
                packageStats = arrayMap;
                j4 = j4;
                jElapsedRealtime = jElapsedRealtime;
                batteryUptime = j3;
                arrayList4 = arrayList2;
                sparseArray3 = sparseArray2;
            }
            ArrayMap<String, ? extends Uid.Pkg> arrayMap3 = packageStats;
            long j5 = jElapsedRealtime;
            long j6 = j4;
            long j7 = batteryUptime;
            SparseArray sparseArray7 = sparseArray3;
            for (String str : arrayList) {
                long jStart4 = protoOutputStream2.start(2246267895810L);
                protoOutputStream2.write(1138166333441L, str);
                protoOutputStream2.end(jStart4);
            }
            if (uidValueAt.getAggregatedPartialWakelockTimer() != null) {
                Timer aggregatedPartialWakelockTimer = uidValueAt.getAggregatedPartialWakelockTimer();
                j = j5;
                long totalDurationMsLocked = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j);
                Timer subTimer = aggregatedPartialWakelockTimer.getSubTimer();
                long totalDurationMsLocked2 = subTimer != null ? subTimer.getTotalDurationMsLocked(j) : 0L;
                long jStart5 = protoOutputStream2.start(1146756268056L);
                protoOutputStream2.write(1112396529665L, totalDurationMsLocked);
                protoOutputStream2.write(1112396529666L, totalDurationMsLocked2);
                protoOutputStream2.end(jStart5);
            } else {
                j = j5;
            }
            int i6 = i5;
            int i7 = iKeyAt;
            SparseArray sparseArray8 = sparseArray5;
            ArrayMap<String, ? extends Uid.Pkg> arrayMap4 = arrayMap3;
            dumpTimer(protoOutputStream, 1146756268040L, uidValueAt.getAudioTurnedOnTimer(), j6, 0);
            dumpControllerActivityProto(protoOutputStream2, 1146756268035L, uidValueAt.getBluetoothControllerActivity(), 0);
            Timer bluetoothScanTimer = uidValueAt.getBluetoothScanTimer();
            if (bluetoothScanTimer != null) {
                long jStart6 = protoOutputStream2.start(1146756268038L);
                dumpTimer(protoOutputStream, 1146756268033L, bluetoothScanTimer, j6, 0);
                dumpTimer(protoOutputStream, 1146756268034L, uidValueAt.getBluetoothScanBackgroundTimer(), j6, 0);
                dumpTimer(protoOutputStream, 1146756268035L, uidValueAt.getBluetoothUnoptimizedScanTimer(), j6, 0);
                dumpTimer(protoOutputStream, 1146756268036L, uidValueAt.getBluetoothUnoptimizedScanBackgroundTimer(), j6, 0);
                protoOutputStream2.write(1120986464261L, uidValueAt.getBluetoothScanResultCounter() != null ? uidValueAt.getBluetoothScanResultCounter().getCountLocked(0) : 0);
                protoOutputStream2.write(1120986464262L, uidValueAt.getBluetoothScanResultBgCounter() != null ? uidValueAt.getBluetoothScanResultBgCounter().getCountLocked(0) : 0);
                protoOutputStream2.end(jStart6);
            }
            dumpTimer(protoOutputStream, 1146756268041L, uidValueAt.getCameraTurnedOnTimer(), j6, 0);
            long jStart7 = protoOutputStream2.start(1146756268039L);
            Uid uid = uidValueAt;
            protoOutputStream2.write(1112396529665L, roundUsToMs(uid.getUserCpuTimeUs(0)));
            protoOutputStream2.write(1112396529666L, roundUsToMs(uid.getSystemCpuTimeUs(0)));
            long[] cpuFreqs = getCpuFreqs();
            if (cpuFreqs != null && (cpuFreqTimes = uid.getCpuFreqTimes(0)) != null && cpuFreqTimes.length == cpuFreqs.length) {
                long[] screenOffCpuFreqTimes = uid.getScreenOffCpuFreqTimes(0);
                if (screenOffCpuFreqTimes == null) {
                    screenOffCpuFreqTimes = new long[cpuFreqTimes.length];
                }
                int i8 = 0;
                while (i8 < cpuFreqTimes.length) {
                    long jStart8 = protoOutputStream2.start(2246267895811L);
                    int i9 = i8 + 1;
                    protoOutputStream2.write(1120986464257L, i9);
                    protoOutputStream2.write(1112396529666L, cpuFreqTimes[i8]);
                    protoOutputStream2.write(1112396529667L, screenOffCpuFreqTimes[i8]);
                    protoOutputStream2.end(jStart8);
                    i8 = i9;
                    i6 = i6;
                    i7 = i7;
                    arrayMap4 = arrayMap4;
                    j = j;
                }
            }
            int i10 = i6;
            int i11 = i7;
            ArrayMap<String, ? extends Uid.Pkg> arrayMap5 = arrayMap4;
            long j8 = j;
            int i12 = 0;
            while (i12 < 7) {
                long[] cpuFreqTimes2 = uid.getCpuFreqTimes(0, i12);
                if (cpuFreqTimes2 == null || cpuFreqTimes2.length != cpuFreqs.length) {
                    j2 = jStart7;
                    jArr = cpuFreqs;
                    sparseArray = sparseArray8;
                } else {
                    long[] screenOffCpuFreqTimes2 = uid.getScreenOffCpuFreqTimes(0, i12);
                    if (screenOffCpuFreqTimes2 == null) {
                        screenOffCpuFreqTimes2 = new long[cpuFreqTimes2.length];
                    }
                    long jStart9 = protoOutputStream2.start(2246267895812L);
                    protoOutputStream2.write(1159641169921L, i12);
                    int i13 = 0;
                    while (i13 < cpuFreqTimes2.length) {
                        long jStart10 = protoOutputStream2.start(2246267895810L);
                        int i14 = i13 + 1;
                        protoOutputStream2.write(1120986464257L, i14);
                        protoOutputStream2.write(1112396529666L, cpuFreqTimes2[i13]);
                        protoOutputStream2.write(1112396529667L, screenOffCpuFreqTimes2[i13]);
                        protoOutputStream2.end(jStart10);
                        cpuFreqs = cpuFreqs;
                        i13 = i14;
                        sparseArray8 = sparseArray8;
                        jStart7 = jStart7;
                    }
                    j2 = jStart7;
                    jArr = cpuFreqs;
                    sparseArray = sparseArray8;
                    protoOutputStream2.end(jStart9);
                }
                i12++;
                cpuFreqs = jArr;
                sparseArray8 = sparseArray;
                jStart7 = j2;
            }
            SparseArray sparseArray9 = sparseArray8;
            protoOutputStream2.end(jStart7);
            dumpTimer(protoOutputStream, 1146756268042L, uid.getFlashlightTurnedOnTimer(), j6, 0);
            dumpTimer(protoOutputStream, 1146756268043L, uid.getForegroundActivityTimer(), j6, 0);
            dumpTimer(protoOutputStream, 1146756268044L, uid.getForegroundServiceTimer(), j6, 0);
            ArrayMap<String, SparseIntArray> jobCompletionStats = uid.getJobCompletionStats();
            int i15 = 5;
            int[] iArr = {0, 1, 2, 3, 4};
            int i16 = 0;
            while (i16 < jobCompletionStats.size()) {
                SparseIntArray sparseIntArrayValueAt = jobCompletionStats.valueAt(i16);
                if (sparseIntArrayValueAt != null) {
                    long jStart11 = protoOutputStream2.start(2246267895824L);
                    protoOutputStream2.write(1138166333441L, jobCompletionStats.keyAt(i16));
                    int i17 = 0;
                    while (i17 < i15) {
                        int i18 = iArr[i17];
                        long jStart12 = protoOutputStream2.start(2246267895810L);
                        protoOutputStream2.write(1159641169921L, i18);
                        protoOutputStream2.write(1120986464258L, sparseIntArrayValueAt.get(i18, 0));
                        protoOutputStream2.end(jStart12);
                        i17++;
                        i15 = 5;
                    }
                    protoOutputStream2.end(jStart11);
                }
                i16++;
                i15 = 5;
            }
            ArrayMap<String, ? extends Timer> jobStats = uid.getJobStats();
            for (int size4 = jobStats.size() - 1; size4 >= 0; size4--) {
                Timer timerValueAt = jobStats.valueAt(size4);
                Timer subTimer2 = timerValueAt.getSubTimer();
                long jStart13 = protoOutputStream2.start(2246267895823L);
                protoOutputStream2.write(1138166333441L, jobStats.keyAt(size4));
                dumpTimer(protoOutputStream, 1146756268034L, timerValueAt, j6, 0);
                dumpTimer(protoOutputStream, 1146756268035L, subTimer2, j6, 0);
                protoOutputStream2.end(jStart13);
            }
            dumpControllerActivityProto(protoOutputStream2, 1146756268036L, uid.getModemControllerActivity(), 0);
            long jStart14 = protoOutputStream2.start(1146756268049L);
            protoOutputStream2.write(1112396529665L, uid.getNetworkActivityBytes(0, 0));
            protoOutputStream2.write(1112396529666L, uid.getNetworkActivityBytes(1, 0));
            protoOutputStream2.write(1112396529667L, uid.getNetworkActivityBytes(2, 0));
            protoOutputStream2.write(1112396529668L, uid.getNetworkActivityBytes(3, 0));
            protoOutputStream2.write(1112396529669L, uid.getNetworkActivityBytes(4, 0));
            protoOutputStream2.write(1112396529670L, uid.getNetworkActivityBytes(5, 0));
            protoOutputStream2.write(1112396529671L, uid.getNetworkActivityPackets(0, 0));
            protoOutputStream2.write(1112396529672L, uid.getNetworkActivityPackets(1, 0));
            protoOutputStream2.write(1112396529673L, uid.getNetworkActivityPackets(2, 0));
            protoOutputStream2.write(1112396529674L, uid.getNetworkActivityPackets(3, 0));
            protoOutputStream2.write(1112396529675L, roundUsToMs(uid.getMobileRadioActiveTime(0)));
            protoOutputStream2.write(1120986464268L, uid.getMobileRadioActiveCount(0));
            protoOutputStream2.write(1120986464269L, uid.getMobileRadioApWakeupCount(0));
            protoOutputStream2.write(1120986464270L, uid.getWifiRadioApWakeupCount(0));
            protoOutputStream2.write(1112396529679L, uid.getNetworkActivityBytes(6, 0));
            protoOutputStream2.write(1112396529680L, uid.getNetworkActivityBytes(7, 0));
            protoOutputStream2.write(1112396529681L, uid.getNetworkActivityBytes(8, 0));
            protoOutputStream2.write(1112396529682L, uid.getNetworkActivityBytes(9, 0));
            protoOutputStream2.write(1112396529683L, uid.getNetworkActivityPackets(6, 0));
            protoOutputStream2.write(1112396529684L, uid.getNetworkActivityPackets(7, 0));
            protoOutputStream2.write(1112396529685L, uid.getNetworkActivityPackets(8, 0));
            protoOutputStream2.write(1112396529686L, uid.getNetworkActivityPackets(9, 0));
            protoOutputStream2.end(jStart14);
            SparseArray sparseArray10 = sparseArray9;
            BatterySipper batterySipper2 = (BatterySipper) sparseArray10.get(i11);
            if (batterySipper2 != null) {
                long jStart15 = protoOutputStream2.start(1146756268050L);
                protoOutputStream2.write(1103806595073L, batterySipper2.totalPowerMah);
                protoOutputStream2.write(1133871366146L, batterySipper2.shouldHide);
                protoOutputStream2.write(1103806595075L, batterySipper2.screenPowerMah);
                protoOutputStream2.write(1103806595076L, batterySipper2.proportionalSmearMah);
                protoOutputStream2.end(jStart15);
            }
            ArrayMap<String, ? extends Uid.Proc> processStats = uid.getProcessStats();
            for (int size5 = processStats.size() - 1; size5 >= 0; size5--) {
                Uid.Proc procValueAt = processStats.valueAt(size5);
                long jStart16 = protoOutputStream2.start(2246267895827L);
                protoOutputStream2.write(1138166333441L, processStats.keyAt(size5));
                protoOutputStream2.write(1112396529666L, procValueAt.getUserTime(0));
                protoOutputStream2.write(1112396529667L, procValueAt.getSystemTime(0));
                protoOutputStream2.write(1112396529668L, procValueAt.getForegroundTime(0));
                protoOutputStream2.write(1120986464261L, procValueAt.getStarts(0));
                protoOutputStream2.write(1120986464262L, procValueAt.getNumAnrs(0));
                protoOutputStream2.write(1120986464263L, procValueAt.getNumCrashes(0));
                protoOutputStream2.end(jStart16);
            }
            SparseArray<? extends Uid.Sensor> sensorStats = uid.getSensorStats();
            int i19 = 0;
            while (i19 < sensorStats.size()) {
                Uid.Sensor sensorValueAt = sensorStats.valueAt(i19);
                Timer sensorTime = sensorValueAt.getSensorTime();
                if (sensorTime != null) {
                    Timer sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                    int iKeyAt2 = sensorStats.keyAt(i19);
                    long jStart17 = protoOutputStream2.start(UidProto.SENSORS);
                    protoOutputStream2.write(1120986464257L, iKeyAt2);
                    dumpTimer(protoOutputStream, 1146756268034L, sensorTime, j6, 0);
                    dumpTimer(protoOutputStream, 1146756268035L, sensorBackgroundTime, j6, 0);
                    protoOutputStream2.end(jStart17);
                }
                i19++;
                uid = uid;
            }
            Uid uid2 = uid;
            int i20 = 0;
            while (i20 < 7) {
                Uid uid3 = uid2;
                long j9 = j6;
                long jRoundUsToMs2 = roundUsToMs(uid3.getProcessStateTime(i20, j9, 0));
                if (jRoundUsToMs2 != 0) {
                    long jStart18 = protoOutputStream2.start(2246267895828L);
                    protoOutputStream2.write(1159641169921L, i20);
                    protoOutputStream2.write(1112396529666L, jRoundUsToMs2);
                    protoOutputStream2.end(jStart18);
                }
                i20++;
                j6 = j9;
                uid2 = uid3;
            }
            Uid uid4 = uid2;
            long j10 = j6;
            ArrayMap<String, ? extends Timer> syncStats = uid4.getSyncStats();
            int size6 = syncStats.size() - 1;
            while (size6 >= 0) {
                Timer timerValueAt2 = syncStats.valueAt(size6);
                Timer subTimer3 = timerValueAt2.getSubTimer();
                long jStart19 = protoOutputStream2.start(2246267895830L);
                protoOutputStream2.write(1138166333441L, syncStats.keyAt(size6));
                dumpTimer(protoOutputStream, 1146756268034L, timerValueAt2, j10, 0);
                dumpTimer(protoOutputStream, 1146756268035L, subTimer3, j10, 0);
                protoOutputStream2.end(jStart19);
                size6--;
                uid4 = uid4;
            }
            Uid uid5 = uid4;
            if (uid5.hasUserActivity()) {
                int i21 = 0;
                while (i21 < 4) {
                    Uid uid6 = uid5;
                    int userActivityCount = uid6.getUserActivityCount(i21, 0);
                    if (userActivityCount != 0) {
                        long jStart20 = protoOutputStream2.start(2246267895831L);
                        protoOutputStream2.write(1159641169921L, i21);
                        protoOutputStream2.write(1120986464258L, userActivityCount);
                        protoOutputStream2.end(jStart20);
                    }
                    i21++;
                    uid5 = uid6;
                }
            }
            Uid uid7 = uid5;
            dumpTimer(protoOutputStream, 1146756268045L, uid7.getVibratorOnTimer(), j10, 0);
            dumpTimer(protoOutputStream, 1146756268046L, uid7.getVideoTurnedOnTimer(), j10, 0);
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uid7.getWakelockStats();
            int i22 = 1;
            int size7 = wakelockStats.size() - 1;
            while (size7 >= 0) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size7);
                long jStart21 = protoOutputStream2.start(2246267895833L);
                protoOutputStream2.write(1138166333441L, wakelockStats.keyAt(size7));
                SparseArray sparseArray11 = sparseArray10;
                dumpTimer(protoOutputStream, 1146756268034L, wakelockValueAt.getWakeTime(i22), j10, 0);
                Timer wakeTime = wakelockValueAt.getWakeTime(0);
                if (wakeTime != null) {
                    dumpTimer(protoOutputStream, 1146756268035L, wakeTime, j10, 0);
                    dumpTimer(protoOutputStream, 1146756268036L, wakeTime.getSubTimer(), j10, 0);
                }
                dumpTimer(protoOutputStream, 1146756268037L, wakelockValueAt.getWakeTime(2), j10, 0);
                protoOutputStream2.end(jStart21);
                size7--;
                sparseArray10 = sparseArray11;
                i22 = 1;
            }
            SparseArray sparseArray12 = sparseArray10;
            dumpTimer(protoOutputStream, 1146756268060L, uid7.getMulticastWakelockStats(), j10, 0);
            int i23 = 1;
            int size8 = arrayMap5.size() - 1;
            while (size8 >= 0) {
                ArrayMap<String, ? extends Uid.Pkg> arrayMap6 = arrayMap5;
                ArrayMap<String, ? extends Counter> wakeupAlarmStats = arrayMap6.valueAt(size8).getWakeupAlarmStats();
                int size9 = wakeupAlarmStats.size() - i23;
                while (size9 >= 0) {
                    long jStart22 = protoOutputStream2.start(2246267895834L);
                    protoOutputStream2.write(1138166333441L, wakeupAlarmStats.keyAt(size9));
                    protoOutputStream2.write(1120986464258L, wakeupAlarmStats.valueAt(size9).getCountLocked(0));
                    protoOutputStream2.end(jStart22);
                    size9--;
                    arrayMap6 = arrayMap6;
                }
                arrayMap5 = arrayMap6;
                size8--;
                i23 = 1;
            }
            dumpControllerActivityProto(protoOutputStream2, 1146756268037L, uid7.getWifiControllerActivity(), 0);
            long jStart23 = protoOutputStream2.start(1146756268059L);
            protoOutputStream2.write(1112396529665L, roundUsToMs(uid7.getFullWifiLockTime(j10, 0)));
            dumpTimer(protoOutputStream, 1146756268035L, uid7.getWifiScanTimer(), j10, 0);
            protoOutputStream2.write(1112396529666L, roundUsToMs(uid7.getWifiRunningTime(j10, 0)));
            dumpTimer(protoOutputStream, 1146756268036L, uid7.getWifiScanBackgroundTimer(), j10, 0);
            protoOutputStream2.end(jStart23);
            protoOutputStream2.end(jStart);
            i3 = i10 + 1;
            j4 = j10;
            size = i4;
            uidStats = sparseArray6;
            sparseArray4 = sparseArray12;
            jElapsedRealtime = j8;
            batteryUptime = j7;
            sparseArray3 = sparseArray7;
        }
    }

    private void dumpProtoHistoryLocked(ProtoOutputStream protoOutputStream, int i, long j) {
        if (startIteratingHistoryLocked()) {
            protoOutputStream.write(1120986464257L, 32);
            protoOutputStream.write(1112396529666L, getParcelVersion());
            protoOutputStream.write(1138166333443L, getStartPlatformVersion());
            protoOutputStream.write(1138166333444L, getEndPlatformVersion());
            for (int i2 = 0; i2 < getHistoryStringPoolSize(); i2++) {
                try {
                    long jStart = protoOutputStream.start(2246267895813L);
                    protoOutputStream.write(1120986464257L, i2);
                    protoOutputStream.write(1120986464258L, getHistoryTagPoolUid(i2));
                    protoOutputStream.write(1138166333443L, getHistoryTagPoolString(i2));
                    protoOutputStream.end(jStart);
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            HistoryPrinter historyPrinter = new HistoryPrinter();
            HistoryItem historyItem = new HistoryItem();
            long j2 = -1;
            long j3 = -1;
            boolean z = false;
            while (getNextHistoryLocked(historyItem)) {
                long j4 = historyItem.time;
                long j5 = j2 < 0 ? j4 : j2;
                if (historyItem.time >= j) {
                    if (j >= 0 && !z) {
                        if (historyItem.cmd == 5 || historyItem.cmd == 7 || historyItem.cmd == 4 || historyItem.cmd == 8) {
                            historyPrinter.printNextItem(protoOutputStream, historyItem, j5, (i & 32) != 0);
                            historyItem.cmd = (byte) 0;
                        } else if (historyItem.currentTime != 0) {
                            byte b = historyItem.cmd;
                            historyItem.cmd = (byte) 5;
                            historyPrinter.printNextItem(protoOutputStream, historyItem, j5, (i & 32) != 0);
                            historyItem.cmd = b;
                        }
                        z = true;
                    }
                    boolean z2 = z;
                    historyPrinter.printNextItem(protoOutputStream, historyItem, j5, (i & 32) != 0);
                    z = z2;
                }
                j3 = j4;
                j2 = j5;
            }
            if (j >= 0) {
                commitCurrentHistoryBatchLocked();
                protoOutputStream.write(2237677961222L, "NEXT: " + (j3 + 1));
            }
            finishIteratingHistoryLocked();
        }
    }

    private void dumpProtoSystemLocked(ProtoOutputStream protoOutputStream, BatteryStatsHelper batteryStatsHelper) {
        int i;
        int i2;
        int uid;
        long jStart = protoOutputStream.start(1146756268038L);
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long jStart2 = protoOutputStream.start(1146756268033L);
        protoOutputStream.write(1112396529665L, getStartClockTime());
        protoOutputStream.write(1112396529666L, getStartCount());
        int i3 = 0;
        protoOutputStream.write(1112396529667L, computeRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529668L, computeUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529669L, computeBatteryRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529670L, computeBatteryUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529671L, computeBatteryScreenOffRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529672L, computeBatteryScreenOffUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529673L, getScreenDozeTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529674L, getEstimatedBatteryCapacity());
        protoOutputStream.write(1112396529675L, getMinLearnedBatteryCapacity());
        protoOutputStream.write(1112396529676L, getMaxLearnedBatteryCapacity());
        protoOutputStream.end(jStart2);
        long jStart3 = protoOutputStream.start(1146756268034L);
        protoOutputStream.write(1120986464257L, getLowDischargeAmountSinceCharge());
        protoOutputStream.write(1120986464258L, getHighDischargeAmountSinceCharge());
        protoOutputStream.write(1120986464259L, getDischargeAmountScreenOnSinceCharge());
        protoOutputStream.write(1120986464260L, getDischargeAmountScreenOffSinceCharge());
        protoOutputStream.write(1120986464261L, getDischargeAmountScreenDozeSinceCharge());
        protoOutputStream.write(1112396529670L, getUahDischarge(0) / 1000);
        protoOutputStream.write(1112396529671L, getUahDischargeScreenOff(0) / 1000);
        protoOutputStream.write(1112396529672L, getUahDischargeScreenDoze(0) / 1000);
        protoOutputStream.write(1112396529673L, getUahDischargeLightDoze(0) / 1000);
        protoOutputStream.write(1112396529674L, getUahDischargeDeepDoze(0) / 1000);
        protoOutputStream.end(jStart3);
        long jComputeChargeTimeRemaining = computeChargeTimeRemaining(jElapsedRealtime);
        long totalTimeLocked = 0;
        if (jComputeChargeTimeRemaining >= 0) {
            protoOutputStream.write(1112396529667L, jComputeChargeTimeRemaining / 1000);
        } else {
            long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(jElapsedRealtime);
            if (jComputeBatteryTimeRemaining >= 0) {
                protoOutputStream.write(1112396529668L, jComputeBatteryTimeRemaining / 1000);
            } else {
                protoOutputStream.write(1112396529668L, -1);
            }
        }
        dumpDurationSteps(protoOutputStream, 2246267895813L, getChargeLevelStepTracker());
        int i4 = 0;
        while (true) {
            if (i4 >= 21) {
                break;
            }
            boolean z = i4 != 0 ? i3 : true;
            int i5 = i4 == 20 ? i3 : i4;
            long jStart4 = protoOutputStream.start(2246267895816L);
            if (z) {
                protoOutputStream.write(1133871366146L, z);
            } else {
                protoOutputStream.write(1159641169921L, i5);
            }
            dumpTimer(protoOutputStream, 1146756268035L, getPhoneDataConnectionTimer(i4), jElapsedRealtime, 0);
            protoOutputStream.end(jStart4);
            i4++;
            i3 = i3;
        }
        int i6 = i3;
        dumpDurationSteps(protoOutputStream, 2246267895814L, getDischargeLevelStepTracker());
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            int length = cpuFreqs.length;
            for (int i7 = i6; i7 < length; i7++) {
                protoOutputStream.write(SystemProto.CPU_FREQUENCY, cpuFreqs[i7]);
            }
        }
        dumpControllerActivityProto(protoOutputStream, 1146756268041L, getBluetoothControllerActivity(), i6);
        dumpControllerActivityProto(protoOutputStream, 1146756268042L, getModemControllerActivity(), i6);
        long jStart5 = protoOutputStream.start(1146756268044L);
        protoOutputStream.write(1112396529665L, getNetworkActivityBytes(i6, i6));
        protoOutputStream.write(1112396529666L, getNetworkActivityBytes(1, i6));
        protoOutputStream.write(1112396529669L, getNetworkActivityPackets(i6, i6));
        protoOutputStream.write(1112396529670L, getNetworkActivityPackets(1, i6));
        protoOutputStream.write(1112396529667L, getNetworkActivityBytes(2, i6));
        protoOutputStream.write(1112396529668L, getNetworkActivityBytes(3, i6));
        protoOutputStream.write(1112396529671L, getNetworkActivityPackets(2, i6));
        protoOutputStream.write(1112396529672L, getNetworkActivityPackets(3, i6));
        protoOutputStream.write(1112396529673L, getNetworkActivityBytes(4, i6));
        char c = 5;
        protoOutputStream.write(1112396529674L, getNetworkActivityBytes(5, i6));
        protoOutputStream.end(jStart5);
        dumpControllerActivityProto(protoOutputStream, 1146756268043L, getWifiControllerActivity(), i6);
        long jStart6 = protoOutputStream.start(1146756268045L);
        protoOutputStream.write(1112396529665L, getWifiOnTime(jElapsedRealtime, i6) / 1000);
        protoOutputStream.write(1112396529666L, getGlobalWifiRunningTime(jElapsedRealtime, i6) / 1000);
        protoOutputStream.end(jStart6);
        for (Map.Entry<String, ? extends Timer> entry : getKernelWakelockStats().entrySet()) {
            long jStart7 = protoOutputStream.start(2246267895822L);
            protoOutputStream.write(1138166333441L, entry.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, entry.getValue(), jElapsedRealtime, 0);
            protoOutputStream.end(jStart7);
            c = 5;
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        long totalTimeLocked2 = 0;
        for (int i8 = 0; i8 < uidStats.size(); i8++) {
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uidStats.valueAt(i8).getWakelockStats();
            for (int size = wakelockStats.size() - 1; size >= 0; size--) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size);
                Timer wakeTime = wakelockValueAt.getWakeTime(1);
                if (wakeTime != null) {
                    totalTimeLocked += wakeTime.getTotalTimeLocked(jElapsedRealtime, 0);
                }
                Timer wakeTime2 = wakelockValueAt.getWakeTime(0);
                if (wakeTime2 != null) {
                    totalTimeLocked2 += wakeTime2.getTotalTimeLocked(jElapsedRealtime, 0);
                }
            }
        }
        long jStart8 = protoOutputStream.start(1146756268047L);
        protoOutputStream.write(1112396529665L, getScreenOnTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529666L, getPhoneOnTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529667L, totalTimeLocked / 1000);
        protoOutputStream.write(1112396529668L, totalTimeLocked2 / 1000);
        protoOutputStream.write(1112396529669L, getMobileRadioActiveTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529670L, getMobileRadioActiveAdjustedTime(0) / 1000);
        protoOutputStream.write(1120986464263L, getMobileRadioActiveCount(0));
        protoOutputStream.write(1120986464264L, getMobileRadioActiveUnknownTime(0) / 1000);
        protoOutputStream.write(1112396529673L, getInteractiveTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529674L, getPowerSaveModeEnabledTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464267L, getNumConnectivityChange(0));
        int i9 = 2;
        protoOutputStream.write(1112396529676L, getDeviceIdleModeTime(2, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464269L, getDeviceIdleModeCount(2, 0));
        protoOutputStream.write(1112396529678L, getDeviceIdlingTime(2, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464271L, getDeviceIdlingCount(2, 0));
        protoOutputStream.write(1112396529680L, getLongestDeviceIdleModeTime(2));
        protoOutputStream.write(1112396529681L, getDeviceIdleModeTime(1, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464274L, getDeviceIdleModeCount(1, 0));
        protoOutputStream.write(1112396529683L, getDeviceIdlingTime(1, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464276L, getDeviceIdlingCount(1, 0));
        protoOutputStream.write(1112396529685L, getLongestDeviceIdleModeTime(1));
        protoOutputStream.end(jStart8);
        long wifiMulticastWakelockTime = getWifiMulticastWakelockTime(jElapsedRealtime, 0);
        int wifiMulticastWakelockCount = getWifiMulticastWakelockCount(0);
        long jStart9 = protoOutputStream.start(1146756268055L);
        protoOutputStream.write(1112396529665L, wifiMulticastWakelockTime / 1000);
        long j = 1120986464258L;
        protoOutputStream.write(1120986464258L, wifiMulticastWakelockCount);
        protoOutputStream.end(jStart9);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null) {
            int i10 = 0;
            while (i10 < usageList.size()) {
                BatterySipper batterySipper = usageList.get(i10);
                switch (AnonymousClass2.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[batterySipper.drainType.ordinal()]) {
                    case 1:
                        i = 0;
                        i2 = 13;
                        uid = i;
                        long jStart10 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart10);
                        break;
                    case 2:
                        uid = 0;
                        i2 = 1;
                        long jStart11 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart11);
                        break;
                    case 3:
                        i = 0;
                        i2 = i9;
                        uid = i;
                        long jStart12 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart12);
                        break;
                    case 4:
                        uid = 0;
                        i2 = 3;
                        long jStart13 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart13);
                        break;
                    case 5:
                        i = 0;
                        i2 = 4;
                        uid = i;
                        long jStart14 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart14);
                        break;
                    case 6:
                        uid = 0;
                        i2 = 5;
                        long jStart15 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart15);
                        break;
                    case 7:
                        i = 0;
                        i2 = 7;
                        uid = i;
                        long jStart16 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart16);
                        break;
                    case 8:
                        i = 0;
                        i2 = 6;
                        uid = i;
                        long jStart17 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart17);
                        break;
                    case 9:
                        break;
                    case 10:
                        i2 = 8;
                        uid = UserHandle.getUid(batterySipper.userId, 0);
                        long jStart18 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart18);
                        break;
                    case 11:
                        i2 = 9;
                        uid = 0;
                        long jStart19 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart19);
                        break;
                    case 12:
                        i2 = 10;
                        uid = 0;
                        long jStart110 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart110);
                        break;
                    case 13:
                        i2 = 11;
                        uid = 0;
                        long jStart111 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart111);
                        break;
                    case 14:
                        i2 = 12;
                        uid = 0;
                        long jStart112 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart112);
                        break;
                    default:
                        i2 = 0;
                        uid = 0;
                        long jStart113 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(j, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart113);
                        break;
                }
                i10++;
                usageList = usageList;
                j = 1120986464258L;
                i9 = 2;
            }
        }
        long jStart20 = protoOutputStream.start(1146756268050L);
        protoOutputStream.write(1103806595073L, batteryStatsHelper.getPowerProfile().getBatteryCapacity());
        protoOutputStream.write(SystemProto.PowerUseSummary.COMPUTED_POWER_MAH, batteryStatsHelper.getComputedPower());
        protoOutputStream.write(1103806595075L, batteryStatsHelper.getMinDrainedPower());
        protoOutputStream.write(1103806595076L, batteryStatsHelper.getMaxDrainedPower());
        protoOutputStream.end(jStart20);
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        Map<String, ? extends Timer> screenOffRpmStats = getScreenOffRpmStats();
        for (Iterator<Map.Entry<String, ? extends Timer>> it = rpmStats.entrySet().iterator(); it.hasNext(); it = it) {
            Map.Entry<String, ? extends Timer> next = it.next();
            long jStart21 = protoOutputStream.start(2246267895827L);
            protoOutputStream.write(1138166333441L, next.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, next.getValue(), jElapsedRealtime, 0);
            dumpTimer(protoOutputStream, 1146756268035L, screenOffRpmStats.get(next.getKey()), jElapsedRealtime, 0);
            protoOutputStream.end(jStart21);
        }
        for (int i11 = 0; i11 < 5; i11++) {
            long jStart22 = protoOutputStream.start(2246267895828L);
            protoOutputStream.write(1159641169921L, i11);
            dumpTimer(protoOutputStream, 1146756268034L, getScreenBrightnessTimer(i11), jElapsedRealtime, 0);
            protoOutputStream.end(jStart22);
        }
        dumpTimer(protoOutputStream, 1146756268053L, getPhoneSignalScanningTimer(), jElapsedRealtime, 0);
        for (int i12 = 0; i12 < 5; i12++) {
            long jStart23 = protoOutputStream.start(2246267895824L);
            protoOutputStream.write(1159641169921L, i12);
            dumpTimer(protoOutputStream, 1146756268034L, getPhoneSignalStrengthTimer(i12), jElapsedRealtime, 0);
            protoOutputStream.end(jStart23);
        }
        for (Map.Entry<String, ? extends Timer> entry2 : getWakeupReasonStats().entrySet()) {
            long jStart24 = protoOutputStream.start(2246267895830L);
            protoOutputStream.write(1138166333441L, entry2.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, entry2.getValue(), jElapsedRealtime, 0);
            protoOutputStream.end(jStart24);
        }
        for (int i13 = 0; i13 < 5; i13++) {
            long jStart25 = protoOutputStream.start(2246267895832L);
            protoOutputStream.write(1159641169921L, i13);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiSignalStrengthTimer(i13), jElapsedRealtime, 0);
            protoOutputStream.end(jStart25);
        }
        for (int i14 = 0; i14 < 8; i14++) {
            long jStart26 = protoOutputStream.start(2246267895833L);
            protoOutputStream.write(1159641169921L, i14);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiStateTimer(i14), jElapsedRealtime, 0);
            protoOutputStream.end(jStart26);
        }
        for (int i15 = 0; i15 < 13; i15++) {
            long jStart27 = protoOutputStream.start(2246267895834L);
            protoOutputStream.write(1159641169921L, i15);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiSupplStateTimer(i15), jElapsedRealtime, 0);
            protoOutputStream.end(jStart27);
        }
        protoOutputStream.end(jStart);
    }
}
