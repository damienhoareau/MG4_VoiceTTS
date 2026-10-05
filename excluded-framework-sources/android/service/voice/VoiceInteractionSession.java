package android.service.voice;

import android.R;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.inputmethodservice.SoftInputWindow;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.SettingsStringUtil;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.IVoiceInteractorCallback;
import com.android.internal.app.IVoiceInteractorRequest;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceInteractionSession implements KeyEvent.Callback, ComponentCallbacks2 {
    static final boolean DEBUG = false;
    static final int MSG_CANCEL = 7;
    static final int MSG_CLOSE_SYSTEM_DIALOGS = 102;
    static final int MSG_DESTROY = 103;
    static final int MSG_HANDLE_ASSIST = 104;
    static final int MSG_HANDLE_SCREENSHOT = 105;
    static final int MSG_HIDE = 107;
    static final int MSG_ON_LOCKSCREEN_SHOWN = 108;
    static final int MSG_SHOW = 106;
    static final int MSG_START_ABORT_VOICE = 4;
    static final int MSG_START_COMMAND = 5;
    static final int MSG_START_COMPLETE_VOICE = 3;
    static final int MSG_START_CONFIRMATION = 1;
    static final int MSG_START_PICK_OPTION = 2;
    static final int MSG_SUPPORTS_COMMANDS = 6;
    static final int MSG_TASK_FINISHED = 101;
    static final int MSG_TASK_STARTED = 100;
    public static final int SHOW_SOURCE_ACTIVITY = 16;
    public static final int SHOW_SOURCE_APPLICATION = 8;
    public static final int SHOW_SOURCE_ASSIST_GESTURE = 4;
    public static final int SHOW_WITH_ASSIST = 1;
    public static final int SHOW_WITH_SCREENSHOT = 2;
    static final String TAG = "VoiceInteractionSession";
    final ArrayMap<IBinder, Request> mActiveRequests;
    final MyCallbacks mCallbacks;
    FrameLayout mContentFrame;
    final Context mContext;
    final KeyEvent.DispatcherState mDispatcherState;
    final HandlerCaller mHandlerCaller;
    boolean mInShowWindow;
    LayoutInflater mInflater;
    boolean mInitialized;
    final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer;
    final IVoiceInteractor mInteractor;
    View mRootView;
    final IVoiceInteractionSession mSession;
    IVoiceInteractionManagerService mSystemService;
    int mTheme;
    TypedArray mThemeAttrs;
    final Insets mTmpInsets;
    IBinder mToken;
    boolean mUiEnabled;
    final WeakReference<VoiceInteractionSession> mWeakRef;
    SoftInputWindow mWindow;
    boolean mWindowAdded;
    boolean mWindowVisible;
    boolean mWindowWasVisible;

    public static final class Insets {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public int touchableInsets;
        public final Rect contentInsets = new Rect();
        public final Region touchableRegion = new Region();
    }

    public void onAssistStructureFailure(Throwable th) {
    }

    public void onCancelRequest(Request request) {
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
    }

    public View onCreateContentView() {
        return null;
    }

    public void onDestroy() {
    }

    public void onHandleAssist(Bundle bundle, AssistStructure assistStructure, AssistContent assistContent) {
    }

    public void onHandleAssistSecondary(Bundle bundle, AssistStructure assistStructure, AssistContent assistContent, int i, int i2) {
    }

    public void onHandleScreenshot(Bitmap bitmap) {
    }

    public void onHide() {
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    public void onPrepareShow(Bundle bundle, int i) {
    }

    public void onRequestAbortVoice(AbortVoiceRequest abortVoiceRequest) {
    }

    public void onRequestCommand(CommandRequest commandRequest) {
    }

    public void onRequestCompleteVoice(CompleteVoiceRequest completeVoiceRequest) {
    }

    public void onRequestConfirmation(ConfirmationRequest confirmationRequest) {
    }

    public void onRequestPickOption(PickOptionRequest pickOptionRequest) {
    }

    public void onShow(Bundle bundle, int i) {
    }

    public void onTaskStarted(Intent intent, int i) {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
    }

    public static class Request {
        final IVoiceInteractorCallback mCallback;
        final String mCallingPackage;
        final int mCallingUid;
        final Bundle mExtras;
        final IVoiceInteractorRequest mInterface = new IVoiceInteractorRequest.Stub() { // from class: android.service.voice.VoiceInteractionSession.Request.1
            @Override // com.android.internal.app.IVoiceInteractorRequest
            public void cancel() throws RemoteException {
                VoiceInteractionSession voiceInteractionSession = Request.this.mSession.get();
                if (voiceInteractionSession != null) {
                    voiceInteractionSession.mHandlerCaller.sendMessage(voiceInteractionSession.mHandlerCaller.obtainMessageO(7, Request.this));
                }
            }
        };
        final WeakReference<VoiceInteractionSession> mSession;

        Request(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, Bundle bundle) {
            this.mCallingPackage = str;
            this.mCallingUid = i;
            this.mCallback = iVoiceInteractorCallback;
            this.mSession = voiceInteractionSession.mWeakRef;
            this.mExtras = bundle;
        }

        public int getCallingUid() {
            return this.mCallingUid;
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public boolean isActive() {
            VoiceInteractionSession voiceInteractionSession = this.mSession.get();
            if (voiceInteractionSession == null) {
                return false;
            }
            return voiceInteractionSession.isRequestActive(this.mInterface.asBinder());
        }

        void finishRequest() {
            VoiceInteractionSession voiceInteractionSession = this.mSession.get();
            if (voiceInteractionSession == null) {
                throw new IllegalStateException("VoiceInteractionSession has been destroyed");
            }
            Request requestRemoveRequest = voiceInteractionSession.removeRequest(this.mInterface.asBinder());
            if (requestRemoveRequest == null) {
                throw new IllegalStateException("Request not active: " + this);
            }
            if (requestRemoveRequest == this) {
                return;
            }
            throw new IllegalStateException("Current active request " + requestRemoveRequest + " not same as calling request " + this);
        }

        public void cancel() {
            try {
                finishRequest();
                this.mCallback.deliverCancel(this.mInterface);
            } catch (RemoteException unused) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            DebugUtils.buildShortClassTag(this, sb);
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            sb.append(this.mInterface.asBinder());
            sb.append(" pkg=");
            sb.append(this.mCallingPackage);
            sb.append(" uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append('}');
            return sb.toString();
        }

        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print(str);
            printWriter.print("mInterface=");
            printWriter.println(this.mInterface.asBinder());
            printWriter.print(str);
            printWriter.print("mCallingPackage=");
            printWriter.print(this.mCallingPackage);
            printWriter.print(" mCallingUid=");
            UserHandle.formatUid(printWriter, this.mCallingUid);
            printWriter.println();
            printWriter.print(str);
            printWriter.print("mCallback=");
            printWriter.println(this.mCallback.asBinder());
            if (this.mExtras != null) {
                printWriter.print(str);
                printWriter.print("mExtras=");
                printWriter.println(this.mExtras);
            }
        }
    }

    public static final class ConfirmationRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        ConfirmationRequest(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, VoiceInteractor.Prompt prompt, Bundle bundle) {
            super(str, i, iVoiceInteractorCallback, voiceInteractionSession, bundle);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getPrompt() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendConfirmationResult(boolean z, Bundle bundle) {
            try {
                finishRequest();
                this.mCallback.deliverConfirmationResult(this.mInterface, z, bundle);
            } catch (RemoteException unused) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("mPrompt=");
            printWriter.println(this.mPrompt);
        }
    }

    public static final class PickOptionRequest extends Request {
        final VoiceInteractor.PickOptionRequest.Option[] mOptions;
        final VoiceInteractor.Prompt mPrompt;

        PickOptionRequest(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            super(str, i, iVoiceInteractorCallback, voiceInteractionSession, bundle);
            this.mPrompt = prompt;
            this.mOptions = optionArr;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getPrompt() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public VoiceInteractor.PickOptionRequest.Option[] getOptions() {
            return this.mOptions;
        }

        void sendPickOptionResult(boolean z, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            if (z) {
                try {
                    finishRequest();
                } catch (RemoteException unused) {
                    return;
                }
            }
            this.mCallback.deliverPickOptionResult(this.mInterface, z, optionArr, bundle);
        }

        public void sendIntermediatePickOptionResult(VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            sendPickOptionResult(false, optionArr, bundle);
        }

        public void sendPickOptionResult(VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
            sendPickOptionResult(true, optionArr, bundle);
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("mPrompt=");
            printWriter.println(this.mPrompt);
            if (this.mOptions == null) {
                return;
            }
            printWriter.print(str);
            printWriter.println("Options:");
            int i = 0;
            while (true) {
                VoiceInteractor.PickOptionRequest.Option[] optionArr = this.mOptions;
                if (i >= optionArr.length) {
                    return;
                }
                VoiceInteractor.PickOptionRequest.Option option = optionArr[i];
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.println(SettingsStringUtil.DELIMITER);
                printWriter.print(str);
                printWriter.print("    mLabel=");
                printWriter.println(option.getLabel());
                printWriter.print(str);
                printWriter.print("    mIndex=");
                printWriter.println(option.getIndex());
                if (option.countSynonyms() > 0) {
                    printWriter.print(str);
                    printWriter.println("    Synonyms:");
                    for (int i2 = 0; i2 < option.countSynonyms(); i2++) {
                        printWriter.print(str);
                        printWriter.print("      #");
                        printWriter.print(i2);
                        printWriter.print(": ");
                        printWriter.println(option.getSynonymAt(i2));
                    }
                }
                if (option.getExtras() != null) {
                    printWriter.print(str);
                    printWriter.print("    mExtras=");
                    printWriter.println(option.getExtras());
                }
                i++;
            }
        }
    }

    public static final class CompleteVoiceRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        CompleteVoiceRequest(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, VoiceInteractor.Prompt prompt, Bundle bundle) {
            super(str, i, iVoiceInteractorCallback, voiceInteractionSession, bundle);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getMessage() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendCompleteResult(Bundle bundle) {
            try {
                finishRequest();
                this.mCallback.deliverCompleteVoiceResult(this.mInterface, bundle);
            } catch (RemoteException unused) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("mPrompt=");
            printWriter.println(this.mPrompt);
        }
    }

    public static final class AbortVoiceRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        AbortVoiceRequest(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, VoiceInteractor.Prompt prompt, Bundle bundle) {
            super(str, i, iVoiceInteractorCallback, voiceInteractionSession, bundle);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getMessage() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendAbortResult(Bundle bundle) {
            try {
                finishRequest();
                this.mCallback.deliverAbortVoiceResult(this.mInterface, bundle);
            } catch (RemoteException unused) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("mPrompt=");
            printWriter.println(this.mPrompt);
        }
    }

    public static final class CommandRequest extends Request {
        final String mCommand;

        CommandRequest(String str, int i, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractionSession voiceInteractionSession, String str2, Bundle bundle) {
            super(str, i, iVoiceInteractorCallback, voiceInteractionSession, bundle);
            this.mCommand = str2;
        }

        public String getCommand() {
            return this.mCommand;
        }

        void sendCommandResult(boolean z, Bundle bundle) {
            if (z) {
                try {
                    finishRequest();
                } catch (RemoteException unused) {
                    return;
                }
            }
            this.mCallback.deliverCommandResult(this.mInterface, z, bundle);
        }

        public void sendIntermediateResult(Bundle bundle) {
            sendCommandResult(false, bundle);
        }

        public void sendResult(Bundle bundle) {
            sendCommandResult(true, bundle);
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("mCommand=");
            printWriter.println(this.mCommand);
        }
    }

    class MyCallbacks implements HandlerCaller.Callback, SoftInputWindow.Callback {
        MyCallbacks() {
        }

        @Override // com.android.internal.os.HandlerCaller.Callback
        public void executeMessage(Message message) {
            int i = message.what;
            SomeArgs someArgs = null;
            switch (i) {
                case 1:
                    VoiceInteractionSession.this.onRequestConfirmation((ConfirmationRequest) message.obj);
                    break;
                case 2:
                    VoiceInteractionSession.this.onRequestPickOption((PickOptionRequest) message.obj);
                    break;
                case 3:
                    VoiceInteractionSession.this.onRequestCompleteVoice((CompleteVoiceRequest) message.obj);
                    break;
                case 4:
                    VoiceInteractionSession.this.onRequestAbortVoice((AbortVoiceRequest) message.obj);
                    break;
                case 5:
                    VoiceInteractionSession.this.onRequestCommand((CommandRequest) message.obj);
                    break;
                case 6:
                    SomeArgs someArgs2 = (SomeArgs) message.obj;
                    someArgs2.arg1 = VoiceInteractionSession.this.onGetSupportedCommands((String[]) someArgs2.arg1);
                    someArgs2.complete();
                    break;
                case 7:
                    VoiceInteractionSession.this.onCancelRequest((Request) message.obj);
                    break;
                default:
                    switch (i) {
                        case 100:
                            VoiceInteractionSession.this.onTaskStarted((Intent) message.obj, message.arg1);
                            break;
                        case 101:
                            VoiceInteractionSession.this.onTaskFinished((Intent) message.obj, message.arg1);
                            break;
                        case 102:
                            VoiceInteractionSession.this.onCloseSystemDialogs();
                            break;
                        case 103:
                            VoiceInteractionSession.this.doDestroy();
                            break;
                        case 104:
                            someArgs = (SomeArgs) message.obj;
                            if (someArgs.argi5 == 0) {
                                VoiceInteractionSession.this.doOnHandleAssist((Bundle) someArgs.arg1, (AssistStructure) someArgs.arg2, (Throwable) someArgs.arg3, (AssistContent) someArgs.arg4);
                            } else {
                                VoiceInteractionSession.this.doOnHandleAssistSecondary((Bundle) someArgs.arg1, (AssistStructure) someArgs.arg2, (Throwable) someArgs.arg3, (AssistContent) someArgs.arg4, someArgs.argi5, someArgs.argi6);
                            }
                            break;
                        case 105:
                            VoiceInteractionSession.this.onHandleScreenshot((Bitmap) message.obj);
                            break;
                        case 106:
                            someArgs = (SomeArgs) message.obj;
                            VoiceInteractionSession.this.doShow((Bundle) someArgs.arg1, message.arg1, (IVoiceInteractionSessionShowCallback) someArgs.arg2);
                            break;
                        case 107:
                            VoiceInteractionSession.this.doHide();
                            break;
                        case 108:
                            VoiceInteractionSession.this.onLockscreenShown();
                            break;
                    }
                    break;
            }
            if (someArgs != null) {
                someArgs.recycle();
            }
        }

        @Override // android.inputmethodservice.SoftInputWindow.Callback
        public void onBackPressed() {
            VoiceInteractionSession.this.onBackPressed();
        }
    }

    public VoiceInteractionSession(Context context) {
        this(context, new Handler());
    }

    public VoiceInteractionSession(Context context, Handler handler) {
        this.mDispatcherState = new KeyEvent.DispatcherState();
        this.mTheme = 0;
        this.mUiEnabled = true;
        this.mActiveRequests = new ArrayMap<>();
        this.mTmpInsets = new Insets();
        this.mWeakRef = new WeakReference<>(this);
        this.mInteractor = new IVoiceInteractor.Stub() { // from class: android.service.voice.VoiceInteractionSession.1
            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startConfirmation(String str, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractor.Prompt prompt, Bundle bundle) {
                ConfirmationRequest confirmationRequest = new ConfirmationRequest(str, Binder.getCallingUid(), iVoiceInteractorCallback, VoiceInteractionSession.this, prompt, bundle);
                VoiceInteractionSession.this.addRequest(confirmationRequest);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(1, confirmationRequest));
                return confirmationRequest.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startPickOption(String str, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] optionArr, Bundle bundle) {
                PickOptionRequest pickOptionRequest = new PickOptionRequest(str, Binder.getCallingUid(), iVoiceInteractorCallback, VoiceInteractionSession.this, prompt, optionArr, bundle);
                VoiceInteractionSession.this.addRequest(pickOptionRequest);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(2, pickOptionRequest));
                return pickOptionRequest.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startCompleteVoice(String str, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractor.Prompt prompt, Bundle bundle) {
                CompleteVoiceRequest completeVoiceRequest = new CompleteVoiceRequest(str, Binder.getCallingUid(), iVoiceInteractorCallback, VoiceInteractionSession.this, prompt, bundle);
                VoiceInteractionSession.this.addRequest(completeVoiceRequest);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(3, completeVoiceRequest));
                return completeVoiceRequest.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startAbortVoice(String str, IVoiceInteractorCallback iVoiceInteractorCallback, VoiceInteractor.Prompt prompt, Bundle bundle) {
                AbortVoiceRequest abortVoiceRequest = new AbortVoiceRequest(str, Binder.getCallingUid(), iVoiceInteractorCallback, VoiceInteractionSession.this, prompt, bundle);
                VoiceInteractionSession.this.addRequest(abortVoiceRequest);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(4, abortVoiceRequest));
                return abortVoiceRequest.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startCommand(String str, IVoiceInteractorCallback iVoiceInteractorCallback, String str2, Bundle bundle) {
                CommandRequest commandRequest = new CommandRequest(str, Binder.getCallingUid(), iVoiceInteractorCallback, VoiceInteractionSession.this, str2, bundle);
                VoiceInteractionSession.this.addRequest(commandRequest);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(5, commandRequest));
                return commandRequest.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public boolean[] supportsCommands(String str, String[] strArr) {
                SomeArgs someArgsSendMessageAndWait = VoiceInteractionSession.this.mHandlerCaller.sendMessageAndWait(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(6, 0, strArr, null));
                if (someArgsSendMessageAndWait != null) {
                    boolean[] zArr = (boolean[]) someArgsSendMessageAndWait.arg1;
                    someArgsSendMessageAndWait.recycle();
                    return zArr;
                }
                return new boolean[strArr.length];
            }
        };
        this.mSession = new IVoiceInteractionSession.Stub() { // from class: android.service.voice.VoiceInteractionSession.2
            @Override // android.service.voice.IVoiceInteractionSession
            public void show(Bundle bundle, int i, IVoiceInteractionSessionShowCallback iVoiceInteractionSessionShowCallback) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(106, i, bundle, iVoiceInteractionSessionShowCallback));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void hide() {
                VoiceInteractionSession.this.mHandlerCaller.removeMessages(106);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(107));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleAssist(final Bundle bundle, final AssistStructure assistStructure, final AssistContent assistContent, final int i, final int i2) {
                new Thread("AssistStructure retriever") { // from class: android.service.voice.VoiceInteractionSession.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        Throwable th;
                        AssistStructure assistStructure2 = assistStructure;
                        if (assistStructure2 != null) {
                            try {
                                assistStructure2.ensureData();
                            } catch (Throwable th2) {
                                Log.w(VoiceInteractionSession.TAG, "Failure retrieving AssistStructure", th2);
                                th = th2;
                            }
                        }
                        th = null;
                        VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageOOOOII(104, bundle, th == null ? assistStructure : null, th, assistContent, i, i2));
                    }
                }.start();
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleScreenshot(Bitmap bitmap) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(105, bitmap));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskStarted(Intent intent, int i) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(100, i, intent));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskFinished(Intent intent, int i) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(101, i, intent));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void closeSystemDialogs() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(102));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void onLockscreenShown() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(108));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void destroy() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(103));
            }
        };
        this.mCallbacks = new MyCallbacks();
        this.mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: android.service.voice.VoiceInteractionSession.3
            @Override // android.view.ViewTreeObserver.OnComputeInternalInsetsListener
            public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                VoiceInteractionSession voiceInteractionSession = VoiceInteractionSession.this;
                voiceInteractionSession.onComputeInsets(voiceInteractionSession.mTmpInsets);
                internalInsetsInfo.contentInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
                internalInsetsInfo.visibleInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
                internalInsetsInfo.touchableRegion.set(VoiceInteractionSession.this.mTmpInsets.touchableRegion);
                internalInsetsInfo.setTouchableInsets(VoiceInteractionSession.this.mTmpInsets.touchableInsets);
            }
        };
        this.mContext = context;
        this.mHandlerCaller = new HandlerCaller(context, handler.getLooper(), this.mCallbacks, true);
    }

    public Context getContext() {
        return this.mContext;
    }

    void addRequest(Request request) {
        synchronized (this) {
            this.mActiveRequests.put(request.mInterface.asBinder(), request);
        }
    }

    boolean isRequestActive(IBinder iBinder) {
        boolean zContainsKey;
        synchronized (this) {
            zContainsKey = this.mActiveRequests.containsKey(iBinder);
        }
        return zContainsKey;
    }

    Request removeRequest(IBinder iBinder) {
        Request requestRemove;
        synchronized (this) {
            requestRemove = this.mActiveRequests.remove(iBinder);
        }
        return requestRemove;
    }

    void doCreate(IVoiceInteractionManagerService iVoiceInteractionManagerService, IBinder iBinder) {
        this.mSystemService = iVoiceInteractionManagerService;
        this.mToken = iBinder;
        onCreate();
    }

    void doShow(Bundle bundle, int i, final IVoiceInteractionSessionShowCallback iVoiceInteractionSessionShowCallback) {
        if (this.mInShowWindow) {
            Log.w(TAG, "Re-entrance in to showWindow");
            return;
        }
        try {
            this.mInShowWindow = true;
            onPrepareShow(bundle, i);
            if (!this.mWindowVisible) {
                ensureWindowAdded();
            }
            onShow(bundle, i);
            if (!this.mWindowVisible) {
                this.mWindowVisible = true;
                if (this.mUiEnabled) {
                    this.mWindow.show();
                }
            }
            if (iVoiceInteractionSessionShowCallback != null) {
                if (this.mUiEnabled) {
                    this.mRootView.invalidate();
                    this.mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.service.voice.VoiceInteractionSession.4
                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            VoiceInteractionSession.this.mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                            try {
                                iVoiceInteractionSessionShowCallback.onShown();
                                return true;
                            } catch (RemoteException e) {
                                Log.w(VoiceInteractionSession.TAG, "Error calling onShown", e);
                                return true;
                            }
                        }
                    });
                } else {
                    try {
                        iVoiceInteractionSessionShowCallback.onShown();
                    } catch (RemoteException e) {
                        Log.w(TAG, "Error calling onShown", e);
                    }
                }
            }
            this.mWindowWasVisible = true;
            this.mInShowWindow = false;
        } catch (Throwable th) {
            this.mWindowWasVisible = true;
            this.mInShowWindow = false;
            throw th;
        }
    }

    void doHide() {
        if (this.mWindowVisible) {
            ensureWindowHidden();
            this.mWindowVisible = false;
            onHide();
        }
    }

    void doDestroy() {
        onDestroy();
        if (this.mInitialized) {
            this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
            if (this.mWindowAdded) {
                this.mWindow.dismiss();
                this.mWindowAdded = false;
            }
            this.mInitialized = false;
        }
    }

    void ensureWindowCreated() {
        if (this.mInitialized) {
            return;
        }
        if (!this.mUiEnabled) {
            throw new IllegalStateException("setUiEnabled is false");
        }
        this.mInitialized = true;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SoftInputWindow softInputWindow = new SoftInputWindow(this.mContext, TAG, this.mTheme, this.mCallbacks, this, this.mDispatcherState, WindowManager.LayoutParams.TYPE_VOICE_INTERACTION, 80, true);
        this.mWindow = softInputWindow;
        softInputWindow.getWindow().addFlags(16843008);
        this.mThemeAttrs = this.mContext.obtainStyledAttributes(R.styleable.VoiceInteractionSession);
        View viewInflate = this.mInflater.inflate(com.android.internal.R.layout.voice_interaction_session, (ViewGroup) null);
        this.mRootView = viewInflate;
        viewInflate.setSystemUiVisibility(1792);
        this.mWindow.setContentView(this.mRootView);
        this.mRootView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsComputer);
        this.mContentFrame = (FrameLayout) this.mRootView.findViewById(16908290);
        this.mWindow.getWindow().setLayout(-1, -1);
        this.mWindow.setToken(this.mToken);
    }

    void ensureWindowAdded() {
        if (!this.mUiEnabled || this.mWindowAdded) {
            return;
        }
        this.mWindowAdded = true;
        ensureWindowCreated();
        View viewOnCreateContentView = onCreateContentView();
        if (viewOnCreateContentView != null) {
            setContentView(viewOnCreateContentView);
        }
    }

    void ensureWindowHidden() {
        SoftInputWindow softInputWindow = this.mWindow;
        if (softInputWindow != null) {
            softInputWindow.hide();
        }
    }

    public void setDisabledShowContext(int i) {
        try {
            this.mSystemService.setDisabledShowContext(i);
        } catch (RemoteException unused) {
        }
    }

    public int getDisabledShowContext() {
        try {
            return this.mSystemService.getDisabledShowContext();
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public int getUserDisabledShowContext() {
        try {
            return this.mSystemService.getUserDisabledShowContext();
        } catch (RemoteException unused) {
            return 0;
        }
    }

    public void show(Bundle bundle, int i) {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.showSessionFromSession(iBinder, bundle, i);
        } catch (RemoteException unused) {
        }
    }

    public void hide() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.hideSessionFromSession(iBinder);
        } catch (RemoteException unused) {
        }
    }

    public void setUiEnabled(boolean z) {
        if (this.mUiEnabled != z) {
            this.mUiEnabled = z;
            if (this.mWindowVisible) {
                if (z) {
                    ensureWindowAdded();
                    this.mWindow.show();
                } else {
                    ensureWindowHidden();
                }
            }
        }
    }

    public void setTheme(int i) {
        if (this.mWindow != null) {
            throw new IllegalStateException("Must be called before onCreate()");
        }
        this.mTheme = i;
    }

    public void startVoiceActivity(Intent intent) {
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(this.mContext);
            Instrumentation.checkStartActivityResult(this.mSystemService.startVoiceActivity(this.mToken, intent, intent.resolveType(this.mContext.getContentResolver())), intent);
        } catch (RemoteException unused) {
        }
    }

    public void startAssistantActivity(Intent intent) {
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            intent.migrateExtraStreamToClipData();
            intent.prepareToLeaveProcess(this.mContext);
            Instrumentation.checkStartActivityResult(this.mSystemService.startAssistantActivity(this.mToken, intent, intent.resolveType(this.mContext.getContentResolver())), intent);
        } catch (RemoteException unused) {
        }
    }

    public void setKeepAwake(boolean z) {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.setKeepAwake(iBinder, z);
        } catch (RemoteException unused) {
        }
    }

    public void closeSystemDialogs() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.closeSystemDialogs(iBinder);
        } catch (RemoteException unused) {
        }
    }

    public LayoutInflater getLayoutInflater() {
        ensureWindowCreated();
        return this.mInflater;
    }

    public Dialog getWindow() {
        ensureWindowCreated();
        return this.mWindow;
    }

    public void finish() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.finish(iBinder);
        } catch (RemoteException unused) {
        }
    }

    public void onCreate() {
        doOnCreate();
    }

    private void doOnCreate() {
        int i = this.mTheme;
        if (i == 0) {
            i = com.android.internal.R.style.Theme_DeviceDefault_VoiceInteractionSession;
        }
        this.mTheme = i;
    }

    public void setContentView(View view) {
        ensureWindowCreated();
        this.mContentFrame.removeAllViews();
        this.mContentFrame.addView(view, new FrameLayout.LayoutParams(-1, -1));
        this.mContentFrame.requestApplyInsets();
    }

    void doOnHandleAssist(Bundle bundle, AssistStructure assistStructure, Throwable th, AssistContent assistContent) {
        if (th != null) {
            onAssistStructureFailure(th);
        }
        onHandleAssist(bundle, assistStructure, assistContent);
    }

    void doOnHandleAssistSecondary(Bundle bundle, AssistStructure assistStructure, Throwable th, AssistContent assistContent, int i, int i2) {
        if (th != null) {
            onAssistStructureFailure(th);
        }
        onHandleAssistSecondary(bundle, assistStructure, assistContent, i, i2);
    }

    public void onBackPressed() {
        hide();
    }

    public void onCloseSystemDialogs() {
        hide();
    }

    public void onLockscreenShown() {
        hide();
    }

    public void onComputeInsets(Insets insets) {
        insets.contentInsets.left = 0;
        insets.contentInsets.bottom = 0;
        insets.contentInsets.right = 0;
        View decorView = getWindow().getWindow().getDecorView();
        insets.contentInsets.top = decorView.getHeight();
        insets.touchableInsets = 0;
        insets.touchableRegion.setEmpty();
    }

    public void onTaskFinished(Intent intent, int i) {
        hide();
    }

    public boolean[] onGetSupportedCommands(String[] strArr) {
        return new boolean[strArr.length];
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mToken=");
        printWriter.println(this.mToken);
        printWriter.print(str);
        printWriter.print("mTheme=#");
        printWriter.println(Integer.toHexString(this.mTheme));
        printWriter.print(str);
        printWriter.print("mUiEnabled=");
        printWriter.println(this.mUiEnabled);
        printWriter.print(" mInitialized=");
        printWriter.println(this.mInitialized);
        printWriter.print(str);
        printWriter.print("mWindowAdded=");
        printWriter.print(this.mWindowAdded);
        printWriter.print(" mWindowVisible=");
        printWriter.println(this.mWindowVisible);
        printWriter.print(str);
        printWriter.print("mWindowWasVisible=");
        printWriter.print(this.mWindowWasVisible);
        printWriter.print(" mInShowWindow=");
        printWriter.println(this.mInShowWindow);
        if (this.mActiveRequests.size() > 0) {
            printWriter.print(str);
            printWriter.println("Active requests:");
            String str2 = str + "    ";
            for (int i = 0; i < this.mActiveRequests.size(); i++) {
                Request requestValueAt = this.mActiveRequests.valueAt(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(requestValueAt);
                requestValueAt.dump(str2, fileDescriptor, printWriter, strArr);
            }
        }
    }
}
