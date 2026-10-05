package android.widget;

import android.app.slice.Slice;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AbsListView extends AdapterView<ListAdapter> implements TextWatcher, ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener, ViewTreeObserver.OnTouchModeChangeListener, RemoteViewsAdapter.RemoteAdapterConnectionCallback {
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    private static final int INVALID_POINTER = -1;
    static final int LAYOUT_FORCE_BOTTOM = 3;
    static final int LAYOUT_FORCE_TOP = 1;
    static final int LAYOUT_MOVE_SELECTION = 6;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_SET_SELECTION = 2;
    static final int LAYOUT_SPECIFIC = 4;
    static final int LAYOUT_SYNC = 5;
    static final int OVERSCROLL_LIMIT_DIVISOR = 3;
    private static final boolean PROFILE_FLINGING = false;
    private static final boolean PROFILE_SCROLLING = false;
    private static final String TAG = "AbsListView";
    static final int TOUCH_MODE_DONE_WAITING = 2;
    static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_FLING = 4;
    private static final int TOUCH_MODE_OFF = 1;
    private static final int TOUCH_MODE_ON = 0;
    static final int TOUCH_MODE_OVERFLING = 6;
    static final int TOUCH_MODE_OVERSCROLL = 5;
    static final int TOUCH_MODE_REST = -1;
    static final int TOUCH_MODE_SCROLL = 3;
    static final int TOUCH_MODE_TAP = 1;
    private static final int TOUCH_MODE_UNKNOWN = -1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    static final Interpolator sLinearInterpolator = new LinearInterpolator();
    private ListItemAccessibilityDelegate mAccessibilityDelegate;
    private int mActivePointerId;
    ListAdapter mAdapter;
    boolean mAdapterHasStableIds;
    private int mCacheColorHint;
    boolean mCachingActive;
    boolean mCachingStarted;
    SparseBooleanArray mCheckStates;
    LongSparseArray<Integer> mCheckedIdStates;
    int mCheckedItemCount;
    ActionMode mChoiceActionMode;
    int mChoiceMode;
    private Runnable mClearScrollingCache;
    private ContextMenu.ContextMenuInfo mContextMenuInfo;
    AdapterDataSetObserver mDataSetObserver;
    private InputConnection mDefInputConnection;
    private boolean mDeferNotifyDataSetChanged;
    private float mDensityScale;
    private int mDirection;
    boolean mDrawSelectorOnTop;
    private EdgeEffect mEdgeGlowBottom;
    private EdgeEffect mEdgeGlowTop;
    private FastScroller mFastScroll;
    boolean mFastScrollAlwaysVisible;
    boolean mFastScrollEnabled;
    private int mFastScrollStyle;
    private boolean mFiltered;
    private int mFirstPositionDistanceGuess;
    private boolean mFlingProfilingStarted;
    private FlingRunnable mFlingRunnable;
    private StrictMode.Span mFlingStrictSpan;
    private boolean mForceTranscriptScroll;
    private boolean mGlobalLayoutListenerAddedFilter;
    private boolean mHasPerformedLongPress;
    private boolean mIsChildViewEnabled;
    private boolean mIsDetaching;
    final boolean[] mIsScrap;
    private int mLastAccessibilityScrollEventFromIndex;
    private int mLastAccessibilityScrollEventToIndex;
    private int mLastHandledItemCount;
    private int mLastPositionDistanceGuess;
    private int mLastScrollState;
    private int mLastTouchMode;
    int mLastY;
    int mLayoutMode;
    Rect mListPadding;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    int mMotionCorrection;
    int mMotionPosition;
    int mMotionViewNewTop;
    int mMotionViewOriginalTop;
    int mMotionX;
    int mMotionY;
    MultiChoiceModeWrapper mMultiChoiceModeCallback;
    private int mNestedYOffset;
    private OnScrollListener mOnScrollListener;
    int mOverflingDistance;
    int mOverscrollDistance;
    int mOverscrollMax;
    private final Thread mOwnerThread;
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private SavedState mPendingSync;
    private PerformClick mPerformClick;
    PopupWindow mPopup;
    private boolean mPopupHidden;
    Runnable mPositionScrollAfterLayout;
    AbsPositionScroller mPositionScroller;
    private InputConnectionWrapper mPublicInputConnection;
    final RecycleBin mRecycler;
    private RemoteViewsAdapter mRemoteAdapter;
    int mResurrectToPosition;
    private final int[] mScrollConsumed;
    View mScrollDown;
    private final int[] mScrollOffset;
    private boolean mScrollProfilingStarted;
    private StrictMode.Span mScrollStrictSpan;
    View mScrollUp;
    boolean mScrollingCacheEnabled;
    int mSelectedTop;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    Drawable mSelector;
    int mSelectorPosition;
    Rect mSelectorRect;
    private int[] mSelectorState;
    private boolean mSmoothScrollbarEnabled;
    boolean mStackFromBottom;
    EditText mTextFilter;
    private boolean mTextFilterEnabled;
    private final float[] mTmpPoint;
    private Rect mTouchFrame;
    int mTouchMode;
    private Runnable mTouchModeReset;
    private int mTouchSlop;
    private int mTranscriptMode;
    private float mVelocityScale;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;
    int mWidthMeasureSpec;

    public interface MultiChoiceModeListener extends ActionMode.Callback {
        void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z);
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScroll(AbsListView absListView, int i, int i2, int i3);

        void onScrollStateChanged(AbsListView absListView, int i);
    }

    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    public interface SelectionBoundsAdjuster {
        void adjustListItemSelectionBounds(Rect rect);
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDrawableHotspotChanged(float f, float f2) {
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean z) {
    }

    abstract void fillGap(boolean z);

    abstract int findMotionRow(int i);

    int getFooterViewsCount() {
        return 0;
    }

    int getHeaderViewsCount() {
        return 0;
    }

    @Override // android.view.View
    protected boolean handleScrollBarDragging(MotionEvent motionEvent) {
        return false;
    }

    protected void layoutChildren() {
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void onRemoteAdapterDisconnected() {
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return (i & 2) != 0;
    }

    abstract void setSelectionInt(int i);

    public AbsListView(Context context) {
        super(context);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mTmpPoint = new float[2];
        this.mNestedYOffset = 0;
        this.mActivePointerId = -1;
        this.mDirection = 0;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        setVerticalScrollBarEnabled(true);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(R.styleable.View);
        initializeScrollbarsInternal(typedArrayObtainStyledAttributes);
        typedArrayObtainStyledAttributes.recycle();
    }

    public AbsListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842858);
    }

    public AbsListView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AbsListView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mTmpPoint = new float[2];
        this.mNestedYOffset = 0;
        this.mActivePointerId = -1;
        this.mDirection = 0;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AbsListView, i, i2);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        if (drawable != null) {
            setSelector(drawable);
        }
        this.mDrawSelectorOnTop = typedArrayObtainStyledAttributes.getBoolean(1, false);
        setStackFromBottom(typedArrayObtainStyledAttributes.getBoolean(2, false));
        setScrollingCacheEnabled(typedArrayObtainStyledAttributes.getBoolean(3, true));
        setTextFilterEnabled(typedArrayObtainStyledAttributes.getBoolean(4, false));
        setTranscriptMode(typedArrayObtainStyledAttributes.getInt(5, 0));
        setCacheColorHint(typedArrayObtainStyledAttributes.getColor(6, 0));
        setSmoothScrollbarEnabled(typedArrayObtainStyledAttributes.getBoolean(9, true));
        setChoiceMode(typedArrayObtainStyledAttributes.getInt(7, 0));
        setFastScrollEnabled(typedArrayObtainStyledAttributes.getBoolean(8, false));
        setFastScrollStyle(typedArrayObtainStyledAttributes.getResourceId(11, 0));
        setFastScrollAlwaysVisible(typedArrayObtainStyledAttributes.getBoolean(10, false));
        typedArrayObtainStyledAttributes.recycle();
        if (context.getResources().getConfiguration().uiMode == 6) {
            setRevealOnFocusHint(false);
        }
    }

    private void initAbsListView() {
        setClickable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setScrollingCacheEnabled(true);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mVerticalScrollFactor = viewConfiguration.getScaledVerticalScrollFactor();
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mOverscrollDistance = viewConfiguration.getScaledOverscrollDistance();
        this.mOverflingDistance = viewConfiguration.getScaledOverflingDistance();
        this.mDensityScale = getContext().getResources().getDisplayMetrics().density;
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        if (i != 2) {
            if (this.mEdgeGlowTop == null) {
                Context context = getContext();
                this.mEdgeGlowTop = new EdgeEffect(context);
                this.mEdgeGlowBottom = new EdgeEffect(context);
            }
        } else {
            this.mEdgeGlowTop = null;
            this.mEdgeGlowBottom = null;
        }
        super.setOverScrollMode(i);
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter listAdapter) {
        if (listAdapter != null) {
            boolean zHasStableIds = this.mAdapter.hasStableIds();
            this.mAdapterHasStableIds = zHasStableIds;
            if (this.mChoiceMode != 0 && zHasStableIds && this.mCheckedIdStates == null) {
                this.mCheckedIdStates = new LongSparseArray<>();
            }
        }
        clearChoices();
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }

    public boolean isItemChecked(int i) {
        SparseBooleanArray sparseBooleanArray;
        if (this.mChoiceMode == 0 || (sparseBooleanArray = this.mCheckStates) == null) {
            return false;
        }
        return sparseBooleanArray.get(i);
    }

    public int getCheckedItemPosition() {
        SparseBooleanArray sparseBooleanArray;
        if (this.mChoiceMode == 1 && (sparseBooleanArray = this.mCheckStates) != null && sparseBooleanArray.size() == 1) {
            return this.mCheckStates.keyAt(0);
        }
        return -1;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (this.mChoiceMode != 0) {
            return this.mCheckStates;
        }
        return null;
    }

    public long[] getCheckedItemIds() {
        LongSparseArray<Integer> longSparseArray;
        if (this.mChoiceMode == 0 || (longSparseArray = this.mCheckedIdStates) == null || this.mAdapter == null) {
            return new long[0];
        }
        int size = longSparseArray.size();
        long[] jArr = new long[size];
        for (int i = 0; i < size; i++) {
            jArr[i] = longSparseArray.keyAt(i);
        }
        return jArr;
    }

    public void clearChoices() {
        SparseBooleanArray sparseBooleanArray = this.mCheckStates;
        if (sparseBooleanArray != null) {
            sparseBooleanArray.clear();
        }
        LongSparseArray<Integer> longSparseArray = this.mCheckedIdStates;
        if (longSparseArray != null) {
            longSparseArray.clear();
        }
        this.mCheckedItemCount = 0;
    }

    public void setItemChecked(int i, boolean z) {
        boolean z2;
        int i2 = this.mChoiceMode;
        if (i2 == 0) {
            return;
        }
        if (z && i2 == 3 && this.mChoiceActionMode == null) {
            MultiChoiceModeWrapper multiChoiceModeWrapper = this.mMultiChoiceModeCallback;
            if (multiChoiceModeWrapper == null || !multiChoiceModeWrapper.hasWrappedCallback()) {
                throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
            }
            this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
        }
        int i3 = this.mChoiceMode;
        if (i3 == 2 || i3 == 3) {
            boolean z3 = this.mCheckStates.get(i);
            this.mCheckStates.put(i, z);
            if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                if (z) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(i), Integer.valueOf(i));
                } else {
                    this.mCheckedIdStates.delete(this.mAdapter.getItemId(i));
                }
            }
            boolean z4 = z3 != z;
            if (z4) {
                if (z) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
            }
            if (this.mChoiceActionMode != null) {
                this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, i, this.mAdapter.getItemId(i), z);
            }
            z2 = z4;
        } else {
            boolean z5 = this.mCheckedIdStates != null && this.mAdapter.hasStableIds();
            z2 = isItemChecked(i) != z;
            if (z || isItemChecked(i)) {
                this.mCheckStates.clear();
                if (z5) {
                    this.mCheckedIdStates.clear();
                }
            }
            if (z) {
                this.mCheckStates.put(i, true);
                if (z5) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(i), Integer.valueOf(i));
                }
                this.mCheckedItemCount = 1;
            } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                this.mCheckedItemCount = 0;
            }
        }
        if (this.mInLayout || this.mBlockLayoutRequests || !z2) {
            return;
        }
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
    }

    @Override // android.widget.AdapterView
    public boolean performItemClick(View view, int i, long j) {
        boolean z;
        int i2 = this.mChoiceMode;
        boolean z2 = false;
        boolean z3 = true;
        if (i2 != 0) {
            if (i2 == 2 || (i2 == 3 && this.mChoiceActionMode != null)) {
                boolean z4 = !this.mCheckStates.get(i, false);
                this.mCheckStates.put(i, z4);
                if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if (z4) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(i), Integer.valueOf(i));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(i));
                    }
                }
                if (z4) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
                ActionMode actionMode = this.mChoiceActionMode;
                if (actionMode != null) {
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(actionMode, i, j, z4);
                } else {
                    z2 = true;
                }
                z = z2;
                z2 = true;
            } else if (this.mChoiceMode == 1) {
                if (!this.mCheckStates.get(i, false)) {
                    this.mCheckStates.clear();
                    this.mCheckStates.put(i, true);
                    if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                        this.mCheckedIdStates.clear();
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(i), Integer.valueOf(i));
                    }
                    this.mCheckedItemCount = 1;
                } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                    this.mCheckedItemCount = 0;
                }
                z = true;
                z2 = true;
            } else {
                z = true;
            }
            if (z2) {
                updateOnScreenCheckedViews();
            }
            z2 = true;
            z3 = z;
        }
        return z3 ? z2 | super.performItemClick(view, i, j) : z2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateOnScreenCheckedViews() {
        int i = this.mFirstPosition;
        int childCount = getChildCount();
        boolean z = getContext().getApplicationInfo().targetSdkVersion >= 11;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            int i3 = i + i2;
            if (childAt instanceof Checkable) {
                ((Checkable) childAt).setChecked(this.mCheckStates.get(i3));
            } else if (z) {
                childAt.setActivated(this.mCheckStates.get(i3));
            }
        }
    }

    public int getChoiceMode() {
        return this.mChoiceMode;
    }

    public void setChoiceMode(int i) {
        ListAdapter listAdapter;
        this.mChoiceMode = i;
        ActionMode actionMode = this.mChoiceActionMode;
        if (actionMode != null) {
            actionMode.finish();
            this.mChoiceActionMode = null;
        }
        if (this.mChoiceMode != 0) {
            if (this.mCheckStates == null) {
                this.mCheckStates = new SparseBooleanArray(0);
            }
            if (this.mCheckedIdStates == null && (listAdapter = this.mAdapter) != null && listAdapter.hasStableIds()) {
                this.mCheckedIdStates = new LongSparseArray<>(0);
            }
            if (this.mChoiceMode == 3) {
                clearChoices();
                setLongClickable(true);
            }
        }
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener multiChoiceModeListener) {
        if (this.mMultiChoiceModeCallback == null) {
            this.mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
        }
        this.mMultiChoiceModeCallback.setWrapped(multiChoiceModeListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean contentFits() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        if (childCount != this.mItemCount) {
            return false;
        }
        return getChildAt(0).getTop() >= this.mListPadding.top && getChildAt(childCount - 1).getBottom() <= getHeight() - this.mListPadding.bottom;
    }

    public void setFastScrollEnabled(final boolean z) {
        if (this.mFastScrollEnabled != z) {
            this.mFastScrollEnabled = z;
            if (isOwnerThread()) {
                setFastScrollerEnabledUiThread(z);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerEnabledUiThread(z);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerEnabledUiThread(boolean z) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setEnabled(z);
        } else if (z) {
            FastScroller fastScroller2 = new FastScroller(this, this.mFastScrollStyle);
            this.mFastScroll = fastScroller2;
            fastScroller2.setEnabled(true);
        }
        resolvePadding();
        FastScroller fastScroller3 = this.mFastScroll;
        if (fastScroller3 != null) {
            fastScroller3.updateLayout();
        }
    }

    public void setFastScrollStyle(int i) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null) {
            this.mFastScrollStyle = i;
        } else {
            fastScroller.setStyle(i);
        }
    }

    public void setFastScrollAlwaysVisible(final boolean z) {
        if (this.mFastScrollAlwaysVisible != z) {
            if (z && !this.mFastScrollEnabled) {
                setFastScrollEnabled(true);
            }
            this.mFastScrollAlwaysVisible = z;
            if (isOwnerThread()) {
                setFastScrollerAlwaysVisibleUiThread(z);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerAlwaysVisibleUiThread(z);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerAlwaysVisibleUiThread(boolean z) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setAlwaysShow(z);
        }
    }

    private boolean isOwnerThread() {
        return this.mOwnerThread == Thread.currentThread();
    }

    public boolean isFastScrollAlwaysVisible() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null) {
            return this.mFastScrollEnabled && this.mFastScrollAlwaysVisible;
        }
        return fastScroller.isEnabled() && this.mFastScroll.isAlwaysShowEnabled();
    }

    @Override // android.view.View
    public int getVerticalScrollbarWidth() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && fastScroller.isEnabled()) {
            return Math.max(super.getVerticalScrollbarWidth(), this.mFastScroll.getWidth());
        }
        return super.getVerticalScrollbarWidth();
    }

    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null) {
            return this.mFastScrollEnabled;
        }
        return fastScroller.isEnabled();
    }

    @Override // android.view.View
    public void setVerticalScrollbarPosition(int i) {
        super.setVerticalScrollbarPosition(i);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollbarPosition(i);
        }
    }

    @Override // android.view.View
    public void setScrollBarStyle(int i) {
        super.setScrollBarStyle(i);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollBarStyle(i);
        }
    }

    @Override // android.view.View
    protected boolean isVerticalScrollBarHidden() {
        return isFastScrollEnabled();
    }

    public void setSmoothScrollbarEnabled(boolean z) {
        this.mSmoothScrollbarEnabled = z;
    }

    @ViewDebug.ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
        invokeOnItemScrollListener();
    }

    void invokeOnItemScrollListener() {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onScroll(this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        onScrollChanged(0, 0, 0, 0);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            int firstVisiblePosition = getFirstVisiblePosition();
            int lastVisiblePosition = getLastVisiblePosition();
            if (this.mLastAccessibilityScrollEventFromIndex == firstVisiblePosition && this.mLastAccessibilityScrollEventToIndex == lastVisiblePosition) {
                return;
            }
            this.mLastAccessibilityScrollEventFromIndex = firstVisiblePosition;
            this.mLastAccessibilityScrollEventToIndex = lastVisiblePosition;
        }
        super.sendAccessibilityEventUnchecked(accessibilityEvent);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AbsListView.class.getName();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (isEnabled()) {
            if (canScrollUp()) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
                accessibilityNodeInfo.setScrollable(true);
            }
            if (canScrollDown()) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
                accessibilityNodeInfo.setScrollable(true);
            }
        }
        accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
        accessibilityNodeInfo.setClickable(false);
    }

    int getSelectionModeForAccessibility() {
        int choiceMode = getChoiceMode();
        int i = 1;
        if (choiceMode != 1) {
            i = 2;
            if (choiceMode != 2 && choiceMode != 3) {
                return 0;
            }
        }
        return i;
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        if (i != 4096) {
            if (i == 8192 || i == 16908344) {
                if (!isEnabled() || !canScrollUp()) {
                    return false;
                }
                smoothScrollBy(-((getHeight() - this.mListPadding.top) - this.mListPadding.bottom), 200);
                return true;
            }
            if (i != 16908346) {
                return false;
            }
        }
        if (!isEnabled() || !canScrollDown()) {
            return false;
        }
        smoothScrollBy((getHeight() - this.mListPadding.top) - this.mListPadding.bottom, 200);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public View findViewByAccessibilityIdTraversal(int i) {
        return i == getAccessibilityViewId() ? this : super.findViewByAccessibilityIdTraversal(i);
    }

    @ViewDebug.ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean z) {
        if (this.mScrollingCacheEnabled && !z) {
            clearScrollingCache();
        }
        this.mScrollingCacheEnabled = z;
    }

    public void setTextFilterEnabled(boolean z) {
        this.mTextFilterEnabled = z;
    }

    @ViewDebug.ExportedProperty
    public boolean isTextFilterEnabled() {
        return this.mTextFilterEnabled;
    }

    @Override // android.view.View
    public void getFocusedRect(Rect rect) {
        View selectedView = getSelectedView();
        if (selectedView != null && selectedView.getParent() == this) {
            selectedView.getFocusedRect(rect);
            offsetDescendantRectToMyCoords(selectedView, rect);
        } else {
            super.getFocusedRect(rect);
        }
    }

    private void useDefaultSelector() {
        setSelector(getContext().getDrawable(17301602));
    }

    @ViewDebug.ExportedProperty
    public boolean isStackFromBottom() {
        return this.mStackFromBottom;
    }

    public void setStackFromBottom(boolean z) {
        if (this.mStackFromBottom != z) {
            this.mStackFromBottom = z;
            requestLayoutIfNecessary();
        }
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.AbsListView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        LongSparseArray<Integer> checkIdState;
        SparseBooleanArray checkState;
        int checkedItemCount;
        String filter;
        long firstId;
        int height;
        boolean inActionMode;
        int position;
        long selectedId;
        int viewTop;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.selectedId = parcel.readLong();
            this.firstId = parcel.readLong();
            this.viewTop = parcel.readInt();
            this.position = parcel.readInt();
            this.height = parcel.readInt();
            this.filter = parcel.readString();
            this.inActionMode = parcel.readByte() != 0;
            this.checkedItemCount = parcel.readInt();
            this.checkState = parcel.readSparseBooleanArray();
            int i = parcel.readInt();
            if (i > 0) {
                this.checkIdState = new LongSparseArray<>();
                for (int i2 = 0; i2 < i; i2++) {
                    this.checkIdState.put(parcel.readLong(), Integer.valueOf(parcel.readInt()));
                }
            }
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeLong(this.selectedId);
            parcel.writeLong(this.firstId);
            parcel.writeInt(this.viewTop);
            parcel.writeInt(this.position);
            parcel.writeInt(this.height);
            parcel.writeString(this.filter);
            parcel.writeByte(this.inActionMode ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.checkedItemCount);
            parcel.writeSparseBooleanArray(this.checkState);
            LongSparseArray<Integer> longSparseArray = this.checkIdState;
            int size = longSparseArray != null ? longSparseArray.size() : 0;
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeLong(this.checkIdState.keyAt(i2));
                parcel.writeInt(this.checkIdState.valueAt(i2).intValue());
            }
        }

        public String toString() {
            return "AbsListView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " firstId=" + this.firstId + " viewTop=" + this.viewTop + " position=" + this.position + " height=" + this.height + " filter=" + this.filter + " checkState=" + this.checkState + "}";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        EditText editText;
        Editable text;
        dismissPopup();
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SavedState savedState2 = this.mPendingSync;
        if (savedState2 != null) {
            savedState.selectedId = savedState2.selectedId;
            savedState.firstId = this.mPendingSync.firstId;
            savedState.viewTop = this.mPendingSync.viewTop;
            savedState.position = this.mPendingSync.position;
            savedState.height = this.mPendingSync.height;
            savedState.filter = this.mPendingSync.filter;
            savedState.inActionMode = this.mPendingSync.inActionMode;
            savedState.checkedItemCount = this.mPendingSync.checkedItemCount;
            savedState.checkState = this.mPendingSync.checkState;
            savedState.checkIdState = this.mPendingSync.checkIdState;
            return savedState;
        }
        boolean z = getChildCount() > 0 && this.mItemCount > 0;
        long selectedItemId = getSelectedItemId();
        savedState.selectedId = selectedItemId;
        savedState.height = getHeight();
        if (selectedItemId >= 0) {
            savedState.viewTop = this.mSelectedTop;
            savedState.position = getSelectedItemPosition();
            savedState.firstId = -1L;
        } else if (z && this.mFirstPosition > 0) {
            savedState.viewTop = getChildAt(0).getTop();
            int i = this.mFirstPosition;
            if (i >= this.mItemCount) {
                i = this.mItemCount - 1;
            }
            savedState.position = i;
            savedState.firstId = this.mAdapter.getItemId(i);
        } else {
            savedState.viewTop = 0;
            savedState.firstId = -1L;
            savedState.position = 0;
        }
        savedState.filter = null;
        if (this.mFiltered && (editText = this.mTextFilter) != null && (text = editText.getText()) != null) {
            savedState.filter = text.toString();
        }
        savedState.inActionMode = this.mChoiceMode == 3 && this.mChoiceActionMode != null;
        SparseBooleanArray sparseBooleanArray = this.mCheckStates;
        if (sparseBooleanArray != null) {
            savedState.checkState = sparseBooleanArray.m33clone();
        }
        if (this.mCheckedIdStates != null) {
            LongSparseArray<Integer> longSparseArray = new LongSparseArray<>();
            int size = this.mCheckedIdStates.size();
            for (int i2 = 0; i2 < size; i2++) {
                longSparseArray.put(this.mCheckedIdStates.keyAt(i2), this.mCheckedIdStates.valueAt(i2));
            }
            savedState.checkIdState = longSparseArray;
        }
        savedState.checkedItemCount = this.mCheckedItemCount;
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.saveRemoteViewsCache();
        }
        return savedState;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        MultiChoiceModeWrapper multiChoiceModeWrapper;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mDataChanged = true;
        this.mSyncHeight = savedState.height;
        if (savedState.selectedId >= 0) {
            this.mNeedSync = true;
            this.mPendingSync = savedState;
            this.mSyncRowId = savedState.selectedId;
            this.mSyncPosition = savedState.position;
            this.mSpecificTop = savedState.viewTop;
            this.mSyncMode = 0;
        } else if (savedState.firstId >= 0) {
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectorPosition = -1;
            this.mNeedSync = true;
            this.mPendingSync = savedState;
            this.mSyncRowId = savedState.firstId;
            this.mSyncPosition = savedState.position;
            this.mSpecificTop = savedState.viewTop;
            this.mSyncMode = 1;
        }
        setFilterText(savedState.filter);
        if (savedState.checkState != null) {
            this.mCheckStates = savedState.checkState;
        }
        if (savedState.checkIdState != null) {
            this.mCheckedIdStates = savedState.checkIdState;
        }
        this.mCheckedItemCount = savedState.checkedItemCount;
        if (savedState.inActionMode && this.mChoiceMode == 3 && (multiChoiceModeWrapper = this.mMultiChoiceModeCallback) != null) {
            this.mChoiceActionMode = startActionMode(multiChoiceModeWrapper);
        }
        requestLayout();
    }

    private boolean acceptFilter() {
        return this.mTextFilterEnabled && (getAdapter() instanceof Filterable) && ((Filterable) getAdapter()).getFilter() != null;
    }

    public void setFilterText(String str) {
        if (!this.mTextFilterEnabled || TextUtils.isEmpty(str)) {
            return;
        }
        createTextFilter(false);
        this.mTextFilter.setText(str);
        this.mTextFilter.setSelection(str.length());
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter instanceof Filterable) {
            if (this.mPopup == null) {
                ((Filterable) listAdapter).getFilter().filter(str);
            }
            this.mFiltered = true;
            this.mDataSetObserver.clearSavedState();
        }
    }

    public CharSequence getTextFilter() {
        EditText editText;
        if (!this.mTextFilterEnabled || (editText = this.mTextFilter) == null) {
            return null;
        }
        return editText.getText();
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (!z || this.mSelectedPosition >= 0 || isInTouchMode()) {
            return;
        }
        if (!isAttachedToWindow() && this.mAdapter != null) {
            this.mDataChanged = true;
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
        }
        resurrectSelection();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mBlockLayoutRequests || this.mInLayout) {
            return;
        }
        super.requestLayout();
    }

    void resetList() {
        removeAllViewsInLayout();
        this.mFirstPosition = 0;
        this.mDataChanged = false;
        this.mPositionScrollAfterLayout = null;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
        this.mSelectedTop = 0;
        this.mSelectorPosition = -1;
        this.mSelectorRect.setEmpty();
        invalidate();
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return 0;
        }
        if (!this.mSmoothScrollbarEnabled) {
            return 1;
        }
        int i = childCount * 100;
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        int height = childAt.getHeight();
        if (height > 0) {
            i += (top * 100) / height;
        }
        View childAt2 = getChildAt(childCount - 1);
        int bottom = childAt2.getBottom();
        int height2 = childAt2.getHeight();
        return height2 > 0 ? i - (((bottom - getHeight()) * 100) / height2) : i;
    }

    @Override // android.view.View
    protected int computeVerticalScrollOffset() {
        int i = this.mFirstPosition;
        int childCount = getChildCount();
        int i2 = 0;
        if (i >= 0 && childCount > 0) {
            if (this.mSmoothScrollbarEnabled) {
                View childAt = getChildAt(0);
                int top = childAt.getTop();
                int height = childAt.getHeight();
                if (height > 0) {
                    return Math.max(((i * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f)), 0);
                }
            } else {
                int i3 = this.mItemCount;
                if (i != 0) {
                    i2 = i + childCount == i3 ? i3 : (childCount / 2) + i;
                }
                return (int) (i + (childCount * (i2 / i3)));
            }
        }
        return 0;
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        if (this.mSmoothScrollbarEnabled) {
            int iMax = Math.max(this.mItemCount * 100, 0);
            return this.mScrollY != 0 ? iMax + Math.abs((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f)) : iMax;
        }
        return this.mItemCount;
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        int childCount = getChildCount();
        float topFadingEdgeStrength = super.getTopFadingEdgeStrength();
        if (childCount == 0) {
            return topFadingEdgeStrength;
        }
        if (this.mFirstPosition > 0) {
            return 1.0f;
        }
        int top = getChildAt(0).getTop();
        return top < this.mPaddingTop ? (-(top - this.mPaddingTop)) / getVerticalFadingEdgeLength() : topFadingEdgeStrength;
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        int childCount = getChildCount();
        float bottomFadingEdgeStrength = super.getBottomFadingEdgeStrength();
        if (childCount == 0) {
            return bottomFadingEdgeStrength;
        }
        if ((this.mFirstPosition + childCount) - 1 < this.mItemCount - 1) {
            return 1.0f;
        }
        int bottom = getChildAt(childCount - 1).getBottom();
        int height = getHeight();
        return bottom > height - this.mPaddingBottom ? ((bottom - height) + this.mPaddingBottom) / getVerticalFadingEdgeLength() : bottomFadingEdgeStrength;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mSelector == null) {
            useDefaultSelector();
        }
        Rect rect = this.mListPadding;
        rect.left = this.mSelectionLeftPadding + this.mPaddingLeft;
        rect.top = this.mSelectionTopPadding + this.mPaddingTop;
        rect.right = this.mSelectionRightPadding + this.mPaddingRight;
        rect.bottom = this.mSelectionBottomPadding + this.mPaddingBottom;
        if (this.mTranscriptMode == 1) {
            int childCount = getChildCount();
            int height = getHeight() - getPaddingBottom();
            View childAt = getChildAt(childCount - 1);
            this.mForceTranscriptScroll = this.mFirstPosition + childCount >= this.mLastHandledItemCount && (childAt != null ? childAt.getBottom() : height) <= height;
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mInLayout = true;
        int childCount = getChildCount();
        if (z) {
            for (int i5 = 0; i5 < childCount; i5++) {
                getChildAt(i5).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
        }
        layoutChildren();
        this.mOverscrollMax = (i4 - i2) / 3;
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onItemCountChanged(getChildCount(), this.mItemCount);
        }
        this.mInLayout = false;
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        PopupWindow popupWindow;
        boolean frame = super.setFrame(i, i2, i3, i4);
        if (frame) {
            boolean z = getWindowVisibility() == 0;
            if (this.mFiltered && z && (popupWindow = this.mPopup) != null && popupWindow.isShowing()) {
                positionPopup();
            }
        }
        return frame;
    }

    View getAccessibilityFocusedChild(View view) {
        boolean z;
        ViewParent parent = view.getParent();
        while (true) {
            z = parent instanceof View;
            if (!z || parent == this) {
                break;
            }
            view = parent;
            parent = parent.getParent();
        }
        if (z) {
            return view;
        }
        return null;
    }

    void updateScrollIndicators() {
        View view = this.mScrollUp;
        if (view != null) {
            view.setVisibility(canScrollUp() ? 0 : 4);
        }
        View view2 = this.mScrollDown;
        if (view2 != null) {
            view2.setVisibility(canScrollDown() ? 0 : 4);
        }
    }

    private boolean canScrollUp() {
        boolean z = this.mFirstPosition > 0;
        if (z || getChildCount() <= 0) {
            return z;
        }
        return getChildAt(0).getTop() < this.mListPadding.top;
    }

    private boolean canScrollDown() {
        int childCount = getChildCount();
        boolean z = this.mFirstPosition + childCount < this.mItemCount;
        if (z || childCount <= 0) {
            return z;
        }
        return getChildAt(childCount - 1).getBottom() > this.mBottom - this.mListPadding.bottom;
    }

    @Override // android.widget.AdapterView
    @ViewDebug.ExportedProperty
    public View getSelectedView() {
        if (this.mItemCount <= 0 || this.mSelectedPosition < 0) {
            return null;
        }
        return getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
    }

    View obtainView(int i, boolean[] zArr) {
        View view;
        Trace.traceBegin(8L, "obtainView");
        zArr[0] = false;
        View transientStateView = this.mRecycler.getTransientStateView(i);
        if (transientStateView != null) {
            if (((LayoutParams) transientStateView.getLayoutParams()).viewType == this.mAdapter.getItemViewType(i) && (view = this.mAdapter.getView(i, transientStateView, this)) != transientStateView) {
                setItemViewLayoutParams(view, i);
                this.mRecycler.addScrapView(view, i);
            }
            zArr[0] = true;
            transientStateView.dispatchFinishTemporaryDetach();
            return transientStateView;
        }
        View scrapView = this.mRecycler.getScrapView(i);
        View view2 = this.mAdapter.getView(i, scrapView, this);
        if (scrapView != null) {
            if (view2 != scrapView) {
                this.mRecycler.addScrapView(scrapView, i);
            } else if (view2.isTemporarilyDetached()) {
                zArr[0] = true;
                view2.dispatchFinishTemporaryDetach();
            }
        }
        int i2 = this.mCacheColorHint;
        if (i2 != 0) {
            view2.setDrawingCacheBackgroundColor(i2);
        }
        if (view2.getImportantForAccessibility() == 0) {
            view2.setImportantForAccessibility(1);
        }
        setItemViewLayoutParams(view2, i);
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAccessibilityDelegate == null) {
                this.mAccessibilityDelegate = new ListItemAccessibilityDelegate();
            }
            if (view2.getAccessibilityDelegate() == null) {
                view2.setAccessibilityDelegate(this.mAccessibilityDelegate);
            }
        }
        Trace.traceEnd(8L);
        return view2;
    }

    private void setItemViewLayoutParams(View view, int i) {
        LayoutParams layoutParams;
        ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
        if (layoutParams2 == null) {
            layoutParams = (LayoutParams) generateDefaultLayoutParams();
        } else if (!checkLayoutParams(layoutParams2)) {
            layoutParams = (LayoutParams) generateLayoutParams(layoutParams2);
        } else {
            layoutParams = (LayoutParams) layoutParams2;
        }
        if (this.mAdapterHasStableIds) {
            layoutParams.itemId = this.mAdapter.getItemId(i);
        }
        layoutParams.viewType = this.mAdapter.getItemViewType(i);
        layoutParams.isEnabled = this.mAdapter.isEnabled(i);
        if (layoutParams != layoutParams2) {
            view.setLayoutParams(layoutParams);
        }
    }

    class ListItemAccessibilityDelegate extends View.AccessibilityDelegate {
        ListItemAccessibilityDelegate() {
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            AbsListView.this.onInitializeAccessibilityNodeInfoForItem(view, AbsListView.this.getPositionForView(view), accessibilityNodeInfo);
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            int positionForView = AbsListView.this.getPositionForView(view);
            if (positionForView == -1 || AbsListView.this.mAdapter == null || positionForView >= AbsListView.this.mAdapter.getCount()) {
                return false;
            }
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            boolean z = layoutParams instanceof LayoutParams ? ((LayoutParams) layoutParams).isEnabled : false;
            if (AbsListView.this.isEnabled() && z) {
                if (i != 4) {
                    if (i == 8) {
                        if (AbsListView.this.getSelectedItemPosition() != positionForView) {
                            return false;
                        }
                        AbsListView.this.setSelection(-1);
                        return true;
                    }
                    if (i == 16) {
                        if (AbsListView.this.isItemClickable(view)) {
                            return AbsListView.this.performItemClick(view, positionForView, AbsListView.this.getItemIdAtPosition(positionForView));
                        }
                        return false;
                    }
                    if (i == 32 && AbsListView.this.isLongClickable()) {
                        return AbsListView.this.performLongPress(view, positionForView, AbsListView.this.getItemIdAtPosition(positionForView));
                    }
                    return false;
                }
                if (AbsListView.this.getSelectedItemPosition() != positionForView) {
                    AbsListView.this.setSelection(positionForView);
                    return true;
                }
            }
            return false;
        }
    }

    public void onInitializeAccessibilityNodeInfoForItem(View view, int i, AccessibilityNodeInfo accessibilityNodeInfo) {
        if (i == -1) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        boolean z = layoutParams instanceof LayoutParams ? ((LayoutParams) layoutParams).isEnabled : false;
        if (!isEnabled() || !z) {
            accessibilityNodeInfo.setEnabled(false);
            return;
        }
        if (i == getSelectedItemPosition()) {
            accessibilityNodeInfo.setSelected(true);
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_SELECTION);
        } else {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SELECT);
        }
        if (isItemClickable(view)) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            accessibilityNodeInfo.setClickable(true);
        }
        if (isLongClickable()) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
            accessibilityNodeInfo.setLongClickable(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isItemClickable(View view) {
        return !view.hasExplicitFocusable();
    }

    void positionSelectorLikeTouch(int i, View view, float f, float f2) {
        positionSelector(i, view, true, f, f2);
    }

    void positionSelectorLikeFocus(int i, View view) {
        if (this.mSelector != null && this.mSelectorPosition != i && i != -1) {
            Rect rect = this.mSelectorRect;
            positionSelector(i, view, true, rect.exactCenterX(), rect.exactCenterY());
        } else {
            positionSelector(i, view);
        }
    }

    void positionSelector(int i, View view) {
        positionSelector(i, view, false, -1.0f, -1.0f);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void positionSelector(int i, View view, boolean z, float f, float f2) {
        boolean z2 = i != this.mSelectorPosition;
        if (i != -1) {
            this.mSelectorPosition = i;
        }
        Rect rect = this.mSelectorRect;
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        if (view instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster) view).adjustListItemSelectionBounds(rect);
        }
        rect.left -= this.mSelectionLeftPadding;
        rect.top -= this.mSelectionTopPadding;
        rect.right += this.mSelectionRightPadding;
        rect.bottom += this.mSelectionBottomPadding;
        boolean zIsEnabled = view.isEnabled();
        if (this.mIsChildViewEnabled != zIsEnabled) {
            this.mIsChildViewEnabled = zIsEnabled;
        }
        Drawable drawable = this.mSelector;
        if (drawable != null) {
            if (z2) {
                drawable.setVisible(false, false);
                drawable.setState(StateSet.NOTHING);
            }
            drawable.setBounds(rect);
            if (z2) {
                if (getVisibility() == 0) {
                    drawable.setVisible(true, false);
                }
                updateSelectorState();
            }
            if (z) {
                drawable.setHotspot(f, f2);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int iSave = 0;
        boolean z = (this.mGroupFlags & 34) == 34;
        if (z) {
            iSave = canvas.save();
            int i = this.mScrollX;
            int i2 = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + i, this.mPaddingTop + i2, ((i + this.mRight) - this.mLeft) - this.mPaddingRight, ((i2 + this.mBottom) - this.mTop) - this.mPaddingBottom);
            this.mGroupFlags &= -35;
        }
        boolean z2 = this.mDrawSelectorOnTop;
        if (!z2) {
            drawSelector(canvas);
        }
        super.dispatchDraw(canvas);
        if (z2) {
            drawSelector(canvas);
        }
        if (z) {
            canvas.restoreToCount(iSave);
            this.mGroupFlags |= 34;
        }
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mGroupFlags & 34) != 34;
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingLeft;
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingTop;
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingRight;
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingBottom;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void internalSetPadding(int i, int i2, int i3, int i4) {
        super.internalSetPadding(i, i2, i3, i4);
        if (isLayoutRequested()) {
            handleBoundsChange();
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        handleBoundsChange();
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.onSizeChanged(i, i2, i3, i4);
        }
    }

    void handleBoundsChange() {
        int childCount;
        if (!this.mInLayout && (childCount = getChildCount()) > 0) {
            this.mDataChanged = true;
            rememberSyncState();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams == null || layoutParams.width < 1 || layoutParams.height < 1) {
                    childAt.forceLayout();
                }
            }
        }
    }

    boolean touchModeDrawsInPressedState() {
        int i = this.mTouchMode;
        return i == 1 || i == 2;
    }

    boolean shouldShowSelector() {
        return (isFocused() && !isInTouchMode()) || (touchModeDrawsInPressedState() && isPressed());
    }

    private void drawSelector(Canvas canvas) {
        if (shouldDrawSelector()) {
            Drawable drawable = this.mSelector;
            drawable.setBounds(this.mSelectorRect);
            drawable.draw(canvas);
        }
    }

    public final boolean shouldDrawSelector() {
        return !this.mSelectorRect.isEmpty();
    }

    public void setDrawSelectorOnTop(boolean z) {
        this.mDrawSelectorOnTop = z;
    }

    public void setSelector(int i) {
        setSelector(getContext().getDrawable(i));
    }

    public void setSelector(Drawable drawable) {
        Drawable drawable2 = this.mSelector;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.mSelector);
        }
        this.mSelector = drawable;
        Rect rect = new Rect();
        drawable.getPadding(rect);
        this.mSelectionLeftPadding = rect.left;
        this.mSelectionTopPadding = rect.top;
        this.mSelectionRightPadding = rect.right;
        this.mSelectionBottomPadding = rect.bottom;
        drawable.setCallback(this);
        updateSelectorState();
    }

    public Drawable getSelector() {
        return this.mSelector;
    }

    void keyPressed() {
        if (isEnabled() && isClickable()) {
            Drawable drawable = this.mSelector;
            Rect rect = this.mSelectorRect;
            if (drawable != null) {
                if ((isFocused() || touchModeDrawsInPressedState()) && !rect.isEmpty()) {
                    View childAt = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                    if (childAt != null) {
                        if (childAt.hasExplicitFocusable()) {
                            return;
                        } else {
                            childAt.setPressed(true);
                        }
                    }
                    setPressed(true);
                    boolean zIsLongClickable = isLongClickable();
                    Drawable current = drawable.getCurrent();
                    if (current != null && (current instanceof TransitionDrawable)) {
                        if (zIsLongClickable) {
                            ((TransitionDrawable) current).startTransition(ViewConfiguration.getLongPressTimeout());
                        } else {
                            ((TransitionDrawable) current).resetTransition();
                        }
                    }
                    if (!zIsLongClickable || this.mDataChanged) {
                        return;
                    }
                    if (this.mPendingCheckForKeyLongPress == null) {
                        this.mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
                    }
                    this.mPendingCheckForKeyLongPress.rememberWindowAttachCount();
                    postDelayed(this.mPendingCheckForKeyLongPress, ViewConfiguration.getLongPressTimeout());
                }
            }
        }
    }

    public void setScrollIndicators(View view, View view2) {
        this.mScrollUp = view;
        this.mScrollDown = view2;
    }

    void updateSelectorState() {
        Drawable drawable = this.mSelector;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        if (shouldShowSelector()) {
            if (drawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(drawable);
                return;
            }
            return;
        }
        drawable.setState(StateSet.NOTHING);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    private int[] getDrawableStateForSelector() {
        if (this.mIsChildViewEnabled) {
            return super.getDrawableState();
        }
        int i = ENABLED_STATE_SET[0];
        int[] iArrOnCreateDrawableState = onCreateDrawableState(1);
        int length = iArrOnCreateDrawableState.length - 1;
        while (true) {
            if (length < 0) {
                length = -1;
                break;
            }
            if (iArrOnCreateDrawableState[length] == i) {
                break;
            }
            length--;
        }
        if (length >= 0) {
            System.arraycopy(iArrOnCreateDrawableState, length + 1, iArrOnCreateDrawableState, length, (iArrOnCreateDrawableState.length - length) - 1);
        }
        return iArrOnCreateDrawableState;
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return this.mSelector == drawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mSelector;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null && !this.mGlobalLayoutListenerAddedFilter) {
            viewTreeObserver.addOnGlobalLayoutListener(this);
        }
        if (this.mAdapter == null || this.mDataSetObserver != null) {
            return;
        }
        AdapterDataSetObserver adapterDataSetObserver = new AdapterDataSetObserver();
        this.mDataSetObserver = adapterDataSetObserver;
        this.mAdapter.registerDataSetObserver(adapterDataSetObserver);
        this.mDataChanged = true;
        this.mOldItemCount = this.mItemCount;
        this.mItemCount = this.mAdapter.getCount();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        AdapterDataSetObserver adapterDataSetObserver;
        super.onDetachedFromWindow();
        this.mIsDetaching = true;
        dismissPopup();
        this.mRecycler.clear();
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.removeOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null) {
            viewTreeObserver.removeOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = false;
        }
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null && (adapterDataSetObserver = this.mDataSetObserver) != null) {
            listAdapter.unregisterDataSetObserver(adapterDataSetObserver);
            this.mDataSetObserver = null;
        }
        StrictMode.Span span = this.mScrollStrictSpan;
        if (span != null) {
            span.finish();
            this.mScrollStrictSpan = null;
        }
        StrictMode.Span span2 = this.mFlingStrictSpan;
        if (span2 != null) {
            span2.finish();
            this.mFlingStrictSpan = null;
        }
        FlingRunnable flingRunnable = this.mFlingRunnable;
        if (flingRunnable != null) {
            removeCallbacks(flingRunnable);
        }
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        Runnable runnable = this.mClearScrollingCache;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
        Runnable runnable2 = this.mTouchModeReset;
        if (runnable2 != null) {
            removeCallbacks(runnable2);
            this.mTouchModeReset.run();
        }
        this.mIsDetaching = false;
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        int i = !isInTouchMode() ? 1 : 0;
        if (!z) {
            setChildrenDrawingCacheEnabled(false);
            FlingRunnable flingRunnable = this.mFlingRunnable;
            if (flingRunnable != null) {
                removeCallbacks(flingRunnable);
                this.mFlingRunnable.mSuppressIdleStateChangeCall = false;
                this.mFlingRunnable.endFling();
                AbsPositionScroller absPositionScroller = this.mPositionScroller;
                if (absPositionScroller != null) {
                    absPositionScroller.stop();
                }
                if (this.mScrollY != 0) {
                    this.mScrollY = 0;
                    invalidateParentCaches();
                    finishGlows();
                    invalidate();
                }
            }
            dismissPopup();
            if (i == 1) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
        } else {
            if (this.mFiltered && !this.mPopupHidden) {
                showPopup();
            }
            int i2 = this.mLastTouchMode;
            if (i != i2 && i2 != -1) {
                if (i == 1) {
                    resurrectSelection();
                } else {
                    hideSelector();
                    this.mLayoutMode = 0;
                    layoutChildren();
                }
            }
        }
        this.mLastTouchMode = i;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null) {
            fastScroller.setScrollbarPosition(getVerticalScrollbarPosition());
        }
    }

    ContextMenu.ContextMenuInfo createContextMenuInfo(View view, int i, long j) {
        return new AdapterView.AdapterContextMenuInfo(view, i, j);
    }

    @Override // android.view.View
    public void onCancelPendingInputEvents() {
        super.onCancelPendingInputEvents();
        PerformClick performClick = this.mPerformClick;
        if (performClick != null) {
            removeCallbacks(performClick);
        }
        CheckForTap checkForTap = this.mPendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
        CheckForLongPress checkForLongPress = this.mPendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForKeyLongPress checkForKeyLongPress = this.mPendingCheckForKeyLongPress;
        if (checkForKeyLongPress != null) {
            removeCallbacks(checkForKeyLongPress);
        }
    }

    private class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = AbsListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return AbsListView.this.getWindowAttachCount() == this.mOriginalAttachCount;
        }
    }

    private class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        private PerformClick() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.mDataChanged) {
                return;
            }
            ListAdapter listAdapter = AbsListView.this.mAdapter;
            int i = this.mClickMotionPosition;
            if (listAdapter == null || AbsListView.this.mItemCount <= 0 || i == -1 || i >= listAdapter.getCount() || !sameWindow() || !listAdapter.isEnabled(i)) {
                return;
            }
            AbsListView absListView = AbsListView.this;
            View childAt = absListView.getChildAt(i - absListView.mFirstPosition);
            if (childAt != null) {
                AbsListView.this.performItemClick(childAt, i, listAdapter.getItemId(i));
            }
        }
    }

    private class CheckForLongPress extends WindowRunnnable implements Runnable {
        private static final int INVALID_COORD = -1;
        private float mX;
        private float mY;

        private CheckForLongPress() {
            super();
            this.mX = -1.0f;
            this.mY = -1.0f;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCoords(float f, float f2) {
            this.mX = f;
            this.mY = f2;
        }

        /* JADX WARN: Code duplicated, block: B:13:0x0042  */
        @Override // java.lang.Runnable
        public void run() {
            boolean zPerformLongPress;
            int i = AbsListView.this.mMotionPosition;
            AbsListView absListView = AbsListView.this;
            View childAt = absListView.getChildAt(i - absListView.mFirstPosition);
            if (childAt != null) {
                int i2 = AbsListView.this.mMotionPosition;
                long itemId = AbsListView.this.mAdapter.getItemId(AbsListView.this.mMotionPosition);
                if (!sameWindow() || AbsListView.this.mDataChanged) {
                    zPerformLongPress = false;
                } else {
                    float f = this.mX;
                    if (f != -1.0f) {
                        float f2 = this.mY;
                        if (f2 != -1.0f) {
                            zPerformLongPress = AbsListView.this.performLongPress(childAt, i2, itemId, f, f2);
                        } else {
                            zPerformLongPress = AbsListView.this.performLongPress(childAt, i2, itemId);
                        }
                    } else {
                        zPerformLongPress = AbsListView.this.performLongPress(childAt, i2, itemId);
                    }
                }
                if (zPerformLongPress) {
                    AbsListView.this.mHasPerformedLongPress = true;
                    AbsListView.this.mTouchMode = -1;
                    AbsListView.this.setPressed(false);
                    childAt.setPressed(false);
                    return;
                }
                AbsListView.this.mTouchMode = 2;
            }
        }
    }

    private class CheckForKeyLongPress extends WindowRunnnable implements Runnable {
        private CheckForKeyLongPress() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean zPerformLongPress;
            if (!AbsListView.this.isPressed() || AbsListView.this.mSelectedPosition < 0) {
                return;
            }
            View childAt = AbsListView.this.getChildAt(AbsListView.this.mSelectedPosition - AbsListView.this.mFirstPosition);
            if (!AbsListView.this.mDataChanged) {
                if (sameWindow()) {
                    AbsListView absListView = AbsListView.this;
                    zPerformLongPress = absListView.performLongPress(childAt, absListView.mSelectedPosition, AbsListView.this.mSelectedRowId);
                } else {
                    zPerformLongPress = false;
                }
                if (zPerformLongPress) {
                    AbsListView.this.setPressed(false);
                    childAt.setPressed(false);
                    return;
                }
                return;
            }
            AbsListView.this.setPressed(false);
            if (childAt != null) {
                childAt.setPressed(false);
            }
        }
    }

    private boolean performStylusButtonPressAction(MotionEvent motionEvent) {
        View childAt;
        if (this.mChoiceMode == 3 && this.mChoiceActionMode == null && (childAt = getChildAt(this.mMotionPosition - this.mFirstPosition)) != null) {
            int i = this.mMotionPosition;
            if (performLongPress(childAt, i, this.mAdapter.getItemId(i))) {
                this.mTouchMode = -1;
                setPressed(false);
                childAt.setPressed(false);
                return true;
            }
        }
        return false;
    }

    boolean performLongPress(View view, int i, long j) {
        return performLongPress(view, i, j, -1.0f, -1.0f);
    }

    boolean performLongPress(View view, int i, long j, float f, float f2) {
        if (this.mChoiceMode == 3) {
            if (this.mChoiceActionMode == null) {
                ActionMode actionModeStartActionMode = startActionMode(this.mMultiChoiceModeCallback);
                this.mChoiceActionMode = actionModeStartActionMode;
                if (actionModeStartActionMode != null) {
                    setItemChecked(i, true);
                    performHapticFeedback(0);
                }
            }
            return true;
        }
        boolean zOnItemLongClick = this.mOnItemLongClickListener != null ? this.mOnItemLongClickListener.onItemLongClick(this, view, i, j) : false;
        if (!zOnItemLongClick) {
            this.mContextMenuInfo = createContextMenuInfo(view, i, j);
            if (f != -1.0f && f2 != -1.0f) {
                zOnItemLongClick = super.showContextMenuForChild(this, f, f2);
            } else {
                zOnItemLongClick = super.showContextMenuForChild(this);
            }
        }
        if (zOnItemLongClick) {
            performHapticFeedback(0);
        }
        return zOnItemLongClick;
    }

    @Override // android.view.View
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    @Override // android.view.View
    public boolean showContextMenu() {
        return showContextMenuInternal(0.0f, 0.0f, false);
    }

    @Override // android.view.View
    public boolean showContextMenu(float f, float f2) {
        return showContextMenuInternal(f, f2, true);
    }

    private boolean showContextMenuInternal(float f, float f2, boolean z) {
        int iPointToPosition = pointToPosition((int) f, (int) f2);
        if (iPointToPosition != -1) {
            long itemId = this.mAdapter.getItemId(iPointToPosition);
            View childAt = getChildAt(iPointToPosition - this.mFirstPosition);
            if (childAt != null) {
                this.mContextMenuInfo = createContextMenuInfo(childAt, iPointToPosition, itemId);
                if (z) {
                    return super.showContextMenuForChild(this, f, f2);
                }
                return super.showContextMenuForChild(this);
            }
        }
        if (z) {
            return super.showContextMenu(f, f2);
        }
        return super.showContextMenu();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        if (isShowingContextMenuWithCoords()) {
            return false;
        }
        return showContextMenuForChildInternal(view, 0.0f, 0.0f, false);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View view, float f, float f2) {
        return showContextMenuForChildInternal(view, f, f2, true);
    }

    private boolean showContextMenuForChildInternal(View view, float f, float f2, boolean z) {
        int positionForView = getPositionForView(view);
        if (positionForView < 0) {
            return false;
        }
        long itemId = this.mAdapter.getItemId(positionForView);
        boolean zOnItemLongClick = this.mOnItemLongClickListener != null ? this.mOnItemLongClickListener.onItemLongClick(this, view, positionForView, itemId) : false;
        if (zOnItemLongClick) {
            return zOnItemLongClick;
        }
        this.mContextMenuInfo = createContextMenuInfo(getChildAt(positionForView - this.mFirstPosition), positionForView, itemId);
        if (z) {
            return super.showContextMenuForChild(view, f, f2);
        }
        return super.showContextMenuForChild(view);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (KeyEvent.isConfirmKey(i)) {
            if (!isEnabled()) {
                return true;
            }
            if (isClickable() && isPressed() && this.mSelectedPosition >= 0 && this.mAdapter != null && this.mSelectedPosition < this.mAdapter.getCount()) {
                View childAt = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (childAt != null) {
                    performItemClick(childAt, this.mSelectedPosition, this.mSelectedRowId);
                    childAt.setPressed(false);
                }
                setPressed(false);
                return true;
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    public int pointToPosition(int i, int i2) {
        Rect rect = this.mTouchFrame;
        if (rect == null) {
            rect = new Rect();
            this.mTouchFrame = rect;
        }
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(rect);
                if (rect.contains(i, i2)) {
                    return this.mFirstPosition + childCount;
                }
            }
        }
        return -1;
    }

    public long pointToRowId(int i, int i2) {
        int iPointToPosition = pointToPosition(i, i2);
        if (iPointToPosition >= 0) {
            return this.mAdapter.getItemId(iPointToPosition);
        }
        return Long.MIN_VALUE;
    }

    private final class CheckForTap implements Runnable {
        float x;
        float y;

        private CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.mTouchMode == 0) {
                AbsListView.this.mTouchMode = 1;
                AbsListView absListView = AbsListView.this;
                View childAt = absListView.getChildAt(absListView.mMotionPosition - AbsListView.this.mFirstPosition);
                if (childAt == null || childAt.hasExplicitFocusable()) {
                    return;
                }
                AbsListView.this.mLayoutMode = 0;
                if (!AbsListView.this.mDataChanged) {
                    float[] fArr = AbsListView.this.mTmpPoint;
                    fArr[0] = this.x;
                    fArr[1] = this.y;
                    AbsListView.this.transformPointToViewLocal(fArr, childAt);
                    childAt.drawableHotspotChanged(fArr[0], fArr[1]);
                    childAt.setPressed(true);
                    AbsListView.this.setPressed(true);
                    AbsListView.this.layoutChildren();
                    AbsListView absListView2 = AbsListView.this;
                    absListView2.positionSelector(absListView2.mMotionPosition, childAt);
                    AbsListView.this.refreshDrawableState();
                    int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                    boolean zIsLongClickable = AbsListView.this.isLongClickable();
                    if (AbsListView.this.mSelector != null) {
                        Drawable current = AbsListView.this.mSelector.getCurrent();
                        if (current != null && (current instanceof TransitionDrawable)) {
                            if (zIsLongClickable) {
                                ((TransitionDrawable) current).startTransition(longPressTimeout);
                            } else {
                                ((TransitionDrawable) current).resetTransition();
                            }
                        }
                        AbsListView.this.mSelector.setHotspot(this.x, this.y);
                    }
                    if (zIsLongClickable) {
                        if (AbsListView.this.mPendingCheckForLongPress == null) {
                            AbsListView.this.mPendingCheckForLongPress = new CheckForLongPress();
                        }
                        AbsListView.this.mPendingCheckForLongPress.setCoords(this.x, this.y);
                        AbsListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
                        AbsListView absListView3 = AbsListView.this;
                        absListView3.postDelayed(absListView3.mPendingCheckForLongPress, longPressTimeout);
                        return;
                    }
                    AbsListView.this.mTouchMode = 2;
                    return;
                }
                AbsListView.this.mTouchMode = 2;
            }
        }
    }

    private boolean startScrollIfNeeded(int i, int i2, MotionEvent motionEvent) {
        int i3 = i2 - this.mMotionY;
        int iAbs = Math.abs(i3);
        boolean z = this.mScrollY != 0;
        if ((!z && iAbs <= this.mTouchSlop) || (getNestedScrollAxes() & 2) != 0) {
            return false;
        }
        createScrollingCache();
        if (z) {
            this.mTouchMode = 5;
            this.mMotionCorrection = 0;
        } else {
            this.mTouchMode = 3;
            this.mMotionCorrection = i3 > 0 ? this.mTouchSlop : -this.mTouchSlop;
        }
        removeCallbacks(this.mPendingCheckForLongPress);
        setPressed(false);
        View childAt = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (childAt != null) {
            childAt.setPressed(false);
        }
        reportScrollStateChange(1);
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        scrollIfNeeded(i, i2, motionEvent);
        return true;
    }

    private void scrollIfNeeded(int i, int i2, MotionEvent motionEvent) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int childCount;
        VelocityTracker velocityTracker;
        ViewParent parent;
        int i10 = i2 - this.mMotionY;
        if (this.mLastY == Integer.MIN_VALUE) {
            i10 -= this.mMotionCorrection;
        }
        int i11 = this.mLastY;
        int i12 = 0;
        if (dispatchNestedPreScroll(0, i11 != Integer.MIN_VALUE ? i11 - i2 : -i10, this.mScrollConsumed, this.mScrollOffset)) {
            int[] iArr = this.mScrollConsumed;
            int i13 = i10 + iArr[1];
            int[] iArr2 = this.mScrollOffset;
            int i14 = -iArr2[1];
            i4 = iArr[1];
            if (motionEvent != null) {
                motionEvent.offsetLocation(0.0f, iArr2[1]);
                this.mNestedYOffset += this.mScrollOffset[1];
            }
            i3 = i13;
            i5 = i14;
        } else {
            i3 = i10;
            i4 = 0;
            i5 = 0;
        }
        int i15 = this.mLastY;
        int i16 = i15 != Integer.MIN_VALUE ? (i2 - i15) + i4 : i3;
        int i17 = this.mTouchMode;
        if (i17 == 3) {
            if (this.mScrollStrictSpan == null) {
                this.mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
            }
            if (i2 != this.mLastY) {
                if ((this.mGroupFlags & 524288) == 0 && Math.abs(i3) > this.mTouchSlop && (parent = getParent()) != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                int i18 = this.mMotionPosition;
                if (i18 >= 0) {
                    childCount = i18 - this.mFirstPosition;
                } else {
                    childCount = getChildCount() / 2;
                }
                View childAt = getChildAt(childCount);
                int top = childAt != null ? childAt.getTop() : 0;
                boolean zTrackMotionScroll = i16 != 0 ? trackMotionScroll(i3, i16) : false;
                View childAt2 = getChildAt(childCount);
                if (childAt2 != null) {
                    int top2 = childAt2.getTop();
                    if (zTrackMotionScroll) {
                        int i19 = (-i16) - (top2 - top);
                        if (dispatchNestedScroll(0, i19 - i16, 0, i19, this.mScrollOffset)) {
                            int[] iArr3 = this.mScrollOffset;
                            i12 = 0 - iArr3[1];
                            if (motionEvent != null) {
                                motionEvent.offsetLocation(0.0f, iArr3[1]);
                                this.mNestedYOffset += this.mScrollOffset[1];
                            }
                        } else {
                            int i20 = i16;
                            boolean zOverScrollBy = overScrollBy(0, i19, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
                            if (zOverScrollBy && (velocityTracker = this.mVelocityTracker) != null) {
                                velocityTracker.clear();
                            }
                            int overScrollMode = getOverScrollMode();
                            if (overScrollMode == 0 || (overScrollMode == 1 && !contentFits())) {
                                if (!zOverScrollBy) {
                                    this.mDirection = 0;
                                    this.mTouchMode = 5;
                                }
                                if (i20 > 0) {
                                    this.mEdgeGlowTop.onPull((-i19) / getHeight(), i / getWidth());
                                    if (!this.mEdgeGlowBottom.isFinished()) {
                                        this.mEdgeGlowBottom.onRelease();
                                    }
                                    invalidateTopGlow();
                                } else if (i20 < 0) {
                                    this.mEdgeGlowBottom.onPull(i19 / getHeight(), 1.0f - (i / getWidth()));
                                    if (!this.mEdgeGlowTop.isFinished()) {
                                        this.mEdgeGlowTop.onRelease();
                                    }
                                    invalidateBottomGlow();
                                }
                            }
                        }
                    }
                    this.mMotionY = i2 + i12 + i5;
                }
                this.mLastY = i2 + i12 + i5;
                return;
            }
            return;
        }
        int i21 = i16;
        if (i17 != 5 || i2 == this.mLastY) {
            return;
        }
        int i22 = this.mScrollY;
        int i23 = i22 - i21;
        int i24 = i2 > this.mLastY ? 1 : -1;
        if (this.mDirection == 0) {
            this.mDirection = i24;
        }
        int i25 = -i21;
        if ((i23 >= 0 || i22 < 0) && (i23 <= 0 || i22 > 0)) {
            i6 = i25;
            i7 = 0;
        } else {
            int i26 = -i22;
            i7 = i21 + i26;
            i6 = i26;
        }
        if (i6 != 0) {
            int i27 = i6;
            int i28 = i7;
            overScrollBy(0, i6, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
            int overScrollMode2 = getOverScrollMode();
            if (overScrollMode2 == 0 || (overScrollMode2 == 1 && !contentFits())) {
                if (i3 > 0) {
                    this.mEdgeGlowTop.onPull(i27 / getHeight(), i / getWidth());
                    if (!this.mEdgeGlowBottom.isFinished()) {
                        this.mEdgeGlowBottom.onRelease();
                    }
                    invalidateTopGlow();
                } else if (i3 < 0) {
                    this.mEdgeGlowBottom.onPull(i27 / getHeight(), 1.0f - (i / getWidth()));
                    if (!this.mEdgeGlowTop.isFinished()) {
                        this.mEdgeGlowTop.onRelease();
                    }
                    invalidateBottomGlow();
                }
            }
            i8 = i28;
        } else {
            i8 = i7;
        }
        if (i8 != 0) {
            if (this.mScrollY != 0) {
                i9 = 0;
                this.mScrollY = 0;
                invalidateParentIfNeeded();
            } else {
                i9 = 0;
            }
            trackMotionScroll(i8, i8);
            this.mTouchMode = 3;
            int iFindClosestMotionRow = findClosestMotionRow(i2);
            this.mMotionCorrection = i9;
            View childAt3 = getChildAt(iFindClosestMotionRow - this.mFirstPosition);
            this.mMotionViewOriginalTop = childAt3 != null ? childAt3.getTop() : 0;
            this.mMotionY = i2 + i5;
            this.mMotionPosition = iFindClosestMotionRow;
        }
        this.mLastY = 0 + i2 + i5;
        this.mDirection = i24;
    }

    private void invalidateTopGlow() {
        if (this.mEdgeGlowTop == null) {
            return;
        }
        boolean clipToPadding = getClipToPadding();
        int i = clipToPadding ? this.mPaddingTop : 0;
        invalidate(clipToPadding ? this.mPaddingLeft : 0, i, clipToPadding ? getWidth() - this.mPaddingRight : getWidth(), this.mEdgeGlowTop.getMaxHeight() + i);
    }

    private void invalidateBottomGlow() {
        if (this.mEdgeGlowBottom == null) {
            return;
        }
        boolean clipToPadding = getClipToPadding();
        int height = getHeight();
        if (clipToPadding) {
            height -= this.mPaddingBottom;
        }
        invalidate(clipToPadding ? this.mPaddingLeft : 0, height - this.mEdgeGlowBottom.getMaxHeight(), clipToPadding ? getWidth() - this.mPaddingRight : getWidth(), height);
    }

    @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean z) {
        if (z) {
            hideSelector();
            if (getHeight() > 0 && getChildCount() > 0) {
                layoutChildren();
            }
            updateSelectorState();
            return;
        }
        int i = this.mTouchMode;
        if (i == 5 || i == 6) {
            FlingRunnable flingRunnable = this.mFlingRunnable;
            if (flingRunnable != null) {
                flingRunnable.endFling();
            }
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            if (this.mScrollY != 0) {
                this.mScrollY = 0;
                invalidateParentCaches();
                finishGlows();
                invalidate();
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        if (this.mIsDetaching || !isAttachedToWindow()) {
            return false;
        }
        startNestedScroll(2);
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && fastScroller.onTouchEvent(motionEvent)) {
            return true;
        }
        initVelocityTrackerIfNotExists();
        MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mNestedYOffset = 0;
        }
        motionEventObtain.offsetLocation(0.0f, this.mNestedYOffset);
        if (actionMasked == 0) {
            onTouchDown(motionEvent);
        } else if (actionMasked == 1) {
            onTouchUp(motionEvent);
        } else if (actionMasked == 2) {
            onTouchMove(motionEvent, motionEventObtain);
        } else if (actionMasked == 3) {
            onTouchCancel();
        } else if (actionMasked == 5) {
            int actionIndex = motionEvent.getActionIndex();
            int pointerId = motionEvent.getPointerId(actionIndex);
            int x = (int) motionEvent.getX(actionIndex);
            int y = (int) motionEvent.getY(actionIndex);
            this.mMotionCorrection = 0;
            this.mActivePointerId = pointerId;
            this.mMotionX = x;
            this.mMotionY = y;
            int iPointToPosition = pointToPosition(x, y);
            if (iPointToPosition >= 0) {
                this.mMotionViewOriginalTop = getChildAt(iPointToPosition - this.mFirstPosition).getTop();
                this.mMotionPosition = iPointToPosition;
            }
            this.mLastY = y;
        } else if (actionMasked == 6) {
            onSecondaryPointerUp(motionEvent);
            int i = this.mMotionX;
            int i2 = this.mMotionY;
            int iPointToPosition2 = pointToPosition(i, i2);
            if (iPointToPosition2 >= 0) {
                this.mMotionViewOriginalTop = getChildAt(iPointToPosition2 - this.mFirstPosition).getTop();
                this.mMotionPosition = iPointToPosition2;
            }
            this.mLastY = i2;
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEventObtain);
        }
        motionEventObtain.recycle();
        return true;
    }

    private void onTouchDown(MotionEvent motionEvent) {
        this.mHasPerformedLongPress = false;
        this.mActivePointerId = motionEvent.getPointerId(0);
        hideSelector();
        if (this.mTouchMode == 6) {
            this.mFlingRunnable.endFling();
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            this.mTouchMode = 5;
            this.mMotionX = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            this.mMotionY = y;
            this.mLastY = y;
            this.mMotionCorrection = 0;
            this.mDirection = 0;
        } else {
            int x = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            int iPointToPosition = pointToPosition(x, y2);
            if (!this.mDataChanged) {
                if (this.mTouchMode == 4) {
                    createScrollingCache();
                    this.mTouchMode = 3;
                    this.mMotionCorrection = 0;
                    iPointToPosition = findMotionRow(y2);
                    this.mFlingRunnable.flywheelTouch();
                } else if (iPointToPosition >= 0 && getAdapter().isEnabled(iPointToPosition)) {
                    this.mTouchMode = 0;
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    this.mPendingCheckForTap.x = motionEvent.getX();
                    this.mPendingCheckForTap.y = motionEvent.getY();
                    postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                }
            }
            if (iPointToPosition >= 0) {
                this.mMotionViewOriginalTop = getChildAt(iPointToPosition - this.mFirstPosition).getTop();
            }
            this.mMotionX = x;
            this.mMotionY = y2;
            this.mMotionPosition = iPointToPosition;
            this.mLastY = Integer.MIN_VALUE;
        }
        if (this.mTouchMode == 0 && this.mMotionPosition != -1 && performButtonActionOnTouchDown(motionEvent)) {
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    private void onTouchMove(MotionEvent motionEvent, MotionEvent motionEvent2) {
        if (this.mHasPerformedLongPress) {
            return;
        }
        int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        if (iFindPointerIndex == -1) {
            this.mActivePointerId = motionEvent.getPointerId(0);
            iFindPointerIndex = 0;
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        int y = (int) motionEvent.getY(iFindPointerIndex);
        int i = this.mTouchMode;
        if (i != 0 && i != 1 && i != 2) {
            if (i == 3 || i == 5) {
                scrollIfNeeded((int) motionEvent.getX(iFindPointerIndex), y, motionEvent2);
                return;
            }
            return;
        }
        if (startScrollIfNeeded((int) motionEvent.getX(iFindPointerIndex), y, motionEvent2)) {
            return;
        }
        View childAt = getChildAt(this.mMotionPosition - this.mFirstPosition);
        float x = motionEvent.getX(iFindPointerIndex);
        float f = y;
        if (!pointInView(x, f, this.mTouchSlop)) {
            setPressed(false);
            if (childAt != null) {
                childAt.setPressed(false);
            }
            removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
            this.mTouchMode = 2;
            updateSelectorState();
            return;
        }
        if (childAt != null) {
            float[] fArr = this.mTmpPoint;
            fArr[0] = x;
            fArr[1] = f;
            transformPointToViewLocal(fArr, childAt);
            childAt.drawableHotspotChanged(fArr[0], fArr[1]);
        }
    }

    private void onTouchUp(MotionEvent motionEvent) {
        int i = this.mTouchMode;
        if (i == 0 || i == 1 || i == 2) {
            int i2 = this.mMotionPosition;
            final View childAt = getChildAt(i2 - this.mFirstPosition);
            if (childAt != null) {
                if (this.mTouchMode != 0) {
                    childAt.setPressed(false);
                }
                float x = motionEvent.getX();
                if ((x > ((float) this.mListPadding.left) && x < ((float) (getWidth() - this.mListPadding.right))) && !childAt.hasExplicitFocusable()) {
                    if (this.mPerformClick == null) {
                        this.mPerformClick = new PerformClick();
                    }
                    final PerformClick performClick = this.mPerformClick;
                    performClick.mClickMotionPosition = i2;
                    performClick.rememberWindowAttachCount();
                    this.mResurrectToPosition = i2;
                    int i3 = this.mTouchMode;
                    if (i3 == 0 || i3 == 1) {
                        removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
                        this.mLayoutMode = 0;
                        if (!this.mDataChanged && this.mAdapter.isEnabled(i2)) {
                            this.mTouchMode = 1;
                            setSelectedPositionInt(this.mMotionPosition);
                            layoutChildren();
                            childAt.setPressed(true);
                            positionSelector(this.mMotionPosition, childAt);
                            setPressed(true);
                            Drawable drawable = this.mSelector;
                            if (drawable != null) {
                                Drawable current = drawable.getCurrent();
                                if (current != null && (current instanceof TransitionDrawable)) {
                                    ((TransitionDrawable) current).resetTransition();
                                }
                                this.mSelector.setHotspot(x, motionEvent.getY());
                            }
                            Runnable runnable = this.mTouchModeReset;
                            if (runnable != null) {
                                removeCallbacks(runnable);
                            }
                            Runnable runnable2 = new Runnable() { // from class: android.widget.AbsListView.3
                                @Override // java.lang.Runnable
                                public void run() {
                                    AbsListView.this.mTouchModeReset = null;
                                    AbsListView.this.mTouchMode = -1;
                                    childAt.setPressed(false);
                                    AbsListView.this.setPressed(false);
                                    if (AbsListView.this.mDataChanged || AbsListView.this.mIsDetaching || !AbsListView.this.isAttachedToWindow()) {
                                        return;
                                    }
                                    performClick.run();
                                }
                            };
                            this.mTouchModeReset = runnable2;
                            postDelayed(runnable2, ViewConfiguration.getPressedStateDuration());
                            return;
                        }
                        this.mTouchMode = -1;
                        updateSelectorState();
                        return;
                    }
                    if (!this.mDataChanged && this.mAdapter.isEnabled(i2)) {
                        performClick.run();
                    }
                }
            }
            this.mTouchMode = -1;
            updateSelectorState();
        } else if (i == 3) {
            int childCount = getChildCount();
            if (childCount > 0) {
                int top = getChildAt(0).getTop();
                int bottom = getChildAt(childCount - 1).getBottom();
                int i4 = this.mListPadding.top;
                int height = getHeight() - this.mListPadding.bottom;
                if (this.mFirstPosition == 0 && top >= i4 && this.mFirstPosition + childCount < this.mItemCount && bottom <= getHeight() - height) {
                    this.mTouchMode = -1;
                    reportScrollStateChange(0);
                } else {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                    int yVelocity = (int) (velocityTracker.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
                    boolean z = Math.abs(yVelocity) > this.mMinimumVelocity;
                    if (z && ((this.mFirstPosition != 0 || top != i4 - this.mOverscrollDistance) && (this.mFirstPosition + childCount != this.mItemCount || bottom != height + this.mOverscrollDistance))) {
                        int i5 = -yVelocity;
                        float f = i5;
                        if (!dispatchNestedPreFling(0.0f, f)) {
                            if (this.mFlingRunnable == null) {
                                this.mFlingRunnable = new FlingRunnable();
                            }
                            reportScrollStateChange(2);
                            this.mFlingRunnable.start(i5);
                            dispatchNestedFling(0.0f, f, true);
                        } else {
                            this.mTouchMode = -1;
                            reportScrollStateChange(0);
                        }
                    } else {
                        this.mTouchMode = -1;
                        reportScrollStateChange(0);
                        FlingRunnable flingRunnable = this.mFlingRunnable;
                        if (flingRunnable != null) {
                            flingRunnable.endFling();
                        }
                        AbsPositionScroller absPositionScroller = this.mPositionScroller;
                        if (absPositionScroller != null) {
                            absPositionScroller.stop();
                        }
                        if (z) {
                            float f2 = -yVelocity;
                            if (!dispatchNestedPreFling(0.0f, f2)) {
                                dispatchNestedFling(0.0f, f2, false);
                            }
                        }
                    }
                }
            } else {
                this.mTouchMode = -1;
                reportScrollStateChange(0);
            }
        } else if (i == 5) {
            if (this.mFlingRunnable == null) {
                this.mFlingRunnable = new FlingRunnable();
            }
            VelocityTracker velocityTracker2 = this.mVelocityTracker;
            velocityTracker2.computeCurrentVelocity(1000, this.mMaximumVelocity);
            int yVelocity2 = (int) velocityTracker2.getYVelocity(this.mActivePointerId);
            reportScrollStateChange(2);
            if (Math.abs(yVelocity2) > this.mMinimumVelocity) {
                this.mFlingRunnable.startOverfling(-yVelocity2);
            } else {
                this.mFlingRunnable.startSpringback();
            }
        }
        setPressed(false);
        EdgeEffect edgeEffect = this.mEdgeGlowTop;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        invalidate();
        removeCallbacks(this.mPendingCheckForLongPress);
        recycleVelocityTracker();
        this.mActivePointerId = -1;
        StrictMode.Span span = this.mScrollStrictSpan;
        if (span != null) {
            span.finish();
            this.mScrollStrictSpan = null;
        }
    }

    private void onTouchCancel() {
        int i = this.mTouchMode;
        if (i == 5) {
            if (this.mFlingRunnable == null) {
                this.mFlingRunnable = new FlingRunnable();
            }
            this.mFlingRunnable.startSpringback();
        } else if (i != 6) {
            this.mTouchMode = -1;
            setPressed(false);
            View childAt = getChildAt(this.mMotionPosition - this.mFirstPosition);
            if (childAt != null) {
                childAt.setPressed(false);
            }
            clearScrollingCache();
            removeCallbacks(this.mPendingCheckForLongPress);
            recycleVelocityTracker();
        }
        EdgeEffect edgeEffect = this.mEdgeGlowTop;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        this.mActivePointerId = -1;
    }

    @Override // android.view.View
    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
        if (this.mScrollY != i2) {
            onScrollChanged(this.mScrollX, i2, this.mScrollX, this.mScrollY);
            this.mScrollY = i2;
            invalidateParentIfNeeded();
            awakenScrollBars();
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        float axisValue;
        int actionButton;
        int i;
        int action = motionEvent.getAction();
        if (action == 8) {
            if (motionEvent.isFromSource(2)) {
                axisValue = motionEvent.getAxisValue(9);
            } else {
                axisValue = motionEvent.isFromSource(4194304) ? motionEvent.getAxisValue(26) : 0.0f;
            }
            int iRound = Math.round(axisValue * this.mVerticalScrollFactor);
            if (iRound != 0 && !trackMotionScroll(iRound, iRound)) {
                return true;
            }
        } else if (action == 11 && motionEvent.isFromSource(2) && (((actionButton = motionEvent.getActionButton()) == 32 || actionButton == 2) && (((i = this.mTouchMode) == 0 || i == 1) && performStylusButtonPressAction(motionEvent)))) {
            removeCallbacks(this.mPendingCheckForLongPress);
            removeCallbacks(this.mPendingCheckForTap);
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    public void fling(int i) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        reportScrollStateChange(2);
        this.mFlingRunnable.start(i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        super.onNestedScrollAccepted(view, view2, i);
        startNestedScroll(2);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        View childAt = getChildAt(getChildCount() / 2);
        int top = 0;
        int top2 = childAt != null ? childAt.getTop() : 0;
        if (childAt != null) {
            int i5 = -i4;
            if (!trackMotionScroll(i5, i5)) {
                return;
            }
        }
        if (childAt != null) {
            top = childAt.getTop() - top2;
            i4 -= top;
        }
        dispatchNestedScroll(0, top, 0, i4, null);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        int childCount = getChildCount();
        if (!z && childCount > 0) {
            int i = (int) f2;
            if (canScrollList(i) && Math.abs(f2) > this.mMinimumVelocity) {
                reportScrollStateChange(2);
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable();
                }
                if (dispatchNestedPreFling(0.0f, f2)) {
                    return true;
                }
                this.mFlingRunnable.start(i);
                return true;
            }
        }
        return dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int width;
        int height;
        int i;
        int i2;
        super.draw(canvas);
        if (this.mEdgeGlowTop != null) {
            int i3 = this.mScrollY;
            boolean clipToPadding = getClipToPadding();
            if (clipToPadding) {
                width = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
                height = (getHeight() - this.mPaddingTop) - this.mPaddingBottom;
                i = this.mPaddingLeft;
                i2 = this.mPaddingTop;
            } else {
                width = getWidth();
                height = getHeight();
                i = 0;
                i2 = 0;
            }
            if (!this.mEdgeGlowTop.isFinished()) {
                int iSave = canvas.save();
                canvas.clipRect(i, i2, i + width, this.mEdgeGlowTop.getMaxHeight() + i2);
                canvas.translate(i, Math.min(0, this.mFirstPositionDistanceGuess + i3) + i2);
                this.mEdgeGlowTop.setSize(width, height);
                if (this.mEdgeGlowTop.draw(canvas)) {
                    invalidateTopGlow();
                }
                canvas.restoreToCount(iSave);
            }
            if (this.mEdgeGlowBottom.isFinished()) {
                return;
            }
            int iSave2 = canvas.save();
            int i4 = i2 + height;
            canvas.clipRect(i, i4 - this.mEdgeGlowBottom.getMaxHeight(), i + width, i4);
            canvas.translate((-width) + i, Math.max(getHeight(), i3 + this.mLastPositionDistanceGuess) - (clipToPadding ? this.mPaddingBottom : 0));
            canvas.rotate(180.0f, width, 0.0f);
            this.mEdgeGlowBottom.setSize(width, height);
            if (this.mEdgeGlowBottom.draw(canvas)) {
                invalidateBottomGlow();
            }
            canvas.restoreToCount(iSave2);
        }
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller == null || !fastScroller.onInterceptHoverEvent(motionEvent)) {
            return super.onInterceptHoverEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
        PointerIcon pointerIconOnResolvePointerIcon;
        FastScroller fastScroller = this.mFastScroll;
        return (fastScroller == null || (pointerIconOnResolvePointerIcon = fastScroller.onResolvePointerIcon(motionEvent, i)) == null) ? super.onResolvePointerIcon(motionEvent, i) : pointerIconOnResolvePointerIcon;
    }

    /* JADX WARN: Code duplicated, block: B:34:0x0068  */
    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        AbsPositionScroller absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        if (this.mIsDetaching || !isAttachedToWindow()) {
            return false;
        }
        FastScroller fastScroller = this.mFastScroll;
        if (fastScroller != null && fastScroller.onInterceptTouchEvent(motionEvent)) {
            return true;
        }
        if (actionMasked == 0) {
            int i = this.mTouchMode;
            if (i == 6 || i == 5) {
                this.mMotionCorrection = 0;
                return true;
            }
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            this.mActivePointerId = motionEvent.getPointerId(0);
            int iFindMotionRow = findMotionRow(y);
            if (i != 4 && iFindMotionRow >= 0) {
                this.mMotionViewOriginalTop = getChildAt(iFindMotionRow - this.mFirstPosition).getTop();
                this.mMotionX = x;
                this.mMotionY = y;
                this.mMotionPosition = iFindMotionRow;
                this.mTouchMode = 0;
                clearScrollingCache();
            }
            this.mLastY = Integer.MIN_VALUE;
            initOrResetVelocityTracker();
            this.mVelocityTracker.addMovement(motionEvent);
            this.mNestedYOffset = 0;
            startNestedScroll(2);
            if (i == 4) {
                return true;
            }
        } else if (actionMasked == 1) {
            this.mTouchMode = -1;
            this.mActivePointerId = -1;
            recycleVelocityTracker();
            reportScrollStateChange(0);
            stopNestedScroll();
        } else if (actionMasked != 2) {
            if (actionMasked == 3) {
                this.mTouchMode = -1;
                this.mActivePointerId = -1;
                recycleVelocityTracker();
                reportScrollStateChange(0);
                stopNestedScroll();
            } else if (actionMasked == 6) {
                onSecondaryPointerUp(motionEvent);
            }
        } else if (this.mTouchMode == 0) {
            int iFindPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
            if (iFindPointerIndex == -1) {
                this.mActivePointerId = motionEvent.getPointerId(0);
                iFindPointerIndex = 0;
            }
            int y2 = (int) motionEvent.getY(iFindPointerIndex);
            initVelocityTrackerIfNotExists();
            this.mVelocityTracker.addMovement(motionEvent);
            if (startScrollIfNeeded((int) motionEvent.getX(iFindPointerIndex), y2, null)) {
                return true;
            }
        }
        return false;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int action = (motionEvent.getAction() & 65280) >> 8;
        if (motionEvent.getPointerId(action) == this.mActivePointerId) {
            int i = action == 0 ? 1 : 0;
            this.mMotionX = (int) motionEvent.getX(i);
            this.mMotionY = (int) motionEvent.getY(i);
            this.mMotionCorrection = 0;
            this.mActivePointerId = motionEvent.getPointerId(i);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addTouchables(ArrayList<View> arrayList) {
        int childCount = getChildCount();
        int i = this.mFirstPosition;
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null) {
            return;
        }
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (listAdapter.isEnabled(i + i2)) {
                arrayList.add(childAt);
            }
            childAt.addTouchables(arrayList);
        }
    }

    void reportScrollStateChange(int i) {
        OnScrollListener onScrollListener;
        if (i == this.mLastScrollState || (onScrollListener = this.mOnScrollListener) == null) {
            return;
        }
        this.mLastScrollState = i;
        onScrollListener.onScrollStateChanged(this, i);
    }

    private class FlingRunnable implements Runnable {
        private static final int FLYWHEEL_TIMEOUT = 40;
        private final Runnable mCheckFlywheel = new Runnable() { // from class: android.widget.AbsListView.FlingRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                int i = AbsListView.this.mActivePointerId;
                VelocityTracker velocityTracker = AbsListView.this.mVelocityTracker;
                OverScroller overScroller = FlingRunnable.this.mScroller;
                if (velocityTracker == null || i == -1) {
                    return;
                }
                velocityTracker.computeCurrentVelocity(1000, AbsListView.this.mMaximumVelocity);
                float f = -velocityTracker.getYVelocity(i);
                if (Math.abs(f) >= AbsListView.this.mMinimumVelocity && overScroller.isScrollingInDirection(0.0f, f)) {
                    AbsListView.this.postDelayed(this, 40L);
                    return;
                }
                FlingRunnable.this.endFling();
                AbsListView.this.mTouchMode = 3;
                AbsListView.this.reportScrollStateChange(1);
            }
        };
        private int mLastFlingY;
        private final OverScroller mScroller;
        private boolean mSuppressIdleStateChangeCall;

        FlingRunnable() {
            this.mScroller = new OverScroller(AbsListView.this.getContext());
        }

        void start(int i) {
            int i2 = i < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = i2;
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, i2, 0, i, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            AbsListView.this.mTouchMode = 4;
            this.mSuppressIdleStateChangeCall = false;
            AbsListView.this.postOnAnimation(this);
            if (AbsListView.this.mFlingStrictSpan == null) {
                AbsListView.this.mFlingStrictSpan = StrictMode.enterCriticalSpan("AbsListView-fling");
            }
        }

        void startSpringback() {
            this.mSuppressIdleStateChangeCall = false;
            if (this.mScroller.springBack(0, AbsListView.this.mScrollY, 0, 0, 0, 0)) {
                AbsListView.this.mTouchMode = 6;
                AbsListView.this.invalidate();
                AbsListView.this.postOnAnimation(this);
            } else {
                AbsListView.this.mTouchMode = -1;
                AbsListView.this.reportScrollStateChange(0);
            }
        }

        void startOverfling(int i) {
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, AbsListView.this.mScrollY, 0, i, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, AbsListView.this.getHeight());
            AbsListView.this.mTouchMode = 6;
            this.mSuppressIdleStateChangeCall = false;
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void edgeReached(int i) {
            this.mScroller.notifyVerticalEdgeReached(AbsListView.this.mScrollY, 0, AbsListView.this.mOverflingDistance);
            int overScrollMode = AbsListView.this.getOverScrollMode();
            if (overScrollMode == 0 || (overScrollMode == 1 && !AbsListView.this.contentFits())) {
                AbsListView.this.mTouchMode = 6;
                int currVelocity = (int) this.mScroller.getCurrVelocity();
                if (i > 0) {
                    AbsListView.this.mEdgeGlowTop.onAbsorb(currVelocity);
                } else {
                    AbsListView.this.mEdgeGlowBottom.onAbsorb(currVelocity);
                }
            } else {
                AbsListView.this.mTouchMode = -1;
                if (AbsListView.this.mPositionScroller != null) {
                    AbsListView.this.mPositionScroller.stop();
                }
            }
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void startScroll(int i, int i2, boolean z, boolean z2) {
            int i3 = i < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = i3;
            this.mScroller.setInterpolator(z ? AbsListView.sLinearInterpolator : null);
            this.mScroller.startScroll(0, i3, 0, i, i2);
            AbsListView.this.mTouchMode = 4;
            this.mSuppressIdleStateChangeCall = z2;
            AbsListView.this.postOnAnimation(this);
        }

        void endFling() {
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.removeCallbacks(this);
            AbsListView.this.removeCallbacks(this.mCheckFlywheel);
            if (!this.mSuppressIdleStateChangeCall) {
                AbsListView.this.reportScrollStateChange(0);
            }
            AbsListView.this.clearScrollingCache();
            this.mScroller.abortAnimation();
            if (AbsListView.this.mFlingStrictSpan != null) {
                AbsListView.this.mFlingStrictSpan.finish();
                AbsListView.this.mFlingStrictSpan = null;
            }
        }

        void flywheelTouch() {
            AbsListView.this.postDelayed(this.mCheckFlywheel, 40L);
        }

        @Override // java.lang.Runnable
        public void run() {
            int iMax;
            int i = AbsListView.this.mTouchMode;
            boolean z = false;
            if (i != 3) {
                if (i != 4) {
                    if (i != 6) {
                        endFling();
                        return;
                    }
                    OverScroller overScroller = this.mScroller;
                    if (overScroller.computeScrollOffset()) {
                        int i2 = AbsListView.this.mScrollY;
                        int currY = overScroller.getCurrY();
                        AbsListView absListView = AbsListView.this;
                        if (!absListView.overScrollBy(0, currY - i2, 0, i2, 0, 0, 0, absListView.mOverflingDistance, false)) {
                            AbsListView.this.invalidate();
                            AbsListView.this.postOnAnimation(this);
                            return;
                        }
                        boolean z2 = i2 <= 0 && currY > 0;
                        if (i2 >= 0 && currY < 0) {
                            z = true;
                        }
                        if (z2 || z) {
                            int currVelocity = (int) overScroller.getCurrVelocity();
                            if (z) {
                                currVelocity = -currVelocity;
                            }
                            overScroller.abortAnimation();
                            start(currVelocity);
                            return;
                        }
                        startSpringback();
                        return;
                    }
                    endFling();
                    return;
                }
            } else if (this.mScroller.isFinished()) {
                return;
            }
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.layoutChildren();
            }
            if (AbsListView.this.mItemCount == 0 || AbsListView.this.getChildCount() == 0) {
                endFling();
                return;
            }
            OverScroller overScroller2 = this.mScroller;
            boolean zComputeScrollOffset = overScroller2.computeScrollOffset();
            int currY2 = overScroller2.getCurrY();
            int i3 = this.mLastFlingY - currY2;
            if (i3 > 0) {
                AbsListView absListView2 = AbsListView.this;
                absListView2.mMotionPosition = absListView2.mFirstPosition;
                AbsListView.this.mMotionViewOriginalTop = AbsListView.this.getChildAt(0).getTop();
                iMax = Math.min(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1, i3);
            } else {
                int childCount = AbsListView.this.getChildCount() - 1;
                AbsListView absListView3 = AbsListView.this;
                absListView3.mMotionPosition = absListView3.mFirstPosition + childCount;
                AbsListView.this.mMotionViewOriginalTop = AbsListView.this.getChildAt(childCount).getTop();
                iMax = Math.max(-(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1), i3);
            }
            AbsListView absListView4 = AbsListView.this;
            View childAt = absListView4.getChildAt(absListView4.mMotionPosition - AbsListView.this.mFirstPosition);
            int top = childAt != null ? childAt.getTop() : 0;
            boolean zTrackMotionScroll = AbsListView.this.trackMotionScroll(iMax, iMax);
            if (zTrackMotionScroll && iMax != 0) {
                z = true;
            }
            if (z) {
                if (childAt != null) {
                    int i4 = -(iMax - (childAt.getTop() - top));
                    AbsListView absListView5 = AbsListView.this;
                    absListView5.overScrollBy(0, i4, 0, absListView5.mScrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false);
                }
                if (zComputeScrollOffset) {
                    edgeReached(iMax);
                    return;
                }
                return;
            }
            if (zComputeScrollOffset && !z) {
                if (zTrackMotionScroll) {
                    AbsListView.this.invalidate();
                }
                this.mLastFlingY = currY2;
                AbsListView.this.postOnAnimation(this);
                return;
            }
            endFling();
        }
    }

    public void setFriction(float f) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        this.mFlingRunnable.mScroller.setFriction(f);
    }

    public void setVelocityScale(float f) {
        this.mVelocityScale = f;
    }

    AbsPositionScroller createPositionScroller() {
        return new PositionScroller();
    }

    public void smoothScrollToPosition(int i) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(i);
    }

    public void smoothScrollToPositionFromTop(int i, int i2, int i3) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(i, i2, i3);
    }

    public void smoothScrollToPositionFromTop(int i, int i2) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(i, i2);
    }

    public void smoothScrollToPosition(int i, int i2) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(i, i2);
    }

    public void smoothScrollBy(int i, int i2) {
        smoothScrollBy(i, i2, false, false);
    }

    void smoothScrollBy(int i, int i2, boolean z, boolean z2) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        int i3 = this.mFirstPosition;
        int childCount = getChildCount();
        int i4 = i3 + childCount;
        int paddingTop = getPaddingTop();
        int height = getHeight() - getPaddingBottom();
        if (i == 0 || this.mItemCount == 0 || childCount == 0 || ((i3 == 0 && getChildAt(0).getTop() == paddingTop && i < 0) || (i4 == this.mItemCount && getChildAt(childCount - 1).getBottom() == height && i > 0))) {
            this.mFlingRunnable.endFling();
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
                return;
            }
            return;
        }
        reportScrollStateChange(2);
        this.mFlingRunnable.startScroll(i, i2, z, z2);
    }

    void smoothScrollByOffset(int i) {
        int lastVisiblePosition;
        View childAt;
        if (i < 0) {
            lastVisiblePosition = getFirstVisiblePosition();
        } else {
            lastVisiblePosition = i > 0 ? getLastVisiblePosition() : -1;
        }
        if (lastVisiblePosition <= -1 || (childAt = getChildAt(lastVisiblePosition - getFirstVisiblePosition())) == null) {
            return;
        }
        Rect rect = new Rect();
        if (childAt.getGlobalVisibleRect(rect)) {
            float fWidth = (rect.width() * rect.height()) / (childAt.getWidth() * childAt.getHeight());
            if (i < 0 && fWidth < 0.75f) {
                lastVisiblePosition++;
            } else if (i > 0 && fWidth < 0.75f) {
                lastVisiblePosition--;
            }
        }
        smoothScrollToPosition(Math.max(0, Math.min(getCount(), lastVisiblePosition + i)));
    }

    private void createScrollingCache() {
        if (!this.mScrollingCacheEnabled || this.mCachingStarted || isHardwareAccelerated()) {
            return;
        }
        setChildrenDrawnWithCacheEnabled(true);
        setChildrenDrawingCacheEnabled(true);
        this.mCachingActive = true;
        this.mCachingStarted = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearScrollingCache() {
        if (isHardwareAccelerated()) {
            return;
        }
        if (this.mClearScrollingCache == null) {
            this.mClearScrollingCache = new Runnable() { // from class: android.widget.AbsListView.4
                @Override // java.lang.Runnable
                public void run() {
                    if (AbsListView.this.mCachingStarted) {
                        AbsListView absListView = AbsListView.this;
                        absListView.mCachingActive = false;
                        absListView.mCachingStarted = false;
                        AbsListView.this.setChildrenDrawnWithCacheEnabled(false);
                        if ((AbsListView.this.mPersistentDrawingCache & 2) == 0) {
                            AbsListView.this.setChildrenDrawingCacheEnabled(false);
                        }
                        if (AbsListView.this.isAlwaysDrawnWithCacheEnabled()) {
                            return;
                        }
                        AbsListView.this.invalidate();
                    }
                }
            };
        }
        post(this.mClearScrollingCache);
    }

    public void scrollListBy(int i) {
        int i2 = -i;
        trackMotionScroll(i2, i2);
    }

    public boolean canScrollList(int i) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }
        int i2 = this.mFirstPosition;
        Rect rect = this.mListPadding;
        if (i > 0) {
            return i2 + childCount < this.mItemCount || getChildAt(childCount + (-1)).getBottom() > getHeight() - rect.bottom;
        }
        return i2 > 0 || getChildAt(0).getTop() < rect.top;
    }

    /* JADX WARN: Code duplicated, block: B:106:0x0197  */
    boolean trackMotionScroll(int i, int i2) {
        int i3;
        int i4;
        int iMin;
        int iMin2;
        int i5;
        int i6;
        int i7;
        boolean z;
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        int top = getChildAt(0).getTop();
        int i8 = childCount - 1;
        int bottom = getChildAt(i8).getBottom();
        Rect rect = this.mListPadding;
        if ((this.mGroupFlags & 34) == 34) {
            i3 = rect.top;
            i4 = rect.bottom;
        } else {
            i3 = 0;
            i4 = 0;
        }
        int i9 = i3 - top;
        int height = bottom - (getHeight() - i4);
        int height2 = (getHeight() - this.mPaddingBottom) - this.mPaddingTop;
        if (i < 0) {
            iMin = Math.max(-(height2 - 1), i);
        } else {
            iMin = Math.min(height2 - 1, i);
        }
        if (i2 < 0) {
            iMin2 = Math.max(-(height2 - 1), i2);
        } else {
            iMin2 = Math.min(height2 - 1, i2);
        }
        int i10 = this.mFirstPosition;
        if (i10 == 0) {
            this.mFirstPositionDistanceGuess = top - rect.top;
        } else {
            this.mFirstPositionDistanceGuess += iMin2;
        }
        int i11 = i10 + childCount;
        if (i11 == this.mItemCount) {
            this.mLastPositionDistanceGuess = rect.bottom + bottom;
        } else {
            this.mLastPositionDistanceGuess += iMin2;
        }
        boolean z2 = i10 == 0 && top >= rect.top && iMin2 >= 0;
        boolean z3 = i11 == this.mItemCount && bottom <= getHeight() - rect.bottom && iMin2 <= 0;
        if (z2 || z3) {
            return iMin2 != 0;
        }
        boolean z4 = iMin2 < 0;
        boolean zIsInTouchMode = isInTouchMode();
        if (zIsInTouchMode) {
            hideSelector();
        }
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = this.mItemCount - getFooterViewsCount();
        if (z4) {
            int i12 = -iMin2;
            if ((this.mGroupFlags & 34) == 34) {
                i12 += rect.top;
            }
            int i13 = 0;
            i6 = 0;
            while (i13 < childCount) {
                View childAt = getChildAt(i13);
                if (childAt.getBottom() >= i12) {
                    break;
                }
                i6++;
                int i14 = i10 + i13;
                if (i14 >= headerViewsCount && i14 < footerViewsCount) {
                    childAt.clearAccessibilityFocus();
                    this.mRecycler.addScrapView(childAt, i14);
                }
                i13++;
                childCount = childCount;
            }
            i5 = 0;
        } else {
            int height3 = getHeight() - iMin2;
            if ((this.mGroupFlags & 34) == 34) {
                height3 -= rect.bottom;
            }
            i5 = 0;
            i6 = 0;
            while (i8 >= 0) {
                View childAt2 = getChildAt(i8);
                if (childAt2.getTop() <= height3) {
                    break;
                }
                i6++;
                int i15 = i10 + i8;
                if (i15 >= headerViewsCount && i15 < footerViewsCount) {
                    childAt2.clearAccessibilityFocus();
                    this.mRecycler.addScrapView(childAt2, i15);
                }
                int i16 = i8;
                i8--;
                i5 = i16;
            }
        }
        this.mMotionViewNewTop = this.mMotionViewOriginalTop + iMin;
        this.mBlockLayoutRequests = true;
        if (i6 > 0) {
            detachViewsFromParent(i5, i6);
            this.mRecycler.removeSkippedScrap();
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        offsetChildrenTopAndBottom(iMin2);
        if (z4) {
            this.mFirstPosition += i6;
        }
        int iAbs = Math.abs(iMin2);
        if (i9 < iAbs || height < iAbs) {
            fillGap(z4);
        }
        this.mRecycler.fullyDetachScrapViews();
        if (!zIsInTouchMode && this.mSelectedPosition != -1) {
            int i17 = this.mSelectedPosition - this.mFirstPosition;
            if (i17 < 0 || i17 >= getChildCount()) {
                z = false;
            } else {
                positionSelector(this.mSelectedPosition, getChildAt(i17));
                z = true;
            }
        } else {
            int i18 = this.mSelectorPosition;
            if (i18 == -1 || (i7 = i18 - this.mFirstPosition) < 0 || i7 >= getChildCount()) {
                z = false;
            } else {
                positionSelector(this.mSelectorPosition, getChildAt(i7));
                z = true;
            }
        }
        if (!z) {
            this.mSelectorRect.setEmpty();
        }
        this.mBlockLayoutRequests = false;
        invokeOnItemScrollListener();
        return false;
    }

    void hideSelector() {
        if (this.mSelectedPosition != -1) {
            if (this.mLayoutMode != 4) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
            if (this.mNextSelectedPosition >= 0 && this.mNextSelectedPosition != this.mSelectedPosition) {
                this.mResurrectToPosition = this.mNextSelectedPosition;
            }
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectedTop = 0;
        }
    }

    int reconcileSelectedPosition() {
        int i = this.mSelectedPosition;
        if (i < 0) {
            i = this.mResurrectToPosition;
        }
        return Math.min(Math.max(0, i), this.mItemCount - 1);
    }

    int findClosestMotionRow(int i) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return -1;
        }
        int iFindMotionRow = findMotionRow(i);
        return iFindMotionRow != -1 ? iFindMotionRow : (this.mFirstPosition + childCount) - 1;
    }

    public void invalidateViews() {
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }

    boolean resurrectSelectionIfNeeded() {
        if (this.mSelectedPosition >= 0 || !resurrectSelection()) {
            return false;
        }
        updateSelectorState();
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:42:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:50:0x00d2  */
    /* JADX WARN: Code duplicated, block: B:56:? A[RETURN, SYNTHETIC] */
    boolean resurrectSelection() {
        boolean z;
        int top;
        int i;
        AbsPositionScroller absPositionScroller;
        int iLookForSelectablePosition;
        int childCount = getChildCount();
        if (childCount <= 0) {
            return false;
        }
        int verticalFadingEdgeLength = this.mListPadding.top;
        int verticalFadingEdgeLength2 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int i2 = this.mFirstPosition;
        int i3 = this.mResurrectToPosition;
        if (i3 >= i2 && i3 < i2 + childCount) {
            View childAt = getChildAt(i3 - this.mFirstPosition);
            top = childAt.getTop();
            int bottom = childAt.getBottom();
            if (top < verticalFadingEdgeLength) {
                top = verticalFadingEdgeLength + getVerticalFadingEdgeLength();
            } else if (bottom > verticalFadingEdgeLength2) {
                top = (verticalFadingEdgeLength2 - childAt.getMeasuredHeight()) - getVerticalFadingEdgeLength();
            }
        } else {
            if (i3 >= i2) {
                int i4 = this.mItemCount;
                int i5 = i2 + childCount;
                int i6 = i5 - 1;
                int i7 = childCount - 1;
                int i8 = i7;
                int i9 = 0;
                while (true) {
                    if (i8 < 0) {
                        z = false;
                        i3 = i6;
                        top = i9;
                        break;
                    }
                    View childAt2 = getChildAt(i8);
                    int top2 = childAt2.getTop();
                    int bottom2 = childAt2.getBottom();
                    if (i8 == i7) {
                        if (i5 < i4 || bottom2 > verticalFadingEdgeLength2) {
                            verticalFadingEdgeLength2 -= getVerticalFadingEdgeLength();
                        }
                        i9 = top2;
                    }
                    if (bottom2 <= verticalFadingEdgeLength2) {
                        i3 = i2 + i8;
                        z = false;
                        top = top2;
                        break;
                    }
                    i8--;
                }
            } else {
                int i10 = 0;
                int i11 = 0;
                while (true) {
                    if (i10 >= childCount) {
                        top = i11;
                        i3 = i2;
                        break;
                    }
                    top = getChildAt(i10).getTop();
                    if (i10 == 0) {
                        if (i2 > 0 || top < verticalFadingEdgeLength) {
                            verticalFadingEdgeLength += getVerticalFadingEdgeLength();
                        }
                        i11 = top;
                    }
                    if (top >= verticalFadingEdgeLength) {
                        i3 = i10 + i2;
                        break;
                    }
                    i10++;
                }
            }
            i = -1;
            this.mResurrectToPosition = -1;
            removeCallbacks(this.mFlingRunnable);
            absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            this.mTouchMode = -1;
            clearScrollingCache();
            this.mSpecificTop = top;
            iLookForSelectablePosition = lookForSelectablePosition(i3, z);
            if (iLookForSelectablePosition >= i2 && iLookForSelectablePosition <= getLastVisiblePosition()) {
                this.mLayoutMode = 4;
                updateSelectorState();
                setSelectionInt(iLookForSelectablePosition);
                invokeOnItemScrollListener();
                i = iLookForSelectablePosition;
            }
            reportScrollStateChange(0);
            if (i >= 0) {
                return true;
            }
            return false;
        }
        z = true;
        i = -1;
        this.mResurrectToPosition = -1;
        removeCallbacks(this.mFlingRunnable);
        absPositionScroller = this.mPositionScroller;
        if (absPositionScroller != null) {
            absPositionScroller.stop();
        }
        this.mTouchMode = -1;
        clearScrollingCache();
        this.mSpecificTop = top;
        iLookForSelectablePosition = lookForSelectablePosition(i3, z);
        if (iLookForSelectablePosition >= i2) {
            this.mLayoutMode = 4;
            updateSelectorState();
            setSelectionInt(iLookForSelectablePosition);
            invokeOnItemScrollListener();
            i = iLookForSelectablePosition;
        }
        reportScrollStateChange(0);
        if (i >= 0) {
            return true;
        }
        return false;
    }

    void confirmCheckedPositionsById() {
        ActionMode actionMode;
        boolean z;
        MultiChoiceModeWrapper multiChoiceModeWrapper;
        this.mCheckStates.clear();
        int i = 0;
        boolean z2 = false;
        while (i < this.mCheckedIdStates.size()) {
            long jKeyAt = this.mCheckedIdStates.keyAt(i);
            int iIntValue = this.mCheckedIdStates.valueAt(i).intValue();
            if (jKeyAt != this.mAdapter.getItemId(iIntValue)) {
                int iMax = Math.max(0, iIntValue - 20);
                int iMin = Math.min(iIntValue + 20, this.mItemCount);
                while (true) {
                    if (iMax >= iMin) {
                        z = false;
                        break;
                    } else {
                        if (jKeyAt == this.mAdapter.getItemId(iMax)) {
                            this.mCheckStates.put(iMax, true);
                            this.mCheckedIdStates.setValueAt(i, Integer.valueOf(iMax));
                            z = true;
                            break;
                        }
                        iMax++;
                    }
                }
                if (!z) {
                    this.mCheckedIdStates.delete(jKeyAt);
                    i--;
                    this.mCheckedItemCount--;
                    ActionMode actionMode2 = this.mChoiceActionMode;
                    if (actionMode2 != null && (multiChoiceModeWrapper = this.mMultiChoiceModeCallback) != null) {
                        multiChoiceModeWrapper.onItemCheckedStateChanged(actionMode2, iIntValue, jKeyAt, false);
                    }
                    z2 = true;
                }
            } else {
                this.mCheckStates.put(iIntValue, true);
            }
            i++;
        }
        if (!z2 || (actionMode = this.mChoiceActionMode) == null) {
            return;
        }
        actionMode.invalidate();
    }

    @Override // android.widget.AdapterView
    protected void handleDataChanged() {
        ListAdapter listAdapter;
        int i = this.mItemCount;
        int i2 = this.mLastHandledItemCount;
        this.mLastHandledItemCount = this.mItemCount;
        if (this.mChoiceMode != 0 && (listAdapter = this.mAdapter) != null && listAdapter.hasStableIds()) {
            confirmCheckedPositionsById();
        }
        this.mRecycler.clearTransientStateViews();
        if (i > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                this.mPendingSync = null;
                int i3 = this.mTranscriptMode;
                if (i3 == 2) {
                    this.mLayoutMode = 3;
                    return;
                }
                if (i3 == 1) {
                    if (this.mForceTranscriptScroll) {
                        this.mForceTranscriptScroll = false;
                        this.mLayoutMode = 3;
                        return;
                    }
                    int childCount = getChildCount();
                    int height = getHeight() - getPaddingBottom();
                    View childAt = getChildAt(childCount - 1);
                    int bottom = childAt != null ? childAt.getBottom() : height;
                    if (this.mFirstPosition + childCount >= i2 && bottom <= height) {
                        this.mLayoutMode = 3;
                        return;
                    }
                    awakenScrollBars();
                }
                int i4 = this.mSyncMode;
                if (i4 != 0) {
                    if (i4 == 1) {
                        this.mLayoutMode = 5;
                        this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), i - 1);
                        return;
                    }
                } else {
                    if (isInTouchMode()) {
                        this.mLayoutMode = 5;
                        this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), i - 1);
                        return;
                    }
                    int iFindSyncPosition = findSyncPosition();
                    if (iFindSyncPosition >= 0 && lookForSelectablePosition(iFindSyncPosition, true) == iFindSyncPosition) {
                        this.mSyncPosition = iFindSyncPosition;
                        if (this.mSyncHeight == getHeight()) {
                            this.mLayoutMode = 5;
                        } else {
                            this.mLayoutMode = 2;
                        }
                        setNextSelectedPositionInt(iFindSyncPosition);
                        return;
                    }
                }
            }
            if (!isInTouchMode()) {
                int selectedItemPosition = getSelectedItemPosition();
                if (selectedItemPosition >= i) {
                    selectedItemPosition = i - 1;
                }
                if (selectedItemPosition < 0) {
                    selectedItemPosition = 0;
                }
                int iLookForSelectablePosition = lookForSelectablePosition(selectedItemPosition, true);
                if (iLookForSelectablePosition >= 0) {
                    setNextSelectedPositionInt(iLookForSelectablePosition);
                    return;
                }
                int iLookForSelectablePosition2 = lookForSelectablePosition(selectedItemPosition, false);
                if (iLookForSelectablePosition2 >= 0) {
                    setNextSelectedPositionInt(iLookForSelectablePosition2);
                    return;
                }
            } else if (this.mResurrectToPosition >= 0) {
                return;
            }
        }
        this.mLayoutMode = this.mStackFromBottom ? 3 : 1;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mSelectorPosition = -1;
        checkSelectionChanged();
    }

    @Override // android.view.View
    protected void onDisplayHint(int i) {
        PopupWindow popupWindow;
        PopupWindow popupWindow2;
        super.onDisplayHint(i);
        if (i != 0) {
            if (i == 4 && (popupWindow2 = this.mPopup) != null && popupWindow2.isShowing()) {
                dismissPopup();
            }
        } else if (this.mFiltered && (popupWindow = this.mPopup) != null && !popupWindow.isShowing()) {
            showPopup();
        }
        this.mPopupHidden = i == 4;
    }

    private void dismissPopup() {
        PopupWindow popupWindow = this.mPopup;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void showPopup() {
        if (getWindowVisibility() == 0) {
            createTextFilter(true);
            positionPopup();
            checkFocus();
        }
    }

    private void positionPopup() {
        int i = getResources().getDisplayMetrics().heightPixels;
        int[] iArr = new int[2];
        getLocationOnScreen(iArr);
        int height = ((i - iArr[1]) - getHeight()) + ((int) (this.mDensityScale * 20.0f));
        if (!this.mPopup.isShowing()) {
            this.mPopup.showAtLocation(this, 81, iArr[0], height);
        } else {
            this.mPopup.update(iArr[0], height, -1, -1);
        }
    }

    static int getDistance(Rect rect, Rect rect2, int i) {
        int iWidth;
        int iHeight;
        int iWidth2;
        int i2;
        int iHeight2;
        int i3;
        if (i == 1 || i == 2) {
            iWidth = rect.right + (rect.width() / 2);
            iHeight = (rect.height() / 2) + rect.top;
            iWidth2 = rect2.left + (rect2.width() / 2);
            i2 = rect2.top;
            iHeight2 = rect2.height() / 2;
        } else {
            if (i != 17) {
                if (i == 33) {
                    iWidth = rect.left + (rect.width() / 2);
                    iHeight = rect.top;
                    iWidth2 = rect2.left + (rect2.width() / 2);
                    i3 = rect2.bottom;
                } else if (i == 66) {
                    iWidth = rect.right;
                    iHeight = (rect.height() / 2) + rect.top;
                    iWidth2 = rect2.left;
                    i2 = rect2.top;
                    iHeight2 = rect2.height() / 2;
                } else if (i == 130) {
                    iWidth = rect.left + (rect.width() / 2);
                    iHeight = rect.bottom;
                    iWidth2 = rect2.left + (rect2.width() / 2);
                    i3 = rect2.top;
                } else {
                    throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
                }
                int i4 = iWidth2 - iWidth;
                int i5 = i3 - iHeight;
                return (i5 * i5) + (i4 * i4);
            }
            iWidth = rect.left;
            iHeight = (rect.height() / 2) + rect.top;
            iWidth2 = rect2.right;
            i2 = rect2.top;
            iHeight2 = rect2.height() / 2;
        }
        i3 = iHeight2 + i2;
        int i6 = iWidth2 - iWidth;
        int i7 = i3 - iHeight;
        return (i7 * i7) + (i6 * i6);
    }

    @Override // android.widget.AdapterView
    protected boolean isInFilterMode() {
        return this.mFiltered;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x001a  */
    /* JADX WARN: Code duplicated, block: B:36:0x0060  */
    boolean sendToTextFilter(int i, int i2, KeyEvent keyEvent) {
        boolean z;
        boolean z2;
        boolean z3;
        PopupWindow popupWindow;
        if (!acceptFilter()) {
            return false;
        }
        if (i == 4) {
            if (this.mFiltered && (popupWindow = this.mPopup) != null && popupWindow.isShowing()) {
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, this);
                    }
                } else if (keyEvent.getAction() == 1 && keyEvent.isTracking() && !keyEvent.isCanceled()) {
                    this.mTextFilter.setText("");
                } else {
                    z = false;
                }
                z = true;
            } else {
                z = false;
            }
            z2 = z;
            z3 = false;
        } else if (i == 62) {
            z3 = this.mFiltered;
            z2 = false;
        } else if (i != 66) {
            switch (i) {
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    z3 = false;
                    z2 = false;
                    break;
                default:
                    z2 = false;
                    z3 = true;
                    break;
            }
        } else {
            z3 = false;
            z2 = false;
        }
        if (!z3) {
            return z2;
        }
        createTextFilter(true);
        KeyEvent keyEventChangeTimeRepeat = keyEvent.getRepeatCount() > 0 ? KeyEvent.changeTimeRepeat(keyEvent, keyEvent.getEventTime(), 0) : keyEvent;
        int action = keyEvent.getAction();
        if (action == 0) {
            return this.mTextFilter.onKeyDown(i, keyEventChangeTimeRepeat);
        }
        if (action != 1) {
            return action != 2 ? z2 : this.mTextFilter.onKeyMultiple(i, i2, keyEvent);
        }
        return this.mTextFilter.onKeyUp(i, keyEventChangeTimeRepeat);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        if (!isTextFilterEnabled()) {
            return null;
        }
        if (this.mPublicInputConnection == null) {
            this.mDefInputConnection = new BaseInputConnection((View) this, false);
            this.mPublicInputConnection = new InputConnectionWrapper(editorInfo);
        }
        editorInfo.inputType = 177;
        editorInfo.imeOptions = 6;
        return this.mPublicInputConnection;
    }

    private class InputConnectionWrapper implements InputConnection {
        private final EditorInfo mOutAttrs;
        private InputConnection mTarget;

        public InputConnectionWrapper(EditorInfo editorInfo) {
            this.mOutAttrs = editorInfo;
        }

        private InputConnection getTarget() {
            if (this.mTarget == null) {
                this.mTarget = AbsListView.this.getTextFilterInput().onCreateInputConnection(this.mOutAttrs);
            }
            return this.mTarget;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean reportFullscreenMode(boolean z) {
            return AbsListView.this.mDefInputConnection.reportFullscreenMode(z);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performEditorAction(int i) {
            if (i != 6) {
                return false;
            }
            InputMethodManager inputMethodManager = (InputMethodManager) AbsListView.this.getContext().getSystemService(InputMethodManager.class);
            if (inputMethodManager == null) {
                return true;
            }
            inputMethodManager.hideSoftInputFromWindow(AbsListView.this.getWindowToken(), 0);
            return true;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean sendKeyEvent(KeyEvent keyEvent) {
            return AbsListView.this.mDefInputConnection.sendKeyEvent(keyEvent);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextBeforeCursor(int i, int i2) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getTextBeforeCursor(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextAfterCursor(int i, int i2) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getTextAfterCursor(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getSelectedText(int i) {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null ? "" : inputConnection.getSelectedText(i);
        }

        @Override // android.view.inputmethod.InputConnection
        public int getCursorCapsMode(int i) {
            InputConnection inputConnection = this.mTarget;
            if (inputConnection == null) {
                return 16384;
            }
            return inputConnection.getCursorCapsMode(i);
        }

        @Override // android.view.inputmethod.InputConnection
        public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
            return getTarget().getExtractedText(extractedTextRequest, i);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean deleteSurroundingText(int i, int i2) {
            return getTarget().deleteSurroundingText(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean deleteSurroundingTextInCodePoints(int i, int i2) {
            return getTarget().deleteSurroundingTextInCodePoints(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingText(CharSequence charSequence, int i) {
            return getTarget().setComposingText(charSequence, i);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingRegion(int i, int i2) {
            return getTarget().setComposingRegion(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean finishComposingText() {
            InputConnection inputConnection = this.mTarget;
            return inputConnection == null || inputConnection.finishComposingText();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitText(CharSequence charSequence, int i) {
            return getTarget().commitText(charSequence, i);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCompletion(CompletionInfo completionInfo) {
            return getTarget().commitCompletion(completionInfo);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return getTarget().commitCorrection(correctionInfo);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setSelection(int i, int i2) {
            return getTarget().setSelection(i, i2);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performContextMenuAction(int i) {
            return getTarget().performContextMenuAction(i);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean beginBatchEdit() {
            return getTarget().beginBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean endBatchEdit() {
            return getTarget().endBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean clearMetaKeyStates(int i) {
            return getTarget().clearMetaKeyStates(i);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performPrivateCommand(String str, Bundle bundle) {
            return getTarget().performPrivateCommand(str, bundle);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean requestCursorUpdates(int i) {
            return getTarget().requestCursorUpdates(i);
        }

        @Override // android.view.inputmethod.InputConnection
        public Handler getHandler() {
            return getTarget().getHandler();
        }

        @Override // android.view.inputmethod.InputConnection
        public void closeConnection() {
            getTarget().closeConnection();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitContent(InputContentInfo inputContentInfo, int i, Bundle bundle) {
            return getTarget().commitContent(inputContentInfo, i, bundle);
        }
    }

    @Override // android.view.View
    public boolean checkInputConnectionProxy(View view) {
        return view == this.mTextFilter;
    }

    private void createTextFilter(boolean z) {
        if (this.mPopup == null) {
            PopupWindow popupWindow = new PopupWindow(getContext());
            popupWindow.setFocusable(false);
            popupWindow.setTouchable(false);
            popupWindow.setInputMethodMode(2);
            popupWindow.setContentView(getTextFilterInput());
            popupWindow.setWidth(-2);
            popupWindow.setHeight(-2);
            popupWindow.setBackgroundDrawable(null);
            this.mPopup = popupWindow;
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = true;
        }
        if (z) {
            this.mPopup.setAnimationStyle(R.style.Animation_TypingFilter);
        } else {
            this.mPopup.setAnimationStyle(R.style.Animation_TypingFilterRestore);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public EditText getTextFilterInput() {
        if (this.mTextFilter == null) {
            EditText editText = (EditText) LayoutInflater.from(getContext()).inflate(R.layout.typing_filter, (ViewGroup) null);
            this.mTextFilter = editText;
            editText.setRawInputType(177);
            this.mTextFilter.setImeOptions(268435456);
            this.mTextFilter.addTextChangedListener(this);
        }
        return this.mTextFilter;
    }

    public void clearTextFilter() {
        if (this.mFiltered) {
            getTextFilterInput().setText("");
            this.mFiltered = false;
            PopupWindow popupWindow = this.mPopup;
            if (popupWindow == null || !popupWindow.isShowing()) {
                return;
            }
            dismissPopup();
        }
    }

    public boolean hasTextFilter() {
        return this.mFiltered;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        PopupWindow popupWindow;
        if (isShown()) {
            if (!this.mFiltered || (popupWindow = this.mPopup) == null || popupWindow.isShowing() || this.mPopupHidden) {
                return;
            }
            showPopup();
            return;
        }
        PopupWindow popupWindow2 = this.mPopup;
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            return;
        }
        dismissPopup();
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (isTextFilterEnabled()) {
            createTextFilter(true);
            int length = charSequence.length();
            boolean zIsShowing = this.mPopup.isShowing();
            if (!zIsShowing && length > 0) {
                showPopup();
                this.mFiltered = true;
            } else if (zIsShowing && length == 0) {
                dismissPopup();
                this.mFiltered = false;
            }
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter instanceof Filterable) {
                Filter filter = ((Filterable) listAdapter).getFilter();
                if (filter != null) {
                    filter.filter(charSequence, this);
                    return;
                }
                throw new IllegalStateException("You cannot call onTextChanged with a non filterable adapter");
            }
        }
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int i) {
        if (this.mSelectedPosition >= 0 || i <= 0) {
            return;
        }
        this.mResurrectToPosition = -1;
        resurrectSelection();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2, 0);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void setTranscriptMode(int i) {
        this.mTranscriptMode = i;
    }

    public int getTranscriptMode() {
        return this.mTranscriptMode;
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mCacheColorHint;
    }

    public void setCacheColorHint(int i) {
        if (i != this.mCacheColorHint) {
            this.mCacheColorHint = i;
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                getChildAt(i2).setDrawingCacheBackgroundColor(i);
            }
            this.mRecycler.setCacheColorHint(i);
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getCacheColorHint() {
        return this.mCacheColorHint;
    }

    public void reclaimViews(List<View> list) {
        int childCount = getChildCount();
        RecyclerListener recyclerListener = this.mRecycler.mRecyclerListener;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams != null && this.mRecycler.shouldRecycleViewType(layoutParams.viewType)) {
                list.add(childAt);
                childAt.setAccessibilityDelegate(null);
                if (recyclerListener != null) {
                    recyclerListener.onMovedToScrapHeap(childAt);
                }
            }
        }
        this.mRecycler.reclaimScrapViews(list);
        removeAllViewsInLayout();
    }

    private void finishGlows() {
        EdgeEffect edgeEffect = this.mEdgeGlowTop;
        if (edgeEffect != null) {
            edgeEffect.finish();
            this.mEdgeGlowBottom.finish();
        }
    }

    public void setRemoteViewsAdapter(Intent intent) {
        setRemoteViewsAdapter(intent, false);
    }

    public Runnable setRemoteViewsAdapterAsync(Intent intent) {
        return new RemoteViewsAdapter.AsyncRemoteAdapterAction(this, intent);
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void setRemoteViewsAdapter(Intent intent, boolean z) {
        if (this.mRemoteAdapter == null || !new Intent.FilterComparison(intent).equals(new Intent.FilterComparison(this.mRemoteAdapter.getRemoteViewsServiceIntent()))) {
            this.mDeferNotifyDataSetChanged = false;
            RemoteViewsAdapter remoteViewsAdapter = new RemoteViewsAdapter(getContext(), intent, this, z);
            this.mRemoteAdapter = remoteViewsAdapter;
            if (remoteViewsAdapter.isDataReady()) {
                setAdapter((ListAdapter) this.mRemoteAdapter);
            }
        }
    }

    public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler onClickHandler) {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.setRemoteViewsOnClickHandler(onClickHandler);
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void deferNotifyDataSetChanged() {
        this.mDeferNotifyDataSetChanged = true;
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public boolean onRemoteAdapterConnected() {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter == this.mAdapter) {
            if (remoteViewsAdapter == null) {
                return false;
            }
            remoteViewsAdapter.superNotifyDataSetChanged();
            return true;
        }
        setAdapter((ListAdapter) remoteViewsAdapter);
        if (this.mDeferNotifyDataSetChanged) {
            this.mRemoteAdapter.notifyDataSetChanged();
            this.mDeferNotifyDataSetChanged = false;
        }
        return false;
    }

    void setVisibleRangeHint(int i, int i2) {
        RemoteViewsAdapter remoteViewsAdapter = this.mRemoteAdapter;
        if (remoteViewsAdapter != null) {
            remoteViewsAdapter.setVisibleRangeHint(i, i2);
        }
    }

    public void setRecyclerListener(RecyclerListener recyclerListener) {
        this.mRecycler.mRecyclerListener = recyclerListener;
    }

    class AdapterDataSetObserver extends AdapterView<ListAdapter>.AdapterDataSetObserver {
        AdapterDataSetObserver() {
            super();
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onChanged() {
            super.onChanged();
            if (AbsListView.this.mFastScroll != null) {
                AbsListView.this.mFastScroll.onSectionsChanged();
            }
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onInvalidated() {
            super.onInvalidated();
            if (AbsListView.this.mFastScroll != null) {
                AbsListView.this.mFastScroll.onSectionsChanged();
            }
        }
    }

    class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        MultiChoiceModeWrapper() {
        }

        public void setWrapped(MultiChoiceModeListener multiChoiceModeListener) {
            this.mWrapped = multiChoiceModeListener;
        }

        public boolean hasWrappedCallback() {
            return this.mWrapped != null;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            if (!this.mWrapped.onCreateActionMode(actionMode, menu)) {
                return false;
            }
            AbsListView.this.setLongClickable(false);
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            AbsListView.this.mChoiceActionMode = null;
            AbsListView.this.clearChoices();
            AbsListView.this.mDataChanged = true;
            AbsListView.this.rememberSyncState();
            AbsListView.this.requestLayout();
            AbsListView.this.setLongClickable(true);
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z) {
            this.mWrapped.onItemCheckedStateChanged(actionMode, i, j, z);
            if (AbsListView.this.getCheckedItemCount() == 0) {
                actionMode.finish();
            }
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
        boolean forceAdd;
        boolean isEnabled;
        long itemId;

        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST)
        boolean recycledHeaderFooter;
        int scrappedFromPosition;

        @ViewDebug.ExportedProperty(category = Slice.HINT_LIST, mapping = {@ViewDebug.IntToString(from = -1, to = "ITEM_VIEW_TYPE_IGNORE"), @ViewDebug.IntToString(from = -2, to = "ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
        int viewType;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.itemId = -1L;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.itemId = -1L;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2);
            this.itemId = -1L;
            this.viewType = i3;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.itemId = -1L;
        }

        @Override // android.view.ViewGroup.LayoutParams
        protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
            super.encodeProperties(viewHierarchyEncoder);
            viewHierarchyEncoder.addProperty("list:viewType", this.viewType);
            viewHierarchyEncoder.addProperty("list:recycledHeaderFooter", this.recycledHeaderFooter);
            viewHierarchyEncoder.addProperty("list:forceAdd", this.forceAdd);
            viewHierarchyEncoder.addProperty("list:isEnabled", this.isEnabled);
        }
    }

    class RecycleBin {
        private View[] mActiveViews = new View[0];
        private ArrayList<View> mCurrentScrap;
        private int mFirstActivePosition;
        private RecyclerListener mRecyclerListener;
        private ArrayList<View>[] mScrapViews;
        private ArrayList<View> mSkippedScrap;
        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;
        private int mViewTypeCount;

        public boolean shouldRecycleViewType(int i) {
            return i >= 0;
        }

        RecycleBin() {
        }

        public void setViewTypeCount(int i) {
            if (i < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            ArrayList<View>[] arrayListArr = new ArrayList[i];
            for (int i2 = 0; i2 < i; i2++) {
                arrayListArr[i2] = new ArrayList<>();
            }
            this.mViewTypeCount = i;
            this.mCurrentScrap = arrayListArr[0];
            this.mScrapViews = arrayListArr;
        }

        public void markChildrenDirty() {
            int i = this.mViewTypeCount;
            if (i == 1) {
                ArrayList<View> arrayList = this.mCurrentScrap;
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    arrayList.get(i2).forceLayout();
                }
            } else {
                for (int i3 = 0; i3 < i; i3++) {
                    ArrayList<View> arrayList2 = this.mScrapViews[i3];
                    int size2 = arrayList2.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        arrayList2.get(i4).forceLayout();
                    }
                }
            }
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray != null) {
                int size3 = sparseArray.size();
                for (int i5 = 0; i5 < size3; i5++) {
                    this.mTransientStateViews.valueAt(i5).forceLayout();
                }
            }
            LongSparseArray<View> longSparseArray = this.mTransientStateViewsById;
            if (longSparseArray != null) {
                int size4 = longSparseArray.size();
                for (int i6 = 0; i6 < size4; i6++) {
                    this.mTransientStateViewsById.valueAt(i6).forceLayout();
                }
            }
        }

        void clear() {
            int i = this.mViewTypeCount;
            if (i == 1) {
                clearScrap(this.mCurrentScrap);
            } else {
                for (int i2 = 0; i2 < i; i2++) {
                    clearScrap(this.mScrapViews[i2]);
                }
            }
            clearTransientStateViews();
        }

        void fillActiveViews(int i, int i2) {
            if (this.mActiveViews.length < i) {
                this.mActiveViews = new View[i];
            }
            this.mFirstActivePosition = i2;
            View[] viewArr = this.mActiveViews;
            for (int i3 = 0; i3 < i; i3++) {
                View childAt = AbsListView.this.getChildAt(i3);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams != null && layoutParams.viewType != -2) {
                    viewArr[i3] = childAt;
                    layoutParams.scrappedFromPosition = i2 + i3;
                }
            }
        }

        View getActiveView(int i) {
            int i2 = i - this.mFirstActivePosition;
            View[] viewArr = this.mActiveViews;
            if (i2 < 0 || i2 >= viewArr.length) {
                return null;
            }
            View view = viewArr[i2];
            viewArr[i2] = null;
            return view;
        }

        View getTransientStateView(int i) {
            int iIndexOfKey;
            if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds && this.mTransientStateViewsById != null) {
                long itemId = AbsListView.this.mAdapter.getItemId(i);
                View view = this.mTransientStateViewsById.get(itemId);
                this.mTransientStateViewsById.remove(itemId);
                return view;
            }
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray == null || (iIndexOfKey = sparseArray.indexOfKey(i)) < 0) {
                return null;
            }
            View viewValueAt = this.mTransientStateViews.valueAt(iIndexOfKey);
            this.mTransientStateViews.removeAt(iIndexOfKey);
            return viewValueAt;
        }

        void clearTransientStateViews() {
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray != null) {
                int size = sparseArray.size();
                for (int i = 0; i < size; i++) {
                    removeDetachedView(sparseArray.valueAt(i), false);
                }
                sparseArray.clear();
            }
            LongSparseArray<View> longSparseArray = this.mTransientStateViewsById;
            if (longSparseArray != null) {
                int size2 = longSparseArray.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    removeDetachedView(longSparseArray.valueAt(i2), false);
                }
                longSparseArray.clear();
            }
        }

        View getScrapView(int i) {
            int itemViewType = AbsListView.this.mAdapter.getItemViewType(i);
            if (itemViewType < 0) {
                return null;
            }
            if (this.mViewTypeCount == 1) {
                return retrieveFromScrap(this.mCurrentScrap, i);
            }
            ArrayList<View>[] arrayListArr = this.mScrapViews;
            if (itemViewType < arrayListArr.length) {
                return retrieveFromScrap(arrayListArr[itemViewType], i);
            }
            return null;
        }

        void addScrapView(View view, int i) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.scrappedFromPosition = i;
            int i2 = layoutParams.viewType;
            if (!shouldRecycleViewType(i2)) {
                if (i2 != -2) {
                    getSkippedScrap().add(view);
                    return;
                }
                return;
            }
            view.dispatchStartTemporaryDetach();
            AbsListView.this.notifyViewAccessibilityStateChangedIfNeeded(1);
            if (view.hasTransientState()) {
                if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                    if (this.mTransientStateViewsById == null) {
                        this.mTransientStateViewsById = new LongSparseArray<>();
                    }
                    this.mTransientStateViewsById.put(layoutParams.itemId, view);
                    return;
                } else if (!AbsListView.this.mDataChanged) {
                    if (this.mTransientStateViews == null) {
                        this.mTransientStateViews = new SparseArray<>();
                    }
                    this.mTransientStateViews.put(i, view);
                    return;
                } else {
                    clearScrapForRebind(view);
                    getSkippedScrap().add(view);
                    return;
                }
            }
            clearScrapForRebind(view);
            if (this.mViewTypeCount == 1) {
                this.mCurrentScrap.add(view);
            } else {
                this.mScrapViews[i2].add(view);
            }
            RecyclerListener recyclerListener = this.mRecyclerListener;
            if (recyclerListener != null) {
                recyclerListener.onMovedToScrapHeap(view);
            }
        }

        private ArrayList<View> getSkippedScrap() {
            if (this.mSkippedScrap == null) {
                this.mSkippedScrap = new ArrayList<>();
            }
            return this.mSkippedScrap;
        }

        void removeSkippedScrap() {
            ArrayList<View> arrayList = this.mSkippedScrap;
            if (arrayList == null) {
                return;
            }
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                removeDetachedView(this.mSkippedScrap.get(i), false);
            }
            this.mSkippedScrap.clear();
        }

        void scrapActiveViews() {
            View[] viewArr = this.mActiveViews;
            boolean z = this.mRecyclerListener != null;
            boolean z2 = this.mViewTypeCount > 1;
            ArrayList<View> arrayList = this.mCurrentScrap;
            for (int length = viewArr.length - 1; length >= 0; length--) {
                View view = viewArr[length];
                if (view != null) {
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    int i = layoutParams.viewType;
                    viewArr[length] = null;
                    if (view.hasTransientState()) {
                        view.dispatchStartTemporaryDetach();
                        if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                            if (this.mTransientStateViewsById == null) {
                                this.mTransientStateViewsById = new LongSparseArray<>();
                            }
                            this.mTransientStateViewsById.put(AbsListView.this.mAdapter.getItemId(this.mFirstActivePosition + length), view);
                        } else if (!AbsListView.this.mDataChanged) {
                            if (this.mTransientStateViews == null) {
                                this.mTransientStateViews = new SparseArray<>();
                            }
                            this.mTransientStateViews.put(this.mFirstActivePosition + length, view);
                        } else if (i != -2) {
                            removeDetachedView(view, false);
                        }
                    } else if (shouldRecycleViewType(i)) {
                        if (z2) {
                            arrayList = this.mScrapViews[i];
                        }
                        layoutParams.scrappedFromPosition = this.mFirstActivePosition + length;
                        removeDetachedView(view, false);
                        arrayList.add(view);
                        if (z) {
                            this.mRecyclerListener.onMovedToScrapHeap(view);
                        }
                    } else if (i != -2) {
                        removeDetachedView(view, false);
                    }
                }
            }
            pruneScrapViews();
        }

        void fullyDetachScrapViews() {
            int i = this.mViewTypeCount;
            ArrayList<View>[] arrayListArr = this.mScrapViews;
            for (int i2 = 0; i2 < i; i2++) {
                ArrayList<View> arrayList = arrayListArr[i2];
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    View view = arrayList.get(size);
                    if (view.isTemporarilyDetached()) {
                        removeDetachedView(view, false);
                    }
                }
            }
        }

        private void pruneScrapViews() {
            int length = this.mActiveViews.length;
            int i = this.mViewTypeCount;
            ArrayList<View>[] arrayListArr = this.mScrapViews;
            for (int i2 = 0; i2 < i; i2++) {
                ArrayList<View> arrayList = arrayListArr[i2];
                int size = arrayList.size();
                while (size > length) {
                    size--;
                    arrayList.remove(size);
                }
            }
            SparseArray<View> sparseArray = this.mTransientStateViews;
            if (sparseArray != null) {
                int i3 = 0;
                while (i3 < sparseArray.size()) {
                    View viewValueAt = sparseArray.valueAt(i3);
                    if (!viewValueAt.hasTransientState()) {
                        removeDetachedView(viewValueAt, false);
                        sparseArray.removeAt(i3);
                        i3--;
                    }
                    i3++;
                }
            }
            LongSparseArray<View> longSparseArray = this.mTransientStateViewsById;
            if (longSparseArray != null) {
                int i4 = 0;
                while (i4 < longSparseArray.size()) {
                    View viewValueAt2 = longSparseArray.valueAt(i4);
                    if (!viewValueAt2.hasTransientState()) {
                        removeDetachedView(viewValueAt2, false);
                        longSparseArray.removeAt(i4);
                        i4--;
                    }
                    i4++;
                }
            }
        }

        void reclaimScrapViews(List<View> list) {
            int i = this.mViewTypeCount;
            if (i == 1) {
                list.addAll(this.mCurrentScrap);
                return;
            }
            ArrayList<View>[] arrayListArr = this.mScrapViews;
            for (int i2 = 0; i2 < i; i2++) {
                list.addAll(arrayListArr[i2]);
            }
        }

        void setCacheColorHint(int i) {
            int i2 = this.mViewTypeCount;
            if (i2 == 1) {
                ArrayList<View> arrayList = this.mCurrentScrap;
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    arrayList.get(i3).setDrawingCacheBackgroundColor(i);
                }
            } else {
                for (int i4 = 0; i4 < i2; i4++) {
                    ArrayList<View> arrayList2 = this.mScrapViews[i4];
                    int size2 = arrayList2.size();
                    for (int i5 = 0; i5 < size2; i5++) {
                        arrayList2.get(i5).setDrawingCacheBackgroundColor(i);
                    }
                }
            }
            for (View view : this.mActiveViews) {
                if (view != null) {
                    view.setDrawingCacheBackgroundColor(i);
                }
            }
        }

        private View retrieveFromScrap(ArrayList<View> arrayList, int i) {
            int size = arrayList.size();
            if (size <= 0) {
                return null;
            }
            int i2 = size - 1;
            for (int i3 = i2; i3 >= 0; i3--) {
                LayoutParams layoutParams = (LayoutParams) arrayList.get(i3).getLayoutParams();
                if (AbsListView.this.mAdapterHasStableIds) {
                    if (AbsListView.this.mAdapter.getItemId(i) == layoutParams.itemId) {
                        return arrayList.remove(i3);
                    }
                } else if (layoutParams.scrappedFromPosition == i) {
                    View viewRemove = arrayList.remove(i3);
                    clearScrapForRebind(viewRemove);
                    return viewRemove;
                }
            }
            View viewRemove2 = arrayList.remove(i2);
            clearScrapForRebind(viewRemove2);
            return viewRemove2;
        }

        private void clearScrap(ArrayList<View> arrayList) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                removeDetachedView(arrayList.remove((size - 1) - i), false);
            }
        }

        private void clearScrapForRebind(View view) {
            view.clearAccessibilityFocus();
            view.setAccessibilityDelegate(null);
        }

        private void removeDetachedView(View view, boolean z) {
            view.setAccessibilityDelegate(null);
            AbsListView.this.removeDetachedView(view, z);
        }
    }

    int getHeightForPosition(int i) {
        int firstVisiblePosition = getFirstVisiblePosition();
        int childCount = getChildCount();
        int i2 = i - firstVisiblePosition;
        if (i2 >= 0 && i2 < childCount) {
            return getChildAt(i2).getHeight();
        }
        View viewObtainView = obtainView(i, this.mIsScrap);
        viewObtainView.measure(this.mWidthMeasureSpec, 0);
        int measuredHeight = viewObtainView.getMeasuredHeight();
        this.mRecycler.addScrapView(viewObtainView, i);
        return measuredHeight;
    }

    public void setSelectionFromTop(int i, int i2) {
        if (this.mAdapter == null) {
            return;
        }
        if (!isInTouchMode()) {
            i = lookForSelectablePosition(i, true);
            if (i >= 0) {
                setNextSelectedPositionInt(i);
            }
        } else {
            this.mResurrectToPosition = i;
        }
        if (i >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mListPadding.top + i2;
            if (this.mNeedSync) {
                this.mSyncPosition = i;
                this.mSyncRowId = this.mAdapter.getItemId(i);
            }
            AbsPositionScroller absPositionScroller = this.mPositionScroller;
            if (absPositionScroller != null) {
                absPositionScroller.stop();
            }
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("drawing:cacheColorHint", getCacheColorHint());
        viewHierarchyEncoder.addProperty("list:fastScrollEnabled", isFastScrollEnabled());
        viewHierarchyEncoder.addProperty("list:scrollingCacheEnabled", isScrollingCacheEnabled());
        viewHierarchyEncoder.addProperty("list:smoothScrollbarEnabled", isSmoothScrollbarEnabled());
        viewHierarchyEncoder.addProperty("list:stackFromBottom", isStackFromBottom());
        viewHierarchyEncoder.addProperty("list:textFilterEnabled", isTextFilterEnabled());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            viewHierarchyEncoder.addPropertyKey("selectedView");
            selectedView.encode(viewHierarchyEncoder);
        }
    }

    static abstract class AbsPositionScroller {
        public abstract void start(int i);

        public abstract void start(int i, int i2);

        public abstract void startWithOffset(int i, int i2);

        public abstract void startWithOffset(int i, int i2, int i3);

        public abstract void stop();

        AbsPositionScroller() {
        }
    }

    class PositionScroller extends AbsPositionScroller implements Runnable {
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_OFFSET = 5;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_UP_POS = 2;
        private static final int SCROLL_DURATION = 200;
        private int mBoundPos;
        private final int mExtraScroll;
        private int mLastSeenPos;
        private int mMode;
        private int mOffsetFromTop;
        private int mScrollDuration;
        private int mTargetPos;

        PositionScroller() {
            this.mExtraScroll = ViewConfiguration.get(AbsListView.this.mContext).getScaledFadingEdgeLength();
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void start(final int i) {
            int i2;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(i);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int i3 = AbsListView.this.mFirstPosition;
            int i4 = (childCount + i3) - 1;
            int iMax = Math.max(0, Math.min(AbsListView.this.getCount() - 1, i));
            if (iMax < i3) {
                i2 = (i3 - iMax) + 1;
                this.mMode = 2;
            } else if (iMax > i4) {
                i2 = (iMax - i4) + 1;
                this.mMode = 1;
            } else {
                scrollToVisible(iMax, -1, 200);
                return;
            }
            if (i2 > 0) {
                this.mScrollDuration = 200 / i2;
            } else {
                this.mScrollDuration = 200;
            }
            this.mTargetPos = iMax;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void start(final int i, final int i2) {
            int i3;
            int i4;
            stop();
            if (i2 == -1) {
                start(i);
                return;
            }
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(i, i2);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int i5 = AbsListView.this.mFirstPosition;
            int i6 = (childCount + i5) - 1;
            int iMax = Math.max(0, Math.min(AbsListView.this.getCount() - 1, i));
            if (iMax < i5) {
                int i7 = i6 - i2;
                if (i7 < 1) {
                    return;
                }
                i4 = (i5 - iMax) + 1;
                i3 = i7 - 1;
                if (i3 < i4) {
                    this.mMode = 4;
                    i4 = i3;
                } else {
                    this.mMode = 2;
                }
            } else {
                if (iMax <= i6) {
                    scrollToVisible(iMax, i2, 200);
                    return;
                }
                int i8 = i2 - i5;
                if (i8 < 1) {
                    return;
                }
                i3 = (iMax - i6) + 1;
                i4 = i8 - 1;
                if (i4 < i3) {
                    this.mMode = 3;
                } else {
                    this.mMode = 1;
                    i4 = i3;
                }
            }
            if (i4 > 0) {
                this.mScrollDuration = 200 / i4;
            } else {
                this.mScrollDuration = 200;
            }
            this.mTargetPos = iMax;
            this.mBoundPos = i2;
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void startWithOffset(int i, int i2) {
            startWithOffset(i, i2, 200);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void startWithOffset(final int i, final int i2, final int i3) {
            int i4;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.startWithOffset(i, i2, i3);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int paddingTop = i2 + AbsListView.this.getPaddingTop();
            this.mTargetPos = Math.max(0, Math.min(AbsListView.this.getCount() - 1, i));
            this.mOffsetFromTop = paddingTop;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            this.mMode = 5;
            int i5 = AbsListView.this.mFirstPosition;
            int i6 = (i5 + childCount) - 1;
            int i7 = this.mTargetPos;
            if (i7 < i5) {
                i4 = i5 - i7;
            } else {
                if (i7 <= i6) {
                    AbsListView.this.smoothScrollBy(AbsListView.this.getChildAt(i7 - i5).getTop() - paddingTop, i3, true, false);
                    return;
                }
                i4 = i7 - i6;
            }
            float f = i4 / childCount;
            if (f >= 1.0f) {
                i3 = (int) (i3 / f);
            }
            this.mScrollDuration = i3;
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        private void scrollToVisible(int i, int i2, int i3) {
            int i4 = AbsListView.this.mFirstPosition;
            int childCount = (AbsListView.this.getChildCount() + i4) - 1;
            int i5 = AbsListView.this.mListPadding.top;
            int height = AbsListView.this.getHeight() - AbsListView.this.mListPadding.bottom;
            if (i < i4 || i > childCount) {
                Log.w(AbsListView.TAG, "scrollToVisible called with targetPos " + i + " not visible [" + i4 + ", " + childCount + "]");
            }
            if (i2 < i4 || i2 > childCount) {
                i2 = -1;
            }
            View childAt = AbsListView.this.getChildAt(i - i4);
            int top = childAt.getTop();
            int bottom = childAt.getBottom();
            int iMin = bottom > height ? bottom - height : 0;
            if (top < i5) {
                iMin = top - i5;
            }
            if (iMin == 0) {
                return;
            }
            if (i2 >= 0) {
                View childAt2 = AbsListView.this.getChildAt(i2 - i4);
                int top2 = childAt2.getTop();
                int bottom2 = childAt2.getBottom();
                int iAbs = Math.abs(iMin);
                if (iMin < 0 && bottom2 + iAbs > height) {
                    iMin = Math.max(0, bottom2 - height);
                } else if (iMin > 0 && top2 - iAbs < i5) {
                    iMin = Math.min(0, top2 - i5);
                }
            }
            AbsListView.this.smoothScrollBy(iMin, i3);
        }

        @Override // android.widget.AbsListView.AbsPositionScroller
        public void stop() {
            AbsListView.this.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            int height = AbsListView.this.getHeight();
            int i = AbsListView.this.mFirstPosition;
            int i2 = this.mMode;
            if (i2 == 1) {
                int childCount = AbsListView.this.getChildCount() - 1;
                int i3 = i + childCount;
                if (childCount < 0) {
                    return;
                }
                if (i3 == this.mLastSeenPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                View childAt = AbsListView.this.getChildAt(childCount);
                AbsListView.this.smoothScrollBy((childAt.getHeight() - (height - childAt.getTop())) + (i3 < AbsListView.this.mItemCount - 1 ? Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll) : AbsListView.this.mListPadding.bottom), this.mScrollDuration, true, i3 < this.mTargetPos);
                this.mLastSeenPos = i3;
                if (i3 < this.mTargetPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                return;
            }
            if (i2 == 2) {
                if (i == this.mLastSeenPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                View childAt2 = AbsListView.this.getChildAt(0);
                if (childAt2 == null) {
                    return;
                }
                AbsListView.this.smoothScrollBy(childAt2.getTop() - (i > 0 ? Math.max(this.mExtraScroll, AbsListView.this.mListPadding.top) : AbsListView.this.mListPadding.top), this.mScrollDuration, true, i > this.mTargetPos);
                this.mLastSeenPos = i;
                if (i > this.mTargetPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                return;
            }
            if (i2 == 3) {
                int childCount2 = AbsListView.this.getChildCount();
                if (i == this.mBoundPos || childCount2 <= 1 || childCount2 + i >= AbsListView.this.mItemCount) {
                    AbsListView.this.reportScrollStateChange(0);
                    return;
                }
                int i4 = i + 1;
                if (i4 == this.mLastSeenPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                View childAt3 = AbsListView.this.getChildAt(1);
                int height2 = childAt3.getHeight();
                int top = childAt3.getTop();
                int iMax = Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll);
                if (i4 < this.mBoundPos) {
                    AbsListView.this.smoothScrollBy(Math.max(0, (height2 + top) - iMax), this.mScrollDuration, true, true);
                    this.mLastSeenPos = i4;
                    AbsListView.this.postOnAnimation(this);
                    return;
                } else if (top > iMax) {
                    AbsListView.this.smoothScrollBy(top - iMax, this.mScrollDuration, true, false);
                    return;
                } else {
                    AbsListView.this.reportScrollStateChange(0);
                    return;
                }
            }
            if (i2 == 4) {
                int childCount3 = AbsListView.this.getChildCount() - 2;
                if (childCount3 < 0) {
                    return;
                }
                int i5 = i + childCount3;
                if (i5 == this.mLastSeenPos) {
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                View childAt4 = AbsListView.this.getChildAt(childCount3);
                int height3 = childAt4.getHeight();
                int top2 = childAt4.getTop();
                int i6 = height - top2;
                int iMax2 = Math.max(AbsListView.this.mListPadding.top, this.mExtraScroll);
                this.mLastSeenPos = i5;
                if (i5 > this.mBoundPos) {
                    AbsListView.this.smoothScrollBy(-(i6 - iMax2), this.mScrollDuration, true, true);
                    AbsListView.this.postOnAnimation(this);
                    return;
                }
                int i7 = height - iMax2;
                int i8 = top2 + height3;
                if (i7 > i8) {
                    AbsListView.this.smoothScrollBy(-(i7 - i8), this.mScrollDuration, true, false);
                    return;
                } else {
                    AbsListView.this.reportScrollStateChange(0);
                    return;
                }
            }
            if (i2 != 5) {
                return;
            }
            if (this.mLastSeenPos == i) {
                AbsListView.this.postOnAnimation(this);
                return;
            }
            this.mLastSeenPos = i;
            int childCount4 = AbsListView.this.getChildCount();
            int i9 = this.mTargetPos;
            int i10 = (i + childCount4) - 1;
            View childAt5 = AbsListView.this.getChildAt(0);
            int height4 = childAt5.getHeight();
            View childAt6 = AbsListView.this.getChildAt(childCount4 - 1);
            int height5 = childAt6.getHeight();
            float f = height4;
            float f2 = 0.0f;
            float top3 = f == 0.0f ? 1.0f : (height4 + childAt5.getTop()) / f;
            float f3 = height5;
            float height6 = f3 == 0.0f ? 1.0f : ((height5 + AbsListView.this.getHeight()) - childAt6.getBottom()) / f3;
            if (i9 < i) {
                f2 = (i - i9) + (1.0f - top3) + 1.0f;
            } else if (i9 > i10) {
                f2 = (i9 - i10) + (1.0f - height6);
            }
            float fMin = Math.min(Math.abs(f2 / childCount4), 1.0f);
            if (i9 < i) {
                AbsListView.this.smoothScrollBy((int) ((-AbsListView.this.getHeight()) * fMin), (int) (this.mScrollDuration * fMin), true, true);
                AbsListView.this.postOnAnimation(this);
            } else if (i9 > i10) {
                AbsListView.this.smoothScrollBy((int) (AbsListView.this.getHeight() * fMin), (int) (this.mScrollDuration * fMin), true, true);
                AbsListView.this.postOnAnimation(this);
            } else {
                int top4 = AbsListView.this.getChildAt(i9 - i).getTop() - this.mOffsetFromTop;
                AbsListView.this.smoothScrollBy(top4, (int) (this.mScrollDuration * (Math.abs(top4) / AbsListView.this.getHeight())), true, false);
            }
        }
    }
}
