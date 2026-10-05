package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.method.TextKeyListener;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.TabStopSpan;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Layout {
    public static final int BREAK_STRATEGY_BALANCED = 2;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_SIMPLE = 0;
    public static final float DEFAULT_LINESPACING_ADDITION = 0.0f;
    public static final float DEFAULT_LINESPACING_MULTIPLIER = 1.0f;
    public static final int DIR_LEFT_TO_RIGHT = 1;
    static final int DIR_REQUEST_DEFAULT_LTR = 2;
    static final int DIR_REQUEST_DEFAULT_RTL = -2;
    static final int DIR_REQUEST_LTR = 1;
    static final int DIR_REQUEST_RTL = -1;
    public static final int DIR_RIGHT_TO_LEFT = -1;
    public static final int HYPHENATION_FREQUENCY_FULL = 2;
    public static final int HYPHENATION_FREQUENCY_NONE = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;
    public static final int JUSTIFICATION_MODE_NONE = 0;
    static final int RUN_LEVEL_MASK = 63;
    static final int RUN_LEVEL_SHIFT = 26;
    static final int RUN_RTL_FLAG = 67108864;
    private static final int TAB_INCREMENT = 20;
    public static final int TEXT_SELECTION_LAYOUT_LEFT_TO_RIGHT = 1;
    public static final int TEXT_SELECTION_LAYOUT_RIGHT_TO_LEFT = 0;
    private Alignment mAlignment;
    private int mJustificationMode;
    private SpanSet<LineBackgroundSpan> mLineBackgroundSpans;
    private TextPaint mPaint;
    private float mSpacingAdd;
    private float mSpacingMult;
    private boolean mSpannedText;
    private CharSequence mText;
    private TextDirectionHeuristic mTextDir;
    private int mWidth;
    private TextPaint mWorkPaint;
    private static final ParagraphStyle[] NO_PARA_SPANS = (ParagraphStyle[]) ArrayUtils.emptyArray(ParagraphStyle.class);
    private static final Rect sTempRect = new Rect();
    static final int RUN_LENGTH_MASK = 67108863;
    public static final Directions DIRS_ALL_LEFT_TO_RIGHT = new Directions(new int[]{0, RUN_LENGTH_MASK});
    public static final Directions DIRS_ALL_RIGHT_TO_LEFT = new Directions(new int[]{0, 134217727});

    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_OPPOSITE,
        ALIGN_CENTER,
        ALIGN_LEFT,
        ALIGN_RIGHT
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface BreakStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface HyphenationFrequency {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface JustificationMode {
    }

    @FunctionalInterface
    public interface SelectionRectangleConsumer {
        void accept(float f, float f2, float f3, float f4, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TextSelectionLayout {
    }

    public abstract int getBottomPadding();

    public abstract int getEllipsisCount(int i);

    public abstract int getEllipsisStart(int i);

    public int getHyphen(int i) {
        return 0;
    }

    public int getIndentAdjust(int i, Alignment alignment) {
        return 0;
    }

    public abstract boolean getLineContainsTab(int i);

    public abstract int getLineCount();

    public abstract int getLineDescent(int i);

    public abstract Directions getLineDirections(int i);

    public int getLineExtra(int i) {
        return 0;
    }

    public abstract int getLineStart(int i);

    public abstract int getLineTop(int i);

    public abstract int getParagraphDirection(int i);

    public abstract int getTopPadding();

    public static float getDesiredWidth(CharSequence charSequence, TextPaint textPaint) {
        return getDesiredWidth(charSequence, 0, charSequence.length(), textPaint);
    }

    public static float getDesiredWidth(CharSequence charSequence, int i, int i2, TextPaint textPaint) {
        return getDesiredWidth(charSequence, i, i2, textPaint, TextDirectionHeuristics.FIRSTSTRONG_LTR);
    }

    public static float getDesiredWidth(CharSequence charSequence, int i, int i2, TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic) {
        return getDesiredWidthWithLimit(charSequence, i, i2, textPaint, textDirectionHeuristic, Float.MAX_VALUE);
    }

    public static float getDesiredWidthWithLimit(CharSequence charSequence, int i, int i2, TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic, float f) throws Throwable {
        float f2 = 0.0f;
        while (i <= i2) {
            int iIndexOf = TextUtils.indexOf(charSequence, '\n', i, i2);
            if (iIndexOf < 0) {
                iIndexOf = i2;
            }
            float fMeasurePara = measurePara(textPaint, charSequence, i, iIndexOf, textDirectionHeuristic);
            if (fMeasurePara > f) {
                return f;
            }
            if (fMeasurePara > f2) {
                f2 = fMeasurePara;
            }
            i = iIndexOf + 1;
        }
        return f2;
    }

    protected Layout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2) {
        this(charSequence, textPaint, i, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2);
    }

    protected Layout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2) {
        this.mWorkPaint = new TextPaint();
        this.mAlignment = Alignment.ALIGN_NORMAL;
        if (i < 0) {
            throw new IllegalArgumentException("Layout: " + i + " < 0");
        }
        if (textPaint != null) {
            textPaint.bgColor = 0;
            textPaint.baselineShift = 0;
        }
        this.mText = charSequence;
        this.mPaint = textPaint;
        this.mWidth = i;
        this.mAlignment = alignment;
        this.mSpacingMult = f;
        this.mSpacingAdd = f2;
        this.mSpannedText = charSequence instanceof Spanned;
        this.mTextDir = textDirectionHeuristic;
    }

    protected void setJustificationMode(int i) {
        this.mJustificationMode = i;
    }

    void replaceWith(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2) {
        if (i < 0) {
            throw new IllegalArgumentException("Layout: " + i + " < 0");
        }
        this.mText = charSequence;
        this.mPaint = textPaint;
        this.mWidth = i;
        this.mAlignment = alignment;
        this.mSpacingMult = f;
        this.mSpacingAdd = f2;
        this.mSpannedText = charSequence instanceof Spanned;
    }

    public void draw(Canvas canvas) {
        draw(canvas, null, null, 0);
    }

    public void draw(Canvas canvas, Path path, Paint paint, int i) {
        long lineRangeForDraw = getLineRangeForDraw(canvas);
        int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(lineRangeForDraw);
        int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(lineRangeForDraw);
        if (iUnpackRangeEndFromLong < 0) {
            return;
        }
        drawBackground(canvas, path, paint, i, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
        drawText(canvas, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
    }

    private boolean isJustificationRequired(int i) {
        int lineEnd;
        return (this.mJustificationMode == 0 || (lineEnd = getLineEnd(i)) >= this.mText.length() || this.mText.charAt(lineEnd - 1) == '\n') ? false : true;
    }

    private float getJustifyWidth(int i) {
        int indentAdjust;
        int indentAdjust2;
        Alignment alignment = this.mAlignment;
        int leadingMargin = this.mWidth;
        int paragraphDirection = getParagraphDirection(i);
        ParagraphStyle[] paragraphStyleArr = NO_PARA_SPANS;
        int i2 = 0;
        if (this.mSpannedText) {
            Spanned spanned = (Spanned) this.mText;
            int lineStart = getLineStart(i);
            boolean z = lineStart == 0 || this.mText.charAt(lineStart + (-1)) == '\n';
            if (z) {
                paragraphStyleArr = (ParagraphStyle[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, this.mText.length(), ParagraphStyle.class), ParagraphStyle.class);
                for (int length = paragraphStyleArr.length - 1; length >= 0; length--) {
                    if (paragraphStyleArr[length] instanceof AlignmentSpan) {
                        alignment = ((AlignmentSpan) paragraphStyleArr[length]).getAlignment();
                        break;
                    }
                }
            }
            int length2 = paragraphStyleArr.length;
            for (int i3 = 0; i3 < length2; i3++) {
                if (paragraphStyleArr[i3] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                    if (i < getLineForOffset(spanned.getSpanStart(paragraphStyleArr[i3])) + ((LeadingMarginSpan.LeadingMarginSpan2) paragraphStyleArr[i3]).getLeadingMarginLineCount()) {
                        z = true;
                        break;
                    }
                }
            }
            int leadingMargin2 = 0;
            while (i2 < length2) {
                if (paragraphStyleArr[i2] instanceof LeadingMarginSpan) {
                    LeadingMarginSpan leadingMarginSpan = (LeadingMarginSpan) paragraphStyleArr[i2];
                    if (paragraphDirection == -1) {
                        leadingMargin -= leadingMarginSpan.getLeadingMargin(z);
                    } else {
                        leadingMargin2 += leadingMarginSpan.getLeadingMargin(z);
                    }
                }
                i2++;
            }
            i2 = leadingMargin2;
        }
        if (alignment == Alignment.ALIGN_LEFT) {
            alignment = paragraphDirection == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else if (alignment == Alignment.ALIGN_RIGHT) {
            alignment = paragraphDirection == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
        }
        if (alignment == Alignment.ALIGN_NORMAL) {
            if (paragraphDirection == 1) {
                indentAdjust = getIndentAdjust(i, Alignment.ALIGN_LEFT);
            } else {
                indentAdjust2 = getIndentAdjust(i, Alignment.ALIGN_RIGHT);
                indentAdjust = -indentAdjust2;
            }
        } else if (alignment != Alignment.ALIGN_OPPOSITE) {
            indentAdjust = getIndentAdjust(i, Alignment.ALIGN_CENTER);
        } else if (paragraphDirection == 1) {
            indentAdjust2 = getIndentAdjust(i, Alignment.ALIGN_RIGHT);
            indentAdjust = -indentAdjust2;
        } else {
            indentAdjust = getIndentAdjust(i, Alignment.ALIGN_LEFT);
        }
        return (leadingMargin - i2) - indentAdjust;
    }

    /* JADX WARN: Code duplicated, block: B:102:0x00e8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:103:0x00de A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:105:0x00e3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:25:0x00b5  */
    /* JADX WARN: Code duplicated, block: B:28:0x00c1  */
    /* JADX WARN: Code duplicated, block: B:30:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:33:0x00e1  */
    /* JADX WARN: Code duplicated, block: B:38:0x00f1  */
    /* JADX WARN: Code duplicated, block: B:40:0x00f7  */
    /* JADX WARN: Code duplicated, block: B:42:0x00ff  */
    /* JADX WARN: Code duplicated, block: B:43:0x0137  */
    /* JADX WARN: Code duplicated, block: B:44:0x0171  */
    public void drawText(Canvas canvas, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        CharSequence charSequence;
        TextLine textLine;
        TabStops tabStops;
        int i7;
        int i8;
        Alignment alignment;
        int leadingMargin;
        TabStops tabStops2;
        int i9;
        int i10;
        Alignment alignment2;
        boolean z;
        int i11;
        int i12;
        int indentAdjust;
        int i13;
        TextLine textLine2;
        int indentAdjust2;
        int i14;
        TabStops tabStops3;
        ParagraphStyle[] paragraphStyleArr;
        Alignment alignment3;
        boolean z2;
        boolean z3;
        ParagraphStyle[] paragraphStyleArr2;
        boolean z4;
        int i15;
        int length;
        int i16;
        boolean z5;
        int i17;
        boolean z6;
        int i18;
        int i19;
        CharSequence charSequence2;
        int i20;
        LeadingMarginSpan leadingMarginSpan;
        this = this;
        int i21 = i;
        int lineTop = this.getLineTop(i21);
        int lineStart = this.getLineStart(i21);
        ParagraphStyle[] paragraphStyleArr3 = NO_PARA_SPANS;
        TextPaint textPaint = this.mWorkPaint;
        textPaint.set(this.mPaint);
        CharSequence charSequence3 = this.mText;
        Alignment alignment4 = this.mAlignment;
        TextLine textLineObtain = TextLine.obtain();
        int i22 = lineTop;
        int i23 = lineStart;
        TabStops tabStops4 = null;
        int i24 = i21;
        int i25 = 0;
        boolean z7 = false;
        while (i24 <= i2) {
            int i26 = i24 + 1;
            int lineStart2 = this.getLineStart(i26);
            boolean zIsJustificationRequired = this.isJustificationRequired(i24);
            int lineVisibleEnd = this.getLineVisibleEnd(i24, i23, lineStart2);
            textPaint.setHyphenEdit(this.getHyphen(i24));
            int lineTop2 = this.getLineTop(i26);
            int lineDescent = lineTop2 - this.getLineDescent(i24);
            TextLine textLine3 = textLineObtain;
            int paragraphDirection = this.getParagraphDirection(i24);
            boolean z8 = z7;
            int leadingMargin2 = this.mWidth;
            TabStops tabStops5 = tabStops4;
            if (this.mSpannedText) {
                Spanned spanned = (Spanned) charSequence3;
                int length2 = charSequence3.length();
                if (i23 != 0) {
                    paragraphStyleArr = paragraphStyleArr3;
                    alignment3 = alignment4;
                    if (charSequence3.charAt(i23 - 1) != '\n') {
                        z2 = false;
                    }
                    if (i23 >= i25 || !(i24 == i21 || z2)) {
                        z3 = true;
                        paragraphStyleArr2 = paragraphStyleArr;
                        z4 = z8;
                        i15 = i25;
                    } else {
                        int iNextSpanTransition = spanned.nextSpanTransition(i23, length2, ParagraphStyle.class);
                        ParagraphStyle[] paragraphStyleArr4 = (ParagraphStyle[]) getParagraphSpans(spanned, i23, iNextSpanTransition, ParagraphStyle.class);
                        Alignment alignment5 = this.mAlignment;
                        z3 = true;
                        int length3 = paragraphStyleArr4.length - 1;
                        while (true) {
                            i15 = iNextSpanTransition;
                            if (length3 < 0) {
                                break;
                            }
                            if (paragraphStyleArr4[length3] instanceof AlignmentSpan) {
                                alignment5 = ((AlignmentSpan) paragraphStyleArr4[length3]).getAlignment();
                                break;
                            } else {
                                length3--;
                                iNextSpanTransition = i15;
                            }
                        }
                        paragraphStyleArr2 = paragraphStyleArr4;
                        alignment3 = alignment5;
                        z4 = false;
                    }
                    length = paragraphStyleArr2.length;
                    i16 = 0;
                    while (true) {
                        if (i16 < length) {
                            i3 = lineStart2;
                            z5 = z2;
                            break;
                        }
                        if (paragraphStyleArr2[i16] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                            i3 = lineStart2;
                            if (i24 < this.getLineForOffset(spanned.getSpanStart(paragraphStyleArr2[i16])) + ((LeadingMarginSpan.LeadingMarginSpan2) paragraphStyleArr2[i16]).getLeadingMarginLineCount()) {
                                z5 = z3;
                                break;
                            }
                        } else {
                            i3 = lineStart2;
                        }
                        i16++;
                        lineStart2 = i3;
                    }
                    i17 = 0;
                    leadingMargin = 0;
                    while (i17 < length) {
                        if (paragraphStyleArr2[i17] instanceof LeadingMarginSpan) {
                            leadingMarginSpan = (LeadingMarginSpan) paragraphStyleArr2[i17];
                            if (paragraphDirection == -1) {
                                z6 = z5;
                                i18 = i23;
                                i19 = lineDescent;
                                i20 = paragraphDirection;
                                charSequence2 = charSequence3;
                                leadingMarginSpan.drawLeadingMargin(canvas, textPaint, leadingMargin2, paragraphDirection, i22, lineDescent, lineTop2, charSequence3, i18, lineVisibleEnd, z2, this);
                                leadingMargin2 -= leadingMarginSpan.getLeadingMargin(z6);
                            } else {
                                z6 = z5;
                                i18 = i23;
                                i19 = lineDescent;
                                charSequence2 = charSequence3;
                                i20 = paragraphDirection;
                                leadingMarginSpan.drawLeadingMargin(canvas, textPaint, leadingMargin, i20, i22, i19, lineTop2, charSequence2, i18, lineVisibleEnd, z2, this);
                                leadingMargin += leadingMarginSpan.getLeadingMargin(z6);
                            }
                        } else {
                            z6 = z5;
                            i18 = i23;
                            i19 = lineDescent;
                            charSequence2 = charSequence3;
                            i20 = paragraphDirection;
                        }
                        i17++;
                        z5 = z6;
                        lineDescent = i19;
                        paragraphDirection = i20;
                        paragraphStyleArr2 = paragraphStyleArr2;
                        textLine3 = textLine3;
                        length = length;
                        i26 = i26;
                        i24 = i24;
                        i23 = i18;
                        tabStops5 = tabStops5;
                        charSequence3 = charSequence2;
                    }
                    i4 = i26;
                    i5 = i23;
                    i6 = lineDescent;
                    charSequence = charSequence3;
                    textLine = textLine3;
                    tabStops = tabStops5;
                    i7 = paragraphDirection;
                    i8 = i24;
                    i25 = i15;
                    paragraphStyleArr3 = paragraphStyleArr2;
                    z7 = z4;
                    alignment = alignment3;
                } else {
                    paragraphStyleArr = paragraphStyleArr3;
                    alignment3 = alignment4;
                }
                z2 = true;
                if (i23 >= i25) {
                    z3 = true;
                    paragraphStyleArr2 = paragraphStyleArr;
                    z4 = z8;
                    i15 = i25;
                } else {
                    z3 = true;
                    paragraphStyleArr2 = paragraphStyleArr;
                    z4 = z8;
                    i15 = i25;
                }
                length = paragraphStyleArr2.length;
                i16 = 0;
                while (true) {
                    if (i16 < length) {
                        i3 = lineStart2;
                        z5 = z2;
                        break;
                    }
                    if (paragraphStyleArr2[i16] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                        i3 = lineStart2;
                        if (i24 < this.getLineForOffset(spanned.getSpanStart(paragraphStyleArr2[i16])) + ((LeadingMarginSpan.LeadingMarginSpan2) paragraphStyleArr2[i16]).getLeadingMarginLineCount()) {
                            z5 = z3;
                            break;
                        }
                    } else {
                        i3 = lineStart2;
                    }
                    i16++;
                    lineStart2 = i3;
                }
                i17 = 0;
                leadingMargin = 0;
                while (i17 < length) {
                    if (paragraphStyleArr2[i17] instanceof LeadingMarginSpan) {
                        leadingMarginSpan = (LeadingMarginSpan) paragraphStyleArr2[i17];
                        if (paragraphDirection == -1) {
                            z6 = z5;
                            i18 = i23;
                            i19 = lineDescent;
                            i20 = paragraphDirection;
                            charSequence2 = charSequence3;
                            leadingMarginSpan.drawLeadingMargin(canvas, textPaint, leadingMargin2, paragraphDirection, i22, lineDescent, lineTop2, charSequence3, i18, lineVisibleEnd, z2, this);
                            leadingMargin2 -= leadingMarginSpan.getLeadingMargin(z6);
                        } else {
                            z6 = z5;
                            i18 = i23;
                            i19 = lineDescent;
                            charSequence2 = charSequence3;
                            i20 = paragraphDirection;
                            leadingMarginSpan.drawLeadingMargin(canvas, textPaint, leadingMargin, i20, i22, i19, lineTop2, charSequence2, i18, lineVisibleEnd, z2, this);
                            leadingMargin += leadingMarginSpan.getLeadingMargin(z6);
                        }
                    } else {
                        z6 = z5;
                        i18 = i23;
                        i19 = lineDescent;
                        charSequence2 = charSequence3;
                        i20 = paragraphDirection;
                    }
                    i17++;
                    z5 = z6;
                    lineDescent = i19;
                    paragraphDirection = i20;
                    paragraphStyleArr2 = paragraphStyleArr2;
                    textLine3 = textLine3;
                    length = length;
                    i26 = i26;
                    i24 = i24;
                    i23 = i18;
                    tabStops5 = tabStops5;
                    charSequence3 = charSequence2;
                }
                i4 = i26;
                i5 = i23;
                i6 = lineDescent;
                charSequence = charSequence3;
                textLine = textLine3;
                tabStops = tabStops5;
                i7 = paragraphDirection;
                i8 = i24;
                i25 = i15;
                paragraphStyleArr3 = paragraphStyleArr2;
                z7 = z4;
                alignment = alignment3;
            } else {
                i3 = lineStart2;
                i4 = i26;
                i5 = i23;
                i6 = lineDescent;
                charSequence = charSequence3;
                textLine = textLine3;
                tabStops = tabStops5;
                i7 = paragraphDirection;
                i8 = i24;
                z7 = z8;
                alignment = alignment4;
                leadingMargin = 0;
            }
            boolean lineContainsTab = getLineContainsTab(i8);
            if (!lineContainsTab || z7) {
                tabStops2 = tabStops;
            } else {
                TabStops tabStops6 = tabStops;
                if (tabStops6 == null) {
                    tabStops3 = new TabStops(20, paragraphStyleArr3);
                } else {
                    tabStops6.reset(20, paragraphStyleArr3);
                    tabStops3 = tabStops6;
                }
                tabStops2 = tabStops3;
                z7 = true;
            }
            if (alignment == Alignment.ALIGN_LEFT) {
                i9 = i7;
                i10 = 1;
                alignment2 = i9 == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
            } else {
                i9 = i7;
                i10 = 1;
                if (alignment == Alignment.ALIGN_RIGHT) {
                    alignment2 = i9 == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
                } else {
                    alignment2 = alignment;
                }
            }
            if (alignment2 == Alignment.ALIGN_NORMAL) {
                if (i9 == i10) {
                    indentAdjust2 = getIndentAdjust(i8, Alignment.ALIGN_LEFT);
                    i14 = leadingMargin + indentAdjust2;
                } else {
                    indentAdjust2 = -getIndentAdjust(i8, Alignment.ALIGN_RIGHT);
                    i14 = leadingMargin2 - indentAdjust2;
                }
                i12 = indentAdjust2;
                i11 = i14;
                z = false;
            } else {
                z = false;
                int lineExtent = (int) getLineExtent(i8, tabStops2, false);
                if (alignment2 == Alignment.ALIGN_OPPOSITE) {
                    if (i9 == i10) {
                        indentAdjust = -getIndentAdjust(i8, Alignment.ALIGN_RIGHT);
                        i13 = (leadingMargin2 - lineExtent) - indentAdjust;
                    } else {
                        indentAdjust = getIndentAdjust(i8, Alignment.ALIGN_LEFT);
                        i13 = (leadingMargin - lineExtent) + indentAdjust;
                    }
                    i12 = indentAdjust;
                    i11 = i13;
                } else {
                    int indentAdjust3 = getIndentAdjust(i8, Alignment.ALIGN_CENTER);
                    i11 = (((leadingMargin2 + leadingMargin) - (lineExtent & (-2))) >> 1) + indentAdjust3;
                    i12 = indentAdjust3;
                }
            }
            Directions lineDirections = getLineDirections(i8);
            if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT && !this.mSpannedText && !lineContainsTab && !zIsJustificationRequired) {
                canvas.drawText(charSequence, i5, lineVisibleEnd, i11, i6, textPaint);
                textLine2 = textLine;
            } else {
                int i27 = i6;
                int i28 = i11;
                textLine.set(textPaint, charSequence, i5, lineVisibleEnd, i9, lineDirections, lineContainsTab, tabStops2);
                if (zIsJustificationRequired) {
                    textLine2 = textLine;
                    textLine2.justify((leadingMargin2 - leadingMargin) - i12);
                } else {
                    textLine2 = textLine;
                }
                textLine2.draw(canvas, i28, i22, i27, lineTop2);
            }
            textLineObtain = textLine2;
            alignment4 = alignment;
            tabStops4 = tabStops2;
            i25 = i25;
            i22 = lineTop2;
            i23 = i3;
            i24 = i4;
            charSequence3 = charSequence;
            i21 = i;
        }
        TextLine.recycle(textLineObtain);
    }

    /* JADX WARN: Code duplicated, block: B:33:0x009f A[LOOP:1: B:32:0x009d->B:33:0x009f, LOOP_END] */
    public void drawBackground(Canvas canvas, Path path, Paint paint, int i, int i2, int i3) {
        int i4;
        ParagraphStyle[] paragraphStyleArr;
        int i5;
        if (this.mSpannedText) {
            if (this.mLineBackgroundSpans == null) {
                this.mLineBackgroundSpans = new SpanSet<>(LineBackgroundSpan.class);
            }
            Spanned spanned = (Spanned) this.mText;
            int length = spanned.length();
            int i6 = 0;
            this.mLineBackgroundSpans.init(spanned, 0, length);
            if (this.mLineBackgroundSpans.numberOfSpans > 0) {
                int lineTop = getLineTop(i2);
                int lineStart = getLineStart(i2);
                ParagraphStyle[] paragraphStyleArr2 = NO_PARA_SPANS;
                TextPaint textPaint = this.mPaint;
                int i7 = this.mWidth;
                int i8 = i2;
                int i9 = lineTop;
                int i10 = lineStart;
                int i11 = 0;
                int i12 = 0;
                while (i8 <= i3) {
                    int i13 = i8 + 1;
                    int lineStart2 = getLineStart(i13);
                    int lineTop2 = getLineTop(i13);
                    int lineDescent = lineTop2 - getLineDescent(i8);
                    if (i10 >= i11) {
                        int nextTransition = this.mLineBackgroundSpans.getNextTransition(i10, length);
                        if (i10 != lineStart2 || i10 == 0) {
                            i12 = i6;
                            while (i6 < this.mLineBackgroundSpans.numberOfSpans) {
                                if (this.mLineBackgroundSpans.spanStarts[i6] < lineStart2 && this.mLineBackgroundSpans.spanEnds[i6] > i10) {
                                    ParagraphStyle[] paragraphStyleArr3 = (ParagraphStyle[]) GrowingArrayUtils.append((LineBackgroundSpan[]) paragraphStyleArr2, i12, this.mLineBackgroundSpans.spans[i6]);
                                    i12++;
                                    paragraphStyleArr2 = paragraphStyleArr3;
                                }
                                i6++;
                            }
                            i4 = nextTransition;
                        } else {
                            i4 = nextTransition;
                        }
                        paragraphStyleArr = paragraphStyleArr2;
                        i5 = 0;
                        while (i5 < i6) {
                            int i14 = lineStart2;
                            int i15 = i10;
                            int i16 = i8;
                            ((LineBackgroundSpan) paragraphStyleArr[i5]).drawBackground(canvas, textPaint, 0, i7, i9, lineDescent, lineTop2, spanned, i15, i14, i16);
                            i5++;
                            i6 = i6;
                            i13 = i13;
                            lineStart2 = i14;
                            i10 = i15;
                            i8 = i16;
                            i7 = i7;
                            textPaint = textPaint;
                            length = length;
                        }
                        i9 = lineTop2;
                        i12 = i6;
                        i11 = i4;
                        paragraphStyleArr2 = paragraphStyleArr;
                        i8 = i13;
                        i10 = lineStart2;
                        i6 = 0;
                    } else {
                        i4 = i11;
                    }
                    i6 = i12;
                    paragraphStyleArr = paragraphStyleArr2;
                    i5 = 0;
                    while (i5 < i6) {
                        int i17 = lineStart2;
                        int i18 = i10;
                        int i19 = i8;
                        ((LineBackgroundSpan) paragraphStyleArr[i5]).drawBackground(canvas, textPaint, 0, i7, i9, lineDescent, lineTop2, spanned, i18, i17, i19);
                        i5++;
                        i6 = i6;
                        i13 = i13;
                        lineStart2 = i17;
                        i10 = i18;
                        i8 = i19;
                        i7 = i7;
                        textPaint = textPaint;
                        length = length;
                    }
                    i9 = lineTop2;
                    i12 = i6;
                    i11 = i4;
                    paragraphStyleArr2 = paragraphStyleArr;
                    i8 = i13;
                    i10 = lineStart2;
                    i6 = 0;
                }
            }
            this.mLineBackgroundSpans.recycle();
        }
        if (path != null) {
            if (i != 0) {
                canvas.translate(0.0f, i);
            }
            canvas.drawPath(path, paint);
            if (i != 0) {
                canvas.translate(0.0f, -i);
            }
        }
    }

    public long getLineRangeForDraw(Canvas canvas) {
        synchronized (sTempRect) {
            if (!canvas.getClipBounds(sTempRect)) {
                return TextUtils.packRangeInLong(0, -1);
            }
            int i = sTempRect.top;
            int i2 = sTempRect.bottom;
            int iMax = Math.max(i, 0);
            int iMin = Math.min(getLineTop(getLineCount()), i2);
            if (iMax >= iMin) {
                return TextUtils.packRangeInLong(0, -1);
            }
            return TextUtils.packRangeInLong(getLineForVertical(iMax), getLineForVertical(iMin));
        }
    }

    private int getLineStartPos(int i, int i2, int i3) {
        int indentAdjust;
        int indentAdjust2;
        Alignment paragraphAlignment = getParagraphAlignment(i);
        int paragraphDirection = getParagraphDirection(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            paragraphAlignment = paragraphDirection == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
            paragraphAlignment = paragraphDirection == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
        }
        if (paragraphAlignment == Alignment.ALIGN_NORMAL) {
            if (paragraphDirection == 1) {
                indentAdjust = getIndentAdjust(i, Alignment.ALIGN_LEFT);
                return i2 + indentAdjust;
            }
            indentAdjust2 = getIndentAdjust(i, Alignment.ALIGN_RIGHT);
            return i3 + indentAdjust2;
        }
        TabStops tabStops = null;
        if (this.mSpannedText && getLineContainsTab(i)) {
            Spanned spanned = (Spanned) this.mText;
            int lineStart = getLineStart(i);
            TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, spanned.length(), TabStopSpan.class), TabStopSpan.class);
            if (tabStopSpanArr.length > 0) {
                tabStops = new TabStops(20, tabStopSpanArr);
            }
        }
        int lineExtent = (int) getLineExtent(i, tabStops, false);
        if (paragraphAlignment != Alignment.ALIGN_OPPOSITE) {
            return ((i2 + i3) - (lineExtent & (-2))) >> (getIndentAdjust(i, Alignment.ALIGN_CENTER) + 1);
        }
        if (paragraphDirection == 1) {
            i3 -= lineExtent;
            indentAdjust2 = getIndentAdjust(i, Alignment.ALIGN_RIGHT);
            return i3 + indentAdjust2;
        }
        i2 -= lineExtent;
        indentAdjust = getIndentAdjust(i, Alignment.ALIGN_LEFT);
        return i2 + indentAdjust;
    }

    public final CharSequence getText() {
        return this.mText;
    }

    public final TextPaint getPaint() {
        return this.mPaint;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public int getEllipsizedWidth() {
        return this.mWidth;
    }

    public final void increaseWidthTo(int i) {
        if (i < this.mWidth) {
            throw new RuntimeException("attempted to reduce Layout width");
        }
        this.mWidth = i;
    }

    public int getHeight() {
        return getLineTop(getLineCount());
    }

    public int getHeight(boolean z) {
        return getHeight();
    }

    public final Alignment getAlignment() {
        return this.mAlignment;
    }

    public final float getSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public final float getSpacingAdd() {
        return this.mSpacingAdd;
    }

    public final TextDirectionHeuristic getTextDirectionHeuristic() {
        return this.mTextDir;
    }

    public int getLineBounds(int i, Rect rect) {
        if (rect != null) {
            rect.left = 0;
            rect.top = getLineTop(i);
            rect.right = this.mWidth;
            rect.bottom = getLineTop(i + 1);
        }
        return getLineBaseline(i);
    }

    public boolean isLevelBoundary(int i) {
        int lineForOffset = getLineForOffset(i);
        Directions lineDirections = getLineDirections(lineForOffset);
        if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT || lineDirections == DIRS_ALL_RIGHT_TO_LEFT) {
            return false;
        }
        int[] iArr = lineDirections.mDirections;
        int lineStart = getLineStart(lineForOffset);
        int lineEnd = getLineEnd(lineForOffset);
        if (i == lineStart || i == lineEnd) {
            return ((iArr[(i == lineStart ? 0 : iArr.length + (-2)) + 1] >>> 26) & 63) != (getParagraphDirection(lineForOffset) == 1 ? 0 : 1);
        }
        int i2 = i - lineStart;
        for (int i3 = 0; i3 < iArr.length; i3 += 2) {
            if (i2 == iArr[i3]) {
                return true;
            }
        }
        return false;
    }

    public boolean isRtlCharAt(int i) {
        int lineForOffset = getLineForOffset(i);
        Directions lineDirections = getLineDirections(lineForOffset);
        if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT) {
            return false;
        }
        if (lineDirections == DIRS_ALL_RIGHT_TO_LEFT) {
            return true;
        }
        int[] iArr = lineDirections.mDirections;
        int lineStart = getLineStart(lineForOffset);
        for (int i2 = 0; i2 < iArr.length; i2 += 2) {
            int i3 = iArr[i2] + lineStart;
            int i4 = i2 + 1;
            int i5 = (iArr[i4] & RUN_LENGTH_MASK) + i3;
            if (i >= i3 && i < i5) {
                return (((iArr[i4] >>> 26) & 63) & 1) != 0;
            }
        }
        return false;
    }

    public long getRunRange(int i) {
        int lineForOffset = getLineForOffset(i);
        Directions lineDirections = getLineDirections(lineForOffset);
        if (lineDirections == DIRS_ALL_LEFT_TO_RIGHT || lineDirections == DIRS_ALL_RIGHT_TO_LEFT) {
            return TextUtils.packRangeInLong(0, getLineEnd(lineForOffset));
        }
        int[] iArr = lineDirections.mDirections;
        int lineStart = getLineStart(lineForOffset);
        for (int i2 = 0; i2 < iArr.length; i2 += 2) {
            int i3 = iArr[i2] + lineStart;
            int i4 = (iArr[i2 + 1] & RUN_LENGTH_MASK) + i3;
            if (i >= i3 && i < i4) {
                return TextUtils.packRangeInLong(i3, i4);
            }
        }
        return TextUtils.packRangeInLong(0, getLineEnd(lineForOffset));
    }

    private boolean primaryIsTrailingPrevious(int i) {
        int i2;
        int i3;
        int lineForOffset = getLineForOffset(i);
        int lineStart = getLineStart(lineForOffset);
        int lineEnd = getLineEnd(lineForOffset);
        int[] iArr = getLineDirections(lineForOffset).mDirections;
        int i4 = 0;
        while (true) {
            i2 = -1;
            if (i4 >= iArr.length) {
                i3 = -1;
                break;
            }
            int i5 = iArr[i4] + lineStart;
            int i6 = i4 + 1;
            int i7 = (iArr[i6] & RUN_LENGTH_MASK) + i5;
            if (i7 > lineEnd) {
                i7 = lineEnd;
            }
            if (i >= i5 && i < i7) {
                if (i <= i5) {
                    i3 = (iArr[i6] >>> 26) & 63;
                    break;
                }
                return false;
            }
            i4 += 2;
        }
        if (i3 == -1) {
            i3 = getParagraphDirection(lineForOffset) == 1 ? 0 : 1;
        }
        if (i == lineStart) {
            i2 = getParagraphDirection(lineForOffset) == 1 ? 0 : 1;
        } else {
            int i8 = i - 1;
            for (int i9 = 0; i9 < iArr.length; i9 += 2) {
                int i10 = iArr[i9] + lineStart;
                int i11 = i9 + 1;
                int i12 = (iArr[i11] & RUN_LENGTH_MASK) + i10;
                if (i12 > lineEnd) {
                    i12 = lineEnd;
                }
                if (i8 >= i10 && i8 < i12) {
                    i2 = (iArr[i11] >>> 26) & 63;
                    break;
                }
            }
        }
        return i2 < i3;
    }

    private boolean[] primaryIsTrailingPreviousAllLineOffsets(int i) {
        byte b;
        int lineStart = getLineStart(i);
        int lineEnd = getLineEnd(i);
        int[] iArr = getLineDirections(i).mDirections;
        int i2 = (lineEnd - lineStart) + 1;
        boolean[] zArr = new boolean[i2];
        byte[] bArr = new byte[i2];
        for (int i3 = 0; i3 < iArr.length; i3 += 2) {
            int i4 = i3 + 1;
            int i5 = iArr[i3] + lineStart + (iArr[i4] & RUN_LENGTH_MASK);
            if (i5 > lineEnd) {
                i5 = lineEnd;
            }
            bArr[(i5 - lineStart) - 1] = (byte) ((iArr[i4] >>> 26) & 63);
        }
        for (int i6 = 0; i6 < iArr.length; i6 += 2) {
            int i7 = iArr[i6] + lineStart;
            byte b2 = (byte) ((iArr[i6 + 1] >>> 26) & 63);
            int i8 = i7 - lineStart;
            if (i7 == lineStart) {
                b = getParagraphDirection(i) == 1 ? (byte) 0 : (byte) 1;
            } else {
                b = bArr[i8 - 1];
            }
            zArr[i8] = b2 > b;
        }
        return zArr;
    }

    public float getPrimaryHorizontal(int i) {
        return getPrimaryHorizontal(i, false);
    }

    public float getPrimaryHorizontal(int i, boolean z) {
        return getHorizontal(i, primaryIsTrailingPrevious(i), z);
    }

    public float getSecondaryHorizontal(int i) {
        return getSecondaryHorizontal(i, false);
    }

    public float getSecondaryHorizontal(int i, boolean z) {
        return getHorizontal(i, !primaryIsTrailingPrevious(i), z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getHorizontal(int i, boolean z) {
        return z ? getPrimaryHorizontal(i) : getSecondaryHorizontal(i);
    }

    private float getHorizontal(int i, boolean z, boolean z2) {
        return getHorizontal(i, z, getLineForOffset(i), z2);
    }

    /* JADX WARN: Code duplicated, block: B:9:0x0036  */
    private float getHorizontal(int i, boolean z, int i2, boolean z2) {
        TabStops tabStops;
        int lineStart = getLineStart(i2);
        int lineEnd = getLineEnd(i2);
        int paragraphDirection = getParagraphDirection(i2);
        boolean lineContainsTab = getLineContainsTab(i2);
        Directions lineDirections = getLineDirections(i2);
        if (lineContainsTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, lineStart, lineEnd, TabStopSpan.class);
                if (tabStopSpanArr.length > 0) {
                    tabStops = new TabStops(20, tabStopSpanArr);
                } else {
                    tabStops = null;
                }
            } else {
                tabStops = null;
            }
        } else {
            tabStops = null;
        }
        TextLine textLineObtain = TextLine.obtain();
        textLineObtain.set(this.mPaint, this.mText, lineStart, lineEnd, paragraphDirection, lineDirections, lineContainsTab, tabStops);
        float fMeasure = textLineObtain.measure(i - lineStart, z, null);
        TextLine.recycle(textLineObtain);
        if (z2) {
            int i3 = this.mWidth;
            if (fMeasure > i3) {
                fMeasure = i3;
            }
        }
        return getLineStartPos(i2, getParagraphLeft(i2), getParagraphRight(i2)) + fMeasure;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:9:0x0034  */
    public float[] getLineHorizontals(int i, boolean z, boolean z2) {
        TabStops tabStops;
        int lineStart = getLineStart(i);
        int lineEnd = getLineEnd(i);
        int paragraphDirection = getParagraphDirection(i);
        boolean lineContainsTab = getLineContainsTab(i);
        Directions lineDirections = getLineDirections(i);
        if (lineContainsTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, lineStart, lineEnd, TabStopSpan.class);
                if (tabStopSpanArr.length > 0) {
                    tabStops = new TabStops(20, tabStopSpanArr);
                } else {
                    tabStops = null;
                }
            } else {
                tabStops = null;
            }
        } else {
            tabStops = null;
        }
        TextLine textLineObtain = TextLine.obtain();
        textLineObtain.set(this.mPaint, this.mText, lineStart, lineEnd, paragraphDirection, lineDirections, lineContainsTab, tabStops);
        boolean[] zArrPrimaryIsTrailingPreviousAllLineOffsets = primaryIsTrailingPreviousAllLineOffsets(i);
        if (!z2) {
            for (int i2 = 0; i2 < zArrPrimaryIsTrailingPreviousAllLineOffsets.length; i2++) {
                zArrPrimaryIsTrailingPreviousAllLineOffsets[i2] = !zArrPrimaryIsTrailingPreviousAllLineOffsets[i2];
            }
        }
        float[] fArrMeasureAllOffsets = textLineObtain.measureAllOffsets(zArrPrimaryIsTrailingPreviousAllLineOffsets, null);
        TextLine.recycle(textLineObtain);
        if (z) {
            for (int i3 = 0; i3 <= fArrMeasureAllOffsets.length; i3++) {
                float f = fArrMeasureAllOffsets[i3];
                int i4 = this.mWidth;
                if (f > i4) {
                    fArrMeasureAllOffsets[i3] = i4;
                }
            }
        }
        int lineStartPos = getLineStartPos(i, getParagraphLeft(i), getParagraphRight(i));
        int i5 = (lineEnd - lineStart) + 1;
        float[] fArr = new float[i5];
        for (int i6 = 0; i6 < i5; i6++) {
            fArr[i6] = lineStartPos + fArrMeasureAllOffsets[i6];
        }
        return fArr;
    }

    public float getLineLeft(int i) {
        float paragraphRight;
        float lineMax;
        int paragraphDirection = getParagraphDirection(i);
        Alignment paragraphAlignment = getParagraphAlignment(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            return 0.0f;
        }
        if (paragraphAlignment == Alignment.ALIGN_NORMAL) {
            if (paragraphDirection != -1) {
                return 0.0f;
            }
            paragraphRight = getParagraphRight(i);
            lineMax = getLineMax(i);
        } else if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
            paragraphRight = this.mWidth;
            lineMax = getLineMax(i);
        } else {
            if (paragraphAlignment != Alignment.ALIGN_OPPOSITE) {
                int paragraphLeft = getParagraphLeft(i);
                return paragraphLeft + (((getParagraphRight(i) - paragraphLeft) - (((int) getLineMax(i)) & (-2))) / 2);
            }
            if (paragraphDirection == -1) {
                return 0.0f;
            }
            paragraphRight = this.mWidth;
            lineMax = getLineMax(i);
        }
        return paragraphRight - lineMax;
    }

    public float getLineRight(int i) {
        float paragraphLeft;
        float lineMax;
        int paragraphDirection = getParagraphDirection(i);
        Alignment paragraphAlignment = getParagraphAlignment(i);
        if (paragraphAlignment == Alignment.ALIGN_LEFT) {
            paragraphLeft = getParagraphLeft(i);
            lineMax = getLineMax(i);
        } else {
            if (paragraphAlignment != Alignment.ALIGN_NORMAL) {
                if (paragraphAlignment == Alignment.ALIGN_RIGHT) {
                    return this.mWidth;
                }
                if (paragraphAlignment == Alignment.ALIGN_OPPOSITE) {
                    if (paragraphDirection == -1) {
                        return getLineMax(i);
                    }
                    return this.mWidth;
                }
                int paragraphLeft2 = getParagraphLeft(i);
                int paragraphRight = getParagraphRight(i);
                return paragraphRight - (((paragraphRight - paragraphLeft2) - (((int) getLineMax(i)) & (-2))) / 2);
            }
            if (paragraphDirection == -1) {
                return this.mWidth;
            }
            paragraphLeft = getParagraphLeft(i);
            lineMax = getLineMax(i);
        }
        return paragraphLeft + lineMax;
    }

    public float getLineMax(int i) {
        float paragraphLeadingMargin = getParagraphLeadingMargin(i);
        float lineExtent = getLineExtent(i, false);
        if (lineExtent < 0.0f) {
            lineExtent = -lineExtent;
        }
        return paragraphLeadingMargin + lineExtent;
    }

    public float getLineWidth(int i) {
        float paragraphLeadingMargin = getParagraphLeadingMargin(i);
        float lineExtent = getLineExtent(i, true);
        if (lineExtent < 0.0f) {
            lineExtent = -lineExtent;
        }
        return paragraphLeadingMargin + lineExtent;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0033  */
    private float getLineExtent(int i, boolean z) {
        TabStops tabStops;
        int lineStart = getLineStart(i);
        int lineEnd = z ? getLineEnd(i) : getLineVisibleEnd(i);
        boolean lineContainsTab = getLineContainsTab(i);
        if (lineContainsTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, lineStart, lineEnd, TabStopSpan.class);
                if (tabStopSpanArr.length > 0) {
                    tabStops = new TabStops(20, tabStopSpanArr);
                } else {
                    tabStops = null;
                }
            } else {
                tabStops = null;
            }
        } else {
            tabStops = null;
        }
        Directions lineDirections = getLineDirections(i);
        if (lineDirections == null) {
            return 0.0f;
        }
        int paragraphDirection = getParagraphDirection(i);
        TextLine textLineObtain = TextLine.obtain();
        TextPaint textPaint = this.mWorkPaint;
        textPaint.set(this.mPaint);
        textPaint.setHyphenEdit(getHyphen(i));
        textLineObtain.set(textPaint, this.mText, lineStart, lineEnd, paragraphDirection, lineDirections, lineContainsTab, tabStops);
        if (isJustificationRequired(i)) {
            textLineObtain.justify(getJustifyWidth(i));
        }
        float fMetrics = textLineObtain.metrics(null);
        TextLine.recycle(textLineObtain);
        return fMetrics;
    }

    private float getLineExtent(int i, TabStops tabStops, boolean z) {
        int lineStart = getLineStart(i);
        int lineEnd = z ? getLineEnd(i) : getLineVisibleEnd(i);
        boolean lineContainsTab = getLineContainsTab(i);
        Directions lineDirections = getLineDirections(i);
        int paragraphDirection = getParagraphDirection(i);
        TextLine textLineObtain = TextLine.obtain();
        TextPaint textPaint = this.mWorkPaint;
        textPaint.set(this.mPaint);
        textPaint.setHyphenEdit(getHyphen(i));
        textLineObtain.set(textPaint, this.mText, lineStart, lineEnd, paragraphDirection, lineDirections, lineContainsTab, tabStops);
        if (isJustificationRequired(i)) {
            textLineObtain.justify(getJustifyWidth(i));
        }
        float fMetrics = textLineObtain.metrics(null);
        TextLine.recycle(textLineObtain);
        return fMetrics;
    }

    public int getLineForVertical(int i) {
        int lineCount = getLineCount();
        int i2 = -1;
        while (lineCount - i2 > 1) {
            int i3 = (lineCount + i2) / 2;
            if (getLineTop(i3) > i) {
                lineCount = i3;
            } else {
                i2 = i3;
            }
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public int getLineForOffset(int i) {
        int lineCount = getLineCount();
        int i2 = -1;
        while (lineCount - i2 > 1) {
            int i3 = (lineCount + i2) / 2;
            if (getLineStart(i3) > i) {
                lineCount = i3;
            } else {
                i2 = i3;
            }
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public int getOffsetForHorizontal(int i, float f) {
        return getOffsetForHorizontal(i, f, true);
    }

    /* JADX WARN: Code duplicated, block: B:38:0x00da  */
    /* JADX WARN: Code duplicated, block: B:42:0x00e2  */
    /* JADX WARN: Multi-variable type inference failed */
    public int getOffsetForHorizontal(int i, float f, boolean z) {
        Layout layout = this;
        int lineEnd = getLineEnd(i);
        int lineStart = getLineStart(i);
        Directions lineDirections = getLineDirections(i);
        TextLine textLineObtain = TextLine.obtain();
        textLineObtain.set(layout.mPaint, layout.mText, lineStart, lineEnd, getParagraphDirection(i), lineDirections, false, null);
        HorizontalMeasurementProvider horizontalMeasurementProvider = layout.new HorizontalMeasurementProvider(i, z);
        int i2 = 1;
        if (i != getLineCount() - 1) {
            lineEnd = textLineObtain.getOffsetToLeftRightOf(lineEnd - lineStart, !layout.isRtlCharAt(lineEnd - 1)) + lineStart;
        }
        float fAbs = Math.abs(horizontalMeasurementProvider.get(lineStart) - f);
        int i3 = lineStart;
        int i4 = 0;
        while (i4 < lineDirections.mDirections.length) {
            int i5 = lineDirections.mDirections[i4] + lineStart;
            int i6 = i4 + 1;
            int i7 = (lineDirections.mDirections[i6] & RUN_LENGTH_MASK) + i5;
            boolean z2 = (lineDirections.mDirections[i6] & 67108864) != 0 ? i2 : 0;
            int i8 = z2 != 0 ? -1 : i2;
            if (i7 > lineEnd) {
                i7 = lineEnd;
            }
            int i9 = (i7 - 1) + i2;
            int i10 = i5 + 1;
            int i11 = i3;
            int i12 = i10 - 1;
            Directions directions = lineDirections;
            while (i9 - i12 > i2) {
                int i13 = (i9 + i12) / 2;
                float f2 = horizontalMeasurementProvider.get(layout.getOffsetAtStartOf(i13));
                float f3 = i8;
                if (f2 * f3 >= f3 * f) {
                    i9 = i13;
                } else {
                    i12 = i13;
                }
                i2 = 1;
                layout = this;
            }
            if (i12 >= i10) {
                i10 = i12;
            }
            if (i10 < i7) {
                int offsetToLeftRightOf = textLineObtain.getOffsetToLeftRightOf(i10 - lineStart, z2) + lineStart;
                int offsetToLeftRightOf2 = textLineObtain.getOffsetToLeftRightOf(offsetToLeftRightOf - lineStart, !z2) + lineStart;
                if (offsetToLeftRightOf2 < i5 || offsetToLeftRightOf2 >= i7) {
                    i3 = i11;
                } else {
                    float fAbs2 = Math.abs(horizontalMeasurementProvider.get(offsetToLeftRightOf2) - f);
                    if (offsetToLeftRightOf < i7) {
                        float fAbs3 = Math.abs(horizontalMeasurementProvider.get(offsetToLeftRightOf) - f);
                        if (fAbs3 < fAbs2) {
                            fAbs2 = fAbs3;
                        } else {
                            offsetToLeftRightOf = offsetToLeftRightOf2;
                        }
                    } else {
                        offsetToLeftRightOf = offsetToLeftRightOf2;
                    }
                    if (fAbs2 < fAbs) {
                        i3 = offsetToLeftRightOf;
                        fAbs = fAbs2;
                    } else {
                        i3 = i11;
                    }
                }
            } else {
                i3 = i11;
            }
            float fAbs4 = Math.abs(horizontalMeasurementProvider.get(i5) - f);
            if (fAbs4 < fAbs) {
                fAbs = fAbs4;
                i3 = i5;
            }
            i4 += 2;
            i2 = 1;
            layout = this;
            lineDirections = directions;
        }
        int i14 = i3;
        if (Math.abs(horizontalMeasurementProvider.get(lineEnd) - f) > fAbs) {
            lineEnd = i14;
        }
        TextLine.recycle(textLineObtain);
        return lineEnd;
    }

    private class HorizontalMeasurementProvider {
        private float[] mHorizontals;
        private final int mLine;
        private int mLineStartOffset;
        private final boolean mPrimary;

        HorizontalMeasurementProvider(int i, boolean z) {
            this.mLine = i;
            this.mPrimary = z;
            init();
        }

        private void init() {
            if (Layout.this.getLineDirections(this.mLine) == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return;
            }
            this.mHorizontals = Layout.this.getLineHorizontals(this.mLine, false, this.mPrimary);
            this.mLineStartOffset = Layout.this.getLineStart(this.mLine);
        }

        float get(int i) {
            int i2;
            float[] fArr = this.mHorizontals;
            if (fArr == null || i < (i2 = this.mLineStartOffset) || i >= fArr.length + i2) {
                return Layout.this.getHorizontal(i, this.mPrimary);
            }
            return fArr[i - i2];
        }
    }

    public final int getLineEnd(int i) {
        return getLineStart(i + 1);
    }

    public int getLineVisibleEnd(int i) {
        return getLineVisibleEnd(i, getLineStart(i), getLineStart(i + 1));
    }

    private int getLineVisibleEnd(int i, int i2, int i3) {
        CharSequence charSequence = this.mText;
        if (i == getLineCount() - 1) {
            return i3;
        }
        while (i3 > i2) {
            int i4 = i3 - 1;
            char cCharAt = charSequence.charAt(i4);
            if (cCharAt == '\n') {
                return i4;
            }
            if (!TextLine.isLineEndSpace(cCharAt)) {
                break;
            }
            i3--;
        }
        return i3;
    }

    public final int getLineBottom(int i) {
        return getLineTop(i + 1);
    }

    public final int getLineBottomWithoutSpacing(int i) {
        return getLineTop(i + 1) - getLineExtra(i);
    }

    public final int getLineBaseline(int i) {
        return getLineTop(i + 1) - getLineDescent(i);
    }

    public final int getLineAscent(int i) {
        return getLineTop(i) - (getLineTop(i + 1) - getLineDescent(i));
    }

    public int getOffsetToLeftOf(int i) {
        return getOffsetToLeftRightOf(i, true);
    }

    public int getOffsetToRightOf(int i) {
        return getOffsetToLeftRightOf(i, false);
    }

    /* JADX WARN: Code duplicated, block: B:24:0x004b A[PHI: r1 r2
  0x004b: PHI (r1v1 int) = (r1v0 int), (r1v3 int) binds: [B:20:0x0035, B:22:0x0043] A[DONT_GENERATE, DONT_INLINE]
  0x004b: PHI (r2v1 int) = (r2v0 int), (r2v3 int) binds: [B:20:0x0035, B:22:0x0043] A[DONT_GENERATE, DONT_INLINE]] */
    private int getOffsetToLeftRightOf(int i, boolean z) {
        int i2;
        int i3;
        int i4;
        int lineForOffset = getLineForOffset(i);
        int lineStart = getLineStart(lineForOffset);
        int lineEnd = getLineEnd(lineForOffset);
        int paragraphDirection = getParagraphDirection(lineForOffset);
        boolean z2 = false;
        if (z == (paragraphDirection == -1)) {
            if (i == lineEnd) {
                if (lineForOffset >= getLineCount() - 1) {
                    return i;
                }
                lineForOffset++;
                z2 = true;
            }
        } else if (i == lineStart) {
            if (lineForOffset <= 0) {
                return i;
            }
            lineForOffset--;
            z2 = true;
        }
        if (z2) {
            lineStart = getLineStart(lineForOffset);
            lineEnd = getLineEnd(lineForOffset);
            int paragraphDirection2 = getParagraphDirection(lineForOffset);
            if (paragraphDirection2 != paragraphDirection) {
                z = !z;
                i2 = lineStart;
                i3 = lineEnd;
                i4 = paragraphDirection2;
            } else {
                i2 = lineStart;
                i3 = lineEnd;
                i4 = paragraphDirection;
            }
        } else {
            i2 = lineStart;
            i3 = lineEnd;
            i4 = paragraphDirection;
        }
        Directions lineDirections = getLineDirections(lineForOffset);
        TextLine textLineObtain = TextLine.obtain();
        textLineObtain.set(this.mPaint, this.mText, i2, i3, i4, lineDirections, false, null);
        int offsetToLeftRightOf = i2 + textLineObtain.getOffsetToLeftRightOf(i - i2, z);
        TextLine.recycle(textLineObtain);
        return offsetToLeftRightOf;
    }

    private int getOffsetAtStartOf(int i) {
        char cCharAt;
        if (i == 0) {
            return 0;
        }
        CharSequence charSequence = this.mText;
        char cCharAt2 = charSequence.charAt(i);
        if (cCharAt2 >= 56320 && cCharAt2 <= 57343 && (cCharAt = charSequence.charAt(i - 1)) >= 55296 && cCharAt <= 56319) {
            i--;
        }
        if (this.mSpannedText) {
            Spanned spanned = (Spanned) charSequence;
            ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i, i, ReplacementSpan.class);
            for (int i2 = 0; i2 < replacementSpanArr.length; i2++) {
                int spanStart = spanned.getSpanStart(replacementSpanArr[i2]);
                int spanEnd = spanned.getSpanEnd(replacementSpanArr[i2]);
                if (spanStart < i && spanEnd > i) {
                    i = spanStart;
                }
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: android.text.Layout$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            int[] iArr = new int[Alignment.values().length];
            $SwitchMap$android$text$Layout$Alignment = iArr;
            try {
                iArr[Alignment.ALIGN_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public boolean shouldClampCursor(int i) {
        int i2 = AnonymousClass1.$SwitchMap$android$text$Layout$Alignment[getParagraphAlignment(i).ordinal()];
        if (i2 != 1) {
            return i2 == 2 && getParagraphDirection(i) > 0;
        }
        return true;
    }

    public void getCursorPath(int i, Path path, CharSequence charSequence) {
        path.reset();
        int lineForOffset = getLineForOffset(i);
        int lineTop = getLineTop(lineForOffset);
        int lineBottomWithoutSpacing = getLineBottomWithoutSpacing(lineForOffset);
        boolean zShouldClampCursor = shouldClampCursor(lineForOffset);
        float primaryHorizontal = getPrimaryHorizontal(i, zShouldClampCursor) - 0.5f;
        float secondaryHorizontal = isLevelBoundary(i) ? getSecondaryHorizontal(i, zShouldClampCursor) - 0.5f : primaryHorizontal;
        int metaState = TextKeyListener.getMetaState(charSequence, 1) | TextKeyListener.getMetaState(charSequence, 2048);
        int metaState2 = TextKeyListener.getMetaState(charSequence, 2);
        int i2 = 0;
        if (metaState != 0 || metaState2 != 0) {
            i2 = (lineBottomWithoutSpacing - lineTop) >> 2;
            if (metaState2 != 0) {
                lineTop += i2;
            }
            if (metaState != 0) {
                lineBottomWithoutSpacing -= i2;
            }
        }
        if (primaryHorizontal < 0.5f) {
            primaryHorizontal = 0.5f;
        }
        if (secondaryHorizontal < 0.5f) {
            secondaryHorizontal = 0.5f;
        }
        if (Float.compare(primaryHorizontal, secondaryHorizontal) == 0) {
            path.moveTo(primaryHorizontal, lineTop);
            path.lineTo(primaryHorizontal, lineBottomWithoutSpacing);
        } else {
            path.moveTo(primaryHorizontal, lineTop);
            float f = (lineTop + lineBottomWithoutSpacing) >> 1;
            path.lineTo(primaryHorizontal, f);
            path.moveTo(secondaryHorizontal, f);
            path.lineTo(secondaryHorizontal, lineBottomWithoutSpacing);
        }
        if (metaState == 2) {
            float f2 = lineBottomWithoutSpacing;
            path.moveTo(secondaryHorizontal, f2);
            float f3 = i2;
            float f4 = lineBottomWithoutSpacing + i2;
            path.lineTo(secondaryHorizontal - f3, f4);
            path.lineTo(secondaryHorizontal, f2);
            path.lineTo(secondaryHorizontal + f3, f4);
        } else if (metaState == 1) {
            float f5 = lineBottomWithoutSpacing;
            path.moveTo(secondaryHorizontal, f5);
            float f6 = i2;
            float f7 = secondaryHorizontal - f6;
            float f8 = lineBottomWithoutSpacing + i2;
            path.lineTo(f7, f8);
            float f9 = f8 - 0.5f;
            path.moveTo(f7, f9);
            float f10 = f6 + secondaryHorizontal;
            path.lineTo(f10, f9);
            path.moveTo(f10, f8);
            path.lineTo(secondaryHorizontal, f5);
        }
        if (metaState2 == 2) {
            float f11 = lineTop;
            path.moveTo(primaryHorizontal, f11);
            float f12 = i2;
            float f13 = lineTop - i2;
            path.lineTo(primaryHorizontal - f12, f13);
            path.lineTo(primaryHorizontal, f11);
            path.lineTo(primaryHorizontal + f12, f13);
            return;
        }
        if (metaState2 == 1) {
            float f14 = lineTop;
            path.moveTo(primaryHorizontal, f14);
            float f15 = i2;
            float f16 = primaryHorizontal - f15;
            float f17 = lineTop - i2;
            path.lineTo(f16, f17);
            float f18 = 0.5f + f17;
            path.moveTo(f16, f18);
            float f19 = f15 + primaryHorizontal;
            path.lineTo(f19, f18);
            path.moveTo(f19, f17);
            path.lineTo(primaryHorizontal, f14);
        }
    }

    private void addSelection(int i, int i2, int i3, int i4, int i5, SelectionRectangleConsumer selectionRectangleConsumer) {
        int iMax;
        int iMin;
        int lineStart = getLineStart(i);
        int lineEnd = getLineEnd(i);
        Directions lineDirections = getLineDirections(i);
        if (lineEnd > lineStart && this.mText.charAt(lineEnd - 1) == '\n') {
            lineEnd--;
        }
        for (int i6 = 0; i6 < lineDirections.mDirections.length; i6 += 2) {
            int i7 = lineDirections.mDirections[i6] + lineStart;
            int i8 = i6 + 1;
            int i9 = (lineDirections.mDirections[i8] & RUN_LENGTH_MASK) + i7;
            if (i9 > lineEnd) {
                i9 = lineEnd;
            }
            if (i2 <= i9 && i3 >= i7 && (iMax = Math.max(i2, i7)) != (iMin = Math.min(i3, i9))) {
                float horizontal = getHorizontal(iMax, false, i, false);
                float horizontal2 = getHorizontal(iMin, true, i, false);
                selectionRectangleConsumer.accept(Math.min(horizontal, horizontal2), i4, Math.max(horizontal, horizontal2), i5, (lineDirections.mDirections[i8] & 67108864) != 0 ? 0 : 1);
            }
        }
    }

    public void getSelectionPath(int i, int i2, final Path path) {
        path.reset();
        getSelection(i, i2, new SelectionRectangleConsumer() { // from class: android.text.-$$Lambda$Layout$MzjK2UE2G8VG0asK8_KWY3gHAmY
            @Override // android.text.Layout.SelectionRectangleConsumer
            public final void accept(float f, float f2, float f3, float f4, int i3) {
                path.addRect(f, f2, f3, f4, Path.Direction.CW);
            }
        });
    }

    public final void getSelection(int i, int i2, SelectionRectangleConsumer selectionRectangleConsumer) {
        int i3;
        int i4;
        if (i == i2) {
            return;
        }
        if (i2 < i) {
            i4 = i;
            i3 = i2;
        } else {
            i3 = i;
            i4 = i2;
        }
        int lineForOffset = getLineForOffset(i3);
        int lineForOffset2 = getLineForOffset(i4);
        int lineTop = getLineTop(lineForOffset);
        int lineBottomWithoutSpacing = getLineBottomWithoutSpacing(lineForOffset2);
        if (lineForOffset == lineForOffset2) {
            addSelection(lineForOffset, i3, i4, lineTop, lineBottomWithoutSpacing, selectionRectangleConsumer);
            return;
        }
        float f = this.mWidth;
        addSelection(lineForOffset, i3, getLineEnd(lineForOffset), lineTop, getLineBottom(lineForOffset), selectionRectangleConsumer);
        if (getParagraphDirection(lineForOffset) == -1) {
            selectionRectangleConsumer.accept(getLineLeft(lineForOffset), lineTop, 0.0f, getLineBottom(lineForOffset), 0);
        } else {
            selectionRectangleConsumer.accept(getLineRight(lineForOffset), lineTop, f, getLineBottom(lineForOffset), 1);
        }
        while (true) {
            lineForOffset++;
            if (lineForOffset >= lineForOffset2) {
                break;
            }
            int lineTop2 = getLineTop(lineForOffset);
            int lineBottom = getLineBottom(lineForOffset);
            if (getParagraphDirection(lineForOffset) == -1) {
                selectionRectangleConsumer.accept(0.0f, lineTop2, f, lineBottom, 0);
            } else {
                selectionRectangleConsumer.accept(0.0f, lineTop2, f, lineBottom, 1);
            }
        }
        int lineTop3 = getLineTop(lineForOffset2);
        int lineBottomWithoutSpacing2 = getLineBottomWithoutSpacing(lineForOffset2);
        addSelection(lineForOffset2, getLineStart(lineForOffset2), i4, lineTop3, lineBottomWithoutSpacing2, selectionRectangleConsumer);
        if (getParagraphDirection(lineForOffset2) == -1) {
            selectionRectangleConsumer.accept(f, lineTop3, getLineRight(lineForOffset2), lineBottomWithoutSpacing2, 0);
        } else {
            selectionRectangleConsumer.accept(0.0f, lineTop3, getLineLeft(lineForOffset2), lineBottomWithoutSpacing2, 1);
        }
    }

    public final Alignment getParagraphAlignment(int i) {
        AlignmentSpan[] alignmentSpanArr;
        int length;
        Alignment alignment = this.mAlignment;
        return (!this.mSpannedText || (length = (alignmentSpanArr = (AlignmentSpan[]) getParagraphSpans((Spanned) this.mText, getLineStart(i), getLineEnd(i), AlignmentSpan.class)).length) <= 0) ? alignment : alignmentSpanArr[length - 1].getAlignment();
    }

    public final int getParagraphLeft(int i) {
        if (getParagraphDirection(i) == -1 || !this.mSpannedText) {
            return 0;
        }
        return getParagraphLeadingMargin(i);
    }

    public final int getParagraphRight(int i) {
        int i2 = this.mWidth;
        return (getParagraphDirection(i) == 1 || !this.mSpannedText) ? i2 : i2 - getParagraphLeadingMargin(i);
    }

    private int getParagraphLeadingMargin(int i) {
        if (!this.mSpannedText) {
            return 0;
        }
        Spanned spanned = (Spanned) this.mText;
        int lineStart = getLineStart(i);
        LeadingMarginSpan[] leadingMarginSpanArr = (LeadingMarginSpan[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, getLineEnd(i), LeadingMarginSpan.class), LeadingMarginSpan.class);
        if (leadingMarginSpanArr.length == 0) {
            return 0;
        }
        boolean z = lineStart == 0 || spanned.charAt(lineStart - 1) == '\n';
        for (int i2 = 0; i2 < leadingMarginSpanArr.length; i2++) {
            if (leadingMarginSpanArr[i2] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                z |= i < getLineForOffset(spanned.getSpanStart(leadingMarginSpanArr[i2])) + ((LeadingMarginSpan.LeadingMarginSpan2) leadingMarginSpanArr[i2]).getLeadingMarginLineCount();
            }
        }
        int leadingMargin = 0;
        for (LeadingMarginSpan leadingMarginSpan : leadingMarginSpanArr) {
            leadingMargin += leadingMarginSpan.getLeadingMargin(z);
        }
        return leadingMargin;
    }

    private static float measurePara(TextPaint textPaint, CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic) throws Throwable {
        int leadingMargin;
        TabStops tabStops;
        boolean z;
        TextLine textLineObtain = TextLine.obtain();
        MeasuredParagraph measuredParagraph = null;
        try {
            MeasuredParagraph measuredParagraphBuildForBidi = MeasuredParagraph.buildForBidi(charSequence, i, i2, textDirectionHeuristic, null);
            try {
                char[] chars = measuredParagraphBuildForBidi.getChars();
                int length = chars.length;
                Directions directions = measuredParagraphBuildForBidi.getDirections(0, length);
                int paragraphDir = measuredParagraphBuildForBidi.getParagraphDir();
                if (charSequence instanceof Spanned) {
                    leadingMargin = 0;
                    for (LeadingMarginSpan leadingMarginSpan : (LeadingMarginSpan[]) getParagraphSpans((Spanned) charSequence, i, i2, LeadingMarginSpan.class)) {
                        leadingMargin += leadingMarginSpan.getLeadingMargin(true);
                    }
                } else {
                    leadingMargin = 0;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= length) {
                        tabStops = null;
                        z = false;
                        break;
                    }
                    if (chars[i3] == '\t') {
                        if (!(charSequence instanceof Spanned)) {
                            z = true;
                            tabStops = null;
                            break;
                        }
                        Spanned spanned = (Spanned) charSequence;
                        TabStopSpan[] tabStopSpanArr = (TabStopSpan[]) getParagraphSpans(spanned, i, spanned.nextSpanTransition(i, i2, TabStopSpan.class), TabStopSpan.class);
                        z = true;
                        tabStops = tabStopSpanArr.length > 0 ? new TabStops(20, tabStopSpanArr) : null;
                        break;
                    }
                    i3++;
                }
                textLineObtain.set(textPaint, charSequence, i, i2, paragraphDir, directions, z, tabStops);
                float fAbs = leadingMargin + Math.abs(textLineObtain.metrics(null));
                TextLine.recycle(textLineObtain);
                if (measuredParagraphBuildForBidi != null) {
                    measuredParagraphBuildForBidi.recycle();
                }
                return fAbs;
            } catch (Throwable th) {
                th = th;
                measuredParagraph = measuredParagraphBuildForBidi;
                TextLine.recycle(textLineObtain);
                if (measuredParagraph != null) {
                    measuredParagraph.recycle();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    static class TabStops {
        private int mIncrement;
        private int mNumStops;
        private int[] mStops;

        public static float nextDefaultStop(float f, int i) {
            float f2 = i;
            return ((int) ((f + f2) / f2)) * i;
        }

        TabStops(int i, Object[] objArr) {
            reset(i, objArr);
        }

        void reset(int i, Object[] objArr) {
            this.mIncrement = i;
            int i2 = 0;
            if (objArr != null) {
                int[] iArr = this.mStops;
                int i3 = 0;
                for (Object obj : objArr) {
                    if (obj instanceof TabStopSpan) {
                        if (iArr == null) {
                            iArr = new int[10];
                        } else if (i3 == iArr.length) {
                            int[] iArr2 = new int[i3 * 2];
                            for (int i4 = 0; i4 < i3; i4++) {
                                iArr2[i4] = iArr[i4];
                            }
                            iArr = iArr2;
                        }
                        iArr[i3] = ((TabStopSpan) obj).getTabStop();
                        i3++;
                    }
                }
                if (i3 > 1) {
                    Arrays.sort(iArr, 0, i3);
                }
                if (iArr != this.mStops) {
                    this.mStops = iArr;
                }
                i2 = i3;
            }
            this.mNumStops = i2;
        }

        float nextTab(float f) {
            int i = this.mNumStops;
            if (i > 0) {
                int[] iArr = this.mStops;
                for (int i2 = 0; i2 < i; i2++) {
                    float f2 = iArr[i2];
                    if (f2 > f) {
                        return f2;
                    }
                }
            }
            return nextDefaultStop(f, this.mIncrement);
        }
    }

    static float nextTab(CharSequence charSequence, int i, int i2, float f, Object[] objArr) {
        boolean z;
        if (charSequence instanceof Spanned) {
            if (objArr == null) {
                objArr = getParagraphSpans((Spanned) charSequence, i, i2, TabStopSpan.class);
                z = true;
            } else {
                z = false;
            }
            float f2 = Float.MAX_VALUE;
            for (int i3 = 0; i3 < objArr.length; i3++) {
                if (z || (objArr[i3] instanceof TabStopSpan)) {
                    float tabStop = ((TabStopSpan) objArr[i3]).getTabStop();
                    if (tabStop < f2 && tabStop > f) {
                        f2 = tabStop;
                    }
                }
            }
            if (f2 != Float.MAX_VALUE) {
                return f2;
            }
        }
        return ((int) ((f + 20.0f) / 20.0f)) * 20;
    }

    protected final boolean isSpanned() {
        return this.mSpannedText;
    }

    static <T> T[] getParagraphSpans(Spanned spanned, int i, int i2, Class<T> cls) {
        if (i == i2 && i > 0) {
            return (T[]) ArrayUtils.emptyArray(cls);
        }
        if (spanned instanceof SpannableStringBuilder) {
            return (T[]) ((SpannableStringBuilder) spanned).getSpans(i, i2, cls, false);
        }
        return (T[]) spanned.getSpans(i, i2, cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ellipsize(int i, int i2, int i3, char[] cArr, int i4, TextUtils.TruncateAt truncateAt) {
        int ellipsisCount = getEllipsisCount(i3);
        if (ellipsisCount == 0) {
            return;
        }
        int ellipsisStart = getEllipsisStart(i3);
        int lineStart = getLineStart(i3);
        String ellipsisString = TextUtils.getEllipsisString(truncateAt);
        int length = ellipsisString.length();
        int i5 = 0;
        boolean z = ellipsisCount >= length;
        while (i5 < ellipsisCount) {
            char cCharAt = (!z || i5 >= length) ? (char) 65279 : ellipsisString.charAt(i5);
            int i6 = i5 + ellipsisStart + lineStart;
            if (i <= i6 && i6 < i2) {
                cArr[(i6 + i4) - i] = cCharAt;
            }
            i5++;
        }
    }

    public static class Directions {
        public int[] mDirections;

        public Directions(int[] iArr) {
            this.mDirections = iArr;
        }
    }

    static class Ellipsizer implements CharSequence, GetChars {
        Layout mLayout;
        TextUtils.TruncateAt mMethod;
        CharSequence mText;
        int mWidth;

        public Ellipsizer(CharSequence charSequence) {
            this.mText = charSequence;
        }

        @Override // java.lang.CharSequence
        public char charAt(int i) {
            char[] cArrObtain = TextUtils.obtain(1);
            getChars(i, i + 1, cArrObtain, 0);
            char c = cArrObtain[0];
            TextUtils.recycle(cArrObtain);
            return c;
        }

        @Override // android.text.GetChars
        public void getChars(int i, int i2, char[] cArr, int i3) {
            int lineForOffset = this.mLayout.getLineForOffset(i2);
            TextUtils.getChars(this.mText, i, i2, cArr, i3);
            for (int lineForOffset2 = this.mLayout.getLineForOffset(i); lineForOffset2 <= lineForOffset; lineForOffset2++) {
                this.mLayout.ellipsize(i, i2, lineForOffset2, cArr, i3, this.mMethod);
            }
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mText.length();
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            char[] cArr = new char[i2 - i];
            getChars(i, i2, cArr, 0);
            return new String(cArr);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            char[] cArr = new char[length()];
            getChars(0, length(), cArr, 0);
            return new String(cArr);
        }
    }

    static class SpannedEllipsizer extends Ellipsizer implements Spanned {
        private Spanned mSpanned;

        public SpannedEllipsizer(CharSequence charSequence) {
            super(charSequence);
            this.mSpanned = (Spanned) charSequence;
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int i, int i2, Class<T> cls) {
            return (T[]) this.mSpanned.getSpans(i, i2, cls);
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object obj) {
            return this.mSpanned.getSpanStart(obj);
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object obj) {
            return this.mSpanned.getSpanEnd(obj);
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object obj) {
            return this.mSpanned.getSpanFlags(obj);
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int i, int i2, Class cls) {
            return this.mSpanned.nextSpanTransition(i, i2, cls);
        }

        @Override // android.text.Layout.Ellipsizer, java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            char[] cArr = new char[i2 - i];
            getChars(i, i2, cArr, 0);
            SpannableString spannableString = new SpannableString(new String(cArr));
            TextUtils.copySpansFrom(this.mSpanned, i, i2, Object.class, spannableString, 0);
            return spannableString;
        }
    }
}
