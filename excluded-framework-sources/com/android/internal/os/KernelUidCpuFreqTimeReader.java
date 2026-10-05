package com.android.internal.os;

import android.net.wifi.WifiEnterpriseConfig;
import android.os.StrictMode;
import android.util.IntArray;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public class KernelUidCpuFreqTimeReader extends KernelUidCpuTimeReaderBase<Callback> {
    private static final String TAG = KernelUidCpuFreqTimeReader.class.getSimpleName();
    private static final int TOTAL_READ_ERROR_COUNT = 5;
    static final String UID_TIMES_PROC_FILE = "/proc/uid_time_in_state";
    private boolean mAllUidTimesAvailable;
    private long[] mCpuFreqs;
    private int mCpuFreqsCount;
    private long[] mCurTimes;
    private long[] mDeltaTimes;
    private SparseArray<long[]> mLastUidCpuFreqTimeMs;
    private boolean mPerClusterTimesAvailable;
    private final KernelCpuProcReader mProcReader;
    private int mReadErrorCounter;

    public interface Callback extends KernelUidCpuTimeReaderBase.Callback {
        void onUidCpuFreqTime(int i, long[] jArr);
    }

    public KernelUidCpuFreqTimeReader() {
        this.mLastUidCpuFreqTimeMs = new SparseArray<>();
        this.mAllUidTimesAvailable = true;
        this.mProcReader = KernelCpuProcReader.getFreqTimeReaderInstance();
    }

    public KernelUidCpuFreqTimeReader(KernelCpuProcReader kernelCpuProcReader) {
        this.mLastUidCpuFreqTimeMs = new SparseArray<>();
        this.mAllUidTimesAvailable = true;
        this.mProcReader = kernelCpuProcReader;
    }

    public boolean perClusterTimesAvailable() {
        return this.mPerClusterTimesAvailable;
    }

    public boolean allUidTimesAvailable() {
        return this.mAllUidTimesAvailable;
    }

    public SparseArray<long[]> getAllUidCpuFreqTimeMs() {
        return this.mLastUidCpuFreqTimeMs;
    }

    public long[] readFreqs(PowerProfile powerProfile) {
        Preconditions.checkNotNull(powerProfile);
        long[] jArr = this.mCpuFreqs;
        if (jArr != null) {
            return jArr;
        }
        if (!this.mAllUidTimesAvailable) {
            return null;
        }
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(UID_TIMES_PROC_FILE));
                try {
                    long[] freqs = readFreqs(bufferedReader, powerProfile);
                    bufferedReader.close();
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    return freqs;
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                        throw th2;
                    }
                }
            } catch (IOException e) {
                int i = this.mReadErrorCounter + 1;
                this.mReadErrorCounter = i;
                if (i >= 5) {
                    this.mAllUidTimesAvailable = false;
                }
                Slog.e(TAG, "Failed to read /proc/uid_time_in_state: " + e);
                StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                return null;
            }
        } catch (Throwable th4) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            throw th4;
        }
    }

    public long[] readFreqs(BufferedReader bufferedReader, PowerProfile powerProfile) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        String[] strArrSplit = line.split(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        int length = strArrSplit.length - 1;
        this.mCpuFreqsCount = length;
        this.mCpuFreqs = new long[length];
        this.mCurTimes = new long[length];
        this.mDeltaTimes = new long[length];
        int i = 0;
        while (i < this.mCpuFreqsCount) {
            int i2 = i + 1;
            this.mCpuFreqs[i] = Long.parseLong(strArrSplit[i2], 10);
            i = i2;
        }
        IntArray intArrayExtractClusterInfoFromProcFileFreqs = extractClusterInfoFromProcFileFreqs();
        int numCpuClusters = powerProfile.getNumCpuClusters();
        if (intArrayExtractClusterInfoFromProcFileFreqs.size() == numCpuClusters) {
            this.mPerClusterTimesAvailable = true;
            for (int i3 = 0; i3 < numCpuClusters; i3++) {
                if (intArrayExtractClusterInfoFromProcFileFreqs.get(i3) != powerProfile.getNumSpeedStepsInCpuCluster(i3)) {
                    this.mPerClusterTimesAvailable = false;
                    break;
                }
            }
        } else {
            this.mPerClusterTimesAvailable = false;
        }
        Slog.i(TAG, "mPerClusterTimesAvailable=" + this.mPerClusterTimesAvailable);
        return this.mCpuFreqs;
    }

    @Override // com.android.internal.os.KernelUidCpuTimeReaderBase
    public void readDeltaImpl(final Callback callback) {
        if (this.mCpuFreqs == null) {
            return;
        }
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuFreqTimeReader$_LfRKir9FA4B4VL15YGHagRZaR8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$readDeltaImpl$0$KernelUidCpuFreqTimeReader(callback, (IntBuffer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$readDeltaImpl$0$KernelUidCpuFreqTimeReader(Callback callback, IntBuffer intBuffer) {
        int i;
        int i2 = intBuffer.get();
        long[] jArr = this.mLastUidCpuFreqTimeMs.get(i2);
        if (jArr == null) {
            jArr = new long[this.mCpuFreqsCount];
            this.mLastUidCpuFreqTimeMs.put(i2, jArr);
        }
        if (getFreqTimeForUid(intBuffer, this.mCurTimes)) {
            boolean z = true;
            int i3 = 0;
            boolean z2 = false;
            while (true) {
                i = this.mCpuFreqsCount;
                if (i3 >= i) {
                    break;
                }
                long[] jArr2 = this.mDeltaTimes;
                jArr2[i3] = this.mCurTimes[i3] - jArr[i3];
                if (jArr2[i3] < 0) {
                    Slog.e(TAG, "Negative delta from freq time proc: " + this.mDeltaTimes[i3]);
                    z = false;
                }
                z2 |= this.mDeltaTimes[i3] > 0;
                i3++;
            }
            if (z2 && z) {
                System.arraycopy(this.mCurTimes, 0, jArr, 0, i);
                if (callback != null) {
                    callback.onUidCpuFreqTime(i2, this.mDeltaTimes);
                }
            }
        }
    }

    public void readAbsolute(final Callback callback) {
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuFreqTimeReader$s7iJKg0yjXXtqM4hsU8GS_gavIY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$readAbsolute$1$KernelUidCpuFreqTimeReader(callback, (IntBuffer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$readAbsolute$1$KernelUidCpuFreqTimeReader(Callback callback, IntBuffer intBuffer) {
        int i = intBuffer.get();
        if (getFreqTimeForUid(intBuffer, this.mCurTimes)) {
            callback.onUidCpuFreqTime(i, this.mCurTimes);
        }
    }

    private boolean getFreqTimeForUid(IntBuffer intBuffer, long[] jArr) {
        boolean z = true;
        for (int i = 0; i < this.mCpuFreqsCount; i++) {
            jArr[i] = ((long) intBuffer.get()) * 10;
            if (jArr[i] < 0) {
                Slog.e(TAG, "Negative time from freq time proc: " + jArr[i]);
                z = false;
            }
        }
        return z;
    }

    private void readImpl(Consumer<IntBuffer> consumer) {
        synchronized (this.mProcReader) {
            ByteBuffer bytes = this.mProcReader.readBytes();
            if (bytes != null && bytes.remaining() > 4) {
                if ((bytes.remaining() & 3) != 0) {
                    Slog.wtf(TAG, "Cannot parse freq time proc bytes to int: " + bytes.remaining());
                    return;
                }
                IntBuffer intBufferAsIntBuffer = bytes.asIntBuffer();
                int i = intBufferAsIntBuffer.get();
                if (i != this.mCpuFreqsCount) {
                    Slog.wtf(TAG, "Cpu freqs expect " + this.mCpuFreqsCount + " , got " + i);
                    return;
                }
                int i2 = i + 1;
                if (intBufferAsIntBuffer.remaining() % i2 != 0) {
                    Slog.wtf(TAG, "Freq time format error: " + intBufferAsIntBuffer.remaining() + " / " + i2);
                    return;
                }
                int iRemaining = intBufferAsIntBuffer.remaining() / i2;
                for (int i3 = 0; i3 < iRemaining; i3++) {
                    consumer.accept(intBufferAsIntBuffer);
                }
            }
        }
    }

    public void removeUid(int i) {
        this.mLastUidCpuFreqTimeMs.delete(i);
    }

    public void removeUidsInRange(int i, int i2) {
        this.mLastUidCpuFreqTimeMs.put(i, null);
        this.mLastUidCpuFreqTimeMs.put(i2, null);
        int iIndexOfKey = this.mLastUidCpuFreqTimeMs.indexOfKey(i);
        this.mLastUidCpuFreqTimeMs.removeAtRange(iIndexOfKey, (this.mLastUidCpuFreqTimeMs.indexOfKey(i2) - iIndexOfKey) + 1);
    }

    /* JADX WARN: Code duplicated, block: B:9:0x001c  */
    private IntArray extractClusterInfoFromProcFileFreqs() {
        IntArray intArray = new IntArray();
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = this.mCpuFreqsCount;
            if (i >= i3) {
                return intArray;
            }
            i2++;
            int i4 = i + 1;
            if (i4 != i3) {
                long[] jArr = this.mCpuFreqs;
                if (jArr[i4] <= jArr[i]) {
                    intArray.add(i2);
                    i2 = 0;
                }
            } else {
                intArray.add(i2);
                i2 = 0;
            }
            i = i4;
        }
    }
}
