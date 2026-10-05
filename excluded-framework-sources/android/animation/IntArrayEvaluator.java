package android.animation;

/* JADX INFO: loaded from: classes.dex */
public class IntArrayEvaluator implements TypeEvaluator<int[]> {
    private int[] mArray;

    public IntArrayEvaluator() {
    }

    public IntArrayEvaluator(int[] iArr) {
        this.mArray = iArr;
    }

    @Override // android.animation.TypeEvaluator
    public int[] evaluate(float f, int[] iArr, int[] iArr2) {
        int[] iArr3 = this.mArray;
        if (iArr3 == null) {
            iArr3 = new int[iArr.length];
        }
        for (int i = 0; i < iArr3.length; i++) {
            int i2 = iArr[i];
            iArr3[i] = (int) (i2 + ((iArr2[i] - i2) * f));
        }
        return iArr3;
    }
}
