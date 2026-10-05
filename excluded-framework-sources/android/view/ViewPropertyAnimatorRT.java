package android.view;

import android.animation.TimeInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.android.internal.view.animation.FallbackLUTInterpolator;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
class ViewPropertyAnimatorRT {
    private static final Interpolator sLinearInterpolator = new LinearInterpolator();
    private RenderNodeAnimator[] mAnimators = new RenderNodeAnimator[12];
    private final View mView;

    ViewPropertyAnimatorRT(View view) {
        this.mView = view;
    }

    public boolean startAnimation(ViewPropertyAnimator viewPropertyAnimator) {
        cancelAnimators(viewPropertyAnimator.mPendingAnimations);
        if (!canHandleAnimator(viewPropertyAnimator)) {
            return false;
        }
        doStartAnimation(viewPropertyAnimator);
        return true;
    }

    public void cancelAll() {
        int i = 0;
        while (true) {
            RenderNodeAnimator[] renderNodeAnimatorArr = this.mAnimators;
            if (i >= renderNodeAnimatorArr.length) {
                return;
            }
            if (renderNodeAnimatorArr[i] != null) {
                renderNodeAnimatorArr[i].cancel();
                this.mAnimators[i] = null;
            }
            i++;
        }
    }

    private void doStartAnimation(ViewPropertyAnimator viewPropertyAnimator) {
        int size = viewPropertyAnimator.mPendingAnimations.size();
        long startDelay = viewPropertyAnimator.getStartDelay();
        long duration = viewPropertyAnimator.getDuration();
        TimeInterpolator interpolator = viewPropertyAnimator.getInterpolator();
        if (interpolator == null) {
            interpolator = sLinearInterpolator;
        }
        if (!RenderNodeAnimator.isNativeInterpolator(interpolator)) {
            interpolator = new FallbackLUTInterpolator(interpolator, duration);
        }
        for (int i = 0; i < size; i++) {
            ViewPropertyAnimator.NameValuesHolder nameValuesHolder = viewPropertyAnimator.mPendingAnimations.get(i);
            int iMapViewPropertyToRenderProperty = RenderNodeAnimator.mapViewPropertyToRenderProperty(nameValuesHolder.mNameConstant);
            RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(iMapViewPropertyToRenderProperty, nameValuesHolder.mFromValue + nameValuesHolder.mDeltaValue);
            renderNodeAnimator.setStartDelay(startDelay);
            renderNodeAnimator.setDuration(duration);
            renderNodeAnimator.setInterpolator(interpolator);
            renderNodeAnimator.setTarget(this.mView);
            renderNodeAnimator.start();
            this.mAnimators[iMapViewPropertyToRenderProperty] = renderNodeAnimator;
        }
        viewPropertyAnimator.mPendingAnimations.clear();
    }

    private boolean canHandleAnimator(ViewPropertyAnimator viewPropertyAnimator) {
        return viewPropertyAnimator.getUpdateListener() == null && viewPropertyAnimator.getListener() == null && this.mView.isHardwareAccelerated() && !viewPropertyAnimator.hasActions();
    }

    private void cancelAnimators(ArrayList<ViewPropertyAnimator.NameValuesHolder> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            int iMapViewPropertyToRenderProperty = RenderNodeAnimator.mapViewPropertyToRenderProperty(arrayList.get(i).mNameConstant);
            RenderNodeAnimator[] renderNodeAnimatorArr = this.mAnimators;
            if (renderNodeAnimatorArr[iMapViewPropertyToRenderProperty] != null) {
                renderNodeAnimatorArr[iMapViewPropertyToRenderProperty].cancel();
                this.mAnimators[iMapViewPropertyToRenderProperty] = null;
            }
        }
    }
}
