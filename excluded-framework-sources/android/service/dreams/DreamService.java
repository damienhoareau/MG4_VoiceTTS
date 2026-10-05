package android.service.dreams;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.MathUtils;
import android.util.Slog;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.util.DumpUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes2.dex */
public class DreamService extends Service implements Window.Callback {
    public static final String DREAM_META_DATA = "android.service.dream";
    public static final String DREAM_SERVICE = "dreams";
    public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";
    private boolean mCanDoze;
    private boolean mDozing;
    private boolean mFinished;
    private boolean mFullscreen;
    private boolean mInteractive;
    private boolean mStarted;
    private boolean mWaking;
    private Window mWindow;
    private IBinder mWindowToken;
    private boolean mWindowless;
    private final String TAG = DreamService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
    private final Handler mHandler = new Handler();
    private boolean mLowProfile = true;
    private boolean mScreenBright = true;
    private int mDozeScreenState = 0;
    private int mDozeScreenBrightness = -1;
    private boolean mDebug = false;
    private final IDreamManager mSandman = IDreamManager.Stub.asInterface(ServiceManager.getService(DREAM_SERVICE));

    private int applyFlags(int i, int i2, int i3) {
        return (i & (~i3)) | (i2 & i3);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode actionMode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode actionMode) {
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int i) {
        return null;
    }

    @Override // android.view.Window.Callback
    public void onDetachedFromWindow() {
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int i, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int i, Menu menu) {
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
        return null;
    }

    public void setDebug(boolean z) {
        this.mDebug = z;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on keyEvent");
            }
            wakeUp();
            return true;
        }
        if (keyEvent.getKeyCode() == 4) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on back key");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchKeyEvent(keyEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on keyShortcutEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchKeyShortcutEvent(keyEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on touchEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchTouchEvent(motionEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on trackballEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchTrackballEvent(motionEvent);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Waking up on genericMotionEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchGenericMotionEvent(motionEvent);
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return onSearchRequested();
    }

    public WindowManager getWindowManager() {
        Window window = this.mWindow;
        if (window != null) {
            return window.getWindowManager();
        }
        return null;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    public void setContentView(int i) {
        getWindow().setContentView(i);
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getWindow().setContentView(view, layoutParams);
    }

    public void addContentView(View view, ViewGroup.LayoutParams layoutParams) {
        getWindow().addContentView(view, layoutParams);
    }

    public <T extends View> T findViewById(int i) {
        return (T) getWindow().findViewById(i);
    }

    public final <T extends View> T requireViewById(int i) {
        T t = (T) findViewById(i);
        if (t != null) {
            return t;
        }
        throw new IllegalArgumentException("ID does not reference a View inside this DreamService");
    }

    public void setInteractive(boolean z) {
        this.mInteractive = z;
    }

    public boolean isInteractive() {
        return this.mInteractive;
    }

    public void setLowProfile(boolean z) {
        if (this.mLowProfile != z) {
            this.mLowProfile = z;
            applySystemUiVisibilityFlags(z ? 1 : 0, 1);
        }
    }

    public boolean isLowProfile() {
        return getSystemUiVisibilityFlagValue(1, this.mLowProfile);
    }

    public void setFullscreen(boolean z) {
        if (this.mFullscreen != z) {
            this.mFullscreen = z;
            applyWindowFlags(z ? 1024 : 0, 1024);
        }
    }

    public boolean isFullscreen() {
        return this.mFullscreen;
    }

    public void setScreenBright(boolean z) {
        if (this.mScreenBright != z) {
            this.mScreenBright = z;
            applyWindowFlags(z ? 128 : 0, 128);
        }
    }

    public boolean isScreenBright() {
        return getWindowFlagValue(128, this.mScreenBright);
    }

    public void setWindowless(boolean z) {
        this.mWindowless = z;
    }

    public boolean isWindowless() {
        return this.mWindowless;
    }

    public boolean canDoze() {
        return this.mCanDoze;
    }

    public void startDozing() {
        if (!this.mCanDoze || this.mDozing) {
            return;
        }
        this.mDozing = true;
        updateDoze();
    }

    private void updateDoze() {
        IBinder iBinder = this.mWindowToken;
        if (iBinder == null) {
            Slog.w(this.TAG, "Updating doze without a window token.");
        } else if (this.mDozing) {
            try {
                this.mSandman.startDozing(iBinder, this.mDozeScreenState, this.mDozeScreenBrightness);
            } catch (RemoteException unused) {
            }
        }
    }

    public void stopDozing() {
        if (this.mDozing) {
            this.mDozing = false;
            try {
                this.mSandman.stopDozing(this.mWindowToken);
            } catch (RemoteException unused) {
            }
        }
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public int getDozeScreenState() {
        return this.mDozeScreenState;
    }

    public void setDozeScreenState(int i) {
        if (this.mDozeScreenState != i) {
            this.mDozeScreenState = i;
            updateDoze();
        }
    }

    public int getDozeScreenBrightness() {
        return this.mDozeScreenBrightness;
    }

    public void setDozeScreenBrightness(int i) {
        if (i != -1) {
            i = clampAbsoluteBrightness(i);
        }
        if (this.mDozeScreenBrightness != i) {
            this.mDozeScreenBrightness = i;
            updateDoze();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onCreate()");
        }
        super.onCreate();
    }

    public void onDreamingStarted() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStarted()");
        }
    }

    public void onDreamingStopped() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStopped()");
        }
    }

    public void onWakeUp() {
        finish();
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mDebug) {
            Slog.v(this.TAG, "onBind() intent = " + intent);
        }
        return new DreamServiceWrapper();
    }

    public final void finish() {
        if (this.mDebug) {
            Slog.v(this.TAG, "finish(): mFinished=" + this.mFinished);
        }
        if (this.mFinished) {
            return;
        }
        this.mFinished = true;
        IBinder iBinder = this.mWindowToken;
        if (iBinder == null) {
            Slog.w(this.TAG, "Finish was called before the dream was attached.");
        } else {
            try {
                this.mSandman.finishSelf(iBinder, true);
            } catch (RemoteException unused) {
            }
        }
        stopSelf();
    }

    public final void wakeUp() {
        wakeUp(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void wakeUp(boolean z) {
        if (this.mDebug) {
            Slog.v(this.TAG, "wakeUp(): fromSystem=" + z + ", mWaking=" + this.mWaking + ", mFinished=" + this.mFinished);
        }
        if (this.mWaking || this.mFinished) {
            return;
        }
        this.mWaking = true;
        onWakeUp();
        if (z || this.mFinished) {
            return;
        }
        IBinder iBinder = this.mWindowToken;
        if (iBinder == null) {
            Slog.w(this.TAG, "WakeUp was called before the dream was attached.");
        } else {
            try {
                this.mSandman.finishSelf(iBinder, false);
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDestroy()");
        }
        detach();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void detach() {
        if (this.mStarted) {
            if (this.mDebug) {
                Slog.v(this.TAG, "detach(): Calling onDreamingStopped()");
            }
            this.mStarted = false;
            onDreamingStopped();
        }
        if (this.mWindow != null) {
            if (this.mDebug) {
                Slog.v(this.TAG, "detach(): Removing window from window manager");
            }
            this.mWindow.getWindowManager().removeViewImmediate(this.mWindow.getDecorView());
            this.mWindow = null;
        }
        if (this.mWindowToken != null) {
            WindowManagerGlobal.getInstance().closeAll(this.mWindowToken, getClass().getName(), "Dream");
            this.mWindowToken = null;
            this.mCanDoze = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void attach(IBinder iBinder, boolean z, final IRemoteCallback iRemoteCallback) {
        if (this.mWindowToken != null) {
            Slog.e(this.TAG, "attach() called when already attached with token=" + this.mWindowToken);
            return;
        }
        if (this.mFinished || this.mWaking) {
            Slog.w(this.TAG, "attach() called after dream already finished");
            try {
                this.mSandman.finishSelf(iBinder, true);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        this.mWindowToken = iBinder;
        this.mCanDoze = z;
        if (this.mWindowless && !z) {
            throw new IllegalStateException("Only doze dreams can be windowless");
        }
        if (!this.mWindowless) {
            PhoneWindow phoneWindow = new PhoneWindow(this);
            this.mWindow = phoneWindow;
            phoneWindow.setCallback(this);
            this.mWindow.requestFeature(1);
            this.mWindow.setBackgroundDrawable(new ColorDrawable(-16777216));
            this.mWindow.setFormat(-1);
            if (this.mDebug) {
                Slog.v(this.TAG, String.format("Attaching window token: %s to window of type %s", iBinder, Integer.valueOf(WindowManager.LayoutParams.TYPE_DREAM)));
            }
            WindowManager.LayoutParams attributes = this.mWindow.getAttributes();
            attributes.type = WindowManager.LayoutParams.TYPE_DREAM;
            attributes.token = iBinder;
            attributes.windowAnimations = R.style.Animation_Dream;
            attributes.flags |= (this.mScreenBright ? 128 : 0) | 4784385 | (this.mFullscreen ? 1024 : 0);
            this.mWindow.setAttributes(attributes);
            this.mWindow.clearFlags(Integer.MIN_VALUE);
            this.mWindow.setWindowManager(null, iBinder, "dream", true);
            applySystemUiVisibilityFlags(this.mLowProfile ? 1 : 0, 1);
            try {
                getWindowManager().addView(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            } catch (WindowManager.BadTokenException unused2) {
                Slog.i(this.TAG, "attach() called after window token already removed, dream will finish soon");
                this.mWindow = null;
                return;
            }
        }
        this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.1
            @Override // java.lang.Runnable
            public void run() {
                if (DreamService.this.mWindow != null || DreamService.this.mWindowless) {
                    if (DreamService.this.mDebug) {
                        Slog.v(DreamService.this.TAG, "Calling onDreamingStarted()");
                    }
                    DreamService.this.mStarted = true;
                    try {
                        DreamService.this.onDreamingStarted();
                        try {
                            iRemoteCallback.sendResult(null);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    } catch (Throwable th) {
                        try {
                            iRemoteCallback.sendResult(null);
                            throw th;
                        } catch (RemoteException e2) {
                            throw e2.rethrowFromSystemServer();
                        }
                    }
                }
            }
        });
    }

    private boolean getWindowFlagValue(int i, boolean z) {
        Window window = this.mWindow;
        if (window == null) {
            return z;
        }
        return (i & window.getAttributes().flags) != 0;
    }

    private void applyWindowFlags(int i, int i2) {
        Window window = this.mWindow;
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags = applyFlags(attributes.flags, i, i2);
            this.mWindow.setAttributes(attributes);
            this.mWindow.getWindowManager().updateViewLayout(this.mWindow.getDecorView(), attributes);
        }
    }

    private boolean getSystemUiVisibilityFlagValue(int i, boolean z) {
        Window window = this.mWindow;
        View decorView = window == null ? null : window.getDecorView();
        if (decorView == null) {
            return z;
        }
        return (i & decorView.getSystemUiVisibility()) != 0;
    }

    private void applySystemUiVisibilityFlags(int i, int i2) {
        Window window = this.mWindow;
        View decorView = window == null ? null : window.getDecorView();
        if (decorView != null) {
            decorView.setSystemUiVisibility(applyFlags(decorView.getSystemUiVisibility(), i, i2));
        }
    }

    @Override // android.app.Service
    protected void dump(final FileDescriptor fileDescriptor, PrintWriter printWriter, final String[] strArr) {
        DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump() { // from class: android.service.dreams.DreamService.2
            @Override // com.android.internal.util.DumpUtils.Dump
            public void dump(PrintWriter printWriter2, String str) {
                DreamService.this.dumpOnHandler(fileDescriptor, printWriter2, strArr);
            }
        }, printWriter, "", 1000L);
    }

    protected void dumpOnHandler(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(this.TAG + ": ");
        if (this.mWindowToken == null) {
            printWriter.println("stopped");
        } else {
            printWriter.println("running (token=" + this.mWindowToken + ")");
        }
        printWriter.println("  window: " + this.mWindow);
        printWriter.print("  flags:");
        if (isInteractive()) {
            printWriter.print(" interactive");
        }
        if (isLowProfile()) {
            printWriter.print(" lowprofile");
        }
        if (isFullscreen()) {
            printWriter.print(" fullscreen");
        }
        if (isScreenBright()) {
            printWriter.print(" bright");
        }
        if (isWindowless()) {
            printWriter.print(" windowless");
        }
        if (isDozing()) {
            printWriter.print(" dozing");
        } else if (canDoze()) {
            printWriter.print(" candoze");
        }
        printWriter.println();
        if (canDoze()) {
            printWriter.println("  doze screen state: " + Display.stateToString(this.mDozeScreenState));
            printWriter.println("  doze screen brightness: " + this.mDozeScreenBrightness);
        }
    }

    private static int clampAbsoluteBrightness(int i) {
        return MathUtils.constrain(i, 0, 255);
    }

    private final class DreamServiceWrapper extends IDreamService.Stub {
        private DreamServiceWrapper() {
        }

        @Override // android.service.dreams.IDreamService
        public void attach(final IBinder iBinder, final boolean z, final IRemoteCallback iRemoteCallback) {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.attach(iBinder, z, iRemoteCallback);
                }
            });
        }

        @Override // android.service.dreams.IDreamService
        public void detach() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.detach();
                }
            });
        }

        @Override // android.service.dreams.IDreamService
        public void wakeUp() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.3
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.wakeUp(true);
                }
            });
        }
    }
}
