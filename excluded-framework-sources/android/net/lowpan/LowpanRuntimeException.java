package android.net.lowpan;

import android.util.AndroidRuntimeException;

/* JADX INFO: loaded from: classes.dex */
public class LowpanRuntimeException extends AndroidRuntimeException {
    public LowpanRuntimeException() {
    }

    public LowpanRuntimeException(String str) {
        super(str);
    }

    public LowpanRuntimeException(String str, Throwable th) {
        super(str, th);
    }

    public LowpanRuntimeException(Exception exc) {
        super(exc);
    }
}
