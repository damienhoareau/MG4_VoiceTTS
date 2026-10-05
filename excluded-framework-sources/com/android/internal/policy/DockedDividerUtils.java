package com.android.internal.policy;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes3.dex */
public class DockedDividerUtils {
    public static int getDockSideFromCreatedMode(boolean z, boolean z2) {
        if (z) {
            return z2 ? 2 : 1;
        }
        return z2 ? 4 : 3;
    }

    public static int invertDockSide(int i) {
        if (i == 1) {
            return 3;
        }
        if (i == 2) {
            return 4;
        }
        if (i != 3) {
            return i != 4 ? -1 : 2;
        }
        return 1;
    }

    public static void calculateBoundsForPosition(int i, int i2, Rect rect, int i3, int i4, int i5) {
        rect.set(0, 0, i3, i4);
        if (i2 == 1) {
            rect.right = i;
        } else if (i2 == 2) {
            rect.bottom = i;
        } else if (i2 == 3) {
            rect.left = i + i5;
        } else if (i2 == 4) {
            rect.top = i + i5;
        }
        sanitizeStackBounds(rect, i2 == 1 || i2 == 2);
    }

    public static void sanitizeStackBounds(Rect rect, boolean z) {
        if (z) {
            if (rect.left >= rect.right) {
                rect.left = rect.right - 1;
            }
            if (rect.top >= rect.bottom) {
                rect.top = rect.bottom - 1;
                return;
            }
            return;
        }
        if (rect.right <= rect.left) {
            rect.right = rect.left + 1;
        }
        if (rect.bottom <= rect.top) {
            rect.bottom = rect.top + 1;
        }
    }

    public static int calculatePositionForBounds(Rect rect, int i, int i2) {
        int i3;
        if (i == 1) {
            return rect.right;
        }
        if (i == 2) {
            return rect.bottom;
        }
        if (i == 3) {
            i3 = rect.left;
        } else {
            if (i != 4) {
                return 0;
            }
            i3 = rect.top;
        }
        return i3 - i2;
    }

    public static int calculateMiddlePosition(boolean z, Rect rect, int i, int i2, int i3) {
        int i4;
        int i5 = z ? rect.top : rect.left;
        if (z) {
            i4 = i2 - rect.bottom;
        } else {
            i4 = i - rect.right;
        }
        return (i5 + ((i4 - i5) / 2)) - (i3 / 2);
    }
}
