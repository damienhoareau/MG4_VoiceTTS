package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class OperationCanceledException extends LowpanException {
    public OperationCanceledException() {
    }

    public OperationCanceledException(String str) {
        super(str);
    }

    public OperationCanceledException(String str, Throwable th) {
        super(str, th);
    }

    protected OperationCanceledException(Exception exc) {
        super(exc);
    }
}
