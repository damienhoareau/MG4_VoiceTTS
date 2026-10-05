package com.android.internal.os;

import android.util.SparseArray;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

/* JADX INFO: loaded from: classes3.dex */
public class KernelSingleUidTimeReader {
    public static final int TOTAL_READ_ERROR_COUNT = 5;
    private final boolean DBG;
    private final String PROC_FILE_DIR;
    private final String PROC_FILE_NAME;
    private final String TAG;
    private final int mCpuFreqsCount;
    private boolean mCpuFreqsCountVerified;
    private boolean mHasStaleData;
    private final Injector mInjector;
    private SparseArray<long[]> mLastUidCpuTimeMs;
    private int mReadErrorCounter;
    private boolean mSingleUidCpuTimesAvailable;

    KernelSingleUidTimeReader(int i) {
        this(i, new Injector());
    }

    public KernelSingleUidTimeReader(int i, Injector injector) {
        this.TAG = KernelUidCpuFreqTimeReader.class.getName();
        this.DBG = false;
        this.PROC_FILE_DIR = "/proc/uid/";
        this.PROC_FILE_NAME = "/time_in_state";
        this.mLastUidCpuTimeMs = new SparseArray<>();
        this.mSingleUidCpuTimesAvailable = true;
        this.mInjector = injector;
        this.mCpuFreqsCount = i;
        if (i == 0) {
            this.mSingleUidCpuTimesAvailable = false;
        }
    }

    public boolean singleUidCpuTimesAvailable() {
        return this.mSingleUidCpuTimesAvailable;
    }

    public long[] readDeltaMs(int i) {
        synchronized (this) {
            if (!this.mSingleUidCpuTimesAvailable) {
                return null;
            }
            String str = "/proc/uid/" + i + "/time_in_state";
            try {
                byte[] data = this.mInjector.readData(str);
                if (!this.mCpuFreqsCountVerified) {
                    verifyCpuFreqsCount(data.length, str);
                }
                ByteBuffer byteBufferWrap = ByteBuffer.wrap(data);
                byteBufferWrap.order(ByteOrder.nativeOrder());
                return computeDelta(i, readCpuTimesFromByteBuffer(byteBufferWrap));
            } catch (Exception unused) {
                int i2 = this.mReadErrorCounter + 1;
                this.mReadErrorCounter = i2;
                if (i2 >= 5) {
                    this.mSingleUidCpuTimesAvailable = false;
                }
                return null;
            }
        }
    }

    private void verifyCpuFreqsCount(int i, String str) {
        int i2 = i / 8;
        if (this.mCpuFreqsCount != i2) {
            this.mSingleUidCpuTimesAvailable = false;
            throw new IllegalStateException("Freq count didn't match,count from /proc/uid_time_in_state=" + this.mCpuFreqsCount + ", butcount from " + str + "=" + i2);
        }
        this.mCpuFreqsCountVerified = true;
    }

    private long[] readCpuTimesFromByteBuffer(ByteBuffer byteBuffer) {
        long[] jArr = new long[this.mCpuFreqsCount];
        for (int i = 0; i < this.mCpuFreqsCount; i++) {
            jArr[i] = byteBuffer.getLong() * 10;
        }
        return jArr;
    }

    public long[] computeDelta(int i, long[] jArr) {
        synchronized (this) {
            if (!this.mSingleUidCpuTimesAvailable) {
                return null;
            }
            long[] deltaLocked = getDeltaLocked(this.mLastUidCpuTimeMs.get(i), jArr);
            if (deltaLocked == null) {
                return null;
            }
            boolean z = false;
            for (int length = deltaLocked.length - 1; length >= 0; length--) {
                if (deltaLocked[length] > 0) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                return null;
            }
            this.mLastUidCpuTimeMs.put(i, jArr);
            return deltaLocked;
        }
    }

    public long[] getDeltaLocked(long[] jArr, long[] jArr2) {
        int length = jArr2.length;
        do {
            length--;
            if (length < 0) {
                if (jArr == null) {
                    return jArr2;
                }
                long[] jArr3 = new long[jArr2.length];
                for (int length2 = jArr2.length - 1; length2 >= 0; length2--) {
                    jArr3[length2] = jArr2[length2] - jArr[length2];
                    if (jArr3[length2] < 0) {
                        return null;
                    }
                }
                return jArr3;
            }
        } while (jArr2[length] >= 0);
        return null;
    }

    public void markDataAsStale(boolean z) {
        synchronized (this) {
            this.mHasStaleData = z;
        }
    }

    public boolean hasStaleData() {
        boolean z;
        synchronized (this) {
            z = this.mHasStaleData;
        }
        return z;
    }

    public void setAllUidsCpuTimesMs(SparseArray<long[]> sparseArray) {
        synchronized (this) {
            this.mLastUidCpuTimeMs.clear();
            for (int size = sparseArray.size() - 1; size >= 0; size--) {
                long[] jArrValueAt = sparseArray.valueAt(size);
                if (jArrValueAt != null) {
                    this.mLastUidCpuTimeMs.put(sparseArray.keyAt(size), (long[]) jArrValueAt.clone());
                }
            }
        }
    }

    public void removeUid(int i) {
        synchronized (this) {
            this.mLastUidCpuTimeMs.delete(i);
        }
    }

    public void removeUidsInRange(int i, int i2) {
        if (i2 < i) {
            return;
        }
        synchronized (this) {
            this.mLastUidCpuTimeMs.put(i, null);
            this.mLastUidCpuTimeMs.put(i2, null);
            int iIndexOfKey = this.mLastUidCpuTimeMs.indexOfKey(i);
            this.mLastUidCpuTimeMs.removeAtRange(iIndexOfKey, (this.mLastUidCpuTimeMs.indexOfKey(i2) - iIndexOfKey) + 1);
        }
    }

    public static class Injector {
        public byte[] readData(String str) throws IOException {
            return Files.readAllBytes(Paths.get(str, new String[0]));
        }
    }

    public SparseArray<long[]> getLastUidCpuTimeMs() {
        return this.mLastUidCpuTimeMs;
    }

    public void setSingleUidCpuTimesAvailable(boolean z) {
        this.mSingleUidCpuTimesAvailable = z;
    }
}
