package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import com.android.internal.R;

/* JADX INFO: loaded from: classes3.dex */
public class ResolverDrawerLayout extends ViewGroup {
    private static final String TAG = "ResolverDrawerLayout";
    private int mActivePointerId;
    private float mCollapseOffset;
    private int mCollapsibleHeight;
    private int mCollapsibleHeightReserved;
    private boolean mDismissLocked;
    private boolean mDismissOnScrollerFinished;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mIsDragging;
    private float mLastTouchY;
    private int mMaxCollapsedHeight;
    private int mMaxCollapsedHeightSmall;
    private int mMaxWidth;
    private final float mMinFlingVelocity;
    private OnDismissedListener mOnDismissedListener;
    private boolean mOpenOnClick;
    private boolean mOpenOnLayout;
    private RunOnDismissedListener mRunOnDismissedListener;
    private Drawable mScrollIndicatorDrawable;
    private final OverScroller mScroller;
    private boolean mShowAtTop;
    private boolean mSmallCollapsed;
    private final Rect mTempRect;
    private int mTopOffset;
    private final ViewTreeObserver.OnTouchModeChangeListener mTouchModeChangeListener;
    private final int mTouchSlop;
    private int mUncollapsibleHeight;
    private final VelocityTracker mVelocityTracker;

    public interface OnDismissedListener {
        void onDismissed();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return (i & 2) != 0;
    }

    public ResolverDrawerLayout(Context context) {
        this(context, null);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivePointerId = -1;
        this.mTempRect = new Rect();
        this.mTouchModeChangeListener = new ViewTreeObserver.OnTouchModeChangeListener() { // from class: com.android.internal.widget.ResolverDrawerLayout.1
            @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
            public void onTouchModeChanged(boolean z) {
                if (z || !ResolverDrawerLayout.this.hasFocus()) {
                    return;
                }
                ResolverDrawerLayout resolverDrawerLayout = ResolverDrawerLayout.this;
                if (resolverDrawerLayout.isDescendantClipped(resolverDrawerLayout.getFocusedChild())) {
                    ResolverDrawerLayout.this.smoothScrollTo(0, 0.0f);
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ResolverDrawerLayout, i, 0);
        this.mMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, -1);
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(1, 0);
        this.mMaxCollapsedHeight = dimensionPixelSize;
        this.mMaxCollapsedHeightSmall = typedArrayObtainStyledAttributes.getDimensionPixelSize(2, dimensionPixelSize);
        this.mShowAtTop = typedArrayObtainStyledAttributes.getBoolean(3, false);
        typedArrayObtainStyledAttributes.recycle();
        this.mScrollIndicatorDrawable = this.mContext.getDrawable(R.drawable.scroll_indicator_material);
        this.mScroller = new OverScroller(context, AnimationUtils.loadInterpolator(context, 17563653));
        this.mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        setImportantForAccessibility(1);
    }

    public void setSmallCollapsed(boolean z) {
        this.mSmallCollapsed = z;
        requestLayout();
    }

    public boolean isSmallCollapsed() {
        return this.mSmallCollapsed;
    }

    public boolean isCollapsed() {
        return this.mCollapseOffset > 0.0f;
    }

    public void setShowAtTop(boolean z) {
        this.mShowAtTop = z;
        invalidate();
        requestLayout();
    }

    public boolean getShowAtTop() {
        return this.mShowAtTop;
    }

    public void setCollapsed(boolean z) {
        if (!isLaidOut()) {
            this.mOpenOnLayout = z;
        } else {
            smoothScrollTo(z ? this.mCollapsibleHeight : 0, 0.0f);
        }
    }

    public void setCollapsibleHeightReserved(int i) {
        int i2 = this.mCollapsibleHeightReserved;
        this.mCollapsibleHeightReserved = i;
        int i3 = i - i2;
        if (i3 != 0 && this.mIsDragging) {
            this.mLastTouchY -= i3;
        }
        int i4 = this.mCollapsibleHeight;
        this.mCollapsibleHeight = Math.max(i4, getMaxCollapsedHeight());
        if (updateCollapseOffset(i4, !isDragging())) {
            return;
        }
        invalidate();
    }

    public void setDismissLocked(boolean z) {
        this.mDismissLocked = z;
    }

    private boolean isMoving() {
        return this.mIsDragging || !this.mScroller.isFinished();
    }

    private boolean isDragging() {
        return this.mIsDragging || getNestedScrollAxes() == 2;
    }

    private boolean updateCollapseOffset(int i, boolean z) {
        int i2;
        if (i == this.mCollapsibleHeight) {
            return false;
        }
        if (getShowAtTop()) {
            this.mCollapseOffset = 0.0f;
            return false;
        }
        if (isLaidOut()) {
            boolean z2 = this.mCollapseOffset != 0.0f;
            if (z && i < (i2 = this.mCollapsibleHeight) && this.mCollapseOffset == i) {
                this.mCollapseOffset = i2;
            } else {
                this.mCollapseOffset = Math.min(this.mCollapseOffset, this.mCollapsibleHeight);
            }
            boolean z3 = this.mCollapseOffset != 0.0f;
            if (z2 != z3) {
                onCollapsedChanged(z3);
            }
        } else {
            this.mCollapseOffset = this.mOpenOnLayout ? 0.0f : this.mCollapsibleHeight;
        }
        return true;
    }

    private int getMaxCollapsedHeight() {
        return (isSmallCollapsed() ? this.mMaxCollapsedHeightSmall : this.mMaxCollapsedHeight) + this.mCollapsibleHeightReserved;
    }

    public void setOnDismissedListener(OnDismissedListener onDismissedListener) {
        this.mOnDismissedListener = onDismissedListener;
    }

    private boolean isDismissable() {
        return (this.mOnDismissedListener == null || this.mDismissLocked) ? false : true;
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0065  */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mVelocityTracker.clear();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        if (actionMasked == 0) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.mInitialTouchX = x;
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            this.mOpenOnClick = isListChildUnderClipped(x, y) && this.mCollapseOffset > 0.0f;
        } else if (actionMasked == 1) {
            resetTouch();
        } else if (actionMasked == 2) {
            float x2 = motionEvent.getX();
            float y2 = motionEvent.getY();
            float f = y2 - this.mInitialTouchY;
            if (Math.abs(f) > this.mTouchSlop && findChildUnder(x2, y2) != null && (getNestedScrollAxes() & 2) == 0) {
                this.mActivePointerId = motionEvent.getPointerId(0);
                this.mIsDragging = true;
                float f2 = this.mLastTouchY;
                int i = this.mTouchSlop;
                this.mLastTouchY = Math.max(f2 - i, Math.min(f + f2, f2 + i));
            }
        } else if (actionMasked == 3) {
            resetTouch();
        } else if (actionMasked == 6) {
            onSecondaryPointerUp(motionEvent);
        }
        if (this.mIsDragging) {
            abortAnimation();
        }
        return this.mIsDragging || this.mOpenOnClick;
    }

    /* JADX WARN: Code duplicated, block: B:36:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:80:0x0187  */
    /* JADX WARN: Code duplicated, block: B:82:0x018b  */
    /* JADX WARN: Code duplicated, block: B:83:0x018d  */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        int actionMasked = motionEvent.getActionMasked();
        this.mVelocityTracker.addMovement(motionEvent);
        boolean z = true;
        if (actionMasked == 0) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.mInitialTouchX = x;
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            this.mActivePointerId = motionEvent.getPointerId(0);
            boolean z2 = findChildUnder(this.mInitialTouchX, this.mInitialTouchY) != null;
            boolean z3 = isDismissable() || this.mCollapsibleHeight > 0;
            this.mIsDragging = z2 && z3;
            abortAnimation();
            return z3;
        }
        if (actionMasked == 1) {
            boolean z4 = this.mIsDragging;
            this.mIsDragging = false;
            if (!z4 && findChildUnder(this.mInitialTouchX, this.mInitialTouchY) == null && findChildUnder(motionEvent.getX(), motionEvent.getY()) == null && isDismissable()) {
                dispatchOnDismissed();
                resetTouch();
                return true;
            }
            if (this.mOpenOnClick && Math.abs(motionEvent.getX() - this.mInitialTouchX) < this.mTouchSlop && Math.abs(motionEvent.getY() - this.mInitialTouchY) < this.mTouchSlop) {
                smoothScrollTo(0, 0.0f);
                return true;
            }
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            if (Math.abs(yVelocity) > this.mMinFlingVelocity) {
                if (getShowAtTop()) {
                    if (isDismissable() && yVelocity < 0.0f) {
                        abortAnimation();
                        dismiss();
                    } else {
                        smoothScrollTo(yVelocity < 0.0f ? 0 : this.mCollapsibleHeight, yVelocity);
                    }
                } else if (!isDismissable() || yVelocity <= 0.0f) {
                    if (yVelocity < 0.0f) {
                        i = 0;
                    } else {
                        i = this.mCollapsibleHeight;
                    }
                    smoothScrollTo(i, yVelocity);
                } else {
                    float f = this.mCollapseOffset;
                    int i2 = this.mCollapsibleHeight;
                    if (f > i2) {
                        smoothScrollTo(i2 + this.mUncollapsibleHeight, yVelocity);
                        this.mDismissOnScrollerFinished = true;
                    } else {
                        if (yVelocity < 0.0f) {
                            i = 0;
                        } else {
                            i = this.mCollapsibleHeight;
                        }
                        smoothScrollTo(i, yVelocity);
                    }
                }
            } else {
                float f2 = this.mCollapseOffset;
                int i3 = this.mCollapsibleHeight;
                if (f2 < i3 / 2) {
                    i3 = 0;
                }
                smoothScrollTo(i3, 0.0f);
            }
            resetTouch();
            return false;
        }
        if (actionMasked != 2) {
            if (actionMasked == 3) {
                if (this.mIsDragging) {
                    float f3 = this.mCollapseOffset;
                    int i4 = this.mCollapsibleHeight;
                    smoothScrollTo(f3 >= ((float) (i4 / 2)) ? i4 : 0, 0.0f);
                }
                resetTouch();
                return true;
            }
            if (actionMasked != 5) {
                if (actionMasked != 6) {
                    return false;
                }
                onSecondaryPointerUp(motionEvent);
                return false;
            }
            int actionIndex = motionEvent.getActionIndex();
            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
            this.mInitialTouchX = motionEvent.getX(actionIndex);
            float y2 = motionEvent.getY(actionIndex);
            this.mLastTouchY = y2;
            this.mInitialTouchY = y2;
            return false;
        }
        int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        if (iFindPointerIndex < 0) {
            Log.e(TAG, "Bad pointer id " + this.mActivePointerId + ", resetting");
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mInitialTouchX = motionEvent.getX();
            float y3 = motionEvent.getY();
            this.mLastTouchY = y3;
            this.mInitialTouchY = y3;
            iFindPointerIndex = 0;
        }
        float x2 = motionEvent.getX(iFindPointerIndex);
        float y4 = motionEvent.getY(iFindPointerIndex);
        if (this.mIsDragging) {
            z = false;
        } else {
            float f4 = y4 - this.mInitialTouchY;
            if (Math.abs(f4) <= this.mTouchSlop || findChildUnder(x2, y4) == null) {
                z = false;
            } else {
                this.mIsDragging = true;
                float f5 = this.mLastTouchY;
                int i5 = this.mTouchSlop;
                this.mLastTouchY = Math.max(f5 - i5, Math.min(f4 + f5, f5 + i5));
            }
        }
        if (this.mIsDragging) {
            performDrag(y4 - this.mLastTouchY);
        }
        this.mLastTouchY = y4;
        return z;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mInitialTouchX = motionEvent.getX(i);
            float y = motionEvent.getY(i);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            this.mActivePointerId = motionEvent.getPointerId(i);
        }
    }

    private void resetTouch() {
        this.mActivePointerId = -1;
        this.mIsDragging = false;
        this.mOpenOnClick = false;
        this.mLastTouchY = 0.0f;
        this.mInitialTouchY = 0.0f;
        this.mInitialTouchX = 0.0f;
        this.mVelocityTracker.clear();
    }

    private void dismiss() {
        RunOnDismissedListener runOnDismissedListener = new RunOnDismissedListener();
        this.mRunOnDismissedListener = runOnDismissedListener;
        post(runOnDismissedListener);
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            boolean z = !this.mScroller.isFinished();
            performDrag(this.mScroller.getCurrY() - this.mCollapseOffset);
            if (z) {
                postInvalidateOnAnimation();
            } else {
                if (!this.mDismissOnScrollerFinished || this.mOnDismissedListener == null) {
                    return;
                }
                dismiss();
            }
        }
    }

    private void abortAnimation() {
        this.mScroller.abortAnimation();
        this.mRunOnDismissedListener = null;
        this.mDismissOnScrollerFinished = false;
    }

    private float performDrag(float f) {
        if (getShowAtTop()) {
            return 0.0f;
        }
        float fMax = Math.max(0.0f, Math.min(this.mCollapseOffset + f, this.mCollapsibleHeight + this.mUncollapsibleHeight));
        float f2 = this.mCollapseOffset;
        if (fMax == f2) {
            return 0.0f;
        }
        float f3 = fMax - f2;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (!((LayoutParams) childAt.getLayoutParams()).ignoreOffset) {
                childAt.offsetTopAndBottom((int) f3);
            }
        }
        boolean z = this.mCollapseOffset != 0.0f;
        this.mCollapseOffset = fMax;
        this.mTopOffset = (int) (this.mTopOffset + f3);
        boolean z2 = fMax != 0.0f;
        if (z != z2) {
            onCollapsedChanged(z2);
        }
        postInvalidateOnAnimation();
        return f3;
    }

    private void onCollapsedChanged(boolean z) {
        notifyViewAccessibilityStateChangedIfNeeded(0);
        if (this.mScrollIndicatorDrawable != null) {
            setWillNotDraw(!z);
        }
    }

    void dispatchOnDismissed() {
        OnDismissedListener onDismissedListener = this.mOnDismissedListener;
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed();
        }
        RunOnDismissedListener runOnDismissedListener = this.mRunOnDismissedListener;
        if (runOnDismissedListener != null) {
            removeCallbacks(runOnDismissedListener);
            this.mRunOnDismissedListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void smoothScrollTo(int i, float f) {
        int iAbs;
        abortAnimation();
        int i2 = (int) this.mCollapseOffset;
        int i3 = i - i2;
        if (i3 == 0) {
            return;
        }
        int height = getHeight();
        int i4 = height / 2;
        float f2 = height;
        float f3 = i4;
        float fDistanceInfluenceForSnapDuration = f3 + (distanceInfluenceForSnapDuration(Math.min(1.0f, (Math.abs(i3) * 1.0f) / f2)) * f3);
        float fAbs = Math.abs(f);
        if (fAbs > 0.0f) {
            iAbs = Math.round(Math.abs(fDistanceInfluenceForSnapDuration / fAbs) * 1000.0f) * 4;
        } else {
            iAbs = (int) (((Math.abs(i3) / f2) + 1.0f) * 100.0f);
        }
        this.mScroller.startScroll(0, i2, 0, i3, Math.min(iAbs, 300));
        postInvalidateOnAnimation();
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((float) (((double) (f - 0.5f)) * 0.4712389167638204d));
    }

    private View findChildUnder(float f, float f2) {
        return findChildUnder(this, f, f2);
    }

    private static View findChildUnder(ViewGroup viewGroup, float f, float f2) {
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = viewGroup.getChildAt(childCount);
            if (isChildUnder(childAt, f, f2)) {
                return childAt;
            }
        }
        return null;
    }

    private View findListChildUnder(float f, float f2) {
        View viewFindChildUnder = findChildUnder(f, f2);
        while (viewFindChildUnder != null) {
            f -= viewFindChildUnder.getX();
            f2 -= viewFindChildUnder.getY();
            if (viewFindChildUnder instanceof AbsListView) {
                return findChildUnder((ViewGroup) viewFindChildUnder, f, f2);
            }
            viewFindChildUnder = viewFindChildUnder instanceof ViewGroup ? findChildUnder((ViewGroup) viewFindChildUnder, f, f2) : null;
        }
        return viewFindChildUnder;
    }

    private boolean isListChildUnderClipped(float f, float f2) {
        View viewFindListChildUnder = findListChildUnder(f, f2);
        return viewFindListChildUnder != null && isDescendantClipped(viewFindListChildUnder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDescendantClipped(View view) {
        this.mTempRect.set(0, 0, view.getWidth(), view.getHeight());
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        if (view.getParent() != this) {
            ViewParent parent = view.getParent();
            while (parent != this) {
                view = parent;
                parent = view.getParent();
            }
        }
        int height = getHeight() - getPaddingBottom();
        int childCount = getChildCount();
        for (int iIndexOfChild = indexOfChild(view) + 1; iIndexOfChild < childCount; iIndexOfChild++) {
            View childAt = getChildAt(iIndexOfChild);
            if (childAt.getVisibility() != 8) {
                height = Math.min(height, childAt.getTop());
            }
        }
        return this.mTempRect.bottom > height;
    }

    private static boolean isChildUnder(View view, float f, float f2) {
        float x = view.getX();
        float y = view.getY();
        return f >= x && f2 >= y && f < ((float) view.getWidth()) + x && f2 < ((float) view.getHeight()) + y;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        if (isInTouchMode() || !isDescendantClipped(view2)) {
            return;
        }
        smoothScrollTo(0, 0.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnTouchModeChangeListener(this.mTouchModeChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnTouchModeChangeListener(this.mTouchModeChangeListener);
        abortAnimation();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        super.onNestedScrollAccepted(view, view2, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onStopNestedScroll(View view) {
        super.onStopNestedScroll(view);
        if (this.mScroller.isFinished()) {
            float f = this.mCollapseOffset;
            int i = this.mCollapsibleHeight;
            if (f < i / 2) {
                i = 0;
            }
            smoothScrollTo(i, 0.0f);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        if (i4 < 0) {
            performDrag(-i4);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        if (i2 > 0) {
            iArr[1] = (int) (-performDrag(-i2));
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedPreFling(View view, float f, float f2) {
        if (getShowAtTop() || f2 <= this.mMinFlingVelocity || this.mCollapseOffset == 0.0f) {
            return false;
        }
        smoothScrollTo(0, f2);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x004c  */
    /* JADX WARN: Code duplicated, block: B:27:0x0051  */
    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        if (z || Math.abs(f2) <= this.mMinFlingVelocity) {
            return false;
        }
        if (getShowAtTop()) {
            if (isDismissable() && f2 > 0.0f) {
                abortAnimation();
                dismiss();
            } else {
                smoothScrollTo(f2 < 0.0f ? this.mCollapsibleHeight : 0, f2);
            }
        } else if (!isDismissable() || f2 >= 0.0f) {
            smoothScrollTo(f2 <= 0.0f ? this.mCollapsibleHeight : 0, f2);
        } else {
            float f3 = this.mCollapseOffset;
            int i = this.mCollapsibleHeight;
            if (f3 > i) {
                smoothScrollTo(i + this.mUncollapsibleHeight, f2);
                this.mDismissOnScrollerFinished = true;
            } else {
                smoothScrollTo(f2 <= 0.0f ? this.mCollapsibleHeight : 0, f2);
            }
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedPrePerformAccessibilityAction(View view, int i, Bundle bundle) {
        if (super.onNestedPrePerformAccessibilityAction(view, i, bundle)) {
            return true;
        }
        if (i != 4096 || this.mCollapseOffset == 0.0f) {
            return false;
        }
        smoothScrollTo(0, 0.0f);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ScrollView.class.getName();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (isEnabled() && this.mCollapseOffset != 0.0f) {
            accessibilityNodeInfo.addAction(4096);
            accessibilityNodeInfo.setScrollable(true);
        }
        accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS);
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (i == AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS.getId()) {
            return false;
        }
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        if (i != 4096 || this.mCollapseOffset == 0.0f) {
            return false;
        }
        smoothScrollTo(0, 0.0f);
        return true;
    }

    @Override // android.view.View
    public void onDrawForeground(Canvas canvas) {
        Drawable drawable = this.mScrollIndicatorDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        super.onDrawForeground(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int i4 = this.mMaxWidth;
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i4 >= 0 ? Math.min(size, i4) : size, 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int childCount = getChildCount();
        int measuredHeight = paddingTop;
        int i5 = 0;
        while (true) {
            i3 = 8;
            if (i5 >= childCount) {
                break;
            }
            View childAt = getChildAt(i5);
            if (((LayoutParams) childAt.getLayoutParams()).alwaysShow && childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, iMakeMeasureSpec, paddingLeft, iMakeMeasureSpec2, measuredHeight);
                measuredHeight += childAt.getMeasuredHeight();
            }
            i5++;
        }
        int i6 = 0;
        int measuredHeight2 = measuredHeight;
        while (i6 < childCount) {
            View childAt2 = getChildAt(i6);
            if (!((LayoutParams) childAt2.getLayoutParams()).alwaysShow && childAt2.getVisibility() != i3) {
                measureChildWithMargins(childAt2, iMakeMeasureSpec, paddingLeft, iMakeMeasureSpec2, measuredHeight2);
                measuredHeight2 += childAt2.getMeasuredHeight();
            }
            i6++;
            i3 = i3;
        }
        int i7 = this.mCollapsibleHeight;
        int iMax = Math.max(0, (measuredHeight2 - measuredHeight) - getMaxCollapsedHeight());
        this.mCollapsibleHeight = iMax;
        this.mUncollapsibleHeight = measuredHeight2 - iMax;
        updateCollapseOffset(i7, !isDragging());
        if (getShowAtTop()) {
            this.mTopOffset = 0;
        } else {
            this.mTopOffset = Math.max(0, size2 - measuredHeight2) + ((int) this.mCollapseOffset);
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int width = getWidth();
        int i5 = this.mTopOffset;
        int paddingLeft = getPaddingLeft();
        int paddingRight = width - getPaddingRight();
        int childCount = getChildCount();
        View view = null;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.hasNestedScrollIndicator) {
                view = childAt;
            }
            if (childAt.getVisibility() != 8) {
                int i7 = i5 + layoutParams.topMargin;
                if (layoutParams.ignoreOffset) {
                    i7 = (int) (i7 - this.mCollapseOffset);
                }
                int measuredHeight = childAt.getMeasuredHeight() + i7;
                int measuredWidth = childAt.getMeasuredWidth();
                int i8 = (((paddingRight - paddingLeft) - measuredWidth) / 2) + paddingLeft;
                childAt.layout(i8, i7, measuredWidth + i8, measuredHeight);
                i5 = measuredHeight + layoutParams.bottomMargin;
            }
        }
        if (this.mScrollIndicatorDrawable != null) {
            if (view != null) {
                int left = view.getLeft();
                int right = view.getRight();
                int top = view.getTop();
                this.mScrollIndicatorDrawable.setBounds(left, top - this.mScrollIndicatorDrawable.getIntrinsicHeight(), right, top);
                setWillNotDraw(true ^ isCollapsed());
                return;
            }
            this.mScrollIndicatorDrawable = null;
            setWillNotDraw(true);
        }
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.open = this.mCollapsibleHeight > 0 && this.mCollapseOffset == 0.0f;
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mOpenOnLayout = savedState.open;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public boolean alwaysShow;
        public boolean hasNestedScrollIndicator;
        public boolean ignoreOffset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ResolverDrawerLayout_LayoutParams);
            this.alwaysShow = typedArrayObtainStyledAttributes.getBoolean(1, false);
            this.ignoreOffset = typedArrayObtainStyledAttributes.getBoolean(3, false);
            this.hasNestedScrollIndicator = typedArrayObtainStyledAttributes.getBoolean(2, false);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.alwaysShow = layoutParams.alwaysShow;
            this.ignoreOffset = layoutParams.ignoreOffset;
            this.hasNestedScrollIndicator = layoutParams.hasNestedScrollIndicator;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.ResolverDrawerLayout.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean open;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.open = parcel.readInt() != 0;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.open ? 1 : 0);
        }
    }

    private class RunOnDismissedListener implements Runnable {
        private RunOnDismissedListener() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ResolverDrawerLayout.this.dispatchOnDismissed();
        }
    }
}
