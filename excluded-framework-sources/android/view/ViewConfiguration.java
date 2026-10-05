package android.view;

import android.app.AppGlobals;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.android.internal.R;

/* JADX INFO: loaded from: classes2.dex */
public class ViewConfiguration {
    private static final int A11Y_SHORTCUT_KEY_TIMEOUT = 3000;
    private static final int A11Y_SHORTCUT_KEY_TIMEOUT_AFTER_CONFIRMATION = 1000;
    private static final long ACTION_MODE_HIDE_DURATION_DEFAULT = 2000;
    private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;
    private static final int DEFAULT_MULTI_PRESS_TIMEOUT = 300;
    private static final int DOUBLE_TAP_MIN_TIME = 40;
    private static final int DOUBLE_TAP_SLOP = 100;
    private static final int DOUBLE_TAP_TIMEOUT = 300;
    private static final int DOUBLE_TAP_TOUCH_SLOP = 8;
    private static final int EDGE_SLOP = 12;
    private static final int FADING_EDGE_LENGTH = 12;
    private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
    private static final int HAS_PERMANENT_MENU_KEY_AUTODETECT = 0;
    private static final int HAS_PERMANENT_MENU_KEY_FALSE = 2;
    private static final int HAS_PERMANENT_MENU_KEY_TRUE = 1;
    private static final float HORIZONTAL_SCROLL_FACTOR = 64.0f;
    private static final int HOVER_TAP_SLOP = 20;
    private static final int HOVER_TAP_TIMEOUT = 150;
    private static final int HOVER_TOOLTIP_HIDE_SHORT_TIMEOUT = 3000;
    private static final int HOVER_TOOLTIP_HIDE_TIMEOUT = 15000;
    private static final int HOVER_TOOLTIP_SHOW_TIMEOUT = 500;
    private static final int JUMP_TAP_TIMEOUT = 500;
    private static final int KEY_REPEAT_DELAY = 50;
    private static final int LONG_PRESS_TOOLTIP_HIDE_TIMEOUT = 1500;

    @Deprecated
    private static final int MAXIMUM_DRAWING_CACHE_SIZE = 1536000;
    private static final int MAXIMUM_FLING_VELOCITY = 8000;
    private static final int MINIMUM_FLING_VELOCITY = 50;
    private static final int MIN_SCROLLBAR_TOUCH_TARGET = 48;
    private static final int OVERFLING_DISTANCE = 6;
    private static final int OVERSCROLL_DISTANCE = 0;
    private static final int PAGING_TOUCH_SLOP = 16;
    private static final int PRESSED_STATE_DURATION = 64;
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;
    private static final int SCROLL_BAR_FADE_DURATION = 250;
    private static final int SCROLL_BAR_SIZE = 4;
    private static final float SCROLL_FRICTION = 0.015f;
    private static final long SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS = 100;
    private static final int TAP_TIMEOUT = 100;
    private static final int TOUCH_SLOP = 8;
    private static final float VERTICAL_SCROLL_FACTOR = 64.0f;
    private static final int WINDOW_TOUCH_SLOP = 16;
    private static final int ZOOM_CONTROLS_TIMEOUT = 3000;
    static final SparseArray<ViewConfiguration> sConfigurations = new SparseArray<>(2);
    private final int mDoubleTapSlop;
    private final int mDoubleTapTouchSlop;
    private final int mEdgeSlop;
    private final int mFadingEdgeLength;
    private final boolean mFadingMarqueeEnabled;
    private final long mGlobalActionsKeyTimeout;
    private final float mHorizontalScrollFactor;
    private final int mHoverSlop;
    private final int mMaximumDrawingCacheSize;
    private final int mMaximumFlingVelocity;
    private final int mMinScrollbarTouchTarget;
    private final int mMinimumFlingVelocity;
    private final int mOverflingDistance;
    private final int mOverscrollDistance;
    private final int mPagingTouchSlop;
    private final int mScrollbarSize;
    private final boolean mShowMenuShortcutsWhenKeyboardPresent;
    private final int mTouchSlop;
    private final float mVerticalScrollFactor;
    private final int mWindowTouchSlop;
    private boolean sHasPermanentMenuKey;
    private boolean sHasPermanentMenuKeySet;

    public static long getDefaultActionModeHideDuration() {
        return ACTION_MODE_HIDE_DURATION_DEFAULT;
    }

    public static int getDoubleTapMinTime() {
        return 40;
    }

    @Deprecated
    public static int getDoubleTapSlop() {
        return 100;
    }

    public static int getDoubleTapTimeout() {
        return 300;
    }

    @Deprecated
    public static int getEdgeSlop() {
        return 12;
    }

    @Deprecated
    public static int getFadingEdgeLength() {
        return 12;
    }

    @Deprecated
    public static long getGlobalActionKeyTimeout() {
        return 500L;
    }

    public static int getHoverTapSlop() {
        return 20;
    }

    public static int getHoverTapTimeout() {
        return 150;
    }

    public static int getHoverTooltipHideShortTimeout() {
        return PathInterpolatorCompat.MAX_NUM_POINTS;
    }

    public static int getHoverTooltipHideTimeout() {
        return HOVER_TOOLTIP_HIDE_TIMEOUT;
    }

    public static int getHoverTooltipShowTimeout() {
        return 500;
    }

    public static int getJumpTapTimeout() {
        return 500;
    }

    public static int getKeyRepeatDelay() {
        return 50;
    }

    public static int getLongPressTooltipHideTimeout() {
        return 1500;
    }

    @Deprecated
    public static int getMaximumDrawingCacheSize() {
        return MAXIMUM_DRAWING_CACHE_SIZE;
    }

    @Deprecated
    public static int getMaximumFlingVelocity() {
        return MAXIMUM_FLING_VELOCITY;
    }

    @Deprecated
    public static int getMinimumFlingVelocity() {
        return 50;
    }

    public static int getPressedStateDuration() {
        return 64;
    }

    public static int getScrollBarFadeDuration() {
        return 250;
    }

    @Deprecated
    public static int getScrollBarSize() {
        return 4;
    }

    public static int getScrollDefaultDelay() {
        return 300;
    }

    public static float getScrollFriction() {
        return SCROLL_FRICTION;
    }

    public static long getSendRecurringAccessibilityEventsInterval() {
        return SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS;
    }

    public static int getTapTimeout() {
        return 100;
    }

    @Deprecated
    public static int getTouchSlop() {
        return 8;
    }

    @Deprecated
    public static int getWindowTouchSlop() {
        return 16;
    }

    public static long getZoomControlsTimeout() {
        return 3000L;
    }

    public long getAccessibilityShortcutKeyTimeout() {
        return 3000L;
    }

    public long getAccessibilityShortcutKeyTimeoutAfterConfirmation() {
        return 1000L;
    }

    @Deprecated
    public ViewConfiguration() {
        this.mEdgeSlop = 12;
        this.mFadingEdgeLength = 12;
        this.mMinimumFlingVelocity = 50;
        this.mMaximumFlingVelocity = MAXIMUM_FLING_VELOCITY;
        this.mScrollbarSize = 4;
        this.mTouchSlop = 8;
        this.mHoverSlop = 4;
        this.mMinScrollbarTouchTarget = 48;
        this.mDoubleTapTouchSlop = 8;
        this.mPagingTouchSlop = 16;
        this.mDoubleTapSlop = 100;
        this.mWindowTouchSlop = 16;
        this.mMaximumDrawingCacheSize = MAXIMUM_DRAWING_CACHE_SIZE;
        this.mOverscrollDistance = 0;
        this.mOverflingDistance = 6;
        this.mFadingMarqueeEnabled = true;
        this.mGlobalActionsKeyTimeout = 500L;
        this.mHorizontalScrollFactor = 64.0f;
        this.mVerticalScrollFactor = 64.0f;
        this.mShowMenuShortcutsWhenKeyboardPresent = false;
    }

    private ViewConfiguration(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        float f = displayMetrics.density;
        f = configuration.isLayoutSizeAtLeast(4) ? f * 1.5f : f;
        int i = (int) ((12.0f * f) + 0.5f);
        this.mEdgeSlop = i;
        this.mFadingEdgeLength = i;
        this.mScrollbarSize = resources.getDimensionPixelSize(R.dimen.config_scrollbarSize);
        this.mDoubleTapSlop = (int) ((100.0f * f) + 0.5f);
        this.mWindowTouchSlop = (int) ((16.0f * f) + 0.5f);
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        this.mMaximumDrawingCacheSize = point.x * 4 * point.y;
        this.mOverscrollDistance = (int) ((0.0f * f) + 0.5f);
        this.mOverflingDistance = (int) ((f * 6.0f) + 0.5f);
        if (!this.sHasPermanentMenuKeySet) {
            int integer = resources.getInteger(R.integer.config_overrideHasPermanentMenuKey);
            if (integer == 1) {
                this.sHasPermanentMenuKey = true;
                this.sHasPermanentMenuKeySet = true;
            } else if (integer != 2) {
                try {
                    this.sHasPermanentMenuKey = !WindowManagerGlobal.getWindowManagerService().hasNavigationBar();
                    this.sHasPermanentMenuKeySet = true;
                } catch (RemoteException unused) {
                    this.sHasPermanentMenuKey = false;
                }
            } else {
                this.sHasPermanentMenuKey = false;
                this.sHasPermanentMenuKeySet = true;
            }
        }
        this.mFadingMarqueeEnabled = resources.getBoolean(R.bool.config_ui_enableFadingMarquee);
        this.mTouchSlop = resources.getDimensionPixelSize(R.dimen.config_viewConfigurationTouchSlop);
        this.mHoverSlop = resources.getDimensionPixelSize(R.dimen.config_viewConfigurationHoverSlop);
        this.mMinScrollbarTouchTarget = resources.getDimensionPixelSize(R.dimen.config_minScrollbarTouchTarget);
        int i2 = this.mTouchSlop;
        this.mPagingTouchSlop = i2 * 2;
        this.mDoubleTapTouchSlop = i2;
        this.mMinimumFlingVelocity = resources.getDimensionPixelSize(R.dimen.config_viewMinFlingVelocity);
        this.mMaximumFlingVelocity = resources.getDimensionPixelSize(R.dimen.config_viewMaxFlingVelocity);
        this.mGlobalActionsKeyTimeout = resources.getInteger(R.integer.config_globalActionsKeyTimeout);
        this.mHorizontalScrollFactor = resources.getDimensionPixelSize(R.dimen.config_horizontalScrollFactor);
        this.mVerticalScrollFactor = resources.getDimensionPixelSize(R.dimen.config_verticalScrollFactor);
        this.mShowMenuShortcutsWhenKeyboardPresent = resources.getBoolean(R.bool.config_showMenuShortcutsWhenKeyboardPresent);
    }

    public static ViewConfiguration get(Context context) {
        int i = (int) (context.getResources().getDisplayMetrics().density * 100.0f);
        ViewConfiguration viewConfiguration = sConfigurations.get(i);
        if (viewConfiguration != null) {
            return viewConfiguration;
        }
        ViewConfiguration viewConfiguration2 = new ViewConfiguration(context);
        sConfigurations.put(i, viewConfiguration2);
        return viewConfiguration2;
    }

    public int getScaledScrollBarSize() {
        return this.mScrollbarSize;
    }

    public int getScaledMinScrollbarTouchTarget() {
        return this.mMinScrollbarTouchTarget;
    }

    public int getScaledFadingEdgeLength() {
        return this.mFadingEdgeLength;
    }

    public static int getLongPressTimeout() {
        return AppGlobals.getIntCoreSetting(Settings.Secure.LONG_PRESS_TIMEOUT, 500);
    }

    public static int getMultiPressTimeout() {
        return AppGlobals.getIntCoreSetting(Settings.Secure.MULTI_PRESS_TIMEOUT, 300);
    }

    public static int getKeyRepeatTimeout() {
        return getLongPressTimeout();
    }

    public int getScaledEdgeSlop() {
        return this.mEdgeSlop;
    }

    public int getScaledTouchSlop() {
        return this.mTouchSlop;
    }

    public int getScaledHoverSlop() {
        return this.mHoverSlop;
    }

    public int getScaledDoubleTapTouchSlop() {
        return this.mDoubleTapTouchSlop;
    }

    public int getScaledPagingTouchSlop() {
        return this.mPagingTouchSlop;
    }

    public int getScaledDoubleTapSlop() {
        return this.mDoubleTapSlop;
    }

    public int getScaledWindowTouchSlop() {
        return this.mWindowTouchSlop;
    }

    public int getScaledMinimumFlingVelocity() {
        return this.mMinimumFlingVelocity;
    }

    public int getScaledMaximumFlingVelocity() {
        return this.mMaximumFlingVelocity;
    }

    public int getScaledScrollFactor() {
        return (int) this.mVerticalScrollFactor;
    }

    public float getScaledHorizontalScrollFactor() {
        return this.mHorizontalScrollFactor;
    }

    public float getScaledVerticalScrollFactor() {
        return this.mVerticalScrollFactor;
    }

    public int getScaledMaximumDrawingCacheSize() {
        return this.mMaximumDrawingCacheSize;
    }

    public int getScaledOverscrollDistance() {
        return this.mOverscrollDistance;
    }

    public int getScaledOverflingDistance() {
        return this.mOverflingDistance;
    }

    public long getDeviceGlobalActionKeyTimeout() {
        return this.mGlobalActionsKeyTimeout;
    }

    public boolean hasPermanentMenuKey() {
        return this.sHasPermanentMenuKey;
    }

    public boolean shouldShowMenuShortcutsWhenKeyboardPresent() {
        return this.mShowMenuShortcutsWhenKeyboardPresent;
    }

    public boolean isFadingMarqueeEnabled() {
        return this.mFadingMarqueeEnabled;
    }
}
