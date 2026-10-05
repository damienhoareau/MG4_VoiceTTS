package android.os.strictmode;

/* JADX INFO: loaded from: classes2.dex */
public final class WebViewMethodCalledOnWrongThreadViolation extends Violation {
    public WebViewMethodCalledOnWrongThreadViolation(Throwable th) {
        super(null);
        setStackTrace(th.getStackTrace());
    }
}
