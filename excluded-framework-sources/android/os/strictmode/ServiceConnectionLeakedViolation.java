package android.os.strictmode;

/* JADX INFO: loaded from: classes2.dex */
public final class ServiceConnectionLeakedViolation extends Violation {
    public ServiceConnectionLeakedViolation(Throwable th) {
        super(null);
        setStackTrace(th.getStackTrace());
    }
}
