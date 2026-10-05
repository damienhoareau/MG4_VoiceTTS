package com.android.internal.os;

import android.os.BatteryStats;
import android.util.LongSparseArray;

/* JADX INFO: loaded from: classes3.dex */
public class MemoryPowerCalculator extends PowerCalculator {
    private static final boolean DEBUG = false;
    public static final String TAG = "MemoryPowerCalculator";
    private final double[] powerAverages;

    @Override // com.android.internal.os.PowerCalculator
    public void calculateApp(BatterySipper batterySipper, BatteryStats.Uid uid, long j, long j2, int i) {
    }

    public MemoryPowerCalculator(PowerProfile powerProfile) {
        int numElements = powerProfile.getNumElements(PowerProfile.POWER_MEMORY);
        this.powerAverages = new double[numElements];
        for (int i = 0; i < numElements; i++) {
            this.powerAverages[i] = powerProfile.getAveragePower(PowerProfile.POWER_MEMORY, i);
            double d = this.powerAverages[i];
        }
    }

    @Override // com.android.internal.os.PowerCalculator
    public void calculateRemaining(BatterySipper batterySipper, BatteryStats batteryStats, long j, long j2, int i) {
        LongSparseArray<? extends BatteryStats.Timer> kernelMemoryStats = batteryStats.getKernelMemoryStats();
        double d = 0.0d;
        long j3 = 0;
        for (int i2 = 0; i2 < kernelMemoryStats.size(); i2++) {
            double[] dArr = this.powerAverages;
            if (i2 >= dArr.length) {
                break;
            }
            double d2 = dArr[(int) kernelMemoryStats.keyAt(i2)];
            long totalTimeLocked = kernelMemoryStats.valueAt(i2).getTotalTimeLocked(j, i);
            d += ((d2 * totalTimeLocked) / 60000.0d) / 60.0d;
            j3 += totalTimeLocked;
        }
        batterySipper.usagePowerMah = d;
        batterySipper.usageTimeMs = j3;
    }
}
