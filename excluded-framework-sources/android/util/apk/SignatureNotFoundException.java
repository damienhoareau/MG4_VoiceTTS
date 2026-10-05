package android.util.apk;

/* JADX INFO: loaded from: classes2.dex */
public class SignatureNotFoundException extends Exception {
    private static final long serialVersionUID = 1;

    public SignatureNotFoundException(String str) {
        super(str);
    }

    public SignatureNotFoundException(String str, Throwable th) {
        super(str, th);
    }
}
