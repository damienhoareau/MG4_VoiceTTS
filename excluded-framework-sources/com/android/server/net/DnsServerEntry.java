package com.android.server.net;

import java.net.InetAddress;

/* JADX INFO: compiled from: NetlinkTracker.java */
/* JADX INFO: loaded from: classes3.dex */
class DnsServerEntry implements Comparable<DnsServerEntry> {
    public final InetAddress address;
    public long expiry;

    public DnsServerEntry(InetAddress inetAddress, long j) throws IllegalArgumentException {
        this.address = inetAddress;
        this.expiry = j;
    }

    @Override // java.lang.Comparable
    public int compareTo(DnsServerEntry dnsServerEntry) {
        return Long.compare(dnsServerEntry.expiry, this.expiry);
    }
}
