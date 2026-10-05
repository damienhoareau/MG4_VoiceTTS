package android.os.strictmode;

/* JADX INFO: loaded from: classes2.dex */
public final class LeakedClosableViolation extends Violation {
    public LeakedClosableViolation(String str, Throwable th) {
        super(str);
        initCause(th);
    }
}
