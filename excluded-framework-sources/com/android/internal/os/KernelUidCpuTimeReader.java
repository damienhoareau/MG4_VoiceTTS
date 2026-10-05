package com.android.internal.os;

import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class KernelUidCpuTimeReader extends KernelUidCpuTimeReaderBase<Callback> {
    private static final String TAG = KernelUidCpuTimeReader.class.getSimpleName();
    private static final String sProcFile = "/proc/uid_cputime/show_uid_stat";
    private static final String sRemoveUidProcFile = "/proc/uid_cputime/remove_uid_range";
    private SparseLongArray mLastUserTimeUs = new SparseLongArray();
    private SparseLongArray mLastSystemTimeUs = new SparseLongArray();
    private long mLastTimeReadUs = 0;

    public interface Callback extends KernelUidCpuTimeReaderBase.Callback {
        void onUidCpuTime(int i, long j, long j2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code duplicated, block: B:34:0x0127 A[Catch: all -> 0x0139, TRY_LEAVE, TryCatch #2 {all -> 0x0139, blocks: (B:4:0x001b, B:5:0x0022, B:7:0x0028, B:9:0x0052, B:11:0x005c, B:13:0x0064, B:32:0x011b, B:34:0x0127, B:19:0x0085), top: B:55:0x001b }] */
    /* JADX WARN: Code duplicated, block: B:61:0x012e A[SYNTHETIC] */
    @Override // com.android.internal.os.KernelUidCpuTimeReaderBase
    public void readDeltaImpl(Callback callback) {
        TextUtils.SimpleStringSplitter simpleStringSplitter;
        long j;
        long jValueAt;
        boolean z;
        long jValueAt2;
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        long j2 = 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(sProcFile));
                try {
                    TextUtils.SimpleStringSplitter simpleStringSplitter2 = new TextUtils.SimpleStringSplitter(' ');
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        simpleStringSplitter2.setString(line);
                        String next = simpleStringSplitter2.next();
                        int i = Integer.parseInt(next.substring(0, next.length() - 1), 10);
                        long j3 = Long.parseLong(simpleStringSplitter2.next(), 10);
                        long j4 = Long.parseLong(simpleStringSplitter2.next(), 10);
                        if (callback != null) {
                            simpleStringSplitter = simpleStringSplitter2;
                            if (this.mLastTimeReadUs != 0) {
                                int iIndexOfKey = this.mLastUserTimeUs.indexOfKey(i);
                                if (iIndexOfKey >= 0) {
                                    jValueAt = j3 - this.mLastUserTimeUs.valueAt(iIndexOfKey);
                                    jValueAt2 = j4 - this.mLastSystemTimeUs.valueAt(iIndexOfKey);
                                    long j5 = jElapsedRealtime - this.mLastTimeReadUs;
                                    if (jValueAt < 0 || jValueAt2 < 0) {
                                        StringBuilder sb = new StringBuilder("Malformed cpu data for UID=");
                                        sb.append(i);
                                        sb.append("!\n");
                                        sb.append("Time between reads: ");
                                        TimeUtils.formatDuration(j5 / 1000, sb);
                                        sb.append("\n");
                                        sb.append("Previous times: u=");
                                        j2 = 1000;
                                        TimeUtils.formatDuration(this.mLastUserTimeUs.valueAt(iIndexOfKey) / 1000, sb);
                                        sb.append(" s=");
                                        TimeUtils.formatDuration(this.mLastSystemTimeUs.valueAt(iIndexOfKey) / 1000, sb);
                                        sb.append("\nCurrent times: u=");
                                        TimeUtils.formatDuration(j3 / 1000, sb);
                                        sb.append(" s=");
                                        TimeUtils.formatDuration(j4 / 1000, sb);
                                        sb.append("\nDelta: u=");
                                        TimeUtils.formatDuration(jValueAt / 1000, sb);
                                        sb.append(" s=");
                                        TimeUtils.formatDuration(jValueAt2 / 1000, sb);
                                        Slog.e(TAG, sb.toString());
                                        jValueAt = 0;
                                        jValueAt2 = 0;
                                    } else {
                                        j2 = 1000;
                                    }
                                } else {
                                    j2 = j2;
                                    jValueAt2 = j4;
                                    jValueAt = j3;
                                }
                                z = (jValueAt == 0 && jValueAt2 == 0) ? false : true;
                                j = jValueAt2;
                            }
                            this.mLastUserTimeUs.put(i, j3);
                            this.mLastSystemTimeUs.put(i, j4);
                            if (z) {
                                callback.onUidCpuTime(i, jValueAt, j);
                            }
                            simpleStringSplitter2 = simpleStringSplitter;
                            j2 = j2;
                        } else {
                            simpleStringSplitter = simpleStringSplitter2;
                        }
                        j = j4;
                        jValueAt = j3;
                        z = false;
                        this.mLastUserTimeUs.put(i, j3);
                        this.mLastSystemTimeUs.put(i, j4);
                        if (z) {
                            callback.onUidCpuTime(i, jValueAt, j);
                        }
                        simpleStringSplitter2 = simpleStringSplitter;
                        j2 = j2;
                    }
                    $closeResource(null, bufferedReader);
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    this.mLastTimeReadUs = jElapsedRealtime;
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        $closeResource(th, bufferedReader);
                        throw th2;
                    }
                }
            } catch (IOException e) {
                Slog.e(TAG, "Failed to read uid_cputime: " + e.getMessage());
            }
        } catch (Throwable th3) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            throw th3;
        }
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

    public void readAbsolute(Callback callback) {
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(sProcFile));
                try {
                    TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(' ');
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        simpleStringSplitter.setString(line);
                        String next = simpleStringSplitter.next();
                        callback.onUidCpuTime(Integer.parseInt(next.substring(0, next.length() - 1), 10), Long.parseLong(simpleStringSplitter.next(), 10), Long.parseLong(simpleStringSplitter.next(), 10));
                    }
                    $closeResource(null, bufferedReader);
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        $closeResource(th, bufferedReader);
                        throw th2;
                    }
                }
            } catch (IOException e) {
                Slog.e(TAG, "Failed to read uid_cputime: " + e.getMessage());
            }
        } catch (Throwable th3) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            throw th3;
        }
    }

    public void removeUid(int i) {
        int iIndexOfKey = this.mLastSystemTimeUs.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            this.mLastSystemTimeUs.removeAt(iIndexOfKey);
            this.mLastUserTimeUs.removeAt(iIndexOfKey);
        }
        removeUidsFromKernelModule(i, i);
    }

    public void removeUidsInRange(int i, int i2) {
        if (i2 < i) {
            return;
        }
        this.mLastSystemTimeUs.put(i, 0L);
        this.mLastUserTimeUs.put(i, 0L);
        this.mLastSystemTimeUs.put(i2, 0L);
        this.mLastUserTimeUs.put(i2, 0L);
        int iIndexOfKey = this.mLastSystemTimeUs.indexOfKey(i);
        int iIndexOfKey2 = (this.mLastSystemTimeUs.indexOfKey(i2) - iIndexOfKey) + 1;
        this.mLastSystemTimeUs.removeAtRange(iIndexOfKey, iIndexOfKey2);
        this.mLastUserTimeUs.removeAtRange(iIndexOfKey, iIndexOfKey2);
        removeUidsFromKernelModule(i, i2);
    }

    private void removeUidsFromKernelModule(int i, int i2) {
        Slog.d(TAG, "Removing uids " + i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + i2);
        int iAllowThreadDiskWritesMask = StrictMode.allowThreadDiskWritesMask();
        try {
            try {
                FileWriter fileWriter = new FileWriter(sRemoveUidProcFile);
                try {
                    fileWriter.write(i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + i2);
                    fileWriter.flush();
                    $closeResource(null, fileWriter);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        $closeResource(th, fileWriter);
                        throw th2;
                    }
                }
            } catch (IOException e) {
                Slog.e(TAG, "failed to remove uids " + i + " - " + i2 + " from uid_cputime module", e);
            }
            StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
        } catch (Throwable th3) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
            throw th3;
        }
    }
}
