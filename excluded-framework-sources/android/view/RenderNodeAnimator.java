package android.view;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.util.SparseIntArray;
import com.android.internal.util.VirtualRefBasePtr;
import com.android.internal.view.animation.FallbackLUTInterpolator;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class RenderNodeAnimator extends Animator {
    public static final int ALPHA = 11;
    public static final int LAST_VALUE = 11;
    public static final int PAINT_ALPHA = 1;
    public static final int PAINT_STROKE_WIDTH = 0;
    public static final int ROTATION = 5;
    public static final int ROTATION_X = 6;
    public static final int ROTATION_Y = 7;
    public static final int SCALE_X = 3;
    public static final int SCALE_Y = 4;
    private static final int STATE_DELAYED = 1;
    private static final int STATE_FINISHED = 3;
    private static final int STATE_PREPARE = 0;
    private static final int STATE_RUNNING = 2;
    public static final int TRANSLATION_X = 0;
    public static final int TRANSLATION_Y = 1;
    public static final int TRANSLATION_Z = 2;
    public static final int X = 8;
    public static final int Y = 9;
    public static final int Z = 10;
    private float mFinalValue;
    private TimeInterpolator mInterpolator;
    private VirtualRefBasePtr mNativePtr;
    private int mRenderProperty;
    private long mStartDelay;
    private long mStartTime;
    private int mState;
    private RenderNode mTarget;
    private final boolean mUiThreadHandlesDelay;
    private long mUnscaledDuration;
    private long mUnscaledStartDelay;
    private View mViewTarget;
    private static final SparseIntArray sViewPropertyAnimatorMap = new SparseIntArray(15) { // from class: android.view.RenderNodeAnimator.1
        {
            put(1, 0);
            put(2, 1);
            put(4, 2);
            put(8, 3);
            put(16, 4);
            put(32, 5);
            put(64, 6);
            put(128, 7);
            put(256, 8);
            put(512, 9);
            put(1024, 10);
            put(2048, 11);
        }
    };
    private static ThreadLocal<DelayedAnimationHelper> sAnimationHelper = new ThreadLocal<>();

    private static native long nCreateAnimator(int i, float f);

    private static native long nCreateCanvasPropertyFloatAnimator(long j, float f);

    private static native long nCreateCanvasPropertyPaintAnimator(long j, int i, float f);

    private static native long nCreateRevealAnimator(int i, int i2, float f, float f2);

    private static native void nEnd(long j);

    private static native long nGetDuration(long j);

    private static native void nSetAllowRunningAsync(long j, boolean z);

    private static native void nSetDuration(long j, long j2);

    private static native void nSetInterpolator(long j, long j2);

    private static native void nSetListener(long j, RenderNodeAnimator renderNodeAnimator);

    private static native void nSetStartDelay(long j, long j2);

    private static native void nSetStartValue(long j, float f);

    private static native void nStart(long j);

    public static int mapViewPropertyToRenderProperty(int i) {
        return sViewPropertyAnimatorMap.get(i);
    }

    public RenderNodeAnimator(int i, float f) {
        this.mRenderProperty = -1;
        this.mState = 0;
        this.mUnscaledDuration = 300L;
        this.mUnscaledStartDelay = 0L;
        this.mStartDelay = 0L;
        this.mRenderProperty = i;
        this.mFinalValue = f;
        this.mUiThreadHandlesDelay = true;
        init(nCreateAnimator(i, f));
    }

    public RenderNodeAnimator(CanvasProperty<Float> canvasProperty, float f) {
        this.mRenderProperty = -1;
        this.mState = 0;
        this.mUnscaledDuration = 300L;
        this.mUnscaledStartDelay = 0L;
        this.mStartDelay = 0L;
        init(nCreateCanvasPropertyFloatAnimator(canvasProperty.getNativeContainer(), f));
        this.mUiThreadHandlesDelay = false;
    }

    public RenderNodeAnimator(CanvasProperty<Paint> canvasProperty, int i, float f) {
        this.mRenderProperty = -1;
        this.mState = 0;
        this.mUnscaledDuration = 300L;
        this.mUnscaledStartDelay = 0L;
        this.mStartDelay = 0L;
        init(nCreateCanvasPropertyPaintAnimator(canvasProperty.getNativeContainer(), i, f));
        this.mUiThreadHandlesDelay = false;
    }

    public RenderNodeAnimator(int i, int i2, float f, float f2) {
        this.mRenderProperty = -1;
        this.mState = 0;
        this.mUnscaledDuration = 300L;
        this.mUnscaledStartDelay = 0L;
        this.mStartDelay = 0L;
        init(nCreateRevealAnimator(i, i2, f, f2));
        this.mUiThreadHandlesDelay = true;
    }

    private void init(long j) {
        this.mNativePtr = new VirtualRefBasePtr(j);
    }

    private void checkMutable() {
        if (this.mState != 0) {
            throw new IllegalStateException("Animator has already started, cannot change it now!");
        }
        if (this.mNativePtr == null) {
            throw new IllegalStateException("Animator's target has been destroyed (trying to modify an animation after activity destroy?)");
        }
    }

    static boolean isNativeInterpolator(TimeInterpolator timeInterpolator) {
        return timeInterpolator.getClass().isAnnotationPresent(HasNativeInterpolator.class);
    }

    private void applyInterpolator() {
        long jCreateNativeInterpolator;
        TimeInterpolator timeInterpolator = this.mInterpolator;
        if (timeInterpolator == null || this.mNativePtr == null) {
            return;
        }
        if (isNativeInterpolator(timeInterpolator)) {
            jCreateNativeInterpolator = ((NativeInterpolatorFactory) this.mInterpolator).createNativeInterpolator();
        } else {
            jCreateNativeInterpolator = FallbackLUTInterpolator.createNativeInterpolator(this.mInterpolator, nGetDuration(this.mNativePtr.get()));
        }
        nSetInterpolator(this.mNativePtr.get(), jCreateNativeInterpolator);
    }

    @Override // android.animation.Animator
    public void start() {
        if (this.mTarget == null) {
            throw new IllegalStateException("Missing target!");
        }
        if (this.mState != 0) {
            throw new IllegalStateException("Already started!");
        }
        this.mState = 1;
        applyInterpolator();
        if (this.mNativePtr == null) {
            cancel();
        } else if (this.mStartDelay <= 0 || !this.mUiThreadHandlesDelay) {
            nSetStartDelay(this.mNativePtr.get(), this.mStartDelay);
            doStart();
        } else {
            getHelper().addDelayedAnimation(this);
        }
    }

    private void doStart() {
        if (this.mRenderProperty == 11) {
            this.mViewTarget.ensureTransformationInfo();
            this.mViewTarget.mTransformationInfo.mAlpha = this.mFinalValue;
        }
        moveToRunningState();
        View view = this.mViewTarget;
        if (view != null) {
            view.invalidateViewProperty(true, false);
        }
    }

    private void moveToRunningState() {
        this.mState = 2;
        VirtualRefBasePtr virtualRefBasePtr = this.mNativePtr;
        if (virtualRefBasePtr != null) {
            nStart(virtualRefBasePtr.get());
        }
        notifyStartListeners();
    }

    private void notifyStartListeners() {
        ArrayList<Animator.AnimatorListener> arrayListCloneListeners = cloneListeners();
        int size = arrayListCloneListeners == null ? 0 : arrayListCloneListeners.size();
        for (int i = 0; i < size; i++) {
            arrayListCloneListeners.get(i).onAnimationStart(this);
        }
    }

    @Override // android.animation.Animator
    public void cancel() {
        int i = this.mState;
        if (i == 0 || i == 3) {
            return;
        }
        if (i == 1) {
            getHelper().removeDelayedAnimation(this);
            moveToRunningState();
        }
        ArrayList<Animator.AnimatorListener> arrayListCloneListeners = cloneListeners();
        int size = arrayListCloneListeners == null ? 0 : arrayListCloneListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayListCloneListeners.get(i2).onAnimationCancel(this);
        }
        end();
    }

    @Override // android.animation.Animator
    public void end() {
        int i = this.mState;
        if (i != 3) {
            if (i < 2) {
                getHelper().removeDelayedAnimation(this);
                doStart();
            }
            VirtualRefBasePtr virtualRefBasePtr = this.mNativePtr;
            if (virtualRefBasePtr != null) {
                nEnd(virtualRefBasePtr.get());
                View view = this.mViewTarget;
                if (view != null) {
                    view.invalidateViewProperty(true, false);
                    return;
                }
                return;
            }
            onFinished();
        }
    }

    @Override // android.animation.Animator
    public void pause() {
        throw new UnsupportedOperationException();
    }

    @Override // android.animation.Animator
    public void resume() {
        throw new UnsupportedOperationException();
    }

    public void setTarget(View view) {
        this.mViewTarget = view;
        setTarget(view.mRenderNode);
    }

    public void setTarget(DisplayListCanvas displayListCanvas) {
        setTarget(displayListCanvas.mNode);
    }

    private void setTarget(RenderNode renderNode) {
        checkMutable();
        if (this.mTarget != null) {
            throw new IllegalStateException("Target already set!");
        }
        nSetListener(this.mNativePtr.get(), this);
        this.mTarget = renderNode;
        renderNode.addAnimator(this);
    }

    public void setStartValue(float f) {
        checkMutable();
        nSetStartValue(this.mNativePtr.get(), f);
    }

    @Override // android.animation.Animator
    public void setStartDelay(long j) {
        checkMutable();
        if (j < 0) {
            throw new IllegalArgumentException("startDelay must be positive; " + j);
        }
        this.mUnscaledStartDelay = j;
        this.mStartDelay = (long) (ValueAnimator.getDurationScale() * j);
    }

    @Override // android.animation.Animator
    public long getStartDelay() {
        return this.mUnscaledStartDelay;
    }

    @Override // android.animation.Animator
    public RenderNodeAnimator setDuration(long j) {
        checkMutable();
        if (j < 0) {
            throw new IllegalArgumentException("duration must be positive; " + j);
        }
        this.mUnscaledDuration = j;
        nSetDuration(this.mNativePtr.get(), (long) (j * ValueAnimator.getDurationScale()));
        return this;
    }

    @Override // android.animation.Animator
    public long getDuration() {
        return this.mUnscaledDuration;
    }

    @Override // android.animation.Animator
    public long getTotalDuration() {
        return this.mUnscaledDuration + this.mUnscaledStartDelay;
    }

    @Override // android.animation.Animator
    public boolean isRunning() {
        int i = this.mState;
        return i == 1 || i == 2;
    }

    @Override // android.animation.Animator
    public boolean isStarted() {
        return this.mState != 0;
    }

    @Override // android.animation.Animator
    public void setInterpolator(TimeInterpolator timeInterpolator) {
        checkMutable();
        this.mInterpolator = timeInterpolator;
    }

    @Override // android.animation.Animator
    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    protected void onFinished() {
        int i = this.mState;
        if (i == 0) {
            releaseNativePtr();
            return;
        }
        if (i == 1) {
            getHelper().removeDelayedAnimation(this);
            notifyStartListeners();
        }
        this.mState = 3;
        ArrayList<Animator.AnimatorListener> arrayListCloneListeners = cloneListeners();
        int size = arrayListCloneListeners == null ? 0 : arrayListCloneListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            arrayListCloneListeners.get(i2).onAnimationEnd(this);
        }
        releaseNativePtr();
    }

    private void releaseNativePtr() {
        VirtualRefBasePtr virtualRefBasePtr = this.mNativePtr;
        if (virtualRefBasePtr != null) {
            virtualRefBasePtr.release();
            this.mNativePtr = null;
        }
    }

    private ArrayList<Animator.AnimatorListener> cloneListeners() {
        ArrayList<Animator.AnimatorListener> listeners = getListeners();
        return listeners != null ? (ArrayList) listeners.clone() : listeners;
    }

    long getNativeAnimator() {
        return this.mNativePtr.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean processDelayed(long j) {
        long j2 = this.mStartTime;
        if (j2 == 0) {
            this.mStartTime = j;
            return false;
        }
        if (j - j2 < this.mStartDelay) {
            return false;
        }
        doStart();
        return true;
    }

    private static DelayedAnimationHelper getHelper() {
        DelayedAnimationHelper delayedAnimationHelper = sAnimationHelper.get();
        if (delayedAnimationHelper != null) {
            return delayedAnimationHelper;
        }
        DelayedAnimationHelper delayedAnimationHelper2 = new DelayedAnimationHelper();
        sAnimationHelper.set(delayedAnimationHelper2);
        return delayedAnimationHelper2;
    }

    private static class DelayedAnimationHelper implements Runnable {
        private boolean mCallbackScheduled;
        private ArrayList<RenderNodeAnimator> mDelayedAnims = new ArrayList<>();
        private final Choreographer mChoreographer = Choreographer.getInstance();

        public void addDelayedAnimation(RenderNodeAnimator renderNodeAnimator) {
            this.mDelayedAnims.add(renderNodeAnimator);
            scheduleCallback();
        }

        public void removeDelayedAnimation(RenderNodeAnimator renderNodeAnimator) {
            this.mDelayedAnims.remove(renderNodeAnimator);
        }

        private void scheduleCallback() {
            if (this.mCallbackScheduled) {
                return;
            }
            this.mCallbackScheduled = true;
            this.mChoreographer.postCallback(1, this, null);
        }

        @Override // java.lang.Runnable
        public void run() {
            long frameTime = this.mChoreographer.getFrameTime();
            this.mCallbackScheduled = false;
            int i = 0;
            for (int i2 = 0; i2 < this.mDelayedAnims.size(); i2++) {
                RenderNodeAnimator renderNodeAnimator = this.mDelayedAnims.get(i2);
                if (!renderNodeAnimator.processDelayed(frameTime)) {
                    if (i != i2) {
                        this.mDelayedAnims.set(i, renderNodeAnimator);
                    }
                    i++;
                }
            }
            while (this.mDelayedAnims.size() > i) {
                ArrayList<RenderNodeAnimator> arrayList = this.mDelayedAnims;
                arrayList.remove(arrayList.size() - 1);
            }
            if (this.mDelayedAnims.size() > 0) {
                scheduleCallback();
            }
        }
    }

    private static void callOnFinished(RenderNodeAnimator renderNodeAnimator) {
        renderNodeAnimator.onFinished();
    }

    @Override // android.animation.Animator
    /* JADX INFO: renamed from: clone */
    public Animator mo0clone() {
        throw new IllegalStateException("Cannot clone this animator");
    }

    @Override // android.animation.Animator
    public void setAllowRunningAsynchronously(boolean z) {
        checkMutable();
        nSetAllowRunningAsync(this.mNativePtr.get(), z);
    }
}
