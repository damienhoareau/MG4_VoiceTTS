package android.gesture;

import android.graphics.RectF;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class GestureUtils {
    private static final float NONUNIFORM_SCALE = (float) Math.sqrt(2.0d);
    private static final float SCALING_THRESHOLD = 0.26f;

    private GestureUtils() {
    }

    static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(GestureConstants.LOG_TAG, "Could not close stream", e);
            }
        }
    }

    public static float[] spatialSampling(Gesture gesture, int i) {
        return spatialSampling(gesture, i, false);
    }

    /* JADX WARN: Code duplicated, block: B:66:0x0128  */
    /* JADX WARN: Code duplicated, block: B:69:0x0137 A[LOOP:4: B:67:0x0133->B:69:0x0137, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:70:0x0142  */
    /* JADX WARN: Code duplicated, block: B:72:0x0146  */
    /* JADX WARN: Code duplicated, block: B:75:0x0155 A[LOOP:5: B:73:0x0151->B:75:0x0155, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:7:0x0026  */
    /* JADX WARN: Code duplicated, block: B:85:0x0167 A[SYNTHETIC] */
    public static float[] spatialSampling(Gesture gesture, int i, boolean z) {
        float f;
        float f2;
        float f3;
        float f4;
        float fCeil;
        float f5;
        float fCeil2;
        float f6;
        float f7 = i - 1;
        float[] fArr = new float[i * i];
        float f8 = 0.0f;
        Arrays.fill(fArr, 0.0f);
        RectF boundingBox = gesture.getBoundingBox();
        float fWidth = boundingBox.width();
        float fHeight = boundingBox.height();
        float f9 = f7 / fWidth;
        float f10 = f7 / fHeight;
        if (z) {
            if (f9 >= f10) {
                f9 = f10;
            }
            f10 = f9;
        } else {
            float f11 = fWidth / fHeight;
            if (f11 > 1.0f) {
                f11 = 1.0f / f11;
            }
            if (f11 < SCALING_THRESHOLD) {
                if (f9 >= f10) {
                    f9 = f10;
                }
                f10 = f9;
            } else if (f9 > f10) {
                float f12 = NONUNIFORM_SCALE * f10;
                if (f12 < f9) {
                    f9 = f12;
                }
            } else {
                float f13 = NONUNIFORM_SCALE * f9;
                if (f13 < f10) {
                    f10 = f13;
                }
            }
        }
        float f14 = -boundingBox.centerX();
        float f15 = -boundingBox.centerY();
        float f16 = f7 / 2.0f;
        ArrayList<GestureStroke> strokes = gesture.getStrokes();
        int size = strokes.size();
        int i2 = 0;
        while (i2 < size) {
            float[] fArr2 = strokes.get(i2).points;
            int length = fArr2.length;
            float[] fArr3 = new float[length];
            for (int i3 = 0; i3 < length; i3 += 2) {
                fArr3[i3] = ((fArr2[i3] + f14) * f9) + f16;
                int i4 = i3 + 1;
                fArr3[i4] = ((fArr2[i4] + f15) * f10) + f16;
            }
            float f17 = -1.0f;
            int i5 = 0;
            float f18 = -1.0f;
            while (i5 < length) {
                float f19 = fArr3[i5] < f8 ? f8 : fArr3[i5];
                int i6 = i5 + 1;
                float f20 = fArr3[i6] < f8 ? f8 : fArr3[i6];
                float f21 = f19 > f7 ? f7 : f19;
                if (f20 <= f7) {
                    f7 = f20;
                }
                plot(f21, f7, fArr, i);
                if (f17 != -1.0f) {
                    if (f17 > f21) {
                        f = f15;
                        f2 = f14;
                        float fCeil3 = (float) Math.ceil(f21);
                        f4 = f18;
                        float f22 = (f4 - f7) / (f17 - f21);
                        while (fCeil3 < f17) {
                            plot(fCeil3, ((fCeil3 - f21) * f22) + f7, fArr, i);
                            fCeil3 += 1.0f;
                            f16 = f16;
                        }
                        f3 = f16;
                    } else {
                        f = f15;
                        f2 = f14;
                        f3 = f16;
                        f4 = f18;
                        if (f17 < f21) {
                            float f23 = (f4 - f7) / (f17 - f21);
                            for (float fCeil4 = (float) Math.ceil(f17); fCeil4 < f21; fCeil4 += 1.0f) {
                                plot(fCeil4, ((fCeil4 - f21) * f23) + f7, fArr, i);
                            }
                        }
                        if (f4 > f7) {
                            f6 = (f17 - f21) / (f4 - f7);
                            for (fCeil2 = (float) Math.ceil(f7); fCeil2 < f4; fCeil2 += 1.0f) {
                                plot(((fCeil2 - f7) * f6) + f21, fCeil2, fArr, i);
                            }
                        } else if (f4 < f7) {
                            f5 = (f17 - f21) / (f4 - f7);
                            for (fCeil = (float) Math.ceil(f4); fCeil < f7; fCeil += 1.0f) {
                                plot(((fCeil - f7) * f5) + f21, fCeil, fArr, i);
                            }
                        }
                    }
                    if (f4 > f7) {
                        f6 = (f17 - f21) / (f4 - f7);
                        while (fCeil2 < f4) {
                            plot(((fCeil2 - f7) * f6) + f21, fCeil2, fArr, i);
                        }
                    } else if (f4 < f7) {
                        f5 = (f17 - f21) / (f4 - f7);
                        while (fCeil < f7) {
                            plot(((fCeil - f7) * f5) + f21, fCeil, fArr, i);
                        }
                    }
                } else {
                    f = f15;
                    f2 = f14;
                    f3 = f16;
                    f9 = f9;
                }
                i5 += 2;
                f18 = f7;
                f17 = f21;
                f9 = f9;
                f7 = f7;
                f14 = f2;
                f16 = f3;
                f8 = 0.0f;
                f15 = f;
            }
            i2++;
            f14 = f14;
            f8 = 0.0f;
            f15 = f15;
        }
        return fArr;
    }

    private static void plot(float f, float f2, float[] fArr, int i) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        double d = f;
        int iFloor = (int) Math.floor(d);
        int iCeil = (int) Math.ceil(d);
        double d2 = f2;
        int iFloor2 = (int) Math.floor(d2);
        int iCeil2 = (int) Math.ceil(d2);
        float f3 = iFloor;
        if (f == f3 && f2 == iFloor2) {
            int i2 = (iCeil2 * i) + iCeil;
            if (fArr[i2] < 1.0f) {
                fArr[i2] = 1.0f;
                return;
            }
            return;
        }
        double dPow = Math.pow(f3 - f, 2.0d);
        double dPow2 = Math.pow(iFloor2 - f2, 2.0d);
        double dPow3 = Math.pow(iCeil - f, 2.0d);
        double dPow4 = Math.pow(iCeil2 - f2, 2.0d);
        float fSqrt = (float) Math.sqrt(dPow + dPow2);
        float fSqrt2 = (float) Math.sqrt(dPow2 + dPow3);
        float fSqrt3 = (float) Math.sqrt(dPow + dPow4);
        float fSqrt4 = (float) Math.sqrt(dPow3 + dPow4);
        float f4 = fSqrt + fSqrt2 + fSqrt3 + fSqrt4;
        float f5 = fSqrt / f4;
        int i3 = iFloor2 * i;
        int i4 = i3 + iFloor;
        if (f5 > fArr[i4]) {
            fArr[i4] = f5;
        }
        float f6 = fSqrt2 / f4;
        int i5 = i3 + iCeil;
        if (f6 > fArr[i5]) {
            fArr[i5] = f6;
        }
        float f7 = fSqrt3 / f4;
        int i6 = iCeil2 * i;
        int i7 = iFloor + i6;
        if (f7 > fArr[i7]) {
            fArr[i7] = f7;
        }
        float f8 = fSqrt4 / f4;
        int i8 = i6 + iCeil;
        if (f8 > fArr[i8]) {
            fArr[i8] = f8;
        }
    }

    public static float[] temporalSampling(GestureStroke gestureStroke, int i) {
        float f = gestureStroke.length / (i - 1);
        int i2 = 2;
        int i3 = i * 2;
        float[] fArr = new float[i3];
        float[] fArr2 = gestureStroke.points;
        int i4 = 0;
        float f2 = fArr2[0];
        int i5 = 1;
        float f3 = fArr2[1];
        fArr[0] = f2;
        fArr[1] = f3;
        int length = fArr2.length / 2;
        float f4 = Float.MIN_VALUE;
        float f5 = Float.MIN_VALUE;
        float f6 = Float.MIN_VALUE;
        float f7 = 0.0f;
        while (i4 < length) {
            if (f5 == f4) {
                i4++;
                if (i4 >= length) {
                    break;
                }
                int i6 = i4 * 2;
                float f8 = fArr2[i6];
                f6 = fArr2[i6 + i5];
                f5 = f8;
            }
            float f9 = f5 - f2;
            float f10 = f6 - f3;
            float f11 = f5;
            float f12 = f3;
            float fHypot = (float) Math.hypot(f9, f10);
            float f13 = f7 + fHypot;
            if (f13 >= f) {
                float f14 = (f - f7) / fHypot;
                f2 += f9 * f14;
                f3 = f12 + (f14 * f10);
                fArr[i2] = f2;
                int i7 = i2 + 1;
                fArr[i7] = f3;
                i5 = 1;
                i2 = i7 + 1;
                f5 = f11;
                f7 = 0.0f;
            } else {
                i5 = 1;
                f7 = f13;
                f3 = f6;
                f2 = f11;
                f5 = Float.MIN_VALUE;
                f6 = Float.MIN_VALUE;
            }
            f4 = Float.MIN_VALUE;
        }
        float f15 = f3;
        while (i2 < i3) {
            fArr[i2] = f2;
            fArr[i2 + 1] = f15;
            i2 += 2;
        }
        return fArr;
    }

    static float[] computeCentroid(float[] fArr) {
        int length = fArr.length;
        float f = 0.0f;
        int i = 0;
        float f2 = 0.0f;
        while (i < length) {
            f += fArr[i];
            int i2 = i + 1;
            f2 += fArr[i2];
            i = i2 + 1;
        }
        float f3 = length;
        return new float[]{(f * 2.0f) / f3, (f2 * 2.0f) / f3};
    }

    private static float[][] computeCoVariance(float[] fArr) {
        float[][] fArr2 = (float[][]) Array.newInstance((Class<?>) float.class, 2, 2);
        fArr2[0][0] = 0.0f;
        fArr2[0][1] = 0.0f;
        fArr2[1][0] = 0.0f;
        fArr2[1][1] = 0.0f;
        int length = fArr.length;
        int i = 0;
        while (i < length) {
            float f = fArr[i];
            int i2 = i + 1;
            float f2 = fArr[i2];
            float[] fArr3 = fArr2[0];
            fArr3[0] = fArr3[0] + (f * f);
            float[] fArr4 = fArr2[0];
            fArr4[1] = fArr4[1] + (f * f2);
            fArr2[1][0] = fArr2[0][1];
            float[] fArr5 = fArr2[1];
            fArr5[1] = fArr5[1] + (f2 * f2);
            i = i2 + 1;
        }
        float[] fArr6 = fArr2[0];
        float f3 = length / 2;
        fArr6[0] = fArr6[0] / f3;
        float[] fArr7 = fArr2[0];
        fArr7[1] = fArr7[1] / f3;
        float[] fArr8 = fArr2[1];
        fArr8[0] = fArr8[0] / f3;
        float[] fArr9 = fArr2[1];
        fArr9[1] = fArr9[1] / f3;
        return fArr2;
    }

    static float computeTotalLength(float[] fArr) {
        int length = fArr.length - 4;
        float fHypot = 0.0f;
        int i = 0;
        while (i < length) {
            int i2 = i + 2;
            fHypot = (float) (((double) fHypot) + Math.hypot(fArr[i2] - fArr[i], fArr[i + 3] - fArr[i + 1]));
            i = i2;
        }
        return fHypot;
    }

    static float computeStraightness(float[] fArr) {
        return ((float) Math.hypot(fArr[2] - fArr[0], fArr[3] - fArr[1])) / computeTotalLength(fArr);
    }

    static float computeStraightness(float[] fArr, float f) {
        return ((float) Math.hypot(fArr[2] - fArr[0], fArr[3] - fArr[1])) / f;
    }

    static float squaredEuclideanDistance(float[] fArr, float[] fArr2) {
        int length = fArr.length;
        float f = 0.0f;
        for (int i = 0; i < length; i++) {
            float f2 = fArr[i] - fArr2[i];
            f += f2 * f2;
        }
        return f / length;
    }

    static float cosineDistance(float[] fArr, float[] fArr2) {
        int length = fArr.length;
        float f = 0.0f;
        for (int i = 0; i < length; i++) {
            f += fArr[i] * fArr2[i];
        }
        return (float) Math.acos(f);
    }

    static float minimumCosineDistance(float[] fArr, float[] fArr2, int i) {
        double dAcos;
        int length = fArr.length;
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i2 = 0; i2 < length; i2 += 2) {
            int i3 = i2 + 1;
            f += (fArr[i2] * fArr2[i2]) + (fArr[i3] * fArr2[i3]);
            f2 += (fArr[i2] * fArr2[i3]) - (fArr[i3] * fArr2[i2]);
        }
        if (f == 0.0f) {
            return 1.5707964f;
        }
        double d = f2 / f;
        double dAtan = Math.atan(d);
        if (i > 2 && Math.abs(dAtan) >= 3.141592653589793d / ((double) i)) {
            dAcos = Math.acos(f);
        } else {
            double dCos = Math.cos(dAtan);
            dAcos = Math.acos((((double) f) * dCos) + (((double) f2) * d * dCos));
        }
        return (float) dAcos;
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(ArrayList<GesturePoint> arrayList) {
        int size = arrayList.size();
        float[] fArr = new float[size * 2];
        for (int i = 0; i < size; i++) {
            GesturePoint gesturePoint = arrayList.get(i);
            int i2 = i * 2;
            fArr[i2] = gesturePoint.x;
            fArr[i2 + 1] = gesturePoint.y;
        }
        return computeOrientedBoundingBox(fArr, computeCentroid(fArr));
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(float[] fArr) {
        int length = fArr.length;
        float[] fArr2 = new float[length];
        for (int i = 0; i < length; i++) {
            fArr2[i] = fArr[i];
        }
        return computeOrientedBoundingBox(fArr2, computeCentroid(fArr2));
    }

    private static OrientedBoundingBox computeOrientedBoundingBox(float[] fArr, float[] fArr2) {
        float fAtan2;
        translate(fArr, -fArr2[0], -fArr2[1]);
        float[] fArrComputeOrientation = computeOrientation(computeCoVariance(fArr));
        if (fArrComputeOrientation[0] == 0.0f && fArrComputeOrientation[1] == 0.0f) {
            fAtan2 = -1.5707964f;
        } else {
            fAtan2 = (float) Math.atan2(fArrComputeOrientation[1], fArrComputeOrientation[0]);
            rotate(fArr, -fAtan2);
        }
        int length = fArr.length;
        float f = Float.MIN_VALUE;
        int i = 0;
        float f2 = Float.MAX_VALUE;
        float f3 = Float.MAX_VALUE;
        float f4 = Float.MIN_VALUE;
        while (i < length) {
            if (fArr[i] < f2) {
                f2 = fArr[i];
            }
            if (fArr[i] > f) {
                f = fArr[i];
            }
            int i2 = i + 1;
            if (fArr[i2] < f3) {
                f3 = fArr[i2];
            }
            if (fArr[i2] > f4) {
                f4 = fArr[i2];
            }
            i = i2 + 1;
        }
        return new OrientedBoundingBox((float) (((double) (fAtan2 * 180.0f)) / 3.141592653589793d), fArr2[0], fArr2[1], f - f2, f4 - f3);
    }

    private static float[] computeOrientation(float[][] fArr) {
        float[] fArr2 = new float[2];
        if (fArr[0][1] == 0.0f || fArr[1][0] == 0.0f) {
            fArr2[0] = 1.0f;
            fArr2[1] = 0.0f;
        }
        float f = ((-fArr[0][0]) - fArr[1][1]) / 2.0f;
        float fSqrt = (float) Math.sqrt(Math.pow(f, 2.0d) - ((double) ((fArr[0][0] * fArr[1][1]) - (fArr[0][1] * fArr[1][0]))));
        float f2 = -f;
        float f3 = f2 + fSqrt;
        float f4 = f2 - fSqrt;
        if (f3 == f4) {
            fArr2[0] = 0.0f;
            fArr2[1] = 0.0f;
        } else {
            if (f3 <= f4) {
                f3 = f4;
            }
            fArr2[0] = 1.0f;
            fArr2[1] = (f3 - fArr[0][0]) / fArr[0][1];
        }
        return fArr2;
    }

    static float[] rotate(float[] fArr, float f) {
        double d = f;
        float fCos = (float) Math.cos(d);
        float fSin = (float) Math.sin(d);
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            int i2 = i + 1;
            float f2 = (fArr[i] * fCos) - (fArr[i2] * fSin);
            float f3 = (fArr[i] * fSin) + (fArr[i2] * fCos);
            fArr[i] = f2;
            fArr[i2] = f3;
        }
        return fArr;
    }

    static float[] translate(float[] fArr, float f, float f2) {
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            fArr[i] = fArr[i] + f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] + f2;
        }
        return fArr;
    }

    static float[] scale(float[] fArr, float f, float f2) {
        int length = fArr.length;
        for (int i = 0; i < length; i += 2) {
            fArr[i] = fArr[i] * f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] * f2;
        }
        return fArr;
    }
}
