package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.Comparator;

/* JADX INFO: loaded from: classes3.dex */
@RemoteViews.RemoteView
public class NotificationActionListLayout extends LinearLayout {
    public static final Comparator<Pair<Integer, TextView>> MEASURE_ORDER_COMPARATOR = new Comparator() { // from class: com.android.internal.widget.-$$Lambda$NotificationActionListLayout$uFZFEmIEBpI3kn6c3tNvvgmMSv8
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return ((Integer) ((Pair) obj).first).compareTo((Integer) ((Pair) obj2).first);
        }
    };
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingTop;
    private int mEmphasizedHeight;
    private boolean mEmphasizedMode;
    private final int mGravity;
    private ArrayList<View> mMeasureOrderOther;
    private ArrayList<Pair<Integer, TextView>> mMeasureOrderTextViews;
    private int mRegularHeight;
    private int mTotalWidth;

    public NotificationActionListLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationActionListLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public NotificationActionListLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTotalWidth = 0;
        this.mMeasureOrderTextViews = new ArrayList<>();
        this.mMeasureOrderOther = new ArrayList<>();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16842927}, i, i2);
        this.mGravity = typedArrayObtainStyledAttributes.getInt(0, 0);
        typedArrayObtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        TextView textView;
        if (this.mEmphasizedMode) {
            super.onMeasure(i, i2);
            return;
        }
        int childCount = getChildCount();
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (true) {
            i3 = 8;
            if (i4 >= childCount) {
                break;
            }
            View childAt = getChildAt(i4);
            if (childAt instanceof TextView) {
                i5++;
            } else {
                i6++;
            }
            if (childAt.getVisibility() != 8) {
                i7++;
            }
            i4++;
        }
        boolean z = (i5 == this.mMeasureOrderTextViews.size() && i6 == this.mMeasureOrderOther.size()) ? false : true;
        if (!z) {
            int size = this.mMeasureOrderTextViews.size();
            for (int i8 = 0; i8 < size; i8++) {
                Pair<Integer, TextView> pair = this.mMeasureOrderTextViews.get(i8);
                if (pair.first.intValue() != pair.second.getText().length()) {
                    z = true;
                }
            }
        }
        if (z) {
            rebuildMeasureOrder(i5, i6);
        }
        boolean z2 = View.MeasureSpec.getMode(i) != 0;
        int size2 = (View.MeasureSpec.getSize(i) - this.mPaddingLeft) - this.mPaddingRight;
        int size3 = this.mMeasureOrderOther.size();
        int i9 = 0;
        int measuredWidth = 0;
        int i10 = 0;
        while (i9 < childCount) {
            if (i9 < size3) {
                textView = this.mMeasureOrderOther.get(i9);
            } else {
                textView = this.mMeasureOrderTextViews.get(i9 - size3).second;
            }
            View view = textView;
            if (view.getVisibility() != i3) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                measureChildWithMargins(view, i, z2 ? size2 - ((size2 - measuredWidth) / (i7 - i10)) : measuredWidth, i2, 0);
                measuredWidth += view.getMeasuredWidth() + marginLayoutParams.rightMargin + marginLayoutParams.leftMargin;
                i10++;
            }
            i9++;
            i3 = 8;
        }
        this.mTotalWidth = measuredWidth + this.mPaddingRight + this.mPaddingLeft;
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), i), resolveSize(getSuggestedMinimumHeight(), i2));
    }

    /* JADX WARN: Code duplicated, block: B:9:0x003f  */
    private void rebuildMeasureOrder(int i, int i2) {
        clearMeasureOrder();
        this.mMeasureOrderTextViews.ensureCapacity(i);
        this.mMeasureOrderOther.ensureCapacity(i2);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt instanceof TextView) {
                TextView textView = (TextView) childAt;
                if (textView.getText().length() > 0) {
                    this.mMeasureOrderTextViews.add(Pair.create(Integer.valueOf(textView.getText().length()), textView));
                } else {
                    this.mMeasureOrderOther.add(childAt);
                }
            } else {
                this.mMeasureOrderOther.add(childAt);
            }
        }
        this.mMeasureOrderTextViews.sort(MEASURE_ORDER_COMPARATOR);
    }

    private void clearMeasureOrder() {
        this.mMeasureOrderOther.clear();
        this.mMeasureOrderTextViews.clear();
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        super.onViewAdded(view);
        clearMeasureOrder();
        if (view.getBackground() instanceof RippleDrawable) {
            ((RippleDrawable) view.getBackground()).setForceSoftware(true);
        }
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        clearMeasureOrder();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        if (this.mEmphasizedMode) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        boolean zIsLayoutRtl = isLayoutRtl();
        int i7 = this.mPaddingTop;
        int i8 = 1;
        if ((this.mGravity & 1) != 0) {
            i5 = ((this.mPaddingLeft + i) + ((i3 - i) / 2)) - (this.mTotalWidth / 2);
        } else {
            i5 = this.mPaddingLeft;
            if (Gravity.getAbsoluteGravity(8388611, getLayoutDirection()) == 5) {
                i5 += (i3 - i) - this.mTotalWidth;
            }
        }
        int i9 = ((i4 - i2) - i7) - this.mPaddingBottom;
        int childCount = getChildCount();
        if (zIsLayoutRtl) {
            i6 = childCount - 1;
            i8 = -1;
        } else {
            i6 = 0;
        }
        for (int i10 = 0; i10 < childCount; i10++) {
            View childAt = getChildAt((i8 * i10) + i6);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
                int i11 = ((((i9 - measuredHeight) / 2) + i7) + marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin;
                int i12 = i5 + marginLayoutParams.leftMargin;
                childAt.layout(i12, i11, i12 + measuredWidth, measuredHeight + i11);
                i5 = i12 + measuredWidth + marginLayoutParams.rightMargin;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDefaultPaddingBottom = getPaddingBottom();
        this.mDefaultPaddingTop = getPaddingTop();
        updateHeights();
    }

    private void updateHeights() {
        this.mEmphasizedHeight = getResources().getDimensionPixelSize(R.dimen.notification_content_margin_end) + getResources().getDimensionPixelSize(R.dimen.notification_content_margin) + getResources().getDimensionPixelSize(R.dimen.notification_action_emphasized_height);
        this.mRegularHeight = getResources().getDimensionPixelSize(R.dimen.notification_action_list_height);
    }

    @RemotableViewMethod
    public void setEmphasizedMode(boolean z) {
        int i;
        this.mEmphasizedMode = z;
        if (z) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.notification_content_margin);
            int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.notification_content_margin_end);
            i = this.mEmphasizedHeight;
            int dimensionPixelSize3 = getResources().getDimensionPixelSize(R.dimen.button_inset_vertical_material);
            setPaddingRelative(getPaddingStart(), dimensionPixelSize - dimensionPixelSize3, getPaddingEnd(), dimensionPixelSize2 - dimensionPixelSize3);
        } else {
            setPaddingRelative(getPaddingStart(), this.mDefaultPaddingTop, getPaddingEnd(), this.mDefaultPaddingBottom);
            i = this.mRegularHeight;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = i;
        setLayoutParams(layoutParams);
    }

    public int getExtraMeasureHeight() {
        if (this.mEmphasizedMode) {
            return this.mEmphasizedHeight - this.mRegularHeight;
        }
        return 0;
    }
}
