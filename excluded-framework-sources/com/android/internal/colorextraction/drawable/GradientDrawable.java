package com.android.internal.colorextraction.drawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.graphics.ColorUtils;

/* JADX INFO: loaded from: classes3.dex */
public class GradientDrawable extends Drawable {
    private static final float CENTRALIZED_CIRCLE_1 = -2.0f;
    private static final long COLOR_ANIMATION_DURATION = 2000;
    private static final int GRADIENT_RADIUS = 480;
    private static final String TAG = "GradientDrawable";
    private ValueAnimator mColorAnimation;
    private float mDensity;
    private int mMainColor;
    private int mMainColorTo;
    private final Paint mPaint;
    private int mSecondaryColor;
    private int mSecondaryColorTo;
    private int mAlpha = 255;
    private final Splat mSplat = new Splat(0.5f, 1.0f, 480.0f, CENTRALIZED_CIRCLE_1);
    private final Rect mWindowBounds = new Rect();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public GradientDrawable(Context context) {
        this.mDensity = context.getResources().getDisplayMetrics().density;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL);
    }

    public void setColors(ColorExtractor.GradientColors gradientColors) {
        setColors(gradientColors.getMainColor(), gradientColors.getSecondaryColor(), true);
    }

    public void setColors(ColorExtractor.GradientColors gradientColors, boolean z) {
        setColors(gradientColors.getMainColor(), gradientColors.getSecondaryColor(), z);
    }

    public void setColors(final int i, final int i2, boolean z) {
        if (i == this.mMainColorTo && i2 == this.mSecondaryColorTo) {
            return;
        }
        ValueAnimator valueAnimator = this.mColorAnimation;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mColorAnimation.cancel();
        }
        this.mMainColorTo = i;
        this.mSecondaryColorTo = i;
        if (z) {
            final int i3 = this.mMainColor;
            final int i4 = this.mSecondaryColor;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat.setDuration(COLOR_ANIMATION_DURATION);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.internal.colorextraction.drawable.-$$Lambda$GradientDrawable$lMoQsZzfSN2bVHgYiK0hm0tzCVE
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$setColors$0$GradientDrawable(i3, i, i4, i2, valueAnimator2);
                }
            });
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.colorextraction.drawable.GradientDrawable.1
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator, boolean z2) {
                    if (GradientDrawable.this.mColorAnimation == animator) {
                        GradientDrawable.this.mColorAnimation = null;
                    }
                }
            });
            valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
            valueAnimatorOfFloat.start();
            this.mColorAnimation = valueAnimatorOfFloat;
            return;
        }
        this.mMainColor = i;
        this.mSecondaryColor = i2;
        buildPaints();
        invalidateSelf();
    }

    public /* synthetic */ void lambda$setColors$0$GradientDrawable(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mMainColor = ColorUtils.blendARGB(i, i2, fFloatValue);
        this.mSecondaryColor = ColorUtils.blendARGB(i3, i4, fFloatValue);
        buildPaints();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (i != this.mAlpha) {
            this.mAlpha = i;
            this.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setXfermode(Xfermode xfermode) {
        this.mPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    public void setScreenSize(int i, int i2) {
        this.mWindowBounds.set(0, 0, i, i2);
        setBounds(0, 0, i, i2);
        buildPaints();
    }

    private void buildPaints() {
        Rect rect = this.mWindowBounds;
        if (rect.width() == 0) {
            return;
        }
        this.mPaint.setShader(new RadialGradient(this.mSplat.x * rect.width(), this.mSplat.y * rect.height(), this.mSplat.radius * this.mDensity, this.mSecondaryColor, this.mMainColor, Shader.TileMode.CLAMP));
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect rect = this.mWindowBounds;
        if (rect.width() == 0) {
            throw new IllegalStateException("You need to call setScreenSize before drawing.");
        }
        float fWidth = rect.width();
        float fHeight = rect.height();
        float f = this.mSplat.x * fWidth;
        float f2 = this.mSplat.y * fHeight;
        float fMax = Math.max(fWidth, fHeight);
        canvas.drawRect(f - fMax, f2 - fMax, f + fMax, f2 + fMax, this.mPaint);
    }

    public int getMainColor() {
        return this.mMainColor;
    }

    public int getSecondaryColor() {
        return this.mSecondaryColor;
    }

    static final class Splat {
        final float colorIndex;
        final float radius;
        final float x;
        final float y;

        Splat(float f, float f2, float f3, float f4) {
            this.x = f;
            this.y = f2;
            this.radius = f3;
            this.colorIndex = f4;
        }
    }
}
