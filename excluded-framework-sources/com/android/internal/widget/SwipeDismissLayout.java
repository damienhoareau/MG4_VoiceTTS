package com.android.internal.widget;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.android.internal.R;

/* JADX INFO: loaded from: classes3.dex */
public class SwipeDismissLayout extends FrameLayout {
    private static final float MAX_DIST_THRESHOLD = 0.33f;
    private static final float MIN_DIST_THRESHOLD = 0.1f;
    private static final String TAG = "SwipeDismissLayout";
    private int mActiveTouchId;
    private boolean mActivityTranslucencyConverted;
    private boolean mBlockGesture;
    private boolean mDiscardIntercept;
    private final DismissAnimator mDismissAnimator;
    private boolean mDismissable;
    private boolean mDismissed;
    private OnDismissedListener mDismissedListener;
    private float mDownX;
    private float mDownY;
    private boolean mIsWindowNativelyTranslucent;
    private float mLastX;
    private int mMinFlingVelocity;
    private OnSwipeProgressChangedListener mProgressListener;
    private IntentFilter mScreenOffFilter;
    private BroadcastReceiver mScreenOffReceiver;
    private int mSlop;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;

    public interface OnDismissedListener {
        void onDismissed(SwipeDismissLayout swipeDismissLayout);
    }

    public interface OnSwipeProgressChangedListener {
        void onSwipeCancelled(SwipeDismissLayout swipeDismissLayout);

        void onSwipeProgressChanged(SwipeDismissLayout swipeDismissLayout, float f, float f2);
    }

    private float progressToAlpha(float f) {
        return 1.0f - ((f * f) * f);
    }

    public SwipeDismissLayout(Context context) {
        super(context);
        this.mBlockGesture = false;
        this.mActivityTranslucencyConverted = false;
        this.mDismissAnimator = new DismissAnimator();
        this.mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.mDismissable = true;
        init(context);
    }

    public SwipeDismissLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBlockGesture = false;
        this.mActivityTranslucencyConverted = false;
        this.mDismissAnimator = new DismissAnimator();
        this.mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.mDismissable = true;
        init(context);
    }

    public SwipeDismissLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBlockGesture = false;
        this.mActivityTranslucencyConverted = false;
        this.mDismissAnimator = new DismissAnimator();
        this.mScreenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.mDismissable = true;
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(R.styleable.Theme);
        this.mIsWindowNativelyTranslucent = typedArrayObtainStyledAttributes.getBoolean(5, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    public void setOnDismissedListener(OnDismissedListener onDismissedListener) {
        this.mDismissedListener = onDismissedListener;
    }

    public void setOnSwipeProgressChangedListener(OnSwipeProgressChangedListener onSwipeProgressChangedListener) {
        this.mProgressListener = onSwipeProgressChangedListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            this.mScreenOffReceiver = new AnonymousClass1();
            getContext().registerReceiver(this.mScreenOffReceiver, this.mScreenOffFilter);
        } catch (ReceiverCallNotAllowedException unused) {
            this.mScreenOffReceiver = null;
        }
    }

    /* JADX INFO: renamed from: com.android.internal.widget.SwipeDismissLayout$1, reason: invalid class name */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SwipeDismissLayout.this.post(new Runnable() { // from class: com.android.internal.widget.-$$Lambda$SwipeDismissLayout$1$NDXsqpv65OVP2OutTHt-hDxJywg
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onReceive$0$SwipeDismissLayout$1();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0$SwipeDismissLayout$1() {
            if (SwipeDismissLayout.this.mDismissed) {
                SwipeDismissLayout.this.dismiss();
            } else {
                SwipeDismissLayout.this.cancel();
            }
            SwipeDismissLayout.this.resetMembers();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        if (this.mScreenOffReceiver != null) {
            getContext().unregisterReceiver(this.mScreenOffReceiver);
            this.mScreenOffReceiver = null;
        }
        super.onDetachedFromWindow();
    }

    /* JADX WARN: Code duplicated, block: B:42:0x009a  */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        checkGesture(motionEvent);
        if (this.mBlockGesture) {
            return true;
        }
        if (!this.mDismissable) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        motionEvent.offsetLocation(motionEvent.getRawX() - motionEvent.getX(), 0.0f);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            resetMembers();
            this.mDownX = motionEvent.getRawX();
            this.mDownY = motionEvent.getRawY();
            this.mActiveTouchId = motionEvent.getPointerId(0);
            VelocityTracker velocityTrackerObtain = VelocityTracker.obtain("int1");
            this.mVelocityTracker = velocityTrackerObtain;
            velocityTrackerObtain.addMovement(motionEvent);
        } else if (actionMasked == 1) {
            resetMembers();
        } else if (actionMasked != 2) {
            if (actionMasked == 3) {
                resetMembers();
            } else if (actionMasked == 5) {
                this.mActiveTouchId = motionEvent.getPointerId(motionEvent.getActionIndex());
            } else if (actionMasked == 6) {
                int actionIndex = motionEvent.getActionIndex();
                if (motionEvent.getPointerId(actionIndex) == this.mActiveTouchId) {
                    this.mActiveTouchId = motionEvent.getPointerId(actionIndex == 0 ? 1 : 0);
                }
            }
        } else if (this.mVelocityTracker != null && !this.mDiscardIntercept) {
            int iFindPointerIndex = motionEvent.findPointerIndex(this.mActiveTouchId);
            if (iFindPointerIndex == -1) {
                Log.e(TAG, "Invalid pointer index: ignoring.");
                this.mDiscardIntercept = true;
            } else {
                float rawX = motionEvent.getRawX() - this.mDownX;
                float x = motionEvent.getX(iFindPointerIndex);
                float y = motionEvent.getY(iFindPointerIndex);
                if (rawX != 0.0f && canScroll(this, false, rawX, x, y)) {
                    this.mDiscardIntercept = true;
                } else {
                    updateSwiping(motionEvent);
                }
            }
        }
        return !this.mDiscardIntercept && this.mSwiping;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        checkGesture(motionEvent);
        if (this.mBlockGesture) {
            return true;
        }
        if (this.mVelocityTracker == null || !this.mDismissable) {
            return super.onTouchEvent(motionEvent);
        }
        motionEvent.offsetLocation(motionEvent.getRawX() - motionEvent.getX(), 0.0f);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1) {
            updateDismiss(motionEvent);
            if (this.mDismissed) {
                this.mDismissAnimator.animateDismissal(motionEvent.getRawX() - this.mDownX);
            } else if (this.mSwiping && this.mLastX != -2.1474836E9f) {
                this.mDismissAnimator.animateRecovery(motionEvent.getRawX() - this.mDownX);
            }
            resetMembers();
        } else if (actionMasked == 2) {
            this.mVelocityTracker.addMovement(motionEvent);
            this.mLastX = motionEvent.getRawX();
            updateSwiping(motionEvent);
            if (this.mSwiping) {
                setProgress(motionEvent.getRawX() - this.mDownX);
            }
        } else if (actionMasked == 3) {
            cancel();
            resetMembers();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProgress(float f) {
        OnSwipeProgressChangedListener onSwipeProgressChangedListener = this.mProgressListener;
        if (onSwipeProgressChangedListener == null || f < 0.0f) {
            return;
        }
        onSwipeProgressChangedListener.onSwipeProgressChanged(this, progressToAlpha(f / getWidth()), f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss() {
        OnDismissedListener onDismissedListener = this.mDismissedListener;
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed(this);
        }
    }

    protected void cancel() {
        Activity activityFindActivity;
        if (!this.mIsWindowNativelyTranslucent && (activityFindActivity = findActivity()) != null && this.mActivityTranslucencyConverted) {
            activityFindActivity.convertFromTranslucent();
            this.mActivityTranslucencyConverted = false;
        }
        OnSwipeProgressChangedListener onSwipeProgressChangedListener = this.mProgressListener;
        if (onSwipeProgressChangedListener != null) {
            onSwipeProgressChangedListener.onSwipeCancelled(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetMembers() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = null;
        this.mDownX = 0.0f;
        this.mLastX = -2.1474836E9f;
        this.mDownY = 0.0f;
        this.mSwiping = false;
        this.mDismissed = false;
        this.mDiscardIntercept = false;
    }

    private void updateSwiping(MotionEvent motionEvent) {
        Activity activityFindActivity;
        boolean z = this.mSwiping;
        if (!z) {
            float rawX = motionEvent.getRawX() - this.mDownX;
            float rawY = motionEvent.getRawY() - this.mDownY;
            float f = (rawX * rawX) + (rawY * rawY);
            int i = this.mSlop;
            boolean z2 = false;
            if (f > i * i) {
                if (rawX > i * 2 && Math.abs(rawY) < Math.abs(rawX)) {
                    z2 = true;
                }
                this.mSwiping = z2;
            } else {
                this.mSwiping = false;
            }
        }
        if (!this.mSwiping || z || this.mIsWindowNativelyTranslucent || (activityFindActivity = findActivity()) == null) {
            return;
        }
        this.mActivityTranslucencyConverted = activityFindActivity.convertToTranslucent(null, null);
    }

    private void updateDismiss(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX() - this.mDownX;
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        if (this.mLastX == -2.1474836E9f) {
            xVelocity = rawX / ((motionEvent.getEventTime() - motionEvent.getDownTime()) / 1000);
        }
        if (!this.mDismissed && ((rawX > getWidth() * Math.max(Math.min((((-0.23000002f) * xVelocity) / this.mMinFlingVelocity) + MAX_DIST_THRESHOLD, MAX_DIST_THRESHOLD), MIN_DIST_THRESHOLD) && motionEvent.getRawX() >= this.mLastX) || xVelocity >= this.mMinFlingVelocity)) {
            this.mDismissed = true;
        }
        if (this.mDismissed && this.mSwiping && xVelocity < (-this.mMinFlingVelocity)) {
            this.mDismissed = false;
        }
    }

    protected boolean canScroll(View view, boolean z, float f, float f2, float f3) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                float f4 = f2 + scrollX;
                if (f4 >= childAt.getLeft() && f4 < childAt.getRight()) {
                    float f5 = f3 + scrollY;
                    if (f5 >= childAt.getTop() && f5 < childAt.getBottom() && canScroll(childAt, true, f, f4 - childAt.getLeft(), f5 - childAt.getTop())) {
                        return true;
                    }
                }
            }
        }
        return z && view.canScrollHorizontally((int) (-f));
    }

    public void setDismissable(boolean z) {
        if (!z && this.mDismissable) {
            cancel();
            resetMembers();
        }
        this.mDismissable = z;
    }

    private void checkGesture(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mBlockGesture = this.mDismissAnimator.isAnimating();
        }
    }

    private Activity findActivity() {
        for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }

    private class DismissAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
        private final ValueAnimator mDismissAnimator;
        private boolean mDismissOnComplete;
        private boolean mWasCanceled;
        private final TimeInterpolator DISMISS_INTERPOLATOR = new DecelerateInterpolator(1.5f);
        private final long DISMISS_DURATION = 250;

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        DismissAnimator() {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.mDismissAnimator = valueAnimator;
            this.mWasCanceled = false;
            this.mDismissOnComplete = false;
            valueAnimator.addUpdateListener(this);
            this.mDismissAnimator.addListener(this);
        }

        void animateDismissal(float f) {
            animate(f / SwipeDismissLayout.this.getWidth(), 1.0f, 250L, this.DISMISS_INTERPOLATOR, true);
        }

        void animateRecovery(float f) {
            animate(f / SwipeDismissLayout.this.getWidth(), 0.0f, 250L, this.DISMISS_INTERPOLATOR, false);
        }

        boolean isAnimating() {
            return this.mDismissAnimator.isStarted();
        }

        private void animate(float f, float f2, long j, TimeInterpolator timeInterpolator, boolean z) {
            this.mDismissAnimator.cancel();
            this.mDismissOnComplete = z;
            this.mDismissAnimator.setFloatValues(f, f2);
            this.mDismissAnimator.setDuration(j);
            this.mDismissAnimator.setInterpolator(timeInterpolator);
            this.mDismissAnimator.start();
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            SwipeDismissLayout swipeDismissLayout = SwipeDismissLayout.this;
            swipeDismissLayout.setProgress(fFloatValue * swipeDismissLayout.getWidth());
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            this.mWasCanceled = false;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            this.mWasCanceled = true;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (this.mWasCanceled) {
                return;
            }
            if (this.mDismissOnComplete) {
                SwipeDismissLayout.this.dismiss();
            } else {
                SwipeDismissLayout.this.cancel();
            }
        }
    }
}
