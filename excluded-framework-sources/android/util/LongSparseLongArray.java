package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes2.dex */
public class LongSparseLongArray implements Cloneable {
    private long[] mKeys;
    private int mSize;
    private long[] mValues;

    public LongSparseLongArray() {
        this(10);
    }

    public LongSparseLongArray(int i) {
        if (i == 0) {
            this.mKeys = EmptyArray.LONG;
            this.mValues = EmptyArray.LONG;
        } else {
            long[] jArrNewUnpaddedLongArray = ArrayUtils.newUnpaddedLongArray(i);
            this.mKeys = jArrNewUnpaddedLongArray;
            this.mValues = new long[jArrNewUnpaddedLongArray.length];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public LongSparseLongArray m31clone() {
        LongSparseLongArray longSparseLongArray = null;
        try {
            LongSparseLongArray longSparseLongArray2 = (LongSparseLongArray) super.clone();
            try {
                longSparseLongArray2.mKeys = (long[]) this.mKeys.clone();
                longSparseLongArray2.mValues = (long[]) this.mValues.clone();
                return longSparseLongArray2;
            } catch (CloneNotSupportedException unused) {
                longSparseLongArray = longSparseLongArray2;
                return longSparseLongArray;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    public long get(long j) {
        return get(j, 0L);
    }

    public long get(long j, long j2) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        return iBinarySearch < 0 ? j2 : this.mValues[iBinarySearch];
    }

    public void delete(long j) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (iBinarySearch >= 0) {
            removeAt(iBinarySearch);
        }
    }

    public void removeAt(int i) {
        long[] jArr = this.mKeys;
        int i2 = i + 1;
        System.arraycopy(jArr, i2, jArr, i, this.mSize - i2);
        long[] jArr2 = this.mValues;
        System.arraycopy(jArr2, i2, jArr2, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(long j, long j2) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = j2;
            return;
        }
        int i = ~iBinarySearch;
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i, j);
        this.mValues = GrowingArrayUtils.insert(this.mValues, this.mSize, i, j2);
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public long keyAt(int i) {
        return this.mKeys[i];
    }

    public long valueAt(int i) {
        return this.mValues[i];
    }

    public int indexOfKey(long j) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
    }

    public int indexOfValue(long j) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == j) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(long j, long j2) {
        int i = this.mSize;
        if (i != 0 && j <= this.mKeys[i - 1]) {
            put(j, j2);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, j);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, j2);
        this.mSize++;
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keyAt(i));
            sb.append('=');
            sb.append(valueAt(i));
        }
        sb.append('}');
        return sb.toString();
    }
}
