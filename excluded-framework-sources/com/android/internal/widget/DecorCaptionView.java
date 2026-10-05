package com.android.internal.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import com.android.internal.R;
import com.android.internal.policy.PhoneWindow;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class DecorCaptionView extends ViewGroup implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private static final String TAG = "DecorCaptionView";
    private View mCaption;
    private boolean mCheckForDragging;
    private View mClickTarget;
    private View mClose;
    private final Rect mCloseRect;
    private View mContent;
    private int mDragSlop;
    private boolean mDragging;
    private GestureDetector mGestureDetector;
    private View mMaximize;
    private final Rect mMaximizeRect;
    private boolean mOverlayWithAppContent;
    private PhoneWindow mOwner;
    private boolean mShow;
    private ArrayList<View> mTouchDispatchList;
    private int mTouchDownX;
    private int mTouchDownY;

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public DecorCaptionView(Context context) {
        super(context);
        this.mOwner = null;
        this.mShow = false;
        this.mDragging = false;
        this.mOverlayWithAppContent = false;
        this.mTouchDispatchList = new ArrayList<>(2);
        this.mCloseRect = new Rect();
        this.mMaximizeRect = new Rect();
        init(context);
    }

    public DecorCaptionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOwner = null;
        this.mShow = false;
        this.mDragging = false;
        this.mOverlayWithAppContent = false;
        this.mTouchDispatchList = new ArrayList<>(2);
        this.mCloseRect = new Rect();
        this.mMaximizeRect = new Rect();
        init(context);
    }

    public DecorCaptionView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOwner = null;
        this.mShow = false;
        this.mDragging = false;
        this.mOverlayWithAppContent = false;
        this.mTouchDispatchList = new ArrayList<>(2);
        this.mCloseRect = new Rect();
        this.mMaximizeRect = new Rect();
        init(context);
    }

    private void init(Context context) {
        this.mDragSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mGestureDetector = new GestureDetector(context, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCaption = getChildAt(0);
    }

    public void setPhoneWindow(PhoneWindow phoneWindow, boolean z) {
        this.mOwner = phoneWindow;
        this.mShow = z;
        boolean zIsOverlayWithDecorCaptionEnabled = phoneWindow.isOverlayWithDecorCaptionEnabled();
        this.mOverlayWithAppContent = zIsOverlayWithDecorCaptionEnabled;
        if (zIsOverlayWithDecorCaptionEnabled) {
            this.mCaption.setBackgroundColor(0);
        }
        updateCaptionVisibility();
        this.mOwner.getDecorView().setOutlineProvider(ViewOutlineProvider.BOUNDS);
        this.mMaximize = findViewById(R.id.maximize_window);
        this.mClose = findViewById(R.id.close_window);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (this.mMaximizeRect.contains(x, y)) {
                this.mClickTarget = this.mMaximize;
            }
            if (this.mCloseRect.contains(x, y)) {
                this.mClickTarget = this.mClose;
            }
        }
        return this.mClickTarget != null;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mClickTarget == null) {
            return false;
        }
        this.mGestureDetector.onTouchEvent(motionEvent);
        int action = motionEvent.getAction();
        if (action == 1 || action == 3) {
            this.mClickTarget = null;
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0052  */
    /* JADX WARN: Code duplicated, block: B:28:0x0057  */
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        boolean z = motionEvent.getToolType(motionEvent.getActionIndex()) == 3;
        boolean z2 = (motionEvent.getButtonState() & 1) != 0;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                if (this.mDragging) {
                    this.mDragging = false;
                    return !this.mCheckForDragging;
                }
            } else if (actionMasked != 2) {
                if (actionMasked == 3) {
                    if (this.mDragging) {
                        this.mDragging = false;
                        return !this.mCheckForDragging;
                    }
                }
            } else if (!this.mDragging && this.mCheckForDragging && (z || passedSlop(x, y))) {
                this.mCheckForDragging = false;
                this.mDragging = true;
                startMovingTask(motionEvent.getRawX(), motionEvent.getRawY());
            }
        } else {
            if (!this.mShow) {
                return false;
            }
            if (!z || z2) {
                this.mCheckForDragging = true;
                this.mTouchDownX = x;
                this.mTouchDownY = y;
            }
        }
        return this.mDragging || this.mCheckForDragging;
    }

    @Override // android.view.ViewGroup
    public ArrayList<View> buildTouchDispatchChildList() {
        this.mTouchDispatchList.ensureCapacity(3);
        View view = this.mCaption;
        if (view != null) {
            this.mTouchDispatchList.add(view);
        }
        View view2 = this.mContent;
        if (view2 != null) {
            this.mTouchDispatchList.add(view2);
        }
        return this.mTouchDispatchList;
    }

    private boolean passedSlop(int i, int i2) {
        return Math.abs(i - this.mTouchDownX) > this.mDragSlop || Math.abs(i2 - this.mTouchDownY) > this.mDragSlop;
    }

    public void onConfigurationChanged(boolean z) {
        this.mShow = z;
        updateCaptionVisibility();
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (!(layoutParams instanceof ViewGroup.MarginLayoutParams)) {
            throw new IllegalArgumentException("params " + layoutParams + " must subclass MarginLayoutParams");
        }
        if (i >= 2 || getChildCount() >= 2) {
            throw new IllegalStateException("DecorCaptionView can only handle 1 client view");
        }
        super.addView(view, 0, layoutParams);
        this.mContent = view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int measuredHeight;
        if (this.mCaption.getVisibility() != 8) {
            measureChildWithMargins(this.mCaption, i, 0, i2, 0);
            measuredHeight = this.mCaption.getMeasuredHeight();
        } else {
            measuredHeight = 0;
        }
        int i3 = measuredHeight;
        View view = this.mContent;
        if (view != null) {
            if (this.mOverlayWithAppContent) {
                measureChildWithMargins(view, i, 0, i2, 0);
            } else {
                measureChildWithMargins(view, i, 0, i2, i3);
            }
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int bottom;
        if (this.mCaption.getVisibility() != 8) {
            View view = this.mCaption;
            view.layout(0, 0, view.getMeasuredWidth(), this.mCaption.getMeasuredHeight());
            bottom = this.mCaption.getBottom() - this.mCaption.getTop();
            this.mMaximize.getHitRect(this.mMaximizeRect);
            this.mClose.getHitRect(this.mCloseRect);
        } else {
            this.mMaximizeRect.setEmpty();
            this.mCloseRect.setEmpty();
            bottom = 0;
        }
        View view2 = this.mContent;
        if (view2 != null) {
            if (this.mOverlayWithAppContent) {
                view2.layout(0, 0, view2.getMeasuredWidth(), this.mContent.getMeasuredHeight());
            } else {
                view2.layout(0, bottom, view2.getMeasuredWidth(), this.mContent.getMeasuredHeight() + bottom);
            }
        }
        this.mOwner.notifyRestrictedCaptionAreaCallback(this.mMaximize.getLeft(), this.mMaximize.getTop(), this.mClose.getRight(), this.mClose.getBottom());
    }

    private boolean isFillingScreen() {
        return ((getWindowSystemUiVisibility() | getSystemUiVisibility()) & 2565) != 0;
    }

    private void updateCaptionVisibility() {
        this.mCaption.setVisibility(isFillingScreen() || !this.mShow ? 8 : 0);
        this.mCaption.setOnTouchListener(this);
    }

    private void maximizeWindow() {
        Window.WindowControllerCallback windowControllerCallback = this.mOwner.getWindowControllerCallback();
        if (windowControllerCallback != null) {
            try {
                windowControllerCallback.exitFreeformMode();
            } catch (RemoteException unused) {
                Log.e(TAG, "Cannot change task workspace.");
            }
        }
    }

    public boolean isCaptionShowing() {
        return this.mShow;
    }

    public int getCaptionHeight() {
        View view = this.mCaption;
        if (view != null) {
            return view.getHeight();
        }
        return 0;
    }

    public void removeContentView() {
        View view = this.mContent;
        if (view != null) {
            removeView(view);
            this.mContent = null;
        }
    }

    public View getCaption() {
        return this.mCaption;
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(-1, -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new ViewGroup.MarginLayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof ViewGroup.MarginLayoutParams;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        View view = this.mClickTarget;
        if (view == this.mMaximize) {
            maximizeWindow();
        } else if (view == this.mClose) {
            this.mOwner.dispatchOnWindowDismissed(true, false);
        }
        return true;
    }
}
