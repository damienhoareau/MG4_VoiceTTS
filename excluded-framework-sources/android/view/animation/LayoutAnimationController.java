package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.R;
import java.util.Random;

/* JADX INFO: loaded from: classes2.dex */
public class LayoutAnimationController {
    public static final int ORDER_NORMAL = 0;
    public static final int ORDER_RANDOM = 2;
    public static final int ORDER_REVERSE = 1;
    protected Animation mAnimation;
    private float mDelay;
    private long mDuration;
    protected Interpolator mInterpolator;
    private long mMaxDelay;
    private int mOrder;
    protected Random mRandomizer;

    public static class AnimationParameters {
        public int count;
        public int index;
    }

    public LayoutAnimationController(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LayoutAnimation);
        this.mDelay = Animation.Description.parseValue(typedArrayObtainStyledAttributes.peekValue(1)).value;
        this.mOrder = typedArrayObtainStyledAttributes.getInt(3, 0);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(2, 0);
        if (resourceId > 0) {
            setAnimation(context, resourceId);
        }
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        if (resourceId2 > 0) {
            setInterpolator(context, resourceId2);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public LayoutAnimationController(Animation animation) {
        this(animation, 0.5f);
    }

    public LayoutAnimationController(Animation animation, float f) {
        this.mDelay = f;
        setAnimation(animation);
    }

    public int getOrder() {
        return this.mOrder;
    }

    public void setOrder(int i) {
        this.mOrder = i;
    }

    public void setAnimation(Context context, int i) {
        setAnimation(AnimationUtils.loadAnimation(context, i));
    }

    public void setAnimation(Animation animation) {
        this.mAnimation = animation;
        animation.setFillBefore(true);
    }

    public Animation getAnimation() {
        return this.mAnimation;
    }

    public void setInterpolator(Context context, int i) {
        setInterpolator(AnimationUtils.loadInterpolator(context, i));
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public float getDelay() {
        return this.mDelay;
    }

    public void setDelay(float f) {
        this.mDelay = f;
    }

    public boolean willOverlap() {
        return this.mDelay < 1.0f;
    }

    public void start() {
        this.mDuration = this.mAnimation.getDuration();
        this.mMaxDelay = Long.MIN_VALUE;
        this.mAnimation.setStartTime(-1L);
    }

    public final Animation getAnimationForView(View view) {
        long delayForView = getDelayForView(view) + this.mAnimation.getStartOffset();
        this.mMaxDelay = Math.max(this.mMaxDelay, delayForView);
        try {
            Animation animationMo36clone = this.mAnimation.mo36clone();
            animationMo36clone.setStartOffset(delayForView);
            return animationMo36clone;
        } catch (CloneNotSupportedException unused) {
            return null;
        }
    }

    public boolean isDone() {
        return AnimationUtils.currentAnimationTimeMillis() > (this.mAnimation.getStartTime() + this.mMaxDelay) + this.mDuration;
    }

    protected long getDelayForView(View view) {
        AnimationParameters animationParameters = view.getLayoutParams().layoutAnimationParameters;
        if (animationParameters == null) {
            return 0L;
        }
        float duration = this.mDelay * this.mAnimation.getDuration();
        long transformedIndex = (long) (getTransformedIndex(animationParameters) * duration);
        float f = duration * animationParameters.count;
        if (this.mInterpolator == null) {
            this.mInterpolator = new LinearInterpolator();
        }
        return (long) (this.mInterpolator.getInterpolation(transformedIndex / f) * f);
    }

    protected int getTransformedIndex(AnimationParameters animationParameters) {
        int order = getOrder();
        if (order == 1) {
            return (animationParameters.count - 1) - animationParameters.index;
        }
        if (order == 2) {
            if (this.mRandomizer == null) {
                this.mRandomizer = new Random();
            }
            return (int) (animationParameters.count * this.mRandomizer.nextFloat());
        }
        return animationParameters.index;
    }
}
