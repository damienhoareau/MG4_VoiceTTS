package android.os;

import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public abstract class PowerManagerInternal {
    public static final int WAKEFULNESS_ASLEEP = 0;
    public static final int WAKEFULNESS_AWAKE = 1;
    public static final int WAKEFULNESS_DOZING = 3;
    public static final int WAKEFULNESS_DREAMING = 2;

    public interface LowPowerModeListener {
        int getServiceType();

        void onLowPowerModeChanged(PowerSaveState powerSaveState);
    }

    public static boolean isInteractive(int i) {
        return i == 1 || i == 2;
    }

    public static int wakefulnessToProtoEnum(int i) {
        if (i == 0) {
            return 0;
        }
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    return i;
                }
            }
        }
        return i2;
    }

    public abstract void finishUidChanges();

    public abstract PowerSaveState getLowPowerState(int i);

    public abstract void powerHint(int i, int i2);

    public abstract void registerLowPowerModeObserver(LowPowerModeListener lowPowerModeListener);

    public abstract boolean setDeviceIdleMode(boolean z);

    public abstract void setDeviceIdleTempWhitelist(int[] iArr);

    public abstract void setDeviceIdleWhitelist(int[] iArr);

    public abstract void setDozeOverrideFromDreamManager(int i, int i2);

    public abstract void setDrawWakeLockOverrideFromSidekick(boolean z);

    public abstract boolean setLightDeviceIdleMode(boolean z);

    public abstract void setMaximumScreenOffTimeoutFromDeviceAdmin(int i, long j);

    public abstract void setScreenBrightnessOverrideFromWindowManager(int i);

    public abstract void setUserActivityTimeoutOverrideFromWindowManager(long j);

    public abstract void setUserInactiveOverrideFromWindowManager();

    public abstract void startUidChanges();

    public abstract void uidActive(int i);

    public abstract void uidGone(int i);

    public abstract void uidIdle(int i);

    public abstract void updateUidProcState(int i, int i2);

    public static String wakefulnessToString(int i) {
        if (i == 0) {
            return "Asleep";
        }
        if (i == 1) {
            return "Awake";
        }
        if (i != 2) {
            return i != 3 ? Integer.toString(i) : "Dozing";
        }
        return "Dreaming";
    }

    public void registerLowPowerModeObserver(final int i, final Consumer<PowerSaveState> consumer) {
        registerLowPowerModeObserver(new LowPowerModeListener() { // from class: android.os.PowerManagerInternal.1
            @Override // android.os.PowerManagerInternal.LowPowerModeListener
            public int getServiceType() {
                return i;
            }

            @Override // android.os.PowerManagerInternal.LowPowerModeListener
            public void onLowPowerModeChanged(PowerSaveState powerSaveState) {
                consumer.accept(powerSaveState);
            }
        });
    }
}
