package android.inputmethodservice;

import android.Manifest;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.InputChannel;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethod;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.InputConnectionWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
class IInputMethodWrapper extends IInputMethod.Stub implements HandlerCaller.Callback {
    private static final int DO_ATTACH_TOKEN = 10;
    private static final int DO_CHANGE_INPUTMETHOD_SUBTYPE = 80;
    private static final int DO_CREATE_SESSION = 40;
    private static final int DO_DUMP = 1;
    private static final int DO_HIDE_SOFT_INPUT = 70;
    private static final int DO_REVOKE_SESSION = 50;
    private static final int DO_SET_INPUT_CONTEXT = 20;
    private static final int DO_SET_SESSION_ENABLED = 45;
    private static final int DO_SHOW_SOFT_INPUT = 60;
    private static final int DO_START_INPUT = 32;
    private static final int DO_UNSET_INPUT_CONTEXT = 30;
    private static final String TAG = "InputMethodWrapper";
    final HandlerCaller mCaller;
    final Context mContext;
    final WeakReference<InputMethod> mInputMethod;
    AtomicBoolean mIsUnbindIssued = null;
    final WeakReference<AbstractInputMethodService> mTarget;
    final int mTargetSdkVersion;

    static final class InputMethodSessionCallbackWrapper implements InputMethod.SessionCallback {
        final IInputSessionCallback mCb;
        final InputChannel mChannel;
        final Context mContext;

        InputMethodSessionCallbackWrapper(Context context, InputChannel inputChannel, IInputSessionCallback iInputSessionCallback) {
            this.mContext = context;
            this.mChannel = inputChannel;
            this.mCb = iInputSessionCallback;
        }

        @Override // android.view.inputmethod.InputMethod.SessionCallback
        public void sessionCreated(InputMethodSession inputMethodSession) {
            try {
                if (inputMethodSession != null) {
                    this.mCb.sessionCreated(new IInputMethodSessionWrapper(this.mContext, inputMethodSession, this.mChannel));
                } else {
                    if (this.mChannel != null) {
                        this.mChannel.dispose();
                    }
                    this.mCb.sessionCreated(null);
                }
            } catch (RemoteException unused) {
            }
        }
    }

    public IInputMethodWrapper(AbstractInputMethodService abstractInputMethodService, InputMethod inputMethod) {
        this.mTarget = new WeakReference<>(abstractInputMethodService);
        this.mContext = abstractInputMethodService.getApplicationContext();
        this.mCaller = new HandlerCaller(this.mContext, null, this, true);
        this.mInputMethod = new WeakReference<>(inputMethod);
        this.mTargetSdkVersion = abstractInputMethodService.getApplicationInfo().targetSdkVersion;
    }

    @Override // com.android.internal.os.HandlerCaller.Callback
    public void executeMessage(Message message) {
        InputMethod inputMethod = this.mInputMethod.get();
        if (inputMethod == null && message.what != 1) {
            Log.w(TAG, "Input method reference was null, ignoring message: " + message.what);
            return;
        }
        switch (message.what) {
            case 1:
                AbstractInputMethodService abstractInputMethodService = this.mTarget.get();
                if (abstractInputMethodService == null) {
                    return;
                }
                SomeArgs someArgs = (SomeArgs) message.obj;
                try {
                    abstractInputMethodService.dump((FileDescriptor) someArgs.arg1, (PrintWriter) someArgs.arg2, (String[]) someArgs.arg3);
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
            case 10:
                inputMethod.initializeInternal((IBinder) message.obj, message.arg1);
                return;
            case 20:
                inputMethod.bindInput((InputBinding) message.obj);
                return;
            case 30:
                inputMethod.unbindInput();
                return;
            case 32:
                SomeArgs someArgs2 = (SomeArgs) message.obj;
                int i = message.arg1;
                boolean z = message.arg2 != 0;
                IBinder iBinder = (IBinder) someArgs2.arg1;
                IInputContext iInputContext = (IInputContext) someArgs2.arg2;
                EditorInfo editorInfo = (EditorInfo) someArgs2.arg3;
                InputConnectionWrapper inputConnectionWrapper = iInputContext != null ? new InputConnectionWrapper(this.mTarget, iInputContext, i, (AtomicBoolean) someArgs2.arg4) : null;
                editorInfo.makeCompatible(this.mTargetSdkVersion);
                inputMethod.dispatchStartInputWithToken(inputConnectionWrapper, editorInfo, z, iBinder);
                someArgs2.recycle();
                return;
            case 40:
                SomeArgs someArgs3 = (SomeArgs) message.obj;
                inputMethod.createSession(new InputMethodSessionCallbackWrapper(this.mContext, (InputChannel) someArgs3.arg1, (IInputSessionCallback) someArgs3.arg2));
                someArgs3.recycle();
                return;
            case 45:
                inputMethod.setSessionEnabled((InputMethodSession) message.obj, message.arg1 != 0);
                return;
            case 50:
                inputMethod.revokeSession((InputMethodSession) message.obj);
                return;
            case 60:
                inputMethod.showSoftInput(message.arg1, (ResultReceiver) message.obj);
                return;
            case 70:
                inputMethod.hideSoftInput(message.arg1, (ResultReceiver) message.obj);
                return;
            case 80:
                inputMethod.changeInputMethodSubtype((InputMethodSubtype) message.obj);
                return;
            default:
                Log.w(TAG, "Unhandled message code: " + message.what);
                return;
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AbstractInputMethodService abstractInputMethodService = this.mTarget.get();
        if (abstractInputMethodService == null) {
            return;
        }
        if (abstractInputMethodService.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            printWriter.println("Permission Denial: can't dump InputMethodManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOOOO(1, fileDescriptor, printWriter, strArr, countDownLatch));
        try {
            if (countDownLatch.await(5L, TimeUnit.SECONDS)) {
                return;
            }
            printWriter.println("Timeout waiting for dump");
        } catch (InterruptedException unused) {
            printWriter.println("Interrupted waiting for dump");
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void initializeInternal(IBinder iBinder, int i) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIO(10, i, iBinder));
    }

    @Override // com.android.internal.view.IInputMethod
    public void bindInput(InputBinding inputBinding) {
        if (this.mIsUnbindIssued != null) {
            Log.e(TAG, "bindInput must be paired with unbindInput.");
        }
        this.mIsUnbindIssued = new AtomicBoolean();
        InputBinding inputBinding2 = new InputBinding(new InputConnectionWrapper(this.mTarget, IInputContext.Stub.asInterface(inputBinding.getConnectionToken()), 0, this.mIsUnbindIssued), inputBinding);
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(20, inputBinding2));
    }

    @Override // com.android.internal.view.IInputMethod
    public void unbindInput() {
        AtomicBoolean atomicBoolean = this.mIsUnbindIssued;
        if (atomicBoolean != null) {
            atomicBoolean.set(true);
            this.mIsUnbindIssued = null;
        } else {
            Log.e(TAG, "unbindInput must be paired with bindInput.");
        }
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(30));
    }

    @Override // com.android.internal.view.IInputMethod
    public void startInput(IBinder iBinder, IInputContext iInputContext, int i, EditorInfo editorInfo, boolean z) {
        if (this.mIsUnbindIssued == null) {
            Log.e(TAG, "startInput must be called after bindInput.");
            this.mIsUnbindIssued = new AtomicBoolean();
        }
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIIOOOO(32, i, z ? 1 : 0, iBinder, iInputContext, editorInfo, this.mIsUnbindIssued));
    }

    @Override // com.android.internal.view.IInputMethod
    public void createSession(InputChannel inputChannel, IInputSessionCallback iInputSessionCallback) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(40, inputChannel, iInputSessionCallback));
    }

    @Override // com.android.internal.view.IInputMethod
    public void setSessionEnabled(IInputMethodSession iInputMethodSession, boolean z) {
        try {
            InputMethodSession internalInputMethodSession = ((IInputMethodSessionWrapper) iInputMethodSession).getInternalInputMethodSession();
            if (internalInputMethodSession == null) {
                Log.w(TAG, "Session is already finished: " + iInputMethodSession);
                return;
            }
            this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(45, z ? 1 : 0, internalInputMethodSession));
        } catch (ClassCastException e) {
            Log.w(TAG, "Incoming session not of correct type: " + iInputMethodSession, e);
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void revokeSession(IInputMethodSession iInputMethodSession) {
        try {
            InputMethodSession internalInputMethodSession = ((IInputMethodSessionWrapper) iInputMethodSession).getInternalInputMethodSession();
            if (internalInputMethodSession == null) {
                Log.w(TAG, "Session is already finished: " + iInputMethodSession);
                return;
            }
            this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(50, internalInputMethodSession));
        } catch (ClassCastException e) {
            Log.w(TAG, "Incoming session not of correct type: " + iInputMethodSession, e);
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void showSoftInput(int i, ResultReceiver resultReceiver) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIO(60, i, resultReceiver));
    }

    @Override // com.android.internal.view.IInputMethod
    public void hideSoftInput(int i, ResultReceiver resultReceiver) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIO(70, i, resultReceiver));
    }

    @Override // com.android.internal.view.IInputMethod
    public void changeInputMethodSubtype(InputMethodSubtype inputMethodSubtype) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(80, inputMethodSubtype));
    }
}
