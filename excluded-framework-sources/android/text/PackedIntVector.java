package android.text;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;

/* JADX INFO: loaded from: classes2.dex */
public class PackedIntVector {
    private final int mColumns;
    private int[] mValueGap;
    private int mRows = 0;
    private int mRowGapStart = 0;
    private int mRowGapLength = 0;
    private int[] mValues = null;

    public PackedIntVector(int i) {
        this.mColumns = i;
        this.mValueGap = new int[i * 2];
    }

    public int getValue(int i, int i2) {
        int i3 = this.mColumns;
        if ((i | i2) < 0 || i >= size() || i2 >= i3) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        if (i >= this.mRowGapStart) {
            i += this.mRowGapLength;
        }
        int i4 = this.mValues[(i * i3) + i2];
        int[] iArr = this.mValueGap;
        return i >= iArr[i2] ? i4 + iArr[i2 + i3] : i4;
    }

    public void setValue(int i, int i2, int i3) {
        if ((i | i2) < 0 || i >= size() || i2 >= this.mColumns) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        if (i >= this.mRowGapStart) {
            i += this.mRowGapLength;
        }
        int[] iArr = this.mValueGap;
        if (i >= iArr[i2]) {
            i3 -= iArr[this.mColumns + i2];
        }
        this.mValues[(i * this.mColumns) + i2] = i3;
    }

    private void setValueInternal(int i, int i2, int i3) {
        if (i >= this.mRowGapStart) {
            i += this.mRowGapLength;
        }
        int[] iArr = this.mValueGap;
        if (i >= iArr[i2]) {
            i3 -= iArr[this.mColumns + i2];
        }
        this.mValues[(i * this.mColumns) + i2] = i3;
    }

    public void adjustValuesBelow(int i, int i2, int i3) {
        if ((i | i2) < 0 || i > size() || i2 >= width()) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        if (i >= this.mRowGapStart) {
            i += this.mRowGapLength;
        }
        moveValueGapTo(i2, i);
        int[] iArr = this.mValueGap;
        int i4 = i2 + this.mColumns;
        iArr[i4] = iArr[i4] + i3;
    }

    public void insertAt(int i, int[] iArr) {
        if (i < 0 || i > size()) {
            throw new IndexOutOfBoundsException("row " + i);
        }
        if (iArr != null && iArr.length < width()) {
            throw new IndexOutOfBoundsException("value count " + iArr.length);
        }
        moveRowGapTo(i);
        if (this.mRowGapLength == 0) {
            growBuffer();
        }
        this.mRowGapStart++;
        this.mRowGapLength--;
        if (iArr == null) {
            for (int i2 = this.mColumns - 1; i2 >= 0; i2--) {
                setValueInternal(i, i2, 0);
            }
            return;
        }
        for (int i3 = this.mColumns - 1; i3 >= 0; i3--) {
            setValueInternal(i, i3, iArr[i3]);
        }
    }

    public void deleteAt(int i, int i2) {
        int i3;
        if ((i | i2) < 0 || (i3 = i + i2) > size()) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        moveRowGapTo(i3);
        this.mRowGapStart -= i2;
        this.mRowGapLength += i2;
    }

    public int size() {
        return this.mRows - this.mRowGapLength;
    }

    public int width() {
        return this.mColumns;
    }

    private final void growBuffer() {
        int i = this.mColumns;
        int[] iArrNewUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(GrowingArrayUtils.growSize(size()) * i);
        int length = iArrNewUnpaddedIntArray.length / i;
        int[] iArr = this.mValueGap;
        int i2 = this.mRowGapStart;
        int i3 = this.mRows - (this.mRowGapLength + i2);
        int[] iArr2 = this.mValues;
        if (iArr2 != null) {
            System.arraycopy(iArr2, 0, iArrNewUnpaddedIntArray, 0, i * i2);
            System.arraycopy(this.mValues, (this.mRows - i3) * i, iArrNewUnpaddedIntArray, (length - i3) * i, i3 * i);
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (iArr[i4] >= i2) {
                iArr[i4] = iArr[i4] + (length - this.mRows);
                if (iArr[i4] < i2) {
                    iArr[i4] = i2;
                }
            }
        }
        this.mRowGapLength += length - this.mRows;
        this.mRows = length;
        this.mValues = iArrNewUnpaddedIntArray;
    }

    private final void moveValueGapTo(int i, int i2) {
        int[] iArr = this.mValueGap;
        int[] iArr2 = this.mValues;
        int i3 = this.mColumns;
        if (i2 == iArr[i]) {
            return;
        }
        if (i2 > iArr[i]) {
            for (int i4 = iArr[i]; i4 < i2; i4++) {
                int i5 = (i4 * i3) + i;
                iArr2[i5] = iArr2[i5] + iArr[i + i3];
            }
        } else {
            for (int i6 = i2; i6 < iArr[i]; i6++) {
                int i7 = (i6 * i3) + i;
                iArr2[i7] = iArr2[i7] - iArr[i + i3];
            }
        }
        iArr[i] = i2;
    }

    private final void moveRowGapTo(int i) {
        int i2 = this.mRowGapStart;
        if (i == i2) {
            return;
        }
        if (i > i2) {
            int i3 = this.mRowGapLength;
            int i4 = (i + i3) - (i2 + i3);
            int i5 = this.mColumns;
            int[] iArr = this.mValueGap;
            int[] iArr2 = this.mValues;
            int i6 = i2 + i3;
            for (int i7 = i6; i7 < i6 + i4; i7++) {
                int i8 = (i7 - i6) + this.mRowGapStart;
                for (int i9 = 0; i9 < i5; i9++) {
                    int i10 = iArr2[(i7 * i5) + i9];
                    if (i7 >= iArr[i9]) {
                        i10 += iArr[i9 + i5];
                    }
                    if (i8 >= iArr[i9]) {
                        i10 -= iArr[i9 + i5];
                    }
                    iArr2[(i8 * i5) + i9] = i10;
                }
            }
        } else {
            int i11 = i2 - i;
            int i12 = this.mColumns;
            int[] iArr3 = this.mValueGap;
            int[] iArr4 = this.mValues;
            int i13 = i2 + this.mRowGapLength;
            for (int i14 = (i + i11) - 1; i14 >= i; i14--) {
                int i15 = ((i14 - i) + i13) - i11;
                for (int i16 = 0; i16 < i12; i16++) {
                    int i17 = iArr4[(i14 * i12) + i16];
                    if (i14 >= iArr3[i16]) {
                        i17 += iArr3[i16 + i12];
                    }
                    if (i15 >= iArr3[i16]) {
                        i17 -= iArr3[i16 + i12];
                    }
                    iArr4[(i15 * i12) + i16] = i17;
                }
            }
        }
        this.mRowGapStart = i;
    }
}
