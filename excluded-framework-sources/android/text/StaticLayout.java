package android.text;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.util.Pools;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;

/* JADX INFO: loaded from: classes2.dex */
public class StaticLayout extends Layout {
    private static final char CHAR_NEW_LINE = '\n';
    private static final int COLUMNS_ELLIPSIZE = 7;
    private static final int COLUMNS_NORMAL = 5;
    private static final int DEFAULT_MAX_LINE_HEIGHT = -1;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 6;
    private static final int ELLIPSIS_START = 5;
    private static final int EXTRA = 3;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int HYPHEN = 4;
    private static final int HYPHEN_MASK = 255;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_INCREMENT = 20;
    private static final int TAB_MASK = 536870912;
    static final String TAG = "StaticLayout";
    private static final int TOP = 1;
    private int mBottomPadding;
    private int mColumns;
    private boolean mEllipsized;
    private int mEllipsizedWidth;
    private int[] mLeftIndents;
    private int[] mLeftPaddings;
    private int mLineCount;
    private Layout.Directions[] mLineDirections;
    private int[] mLines;
    private int mMaxLineHeight;
    private int mMaximumVisibleLineCount;
    private int[] mRightIndents;
    private int[] mRightPaddings;
    private int mTopPadding;

    private static native int nComputeLineBreaks(long j, char[] cArr, long j2, int i, float f, int i2, float f2, int[] iArr, int i3, int i4, LineBreaks lineBreaks, int i5, int[] iArr2, float[] fArr, float[] fArr2, float[] fArr3, int[] iArr3, float[] fArr4);

    @CriticalNative
    private static native void nFinish(long j);

    @FastNative
    private static native long nInit(int i, int i2, boolean z, int[] iArr, int[] iArr2, int[] iArr3);

    public static final class Builder {
        private static final Pools.SynchronizedPool<Builder> sPool = new Pools.SynchronizedPool<>(3);
        private boolean mAddLastLineLineSpacing;
        private Layout.Alignment mAlignment;
        private int mBreakStrategy;
        private TextUtils.TruncateAt mEllipsize;
        private int mEllipsizedWidth;
        private int mEnd;
        private boolean mFallbackLineSpacing;
        private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
        private int mHyphenationFrequency;
        private boolean mIncludePad;
        private int mJustificationMode;
        private int[] mLeftIndents;
        private int[] mLeftPaddings;
        private int mMaxLines;
        private TextPaint mPaint;
        private int[] mRightIndents;
        private int[] mRightPaddings;
        private float mSpacingAdd;
        private float mSpacingMult;
        private int mStart;
        private CharSequence mText;
        private TextDirectionHeuristic mTextDir;
        private int mWidth;

        private Builder() {
        }

        public static Builder obtain(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3) {
            Builder builderAcquire = sPool.acquire();
            if (builderAcquire == null) {
                builderAcquire = new Builder();
            }
            builderAcquire.mText = charSequence;
            builderAcquire.mStart = i;
            builderAcquire.mEnd = i2;
            builderAcquire.mPaint = textPaint;
            builderAcquire.mWidth = i3;
            builderAcquire.mAlignment = Layout.Alignment.ALIGN_NORMAL;
            builderAcquire.mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
            builderAcquire.mSpacingMult = 1.0f;
            builderAcquire.mSpacingAdd = 0.0f;
            builderAcquire.mIncludePad = true;
            builderAcquire.mFallbackLineSpacing = false;
            builderAcquire.mEllipsizedWidth = i3;
            builderAcquire.mEllipsize = null;
            builderAcquire.mMaxLines = Integer.MAX_VALUE;
            builderAcquire.mBreakStrategy = 0;
            builderAcquire.mHyphenationFrequency = 0;
            builderAcquire.mJustificationMode = 0;
            return builderAcquire;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void recycle(Builder builder) {
            builder.mPaint = null;
            builder.mText = null;
            builder.mLeftIndents = null;
            builder.mRightIndents = null;
            builder.mLeftPaddings = null;
            builder.mRightPaddings = null;
            sPool.release(builder);
        }

        void finish() {
            this.mText = null;
            this.mPaint = null;
            this.mLeftIndents = null;
            this.mRightIndents = null;
            this.mLeftPaddings = null;
            this.mRightPaddings = null;
        }

        public Builder setText(CharSequence charSequence) {
            return setText(charSequence, 0, charSequence.length());
        }

        public Builder setText(CharSequence charSequence, int i, int i2) {
            this.mText = charSequence;
            this.mStart = i;
            this.mEnd = i2;
            return this;
        }

        public Builder setPaint(TextPaint textPaint) {
            this.mPaint = textPaint;
            return this;
        }

        public Builder setWidth(int i) {
            this.mWidth = i;
            if (this.mEllipsize == null) {
                this.mEllipsizedWidth = i;
            }
            return this;
        }

        public Builder setAlignment(Layout.Alignment alignment) {
            this.mAlignment = alignment;
            return this;
        }

        public Builder setTextDirection(TextDirectionHeuristic textDirectionHeuristic) {
            this.mTextDir = textDirectionHeuristic;
            return this;
        }

        public Builder setLineSpacing(float f, float f2) {
            this.mSpacingAdd = f;
            this.mSpacingMult = f2;
            return this;
        }

        public Builder setIncludePad(boolean z) {
            this.mIncludePad = z;
            return this;
        }

        public Builder setUseLineSpacingFromFallbacks(boolean z) {
            this.mFallbackLineSpacing = z;
            return this;
        }

        public Builder setEllipsizedWidth(int i) {
            this.mEllipsizedWidth = i;
            return this;
        }

        public Builder setEllipsize(TextUtils.TruncateAt truncateAt) {
            this.mEllipsize = truncateAt;
            return this;
        }

        public Builder setMaxLines(int i) {
            this.mMaxLines = i;
            return this;
        }

        public Builder setBreakStrategy(int i) {
            this.mBreakStrategy = i;
            return this;
        }

        public Builder setHyphenationFrequency(int i) {
            this.mHyphenationFrequency = i;
            return this;
        }

        public Builder setIndents(int[] iArr, int[] iArr2) {
            this.mLeftIndents = iArr;
            this.mRightIndents = iArr2;
            return this;
        }

        public Builder setAvailablePaddings(int[] iArr, int[] iArr2) {
            this.mLeftPaddings = iArr;
            this.mRightPaddings = iArr2;
            return this;
        }

        public Builder setJustificationMode(int i) {
            this.mJustificationMode = i;
            return this;
        }

        Builder setAddLastLineLineSpacing(boolean z) {
            this.mAddLastLineLineSpacing = z;
            return this;
        }

        public StaticLayout build() {
            StaticLayout staticLayout = new StaticLayout(this);
            recycle(this);
            return staticLayout;
        }
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z);
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, null, 0);
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4) {
        this(charSequence, i, i2, textPaint, i3, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2, z, truncateAt, i4, Integer.MAX_VALUE);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4, int i5) {
        CharSequence ellipsizer;
        CharSequence charSequence2;
        if (truncateAt == null) {
            charSequence2 = charSequence;
        } else {
            if (charSequence instanceof Spanned) {
                ellipsizer = new Layout.SpannedEllipsizer(charSequence);
            } else {
                ellipsizer = new Layout.Ellipsizer(charSequence);
            }
            charSequence2 = ellipsizer;
        }
        super(charSequence2, textPaint, i3, alignment, textDirectionHeuristic, f, f2);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        Builder maxLines = Builder.obtain(charSequence, i, i2, textPaint, i3).setAlignment(alignment).setTextDirection(textDirectionHeuristic).setLineSpacing(f2, f).setIncludePad(z).setEllipsizedWidth(i4).setEllipsize(truncateAt).setMaxLines(i5);
        if (truncateAt != null) {
            Layout.Ellipsizer ellipsizer2 = (Layout.Ellipsizer) getText();
            ellipsizer2.mLayout = this;
            ellipsizer2.mWidth = i4;
            ellipsizer2.mMethod = truncateAt;
            this.mEllipsizedWidth = i4;
            this.mColumns = 7;
        } else {
            this.mColumns = 5;
            this.mEllipsizedWidth = i3;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = i5;
        generate(maxLines, maxLines.mIncludePad, maxLines.mIncludePad);
        Builder.recycle(maxLines);
    }

    StaticLayout(CharSequence charSequence) {
        super(charSequence, null, 0, null, 0.0f, 0.0f);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mColumns = 7;
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    private StaticLayout(Builder builder) {
        CharSequence ellipsizer;
        if (builder.mEllipsize == null) {
            ellipsizer = builder.mText;
        } else if (builder.mText instanceof Spanned) {
            ellipsizer = new Layout.SpannedEllipsizer(builder.mText);
        } else {
            ellipsizer = new Layout.Ellipsizer(builder.mText);
        }
        super(ellipsizer, builder.mPaint, builder.mWidth, builder.mAlignment, builder.mTextDir, builder.mSpacingMult, builder.mSpacingAdd);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        if (builder.mEllipsize != null) {
            Layout.Ellipsizer ellipsizer2 = (Layout.Ellipsizer) getText();
            ellipsizer2.mLayout = this;
            ellipsizer2.mWidth = builder.mEllipsizedWidth;
            ellipsizer2.mMethod = builder.mEllipsize;
            this.mEllipsizedWidth = builder.mEllipsizedWidth;
            this.mColumns = 7;
        } else {
            this.mColumns = 5;
            this.mEllipsizedWidth = builder.mWidth;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = builder.mMaxLines;
        this.mLeftIndents = builder.mLeftIndents;
        this.mRightIndents = builder.mRightIndents;
        this.mLeftPaddings = builder.mLeftPaddings;
        this.mRightPaddings = builder.mRightPaddings;
        setJustificationMode(builder.mJustificationMode);
        generate(builder, builder.mIncludePad, builder.mIncludePad);
    }

    /* JADX WARN: Code duplicated, block: B:128:0x02d4  */
    /* JADX WARN: Code duplicated, block: B:131:0x02e7  */
    /* JADX WARN: Code duplicated, block: B:134:0x0311 A[Catch: all -> 0x055a, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x055a, blocks: (B:52:0x011b, B:61:0x0131, B:62:0x0145, B:64:0x0148, B:66:0x0164, B:67:0x016f, B:68:0x0176, B:87:0x01d2, B:89:0x01dd, B:90:0x01e3, B:92:0x01e6, B:93:0x01f1, B:99:0x026f, B:101:0x0273, B:103:0x0278, B:113:0x0288, B:115:0x0291, B:117:0x0297, B:125:0x02b5, B:122:0x02a8, B:124:0x02ac, B:120:0x02a2, B:126:0x02c3, B:134:0x0311, B:137:0x0317, B:140:0x031d, B:143:0x0323, B:146:0x0329, B:160:0x034c, B:163:0x035e, B:170:0x03e8, B:172:0x03fc, B:174:0x0406, B:192:0x0505, B:196:0x0510, B:198:0x0516, B:72:0x018f, B:76:0x0199, B:78:0x019c, B:80:0x01a4, B:82:0x01b1, B:81:0x01af, B:74:0x0193), top: B:206:0x011b }] */
    /* JADX WARN: Code duplicated, block: B:137:0x0317 A[Catch: all -> 0x055a, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x055a, blocks: (B:52:0x011b, B:61:0x0131, B:62:0x0145, B:64:0x0148, B:66:0x0164, B:67:0x016f, B:68:0x0176, B:87:0x01d2, B:89:0x01dd, B:90:0x01e3, B:92:0x01e6, B:93:0x01f1, B:99:0x026f, B:101:0x0273, B:103:0x0278, B:113:0x0288, B:115:0x0291, B:117:0x0297, B:125:0x02b5, B:122:0x02a8, B:124:0x02ac, B:120:0x02a2, B:126:0x02c3, B:134:0x0311, B:137:0x0317, B:140:0x031d, B:143:0x0323, B:146:0x0329, B:160:0x034c, B:163:0x035e, B:170:0x03e8, B:172:0x03fc, B:174:0x0406, B:192:0x0505, B:196:0x0510, B:198:0x0516, B:72:0x018f, B:76:0x0199, B:78:0x019c, B:80:0x01a4, B:82:0x01b1, B:81:0x01af, B:74:0x0193), top: B:206:0x011b }] */
    /* JADX WARN: Code duplicated, block: B:140:0x031d A[Catch: all -> 0x055a, TRY_ENTER, TRY_LEAVE, TryCatch #1 {all -> 0x055a, blocks: (B:52:0x011b, B:61:0x0131, B:62:0x0145, B:64:0x0148, B:66:0x0164, B:67:0x016f, B:68:0x0176, B:87:0x01d2, B:89:0x01dd, B:90:0x01e3, B:92:0x01e6, B:93:0x01f1, B:99:0x026f, B:101:0x0273, B:103:0x0278, B:113:0x0288, B:115:0x0291, B:117:0x0297, B:125:0x02b5, B:122:0x02a8, B:124:0x02ac, B:120:0x02a2, B:126:0x02c3, B:134:0x0311, B:137:0x0317, B:140:0x031d, B:143:0x0323, B:146:0x0329, B:160:0x034c, B:163:0x035e, B:170:0x03e8, B:172:0x03fc, B:174:0x0406, B:192:0x0505, B:196:0x0510, B:198:0x0516, B:72:0x018f, B:76:0x0199, B:78:0x019c, B:80:0x01a4, B:82:0x01b1, B:81:0x01af, B:74:0x0193), top: B:206:0x011b }] */
    /* JADX WARN: Code duplicated, block: B:143:0x0323 A[Catch: all -> 0x055a, TRY_ENTER, TryCatch #1 {all -> 0x055a, blocks: (B:52:0x011b, B:61:0x0131, B:62:0x0145, B:64:0x0148, B:66:0x0164, B:67:0x016f, B:68:0x0176, B:87:0x01d2, B:89:0x01dd, B:90:0x01e3, B:92:0x01e6, B:93:0x01f1, B:99:0x026f, B:101:0x0273, B:103:0x0278, B:113:0x0288, B:115:0x0291, B:117:0x0297, B:125:0x02b5, B:122:0x02a8, B:124:0x02ac, B:120:0x02a2, B:126:0x02c3, B:134:0x0311, B:137:0x0317, B:140:0x031d, B:143:0x0323, B:146:0x0329, B:160:0x034c, B:163:0x035e, B:170:0x03e8, B:172:0x03fc, B:174:0x0406, B:192:0x0505, B:196:0x0510, B:198:0x0516, B:72:0x018f, B:76:0x0199, B:78:0x019c, B:80:0x01a4, B:82:0x01b1, B:81:0x01af, B:74:0x0193), top: B:206:0x011b }] */
    /* JADX WARN: Code duplicated, block: B:95:0x01f8  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v73 */
    /* JADX WARN: Type inference failed for: r77v1 */
    /* JADX WARN: Type inference failed for: r77v2 */
    /* JADX WARN: Type inference failed for: r77v5 */
    /* JADX WARN: Type inference failed for: r77v6 */
    /* JADX WARN: Type inference failed for: r9v2 */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:151:0x0335
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:162)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:117)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:590)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:82)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    void generate(android.text.StaticLayout.Builder r77, boolean r78, boolean r79) {
        /*
            Method dump skipped, instruction units count: 1375
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.StaticLayout.generate(android.text.StaticLayout$Builder, boolean, boolean):void");
    }

    /* JADX WARN: Code duplicated, block: B:53:0x012b  */
    private int out(CharSequence charSequence, int i, int i2, int i3, int i4, int i5, int i6, int i7, float f, float f2, LineHeightSpan[] lineHeightSpanArr, int[] iArr, Paint.FontMetricsInt fontMetricsInt, int i8, boolean z, MeasuredParagraph measuredParagraph, int i9, boolean z2, boolean z3, boolean z4, char[] cArr, float[] fArr, int i10, TextUtils.TruncateAt truncateAt, float f3, float f4, TextPaint textPaint, boolean z5) {
        int[] iArr2;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        int i17;
        int i18;
        int i19;
        int i20;
        int i21;
        int i22 = this.mLineCount;
        int i23 = this.mColumns;
        int i24 = i22 * i23;
        int i25 = 1;
        int i26 = i24 + i23 + 1;
        int[] iArr3 = this.mLines;
        int paragraphDir = measuredParagraph.getParagraphDir();
        int i27 = 0;
        if (i26 >= iArr3.length) {
            int[] iArrNewUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(GrowingArrayUtils.growSize(i26));
            System.arraycopy(iArr3, 0, iArrNewUnpaddedIntArray, 0, iArr3.length);
            this.mLines = iArrNewUnpaddedIntArray;
            iArr2 = iArrNewUnpaddedIntArray;
        } else {
            iArr2 = iArr3;
        }
        if (i22 >= this.mLineDirections.length) {
            Layout.Directions[] directionsArr = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, GrowingArrayUtils.growSize(i22));
            Layout.Directions[] directionsArr2 = this.mLineDirections;
            System.arraycopy(directionsArr2, 0, directionsArr, 0, directionsArr2.length);
            this.mLineDirections = directionsArr;
        }
        int i28 = i3;
        if (lineHeightSpanArr != null) {
            fontMetricsInt.ascent = i28;
            fontMetricsInt.descent = i4;
            fontMetricsInt.top = i5;
            fontMetricsInt.bottom = i6;
            int i29 = 0;
            while (i29 < lineHeightSpanArr.length) {
                if (lineHeightSpanArr[i29] instanceof LineHeightSpan.WithDensity) {
                    i21 = i29;
                    ((LineHeightSpan.WithDensity) lineHeightSpanArr[i29]).chooseHeight(charSequence, i, i2, iArr[i29], i7, fontMetricsInt, textPaint);
                } else {
                    i21 = i29;
                    lineHeightSpanArr[i21].chooseHeight(charSequence, i, i2, iArr[i21], i7, fontMetricsInt);
                }
                i29 = i21 + 1;
                i25 = i25;
                i27 = i27;
                i22 = i22;
            }
            i14 = i27;
            i15 = i25;
            i16 = i22;
            i28 = fontMetricsInt.ascent;
            i11 = fontMetricsInt.descent;
            i12 = fontMetricsInt.top;
            i13 = fontMetricsInt.bottom;
        } else {
            i11 = i4;
            i12 = i5;
            i13 = i6;
            i14 = 0;
            i15 = 1;
            i16 = i22;
        }
        int i30 = i28;
        int i31 = i11;
        int i32 = i12;
        int i33 = i13;
        int i34 = i16 == 0 ? i15 : i14;
        int i35 = i16 + 1 == this.mMaximumVisibleLineCount ? i15 : i14;
        if (truncateAt != null) {
            int i36 = (z5 && this.mLineCount + i15 == this.mMaximumVisibleLineCount) ? i15 : i14;
            if (((((!(this.mMaximumVisibleLineCount == i15 && z5) && (i34 == 0 || z5)) || truncateAt == TextUtils.TruncateAt.MARQUEE) && (i34 != 0 || ((i35 == 0 && z5) || truncateAt != TextUtils.TruncateAt.END))) ? i14 : i15) != 0) {
                calculateEllipsis(i, i2, fArr, i10, f3, truncateAt, i16, f4, textPaint, i36);
            }
        }
        if (this.mEllipsized) {
            i17 = i;
            i18 = i15;
            i19 = i18;
        } else {
            int i37 = (i10 == i9 || i9 <= 0 || charSequence.charAt(i9 + (-1)) != '\n') ? i14 : i15;
            if (i2 == i9 && i37 == 0) {
                i17 = i;
                i18 = i15;
            } else {
                i17 = i;
                i18 = i15;
                if (i17 != i9 || i37 == 0) {
                    i19 = i14;
                }
            }
            i19 = i18;
        }
        if (i34 != 0) {
            if (z3) {
                this.mTopPadding = i32 - i30;
            }
            if (z2) {
                i30 = i32;
            }
        }
        if (i19 != 0) {
            if (z3) {
                this.mBottomPadding = i33 - i31;
            }
            if (z2) {
                i31 = i33;
            }
        }
        if (z && (z4 || i19 == 0)) {
            double d = ((i31 - i30) * (f - 1.0f)) + f2;
            i20 = d >= 0.0d ? (int) (d + EXTRA_ROUNDING) : -((int) ((-d) + EXTRA_ROUNDING));
        } else {
            i20 = i14;
        }
        int i38 = i24 + 0;
        iArr2[i38] = i17;
        iArr2[i24 + 1] = i7;
        iArr2[i24 + 2] = i31 + i20;
        iArr2[i24 + 3] = i20;
        if (!this.mEllipsized && i35 != 0) {
            if (!z2) {
                i33 = i31;
            }
            this.mMaxLineHeight = i7 + (i33 - i30);
        }
        int i39 = i7 + (i31 - i30) + i20;
        int i40 = this.mColumns;
        iArr2[i24 + i40 + 0] = i2;
        iArr2[i24 + i40 + i18] = i39;
        iArr2[i38] = iArr2[i38] | (i8 & 536870912);
        iArr2[i24 + 4] = i8;
        iArr2[i38] = iArr2[i38] | (paragraphDir << 30);
        this.mLineDirections[i16] = measuredParagraph.getDirections(i17 - i10, i2 - i10);
        this.mLineCount += i18;
        return i39;
    }

    private void calculateEllipsis(int i, int i2, float[] fArr, int i3, float f, TextUtils.TruncateAt truncateAt, int i4, float f2, TextPaint textPaint, boolean z) {
        int i5;
        float totalInsets = f - getTotalInsets(i4);
        int i6 = 0;
        if (f2 <= totalInsets && !z) {
            int[] iArr = this.mLines;
            int i7 = this.mColumns;
            iArr[(i7 * i4) + 5] = 0;
            iArr[(i7 * i4) + 6] = 0;
            return;
        }
        float fMeasureText = textPaint.measureText(TextUtils.getEllipsisString(truncateAt));
        int i8 = i2 - i;
        float f3 = 0.0f;
        if (truncateAt == TextUtils.TruncateAt.START) {
            if (this.mMaximumVisibleLineCount == 1) {
                int i9 = i8;
                float f4 = 0.0f;
                while (i9 > 0) {
                    f4 += fArr[((i9 - 1) + i) - i3];
                    if (f4 + fMeasureText > totalInsets) {
                        while (i9 < i8 && fArr[(i9 + i) - i3] == 0.0f) {
                            i9++;
                        }
                        break;
                    }
                    i9--;
                }
                i5 = i9;
            } else {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Start Ellipsis only supported with one line");
                }
                i5 = 0;
            }
        } else if (truncateAt == TextUtils.TruncateAt.END || truncateAt == TextUtils.TruncateAt.MARQUEE || truncateAt == TextUtils.TruncateAt.END_SMALL) {
            while (i6 < i8) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 + fMeasureText > totalInsets) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i8 - i6;
            if (z && i5 == 0 && i8 > 0) {
                i6 = i8 - 1;
                i5 = 1;
            }
        } else if (this.mMaximumVisibleLineCount == 1) {
            float f5 = totalInsets - fMeasureText;
            float f6 = f5 / 2.0f;
            int i10 = i8;
            float f7 = 0.0f;
            while (i10 > 0) {
                float f8 = fArr[((i10 - 1) + i) - i3] + f7;
                if (f8 > f6) {
                    while (i10 < i8 && fArr[(i10 + i) - i3] == 0.0f) {
                        i10++;
                    }
                    break;
                }
                i10--;
                f7 = f8;
            }
            float f9 = f5 - f7;
            while (i6 < i10) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 > f9) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i10 - i6;
        } else {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Middle Ellipsis only supported with one line");
            }
            i5 = 0;
        }
        this.mEllipsized = true;
        int[] iArr2 = this.mLines;
        int i11 = this.mColumns;
        iArr2[(i11 * i4) + 5] = i6;
        iArr2[(i11 * i4) + 6] = i5;
    }

    private float getTotalInsets(int i) {
        int[] iArr = this.mLeftIndents;
        int i2 = iArr != null ? iArr[Math.min(i, iArr.length - 1)] : 0;
        int[] iArr2 = this.mRightIndents;
        if (iArr2 != null) {
            i2 += iArr2[Math.min(i, iArr2.length - 1)];
        }
        return i2;
    }

    @Override // android.text.Layout
    public int getLineForVertical(int i) {
        int i2 = this.mLineCount;
        int[] iArr = this.mLines;
        int i3 = -1;
        while (i2 - i3 > 1) {
            int i4 = (i2 + i3) >> 1;
            if (iArr[(this.mColumns * i4) + 1] > i) {
                i2 = i4;
            } else {
                i3 = i4;
            }
        }
        if (i3 < 0) {
            return 0;
        }
        return i3;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // android.text.Layout
    public int getLineTop(int i) {
        return this.mLines[(this.mColumns * i) + 1];
    }

    @Override // android.text.Layout
    public int getLineExtra(int i) {
        return this.mLines[(this.mColumns * i) + 3];
    }

    @Override // android.text.Layout
    public int getLineDescent(int i) {
        return this.mLines[(this.mColumns * i) + 2];
    }

    @Override // android.text.Layout
    public int getLineStart(int i) {
        return this.mLines[(this.mColumns * i) + 0] & 536870911;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int i) {
        return this.mLines[(this.mColumns * i) + 0] >> 30;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int i) {
        return (this.mLines[(this.mColumns * i) + 0] & 536870912) != 0;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int i) {
        if (i > getLineCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.mLineDirections[i];
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    @Override // android.text.Layout
    public int getHyphen(int i) {
        return this.mLines[(this.mColumns * i) + 4] & 255;
    }

    @Override // android.text.Layout
    public int getIndentAdjust(int i, Layout.Alignment alignment) {
        if (alignment == Layout.Alignment.ALIGN_LEFT) {
            int[] iArr = this.mLeftIndents;
            if (iArr == null) {
                return 0;
            }
            return iArr[Math.min(i, iArr.length - 1)];
        }
        if (alignment == Layout.Alignment.ALIGN_RIGHT) {
            int[] iArr2 = this.mRightIndents;
            if (iArr2 == null) {
                return 0;
            }
            return -iArr2[Math.min(i, iArr2.length - 1)];
        }
        if (alignment == Layout.Alignment.ALIGN_CENTER) {
            int[] iArr3 = this.mLeftIndents;
            int i2 = iArr3 != null ? iArr3[Math.min(i, iArr3.length - 1)] : 0;
            int[] iArr4 = this.mRightIndents;
            return (i2 - (iArr4 != null ? iArr4[Math.min(i, iArr4.length - 1)] : 0)) >> 1;
        }
        throw new AssertionError("unhandled alignment " + alignment);
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int i) {
        int i2 = this.mColumns;
        if (i2 < 7) {
            return 0;
        }
        return this.mLines[(i2 * i) + 6];
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int i) {
        int i2 = this.mColumns;
        if (i2 < 7) {
            return 0;
        }
        return this.mLines[(i2 * i) + 5];
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public int getHeight(boolean z) {
        int i;
        if (z && this.mLineCount >= this.mMaximumVisibleLineCount && this.mMaxLineHeight == -1 && Log.isLoggable(TAG, 5)) {
            Log.w(TAG, "maxLineHeight should not be -1.  maxLines:" + this.mMaximumVisibleLineCount + " lineCount:" + this.mLineCount);
        }
        return (!z || this.mLineCount < this.mMaximumVisibleLineCount || (i = this.mMaxLineHeight) == -1) ? super.getHeight() : i;
    }

    static class LineBreaks {
        private static final int INITIAL_SIZE = 16;
        public int[] breaks = new int[16];
        public float[] widths = new float[16];
        public float[] ascents = new float[16];
        public float[] descents = new float[16];
        public int[] flags = new int[16];

        LineBreaks() {
        }
    }
}
