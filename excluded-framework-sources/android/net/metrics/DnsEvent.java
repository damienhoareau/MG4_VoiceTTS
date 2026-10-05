package android.net.metrics;

import android.net.NetworkCapabilities;
import com.android.internal.util.BitUtils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class DnsEvent {
    private static final int SIZE_LIMIT = 20000;
    public int eventCount;
    public byte[] eventTypes;
    public int[] latenciesMs;
    public final int netId;
    public byte[] returnCodes;
    public int successCount;
    public final long transports;

    public DnsEvent(int i, long j, int i2) {
        this.netId = i;
        this.transports = j;
        this.eventTypes = new byte[i2];
        this.returnCodes = new byte[i2];
        this.latenciesMs = new int[i2];
    }

    boolean addResult(byte b, byte b2, int i) {
        boolean z = b2 == 0;
        int i2 = this.eventCount;
        if (i2 >= 20000) {
            return z;
        }
        if (i2 == this.eventTypes.length) {
            resize((int) (((double) i2) * 1.4d));
        }
        byte[] bArr = this.eventTypes;
        int i3 = this.eventCount;
        bArr[i3] = b;
        this.returnCodes[i3] = b2;
        this.latenciesMs[i3] = i;
        this.eventCount = i3 + 1;
        if (z) {
            this.successCount++;
        }
        return z;
    }

    public void resize(int i) {
        this.eventTypes = Arrays.copyOf(this.eventTypes, i);
        this.returnCodes = Arrays.copyOf(this.returnCodes, i);
        this.latenciesMs = Arrays.copyOf(this.latenciesMs, i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("DnsEvent(");
        sb.append("netId=");
        sb.append(this.netId);
        sb.append(", ");
        for (int i : BitUtils.unpackBits(this.transports)) {
            sb.append(NetworkCapabilities.transportNameOf(i));
            sb.append(", ");
        }
        sb.append(String.format("%d events, ", Integer.valueOf(this.eventCount)));
        sb.append(String.format("%d success)", Integer.valueOf(this.successCount)));
        return sb.toString();
    }
}
