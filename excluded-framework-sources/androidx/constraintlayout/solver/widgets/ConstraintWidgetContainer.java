package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.Metrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ConstraintWidgetContainer extends WidgetContainer {
    private static final boolean DEBUG = false;
    static final boolean DEBUG_GRAPH = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final int MAX_ITERATIONS = 8;
    private static final boolean USE_SNAPSHOT = true;
    int mDebugSolverPassCount;
    public boolean mGroupsWrapOptimized;
    private boolean mHeightMeasuredTooSmall;
    ChainHead[] mHorizontalChainsArray;
    int mHorizontalChainsSize;
    public boolean mHorizontalWrapOptimized;
    private boolean mIsRtl;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    public boolean mSkipSolver;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem;
    ChainHead[] mVerticalChainsArray;
    int mVerticalChainsSize;
    public boolean mVerticalWrapOptimized;
    public List<ConstraintWidgetGroup> mWidgetGroups;
    private boolean mWidthMeasuredTooSmall;
    public int mWrapFixedHeight;
    public int mWrapFixedWidth;

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public String getType() {
        return "ConstraintLayout";
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public void fillMetrics(Metrics metrics) {
        this.mSystem.fillMetrics(metrics);
    }

    public ConstraintWidgetContainer() {
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int i, int i2) {
        super(i, i2);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public void setOptimizationLevel(int i) {
        this.mOptimizationLevel = i;
    }

    public int getOptimizationLevel() {
        return this.mOptimizationLevel;
    }

    public boolean optimizeFor(int i) {
        return (this.mOptimizationLevel & i) == i;
    }

    @Override // androidx.constraintlayout.solver.widgets.WidgetContainer, androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        this.mWidgetGroups.clear();
        this.mSkipSolver = false;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean addChildrenToSolver(LinearSystem linearSystem) {
        addToSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour dimensionBehaviour = constraintWidget.mListDimensionBehaviors[0];
                ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mListDimensionBehaviors[1];
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem);
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
                }
            } else {
                Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                constraintWidget.addToSolver(linearSystem);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 1);
        }
        return true;
    }

    public void updateChildrenFromSolver(LinearSystem linearSystem, boolean[] zArr) {
        zArr[2] = false;
        updateFromSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            constraintWidget.updateFromSolver(linearSystem);
            if (constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                zArr[2] = true;
            }
            if (constraintWidget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                zArr[2] = true;
            }
        }
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.mPaddingLeft = i;
        this.mPaddingTop = i2;
        this.mPaddingRight = i3;
        this.mPaddingBottom = i4;
    }

    public void setRtl(boolean z) {
        this.mIsRtl = z;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void analyze(int i) {
        super.analyze(i);
        int size = this.mChildren.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mChildren.get(i2).analyze(i);
        }
    }

    /* JADX WARN: Code duplicated, block: B:105:0x0253  */
    /* JADX WARN: Code duplicated, block: B:108:0x0266  */
    /* JADX WARN: Code duplicated, block: B:111:0x0283  */
    /* JADX WARN: Code duplicated, block: B:112:0x0290  */
    /* JADX WARN: Code duplicated, block: B:114:0x0295  */
    /* JADX WARN: Code duplicated, block: B:116:0x029e A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:119:0x02a6  */
    /* JADX WARN: Code duplicated, block: B:122:0x02bc A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:126:0x02d2 A[PHI: r0 r9
  0x02d2: PHI (r0v36 ??) = (r0v35 ??), (r0v38 ??), (r0v38 ??), (r0v38 ??) binds: [B:113:0x0293, B:121:0x02ba, B:122:0x02bc, B:124:0x02c2] A[DONT_GENERATE, DONT_INLINE]
  0x02d2: PHI (r9v12 boolean) = (r9v11 boolean), (r9v13 boolean), (r9v13 boolean), (r9v13 boolean) binds: [B:113:0x0293, B:121:0x02ba, B:122:0x02bc, B:124:0x02c2] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:160:0x018f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:162:0x01d7 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:71:0x0188  */
    /* JADX WARN: Code duplicated, block: B:73:0x0191  */
    /* JADX WARN: Code duplicated, block: B:75:0x0199  */
    /* JADX WARN: Code duplicated, block: B:80:0x01bd  */
    /* JADX WARN: Code duplicated, block: B:82:0x01c6  */
    /* JADX WARN: Code duplicated, block: B:87:0x01dd  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v35 */
    /* JADX WARN: Type inference failed for: r0v36 */
    /* JADX WARN: Type inference failed for: r0v38 */
    /* JADX WARN: Type inference failed for: r0v41 */
    /* JADX WARN: Type inference failed for: r0v43 */
    /* JADX WARN: Type inference failed for: r0v90 */
    /* JADX WARN: Type inference failed for: r0v91 */
    /* JADX WARN: Type inference failed for: r0v92 */
    /* JADX WARN: Type inference failed for: r0v93 */
    /* JADX WARN: Type inference failed for: r0v94 */
    /* JADX WARN: Type inference failed for: r18v5 */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v23 */
    /* JADX WARN: Type inference failed for: r8v24, types: [boolean] */
    /* JADX WARN: Type inference failed for: r8v25 */
    /* JADX WARN: Type inference failed for: r8v26 */
    /* JADX WARN: Type inference failed for: r8v27 */
    /* JADX WARN: Type inference failed for: r8v28 */
    /* JADX WARN: Type inference failed for: r8v43 */
    /* JADX WARN: Type inference failed for: r8v45 */
    /* JADX WARN: Type inference failed for: r8v46 */
    /* JADX WARN: Type inference failed for: r8v47, types: [boolean] */
    /* JADX WARN: Type inference failed for: r8v48 */
    /* JADX WARN: Type inference failed for: r8v50 */
    /* JADX WARN: Type inference failed for: r8v57 */
    /* JADX WARN: Type inference failed for: r8v58 */
    /* JADX WARN: Type inference failed for: r8v9 */
    @Override // androidx.constraintlayout.solver.widgets.WidgetContainer
    public void layout() {
        int i;
        ?? r8;
        int i2;
        ConstraintWidget constraintWidget;
        char c;
        int i3;
        boolean z;
        boolean z2;
        int iMax;
        boolean z3;
        boolean z4;
        int iMax2;
        ?? r9;
        boolean z5;
        ?? r0;
        ?? r10;
        boolean z6;
        boolean z7;
        boolean z8;
        int i4 = this.mX;
        int i5 = this.mY;
        int i6 = 0;
        int iMax3 = Math.max(0, getWidth());
        int iMax4 = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int i7 = 32;
        if (this.mOptimizationLevel != 0) {
            if (!optimizeFor(8)) {
                optimizeReset();
            }
            if (!optimizeFor(32)) {
                optimize();
            }
            this.mSystem.graphOptimizer = true;
        } else {
            this.mSystem.graphOptimizer = false;
        }
        ConstraintWidget.DimensionBehaviour dimensionBehaviour = this.mListDimensionBehaviors[1];
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = this.mListDimensionBehaviors[0];
        resetChains();
        if (this.mWidgetGroups.size() == 0) {
            this.mWidgetGroups.clear();
            this.mWidgetGroups.add(0, new ConstraintWidgetGroup(this.mChildren));
        }
        int size = this.mWidgetGroups.size();
        ArrayList<ConstraintWidget> arrayList = this.mChildren;
        boolean z9 = getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        boolean z10 = false;
        int i8 = 0;
        while (i8 < size && !this.mSkipSolver) {
            if (this.mWidgetGroups.get(i8).mSkipSolver) {
                i = size;
            } else {
                if (optimizeFor(i7)) {
                    if (getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED && getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED) {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(i8).getWidgetsToSolve();
                    } else {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(i8).mConstrainedGroup;
                    }
                }
                resetChains();
                int size2 = this.mChildren.size();
                for (int i9 = i6; i9 < size2; i9++) {
                    ConstraintWidget constraintWidget2 = this.mChildren.get(i9);
                    if (constraintWidget2 instanceof WidgetContainer) {
                        ((WidgetContainer) constraintWidget2).layout();
                    }
                }
                boolean z11 = z10 ? 1 : 0;
                int i10 = 0;
                ?? AddChildrenToSolver = 1;
                while (AddChildrenToSolver != 0) {
                    boolean z12 = z11;
                    int i11 = i10 + 1;
                    try {
                        this.mSystem.reset();
                        resetChains();
                        createObjectVariables(this.mSystem);
                        int i12 = 0;
                        while (i12 < size2) {
                            ConstraintWidget constraintWidget3 = this.mChildren.get(i12);
                            boolean z13 = AddChildrenToSolver == true ? 1 : 0;
                            try {
                                constraintWidget3.createObjectVariables(this.mSystem);
                                i12++;
                                AddChildrenToSolver = z13 ? 1 : 0;
                            } catch (Exception e) {
                                e = e;
                                AddChildrenToSolver = z13 ? 1 : 0;
                                e.printStackTrace();
                                System.out.println("EXCEPTION : " + e);
                                r8 = AddChildrenToSolver == true ? 1 : 0;
                                if (r8 != 0) {
                                    updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                                } else {
                                    updateFromSolver(this.mSystem);
                                    i2 = 0;
                                    while (true) {
                                        if (i2 < size2) {
                                            constraintWidget = this.mChildren.get(i2);
                                            if (constraintWidget.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                }
                                                i2++;
                                            } else {
                                                if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                }
                                                i2++;
                                            }
                                        }
                                    }
                                    if (z9) {
                                        i3 = i11;
                                        z = false;
                                        z2 = z12;
                                    } else {
                                        i3 = i11;
                                        z = false;
                                        z2 = z12;
                                    }
                                    z = z7;
                                    z2 = z8;
                                    iMax = Math.max(this.mMinWidth, getWidth());
                                    z4 = z;
                                    z3 = z2;
                                    if (iMax > getWidth()) {
                                        setWidth(iMax);
                                        this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        z4 = true;
                                        z3 = true;
                                    }
                                    iMax2 = Math.max(this.mMinHeight, getHeight());
                                    if (iMax2 > getHeight()) {
                                        setHeight(iMax2);
                                        r9 = 1;
                                        this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        r0 = 1;
                                        z5 = true;
                                    } else {
                                        r9 = 1;
                                        z5 = z3;
                                    }
                                    if (z5) {
                                        r0 = z4;
                                        r10 = r0;
                                        z6 = z5;
                                    } else {
                                        r0 = z4;
                                        if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                            r0 = r0;
                                            z5 = z5;
                                            if (getWidth() > iMax3) {
                                                this.mWidthMeasuredTooSmall = r9;
                                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                                setWidth(iMax3);
                                                ?? r1 = r9;
                                                z5 = r1 == true ? 1 : 0;
                                                r0 = r1;
                                            }
                                        }
                                        r0 = r0;
                                        r0 = r0;
                                        z5 = z5;
                                        z5 = z5;
                                        if (this.mListDimensionBehaviors[r9] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                            r0 = z4;
                                            r10 = r0;
                                            z6 = z5;
                                        } else {
                                            r0 = z4;
                                            r10 = r0;
                                            z6 = z5;
                                        }
                                    }
                                    i10 = i3;
                                    size = size;
                                    z11 = z6;
                                    AddChildrenToSolver = r10;
                                }
                                c = 2;
                                if (z9) {
                                    i3 = i11;
                                    z = false;
                                    z2 = z12;
                                } else {
                                    i3 = i11;
                                    z = false;
                                    z2 = z12;
                                }
                                z = z7;
                                z2 = z8;
                                iMax = Math.max(this.mMinWidth, getWidth());
                                z4 = z;
                                z3 = z2;
                                if (iMax > getWidth()) {
                                    setWidth(iMax);
                                    this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    z4 = true;
                                    z3 = true;
                                }
                                iMax2 = Math.max(this.mMinHeight, getHeight());
                                if (iMax2 > getHeight()) {
                                    setHeight(iMax2);
                                    r9 = 1;
                                    this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    r0 = 1;
                                    z5 = true;
                                } else {
                                    r9 = 1;
                                    z5 = z3;
                                }
                                if (z5) {
                                    r0 = z4;
                                    if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                        r0 = r0;
                                        z5 = z5;
                                        if (getWidth() > iMax3) {
                                            this.mWidthMeasuredTooSmall = r9;
                                            this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                            setWidth(iMax3);
                                            ?? r2 = r9;
                                            z5 = r2 == true ? 1 : 0;
                                            r0 = r2;
                                        }
                                    }
                                    r0 = r0;
                                    r0 = r0;
                                    z5 = z5;
                                    z5 = z5;
                                    if (this.mListDimensionBehaviors[r9] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                        r0 = z4;
                                        r10 = r0;
                                        z6 = z5;
                                    } else {
                                        r0 = z4;
                                        r10 = r0;
                                        z6 = z5;
                                    }
                                } else {
                                    r0 = z4;
                                    r10 = r0;
                                    z6 = z5;
                                }
                                i10 = i3;
                                size = size;
                                z11 = z6;
                                AddChildrenToSolver = r10;
                            }
                        }
                        boolean z14 = AddChildrenToSolver == true ? 1 : 0;
                        AddChildrenToSolver = addChildrenToSolver(this.mSystem);
                        if (AddChildrenToSolver != 0) {
                            try {
                                this.mSystem.minimize();
                            } catch (Exception e2) {
                                e = e2;
                                e.printStackTrace();
                                System.out.println("EXCEPTION : " + e);
                                r8 = AddChildrenToSolver == true ? 1 : 0;
                            }
                        }
                        r8 = AddChildrenToSolver;
                    } catch (Exception e3) {
                        e = e3;
                        boolean z15 = AddChildrenToSolver == true ? 1 : 0;
                    }
                    if (r8 != 0) {
                        updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                    } else {
                        updateFromSolver(this.mSystem);
                        i2 = 0;
                        while (true) {
                            if (i2 < size2) {
                                constraintWidget = this.mChildren.get(i2);
                                if (constraintWidget.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                                    Optimizer.flags[2] = true;
                                    c = 2;
                                    break;
                                } else {
                                    if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                                        c = 2;
                                        Optimizer.flags[2] = true;
                                        break;
                                    }
                                    i2++;
                                }
                            }
                        }
                        if (z9 || i11 >= 8 || !Optimizer.flags[c]) {
                            i3 = i11;
                            z = false;
                            z2 = z12;
                        } else {
                            int i13 = 0;
                            int iMax5 = 0;
                            int iMax6 = 0;
                            while (i13 < size2) {
                                ConstraintWidget constraintWidget4 = this.mChildren.get(i13);
                                iMax5 = Math.max(iMax5, constraintWidget4.mX + constraintWidget4.getWidth());
                                iMax6 = Math.max(iMax6, constraintWidget4.mY + constraintWidget4.getHeight());
                                i13++;
                                i11 = i11;
                            }
                            i3 = i11;
                            int iMax7 = Math.max(this.mMinWidth, iMax5);
                            int iMax8 = Math.max(this.mMinHeight, iMax6);
                            if (dimensionBehaviour2 != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getWidth() >= iMax7) {
                                z7 = false;
                                z8 = z12;
                            } else {
                                setWidth(iMax7);
                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                z7 = true;
                                z8 = true;
                            }
                            z = z7;
                            z2 = z8;
                            if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getHeight() < iMax8) {
                                z = z7;
                                z2 = z8;
                                setHeight(iMax8);
                                this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                z = true;
                                z2 = true;
                            }
                        }
                        z = z7;
                        z2 = z8;
                        iMax = Math.max(this.mMinWidth, getWidth());
                        z4 = z;
                        z3 = z2;
                        if (iMax > getWidth()) {
                            setWidth(iMax);
                            this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                            z4 = true;
                            z3 = true;
                        }
                        iMax2 = Math.max(this.mMinHeight, getHeight());
                        if (iMax2 > getHeight()) {
                            setHeight(iMax2);
                            r9 = 1;
                            this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                            r0 = 1;
                            z5 = true;
                        } else {
                            r9 = 1;
                            z5 = z3;
                        }
                        if (z5) {
                            r0 = z4;
                            if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && iMax3 > 0) {
                                r0 = r0;
                                z5 = z5;
                                if (getWidth() > iMax3) {
                                    this.mWidthMeasuredTooSmall = r9;
                                    this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    setWidth(iMax3);
                                    ?? r3 = r9;
                                    z5 = r3 == true ? 1 : 0;
                                    r0 = r3;
                                }
                            }
                            r0 = r0;
                            r0 = r0;
                            z5 = z5;
                            z5 = z5;
                            if (this.mListDimensionBehaviors[r9] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || iMax4 <= 0 || getHeight() <= iMax4) {
                                r0 = z4;
                                r10 = r0;
                                z6 = z5;
                            } else {
                                this.mHeightMeasuredTooSmall = r9;
                                this.mListDimensionBehaviors[r9] = ConstraintWidget.DimensionBehaviour.FIXED;
                                setHeight(iMax4);
                                z6 = true;
                                r10 = 1;
                            }
                        } else {
                            r0 = z4;
                            r10 = r0;
                            z6 = z5;
                        }
                        i10 = i3;
                        size = size;
                        z11 = z6;
                        AddChildrenToSolver = r10;
                    }
                    c = 2;
                    if (z9) {
                        i3 = i11;
                        z = false;
                        z2 = z12;
                    } else {
                        i3 = i11;
                        z = false;
                        z2 = z12;
                    }
                    z = z7;
                    z2 = z8;
                    iMax = Math.max(this.mMinWidth, getWidth());
                    z4 = z;
                    z3 = z2;
                    if (iMax > getWidth()) {
                        setWidth(iMax);
                        this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                        z4 = true;
                        z3 = true;
                    }
                    iMax2 = Math.max(this.mMinHeight, getHeight());
                    if (iMax2 > getHeight()) {
                        setHeight(iMax2);
                        r9 = 1;
                        this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                        r0 = 1;
                        z5 = true;
                    } else {
                        r9 = 1;
                        z5 = z3;
                    }
                    if (z5) {
                        r0 = z4;
                        if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                            r0 = r0;
                            z5 = z5;
                            if (getWidth() > iMax3) {
                                this.mWidthMeasuredTooSmall = r9;
                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                setWidth(iMax3);
                                ?? r4 = r9;
                                z5 = r4 == true ? 1 : 0;
                                r0 = r4;
                            }
                        }
                        r0 = r0;
                        r0 = r0;
                        z5 = z5;
                        z5 = z5;
                        if (this.mListDimensionBehaviors[r9] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                            r0 = z4;
                            r10 = r0;
                            z6 = z5;
                        } else {
                            r0 = z4;
                            r10 = r0;
                            z6 = z5;
                        }
                    } else {
                        r0 = z4;
                        r10 = r0;
                        z6 = z5;
                    }
                    i10 = i3;
                    size = size;
                    z11 = z6;
                    AddChildrenToSolver = r10;
                }
                i = size;
                this.mWidgetGroups.get(i8).updateUnresolvedWidgets();
                z10 = z11 ? 1 : 0;
            }
            i8++;
            size = i;
            i6 = 0;
            i7 = 32;
            z10 = z10;
        }
        this.mChildren = arrayList;
        if (this.mParent != null) {
            int iMax9 = Math.max(this.mMinWidth, getWidth());
            int iMax10 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(iMax9 + this.mPaddingLeft + this.mPaddingRight);
            setHeight(iMax10 + this.mPaddingTop + this.mPaddingBottom);
        } else {
            this.mX = i4;
            this.mY = i5;
        }
        if (z10) {
            this.mListDimensionBehaviors[0] = dimensionBehaviour2;
            this.mListDimensionBehaviors[1] = dimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public void preOptimize() {
        optimizeReset();
        analyze(this.mOptimizationLevel);
    }

    public void solveGraph() {
        ResolutionAnchor resolutionNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor resolutionNode2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }

    public void resetGraph() {
        ResolutionAnchor resolutionNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor resolutionNode2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        resolutionNode.invalidateAnchors();
        resolutionNode2.invalidateAnchors();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }

    public void optimizeForDimensions(int i, int i2) {
        if (this.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && this.mResolutionWidth != null) {
            this.mResolutionWidth.resolve(i);
        }
        if (this.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mResolutionHeight == null) {
            return;
        }
        this.mResolutionHeight.resolve(i2);
    }

    public void optimizeReset() {
        int size = this.mChildren.size();
        resetResolutionNodes();
        for (int i = 0; i < size; i++) {
            this.mChildren.get(i).resetResolutionNodes();
        }
    }

    public void optimize() {
        if (!optimizeFor(8)) {
            analyze(this.mOptimizationLevel);
        }
        solveGraph();
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    void addChain(ConstraintWidget constraintWidget, int i) {
        if (i == 0) {
            addHorizontalChain(constraintWidget);
        } else if (i == 1) {
            addVerticalChain(constraintWidget);
        }
    }

    private void addHorizontalChain(ConstraintWidget constraintWidget) {
        int i = this.mHorizontalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mHorizontalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mHorizontalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(constraintWidget, 0, isRtl());
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget constraintWidget) {
        int i = this.mVerticalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mVerticalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mVerticalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(constraintWidget, 1, isRtl());
        this.mVerticalChainsSize++;
    }

    public List<ConstraintWidgetGroup> getWidgetGroups() {
        return this.mWidgetGroups;
    }
}
