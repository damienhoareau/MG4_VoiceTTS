package com.android.internal.os;

import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Slog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/* JADX INFO: loaded from: classes3.dex */
public class KernelCpuProcReader {
    private static final long DEFAULT_THROTTLE_INTERVAL = 3000;
    private static final int ERROR_THRESHOLD = 5;
    private static final int INITIAL_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 1048576;
    private static final String TAG = "KernelCpuProcReader";
    private ByteBuffer mBuffer;
    private int mErrors;
    private final Path mProc;
    private static final String PROC_UID_FREQ_TIME = "/proc/uid_cpupower/time_in_state";
    private static final KernelCpuProcReader mFreqTimeReader = new KernelCpuProcReader(PROC_UID_FREQ_TIME);
    private static final String PROC_UID_ACTIVE_TIME = "/proc/uid_cpupower/concurrent_active_time";
    private static final KernelCpuProcReader mActiveTimeReader = new KernelCpuProcReader(PROC_UID_ACTIVE_TIME);
    private static final String PROC_UID_CLUSTER_TIME = "/proc/uid_cpupower/concurrent_policy_time";
    private static final KernelCpuProcReader mClusterTimeReader = new KernelCpuProcReader(PROC_UID_CLUSTER_TIME);
    private long mThrottleInterval = DEFAULT_THROTTLE_INTERVAL;
    private long mLastReadTime = Long.MIN_VALUE;

    public static KernelCpuProcReader getFreqTimeReaderInstance() {
        return mFreqTimeReader;
    }

    public static KernelCpuProcReader getActiveTimeReaderInstance() {
        return mActiveTimeReader;
    }

    public static KernelCpuProcReader getClusterTimeReaderInstance() {
        return mClusterTimeReader;
    }

    public KernelCpuProcReader(String str) {
        this.mProc = Paths.get(str, new String[0]);
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(8192);
        this.mBuffer = byteBufferAllocateDirect;
        byteBufferAllocateDirect.clear();
    }

    public ByteBuffer readBytes() {
        if (this.mErrors >= 5) {
            return null;
        }
        if (SystemClock.elapsedRealtime() < this.mLastReadTime + this.mThrottleInterval) {
            if (this.mBuffer.limit() <= 0 || this.mBuffer.limit() >= this.mBuffer.capacity()) {
                return null;
            }
            return this.mBuffer.asReadOnlyBuffer().order(ByteOrder.nativeOrder());
        }
        this.mLastReadTime = SystemClock.elapsedRealtime();
        this.mBuffer.clear();
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        try {
            try {
                try {
                    FileChannel fileChannelOpen = FileChannel.open(this.mProc, StandardOpenOption.READ);
                    while (fileChannelOpen.read(this.mBuffer) == this.mBuffer.capacity()) {
                        try {
                            if (!resize()) {
                                this.mErrors++;
                                Slog.e(TAG, "Proc file is too large: " + this.mProc);
                                if (fileChannelOpen != null) {
                                    fileChannelOpen.close();
                                }
                                StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                                return null;
                            }
                            fileChannelOpen.position(0L);
                        } catch (Throwable th) {
                            try {
                                throw th;
                            } catch (Throwable th2) {
                                if (fileChannelOpen != null) {
                                    try {
                                        fileChannelOpen.close();
                                    } catch (Throwable th3) {
                                        th.addSuppressed(th3);
                                    }
                                }
                                throw th2;
                            }
                        }
                    }
                    if (fileChannelOpen != null) {
                        fileChannelOpen.close();
                    }
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    this.mBuffer.flip();
                    return this.mBuffer.asReadOnlyBuffer().order(ByteOrder.nativeOrder());
                } catch (Throwable th4) {
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    throw th4;
                }
            } catch (IOException e) {
                this.mErrors++;
                Slog.e(TAG, "Error reading: " + this.mProc, e);
                StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                return null;
            }
        } catch (FileNotFoundException | NoSuchFileException unused) {
            this.mErrors++;
            Slog.w(TAG, "File not exist: " + this.mProc);
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            return null;
        }
    }

    public void setThrottleInterval(long j) {
        if (j >= 0) {
            this.mThrottleInterval = j;
        }
    }

    private boolean resize() {
        if (this.mBuffer.capacity() >= 1048576) {
            return false;
        }
        this.mBuffer = ByteBuffer.allocateDirect(Math.min(this.mBuffer.capacity() << 1, 1048576));
        return true;
    }
}
