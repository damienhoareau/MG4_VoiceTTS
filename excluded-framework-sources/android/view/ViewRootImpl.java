package android.view;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.CarConfigManager;
import android.app.ResourcesManager;
import android.bluetooth.BluetoothClass;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.TtmlUtils;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongArray;
import android.util.MergedConfiguration;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import com.android.internal.R;
import com.android.internal.os.IResultReceiver;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.PhoneFallbackEventHandler;
import com.android.internal.util.Preconditions;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import com.android.internal.view.SurfaceCallbackHelper;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes2.dex */
public final class ViewRootImpl implements ViewParent, View.AttachInfo.Callbacks, ThreadedRenderer.DrawCallbacks {
    private static final boolean DBG = false;
    private static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_DIALOG = false;
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_FPS = false;
    private static final boolean DEBUG_IMF = false;
    private static final boolean DEBUG_INPUT_RESIZE = false;
    private static final boolean DEBUG_INPUT_STAGES = false;
    private static final boolean DEBUG_KEEP_SCREEN_ON = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_ORIENTATION = false;
    private static final boolean DEBUG_TRACKBALL = false;
    private static final boolean LOCAL_LOGV = false;
    private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
    static final int MAX_TRACKBALL_DELAY = 250;
    private static final int MSG_CHECK_FOCUS = 13;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
    private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
    private static final int MSG_DIE = 3;
    private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
    private static final int MSG_DISPATCH_DRAG_EVENT = 15;
    private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
    private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
    private static final int MSG_DISPATCH_INPUT_EVENT = 7;
    private static final int MSG_DISPATCH_KEY_FROM_AUTOFILL = 12;
    private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
    private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
    private static final int MSG_DISPATCH_WINDOW_SHOWN = 25;
    private static final int MSG_DRAW_FINISHED = 29;
    private static final int MSG_INVALIDATE = 1;
    private static final int MSG_INVALIDATE_RECT = 2;
    private static final int MSG_INVALIDATE_WORLD = 22;
    private static final int MSG_POINTER_CAPTURE_CHANGED = 28;
    private static final int MSG_PROCESS_INPUT_EVENTS = 19;
    private static final int MSG_REQUEST_KEYBOARD_SHORTCUTS = 26;
    private static final int MSG_RESIZED = 4;
    private static final int MSG_RESIZED_REPORT = 5;
    private static final int MSG_SYNTHESIZE_INPUT_EVENT = 24;
    private static final int MSG_UPDATE_CONFIGURATION = 18;
    private static final int MSG_UPDATE_POINTER_ICON = 27;
    private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
    private static final int MSG_WINDOW_MOVED = 23;
    private static final boolean MT_RENDERER_AVAILABLE = true;
    public static final String PROPERTY_EMULATOR_WIN_OUTSET_BOTTOM_PX = "ro.emu.win_outset_bottom_px";
    private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
    private static final String TAG = "ViewRootImpl";
    private static boolean sAlwaysAssignFocus;
    View mAccessibilityFocusedHost;
    AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
    AccessibilityInteractionController mAccessibilityInteractionController;
    final AccessibilityManager mAccessibilityManager;
    private ActivityConfigCallback mActivityConfigCallback;
    private boolean mActivityRelaunched;
    boolean mAdded;
    boolean mAddedTouchMode;
    private boolean mAppVisibilityChanged;
    boolean mApplyInsetsRequested;
    final View.AttachInfo mAttachInfo;
    AudioManager mAudioManager;
    final String mBasePackageName;
    private int mCanvasOffsetX;
    private int mCanvasOffsetY;
    Choreographer mChoreographer;
    int mClientWindowLayoutFlags;
    final ConsumeBatchedInputImmediatelyRunnable mConsumeBatchedInputImmediatelyRunnable;
    boolean mConsumeBatchedInputImmediatelyScheduled;
    boolean mConsumeBatchedInputScheduled;
    final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
    final Context mContext;
    int mCurScrollY;
    View mCurrentDragView;
    private final int mDensity;
    Rect mDirty;
    Display mDisplay;
    private final DisplayManager.DisplayListener mDisplayListener;
    final DisplayManager mDisplayManager;
    ClipDescription mDragDescription;
    private boolean mDragResizing;
    boolean mDrawingAllowed;
    int mDrawsNeededToReport;
    FallbackEventHandler mFallbackEventHandler;
    boolean mFirst;
    InputStage mFirstInputStage;
    InputStage mFirstPostImeInputStage;
    private boolean mForceNextConfigUpdate;
    boolean mForceNextWindowRelayout;
    private int mFpsNumFrames;
    boolean mFullRedrawNeeded;
    boolean mHadWindowFocus;
    final ViewRootHandler mHandler;
    int mHardwareXOffset;
    int mHardwareYOffset;
    boolean mHasHadWindowFocus;
    int mHeight;
    final HighContrastTextManager mHighContrastTextManager;
    InputChannel mInputChannel;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    WindowInputEventReceiver mInputEventReceiver;
    InputQueue mInputQueue;
    InputQueue.Callback mInputQueueCallback;
    final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
    private boolean mInvalidateRootRequested;
    public boolean mIsAnimating;
    boolean mIsCreating;
    boolean mIsDrawing;
    boolean mIsInTraversal;
    boolean mLastOverscanRequested;
    WeakReference<View> mLastScrolledFocus;
    int mLastSystemUiVisibility;
    int mLastTouchSource;
    boolean mLastWasImTarget;
    private WindowInsets mLastWindowInsets;
    boolean mLayoutRequested;
    volatile Object mLocalDragState;
    final WindowLeaked mLocation;
    boolean mLostWindowFocus;
    private boolean mNeedsRendererSetup;
    boolean mNewSurfaceNeeded;
    private ThreadedRenderer.FrameDrawingCallback mNextRtFrameCallback;
    private final int mNoncompatDensity;
    boolean mPendingAlwaysConsumeNavBar;
    int mPendingInputEventCount;
    QueuedInputEvent mPendingInputEventHead;
    QueuedInputEvent mPendingInputEventTail;
    private ArrayList<LayoutTransition> mPendingTransitions;
    boolean mPointerCapture;
    final Region mPreviousTransparentRegion;
    boolean mProcessInputEventsScheduled;
    private boolean mProfile;
    private boolean mProfileRendering;
    private QueuedInputEvent mQueuedInputEventPool;
    private int mQueuedInputEventPoolSize;
    private boolean mRemoved;
    private Choreographer.FrameCallback mRenderProfiler;
    private boolean mRenderProfilingEnabled;
    boolean mReportNextDraw;
    private int mResizeMode;
    boolean mScrollMayChange;
    int mScrollY;
    Scroller mScroller;
    SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
    int mSeq;
    int mSoftInputMode;
    BaseSurfaceHolder mSurfaceHolder;
    SurfaceHolder.Callback2 mSurfaceHolderCallback;
    InputStage mSyntheticInputStage;
    private String mTag;
    final int mTargetSdkVersion;
    HashSet<View> mTempHashSet;
    final Rect mTempRect;
    final Thread mThread;
    CompatibilityInfo.Translator mTranslator;
    final Region mTransparentRegion;
    int mTraversalBarrier;
    final TraversalRunnable mTraversalRunnable;
    public boolean mTraversalScheduled;
    boolean mUnbufferedInputDispatch;
    boolean mUpcomingInTouchMode;
    boolean mUpcomingWindowFocus;
    private boolean mUseMTRenderer;
    View mView;
    final ViewConfiguration mViewConfiguration;
    private int mViewLayoutDirectionInitial;
    int mViewVisibility;
    final Rect mVisRect;
    int mWidth;
    boolean mWillDrawSoon;
    final Rect mWinFrame;
    final W mWindow;
    CountDownLatch mWindowDrawCountDown;
    boolean mWindowFocusChanged;
    final IWindowSession mWindowSession;
    private final ArrayList<WindowStoppedCallback> mWindowStoppedCallbacks;
    static final ThreadLocal<HandlerActionQueue> sRunQueues = new ThreadLocal<>();
    static final ArrayList<Runnable> sFirstDrawHandlers = new ArrayList<>();
    static boolean sFirstDrawComplete = false;
    private static final ArrayList<ConfigChangedCallback> sConfigCallbacks = new ArrayList<>();
    private static boolean sCompatibilityDone = false;
    static final Interpolator mResizeInterpolator = new AccelerateDecelerateInterpolator();
    final ArrayList<WindowCallbacks> mWindowCallbacks = new ArrayList<>();
    final int[] mTmpLocation = new int[2];
    final TypedValue mTmpValue = new TypedValue();
    public final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();
    boolean mAppVisible = true;
    private boolean mForceDecorViewVisibility = false;
    int mOrigWindowType = -1;
    boolean mStopped = false;
    boolean mIsAmbientMode = false;
    boolean mPausedForTransition = false;
    boolean mLastInCompatMode = false;
    String mPendingInputEventQueueLengthCounterName = "pq";
    private final UnhandledKeyManager mUnhandledKeyManager = new UnhandledKeyManager();
    boolean mWindowAttributesChanged = false;
    int mWindowAttributesChangesFlag = 0;
    public final Surface mSurface = new Surface();
    final Rect mPendingOverscanInsets = new Rect();
    final Rect mPendingVisibleInsets = new Rect();
    final Rect mPendingStableInsets = new Rect();
    final Rect mPendingContentInsets = new Rect();
    final Rect mPendingOutsets = new Rect();
    final Rect mPendingBackDropFrame = new Rect();
    final DisplayCutout.ParcelableWrapper mPendingDisplayCutout = new DisplayCutout.ParcelableWrapper(DisplayCutout.NO_CUTOUT);
    final ViewTreeObserver.InternalInsetsInfo mLastGivenInsets = new ViewTreeObserver.InternalInsetsInfo();
    final Rect mDispatchContentInsets = new Rect();
    final Rect mDispatchStableInsets = new Rect();
    DisplayCutout mDispatchDisplayCutout = DisplayCutout.NO_CUTOUT;
    private final Configuration mLastConfigurationFromResources = new Configuration();
    private final MergedConfiguration mLastReportedMergedConfiguration = new MergedConfiguration();
    private final MergedConfiguration mPendingMergedConfiguration = new MergedConfiguration();
    final PointF mDragPoint = new PointF();
    final PointF mLastTouchPoint = new PointF();
    private long mFpsStartTime = -1;
    private long mFpsPrevTime = -1;
    private int mPointerIconType = 1;
    private PointerIcon mCustomPointerIcon = null;
    final AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager = new AccessibilityInteractionConnectionManager();
    private boolean mInLayout = false;
    ArrayList<View> mLayoutRequesters = new ArrayList<>();
    boolean mHandlingLayoutInLayoutRequest = false;

    public interface ActivityConfigCallback {
        void onConfigurationChanged(Configuration configuration, int i);
    }

    public interface ConfigChangedCallback {
        void onConfigurationChanged(Configuration configuration);
    }

    interface WindowStoppedCallback {
        void windowStopped(boolean z);
    }

    @Override // android.view.ViewParent
    public void bringChildToFront(View view) {
    }

    @Override // android.view.ViewParent
    public boolean canResolveLayoutDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextAlignment() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public void childDrawableStateChanged(View view) {
    }

    @Override // android.view.ViewParent
    public void childHasTransientStateChanged(View view, boolean z) {
    }

    @Override // android.view.ViewParent
    public void createContextMenu(ContextMenu contextMenu) {
    }

    @Override // android.view.ViewParent
    public int getLayoutDirection() {
        return 0;
    }

    @Override // android.view.ViewParent
    public ViewParent getParent() {
        return null;
    }

    @Override // android.view.ViewParent
    public ViewParent getParentForAccessibility() {
        return null;
    }

    @Override // android.view.ViewParent
    public int getTextAlignment() {
        return 1;
    }

    @Override // android.view.ViewParent
    public int getTextDirection() {
        return 1;
    }

    @Override // android.view.ViewParent
    public boolean isLayoutDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextAlignmentResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean onNestedPreFling(View view, float f, float f2) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean onNestedPrePerformAccessibilityAction(View view, int i, Bundle bundle) {
        return false;
    }

    @Override // android.view.ViewParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
    }

    @Override // android.view.ViewParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
    }

    @Override // android.view.ViewParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
    }

    @Override // android.view.ViewParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return false;
    }

    @Override // android.view.ViewParent
    public void onStopNestedScroll(View view) {
    }

    @Override // android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View view) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View view, float f, float f2) {
        return false;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View view, ActionMode.Callback callback, int i) {
        return null;
    }

    static final class SystemUiVisibilityInfo {
        int globalVisibility;
        int localChanges;
        int localValue;
        int seq;

        SystemUiVisibilityInfo() {
        }
    }

    public ViewRootImpl(Context context, Display display) {
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mTag = TAG;
        this.mProfile = false;
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: android.view.ViewRootImpl.1
            private int toViewScreenState(int i) {
                return i == 1 ? 0 : 1;
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                int i2;
                int state;
                if (ViewRootImpl.this.mView == null || ViewRootImpl.this.mDisplay.getDisplayId() != i || (i2 = ViewRootImpl.this.mAttachInfo.mDisplayState) == (state = ViewRootImpl.this.mDisplay.getState())) {
                    return;
                }
                ViewRootImpl.this.mAttachInfo.mDisplayState = state;
                ViewRootImpl.this.pokeDrawLockIfNeeded();
                if (i2 != 0) {
                    int viewScreenState = toViewScreenState(i2);
                    int viewScreenState2 = toViewScreenState(state);
                    if (viewScreenState != viewScreenState2) {
                        ViewRootImpl.this.mView.dispatchScreenStateChanged(viewScreenState2);
                    }
                    if (i2 == 1) {
                        ViewRootImpl.this.mFullRedrawNeeded = true;
                        ViewRootImpl.this.scheduleTraversals();
                    }
                }
            }
        };
        this.mWindowStoppedCallbacks = new ArrayList<>();
        this.mDrawsNeededToReport = 0;
        this.mHandler = new ViewRootHandler();
        this.mTraversalRunnable = new TraversalRunnable();
        this.mConsumedBatchedInputRunnable = new ConsumeBatchedInputRunnable();
        this.mConsumeBatchedInputImmediatelyRunnable = new ConsumeBatchedInputImmediatelyRunnable();
        this.mInvalidateOnAnimationRunnable = new InvalidateOnAnimationRunnable();
        this.mContext = context;
        this.mWindowSession = WindowManagerGlobal.getWindowSession();
        this.mDisplay = display;
        this.mBasePackageName = context.getBasePackageName();
        this.mThread = Thread.currentThread();
        WindowLeaked windowLeaked = new WindowLeaked(null);
        this.mLocation = windowLeaked;
        windowLeaked.fillInStackTrace();
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDirty = new Rect();
        this.mTempRect = new Rect();
        this.mVisRect = new Rect();
        this.mWinFrame = new Rect();
        this.mWindow = new W(this);
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mViewVisibility = 8;
        this.mTransparentRegion = new Region();
        this.mPreviousTransparentRegion = new Region();
        this.mFirst = true;
        this.mAdded = false;
        this.mAttachInfo = new View.AttachInfo(this.mWindowSession, this.mWindow, display, this, this.mHandler, this, context);
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(context);
        this.mAccessibilityManager = accessibilityManager;
        accessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager, this.mHandler);
        HighContrastTextManager highContrastTextManager = new HighContrastTextManager();
        this.mHighContrastTextManager = highContrastTextManager;
        this.mAccessibilityManager.addHighTextContrastStateChangeListener(highContrastTextManager, this.mHandler);
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNoncompatDensity = context.getResources().getDisplayMetrics().noncompatDensityDpi;
        this.mFallbackEventHandler = new PhoneFallbackEventHandler(context);
        this.mChoreographer = Choreographer.getInstance();
        this.mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (!sCompatibilityDone) {
            sAlwaysAssignFocus = this.mTargetSdkVersion < 28;
            sCompatibilityDone = true;
        }
        loadSystemProperties();
    }

    public static void addFirstDrawHandler(Runnable runnable) {
        synchronized (sFirstDrawHandlers) {
            if (!sFirstDrawComplete) {
                sFirstDrawHandlers.add(runnable);
            }
        }
    }

    public static void addConfigCallback(ConfigChangedCallback configChangedCallback) {
        synchronized (sConfigCallbacks) {
            sConfigCallbacks.add(configChangedCallback);
        }
    }

    public void setActivityConfigCallback(ActivityConfigCallback activityConfigCallback) {
        this.mActivityConfigCallback = activityConfigCallback;
    }

    public void addWindowCallbacks(WindowCallbacks windowCallbacks) {
        synchronized (this.mWindowCallbacks) {
            this.mWindowCallbacks.add(windowCallbacks);
        }
    }

    public void removeWindowCallbacks(WindowCallbacks windowCallbacks) {
        synchronized (this.mWindowCallbacks) {
            this.mWindowCallbacks.remove(windowCallbacks);
        }
    }

    public void reportDrawFinish() {
        CountDownLatch countDownLatch = this.mWindowDrawCountDown;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void profile() {
        this.mProfile = true;
    }

    static boolean isInTouchMode() {
        IWindowSession iWindowSessionPeekWindowSession = WindowManagerGlobal.peekWindowSession();
        if (iWindowSessionPeekWindowSession == null) {
            return false;
        }
        try {
            return iWindowSessionPeekWindowSession.getInTouchMode();
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void notifyChildRebuilt() {
        if (this.mView instanceof RootViewSurfaceTaker) {
            SurfaceHolder.Callback2 callback2 = this.mSurfaceHolderCallback;
            if (callback2 != null) {
                this.mSurfaceHolder.removeCallback(callback2);
            }
            SurfaceHolder.Callback2 callback2WillYouTakeTheSurface = ((RootViewSurfaceTaker) this.mView).willYouTakeTheSurface();
            this.mSurfaceHolderCallback = callback2WillYouTakeTheSurface;
            if (callback2WillYouTakeTheSurface != null) {
                TakenSurfaceHolder takenSurfaceHolder = new TakenSurfaceHolder();
                this.mSurfaceHolder = takenSurfaceHolder;
                takenSurfaceHolder.setFormat(0);
                this.mSurfaceHolder.addCallback(this.mSurfaceHolderCallback);
            } else {
                this.mSurfaceHolder = null;
            }
            InputQueue.Callback callbackWillYouTakeTheInputQueue = ((RootViewSurfaceTaker) this.mView).willYouTakeTheInputQueue();
            this.mInputQueueCallback = callbackWillYouTakeTheInputQueue;
            if (callbackWillYouTakeTheInputQueue != null) {
                callbackWillYouTakeTheInputQueue.onInputQueueCreated(this.mInputQueue);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setView(View view, WindowManager.LayoutParams layoutParams, View view2) {
        boolean z;
        boolean z2;
        synchronized (this) {
            if (this.mView == null) {
                this.mView = view;
                this.mAttachInfo.mDisplayState = this.mDisplay.getState();
                this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
                this.mViewLayoutDirectionInitial = this.mView.getRawLayoutDirection();
                this.mFallbackEventHandler.setView(view);
                this.mWindowAttributes.copyFrom(layoutParams);
                if (this.mWindowAttributes.packageName == null) {
                    this.mWindowAttributes.packageName = this.mBasePackageName;
                }
                WindowManager.LayoutParams layoutParams2 = this.mWindowAttributes;
                setTag();
                this.mClientWindowLayoutFlags = layoutParams2.flags;
                setAccessibilityFocus(null, null);
                if (view instanceof RootViewSurfaceTaker) {
                    SurfaceHolder.Callback2 callback2WillYouTakeTheSurface = ((RootViewSurfaceTaker) view).willYouTakeTheSurface();
                    this.mSurfaceHolderCallback = callback2WillYouTakeTheSurface;
                    if (callback2WillYouTakeTheSurface != null) {
                        TakenSurfaceHolder takenSurfaceHolder = new TakenSurfaceHolder();
                        this.mSurfaceHolder = takenSurfaceHolder;
                        takenSurfaceHolder.setFormat(0);
                        this.mSurfaceHolder.addCallback(this.mSurfaceHolderCallback);
                    }
                }
                if (!layoutParams2.hasManualSurfaceInsets) {
                    layoutParams2.setSurfaceInsets(view, false, true);
                }
                CompatibilityInfo compatibilityInfo = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
                this.mTranslator = compatibilityInfo.getTranslator();
                if (this.mSurfaceHolder == null) {
                    enableHardwareAcceleration(layoutParams2);
                    boolean z3 = this.mAttachInfo.mThreadedRenderer != null;
                    if (this.mUseMTRenderer != z3) {
                        endDragResizing();
                        this.mUseMTRenderer = z3;
                    }
                }
                if (this.mTranslator != null) {
                    this.mSurface.setCompatibilityTranslator(this.mTranslator);
                    layoutParams2.backup();
                    this.mTranslator.translateWindowLayout(layoutParams2);
                    z = true;
                } else {
                    z = false;
                }
                if (!compatibilityInfo.supportsScreen()) {
                    layoutParams2.privateFlags |= 128;
                    this.mLastInCompatMode = true;
                }
                this.mSoftInputMode = layoutParams2.softInputMode;
                this.mWindowAttributesChanged = true;
                this.mWindowAttributesChangesFlag = -1;
                this.mAttachInfo.mRootView = view;
                this.mAttachInfo.mScalingRequired = this.mTranslator != null;
                this.mAttachInfo.mApplicationScale = this.mTranslator == null ? 1.0f : this.mTranslator.applicationScale;
                if (view2 != null) {
                    this.mAttachInfo.mPanelParentWindowToken = view2.getApplicationWindowToken();
                }
                this.mAdded = true;
                requestLayout();
                if ((this.mWindowAttributes.inputFeatures & 2) == 0) {
                    this.mInputChannel = new InputChannel();
                }
                this.mForceDecorViewVisibility = (this.mWindowAttributes.privateFlags & 16384) != 0;
                try {
                    try {
                        this.mOrigWindowType = this.mWindowAttributes.type;
                        this.mAttachInfo.mRecomputeGlobalAttributes = true;
                        collectViewAttributes();
                        try {
                            int iAddToDisplay = this.mWindowSession.addToDisplay(this.mWindow, this.mSeq, this.mWindowAttributes, getHostVisibility(), this.mDisplay.getDisplayId(), this.mWinFrame, this.mAttachInfo.mContentInsets, this.mAttachInfo.mStableInsets, this.mAttachInfo.mOutsets, this.mAttachInfo.mDisplayCutout, this.mInputChannel);
                            if (z) {
                                layoutParams2.restore();
                            }
                            if (this.mTranslator != null) {
                                this.mTranslator.translateRectInScreenToAppWindow(this.mAttachInfo.mContentInsets);
                            }
                            this.mPendingOverscanInsets.set(0, 0, 0, 0);
                            this.mPendingContentInsets.set(this.mAttachInfo.mContentInsets);
                            this.mPendingStableInsets.set(this.mAttachInfo.mStableInsets);
                            this.mPendingDisplayCutout.set(this.mAttachInfo.mDisplayCutout);
                            this.mPendingVisibleInsets.set(0, 0, 0, 0);
                            this.mAttachInfo.mAlwaysConsumeNavBar = (iAddToDisplay & 4) != 0;
                            this.mPendingAlwaysConsumeNavBar = this.mAttachInfo.mAlwaysConsumeNavBar;
                            if (iAddToDisplay < 0) {
                                this.mAttachInfo.mRootView = null;
                                this.mAdded = false;
                                this.mFallbackEventHandler.setView(null);
                                unscheduleTraversals();
                                setAccessibilityFocus(null, null);
                                switch (iAddToDisplay) {
                                    case -10:
                                        throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified window type " + this.mWindowAttributes.type + " is not valid");
                                    case -9:
                                        throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified display can not be found");
                                    case -8:
                                        throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- permission denied for window type " + this.mWindowAttributes.type);
                                    case -7:
                                        throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- another window of type " + this.mWindowAttributes.type + " already exists");
                                    case -6:
                                        return;
                                    case -5:
                                        throw new WindowManager.BadTokenException("Unable to add window -- window " + this.mWindow + " has already been added");
                                    case -4:
                                        throw new WindowManager.BadTokenException("Unable to add window -- app for token " + layoutParams2.token + " is exiting");
                                    case -3:
                                        throw new WindowManager.BadTokenException("Unable to add window -- token " + layoutParams2.token + " is not for an application");
                                    case -2:
                                    case -1:
                                        throw new WindowManager.BadTokenException("Unable to add window -- token " + layoutParams2.token + " is not valid; is your activity running?");
                                    default:
                                        throw new RuntimeException("Unable to add window -- unknown error code " + iAddToDisplay);
                                }
                            }
                            if (view instanceof RootViewSurfaceTaker) {
                                this.mInputQueueCallback = ((RootViewSurfaceTaker) view).willYouTakeTheInputQueue();
                            }
                            if (this.mInputChannel != null) {
                                if (this.mInputQueueCallback != null) {
                                    InputQueue inputQueue = new InputQueue();
                                    this.mInputQueue = inputQueue;
                                    this.mInputQueueCallback.onInputQueueCreated(inputQueue);
                                }
                                this.mInputEventReceiver = new WindowInputEventReceiver(this.mInputChannel, Looper.myLooper());
                            }
                            view.assignParent(this);
                            this.mAddedTouchMode = (iAddToDisplay & 1) != 0;
                            this.mAppVisible = (iAddToDisplay & 2) != 0;
                            if (this.mAccessibilityManager.isEnabled()) {
                                this.mAccessibilityInteractionConnectionManager.ensureConnection();
                            }
                            if (view.getImportantForAccessibility() == 0) {
                                view.setImportantForAccessibility(1);
                            }
                            CharSequence title = layoutParams2.getTitle();
                            this.mSyntheticInputStage = new SyntheticInputStage();
                            EarlyPostImeInputStage earlyPostImeInputStage = new EarlyPostImeInputStage(new NativePostImeInputStage(new ViewPostImeInputStage(this.mSyntheticInputStage), "aq:native-post-ime:" + ((Object) title)));
                            this.mFirstInputStage = new NativePreImeInputStage(new ViewPreImeInputStage(new ImeInputStage(earlyPostImeInputStage, "aq:ime:" + ((Object) title))), "aq:native-pre-ime:" + ((Object) title));
                            this.mFirstPostImeInputStage = earlyPostImeInputStage;
                            this.mPendingInputEventQueueLengthCounterName = "aq:pending:" + ((Object) title);
                        } catch (RemoteException e) {
                            e = e;
                            z2 = false;
                            this.mAdded = z2;
                            this.mView = null;
                            this.mAttachInfo.mRootView = null;
                            this.mInputChannel = null;
                            this.mFallbackEventHandler.setView(null);
                            unscheduleTraversals();
                            setAccessibilityFocus(null, null);
                            throw new RuntimeException("Adding window failed", e);
                        }
                    } catch (Throwable th) {
                        if (z) {
                            layoutParams2.restore();
                        }
                        throw th;
                    }
                } catch (RemoteException e2) {
                    e = e2;
                    z2 = false;
                }
            }
        }
    }

    private void setTag() {
        String[] strArrSplit = this.mWindowAttributes.getTitle().toString().split("\\.");
        if (strArrSplit.length > 0) {
            this.mTag = "ViewRootImpl[" + strArrSplit[strArrSplit.length - 1] + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInLocalFocusMode() {
        return (this.mWindowAttributes.flags & 268435456) != 0;
    }

    public int getWindowFlags() {
        return this.mWindowAttributes.flags;
    }

    public int getDisplayId() {
        return this.mDisplay.getDisplayId();
    }

    public CharSequence getTitle() {
        return this.mWindowAttributes.getTitle();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    void destroyHardwareResources() {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.destroyHardwareResources(this.mView);
            this.mAttachInfo.mThreadedRenderer.destroy();
        }
    }

    public void detachFunctor(long j) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.stopDrawing();
        }
    }

    public static void invokeFunctor(long j, boolean z) {
        ThreadedRenderer.invokeFunctor(j, z);
    }

    public void registerAnimatingRenderNode(RenderNode renderNode) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.registerAnimatingRenderNode(renderNode);
            return;
        }
        if (this.mAttachInfo.mPendingAnimatingRenderNodes == null) {
            this.mAttachInfo.mPendingAnimatingRenderNodes = new ArrayList();
        }
        this.mAttachInfo.mPendingAnimatingRenderNodes.add(renderNode);
    }

    public void registerVectorDrawableAnimator(AnimatedVectorDrawable.VectorDrawableAnimatorRT vectorDrawableAnimatorRT) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.registerVectorDrawableAnimator(vectorDrawableAnimatorRT);
        }
    }

    public void registerRtFrameCallback(ThreadedRenderer.FrameDrawingCallback frameDrawingCallback) {
        this.mNextRtFrameCallback = frameDrawingCallback;
    }

    private void enableHardwareAcceleration(WindowManager.LayoutParams layoutParams) {
        boolean z = false;
        this.mAttachInfo.mHardwareAccelerated = false;
        this.mAttachInfo.mHardwareAccelerationRequested = false;
        if (this.mTranslator != null) {
            return;
        }
        if (((layoutParams.flags & 16777216) != 0) && ThreadedRenderer.isAvailable()) {
            boolean z2 = (layoutParams.privateFlags & 1) != 0;
            boolean z3 = (layoutParams.privateFlags & 2) != 0;
            if (z2) {
                this.mAttachInfo.mHardwareAccelerationRequested = true;
                return;
            }
            if (!ThreadedRenderer.sRendererDisabled || (ThreadedRenderer.sSystemRendererDisabled && z3)) {
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    this.mAttachInfo.mThreadedRenderer.destroy();
                }
                Rect rect = layoutParams.surfaceInsets;
                boolean z4 = layoutParams.format != -1 || (rect.left != 0 || rect.right != 0 || rect.top != 0 || rect.bottom != 0);
                if (this.mContext.getResources().getConfiguration().isScreenWideColorGamut() && layoutParams.getColorMode() == 1) {
                    z = true;
                }
                this.mAttachInfo.mThreadedRenderer = ThreadedRenderer.create(this.mContext, z4, layoutParams.getTitle().toString());
                this.mAttachInfo.mThreadedRenderer.setWideGamut(z);
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    View.AttachInfo attachInfo = this.mAttachInfo;
                    attachInfo.mHardwareAccelerationRequested = true;
                    attachInfo.mHardwareAccelerated = true;
                }
            }
        }
    }

    public View getView() {
        return this.mView;
    }

    final WindowLeaked getLocation() {
        return this.mLocation;
    }

    void setLayoutParams(WindowManager.LayoutParams layoutParams, boolean z) {
        synchronized (this) {
            int i = this.mWindowAttributes.surfaceInsets.left;
            int i2 = this.mWindowAttributes.surfaceInsets.top;
            int i3 = this.mWindowAttributes.surfaceInsets.right;
            int i4 = this.mWindowAttributes.surfaceInsets.bottom;
            int i5 = this.mWindowAttributes.softInputMode;
            boolean z2 = this.mWindowAttributes.hasManualSurfaceInsets;
            this.mClientWindowLayoutFlags = layoutParams.flags;
            int i6 = this.mWindowAttributes.privateFlags & 128;
            layoutParams.systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
            layoutParams.subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
            int iCopyFrom = this.mWindowAttributes.copyFrom(layoutParams);
            this.mWindowAttributesChangesFlag = iCopyFrom;
            if ((iCopyFrom & 524288) != 0) {
                this.mAttachInfo.mRecomputeGlobalAttributes = true;
            }
            if ((this.mWindowAttributesChangesFlag & 1) != 0) {
                this.mAttachInfo.mNeedsUpdateLightCenter = true;
            }
            if (this.mWindowAttributes.packageName == null) {
                this.mWindowAttributes.packageName = this.mBasePackageName;
            }
            WindowManager.LayoutParams layoutParams2 = this.mWindowAttributes;
            layoutParams2.privateFlags = i6 | layoutParams2.privateFlags;
            if (this.mWindowAttributes.preservePreviousSurfaceInsets) {
                this.mWindowAttributes.surfaceInsets.set(i, i2, i3, i4);
                this.mWindowAttributes.hasManualSurfaceInsets = z2;
            } else if (this.mWindowAttributes.surfaceInsets.left != i || this.mWindowAttributes.surfaceInsets.top != i2 || this.mWindowAttributes.surfaceInsets.right != i3 || this.mWindowAttributes.surfaceInsets.bottom != i4) {
                this.mNeedsRendererSetup = true;
            }
            applyKeepScreenOnFlag(this.mWindowAttributes);
            if (z) {
                this.mSoftInputMode = layoutParams.softInputMode;
                requestLayout();
            }
            if ((layoutParams.softInputMode & 240) == 0) {
                this.mWindowAttributes.softInputMode = (this.mWindowAttributes.softInputMode & (-241)) | (i5 & 240);
            }
            this.mWindowAttributesChanged = true;
            scheduleTraversals();
        }
    }

    void handleAppVisibility(boolean z) {
        if (this.mAppVisible != z) {
            this.mAppVisible = z;
            this.mAppVisibilityChanged = true;
            scheduleTraversals();
            if (this.mAppVisible) {
                return;
            }
            WindowManagerGlobal.trimForeground();
        }
    }

    void handleGetNewSurface() {
        this.mNewSurfaceNeeded = true;
        this.mFullRedrawNeeded = true;
        scheduleTraversals();
    }

    public void onMovedToDisplay(int i, Configuration configuration) {
        if (this.mDisplay.getDisplayId() == i) {
            return;
        }
        Display adjustedDisplay = ResourcesManager.getInstance().getAdjustedDisplay(i, this.mView.getResources());
        this.mDisplay = adjustedDisplay;
        this.mAttachInfo.mDisplayState = adjustedDisplay.getState();
        this.mView.dispatchMovedToDisplay(this.mDisplay, configuration);
    }

    void pokeDrawLockIfNeeded() {
        int i = this.mAttachInfo.mDisplayState;
        if (this.mView != null && this.mAdded && this.mTraversalScheduled) {
            if (i == 3 || i == 4) {
                try {
                    this.mWindowSession.pokeDrawLock(this.mWindow);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    @Override // android.view.ViewParent
    public void requestFitSystemWindows() {
        checkThread();
        this.mApplyInsetsRequested = true;
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void requestLayout() {
        if (this.mHandlingLayoutInLayoutRequest) {
            return;
        }
        checkThread();
        this.mLayoutRequested = true;
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public boolean isLayoutRequested() {
        return this.mLayoutRequested;
    }

    @Override // android.view.ViewParent
    public void onDescendantInvalidated(View view, View view2) {
        if ((view2.mPrivateFlags & 64) != 0) {
            this.mIsAnimating = true;
        }
        invalidate();
    }

    void invalidate() {
        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
        if (this.mWillDrawSoon) {
            return;
        }
        scheduleTraversals();
    }

    void invalidateWorld(View view) {
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateWorld(viewGroup.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewParent
    public void invalidateChild(View view, Rect rect) {
        invalidateChildInParent(null, rect);
    }

    @Override // android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
        checkThread();
        if (rect == null) {
            invalidate();
            return null;
        }
        if (rect.isEmpty() && !this.mIsAnimating) {
            return null;
        }
        if (this.mCurScrollY != 0 || this.mTranslator != null) {
            this.mTempRect.set(rect);
            rect = this.mTempRect;
            int i = this.mCurScrollY;
            if (i != 0) {
                rect.offset(0, -i);
            }
            CompatibilityInfo.Translator translator = this.mTranslator;
            if (translator != null) {
                translator.translateRectInAppWindowToScreen(rect);
            }
            if (this.mAttachInfo.mScalingRequired) {
                rect.inset(-1, -1);
            }
        }
        invalidateRectOnScreen(rect);
        return null;
    }

    private void invalidateRectOnScreen(Rect rect) {
        Rect rect2 = this.mDirty;
        if (!rect2.isEmpty() && !rect2.contains(rect)) {
            this.mAttachInfo.mSetIgnoreDirtyState = true;
            this.mAttachInfo.mIgnoreDirtyState = true;
        }
        rect2.union(rect.left, rect.top, rect.right, rect.bottom);
        float f = this.mAttachInfo.mApplicationScale;
        boolean zIntersect = rect2.intersect(0, 0, (int) ((this.mWidth * f) + 0.5f), (int) ((this.mHeight * f) + 0.5f));
        if (!zIntersect) {
            rect2.setEmpty();
        }
        if (this.mWillDrawSoon) {
            return;
        }
        if (zIntersect || this.mIsAnimating) {
            scheduleTraversals();
        }
    }

    public void setIsAmbientMode(boolean z) {
        this.mIsAmbientMode = z;
    }

    void addWindowStoppedCallback(WindowStoppedCallback windowStoppedCallback) {
        this.mWindowStoppedCallbacks.add(windowStoppedCallback);
    }

    void removeWindowStoppedCallback(WindowStoppedCallback windowStoppedCallback) {
        this.mWindowStoppedCallbacks.remove(windowStoppedCallback);
    }

    void setWindowStopped(boolean z) {
        if (this.mStopped != z) {
            this.mStopped = z;
            ThreadedRenderer threadedRenderer = this.mAttachInfo.mThreadedRenderer;
            if (threadedRenderer != null) {
                threadedRenderer.setStopped(this.mStopped);
            }
            if (!this.mStopped) {
                scheduleTraversals();
            } else if (threadedRenderer != null) {
                threadedRenderer.destroyHardwareResources(this.mView);
            }
            for (int i = 0; i < this.mWindowStoppedCallbacks.size(); i++) {
                this.mWindowStoppedCallbacks.get(i).windowStopped(z);
            }
        }
    }

    public void setPausedForTransition(boolean z) {
        this.mPausedForTransition = z;
    }

    @Override // android.view.ViewParent
    public boolean getChildVisibleRect(View view, Rect rect, Point point) {
        if (view != this.mView) {
            throw new RuntimeException("child is not mine, honest!");
        }
        return rect.intersect(0, 0, this.mWidth, this.mHeight);
    }

    int getHostVisibility() {
        if (this.mAppVisible || this.mForceDecorViewVisibility) {
            return this.mView.getVisibility();
        }
        return 8;
    }

    public void requestTransitionStart(LayoutTransition layoutTransition) {
        ArrayList<LayoutTransition> arrayList = this.mPendingTransitions;
        if (arrayList == null || !arrayList.contains(layoutTransition)) {
            if (this.mPendingTransitions == null) {
                this.mPendingTransitions = new ArrayList<>();
            }
            this.mPendingTransitions.add(layoutTransition);
        }
    }

    void notifyRendererOfFramePending() {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.notifyFramePending();
        }
    }

    void scheduleTraversals() {
        if (this.mTraversalScheduled) {
            return;
        }
        this.mTraversalScheduled = true;
        this.mTraversalBarrier = this.mHandler.getLooper().getQueue().postSyncBarrier();
        this.mChoreographer.postCallback(2, this.mTraversalRunnable, null);
        if (!this.mUnbufferedInputDispatch) {
            scheduleConsumeBatchedInput();
        }
        notifyRendererOfFramePending();
        pokeDrawLockIfNeeded();
    }

    void unscheduleTraversals() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
            this.mChoreographer.removeCallbacks(2, this.mTraversalRunnable, null);
        }
    }

    void doTraversal() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
            if (this.mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            performTraversals();
            if (this.mProfile) {
                Debug.stopMethodTracing();
                this.mProfile = false;
            }
        }
    }

    private void applyKeepScreenOnFlag(WindowManager.LayoutParams layoutParams) {
        if (this.mAttachInfo.mKeepScreenOn) {
            layoutParams.flags |= 128;
        } else {
            layoutParams.flags = (layoutParams.flags & (-129)) | (this.mClientWindowLayoutFlags & 128);
        }
    }

    private boolean collectViewAttributes() {
        if (this.mAttachInfo.mRecomputeGlobalAttributes) {
            this.mAttachInfo.mRecomputeGlobalAttributes = false;
            boolean z = this.mAttachInfo.mKeepScreenOn;
            this.mAttachInfo.mKeepScreenOn = false;
            this.mAttachInfo.mSystemUiVisibility = 0;
            this.mAttachInfo.mHasSystemUiListeners = false;
            this.mView.dispatchCollectViewAttributes(this.mAttachInfo, 0);
            this.mAttachInfo.mSystemUiVisibility &= ~this.mAttachInfo.mDisabledSystemUiVisibility;
            WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
            this.mAttachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(layoutParams);
            if (this.mAttachInfo.mKeepScreenOn != z || this.mAttachInfo.mSystemUiVisibility != layoutParams.subtreeSystemUiVisibility || this.mAttachInfo.mHasSystemUiListeners != layoutParams.hasSystemUiListeners) {
                applyKeepScreenOnFlag(layoutParams);
                layoutParams.subtreeSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                layoutParams.hasSystemUiListeners = this.mAttachInfo.mHasSystemUiListeners;
                this.mView.dispatchWindowSystemUiVisiblityChanged(this.mAttachInfo.mSystemUiVisibility);
                return true;
            }
        }
        return false;
    }

    private int getImpliedSystemUiVisibility(WindowManager.LayoutParams layoutParams) {
        int i = (layoutParams.flags & 67108864) != 0 ? 1280 : 0;
        return (layoutParams.flags & 134217728) != 0 ? i | 768 : i;
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0055  */
    private boolean measureHierarchy(View view, WindowManager.LayoutParams layoutParams, Resources resources, int i, int i2) {
        boolean z;
        if (layoutParams.width == -2) {
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            resources.getValue(R.dimen.config_prefDialogWidth, this.mTmpValue, true);
            int dimension = this.mTmpValue.type == 5 ? (int) this.mTmpValue.getDimension(displayMetrics) : 0;
            if (dimension == 0 || i <= dimension) {
                z = false;
            } else {
                int rootMeasureSpec = getRootMeasureSpec(dimension, layoutParams.width);
                int rootMeasureSpec2 = getRootMeasureSpec(i2, layoutParams.height);
                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                if ((view.getMeasuredWidthAndState() & 16777216) != 0) {
                    performMeasure(getRootMeasureSpec((dimension + i) / 2, layoutParams.width), rootMeasureSpec2);
                    if ((view.getMeasuredWidthAndState() & 16777216) != 0) {
                        z = false;
                    }
                }
                z = true;
            }
        } else {
            z = false;
        }
        if (z) {
            return false;
        }
        performMeasure(getRootMeasureSpec(i, layoutParams.width), getRootMeasureSpec(i2, layoutParams.height));
        return (this.mWidth == view.getMeasuredWidth() && this.mHeight == view.getMeasuredHeight()) ? false : true;
    }

    void transformMatrixToGlobal(Matrix matrix) {
        matrix.preTranslate(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
    }

    void transformMatrixToLocal(Matrix matrix) {
        matrix.postTranslate(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
    }

    WindowInsets getWindowInsets(boolean z) {
        if (this.mLastWindowInsets == null || z) {
            this.mDispatchContentInsets.set(this.mAttachInfo.mContentInsets);
            this.mDispatchStableInsets.set(this.mAttachInfo.mStableInsets);
            DisplayCutout displayCutout = this.mAttachInfo.mDisplayCutout.get();
            this.mDispatchDisplayCutout = displayCutout;
            Rect rect = this.mDispatchContentInsets;
            Rect rect2 = this.mDispatchStableInsets;
            if (!z && (!this.mPendingContentInsets.equals(rect) || !this.mPendingStableInsets.equals(rect2) || !this.mPendingDisplayCutout.get().equals(displayCutout))) {
                rect = this.mPendingContentInsets;
                rect2 = this.mPendingStableInsets;
                displayCutout = this.mPendingDisplayCutout.get();
            }
            DisplayCutout displayCutout2 = displayCutout;
            Rect rect3 = this.mAttachInfo.mOutsets;
            if (rect3.left > 0 || rect3.top > 0 || rect3.right > 0 || rect3.bottom > 0) {
                rect = new Rect(rect.left + rect3.left, rect.top + rect3.top, rect.right + rect3.right, rect.bottom + rect3.bottom);
            }
            this.mLastWindowInsets = new WindowInsets(ensureInsetsNonNegative(rect, "content"), null, ensureInsetsNonNegative(rect2, "stable"), this.mContext.getResources().getConfiguration().isScreenRound(), this.mAttachInfo.mAlwaysConsumeNavBar, displayCutout2);
        }
        return this.mLastWindowInsets;
    }

    private Rect ensureInsetsNonNegative(Rect rect, String str) {
        if (rect.left >= 0 && rect.top >= 0 && rect.right >= 0 && rect.bottom >= 0) {
            return rect;
        }
        Log.wtf(this.mTag, "Negative " + str + "Insets: " + rect + ", mFirst=" + this.mFirst);
        return new Rect(Math.max(0, rect.left), Math.max(0, rect.top), Math.max(0, rect.right), Math.max(0, rect.bottom));
    }

    void dispatchApplyInsets(View view) {
        WindowInsets windowInsets = getWindowInsets(true);
        if (!(this.mWindowAttributes.layoutInDisplayCutoutMode == 1)) {
            windowInsets = windowInsets.consumeDisplayCutout();
        }
        view.dispatchApplyWindowInsets(windowInsets);
    }

    private static boolean shouldUseDisplaySize(WindowManager.LayoutParams layoutParams) {
        return layoutParams.type == 2014 || layoutParams.type == 2011 || layoutParams.type == 2020;
    }

    private int dipToPx(int i) {
        return (int) ((this.mContext.getResources().getDisplayMetrics().density * i) + 0.5f);
    }

    /* JADX WARN: Code duplicated, block: B:102:0x01c1  */
    /* JADX WARN: Code duplicated, block: B:105:0x01ca  */
    /* JADX WARN: Code duplicated, block: B:112:0x01db  */
    /* JADX WARN: Code duplicated, block: B:114:0x01e1  */
    /* JADX WARN: Code duplicated, block: B:115:0x01f0  */
    /* JADX WARN: Code duplicated, block: B:118:0x021a  */
    /* JADX WARN: Code duplicated, block: B:121:0x0229  */
    /* JADX WARN: Code duplicated, block: B:124:0x0230  */
    /* JADX WARN: Code duplicated, block: B:129:0x0242  */
    /* JADX WARN: Code duplicated, block: B:131:0x024d  */
    /* JADX WARN: Code duplicated, block: B:133:0x0258  */
    /* JADX WARN: Code duplicated, block: B:135:0x0268  */
    /* JADX WARN: Code duplicated, block: B:138:0x026f  */
    /* JADX WARN: Code duplicated, block: B:141:0x0276  */
    /* JADX WARN: Code duplicated, block: B:143:0x0280  */
    /* JADX WARN: Code duplicated, block: B:150:0x029a  */
    /* JADX WARN: Code duplicated, block: B:151:0x029c  */
    /* JADX WARN: Code duplicated, block: B:155:0x02a3  */
    /* JADX WARN: Code duplicated, block: B:157:0x02b3  */
    /* JADX WARN: Code duplicated, block: B:158:0x02cb  */
    /* JADX WARN: Code duplicated, block: B:160:0x02cf  */
    /* JADX WARN: Code duplicated, block: B:180:0x030e  */
    /* JADX WARN: Code duplicated, block: B:186:0x0319  */
    /* JADX WARN: Code duplicated, block: B:193:0x0331  */
    /* JADX WARN: Code duplicated, block: B:196:0x033a  */
    /* JADX WARN: Code duplicated, block: B:197:0x033d  */
    /* JADX WARN: Code duplicated, block: B:207:0x035f  */
    /* JADX WARN: Code duplicated, block: B:209:0x0366  */
    /* JADX WARN: Code duplicated, block: B:210:0x0368  */
    /* JADX WARN: Code duplicated, block: B:214:0x0370  */
    /* JADX WARN: Code duplicated, block: B:216:0x0373  */
    /* JADX WARN: Code duplicated, block: B:219:0x0378  */
    /* JADX WARN: Code duplicated, block: B:223:0x038c A[Catch: RemoteException -> 0x061b, TryCatch #17 {RemoteException -> 0x061b, blocks: (B:221:0x0386, B:223:0x038c, B:225:0x0398, B:226:0x03a2, B:227:0x03ab), top: B:648:0x0386 }] */
    /* JADX WARN: Code duplicated, block: B:225:0x0398 A[Catch: RemoteException -> 0x061b, TryCatch #17 {RemoteException -> 0x061b, blocks: (B:221:0x0386, B:223:0x038c, B:225:0x0398, B:226:0x03a2, B:227:0x03ab), top: B:648:0x0386 }] */
    /* JADX WARN: Code duplicated, block: B:230:0x03b9 A[Catch: RemoteException -> 0x0611, TryCatch #9 {RemoteException -> 0x0611, blocks: (B:228:0x03af, B:230:0x03b9, B:234:0x03c2), top: B:633:0x03af }] */
    /* JADX WARN: Code duplicated, block: B:232:0x03bf  */
    /* JADX WARN: Code duplicated, block: B:233:0x03c1  */
    /* JADX WARN: Code duplicated, block: B:236:0x03c9  */
    /* JADX WARN: Code duplicated, block: B:239:0x03d7  */
    /* JADX WARN: Code duplicated, block: B:240:0x03d9  */
    /* JADX WARN: Code duplicated, block: B:243:0x03e6  */
    /* JADX WARN: Code duplicated, block: B:244:0x03e8  */
    /* JADX WARN: Code duplicated, block: B:247:0x03f5  */
    /* JADX WARN: Code duplicated, block: B:248:0x03f7  */
    /* JADX WARN: Code duplicated, block: B:253:0x0406  */
    /* JADX WARN: Code duplicated, block: B:254:0x0408  */
    /* JADX WARN: Code duplicated, block: B:259:0x0417  */
    /* JADX WARN: Code duplicated, block: B:260:0x0419  */
    /* JADX WARN: Code duplicated, block: B:266:0x042c  */
    /* JADX WARN: Code duplicated, block: B:267:0x042e  */
    /* JADX WARN: Code duplicated, block: B:273:0x043c  */
    /* JADX WARN: Code duplicated, block: B:274:0x043e  */
    /* JADX WARN: Code duplicated, block: B:276:0x0441 A[Catch: RemoteException -> 0x05e0, TRY_LEAVE, TryCatch #1 {RemoteException -> 0x05e0, blocks: (B:271:0x0436, B:276:0x0441), top: B:619:0x0436 }] */
    /* JADX WARN: Code duplicated, block: B:279:0x044d  */
    /* JADX WARN: Code duplicated, block: B:281:0x0451 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:283:0x045e A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:285:0x046b A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:287:0x0478 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:289:0x0482 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:28:0x0041  */
    /* JADX WARN: Code duplicated, block: B:296:0x0498 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:298:0x04b5 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:300:0x04c0 A[Catch: RemoteException -> 0x05e7, TRY_LEAVE, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:302:0x04c8  */
    /* JADX WARN: Code duplicated, block: B:309:0x04e8 A[Catch: OutOfResourcesException -> 0x04ee, RemoteException -> 0x04fa, TRY_LEAVE, TryCatch #0 {OutOfResourcesException -> 0x04ee, blocks: (B:307:0x04e2, B:309:0x04e8), top: B:617:0x04e2 }] */
    /* JADX WARN: Code duplicated, block: B:319:0x0502  */
    /* JADX WARN: Code duplicated, block: B:321:0x050c A[Catch: RemoteException -> 0x05e7, TRY_ENTER, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:323:0x0514 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:325:0x0518 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:328:0x0528 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:331:0x0534 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:334:0x053f A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:337:0x0551 A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:339:0x0559 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:341:0x055d A[Catch: RemoteException -> 0x05e7, TryCatch #7 {RemoteException -> 0x05e7, blocks: (B:278:0x0447, B:281:0x0451, B:283:0x045e, B:285:0x046b, B:287:0x0478, B:289:0x0482, B:291:0x048a, B:293:0x048e, B:298:0x04b5, B:300:0x04c0, B:321:0x050c, B:323:0x0514, B:325:0x0518, B:326:0x051d, B:328:0x0528, B:329:0x0530, B:331:0x0534, B:332:0x0539, B:334:0x053f, B:336:0x0549, B:337:0x0551, B:341:0x055d, B:343:0x0561, B:345:0x0567, B:346:0x056a, B:349:0x0575, B:296:0x0498), top: B:630:0x0447, inners: #4 }] */
    /* JADX WARN: Code duplicated, block: B:355:0x057f  */
    /* JADX WARN: Code duplicated, block: B:356:0x0581  */
    /* JADX WARN: Code duplicated, block: B:359:0x0586  */
    /* JADX WARN: Code duplicated, block: B:360:0x0588  */
    /* JADX WARN: Code duplicated, block: B:362:0x058b A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:365:0x0590  */
    /* JADX WARN: Code duplicated, block: B:368:0x0595 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:369:0x0597 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:370:0x0599  */
    /* JADX WARN: Code duplicated, block: B:371:0x059b  */
    /* JADX WARN: Code duplicated, block: B:375:0x05b6 A[Catch: RemoteException -> 0x05d9, TryCatch #13 {RemoteException -> 0x05d9, blocks: (B:374:0x05b2, B:377:0x05be, B:380:0x05c4, B:381:0x05d1, B:375:0x05b6), top: B:641:0x0593 }] */
    /* JADX WARN: Code duplicated, block: B:376:0x05bc  */
    /* JADX WARN: Code duplicated, block: B:379:0x05c2 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:380:0x05c4 A[Catch: RemoteException -> 0x05d9, TryCatch #13 {RemoteException -> 0x05d9, blocks: (B:374:0x05b2, B:377:0x05be, B:380:0x05c4, B:381:0x05d1, B:375:0x05b6), top: B:641:0x0593 }] */
    /* JADX WARN: Code duplicated, block: B:381:0x05d1 A[Catch: RemoteException -> 0x05d9, TRY_LEAVE, TryCatch #13 {RemoteException -> 0x05d9, blocks: (B:374:0x05b2, B:377:0x05be, B:380:0x05c4, B:381:0x05d1, B:375:0x05b6), top: B:641:0x0593 }] */
    /* JADX WARN: Code duplicated, block: B:405:0x063f  */
    /* JADX WARN: Code duplicated, block: B:407:0x0647  */
    /* JADX WARN: Code duplicated, block: B:410:0x0657  */
    /* JADX WARN: Code duplicated, block: B:412:0x065f  */
    /* JADX WARN: Code duplicated, block: B:415:0x067d A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:416:0x067f  */
    /* JADX WARN: Code duplicated, block: B:418:0x068f  */
    /* JADX WARN: Code duplicated, block: B:420:0x0693 A[LOOP:2: B:419:0x0691->B:420:0x0693, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:423:0x06a1  */
    /* JADX WARN: Code duplicated, block: B:425:0x06a9  */
    /* JADX WARN: Code duplicated, block: B:429:0x06b5 A[LOOP:3: B:428:0x06b3->B:429:0x06b5, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:431:0x06c9 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:432:0x06cb  */
    /* JADX WARN: Code duplicated, block: B:434:0x06d8  */
    /* JADX WARN: Code duplicated, block: B:436:0x06dc A[LOOP:4: B:435:0x06da->B:436:0x06dc, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:445:0x070d  */
    /* JADX WARN: Code duplicated, block: B:457:0x073d  */
    /* JADX WARN: Code duplicated, block: B:459:0x0741  */
    /* JADX WARN: Code duplicated, block: B:461:0x0745  */
    /* JADX WARN: Code duplicated, block: B:462:0x0747  */
    /* JADX WARN: Code duplicated, block: B:465:0x074e  */
    /* JADX WARN: Code duplicated, block: B:471:0x0762  */
    /* JADX WARN: Code duplicated, block: B:473:0x0786  */
    /* JADX WARN: Code duplicated, block: B:474:0x0795  */
    /* JADX WARN: Code duplicated, block: B:477:0x079c  */
    /* JADX WARN: Code duplicated, block: B:479:0x07ac  */
    /* JADX WARN: Code duplicated, block: B:483:0x07b4  */
    /* JADX WARN: Code duplicated, block: B:488:0x07be  */
    /* JADX WARN: Code duplicated, block: B:490:0x07c1  */
    /* JADX WARN: Code duplicated, block: B:494:0x07ca  */
    /* JADX WARN: Code duplicated, block: B:496:0x07cd  */
    /* JADX WARN: Code duplicated, block: B:498:0x07da  */
    /* JADX WARN: Code duplicated, block: B:500:0x0807  */
    /* JADX WARN: Code duplicated, block: B:503:0x0816  */
    /* JADX WARN: Code duplicated, block: B:506:0x082b  */
    /* JADX WARN: Code duplicated, block: B:508:0x0839  */
    /* JADX WARN: Code duplicated, block: B:510:0x0853  */
    /* JADX WARN: Code duplicated, block: B:512:0x085b  */
    /* JADX WARN: Code duplicated, block: B:514:0x0864  */
    /* JADX WARN: Code duplicated, block: B:515:0x087b  */
    /* JADX WARN: Code duplicated, block: B:520:0x0891  */
    /* JADX WARN: Code duplicated, block: B:522:0x0895  */
    /* JADX WARN: Code duplicated, block: B:530:0x08b5  */
    /* JADX WARN: Code duplicated, block: B:532:0x08b9  */
    /* JADX WARN: Code duplicated, block: B:536:0x08c6  */
    /* JADX WARN: Code duplicated, block: B:538:0x08ca A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:543:0x08d5 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:545:0x08d9  */
    /* JADX WARN: Code duplicated, block: B:547:0x08dc  */
    /* JADX WARN: Code duplicated, block: B:550:0x08e2  */
    /* JADX WARN: Code duplicated, block: B:552:0x08e5  */
    /* JADX WARN: Code duplicated, block: B:553:0x08e9  */
    /* JADX WARN: Code duplicated, block: B:554:0x08eb  */
    /* JADX WARN: Code duplicated, block: B:558:0x08f4 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:561:0x08f9  */
    /* JADX WARN: Code duplicated, block: B:563:0x08fd  */
    /* JADX WARN: Code duplicated, block: B:564:0x08ff  */
    /* JADX WARN: Code duplicated, block: B:568:0x0908  */
    /* JADX WARN: Code duplicated, block: B:571:0x091e  */
    /* JADX WARN: Code duplicated, block: B:579:0x0956  */
    /* JADX WARN: Code duplicated, block: B:582:0x095b  */
    /* JADX WARN: Code duplicated, block: B:585:0x0968 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:588:0x096d  */
    /* JADX WARN: Code duplicated, block: B:590:0x0970 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:59:0x0101  */
    /* JADX WARN: Code duplicated, block: B:601:0x099c A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:602:0x099e  */
    /* JADX WARN: Code duplicated, block: B:603:0x09a2  */
    /* JADX WARN: Code duplicated, block: B:605:0x09a6  */
    /* JADX WARN: Code duplicated, block: B:610:0x09b5 A[LOOP:1: B:608:0x09ad->B:610:0x09b5, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:617:0x04e2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:61:0x010a A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:627:0x04d6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:62:0x010c  */
    /* JADX WARN: Code duplicated, block: B:63:0x010e  */
    /* JADX WARN: Code duplicated, block: B:660:0x026a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:68:0x0118  */
    /* JADX WARN: Code duplicated, block: B:71:0x0122  */
    /* JADX WARN: Code duplicated, block: B:74:0x012a  */
    /* JADX WARN: Code duplicated, block: B:82:0x0147  */
    /* JADX WARN: Code duplicated, block: B:85:0x014c  */
    /* JADX WARN: Code duplicated, block: B:87:0x015a  */
    /* JADX WARN: Code duplicated, block: B:88:0x016e  */
    /* JADX WARN: Code duplicated, block: B:90:0x0185  */
    /* JADX WARN: Code duplicated, block: B:93:0x0192  */
    /* JADX WARN: Code duplicated, block: B:96:0x019f  */
    /* JADX WARN: Code duplicated, block: B:99:0x01ac  */
    /* JADX WARN: Instruction removed from duplicated block: B:418:0x068f, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:434:0x06d8, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r14v20 */
    /* JADX WARN: Type inference failed for: r14v21 */
    /* JADX WARN: Type inference failed for: r14v8 */
    /* JADX WARN: Type inference failed for: r1v100 */
    /* JADX WARN: Type inference failed for: r1v101 */
    /* JADX WARN: Type inference failed for: r1v103 */
    /* JADX WARN: Type inference failed for: r1v119 */
    /* JADX WARN: Type inference failed for: r1v233 */
    /* JADX WARN: Type inference failed for: r1v234 */
    /* JADX WARN: Type inference failed for: r1v235 */
    /* JADX WARN: Type inference failed for: r1v236 */
    /* JADX WARN: Type inference failed for: r1v33 */
    /* JADX WARN: Type inference failed for: r1v34, types: [android.graphics.Rect] */
    /* JADX WARN: Type inference failed for: r1v99 */
    private void performTraversals() {
        boolean z;
        boolean z2;
        WindowManager.LayoutParams layoutParams;
        int iWidth;
        int iHeight;
        boolean z3;
        boolean z4;
        boolean zMeasureHierarchy;
        int i;
        int i2;
        boolean z5;
        int i3;
        int size;
        int i4;
        boolean z6;
        boolean z7;
        boolean z8;
        int generationId;
        boolean z9;
        Object obj;
        boolean z10;
        BaseSurfaceHolder baseSurfaceHolder;
        boolean zIsValid;
        boolean z11;
        boolean z12;
        int iRelayoutWindow;
        boolean z13;
        Object obj2;
        boolean z14;
        ?? r1;
        boolean z15;
        ?? r2;
        ThreadedRenderer threadedRenderer;
        boolean z16;
        int rootMeasureSpec;
        int rootMeasureSpec2;
        int measuredWidth;
        int measuredHeight;
        boolean z17;
        int i5;
        SurfaceHolder.Callback[] callbacks;
        int i6;
        SurfaceHolder.Callback[] callbacks2;
        int i7;
        SurfaceHolder.Callback[] callbacks3;
        int i8;
        boolean z18;
        Object obj3;
        Object obj4;
        boolean z19;
        boolean z20;
        boolean z21;
        boolean z22;
        boolean z23;
        boolean z24;
        ?? r14;
        boolean z25;
        boolean z26;
        boolean z27;
        ?? r3;
        int i9;
        boolean zInitialize;
        boolean z28;
        boolean z29;
        boolean z30;
        boolean z31;
        boolean z32;
        boolean z33;
        boolean z34;
        WindowManager.LayoutParams layoutParams2;
        boolean z35;
        boolean z36;
        boolean z37;
        ArrayList<LayoutTransition> arrayList;
        int i10;
        boolean zMayUseInputMethod;
        View view;
        ViewTreeObserver.InternalInsetsInfo internalInsetsInfo;
        CompatibilityInfo.Translator translator;
        Rect translatedContentInsets;
        Rect translatedVisibleInsets;
        Region translatedTouchableArea;
        CompatibilityInfo.Translator translator2;
        boolean z38;
        Resources resources;
        boolean z39;
        int iDipToPx;
        int iDipToPx2;
        int i11;
        boolean z40;
        boolean z41;
        View view2 = this.mView;
        if (view2 == null || !this.mAdded) {
            return;
        }
        this.mIsInTraversal = true;
        this.mWillDrawSoon = true;
        WindowManager.LayoutParams layoutParams3 = this.mWindowAttributes;
        int hostVisibility = getHostVisibility();
        boolean z42 = !this.mFirst && (this.mViewVisibility != hostVisibility || this.mNewSurfaceNeeded || this.mAppVisibilityChanged);
        this.mAppVisibilityChanged = false;
        if (this.mFirst) {
            z = false;
        } else if ((this.mViewVisibility == 0) != (hostVisibility == 0)) {
            z = true;
        } else {
            z = false;
        }
        WindowManager.LayoutParams layoutParams4 = null;
        if (this.mWindowAttributesChanged) {
            this.mWindowAttributesChanged = false;
            z2 = true;
            layoutParams4 = layoutParams3;
        } else {
            z2 = false;
        }
        boolean zSupportsScreen = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo().supportsScreen();
        boolean z43 = this.mLastInCompatMode;
        if (zSupportsScreen == z43) {
            this.mFullRedrawNeeded = true;
            this.mLayoutRequested = true;
            if (z43) {
                layoutParams3.privateFlags &= -129;
                this.mLastInCompatMode = false;
            } else {
                layoutParams3.privateFlags |= 128;
                this.mLastInCompatMode = true;
            }
            layoutParams = layoutParams3;
        } else {
            layoutParams = layoutParams4;
        }
        this.mWindowAttributesChangesFlag = 0;
        Rect rect = this.mWinFrame;
        if (this.mFirst) {
            this.mFullRedrawNeeded = true;
            this.mLayoutRequested = true;
            Configuration configuration = this.mContext.getResources().getConfiguration();
            if (shouldUseDisplaySize(layoutParams3)) {
                Point point = new Point();
                this.mDisplay.getRealSize(point);
                iWidth = point.x;
                iHeight = point.y;
            } else {
                iWidth = this.mWinFrame.width();
                iHeight = this.mWinFrame.height();
            }
            this.mAttachInfo.mUse32BitDrawingCache = true;
            this.mAttachInfo.mHasWindowFocus = false;
            this.mAttachInfo.mWindowVisibility = hostVisibility;
            this.mAttachInfo.mRecomputeGlobalAttributes = false;
            this.mLastConfigurationFromResources.setTo(configuration);
            this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
            if (this.mViewLayoutDirectionInitial == 2) {
                view2.setLayoutDirection(configuration.getLayoutDirection());
            }
            view2.dispatchAttachedToWindow(this.mAttachInfo, 0);
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true);
            dispatchApplyInsets(view2);
        } else {
            iWidth = rect.width();
            iHeight = rect.height();
            if (iWidth != this.mWidth || iHeight != this.mHeight) {
                this.mFullRedrawNeeded = true;
                this.mLayoutRequested = true;
                z3 = true;
            }
            if (z42) {
                this.mAttachInfo.mWindowVisibility = hostVisibility;
                view2.dispatchWindowVisibilityChanged(hostVisibility);
                if (z) {
                    if (hostVisibility == 0) {
                        z41 = true;
                    } else {
                        z41 = false;
                    }
                    view2.dispatchVisibilityAggregated(z41);
                }
                if (hostVisibility == 0 || this.mNewSurfaceNeeded) {
                    endDragResizing();
                    destroyHardwareResources();
                }
                if (hostVisibility == 8) {
                    this.mHasHadWindowFocus = false;
                }
            }
            if (this.mAttachInfo.mWindowVisibility != 0) {
                view2.clearAccessibilityFocus();
            }
            getRunQueue().executeActions(this.mAttachInfo.mHandler);
            if (this.mLayoutRequested || (this.mStopped && !this.mReportNextDraw)) {
                z4 = false;
            } else {
                z4 = true;
            }
            if (z4) {
                resources = this.mView.getContext().getResources();
                if (this.mFirst) {
                    this.mAttachInfo.mInTouchMode = !this.mAddedTouchMode;
                    ensureTouchModeLocally(this.mAddedTouchMode);
                    z40 = z3;
                    i = iHeight;
                    i11 = iWidth;
                    z5 = false;
                } else {
                    z39 = !this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets);
                    if (!this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                        z39 = true;
                    }
                    if (!this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                        z39 = true;
                    }
                    if (!this.mPendingDisplayCutout.equals(this.mAttachInfo.mDisplayCutout)) {
                        z39 = true;
                    }
                    if (!this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                        this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
                    }
                    if (!this.mPendingOutsets.equals(this.mAttachInfo.mOutsets)) {
                        z39 = true;
                    }
                    if (this.mPendingAlwaysConsumeNavBar != this.mAttachInfo.mAlwaysConsumeNavBar) {
                        z39 = true;
                    }
                    if (layoutParams3.width != -2 || layoutParams3.height == -2) {
                        if (shouldUseDisplaySize(layoutParams3)) {
                            Point point2 = new Point();
                            this.mDisplay.getRealSize(point2);
                            iDipToPx = point2.x;
                            iDipToPx2 = point2.y;
                        } else {
                            Configuration configuration2 = resources.getConfiguration();
                            iDipToPx = dipToPx(configuration2.screenWidthDp);
                            iDipToPx2 = dipToPx(configuration2.screenHeightDp);
                        }
                        i = iDipToPx2;
                        i11 = iDipToPx;
                        z5 = z39;
                        z40 = true;
                    } else {
                        z40 = z3;
                        i = iHeight;
                        i11 = iWidth;
                        z5 = z39;
                    }
                }
                zMeasureHierarchy = measureHierarchy(view2, layoutParams3, resources, i11, i) | z40;
                i2 = i11;
            } else {
                zMeasureHierarchy = z3;
                i = iHeight;
                i2 = iWidth;
                z5 = false;
            }
            if (collectViewAttributes()) {
                layoutParams = layoutParams3;
            }
            if (this.mAttachInfo.mForceReportNewAttributes) {
                this.mAttachInfo.mForceReportNewAttributes = false;
                layoutParams = layoutParams3;
            }
            if (!this.mFirst || this.mAttachInfo.mViewVisibilityChanged) {
                this.mAttachInfo.mViewVisibilityChanged = false;
                i3 = this.mSoftInputMode & 240;
                if (i3 == 0) {
                    size = this.mAttachInfo.mScrollContainers.size();
                    for (i4 = 0; i4 < size; i4++) {
                        if (this.mAttachInfo.mScrollContainers.get(i4).isShown()) {
                            i3 = 16;
                        }
                    }
                    if (i3 == 0) {
                        i3 = 32;
                    }
                    if ((layoutParams3.softInputMode & 240) != i3) {
                        layoutParams3.softInputMode = i3 | (layoutParams3.softInputMode & (-241));
                        layoutParams = layoutParams3;
                    }
                }
            }
            if (layoutParams != null) {
                if ((view2.mPrivateFlags & 512) != 0 && !PixelFormat.formatHasAlpha(layoutParams.format)) {
                    layoutParams.format = -3;
                }
                View.AttachInfo attachInfo = this.mAttachInfo;
                if ((layoutParams.flags & 33554432) != 0) {
                    z38 = true;
                } else {
                    z38 = false;
                }
                attachInfo.mOverscanRequested = z38;
            }
            if (this.mApplyInsetsRequested) {
                this.mApplyInsetsRequested = false;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                dispatchApplyInsets(view2);
                if (this.mLayoutRequested) {
                    zMeasureHierarchy |= measureHierarchy(view2, layoutParams3, this.mView.getContext().getResources(), i2, i);
                }
            }
            if (z4) {
                this.mLayoutRequested = false;
            }
            if (z4 || !zMeasureHierarchy || (this.mWidth == view2.getMeasuredWidth() && this.mHeight == view2.getMeasuredHeight() && ((layoutParams3.width != -2 || rect.width() >= i2 || rect.width() == this.mWidth) && (layoutParams3.height != -2 || rect.height() >= i || rect.height() == this.mHeight)))) {
                z6 = false;
            } else {
                z6 = true;
            }
            if (this.mDragResizing || this.mResizeMode != 0) {
                z7 = false;
            } else {
                z7 = true;
            }
            boolean z44 = z6 | z7 | this.mActivityRelaunched;
            if (!this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners() || this.mAttachInfo.mHasNonEmptyGivenInternalInsets) {
                z8 = true;
            } else {
                z8 = false;
            }
            generationId = this.mSurface.getGenerationId();
            if (hostVisibility == 0) {
                z9 = true;
            } else {
                z9 = false;
            }
            boolean z45 = this.mForceNextWindowRelayout;
            if (!this.mFirst || z44 || z5 || z42 || layoutParams != null || z45) {
                obj = rect;
                this.mForceNextWindowRelayout = false;
                if (z9) {
                    if (z8 || !(this.mFirst || z42)) {
                        z29 = false;
                    } else {
                        z29 = true;
                    }
                    z10 = z29;
                } else {
                    z10 = false;
                }
                baseSurfaceHolder = this.mSurfaceHolder;
                if (baseSurfaceHolder != null) {
                    baseSurfaceHolder.mSurfaceLock.lock();
                    this.mDrawingAllowed = true;
                }
                zIsValid = this.mSurface.isValid();
                try {
                    if (this.mAttachInfo.mThreadedRenderer != null) {
                        if (this.mAttachInfo.mThreadedRenderer.pauseSurface(this.mSurface)) {
                            this.mDirty.set(0, 0, this.mWidth, this.mHeight);
                        }
                        this.mChoreographer.mFrameInfo.addFlags(1L);
                    }
                    iRelayoutWindow = relayoutWindow(layoutParams, hostVisibility, z10);
                    try {
                        if (this.mPendingMergedConfiguration.equals(this.mLastReportedMergedConfiguration)) {
                            z13 = false;
                        } else {
                            MergedConfiguration mergedConfiguration = this.mPendingMergedConfiguration;
                            if (this.mFirst) {
                                z28 = false;
                            } else {
                                z28 = true;
                            }
                            performConfigurationChange(mergedConfiguration, z28, -1);
                            z13 = true;
                        }
                        try {
                            if (this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets)) {
                                z18 = false;
                            } else {
                                z18 = true;
                            }
                            if (this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                                z14 = false;
                            } else {
                                z14 = true;
                            }
                            try {
                                if (this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                                    z19 = false;
                                } else {
                                    z19 = true;
                                }
                                z10 = z10;
                                try {
                                    if (this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                                        z20 = false;
                                    } else {
                                        z20 = true;
                                    }
                                    try {
                                        if (this.mPendingDisplayCutout.equals(this.mAttachInfo.mDisplayCutout)) {
                                            z21 = false;
                                        } else {
                                            z21 = true;
                                        }
                                        hostVisibility = hostVisibility;
                                        try {
                                            boolean z46 = !this.mPendingOutsets.equals(this.mAttachInfo.mOutsets);
                                            if ((iRelayoutWindow & 32) != 0) {
                                                z22 = true;
                                            } else {
                                                z22 = false;
                                            }
                                            z15 = z2 | z22;
                                            try {
                                                z23 = this.mPendingAlwaysConsumeNavBar;
                                                z42 = z42;
                                                try {
                                                    if (z23 != this.mAttachInfo.mAlwaysConsumeNavBar) {
                                                        z24 = true;
                                                    } else {
                                                        z24 = false;
                                                    }
                                                    if (z14) {
                                                        Rect rect2 = this.mAttachInfo.mContentInsets;
                                                        z14 = z14;
                                                        try {
                                                            rect2.set(this.mPendingContentInsets);
                                                            r14 = rect2;
                                                        } catch (RemoteException unused) {
                                                            obj3 = obj;
                                                            z2 = z15;
                                                            z11 = false;
                                                            obj4 = obj3;
                                                            z12 = false;
                                                            r1 = obj4;
                                                            z15 = z2;
                                                            r2 = r1;
                                                            this.mAttachInfo.mWindowLeft = r2.left;
                                                            this.mAttachInfo.mWindowTop = r2.top;
                                                            if (this.mWidth == r2.width()) {
                                                                this.mWidth = r2.width();
                                                                this.mHeight = r2.height();
                                                            } else {
                                                                this.mWidth = r2.width();
                                                                this.mHeight = r2.height();
                                                            }
                                                            if (this.mSurfaceHolder != null) {
                                                                if (this.mSurface.isValid()) {
                                                                    this.mSurfaceHolder.mSurface = this.mSurface;
                                                                }
                                                                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                                this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                if (this.mSurface.isValid()) {
                                                                    if (!zIsValid) {
                                                                        this.mSurfaceHolder.ungetCallbacks();
                                                                        this.mIsCreating = true;
                                                                        callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                        if (callbacks3 != null) {
                                                                            for (SurfaceHolder.Callback callback : callbacks3) {
                                                                                callback.surfaceCreated(this.mSurfaceHolder);
                                                                            }
                                                                        }
                                                                        z15 = true;
                                                                    }
                                                                    if (z15) {
                                                                        while (i7 < r2) {
                                                                            callback.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                        }
                                                                    } else {
                                                                        for (SurfaceHolder.Callback callback2 : callbacks2) {
                                                                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                        }
                                                                    }
                                                                    this.mIsCreating = false;
                                                                } else if (zIsValid) {
                                                                    this.mSurfaceHolder.ungetCallbacks();
                                                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                                                    if (callbacks != null) {
                                                                        for (SurfaceHolder.Callback callback3 : callbacks) {
                                                                            callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                                        }
                                                                    }
                                                                    this.mSurfaceHolder.mSurfaceLock.lock();
                                                                    try {
                                                                        this.mSurfaceHolder.mSurface = new Surface();
                                                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                    } catch (Throwable th) {
                                                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                        throw th;
                                                                    }
                                                                }
                                                            }
                                                            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                            if (threadedRenderer != null) {
                                                                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                                this.mNeedsRendererSetup = false;
                                                            }
                                                            if (this.mStopped) {
                                                                if ((iRelayoutWindow & 1) != 0) {
                                                                    z16 = true;
                                                                } else {
                                                                    z16 = false;
                                                                }
                                                                if (!ensureTouchModeLocally(z16)) {
                                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    measuredWidth = view2.getMeasuredWidth();
                                                                    measuredHeight = view2.getMeasuredHeight();
                                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    } else {
                                                                        z17 = false;
                                                                    }
                                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    }
                                                                    if (z17) {
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    measuredWidth = view2.getMeasuredWidth();
                                                                    measuredHeight = view2.getMeasuredHeight();
                                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    } else {
                                                                        z17 = false;
                                                                    }
                                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    }
                                                                    if (z17) {
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    }
                                                                    z4 = true;
                                                                }
                                                            } else {
                                                                if ((iRelayoutWindow & 1) != 0) {
                                                                    z16 = true;
                                                                } else {
                                                                    z16 = false;
                                                                }
                                                                if (!ensureTouchModeLocally(z16)) {
                                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    measuredWidth = view2.getMeasuredWidth();
                                                                    measuredHeight = view2.getMeasuredHeight();
                                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    } else {
                                                                        z17 = false;
                                                                    }
                                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    }
                                                                    if (z17) {
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    measuredWidth = view2.getMeasuredWidth();
                                                                    measuredHeight = view2.getMeasuredHeight();
                                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    } else {
                                                                        z17 = false;
                                                                    }
                                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                        z17 = true;
                                                                    }
                                                                    if (z17) {
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                    }
                                                                    z4 = true;
                                                                }
                                                            }
                                                            i5 = iRelayoutWindow;
                                                            if (z4) {
                                                                z30 = false;
                                                            } else {
                                                                z30 = false;
                                                            }
                                                            if (z30) {
                                                                z31 = true;
                                                            } else {
                                                                z31 = true;
                                                            }
                                                            if (z30) {
                                                                performLayout(layoutParams3, this.mWidth, this.mHeight);
                                                                if ((view2.mPrivateFlags & 512) != 0) {
                                                                    view2.getLocationInWindow(this.mTmpLocation);
                                                                    Region region = this.mTransparentRegion;
                                                                    int[] iArr = this.mTmpLocation;
                                                                    region.set(iArr[0], iArr[1], (iArr[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                                                    view2.gatherTransparentRegion(this.mTransparentRegion);
                                                                    translator2 = this.mTranslator;
                                                                    if (translator2 != null) {
                                                                        translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                                                    }
                                                                    if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                                                        this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                                                        this.mFullRedrawNeeded = true;
                                                                        try {
                                                                            this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                                                        } catch (RemoteException unused2) {
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            if (z31) {
                                                                this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                                                this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                                            }
                                                            if (z8) {
                                                                internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                                                internalInsetsInfo.reset();
                                                                this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                                                this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                                                if (!z10) {
                                                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                                                    translator = this.mTranslator;
                                                                    if (translator != null) {
                                                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                    } else {
                                                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                    }
                                                                    try {
                                                                        this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                    } catch (RemoteException unused3) {
                                                                    }
                                                                } else {
                                                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                                                    translator = this.mTranslator;
                                                                    if (translator != null) {
                                                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                    } else {
                                                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                    }
                                                                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                }
                                                            }
                                                            if (this.mFirst) {
                                                                if (!sAlwaysAssignFocus) {
                                                                    view = this.mView;
                                                                    if (view != null) {
                                                                        this.mView.restoreDefaultFocus();
                                                                    }
                                                                } else {
                                                                    view = this.mView;
                                                                    if (view != null) {
                                                                        this.mView.restoreDefaultFocus();
                                                                    }
                                                                }
                                                            }
                                                            z32 = z42 ? true : true;
                                                            if (this.mAttachInfo.mHasWindowFocus) {
                                                                z33 = false;
                                                            } else {
                                                                z33 = false;
                                                            }
                                                            if (z33) {
                                                                z34 = false;
                                                            } else {
                                                                z34 = false;
                                                            }
                                                            if (z34) {
                                                                this.mLostWindowFocus = false;
                                                            } else if (!z33) {
                                                                this.mLostWindowFocus = true;
                                                            }
                                                            if (!z32) {
                                                                layoutParams2 = this.mWindowAttributes;
                                                                if (layoutParams2 == null) {
                                                                    z35 = false;
                                                                } else {
                                                                    z35 = true;
                                                                }
                                                                if (!z35) {
                                                                    view2.sendAccessibilityEvent(32);
                                                                }
                                                            } else {
                                                                layoutParams2 = this.mWindowAttributes;
                                                                if (layoutParams2 == null) {
                                                                    z35 = false;
                                                                } else {
                                                                    z35 = true;
                                                                }
                                                                if (!z35) {
                                                                    view2.sendAccessibilityEvent(32);
                                                                }
                                                            }
                                                            this.mFirst = false;
                                                            this.mWillDrawSoon = false;
                                                            this.mNewSurfaceNeeded = false;
                                                            this.mActivityRelaunched = false;
                                                            this.mViewVisibility = hostVisibility;
                                                            this.mHadWindowFocus = z33;
                                                            if (z33) {
                                                                z36 = true;
                                                            } else {
                                                                z36 = true;
                                                            }
                                                            if ((i5 & 2) != 0) {
                                                                reportNextDraw();
                                                            }
                                                            if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                                                z37 = z36;
                                                            } else {
                                                                z37 = z36;
                                                            }
                                                            if (!z37) {
                                                                if (z9) {
                                                                    scheduleTraversals();
                                                                } else {
                                                                    arrayList = this.mPendingTransitions;
                                                                    if (arrayList != null) {
                                                                        for (i10 = 0; i10 < this.mPendingTransitions.size(); i10++) {
                                                                            this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                        }
                                                                        this.mPendingTransitions.clear();
                                                                    }
                                                                }
                                                            } else if (z9) {
                                                                scheduleTraversals();
                                                            } else {
                                                                arrayList = this.mPendingTransitions;
                                                                if (arrayList != null) {
                                                                    while (i10 < this.mPendingTransitions.size()) {
                                                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                    }
                                                                    this.mPendingTransitions.clear();
                                                                }
                                                            }
                                                            this.mIsInTraversal = false;
                                                        }
                                                    } else {
                                                        z14 = z14;
                                                    }
                                                    if (z18) {
                                                        r14 = z23;
                                                        this.mAttachInfo.mOverscanInsets.set(this.mPendingOverscanInsets);
                                                        z14 = true;
                                                    }
                                                    if (z20) {
                                                        this.mAttachInfo.mStableInsets.set(this.mPendingStableInsets);
                                                        z14 = true;
                                                    }
                                                    if (z21) {
                                                        this.mAttachInfo.mDisplayCutout.set(this.mPendingDisplayCutout);
                                                        z14 = true;
                                                    }
                                                    if (z24) {
                                                        this.mAttachInfo.mAlwaysConsumeNavBar = this.mPendingAlwaysConsumeNavBar;
                                                        z14 = true;
                                                    }
                                                    if (!z14 || this.mLastSystemUiVisibility != this.mAttachInfo.mSystemUiVisibility || this.mApplyInsetsRequested || this.mLastOverscanRequested != this.mAttachInfo.mOverscanRequested || z46) {
                                                        this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                                                        this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                                                        this.mAttachInfo.mOutsets.set(this.mPendingOutsets);
                                                        this.mApplyInsetsRequested = false;
                                                        dispatchApplyInsets(view2);
                                                    }
                                                    if (z19) {
                                                        this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
                                                    }
                                                    try {
                                                        try {
                                                            if (!zIsValid) {
                                                                if (this.mSurface.isValid()) {
                                                                    try {
                                                                        this.mFullRedrawNeeded = true;
                                                                        this.mPreviousTransparentRegion.setEmpty();
                                                                        if (this.mAttachInfo.mThreadedRenderer != null) {
                                                                            try {
                                                                                zInitialize = this.mAttachInfo.mThreadedRenderer.initialize(this.mSurface);
                                                                                if (zInitialize) {
                                                                                    try {
                                                                                        try {
                                                                                            if ((view2.mPrivateFlags & 512) == 0) {
                                                                                                this.mSurface.allocateBuffers();
                                                                                            }
                                                                                        } catch (Surface.OutOfResourcesException e) {
                                                                                            e = e;
                                                                                            handleOutOfResourcesException(e);
                                                                                            return;
                                                                                        }
                                                                                    } catch (RemoteException unused4) {
                                                                                        z12 = zInitialize;
                                                                                        r1 = obj;
                                                                                        z2 = z15;
                                                                                        z11 = true;
                                                                                        z15 = z2;
                                                                                        r2 = r1;
                                                                                        this.mAttachInfo.mWindowLeft = r2.left;
                                                                                        this.mAttachInfo.mWindowTop = r2.top;
                                                                                        if (this.mWidth == r2.width()) {
                                                                                            this.mWidth = r2.width();
                                                                                            this.mHeight = r2.height();
                                                                                        } else {
                                                                                            this.mWidth = r2.width();
                                                                                            this.mHeight = r2.height();
                                                                                        }
                                                                                        if (this.mSurfaceHolder != null) {
                                                                                            if (this.mSurface.isValid()) {
                                                                                                this.mSurfaceHolder.mSurface = this.mSurface;
                                                                                            }
                                                                                            this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                                                            this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                                            if (this.mSurface.isValid()) {
                                                                                                if (!zIsValid) {
                                                                                                    this.mSurfaceHolder.ungetCallbacks();
                                                                                                    this.mIsCreating = true;
                                                                                                    callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                                                    if (callbacks3 != null) {
                                                                                                        while (i8 < r2) {
                                                                                                            callback.surfaceCreated(this.mSurfaceHolder);
                                                                                                        }
                                                                                                    }
                                                                                                    z15 = true;
                                                                                                }
                                                                                                if (z15) {
                                                                                                    while (i7 < r2) {
                                                                                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                                                    }
                                                                                                } else {
                                                                                                    while (i7 < r2) {
                                                                                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                                                    }
                                                                                                }
                                                                                                this.mIsCreating = false;
                                                                                            } else if (zIsValid) {
                                                                                                this.mSurfaceHolder.ungetCallbacks();
                                                                                                callbacks = this.mSurfaceHolder.getCallbacks();
                                                                                                if (callbacks != null) {
                                                                                                    while (i6 < r2) {
                                                                                                        callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                                                                    }
                                                                                                }
                                                                                                this.mSurfaceHolder.mSurfaceLock.lock();
                                                                                                this.mSurfaceHolder.mSurface = new Surface();
                                                                                                this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                                            }
                                                                                        }
                                                                                        threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                                                        if (threadedRenderer != null) {
                                                                                            threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                                                            this.mNeedsRendererSetup = false;
                                                                                        }
                                                                                        if (this.mStopped) {
                                                                                            if ((iRelayoutWindow & 1) != 0) {
                                                                                                z16 = true;
                                                                                            } else {
                                                                                                z16 = false;
                                                                                            }
                                                                                            if (!ensureTouchModeLocally(z16)) {
                                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                } else {
                                                                                                    z17 = false;
                                                                                                }
                                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                }
                                                                                                if (z17) {
                                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                }
                                                                                                z4 = true;
                                                                                            } else {
                                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                } else {
                                                                                                    z17 = false;
                                                                                                }
                                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                }
                                                                                                if (z17) {
                                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                }
                                                                                                z4 = true;
                                                                                            }
                                                                                        } else {
                                                                                            if ((iRelayoutWindow & 1) != 0) {
                                                                                                z16 = true;
                                                                                            } else {
                                                                                                z16 = false;
                                                                                            }
                                                                                            if (!ensureTouchModeLocally(z16)) {
                                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                } else {
                                                                                                    z17 = false;
                                                                                                }
                                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                }
                                                                                                if (z17) {
                                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                }
                                                                                                z4 = true;
                                                                                            } else {
                                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                } else {
                                                                                                    z17 = false;
                                                                                                }
                                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                                    z17 = true;
                                                                                                }
                                                                                                if (z17) {
                                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                                }
                                                                                                z4 = true;
                                                                                            }
                                                                                        }
                                                                                        i5 = iRelayoutWindow;
                                                                                        if (z4) {
                                                                                            z30 = false;
                                                                                        } else {
                                                                                            z30 = false;
                                                                                        }
                                                                                        if (z30) {
                                                                                            z31 = true;
                                                                                        } else {
                                                                                            z31 = true;
                                                                                        }
                                                                                        if (z30) {
                                                                                            performLayout(layoutParams3, this.mWidth, this.mHeight);
                                                                                            if ((view2.mPrivateFlags & 512) != 0) {
                                                                                                view2.getLocationInWindow(this.mTmpLocation);
                                                                                                Region region2 = this.mTransparentRegion;
                                                                                                int[] iArr2 = this.mTmpLocation;
                                                                                                region2.set(iArr2[0], iArr2[1], (iArr2[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                                                                                view2.gatherTransparentRegion(this.mTransparentRegion);
                                                                                                translator2 = this.mTranslator;
                                                                                                if (translator2 != null) {
                                                                                                    translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                                                                                }
                                                                                                if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                                                                                    this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                                                                                    this.mFullRedrawNeeded = true;
                                                                                                    this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        if (z31) {
                                                                                            this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                                                                            this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                                                                        }
                                                                                        if (z8) {
                                                                                            internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                                                                            internalInsetsInfo.reset();
                                                                                            this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                                                                            this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                                                                            if (!z10) {
                                                                                                this.mLastGivenInsets.set(internalInsetsInfo);
                                                                                                translator = this.mTranslator;
                                                                                                if (translator != null) {
                                                                                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                                                } else {
                                                                                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                                                }
                                                                                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                                            } else {
                                                                                                this.mLastGivenInsets.set(internalInsetsInfo);
                                                                                                translator = this.mTranslator;
                                                                                                if (translator != null) {
                                                                                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                                                } else {
                                                                                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                                                }
                                                                                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                                            }
                                                                                        }
                                                                                        if (this.mFirst) {
                                                                                            if (!sAlwaysAssignFocus) {
                                                                                                view = this.mView;
                                                                                                if (view != null) {
                                                                                                    this.mView.restoreDefaultFocus();
                                                                                                }
                                                                                            } else {
                                                                                                view = this.mView;
                                                                                                if (view != null) {
                                                                                                    this.mView.restoreDefaultFocus();
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        if (z42) {
                                                                                        }
                                                                                        if (this.mAttachInfo.mHasWindowFocus) {
                                                                                            z33 = false;
                                                                                        } else {
                                                                                            z33 = false;
                                                                                        }
                                                                                        if (z33) {
                                                                                            z34 = false;
                                                                                        } else {
                                                                                            z34 = false;
                                                                                        }
                                                                                        if (z34) {
                                                                                            this.mLostWindowFocus = false;
                                                                                        } else if (!z33) {
                                                                                            this.mLostWindowFocus = true;
                                                                                        }
                                                                                        if (!z32) {
                                                                                            layoutParams2 = this.mWindowAttributes;
                                                                                            if (layoutParams2 == null) {
                                                                                                z35 = false;
                                                                                            } else {
                                                                                                z35 = true;
                                                                                            }
                                                                                            if (!z35) {
                                                                                                view2.sendAccessibilityEvent(32);
                                                                                            }
                                                                                        } else {
                                                                                            layoutParams2 = this.mWindowAttributes;
                                                                                            if (layoutParams2 == null) {
                                                                                                z35 = false;
                                                                                            } else {
                                                                                                z35 = true;
                                                                                            }
                                                                                            if (!z35) {
                                                                                                view2.sendAccessibilityEvent(32);
                                                                                            }
                                                                                        }
                                                                                        this.mFirst = false;
                                                                                        this.mWillDrawSoon = false;
                                                                                        this.mNewSurfaceNeeded = false;
                                                                                        this.mActivityRelaunched = false;
                                                                                        this.mViewVisibility = hostVisibility;
                                                                                        this.mHadWindowFocus = z33;
                                                                                        if (z33) {
                                                                                            z36 = true;
                                                                                        } else {
                                                                                            z36 = true;
                                                                                        }
                                                                                        if ((i5 & 2) != 0) {
                                                                                            reportNextDraw();
                                                                                        }
                                                                                        if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                                                                            z37 = z36;
                                                                                        } else {
                                                                                            z37 = z36;
                                                                                        }
                                                                                        if (!z37) {
                                                                                            if (z9) {
                                                                                                scheduleTraversals();
                                                                                            } else {
                                                                                                arrayList = this.mPendingTransitions;
                                                                                                if (arrayList != null) {
                                                                                                    while (i10 < this.mPendingTransitions.size()) {
                                                                                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                                                    }
                                                                                                    this.mPendingTransitions.clear();
                                                                                                }
                                                                                            }
                                                                                        } else if (z9) {
                                                                                            scheduleTraversals();
                                                                                        } else {
                                                                                            arrayList = this.mPendingTransitions;
                                                                                            if (arrayList != null) {
                                                                                                while (i10 < this.mPendingTransitions.size()) {
                                                                                                    this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                                                }
                                                                                                this.mPendingTransitions.clear();
                                                                                            }
                                                                                        }
                                                                                        this.mIsInTraversal = false;
                                                                                    }
                                                                                }
                                                                                z12 = zInitialize;
                                                                                z11 = true;
                                                                            } catch (Surface.OutOfResourcesException e2) {
                                                                                e = e2;
                                                                                zInitialize = false;
                                                                            }
                                                                        } else {
                                                                            z11 = true;
                                                                        }
                                                                        if ((iRelayoutWindow & 16) != 0) {
                                                                            z25 = true;
                                                                        } else {
                                                                            z25 = false;
                                                                        }
                                                                        if ((iRelayoutWindow & 8) != 0) {
                                                                            z26 = true;
                                                                        } else {
                                                                            z26 = false;
                                                                        }
                                                                        if (!z25 || z26) {
                                                                            z27 = true;
                                                                        } else {
                                                                            z27 = false;
                                                                        }
                                                                        if (this.mDragResizing != z27) {
                                                                            if (z27) {
                                                                                if (z25) {
                                                                                    i9 = 0;
                                                                                } else {
                                                                                    i9 = 1;
                                                                                }
                                                                                this.mResizeMode = i9;
                                                                                startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                                                                            } else {
                                                                                endDragResizing();
                                                                            }
                                                                        }
                                                                        if (!this.mUseMTRenderer) {
                                                                            if (z27) {
                                                                                this.mCanvasOffsetX = this.mWinFrame.left;
                                                                                this.mCanvasOffsetY = this.mWinFrame.top;
                                                                            } else {
                                                                                this.mCanvasOffsetY = 0;
                                                                                this.mCanvasOffsetX = 0;
                                                                            }
                                                                        }
                                                                        r2 = obj;
                                                                    } catch (RemoteException unused5) {
                                                                        obj4 = obj;
                                                                        z2 = z15;
                                                                        z11 = true;
                                                                        z12 = false;
                                                                        r1 = obj4;
                                                                        z15 = z2;
                                                                        r2 = r1;
                                                                        this.mAttachInfo.mWindowLeft = r2.left;
                                                                        this.mAttachInfo.mWindowTop = r2.top;
                                                                        if (this.mWidth == r2.width()) {
                                                                            this.mWidth = r2.width();
                                                                            this.mHeight = r2.height();
                                                                        } else {
                                                                            this.mWidth = r2.width();
                                                                            this.mHeight = r2.height();
                                                                        }
                                                                        if (this.mSurfaceHolder != null) {
                                                                            if (this.mSurface.isValid()) {
                                                                                this.mSurfaceHolder.mSurface = this.mSurface;
                                                                            }
                                                                            this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                                            this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                            if (this.mSurface.isValid()) {
                                                                                if (!zIsValid) {
                                                                                    this.mSurfaceHolder.ungetCallbacks();
                                                                                    this.mIsCreating = true;
                                                                                    callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                                    if (callbacks3 != null) {
                                                                                        while (i8 < r2) {
                                                                                            callback.surfaceCreated(this.mSurfaceHolder);
                                                                                        }
                                                                                    }
                                                                                    z15 = true;
                                                                                }
                                                                                if (z15) {
                                                                                    while (i7 < r2) {
                                                                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                                    }
                                                                                } else {
                                                                                    while (i7 < r2) {
                                                                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                                    }
                                                                                }
                                                                                this.mIsCreating = false;
                                                                            } else if (zIsValid) {
                                                                                this.mSurfaceHolder.ungetCallbacks();
                                                                                callbacks = this.mSurfaceHolder.getCallbacks();
                                                                                if (callbacks != null) {
                                                                                    while (i6 < r2) {
                                                                                        callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                                                    }
                                                                                }
                                                                                this.mSurfaceHolder.mSurfaceLock.lock();
                                                                                this.mSurfaceHolder.mSurface = new Surface();
                                                                                this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                            }
                                                                        }
                                                                        threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                                        if (threadedRenderer != null) {
                                                                            threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                                            this.mNeedsRendererSetup = false;
                                                                        }
                                                                        if (this.mStopped) {
                                                                            if ((iRelayoutWindow & 1) != 0) {
                                                                                z16 = true;
                                                                            } else {
                                                                                z16 = false;
                                                                            }
                                                                            if (!ensureTouchModeLocally(z16)) {
                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                } else {
                                                                                    z17 = false;
                                                                                }
                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                }
                                                                                if (z17) {
                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                }
                                                                                z4 = true;
                                                                            } else {
                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                } else {
                                                                                    z17 = false;
                                                                                }
                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                }
                                                                                if (z17) {
                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                }
                                                                                z4 = true;
                                                                            }
                                                                        } else {
                                                                            if ((iRelayoutWindow & 1) != 0) {
                                                                                z16 = true;
                                                                            } else {
                                                                                z16 = false;
                                                                            }
                                                                            if (!ensureTouchModeLocally(z16)) {
                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                } else {
                                                                                    z17 = false;
                                                                                }
                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                }
                                                                                if (z17) {
                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                }
                                                                                z4 = true;
                                                                            } else {
                                                                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                measuredWidth = view2.getMeasuredWidth();
                                                                                measuredHeight = view2.getMeasuredHeight();
                                                                                if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                } else {
                                                                                    z17 = false;
                                                                                }
                                                                                if (layoutParams3.verticalWeight > 0.0f) {
                                                                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                    z17 = true;
                                                                                }
                                                                                if (z17) {
                                                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                                }
                                                                                z4 = true;
                                                                            }
                                                                        }
                                                                        i5 = iRelayoutWindow;
                                                                        if (z4) {
                                                                            z30 = false;
                                                                        } else {
                                                                            z30 = false;
                                                                        }
                                                                        if (z30) {
                                                                            z31 = true;
                                                                        } else {
                                                                            z31 = true;
                                                                        }
                                                                        if (z30) {
                                                                            performLayout(layoutParams3, this.mWidth, this.mHeight);
                                                                            if ((view2.mPrivateFlags & 512) != 0) {
                                                                                view2.getLocationInWindow(this.mTmpLocation);
                                                                                Region region3 = this.mTransparentRegion;
                                                                                int[] iArr3 = this.mTmpLocation;
                                                                                region3.set(iArr3[0], iArr3[1], (iArr3[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                                                                view2.gatherTransparentRegion(this.mTransparentRegion);
                                                                                translator2 = this.mTranslator;
                                                                                if (translator2 != null) {
                                                                                    translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                                                                }
                                                                                if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                                                                    this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                                                                    this.mFullRedrawNeeded = true;
                                                                                    this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                                                                }
                                                                            }
                                                                        }
                                                                        if (z31) {
                                                                            this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                                                            this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                                                        }
                                                                        if (z8) {
                                                                            internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                                                            internalInsetsInfo.reset();
                                                                            this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                                                            this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                                                            if (!z10) {
                                                                                this.mLastGivenInsets.set(internalInsetsInfo);
                                                                                translator = this.mTranslator;
                                                                                if (translator != null) {
                                                                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                                } else {
                                                                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                                }
                                                                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                            } else {
                                                                                this.mLastGivenInsets.set(internalInsetsInfo);
                                                                                translator = this.mTranslator;
                                                                                if (translator != null) {
                                                                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                                                } else {
                                                                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                                                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                                                }
                                                                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                                            }
                                                                        }
                                                                        if (this.mFirst) {
                                                                            if (!sAlwaysAssignFocus) {
                                                                                view = this.mView;
                                                                                if (view != null) {
                                                                                    this.mView.restoreDefaultFocus();
                                                                                }
                                                                            } else {
                                                                                view = this.mView;
                                                                                if (view != null) {
                                                                                    this.mView.restoreDefaultFocus();
                                                                                }
                                                                            }
                                                                        }
                                                                        if (z42) {
                                                                        }
                                                                        if (this.mAttachInfo.mHasWindowFocus) {
                                                                            z33 = false;
                                                                        } else {
                                                                            z33 = false;
                                                                        }
                                                                        if (z33) {
                                                                            z34 = false;
                                                                        } else {
                                                                            z34 = false;
                                                                        }
                                                                        if (z34) {
                                                                            this.mLostWindowFocus = false;
                                                                        } else if (!z33) {
                                                                            this.mLostWindowFocus = true;
                                                                        }
                                                                        if (!z32) {
                                                                            layoutParams2 = this.mWindowAttributes;
                                                                            if (layoutParams2 == null) {
                                                                                z35 = false;
                                                                            } else {
                                                                                z35 = true;
                                                                            }
                                                                            if (!z35) {
                                                                                view2.sendAccessibilityEvent(32);
                                                                            }
                                                                        } else {
                                                                            layoutParams2 = this.mWindowAttributes;
                                                                            if (layoutParams2 == null) {
                                                                                z35 = false;
                                                                            } else {
                                                                                z35 = true;
                                                                            }
                                                                            if (!z35) {
                                                                                view2.sendAccessibilityEvent(32);
                                                                            }
                                                                        }
                                                                        this.mFirst = false;
                                                                        this.mWillDrawSoon = false;
                                                                        this.mNewSurfaceNeeded = false;
                                                                        this.mActivityRelaunched = false;
                                                                        this.mViewVisibility = hostVisibility;
                                                                        this.mHadWindowFocus = z33;
                                                                        if (z33) {
                                                                            z36 = true;
                                                                        } else {
                                                                            z36 = true;
                                                                        }
                                                                        if ((i5 & 2) != 0) {
                                                                            reportNextDraw();
                                                                        }
                                                                        if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                                                            z37 = z36;
                                                                        } else {
                                                                            z37 = z36;
                                                                        }
                                                                        if (!z37) {
                                                                            if (z9) {
                                                                                scheduleTraversals();
                                                                            } else {
                                                                                arrayList = this.mPendingTransitions;
                                                                                if (arrayList != null) {
                                                                                    while (i10 < this.mPendingTransitions.size()) {
                                                                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                                    }
                                                                                    this.mPendingTransitions.clear();
                                                                                }
                                                                            }
                                                                        } else if (z9) {
                                                                            scheduleTraversals();
                                                                        } else {
                                                                            arrayList = this.mPendingTransitions;
                                                                            if (arrayList != null) {
                                                                                while (i10 < this.mPendingTransitions.size()) {
                                                                                    this.mPendingTransitions.get(i10).endChangingAnimations();
                                                                                }
                                                                                this.mPendingTransitions.clear();
                                                                            }
                                                                        }
                                                                        this.mIsInTraversal = false;
                                                                    }
                                                                    this.mAttachInfo.mWindowLeft = r2.left;
                                                                    this.mAttachInfo.mWindowTop = r2.top;
                                                                    if (this.mWidth == r2.width() || this.mHeight != r2.height()) {
                                                                        this.mWidth = r2.width();
                                                                        this.mHeight = r2.height();
                                                                    }
                                                                    if (this.mSurfaceHolder != null) {
                                                                        if (this.mSurface.isValid()) {
                                                                            this.mSurfaceHolder.mSurface = this.mSurface;
                                                                        }
                                                                        this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                        if (this.mSurface.isValid()) {
                                                                            if (!zIsValid) {
                                                                                this.mSurfaceHolder.ungetCallbacks();
                                                                                this.mIsCreating = true;
                                                                                callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                                if (callbacks3 != null) {
                                                                                    while (i8 < r2) {
                                                                                        callback.surfaceCreated(this.mSurfaceHolder);
                                                                                    }
                                                                                }
                                                                                z15 = true;
                                                                            }
                                                                            if ((z15 || generationId != this.mSurface.getGenerationId()) && (callbacks2 = this.mSurfaceHolder.getCallbacks()) != null) {
                                                                                while (i7 < r2) {
                                                                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                                }
                                                                            }
                                                                            this.mIsCreating = false;
                                                                        } else if (zIsValid) {
                                                                            this.mSurfaceHolder.ungetCallbacks();
                                                                            callbacks = this.mSurfaceHolder.getCallbacks();
                                                                            if (callbacks != null) {
                                                                                while (i6 < r2) {
                                                                                    callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                                                }
                                                                            }
                                                                            this.mSurfaceHolder.mSurfaceLock.lock();
                                                                            this.mSurfaceHolder.mSurface = new Surface();
                                                                            this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                        }
                                                                    }
                                                                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                                    if (threadedRenderer != null && threadedRenderer.isEnabled() && (z12 || this.mWidth != threadedRenderer.getWidth() || this.mHeight != threadedRenderer.getHeight() || this.mNeedsRendererSetup)) {
                                                                        threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                                        this.mNeedsRendererSetup = false;
                                                                    }
                                                                    if (this.mStopped || this.mReportNextDraw) {
                                                                        if ((iRelayoutWindow & 1) != 0) {
                                                                            z16 = true;
                                                                        } else {
                                                                            z16 = false;
                                                                        }
                                                                        if (!ensureTouchModeLocally(z16) || this.mWidth != view2.getMeasuredWidth() || this.mHeight != view2.getMeasuredHeight() || z14 || z13) {
                                                                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                            measuredWidth = view2.getMeasuredWidth();
                                                                            measuredHeight = view2.getMeasuredHeight();
                                                                            if (layoutParams3.horizontalWeight > 0.0f) {
                                                                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                                z17 = true;
                                                                            } else {
                                                                                z17 = false;
                                                                            }
                                                                            if (layoutParams3.verticalWeight > 0.0f) {
                                                                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                                z17 = true;
                                                                            }
                                                                            if (z17) {
                                                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                            }
                                                                            z4 = true;
                                                                        }
                                                                    }
                                                                    i5 = iRelayoutWindow;
                                                                }
                                                                z12 = false;
                                                                if ((iRelayoutWindow & 16) != 0) {
                                                                    z25 = true;
                                                                } else {
                                                                    z25 = false;
                                                                }
                                                                if ((iRelayoutWindow & 8) != 0) {
                                                                    z26 = true;
                                                                } else {
                                                                    z26 = false;
                                                                }
                                                                if (z25) {
                                                                    z27 = true;
                                                                } else {
                                                                    z27 = true;
                                                                }
                                                                if (this.mDragResizing != z27) {
                                                                    if (z27) {
                                                                        if (z25) {
                                                                            i9 = 0;
                                                                        } else {
                                                                            i9 = 1;
                                                                        }
                                                                        this.mResizeMode = i9;
                                                                        startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                                                                    } else {
                                                                        endDragResizing();
                                                                    }
                                                                }
                                                                if (!this.mUseMTRenderer) {
                                                                    if (z27) {
                                                                        this.mCanvasOffsetX = this.mWinFrame.left;
                                                                        this.mCanvasOffsetY = this.mWinFrame.top;
                                                                    } else {
                                                                        this.mCanvasOffsetY = 0;
                                                                        this.mCanvasOffsetX = 0;
                                                                    }
                                                                }
                                                                r2 = obj;
                                                                this.mAttachInfo.mWindowLeft = r2.left;
                                                                this.mAttachInfo.mWindowTop = r2.top;
                                                                if (this.mWidth == r2.width()) {
                                                                    this.mWidth = r2.width();
                                                                    this.mHeight = r2.height();
                                                                } else {
                                                                    this.mWidth = r2.width();
                                                                    this.mHeight = r2.height();
                                                                }
                                                                if (this.mSurfaceHolder != null) {
                                                                    if (this.mSurface.isValid()) {
                                                                        this.mSurfaceHolder.mSurface = this.mSurface;
                                                                    }
                                                                    this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                                    this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                    if (this.mSurface.isValid()) {
                                                                        if (!zIsValid) {
                                                                            this.mSurfaceHolder.ungetCallbacks();
                                                                            this.mIsCreating = true;
                                                                            callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                            if (callbacks3 != null) {
                                                                                while (i8 < r2) {
                                                                                    callback.surfaceCreated(this.mSurfaceHolder);
                                                                                }
                                                                            }
                                                                            z15 = true;
                                                                        }
                                                                        if (z15) {
                                                                            while (i7 < r2) {
                                                                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                            }
                                                                        } else {
                                                                            while (i7 < r2) {
                                                                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                                            }
                                                                        }
                                                                        this.mIsCreating = false;
                                                                    } else if (zIsValid) {
                                                                        this.mSurfaceHolder.ungetCallbacks();
                                                                        callbacks = this.mSurfaceHolder.getCallbacks();
                                                                        if (callbacks != null) {
                                                                            while (i6 < r2) {
                                                                                callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                                            }
                                                                        }
                                                                        this.mSurfaceHolder.mSurfaceLock.lock();
                                                                        this.mSurfaceHolder.mSurface = new Surface();
                                                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                                                    }
                                                                }
                                                                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                                if (threadedRenderer != null) {
                                                                    threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                                    this.mNeedsRendererSetup = false;
                                                                }
                                                                if (this.mStopped) {
                                                                    if ((iRelayoutWindow & 1) != 0) {
                                                                        z16 = true;
                                                                    } else {
                                                                        z16 = false;
                                                                    }
                                                                    if (!ensureTouchModeLocally(z16)) {
                                                                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        measuredWidth = view2.getMeasuredWidth();
                                                                        measuredHeight = view2.getMeasuredHeight();
                                                                        if (layoutParams3.horizontalWeight > 0.0f) {
                                                                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        } else {
                                                                            z17 = false;
                                                                        }
                                                                        if (layoutParams3.verticalWeight > 0.0f) {
                                                                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        }
                                                                        if (z17) {
                                                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        }
                                                                        z4 = true;
                                                                    } else {
                                                                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        measuredWidth = view2.getMeasuredWidth();
                                                                        measuredHeight = view2.getMeasuredHeight();
                                                                        if (layoutParams3.horizontalWeight > 0.0f) {
                                                                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        } else {
                                                                            z17 = false;
                                                                        }
                                                                        if (layoutParams3.verticalWeight > 0.0f) {
                                                                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        }
                                                                        if (z17) {
                                                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        }
                                                                        z4 = true;
                                                                    }
                                                                } else {
                                                                    if ((iRelayoutWindow & 1) != 0) {
                                                                        z16 = true;
                                                                    } else {
                                                                        z16 = false;
                                                                    }
                                                                    if (!ensureTouchModeLocally(z16)) {
                                                                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        measuredWidth = view2.getMeasuredWidth();
                                                                        measuredHeight = view2.getMeasuredHeight();
                                                                        if (layoutParams3.horizontalWeight > 0.0f) {
                                                                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        } else {
                                                                            z17 = false;
                                                                        }
                                                                        if (layoutParams3.verticalWeight > 0.0f) {
                                                                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        }
                                                                        if (z17) {
                                                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        }
                                                                        z4 = true;
                                                                    } else {
                                                                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        measuredWidth = view2.getMeasuredWidth();
                                                                        measuredHeight = view2.getMeasuredHeight();
                                                                        if (layoutParams3.horizontalWeight > 0.0f) {
                                                                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        } else {
                                                                            z17 = false;
                                                                        }
                                                                        if (layoutParams3.verticalWeight > 0.0f) {
                                                                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                                            z17 = true;
                                                                        }
                                                                        if (z17) {
                                                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                                        }
                                                                        z4 = true;
                                                                    }
                                                                }
                                                                i5 = iRelayoutWindow;
                                                            } else if (!this.mSurface.isValid()) {
                                                                if (this.mLastScrolledFocus != null) {
                                                                    this.mLastScrolledFocus.clear();
                                                                }
                                                                this.mCurScrollY = 0;
                                                                this.mScrollY = 0;
                                                                if (this.mView instanceof RootViewSurfaceTaker) {
                                                                    ((RootViewSurfaceTaker) this.mView).onRootViewScrollYChanged(0);
                                                                }
                                                                if (this.mScroller != null) {
                                                                    this.mScroller.abortAnimation();
                                                                }
                                                                if (this.mAttachInfo.mThreadedRenderer != null && this.mAttachInfo.mThreadedRenderer.isEnabled()) {
                                                                    this.mAttachInfo.mThreadedRenderer.destroy();
                                                                }
                                                            } else if ((generationId == this.mSurface.getGenerationId() || z22 || z45) && this.mSurfaceHolder == null && this.mAttachInfo.mThreadedRenderer != null) {
                                                                this.mFullRedrawNeeded = true;
                                                                try {
                                                                    this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
                                                                } catch (Surface.OutOfResourcesException e3) {
                                                                    handleOutOfResourcesException(e3);
                                                                    return;
                                                                }
                                                            }
                                                            if (this.mDragResizing != z27) {
                                                                if (z27) {
                                                                    if (z25) {
                                                                        i9 = 0;
                                                                    } else {
                                                                        i9 = 1;
                                                                    }
                                                                    this.mResizeMode = i9;
                                                                    startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                                                                } else {
                                                                    endDragResizing();
                                                                }
                                                            }
                                                            if (!this.mUseMTRenderer) {
                                                                if (z27) {
                                                                    this.mCanvasOffsetX = this.mWinFrame.left;
                                                                    this.mCanvasOffsetY = this.mWinFrame.top;
                                                                } else {
                                                                    this.mCanvasOffsetY = 0;
                                                                    this.mCanvasOffsetX = 0;
                                                                }
                                                            }
                                                            r2 = obj;
                                                        } catch (RemoteException unused6) {
                                                            r3 = r14;
                                                            z2 = z15;
                                                            r1 = r3;
                                                            z15 = z2;
                                                            r2 = r1;
                                                        }
                                                    } catch (RemoteException unused7) {
                                                        r3 = obj;
                                                    }
                                                    z11 = false;
                                                    z12 = false;
                                                    if ((iRelayoutWindow & 16) != 0) {
                                                        z25 = true;
                                                    } else {
                                                        z25 = false;
                                                    }
                                                    if ((iRelayoutWindow & 8) != 0) {
                                                        z26 = true;
                                                    } else {
                                                        z26 = false;
                                                    }
                                                    if (z25) {
                                                        z27 = true;
                                                    } else {
                                                        z27 = true;
                                                    }
                                                } catch (RemoteException unused8) {
                                                    z14 = z14;
                                                }
                                            } catch (RemoteException unused9) {
                                                z14 = z14;
                                                z42 = z42;
                                            }
                                        } catch (RemoteException unused10) {
                                            z42 = z42;
                                            obj3 = obj;
                                            z11 = false;
                                            obj4 = obj3;
                                            z12 = false;
                                            r1 = obj4;
                                            z15 = z2;
                                            r2 = r1;
                                            this.mAttachInfo.mWindowLeft = r2.left;
                                            this.mAttachInfo.mWindowTop = r2.top;
                                            if (this.mWidth == r2.width()) {
                                                this.mWidth = r2.width();
                                                this.mHeight = r2.height();
                                            } else {
                                                this.mWidth = r2.width();
                                                this.mHeight = r2.height();
                                            }
                                            if (this.mSurfaceHolder != null) {
                                                if (this.mSurface.isValid()) {
                                                    this.mSurfaceHolder.mSurface = this.mSurface;
                                                }
                                                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                                this.mSurfaceHolder.mSurfaceLock.unlock();
                                                if (this.mSurface.isValid()) {
                                                    if (!zIsValid) {
                                                        this.mSurfaceHolder.ungetCallbacks();
                                                        this.mIsCreating = true;
                                                        callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                        if (callbacks3 != null) {
                                                            while (i8 < r2) {
                                                                callback.surfaceCreated(this.mSurfaceHolder);
                                                            }
                                                        }
                                                        z15 = true;
                                                    }
                                                    if (z15) {
                                                        while (i7 < r2) {
                                                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                        }
                                                    } else {
                                                        while (i7 < r2) {
                                                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                        }
                                                    }
                                                    this.mIsCreating = false;
                                                } else if (zIsValid) {
                                                    this.mSurfaceHolder.ungetCallbacks();
                                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                                    if (callbacks != null) {
                                                        while (i6 < r2) {
                                                            callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                        }
                                                    }
                                                    this.mSurfaceHolder.mSurfaceLock.lock();
                                                    this.mSurfaceHolder.mSurface = new Surface();
                                                    this.mSurfaceHolder.mSurfaceLock.unlock();
                                                }
                                            }
                                            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                            if (threadedRenderer != null) {
                                                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                                this.mNeedsRendererSetup = false;
                                            }
                                            if (this.mStopped) {
                                                if ((iRelayoutWindow & 1) != 0) {
                                                    z16 = true;
                                                } else {
                                                    z16 = false;
                                                }
                                                if (!ensureTouchModeLocally(z16)) {
                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    measuredWidth = view2.getMeasuredWidth();
                                                    measuredHeight = view2.getMeasuredHeight();
                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                        z17 = true;
                                                    } else {
                                                        z17 = false;
                                                    }
                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                        z17 = true;
                                                    }
                                                    if (z17) {
                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    }
                                                    z4 = true;
                                                } else {
                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    measuredWidth = view2.getMeasuredWidth();
                                                    measuredHeight = view2.getMeasuredHeight();
                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                        z17 = true;
                                                    } else {
                                                        z17 = false;
                                                    }
                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                        z17 = true;
                                                    }
                                                    if (z17) {
                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    }
                                                    z4 = true;
                                                }
                                            } else {
                                                if ((iRelayoutWindow & 1) != 0) {
                                                    z16 = true;
                                                } else {
                                                    z16 = false;
                                                }
                                                if (!ensureTouchModeLocally(z16)) {
                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    measuredWidth = view2.getMeasuredWidth();
                                                    measuredHeight = view2.getMeasuredHeight();
                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                        z17 = true;
                                                    } else {
                                                        z17 = false;
                                                    }
                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                        z17 = true;
                                                    }
                                                    if (z17) {
                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    }
                                                    z4 = true;
                                                } else {
                                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    measuredWidth = view2.getMeasuredWidth();
                                                    measuredHeight = view2.getMeasuredHeight();
                                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                        z17 = true;
                                                    } else {
                                                        z17 = false;
                                                    }
                                                    if (layoutParams3.verticalWeight > 0.0f) {
                                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                        z17 = true;
                                                    }
                                                    if (z17) {
                                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                                    }
                                                    z4 = true;
                                                }
                                            }
                                            i5 = iRelayoutWindow;
                                            if (z4) {
                                                z30 = false;
                                            } else {
                                                z30 = false;
                                            }
                                            if (z30) {
                                                z31 = true;
                                            } else {
                                                z31 = true;
                                            }
                                            if (z30) {
                                                performLayout(layoutParams3, this.mWidth, this.mHeight);
                                                if ((view2.mPrivateFlags & 512) != 0) {
                                                    view2.getLocationInWindow(this.mTmpLocation);
                                                    Region region4 = this.mTransparentRegion;
                                                    int[] iArr4 = this.mTmpLocation;
                                                    region4.set(iArr4[0], iArr4[1], (iArr4[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                                    view2.gatherTransparentRegion(this.mTransparentRegion);
                                                    translator2 = this.mTranslator;
                                                    if (translator2 != null) {
                                                        translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                                    }
                                                    if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                                        this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                                        this.mFullRedrawNeeded = true;
                                                        this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                                    }
                                                }
                                            }
                                            if (z31) {
                                                this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                                this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                            }
                                            if (z8) {
                                                internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                                internalInsetsInfo.reset();
                                                this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                                this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                                if (!z10) {
                                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                                    translator = this.mTranslator;
                                                    if (translator != null) {
                                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                    } else {
                                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                    }
                                                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                } else {
                                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                                    translator = this.mTranslator;
                                                    if (translator != null) {
                                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                                    } else {
                                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                                    }
                                                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                                }
                                            }
                                            if (this.mFirst) {
                                                if (!sAlwaysAssignFocus) {
                                                    view = this.mView;
                                                    if (view != null) {
                                                        this.mView.restoreDefaultFocus();
                                                    }
                                                } else {
                                                    view = this.mView;
                                                    if (view != null) {
                                                        this.mView.restoreDefaultFocus();
                                                    }
                                                }
                                            }
                                            if (z42) {
                                            }
                                            if (this.mAttachInfo.mHasWindowFocus) {
                                                z33 = false;
                                            } else {
                                                z33 = false;
                                            }
                                            if (z33) {
                                                z34 = false;
                                            } else {
                                                z34 = false;
                                            }
                                            if (z34) {
                                                this.mLostWindowFocus = false;
                                            } else if (!z33) {
                                                this.mLostWindowFocus = true;
                                            }
                                            if (!z32) {
                                                layoutParams2 = this.mWindowAttributes;
                                                if (layoutParams2 == null) {
                                                    z35 = false;
                                                } else {
                                                    z35 = true;
                                                }
                                                if (!z35) {
                                                    view2.sendAccessibilityEvent(32);
                                                }
                                            } else {
                                                layoutParams2 = this.mWindowAttributes;
                                                if (layoutParams2 == null) {
                                                    z35 = false;
                                                } else {
                                                    z35 = true;
                                                }
                                                if (!z35) {
                                                    view2.sendAccessibilityEvent(32);
                                                }
                                            }
                                            this.mFirst = false;
                                            this.mWillDrawSoon = false;
                                            this.mNewSurfaceNeeded = false;
                                            this.mActivityRelaunched = false;
                                            this.mViewVisibility = hostVisibility;
                                            this.mHadWindowFocus = z33;
                                            if (z33) {
                                                z36 = true;
                                            } else {
                                                z36 = true;
                                            }
                                            if ((i5 & 2) != 0) {
                                                reportNextDraw();
                                            }
                                            if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                                z37 = z36;
                                            } else {
                                                z37 = z36;
                                            }
                                            if (!z37) {
                                                if (z9) {
                                                    scheduleTraversals();
                                                } else {
                                                    arrayList = this.mPendingTransitions;
                                                    if (arrayList != null) {
                                                        while (i10 < this.mPendingTransitions.size()) {
                                                            this.mPendingTransitions.get(i10).endChangingAnimations();
                                                        }
                                                        this.mPendingTransitions.clear();
                                                    }
                                                }
                                            } else if (z9) {
                                                scheduleTraversals();
                                            } else {
                                                arrayList = this.mPendingTransitions;
                                                if (arrayList != null) {
                                                    while (i10 < this.mPendingTransitions.size()) {
                                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                                    }
                                                    this.mPendingTransitions.clear();
                                                }
                                            }
                                            this.mIsInTraversal = false;
                                        }
                                    } catch (RemoteException unused11) {
                                        hostVisibility = hostVisibility;
                                    }
                                } catch (RemoteException unused12) {
                                    obj3 = obj;
                                    hostVisibility = hostVisibility;
                                    z42 = z42;
                                    z11 = false;
                                    obj4 = obj3;
                                    z12 = false;
                                    r1 = obj4;
                                    z15 = z2;
                                    r2 = r1;
                                    this.mAttachInfo.mWindowLeft = r2.left;
                                    this.mAttachInfo.mWindowTop = r2.top;
                                    if (this.mWidth == r2.width()) {
                                        this.mWidth = r2.width();
                                        this.mHeight = r2.height();
                                    } else {
                                        this.mWidth = r2.width();
                                        this.mHeight = r2.height();
                                    }
                                    if (this.mSurfaceHolder != null) {
                                        if (this.mSurface.isValid()) {
                                            this.mSurfaceHolder.mSurface = this.mSurface;
                                        }
                                        this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                        if (this.mSurface.isValid()) {
                                            if (!zIsValid) {
                                                this.mSurfaceHolder.ungetCallbacks();
                                                this.mIsCreating = true;
                                                callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                if (callbacks3 != null) {
                                                    while (i8 < r2) {
                                                        callback.surfaceCreated(this.mSurfaceHolder);
                                                    }
                                                }
                                                z15 = true;
                                            }
                                            if (z15) {
                                                while (i7 < r2) {
                                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                }
                                            } else {
                                                while (i7 < r2) {
                                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                                }
                                            }
                                            this.mIsCreating = false;
                                        } else if (zIsValid) {
                                            this.mSurfaceHolder.ungetCallbacks();
                                            callbacks = this.mSurfaceHolder.getCallbacks();
                                            if (callbacks != null) {
                                                while (i6 < r2) {
                                                    callback3.surfaceDestroyed(this.mSurfaceHolder);
                                                }
                                            }
                                            this.mSurfaceHolder.mSurfaceLock.lock();
                                            this.mSurfaceHolder.mSurface = new Surface();
                                            this.mSurfaceHolder.mSurfaceLock.unlock();
                                        }
                                    }
                                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                    if (threadedRenderer != null) {
                                        threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                        this.mNeedsRendererSetup = false;
                                    }
                                    if (this.mStopped) {
                                        if ((iRelayoutWindow & 1) != 0) {
                                            z16 = true;
                                        } else {
                                            z16 = false;
                                        }
                                        if (!ensureTouchModeLocally(z16)) {
                                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            measuredWidth = view2.getMeasuredWidth();
                                            measuredHeight = view2.getMeasuredHeight();
                                            if (layoutParams3.horizontalWeight > 0.0f) {
                                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                z17 = true;
                                            } else {
                                                z17 = false;
                                            }
                                            if (layoutParams3.verticalWeight > 0.0f) {
                                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                z17 = true;
                                            }
                                            if (z17) {
                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            }
                                            z4 = true;
                                        } else {
                                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            measuredWidth = view2.getMeasuredWidth();
                                            measuredHeight = view2.getMeasuredHeight();
                                            if (layoutParams3.horizontalWeight > 0.0f) {
                                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                z17 = true;
                                            } else {
                                                z17 = false;
                                            }
                                            if (layoutParams3.verticalWeight > 0.0f) {
                                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                z17 = true;
                                            }
                                            if (z17) {
                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            }
                                            z4 = true;
                                        }
                                    } else {
                                        if ((iRelayoutWindow & 1) != 0) {
                                            z16 = true;
                                        } else {
                                            z16 = false;
                                        }
                                        if (!ensureTouchModeLocally(z16)) {
                                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            measuredWidth = view2.getMeasuredWidth();
                                            measuredHeight = view2.getMeasuredHeight();
                                            if (layoutParams3.horizontalWeight > 0.0f) {
                                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                z17 = true;
                                            } else {
                                                z17 = false;
                                            }
                                            if (layoutParams3.verticalWeight > 0.0f) {
                                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                z17 = true;
                                            }
                                            if (z17) {
                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            }
                                            z4 = true;
                                        } else {
                                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            measuredWidth = view2.getMeasuredWidth();
                                            measuredHeight = view2.getMeasuredHeight();
                                            if (layoutParams3.horizontalWeight > 0.0f) {
                                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                                z17 = true;
                                            } else {
                                                z17 = false;
                                            }
                                            if (layoutParams3.verticalWeight > 0.0f) {
                                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                                z17 = true;
                                            }
                                            if (z17) {
                                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                            }
                                            z4 = true;
                                        }
                                    }
                                    i5 = iRelayoutWindow;
                                    if (z4) {
                                        z30 = false;
                                    } else {
                                        z30 = false;
                                    }
                                    if (z30) {
                                        z31 = true;
                                    } else {
                                        z31 = true;
                                    }
                                    if (z30) {
                                        performLayout(layoutParams3, this.mWidth, this.mHeight);
                                        if ((view2.mPrivateFlags & 512) != 0) {
                                            view2.getLocationInWindow(this.mTmpLocation);
                                            Region region5 = this.mTransparentRegion;
                                            int[] iArr5 = this.mTmpLocation;
                                            region5.set(iArr5[0], iArr5[1], (iArr5[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                            view2.gatherTransparentRegion(this.mTransparentRegion);
                                            translator2 = this.mTranslator;
                                            if (translator2 != null) {
                                                translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                            }
                                            if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                                this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                                this.mFullRedrawNeeded = true;
                                                this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                            }
                                        }
                                    }
                                    if (z31) {
                                        this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                        this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                    }
                                    if (z8) {
                                        internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                        internalInsetsInfo.reset();
                                        this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                        this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                        if (!z10) {
                                            this.mLastGivenInsets.set(internalInsetsInfo);
                                            translator = this.mTranslator;
                                            if (translator != null) {
                                                translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                            } else {
                                                translatedContentInsets = internalInsetsInfo.contentInsets;
                                                translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                            }
                                            this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                        } else {
                                            this.mLastGivenInsets.set(internalInsetsInfo);
                                            translator = this.mTranslator;
                                            if (translator != null) {
                                                translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                                translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                                translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                            } else {
                                                translatedContentInsets = internalInsetsInfo.contentInsets;
                                                translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                                translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                            }
                                            this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                        }
                                    }
                                    if (this.mFirst) {
                                        if (!sAlwaysAssignFocus) {
                                            view = this.mView;
                                            if (view != null) {
                                                this.mView.restoreDefaultFocus();
                                            }
                                        } else {
                                            view = this.mView;
                                            if (view != null) {
                                                this.mView.restoreDefaultFocus();
                                            }
                                        }
                                    }
                                    if (z42) {
                                    }
                                    if (this.mAttachInfo.mHasWindowFocus) {
                                        z33 = false;
                                    } else {
                                        z33 = false;
                                    }
                                    if (z33) {
                                        z34 = false;
                                    } else {
                                        z34 = false;
                                    }
                                    if (z34) {
                                        this.mLostWindowFocus = false;
                                    } else if (!z33) {
                                        this.mLostWindowFocus = true;
                                    }
                                    if (!z32) {
                                        layoutParams2 = this.mWindowAttributes;
                                        if (layoutParams2 == null) {
                                            z35 = false;
                                        } else {
                                            z35 = true;
                                        }
                                        if (!z35) {
                                            view2.sendAccessibilityEvent(32);
                                        }
                                    } else {
                                        layoutParams2 = this.mWindowAttributes;
                                        if (layoutParams2 == null) {
                                            z35 = false;
                                        } else {
                                            z35 = true;
                                        }
                                        if (!z35) {
                                            view2.sendAccessibilityEvent(32);
                                        }
                                    }
                                    this.mFirst = false;
                                    this.mWillDrawSoon = false;
                                    this.mNewSurfaceNeeded = false;
                                    this.mActivityRelaunched = false;
                                    this.mViewVisibility = hostVisibility;
                                    this.mHadWindowFocus = z33;
                                    if (z33) {
                                        z36 = true;
                                    } else {
                                        z36 = true;
                                    }
                                    if ((i5 & 2) != 0) {
                                        reportNextDraw();
                                    }
                                    if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                        z37 = z36;
                                    } else {
                                        z37 = z36;
                                    }
                                    if (!z37) {
                                        if (z9) {
                                            scheduleTraversals();
                                        } else {
                                            arrayList = this.mPendingTransitions;
                                            if (arrayList != null) {
                                                while (i10 < this.mPendingTransitions.size()) {
                                                    this.mPendingTransitions.get(i10).endChangingAnimations();
                                                }
                                                this.mPendingTransitions.clear();
                                            }
                                        }
                                    } else if (z9) {
                                        scheduleTraversals();
                                    } else {
                                        arrayList = this.mPendingTransitions;
                                        if (arrayList != null) {
                                            while (i10 < this.mPendingTransitions.size()) {
                                                this.mPendingTransitions.get(i10).endChangingAnimations();
                                            }
                                            this.mPendingTransitions.clear();
                                        }
                                    }
                                    this.mIsInTraversal = false;
                                }
                            } catch (RemoteException unused13) {
                                z10 = z10;
                            }
                        } catch (RemoteException unused14) {
                            z10 = z10;
                            obj2 = obj;
                            hostVisibility = hostVisibility;
                            z42 = z42;
                            z11 = false;
                            z12 = false;
                            z14 = false;
                            r1 = obj2;
                            z15 = z2;
                            r2 = r1;
                            this.mAttachInfo.mWindowLeft = r2.left;
                            this.mAttachInfo.mWindowTop = r2.top;
                            if (this.mWidth == r2.width()) {
                                this.mWidth = r2.width();
                                this.mHeight = r2.height();
                            } else {
                                this.mWidth = r2.width();
                                this.mHeight = r2.height();
                            }
                            if (this.mSurfaceHolder != null) {
                                if (this.mSurface.isValid()) {
                                    this.mSurfaceHolder.mSurface = this.mSurface;
                                }
                                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                this.mSurfaceHolder.mSurfaceLock.unlock();
                                if (this.mSurface.isValid()) {
                                    if (!zIsValid) {
                                        this.mSurfaceHolder.ungetCallbacks();
                                        this.mIsCreating = true;
                                        callbacks3 = this.mSurfaceHolder.getCallbacks();
                                        if (callbacks3 != null) {
                                            while (i8 < r2) {
                                                callback.surfaceCreated(this.mSurfaceHolder);
                                            }
                                        }
                                        z15 = true;
                                    }
                                    if (z15) {
                                        while (i7 < r2) {
                                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                        }
                                    } else {
                                        while (i7 < r2) {
                                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                        }
                                    }
                                    this.mIsCreating = false;
                                } else if (zIsValid) {
                                    this.mSurfaceHolder.ungetCallbacks();
                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        while (i6 < r2) {
                                            callback3.surfaceDestroyed(this.mSurfaceHolder);
                                        }
                                    }
                                    this.mSurfaceHolder.mSurfaceLock.lock();
                                    this.mSurfaceHolder.mSurface = new Surface();
                                    this.mSurfaceHolder.mSurfaceLock.unlock();
                                }
                            }
                            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                            if (threadedRenderer != null) {
                                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                this.mNeedsRendererSetup = false;
                            }
                            if (this.mStopped) {
                                if ((iRelayoutWindow & 1) != 0) {
                                    z16 = true;
                                } else {
                                    z16 = false;
                                }
                                if (!ensureTouchModeLocally(z16)) {
                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    measuredWidth = view2.getMeasuredWidth();
                                    measuredHeight = view2.getMeasuredHeight();
                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                        z17 = true;
                                    } else {
                                        z17 = false;
                                    }
                                    if (layoutParams3.verticalWeight > 0.0f) {
                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                        z17 = true;
                                    }
                                    if (z17) {
                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    }
                                    z4 = true;
                                } else {
                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    measuredWidth = view2.getMeasuredWidth();
                                    measuredHeight = view2.getMeasuredHeight();
                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                        z17 = true;
                                    } else {
                                        z17 = false;
                                    }
                                    if (layoutParams3.verticalWeight > 0.0f) {
                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                        z17 = true;
                                    }
                                    if (z17) {
                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    }
                                    z4 = true;
                                }
                            } else {
                                if ((iRelayoutWindow & 1) != 0) {
                                    z16 = true;
                                } else {
                                    z16 = false;
                                }
                                if (!ensureTouchModeLocally(z16)) {
                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    measuredWidth = view2.getMeasuredWidth();
                                    measuredHeight = view2.getMeasuredHeight();
                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                        z17 = true;
                                    } else {
                                        z17 = false;
                                    }
                                    if (layoutParams3.verticalWeight > 0.0f) {
                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                        z17 = true;
                                    }
                                    if (z17) {
                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    }
                                    z4 = true;
                                } else {
                                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    measuredWidth = view2.getMeasuredWidth();
                                    measuredHeight = view2.getMeasuredHeight();
                                    if (layoutParams3.horizontalWeight > 0.0f) {
                                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                        z17 = true;
                                    } else {
                                        z17 = false;
                                    }
                                    if (layoutParams3.verticalWeight > 0.0f) {
                                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                        z17 = true;
                                    }
                                    if (z17) {
                                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                    }
                                    z4 = true;
                                }
                            }
                            i5 = iRelayoutWindow;
                            if (z4) {
                                z30 = false;
                            } else {
                                z30 = false;
                            }
                            if (z30) {
                                z31 = true;
                            } else {
                                z31 = true;
                            }
                            if (z30) {
                                performLayout(layoutParams3, this.mWidth, this.mHeight);
                                if ((view2.mPrivateFlags & 512) != 0) {
                                    view2.getLocationInWindow(this.mTmpLocation);
                                    Region region6 = this.mTransparentRegion;
                                    int[] iArr6 = this.mTmpLocation;
                                    region6.set(iArr6[0], iArr6[1], (iArr6[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                    view2.gatherTransparentRegion(this.mTransparentRegion);
                                    translator2 = this.mTranslator;
                                    if (translator2 != null) {
                                        translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                    }
                                    if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                        this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                        this.mFullRedrawNeeded = true;
                                        this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                    }
                                }
                            }
                            if (z31) {
                                this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                            }
                            if (z8) {
                                internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                                internalInsetsInfo.reset();
                                this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                                this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                                if (!z10) {
                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                    translator = this.mTranslator;
                                    if (translator != null) {
                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                    } else {
                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                    }
                                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                } else {
                                    this.mLastGivenInsets.set(internalInsetsInfo);
                                    translator = this.mTranslator;
                                    if (translator != null) {
                                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                    } else {
                                        translatedContentInsets = internalInsetsInfo.contentInsets;
                                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                    }
                                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                                }
                            }
                            if (this.mFirst) {
                                if (!sAlwaysAssignFocus) {
                                    view = this.mView;
                                    if (view != null) {
                                        this.mView.restoreDefaultFocus();
                                    }
                                } else {
                                    view = this.mView;
                                    if (view != null) {
                                        this.mView.restoreDefaultFocus();
                                    }
                                }
                            }
                            if (z42) {
                            }
                            if (this.mAttachInfo.mHasWindowFocus) {
                                z33 = false;
                            } else {
                                z33 = false;
                            }
                            if (z33) {
                                z34 = false;
                            } else {
                                z34 = false;
                            }
                            if (z34) {
                                this.mLostWindowFocus = false;
                            } else if (!z33) {
                                this.mLostWindowFocus = true;
                            }
                            if (!z32) {
                                layoutParams2 = this.mWindowAttributes;
                                if (layoutParams2 == null) {
                                    z35 = false;
                                } else {
                                    z35 = true;
                                }
                                if (!z35) {
                                    view2.sendAccessibilityEvent(32);
                                }
                            } else {
                                layoutParams2 = this.mWindowAttributes;
                                if (layoutParams2 == null) {
                                    z35 = false;
                                } else {
                                    z35 = true;
                                }
                                if (!z35) {
                                    view2.sendAccessibilityEvent(32);
                                }
                            }
                            this.mFirst = false;
                            this.mWillDrawSoon = false;
                            this.mNewSurfaceNeeded = false;
                            this.mActivityRelaunched = false;
                            this.mViewVisibility = hostVisibility;
                            this.mHadWindowFocus = z33;
                            if (z33) {
                                z36 = true;
                            } else {
                                z36 = true;
                            }
                            if ((i5 & 2) != 0) {
                                reportNextDraw();
                            }
                            if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                                z37 = z36;
                            } else {
                                z37 = z36;
                            }
                            if (!z37) {
                                if (z9) {
                                    scheduleTraversals();
                                } else {
                                    arrayList = this.mPendingTransitions;
                                    if (arrayList != null) {
                                        while (i10 < this.mPendingTransitions.size()) {
                                            this.mPendingTransitions.get(i10).endChangingAnimations();
                                        }
                                        this.mPendingTransitions.clear();
                                    }
                                }
                            } else if (z9) {
                                scheduleTraversals();
                            } else {
                                arrayList = this.mPendingTransitions;
                                if (arrayList != null) {
                                    while (i10 < this.mPendingTransitions.size()) {
                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                    }
                                    this.mPendingTransitions.clear();
                                }
                            }
                            this.mIsInTraversal = false;
                        }
                    } catch (RemoteException unused15) {
                        z11 = false;
                        z12 = false;
                        z13 = false;
                        obj2 = obj;
                        z14 = false;
                        r1 = obj2;
                        z15 = z2;
                        r2 = r1;
                        this.mAttachInfo.mWindowLeft = r2.left;
                        this.mAttachInfo.mWindowTop = r2.top;
                        if (this.mWidth == r2.width()) {
                            this.mWidth = r2.width();
                            this.mHeight = r2.height();
                        } else {
                            this.mWidth = r2.width();
                            this.mHeight = r2.height();
                        }
                        if (this.mSurfaceHolder != null) {
                            if (this.mSurface.isValid()) {
                                this.mSurfaceHolder.mSurface = this.mSurface;
                            }
                            this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                            this.mSurfaceHolder.mSurfaceLock.unlock();
                            if (this.mSurface.isValid()) {
                                if (!zIsValid) {
                                    this.mSurfaceHolder.ungetCallbacks();
                                    this.mIsCreating = true;
                                    callbacks3 = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks3 != null) {
                                        while (i8 < r2) {
                                            callback.surfaceCreated(this.mSurfaceHolder);
                                        }
                                    }
                                    z15 = true;
                                }
                                if (z15) {
                                    while (i7 < r2) {
                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                    }
                                } else {
                                    while (i7 < r2) {
                                        callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                    }
                                }
                                this.mIsCreating = false;
                            } else if (zIsValid) {
                                this.mSurfaceHolder.ungetCallbacks();
                                callbacks = this.mSurfaceHolder.getCallbacks();
                                if (callbacks != null) {
                                    while (i6 < r2) {
                                        callback3.surfaceDestroyed(this.mSurfaceHolder);
                                    }
                                }
                                this.mSurfaceHolder.mSurfaceLock.lock();
                                this.mSurfaceHolder.mSurface = new Surface();
                                this.mSurfaceHolder.mSurfaceLock.unlock();
                            }
                        }
                        threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                        if (threadedRenderer != null) {
                            threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                            this.mNeedsRendererSetup = false;
                        }
                        if (this.mStopped) {
                            if ((iRelayoutWindow & 1) != 0) {
                                z16 = true;
                            } else {
                                z16 = false;
                            }
                            if (!ensureTouchModeLocally(z16)) {
                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                measuredWidth = view2.getMeasuredWidth();
                                measuredHeight = view2.getMeasuredHeight();
                                if (layoutParams3.horizontalWeight > 0.0f) {
                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                    z17 = true;
                                } else {
                                    z17 = false;
                                }
                                if (layoutParams3.verticalWeight > 0.0f) {
                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                    z17 = true;
                                }
                                if (z17) {
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                }
                                z4 = true;
                            } else {
                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                measuredWidth = view2.getMeasuredWidth();
                                measuredHeight = view2.getMeasuredHeight();
                                if (layoutParams3.horizontalWeight > 0.0f) {
                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                    z17 = true;
                                } else {
                                    z17 = false;
                                }
                                if (layoutParams3.verticalWeight > 0.0f) {
                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                    z17 = true;
                                }
                                if (z17) {
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                }
                                z4 = true;
                            }
                        } else {
                            if ((iRelayoutWindow & 1) != 0) {
                                z16 = true;
                            } else {
                                z16 = false;
                            }
                            if (!ensureTouchModeLocally(z16)) {
                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                measuredWidth = view2.getMeasuredWidth();
                                measuredHeight = view2.getMeasuredHeight();
                                if (layoutParams3.horizontalWeight > 0.0f) {
                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                    z17 = true;
                                } else {
                                    z17 = false;
                                }
                                if (layoutParams3.verticalWeight > 0.0f) {
                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                    z17 = true;
                                }
                                if (z17) {
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                }
                                z4 = true;
                            } else {
                                rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                                rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                measuredWidth = view2.getMeasuredWidth();
                                measuredHeight = view2.getMeasuredHeight();
                                if (layoutParams3.horizontalWeight > 0.0f) {
                                    rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                    z17 = true;
                                } else {
                                    z17 = false;
                                }
                                if (layoutParams3.verticalWeight > 0.0f) {
                                    rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                    z17 = true;
                                }
                                if (z17) {
                                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                                }
                                z4 = true;
                            }
                        }
                        i5 = iRelayoutWindow;
                        if (z4) {
                            z30 = false;
                        } else {
                            z30 = false;
                        }
                        if (z30) {
                            z31 = true;
                        } else {
                            z31 = true;
                        }
                        if (z30) {
                            performLayout(layoutParams3, this.mWidth, this.mHeight);
                            if ((view2.mPrivateFlags & 512) != 0) {
                                view2.getLocationInWindow(this.mTmpLocation);
                                Region region7 = this.mTransparentRegion;
                                int[] iArr7 = this.mTmpLocation;
                                region7.set(iArr7[0], iArr7[1], (iArr7[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                                view2.gatherTransparentRegion(this.mTransparentRegion);
                                translator2 = this.mTranslator;
                                if (translator2 != null) {
                                    translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                                }
                                if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                    this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                    this.mFullRedrawNeeded = true;
                                    this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                                }
                            }
                        }
                        if (z31) {
                            this.mAttachInfo.mRecomputeGlobalAttributes = false;
                            this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                        }
                        if (z8) {
                            internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                            internalInsetsInfo.reset();
                            this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                            this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                            if (!z10) {
                                this.mLastGivenInsets.set(internalInsetsInfo);
                                translator = this.mTranslator;
                                if (translator != null) {
                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                } else {
                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                }
                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                            } else {
                                this.mLastGivenInsets.set(internalInsetsInfo);
                                translator = this.mTranslator;
                                if (translator != null) {
                                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                                } else {
                                    translatedContentInsets = internalInsetsInfo.contentInsets;
                                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                                }
                                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                            }
                        }
                        if (this.mFirst) {
                            if (!sAlwaysAssignFocus) {
                                view = this.mView;
                                if (view != null) {
                                    this.mView.restoreDefaultFocus();
                                }
                            } else {
                                view = this.mView;
                                if (view != null) {
                                    this.mView.restoreDefaultFocus();
                                }
                            }
                        }
                        if (z42) {
                        }
                        if (this.mAttachInfo.mHasWindowFocus) {
                            z33 = false;
                        } else {
                            z33 = false;
                        }
                        if (z33) {
                            z34 = false;
                        } else {
                            z34 = false;
                        }
                        if (z34) {
                            this.mLostWindowFocus = false;
                        } else if (!z33) {
                            this.mLostWindowFocus = true;
                        }
                        if (!z32) {
                            layoutParams2 = this.mWindowAttributes;
                            if (layoutParams2 == null) {
                                z35 = false;
                            } else {
                                z35 = true;
                            }
                            if (!z35) {
                                view2.sendAccessibilityEvent(32);
                            }
                        } else {
                            layoutParams2 = this.mWindowAttributes;
                            if (layoutParams2 == null) {
                                z35 = false;
                            } else {
                                z35 = true;
                            }
                            if (!z35) {
                                view2.sendAccessibilityEvent(32);
                            }
                        }
                        this.mFirst = false;
                        this.mWillDrawSoon = false;
                        this.mNewSurfaceNeeded = false;
                        this.mActivityRelaunched = false;
                        this.mViewVisibility = hostVisibility;
                        this.mHadWindowFocus = z33;
                        if (z33) {
                            z36 = true;
                        } else {
                            z36 = true;
                        }
                        if ((i5 & 2) != 0) {
                            reportNextDraw();
                        }
                        if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
                            z37 = z36;
                        } else {
                            z37 = z36;
                        }
                        if (!z37) {
                            if (z9) {
                                scheduleTraversals();
                            } else {
                                arrayList = this.mPendingTransitions;
                                if (arrayList != null) {
                                    while (i10 < this.mPendingTransitions.size()) {
                                        this.mPendingTransitions.get(i10).endChangingAnimations();
                                    }
                                    this.mPendingTransitions.clear();
                                }
                            }
                        } else if (z9) {
                            scheduleTraversals();
                        } else {
                            arrayList = this.mPendingTransitions;
                            if (arrayList != null) {
                                while (i10 < this.mPendingTransitions.size()) {
                                    this.mPendingTransitions.get(i10).endChangingAnimations();
                                }
                                this.mPendingTransitions.clear();
                            }
                        }
                        this.mIsInTraversal = false;
                    }
                } catch (RemoteException unused16) {
                    z11 = false;
                    z12 = false;
                    iRelayoutWindow = 0;
                }
                this.mAttachInfo.mWindowLeft = r2.left;
                this.mAttachInfo.mWindowTop = r2.top;
                if (this.mWidth == r2.width()) {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                } else {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                }
                if (this.mSurfaceHolder != null) {
                    if (this.mSurface.isValid()) {
                        this.mSurfaceHolder.mSurface = this.mSurface;
                    }
                    this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                    this.mSurfaceHolder.mSurfaceLock.unlock();
                    if (this.mSurface.isValid()) {
                        if (!zIsValid) {
                            this.mSurfaceHolder.ungetCallbacks();
                            this.mIsCreating = true;
                            callbacks3 = this.mSurfaceHolder.getCallbacks();
                            if (callbacks3 != null) {
                                while (i8 < r2) {
                                    callback.surfaceCreated(this.mSurfaceHolder);
                                }
                            }
                            z15 = true;
                        }
                        if (z15) {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        } else {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        }
                        this.mIsCreating = false;
                    } else if (zIsValid) {
                        this.mSurfaceHolder.ungetCallbacks();
                        callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            while (i6 < r2) {
                                callback3.surfaceDestroyed(this.mSurfaceHolder);
                            }
                        }
                        this.mSurfaceHolder.mSurfaceLock.lock();
                        this.mSurfaceHolder.mSurface = new Surface();
                        this.mSurfaceHolder.mSurfaceLock.unlock();
                    }
                }
                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                if (threadedRenderer != null) {
                    threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                    this.mNeedsRendererSetup = false;
                }
                if (this.mStopped) {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                } else {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                }
                i5 = iRelayoutWindow;
            } else {
                maybeHandleWindowMove(rect);
                hostVisibility = hostVisibility;
                z42 = z42;
                z11 = false;
                i5 = 0;
                z10 = false;
            }
            if (z4 || (this.mStopped && !this.mReportNextDraw)) {
                z30 = false;
            } else {
                z30 = true;
            }
            if (z30 || this.mAttachInfo.mRecomputeGlobalAttributes) {
                z31 = true;
            } else {
                z31 = false;
            }
            if (z30) {
                performLayout(layoutParams3, this.mWidth, this.mHeight);
                if ((view2.mPrivateFlags & 512) != 0) {
                    view2.getLocationInWindow(this.mTmpLocation);
                    Region region8 = this.mTransparentRegion;
                    int[] iArr8 = this.mTmpLocation;
                    region8.set(iArr8[0], iArr8[1], (iArr8[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                    view2.gatherTransparentRegion(this.mTransparentRegion);
                    translator2 = this.mTranslator;
                    if (translator2 != null) {
                        translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                    }
                    if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                        this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                        this.mFullRedrawNeeded = true;
                        this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                    }
                }
            }
            if (z31) {
                this.mAttachInfo.mRecomputeGlobalAttributes = false;
                this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
            }
            if (z8) {
                internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
                internalInsetsInfo.reset();
                this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
                this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
                if (!z10 || !this.mLastGivenInsets.equals(internalInsetsInfo)) {
                    this.mLastGivenInsets.set(internalInsetsInfo);
                    translator = this.mTranslator;
                    if (translator != null) {
                        translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                        translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                        translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                    } else {
                        translatedContentInsets = internalInsetsInfo.contentInsets;
                        translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                        translatedTouchableArea = internalInsetsInfo.touchableRegion;
                    }
                    this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
                }
            }
            if (this.mFirst) {
                if (!sAlwaysAssignFocus || !isInTouchMode()) {
                    view = this.mView;
                    if (view != null && !view.hasFocus()) {
                        this.mView.restoreDefaultFocus();
                    }
                } else {
                    View viewFindFocus = this.mView.findFocus();
                    if ((viewFindFocus instanceof ViewGroup) && ((ViewGroup) viewFindFocus).getDescendantFocusability() == 262144) {
                        viewFindFocus.restoreDefaultFocus();
                    }
                }
            }
            if ((z42 && !this.mFirst) || !z9) {
                z32 = false;
            }
            if (this.mAttachInfo.mHasWindowFocus || !z9) {
                z33 = false;
            } else {
                z33 = true;
            }
            if (z33 || !this.mLostWindowFocus) {
                z34 = false;
            } else {
                z34 = true;
            }
            if (z34) {
                this.mLostWindowFocus = false;
            } else if (!z33 && this.mHadWindowFocus) {
                this.mLostWindowFocus = true;
            }
            if (!z32 || z34) {
                layoutParams2 = this.mWindowAttributes;
                if (layoutParams2 == null && layoutParams2.type == 2005) {
                    z35 = true;
                } else {
                    z35 = false;
                }
                if (!z35) {
                    view2.sendAccessibilityEvent(32);
                }
            }
            this.mFirst = false;
            this.mWillDrawSoon = false;
            this.mNewSurfaceNeeded = false;
            this.mActivityRelaunched = false;
            this.mViewVisibility = hostVisibility;
            this.mHadWindowFocus = z33;
            if (z33 || isInLocalFocusMode() || (zMayUseInputMethod = WindowManager.LayoutParams.mayUseInputMethod(this.mWindowAttributes.flags)) == this.mLastWasImTarget) {
                z36 = true;
            } else {
                this.mLastWasImTarget = zMayUseInputMethod;
                InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                if (inputMethodManagerPeekInstance == null || !zMayUseInputMethod) {
                    z36 = true;
                } else {
                    inputMethodManagerPeekInstance.onPreWindowFocus(this.mView, z33);
                    View view3 = this.mView;
                    z36 = true;
                    inputMethodManagerPeekInstance.onPostWindowFocus(view3, view3.findFocus(), this.mWindowAttributes.softInputMode, !this.mHasHadWindowFocus, this.mWindowAttributes.flags);
                }
            }
            if ((i5 & 2) != 0) {
                reportNextDraw();
            }
            if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw() || !z9) {
                z37 = z36;
            } else {
                z37 = false;
            }
            if (!z37 && !z11) {
                ArrayList<LayoutTransition> arrayList2 = this.mPendingTransitions;
                if (arrayList2 != null && arrayList2.size() > 0) {
                    for (int i12 = 0; i12 < this.mPendingTransitions.size(); i12++) {
                        this.mPendingTransitions.get(i12).startChangingAnimations();
                    }
                    this.mPendingTransitions.clear();
                }
                performDraw();
            } else if (z9) {
                scheduleTraversals();
            } else {
                arrayList = this.mPendingTransitions;
                if (arrayList != null && arrayList.size() > 0) {
                    while (i10 < this.mPendingTransitions.size()) {
                        this.mPendingTransitions.get(i10).endChangingAnimations();
                    }
                    this.mPendingTransitions.clear();
                }
            }
            this.mIsInTraversal = false;
        }
        z3 = false;
        if (z42) {
            this.mAttachInfo.mWindowVisibility = hostVisibility;
            view2.dispatchWindowVisibilityChanged(hostVisibility);
            if (z) {
                if (hostVisibility == 0) {
                    z41 = true;
                } else {
                    z41 = false;
                }
                view2.dispatchVisibilityAggregated(z41);
            }
            if (hostVisibility == 0) {
                endDragResizing();
                destroyHardwareResources();
            } else {
                endDragResizing();
                destroyHardwareResources();
            }
            if (hostVisibility == 8) {
                this.mHasHadWindowFocus = false;
            }
        }
        if (this.mAttachInfo.mWindowVisibility != 0) {
            view2.clearAccessibilityFocus();
        }
        getRunQueue().executeActions(this.mAttachInfo.mHandler);
        if (this.mLayoutRequested) {
            z4 = false;
        } else {
            z4 = false;
        }
        if (z4) {
            resources = this.mView.getContext().getResources();
            if (this.mFirst) {
                this.mAttachInfo.mInTouchMode = !this.mAddedTouchMode;
                ensureTouchModeLocally(this.mAddedTouchMode);
                z40 = z3;
                i = iHeight;
                i11 = iWidth;
                z5 = false;
            } else {
                z39 = !this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets);
                if (!this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                    z39 = true;
                }
                if (!this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                    z39 = true;
                }
                if (!this.mPendingDisplayCutout.equals(this.mAttachInfo.mDisplayCutout)) {
                    z39 = true;
                }
                if (!this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                    this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
                }
                if (!this.mPendingOutsets.equals(this.mAttachInfo.mOutsets)) {
                    z39 = true;
                }
                if (this.mPendingAlwaysConsumeNavBar != this.mAttachInfo.mAlwaysConsumeNavBar) {
                    z39 = true;
                }
                if (layoutParams3.width != -2) {
                    if (shouldUseDisplaySize(layoutParams3)) {
                        Point point3 = new Point();
                        this.mDisplay.getRealSize(point3);
                        iDipToPx = point3.x;
                        iDipToPx2 = point3.y;
                    } else {
                        Configuration configuration3 = resources.getConfiguration();
                        iDipToPx = dipToPx(configuration3.screenWidthDp);
                        iDipToPx2 = dipToPx(configuration3.screenHeightDp);
                    }
                    i = iDipToPx2;
                    i11 = iDipToPx;
                    z5 = z39;
                    z40 = true;
                } else {
                    if (shouldUseDisplaySize(layoutParams3)) {
                        Point point4 = new Point();
                        this.mDisplay.getRealSize(point4);
                        iDipToPx = point4.x;
                        iDipToPx2 = point4.y;
                    } else {
                        Configuration configuration4 = resources.getConfiguration();
                        iDipToPx = dipToPx(configuration4.screenWidthDp);
                        iDipToPx2 = dipToPx(configuration4.screenHeightDp);
                    }
                    i = iDipToPx2;
                    i11 = iDipToPx;
                    z5 = z39;
                    z40 = true;
                }
            }
            zMeasureHierarchy = measureHierarchy(view2, layoutParams3, resources, i11, i) | z40;
            i2 = i11;
        } else {
            zMeasureHierarchy = z3;
            i = iHeight;
            i2 = iWidth;
            z5 = false;
        }
        if (collectViewAttributes()) {
            layoutParams = layoutParams3;
        }
        if (this.mAttachInfo.mForceReportNewAttributes) {
            this.mAttachInfo.mForceReportNewAttributes = false;
            layoutParams = layoutParams3;
        }
        if (!this.mFirst) {
            this.mAttachInfo.mViewVisibilityChanged = false;
            i3 = this.mSoftInputMode & 240;
            if (i3 == 0) {
                size = this.mAttachInfo.mScrollContainers.size();
                while (i4 < size) {
                    if (this.mAttachInfo.mScrollContainers.get(i4).isShown()) {
                        i3 = 16;
                    }
                }
                if (i3 == 0) {
                    i3 = 32;
                }
                if ((layoutParams3.softInputMode & 240) != i3) {
                    layoutParams3.softInputMode = i3 | (layoutParams3.softInputMode & (-241));
                    layoutParams = layoutParams3;
                }
            }
        } else {
            this.mAttachInfo.mViewVisibilityChanged = false;
            i3 = this.mSoftInputMode & 240;
            if (i3 == 0) {
                size = this.mAttachInfo.mScrollContainers.size();
                while (i4 < size) {
                    if (this.mAttachInfo.mScrollContainers.get(i4).isShown()) {
                        i3 = 16;
                    }
                }
                if (i3 == 0) {
                    i3 = 32;
                }
                if ((layoutParams3.softInputMode & 240) != i3) {
                    layoutParams3.softInputMode = i3 | (layoutParams3.softInputMode & (-241));
                    layoutParams = layoutParams3;
                }
            }
        }
        if (layoutParams != null) {
            if ((view2.mPrivateFlags & 512) != 0) {
                layoutParams.format = -3;
            }
            View.AttachInfo attachInfo2 = this.mAttachInfo;
            if ((layoutParams.flags & 33554432) != 0) {
                z38 = true;
            } else {
                z38 = false;
            }
            attachInfo2.mOverscanRequested = z38;
        }
        if (this.mApplyInsetsRequested) {
            this.mApplyInsetsRequested = false;
            this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
            dispatchApplyInsets(view2);
            if (this.mLayoutRequested) {
                zMeasureHierarchy |= measureHierarchy(view2, layoutParams3, this.mView.getContext().getResources(), i2, i);
            }
        }
        if (z4) {
            this.mLayoutRequested = false;
        }
        if (z4) {
            z6 = false;
        } else {
            z6 = false;
        }
        if (this.mDragResizing) {
            z7 = false;
        } else {
            z7 = false;
        }
        boolean z47 = z6 | z7 | this.mActivityRelaunched;
        if (this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners()) {
            z8 = true;
        } else {
            z8 = true;
        }
        generationId = this.mSurface.getGenerationId();
        if (hostVisibility == 0) {
            z9 = true;
        } else {
            z9 = false;
        }
        boolean z48 = this.mForceNextWindowRelayout;
        if (!this.mFirst) {
            obj = rect;
            this.mForceNextWindowRelayout = false;
            if (z9) {
                if (z8) {
                    z29 = false;
                } else {
                    z29 = false;
                }
                z10 = z29;
            } else {
                z10 = false;
            }
            baseSurfaceHolder = this.mSurfaceHolder;
            if (baseSurfaceHolder != null) {
                baseSurfaceHolder.mSurfaceLock.lock();
                this.mDrawingAllowed = true;
            }
            zIsValid = this.mSurface.isValid();
            if (this.mAttachInfo.mThreadedRenderer != null) {
                if (this.mAttachInfo.mThreadedRenderer.pauseSurface(this.mSurface)) {
                    this.mDirty.set(0, 0, this.mWidth, this.mHeight);
                }
                this.mChoreographer.mFrameInfo.addFlags(1L);
            }
            iRelayoutWindow = relayoutWindow(layoutParams, hostVisibility, z10);
            if (this.mPendingMergedConfiguration.equals(this.mLastReportedMergedConfiguration)) {
                MergedConfiguration mergedConfiguration2 = this.mPendingMergedConfiguration;
                if (this.mFirst) {
                    z28 = true;
                } else {
                    z28 = false;
                }
                performConfigurationChange(mergedConfiguration2, z28, -1);
                z13 = true;
            } else {
                z13 = false;
            }
            if (this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets)) {
                z18 = true;
            } else {
                z18 = false;
            }
            if (this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                z14 = true;
            } else {
                z14 = false;
            }
            if (this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                z19 = true;
            } else {
                z19 = false;
            }
            z10 = z10;
            if (this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                z20 = true;
            } else {
                z20 = false;
            }
            if (this.mPendingDisplayCutout.equals(this.mAttachInfo.mDisplayCutout)) {
                z21 = true;
            } else {
                z21 = false;
            }
            hostVisibility = hostVisibility;
            boolean z49 = !this.mPendingOutsets.equals(this.mAttachInfo.mOutsets);
            if ((iRelayoutWindow & 32) != 0) {
                z22 = true;
            } else {
                z22 = false;
            }
            z15 = z2 | z22;
            z23 = this.mPendingAlwaysConsumeNavBar;
            z42 = z42;
            if (z23 != this.mAttachInfo.mAlwaysConsumeNavBar) {
                z24 = true;
            } else {
                z24 = false;
            }
            if (z14) {
                Rect rect3 = this.mAttachInfo.mContentInsets;
                z14 = z14;
                rect3.set(this.mPendingContentInsets);
                r14 = rect3;
            } else {
                z14 = z14;
            }
            if (z18) {
                r14 = z23;
                this.mAttachInfo.mOverscanInsets.set(this.mPendingOverscanInsets);
                z14 = true;
            }
            if (z20) {
                this.mAttachInfo.mStableInsets.set(this.mPendingStableInsets);
                z14 = true;
            }
            if (z21) {
                this.mAttachInfo.mDisplayCutout.set(this.mPendingDisplayCutout);
                z14 = true;
            }
            if (z24) {
                this.mAttachInfo.mAlwaysConsumeNavBar = this.mPendingAlwaysConsumeNavBar;
                z14 = true;
            }
            if (!z14) {
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                this.mAttachInfo.mOutsets.set(this.mPendingOutsets);
                this.mApplyInsetsRequested = false;
                dispatchApplyInsets(view2);
            } else {
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                this.mAttachInfo.mOutsets.set(this.mPendingOutsets);
                this.mApplyInsetsRequested = false;
                dispatchApplyInsets(view2);
            }
            if (z19) {
                this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
            }
            if (!zIsValid) {
                if (this.mSurface.isValid()) {
                    this.mFullRedrawNeeded = true;
                    this.mPreviousTransparentRegion.setEmpty();
                    if (this.mAttachInfo.mThreadedRenderer != null) {
                        zInitialize = this.mAttachInfo.mThreadedRenderer.initialize(this.mSurface);
                        if (zInitialize) {
                            if ((view2.mPrivateFlags & 512) == 0) {
                                this.mSurface.allocateBuffers();
                            }
                        }
                        z12 = zInitialize;
                        z11 = true;
                    } else {
                        z11 = true;
                    }
                    if ((iRelayoutWindow & 16) != 0) {
                        z25 = true;
                    } else {
                        z25 = false;
                    }
                    if ((iRelayoutWindow & 8) != 0) {
                        z26 = true;
                    } else {
                        z26 = false;
                    }
                    if (z25) {
                        z27 = true;
                    } else {
                        z27 = true;
                    }
                    if (this.mDragResizing != z27) {
                        if (z27) {
                            if (z25) {
                                i9 = 0;
                            } else {
                                i9 = 1;
                            }
                            this.mResizeMode = i9;
                            startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                        } else {
                            endDragResizing();
                        }
                    }
                    if (!this.mUseMTRenderer) {
                        if (z27) {
                            this.mCanvasOffsetX = this.mWinFrame.left;
                            this.mCanvasOffsetY = this.mWinFrame.top;
                        } else {
                            this.mCanvasOffsetY = 0;
                            this.mCanvasOffsetX = 0;
                        }
                    }
                    r2 = obj;
                    this.mAttachInfo.mWindowLeft = r2.left;
                    this.mAttachInfo.mWindowTop = r2.top;
                    if (this.mWidth == r2.width()) {
                        this.mWidth = r2.width();
                        this.mHeight = r2.height();
                    } else {
                        this.mWidth = r2.width();
                        this.mHeight = r2.height();
                    }
                    if (this.mSurfaceHolder != null) {
                        if (this.mSurface.isValid()) {
                            this.mSurfaceHolder.mSurface = this.mSurface;
                        }
                        this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                        this.mSurfaceHolder.mSurfaceLock.unlock();
                        if (this.mSurface.isValid()) {
                            if (!zIsValid) {
                                this.mSurfaceHolder.ungetCallbacks();
                                this.mIsCreating = true;
                                callbacks3 = this.mSurfaceHolder.getCallbacks();
                                if (callbacks3 != null) {
                                    while (i8 < r2) {
                                        callback.surfaceCreated(this.mSurfaceHolder);
                                    }
                                }
                                z15 = true;
                            }
                            if (z15) {
                                while (i7 < r2) {
                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                }
                            } else {
                                while (i7 < r2) {
                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                }
                            }
                            this.mIsCreating = false;
                        } else if (zIsValid) {
                            this.mSurfaceHolder.ungetCallbacks();
                            callbacks = this.mSurfaceHolder.getCallbacks();
                            if (callbacks != null) {
                                while (i6 < r2) {
                                    callback3.surfaceDestroyed(this.mSurfaceHolder);
                                }
                            }
                            this.mSurfaceHolder.mSurfaceLock.lock();
                            this.mSurfaceHolder.mSurface = new Surface();
                            this.mSurfaceHolder.mSurfaceLock.unlock();
                        }
                    }
                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                    if (threadedRenderer != null) {
                        threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                        this.mNeedsRendererSetup = false;
                    }
                    if (this.mStopped) {
                        if ((iRelayoutWindow & 1) != 0) {
                            z16 = true;
                        } else {
                            z16 = false;
                        }
                        if (!ensureTouchModeLocally(z16)) {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        } else {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        }
                    } else {
                        if ((iRelayoutWindow & 1) != 0) {
                            z16 = true;
                        } else {
                            z16 = false;
                        }
                        if (!ensureTouchModeLocally(z16)) {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        } else {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        }
                    }
                    i5 = iRelayoutWindow;
                }
                z12 = false;
                if ((iRelayoutWindow & 16) != 0) {
                    z25 = true;
                } else {
                    z25 = false;
                }
                if ((iRelayoutWindow & 8) != 0) {
                    z26 = true;
                } else {
                    z26 = false;
                }
                if (z25) {
                    z27 = true;
                } else {
                    z27 = true;
                }
                if (this.mDragResizing != z27) {
                    if (z27) {
                        if (z25) {
                            i9 = 0;
                        } else {
                            i9 = 1;
                        }
                        this.mResizeMode = i9;
                        startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                    } else {
                        endDragResizing();
                    }
                }
                if (!this.mUseMTRenderer) {
                    if (z27) {
                        this.mCanvasOffsetX = this.mWinFrame.left;
                        this.mCanvasOffsetY = this.mWinFrame.top;
                    } else {
                        this.mCanvasOffsetY = 0;
                        this.mCanvasOffsetX = 0;
                    }
                }
                r2 = obj;
                this.mAttachInfo.mWindowLeft = r2.left;
                this.mAttachInfo.mWindowTop = r2.top;
                if (this.mWidth == r2.width()) {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                } else {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                }
                if (this.mSurfaceHolder != null) {
                    if (this.mSurface.isValid()) {
                        this.mSurfaceHolder.mSurface = this.mSurface;
                    }
                    this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                    this.mSurfaceHolder.mSurfaceLock.unlock();
                    if (this.mSurface.isValid()) {
                        if (!zIsValid) {
                            this.mSurfaceHolder.ungetCallbacks();
                            this.mIsCreating = true;
                            callbacks3 = this.mSurfaceHolder.getCallbacks();
                            if (callbacks3 != null) {
                                while (i8 < r2) {
                                    callback.surfaceCreated(this.mSurfaceHolder);
                                }
                            }
                            z15 = true;
                        }
                        if (z15) {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        } else {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        }
                        this.mIsCreating = false;
                    } else if (zIsValid) {
                        this.mSurfaceHolder.ungetCallbacks();
                        callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            while (i6 < r2) {
                                callback3.surfaceDestroyed(this.mSurfaceHolder);
                            }
                        }
                        this.mSurfaceHolder.mSurfaceLock.lock();
                        this.mSurfaceHolder.mSurface = new Surface();
                        this.mSurfaceHolder.mSurfaceLock.unlock();
                    }
                }
                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                if (threadedRenderer != null) {
                    threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                    this.mNeedsRendererSetup = false;
                }
                if (this.mStopped) {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                } else {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                }
                i5 = iRelayoutWindow;
            } else if (!this.mSurface.isValid()) {
                if (this.mLastScrolledFocus != null) {
                    this.mLastScrolledFocus.clear();
                }
                this.mCurScrollY = 0;
                this.mScrollY = 0;
                if (this.mView instanceof RootViewSurfaceTaker) {
                    ((RootViewSurfaceTaker) this.mView).onRootViewScrollYChanged(0);
                }
                if (this.mScroller != null) {
                    this.mScroller.abortAnimation();
                }
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    this.mAttachInfo.mThreadedRenderer.destroy();
                }
            } else if (generationId == this.mSurface.getGenerationId()) {
                this.mFullRedrawNeeded = true;
                this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
            } else {
                this.mFullRedrawNeeded = true;
                this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
            }
            z11 = false;
            z12 = false;
            if ((iRelayoutWindow & 16) != 0) {
                z25 = true;
            } else {
                z25 = false;
            }
            if ((iRelayoutWindow & 8) != 0) {
                z26 = true;
            } else {
                z26 = false;
            }
            if (z25) {
                z27 = true;
            } else {
                z27 = true;
            }
            if (this.mDragResizing != z27) {
                if (z27) {
                    if (z25) {
                        i9 = 0;
                    } else {
                        i9 = 1;
                    }
                    this.mResizeMode = i9;
                    startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                } else {
                    endDragResizing();
                }
            }
            if (!this.mUseMTRenderer) {
                if (z27) {
                    this.mCanvasOffsetX = this.mWinFrame.left;
                    this.mCanvasOffsetY = this.mWinFrame.top;
                } else {
                    this.mCanvasOffsetY = 0;
                    this.mCanvasOffsetX = 0;
                }
            }
            r2 = obj;
            this.mAttachInfo.mWindowLeft = r2.left;
            this.mAttachInfo.mWindowTop = r2.top;
            if (this.mWidth == r2.width()) {
                this.mWidth = r2.width();
                this.mHeight = r2.height();
            } else {
                this.mWidth = r2.width();
                this.mHeight = r2.height();
            }
            if (this.mSurfaceHolder != null) {
                if (this.mSurface.isValid()) {
                    this.mSurfaceHolder.mSurface = this.mSurface;
                }
                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                this.mSurfaceHolder.mSurfaceLock.unlock();
                if (this.mSurface.isValid()) {
                    if (!zIsValid) {
                        this.mSurfaceHolder.ungetCallbacks();
                        this.mIsCreating = true;
                        callbacks3 = this.mSurfaceHolder.getCallbacks();
                        if (callbacks3 != null) {
                            while (i8 < r2) {
                                callback.surfaceCreated(this.mSurfaceHolder);
                            }
                        }
                        z15 = true;
                    }
                    if (z15) {
                        while (i7 < r2) {
                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                        }
                    } else {
                        while (i7 < r2) {
                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                        }
                    }
                    this.mIsCreating = false;
                } else if (zIsValid) {
                    this.mSurfaceHolder.ungetCallbacks();
                    callbacks = this.mSurfaceHolder.getCallbacks();
                    if (callbacks != null) {
                        while (i6 < r2) {
                            callback3.surfaceDestroyed(this.mSurfaceHolder);
                        }
                    }
                    this.mSurfaceHolder.mSurfaceLock.lock();
                    this.mSurfaceHolder.mSurface = new Surface();
                    this.mSurfaceHolder.mSurfaceLock.unlock();
                }
            }
            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
            if (threadedRenderer != null) {
                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                this.mNeedsRendererSetup = false;
            }
            if (this.mStopped) {
                if ((iRelayoutWindow & 1) != 0) {
                    z16 = true;
                } else {
                    z16 = false;
                }
                if (!ensureTouchModeLocally(z16)) {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                } else {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                }
            } else {
                if ((iRelayoutWindow & 1) != 0) {
                    z16 = true;
                } else {
                    z16 = false;
                }
                if (!ensureTouchModeLocally(z16)) {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                } else {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                }
            }
            i5 = iRelayoutWindow;
        } else {
            obj = rect;
            this.mForceNextWindowRelayout = false;
            if (z9) {
                if (z8) {
                    z29 = false;
                } else {
                    z29 = false;
                }
                z10 = z29;
            } else {
                z10 = false;
            }
            baseSurfaceHolder = this.mSurfaceHolder;
            if (baseSurfaceHolder != null) {
                baseSurfaceHolder.mSurfaceLock.lock();
                this.mDrawingAllowed = true;
            }
            zIsValid = this.mSurface.isValid();
            if (this.mAttachInfo.mThreadedRenderer != null) {
                if (this.mAttachInfo.mThreadedRenderer.pauseSurface(this.mSurface)) {
                    this.mDirty.set(0, 0, this.mWidth, this.mHeight);
                }
                this.mChoreographer.mFrameInfo.addFlags(1L);
            }
            iRelayoutWindow = relayoutWindow(layoutParams, hostVisibility, z10);
            if (this.mPendingMergedConfiguration.equals(this.mLastReportedMergedConfiguration)) {
                MergedConfiguration mergedConfiguration3 = this.mPendingMergedConfiguration;
                if (this.mFirst) {
                    z28 = true;
                } else {
                    z28 = false;
                }
                performConfigurationChange(mergedConfiguration3, z28, -1);
                z13 = true;
            } else {
                z13 = false;
            }
            if (this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets)) {
                z18 = true;
            } else {
                z18 = false;
            }
            if (this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                z14 = true;
            } else {
                z14 = false;
            }
            if (this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                z19 = true;
            } else {
                z19 = false;
            }
            z10 = z10;
            if (this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                z20 = true;
            } else {
                z20 = false;
            }
            if (this.mPendingDisplayCutout.equals(this.mAttachInfo.mDisplayCutout)) {
                z21 = true;
            } else {
                z21 = false;
            }
            hostVisibility = hostVisibility;
            boolean z410 = !this.mPendingOutsets.equals(this.mAttachInfo.mOutsets);
            if ((iRelayoutWindow & 32) != 0) {
                z22 = true;
            } else {
                z22 = false;
            }
            z15 = z2 | z22;
            z23 = this.mPendingAlwaysConsumeNavBar;
            z42 = z42;
            if (z23 != this.mAttachInfo.mAlwaysConsumeNavBar) {
                z24 = true;
            } else {
                z24 = false;
            }
            if (z14) {
                Rect rect4 = this.mAttachInfo.mContentInsets;
                z14 = z14;
                rect4.set(this.mPendingContentInsets);
                r14 = rect4;
            } else {
                z14 = z14;
            }
            if (z18) {
                r14 = z23;
                this.mAttachInfo.mOverscanInsets.set(this.mPendingOverscanInsets);
                z14 = true;
            }
            if (z20) {
                this.mAttachInfo.mStableInsets.set(this.mPendingStableInsets);
                z14 = true;
            }
            if (z21) {
                this.mAttachInfo.mDisplayCutout.set(this.mPendingDisplayCutout);
                z14 = true;
            }
            if (z24) {
                this.mAttachInfo.mAlwaysConsumeNavBar = this.mPendingAlwaysConsumeNavBar;
                z14 = true;
            }
            if (!z14) {
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                this.mAttachInfo.mOutsets.set(this.mPendingOutsets);
                this.mApplyInsetsRequested = false;
                dispatchApplyInsets(view2);
            } else {
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                this.mAttachInfo.mOutsets.set(this.mPendingOutsets);
                this.mApplyInsetsRequested = false;
                dispatchApplyInsets(view2);
            }
            if (z19) {
                this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
            }
            if (!zIsValid) {
                if (this.mSurface.isValid()) {
                    this.mFullRedrawNeeded = true;
                    this.mPreviousTransparentRegion.setEmpty();
                    if (this.mAttachInfo.mThreadedRenderer != null) {
                        zInitialize = this.mAttachInfo.mThreadedRenderer.initialize(this.mSurface);
                        if (zInitialize) {
                            if ((view2.mPrivateFlags & 512) == 0) {
                                this.mSurface.allocateBuffers();
                            }
                        }
                        z12 = zInitialize;
                        z11 = true;
                    } else {
                        z11 = true;
                    }
                    if ((iRelayoutWindow & 16) != 0) {
                        z25 = true;
                    } else {
                        z25 = false;
                    }
                    if ((iRelayoutWindow & 8) != 0) {
                        z26 = true;
                    } else {
                        z26 = false;
                    }
                    if (z25) {
                        z27 = true;
                    } else {
                        z27 = true;
                    }
                    if (this.mDragResizing != z27) {
                        if (z27) {
                            if (z25) {
                                i9 = 0;
                            } else {
                                i9 = 1;
                            }
                            this.mResizeMode = i9;
                            startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                        } else {
                            endDragResizing();
                        }
                    }
                    if (!this.mUseMTRenderer) {
                        if (z27) {
                            this.mCanvasOffsetX = this.mWinFrame.left;
                            this.mCanvasOffsetY = this.mWinFrame.top;
                        } else {
                            this.mCanvasOffsetY = 0;
                            this.mCanvasOffsetX = 0;
                        }
                    }
                    r2 = obj;
                    this.mAttachInfo.mWindowLeft = r2.left;
                    this.mAttachInfo.mWindowTop = r2.top;
                    if (this.mWidth == r2.width()) {
                        this.mWidth = r2.width();
                        this.mHeight = r2.height();
                    } else {
                        this.mWidth = r2.width();
                        this.mHeight = r2.height();
                    }
                    if (this.mSurfaceHolder != null) {
                        if (this.mSurface.isValid()) {
                            this.mSurfaceHolder.mSurface = this.mSurface;
                        }
                        this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                        this.mSurfaceHolder.mSurfaceLock.unlock();
                        if (this.mSurface.isValid()) {
                            if (!zIsValid) {
                                this.mSurfaceHolder.ungetCallbacks();
                                this.mIsCreating = true;
                                callbacks3 = this.mSurfaceHolder.getCallbacks();
                                if (callbacks3 != null) {
                                    while (i8 < r2) {
                                        callback.surfaceCreated(this.mSurfaceHolder);
                                    }
                                }
                                z15 = true;
                            }
                            if (z15) {
                                while (i7 < r2) {
                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                }
                            } else {
                                while (i7 < r2) {
                                    callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                                }
                            }
                            this.mIsCreating = false;
                        } else if (zIsValid) {
                            this.mSurfaceHolder.ungetCallbacks();
                            callbacks = this.mSurfaceHolder.getCallbacks();
                            if (callbacks != null) {
                                while (i6 < r2) {
                                    callback3.surfaceDestroyed(this.mSurfaceHolder);
                                }
                            }
                            this.mSurfaceHolder.mSurfaceLock.lock();
                            this.mSurfaceHolder.mSurface = new Surface();
                            this.mSurfaceHolder.mSurfaceLock.unlock();
                        }
                    }
                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                    if (threadedRenderer != null) {
                        threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                        this.mNeedsRendererSetup = false;
                    }
                    if (this.mStopped) {
                        if ((iRelayoutWindow & 1) != 0) {
                            z16 = true;
                        } else {
                            z16 = false;
                        }
                        if (!ensureTouchModeLocally(z16)) {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        } else {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        }
                    } else {
                        if ((iRelayoutWindow & 1) != 0) {
                            z16 = true;
                        } else {
                            z16 = false;
                        }
                        if (!ensureTouchModeLocally(z16)) {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        } else {
                            rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                            rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            measuredWidth = view2.getMeasuredWidth();
                            measuredHeight = view2.getMeasuredHeight();
                            if (layoutParams3.horizontalWeight > 0.0f) {
                                rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                                z17 = true;
                            } else {
                                z17 = false;
                            }
                            if (layoutParams3.verticalWeight > 0.0f) {
                                rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                                z17 = true;
                            }
                            if (z17) {
                                performMeasure(rootMeasureSpec, rootMeasureSpec2);
                            }
                            z4 = true;
                        }
                    }
                    i5 = iRelayoutWindow;
                }
                z12 = false;
                if ((iRelayoutWindow & 16) != 0) {
                    z25 = true;
                } else {
                    z25 = false;
                }
                if ((iRelayoutWindow & 8) != 0) {
                    z26 = true;
                } else {
                    z26 = false;
                }
                if (z25) {
                    z27 = true;
                } else {
                    z27 = true;
                }
                if (this.mDragResizing != z27) {
                    if (z27) {
                        if (z25) {
                            i9 = 0;
                        } else {
                            i9 = 1;
                        }
                        this.mResizeMode = i9;
                        startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                    } else {
                        endDragResizing();
                    }
                }
                if (!this.mUseMTRenderer) {
                    if (z27) {
                        this.mCanvasOffsetX = this.mWinFrame.left;
                        this.mCanvasOffsetY = this.mWinFrame.top;
                    } else {
                        this.mCanvasOffsetY = 0;
                        this.mCanvasOffsetX = 0;
                    }
                }
                r2 = obj;
                this.mAttachInfo.mWindowLeft = r2.left;
                this.mAttachInfo.mWindowTop = r2.top;
                if (this.mWidth == r2.width()) {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                } else {
                    this.mWidth = r2.width();
                    this.mHeight = r2.height();
                }
                if (this.mSurfaceHolder != null) {
                    if (this.mSurface.isValid()) {
                        this.mSurfaceHolder.mSurface = this.mSurface;
                    }
                    this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                    this.mSurfaceHolder.mSurfaceLock.unlock();
                    if (this.mSurface.isValid()) {
                        if (!zIsValid) {
                            this.mSurfaceHolder.ungetCallbacks();
                            this.mIsCreating = true;
                            callbacks3 = this.mSurfaceHolder.getCallbacks();
                            if (callbacks3 != null) {
                                while (i8 < r2) {
                                    callback.surfaceCreated(this.mSurfaceHolder);
                                }
                            }
                            z15 = true;
                        }
                        if (z15) {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        } else {
                            while (i7 < r2) {
                                callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                            }
                        }
                        this.mIsCreating = false;
                    } else if (zIsValid) {
                        this.mSurfaceHolder.ungetCallbacks();
                        callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            while (i6 < r2) {
                                callback3.surfaceDestroyed(this.mSurfaceHolder);
                            }
                        }
                        this.mSurfaceHolder.mSurfaceLock.lock();
                        this.mSurfaceHolder.mSurface = new Surface();
                        this.mSurfaceHolder.mSurfaceLock.unlock();
                    }
                }
                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                if (threadedRenderer != null) {
                    threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                    this.mNeedsRendererSetup = false;
                }
                if (this.mStopped) {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                } else {
                    if ((iRelayoutWindow & 1) != 0) {
                        z16 = true;
                    } else {
                        z16 = false;
                    }
                    if (!ensureTouchModeLocally(z16)) {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    } else {
                        rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                        rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        measuredWidth = view2.getMeasuredWidth();
                        measuredHeight = view2.getMeasuredHeight();
                        if (layoutParams3.horizontalWeight > 0.0f) {
                            rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                            z17 = true;
                        } else {
                            z17 = false;
                        }
                        if (layoutParams3.verticalWeight > 0.0f) {
                            rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                            z17 = true;
                        }
                        if (z17) {
                            performMeasure(rootMeasureSpec, rootMeasureSpec2);
                        }
                        z4 = true;
                    }
                }
                i5 = iRelayoutWindow;
            } else if (!this.mSurface.isValid()) {
                if (this.mLastScrolledFocus != null) {
                    this.mLastScrolledFocus.clear();
                }
                this.mCurScrollY = 0;
                this.mScrollY = 0;
                if (this.mView instanceof RootViewSurfaceTaker) {
                    ((RootViewSurfaceTaker) this.mView).onRootViewScrollYChanged(0);
                }
                if (this.mScroller != null) {
                    this.mScroller.abortAnimation();
                }
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    this.mAttachInfo.mThreadedRenderer.destroy();
                }
            } else if (generationId == this.mSurface.getGenerationId()) {
                this.mFullRedrawNeeded = true;
                this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
            } else {
                this.mFullRedrawNeeded = true;
                this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
            }
            z11 = false;
            z12 = false;
            if ((iRelayoutWindow & 16) != 0) {
                z25 = true;
            } else {
                z25 = false;
            }
            if ((iRelayoutWindow & 8) != 0) {
                z26 = true;
            } else {
                z26 = false;
            }
            if (z25) {
                z27 = true;
            } else {
                z27 = true;
            }
            if (this.mDragResizing != z27) {
                if (z27) {
                    if (z25) {
                        i9 = 0;
                    } else {
                        i9 = 1;
                    }
                    this.mResizeMode = i9;
                    startDragResizing(this.mPendingBackDropFrame, this.mWinFrame.equals(this.mPendingBackDropFrame), this.mPendingVisibleInsets, this.mPendingStableInsets, this.mResizeMode);
                } else {
                    endDragResizing();
                }
            }
            if (!this.mUseMTRenderer) {
                if (z27) {
                    this.mCanvasOffsetX = this.mWinFrame.left;
                    this.mCanvasOffsetY = this.mWinFrame.top;
                } else {
                    this.mCanvasOffsetY = 0;
                    this.mCanvasOffsetX = 0;
                }
            }
            r2 = obj;
            this.mAttachInfo.mWindowLeft = r2.left;
            this.mAttachInfo.mWindowTop = r2.top;
            if (this.mWidth == r2.width()) {
                this.mWidth = r2.width();
                this.mHeight = r2.height();
            } else {
                this.mWidth = r2.width();
                this.mHeight = r2.height();
            }
            if (this.mSurfaceHolder != null) {
                if (this.mSurface.isValid()) {
                    this.mSurfaceHolder.mSurface = this.mSurface;
                }
                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                this.mSurfaceHolder.mSurfaceLock.unlock();
                if (this.mSurface.isValid()) {
                    if (!zIsValid) {
                        this.mSurfaceHolder.ungetCallbacks();
                        this.mIsCreating = true;
                        callbacks3 = this.mSurfaceHolder.getCallbacks();
                        if (callbacks3 != null) {
                            while (i8 < r2) {
                                callback.surfaceCreated(this.mSurfaceHolder);
                            }
                        }
                        z15 = true;
                    }
                    if (z15) {
                        while (i7 < r2) {
                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                        }
                    } else {
                        while (i7 < r2) {
                            callback2.surfaceChanged(this.mSurfaceHolder, layoutParams3.format, this.mWidth, this.mHeight);
                        }
                    }
                    this.mIsCreating = false;
                } else if (zIsValid) {
                    this.mSurfaceHolder.ungetCallbacks();
                    callbacks = this.mSurfaceHolder.getCallbacks();
                    if (callbacks != null) {
                        while (i6 < r2) {
                            callback3.surfaceDestroyed(this.mSurfaceHolder);
                        }
                    }
                    this.mSurfaceHolder.mSurfaceLock.lock();
                    this.mSurfaceHolder.mSurface = new Surface();
                    this.mSurfaceHolder.mSurfaceLock.unlock();
                }
            }
            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
            if (threadedRenderer != null) {
                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                this.mNeedsRendererSetup = false;
            }
            if (this.mStopped) {
                if ((iRelayoutWindow & 1) != 0) {
                    z16 = true;
                } else {
                    z16 = false;
                }
                if (!ensureTouchModeLocally(z16)) {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                } else {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                }
            } else {
                if ((iRelayoutWindow & 1) != 0) {
                    z16 = true;
                } else {
                    z16 = false;
                }
                if (!ensureTouchModeLocally(z16)) {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                } else {
                    rootMeasureSpec = getRootMeasureSpec(this.mWidth, layoutParams3.width);
                    rootMeasureSpec2 = getRootMeasureSpec(this.mHeight, layoutParams3.height);
                    performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    measuredWidth = view2.getMeasuredWidth();
                    measuredHeight = view2.getMeasuredHeight();
                    if (layoutParams3.horizontalWeight > 0.0f) {
                        rootMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth + ((int) ((this.mWidth - measuredWidth) * layoutParams3.horizontalWeight)), 1073741824);
                        z17 = true;
                    } else {
                        z17 = false;
                    }
                    if (layoutParams3.verticalWeight > 0.0f) {
                        rootMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight + ((int) ((this.mHeight - measuredHeight) * layoutParams3.verticalWeight)), 1073741824);
                        z17 = true;
                    }
                    if (z17) {
                        performMeasure(rootMeasureSpec, rootMeasureSpec2);
                    }
                    z4 = true;
                }
            }
            i5 = iRelayoutWindow;
        }
        if (z4) {
            z30 = false;
        } else {
            z30 = false;
        }
        if (z30) {
            z31 = true;
        } else {
            z31 = true;
        }
        if (z30) {
            performLayout(layoutParams3, this.mWidth, this.mHeight);
            if ((view2.mPrivateFlags & 512) != 0) {
                view2.getLocationInWindow(this.mTmpLocation);
                Region region9 = this.mTransparentRegion;
                int[] iArr9 = this.mTmpLocation;
                region9.set(iArr9[0], iArr9[1], (iArr9[0] + view2.mRight) - view2.mLeft, (this.mTmpLocation[1] + view2.mBottom) - view2.mTop);
                view2.gatherTransparentRegion(this.mTransparentRegion);
                translator2 = this.mTranslator;
                if (translator2 != null) {
                    translator2.translateRegionInWindowToScreen(this.mTransparentRegion);
                }
                if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                    this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                    this.mFullRedrawNeeded = true;
                    this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                }
            }
        }
        if (z31) {
            this.mAttachInfo.mRecomputeGlobalAttributes = false;
            this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
        }
        if (z8) {
            internalInsetsInfo = this.mAttachInfo.mGivenInternalInsets;
            internalInsetsInfo.reset();
            this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(internalInsetsInfo);
            this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !internalInsetsInfo.isEmpty();
            if (!z10) {
                this.mLastGivenInsets.set(internalInsetsInfo);
                translator = this.mTranslator;
                if (translator != null) {
                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                } else {
                    translatedContentInsets = internalInsetsInfo.contentInsets;
                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                }
                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
            } else {
                this.mLastGivenInsets.set(internalInsetsInfo);
                translator = this.mTranslator;
                if (translator != null) {
                    translatedContentInsets = translator.getTranslatedContentInsets(internalInsetsInfo.contentInsets);
                    translatedVisibleInsets = this.mTranslator.getTranslatedVisibleInsets(internalInsetsInfo.visibleInsets);
                    translatedTouchableArea = this.mTranslator.getTranslatedTouchableArea(internalInsetsInfo.touchableRegion);
                } else {
                    translatedContentInsets = internalInsetsInfo.contentInsets;
                    translatedVisibleInsets = internalInsetsInfo.visibleInsets;
                    translatedTouchableArea = internalInsetsInfo.touchableRegion;
                }
                this.mWindowSession.setInsets(this.mWindow, internalInsetsInfo.mTouchableInsets, translatedContentInsets, translatedVisibleInsets, translatedTouchableArea);
            }
        }
        if (this.mFirst) {
            if (!sAlwaysAssignFocus) {
                view = this.mView;
                if (view != null) {
                    this.mView.restoreDefaultFocus();
                }
            } else {
                view = this.mView;
                if (view != null) {
                    this.mView.restoreDefaultFocus();
                }
            }
        }
        if (z42) {
        }
        if (this.mAttachInfo.mHasWindowFocus) {
            z33 = false;
        } else {
            z33 = false;
        }
        if (z33) {
            z34 = false;
        } else {
            z34 = false;
        }
        if (z34) {
            this.mLostWindowFocus = false;
        } else if (!z33) {
            this.mLostWindowFocus = true;
        }
        if (!z32) {
            layoutParams2 = this.mWindowAttributes;
            if (layoutParams2 == null) {
                z35 = false;
            } else {
                z35 = true;
            }
            if (!z35) {
                view2.sendAccessibilityEvent(32);
            }
        } else {
            layoutParams2 = this.mWindowAttributes;
            if (layoutParams2 == null) {
                z35 = false;
            } else {
                z35 = true;
            }
            if (!z35) {
                view2.sendAccessibilityEvent(32);
            }
        }
        this.mFirst = false;
        this.mWillDrawSoon = false;
        this.mNewSurfaceNeeded = false;
        this.mActivityRelaunched = false;
        this.mViewVisibility = hostVisibility;
        this.mHadWindowFocus = z33;
        if (z33) {
            z36 = true;
        } else {
            z36 = true;
        }
        if ((i5 & 2) != 0) {
            reportNextDraw();
        }
        if (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw()) {
            z37 = z36;
        } else {
            z37 = z36;
        }
        if (!z37) {
            if (z9) {
                scheduleTraversals();
            } else {
                arrayList = this.mPendingTransitions;
                if (arrayList != null) {
                    while (i10 < this.mPendingTransitions.size()) {
                        this.mPendingTransitions.get(i10).endChangingAnimations();
                    }
                    this.mPendingTransitions.clear();
                }
            }
        } else if (z9) {
            scheduleTraversals();
        } else {
            arrayList = this.mPendingTransitions;
            if (arrayList != null) {
                while (i10 < this.mPendingTransitions.size()) {
                    this.mPendingTransitions.get(i10).endChangingAnimations();
                }
                this.mPendingTransitions.clear();
            }
        }
        this.mIsInTraversal = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeHandleWindowMove(Rect rect) {
        boolean z = (this.mAttachInfo.mWindowLeft == rect.left && this.mAttachInfo.mWindowTop == rect.top) ? false : true;
        if (z) {
            CompatibilityInfo.Translator translator = this.mTranslator;
            if (translator != null) {
                translator.translateRectInScreenToAppWinFrame(rect);
            }
            this.mAttachInfo.mWindowLeft = rect.left;
            this.mAttachInfo.mWindowTop = rect.top;
        }
        if (z || this.mAttachInfo.mNeedsUpdateLightCenter) {
            if (this.mAttachInfo.mThreadedRenderer != null) {
                this.mAttachInfo.mThreadedRenderer.setLightCenter(this.mAttachInfo);
            }
            this.mAttachInfo.mNeedsUpdateLightCenter = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWindowFocusChanged() {
        synchronized (this) {
            if (this.mWindowFocusChanged) {
                this.mWindowFocusChanged = false;
                boolean z = this.mUpcomingWindowFocus;
                boolean z2 = this.mUpcomingInTouchMode;
                if (this.mAdded) {
                    profileRendering(z);
                    if (z) {
                        ensureTouchModeLocally(z2);
                        if (this.mAttachInfo.mThreadedRenderer != null && this.mSurface.isValid()) {
                            this.mFullRedrawNeeded = true;
                            try {
                                WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
                                this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, layoutParams != null ? layoutParams.surfaceInsets : null);
                            } catch (Surface.OutOfResourcesException e) {
                                Log.e(this.mTag, "OutOfResourcesException locking surface", e);
                                try {
                                    if (!this.mWindowSession.outOfMemory(this.mWindow)) {
                                        Slog.w(this.mTag, "No processes killed for memory; killing self");
                                        Process.killProcess(Process.myPid());
                                    }
                                } catch (RemoteException unused) {
                                }
                                ViewRootHandler viewRootHandler = this.mHandler;
                                viewRootHandler.sendMessageDelayed(viewRootHandler.obtainMessage(6), 500L);
                                return;
                            }
                        }
                    }
                    this.mAttachInfo.mHasWindowFocus = z;
                    this.mLastWasImTarget = WindowManager.LayoutParams.mayUseInputMethod(this.mWindowAttributes.flags);
                    InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                    if (inputMethodManagerPeekInstance != null && this.mLastWasImTarget && !isInLocalFocusMode()) {
                        inputMethodManagerPeekInstance.onPreWindowFocus(this.mView, z);
                    }
                    if (this.mView != null) {
                        this.mAttachInfo.mKeyDispatchState.reset();
                        this.mView.dispatchWindowFocusChanged(z);
                        this.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(z);
                        if (this.mAttachInfo.mTooltipHost != null) {
                            this.mAttachInfo.mTooltipHost.hideTooltip();
                        }
                    }
                    if (z) {
                        if (inputMethodManagerPeekInstance != null && this.mLastWasImTarget && !isInLocalFocusMode()) {
                            Slog.i(TAG, "Func handleWindowFocusChanged mView=" + this.mView + ", mView.findFocus()=" + this.mView.findFocus());
                            View view = this.mView;
                            inputMethodManagerPeekInstance.onPostWindowFocus(view, view.findFocus(), this.mWindowAttributes.softInputMode, this.mHasHadWindowFocus ^ true, this.mWindowAttributes.flags);
                        }
                        this.mWindowAttributes.softInputMode &= -257;
                        ((WindowManager.LayoutParams) this.mView.getLayoutParams()).softInputMode &= -257;
                        this.mHasHadWindowFocus = true;
                        fireAccessibilityFocusEventIfHasFocusedNode();
                    } else if (this.mPointerCapture) {
                        handlePointerCaptureChanged(false);
                    }
                }
                this.mFirstInputStage.onWindowFocusChanged(z);
            }
        }
    }

    private void fireAccessibilityFocusEventIfHasFocusedNode() {
        View viewFindFocus;
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && (viewFindFocus = this.mView.findFocus()) != null) {
            AccessibilityNodeProvider accessibilityNodeProvider = viewFindFocus.getAccessibilityNodeProvider();
            if (accessibilityNodeProvider == null) {
                viewFindFocus.sendAccessibilityEvent(8);
                return;
            }
            AccessibilityNodeInfo accessibilityNodeInfoFindFocusedVirtualNode = findFocusedVirtualNode(accessibilityNodeProvider);
            if (accessibilityNodeInfoFindFocusedVirtualNode != null) {
                int virtualDescendantId = AccessibilityNodeInfo.getVirtualDescendantId(accessibilityNodeInfoFindFocusedVirtualNode.getSourceNodeId());
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(8);
                accessibilityEventObtain.setSource(viewFindFocus, virtualDescendantId);
                accessibilityEventObtain.setPackageName(accessibilityNodeInfoFindFocusedVirtualNode.getPackageName());
                accessibilityEventObtain.setChecked(accessibilityNodeInfoFindFocusedVirtualNode.isChecked());
                accessibilityEventObtain.setContentDescription(accessibilityNodeInfoFindFocusedVirtualNode.getContentDescription());
                accessibilityEventObtain.setPassword(accessibilityNodeInfoFindFocusedVirtualNode.isPassword());
                accessibilityEventObtain.getText().add(accessibilityNodeInfoFindFocusedVirtualNode.getText());
                accessibilityEventObtain.setEnabled(accessibilityNodeInfoFindFocusedVirtualNode.isEnabled());
                viewFindFocus.getParent().requestSendAccessibilityEvent(viewFindFocus, accessibilityEventObtain);
                accessibilityNodeInfoFindFocusedVirtualNode.recycle();
            }
        }
    }

    private AccessibilityNodeInfo findFocusedVirtualNode(AccessibilityNodeProvider accessibilityNodeProvider) {
        AccessibilityNodeInfo accessibilityNodeInfoFindFocus = accessibilityNodeProvider.findFocus(1);
        if (accessibilityNodeInfoFindFocus != null) {
            return accessibilityNodeInfoFindFocus;
        }
        if (!this.mContext.isAutofillCompatibilityEnabled()) {
            return null;
        }
        AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(-1);
        if (accessibilityNodeInfoCreateAccessibilityNodeInfo.isFocused()) {
            return accessibilityNodeInfoCreateAccessibilityNodeInfo;
        }
        LinkedList linkedList = new LinkedList();
        linkedList.offer(accessibilityNodeInfoCreateAccessibilityNodeInfo);
        while (!linkedList.isEmpty()) {
            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) linkedList.poll();
            LongArray childNodeIds = accessibilityNodeInfo.getChildNodeIds();
            if (childNodeIds != null && childNodeIds.size() > 0) {
                int size = childNodeIds.size();
                for (int i = 0; i < size; i++) {
                    AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo2 = accessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(childNodeIds.get(i)));
                    if (accessibilityNodeInfoCreateAccessibilityNodeInfo2 != null) {
                        if (accessibilityNodeInfoCreateAccessibilityNodeInfo2.isFocused()) {
                            return accessibilityNodeInfoCreateAccessibilityNodeInfo2;
                        }
                        linkedList.offer(accessibilityNodeInfoCreateAccessibilityNodeInfo2);
                    }
                }
                accessibilityNodeInfo.recycle();
            }
        }
        return null;
    }

    private void handleOutOfResourcesException(Surface.OutOfResourcesException outOfResourcesException) {
        Log.e(this.mTag, "OutOfResourcesException initializing HW surface", outOfResourcesException);
        try {
            if (!this.mWindowSession.outOfMemory(this.mWindow) && Process.myUid() != 1000) {
                Slog.w(this.mTag, "No processes killed for memory; killing self");
                Process.killProcess(Process.myPid());
            }
        } catch (RemoteException unused) {
        }
        this.mLayoutRequested = true;
    }

    private void performMeasure(int i, int i2) {
        if (this.mView == null) {
            return;
        }
        Trace.traceBegin(8L, "measure");
        try {
            this.mView.measure(i, i2);
        } finally {
            Trace.traceEnd(8L);
        }
    }

    boolean isInLayout() {
        return this.mInLayout;
    }

    boolean requestLayoutDuringLayout(View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            return true;
        }
        if (!this.mLayoutRequesters.contains(view)) {
            this.mLayoutRequesters.add(view);
        }
        return !this.mHandlingLayoutInLayoutRequest;
    }

    private void performLayout(WindowManager.LayoutParams layoutParams, int i, int i2) {
        ArrayList<View> validLayoutRequesters;
        this.mLayoutRequested = false;
        this.mScrollMayChange = true;
        this.mInLayout = true;
        View view = this.mView;
        if (view == null) {
            return;
        }
        Trace.traceBegin(8L, TtmlUtils.TAG_LAYOUT);
        try {
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            this.mInLayout = false;
            if (this.mLayoutRequesters.size() > 0 && (validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, false)) != null) {
                this.mHandlingLayoutInLayoutRequest = true;
                int size = validLayoutRequesters.size();
                for (int i3 = 0; i3 < size; i3++) {
                    View view2 = validLayoutRequesters.get(i3);
                    Log.w("View", "requestLayout() improperly called by " + view2 + " during layout: running second layout pass");
                    view2.requestLayout();
                }
                measureHierarchy(view, layoutParams, this.mView.getContext().getResources(), i, i2);
                this.mInLayout = true;
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                this.mHandlingLayoutInLayoutRequest = false;
                final ArrayList<View> validLayoutRequesters2 = getValidLayoutRequesters(this.mLayoutRequesters, true);
                if (validLayoutRequesters2 != null) {
                    getRunQueue().post(new Runnable() { // from class: android.view.ViewRootImpl.2
                        @Override // java.lang.Runnable
                        public void run() {
                            int size2 = validLayoutRequesters2.size();
                            for (int i4 = 0; i4 < size2; i4++) {
                                View view3 = (View) validLayoutRequesters2.get(i4);
                                Log.w("View", "requestLayout() improperly called by " + view3 + " during second layout pass: posting in next frame");
                                view3.requestLayout();
                            }
                        }
                    });
                }
            }
            Trace.traceEnd(8L);
            this.mInLayout = false;
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    private ArrayList<View> getValidLayoutRequesters(ArrayList<View> arrayList, boolean z) {
        boolean z2;
        int size = arrayList.size();
        ArrayList<View> arrayList2 = null;
        for (int i = 0; i < size; i++) {
            View view = arrayList.get(i);
            if (view != null && view.mAttachInfo != null && view.mParent != null && (z || (view.mPrivateFlags & 4096) == 4096)) {
                View view2 = view;
                while (true) {
                    if (view2 == null) {
                        z2 = false;
                        break;
                    }
                    if ((view2.mViewFlags & 12) == 8) {
                        z2 = true;
                        break;
                    }
                    view2 = view2.mParent instanceof View ? (View) view2.mParent : null;
                }
                if (!z2) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList<>();
                    }
                    arrayList2.add(view);
                }
            }
        }
        if (!z) {
            for (int i2 = 0; i2 < size; i2++) {
                View view3 = arrayList.get(i2);
                while (view3 != null && (view3.mPrivateFlags & 4096) != 0) {
                    view3.mPrivateFlags &= -4097;
                    view3 = view3.mParent instanceof View ? (View) view3.mParent : null;
                }
            }
        }
        arrayList.clear();
        return arrayList2;
    }

    @Override // android.view.ViewParent
    public void requestTransparentRegion(View view) {
        checkThread();
        View view2 = this.mView;
        if (view2 == view) {
            view2.mPrivateFlags |= 512;
            this.mWindowAttributesChanged = true;
            this.mWindowAttributesChangesFlag = 0;
            requestLayout();
        }
    }

    private static int getRootMeasureSpec(int i, int i2) {
        if (i2 == -2) {
            return View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
        }
        if (i2 == -1) {
            return View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
    }

    @Override // android.view.ThreadedRenderer.DrawCallbacks
    public void onPreDraw(DisplayListCanvas displayListCanvas) {
        if (this.mCurScrollY != 0 && this.mHardwareYOffset != 0 && this.mAttachInfo.mThreadedRenderer.isOpaque()) {
            displayListCanvas.drawColor(-16777216);
        }
        displayListCanvas.translate(-this.mHardwareXOffset, -this.mHardwareYOffset);
    }

    @Override // android.view.ThreadedRenderer.DrawCallbacks
    public void onPostDraw(DisplayListCanvas displayListCanvas) {
        drawAccessibilityFocusedDrawableIfNeeded(displayListCanvas);
        if (this.mUseMTRenderer) {
            for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                this.mWindowCallbacks.get(size).onPostDraw(displayListCanvas);
            }
        }
    }

    void outputDisplayList(View view) {
        view.mRenderNode.output();
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.serializeDisplayListTree();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void profileRendering(boolean z) {
        if (this.mProfileRendering) {
            this.mRenderProfilingEnabled = z;
            Choreographer.FrameCallback frameCallback = this.mRenderProfiler;
            if (frameCallback != null) {
                this.mChoreographer.removeFrameCallback(frameCallback);
            }
            if (this.mRenderProfilingEnabled) {
                if (this.mRenderProfiler == null) {
                    this.mRenderProfiler = new Choreographer.FrameCallback() { // from class: android.view.ViewRootImpl.3
                        @Override // android.view.Choreographer.FrameCallback
                        public void doFrame(long j) {
                            ViewRootImpl.this.mDirty.set(0, 0, ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight);
                            ViewRootImpl.this.scheduleTraversals();
                            if (ViewRootImpl.this.mRenderProfilingEnabled) {
                                ViewRootImpl.this.mChoreographer.postFrameCallback(ViewRootImpl.this.mRenderProfiler);
                            }
                        }
                    };
                }
                this.mChoreographer.postFrameCallback(this.mRenderProfiler);
                return;
            }
            this.mRenderProfiler = null;
        }
    }

    private void trackFPS() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.mFpsStartTime < 0) {
            this.mFpsPrevTime = jCurrentTimeMillis;
            this.mFpsStartTime = jCurrentTimeMillis;
            this.mFpsNumFrames = 0;
            return;
        }
        this.mFpsNumFrames++;
        String hexString = Integer.toHexString(System.identityHashCode(this));
        long j = jCurrentTimeMillis - this.mFpsPrevTime;
        long j2 = jCurrentTimeMillis - this.mFpsStartTime;
        Log.v(this.mTag, CarConfigManager.HEX_VALUE_DEFAULT + hexString + "\tFrame time:\t" + j);
        this.mFpsPrevTime = jCurrentTimeMillis;
        if (j2 > 1000) {
            float f = (this.mFpsNumFrames * 1000.0f) / j2;
            Log.v(this.mTag, CarConfigManager.HEX_VALUE_DEFAULT + hexString + "\tFPS:\t" + f);
            this.mFpsStartTime = jCurrentTimeMillis;
            this.mFpsNumFrames = 0;
        }
    }

    void drawPending() {
        this.mDrawsNeededToReport++;
    }

    void pendingDrawFinished() {
        int i = this.mDrawsNeededToReport;
        if (i == 0) {
            throw new RuntimeException("Unbalanced drawPending/pendingDrawFinished calls");
        }
        int i2 = i - 1;
        this.mDrawsNeededToReport = i2;
        if (i2 == 0) {
            reportDrawFinished();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postDrawFinished() {
        this.mHandler.sendEmptyMessage(29);
    }

    private void reportDrawFinished() {
        try {
            this.mDrawsNeededToReport = 0;
            this.mWindowSession.finishDrawing(this.mWindow);
        } catch (RemoteException unused) {
        }
    }

    private void performDraw() {
        boolean z = true;
        if ((this.mAttachInfo.mDisplayState != 1 || this.mReportNextDraw) && this.mView != null) {
            boolean z2 = this.mFullRedrawNeeded || this.mReportNextDraw;
            this.mFullRedrawNeeded = false;
            this.mIsDrawing = true;
            Trace.traceBegin(8L, "draw");
            if (this.mReportNextDraw && this.mAttachInfo.mThreadedRenderer != null && this.mAttachInfo.mThreadedRenderer.isEnabled()) {
                this.mAttachInfo.mThreadedRenderer.setFrameCompleteCallback(new ThreadedRenderer.FrameCompleteCallback() { // from class: android.view.-$$Lambda$ViewRootImpl$zmAX2p20-kqxknxcUyGhSNjsJvM
                    @Override // android.view.ThreadedRenderer.FrameCompleteCallback
                    public final void onFrameComplete(long j) {
                        this.f$0.lambda$performDraw$0$ViewRootImpl(j);
                    }
                });
            } else {
                z = false;
            }
            try {
                boolean zDraw = draw(z2);
                if (z && !zDraw) {
                    this.mAttachInfo.mThreadedRenderer.setFrameCompleteCallback(null);
                    z = false;
                }
                this.mIsDrawing = false;
                Trace.traceEnd(8L);
                if (this.mAttachInfo.mPendingAnimatingRenderNodes != null) {
                    int size = this.mAttachInfo.mPendingAnimatingRenderNodes.size();
                    for (int i = 0; i < size; i++) {
                        this.mAttachInfo.mPendingAnimatingRenderNodes.get(i).endAllAnimators();
                    }
                    this.mAttachInfo.mPendingAnimatingRenderNodes.clear();
                }
                if (this.mReportNextDraw) {
                    this.mReportNextDraw = false;
                    CountDownLatch countDownLatch = this.mWindowDrawCountDown;
                    if (countDownLatch != null) {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException unused) {
                            Log.e(this.mTag, "Window redraw count down interrupted!");
                        }
                        this.mWindowDrawCountDown = null;
                    }
                    if (this.mAttachInfo.mThreadedRenderer != null) {
                        this.mAttachInfo.mThreadedRenderer.setStopped(this.mStopped);
                    }
                    if (this.mSurfaceHolder != null && this.mSurface.isValid()) {
                        new SurfaceCallbackHelper(new Runnable() { // from class: android.view.-$$Lambda$ViewRootImpl$dznxCZGM2R1fsBljsJKomLjBRoM
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.postDrawFinished();
                            }
                        }).dispatchSurfaceRedrawNeededAsync(this.mSurfaceHolder, this.mSurfaceHolder.getCallbacks());
                    } else {
                        if (z) {
                            return;
                        }
                        if (this.mAttachInfo.mThreadedRenderer != null) {
                            this.mAttachInfo.mThreadedRenderer.fence();
                        }
                        pendingDrawFinished();
                    }
                }
            } catch (Throwable th) {
                this.mIsDrawing = false;
                Trace.traceEnd(8L);
                throw th;
            }
        }
    }

    public /* synthetic */ void lambda$performDraw$0$ViewRootImpl(long j) {
        pendingDrawFinished();
    }

    /* JADX WARN: Code duplicated, block: B:60:0x00ef  */
    private boolean draw(boolean z) {
        int currY;
        boolean z2;
        boolean z3;
        Scroller scroller;
        Surface surface = this.mSurface;
        boolean z4 = false;
        if (!surface.isValid()) {
            return false;
        }
        if (!sFirstDrawComplete) {
            synchronized (sFirstDrawHandlers) {
                sFirstDrawComplete = true;
                int size = sFirstDrawHandlers.size();
                for (int i = 0; i < size; i++) {
                    this.mHandler.post(sFirstDrawHandlers.get(i));
                }
            }
        }
        scrollToRectOrFocus(null, false);
        if (this.mAttachInfo.mViewScrollChanged) {
            this.mAttachInfo.mViewScrollChanged = false;
            this.mAttachInfo.mTreeObserver.dispatchOnScrollChanged();
        }
        Scroller scroller2 = this.mScroller;
        boolean z5 = scroller2 != null && scroller2.computeScrollOffset();
        if (z5) {
            currY = this.mScroller.getCurrY();
        } else {
            currY = this.mScrollY;
        }
        if (this.mCurScrollY != currY) {
            this.mCurScrollY = currY;
            KeyEvent.Callback callback = this.mView;
            if (callback instanceof RootViewSurfaceTaker) {
                ((RootViewSurfaceTaker) callback).onRootViewScrollYChanged(currY);
            }
            z2 = true;
        } else {
            z2 = z;
        }
        float f = this.mAttachInfo.mApplicationScale;
        boolean z6 = this.mAttachInfo.mScalingRequired;
        Rect rect = this.mDirty;
        if (this.mSurfaceHolder != null) {
            rect.setEmpty();
            if (z5 && (scroller = this.mScroller) != null) {
                scroller.abortAnimation();
            }
            return false;
        }
        if (z2) {
            this.mAttachInfo.mIgnoreDirtyState = true;
            rect.set(0, 0, (int) ((this.mWidth * f) + 0.5f), (int) ((this.mHeight * f) + 0.5f));
        }
        this.mAttachInfo.mTreeObserver.dispatchOnDraw();
        int i2 = -this.mCanvasOffsetX;
        int i3 = (-this.mCanvasOffsetY) + currY;
        WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
        Rect rect2 = layoutParams != null ? layoutParams.surfaceInsets : null;
        if (rect2 != null) {
            i2 -= rect2.left;
            i3 -= rect2.top;
            rect.offset(rect2.left, rect2.right);
        }
        int i4 = i3;
        int i5 = i2;
        Drawable drawable = this.mAttachInfo.mAccessibilityFocusDrawable;
        if (drawable != null) {
            Rect rect3 = this.mAttachInfo.mTmpInvalRect;
            if (!getAccessibilityFocusedRect(rect3)) {
                rect3.setEmpty();
            }
            if (rect3.equals(drawable.getBounds())) {
                z3 = false;
            } else {
                z3 = true;
            }
        } else {
            z3 = false;
        }
        this.mAttachInfo.mDrawingTime = this.mChoreographer.getFrameTimeNanos() / TimeUtils.NANOS_PER_MS;
        if (!rect.isEmpty() || this.mIsAnimating || z3) {
            if (this.mAttachInfo.mThreadedRenderer != null && this.mAttachInfo.mThreadedRenderer.isEnabled()) {
                boolean z7 = z3 || this.mInvalidateRootRequested;
                this.mInvalidateRootRequested = false;
                this.mIsAnimating = false;
                if (this.mHardwareYOffset != i4 || this.mHardwareXOffset != i5) {
                    this.mHardwareYOffset = i4;
                    this.mHardwareXOffset = i5;
                    z7 = true;
                }
                if (z7) {
                    this.mAttachInfo.mThreadedRenderer.invalidateRoot();
                }
                rect.setEmpty();
                boolean zUpdateContentDrawBounds = updateContentDrawBounds();
                if (this.mReportNextDraw) {
                    this.mAttachInfo.mThreadedRenderer.setStopped(false);
                }
                if (zUpdateContentDrawBounds) {
                    requestDrawWindow();
                }
                ThreadedRenderer.FrameDrawingCallback frameDrawingCallback = this.mNextRtFrameCallback;
                this.mNextRtFrameCallback = null;
                this.mAttachInfo.mThreadedRenderer.draw(this.mView, this.mAttachInfo, this, frameDrawingCallback);
                z4 = true;
            } else {
                if (this.mAttachInfo.mThreadedRenderer != null && !this.mAttachInfo.mThreadedRenderer.isEnabled() && this.mAttachInfo.mThreadedRenderer.isRequested() && this.mSurface.isValid()) {
                    try {
                        this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, rect2);
                        this.mFullRedrawNeeded = true;
                        scheduleTraversals();
                        return false;
                    } catch (Surface.OutOfResourcesException e) {
                        handleOutOfResourcesException(e);
                        return false;
                    }
                }
                if (!drawSoftware(surface, this.mAttachInfo, i5, i4, z6, rect, rect2)) {
                    return false;
                }
            }
        }
        if (z5) {
            this.mFullRedrawNeeded = true;
            scheduleTraversals();
        }
        return z4;
    }

    private boolean drawSoftware(Surface surface, View.AttachInfo attachInfo, int i, int i2, boolean z, Rect rect, Rect rect2) {
        int i3;
        int i4;
        boolean z2;
        if (rect2 != null) {
            i3 = rect2.left + i;
            i4 = rect2.top + i2;
        } else {
            i3 = i;
            i4 = i2;
        }
        try {
            try {
                rect.offset(-i3, -i4);
                int i5 = rect.left;
                int i6 = rect.top;
                int i7 = rect.right;
                int i8 = rect.bottom;
                Canvas canvasLockCanvas = this.mSurface.lockCanvas(rect);
                if (i5 != rect.left || i6 != rect.top || i7 != rect.right || i8 != rect.bottom) {
                    attachInfo.mIgnoreDirtyState = true;
                }
                canvasLockCanvas.setDensity(this.mDensity);
                rect.offset(i3, i4);
                try {
                    try {
                        if (canvasLockCanvas.isOpaque() && i2 == 0 && i == 0) {
                            z2 = false;
                        } else {
                            z2 = false;
                            canvasLockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        }
                        rect.setEmpty();
                        this.mIsAnimating = z2;
                        this.mView.mPrivateFlags |= 32;
                        try {
                            canvasLockCanvas.translate(-i, -i2);
                            if (this.mTranslator != null) {
                                this.mTranslator.translateCanvas(canvasLockCanvas);
                            }
                            canvasLockCanvas.setScreenDensity(z ? this.mNoncompatDensity : 0);
                            attachInfo.mSetIgnoreDirtyState = false;
                            this.mView.draw(canvasLockCanvas);
                            drawAccessibilityFocusedDrawableIfNeeded(canvasLockCanvas);
                            if (!attachInfo.mSetIgnoreDirtyState) {
                                attachInfo.mIgnoreDirtyState = false;
                            }
                            surface.unlockCanvasAndPost(canvasLockCanvas);
                            return true;
                        } catch (Throwable th) {
                            if (!attachInfo.mSetIgnoreDirtyState) {
                                attachInfo.mIgnoreDirtyState = false;
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        surface.unlockCanvasAndPost(canvasLockCanvas);
                        throw th2;
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(this.mTag, "Could not unlock surface", e);
                    this.mLayoutRequested = true;
                    return false;
                }
            } catch (Throwable th3) {
                rect.offset(i3, i4);
                throw th3;
            }
        } catch (Surface.OutOfResourcesException e2) {
            handleOutOfResourcesException(e2);
            rect.offset(i3, i4);
            return false;
        } catch (IllegalArgumentException e3) {
            Log.e(this.mTag, "Could not lock surface", e3);
            this.mLayoutRequested = true;
            rect.offset(i3, i4);
            return false;
        }
    }

    private void drawAccessibilityFocusedDrawableIfNeeded(Canvas canvas) {
        Rect rect = this.mAttachInfo.mTmpInvalRect;
        if (getAccessibilityFocusedRect(rect)) {
            Drawable accessibilityFocusedDrawable = getAccessibilityFocusedDrawable();
            if (accessibilityFocusedDrawable != null) {
                accessibilityFocusedDrawable.setBounds(rect);
                accessibilityFocusedDrawable.draw(canvas);
                return;
            }
            return;
        }
        if (this.mAttachInfo.mAccessibilityFocusDrawable != null) {
            this.mAttachInfo.mAccessibilityFocusDrawable.setBounds(0, 0, 0, 0);
        }
    }

    private boolean getAccessibilityFocusedRect(Rect rect) {
        View view;
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mView.mContext);
        if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled() && (view = this.mAccessibilityFocusedHost) != null && view.mAttachInfo != null) {
            if (view.getAccessibilityNodeProvider() == null) {
                view.getBoundsOnScreen(rect, true);
            } else {
                AccessibilityNodeInfo accessibilityNodeInfo = this.mAccessibilityFocusedVirtualView;
                if (accessibilityNodeInfo != null) {
                    accessibilityNodeInfo.getBoundsInScreen(rect);
                }
            }
            View.AttachInfo attachInfo = this.mAttachInfo;
            rect.offset(0, attachInfo.mViewRootImpl.mScrollY);
            rect.offset(-attachInfo.mWindowLeft, -attachInfo.mWindowTop);
            if (!rect.intersect(0, 0, attachInfo.mViewRootImpl.mWidth, attachInfo.mViewRootImpl.mHeight)) {
                rect.setEmpty();
            }
            return !rect.isEmpty();
        }
        return false;
    }

    private Drawable getAccessibilityFocusedDrawable() {
        if (this.mAttachInfo.mAccessibilityFocusDrawable == null) {
            TypedValue typedValue = new TypedValue();
            if (this.mView.mContext.getTheme().resolveAttribute(R.attr.accessibilityFocusedDrawable, typedValue, true)) {
                this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getDrawable(typedValue.resourceId);
            }
        }
        return this.mAttachInfo.mAccessibilityFocusDrawable;
    }

    public void requestInvalidateRootRenderNode() {
        this.mInvalidateRootRequested = true;
    }

    /* JADX WARN: Code duplicated, block: B:47:0x00c6  */
    boolean scrollToRectOrFocus(Rect rect, boolean z) {
        int i;
        boolean z2;
        int i2;
        int height;
        Rect rect2 = this.mAttachInfo.mContentInsets;
        Rect rect3 = this.mAttachInfo.mVisibleInsets;
        if (rect3.left > rect2.left || rect3.top > rect2.top || rect3.right > rect2.right || rect3.bottom > rect2.bottom) {
            i = this.mScrollY;
            View viewFindFocus = this.mView.findFocus();
            if (viewFindFocus == null) {
                return false;
            }
            WeakReference<View> weakReference = this.mLastScrolledFocus;
            View view = weakReference != null ? weakReference.get() : null;
            if (viewFindFocus != view) {
                rect = null;
            }
            if (viewFindFocus == view && !this.mScrollMayChange && rect == null) {
                z2 = false;
            } else {
                this.mLastScrolledFocus = new WeakReference<>(viewFindFocus);
                this.mScrollMayChange = false;
                if (viewFindFocus.getGlobalVisibleRect(this.mVisRect, null)) {
                    if (rect == null) {
                        viewFindFocus.getFocusedRect(this.mTempRect);
                        View view2 = this.mView;
                        if (view2 instanceof ViewGroup) {
                            ((ViewGroup) view2).offsetDescendantRectToMyCoords(viewFindFocus, this.mTempRect);
                        }
                    } else {
                        this.mTempRect.set(rect);
                    }
                    if (this.mTempRect.intersect(this.mVisRect)) {
                        if (this.mTempRect.height() <= (this.mView.getHeight() - rect3.top) - rect3.bottom) {
                            if (this.mTempRect.top < rect3.top) {
                                i2 = this.mTempRect.top;
                                height = rect3.top;
                            } else if (this.mTempRect.bottom > this.mView.getHeight() - rect3.bottom) {
                                i2 = this.mTempRect.bottom;
                                height = this.mView.getHeight() - rect3.bottom;
                            } else {
                                i = 0;
                            }
                            i = i2 - height;
                        }
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                } else {
                    z2 = false;
                }
            }
        } else {
            z2 = false;
            i = 0;
        }
        if (i != this.mScrollY) {
            if (!z) {
                if (this.mScroller == null) {
                    this.mScroller = new Scroller(this.mView.getContext());
                }
                Scroller scroller = this.mScroller;
                int i3 = this.mScrollY;
                scroller.startScroll(0, i3, 0, i - i3);
            } else {
                Scroller scroller2 = this.mScroller;
                if (scroller2 != null) {
                    scroller2.abortAnimation();
                }
            }
            this.mScrollY = i;
        }
        return z2;
    }

    public View getAccessibilityFocusedHost() {
        return this.mAccessibilityFocusedHost;
    }

    public AccessibilityNodeInfo getAccessibilityFocusedVirtualView() {
        return this.mAccessibilityFocusedVirtualView;
    }

    void setAccessibilityFocus(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo accessibilityNodeInfo2 = this.mAccessibilityFocusedVirtualView;
        if (accessibilityNodeInfo2 != null) {
            View view2 = this.mAccessibilityFocusedHost;
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            view2.clearAccessibilityFocusNoCallbacks(64);
            AccessibilityNodeProvider accessibilityNodeProvider = view2.getAccessibilityNodeProvider();
            if (accessibilityNodeProvider != null) {
                accessibilityNodeInfo2.getBoundsInParent(this.mTempRect);
                view2.invalidate(this.mTempRect);
                accessibilityNodeProvider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(accessibilityNodeInfo2.getSourceNodeId()), 128, null);
            }
            accessibilityNodeInfo2.recycle();
        }
        View view3 = this.mAccessibilityFocusedHost;
        if (view3 != null && view3 != view) {
            view3.clearAccessibilityFocusNoCallbacks(64);
        }
        this.mAccessibilityFocusedHost = view;
        this.mAccessibilityFocusedVirtualView = accessibilityNodeInfo;
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.invalidateRoot();
        }
    }

    boolean hasPointerCapture() {
        return this.mPointerCapture;
    }

    void requestPointerCapture(boolean z) {
        if (this.mPointerCapture == z) {
            return;
        }
        InputManager.getInstance().requestPointerCapture(this.mAttachInfo.mWindowToken, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePointerCaptureChanged(boolean z) {
        if (this.mPointerCapture == z) {
            return;
        }
        this.mPointerCapture = z;
        View view = this.mView;
        if (view != null) {
            view.dispatchPointerCaptureChanged(z);
        }
    }

    @Override // android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void clearChildFocus(View view) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void focusableViewAvailable(View view) {
        checkThread();
        View view2 = this.mView;
        if (view2 != null) {
            if (!view2.hasFocus()) {
                if (sAlwaysAssignFocus || !this.mAttachInfo.mInTouchMode) {
                    view.requestFocus();
                    return;
                }
                return;
            }
            View viewFindFocus = this.mView.findFocus();
            if ((viewFindFocus instanceof ViewGroup) && ((ViewGroup) viewFindFocus).getDescendantFocusability() == 262144 && isViewDescendantOf(view, viewFindFocus)) {
                view.requestFocus();
            }
        }
    }

    @Override // android.view.ViewParent
    public void recomputeViewAttributes(View view) {
        checkThread();
        if (this.mView == view) {
            this.mAttachInfo.mRecomputeGlobalAttributes = true;
            if (this.mWillDrawSoon) {
                return;
            }
            scheduleTraversals();
        }
    }

    void dispatchDetachedFromWindow() {
        InputQueue inputQueue;
        this.mFirstInputStage.onDetachedFromWindow();
        View view = this.mView;
        if (view != null && view.mAttachInfo != null) {
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);
            this.mView.dispatchDetachedFromWindow();
        }
        this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        this.mAccessibilityManager.removeHighTextContrastStateChangeListener(this.mHighContrastTextManager);
        removeSendWindowContentChangedCallback();
        destroyHardwareRenderer();
        setAccessibilityFocus(null, null);
        this.mView.assignParent(null);
        this.mView = null;
        this.mAttachInfo.mRootView = null;
        this.mSurface.release();
        InputQueue.Callback callback = this.mInputQueueCallback;
        if (callback != null && (inputQueue = this.mInputQueue) != null) {
            callback.onInputQueueDestroyed(inputQueue);
            this.mInputQueue.dispose();
            this.mInputQueueCallback = null;
            this.mInputQueue = null;
        }
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver != null) {
            windowInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        try {
            this.mWindowSession.remove(this.mWindow);
        } catch (RemoteException unused) {
        }
        InputChannel inputChannel = this.mInputChannel;
        if (inputChannel != null) {
            inputChannel.dispose();
            this.mInputChannel = null;
        }
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
        unscheduleTraversals();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performConfigurationChange(MergedConfiguration mergedConfiguration, boolean z, int i) {
        if (mergedConfiguration == null) {
            throw new IllegalArgumentException("No merged config provided.");
        }
        Configuration globalConfiguration = mergedConfiguration.getGlobalConfiguration();
        Configuration overrideConfiguration = mergedConfiguration.getOverrideConfiguration();
        CompatibilityInfo compatibilityInfo = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
        if (!compatibilityInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            Configuration configuration = new Configuration(globalConfiguration);
            compatibilityInfo.applyToConfiguration(this.mNoncompatDensity, configuration);
            globalConfiguration = configuration;
        }
        synchronized (sConfigCallbacks) {
            for (int size = sConfigCallbacks.size() - 1; size >= 0; size--) {
                sConfigCallbacks.get(size).onConfigurationChanged(globalConfiguration);
            }
        }
        this.mLastReportedMergedConfiguration.setConfiguration(globalConfiguration, overrideConfiguration);
        this.mForceNextConfigUpdate = z;
        ActivityConfigCallback activityConfigCallback = this.mActivityConfigCallback;
        if (activityConfigCallback != null) {
            activityConfigCallback.onConfigurationChanged(overrideConfiguration, i);
        } else {
            updateConfiguration(i);
        }
        this.mForceNextConfigUpdate = false;
    }

    public void updateConfiguration(int i) {
        View view = this.mView;
        if (view == null) {
            return;
        }
        Resources resources = view.getResources();
        Configuration configuration = resources.getConfiguration();
        if (i != -1) {
            onMovedToDisplay(i, configuration);
        }
        if (this.mForceNextConfigUpdate || this.mLastConfigurationFromResources.diff(configuration) != 0) {
            this.mDisplay = ResourcesManager.getInstance().getAdjustedDisplay(this.mDisplay.getDisplayId(), resources);
            int layoutDirection = this.mLastConfigurationFromResources.getLayoutDirection();
            int layoutDirection2 = configuration.getLayoutDirection();
            this.mLastConfigurationFromResources.setTo(configuration);
            if (layoutDirection != layoutDirection2 && this.mViewLayoutDirectionInitial == 2) {
                this.mView.setLayoutDirection(layoutDirection2);
            }
            this.mView.dispatchConfigurationChanged(configuration);
            this.mForceNextWindowRelayout = true;
            requestLayout();
        }
    }

    public static boolean isViewDescendantOf(View view, View view2) {
        if (view == view2) {
            return true;
        }
        Object parent = view.getParent();
        return (parent instanceof ViewGroup) && isViewDescendantOf((View) parent, view2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                forceLayout(viewGroup.getChildAt(i));
            }
        }
    }

    final class ViewRootHandler extends Handler {
        ViewRootHandler() {
        }

        @Override // android.os.Handler
        public String getMessageName(Message message) {
            switch (message.what) {
                case 1:
                    return "MSG_INVALIDATE";
                case 2:
                    return "MSG_INVALIDATE_RECT";
                case 3:
                    return "MSG_DIE";
                case 4:
                    return "MSG_RESIZED";
                case 5:
                    return "MSG_RESIZED_REPORT";
                case 6:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case 7:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case 8:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case 9:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case 10:
                case 20:
                case 22:
                case 26:
                default:
                    return super.getMessageName(message);
                case 11:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case 12:
                    return "MSG_DISPATCH_KEY_FROM_AUTOFILL";
                case 13:
                    return "MSG_CHECK_FOCUS";
                case 14:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case 15:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case 16:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case 17:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case 18:
                    return "MSG_UPDATE_CONFIGURATION";
                case 19:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case 21:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case 23:
                    return "MSG_WINDOW_MOVED";
                case 24:
                    return "MSG_SYNTHESIZE_INPUT_EVENT";
                case 25:
                    return "MSG_DISPATCH_WINDOW_SHOWN";
                case 27:
                    return "MSG_UPDATE_POINTER_ICON";
                case 28:
                    return "MSG_POINTER_CAPTURE_CHANGED";
                case 29:
                    return "MSG_DRAW_FINISHED";
            }
        }

        @Override // android.os.Handler
        public boolean sendMessageAtTime(Message message, long j) {
            if (message.what == 26 && message.obj == null) {
                throw new NullPointerException("Attempted to call MSG_REQUEST_KEYBOARD_SHORTCUTS with null receiver:");
            }
            return super.sendMessageAtTime(message, j);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((View) message.obj).invalidate();
                    return;
                case 2:
                    View.AttachInfo.InvalidateInfo invalidateInfo = (View.AttachInfo.InvalidateInfo) message.obj;
                    invalidateInfo.target.invalidate(invalidateInfo.left, invalidateInfo.top, invalidateInfo.right, invalidateInfo.bottom);
                    invalidateInfo.recycle();
                    return;
                case 3:
                    ViewRootImpl.this.doDie();
                    return;
                case 4:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    if (ViewRootImpl.this.mWinFrame.equals(someArgs.arg1) && ViewRootImpl.this.mPendingOverscanInsets.equals(someArgs.arg5) && ViewRootImpl.this.mPendingContentInsets.equals(someArgs.arg2) && ViewRootImpl.this.mPendingStableInsets.equals(someArgs.arg6) && ViewRootImpl.this.mPendingDisplayCutout.get().equals(someArgs.arg9) && ViewRootImpl.this.mPendingVisibleInsets.equals(someArgs.arg3) && ViewRootImpl.this.mPendingOutsets.equals(someArgs.arg7) && ViewRootImpl.this.mPendingBackDropFrame.equals(someArgs.arg8) && someArgs.arg4 == null && someArgs.argi1 == 0 && ViewRootImpl.this.mDisplay.getDisplayId() == someArgs.argi3) {
                        return;
                    }
                    break;
                case 5:
                    break;
                case 6:
                    ViewRootImpl.this.handleWindowFocusChanged();
                    return;
                case 7:
                    SomeArgs someArgs2 = (SomeArgs) message.obj;
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) someArgs2.arg1, (InputEventReceiver) someArgs2.arg2, 0, true);
                    someArgs2.recycle();
                    return;
                case 8:
                    ViewRootImpl.this.handleAppVisibility(message.arg1 != 0);
                    return;
                case 9:
                    ViewRootImpl.this.handleGetNewSurface();
                    return;
                case 10:
                case 20:
                default:
                    return;
                case 11:
                    KeyEvent keyEventChangeFlags = (KeyEvent) message.obj;
                    if ((keyEventChangeFlags.getFlags() & 8) != 0) {
                        keyEventChangeFlags = KeyEvent.changeFlags(keyEventChangeFlags, keyEventChangeFlags.getFlags() & (-9));
                    }
                    ViewRootImpl.this.enqueueInputEvent(keyEventChangeFlags, null, 1, true);
                    return;
                case 12:
                    ViewRootImpl.this.enqueueInputEvent((KeyEvent) message.obj, null, 0, true);
                    return;
                case 13:
                    InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                    if (inputMethodManagerPeekInstance != null) {
                        inputMethodManagerPeekInstance.checkFocus();
                        return;
                    }
                    return;
                case 14:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.mView.onCloseSystemDialogs((String) message.obj);
                        return;
                    }
                    return;
                case 15:
                case 16:
                    DragEvent dragEvent = (DragEvent) message.obj;
                    dragEvent.mLocalState = ViewRootImpl.this.mLocalDragState;
                    ViewRootImpl.this.handleDragEvent(dragEvent);
                    return;
                case 17:
                    ViewRootImpl.this.handleDispatchSystemUiVisibilityChanged((SystemUiVisibilityInfo) message.obj);
                    return;
                case 18:
                    Configuration globalConfiguration = (Configuration) message.obj;
                    if (globalConfiguration.isOtherSeqNewer(ViewRootImpl.this.mLastReportedMergedConfiguration.getMergedConfiguration())) {
                        globalConfiguration = ViewRootImpl.this.mLastReportedMergedConfiguration.getGlobalConfiguration();
                    }
                    ViewRootImpl.this.mPendingMergedConfiguration.setConfiguration(globalConfiguration, ViewRootImpl.this.mLastReportedMergedConfiguration.getOverrideConfiguration());
                    ViewRootImpl viewRootImpl = ViewRootImpl.this;
                    viewRootImpl.performConfigurationChange(viewRootImpl.mPendingMergedConfiguration, false, -1);
                    return;
                case 19:
                    ViewRootImpl.this.mProcessInputEventsScheduled = false;
                    ViewRootImpl.this.doProcessInputEvents();
                    return;
                case 21:
                    ViewRootImpl.this.setAccessibilityFocus(null, null);
                    return;
                case 22:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl viewRootImpl2 = ViewRootImpl.this;
                        viewRootImpl2.invalidateWorld(viewRootImpl2.mView);
                        return;
                    }
                    return;
                case 23:
                    if (ViewRootImpl.this.mAdded) {
                        int iWidth = ViewRootImpl.this.mWinFrame.width();
                        int iHeight = ViewRootImpl.this.mWinFrame.height();
                        int i = message.arg1;
                        int i2 = message.arg2;
                        ViewRootImpl.this.mWinFrame.left = i;
                        ViewRootImpl.this.mWinFrame.right = i + iWidth;
                        ViewRootImpl.this.mWinFrame.top = i2;
                        ViewRootImpl.this.mWinFrame.bottom = i2 + iHeight;
                        ViewRootImpl.this.mPendingBackDropFrame.set(ViewRootImpl.this.mWinFrame);
                        ViewRootImpl viewRootImpl3 = ViewRootImpl.this;
                        viewRootImpl3.maybeHandleWindowMove(viewRootImpl3.mWinFrame);
                        return;
                    }
                    return;
                case 24:
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) message.obj, null, 32, true);
                    return;
                case 25:
                    ViewRootImpl.this.handleDispatchWindowShown();
                    return;
                case 26:
                    ViewRootImpl.this.handleRequestKeyboardShortcuts((IResultReceiver) message.obj, message.arg1);
                    return;
                case 27:
                    ViewRootImpl.this.resetPointerIcon((MotionEvent) message.obj);
                    return;
                case 28:
                    ViewRootImpl.this.handlePointerCaptureChanged(message.arg1 != 0);
                    return;
                case 29:
                    ViewRootImpl.this.pendingDrawFinished();
                    return;
            }
            if (ViewRootImpl.this.mAdded) {
                SomeArgs someArgs3 = (SomeArgs) message.obj;
                int i3 = someArgs3.argi3;
                MergedConfiguration mergedConfiguration = (MergedConfiguration) someArgs3.arg4;
                boolean z = ViewRootImpl.this.mDisplay.getDisplayId() != i3;
                if (!ViewRootImpl.this.mLastReportedMergedConfiguration.equals(mergedConfiguration)) {
                    ViewRootImpl.this.performConfigurationChange(mergedConfiguration, false, z ? i3 : -1);
                } else if (z) {
                    ViewRootImpl viewRootImpl4 = ViewRootImpl.this;
                    viewRootImpl4.onMovedToDisplay(i3, viewRootImpl4.mLastConfigurationFromResources);
                }
                boolean z2 = (ViewRootImpl.this.mWinFrame.equals(someArgs3.arg1) && ViewRootImpl.this.mPendingOverscanInsets.equals(someArgs3.arg5) && ViewRootImpl.this.mPendingContentInsets.equals(someArgs3.arg2) && ViewRootImpl.this.mPendingStableInsets.equals(someArgs3.arg6) && ViewRootImpl.this.mPendingDisplayCutout.get().equals(someArgs3.arg9) && ViewRootImpl.this.mPendingVisibleInsets.equals(someArgs3.arg3) && ViewRootImpl.this.mPendingOutsets.equals(someArgs3.arg7)) ? false : true;
                ViewRootImpl.this.mWinFrame.set((Rect) someArgs3.arg1);
                ViewRootImpl.this.mPendingOverscanInsets.set((Rect) someArgs3.arg5);
                ViewRootImpl.this.mPendingContentInsets.set((Rect) someArgs3.arg2);
                ViewRootImpl.this.mPendingStableInsets.set((Rect) someArgs3.arg6);
                ViewRootImpl.this.mPendingDisplayCutout.set((DisplayCutout) someArgs3.arg9);
                ViewRootImpl.this.mPendingVisibleInsets.set((Rect) someArgs3.arg3);
                ViewRootImpl.this.mPendingOutsets.set((Rect) someArgs3.arg7);
                ViewRootImpl.this.mPendingBackDropFrame.set((Rect) someArgs3.arg8);
                ViewRootImpl.this.mForceNextWindowRelayout = someArgs3.argi1 != 0;
                ViewRootImpl.this.mPendingAlwaysConsumeNavBar = someArgs3.argi2 != 0;
                someArgs3.recycle();
                if (message.what == 5) {
                    ViewRootImpl.this.reportNextDraw();
                }
                if (ViewRootImpl.this.mView != null && z2) {
                    ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
                }
                ViewRootImpl.this.requestLayout();
            }
        }
    }

    boolean ensureTouchMode(boolean z) {
        if (this.mAttachInfo.mInTouchMode == z) {
            return false;
        }
        try {
            this.mWindowSession.setInTouchMode(z);
            return ensureTouchModeLocally(z);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean ensureTouchModeLocally(boolean z) {
        if (this.mAttachInfo.mInTouchMode == z) {
            return false;
        }
        this.mAttachInfo.mInTouchMode = z;
        this.mAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(z);
        return z ? enterTouchMode() : leaveTouchMode();
    }

    private boolean enterTouchMode() {
        View viewFindFocus;
        View view = this.mView;
        if (view == null || !view.hasFocus() || (viewFindFocus = this.mView.findFocus()) == null || viewFindFocus.isFocusableInTouchMode()) {
            return false;
        }
        ViewGroup viewGroupFindAncestorToTakeFocusInTouchMode = findAncestorToTakeFocusInTouchMode(viewFindFocus);
        if (viewGroupFindAncestorToTakeFocusInTouchMode != null) {
            return viewGroupFindAncestorToTakeFocusInTouchMode.requestFocus();
        }
        viewFindFocus.clearFocusInternal(null, true, false);
        return true;
    }

    private static ViewGroup findAncestorToTakeFocusInTouchMode(View view) {
        ViewParent parent = view.getParent();
        while (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.getDescendantFocusability() == 262144 && viewGroup.isFocusableInTouchMode()) {
                return viewGroup;
            }
            if (viewGroup.isRootNamespace()) {
                return null;
            }
            parent = viewGroup.getParent();
        }
        return null;
    }

    private boolean leaveTouchMode() {
        View view = this.mView;
        if (view == null) {
            return false;
        }
        if (view.hasFocus()) {
            View viewFindFocus = this.mView.findFocus();
            if (!(viewFindFocus instanceof ViewGroup) || ((ViewGroup) viewFindFocus).getDescendantFocusability() != 262144) {
                return false;
            }
        }
        return this.mView.restoreDefaultFocus();
    }

    abstract class InputStage {
        protected static final int FINISH_HANDLED = 1;
        protected static final int FINISH_NOT_HANDLED = 2;
        protected static final int FORWARD = 0;
        private final InputStage mNext;

        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            return 0;
        }

        public InputStage(InputStage inputStage) {
            this.mNext = inputStage;
        }

        public final void deliver(QueuedInputEvent queuedInputEvent) {
            if ((queuedInputEvent.mFlags & 4) != 0) {
                forward(queuedInputEvent);
            } else if (shouldDropInputEvent(queuedInputEvent)) {
                finish(queuedInputEvent, false);
            } else {
                apply(queuedInputEvent, onProcess(queuedInputEvent));
            }
        }

        protected void finish(QueuedInputEvent queuedInputEvent, boolean z) {
            queuedInputEvent.mFlags |= 4;
            if (z) {
                queuedInputEvent.mFlags |= 8;
            }
            forward(queuedInputEvent);
        }

        protected void forward(QueuedInputEvent queuedInputEvent) {
            onDeliverToNext(queuedInputEvent);
        }

        protected void apply(QueuedInputEvent queuedInputEvent, int i) {
            if (i == 0) {
                forward(queuedInputEvent);
                return;
            }
            if (i == 1) {
                finish(queuedInputEvent, true);
            } else {
                if (i == 2) {
                    finish(queuedInputEvent, false);
                    return;
                }
                throw new IllegalArgumentException("Invalid result: " + i);
            }
        }

        protected void onDeliverToNext(QueuedInputEvent queuedInputEvent) {
            InputStage inputStage = this.mNext;
            if (inputStage == null) {
                ViewRootImpl.this.finishInputEvent(queuedInputEvent);
            } else {
                inputStage.deliver(queuedInputEvent);
            }
        }

        protected void onWindowFocusChanged(boolean z) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.onWindowFocusChanged(z);
            }
        }

        protected void onDetachedFromWindow() {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.onDetachedFromWindow();
            }
        }

        protected boolean shouldDropInputEvent(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                Slog.w(ViewRootImpl.this.mTag, "Dropping event due to root view being removed: " + queuedInputEvent.mEvent);
                return true;
            }
            if ((ViewRootImpl.this.mAttachInfo.mHasWindowFocus || queuedInputEvent.mEvent.isFromSource(2) || ViewRootImpl.this.isAutofillUiShowing()) && !ViewRootImpl.this.mStopped && ((!ViewRootImpl.this.mIsAmbientMode || queuedInputEvent.mEvent.isFromSource(1)) && (!ViewRootImpl.this.mPausedForTransition || isBack(queuedInputEvent.mEvent)))) {
                return false;
            }
            if (!ViewRootImpl.isTerminalInputEvent(queuedInputEvent.mEvent)) {
                Slog.w(ViewRootImpl.this.mTag, "Dropping event due to no window focus: " + queuedInputEvent.mEvent);
                return true;
            }
            queuedInputEvent.mEvent.cancel();
            Slog.w(ViewRootImpl.this.mTag, "Cancelling event due to no window focus: " + queuedInputEvent.mEvent);
            return false;
        }

        void dump(String str, PrintWriter printWriter) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.dump(str, printWriter);
            }
        }

        private boolean isBack(InputEvent inputEvent) {
            return (inputEvent instanceof KeyEvent) && ((KeyEvent) inputEvent).getKeyCode() == 4;
        }
    }

    abstract class AsyncInputStage extends InputStage {
        protected static final int DEFER = 3;
        private QueuedInputEvent mQueueHead;
        private int mQueueLength;
        private QueuedInputEvent mQueueTail;
        private final String mTraceCounter;

        public AsyncInputStage(InputStage inputStage, String str) {
            super(inputStage);
            this.mTraceCounter = str;
        }

        protected void defer(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags |= 2;
            enqueue(queuedInputEvent);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void forward(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags &= -3;
            QueuedInputEvent queuedInputEvent2 = this.mQueueHead;
            if (queuedInputEvent2 == null) {
                super.forward(queuedInputEvent);
                return;
            }
            int deviceId = queuedInputEvent.mEvent.getDeviceId();
            QueuedInputEvent queuedInputEvent3 = null;
            boolean z = false;
            while (queuedInputEvent2 != null && queuedInputEvent2 != queuedInputEvent) {
                if (!z && deviceId == queuedInputEvent2.mEvent.getDeviceId()) {
                    z = true;
                }
                queuedInputEvent3 = queuedInputEvent2;
                queuedInputEvent2 = queuedInputEvent2.mNext;
            }
            if (z) {
                if (queuedInputEvent2 == null) {
                    enqueue(queuedInputEvent);
                    return;
                }
                return;
            }
            if (queuedInputEvent2 != null) {
                queuedInputEvent2 = queuedInputEvent2.mNext;
                dequeue(queuedInputEvent, queuedInputEvent3);
            }
            super.forward(queuedInputEvent);
            QueuedInputEvent queuedInputEvent4 = queuedInputEvent3;
            while (true) {
                QueuedInputEvent queuedInputEvent5 = queuedInputEvent2;
                while (queuedInputEvent5 != null) {
                    if (deviceId == queuedInputEvent5.mEvent.getDeviceId()) {
                        if ((queuedInputEvent5.mFlags & 2) != 0) {
                            return;
                        }
                        queuedInputEvent2 = queuedInputEvent5.mNext;
                        dequeue(queuedInputEvent5, queuedInputEvent4);
                        super.forward(queuedInputEvent5);
                    } else {
                        QueuedInputEvent queuedInputEvent6 = queuedInputEvent5;
                        queuedInputEvent5 = queuedInputEvent5.mNext;
                        queuedInputEvent4 = queuedInputEvent6;
                    }
                }
                return;
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void apply(QueuedInputEvent queuedInputEvent, int i) {
            if (i == 3) {
                defer(queuedInputEvent);
            } else {
                super.apply(queuedInputEvent, i);
            }
        }

        private void enqueue(QueuedInputEvent queuedInputEvent) {
            QueuedInputEvent queuedInputEvent2 = this.mQueueTail;
            if (queuedInputEvent2 == null) {
                this.mQueueHead = queuedInputEvent;
                this.mQueueTail = queuedInputEvent;
            } else {
                queuedInputEvent2.mNext = queuedInputEvent;
                this.mQueueTail = queuedInputEvent;
            }
            int i = this.mQueueLength + 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        private void dequeue(QueuedInputEvent queuedInputEvent, QueuedInputEvent queuedInputEvent2) {
            if (queuedInputEvent2 == null) {
                this.mQueueHead = queuedInputEvent.mNext;
            } else {
                queuedInputEvent2.mNext = queuedInputEvent.mNext;
            }
            if (this.mQueueTail == queuedInputEvent) {
                this.mQueueTail = queuedInputEvent2;
            }
            queuedInputEvent.mNext = null;
            int i = this.mQueueLength - 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        @Override // android.view.ViewRootImpl.InputStage
        void dump(String str, PrintWriter printWriter) {
            printWriter.print(str);
            printWriter.print(getClass().getName());
            printWriter.print(": mQueueLength=");
            printWriter.println(this.mQueueLength);
            super.dump(str, printWriter);
        }
    }

    final class NativePreImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePreImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mInputQueue == null || !(queuedInputEvent.mEvent instanceof KeyEvent)) {
                return 0;
            }
            ViewRootImpl.this.mInputQueue.sendInputEvent(queuedInputEvent.mEvent, queuedInputEvent, true, this);
            return 3;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class ViewPreImeInputStage extends InputStage {
        public ViewPreImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchKeyEventPreIme((KeyEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }
    }

    final class ImeInputStage extends AsyncInputStage implements InputMethodManager.FinishedInputEventCallback {
        public ImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            InputMethodManager inputMethodManagerPeekInstance;
            if (!ViewRootImpl.this.mLastWasImTarget || ViewRootImpl.this.isInLocalFocusMode() || (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) == null) {
                return 0;
            }
            int iDispatchInputEvent = inputMethodManagerPeekInstance.dispatchInputEvent(queuedInputEvent.mEvent, queuedInputEvent, this, ViewRootImpl.this.mHandler);
            if (iDispatchInputEvent == 1) {
                return 1;
            }
            return iDispatchInputEvent == 0 ? 0 : 3;
        }

        @Override // android.view.inputmethod.InputMethodManager.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class EarlyPostImeInputStage extends InputStage {
        public EarlyPostImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            if ((queuedInputEvent.mEvent.getSource() & 2) != 0) {
                return processPointerEvent(queuedInputEvent);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            KeyEvent keyEvent = (KeyEvent) queuedInputEvent.mEvent;
            if (ViewRootImpl.this.mAttachInfo.mTooltipHost != null) {
                ViewRootImpl.this.mAttachInfo.mTooltipHost.handleTooltipKey(keyEvent);
            }
            if (ViewRootImpl.this.checkForLeavingTouchModeAndConsume(keyEvent)) {
                return 1;
            }
            ViewRootImpl.this.mFallbackEventHandler.preDispatchKeyEvent(keyEvent);
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent queuedInputEvent) {
            AutofillManager autofillManager;
            MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
            if (ViewRootImpl.this.mTranslator != null) {
                ViewRootImpl.this.mTranslator.translateEventInScreenToAppWindow(motionEvent);
            }
            int action = motionEvent.getAction();
            if (action == 0 || action == 8) {
                ViewRootImpl.this.ensureTouchMode(motionEvent.isFromSource(4098));
            }
            if (action == 0 && (autofillManager = ViewRootImpl.this.getAutofillManager()) != null) {
                autofillManager.requestHideFillUi();
            }
            if (action == 0 && ViewRootImpl.this.mAttachInfo.mTooltipHost != null) {
                ViewRootImpl.this.mAttachInfo.mTooltipHost.hideTooltip();
            }
            if (ViewRootImpl.this.mCurScrollY != 0) {
                motionEvent.offsetLocation(0.0f, ViewRootImpl.this.mCurScrollY);
            }
            if (!motionEvent.isTouchEvent()) {
                return 0;
            }
            ViewRootImpl.this.mLastTouchPoint.x = motionEvent.getRawX();
            ViewRootImpl.this.mLastTouchPoint.y = motionEvent.getRawY();
            ViewRootImpl.this.mLastTouchSource = motionEvent.getSource();
            return 0;
        }
    }

    final class NativePostImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePostImeInputStage(InputStage inputStage, String str) {
            super(inputStage, str);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mInputQueue == null) {
                return 0;
            }
            ViewRootImpl.this.mInputQueue.sendInputEvent(queuedInputEvent.mEvent, queuedInputEvent, false, this);
            return 3;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object obj, boolean z) {
            QueuedInputEvent queuedInputEvent = (QueuedInputEvent) obj;
            if (z) {
                finish(queuedInputEvent, true);
            } else {
                forward(queuedInputEvent);
            }
        }
    }

    final class ViewPostImeInputStage extends InputStage {
        public ViewPostImeInputStage(InputStage inputStage) {
            super(inputStage);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            if (queuedInputEvent.mEvent instanceof KeyEvent) {
                return processKeyEvent(queuedInputEvent);
            }
            int source = queuedInputEvent.mEvent.getSource();
            if ((source & 2) != 0) {
                return processPointerEvent(queuedInputEvent);
            }
            if ((source & 4) != 0) {
                return processTrackballEvent(queuedInputEvent);
            }
            return processGenericMotionEvent(queuedInputEvent);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent queuedInputEvent) {
            if (ViewRootImpl.this.mUnbufferedInputDispatch && (queuedInputEvent.mEvent instanceof MotionEvent) && ((MotionEvent) queuedInputEvent.mEvent).isTouchEvent() && ViewRootImpl.isTerminalInputEvent(queuedInputEvent.mEvent)) {
                ViewRootImpl.this.mUnbufferedInputDispatch = false;
                ViewRootImpl.this.scheduleConsumeBatchedInput();
            }
            super.onDeliverToNext(queuedInputEvent);
        }

        private boolean performFocusNavigation(KeyEvent keyEvent) {
            int i;
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 61) {
                switch (keyCode) {
                    case 19:
                        i = !keyEvent.hasNoModifiers() ? 0 : 33;
                        break;
                    case 20:
                        i = !keyEvent.hasNoModifiers() ? 0 : 130;
                        break;
                    case 21:
                        i = !keyEvent.hasNoModifiers() ? 0 : 17;
                        break;
                    case 22:
                        i = !keyEvent.hasNoModifiers() ? 0 : 66;
                        break;
                    default:
                        i = 0;
                        break;
                }
            } else if (keyEvent.hasNoModifiers()) {
                i = 2;
            } else {
                i = keyEvent.hasModifiers(1) ? 1 : 0;
            }
            if (i != 0) {
                View viewFindFocus = ViewRootImpl.this.mView.findFocus();
                if (viewFindFocus != null) {
                    View viewFocusSearch = viewFindFocus.focusSearch(i);
                    if (viewFocusSearch != null && viewFocusSearch != viewFindFocus) {
                        viewFindFocus.getFocusedRect(ViewRootImpl.this.mTempRect);
                        if (ViewRootImpl.this.mView instanceof ViewGroup) {
                            ((ViewGroup) ViewRootImpl.this.mView).offsetDescendantRectToMyCoords(viewFindFocus, ViewRootImpl.this.mTempRect);
                            ((ViewGroup) ViewRootImpl.this.mView).offsetRectIntoDescendantCoords(viewFocusSearch, ViewRootImpl.this.mTempRect);
                        }
                        if (viewFocusSearch.requestFocus(i, ViewRootImpl.this.mTempRect)) {
                            ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
                            return true;
                        }
                    }
                    if (ViewRootImpl.this.mView.dispatchUnhandledMove(viewFindFocus, i)) {
                        return true;
                    }
                } else if (ViewRootImpl.this.mView.restoreDefaultFocus()) {
                    return true;
                }
            }
            return false;
        }

        private boolean performKeyboardGroupNavigation(int i) {
            View viewKeyboardNavigationClusterSearch;
            View viewFindFocus = ViewRootImpl.this.mView.findFocus();
            if (viewFindFocus == null && ViewRootImpl.this.mView.restoreDefaultFocus()) {
                return true;
            }
            if (viewFindFocus == null) {
                viewKeyboardNavigationClusterSearch = ViewRootImpl.this.keyboardNavigationClusterSearch(null, i);
            } else {
                viewKeyboardNavigationClusterSearch = viewFindFocus.keyboardNavigationClusterSearch(null, i);
            }
            int i2 = (i == 2 || i == 1) ? 130 : i;
            if (viewKeyboardNavigationClusterSearch != null && viewKeyboardNavigationClusterSearch.isRootNamespace()) {
                if (viewKeyboardNavigationClusterSearch.restoreFocusNotInCluster()) {
                    ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
                    return true;
                }
                viewKeyboardNavigationClusterSearch = ViewRootImpl.this.keyboardNavigationClusterSearch(null, i);
            }
            if (viewKeyboardNavigationClusterSearch == null || !viewKeyboardNavigationClusterSearch.restoreFocusInCluster(i2)) {
                return false;
            }
            ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            return true;
        }

        /* JADX WARN: Code duplicated, block: B:24:0x0062  */
        private int processKeyEvent(QueuedInputEvent queuedInputEvent) {
            int i;
            KeyEvent keyEvent = (KeyEvent) queuedInputEvent.mEvent;
            if (ViewRootImpl.this.mUnhandledKeyManager.preViewDispatch(keyEvent) || ViewRootImpl.this.mView.dispatchKeyEvent(keyEvent)) {
                return 1;
            }
            if (shouldDropInputEvent(queuedInputEvent)) {
                return 2;
            }
            if (ViewRootImpl.this.mUnhandledKeyManager.dispatch(ViewRootImpl.this.mView, keyEvent)) {
                return 1;
            }
            if (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 61) {
                i = 0;
            } else if (KeyEvent.metaStateHasModifiers(keyEvent.getMetaState(), 65536)) {
                i = 2;
            } else if (KeyEvent.metaStateHasModifiers(keyEvent.getMetaState(), 65537)) {
                i = 1;
            } else {
                i = 0;
            }
            if (keyEvent.getAction() == 0 && !KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState()) && keyEvent.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyEvent.getKeyCode()) && i == 0) {
                if (ViewRootImpl.this.mView.dispatchKeyShortcutEvent(keyEvent)) {
                    return 1;
                }
                if (shouldDropInputEvent(queuedInputEvent)) {
                    return 2;
                }
            }
            if (ViewRootImpl.this.mFallbackEventHandler.dispatchKeyEvent(keyEvent)) {
                return 1;
            }
            if (shouldDropInputEvent(queuedInputEvent)) {
                return 2;
            }
            if (keyEvent.getAction() == 0) {
                if (i != 0) {
                    if (performKeyboardGroupNavigation(i)) {
                        return 1;
                    }
                } else if (performFocusNavigation(keyEvent)) {
                    return 1;
                }
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent queuedInputEvent) {
            MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
            ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested = false;
            ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = true;
            boolean zDispatchPointerEvent = ViewRootImpl.this.mView.dispatchPointerEvent(motionEvent);
            maybeUpdatePointerIcon(motionEvent);
            ViewRootImpl.this.maybeUpdateTooltip(motionEvent);
            ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = false;
            if (ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested && !ViewRootImpl.this.mUnbufferedInputDispatch) {
                ViewRootImpl.this.mUnbufferedInputDispatch = true;
                if (ViewRootImpl.this.mConsumeBatchedInputScheduled) {
                    ViewRootImpl.this.scheduleConsumeBatchedInputImmediately();
                }
            }
            return zDispatchPointerEvent ? 1 : 0;
        }

        private void maybeUpdatePointerIcon(MotionEvent motionEvent) {
            if (motionEvent.getPointerCount() == 1 && motionEvent.isFromSource(8194)) {
                if (motionEvent.getActionMasked() == 9 || motionEvent.getActionMasked() == 10) {
                    ViewRootImpl.this.mPointerIconType = 1;
                }
                if (motionEvent.getActionMasked() == 10 || ViewRootImpl.this.updatePointerIcon(motionEvent) || motionEvent.getActionMasked() != 7) {
                    return;
                }
                ViewRootImpl.this.mPointerIconType = 1;
            }
        }

        private int processTrackballEvent(QueuedInputEvent queuedInputEvent) {
            MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
            return ((!motionEvent.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE) || (ViewRootImpl.this.hasPointerCapture() && !ViewRootImpl.this.mView.dispatchCapturedPointerEvent(motionEvent))) && !ViewRootImpl.this.mView.dispatchTrackballEvent(motionEvent)) ? 0 : 1;
        }

        private int processGenericMotionEvent(QueuedInputEvent queuedInputEvent) {
            return ViewRootImpl.this.mView.dispatchGenericMotionEvent((MotionEvent) queuedInputEvent.mEvent) ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetPointerIcon(MotionEvent motionEvent) {
        this.mPointerIconType = 1;
        updatePointerIcon(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updatePointerIcon(MotionEvent motionEvent) {
        float x = motionEvent.getX(0);
        float y = motionEvent.getY(0);
        View view = this.mView;
        if (view == null) {
            Slog.d(this.mTag, "updatePointerIcon called after view was removed");
            return false;
        }
        if (x < 0.0f || x >= view.getWidth() || y < 0.0f || y >= this.mView.getHeight()) {
            Slog.d(this.mTag, "updatePointerIcon called with position out of bounds");
            return false;
        }
        PointerIcon pointerIconOnResolvePointerIcon = this.mView.onResolvePointerIcon(motionEvent, 0);
        int type = pointerIconOnResolvePointerIcon != null ? pointerIconOnResolvePointerIcon.getType() : 1000;
        if (this.mPointerIconType != type) {
            this.mPointerIconType = type;
            this.mCustomPointerIcon = null;
            if (type != -1) {
                InputManager.getInstance().setPointerIconType(type);
                return true;
            }
        }
        if (this.mPointerIconType == -1 && !pointerIconOnResolvePointerIcon.equals(this.mCustomPointerIcon)) {
            this.mCustomPointerIcon = pointerIconOnResolvePointerIcon;
            InputManager.getInstance().setCustomPointerIcon(this.mCustomPointerIcon);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeUpdateTooltip(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() != 1) {
            return;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 9 || actionMasked == 7 || actionMasked == 10) {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mContext);
            if (accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()) {
                return;
            }
            View view = this.mView;
            if (view == null) {
                Slog.d(this.mTag, "maybeUpdateTooltip called after view was removed");
            } else {
                view.dispatchTooltipHoverEvent(motionEvent);
            }
        }
    }

    final class SyntheticInputStage extends InputStage {
        private final SyntheticJoystickHandler mJoystick;
        private final SyntheticKeyboardHandler mKeyboard;
        private final SyntheticTouchNavigationHandler mTouchNavigation;
        private final SyntheticTrackballHandler mTrackball;

        public SyntheticInputStage() {
            super(null);
            this.mTrackball = ViewRootImpl.this.new SyntheticTrackballHandler();
            this.mJoystick = ViewRootImpl.this.new SyntheticJoystickHandler();
            this.mTouchNavigation = ViewRootImpl.this.new SyntheticTouchNavigationHandler();
            this.mKeyboard = ViewRootImpl.this.new SyntheticKeyboardHandler();
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent queuedInputEvent) {
            queuedInputEvent.mFlags |= 16;
            if (queuedInputEvent.mEvent instanceof MotionEvent) {
                MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
                int source = motionEvent.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.process(motionEvent);
                    return 1;
                }
                if ((source & 16) != 0) {
                    this.mJoystick.process(motionEvent);
                    return 1;
                }
                if ((source & 2097152) != 2097152) {
                    return 0;
                }
                this.mTouchNavigation.process(motionEvent);
                return 1;
            }
            if ((queuedInputEvent.mFlags & 32) == 0) {
                return 0;
            }
            this.mKeyboard.process((KeyEvent) queuedInputEvent.mEvent);
            return 1;
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent queuedInputEvent) {
            if ((queuedInputEvent.mFlags & 16) == 0 && (queuedInputEvent.mEvent instanceof MotionEvent)) {
                MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
                int source = motionEvent.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.cancel();
                } else if ((source & 16) != 0) {
                    this.mJoystick.cancel();
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.cancel(motionEvent);
                }
            }
            super.onDeliverToNext(queuedInputEvent);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onWindowFocusChanged(boolean z) {
            if (z) {
                return;
            }
            this.mJoystick.cancel();
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDetachedFromWindow() {
            this.mJoystick.cancel();
        }
    }

    final class SyntheticTrackballHandler {
        private long mLastTime;
        private final TrackballAxis mX = new TrackballAxis();
        private final TrackballAxis mY = new TrackballAxis();

        SyntheticTrackballHandler() {
        }

        /* JADX WARN: Failed to calculate best type for var: r13v10 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v10 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v13 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v13 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v14 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v14 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v15 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v15 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v16 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v16 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v3 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 6 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v3 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v4 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v5 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v6 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v6 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v7 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v7 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v8 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v8 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r13v9 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v9 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r15v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v0 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r1v13 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v13 ??, new type: android.view.ViewRootImpl$TrackballAxis
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r1v19 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v19 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r1v26 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v26 ??, new type: android.view.ViewRootImpl$TrackballAxis
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r22v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r22v0 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r7v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v1 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /* JADX WARN: Failed to calculate best type for var: r8v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r8v1 ??, new type: int
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
        	... 7 more
         */
        /*  JADX ERROR: Types fix failed
            jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v3 ??, new type: char
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
            Caused by: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "arg" is null
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.verifyType(TypeUpdate.java:210)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.queueTypeUpdate(TypeUpdate.java:171)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.sameFirstArgListener(TypeUpdate.java:454)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:310)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.runUpdate(TypeUpdate.java:124)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:91)
            	... 5 more
            */
        public void process(android.view.MotionEvent r36) {
            /*
                Method dump skipped, instruction units count: 376
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SyntheticTrackballHandler.process(android.view.MotionEvent):void");
        }

        public void cancel() {
            this.mLastTime = -2147483648L;
            if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                return;
            }
            ViewRootImpl.this.ensureTouchMode(false);
        }
    }

    static final class TrackballAxis {
        static final float ACCEL_MOVE_SCALING_FACTOR = 0.025f;
        static final long FAST_MOVE_TIME = 150;
        static final float FIRST_MOVEMENT_THRESHOLD = 0.5f;
        static final float MAX_ACCELERATION = 20.0f;
        static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0f;
        static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0f;
        int dir;
        int nonAccelMovement;
        float position;
        int step;
        float acceleration = 1.0f;
        long lastMoveTime = 0;

        TrackballAxis() {
        }

        void reset(int i) {
            this.position = 0.0f;
            this.acceleration = 1.0f;
            this.lastMoveTime = 0L;
            this.step = i;
            this.dir = 0;
        }

        float collect(float f, long j, String str) {
            long j2;
            if (f > 0.0f) {
                j2 = (long) (150.0f * f);
                if (this.dir < 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = 1;
            } else if (f < 0.0f) {
                j2 = (long) ((-f) * 150.0f);
                if (this.dir > 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = -1;
            } else {
                j2 = 0;
            }
            if (j2 > 0) {
                long j3 = j - this.lastMoveTime;
                this.lastMoveTime = j;
                float f2 = this.acceleration;
                if (j3 < j2) {
                    float f3 = (j2 - j3) * ACCEL_MOVE_SCALING_FACTOR;
                    if (f3 > 1.0f) {
                        f2 *= f3;
                    }
                    if (f2 >= MAX_ACCELERATION) {
                        f2 = 20.0f;
                    }
                    this.acceleration = f2;
                } else {
                    float f4 = (j3 - j2) * ACCEL_MOVE_SCALING_FACTOR;
                    if (f4 > 1.0f) {
                        f2 /= f4;
                    }
                    this.acceleration = f2 > 1.0f ? f2 : 1.0f;
                }
            }
            float f5 = this.position + f;
            this.position = f5;
            return Math.abs(f5);
        }

        int generate() {
            int i = 0;
            this.nonAccelMovement = 0;
            while (true) {
                int i2 = this.position >= 0.0f ? 1 : -1;
                int i3 = this.step;
                if (i3 != 0) {
                    if (i3 != 1) {
                        if (Math.abs(this.position) < 1.0f) {
                            return i;
                        }
                        i += i2;
                        this.position -= i2 * 1.0f;
                        float f = this.acceleration;
                        float f2 = 1.1f * f;
                        if (f2 < MAX_ACCELERATION) {
                            f = f2;
                        }
                        this.acceleration = f;
                    } else {
                        if (Math.abs(this.position) < SECOND_CUMULATIVE_MOVEMENT_THRESHOLD) {
                            return i;
                        }
                        i += i2;
                        this.nonAccelMovement += i2;
                        this.position -= i2 * SECOND_CUMULATIVE_MOVEMENT_THRESHOLD;
                        this.step = 2;
                    }
                } else {
                    if (Math.abs(this.position) < FIRST_MOVEMENT_THRESHOLD) {
                        return i;
                    }
                    i += i2;
                    this.nonAccelMovement += i2;
                    this.step = 1;
                }
            }
        }
    }

    final class SyntheticJoystickHandler extends Handler {
        private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
        private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
        private final SparseArray<KeyEvent> mDeviceKeyEvents;
        private final JoystickAxesState mJoystickAxesState;

        public SyntheticJoystickHandler() {
            super(true);
            this.mJoystickAxesState = new JoystickAxesState();
            this.mDeviceKeyEvents = new SparseArray<>();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if ((i == 1 || i == 2) && ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                KeyEvent keyEvent = (KeyEvent) message.obj;
                KeyEvent keyEventChangeTimeRepeat = KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), keyEvent.getRepeatCount() + 1);
                ViewRootImpl.this.enqueueInputEvent(keyEventChangeTimeRepeat);
                Message messageObtainMessage = obtainMessage(message.what, keyEventChangeTimeRepeat);
                messageObtainMessage.setAsynchronous(true);
                sendMessageDelayed(messageObtainMessage, ViewConfiguration.getKeyRepeatDelay());
            }
        }

        public void process(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 2) {
                update(motionEvent);
                return;
            }
            if (actionMasked != 3) {
                Log.w(ViewRootImpl.this.mTag, "Unexpected action: " + motionEvent.getActionMasked());
                return;
            }
            cancel();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancel() {
            removeMessages(1);
            removeMessages(2);
            for (int i = 0; i < this.mDeviceKeyEvents.size(); i++) {
                KeyEvent keyEventValueAt = this.mDeviceKeyEvents.valueAt(i);
                if (keyEventValueAt != null) {
                    ViewRootImpl.this.enqueueInputEvent(KeyEvent.changeTimeRepeat(keyEventValueAt, SystemClock.uptimeMillis(), 0));
                }
            }
            this.mDeviceKeyEvents.clear();
            this.mJoystickAxesState.resetState();
        }

        private void update(MotionEvent motionEvent) {
            int historySize = motionEvent.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                long historicalEventTime = motionEvent.getHistoricalEventTime(i);
                this.mJoystickAxesState.updateStateForAxis(motionEvent, historicalEventTime, 0, motionEvent.getHistoricalAxisValue(0, 0, i));
                this.mJoystickAxesState.updateStateForAxis(motionEvent, historicalEventTime, 1, motionEvent.getHistoricalAxisValue(1, 0, i));
                this.mJoystickAxesState.updateStateForAxis(motionEvent, historicalEventTime, 15, motionEvent.getHistoricalAxisValue(15, 0, i));
                this.mJoystickAxesState.updateStateForAxis(motionEvent, historicalEventTime, 16, motionEvent.getHistoricalAxisValue(16, 0, i));
            }
            long eventTime = motionEvent.getEventTime();
            this.mJoystickAxesState.updateStateForAxis(motionEvent, eventTime, 0, motionEvent.getAxisValue(0));
            this.mJoystickAxesState.updateStateForAxis(motionEvent, eventTime, 1, motionEvent.getAxisValue(1));
            this.mJoystickAxesState.updateStateForAxis(motionEvent, eventTime, 15, motionEvent.getAxisValue(15));
            this.mJoystickAxesState.updateStateForAxis(motionEvent, eventTime, 16, motionEvent.getAxisValue(16));
        }

        final class JoystickAxesState {
            private static final int STATE_DOWN_OR_RIGHT = 1;
            private static final int STATE_NEUTRAL = 0;
            private static final int STATE_UP_OR_LEFT = -1;
            final int[] mAxisStatesHat = {0, 0};
            final int[] mAxisStatesStick = {0, 0};

            private boolean isXAxis(int i) {
                return i == 0 || i == 15;
            }

            private boolean isYAxis(int i) {
                return i == 1 || i == 16;
            }

            private int joystickAxisValueToState(float f) {
                if (f >= 0.5f) {
                    return 1;
                }
                return f <= -0.5f ? -1 : 0;
            }

            JoystickAxesState() {
            }

            void resetState() {
                int[] iArr = this.mAxisStatesHat;
                iArr[0] = 0;
                iArr[1] = 0;
                int[] iArr2 = this.mAxisStatesStick;
                iArr2[0] = 0;
                iArr2[1] = 0;
            }

            void updateStateForAxis(MotionEvent motionEvent, long j, int i, float f) {
                int i2;
                char c;
                int i3;
                int iJoystickAxisAndStateToKeycode;
                int i4 = 1;
                if (isXAxis(i)) {
                    c = 0;
                    i2 = 1;
                } else {
                    if (!isYAxis(i)) {
                        Log.e(ViewRootImpl.this.mTag, "Unexpected axis " + i + " in updateStateForAxis!");
                        return;
                    }
                    i2 = 2;
                    c = 1;
                }
                int iJoystickAxisValueToState = joystickAxisValueToState(f);
                if (i == 0 || i == 1) {
                    i3 = this.mAxisStatesStick[c];
                } else {
                    i3 = this.mAxisStatesHat[c];
                }
                if (i3 == iJoystickAxisValueToState) {
                    return;
                }
                int metaState = motionEvent.getMetaState();
                int deviceId = motionEvent.getDeviceId();
                int source = motionEvent.getSource();
                if (i3 == 1 || i3 == -1) {
                    int iJoystickAxisAndStateToKeycode2 = joystickAxisAndStateToKeycode(i, i3);
                    if (iJoystickAxisAndStateToKeycode2 != 0) {
                        ViewRootImpl.this.enqueueInputEvent(new KeyEvent(j, j, 1, iJoystickAxisAndStateToKeycode2, 0, metaState, deviceId, 0, 1024, source));
                        deviceId = deviceId;
                        SyntheticJoystickHandler.this.mDeviceKeyEvents.put(deviceId, null);
                    }
                    SyntheticJoystickHandler.this.removeMessages(i2);
                    i4 = 1;
                }
                if ((iJoystickAxisValueToState == i4 || iJoystickAxisValueToState == -1) && (iJoystickAxisAndStateToKeycode = joystickAxisAndStateToKeycode(i, iJoystickAxisValueToState)) != 0) {
                    int i5 = deviceId;
                    KeyEvent keyEvent = new KeyEvent(j, j, 0, iJoystickAxisAndStateToKeycode, 0, metaState, i5, 0, 1024, source);
                    ViewRootImpl.this.enqueueInputEvent(keyEvent);
                    Message messageObtainMessage = SyntheticJoystickHandler.this.obtainMessage(i2, keyEvent);
                    messageObtainMessage.setAsynchronous(true);
                    SyntheticJoystickHandler.this.sendMessageDelayed(messageObtainMessage, ViewConfiguration.getKeyRepeatTimeout());
                    SyntheticJoystickHandler.this.mDeviceKeyEvents.put(i5, new KeyEvent(j, j, 1, iJoystickAxisAndStateToKeycode, 0, metaState, i5, 0, BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO, source));
                }
                if (i == 0 || i == 1) {
                    this.mAxisStatesStick[c] = iJoystickAxisValueToState;
                } else {
                    this.mAxisStatesHat[c] = iJoystickAxisValueToState;
                }
            }

            private int joystickAxisAndStateToKeycode(int i, int i2) {
                if (isXAxis(i) && i2 == -1) {
                    return 21;
                }
                if (isXAxis(i) && i2 == 1) {
                    return 22;
                }
                if (isYAxis(i) && i2 == -1) {
                    return 19;
                }
                if (isYAxis(i) && i2 == 1) {
                    return 20;
                }
                Log.e(ViewRootImpl.this.mTag, "Unknown axis " + i + " or direction " + i2);
                return 0;
            }
        }
    }

    final class SyntheticTouchNavigationHandler extends Handler {
        private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0f;
        private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0f;
        private static final float FLING_TICK_DECAY = 0.8f;
        private static final boolean LOCAL_DEBUG = false;
        private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
        private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0f;
        private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0f;
        private static final int TICK_DISTANCE_MILLIMETERS = 12;
        private float mAccumulatedX;
        private float mAccumulatedY;
        private int mActivePointerId;
        private float mConfigMaxFlingVelocity;
        private float mConfigMinFlingVelocity;
        private float mConfigTickDistance;
        private boolean mConsumedMovement;
        private int mCurrentDeviceId;
        private boolean mCurrentDeviceSupported;
        private int mCurrentSource;
        private final Runnable mFlingRunnable;
        private float mFlingVelocity;
        private boolean mFlinging;
        private float mLastX;
        private float mLastY;
        private int mPendingKeyCode;
        private long mPendingKeyDownTime;
        private int mPendingKeyMetaState;
        private int mPendingKeyRepeatCount;
        private float mStartX;
        private float mStartY;
        private VelocityTracker mVelocityTracker;

        static /* synthetic */ float access$2932(SyntheticTouchNavigationHandler syntheticTouchNavigationHandler, float f) {
            float f2 = syntheticTouchNavigationHandler.mFlingVelocity * f;
            syntheticTouchNavigationHandler.mFlingVelocity = f2;
            return f2;
        }

        public SyntheticTouchNavigationHandler() {
            super(true);
            this.mCurrentDeviceId = -1;
            this.mActivePointerId = -1;
            this.mPendingKeyCode = 0;
            this.mFlingRunnable = new Runnable() { // from class: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    SyntheticTouchNavigationHandler syntheticTouchNavigationHandler = SyntheticTouchNavigationHandler.this;
                    syntheticTouchNavigationHandler.sendKeyDownOrRepeat(jUptimeMillis, syntheticTouchNavigationHandler.mPendingKeyCode, SyntheticTouchNavigationHandler.this.mPendingKeyMetaState);
                    SyntheticTouchNavigationHandler.access$2932(SyntheticTouchNavigationHandler.this, SyntheticTouchNavigationHandler.FLING_TICK_DECAY);
                    if (SyntheticTouchNavigationHandler.this.postFling(jUptimeMillis)) {
                        return;
                    }
                    SyntheticTouchNavigationHandler.this.mFlinging = false;
                    SyntheticTouchNavigationHandler.this.finishKeys(jUptimeMillis);
                }
            };
        }

        public void process(MotionEvent motionEvent) {
            long eventTime = motionEvent.getEventTime();
            int deviceId = motionEvent.getDeviceId();
            int source = motionEvent.getSource();
            if (this.mCurrentDeviceId != deviceId || this.mCurrentSource != source) {
                finishKeys(eventTime);
                finishTracking(eventTime);
                this.mCurrentDeviceId = deviceId;
                this.mCurrentSource = source;
                this.mCurrentDeviceSupported = false;
                InputDevice device = motionEvent.getDevice();
                if (device != null) {
                    InputDevice.MotionRange motionRange = device.getMotionRange(0);
                    InputDevice.MotionRange motionRange2 = device.getMotionRange(1);
                    if (motionRange != null && motionRange2 != null) {
                        this.mCurrentDeviceSupported = true;
                        float resolution = motionRange.getResolution();
                        if (resolution <= 0.0f) {
                            resolution = motionRange.getRange() / 48.0f;
                        }
                        float resolution2 = motionRange2.getResolution();
                        if (resolution2 <= 0.0f) {
                            resolution2 = motionRange2.getRange() / 48.0f;
                        }
                        float f = (resolution + resolution2) * 0.5f * 12.0f;
                        this.mConfigTickDistance = f;
                        this.mConfigMinFlingVelocity = MIN_FLING_VELOCITY_TICKS_PER_SECOND * f;
                        this.mConfigMaxFlingVelocity = f * MAX_FLING_VELOCITY_TICKS_PER_SECOND;
                    }
                }
            }
            if (this.mCurrentDeviceSupported) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    boolean z = this.mFlinging;
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    VelocityTracker velocityTrackerObtain = VelocityTracker.obtain();
                    this.mVelocityTracker = velocityTrackerObtain;
                    velocityTrackerObtain.addMovement(motionEvent);
                    this.mStartX = motionEvent.getX();
                    float y = motionEvent.getY();
                    this.mStartY = y;
                    this.mLastX = this.mStartX;
                    this.mLastY = y;
                    this.mAccumulatedX = 0.0f;
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = z;
                    return;
                }
                if (actionMasked != 1 && actionMasked != 2) {
                    if (actionMasked != 3) {
                        return;
                    }
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    return;
                }
                int i = this.mActivePointerId;
                if (i < 0) {
                    return;
                }
                int iFindPointerIndex = motionEvent.findPointerIndex(i);
                if (iFindPointerIndex < 0) {
                    finishKeys(eventTime);
                    finishTracking(eventTime);
                    return;
                }
                this.mVelocityTracker.addMovement(motionEvent);
                float x = motionEvent.getX(iFindPointerIndex);
                float y2 = motionEvent.getY(iFindPointerIndex);
                this.mAccumulatedX += x - this.mLastX;
                this.mAccumulatedY += y2 - this.mLastY;
                this.mLastX = x;
                this.mLastY = y2;
                consumeAccumulatedMovement(eventTime, motionEvent.getMetaState());
                if (actionMasked == 1) {
                    if (this.mConsumedMovement && this.mPendingKeyCode != 0) {
                        this.mVelocityTracker.computeCurrentVelocity(1000, this.mConfigMaxFlingVelocity);
                        if (!startFling(eventTime, this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mVelocityTracker.getYVelocity(this.mActivePointerId))) {
                            finishKeys(eventTime);
                        }
                    }
                    finishTracking(eventTime);
                }
            }
        }

        public void cancel(MotionEvent motionEvent) {
            if (this.mCurrentDeviceId == motionEvent.getDeviceId() && this.mCurrentSource == motionEvent.getSource()) {
                long eventTime = motionEvent.getEventTime();
                finishKeys(eventTime);
                finishTracking(eventTime);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishKeys(long j) {
            cancelFling();
            sendKeyUp(j);
        }

        private void finishTracking(long j) {
            if (this.mActivePointerId >= 0) {
                this.mActivePointerId = -1;
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }

        private void consumeAccumulatedMovement(long j, int i) {
            float fAbs = Math.abs(this.mAccumulatedX);
            float fAbs2 = Math.abs(this.mAccumulatedY);
            if (fAbs >= fAbs2) {
                if (fAbs >= this.mConfigTickDistance) {
                    this.mAccumulatedX = consumeAccumulatedMovement(j, i, this.mAccumulatedX, 21, 22);
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = true;
                    return;
                }
                return;
            }
            if (fAbs2 >= this.mConfigTickDistance) {
                this.mAccumulatedY = consumeAccumulatedMovement(j, i, this.mAccumulatedY, 19, 20);
                this.mAccumulatedX = 0.0f;
                this.mConsumedMovement = true;
            }
        }

        private float consumeAccumulatedMovement(long j, int i, float f, int i2, int i3) {
            while (f <= (-this.mConfigTickDistance)) {
                sendKeyDownOrRepeat(j, i2, i);
                f += this.mConfigTickDistance;
            }
            while (f >= this.mConfigTickDistance) {
                sendKeyDownOrRepeat(j, i3, i);
                f -= this.mConfigTickDistance;
            }
            return f;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendKeyDownOrRepeat(long j, int i, int i2) {
            if (this.mPendingKeyCode != i) {
                sendKeyUp(j);
                this.mPendingKeyDownTime = j;
                this.mPendingKeyCode = i;
                this.mPendingKeyRepeatCount = 0;
            } else {
                this.mPendingKeyRepeatCount++;
            }
            this.mPendingKeyMetaState = i2;
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, j, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, 1024, this.mCurrentSource));
        }

        private void sendKeyUp(long j) {
            if (this.mPendingKeyCode != 0) {
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, j, 1, this.mPendingKeyCode, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, 1024, this.mCurrentSource));
                this.mPendingKeyCode = 0;
            }
        }

        private boolean startFling(long j, float f, float f2) {
            switch (this.mPendingKeyCode) {
                case 19:
                    float f3 = -f2;
                    if (f3 < this.mConfigMinFlingVelocity || Math.abs(f) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f3;
                    break;
                case 20:
                    if (f2 < this.mConfigMinFlingVelocity || Math.abs(f) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f2;
                    break;
                case 21:
                    float f4 = -f;
                    if (f4 < this.mConfigMinFlingVelocity || Math.abs(f2) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f4;
                    break;
                case 22:
                    if (f < this.mConfigMinFlingVelocity || Math.abs(f2) >= this.mConfigMinFlingVelocity) {
                        return false;
                    }
                    this.mFlingVelocity = f;
                    break;
            }
            boolean zPostFling = postFling(j);
            this.mFlinging = zPostFling;
            return zPostFling;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean postFling(long j) {
            float f = this.mFlingVelocity;
            if (f < this.mConfigMinFlingVelocity) {
                return false;
            }
            postAtTime(this.mFlingRunnable, j + ((long) ((this.mConfigTickDistance / f) * 1000.0f)));
            return true;
        }

        private void cancelFling() {
            if (this.mFlinging) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlinging = false;
            }
        }
    }

    final class SyntheticKeyboardHandler {
        SyntheticKeyboardHandler() {
        }

        public void process(KeyEvent keyEvent) {
            KeyCharacterMap.FallbackAction fallbackAction;
            if ((keyEvent.getFlags() & 1024) == 0 && (fallbackAction = keyEvent.getKeyCharacterMap().getFallbackAction(keyEvent.getKeyCode(), keyEvent.getMetaState())) != null) {
                KeyEvent keyEventObtain = KeyEvent.obtain(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), fallbackAction.keyCode, keyEvent.getRepeatCount(), fallbackAction.metaState, keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags() | 1024, keyEvent.getSource(), null);
                fallbackAction.recycle();
                ViewRootImpl.this.enqueueInputEvent(keyEventObtain);
            }
        }
    }

    private static boolean isNavigationKey(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 61 || keyCode == 62 || keyCode == 66 || keyCode == 92 || keyCode == 93 || keyCode == 122 || keyCode == 123) {
            return true;
        }
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return false;
        }
    }

    private static boolean isTypingKey(KeyEvent keyEvent) {
        return keyEvent.getUnicodeChar() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkForLeavingTouchModeAndConsume(KeyEvent keyEvent) {
        if (!this.mAttachInfo.mInTouchMode) {
            return false;
        }
        int action = keyEvent.getAction();
        if ((action != 0 && action != 2) || (keyEvent.getFlags() & 4) != 0) {
            return false;
        }
        if (isNavigationKey(keyEvent)) {
            return ensureTouchMode(false);
        }
        if (isTypingKey(keyEvent)) {
            ensureTouchMode(false);
        }
        return false;
    }

    void setLocalDragState(Object obj) {
        this.mLocalDragState = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:44:0x008b A[Catch: RemoteException -> 0x0093, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x0093, blocks: (B:41:0x0080, B:42:0x0087, B:44:0x008b), top: B:57:0x0080 }] */
    public void handleDragEvent(DragEvent dragEvent) {
        if (this.mView != null && this.mAdded) {
            int i = dragEvent.mAction;
            if (i == 1) {
                this.mCurrentDragView = null;
                this.mDragDescription = dragEvent.mClipDescription;
            } else {
                if (i == 4) {
                    this.mDragDescription = null;
                }
                dragEvent.mClipDescription = this.mDragDescription;
            }
            if (i == 6) {
                if (View.sCascadedDragDrop) {
                    this.mView.dispatchDragEnterExitInPreN(dragEvent);
                }
                setDragFocus(null, dragEvent);
            } else {
                if (i == 2 || i == 3) {
                    this.mDragPoint.set(dragEvent.mX, dragEvent.mY);
                    CompatibilityInfo.Translator translator = this.mTranslator;
                    if (translator != null) {
                        translator.translatePointInScreenToAppWindow(this.mDragPoint);
                    }
                    int i2 = this.mCurScrollY;
                    if (i2 != 0) {
                        this.mDragPoint.offset(0.0f, i2);
                    }
                    dragEvent.mX = this.mDragPoint.x;
                    dragEvent.mY = this.mDragPoint.y;
                }
                View view = this.mCurrentDragView;
                if (i == 3 && dragEvent.mClipData != null) {
                    dragEvent.mClipData.prepareToEnterProcess();
                }
                boolean zDispatchDragEvent = this.mView.dispatchDragEvent(dragEvent);
                if (i == 2 && !dragEvent.mEventHandlerWasCalled) {
                    setDragFocus(null, dragEvent);
                }
                if (view != this.mCurrentDragView) {
                    if (view != null) {
                        try {
                            this.mWindowSession.dragRecipientExited(this.mWindow);
                            if (this.mCurrentDragView != null) {
                                this.mWindowSession.dragRecipientEntered(this.mWindow);
                            }
                        } catch (RemoteException unused) {
                            Slog.e(this.mTag, "Unable to note drag target change");
                        }
                    } else if (this.mCurrentDragView != null) {
                        this.mWindowSession.dragRecipientEntered(this.mWindow);
                    }
                }
                if (i == 3) {
                    try {
                        Log.i(this.mTag, "Reporting drop result: " + zDispatchDragEvent);
                        this.mWindowSession.reportDropResult(this.mWindow, zDispatchDragEvent);
                    } catch (RemoteException unused2) {
                        Log.e(this.mTag, "Unable to report drop result");
                    }
                }
                if (i == 4) {
                    this.mCurrentDragView = null;
                    setLocalDragState(null);
                    this.mAttachInfo.mDragToken = null;
                    if (this.mAttachInfo.mDragSurface != null) {
                        this.mAttachInfo.mDragSurface.release();
                        this.mAttachInfo.mDragSurface = null;
                    }
                }
            }
        }
        dragEvent.recycle();
    }

    public void handleDispatchSystemUiVisibilityChanged(SystemUiVisibilityInfo systemUiVisibilityInfo) {
        if (this.mSeq != systemUiVisibilityInfo.seq) {
            this.mSeq = systemUiVisibilityInfo.seq;
            this.mAttachInfo.mForceReportNewAttributes = true;
            scheduleTraversals();
        }
        if (this.mView == null) {
            return;
        }
        if (systemUiVisibilityInfo.localChanges != 0) {
            this.mView.updateLocalSystemUiVisibility(systemUiVisibilityInfo.localValue, systemUiVisibilityInfo.localChanges);
        }
        int i = systemUiVisibilityInfo.globalVisibility & 7;
        if (i != this.mAttachInfo.mGlobalSystemUiVisibility) {
            this.mAttachInfo.mGlobalSystemUiVisibility = i;
            this.mView.dispatchSystemUiVisibilityChanged(i);
        }
    }

    public void onWindowTitleChanged() {
        this.mAttachInfo.mForceReportNewAttributes = true;
    }

    public void handleDispatchWindowShown() {
        this.mAttachInfo.mTreeObserver.dispatchOnWindowShown();
    }

    public void handleRequestKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
        Bundle bundle = new Bundle();
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
        View view = this.mView;
        if (view != null) {
            view.requestKeyboardShortcuts(arrayList, i);
        }
        bundle.putParcelableArrayList(WindowManager.PARCEL_KEY_SHORTCUTS_ARRAY, arrayList);
        try {
            iResultReceiver.send(0, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void getLastTouchPoint(Point point) {
        point.x = (int) this.mLastTouchPoint.x;
        point.y = (int) this.mLastTouchPoint.y;
    }

    public int getLastTouchSource() {
        return this.mLastTouchSource;
    }

    public void setDragFocus(View view, DragEvent dragEvent) {
        if (this.mCurrentDragView != view && !View.sCascadedDragDrop) {
            float f = dragEvent.mX;
            float f2 = dragEvent.mY;
            int i = dragEvent.mAction;
            ClipData clipData = dragEvent.mClipData;
            dragEvent.mX = 0.0f;
            dragEvent.mY = 0.0f;
            dragEvent.mClipData = null;
            if (this.mCurrentDragView != null) {
                dragEvent.mAction = 6;
                this.mCurrentDragView.callDragEventHandler(dragEvent);
            }
            if (view != null) {
                dragEvent.mAction = 5;
                view.callDragEventHandler(dragEvent);
            }
            dragEvent.mAction = i;
            dragEvent.mX = f;
            dragEvent.mY = f2;
            dragEvent.mClipData = clipData;
        }
        this.mCurrentDragView = view;
    }

    private AudioManager getAudioManager() {
        View view = this.mView;
        if (view == null) {
            throw new IllegalStateException("getAudioManager called when there is no mView");
        }
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) view.getContext().getSystemService("audio");
        }
        return this.mAudioManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AutofillManager getAutofillManager() {
        View view = this.mView;
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup.getChildCount() > 0) {
            return (AutofillManager) viewGroup.getChildAt(0).getContext().getSystemService(AutofillManager.class);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAutofillUiShowing() {
        AutofillManager autofillManager = getAutofillManager();
        if (autofillManager == null) {
            return false;
        }
        return autofillManager.isAutofillUiShowing();
    }

    public AccessibilityInteractionController getAccessibilityInteractionController() {
        if (this.mView == null) {
            throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
        }
        if (this.mAccessibilityInteractionController == null) {
            this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
        }
        return this.mAccessibilityInteractionController;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int relayoutWindow(WindowManager.LayoutParams layoutParams, int i, boolean z) throws RemoteException {
        Object[] objArr;
        float f = this.mAttachInfo.mApplicationScale;
        if (layoutParams == null || this.mTranslator == null) {
            objArr = false;
        } else {
            layoutParams.backup();
            this.mTranslator.translateWindowLayout(layoutParams);
            objArr = true;
        }
        if (layoutParams != null && this.mOrigWindowType != layoutParams.type && this.mTargetSdkVersion < 14) {
            Slog.w(this.mTag, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
            layoutParams.type = this.mOrigWindowType;
        }
        int iRelayout = this.mWindowSession.relayout(this.mWindow, this.mSeq, layoutParams, (int) ((this.mView.getMeasuredWidth() * f) + 0.5f), (int) ((this.mView.getMeasuredHeight() * f) + 0.5f), i, z ? 1 : 0, this.mSurface.isValid() ? this.mSurface.getNextFrameNumber() : -1L, this.mWinFrame, this.mPendingOverscanInsets, this.mPendingContentInsets, this.mPendingVisibleInsets, this.mPendingStableInsets, this.mPendingOutsets, this.mPendingBackDropFrame, this.mPendingDisplayCutout, this.mPendingMergedConfiguration, this.mSurface);
        this.mPendingAlwaysConsumeNavBar = (iRelayout & 64) != 0;
        if (objArr != false) {
            layoutParams.restore();
        }
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateRectInScreenToAppWinFrame(this.mWinFrame);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingOverscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingContentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingVisibleInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingStableInsets);
        }
        return iRelayout;
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public void playSoundEffect(int i) {
        checkThread();
        try {
            AudioManager audioManager = getAudioManager();
            if (i == 0) {
                audioManager.playSoundEffect(0);
                return;
            }
            if (i == 1) {
                audioManager.playSoundEffect(3);
                return;
            }
            if (i == 2) {
                audioManager.playSoundEffect(1);
                return;
            }
            if (i == 3) {
                audioManager.playSoundEffect(4);
                return;
            }
            if (i == 4) {
                audioManager.playSoundEffect(2);
                return;
            }
            throw new IllegalArgumentException("unknown effect id " + i + " not defined in " + SoundEffectConstants.class.getCanonicalName());
        } catch (IllegalStateException e) {
            Log.e(this.mTag, "FATAL EXCEPTION when attempting to play sound effect: " + e);
            e.printStackTrace();
        }
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public boolean performHapticFeedback(int i, boolean z) {
        try {
            return this.mWindowSession.performHapticFeedback(this.mWindow, i, z);
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.ViewParent
    public View focusSearch(View view, int i) {
        checkThread();
        if (this.mView instanceof ViewGroup) {
            return FocusFinder.getInstance().findNextFocus((ViewGroup) this.mView, view, i);
        }
        return null;
    }

    @Override // android.view.ViewParent
    public View keyboardNavigationClusterSearch(View view, int i) {
        checkThread();
        return FocusFinder.getInstance().findNextKeyboardNavigationCluster(this.mView, view, i);
    }

    public void debug() {
        this.mView.debug();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.println("ViewRoot:");
        printWriter.print(str2);
        printWriter.print("mAdded=");
        printWriter.print(this.mAdded);
        printWriter.print(" mRemoved=");
        printWriter.println(this.mRemoved);
        printWriter.print(str2);
        printWriter.print("mConsumeBatchedInputScheduled=");
        printWriter.println(this.mConsumeBatchedInputScheduled);
        printWriter.print(str2);
        printWriter.print("mConsumeBatchedInputImmediatelyScheduled=");
        printWriter.println(this.mConsumeBatchedInputImmediatelyScheduled);
        printWriter.print(str2);
        printWriter.print("mPendingInputEventCount=");
        printWriter.println(this.mPendingInputEventCount);
        printWriter.print(str2);
        printWriter.print("mProcessInputEventsScheduled=");
        printWriter.println(this.mProcessInputEventsScheduled);
        printWriter.print(str2);
        printWriter.print("mTraversalScheduled=");
        printWriter.print(this.mTraversalScheduled);
        printWriter.print(str2);
        printWriter.print("mIsAmbientMode=");
        printWriter.print(this.mIsAmbientMode);
        if (this.mTraversalScheduled) {
            printWriter.print(" (barrier=");
            printWriter.print(this.mTraversalBarrier);
            printWriter.println(")");
        } else {
            printWriter.println();
        }
        this.mFirstInputStage.dump(str2, printWriter);
        this.mChoreographer.dump(str, printWriter);
        printWriter.print(str);
        printWriter.println("View Hierarchy:");
        dumpViewHierarchy(str2, printWriter, this.mView);
    }

    private void dumpViewHierarchy(String str, PrintWriter printWriter, View view) {
        ViewGroup viewGroup;
        int childCount;
        printWriter.print(str);
        if (view == null) {
            printWriter.println("null");
            return;
        }
        printWriter.println(view.toString());
        if ((view instanceof ViewGroup) && (childCount = (viewGroup = (ViewGroup) view).getChildCount()) > 0) {
            String str2 = str + "  ";
            for (int i = 0; i < childCount; i++) {
                dumpViewHierarchy(str2, printWriter, viewGroup.getChildAt(i));
            }
        }
    }

    public void dumpGfxInfo(int[] iArr) {
        iArr[1] = 0;
        iArr[0] = 0;
        View view = this.mView;
        if (view != null) {
            getGfxInfo(view, iArr);
        }
    }

    private static void getGfxInfo(View view, int[] iArr) {
        RenderNode renderNode = view.mRenderNode;
        iArr[0] = iArr[0] + 1;
        if (renderNode != null) {
            iArr[1] = iArr[1] + renderNode.getDebugSize();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                getGfxInfo(viewGroup.getChildAt(i), iArr);
            }
        }
    }

    boolean die(boolean z) {
        if (z && !this.mIsInTraversal) {
            doDie();
            return false;
        }
        if (!this.mIsDrawing) {
            destroyHardwareRenderer();
        } else {
            Log.e(this.mTag, "Attempting to destroy the window while drawing!\n  window=" + this + ", title=" + ((Object) this.mWindowAttributes.getTitle()));
        }
        this.mHandler.sendEmptyMessage(3);
        return true;
    }

    void doDie() {
        checkThread();
        synchronized (this) {
            if (this.mRemoved) {
                return;
            }
            boolean z = true;
            this.mRemoved = true;
            if (this.mAdded) {
                dispatchDetachedFromWindow();
            }
            if (this.mAdded && !this.mFirst) {
                destroyHardwareRenderer();
                if (this.mView != null) {
                    int visibility = this.mView.getVisibility();
                    if (this.mViewVisibility == visibility) {
                        z = false;
                    }
                    if (this.mWindowAttributesChanged || z) {
                        try {
                            if ((relayoutWindow(this.mWindowAttributes, visibility, false) & 2) != 0) {
                                this.mWindowSession.finishDrawing(this.mWindow);
                            }
                        } catch (RemoteException unused) {
                        }
                    }
                    this.mSurface.release();
                }
            }
            this.mAdded = false;
            WindowManagerGlobal.getInstance().doRemoveView(this);
        }
    }

    public void requestUpdateConfiguration(Configuration configuration) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(18, configuration));
    }

    public void loadSystemProperties() {
        this.mHandler.post(new Runnable() { // from class: android.view.ViewRootImpl.4
            @Override // java.lang.Runnable
            public void run() {
                ViewRootImpl.this.mProfileRendering = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_PROFILE_RENDERING, false);
                ViewRootImpl viewRootImpl = ViewRootImpl.this;
                viewRootImpl.profileRendering(viewRootImpl.mAttachInfo.mHasWindowFocus);
                if (ViewRootImpl.this.mAttachInfo.mThreadedRenderer != null && ViewRootImpl.this.mAttachInfo.mThreadedRenderer.loadSystemProperties()) {
                    ViewRootImpl.this.invalidate();
                }
                boolean z = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
                if (z != ViewRootImpl.this.mAttachInfo.mDebugLayout) {
                    ViewRootImpl.this.mAttachInfo.mDebugLayout = z;
                    if (ViewRootImpl.this.mHandler.hasMessages(22)) {
                        return;
                    }
                    ViewRootImpl.this.mHandler.sendEmptyMessageDelayed(22, 200L);
                }
            }
        });
    }

    private void destroyHardwareRenderer() {
        ThreadedRenderer threadedRenderer = this.mAttachInfo.mThreadedRenderer;
        if (threadedRenderer != null) {
            View view = this.mView;
            if (view != null) {
                threadedRenderer.destroyHardwareResources(view);
            }
            threadedRenderer.destroy();
            threadedRenderer.setRequested(false);
            this.mAttachInfo.mThreadedRenderer = null;
            this.mAttachInfo.mHardwareAccelerated = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchResized(Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, Rect rect6, boolean z, MergedConfiguration mergedConfiguration, Rect rect7, boolean z2, boolean z3, int i, DisplayCutout.ParcelableWrapper parcelableWrapper) {
        Rect rect8 = rect2;
        Rect rect9 = rect3;
        Rect rect10 = rect4;
        Rect rect11 = rect5;
        MergedConfiguration mergedConfiguration2 = mergedConfiguration;
        Rect rect12 = rect7;
        if (this.mDragResizing && this.mUseMTRenderer) {
            boolean zEquals = rect.equals(rect12);
            synchronized (this.mWindowCallbacks) {
                for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                    this.mWindowCallbacks.get(size).onWindowSizeIsChanging(rect12, zEquals, rect10, rect11);
                }
            }
        }
        Message messageObtainMessage = this.mHandler.obtainMessage(z ? 5 : 4);
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateRectInScreenToAppWindow(rect);
            this.mTranslator.translateRectInScreenToAppWindow(rect2);
            this.mTranslator.translateRectInScreenToAppWindow(rect9);
            this.mTranslator.translateRectInScreenToAppWindow(rect10);
        }
        SomeArgs someArgsObtain = SomeArgs.obtain();
        boolean z4 = Binder.getCallingPid() == Process.myPid();
        someArgsObtain.arg1 = z4 ? new Rect(rect) : rect;
        if (z4) {
            rect9 = new Rect(rect9);
        }
        someArgsObtain.arg2 = rect9;
        if (z4) {
            rect10 = new Rect(rect10);
        }
        someArgsObtain.arg3 = rect10;
        if (z4 && mergedConfiguration2 != null) {
            mergedConfiguration2 = new MergedConfiguration(mergedConfiguration2);
        }
        someArgsObtain.arg4 = mergedConfiguration2;
        if (z4) {
            rect8 = new Rect(rect2);
        }
        someArgsObtain.arg5 = rect8;
        if (z4) {
            rect11 = new Rect(rect11);
        }
        someArgsObtain.arg6 = rect11;
        someArgsObtain.arg7 = z4 ? new Rect(rect6) : rect6;
        if (z4) {
            rect12 = new Rect(rect12);
        }
        someArgsObtain.arg8 = rect12;
        someArgsObtain.arg9 = parcelableWrapper.get();
        someArgsObtain.argi1 = z2 ? 1 : 0;
        someArgsObtain.argi2 = z3 ? 1 : 0;
        someArgsObtain.argi3 = i;
        messageObtainMessage.obj = someArgsObtain;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchMoved(int i, int i2) {
        if (this.mTranslator != null) {
            PointF pointF = new PointF(i, i2);
            this.mTranslator.translatePointInScreenToAppWindow(pointF);
            i = (int) (((double) pointF.x) + 0.5d);
            i2 = (int) (((double) pointF.y) + 0.5d);
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(23, i, i2));
    }

    private static final class QueuedInputEvent {
        public static final int FLAG_DEFERRED = 2;
        public static final int FLAG_DELIVER_POST_IME = 1;
        public static final int FLAG_FINISHED = 4;
        public static final int FLAG_FINISHED_HANDLED = 8;
        public static final int FLAG_RESYNTHESIZED = 16;
        public static final int FLAG_UNHANDLED = 32;
        public InputEvent mEvent;
        public int mFlags;
        public QueuedInputEvent mNext;
        public InputEventReceiver mReceiver;

        private QueuedInputEvent() {
        }

        public boolean shouldSkipIme() {
            if ((this.mFlags & 1) != 0) {
                return true;
            }
            InputEvent inputEvent = this.mEvent;
            return (inputEvent instanceof MotionEvent) && (inputEvent.isFromSource(2) || this.mEvent.isFromSource(4194304));
        }

        public boolean shouldSendToSynthesizer() {
            return (this.mFlags & 32) != 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("QueuedInputEvent{flags=");
            if (!flagToString("UNHANDLED", 32, flagToString("RESYNTHESIZED", 16, flagToString("FINISHED_HANDLED", 8, flagToString("FINISHED", 4, flagToString("DEFERRED", 2, flagToString("DELIVER_POST_IME", 1, false, sb), sb), sb), sb), sb), sb)) {
                sb.append("0");
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(", hasNextQueuedEvent=");
            sb2.append(this.mEvent != null ? "true" : "false");
            sb.append(sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append(", hasInputEventReceiver=");
            sb3.append(this.mReceiver == null ? "false" : "true");
            sb.append(sb3.toString());
            sb.append(", mEvent=" + this.mEvent + "}");
            return sb.toString();
        }

        private boolean flagToString(String str, int i, boolean z, StringBuilder sb) {
            if ((i & this.mFlags) == 0) {
                return z;
            }
            if (z) {
                sb.append("|");
            }
            sb.append(str);
            return true;
        }
    }

    private QueuedInputEvent obtainQueuedInputEvent(InputEvent inputEvent, InputEventReceiver inputEventReceiver, int i) {
        QueuedInputEvent queuedInputEvent = this.mQueuedInputEventPool;
        if (queuedInputEvent != null) {
            this.mQueuedInputEventPoolSize--;
            this.mQueuedInputEventPool = queuedInputEvent.mNext;
            queuedInputEvent.mNext = null;
        } else {
            queuedInputEvent = new QueuedInputEvent();
        }
        queuedInputEvent.mEvent = inputEvent;
        queuedInputEvent.mReceiver = inputEventReceiver;
        queuedInputEvent.mFlags = i;
        return queuedInputEvent;
    }

    private void recycleQueuedInputEvent(QueuedInputEvent queuedInputEvent) {
        queuedInputEvent.mEvent = null;
        queuedInputEvent.mReceiver = null;
        int i = this.mQueuedInputEventPoolSize;
        if (i < 10) {
            this.mQueuedInputEventPoolSize = i + 1;
            queuedInputEvent.mNext = this.mQueuedInputEventPool;
            this.mQueuedInputEventPool = queuedInputEvent;
        }
    }

    void enqueueInputEvent(InputEvent inputEvent) {
        enqueueInputEvent(inputEvent, null, 0, false);
    }

    void enqueueInputEvent(InputEvent inputEvent, InputEventReceiver inputEventReceiver, int i, boolean z) {
        adjustInputEventForCompatibility(inputEvent);
        QueuedInputEvent queuedInputEventObtainQueuedInputEvent = obtainQueuedInputEvent(inputEvent, inputEventReceiver, i);
        QueuedInputEvent queuedInputEvent = this.mPendingInputEventTail;
        if (queuedInputEvent == null) {
            this.mPendingInputEventHead = queuedInputEventObtainQueuedInputEvent;
            this.mPendingInputEventTail = queuedInputEventObtainQueuedInputEvent;
        } else {
            queuedInputEvent.mNext = queuedInputEventObtainQueuedInputEvent;
            this.mPendingInputEventTail = queuedInputEventObtainQueuedInputEvent;
        }
        int i2 = this.mPendingInputEventCount + 1;
        this.mPendingInputEventCount = i2;
        Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i2);
        if (z) {
            doProcessInputEvents();
        } else {
            scheduleProcessInputEvents();
        }
    }

    private void scheduleProcessInputEvents() {
        if (this.mProcessInputEventsScheduled) {
            return;
        }
        this.mProcessInputEventsScheduled = true;
        Message messageObtainMessage = this.mHandler.obtainMessage(19);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0038  */
    void doProcessInputEvents() {
        long historicalEventTimeNano;
        while (true) {
            QueuedInputEvent queuedInputEvent = this.mPendingInputEventHead;
            if (queuedInputEvent == null) {
                break;
            }
            QueuedInputEvent queuedInputEvent2 = queuedInputEvent.mNext;
            this.mPendingInputEventHead = queuedInputEvent2;
            if (queuedInputEvent2 == null) {
                this.mPendingInputEventTail = null;
            }
            queuedInputEvent.mNext = null;
            int i = this.mPendingInputEventCount - 1;
            this.mPendingInputEventCount = i;
            Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i);
            long eventTimeNano = queuedInputEvent.mEvent.getEventTimeNano();
            if (queuedInputEvent.mEvent instanceof MotionEvent) {
                MotionEvent motionEvent = (MotionEvent) queuedInputEvent.mEvent;
                if (motionEvent.getHistorySize() > 0) {
                    historicalEventTimeNano = motionEvent.getHistoricalEventTimeNano(0);
                } else {
                    historicalEventTimeNano = eventTimeNano;
                }
            } else {
                historicalEventTimeNano = eventTimeNano;
            }
            this.mChoreographer.mFrameInfo.updateInputEventTime(eventTimeNano, historicalEventTimeNano);
            deliverInputEvent(queuedInputEvent);
        }
        if (this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = false;
            this.mHandler.removeMessages(19);
        }
    }

    private void deliverInputEvent(QueuedInputEvent queuedInputEvent) {
        InputStage inputStage;
        Trace.asyncTraceBegin(8L, "deliverInputEvent", queuedInputEvent.mEvent.getSequenceNumber());
        InputEventConsistencyVerifier inputEventConsistencyVerifier = this.mInputEventConsistencyVerifier;
        if (inputEventConsistencyVerifier != null) {
            inputEventConsistencyVerifier.onInputEvent(queuedInputEvent.mEvent, 0);
        }
        if (queuedInputEvent.shouldSendToSynthesizer()) {
            inputStage = this.mSyntheticInputStage;
        } else {
            inputStage = queuedInputEvent.shouldSkipIme() ? this.mFirstPostImeInputStage : this.mFirstInputStage;
        }
        if (queuedInputEvent.mEvent instanceof KeyEvent) {
            this.mUnhandledKeyManager.preDispatch((KeyEvent) queuedInputEvent.mEvent);
        }
        if (inputStage != null) {
            handleWindowFocusChanged();
            inputStage.deliver(queuedInputEvent);
        } else {
            finishInputEvent(queuedInputEvent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishInputEvent(QueuedInputEvent queuedInputEvent) {
        Trace.asyncTraceEnd(8L, "deliverInputEvent", queuedInputEvent.mEvent.getSequenceNumber());
        if (queuedInputEvent.mReceiver != null) {
            queuedInputEvent.mReceiver.finishInputEvent(queuedInputEvent.mEvent, (queuedInputEvent.mFlags & 8) != 0);
        } else {
            queuedInputEvent.mEvent.recycleIfNeededAfterDispatch();
        }
        recycleQueuedInputEvent(queuedInputEvent);
    }

    private void adjustInputEventForCompatibility(InputEvent inputEvent) {
        if (this.mTargetSdkVersion >= 23 || !(inputEvent instanceof MotionEvent)) {
            return;
        }
        MotionEvent motionEvent = (MotionEvent) inputEvent;
        int buttonState = motionEvent.getButtonState();
        int i = (buttonState & 96) >> 4;
        if (i != 0) {
            motionEvent.setButtonState(buttonState | i);
        }
    }

    static boolean isTerminalInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof KeyEvent) {
            return ((KeyEvent) inputEvent).getAction() == 1;
        }
        int action = ((MotionEvent) inputEvent).getAction();
        return action == 1 || action == 3 || action == 10;
    }

    void scheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            return;
        }
        this.mConsumeBatchedInputScheduled = true;
        this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
    }

    void unscheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void scheduleConsumeBatchedInputImmediately() {
        if (this.mConsumeBatchedInputImmediatelyScheduled) {
            return;
        }
        unscheduleConsumeBatchedInput();
        this.mConsumeBatchedInputImmediatelyScheduled = true;
        this.mHandler.post(this.mConsumeBatchedInputImmediatelyRunnable);
    }

    void doConsumeBatchedInput(long j) {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
            if (windowInputEventReceiver != null && windowInputEventReceiver.consumeBatchedInputEvents(j) && j != -1) {
                scheduleConsumeBatchedInput();
            }
            doProcessInputEvents();
        }
    }

    final class TraversalRunnable implements Runnable {
        TraversalRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doTraversal();
        }
    }

    final class WindowInputEventReceiver extends InputEventReceiver {
        public WindowInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventReceiver
        public void onInputEvent(InputEvent inputEvent, int i) {
            ViewRootImpl.this.enqueueInputEvent(inputEvent, this, 0, true);
        }

        @Override // android.view.InputEventReceiver
        public void onBatchedInputEventPending() {
            if (ViewRootImpl.this.mUnbufferedInputDispatch) {
                super.onBatchedInputEventPending();
            } else {
                ViewRootImpl.this.scheduleConsumeBatchedInput();
            }
        }

        @Override // android.view.InputEventReceiver
        public void dispose() {
            ViewRootImpl.this.unscheduleConsumeBatchedInput();
            super.dispose();
        }
    }

    final class ConsumeBatchedInputRunnable implements Runnable {
        ConsumeBatchedInputRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl viewRootImpl = ViewRootImpl.this;
            viewRootImpl.doConsumeBatchedInput(viewRootImpl.mChoreographer.getFrameTimeNanos());
        }
    }

    final class ConsumeBatchedInputImmediatelyRunnable implements Runnable {
        ConsumeBatchedInputImmediatelyRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doConsumeBatchedInput(-1L);
        }
    }

    final class InvalidateOnAnimationRunnable implements Runnable {
        private boolean mPosted;
        private View.AttachInfo.InvalidateInfo[] mTempViewRects;
        private View[] mTempViews;
        private final ArrayList<View> mViews = new ArrayList<>();
        private final ArrayList<View.AttachInfo.InvalidateInfo> mViewRects = new ArrayList<>();

        InvalidateOnAnimationRunnable() {
        }

        public void addView(View view) {
            synchronized (this) {
                this.mViews.add(view);
                postIfNeededLocked();
            }
        }

        public void addViewRect(View.AttachInfo.InvalidateInfo invalidateInfo) {
            synchronized (this) {
                this.mViewRects.add(invalidateInfo);
                postIfNeededLocked();
            }
        }

        public void removeView(View view) {
            synchronized (this) {
                this.mViews.remove(view);
                int size = this.mViewRects.size();
                while (true) {
                    int i = size - 1;
                    if (size <= 0) {
                        break;
                    }
                    View.AttachInfo.InvalidateInfo invalidateInfo = this.mViewRects.get(i);
                    if (invalidateInfo.target == view) {
                        this.mViewRects.remove(i);
                        invalidateInfo.recycle();
                    }
                    size = i;
                }
                if (this.mPosted && this.mViews.isEmpty() && this.mViewRects.isEmpty()) {
                    ViewRootImpl.this.mChoreographer.removeCallbacks(1, this, null);
                    this.mPosted = false;
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int i;
            int size;
            int size2;
            synchronized (this) {
                this.mPosted = false;
                size = this.mViews.size();
                if (size != 0) {
                    this.mTempViews = (View[]) this.mViews.toArray(this.mTempViews != null ? this.mTempViews : new View[size]);
                    this.mViews.clear();
                }
                size2 = this.mViewRects.size();
                if (size2 != 0) {
                    this.mTempViewRects = (View.AttachInfo.InvalidateInfo[]) this.mViewRects.toArray(this.mTempViewRects != null ? this.mTempViewRects : new View.AttachInfo.InvalidateInfo[size2]);
                    this.mViewRects.clear();
                }
            }
            for (int i2 = 0; i2 < size; i2++) {
                this.mTempViews[i2].invalidate();
                this.mTempViews[i2] = null;
            }
            for (i = 0; i < size2; i++) {
                View.AttachInfo.InvalidateInfo invalidateInfo = this.mTempViewRects[i];
                invalidateInfo.target.invalidate(invalidateInfo.left, invalidateInfo.top, invalidateInfo.right, invalidateInfo.bottom);
                invalidateInfo.recycle();
            }
        }

        private void postIfNeededLocked() {
            if (this.mPosted) {
                return;
            }
            ViewRootImpl.this.mChoreographer.postCallback(1, this, null);
            this.mPosted = true;
        }
    }

    public void dispatchInvalidateDelayed(View view, long j) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, view), j);
    }

    public void dispatchInvalidateRectDelayed(View.AttachInfo.InvalidateInfo invalidateInfo, long j) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2, invalidateInfo), j);
    }

    public void dispatchInvalidateOnAnimation(View view) {
        this.mInvalidateOnAnimationRunnable.addView(view);
    }

    public void dispatchInvalidateRectOnAnimation(View.AttachInfo.InvalidateInfo invalidateInfo) {
        this.mInvalidateOnAnimationRunnable.addViewRect(invalidateInfo);
    }

    public void cancelInvalidate(View view) {
        this.mHandler.removeMessages(1, view);
        this.mHandler.removeMessages(2, view);
        this.mInvalidateOnAnimationRunnable.removeView(view);
    }

    public void dispatchInputEvent(InputEvent inputEvent) {
        dispatchInputEvent(inputEvent, null);
    }

    public void dispatchInputEvent(InputEvent inputEvent, InputEventReceiver inputEventReceiver) {
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.arg1 = inputEvent;
        someArgsObtain.arg2 = inputEventReceiver;
        Message messageObtainMessage = this.mHandler.obtainMessage(7, someArgsObtain);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void synthesizeInputEvent(InputEvent inputEvent) {
        Message messageObtainMessage = this.mHandler.obtainMessage(24, inputEvent);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchKeyFromIme(KeyEvent keyEvent) {
        Message messageObtainMessage = this.mHandler.obtainMessage(11, keyEvent);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchKeyFromAutofill(KeyEvent keyEvent) {
        Message messageObtainMessage = this.mHandler.obtainMessage(12, keyEvent);
        messageObtainMessage.setAsynchronous(true);
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchUnhandledInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            inputEvent = MotionEvent.obtain((MotionEvent) inputEvent);
        }
        synthesizeInputEvent(inputEvent);
    }

    public void dispatchAppVisibility(boolean z) {
        Message messageObtainMessage = this.mHandler.obtainMessage(8);
        messageObtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    public void dispatchGetNewSurface() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(9));
    }

    public void windowFocusChanged(boolean z, boolean z2) {
        synchronized (this) {
            this.mWindowFocusChanged = true;
            this.mUpcomingWindowFocus = z;
            this.mUpcomingInTouchMode = z2;
        }
        Message messageObtain = Message.obtain();
        messageObtain.what = 6;
        this.mHandler.sendMessage(messageObtain);
    }

    public void dispatchWindowShown() {
        this.mHandler.sendEmptyMessage(25);
    }

    public void dispatchCloseSystemDialogs(String str) {
        Message messageObtain = Message.obtain();
        messageObtain.what = 14;
        messageObtain.obj = str;
        this.mHandler.sendMessage(messageObtain);
    }

    public void dispatchDragEvent(DragEvent dragEvent) {
        int i;
        if (dragEvent.getAction() == 2) {
            i = 16;
            this.mHandler.removeMessages(16);
        } else {
            i = 15;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(i, dragEvent));
    }

    public void updatePointerIcon(float f, float f2) {
        this.mHandler.removeMessages(27);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(27, MotionEvent.obtain(0L, SystemClock.uptimeMillis(), 7, f, f2, 0)));
    }

    public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
        SystemUiVisibilityInfo systemUiVisibilityInfo = new SystemUiVisibilityInfo();
        systemUiVisibilityInfo.seq = i;
        systemUiVisibilityInfo.globalVisibility = i2;
        systemUiVisibilityInfo.localValue = i3;
        systemUiVisibilityInfo.localChanges = i4;
        ViewRootHandler viewRootHandler = this.mHandler;
        viewRootHandler.sendMessage(viewRootHandler.obtainMessage(17, systemUiVisibilityInfo));
    }

    public void dispatchCheckFocus() {
        if (this.mHandler.hasMessages(13)) {
            return;
        }
        this.mHandler.sendEmptyMessage(13);
    }

    public void dispatchRequestKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
        this.mHandler.obtainMessage(26, i, 0, iResultReceiver).sendToTarget();
    }

    public void dispatchPointerCaptureChanged(boolean z) {
        this.mHandler.removeMessages(28);
        Message messageObtainMessage = this.mHandler.obtainMessage(28);
        messageObtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(messageObtainMessage);
    }

    private void postSendWindowContentChangedCallback(View view, int i) {
        if (this.mSendWindowContentChangedAccessibilityEvent == null) {
            this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent();
        }
        this.mSendWindowContentChangedAccessibilityEvent.runOrPost(view, i);
    }

    private void removeSendWindowContentChangedCallback() {
        SendWindowContentChangedAccessibilityEvent sendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent;
        if (sendWindowContentChangedAccessibilityEvent != null) {
            this.mHandler.removeCallbacks(sendWindowContentChangedAccessibilityEvent);
        }
    }

    @Override // android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeProvider accessibilityNodeProvider;
        SendWindowContentChangedAccessibilityEvent sendWindowContentChangedAccessibilityEvent;
        if (this.mView == null || this.mStopped || this.mPausedForTransition) {
            return false;
        }
        if (accessibilityEvent.getEventType() != 2048 && (sendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent) != null && sendWindowContentChangedAccessibilityEvent.mSource != null) {
            this.mSendWindowContentChangedAccessibilityEvent.removeCallbacksAndRun();
        }
        int eventType = accessibilityEvent.getEventType();
        if (eventType == 2048) {
            handleWindowContentChangedEvent(accessibilityEvent);
        } else if (eventType == 32768) {
            long sourceNodeId = accessibilityEvent.getSourceNodeId();
            View viewFindViewByAccessibilityId = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(sourceNodeId));
            if (viewFindViewByAccessibilityId != null && (accessibilityNodeProvider = viewFindViewByAccessibilityId.getAccessibilityNodeProvider()) != null) {
                setAccessibilityFocus(viewFindViewByAccessibilityId, accessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(sourceNodeId)));
            }
        } else if (eventType == 65536) {
            View viewFindViewByAccessibilityId2 = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(accessibilityEvent.getSourceNodeId()));
            if (viewFindViewByAccessibilityId2 != null && viewFindViewByAccessibilityId2.getAccessibilityNodeProvider() != null) {
                setAccessibilityFocus(null, null);
            }
        }
        this.mAccessibilityManager.sendAccessibilityEvent(accessibilityEvent);
        return true;
    }

    private void handleWindowContentChangedEvent(AccessibilityEvent accessibilityEvent) {
        View view = this.mAccessibilityFocusedHost;
        if (view == null || this.mAccessibilityFocusedVirtualView == null) {
            return;
        }
        AccessibilityNodeProvider accessibilityNodeProvider = view.getAccessibilityNodeProvider();
        if (accessibilityNodeProvider == null) {
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            view.clearAccessibilityFocusNoCallbacks(0);
            return;
        }
        int contentChangeTypes = accessibilityEvent.getContentChangeTypes();
        if ((contentChangeTypes & 1) != 0 || contentChangeTypes == 0) {
            int accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(accessibilityEvent.getSourceNodeId());
            View view2 = this.mAccessibilityFocusedHost;
            boolean z = false;
            while (view2 != null && !z) {
                if (accessibilityViewId == view2.getAccessibilityViewId()) {
                    z = true;
                } else {
                    Object parent = view2.getParent();
                    view2 = parent instanceof View ? (View) parent : null;
                }
            }
            if (z) {
                int virtualDescendantId = AccessibilityNodeInfo.getVirtualDescendantId(this.mAccessibilityFocusedVirtualView.getSourceNodeId());
                Rect rect = this.mTempRect;
                this.mAccessibilityFocusedVirtualView.getBoundsInScreen(rect);
                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(virtualDescendantId);
                this.mAccessibilityFocusedVirtualView = accessibilityNodeInfoCreateAccessibilityNodeInfo;
                if (accessibilityNodeInfoCreateAccessibilityNodeInfo == null) {
                    this.mAccessibilityFocusedHost = null;
                    view.clearAccessibilityFocusNoCallbacks(0);
                    accessibilityNodeProvider.performAction(virtualDescendantId, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS.getId(), null);
                    invalidateRectOnScreen(rect);
                    return;
                }
                Rect boundsInScreen = accessibilityNodeInfoCreateAccessibilityNodeInfo.getBoundsInScreen();
                if (rect.equals(boundsInScreen)) {
                    return;
                }
                rect.union(boundsInScreen);
                invalidateRectOnScreen(rect);
            }
        }
    }

    @Override // android.view.ViewParent
    public void notifySubtreeAccessibilityStateChanged(View view, View view2, int i) {
        postSendWindowContentChangedCallback((View) Preconditions.checkNotNull(view2), i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getCommonPredecessor(View view, View view2) {
        if (this.mTempHashSet == null) {
            this.mTempHashSet = new HashSet<>();
        }
        HashSet<View> hashSet = this.mTempHashSet;
        hashSet.clear();
        while (view != null) {
            hashSet.add(view);
            Object obj = view.mParent;
            view = obj instanceof View ? (View) obj : null;
        }
        while (view2 != null) {
            if (hashSet.contains(view2)) {
                hashSet.clear();
                return view2;
            }
            Object obj2 = view2.mParent;
            view2 = obj2 instanceof View ? (View) obj2 : null;
        }
        hashSet.clear();
        return null;
    }

    void checkThread() {
        if (this.mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
        }
    }

    @Override // android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        if (rect == null) {
            return scrollToRectOrFocus(null, z);
        }
        rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
        boolean zScrollToRectOrFocus = scrollToRectOrFocus(rect, z);
        this.mTempRect.set(rect);
        this.mTempRect.offset(0, -this.mCurScrollY);
        this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
        try {
            this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect);
        } catch (RemoteException unused) {
        }
        return zScrollToRectOrFocus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportNextDraw() {
        if (!this.mReportNextDraw) {
            drawPending();
        }
        this.mReportNextDraw = true;
    }

    public void setReportNextDraw() {
        reportNextDraw();
        invalidate();
    }

    void changeCanvasOpacity(boolean z) {
        Log.d(this.mTag, "changeCanvasOpacity: opaque=" + z);
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.setOpaque(z);
        }
    }

    public boolean dispatchUnhandledKeyEvent(KeyEvent keyEvent) {
        return this.mUnhandledKeyManager.dispatch(this.mView, keyEvent);
    }

    class TakenSurfaceHolder extends BaseSurfaceHolder {
        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onRelayoutContainer() {
        }

        TakenSurfaceHolder() {
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public boolean onAllowLockCanvas() {
            return ViewRootImpl.this.mDrawingAllowed;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFormat(int i) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceFormat(i);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setType(int i) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceType(i);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onUpdateSurface() {
            throw new IllegalStateException("Shouldn't be here");
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return ViewRootImpl.this.mIsCreating;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFixedSize(int i, int i2) {
            throw new UnsupportedOperationException("Currently only support sizing from layout");
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(boolean z) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceKeepScreenOn(z);
        }
    }

    static class W extends IWindow.Stub {
        private final WeakReference<ViewRootImpl> mViewAncestor;
        private final IWindowSession mWindowSession;

        W(ViewRootImpl viewRootImpl) {
            this.mViewAncestor = new WeakReference<>(viewRootImpl);
            this.mWindowSession = viewRootImpl.mWindowSession;
        }

        @Override // android.view.IWindow
        public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, Rect rect6, boolean z, MergedConfiguration mergedConfiguration, Rect rect7, boolean z2, boolean z3, int i, DisplayCutout.ParcelableWrapper parcelableWrapper) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchResized(rect, rect2, rect3, rect4, rect5, rect6, z, mergedConfiguration, rect7, z2, z3, i, parcelableWrapper);
            }
        }

        @Override // android.view.IWindow
        public void moved(int i, int i2) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchMoved(i, i2);
            }
        }

        @Override // android.view.IWindow
        public void dispatchAppVisibility(boolean z) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchAppVisibility(z);
            }
        }

        @Override // android.view.IWindow
        public void dispatchGetNewSurface() {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchGetNewSurface();
            }
        }

        @Override // android.view.IWindow
        public void windowFocusChanged(boolean z, boolean z2) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.windowFocusChanged(z, z2);
            }
        }

        private static int checkCallingPermission(String str) {
            try {
                return ActivityManager.getService().checkPermission(str, Binder.getCallingPid(), Binder.getCallingUid());
            } catch (RemoteException unused) {
                return -1;
            }
        }

        @Override // android.view.IWindow
        public void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
            View view;
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl == null || (view = viewRootImpl.mView) == null) {
                return;
            }
            if (checkCallingPermission(Manifest.permission.DUMP) != 0) {
                throw new SecurityException("Insufficient permissions to invoke executeCommand() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            }
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = null;
            try {
                try {
                    try {
                        ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream2 = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                        try {
                            ViewDebug.dispatchCommand(view, str, str2, autoCloseOutputStream2);
                            autoCloseOutputStream2.close();
                        } catch (IOException e) {
                            e = e;
                            autoCloseOutputStream = autoCloseOutputStream2;
                            e.printStackTrace();
                            if (autoCloseOutputStream != null) {
                                autoCloseOutputStream.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            autoCloseOutputStream = autoCloseOutputStream2;
                            if (autoCloseOutputStream != null) {
                                try {
                                    autoCloseOutputStream.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                } catch (IOException e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }

        @Override // android.view.IWindow
        public void closeSystemDialogs(String str) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchCloseSystemDialogs(str);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, boolean z) {
            if (z) {
                try {
                    this.mWindowSession.wallpaperOffsetsComplete(asBinder());
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
            if (z) {
                try {
                    this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchDragEvent(DragEvent dragEvent) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchDragEvent(dragEvent);
            }
        }

        @Override // android.view.IWindow
        public void updatePointerIcon(float f, float f2) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.updatePointerIcon(f, f2);
            }
        }

        @Override // android.view.IWindow
        public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchSystemUiVisibilityChanged(i, i2, i3, i4);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWindowShown() {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchWindowShown();
            }
        }

        @Override // android.view.IWindow
        public void requestAppKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchRequestKeyboardShortcuts(iResultReceiver, i);
            }
        }

        @Override // android.view.IWindow
        public void dispatchPointerCaptureChanged(boolean z) {
            ViewRootImpl viewRootImpl = this.mViewAncestor.get();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchPointerCaptureChanged(z);
            }
        }
    }

    public static final class CalledFromWrongThreadException extends AndroidRuntimeException {
        public CalledFromWrongThreadException(String str) {
            super(str);
        }
    }

    static HandlerActionQueue getRunQueue() {
        HandlerActionQueue handlerActionQueue = sRunQueues.get();
        if (handlerActionQueue != null) {
            return handlerActionQueue;
        }
        HandlerActionQueue handlerActionQueue2 = new HandlerActionQueue();
        sRunQueues.set(handlerActionQueue2);
        return handlerActionQueue2;
    }

    private void startDragResizing(Rect rect, boolean z, Rect rect2, Rect rect3, int i) {
        if (this.mDragResizing) {
            return;
        }
        this.mDragResizing = true;
        if (this.mUseMTRenderer) {
            for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                this.mWindowCallbacks.get(size).onWindowDragResizeStart(rect, z, rect2, rect3, i);
            }
        }
        this.mFullRedrawNeeded = true;
    }

    private void endDragResizing() {
        if (this.mDragResizing) {
            this.mDragResizing = false;
            if (this.mUseMTRenderer) {
                for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                    this.mWindowCallbacks.get(size).onWindowDragResizeEnd();
                }
            }
            this.mFullRedrawNeeded = true;
        }
    }

    private boolean updateContentDrawBounds() {
        boolean zOnContentDrawn;
        if (this.mUseMTRenderer) {
            zOnContentDrawn = false;
            for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                zOnContentDrawn |= this.mWindowCallbacks.get(size).onContentDrawn(this.mWindowAttributes.surfaceInsets.left, this.mWindowAttributes.surfaceInsets.top, this.mWidth, this.mHeight);
            }
        } else {
            zOnContentDrawn = false;
        }
        return zOnContentDrawn | (this.mDragResizing && this.mReportNextDraw);
    }

    private void requestDrawWindow() {
        if (this.mUseMTRenderer) {
            this.mWindowDrawCountDown = new CountDownLatch(this.mWindowCallbacks.size());
            for (int size = this.mWindowCallbacks.size() - 1; size >= 0; size--) {
                this.mWindowCallbacks.get(size).onRequestDraw(this.mReportNextDraw);
            }
        }
    }

    public void reportActivityRelaunched() {
        this.mActivityRelaunched = true;
    }

    final class AccessibilityInteractionConnectionManager implements AccessibilityManager.AccessibilityStateChangeListener {
        AccessibilityInteractionConnectionManager() {
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean z) {
            if (z) {
                ensureConnection();
                if (!ViewRootImpl.this.mAttachInfo.mHasWindowFocus || ViewRootImpl.this.mView == null) {
                    return;
                }
                ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                View viewFindFocus = ViewRootImpl.this.mView.findFocus();
                if (viewFindFocus == null || viewFindFocus == ViewRootImpl.this.mView) {
                    return;
                }
                viewFindFocus.sendAccessibilityEvent(8);
                return;
            }
            ensureNoConnection();
            ViewRootImpl.this.mHandler.obtainMessage(21).sendToTarget();
        }

        public void ensureConnection() {
            if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
                return;
            }
            ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = ViewRootImpl.this.mAccessibilityManager.addAccessibilityInteractionConnection(ViewRootImpl.this.mWindow, ViewRootImpl.this.mContext.getPackageName(), new AccessibilityInteractionConnection(ViewRootImpl.this));
        }

        public void ensureNoConnection() {
            if (ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1) {
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = -1;
                ViewRootImpl.this.mAccessibilityManager.removeAccessibilityInteractionConnection(ViewRootImpl.this.mWindow);
            }
        }
    }

    final class HighContrastTextManager implements AccessibilityManager.HighTextContrastChangeListener {
        HighContrastTextManager() {
            ThreadedRenderer.setHighContrastText(ViewRootImpl.this.mAccessibilityManager.isHighTextContrastEnabled());
        }

        @Override // android.view.accessibility.AccessibilityManager.HighTextContrastChangeListener
        public void onHighTextContrastStateChanged(boolean z) {
            ThreadedRenderer.setHighContrastText(z);
            ViewRootImpl.this.destroyHardwareResources();
            ViewRootImpl.this.invalidate();
        }
    }

    static final class AccessibilityInteractionConnection extends IAccessibilityInteractionConnection.Stub {
        private final WeakReference<ViewRootImpl> mViewRootImpl;

        AccessibilityInteractionConnection(ViewRootImpl viewRootImpl) {
            this.mViewRootImpl = new WeakReference<>(viewRootImpl);
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfoByAccessibilityId(long j, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec, Bundle bundle) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfoByAccessibilityIdClientThread(j, region, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec, bundle);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void performAccessibilityAction(long j, int i, Bundle bundle, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().performAccessibilityActionClientThread(j, i, bundle, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setPerformAccessibilityActionResult(false, i2);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByViewId(long j, String str, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByViewIdClientThread(j, str, region, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByText(long j, String str, Region region, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByTextClientThread(j, str, region, i, iAccessibilityInteractionConnectionCallback, i2, i3, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findFocus(long j, int i, Region region, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findFocusClientThread(j, i, region, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i2);
                } catch (RemoteException unused) {
                }
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void focusSearch(long j, int i, Region region, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().focusSearchClientThread(j, i, region, i2, iAccessibilityInteractionConnectionCallback, i3, i4, j2, magnificationSpec);
            } else {
                try {
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i2);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    private class SendWindowContentChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        public long mLastEventTimeMillis;
        public StackTraceElement[] mOrigin;
        public View mSource;

        private SendWindowContentChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            View view = this.mSource;
            this.mSource = null;
            if (view == null) {
                Log.e(ViewRootImpl.TAG, "Accessibility content change has no source");
                return;
            }
            if (AccessibilityManager.getInstance(ViewRootImpl.this.mContext).isEnabled()) {
                this.mLastEventTimeMillis = SystemClock.uptimeMillis();
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain();
                accessibilityEventObtain.setEventType(2048);
                accessibilityEventObtain.setContentChangeTypes(this.mChangeTypes);
                view.sendAccessibilityEventUnchecked(accessibilityEventObtain);
            } else {
                this.mLastEventTimeMillis = 0L;
            }
            view.resetSubtreeAccessibilityStateChanged();
            this.mChangeTypes = 0;
        }

        public void runOrPost(View view, int i) {
            if (ViewRootImpl.this.mHandler.getLooper() != Looper.myLooper()) {
                Log.e(ViewRootImpl.TAG, "Accessibility content change on non-UI thread. Future Android versions will throw an exception.", new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views."));
                ViewRootImpl.this.mHandler.removeCallbacks(this);
                if (this.mSource != null) {
                    run();
                }
            }
            View view2 = this.mSource;
            if (view2 != null) {
                View commonPredecessor = ViewRootImpl.this.getCommonPredecessor(view2, view);
                if (commonPredecessor != null) {
                    commonPredecessor = commonPredecessor.getSelfOrParentImportantForA11y();
                }
                if (commonPredecessor != null) {
                    view = commonPredecessor;
                }
                this.mSource = view;
                this.mChangeTypes |= i;
                return;
            }
            this.mSource = view;
            this.mChangeTypes = i;
            long jUptimeMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
            long sendRecurringAccessibilityEventsInterval = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            if (jUptimeMillis >= sendRecurringAccessibilityEventsInterval) {
                removeCallbacksAndRun();
            } else {
                ViewRootImpl.this.mHandler.postDelayed(this, sendRecurringAccessibilityEventsInterval - jUptimeMillis);
            }
        }

        public void removeCallbacksAndRun() {
            ViewRootImpl.this.mHandler.removeCallbacks(this);
            run();
        }
    }

    private static class UnhandledKeyManager {
        private final SparseArray<WeakReference<View>> mCapturedKeys;
        private WeakReference<View> mCurrentReceiver;
        private boolean mDispatched;

        private UnhandledKeyManager() {
            this.mDispatched = true;
            this.mCapturedKeys = new SparseArray<>();
            this.mCurrentReceiver = null;
        }

        boolean dispatch(View view, KeyEvent keyEvent) {
            if (this.mDispatched) {
                return false;
            }
            try {
                Trace.traceBegin(8L, "UnhandledKeyEvent dispatch");
                this.mDispatched = true;
                View viewDispatchUnhandledKeyEvent = view.dispatchUnhandledKeyEvent(keyEvent);
                if (keyEvent.getAction() == 0) {
                    int keyCode = keyEvent.getKeyCode();
                    if (viewDispatchUnhandledKeyEvent != null && !KeyEvent.isModifierKey(keyCode)) {
                        this.mCapturedKeys.put(keyCode, new WeakReference<>(viewDispatchUnhandledKeyEvent));
                    }
                }
                Trace.traceEnd(8L);
                return viewDispatchUnhandledKeyEvent != null;
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }

        void preDispatch(KeyEvent keyEvent) {
            int iIndexOfKey;
            this.mCurrentReceiver = null;
            if (keyEvent.getAction() != 1 || (iIndexOfKey = this.mCapturedKeys.indexOfKey(keyEvent.getKeyCode())) < 0) {
                return;
            }
            this.mCurrentReceiver = this.mCapturedKeys.valueAt(iIndexOfKey);
            this.mCapturedKeys.removeAt(iIndexOfKey);
        }

        boolean preViewDispatch(KeyEvent keyEvent) {
            this.mDispatched = false;
            if (this.mCurrentReceiver == null) {
                this.mCurrentReceiver = this.mCapturedKeys.get(keyEvent.getKeyCode());
            }
            WeakReference<View> weakReference = this.mCurrentReceiver;
            if (weakReference == null) {
                return false;
            }
            View view = weakReference.get();
            if (keyEvent.getAction() == 1) {
                this.mCurrentReceiver = null;
            }
            if (view != null && view.isAttachedToWindow()) {
                view.onUnhandledKeyEvent(keyEvent);
            }
            return true;
        }
    }
}
