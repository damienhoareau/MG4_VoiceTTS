package android.view.inputmethod;

import android.content.ComponentName;

/* JADX INFO: loaded from: classes2.dex */
public interface InputMethodManagerInternal {
    void hideCurrentInputMethod();

    void setInteractive(boolean z);

    void startVrInputMethodNoCheck(ComponentName componentName);

    void switchInputMethod(boolean z);
}
