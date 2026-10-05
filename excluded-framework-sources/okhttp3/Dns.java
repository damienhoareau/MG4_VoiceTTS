package okhttp3;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public interface Dns {
    public static final Dns SYSTEM = new Dns() { // from class: okhttp3.-$$Lambda$Dns$mTkNcZf2K4euny3_jks6Cac6Az0
        @Override // okhttp3.Dns
        public final List lookup(String str) {
            return Dns.lambda$static$0(str);
        }
    };

    List<InetAddress> lookup(String str) throws UnknownHostException;

    static /* synthetic */ List lambda$static$0(String str) throws UnknownHostException {
        if (str == null) {
            throw new UnknownHostException("hostname == null");
        }
        try {
            return Arrays.asList(InetAddress.getAllByName(str));
        } catch (NullPointerException e) {
            UnknownHostException unknownHostException = new UnknownHostException("Broken system behaviour for dns lookup of " + str);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
