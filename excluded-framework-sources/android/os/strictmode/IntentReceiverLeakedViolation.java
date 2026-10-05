package android.os.strictmode;

/* JADX INFO: loaded from: classes2.dex */
public final class IntentReceiverLeakedViolation extends Violation {
    public IntentReceiverLeakedViolation(Throwable th) {
        super(null);
        setStackTrace(th.getStackTrace());
    }
}
