package android.view.inputmethod;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.Trace;
import android.provider.SettingsStringUtil;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.Pools;
import android.util.PrintWriterPrinter;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.autofill.AutofillManager;
import com.android.internal.inputmethod.IInputContentUriToken;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputConnectionWrapper;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.InputBindResult;
import com.android.internal.view.InputMethodClient;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public final class InputMethodManager {
    public static final int CONTROL_START_INITIAL = 256;
    public static final int CONTROL_WINDOW_FIRST = 4;
    public static final int CONTROL_WINDOW_IS_TEXT_EDITOR = 2;
    public static final int CONTROL_WINDOW_VIEW_HAS_FOCUS = 1;
    static final boolean DEBUG = false;
    public static final int DISPATCH_HANDLED = 1;
    public static final int DISPATCH_IN_PROGRESS = -1;
    public static final int DISPATCH_NOT_HANDLED = 0;
    public static final int HIDE_IMPLICIT_ONLY = 1;
    public static final int HIDE_NOT_ALWAYS = 2;
    static final long INPUT_METHOD_NOT_RESPONDING_TIMEOUT = 2500;
    static final int MSG_BIND = 2;
    static final int MSG_DUMP = 1;
    static final int MSG_FLUSH_INPUT_EVENT = 7;
    static final int MSG_REPORT_FULLSCREEN_MODE = 10;
    static final int MSG_SEND_INPUT_EVENT = 5;
    static final int MSG_SET_ACTIVE = 4;
    static final int MSG_SET_USER_ACTION_NOTIFICATION_SEQUENCE_NUMBER = 9;
    static final int MSG_TIMEOUT_INPUT_EVENT = 6;
    static final int MSG_UNBIND = 3;
    private static final int NOT_AN_ACTION_NOTIFICATION_SEQUENCE_NUMBER = -1;
    static final String PENDING_EVENT_COUNTER = "aq:imm";
    private static final int REQUEST_UPDATE_CURSOR_ANCHOR_INFO_NONE = 0;
    public static final int RESULT_HIDDEN = 3;
    public static final int RESULT_SHOWN = 2;
    public static final int RESULT_UNCHANGED_HIDDEN = 1;
    public static final int RESULT_UNCHANGED_SHOWN = 0;
    public static final int SHOW_FORCED = 2;
    public static final int SHOW_IMPLICIT = 1;
    public static final int SHOW_IM_PICKER_MODE_AUTO = 0;
    public static final int SHOW_IM_PICKER_MODE_EXCLUDE_AUXILIARY_SUBTYPES = 2;
    public static final int SHOW_IM_PICKER_MODE_INCLUDE_AUXILIARY_SUBTYPES = 1;
    static final String TAG = "InputMethodManager";
    static InputMethodManager sInstance;
    boolean mActive;
    int mBindSequence;
    final IInputMethodClient.Stub mClient;
    CompletionInfo[] mCompletions;
    InputChannel mCurChannel;
    String mCurId;
    IInputMethodSession mCurMethod;
    View mCurRootView;
    ImeInputEventSender mCurSender;
    EditorInfo mCurrentTextBoxAttribute;
    private CursorAnchorInfo mCursorAnchorInfo;
    int mCursorCandEnd;
    int mCursorCandStart;
    Rect mCursorRect;
    int mCursorSelEnd;
    int mCursorSelStart;
    final InputConnection mDummyInputConnection;
    boolean mFullscreenMode;
    final H mH;
    final IInputContext mIInputContext;
    private int mLastSentUserActionNotificationSequenceNumber;
    final Looper mMainLooper;
    View mNextServedView;
    private int mNextUserActionNotificationSequenceNumber;
    final Pools.Pool<PendingEvent> mPendingEventPool;
    final SparseArray<PendingEvent> mPendingEvents;
    private int mRequestUpdateCursorAnchorInfoMonitorMode;
    boolean mRestartOnNextWindowFocus;
    boolean mServedConnecting;
    ControlledInputConnectionWrapper mServedInputConnectionWrapper;
    View mServedView;
    final IInputMethodManager mService;
    Rect mTmpCursorRect;

    public interface FinishedInputEventCallback {
        void onFinishedInputEvent(Object obj, boolean z);
    }

    @Deprecated
    public boolean isWatchingCursor(View view) {
        return false;
    }

    private static boolean isAutofillUIShowing(View view) {
        AutofillManager autofillManager = (AutofillManager) view.getContext().getSystemService(AutofillManager.class);
        return autofillManager != null && autofillManager.isAutofillUiShowing();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean canStartInput(View view) {
        return view.hasWindowFocus() || isAutofillUIShowing(view);
    }

    class H extends Handler {
        H(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            InputConnection inputConnection;
            switch (message.what) {
                case 1:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    try {
                        InputMethodManager.this.doDump((FileDescriptor) someArgs.arg1, (PrintWriter) someArgs.arg2, (String[]) someArgs.arg3);
                        break;
                    } catch (RuntimeException e) {
                        ((PrintWriter) someArgs.arg2).println("Exception: " + e);
                    }
                    synchronized (someArgs.arg4) {
                        ((CountDownLatch) someArgs.arg4).countDown();
                        break;
                    }
                    someArgs.recycle();
                    return;
                case 2:
                    InputBindResult inputBindResult = (InputBindResult) message.obj;
                    synchronized (InputMethodManager.this.mH) {
                        if (InputMethodManager.this.mBindSequence >= 0 && InputMethodManager.this.mBindSequence == inputBindResult.sequence) {
                            InputMethodManager.this.mRequestUpdateCursorAnchorInfoMonitorMode = 0;
                            InputMethodManager.this.setInputChannelLocked(inputBindResult.channel);
                            InputMethodManager.this.mCurMethod = inputBindResult.method;
                            InputMethodManager.this.mCurId = inputBindResult.id;
                            InputMethodManager.this.mBindSequence = inputBindResult.sequence;
                            InputMethodManager.this.startInputInner(5, null, 0, 0, 0);
                            return;
                        }
                        Log.w(InputMethodManager.TAG, "Ignoring onBind: cur seq=" + InputMethodManager.this.mBindSequence + ", given seq=" + inputBindResult.sequence);
                        if (inputBindResult.channel != null && inputBindResult.channel != InputMethodManager.this.mCurChannel) {
                            inputBindResult.channel.dispose();
                        }
                        return;
                    }
                case 3:
                    int i = message.arg1;
                    int i2 = message.arg2;
                    synchronized (InputMethodManager.this.mH) {
                        if (InputMethodManager.this.mBindSequence != i) {
                            return;
                        }
                        InputMethodManager.this.clearBindingLocked();
                        if (InputMethodManager.this.mServedView != null && InputMethodManager.this.mServedView.isFocused()) {
                            InputMethodManager.this.mServedConnecting = true;
                        }
                        boolean z = InputMethodManager.this.mActive;
                        if (z) {
                            InputMethodManager.this.startInputInner(6, null, 0, 0, 0);
                            return;
                        }
                        return;
                    }
                case 4:
                    boolean z2 = message.arg1 != 0;
                    boolean z3 = message.arg2 != 0;
                    synchronized (InputMethodManager.this.mH) {
                        InputMethodManager.this.mActive = z2;
                        InputMethodManager.this.mFullscreenMode = z3;
                        if (!z2) {
                            InputMethodManager.this.mRestartOnNextWindowFocus = true;
                            try {
                                InputMethodManager.this.mIInputContext.finishComposingText();
                                break;
                            } catch (RemoteException unused) {
                            }
                        }
                        if (InputMethodManager.this.mServedView != null && InputMethodManager.canStartInput(InputMethodManager.this.mServedView) && InputMethodManager.this.checkFocusNoStartInput(InputMethodManager.this.mRestartOnNextWindowFocus)) {
                            InputMethodManager.this.startInputInner(z2 ? 7 : 8, null, 0, 0, 0);
                        }
                        break;
                    }
                    return;
                case 5:
                    InputMethodManager.this.sendInputEventAndReportResultOnMainLooper((PendingEvent) message.obj);
                    return;
                case 6:
                    InputMethodManager.this.finishedInputEvent(message.arg1, false, true);
                    return;
                case 7:
                    InputMethodManager.this.finishedInputEvent(message.arg1, false, false);
                    return;
                case 8:
                default:
                    return;
                case 9:
                    synchronized (InputMethodManager.this.mH) {
                        InputMethodManager.this.mNextUserActionNotificationSequenceNumber = message.arg1;
                        break;
                    }
                    return;
                case 10:
                    boolean z4 = message.arg1 != 0;
                    synchronized (InputMethodManager.this.mH) {
                        InputMethodManager.this.mFullscreenMode = z4;
                        inputConnection = InputMethodManager.this.mServedInputConnectionWrapper != null ? InputMethodManager.this.mServedInputConnectionWrapper.getInputConnection() : null;
                        break;
                    }
                    if (inputConnection != null) {
                        inputConnection.reportFullscreenMode(z4);
                        return;
                    }
                    return;
            }
        }
    }

    private static class ControlledInputConnectionWrapper extends IInputConnectionWrapper {
        private final InputMethodManager mParentInputMethodManager;

        public ControlledInputConnectionWrapper(Looper looper, InputConnection inputConnection, InputMethodManager inputMethodManager) {
            super(looper, inputConnection);
            this.mParentInputMethodManager = inputMethodManager;
        }

        @Override // com.android.internal.view.IInputConnectionWrapper
        public boolean isActive() {
            return this.mParentInputMethodManager.mActive && !isFinished();
        }

        void deactivate() {
            if (isFinished()) {
                return;
            }
            closeConnection();
        }

        @Override // com.android.internal.view.IInputConnectionWrapper
        protected void onUserAction() {
            this.mParentInputMethodManager.notifyUserAction();
        }

        public String toString() {
            return "ControlledInputConnectionWrapper{connection=" + getInputConnection() + " finished=" + isFinished() + " mParentInputMethodManager.mActive=" + this.mParentInputMethodManager.mActive + "}";
        }
    }

    InputMethodManager(Looper looper) throws ServiceManager.ServiceNotFoundException {
        this(IInputMethodManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.INPUT_METHOD_SERVICE)), looper);
    }

    InputMethodManager(IInputMethodManager iInputMethodManager, Looper looper) {
        this.mActive = false;
        this.mRestartOnNextWindowFocus = true;
        this.mTmpCursorRect = new Rect();
        this.mCursorRect = new Rect();
        this.mNextUserActionNotificationSequenceNumber = -1;
        this.mLastSentUserActionNotificationSequenceNumber = -1;
        this.mCursorAnchorInfo = null;
        this.mBindSequence = -1;
        this.mRequestUpdateCursorAnchorInfoMonitorMode = 0;
        this.mPendingEventPool = new Pools.SimplePool(20);
        this.mPendingEvents = new SparseArray<>(20);
        this.mClient = new IInputMethodClient.Stub() { // from class: android.view.inputmethod.InputMethodManager.1
            @Override // com.android.internal.view.IInputMethodClient
            public void setUsingInputMethod(boolean z) {
            }

            @Override // android.os.Binder
            protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = fileDescriptor;
                someArgsObtain.arg2 = printWriter;
                someArgsObtain.arg3 = strArr;
                someArgsObtain.arg4 = countDownLatch;
                InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(1, someArgsObtain));
                try {
                    if (countDownLatch.await(5L, TimeUnit.SECONDS)) {
                        return;
                    }
                    printWriter.println("Timeout waiting for dump");
                } catch (InterruptedException unused) {
                    printWriter.println("Interrupted waiting for dump");
                }
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void onBindMethod(InputBindResult inputBindResult) {
                InputMethodManager.this.mH.obtainMessage(2, inputBindResult).sendToTarget();
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void onUnbindMethod(int i, int i2) {
                InputMethodManager.this.mH.obtainMessage(3, i, i2).sendToTarget();
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void setActive(boolean z, boolean z2) {
                InputMethodManager.this.mH.obtainMessage(4, z ? 1 : 0, z2 ? 1 : 0).sendToTarget();
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void setUserActionNotificationSequenceNumber(int i) {
                InputMethodManager.this.mH.obtainMessage(9, i, 0).sendToTarget();
            }

            @Override // com.android.internal.view.IInputMethodClient
            public void reportFullscreenMode(boolean z) {
                InputMethodManager.this.mH.obtainMessage(10, z ? 1 : 0, 0).sendToTarget();
            }
        };
        this.mDummyInputConnection = new BaseInputConnection(this, false);
        this.mService = iInputMethodManager;
        this.mMainLooper = looper;
        this.mH = new H(looper);
        this.mIInputContext = new ControlledInputConnectionWrapper(looper, this.mDummyInputConnection, this);
    }

    public static InputMethodManager getInstance() {
        InputMethodManager inputMethodManager;
        synchronized (InputMethodManager.class) {
            if (sInstance == null) {
                try {
                    sInstance = new InputMethodManager(Looper.getMainLooper());
                } catch (ServiceManager.ServiceNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            }
            inputMethodManager = sInstance;
        }
        return inputMethodManager;
    }

    public static InputMethodManager peekInstance() {
        return sInstance;
    }

    public IInputMethodClient getClient() {
        return this.mClient;
    }

    public IInputContext getInputContext() {
        return this.mIInputContext;
    }

    public List<InputMethodInfo> getInputMethodList() {
        try {
            return this.mService.getInputMethodList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<InputMethodInfo> getVrInputMethodList() {
        try {
            return this.mService.getVrInputMethodList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<InputMethodInfo> getEnabledInputMethodList() {
        try {
            return this.mService.getEnabledInputMethodList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(InputMethodInfo inputMethodInfo, boolean z) {
        try {
            return this.mService.getEnabledInputMethodSubtypeList(inputMethodInfo == null ? null : inputMethodInfo.getId(), z);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void showStatusIcon(IBinder iBinder, String str, int i) {
        showStatusIconInternal(iBinder, str, i);
    }

    public void showStatusIconInternal(IBinder iBinder, String str, int i) {
        try {
            this.mService.updateStatusIcon(iBinder, str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void hideStatusIcon(IBinder iBinder) {
        hideStatusIconInternal(iBinder);
    }

    public void hideStatusIconInternal(IBinder iBinder) {
        try {
            this.mService.updateStatusIcon(iBinder, null, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setImeWindowStatus(IBinder iBinder, IBinder iBinder2, int i, int i2) {
        try {
            this.mService.setImeWindowStatus(iBinder, iBinder2, i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerSuggestionSpansForNotification(SuggestionSpan[] suggestionSpanArr) {
        try {
            this.mService.registerSuggestionSpansForNotification(suggestionSpanArr);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifySuggestionPicked(SuggestionSpan suggestionSpan, String str, int i) {
        try {
            this.mService.notifySuggestionPicked(suggestionSpan, str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isFullscreenMode() {
        boolean z;
        synchronized (this.mH) {
            z = this.mFullscreenMode;
        }
        return z;
    }

    public void reportFullscreenMode(IBinder iBinder, boolean z) {
        try {
            this.mService.reportFullscreenMode(iBinder, z);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isActive(View view) {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            z = (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null;
        }
        return z;
    }

    public boolean isActive() {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            z = (this.mServedView == null || this.mCurrentTextBoxAttribute == null) ? false : true;
        }
        return z;
    }

    public boolean isAcceptingText() {
        checkFocus();
        ControlledInputConnectionWrapper controlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
        return (controlledInputConnectionWrapper == null || controlledInputConnectionWrapper.getInputConnection() == null) ? false : true;
    }

    void clearBindingLocked() {
        clearConnectionLocked();
        setInputChannelLocked(null);
        this.mBindSequence = -1;
        this.mCurId = null;
        this.mCurMethod = null;
    }

    void setInputChannelLocked(InputChannel inputChannel) {
        if (this.mCurChannel != inputChannel) {
            if (this.mCurSender != null) {
                flushPendingEventsLocked();
                this.mCurSender.dispose();
                this.mCurSender = null;
            }
            InputChannel inputChannel2 = this.mCurChannel;
            if (inputChannel2 != null) {
                inputChannel2.dispose();
            }
            this.mCurChannel = inputChannel;
        }
    }

    void clearConnectionLocked() {
        this.mCurrentTextBoxAttribute = null;
        ControlledInputConnectionWrapper controlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
        if (controlledInputConnectionWrapper != null) {
            controlledInputConnectionWrapper.deactivate();
            this.mServedInputConnectionWrapper = null;
        }
    }

    void finishInputLocked() {
        this.mNextServedView = null;
        if (this.mServedView != null) {
            if (this.mCurrentTextBoxAttribute != null) {
                try {
                    this.mService.finishInput(this.mClient);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            this.mServedView = null;
            this.mCompletions = null;
            this.mServedConnecting = false;
            clearConnectionLocked();
        }
    }

    public void displayCompletions(View view, CompletionInfo[] completionInfoArr) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                this.mCompletions = completionInfoArr;
                if (this.mCurMethod != null) {
                    try {
                        this.mCurMethod.displayCompletions(completionInfoArr);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public void updateExtractedText(View view, int i, ExtractedText extractedText) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                if (this.mCurMethod != null) {
                    try {
                        this.mCurMethod.updateExtractedText(i, extractedText);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public boolean showSoftInput(View view, int i) {
        return showSoftInput(view, i, null);
    }

    public boolean showSoftInput(View view, int i, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) {
                return false;
            }
            try {
                return this.mService.showSoftInput(this.mClient, i, resultReceiver);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public void showSoftInputUnchecked(int i, ResultReceiver resultReceiver) {
        try {
            Log.w(TAG, "showSoftInputUnchecked() is a hidden method, which will be removed soon. If you are using android.support.v7.widget.SearchView, please update to version 26.0 or newer version.");
            this.mService.showSoftInput(this.mClient, i, resultReceiver);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hideSoftInputFromWindow(IBinder iBinder, int i) {
        return hideSoftInputFromWindow(iBinder, i, null);
    }

    public boolean hideSoftInputFromWindow(IBinder iBinder, int i, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == null || this.mServedView.getWindowToken() != iBinder) {
                return false;
            }
            try {
                return this.mService.hideSoftInput(this.mClient, i, resultReceiver);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void toggleSoftInputFromWindow(IBinder iBinder, int i, int i2) {
        synchronized (this.mH) {
            if (this.mServedView != null && this.mServedView.getWindowToken() == iBinder) {
                if (this.mCurMethod != null) {
                    try {
                        this.mCurMethod.toggleSoftInput(i, i2);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public void toggleSoftInput(int i, int i2) {
        IInputMethodSession iInputMethodSession = this.mCurMethod;
        if (iInputMethodSession != null) {
            try {
                iInputMethodSession.toggleSoftInput(i, i2);
            } catch (RemoteException unused) {
            }
        }
    }

    public void restartInput(View view) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                this.mServedConnecting = true;
                startInputInner(3, null, 0, 0, 0);
            }
        }
    }

    boolean startInputInner(final int i, IBinder iBinder, int i2, int i3, int i4) throws Throwable {
        int i5;
        ControlledInputConnectionWrapper controlledInputConnectionWrapper;
        boolean z;
        H h;
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view == null) {
                return false;
            }
            Handler handler = view.getHandler();
            if (handler == null) {
                closeCurrentInput();
                return false;
            }
            if (handler.getLooper() != Looper.myLooper()) {
                handler.post(new Runnable() { // from class: android.view.inputmethod.-$$Lambda$InputMethodManager$jNoqB3BbMToNjx3pS-WwvtHoFfg
                    @Override // java.lang.Runnable
                    public final void run() throws Throwable {
                        this.f$0.lambda$startInputInner$0$InputMethodManager(i);
                    }
                });
                return false;
            }
            EditorInfo editorInfo = new EditorInfo();
            editorInfo.packageName = view.getContext().getOpPackageName();
            editorInfo.fieldId = view.getId();
            InputConnection inputConnectionOnCreateInputConnection = view.onCreateInputConnection(editorInfo);
            H h2 = this.mH;
            synchronized (h2) {
                try {
                    try {
                        if (this.mServedView == view && this.mServedConnecting) {
                            int i6 = this.mCurrentTextBoxAttribute == null ? i2 | 256 : i2;
                            this.mCurrentTextBoxAttribute = editorInfo;
                            this.mServedConnecting = false;
                            Handler handler2 = null;
                            if (this.mServedInputConnectionWrapper != null) {
                                this.mServedInputConnectionWrapper.deactivate();
                                this.mServedInputConnectionWrapper = null;
                            }
                            if (inputConnectionOnCreateInputConnection != null) {
                                this.mCursorSelStart = editorInfo.initialSelStart;
                                this.mCursorSelEnd = editorInfo.initialSelEnd;
                                this.mCursorCandStart = -1;
                                this.mCursorCandEnd = -1;
                                this.mCursorRect.setEmpty();
                                this.mCursorAnchorInfo = null;
                                int missingMethodFlags = InputConnectionInspector.getMissingMethodFlags(inputConnectionOnCreateInputConnection);
                                if ((missingMethodFlags & 32) == 0) {
                                    handler2 = inputConnectionOnCreateInputConnection.getHandler();
                                }
                                ControlledInputConnectionWrapper controlledInputConnectionWrapper2 = new ControlledInputConnectionWrapper(handler2 != null ? handler2.getLooper() : handler.getLooper(), inputConnectionOnCreateInputConnection, this);
                                i5 = missingMethodFlags;
                                controlledInputConnectionWrapper = controlledInputConnectionWrapper2;
                            } else {
                                i5 = 0;
                                controlledInputConnectionWrapper = null;
                            }
                            this.mServedInputConnectionWrapper = controlledInputConnectionWrapper;
                            try {
                                h = h2;
                                try {
                                    InputBindResult inputBindResultStartInputOrWindowGainedFocus = this.mService.startInputOrWindowGainedFocus(i, this.mClient, iBinder, i6, i3, i4, editorInfo, controlledInputConnectionWrapper, i5, view.getContext().getApplicationInfo().targetSdkVersion);
                                    if (inputBindResultStartInputOrWindowGainedFocus == null) {
                                        Log.wtf(TAG, "startInputOrWindowGainedFocus must not return null. startInputReason=" + InputMethodClient.getStartInputReason(i) + " editorInfo=" + editorInfo + " controlFlags=#" + Integer.toHexString(i6));
                                        return false;
                                    }
                                    if (inputBindResultStartInputOrWindowGainedFocus.id != null) {
                                        setInputChannelLocked(inputBindResultStartInputOrWindowGainedFocus.channel);
                                        this.mBindSequence = inputBindResultStartInputOrWindowGainedFocus.sequence;
                                        this.mCurMethod = inputBindResultStartInputOrWindowGainedFocus.method;
                                        this.mCurId = inputBindResultStartInputOrWindowGainedFocus.id;
                                        this.mNextUserActionNotificationSequenceNumber = inputBindResultStartInputOrWindowGainedFocus.userActionNotificationSequenceNumber;
                                    } else if (inputBindResultStartInputOrWindowGainedFocus.channel != null && inputBindResultStartInputOrWindowGainedFocus.channel != this.mCurChannel) {
                                        inputBindResultStartInputOrWindowGainedFocus.channel.dispose();
                                    }
                                    if (inputBindResultStartInputOrWindowGainedFocus.result != 11) {
                                        z = true;
                                    } else {
                                        z = true;
                                        try {
                                            this.mRestartOnNextWindowFocus = true;
                                        } catch (RemoteException e) {
                                            e = e;
                                            Log.w(TAG, "IME died: " + this.mCurId, e);
                                        }
                                    }
                                    if (this.mCurMethod != null && this.mCompletions != null) {
                                        try {
                                            this.mCurMethod.displayCompletions(this.mCompletions);
                                        } catch (RemoteException unused) {
                                        }
                                    }
                                    return z;
                                } catch (RemoteException e2) {
                                    e = e2;
                                    z = true;
                                }
                            } catch (RemoteException e3) {
                                e = e3;
                                z = true;
                                h = h2;
                            }
                        }
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
    }

    public /* synthetic */ void lambda$startInputInner$0$InputMethodManager(int i) throws Throwable {
        startInputInner(i, null, 0, 0, 0);
    }

    public void windowDismissed(IBinder iBinder) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView != null && this.mServedView.getWindowToken() == iBinder) {
                finishInputLocked();
            }
        }
    }

    public void focusIn(View view) {
        synchronized (this.mH) {
            focusInLocked(view);
        }
    }

    void focusInLocked(View view) {
        if ((view == null || !view.isTemporarilyDetached()) && this.mCurRootView == view.getRootView()) {
            this.mNextServedView = view;
            scheduleCheckFocusLocked(view);
        }
    }

    public void focusOut(View view) {
        synchronized (this.mH) {
        }
    }

    public void onViewDetachedFromWindow(View view) {
        synchronized (this.mH) {
            if (this.mServedView == view) {
                this.mNextServedView = null;
                scheduleCheckFocusLocked(view);
            }
        }
    }

    static void scheduleCheckFocusLocked(View view) {
        ViewRootImpl viewRootImpl = view.getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.dispatchCheckFocus();
        }
    }

    public void checkFocus() {
        if (checkFocusNoStartInput(false)) {
            startInputInner(4, null, 0, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkFocusNoStartInput(boolean z) {
        if (this.mServedView == this.mNextServedView && !z) {
            return false;
        }
        synchronized (this.mH) {
            if (this.mServedView == this.mNextServedView && !z) {
                return false;
            }
            if (this.mNextServedView == null) {
                finishInputLocked();
                closeCurrentInput();
                return false;
            }
            ControlledInputConnectionWrapper controlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
            this.mServedView = this.mNextServedView;
            this.mCurrentTextBoxAttribute = null;
            this.mCompletions = null;
            this.mServedConnecting = true;
            if (controlledInputConnectionWrapper != null) {
                controlledInputConnectionWrapper.finishComposingText();
            }
            return true;
        }
    }

    void closeCurrentInput() {
        try {
            this.mService.hideSoftInput(this.mClient, 2, null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onPostWindowFocus(View view, View view2, int i, boolean z, int i2) {
        int i3;
        boolean z2;
        synchronized (this.mH) {
            i3 = 1;
            if (this.mRestartOnNextWindowFocus) {
                this.mRestartOnNextWindowFocus = false;
                z2 = true;
            } else {
                z2 = false;
            }
            focusInLocked(view2 != null ? view2 : view);
        }
        if (view2 == null) {
            i3 = 0;
        } else if (view2.onCheckIsTextEditor()) {
            i3 = 3;
        }
        if (z) {
            i3 |= 4;
        }
        int i4 = i3;
        if (checkFocusNoStartInput(z2) && startInputInner(1, view.getWindowToken(), i4, i, i2)) {
            return;
        }
        synchronized (this.mH) {
            try {
                try {
                    this.mService.startInputOrWindowGainedFocus(2, this.mClient, view.getWindowToken(), i4, i, i2, null, null, 0, view.getContext().getApplicationInfo().targetSdkVersion);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void onPreWindowFocus(View view, boolean z) {
        synchronized (this.mH) {
            if (view == null) {
                try {
                    this.mCurRootView = null;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (z) {
                this.mCurRootView = view;
            } else if (view == this.mCurRootView) {
                this.mCurRootView = null;
            }
        }
    }

    public void updateSelection(View view, int i, int i2, int i3, int i4) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null && this.mCurMethod != null) {
                if (this.mCursorSelStart != i || this.mCursorSelEnd != i2 || this.mCursorCandStart != i3 || this.mCursorCandEnd != i4) {
                    try {
                        int i5 = this.mCursorSelStart;
                        int i6 = this.mCursorSelEnd;
                        this.mCursorSelStart = i;
                        this.mCursorSelEnd = i2;
                        this.mCursorCandStart = i3;
                        this.mCursorCandEnd = i4;
                        this.mCurMethod.updateSelection(i5, i6, i, i2, i3, i4);
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                }
            }
        }
    }

    public void viewClicked(View view) {
        boolean z = this.mServedView != this.mNextServedView;
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            try {
                this.mCurMethod.viewClicked(z);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public boolean isCursorAnchorInfoEnabled() {
        boolean z;
        synchronized (this.mH) {
            z = true;
            boolean z2 = (this.mRequestUpdateCursorAnchorInfoMonitorMode & 1) != 0;
            boolean z3 = (this.mRequestUpdateCursorAnchorInfoMonitorMode & 2) != 0;
            if (!z2 && !z3) {
                z = false;
            }
        }
        return z;
    }

    public void setUpdateCursorAnchorInfoMode(int i) {
        synchronized (this.mH) {
            this.mRequestUpdateCursorAnchorInfoMonitorMode = i;
        }
    }

    @Deprecated
    public void updateCursor(View view, int i, int i2, int i3, int i4) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null && this.mCurMethod != null) {
                this.mTmpCursorRect.set(i, i2, i3, i4);
                if (!this.mCursorRect.equals(this.mTmpCursorRect)) {
                    try {
                        this.mCurMethod.updateCursor(this.mTmpCursorRect);
                        this.mCursorRect.set(this.mTmpCursorRect);
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                }
            }
        }
    }

    public void updateCursorAnchorInfo(View view, CursorAnchorInfo cursorAnchorInfo) {
        if (view == null || cursorAnchorInfo == null) {
            return;
        }
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null && this.mCurMethod != null) {
                boolean z = true;
                if ((this.mRequestUpdateCursorAnchorInfoMonitorMode & 1) == 0) {
                    z = false;
                }
                if (z || !Objects.equals(this.mCursorAnchorInfo, cursorAnchorInfo)) {
                    try {
                        this.mCurMethod.updateCursorAnchorInfo(cursorAnchorInfo);
                        this.mCursorAnchorInfo = cursorAnchorInfo;
                        this.mRequestUpdateCursorAnchorInfoMonitorMode &= -2;
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                }
            }
        }
    }

    public void sendAppPrivateCommand(View view, String str, Bundle bundle) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            try {
                this.mCurMethod.appPrivateCommand(str, bundle);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    @Deprecated
    public void setInputMethod(IBinder iBinder, String str) {
        setInputMethodInternal(iBinder, str);
    }

    public void setInputMethodInternal(IBinder iBinder, String str) {
        try {
            this.mService.setInputMethod(iBinder, str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void setInputMethodAndSubtype(IBinder iBinder, String str, InputMethodSubtype inputMethodSubtype) {
        setInputMethodAndSubtypeInternal(iBinder, str, inputMethodSubtype);
    }

    public void setInputMethodAndSubtypeInternal(IBinder iBinder, String str, InputMethodSubtype inputMethodSubtype) {
        try {
            this.mService.setInputMethodAndSubtype(iBinder, str, inputMethodSubtype);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void hideSoftInputFromInputMethod(IBinder iBinder, int i) {
        hideSoftInputFromInputMethodInternal(iBinder, i);
    }

    public void hideSoftInputFromInputMethodInternal(IBinder iBinder, int i) {
        try {
            this.mService.hideMySoftInput(iBinder, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void showSoftInputFromInputMethod(IBinder iBinder, int i) {
        showSoftInputFromInputMethodInternal(iBinder, i);
    }

    public void showSoftInputFromInputMethodInternal(IBinder iBinder, int i) {
        try {
            this.mService.showMySoftInput(iBinder, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int dispatchInputEvent(InputEvent inputEvent, Object obj, FinishedInputEventCallback finishedInputEventCallback, Handler handler) {
        synchronized (this.mH) {
            if (this.mCurMethod == null) {
                return 0;
            }
            if (inputEvent instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent) inputEvent;
                if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 63 && keyEvent.getRepeatCount() == 0) {
                    showInputMethodPickerLocked();
                    return 1;
                }
            }
            PendingEvent pendingEventObtainPendingEventLocked = obtainPendingEventLocked(inputEvent, obj, this.mCurId, finishedInputEventCallback, handler);
            if (this.mMainLooper.isCurrentThread()) {
                return sendInputEventOnMainLooperLocked(pendingEventObtainPendingEventLocked);
            }
            Message messageObtainMessage = this.mH.obtainMessage(5, pendingEventObtainPendingEventLocked);
            messageObtainMessage.setAsynchronous(true);
            this.mH.sendMessage(messageObtainMessage);
            return -1;
        }
    }

    public void dispatchKeyEventFromInputMethod(View view, KeyEvent keyEvent) {
        ViewRootImpl viewRootImpl;
        synchronized (this.mH) {
            if (view != null) {
                try {
                    viewRootImpl = view.getViewRootImpl();
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                viewRootImpl = null;
            }
            if (viewRootImpl == null && this.mServedView != null) {
                viewRootImpl = this.mServedView.getViewRootImpl();
            }
            if (viewRootImpl != null) {
                viewRootImpl.dispatchKeyFromIme(keyEvent);
            }
        }
    }

    void sendInputEventAndReportResultOnMainLooper(PendingEvent pendingEvent) {
        synchronized (this.mH) {
            int iSendInputEventOnMainLooperLocked = sendInputEventOnMainLooperLocked(pendingEvent);
            if (iSendInputEventOnMainLooperLocked == -1) {
                return;
            }
            boolean z = true;
            if (iSendInputEventOnMainLooperLocked != 1) {
                z = false;
            }
            invokeFinishedInputEventCallback(pendingEvent, z);
        }
    }

    int sendInputEventOnMainLooperLocked(PendingEvent pendingEvent) {
        if (this.mCurChannel != null) {
            if (this.mCurSender == null) {
                this.mCurSender = new ImeInputEventSender(this.mCurChannel, this.mH.getLooper());
            }
            InputEvent inputEvent = pendingEvent.mEvent;
            int sequenceNumber = inputEvent.getSequenceNumber();
            if (this.mCurSender.sendInputEvent(sequenceNumber, inputEvent)) {
                this.mPendingEvents.put(sequenceNumber, pendingEvent);
                Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
                Message messageObtainMessage = this.mH.obtainMessage(6, sequenceNumber, 0, pendingEvent);
                messageObtainMessage.setAsynchronous(true);
                this.mH.sendMessageDelayed(messageObtainMessage, INPUT_METHOD_NOT_RESPONDING_TIMEOUT);
                return -1;
            }
            Log.w(TAG, "Unable to send input event to IME: " + this.mCurId + " dropping: " + inputEvent);
        }
        return 0;
    }

    void finishedInputEvent(int i, boolean z, boolean z2) {
        synchronized (this.mH) {
            int iIndexOfKey = this.mPendingEvents.indexOfKey(i);
            if (iIndexOfKey < 0) {
                return;
            }
            PendingEvent pendingEventValueAt = this.mPendingEvents.valueAt(iIndexOfKey);
            this.mPendingEvents.removeAt(iIndexOfKey);
            Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
            if (z2) {
                Log.w(TAG, "Timeout waiting for IME to handle input event after 2500 ms: " + pendingEventValueAt.mInputMethodId);
            } else {
                this.mH.removeMessages(6, pendingEventValueAt);
            }
            invokeFinishedInputEventCallback(pendingEventValueAt, z);
        }
    }

    void invokeFinishedInputEventCallback(PendingEvent pendingEvent, boolean z) {
        pendingEvent.mHandled = z;
        if (pendingEvent.mHandler.getLooper().isCurrentThread()) {
            pendingEvent.run();
            return;
        }
        Message messageObtain = Message.obtain(pendingEvent.mHandler, pendingEvent);
        messageObtain.setAsynchronous(true);
        messageObtain.sendToTarget();
    }

    private void flushPendingEventsLocked() {
        this.mH.removeMessages(7);
        int size = this.mPendingEvents.size();
        for (int i = 0; i < size; i++) {
            Message messageObtainMessage = this.mH.obtainMessage(7, this.mPendingEvents.keyAt(i), 0);
            messageObtainMessage.setAsynchronous(true);
            messageObtainMessage.sendToTarget();
        }
    }

    private PendingEvent obtainPendingEventLocked(InputEvent inputEvent, Object obj, String str, FinishedInputEventCallback finishedInputEventCallback, Handler handler) {
        PendingEvent pendingEventAcquire = this.mPendingEventPool.acquire();
        if (pendingEventAcquire == null) {
            pendingEventAcquire = new PendingEvent();
        }
        pendingEventAcquire.mEvent = inputEvent;
        pendingEventAcquire.mToken = obj;
        pendingEventAcquire.mInputMethodId = str;
        pendingEventAcquire.mCallback = finishedInputEventCallback;
        pendingEventAcquire.mHandler = handler;
        return pendingEventAcquire;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recyclePendingEventLocked(PendingEvent pendingEvent) {
        pendingEvent.recycle();
        this.mPendingEventPool.release(pendingEvent);
    }

    public void showInputMethodPicker() {
        synchronized (this.mH) {
            showInputMethodPickerLocked();
        }
    }

    public void showInputMethodPicker(boolean z) {
        synchronized (this.mH) {
            try {
                try {
                    this.mService.showInputMethodPickerFromClient(this.mClient, z ? 1 : 2);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void showInputMethodPickerLocked() {
        try {
            this.mService.showInputMethodPickerFromClient(this.mClient, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isInputMethodPickerShown() {
        try {
            return this.mService.isInputMethodPickerShownForTest();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void showInputMethodAndSubtypeEnabler(String str) {
        synchronized (this.mH) {
            try {
                try {
                    this.mService.showInputMethodAndSubtypeEnablerFromClient(this.mClient, str);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public InputMethodSubtype getCurrentInputMethodSubtype() {
        try {
            return this.mService.getCurrentInputMethodSubtype();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setCurrentInputMethodSubtype(InputMethodSubtype inputMethodSubtype) {
        boolean currentInputMethodSubtype;
        synchronized (this.mH) {
            try {
                try {
                    currentInputMethodSubtype = this.mService.setCurrentInputMethodSubtype(inputMethodSubtype);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return currentInputMethodSubtype;
    }

    public void notifyUserAction() {
        synchronized (this.mH) {
            if (this.mLastSentUserActionNotificationSequenceNumber == this.mNextUserActionNotificationSequenceNumber) {
                return;
            }
            try {
                this.mService.notifyUserAction(this.mNextUserActionNotificationSequenceNumber);
                this.mLastSentUserActionNotificationSequenceNumber = this.mNextUserActionNotificationSequenceNumber;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public Map<InputMethodInfo, List<InputMethodSubtype>> getShortcutInputMethodsAndSubtypes() {
        HashMap map;
        synchronized (this.mH) {
            try {
                map = new HashMap();
                try {
                    List shortcutInputMethodsAndSubtypes = this.mService.getShortcutInputMethodsAndSubtypes();
                    ArrayList arrayList = null;
                    if (shortcutInputMethodsAndSubtypes != null && !shortcutInputMethodsAndSubtypes.isEmpty()) {
                        int size = shortcutInputMethodsAndSubtypes.size();
                        for (int i = 0; i < size; i++) {
                            Object obj = shortcutInputMethodsAndSubtypes.get(i);
                            if (obj instanceof InputMethodInfo) {
                                if (map.containsKey(obj)) {
                                    Log.e(TAG, "IMI list already contains the same InputMethod.");
                                    break;
                                }
                                arrayList = new ArrayList();
                                map.put((InputMethodInfo) obj, arrayList);
                            } else if (arrayList != null && (obj instanceof InputMethodSubtype)) {
                                arrayList.add((InputMethodSubtype) obj);
                            }
                        }
                    }
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return map;
    }

    public int getInputMethodWindowVisibleHeight() {
        int inputMethodWindowVisibleHeight;
        synchronized (this.mH) {
            try {
                try {
                    inputMethodWindowVisibleHeight = this.mService.getInputMethodWindowVisibleHeight();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return inputMethodWindowVisibleHeight;
    }

    public void clearLastInputMethodWindowForTransition(IBinder iBinder) {
        synchronized (this.mH) {
            try {
                try {
                    this.mService.clearLastInputMethodWindowForTransition(iBinder);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Deprecated
    public boolean switchToLastInputMethod(IBinder iBinder) {
        return switchToPreviousInputMethodInternal(iBinder);
    }

    public boolean switchToPreviousInputMethodInternal(IBinder iBinder) {
        boolean zSwitchToPreviousInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zSwitchToPreviousInputMethod = this.mService.switchToPreviousInputMethod(iBinder);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zSwitchToPreviousInputMethod;
    }

    @Deprecated
    public boolean switchToNextInputMethod(IBinder iBinder, boolean z) {
        return switchToNextInputMethodInternal(iBinder, z);
    }

    public boolean switchToNextInputMethodInternal(IBinder iBinder, boolean z) {
        boolean zSwitchToNextInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zSwitchToNextInputMethod = this.mService.switchToNextInputMethod(iBinder, z);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zSwitchToNextInputMethod;
    }

    @Deprecated
    public boolean shouldOfferSwitchingToNextInputMethod(IBinder iBinder) {
        return shouldOfferSwitchingToNextInputMethodInternal(iBinder);
    }

    public boolean shouldOfferSwitchingToNextInputMethodInternal(IBinder iBinder) {
        boolean zShouldOfferSwitchingToNextInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zShouldOfferSwitchingToNextInputMethod = this.mService.shouldOfferSwitchingToNextInputMethod(iBinder);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zShouldOfferSwitchingToNextInputMethod;
    }

    public void setAdditionalInputMethodSubtypes(String str, InputMethodSubtype[] inputMethodSubtypeArr) {
        synchronized (this.mH) {
            try {
                try {
                    this.mService.setAdditionalInputMethodSubtypes(str, inputMethodSubtypeArr);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public InputMethodSubtype getLastInputMethodSubtype() {
        InputMethodSubtype lastInputMethodSubtype;
        synchronized (this.mH) {
            try {
                try {
                    lastInputMethodSubtype = this.mService.getLastInputMethodSubtype();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return lastInputMethodSubtype;
    }

    public void exposeContent(IBinder iBinder, InputContentInfo inputContentInfo, EditorInfo editorInfo) {
        Uri contentUri = inputContentInfo.getContentUri();
        try {
            IInputContentUriToken iInputContentUriTokenCreateInputContentUriToken = this.mService.createInputContentUriToken(iBinder, contentUri, editorInfo.packageName);
            if (iInputContentUriTokenCreateInputContentUriToken == null) {
                return;
            }
            inputContentInfo.setUriToken(iInputContentUriTokenCreateInputContentUriToken);
        } catch (RemoteException e) {
            Log.e(TAG, "createInputContentAccessToken failed. contentUri=" + contentUri.toString() + " packageName=" + editorInfo.packageName, e);
        }
    }

    void doDump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        printWriterPrinter.println("Input method client state for " + this + SettingsStringUtil.DELIMITER);
        StringBuilder sb = new StringBuilder();
        sb.append("  mService=");
        sb.append(this.mService);
        printWriterPrinter.println(sb.toString());
        printWriterPrinter.println("  mMainLooper=" + this.mMainLooper);
        printWriterPrinter.println("  mIInputContext=" + this.mIInputContext);
        printWriterPrinter.println("  mActive=" + this.mActive + " mRestartOnNextWindowFocus=" + this.mRestartOnNextWindowFocus + " mBindSequence=" + this.mBindSequence + " mCurId=" + this.mCurId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mFullscreenMode=");
        sb2.append(this.mFullscreenMode);
        printWriterPrinter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mCurMethod=");
        sb3.append(this.mCurMethod);
        printWriterPrinter.println(sb3.toString());
        printWriterPrinter.println("  mCurRootView=" + this.mCurRootView);
        printWriterPrinter.println("  mServedView=" + this.mServedView);
        printWriterPrinter.println("  mNextServedView=" + this.mNextServedView);
        printWriterPrinter.println("  mServedConnecting=" + this.mServedConnecting);
        if (this.mCurrentTextBoxAttribute != null) {
            printWriterPrinter.println("  mCurrentTextBoxAttribute:");
            this.mCurrentTextBoxAttribute.dump(printWriterPrinter, "    ");
        } else {
            printWriterPrinter.println("  mCurrentTextBoxAttribute: null");
        }
        printWriterPrinter.println("  mServedInputConnectionWrapper=" + this.mServedInputConnectionWrapper);
        printWriterPrinter.println("  mCompletions=" + Arrays.toString(this.mCompletions));
        printWriterPrinter.println("  mCursorRect=" + this.mCursorRect);
        printWriterPrinter.println("  mCursorSelStart=" + this.mCursorSelStart + " mCursorSelEnd=" + this.mCursorSelEnd + " mCursorCandStart=" + this.mCursorCandStart + " mCursorCandEnd=" + this.mCursorCandEnd);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mNextUserActionNotificationSequenceNumber=");
        sb4.append(this.mNextUserActionNotificationSequenceNumber);
        sb4.append(" mLastSentUserActionNotificationSequenceNumber=");
        sb4.append(this.mLastSentUserActionNotificationSequenceNumber);
        printWriterPrinter.println(sb4.toString());
    }

    private final class ImeInputEventSender extends InputEventSender {
        public ImeInputEventSender(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventSender
        public void onInputEventFinished(int i, boolean z) {
            InputMethodManager.this.finishedInputEvent(i, z, false);
        }
    }

    private final class PendingEvent implements Runnable {
        public FinishedInputEventCallback mCallback;
        public InputEvent mEvent;
        public boolean mHandled;
        public Handler mHandler;
        public String mInputMethodId;
        public Object mToken;

        private PendingEvent() {
        }

        public void recycle() {
            this.mEvent = null;
            this.mToken = null;
            this.mInputMethodId = null;
            this.mCallback = null;
            this.mHandler = null;
            this.mHandled = false;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mCallback.onFinishedInputEvent(this.mToken, this.mHandled);
            synchronized (InputMethodManager.this.mH) {
                InputMethodManager.this.recyclePendingEventLocked(this);
            }
        }
    }

    private static String dumpViewInfo(View view) {
        if (view == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(view);
        sb.append(",focus=" + view.hasFocus());
        sb.append(",windowFocus=" + view.hasWindowFocus());
        sb.append(",autofillUiShowing=" + isAutofillUIShowing(view));
        sb.append(",window=" + view.getWindowToken());
        sb.append(",temporaryDetach=" + view.isTemporarilyDetached());
        return sb.toString();
    }
}
