package android.text;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import android.util.Pools;
import dalvik.annotation.optimization.CriticalNative;
import java.util.Arrays;
import libcore.util.NativeAllocationRegistry;

/* JADX INFO: loaded from: classes2.dex */
public class MeasuredParagraph {
    private static final char OBJECT_REPLACEMENT_CHARACTER = 65532;
    private Paint.FontMetricsInt mCachedFm;
    private char[] mCopiedBuffer;
    private boolean mLtrWithoutBidi;
    private Runnable mNativeObjectCleaner;
    private int mParaDir;
    private Spanned mSpanned;
    private int mTextLength;
    private int mTextStart;
    private float mWholeWidth;
    private static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(MeasuredParagraph.class.getClassLoader(), nGetReleaseFunc(), 1024);
    private static final Pools.SynchronizedPool<MeasuredParagraph> sPool = new Pools.SynchronizedPool<>(1);
    private AutoGrowArray.ByteArray mLevels = new AutoGrowArray.ByteArray();
    private AutoGrowArray.FloatArray mWidths = new AutoGrowArray.FloatArray();
    private AutoGrowArray.IntArray mSpanEndCache = new AutoGrowArray.IntArray(4);
    private AutoGrowArray.IntArray mFontMetrics = new AutoGrowArray.IntArray(16);
    private long mNativePtr = 0;
    private TextPaint mCachedPaint = new TextPaint();

    private static native void nAddReplacementRun(long j, long j2, int i, int i2, float f);

    private static native void nAddStyleRun(long j, long j2, int i, int i2, boolean z);

    private static native long nBuildNativeMeasuredParagraph(long j, char[] cArr, boolean z, boolean z2);

    private static native void nFreeBuilder(long j);

    private static native void nGetBounds(long j, char[] cArr, int i, int i2, Rect rect);

    @CriticalNative
    private static native int nGetMemoryUsage(long j);

    @CriticalNative
    private static native long nGetReleaseFunc();

    @CriticalNative
    private static native float nGetWidth(long j, int i, int i2);

    private static native long nInitBuilder();

    private MeasuredParagraph() {
    }

    private static MeasuredParagraph obtain() {
        MeasuredParagraph measuredParagraphAcquire = sPool.acquire();
        return measuredParagraphAcquire != null ? measuredParagraphAcquire : new MeasuredParagraph();
    }

    public void recycle() {
        release();
        sPool.release(this);
    }

    private void bindNativeObject(long j) {
        this.mNativePtr = j;
        this.mNativeObjectCleaner = sRegistry.registerNativeAllocation(this, j);
    }

    private void unbindNativeObject() {
        if (this.mNativePtr != 0) {
            this.mNativeObjectCleaner.run();
            this.mNativePtr = 0L;
        }
    }

    public void release() {
        reset();
        this.mLevels.clearWithReleasingLargeArray();
        this.mWidths.clearWithReleasingLargeArray();
        this.mFontMetrics.clearWithReleasingLargeArray();
        this.mSpanEndCache.clearWithReleasingLargeArray();
    }

    private void reset() {
        this.mSpanned = null;
        this.mCopiedBuffer = null;
        this.mWholeWidth = 0.0f;
        this.mLevels.clear();
        this.mWidths.clear();
        this.mFontMetrics.clear();
        this.mSpanEndCache.clear();
        unbindNativeObject();
    }

    public int getTextLength() {
        return this.mTextLength;
    }

    public char[] getChars() {
        return this.mCopiedBuffer;
    }

    public int getParagraphDir() {
        return this.mParaDir;
    }

    public Layout.Directions getDirections(int i, int i2) {
        if (this.mLtrWithoutBidi) {
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        return AndroidBidi.directions(this.mParaDir, this.mLevels.getRawArray(), i, this.mCopiedBuffer, i, i2 - i);
    }

    public float getWholeWidth() {
        return this.mWholeWidth;
    }

    public AutoGrowArray.FloatArray getWidths() {
        return this.mWidths;
    }

    public AutoGrowArray.IntArray getSpanEndCache() {
        return this.mSpanEndCache;
    }

    public AutoGrowArray.IntArray getFontMetrics() {
        return this.mFontMetrics;
    }

    public long getNativePtr() {
        return this.mNativePtr;
    }

    public float getWidth(int i, int i2) {
        long j = this.mNativePtr;
        if (j == 0) {
            float[] rawArray = this.mWidths.getRawArray();
            float f = 0.0f;
            while (i < i2) {
                f += rawArray[i];
                i++;
            }
            return f;
        }
        return nGetWidth(j, i, i2);
    }

    public void getBounds(int i, int i2, Rect rect) {
        nGetBounds(this.mNativePtr, this.mCopiedBuffer, i, i2, rect);
    }

    public static MeasuredParagraph buildForBidi(CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic, MeasuredParagraph measuredParagraph) {
        if (measuredParagraph == null) {
            measuredParagraph = obtain();
        }
        measuredParagraph.resetAndAnalyzeBidi(charSequence, i, i2, textDirectionHeuristic);
        return measuredParagraph;
    }

    public static MeasuredParagraph buildForMeasurement(TextPaint textPaint, CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic, MeasuredParagraph measuredParagraph) {
        if (measuredParagraph == null) {
            measuredParagraph = obtain();
        }
        measuredParagraph.resetAndAnalyzeBidi(charSequence, i, i2, textDirectionHeuristic);
        measuredParagraph.mWidths.resize(measuredParagraph.mTextLength);
        if (measuredParagraph.mTextLength == 0) {
            return measuredParagraph;
        }
        if (measuredParagraph.mSpanned != null) {
            while (true) {
                int i3 = i;
                if (i3 >= i2) {
                    break;
                }
                i = measuredParagraph.mSpanned.nextSpanTransition(i3, i2, MetricAffectingSpan.class);
                measuredParagraph.applyMetricsAffectingSpan(textPaint, (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) measuredParagraph.mSpanned.getSpans(i3, i, MetricAffectingSpan.class), measuredParagraph.mSpanned, MetricAffectingSpan.class), i3, i, 0L);
            }
        } else {
            measuredParagraph.applyMetricsAffectingSpan(textPaint, null, i, i2, 0L);
        }
        return measuredParagraph;
    }

    public static MeasuredParagraph buildForStaticLayout(TextPaint textPaint, CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic, boolean z, boolean z2, MeasuredParagraph measuredParagraph) throws Throwable {
        long j;
        MeasuredParagraph measuredParagraphObtain = measuredParagraph == null ? obtain() : measuredParagraph;
        measuredParagraphObtain.resetAndAnalyzeBidi(charSequence, i, i2, textDirectionHeuristic);
        if (measuredParagraphObtain.mTextLength == 0) {
            long jNInitBuilder = nInitBuilder();
            try {
                measuredParagraphObtain.bindNativeObject(nBuildNativeMeasuredParagraph(jNInitBuilder, measuredParagraphObtain.mCopiedBuffer, z, z2));
                return measuredParagraphObtain;
            } finally {
                nFreeBuilder(jNInitBuilder);
            }
        }
        long jNInitBuilder2 = nInitBuilder();
        try {
            if (measuredParagraphObtain.mSpanned == null) {
                measuredParagraphObtain.applyMetricsAffectingSpan(textPaint, null, i, i2, jNInitBuilder2);
                measuredParagraphObtain.mSpanEndCache.append(i2);
            } else {
                while (i < i2) {
                    int iNextSpanTransition = measuredParagraphObtain.mSpanned.nextSpanTransition(i, i2, MetricAffectingSpan.class);
                    MetricAffectingSpan[] metricAffectingSpanArr = (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) measuredParagraphObtain.mSpanned.getSpans(i, iNextSpanTransition, MetricAffectingSpan.class), measuredParagraphObtain.mSpanned, MetricAffectingSpan.class);
                    j = jNInitBuilder2;
                    int i3 = i;
                    MeasuredParagraph measuredParagraph2 = measuredParagraphObtain;
                    try {
                        measuredParagraphObtain.applyMetricsAffectingSpan(textPaint, metricAffectingSpanArr, i3, iNextSpanTransition, j);
                        measuredParagraph2.mSpanEndCache.append(iNextSpanTransition);
                        jNInitBuilder2 = j;
                        measuredParagraphObtain = measuredParagraph2;
                        i = iNextSpanTransition;
                    } catch (Throwable th) {
                        th = th;
                        nFreeBuilder(j);
                        throw th;
                    }
                }
            }
            j = jNInitBuilder2;
            MeasuredParagraph measuredParagraph3 = measuredParagraphObtain;
            measuredParagraph3.bindNativeObject(nBuildNativeMeasuredParagraph(j, measuredParagraph3.mCopiedBuffer, z, z2));
            nFreeBuilder(j);
            return measuredParagraph3;
        } catch (Throwable th2) {
            th = th2;
            j = jNInitBuilder2;
        }
    }

    /* JADX WARN: Code duplicated, block: B:37:0x0087  */
    private void resetAndAnalyzeBidi(CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic) {
        reset();
        this.mSpanned = charSequence instanceof Spanned ? (Spanned) charSequence : null;
        this.mTextStart = i;
        int i3 = i2 - i;
        this.mTextLength = i3;
        char[] cArr = this.mCopiedBuffer;
        if (cArr == null || cArr.length != i3) {
            this.mCopiedBuffer = new char[this.mTextLength];
        }
        TextUtils.getChars(charSequence, i, i2, this.mCopiedBuffer, 0);
        Spanned spanned = this.mSpanned;
        if (spanned != null) {
            ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i, i2, ReplacementSpan.class);
            for (int i4 = 0; i4 < replacementSpanArr.length; i4++) {
                int spanStart = this.mSpanned.getSpanStart(replacementSpanArr[i4]) - i;
                int spanEnd = this.mSpanned.getSpanEnd(replacementSpanArr[i4]) - i;
                if (spanStart < 0) {
                    spanStart = 0;
                }
                int i5 = this.mTextLength;
                if (spanEnd > i5) {
                    spanEnd = i5;
                }
                Arrays.fill(this.mCopiedBuffer, spanStart, spanEnd, OBJECT_REPLACEMENT_CHARACTER);
            }
        }
        int i6 = 1;
        if ((textDirectionHeuristic == TextDirectionHeuristics.LTR || textDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR || textDirectionHeuristic == TextDirectionHeuristics.ANYRTL_LTR) && TextUtils.doesNotNeedBidi(this.mCopiedBuffer, 0, this.mTextLength)) {
            this.mLevels.clear();
            this.mParaDir = 1;
            this.mLtrWithoutBidi = true;
            return;
        }
        if (textDirectionHeuristic != TextDirectionHeuristics.LTR) {
            if (textDirectionHeuristic == TextDirectionHeuristics.RTL) {
                i6 = -1;
            } else if (textDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
                i6 = 2;
            } else if (textDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
                i6 = -2;
            } else if (textDirectionHeuristic.isRtl(this.mCopiedBuffer, 0, this.mTextLength)) {
                i6 = -1;
            }
        }
        this.mLevels.resize(this.mTextLength);
        this.mParaDir = AndroidBidi.bidi(i6, this.mCopiedBuffer, this.mLevels.getRawArray());
        this.mLtrWithoutBidi = false;
    }

    private void applyReplacementRun(ReplacementSpan replacementSpan, int i, int i2, long j) {
        TextPaint textPaint = this.mCachedPaint;
        Spanned spanned = this.mSpanned;
        int i3 = this.mTextStart;
        float size = replacementSpan.getSize(textPaint, spanned, i + i3, i2 + i3, this.mCachedFm);
        if (j == 0) {
            this.mWidths.set(i, size);
            int i4 = i + 1;
            if (i2 > i4) {
                Arrays.fill(this.mWidths.getRawArray(), i4, i2, 0.0f);
            }
            this.mWholeWidth += size;
            return;
        }
        nAddReplacementRun(j, this.mCachedPaint.getNativeInstance(), i, i2, size);
    }

    private void applyStyleRun(int i, int i2, long j) {
        if (this.mLtrWithoutBidi) {
            if (j == 0) {
                int i3 = i2 - i;
                this.mWholeWidth += this.mCachedPaint.getTextRunAdvances(this.mCopiedBuffer, i, i3, i, i3, false, this.mWidths.getRawArray(), i);
                return;
            } else {
                nAddStyleRun(j, this.mCachedPaint.getNativeInstance(), i, i2, false);
                return;
            }
        }
        byte b = this.mLevels.get(i);
        int i4 = i + 1;
        int i5 = i;
        while (true) {
            if (i4 == i2 || this.mLevels.get(i4) != b) {
                boolean z = (b & 1) != 0;
                if (j == 0) {
                    int i6 = i4 - i5;
                    this.mWholeWidth += this.mCachedPaint.getTextRunAdvances(this.mCopiedBuffer, i5, i6, i5, i6, z, this.mWidths.getRawArray(), i5);
                } else {
                    nAddStyleRun(j, this.mCachedPaint.getNativeInstance(), i5, i4, z);
                }
                if (i4 == i2) {
                    return;
                }
                b = this.mLevels.get(i4);
                i5 = i4;
            }
            i4++;
        }
    }

    private void applyMetricsAffectingSpan(TextPaint textPaint, MetricAffectingSpan[] metricAffectingSpanArr, int i, int i2, long j) {
        this.mCachedPaint.set(textPaint);
        this.mCachedPaint.baselineShift = 0;
        boolean z = j != 0;
        if (z && this.mCachedFm == null) {
            this.mCachedFm = new Paint.FontMetricsInt();
        }
        ReplacementSpan replacementSpan = null;
        if (metricAffectingSpanArr != null) {
            for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
                if (metricAffectingSpan instanceof ReplacementSpan) {
                    replacementSpan = (ReplacementSpan) metricAffectingSpan;
                } else {
                    metricAffectingSpan.updateMeasureState(this.mCachedPaint);
                }
            }
        }
        ReplacementSpan replacementSpan2 = replacementSpan;
        int i3 = this.mTextStart;
        int i4 = i - i3;
        int i5 = i2 - i3;
        if (j != 0) {
            this.mCachedPaint.getFontMetricsInt(this.mCachedFm);
        }
        if (replacementSpan2 != null) {
            applyReplacementRun(replacementSpan2, i4, i5, j);
        } else {
            applyStyleRun(i4, i5, j);
        }
        if (z) {
            if (this.mCachedPaint.baselineShift < 0) {
                this.mCachedFm.ascent += this.mCachedPaint.baselineShift;
                this.mCachedFm.top += this.mCachedPaint.baselineShift;
            } else {
                this.mCachedFm.descent += this.mCachedPaint.baselineShift;
                this.mCachedFm.bottom += this.mCachedPaint.baselineShift;
            }
            this.mFontMetrics.append(this.mCachedFm.top);
            this.mFontMetrics.append(this.mCachedFm.bottom);
            this.mFontMetrics.append(this.mCachedFm.ascent);
            this.mFontMetrics.append(this.mCachedFm.descent);
        }
    }

    int breakText(int i, boolean z, float f) {
        float[] rawArray = this.mWidths.getRawArray();
        if (z) {
            int i2 = 0;
            while (i2 < i) {
                f -= rawArray[i2];
                if (f < 0.0f) {
                    break;
                }
                i2++;
            }
            while (i2 > 0 && this.mCopiedBuffer[i2 - 1] == ' ') {
                i2--;
            }
            return i2;
        }
        int i3 = i - 1;
        int i4 = i3;
        while (i4 >= 0) {
            f -= rawArray[i4];
            if (f < 0.0f) {
                break;
            }
            i4--;
        }
        while (i4 < i3) {
            int i5 = i4 + 1;
            if (this.mCopiedBuffer[i5] != ' ' && rawArray[i5] != 0.0f) {
                break;
            }
            i4 = i5;
        }
        return (i - i4) - 1;
    }

    float measure(int i, int i2) {
        float[] rawArray = this.mWidths.getRawArray();
        float f = 0.0f;
        while (i < i2) {
            f += rawArray[i];
            i++;
        }
        return f;
    }

    public int getMemoryUsage() {
        return nGetMemoryUsage(this.mNativePtr);
    }
}
