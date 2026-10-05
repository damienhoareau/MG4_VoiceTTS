package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public class StrictHostnameVerifier extends AbstractVerifier {
    public final String toString() {
        return "STRICT";
    }

    @Override // org.apache.http.conn.ssl.X509HostnameVerifier
    public final void verify(String str, String[] strArr, String[] strArr2) throws SSLException {
        verify(str, strArr, strArr2, true);
    }
}
