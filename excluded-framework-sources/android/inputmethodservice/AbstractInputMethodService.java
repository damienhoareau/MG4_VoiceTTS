package android.inputmethodservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodSession;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractInputMethodService extends Service implements KeyEvent.Callback {
    final KeyEvent.DispatcherState mDispatcherState = new KeyEvent.DispatcherState();
    private InputMethod mInputMethod;

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    public void exposeContent(InputContentInfo inputContentInfo, InputConnection inputConnection) {
    }

    public abstract AbstractInputMethodImpl onCreateInputMethodInterface();

    public abstract AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface();

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return false;
    }

    public abstract class AbstractInputMethodImpl implements InputMethod {
        public AbstractInputMethodImpl() {
        }

        @Override // android.view.inputmethod.InputMethod
        public void createSession(InputMethod.SessionCallback sessionCallback) {
            sessionCallback.sessionCreated(AbstractInputMethodService.this.onCreateInputMethodSessionInterface());
        }

        @Override // android.view.inputmethod.InputMethod
        public void setSessionEnabled(InputMethodSession inputMethodSession, boolean z) {
            ((AbstractInputMethodSessionImpl) inputMethodSession).setEnabled(z);
        }

        @Override // android.view.inputmethod.InputMethod
        public void revokeSession(InputMethodSession inputMethodSession) {
            ((AbstractInputMethodSessionImpl) inputMethodSession).revokeSelf();
        }
    }

    public abstract class AbstractInputMethodSessionImpl implements InputMethodSession {
        boolean mEnabled = true;
        boolean mRevoked;

        public AbstractInputMethodSessionImpl() {
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isRevoked() {
            return this.mRevoked;
        }

        public void setEnabled(boolean z) {
            if (this.mRevoked) {
                return;
            }
            this.mEnabled = z;
        }

        public void revokeSelf() {
            this.mRevoked = true;
            this.mEnabled = false;
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchKeyEvent(int i, KeyEvent keyEvent, InputMethodSession.EventCallback eventCallback) {
            AbstractInputMethodService abstractInputMethodService = AbstractInputMethodService.this;
            boolean zDispatch = keyEvent.dispatch(abstractInputMethodService, abstractInputMethodService.mDispatcherState, this);
            if (eventCallback != null) {
                eventCallback.finishedEvent(i, zDispatch);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchTrackballEvent(int i, MotionEvent motionEvent, InputMethodSession.EventCallback eventCallback) {
            boolean zOnTrackballEvent = AbstractInputMethodService.this.onTrackballEvent(motionEvent);
            if (eventCallback != null) {
                eventCallback.finishedEvent(i, zOnTrackballEvent);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchGenericMotionEvent(int i, MotionEvent motionEvent, InputMethodSession.EventCallback eventCallback) {
            boolean zOnGenericMotionEvent = AbstractInputMethodService.this.onGenericMotionEvent(motionEvent);
            if (eventCallback != null) {
                eventCallback.finishedEvent(i, zOnGenericMotionEvent);
            }
        }
    }

    public KeyEvent.DispatcherState getKeyDispatcherState() {
        return this.mDispatcherState;
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mInputMethod == null) {
            this.mInputMethod = onCreateInputMethodInterface();
        }
        return new IInputMethodWrapper(this, this.mInputMethod);
    }
}
