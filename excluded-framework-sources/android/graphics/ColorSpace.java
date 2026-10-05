package android.graphics;

import android.util.Pair;
import com.android.internal.logging.nano.MetricsProto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/* JADX INFO: loaded from: classes.dex */
public abstract class ColorSpace {
    public static final int MAX_ID = 63;
    public static final int MIN_ID = -1;
    private static final ColorSpace[] sNamedColorSpaces;
    private final int mId;
    private final Model mModel;
    private final String mName;
    public static final float[] ILLUMINANT_A = {0.44757f, 0.40745f};
    public static final float[] ILLUMINANT_B = {0.34842f, 0.35161f};
    public static final float[] ILLUMINANT_C = {0.31006f, 0.31616f};
    public static final float[] ILLUMINANT_D50 = {0.34567f, 0.3585f};
    public static final float[] ILLUMINANT_D55 = {0.33242f, 0.34743f};
    public static final float[] ILLUMINANT_D60 = {0.32168f, 0.33767f};
    public static final float[] ILLUMINANT_D65 = {0.31271f, 0.32902f};
    public static final float[] ILLUMINANT_D75 = {0.29902f, 0.31485f};
    public static final float[] ILLUMINANT_E = {0.33333f, 0.33333f};
    private static final float[] SRGB_PRIMARIES = {0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f};
    private static final float[] NTSC_1953_PRIMARIES = {0.67f, 0.33f, 0.21f, 0.71f, 0.14f, 0.08f};
    private static final float[] ILLUMINANT_D50_XYZ = {0.964212f, 1.0f, 0.825188f};

    public enum Named {
        SRGB,
        LINEAR_SRGB,
        EXTENDED_SRGB,
        LINEAR_EXTENDED_SRGB,
        BT709,
        BT2020,
        DCI_P3,
        DISPLAY_P3,
        NTSC_1953,
        SMPTE_C,
        ADOBE_RGB,
        PRO_PHOTO_RGB,
        ACES,
        ACESCG,
        CIE_XYZ,
        CIE_LAB
    }

    public enum RenderIntent {
        PERCEPTUAL,
        RELATIVE,
        SATURATION,
        ABSOLUTE
    }

    public abstract float[] fromXyz(float[] fArr);

    public abstract float getMaxValue(int i);

    public abstract float getMinValue(int i);

    public boolean isSrgb() {
        return false;
    }

    public abstract boolean isWideGamut();

    public abstract float[] toXyz(float[] fArr);

    static {
        ColorSpace[] colorSpaceArr = new ColorSpace[Named.values().length];
        sNamedColorSpaces = colorSpaceArr;
        colorSpaceArr[Named.SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1", SRGB_PRIMARIES, ILLUMINANT_D65, new Rgb.TransferParameters(0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d), Named.SRGB.ordinal());
        sNamedColorSpaces[Named.LINEAR_SRGB.ordinal()] = new Rgb("sRGB IEC61966-2.1 (Linear)", SRGB_PRIMARIES, ILLUMINANT_D65, 1.0d, 0.0f, 1.0f, Named.LINEAR_SRGB.ordinal());
        sNamedColorSpaces[Named.EXTENDED_SRGB.ordinal()] = new Rgb("scRGB-nl IEC 61966-2-2:2003", SRGB_PRIMARIES, ILLUMINANT_D65, new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$BNp-1CyCzsQzfE-Ads9uc4rJDfw
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                return ColorSpace.absRcpResponse(d, 0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
            }
        }, new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$S2rlqJvkXGTpUF6mZhvkElds8JE
            @Override // java.util.function.DoubleUnaryOperator
            public final double applyAsDouble(double d) {
                return ColorSpace.absResponse(d, 0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.04045d, 2.4d);
            }
        }, -0.799f, 2.399f, Named.EXTENDED_SRGB.ordinal());
        sNamedColorSpaces[Named.LINEAR_EXTENDED_SRGB.ordinal()] = new Rgb("scRGB IEC 61966-2-2:2003", SRGB_PRIMARIES, ILLUMINANT_D65, 1.0d, -0.5f, 7.499f, Named.LINEAR_EXTENDED_SRGB.ordinal());
        sNamedColorSpaces[Named.BT709.ordinal()] = new Rgb("Rec. ITU-R BT.709-5", new float[]{0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f}, ILLUMINANT_D65, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.BT709.ordinal());
        sNamedColorSpaces[Named.BT2020.ordinal()] = new Rgb("Rec. ITU-R BT.2020-1", new float[]{0.708f, 0.292f, 0.17f, 0.797f, 0.131f, 0.046f}, ILLUMINANT_D65, new Rgb.TransferParameters(0.9096697898662786d, 0.09033021013372146d, 0.2222222222222222d, 0.08145d, 2.2222222222222223d), Named.BT2020.ordinal());
        sNamedColorSpaces[Named.DCI_P3.ordinal()] = new Rgb("SMPTE RP 431-2-2007 DCI (P3)", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, new float[]{0.314f, 0.351f}, 2.6d, 0.0f, 1.0f, Named.DCI_P3.ordinal());
        sNamedColorSpaces[Named.DISPLAY_P3.ordinal()] = new Rgb("Display P3", new float[]{0.68f, 0.32f, 0.265f, 0.69f, 0.15f, 0.06f}, ILLUMINANT_D65, new Rgb.TransferParameters(0.9478672985781991d, 0.05213270142180095d, 0.07739938080495357d, 0.039d, 2.4d), Named.DISPLAY_P3.ordinal());
        sNamedColorSpaces[Named.NTSC_1953.ordinal()] = new Rgb("NTSC (1953)", NTSC_1953_PRIMARIES, ILLUMINANT_C, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.NTSC_1953.ordinal());
        sNamedColorSpaces[Named.SMPTE_C.ordinal()] = new Rgb("SMPTE-C RGB", new float[]{0.63f, 0.34f, 0.31f, 0.595f, 0.155f, 0.07f}, ILLUMINANT_D65, new Rgb.TransferParameters(0.9099181073703367d, 0.09008189262966333d, 0.2222222222222222d, 0.081d, 2.2222222222222223d), Named.SMPTE_C.ordinal());
        sNamedColorSpaces[Named.ADOBE_RGB.ordinal()] = new Rgb("Adobe RGB (1998)", new float[]{0.64f, 0.33f, 0.21f, 0.71f, 0.15f, 0.06f}, ILLUMINANT_D65, 2.2d, 0.0f, 1.0f, Named.ADOBE_RGB.ordinal());
        sNamedColorSpaces[Named.PRO_PHOTO_RGB.ordinal()] = new Rgb("ROMM RGB ISO 22028-2:2013", new float[]{0.7347f, 0.2653f, 0.1596f, 0.8404f, 0.0366f, 1.0E-4f}, ILLUMINANT_D50, new Rgb.TransferParameters(1.0d, 0.0d, 0.0625d, 0.031248d, 1.8d), Named.PRO_PHOTO_RGB.ordinal());
        double d = 1.0d;
        float f = -65504.0f;
        float f2 = 65504.0f;
        sNamedColorSpaces[Named.ACES.ordinal()] = new Rgb("SMPTE ST 2065-1:2012 ACES", new float[]{0.7347f, 0.2653f, 0.0f, 1.0f, 1.0E-4f, -0.077f}, ILLUMINANT_D60, d, f, f2, Named.ACES.ordinal());
        sNamedColorSpaces[Named.ACESCG.ordinal()] = new Rgb("Academy S-2014-004 ACEScg", new float[]{0.713f, 0.293f, 0.165f, 0.83f, 0.128f, 0.044f}, ILLUMINANT_D60, d, f, f2, Named.ACESCG.ordinal());
        sNamedColorSpaces[Named.CIE_XYZ.ordinal()] = new Xyz("Generic XYZ", Named.CIE_XYZ.ordinal());
        sNamedColorSpaces[Named.CIE_LAB.ordinal()] = new Lab("Generic L*a*b*", Named.CIE_LAB.ordinal());
    }

    public enum Adaptation {
        BRADFORD(new float[]{0.8951f, -0.7502f, 0.0389f, 0.2664f, 1.7135f, -0.0685f, -0.1614f, 0.0367f, 1.0296f}),
        VON_KRIES(new float[]{0.40024f, -0.2263f, 0.0f, 0.7076f, 1.16532f, 0.0f, -0.08081f, 0.0457f, 0.91822f}),
        CIECAT02(new float[]{0.7328f, -0.7036f, 0.003f, 0.4296f, 1.6975f, 0.0136f, -0.1624f, 0.0061f, 0.9834f});

        final float[] mTransform;

        Adaptation(float[] fArr) {
            this.mTransform = fArr;
        }
    }

    public enum Model {
        RGB(3),
        XYZ(3),
        LAB(3),
        CMYK(4);

        private final int mComponentCount;

        Model(int i) {
            this.mComponentCount = i;
        }

        public int getComponentCount() {
            return this.mComponentCount;
        }
    }

    private ColorSpace(String str, Model model, int i) {
        if (str == null || str.length() < 1) {
            throw new IllegalArgumentException("The name of a color space cannot be null and must contain at least 1 character");
        }
        if (model == null) {
            throw new IllegalArgumentException("A color space must have a model");
        }
        if (i < -1 || i > 63) {
            throw new IllegalArgumentException("The id must be between -1 and 63");
        }
        this.mName = str;
        this.mModel = model;
        this.mId = i;
    }

    public String getName() {
        return this.mName;
    }

    public int getId() {
        return this.mId;
    }

    public Model getModel() {
        return this.mModel;
    }

    public int getComponentCount() {
        return this.mModel.getComponentCount();
    }

    public float[] toXyz(float f, float f2, float f3) {
        return toXyz(new float[]{f, f2, f3});
    }

    public float[] fromXyz(float f, float f2, float f3) {
        float[] fArr = new float[this.mModel.getComponentCount()];
        fArr[0] = f;
        fArr[1] = f2;
        fArr[2] = f3;
        return fromXyz(fArr);
    }

    public String toString() {
        return this.mName + " (id=" + this.mId + ", model=" + this.mModel + ")";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ColorSpace colorSpace = (ColorSpace) obj;
        return this.mId == colorSpace.mId && this.mName.equals(colorSpace.mName) && this.mModel == colorSpace.mModel;
    }

    public int hashCode() {
        return (((this.mName.hashCode() * 31) + this.mModel.hashCode()) * 31) + this.mId;
    }

    public static Connector connect(ColorSpace colorSpace, ColorSpace colorSpace2) {
        return connect(colorSpace, colorSpace2, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace colorSpace, ColorSpace colorSpace2, RenderIntent renderIntent) {
        if (colorSpace.equals(colorSpace2)) {
            return Connector.identity(colorSpace);
        }
        if (colorSpace.getModel() == Model.RGB && colorSpace2.getModel() == Model.RGB) {
            return new Connector.Rgb((Rgb) colorSpace, (Rgb) colorSpace2, renderIntent);
        }
        return new Connector(colorSpace, colorSpace2, renderIntent);
    }

    public static Connector connect(ColorSpace colorSpace) {
        return connect(colorSpace, RenderIntent.PERCEPTUAL);
    }

    public static Connector connect(ColorSpace colorSpace, RenderIntent renderIntent) {
        if (colorSpace.isSrgb()) {
            return Connector.identity(colorSpace);
        }
        if (colorSpace.getModel() == Model.RGB) {
            return new Connector.Rgb((Rgb) colorSpace, (Rgb) get(Named.SRGB), renderIntent);
        }
        return new Connector(colorSpace, get(Named.SRGB), renderIntent);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] fArr) {
        return adapt(colorSpace, fArr, Adaptation.BRADFORD);
    }

    public static ColorSpace adapt(ColorSpace colorSpace, float[] fArr, Adaptation adaptation) {
        if (colorSpace.getModel() != Model.RGB) {
            return colorSpace;
        }
        Rgb rgb = (Rgb) colorSpace;
        if (compare(rgb.mWhitePoint, fArr)) {
            return colorSpace;
        }
        return new Rgb(rgb, mul3x3(chromaticAdaptation(adaptation.mTransform, xyYToXyz(rgb.getWhitePoint()), fArr.length == 3 ? Arrays.copyOf(fArr, 3) : xyYToXyz(fArr)), rgb.mTransform), fArr);
    }

    static ColorSpace get(int i) {
        if (i < 0 || i > Named.values().length) {
            throw new IllegalArgumentException("Invalid ID, must be in the range [0.." + Named.values().length + "]");
        }
        return sNamedColorSpaces[i];
    }

    public static ColorSpace get(Named named) {
        return sNamedColorSpaces[named.ordinal()];
    }

    public static ColorSpace match(float[] fArr, Rgb.TransferParameters transferParameters) {
        for (ColorSpace colorSpace : sNamedColorSpaces) {
            if (colorSpace.getModel() == Model.RGB) {
                Rgb rgb = (Rgb) adapt(colorSpace, ILLUMINANT_D50_XYZ);
                if (compare(fArr, rgb.mTransform) && compare(transferParameters, rgb.mTransferParameters)) {
                    return colorSpace;
                }
            }
        }
        return null;
    }

    public static Renderer createRenderer() {
        return new Renderer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double rcpResponse(double d, double d2, double d3, double d4, double d5, double d6) {
        return d >= d5 * d4 ? (Math.pow(d, 1.0d / d6) - d3) / d2 : d / d4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double response(double d, double d2, double d3, double d4, double d5, double d6) {
        return d >= d5 ? Math.pow((d2 * d) + d3, d6) : d * d4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double rcpResponse(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8) {
        return d >= d5 * d4 ? (Math.pow(d - d6, 1.0d / d8) - d3) / d2 : (d - d7) / d4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double response(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8) {
        return d >= d5 ? Math.pow((d2 * d) + d3, d8) + d6 : (d4 * d) + d7;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double absRcpResponse(double d, double d2, double d3, double d4, double d5, double d6) {
        return Math.copySign(rcpResponse(d < 0.0d ? -d : d, d2, d3, d4, d5, d6), d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double absResponse(double d, double d2, double d3, double d4, double d5, double d6) {
        return Math.copySign(response(d < 0.0d ? -d : d, d2, d3, d4, d5, d6), d);
    }

    private static boolean compare(Rgb.TransferParameters transferParameters, Rgb.TransferParameters transferParameters2) {
        if (transferParameters == null && transferParameters2 == null) {
            return true;
        }
        return transferParameters != null && transferParameters2 != null && Math.abs(transferParameters.a - transferParameters2.a) < 0.001d && Math.abs(transferParameters.b - transferParameters2.b) < 0.001d && Math.abs(transferParameters.c - transferParameters2.c) < 0.001d && Math.abs(transferParameters.d - transferParameters2.d) < 0.002d && Math.abs(transferParameters.e - transferParameters2.e) < 0.001d && Math.abs(transferParameters.f - transferParameters2.f) < 0.001d && Math.abs(transferParameters.g - transferParameters2.g) < 0.001d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean compare(float[] fArr, float[] fArr2) {
        if (fArr == fArr2) {
            return true;
        }
        for (int i = 0; i < fArr.length; i++) {
            if (Float.compare(fArr[i], fArr2[i]) != 0 && Math.abs(fArr[i] - fArr2[i]) > 0.001f) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] inverse3x3(float[] fArr) {
        float f = fArr[0];
        float f2 = fArr[3];
        float f3 = fArr[6];
        float f4 = fArr[1];
        float f5 = fArr[4];
        float f6 = fArr[7];
        float f7 = fArr[2];
        float f8 = fArr[5];
        float f9 = fArr[8];
        float f10 = (f5 * f9) - (f6 * f8);
        float f11 = (f6 * f7) - (f4 * f9);
        float f12 = (f4 * f8) - (f5 * f7);
        float f13 = (f * f10) + (f2 * f11) + (f3 * f12);
        float[] fArr2 = new float[fArr.length];
        fArr2[0] = f10 / f13;
        fArr2[1] = f11 / f13;
        fArr2[2] = f12 / f13;
        fArr2[3] = ((f3 * f8) - (f2 * f9)) / f13;
        fArr2[4] = ((f9 * f) - (f3 * f7)) / f13;
        fArr2[5] = ((f7 * f2) - (f8 * f)) / f13;
        fArr2[6] = ((f2 * f6) - (f3 * f5)) / f13;
        fArr2[7] = ((f3 * f4) - (f6 * f)) / f13;
        fArr2[8] = ((f * f5) - (f2 * f4)) / f13;
        return fArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3(float[] fArr, float[] fArr2) {
        return new float[]{(fArr[0] * fArr2[0]) + (fArr[3] * fArr2[1]) + (fArr[6] * fArr2[2]), (fArr[1] * fArr2[0]) + (fArr[4] * fArr2[1]) + (fArr[7] * fArr2[2]), (fArr[2] * fArr2[0]) + (fArr[5] * fArr2[1]) + (fArr[8] * fArr2[2]), (fArr[0] * fArr2[3]) + (fArr[3] * fArr2[4]) + (fArr[6] * fArr2[5]), (fArr[1] * fArr2[3]) + (fArr[4] * fArr2[4]) + (fArr[7] * fArr2[5]), (fArr[2] * fArr2[3]) + (fArr[5] * fArr2[4]) + (fArr[8] * fArr2[5]), (fArr[0] * fArr2[6]) + (fArr[3] * fArr2[7]) + (fArr[6] * fArr2[8]), (fArr[1] * fArr2[6]) + (fArr[4] * fArr2[7]) + (fArr[7] * fArr2[8]), (fArr[2] * fArr2[6]) + (fArr[5] * fArr2[7]) + (fArr[8] * fArr2[8])};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3Float3(float[] fArr, float[] fArr2) {
        float f = fArr2[0];
        float f2 = fArr2[1];
        float f3 = fArr2[2];
        fArr2[0] = (fArr[0] * f) + (fArr[3] * f2) + (fArr[6] * f3);
        fArr2[1] = (fArr[1] * f) + (fArr[4] * f2) + (fArr[7] * f3);
        fArr2[2] = (fArr[2] * f) + (fArr[5] * f2) + (fArr[8] * f3);
        return fArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] mul3x3Diag(float[] fArr, float[] fArr2) {
        return new float[]{fArr[0] * fArr2[0], fArr[1] * fArr2[1], fArr[2] * fArr2[2], fArr[0] * fArr2[3], fArr[1] * fArr2[4], fArr[2] * fArr2[5], fArr[0] * fArr2[6], fArr[1] * fArr2[7], fArr[2] * fArr2[8]};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] xyYToXyz(float[] fArr) {
        return new float[]{fArr[0] / fArr[1], 1.0f, ((1.0f - fArr[0]) - fArr[1]) / fArr[1]};
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void xyYToUv(float[] fArr) {
        for (int i = 0; i < fArr.length; i += 2) {
            float f = fArr[i];
            int i2 = i + 1;
            float f2 = fArr[i2];
            float f3 = ((-2.0f) * f) + (12.0f * f2) + 3.0f;
            fArr[i] = (f * 4.0f) / f3;
            fArr[i2] = (f2 * 9.0f) / f3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float[] chromaticAdaptation(float[] fArr, float[] fArr2, float[] fArr3) {
        float[] fArrMul3x3Float3 = mul3x3Float3(fArr, fArr2);
        float[] fArrMul3x3Float4 = mul3x3Float3(fArr, fArr3);
        return mul3x3(inverse3x3(fArr), mul3x3Diag(new float[]{fArrMul3x3Float4[0] / fArrMul3x3Float3[0], fArrMul3x3Float4[1] / fArrMul3x3Float3[1], fArrMul3x3Float4[2] / fArrMul3x3Float3[2]}, fArr));
    }

    private static final class Xyz extends ColorSpace {
        private static float clamp(float f) {
            if (f < -2.0f) {
                return -2.0f;
            }
            if (f > 2.0f) {
                return 2.0f;
            }
            return f;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int i) {
            return 2.0f;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int i) {
            return -2.0f;
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return true;
        }

        private Xyz(String str, int i) {
            super(str, Model.XYZ, i);
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] fArr) {
            fArr[0] = clamp(fArr[0]);
            fArr[1] = clamp(fArr[1]);
            fArr[2] = clamp(fArr[2]);
            return fArr;
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] fArr) {
            fArr[0] = clamp(fArr[0]);
            fArr[1] = clamp(fArr[1]);
            fArr[2] = clamp(fArr[2]);
            return fArr;
        }
    }

    private static final class Lab extends ColorSpace {
        private static final float A = 0.008856452f;
        private static final float B = 7.787037f;
        private static final float C = 0.13793103f;
        private static final float D = 0.20689656f;

        private static float clamp(float f, float f2, float f3) {
            if (f < f2) {
                return f2;
            }
            return f > f3 ? f3 : f;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int i) {
            return i == 0 ? 100.0f : 128.0f;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int i) {
            return i == 0 ? 0.0f : -128.0f;
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return true;
        }

        private Lab(String str, int i) {
            super(str, Model.LAB, i);
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] fArr) {
            fArr[0] = clamp(fArr[0], 0.0f, 100.0f);
            fArr[1] = clamp(fArr[1], -128.0f, 128.0f);
            fArr[2] = clamp(fArr[2], -128.0f, 128.0f);
            float f = (fArr[0] + 16.0f) / 116.0f;
            float f2 = (fArr[1] * 0.002f) + f;
            float f3 = f - (fArr[2] * 0.005f);
            float f4 = f2 > D ? f2 * f2 * f2 : (f2 - C) * 0.12841855f;
            float f5 = f > D ? f * f * f : (f - C) * 0.12841855f;
            float f6 = f3 > D ? f3 * f3 * f3 : (f3 - C) * 0.12841855f;
            fArr[0] = f4 * ColorSpace.ILLUMINANT_D50_XYZ[0];
            fArr[1] = f5 * ColorSpace.ILLUMINANT_D50_XYZ[1];
            fArr[2] = f6 * ColorSpace.ILLUMINANT_D50_XYZ[2];
            return fArr;
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] fArr) {
            float f = fArr[0] / ColorSpace.ILLUMINANT_D50_XYZ[0];
            float f2 = fArr[1] / ColorSpace.ILLUMINANT_D50_XYZ[1];
            float f3 = fArr[2] / ColorSpace.ILLUMINANT_D50_XYZ[2];
            float fPow = f > A ? (float) Math.pow(f, 0.3333333333333333d) : (f * B) + C;
            float fPow2 = f2 > A ? (float) Math.pow(f2, 0.3333333333333333d) : (f2 * B) + C;
            float f4 = (116.0f * fPow2) - 16.0f;
            float f5 = (fPow - fPow2) * 500.0f;
            float fPow3 = (fPow2 - (f3 > A ? (float) Math.pow(f3, 0.3333333333333333d) : (f3 * B) + C)) * 200.0f;
            fArr[0] = clamp(f4, 0.0f, 100.0f);
            fArr[1] = clamp(f5, -128.0f, 128.0f);
            fArr[2] = clamp(fPow3, -128.0f, 128.0f);
            return fArr;
        }
    }

    public static class Rgb extends ColorSpace {
        private final DoubleUnaryOperator mClampedEotf;
        private final DoubleUnaryOperator mClampedOetf;
        private final DoubleUnaryOperator mEotf;
        private final float[] mInverseTransform;
        private final boolean mIsSrgb;
        private final boolean mIsWideGamut;
        private final float mMax;
        private final float mMin;
        private final DoubleUnaryOperator mOetf;
        private final float[] mPrimaries;
        private TransferParameters mTransferParameters;
        private final float[] mTransform;
        private final float[] mWhitePoint;

        private static float cross(float f, float f2, float f3, float f4) {
            return (f * f4) - (f2 * f3);
        }

        public static class TransferParameters {
            public final double a;
            public final double b;
            public final double c;
            public final double d;
            public final double e;
            public final double f;
            public final double g;

            public TransferParameters(double d, double d2, double d3, double d4, double d5) {
                this(d, d2, d3, d4, 0.0d, 0.0d, d5);
            }

            public TransferParameters(double d, double d2, double d3, double d4, double d5, double d6, double d7) {
                if (Double.isNaN(d) || Double.isNaN(d2) || Double.isNaN(d3) || Double.isNaN(d4) || Double.isNaN(d5) || Double.isNaN(d6) || Double.isNaN(d7)) {
                    throw new IllegalArgumentException("Parameters cannot be NaN");
                }
                if (d4 < 0.0d || d4 > Math.ulp(1.0f) + 1.0f) {
                    throw new IllegalArgumentException("Parameter d must be in the range [0..1], was " + d4);
                }
                if (d4 == 0.0d && (d == 0.0d || d7 == 0.0d)) {
                    throw new IllegalArgumentException("Parameter a or g is zero, the transfer function is constant");
                }
                if (d4 >= 1.0d && d3 == 0.0d) {
                    throw new IllegalArgumentException("Parameter c is zero, the transfer function is constant");
                }
                if ((d == 0.0d || d7 == 0.0d) && d3 == 0.0d) {
                    throw new IllegalArgumentException("Parameter a or g is zero, and c is zero, the transfer function is constant");
                }
                if (d3 < 0.0d) {
                    throw new IllegalArgumentException("The transfer function must be increasing");
                }
                if (d < 0.0d || d7 < 0.0d) {
                    throw new IllegalArgumentException("The transfer function must be positive or increasing");
                }
                this.a = d;
                this.b = d2;
                this.c = d3;
                this.d = d4;
                this.e = d5;
                this.f = d6;
                this.g = d7;
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || getClass() != obj.getClass()) {
                    return false;
                }
                TransferParameters transferParameters = (TransferParameters) obj;
                return Double.compare(transferParameters.a, this.a) == 0 && Double.compare(transferParameters.b, this.b) == 0 && Double.compare(transferParameters.c, this.c) == 0 && Double.compare(transferParameters.d, this.d) == 0 && Double.compare(transferParameters.e, this.e) == 0 && Double.compare(transferParameters.f, this.f) == 0 && Double.compare(transferParameters.g, this.g) == 0;
            }

            public int hashCode() {
                long jDoubleToLongBits = Double.doubleToLongBits(this.a);
                long jDoubleToLongBits2 = Double.doubleToLongBits(this.b);
                int i = (((int) (jDoubleToLongBits ^ (jDoubleToLongBits >>> 32))) * 31) + ((int) (jDoubleToLongBits2 ^ (jDoubleToLongBits2 >>> 32)));
                long jDoubleToLongBits3 = Double.doubleToLongBits(this.c);
                int i2 = (i * 31) + ((int) (jDoubleToLongBits3 ^ (jDoubleToLongBits3 >>> 32)));
                long jDoubleToLongBits4 = Double.doubleToLongBits(this.d);
                int i3 = (i2 * 31) + ((int) (jDoubleToLongBits4 ^ (jDoubleToLongBits4 >>> 32)));
                long jDoubleToLongBits5 = Double.doubleToLongBits(this.e);
                int i4 = (i3 * 31) + ((int) (jDoubleToLongBits5 ^ (jDoubleToLongBits5 >>> 32)));
                long jDoubleToLongBits6 = Double.doubleToLongBits(this.f);
                int i5 = (i4 * 31) + ((int) (jDoubleToLongBits6 ^ (jDoubleToLongBits6 >>> 32)));
                long jDoubleToLongBits7 = Double.doubleToLongBits(this.g);
                return (i5 * 31) + ((int) ((jDoubleToLongBits7 >>> 32) ^ jDoubleToLongBits7));
            }
        }

        public Rgb(String str, float[] fArr, DoubleUnaryOperator doubleUnaryOperator, DoubleUnaryOperator doubleUnaryOperator2) {
            this(str, computePrimaries(fArr), computeWhitePoint(fArr), doubleUnaryOperator, doubleUnaryOperator2, 0.0f, 1.0f, -1);
        }

        public Rgb(String str, float[] fArr, float[] fArr2, DoubleUnaryOperator doubleUnaryOperator, DoubleUnaryOperator doubleUnaryOperator2, float f, float f2) {
            this(str, fArr, fArr2, doubleUnaryOperator, doubleUnaryOperator2, f, f2, -1);
        }

        public Rgb(String str, float[] fArr, TransferParameters transferParameters) {
            this(str, computePrimaries(fArr), computeWhitePoint(fArr), transferParameters, -1);
        }

        public Rgb(String str, float[] fArr, float[] fArr2, TransferParameters transferParameters) {
            this(str, fArr, fArr2, transferParameters, -1);
        }

        private Rgb(String str, float[] fArr, float[] fArr2, final TransferParameters transferParameters, int i) {
            DoubleUnaryOperator doubleUnaryOperator;
            DoubleUnaryOperator doubleUnaryOperator2;
            if (transferParameters.e == 0.0d && transferParameters.f == 0.0d) {
                doubleUnaryOperator = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$bWzafC8vMHNuVmRuTUPEFUMlfuY
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        ColorSpace.Rgb.TransferParameters transferParameters2 = transferParameters;
                        return ColorSpace.rcpResponse(d, transferParameters2.a, transferParameters2.b, transferParameters2.c, transferParameters2.d, transferParameters2.g);
                    }
                };
            } else {
                doubleUnaryOperator = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$V_0lmM2WEpxGBDV_1G1wvvidn7Y
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        ColorSpace.Rgb.TransferParameters transferParameters2 = transferParameters;
                        return ColorSpace.rcpResponse(d, transferParameters2.a, transferParameters2.b, transferParameters2.c, transferParameters2.d, transferParameters2.e, transferParameters2.f, transferParameters2.g);
                    }
                };
            }
            DoubleUnaryOperator doubleUnaryOperator3 = doubleUnaryOperator;
            if (transferParameters.e == 0.0d && transferParameters.f == 0.0d) {
                doubleUnaryOperator2 = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$b9VGKuNnse0bbguR9jbOM_wK2Ac
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        ColorSpace.Rgb.TransferParameters transferParameters2 = transferParameters;
                        return ColorSpace.response(d, transferParameters2.a, transferParameters2.b, transferParameters2.c, transferParameters2.d, transferParameters2.g);
                    }
                };
            } else {
                doubleUnaryOperator2 = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$iMkODTKa3_8kPZUnZZerD2Lv-yo
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d) {
                        ColorSpace.Rgb.TransferParameters transferParameters2 = transferParameters;
                        return ColorSpace.response(d, transferParameters2.a, transferParameters2.b, transferParameters2.c, transferParameters2.d, transferParameters2.e, transferParameters2.f, transferParameters2.g);
                    }
                };
            }
            this(str, fArr, fArr2, doubleUnaryOperator3, doubleUnaryOperator2, 0.0f, 1.0f, i);
            this.mTransferParameters = transferParameters;
        }

        public Rgb(String str, float[] fArr, double d) {
            this(str, computePrimaries(fArr), computeWhitePoint(fArr), d, 0.0f, 1.0f, -1);
        }

        public Rgb(String str, float[] fArr, float[] fArr2, double d) {
            this(str, fArr, fArr2, d, 0.0f, 1.0f, -1);
        }

        /* JADX WARN: Illegal instructions before constructor call */
        private Rgb(String str, float[] fArr, float[] fArr2, final double d, float f, float f2, int i) {
            DoubleUnaryOperator doubleUnaryOperatorIdentity;
            DoubleUnaryOperator doubleUnaryOperatorIdentity2;
            TransferParameters transferParameters;
            if (d == 1.0d) {
                doubleUnaryOperatorIdentity = DoubleUnaryOperator.identity();
            } else {
                doubleUnaryOperatorIdentity = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$CqKld6797g7__JnuY0NeFz5q4_E
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d2) {
                        return ColorSpace.Rgb.lambda$new$4(d, d2);
                    }
                };
            }
            DoubleUnaryOperator doubleUnaryOperator = doubleUnaryOperatorIdentity;
            if (d == 1.0d) {
                doubleUnaryOperatorIdentity2 = DoubleUnaryOperator.identity();
            } else {
                doubleUnaryOperatorIdentity2 = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$ZvS77aTfobOSa2o9MTqYMph4Rcg
                    @Override // java.util.function.DoubleUnaryOperator
                    public final double applyAsDouble(double d2) {
                        return ColorSpace.Rgb.lambda$new$5(d, d2);
                    }
                };
            }
            this(str, fArr, fArr2, doubleUnaryOperator, doubleUnaryOperatorIdentity2, f, f2, i);
            if (d == 1.0d) {
                transferParameters = new TransferParameters(0.0d, 0.0d, 1.0d, ((double) Math.ulp(1.0f)) + 1.0d, d);
            } else {
                transferParameters = new TransferParameters(1.0d, 0.0d, 0.0d, 0.0d, d);
            }
            this.mTransferParameters = transferParameters;
        }

        static /* synthetic */ double lambda$new$4(double d, double d2) {
            if (d2 < 0.0d) {
                d2 = 0.0d;
            }
            return Math.pow(d2, 1.0d / d);
        }

        static /* synthetic */ double lambda$new$5(double d, double d2) {
            if (d2 < 0.0d) {
                d2 = 0.0d;
            }
            return Math.pow(d2, d);
        }

        private Rgb(String str, float[] fArr, float[] fArr2, DoubleUnaryOperator doubleUnaryOperator, DoubleUnaryOperator doubleUnaryOperator2, float f, float f2, int i) {
            super(str, Model.RGB, i);
            if (fArr == null || (fArr.length != 6 && fArr.length != 9)) {
                throw new IllegalArgumentException("The color space's primaries must be defined as an array of 6 floats in xyY or 9 floats in XYZ");
            }
            if (fArr2 == null || !(fArr2.length == 2 || fArr2.length == 3)) {
                throw new IllegalArgumentException("The color space's white point must be defined as an array of 2 floats in xyY or 3 float in XYZ");
            }
            if (doubleUnaryOperator == null || doubleUnaryOperator2 == null) {
                throw new IllegalArgumentException("The transfer functions of a color space cannot be null");
            }
            if (f >= f2) {
                throw new IllegalArgumentException("Invalid range: min=" + f + ", max=" + f2 + "; min must be strictly < max");
            }
            this.mWhitePoint = xyWhitePoint(fArr2);
            float[] fArrXyPrimaries = xyPrimaries(fArr);
            this.mPrimaries = fArrXyPrimaries;
            float[] fArrComputeXYZMatrix = computeXYZMatrix(fArrXyPrimaries, this.mWhitePoint);
            this.mTransform = fArrComputeXYZMatrix;
            this.mInverseTransform = ColorSpace.inverse3x3(fArrComputeXYZMatrix);
            this.mOetf = doubleUnaryOperator;
            this.mEotf = doubleUnaryOperator2;
            this.mMin = f;
            this.mMax = f2;
            DoubleUnaryOperator doubleUnaryOperator3 = new DoubleUnaryOperator() { // from class: android.graphics.-$$Lambda$ColorSpace$Rgb$8EkhO2jIf14tuA3BvrmYJMa7YXM
                @Override // java.util.function.DoubleUnaryOperator
                public final double applyAsDouble(double d) {
                    return this.f$0.clamp(d);
                }
            };
            this.mClampedOetf = doubleUnaryOperator.andThen(doubleUnaryOperator3);
            this.mClampedEotf = doubleUnaryOperator3.andThen(doubleUnaryOperator2);
            this.mIsWideGamut = isWideGamut(this.mPrimaries, f, f2);
            this.mIsSrgb = isSrgb(this.mPrimaries, this.mWhitePoint, doubleUnaryOperator, doubleUnaryOperator2, f, f2, i);
        }

        private Rgb(Rgb rgb, float[] fArr, float[] fArr2) {
            super(rgb.getName(), Model.RGB, -1);
            this.mWhitePoint = xyWhitePoint(fArr2);
            this.mPrimaries = rgb.mPrimaries;
            this.mTransform = fArr;
            this.mInverseTransform = ColorSpace.inverse3x3(fArr);
            this.mMin = rgb.mMin;
            this.mMax = rgb.mMax;
            this.mOetf = rgb.mOetf;
            this.mEotf = rgb.mEotf;
            this.mClampedOetf = rgb.mClampedOetf;
            this.mClampedEotf = rgb.mClampedEotf;
            this.mIsWideGamut = rgb.mIsWideGamut;
            this.mIsSrgb = rgb.mIsSrgb;
            this.mTransferParameters = rgb.mTransferParameters;
        }

        public float[] getWhitePoint(float[] fArr) {
            float[] fArr2 = this.mWhitePoint;
            fArr[0] = fArr2[0];
            fArr[1] = fArr2[1];
            return fArr;
        }

        public float[] getWhitePoint() {
            float[] fArr = this.mWhitePoint;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getPrimaries(float[] fArr) {
            float[] fArr2 = this.mPrimaries;
            System.arraycopy(fArr2, 0, fArr, 0, fArr2.length);
            return fArr;
        }

        public float[] getPrimaries() {
            float[] fArr = this.mPrimaries;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getTransform(float[] fArr) {
            float[] fArr2 = this.mTransform;
            System.arraycopy(fArr2, 0, fArr, 0, fArr2.length);
            return fArr;
        }

        public float[] getTransform() {
            float[] fArr = this.mTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public float[] getInverseTransform(float[] fArr) {
            float[] fArr2 = this.mInverseTransform;
            System.arraycopy(fArr2, 0, fArr, 0, fArr2.length);
            return fArr;
        }

        public float[] getInverseTransform() {
            float[] fArr = this.mInverseTransform;
            return Arrays.copyOf(fArr, fArr.length);
        }

        public DoubleUnaryOperator getOetf() {
            return this.mClampedOetf;
        }

        public DoubleUnaryOperator getEotf() {
            return this.mClampedEotf;
        }

        public TransferParameters getTransferParameters() {
            return this.mTransferParameters;
        }

        @Override // android.graphics.ColorSpace
        public boolean isSrgb() {
            return this.mIsSrgb;
        }

        @Override // android.graphics.ColorSpace
        public boolean isWideGamut() {
            return this.mIsWideGamut;
        }

        @Override // android.graphics.ColorSpace
        public float getMinValue(int i) {
            return this.mMin;
        }

        @Override // android.graphics.ColorSpace
        public float getMaxValue(int i) {
            return this.mMax;
        }

        public float[] toLinear(float f, float f2, float f3) {
            return toLinear(new float[]{f, f2, f3});
        }

        public float[] toLinear(float[] fArr) {
            fArr[0] = (float) this.mClampedEotf.applyAsDouble(fArr[0]);
            fArr[1] = (float) this.mClampedEotf.applyAsDouble(fArr[1]);
            fArr[2] = (float) this.mClampedEotf.applyAsDouble(fArr[2]);
            return fArr;
        }

        public float[] fromLinear(float f, float f2, float f3) {
            return fromLinear(new float[]{f, f2, f3});
        }

        public float[] fromLinear(float[] fArr) {
            fArr[0] = (float) this.mClampedOetf.applyAsDouble(fArr[0]);
            fArr[1] = (float) this.mClampedOetf.applyAsDouble(fArr[1]);
            fArr[2] = (float) this.mClampedOetf.applyAsDouble(fArr[2]);
            return fArr;
        }

        @Override // android.graphics.ColorSpace
        public float[] toXyz(float[] fArr) {
            fArr[0] = (float) this.mClampedEotf.applyAsDouble(fArr[0]);
            fArr[1] = (float) this.mClampedEotf.applyAsDouble(fArr[1]);
            fArr[2] = (float) this.mClampedEotf.applyAsDouble(fArr[2]);
            return ColorSpace.mul3x3Float3(this.mTransform, fArr);
        }

        @Override // android.graphics.ColorSpace
        public float[] fromXyz(float[] fArr) {
            ColorSpace.mul3x3Float3(this.mInverseTransform, fArr);
            fArr[0] = (float) this.mClampedOetf.applyAsDouble(fArr[0]);
            fArr[1] = (float) this.mClampedOetf.applyAsDouble(fArr[1]);
            fArr[2] = (float) this.mClampedOetf.applyAsDouble(fArr[2]);
            return fArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public double clamp(double d) {
            float f = this.mMin;
            if (d >= f) {
                f = this.mMax;
                if (d <= f) {
                    return d;
                }
            }
            return f;
        }

        @Override // android.graphics.ColorSpace
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
                return false;
            }
            Rgb rgb = (Rgb) obj;
            if (Float.compare(rgb.mMin, this.mMin) != 0 || Float.compare(rgb.mMax, this.mMax) != 0 || !Arrays.equals(this.mWhitePoint, rgb.mWhitePoint) || !Arrays.equals(this.mPrimaries, rgb.mPrimaries)) {
                return false;
            }
            TransferParameters transferParameters = this.mTransferParameters;
            if (transferParameters != null) {
                return transferParameters.equals(rgb.mTransferParameters);
            }
            if (rgb.mTransferParameters == null) {
                return true;
            }
            if (this.mOetf.equals(rgb.mOetf)) {
                return this.mEotf.equals(rgb.mEotf);
            }
            return false;
        }

        @Override // android.graphics.ColorSpace
        public int hashCode() {
            int iHashCode = ((((super.hashCode() * 31) + Arrays.hashCode(this.mWhitePoint)) * 31) + Arrays.hashCode(this.mPrimaries)) * 31;
            float f = this.mMin;
            int iFloatToIntBits = (iHashCode + (f != 0.0f ? Float.floatToIntBits(f) : 0)) * 31;
            float f2 = this.mMax;
            int iFloatToIntBits2 = (iFloatToIntBits + (f2 != 0.0f ? Float.floatToIntBits(f2) : 0)) * 31;
            TransferParameters transferParameters = this.mTransferParameters;
            int iHashCode2 = iFloatToIntBits2 + (transferParameters != null ? transferParameters.hashCode() : 0);
            return this.mTransferParameters == null ? (((iHashCode2 * 31) + this.mOetf.hashCode()) * 31) + this.mEotf.hashCode() : iHashCode2;
        }

        private static boolean isSrgb(float[] fArr, float[] fArr2, DoubleUnaryOperator doubleUnaryOperator, DoubleUnaryOperator doubleUnaryOperator2, float f, float f2, int i) {
            if (i == 0) {
                return true;
            }
            return ColorSpace.compare(fArr, ColorSpace.SRGB_PRIMARIES) && ColorSpace.compare(fArr2, ILLUMINANT_D65) && doubleUnaryOperator.applyAsDouble(0.5d) >= 0.5001d && doubleUnaryOperator2.applyAsDouble(0.5d) <= 0.5001d && f == 0.0f && f2 == 1.0f;
        }

        private static boolean isWideGamut(float[] fArr, float f, float f2) {
            return (area(fArr) / area(ColorSpace.NTSC_1953_PRIMARIES) > 0.9f && contains(fArr, ColorSpace.SRGB_PRIMARIES)) || (f < 0.0f && f2 > 1.0f);
        }

        private static float area(float[] fArr) {
            float f = fArr[0];
            float f2 = fArr[1];
            float f3 = fArr[2];
            float f4 = fArr[3];
            float f5 = fArr[4];
            float f6 = fArr[5];
            float f7 = ((((((f * f4) + (f2 * f5)) + (f3 * f6)) - (f4 * f5)) - (f2 * f3)) - (f * f6)) * 0.5f;
            return f7 < 0.0f ? -f7 : f7;
        }

        private static boolean contains(float[] fArr, float[] fArr2) {
            float[] fArr3 = {fArr[0] - fArr2[0], fArr[1] - fArr2[1], fArr[2] - fArr2[2], fArr[3] - fArr2[3], fArr[4] - fArr2[4], fArr[5] - fArr2[5]};
            return cross(fArr3[0], fArr3[1], fArr2[0] - fArr2[4], fArr2[1] - fArr2[5]) >= 0.0f && cross(fArr2[0] - fArr2[2], fArr2[1] - fArr2[3], fArr3[0], fArr3[1]) >= 0.0f && cross(fArr3[2], fArr3[3], fArr2[2] - fArr2[0], fArr2[3] - fArr2[1]) >= 0.0f && cross(fArr2[2] - fArr2[4], fArr2[3] - fArr2[5], fArr3[2], fArr3[3]) >= 0.0f && cross(fArr3[4], fArr3[5], fArr2[4] - fArr2[2], fArr2[5] - fArr2[3]) >= 0.0f && cross(fArr2[4] - fArr2[0], fArr2[5] - fArr2[1], fArr3[4], fArr3[5]) >= 0.0f;
        }

        private static float[] computePrimaries(float[] fArr) {
            float[] fArrMul3x3Float3 = ColorSpace.mul3x3Float3(fArr, new float[]{1.0f, 0.0f, 0.0f});
            float[] fArrMul3x3Float4 = ColorSpace.mul3x3Float3(fArr, new float[]{0.0f, 1.0f, 0.0f});
            float[] fArrMul3x3Float5 = ColorSpace.mul3x3Float3(fArr, new float[]{0.0f, 0.0f, 1.0f});
            float f = fArrMul3x3Float3[0] + fArrMul3x3Float3[1] + fArrMul3x3Float3[2];
            float f2 = fArrMul3x3Float4[0] + fArrMul3x3Float4[1] + fArrMul3x3Float4[2];
            float f3 = fArrMul3x3Float5[0] + fArrMul3x3Float5[1] + fArrMul3x3Float5[2];
            return new float[]{fArrMul3x3Float3[0] / f, fArrMul3x3Float3[1] / f, fArrMul3x3Float4[0] / f2, fArrMul3x3Float4[1] / f2, fArrMul3x3Float5[0] / f3, fArrMul3x3Float5[1] / f3};
        }

        private static float[] computeWhitePoint(float[] fArr) {
            float[] fArrMul3x3Float3 = ColorSpace.mul3x3Float3(fArr, new float[]{1.0f, 1.0f, 1.0f});
            float f = fArrMul3x3Float3[0] + fArrMul3x3Float3[1] + fArrMul3x3Float3[2];
            return new float[]{fArrMul3x3Float3[0] / f, fArrMul3x3Float3[1] / f};
        }

        private static float[] xyPrimaries(float[] fArr) {
            float[] fArr2 = new float[6];
            if (fArr.length == 9) {
                float f = fArr[0] + fArr[1] + fArr[2];
                fArr2[0] = fArr[0] / f;
                fArr2[1] = fArr[1] / f;
                float f2 = fArr[3] + fArr[4] + fArr[5];
                fArr2[2] = fArr[3] / f2;
                fArr2[3] = fArr[4] / f2;
                float f3 = fArr[6] + fArr[7] + fArr[8];
                fArr2[4] = fArr[6] / f3;
                fArr2[5] = fArr[7] / f3;
            } else {
                System.arraycopy(fArr, 0, fArr2, 0, 6);
            }
            return fArr2;
        }

        private static float[] xyWhitePoint(float[] fArr) {
            float[] fArr2 = new float[2];
            if (fArr.length == 3) {
                float f = fArr[0] + fArr[1] + fArr[2];
                fArr2[0] = fArr[0] / f;
                fArr2[1] = fArr[1] / f;
            } else {
                System.arraycopy(fArr, 0, fArr2, 0, 2);
            }
            return fArr2;
        }

        private static float[] computeXYZMatrix(float[] fArr, float[] fArr2) {
            float f = fArr[0];
            float f2 = fArr[1];
            float f3 = fArr[2];
            float f4 = fArr[3];
            float f5 = fArr[4];
            float f6 = fArr[5];
            float f7 = fArr2[0];
            float f8 = fArr2[1];
            float f9 = 1.0f - f;
            float f10 = f9 / f2;
            float f11 = 1.0f - f3;
            float f12 = 1.0f - f5;
            float f13 = (1.0f - f7) / f8;
            float f14 = f / f2;
            float f15 = (f3 / f4) - f14;
            float f16 = (f7 / f8) - f14;
            float f17 = (f11 / f4) - f10;
            float f18 = (f5 / f6) - f14;
            float f19 = (((f13 - f10) * f15) - (f16 * f17)) / ((((f12 / f6) - f10) * f15) - (f17 * f18));
            float f20 = (f16 - (f18 * f19)) / f15;
            float f21 = (1.0f - f20) - f19;
            float f22 = f21 / f2;
            float f23 = f20 / f4;
            float f24 = f19 / f6;
            return new float[]{f * f22, f21, f22 * (f9 - f2), f3 * f23, f20, f23 * (f11 - f4), f5 * f24, f19, f24 * (f12 - f6)};
        }
    }

    public static class Connector {
        private final ColorSpace mDestination;
        private final RenderIntent mIntent;
        private final ColorSpace mSource;
        private final float[] mTransform;
        private final ColorSpace mTransformDestination;
        private final ColorSpace mTransformSource;

        Connector(ColorSpace colorSpace, ColorSpace colorSpace2, RenderIntent renderIntent) {
            this(colorSpace, colorSpace2, colorSpace.getModel() == Model.RGB ? ColorSpace.adapt(colorSpace, ColorSpace.ILLUMINANT_D50_XYZ) : colorSpace, colorSpace2.getModel() == Model.RGB ? ColorSpace.adapt(colorSpace2, ColorSpace.ILLUMINANT_D50_XYZ) : colorSpace2, renderIntent, computeTransform(colorSpace, colorSpace2, renderIntent));
        }

        private Connector(ColorSpace colorSpace, ColorSpace colorSpace2, ColorSpace colorSpace3, ColorSpace colorSpace4, RenderIntent renderIntent, float[] fArr) {
            this.mSource = colorSpace;
            this.mDestination = colorSpace2;
            this.mTransformSource = colorSpace3;
            this.mTransformDestination = colorSpace4;
            this.mIntent = renderIntent;
            this.mTransform = fArr;
        }

        private static float[] computeTransform(ColorSpace colorSpace, ColorSpace colorSpace2, RenderIntent renderIntent) {
            if (renderIntent != RenderIntent.ABSOLUTE) {
                return null;
            }
            boolean z = colorSpace.getModel() == Model.RGB;
            boolean z2 = colorSpace2.getModel() == Model.RGB;
            if (z && z2) {
                return null;
            }
            if (!z && !z2) {
                return null;
            }
            if (!z) {
                colorSpace = colorSpace2;
            }
            Rgb rgb = (Rgb) colorSpace;
            float[] fArrXyYToXyz = z ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
            float[] fArrXyYToXyz2 = z2 ? ColorSpace.xyYToXyz(rgb.mWhitePoint) : ColorSpace.ILLUMINANT_D50_XYZ;
            return new float[]{fArrXyYToXyz[0] / fArrXyYToXyz2[0], fArrXyYToXyz[1] / fArrXyYToXyz2[1], fArrXyYToXyz[2] / fArrXyYToXyz2[2]};
        }

        public ColorSpace getSource() {
            return this.mSource;
        }

        public ColorSpace getDestination() {
            return this.mDestination;
        }

        public RenderIntent getRenderIntent() {
            return this.mIntent;
        }

        public float[] transform(float f, float f2, float f3) {
            return transform(new float[]{f, f2, f3});
        }

        public float[] transform(float[] fArr) {
            float[] xyz = this.mTransformSource.toXyz(fArr);
            float[] fArr2 = this.mTransform;
            if (fArr2 != null) {
                xyz[0] = xyz[0] * fArr2[0];
                xyz[1] = xyz[1] * fArr2[1];
                xyz[2] = xyz[2] * fArr2[2];
            }
            return this.mTransformDestination.fromXyz(xyz);
        }

        private static class Rgb extends Connector {
            private final Rgb mDestination;
            private final Rgb mSource;
            private final float[] mTransform;

            Rgb(Rgb rgb, Rgb rgb2, RenderIntent renderIntent) {
                super(rgb2, rgb, rgb2, renderIntent, null);
                this.mSource = rgb;
                this.mDestination = rgb2;
                this.mTransform = computeTransform(rgb, rgb2, renderIntent);
            }

            @Override // android.graphics.ColorSpace.Connector
            public float[] transform(float[] fArr) {
                fArr[0] = (float) this.mSource.mClampedEotf.applyAsDouble(fArr[0]);
                fArr[1] = (float) this.mSource.mClampedEotf.applyAsDouble(fArr[1]);
                fArr[2] = (float) this.mSource.mClampedEotf.applyAsDouble(fArr[2]);
                ColorSpace.mul3x3Float3(this.mTransform, fArr);
                fArr[0] = (float) this.mDestination.mClampedOetf.applyAsDouble(fArr[0]);
                fArr[1] = (float) this.mDestination.mClampedOetf.applyAsDouble(fArr[1]);
                fArr[2] = (float) this.mDestination.mClampedOetf.applyAsDouble(fArr[2]);
                return fArr;
            }

            private static float[] computeTransform(Rgb rgb, Rgb rgb2, RenderIntent renderIntent) {
                if (ColorSpace.compare(rgb.mWhitePoint, rgb2.mWhitePoint)) {
                    return ColorSpace.mul3x3(rgb2.mInverseTransform, rgb.mTransform);
                }
                float[] fArrMul3x3Diag = rgb.mTransform;
                float[] fArrInverse3x3 = rgb2.mInverseTransform;
                float[] fArrXyYToXyz = ColorSpace.xyYToXyz(rgb.mWhitePoint);
                float[] fArrXyYToXyz2 = ColorSpace.xyYToXyz(rgb2.mWhitePoint);
                if (!ColorSpace.compare(rgb.mWhitePoint, ColorSpace.ILLUMINANT_D50)) {
                    fArrMul3x3Diag = ColorSpace.mul3x3(ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, fArrXyYToXyz, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3)), rgb.mTransform);
                }
                if (!ColorSpace.compare(rgb2.mWhitePoint, ColorSpace.ILLUMINANT_D50)) {
                    fArrInverse3x3 = ColorSpace.inverse3x3(ColorSpace.mul3x3(ColorSpace.chromaticAdaptation(Adaptation.BRADFORD.mTransform, fArrXyYToXyz2, Arrays.copyOf(ColorSpace.ILLUMINANT_D50_XYZ, 3)), rgb2.mTransform));
                }
                if (renderIntent == RenderIntent.ABSOLUTE) {
                    fArrMul3x3Diag = ColorSpace.mul3x3Diag(new float[]{fArrXyYToXyz[0] / fArrXyYToXyz2[0], fArrXyYToXyz[1] / fArrXyYToXyz2[1], fArrXyYToXyz[2] / fArrXyYToXyz2[2]}, fArrMul3x3Diag);
                }
                return ColorSpace.mul3x3(fArrInverse3x3, fArrMul3x3Diag);
            }
        }

        static Connector identity(ColorSpace colorSpace) {
            return new Connector(colorSpace, colorSpace, RenderIntent.RELATIVE) { // from class: android.graphics.ColorSpace.Connector.1
                @Override // android.graphics.ColorSpace.Connector
                public float[] transform(float[] fArr) {
                    return fArr;
                }
            };
        }
    }

    public static class Renderer {
        private static final int CHROMATICITY_RESOLUTION = 32;
        private static final int NATIVE_SIZE = 1440;
        private static final double ONE_THIRD = 0.3333333333333333d;
        private static final float[] SPECTRUM_LOCUS_X = {0.175596f, 0.172787f, 0.170806f, 0.170085f, 0.160343f, 0.146958f, 0.139149f, 0.133536f, 0.126688f, 0.11583f, 0.109616f, 0.099146f, 0.09131f, 0.07813f, 0.068717f, 0.054675f, 0.040763f, 0.027497f, 0.01627f, 0.008169f, 0.004876f, 0.003983f, 0.003859f, 0.004646f, 0.007988f, 0.01387f, 0.022244f, 0.027273f, 0.03282f, 0.038851f, 0.045327f, 0.052175f, 0.059323f, 0.066713f, 0.074299f, 0.089937f, 0.114155f, 0.138695f, 0.154714f, 0.192865f, 0.229607f, 0.26576f, 0.301588f, 0.337346f, 0.373083f, 0.408717f, 0.444043f, 0.478755f, 0.512467f, 0.544767f, 0.575132f, 0.602914f, 0.627018f, 0.648215f, 0.665746f, 0.680061f, 0.691487f, 0.700589f, 0.707901f, 0.714015f, 0.719017f, 0.723016f, 0.734674f, 0.717203f, 0.699732f, 0.68226f, 0.664789f, 0.647318f, 0.629847f, 0.612376f, 0.594905f, 0.577433f, 0.559962f, 0.542491f, 0.52502f, 0.507549f, 0.490077f, 0.472606f, 0.455135f, 0.437664f, 0.420193f, 0.402721f, 0.38525f, 0.367779f, 0.350308f, 0.332837f, 0.315366f, 0.297894f, 0.280423f, 0.262952f, 0.245481f, 0.22801f, 0.210538f, 0.193067f, 0.175596f};
        private static final float[] SPECTRUM_LOCUS_Y = {0.005295f, 0.0048f, 0.005472f, 0.005976f, 0.014496f, 0.026643f, 0.035211f, 0.042704f, 0.053441f, 0.073601f, 0.086866f, 0.112037f, 0.132737f, 0.170464f, 0.200773f, 0.254155f, 0.317049f, 0.387997f, 0.463035f, 0.538504f, 0.587196f, 0.610526f, 0.654897f, 0.67597f, 0.715407f, 0.750246f, 0.779682f, 0.792153f, 0.802971f, 0.812059f, 0.81943f, 0.8252f, 0.82946f, 0.832306f, 0.833833f, 0.833316f, 0.826231f, 0.814796f, 0.805884f, 0.781648f, 0.754347f, 0.724342f, 0.692326f, 0.658867f, 0.62447f, 0.589626f, 0.554734f, 0.520222f, 0.486611f, 0.454454f, 0.424252f, 0.396516f, 0.37251f, 0.351413f, 0.334028f, 0.319765f, 0.308359f, 0.299317f, 0.292044f, 0.285945f, 0.280951f, 0.276964f, 0.265326f, 0.2572f, 0.249074f, 0.240948f, 0.232822f, 0.224696f, 0.21657f, 0.208444f, 0.200318f, 0.192192f, 0.184066f, 0.17594f, 0.167814f, 0.159688f, 0.151562f, 0.143436f, 0.135311f, 0.127185f, 0.119059f, 0.110933f, 0.102807f, 0.094681f, 0.086555f, 0.078429f, 0.070303f, 0.062177f, 0.054051f, 0.045925f, 0.037799f, 0.029673f, 0.021547f, 0.013421f, 0.005295f};
        private static final float UCS_SCALE = 1.5f;
        private boolean mClip;
        private final List<Pair<ColorSpace, Integer>> mColorSpaces;
        private final List<Point> mPoints;
        private boolean mShowWhitePoint;
        private int mSize;
        private boolean mUcs;

        private static double sqr(double d) {
            return d * d;
        }

        private Renderer() {
            this.mSize = 1024;
            this.mShowWhitePoint = true;
            this.mClip = false;
            this.mUcs = false;
            this.mColorSpaces = new ArrayList(2);
            this.mPoints = new ArrayList(0);
        }

        public Renderer clip(boolean z) {
            this.mClip = z;
            return this;
        }

        public Renderer uniformChromaticityScale(boolean z) {
            this.mUcs = z;
            return this;
        }

        public Renderer size(int i) {
            this.mSize = Math.max(128, i);
            return this;
        }

        public Renderer showWhitePoint(boolean z) {
            this.mShowWhitePoint = z;
            return this;
        }

        public Renderer add(ColorSpace colorSpace, int i) {
            this.mColorSpaces.add(new Pair<>(colorSpace, Integer.valueOf(i)));
            return this;
        }

        public Renderer add(ColorSpace colorSpace, float f, float f2, float f3, int i) {
            this.mPoints.add(new Point(colorSpace, new float[]{f, f2, f3}, i));
            return this;
        }

        public Bitmap render() {
            Paint paint = new Paint(1);
            int i = this.mSize;
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            float[] fArr = new float[6];
            Path path = new Path();
            setTransform(canvas, 1440, 1440, fArr);
            drawBox(canvas, 1440, 1440, paint, path);
            setUcsTransform(canvas, 1440);
            drawLocus(canvas, 1440, 1440, paint, path, fArr);
            drawGamuts(canvas, 1440, 1440, paint, path, fArr, new float[2]);
            drawPoints(canvas, 1440, 1440, paint);
            return bitmapCreateBitmap;
        }

        private void drawPoints(Canvas canvas, int i, int i2, Paint paint) {
            paint.setStyle(Paint.Style.FILL);
            float f = 4.0f / (this.mUcs ? UCS_SCALE : 1.0f);
            float[] fArr = new float[3];
            float[] fArr2 = new float[2];
            for (Point point : this.mPoints) {
                fArr[0] = point.mRgb[0];
                fArr[1] = point.mRgb[1];
                fArr[2] = point.mRgb[2];
                point.mColorSpace.toXyz(fArr);
                paint.setColor(point.mColor);
                float f2 = fArr[0] + fArr[1] + fArr[2];
                fArr2[0] = fArr[0] / f2;
                fArr2[1] = fArr[1] / f2;
                if (this.mUcs) {
                    ColorSpace.xyYToUv(fArr2);
                }
                float f3 = i2;
                canvas.drawCircle(i * fArr2[0], f3 - (fArr2[1] * f3), f, paint);
            }
        }

        private void drawGamuts(Canvas canvas, int i, int i2, Paint paint, Path path, float[] fArr, float[] fArr2) {
            float f = 4.0f / (this.mUcs ? UCS_SCALE : 1.0f);
            for (Pair<ColorSpace, Integer> pair : this.mColorSpaces) {
                ColorSpace colorSpace = pair.first;
                int iIntValue = pair.second.intValue();
                if (colorSpace.getModel() == Model.RGB) {
                    Rgb rgb = (Rgb) colorSpace;
                    getPrimaries(rgb, fArr, this.mUcs);
                    path.rewind();
                    float f2 = i;
                    float f3 = i2;
                    path.moveTo(fArr[0] * f2, f3 - (fArr[1] * f3));
                    path.lineTo(fArr[2] * f2, f3 - (fArr[3] * f3));
                    path.lineTo(fArr[4] * f2, f3 - (fArr[5] * f3));
                    path.close();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(iIntValue);
                    canvas.drawPath(path, paint);
                    if (this.mShowWhitePoint) {
                        rgb.getWhitePoint(fArr2);
                        if (this.mUcs) {
                            ColorSpace.xyYToUv(fArr2);
                        }
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(iIntValue);
                        canvas.drawCircle(f2 * fArr2[0], f3 - (fArr2[1] * f3), f, paint);
                    }
                }
            }
        }

        private static void getPrimaries(Rgb rgb, float[] fArr, boolean z) {
            if (rgb.equals(ColorSpace.get(Named.EXTENDED_SRGB)) || rgb.equals(ColorSpace.get(Named.LINEAR_EXTENDED_SRGB))) {
                fArr[0] = 1.41f;
                fArr[1] = 0.33f;
                fArr[2] = 0.27f;
                fArr[3] = 1.24f;
                fArr[4] = -0.23f;
                fArr[5] = -0.57f;
            } else {
                rgb.getPrimaries(fArr);
            }
            if (z) {
                ColorSpace.xyYToUv(fArr);
            }
        }

        private void drawLocus(Canvas canvas, int i, int i2, Paint paint, Path path, float[] fArr) {
            float[] fArr2;
            Path path2;
            int length = SPECTRUM_LOCUS_X.length * 32 * 6 * 2;
            float[] fArr3 = new float[length];
            int[] iArr = new int[length];
            computeChromaticityMesh(fArr3, iArr);
            if (this.mUcs) {
                ColorSpace.xyYToUv(fArr3);
            }
            for (int i3 = 0; i3 < length; i3 += 2) {
                fArr3[i3] = fArr3[i3] * i;
                int i4 = i3 + 1;
                float f = i2;
                fArr3[i4] = f - (fArr3[i4] * f);
            }
            if (this.mClip && this.mColorSpaces.size() > 0) {
                Iterator<Pair<ColorSpace, Integer>> it = this.mColorSpaces.iterator();
                while (it.hasNext()) {
                    ColorSpace colorSpace = it.next().first;
                    if (colorSpace.getModel() == Model.RGB) {
                        getPrimaries((Rgb) colorSpace, fArr, this.mUcs);
                        break;
                    }
                }
                path.rewind();
                float f2 = i;
                float f3 = i2;
                path.moveTo(fArr[0] * f2, f3 - (fArr[1] * f3));
                path.lineTo(fArr[2] * f2, f3 - (fArr[3] * f3));
                path.lineTo(f2 * fArr[4], f3 - (fArr[5] * f3));
                path.close();
                int[] iArr2 = new int[length];
                Arrays.fill(iArr2, -9671572);
                fArr2 = fArr3;
                path2 = path;
                canvas.drawVertices(Canvas.VertexMode.TRIANGLES, length, fArr3, 0, null, 0, iArr2, 0, null, 0, 0, paint);
                canvas.save();
                canvas.clipPath(path2);
                canvas.drawVertices(Canvas.VertexMode.TRIANGLES, length, fArr2, 0, null, 0, iArr, 0, null, 0, 0, paint);
                canvas.restore();
            } else {
                fArr2 = fArr3;
                path2 = path;
                canvas.drawVertices(Canvas.VertexMode.TRIANGLES, length, fArr2, 0, null, 0, iArr, 0, null, 0, 0, paint);
            }
            int i5 = MetricsProto.MetricsEvent.ACTION_SETTINGS_CONDITION_DISMISS;
            path.reset();
            path2.moveTo(fArr2[372], fArr2[373]);
            for (int i6 = 2; i6 < SPECTRUM_LOCUS_X.length; i6++) {
                i5 += MetricsProto.MetricsEvent.ACTION_SHOW_SETTINGS_SUGGESTION;
                path2.lineTo(fArr2[i5], fArr2[i5 + 1]);
            }
            path.close();
            paint.setStrokeWidth(4.0f / (this.mUcs ? UCS_SCALE : 1.0f));
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(-16777216);
            canvas.drawPath(path2, paint);
        }

        private void drawBox(Canvas canvas, int i, int i2, Paint paint, Path path) {
            int i3;
            float f;
            int i4;
            if (this.mUcs) {
                i3 = 7;
                f = UCS_SCALE;
            } else {
                i3 = 10;
                f = 1.0f;
            }
            int i5 = i3;
            float f2 = f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);
            paint.setColor(-4144960);
            int i6 = 1;
            while (true) {
                i4 = i5 - 1;
                if (i6 >= i4) {
                    break;
                }
                float f3 = i6 / 10.0f;
                float f4 = i;
                float f5 = f4 * f3 * f2;
                float f6 = i2;
                float f7 = f6 - ((f3 * f6) * f2);
                canvas.drawLine(0.0f, f7, f4 * 0.9f, f7, paint);
                canvas.drawLine(f5, f6, f5, f6 * 0.1f, paint);
                i6++;
            }
            paint.setStrokeWidth(4.0f);
            paint.setColor(-16777216);
            int i7 = 1;
            while (i7 < i4) {
                float f8 = i7 / 10.0f;
                float f9 = i;
                float f10 = f9 * f8 * f2;
                float f11 = i2;
                float f12 = f11 - ((f8 * f11) * f2);
                canvas.drawLine(0.0f, f12, f9 / 100.0f, f12, paint);
                canvas.drawLine(f10, f11, f10, f11 - (f11 / 100.0f), paint);
                i7++;
                i4 = i4;
            }
            int i8 = i4;
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(36.0f);
            int i9 = 0;
            paint.setTypeface(Typeface.create("sans-serif-light", 0));
            Rect rect = new Rect();
            int i10 = 1;
            while (i10 < i8) {
                String str = "0." + i10;
                paint.getTextBounds(str, i9, str.length(), rect);
                float f13 = i10 / 10.0f;
                float f14 = i;
                float f15 = i2;
                canvas.drawText(str, (f14 * (-0.05f)) + 10.0f, (f15 - ((f13 * f15) * f2)) + (rect.height() / 2.0f), paint);
                canvas.drawText(str, ((f14 * f13) * f2) - (rect.width() / 2.0f), rect.height() + i2 + 16, paint);
                i10++;
                i9 = 0;
            }
            paint.setStyle(Paint.Style.STROKE);
            float f16 = i2;
            path.moveTo(0.0f, f16);
            float f17 = i * 0.9f;
            path.lineTo(f17, f16);
            float f18 = f16 * 0.1f;
            path.lineTo(f17, f18);
            path.lineTo(0.0f, f18);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void setTransform(Canvas canvas, int i, int i2, float[] fArr) {
            RectF rectF = new RectF();
            Iterator<Pair<ColorSpace, Integer>> it = this.mColorSpaces.iterator();
            while (it.hasNext()) {
                ColorSpace colorSpace = it.next().first;
                if (colorSpace.getModel() == Model.RGB) {
                    getPrimaries((Rgb) colorSpace, fArr, this.mUcs);
                    rectF.left = Math.min(rectF.left, fArr[4]);
                    rectF.top = Math.min(rectF.top, fArr[5]);
                    rectF.right = Math.max(rectF.right, fArr[0]);
                    rectF.bottom = Math.max(rectF.bottom, fArr[3]);
                }
            }
            float f = this.mUcs ? 0.6f : 0.9f;
            rectF.left = Math.min(0.0f, rectF.left);
            rectF.top = Math.min(0.0f, rectF.top);
            rectF.right = Math.max(f, rectF.right);
            rectF.bottom = Math.max(f, rectF.bottom);
            float fMin = Math.min(f / rectF.width(), f / rectF.height());
            int i3 = this.mSize;
            canvas.scale(i3 / 1440.0f, i3 / 1440.0f);
            canvas.scale(fMin, fMin);
            float f2 = i;
            float f3 = i2;
            canvas.translate(((rectF.width() - f) * f2) / 2.0f, ((rectF.height() - f) * f3) / 2.0f);
            canvas.translate(f2 * 0.05f, f3 * (-0.05f));
        }

        private void setUcsTransform(Canvas canvas, int i) {
            if (this.mUcs) {
                float f = i;
                canvas.translate(0.0f, f - (f * UCS_SCALE));
                canvas.scale(UCS_SCALE, UCS_SCALE);
            }
        }

        private static void computeChromaticityMesh(float[] fArr, int[] iArr) {
            ColorSpace colorSpace = ColorSpace.get(Named.SRGB);
            float[] fArr2 = new float[3];
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                float[] fArr3 = SPECTRUM_LOCUS_X;
                if (i >= fArr3.length) {
                    return;
                }
                int length = (i % (fArr3.length - 1)) + 1;
                float fAtan2 = (float) Math.atan2(((double) SPECTRUM_LOCUS_Y[i]) - ONE_THIRD, ((double) fArr3[i]) - ONE_THIRD);
                float fAtan3 = (float) Math.atan2(((double) SPECTRUM_LOCUS_Y[length]) - ONE_THIRD, ((double) SPECTRUM_LOCUS_X[length]) - ONE_THIRD);
                float fPow = (float) Math.pow(sqr(((double) SPECTRUM_LOCUS_X[i]) - ONE_THIRD) + sqr(((double) SPECTRUM_LOCUS_Y[i]) - ONE_THIRD), 0.5d);
                float f = fAtan3;
                float fPow2 = (float) Math.pow(sqr(((double) SPECTRUM_LOCUS_X[length]) - ONE_THIRD) + sqr(((double) SPECTRUM_LOCUS_Y[length]) - ONE_THIRD), 0.5d);
                int i4 = 1;
                while (i4 <= 32) {
                    double d = fPow;
                    double d2 = fAtan2;
                    double dCos = Math.cos(d2) * d;
                    double dSin = d * Math.sin(d2);
                    double d3 = fPow2;
                    float f2 = fAtan2;
                    float f3 = f;
                    float f4 = fPow2;
                    double d4 = f3;
                    double dCos2 = Math.cos(d4) * d3;
                    double dSin2 = d3 * Math.sin(d4);
                    double d5 = i4 / 32.0f;
                    float f5 = fPow;
                    float f6 = (float) ((dCos * d5) + ONE_THIRD);
                    int i5 = i;
                    int i6 = i2;
                    float f7 = (float) ((dSin * d5) + ONE_THIRD);
                    double d6 = (i4 - 1) / 32.0f;
                    ColorSpace colorSpace2 = colorSpace;
                    float[] fArr4 = fArr2;
                    float f8 = (float) ((dCos * d6) + ONE_THIRD);
                    float f9 = (float) ((dSin * d6) + ONE_THIRD);
                    float f10 = (float) ((dCos2 * d6) + ONE_THIRD);
                    float f11 = (float) ((d6 * dSin2) + ONE_THIRD);
                    float f12 = (float) ((dCos2 * d5) + ONE_THIRD);
                    float f13 = (float) ((dSin2 * d5) + ONE_THIRD);
                    iArr[i3] = computeColor(fArr4, f6, f7, (1.0f - f6) - f7, colorSpace2);
                    iArr[i3 + 1] = computeColor(fArr4, f8, f9, (1.0f - f8) - f9, colorSpace2);
                    int i7 = i3 + 2;
                    iArr[i7] = computeColor(fArr4, f10, f11, (1.0f - f10) - f11, colorSpace2);
                    iArr[i3 + 3] = iArr[i3];
                    iArr[i3 + 4] = iArr[i7];
                    iArr[i3 + 5] = computeColor(fArr4, f12, f13, (1.0f - f12) - f13, colorSpace2);
                    i3 += 6;
                    int i8 = i6 + 1;
                    fArr[i6] = f6;
                    int i9 = i8 + 1;
                    fArr[i8] = f7;
                    int i10 = i9 + 1;
                    fArr[i9] = f8;
                    int i11 = i10 + 1;
                    fArr[i10] = f9;
                    int i12 = i11 + 1;
                    fArr[i11] = f10;
                    int i13 = i12 + 1;
                    fArr[i12] = f11;
                    int i14 = i13 + 1;
                    fArr[i13] = f6;
                    int i15 = i14 + 1;
                    fArr[i14] = f7;
                    int i16 = i15 + 1;
                    fArr[i15] = f10;
                    int i17 = i16 + 1;
                    fArr[i16] = f11;
                    int i18 = i17 + 1;
                    fArr[i17] = f12;
                    i2 = i18 + 1;
                    fArr[i18] = f13;
                    i4++;
                    i = i5;
                    colorSpace = colorSpace2;
                    fArr2 = fArr4;
                    fAtan2 = f2;
                    fPow2 = f4;
                    f = f3;
                    fPow = f5;
                }
                i++;
            }
        }

        private static int computeColor(float[] fArr, float f, float f2, float f3, ColorSpace colorSpace) {
            fArr[0] = f;
            fArr[1] = f2;
            fArr[2] = f3;
            colorSpace.fromXyz(fArr);
            return (((int) (fArr[2] * 255.0f)) & 255) | ((((int) (fArr[1] * 255.0f)) & 255) << 8) | ((((int) (fArr[0] * 255.0f)) & 255) << 16) | (-16777216);
        }

        private static class Point {
            final int mColor;
            final ColorSpace mColorSpace;
            final float[] mRgb;

            Point(ColorSpace colorSpace, float[] fArr, int i) {
                this.mColorSpace = colorSpace;
                this.mRgb = fArr;
                this.mColor = i;
            }
        }
    }
}
