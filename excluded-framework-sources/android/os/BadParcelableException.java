package android.os;

import android.util.AndroidRuntimeException;

/* JADX INFO: loaded from: classes2.dex */
public class BadParcelableException extends AndroidRuntimeException {
    public BadParcelableException(String str) {
        super(str);
    }

    public BadParcelableException(Exception exc) {
        super(exc);
    }
}
