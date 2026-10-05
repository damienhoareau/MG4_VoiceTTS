package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class ReprocessFormatsMap {
    private final int[] mEntry;
    private final int mInputCount;

    public ReprocessFormatsMap(int[] iArr) {
        Preconditions.checkNotNull(iArr, "entry must not be null");
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        while (i < iArr.length) {
            int iCheckArgumentFormatInternal = StreamConfigurationMap.checkArgumentFormatInternal(iArr[i]);
            int i3 = length - 1;
            int i4 = i + 1;
            if (i3 < 1) {
                throw new IllegalArgumentException(String.format("Input %x had no output format length listed", Integer.valueOf(iCheckArgumentFormatInternal)));
            }
            int i5 = iArr[i4];
            length = i3 - 1;
            i = i4 + 1;
            for (int i6 = 0; i6 < i5; i6++) {
                StreamConfigurationMap.checkArgumentFormatInternal(iArr[i + i6]);
            }
            if (i5 > 0) {
                if (length < i5) {
                    throw new IllegalArgumentException(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(iCheckArgumentFormatInternal), Integer.valueOf(length), Integer.valueOf(i5)));
                }
                i += i5;
                length -= i5;
            }
            i2++;
        }
        this.mEntry = iArr;
        this.mInputCount = i2;
    }

    public int[] getInputs() {
        int[] iArr = new int[this.mInputCount];
        int length = this.mEntry.length;
        int i = 0;
        int i2 = 0;
        while (true) {
            int[] iArr2 = this.mEntry;
            if (i < iArr2.length) {
                int i3 = iArr2[i];
                int i4 = length - 1;
                int i5 = i + 1;
                if (i4 < 1) {
                    throw new AssertionError(String.format("Input %x had no output format length listed", Integer.valueOf(i3)));
                }
                int i6 = iArr2[i5];
                length = i4 - 1;
                i = i5 + 1;
                if (i6 > 0) {
                    if (length < i6) {
                        throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(i3), Integer.valueOf(length), Integer.valueOf(i6)));
                    }
                    i += i6;
                    length -= i6;
                }
                iArr[i2] = i3;
                i2++;
            } else {
                return StreamConfigurationMap.imageFormatToPublic(iArr);
            }
        }
    }

    public int[] getOutputs(int i) {
        int length = this.mEntry.length;
        int i2 = 0;
        while (true) {
            int[] iArr = this.mEntry;
            if (i2 < iArr.length) {
                int i3 = iArr[i2];
                int i4 = length - 1;
                int i5 = i2 + 1;
                if (i4 < 1) {
                    throw new AssertionError(String.format("Input %x had no output format length listed", Integer.valueOf(i)));
                }
                int i6 = iArr[i5];
                int i7 = i4 - 1;
                int i8 = i5 + 1;
                if (i6 > 0 && i7 < i6) {
                    throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", Integer.valueOf(i), Integer.valueOf(i7), Integer.valueOf(i6)));
                }
                if (i3 == i) {
                    int[] iArr2 = new int[i6];
                    for (int i9 = 0; i9 < i6; i9++) {
                        iArr2[i9] = this.mEntry[i8 + i9];
                    }
                    return StreamConfigurationMap.imageFormatToPublic(iArr2);
                }
                i2 = i8 + i6;
                length = i7 - i6;
            } else {
                throw new IllegalArgumentException(String.format("Input format %x was not one in #getInputs", Integer.valueOf(i)));
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReprocessFormatsMap) {
            return Arrays.equals(this.mEntry, ((ReprocessFormatsMap) obj).mEntry);
        }
        return false;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCode(this.mEntry);
    }
}
