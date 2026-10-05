package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;

/* JADX INFO: loaded from: classes3.dex */
@Deprecated
public interface LayeredSocketFactory extends SocketFactory {
    Socket createSocket(Socket socket, String str, int i, boolean z) throws IOException;
}
