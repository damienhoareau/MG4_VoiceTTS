package com.android.internal.policy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.view.DisplayInfo;
import com.android.internal.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class DividerSnapAlgorithm {
    private static final int MIN_DISMISS_VELOCITY_DP_PER_SECOND = 600;
    private static final int MIN_FLING_VELOCITY_DP_PER_SECOND = 400;
    private static final int SNAP_FIXED_RATIO = 1;
    private static final int SNAP_MODE_16_9 = 0;
    private static final int SNAP_MODE_MINIMIZED = 3;
    private static final int SNAP_ONLY_1_1 = 2;
    private final SnapTarget mDismissEndTarget;
    private final SnapTarget mDismissStartTarget;
    private final int mDisplayHeight;
    private final int mDisplayWidth;
    private final int mDividerSize;
    private final SnapTarget mFirstSplitTarget;
    private final float mFixedRatio;
    private final Rect mInsets;
    private boolean mIsHorizontalDivision;
    private final SnapTarget mLastSplitTarget;
    private final SnapTarget mMiddleTarget;
    private final float mMinDismissVelocityPxPerSecond;
    private final float mMinFlingVelocityPxPerSecond;
    private final int mMinimalSizeResizableTask;
    private final int mSnapMode;
    private final ArrayList<SnapTarget> mTargets;
    private final int mTaskHeightInMinimizedMode;

    public static DividerSnapAlgorithm create(Context context, Rect rect) {
        DisplayInfo displayInfo = new DisplayInfo();
        ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(0).getDisplayInfo(displayInfo);
        return new DividerSnapAlgorithm(context.getResources(), displayInfo.logicalWidth, displayInfo.logicalHeight, context.getResources().getDimensionPixelSize(R.dimen.docked_stack_divider_thickness) - (context.getResources().getDimensionPixelSize(R.dimen.docked_stack_divider_insets) * 2), context.getApplicationContext().getResources().getConfiguration().orientation == 1, rect);
    }

    public DividerSnapAlgorithm(Resources resources, int i, int i2, int i3, boolean z, Rect rect) {
        this(resources, i, i2, i3, z, rect, -1, false);
    }

    public DividerSnapAlgorithm(Resources resources, int i, int i2, int i3, boolean z, Rect rect, int i4) {
        this(resources, i, i2, i3, z, rect, i4, false);
    }

    public DividerSnapAlgorithm(Resources resources, int i, int i2, int i3, boolean z, Rect rect, int i4, boolean z2) {
        this.mTargets = new ArrayList<>();
        this.mInsets = new Rect();
        this.mMinFlingVelocityPxPerSecond = resources.getDisplayMetrics().density * 400.0f;
        this.mMinDismissVelocityPxPerSecond = resources.getDisplayMetrics().density * 600.0f;
        this.mDividerSize = i3;
        this.mDisplayWidth = i;
        this.mDisplayHeight = i2;
        this.mIsHorizontalDivision = z;
        this.mInsets.set(rect);
        this.mSnapMode = z2 ? 3 : resources.getInteger(R.integer.config_dockedStackDividerSnapMode);
        this.mFixedRatio = resources.getFraction(R.fraction.docked_stack_divider_fixed_ratio, 1, 1);
        this.mMinimalSizeResizableTask = resources.getDimensionPixelSize(R.dimen.default_minimal_size_resizable_task);
        this.mTaskHeightInMinimizedMode = resources.getDimensionPixelSize(R.dimen.task_height_of_minimized_mode);
        calculateTargets(z, i4);
        this.mFirstSplitTarget = this.mTargets.get(1);
        ArrayList<SnapTarget> arrayList = this.mTargets;
        this.mLastSplitTarget = arrayList.get(arrayList.size() - 2);
        this.mDismissStartTarget = this.mTargets.get(0);
        ArrayList<SnapTarget> arrayList2 = this.mTargets;
        this.mDismissEndTarget = arrayList2.get(arrayList2.size() - 1);
        ArrayList<SnapTarget> arrayList3 = this.mTargets;
        this.mMiddleTarget = arrayList3.get(arrayList3.size() / 2);
    }

    public boolean isSplitScreenFeasible() {
        int i;
        int i2 = this.mInsets.top;
        int i3 = this.mIsHorizontalDivision ? this.mInsets.bottom : this.mInsets.right;
        if (this.mIsHorizontalDivision) {
            i = this.mDisplayHeight;
        } else {
            i = this.mDisplayWidth;
        }
        return (((i - i3) - i2) - this.mDividerSize) / 2 >= this.mMinimalSizeResizableTask;
    }

    public SnapTarget calculateSnapTarget(int i, float f) {
        return calculateSnapTarget(i, f, true);
    }

    public SnapTarget calculateSnapTarget(int i, float f, boolean z) {
        if (i < this.mFirstSplitTarget.position && f < (-this.mMinDismissVelocityPxPerSecond)) {
            return this.mDismissStartTarget;
        }
        if (i > this.mLastSplitTarget.position && f > this.mMinDismissVelocityPxPerSecond) {
            return this.mDismissEndTarget;
        }
        if (Math.abs(f) < this.mMinFlingVelocityPxPerSecond) {
            return snap(i, z);
        }
        if (f < 0.0f) {
            return this.mFirstSplitTarget;
        }
        return this.mLastSplitTarget;
    }

    public SnapTarget calculateNonDismissingSnapTarget(int i) {
        SnapTarget snapTargetSnap = snap(i, false);
        if (snapTargetSnap == this.mDismissStartTarget) {
            return this.mFirstSplitTarget;
        }
        return snapTargetSnap == this.mDismissEndTarget ? this.mLastSplitTarget : snapTargetSnap;
    }

    public float calculateDismissingFraction(int i) {
        if (i < this.mFirstSplitTarget.position) {
            return 1.0f - ((i - getStartInset()) / (this.mFirstSplitTarget.position - getStartInset()));
        }
        if (i > this.mLastSplitTarget.position) {
            return (i - this.mLastSplitTarget.position) / ((this.mDismissEndTarget.position - this.mLastSplitTarget.position) - this.mDividerSize);
        }
        return 0.0f;
    }

    public SnapTarget getClosestDismissTarget(int i) {
        if (i < this.mFirstSplitTarget.position) {
            return this.mDismissStartTarget;
        }
        if (i > this.mLastSplitTarget.position) {
            return this.mDismissEndTarget;
        }
        if (i - this.mDismissStartTarget.position < this.mDismissEndTarget.position - i) {
            return this.mDismissStartTarget;
        }
        return this.mDismissEndTarget;
    }

    public SnapTarget getFirstSplitTarget() {
        return this.mFirstSplitTarget;
    }

    public SnapTarget getLastSplitTarget() {
        return this.mLastSplitTarget;
    }

    public SnapTarget getDismissStartTarget() {
        return this.mDismissStartTarget;
    }

    public SnapTarget getDismissEndTarget() {
        return this.mDismissEndTarget;
    }

    private int getStartInset() {
        if (this.mIsHorizontalDivision) {
            return this.mInsets.top;
        }
        return this.mInsets.left;
    }

    private int getEndInset() {
        if (this.mIsHorizontalDivision) {
            return this.mInsets.bottom;
        }
        return this.mInsets.right;
    }

    private SnapTarget snap(int i, boolean z) {
        int size = this.mTargets.size();
        int i2 = -1;
        float f = Float.MAX_VALUE;
        for (int i3 = 0; i3 < size; i3++) {
            SnapTarget snapTarget = this.mTargets.get(i3);
            float fAbs = Math.abs(i - snapTarget.position);
            if (z) {
                fAbs /= snapTarget.distanceMultiplier;
            }
            if (fAbs < f) {
                i2 = i3;
                f = fAbs;
            }
        }
        return this.mTargets.get(i2);
    }

    private void calculateTargets(boolean z, int i) {
        int i2;
        this.mTargets.clear();
        if (z) {
            i2 = this.mDisplayHeight;
        } else {
            i2 = this.mDisplayWidth;
        }
        Rect rect = this.mInsets;
        int i3 = z ? rect.bottom : rect.right;
        int i4 = -this.mDividerSize;
        if (i == 3) {
            i4 += this.mInsets.left;
        }
        this.mTargets.add(new SnapTarget(i4, i4, 1, 0.35f));
        int i5 = this.mSnapMode;
        if (i5 == 0) {
            addRatio16_9Targets(z, i2);
        } else if (i5 == 1) {
            addFixedDivisionTargets(z, i2);
        } else if (i5 == 2) {
            addMiddleTarget(z);
        } else if (i5 == 3) {
            addMinimizedTarget(z, i);
        }
        this.mTargets.add(new SnapTarget(i2 - i3, i2, 2, 0.35f));
    }

    private void addNonDismissingTargets(boolean z, int i, int i2, int i3) {
        maybeAddTarget(i, i - this.mInsets.top);
        addMiddleTarget(z);
        maybeAddTarget(i2, (i3 - this.mInsets.bottom) - (this.mDividerSize + i2));
    }

    private void addFixedDivisionTargets(boolean z, int i) {
        int i2;
        int i3;
        Rect rect = this.mInsets;
        int i4 = z ? rect.top : rect.left;
        if (z) {
            i2 = this.mDisplayHeight;
            i3 = this.mInsets.bottom;
        } else {
            i2 = this.mDisplayWidth;
            i3 = this.mInsets.right;
        }
        int i5 = i2 - i3;
        int i6 = (int) (this.mFixedRatio * (i5 - i4));
        int i7 = this.mDividerSize;
        int i8 = i6 - (i7 / 2);
        addNonDismissingTargets(z, i4 + i8, (i5 - i8) - i7, i);
    }

    private void addRatio16_9Targets(boolean z, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        Rect rect = this.mInsets;
        int i6 = z ? rect.top : rect.left;
        if (z) {
            i2 = this.mDisplayHeight;
            i3 = this.mInsets.bottom;
        } else {
            i2 = this.mDisplayWidth;
            i3 = this.mInsets.right;
        }
        int i7 = i2 - i3;
        Rect rect2 = this.mInsets;
        int i8 = z ? rect2.left : rect2.top;
        if (z) {
            i4 = this.mDisplayWidth;
            i5 = this.mInsets.right;
        } else {
            i4 = this.mDisplayHeight;
            i5 = this.mInsets.bottom;
        }
        int iFloor = (int) Math.floor(((i4 - i5) - i8) * 0.5625f);
        addNonDismissingTargets(z, i6 + iFloor, (i7 - iFloor) - this.mDividerSize, i);
    }

    private void maybeAddTarget(int i, int i2) {
        if (i2 >= this.mMinimalSizeResizableTask) {
            this.mTargets.add(new SnapTarget(i, i, 0));
        }
    }

    private void addMiddleTarget(boolean z) {
        int iCalculateMiddlePosition = DockedDividerUtils.calculateMiddlePosition(z, this.mInsets, this.mDisplayWidth, this.mDisplayHeight, this.mDividerSize);
        this.mTargets.add(new SnapTarget(iCalculateMiddlePosition, iCalculateMiddlePosition, 0));
    }

    private void addMinimizedTarget(boolean z, int i) {
        int i2 = this.mTaskHeightInMinimizedMode + this.mInsets.top;
        if (!z) {
            if (i == 1) {
                i2 += this.mInsets.left;
            } else if (i == 3) {
                i2 = ((this.mDisplayWidth - i2) - this.mInsets.right) - this.mDividerSize;
            }
        }
        this.mTargets.add(new SnapTarget(i2, i2, 0));
    }

    public SnapTarget getMiddleTarget() {
        return this.mMiddleTarget;
    }

    public SnapTarget getNextTarget(SnapTarget snapTarget) {
        int iIndexOf = this.mTargets.indexOf(snapTarget);
        return (iIndexOf == -1 || iIndexOf >= this.mTargets.size() + (-1)) ? snapTarget : this.mTargets.get(iIndexOf + 1);
    }

    public SnapTarget getPreviousTarget(SnapTarget snapTarget) {
        int iIndexOf = this.mTargets.indexOf(snapTarget);
        return (iIndexOf == -1 || iIndexOf <= 0) ? snapTarget : this.mTargets.get(iIndexOf - 1);
    }

    public boolean showMiddleSplitTargetForAccessibility() {
        return this.mTargets.size() + (-2) > 1;
    }

    public boolean isFirstSplitTargetAvailable() {
        return this.mFirstSplitTarget != this.mMiddleTarget;
    }

    public boolean isLastSplitTargetAvailable() {
        return this.mLastSplitTarget != this.mMiddleTarget;
    }

    public SnapTarget cycleNonDismissTarget(SnapTarget snapTarget, int i) {
        int iIndexOf = this.mTargets.indexOf(snapTarget);
        if (iIndexOf == -1) {
            return snapTarget;
        }
        ArrayList<SnapTarget> arrayList = this.mTargets;
        SnapTarget snapTarget2 = arrayList.get(((iIndexOf + arrayList.size()) + i) % this.mTargets.size());
        if (snapTarget2 == this.mDismissStartTarget) {
            return this.mLastSplitTarget;
        }
        return snapTarget2 == this.mDismissEndTarget ? this.mFirstSplitTarget : snapTarget2;
    }

    public static class SnapTarget {
        public static final int FLAG_DISMISS_END = 2;
        public static final int FLAG_DISMISS_START = 1;
        public static final int FLAG_NONE = 0;
        private final float distanceMultiplier;
        public final int flag;
        public final int position;
        public final int taskPosition;

        public SnapTarget(int i, int i2, int i3) {
            this(i, i2, i3, 1.0f);
        }

        public SnapTarget(int i, int i2, int i3, float f) {
            this.position = i;
            this.taskPosition = i2;
            this.flag = i3;
            this.distanceMultiplier = f;
        }
    }
}
