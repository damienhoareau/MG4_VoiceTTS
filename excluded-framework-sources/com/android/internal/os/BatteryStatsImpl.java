package com.android.internal.os;

import android.app.ActivityManager;
import android.app.slice.SliceProvider;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.UidTraffic;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkStats;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.BatteryStats;
import android.os.Build;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBatteryPropertiesRegistrar;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.os.connectivity.CellularBatteryStats;
import android.os.connectivity.GpsBatteryStats;
import android.os.connectivity.WifiBatteryStats;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.ParcelableCallAnalytics;
import android.telephony.ModemActivityInfo;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.IntArray;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.LogWriter;
import android.util.LongSparseArray;
import android.util.LongSparseLongArray;
import android.util.MutableInt;
import android.util.Pools;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.StatsLog;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.ims.ImsConfig;
import com.android.internal.logging.EventLogTags;
import com.android.internal.net.NetworkStatsFactory;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes3.dex */
public class BatteryStatsImpl extends BatteryStats {
    static final int BATTERY_DELTA_LEVEL_FLAG = 1;
    public static final int BATTERY_PLUGGED_NONE = 0;
    public static final Parcelable.Creator<BatteryStatsImpl> CREATOR;
    private static final boolean DEBUG = false;
    public static final boolean DEBUG_ENERGY = false;
    private static final boolean DEBUG_ENERGY_CPU = false;
    private static final boolean DEBUG_HISTORY = false;
    private static final boolean DEBUG_MEMORY = false;
    static final long DELAY_UPDATE_WAKELOCKS = 5000;
    static final int DELTA_BATTERY_CHARGE_FLAG = 16777216;
    static final int DELTA_BATTERY_LEVEL_FLAG = 524288;
    static final int DELTA_EVENT_FLAG = 8388608;
    static final int DELTA_STATE2_FLAG = 2097152;
    static final int DELTA_STATE_FLAG = 1048576;
    static final int DELTA_STATE_MASK = -33554432;
    static final int DELTA_TIME_ABS = 524285;
    static final int DELTA_TIME_INT = 524286;
    static final int DELTA_TIME_LONG = 524287;
    static final int DELTA_TIME_MASK = 524287;
    static final int DELTA_WAKELOCK_FLAG = 4194304;
    private static final int MAGIC = -1166707595;
    static final int MAX_DAILY_ITEMS = 10;
    static final int MAX_HISTORY_BUFFER;
    private static final int MAX_HISTORY_ITEMS;
    static final int MAX_LEVEL_STEPS = 200;
    static final int MAX_MAX_HISTORY_BUFFER;
    private static final int MAX_MAX_HISTORY_ITEMS;
    private static final int MAX_WAKELOCKS_PER_UID;
    static final int MSG_REPORT_CHARGING = 3;
    static final int MSG_REPORT_CPU_UPDATE_NEEDED = 1;
    static final int MSG_REPORT_POWER_CHANGE = 2;
    static final int MSG_REPORT_RESET_STATS = 4;
    private static final int NUM_BT_TX_LEVELS = 1;
    private static final int NUM_WIFI_TX_LEVELS = 1;
    private static final long RPM_STATS_UPDATE_FREQ_MS = 1000;
    static final int STATE_BATTERY_HEALTH_MASK = 7;
    static final int STATE_BATTERY_HEALTH_SHIFT = 26;
    static final int STATE_BATTERY_MASK = -16777216;
    static final int STATE_BATTERY_PLUG_MASK = 3;
    static final int STATE_BATTERY_PLUG_SHIFT = 24;
    static final int STATE_BATTERY_STATUS_MASK = 7;
    static final int STATE_BATTERY_STATUS_SHIFT = 29;
    private static final String TAG = "BatteryStatsImpl";
    private static final int USB_DATA_CONNECTED = 2;
    private static final int USB_DATA_DISCONNECTED = 1;
    private static final int USB_DATA_UNKNOWN = 0;
    private static final boolean USE_OLD_HISTORY = false;
    private static final int VERSION = 177;
    public static final int WAKE_LOCK_WEIGHT = 50;
    final BatteryStats.HistoryEventTracker mActiveEvents;
    int mActiveHistoryStates;
    int mActiveHistoryStates2;
    int mAudioOnNesting;
    StopwatchTimer mAudioOnTimer;
    final ArrayList<StopwatchTimer> mAudioTurnedOnTimers;
    ControllerActivityCounterImpl mBluetoothActivity;
    int mBluetoothScanNesting;
    final ArrayList<StopwatchTimer> mBluetoothScanOnTimers;
    protected StopwatchTimer mBluetoothScanTimer;
    private BatteryCallback mCallback;
    int mCameraOnNesting;
    StopwatchTimer mCameraOnTimer;
    final ArrayList<StopwatchTimer> mCameraTurnedOnTimers;
    int mChangedStates;
    int mChangedStates2;
    final BatteryStats.LevelStepTracker mChargeStepTracker;
    boolean mCharging;
    public final AtomicFile mCheckinFile;
    protected Clocks mClocks;
    private final Constants mConstants;
    private long[] mCpuFreqs;
    private long mCpuTimeReadsTrackingStartTime;
    final BatteryStats.HistoryStepDetails mCurHistoryStepDetails;
    long mCurStepCpuSystemTime;
    long mCurStepCpuUserTime;
    int mCurStepMode;
    long mCurStepStatIOWaitTime;
    long mCurStepStatIdleTime;
    long mCurStepStatIrqTime;
    long mCurStepStatSoftIrqTime;
    long mCurStepStatSystemTime;
    long mCurStepStatUserTime;
    int mCurrentBatteryLevel;
    final BatteryStats.LevelStepTracker mDailyChargeStepTracker;
    final BatteryStats.LevelStepTracker mDailyDischargeStepTracker;
    public final AtomicFile mDailyFile;
    final ArrayList<BatteryStats.DailyItem> mDailyItems;
    ArrayList<BatteryStats.PackageChange> mDailyPackageChanges;
    long mDailyStartTime;
    int mDeviceIdleMode;
    StopwatchTimer mDeviceIdleModeFullTimer;
    StopwatchTimer mDeviceIdleModeLightTimer;
    boolean mDeviceIdling;
    StopwatchTimer mDeviceIdlingTimer;
    boolean mDeviceLightIdling;
    StopwatchTimer mDeviceLightIdlingTimer;
    int mDischargeAmountScreenDoze;
    int mDischargeAmountScreenDozeSinceCharge;
    int mDischargeAmountScreenOff;
    int mDischargeAmountScreenOffSinceCharge;
    int mDischargeAmountScreenOn;
    int mDischargeAmountScreenOnSinceCharge;
    private LongSamplingCounter mDischargeCounter;
    int mDischargeCurrentLevel;
    private LongSamplingCounter mDischargeDeepDozeCounter;
    private LongSamplingCounter mDischargeLightDozeCounter;
    int mDischargePlugLevel;
    private LongSamplingCounter mDischargeScreenDozeCounter;
    int mDischargeScreenDozeUnplugLevel;
    private LongSamplingCounter mDischargeScreenOffCounter;
    int mDischargeScreenOffUnplugLevel;
    int mDischargeScreenOnUnplugLevel;
    int mDischargeStartLevel;
    final BatteryStats.LevelStepTracker mDischargeStepTracker;
    int mDischargeUnplugLevel;
    boolean mDistributeWakelockCpu;
    final ArrayList<StopwatchTimer> mDrawTimers;
    String mEndPlatformVersion;
    private int mEstimatedBatteryCapacity;
    private ExternalStatsSync mExternalSync;
    private final JournaledFile mFile;
    int mFlashlightOnNesting;
    StopwatchTimer mFlashlightOnTimer;
    final ArrayList<StopwatchTimer> mFlashlightTurnedOnTimers;
    final ArrayList<StopwatchTimer> mFullTimers;
    final ArrayList<StopwatchTimer> mFullWifiLockTimers;
    boolean mGlobalWifiRunning;
    StopwatchTimer mGlobalWifiRunningTimer;
    int mGpsNesting;
    int mGpsSignalQualityBin;
    protected final StopwatchTimer[] mGpsSignalQualityTimer;
    public Handler mHandler;
    boolean mHasBluetoothReporting;
    boolean mHasModemReporting;
    boolean mHasWifiReporting;
    protected boolean mHaveBatteryLevel;
    int mHighDischargeAmountSinceCharge;
    BatteryStats.HistoryItem mHistory;
    final BatteryStats.HistoryItem mHistoryAddTmp;
    long mHistoryBaseTime;
    final Parcel mHistoryBuffer;
    int mHistoryBufferLastPos;
    BatteryStats.HistoryItem mHistoryCache;
    final BatteryStats.HistoryItem mHistoryCur;
    BatteryStats.HistoryItem mHistoryEnd;
    private BatteryStats.HistoryItem mHistoryIterator;
    BatteryStats.HistoryItem mHistoryLastEnd;
    final BatteryStats.HistoryItem mHistoryLastLastWritten;
    final BatteryStats.HistoryItem mHistoryLastWritten;
    boolean mHistoryOverflow;
    final BatteryStats.HistoryItem mHistoryReadTmp;
    final HashMap<BatteryStats.HistoryTag, Integer> mHistoryTagPool;
    int mInitStepMode;
    private String mInitialAcquireWakeName;
    private int mInitialAcquireWakeUid;
    boolean mInteractive;
    StopwatchTimer mInteractiveTimer;
    boolean mIsCellularTxPowerHigh;
    final SparseIntArray mIsolatedUids;
    private boolean mIteratingHistory;
    protected KernelCpuSpeedReader[] mKernelCpuSpeedReaders;
    private final KernelMemoryBandwidthStats mKernelMemoryBandwidthStats;
    private final LongSparseArray<SamplingTimer> mKernelMemoryStats;
    protected KernelSingleUidTimeReader mKernelSingleUidTimeReader;
    protected KernelUidCpuActiveTimeReader mKernelUidCpuActiveTimeReader;
    protected KernelUidCpuClusterTimeReader mKernelUidCpuClusterTimeReader;
    protected KernelUidCpuFreqTimeReader mKernelUidCpuFreqTimeReader;
    protected KernelUidCpuTimeReader mKernelUidCpuTimeReader;
    private final KernelWakelockReader mKernelWakelockReader;
    private final HashMap<String, SamplingTimer> mKernelWakelockStats;
    private final BluetoothActivityInfoCache mLastBluetoothActivityInfo;
    int mLastChargeStepLevel;
    int mLastChargingStateLevel;
    int mLastDischargeStepLevel;
    long mLastHistoryElapsedRealtime;
    BatteryStats.HistoryStepDetails mLastHistoryStepDetails;
    byte mLastHistoryStepLevel;
    long mLastIdleTimeStart;
    private ModemActivityInfo mLastModemActivityInfo;
    private NetworkStats mLastModemNetworkStats;
    protected ArrayList<StopwatchTimer> mLastPartialTimers;
    private long mLastRpmStatsUpdateTimeMs;
    long mLastStepCpuSystemTime;
    long mLastStepCpuUserTime;
    long mLastStepStatIOWaitTime;
    long mLastStepStatIdleTime;
    long mLastStepStatIrqTime;
    long mLastStepStatSoftIrqTime;
    long mLastStepStatSystemTime;
    long mLastStepStatUserTime;
    String mLastWakeupReason;
    long mLastWakeupUptimeMs;
    private NetworkStats mLastWifiNetworkStats;
    long mLastWriteTime;
    private int mLoadedNumConnectivityChange;
    long mLongestFullIdleTime;
    long mLongestLightIdleTime;
    int mLowDischargeAmountSinceCharge;
    int mMaxChargeStepLevel;
    private int mMaxLearnedBatteryCapacity;
    int mMinDischargeStepLevel;
    private int mMinLearnedBatteryCapacity;
    LongSamplingCounter mMobileRadioActiveAdjustedTime;
    StopwatchTimer mMobileRadioActivePerAppTimer;
    long mMobileRadioActiveStartTime;
    StopwatchTimer mMobileRadioActiveTimer;
    LongSamplingCounter mMobileRadioActiveUnknownCount;
    LongSamplingCounter mMobileRadioActiveUnknownTime;
    int mMobileRadioPowerState;
    int mModStepMode;
    ControllerActivityCounterImpl mModemActivity;
    private String[] mModemIfaces;
    private final Object mModemNetworkLock;
    final LongSamplingCounter[] mNetworkByteActivityCounters;
    final LongSamplingCounter[] mNetworkPacketActivityCounters;
    private final NetworkStatsFactory mNetworkStatsFactory;
    private final Pools.Pool<NetworkStats> mNetworkStatsPool;
    int mNextHistoryTagIdx;
    long mNextMaxDailyDeadline;
    long mNextMinDailyDeadline;
    boolean mNoAutoReset;
    private int mNumAllUidCpuTimeReads;
    private long mNumBatchedSingleUidCpuTimeReads;
    private int mNumConnectivityChange;
    int mNumHistoryItems;
    int mNumHistoryTagChars;
    private long mNumSingleUidCpuTimeReads;
    private int mNumUidsRemoved;
    boolean mOnBattery;
    protected boolean mOnBatteryInternal;
    protected final TimeBase mOnBatteryScreenOffTimeBase;
    protected final TimeBase mOnBatteryTimeBase;
    protected ArrayList<StopwatchTimer> mPartialTimers;
    protected Queue<UidToRemove> mPendingRemovedUids;
    protected final SparseIntArray mPendingUids;
    Parcel mPendingWrite;
    public boolean mPerProcStateCpuTimesAvailable;
    int mPhoneDataConnectionType;
    final StopwatchTimer[] mPhoneDataConnectionsTimer;
    boolean mPhoneOn;
    StopwatchTimer mPhoneOnTimer;
    private int mPhoneServiceState;
    private int mPhoneServiceStateRaw;
    StopwatchTimer mPhoneSignalScanningTimer;
    int mPhoneSignalStrengthBin;
    int mPhoneSignalStrengthBinRaw;
    final StopwatchTimer[] mPhoneSignalStrengthsTimer;
    private int mPhoneSimStateRaw;
    private final PlatformIdleStateCallback mPlatformIdleStateCallback;
    protected PowerProfile mPowerProfile;
    boolean mPowerSaveModeEnabled;
    StopwatchTimer mPowerSaveModeEnabledTimer;
    boolean mPretendScreenOff;
    int mReadHistoryChars;
    final BatteryStats.HistoryStepDetails mReadHistoryStepDetails;
    String[] mReadHistoryStrings;
    int[] mReadHistoryUids;
    private boolean mReadOverflow;
    long mRealtime;
    long mRealtimeStart;
    public boolean mRecordAllHistory;
    protected boolean mRecordingHistory;
    private final HashMap<String, SamplingTimer> mRpmStats;
    int mScreenBrightnessBin;
    final StopwatchTimer[] mScreenBrightnessTimer;
    protected StopwatchTimer mScreenDozeTimer;
    private final HashMap<String, SamplingTimer> mScreenOffRpmStats;
    protected StopwatchTimer mScreenOnTimer;
    protected int mScreenState;
    int mSensorNesting;
    final SparseArray<ArrayList<StopwatchTimer>> mSensorTimers;
    boolean mShuttingDown;
    long mStartClockTime;
    int mStartCount;
    String mStartPlatformVersion;
    long mTempTotalCpuSystemTimeUs;
    long mTempTotalCpuUserTimeUs;
    final BatteryStats.HistoryStepDetails mTmpHistoryStepDetails;
    private final RpmStats mTmpRpmStats;
    private final KernelWakelockStats mTmpWakelockStats;
    long mTrackRunningHistoryElapsedRealtime;
    long mTrackRunningHistoryUptime;
    final SparseArray<Uid> mUidStats;
    private int mUnpluggedNumConnectivityChange;
    long mUptime;
    long mUptimeStart;
    int mUsbDataState;
    protected UserInfoProvider mUserInfoProvider;
    int mVideoOnNesting;
    StopwatchTimer mVideoOnTimer;
    final ArrayList<StopwatchTimer> mVideoTurnedOnTimers;
    long[][] mWakeLockAllocationsUs;
    boolean mWakeLockImportant;
    int mWakeLockNesting;
    private final HashMap<String, SamplingTimer> mWakeupReasonStats;
    StopwatchTimer mWifiActiveTimer;
    ControllerActivityCounterImpl mWifiActivity;
    final SparseArray<ArrayList<StopwatchTimer>> mWifiBatchedScanTimers;
    int mWifiFullLockNesting;
    private String[] mWifiIfaces;
    int mWifiMulticastNesting;
    final ArrayList<StopwatchTimer> mWifiMulticastTimers;
    StopwatchTimer mWifiMulticastWakelockTimer;
    private final Object mWifiNetworkLock;
    boolean mWifiOn;
    StopwatchTimer mWifiOnTimer;
    int mWifiRadioPowerState;
    final ArrayList<StopwatchTimer> mWifiRunningTimers;
    int mWifiScanNesting;
    final ArrayList<StopwatchTimer> mWifiScanTimers;
    int mWifiSignalStrengthBin;
    final StopwatchTimer[] mWifiSignalStrengthsTimer;
    int mWifiState;
    final StopwatchTimer[] mWifiStateTimer;
    int mWifiSupplState;
    final StopwatchTimer[] mWifiSupplStateTimer;
    final ArrayList<StopwatchTimer> mWindowTimers;
    final ReentrantLock mWriteLock;

    public interface BatteryCallback {
        void batteryNeedsCpuUpdate();

        void batteryPowerChanged(boolean z);

        void batterySendBroadcast(Intent intent);

        void batteryStatsReset();
    }

    public interface Clocks {
        long elapsedRealtime();

        long uptimeMillis();
    }

    public interface ExternalStatsSync {
        public static final int UPDATE_ALL = 31;
        public static final int UPDATE_BT = 8;
        public static final int UPDATE_CPU = 1;
        public static final int UPDATE_RADIO = 4;
        public static final int UPDATE_RPM = 16;
        public static final int UPDATE_WIFI = 2;

        void cancelCpuSyncDueToWakelockChange();

        Future<?> scheduleCopyFromAllUidsCpuTimes(boolean z, boolean z2);

        Future<?> scheduleCpuSyncDueToRemovedUid(int i);

        Future<?> scheduleCpuSyncDueToScreenStateChange(boolean z, boolean z2);

        Future<?> scheduleCpuSyncDueToSettingChange();

        Future<?> scheduleCpuSyncDueToWakelockChange(long j);

        Future<?> scheduleReadProcStateCpuTimes(boolean z, boolean z2, long j);

        Future<?> scheduleSync(String str, int i);

        Future<?> scheduleSyncDueToBatteryLevelChange(long j);
    }

    public interface PlatformIdleStateCallback {
        void fillLowPowerStats(RpmStats rpmStats);

        String getPlatformLowPowerStats();

        String getSubsystemLowPowerStats();
    }

    public interface TimeBaseObs {
        void onTimeStarted(long j, long j2, long j3);

        void onTimeStopped(long j, long j2, long j3);
    }

    public static boolean isOnBattery(int i, int i2) {
        return i == 0 && i2 != 1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.BatteryStats
    public int getParcelVersion() {
        return 177;
    }

    public boolean isScreenDoze(int i) {
        return i == 3 || i == 4;
    }

    public boolean isScreenOff(int i) {
        return i == 1;
    }

    public boolean isScreenOn(int i) {
        return i == 2 || i == 5 || i == 6;
    }

    void readOldHistory(Parcel parcel) {
    }

    void writeOldHistory(Parcel parcel) {
    }

    static /* synthetic */ int access$108(BatteryStatsImpl batteryStatsImpl) {
        int i = batteryStatsImpl.mNumUidsRemoved;
        batteryStatsImpl.mNumUidsRemoved = i + 1;
        return i;
    }

    static /* synthetic */ long access$1408(BatteryStatsImpl batteryStatsImpl) {
        long j = batteryStatsImpl.mNumSingleUidCpuTimeReads;
        batteryStatsImpl.mNumSingleUidCpuTimeReads = 1 + j;
        return j;
    }

    static /* synthetic */ long access$1508(BatteryStatsImpl batteryStatsImpl) {
        long j = batteryStatsImpl.mNumBatchedSingleUidCpuTimeReads;
        batteryStatsImpl.mNumBatchedSingleUidCpuTimeReads = 1 + j;
        return j;
    }

    static {
        if (ActivityManager.isLowRamDeviceStatic()) {
            MAX_HISTORY_ITEMS = 800;
            MAX_MAX_HISTORY_ITEMS = 1200;
            MAX_WAKELOCKS_PER_UID = 40;
            MAX_HISTORY_BUFFER = 98304;
            MAX_MAX_HISTORY_BUFFER = 131072;
        } else {
            MAX_HISTORY_ITEMS = 4000;
            MAX_MAX_HISTORY_ITEMS = BluetoothHealth.HEALTH_OPERATION_SUCCESS;
            MAX_WAKELOCKS_PER_UID = 200;
            MAX_HISTORY_BUFFER = 524288;
            MAX_MAX_HISTORY_BUFFER = 655360;
        }
        CREATOR = new Parcelable.Creator<BatteryStatsImpl>() { // from class: com.android.internal.os.BatteryStatsImpl.5
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BatteryStatsImpl createFromParcel(Parcel parcel) {
                return new BatteryStatsImpl(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public BatteryStatsImpl[] newArray(int i) {
                return new BatteryStatsImpl[i];
            }
        };
    }

    @Override // android.os.BatteryStats
    public LongSparseArray<SamplingTimer> getKernelMemoryStats() {
        return this.mKernelMemoryStats;
    }

    public final class UidToRemove {
        int endUid;
        int startUid;
        long timeAddedInQueue;

        public UidToRemove(BatteryStatsImpl batteryStatsImpl, int i, long j) {
            this(i, i, j);
        }

        public UidToRemove(int i, int i2, long j) {
            this.startUid = i;
            this.endUid = i2;
            this.timeAddedInQueue = j;
        }

        void remove() {
            int i = this.startUid;
            int i2 = this.endUid;
            if (i == i2) {
                BatteryStatsImpl.this.mKernelUidCpuTimeReader.removeUid(this.startUid);
                BatteryStatsImpl.this.mKernelUidCpuFreqTimeReader.removeUid(this.startUid);
                if (BatteryStatsImpl.this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                    BatteryStatsImpl.this.mKernelUidCpuActiveTimeReader.removeUid(this.startUid);
                    BatteryStatsImpl.this.mKernelUidCpuClusterTimeReader.removeUid(this.startUid);
                }
                if (BatteryStatsImpl.this.mKernelSingleUidTimeReader != null) {
                    BatteryStatsImpl.this.mKernelSingleUidTimeReader.removeUid(this.startUid);
                }
                BatteryStatsImpl.access$108(BatteryStatsImpl.this);
                return;
            }
            if (i < i2) {
                BatteryStatsImpl.this.mKernelUidCpuFreqTimeReader.removeUidsInRange(this.startUid, this.endUid);
                BatteryStatsImpl.this.mKernelUidCpuTimeReader.removeUidsInRange(this.startUid, this.endUid);
                if (BatteryStatsImpl.this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                    BatteryStatsImpl.this.mKernelUidCpuActiveTimeReader.removeUidsInRange(this.startUid, this.endUid);
                    BatteryStatsImpl.this.mKernelUidCpuClusterTimeReader.removeUidsInRange(this.startUid, this.endUid);
                }
                if (BatteryStatsImpl.this.mKernelSingleUidTimeReader != null) {
                    BatteryStatsImpl.this.mKernelSingleUidTimeReader.removeUidsInRange(this.startUid, this.endUid);
                }
                BatteryStatsImpl.access$108(BatteryStatsImpl.this);
                return;
            }
            Slog.w(BatteryStatsImpl.TAG, "End UID " + this.endUid + " is smaller than start UID " + this.startUid);
        }
    }

    public static abstract class UserInfoProvider {
        private int[] userIds;

        protected abstract int[] getUserIds();

        public final void refreshUserIds() {
            this.userIds = getUserIds();
        }

        public boolean exists(int i) {
            int[] iArr = this.userIds;
            if (iArr != null) {
                return ArrayUtils.contains(iArr, i);
            }
            return true;
        }
    }

    final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            String str;
            BatteryCallback batteryCallback = BatteryStatsImpl.this.mCallback;
            int i = message.what;
            if (i == 1) {
                if (batteryCallback != null) {
                    batteryCallback.batteryNeedsCpuUpdate();
                    return;
                }
                return;
            }
            if (i == 2) {
                if (batteryCallback != null) {
                    batteryCallback.batteryPowerChanged(message.arg1 != 0);
                }
            } else {
                if (i != 3) {
                    if (i == 4 && batteryCallback != null) {
                        batteryCallback.batteryStatsReset();
                        return;
                    }
                    return;
                }
                if (batteryCallback != null) {
                    synchronized (BatteryStatsImpl.this) {
                        str = BatteryStatsImpl.this.mCharging ? BatteryManager.ACTION_CHARGING : BatteryManager.ACTION_DISCHARGING;
                    }
                    Intent intent = new Intent(str);
                    intent.addFlags(67108864);
                    batteryCallback.batterySendBroadcast(intent);
                }
            }
        }
    }

    public void postBatteryNeedsCpuUpdateMsg() {
        this.mHandler.sendEmptyMessage(1);
    }

    public void updateProcStateCpuTimes(boolean z, boolean z2) {
        int[] array;
        synchronized (this) {
            if (this.mConstants.TRACK_CPU_TIMES_BY_PROC_STATE) {
                if (initKernelSingleUidTimeReaderLocked()) {
                    if (this.mKernelSingleUidTimeReader.hasStaleData()) {
                        this.mPendingUids.clear();
                        return;
                    }
                    if (this.mPendingUids.size() == 0) {
                        return;
                    }
                    SparseIntArray sparseIntArrayM34clone = this.mPendingUids.m34clone();
                    this.mPendingUids.clear();
                    for (int size = sparseIntArrayM34clone.size() - 1; size >= 0; size--) {
                        int iKeyAt = sparseIntArrayM34clone.keyAt(size);
                        int iValueAt = sparseIntArrayM34clone.valueAt(size);
                        synchronized (this) {
                            Uid availableUidStatsLocked = getAvailableUidStatsLocked(iKeyAt);
                            if (availableUidStatsLocked != null) {
                                if (availableUidStatsLocked.mChildUids == null) {
                                    array = null;
                                } else {
                                    array = availableUidStatsLocked.mChildUids.toArray();
                                    for (int length = array.length - 1; length >= 0; length--) {
                                        array[length] = availableUidStatsLocked.mChildUids.get(length);
                                    }
                                }
                                long[] deltaMs = this.mKernelSingleUidTimeReader.readDeltaMs(iKeyAt);
                                if (array != null) {
                                    for (int length2 = array.length - 1; length2 >= 0; length2--) {
                                        deltaMs = addCpuTimes(deltaMs, this.mKernelSingleUidTimeReader.readDeltaMs(array[length2]));
                                    }
                                }
                                if (z && deltaMs != null) {
                                    synchronized (this) {
                                        availableUidStatsLocked.addProcStateTimesMs(iValueAt, deltaMs, z);
                                        availableUidStatsLocked.addProcStateScreenOffTimesMs(iValueAt, deltaMs, z2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void clearPendingRemovedUids() {
        long jElapsedRealtime = this.mClocks.elapsedRealtime() - this.mConstants.UID_REMOVE_DELAY_MS;
        while (!this.mPendingRemovedUids.isEmpty() && this.mPendingRemovedUids.peek().timeAddedInQueue < jElapsedRealtime) {
            this.mPendingRemovedUids.poll().remove();
        }
    }

    public void copyFromAllUidsCpuTimes() {
        synchronized (this) {
            copyFromAllUidsCpuTimes(this.mOnBatteryTimeBase.isRunning(), this.mOnBatteryScreenOffTimeBase.isRunning());
        }
    }

    public void copyFromAllUidsCpuTimes(boolean z, boolean z2) {
        long[] jArrValueAt;
        int iValueAt;
        synchronized (this) {
            if (this.mConstants.TRACK_CPU_TIMES_BY_PROC_STATE) {
                if (initKernelSingleUidTimeReaderLocked()) {
                    SparseArray<long[]> allUidCpuFreqTimeMs = this.mKernelUidCpuFreqTimeReader.getAllUidCpuFreqTimeMs();
                    if (this.mKernelSingleUidTimeReader.hasStaleData()) {
                        this.mKernelSingleUidTimeReader.setAllUidsCpuTimesMs(allUidCpuFreqTimeMs);
                        this.mKernelSingleUidTimeReader.markDataAsStale(false);
                        this.mPendingUids.clear();
                        return;
                    }
                    for (int size = allUidCpuFreqTimeMs.size() - 1; size >= 0; size--) {
                        int iKeyAt = allUidCpuFreqTimeMs.keyAt(size);
                        Uid availableUidStatsLocked = getAvailableUidStatsLocked(mapUid(iKeyAt));
                        if (availableUidStatsLocked != null && (jArrValueAt = allUidCpuFreqTimeMs.valueAt(size)) != null) {
                            long[] jArrComputeDelta = this.mKernelSingleUidTimeReader.computeDelta(iKeyAt, (long[]) jArrValueAt.clone());
                            if (z && jArrComputeDelta != null) {
                                int iIndexOfKey = this.mPendingUids.indexOfKey(iKeyAt);
                                if (iIndexOfKey >= 0) {
                                    iValueAt = this.mPendingUids.valueAt(iIndexOfKey);
                                    this.mPendingUids.removeAt(iIndexOfKey);
                                } else {
                                    iValueAt = availableUidStatsLocked.mProcessState;
                                }
                                if (iValueAt >= 0 && iValueAt < 7) {
                                    availableUidStatsLocked.addProcStateTimesMs(iValueAt, jArrComputeDelta, z);
                                    availableUidStatsLocked.addProcStateScreenOffTimesMs(iValueAt, jArrComputeDelta, z2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public long[] addCpuTimes(long[] jArr, long[] jArr2) {
        if (jArr == null || jArr2 == null) {
            if (jArr != null) {
                return jArr;
            }
            if (jArr2 == null) {
                return null;
            }
            return jArr2;
        }
        for (int length = jArr.length - 1; length >= 0; length--) {
            jArr[length] = jArr[length] + jArr2[length];
        }
        return jArr;
    }

    private boolean initKernelSingleUidTimeReaderLocked() {
        boolean z = false;
        if (this.mKernelSingleUidTimeReader == null) {
            PowerProfile powerProfile = this.mPowerProfile;
            if (powerProfile == null) {
                return false;
            }
            if (this.mCpuFreqs == null) {
                this.mCpuFreqs = this.mKernelUidCpuFreqTimeReader.readFreqs(powerProfile);
            }
            if (this.mCpuFreqs != null) {
                this.mKernelSingleUidTimeReader = new KernelSingleUidTimeReader(this.mCpuFreqs.length);
            } else {
                this.mPerProcStateCpuTimesAvailable = this.mKernelUidCpuFreqTimeReader.allUidTimesAvailable();
                return false;
            }
        }
        if (this.mKernelUidCpuFreqTimeReader.allUidTimesAvailable() && this.mKernelSingleUidTimeReader.singleUidCpuTimesAvailable()) {
            z = true;
        }
        this.mPerProcStateCpuTimesAvailable = z;
        return true;
    }

    public static class SystemClocks implements Clocks {
        @Override // com.android.internal.os.BatteryStatsImpl.Clocks
        public long elapsedRealtime() {
            return SystemClock.elapsedRealtime();
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Clocks
        public long uptimeMillis() {
            return SystemClock.uptimeMillis();
        }
    }

    @Override // android.os.BatteryStats
    public Map<String, ? extends Timer> getRpmStats() {
        return this.mRpmStats;
    }

    @Override // android.os.BatteryStats
    public Map<String, ? extends Timer> getScreenOffRpmStats() {
        return this.mScreenOffRpmStats;
    }

    @Override // android.os.BatteryStats
    public Map<String, ? extends Timer> getKernelWakelockStats() {
        return this.mKernelWakelockStats;
    }

    @Override // android.os.BatteryStats
    public Map<String, ? extends Timer> getWakeupReasonStats() {
        return this.mWakeupReasonStats;
    }

    @Override // android.os.BatteryStats
    public long getUahDischarge(int i) {
        return this.mDischargeCounter.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getUahDischargeScreenOff(int i) {
        return this.mDischargeScreenOffCounter.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getUahDischargeScreenDoze(int i) {
        return this.mDischargeScreenDozeCounter.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getUahDischargeLightDoze(int i) {
        return this.mDischargeLightDozeCounter.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getUahDischargeDeepDoze(int i) {
        return this.mDischargeDeepDozeCounter.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public int getEstimatedBatteryCapacity() {
        return this.mEstimatedBatteryCapacity;
    }

    @Override // android.os.BatteryStats
    public int getMinLearnedBatteryCapacity() {
        return this.mMinLearnedBatteryCapacity;
    }

    @Override // android.os.BatteryStats
    public int getMaxLearnedBatteryCapacity() {
        return this.mMaxLearnedBatteryCapacity;
    }

    public BatteryStatsImpl() {
        this(new SystemClocks());
    }

    public BatteryStatsImpl(Clocks clocks) {
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mKernelUidCpuTimeReader = new KernelUidCpuTimeReader();
        this.mKernelUidCpuFreqTimeReader = new KernelUidCpuFreqTimeReader();
        this.mKernelUidCpuActiveTimeReader = new KernelUidCpuActiveTimeReader();
        this.mKernelUidCpuClusterTimeReader = new KernelUidCpuClusterTimeReader();
        this.mKernelMemoryBandwidthStats = new KernelMemoryBandwidthStats();
        this.mKernelMemoryStats = new LongSparseArray<>();
        this.mPerProcStateCpuTimesAvailable = true;
        this.mPendingUids = new SparseIntArray();
        this.mCpuTimeReadsTrackingStartTime = SystemClock.uptimeMillis();
        this.mTmpRpmStats = new RpmStats();
        this.mLastRpmStatsUpdateTimeMs = -1000L;
        this.mPendingRemovedUids = new LinkedList();
        this.mExternalSync = null;
        this.mUserInfoProvider = null;
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mDrawTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mWifiBatchedScanTimers = new SparseArray<>();
        this.mAudioTurnedOnTimers = new ArrayList<>();
        this.mVideoTurnedOnTimers = new ArrayList<>();
        this.mFlashlightTurnedOnTimers = new ArrayList<>();
        this.mCameraTurnedOnTimers = new ArrayList<>();
        this.mBluetoothScanOnTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new BatteryStats.HistoryEventTracker();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = false;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryAddTmp = new BatteryStats.HistoryItem();
        this.mHistoryTagPool = new HashMap<>();
        this.mNextHistoryTagIdx = 0;
        this.mNumHistoryTagChars = 0;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mActiveHistoryStates = -1;
        this.mActiveHistoryStates2 = -1;
        this.mLastHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryUptime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mLastHistoryStepDetails = null;
        this.mLastHistoryStepLevel = (byte) 0;
        this.mCurHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mReadHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mTmpHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mScreenState = 0;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mUsbDataState = 0;
        this.mGpsSignalQualityBin = -1;
        this.mGpsSignalQualityTimer = new StopwatchTimer[2];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[21];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
        this.mHasWifiReporting = false;
        this.mHasBluetoothReporting = false;
        this.mHasModemReporting = false;
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mIsCellularTxPowerHigh = false;
        this.mMobileRadioPowerState = 1;
        this.mWifiRadioPowerState = 1;
        this.mCharging = true;
        this.mInitStepMode = 0;
        this.mCurStepMode = 0;
        this.mModStepMode = 0;
        this.mDischargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyDischargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mChargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyChargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mDailyStartTime = 0L;
        this.mNextMinDailyDeadline = 0L;
        this.mNextMaxDailyDeadline = 0L;
        this.mDailyItems = new ArrayList<>();
        this.mLastWriteTime = 0L;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mEstimatedBatteryCapacity = -1;
        this.mMinLearnedBatteryCapacity = -1;
        this.mMaxLearnedBatteryCapacity = -1;
        this.mRpmStats = new HashMap<>();
        this.mScreenOffRpmStats = new HashMap<>();
        this.mKernelWakelockStats = new HashMap<>();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0L;
        this.mWakeupReasonStats = new HashMap<>();
        this.mChangedStates = 0;
        this.mChangedStates2 = 0;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mNetworkStatsPool = new Pools.SynchronizedPool(6);
        this.mWifiNetworkLock = new Object();
        this.mWifiIfaces = EmptyArray.STRING;
        this.mLastWifiNetworkStats = new NetworkStats(0L, -1);
        this.mModemNetworkLock = new Object();
        this.mModemIfaces = EmptyArray.STRING;
        this.mLastModemNetworkStats = new NetworkStats(0L, -1);
        this.mLastModemActivityInfo = new ModemActivityInfo(0L, 0, 0, new int[0], 0, 0);
        this.mLastBluetoothActivityInfo = new BluetoothActivityInfoCache();
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        init(clocks);
        this.mFile = null;
        this.mCheckinFile = null;
        this.mDailyFile = null;
        this.mHandler = null;
        this.mPlatformIdleStateCallback = null;
        this.mUserInfoProvider = null;
        this.mConstants = new Constants(this.mHandler);
        clearHistoryLocked();
    }

    private void init(Clocks clocks) {
        this.mClocks = clocks;
    }

    public static class TimeBase {
        protected final ArrayList<TimeBaseObs> mObservers = new ArrayList<>();
        protected long mPastRealtime;
        protected long mPastUptime;
        protected long mRealtime;
        protected long mRealtimeStart;
        protected boolean mRunning;
        protected long mUnpluggedRealtime;
        protected long mUnpluggedUptime;
        protected long mUptime;
        protected long mUptimeStart;

        public void dump(PrintWriter printWriter, String str) {
            StringBuilder sb = new StringBuilder(128);
            printWriter.print(str);
            printWriter.print("mRunning=");
            printWriter.println(this.mRunning);
            sb.setLength(0);
            sb.append(str);
            sb.append("mUptime=");
            BatteryStats.formatTimeMs(sb, this.mUptime / 1000);
            printWriter.println(sb.toString());
            sb.setLength(0);
            sb.append(str);
            sb.append("mRealtime=");
            BatteryStats.formatTimeMs(sb, this.mRealtime / 1000);
            printWriter.println(sb.toString());
            sb.setLength(0);
            sb.append(str);
            sb.append("mPastUptime=");
            BatteryStats.formatTimeMs(sb, this.mPastUptime / 1000);
            sb.append("mUptimeStart=");
            BatteryStats.formatTimeMs(sb, this.mUptimeStart / 1000);
            sb.append("mUnpluggedUptime=");
            BatteryStats.formatTimeMs(sb, this.mUnpluggedUptime / 1000);
            printWriter.println(sb.toString());
            sb.setLength(0);
            sb.append(str);
            sb.append("mPastRealtime=");
            BatteryStats.formatTimeMs(sb, this.mPastRealtime / 1000);
            sb.append("mRealtimeStart=");
            BatteryStats.formatTimeMs(sb, this.mRealtimeStart / 1000);
            sb.append("mUnpluggedRealtime=");
            BatteryStats.formatTimeMs(sb, this.mUnpluggedRealtime / 1000);
            printWriter.println(sb.toString());
        }

        public void add(TimeBaseObs timeBaseObs) {
            this.mObservers.add(timeBaseObs);
        }

        public void remove(TimeBaseObs timeBaseObs) {
            if (this.mObservers.remove(timeBaseObs)) {
                return;
            }
            Slog.wtf(BatteryStatsImpl.TAG, "Removed unknown observer: " + timeBaseObs);
        }

        public boolean hasObserver(TimeBaseObs timeBaseObs) {
            return this.mObservers.contains(timeBaseObs);
        }

        public void init(long j, long j2) {
            this.mRealtime = 0L;
            this.mUptime = 0L;
            this.mPastUptime = 0L;
            this.mPastRealtime = 0L;
            this.mUptimeStart = j;
            this.mRealtimeStart = j2;
            this.mUnpluggedUptime = getUptime(j);
            this.mUnpluggedRealtime = getRealtime(this.mRealtimeStart);
        }

        public void reset(long j, long j2) {
            if (!this.mRunning) {
                this.mPastUptime = 0L;
                this.mPastRealtime = 0L;
            } else {
                this.mUptimeStart = j;
                this.mRealtimeStart = j2;
                this.mUnpluggedUptime = getUptime(j);
                this.mUnpluggedRealtime = getRealtime(j2);
            }
        }

        public long computeUptime(long j, int i) {
            if (i == 0) {
                return this.mUptime + getUptime(j);
            }
            if (i == 1) {
                return getUptime(j);
            }
            if (i != 2) {
                return 0L;
            }
            return getUptime(j) - this.mUnpluggedUptime;
        }

        public long computeRealtime(long j, int i) {
            if (i == 0) {
                return this.mRealtime + getRealtime(j);
            }
            if (i == 1) {
                return getRealtime(j);
            }
            if (i != 2) {
                return 0L;
            }
            return getRealtime(j) - this.mUnpluggedRealtime;
        }

        public long getUptime(long j) {
            long j2 = this.mPastUptime;
            return this.mRunning ? j2 + (j - this.mUptimeStart) : j2;
        }

        public long getRealtime(long j) {
            long j2 = this.mPastRealtime;
            return this.mRunning ? j2 + (j - this.mRealtimeStart) : j2;
        }

        public long getUptimeStart() {
            return this.mUptimeStart;
        }

        public long getRealtimeStart() {
            return this.mRealtimeStart;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public boolean setRunning(boolean z, long j, long j2) {
            if (this.mRunning == z) {
                return false;
            }
            this.mRunning = z;
            if (z) {
                this.mUptimeStart = j;
                this.mRealtimeStart = j2;
                long uptime = getUptime(j);
                this.mUnpluggedUptime = uptime;
                long realtime = getRealtime(j2);
                this.mUnpluggedRealtime = realtime;
                for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                    this.mObservers.get(size).onTimeStarted(j2, uptime, realtime);
                }
            } else {
                this.mPastUptime += j - this.mUptimeStart;
                this.mPastRealtime += j2 - this.mRealtimeStart;
                long uptime2 = getUptime(j);
                long realtime2 = getRealtime(j2);
                for (int size2 = this.mObservers.size() - 1; size2 >= 0; size2--) {
                    this.mObservers.get(size2).onTimeStopped(j2, uptime2, realtime2);
                }
            }
            return true;
        }

        public void readSummaryFromParcel(Parcel parcel) {
            this.mUptime = parcel.readLong();
            this.mRealtime = parcel.readLong();
        }

        public void writeSummaryToParcel(Parcel parcel, long j, long j2) {
            parcel.writeLong(computeUptime(j, 0));
            parcel.writeLong(computeRealtime(j2, 0));
        }

        public void readFromParcel(Parcel parcel) {
            this.mRunning = false;
            this.mUptime = parcel.readLong();
            this.mPastUptime = parcel.readLong();
            this.mUptimeStart = parcel.readLong();
            this.mRealtime = parcel.readLong();
            this.mPastRealtime = parcel.readLong();
            this.mRealtimeStart = parcel.readLong();
            this.mUnpluggedUptime = parcel.readLong();
            this.mUnpluggedRealtime = parcel.readLong();
        }

        public void writeToParcel(Parcel parcel, long j, long j2) {
            long uptime = getUptime(j);
            long realtime = getRealtime(j2);
            parcel.writeLong(this.mUptime);
            parcel.writeLong(uptime);
            parcel.writeLong(this.mUptimeStart);
            parcel.writeLong(this.mRealtime);
            parcel.writeLong(realtime);
            parcel.writeLong(this.mRealtimeStart);
            parcel.writeLong(this.mUnpluggedUptime);
            parcel.writeLong(this.mUnpluggedRealtime);
        }
    }

    public static class Counter extends BatteryStats.Counter implements TimeBaseObs {
        final AtomicInteger mCount = new AtomicInteger();
        int mLoadedCount;
        int mPluggedCount;
        final TimeBase mTimeBase;
        int mUnpluggedCount;

        public Counter(TimeBase timeBase, Parcel parcel) {
            this.mTimeBase = timeBase;
            int i = parcel.readInt();
            this.mPluggedCount = i;
            this.mCount.set(i);
            this.mLoadedCount = parcel.readInt();
            this.mUnpluggedCount = parcel.readInt();
            timeBase.add(this);
        }

        public Counter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.mCount.get());
            parcel.writeInt(this.mLoadedCount);
            parcel.writeInt(this.mUnpluggedCount);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            this.mUnpluggedCount = this.mPluggedCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            this.mPluggedCount = this.mCount.get();
        }

        public static void writeCounterToParcel(Parcel parcel, Counter counter) {
            if (counter == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                counter.writeToParcel(parcel);
            }
        }

        public static Counter readCounterFromParcel(TimeBase timeBase, Parcel parcel) {
            if (parcel.readInt() == 0) {
                return null;
            }
            return new Counter(timeBase, parcel);
        }

        @Override // android.os.BatteryStats.Counter
        public int getCountLocked(int i) {
            int i2;
            int i3 = this.mCount.get();
            if (i == 2) {
                i2 = this.mUnpluggedCount;
            } else {
                if (i == 0) {
                    return i3;
                }
                i2 = this.mLoadedCount;
            }
            return i3 - i2;
        }

        @Override // android.os.BatteryStats.Counter
        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount.get() + " mLoadedCount=" + this.mLoadedCount + " mUnpluggedCount=" + this.mUnpluggedCount + " mPluggedCount=" + this.mPluggedCount);
        }

        public void stepAtomic() {
            if (this.mTimeBase.isRunning()) {
                this.mCount.incrementAndGet();
            }
        }

        void addAtomic(int i) {
            if (this.mTimeBase.isRunning()) {
                this.mCount.addAndGet(i);
            }
        }

        void reset(boolean z) {
            this.mCount.set(0);
            this.mUnpluggedCount = 0;
            this.mPluggedCount = 0;
            this.mLoadedCount = 0;
            if (z) {
                detach();
            }
        }

        void detach() {
            this.mTimeBase.remove(this);
        }

        public void writeSummaryFromParcelLocked(Parcel parcel) {
            parcel.writeInt(this.mCount.get());
        }

        public void readSummaryFromParcelLocked(Parcel parcel) {
            int i = parcel.readInt();
            this.mLoadedCount = i;
            this.mCount.set(i);
            int i2 = this.mLoadedCount;
            this.mPluggedCount = i2;
            this.mUnpluggedCount = i2;
        }
    }

    public static class LongSamplingCounterArray extends BatteryStats.LongCounterArray implements TimeBaseObs {
        public long[] mCounts;
        public long[] mLoadedCounts;
        final TimeBase mTimeBase;
        public long[] mUnpluggedCounts;

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
        }

        private LongSamplingCounterArray(TimeBase timeBase, Parcel parcel) {
            this.mTimeBase = timeBase;
            this.mCounts = parcel.createLongArray();
            this.mLoadedCounts = parcel.createLongArray();
            this.mUnpluggedCounts = parcel.createLongArray();
            timeBase.add(this);
        }

        public LongSamplingCounterArray(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeToParcel(Parcel parcel) {
            parcel.writeLongArray(this.mCounts);
            parcel.writeLongArray(this.mLoadedCounts);
            parcel.writeLongArray(this.mUnpluggedCounts);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            this.mUnpluggedCounts = copyArray(this.mCounts, this.mUnpluggedCounts);
        }

        @Override // android.os.BatteryStats.LongCounterArray
        public long[] getCountsLocked(int i) {
            long[] jArrCopyArray = copyArray(this.mCounts, null);
            if (i == 2) {
                subtract(jArrCopyArray, this.mUnpluggedCounts);
            } else if (i != 0) {
                subtract(jArrCopyArray, this.mLoadedCounts);
            }
            return jArrCopyArray;
        }

        @Override // android.os.BatteryStats.LongCounterArray
        public void logState(Printer printer, String str) {
            printer.println(str + "mCounts=" + Arrays.toString(this.mCounts) + " mLoadedCounts=" + Arrays.toString(this.mLoadedCounts) + " mUnpluggedCounts=" + Arrays.toString(this.mUnpluggedCounts));
        }

        public void addCountLocked(long[] jArr) {
            addCountLocked(jArr, this.mTimeBase.isRunning());
        }

        public void addCountLocked(long[] jArr, boolean z) {
            if (jArr != null && z) {
                if (this.mCounts == null) {
                    this.mCounts = new long[jArr.length];
                }
                for (int i = 0; i < jArr.length; i++) {
                    long[] jArr2 = this.mCounts;
                    jArr2[i] = jArr2[i] + jArr[i];
                }
            }
        }

        public int getSize() {
            long[] jArr = this.mCounts;
            if (jArr == null) {
                return 0;
            }
            return jArr.length;
        }

        public void reset(boolean z) {
            fillArray(this.mCounts, 0L);
            fillArray(this.mLoadedCounts, 0L);
            fillArray(this.mUnpluggedCounts, 0L);
            if (z) {
                detach();
            }
        }

        public void detach() {
            this.mTimeBase.remove(this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeSummaryToParcelLocked(Parcel parcel) {
            parcel.writeLongArray(this.mCounts);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void readSummaryFromParcelLocked(Parcel parcel) {
            long[] jArrCreateLongArray = parcel.createLongArray();
            this.mCounts = jArrCreateLongArray;
            this.mLoadedCounts = copyArray(jArrCreateLongArray, this.mLoadedCounts);
            this.mUnpluggedCounts = copyArray(this.mCounts, this.mUnpluggedCounts);
        }

        public static void writeToParcel(Parcel parcel, LongSamplingCounterArray longSamplingCounterArray) {
            if (longSamplingCounterArray != null) {
                parcel.writeInt(1);
                longSamplingCounterArray.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
        }

        public static LongSamplingCounterArray readFromParcel(Parcel parcel, TimeBase timeBase) {
            if (parcel.readInt() != 0) {
                return new LongSamplingCounterArray(timeBase, parcel);
            }
            return null;
        }

        public static void writeSummaryToParcelLocked(Parcel parcel, LongSamplingCounterArray longSamplingCounterArray) {
            if (longSamplingCounterArray != null) {
                parcel.writeInt(1);
                longSamplingCounterArray.writeSummaryToParcelLocked(parcel);
            } else {
                parcel.writeInt(0);
            }
        }

        public static LongSamplingCounterArray readSummaryFromParcelLocked(Parcel parcel, TimeBase timeBase) {
            if (parcel.readInt() == 0) {
                return null;
            }
            LongSamplingCounterArray longSamplingCounterArray = new LongSamplingCounterArray(timeBase);
            longSamplingCounterArray.readSummaryFromParcelLocked(parcel);
            return longSamplingCounterArray;
        }

        private static void fillArray(long[] jArr, long j) {
            if (jArr != null) {
                Arrays.fill(jArr, j);
            }
        }

        private static void subtract(long[] jArr, long[] jArr2) {
            if (jArr2 == null) {
                return;
            }
            for (int i = 0; i < jArr.length; i++) {
                jArr[i] = jArr[i] - jArr2[i];
            }
        }

        private static long[] copyArray(long[] jArr, long[] jArr2) {
            if (jArr == null) {
                return null;
            }
            if (jArr2 == null) {
                jArr2 = new long[jArr.length];
            }
            System.arraycopy(jArr, 0, jArr2, 0, jArr.length);
            return jArr2;
        }
    }

    public static class LongSamplingCounter extends BatteryStats.LongCounter implements TimeBaseObs {
        public long mCount;
        public long mCurrentCount;
        public long mLoadedCount;
        final TimeBase mTimeBase;
        public long mUnpluggedCount;

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
        }

        public LongSamplingCounter(TimeBase timeBase, Parcel parcel) {
            this.mTimeBase = timeBase;
            this.mCount = parcel.readLong();
            this.mCurrentCount = parcel.readLong();
            this.mLoadedCount = parcel.readLong();
            this.mUnpluggedCount = parcel.readLong();
            timeBase.add(this);
        }

        public LongSamplingCounter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeLong(this.mCount);
            parcel.writeLong(this.mCurrentCount);
            parcel.writeLong(this.mLoadedCount);
            parcel.writeLong(this.mUnpluggedCount);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            this.mUnpluggedCount = this.mCount;
        }

        @Override // android.os.BatteryStats.LongCounter
        public long getCountLocked(int i) {
            long j;
            long j2 = this.mCount;
            if (i == 2) {
                j = this.mUnpluggedCount;
            } else {
                if (i == 0) {
                    return j2;
                }
                j = this.mLoadedCount;
            }
            return j2 - j;
        }

        @Override // android.os.BatteryStats.LongCounter
        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount + " mCurrentCount=" + this.mCurrentCount + " mLoadedCount=" + this.mLoadedCount + " mUnpluggedCount=" + this.mUnpluggedCount);
        }

        public void addCountLocked(long j) {
            update(this.mCurrentCount + j, this.mTimeBase.isRunning());
        }

        public void addCountLocked(long j, boolean z) {
            update(this.mCurrentCount + j, z);
        }

        public void update(long j) {
            update(j, this.mTimeBase.isRunning());
        }

        public void update(long j, boolean z) {
            if (j < this.mCurrentCount) {
                this.mCurrentCount = 0L;
            }
            if (z) {
                this.mCount += j - this.mCurrentCount;
            }
            this.mCurrentCount = j;
        }

        public void reset(boolean z) {
            this.mCount = 0L;
            this.mUnpluggedCount = 0L;
            this.mLoadedCount = 0L;
            if (z) {
                detach();
            }
        }

        public void detach() {
            this.mTimeBase.remove(this);
        }

        public void writeSummaryFromParcelLocked(Parcel parcel) {
            parcel.writeLong(this.mCount);
        }

        public void readSummaryFromParcelLocked(Parcel parcel) {
            long j = parcel.readLong();
            this.mLoadedCount = j;
            this.mUnpluggedCount = j;
            this.mCount = j;
        }
    }

    public static abstract class Timer extends BatteryStats.Timer implements TimeBaseObs {
        protected final Clocks mClocks;
        protected int mCount;
        protected int mLastCount;
        protected long mLastTime;
        protected int mLoadedCount;
        protected long mLoadedTime;
        protected final TimeBase mTimeBase;
        protected long mTimeBeforeMark;
        protected long mTotalTime;
        protected final int mType;
        protected int mUnpluggedCount;
        protected long mUnpluggedTime;

        protected abstract int computeCurrentCountLocked();

        protected abstract long computeRunTimeLocked(long j);

        public Timer(Clocks clocks, int i, TimeBase timeBase, Parcel parcel) {
            this.mClocks = clocks;
            this.mType = i;
            this.mTimeBase = timeBase;
            this.mCount = parcel.readInt();
            this.mLoadedCount = parcel.readInt();
            this.mLastCount = 0;
            this.mUnpluggedCount = parcel.readInt();
            this.mTotalTime = parcel.readLong();
            this.mLoadedTime = parcel.readLong();
            this.mLastTime = 0L;
            this.mUnpluggedTime = parcel.readLong();
            this.mTimeBeforeMark = parcel.readLong();
            timeBase.add(this);
        }

        public Timer(Clocks clocks, int i, TimeBase timeBase) {
            this.mClocks = clocks;
            this.mType = i;
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public boolean reset(boolean z) {
            this.mTimeBeforeMark = 0L;
            this.mLastTime = 0L;
            this.mLoadedTime = 0L;
            this.mTotalTime = 0L;
            this.mLastCount = 0;
            this.mLoadedCount = 0;
            this.mCount = 0;
            if (!z) {
                return true;
            }
            detach();
            return true;
        }

        public void detach() {
            this.mTimeBase.remove(this);
        }

        public void writeToParcel(Parcel parcel, long j) {
            parcel.writeInt(computeCurrentCountLocked());
            parcel.writeInt(this.mLoadedCount);
            parcel.writeInt(this.mUnpluggedCount);
            parcel.writeLong(computeRunTimeLocked(this.mTimeBase.getRealtime(j)));
            parcel.writeLong(this.mLoadedTime);
            parcel.writeLong(this.mUnpluggedTime);
            parcel.writeLong(this.mTimeBeforeMark);
        }

        public void onTimeStarted(long j, long j2, long j3) {
            this.mUnpluggedTime = computeRunTimeLocked(j3);
            this.mUnpluggedCount = computeCurrentCountLocked();
        }

        public void onTimeStopped(long j, long j2, long j3) {
            this.mTotalTime = computeRunTimeLocked(j3);
            this.mCount = computeCurrentCountLocked();
        }

        public static void writeTimerToParcel(Parcel parcel, Timer timer, long j) {
            if (timer == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                timer.writeToParcel(parcel, j);
            }
        }

        @Override // android.os.BatteryStats.Timer
        public long getTotalTimeLocked(long j, int i) {
            long j2;
            long jComputeRunTimeLocked = computeRunTimeLocked(this.mTimeBase.getRealtime(j));
            if (i == 2) {
                j2 = this.mUnpluggedTime;
            } else {
                if (i == 0) {
                    return jComputeRunTimeLocked;
                }
                j2 = this.mLoadedTime;
            }
            return jComputeRunTimeLocked - j2;
        }

        @Override // android.os.BatteryStats.Timer
        public int getCountLocked(int i) {
            int i2;
            int iComputeCurrentCountLocked = computeCurrentCountLocked();
            if (i == 2) {
                i2 = this.mUnpluggedCount;
            } else {
                if (i == 0) {
                    return iComputeCurrentCountLocked;
                }
                i2 = this.mLoadedCount;
            }
            return iComputeCurrentCountLocked - i2;
        }

        @Override // android.os.BatteryStats.Timer
        public long getTimeSinceMarkLocked(long j) {
            return computeRunTimeLocked(this.mTimeBase.getRealtime(j)) - this.mTimeBeforeMark;
        }

        @Override // android.os.BatteryStats.Timer
        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount);
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("mTotalTime=");
            sb.append(this.mTotalTime);
            sb.append(" mLoadedTime=");
            sb.append(this.mLoadedTime);
            printer.println(sb.toString());
            printer.println(str + "mLastTime=" + this.mLastTime + " mUnpluggedTime=" + this.mUnpluggedTime);
        }

        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            parcel.writeLong(computeRunTimeLocked(this.mTimeBase.getRealtime(j)));
            parcel.writeInt(computeCurrentCountLocked());
        }

        public void readSummaryFromParcelLocked(Parcel parcel) {
            long j = parcel.readLong();
            this.mLoadedTime = j;
            this.mTotalTime = j;
            this.mLastTime = 0L;
            this.mUnpluggedTime = j;
            int i = parcel.readInt();
            this.mLoadedCount = i;
            this.mCount = i;
            this.mLastCount = 0;
            this.mUnpluggedCount = i;
            this.mTimeBeforeMark = this.mTotalTime;
        }
    }

    public static class SamplingTimer extends Timer {
        int mCurrentReportedCount;
        long mCurrentReportedTotalTime;
        boolean mTimeBaseRunning;
        boolean mTrackingReportedValues;
        int mUnpluggedReportedCount;
        long mUnpluggedReportedTotalTime;
        int mUpdateVersion;

        public SamplingTimer(Clocks clocks, TimeBase timeBase, Parcel parcel) {
            super(clocks, 0, timeBase, parcel);
            this.mCurrentReportedCount = parcel.readInt();
            this.mUnpluggedReportedCount = parcel.readInt();
            this.mCurrentReportedTotalTime = parcel.readLong();
            this.mUnpluggedReportedTotalTime = parcel.readLong();
            this.mTrackingReportedValues = parcel.readInt() == 1;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        public SamplingTimer(Clocks clocks, TimeBase timeBase) {
            super(clocks, 0, timeBase);
            this.mTrackingReportedValues = false;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        public void endSample() {
            this.mTotalTime = computeRunTimeLocked(0L);
            this.mCount = computeCurrentCountLocked();
            this.mCurrentReportedTotalTime = 0L;
            this.mUnpluggedReportedTotalTime = 0L;
            this.mCurrentReportedCount = 0;
            this.mUnpluggedReportedCount = 0;
        }

        public void setUpdateVersion(int i) {
            this.mUpdateVersion = i;
        }

        public int getUpdateVersion() {
            return this.mUpdateVersion;
        }

        public void update(long j, int i) {
            if (this.mTimeBaseRunning && !this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTime = j;
                this.mUnpluggedReportedCount = i;
            }
            this.mTrackingReportedValues = true;
            if (j < this.mCurrentReportedTotalTime || i < this.mCurrentReportedCount) {
                endSample();
            }
            this.mCurrentReportedTotalTime = j;
            this.mCurrentReportedCount = i;
        }

        public void add(long j, int i) {
            update(this.mCurrentReportedTotalTime + j, this.mCurrentReportedCount + i);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            super.onTimeStarted(j, j2, j3);
            if (this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTime = this.mCurrentReportedTotalTime;
                this.mUnpluggedReportedCount = this.mCurrentReportedCount;
            }
            this.mTimeBaseRunning = true;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            super.onTimeStopped(j, j2, j3);
            this.mTimeBaseRunning = false;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mCurrentReportedCount=" + this.mCurrentReportedCount + " mUnpluggedReportedCount=" + this.mUnpluggedReportedCount + " mCurrentReportedTotalTime=" + this.mCurrentReportedTotalTime + " mUnpluggedReportedTotalTime=" + this.mUnpluggedReportedTotalTime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long j) {
            return this.mTotalTime + ((this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedTotalTime - this.mUnpluggedReportedTotalTime : 0L);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount + ((this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedCount - this.mUnpluggedReportedCount : 0);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel parcel, long j) {
            super.writeToParcel(parcel, j);
            parcel.writeInt(this.mCurrentReportedCount);
            parcel.writeInt(this.mUnpluggedReportedCount);
            parcel.writeLong(this.mCurrentReportedTotalTime);
            parcel.writeLong(this.mUnpluggedReportedTotalTime);
            parcel.writeInt(this.mTrackingReportedValues ? 1 : 0);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public boolean reset(boolean z) {
            super.reset(z);
            this.mTrackingReportedValues = false;
            this.mUnpluggedReportedTotalTime = 0L;
            this.mUnpluggedReportedCount = 0;
            return true;
        }
    }

    public static class BatchTimer extends Timer {
        boolean mInDischarge;
        long mLastAddedDuration;
        long mLastAddedTime;
        final Uid mUid;

        BatchTimer(Clocks clocks, Uid uid, int i, TimeBase timeBase, Parcel parcel) {
            super(clocks, i, timeBase, parcel);
            this.mUid = uid;
            this.mLastAddedTime = parcel.readLong();
            this.mLastAddedDuration = parcel.readLong();
            this.mInDischarge = timeBase.isRunning();
        }

        BatchTimer(Clocks clocks, Uid uid, int i, TimeBase timeBase) {
            super(clocks, i, timeBase);
            this.mUid = uid;
            this.mInDischarge = timeBase.isRunning();
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel parcel, long j) {
            super.writeToParcel(parcel, j);
            parcel.writeLong(this.mLastAddedTime);
            parcel.writeLong(this.mLastAddedDuration);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            recomputeLastDuration(this.mClocks.elapsedRealtime() * 1000, false);
            this.mInDischarge = false;
            super.onTimeStopped(j, j2, j3);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            recomputeLastDuration(j, false);
            this.mInDischarge = true;
            if (this.mLastAddedTime == j) {
                this.mTotalTime += this.mLastAddedDuration;
            }
            super.onTimeStarted(j, j2, j3);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mLastAddedTime=" + this.mLastAddedTime + " mLastAddedDuration=" + this.mLastAddedDuration);
        }

        private long computeOverage(long j) {
            if (this.mLastAddedTime > 0) {
                return (this.mLastTime + this.mLastAddedDuration) - j;
            }
            return 0L;
        }

        private void recomputeLastDuration(long j, boolean z) {
            long jComputeOverage = computeOverage(j);
            if (jComputeOverage > 0) {
                if (this.mInDischarge) {
                    this.mTotalTime -= jComputeOverage;
                }
                if (z) {
                    this.mLastAddedTime = 0L;
                } else {
                    this.mLastAddedTime = j;
                    this.mLastAddedDuration -= jComputeOverage;
                }
            }
        }

        public void addDuration(BatteryStatsImpl batteryStatsImpl, long j) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
            recomputeLastDuration(jElapsedRealtime, true);
            this.mLastAddedTime = jElapsedRealtime;
            this.mLastAddedDuration = j * 1000;
            if (this.mInDischarge) {
                this.mTotalTime += this.mLastAddedDuration;
                this.mCount++;
            }
        }

        public void abortLastDuration(BatteryStatsImpl batteryStatsImpl) {
            recomputeLastDuration(this.mClocks.elapsedRealtime() * 1000, true);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long j) {
            long jComputeOverage = computeOverage(this.mClocks.elapsedRealtime() * 1000);
            if (jComputeOverage > 0) {
                this.mTotalTime = jComputeOverage;
                return jComputeOverage;
            }
            return this.mTotalTime;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public boolean reset(boolean z) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
            recomputeLastDuration(jElapsedRealtime, true);
            boolean z2 = false;
            boolean z3 = this.mLastAddedTime == jElapsedRealtime;
            if (!z3 && z) {
                z2 = true;
            }
            super.reset(z2);
            return !z3;
        }
    }

    public static class DurationTimer extends StopwatchTimer {
        long mCurrentDurationMs;
        long mMaxDurationMs;
        long mStartTimeMs;
        long mTotalDurationMs;

        public DurationTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, Parcel parcel) {
            super(clocks, uid, i, arrayList, timeBase, parcel);
            this.mStartTimeMs = -1L;
            this.mMaxDurationMs = parcel.readLong();
            this.mTotalDurationMs = parcel.readLong();
            this.mCurrentDurationMs = parcel.readLong();
        }

        public DurationTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase) {
            super(clocks, uid, i, arrayList, timeBase);
            this.mStartTimeMs = -1L;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel parcel, long j) {
            super.writeToParcel(parcel, j);
            long j2 = j / 1000;
            parcel.writeLong(getMaxDurationMsLocked(j2));
            parcel.writeLong(this.mTotalDurationMs);
            parcel.writeLong(getCurrentDurationMsLocked(j2));
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            super.writeSummaryFromParcelLocked(parcel, j);
            long j2 = j / 1000;
            parcel.writeLong(getMaxDurationMsLocked(j2));
            parcel.writeLong(getTotalDurationMsLocked(j2));
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mMaxDurationMs = parcel.readLong();
            this.mTotalDurationMs = parcel.readLong();
            this.mStartTimeMs = -1L;
            this.mCurrentDurationMs = 0L;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            super.onTimeStarted(j, j2, j3);
            if (this.mNesting > 0) {
                this.mStartTimeMs = j3 / 1000;
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            super.onTimeStopped(j, j2, j3);
            if (this.mNesting > 0) {
                this.mCurrentDurationMs += (j3 / 1000) - this.mStartTimeMs;
            }
            this.mStartTimeMs = -1L;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer
        public void startRunningLocked(long j) {
            super.startRunningLocked(j);
            if (this.mNesting == 1 && this.mTimeBase.isRunning()) {
                this.mStartTimeMs = this.mTimeBase.getRealtime(j * 1000) / 1000;
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer
        public void stopRunningLocked(long j) {
            if (this.mNesting == 1) {
                long currentDurationMsLocked = getCurrentDurationMsLocked(j);
                this.mTotalDurationMs += currentDurationMsLocked;
                if (currentDurationMsLocked > this.mMaxDurationMs) {
                    this.mMaxDurationMs = currentDurationMsLocked;
                }
                this.mStartTimeMs = -1L;
                this.mCurrentDurationMs = 0L;
            }
            super.stopRunningLocked(j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public boolean reset(boolean z) {
            boolean zReset = super.reset(z);
            this.mMaxDurationMs = 0L;
            this.mTotalDurationMs = 0L;
            this.mCurrentDurationMs = 0L;
            if (this.mNesting > 0) {
                this.mStartTimeMs = this.mTimeBase.getRealtime(this.mClocks.elapsedRealtime() * 1000) / 1000;
            } else {
                this.mStartTimeMs = -1L;
            }
            return zReset;
        }

        @Override // android.os.BatteryStats.Timer
        public long getMaxDurationMsLocked(long j) {
            if (this.mNesting > 0) {
                long currentDurationMsLocked = getCurrentDurationMsLocked(j);
                if (currentDurationMsLocked > this.mMaxDurationMs) {
                    return currentDurationMsLocked;
                }
            }
            return this.mMaxDurationMs;
        }

        @Override // android.os.BatteryStats.Timer
        public long getCurrentDurationMsLocked(long j) {
            long j2 = this.mCurrentDurationMs;
            return (this.mNesting <= 0 || !this.mTimeBase.isRunning()) ? j2 : j2 + ((this.mTimeBase.getRealtime(j * 1000) / 1000) - this.mStartTimeMs);
        }

        @Override // android.os.BatteryStats.Timer
        public long getTotalDurationMsLocked(long j) {
            return this.mTotalDurationMs + getCurrentDurationMsLocked(j);
        }
    }

    public static class StopwatchTimer extends Timer {
        long mAcquireTime;
        public boolean mInList;
        int mNesting;
        long mTimeout;
        final ArrayList<StopwatchTimer> mTimerPool;
        final Uid mUid;
        long mUpdateTime;

        public StopwatchTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, Parcel parcel) {
            super(clocks, i, timeBase, parcel);
            this.mAcquireTime = -1L;
            this.mUid = uid;
            this.mTimerPool = arrayList;
            this.mUpdateTime = parcel.readLong();
        }

        public StopwatchTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase) {
            super(clocks, i, timeBase);
            this.mAcquireTime = -1L;
            this.mUid = uid;
            this.mTimerPool = arrayList;
        }

        public void setTimeout(long j) {
            this.mTimeout = j;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel parcel, long j) {
            super.writeToParcel(parcel, j);
            parcel.writeLong(this.mUpdateTime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            if (this.mNesting > 0) {
                super.onTimeStopped(j, j2, j3);
                this.mUpdateTime = j3;
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mNesting=" + this.mNesting + " mUpdateTime=" + this.mUpdateTime + " mAcquireTime=" + this.mAcquireTime);
        }

        public void startRunningLocked(long j) {
            int i = this.mNesting;
            this.mNesting = i + 1;
            if (i == 0) {
                long realtime = this.mTimeBase.getRealtime(j * 1000);
                this.mUpdateTime = realtime;
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, null);
                    this.mTimerPool.add(this);
                }
                if (this.mTimeBase.isRunning()) {
                    this.mCount++;
                    this.mAcquireTime = this.mTotalTime;
                } else {
                    this.mAcquireTime = -1L;
                }
            }
        }

        @Override // android.os.BatteryStats.Timer
        public boolean isRunningLocked() {
            return this.mNesting > 0;
        }

        public void stopRunningLocked(long j) {
            int i = this.mNesting;
            if (i == 0) {
                return;
            }
            int i2 = i - 1;
            this.mNesting = i2;
            if (i2 == 0) {
                long realtime = this.mTimeBase.getRealtime(j * 1000);
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, null);
                    this.mTimerPool.remove(this);
                } else {
                    this.mNesting = 1;
                    this.mTotalTime = computeRunTimeLocked(realtime);
                    this.mNesting = 0;
                }
                if (this.mAcquireTime < 0 || this.mTotalTime != this.mAcquireTime) {
                    return;
                }
                this.mCount--;
            }
        }

        public void stopAllRunningLocked(long j) {
            if (this.mNesting > 0) {
                this.mNesting = 1;
                stopRunningLocked(j);
            }
        }

        private static long refreshTimersLocked(long j, ArrayList<StopwatchTimer> arrayList, StopwatchTimer stopwatchTimer) {
            int size = arrayList.size();
            long j2 = 0;
            for (int i = size - 1; i >= 0; i--) {
                StopwatchTimer stopwatchTimer2 = arrayList.get(i);
                long j3 = j - stopwatchTimer2.mUpdateTime;
                if (j3 > 0) {
                    long j4 = j3 / ((long) size);
                    if (stopwatchTimer2 == stopwatchTimer) {
                        j2 = j4;
                    }
                    stopwatchTimer2.mTotalTime += j4;
                }
                stopwatchTimer2.mUpdateTime = j;
            }
            return j2;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long j) {
            long j2 = this.mTimeout;
            long size = 0;
            if (j2 > 0) {
                long j3 = this.mUpdateTime;
                if (j > j3 + j2) {
                    j = j3 + j2;
                }
            }
            long j4 = this.mTotalTime;
            if (this.mNesting > 0) {
                long j5 = j - this.mUpdateTime;
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                size = j5 / ((long) (arrayList != null ? arrayList.size() : 1));
            }
            return j4 + size;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public boolean reset(boolean z) {
            boolean z2 = this.mNesting <= 0;
            super.reset(z2 && z);
            if (this.mNesting > 0) {
                this.mUpdateTime = this.mTimeBase.getRealtime(this.mClocks.elapsedRealtime() * 1000);
            }
            this.mAcquireTime = -1L;
            return z2;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void detach() {
            super.detach();
            ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
            if (arrayList != null) {
                arrayList.remove(this);
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mNesting = 0;
        }

        public void setMark(long j) {
            long realtime = this.mTimeBase.getRealtime(j * 1000);
            if (this.mNesting > 0) {
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, this);
                } else {
                    this.mTotalTime += realtime - this.mUpdateTime;
                    this.mUpdateTime = realtime;
                }
            }
            this.mTimeBeforeMark = this.mTotalTime;
        }
    }

    public static class DualTimer extends DurationTimer {
        private final DurationTimer mSubTimer;

        public DualTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, TimeBase timeBase2, Parcel parcel) {
            super(clocks, uid, i, arrayList, timeBase, parcel);
            this.mSubTimer = new DurationTimer(clocks, uid, i, null, timeBase2, parcel);
        }

        public DualTimer(Clocks clocks, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, TimeBase timeBase2) {
            super(clocks, uid, i, arrayList, timeBase);
            this.mSubTimer = new DurationTimer(clocks, uid, i, null, timeBase2);
        }

        @Override // android.os.BatteryStats.Timer
        public DurationTimer getSubTimer() {
            return this.mSubTimer;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.StopwatchTimer
        public void startRunningLocked(long j) {
            super.startRunningLocked(j);
            this.mSubTimer.startRunningLocked(j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.StopwatchTimer
        public void stopRunningLocked(long j) {
            super.stopRunningLocked(j);
            this.mSubTimer.stopRunningLocked(j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer
        public void stopAllRunningLocked(long j) {
            super.stopAllRunningLocked(j);
            this.mSubTimer.stopAllRunningLocked(j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public boolean reset(boolean z) {
            return !((!super.reset(z)) | (!this.mSubTimer.reset(false)) | false);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void detach() {
            this.mSubTimer.detach();
            super.detach();
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel parcel, long j) {
            super.writeToParcel(parcel, j);
            this.mSubTimer.writeToParcel(parcel, j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            super.writeSummaryFromParcelLocked(parcel, j);
            this.mSubTimer.writeSummaryFromParcelLocked(parcel, j);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.DurationTimer, com.android.internal.os.BatteryStatsImpl.StopwatchTimer, com.android.internal.os.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mSubTimer.readSummaryFromParcelLocked(parcel);
        }
    }

    public abstract class OverflowArrayMap<T> {
        private static final String OVERFLOW_NAME = "*overflow*";
        ArrayMap<String, MutableInt> mActiveOverflow;
        T mCurOverflow;
        long mLastCleanupTime;
        long mLastClearTime;
        long mLastOverflowFinishTime;
        long mLastOverflowTime;
        final ArrayMap<String, T> mMap = new ArrayMap<>();
        final int mUid;

        public abstract T instantiateObject();

        public OverflowArrayMap(int i) {
            this.mUid = i;
        }

        public ArrayMap<String, T> getMap() {
            return this.mMap;
        }

        public void clear() {
            this.mLastClearTime = SystemClock.elapsedRealtime();
            this.mMap.clear();
            this.mCurOverflow = null;
            this.mActiveOverflow = null;
        }

        public void add(String str, T t) {
            if (str == null) {
                str = "";
            }
            this.mMap.put(str, t);
            if (OVERFLOW_NAME.equals(str)) {
                this.mCurOverflow = t;
            }
        }

        public void cleanup() {
            this.mLastCleanupTime = SystemClock.elapsedRealtime();
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap != null && arrayMap.size() == 0) {
                this.mActiveOverflow = null;
            }
            if (this.mActiveOverflow == null) {
                if (this.mMap.containsKey(OVERFLOW_NAME)) {
                    Slog.wtf(BatteryStatsImpl.TAG, "Cleaning up with no active overflow, but have overflow entry " + this.mMap.get(OVERFLOW_NAME));
                    this.mMap.remove(OVERFLOW_NAME);
                }
                this.mCurOverflow = null;
                return;
            }
            if (this.mCurOverflow == null || !this.mMap.containsKey(OVERFLOW_NAME)) {
                Slog.wtf(BatteryStatsImpl.TAG, "Cleaning up with active overflow, but no overflow entry: cur=" + this.mCurOverflow + " map=" + this.mMap.get(OVERFLOW_NAME));
            }
        }

        public T startObject(String str) {
            MutableInt mutableInt;
            if (str == null) {
                str = "";
            }
            T t = this.mMap.get(str);
            if (t != null) {
                return t;
            }
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap == null || (mutableInt = arrayMap.get(str)) == null) {
                if (this.mMap.size() >= BatteryStatsImpl.MAX_WAKELOCKS_PER_UID) {
                    T tInstantiateObject = this.mCurOverflow;
                    if (tInstantiateObject == null) {
                        tInstantiateObject = instantiateObject();
                        this.mCurOverflow = tInstantiateObject;
                        this.mMap.put(OVERFLOW_NAME, tInstantiateObject);
                    }
                    if (this.mActiveOverflow == null) {
                        this.mActiveOverflow = new ArrayMap<>();
                    }
                    this.mActiveOverflow.put(str, new MutableInt(1));
                    this.mLastOverflowTime = SystemClock.elapsedRealtime();
                    return tInstantiateObject;
                }
                T tInstantiateObject2 = instantiateObject();
                this.mMap.put(str, tInstantiateObject2);
                return tInstantiateObject2;
            }
            T tInstantiateObject3 = this.mCurOverflow;
            if (tInstantiateObject3 == null) {
                Slog.wtf(BatteryStatsImpl.TAG, "Have active overflow " + str + " but null overflow");
                tInstantiateObject3 = instantiateObject();
                this.mCurOverflow = tInstantiateObject3;
                this.mMap.put(OVERFLOW_NAME, tInstantiateObject3);
            }
            mutableInt.value++;
            return tInstantiateObject3;
        }

        public T stopObject(String str) {
            MutableInt mutableInt;
            T t;
            if (str == null) {
                str = "";
            }
            T t2 = this.mMap.get(str);
            if (t2 != null) {
                return t2;
            }
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap != null && (mutableInt = arrayMap.get(str)) != null && (t = this.mCurOverflow) != null) {
                mutableInt.value--;
                if (mutableInt.value <= 0) {
                    this.mActiveOverflow.remove(str);
                    this.mLastOverflowFinishTime = SystemClock.elapsedRealtime();
                }
                return t;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to find object for ");
            sb.append(str);
            sb.append(" in uid ");
            sb.append(this.mUid);
            sb.append(" mapsize=");
            sb.append(this.mMap.size());
            sb.append(" activeoverflow=");
            sb.append(this.mActiveOverflow);
            sb.append(" curoverflow=");
            sb.append(this.mCurOverflow);
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (this.mLastOverflowTime != 0) {
                sb.append(" lastOverflowTime=");
                TimeUtils.formatDuration(this.mLastOverflowTime - jElapsedRealtime, sb);
            }
            if (this.mLastOverflowFinishTime != 0) {
                sb.append(" lastOverflowFinishTime=");
                TimeUtils.formatDuration(this.mLastOverflowFinishTime - jElapsedRealtime, sb);
            }
            if (this.mLastClearTime != 0) {
                sb.append(" lastClearTime=");
                TimeUtils.formatDuration(this.mLastClearTime - jElapsedRealtime, sb);
            }
            if (this.mLastCleanupTime != 0) {
                sb.append(" lastCleanupTime=");
                TimeUtils.formatDuration(this.mLastCleanupTime - jElapsedRealtime, sb);
            }
            Slog.wtf(BatteryStatsImpl.TAG, sb.toString());
            return null;
        }
    }

    public static class ControllerActivityCounterImpl extends BatteryStats.ControllerActivityCounter implements Parcelable {
        private final LongSamplingCounter mIdleTimeMillis;
        private final LongSamplingCounter mPowerDrainMaMs;
        private final LongSamplingCounter mRxTimeMillis;
        private final LongSamplingCounter mScanTimeMillis;
        private final LongSamplingCounter mSleepTimeMillis;
        private final LongSamplingCounter[] mTxTimeMillis;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ControllerActivityCounterImpl(TimeBase timeBase, int i) {
            this.mIdleTimeMillis = new LongSamplingCounter(timeBase);
            this.mScanTimeMillis = new LongSamplingCounter(timeBase);
            this.mSleepTimeMillis = new LongSamplingCounter(timeBase);
            this.mRxTimeMillis = new LongSamplingCounter(timeBase);
            this.mTxTimeMillis = new LongSamplingCounter[i];
            for (int i2 = 0; i2 < i; i2++) {
                this.mTxTimeMillis[i2] = new LongSamplingCounter(timeBase);
            }
            this.mPowerDrainMaMs = new LongSamplingCounter(timeBase);
        }

        public ControllerActivityCounterImpl(TimeBase timeBase, int i, Parcel parcel) {
            this.mIdleTimeMillis = new LongSamplingCounter(timeBase, parcel);
            this.mScanTimeMillis = new LongSamplingCounter(timeBase, parcel);
            this.mSleepTimeMillis = new LongSamplingCounter(timeBase, parcel);
            this.mRxTimeMillis = new LongSamplingCounter(timeBase, parcel);
            if (parcel.readInt() != i) {
                throw new ParcelFormatException("inconsistent tx state lengths");
            }
            this.mTxTimeMillis = new LongSamplingCounter[i];
            for (int i2 = 0; i2 < i; i2++) {
                this.mTxTimeMillis[i2] = new LongSamplingCounter(timeBase, parcel);
            }
            this.mPowerDrainMaMs = new LongSamplingCounter(timeBase, parcel);
        }

        public void readSummaryFromParcel(Parcel parcel) {
            this.mIdleTimeMillis.readSummaryFromParcelLocked(parcel);
            this.mScanTimeMillis.readSummaryFromParcelLocked(parcel);
            this.mSleepTimeMillis.readSummaryFromParcelLocked(parcel);
            this.mRxTimeMillis.readSummaryFromParcelLocked(parcel);
            int i = parcel.readInt();
            LongSamplingCounter[] longSamplingCounterArr = this.mTxTimeMillis;
            if (i != longSamplingCounterArr.length) {
                throw new ParcelFormatException("inconsistent tx state lengths");
            }
            for (LongSamplingCounter longSamplingCounter : longSamplingCounterArr) {
                longSamplingCounter.readSummaryFromParcelLocked(parcel);
            }
            this.mPowerDrainMaMs.readSummaryFromParcelLocked(parcel);
        }

        public void writeSummaryToParcel(Parcel parcel) {
            this.mIdleTimeMillis.writeSummaryFromParcelLocked(parcel);
            this.mScanTimeMillis.writeSummaryFromParcelLocked(parcel);
            this.mSleepTimeMillis.writeSummaryFromParcelLocked(parcel);
            this.mRxTimeMillis.writeSummaryFromParcelLocked(parcel);
            parcel.writeInt(this.mTxTimeMillis.length);
            for (LongSamplingCounter longSamplingCounter : this.mTxTimeMillis) {
                longSamplingCounter.writeSummaryFromParcelLocked(parcel);
            }
            this.mPowerDrainMaMs.writeSummaryFromParcelLocked(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            this.mIdleTimeMillis.writeToParcel(parcel);
            this.mScanTimeMillis.writeToParcel(parcel);
            this.mSleepTimeMillis.writeToParcel(parcel);
            this.mRxTimeMillis.writeToParcel(parcel);
            parcel.writeInt(this.mTxTimeMillis.length);
            for (LongSamplingCounter longSamplingCounter : this.mTxTimeMillis) {
                longSamplingCounter.writeToParcel(parcel);
            }
            this.mPowerDrainMaMs.writeToParcel(parcel);
        }

        public void reset(boolean z) {
            this.mIdleTimeMillis.reset(z);
            this.mScanTimeMillis.reset(z);
            this.mSleepTimeMillis.reset(z);
            this.mRxTimeMillis.reset(z);
            for (LongSamplingCounter longSamplingCounter : this.mTxTimeMillis) {
                longSamplingCounter.reset(z);
            }
            this.mPowerDrainMaMs.reset(z);
        }

        public void detach() {
            this.mIdleTimeMillis.detach();
            this.mScanTimeMillis.detach();
            this.mSleepTimeMillis.detach();
            this.mRxTimeMillis.detach();
            for (LongSamplingCounter longSamplingCounter : this.mTxTimeMillis) {
                longSamplingCounter.detach();
            }
            this.mPowerDrainMaMs.detach();
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter getIdleTimeCounter() {
            return this.mIdleTimeMillis;
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter getScanTimeCounter() {
            return this.mScanTimeMillis;
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter getSleepTimeCounter() {
            return this.mSleepTimeMillis;
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter getRxTimeCounter() {
            return this.mRxTimeMillis;
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter[] getTxTimeCounters() {
            return this.mTxTimeMillis;
        }

        @Override // android.os.BatteryStats.ControllerActivityCounter
        public LongSamplingCounter getPowerCounter() {
            return this.mPowerDrainMaMs;
        }
    }

    public SamplingTimer getRpmTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mRpmStats.get(str);
        if (samplingTimer != null) {
            return samplingTimer;
        }
        SamplingTimer samplingTimer2 = new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase);
        this.mRpmStats.put(str, samplingTimer2);
        return samplingTimer2;
    }

    public SamplingTimer getScreenOffRpmTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mScreenOffRpmStats.get(str);
        if (samplingTimer != null) {
            return samplingTimer;
        }
        SamplingTimer samplingTimer2 = new SamplingTimer(this.mClocks, this.mOnBatteryScreenOffTimeBase);
        this.mScreenOffRpmStats.put(str, samplingTimer2);
        return samplingTimer2;
    }

    public SamplingTimer getWakeupReasonTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mWakeupReasonStats.get(str);
        if (samplingTimer != null) {
            return samplingTimer;
        }
        SamplingTimer samplingTimer2 = new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase);
        this.mWakeupReasonStats.put(str, samplingTimer2);
        return samplingTimer2;
    }

    public SamplingTimer getKernelWakelockTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mKernelWakelockStats.get(str);
        if (samplingTimer != null) {
            return samplingTimer;
        }
        SamplingTimer samplingTimer2 = new SamplingTimer(this.mClocks, this.mOnBatteryScreenOffTimeBase);
        this.mKernelWakelockStats.put(str, samplingTimer2);
        return samplingTimer2;
    }

    public SamplingTimer getKernelMemoryTimerLocked(long j) {
        SamplingTimer samplingTimer = this.mKernelMemoryStats.get(j);
        if (samplingTimer != null) {
            return samplingTimer;
        }
        SamplingTimer samplingTimer2 = new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase);
        this.mKernelMemoryStats.put(j, samplingTimer2);
        return samplingTimer2;
    }

    private int writeHistoryTag(BatteryStats.HistoryTag historyTag) {
        Integer num = this.mHistoryTagPool.get(historyTag);
        if (num != null) {
            return num.intValue();
        }
        int i = this.mNextHistoryTagIdx;
        BatteryStats.HistoryTag historyTag2 = new BatteryStats.HistoryTag();
        historyTag2.setTo(historyTag);
        historyTag.poolIdx = i;
        this.mHistoryTagPool.put(historyTag2, Integer.valueOf(i));
        this.mNextHistoryTagIdx++;
        this.mNumHistoryTagChars += historyTag2.string.length() + 1;
        return i;
    }

    private void readHistoryTag(int i, BatteryStats.HistoryTag historyTag) {
        historyTag.string = this.mReadHistoryStrings[i];
        historyTag.uid = this.mReadHistoryUids[i];
        historyTag.poolIdx = i;
    }

    public void writeHistoryDelta(Parcel parcel, BatteryStats.HistoryItem historyItem, BatteryStats.HistoryItem historyItem2) {
        int i;
        if (historyItem2 == null || historyItem.cmd != 0) {
            parcel.writeInt(DELTA_TIME_ABS);
            historyItem.writeToParcel(parcel, 0);
            return;
        }
        long j = historyItem.time - historyItem2.time;
        int iBuildBatteryLevelInt = buildBatteryLevelInt(historyItem2);
        int iBuildStateInt = buildStateInt(historyItem2);
        if (j < 0 || j > 2147483647L) {
            i = EventLogTags.SYSUI_VIEW_VISIBILITY;
        } else {
            i = j >= 524285 ? DELTA_TIME_INT : (int) j;
        }
        int i2 = (historyItem.states & DELTA_STATE_MASK) | i;
        int i3 = this.mLastHistoryStepLevel > historyItem.batteryLevel ? 1 : 0;
        boolean z = i3 != 0 || this.mLastHistoryStepDetails == null;
        int iBuildBatteryLevelInt2 = buildBatteryLevelInt(historyItem) | i3;
        boolean z2 = iBuildBatteryLevelInt2 != iBuildBatteryLevelInt;
        if (z2) {
            i2 |= 524288;
        }
        int iBuildStateInt2 = buildStateInt(historyItem);
        boolean z3 = iBuildStateInt2 != iBuildStateInt;
        if (z3) {
            i2 |= 1048576;
        }
        boolean z4 = historyItem.states2 != historyItem2.states2;
        if (z4) {
            i2 |= 2097152;
        }
        if (historyItem.wakelockTag != null || historyItem.wakeReasonTag != null) {
            i2 |= 4194304;
        }
        if (historyItem.eventCode != 0) {
            i2 |= 8388608;
        }
        boolean z5 = historyItem.batteryChargeUAh != historyItem2.batteryChargeUAh;
        if (z5) {
            i2 |= 16777216;
        }
        parcel.writeInt(i2);
        if (i >= DELTA_TIME_INT) {
            if (i == DELTA_TIME_INT) {
                parcel.writeInt((int) j);
            } else {
                parcel.writeLong(j);
            }
        }
        if (z2) {
            parcel.writeInt(iBuildBatteryLevelInt2);
        }
        if (z3) {
            parcel.writeInt(iBuildStateInt2);
        }
        if (z4) {
            parcel.writeInt(historyItem.states2);
        }
        if (historyItem.wakelockTag != null || historyItem.wakeReasonTag != null) {
            parcel.writeInt((historyItem.wakelockTag != null ? writeHistoryTag(historyItem.wakelockTag) : 65535) | ((historyItem.wakeReasonTag != null ? writeHistoryTag(historyItem.wakeReasonTag) : 65535) << 16));
        }
        if (historyItem.eventCode != 0) {
            parcel.writeInt((writeHistoryTag(historyItem.eventTag) << 16) | (65535 & historyItem.eventCode));
        }
        if (z) {
            PlatformIdleStateCallback platformIdleStateCallback = this.mPlatformIdleStateCallback;
            if (platformIdleStateCallback != null) {
                this.mCurHistoryStepDetails.statPlatformIdleState = platformIdleStateCallback.getPlatformLowPowerStats();
                this.mCurHistoryStepDetails.statSubsystemPowerState = this.mPlatformIdleStateCallback.getSubsystemLowPowerStats();
            }
            computeHistoryStepDetails(this.mCurHistoryStepDetails, this.mLastHistoryStepDetails);
            if (i3 != 0) {
                this.mCurHistoryStepDetails.writeToParcel(parcel);
            }
            historyItem.stepDetails = this.mCurHistoryStepDetails;
            this.mLastHistoryStepDetails = this.mCurHistoryStepDetails;
        } else {
            historyItem.stepDetails = null;
        }
        if (this.mLastHistoryStepLevel < historyItem.batteryLevel) {
            this.mLastHistoryStepDetails = null;
        }
        this.mLastHistoryStepLevel = historyItem.batteryLevel;
        if (z5) {
            parcel.writeInt(historyItem.batteryChargeUAh);
        }
    }

    private int buildBatteryLevelInt(BatteryStats.HistoryItem historyItem) {
        return ((historyItem.batteryVoltage << 1) & 32766) | ((historyItem.batteryLevel << 25) & DELTA_STATE_MASK) | ((historyItem.batteryTemperature << 15) & 33521664);
    }

    private void readBatteryLevelInt(int i, BatteryStats.HistoryItem historyItem) {
        historyItem.batteryLevel = (byte) ((DELTA_STATE_MASK & i) >>> 25);
        historyItem.batteryTemperature = (short) ((33521664 & i) >>> 15);
        historyItem.batteryVoltage = (char) ((i & 32766) >>> 1);
    }

    private int buildStateInt(BatteryStats.HistoryItem historyItem) {
        int i = 1;
        if ((historyItem.batteryPlugType & 1) == 0) {
            if ((historyItem.batteryPlugType & 2) != 0) {
                i = 2;
            } else {
                i = (historyItem.batteryPlugType & 4) != 0 ? 3 : 0;
            }
        }
        return (historyItem.states & 16777215) | ((historyItem.batteryStatus & 7) << 29) | ((historyItem.batteryHealth & 7) << 26) | ((i & 3) << 24);
    }

    private void computeHistoryStepDetails(BatteryStats.HistoryStepDetails historyStepDetails, BatteryStats.HistoryStepDetails historyStepDetails2) {
        BatteryStats.HistoryStepDetails historyStepDetails3 = historyStepDetails2 != null ? this.mTmpHistoryStepDetails : historyStepDetails;
        requestImmediateCpuUpdate();
        int i = 0;
        if (historyStepDetails2 == null) {
            int size = this.mUidStats.size();
            while (i < size) {
                Uid uidValueAt = this.mUidStats.valueAt(i);
                uidValueAt.mLastStepUserTime = uidValueAt.mCurStepUserTime;
                uidValueAt.mLastStepSystemTime = uidValueAt.mCurStepSystemTime;
                i++;
            }
            this.mLastStepCpuUserTime = this.mCurStepCpuUserTime;
            this.mLastStepCpuSystemTime = this.mCurStepCpuSystemTime;
            this.mLastStepStatUserTime = this.mCurStepStatUserTime;
            this.mLastStepStatSystemTime = this.mCurStepStatSystemTime;
            this.mLastStepStatIOWaitTime = this.mCurStepStatIOWaitTime;
            this.mLastStepStatIrqTime = this.mCurStepStatIrqTime;
            this.mLastStepStatSoftIrqTime = this.mCurStepStatSoftIrqTime;
            this.mLastStepStatIdleTime = this.mCurStepStatIdleTime;
            historyStepDetails3.clear();
            return;
        }
        historyStepDetails.userTime = (int) (this.mCurStepCpuUserTime - this.mLastStepCpuUserTime);
        historyStepDetails.systemTime = (int) (this.mCurStepCpuSystemTime - this.mLastStepCpuSystemTime);
        historyStepDetails.statUserTime = (int) (this.mCurStepStatUserTime - this.mLastStepStatUserTime);
        historyStepDetails.statSystemTime = (int) (this.mCurStepStatSystemTime - this.mLastStepStatSystemTime);
        historyStepDetails.statIOWaitTime = (int) (this.mCurStepStatIOWaitTime - this.mLastStepStatIOWaitTime);
        historyStepDetails.statIrqTime = (int) (this.mCurStepStatIrqTime - this.mLastStepStatIrqTime);
        historyStepDetails.statSoftIrqTime = (int) (this.mCurStepStatSoftIrqTime - this.mLastStepStatSoftIrqTime);
        historyStepDetails.statIdlTime = (int) (this.mCurStepStatIdleTime - this.mLastStepStatIdleTime);
        historyStepDetails.appCpuUid3 = -1;
        historyStepDetails.appCpuUid2 = -1;
        historyStepDetails.appCpuUid1 = -1;
        historyStepDetails.appCpuUTime3 = 0;
        historyStepDetails.appCpuUTime2 = 0;
        historyStepDetails.appCpuUTime1 = 0;
        historyStepDetails.appCpuSTime3 = 0;
        historyStepDetails.appCpuSTime2 = 0;
        historyStepDetails.appCpuSTime1 = 0;
        int size2 = this.mUidStats.size();
        while (i < size2) {
            Uid uidValueAt2 = this.mUidStats.valueAt(i);
            int i2 = (int) (uidValueAt2.mCurStepUserTime - uidValueAt2.mLastStepUserTime);
            int i3 = (int) (uidValueAt2.mCurStepSystemTime - uidValueAt2.mLastStepSystemTime);
            int i4 = i2 + i3;
            uidValueAt2.mLastStepUserTime = uidValueAt2.mCurStepUserTime;
            uidValueAt2.mLastStepSystemTime = uidValueAt2.mCurStepSystemTime;
            if (i4 > historyStepDetails.appCpuUTime3 + historyStepDetails.appCpuSTime3) {
                if (i4 <= historyStepDetails.appCpuUTime2 + historyStepDetails.appCpuSTime2) {
                    historyStepDetails.appCpuUid3 = uidValueAt2.mUid;
                    historyStepDetails.appCpuUTime3 = i2;
                    historyStepDetails.appCpuSTime3 = i3;
                } else {
                    historyStepDetails.appCpuUid3 = historyStepDetails.appCpuUid2;
                    historyStepDetails.appCpuUTime3 = historyStepDetails.appCpuUTime2;
                    historyStepDetails.appCpuSTime3 = historyStepDetails.appCpuSTime2;
                    if (i4 <= historyStepDetails.appCpuUTime1 + historyStepDetails.appCpuSTime1) {
                        historyStepDetails.appCpuUid2 = uidValueAt2.mUid;
                        historyStepDetails.appCpuUTime2 = i2;
                        historyStepDetails.appCpuSTime2 = i3;
                    } else {
                        historyStepDetails.appCpuUid2 = historyStepDetails.appCpuUid1;
                        historyStepDetails.appCpuUTime2 = historyStepDetails.appCpuUTime1;
                        historyStepDetails.appCpuSTime2 = historyStepDetails.appCpuSTime1;
                        historyStepDetails.appCpuUid1 = uidValueAt2.mUid;
                        historyStepDetails.appCpuUTime1 = i2;
                        historyStepDetails.appCpuSTime1 = i3;
                    }
                }
            }
            i++;
        }
        this.mLastStepCpuUserTime = this.mCurStepCpuUserTime;
        this.mLastStepCpuSystemTime = this.mCurStepCpuSystemTime;
        this.mLastStepStatUserTime = this.mCurStepStatUserTime;
        this.mLastStepStatSystemTime = this.mCurStepStatSystemTime;
        this.mLastStepStatIOWaitTime = this.mCurStepStatIOWaitTime;
        this.mLastStepStatIrqTime = this.mCurStepStatIrqTime;
        this.mLastStepStatSoftIrqTime = this.mCurStepStatSoftIrqTime;
        this.mLastStepStatIdleTime = this.mCurStepStatIdleTime;
    }

    public void readHistoryDelta(Parcel parcel, BatteryStats.HistoryItem historyItem) {
        int i;
        int i2 = parcel.readInt();
        int i3 = 524287 & i2;
        historyItem.cmd = (byte) 0;
        historyItem.numReadInts = 1;
        if (i3 < DELTA_TIME_ABS) {
            historyItem.time += (long) i3;
        } else if (i3 == DELTA_TIME_ABS) {
            historyItem.time = parcel.readLong();
            historyItem.numReadInts += 2;
            historyItem.readFromParcel(parcel);
            return;
        } else if (i3 == DELTA_TIME_INT) {
            historyItem.time += (long) parcel.readInt();
            historyItem.numReadInts++;
        } else {
            historyItem.time += parcel.readLong();
            historyItem.numReadInts += 2;
        }
        if ((524288 & i2) != 0) {
            i = parcel.readInt();
            readBatteryLevelInt(i, historyItem);
            historyItem.numReadInts++;
        } else {
            i = 0;
        }
        if ((1048576 & i2) != 0) {
            int i4 = parcel.readInt();
            historyItem.states = (16777215 & i4) | (DELTA_STATE_MASK & i2);
            historyItem.batteryStatus = (byte) ((i4 >> 29) & 7);
            historyItem.batteryHealth = (byte) ((i4 >> 26) & 7);
            historyItem.batteryPlugType = (byte) ((i4 >> 24) & 3);
            byte b = historyItem.batteryPlugType;
            if (b == 1) {
                historyItem.batteryPlugType = (byte) 1;
            } else if (b == 2) {
                historyItem.batteryPlugType = (byte) 2;
            } else if (b == 3) {
                historyItem.batteryPlugType = (byte) 4;
            }
            historyItem.numReadInts++;
        } else {
            historyItem.states = (i2 & DELTA_STATE_MASK) | (historyItem.states & 16777215);
        }
        if ((2097152 & i2) != 0) {
            historyItem.states2 = parcel.readInt();
        }
        if ((4194304 & i2) != 0) {
            int i5 = parcel.readInt();
            int i6 = i5 & 65535;
            int i7 = (i5 >> 16) & 65535;
            if (i6 != 65535) {
                historyItem.wakelockTag = historyItem.localWakelockTag;
                readHistoryTag(i6, historyItem.wakelockTag);
            } else {
                historyItem.wakelockTag = null;
            }
            if (i7 != 65535) {
                historyItem.wakeReasonTag = historyItem.localWakeReasonTag;
                readHistoryTag(i7, historyItem.wakeReasonTag);
            } else {
                historyItem.wakeReasonTag = null;
            }
            historyItem.numReadInts++;
        } else {
            historyItem.wakelockTag = null;
            historyItem.wakeReasonTag = null;
        }
        if ((8388608 & i2) != 0) {
            historyItem.eventTag = historyItem.localEventTag;
            int i8 = parcel.readInt();
            historyItem.eventCode = i8 & 65535;
            readHistoryTag((i8 >> 16) & 65535, historyItem.eventTag);
            historyItem.numReadInts++;
        } else {
            historyItem.eventCode = 0;
        }
        if ((i & 1) != 0) {
            historyItem.stepDetails = this.mReadHistoryStepDetails;
            historyItem.stepDetails.readFromParcel(parcel);
        } else {
            historyItem.stepDetails = null;
        }
        if ((i2 & 16777216) != 0) {
            historyItem.batteryChargeUAh = parcel.readInt();
        }
    }

    @Override // android.os.BatteryStats
    public void commitCurrentHistoryBatchLocked() {
        this.mHistoryLastWritten.cmd = (byte) -1;
    }

    void addHistoryBufferLocked(long j, BatteryStats.HistoryItem historyItem) {
        boolean z;
        if (this.mHaveBatteryLevel && this.mRecordingHistory) {
            long j2 = (this.mHistoryBaseTime + j) - this.mHistoryLastWritten.time;
            int i = this.mHistoryLastWritten.states ^ (historyItem.states & this.mActiveHistoryStates);
            int i2 = this.mHistoryLastWritten.states2 ^ (historyItem.states2 & this.mActiveHistoryStates2);
            int i3 = this.mHistoryLastWritten.states ^ this.mHistoryLastLastWritten.states;
            int i4 = this.mHistoryLastWritten.states2 ^ this.mHistoryLastLastWritten.states2;
            if (this.mHistoryBufferLastPos >= 0 && this.mHistoryLastWritten.cmd == 0 && j2 < 1000 && (i & i3) == 0 && (i2 & i4) == 0 && ((this.mHistoryLastWritten.wakelockTag == null || historyItem.wakelockTag == null) && ((this.mHistoryLastWritten.wakeReasonTag == null || historyItem.wakeReasonTag == null) && this.mHistoryLastWritten.stepDetails == null && ((this.mHistoryLastWritten.eventCode == 0 || historyItem.eventCode == 0) && this.mHistoryLastWritten.batteryLevel == historyItem.batteryLevel && this.mHistoryLastWritten.batteryStatus == historyItem.batteryStatus && this.mHistoryLastWritten.batteryHealth == historyItem.batteryHealth && this.mHistoryLastWritten.batteryPlugType == historyItem.batteryPlugType && this.mHistoryLastWritten.batteryTemperature == historyItem.batteryTemperature && this.mHistoryLastWritten.batteryVoltage == historyItem.batteryVoltage)))) {
                this.mHistoryBuffer.setDataSize(this.mHistoryBufferLastPos);
                this.mHistoryBuffer.setDataPosition(this.mHistoryBufferLastPos);
                this.mHistoryBufferLastPos = -1;
                j = this.mHistoryLastWritten.time - this.mHistoryBaseTime;
                if (this.mHistoryLastWritten.wakelockTag != null) {
                    historyItem.wakelockTag = historyItem.localWakelockTag;
                    historyItem.wakelockTag.setTo(this.mHistoryLastWritten.wakelockTag);
                }
                if (this.mHistoryLastWritten.wakeReasonTag != null) {
                    historyItem.wakeReasonTag = historyItem.localWakeReasonTag;
                    historyItem.wakeReasonTag.setTo(this.mHistoryLastWritten.wakeReasonTag);
                }
                if (this.mHistoryLastWritten.eventCode != 0) {
                    historyItem.eventCode = this.mHistoryLastWritten.eventCode;
                    historyItem.eventTag = historyItem.localEventTag;
                    historyItem.eventTag.setTo(this.mHistoryLastWritten.eventTag);
                }
                this.mHistoryLastWritten.setTo(this.mHistoryLastLastWritten);
            }
            int iDataSize = this.mHistoryBuffer.dataSize();
            boolean z2 = true;
            if (iDataSize >= MAX_MAX_HISTORY_BUFFER * 3) {
                resetAllStatsLocked();
            } else {
                if (iDataSize >= MAX_HISTORY_BUFFER) {
                    if (!this.mHistoryOverflow) {
                        this.mHistoryOverflow = true;
                        addHistoryBufferLocked(j, (byte) 0, historyItem);
                        addHistoryBufferLocked(j, (byte) 6, historyItem);
                        return;
                    }
                    int i5 = historyItem.states & BatteryStats.HistoryItem.SETTLE_TO_ZERO_STATES & this.mActiveHistoryStates;
                    if (this.mHistoryLastWritten.states != i5) {
                        int i6 = this.mActiveHistoryStates;
                        int i7 = (i5 | 1900543) & i6;
                        this.mActiveHistoryStates = i7;
                        z = (i6 != i7) | false;
                    } else {
                        z = false;
                    }
                    int i8 = historyItem.states2 & BatteryStats.HistoryItem.SETTLE_TO_ZERO_STATES2 & this.mActiveHistoryStates2;
                    if (this.mHistoryLastWritten.states2 != i8) {
                        int i9 = this.mActiveHistoryStates2;
                        int i10 = (i8 | (-1748959233)) & i9;
                        this.mActiveHistoryStates2 = i10;
                        z |= i9 != i10;
                    }
                    if (!z && this.mHistoryLastWritten.batteryLevel == historyItem.batteryLevel && (iDataSize >= MAX_MAX_HISTORY_BUFFER || ((this.mHistoryLastWritten.states ^ historyItem.states) & BatteryStats.HistoryItem.MOST_INTERESTING_STATES) == 0 || ((this.mHistoryLastWritten.states2 ^ historyItem.states2) & BatteryStats.HistoryItem.MOST_INTERESTING_STATES2) == 0)) {
                        return;
                    }
                    addHistoryBufferLocked(j, (byte) 0, historyItem);
                    return;
                }
                z2 = false;
            }
            if (iDataSize == 0 || z2) {
                historyItem.currentTime = System.currentTimeMillis();
                if (z2) {
                    addHistoryBufferLocked(j, (byte) 6, historyItem);
                }
                addHistoryBufferLocked(j, (byte) 7, historyItem);
            }
            addHistoryBufferLocked(j, (byte) 0, historyItem);
        }
    }

    private void addHistoryBufferLocked(long j, byte b, BatteryStats.HistoryItem historyItem) {
        if (this.mIteratingHistory) {
            throw new IllegalStateException("Can't do this while iterating history!");
        }
        this.mHistoryBufferLastPos = this.mHistoryBuffer.dataPosition();
        this.mHistoryLastLastWritten.setTo(this.mHistoryLastWritten);
        this.mHistoryLastWritten.setTo(this.mHistoryBaseTime + j, b, historyItem);
        this.mHistoryLastWritten.states &= this.mActiveHistoryStates;
        this.mHistoryLastWritten.states2 &= this.mActiveHistoryStates2;
        writeHistoryDelta(this.mHistoryBuffer, this.mHistoryLastWritten, this.mHistoryLastLastWritten);
        this.mLastHistoryElapsedRealtime = j;
        historyItem.wakelockTag = null;
        historyItem.wakeReasonTag = null;
        historyItem.eventCode = 0;
        historyItem.eventTag = null;
    }

    void addHistoryRecordLocked(long j, long j2) {
        long j3 = this.mTrackRunningHistoryElapsedRealtime;
        if (j3 != 0) {
            long j4 = j - j3;
            long j5 = j2 - this.mTrackRunningHistoryUptime;
            if (j5 < j4 - 20) {
                long j6 = j - (j4 - j5);
                this.mHistoryAddTmp.setTo(this.mHistoryLastWritten);
                this.mHistoryAddTmp.wakelockTag = null;
                this.mHistoryAddTmp.wakeReasonTag = null;
                this.mHistoryAddTmp.eventCode = 0;
                this.mHistoryAddTmp.states &= Integer.MAX_VALUE;
                addHistoryRecordInnerLocked(j6, this.mHistoryAddTmp);
            }
        }
        this.mHistoryCur.states |= Integer.MIN_VALUE;
        this.mTrackRunningHistoryElapsedRealtime = j;
        this.mTrackRunningHistoryUptime = j2;
        addHistoryRecordInnerLocked(j, this.mHistoryCur);
    }

    void addHistoryRecordInnerLocked(long j, BatteryStats.HistoryItem historyItem) {
        addHistoryBufferLocked(j, historyItem);
    }

    public void addHistoryEventLocked(long j, long j2, int i, String str, int i2) {
        this.mHistoryCur.eventCode = i;
        BatteryStats.HistoryItem historyItem = this.mHistoryCur;
        historyItem.eventTag = historyItem.localEventTag;
        this.mHistoryCur.eventTag.string = str;
        this.mHistoryCur.eventTag.uid = i2;
        addHistoryRecordLocked(j, j2);
    }

    void addHistoryRecordLocked(long j, long j2, byte b, BatteryStats.HistoryItem historyItem) {
        BatteryStats.HistoryItem historyItem2 = this.mHistoryCache;
        if (historyItem2 != null) {
            this.mHistoryCache = historyItem2.next;
        } else {
            historyItem2 = new BatteryStats.HistoryItem();
        }
        historyItem2.setTo(this.mHistoryBaseTime + j, b, historyItem);
        addHistoryRecordLocked(historyItem2);
    }

    void addHistoryRecordLocked(BatteryStats.HistoryItem historyItem) {
        this.mNumHistoryItems++;
        historyItem.next = null;
        BatteryStats.HistoryItem historyItem2 = this.mHistoryEnd;
        this.mHistoryLastEnd = historyItem2;
        if (historyItem2 != null) {
            historyItem2.next = historyItem;
            this.mHistoryEnd = historyItem;
        } else {
            this.mHistoryEnd = historyItem;
            this.mHistory = historyItem;
        }
    }

    void clearHistoryLocked() {
        this.mHistoryBaseTime = 0L;
        this.mLastHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryUptime = 0L;
        this.mHistoryBuffer.setDataSize(0);
        this.mHistoryBuffer.setDataPosition(0);
        this.mHistoryBuffer.setDataCapacity(MAX_HISTORY_BUFFER / 2);
        this.mHistoryLastLastWritten.clear();
        this.mHistoryLastWritten.clear();
        this.mHistoryTagPool.clear();
        this.mNextHistoryTagIdx = 0;
        this.mNumHistoryTagChars = 0;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mActiveHistoryStates = -1;
        this.mActiveHistoryStates2 = -1;
    }

    public void updateTimeBasesLocked(boolean z, int i, long j, long j2) {
        boolean z2 = !isScreenOn(i);
        boolean z3 = z != this.mOnBatteryTimeBase.isRunning();
        boolean z4 = (z && z2) != this.mOnBatteryScreenOffTimeBase.isRunning();
        if (z4 || z3) {
            if (z4) {
                updateKernelWakelocksLocked();
                updateBatteryPropertiesLocked();
            }
            if (z3) {
                updateRpmStatsLocked();
            }
            this.mOnBatteryTimeBase.setRunning(z, j, j2);
            if (z3) {
                for (int size = this.mUidStats.size() - 1; size >= 0; size--) {
                    this.mUidStats.valueAt(size).updateOnBatteryBgTimeBase(j, j2);
                }
            }
            if (z4) {
                this.mOnBatteryScreenOffTimeBase.setRunning(z && z2, j, j2);
                for (int size2 = this.mUidStats.size() - 1; size2 >= 0; size2--) {
                    this.mUidStats.valueAt(size2).updateOnBatteryScreenOffBgTimeBase(j, j2);
                }
            }
        }
    }

    private void updateBatteryPropertiesLocked() {
        try {
            IBatteryPropertiesRegistrar.Stub.asInterface(ServiceManager.getService("batteryproperties")).scheduleUpdate();
        } catch (RemoteException unused) {
        }
    }

    public void addIsolatedUidLocked(int i, int i2) {
        this.mIsolatedUids.put(i, i2);
        StatsLog.write(43, i2, i, 1);
        getUidStatsLocked(i2).addIsolatedUid(i);
    }

    public void scheduleRemoveIsolatedUidLocked(int i, int i2) {
        ExternalStatsSync externalStatsSync;
        if (this.mIsolatedUids.get(i, -1) != i2 || (externalStatsSync = this.mExternalSync) == null) {
            return;
        }
        externalStatsSync.scheduleCpuSyncDueToRemovedUid(i);
    }

    public void removeIsolatedUidLocked(int i) {
        StatsLog.write(43, this.mIsolatedUids.get(i, -1), i, 0);
        int iIndexOfKey = this.mIsolatedUids.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            getUidStatsLocked(this.mIsolatedUids.valueAt(iIndexOfKey)).removeIsolatedUid(i);
            this.mIsolatedUids.removeAt(iIndexOfKey);
        }
        this.mPendingRemovedUids.add(new UidToRemove(this, i, this.mClocks.elapsedRealtime()));
    }

    public int mapUid(int i) {
        int i2 = this.mIsolatedUids.get(i, -1);
        return i2 > 0 ? i2 : i;
    }

    public void noteEventLocked(int i, String str, int i2) {
        int iMapUid = mapUid(i2);
        if (this.mActiveEvents.updateState(i, str, iMapUid, 0)) {
            addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), i, str, iMapUid);
        }
    }

    boolean ensureStartClockTime(long j) {
        if ((j <= 31536000000L || this.mStartClockTime >= j - 31536000000L) && this.mStartClockTime <= j) {
            return false;
        }
        this.mStartClockTime = j - (this.mClocks.elapsedRealtime() - (this.mRealtimeStart / 1000));
        return true;
    }

    public void noteCurrentTimeChangedLocked() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        recordCurrentTimeChangeLocked(jCurrentTimeMillis, this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis());
        ensureStartClockTime(jCurrentTimeMillis);
    }

    public void noteProcessStartLocked(String str, int i) {
        int iMapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(iMapUid).getProcessStatsLocked(str).incStartsLocked();
        }
        if (this.mActiveEvents.updateState(32769, str, iMapUid, 0) && this.mRecordAllHistory) {
            addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 32769, str, iMapUid);
        }
    }

    public void noteProcessCrashLocked(String str, int i) {
        int iMapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(iMapUid).getProcessStatsLocked(str).incNumCrashesLocked();
        }
    }

    public void noteProcessAnrLocked(String str, int i) {
        int iMapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(iMapUid).getProcessStatsLocked(str).incNumAnrsLocked();
        }
    }

    public void noteUidProcessStateLocked(int i, int i2) {
        if (i != mapUid(i)) {
            return;
        }
        getUidStatsLocked(i).updateUidProcessStateLocked(i2);
    }

    public void noteProcessFinishLocked(String str, int i) {
        int iMapUid = mapUid(i);
        if (this.mActiveEvents.updateState(16385, str, iMapUid, 0) && this.mRecordAllHistory) {
            addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 16385, str, iMapUid);
        }
    }

    public void noteSyncStartLocked(String str, int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        getUidStatsLocked(iMapUid).noteStartSyncLocked(str, jElapsedRealtime);
        if (this.mActiveEvents.updateState(32772, str, iMapUid, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 32772, str, iMapUid);
        }
    }

    public void noteSyncFinishLocked(String str, int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        getUidStatsLocked(iMapUid).noteStopSyncLocked(str, jElapsedRealtime);
        if (this.mActiveEvents.updateState(16388, str, iMapUid, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 16388, str, iMapUid);
        }
    }

    public void noteJobStartLocked(String str, int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        getUidStatsLocked(iMapUid).noteStartJobLocked(str, jElapsedRealtime);
        if (this.mActiveEvents.updateState(32774, str, iMapUid, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 32774, str, iMapUid);
        }
    }

    public void noteJobFinishLocked(String str, int i, int i2) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        getUidStatsLocked(iMapUid).noteStopJobLocked(str, jElapsedRealtime, i2);
        if (this.mActiveEvents.updateState(16390, str, iMapUid, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 16390, str, iMapUid);
        }
    }

    public void noteJobsDeferredLocked(int i, int i2, long j) {
        getUidStatsLocked(mapUid(i)).noteJobsDeferredLocked(i2, j);
    }

    public void noteAlarmStartLocked(String str, WorkSource workSource, int i) {
        noteAlarmStartOrFinishLocked(BatteryStats.HistoryItem.EVENT_ALARM_START, str, workSource, i);
    }

    public void noteAlarmFinishLocked(String str, WorkSource workSource, int i) {
        noteAlarmStartOrFinishLocked(16397, str, workSource, i);
    }

    private void noteAlarmStartOrFinishLocked(int i, String str, WorkSource workSource, int i2) {
        WorkSource workSource2 = workSource;
        if (this.mRecordAllHistory) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            int i3 = 0;
            if (workSource2 != null) {
                int i4 = 0;
                while (i4 < workSource.size()) {
                    int iMapUid = mapUid(workSource2.get(i4));
                    if (this.mActiveEvents.updateState(i, str, iMapUid, i3)) {
                        addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, i, str, iMapUid);
                    }
                    i4++;
                    i3 = i3;
                    workSource2 = workSource;
                }
                int i5 = i3;
                ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
                if (workChains != null) {
                    int i6 = i5;
                    while (i6 < workChains.size()) {
                        int iMapUid2 = mapUid(workChains.get(i6).getAttributionUid());
                        if (this.mActiveEvents.updateState(i, str, iMapUid2, i5)) {
                            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, i, str, iMapUid2);
                        }
                        i6++;
                        workChains = workChains;
                    }
                    return;
                }
                return;
            }
            int iMapUid3 = mapUid(i2);
            if (this.mActiveEvents.updateState(i, str, iMapUid3, 0)) {
                addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, i, str, iMapUid3);
            }
        }
    }

    public void noteWakupAlarmLocked(String str, int i, WorkSource workSource, String str2) {
        if (workSource != null) {
            for (int i2 = 0; i2 < workSource.size(); i2++) {
                int i3 = workSource.get(i2);
                String name = workSource.getName(i2);
                if (isOnBattery()) {
                    if (name == null) {
                        name = str;
                    }
                    getPackageStatsLocked(i3, name).noteWakeupAlarmLocked(str2);
                }
                StatsLog.write_non_chained(35, workSource.get(i2), workSource.getName(i2), str2);
            }
            ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i4 = 0; i4 < workChains.size(); i4++) {
                    WorkSource.WorkChain workChain = workChains.get(i4);
                    int attributionUid = workChain.getAttributionUid();
                    if (isOnBattery()) {
                        getPackageStatsLocked(attributionUid, str).noteWakeupAlarmLocked(str2);
                    }
                    StatsLog.write(35, workChain.getUids(), workChain.getTags(), str2);
                }
                return;
            }
            return;
        }
        if (isOnBattery()) {
            getPackageStatsLocked(i, str).noteWakeupAlarmLocked(str2);
        }
        StatsLog.write_non_chained(35, i, (String) null, str2);
    }

    private void requestWakelockCpuUpdate() {
        this.mExternalSync.scheduleCpuSyncDueToWakelockChange(5000L);
    }

    private void requestImmediateCpuUpdate() {
        this.mExternalSync.scheduleCpuSyncDueToWakelockChange(0L);
    }

    public void setRecordAllHistoryLocked(boolean z) {
        this.mRecordAllHistory = z;
        if (!z) {
            this.mActiveEvents.removeEvents(5);
            this.mActiveEvents.removeEvents(13);
            HashMap<String, SparseIntArray> stateForEvent = this.mActiveEvents.getStateForEvent(1);
            if (stateForEvent != null) {
                long jElapsedRealtime = this.mClocks.elapsedRealtime();
                long jUptimeMillis = this.mClocks.uptimeMillis();
                for (Map.Entry<String, SparseIntArray> entry : stateForEvent.entrySet()) {
                    int i = 0;
                    for (SparseIntArray value = entry.getValue(); i < value.size(); value = value) {
                        addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 16385, entry.getKey(), value.keyAt(i));
                        i++;
                    }
                }
                return;
            }
            return;
        }
        HashMap<String, SparseIntArray> stateForEvent2 = this.mActiveEvents.getStateForEvent(1);
        if (stateForEvent2 != null) {
            long jElapsedRealtime2 = this.mClocks.elapsedRealtime();
            long jUptimeMillis2 = this.mClocks.uptimeMillis();
            for (Map.Entry<String, SparseIntArray> entry2 : stateForEvent2.entrySet()) {
                int i2 = 0;
                for (SparseIntArray value2 = entry2.getValue(); i2 < value2.size(); value2 = value2) {
                    addHistoryEventLocked(jElapsedRealtime2, jUptimeMillis2, 32769, entry2.getKey(), value2.keyAt(i2));
                    i2++;
                }
            }
        }
    }

    public void setNoAutoReset(boolean z) {
        this.mNoAutoReset = z;
    }

    public void setPretendScreenOff(boolean z) {
        if (this.mPretendScreenOff != z) {
            this.mPretendScreenOff = z;
            noteScreenStateLocked(z ? 1 : 2);
        }
    }

    public void noteStartWakeLocked(int i, int i2, WorkSource.WorkChain workChain, String str, String str2, int i3, boolean z, long j, long j2) {
        int iMapUid = mapUid(i);
        if (i3 == 0) {
            aggregateLastWakeupUptimeLocked(j2);
            String str3 = str2 == null ? str : str2;
            if (this.mRecordAllHistory && this.mActiveEvents.updateState(32773, str3, iMapUid, 0)) {
                addHistoryEventLocked(j, j2, 32773, str3, iMapUid);
            }
            if (this.mWakeLockNesting == 0) {
                this.mHistoryCur.states |= 1073741824;
                BatteryStats.HistoryItem historyItem = this.mHistoryCur;
                historyItem.wakelockTag = historyItem.localWakelockTag;
                BatteryStats.HistoryTag historyTag = this.mHistoryCur.wakelockTag;
                this.mInitialAcquireWakeName = str3;
                historyTag.string = str3;
                BatteryStats.HistoryTag historyTag2 = this.mHistoryCur.wakelockTag;
                this.mInitialAcquireWakeUid = iMapUid;
                historyTag2.uid = iMapUid;
                this.mWakeLockImportant = !z;
                addHistoryRecordLocked(j, j2);
            } else if (!this.mWakeLockImportant && !z && this.mHistoryLastWritten.cmd == 0) {
                if (this.mHistoryLastWritten.wakelockTag != null) {
                    this.mHistoryLastWritten.wakelockTag = null;
                    BatteryStats.HistoryItem historyItem2 = this.mHistoryCur;
                    historyItem2.wakelockTag = historyItem2.localWakelockTag;
                    BatteryStats.HistoryTag historyTag3 = this.mHistoryCur.wakelockTag;
                    this.mInitialAcquireWakeName = str3;
                    historyTag3.string = str3;
                    BatteryStats.HistoryTag historyTag4 = this.mHistoryCur.wakelockTag;
                    this.mInitialAcquireWakeUid = iMapUid;
                    historyTag4.uid = iMapUid;
                    addHistoryRecordLocked(j, j2);
                }
                this.mWakeLockImportant = true;
            }
            this.mWakeLockNesting++;
        }
        if (iMapUid >= 0) {
            if (this.mOnBatteryScreenOffTimeBase.isRunning()) {
                requestWakelockCpuUpdate();
            }
            getUidStatsLocked(iMapUid).noteStartWakeLocked(i2, str, i3, j);
            if (workChain != null) {
                StatsLog.write(10, workChain.getUids(), workChain.getTags(), getPowerManagerWakeLockLevel(i3), str, 1);
            } else {
                StatsLog.write_non_chained(10, iMapUid, (String) null, getPowerManagerWakeLockLevel(i3), str, 1);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:15:0x004b  */
    public void noteStopWakeLocked(int i, int i2, WorkSource.WorkChain workChain, String str, String str2, int i3, long j, long j2) {
        int iMapUid = mapUid(i);
        if (i3 == 0) {
            this.mWakeLockNesting--;
            if (this.mRecordAllHistory) {
                String str3 = str2 == null ? str : str2;
                if (this.mActiveEvents.updateState(16389, str3, iMapUid, 0)) {
                    addHistoryEventLocked(j, j2, 16389, str3, iMapUid);
                }
            }
            if (this.mWakeLockNesting == 0) {
                this.mHistoryCur.states &= -1073741825;
                this.mInitialAcquireWakeName = null;
                this.mInitialAcquireWakeUid = -1;
                addHistoryRecordLocked(j, j2);
            }
        }
        if (iMapUid >= 0) {
            if (this.mOnBatteryScreenOffTimeBase.isRunning()) {
                requestWakelockCpuUpdate();
            }
            getUidStatsLocked(iMapUid).noteStopWakeLocked(i2, str, i3, j);
            if (workChain != null) {
                StatsLog.write(10, workChain.getUids(), workChain.getTags(), getPowerManagerWakeLockLevel(i3), str, 0);
            } else {
                StatsLog.write_non_chained(10, iMapUid, (String) null, getPowerManagerWakeLockLevel(i3), str, 0);
            }
        }
    }

    private int getPowerManagerWakeLockLevel(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1) {
            return 26;
        }
        if (i == 2) {
            Slog.e(TAG, "Illegal window wakelock type observed in batterystats.");
            return -1;
        }
        if (i == 18) {
            return 128;
        }
        Slog.e(TAG, "Illegal wakelock type in batterystats: " + i);
        return -1;
    }

    public void noteStartWakeFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2, boolean z) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i3 = 0;
        for (int size = workSource.size(); i3 < size; size = size) {
            noteStartWakeLocked(workSource.get(i3), i, null, str, str2, i2, z, jElapsedRealtime, jUptimeMillis);
            i3++;
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            int i4 = 0;
            while (i4 < workChains.size()) {
                WorkSource.WorkChain workChain = workChains.get(i4);
                noteStartWakeLocked(workChain.getAttributionUid(), i, workChain, str, str2, i2, z, jElapsedRealtime, jUptimeMillis);
                i4++;
                workChains = workChains;
            }
        }
    }

    public void noteChangeWakelockFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2, WorkSource workSource2, int i3, String str3, String str4, int i4, boolean z) {
        ArrayList<WorkSource.WorkChain> arrayList;
        ArrayList<WorkSource.WorkChain> arrayList2;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        ArrayList<WorkSource.WorkChain>[] arrayListArrDiffChains = WorkSource.diffChains(workSource, workSource2);
        int i5 = 0;
        for (int size = workSource2.size(); i5 < size; size = size) {
            noteStartWakeLocked(workSource2.get(i5), i3, null, str3, str4, i4, z, jElapsedRealtime, jUptimeMillis);
            i5++;
        }
        if (arrayListArrDiffChains != null && (arrayList2 = arrayListArrDiffChains[0]) != null) {
            for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                WorkSource.WorkChain workChain = arrayList2.get(i6);
                noteStartWakeLocked(workChain.getAttributionUid(), i3, workChain, str3, str4, i4, z, jElapsedRealtime, jUptimeMillis);
            }
        }
        int size2 = workSource.size();
        for (int i7 = 0; i7 < size2; i7++) {
            noteStopWakeLocked(workSource.get(i7), i, null, str, str2, i2, jElapsedRealtime, jUptimeMillis);
        }
        if (arrayListArrDiffChains == null || (arrayList = arrayListArrDiffChains[1]) == null) {
            return;
        }
        for (int i8 = 0; i8 < arrayList.size(); i8++) {
            WorkSource.WorkChain workChain2 = arrayList.get(i8);
            noteStopWakeLocked(workChain2.getAttributionUid(), i, workChain2, str, str2, i2, jElapsedRealtime, jUptimeMillis);
        }
    }

    public void noteStopWakeFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i3 = 0;
        for (int size = workSource.size(); i3 < size; size = size) {
            noteStopWakeLocked(workSource.get(i3), i, null, str, str2, i2, jElapsedRealtime, jUptimeMillis);
            i3++;
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            int i4 = 0;
            while (i4 < workChains.size()) {
                WorkSource.WorkChain workChain = workChains.get(i4);
                noteStopWakeLocked(workChain.getAttributionUid(), i, workChain, str, str2, i2, jElapsedRealtime, jUptimeMillis);
                i4++;
                workChains = workChains;
            }
        }
    }

    public void noteLongPartialWakelockStart(String str, String str2, int i) {
        StatsLog.write_non_chained(11, i, (String) null, str, str2, 1);
        noteLongPartialWakeLockStartInternal(str, str2, mapUid(i));
    }

    public void noteLongPartialWakelockStartFromSource(String str, String str2, WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteLongPartialWakeLockStartInternal(str, str2, mapUid(workSource.get(i)));
            StatsLog.write_non_chained(11, workSource.get(i), workSource.getName(i), str, str2, 1);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteLongPartialWakeLockStartInternal(str, str2, workChain.getAttributionUid());
                StatsLog.write(11, workChain.getUids(), workChain.getTags(), str, str2, 1);
            }
        }
    }

    private void noteLongPartialWakeLockStartInternal(String str, String str2, int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        String str3 = str2 == null ? str : str2;
        if (this.mActiveEvents.updateState(BatteryStats.HistoryItem.EVENT_LONG_WAKE_LOCK_START, str3, i, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, BatteryStats.HistoryItem.EVENT_LONG_WAKE_LOCK_START, str3, i);
        }
    }

    public void noteLongPartialWakelockFinish(String str, String str2, int i) {
        StatsLog.write_non_chained(11, i, (String) null, str, str2, 0);
        noteLongPartialWakeLockFinishInternal(str, str2, mapUid(i));
    }

    public void noteLongPartialWakelockFinishFromSource(String str, String str2, WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteLongPartialWakeLockFinishInternal(str, str2, mapUid(workSource.get(i)));
            StatsLog.write_non_chained(11, workSource.get(i), workSource.getName(i), str, str2, 0);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteLongPartialWakeLockFinishInternal(str, str2, workChain.getAttributionUid());
                StatsLog.write(11, workChain.getUids(), workChain.getTags(), str, str2, 0);
            }
        }
    }

    private void noteLongPartialWakeLockFinishInternal(String str, String str2, int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        String str3 = str2 == null ? str : str2;
        if (this.mActiveEvents.updateState(BatteryStats.HistoryItem.EVENT_LONG_WAKE_LOCK_FINISH, str3, i, 0)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, BatteryStats.HistoryItem.EVENT_LONG_WAKE_LOCK_FINISH, str3, i);
        }
    }

    void aggregateLastWakeupUptimeLocked(long j) {
        String str = this.mLastWakeupReason;
        if (str != null) {
            long j2 = (j - this.mLastWakeupUptimeMs) * 1000;
            getWakeupReasonTimerLocked(str).add(j2, 1);
            StatsLog.write(36, this.mLastWakeupReason, j2);
            this.mLastWakeupReason = null;
        }
    }

    public void noteWakeupReasonLocked(String str) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        aggregateLastWakeupUptimeLocked(jUptimeMillis);
        BatteryStats.HistoryItem historyItem = this.mHistoryCur;
        historyItem.wakeReasonTag = historyItem.localWakeReasonTag;
        this.mHistoryCur.wakeReasonTag.string = str;
        this.mHistoryCur.wakeReasonTag.uid = 0;
        this.mLastWakeupReason = str;
        this.mLastWakeupUptimeMs = jUptimeMillis;
        addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
    }

    public boolean startAddingCpuLocked() {
        this.mExternalSync.cancelCpuSyncDueToWakelockChange();
        return this.mOnBatteryInternal;
    }

    public void finishAddingCpuLocked(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mCurStepCpuUserTime += (long) i;
        this.mCurStepCpuSystemTime += (long) i2;
        this.mCurStepStatUserTime += (long) i3;
        this.mCurStepStatSystemTime += (long) i4;
        this.mCurStepStatIOWaitTime += (long) i5;
        this.mCurStepStatIrqTime += (long) i6;
        this.mCurStepStatSoftIrqTime += (long) i7;
        this.mCurStepStatIdleTime += (long) i8;
    }

    public void noteProcessDiedLocked(int i, int i2) {
        Uid uid = this.mUidStats.get(mapUid(i));
        if (uid != null) {
            uid.mPids.remove(i2);
        }
    }

    public long getProcessWakeTime(int i, int i2, long j) {
        BatteryStats.Uid.Pid pid;
        Uid uid = this.mUidStats.get(mapUid(i));
        if (uid == null || (pid = uid.mPids.get(i2)) == null) {
            return 0L;
        }
        return pid.mWakeSumMs + (pid.mWakeNesting > 0 ? j - pid.mWakeStartMs : 0L);
    }

    public void reportExcessiveCpuLocked(int i, String str, long j, long j2) {
        Uid uid = this.mUidStats.get(mapUid(i));
        if (uid != null) {
            uid.reportExcessiveCpuLocked(str, j, j2);
        }
    }

    public void noteStartSensorLocked(int i, int i2) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mSensorNesting == 0) {
            this.mHistoryCur.states |= 8388608;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        this.mSensorNesting++;
        getUidStatsLocked(iMapUid).noteStartSensor(i2, jElapsedRealtime);
    }

    public void noteStopSensorLocked(int i, int i2) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i3 = this.mSensorNesting - 1;
        this.mSensorNesting = i3;
        if (i3 == 0) {
            this.mHistoryCur.states &= -8388609;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        getUidStatsLocked(iMapUid).noteStopSensor(i2, jElapsedRealtime);
    }

    public void noteGpsChangedLocked(WorkSource workSource, WorkSource workSource2) {
        for (int i = 0; i < workSource2.size(); i++) {
            noteStartGpsLocked(workSource2.get(i), null);
        }
        for (int i2 = 0; i2 < workSource.size(); i2++) {
            noteStopGpsLocked(workSource.get(i2), null);
        }
        ArrayList<WorkSource.WorkChain>[] arrayListArrDiffChains = WorkSource.diffChains(workSource, workSource2);
        if (arrayListArrDiffChains != null) {
            if (arrayListArrDiffChains[0] != null) {
                ArrayList<WorkSource.WorkChain> arrayList = arrayListArrDiffChains[0];
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    noteStartGpsLocked(-1, arrayList.get(i3));
                }
            }
            if (arrayListArrDiffChains[1] != null) {
                ArrayList<WorkSource.WorkChain> arrayList2 = arrayListArrDiffChains[1];
                for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                    noteStopGpsLocked(-1, arrayList2.get(i4));
                }
            }
        }
    }

    private void noteStartGpsLocked(int i, WorkSource.WorkChain workChain) {
        int attributionUid = getAttributionUid(i, workChain);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mGpsNesting == 0) {
            this.mHistoryCur.states |= 536870912;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        this.mGpsNesting++;
        if (workChain == null) {
            StatsLog.write_non_chained(6, attributionUid, (String) null, 1);
        } else {
            StatsLog.write(6, workChain.getUids(), workChain.getTags(), 1);
        }
        getUidStatsLocked(attributionUid).noteStartGps(jElapsedRealtime);
    }

    private void noteStopGpsLocked(int i, WorkSource.WorkChain workChain) {
        int attributionUid = getAttributionUid(i, workChain);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mGpsNesting - 1;
        this.mGpsNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states &= -536870913;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            stopAllGpsSignalQualityTimersLocked(-1);
            this.mGpsSignalQualityBin = -1;
        }
        if (workChain == null) {
            StatsLog.write_non_chained(6, attributionUid, (String) null, 0);
        } else {
            StatsLog.write(6, workChain.getUids(), workChain.getTags(), 0);
        }
        getUidStatsLocked(attributionUid).noteStopGps(jElapsedRealtime);
    }

    public void noteGpsSignalQualityLocked(int i) {
        if (this.mGpsNesting == 0) {
            return;
        }
        if (i < 0 || i >= 2) {
            stopAllGpsSignalQualityTimersLocked(-1);
            return;
        }
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mGpsSignalQualityBin;
        if (i2 != i) {
            if (i2 >= 0) {
                this.mGpsSignalQualityTimer[i2].stopRunningLocked(jElapsedRealtime);
            }
            if (!this.mGpsSignalQualityTimer[i].isRunningLocked()) {
                this.mGpsSignalQualityTimer[i].startRunningLocked(jElapsedRealtime);
            }
            BatteryStats.HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 = (historyItem.states2 & (-129)) | (i << 7);
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mGpsSignalQualityBin = i;
        }
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:31:0x00b2  */
    /* JADX WARN: Code duplicated, block: B:32:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:34:0x00c0  */
    /* JADX WARN: Code duplicated, block: B:36:0x00d3  */
    /* JADX WARN: Code duplicated, block: B:37:0x00db  */
    /* JADX WARN: Code duplicated, block: B:39:0x00de  */
    /* JADX WARN: Code duplicated, block: B:42:0x00fa  */
    /* JADX WARN: Code duplicated, block: B:43:0x0123  */
    /* JADX WARN: Code duplicated, block: B:45:0x012b  */
    /* JADX WARN: Code duplicated, block: B:48:0x0159  */
    /* JADX WARN: Code duplicated, block: B:51:? A[RETURN, SYNTHETIC] */
    public void noteScreenStateLocked(int i) {
        int i2;
        int i3;
        boolean z = true;
        int i4 = this.mPretendScreenOff ? 1 : i;
        if (i4 > 4) {
            if (i4 != 5) {
                Slog.wtf(TAG, "Unknown screen state (not mapped): " + i4);
            } else {
                i4 = 2;
            }
        }
        int i5 = i4;
        if (this.mScreenState != i5) {
            recordDailyStatsIfNeededLocked(true);
            int i6 = this.mScreenState;
            this.mScreenState = i5;
            if (i5 != 0) {
                int i7 = i5 - 1;
                if ((i7 & 3) == i7) {
                    int i8 = this.mModStepMode;
                    int i9 = this.mCurStepMode;
                    this.mModStepMode = i8 | ((i9 & 3) ^ i7);
                    this.mCurStepMode = i7 | (i9 & (-4));
                } else {
                    Slog.wtf(TAG, "Unexpected screen state: " + i5);
                }
            }
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            boolean z2 = false;
            if (isScreenDoze(i5)) {
                this.mHistoryCur.states |= 262144;
                this.mScreenDozeTimer.startRunningLocked(jElapsedRealtime);
            } else {
                if (isScreenDoze(i6)) {
                    this.mHistoryCur.states &= -262145;
                    this.mScreenDozeTimer.stopRunningLocked(jElapsedRealtime);
                }
                if (isScreenOn(i5)) {
                    this.mHistoryCur.states |= 1048576;
                    this.mScreenOnTimer.startRunningLocked(jElapsedRealtime);
                    i3 = this.mScreenBrightnessBin;
                    if (i3 >= 0) {
                        this.mScreenBrightnessTimer[i3].startRunningLocked(jElapsedRealtime);
                    }
                } else if (isScreenOn(i6)) {
                    this.mHistoryCur.states &= -1048577;
                    this.mScreenOnTimer.stopRunningLocked(jElapsedRealtime);
                    i2 = this.mScreenBrightnessBin;
                    if (i2 >= 0) {
                        this.mScreenBrightnessTimer[i2].stopRunningLocked(jElapsedRealtime);
                    }
                } else {
                    z = z2;
                }
                if (z) {
                    addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
                }
                this.mExternalSync.scheduleCpuSyncDueToScreenStateChange(this.mOnBatteryTimeBase.isRunning(), this.mOnBatteryScreenOffTimeBase.isRunning());
                if (isScreenOn(i5)) {
                    updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i5, this.mClocks.uptimeMillis() * 1000, jElapsedRealtime * 1000);
                    noteStartWakeLocked(-1, -1, null, "screen", null, 0, false, jElapsedRealtime, jUptimeMillis);
                } else if (isScreenOn(i6)) {
                    noteStopWakeLocked(-1, -1, null, "screen", "screen", 0, jElapsedRealtime, jUptimeMillis);
                    updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i5, this.mClocks.uptimeMillis() * 1000, jElapsedRealtime * 1000);
                }
                if (this.mOnBatteryInternal) {
                    updateDischargeScreenLevelsLocked(i6, i5);
                }
            }
            z2 = true;
            if (isScreenOn(i5)) {
                this.mHistoryCur.states |= 1048576;
                this.mScreenOnTimer.startRunningLocked(jElapsedRealtime);
                i3 = this.mScreenBrightnessBin;
                if (i3 >= 0) {
                    this.mScreenBrightnessTimer[i3].startRunningLocked(jElapsedRealtime);
                }
            } else if (isScreenOn(i6)) {
                this.mHistoryCur.states &= -1048577;
                this.mScreenOnTimer.stopRunningLocked(jElapsedRealtime);
                i2 = this.mScreenBrightnessBin;
                if (i2 >= 0) {
                    this.mScreenBrightnessTimer[i2].stopRunningLocked(jElapsedRealtime);
                }
            } else {
                z = z2;
            }
            if (z) {
                addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            }
            this.mExternalSync.scheduleCpuSyncDueToScreenStateChange(this.mOnBatteryTimeBase.isRunning(), this.mOnBatteryScreenOffTimeBase.isRunning());
            if (isScreenOn(i5)) {
                updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i5, this.mClocks.uptimeMillis() * 1000, jElapsedRealtime * 1000);
                noteStartWakeLocked(-1, -1, null, "screen", null, 0, false, jElapsedRealtime, jUptimeMillis);
            } else if (isScreenOn(i6)) {
                noteStopWakeLocked(-1, -1, null, "screen", "screen", 0, jElapsedRealtime, jUptimeMillis);
                updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i5, this.mClocks.uptimeMillis() * 1000, jElapsedRealtime * 1000);
            }
            if (this.mOnBatteryInternal) {
                updateDischargeScreenLevelsLocked(i6, i5);
            }
        }
    }

    public void noteScreenBrightnessLocked(int i) {
        int i2 = i / 51;
        if (i2 < 0) {
            i2 = 0;
        } else if (i2 >= 5) {
            i2 = 4;
        }
        if (this.mScreenBrightnessBin != i2) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            BatteryStats.HistoryItem historyItem = this.mHistoryCur;
            historyItem.states = (historyItem.states & (-8)) | (i2 << 0);
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            if (this.mScreenState == 2) {
                int i3 = this.mScreenBrightnessBin;
                if (i3 >= 0) {
                    this.mScreenBrightnessTimer[i3].stopRunningLocked(jElapsedRealtime);
                }
                this.mScreenBrightnessTimer[i2].startRunningLocked(jElapsedRealtime);
            }
            this.mScreenBrightnessBin = i2;
        }
    }

    public void noteUserActivityLocked(int i, int i2) {
        if (this.mOnBatteryInternal) {
            getUidStatsLocked(mapUid(i)).noteUserActivityLocked(i2);
        }
    }

    public void noteWakeUpLocked(String str, int i) {
        addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 18, str, i);
    }

    public void noteInteractiveLocked(boolean z) {
        if (this.mInteractive != z) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            this.mInteractive = z;
            if (z) {
                this.mInteractiveTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mInteractiveTimer.stopRunningLocked(jElapsedRealtime);
            }
        }
    }

    public void noteConnectivityChangedLocked(int i, String str) {
        addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 9, str, i);
        this.mNumConnectivityChange++;
    }

    private void noteMobileRadioApWakeupLocked(long j, long j2, int i) {
        int iMapUid = mapUid(i);
        addHistoryEventLocked(j, j2, 19, "", iMapUid);
        getUidStatsLocked(iMapUid).noteMobileRadioApWakeupLocked();
    }

    public boolean noteMobileRadioPowerStateLocked(int i, long j, int i2) {
        long j2;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mMobileRadioPowerState != i) {
            boolean z = i == 2 || i == 3;
            if (z) {
                if (i2 > 0) {
                    noteMobileRadioApWakeupLocked(jElapsedRealtime, jUptimeMillis, i2);
                }
                j2 = j / TimeUtils.NANOS_PER_MS;
                this.mMobileRadioActiveStartTime = j2;
                this.mHistoryCur.states |= 33554432;
            } else {
                j2 = j / TimeUtils.NANOS_PER_MS;
                long j3 = this.mMobileRadioActiveStartTime;
                if (j2 < j3) {
                    Slog.wtf(TAG, "Data connection inactive timestamp " + j2 + " is before start time " + j3);
                    j2 = jElapsedRealtime;
                } else if (j2 < jElapsedRealtime) {
                    this.mMobileRadioActiveAdjustedTime.addCountLocked(jElapsedRealtime - j2);
                }
                this.mHistoryCur.states &= -33554433;
            }
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mMobileRadioPowerState = i;
            StatsLog.write_non_chained(12, i2, (String) null, i);
            if (z) {
                this.mMobileRadioActiveTimer.startRunningLocked(jElapsedRealtime);
                this.mMobileRadioActivePerAppTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mMobileRadioActiveTimer.stopRunningLocked(j2);
                this.mMobileRadioActivePerAppTimer.stopRunningLocked(j2);
                return true;
            }
        }
        return false;
    }

    public void notePowerSaveModeLocked(boolean z) {
        if (this.mPowerSaveModeEnabled != z) {
            int i = z ? 4 : 0;
            int i2 = this.mModStepMode;
            int i3 = this.mCurStepMode;
            this.mModStepMode = i2 | ((i3 & 4) ^ i);
            this.mCurStepMode = i | (i3 & (-5));
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mPowerSaveModeEnabled = z;
            if (z) {
                this.mHistoryCur.states2 |= Integer.MIN_VALUE;
                this.mPowerSaveModeEnabledTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mHistoryCur.states2 &= Integer.MAX_VALUE;
                this.mPowerSaveModeEnabledTimer.stopRunningLocked(jElapsedRealtime);
            }
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            StatsLog.write(20, z ? 1 : 0);
        }
    }

    public void noteDeviceIdleModeLocked(int i, String str, int i2) {
        int i3;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        boolean z = i == 2;
        boolean z2 = (this.mDeviceIdling && !z && str == null) ? true : z;
        boolean z3 = i == 1;
        boolean z4 = (!this.mDeviceLightIdling || z3 || z2 || str != null) ? z3 : true;
        if (str != null && (this.mDeviceIdling || this.mDeviceLightIdling)) {
            addHistoryEventLocked(jElapsedRealtime, jUptimeMillis, 10, str, i2);
        }
        if (this.mDeviceIdling != z2 || this.mDeviceLightIdling != z4) {
            if (z2 != 0) {
                i3 = 2;
            } else {
                i3 = z4 != 0 ? 1 : 0;
            }
            StatsLog.write(22, i3);
        }
        if (this.mDeviceIdling != z2) {
            this.mDeviceIdling = z2;
            int i4 = z2 != 0 ? 8 : 0;
            int i5 = this.mModStepMode;
            int i6 = this.mCurStepMode;
            this.mModStepMode = i5 | ((i6 & 8) ^ i4);
            this.mCurStepMode = i4 | (i6 & (-9));
            if (z2) {
                this.mDeviceIdlingTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mDeviceIdlingTimer.stopRunningLocked(jElapsedRealtime);
            }
        }
        if (this.mDeviceLightIdling != z4) {
            this.mDeviceLightIdling = z4;
            if (z4 != 0) {
                this.mDeviceLightIdlingTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mDeviceLightIdlingTimer.stopRunningLocked(jElapsedRealtime);
            }
        }
        if (this.mDeviceIdleMode != i) {
            BatteryStats.HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 = (historyItem.states2 & (-100663297)) | (i << 25);
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            long j = jElapsedRealtime - this.mLastIdleTimeStart;
            this.mLastIdleTimeStart = jElapsedRealtime;
            int i7 = this.mDeviceIdleMode;
            if (i7 == 1) {
                if (j > this.mLongestLightIdleTime) {
                    this.mLongestLightIdleTime = j;
                }
                this.mDeviceIdleModeLightTimer.stopRunningLocked(jElapsedRealtime);
            } else if (i7 == 2) {
                if (j > this.mLongestFullIdleTime) {
                    this.mLongestFullIdleTime = j;
                }
                this.mDeviceIdleModeFullTimer.stopRunningLocked(jElapsedRealtime);
            }
            if (i == 1) {
                this.mDeviceIdleModeLightTimer.startRunningLocked(jElapsedRealtime);
            } else if (i == 2) {
                this.mDeviceIdleModeFullTimer.startRunningLocked(jElapsedRealtime);
            }
            this.mDeviceIdleMode = i;
            StatsLog.write(21, i);
        }
    }

    public void notePackageInstalledLocked(String str, long j) {
        addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 11, str, (int) j);
        BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
        packageChange.mPackageName = str;
        packageChange.mUpdate = true;
        packageChange.mVersionCode = j;
        addPackageChange(packageChange);
    }

    public void notePackageUninstalledLocked(String str) {
        addHistoryEventLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis(), 12, str, 0);
        BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
        packageChange.mPackageName = str;
        packageChange.mUpdate = true;
        addPackageChange(packageChange);
    }

    private void addPackageChange(BatteryStats.PackageChange packageChange) {
        if (this.mDailyPackageChanges == null) {
            this.mDailyPackageChanges = new ArrayList<>();
        }
        this.mDailyPackageChanges.add(packageChange);
    }

    void stopAllGpsSignalQualityTimersLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        for (int i2 = 0; i2 < 2; i2++) {
            if (i2 != i) {
                while (this.mGpsSignalQualityTimer[i2].isRunningLocked()) {
                    this.mGpsSignalQualityTimer[i2].stopRunningLocked(jElapsedRealtime);
                }
            }
        }
    }

    public void notePhoneOnLocked() {
        if (this.mPhoneOn) {
            return;
        }
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        this.mHistoryCur.states2 |= 8388608;
        addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        this.mPhoneOn = true;
        this.mPhoneOnTimer.startRunningLocked(jElapsedRealtime);
    }

    public void notePhoneOffLocked() {
        if (this.mPhoneOn) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mHistoryCur.states2 &= -8388609;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mPhoneOn = false;
            this.mPhoneOnTimer.stopRunningLocked(jElapsedRealtime);
        }
    }

    private void registerUsbStateReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_STATE);
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.internal.os.BatteryStatsImpl.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                boolean booleanExtra = intent.getBooleanExtra("connected", false);
                synchronized (BatteryStatsImpl.this) {
                    BatteryStatsImpl.this.noteUsbConnectionStateLocked(booleanExtra);
                }
            }
        }, intentFilter);
        synchronized (this) {
            if (this.mUsbDataState == 0) {
                Intent intentRegisterReceiver = context.registerReceiver(null, intentFilter);
                boolean z = false;
                if (intentRegisterReceiver != null && intentRegisterReceiver.getBooleanExtra("connected", false)) {
                    z = true;
                }
                noteUsbConnectionStateLocked(z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteUsbConnectionStateLocked(boolean z) {
        int i = z ? 2 : 1;
        if (this.mUsbDataState != i) {
            this.mUsbDataState = i;
            if (z) {
                this.mHistoryCur.states2 |= 262144;
            } else {
                this.mHistoryCur.states2 &= -262145;
            }
            addHistoryRecordLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis());
        }
    }

    void stopAllPhoneSignalStrengthTimersLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        for (int i2 = 0; i2 < 5; i2++) {
            if (i2 != i) {
                while (this.mPhoneSignalStrengthsTimer[i2].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i2].stopRunningLocked(jElapsedRealtime);
                }
            }
        }
    }

    private int fixPhoneServiceState(int i, int i2) {
        if (this.mPhoneSimStateRaw == 1 && i == 1 && i2 > 0) {
            return 0;
        }
        return i;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0061  */
    /* JADX WARN: Code duplicated, block: B:27:0x0073 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:28:0x0075  */
    /* JADX WARN: Code duplicated, block: B:30:0x007e  */
    /* JADX WARN: Code duplicated, block: B:32:0x0088  */
    /* JADX WARN: Code duplicated, block: B:34:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:37:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:39:? A[RETURN, SYNTHETIC] */
    private void updateAllPhoneStateLocked(int i, int i2, int i3) {
        boolean z;
        int i4;
        this.mPhoneServiceStateRaw = i;
        this.mPhoneSimStateRaw = i2;
        this.mPhoneSignalStrengthBinRaw = i3;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        boolean z2 = false;
        boolean z3 = true;
        if (i2 == 1 && i == 1 && i3 > 0) {
            i = 0;
        }
        if (i != 3) {
            if (i != 0 && i == 1) {
                if (this.mPhoneSignalScanningTimer.isRunningLocked()) {
                    i3 = 0;
                    z = false;
                    z2 = true;
                } else {
                    this.mHistoryCur.states |= 2097152;
                    this.mPhoneSignalScanningTimer.startRunningLocked(jElapsedRealtime);
                    i3 = 0;
                    z2 = true;
                }
            }
            if (!z2 && this.mPhoneSignalScanningTimer.isRunningLocked()) {
                this.mHistoryCur.states &= -2097153;
                this.mPhoneSignalScanningTimer.stopRunningLocked(jElapsedRealtime);
                z = true;
            }
            if (this.mPhoneServiceState != i) {
                BatteryStats.HistoryItem historyItem = this.mHistoryCur;
                historyItem.states = (historyItem.states & (-449)) | (i << 6);
                this.mPhoneServiceState = i;
                z = true;
            }
            i4 = this.mPhoneSignalStrengthBin;
            if (i4 != i3) {
                if (i4 >= 0) {
                    this.mPhoneSignalStrengthsTimer[i4].stopRunningLocked(jElapsedRealtime);
                }
                if (i3 >= 0) {
                    if (!this.mPhoneSignalStrengthsTimer[i3].isRunningLocked()) {
                        this.mPhoneSignalStrengthsTimer[i3].startRunningLocked(jElapsedRealtime);
                    }
                    BatteryStats.HistoryItem historyItem2 = this.mHistoryCur;
                    historyItem2.states = (historyItem2.states & (-57)) | (i3 << 3);
                    StatsLog.write(40, i3);
                } else {
                    stopAllPhoneSignalStrengthTimersLocked(-1);
                    z3 = z;
                }
                this.mPhoneSignalStrengthBin = i3;
                z = z3;
            }
            if (z) {
                addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            }
        }
        i3 = -1;
        z = z2;
        if (!z2) {
            this.mHistoryCur.states &= -2097153;
            this.mPhoneSignalScanningTimer.stopRunningLocked(jElapsedRealtime);
            z = true;
        }
        if (this.mPhoneServiceState != i) {
            BatteryStats.HistoryItem historyItem3 = this.mHistoryCur;
            historyItem3.states = (historyItem3.states & (-449)) | (i << 6);
            this.mPhoneServiceState = i;
            z = true;
        }
        i4 = this.mPhoneSignalStrengthBin;
        if (i4 != i3) {
            if (i4 >= 0) {
                this.mPhoneSignalStrengthsTimer[i4].stopRunningLocked(jElapsedRealtime);
            }
            if (i3 >= 0) {
                if (!this.mPhoneSignalStrengthsTimer[i3].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i3].startRunningLocked(jElapsedRealtime);
                }
                BatteryStats.HistoryItem historyItem4 = this.mHistoryCur;
                historyItem4.states = (historyItem4.states & (-57)) | (i3 << 3);
                StatsLog.write(40, i3);
            } else {
                stopAllPhoneSignalStrengthTimersLocked(-1);
                z3 = z;
            }
            this.mPhoneSignalStrengthBin = i3;
            z = z3;
        }
        if (z) {
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
    }

    public void notePhoneStateLocked(int i, int i2) {
        updateAllPhoneStateLocked(i, i2, this.mPhoneSignalStrengthBinRaw);
    }

    public void notePhoneSignalStrengthLocked(SignalStrength signalStrength) {
        updateAllPhoneStateLocked(this.mPhoneServiceStateRaw, this.mPhoneSimStateRaw, signalStrength.getLevel());
    }

    public void notePhoneDataConnectionStateLocked(int i, boolean z) {
        if (!z) {
            i = 0;
        } else if (i <= 0 || i > 19) {
            i = 20;
        }
        if (this.mPhoneDataConnectionType != i) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            BatteryStats.HistoryItem historyItem = this.mHistoryCur;
            historyItem.states = (historyItem.states & (-15873)) | (i << 9);
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            int i2 = this.mPhoneDataConnectionType;
            if (i2 >= 0) {
                this.mPhoneDataConnectionsTimer[i2].stopRunningLocked(jElapsedRealtime);
            }
            this.mPhoneDataConnectionType = i;
            this.mPhoneDataConnectionsTimer[i].startRunningLocked(jElapsedRealtime);
        }
    }

    public void noteWifiOnLocked() {
        if (this.mWifiOn) {
            return;
        }
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        this.mHistoryCur.states2 |= 268435456;
        addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        this.mWifiOn = true;
        this.mWifiOnTimer.startRunningLocked(jElapsedRealtime);
        scheduleSyncExternalStatsLocked("wifi-off", 2);
    }

    public void noteWifiOffLocked() {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mWifiOn) {
            this.mHistoryCur.states2 &= -268435457;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mWifiOn = false;
            this.mWifiOnTimer.stopRunningLocked(jElapsedRealtime);
            scheduleSyncExternalStatsLocked("wifi-on", 2);
        }
    }

    public void noteAudioOnLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mAudioOnNesting == 0) {
            this.mHistoryCur.states |= 4194304;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mAudioOnTimer.startRunningLocked(jElapsedRealtime);
        }
        this.mAudioOnNesting++;
        getUidStatsLocked(iMapUid).noteAudioTurnedOnLocked(jElapsedRealtime);
    }

    public void noteAudioOffLocked(int i) {
        if (this.mAudioOnNesting == 0) {
            return;
        }
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mAudioOnNesting - 1;
        this.mAudioOnNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states &= -4194305;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mAudioOnTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteAudioTurnedOffLocked(jElapsedRealtime);
    }

    public void noteVideoOnLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mVideoOnNesting == 0) {
            this.mHistoryCur.states2 |= 1073741824;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mVideoOnTimer.startRunningLocked(jElapsedRealtime);
        }
        this.mVideoOnNesting++;
        getUidStatsLocked(iMapUid).noteVideoTurnedOnLocked(jElapsedRealtime);
    }

    public void noteVideoOffLocked(int i) {
        if (this.mVideoOnNesting == 0) {
            return;
        }
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mVideoOnNesting - 1;
        this.mVideoOnNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states2 &= -1073741825;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mVideoOnTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteVideoTurnedOffLocked(jElapsedRealtime);
    }

    public void noteResetAudioLocked() {
        if (this.mAudioOnNesting > 0) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mAudioOnNesting = 0;
            this.mHistoryCur.states &= -4194305;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mAudioOnTimer.stopAllRunningLocked(jElapsedRealtime);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetAudioLocked(jElapsedRealtime);
            }
        }
    }

    public void noteResetVideoLocked() {
        if (this.mVideoOnNesting > 0) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mAudioOnNesting = 0;
            this.mHistoryCur.states2 &= -1073741825;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mVideoOnTimer.stopAllRunningLocked(jElapsedRealtime);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetVideoLocked(jElapsedRealtime);
            }
        }
    }

    public void noteActivityResumedLocked(int i) {
        getUidStatsLocked(mapUid(i)).noteActivityResumedLocked(this.mClocks.elapsedRealtime());
    }

    public void noteActivityPausedLocked(int i) {
        getUidStatsLocked(mapUid(i)).noteActivityPausedLocked(this.mClocks.elapsedRealtime());
    }

    public void noteVibratorOnLocked(int i, long j) {
        getUidStatsLocked(mapUid(i)).noteVibratorOnLocked(j);
    }

    public void noteVibratorOffLocked(int i) {
        getUidStatsLocked(mapUid(i)).noteVibratorOffLocked();
    }

    public void noteFlashlightOnLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mFlashlightOnNesting;
        this.mFlashlightOnNesting = i2 + 1;
        if (i2 == 0) {
            this.mHistoryCur.states2 |= 134217728;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mFlashlightOnTimer.startRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteFlashlightTurnedOnLocked(jElapsedRealtime);
    }

    public void noteFlashlightOffLocked(int i) {
        if (this.mFlashlightOnNesting == 0) {
            return;
        }
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mFlashlightOnNesting - 1;
        this.mFlashlightOnNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states2 &= -134217729;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mFlashlightOnTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteFlashlightTurnedOffLocked(jElapsedRealtime);
    }

    public void noteCameraOnLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mCameraOnNesting;
        this.mCameraOnNesting = i2 + 1;
        if (i2 == 0) {
            this.mHistoryCur.states2 |= 2097152;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mCameraOnTimer.startRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteCameraTurnedOnLocked(jElapsedRealtime);
    }

    public void noteCameraOffLocked(int i) {
        if (this.mCameraOnNesting == 0) {
            return;
        }
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mCameraOnNesting - 1;
        this.mCameraOnNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states2 &= -2097153;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mCameraOnTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(iMapUid).noteCameraTurnedOffLocked(jElapsedRealtime);
    }

    public void noteResetCameraLocked() {
        if (this.mCameraOnNesting > 0) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mCameraOnNesting = 0;
            this.mHistoryCur.states2 &= -2097153;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mCameraOnTimer.stopAllRunningLocked(jElapsedRealtime);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetCameraLocked(jElapsedRealtime);
            }
        }
    }

    public void noteResetFlashlightLocked() {
        if (this.mFlashlightOnNesting > 0) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mFlashlightOnNesting = 0;
            this.mHistoryCur.states2 &= -134217729;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mFlashlightOnTimer.stopAllRunningLocked(jElapsedRealtime);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetFlashlightLocked(jElapsedRealtime);
            }
        }
    }

    private void noteBluetoothScanStartedLocked(WorkSource.WorkChain workChain, int i, boolean z) {
        int attributionUid = getAttributionUid(i, workChain);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mBluetoothScanNesting == 0) {
            this.mHistoryCur.states2 |= 1048576;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mBluetoothScanTimer.startRunningLocked(jElapsedRealtime);
        }
        this.mBluetoothScanNesting++;
        getUidStatsLocked(attributionUid).noteBluetoothScanStartedLocked(jElapsedRealtime, z);
    }

    public void noteBluetoothScanStartedFromSourceLocked(WorkSource workSource, boolean z) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteBluetoothScanStartedLocked(null, workSource.get(i), z);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteBluetoothScanStartedLocked(workChains.get(i2), -1, z);
            }
        }
    }

    private void noteBluetoothScanStoppedLocked(WorkSource.WorkChain workChain, int i, boolean z) {
        int attributionUid = getAttributionUid(i, workChain);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mBluetoothScanNesting - 1;
        this.mBluetoothScanNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states2 &= -1048577;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mBluetoothScanTimer.stopRunningLocked(jElapsedRealtime);
        }
        getUidStatsLocked(attributionUid).noteBluetoothScanStoppedLocked(jElapsedRealtime, z);
    }

    private int getAttributionUid(int i, WorkSource.WorkChain workChain) {
        if (workChain != null) {
            return mapUid(workChain.getAttributionUid());
        }
        return mapUid(i);
    }

    public void noteBluetoothScanStoppedFromSourceLocked(WorkSource workSource, boolean z) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteBluetoothScanStoppedLocked(null, workSource.get(i), z);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteBluetoothScanStoppedLocked(workChains.get(i2), -1, z);
            }
        }
    }

    public void noteResetBluetoothScanLocked() {
        if (this.mBluetoothScanNesting > 0) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mBluetoothScanNesting = 0;
            this.mHistoryCur.states2 &= -1048577;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mBluetoothScanTimer.stopAllRunningLocked(jElapsedRealtime);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetBluetoothScanLocked(jElapsedRealtime);
            }
        }
    }

    public void noteBluetoothScanResultsFromSourceLocked(WorkSource workSource, int i) {
        int size = workSource.size();
        for (int i2 = 0; i2 < size; i2++) {
            getUidStatsLocked(mapUid(workSource.get(i2))).noteBluetoothScanResultsLocked(i);
            StatsLog.write_non_chained(4, workSource.get(i2), workSource.getName(i2), i);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i3 = 0; i3 < workChains.size(); i3++) {
                WorkSource.WorkChain workChain = workChains.get(i3);
                getUidStatsLocked(mapUid(workChain.getAttributionUid())).noteBluetoothScanResultsLocked(i);
                StatsLog.write(4, workChain.getUids(), workChain.getTags(), i);
            }
        }
    }

    private void noteWifiRadioApWakeupLocked(long j, long j2, int i) {
        int iMapUid = mapUid(i);
        addHistoryEventLocked(j, j2, 19, "", iMapUid);
        getUidStatsLocked(iMapUid).noteWifiRadioApWakeupLocked();
    }

    public void noteWifiRadioPowerState(int i, long j, int i2) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mWifiRadioPowerState != i) {
            if (i == 2 || i == 3) {
                if (i2 > 0) {
                    noteWifiRadioApWakeupLocked(jElapsedRealtime, jUptimeMillis, i2);
                }
                this.mHistoryCur.states |= 67108864;
                this.mWifiActiveTimer.startRunningLocked(jElapsedRealtime);
            } else {
                this.mHistoryCur.states &= -67108865;
                this.mWifiActiveTimer.stopRunningLocked(j / TimeUtils.NANOS_PER_MS);
            }
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mWifiRadioPowerState = i;
            StatsLog.write_non_chained(13, i2, (String) null, i);
        }
    }

    public void noteWifiRunningLocked(WorkSource workSource) {
        if (!this.mGlobalWifiRunning) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mHistoryCur.states2 |= 536870912;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mGlobalWifiRunning = true;
            this.mGlobalWifiRunningTimer.startRunningLocked(jElapsedRealtime);
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.get(i))).noteWifiRunningLocked(jElapsedRealtime);
            }
            ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(workChains.get(i2).getAttributionUid())).noteWifiRunningLocked(jElapsedRealtime);
                }
            }
            scheduleSyncExternalStatsLocked("wifi-running", 2);
            return;
        }
        Log.w(TAG, "noteWifiRunningLocked -- called while WIFI running");
    }

    public void noteWifiRunningChangedLocked(WorkSource workSource, WorkSource workSource2) {
        if (this.mGlobalWifiRunning) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.get(i))).noteWifiStoppedLocked(jElapsedRealtime);
            }
            ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(workChains.get(i2).getAttributionUid())).noteWifiStoppedLocked(jElapsedRealtime);
                }
            }
            int size2 = workSource2.size();
            for (int i3 = 0; i3 < size2; i3++) {
                getUidStatsLocked(mapUid(workSource2.get(i3))).noteWifiRunningLocked(jElapsedRealtime);
            }
            ArrayList<WorkSource.WorkChain> workChains2 = workSource2.getWorkChains();
            if (workChains2 != null) {
                for (int i4 = 0; i4 < workChains2.size(); i4++) {
                    getUidStatsLocked(mapUid(workChains2.get(i4).getAttributionUid())).noteWifiRunningLocked(jElapsedRealtime);
                }
                return;
            }
            return;
        }
        Log.w(TAG, "noteWifiRunningChangedLocked -- called while WIFI not running");
    }

    public void noteWifiStoppedLocked(WorkSource workSource) {
        if (this.mGlobalWifiRunning) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            this.mHistoryCur.states2 &= -536870913;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            this.mGlobalWifiRunning = false;
            this.mGlobalWifiRunningTimer.stopRunningLocked(jElapsedRealtime);
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.get(i))).noteWifiStoppedLocked(jElapsedRealtime);
            }
            ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(workChains.get(i2).getAttributionUid())).noteWifiStoppedLocked(jElapsedRealtime);
                }
            }
            scheduleSyncExternalStatsLocked("wifi-stopped", 2);
            return;
        }
        Log.w(TAG, "noteWifiStoppedLocked -- called while WIFI not running");
    }

    public void noteWifiStateLocked(int i, String str) {
        if (this.mWifiState != i) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            int i2 = this.mWifiState;
            if (i2 >= 0) {
                this.mWifiStateTimer[i2].stopRunningLocked(jElapsedRealtime);
            }
            this.mWifiState = i;
            this.mWifiStateTimer[i].startRunningLocked(jElapsedRealtime);
            scheduleSyncExternalStatsLocked("wifi-state", 2);
        }
    }

    public void noteWifiSupplicantStateChangedLocked(int i, boolean z) {
        if (this.mWifiSupplState != i) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            int i2 = this.mWifiSupplState;
            if (i2 >= 0) {
                this.mWifiSupplStateTimer[i2].stopRunningLocked(jElapsedRealtime);
            }
            this.mWifiSupplState = i;
            this.mWifiSupplStateTimer[i].startRunningLocked(jElapsedRealtime);
            BatteryStats.HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 = (i << 0) | (historyItem.states2 & (-16));
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
    }

    void stopAllWifiSignalStrengthTimersLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        for (int i2 = 0; i2 < 5; i2++) {
            if (i2 != i) {
                while (this.mWifiSignalStrengthsTimer[i2].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[i2].stopRunningLocked(jElapsedRealtime);
                }
            }
        }
    }

    public void noteWifiRssiChangedLocked(int i) {
        int iCalculateSignalLevel = WifiManager.calculateSignalLevel(i, 5);
        if (this.mWifiSignalStrengthBin != iCalculateSignalLevel) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            int i2 = this.mWifiSignalStrengthBin;
            if (i2 >= 0) {
                this.mWifiSignalStrengthsTimer[i2].stopRunningLocked(jElapsedRealtime);
            }
            if (iCalculateSignalLevel >= 0) {
                if (!this.mWifiSignalStrengthsTimer[iCalculateSignalLevel].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[iCalculateSignalLevel].startRunningLocked(jElapsedRealtime);
                }
                BatteryStats.HistoryItem historyItem = this.mHistoryCur;
                historyItem.states2 = (historyItem.states2 & PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS) | (iCalculateSignalLevel << 4);
                addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            } else {
                stopAllWifiSignalStrengthTimersLocked(-1);
            }
            StatsLog.write(38, iCalculateSignalLevel);
            this.mWifiSignalStrengthBin = iCalculateSignalLevel;
        }
    }

    public void noteFullWifiLockAcquiredLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mWifiFullLockNesting == 0) {
            this.mHistoryCur.states |= 268435456;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        this.mWifiFullLockNesting++;
        getUidStatsLocked(i).noteFullWifiLockAcquiredLocked(jElapsedRealtime);
    }

    public void noteFullWifiLockReleasedLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mWifiFullLockNesting - 1;
        this.mWifiFullLockNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states &= -268435457;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        getUidStatsLocked(i).noteFullWifiLockReleasedLocked(jElapsedRealtime);
    }

    public void noteWifiScanStartedLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mWifiScanNesting == 0) {
            this.mHistoryCur.states |= 134217728;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        this.mWifiScanNesting++;
        getUidStatsLocked(i).noteWifiScanStartedLocked(jElapsedRealtime);
    }

    public void noteWifiScanStoppedLocked(int i) {
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mWifiScanNesting - 1;
        this.mWifiScanNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states &= -134217729;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        }
        getUidStatsLocked(i).noteWifiScanStoppedLocked(jElapsedRealtime);
    }

    public void noteWifiBatchedScanStartedLocked(int i, int i2) {
        int iMapUid = mapUid(i);
        getUidStatsLocked(iMapUid).noteWifiBatchedScanStartedLocked(i2, this.mClocks.elapsedRealtime());
    }

    public void noteWifiBatchedScanStoppedLocked(int i) {
        int iMapUid = mapUid(i);
        getUidStatsLocked(iMapUid).noteWifiBatchedScanStoppedLocked(this.mClocks.elapsedRealtime());
    }

    public void noteWifiMulticastEnabledLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        if (this.mWifiMulticastNesting == 0) {
            this.mHistoryCur.states |= 65536;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            if (!this.mWifiMulticastWakelockTimer.isRunningLocked()) {
                this.mWifiMulticastWakelockTimer.startRunningLocked(jElapsedRealtime);
            }
        }
        this.mWifiMulticastNesting++;
        getUidStatsLocked(iMapUid).noteWifiMulticastEnabledLocked(jElapsedRealtime);
    }

    public void noteWifiMulticastDisabledLocked(int i) {
        int iMapUid = mapUid(i);
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        int i2 = this.mWifiMulticastNesting - 1;
        this.mWifiMulticastNesting = i2;
        if (i2 == 0) {
            this.mHistoryCur.states &= -65537;
            addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
            if (this.mWifiMulticastWakelockTimer.isRunningLocked()) {
                this.mWifiMulticastWakelockTimer.stopRunningLocked(jElapsedRealtime);
            }
        }
        getUidStatsLocked(iMapUid).noteWifiMulticastDisabledLocked(jElapsedRealtime);
    }

    public void noteFullWifiLockAcquiredFromSourceLocked(WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteFullWifiLockAcquiredLocked(mapUid(workSource.get(i)));
            StatsLog.write_non_chained(37, workSource.get(i), workSource.getName(i), 1);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteFullWifiLockAcquiredLocked(mapUid(workChain.getAttributionUid()));
                StatsLog.write(37, workChain.getUids(), workChain.getTags(), 1);
            }
        }
    }

    public void noteFullWifiLockReleasedFromSourceLocked(WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteFullWifiLockReleasedLocked(mapUid(workSource.get(i)));
            StatsLog.write_non_chained(37, workSource.get(i), workSource.getName(i), 0);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteFullWifiLockReleasedLocked(mapUid(workChain.getAttributionUid()));
                StatsLog.write(37, workChain.getUids(), workChain.getTags(), 0);
            }
        }
    }

    public void noteWifiScanStartedFromSourceLocked(WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiScanStartedLocked(mapUid(workSource.get(i)));
            StatsLog.write_non_chained(39, workSource.get(i), workSource.getName(i), 1);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteWifiScanStartedLocked(mapUid(workChain.getAttributionUid()));
                StatsLog.write(39, workChain.getUids(), workChain.getTags(), 1);
            }
        }
    }

    public void noteWifiScanStoppedFromSourceLocked(WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiScanStoppedLocked(mapUid(workSource.get(i)));
            StatsLog.write_non_chained(39, workSource.get(i), workSource.getName(i), 0);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                WorkSource.WorkChain workChain = workChains.get(i2);
                noteWifiScanStoppedLocked(mapUid(workChain.getAttributionUid()));
                StatsLog.write(39, workChain.getUids(), workChain.getTags(), 0);
            }
        }
    }

    public void noteWifiBatchedScanStartedFromSourceLocked(WorkSource workSource, int i) {
        int size = workSource.size();
        for (int i2 = 0; i2 < size; i2++) {
            noteWifiBatchedScanStartedLocked(workSource.get(i2), i);
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i3 = 0; i3 < workChains.size(); i3++) {
                noteWifiBatchedScanStartedLocked(workChains.get(i3).getAttributionUid(), i);
            }
        }
    }

    public void noteWifiBatchedScanStoppedFromSourceLocked(WorkSource workSource) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiBatchedScanStoppedLocked(workSource.get(i));
        }
        ArrayList<WorkSource.WorkChain> workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteWifiBatchedScanStoppedLocked(workChains.get(i2).getAttributionUid());
            }
        }
    }

    private static String[] includeInStringArray(String[] strArr, String str) {
        if (ArrayUtils.indexOf(strArr, str) >= 0) {
            return strArr;
        }
        String[] strArr2 = new String[strArr.length + 1];
        System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
        strArr2[strArr.length] = str;
        return strArr2;
    }

    private static String[] excludeFromStringArray(String[] strArr, String str) {
        int iIndexOf = ArrayUtils.indexOf(strArr, str);
        if (iIndexOf < 0) {
            return strArr;
        }
        String[] strArr2 = new String[strArr.length - 1];
        if (iIndexOf > 0) {
            System.arraycopy(strArr, 0, strArr2, 0, iIndexOf);
        }
        if (iIndexOf < strArr.length - 1) {
            System.arraycopy(strArr, iIndexOf + 1, strArr2, iIndexOf, (strArr.length - iIndexOf) - 1);
        }
        return strArr2;
    }

    public void noteNetworkInterfaceTypeLocked(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        synchronized (this.mModemNetworkLock) {
            if (ConnectivityManager.isNetworkTypeMobile(i)) {
                this.mModemIfaces = includeInStringArray(this.mModemIfaces, str);
            } else {
                this.mModemIfaces = excludeFromStringArray(this.mModemIfaces, str);
            }
        }
        synchronized (this.mWifiNetworkLock) {
            if (ConnectivityManager.isNetworkTypeWifi(i)) {
                this.mWifiIfaces = includeInStringArray(this.mWifiIfaces, str);
            } else {
                this.mWifiIfaces = excludeFromStringArray(this.mWifiIfaces, str);
            }
        }
    }

    public String[] getWifiIfaces() {
        String[] strArr;
        synchronized (this.mWifiNetworkLock) {
            strArr = this.mWifiIfaces;
        }
        return strArr;
    }

    public String[] getMobileIfaces() {
        String[] strArr;
        synchronized (this.mModemNetworkLock) {
            strArr = this.mModemIfaces;
        }
        return strArr;
    }

    @Override // android.os.BatteryStats
    public long getScreenOnTime(long j, int i) {
        return this.mScreenOnTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getScreenOnCount(int i) {
        return this.mScreenOnTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getScreenDozeTime(long j, int i) {
        return this.mScreenDozeTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getScreenDozeCount(int i) {
        return this.mScreenDozeTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getScreenBrightnessTime(int i, long j, int i2) {
        return this.mScreenBrightnessTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public Timer getScreenBrightnessTimer(int i) {
        return this.mScreenBrightnessTimer[i];
    }

    @Override // android.os.BatteryStats
    public long getInteractiveTime(long j, int i) {
        return this.mInteractiveTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getPowerSaveModeEnabledTime(long j, int i) {
        return this.mPowerSaveModeEnabledTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getPowerSaveModeEnabledCount(int i) {
        return this.mPowerSaveModeEnabledTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getDeviceIdleModeTime(int i, long j, int i2) {
        if (i == 1) {
            return this.mDeviceIdleModeLightTimer.getTotalTimeLocked(j, i2);
        }
        if (i != 2) {
            return 0L;
        }
        return this.mDeviceIdleModeFullTimer.getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getDeviceIdleModeCount(int i, int i2) {
        if (i == 1) {
            return this.mDeviceIdleModeLightTimer.getCountLocked(i2);
        }
        if (i != 2) {
            return 0;
        }
        return this.mDeviceIdleModeFullTimer.getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public long getLongestDeviceIdleModeTime(int i) {
        if (i == 1) {
            return this.mLongestLightIdleTime;
        }
        if (i != 2) {
            return 0L;
        }
        return this.mLongestFullIdleTime;
    }

    @Override // android.os.BatteryStats
    public long getDeviceIdlingTime(int i, long j, int i2) {
        if (i == 1) {
            return this.mDeviceLightIdlingTimer.getTotalTimeLocked(j, i2);
        }
        if (i != 2) {
            return 0L;
        }
        return this.mDeviceIdlingTimer.getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getDeviceIdlingCount(int i, int i2) {
        if (i == 1) {
            return this.mDeviceLightIdlingTimer.getCountLocked(i2);
        }
        if (i != 2) {
            return 0;
        }
        return this.mDeviceIdlingTimer.getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public int getNumConnectivityChange(int i) {
        int i2;
        int i3 = this.mNumConnectivityChange;
        if (i == 1) {
            i2 = this.mLoadedNumConnectivityChange;
        } else {
            if (i != 2) {
                return i3;
            }
            i2 = this.mUnpluggedNumConnectivityChange;
        }
        return i3 - i2;
    }

    @Override // android.os.BatteryStats
    public long getGpsSignalQualityTime(int i, long j, int i2) {
        if (i < 0 || i >= 2) {
            return 0L;
        }
        return this.mGpsSignalQualityTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public long getGpsBatteryDrainMaMs() {
        double averagePower = 0.0d;
        if (this.mPowerProfile.getAveragePower(PowerProfile.POWER_GPS_OPERATING_VOLTAGE) / 1000.0d == 0.0d) {
            return 0L;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        for (int i = 0; i < 2; i++) {
            averagePower += this.mPowerProfile.getAveragePower(PowerProfile.POWER_GPS_SIGNAL_QUALITY_BASED, i) * (getGpsSignalQualityTime(i, jElapsedRealtime, 0) / 1000);
        }
        return (long) averagePower;
    }

    @Override // android.os.BatteryStats
    public long getPhoneOnTime(long j, int i) {
        return this.mPhoneOnTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getPhoneOnCount(int i) {
        return this.mPhoneOnTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getPhoneSignalStrengthTime(int i, long j, int i2) {
        return this.mPhoneSignalStrengthsTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public long getPhoneSignalScanningTime(long j, int i) {
        return this.mPhoneSignalScanningTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public Timer getPhoneSignalScanningTimer() {
        return this.mPhoneSignalScanningTimer;
    }

    @Override // android.os.BatteryStats
    public int getPhoneSignalStrengthCount(int i, int i2) {
        return this.mPhoneSignalStrengthsTimer[i].getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public Timer getPhoneSignalStrengthTimer(int i) {
        return this.mPhoneSignalStrengthsTimer[i];
    }

    @Override // android.os.BatteryStats
    public long getPhoneDataConnectionTime(int i, long j, int i2) {
        return this.mPhoneDataConnectionsTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getPhoneDataConnectionCount(int i, int i2) {
        return this.mPhoneDataConnectionsTimer[i].getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public Timer getPhoneDataConnectionTimer(int i) {
        return this.mPhoneDataConnectionsTimer[i];
    }

    @Override // android.os.BatteryStats
    public long getMobileRadioActiveTime(long j, int i) {
        return this.mMobileRadioActiveTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getMobileRadioActiveCount(int i) {
        return this.mMobileRadioActiveTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getMobileRadioActiveAdjustedTime(int i) {
        return this.mMobileRadioActiveAdjustedTime.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getMobileRadioActiveUnknownTime(int i) {
        return this.mMobileRadioActiveUnknownTime.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public int getMobileRadioActiveUnknownCount(int i) {
        return (int) this.mMobileRadioActiveUnknownCount.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getWifiMulticastWakelockTime(long j, int i) {
        return this.mWifiMulticastWakelockTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public int getWifiMulticastWakelockCount(int i) {
        return this.mWifiMulticastWakelockTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getWifiOnTime(long j, int i) {
        return this.mWifiOnTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getWifiActiveTime(long j, int i) {
        return this.mWifiActiveTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getGlobalWifiRunningTime(long j, int i) {
        return this.mGlobalWifiRunningTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getWifiStateTime(int i, long j, int i2) {
        return this.mWifiStateTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getWifiStateCount(int i, int i2) {
        return this.mWifiStateTimer[i].getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public Timer getWifiStateTimer(int i) {
        return this.mWifiStateTimer[i];
    }

    @Override // android.os.BatteryStats
    public long getWifiSupplStateTime(int i, long j, int i2) {
        return this.mWifiSupplStateTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getWifiSupplStateCount(int i, int i2) {
        return this.mWifiSupplStateTimer[i].getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public Timer getWifiSupplStateTimer(int i) {
        return this.mWifiSupplStateTimer[i];
    }

    @Override // android.os.BatteryStats
    public long getWifiSignalStrengthTime(int i, long j, int i2) {
        return this.mWifiSignalStrengthsTimer[i].getTotalTimeLocked(j, i2);
    }

    @Override // android.os.BatteryStats
    public int getWifiSignalStrengthCount(int i, int i2) {
        return this.mWifiSignalStrengthsTimer[i].getCountLocked(i2);
    }

    @Override // android.os.BatteryStats
    public Timer getWifiSignalStrengthTimer(int i) {
        return this.mWifiSignalStrengthsTimer[i];
    }

    @Override // android.os.BatteryStats
    public BatteryStats.ControllerActivityCounter getBluetoothControllerActivity() {
        return this.mBluetoothActivity;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.ControllerActivityCounter getWifiControllerActivity() {
        return this.mWifiActivity;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.ControllerActivityCounter getModemControllerActivity() {
        return this.mModemActivity;
    }

    @Override // android.os.BatteryStats
    public boolean hasBluetoothActivityReporting() {
        return this.mHasBluetoothReporting;
    }

    @Override // android.os.BatteryStats
    public boolean hasWifiActivityReporting() {
        return this.mHasWifiReporting;
    }

    @Override // android.os.BatteryStats
    public boolean hasModemActivityReporting() {
        return this.mHasModemReporting;
    }

    @Override // android.os.BatteryStats
    public long getFlashlightOnTime(long j, int i) {
        return this.mFlashlightOnTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getFlashlightOnCount(int i) {
        return this.mFlashlightOnTimer.getCountLocked(i);
    }

    @Override // android.os.BatteryStats
    public long getCameraOnTime(long j, int i) {
        return this.mCameraOnTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getBluetoothScanTime(long j, int i) {
        return this.mBluetoothScanTimer.getTotalTimeLocked(j, i);
    }

    @Override // android.os.BatteryStats
    public long getNetworkActivityBytes(int i, int i2) {
        if (i < 0) {
            return 0L;
        }
        LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
        if (i < longSamplingCounterArr.length) {
            return longSamplingCounterArr[i].getCountLocked(i2);
        }
        return 0L;
    }

    @Override // android.os.BatteryStats
    public long getNetworkActivityPackets(int i, int i2) {
        if (i < 0) {
            return 0L;
        }
        LongSamplingCounter[] longSamplingCounterArr = this.mNetworkPacketActivityCounters;
        if (i < longSamplingCounterArr.length) {
            return longSamplingCounterArr[i].getCountLocked(i2);
        }
        return 0L;
    }

    @Override // android.os.BatteryStats
    public long getStartClockTime() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (ensureStartClockTime(jCurrentTimeMillis)) {
            recordCurrentTimeChangeLocked(jCurrentTimeMillis, this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis());
        }
        return this.mStartClockTime;
    }

    @Override // android.os.BatteryStats
    public String getStartPlatformVersion() {
        return this.mStartPlatformVersion;
    }

    @Override // android.os.BatteryStats
    public String getEndPlatformVersion() {
        return this.mEndPlatformVersion;
    }

    @Override // android.os.BatteryStats
    public boolean getIsOnBattery() {
        return this.mOnBattery;
    }

    @Override // android.os.BatteryStats
    public SparseArray<? extends BatteryStats.Uid> getUidStats() {
        return this.mUidStats;
    }

    private static void detachTimerIfNotNull(Timer timer) {
        if (timer != null) {
            timer.detach();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean resetTimerIfNotNull(Timer timer, boolean z) {
        if (timer != null) {
            return timer.reset(z);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean resetTimerIfNotNull(DualTimer dualTimer, boolean z) {
        if (dualTimer != null) {
            return dualTimer.reset(z);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void detachLongCounterIfNotNull(LongSamplingCounter longSamplingCounter) {
        if (longSamplingCounter != null) {
            longSamplingCounter.detach();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void resetLongCounterIfNotNull(LongSamplingCounter longSamplingCounter, boolean z) {
        if (longSamplingCounter != null) {
            longSamplingCounter.reset(z);
        }
    }

    public static class Uid extends BatteryStats.Uid {
        static final int NO_BATCHED_SCAN_STARTED = -1;
        DualTimer mAggregatedPartialWakelockTimer;
        StopwatchTimer mAudioTurnedOnTimer;
        private ControllerActivityCounterImpl mBluetoothControllerActivity;
        Counter mBluetoothScanResultBgCounter;
        Counter mBluetoothScanResultCounter;
        DualTimer mBluetoothScanTimer;
        DualTimer mBluetoothUnoptimizedScanTimer;
        protected BatteryStatsImpl mBsi;
        StopwatchTimer mCameraTurnedOnTimer;
        IntArray mChildUids;
        LongSamplingCounter mCpuActiveTimeMs;
        LongSamplingCounter[][] mCpuClusterSpeedTimesUs;
        LongSamplingCounterArray mCpuClusterTimesMs;
        LongSamplingCounterArray mCpuFreqTimeMs;
        long mCurStepSystemTime;
        long mCurStepUserTime;
        StopwatchTimer mFlashlightTurnedOnTimer;
        StopwatchTimer mForegroundActivityTimer;
        StopwatchTimer mForegroundServiceTimer;
        boolean mFullWifiLockOut;
        StopwatchTimer mFullWifiLockTimer;
        final OverflowArrayMap<DualTimer> mJobStats;
        Counter mJobsDeferredCount;
        Counter mJobsDeferredEventCount;
        final Counter[] mJobsFreshnessBuckets;
        LongSamplingCounter mJobsFreshnessTimeMs;
        long mLastStepSystemTime;
        long mLastStepUserTime;
        LongSamplingCounter mMobileRadioActiveCount;
        LongSamplingCounter mMobileRadioActiveTime;
        private LongSamplingCounter mMobileRadioApWakeupCount;
        private ControllerActivityCounterImpl mModemControllerActivity;
        LongSamplingCounter[] mNetworkByteActivityCounters;
        LongSamplingCounter[] mNetworkPacketActivityCounters;
        public final TimeBase mOnBatteryBackgroundTimeBase;
        public final TimeBase mOnBatteryScreenOffBackgroundTimeBase;
        LongSamplingCounterArray[] mProcStateScreenOffTimeMs;
        LongSamplingCounterArray[] mProcStateTimeMs;
        StopwatchTimer[] mProcessStateTimer;
        LongSamplingCounterArray mScreenOffCpuFreqTimeMs;
        final OverflowArrayMap<DualTimer> mSyncStats;
        LongSamplingCounter mSystemCpuTime;
        final int mUid;
        Counter[] mUserActivityCounters;
        LongSamplingCounter mUserCpuTime;
        BatchTimer mVibratorOnTimer;
        StopwatchTimer mVideoTurnedOnTimer;
        final OverflowArrayMap<Wakelock> mWakelockStats;
        StopwatchTimer[] mWifiBatchedScanTimer;
        private ControllerActivityCounterImpl mWifiControllerActivity;
        boolean mWifiMulticastEnabled;
        StopwatchTimer mWifiMulticastTimer;
        private LongSamplingCounter mWifiRadioApWakeupCount;
        boolean mWifiRunning;
        StopwatchTimer mWifiRunningTimer;
        boolean mWifiScanStarted;
        DualTimer mWifiScanTimer;
        int mWifiBatchedScanBinStarted = -1;
        int mProcessState = 19;
        boolean mInForegroundService = false;
        final ArrayMap<String, SparseIntArray> mJobCompletions = new ArrayMap<>();
        final SparseArray<Sensor> mSensorStats = new SparseArray<>();
        final ArrayMap<String, Proc> mProcessStats = new ArrayMap<>();
        final ArrayMap<String, Pkg> mPackageStats = new ArrayMap<>();
        final SparseArray<BatteryStats.Uid.Pid> mPids = new SparseArray<>();

        public Uid(BatteryStatsImpl batteryStatsImpl, int i) {
            this.mBsi = batteryStatsImpl;
            this.mUid = i;
            TimeBase timeBase = new TimeBase();
            this.mOnBatteryBackgroundTimeBase = timeBase;
            timeBase.init(this.mBsi.mClocks.uptimeMillis() * 1000, this.mBsi.mClocks.elapsedRealtime() * 1000);
            TimeBase timeBase2 = new TimeBase();
            this.mOnBatteryScreenOffBackgroundTimeBase = timeBase2;
            timeBase2.init(this.mBsi.mClocks.uptimeMillis() * 1000, this.mBsi.mClocks.elapsedRealtime() * 1000);
            this.mUserCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mSystemCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mCpuActiveTimeMs = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mCpuClusterTimesMs = new LongSamplingCounterArray(this.mBsi.mOnBatteryTimeBase);
            BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl2);
            this.mWakelockStats = new OverflowArrayMap<Wakelock>(batteryStatsImpl2, i) { // from class: com.android.internal.os.BatteryStatsImpl.Uid.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl2);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.internal.os.BatteryStatsImpl.OverflowArrayMap
                public Wakelock instantiateObject() {
                    return new Wakelock(Uid.this.mBsi, Uid.this);
                }
            };
            BatteryStatsImpl batteryStatsImpl3 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl3);
            this.mSyncStats = new OverflowArrayMap<DualTimer>(batteryStatsImpl3, i) { // from class: com.android.internal.os.BatteryStatsImpl.Uid.2
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl3);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.internal.os.BatteryStatsImpl.OverflowArrayMap
                public DualTimer instantiateObject() {
                    Clocks clocks = Uid.this.mBsi.mClocks;
                    Uid uid = Uid.this;
                    return new DualTimer(clocks, uid, 13, null, uid.mBsi.mOnBatteryTimeBase, Uid.this.mOnBatteryBackgroundTimeBase);
                }
            };
            BatteryStatsImpl batteryStatsImpl4 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl4);
            this.mJobStats = new OverflowArrayMap<DualTimer>(batteryStatsImpl4, i) { // from class: com.android.internal.os.BatteryStatsImpl.Uid.3
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl4);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.internal.os.BatteryStatsImpl.OverflowArrayMap
                public DualTimer instantiateObject() {
                    Clocks clocks = Uid.this.mBsi.mClocks;
                    Uid uid = Uid.this;
                    return new DualTimer(clocks, uid, 14, null, uid.mBsi.mOnBatteryTimeBase, Uid.this.mOnBatteryBackgroundTimeBase);
                }
            };
            this.mWifiRunningTimer = new StopwatchTimer(this.mBsi.mClocks, this, 4, this.mBsi.mWifiRunningTimers, this.mBsi.mOnBatteryTimeBase);
            this.mFullWifiLockTimer = new StopwatchTimer(this.mBsi.mClocks, this, 5, this.mBsi.mFullWifiLockTimers, this.mBsi.mOnBatteryTimeBase);
            this.mWifiScanTimer = new DualTimer(this.mBsi.mClocks, this, 6, this.mBsi.mWifiScanTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            this.mWifiBatchedScanTimer = new StopwatchTimer[5];
            this.mWifiMulticastTimer = new StopwatchTimer(this.mBsi.mClocks, this, 7, this.mBsi.mWifiMulticastTimers, this.mBsi.mOnBatteryTimeBase);
            this.mProcessStateTimer = new StopwatchTimer[7];
            this.mJobsDeferredEventCount = new Counter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsDeferredCount = new Counter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsFreshnessTimeMs = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsFreshnessBuckets = new Counter[BatteryStats.JOB_FRESHNESS_BUCKETS.length];
        }

        public void setProcessStateForTest(int i) {
            this.mProcessState = i;
        }

        @Override // android.os.BatteryStats.Uid
        public long[] getCpuFreqTimes(int i) {
            return nullIfAllZeros(this.mCpuFreqTimeMs, i);
        }

        @Override // android.os.BatteryStats.Uid
        public long[] getScreenOffCpuFreqTimes(int i) {
            return nullIfAllZeros(this.mScreenOffCpuFreqTimeMs, i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getCpuActiveTime() {
            return this.mCpuActiveTimeMs.getCountLocked(0);
        }

        @Override // android.os.BatteryStats.Uid
        public long[] getCpuClusterTimes() {
            return nullIfAllZeros(this.mCpuClusterTimesMs, 0);
        }

        @Override // android.os.BatteryStats.Uid
        public long[] getCpuFreqTimes(int i, int i2) {
            if (i < 0 || i >= 7 || this.mProcStateTimeMs == null) {
                return null;
            }
            if (!this.mBsi.mPerProcStateCpuTimesAvailable) {
                this.mProcStateTimeMs = null;
                return null;
            }
            return nullIfAllZeros(this.mProcStateTimeMs[i2], i);
        }

        @Override // android.os.BatteryStats.Uid
        public long[] getScreenOffCpuFreqTimes(int i, int i2) {
            if (i < 0 || i >= 7 || this.mProcStateScreenOffTimeMs == null) {
                return null;
            }
            if (!this.mBsi.mPerProcStateCpuTimesAvailable) {
                this.mProcStateScreenOffTimeMs = null;
                return null;
            }
            return nullIfAllZeros(this.mProcStateScreenOffTimeMs[i2], i);
        }

        public void addIsolatedUid(int i) {
            IntArray intArray = this.mChildUids;
            if (intArray == null) {
                this.mChildUids = new IntArray();
            } else if (intArray.indexOf(i) >= 0) {
                return;
            }
            this.mChildUids.add(i);
        }

        public void removeIsolatedUid(int i) {
            IntArray intArray = this.mChildUids;
            int iIndexOf = intArray == null ? -1 : intArray.indexOf(i);
            if (iIndexOf < 0) {
                return;
            }
            this.mChildUids.remove(iIndexOf);
        }

        private long[] nullIfAllZeros(LongSamplingCounterArray longSamplingCounterArray, int i) {
            long[] countsLocked;
            if (longSamplingCounterArray == null || (countsLocked = longSamplingCounterArray.getCountsLocked(i)) == null) {
                return null;
            }
            for (int length = countsLocked.length - 1; length >= 0; length--) {
                if (countsLocked[length] != 0) {
                    return countsLocked;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addProcStateTimesMs(int i, long[] jArr, boolean z) {
            if (this.mProcStateTimeMs == null) {
                this.mProcStateTimeMs = new LongSamplingCounterArray[7];
            }
            LongSamplingCounterArray[] longSamplingCounterArrayArr = this.mProcStateTimeMs;
            if (longSamplingCounterArrayArr[i] == null || longSamplingCounterArrayArr[i].getSize() != jArr.length) {
                this.mProcStateTimeMs[i] = new LongSamplingCounterArray(this.mBsi.mOnBatteryTimeBase);
            }
            this.mProcStateTimeMs[i].addCountLocked(jArr, z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addProcStateScreenOffTimesMs(int i, long[] jArr, boolean z) {
            if (this.mProcStateScreenOffTimeMs == null) {
                this.mProcStateScreenOffTimeMs = new LongSamplingCounterArray[7];
            }
            LongSamplingCounterArray[] longSamplingCounterArrayArr = this.mProcStateScreenOffTimeMs;
            if (longSamplingCounterArrayArr[i] == null || longSamplingCounterArrayArr[i].getSize() != jArr.length) {
                this.mProcStateScreenOffTimeMs[i] = new LongSamplingCounterArray(this.mBsi.mOnBatteryScreenOffTimeBase);
            }
            this.mProcStateScreenOffTimeMs[i].addCountLocked(jArr, z);
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getAggregatedPartialWakelockTimer() {
            return this.mAggregatedPartialWakelockTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, ? extends BatteryStats.Uid.Wakelock> getWakelockStats() {
            return this.mWakelockStats.getMap();
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getMulticastWakelockStats() {
            return this.mWifiMulticastTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, ? extends BatteryStats.Timer> getSyncStats() {
            return this.mSyncStats.getMap();
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, ? extends BatteryStats.Timer> getJobStats() {
            return this.mJobStats.getMap();
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, SparseIntArray> getJobCompletionStats() {
            return this.mJobCompletions;
        }

        @Override // android.os.BatteryStats.Uid
        public SparseArray<? extends BatteryStats.Uid.Sensor> getSensorStats() {
            return this.mSensorStats;
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, ? extends BatteryStats.Uid.Proc> getProcessStats() {
            return this.mProcessStats;
        }

        @Override // android.os.BatteryStats.Uid
        public ArrayMap<String, ? extends BatteryStats.Uid.Pkg> getPackageStats() {
            return this.mPackageStats;
        }

        @Override // android.os.BatteryStats.Uid
        public int getUid() {
            return this.mUid;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiRunningLocked(long j) {
            if (this.mWifiRunning) {
                return;
            }
            this.mWifiRunning = true;
            if (this.mWifiRunningTimer == null) {
                this.mWifiRunningTimer = new StopwatchTimer(this.mBsi.mClocks, this, 4, this.mBsi.mWifiRunningTimers, this.mBsi.mOnBatteryTimeBase);
            }
            this.mWifiRunningTimer.startRunningLocked(j);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiStoppedLocked(long j) {
            if (this.mWifiRunning) {
                this.mWifiRunning = false;
                this.mWifiRunningTimer.stopRunningLocked(j);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteFullWifiLockAcquiredLocked(long j) {
            if (this.mFullWifiLockOut) {
                return;
            }
            this.mFullWifiLockOut = true;
            if (this.mFullWifiLockTimer == null) {
                this.mFullWifiLockTimer = new StopwatchTimer(this.mBsi.mClocks, this, 5, this.mBsi.mFullWifiLockTimers, this.mBsi.mOnBatteryTimeBase);
            }
            this.mFullWifiLockTimer.startRunningLocked(j);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteFullWifiLockReleasedLocked(long j) {
            if (this.mFullWifiLockOut) {
                this.mFullWifiLockOut = false;
                this.mFullWifiLockTimer.stopRunningLocked(j);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiScanStartedLocked(long j) {
            if (this.mWifiScanStarted) {
                return;
            }
            this.mWifiScanStarted = true;
            if (this.mWifiScanTimer == null) {
                this.mWifiScanTimer = new DualTimer(this.mBsi.mClocks, this, 6, this.mBsi.mWifiScanTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            this.mWifiScanTimer.startRunningLocked(j);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiScanStoppedLocked(long j) {
            if (this.mWifiScanStarted) {
                this.mWifiScanStarted = false;
                this.mWifiScanTimer.stopRunningLocked(j);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiBatchedScanStartedLocked(int i, long j) {
            int i2 = 0;
            while (i > 8 && i2 < 4) {
                i >>= 3;
                i2++;
            }
            int i3 = this.mWifiBatchedScanBinStarted;
            if (i3 == i2) {
                return;
            }
            if (i3 != -1) {
                this.mWifiBatchedScanTimer[i3].stopRunningLocked(j);
            }
            this.mWifiBatchedScanBinStarted = i2;
            if (this.mWifiBatchedScanTimer[i2] == null) {
                makeWifiBatchedScanBin(i2, null);
            }
            this.mWifiBatchedScanTimer[i2].startRunningLocked(j);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiBatchedScanStoppedLocked(long j) {
            int i = this.mWifiBatchedScanBinStarted;
            if (i != -1) {
                this.mWifiBatchedScanTimer[i].stopRunningLocked(j);
                this.mWifiBatchedScanBinStarted = -1;
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiMulticastEnabledLocked(long j) {
            if (this.mWifiMulticastEnabled) {
                return;
            }
            this.mWifiMulticastEnabled = true;
            if (this.mWifiMulticastTimer == null) {
                this.mWifiMulticastTimer = new StopwatchTimer(this.mBsi.mClocks, this, 7, this.mBsi.mWifiMulticastTimers, this.mBsi.mOnBatteryTimeBase);
            }
            this.mWifiMulticastTimer.startRunningLocked(j);
            StatsLog.write_non_chained(53, getUid(), (String) null, 1);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiMulticastDisabledLocked(long j) {
            if (this.mWifiMulticastEnabled) {
                this.mWifiMulticastEnabled = false;
                this.mWifiMulticastTimer.stopRunningLocked(j);
                StatsLog.write_non_chained(53, getUid(), (String) null, 0);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public BatteryStats.ControllerActivityCounter getWifiControllerActivity() {
            return this.mWifiControllerActivity;
        }

        @Override // android.os.BatteryStats.Uid
        public BatteryStats.ControllerActivityCounter getBluetoothControllerActivity() {
            return this.mBluetoothControllerActivity;
        }

        @Override // android.os.BatteryStats.Uid
        public BatteryStats.ControllerActivityCounter getModemControllerActivity() {
            return this.mModemControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateWifiControllerActivityLocked() {
            if (this.mWifiControllerActivity == null) {
                this.mWifiControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 1);
            }
            return this.mWifiControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateBluetoothControllerActivityLocked() {
            if (this.mBluetoothControllerActivity == null) {
                this.mBluetoothControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 1);
            }
            return this.mBluetoothControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateModemControllerActivityLocked() {
            if (this.mModemControllerActivity == null) {
                this.mModemControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 5);
            }
            return this.mModemControllerActivity;
        }

        public StopwatchTimer createAudioTurnedOnTimerLocked() {
            if (this.mAudioTurnedOnTimer == null) {
                this.mAudioTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 15, this.mBsi.mAudioTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mAudioTurnedOnTimer;
        }

        public void noteAudioTurnedOnLocked(long j) {
            createAudioTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteAudioTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetAudioLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createVideoTurnedOnTimerLocked() {
            if (this.mVideoTurnedOnTimer == null) {
                this.mVideoTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 8, this.mBsi.mVideoTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mVideoTurnedOnTimer;
        }

        public void noteVideoTurnedOnLocked(long j) {
            createVideoTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteVideoTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mVideoTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetVideoLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mVideoTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createFlashlightTurnedOnTimerLocked() {
            if (this.mFlashlightTurnedOnTimer == null) {
                this.mFlashlightTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 16, this.mBsi.mFlashlightTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mFlashlightTurnedOnTimer;
        }

        public void noteFlashlightTurnedOnLocked(long j) {
            createFlashlightTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteFlashlightTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mFlashlightTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetFlashlightLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mFlashlightTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createCameraTurnedOnTimerLocked() {
            if (this.mCameraTurnedOnTimer == null) {
                this.mCameraTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 17, this.mBsi.mCameraTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mCameraTurnedOnTimer;
        }

        public void noteCameraTurnedOnLocked(long j) {
            createCameraTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteCameraTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mCameraTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetCameraLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mCameraTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createForegroundActivityTimerLocked() {
            if (this.mForegroundActivityTimer == null) {
                this.mForegroundActivityTimer = new StopwatchTimer(this.mBsi.mClocks, this, 10, null, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mForegroundActivityTimer;
        }

        public StopwatchTimer createForegroundServiceTimerLocked() {
            if (this.mForegroundServiceTimer == null) {
                this.mForegroundServiceTimer = new StopwatchTimer(this.mBsi.mClocks, this, 22, null, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mForegroundServiceTimer;
        }

        public DualTimer createAggregatedPartialWakelockTimerLocked() {
            if (this.mAggregatedPartialWakelockTimer == null) {
                this.mAggregatedPartialWakelockTimer = new DualTimer(this.mBsi.mClocks, this, 20, null, this.mBsi.mOnBatteryScreenOffTimeBase, this.mOnBatteryScreenOffBackgroundTimeBase);
            }
            return this.mAggregatedPartialWakelockTimer;
        }

        public DualTimer createBluetoothScanTimerLocked() {
            if (this.mBluetoothScanTimer == null) {
                this.mBluetoothScanTimer = new DualTimer(this.mBsi.mClocks, this, 19, this.mBsi.mBluetoothScanOnTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothScanTimer;
        }

        public DualTimer createBluetoothUnoptimizedScanTimerLocked() {
            if (this.mBluetoothUnoptimizedScanTimer == null) {
                this.mBluetoothUnoptimizedScanTimer = new DualTimer(this.mBsi.mClocks, this, 21, null, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothUnoptimizedScanTimer;
        }

        public void noteBluetoothScanStartedLocked(long j, boolean z) {
            createBluetoothScanTimerLocked().startRunningLocked(j);
            if (z) {
                createBluetoothUnoptimizedScanTimerLocked().startRunningLocked(j);
            }
        }

        public void noteBluetoothScanStoppedLocked(long j, boolean z) {
            DualTimer dualTimer;
            DualTimer dualTimer2 = this.mBluetoothScanTimer;
            if (dualTimer2 != null) {
                dualTimer2.stopRunningLocked(j);
            }
            if (!z || (dualTimer = this.mBluetoothUnoptimizedScanTimer) == null) {
                return;
            }
            dualTimer.stopRunningLocked(j);
        }

        public void noteResetBluetoothScanLocked(long j) {
            DualTimer dualTimer = this.mBluetoothScanTimer;
            if (dualTimer != null) {
                dualTimer.stopAllRunningLocked(j);
            }
            DualTimer dualTimer2 = this.mBluetoothUnoptimizedScanTimer;
            if (dualTimer2 != null) {
                dualTimer2.stopAllRunningLocked(j);
            }
        }

        public Counter createBluetoothScanResultCounterLocked() {
            if (this.mBluetoothScanResultCounter == null) {
                this.mBluetoothScanResultCounter = new Counter(this.mBsi.mOnBatteryTimeBase);
            }
            return this.mBluetoothScanResultCounter;
        }

        public Counter createBluetoothScanResultBgCounterLocked() {
            if (this.mBluetoothScanResultBgCounter == null) {
                this.mBluetoothScanResultBgCounter = new Counter(this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothScanResultBgCounter;
        }

        public void noteBluetoothScanResultsLocked(int i) {
            createBluetoothScanResultCounterLocked().addAtomic(i);
            createBluetoothScanResultBgCounterLocked().addAtomic(i);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteActivityResumedLocked(long j) {
            createForegroundActivityTimerLocked().startRunningLocked(j);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteActivityPausedLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mForegroundActivityTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteForegroundServiceResumedLocked(long j) {
            createForegroundServiceTimerLocked().startRunningLocked(j);
        }

        public void noteForegroundServicePausedLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mForegroundServiceTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public BatchTimer createVibratorOnTimerLocked() {
            if (this.mVibratorOnTimer == null) {
                this.mVibratorOnTimer = new BatchTimer(this.mBsi.mClocks, this, 9, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mVibratorOnTimer;
        }

        public void noteVibratorOnLocked(long j) {
            createVibratorOnTimerLocked().addDuration(this.mBsi, j);
        }

        public void noteVibratorOffLocked() {
            BatchTimer batchTimer = this.mVibratorOnTimer;
            if (batchTimer != null) {
                batchTimer.abortLastDuration(this.mBsi);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiRunningTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mWifiRunningTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getFullWifiLockTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mFullWifiLockTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiScanTime(long j, int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0L;
            }
            return dualTimer.getTotalTimeLocked(j, i);
        }

        @Override // android.os.BatteryStats.Uid
        public int getWifiScanCount(int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0;
            }
            return dualTimer.getCountLocked(i);
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getWifiScanTimer() {
            return this.mWifiScanTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public int getWifiScanBackgroundCount(int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null || dualTimer.getSubTimer() == null) {
                return 0;
            }
            return this.mWifiScanTimer.getSubTimer().getCountLocked(i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiScanActualTime(long j) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0L;
            }
            return dualTimer.getTotalDurationMsLocked((j + 500) / 1000) * 1000;
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiScanBackgroundTime(long j) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null || dualTimer.getSubTimer() == null) {
                return 0L;
            }
            return this.mWifiScanTimer.getSubTimer().getTotalDurationMsLocked((j + 500) / 1000) * 1000;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getWifiScanBackgroundTimer() {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiBatchedScanTime(int i, long j, int i2) {
            if (i < 0 || i >= 5) {
                return 0L;
            }
            StopwatchTimer[] stopwatchTimerArr = this.mWifiBatchedScanTimer;
            if (stopwatchTimerArr[i] == null) {
                return 0L;
            }
            return stopwatchTimerArr[i].getTotalTimeLocked(j, i2);
        }

        @Override // android.os.BatteryStats.Uid
        public int getWifiBatchedScanCount(int i, int i2) {
            if (i < 0 || i >= 5) {
                return 0;
            }
            StopwatchTimer[] stopwatchTimerArr = this.mWifiBatchedScanTimer;
            if (stopwatchTimerArr[i] == null) {
                return 0;
            }
            return stopwatchTimerArr[i].getCountLocked(i2);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiMulticastTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mWifiMulticastTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getAudioTurnedOnTimer() {
            return this.mAudioTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getVideoTurnedOnTimer() {
            return this.mVideoTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getFlashlightTurnedOnTimer() {
            return this.mFlashlightTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getCameraTurnedOnTimer() {
            return this.mCameraTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getForegroundActivityTimer() {
            return this.mForegroundActivityTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getForegroundServiceTimer() {
            return this.mForegroundServiceTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getBluetoothScanTimer() {
            return this.mBluetoothScanTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getBluetoothScanBackgroundTimer() {
            DualTimer dualTimer = this.mBluetoothScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getBluetoothUnoptimizedScanTimer() {
            return this.mBluetoothUnoptimizedScanTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getBluetoothUnoptimizedScanBackgroundTimer() {
            DualTimer dualTimer = this.mBluetoothUnoptimizedScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        @Override // android.os.BatteryStats.Uid
        public Counter getBluetoothScanResultCounter() {
            return this.mBluetoothScanResultCounter;
        }

        @Override // android.os.BatteryStats.Uid
        public Counter getBluetoothScanResultBgCounter() {
            return this.mBluetoothScanResultBgCounter;
        }

        void makeProcessState(int i, Parcel parcel) {
            if (i < 0 || i >= 7) {
                return;
            }
            if (parcel == null) {
                this.mProcessStateTimer[i] = new StopwatchTimer(this.mBsi.mClocks, this, 12, null, this.mBsi.mOnBatteryTimeBase);
            } else {
                this.mProcessStateTimer[i] = new StopwatchTimer(this.mBsi.mClocks, this, 12, null, this.mBsi.mOnBatteryTimeBase, parcel);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public long getProcessStateTime(int i, long j, int i2) {
            if (i < 0 || i >= 7) {
                return 0L;
            }
            StopwatchTimer[] stopwatchTimerArr = this.mProcessStateTimer;
            if (stopwatchTimerArr[i] == null) {
                return 0L;
            }
            return stopwatchTimerArr[i].getTotalTimeLocked(j, i2);
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getProcessStateTimer(int i) {
            if (i < 0 || i >= 7) {
                return null;
            }
            return this.mProcessStateTimer[i];
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getVibratorOnTimer() {
            return this.mVibratorOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteUserActivityLocked(int i) {
            if (this.mUserActivityCounters == null) {
                initUserActivityLocked();
            }
            if (i >= 0 && i < 4) {
                this.mUserActivityCounters[i].stepAtomic();
                return;
            }
            Slog.w(BatteryStatsImpl.TAG, "Unknown user activity type " + i + " was specified.", new Throwable());
        }

        @Override // android.os.BatteryStats.Uid
        public boolean hasUserActivity() {
            return this.mUserActivityCounters != null;
        }

        @Override // android.os.BatteryStats.Uid
        public int getUserActivityCount(int i, int i2) {
            Counter[] counterArr = this.mUserActivityCounters;
            if (counterArr == null) {
                return 0;
            }
            return counterArr[i].getCountLocked(i2);
        }

        void makeWifiBatchedScanBin(int i, Parcel parcel) {
            if (i < 0 || i >= 5) {
                return;
            }
            ArrayList<StopwatchTimer> arrayList = this.mBsi.mWifiBatchedScanTimers.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mBsi.mWifiBatchedScanTimers.put(i, arrayList);
            }
            ArrayList<StopwatchTimer> arrayList2 = arrayList;
            if (parcel == null) {
                this.mWifiBatchedScanTimer[i] = new StopwatchTimer(this.mBsi.mClocks, this, 11, arrayList2, this.mBsi.mOnBatteryTimeBase);
            } else {
                this.mWifiBatchedScanTimer[i] = new StopwatchTimer(this.mBsi.mClocks, this, 11, arrayList2, this.mBsi.mOnBatteryTimeBase, parcel);
            }
        }

        void initUserActivityLocked() {
            this.mUserActivityCounters = new Counter[4];
            for (int i = 0; i < 4; i++) {
                this.mUserActivityCounters[i] = new Counter(this.mBsi.mOnBatteryTimeBase);
            }
        }

        void noteNetworkActivityLocked(int i, long j, long j2) {
            if (this.mNetworkByteActivityCounters == null) {
                initNetworkActivityLocked();
            }
            if (i >= 0 && i < 10) {
                this.mNetworkByteActivityCounters[i].addCountLocked(j);
                this.mNetworkPacketActivityCounters[i].addCountLocked(j2);
                return;
            }
            Slog.w(BatteryStatsImpl.TAG, "Unknown network activity type " + i + " was specified.", new Throwable());
        }

        void noteMobileRadioActiveTimeLocked(long j) {
            if (this.mNetworkByteActivityCounters == null) {
                initNetworkActivityLocked();
            }
            this.mMobileRadioActiveTime.addCountLocked(j);
            this.mMobileRadioActiveCount.addCountLocked(1L);
        }

        @Override // android.os.BatteryStats.Uid
        public boolean hasNetworkActivity() {
            return this.mNetworkByteActivityCounters != null;
        }

        @Override // android.os.BatteryStats.Uid
        public long getNetworkActivityBytes(int i, int i2) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
            if (longSamplingCounterArr == null || i < 0 || i >= longSamplingCounterArr.length) {
                return 0L;
            }
            return longSamplingCounterArr[i].getCountLocked(i2);
        }

        @Override // android.os.BatteryStats.Uid
        public long getNetworkActivityPackets(int i, int i2) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkPacketActivityCounters;
            if (longSamplingCounterArr == null || i < 0 || i >= longSamplingCounterArr.length) {
                return 0L;
            }
            return longSamplingCounterArr[i].getCountLocked(i2);
        }

        @Override // android.os.BatteryStats.Uid
        public long getMobileRadioActiveTime(int i) {
            LongSamplingCounter longSamplingCounter = this.mMobileRadioActiveTime;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i);
            }
            return 0L;
        }

        @Override // android.os.BatteryStats.Uid
        public int getMobileRadioActiveCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mMobileRadioActiveCount;
            if (longSamplingCounter != null) {
                return (int) longSamplingCounter.getCountLocked(i);
            }
            return 0;
        }

        @Override // android.os.BatteryStats.Uid
        public long getUserCpuTimeUs(int i) {
            return this.mUserCpuTime.getCountLocked(i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getSystemCpuTimeUs(int i) {
            return this.mSystemCpuTime.getCountLocked(i);
        }

        @Override // android.os.BatteryStats.Uid
        public long getTimeAtCpuSpeed(int i, int i2, int i3) {
            LongSamplingCounter[] longSamplingCounterArr;
            LongSamplingCounter longSamplingCounter;
            LongSamplingCounter[][] longSamplingCounterArr2 = this.mCpuClusterSpeedTimesUs;
            if (longSamplingCounterArr2 == null || i < 0 || i >= longSamplingCounterArr2.length || (longSamplingCounterArr = longSamplingCounterArr2[i]) == null || i2 < 0 || i2 >= longSamplingCounterArr.length || (longSamplingCounter = longSamplingCounterArr[i2]) == null) {
                return 0L;
            }
            return longSamplingCounter.getCountLocked(i3);
        }

        public void noteMobileRadioApWakeupLocked() {
            if (this.mMobileRadioApWakeupCount == null) {
                this.mMobileRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mMobileRadioApWakeupCount.addCountLocked(1L);
        }

        @Override // android.os.BatteryStats.Uid
        public long getMobileRadioApWakeupCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mMobileRadioApWakeupCount;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i);
            }
            return 0L;
        }

        public void noteWifiRadioApWakeupLocked() {
            if (this.mWifiRadioApWakeupCount == null) {
                this.mWifiRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mWifiRadioApWakeupCount.addCountLocked(1L);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiRadioApWakeupCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mWifiRadioApWakeupCount;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i);
            }
            return 0L;
        }

        @Override // android.os.BatteryStats.Uid
        public void getDeferredJobsCheckinLineLocked(StringBuilder sb, int i) {
            sb.setLength(0);
            int countLocked = this.mJobsDeferredEventCount.getCountLocked(i);
            if (countLocked == 0) {
                return;
            }
            int countLocked2 = this.mJobsDeferredCount.getCountLocked(i);
            long countLocked3 = this.mJobsFreshnessTimeMs.getCountLocked(i);
            sb.append(countLocked);
            sb.append(',');
            sb.append(countLocked2);
            sb.append(',');
            sb.append(countLocked3);
            for (int i2 = 0; i2 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i2++) {
                if (this.mJobsFreshnessBuckets[i2] == null) {
                    sb.append(",0");
                } else {
                    sb.append(",");
                    sb.append(this.mJobsFreshnessBuckets[i2].getCountLocked(i));
                }
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void getDeferredJobsLineLocked(StringBuilder sb, int i) {
            sb.setLength(0);
            int countLocked = this.mJobsDeferredEventCount.getCountLocked(i);
            if (countLocked == 0) {
                return;
            }
            int countLocked2 = this.mJobsDeferredCount.getCountLocked(i);
            long countLocked3 = this.mJobsFreshnessTimeMs.getCountLocked(i);
            sb.append("times=");
            sb.append(countLocked);
            sb.append(", ");
            sb.append("count=");
            sb.append(countLocked2);
            sb.append(", ");
            sb.append("totalLatencyMs=");
            sb.append(countLocked3);
            sb.append(", ");
            for (int i2 = 0; i2 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i2++) {
                sb.append("<");
                sb.append(BatteryStats.JOB_FRESHNESS_BUCKETS[i2]);
                sb.append("ms=");
                Counter[] counterArr = this.mJobsFreshnessBuckets;
                if (counterArr[i2] == null) {
                    sb.append("0");
                } else {
                    sb.append(counterArr[i2].getCountLocked(i));
                }
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            }
        }

        void initNetworkActivityLocked() {
            this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
            this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
            for (int i = 0; i < 10; i++) {
                this.mNetworkByteActivityCounters[i] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
                this.mNetworkPacketActivityCounters[i] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mMobileRadioActiveTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mMobileRadioActiveCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
        }

        public boolean reset(long j, long j2) {
            this.mOnBatteryBackgroundTimeBase.init(j, j2);
            this.mOnBatteryScreenOffBackgroundTimeBase.init(j, j2);
            StopwatchTimer stopwatchTimer = this.mWifiRunningTimer;
            boolean z = stopwatchTimer != null ? (!stopwatchTimer.reset(false)) | false | this.mWifiRunning : false;
            StopwatchTimer stopwatchTimer2 = this.mFullWifiLockTimer;
            if (stopwatchTimer2 != null) {
                z = z | (!stopwatchTimer2.reset(false)) | this.mFullWifiLockOut;
            }
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer != null) {
                z = z | (!dualTimer.reset(false)) | this.mWifiScanStarted;
            }
            if (this.mWifiBatchedScanTimer != null) {
                for (int i = 0; i < 5; i++) {
                    StopwatchTimer[] stopwatchTimerArr = this.mWifiBatchedScanTimer;
                    if (stopwatchTimerArr[i] != null) {
                        z |= !stopwatchTimerArr[i].reset(false);
                    }
                }
                z |= this.mWifiBatchedScanBinStarted != -1;
            }
            StopwatchTimer stopwatchTimer3 = this.mWifiMulticastTimer;
            if (stopwatchTimer3 != null) {
                z = z | (!stopwatchTimer3.reset(false)) | this.mWifiMulticastEnabled;
            }
            boolean z2 = z | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mAudioTurnedOnTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mVideoTurnedOnTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mFlashlightTurnedOnTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mCameraTurnedOnTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mForegroundActivityTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull((Timer) this.mForegroundServiceTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull(this.mAggregatedPartialWakelockTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull(this.mBluetoothScanTimer, false)) | (!BatteryStatsImpl.resetTimerIfNotNull(this.mBluetoothUnoptimizedScanTimer, false));
            Counter counter = this.mBluetoothScanResultCounter;
            if (counter != null) {
                counter.reset(false);
            }
            Counter counter2 = this.mBluetoothScanResultBgCounter;
            if (counter2 != null) {
                counter2.reset(false);
            }
            if (this.mProcessStateTimer != null) {
                for (int i2 = 0; i2 < 7; i2++) {
                    StopwatchTimer[] stopwatchTimerArr2 = this.mProcessStateTimer;
                    if (stopwatchTimerArr2[i2] != null) {
                        z2 |= !stopwatchTimerArr2[i2].reset(false);
                    }
                }
                z2 |= this.mProcessState != 19;
            }
            BatchTimer batchTimer = this.mVibratorOnTimer;
            if (batchTimer != null) {
                if (batchTimer.reset(false)) {
                    this.mVibratorOnTimer.detach();
                    this.mVibratorOnTimer = null;
                } else {
                    z2 = true;
                }
            }
            if (this.mUserActivityCounters != null) {
                for (int i3 = 0; i3 < 4; i3++) {
                    this.mUserActivityCounters[i3].reset(false);
                }
            }
            if (this.mNetworkByteActivityCounters != null) {
                for (int i4 = 0; i4 < 10; i4++) {
                    this.mNetworkByteActivityCounters[i4].reset(false);
                    this.mNetworkPacketActivityCounters[i4].reset(false);
                }
                this.mMobileRadioActiveTime.reset(false);
                this.mMobileRadioActiveCount.reset(false);
            }
            ControllerActivityCounterImpl controllerActivityCounterImpl = this.mWifiControllerActivity;
            if (controllerActivityCounterImpl != null) {
                controllerActivityCounterImpl.reset(false);
            }
            ControllerActivityCounterImpl controllerActivityCounterImpl2 = this.mBluetoothControllerActivity;
            if (controllerActivityCounterImpl2 != null) {
                controllerActivityCounterImpl2.reset(false);
            }
            ControllerActivityCounterImpl controllerActivityCounterImpl3 = this.mModemControllerActivity;
            if (controllerActivityCounterImpl3 != null) {
                controllerActivityCounterImpl3.reset(false);
            }
            this.mUserCpuTime.reset(false);
            this.mSystemCpuTime.reset(false);
            LongSamplingCounter[][] longSamplingCounterArr = this.mCpuClusterSpeedTimesUs;
            if (longSamplingCounterArr != null) {
                for (LongSamplingCounter[] longSamplingCounterArr2 : longSamplingCounterArr) {
                    if (longSamplingCounterArr2 != null) {
                        for (LongSamplingCounter longSamplingCounter : longSamplingCounterArr2) {
                            if (longSamplingCounter != null) {
                                longSamplingCounter.reset(false);
                            }
                        }
                    }
                }
            }
            LongSamplingCounterArray longSamplingCounterArray = this.mCpuFreqTimeMs;
            if (longSamplingCounterArray != null) {
                longSamplingCounterArray.reset(false);
            }
            LongSamplingCounterArray longSamplingCounterArray2 = this.mScreenOffCpuFreqTimeMs;
            if (longSamplingCounterArray2 != null) {
                longSamplingCounterArray2.reset(false);
            }
            this.mCpuActiveTimeMs.reset(false);
            this.mCpuClusterTimesMs.reset(false);
            LongSamplingCounterArray[] longSamplingCounterArrayArr = this.mProcStateTimeMs;
            if (longSamplingCounterArrayArr != null) {
                for (LongSamplingCounterArray longSamplingCounterArray3 : longSamplingCounterArrayArr) {
                    if (longSamplingCounterArray3 != null) {
                        longSamplingCounterArray3.reset(false);
                    }
                }
            }
            LongSamplingCounterArray[] longSamplingCounterArrayArr2 = this.mProcStateScreenOffTimeMs;
            if (longSamplingCounterArrayArr2 != null) {
                for (LongSamplingCounterArray longSamplingCounterArray4 : longSamplingCounterArrayArr2) {
                    if (longSamplingCounterArray4 != null) {
                        longSamplingCounterArray4.reset(false);
                    }
                }
            }
            BatteryStatsImpl.resetLongCounterIfNotNull(this.mMobileRadioApWakeupCount, false);
            BatteryStatsImpl.resetLongCounterIfNotNull(this.mWifiRadioApWakeupCount, false);
            ArrayMap<String, Wakelock> map = this.mWakelockStats.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                if (map.valueAt(size).reset()) {
                    map.removeAt(size);
                } else {
                    z2 = true;
                }
            }
            this.mWakelockStats.cleanup();
            ArrayMap<String, DualTimer> map2 = this.mSyncStats.getMap();
            for (int size2 = map2.size() - 1; size2 >= 0; size2--) {
                DualTimer dualTimerValueAt = map2.valueAt(size2);
                if (dualTimerValueAt.reset(false)) {
                    map2.removeAt(size2);
                    dualTimerValueAt.detach();
                } else {
                    z2 = true;
                }
            }
            this.mSyncStats.cleanup();
            ArrayMap<String, DualTimer> map3 = this.mJobStats.getMap();
            for (int size3 = map3.size() - 1; size3 >= 0; size3--) {
                DualTimer dualTimerValueAt2 = map3.valueAt(size3);
                if (dualTimerValueAt2.reset(false)) {
                    map3.removeAt(size3);
                    dualTimerValueAt2.detach();
                } else {
                    z2 = true;
                }
            }
            this.mJobStats.cleanup();
            this.mJobCompletions.clear();
            this.mJobsDeferredEventCount.reset(false);
            this.mJobsDeferredCount.reset(false);
            this.mJobsFreshnessTimeMs.reset(false);
            for (int i5 = 0; i5 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i5++) {
                Counter[] counterArr = this.mJobsFreshnessBuckets;
                if (counterArr[i5] != null) {
                    counterArr[i5].reset(false);
                }
            }
            for (int size4 = this.mSensorStats.size() - 1; size4 >= 0; size4--) {
                if (this.mSensorStats.valueAt(size4).reset()) {
                    this.mSensorStats.removeAt(size4);
                } else {
                    z2 = true;
                }
            }
            for (int size5 = this.mProcessStats.size() - 1; size5 >= 0; size5--) {
                this.mProcessStats.valueAt(size5).detach();
            }
            this.mProcessStats.clear();
            if (this.mPids.size() > 0) {
                for (int size6 = this.mPids.size() - 1; size6 >= 0; size6--) {
                    if (this.mPids.valueAt(size6).mWakeNesting > 0) {
                        z2 = true;
                    } else {
                        this.mPids.removeAt(size6);
                    }
                }
            }
            if (this.mPackageStats.size() > 0) {
                Iterator<Map.Entry<String, Pkg>> it = this.mPackageStats.entrySet().iterator();
                while (it.hasNext()) {
                    Pkg value = it.next().getValue();
                    value.detach();
                    if (value.mServiceStats.size() > 0) {
                        Iterator<Map.Entry<String, Pkg.Serv>> it2 = value.mServiceStats.entrySet().iterator();
                        while (it2.hasNext()) {
                            it2.next().getValue().detach();
                        }
                    }
                }
                this.mPackageStats.clear();
            }
            this.mLastStepSystemTime = 0L;
            this.mLastStepUserTime = 0L;
            this.mCurStepSystemTime = 0L;
            this.mCurStepUserTime = 0L;
            if (!z2) {
                StopwatchTimer stopwatchTimer4 = this.mWifiRunningTimer;
                if (stopwatchTimer4 != null) {
                    stopwatchTimer4.detach();
                }
                StopwatchTimer stopwatchTimer5 = this.mFullWifiLockTimer;
                if (stopwatchTimer5 != null) {
                    stopwatchTimer5.detach();
                }
                DualTimer dualTimer2 = this.mWifiScanTimer;
                if (dualTimer2 != null) {
                    dualTimer2.detach();
                }
                for (int i6 = 0; i6 < 5; i6++) {
                    StopwatchTimer[] stopwatchTimerArr3 = this.mWifiBatchedScanTimer;
                    if (stopwatchTimerArr3[i6] != null) {
                        stopwatchTimerArr3[i6].detach();
                    }
                }
                StopwatchTimer stopwatchTimer6 = this.mWifiMulticastTimer;
                if (stopwatchTimer6 != null) {
                    stopwatchTimer6.detach();
                }
                StopwatchTimer stopwatchTimer7 = this.mAudioTurnedOnTimer;
                if (stopwatchTimer7 != null) {
                    stopwatchTimer7.detach();
                    this.mAudioTurnedOnTimer = null;
                }
                StopwatchTimer stopwatchTimer8 = this.mVideoTurnedOnTimer;
                if (stopwatchTimer8 != null) {
                    stopwatchTimer8.detach();
                    this.mVideoTurnedOnTimer = null;
                }
                StopwatchTimer stopwatchTimer9 = this.mFlashlightTurnedOnTimer;
                if (stopwatchTimer9 != null) {
                    stopwatchTimer9.detach();
                    this.mFlashlightTurnedOnTimer = null;
                }
                StopwatchTimer stopwatchTimer10 = this.mCameraTurnedOnTimer;
                if (stopwatchTimer10 != null) {
                    stopwatchTimer10.detach();
                    this.mCameraTurnedOnTimer = null;
                }
                StopwatchTimer stopwatchTimer11 = this.mForegroundActivityTimer;
                if (stopwatchTimer11 != null) {
                    stopwatchTimer11.detach();
                    this.mForegroundActivityTimer = null;
                }
                StopwatchTimer stopwatchTimer12 = this.mForegroundServiceTimer;
                if (stopwatchTimer12 != null) {
                    stopwatchTimer12.detach();
                    this.mForegroundServiceTimer = null;
                }
                DualTimer dualTimer3 = this.mAggregatedPartialWakelockTimer;
                if (dualTimer3 != null) {
                    dualTimer3.detach();
                    this.mAggregatedPartialWakelockTimer = null;
                }
                DualTimer dualTimer4 = this.mBluetoothScanTimer;
                if (dualTimer4 != null) {
                    dualTimer4.detach();
                    this.mBluetoothScanTimer = null;
                }
                DualTimer dualTimer5 = this.mBluetoothUnoptimizedScanTimer;
                if (dualTimer5 != null) {
                    dualTimer5.detach();
                    this.mBluetoothUnoptimizedScanTimer = null;
                }
                Counter counter3 = this.mBluetoothScanResultCounter;
                if (counter3 != null) {
                    counter3.detach();
                    this.mBluetoothScanResultCounter = null;
                }
                Counter counter4 = this.mBluetoothScanResultBgCounter;
                if (counter4 != null) {
                    counter4.detach();
                    this.mBluetoothScanResultBgCounter = null;
                }
                if (this.mUserActivityCounters != null) {
                    for (int i7 = 0; i7 < 4; i7++) {
                        this.mUserActivityCounters[i7].detach();
                    }
                }
                if (this.mNetworkByteActivityCounters != null) {
                    for (int i8 = 0; i8 < 10; i8++) {
                        this.mNetworkByteActivityCounters[i8].detach();
                        this.mNetworkPacketActivityCounters[i8].detach();
                    }
                }
                ControllerActivityCounterImpl controllerActivityCounterImpl4 = this.mWifiControllerActivity;
                if (controllerActivityCounterImpl4 != null) {
                    controllerActivityCounterImpl4.detach();
                }
                ControllerActivityCounterImpl controllerActivityCounterImpl5 = this.mBluetoothControllerActivity;
                if (controllerActivityCounterImpl5 != null) {
                    controllerActivityCounterImpl5.detach();
                }
                ControllerActivityCounterImpl controllerActivityCounterImpl6 = this.mModemControllerActivity;
                if (controllerActivityCounterImpl6 != null) {
                    controllerActivityCounterImpl6.detach();
                }
                this.mPids.clear();
                this.mUserCpuTime.detach();
                this.mSystemCpuTime.detach();
                LongSamplingCounter[][] longSamplingCounterArr3 = this.mCpuClusterSpeedTimesUs;
                if (longSamplingCounterArr3 != null) {
                    for (LongSamplingCounter[] longSamplingCounterArr4 : longSamplingCounterArr3) {
                        if (longSamplingCounterArr4 != null) {
                            for (LongSamplingCounter longSamplingCounter2 : longSamplingCounterArr4) {
                                if (longSamplingCounter2 != null) {
                                    longSamplingCounter2.detach();
                                }
                            }
                        }
                    }
                }
                LongSamplingCounterArray longSamplingCounterArray5 = this.mCpuFreqTimeMs;
                if (longSamplingCounterArray5 != null) {
                    longSamplingCounterArray5.detach();
                }
                LongSamplingCounterArray longSamplingCounterArray6 = this.mScreenOffCpuFreqTimeMs;
                if (longSamplingCounterArray6 != null) {
                    longSamplingCounterArray6.detach();
                }
                this.mCpuActiveTimeMs.detach();
                this.mCpuClusterTimesMs.detach();
                LongSamplingCounterArray[] longSamplingCounterArrayArr3 = this.mProcStateTimeMs;
                if (longSamplingCounterArrayArr3 != null) {
                    for (LongSamplingCounterArray longSamplingCounterArray7 : longSamplingCounterArrayArr3) {
                        if (longSamplingCounterArray7 != null) {
                            longSamplingCounterArray7.detach();
                        }
                    }
                }
                LongSamplingCounterArray[] longSamplingCounterArrayArr4 = this.mProcStateScreenOffTimeMs;
                if (longSamplingCounterArrayArr4 != null) {
                    for (LongSamplingCounterArray longSamplingCounterArray8 : longSamplingCounterArrayArr4) {
                        if (longSamplingCounterArray8 != null) {
                            longSamplingCounterArray8.detach();
                        }
                    }
                }
                BatteryStatsImpl.detachLongCounterIfNotNull(this.mMobileRadioApWakeupCount);
                BatteryStatsImpl.detachLongCounterIfNotNull(this.mWifiRadioApWakeupCount);
            }
            return !z2;
        }

        void writeJobCompletionsToParcelLocked(Parcel parcel) {
            int size = this.mJobCompletions.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                parcel.writeString(this.mJobCompletions.keyAt(i));
                SparseIntArray sparseIntArrayValueAt = this.mJobCompletions.valueAt(i);
                int size2 = sparseIntArrayValueAt.size();
                parcel.writeInt(size2);
                for (int i2 = 0; i2 < size2; i2++) {
                    parcel.writeInt(sparseIntArrayValueAt.keyAt(i2));
                    parcel.writeInt(sparseIntArrayValueAt.valueAt(i2));
                }
            }
        }

        void writeToParcelLocked(Parcel parcel, long j, long j2) {
            this.mOnBatteryBackgroundTimeBase.writeToParcel(parcel, j, j2);
            this.mOnBatteryScreenOffBackgroundTimeBase.writeToParcel(parcel, j, j2);
            ArrayMap<String, Wakelock> map = this.mWakelockStats.getMap();
            int size = map.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                parcel.writeString(map.keyAt(i));
                map.valueAt(i).writeToParcelLocked(parcel, j2);
            }
            ArrayMap<String, DualTimer> map2 = this.mSyncStats.getMap();
            int size2 = map2.size();
            parcel.writeInt(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                parcel.writeString(map2.keyAt(i2));
                Timer.writeTimerToParcel(parcel, map2.valueAt(i2), j2);
            }
            ArrayMap<String, DualTimer> map3 = this.mJobStats.getMap();
            int size3 = map3.size();
            parcel.writeInt(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                parcel.writeString(map3.keyAt(i3));
                Timer.writeTimerToParcel(parcel, map3.valueAt(i3), j2);
            }
            writeJobCompletionsToParcelLocked(parcel);
            this.mJobsDeferredEventCount.writeToParcel(parcel);
            this.mJobsDeferredCount.writeToParcel(parcel);
            this.mJobsFreshnessTimeMs.writeToParcel(parcel);
            for (int i4 = 0; i4 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i4++) {
                Counter.writeCounterToParcel(parcel, this.mJobsFreshnessBuckets[i4]);
            }
            int size4 = this.mSensorStats.size();
            parcel.writeInt(size4);
            for (int i5 = 0; i5 < size4; i5++) {
                parcel.writeInt(this.mSensorStats.keyAt(i5));
                this.mSensorStats.valueAt(i5).writeToParcelLocked(parcel, j2);
            }
            int size5 = this.mProcessStats.size();
            parcel.writeInt(size5);
            for (int i6 = 0; i6 < size5; i6++) {
                parcel.writeString(this.mProcessStats.keyAt(i6));
                this.mProcessStats.valueAt(i6).writeToParcelLocked(parcel);
            }
            parcel.writeInt(this.mPackageStats.size());
            for (Map.Entry<String, Pkg> entry : this.mPackageStats.entrySet()) {
                parcel.writeString(entry.getKey());
                entry.getValue().writeToParcelLocked(parcel);
            }
            if (this.mWifiRunningTimer != null) {
                parcel.writeInt(1);
                this.mWifiRunningTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mFullWifiLockTimer != null) {
                parcel.writeInt(1);
                this.mFullWifiLockTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mWifiScanTimer != null) {
                parcel.writeInt(1);
                this.mWifiScanTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            for (int i7 = 0; i7 < 5; i7++) {
                if (this.mWifiBatchedScanTimer[i7] != null) {
                    parcel.writeInt(1);
                    this.mWifiBatchedScanTimer[i7].writeToParcel(parcel, j2);
                } else {
                    parcel.writeInt(0);
                }
            }
            if (this.mWifiMulticastTimer != null) {
                parcel.writeInt(1);
                this.mWifiMulticastTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mAudioTurnedOnTimer != null) {
                parcel.writeInt(1);
                this.mAudioTurnedOnTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mVideoTurnedOnTimer != null) {
                parcel.writeInt(1);
                this.mVideoTurnedOnTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mFlashlightTurnedOnTimer != null) {
                parcel.writeInt(1);
                this.mFlashlightTurnedOnTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mCameraTurnedOnTimer != null) {
                parcel.writeInt(1);
                this.mCameraTurnedOnTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mForegroundActivityTimer != null) {
                parcel.writeInt(1);
                this.mForegroundActivityTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mForegroundServiceTimer != null) {
                parcel.writeInt(1);
                this.mForegroundServiceTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mAggregatedPartialWakelockTimer != null) {
                parcel.writeInt(1);
                this.mAggregatedPartialWakelockTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mBluetoothScanTimer != null) {
                parcel.writeInt(1);
                this.mBluetoothScanTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mBluetoothUnoptimizedScanTimer != null) {
                parcel.writeInt(1);
                this.mBluetoothUnoptimizedScanTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mBluetoothScanResultCounter != null) {
                parcel.writeInt(1);
                this.mBluetoothScanResultCounter.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
            if (this.mBluetoothScanResultBgCounter != null) {
                parcel.writeInt(1);
                this.mBluetoothScanResultBgCounter.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
            for (int i8 = 0; i8 < 7; i8++) {
                if (this.mProcessStateTimer[i8] != null) {
                    parcel.writeInt(1);
                    this.mProcessStateTimer[i8].writeToParcel(parcel, j2);
                } else {
                    parcel.writeInt(0);
                }
            }
            if (this.mVibratorOnTimer != null) {
                parcel.writeInt(1);
                this.mVibratorOnTimer.writeToParcel(parcel, j2);
            } else {
                parcel.writeInt(0);
            }
            if (this.mUserActivityCounters != null) {
                parcel.writeInt(1);
                for (int i9 = 0; i9 < 4; i9++) {
                    this.mUserActivityCounters[i9].writeToParcel(parcel);
                }
            } else {
                parcel.writeInt(0);
            }
            if (this.mNetworkByteActivityCounters != null) {
                parcel.writeInt(1);
                for (int i10 = 0; i10 < 10; i10++) {
                    this.mNetworkByteActivityCounters[i10].writeToParcel(parcel);
                    this.mNetworkPacketActivityCounters[i10].writeToParcel(parcel);
                }
                this.mMobileRadioActiveTime.writeToParcel(parcel);
                this.mMobileRadioActiveCount.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
            if (this.mWifiControllerActivity != null) {
                parcel.writeInt(1);
                this.mWifiControllerActivity.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            if (this.mBluetoothControllerActivity != null) {
                parcel.writeInt(1);
                this.mBluetoothControllerActivity.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            if (this.mModemControllerActivity != null) {
                parcel.writeInt(1);
                this.mModemControllerActivity.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            this.mUserCpuTime.writeToParcel(parcel);
            this.mSystemCpuTime.writeToParcel(parcel);
            if (this.mCpuClusterSpeedTimesUs != null) {
                parcel.writeInt(1);
                parcel.writeInt(this.mCpuClusterSpeedTimesUs.length);
                for (LongSamplingCounter[] longSamplingCounterArr : this.mCpuClusterSpeedTimesUs) {
                    if (longSamplingCounterArr != null) {
                        parcel.writeInt(1);
                        parcel.writeInt(longSamplingCounterArr.length);
                        for (LongSamplingCounter longSamplingCounter : longSamplingCounterArr) {
                            if (longSamplingCounter != null) {
                                parcel.writeInt(1);
                                longSamplingCounter.writeToParcel(parcel);
                            } else {
                                parcel.writeInt(0);
                            }
                        }
                    } else {
                        parcel.writeInt(0);
                    }
                }
            } else {
                parcel.writeInt(0);
            }
            LongSamplingCounterArray.writeToParcel(parcel, this.mCpuFreqTimeMs);
            LongSamplingCounterArray.writeToParcel(parcel, this.mScreenOffCpuFreqTimeMs);
            this.mCpuActiveTimeMs.writeToParcel(parcel);
            this.mCpuClusterTimesMs.writeToParcel(parcel);
            LongSamplingCounterArray[] longSamplingCounterArrayArr = this.mProcStateTimeMs;
            if (longSamplingCounterArrayArr != null) {
                parcel.writeInt(longSamplingCounterArrayArr.length);
                for (LongSamplingCounterArray longSamplingCounterArray : this.mProcStateTimeMs) {
                    LongSamplingCounterArray.writeToParcel(parcel, longSamplingCounterArray);
                }
            } else {
                parcel.writeInt(0);
            }
            LongSamplingCounterArray[] longSamplingCounterArrayArr2 = this.mProcStateScreenOffTimeMs;
            if (longSamplingCounterArrayArr2 != null) {
                parcel.writeInt(longSamplingCounterArrayArr2.length);
                for (LongSamplingCounterArray longSamplingCounterArray2 : this.mProcStateScreenOffTimeMs) {
                    LongSamplingCounterArray.writeToParcel(parcel, longSamplingCounterArray2);
                }
            } else {
                parcel.writeInt(0);
            }
            if (this.mMobileRadioApWakeupCount != null) {
                parcel.writeInt(1);
                this.mMobileRadioApWakeupCount.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
            if (this.mWifiRadioApWakeupCount != null) {
                parcel.writeInt(1);
                this.mWifiRadioApWakeupCount.writeToParcel(parcel);
            } else {
                parcel.writeInt(0);
            }
        }

        void readJobCompletionsFromParcelLocked(Parcel parcel) {
            int i = parcel.readInt();
            this.mJobCompletions.clear();
            for (int i2 = 0; i2 < i; i2++) {
                String string = parcel.readString();
                int i3 = parcel.readInt();
                if (i3 > 0) {
                    SparseIntArray sparseIntArray = new SparseIntArray();
                    for (int i4 = 0; i4 < i3; i4++) {
                        sparseIntArray.put(parcel.readInt(), parcel.readInt());
                    }
                    this.mJobCompletions.put(string, sparseIntArray);
                }
            }
        }

        void readFromParcelLocked(TimeBase timeBase, TimeBase timeBase2, Parcel parcel) {
            this.mOnBatteryBackgroundTimeBase.readFromParcel(parcel);
            this.mOnBatteryScreenOffBackgroundTimeBase.readFromParcel(parcel);
            int i = parcel.readInt();
            this.mWakelockStats.clear();
            for (int i2 = 0; i2 < i; i2++) {
                String string = parcel.readString();
                Wakelock wakelock = new Wakelock(this.mBsi, this);
                wakelock.readFromParcelLocked(timeBase, timeBase2, this.mOnBatteryScreenOffBackgroundTimeBase, parcel);
                this.mWakelockStats.add(string, wakelock);
            }
            int i3 = parcel.readInt();
            this.mSyncStats.clear();
            for (int i4 = 0; i4 < i3; i4++) {
                String string2 = parcel.readString();
                if (parcel.readInt() != 0) {
                    this.mSyncStats.add(string2, new DualTimer(this.mBsi.mClocks, this, 13, null, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel));
                }
            }
            int i5 = parcel.readInt();
            this.mJobStats.clear();
            for (int i6 = 0; i6 < i5; i6++) {
                String string3 = parcel.readString();
                if (parcel.readInt() != 0) {
                    this.mJobStats.add(string3, new DualTimer(this.mBsi.mClocks, this, 14, null, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel));
                }
            }
            readJobCompletionsFromParcelLocked(parcel);
            this.mJobsDeferredEventCount = new Counter(this.mBsi.mOnBatteryTimeBase, parcel);
            this.mJobsDeferredCount = new Counter(this.mBsi.mOnBatteryTimeBase, parcel);
            this.mJobsFreshnessTimeMs = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            for (int i7 = 0; i7 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i7++) {
                this.mJobsFreshnessBuckets[i7] = Counter.readCounterFromParcel(this.mBsi.mOnBatteryTimeBase, parcel);
            }
            int i8 = parcel.readInt();
            this.mSensorStats.clear();
            for (int i9 = 0; i9 < i8; i9++) {
                int i10 = parcel.readInt();
                Sensor sensor = new Sensor(this.mBsi, this, i10);
                sensor.readFromParcelLocked(this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel);
                this.mSensorStats.put(i10, sensor);
            }
            int i11 = parcel.readInt();
            this.mProcessStats.clear();
            for (int i12 = 0; i12 < i11; i12++) {
                String string4 = parcel.readString();
                Proc proc = new Proc(this.mBsi, string4);
                proc.readFromParcelLocked(parcel);
                this.mProcessStats.put(string4, proc);
            }
            int i13 = parcel.readInt();
            this.mPackageStats.clear();
            for (int i14 = 0; i14 < i13; i14++) {
                String string5 = parcel.readString();
                Pkg pkg = new Pkg(this.mBsi);
                pkg.readFromParcelLocked(parcel);
                this.mPackageStats.put(string5, pkg);
            }
            this.mWifiRunning = false;
            if (parcel.readInt() != 0) {
                this.mWifiRunningTimer = new StopwatchTimer(this.mBsi.mClocks, this, 4, this.mBsi.mWifiRunningTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mWifiRunningTimer = null;
            }
            this.mFullWifiLockOut = false;
            if (parcel.readInt() != 0) {
                this.mFullWifiLockTimer = new StopwatchTimer(this.mBsi.mClocks, this, 5, this.mBsi.mFullWifiLockTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mFullWifiLockTimer = null;
            }
            this.mWifiScanStarted = false;
            if (parcel.readInt() != 0) {
                this.mWifiScanTimer = new DualTimer(this.mBsi.mClocks, this, 6, this.mBsi.mWifiScanTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel);
            } else {
                this.mWifiScanTimer = null;
            }
            this.mWifiBatchedScanBinStarted = -1;
            for (int i15 = 0; i15 < 5; i15++) {
                if (parcel.readInt() != 0) {
                    makeWifiBatchedScanBin(i15, parcel);
                } else {
                    this.mWifiBatchedScanTimer[i15] = null;
                }
            }
            this.mWifiMulticastEnabled = false;
            if (parcel.readInt() != 0) {
                this.mWifiMulticastTimer = new StopwatchTimer(this.mBsi.mClocks, this, 7, this.mBsi.mWifiMulticastTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mWifiMulticastTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mAudioTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 15, this.mBsi.mAudioTurnedOnTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mAudioTurnedOnTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mVideoTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 8, this.mBsi.mVideoTurnedOnTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mVideoTurnedOnTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mFlashlightTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 16, this.mBsi.mFlashlightTurnedOnTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mFlashlightTurnedOnTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mCameraTurnedOnTimer = new StopwatchTimer(this.mBsi.mClocks, this, 17, this.mBsi.mCameraTurnedOnTimers, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mCameraTurnedOnTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mForegroundActivityTimer = new StopwatchTimer(this.mBsi.mClocks, this, 10, null, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mForegroundActivityTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mForegroundServiceTimer = new StopwatchTimer(this.mBsi.mClocks, this, 22, null, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mForegroundServiceTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mAggregatedPartialWakelockTimer = new DualTimer(this.mBsi.mClocks, this, 20, null, this.mBsi.mOnBatteryScreenOffTimeBase, this.mOnBatteryScreenOffBackgroundTimeBase, parcel);
            } else {
                this.mAggregatedPartialWakelockTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mBluetoothScanTimer = new DualTimer(this.mBsi.mClocks, this, 19, this.mBsi.mBluetoothScanOnTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel);
            } else {
                this.mBluetoothScanTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mBluetoothUnoptimizedScanTimer = new DualTimer(this.mBsi.mClocks, this, 21, null, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase, parcel);
            } else {
                this.mBluetoothUnoptimizedScanTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mBluetoothScanResultCounter = new Counter(this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mBluetoothScanResultCounter = null;
            }
            if (parcel.readInt() != 0) {
                this.mBluetoothScanResultBgCounter = new Counter(this.mOnBatteryBackgroundTimeBase, parcel);
            } else {
                this.mBluetoothScanResultBgCounter = null;
            }
            this.mProcessState = 19;
            for (int i16 = 0; i16 < 7; i16++) {
                if (parcel.readInt() != 0) {
                    makeProcessState(i16, parcel);
                } else {
                    this.mProcessStateTimer[i16] = null;
                }
            }
            if (parcel.readInt() != 0) {
                this.mVibratorOnTimer = new BatchTimer(this.mBsi.mClocks, this, 9, this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mVibratorOnTimer = null;
            }
            if (parcel.readInt() != 0) {
                this.mUserActivityCounters = new Counter[4];
                for (int i17 = 0; i17 < 4; i17++) {
                    this.mUserActivityCounters[i17] = new Counter(this.mBsi.mOnBatteryTimeBase, parcel);
                }
            } else {
                this.mUserActivityCounters = null;
            }
            if (parcel.readInt() != 0) {
                this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
                this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
                for (int i18 = 0; i18 < 10; i18++) {
                    this.mNetworkByteActivityCounters[i18] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
                    this.mNetworkPacketActivityCounters[i18] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
                }
                this.mMobileRadioActiveTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
                this.mMobileRadioActiveCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mNetworkByteActivityCounters = null;
                this.mNetworkPacketActivityCounters = null;
            }
            if (parcel.readInt() != 0) {
                this.mWifiControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 1, parcel);
            } else {
                this.mWifiControllerActivity = null;
            }
            if (parcel.readInt() != 0) {
                this.mBluetoothControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 1, parcel);
            } else {
                this.mBluetoothControllerActivity = null;
            }
            if (parcel.readInt() != 0) {
                this.mModemControllerActivity = new ControllerActivityCounterImpl(this.mBsi.mOnBatteryTimeBase, 5, parcel);
            } else {
                this.mModemControllerActivity = null;
            }
            this.mUserCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            this.mSystemCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            if (parcel.readInt() != 0) {
                int i19 = parcel.readInt();
                if (this.mBsi.mPowerProfile != null && this.mBsi.mPowerProfile.getNumCpuClusters() != i19) {
                    throw new ParcelFormatException("Incompatible number of cpu clusters");
                }
                this.mCpuClusterSpeedTimesUs = new LongSamplingCounter[i19][];
                for (int i20 = 0; i20 < i19; i20++) {
                    if (parcel.readInt() != 0) {
                        int i21 = parcel.readInt();
                        if (this.mBsi.mPowerProfile != null && this.mBsi.mPowerProfile.getNumSpeedStepsInCpuCluster(i20) != i21) {
                            throw new ParcelFormatException("Incompatible number of cpu speeds");
                        }
                        LongSamplingCounter[] longSamplingCounterArr = new LongSamplingCounter[i21];
                        this.mCpuClusterSpeedTimesUs[i20] = longSamplingCounterArr;
                        for (int i22 = 0; i22 < i21; i22++) {
                            if (parcel.readInt() != 0) {
                                longSamplingCounterArr[i22] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
                            }
                        }
                    } else {
                        this.mCpuClusterSpeedTimesUs[i20] = null;
                    }
                }
            } else {
                this.mCpuClusterSpeedTimesUs = null;
            }
            this.mCpuFreqTimeMs = LongSamplingCounterArray.readFromParcel(parcel, this.mBsi.mOnBatteryTimeBase);
            this.mScreenOffCpuFreqTimeMs = LongSamplingCounterArray.readFromParcel(parcel, this.mBsi.mOnBatteryScreenOffTimeBase);
            this.mCpuActiveTimeMs = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            this.mCpuClusterTimesMs = new LongSamplingCounterArray(this.mBsi.mOnBatteryTimeBase, parcel);
            int i23 = parcel.readInt();
            if (i23 == 7) {
                this.mProcStateTimeMs = new LongSamplingCounterArray[i23];
                for (int i24 = 0; i24 < i23; i24++) {
                    this.mProcStateTimeMs[i24] = LongSamplingCounterArray.readFromParcel(parcel, this.mBsi.mOnBatteryTimeBase);
                }
            } else {
                this.mProcStateTimeMs = null;
            }
            int i25 = parcel.readInt();
            if (i25 == 7) {
                this.mProcStateScreenOffTimeMs = new LongSamplingCounterArray[i25];
                for (int i26 = 0; i26 < i25; i26++) {
                    this.mProcStateScreenOffTimeMs[i26] = LongSamplingCounterArray.readFromParcel(parcel, this.mBsi.mOnBatteryScreenOffTimeBase);
                }
            } else {
                this.mProcStateScreenOffTimeMs = null;
            }
            if (parcel.readInt() != 0) {
                this.mMobileRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mMobileRadioApWakeupCount = null;
            }
            if (parcel.readInt() != 0) {
                this.mWifiRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase, parcel);
            } else {
                this.mWifiRadioApWakeupCount = null;
            }
        }

        public void noteJobsDeferredLocked(int i, long j) {
            this.mJobsDeferredEventCount.addAtomic(1);
            this.mJobsDeferredCount.addAtomic(i);
            if (j != 0) {
                this.mJobsFreshnessTimeMs.addCountLocked(j);
                for (int i2 = 0; i2 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i2++) {
                    if (j < BatteryStats.JOB_FRESHNESS_BUCKETS[i2]) {
                        Counter[] counterArr = this.mJobsFreshnessBuckets;
                        if (counterArr[i2] == null) {
                            counterArr[i2] = new Counter(this.mBsi.mOnBatteryTimeBase);
                        }
                        this.mJobsFreshnessBuckets[i2].addAtomic(1);
                        return;
                    }
                }
            }
        }

        public static class Wakelock extends BatteryStats.Uid.Wakelock {
            protected BatteryStatsImpl mBsi;
            StopwatchTimer mTimerDraw;
            StopwatchTimer mTimerFull;
            DualTimer mTimerPartial;
            StopwatchTimer mTimerWindow;
            protected Uid mUid;

            public Wakelock(BatteryStatsImpl batteryStatsImpl, Uid uid) {
                this.mBsi = batteryStatsImpl;
                this.mUid = uid;
            }

            private StopwatchTimer readStopwatchTimerFromParcel(int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, Parcel parcel) {
                if (parcel.readInt() == 0) {
                    return null;
                }
                return new StopwatchTimer(this.mBsi.mClocks, this.mUid, i, arrayList, timeBase, parcel);
            }

            private DualTimer readDualTimerFromParcel(int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, TimeBase timeBase2, Parcel parcel) {
                if (parcel.readInt() == 0) {
                    return null;
                }
                return new DualTimer(this.mBsi.mClocks, this.mUid, i, arrayList, timeBase, timeBase2, parcel);
            }

            boolean reset() {
                StopwatchTimer stopwatchTimer = this.mTimerFull;
                boolean z = stopwatchTimer != null ? (!stopwatchTimer.reset(false)) | false : false;
                DualTimer dualTimer = this.mTimerPartial;
                if (dualTimer != null) {
                    z |= !dualTimer.reset(false);
                }
                StopwatchTimer stopwatchTimer2 = this.mTimerWindow;
                if (stopwatchTimer2 != null) {
                    z |= !stopwatchTimer2.reset(false);
                }
                StopwatchTimer stopwatchTimer3 = this.mTimerDraw;
                if (stopwatchTimer3 != null) {
                    z |= !stopwatchTimer3.reset(false);
                }
                if (!z) {
                    StopwatchTimer stopwatchTimer4 = this.mTimerFull;
                    if (stopwatchTimer4 != null) {
                        stopwatchTimer4.detach();
                        this.mTimerFull = null;
                    }
                    DualTimer dualTimer2 = this.mTimerPartial;
                    if (dualTimer2 != null) {
                        dualTimer2.detach();
                        this.mTimerPartial = null;
                    }
                    StopwatchTimer stopwatchTimer5 = this.mTimerWindow;
                    if (stopwatchTimer5 != null) {
                        stopwatchTimer5.detach();
                        this.mTimerWindow = null;
                    }
                    StopwatchTimer stopwatchTimer6 = this.mTimerDraw;
                    if (stopwatchTimer6 != null) {
                        stopwatchTimer6.detach();
                        this.mTimerDraw = null;
                    }
                }
                return !z;
            }

            void readFromParcelLocked(TimeBase timeBase, TimeBase timeBase2, TimeBase timeBase3, Parcel parcel) {
                this.mTimerPartial = readDualTimerFromParcel(0, this.mBsi.mPartialTimers, timeBase2, timeBase3, parcel);
                this.mTimerFull = readStopwatchTimerFromParcel(1, this.mBsi.mFullTimers, timeBase, parcel);
                this.mTimerWindow = readStopwatchTimerFromParcel(2, this.mBsi.mWindowTimers, timeBase, parcel);
                this.mTimerDraw = readStopwatchTimerFromParcel(18, this.mBsi.mDrawTimers, timeBase, parcel);
            }

            void writeToParcelLocked(Parcel parcel, long j) {
                Timer.writeTimerToParcel(parcel, this.mTimerPartial, j);
                Timer.writeTimerToParcel(parcel, this.mTimerFull, j);
                Timer.writeTimerToParcel(parcel, this.mTimerWindow, j);
                Timer.writeTimerToParcel(parcel, this.mTimerDraw, j);
            }

            @Override // android.os.BatteryStats.Uid.Wakelock
            public Timer getWakeTime(int i) {
                if (i == 0) {
                    return this.mTimerPartial;
                }
                if (i == 1) {
                    return this.mTimerFull;
                }
                if (i == 2) {
                    return this.mTimerWindow;
                }
                if (i == 18) {
                    return this.mTimerDraw;
                }
                throw new IllegalArgumentException("type = " + i);
            }
        }

        public static class Sensor extends BatteryStats.Uid.Sensor {
            protected BatteryStatsImpl mBsi;
            final int mHandle;
            DualTimer mTimer;
            protected Uid mUid;

            public Sensor(BatteryStatsImpl batteryStatsImpl, Uid uid, int i) {
                this.mBsi = batteryStatsImpl;
                this.mUid = uid;
                this.mHandle = i;
            }

            private DualTimer readTimersFromParcel(TimeBase timeBase, TimeBase timeBase2, Parcel parcel) {
                if (parcel.readInt() == 0) {
                    return null;
                }
                ArrayList<StopwatchTimer> arrayList = this.mBsi.mSensorTimers.get(this.mHandle);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.mBsi.mSensorTimers.put(this.mHandle, arrayList);
                }
                return new DualTimer(this.mBsi.mClocks, this.mUid, 0, arrayList, timeBase, timeBase2, parcel);
            }

            boolean reset() {
                if (!this.mTimer.reset(true)) {
                    return false;
                }
                this.mTimer = null;
                return true;
            }

            void readFromParcelLocked(TimeBase timeBase, TimeBase timeBase2, Parcel parcel) {
                this.mTimer = readTimersFromParcel(timeBase, timeBase2, parcel);
            }

            void writeToParcelLocked(Parcel parcel, long j) {
                Timer.writeTimerToParcel(parcel, this.mTimer, j);
            }

            @Override // android.os.BatteryStats.Uid.Sensor
            public Timer getSensorTime() {
                return this.mTimer;
            }

            @Override // android.os.BatteryStats.Uid.Sensor
            public Timer getSensorBackgroundTime() {
                DualTimer dualTimer = this.mTimer;
                if (dualTimer == null) {
                    return null;
                }
                return dualTimer.getSubTimer();
            }

            @Override // android.os.BatteryStats.Uid.Sensor
            public int getHandle() {
                return this.mHandle;
            }
        }

        public static class Proc extends BatteryStats.Uid.Proc implements TimeBaseObs {
            boolean mActive = true;
            protected BatteryStatsImpl mBsi;
            ArrayList<BatteryStats.Uid.Proc.ExcessivePower> mExcessivePower;
            long mForegroundTime;
            long mLoadedForegroundTime;
            int mLoadedNumAnrs;
            int mLoadedNumCrashes;
            int mLoadedStarts;
            long mLoadedSystemTime;
            long mLoadedUserTime;
            final String mName;
            int mNumAnrs;
            int mNumCrashes;
            int mStarts;
            long mSystemTime;
            long mUnpluggedForegroundTime;
            int mUnpluggedNumAnrs;
            int mUnpluggedNumCrashes;
            int mUnpluggedStarts;
            long mUnpluggedSystemTime;
            long mUnpluggedUserTime;
            long mUserTime;

            @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
            public void onTimeStopped(long j, long j2, long j3) {
            }

            public Proc(BatteryStatsImpl batteryStatsImpl, String str) {
                this.mBsi = batteryStatsImpl;
                this.mName = str;
                batteryStatsImpl.mOnBatteryTimeBase.add(this);
            }

            @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
            public void onTimeStarted(long j, long j2, long j3) {
                this.mUnpluggedUserTime = this.mUserTime;
                this.mUnpluggedSystemTime = this.mSystemTime;
                this.mUnpluggedForegroundTime = this.mForegroundTime;
                this.mUnpluggedStarts = this.mStarts;
                this.mUnpluggedNumCrashes = this.mNumCrashes;
                this.mUnpluggedNumAnrs = this.mNumAnrs;
            }

            void detach() {
                this.mActive = false;
                this.mBsi.mOnBatteryTimeBase.remove(this);
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int countExcessivePowers() {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList != null) {
                    return arrayList.size();
                }
                return 0;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public BatteryStats.Uid.Proc.ExcessivePower getExcessivePower(int i) {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList != null) {
                    return arrayList.get(i);
                }
                return null;
            }

            public void addExcessiveCpu(long j, long j2) {
                if (this.mExcessivePower == null) {
                    this.mExcessivePower = new ArrayList<>();
                }
                BatteryStats.Uid.Proc.ExcessivePower excessivePower = new BatteryStats.Uid.Proc.ExcessivePower();
                excessivePower.type = 2;
                excessivePower.overTime = j;
                excessivePower.usedTime = j2;
                this.mExcessivePower.add(excessivePower);
            }

            void writeExcessivePowerToParcelLocked(Parcel parcel) {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList == null) {
                    parcel.writeInt(0);
                    return;
                }
                int size = arrayList.size();
                parcel.writeInt(size);
                for (int i = 0; i < size; i++) {
                    BatteryStats.Uid.Proc.ExcessivePower excessivePower = this.mExcessivePower.get(i);
                    parcel.writeInt(excessivePower.type);
                    parcel.writeLong(excessivePower.overTime);
                    parcel.writeLong(excessivePower.usedTime);
                }
            }

            void readExcessivePowerFromParcelLocked(Parcel parcel) {
                int i = parcel.readInt();
                if (i == 0) {
                    this.mExcessivePower = null;
                    return;
                }
                if (i > 10000) {
                    throw new ParcelFormatException("File corrupt: too many excessive power entries " + i);
                }
                this.mExcessivePower = new ArrayList<>();
                for (int i2 = 0; i2 < i; i2++) {
                    BatteryStats.Uid.Proc.ExcessivePower excessivePower = new BatteryStats.Uid.Proc.ExcessivePower();
                    excessivePower.type = parcel.readInt();
                    excessivePower.overTime = parcel.readLong();
                    excessivePower.usedTime = parcel.readLong();
                    this.mExcessivePower.add(excessivePower);
                }
            }

            void writeToParcelLocked(Parcel parcel) {
                parcel.writeLong(this.mUserTime);
                parcel.writeLong(this.mSystemTime);
                parcel.writeLong(this.mForegroundTime);
                parcel.writeInt(this.mStarts);
                parcel.writeInt(this.mNumCrashes);
                parcel.writeInt(this.mNumAnrs);
                parcel.writeLong(this.mLoadedUserTime);
                parcel.writeLong(this.mLoadedSystemTime);
                parcel.writeLong(this.mLoadedForegroundTime);
                parcel.writeInt(this.mLoadedStarts);
                parcel.writeInt(this.mLoadedNumCrashes);
                parcel.writeInt(this.mLoadedNumAnrs);
                parcel.writeLong(this.mUnpluggedUserTime);
                parcel.writeLong(this.mUnpluggedSystemTime);
                parcel.writeLong(this.mUnpluggedForegroundTime);
                parcel.writeInt(this.mUnpluggedStarts);
                parcel.writeInt(this.mUnpluggedNumCrashes);
                parcel.writeInt(this.mUnpluggedNumAnrs);
                writeExcessivePowerToParcelLocked(parcel);
            }

            void readFromParcelLocked(Parcel parcel) {
                this.mUserTime = parcel.readLong();
                this.mSystemTime = parcel.readLong();
                this.mForegroundTime = parcel.readLong();
                this.mStarts = parcel.readInt();
                this.mNumCrashes = parcel.readInt();
                this.mNumAnrs = parcel.readInt();
                this.mLoadedUserTime = parcel.readLong();
                this.mLoadedSystemTime = parcel.readLong();
                this.mLoadedForegroundTime = parcel.readLong();
                this.mLoadedStarts = parcel.readInt();
                this.mLoadedNumCrashes = parcel.readInt();
                this.mLoadedNumAnrs = parcel.readInt();
                this.mUnpluggedUserTime = parcel.readLong();
                this.mUnpluggedSystemTime = parcel.readLong();
                this.mUnpluggedForegroundTime = parcel.readLong();
                this.mUnpluggedStarts = parcel.readInt();
                this.mUnpluggedNumCrashes = parcel.readInt();
                this.mUnpluggedNumAnrs = parcel.readInt();
                readExcessivePowerFromParcelLocked(parcel);
            }

            public void addCpuTimeLocked(int i, int i2) {
                addCpuTimeLocked(i, i2, this.mBsi.mOnBatteryTimeBase.isRunning());
            }

            public void addCpuTimeLocked(int i, int i2, boolean z) {
                if (z) {
                    this.mUserTime += (long) i;
                    this.mSystemTime += (long) i2;
                }
            }

            public void addForegroundTimeLocked(long j) {
                this.mForegroundTime += j;
            }

            public void incStartsLocked() {
                this.mStarts++;
            }

            public void incNumCrashesLocked() {
                this.mNumCrashes++;
            }

            public void incNumAnrsLocked() {
                this.mNumAnrs++;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public boolean isActive() {
                return this.mActive;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getUserTime(int i) {
                long j;
                long j2 = this.mUserTime;
                if (i == 1) {
                    j = this.mLoadedUserTime;
                } else {
                    if (i != 2) {
                        return j2;
                    }
                    j = this.mUnpluggedUserTime;
                }
                return j2 - j;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getSystemTime(int i) {
                long j;
                long j2 = this.mSystemTime;
                if (i == 1) {
                    j = this.mLoadedSystemTime;
                } else {
                    if (i != 2) {
                        return j2;
                    }
                    j = this.mUnpluggedSystemTime;
                }
                return j2 - j;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getForegroundTime(int i) {
                long j;
                long j2 = this.mForegroundTime;
                if (i == 1) {
                    j = this.mLoadedForegroundTime;
                } else {
                    if (i != 2) {
                        return j2;
                    }
                    j = this.mUnpluggedForegroundTime;
                }
                return j2 - j;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int getStarts(int i) {
                int i2;
                int i3 = this.mStarts;
                if (i == 1) {
                    i2 = this.mLoadedStarts;
                } else {
                    if (i != 2) {
                        return i3;
                    }
                    i2 = this.mUnpluggedStarts;
                }
                return i3 - i2;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int getNumCrashes(int i) {
                int i2;
                int i3 = this.mNumCrashes;
                if (i == 1) {
                    i2 = this.mLoadedNumCrashes;
                } else {
                    if (i != 2) {
                        return i3;
                    }
                    i2 = this.mUnpluggedNumCrashes;
                }
                return i3 - i2;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int getNumAnrs(int i) {
                int i2;
                int i3 = this.mNumAnrs;
                if (i == 1) {
                    i2 = this.mLoadedNumAnrs;
                } else {
                    if (i != 2) {
                        return i3;
                    }
                    i2 = this.mUnpluggedNumAnrs;
                }
                return i3 - i2;
            }
        }

        public static class Pkg extends BatteryStats.Uid.Pkg implements TimeBaseObs {
            protected BatteryStatsImpl mBsi;
            ArrayMap<String, Counter> mWakeupAlarms = new ArrayMap<>();
            final ArrayMap<String, Serv> mServiceStats = new ArrayMap<>();

            @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
            public void onTimeStarted(long j, long j2, long j3) {
            }

            @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
            public void onTimeStopped(long j, long j2, long j3) {
            }

            public Pkg(BatteryStatsImpl batteryStatsImpl) {
                this.mBsi = batteryStatsImpl;
                batteryStatsImpl.mOnBatteryScreenOffTimeBase.add(this);
            }

            void detach() {
                this.mBsi.mOnBatteryScreenOffTimeBase.remove(this);
            }

            void readFromParcelLocked(Parcel parcel) {
                int i = parcel.readInt();
                this.mWakeupAlarms.clear();
                for (int i2 = 0; i2 < i; i2++) {
                    this.mWakeupAlarms.put(parcel.readString(), new Counter(this.mBsi.mOnBatteryScreenOffTimeBase, parcel));
                }
                int i3 = parcel.readInt();
                this.mServiceStats.clear();
                for (int i4 = 0; i4 < i3; i4++) {
                    String string = parcel.readString();
                    Serv serv = new Serv(this.mBsi);
                    this.mServiceStats.put(string, serv);
                    serv.readFromParcelLocked(parcel);
                }
            }

            void writeToParcelLocked(Parcel parcel) {
                int size = this.mWakeupAlarms.size();
                parcel.writeInt(size);
                for (int i = 0; i < size; i++) {
                    parcel.writeString(this.mWakeupAlarms.keyAt(i));
                    this.mWakeupAlarms.valueAt(i).writeToParcel(parcel);
                }
                int size2 = this.mServiceStats.size();
                parcel.writeInt(size2);
                for (int i2 = 0; i2 < size2; i2++) {
                    parcel.writeString(this.mServiceStats.keyAt(i2));
                    this.mServiceStats.valueAt(i2).writeToParcelLocked(parcel);
                }
            }

            @Override // android.os.BatteryStats.Uid.Pkg
            public ArrayMap<String, ? extends BatteryStats.Counter> getWakeupAlarmStats() {
                return this.mWakeupAlarms;
            }

            public void noteWakeupAlarmLocked(String str) {
                Counter counter = this.mWakeupAlarms.get(str);
                if (counter == null) {
                    counter = new Counter(this.mBsi.mOnBatteryScreenOffTimeBase);
                    this.mWakeupAlarms.put(str, counter);
                }
                counter.stepAtomic();
            }

            @Override // android.os.BatteryStats.Uid.Pkg
            public ArrayMap<String, ? extends BatteryStats.Uid.Pkg.Serv> getServiceStats() {
                return this.mServiceStats;
            }

            public static class Serv extends BatteryStats.Uid.Pkg.Serv implements TimeBaseObs {
                protected BatteryStatsImpl mBsi;
                protected int mLastLaunches;
                protected long mLastStartTime;
                protected int mLastStarts;
                protected boolean mLaunched;
                protected long mLaunchedSince;
                protected long mLaunchedTime;
                protected int mLaunches;
                protected int mLoadedLaunches;
                protected long mLoadedStartTime;
                protected int mLoadedStarts;
                protected Pkg mPkg;
                protected boolean mRunning;
                protected long mRunningSince;
                protected long mStartTime;
                protected int mStarts;
                protected int mUnpluggedLaunches;
                protected long mUnpluggedStartTime;
                protected int mUnpluggedStarts;

                @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
                public void onTimeStopped(long j, long j2, long j3) {
                }

                public Serv(BatteryStatsImpl batteryStatsImpl) {
                    this.mBsi = batteryStatsImpl;
                    batteryStatsImpl.mOnBatteryTimeBase.add(this);
                }

                @Override // com.android.internal.os.BatteryStatsImpl.TimeBaseObs
                public void onTimeStarted(long j, long j2, long j3) {
                    this.mUnpluggedStartTime = getStartTimeToNowLocked(j2);
                    this.mUnpluggedStarts = this.mStarts;
                    this.mUnpluggedLaunches = this.mLaunches;
                }

                public void detach() {
                    this.mBsi.mOnBatteryTimeBase.remove(this);
                }

                public void readFromParcelLocked(Parcel parcel) {
                    this.mStartTime = parcel.readLong();
                    this.mRunningSince = parcel.readLong();
                    this.mRunning = parcel.readInt() != 0;
                    this.mStarts = parcel.readInt();
                    this.mLaunchedTime = parcel.readLong();
                    this.mLaunchedSince = parcel.readLong();
                    this.mLaunched = parcel.readInt() != 0;
                    this.mLaunches = parcel.readInt();
                    this.mLoadedStartTime = parcel.readLong();
                    this.mLoadedStarts = parcel.readInt();
                    this.mLoadedLaunches = parcel.readInt();
                    this.mLastStartTime = 0L;
                    this.mLastStarts = 0;
                    this.mLastLaunches = 0;
                    this.mUnpluggedStartTime = parcel.readLong();
                    this.mUnpluggedStarts = parcel.readInt();
                    this.mUnpluggedLaunches = parcel.readInt();
                }

                public void writeToParcelLocked(Parcel parcel) {
                    parcel.writeLong(this.mStartTime);
                    parcel.writeLong(this.mRunningSince);
                    parcel.writeInt(this.mRunning ? 1 : 0);
                    parcel.writeInt(this.mStarts);
                    parcel.writeLong(this.mLaunchedTime);
                    parcel.writeLong(this.mLaunchedSince);
                    parcel.writeInt(this.mLaunched ? 1 : 0);
                    parcel.writeInt(this.mLaunches);
                    parcel.writeLong(this.mLoadedStartTime);
                    parcel.writeInt(this.mLoadedStarts);
                    parcel.writeInt(this.mLoadedLaunches);
                    parcel.writeLong(this.mUnpluggedStartTime);
                    parcel.writeInt(this.mUnpluggedStarts);
                    parcel.writeInt(this.mUnpluggedLaunches);
                }

                public long getLaunchTimeToNowLocked(long j) {
                    return !this.mLaunched ? this.mLaunchedTime : (this.mLaunchedTime + j) - this.mLaunchedSince;
                }

                public long getStartTimeToNowLocked(long j) {
                    return !this.mRunning ? this.mStartTime : (this.mStartTime + j) - this.mRunningSince;
                }

                public void startLaunchedLocked() {
                    if (this.mLaunched) {
                        return;
                    }
                    this.mLaunches++;
                    this.mLaunchedSince = this.mBsi.getBatteryUptimeLocked();
                    this.mLaunched = true;
                }

                public void stopLaunchedLocked() {
                    if (this.mLaunched) {
                        long batteryUptimeLocked = this.mBsi.getBatteryUptimeLocked() - this.mLaunchedSince;
                        if (batteryUptimeLocked > 0) {
                            this.mLaunchedTime += batteryUptimeLocked;
                        } else {
                            this.mLaunches--;
                        }
                        this.mLaunched = false;
                    }
                }

                public void startRunningLocked() {
                    if (this.mRunning) {
                        return;
                    }
                    this.mStarts++;
                    this.mRunningSince = this.mBsi.getBatteryUptimeLocked();
                    this.mRunning = true;
                }

                public void stopRunningLocked() {
                    if (this.mRunning) {
                        long batteryUptimeLocked = this.mBsi.getBatteryUptimeLocked() - this.mRunningSince;
                        if (batteryUptimeLocked > 0) {
                            this.mStartTime += batteryUptimeLocked;
                        } else {
                            this.mStarts--;
                        }
                        this.mRunning = false;
                    }
                }

                public BatteryStatsImpl getBatteryStats() {
                    return this.mBsi;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public int getLaunches(int i) {
                    int i2;
                    int i3 = this.mLaunches;
                    if (i == 1) {
                        i2 = this.mLoadedLaunches;
                    } else {
                        if (i != 2) {
                            return i3;
                        }
                        i2 = this.mUnpluggedLaunches;
                    }
                    return i3 - i2;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public long getStartTime(long j, int i) {
                    long j2;
                    long startTimeToNowLocked = getStartTimeToNowLocked(j);
                    if (i == 1) {
                        j2 = this.mLoadedStartTime;
                    } else {
                        if (i != 2) {
                            return startTimeToNowLocked;
                        }
                        j2 = this.mUnpluggedStartTime;
                    }
                    return startTimeToNowLocked - j2;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public int getStarts(int i) {
                    int i2;
                    int i3 = this.mStarts;
                    if (i == 1) {
                        i2 = this.mLoadedStarts;
                    } else {
                        if (i != 2) {
                            return i3;
                        }
                        i2 = this.mUnpluggedStarts;
                    }
                    return i3 - i2;
                }
            }

            final Serv newServiceStatsLocked() {
                return new Serv(this.mBsi);
            }
        }

        public Proc getProcessStatsLocked(String str) {
            Proc proc = this.mProcessStats.get(str);
            if (proc != null) {
                return proc;
            }
            Proc proc2 = new Proc(this.mBsi, str);
            this.mProcessStats.put(str, proc2);
            return proc2;
        }

        public void updateUidProcessStateLocked(int i) {
            boolean z = i == 3;
            int iMapToInternalProcessState = BatteryStats.mapToInternalProcessState(i);
            if (this.mProcessState == iMapToInternalProcessState && z == this.mInForegroundService) {
                return;
            }
            long jElapsedRealtime = this.mBsi.mClocks.elapsedRealtime();
            if (this.mProcessState != iMapToInternalProcessState) {
                long jUptimeMillis = this.mBsi.mClocks.uptimeMillis();
                int i2 = this.mProcessState;
                if (i2 != 19) {
                    this.mProcessStateTimer[i2].stopRunningLocked(jElapsedRealtime);
                    if (this.mBsi.trackPerProcStateCpuTimes()) {
                        if (this.mBsi.mPendingUids.size() == 0) {
                            this.mBsi.mExternalSync.scheduleReadProcStateCpuTimes(this.mBsi.mOnBatteryTimeBase.isRunning(), this.mBsi.mOnBatteryScreenOffTimeBase.isRunning(), this.mBsi.mConstants.PROC_STATE_CPU_TIMES_READ_DELAY_MS);
                            BatteryStatsImpl.access$1408(this.mBsi);
                        } else {
                            BatteryStatsImpl.access$1508(this.mBsi);
                        }
                        if (this.mBsi.mPendingUids.indexOfKey(this.mUid) < 0 || ArrayUtils.contains(CRITICAL_PROC_STATES, this.mProcessState)) {
                            this.mBsi.mPendingUids.put(this.mUid, this.mProcessState);
                        }
                    } else {
                        this.mBsi.mPendingUids.clear();
                    }
                }
                this.mProcessState = iMapToInternalProcessState;
                if (iMapToInternalProcessState != 19) {
                    if (this.mProcessStateTimer[iMapToInternalProcessState] == null) {
                        makeProcessState(iMapToInternalProcessState, null);
                    }
                    this.mProcessStateTimer[iMapToInternalProcessState].startRunningLocked(jElapsedRealtime);
                }
                long j = jUptimeMillis * 1000;
                long j2 = 1000 * jElapsedRealtime;
                updateOnBatteryBgTimeBase(j, j2);
                updateOnBatteryScreenOffBgTimeBase(j, j2);
            }
            if (z != this.mInForegroundService) {
                if (z) {
                    noteForegroundServiceResumedLocked(jElapsedRealtime);
                } else {
                    noteForegroundServicePausedLocked(jElapsedRealtime);
                }
                this.mInForegroundService = z;
            }
        }

        public boolean isInBackground() {
            return this.mProcessState >= 3;
        }

        public boolean updateOnBatteryBgTimeBase(long j, long j2) {
            return this.mOnBatteryBackgroundTimeBase.setRunning(this.mBsi.mOnBatteryTimeBase.isRunning() && isInBackground(), j, j2);
        }

        public boolean updateOnBatteryScreenOffBgTimeBase(long j, long j2) {
            return this.mOnBatteryScreenOffBackgroundTimeBase.setRunning(this.mBsi.mOnBatteryScreenOffTimeBase.isRunning() && isInBackground(), j, j2);
        }

        @Override // android.os.BatteryStats.Uid
        public SparseArray<? extends BatteryStats.Uid.Pid> getPidStats() {
            return this.mPids;
        }

        public BatteryStats.Uid.Pid getPidStatsLocked(int i) {
            BatteryStats.Uid.Pid pid = this.mPids.get(i);
            if (pid != null) {
                return pid;
            }
            BatteryStats.Uid.Pid pid2 = new BatteryStats.Uid.Pid();
            this.mPids.put(i, pid2);
            return pid2;
        }

        public Pkg getPackageStatsLocked(String str) {
            Pkg pkg = this.mPackageStats.get(str);
            if (pkg != null) {
                return pkg;
            }
            Pkg pkg2 = new Pkg(this.mBsi);
            this.mPackageStats.put(str, pkg2);
            return pkg2;
        }

        public Pkg.Serv getServiceStatsLocked(String str, String str2) {
            Pkg packageStatsLocked = getPackageStatsLocked(str);
            Pkg.Serv serv = packageStatsLocked.mServiceStats.get(str2);
            if (serv != null) {
                return serv;
            }
            Pkg.Serv servNewServiceStatsLocked = packageStatsLocked.newServiceStatsLocked();
            packageStatsLocked.mServiceStats.put(str2, servNewServiceStatsLocked);
            return servNewServiceStatsLocked;
        }

        public void readSyncSummaryFromParcelLocked(String str, Parcel parcel) {
            DualTimer dualTimerInstantiateObject = this.mSyncStats.instantiateObject();
            dualTimerInstantiateObject.readSummaryFromParcelLocked(parcel);
            this.mSyncStats.add(str, dualTimerInstantiateObject);
        }

        public void readJobSummaryFromParcelLocked(String str, Parcel parcel) {
            DualTimer dualTimerInstantiateObject = this.mJobStats.instantiateObject();
            dualTimerInstantiateObject.readSummaryFromParcelLocked(parcel);
            this.mJobStats.add(str, dualTimerInstantiateObject);
        }

        public void readWakeSummaryFromParcelLocked(String str, Parcel parcel) {
            Wakelock wakelock = new Wakelock(this.mBsi, this);
            this.mWakelockStats.add(str, wakelock);
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 1).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 0).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 2).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 18).readSummaryFromParcelLocked(parcel);
            }
        }

        public DualTimer getSensorTimerLocked(int i, boolean z) {
            Sensor sensor = this.mSensorStats.get(i);
            if (sensor == null) {
                if (!z) {
                    return null;
                }
                sensor = new Sensor(this.mBsi, this, i);
                this.mSensorStats.put(i, sensor);
            }
            DualTimer dualTimer = sensor.mTimer;
            if (dualTimer != null) {
                return dualTimer;
            }
            ArrayList<StopwatchTimer> arrayList = this.mBsi.mSensorTimers.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mBsi.mSensorTimers.put(i, arrayList);
            }
            DualTimer dualTimer2 = new DualTimer(this.mBsi.mClocks, this, 3, arrayList, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            sensor.mTimer = dualTimer2;
            return dualTimer2;
        }

        public void noteStartSyncLocked(String str, long j) {
            DualTimer dualTimerStartObject = this.mSyncStats.startObject(str);
            if (dualTimerStartObject != null) {
                dualTimerStartObject.startRunningLocked(j);
            }
        }

        public void noteStopSyncLocked(String str, long j) {
            DualTimer dualTimerStopObject = this.mSyncStats.stopObject(str);
            if (dualTimerStopObject != null) {
                dualTimerStopObject.stopRunningLocked(j);
            }
        }

        public void noteStartJobLocked(String str, long j) {
            DualTimer dualTimerStartObject = this.mJobStats.startObject(str);
            if (dualTimerStartObject != null) {
                dualTimerStartObject.startRunningLocked(j);
            }
        }

        public void noteStopJobLocked(String str, long j, int i) {
            DualTimer dualTimerStopObject = this.mJobStats.stopObject(str);
            if (dualTimerStopObject != null) {
                dualTimerStopObject.stopRunningLocked(j);
            }
            if (this.mBsi.mOnBatteryTimeBase.isRunning()) {
                SparseIntArray sparseIntArray = this.mJobCompletions.get(str);
                if (sparseIntArray == null) {
                    sparseIntArray = new SparseIntArray();
                    this.mJobCompletions.put(str, sparseIntArray);
                }
                sparseIntArray.put(i, sparseIntArray.get(i, 0) + 1);
            }
        }

        public StopwatchTimer getWakelockTimerLocked(Wakelock wakelock, int i) {
            if (wakelock == null) {
                return null;
            }
            if (i == 0) {
                DualTimer dualTimer = wakelock.mTimerPartial;
                if (dualTimer != null) {
                    return dualTimer;
                }
                DualTimer dualTimer2 = new DualTimer(this.mBsi.mClocks, this, 0, this.mBsi.mPartialTimers, this.mBsi.mOnBatteryScreenOffTimeBase, this.mOnBatteryScreenOffBackgroundTimeBase);
                wakelock.mTimerPartial = dualTimer2;
                return dualTimer2;
            }
            if (i == 1) {
                StopwatchTimer stopwatchTimer = wakelock.mTimerFull;
                if (stopwatchTimer != null) {
                    return stopwatchTimer;
                }
                StopwatchTimer stopwatchTimer2 = new StopwatchTimer(this.mBsi.mClocks, this, 1, this.mBsi.mFullTimers, this.mBsi.mOnBatteryTimeBase);
                wakelock.mTimerFull = stopwatchTimer2;
                return stopwatchTimer2;
            }
            if (i == 2) {
                StopwatchTimer stopwatchTimer3 = wakelock.mTimerWindow;
                if (stopwatchTimer3 != null) {
                    return stopwatchTimer3;
                }
                StopwatchTimer stopwatchTimer4 = new StopwatchTimer(this.mBsi.mClocks, this, 2, this.mBsi.mWindowTimers, this.mBsi.mOnBatteryTimeBase);
                wakelock.mTimerWindow = stopwatchTimer4;
                return stopwatchTimer4;
            }
            if (i == 18) {
                StopwatchTimer stopwatchTimer5 = wakelock.mTimerDraw;
                if (stopwatchTimer5 != null) {
                    return stopwatchTimer5;
                }
                StopwatchTimer stopwatchTimer6 = new StopwatchTimer(this.mBsi.mClocks, this, 18, this.mBsi.mDrawTimers, this.mBsi.mOnBatteryTimeBase);
                wakelock.mTimerDraw = stopwatchTimer6;
                return stopwatchTimer6;
            }
            throw new IllegalArgumentException("type=" + i);
        }

        public void noteStartWakeLocked(int i, String str, int i2, long j) {
            Wakelock wakelockStartObject = this.mWakelockStats.startObject(str);
            if (wakelockStartObject != null) {
                getWakelockTimerLocked(wakelockStartObject, i2).startRunningLocked(j);
            }
            if (i2 == 0) {
                createAggregatedPartialWakelockTimerLocked().startRunningLocked(j);
                if (i >= 0) {
                    BatteryStats.Uid.Pid pidStatsLocked = getPidStatsLocked(i);
                    int i3 = pidStatsLocked.mWakeNesting;
                    pidStatsLocked.mWakeNesting = i3 + 1;
                    if (i3 == 0) {
                        pidStatsLocked.mWakeStartMs = j;
                    }
                }
            }
        }

        public void noteStopWakeLocked(int i, String str, int i2, long j) {
            BatteryStats.Uid.Pid pid;
            Wakelock wakelockStopObject = this.mWakelockStats.stopObject(str);
            if (wakelockStopObject != null) {
                getWakelockTimerLocked(wakelockStopObject, i2).stopRunningLocked(j);
            }
            if (i2 == 0) {
                DualTimer dualTimer = this.mAggregatedPartialWakelockTimer;
                if (dualTimer != null) {
                    dualTimer.stopRunningLocked(j);
                }
                if (i < 0 || (pid = this.mPids.get(i)) == null || pid.mWakeNesting <= 0) {
                    return;
                }
                int i3 = pid.mWakeNesting;
                pid.mWakeNesting = i3 - 1;
                if (i3 == 1) {
                    pid.mWakeSumMs += j - pid.mWakeStartMs;
                    pid.mWakeStartMs = 0L;
                }
            }
        }

        public void reportExcessiveCpuLocked(String str, long j, long j2) {
            Proc processStatsLocked = getProcessStatsLocked(str);
            if (processStatsLocked != null) {
                processStatsLocked.addExcessiveCpu(j, j2);
            }
        }

        public void noteStartSensor(int i, long j) {
            getSensorTimerLocked(i, true).startRunningLocked(j);
        }

        public void noteStopSensor(int i, long j) {
            DualTimer sensorTimerLocked = getSensorTimerLocked(i, false);
            if (sensorTimerLocked != null) {
                sensorTimerLocked.stopRunningLocked(j);
            }
        }

        public void noteStartGps(long j) {
            noteStartSensor(-10000, j);
        }

        public void noteStopGps(long j) {
            noteStopSensor(-10000, j);
        }

        public BatteryStatsImpl getBatteryStats() {
            return this.mBsi;
        }
    }

    @Override // android.os.BatteryStats
    public long[] getCpuFreqs() {
        return this.mCpuFreqs;
    }

    public BatteryStatsImpl(File file, Handler handler, PlatformIdleStateCallback platformIdleStateCallback, UserInfoProvider userInfoProvider) {
        this(new SystemClocks(), file, handler, platformIdleStateCallback, userInfoProvider);
    }

    private BatteryStatsImpl(Clocks clocks, File file, Handler handler, PlatformIdleStateCallback platformIdleStateCallback, UserInfoProvider userInfoProvider) {
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mKernelUidCpuTimeReader = new KernelUidCpuTimeReader();
        this.mKernelUidCpuFreqTimeReader = new KernelUidCpuFreqTimeReader();
        this.mKernelUidCpuActiveTimeReader = new KernelUidCpuActiveTimeReader();
        this.mKernelUidCpuClusterTimeReader = new KernelUidCpuClusterTimeReader();
        this.mKernelMemoryBandwidthStats = new KernelMemoryBandwidthStats();
        this.mKernelMemoryStats = new LongSparseArray<>();
        this.mPerProcStateCpuTimesAvailable = true;
        this.mPendingUids = new SparseIntArray();
        this.mCpuTimeReadsTrackingStartTime = SystemClock.uptimeMillis();
        this.mTmpRpmStats = new RpmStats();
        this.mLastRpmStatsUpdateTimeMs = -1000L;
        this.mPendingRemovedUids = new LinkedList();
        this.mExternalSync = null;
        this.mUserInfoProvider = null;
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mDrawTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mWifiBatchedScanTimers = new SparseArray<>();
        this.mAudioTurnedOnTimers = new ArrayList<>();
        this.mVideoTurnedOnTimers = new ArrayList<>();
        this.mFlashlightTurnedOnTimers = new ArrayList<>();
        this.mCameraTurnedOnTimers = new ArrayList<>();
        this.mBluetoothScanOnTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new BatteryStats.HistoryEventTracker();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = false;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryAddTmp = new BatteryStats.HistoryItem();
        this.mHistoryTagPool = new HashMap<>();
        this.mNextHistoryTagIdx = 0;
        this.mNumHistoryTagChars = 0;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mActiveHistoryStates = -1;
        this.mActiveHistoryStates2 = -1;
        this.mLastHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryUptime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mLastHistoryStepDetails = null;
        this.mLastHistoryStepLevel = (byte) 0;
        this.mCurHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mReadHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mTmpHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mScreenState = 0;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mUsbDataState = 0;
        this.mGpsSignalQualityBin = -1;
        this.mGpsSignalQualityTimer = new StopwatchTimer[2];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[21];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
        this.mHasWifiReporting = false;
        this.mHasBluetoothReporting = false;
        this.mHasModemReporting = false;
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mIsCellularTxPowerHigh = false;
        this.mMobileRadioPowerState = 1;
        this.mWifiRadioPowerState = 1;
        this.mCharging = true;
        this.mInitStepMode = 0;
        this.mCurStepMode = 0;
        this.mModStepMode = 0;
        this.mDischargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyDischargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mChargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyChargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mDailyStartTime = 0L;
        this.mNextMinDailyDeadline = 0L;
        this.mNextMaxDailyDeadline = 0L;
        this.mDailyItems = new ArrayList<>();
        this.mLastWriteTime = 0L;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mEstimatedBatteryCapacity = -1;
        this.mMinLearnedBatteryCapacity = -1;
        this.mMaxLearnedBatteryCapacity = -1;
        this.mRpmStats = new HashMap<>();
        this.mScreenOffRpmStats = new HashMap<>();
        this.mKernelWakelockStats = new HashMap<>();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0L;
        this.mWakeupReasonStats = new HashMap<>();
        this.mChangedStates = 0;
        this.mChangedStates2 = 0;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mNetworkStatsPool = new Pools.SynchronizedPool(6);
        this.mWifiNetworkLock = new Object();
        this.mWifiIfaces = EmptyArray.STRING;
        this.mLastWifiNetworkStats = new NetworkStats(0L, -1);
        this.mModemNetworkLock = new Object();
        this.mModemIfaces = EmptyArray.STRING;
        this.mLastModemNetworkStats = new NetworkStats(0L, -1);
        this.mLastModemActivityInfo = new ModemActivityInfo(0L, 0, 0, new int[0], 0, 0);
        this.mLastBluetoothActivityInfo = new BluetoothActivityInfoCache();
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        init(clocks);
        if (file != null) {
            this.mFile = new JournaledFile(new File(file, "batterystats.bin"), new File(file, "batterystats.bin.tmp"));
        } else {
            this.mFile = null;
        }
        this.mCheckinFile = new AtomicFile(new File(file, "batterystats-checkin.bin"));
        this.mDailyFile = new AtomicFile(new File(file, "batterystats-daily.xml"));
        this.mHandler = new MyHandler(handler.getLooper());
        this.mConstants = new Constants(this.mHandler);
        this.mStartCount++;
        this.mScreenOnTimer = new StopwatchTimer(this.mClocks, null, -1, null, this.mOnBatteryTimeBase);
        this.mScreenDozeTimer = new StopwatchTimer(this.mClocks, null, -1, null, this.mOnBatteryTimeBase);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(this.mClocks, null, (-100) - i, null, this.mOnBatteryTimeBase);
        }
        this.mInteractiveTimer = new StopwatchTimer(this.mClocks, null, -10, null, this.mOnBatteryTimeBase);
        this.mPowerSaveModeEnabledTimer = new StopwatchTimer(this.mClocks, null, -2, null, this.mOnBatteryTimeBase);
        this.mDeviceIdleModeLightTimer = new StopwatchTimer(this.mClocks, null, -11, null, this.mOnBatteryTimeBase);
        this.mDeviceIdleModeFullTimer = new StopwatchTimer(this.mClocks, null, -14, null, this.mOnBatteryTimeBase);
        this.mDeviceLightIdlingTimer = new StopwatchTimer(this.mClocks, null, -15, null, this.mOnBatteryTimeBase);
        this.mDeviceIdlingTimer = new StopwatchTimer(this.mClocks, null, -12, null, this.mOnBatteryTimeBase);
        this.mPhoneOnTimer = new StopwatchTimer(this.mClocks, null, -3, null, this.mOnBatteryTimeBase);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2] = new StopwatchTimer(this.mClocks, null, (-200) - i2, null, this.mOnBatteryTimeBase);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(this.mClocks, null, -199, null, this.mOnBatteryTimeBase);
        for (int i3 = 0; i3 < 21; i3++) {
            this.mPhoneDataConnectionsTimer[i3] = new StopwatchTimer(this.mClocks, null, (-300) - i3, null, this.mOnBatteryTimeBase);
        }
        for (int i4 = 0; i4 < 10; i4++) {
            this.mNetworkByteActivityCounters[i4] = new LongSamplingCounter(this.mOnBatteryTimeBase);
            this.mNetworkPacketActivityCounters[i4] = new LongSamplingCounter(this.mOnBatteryTimeBase);
        }
        this.mWifiActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 1);
        this.mBluetoothActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 1);
        this.mModemActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 5);
        this.mMobileRadioActiveTimer = new StopwatchTimer(this.mClocks, null, -400, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActivePerAppTimer = new StopwatchTimer(this.mClocks, null, -401, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActiveAdjustedTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mWifiMulticastWakelockTimer = new StopwatchTimer(this.mClocks, null, 23, null, this.mOnBatteryTimeBase);
        this.mWifiOnTimer = new StopwatchTimer(this.mClocks, null, -4, null, this.mOnBatteryTimeBase);
        this.mGlobalWifiRunningTimer = new StopwatchTimer(this.mClocks, null, -5, null, this.mOnBatteryTimeBase);
        for (int i5 = 0; i5 < 8; i5++) {
            this.mWifiStateTimer[i5] = new StopwatchTimer(this.mClocks, null, (-600) - i5, null, this.mOnBatteryTimeBase);
        }
        for (int i6 = 0; i6 < 13; i6++) {
            this.mWifiSupplStateTimer[i6] = new StopwatchTimer(this.mClocks, null, (-700) - i6, null, this.mOnBatteryTimeBase);
        }
        for (int i7 = 0; i7 < 5; i7++) {
            this.mWifiSignalStrengthsTimer[i7] = new StopwatchTimer(this.mClocks, null, (-800) - i7, null, this.mOnBatteryTimeBase);
        }
        this.mWifiActiveTimer = new StopwatchTimer(this.mClocks, null, -900, null, this.mOnBatteryTimeBase);
        for (int i8 = 0; i8 < 2; i8++) {
            this.mGpsSignalQualityTimer[i8] = new StopwatchTimer(this.mClocks, null, (-1000) - i8, null, this.mOnBatteryTimeBase);
        }
        this.mAudioOnTimer = new StopwatchTimer(this.mClocks, null, -7, null, this.mOnBatteryTimeBase);
        this.mVideoOnTimer = new StopwatchTimer(this.mClocks, null, -8, null, this.mOnBatteryTimeBase);
        this.mFlashlightOnTimer = new StopwatchTimer(this.mClocks, null, -9, null, this.mOnBatteryTimeBase);
        this.mCameraOnTimer = new StopwatchTimer(this.mClocks, null, -13, null, this.mOnBatteryTimeBase);
        this.mBluetoothScanTimer = new StopwatchTimer(this.mClocks, null, -14, null, this.mOnBatteryTimeBase);
        this.mDischargeScreenOffCounter = new LongSamplingCounter(this.mOnBatteryScreenOffTimeBase);
        this.mDischargeScreenDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mDischargeLightDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mDischargeDeepDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mDischargeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mOnBatteryInternal = false;
        this.mOnBattery = false;
        initTimes(this.mClocks.uptimeMillis() * 1000, this.mClocks.elapsedRealtime() * 1000);
        String str = Build.ID;
        this.mEndPlatformVersion = str;
        this.mStartPlatformVersion = str;
        this.mDischargeStartLevel = 0;
        this.mDischargeUnplugLevel = 0;
        this.mDischargePlugLevel = -1;
        this.mDischargeCurrentLevel = 0;
        this.mCurrentBatteryLevel = 0;
        initDischarge();
        clearHistoryLocked();
        updateDailyDeadlineLocked();
        this.mPlatformIdleStateCallback = platformIdleStateCallback;
        this.mUserInfoProvider = userInfoProvider;
    }

    public BatteryStatsImpl(Parcel parcel) {
        this(new SystemClocks(), parcel);
    }

    public BatteryStatsImpl(Clocks clocks, Parcel parcel) {
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mKernelUidCpuTimeReader = new KernelUidCpuTimeReader();
        this.mKernelUidCpuFreqTimeReader = new KernelUidCpuFreqTimeReader();
        this.mKernelUidCpuActiveTimeReader = new KernelUidCpuActiveTimeReader();
        this.mKernelUidCpuClusterTimeReader = new KernelUidCpuClusterTimeReader();
        this.mKernelMemoryBandwidthStats = new KernelMemoryBandwidthStats();
        this.mKernelMemoryStats = new LongSparseArray<>();
        this.mPerProcStateCpuTimesAvailable = true;
        this.mPendingUids = new SparseIntArray();
        this.mCpuTimeReadsTrackingStartTime = SystemClock.uptimeMillis();
        this.mTmpRpmStats = new RpmStats();
        this.mLastRpmStatsUpdateTimeMs = -1000L;
        this.mPendingRemovedUids = new LinkedList();
        this.mExternalSync = null;
        this.mUserInfoProvider = null;
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mDrawTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mWifiBatchedScanTimers = new SparseArray<>();
        this.mAudioTurnedOnTimers = new ArrayList<>();
        this.mVideoTurnedOnTimers = new ArrayList<>();
        this.mFlashlightTurnedOnTimers = new ArrayList<>();
        this.mCameraTurnedOnTimers = new ArrayList<>();
        this.mBluetoothScanOnTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new BatteryStats.HistoryEventTracker();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = false;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryAddTmp = new BatteryStats.HistoryItem();
        this.mHistoryTagPool = new HashMap<>();
        this.mNextHistoryTagIdx = 0;
        this.mNumHistoryTagChars = 0;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mActiveHistoryStates = -1;
        this.mActiveHistoryStates2 = -1;
        this.mLastHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryElapsedRealtime = 0L;
        this.mTrackRunningHistoryUptime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mLastHistoryStepDetails = null;
        this.mLastHistoryStepLevel = (byte) 0;
        this.mCurHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mReadHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mTmpHistoryStepDetails = new BatteryStats.HistoryStepDetails();
        this.mScreenState = 0;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mUsbDataState = 0;
        this.mGpsSignalQualityBin = -1;
        this.mGpsSignalQualityTimer = new StopwatchTimer[2];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[21];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
        this.mHasWifiReporting = false;
        this.mHasBluetoothReporting = false;
        this.mHasModemReporting = false;
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mIsCellularTxPowerHigh = false;
        this.mMobileRadioPowerState = 1;
        this.mWifiRadioPowerState = 1;
        this.mCharging = true;
        this.mInitStepMode = 0;
        this.mCurStepMode = 0;
        this.mModStepMode = 0;
        this.mDischargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyDischargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mChargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyChargeStepTracker = new BatteryStats.LevelStepTracker(400);
        this.mDailyStartTime = 0L;
        this.mNextMinDailyDeadline = 0L;
        this.mNextMaxDailyDeadline = 0L;
        this.mDailyItems = new ArrayList<>();
        this.mLastWriteTime = 0L;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mEstimatedBatteryCapacity = -1;
        this.mMinLearnedBatteryCapacity = -1;
        this.mMaxLearnedBatteryCapacity = -1;
        this.mRpmStats = new HashMap<>();
        this.mScreenOffRpmStats = new HashMap<>();
        this.mKernelWakelockStats = new HashMap<>();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0L;
        this.mWakeupReasonStats = new HashMap<>();
        this.mChangedStates = 0;
        this.mChangedStates2 = 0;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mNetworkStatsPool = new Pools.SynchronizedPool(6);
        this.mWifiNetworkLock = new Object();
        this.mWifiIfaces = EmptyArray.STRING;
        this.mLastWifiNetworkStats = new NetworkStats(0L, -1);
        this.mModemNetworkLock = new Object();
        this.mModemIfaces = EmptyArray.STRING;
        this.mLastModemNetworkStats = new NetworkStats(0L, -1);
        this.mLastModemActivityInfo = new ModemActivityInfo(0L, 0, 0, new int[0], 0, 0);
        this.mLastBluetoothActivityInfo = new BluetoothActivityInfoCache();
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        init(clocks);
        this.mFile = null;
        this.mCheckinFile = null;
        this.mDailyFile = null;
        this.mHandler = null;
        this.mExternalSync = null;
        this.mConstants = new Constants(this.mHandler);
        clearHistoryLocked();
        readFromParcel(parcel);
        this.mPlatformIdleStateCallback = null;
    }

    public void setPowerProfileLocked(PowerProfile powerProfile) {
        this.mPowerProfile = powerProfile;
        int numCpuClusters = powerProfile.getNumCpuClusters();
        this.mKernelCpuSpeedReaders = new KernelCpuSpeedReader[numCpuClusters];
        int numCoresInCpuCluster = 0;
        for (int i = 0; i < numCpuClusters; i++) {
            this.mKernelCpuSpeedReaders[i] = new KernelCpuSpeedReader(numCoresInCpuCluster, this.mPowerProfile.getNumSpeedStepsInCpuCluster(i));
            numCoresInCpuCluster += this.mPowerProfile.getNumCoresInCpuCluster(i);
        }
        if (this.mEstimatedBatteryCapacity == -1) {
            this.mEstimatedBatteryCapacity = (int) this.mPowerProfile.getBatteryCapacity();
        }
    }

    public void setCallback(BatteryCallback batteryCallback) {
        this.mCallback = batteryCallback;
    }

    public void setRadioScanningTimeoutLocked(long j) {
        StopwatchTimer stopwatchTimer = this.mPhoneSignalScanningTimer;
        if (stopwatchTimer != null) {
            stopwatchTimer.setTimeout(j);
        }
    }

    public void setExternalStatsSyncLocked(ExternalStatsSync externalStatsSync) {
        this.mExternalSync = externalStatsSync;
    }

    public void updateDailyDeadlineLocked() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mDailyStartTime = jCurrentTimeMillis;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(jCurrentTimeMillis);
        calendar.set(6, calendar.get(6) + 1);
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(11, 1);
        this.mNextMinDailyDeadline = calendar.getTimeInMillis();
        calendar.set(11, 3);
        this.mNextMaxDailyDeadline = calendar.getTimeInMillis();
    }

    public void recordDailyStatsIfNeededLocked(boolean z) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis >= this.mNextMaxDailyDeadline) {
            recordDailyStatsLocked();
            return;
        }
        if (z && jCurrentTimeMillis >= this.mNextMinDailyDeadline) {
            recordDailyStatsLocked();
        } else if (jCurrentTimeMillis < this.mDailyStartTime - 86400000) {
            recordDailyStatsLocked();
        }
    }

    public void recordDailyStatsLocked() {
        boolean z;
        BatteryStats.DailyItem dailyItem = new BatteryStats.DailyItem();
        dailyItem.mStartTime = this.mDailyStartTime;
        dailyItem.mEndTime = System.currentTimeMillis();
        boolean z2 = true;
        if (this.mDailyDischargeStepTracker.mNumStepDurations > 0) {
            dailyItem.mDischargeSteps = new BatteryStats.LevelStepTracker(this.mDailyDischargeStepTracker.mNumStepDurations, this.mDailyDischargeStepTracker.mStepDurations);
            z = true;
        } else {
            z = false;
        }
        if (this.mDailyChargeStepTracker.mNumStepDurations > 0) {
            dailyItem.mChargeSteps = new BatteryStats.LevelStepTracker(this.mDailyChargeStepTracker.mNumStepDurations, this.mDailyChargeStepTracker.mStepDurations);
            z = true;
        }
        ArrayList<BatteryStats.PackageChange> arrayList = this.mDailyPackageChanges;
        if (arrayList != null) {
            dailyItem.mPackageChanges = arrayList;
            this.mDailyPackageChanges = null;
        } else {
            z2 = z;
        }
        this.mDailyDischargeStepTracker.init();
        this.mDailyChargeStepTracker.init();
        updateDailyDeadlineLocked();
        if (z2) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            this.mDailyItems.add(dailyItem);
            while (this.mDailyItems.size() > 10) {
                this.mDailyItems.remove(0);
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                XmlSerializer fastXmlSerializer = new FastXmlSerializer();
                fastXmlSerializer.setOutput(byteArrayOutputStream, StandardCharsets.UTF_8.name());
                writeDailyItemsLocked(fastXmlSerializer);
                final long jUptimeMillis2 = SystemClock.uptimeMillis() - jUptimeMillis;
                BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.internal.os.BatteryStatsImpl.2
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (BatteryStatsImpl.this.mCheckinFile) {
                            long jUptimeMillis3 = SystemClock.uptimeMillis();
                            FileOutputStream fileOutputStreamStartWrite = null;
                            try {
                                fileOutputStreamStartWrite = BatteryStatsImpl.this.mDailyFile.startWrite();
                                byteArrayOutputStream.writeTo(fileOutputStreamStartWrite);
                                fileOutputStreamStartWrite.flush();
                                FileUtils.sync(fileOutputStreamStartWrite);
                                fileOutputStreamStartWrite.close();
                                BatteryStatsImpl.this.mDailyFile.finishWrite(fileOutputStreamStartWrite);
                                EventLogTags.writeCommitSysConfigFile("batterystats-daily", (jUptimeMillis2 + SystemClock.uptimeMillis()) - jUptimeMillis3);
                            } catch (IOException e) {
                                Slog.w("BatteryStats", "Error writing battery daily items", e);
                                BatteryStatsImpl.this.mDailyFile.failWrite(fileOutputStreamStartWrite);
                            }
                        }
                    }
                });
            } catch (IOException unused) {
            }
        }
    }

    private void writeDailyItemsLocked(XmlSerializer xmlSerializer) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        xmlSerializer.startDocument(null, true);
        xmlSerializer.startTag(null, "daily-items");
        for (int i = 0; i < this.mDailyItems.size(); i++) {
            BatteryStats.DailyItem dailyItem = this.mDailyItems.get(i);
            xmlSerializer.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            xmlSerializer.attribute(null, Telephony.BaseMmsColumns.START, Long.toString(dailyItem.mStartTime));
            xmlSerializer.attribute(null, "end", Long.toString(dailyItem.mEndTime));
            writeDailyLevelSteps(xmlSerializer, "dis", dailyItem.mDischargeSteps, sb);
            writeDailyLevelSteps(xmlSerializer, "chg", dailyItem.mChargeSteps, sb);
            if (dailyItem.mPackageChanges != null) {
                for (int i2 = 0; i2 < dailyItem.mPackageChanges.size(); i2++) {
                    BatteryStats.PackageChange packageChange = dailyItem.mPackageChanges.get(i2);
                    if (packageChange.mUpdate) {
                        xmlSerializer.startTag(null, "upd");
                        xmlSerializer.attribute(null, SliceProvider.EXTRA_PKG, packageChange.mPackageName);
                        xmlSerializer.attribute(null, "ver", Long.toString(packageChange.mVersionCode));
                        xmlSerializer.endTag(null, "upd");
                    } else {
                        xmlSerializer.startTag(null, "rem");
                        xmlSerializer.attribute(null, SliceProvider.EXTRA_PKG, packageChange.mPackageName);
                        xmlSerializer.endTag(null, "rem");
                    }
                }
            }
            xmlSerializer.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        xmlSerializer.endTag(null, "daily-items");
        xmlSerializer.endDocument();
    }

    private void writeDailyLevelSteps(XmlSerializer xmlSerializer, String str, BatteryStats.LevelStepTracker levelStepTracker, StringBuilder sb) throws IOException {
        if (levelStepTracker != null) {
            xmlSerializer.startTag(null, str);
            xmlSerializer.attribute(null, "n", Integer.toString(levelStepTracker.mNumStepDurations));
            for (int i = 0; i < levelStepTracker.mNumStepDurations; i++) {
                xmlSerializer.startTag(null, "s");
                sb.setLength(0);
                levelStepTracker.encodeEntryAt(i, sb);
                xmlSerializer.attribute(null, Telephony.BaseMmsColumns.MMS_VERSION, sb.toString());
                xmlSerializer.endTag(null, "s");
            }
            xmlSerializer.endTag(null, str);
        }
    }

    public void readDailyStatsLocked() throws IOException {
        Slog.d(TAG, "Reading daily items from " + this.mDailyFile.getBaseFile());
        this.mDailyItems.clear();
        try {
            FileInputStream fileInputStreamOpenRead = this.mDailyFile.openRead();
            try {
                XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
                xmlPullParserNewPullParser.setInput(fileInputStreamOpenRead, StandardCharsets.UTF_8.name());
                readDailyItemsLocked(xmlPullParserNewPullParser);
            } catch (XmlPullParserException unused) {
            } catch (Throwable th) {
                try {
                    fileInputStreamOpenRead.close();
                } catch (IOException unused2) {
                }
                throw th;
            }
            fileInputStreamOpenRead.close();
        } catch (FileNotFoundException | IOException unused3) {
        }
    }

    private void readDailyItemsLocked(XmlPullParser xmlPullParser) {
        int next;
        do {
            try {
                next = xmlPullParser.next();
                if (next == 2) {
                    break;
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failed parsing daily " + e);
                return;
            } catch (IllegalStateException e2) {
                Slog.w(TAG, "Failed parsing daily " + e2);
                return;
            } catch (IndexOutOfBoundsException e3) {
                Slog.w(TAG, "Failed parsing daily " + e3);
                return;
            } catch (NullPointerException e4) {
                Slog.w(TAG, "Failed parsing daily " + e4);
                return;
            } catch (NumberFormatException e5) {
                Slog.w(TAG, "Failed parsing daily " + e5);
                return;
            } catch (XmlPullParserException e6) {
                Slog.w(TAG, "Failed parsing daily " + e6);
                return;
            }
        } while (next != 1);
        if (next != 2) {
            throw new IllegalStateException("no start tag found");
        }
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next2 = xmlPullParser.next();
            if (next2 == 1) {
                return;
            }
            if (next2 == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next2 != 3 && next2 != 4) {
                if (xmlPullParser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    readDailyItemTagLocked(xmlPullParser);
                } else {
                    Slog.w(TAG, "Unknown element under <daily-items>: " + xmlPullParser.getName());
                    XmlUtils.skipCurrentTag(xmlPullParser);
                }
            }
        }
    }

    void readDailyItemTagLocked(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, NumberFormatException {
        BatteryStats.DailyItem dailyItem = new BatteryStats.DailyItem();
        String attributeValue = xmlPullParser.getAttributeValue(null, Telephony.BaseMmsColumns.START);
        if (attributeValue != null) {
            dailyItem.mStartTime = Long.parseLong(attributeValue);
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "end");
        if (attributeValue2 != null) {
            dailyItem.mEndTime = Long.parseLong(attributeValue2);
        }
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            }
            if (next != 3 && next != 4) {
                String name = xmlPullParser.getName();
                if (name.equals("dis")) {
                    readDailyItemTagDetailsLocked(xmlPullParser, dailyItem, false, "dis");
                } else if (name.equals("chg")) {
                    readDailyItemTagDetailsLocked(xmlPullParser, dailyItem, true, "chg");
                } else if (name.equals("upd")) {
                    if (dailyItem.mPackageChanges == null) {
                        dailyItem.mPackageChanges = new ArrayList<>();
                    }
                    BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
                    packageChange.mUpdate = true;
                    packageChange.mPackageName = xmlPullParser.getAttributeValue(null, SliceProvider.EXTRA_PKG);
                    String attributeValue3 = xmlPullParser.getAttributeValue(null, "ver");
                    packageChange.mVersionCode = attributeValue3 != null ? Long.parseLong(attributeValue3) : 0L;
                    dailyItem.mPackageChanges.add(packageChange);
                    XmlUtils.skipCurrentTag(xmlPullParser);
                } else if (name.equals("rem")) {
                    if (dailyItem.mPackageChanges == null) {
                        dailyItem.mPackageChanges = new ArrayList<>();
                    }
                    BatteryStats.PackageChange packageChange2 = new BatteryStats.PackageChange();
                    packageChange2.mUpdate = false;
                    packageChange2.mPackageName = xmlPullParser.getAttributeValue(null, SliceProvider.EXTRA_PKG);
                    dailyItem.mPackageChanges.add(packageChange2);
                    XmlUtils.skipCurrentTag(xmlPullParser);
                } else {
                    Slog.w(TAG, "Unknown element under <item>: " + xmlPullParser.getName());
                    XmlUtils.skipCurrentTag(xmlPullParser);
                }
            }
        }
        this.mDailyItems.add(dailyItem);
    }

    void readDailyItemTagDetailsLocked(XmlPullParser xmlPullParser, BatteryStats.DailyItem dailyItem, boolean z, String str) throws XmlPullParserException, IOException, NumberFormatException {
        String attributeValue;
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "n");
        if (attributeValue2 == null) {
            Slog.w(TAG, "Missing 'n' attribute at " + xmlPullParser.getPositionDescription());
            XmlUtils.skipCurrentTag(xmlPullParser);
            return;
        }
        int i = Integer.parseInt(attributeValue2);
        BatteryStats.LevelStepTracker levelStepTracker = new BatteryStats.LevelStepTracker(i);
        if (z) {
            dailyItem.mChargeSteps = levelStepTracker;
        } else {
            dailyItem.mDischargeSteps = levelStepTracker;
        }
        int i2 = 0;
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            }
            if (next != 3 && next != 4) {
                if (!"s".equals(xmlPullParser.getName())) {
                    Slog.w(TAG, "Unknown element under <" + str + ">: " + xmlPullParser.getName());
                    XmlUtils.skipCurrentTag(xmlPullParser);
                } else if (i2 < i && (attributeValue = xmlPullParser.getAttributeValue(null, Telephony.BaseMmsColumns.MMS_VERSION)) != null) {
                    levelStepTracker.decodeEntryAt(i2, attributeValue);
                    i2++;
                }
            }
        }
        levelStepTracker.mNumStepDurations = i2;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.DailyItem getDailyItemLocked(int i) {
        int size = (this.mDailyItems.size() - 1) - i;
        if (size >= 0) {
            return this.mDailyItems.get(size);
        }
        return null;
    }

    @Override // android.os.BatteryStats
    public long getCurrentDailyStartTime() {
        return this.mDailyStartTime;
    }

    @Override // android.os.BatteryStats
    public long getNextMinDailyDeadline() {
        return this.mNextMinDailyDeadline;
    }

    @Override // android.os.BatteryStats
    public long getNextMaxDailyDeadline() {
        return this.mNextMaxDailyDeadline;
    }

    @Override // android.os.BatteryStats
    public boolean startIteratingOldHistoryLocked() {
        BatteryStats.HistoryItem historyItem = this.mHistory;
        this.mHistoryIterator = historyItem;
        if (historyItem == null) {
            return false;
        }
        this.mHistoryBuffer.setDataPosition(0);
        this.mHistoryReadTmp.clear();
        this.mReadOverflow = false;
        this.mIteratingHistory = true;
        return true;
    }

    @Override // android.os.BatteryStats
    public boolean getNextOldHistoryLocked(BatteryStats.HistoryItem historyItem) {
        boolean z = this.mHistoryBuffer.dataPosition() >= this.mHistoryBuffer.dataSize();
        if (!z) {
            readHistoryDelta(this.mHistoryBuffer, this.mHistoryReadTmp);
            this.mReadOverflow |= this.mHistoryReadTmp.cmd == 6;
        }
        BatteryStats.HistoryItem historyItem2 = this.mHistoryIterator;
        if (historyItem2 == null) {
            if (!this.mReadOverflow && !z) {
                Slog.w(TAG, "Old history ends before new history!");
            }
            return false;
        }
        historyItem.setTo(historyItem2);
        this.mHistoryIterator = historyItem2.next;
        if (!this.mReadOverflow) {
            if (z) {
                Slog.w(TAG, "New history ends before old history!");
            } else if (!historyItem.same(this.mHistoryReadTmp)) {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(new LogWriter(5, TAG));
                fastPrintWriter.println("Histories differ!");
                fastPrintWriter.println("Old history:");
                new BatteryStats.HistoryPrinter().printNextItem(fastPrintWriter, historyItem, 0L, false, true);
                fastPrintWriter.println("New history:");
                new BatteryStats.HistoryPrinter().printNextItem(fastPrintWriter, this.mHistoryReadTmp, 0L, false, true);
                fastPrintWriter.flush();
            }
        }
        return true;
    }

    @Override // android.os.BatteryStats
    public void finishIteratingOldHistoryLocked() {
        this.mIteratingHistory = false;
        Parcel parcel = this.mHistoryBuffer;
        parcel.setDataPosition(parcel.dataSize());
        this.mHistoryIterator = null;
    }

    @Override // android.os.BatteryStats
    public int getHistoryTotalSize() {
        return MAX_HISTORY_BUFFER;
    }

    @Override // android.os.BatteryStats
    public int getHistoryUsedSize() {
        return this.mHistoryBuffer.dataSize();
    }

    @Override // android.os.BatteryStats
    public boolean startIteratingHistoryLocked() {
        if (this.mHistoryBuffer.dataSize() <= 0) {
            return false;
        }
        this.mHistoryBuffer.setDataPosition(0);
        this.mReadOverflow = false;
        this.mIteratingHistory = true;
        this.mReadHistoryStrings = new String[this.mHistoryTagPool.size()];
        this.mReadHistoryUids = new int[this.mHistoryTagPool.size()];
        this.mReadHistoryChars = 0;
        for (Map.Entry<BatteryStats.HistoryTag, Integer> entry : this.mHistoryTagPool.entrySet()) {
            BatteryStats.HistoryTag key = entry.getKey();
            int iIntValue = entry.getValue().intValue();
            this.mReadHistoryStrings[iIntValue] = key.string;
            this.mReadHistoryUids[iIntValue] = key.uid;
            this.mReadHistoryChars += key.string.length() + 1;
        }
        return true;
    }

    @Override // android.os.BatteryStats
    public int getHistoryStringPoolSize() {
        return this.mReadHistoryStrings.length;
    }

    @Override // android.os.BatteryStats
    public int getHistoryStringPoolBytes() {
        return (this.mReadHistoryStrings.length * 12) + (this.mReadHistoryChars * 2);
    }

    @Override // android.os.BatteryStats
    public String getHistoryTagPoolString(int i) {
        return this.mReadHistoryStrings[i];
    }

    @Override // android.os.BatteryStats
    public int getHistoryTagPoolUid(int i) {
        return this.mReadHistoryUids[i];
    }

    @Override // android.os.BatteryStats
    public boolean getNextHistoryLocked(BatteryStats.HistoryItem historyItem) {
        int iDataPosition = this.mHistoryBuffer.dataPosition();
        if (iDataPosition == 0) {
            historyItem.clear();
        }
        if (iDataPosition >= this.mHistoryBuffer.dataSize()) {
            return false;
        }
        long j = historyItem.time;
        long j2 = historyItem.currentTime;
        readHistoryDelta(this.mHistoryBuffer, historyItem);
        if (historyItem.cmd != 5 && historyItem.cmd != 7 && j2 != 0) {
            historyItem.currentTime = j2 + (historyItem.time - j);
        }
        return true;
    }

    @Override // android.os.BatteryStats
    public void finishIteratingHistoryLocked() {
        this.mIteratingHistory = false;
        Parcel parcel = this.mHistoryBuffer;
        parcel.setDataPosition(parcel.dataSize());
        this.mReadHistoryStrings = null;
    }

    @Override // android.os.BatteryStats
    public long getHistoryBaseTime() {
        return this.mHistoryBaseTime;
    }

    @Override // android.os.BatteryStats
    public int getStartCount() {
        return this.mStartCount;
    }

    public boolean isOnBattery() {
        return this.mOnBattery;
    }

    public boolean isCharging() {
        return this.mCharging;
    }

    void initTimes(long j, long j2) {
        this.mStartClockTime = System.currentTimeMillis();
        this.mOnBatteryTimeBase.init(j, j2);
        this.mOnBatteryScreenOffTimeBase.init(j, j2);
        this.mRealtime = 0L;
        this.mUptime = 0L;
        this.mRealtimeStart = j2;
        this.mUptimeStart = j;
    }

    void initDischarge() {
        this.mLowDischargeAmountSinceCharge = 0;
        this.mHighDischargeAmountSinceCharge = 0;
        this.mDischargeAmountScreenOn = 0;
        this.mDischargeAmountScreenOnSinceCharge = 0;
        this.mDischargeAmountScreenOff = 0;
        this.mDischargeAmountScreenOffSinceCharge = 0;
        this.mDischargeAmountScreenDoze = 0;
        this.mDischargeAmountScreenDozeSinceCharge = 0;
        this.mDischargeStepTracker.init();
        this.mChargeStepTracker.init();
        this.mDischargeScreenOffCounter.reset(false);
        this.mDischargeScreenDozeCounter.reset(false);
        this.mDischargeLightDozeCounter.reset(false);
        this.mDischargeDeepDozeCounter.reset(false);
        this.mDischargeCounter.reset(false);
    }

    public void resetAllStatsCmdLocked() {
        resetAllStatsLocked();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        long j = jUptimeMillis * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long j2 = 1000 * jElapsedRealtime;
        this.mDischargeStartLevel = this.mHistoryCur.batteryLevel;
        pullPendingStateUpdatesLocked();
        addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
        byte b = this.mHistoryCur.batteryLevel;
        this.mCurrentBatteryLevel = b;
        this.mDischargePlugLevel = b;
        this.mDischargeUnplugLevel = b;
        this.mDischargeCurrentLevel = b;
        this.mOnBatteryTimeBase.reset(j, j2);
        this.mOnBatteryScreenOffTimeBase.reset(j, j2);
        if ((this.mHistoryCur.states & 524288) == 0) {
            if (isScreenOn(this.mScreenState)) {
                this.mDischargeScreenOnUnplugLevel = this.mHistoryCur.batteryLevel;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else if (isScreenDoze(this.mScreenState)) {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = this.mHistoryCur.batteryLevel;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = this.mHistoryCur.batteryLevel;
            }
            this.mDischargeAmountScreenOn = 0;
            this.mDischargeAmountScreenOff = 0;
            this.mDischargeAmountScreenDoze = 0;
        }
        initActiveHistoryEventsLocked(jElapsedRealtime, jUptimeMillis);
    }

    private void resetAllStatsLocked() {
        long jUptimeMillis = this.mClocks.uptimeMillis();
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        this.mStartCount = 0;
        long j = jUptimeMillis * 1000;
        long j2 = 1000 * jElapsedRealtime;
        initTimes(j, j2);
        this.mScreenOnTimer.reset(false);
        this.mScreenDozeTimer.reset(false);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].reset(false);
        }
        PowerProfile powerProfile = this.mPowerProfile;
        if (powerProfile != null) {
            this.mEstimatedBatteryCapacity = (int) powerProfile.getBatteryCapacity();
        } else {
            this.mEstimatedBatteryCapacity = -1;
        }
        this.mMinLearnedBatteryCapacity = -1;
        this.mMaxLearnedBatteryCapacity = -1;
        this.mInteractiveTimer.reset(false);
        this.mPowerSaveModeEnabledTimer.reset(false);
        this.mLastIdleTimeStart = jElapsedRealtime;
        this.mLongestLightIdleTime = 0L;
        this.mLongestFullIdleTime = 0L;
        this.mDeviceIdleModeLightTimer.reset(false);
        this.mDeviceIdleModeFullTimer.reset(false);
        this.mDeviceLightIdlingTimer.reset(false);
        this.mDeviceIdlingTimer.reset(false);
        this.mPhoneOnTimer.reset(false);
        this.mAudioOnTimer.reset(false);
        this.mVideoOnTimer.reset(false);
        this.mFlashlightOnTimer.reset(false);
        this.mCameraOnTimer.reset(false);
        this.mBluetoothScanTimer.reset(false);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2].reset(false);
        }
        this.mPhoneSignalScanningTimer.reset(false);
        for (int i3 = 0; i3 < 21; i3++) {
            this.mPhoneDataConnectionsTimer[i3].reset(false);
        }
        for (int i4 = 0; i4 < 10; i4++) {
            this.mNetworkByteActivityCounters[i4].reset(false);
            this.mNetworkPacketActivityCounters[i4].reset(false);
        }
        this.mMobileRadioActiveTimer.reset(false);
        this.mMobileRadioActivePerAppTimer.reset(false);
        this.mMobileRadioActiveAdjustedTime.reset(false);
        this.mMobileRadioActiveUnknownTime.reset(false);
        this.mMobileRadioActiveUnknownCount.reset(false);
        this.mWifiOnTimer.reset(false);
        this.mGlobalWifiRunningTimer.reset(false);
        for (int i5 = 0; i5 < 8; i5++) {
            this.mWifiStateTimer[i5].reset(false);
        }
        for (int i6 = 0; i6 < 13; i6++) {
            this.mWifiSupplStateTimer[i6].reset(false);
        }
        for (int i7 = 0; i7 < 5; i7++) {
            this.mWifiSignalStrengthsTimer[i7].reset(false);
        }
        this.mWifiMulticastWakelockTimer.reset(false);
        this.mWifiActiveTimer.reset(false);
        this.mWifiActivity.reset(false);
        for (int i8 = 0; i8 < 2; i8++) {
            this.mGpsSignalQualityTimer[i8].reset(false);
        }
        this.mBluetoothActivity.reset(false);
        this.mModemActivity.reset(false);
        this.mUnpluggedNumConnectivityChange = 0;
        this.mLoadedNumConnectivityChange = 0;
        this.mNumConnectivityChange = 0;
        int i9 = 0;
        while (i9 < this.mUidStats.size()) {
            if (this.mUidStats.valueAt(i9).reset(j, j2)) {
                SparseArray<Uid> sparseArray = this.mUidStats;
                sparseArray.remove(sparseArray.keyAt(i9));
                i9--;
            }
            i9++;
        }
        if (this.mRpmStats.size() > 0) {
            Iterator<SamplingTimer> it = this.mRpmStats.values().iterator();
            while (it.hasNext()) {
                this.mOnBatteryTimeBase.remove(it.next());
            }
            this.mRpmStats.clear();
        }
        if (this.mScreenOffRpmStats.size() > 0) {
            Iterator<SamplingTimer> it2 = this.mScreenOffRpmStats.values().iterator();
            while (it2.hasNext()) {
                this.mOnBatteryScreenOffTimeBase.remove(it2.next());
            }
            this.mScreenOffRpmStats.clear();
        }
        if (this.mKernelWakelockStats.size() > 0) {
            Iterator<SamplingTimer> it3 = this.mKernelWakelockStats.values().iterator();
            while (it3.hasNext()) {
                this.mOnBatteryScreenOffTimeBase.remove(it3.next());
            }
            this.mKernelWakelockStats.clear();
        }
        if (this.mKernelMemoryStats.size() > 0) {
            for (int i10 = 0; i10 < this.mKernelMemoryStats.size(); i10++) {
                this.mOnBatteryTimeBase.remove(this.mKernelMemoryStats.valueAt(i10));
            }
            this.mKernelMemoryStats.clear();
        }
        if (this.mWakeupReasonStats.size() > 0) {
            Iterator<SamplingTimer> it4 = this.mWakeupReasonStats.values().iterator();
            while (it4.hasNext()) {
                this.mOnBatteryTimeBase.remove(it4.next());
            }
            this.mWakeupReasonStats.clear();
        }
        this.mLastHistoryStepDetails = null;
        this.mLastStepCpuSystemTime = 0L;
        this.mLastStepCpuUserTime = 0L;
        this.mCurStepCpuSystemTime = 0L;
        this.mCurStepCpuUserTime = 0L;
        this.mCurStepCpuUserTime = 0L;
        this.mLastStepCpuUserTime = 0L;
        this.mCurStepCpuSystemTime = 0L;
        this.mLastStepCpuSystemTime = 0L;
        this.mCurStepStatUserTime = 0L;
        this.mLastStepStatUserTime = 0L;
        this.mCurStepStatSystemTime = 0L;
        this.mLastStepStatSystemTime = 0L;
        this.mCurStepStatIOWaitTime = 0L;
        this.mLastStepStatIOWaitTime = 0L;
        this.mCurStepStatIrqTime = 0L;
        this.mLastStepStatIrqTime = 0L;
        this.mCurStepStatSoftIrqTime = 0L;
        this.mLastStepStatSoftIrqTime = 0L;
        this.mCurStepStatIdleTime = 0L;
        this.mLastStepStatIdleTime = 0L;
        this.mNumAllUidCpuTimeReads = 0;
        this.mNumUidsRemoved = 0;
        initDischarge();
        clearHistoryLocked();
        this.mHandler.sendEmptyMessage(4);
    }

    private void initActiveHistoryEventsLocked(long j, long j2) {
        HashMap<String, SparseIntArray> stateForEvent;
        for (int i = 0; i < 22; i++) {
            if ((this.mRecordAllHistory || i != 1) && (stateForEvent = this.mActiveEvents.getStateForEvent(i)) != null) {
                for (Map.Entry<String, SparseIntArray> entry : stateForEvent.entrySet()) {
                    SparseIntArray value = entry.getValue();
                    for (int i2 = 0; i2 < value.size(); i2++) {
                        addHistoryEventLocked(j, j2, i, entry.getKey(), value.keyAt(i2));
                    }
                }
            }
        }
    }

    void updateDischargeScreenLevelsLocked(int i, int i2) {
        updateOldDischargeScreenLevelLocked(i);
        updateNewDischargeScreenLevelLocked(i2);
    }

    private void updateOldDischargeScreenLevelLocked(int i) {
        int i2;
        if (isScreenOn(i)) {
            int i3 = this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            if (i3 > 0) {
                this.mDischargeAmountScreenOn += i3;
                this.mDischargeAmountScreenOnSinceCharge += i3;
                return;
            }
            return;
        }
        if (isScreenDoze(i)) {
            int i4 = this.mDischargeScreenDozeUnplugLevel - this.mDischargeCurrentLevel;
            if (i4 > 0) {
                this.mDischargeAmountScreenDoze += i4;
                this.mDischargeAmountScreenDozeSinceCharge += i4;
                return;
            }
            return;
        }
        if (!isScreenOff(i) || (i2 = this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel) <= 0) {
            return;
        }
        this.mDischargeAmountScreenOff += i2;
        this.mDischargeAmountScreenOffSinceCharge += i2;
    }

    private void updateNewDischargeScreenLevelLocked(int i) {
        if (isScreenOn(i)) {
            this.mDischargeScreenOnUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = 0;
        } else if (isScreenDoze(i)) {
            this.mDischargeScreenOnUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = 0;
        } else if (isScreenOff(i)) {
            this.mDischargeScreenOnUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = 0;
            this.mDischargeScreenOffUnplugLevel = this.mDischargeCurrentLevel;
        }
    }

    public void pullPendingStateUpdatesLocked() {
        if (this.mOnBatteryInternal) {
            int i = this.mScreenState;
            updateDischargeScreenLevelsLocked(i, i);
        }
    }

    private NetworkStats readNetworkStatsLocked(String[] strArr) {
        try {
            if (ArrayUtils.isEmpty(strArr)) {
                return null;
            }
            return this.mNetworkStatsFactory.readNetworkStatsDetail(-1, strArr, 0, this.mNetworkStatsPool.acquire());
        } catch (IOException unused) {
            Slog.e(TAG, "failed to read network stats for ifaces: " + Arrays.toString(strArr));
            return null;
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x02d9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateWifiState(android.net.wifi.WifiActivityEnergyInfo r41) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 731
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.updateWifiState(android.net.wifi.WifiActivityEnergyInfo):void");
    }

    private ModemActivityInfo getDeltaModemActivityInfo(ModemActivityInfo modemActivityInfo) {
        if (modemActivityInfo == null) {
            return null;
        }
        int[] iArr = new int[5];
        for (int i = 0; i < 5; i++) {
            iArr[i] = modemActivityInfo.getTxTimeMillis()[i] - this.mLastModemActivityInfo.getTxTimeMillis()[i];
        }
        ModemActivityInfo modemActivityInfo2 = new ModemActivityInfo(modemActivityInfo.getTimestamp(), modemActivityInfo.getSleepTimeMillis() - this.mLastModemActivityInfo.getSleepTimeMillis(), modemActivityInfo.getIdleTimeMillis() - this.mLastModemActivityInfo.getIdleTimeMillis(), iArr, modemActivityInfo.getRxTimeMillis() - this.mLastModemActivityInfo.getRxTimeMillis(), modemActivityInfo.getEnergyUsed() - this.mLastModemActivityInfo.getEnergyUsed());
        this.mLastModemActivityInfo = modemActivityInfo;
        return modemActivityInfo2;
    }

    public void updateMobileRadioState(ModemActivityInfo modemActivityInfo) {
        NetworkStats networkStatsSubtract;
        NetworkStats networkStats;
        long j;
        long j2;
        long j3;
        ModemActivityInfo deltaModemActivityInfo = getDeltaModemActivityInfo(modemActivityInfo);
        addModemTxPowerToHistory(deltaModemActivityInfo);
        synchronized (this.mModemNetworkLock) {
            NetworkStats networkStatsLocked = readNetworkStatsLocked(this.mModemIfaces);
            networkStatsSubtract = null;
            if (networkStatsLocked != null) {
                networkStatsSubtract = NetworkStats.subtract(networkStatsLocked, this.mLastModemNetworkStats, null, null, this.mNetworkStatsPool.acquire());
                this.mNetworkStatsPool.release(this.mLastModemNetworkStats);
                this.mLastModemNetworkStats = networkStatsLocked;
            }
        }
        synchronized (this) {
            if (!this.mOnBatteryInternal) {
                if (networkStatsSubtract != null) {
                    this.mNetworkStatsPool.release(networkStatsSubtract);
                }
                return;
            }
            if (deltaModemActivityInfo != null) {
                this.mHasModemReporting = true;
                this.mModemActivity.getIdleTimeCounter().addCountLocked(deltaModemActivityInfo.getIdleTimeMillis());
                this.mModemActivity.getSleepTimeCounter().addCountLocked(deltaModemActivityInfo.getSleepTimeMillis());
                this.mModemActivity.getRxTimeCounter().addCountLocked(deltaModemActivityInfo.getRxTimeMillis());
                for (int i = 0; i < 5; i++) {
                    this.mModemActivity.getTxTimeCounters()[i].addCountLocked(deltaModemActivityInfo.getTxTimeMillis()[i]);
                }
                if (this.mPowerProfile.getAveragePower(PowerProfile.POWER_MODEM_CONTROLLER_OPERATING_VOLTAGE) / 1000.0d != 0.0d) {
                    double sleepTimeMillis = (((double) deltaModemActivityInfo.getSleepTimeMillis()) * this.mPowerProfile.getAveragePower(PowerProfile.POWER_MODEM_CONTROLLER_SLEEP)) + (((double) deltaModemActivityInfo.getIdleTimeMillis()) * this.mPowerProfile.getAveragePower(PowerProfile.POWER_MODEM_CONTROLLER_IDLE)) + (((double) deltaModemActivityInfo.getRxTimeMillis()) * this.mPowerProfile.getAveragePower(PowerProfile.POWER_MODEM_CONTROLLER_RX));
                    int[] txTimeMillis = deltaModemActivityInfo.getTxTimeMillis();
                    for (int i2 = 0; i2 < Math.min(txTimeMillis.length, 5); i2++) {
                        sleepTimeMillis += ((double) txTimeMillis[i2]) * this.mPowerProfile.getAveragePower(PowerProfile.POWER_MODEM_CONTROLLER_TX, i2);
                    }
                    this.mModemActivity.getPowerCounter().addCountLocked((long) sleepTimeMillis);
                }
            }
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long timeSinceMarkLocked = this.mMobileRadioActivePerAppTimer.getTimeSinceMarkLocked(1000 * jElapsedRealtime);
            this.mMobileRadioActivePerAppTimer.setMark(jElapsedRealtime);
            if (networkStatsSubtract != null) {
                NetworkStats.Entry entry = new NetworkStats.Entry();
                int size = networkStatsSubtract.size();
                long j4 = 0;
                long j5 = 0;
                long j6 = 0;
                int i3 = 0;
                while (i3 < size) {
                    entry = networkStatsSubtract.getValues(i3, entry);
                    if (entry.rxPackets != j4 || entry.txPackets != j4) {
                        j5 += entry.rxPackets;
                        j6 += entry.txPackets;
                        Uid uidStatsLocked = getUidStatsLocked(mapUid(entry.uid));
                        uidStatsLocked.noteNetworkActivityLocked(0, entry.rxBytes, entry.rxPackets);
                        uidStatsLocked.noteNetworkActivityLocked(1, entry.txBytes, entry.txPackets);
                        if (entry.set == 0) {
                            uidStatsLocked.noteNetworkActivityLocked(6, entry.rxBytes, entry.rxPackets);
                            uidStatsLocked.noteNetworkActivityLocked(7, entry.txBytes, entry.txPackets);
                        }
                        this.mNetworkByteActivityCounters[0].addCountLocked(entry.rxBytes);
                        this.mNetworkByteActivityCounters[1].addCountLocked(entry.txBytes);
                        this.mNetworkPacketActivityCounters[0].addCountLocked(entry.rxPackets);
                        this.mNetworkPacketActivityCounters[1].addCountLocked(entry.txPackets);
                    }
                    i3++;
                    timeSinceMarkLocked = timeSinceMarkLocked;
                    j4 = 0;
                }
                long j7 = timeSinceMarkLocked;
                long j8 = j5 + j6;
                long j9 = 0;
                if (j8 > 0) {
                    int i4 = 0;
                    long j10 = j8;
                    j2 = j7;
                    while (i4 < size) {
                        entry = networkStatsSubtract.getValues(i4, entry);
                        NetworkStats networkStats2 = networkStatsSubtract;
                        if (entry.rxPackets == j9 && entry.txPackets == j9) {
                            j3 = j5;
                        } else {
                            Uid uidStatsLocked2 = getUidStatsLocked(mapUid(entry.uid));
                            j3 = j5;
                            long j11 = entry.rxPackets + entry.txPackets;
                            long j12 = (j2 * j11) / j10;
                            uidStatsLocked2.noteMobileRadioActiveTimeLocked(j12);
                            j2 -= j12;
                            j10 -= j11;
                            if (deltaModemActivityInfo != null) {
                                ControllerActivityCounterImpl orCreateModemControllerActivityLocked = uidStatsLocked2.getOrCreateModemControllerActivityLocked();
                                long j13 = 0;
                                if (j3 > 0) {
                                    if (entry.rxPackets > 0) {
                                        orCreateModemControllerActivityLocked.getRxTimeCounter().addCountLocked((entry.rxPackets * ((long) deltaModemActivityInfo.getRxTimeMillis())) / j3);
                                    }
                                    j13 = 0;
                                }
                                if (j6 > j13 && entry.txPackets > j13) {
                                    int i5 = 0;
                                    while (i5 < 5) {
                                        orCreateModemControllerActivityLocked.getTxTimeCounters()[i5].addCountLocked((entry.txPackets * ((long) deltaModemActivityInfo.getTxTimeMillis()[i5])) / j6);
                                        i5++;
                                        networkStats2 = networkStats2;
                                    }
                                    networkStats2 = networkStats2;
                                }
                                i4++;
                                j5 = j3;
                                networkStatsSubtract = networkStats2;
                                j9 = 0;
                            }
                        }
                        i4++;
                        j5 = j3;
                        networkStatsSubtract = networkStats2;
                        j9 = 0;
                    }
                    networkStats = networkStatsSubtract;
                    j = j9;
                } else {
                    networkStats = networkStatsSubtract;
                    j = 0;
                    j2 = j7;
                }
                if (j2 > j) {
                    this.mMobileRadioActiveUnknownTime.addCountLocked(j2);
                    this.mMobileRadioActiveUnknownCount.addCountLocked(1L);
                }
                this.mNetworkStatsPool.release(networkStats);
            }
        }
    }

    private synchronized void addModemTxPowerToHistory(ModemActivityInfo modemActivityInfo) {
        if (modemActivityInfo == null) {
            return;
        }
        int[] txTimeMillis = modemActivityInfo.getTxTimeMillis();
        if (txTimeMillis != null && txTimeMillis.length == 5) {
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            int i = 0;
            for (int i2 = 1; i2 < txTimeMillis.length; i2++) {
                if (txTimeMillis[i2] > txTimeMillis[i]) {
                    i = i2;
                }
            }
            if (i == 4) {
                if (!this.mIsCellularTxPowerHigh) {
                    this.mHistoryCur.states2 |= 524288;
                    addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
                    this.mIsCellularTxPowerHigh = true;
                }
                return;
            }
            if (this.mIsCellularTxPowerHigh) {
                this.mHistoryCur.states2 &= -524289;
                addHistoryRecordLocked(jElapsedRealtime, jUptimeMillis);
                this.mIsCellularTxPowerHigh = false;
            }
        }
    }

    private final class BluetoothActivityInfoCache {
        long energy;
        long idleTimeMs;
        long rxTimeMs;
        long txTimeMs;
        SparseLongArray uidRxBytes;
        SparseLongArray uidTxBytes;

        private BluetoothActivityInfoCache() {
            this.uidRxBytes = new SparseLongArray();
            this.uidTxBytes = new SparseLongArray();
        }

        void set(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo) {
            this.idleTimeMs = bluetoothActivityEnergyInfo.getControllerIdleTimeMillis();
            this.rxTimeMs = bluetoothActivityEnergyInfo.getControllerRxTimeMillis();
            this.txTimeMs = bluetoothActivityEnergyInfo.getControllerTxTimeMillis();
            this.energy = bluetoothActivityEnergyInfo.getControllerEnergyUsed();
            if (bluetoothActivityEnergyInfo.getUidTraffic() != null) {
                for (UidTraffic uidTraffic : bluetoothActivityEnergyInfo.getUidTraffic()) {
                    this.uidRxBytes.put(uidTraffic.getUid(), uidTraffic.getRxBytes());
                    this.uidTxBytes.put(uidTraffic.getUid(), uidTraffic.getTxBytes());
                }
            }
        }
    }

    public void updateBluetoothStateLocked(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo) {
        long j;
        long j2;
        if (bluetoothActivityEnergyInfo == null || !this.mOnBatteryInternal) {
            return;
        }
        this.mHasBluetoothReporting = true;
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        long controllerRxTimeMillis = bluetoothActivityEnergyInfo.getControllerRxTimeMillis() - this.mLastBluetoothActivityInfo.rxTimeMs;
        long controllerTxTimeMillis = bluetoothActivityEnergyInfo.getControllerTxTimeMillis() - this.mLastBluetoothActivityInfo.txTimeMs;
        long controllerIdleTimeMillis = bluetoothActivityEnergyInfo.getControllerIdleTimeMillis() - this.mLastBluetoothActivityInfo.idleTimeMs;
        int size = this.mUidStats.size();
        long timeSinceMarkLocked = 0;
        for (int i = 0; i < size; i++) {
            Uid uidValueAt = this.mUidStats.valueAt(i);
            if (uidValueAt.mBluetoothScanTimer != null) {
                timeSinceMarkLocked += uidValueAt.mBluetoothScanTimer.getTimeSinceMarkLocked(jElapsedRealtime * 1000) / 1000;
            }
        }
        boolean z = timeSinceMarkLocked > controllerRxTimeMillis;
        boolean z2 = timeSinceMarkLocked > controllerTxTimeMillis;
        long j3 = controllerRxTimeMillis;
        long j4 = controllerTxTimeMillis;
        int i2 = 0;
        while (i2 < size) {
            Uid uidValueAt2 = this.mUidStats.valueAt(i2);
            int i3 = size;
            if (uidValueAt2.mBluetoothScanTimer == null) {
                j = controllerIdleTimeMillis;
            } else {
                j = controllerIdleTimeMillis;
                long timeSinceMarkLocked2 = uidValueAt2.mBluetoothScanTimer.getTimeSinceMarkLocked(jElapsedRealtime * 1000) / 1000;
                if (timeSinceMarkLocked2 > 0) {
                    uidValueAt2.mBluetoothScanTimer.setMark(jElapsedRealtime);
                    if (z) {
                        j2 = (controllerRxTimeMillis * timeSinceMarkLocked2) / timeSinceMarkLocked;
                        jElapsedRealtime = jElapsedRealtime;
                    } else {
                        jElapsedRealtime = jElapsedRealtime;
                        j2 = timeSinceMarkLocked2;
                    }
                    if (z2) {
                        timeSinceMarkLocked2 = (timeSinceMarkLocked2 * controllerTxTimeMillis) / timeSinceMarkLocked;
                    }
                    ControllerActivityCounterImpl orCreateBluetoothControllerActivityLocked = uidValueAt2.getOrCreateBluetoothControllerActivityLocked();
                    orCreateBluetoothControllerActivityLocked.getRxTimeCounter().addCountLocked(j2);
                    orCreateBluetoothControllerActivityLocked.getTxTimeCounters()[0].addCountLocked(timeSinceMarkLocked2);
                    j3 -= j2;
                    j4 -= timeSinceMarkLocked2;
                }
                i2++;
                z = z;
                size = i3;
                controllerIdleTimeMillis = j;
                jElapsedRealtime = jElapsedRealtime;
            }
            z = z;
            i2++;
            z = z;
            size = i3;
            controllerIdleTimeMillis = j;
            jElapsedRealtime = jElapsedRealtime;
        }
        long j5 = controllerIdleTimeMillis;
        UidTraffic[] uidTraffic = bluetoothActivityEnergyInfo.getUidTraffic();
        int length = uidTraffic != null ? uidTraffic.length : 0;
        int i4 = 0;
        long j6 = 0;
        long j7 = 0;
        while (i4 < length) {
            UidTraffic uidTraffic2 = uidTraffic[i4];
            long j8 = controllerTxTimeMillis;
            long rxBytes = uidTraffic2.getRxBytes() - this.mLastBluetoothActivityInfo.uidRxBytes.get(uidTraffic2.getUid());
            long j9 = controllerRxTimeMillis;
            long txBytes = uidTraffic2.getTxBytes() - this.mLastBluetoothActivityInfo.uidTxBytes.get(uidTraffic2.getUid());
            this.mNetworkByteActivityCounters[4].addCountLocked(rxBytes);
            this.mNetworkByteActivityCounters[5].addCountLocked(txBytes);
            Uid uidStatsLocked = getUidStatsLocked(mapUid(uidTraffic2.getUid()));
            uidStatsLocked.noteNetworkActivityLocked(4, rxBytes, 0L);
            uidStatsLocked.noteNetworkActivityLocked(5, txBytes, 0L);
            j7 += rxBytes;
            j6 += txBytes;
            i4++;
            controllerTxTimeMillis = j8;
            controllerRxTimeMillis = j9;
        }
        long j10 = controllerRxTimeMillis;
        long j11 = controllerTxTimeMillis;
        if ((j6 != 0 || j7 != 0) && (j3 != 0 || j4 != 0)) {
            for (int i5 = 0; i5 < length; i5++) {
                UidTraffic uidTraffic3 = uidTraffic[i5];
                int uid = uidTraffic3.getUid();
                long rxBytes2 = uidTraffic3.getRxBytes() - this.mLastBluetoothActivityInfo.uidRxBytes.get(uid);
                long txBytes2 = uidTraffic3.getTxBytes() - this.mLastBluetoothActivityInfo.uidTxBytes.get(uid);
                ControllerActivityCounterImpl orCreateBluetoothControllerActivityLocked2 = getUidStatsLocked(mapUid(uid)).getOrCreateBluetoothControllerActivityLocked();
                if (j7 > 0 && rxBytes2 > 0) {
                    long j12 = (rxBytes2 * j3) / j7;
                    orCreateBluetoothControllerActivityLocked2.getRxTimeCounter().addCountLocked(j12);
                    j3 -= j12;
                }
                if (j6 > 0 && txBytes2 > 0) {
                    long j13 = (txBytes2 * j4) / j6;
                    orCreateBluetoothControllerActivityLocked2.getTxTimeCounters()[0].addCountLocked(j13);
                    j4 -= j13;
                }
            }
        }
        this.mBluetoothActivity.getRxTimeCounter().addCountLocked(j10);
        this.mBluetoothActivity.getTxTimeCounters()[0].addCountLocked(j11);
        this.mBluetoothActivity.getIdleTimeCounter().addCountLocked(j5);
        double averagePower = this.mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_CONTROLLER_OPERATING_VOLTAGE) / 1000.0d;
        if (averagePower != 0.0d) {
            this.mBluetoothActivity.getPowerCounter().addCountLocked((long) ((bluetoothActivityEnergyInfo.getControllerEnergyUsed() - this.mLastBluetoothActivityInfo.energy) / averagePower));
        }
        this.mLastBluetoothActivityInfo.set(bluetoothActivityEnergyInfo);
    }

    public void updateRpmStatsLocked() {
        if (this.mPlatformIdleStateCallback == null) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime - this.mLastRpmStatsUpdateTimeMs >= 1000) {
            this.mPlatformIdleStateCallback.fillLowPowerStats(this.mTmpRpmStats);
            this.mLastRpmStatsUpdateTimeMs = jElapsedRealtime;
        }
        for (Map.Entry<String, RpmStats.PowerStatePlatformSleepState> entry : this.mTmpRpmStats.mPlatformLowPowerStats.entrySet()) {
            String key = entry.getKey();
            getRpmTimerLocked(key).update(entry.getValue().mTimeMs * 1000, entry.getValue().mCount);
            for (Map.Entry<String, RpmStats.PowerStateElement> entry2 : entry.getValue().mVoters.entrySet()) {
                getRpmTimerLocked(key + "." + entry2.getKey()).update(entry2.getValue().mTimeMs * 1000, entry2.getValue().mCount);
            }
        }
        for (Map.Entry<String, RpmStats.PowerStateSubsystem> entry3 : this.mTmpRpmStats.mSubsystemLowPowerStats.entrySet()) {
            String key2 = entry3.getKey();
            for (Map.Entry<String, RpmStats.PowerStateElement> entry4 : entry3.getValue().mStates.entrySet()) {
                getRpmTimerLocked(key2 + "." + entry4.getKey()).update(entry4.getValue().mTimeMs * 1000, entry4.getValue().mCount);
            }
        }
    }

    public void updateKernelWakelocksLocked() {
        KernelWakelockStats kernelWakelockStats = this.mKernelWakelockReader.readKernelWakelockStats(this.mTmpWakelockStats);
        if (kernelWakelockStats == null) {
            Slog.w(TAG, "Couldn't get kernel wake lock stats");
            return;
        }
        for (Map.Entry<String, KernelWakelockStats.Entry> entry : kernelWakelockStats.entrySet()) {
            String key = entry.getKey();
            KernelWakelockStats.Entry value = entry.getValue();
            SamplingTimer samplingTimer = this.mKernelWakelockStats.get(key);
            if (samplingTimer == null) {
                samplingTimer = new SamplingTimer(this.mClocks, this.mOnBatteryScreenOffTimeBase);
                this.mKernelWakelockStats.put(key, samplingTimer);
            }
            samplingTimer.update(value.mTotalTime, value.mCount);
            samplingTimer.setUpdateVersion(value.mVersion);
        }
        int i = 0;
        Iterator<Map.Entry<String, SamplingTimer>> it = this.mKernelWakelockStats.entrySet().iterator();
        while (it.hasNext()) {
            SamplingTimer value2 = it.next().getValue();
            if (value2.getUpdateVersion() != kernelWakelockStats.kernelWakelockVersion) {
                value2.endSample();
                i++;
            }
        }
        if (kernelWakelockStats.isEmpty()) {
            Slog.wtf(TAG, "All kernel wakelocks had time of zero");
        }
        if (i == this.mKernelWakelockStats.size()) {
            Slog.wtf(TAG, "All kernel wakelocks were set stale. new version=" + kernelWakelockStats.kernelWakelockVersion);
        }
    }

    public void updateKernelMemoryBandwidthLocked() {
        SamplingTimer samplingTimer;
        this.mKernelMemoryBandwidthStats.updateStats();
        LongSparseLongArray bandwidthEntries = this.mKernelMemoryBandwidthStats.getBandwidthEntries();
        int size = bandwidthEntries.size();
        for (int i = 0; i < size; i++) {
            int iIndexOfKey = this.mKernelMemoryStats.indexOfKey(bandwidthEntries.keyAt(i));
            if (iIndexOfKey >= 0) {
                samplingTimer = this.mKernelMemoryStats.valueAt(iIndexOfKey);
            } else {
                samplingTimer = new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase);
                this.mKernelMemoryStats.put(bandwidthEntries.keyAt(i), samplingTimer);
            }
            samplingTimer.update(bandwidthEntries.valueAt(i), 1);
        }
    }

    public boolean isOnBatteryLocked() {
        return this.mOnBatteryTimeBase.isRunning();
    }

    public boolean isOnBatteryScreenOffLocked() {
        return this.mOnBatteryScreenOffTimeBase.isRunning();
    }

    public void updateCpuTimeLocked(boolean z, boolean z2) {
        ArrayList<StopwatchTimer> arrayList;
        PowerProfile powerProfile = this.mPowerProfile;
        if (powerProfile == null) {
            return;
        }
        if (this.mCpuFreqs == null) {
            this.mCpuFreqs = this.mKernelUidCpuFreqTimeReader.readFreqs(powerProfile);
        }
        if (z2) {
            arrayList = new ArrayList<>();
            for (int size = this.mPartialTimers.size() - 1; size >= 0; size--) {
                StopwatchTimer stopwatchTimer = this.mPartialTimers.get(size);
                if (stopwatchTimer.mInList && stopwatchTimer.mUid != null && stopwatchTimer.mUid.mUid != 1000) {
                    arrayList.add(stopwatchTimer);
                }
            }
        } else {
            arrayList = null;
        }
        markPartialTimersAsEligible();
        if (!z) {
            this.mKernelUidCpuTimeReader.readDelta(null);
            this.mKernelUidCpuFreqTimeReader.readDelta(null);
            this.mNumAllUidCpuTimeReads += 2;
            if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                this.mKernelUidCpuActiveTimeReader.readDelta(null);
                this.mKernelUidCpuClusterTimeReader.readDelta(null);
                this.mNumAllUidCpuTimeReads += 2;
            }
            for (int length = this.mKernelCpuSpeedReaders.length - 1; length >= 0; length--) {
                this.mKernelCpuSpeedReaders[length].readDelta();
            }
            return;
        }
        this.mUserInfoProvider.refreshUserIds();
        SparseLongArray sparseLongArray = this.mKernelUidCpuFreqTimeReader.perClusterTimesAvailable() ? null : new SparseLongArray();
        readKernelUidCpuTimesLocked(arrayList, sparseLongArray, z);
        if (sparseLongArray != null) {
            updateClusterSpeedTimes(sparseLongArray, z);
        }
        readKernelUidCpuFreqTimesLocked(arrayList, z, z2);
        this.mNumAllUidCpuTimeReads += 2;
        if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
            readKernelUidCpuActiveTimesLocked(z);
            readKernelUidCpuClusterTimesLocked(z);
            this.mNumAllUidCpuTimeReads += 2;
        }
    }

    public void markPartialTimersAsEligible() {
        int i;
        if (ArrayUtils.referenceEquals(this.mPartialTimers, this.mLastPartialTimers)) {
            for (int size = this.mPartialTimers.size() - 1; size >= 0; size--) {
                this.mPartialTimers.get(size).mInList = true;
            }
            return;
        }
        int size2 = this.mLastPartialTimers.size() - 1;
        while (true) {
            if (size2 < 0) {
                break;
            }
            this.mLastPartialTimers.get(size2).mInList = false;
            size2--;
        }
        this.mLastPartialTimers.clear();
        int size3 = this.mPartialTimers.size();
        for (i = 0; i < size3; i++) {
            StopwatchTimer stopwatchTimer = this.mPartialTimers.get(i);
            stopwatchTimer.mInList = true;
            this.mLastPartialTimers.add(stopwatchTimer);
        }
    }

    public void updateClusterSpeedTimes(SparseLongArray sparseLongArray, boolean z) {
        BatteryStatsImpl batteryStatsImpl = this;
        int length = batteryStatsImpl.mKernelCpuSpeedReaders.length;
        long[][] jArr = new long[length][];
        long j = 0;
        int i = 0;
        while (true) {
            KernelCpuSpeedReader[] kernelCpuSpeedReaderArr = batteryStatsImpl.mKernelCpuSpeedReaders;
            if (i >= kernelCpuSpeedReaderArr.length) {
                break;
            }
            jArr[i] = kernelCpuSpeedReaderArr[i].readDelta();
            if (jArr[i] != null) {
                for (int length2 = jArr[i].length - 1; length2 >= 0; length2--) {
                    j += jArr[i][length2];
                }
            }
            i++;
        }
        if (j != 0) {
            int size = sparseLongArray.size();
            int i2 = 0;
            while (i2 < size) {
                Uid uidStatsLocked = batteryStatsImpl.getUidStatsLocked(sparseLongArray.keyAt(i2));
                long jValueAt = sparseLongArray.valueAt(i2);
                int numCpuClusters = batteryStatsImpl.mPowerProfile.getNumCpuClusters();
                if (uidStatsLocked.mCpuClusterSpeedTimesUs == null || uidStatsLocked.mCpuClusterSpeedTimesUs.length != numCpuClusters) {
                    uidStatsLocked.mCpuClusterSpeedTimesUs = new LongSamplingCounter[numCpuClusters][];
                }
                int i3 = 0;
                while (i3 < length) {
                    int length3 = jArr[i3].length;
                    if (uidStatsLocked.mCpuClusterSpeedTimesUs[i3] == null || length3 != uidStatsLocked.mCpuClusterSpeedTimesUs[i3].length) {
                        uidStatsLocked.mCpuClusterSpeedTimesUs[i3] = new LongSamplingCounter[length3];
                    }
                    LongSamplingCounter[] longSamplingCounterArr = uidStatsLocked.mCpuClusterSpeedTimesUs[i3];
                    int i4 = 0;
                    while (i4 < length3) {
                        if (longSamplingCounterArr[i4] == null) {
                            longSamplingCounterArr[i4] = new LongSamplingCounter(batteryStatsImpl.mOnBatteryTimeBase);
                        }
                        longSamplingCounterArr[i4].addCountLocked((jArr[i3][i4] * jValueAt) / j, z);
                        i4++;
                        batteryStatsImpl = this;
                        length = length;
                        jArr = jArr;
                    }
                    i3++;
                    batteryStatsImpl = this;
                }
                i2++;
                batteryStatsImpl = this;
            }
        }
    }

    public void readKernelUidCpuTimesLocked(ArrayList<StopwatchTimer> arrayList, final SparseLongArray sparseLongArray, final boolean z) {
        long j = 0;
        this.mTempTotalCpuSystemTimeUs = 0L;
        this.mTempTotalCpuUserTimeUs = 0L;
        int i = 0;
        final int size = arrayList == null ? 0 : arrayList.size();
        long jUptimeMillis = this.mClocks.uptimeMillis();
        this.mKernelUidCpuTimeReader.readDelta(new KernelUidCpuTimeReader.Callback() { // from class: com.android.internal.os.-$$Lambda$BatteryStatsImpl$cVkGM5pv4uMLFgnMwqPRDhEl5a0
            @Override // com.android.internal.os.KernelUidCpuTimeReader.Callback
            public final void onUidCpuTime(int i2, long j2, long j3) {
                this.f$0.lambda$readKernelUidCpuTimesLocked$0$BatteryStatsImpl(size, z, sparseLongArray, i2, j2, j3);
            }
        });
        long jUptimeMillis2 = this.mClocks.uptimeMillis() - jUptimeMillis;
        if (jUptimeMillis2 >= 100) {
            Slog.d(TAG, "Reading cpu stats took " + jUptimeMillis2 + "ms");
        }
        if (size > 0) {
            this.mTempTotalCpuUserTimeUs = (this.mTempTotalCpuUserTimeUs * 50) / 100;
            this.mTempTotalCpuSystemTimeUs = (this.mTempTotalCpuSystemTimeUs * 50) / 100;
            while (i < size) {
                StopwatchTimer stopwatchTimer = arrayList.get(i);
                long j2 = size - i;
                int i2 = (int) (this.mTempTotalCpuUserTimeUs / j2);
                int i3 = (int) (this.mTempTotalCpuSystemTimeUs / j2);
                long j3 = i2;
                stopwatchTimer.mUid.mUserCpuTime.addCountLocked(j3, z);
                long j4 = i3;
                stopwatchTimer.mUid.mSystemCpuTime.addCountLocked(j4, z);
                if (sparseLongArray != null) {
                    int uid = stopwatchTimer.mUid.getUid();
                    sparseLongArray.put(uid, sparseLongArray.get(uid, j) + j3 + j4);
                }
                stopwatchTimer.mUid.getProcessStatsLocked("*wakelock*").addCpuTimeLocked(i2 / 1000, i3 / 1000, z);
                this.mTempTotalCpuUserTimeUs -= j3;
                this.mTempTotalCpuSystemTimeUs -= j4;
                i++;
                j = 0;
            }
        }
    }

    public /* synthetic */ void lambda$readKernelUidCpuTimesLocked$0$BatteryStatsImpl(int i, boolean z, SparseLongArray sparseLongArray, int i2, long j, long j2) {
        int iMapUid = mapUid(i2);
        if (Process.isIsolated(iMapUid)) {
            this.mKernelUidCpuTimeReader.removeUid(iMapUid);
            Slog.d(TAG, "Got readings for an isolated uid with no mapping: " + iMapUid);
            return;
        }
        if (!this.mUserInfoProvider.exists(UserHandle.getUserId(iMapUid))) {
            Slog.d(TAG, "Got readings for an invalid user's uid " + iMapUid);
            this.mKernelUidCpuTimeReader.removeUid(iMapUid);
            return;
        }
        Uid uidStatsLocked = getUidStatsLocked(iMapUid);
        this.mTempTotalCpuUserTimeUs += j;
        this.mTempTotalCpuSystemTimeUs += j2;
        if (i > 0) {
            j = (j * 50) / 100;
            j2 = (j2 * 50) / 100;
        }
        uidStatsLocked.mUserCpuTime.addCountLocked(j, z);
        uidStatsLocked.mSystemCpuTime.addCountLocked(j2, z);
        if (sparseLongArray != null) {
            sparseLongArray.put(uidStatsLocked.getUid(), j + j2);
        }
    }

    public void readKernelUidCpuFreqTimesLocked(ArrayList<StopwatchTimer> arrayList, final boolean z, final boolean z2) {
        final boolean zPerClusterTimesAvailable = this.mKernelUidCpuFreqTimeReader.perClusterTimesAvailable();
        int size = arrayList == null ? 0 : arrayList.size();
        final int numCpuClusters = this.mPowerProfile.getNumCpuClusters();
        this.mWakeLockAllocationsUs = null;
        long jUptimeMillis = this.mClocks.uptimeMillis();
        final int i = size;
        this.mKernelUidCpuFreqTimeReader.readDelta(new KernelUidCpuFreqTimeReader.Callback() { // from class: com.android.internal.os.-$$Lambda$BatteryStatsImpl$qYIdEyLMO9XI4FHBl_g5LWknDZQ
            @Override // com.android.internal.os.KernelUidCpuFreqTimeReader.Callback
            public final void onUidCpuFreqTime(int i2, long[] jArr) {
                this.f$0.lambda$readKernelUidCpuFreqTimesLocked$1$BatteryStatsImpl(z, z2, zPerClusterTimesAvailable, numCpuClusters, i, i2, jArr);
            }
        });
        long jUptimeMillis2 = this.mClocks.uptimeMillis() - jUptimeMillis;
        if (jUptimeMillis2 >= 100) {
            Slog.d(TAG, "Reading cpu freq times took " + jUptimeMillis2 + "ms");
        }
        if (this.mWakeLockAllocationsUs != null) {
            for (int i2 = 0; i2 < size; i2++) {
                Uid uid = arrayList.get(i2).mUid;
                if (uid.mCpuClusterSpeedTimesUs == null || uid.mCpuClusterSpeedTimesUs.length != numCpuClusters) {
                    uid.mCpuClusterSpeedTimesUs = new LongSamplingCounter[numCpuClusters][];
                }
                for (int i3 = 0; i3 < numCpuClusters; i3++) {
                    int numSpeedStepsInCpuCluster = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i3);
                    if (uid.mCpuClusterSpeedTimesUs[i3] == null || uid.mCpuClusterSpeedTimesUs[i3].length != numSpeedStepsInCpuCluster) {
                        uid.mCpuClusterSpeedTimesUs[i3] = new LongSamplingCounter[numSpeedStepsInCpuCluster];
                    }
                    LongSamplingCounter[] longSamplingCounterArr = uid.mCpuClusterSpeedTimesUs[i3];
                    for (int i4 = 0; i4 < numSpeedStepsInCpuCluster; i4++) {
                        if (longSamplingCounterArr[i4] == null) {
                            longSamplingCounterArr[i4] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                        }
                        long j = this.mWakeLockAllocationsUs[i3][i4] / ((long) (size - i2));
                        longSamplingCounterArr[i4].addCountLocked(j, z);
                        long[] jArr = this.mWakeLockAllocationsUs[i3];
                        jArr[i4] = jArr[i4] - j;
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$readKernelUidCpuFreqTimesLocked$1$BatteryStatsImpl(boolean z, boolean z2, boolean z3, int i, int i2, int i3, long[] jArr) {
        long j;
        int iMapUid = mapUid(i3);
        if (Process.isIsolated(iMapUid)) {
            this.mKernelUidCpuFreqTimeReader.removeUid(iMapUid);
            Slog.d(TAG, "Got freq readings for an isolated uid with no mapping: " + iMapUid);
            return;
        }
        if (!this.mUserInfoProvider.exists(UserHandle.getUserId(iMapUid))) {
            Slog.d(TAG, "Got freq readings for an invalid user's uid " + iMapUid);
            this.mKernelUidCpuFreqTimeReader.removeUid(iMapUid);
            return;
        }
        Uid uidStatsLocked = getUidStatsLocked(iMapUid);
        if (uidStatsLocked.mCpuFreqTimeMs == null || uidStatsLocked.mCpuFreqTimeMs.getSize() != jArr.length) {
            uidStatsLocked.mCpuFreqTimeMs = new LongSamplingCounterArray(this.mOnBatteryTimeBase);
        }
        uidStatsLocked.mCpuFreqTimeMs.addCountLocked(jArr, z);
        if (uidStatsLocked.mScreenOffCpuFreqTimeMs == null || uidStatsLocked.mScreenOffCpuFreqTimeMs.getSize() != jArr.length) {
            uidStatsLocked.mScreenOffCpuFreqTimeMs = new LongSamplingCounterArray(this.mOnBatteryScreenOffTimeBase);
        }
        uidStatsLocked.mScreenOffCpuFreqTimeMs.addCountLocked(jArr, z2);
        if (z3) {
            if (uidStatsLocked.mCpuClusterSpeedTimesUs == null || uidStatsLocked.mCpuClusterSpeedTimesUs.length != i) {
                uidStatsLocked.mCpuClusterSpeedTimesUs = new LongSamplingCounter[i][];
            }
            if (i2 > 0 && this.mWakeLockAllocationsUs == null) {
                this.mWakeLockAllocationsUs = new long[i][];
            }
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int numSpeedStepsInCpuCluster = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i5);
                if (uidStatsLocked.mCpuClusterSpeedTimesUs[i5] == null || uidStatsLocked.mCpuClusterSpeedTimesUs[i5].length != numSpeedStepsInCpuCluster) {
                    uidStatsLocked.mCpuClusterSpeedTimesUs[i5] = new LongSamplingCounter[numSpeedStepsInCpuCluster];
                }
                if (i2 > 0) {
                    long[][] jArr2 = this.mWakeLockAllocationsUs;
                    if (jArr2[i5] == null) {
                        jArr2[i5] = new long[numSpeedStepsInCpuCluster];
                    }
                }
                LongSamplingCounter[] longSamplingCounterArr = uidStatsLocked.mCpuClusterSpeedTimesUs[i5];
                for (int i6 = 0; i6 < numSpeedStepsInCpuCluster; i6++) {
                    if (longSamplingCounterArr[i6] == null) {
                        longSamplingCounterArr[i6] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                    }
                    long[][] jArr3 = this.mWakeLockAllocationsUs;
                    if (jArr3 != null) {
                        j = ((jArr[i4] * 1000) * 50) / 100;
                        long[] jArr4 = jArr3[i5];
                        jArr4[i6] = jArr4[i6] + ((jArr[i4] * 1000) - j);
                    } else {
                        j = jArr[i4] * 1000;
                    }
                    longSamplingCounterArr[i6].addCountLocked(j, z);
                    i4++;
                }
            }
        }
    }

    public void readKernelUidCpuActiveTimesLocked(final boolean z) {
        long jUptimeMillis = this.mClocks.uptimeMillis();
        this.mKernelUidCpuActiveTimeReader.readDelta(new KernelUidCpuActiveTimeReader.Callback() { // from class: com.android.internal.os.-$$Lambda$BatteryStatsImpl$mMCK0IbpOZu45KINuNCoRayjoDU
            @Override // com.android.internal.os.KernelUidCpuActiveTimeReader.Callback
            public final void onUidCpuActiveTime(int i, long j) {
                this.f$0.lambda$readKernelUidCpuActiveTimesLocked$2$BatteryStatsImpl(z, i, j);
            }
        });
        long jUptimeMillis2 = this.mClocks.uptimeMillis() - jUptimeMillis;
        if (jUptimeMillis2 >= 100) {
            Slog.d(TAG, "Reading cpu active times took " + jUptimeMillis2 + "ms");
        }
    }

    public /* synthetic */ void lambda$readKernelUidCpuActiveTimesLocked$2$BatteryStatsImpl(boolean z, int i, long j) {
        int iMapUid = mapUid(i);
        if (Process.isIsolated(iMapUid)) {
            this.mKernelUidCpuActiveTimeReader.removeUid(iMapUid);
            Slog.w(TAG, "Got active times for an isolated uid with no mapping: " + iMapUid);
            return;
        }
        if (!this.mUserInfoProvider.exists(UserHandle.getUserId(iMapUid))) {
            Slog.w(TAG, "Got active times for an invalid user's uid " + iMapUid);
            this.mKernelUidCpuActiveTimeReader.removeUid(iMapUid);
            return;
        }
        getUidStatsLocked(iMapUid).mCpuActiveTimeMs.addCountLocked(j, z);
    }

    public void readKernelUidCpuClusterTimesLocked(final boolean z) {
        long jUptimeMillis = this.mClocks.uptimeMillis();
        this.mKernelUidCpuClusterTimeReader.readDelta(new KernelUidCpuClusterTimeReader.Callback() { // from class: com.android.internal.os.-$$Lambda$BatteryStatsImpl$WJBQdQHGlhcwV7yfM8vNEWWvVp0
            @Override // com.android.internal.os.KernelUidCpuClusterTimeReader.Callback
            public final void onUidCpuPolicyTime(int i, long[] jArr) {
                this.f$0.lambda$readKernelUidCpuClusterTimesLocked$3$BatteryStatsImpl(z, i, jArr);
            }
        });
        long jUptimeMillis2 = this.mClocks.uptimeMillis() - jUptimeMillis;
        if (jUptimeMillis2 >= 100) {
            Slog.d(TAG, "Reading cpu cluster times took " + jUptimeMillis2 + "ms");
        }
    }

    public /* synthetic */ void lambda$readKernelUidCpuClusterTimesLocked$3$BatteryStatsImpl(boolean z, int i, long[] jArr) {
        int iMapUid = mapUid(i);
        if (Process.isIsolated(iMapUid)) {
            this.mKernelUidCpuClusterTimeReader.removeUid(iMapUid);
            Slog.w(TAG, "Got cluster times for an isolated uid with no mapping: " + iMapUid);
            return;
        }
        if (!this.mUserInfoProvider.exists(UserHandle.getUserId(iMapUid))) {
            Slog.w(TAG, "Got cluster times for an invalid user's uid " + iMapUid);
            this.mKernelUidCpuClusterTimeReader.removeUid(iMapUid);
            return;
        }
        getUidStatsLocked(iMapUid).mCpuClusterTimesMs.addCountLocked(jArr, z);
    }

    boolean setChargingLocked(boolean z) {
        if (this.mCharging == z) {
            return false;
        }
        this.mCharging = z;
        if (z) {
            this.mHistoryCur.states2 |= 16777216;
        } else {
            this.mHistoryCur.states2 &= -16777217;
        }
        this.mHandler.sendEmptyMessage(3);
        return true;
    }

    protected void setOnBatteryLocked(long j, long j2, boolean z, int i, int i2, int i3) {
        boolean z2;
        Message messageObtainMessage = this.mHandler.obtainMessage(2);
        messageObtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(messageObtainMessage);
        long j3 = j2 * 1000;
        long j4 = j * 1000;
        int i4 = this.mScreenState;
        boolean z3 = false;
        if (z) {
            if (this.mNoAutoReset || (i != 5 && i2 < 90 && ((this.mDischargeCurrentLevel >= 20 || i2 < 80) && (getHighDischargeAmountSinceCharge() < 200 || this.mHistoryBuffer.dataSize() < MAX_HISTORY_BUFFER)))) {
                z2 = false;
            } else {
                Slog.i(TAG, "Resetting battery stats: level=" + i2 + " status=" + i + " dischargeLevel=" + this.mDischargeCurrentLevel + " lowAmount=" + getLowDischargeAmountSinceCharge() + " highAmount=" + getHighDischargeAmountSinceCharge());
                if (getLowDischargeAmountSinceCharge() >= 20) {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    final Parcel parcelObtain = Parcel.obtain();
                    writeSummaryToParcel(parcelObtain, true);
                    final long jUptimeMillis2 = SystemClock.uptimeMillis() - jUptimeMillis;
                    BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.internal.os.BatteryStatsImpl.3
                        @Override // java.lang.Runnable
                        public void run() {
                            Parcel parcel;
                            synchronized (BatteryStatsImpl.this.mCheckinFile) {
                                long jUptimeMillis3 = SystemClock.uptimeMillis();
                                FileOutputStream fileOutputStreamStartWrite = null;
                                try {
                                    try {
                                        fileOutputStreamStartWrite = BatteryStatsImpl.this.mCheckinFile.startWrite();
                                        fileOutputStreamStartWrite.write(parcelObtain.marshall());
                                        fileOutputStreamStartWrite.flush();
                                        FileUtils.sync(fileOutputStreamStartWrite);
                                        fileOutputStreamStartWrite.close();
                                        BatteryStatsImpl.this.mCheckinFile.finishWrite(fileOutputStreamStartWrite);
                                        EventLogTags.writeCommitSysConfigFile("batterystats-checkin", (jUptimeMillis2 + SystemClock.uptimeMillis()) - jUptimeMillis3);
                                        parcel = parcelObtain;
                                    } catch (IOException e) {
                                        Slog.w("BatteryStats", "Error writing checkin battery statistics", e);
                                        BatteryStatsImpl.this.mCheckinFile.failWrite(fileOutputStreamStartWrite);
                                        parcel = parcelObtain;
                                    }
                                    parcel.recycle();
                                } catch (Throwable th) {
                                    parcelObtain.recycle();
                                    throw th;
                                }
                            }
                        }
                    });
                }
                resetAllStatsLocked();
                if (i3 > 0 && i2 > 0) {
                    this.mEstimatedBatteryCapacity = (int) (((double) (i3 / 1000)) / (((double) i2) / 100.0d));
                }
                this.mDischargeStartLevel = i2;
                this.mDischargeStepTracker.init();
                z2 = true;
            }
            boolean z4 = z2;
            if (this.mCharging) {
                setChargingLocked(false);
            }
            this.mLastChargingStateLevel = i2;
            this.mOnBatteryInternal = true;
            this.mOnBattery = true;
            this.mLastDischargeStepLevel = i2;
            this.mMinDischargeStepLevel = i2;
            this.mDischargeStepTracker.clearTime();
            this.mDailyDischargeStepTracker.clearTime();
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = 0;
            pullPendingStateUpdatesLocked();
            this.mHistoryCur.batteryLevel = (byte) i2;
            this.mHistoryCur.states &= -524289;
            if (z2) {
                this.mRecordingHistory = true;
                startRecordingHistory(j, j2, z2);
            }
            addHistoryRecordLocked(j, j2);
            this.mDischargeUnplugLevel = i2;
            this.mDischargeCurrentLevel = i2;
            if (isScreenOn(i4)) {
                this.mDischargeScreenOnUnplugLevel = i2;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else if (isScreenDoze(i4)) {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = i2;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = i2;
            }
            this.mDischargeAmountScreenOn = 0;
            this.mDischargeAmountScreenDoze = 0;
            this.mDischargeAmountScreenOff = 0;
            updateTimeBasesLocked(true, i4, j3, j4);
            z3 = z4;
        } else {
            this.mLastChargingStateLevel = i2;
            this.mOnBatteryInternal = false;
            this.mOnBattery = false;
            pullPendingStateUpdatesLocked();
            this.mHistoryCur.batteryLevel = (byte) i2;
            this.mHistoryCur.states |= 524288;
            addHistoryRecordLocked(j, j2);
            this.mDischargePlugLevel = i2;
            this.mDischargeCurrentLevel = i2;
            int i5 = this.mDischargeUnplugLevel;
            if (i2 < i5) {
                this.mLowDischargeAmountSinceCharge += (i5 - i2) - 1;
                this.mHighDischargeAmountSinceCharge += i5 - i2;
            }
            updateDischargeScreenLevelsLocked(i4, i4);
            updateTimeBasesLocked(false, i4, j3, j4);
            this.mChargeStepTracker.init();
            this.mLastChargeStepLevel = i2;
            this.mMaxChargeStepLevel = i2;
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = 0;
        }
        if ((z3 || this.mLastWriteTime + DateUtils.MINUTE_IN_MILLIS < j) && this.mFile != null) {
            writeAsyncLocked();
        }
    }

    private void startRecordingHistory(long j, long j2, boolean z) {
        this.mRecordingHistory = true;
        this.mHistoryCur.currentTime = System.currentTimeMillis();
        addHistoryBufferLocked(j, z ? (byte) 7 : (byte) 5, this.mHistoryCur);
        this.mHistoryCur.currentTime = 0L;
        if (z) {
            initActiveHistoryEventsLocked(j, j2);
        }
    }

    private void recordCurrentTimeChangeLocked(long j, long j2, long j3) {
        if (this.mRecordingHistory) {
            this.mHistoryCur.currentTime = j;
            addHistoryBufferLocked(j2, (byte) 5, this.mHistoryCur);
            this.mHistoryCur.currentTime = 0L;
        }
    }

    private void recordShutdownLocked(long j, long j2) {
        if (this.mRecordingHistory) {
            this.mHistoryCur.currentTime = System.currentTimeMillis();
            addHistoryBufferLocked(j, (byte) 8, this.mHistoryCur);
            this.mHistoryCur.currentTime = 0L;
        }
    }

    private void scheduleSyncExternalStatsLocked(String str, int i) {
        ExternalStatsSync externalStatsSync = this.mExternalSync;
        if (externalStatsSync != null) {
            externalStatsSync.scheduleSync(str, i);
        }
    }

    public void setBatteryStateLocked(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        long j;
        long j2;
        boolean z;
        boolean chargingLocked;
        boolean z2;
        int i9;
        int iMax = Math.max(0, i5);
        reportChangesToStatsLog(this.mHaveBatteryLevel ? this.mHistoryCur : null, i, i3, i4);
        boolean zIsOnBattery = isOnBattery(i3, i);
        long jUptimeMillis = this.mClocks.uptimeMillis();
        long jElapsedRealtime = this.mClocks.elapsedRealtime();
        if (!this.mHaveBatteryLevel) {
            this.mHaveBatteryLevel = true;
            if (zIsOnBattery == this.mOnBattery) {
                if (zIsOnBattery) {
                    this.mHistoryCur.states &= -524289;
                } else {
                    this.mHistoryCur.states |= 524288;
                }
            }
            this.mHistoryCur.states2 |= 16777216;
            this.mHistoryCur.batteryStatus = (byte) i;
            this.mHistoryCur.batteryLevel = (byte) i4;
            this.mHistoryCur.batteryChargeUAh = i7;
            this.mLastDischargeStepLevel = i4;
            this.mLastChargeStepLevel = i4;
            this.mMinDischargeStepLevel = i4;
            this.mMaxChargeStepLevel = i4;
            this.mLastChargingStateLevel = i4;
        } else if (this.mCurrentBatteryLevel != i4 || this.mOnBattery != zIsOnBattery) {
            recordDailyStatsIfNeededLocked(i4 >= 100 && zIsOnBattery);
        }
        byte b = this.mHistoryCur.batteryStatus;
        if (zIsOnBattery) {
            this.mDischargeCurrentLevel = i4;
            if (this.mRecordingHistory) {
                j = jElapsedRealtime;
                j2 = jUptimeMillis;
                z = zIsOnBattery;
            } else {
                this.mRecordingHistory = true;
                j = jElapsedRealtime;
                j2 = jUptimeMillis;
                z = zIsOnBattery;
                startRecordingHistory(jElapsedRealtime, jUptimeMillis, true);
            }
        } else {
            j = jElapsedRealtime;
            j2 = jUptimeMillis;
            z = zIsOnBattery;
            if (i4 < 96 && i != 1 && !this.mRecordingHistory) {
                this.mRecordingHistory = true;
                startRecordingHistory(j, j2, true);
            }
        }
        this.mCurrentBatteryLevel = i4;
        if (this.mDischargePlugLevel < 0) {
            this.mDischargePlugLevel = i4;
        }
        if (z != this.mOnBattery) {
            this.mHistoryCur.batteryLevel = (byte) i4;
            this.mHistoryCur.batteryStatus = (byte) i;
            this.mHistoryCur.batteryHealth = (byte) i2;
            this.mHistoryCur.batteryPlugType = (byte) i3;
            this.mHistoryCur.batteryTemperature = (short) iMax;
            this.mHistoryCur.batteryVoltage = (char) i6;
            if (i7 < this.mHistoryCur.batteryChargeUAh) {
                long j3 = this.mHistoryCur.batteryChargeUAh - i7;
                this.mDischargeCounter.addCountLocked(j3);
                this.mDischargeScreenOffCounter.addCountLocked(j3);
                if (isScreenDoze(this.mScreenState)) {
                    this.mDischargeScreenDozeCounter.addCountLocked(j3);
                }
                int i10 = this.mDeviceIdleMode;
                if (i10 == 1) {
                    this.mDischargeLightDozeCounter.addCountLocked(j3);
                } else if (i10 == 2) {
                    this.mDischargeDeepDozeCounter.addCountLocked(j3);
                }
            }
            this.mHistoryCur.batteryChargeUAh = i7;
            setOnBatteryLocked(j, j2, z, b, i4, i7);
        } else {
            if (this.mHistoryCur.batteryLevel != i4) {
                this.mHistoryCur.batteryLevel = (byte) i4;
                this.mExternalSync.scheduleSyncDueToBatteryLevelChange(this.mConstants.BATTERY_LEVEL_COLLECTION_DELAY_MS);
                chargingLocked = true;
            } else {
                chargingLocked = false;
            }
            if (this.mHistoryCur.batteryStatus != i) {
                this.mHistoryCur.batteryStatus = (byte) i;
                chargingLocked = true;
            }
            if (this.mHistoryCur.batteryHealth != i2) {
                this.mHistoryCur.batteryHealth = (byte) i2;
                chargingLocked = true;
            }
            if (this.mHistoryCur.batteryPlugType != i3) {
                this.mHistoryCur.batteryPlugType = (byte) i3;
                chargingLocked = true;
            }
            if (iMax >= this.mHistoryCur.batteryTemperature + 10 || iMax <= this.mHistoryCur.batteryTemperature - 10) {
                this.mHistoryCur.batteryTemperature = (short) iMax;
                chargingLocked = true;
            }
            if (i6 > this.mHistoryCur.batteryVoltage + 20 || i6 < this.mHistoryCur.batteryVoltage - 20) {
                this.mHistoryCur.batteryVoltage = (char) i6;
                chargingLocked = true;
            }
            if (i7 >= this.mHistoryCur.batteryChargeUAh + 10 || i7 <= this.mHistoryCur.batteryChargeUAh - 10) {
                if (i7 < this.mHistoryCur.batteryChargeUAh) {
                    long j4 = this.mHistoryCur.batteryChargeUAh - i7;
                    this.mDischargeCounter.addCountLocked(j4);
                    this.mDischargeScreenOffCounter.addCountLocked(j4);
                    if (isScreenDoze(this.mScreenState)) {
                        this.mDischargeScreenDozeCounter.addCountLocked(j4);
                    }
                    int i11 = this.mDeviceIdleMode;
                    if (i11 == 1) {
                        this.mDischargeLightDozeCounter.addCountLocked(j4);
                    } else if (i11 == 2) {
                        this.mDischargeDeepDozeCounter.addCountLocked(j4);
                    }
                }
                this.mHistoryCur.batteryChargeUAh = i7;
                chargingLocked = true;
            }
            long j5 = (((long) this.mInitStepMode) << 48) | (((long) this.mModStepMode) << 56) | (((long) (i4 & 255)) << 40);
            if (z) {
                chargingLocked |= setChargingLocked(false);
                int i12 = this.mLastDischargeStepLevel;
                if (i12 != i4 && this.mMinDischargeStepLevel > i4) {
                    long j6 = j;
                    this.mDischargeStepTracker.addLevelSteps(i12 - i4, j5, j6);
                    this.mDailyDischargeStepTracker.addLevelSteps(this.mLastDischargeStepLevel - i4, j5, j6);
                    this.mLastDischargeStepLevel = i4;
                    this.mMinDischargeStepLevel = i4;
                    this.mInitStepMode = this.mCurStepMode;
                    this.mModStepMode = 0;
                }
            } else {
                if (i4 >= 90) {
                    z2 = true;
                    chargingLocked |= setChargingLocked(true);
                    this.mLastChargeStepLevel = i4;
                } else {
                    z2 = true;
                }
                if (!this.mCharging) {
                    if (this.mLastChargeStepLevel < i4) {
                        chargingLocked |= setChargingLocked(z2);
                        this.mLastChargeStepLevel = i4;
                    }
                } else if (this.mLastChargeStepLevel > i4) {
                    chargingLocked |= setChargingLocked(false);
                    this.mLastChargeStepLevel = i4;
                }
                int i13 = this.mLastChargeStepLevel;
                if (i13 != i4 && this.mMaxChargeStepLevel < i4) {
                    long j7 = j;
                    this.mChargeStepTracker.addLevelSteps(i4 - i13, j5, j7);
                    this.mDailyChargeStepTracker.addLevelSteps(i4 - this.mLastChargeStepLevel, j5, j7);
                    this.mLastChargeStepLevel = i4;
                    this.mMaxChargeStepLevel = i4;
                    this.mInitStepMode = this.mCurStepMode;
                    this.mModStepMode = 0;
                }
            }
            if (chargingLocked) {
                addHistoryRecordLocked(j, j2);
            }
        }
        if (!z && (i == 5 || i == 1)) {
            this.mRecordingHistory = false;
        }
        int i14 = this.mMinLearnedBatteryCapacity;
        if (i14 == -1) {
            i9 = i8;
            this.mMinLearnedBatteryCapacity = i9;
        } else {
            i9 = i8;
            Math.min(i14, i9);
        }
        this.mMaxLearnedBatteryCapacity = Math.max(this.mMaxLearnedBatteryCapacity, i9);
    }

    private void reportChangesToStatsLog(BatteryStats.HistoryItem historyItem, int i, int i2, int i3) {
        if (historyItem == null || historyItem.batteryStatus != i) {
            StatsLog.write(31, i);
        }
        if (historyItem == null || historyItem.batteryPlugType != i2) {
            StatsLog.write(32, i2);
        }
        if (historyItem == null || historyItem.batteryLevel != i3) {
            StatsLog.write(30, i3);
        }
    }

    public long getAwakeTimeBattery() {
        return computeBatteryUptime(getBatteryUptimeLocked(), 1);
    }

    public long getAwakeTimePlugged() {
        return (this.mClocks.uptimeMillis() * 1000) - getAwakeTimeBattery();
    }

    @Override // android.os.BatteryStats
    public long computeUptime(long j, int i) {
        long uptimeStart;
        if (i == 0) {
            return this.mUptime + (j - this.mUptimeStart);
        }
        if (i == 1) {
            uptimeStart = this.mUptimeStart;
        } else {
            if (i != 2) {
                return 0L;
            }
            uptimeStart = this.mOnBatteryTimeBase.getUptimeStart();
        }
        return j - uptimeStart;
    }

    @Override // android.os.BatteryStats
    public long computeRealtime(long j, int i) {
        long realtimeStart;
        if (i == 0) {
            return this.mRealtime + (j - this.mRealtimeStart);
        }
        if (i == 1) {
            realtimeStart = this.mRealtimeStart;
        } else {
            if (i != 2) {
                return 0L;
            }
            realtimeStart = this.mOnBatteryTimeBase.getRealtimeStart();
        }
        return j - realtimeStart;
    }

    @Override // android.os.BatteryStats
    public long computeBatteryUptime(long j, int i) {
        return this.mOnBatteryTimeBase.computeUptime(j, i);
    }

    @Override // android.os.BatteryStats
    public long computeBatteryRealtime(long j, int i) {
        return this.mOnBatteryTimeBase.computeRealtime(j, i);
    }

    @Override // android.os.BatteryStats
    public long computeBatteryScreenOffUptime(long j, int i) {
        return this.mOnBatteryScreenOffTimeBase.computeUptime(j, i);
    }

    @Override // android.os.BatteryStats
    public long computeBatteryScreenOffRealtime(long j, int i) {
        return this.mOnBatteryScreenOffTimeBase.computeRealtime(j, i);
    }

    private long computeTimePerLevel(long[] jArr, int i) {
        if (i <= 0) {
            return -1L;
        }
        long j = 0;
        for (int i2 = 0; i2 < i; i2++) {
            j += jArr[i2] & BatteryStats.STEP_LEVEL_TIME_MASK;
        }
        return j / ((long) i);
    }

    @Override // android.os.BatteryStats
    public long computeBatteryTimeRemaining(long j) {
        if (!this.mOnBattery || this.mDischargeStepTracker.mNumStepDurations < 1) {
            return -1L;
        }
        long jComputeTimePerLevel = this.mDischargeStepTracker.computeTimePerLevel();
        if (jComputeTimePerLevel <= 0) {
            return -1L;
        }
        return jComputeTimePerLevel * ((long) this.mCurrentBatteryLevel) * 1000;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.LevelStepTracker getDischargeLevelStepTracker() {
        return this.mDischargeStepTracker;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.LevelStepTracker getDailyDischargeLevelStepTracker() {
        return this.mDailyDischargeStepTracker;
    }

    @Override // android.os.BatteryStats
    public long computeChargeTimeRemaining(long j) {
        if (this.mOnBattery || this.mChargeStepTracker.mNumStepDurations < 1) {
            return -1L;
        }
        long jComputeTimePerLevel = this.mChargeStepTracker.computeTimePerLevel();
        if (jComputeTimePerLevel <= 0) {
            return -1L;
        }
        return jComputeTimePerLevel * ((long) (100 - this.mCurrentBatteryLevel)) * 1000;
    }

    public CellularBatteryStats getCellularBatteryStats() {
        CellularBatteryStats cellularBatteryStats = new CellularBatteryStats();
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        BatteryStats.ControllerActivityCounter modemControllerActivity = getModemControllerActivity();
        int i = 0;
        long countLocked = modemControllerActivity.getSleepTimeCounter().getCountLocked(0);
        long countLocked2 = modemControllerActivity.getIdleTimeCounter().getCountLocked(0);
        long countLocked3 = modemControllerActivity.getRxTimeCounter().getCountLocked(0);
        long countLocked4 = modemControllerActivity.getPowerCounter().getCountLocked(0);
        long[] jArr = new long[21];
        for (int i2 = 0; i2 < 21; i2++) {
            jArr[i2] = getPhoneDataConnectionTime(i2, jElapsedRealtime, 0) / 1000;
        }
        long j = 1000;
        long[] jArr2 = new long[5];
        int i3 = 0;
        while (i3 < 5) {
            jArr2[i3] = getPhoneSignalStrengthTime(i3, jElapsedRealtime, 0) / j;
            i3++;
            j = 1000;
        }
        int iMin = Math.min(5, modemControllerActivity.getTxTimeCounters().length);
        long[] jArr3 = new long[iMin];
        while (i < iMin) {
            jArr3[i] = modemControllerActivity.getTxTimeCounters()[i].getCountLocked(0);
            long j2 = jArr3[i];
            i++;
            modemControllerActivity = modemControllerActivity;
            iMin = iMin;
        }
        cellularBatteryStats.setLoggingDurationMs(computeBatteryRealtime(jElapsedRealtime, 0) / 1000);
        cellularBatteryStats.setKernelActiveTimeMs(getMobileRadioActiveTime(jElapsedRealtime, 0) / 1000);
        cellularBatteryStats.setNumPacketsTx(getNetworkActivityPackets(1, 0));
        cellularBatteryStats.setNumBytesTx(getNetworkActivityBytes(1, 0));
        cellularBatteryStats.setNumPacketsRx(getNetworkActivityPackets(0, 0));
        cellularBatteryStats.setNumBytesRx(getNetworkActivityBytes(0, 0));
        cellularBatteryStats.setSleepTimeMs(countLocked);
        cellularBatteryStats.setIdleTimeMs(countLocked2);
        cellularBatteryStats.setRxTimeMs(countLocked3);
        cellularBatteryStats.setEnergyConsumedMaMs(countLocked4);
        cellularBatteryStats.setTimeInRatMs(jArr);
        cellularBatteryStats.setTimeInRxSignalStrengthLevelMs(jArr2);
        cellularBatteryStats.setTxTimeMs(jArr3);
        return cellularBatteryStats;
    }

    public WifiBatteryStats getWifiBatteryStats() {
        WifiBatteryStats wifiBatteryStats = new WifiBatteryStats();
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        BatteryStats.ControllerActivityCounter wifiControllerActivity = getWifiControllerActivity();
        int i = 0;
        long countLocked = wifiControllerActivity.getIdleTimeCounter().getCountLocked(0);
        long countLocked2 = wifiControllerActivity.getScanTimeCounter().getCountLocked(0);
        long countLocked3 = wifiControllerActivity.getRxTimeCounter().getCountLocked(0);
        long countLocked4 = wifiControllerActivity.getTxTimeCounters()[0].getCountLocked(0);
        long j = jElapsedRealtime;
        long jComputeBatteryRealtime = (computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, 0) / 1000) - ((countLocked + countLocked3) + countLocked4);
        long countLocked5 = wifiControllerActivity.getPowerCounter().getCountLocked(0);
        int i2 = 0;
        long countLocked6 = 0;
        while (i2 < this.mUidStats.size()) {
            countLocked6 += (long) this.mUidStats.valueAt(i2).mWifiScanTimer.getCountLocked(i);
            i2++;
            countLocked = countLocked;
            countLocked5 = countLocked5;
            i = 0;
        }
        long j2 = countLocked5;
        long j3 = countLocked;
        long j4 = countLocked6;
        long[] jArr = new long[8];
        int i3 = 0;
        for (int i4 = 8; i3 < i4; i4 = 8) {
            long j5 = j;
            jArr[i3] = getWifiStateTime(i3, j5, 0) / 1000;
            i3++;
            j = j5;
        }
        long j6 = j;
        long[] jArr2 = new long[13];
        int i5 = 0;
        for (int i6 = 13; i5 < i6; i6 = 13) {
            jArr2[i5] = getWifiSupplStateTime(i5, j6, 0) / 1000;
            i5++;
        }
        long[] jArr3 = new long[5];
        int i7 = 0;
        for (int i8 = 5; i7 < i8; i8 = 5) {
            jArr3[i7] = getWifiSignalStrengthTime(i7, j6, 0) / 1000;
            i7++;
        }
        wifiBatteryStats.setLoggingDurationMs(computeBatteryRealtime(j6, 0) / 1000);
        wifiBatteryStats.setKernelActiveTimeMs(getWifiActiveTime(j6, 0) / 1000);
        wifiBatteryStats.setNumPacketsTx(getNetworkActivityPackets(3, 0));
        wifiBatteryStats.setNumBytesTx(getNetworkActivityBytes(3, 0));
        wifiBatteryStats.setNumPacketsRx(getNetworkActivityPackets(2, 0));
        wifiBatteryStats.setNumBytesRx(getNetworkActivityBytes(2, 0));
        wifiBatteryStats.setSleepTimeMs(jComputeBatteryRealtime);
        wifiBatteryStats.setIdleTimeMs(j3);
        wifiBatteryStats.setRxTimeMs(countLocked3);
        wifiBatteryStats.setTxTimeMs(countLocked4);
        wifiBatteryStats.setScanTimeMs(countLocked2);
        wifiBatteryStats.setEnergyConsumedMaMs(j2);
        wifiBatteryStats.setNumAppScanRequest(j4);
        wifiBatteryStats.setTimeInStateMs(jArr);
        wifiBatteryStats.setTimeInSupplicantStateMs(jArr2);
        wifiBatteryStats.setTimeInRxSignalStrengthLevelMs(jArr3);
        return wifiBatteryStats;
    }

    public GpsBatteryStats getGpsBatteryStats() {
        GpsBatteryStats gpsBatteryStats = new GpsBatteryStats();
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        gpsBatteryStats.setLoggingDurationMs(computeBatteryRealtime(jElapsedRealtime, 0) / 1000);
        gpsBatteryStats.setEnergyConsumedMaMs(getGpsBatteryDrainMaMs());
        long[] jArr = new long[2];
        for (int i = 0; i < 2; i++) {
            jArr[i] = getGpsSignalQualityTime(i, jElapsedRealtime, 0) / 1000;
        }
        gpsBatteryStats.setTimeInGpsSignalQualityLevel(jArr);
        return gpsBatteryStats;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.LevelStepTracker getChargeLevelStepTracker() {
        return this.mChargeStepTracker;
    }

    @Override // android.os.BatteryStats
    public BatteryStats.LevelStepTracker getDailyChargeLevelStepTracker() {
        return this.mDailyChargeStepTracker;
    }

    @Override // android.os.BatteryStats
    public ArrayList<BatteryStats.PackageChange> getDailyPackageChanges() {
        return this.mDailyPackageChanges;
    }

    protected long getBatteryUptimeLocked() {
        return this.mOnBatteryTimeBase.getUptime(this.mClocks.uptimeMillis() * 1000);
    }

    @Override // android.os.BatteryStats
    public long getBatteryUptime(long j) {
        return this.mOnBatteryTimeBase.getUptime(j);
    }

    @Override // android.os.BatteryStats
    public long getBatteryRealtime(long j) {
        return this.mOnBatteryTimeBase.getRealtime(j);
    }

    @Override // android.os.BatteryStats
    public int getDischargeStartLevel() {
        int dischargeStartLevelLocked;
        synchronized (this) {
            dischargeStartLevelLocked = getDischargeStartLevelLocked();
        }
        return dischargeStartLevelLocked;
    }

    public int getDischargeStartLevelLocked() {
        return this.mDischargeUnplugLevel;
    }

    @Override // android.os.BatteryStats
    public int getDischargeCurrentLevel() {
        int dischargeCurrentLevelLocked;
        synchronized (this) {
            dischargeCurrentLevelLocked = getDischargeCurrentLevelLocked();
        }
        return dischargeCurrentLevelLocked;
    }

    public int getDischargeCurrentLevelLocked() {
        return this.mDischargeCurrentLevel;
    }

    @Override // android.os.BatteryStats
    public int getLowDischargeAmountSinceCharge() {
        int i;
        synchronized (this) {
            i = this.mLowDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                i += (this.mDischargeUnplugLevel - this.mDischargeCurrentLevel) - 1;
            }
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getHighDischargeAmountSinceCharge() {
        int i;
        synchronized (this) {
            i = this.mHighDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                i += this.mDischargeUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmount(int i) {
        int dischargeStartLevel;
        if (i == 0) {
            dischargeStartLevel = getHighDischargeAmountSinceCharge();
        } else {
            dischargeStartLevel = getDischargeStartLevel() - getDischargeCurrentLevel();
        }
        if (dischargeStartLevel < 0) {
            return 0;
        }
        return dischargeStartLevel;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOn() {
        int i;
        synchronized (this) {
            i = this.mDischargeAmountScreenOn;
            if (this.mOnBattery && isScreenOn(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                i += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOnSinceCharge() {
        int i;
        synchronized (this) {
            i = this.mDischargeAmountScreenOnSinceCharge;
            if (this.mOnBattery && isScreenOn(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                i += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOff() {
        int dischargeAmountScreenDoze;
        synchronized (this) {
            int i = this.mDischargeAmountScreenOff;
            if (this.mOnBattery && isScreenOff(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                i += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
            dischargeAmountScreenDoze = i + getDischargeAmountScreenDoze();
        }
        return dischargeAmountScreenDoze;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOffSinceCharge() {
        int dischargeAmountScreenDozeSinceCharge;
        synchronized (this) {
            int i = this.mDischargeAmountScreenOffSinceCharge;
            if (this.mOnBattery && isScreenOff(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                i += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
            dischargeAmountScreenDozeSinceCharge = i + getDischargeAmountScreenDozeSinceCharge();
        }
        return dischargeAmountScreenDozeSinceCharge;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenDoze() {
        int i;
        synchronized (this) {
            i = this.mDischargeAmountScreenDoze;
            if (this.mOnBattery && isScreenDoze(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenDozeUnplugLevel) {
                i += this.mDischargeScreenDozeUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenDozeSinceCharge() {
        int i;
        synchronized (this) {
            i = this.mDischargeAmountScreenDozeSinceCharge;
            if (this.mOnBattery && isScreenDoze(this.mScreenState) && this.mDischargeCurrentLevel < this.mDischargeScreenDozeUnplugLevel) {
                i += this.mDischargeScreenDozeUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return i;
    }

    public Uid getUidStatsLocked(int i) {
        Uid uid = this.mUidStats.get(i);
        if (uid != null) {
            return uid;
        }
        Uid uid2 = new Uid(this, i);
        this.mUidStats.put(i, uid2);
        return uid2;
    }

    public Uid getAvailableUidStatsLocked(int i) {
        return this.mUidStats.get(i);
    }

    public void onCleanupUserLocked(int i) {
        this.mPendingRemovedUids.add(new UidToRemove(UserHandle.getUid(i, 0), UserHandle.getUid(i, Process.LAST_ISOLATED_UID), this.mClocks.elapsedRealtime()));
    }

    public void onUserRemovedLocked(int i) {
        int uid = UserHandle.getUid(i, 0);
        int uid2 = UserHandle.getUid(i, Process.LAST_ISOLATED_UID);
        this.mUidStats.put(uid, null);
        this.mUidStats.put(uid2, null);
        int iIndexOfKey = this.mUidStats.indexOfKey(uid);
        this.mUidStats.removeAtRange(iIndexOfKey, (this.mUidStats.indexOfKey(uid2) - iIndexOfKey) + 1);
    }

    public void removeUidStatsLocked(int i) {
        this.mUidStats.remove(i);
        this.mPendingRemovedUids.add(new UidToRemove(this, i, this.mClocks.elapsedRealtime()));
    }

    public Uid.Proc getProcessStatsLocked(int i, String str) {
        return getUidStatsLocked(mapUid(i)).getProcessStatsLocked(str);
    }

    public Uid.Pkg getPackageStatsLocked(int i, String str) {
        return getUidStatsLocked(mapUid(i)).getPackageStatsLocked(str);
    }

    public Uid.Pkg.Serv getServiceStatsLocked(int i, String str, String str2) {
        return getUidStatsLocked(mapUid(i)).getServiceStatsLocked(str, str2);
    }

    public void shutdownLocked() {
        recordShutdownLocked(this.mClocks.elapsedRealtime(), this.mClocks.uptimeMillis());
        writeSyncLocked();
        this.mShuttingDown = true;
    }

    public boolean trackPerProcStateCpuTimes() {
        return this.mConstants.TRACK_CPU_TIMES_BY_PROC_STATE && this.mPerProcStateCpuTimesAvailable;
    }

    public void systemServicesReady(Context context) {
        this.mConstants.startObserving(context.getContentResolver());
        registerUsbStateReceiver(context);
    }

    public final class Constants extends ContentObserver {
        private static final long DEFAULT_BATTERY_LEVEL_COLLECTION_DELAY_MS = 300000;
        private static final long DEFAULT_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = 600000;
        private static final long DEFAULT_KERNEL_UID_READERS_THROTTLE_TIME = 10000;
        private static final long DEFAULT_PROC_STATE_CPU_TIMES_READ_DELAY_MS = 5000;
        private static final boolean DEFAULT_TRACK_CPU_ACTIVE_CLUSTER_TIME = true;
        private static final boolean DEFAULT_TRACK_CPU_TIMES_BY_PROC_STATE = true;
        private static final long DEFAULT_UID_REMOVE_DELAY_MS = 300000;
        public static final String KEY_BATTERY_LEVEL_COLLECTION_DELAY_MS = "battery_level_collection_delay_ms";
        public static final String KEY_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = "external_stats_collection_rate_limit_ms";
        public static final String KEY_KERNEL_UID_READERS_THROTTLE_TIME = "kernel_uid_readers_throttle_time";
        public static final String KEY_PROC_STATE_CPU_TIMES_READ_DELAY_MS = "proc_state_cpu_times_read_delay_ms";
        public static final String KEY_TRACK_CPU_ACTIVE_CLUSTER_TIME = "track_cpu_active_cluster_time";
        public static final String KEY_TRACK_CPU_TIMES_BY_PROC_STATE = "track_cpu_times_by_proc_state";
        public static final String KEY_UID_REMOVE_DELAY_MS = "uid_remove_delay_ms";
        public long BATTERY_LEVEL_COLLECTION_DELAY_MS;
        public long EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS;
        public long KERNEL_UID_READERS_THROTTLE_TIME;
        public long PROC_STATE_CPU_TIMES_READ_DELAY_MS;
        public boolean TRACK_CPU_ACTIVE_CLUSTER_TIME;
        public boolean TRACK_CPU_TIMES_BY_PROC_STATE;
        public long UID_REMOVE_DELAY_MS;
        private final KeyValueListParser mParser;
        private ContentResolver mResolver;

        public Constants(Handler handler) {
            super(handler);
            this.TRACK_CPU_TIMES_BY_PROC_STATE = true;
            this.TRACK_CPU_ACTIVE_CLUSTER_TIME = true;
            this.PROC_STATE_CPU_TIMES_READ_DELAY_MS = 5000L;
            this.KERNEL_UID_READERS_THROTTLE_TIME = 10000L;
            this.UID_REMOVE_DELAY_MS = ParcelableCallAnalytics.MILLIS_IN_5_MINUTES;
            this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = DEFAULT_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS;
            this.BATTERY_LEVEL_COLLECTION_DELAY_MS = ParcelableCallAnalytics.MILLIS_IN_5_MINUTES;
            this.mParser = new KeyValueListParser(',');
        }

        public void startObserving(ContentResolver contentResolver) {
            this.mResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.BATTERY_STATS_CONSTANTS), false, this);
            updateConstants();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            updateConstants();
        }

        private void updateConstants() {
            synchronized (BatteryStatsImpl.this) {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, Settings.Global.BATTERY_STATS_CONSTANTS));
                } catch (IllegalArgumentException e) {
                    Slog.e(BatteryStatsImpl.TAG, "Bad batterystats settings", e);
                }
                updateTrackCpuTimesByProcStateLocked(this.TRACK_CPU_TIMES_BY_PROC_STATE, this.mParser.getBoolean(KEY_TRACK_CPU_TIMES_BY_PROC_STATE, true));
                this.TRACK_CPU_ACTIVE_CLUSTER_TIME = this.mParser.getBoolean(KEY_TRACK_CPU_ACTIVE_CLUSTER_TIME, true);
                updateProcStateCpuTimesReadDelayMs(this.PROC_STATE_CPU_TIMES_READ_DELAY_MS, this.mParser.getLong(KEY_PROC_STATE_CPU_TIMES_READ_DELAY_MS, 5000L));
                updateKernelUidReadersThrottleTime(this.KERNEL_UID_READERS_THROTTLE_TIME, this.mParser.getLong(KEY_KERNEL_UID_READERS_THROTTLE_TIME, 10000L));
                updateUidRemoveDelay(this.mParser.getLong(KEY_UID_REMOVE_DELAY_MS, ParcelableCallAnalytics.MILLIS_IN_5_MINUTES));
                this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = this.mParser.getLong(KEY_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS, DEFAULT_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS);
                this.BATTERY_LEVEL_COLLECTION_DELAY_MS = this.mParser.getLong(KEY_BATTERY_LEVEL_COLLECTION_DELAY_MS, ParcelableCallAnalytics.MILLIS_IN_5_MINUTES);
            }
        }

        private void updateTrackCpuTimesByProcStateLocked(boolean z, boolean z2) {
            this.TRACK_CPU_TIMES_BY_PROC_STATE = z2;
            if (!z2 || z) {
                return;
            }
            BatteryStatsImpl.this.mKernelSingleUidTimeReader.markDataAsStale(true);
            BatteryStatsImpl.this.mExternalSync.scheduleCpuSyncDueToSettingChange();
            BatteryStatsImpl.this.mNumSingleUidCpuTimeReads = 0L;
            BatteryStatsImpl.this.mNumBatchedSingleUidCpuTimeReads = 0L;
            BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
            batteryStatsImpl.mCpuTimeReadsTrackingStartTime = batteryStatsImpl.mClocks.uptimeMillis();
        }

        private void updateProcStateCpuTimesReadDelayMs(long j, long j2) {
            this.PROC_STATE_CPU_TIMES_READ_DELAY_MS = j2;
            if (j != j2) {
                BatteryStatsImpl.this.mNumSingleUidCpuTimeReads = 0L;
                BatteryStatsImpl.this.mNumBatchedSingleUidCpuTimeReads = 0L;
                BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
                batteryStatsImpl.mCpuTimeReadsTrackingStartTime = batteryStatsImpl.mClocks.uptimeMillis();
            }
        }

        private void updateKernelUidReadersThrottleTime(long j, long j2) {
            this.KERNEL_UID_READERS_THROTTLE_TIME = j2;
            if (j != j2) {
                BatteryStatsImpl.this.mKernelUidCpuTimeReader.setThrottleInterval(this.KERNEL_UID_READERS_THROTTLE_TIME);
                BatteryStatsImpl.this.mKernelUidCpuFreqTimeReader.setThrottleInterval(this.KERNEL_UID_READERS_THROTTLE_TIME);
                BatteryStatsImpl.this.mKernelUidCpuActiveTimeReader.setThrottleInterval(this.KERNEL_UID_READERS_THROTTLE_TIME);
                BatteryStatsImpl.this.mKernelUidCpuClusterTimeReader.setThrottleInterval(this.KERNEL_UID_READERS_THROTTLE_TIME);
            }
        }

        private void updateUidRemoveDelay(long j) {
            this.UID_REMOVE_DELAY_MS = j;
            BatteryStatsImpl.this.clearPendingRemovedUids();
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.print(KEY_TRACK_CPU_TIMES_BY_PROC_STATE);
            printWriter.print("=");
            printWriter.println(this.TRACK_CPU_TIMES_BY_PROC_STATE);
            printWriter.print(KEY_TRACK_CPU_ACTIVE_CLUSTER_TIME);
            printWriter.print("=");
            printWriter.println(this.TRACK_CPU_ACTIVE_CLUSTER_TIME);
            printWriter.print(KEY_PROC_STATE_CPU_TIMES_READ_DELAY_MS);
            printWriter.print("=");
            printWriter.println(this.PROC_STATE_CPU_TIMES_READ_DELAY_MS);
            printWriter.print(KEY_KERNEL_UID_READERS_THROTTLE_TIME);
            printWriter.print("=");
            printWriter.println(this.KERNEL_UID_READERS_THROTTLE_TIME);
            printWriter.print(KEY_EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS);
            printWriter.print("=");
            printWriter.println(this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS);
            printWriter.print(KEY_BATTERY_LEVEL_COLLECTION_DELAY_MS);
            printWriter.print("=");
            printWriter.println(this.BATTERY_LEVEL_COLLECTION_DELAY_MS);
        }
    }

    public long getExternalStatsCollectionRateLimitMs() {
        long j;
        synchronized (this) {
            j = this.mConstants.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS;
        }
        return j;
    }

    public void dumpConstantsLocked(PrintWriter printWriter) {
        this.mConstants.dumpLocked(printWriter);
    }

    public void dumpCpuStatsLocked(PrintWriter printWriter) {
        int size = this.mUidStats.size();
        printWriter.println("Per UID CPU user & system time in ms:");
        for (int i = 0; i < size; i++) {
            int iKeyAt = this.mUidStats.keyAt(i);
            Uid uid = this.mUidStats.get(iKeyAt);
            printWriter.print("  ");
            printWriter.print(iKeyAt);
            printWriter.print(": ");
            printWriter.print(uid.getUserCpuTimeUs(0) / 1000);
            printWriter.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            printWriter.println(uid.getSystemCpuTimeUs(0) / 1000);
        }
        printWriter.println("Per UID CPU active time in ms:");
        for (int i2 = 0; i2 < size; i2++) {
            int iKeyAt2 = this.mUidStats.keyAt(i2);
            Uid uid2 = this.mUidStats.get(iKeyAt2);
            if (uid2.getCpuActiveTime() > 0) {
                printWriter.print("  ");
                printWriter.print(iKeyAt2);
                printWriter.print(": ");
                printWriter.println(uid2.getCpuActiveTime());
            }
        }
        printWriter.println("Per UID CPU cluster time in ms:");
        for (int i3 = 0; i3 < size; i3++) {
            int iKeyAt3 = this.mUidStats.keyAt(i3);
            long[] cpuClusterTimes = this.mUidStats.get(iKeyAt3).getCpuClusterTimes();
            if (cpuClusterTimes != null) {
                printWriter.print("  ");
                printWriter.print(iKeyAt3);
                printWriter.print(": ");
                printWriter.println(Arrays.toString(cpuClusterTimes));
            }
        }
        printWriter.println("Per UID CPU frequency time in ms:");
        for (int i4 = 0; i4 < size; i4++) {
            int iKeyAt4 = this.mUidStats.keyAt(i4);
            long[] cpuFreqTimes = this.mUidStats.get(iKeyAt4).getCpuFreqTimes(0);
            if (cpuFreqTimes != null) {
                printWriter.print("  ");
                printWriter.print(iKeyAt4);
                printWriter.print(": ");
                printWriter.println(Arrays.toString(cpuFreqTimes));
            }
        }
    }

    public void writeAsyncLocked() {
        writeLocked(false);
    }

    public void writeSyncLocked() {
        writeLocked(true);
    }

    void writeLocked(boolean z) {
        if (this.mFile == null) {
            Slog.w("BatteryStats", "writeLocked: no file associated with this instance");
            return;
        }
        if (this.mShuttingDown) {
            return;
        }
        Parcel parcelObtain = Parcel.obtain();
        writeSummaryToParcel(parcelObtain, true);
        this.mLastWriteTime = this.mClocks.elapsedRealtime();
        Parcel parcel = this.mPendingWrite;
        if (parcel != null) {
            parcel.recycle();
        }
        this.mPendingWrite = parcelObtain;
        if (z) {
            commitPendingDataToDisk();
        } else {
            BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.internal.os.BatteryStatsImpl.4
                @Override // java.lang.Runnable
                public void run() {
                    BatteryStatsImpl.this.commitPendingDataToDisk();
                }
            });
        }
    }

    public void commitPendingDataToDisk() {
        synchronized (this) {
            Parcel parcel = this.mPendingWrite;
            this.mPendingWrite = null;
            if (parcel == null) {
                return;
            }
            this.mWriteLock.lock();
            try {
                try {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    FileOutputStream fileOutputStream = new FileOutputStream(this.mFile.chooseForWrite());
                    fileOutputStream.write(parcel.marshall());
                    fileOutputStream.flush();
                    FileUtils.sync(fileOutputStream);
                    fileOutputStream.close();
                    this.mFile.commit();
                    EventLogTags.writeCommitSysConfigFile(BatteryStats.SERVICE_NAME, SystemClock.uptimeMillis() - jUptimeMillis);
                } catch (IOException e) {
                    Slog.w("BatteryStats", "Error writing battery statistics", e);
                    this.mFile.rollback();
                }
            } finally {
                parcel.recycle();
                this.mWriteLock.unlock();
            }
        }
    }

    public void readLocked() throws IOException {
        if (this.mDailyFile != null) {
            readDailyStatsLocked();
        }
        if (this.mFile == null) {
            Slog.w("BatteryStats", "readLocked: no file associated with this instance");
            return;
        }
        this.mUidStats.clear();
        try {
            File fileChooseForRead = this.mFile.chooseForRead();
            if (!fileChooseForRead.exists()) {
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(fileChooseForRead);
            byte[] fully = BatteryStatsHelper.readFully(fileInputStream);
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.unmarshall(fully, 0, fully.length);
            parcelObtain.setDataPosition(0);
            fileInputStream.close();
            readSummaryFromParcel(parcelObtain);
        } catch (Exception e) {
            Slog.e("BatteryStats", "Error reading battery statistics", e);
            resetAllStatsLocked();
        }
        this.mEndPlatformVersion = Build.ID;
        if (this.mHistoryBuffer.dataPosition() > 0) {
            this.mRecordingHistory = true;
            long jElapsedRealtime = this.mClocks.elapsedRealtime();
            long jUptimeMillis = this.mClocks.uptimeMillis();
            addHistoryBufferLocked(jElapsedRealtime, (byte) 4, this.mHistoryCur);
            startRecordingHistory(jElapsedRealtime, jUptimeMillis, false);
        }
        recordDailyStatsIfNeededLocked(false);
    }

    void readHistory(Parcel parcel, boolean z) throws ParcelFormatException {
        long j = parcel.readLong();
        this.mHistoryBuffer.setDataSize(0);
        this.mHistoryBuffer.setDataPosition(0);
        this.mHistoryTagPool.clear();
        this.mNextHistoryTagIdx = 0;
        this.mNumHistoryTagChars = 0;
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = parcel.readInt();
            String string = parcel.readString();
            if (string == null) {
                throw new ParcelFormatException("null history tag string");
            }
            int i4 = parcel.readInt();
            BatteryStats.HistoryTag historyTag = new BatteryStats.HistoryTag();
            historyTag.string = string;
            historyTag.uid = i4;
            historyTag.poolIdx = i3;
            this.mHistoryTagPool.put(historyTag, Integer.valueOf(i3));
            if (i3 >= this.mNextHistoryTagIdx) {
                this.mNextHistoryTagIdx = i3 + 1;
            }
            this.mNumHistoryTagChars += historyTag.string.length() + 1;
        }
        int i5 = parcel.readInt();
        int iDataPosition = parcel.dataPosition();
        if (i5 >= MAX_MAX_HISTORY_BUFFER * 3) {
            throw new ParcelFormatException("File corrupt: history data buffer too large " + i5);
        }
        if ((i5 & (-4)) != i5) {
            throw new ParcelFormatException("File corrupt: history data buffer not aligned " + i5);
        }
        this.mHistoryBuffer.appendFrom(parcel, iDataPosition, i5);
        parcel.setDataPosition(iDataPosition + i5);
        if (z) {
            readOldHistory(parcel);
        }
        this.mHistoryBaseTime = j;
        if (j > 0) {
            this.mHistoryBaseTime = (this.mHistoryBaseTime - this.mClocks.elapsedRealtime()) + 1;
        }
    }

    void writeHistory(Parcel parcel, boolean z, boolean z2) {
        parcel.writeLong(this.mHistoryBaseTime + this.mLastHistoryElapsedRealtime);
        if (!z) {
            parcel.writeInt(0);
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(this.mHistoryTagPool.size());
        for (Map.Entry<BatteryStats.HistoryTag, Integer> entry : this.mHistoryTagPool.entrySet()) {
            BatteryStats.HistoryTag key = entry.getKey();
            parcel.writeInt(entry.getValue().intValue());
            parcel.writeString(key.string);
            parcel.writeInt(key.uid);
        }
        parcel.writeInt(this.mHistoryBuffer.dataSize());
        Parcel parcel2 = this.mHistoryBuffer;
        parcel.appendFrom(parcel2, 0, parcel2.dataSize());
        if (z2) {
            writeOldHistory(parcel);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v22 */
    /* JADX WARN: Type inference failed for: r10v23, types: [int] */
    /* JADX WARN: Type inference failed for: r10v43 */
    /* JADX WARN: Type inference failed for: r10v44, types: [int] */
    /* JADX WARN: Type inference failed for: r10v49 */
    /* JADX WARN: Type inference failed for: r10v50, types: [int] */
    /* JADX WARN: Type inference failed for: r10v7 */
    /* JADX WARN: Type inference failed for: r10v8, types: [int] */
    /* JADX WARN: Type inference failed for: r10v85 */
    /* JADX WARN: Type inference failed for: r10v86 */
    /* JADX WARN: Type inference failed for: r10v87 */
    /* JADX WARN: Type inference failed for: r10v88 */
    /* JADX WARN: Type inference failed for: r11v0, types: [com.android.internal.os.BatteryStatsImpl$Uid, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r13v17 */
    /* JADX WARN: Type inference failed for: r13v6 */
    /* JADX WARN: Type inference failed for: r13v7, types: [int] */
    /* JADX WARN: Type inference failed for: r15v1, types: [com.android.internal.os.PowerProfile] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v12, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v6, types: [android.os.Parcel, com.android.internal.os.BatteryStatsImpl$LongSamplingCounter, com.android.internal.os.BatteryStatsImpl$LongSamplingCounterArray[], com.android.internal.os.BatteryStatsImpl$LongSamplingCounter[][]] */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r5v29, types: [com.android.internal.os.BatteryStatsImpl$LongSamplingCounter[][]] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void readSummaryFromParcel(Parcel parcel) throws ParcelFormatException {
        char c;
        char c2;
        int i = parcel.readInt();
        if (i != 177) {
            Slog.w("BatteryStats", "readFromParcel: version got " + i + ", expected 177; erasing old stats");
            return;
        }
        ?? r2 = 1;
        readHistory(parcel, true);
        this.mStartCount = parcel.readInt();
        this.mUptime = parcel.readLong();
        this.mRealtime = parcel.readLong();
        this.mStartClockTime = parcel.readLong();
        this.mStartPlatformVersion = parcel.readString();
        this.mEndPlatformVersion = parcel.readString();
        this.mOnBatteryTimeBase.readSummaryFromParcel(parcel);
        this.mOnBatteryScreenOffTimeBase.readSummaryFromParcel(parcel);
        this.mDischargeUnplugLevel = parcel.readInt();
        this.mDischargePlugLevel = parcel.readInt();
        this.mDischargeCurrentLevel = parcel.readInt();
        this.mCurrentBatteryLevel = parcel.readInt();
        this.mEstimatedBatteryCapacity = parcel.readInt();
        this.mMinLearnedBatteryCapacity = parcel.readInt();
        this.mMaxLearnedBatteryCapacity = parcel.readInt();
        this.mLowDischargeAmountSinceCharge = parcel.readInt();
        this.mHighDischargeAmountSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOnSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOffSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenDozeSinceCharge = parcel.readInt();
        this.mDischargeStepTracker.readFromParcel(parcel);
        this.mChargeStepTracker.readFromParcel(parcel);
        this.mDailyDischargeStepTracker.readFromParcel(parcel);
        this.mDailyChargeStepTracker.readFromParcel(parcel);
        this.mDischargeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeScreenOffCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeScreenDozeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeLightDozeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeDeepDozeCounter.readSummaryFromParcelLocked(parcel);
        int i2 = parcel.readInt();
        ?? r4 = 0;
        boolean z = false;
        if (i2 > 0) {
            this.mDailyPackageChanges = new ArrayList<>(i2);
            while (i2 > 0) {
                i2--;
                BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
                packageChange.mPackageName = parcel.readString();
                packageChange.mUpdate = parcel.readInt() != 0;
                packageChange.mVersionCode = parcel.readLong();
                this.mDailyPackageChanges.add(packageChange);
            }
        } else {
            this.mDailyPackageChanges = null;
        }
        this.mDailyStartTime = parcel.readLong();
        this.mNextMinDailyDeadline = parcel.readLong();
        this.mNextMaxDailyDeadline = parcel.readLong();
        this.mStartCount++;
        this.mScreenState = 0;
        this.mScreenOnTimer.readSummaryFromParcelLocked(parcel);
        this.mScreenDozeTimer.readSummaryFromParcelLocked(parcel);
        int i3 = 0;
        while (true) {
            c = 5;
            if (i3 >= 5) {
                break;
            }
            this.mScreenBrightnessTimer[i3].readSummaryFromParcelLocked(parcel);
            i3++;
        }
        this.mInteractive = false;
        this.mInteractiveTimer.readSummaryFromParcelLocked(parcel);
        this.mPhoneOn = false;
        this.mPowerSaveModeEnabledTimer.readSummaryFromParcelLocked(parcel);
        this.mLongestLightIdleTime = parcel.readLong();
        this.mLongestFullIdleTime = parcel.readLong();
        this.mDeviceIdleModeLightTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceIdleModeFullTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceLightIdlingTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceIdlingTimer.readSummaryFromParcelLocked(parcel);
        this.mPhoneOnTimer.readSummaryFromParcelLocked(parcel);
        for (int i4 = 0; i4 < 5; i4++) {
            this.mPhoneSignalStrengthsTimer[i4].readSummaryFromParcelLocked(parcel);
        }
        this.mPhoneSignalScanningTimer.readSummaryFromParcelLocked(parcel);
        for (int i5 = 0; i5 < 21; i5++) {
            this.mPhoneDataConnectionsTimer[i5].readSummaryFromParcelLocked(parcel);
        }
        int i6 = 0;
        while (true) {
            c2 = '\n';
            if (i6 >= 10) {
                break;
            }
            this.mNetworkByteActivityCounters[i6].readSummaryFromParcelLocked(parcel);
            this.mNetworkPacketActivityCounters[i6].readSummaryFromParcelLocked(parcel);
            i6++;
        }
        this.mMobileRadioPowerState = 1;
        this.mMobileRadioActiveTimer.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActivePerAppTimer.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveAdjustedTime.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownTime.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownCount.readSummaryFromParcelLocked(parcel);
        this.mWifiMulticastWakelockTimer.readSummaryFromParcelLocked(parcel);
        this.mWifiRadioPowerState = 1;
        this.mWifiOn = false;
        this.mWifiOnTimer.readSummaryFromParcelLocked(parcel);
        this.mGlobalWifiRunning = false;
        this.mGlobalWifiRunningTimer.readSummaryFromParcelLocked(parcel);
        for (int i7 = 0; i7 < 8; i7++) {
            this.mWifiStateTimer[i7].readSummaryFromParcelLocked(parcel);
        }
        for (int i8 = 0; i8 < 13; i8++) {
            this.mWifiSupplStateTimer[i8].readSummaryFromParcelLocked(parcel);
        }
        for (int i9 = 0; i9 < 5; i9++) {
            this.mWifiSignalStrengthsTimer[i9].readSummaryFromParcelLocked(parcel);
        }
        this.mWifiActiveTimer.readSummaryFromParcelLocked(parcel);
        this.mWifiActivity.readSummaryFromParcel(parcel);
        for (int i10 = 0; i10 < 2; i10++) {
            this.mGpsSignalQualityTimer[i10].readSummaryFromParcelLocked(parcel);
        }
        this.mBluetoothActivity.readSummaryFromParcel(parcel);
        this.mModemActivity.readSummaryFromParcel(parcel);
        this.mHasWifiReporting = parcel.readInt() != 0;
        this.mHasBluetoothReporting = parcel.readInt() != 0;
        this.mHasModemReporting = parcel.readInt() != 0;
        int i11 = parcel.readInt();
        this.mLoadedNumConnectivityChange = i11;
        this.mNumConnectivityChange = i11;
        this.mFlashlightOnNesting = 0;
        this.mFlashlightOnTimer.readSummaryFromParcelLocked(parcel);
        this.mCameraOnNesting = 0;
        this.mCameraOnTimer.readSummaryFromParcelLocked(parcel);
        this.mBluetoothScanNesting = 0;
        this.mBluetoothScanTimer.readSummaryFromParcelLocked(parcel);
        this.mIsCellularTxPowerHigh = false;
        int i12 = parcel.readInt();
        if (i12 > 10000) {
            throw new ParcelFormatException("File corrupt: too many rpm stats " + i12);
        }
        for (int i13 = 0; i13 < i12; i13++) {
            if (parcel.readInt() != 0) {
                getRpmTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int i14 = parcel.readInt();
        if (i14 > 10000) {
            throw new ParcelFormatException("File corrupt: too many screen-off rpm stats " + i14);
        }
        for (int i15 = 0; i15 < i14; i15++) {
            if (parcel.readInt() != 0) {
                getScreenOffRpmTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int i16 = parcel.readInt();
        if (i16 > 10000) {
            throw new ParcelFormatException("File corrupt: too many kernel wake locks " + i16);
        }
        for (int i17 = 0; i17 < i16; i17++) {
            if (parcel.readInt() != 0) {
                getKernelWakelockTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int i18 = parcel.readInt();
        if (i18 > 10000) {
            throw new ParcelFormatException("File corrupt: too many wakeup reasons " + i18);
        }
        for (int i19 = 0; i19 < i18; i19++) {
            if (parcel.readInt() != 0) {
                getWakeupReasonTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int i20 = parcel.readInt();
        for (int i21 = 0; i21 < i20; i21++) {
            if (parcel.readInt() != 0) {
                getKernelMemoryTimerLocked(parcel.readLong()).readSummaryFromParcelLocked(parcel);
            }
        }
        int i22 = parcel.readInt();
        if (i22 > 10000) {
            throw new ParcelFormatException("File corrupt: too many uids " + i22);
        }
        int i23 = 0;
        while (i23 < i22) {
            int i24 = parcel.readInt();
            ?? uid = new Uid(this, i24);
            this.mUidStats.put(i24, uid);
            uid.mOnBatteryBackgroundTimeBase.readSummaryFromParcel(parcel);
            uid.mOnBatteryScreenOffBackgroundTimeBase.readSummaryFromParcel(parcel);
            uid.mWifiRunning = z;
            if (parcel.readInt() != 0) {
                uid.mWifiRunningTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mFullWifiLockOut = z;
            if (parcel.readInt() != 0) {
                uid.mFullWifiLockTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mWifiScanStarted = z;
            if (parcel.readInt() != 0) {
                uid.mWifiScanTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mWifiBatchedScanBinStarted = -1;
            for (?? r10 = z; r10 < c; r10++) {
                if (parcel.readInt() != 0) {
                    uid.makeWifiBatchedScanBin(r10, r4);
                    uid.mWifiBatchedScanTimer[r10].readSummaryFromParcelLocked(parcel);
                }
            }
            uid.mWifiMulticastEnabled = z;
            if (parcel.readInt() != 0) {
                uid.mWifiMulticastTimer.readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createAudioTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createVideoTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createFlashlightTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createCameraTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createForegroundActivityTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createForegroundServiceTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createAggregatedPartialWakelockTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothUnoptimizedScanTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanResultCounterLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanResultBgCounterLocked().readSummaryFromParcelLocked(parcel);
            }
            uid.mProcessState = 19;
            for (?? r11 = z; r11 < 7; r11++) {
                if (parcel.readInt() != 0) {
                    uid.makeProcessState(r11, r4);
                    uid.mProcessStateTimer[r11].readSummaryFromParcelLocked(parcel);
                }
            }
            if (parcel.readInt() != 0) {
                uid.createVibratorOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                if (uid.mUserActivityCounters == null) {
                    uid.initUserActivityLocked();
                }
                for (?? r12 = z; r12 < 4; r12++) {
                    uid.mUserActivityCounters[r12].readSummaryFromParcelLocked(parcel);
                }
            }
            if (parcel.readInt() != 0) {
                if (uid.mNetworkByteActivityCounters == null) {
                    uid.initNetworkActivityLocked();
                }
                for (?? r13 = z; r13 < c2; r13++) {
                    uid.mNetworkByteActivityCounters[r13].readSummaryFromParcelLocked(parcel);
                    uid.mNetworkPacketActivityCounters[r13].readSummaryFromParcelLocked(parcel);
                }
                uid.mMobileRadioActiveTime.readSummaryFromParcelLocked(parcel);
                uid.mMobileRadioActiveCount.readSummaryFromParcelLocked(parcel);
            }
            uid.mUserCpuTime.readSummaryFromParcelLocked(parcel);
            uid.mSystemCpuTime.readSummaryFromParcelLocked(parcel);
            if (parcel.readInt() != 0) {
                int i25 = parcel.readInt();
                PowerProfile powerProfile = this.mPowerProfile;
                if (powerProfile != null && powerProfile.getNumCpuClusters() != i25) {
                    throw new ParcelFormatException("Incompatible cpu cluster arrangement");
                }
                uid.mCpuClusterSpeedTimesUs = new LongSamplingCounter[i25][];
                for (?? r14 = z; r14 < i25; r14++) {
                    if (parcel.readInt() != 0) {
                        int i26 = parcel.readInt();
                        ?? r15 = this.mPowerProfile;
                        if (r15 != 0 && r15.getNumSpeedStepsInCpuCluster(r14) != i26) {
                            throw new ParcelFormatException("File corrupt: too many speed bins " + i26);
                        }
                        uid.mCpuClusterSpeedTimesUs[r14] = new LongSamplingCounter[i26];
                        for (int i27 = 0; i27 < i26; i27++) {
                            if (parcel.readInt() != 0) {
                                uid.mCpuClusterSpeedTimesUs[r14][i27] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                                uid.mCpuClusterSpeedTimesUs[r14][i27].readSummaryFromParcelLocked(parcel);
                            }
                        }
                    } else {
                        uid.mCpuClusterSpeedTimesUs[r14] = r4;
                    }
                }
            } else {
                uid.mCpuClusterSpeedTimesUs = r4;
            }
            uid.mCpuFreqTimeMs = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryTimeBase);
            uid.mScreenOffCpuFreqTimeMs = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryScreenOffTimeBase);
            uid.mCpuActiveTimeMs.readSummaryFromParcelLocked(parcel);
            uid.mCpuClusterTimesMs.readSummaryFromParcelLocked(parcel);
            int i28 = parcel.readInt();
            if (i28 == 7) {
                uid.mProcStateTimeMs = new LongSamplingCounterArray[i28];
                for (int i29 = 0; i29 < i28; i29++) {
                    uid.mProcStateTimeMs[i29] = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryTimeBase);
                }
            } else {
                uid.mProcStateTimeMs = r4;
            }
            int i30 = parcel.readInt();
            if (i30 == 7) {
                uid.mProcStateScreenOffTimeMs = new LongSamplingCounterArray[i30];
                for (int i31 = 0; i31 < i30; i31++) {
                    uid.mProcStateScreenOffTimeMs[i31] = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryScreenOffTimeBase);
                }
            } else {
                uid.mProcStateScreenOffTimeMs = r4;
            }
            if (parcel.readInt() != 0) {
                ((Uid) uid).mMobileRadioApWakeupCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
                ((Uid) uid).mMobileRadioApWakeupCount.readSummaryFromParcelLocked(parcel);
            } else {
                ((Uid) uid).mMobileRadioApWakeupCount = r4;
            }
            if (parcel.readInt() != 0) {
                ((Uid) uid).mWifiRadioApWakeupCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
                ((Uid) uid).mWifiRadioApWakeupCount.readSummaryFromParcelLocked(parcel);
            } else {
                ((Uid) uid).mWifiRadioApWakeupCount = r4;
            }
            int i32 = parcel.readInt();
            if (i32 > MAX_WAKELOCKS_PER_UID + r2) {
                throw new ParcelFormatException("File corrupt: too many wake locks " + i32);
            }
            for (int i33 = 0; i33 < i32; i33++) {
                uid.readWakeSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            int i34 = parcel.readInt();
            if (i34 > MAX_WAKELOCKS_PER_UID + r2) {
                throw new ParcelFormatException("File corrupt: too many syncs " + i34);
            }
            for (int i35 = 0; i35 < i34; i35++) {
                uid.readSyncSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            int i36 = parcel.readInt();
            if (i36 > MAX_WAKELOCKS_PER_UID + r2) {
                throw new ParcelFormatException("File corrupt: too many job timers " + i36);
            }
            for (int i37 = 0; i37 < i36; i37++) {
                uid.readJobSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            uid.readJobCompletionsFromParcelLocked(parcel);
            uid.mJobsDeferredEventCount.readSummaryFromParcelLocked(parcel);
            uid.mJobsDeferredCount.readSummaryFromParcelLocked(parcel);
            uid.mJobsFreshnessTimeMs.readSummaryFromParcelLocked(parcel);
            for (int i38 = 0; i38 < JOB_FRESHNESS_BUCKETS.length; i38++) {
                if (parcel.readInt() != 0) {
                    uid.mJobsFreshnessBuckets[i38] = new Counter(uid.mBsi.mOnBatteryTimeBase);
                    uid.mJobsFreshnessBuckets[i38].readSummaryFromParcelLocked(parcel);
                }
            }
            int i39 = parcel.readInt();
            if (i39 > 1000) {
                throw new ParcelFormatException("File corrupt: too many sensors " + i39);
            }
            for (int i40 = 0; i40 < i39; i40++) {
                int i41 = parcel.readInt();
                if (parcel.readInt() != 0) {
                    uid.getSensorTimerLocked(i41, r2).readSummaryFromParcelLocked(parcel);
                }
            }
            int i42 = parcel.readInt();
            if (i42 > 1000) {
                throw new ParcelFormatException("File corrupt: too many processes " + i42);
            }
            for (int i43 = 0; i43 < i42; i43++) {
                Uid.Proc processStatsLocked = uid.getProcessStatsLocked(parcel.readString());
                long j = parcel.readLong();
                processStatsLocked.mLoadedUserTime = j;
                processStatsLocked.mUserTime = j;
                long j2 = parcel.readLong();
                processStatsLocked.mLoadedSystemTime = j2;
                processStatsLocked.mSystemTime = j2;
                long j3 = parcel.readLong();
                processStatsLocked.mLoadedForegroundTime = j3;
                processStatsLocked.mForegroundTime = j3;
                int i44 = parcel.readInt();
                processStatsLocked.mLoadedStarts = i44;
                processStatsLocked.mStarts = i44;
                int i45 = parcel.readInt();
                processStatsLocked.mLoadedNumCrashes = i45;
                processStatsLocked.mNumCrashes = i45;
                int i46 = parcel.readInt();
                processStatsLocked.mLoadedNumAnrs = i46;
                processStatsLocked.mNumAnrs = i46;
                processStatsLocked.readExcessivePowerFromParcelLocked(parcel);
            }
            int i47 = parcel.readInt();
            if (i47 > 10000) {
                throw new ParcelFormatException("File corrupt: too many packages " + i47);
            }
            for (int i48 = 0; i48 < i47; i48++) {
                String string = parcel.readString();
                Uid.Pkg packageStatsLocked = uid.getPackageStatsLocked(string);
                int i49 = parcel.readInt();
                if (i49 > 1000) {
                    throw new ParcelFormatException("File corrupt: too many wakeup alarms " + i49);
                }
                packageStatsLocked.mWakeupAlarms.clear();
                for (int i50 = 0; i50 < i49; i50++) {
                    String string2 = parcel.readString();
                    Counter counter = new Counter(this.mOnBatteryScreenOffTimeBase);
                    counter.readSummaryFromParcelLocked(parcel);
                    packageStatsLocked.mWakeupAlarms.put(string2, counter);
                }
                int i51 = parcel.readInt();
                if (i51 > 1000) {
                    throw new ParcelFormatException("File corrupt: too many services " + i51);
                }
                for (int i52 = 0; i52 < i51; i52++) {
                    Uid.Pkg.Serv serviceStatsLocked = uid.getServiceStatsLocked(string, parcel.readString());
                    long j4 = parcel.readLong();
                    serviceStatsLocked.mLoadedStartTime = j4;
                    serviceStatsLocked.mStartTime = j4;
                    int i53 = parcel.readInt();
                    serviceStatsLocked.mLoadedStarts = i53;
                    serviceStatsLocked.mStarts = i53;
                    int i54 = parcel.readInt();
                    serviceStatsLocked.mLoadedLaunches = i54;
                    serviceStatsLocked.mLaunches = i54;
                }
            }
            i23++;
            r2 = 1;
            r4 = 0;
            z = false;
            c = 5;
            c2 = '\n';
        }
    }

    public void writeSummaryToParcel(Parcel parcel, boolean z) {
        int i;
        pullPendingStateUpdatesLocked();
        long startClockTime = getStartClockTime();
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        parcel.writeInt(177);
        writeHistory(parcel, z, true);
        parcel.writeInt(this.mStartCount);
        parcel.writeLong(computeUptime(jUptimeMillis, 0));
        parcel.writeLong(computeRealtime(jElapsedRealtime, 0));
        parcel.writeLong(startClockTime);
        parcel.writeString(this.mStartPlatformVersion);
        parcel.writeString(this.mEndPlatformVersion);
        this.mOnBatteryTimeBase.writeSummaryToParcel(parcel, jUptimeMillis, jElapsedRealtime);
        this.mOnBatteryScreenOffTimeBase.writeSummaryToParcel(parcel, jUptimeMillis, jElapsedRealtime);
        parcel.writeInt(this.mDischargeUnplugLevel);
        parcel.writeInt(this.mDischargePlugLevel);
        parcel.writeInt(this.mDischargeCurrentLevel);
        parcel.writeInt(this.mCurrentBatteryLevel);
        parcel.writeInt(this.mEstimatedBatteryCapacity);
        parcel.writeInt(this.mMinLearnedBatteryCapacity);
        parcel.writeInt(this.mMaxLearnedBatteryCapacity);
        parcel.writeInt(getLowDischargeAmountSinceCharge());
        parcel.writeInt(getHighDischargeAmountSinceCharge());
        parcel.writeInt(getDischargeAmountScreenOnSinceCharge());
        parcel.writeInt(getDischargeAmountScreenOffSinceCharge());
        parcel.writeInt(getDischargeAmountScreenDozeSinceCharge());
        this.mDischargeStepTracker.writeToParcel(parcel);
        this.mChargeStepTracker.writeToParcel(parcel);
        this.mDailyDischargeStepTracker.writeToParcel(parcel);
        this.mDailyChargeStepTracker.writeToParcel(parcel);
        this.mDischargeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeScreenOffCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeScreenDozeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeLightDozeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeDeepDozeCounter.writeSummaryFromParcelLocked(parcel);
        ArrayList<BatteryStats.PackageChange> arrayList = this.mDailyPackageChanges;
        if (arrayList != null) {
            int size = arrayList.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                BatteryStats.PackageChange packageChange = this.mDailyPackageChanges.get(i2);
                parcel.writeString(packageChange.mPackageName);
                parcel.writeInt(packageChange.mUpdate ? 1 : 0);
                parcel.writeLong(packageChange.mVersionCode);
            }
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLong(this.mDailyStartTime);
        parcel.writeLong(this.mNextMinDailyDeadline);
        parcel.writeLong(this.mNextMaxDailyDeadline);
        this.mScreenOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mScreenDozeTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        int i3 = 0;
        while (true) {
            i = 5;
            if (i3 >= 5) {
                break;
            }
            this.mScreenBrightnessTimer[i3].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            i3++;
        }
        this.mInteractiveTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mPowerSaveModeEnabledTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        parcel.writeLong(this.mLongestLightIdleTime);
        parcel.writeLong(this.mLongestFullIdleTime);
        this.mDeviceIdleModeLightTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mDeviceIdleModeFullTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mDeviceLightIdlingTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mDeviceIdlingTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mPhoneOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        for (int i4 = 0; i4 < 5; i4++) {
            this.mPhoneSignalStrengthsTimer[i4].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        this.mPhoneSignalScanningTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        for (int i5 = 0; i5 < 21; i5++) {
            this.mPhoneDataConnectionsTimer[i5].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        for (int i6 = 0; i6 < 10; i6++) {
            this.mNetworkByteActivityCounters[i6].writeSummaryFromParcelLocked(parcel);
            this.mNetworkPacketActivityCounters[i6].writeSummaryFromParcelLocked(parcel);
        }
        this.mMobileRadioActiveTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mMobileRadioActivePerAppTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mMobileRadioActiveAdjustedTime.writeSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownTime.writeSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownCount.writeSummaryFromParcelLocked(parcel);
        this.mWifiMulticastWakelockTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mWifiOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mGlobalWifiRunningTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        for (int i7 = 0; i7 < 8; i7++) {
            this.mWifiStateTimer[i7].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        for (int i8 = 0; i8 < 13; i8++) {
            this.mWifiSupplStateTimer[i8].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        for (int i9 = 0; i9 < 5; i9++) {
            this.mWifiSignalStrengthsTimer[i9].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        this.mWifiActiveTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mWifiActivity.writeSummaryToParcel(parcel);
        for (int i10 = 0; i10 < 2; i10++) {
            this.mGpsSignalQualityTimer[i10].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        }
        this.mBluetoothActivity.writeSummaryToParcel(parcel);
        this.mModemActivity.writeSummaryToParcel(parcel);
        parcel.writeInt(this.mHasWifiReporting ? 1 : 0);
        parcel.writeInt(this.mHasBluetoothReporting ? 1 : 0);
        parcel.writeInt(this.mHasModemReporting ? 1 : 0);
        parcel.writeInt(this.mNumConnectivityChange);
        this.mFlashlightOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mCameraOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        this.mBluetoothScanTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
        parcel.writeInt(this.mRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry : this.mRpmStats.entrySet()) {
            SamplingTimer value = entry.getValue();
            if (value != null) {
                parcel.writeInt(1);
                parcel.writeString(entry.getKey());
                value.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mScreenOffRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry2 : this.mScreenOffRpmStats.entrySet()) {
            SamplingTimer value2 = entry2.getValue();
            if (value2 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry2.getKey());
                value2.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mKernelWakelockStats.size());
        for (Map.Entry<String, SamplingTimer> entry3 : this.mKernelWakelockStats.entrySet()) {
            SamplingTimer value3 = entry3.getValue();
            if (value3 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry3.getKey());
                value3.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mWakeupReasonStats.size());
        for (Map.Entry<String, SamplingTimer> entry4 : this.mWakeupReasonStats.entrySet()) {
            SamplingTimer value4 = entry4.getValue();
            if (value4 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry4.getKey());
                value4.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mKernelMemoryStats.size());
        for (int i11 = 0; i11 < this.mKernelMemoryStats.size(); i11++) {
            SamplingTimer samplingTimerValueAt = this.mKernelMemoryStats.valueAt(i11);
            if (samplingTimerValueAt != null) {
                parcel.writeInt(1);
                parcel.writeLong(this.mKernelMemoryStats.keyAt(i11));
                samplingTimerValueAt.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        int size2 = this.mUidStats.size();
        parcel.writeInt(size2);
        int i12 = 0;
        while (i12 < size2) {
            parcel.writeInt(this.mUidStats.keyAt(i12));
            Uid uidValueAt = this.mUidStats.valueAt(i12);
            int i13 = size2;
            int i14 = i12;
            uidValueAt.mOnBatteryBackgroundTimeBase.writeSummaryToParcel(parcel, jUptimeMillis, jElapsedRealtime);
            uidValueAt.mOnBatteryScreenOffBackgroundTimeBase.writeSummaryToParcel(parcel, jUptimeMillis, jElapsedRealtime);
            if (uidValueAt.mWifiRunningTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mWifiRunningTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mFullWifiLockTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mFullWifiLockTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mWifiScanTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mWifiScanTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            for (int i15 = 0; i15 < i; i15++) {
                if (uidValueAt.mWifiBatchedScanTimer[i15] != null) {
                    parcel.writeInt(1);
                    uidValueAt.mWifiBatchedScanTimer[i15].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            if (uidValueAt.mWifiMulticastTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mWifiMulticastTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mAudioTurnedOnTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mAudioTurnedOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mVideoTurnedOnTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mVideoTurnedOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mFlashlightTurnedOnTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mFlashlightTurnedOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mCameraTurnedOnTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mCameraTurnedOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mForegroundActivityTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mForegroundActivityTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mForegroundServiceTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mForegroundServiceTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mAggregatedPartialWakelockTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mAggregatedPartialWakelockTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mBluetoothScanTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mBluetoothScanTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mBluetoothUnoptimizedScanTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mBluetoothUnoptimizedScanTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mBluetoothScanResultCounter != null) {
                parcel.writeInt(1);
                uidValueAt.mBluetoothScanResultCounter.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mBluetoothScanResultBgCounter != null) {
                parcel.writeInt(1);
                uidValueAt.mBluetoothScanResultBgCounter.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(0);
            }
            for (int i16 = 0; i16 < 7; i16++) {
                if (uidValueAt.mProcessStateTimer[i16] != null) {
                    parcel.writeInt(1);
                    uidValueAt.mProcessStateTimer[i16].writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            if (uidValueAt.mVibratorOnTimer != null) {
                parcel.writeInt(1);
                uidValueAt.mVibratorOnTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mUserActivityCounters == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                for (int i17 = 0; i17 < 4; i17++) {
                    uidValueAt.mUserActivityCounters[i17].writeSummaryFromParcelLocked(parcel);
                }
            }
            if (uidValueAt.mNetworkByteActivityCounters == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                for (int i18 = 0; i18 < 10; i18++) {
                    uidValueAt.mNetworkByteActivityCounters[i18].writeSummaryFromParcelLocked(parcel);
                    uidValueAt.mNetworkPacketActivityCounters[i18].writeSummaryFromParcelLocked(parcel);
                }
                uidValueAt.mMobileRadioActiveTime.writeSummaryFromParcelLocked(parcel);
                uidValueAt.mMobileRadioActiveCount.writeSummaryFromParcelLocked(parcel);
            }
            uidValueAt.mUserCpuTime.writeSummaryFromParcelLocked(parcel);
            uidValueAt.mSystemCpuTime.writeSummaryFromParcelLocked(parcel);
            if (uidValueAt.mCpuClusterSpeedTimesUs != null) {
                parcel.writeInt(1);
                parcel.writeInt(uidValueAt.mCpuClusterSpeedTimesUs.length);
                for (LongSamplingCounter[] longSamplingCounterArr : uidValueAt.mCpuClusterSpeedTimesUs) {
                    if (longSamplingCounterArr != null) {
                        parcel.writeInt(1);
                        parcel.writeInt(longSamplingCounterArr.length);
                        for (LongSamplingCounter longSamplingCounter : longSamplingCounterArr) {
                            if (longSamplingCounter != null) {
                                parcel.writeInt(1);
                                longSamplingCounter.writeSummaryFromParcelLocked(parcel);
                            } else {
                                parcel.writeInt(0);
                            }
                        }
                    } else {
                        parcel.writeInt(0);
                    }
                }
            } else {
                parcel.writeInt(0);
            }
            LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, uidValueAt.mCpuFreqTimeMs);
            LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, uidValueAt.mScreenOffCpuFreqTimeMs);
            uidValueAt.mCpuActiveTimeMs.writeSummaryFromParcelLocked(parcel);
            uidValueAt.mCpuClusterTimesMs.writeSummaryToParcelLocked(parcel);
            if (uidValueAt.mProcStateTimeMs != null) {
                parcel.writeInt(uidValueAt.mProcStateTimeMs.length);
                LongSamplingCounterArray[] longSamplingCounterArrayArr = uidValueAt.mProcStateTimeMs;
                for (LongSamplingCounterArray longSamplingCounterArray : longSamplingCounterArrayArr) {
                    LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, longSamplingCounterArray);
                }
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mProcStateScreenOffTimeMs != null) {
                parcel.writeInt(uidValueAt.mProcStateScreenOffTimeMs.length);
                LongSamplingCounterArray[] longSamplingCounterArrayArr2 = uidValueAt.mProcStateScreenOffTimeMs;
                for (LongSamplingCounterArray longSamplingCounterArray2 : longSamplingCounterArrayArr2) {
                    LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, longSamplingCounterArray2);
                }
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mMobileRadioApWakeupCount != null) {
                parcel.writeInt(1);
                uidValueAt.mMobileRadioApWakeupCount.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(0);
            }
            if (uidValueAt.mWifiRadioApWakeupCount != null) {
                parcel.writeInt(1);
                uidValueAt.mWifiRadioApWakeupCount.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(0);
            }
            ArrayMap<String, Uid.Wakelock> map = uidValueAt.mWakelockStats.getMap();
            int size3 = map.size();
            parcel.writeInt(size3);
            for (int i19 = 0; i19 < size3; i19++) {
                parcel.writeString(map.keyAt(i19));
                Uid.Wakelock wakelockValueAt = map.valueAt(i19);
                if (wakelockValueAt.mTimerFull != null) {
                    parcel.writeInt(1);
                    wakelockValueAt.mTimerFull.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
                if (wakelockValueAt.mTimerPartial != null) {
                    parcel.writeInt(1);
                    wakelockValueAt.mTimerPartial.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
                if (wakelockValueAt.mTimerWindow != null) {
                    parcel.writeInt(1);
                    wakelockValueAt.mTimerWindow.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
                if (wakelockValueAt.mTimerDraw != null) {
                    parcel.writeInt(1);
                    wakelockValueAt.mTimerDraw.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            ArrayMap<String, DualTimer> map2 = uidValueAt.mSyncStats.getMap();
            int size4 = map2.size();
            parcel.writeInt(size4);
            for (int i20 = 0; i20 < size4; i20++) {
                parcel.writeString(map2.keyAt(i20));
                map2.valueAt(i20).writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            }
            ArrayMap<String, DualTimer> map3 = uidValueAt.mJobStats.getMap();
            int size5 = map3.size();
            parcel.writeInt(size5);
            for (int i21 = 0; i21 < size5; i21++) {
                parcel.writeString(map3.keyAt(i21));
                map3.valueAt(i21).writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
            }
            uidValueAt.writeJobCompletionsToParcelLocked(parcel);
            uidValueAt.mJobsDeferredEventCount.writeSummaryFromParcelLocked(parcel);
            uidValueAt.mJobsDeferredCount.writeSummaryFromParcelLocked(parcel);
            uidValueAt.mJobsFreshnessTimeMs.writeSummaryFromParcelLocked(parcel);
            for (int i22 = 0; i22 < JOB_FRESHNESS_BUCKETS.length; i22++) {
                if (uidValueAt.mJobsFreshnessBuckets[i22] != null) {
                    parcel.writeInt(1);
                    uidValueAt.mJobsFreshnessBuckets[i22].writeSummaryFromParcelLocked(parcel);
                } else {
                    parcel.writeInt(0);
                }
            }
            int size6 = uidValueAt.mSensorStats.size();
            parcel.writeInt(size6);
            for (int i23 = 0; i23 < size6; i23++) {
                parcel.writeInt(uidValueAt.mSensorStats.keyAt(i23));
                Uid.Sensor sensorValueAt = uidValueAt.mSensorStats.valueAt(i23);
                if (sensorValueAt.mTimer != null) {
                    parcel.writeInt(1);
                    sensorValueAt.mTimer.writeSummaryFromParcelLocked(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            int size7 = uidValueAt.mProcessStats.size();
            parcel.writeInt(size7);
            for (int i24 = 0; i24 < size7; i24++) {
                parcel.writeString(uidValueAt.mProcessStats.keyAt(i24));
                Uid.Proc procValueAt = uidValueAt.mProcessStats.valueAt(i24);
                parcel.writeLong(procValueAt.mUserTime);
                parcel.writeLong(procValueAt.mSystemTime);
                parcel.writeLong(procValueAt.mForegroundTime);
                parcel.writeInt(procValueAt.mStarts);
                parcel.writeInt(procValueAt.mNumCrashes);
                parcel.writeInt(procValueAt.mNumAnrs);
                procValueAt.writeExcessivePowerToParcelLocked(parcel);
            }
            int size8 = uidValueAt.mPackageStats.size();
            parcel.writeInt(size8);
            if (size8 > 0) {
                for (Map.Entry<String, Uid.Pkg> entry5 : uidValueAt.mPackageStats.entrySet()) {
                    parcel.writeString(entry5.getKey());
                    Uid.Pkg value5 = entry5.getValue();
                    int size9 = value5.mWakeupAlarms.size();
                    parcel.writeInt(size9);
                    for (int i25 = 0; i25 < size9; i25++) {
                        parcel.writeString(value5.mWakeupAlarms.keyAt(i25));
                        value5.mWakeupAlarms.valueAt(i25).writeSummaryFromParcelLocked(parcel);
                    }
                    int size10 = value5.mServiceStats.size();
                    parcel.writeInt(size10);
                    for (int i26 = 0; i26 < size10; i26++) {
                        parcel.writeString(value5.mServiceStats.keyAt(i26));
                        Uid.Pkg.Serv servValueAt = value5.mServiceStats.valueAt(i26);
                        parcel.writeLong(servValueAt.getStartTimeToNowLocked(this.mOnBatteryTimeBase.getUptime(jUptimeMillis)));
                        parcel.writeInt(servValueAt.mStarts);
                        parcel.writeInt(servValueAt.mLaunches);
                    }
                }
            }
            i12 = i14 + 1;
            size2 = i13;
            i = 5;
        }
    }

    public void readFromParcel(Parcel parcel) {
        readFromParcelLocked(parcel);
    }

    void readFromParcelLocked(Parcel parcel) {
        int i = parcel.readInt();
        if (i != MAGIC) {
            throw new ParcelFormatException("Bad magic number: #" + Integer.toHexString(i));
        }
        readHistory(parcel, false);
        this.mStartCount = parcel.readInt();
        this.mStartClockTime = parcel.readLong();
        this.mStartPlatformVersion = parcel.readString();
        this.mEndPlatformVersion = parcel.readString();
        this.mUptime = parcel.readLong();
        this.mUptimeStart = parcel.readLong();
        this.mRealtime = parcel.readLong();
        this.mRealtimeStart = parcel.readLong();
        this.mOnBattery = parcel.readInt() != 0;
        this.mEstimatedBatteryCapacity = parcel.readInt();
        this.mMinLearnedBatteryCapacity = parcel.readInt();
        this.mMaxLearnedBatteryCapacity = parcel.readInt();
        this.mOnBatteryInternal = false;
        this.mOnBatteryTimeBase.readFromParcel(parcel);
        this.mOnBatteryScreenOffTimeBase.readFromParcel(parcel);
        this.mScreenState = 0;
        this.mScreenOnTimer = new StopwatchTimer(this.mClocks, null, -1, null, this.mOnBatteryTimeBase, parcel);
        this.mScreenDozeTimer = new StopwatchTimer(this.mClocks, null, -1, null, this.mOnBatteryTimeBase, parcel);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mScreenBrightnessTimer[i2] = new StopwatchTimer(this.mClocks, null, (-100) - i2, null, this.mOnBatteryTimeBase, parcel);
        }
        this.mInteractive = false;
        this.mInteractiveTimer = new StopwatchTimer(this.mClocks, null, -10, null, this.mOnBatteryTimeBase, parcel);
        this.mPhoneOn = false;
        this.mPowerSaveModeEnabledTimer = new StopwatchTimer(this.mClocks, null, -2, null, this.mOnBatteryTimeBase, parcel);
        this.mLongestLightIdleTime = parcel.readLong();
        this.mLongestFullIdleTime = parcel.readLong();
        this.mDeviceIdleModeLightTimer = new StopwatchTimer(this.mClocks, null, -14, null, this.mOnBatteryTimeBase, parcel);
        this.mDeviceIdleModeFullTimer = new StopwatchTimer(this.mClocks, null, -11, null, this.mOnBatteryTimeBase, parcel);
        this.mDeviceLightIdlingTimer = new StopwatchTimer(this.mClocks, null, -15, null, this.mOnBatteryTimeBase, parcel);
        this.mDeviceIdlingTimer = new StopwatchTimer(this.mClocks, null, -12, null, this.mOnBatteryTimeBase, parcel);
        this.mPhoneOnTimer = new StopwatchTimer(this.mClocks, null, -3, null, this.mOnBatteryTimeBase, parcel);
        for (int i3 = 0; i3 < 5; i3++) {
            this.mPhoneSignalStrengthsTimer[i3] = new StopwatchTimer(this.mClocks, null, (-200) - i3, null, this.mOnBatteryTimeBase, parcel);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(this.mClocks, null, -199, null, this.mOnBatteryTimeBase, parcel);
        for (int i4 = 0; i4 < 21; i4++) {
            this.mPhoneDataConnectionsTimer[i4] = new StopwatchTimer(this.mClocks, null, (-300) - i4, null, this.mOnBatteryTimeBase, parcel);
        }
        for (int i5 = 0; i5 < 10; i5++) {
            this.mNetworkByteActivityCounters[i5] = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
            this.mNetworkPacketActivityCounters[i5] = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        }
        this.mMobileRadioPowerState = 1;
        this.mMobileRadioActiveTimer = new StopwatchTimer(this.mClocks, null, -400, null, this.mOnBatteryTimeBase, parcel);
        this.mMobileRadioActivePerAppTimer = new StopwatchTimer(this.mClocks, null, -401, null, this.mOnBatteryTimeBase, parcel);
        this.mMobileRadioActiveAdjustedTime = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mMobileRadioActiveUnknownTime = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mMobileRadioActiveUnknownCount = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mWifiMulticastWakelockTimer = new StopwatchTimer(this.mClocks, null, -4, null, this.mOnBatteryTimeBase, parcel);
        this.mWifiRadioPowerState = 1;
        this.mWifiOn = false;
        this.mWifiOnTimer = new StopwatchTimer(this.mClocks, null, -4, null, this.mOnBatteryTimeBase, parcel);
        this.mGlobalWifiRunning = false;
        this.mGlobalWifiRunningTimer = new StopwatchTimer(this.mClocks, null, -5, null, this.mOnBatteryTimeBase, parcel);
        for (int i6 = 0; i6 < 8; i6++) {
            this.mWifiStateTimer[i6] = new StopwatchTimer(this.mClocks, null, (-600) - i6, null, this.mOnBatteryTimeBase, parcel);
        }
        for (int i7 = 0; i7 < 13; i7++) {
            this.mWifiSupplStateTimer[i7] = new StopwatchTimer(this.mClocks, null, (-700) - i7, null, this.mOnBatteryTimeBase, parcel);
        }
        for (int i8 = 0; i8 < 5; i8++) {
            this.mWifiSignalStrengthsTimer[i8] = new StopwatchTimer(this.mClocks, null, (-800) - i8, null, this.mOnBatteryTimeBase, parcel);
        }
        this.mWifiActiveTimer = new StopwatchTimer(this.mClocks, null, -900, null, this.mOnBatteryTimeBase, parcel);
        this.mWifiActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 1, parcel);
        for (int i9 = 0; i9 < 2; i9++) {
            this.mGpsSignalQualityTimer[i9] = new StopwatchTimer(this.mClocks, null, (-1000) - i9, null, this.mOnBatteryTimeBase, parcel);
        }
        this.mBluetoothActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 1, parcel);
        this.mModemActivity = new ControllerActivityCounterImpl(this.mOnBatteryTimeBase, 5, parcel);
        this.mHasWifiReporting = parcel.readInt() != 0;
        this.mHasBluetoothReporting = parcel.readInt() != 0;
        this.mHasModemReporting = parcel.readInt() != 0;
        this.mNumConnectivityChange = parcel.readInt();
        this.mLoadedNumConnectivityChange = parcel.readInt();
        this.mUnpluggedNumConnectivityChange = parcel.readInt();
        this.mAudioOnNesting = 0;
        this.mAudioOnTimer = new StopwatchTimer(this.mClocks, null, -7, null, this.mOnBatteryTimeBase);
        this.mVideoOnNesting = 0;
        this.mVideoOnTimer = new StopwatchTimer(this.mClocks, null, -8, null, this.mOnBatteryTimeBase);
        this.mFlashlightOnNesting = 0;
        this.mFlashlightOnTimer = new StopwatchTimer(this.mClocks, null, -9, null, this.mOnBatteryTimeBase, parcel);
        this.mCameraOnNesting = 0;
        this.mCameraOnTimer = new StopwatchTimer(this.mClocks, null, -13, null, this.mOnBatteryTimeBase, parcel);
        this.mBluetoothScanNesting = 0;
        this.mBluetoothScanTimer = new StopwatchTimer(this.mClocks, null, -14, null, this.mOnBatteryTimeBase, parcel);
        this.mIsCellularTxPowerHigh = false;
        this.mDischargeUnplugLevel = parcel.readInt();
        this.mDischargePlugLevel = parcel.readInt();
        this.mDischargeCurrentLevel = parcel.readInt();
        this.mCurrentBatteryLevel = parcel.readInt();
        this.mLowDischargeAmountSinceCharge = parcel.readInt();
        this.mHighDischargeAmountSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOn = parcel.readInt();
        this.mDischargeAmountScreenOnSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOff = parcel.readInt();
        this.mDischargeAmountScreenOffSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenDoze = parcel.readInt();
        this.mDischargeAmountScreenDozeSinceCharge = parcel.readInt();
        this.mDischargeStepTracker.readFromParcel(parcel);
        this.mChargeStepTracker.readFromParcel(parcel);
        this.mDischargeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mDischargeScreenOffCounter = new LongSamplingCounter(this.mOnBatteryScreenOffTimeBase, parcel);
        this.mDischargeScreenDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mDischargeLightDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mDischargeDeepDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase, parcel);
        this.mLastWriteTime = parcel.readLong();
        this.mRpmStats.clear();
        int i10 = parcel.readInt();
        for (int i11 = 0; i11 < i10; i11++) {
            if (parcel.readInt() != 0) {
                this.mRpmStats.put(parcel.readString(), new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase, parcel));
            }
        }
        this.mScreenOffRpmStats.clear();
        int i12 = parcel.readInt();
        for (int i13 = 0; i13 < i12; i13++) {
            if (parcel.readInt() != 0) {
                this.mScreenOffRpmStats.put(parcel.readString(), new SamplingTimer(this.mClocks, this.mOnBatteryScreenOffTimeBase, parcel));
            }
        }
        this.mKernelWakelockStats.clear();
        int i14 = parcel.readInt();
        for (int i15 = 0; i15 < i14; i15++) {
            if (parcel.readInt() != 0) {
                this.mKernelWakelockStats.put(parcel.readString(), new SamplingTimer(this.mClocks, this.mOnBatteryScreenOffTimeBase, parcel));
            }
        }
        this.mWakeupReasonStats.clear();
        int i16 = parcel.readInt();
        for (int i17 = 0; i17 < i16; i17++) {
            if (parcel.readInt() != 0) {
                this.mWakeupReasonStats.put(parcel.readString(), new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase, parcel));
            }
        }
        this.mKernelMemoryStats.clear();
        int i18 = parcel.readInt();
        for (int i19 = 0; i19 < i18; i19++) {
            if (parcel.readInt() != 0) {
                this.mKernelMemoryStats.put(Long.valueOf(parcel.readLong()).longValue(), new SamplingTimer(this.mClocks, this.mOnBatteryTimeBase, parcel));
            }
        }
        this.mPartialTimers.clear();
        this.mFullTimers.clear();
        this.mWindowTimers.clear();
        this.mWifiRunningTimers.clear();
        this.mFullWifiLockTimers.clear();
        this.mWifiScanTimers.clear();
        this.mWifiBatchedScanTimers.clear();
        this.mWifiMulticastTimers.clear();
        this.mAudioTurnedOnTimers.clear();
        this.mVideoTurnedOnTimers.clear();
        this.mFlashlightTurnedOnTimers.clear();
        this.mCameraTurnedOnTimers.clear();
        int i20 = parcel.readInt();
        this.mUidStats.clear();
        for (int i21 = 0; i21 < i20; i21++) {
            int i22 = parcel.readInt();
            Uid uid = new Uid(this, i22);
            uid.readFromParcelLocked(this.mOnBatteryTimeBase, this.mOnBatteryScreenOffTimeBase, parcel);
            this.mUidStats.append(i22, uid);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelLocked(parcel, true, i);
    }

    @Override // android.os.BatteryStats
    public void writeToParcelWithoutUids(Parcel parcel, int i) {
        writeToParcelLocked(parcel, false, i);
    }

    void writeToParcelLocked(Parcel parcel, boolean z, int i) {
        pullPendingStateUpdatesLocked();
        long startClockTime = getStartClockTime();
        long jUptimeMillis = this.mClocks.uptimeMillis() * 1000;
        long jElapsedRealtime = this.mClocks.elapsedRealtime() * 1000;
        this.mOnBatteryTimeBase.getRealtime(jElapsedRealtime);
        this.mOnBatteryScreenOffTimeBase.getRealtime(jElapsedRealtime);
        parcel.writeInt(MAGIC);
        writeHistory(parcel, true, false);
        parcel.writeInt(this.mStartCount);
        parcel.writeLong(startClockTime);
        parcel.writeString(this.mStartPlatformVersion);
        parcel.writeString(this.mEndPlatformVersion);
        parcel.writeLong(this.mUptime);
        parcel.writeLong(this.mUptimeStart);
        parcel.writeLong(this.mRealtime);
        parcel.writeLong(this.mRealtimeStart);
        parcel.writeInt(this.mOnBattery ? 1 : 0);
        parcel.writeInt(this.mEstimatedBatteryCapacity);
        parcel.writeInt(this.mMinLearnedBatteryCapacity);
        parcel.writeInt(this.mMaxLearnedBatteryCapacity);
        this.mOnBatteryTimeBase.writeToParcel(parcel, jUptimeMillis, jElapsedRealtime);
        this.mOnBatteryScreenOffTimeBase.writeToParcel(parcel, jUptimeMillis, jElapsedRealtime);
        this.mScreenOnTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mScreenDozeTimer.writeToParcel(parcel, jElapsedRealtime);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mScreenBrightnessTimer[i2].writeToParcel(parcel, jElapsedRealtime);
        }
        this.mInteractiveTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mPowerSaveModeEnabledTimer.writeToParcel(parcel, jElapsedRealtime);
        parcel.writeLong(this.mLongestLightIdleTime);
        parcel.writeLong(this.mLongestFullIdleTime);
        this.mDeviceIdleModeLightTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mDeviceIdleModeFullTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mDeviceLightIdlingTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mDeviceIdlingTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mPhoneOnTimer.writeToParcel(parcel, jElapsedRealtime);
        for (int i3 = 0; i3 < 5; i3++) {
            this.mPhoneSignalStrengthsTimer[i3].writeToParcel(parcel, jElapsedRealtime);
        }
        this.mPhoneSignalScanningTimer.writeToParcel(parcel, jElapsedRealtime);
        for (int i4 = 0; i4 < 21; i4++) {
            this.mPhoneDataConnectionsTimer[i4].writeToParcel(parcel, jElapsedRealtime);
        }
        for (int i5 = 0; i5 < 10; i5++) {
            this.mNetworkByteActivityCounters[i5].writeToParcel(parcel);
            this.mNetworkPacketActivityCounters[i5].writeToParcel(parcel);
        }
        this.mMobileRadioActiveTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mMobileRadioActivePerAppTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mMobileRadioActiveAdjustedTime.writeToParcel(parcel);
        this.mMobileRadioActiveUnknownTime.writeToParcel(parcel);
        this.mMobileRadioActiveUnknownCount.writeToParcel(parcel);
        this.mWifiMulticastWakelockTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mWifiOnTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mGlobalWifiRunningTimer.writeToParcel(parcel, jElapsedRealtime);
        for (int i6 = 0; i6 < 8; i6++) {
            this.mWifiStateTimer[i6].writeToParcel(parcel, jElapsedRealtime);
        }
        for (int i7 = 0; i7 < 13; i7++) {
            this.mWifiSupplStateTimer[i7].writeToParcel(parcel, jElapsedRealtime);
        }
        for (int i8 = 0; i8 < 5; i8++) {
            this.mWifiSignalStrengthsTimer[i8].writeToParcel(parcel, jElapsedRealtime);
        }
        this.mWifiActiveTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mWifiActivity.writeToParcel(parcel, 0);
        for (int i9 = 0; i9 < 2; i9++) {
            this.mGpsSignalQualityTimer[i9].writeToParcel(parcel, jElapsedRealtime);
        }
        this.mBluetoothActivity.writeToParcel(parcel, 0);
        this.mModemActivity.writeToParcel(parcel, 0);
        parcel.writeInt(this.mHasWifiReporting ? 1 : 0);
        parcel.writeInt(this.mHasBluetoothReporting ? 1 : 0);
        parcel.writeInt(this.mHasModemReporting ? 1 : 0);
        parcel.writeInt(this.mNumConnectivityChange);
        parcel.writeInt(this.mLoadedNumConnectivityChange);
        parcel.writeInt(this.mUnpluggedNumConnectivityChange);
        this.mFlashlightOnTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mCameraOnTimer.writeToParcel(parcel, jElapsedRealtime);
        this.mBluetoothScanTimer.writeToParcel(parcel, jElapsedRealtime);
        parcel.writeInt(this.mDischargeUnplugLevel);
        parcel.writeInt(this.mDischargePlugLevel);
        parcel.writeInt(this.mDischargeCurrentLevel);
        parcel.writeInt(this.mCurrentBatteryLevel);
        parcel.writeInt(this.mLowDischargeAmountSinceCharge);
        parcel.writeInt(this.mHighDischargeAmountSinceCharge);
        parcel.writeInt(this.mDischargeAmountScreenOn);
        parcel.writeInt(this.mDischargeAmountScreenOnSinceCharge);
        parcel.writeInt(this.mDischargeAmountScreenOff);
        parcel.writeInt(this.mDischargeAmountScreenOffSinceCharge);
        parcel.writeInt(this.mDischargeAmountScreenDoze);
        parcel.writeInt(this.mDischargeAmountScreenDozeSinceCharge);
        this.mDischargeStepTracker.writeToParcel(parcel);
        this.mChargeStepTracker.writeToParcel(parcel);
        this.mDischargeCounter.writeToParcel(parcel);
        this.mDischargeScreenOffCounter.writeToParcel(parcel);
        this.mDischargeScreenDozeCounter.writeToParcel(parcel);
        this.mDischargeLightDozeCounter.writeToParcel(parcel);
        this.mDischargeDeepDozeCounter.writeToParcel(parcel);
        parcel.writeLong(this.mLastWriteTime);
        parcel.writeInt(this.mRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry : this.mRpmStats.entrySet()) {
            SamplingTimer value = entry.getValue();
            if (value != null) {
                parcel.writeInt(1);
                parcel.writeString(entry.getKey());
                value.writeToParcel(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mScreenOffRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry2 : this.mScreenOffRpmStats.entrySet()) {
            SamplingTimer value2 = entry2.getValue();
            if (value2 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry2.getKey());
                value2.writeToParcel(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        if (z) {
            parcel.writeInt(this.mKernelWakelockStats.size());
            for (Map.Entry<String, SamplingTimer> entry3 : this.mKernelWakelockStats.entrySet()) {
                SamplingTimer value3 = entry3.getValue();
                if (value3 != null) {
                    parcel.writeInt(1);
                    parcel.writeString(entry3.getKey());
                    value3.writeToParcel(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            parcel.writeInt(this.mWakeupReasonStats.size());
            for (Map.Entry<String, SamplingTimer> entry4 : this.mWakeupReasonStats.entrySet()) {
                SamplingTimer value4 = entry4.getValue();
                if (value4 != null) {
                    parcel.writeInt(1);
                    parcel.writeString(entry4.getKey());
                    value4.writeToParcel(parcel, jElapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
        } else {
            parcel.writeInt(0);
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mKernelMemoryStats.size());
        for (int i10 = 0; i10 < this.mKernelMemoryStats.size(); i10++) {
            SamplingTimer samplingTimerValueAt = this.mKernelMemoryStats.valueAt(i10);
            if (samplingTimerValueAt != null) {
                parcel.writeInt(1);
                parcel.writeLong(this.mKernelMemoryStats.keyAt(i10));
                samplingTimerValueAt.writeToParcel(parcel, jElapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        if (z) {
            int size = this.mUidStats.size();
            parcel.writeInt(size);
            for (int i11 = 0; i11 < size; i11++) {
                parcel.writeInt(this.mUidStats.keyAt(i11));
                this.mUidStats.valueAt(i11).writeToParcelLocked(parcel, jUptimeMillis, jElapsedRealtime);
            }
            return;
        }
        parcel.writeInt(0);
    }

    @Override // android.os.BatteryStats
    public void prepareForDumpLocked() {
        pullPendingStateUpdatesLocked();
        getStartClockTime();
    }

    @Override // android.os.BatteryStats
    public void dumpLocked(Context context, PrintWriter printWriter, int i, int i2, long j) {
        super.dumpLocked(context, printWriter, i, i2, j);
        printWriter.print("Total cpu time reads: ");
        printWriter.println(this.mNumSingleUidCpuTimeReads);
        printWriter.print("Batched cpu time reads: ");
        printWriter.println(this.mNumBatchedSingleUidCpuTimeReads);
        printWriter.print("Batching Duration (min): ");
        printWriter.println((this.mClocks.uptimeMillis() - this.mCpuTimeReadsTrackingStartTime) / DateUtils.MINUTE_IN_MILLIS);
        printWriter.print("All UID cpu time reads since the later of device start or stats reset: ");
        printWriter.println(this.mNumAllUidCpuTimeReads);
        printWriter.print("UIDs removed since the later of device start or stats reset: ");
        printWriter.println(this.mNumUidsRemoved);
    }
}
