package com.android.internal.os;

import android.util.Slog;
import android.util.SparseArray;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public class KernelUidCpuClusterTimeReader extends KernelUidCpuTimeReaderBase<Callback> {
    private static final String TAG = KernelUidCpuClusterTimeReader.class.getSimpleName();
    private double[] mCurTime;
    private long[] mCurTimeRounded;
    private long[] mDeltaTime;
    private SparseArray<double[]> mLastUidPolicyTimeMs;
    private int mNumClusters;
    private int mNumCores;
    private int[] mNumCoresOnCluster;
    private final KernelCpuProcReader mProcReader;

    public interface Callback extends KernelUidCpuTimeReaderBase.Callback {
        void onUidCpuPolicyTime(int i, long[] jArr);
    }

    public KernelUidCpuClusterTimeReader() {
        this.mLastUidPolicyTimeMs = new SparseArray<>();
        this.mNumClusters = -1;
        this.mProcReader = KernelCpuProcReader.getClusterTimeReaderInstance();
    }

    public KernelUidCpuClusterTimeReader(KernelCpuProcReader kernelCpuProcReader) {
        this.mLastUidPolicyTimeMs = new SparseArray<>();
        this.mNumClusters = -1;
        this.mProcReader = kernelCpuProcReader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.os.KernelUidCpuTimeReaderBase
    public void readDeltaImpl(final Callback callback) {
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuClusterTimeReader$j4vHMa0qvl5KRBiWr-LkFJbasC8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$readDeltaImpl$0$KernelUidCpuClusterTimeReader(callback, (IntBuffer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$readDeltaImpl$0$KernelUidCpuClusterTimeReader(Callback callback, IntBuffer intBuffer) {
        int i;
        int i2 = intBuffer.get();
        double[] dArr = this.mLastUidPolicyTimeMs.get(i2);
        if (dArr == null) {
            dArr = new double[this.mNumClusters];
            this.mLastUidPolicyTimeMs.put(i2, dArr);
        }
        if (sumClusterTime(intBuffer, this.mCurTime)) {
            boolean z = true;
            int i3 = 0;
            boolean z2 = false;
            while (true) {
                i = this.mNumClusters;
                if (i3 >= i) {
                    break;
                }
                long[] jArr = this.mDeltaTime;
                jArr[i3] = (long) (this.mCurTime[i3] - dArr[i3]);
                if (jArr[i3] < 0) {
                    Slog.e(TAG, "Negative delta from cluster time proc: " + this.mDeltaTime[i3]);
                    z = false;
                }
                z2 |= this.mDeltaTime[i3] > 0;
                i3++;
            }
            if (z2 && z) {
                System.arraycopy(this.mCurTime, 0, dArr, 0, i);
                if (callback != null) {
                    callback.onUidCpuPolicyTime(i2, this.mDeltaTime);
                }
            }
        }
    }

    public void readAbsolute(final Callback callback) {
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuClusterTimeReader$SvNbuRWT162Eb4ur1GVE0r4GiDo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$readAbsolute$1$KernelUidCpuClusterTimeReader(callback, (IntBuffer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$readAbsolute$1$KernelUidCpuClusterTimeReader(Callback callback, IntBuffer intBuffer) {
        int i = intBuffer.get();
        if (sumClusterTime(intBuffer, this.mCurTime)) {
            for (int i2 = 0; i2 < this.mNumClusters; i2++) {
                this.mCurTimeRounded[i2] = (long) this.mCurTime[i2];
            }
            callback.onUidCpuPolicyTime(i, this.mCurTimeRounded);
        }
    }

    private boolean sumClusterTime(IntBuffer intBuffer, double[] dArr) {
        boolean z = true;
        for (int i = 0; i < this.mNumClusters; i++) {
            dArr[i] = 0.0d;
            for (int i2 = 1; i2 <= this.mNumCoresOnCluster[i]; i2++) {
                int i3 = intBuffer.get();
                if (i3 < 0) {
                    Slog.e(TAG, "Negative time from cluster time proc: " + i3);
                    z = false;
                }
                dArr[i] = dArr[i] + ((((double) i3) * 10.0d) / ((double) i2));
            }
        }
        return z;
    }

    private void readImpl(Consumer<IntBuffer> consumer) {
        synchronized (this.mProcReader) {
            ByteBuffer bytes = this.mProcReader.readBytes();
            if (bytes != null && bytes.remaining() > 4) {
                if ((bytes.remaining() & 3) != 0) {
                    Slog.wtf(TAG, "Cannot parse cluster time proc bytes to int: " + bytes.remaining());
                    return;
                }
                IntBuffer intBufferAsIntBuffer = bytes.asIntBuffer();
                int i = intBufferAsIntBuffer.get();
                if (i <= 0) {
                    Slog.wtf(TAG, "Cluster time format error: " + i);
                    return;
                }
                if (this.mNumClusters == -1) {
                    this.mNumClusters = i;
                }
                if (intBufferAsIntBuffer.remaining() < i) {
                    Slog.wtf(TAG, "Too few data left in the buffer: " + intBufferAsIntBuffer.remaining());
                    return;
                }
                if (this.mNumCores <= 0) {
                    if (!readCoreInfo(intBufferAsIntBuffer, i)) {
                        return;
                    }
                } else {
                    intBufferAsIntBuffer.position(intBufferAsIntBuffer.position() + i);
                }
                if (intBufferAsIntBuffer.remaining() % (this.mNumCores + 1) != 0) {
                    Slog.wtf(TAG, "Cluster time format error: " + intBufferAsIntBuffer.remaining() + " / " + (this.mNumCores + 1));
                    return;
                }
                int iRemaining = intBufferAsIntBuffer.remaining() / (this.mNumCores + 1);
                for (int i2 = 0; i2 < iRemaining; i2++) {
                    consumer.accept(intBufferAsIntBuffer);
                }
            }
        }
    }

    private boolean readCoreInfo(IntBuffer intBuffer, int i) {
        int[] iArr = new int[i];
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            iArr[i3] = intBuffer.get();
            i2 += iArr[i3];
        }
        if (i2 <= 0) {
            Slog.e(TAG, "Invalid # cores from cluster time proc file: " + i2);
            return false;
        }
        this.mNumCores = i2;
        this.mNumCoresOnCluster = iArr;
        this.mCurTime = new double[i];
        this.mDeltaTime = new long[i];
        this.mCurTimeRounded = new long[i];
        return true;
    }

    public void removeUid(int i) {
        this.mLastUidPolicyTimeMs.delete(i);
    }

    public void removeUidsInRange(int i, int i2) {
        this.mLastUidPolicyTimeMs.put(i, null);
        this.mLastUidPolicyTimeMs.put(i2, null);
        int iIndexOfKey = this.mLastUidPolicyTimeMs.indexOfKey(i);
        this.mLastUidPolicyTimeMs.removeAtRange(iIndexOfKey, (this.mLastUidPolicyTimeMs.indexOfKey(i2) - iIndexOfKey) + 1);
    }
}
