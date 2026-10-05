package com.android.internal.view;

import android.inputmethodservice.AbstractInputMethodService;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionInspector;
import android.view.inputmethod.InputContentInfo;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes3.dex */
public class InputConnectionWrapper implements InputConnection {
    private static final int MAX_WAIT_TIME_MILLIS = 2000;
    private final IInputContext mIInputContext;
    private final WeakReference<AbstractInputMethodService> mInputMethodService;
    private final AtomicBoolean mIsUnbindIssued;
    private final int mMissingMethods;

    @Override // android.view.inputmethod.InputConnection
    public void closeConnection() {
    }

    @Override // android.view.inputmethod.InputConnection
    public Handler getHandler() {
        return null;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean reportFullscreenMode(boolean z) {
        return false;
    }

    static class InputContextCallback extends IInputContextCallback.Stub {
        private static final String TAG = "InputConnectionWrapper.ICC";
        private static InputContextCallback sInstance = new InputContextCallback();
        private static int sSequenceNumber = 1;
        public boolean mCommitContentResult;
        public int mCursorCapsMode;
        public ExtractedText mExtractedText;
        public boolean mHaveValue;
        public boolean mRequestUpdateCursorAnchorInfoResult;
        public CharSequence mSelectedText;
        public int mSeq;
        public CharSequence mTextAfterCursor;
        public CharSequence mTextBeforeCursor;

        InputContextCallback() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static InputContextCallback getInstance() {
            InputContextCallback inputContextCallback;
            synchronized (InputContextCallback.class) {
                if (sInstance != null) {
                    inputContextCallback = sInstance;
                    sInstance = null;
                    inputContextCallback.mHaveValue = false;
                } else {
                    inputContextCallback = new InputContextCallback();
                }
                int i = sSequenceNumber;
                sSequenceNumber = i + 1;
                inputContextCallback.mSeq = i;
            }
            return inputContextCallback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispose() {
            synchronized (InputContextCallback.class) {
                if (sInstance == null) {
                    this.mTextAfterCursor = null;
                    this.mTextBeforeCursor = null;
                    this.mExtractedText = null;
                    sInstance = this;
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setTextBeforeCursor(CharSequence charSequence, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mTextBeforeCursor = charSequence;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setTextBeforeCursor, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setTextAfterCursor(CharSequence charSequence, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mTextAfterCursor = charSequence;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setTextAfterCursor, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setSelectedText(CharSequence charSequence, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mSelectedText = charSequence;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setSelectedText, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setCursorCapsMode(int i, int i2) {
            synchronized (this) {
                if (i2 == this.mSeq) {
                    this.mCursorCapsMode = i;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i2 + " (expected " + this.mSeq + ") in setCursorCapsMode, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setExtractedText(ExtractedText extractedText, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mExtractedText = extractedText;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setExtractedText, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setRequestUpdateCursorAnchorInfoResult(boolean z, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mRequestUpdateCursorAnchorInfoResult = z;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setCursorAnchorInfoRequestResult, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setCommitContentResult(boolean z, int i) {
            synchronized (this) {
                if (i == this.mSeq) {
                    this.mCommitContentResult = z;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + i + " (expected " + this.mSeq + ") in setCommitContentResult, ignoring.");
                }
            }
        }

        void waitForResultLocked() {
            long jUptimeMillis = SystemClock.uptimeMillis() + 2000;
            while (!this.mHaveValue) {
                long jUptimeMillis2 = jUptimeMillis - SystemClock.uptimeMillis();
                if (jUptimeMillis2 <= 0) {
                    Log.w(TAG, "Timed out waiting on IInputContextCallback");
                    return;
                }
                try {
                    wait(jUptimeMillis2);
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    public InputConnectionWrapper(WeakReference<AbstractInputMethodService> weakReference, IInputContext iInputContext, int i, AtomicBoolean atomicBoolean) {
        this.mInputMethodService = weakReference;
        this.mIInputContext = iInputContext;
        this.mMissingMethods = i;
        this.mIsUnbindIssued = atomicBoolean;
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextAfterCursor(int i, int i2) {
        CharSequence charSequence;
        if (this.mIsUnbindIssued.get()) {
            return null;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.getTextAfterCursor(i, i2, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                charSequence = inputContextCallback.mHaveValue ? inputContextCallback.mTextAfterCursor : null;
            }
            inputContextCallback.dispose();
            return charSequence;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextBeforeCursor(int i, int i2) {
        CharSequence charSequence;
        if (this.mIsUnbindIssued.get()) {
            return null;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.getTextBeforeCursor(i, i2, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                charSequence = inputContextCallback.mHaveValue ? inputContextCallback.mTextBeforeCursor : null;
            }
            inputContextCallback.dispose();
            return charSequence;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getSelectedText(int i) {
        CharSequence charSequence;
        if (this.mIsUnbindIssued.get() || isMethodMissing(1)) {
            return null;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.getSelectedText(i, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                charSequence = inputContextCallback.mHaveValue ? inputContextCallback.mSelectedText : null;
            }
            inputContextCallback.dispose();
            return charSequence;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public int getCursorCapsMode(int i) {
        int i2;
        if (this.mIsUnbindIssued.get()) {
            return 0;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.getCursorCapsMode(i, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                i2 = inputContextCallback.mHaveValue ? inputContextCallback.mCursorCapsMode : 0;
            }
            inputContextCallback.dispose();
            return i2;
        } catch (RemoteException unused) {
            return 0;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
        ExtractedText extractedText;
        if (this.mIsUnbindIssued.get()) {
            return null;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.getExtractedText(extractedTextRequest, i, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                extractedText = inputContextCallback.mHaveValue ? inputContextCallback.mExtractedText : null;
            }
            inputContextCallback.dispose();
            return extractedText;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence charSequence, int i) {
        try {
            this.mIInputContext.commitText(charSequence, i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo completionInfo) {
        if (isMethodMissing(4)) {
            return false;
        }
        try {
            this.mIInputContext.commitCompletion(completionInfo);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        try {
            this.mIInputContext.commitCorrection(correctionInfo);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setSelection(int i, int i2) {
        try {
            this.mIInputContext.setSelection(i, i2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performEditorAction(int i) {
        try {
            this.mIInputContext.performEditorAction(i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int i) {
        try {
            this.mIInputContext.performContextMenuAction(i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int i, int i2) {
        if (isMethodMissing(2)) {
            return false;
        }
        try {
            this.mIInputContext.setComposingRegion(i, i2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence charSequence, int i) {
        try {
            this.mIInputContext.setComposingText(charSequence, i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        try {
            this.mIInputContext.finishComposingText();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        try {
            this.mIInputContext.beginBatchEdit();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        try {
            this.mIInputContext.endBatchEdit();
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent keyEvent) {
        try {
            this.mIInputContext.sendKeyEvent(keyEvent);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean clearMetaKeyStates(int i) {
        try {
            this.mIInputContext.clearMetaKeyStates(i);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int i, int i2) {
        try {
            this.mIInputContext.deleteSurroundingText(i, i2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean deleteSurroundingTextInCodePoints(int i, int i2) {
        if (isMethodMissing(16)) {
            return false;
        }
        try {
            this.mIInputContext.deleteSurroundingTextInCodePoints(i, i2);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performPrivateCommand(String str, Bundle bundle) {
        try {
            this.mIInputContext.performPrivateCommand(str, bundle);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int i) {
        boolean z;
        if (this.mIsUnbindIssued.get() || isMethodMissing(8)) {
            return false;
        }
        try {
            InputContextCallback inputContextCallback = InputContextCallback.getInstance();
            this.mIInputContext.requestUpdateCursorAnchorInfo(i, inputContextCallback.mSeq, inputContextCallback);
            synchronized (inputContextCallback) {
                inputContextCallback.waitForResultLocked();
                z = inputContextCallback.mHaveValue ? inputContextCallback.mRequestUpdateCursorAnchorInfoResult : false;
            }
            inputContextCallback.dispose();
            return z;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitContent(InputContentInfo inputContentInfo, int i, Bundle bundle) {
        boolean z;
        if (this.mIsUnbindIssued.get() || isMethodMissing(128)) {
            return false;
        }
        if ((i & 1) != 0) {
            try {
                AbstractInputMethodService abstractInputMethodService = this.mInputMethodService.get();
                if (abstractInputMethodService == null) {
                    return false;
                }
                abstractInputMethodService.exposeContent(inputContentInfo, this);
            } catch (RemoteException unused) {
                return false;
            }
        }
        InputContextCallback inputContextCallback = InputContextCallback.getInstance();
        this.mIInputContext.commitContent(inputContentInfo, i, bundle, inputContextCallback.mSeq, inputContextCallback);
        synchronized (inputContextCallback) {
            inputContextCallback.waitForResultLocked();
            z = inputContextCallback.mHaveValue ? inputContextCallback.mCommitContentResult : false;
        }
        inputContextCallback.dispose();
        return z;
    }

    private boolean isMethodMissing(int i) {
        return (this.mMissingMethods & i) == i;
    }

    public String toString() {
        return "InputConnectionWrapper{idHash=#" + Integer.toHexString(System.identityHashCode(this)) + " mMissingMethods=" + InputConnectionInspector.getMissingMethodFlagsAsString(this.mMissingMethods) + "}";
    }
}
