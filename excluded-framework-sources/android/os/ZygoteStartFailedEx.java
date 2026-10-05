package android.os;

/* JADX INFO: compiled from: ZygoteProcess.java */
/* JADX INFO: loaded from: classes2.dex */
class ZygoteStartFailedEx extends Exception {
    ZygoteStartFailedEx(String str) {
        super(str);
    }

    ZygoteStartFailedEx(Throwable th) {
        super(th);
    }

    ZygoteStartFailedEx(String str, Throwable th) {
        super(str, th);
    }
}
