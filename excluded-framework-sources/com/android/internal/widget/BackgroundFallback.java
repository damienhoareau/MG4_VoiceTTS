package com.android.internal.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes3.dex */
public class BackgroundFallback {
    private Drawable mBackgroundFallback;

    public void setDrawable(Drawable drawable) {
        this.mBackgroundFallback = drawable;
    }

    public boolean hasFallback() {
        return this.mBackgroundFallback != null;
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0057  */
    public void draw(ViewGroup viewGroup, ViewGroup viewGroup2, Canvas canvas, View view, View view2, View view3) {
        int i;
        if (hasFallback()) {
            int width = viewGroup.getWidth();
            int height = viewGroup.getHeight();
            int left = viewGroup2.getLeft();
            int top = viewGroup2.getTop();
            int childCount = viewGroup2.getChildCount();
            int iMin = width;
            int iMin2 = height;
            int i2 = 0;
            int iMax = 0;
            int iMax2 = 0;
            while (i2 < childCount) {
                View childAt = viewGroup2.getChildAt(i2);
                int i3 = childCount;
                Drawable background = childAt.getBackground();
                if (childAt == view) {
                    if (background != null || !(childAt instanceof ViewGroup) || ((ViewGroup) childAt).getChildCount() != 0) {
                        iMin = Math.min(iMin, childAt.getLeft() + left);
                        iMin2 = Math.min(iMin2, childAt.getTop() + top);
                        iMax = Math.max(iMax, childAt.getRight() + left);
                        iMax2 = Math.max(iMax2, childAt.getBottom() + top);
                    }
                } else if (childAt.getVisibility() == 0 && isOpaque(background)) {
                    iMin = Math.min(iMin, childAt.getLeft() + left);
                    iMin2 = Math.min(iMin2, childAt.getTop() + top);
                    iMax = Math.max(iMax, childAt.getRight() + left);
                    iMax2 = Math.max(iMax2, childAt.getBottom() + top);
                }
                i2++;
                childCount = i3;
            }
            boolean z = true;
            int i4 = 0;
            while (i4 < 2) {
                View view4 = i4 == 0 ? view2 : view3;
                if (view4 != null && view4.getVisibility() == 0 && view4.getAlpha() == 1.0f && isOpaque(view4.getBackground())) {
                    if (view4.getTop() <= 0 && view4.getBottom() >= height && view4.getLeft() <= 0 && view4.getRight() >= iMin) {
                        iMin = 0;
                    }
                    if (view4.getTop() <= 0 && view4.getBottom() >= height && view4.getLeft() <= iMax && view4.getRight() >= width) {
                        iMax = width;
                    }
                    if (view4.getTop() <= 0 && view4.getBottom() >= iMin2 && view4.getLeft() <= 0 && view4.getRight() >= width) {
                        iMin2 = 0;
                    }
                    if (view4.getTop() <= iMax2 && view4.getBottom() >= height && view4.getLeft() <= 0 && view4.getRight() >= width) {
                        iMax2 = height;
                    }
                    z &= view4.getTop() <= 0 && view4.getBottom() >= iMin2;
                } else {
                    z = false;
                }
                i4++;
            }
            if (z && (viewsCoverEntireWidth(view2, view3, width) || viewsCoverEntireWidth(view3, view2, width))) {
                iMin2 = 0;
            }
            if (iMin >= iMax || iMin2 >= iMax2) {
                return;
            }
            if (iMin2 > 0) {
                i = 0;
                this.mBackgroundFallback.setBounds(0, 0, width, iMin2);
                this.mBackgroundFallback.draw(canvas);
            } else {
                i = 0;
            }
            if (iMin > 0) {
                this.mBackgroundFallback.setBounds(i, iMin2, iMin, height);
                this.mBackgroundFallback.draw(canvas);
            }
            if (iMax < width) {
                this.mBackgroundFallback.setBounds(iMax, iMin2, width, height);
                this.mBackgroundFallback.draw(canvas);
            }
            if (iMax2 < height) {
                this.mBackgroundFallback.setBounds(iMin, iMax2, iMax, height);
                this.mBackgroundFallback.draw(canvas);
            }
        }
    }

    private boolean isOpaque(Drawable drawable) {
        return drawable != null && drawable.getOpacity() == -1;
    }

    private boolean viewsCoverEntireWidth(View view, View view2, int i) {
        return view.getLeft() <= 0 && view.getRight() >= view2.getLeft() && view2.getRight() >= i;
    }
}
