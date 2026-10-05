package android.util;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidRuntimeException extends RuntimeException {
    public AndroidRuntimeException() {
    }

    public AndroidRuntimeException(String str) {
        super(str);
    }

    public AndroidRuntimeException(String str, Throwable th) {
        super(str, th);
    }

    public AndroidRuntimeException(Exception exc) {
        super(exc);
    }
}
