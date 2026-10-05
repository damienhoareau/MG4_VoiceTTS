package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TextLine {
    private static final boolean DEBUG = false;
    private static final int TAB_INCREMENT = 20;
    private static final TextLine[] sCached = new TextLine[3];
    private float mAddedWidth;
    private char[] mChars;
    private boolean mCharsValid;
    private PrecomputedText mComputed;
    private int mDir;
    private Layout.Directions mDirections;
    private boolean mHasTabs;
    private int mLen;
    private TextPaint mPaint;
    private Spanned mSpanned;
    private int mStart;
    private Layout.TabStops mTabs;
    private CharSequence mText;
    private final TextPaint mWorkPaint = new TextPaint();
    private final TextPaint mActivePaint = new TextPaint();
    private final SpanSet<MetricAffectingSpan> mMetricAffectingSpanSpanSet = new SpanSet<>(MetricAffectingSpan.class);
    private final SpanSet<CharacterStyle> mCharacterStyleSpanSet = new SpanSet<>(CharacterStyle.class);
    private final SpanSet<ReplacementSpan> mReplacementSpanSpanSet = new SpanSet<>(ReplacementSpan.class);
    private final DecorationInfo mDecorationInfo = new DecorationInfo();
    private final ArrayList<DecorationInfo> mDecorations = new ArrayList<>();

    public static boolean isLineEndSpace(char c) {
        return c == ' ' || c == '\t' || c == 5760 || (8192 <= c && c <= 8202 && c != 8199) || c == 8287 || c == 12288;
    }

    private boolean isStretchableWhitespace(int i) {
        return i == 32;
    }

    public static TextLine obtain() {
        synchronized (sCached) {
            int length = sCached.length;
            do {
                length--;
                if (length < 0) {
                    return new TextLine();
                }
            } while (sCached[length] == null);
            TextLine textLine = sCached[length];
            sCached[length] = null;
            return textLine;
        }
    }

    public static TextLine recycle(TextLine textLine) {
        textLine.mText = null;
        textLine.mPaint = null;
        textLine.mDirections = null;
        textLine.mSpanned = null;
        textLine.mTabs = null;
        textLine.mChars = null;
        textLine.mComputed = null;
        textLine.mMetricAffectingSpanSpanSet.recycle();
        textLine.mCharacterStyleSpanSet.recycle();
        textLine.mReplacementSpanSpanSet.recycle();
        synchronized (sCached) {
            for (int i = 0; i < sCached.length; i++) {
                if (sCached[i] == null) {
                    sCached[i] = textLine;
                    break;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:9:0x002d  */
    public void set(TextPaint textPaint, CharSequence charSequence, int i, int i2, int i3, Layout.Directions directions, boolean z, Layout.TabStops tabStops) {
        boolean z2;
        this.mPaint = textPaint;
        this.mText = charSequence;
        this.mStart = i;
        this.mLen = i2 - i;
        this.mDir = i3;
        this.mDirections = directions;
        if (directions == null) {
            throw new IllegalArgumentException("Directions cannot be null");
        }
        this.mHasTabs = z;
        this.mSpanned = null;
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            this.mSpanned = spanned;
            this.mReplacementSpanSpanSet.init(spanned, i, i2);
            if (this.mReplacementSpanSpanSet.numberOfSpans > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
        } else {
            z2 = false;
        }
        this.mComputed = null;
        if (charSequence instanceof PrecomputedText) {
            PrecomputedText precomputedText = (PrecomputedText) charSequence;
            this.mComputed = precomputedText;
            if (!precomputedText.getParams().getTextPaint().equalsForTextMeasurement(textPaint)) {
                this.mComputed = null;
            }
        }
        boolean z3 = z2 || z || directions != Layout.DIRS_ALL_LEFT_TO_RIGHT;
        this.mCharsValid = z3;
        if (z3) {
            char[] cArr = this.mChars;
            if (cArr == null || cArr.length < this.mLen) {
                this.mChars = ArrayUtils.newUnpaddedCharArray(this.mLen);
            }
            TextUtils.getChars(charSequence, i, i2, this.mChars, 0);
            if (z2) {
                char[] cArr2 = this.mChars;
                int i4 = i;
                while (i4 < i2) {
                    int nextTransition = this.mReplacementSpanSpanSet.getNextTransition(i4, i2);
                    if (this.mReplacementSpanSpanSet.hasSpansIntersecting(i4, nextTransition)) {
                        int i5 = i4 - i;
                        cArr2[i5] = 65532;
                        int i6 = nextTransition - i;
                        for (int i7 = i5 + 1; i7 < i6; i7++) {
                            cArr2[i7] = 65279;
                        }
                    }
                    i4 = nextTransition;
                }
            }
        }
        this.mTabs = tabStops;
        this.mAddedWidth = 0.0f;
    }

    public void justify(float f) {
        int i = this.mLen;
        while (i > 0 && isLineEndSpace(this.mText.charAt((this.mStart + i) - 1))) {
            i--;
        }
        int iCountStretchableSpaces = countStretchableSpaces(0, i);
        if (iCountStretchableSpaces == 0) {
            return;
        }
        this.mAddedWidth = (f - Math.abs(measure(i, false, null))) / iCountStretchableSpaces;
    }

    void draw(Canvas canvas, float f, int i, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int iCodePointAt;
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                drawRun(canvas, 0, this.mLen, false, f, i, i2, i3, false);
                return;
            } else if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                drawRun(canvas, 0, this.mLen, true, f, i, i2, i3, false);
                return;
            }
        }
        float f2 = 0.0f;
        int[] iArr = this.mDirections.mDirections;
        int length = iArr.length - 2;
        int i7 = 0;
        while (i7 < iArr.length) {
            int i8 = iArr[i7];
            int i9 = i7 + 1;
            int i10 = (iArr[i9] & 67108863) + i8;
            int i11 = this.mLen;
            int i12 = i10 > i11 ? i11 : i10;
            boolean z = (iArr[i9] & 67108864) != 0;
            float fDrawRun = f2;
            int i13 = this.mHasTabs ? i8 : i12;
            int i14 = i8;
            while (i13 <= i12) {
                if (!this.mHasTabs || i13 >= i12) {
                    i4 = 0;
                } else {
                    char[] cArr = this.mChars;
                    char c = cArr[i13];
                    if (c < 55296 || c >= 56320 || (i6 = i13 + 1) >= i12 || (iCodePointAt = Character.codePointAt(cArr, i13)) <= 65535) {
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = iCodePointAt;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i4 = i5;
                    } else {
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = iCodePointAt;
                        i13 = i6;
                    }
                    i13++;
                }
                if (i13 == i12 || i4 == 9) {
                    int i15 = i4;
                    int i16 = i13;
                    fDrawRun += drawRun(canvas, i14, i13, z, f + fDrawRun, i, i2, i3, (i7 == length && i13 == this.mLen) ? false : true);
                    if (i15 == 9) {
                        int i17 = this.mDir;
                        fDrawRun = i17 * nextTab(i17 * fDrawRun);
                    }
                    i14 = i16 + 1;
                    i13 = i16;
                }
                i13++;
            }
            i7 += 2;
            f2 = fDrawRun;
        }
    }

    public float metrics(Paint.FontMetricsInt fontMetricsInt) {
        return measure(this.mLen, false, fontMetricsInt);
    }

    float measure(int i, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        int i2;
        float fMeasureRun;
        int i3;
        int i4;
        int iCodePointAt;
        int i5 = z ? i - 1 : i;
        float f = 0.0f;
        if (i5 < 0) {
            return 0.0f;
        }
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return measureRun(0, i, this.mLen, false, fontMetricsInt);
            }
            if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                return measureRun(0, i, this.mLen, true, fontMetricsInt);
            }
        }
        char[] cArr = this.mChars;
        int[] iArr = this.mDirections.mDirections;
        int i6 = 0;
        while (i6 < iArr.length) {
            int i7 = iArr[i6];
            int i8 = i6 + 1;
            int i9 = (iArr[i8] & 67108863) + i7;
            int i10 = this.mLen;
            int i11 = i9 > i10 ? i10 : i9;
            boolean z2 = (iArr[i8] & 67108864) != 0;
            float f2 = f;
            int i12 = i7;
            int i13 = this.mHasTabs ? i7 : i11;
            while (i13 <= i11) {
                if (!this.mHasTabs || i13 >= i11) {
                    i2 = 0;
                } else {
                    char c = cArr[i13];
                    if (c < 55296 || c >= 56320 || (i4 = i13 + 1) >= i11 || (iCodePointAt = Character.codePointAt(cArr, i13)) <= 65535) {
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i3 = iCodePointAt;
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i2 = i3;
                    } else {
                        i3 = c;
                        i3 = c;
                        i3 = c;
                        i3 = iCodePointAt;
                        i13 = i4;
                    }
                    i13++;
                }
                if (i13 == i11 || i2 == 9) {
                    boolean z3 = i5 >= i12 && i5 < i13;
                    boolean z4 = (this.mDir == -1) == z2;
                    if (z3 && z4) {
                        fMeasureRun = measureRun(i12, i, i13, z2, fontMetricsInt);
                    } else {
                        int i14 = i2;
                        int i15 = i13;
                        int i16 = i12;
                        float fMeasureRun2 = measureRun(i12, i13, i13, z2, fontMetricsInt);
                        if (!z4) {
                            fMeasureRun2 = -fMeasureRun2;
                        }
                        f2 += fMeasureRun2;
                        if (z3) {
                            fMeasureRun = measureRun(i16, i, i15, z2, null);
                        } else {
                            if (i14 == 9) {
                                if (i == i15) {
                                    return f2;
                                }
                                int i17 = this.mDir;
                                float fNextTab = i17 * nextTab(i17 * f2);
                                if (i5 == i15) {
                                    return fNextTab;
                                }
                                f2 = fNextTab;
                            }
                            i12 = i15 + 1;
                            i13 = i15;
                        }
                    }
                    return f2 + fMeasureRun;
                }
                i13++;
            }
            i6 += 2;
            f = f2;
        }
        return f;
    }

    float[] measureAllOffsets(boolean[] zArr, Paint.FontMetricsInt fontMetricsInt) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int iCodePointAt;
        int i6 = this.mLen;
        float[] fArr = new float[i6 + 1];
        int i7 = 1;
        int i8 = i6 + 1;
        int[] iArr = new int[i8];
        int i9 = 0;
        for (int i10 = 0; i10 < i8; i10++) {
            iArr[i10] = zArr[i10] ? i10 - 1 : i10;
        }
        float f = 0.0f;
        if (iArr[0] < 0) {
            fArr[0] = 0.0f;
        }
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                while (true) {
                    int i11 = this.mLen;
                    if (i9 > i11) {
                        return fArr;
                    }
                    fArr[i9] = measureRun(0, i9, i11, false, fontMetricsInt);
                    i9++;
                }
            } else if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                while (true) {
                    int i12 = this.mLen;
                    if (i9 > i12) {
                        return fArr;
                    }
                    fArr[i9] = measureRun(0, i9, i12, true, fontMetricsInt);
                    i9++;
                }
            }
        }
        char[] cArr = this.mChars;
        int[] iArr2 = this.mDirections.mDirections;
        int i13 = 0;
        while (i13 < iArr2.length) {
            int i14 = iArr2[i13];
            int i15 = i13 + 1;
            int i16 = (iArr2[i15] & 67108863) + i14;
            int i17 = this.mLen;
            int i18 = i16 > i17 ? i17 : i16;
            int i19 = (iArr2[i15] & 67108864) != 0 ? i7 : i9;
            int i20 = i14;
            float f2 = f;
            int i21 = this.mHasTabs ? i14 : i18;
            while (i21 <= i18) {
                if (!this.mHasTabs || i21 >= i18) {
                    i = i9;
                } else {
                    char c = cArr[i21];
                    if (c < 55296 || c >= 56320 || (i5 = i21 + 1) >= i18 || (iCodePointAt = Character.codePointAt(cArr, i21)) <= 65535) {
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i4 = iCodePointAt;
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i = i4;
                    } else {
                        i4 = c;
                        i4 = c;
                        i4 = c;
                        i4 = iCodePointAt;
                        i21 = i5;
                    }
                    i2 = i7;
                    i21 += i2;
                    i7 = i2;
                    cArr = cArr;
                    i9 = 0;
                }
                if (i21 == i18 || i == 9) {
                    int i22 = (this.mDir == -1 ? i7 : i9) == i19 ? i7 : i9;
                    int i23 = i;
                    int i24 = i21;
                    int i25 = i20;
                    float fMeasureRun = measureRun(i20, i21, i21, i19, fontMetricsInt);
                    if (i22 == 0) {
                        fMeasureRun = -fMeasureRun;
                    }
                    float f3 = f2 + fMeasureRun;
                    if (i22 == 0) {
                        f2 = f3;
                    }
                    Paint.FontMetricsInt fontMetricsInt2 = i22 != 0 ? fontMetricsInt : null;
                    int i26 = i25;
                    while (i26 <= i24 && i26 <= this.mLen) {
                        if (iArr[i26] < i25 || iArr[i26] >= i24) {
                            i3 = i26;
                        } else {
                            i3 = i26;
                            fArr[i3] = f2 + measureRun(i25, i26, i24, i19, fontMetricsInt2);
                        }
                        i26 = i3 + 1;
                    }
                    if (i23 == 9) {
                        if (iArr[i24] == i24) {
                            fArr[i24] = f3;
                        }
                        int i27 = this.mDir;
                        float fNextTab = i27 * nextTab(i27 * f3);
                        int i28 = i24 + 1;
                        if (iArr[i28] == i24) {
                            fArr[i28] = fNextTab;
                        }
                        f2 = fNextTab;
                    } else {
                        f2 = f3;
                    }
                    i20 = i24 + 1;
                    i21 = i24;
                    i2 = 1;
                } else {
                    i2 = i7;
                }
                i21 += i2;
                i7 = i2;
                cArr = cArr;
                i9 = 0;
            }
            i13 += 2;
            f = f2;
            i9 = 0;
        }
        int i29 = this.mLen;
        if (iArr[i29] == i29) {
            fArr[i29] = f;
        }
        return fArr;
    }

    private float drawRun(Canvas canvas, int i, int i2, boolean z, float f, int i3, int i4, int i5, boolean z2) {
        if ((this.mDir == 1) == z) {
            float f2 = -measureRun(i, i2, i2, z, null);
            handleRun(i, i2, i2, z, canvas, f + f2, i3, i4, i5, null, false);
            return f2;
        }
        return handleRun(i, i2, i2, z, canvas, f, i3, i4, i5, null, z2);
    }

    private float measureRun(int i, int i2, int i3, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        return handleRun(i, i2, i3, z, null, 0.0f, 0, 0, 0, fontMetricsInt, true);
    }

    /* JADX WARN: Code duplicated, block: B:61:0x00bd  */
    int getOffsetToLeftRightOf(int i, boolean z) {
        int i2;
        int i3;
        int i4;
        boolean z2;
        int i5;
        int length;
        int i6;
        boolean z3;
        int offsetBeforeAfter;
        boolean z4;
        int i7;
        int i8 = this.mLen;
        int i9 = -1;
        boolean z5 = this.mDir == -1;
        int[] iArr = this.mDirections.mDirections;
        if (i == 0) {
            i5 = 0;
            length = -2;
        } else if (i == i8) {
            length = iArr.length;
            i5 = 0;
        } else {
            int i10 = i8;
            int i11 = 0;
            int i12 = 0;
            while (true) {
                if (i11 >= iArr.length) {
                    i2 = i11;
                    i3 = i12;
                    i4 = i10;
                    z2 = false;
                    i5 = 0;
                    break;
                }
                i12 = iArr[i11] + 0;
                if (i >= i12) {
                    int i13 = i11 + 1;
                    int i14 = (iArr[i13] & 67108863) + i12;
                    if (i14 > i8) {
                        i14 = i8;
                    }
                    if (i < i14) {
                        int i15 = (iArr[i13] >>> 26) & 63;
                        if (i != i12) {
                            i2 = i11;
                            i3 = i12;
                            i5 = i15;
                            i4 = i14;
                            z2 = false;
                            break;
                        }
                        int i16 = i - 1;
                        int i17 = 0;
                        while (true) {
                            if (i17 >= iArr.length) {
                                z2 = false;
                                break;
                            }
                            int i18 = iArr[i17] + 0;
                            if (i16 >= i18) {
                                int i19 = i17 + 1;
                                int i20 = i18 + (iArr[i19] & 67108863);
                                if (i20 > i8) {
                                    i20 = i8;
                                }
                                if (i16 < i20 && (i6 = (iArr[i19] >>> 26) & 63) < i15) {
                                    i11 = i17;
                                    i15 = i6;
                                    i14 = i20;
                                    i12 = i18;
                                    z2 = true;
                                    break;
                                }
                            }
                            i17 += 2;
                        }
                        i2 = i11;
                        i3 = i12;
                        i5 = i15;
                        i4 = i14;
                        break;
                    }
                    i10 = i14;
                }
                i11 += 2;
            }
            if (i2 == iArr.length) {
                length = i2;
                i9 = -1;
            } else {
                boolean z6 = (i5 & 1) != 0;
                boolean z7 = z == z6;
                if (i == (z7 ? i4 : i3) && z7 == z2) {
                    length = i2;
                    i9 = -1;
                } else {
                    boolean z8 = z7;
                    int offsetBeforeAfter2 = getOffsetBeforeAfter(i2, i3, i4, z6, i, z8);
                    if (z8) {
                        i3 = i4;
                    }
                    if (offsetBeforeAfter2 != i3) {
                        return offsetBeforeAfter2;
                    }
                    int i21 = i2;
                    i9 = offsetBeforeAfter2;
                    length = i21;
                }
            }
        }
        while (true) {
            z3 = z == z5;
            int i22 = length + (z3 ? 2 : -2);
            if (i22 < 0 || i22 >= iArr.length) {
                break;
            }
            int i23 = 0 + iArr[i22];
            int i24 = i22 + 1;
            int i25 = i23 + (iArr[i24] & 67108863);
            int i26 = i25 > i8 ? i8 : i25;
            int i27 = (iArr[i24] >>> 26) & 63;
            boolean z9 = (i27 & 1) != 0;
            if (z == z9) {
                i7 = -1;
                z4 = true;
            } else {
                z4 = false;
                i7 = -1;
            }
            if (i9 != i7) {
                if (i27 < i5) {
                    return z4 ? i23 : i26;
                }
                return i9;
            }
            offsetBeforeAfter = getOffsetBeforeAfter(i22, i23, i26, z9, z4 ? i23 : i26, z4);
            if (offsetBeforeAfter == (z4 ? i26 : i23)) {
                i5 = i27;
                i9 = offsetBeforeAfter;
                length = i22;
            }
            return offsetBeforeAfter;
        }
        offsetBeforeAfter = -1;
        if (i9 == -1) {
            if (z3) {
                return this.mLen + 1;
            }
            return offsetBeforeAfter;
        }
        if (i9 > i8) {
            return i9;
        }
        if (!z3) {
            i8 = 0;
        }
        return i8;
    }

    private int getOffsetBeforeAfter(int i, int i2, int i3, boolean z, int i4, boolean z2) {
        int i5;
        if (i >= 0) {
            if (i4 != (z2 ? this.mLen : 0)) {
                TextPaint textPaint = this.mWorkPaint;
                textPaint.set(this.mPaint);
                textPaint.setWordSpacing(this.mAddedWidth);
                if (this.mSpanned != null) {
                    int i6 = z2 ? i4 + 1 : i4;
                    int i7 = this.mStart + i3;
                    while (true) {
                        int iNextSpanTransition = this.mSpanned.nextSpanTransition(this.mStart + i2, i7, MetricAffectingSpan.class);
                        i5 = this.mStart;
                        i3 = iNextSpanTransition - i5;
                        if (i3 >= i6) {
                            break;
                        }
                        i2 = i3;
                    }
                    MetricAffectingSpan[] metricAffectingSpanArr = (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) this.mSpanned.getSpans(i5 + i2, i5 + i3, MetricAffectingSpan.class), this.mSpanned, MetricAffectingSpan.class);
                    if (metricAffectingSpanArr.length > 0) {
                        ReplacementSpan replacementSpan = null;
                        for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
                            if (metricAffectingSpan instanceof ReplacementSpan) {
                                replacementSpan = (ReplacementSpan) metricAffectingSpan;
                            } else {
                                metricAffectingSpan.updateMeasureState(textPaint);
                            }
                        }
                        if (replacementSpan != null) {
                            return z2 ? i3 : i2;
                        }
                    }
                }
                int i8 = i2;
                int i9 = z2 ? 0 : 2;
                if (this.mCharsValid) {
                    return textPaint.getTextRunCursor(this.mChars, i8, i3 - i8, z ? 1 : 0, i4, i9);
                }
                CharSequence charSequence = this.mText;
                int i10 = this.mStart;
                return textPaint.getTextRunCursor(charSequence, i8 + i10, i10 + i3, z ? 1 : 0, i10 + i4, i9) - this.mStart;
            }
        }
        if (z2) {
            return TextUtils.getOffsetAfter(this.mText, i4 + this.mStart) - this.mStart;
        }
        return TextUtils.getOffsetBefore(this.mText, i4 + this.mStart) - this.mStart;
    }

    private static void expandMetricsFromPaint(Paint.FontMetricsInt fontMetricsInt, TextPaint textPaint) {
        int i = fontMetricsInt.top;
        int i2 = fontMetricsInt.ascent;
        int i3 = fontMetricsInt.descent;
        int i4 = fontMetricsInt.bottom;
        int i5 = fontMetricsInt.leading;
        textPaint.getFontMetricsInt(fontMetricsInt);
        updateMetrics(fontMetricsInt, i, i2, i3, i4, i5);
    }

    static void updateMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2, int i3, int i4, int i5) {
        fontMetricsInt.top = Math.min(fontMetricsInt.top, i);
        fontMetricsInt.ascent = Math.min(fontMetricsInt.ascent, i2);
        fontMetricsInt.descent = Math.max(fontMetricsInt.descent, i3);
        fontMetricsInt.bottom = Math.max(fontMetricsInt.bottom, i4);
        fontMetricsInt.leading = Math.max(fontMetricsInt.leading, i5);
    }

    private static void drawStroke(TextPaint textPaint, Canvas canvas, int i, float f, float f2, float f3, float f4, float f5) {
        float f6 = f5 + textPaint.baselineShift + f;
        int color = textPaint.getColor();
        Paint.Style style = textPaint.getStyle();
        boolean zIsAntiAlias = textPaint.isAntiAlias();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(i);
        canvas.drawRect(f3, f6, f4, f6 + f2, textPaint);
        textPaint.setStyle(style);
        textPaint.setColor(color);
        textPaint.setAntiAlias(zIsAntiAlias);
    }

    private float getRunAdvance(TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, int i5) {
        if (this.mCharsValid) {
            return textPaint.getRunAdvance(this.mChars, i, i2, i3, i4, z, i5);
        }
        int i6 = this.mStart;
        PrecomputedText precomputedText = this.mComputed;
        if (precomputedText == null) {
            return textPaint.getRunAdvance(this.mText, i6 + i, i6 + i2, i6 + i3, i6 + i4, z, i6 + i5);
        }
        return precomputedText.getWidth(i + i6, i6 + i2);
    }

    private float handleText(TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, Canvas canvas, float f, int i5, int i6, int i7, Paint.FontMetricsInt fontMetricsInt, boolean z2, int i8, ArrayList<DecorationInfo> arrayList) {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        int i9 = i;
        ArrayList<DecorationInfo> arrayList2 = arrayList;
        textPaint.setWordSpacing(this.mAddedWidth);
        if (fontMetricsInt != null) {
            expandMetricsFromPaint(fontMetricsInt, textPaint);
        }
        float runAdvance = 0.0f;
        if (i2 == i9) {
            return 0.0f;
        }
        int size = arrayList2 == null ? 0 : arrayList.size();
        if (z2 || (canvas != null && (textPaint.bgColor != 0 || size != 0 || z))) {
            runAdvance = getRunAdvance(textPaint, i, i2, i3, i4, z, i8);
        }
        float f7 = runAdvance;
        if (canvas != null) {
            if (z) {
                f4 = f;
                f3 = f - f7;
            } else {
                f3 = f;
                f4 = f + f7;
            }
            if (textPaint.bgColor != 0) {
                int color = textPaint.getColor();
                Paint.Style style = textPaint.getStyle();
                textPaint.setColor(textPaint.bgColor);
                textPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(f3, i5, f4, i7, textPaint);
                textPaint.setStyle(style);
                textPaint.setColor(color);
            }
            if (size != 0) {
                int i10 = 0;
                while (i10 < size) {
                    DecorationInfo decorationInfo = arrayList2.get(i10);
                    int iMax = Math.max(decorationInfo.start, i9);
                    int iMin = Math.min(decorationInfo.end, i8);
                    float f8 = f7;
                    float runAdvance2 = getRunAdvance(textPaint, i, i2, i3, i4, z, iMax);
                    float runAdvance3 = getRunAdvance(textPaint, i, i2, i3, i4, z, iMin);
                    if (z) {
                        float f9 = f4 - runAdvance2;
                        f5 = f4 - runAdvance3;
                        f6 = f9;
                    } else {
                        f5 = f3 + runAdvance2;
                        f6 = f3 + runAdvance3;
                    }
                    if (decorationInfo.underlineColor != 0) {
                        drawStroke(textPaint, canvas, decorationInfo.underlineColor, textPaint.getUnderlinePosition(), decorationInfo.underlineThickness, f5, f6, i6);
                    }
                    if (decorationInfo.isUnderlineText) {
                        drawStroke(textPaint, canvas, textPaint.getColor(), textPaint.getUnderlinePosition(), Math.max(textPaint.getUnderlineThickness(), 1.0f), f5, f6, i6);
                    }
                    if (decorationInfo.isStrikeThruText) {
                        drawStroke(textPaint, canvas, textPaint.getColor(), textPaint.getStrikeThruPosition(), Math.max(textPaint.getStrikeThruThickness(), 1.0f), f5, f6, i6);
                    }
                    i10++;
                    i9 = i;
                    arrayList2 = arrayList;
                    f7 = f8;
                }
            }
            f2 = f7;
            drawTextRun(canvas, textPaint, i, i2, i3, i4, z, f3, i6 + textPaint.baselineShift);
        } else {
            f2 = f7;
        }
        return z ? -f2 : f2;
    }

    private float handleReplacement(ReplacementSpan replacementSpan, TextPaint textPaint, int i, int i2, boolean z, Canvas canvas, float f, int i3, int i4, int i5, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        float f2;
        int i11 = this.mStart;
        int i12 = i11 + i;
        int i13 = i11 + i2;
        if (z2 || (canvas != null && z)) {
            boolean z3 = fontMetricsInt != null;
            if (z3) {
                int i14 = fontMetricsInt.top;
                i6 = i14;
                i7 = fontMetricsInt.ascent;
                i8 = fontMetricsInt.descent;
                i9 = fontMetricsInt.bottom;
                i10 = fontMetricsInt.leading;
            } else {
                i6 = 0;
                i7 = 0;
                i8 = 0;
                i9 = 0;
                i10 = 0;
            }
            float size = replacementSpan.getSize(textPaint, this.mText, i12, i13, fontMetricsInt);
            if (z3) {
                updateMetrics(fontMetricsInt, i6, i7, i8, i9, i10);
            }
            f2 = size;
        } else {
            f2 = 0.0f;
        }
        if (canvas != null) {
            replacementSpan.draw(canvas, this.mText, i12, i13, z ? f - f2 : f, i3, i4, i5, textPaint);
        }
        return z ? -f2 : f2;
    }

    private int adjustHyphenEdit(int i, int i2, int i3) {
        if (i > 0) {
            i3 &= -25;
        }
        return i2 < this.mLen ? i3 & (-8) : i3;
    }

    private static final class DecorationInfo {
        public int end;
        public boolean isStrikeThruText;
        public boolean isUnderlineText;
        public int start;
        public int underlineColor;
        public float underlineThickness;

        private DecorationInfo() {
            this.start = -1;
            this.end = -1;
        }

        public boolean hasDecoration() {
            return this.isStrikeThruText || this.isUnderlineText || this.underlineColor != 0;
        }

        public DecorationInfo copyInfo() {
            DecorationInfo decorationInfo = new DecorationInfo();
            decorationInfo.isStrikeThruText = this.isStrikeThruText;
            decorationInfo.isUnderlineText = this.isUnderlineText;
            decorationInfo.underlineColor = this.underlineColor;
            decorationInfo.underlineThickness = this.underlineThickness;
            return decorationInfo;
        }
    }

    private void extractDecorationInfo(TextPaint textPaint, DecorationInfo decorationInfo) {
        decorationInfo.isStrikeThruText = textPaint.isStrikeThruText();
        if (decorationInfo.isStrikeThruText) {
            textPaint.setStrikeThruText(false);
        }
        decorationInfo.isUnderlineText = textPaint.isUnderlineText();
        if (decorationInfo.isUnderlineText) {
            textPaint.setUnderlineText(false);
        }
        decorationInfo.underlineColor = textPaint.underlineColor;
        decorationInfo.underlineThickness = textPaint.underlineThickness;
        textPaint.setUnderlineText(0, 0.0f);
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0026  */
    /* JADX WARN: Code duplicated, block: B:72:0x01e7  */
    /* JADX WARN: Code duplicated, block: B:73:0x01f9  */
    private float handleRun(int i, int i2, int i3, boolean z, Canvas canvas, float f, int i4, int i5, int i6, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        boolean z3;
        int i7;
        float fHandleText;
        int i8;
        TextPaint textPaint;
        TextPaint textPaint2;
        TextLine textLine;
        int i9;
        int i10 = i2;
        int i11 = i3;
        if (i10 < i || i10 > i11) {
            throw new IndexOutOfBoundsException("measureLimit (" + i2 + ") is out of start (" + i + ") and limit (" + i3 + ") bounds");
        }
        if (i == i10) {
            TextPaint textPaint3 = this.mWorkPaint;
            textPaint3.set(this.mPaint);
            if (fontMetricsInt == null) {
                return 0.0f;
            }
            expandMetricsFromPaint(fontMetricsInt, textPaint3);
            return 0.0f;
        }
        Spanned spanned = this.mSpanned;
        if (spanned == null) {
            z3 = false;
        } else {
            SpanSet<MetricAffectingSpan> spanSet = this.mMetricAffectingSpanSpanSet;
            int i12 = this.mStart;
            spanSet.init(spanned, i12 + i, i12 + i11);
            SpanSet<CharacterStyle> spanSet2 = this.mCharacterStyleSpanSet;
            Spanned spanned2 = this.mSpanned;
            int i13 = this.mStart;
            spanSet2.init(spanned2, i13 + i, i13 + i11);
            if (this.mMetricAffectingSpanSpanSet.numberOfSpans == 0 && this.mCharacterStyleSpanSet.numberOfSpans == 0) {
                z3 = false;
            } else {
                z3 = true;
            }
        }
        if (!z3) {
            TextPaint textPaint4 = this.mWorkPaint;
            textPaint4.set(this.mPaint);
            textPaint4.setHyphenEdit(adjustHyphenEdit(i, i11, textPaint4.getHyphenEdit()));
            return handleText(textPaint4, i, i3, i, i3, z, canvas, f, i4, i5, i6, fontMetricsInt, z2, i2, null);
        }
        float fHandleText2 = f;
        int i14 = i;
        while (i14 < i10) {
            TextPaint textPaint5 = this.mWorkPaint;
            textPaint5.set(this.mPaint);
            SpanSet<MetricAffectingSpan> spanSet3 = this.mMetricAffectingSpanSpanSet;
            int i15 = this.mStart;
            int nextTransition = spanSet3.getNextTransition(i15 + i14, i15 + i11) - this.mStart;
            int iMin = Math.min(nextTransition, i10);
            ReplacementSpan replacementSpan = null;
            for (int i16 = 0; i16 < this.mMetricAffectingSpanSpanSet.numberOfSpans; i16++) {
                if (this.mMetricAffectingSpanSpanSet.spanStarts[i16] < this.mStart + iMin && this.mMetricAffectingSpanSpanSet.spanEnds[i16] > this.mStart + i14) {
                    MetricAffectingSpan metricAffectingSpan = this.mMetricAffectingSpanSpanSet.spans[i16];
                    if (metricAffectingSpan instanceof ReplacementSpan) {
                        replacementSpan = (ReplacementSpan) metricAffectingSpan;
                    } else {
                        metricAffectingSpan.updateDrawState(textPaint5);
                    }
                }
            }
            if (replacementSpan != null) {
                i7 = nextTransition;
                fHandleText = handleReplacement(replacementSpan, textPaint5, i14, iMin, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || iMin < i10);
            } else {
                i7 = nextTransition;
                TextLine textLine2 = this;
                TextPaint textPaint6 = textLine2.mActivePaint;
                textPaint6.set(textLine2.mPaint);
                DecorationInfo decorationInfo = textLine2.mDecorationInfo;
                textLine2.mDecorations.clear();
                int i17 = iMin;
                int i18 = i14;
                int i19 = i18;
                while (i19 < iMin) {
                    SpanSet<CharacterStyle> spanSet4 = textLine2.mCharacterStyleSpanSet;
                    int i20 = textLine2.mStart;
                    int nextTransition2 = spanSet4.getNextTransition(i20 + i19, i20 + i7) - textLine2.mStart;
                    int iMin2 = Math.min(nextTransition2, iMin);
                    textPaint5.set(textLine2.mPaint);
                    for (int i21 = 0; i21 < textLine2.mCharacterStyleSpanSet.numberOfSpans; i21++) {
                        if (textLine2.mCharacterStyleSpanSet.spanStarts[i21] < textLine2.mStart + iMin2 && textLine2.mCharacterStyleSpanSet.spanEnds[i21] > textLine2.mStart + i19) {
                            textLine2.mCharacterStyleSpanSet.spans[i21].updateDrawState(textPaint5);
                        }
                    }
                    textLine2.extractDecorationInfo(textPaint5, decorationInfo);
                    if (i19 == i14) {
                        textPaint6.set(textPaint5);
                    } else {
                        if (!textPaint5.hasEqualAttributes(textPaint6)) {
                            textPaint6.setHyphenEdit(textLine2.adjustHyphenEdit(i18, i17, textLine2.mPaint.getHyphenEdit()));
                            i8 = i19;
                            fHandleText2 += handleText(textPaint6, i18, i17, i14, i7, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || i17 < i10, Math.min(i17, iMin), textLine2.mDecorations);
                            textPaint = textPaint5;
                            textPaint2 = textPaint6;
                            textPaint2.set(textPaint);
                            textLine = this;
                            textLine.mDecorations.clear();
                            i18 = i8;
                        }
                        if (decorationInfo.hasDecoration()) {
                            DecorationInfo decorationInfoCopyInfo = decorationInfo.copyInfo();
                            decorationInfoCopyInfo.start = i8;
                            i9 = nextTransition2;
                            decorationInfoCopyInfo.end = i9;
                            textLine.mDecorations.add(decorationInfoCopyInfo);
                        } else {
                            i9 = nextTransition2;
                        }
                        i10 = i2;
                        textPaint5 = textPaint;
                        textPaint6 = textPaint2;
                        i17 = i9;
                        i19 = i17;
                        textLine2 = textLine;
                        i14 = i14;
                        iMin = iMin;
                        decorationInfo = decorationInfo;
                    }
                    i8 = i19;
                    textPaint = textPaint5;
                    textPaint2 = textPaint6;
                    textLine = textLine2;
                    if (decorationInfo.hasDecoration()) {
                        DecorationInfo decorationInfoCopyInfo2 = decorationInfo.copyInfo();
                        decorationInfoCopyInfo2.start = i8;
                        i9 = nextTransition2;
                        decorationInfoCopyInfo2.end = i9;
                        textLine.mDecorations.add(decorationInfoCopyInfo2);
                    } else {
                        i9 = nextTransition2;
                    }
                    i10 = i2;
                    textPaint5 = textPaint;
                    textPaint6 = textPaint2;
                    i17 = i9;
                    i19 = i17;
                    textLine2 = textLine;
                    i14 = i14;
                    iMin = iMin;
                    decorationInfo = decorationInfo;
                }
                int i22 = iMin;
                TextPaint textPaint7 = textPaint6;
                int i23 = i14;
                TextLine textLine3 = textLine2;
                textPaint7.setHyphenEdit(textLine3.adjustHyphenEdit(i18, i17, textLine3.mPaint.getHyphenEdit()));
                fHandleText = handleText(textPaint7, i18, i17, i23, i7, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || i17 < i2, Math.min(i17, i22), textLine3.mDecorations);
            }
            fHandleText2 += fHandleText;
            i10 = i2;
            i11 = i3;
            i14 = i7;
        }
        return fHandleText2 - f;
    }

    private void drawTextRun(Canvas canvas, TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, float f, int i5) {
        if (this.mCharsValid) {
            canvas.drawTextRun(this.mChars, i, i2 - i, i3, i4 - i3, f, i5, z, textPaint);
        } else {
            int i6 = this.mStart;
            canvas.drawTextRun(this.mText, i6 + i, i6 + i2, i6 + i3, i6 + i4, f, i5, z, textPaint);
        }
    }

    float nextTab(float f) {
        Layout.TabStops tabStops = this.mTabs;
        if (tabStops != null) {
            return tabStops.nextTab(f);
        }
        return Layout.TabStops.nextDefaultStop(f, 20);
    }

    private int countStretchableSpaces(int i, int i2) {
        int i3 = 0;
        while (i < i2) {
            if (isStretchableWhitespace(this.mCharsValid ? this.mChars[i] : this.mText.charAt(this.mStart + i))) {
                i3++;
            }
            i++;
        }
        return i3;
    }
}
