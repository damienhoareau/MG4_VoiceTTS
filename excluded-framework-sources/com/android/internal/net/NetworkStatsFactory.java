package com.android.internal.net;

import android.net.NetworkStats;
import android.os.StrictMode;
import android.os.SystemClock;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.ProcFileReader;
import com.android.server.NetworkManagementSocketTagger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
public class NetworkStatsFactory {
    private static final boolean SANITY_CHECK_NATIVE = false;
    private static final String TAG = "NetworkStatsFactory";
    private static final boolean USE_NATIVE_PARSING = true;
    private static final ConcurrentHashMap<String, String> sStackedIfaces = new ConcurrentHashMap<>();
    private final File mStatsXtIfaceAll;
    private final File mStatsXtIfaceFmt;
    private final File mStatsXtUid;
    private boolean mUseBpfStats;

    public static native int nativeReadNetworkStatsDetail(NetworkStats networkStats, String str, int i, String[] strArr, int i2, boolean z);

    public static native int nativeReadNetworkStatsDev(NetworkStats networkStats);

    public static void noteStackedIface(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        sStackedIfaces.put(str, str2);
    }

    public static String[] augmentWithStackedInterfaces(String[] strArr) {
        if (strArr == NetworkStats.INTERFACES_ALL) {
            return null;
        }
        HashSet hashSet = new HashSet(Arrays.asList(strArr));
        for (Map.Entry<String, String> entry : sStackedIfaces.entrySet()) {
            if (hashSet.contains(entry.getKey())) {
                hashSet.add(entry.getValue());
            } else if (hashSet.contains(entry.getValue())) {
                hashSet.add(entry.getKey());
            }
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    public static void apply464xlatAdjustments(NetworkStats networkStats, NetworkStats networkStats2) {
        NetworkStats.apply464xlatAdjustments(networkStats, networkStats2, sStackedIfaces);
    }

    public static void clearStackedIfaces() {
        sStackedIfaces.clear();
    }

    public NetworkStatsFactory() {
        this(new File("/proc/"), new File("/sys/fs/bpf/traffic_uid_stats_map").exists());
    }

    public NetworkStatsFactory(File file, boolean z) {
        this.mStatsXtIfaceAll = new File(file, "net/xt_qtaguid/iface_stat_all");
        this.mStatsXtIfaceFmt = new File(file, "net/xt_qtaguid/iface_stat_fmt");
        this.mStatsXtUid = new File(file, "net/xt_qtaguid/stats");
        this.mUseBpfStats = z;
    }

    public NetworkStats readBpfNetworkStatsDev() throws IOException {
        NetworkStats networkStats = new NetworkStats(SystemClock.elapsedRealtime(), 6);
        if (nativeReadNetworkStatsDev(networkStats) == 0) {
            return networkStats;
        }
        throw new IOException("Failed to parse bpf iface stats");
    }

    public NetworkStats readNetworkStatsSummaryDev() throws Throwable {
        if (this.mUseBpfStats) {
            return readBpfNetworkStatsDev();
        }
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskReads = StrictMode.allowThreadDiskReads();
        NetworkStats networkStats = new NetworkStats(SystemClock.elapsedRealtime(), 6);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        ProcFileReader procFileReader = null;
        try {
            try {
                ProcFileReader procFileReader2 = new ProcFileReader(new FileInputStream(this.mStatsXtIfaceAll));
                while (procFileReader2.hasMoreData()) {
                    try {
                        entry.iface = procFileReader2.nextString();
                        entry.uid = -1;
                        entry.set = -1;
                        entry.tag = 0;
                        boolean z = procFileReader2.nextInt() != 0;
                        entry.rxBytes = procFileReader2.nextLong();
                        entry.rxPackets = procFileReader2.nextLong();
                        entry.txBytes = procFileReader2.nextLong();
                        entry.txPackets = procFileReader2.nextLong();
                        if (z) {
                            entry.rxBytes += procFileReader2.nextLong();
                            entry.rxPackets += procFileReader2.nextLong();
                            entry.txBytes += procFileReader2.nextLong();
                            entry.txPackets += procFileReader2.nextLong();
                        }
                        networkStats.addValues(entry);
                        procFileReader2.finishLine();
                    } catch (NullPointerException e) {
                        e = e;
                        throw new ProtocolException("problem parsing stats", e);
                    } catch (NumberFormatException e2) {
                        e = e2;
                        throw new ProtocolException("problem parsing stats", e);
                    } catch (Throwable th) {
                        th = th;
                        procFileReader = procFileReader2;
                        IoUtils.closeQuietly(procFileReader);
                        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                        throw th;
                    }
                }
                IoUtils.closeQuietly(procFileReader2);
                StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                return networkStats;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (NullPointerException e3) {
            e = e3;
        } catch (NumberFormatException e4) {
            e = e4;
        }
    }

    public NetworkStats readNetworkStatsSummaryXt() throws Throwable {
        if (this.mUseBpfStats) {
            return readBpfNetworkStatsDev();
        }
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskReads = StrictMode.allowThreadDiskReads();
        ProcFileReader procFileReader = null;
        if (!this.mStatsXtIfaceFmt.exists()) {
            return null;
        }
        NetworkStats networkStats = new NetworkStats(SystemClock.elapsedRealtime(), 6);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        try {
            try {
                ProcFileReader procFileReader2 = new ProcFileReader(new FileInputStream(this.mStatsXtIfaceFmt));
                try {
                    procFileReader2.finishLine();
                    while (procFileReader2.hasMoreData()) {
                        entry.iface = procFileReader2.nextString();
                        entry.uid = -1;
                        entry.set = -1;
                        entry.tag = 0;
                        entry.rxBytes = procFileReader2.nextLong();
                        entry.rxPackets = procFileReader2.nextLong();
                        entry.txBytes = procFileReader2.nextLong();
                        entry.txPackets = procFileReader2.nextLong();
                        networkStats.addValues(entry);
                        procFileReader2.finishLine();
                    }
                    IoUtils.closeQuietly(procFileReader2);
                    StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                    return networkStats;
                } catch (NullPointerException e) {
                    e = e;
                    throw new ProtocolException("problem parsing stats", e);
                } catch (NumberFormatException e2) {
                    e = e2;
                    throw new ProtocolException("problem parsing stats", e);
                } catch (Throwable th) {
                    th = th;
                    procFileReader = procFileReader2;
                    IoUtils.closeQuietly(procFileReader);
                    StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (NullPointerException e3) {
            e = e3;
        } catch (NumberFormatException e4) {
            e = e4;
        }
    }

    public NetworkStats readNetworkStatsDetail() throws IOException {
        return readNetworkStatsDetail(-1, null, -1, null);
    }

    public NetworkStats readNetworkStatsDetail(int i, String[] strArr, int i2, NetworkStats networkStats) throws IOException {
        NetworkStats networkStatsDetailInternal = readNetworkStatsDetailInternal(i, strArr, i2, networkStats);
        networkStatsDetailInternal.apply464xlatAdjustments(sStackedIfaces);
        return networkStatsDetailInternal;
    }

    private NetworkStats readNetworkStatsDetailInternal(int i, String[] strArr, int i2, NetworkStats networkStats) throws IOException {
        if (networkStats != null) {
            networkStats.setElapsedRealtime(SystemClock.elapsedRealtime());
        } else {
            networkStats = new NetworkStats(SystemClock.elapsedRealtime(), -1);
        }
        if (nativeReadNetworkStatsDetail(networkStats, this.mStatsXtUid.getAbsolutePath(), i, strArr, i2, this.mUseBpfStats) == 0) {
            return networkStats;
        }
        throw new IOException("Failed to parse network stats");
    }

    public static NetworkStats javaReadNetworkStatsDetail(File file, int i, String[] strArr, int i2) throws Throwable {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskReads = StrictMode.allowThreadDiskReads();
        NetworkStats networkStats = new NetworkStats(SystemClock.elapsedRealtime(), 24);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        int i3 = 1;
        ProcFileReader procFileReader = null;
        try {
            try {
                ProcFileReader procFileReader2 = new ProcFileReader(new FileInputStream(file));
                try {
                    try {
                        procFileReader2.finishLine();
                        int iNextInt = 1;
                        while (procFileReader2.hasMoreData()) {
                            try {
                                iNextInt = procFileReader2.nextInt();
                                if (iNextInt != i3 + 1) {
                                    throw new ProtocolException("inconsistent idx=" + iNextInt + " after lastIdx=" + i3);
                                }
                                entry.iface = procFileReader2.nextString();
                                entry.tag = NetworkManagementSocketTagger.kernelToTag(procFileReader2.nextString());
                                entry.uid = procFileReader2.nextInt();
                                entry.set = procFileReader2.nextInt();
                                entry.rxBytes = procFileReader2.nextLong();
                                entry.rxPackets = procFileReader2.nextLong();
                                entry.txBytes = procFileReader2.nextLong();
                                entry.txPackets = procFileReader2.nextLong();
                                if ((strArr == null || ArrayUtils.contains(strArr, entry.iface)) && ((i == -1 || i == entry.uid) && (i2 == -1 || i2 == entry.tag))) {
                                    networkStats.addValues(entry);
                                }
                                procFileReader2.finishLine();
                                i3 = iNextInt;
                            } catch (NullPointerException | NumberFormatException e) {
                                e = e;
                                i3 = iNextInt;
                                throw new ProtocolException("problem parsing idx " + i3, e);
                            }
                        }
                        IoUtils.closeQuietly(procFileReader2);
                        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                        return networkStats;
                    } catch (Throwable th) {
                        th = th;
                        procFileReader = procFileReader2;
                        IoUtils.closeQuietly(procFileReader);
                        StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskReads);
                        throw th;
                    }
                } catch (NullPointerException e2) {
                    e = e2;
                } catch (NumberFormatException e3) {
                    e = e3;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (NullPointerException e4) {
            e = e4;
        } catch (NumberFormatException e5) {
            e = e5;
        }
    }

    public void assertEquals(NetworkStats networkStats, NetworkStats networkStats2) {
        if (networkStats.size() != networkStats2.size()) {
            throw new AssertionError("Expected size " + networkStats.size() + ", actual size " + networkStats2.size());
        }
        NetworkStats.Entry values = null;
        NetworkStats.Entry values2 = null;
        for (int i = 0; i < networkStats.size(); i++) {
            values = networkStats.getValues(i, values);
            values2 = networkStats2.getValues(i, values2);
            if (!values.equals(values2)) {
                throw new AssertionError("Expected row " + i + ": " + values + ", actual row " + values2);
            }
        }
    }
}
