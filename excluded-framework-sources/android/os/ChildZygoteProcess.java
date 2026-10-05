package android.os;

import android.net.LocalSocketAddress;

/* JADX INFO: loaded from: classes2.dex */
public class ChildZygoteProcess extends ZygoteProcess {
    private final int mPid;

    ChildZygoteProcess(LocalSocketAddress localSocketAddress, int i) {
        super(localSocketAddress, (LocalSocketAddress) null);
        this.mPid = i;
    }

    public int getPid() {
        return this.mPid;
    }
}
