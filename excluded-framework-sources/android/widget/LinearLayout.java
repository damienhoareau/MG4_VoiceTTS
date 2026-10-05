package android.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.TtmlUtils;
import android.security.keystore.KeyProperties;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private static boolean sCompatibilityDone = false;
    private static boolean sRemeasureWeightedChildren = true;
    private final boolean mAllowInconsistentMeasurement;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mBaselineAligned;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private int mBaselineAlignedChildIndex;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;

    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {@ViewDebug.FlagToString(equals = -1, mask = -1, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 0, mask = 0, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 48, mask = 48, name = "TOP"), @ViewDebug.FlagToString(equals = 80, mask = 80, name = "BOTTOM"), @ViewDebug.FlagToString(equals = 3, mask = 3, name = "LEFT"), @ViewDebug.FlagToString(equals = 5, mask = 5, name = "RIGHT"), @ViewDebug.FlagToString(equals = 8388611, mask = 8388611, name = "START"), @ViewDebug.FlagToString(equals = 8388613, mask = 8388613, name = "END"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "CENTER_VERTICAL"), @ViewDebug.FlagToString(equals = 112, mask = 112, name = "FILL_VERTICAL"), @ViewDebug.FlagToString(equals = 1, mask = 1, name = "CENTER_HORIZONTAL"), @ViewDebug.FlagToString(equals = 7, mask = 7, name = "FILL_HORIZONTAL"), @ViewDebug.FlagToString(equals = 17, mask = 17, name = "CENTER"), @ViewDebug.FlagToString(equals = 119, mask = 119, name = "FILL"), @ViewDebug.FlagToString(equals = 8388608, mask = 8388608, name = "RELATIVE")}, formatToHexString = true)
    private int mGravity;
    private int mLayoutDirection;
    private int[] mMaxAscent;
    private int[] mMaxDescent;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mOrientation;
    private int mShowDividers;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mTotalLength;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mUseLargestChild;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    int getLocationOffset(View view) {
        return 0;
    }

    int getNextLocationOffset(View view) {
        return 0;
    }

    int measureNullChild(int i) {
        return 0;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public LinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        this.mLayoutDirection = -1;
        if (!sCompatibilityDone && context != null) {
            sRemeasureWeightedChildren = context.getApplicationInfo().targetSdkVersion >= 28;
            sCompatibilityDone = true;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayout, i, i2);
        int i3 = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (i3 >= 0) {
            setOrientation(i3);
        }
        int i4 = typedArrayObtainStyledAttributes.getInt(0, -1);
        if (i4 >= 0) {
            setGravity(i4);
        }
        boolean z = typedArrayObtainStyledAttributes.getBoolean(2, true);
        if (!z) {
            setBaselineAligned(z);
        }
        this.mWeightSum = typedArrayObtainStyledAttributes.getFloat(4, -1.0f);
        this.mBaselineAlignedChildIndex = typedArrayObtainStyledAttributes.getInt(3, -1);
        this.mUseLargestChild = typedArrayObtainStyledAttributes.getBoolean(6, false);
        this.mShowDividers = typedArrayObtainStyledAttributes.getInt(7, 0);
        this.mDividerPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, 0);
        setDividerDrawable(typedArrayObtainStyledAttributes.getDrawable(5));
        this.mAllowInconsistentMeasurement = context.getApplicationInfo().targetSdkVersion <= 23;
        typedArrayObtainStyledAttributes.recycle();
    }

    private boolean isShowingDividers() {
        return (this.mShowDividers == 0 || this.mDivider == null) ? false : true;
    }

    public void setShowDividers(int i) {
        if (i == this.mShowDividers) {
            return;
        }
        this.mShowDividers = i;
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable drawable) {
        if (drawable == this.mDivider) {
            return;
        }
        this.mDivider = drawable;
        if (drawable != null) {
            this.mDividerWidth = drawable.getIntrinsicWidth();
            this.mDividerHeight = drawable.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    public void setDividerPadding(int i) {
        if (i == this.mDividerPadding) {
            return;
        }
        this.mDividerPadding = i;
        if (isShowingDividers()) {
            requestLayout();
            invalidate();
        }
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int bottom;
        int virtualChildCount = getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View lastNonGoneChild = getLastNonGoneChild();
            if (lastNonGoneChild == null) {
                bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                bottom = lastNonGoneChild.getBottom() + ((LayoutParams) lastNonGoneChild.getLayoutParams()).bottomMargin;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    private View getLastNonGoneChild() {
        for (int virtualChildCount = getVirtualChildCount() - 1; virtualChildCount >= 0; virtualChildCount--) {
            View virtualChildAt = getVirtualChildAt(virtualChildCount);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                return virtualChildAt;
            }
        }
        return null;
    }

    void drawDividersHorizontal(Canvas canvas) {
        int right;
        int left;
        int i;
        int left2;
        int virtualChildCount = getVirtualChildCount();
        boolean zIsLayoutRtl = isLayoutRtl();
        for (int i2 = 0; i2 < virtualChildCount; i2++) {
            View virtualChildAt = getVirtualChildAt(i2);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i2)) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (zIsLayoutRtl) {
                    left2 = virtualChildAt.getRight() + layoutParams.rightMargin;
                } else {
                    left2 = (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, left2);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View lastNonGoneChild = getLastNonGoneChild();
            if (lastNonGoneChild != null) {
                LayoutParams layoutParams2 = (LayoutParams) lastNonGoneChild.getLayoutParams();
                if (zIsLayoutRtl) {
                    left = lastNonGoneChild.getLeft() - layoutParams2.leftMargin;
                    i = this.mDividerWidth;
                    right = left - i;
                } else {
                    right = lastNonGoneChild.getRight() + layoutParams2.rightMargin;
                }
            } else if (zIsLayoutRtl) {
                right = getPaddingLeft();
            } else {
                left = getWidth() - getPaddingRight();
                i = this.mDividerWidth;
                right = left - i;
            }
            drawVerticalDivider(canvas, right);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    @RemotableViewMethod
    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    @RemotableViewMethod
    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    @Override // android.view.View
    public int getBaseline() {
        int i;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        int childCount = getChildCount();
        int i2 = this.mBaselineAlignedChildIndex;
        if (childCount <= i2) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View childAt = getChildAt(i2);
        int baseline = childAt.getBaseline();
        if (baseline == -1) {
            if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            }
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        int i3 = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (i = this.mGravity & 112) != 48) {
            if (i == 16) {
                i3 += ((((this.mBottom - this.mTop) - this.mPaddingTop) - this.mPaddingBottom) - this.mTotalLength) / 2;
            } else if (i == 80) {
                i3 = ((this.mBottom - this.mTop) - this.mPaddingBottom) - this.mTotalLength;
            }
        }
        return i3 + ((LayoutParams) childAt.getLayoutParams()).topMargin + baseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    @RemotableViewMethod
    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
        }
        this.mBaselineAlignedChildIndex = i;
    }

    View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    @RemotableViewMethod
    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mOrientation == 1) {
            measureVertical(i, i2);
        } else {
            measureHorizontal(i, i2);
        }
    }

    protected boolean hasDividerBeforeChildAt(int i) {
        if (i == getVirtualChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        if (allViewsAreGoneBefore(i)) {
            return (this.mShowDividers & 1) != 0;
        }
        return (this.mShowDividers & 2) != 0;
    }

    private boolean allViewsAreGoneBefore(int i) {
        for (int i2 = i - 1; i2 >= 0; i2--) {
            View virtualChildAt = getVirtualChildAt(i2);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:136:0x02a3  */
    /* JADX WARN: Code duplicated, block: B:138:0x02a7  */
    /* JADX WARN: Code duplicated, block: B:140:0x02ab  */
    /* JADX WARN: Code duplicated, block: B:143:0x02b0  */
    /* JADX WARN: Code duplicated, block: B:144:0x02b3  */
    /* JADX WARN: Code duplicated, block: B:157:0x0317  */
    /* JADX WARN: Code duplicated, block: B:163:0x0326  */
    void measureVertical(int i, int i2) {
        int iMax;
        int i3;
        float f;
        float f2;
        int i4;
        int i5;
        boolean z;
        boolean z2;
        int i6;
        int i7;
        int i8;
        boolean z3;
        int i9;
        int i10;
        int i11;
        int i12;
        LayoutParams layoutParams;
        int i13;
        View view;
        int i14;
        boolean z4;
        int iMax2;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int i15 = this.mBaselineAlignedChildIndex;
        boolean z5 = this.mUseLargestChild;
        int i16 = 0;
        int i17 = 0;
        int iMax3 = 0;
        int childrenSkipCount = 0;
        int i18 = 0;
        boolean z6 = false;
        int iMax4 = 0;
        int iCombineMeasuredStates = 0;
        boolean z7 = false;
        float f3 = 0.0f;
        boolean z8 = true;
        int iMax5 = Integer.MIN_VALUE;
        while (true) {
            int i19 = 8;
            if (childrenSkipCount < virtualChildCount) {
                int i20 = iMax3;
                View virtualChildAt = getVirtualChildAt(childrenSkipCount);
                if (virtualChildAt == null) {
                    this.mTotalLength += measureNullChild(childrenSkipCount);
                } else {
                    if (virtualChildAt.getVisibility() == 8) {
                        childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                    } else {
                        int i21 = i16 + 1;
                        if (hasDividerBeforeChildAt(childrenSkipCount)) {
                            this.mTotalLength += this.mDividerHeight;
                        }
                        LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                        float f4 = f3 + layoutParams2.weight;
                        if (layoutParams2.height != 0 || layoutParams2.weight <= 0.0f) {
                            i8 = 1073741824;
                            z3 = false;
                        } else {
                            z3 = true;
                            i8 = 1073741824;
                        }
                        if (mode2 == i8 && z3) {
                            int i22 = this.mTotalLength;
                            this.mTotalLength = Math.max(i22, layoutParams2.topMargin + i22 + layoutParams2.bottomMargin);
                            i12 = childrenSkipCount;
                            layoutParams = layoutParams2;
                            i13 = mode2;
                            z6 = true;
                            i11 = i20;
                            iMax5 = iMax5;
                            i9 = i21;
                            i10 = i17;
                            view = virtualChildAt;
                        } else {
                            int i23 = iMax5;
                            if (z3) {
                                layoutParams2.height = -2;
                            }
                            i9 = i21;
                            i10 = i17;
                            i11 = i20;
                            i12 = childrenSkipCount;
                            layoutParams = layoutParams2;
                            i13 = mode2;
                            measureChildBeforeLayout(virtualChildAt, childrenSkipCount, i, 0, i2, f4 == 0.0f ? this.mTotalLength : 0);
                            int measuredHeight = virtualChildAt.getMeasuredHeight();
                            if (z3) {
                                layoutParams.height = 0;
                                i18 += measuredHeight;
                            }
                            int i24 = this.mTotalLength;
                            view = virtualChildAt;
                            this.mTotalLength = Math.max(i24, i24 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + getNextLocationOffset(view));
                            iMax5 = i23;
                            if (z5) {
                                iMax5 = Math.max(measuredHeight, iMax5);
                            }
                        }
                        if (i15 >= 0) {
                            i14 = i12;
                            if (i15 == i14 + 1) {
                                this.mBaselineChildTop = this.mTotalLength;
                            }
                        } else {
                            i14 = i12;
                        }
                        if (i14 < i15 && layoutParams.weight > 0.0f) {
                            throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                        }
                        if (mode == 1073741824 || layoutParams.width != -1) {
                            z4 = false;
                        } else {
                            z4 = true;
                            z7 = true;
                        }
                        int i25 = layoutParams.leftMargin + layoutParams.rightMargin;
                        int measuredWidth = view.getMeasuredWidth() + i25;
                        iMax4 = Math.max(iMax4, measuredWidth);
                        iCombineMeasuredStates = combineMeasuredStates(iCombineMeasuredStates, view.getMeasuredState());
                        z8 = z8 && layoutParams.width == -1;
                        if (layoutParams.weight > 0.0f) {
                            if (!z4) {
                                i25 = measuredWidth;
                            }
                            iMax2 = Math.max(i10, i25);
                            iMax3 = i11;
                        } else {
                            if (!z4) {
                                i25 = measuredWidth;
                            }
                            iMax3 = Math.max(i11, i25);
                            iMax2 = i10;
                        }
                        childrenSkipCount = i14 + getChildrenSkipCount(view, i14);
                        i17 = iMax2;
                        f3 = f4;
                        i16 = i9;
                    }
                    childrenSkipCount++;
                    mode2 = i13;
                }
                i13 = mode2;
                iMax3 = i20;
                childrenSkipCount++;
                mode2 = i13;
            } else {
                int i26 = i17;
                int i27 = iMax3;
                int i28 = mode2;
                int iCombineMeasuredStates2 = iCombineMeasuredStates;
                if (i16 > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
                    this.mTotalLength += this.mDividerHeight;
                }
                int i29 = i28;
                if (z5 && (i29 == Integer.MIN_VALUE || i29 == 0)) {
                    this.mTotalLength = 0;
                    int childrenSkipCount2 = 0;
                    while (childrenSkipCount2 < virtualChildCount) {
                        View virtualChildAt2 = getVirtualChildAt(childrenSkipCount2);
                        if (virtualChildAt2 == null) {
                            this.mTotalLength += measureNullChild(childrenSkipCount2);
                        } else if (virtualChildAt2.getVisibility() == i19) {
                            childrenSkipCount2 += getChildrenSkipCount(virtualChildAt2, childrenSkipCount2);
                        } else {
                            LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                            int i30 = this.mTotalLength;
                            this.mTotalLength = Math.max(i30, i30 + iMax5 + layoutParams3.topMargin + layoutParams3.bottomMargin + getNextLocationOffset(virtualChildAt2));
                        }
                        childrenSkipCount2++;
                        i19 = 8;
                    }
                }
                int i31 = this.mTotalLength + this.mPaddingTop + this.mPaddingBottom;
                this.mTotalLength = i31;
                int iResolveSizeAndState = resolveSizeAndState(Math.max(i31, getSuggestedMinimumHeight()), i2, 0);
                int i32 = (16777215 & iResolveSizeAndState) - this.mTotalLength;
                if (this.mAllowInconsistentMeasurement) {
                    i18 = 0;
                }
                int i33 = i32 + i18;
                if (z6 || ((sRemeasureWeightedChildren || i33 != 0) && f3 > 0.0f)) {
                    float f5 = this.mWeightSum;
                    if (f5 > 0.0f) {
                        f3 = f5;
                    }
                    this.mTotalLength = 0;
                    int i34 = i33;
                    int iMax6 = iMax4;
                    float f6 = f3;
                    int i35 = 0;
                    while (i35 < virtualChildCount) {
                        View virtualChildAt3 = getVirtualChildAt(i35);
                        if (virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8) {
                            i29 = i29;
                            i3 = iMax5;
                            f = f6;
                        } else {
                            LayoutParams layoutParams4 = (LayoutParams) virtualChildAt3.getLayoutParams();
                            float f7 = layoutParams4.weight;
                            if (f7 > 0.0f) {
                                i3 = iMax5;
                                int measuredHeight2 = (int) ((i34 * f7) / f6);
                                i34 -= measuredHeight2;
                                float f8 = f6 - f7;
                                if (this.mUseLargestChild) {
                                    i6 = 1073741824;
                                    if (i29 != 1073741824) {
                                        i7 = i3;
                                    } else {
                                        if (layoutParams4.height == 0) {
                                            if (this.mAllowInconsistentMeasurement) {
                                                i6 = 1073741824;
                                                if (i29 != 1073741824) {
                                                }
                                            } else {
                                                i6 = 1073741824;
                                            }
                                            i7 = measuredHeight2;
                                        } else {
                                            i6 = 1073741824;
                                        }
                                        measuredHeight2 = virtualChildAt3.getMeasuredHeight() + measuredHeight2;
                                        i7 = measuredHeight2;
                                    }
                                } else {
                                    if (layoutParams4.height == 0) {
                                        if (this.mAllowInconsistentMeasurement) {
                                            i6 = 1073741824;
                                            if (i29 != 1073741824) {
                                            }
                                        } else {
                                            i6 = 1073741824;
                                        }
                                        i7 = measuredHeight2;
                                    } else {
                                        i6 = 1073741824;
                                    }
                                    measuredHeight2 = virtualChildAt3.getMeasuredHeight() + measuredHeight2;
                                    i7 = measuredHeight2;
                                }
                                virtualChildAt3.measure(getChildMeasureSpec(i, this.mPaddingLeft + this.mPaddingRight + layoutParams4.leftMargin + layoutParams4.rightMargin, layoutParams4.width), View.MeasureSpec.makeMeasureSpec(Math.max(0, i7), i6));
                                iCombineMeasuredStates2 = combineMeasuredStates(iCombineMeasuredStates2, virtualChildAt3.getMeasuredState() & (-256));
                                f2 = f8;
                            } else {
                                i29 = i29;
                                i3 = iMax5;
                                f2 = f6;
                            }
                            int i36 = layoutParams4.leftMargin + layoutParams4.rightMargin;
                            int measuredWidth2 = virtualChildAt3.getMeasuredWidth() + i36;
                            iMax6 = Math.max(iMax6, measuredWidth2);
                            float f9 = f2;
                            if (mode != 1073741824) {
                                i4 = i36;
                                i5 = -1;
                                z = layoutParams4.width == -1;
                                if (z) {
                                    measuredWidth2 = i4;
                                }
                                int iMax7 = Math.max(i27, measuredWidth2);
                                if (z8 || layoutParams4.width != i5) {
                                    z2 = false;
                                } else {
                                    z2 = true;
                                }
                                int i37 = this.mTotalLength;
                                this.mTotalLength = Math.max(i37, i37 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                                z8 = z2;
                                i27 = iMax7;
                                f = f9;
                            } else {
                                i4 = i36;
                                i5 = -1;
                            }
                            if (z) {
                                measuredWidth2 = i4;
                            }
                            int iMax8 = Math.max(i27, measuredWidth2);
                            if (z8) {
                                z2 = false;
                            } else {
                                z2 = false;
                            }
                            int i38 = this.mTotalLength;
                            this.mTotalLength = Math.max(i38, i38 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                            z8 = z2;
                            i27 = iMax8;
                            f = f9;
                        }
                        i35++;
                        f6 = f;
                        i29 = i29;
                        iMax5 = i3;
                    }
                    this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
                    iMax = i27;
                    iMax4 = iMax6;
                } else {
                    iMax = Math.max(i27, i26);
                    if (z5 && i29 != 1073741824) {
                        for (int i39 = 0; i39 < virtualChildCount; i39++) {
                            View virtualChildAt4 = getVirtualChildAt(i39);
                            if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                                virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(iMax5, 1073741824));
                            }
                        }
                    }
                }
                if (z8 || mode == 1073741824) {
                    iMax = iMax4;
                }
                setMeasuredDimension(resolveSizeAndState(Math.max(iMax + this.mPaddingLeft + this.mPaddingRight, getSuggestedMinimumWidth()), i, iCombineMeasuredStates2), iResolveSizeAndState);
                if (z7) {
                    forceUniformWidth(virtualChildCount, i2);
                    return;
                }
                return;
            }
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, iMakeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:167:0x039d  */
    /* JADX WARN: Code duplicated, block: B:169:0x03a1  */
    /* JADX WARN: Code duplicated, block: B:171:0x03a5  */
    /* JADX WARN: Code duplicated, block: B:174:0x03aa  */
    /* JADX WARN: Code duplicated, block: B:175:0x03ad  */
    /* JADX WARN: Code duplicated, block: B:204:0x044f  */
    /* JADX WARN: Code duplicated, block: B:205:0x0452  */
    /* JADX WARN: Code duplicated, block: B:67:0x01a2  */
    /* JADX WARN: Code duplicated, block: B:68:0x01a5  */
    /* JADX WARN: Code duplicated, block: B:75:0x01d3  */
    /* JADX WARN: Code duplicated, block: B:78:0x01db A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:80:0x01de  */
    /* JADX WARN: Code duplicated, block: B:82:0x01e6  */
    /* JADX WARN: Code duplicated, block: B:85:0x01eb  */
    void measureHorizontal(int i, int i2) {
        int[] iArr;
        int iMax;
        int i3;
        int i4;
        int i5;
        int baseline;
        int i6;
        int i7;
        int i8;
        int i9;
        boolean z;
        int i10;
        int measuredHeight;
        int baseline2;
        int i11;
        boolean z2 = false;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] iArr2 = this.mMaxAscent;
        int[] iArr3 = this.mMaxDescent;
        iArr2[3] = -1;
        iArr2[2] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        iArr3[3] = -1;
        iArr3[2] = -1;
        iArr3[1] = -1;
        iArr3[0] = -1;
        boolean z3 = this.mBaselineAligned;
        boolean z4 = this.mUseLargestChild;
        int i12 = 1073741824;
        boolean z5 = mode == 1073741824;
        int childrenSkipCount = 0;
        int i13 = 0;
        int iMax2 = 0;
        int i14 = 0;
        boolean z6 = false;
        int iMax3 = 0;
        int iMax4 = 0;
        int iCombineMeasuredStates = 0;
        boolean z7 = false;
        boolean z8 = true;
        float f = 0.0f;
        int iMax5 = Integer.MIN_VALUE;
        while (true) {
            iArr = iArr3;
            if (childrenSkipCount >= virtualChildCount) {
                break;
            }
            View virtualChildAt = getVirtualChildAt(childrenSkipCount);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(childrenSkipCount);
            } else {
                if (virtualChildAt.getVisibility() == 8) {
                    childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                } else {
                    i13++;
                    if (hasDividerBeforeChildAt(childrenSkipCount)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    f += layoutParams.weight;
                    boolean z9 = (layoutParams.width != 0 || layoutParams.weight <= 0.0f) ? z2 : true;
                    if (mode == i12 && z9) {
                        if (z5) {
                            this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                        } else {
                            int i15 = this.mTotalLength;
                            this.mTotalLength = Math.max(i15, layoutParams.leftMargin + i15 + layoutParams.rightMargin);
                        }
                        if (z3) {
                            virtualChildAt.measure(View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(i), 0), View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
                        } else {
                            z6 = true;
                        }
                        i8 = 1073741824;
                    } else {
                        if (z9) {
                            layoutParams.width = -2;
                        }
                        int i16 = childrenSkipCount;
                        childrenSkipCount = i16;
                        z4 = z4;
                        z3 = z3;
                        layoutParams = layoutParams;
                        measureChildBeforeLayout(virtualChildAt, i16, i, f == 0.0f ? this.mTotalLength : 0, i2, 0);
                        int measuredWidth = virtualChildAt.getMeasuredWidth();
                        if (z9) {
                            layoutParams.width = 0;
                            i14 += measuredWidth;
                        }
                        if (z5) {
                            virtualChildAt = virtualChildAt;
                            this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                        } else {
                            virtualChildAt = virtualChildAt;
                            int i17 = this.mTotalLength;
                            this.mTotalLength = Math.max(i17, i17 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt));
                        }
                        if (z4) {
                            iMax5 = Math.max(measuredWidth, iMax5);
                        }
                        i8 = 1073741824;
                    }
                    if (mode2 != i8) {
                        i9 = -1;
                        if (layoutParams.height == -1) {
                            z = true;
                            z7 = true;
                        }
                        i10 = layoutParams.topMargin + layoutParams.bottomMargin;
                        measuredHeight = virtualChildAt.getMeasuredHeight() + i10;
                        iCombineMeasuredStates = combineMeasuredStates(iCombineMeasuredStates, virtualChildAt.getMeasuredState());
                        if (z3 && (baseline2 = virtualChildAt.getBaseline()) != i9) {
                            if (layoutParams.gravity < 0) {
                                i11 = this.mGravity;
                            } else {
                                i11 = layoutParams.gravity;
                            }
                            int i18 = (((i11 & 112) >> 4) & (-2)) >> 1;
                            iArr2[i18] = Math.max(iArr2[i18], baseline2);
                            iArr[i18] = Math.max(iArr[i18], measuredHeight - baseline2);
                        }
                        iMax2 = Math.max(iMax2, measuredHeight);
                        if (z8 || layoutParams.height != -1) {
                            z8 = false;
                        } else {
                            z8 = true;
                        }
                        if (layoutParams.weight > 0.0f) {
                            if (!z) {
                                i10 = measuredHeight;
                            }
                            iMax4 = Math.max(iMax4, i10);
                        } else {
                            int i19 = iMax4;
                            if (!z) {
                                i10 = measuredHeight;
                            }
                            iMax3 = Math.max(iMax3, i10);
                            iMax4 = i19;
                        }
                        int i20 = childrenSkipCount;
                        childrenSkipCount = getChildrenSkipCount(virtualChildAt, i20) + i20;
                    } else {
                        i9 = -1;
                    }
                    z = false;
                    i10 = layoutParams.topMargin + layoutParams.bottomMargin;
                    measuredHeight = virtualChildAt.getMeasuredHeight() + i10;
                    iCombineMeasuredStates = combineMeasuredStates(iCombineMeasuredStates, virtualChildAt.getMeasuredState());
                    if (z3) {
                        if (layoutParams.gravity < 0) {
                            i11 = this.mGravity;
                        } else {
                            i11 = layoutParams.gravity;
                        }
                        int i110 = (((i11 & 112) >> 4) & (-2)) >> 1;
                        iArr2[i110] = Math.max(iArr2[i110], baseline2);
                        iArr[i110] = Math.max(iArr[i110], measuredHeight - baseline2);
                    }
                    iMax2 = Math.max(iMax2, measuredHeight);
                    if (z8) {
                        z8 = false;
                    } else {
                        z8 = false;
                    }
                    if (layoutParams.weight > 0.0f) {
                        if (!z) {
                            i10 = measuredHeight;
                        }
                        iMax4 = Math.max(iMax4, i10);
                    } else {
                        int i111 = iMax4;
                        if (!z) {
                            i10 = measuredHeight;
                        }
                        iMax3 = Math.max(iMax3, i10);
                        iMax4 = i111;
                    }
                    int i21 = childrenSkipCount;
                    childrenSkipCount = getChildrenSkipCount(virtualChildAt, i21) + i21;
                }
                childrenSkipCount++;
                iArr3 = iArr;
                z4 = z4;
                z3 = z3;
                i12 = 1073741824;
                z2 = false;
            }
            z4 = z4;
            z3 = z3;
            childrenSkipCount++;
            iArr3 = iArr;
            z4 = z4;
            z3 = z3;
            i12 = 1073741824;
            z2 = false;
        }
        boolean z10 = z4;
        boolean z11 = z3;
        int i22 = iMax2;
        int iMax6 = iMax3;
        int i23 = iMax4;
        int i24 = iCombineMeasuredStates;
        if (i13 > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        int iMax7 = (iArr2[1] == -1 && iArr2[0] == -1 && iArr2[2] == -1 && iArr2[3] == -1) ? i22 : Math.max(i22, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
        if (z10 && (mode == Integer.MIN_VALUE || mode == 0)) {
            this.mTotalLength = 0;
            int childrenSkipCount2 = 0;
            while (childrenSkipCount2 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(childrenSkipCount2);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(childrenSkipCount2);
                } else if (virtualChildAt2.getVisibility() == 8) {
                    childrenSkipCount2 += getChildrenSkipCount(virtualChildAt2, childrenSkipCount2);
                } else {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    if (z5) {
                        this.mTotalLength += layoutParams2.leftMargin + iMax5 + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2);
                    } else {
                        int i25 = this.mTotalLength;
                        this.mTotalLength = Math.max(i25, i25 + iMax5 + layoutParams2.leftMargin + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2));
                    }
                    childrenSkipCount2++;
                    iMax7 = iMax7;
                }
                childrenSkipCount2++;
                iMax7 = iMax7;
            }
        }
        int i26 = iMax7;
        int i27 = this.mTotalLength + this.mPaddingLeft + this.mPaddingRight;
        this.mTotalLength = i27;
        int iResolveSizeAndState = resolveSizeAndState(Math.max(i27, getSuggestedMinimumWidth()), i, 0);
        int i28 = (16777215 & iResolveSizeAndState) - this.mTotalLength;
        if (this.mAllowInconsistentMeasurement) {
            i14 = 0;
        }
        int i29 = i28 + i14;
        if (z6 || ((sRemeasureWeightedChildren || i29 != 0) && f > 0.0f)) {
            float f2 = this.mWeightSum;
            if (f2 > 0.0f) {
                f = f2;
            }
            iArr2[3] = -1;
            iArr2[2] = -1;
            iArr2[1] = -1;
            iArr2[0] = -1;
            iArr[3] = -1;
            iArr[2] = -1;
            iArr[1] = -1;
            iArr[0] = -1;
            this.mTotalLength = 0;
            int iCombineMeasuredStates2 = i24;
            int iMax8 = -1;
            int i30 = 0;
            while (i30 < virtualChildCount) {
                View virtualChildAt3 = getVirtualChildAt(i30);
                if (virtualChildAt3 != null) {
                    i4 = iMax5;
                    if (virtualChildAt3.getVisibility() != 8) {
                        LayoutParams layoutParams3 = (LayoutParams) virtualChildAt3.getLayoutParams();
                        float f3 = layoutParams3.weight;
                        if (f3 > 0.0f) {
                            int measuredWidth2 = (int) ((i29 * f3) / f);
                            int i31 = i29 - measuredWidth2;
                            f -= f3;
                            if (this.mUseLargestChild) {
                                i7 = 1073741824;
                                if (mode != 1073741824) {
                                    measuredWidth2 = i4;
                                } else {
                                    if (layoutParams3.width == 0) {
                                        i7 = 1073741824;
                                    } else if (this.mAllowInconsistentMeasurement) {
                                        i7 = 1073741824;
                                        if (mode != 1073741824) {
                                        }
                                    } else {
                                        i7 = 1073741824;
                                    }
                                    measuredWidth2 = virtualChildAt3.getMeasuredWidth() + measuredWidth2;
                                }
                            } else {
                                if (layoutParams3.width == 0) {
                                    i7 = 1073741824;
                                } else if (this.mAllowInconsistentMeasurement) {
                                    i7 = 1073741824;
                                    if (mode != 1073741824) {
                                    }
                                } else {
                                    i7 = 1073741824;
                                }
                                measuredWidth2 = virtualChildAt3.getMeasuredWidth() + measuredWidth2;
                            }
                            virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(Math.max(0, measuredWidth2), i7), getChildMeasureSpec(i2, this.mPaddingTop + this.mPaddingBottom + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height));
                            iCombineMeasuredStates2 = combineMeasuredStates(iCombineMeasuredStates2, virtualChildAt3.getMeasuredState() & (-16777216));
                            i29 = i31;
                        }
                        if (z5) {
                            this.mTotalLength += virtualChildAt3.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3);
                        } else {
                            int i32 = this.mTotalLength;
                            this.mTotalLength = Math.max(i32, virtualChildAt3.getMeasuredWidth() + i32 + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3));
                        }
                        boolean z12 = mode2 != 1073741824 && layoutParams3.height == -1;
                        int i33 = layoutParams3.topMargin + layoutParams3.bottomMargin;
                        int measuredHeight2 = virtualChildAt3.getMeasuredHeight() + i33;
                        iMax8 = Math.max(iMax8, measuredHeight2);
                        if (!z12) {
                            i33 = measuredHeight2;
                        }
                        iMax6 = Math.max(iMax6, i33);
                        if (z8) {
                            i5 = -1;
                            boolean z13 = layoutParams3.height == -1;
                            if (z11 && (baseline = virtualChildAt3.getBaseline()) != i5) {
                                if (layoutParams3.gravity < 0) {
                                    i6 = this.mGravity;
                                } else {
                                    i6 = layoutParams3.gravity;
                                }
                                int i34 = (((i6 & 112) >> 4) & (-2)) >> 1;
                                iArr2[i34] = Math.max(iArr2[i34], baseline);
                                iArr[i34] = Math.max(iArr[i34], measuredHeight2 - baseline);
                            }
                            z8 = z13;
                            i29 = i29;
                        } else {
                            i5 = -1;
                        }
                        if (z11) {
                            if (layoutParams3.gravity < 0) {
                                i6 = this.mGravity;
                            } else {
                                i6 = layoutParams3.gravity;
                            }
                            int i35 = (((i6 & 112) >> 4) & (-2)) >> 1;
                            iArr2[i35] = Math.max(iArr2[i35], baseline);
                            iArr[i35] = Math.max(iArr[i35], measuredHeight2 - baseline);
                        }
                        z8 = z13;
                        i29 = i29;
                    }
                } else {
                    i4 = iMax5;
                }
                i30++;
                iMax5 = i4;
            }
            this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
            iMax = (iArr2[1] == -1 && iArr2[0] == -1 && iArr2[2] == -1 && iArr2[3] == -1) ? iMax8 : Math.max(iMax8, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
            i3 = iCombineMeasuredStates2;
        } else {
            iMax6 = Math.max(iMax6, i23);
            if (z10 && mode != 1073741824) {
                for (int i36 = 0; i36 < virtualChildCount; i36++) {
                    View virtualChildAt4 = getVirtualChildAt(i36);
                    if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                        virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(iMax5, 1073741824), View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredHeight(), 1073741824));
                    }
                }
            }
            i3 = i24;
            iMax = i26;
        }
        if (z8 || mode2 == 1073741824) {
            iMax6 = iMax;
        }
        setMeasuredDimension(iResolveSizeAndState | (i3 & (-16777216)), resolveSizeAndState(Math.max(iMax6 + this.mPaddingTop + this.mPaddingBottom, getSuggestedMinimumHeight()), i2, i3 << 16));
        if (z7) {
            forceUniformHeight(virtualChildCount, i);
        }
    }

    private void forceUniformHeight(int i, int i2) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, iMakeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation == 1) {
            layoutVertical(i, i2, i3, i4);
        } else {
            layoutHorizontal(i, i2, i3, i4);
        }
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0093  */
    void layoutVertical(int i, int i2, int i3, int i4) {
        int iMeasureNullChild;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9 = this.mPaddingLeft;
        int i10 = i3 - i;
        int i11 = i10 - this.mPaddingRight;
        int i12 = (i10 - i9) - this.mPaddingRight;
        int virtualChildCount = getVirtualChildCount();
        int i13 = this.mGravity;
        int i14 = i13 & 112;
        int i15 = i13 & 8388615;
        if (i14 == 16) {
            iMeasureNullChild = this.mPaddingTop + (((i4 - i2) - this.mTotalLength) / 2);
        } else if (i14 == 80) {
            iMeasureNullChild = ((this.mPaddingTop + i4) - i2) - this.mTotalLength;
        } else {
            iMeasureNullChild = this.mPaddingTop;
        }
        int childrenSkipCount = 0;
        while (childrenSkipCount < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(childrenSkipCount);
            if (virtualChildAt == null) {
                iMeasureNullChild += measureNullChild(childrenSkipCount);
            } else {
                if (virtualChildAt.getVisibility() != 8) {
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    int measuredHeight = virtualChildAt.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    int i16 = layoutParams.gravity;
                    if (i16 < 0) {
                        i16 = i15;
                    }
                    int absoluteGravity = Gravity.getAbsoluteGravity(i16, getLayoutDirection()) & 7;
                    if (absoluteGravity == 1) {
                        i5 = ((i12 - measuredWidth) / 2) + i9 + layoutParams.leftMargin;
                        i6 = layoutParams.rightMargin;
                    } else {
                        if (absoluteGravity == 5) {
                            i5 = i11 - measuredWidth;
                            i6 = layoutParams.rightMargin;
                        } else {
                            i7 = layoutParams.leftMargin + i9;
                        }
                        int i17 = i7;
                        if (hasDividerBeforeChildAt(childrenSkipCount)) {
                            iMeasureNullChild += this.mDividerHeight;
                        }
                        int i18 = iMeasureNullChild + layoutParams.topMargin;
                        setChildFrame(virtualChildAt, i17, i18 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                        int nextLocationOffset = i18 + measuredHeight + layoutParams.bottomMargin + getNextLocationOffset(virtualChildAt);
                        childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                        iMeasureNullChild = nextLocationOffset;
                        i8 = 1;
                    }
                    i7 = i5 - i6;
                    int i19 = i7;
                    if (hasDividerBeforeChildAt(childrenSkipCount)) {
                        iMeasureNullChild += this.mDividerHeight;
                    }
                    int i110 = iMeasureNullChild + layoutParams.topMargin;
                    setChildFrame(virtualChildAt, i19, i110 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                    int nextLocationOffset2 = i110 + measuredHeight + layoutParams.bottomMargin + getNextLocationOffset(virtualChildAt);
                    childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                    iMeasureNullChild = nextLocationOffset2;
                    i8 = 1;
                }
                childrenSkipCount += i8;
            }
            i8 = 1;
            childrenSkipCount += i8;
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        if (i != this.mLayoutDirection) {
            this.mLayoutDirection = i;
            if (this.mOrientation == 0) {
                requestLayout();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00a3  */
    /* JADX WARN: Code duplicated, block: B:32:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:34:0x00b0  */
    /* JADX WARN: Code duplicated, block: B:36:0x00b4  */
    /* JADX WARN: Code duplicated, block: B:37:0x00b7  */
    /* JADX WARN: Code duplicated, block: B:39:0x00bf  */
    /* JADX WARN: Code duplicated, block: B:40:0x00cd  */
    /* JADX WARN: Code duplicated, block: B:42:0x00d3  */
    /* JADX WARN: Code duplicated, block: B:44:0x00df  */
    /* JADX WARN: Code duplicated, block: B:47:0x00f3  */
    void layoutHorizontal(int i, int i2, int i3, int i4) {
        int iMeasureNullChild;
        int i5;
        int i6;
        boolean z;
        int baseline;
        int i7;
        int i8;
        int measuredHeight;
        boolean zIsLayoutRtl = isLayoutRtl();
        int i9 = this.mPaddingTop;
        int i10 = i4 - i2;
        int i11 = i10 - this.mPaddingBottom;
        int i12 = (i10 - i9) - this.mPaddingBottom;
        int virtualChildCount = getVirtualChildCount();
        int i13 = this.mGravity;
        int i14 = i13 & 112;
        boolean z2 = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        int absoluteGravity = Gravity.getAbsoluteGravity(8388615 & i13, getLayoutDirection());
        boolean z3 = true;
        if (absoluteGravity == 1) {
            iMeasureNullChild = this.mPaddingLeft + (((i3 - i) - this.mTotalLength) / 2);
        } else if (absoluteGravity == 5) {
            iMeasureNullChild = ((this.mPaddingLeft + i3) - i) - this.mTotalLength;
        } else {
            iMeasureNullChild = this.mPaddingLeft;
        }
        if (zIsLayoutRtl) {
            i5 = virtualChildCount - 1;
            i6 = -1;
        } else {
            i5 = 0;
            i6 = 1;
        }
        int childrenSkipCount = 0;
        while (childrenSkipCount < virtualChildCount) {
            int i15 = i5 + (i6 * childrenSkipCount);
            View virtualChildAt = getVirtualChildAt(i15);
            if (virtualChildAt == null) {
                iMeasureNullChild += measureNullChild(i15);
                z = z3;
            } else {
                if (virtualChildAt.getVisibility() != 8) {
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    int measuredHeight2 = virtualChildAt.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    int i16 = childrenSkipCount;
                    if (z2) {
                        virtualChildCount = virtualChildCount;
                        baseline = layoutParams.height != -1 ? virtualChildAt.getBaseline() : -1;
                        i7 = layoutParams.gravity;
                        if (i7 < 0) {
                            i7 = i14;
                        }
                        i8 = i7 & 112;
                        i14 = i14;
                        if (i8 != 16) {
                            if (i8 != 48) {
                                measuredHeight = layoutParams.topMargin + i9;
                                if (baseline != -1) {
                                    z = true;
                                    measuredHeight += iArr[1] - baseline;
                                }
                            } else if (i8 != 80) {
                                measuredHeight = i9;
                            } else {
                                measuredHeight = (i11 - measuredHeight2) - layoutParams.bottomMargin;
                                if (baseline != -1) {
                                    measuredHeight -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline);
                                }
                            }
                            z = true;
                        } else {
                            z = true;
                            measuredHeight = ((((i12 - measuredHeight2) / 2) + i9) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        }
                        if (hasDividerBeforeChildAt(i15)) {
                            iMeasureNullChild += this.mDividerWidth;
                        }
                        int i17 = layoutParams.leftMargin + iMeasureNullChild;
                        i9 = i9;
                        setChildFrame(virtualChildAt, i17 + getLocationOffset(virtualChildAt), measuredHeight, measuredWidth, measuredHeight2);
                        int nextLocationOffset = i17 + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                        childrenSkipCount = i16 + getChildrenSkipCount(virtualChildAt, i15);
                        iMeasureNullChild = nextLocationOffset;
                    } else {
                        virtualChildCount = virtualChildCount;
                    }
                    i7 = layoutParams.gravity;
                    if (i7 < 0) {
                        i7 = i14;
                    }
                    i8 = i7 & 112;
                    i14 = i14;
                    if (i8 != 16) {
                        if (i8 != 48) {
                            measuredHeight = layoutParams.topMargin + i9;
                            if (baseline != -1) {
                                z = true;
                                measuredHeight += iArr[1] - baseline;
                            }
                        } else if (i8 != 80) {
                            measuredHeight = i9;
                        } else {
                            measuredHeight = (i11 - measuredHeight2) - layoutParams.bottomMargin;
                            if (baseline != -1) {
                                measuredHeight -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline);
                            }
                        }
                        z = true;
                    } else {
                        z = true;
                        measuredHeight = ((((i12 - measuredHeight2) / 2) + i9) + layoutParams.topMargin) - layoutParams.bottomMargin;
                    }
                    if (hasDividerBeforeChildAt(i15)) {
                        iMeasureNullChild += this.mDividerWidth;
                    }
                    int i18 = layoutParams.leftMargin + iMeasureNullChild;
                    i9 = i9;
                    setChildFrame(virtualChildAt, i18 + getLocationOffset(virtualChildAt), measuredHeight, measuredWidth, measuredHeight2);
                    int nextLocationOffset2 = i18 + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                    childrenSkipCount = i16 + getChildrenSkipCount(virtualChildAt, i15);
                    iMeasureNullChild = nextLocationOffset2;
                } else {
                    z = true;
                }
                childrenSkipCount++;
                virtualChildCount = virtualChildCount;
                i14 = i14;
                z3 = z;
                i9 = i9;
            }
            childrenSkipCount++;
            virtualChildCount = virtualChildCount;
            i14 = i14;
            z3 = z;
            i9 = i9;
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i3 + i, i4 + i2);
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    @RemotableViewMethod
    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int i) {
        int i2 = i & 8388615;
        int i3 = this.mGravity;
        if ((8388615 & i3) != i2) {
            this.mGravity = i2 | ((-8388616) & i3);
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        int i3 = this.mGravity;
        if ((i3 & 112) != i2) {
            this.mGravity = i2 | (i3 & PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS);
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        int i = this.mOrientation;
        if (i == 0) {
            return new LayoutParams(-2, -2);
        }
        if (i == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (sPreserveMarginParamsInLayoutParamConversion) {
            if (layoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) layoutParams);
            }
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
            }
        }
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return LinearLayout.class.getName();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("layout:baselineAligned", this.mBaselineAligned);
        viewHierarchyEncoder.addProperty("layout:baselineAlignedChildIndex", this.mBaselineAlignedChildIndex);
        viewHierarchyEncoder.addProperty("measurement:baselineChildTop", this.mBaselineChildTop);
        viewHierarchyEncoder.addProperty("measurement:orientation", this.mOrientation);
        viewHierarchyEncoder.addProperty("measurement:gravity", this.mGravity);
        viewHierarchyEncoder.addProperty("measurement:totalLength", this.mTotalLength);
        viewHierarchyEncoder.addProperty("layout:totalLength", this.mTotalLength);
        viewHierarchyEncoder.addProperty("layout:useLargestChild", this.mUseLargestChild);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, mapping = {@ViewDebug.IntToString(from = -1, to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 0, to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 48, to = "TOP"), @ViewDebug.IntToString(from = 80, to = "BOTTOM"), @ViewDebug.IntToString(from = 3, to = "LEFT"), @ViewDebug.IntToString(from = 5, to = "RIGHT"), @ViewDebug.IntToString(from = 8388611, to = "START"), @ViewDebug.IntToString(from = 8388613, to = "END"), @ViewDebug.IntToString(from = 16, to = "CENTER_VERTICAL"), @ViewDebug.IntToString(from = 112, to = "FILL_VERTICAL"), @ViewDebug.IntToString(from = 1, to = "CENTER_HORIZONTAL"), @ViewDebug.IntToString(from = 7, to = "FILL_HORIZONTAL"), @ViewDebug.IntToString(from = 17, to = "CENTER"), @ViewDebug.IntToString(from = 119, to = "FILL")})
        public int gravity;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public float weight;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayout_Layout);
            this.weight = typedArrayObtainStyledAttributes.getFloat(3, 0.0f);
            this.gravity = typedArrayObtainStyledAttributes.getInt(0, -1);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String str) {
            return str + "LinearLayout.LayoutParams={width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " weight=" + this.weight + "}";
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
            super.encodeProperties(viewHierarchyEncoder);
            viewHierarchyEncoder.addProperty("layout:weight", this.weight);
            viewHierarchyEncoder.addProperty("layout:gravity", this.gravity);
        }
    }
}
