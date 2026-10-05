package android.net.lowpan;

/* JADX INFO: loaded from: classes.dex */
public class InterfaceDisabledException extends LowpanException {
    public InterfaceDisabledException() {
    }

    public InterfaceDisabledException(String str) {
        super(str);
    }

    public InterfaceDisabledException(String str, Throwable th) {
        super(str, th);
    }

    protected InterfaceDisabledException(Exception exc) {
        super(exc);
    }
}
