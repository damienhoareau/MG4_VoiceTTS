package com.android.internal.util;

import android.os.SystemClock;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes3.dex */
public class StatLogger {
    private static final String TAG = "StatLogger";
    private final int SIZE;
    private final int[] mCallsPerSecond;
    private final int[] mCountStats;
    private final long[] mDurationPerSecond;
    private final long[] mDurationStats;
    private final String[] mLabels;
    private final int[] mMaxCallsPerSecond;
    private final long[] mMaxDurationPerSecond;
    private final long[] mMaxDurationStats;
    private final Object mLock = new Object();
    private long mNextTickTime = SystemClock.elapsedRealtime() + 1000;

    public StatLogger(String[] strArr) {
        int length = strArr.length;
        this.SIZE = length;
        this.mCountStats = new int[length];
        this.mDurationStats = new long[length];
        this.mCallsPerSecond = new int[length];
        this.mMaxCallsPerSecond = new int[length];
        this.mDurationPerSecond = new long[length];
        this.mMaxDurationPerSecond = new long[length];
        this.mMaxDurationStats = new long[length];
        this.mLabels = strArr;
    }

    public long getTime() {
        return SystemClock.elapsedRealtimeNanos() / 1000;
    }

    public long logDurationStat(int i, long j) {
        synchronized (this.mLock) {
            long time = getTime() - j;
            if (i >= 0 && i < this.SIZE) {
                int[] iArr = this.mCountStats;
                iArr[i] = iArr[i] + 1;
                long[] jArr = this.mDurationStats;
                jArr[i] = jArr[i] + time;
                if (this.mMaxDurationStats[i] < time) {
                    this.mMaxDurationStats[i] = time;
                }
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (jElapsedRealtime > this.mNextTickTime) {
                    if (this.mMaxCallsPerSecond[i] < this.mCallsPerSecond[i]) {
                        this.mMaxCallsPerSecond[i] = this.mCallsPerSecond[i];
                    }
                    if (this.mMaxDurationPerSecond[i] < this.mDurationPerSecond[i]) {
                        this.mMaxDurationPerSecond[i] = this.mDurationPerSecond[i];
                    }
                    this.mCallsPerSecond[i] = 0;
                    this.mDurationPerSecond[i] = 0;
                    this.mNextTickTime = jElapsedRealtime + 1000;
                }
                int[] iArr2 = this.mCallsPerSecond;
                iArr2[i] = iArr2[i] + 1;
                long[] jArr2 = this.mDurationPerSecond;
                jArr2[i] = jArr2[i] + time;
                return time;
            }
            Slog.wtf(TAG, "Invalid event ID: " + i);
            return time;
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        dump(new IndentingPrintWriter(printWriter, "  ").setIndent(str));
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("Stats:");
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.SIZE; i++) {
                int i2 = this.mCountStats[i];
                double d = this.mDurationStats[i] / 1000.0d;
                Object[] objArr = new Object[7];
                objArr[0] = this.mLabels[i];
                objArr[1] = Integer.valueOf(i2);
                objArr[2] = Double.valueOf(d);
                objArr[3] = Double.valueOf(i2 == 0 ? 0.0d : d / ((double) i2));
                objArr[4] = Integer.valueOf(this.mMaxCallsPerSecond[i]);
                objArr[5] = Double.valueOf(this.mMaxDurationPerSecond[i] / 1000.0d);
                objArr[6] = Double.valueOf(this.mMaxDurationStats[i] / 1000.0d);
                indentingPrintWriter.println(String.format("%s: count=%d, total=%.1fms, avg=%.3fms, max calls/s=%d max dur/s=%.1fms max time=%.1fms", objArr));
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public void dumpProto(ProtoOutputStream protoOutputStream, long j) {
        synchronized (this.mLock) {
            long jStart = protoOutputStream.start(j);
            for (int i = 0; i < this.mLabels.length; i++) {
                long jStart2 = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, i);
                protoOutputStream.write(1138166333442L, this.mLabels[i]);
                protoOutputStream.write(1120986464259L, this.mCountStats[i]);
                protoOutputStream.write(1112396529668L, this.mDurationStats[i]);
                protoOutputStream.end(jStart2);
            }
            protoOutputStream.end(jStart);
        }
    }
}
