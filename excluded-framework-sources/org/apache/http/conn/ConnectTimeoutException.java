package org.apache.http.conn;

import java.io.InterruptedIOException;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public class ConnectTimeoutException extends InterruptedIOException {
    private static final long serialVersionUID = -4816682903149535989L;

    public ConnectTimeoutException() {
    }

    public ConnectTimeoutException(String str) {
        super(str);
    }
}
