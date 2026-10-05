package android.os.strictmode;

/* JADX INFO: loaded from: classes2.dex */
public final class SqliteObjectLeakedViolation extends Violation {
    public SqliteObjectLeakedViolation(String str, Throwable th) {
        super(str);
        initCause(th);
    }
}
