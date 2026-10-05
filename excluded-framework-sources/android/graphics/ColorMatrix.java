package android.graphics;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ColorMatrix {
    private final float[] mArray;

    public ColorMatrix() {
        this.mArray = new float[20];
        reset();
    }

    public ColorMatrix(float[] fArr) {
        float[] fArr2 = new float[20];
        this.mArray = fArr2;
        System.arraycopy(fArr, 0, fArr2, 0, 20);
    }

    public ColorMatrix(ColorMatrix colorMatrix) {
        float[] fArr = new float[20];
        this.mArray = fArr;
        System.arraycopy(colorMatrix.mArray, 0, fArr, 0, 20);
    }

    public final float[] getArray() {
        return this.mArray;
    }

    public void reset() {
        float[] fArr = this.mArray;
        Arrays.fill(fArr, 0.0f);
        fArr[18] = 1.0f;
        fArr[12] = 1.0f;
        fArr[6] = 1.0f;
        fArr[0] = 1.0f;
    }

    public void set(ColorMatrix colorMatrix) {
        System.arraycopy(colorMatrix.mArray, 0, this.mArray, 0, 20);
    }

    public void set(float[] fArr) {
        System.arraycopy(fArr, 0, this.mArray, 0, 20);
    }

    public void setScale(float f, float f2, float f3, float f4) {
        float[] fArr = this.mArray;
        for (int i = 19; i > 0; i--) {
            fArr[i] = 0.0f;
        }
        fArr[0] = f;
        fArr[6] = f2;
        fArr[12] = f3;
        fArr[18] = f4;
    }

    public void setRotate(int i, float f) {
        reset();
        double d = (((double) f) * 3.141592653589793d) / 180.0d;
        float fCos = (float) Math.cos(d);
        float fSin = (float) Math.sin(d);
        if (i == 0) {
            float[] fArr = this.mArray;
            fArr[12] = fCos;
            fArr[6] = fCos;
            fArr[7] = fSin;
            fArr[11] = -fSin;
            return;
        }
        if (i == 1) {
            float[] fArr2 = this.mArray;
            fArr2[12] = fCos;
            fArr2[0] = fCos;
            fArr2[2] = -fSin;
            fArr2[10] = fSin;
            return;
        }
        if (i == 2) {
            float[] fArr3 = this.mArray;
            fArr3[6] = fCos;
            fArr3[0] = fCos;
            fArr3[1] = fSin;
            fArr3[5] = -fSin;
            return;
        }
        throw new RuntimeException();
    }

    public void setConcat(ColorMatrix colorMatrix, ColorMatrix colorMatrix2) {
        float[] fArr = (colorMatrix == this || colorMatrix2 == this) ? new float[20] : this.mArray;
        float[] fArr2 = colorMatrix.mArray;
        float[] fArr3 = colorMatrix2.mArray;
        int i = 0;
        int i2 = 0;
        while (i < 20) {
            int i3 = 0;
            while (i3 < 4) {
                fArr[i2] = (fArr2[i + 0] * fArr3[i3 + 0]) + (fArr2[i + 1] * fArr3[i3 + 5]) + (fArr2[i + 2] * fArr3[i3 + 10]) + (fArr2[i + 3] * fArr3[i3 + 15]);
                i3++;
                i2++;
            }
            fArr[i2] = (fArr2[i + 0] * fArr3[4]) + (fArr2[i + 1] * fArr3[9]) + (fArr2[i + 2] * fArr3[14]) + (fArr2[i + 3] * fArr3[19]) + fArr2[i + 4];
            i += 5;
            i2++;
        }
        float[] fArr4 = this.mArray;
        if (fArr != fArr4) {
            System.arraycopy(fArr, 0, fArr4, 0, 20);
        }
    }

    public void preConcat(ColorMatrix colorMatrix) {
        setConcat(this, colorMatrix);
    }

    public void postConcat(ColorMatrix colorMatrix) {
        setConcat(colorMatrix, this);
    }

    public void setSaturation(float f) {
        reset();
        float[] fArr = this.mArray;
        float f2 = 1.0f - f;
        float f3 = 0.213f * f2;
        float f4 = 0.715f * f2;
        float f5 = f2 * 0.072f;
        fArr[0] = f3 + f;
        fArr[1] = f4;
        fArr[2] = f5;
        fArr[5] = f3;
        fArr[6] = f4 + f;
        fArr[7] = f5;
        fArr[10] = f3;
        fArr[11] = f4;
        fArr[12] = f5 + f;
    }

    public void setRGB2YUV() {
        reset();
        float[] fArr = this.mArray;
        fArr[0] = 0.299f;
        fArr[1] = 0.587f;
        fArr[2] = 0.114f;
        fArr[5] = -0.16874f;
        fArr[6] = -0.33126f;
        fArr[7] = 0.5f;
        fArr[10] = 0.5f;
        fArr[11] = -0.41869f;
        fArr[12] = -0.08131f;
    }

    public void setYUV2RGB() {
        reset();
        float[] fArr = this.mArray;
        fArr[2] = 1.402f;
        fArr[5] = 1.0f;
        fArr[6] = -0.34414f;
        fArr[7] = -0.71414f;
        fArr[10] = 1.0f;
        fArr[11] = 1.772f;
        fArr[12] = 0.0f;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ColorMatrix)) {
            return false;
        }
        float[] fArr = ((ColorMatrix) obj).mArray;
        for (int i = 0; i < 20; i++) {
            if (fArr[i] != this.mArray[i]) {
                return false;
            }
        }
        return true;
    }
}
