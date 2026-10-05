package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes2.dex */
public class SparseBooleanArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private boolean[] mValues;

    public SparseBooleanArray() {
        this(10);
    }

    public SparseBooleanArray(int i) {
        if (i == 0) {
            this.mKeys = EmptyArray.INT;
            this.mValues = EmptyArray.BOOLEAN;
        } else {
            int[] iArrNewUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(i);
            this.mKeys = iArrNewUnpaddedIntArray;
            this.mValues = new boolean[iArrNewUnpaddedIntArray.length];
        }
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SparseBooleanArray m33clone() {
        SparseBooleanArray sparseBooleanArray = null;
        try {
            SparseBooleanArray sparseBooleanArray2 = (SparseBooleanArray) super.clone();
            try {
                sparseBooleanArray2.mKeys = (int[]) this.mKeys.clone();
                sparseBooleanArray2.mValues = (boolean[]) this.mValues.clone();
                return sparseBooleanArray2;
            } catch (CloneNotSupportedException unused) {
                sparseBooleanArray = sparseBooleanArray2;
                return sparseBooleanArray;
            }
        } catch (CloneNotSupportedException unused2) {
        }
    }

    public boolean get(int i) {
        return get(i, false);
    }

    public boolean get(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        return iBinarySearch < 0 ? z : this.mValues[iBinarySearch];
    }

    public void delete(int i) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            int[] iArr = this.mKeys;
            int i2 = iBinarySearch + 1;
            System.arraycopy(iArr, i2, iArr, iBinarySearch, this.mSize - i2);
            boolean[] zArr = this.mValues;
            System.arraycopy(zArr, i2, zArr, iBinarySearch, this.mSize - i2);
            this.mSize--;
        }
    }

    public void removeAt(int i) {
        int[] iArr = this.mKeys;
        int i2 = i + 1;
        System.arraycopy(iArr, i2, iArr, i, this.mSize - i2);
        boolean[] zArr = this.mValues;
        System.arraycopy(zArr, i2, zArr, i, this.mSize - i2);
        this.mSize--;
    }

    public void put(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = z;
            return;
        }
        int i2 = ~iBinarySearch;
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, i);
        this.mValues = GrowingArrayUtils.insert(this.mValues, this.mSize, i2, z);
        this.mSize++;
    }

    public int size() {
        return this.mSize;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public boolean valueAt(int i) {
        return this.mValues[i];
    }

    public void setValueAt(int i, boolean z) {
        this.mValues[i] = z;
    }

    public void setKeyAt(int i, int i2) {
        this.mKeys[i] = i2;
    }

    public int indexOfKey(int i) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
    }

    public int indexOfValue(boolean z) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == z) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void append(int i, boolean z) {
        int i2 = this.mSize;
        if (i2 != 0 && i <= this.mKeys[i2 - 1]) {
            put(i, z);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, i);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, z);
        this.mSize++;
    }

    public int hashCode() {
        int i = this.mSize;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = ((i * 31) + this.mKeys[i2]) | (this.mValues[i2] ? 1 : 0);
        }
        return i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SparseBooleanArray)) {
            return false;
        }
        SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) obj;
        if (this.mSize != sparseBooleanArray.mSize) {
            return false;
        }
        for (int i = 0; i < this.mSize; i++) {
            if (this.mKeys[i] != sparseBooleanArray.mKeys[i] || this.mValues[i] != sparseBooleanArray.mValues[i]) {
                return false;
            }
        }
        return true;
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
