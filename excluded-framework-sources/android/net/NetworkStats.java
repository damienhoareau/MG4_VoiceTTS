package android.net;

import android.app.CarConfigManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.ArrayUtils;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes.dex */
public class NetworkStats implements Parcelable {
    private static final String CLATD_INTERFACE_PREFIX = "v4-";
    public static final Parcelable.Creator<NetworkStats> CREATOR = new Parcelable.Creator<NetworkStats>() { // from class: android.net.NetworkStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStats createFromParcel(Parcel parcel) {
            return new NetworkStats(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStats[] newArray(int i) {
            return new NetworkStats[i];
        }
    };
    public static final int DEFAULT_NETWORK_ALL = -1;
    public static final int DEFAULT_NETWORK_NO = 0;
    public static final int DEFAULT_NETWORK_YES = 1;
    public static final String IFACE_ALL = null;
    public static final String[] INTERFACES_ALL = null;
    private static final int IPV4V6_HEADER_DELTA = 20;
    public static final int METERED_ALL = -1;
    public static final int METERED_NO = 0;
    public static final int METERED_YES = 1;
    public static final int ROAMING_ALL = -1;
    public static final int ROAMING_NO = 0;
    public static final int ROAMING_YES = 1;
    public static final int SET_ALL = -1;
    public static final int SET_DBG_VPN_IN = 1001;
    public static final int SET_DBG_VPN_OUT = 1002;
    public static final int SET_DEBUG_START = 1000;
    public static final int SET_DEFAULT = 0;
    public static final int SET_FOREGROUND = 1;
    public static final int STATS_PER_IFACE = 0;
    public static final int STATS_PER_UID = 1;
    private static final String TAG = "NetworkStats";
    public static final int TAG_ALL = -1;
    public static final int TAG_NONE = 0;
    public static final int UID_ALL = -1;
    private int capacity;
    private int[] defaultNetwork;
    private long elapsedRealtime;
    private String[] iface;
    private int[] metered;
    private long[] operations;
    private int[] roaming;
    private long[] rxBytes;
    private long[] rxPackets;
    private int[] set;
    private int size;
    private int[] tag;
    private long[] txBytes;
    private long[] txPackets;
    private int[] uid;

    public interface NonMonotonicObserver<C> {
        void foundNonMonotonic(NetworkStats networkStats, int i, NetworkStats networkStats2, int i2, C c);

        void foundNonMonotonic(NetworkStats networkStats, int i, C c);
    }

    public static String defaultNetworkToString(int i) {
        if (i == -1) {
            return "ALL";
        }
        if (i != 0) {
            return i != 1 ? IccCardConstants.INTENT_VALUE_ICC_UNKNOWN : "YES";
        }
        return "NO";
    }

    public static String meteredToString(int i) {
        if (i == -1) {
            return "ALL";
        }
        if (i != 0) {
            return i != 1 ? IccCardConstants.INTENT_VALUE_ICC_UNKNOWN : "YES";
        }
        return "NO";
    }

    public static String roamingToString(int i) {
        if (i == -1) {
            return "ALL";
        }
        if (i != 0) {
            return i != 1 ? IccCardConstants.INTENT_VALUE_ICC_UNKNOWN : "YES";
        }
        return "NO";
    }

    public static boolean setMatches(int i, int i2) {
        if (i == i2) {
            return true;
        }
        return i == -1 && i2 < 1000;
    }

    public static String setToCheckinString(int i) {
        if (i == -1) {
            return "all";
        }
        if (i == 0) {
            return "def";
        }
        if (i == 1) {
            return "fg";
        }
        if (i != 1001) {
            return i != 1002 ? "unk" : "vpnout";
        }
        return "vpnin";
    }

    public static String setToString(int i) {
        if (i == -1) {
            return "ALL";
        }
        if (i == 0) {
            return "DEFAULT";
        }
        if (i == 1) {
            return "FOREGROUND";
        }
        if (i != 1001) {
            return i != 1002 ? IccCardConstants.INTENT_VALUE_ICC_UNKNOWN : "DBG_VPN_OUT";
        }
        return "DBG_VPN_IN";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class Entry {
        public int defaultNetwork;
        public String iface;
        public int metered;
        public long operations;
        public int roaming;
        public long rxBytes;
        public long rxPackets;
        public int set;
        public int tag;
        public long txBytes;
        public long txPackets;
        public int uid;

        public Entry() {
            this(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        }

        public Entry(long j, long j2, long j3, long j4, long j5) {
            this(NetworkStats.IFACE_ALL, -1, 0, 0, j, j2, j3, j4, j5);
        }

        public Entry(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
            this(str, i, i2, i3, 0, 0, 0, j, j2, j3, j4, j5);
        }

        public Entry(String str, int i, int i2, int i3, int i4, int i5, int i6, long j, long j2, long j3, long j4, long j5) {
            this.iface = str;
            this.uid = i;
            this.set = i2;
            this.tag = i3;
            this.metered = i4;
            this.roaming = i5;
            this.defaultNetwork = i6;
            this.rxBytes = j;
            this.rxPackets = j2;
            this.txBytes = j3;
            this.txPackets = j4;
            this.operations = j5;
        }

        public boolean isNegative() {
            return this.rxBytes < 0 || this.rxPackets < 0 || this.txBytes < 0 || this.txPackets < 0 || this.operations < 0;
        }

        public boolean isEmpty() {
            return this.rxBytes == 0 && this.rxPackets == 0 && this.txBytes == 0 && this.txPackets == 0 && this.operations == 0;
        }

        public void add(Entry entry) {
            this.rxBytes += entry.rxBytes;
            this.rxPackets += entry.rxPackets;
            this.txBytes += entry.txBytes;
            this.txPackets += entry.txPackets;
            this.operations += entry.operations;
        }

        public String toString() {
            return "iface=" + this.iface + " uid=" + this.uid + " set=" + NetworkStats.setToString(this.set) + " tag=" + NetworkStats.tagToString(this.tag) + " metered=" + NetworkStats.meteredToString(this.metered) + " roaming=" + NetworkStats.roamingToString(this.roaming) + " defaultNetwork=" + NetworkStats.defaultNetworkToString(this.defaultNetwork) + " rxBytes=" + this.rxBytes + " rxPackets=" + this.rxPackets + " txBytes=" + this.txBytes + " txPackets=" + this.txPackets + " operations=" + this.operations;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.uid == entry.uid && this.set == entry.set && this.tag == entry.tag && this.metered == entry.metered && this.roaming == entry.roaming && this.defaultNetwork == entry.defaultNetwork && this.rxBytes == entry.rxBytes && this.rxPackets == entry.rxPackets && this.txBytes == entry.txBytes && this.txPackets == entry.txPackets && this.operations == entry.operations && this.iface.equals(entry.iface);
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.uid), Integer.valueOf(this.set), Integer.valueOf(this.tag), Integer.valueOf(this.metered), Integer.valueOf(this.roaming), Integer.valueOf(this.defaultNetwork), this.iface);
        }
    }

    public NetworkStats(long j, int i) {
        this.elapsedRealtime = j;
        this.size = 0;
        if (i > 0) {
            this.capacity = i;
            this.iface = new String[i];
            this.uid = new int[i];
            this.set = new int[i];
            this.tag = new int[i];
            this.metered = new int[i];
            this.roaming = new int[i];
            this.defaultNetwork = new int[i];
            this.rxBytes = new long[i];
            this.rxPackets = new long[i];
            this.txBytes = new long[i];
            this.txPackets = new long[i];
            this.operations = new long[i];
            return;
        }
        clear();
    }

    public NetworkStats(Parcel parcel) {
        this.elapsedRealtime = parcel.readLong();
        this.size = parcel.readInt();
        this.capacity = parcel.readInt();
        this.iface = parcel.createStringArray();
        this.uid = parcel.createIntArray();
        this.set = parcel.createIntArray();
        this.tag = parcel.createIntArray();
        this.metered = parcel.createIntArray();
        this.roaming = parcel.createIntArray();
        this.defaultNetwork = parcel.createIntArray();
        this.rxBytes = parcel.createLongArray();
        this.rxPackets = parcel.createLongArray();
        this.txBytes = parcel.createLongArray();
        this.txPackets = parcel.createLongArray();
        this.operations = parcel.createLongArray();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.elapsedRealtime);
        parcel.writeInt(this.size);
        parcel.writeInt(this.capacity);
        parcel.writeStringArray(this.iface);
        parcel.writeIntArray(this.uid);
        parcel.writeIntArray(this.set);
        parcel.writeIntArray(this.tag);
        parcel.writeIntArray(this.metered);
        parcel.writeIntArray(this.roaming);
        parcel.writeIntArray(this.defaultNetwork);
        parcel.writeLongArray(this.rxBytes);
        parcel.writeLongArray(this.rxPackets);
        parcel.writeLongArray(this.txBytes);
        parcel.writeLongArray(this.txPackets);
        parcel.writeLongArray(this.operations);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NetworkStats m22clone() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, this.size);
        Entry values = null;
        for (int i = 0; i < this.size; i++) {
            values = getValues(i, values);
            networkStats.addValues(values);
        }
        return networkStats;
    }

    public void clear() {
        this.capacity = 0;
        this.iface = EmptyArray.STRING;
        this.uid = EmptyArray.INT;
        this.set = EmptyArray.INT;
        this.tag = EmptyArray.INT;
        this.metered = EmptyArray.INT;
        this.roaming = EmptyArray.INT;
        this.defaultNetwork = EmptyArray.INT;
        this.rxBytes = EmptyArray.LONG;
        this.rxPackets = EmptyArray.LONG;
        this.txBytes = EmptyArray.LONG;
        this.txPackets = EmptyArray.LONG;
        this.operations = EmptyArray.LONG;
    }

    public NetworkStats addIfaceValues(String str, long j, long j2, long j3, long j4) {
        return addValues(str, -1, 0, 0, j, j2, j3, j4, 0L);
    }

    public NetworkStats addValues(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
        return addValues(new Entry(str, i, i2, i3, j, j2, j3, j4, j5));
    }

    public NetworkStats addValues(String str, int i, int i2, int i3, int i4, int i5, int i6, long j, long j2, long j3, long j4, long j5) {
        return addValues(new Entry(str, i, i2, i3, i4, i5, i6, j, j2, j3, j4, j5));
    }

    public NetworkStats addValues(Entry entry) {
        int i = this.size;
        if (i >= this.capacity) {
            int iMax = (Math.max(i, 10) * 3) / 2;
            this.iface = (String[]) Arrays.copyOf(this.iface, iMax);
            this.uid = Arrays.copyOf(this.uid, iMax);
            this.set = Arrays.copyOf(this.set, iMax);
            this.tag = Arrays.copyOf(this.tag, iMax);
            this.metered = Arrays.copyOf(this.metered, iMax);
            this.roaming = Arrays.copyOf(this.roaming, iMax);
            this.defaultNetwork = Arrays.copyOf(this.defaultNetwork, iMax);
            this.rxBytes = Arrays.copyOf(this.rxBytes, iMax);
            this.rxPackets = Arrays.copyOf(this.rxPackets, iMax);
            this.txBytes = Arrays.copyOf(this.txBytes, iMax);
            this.txPackets = Arrays.copyOf(this.txPackets, iMax);
            this.operations = Arrays.copyOf(this.operations, iMax);
            this.capacity = iMax;
        }
        setValues(this.size, entry);
        this.size++;
        return this;
    }

    private void setValues(int i, Entry entry) {
        this.iface[i] = entry.iface;
        this.uid[i] = entry.uid;
        this.set[i] = entry.set;
        this.tag[i] = entry.tag;
        this.metered[i] = entry.metered;
        this.roaming[i] = entry.roaming;
        this.defaultNetwork[i] = entry.defaultNetwork;
        this.rxBytes[i] = entry.rxBytes;
        this.rxPackets[i] = entry.rxPackets;
        this.txBytes[i] = entry.txBytes;
        this.txPackets[i] = entry.txPackets;
        this.operations[i] = entry.operations;
    }

    public Entry getValues(int i, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.iface = this.iface[i];
        entry.uid = this.uid[i];
        entry.set = this.set[i];
        entry.tag = this.tag[i];
        entry.metered = this.metered[i];
        entry.roaming = this.roaming[i];
        entry.defaultNetwork = this.defaultNetwork[i];
        entry.rxBytes = this.rxBytes[i];
        entry.rxPackets = this.rxPackets[i];
        entry.txBytes = this.txBytes[i];
        entry.txPackets = this.txPackets[i];
        entry.operations = this.operations[i];
        return entry;
    }

    public long getElapsedRealtime() {
        return this.elapsedRealtime;
    }

    public void setElapsedRealtime(long j) {
        this.elapsedRealtime = j;
    }

    public long getElapsedRealtimeAge() {
        return SystemClock.elapsedRealtime() - this.elapsedRealtime;
    }

    public int size() {
        return this.size;
    }

    public int internalSize() {
        return this.capacity;
    }

    @Deprecated
    public NetworkStats combineValues(String str, int i, int i2, long j, long j2, long j3, long j4, long j5) {
        return combineValues(str, i, 0, i2, j, j2, j3, j4, j5);
    }

    public NetworkStats combineValues(String str, int i, int i2, int i3, long j, long j2, long j3, long j4, long j5) {
        return combineValues(new Entry(str, i, i2, i3, j, j2, j3, j4, j5));
    }

    public NetworkStats combineValues(Entry entry) {
        int iFindIndex = findIndex(entry.iface, entry.uid, entry.set, entry.tag, entry.metered, entry.roaming, entry.defaultNetwork);
        if (iFindIndex == -1) {
            addValues(entry);
        } else {
            long[] jArr = this.rxBytes;
            jArr[iFindIndex] = jArr[iFindIndex] + entry.rxBytes;
            long[] jArr2 = this.rxPackets;
            jArr2[iFindIndex] = jArr2[iFindIndex] + entry.rxPackets;
            long[] jArr3 = this.txBytes;
            jArr3[iFindIndex] = jArr3[iFindIndex] + entry.txBytes;
            long[] jArr4 = this.txPackets;
            jArr4[iFindIndex] = jArr4[iFindIndex] + entry.txPackets;
            long[] jArr5 = this.operations;
            jArr5[iFindIndex] = jArr5[iFindIndex] + entry.operations;
        }
        return this;
    }

    public void combineAllValues(NetworkStats networkStats) {
        Entry values = null;
        for (int i = 0; i < networkStats.size; i++) {
            values = networkStats.getValues(i, values);
            combineValues(values);
        }
    }

    public int findIndex(String str, int i, int i2, int i3, int i4, int i5, int i6) {
        for (int i7 = 0; i7 < this.size; i7++) {
            if (i == this.uid[i7] && i2 == this.set[i7] && i3 == this.tag[i7] && i4 == this.metered[i7] && i5 == this.roaming[i7] && i6 == this.defaultNetwork[i7] && Objects.equals(str, this.iface[i7])) {
                return i7;
            }
        }
        return -1;
    }

    public int findIndexHinted(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        int i8;
        int i9 = 0;
        while (true) {
            int i10 = this.size;
            if (i9 >= i10) {
                return -1;
            }
            int i11 = i9 / 2;
            if (i9 % 2 == 0) {
                i8 = (i11 + i7) % i10;
            } else {
                i8 = (((i10 + i7) - i11) - 1) % i10;
            }
            if (i == this.uid[i8] && i2 == this.set[i8] && i3 == this.tag[i8] && i4 == this.metered[i8] && i5 == this.roaming[i8] && i6 == this.defaultNetwork[i8] && Objects.equals(str, this.iface[i8])) {
                return i8;
            }
            i9++;
        }
    }

    public void spliceOperationsFrom(NetworkStats networkStats) {
        for (int i = 0; i < this.size; i++) {
            int iFindIndex = networkStats.findIndex(this.iface[i], this.uid[i], this.set[i], this.tag[i], this.metered[i], this.roaming[i], this.defaultNetwork[i]);
            if (iFindIndex == -1) {
                this.operations[i] = 0;
            } else {
                this.operations[i] = networkStats.operations[iFindIndex];
            }
        }
    }

    public String[] getUniqueIfaces() {
        HashSet hashSet = new HashSet();
        for (String str : this.iface) {
            if (str != IFACE_ALL) {
                hashSet.add(str);
            }
        }
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    public int[] getUniqueUids() {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        for (int i : this.uid) {
            sparseBooleanArray.put(i, true);
        }
        int size = sparseBooleanArray.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = sparseBooleanArray.keyAt(i2);
        }
        return iArr;
    }

    public long getTotalBytes() {
        Entry total = getTotal(null);
        return total.rxBytes + total.txBytes;
    }

    public Entry getTotal(Entry entry) {
        return getTotal(entry, null, -1, false);
    }

    public Entry getTotal(Entry entry, int i) {
        return getTotal(entry, null, i, false);
    }

    public Entry getTotal(Entry entry, HashSet<String> hashSet) {
        return getTotal(entry, hashSet, -1, false);
    }

    public Entry getTotalIncludingTags(Entry entry) {
        return getTotal(entry, null, -1, true);
    }

    private Entry getTotal(Entry entry, HashSet<String> hashSet, int i, boolean z) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.iface = IFACE_ALL;
        entry.uid = i;
        entry.set = -1;
        entry.tag = 0;
        entry.metered = -1;
        entry.roaming = -1;
        entry.defaultNetwork = -1;
        entry.rxBytes = 0L;
        entry.rxPackets = 0L;
        entry.txBytes = 0L;
        entry.txPackets = 0L;
        entry.operations = 0L;
        for (int i2 = 0; i2 < this.size; i2++) {
            boolean z2 = true;
            boolean z3 = i == -1 || i == this.uid[i2];
            if (hashSet != null && !hashSet.contains(this.iface[i2])) {
                z2 = false;
            }
            if (z3 && z2 && (this.tag[i2] == 0 || z)) {
                entry.rxBytes += this.rxBytes[i2];
                entry.rxPackets += this.rxPackets[i2];
                entry.txBytes += this.txBytes[i2];
                entry.txPackets += this.txPackets[i2];
                entry.operations += this.operations[i2];
            }
        }
        return entry;
    }

    public long getTotalPackets() {
        long j = 0;
        for (int i = this.size - 1; i >= 0; i--) {
            j += this.rxPackets[i] + this.txPackets[i];
        }
        return j;
    }

    public NetworkStats subtract(NetworkStats networkStats) {
        return subtract(this, networkStats, null, null);
    }

    public static <C> NetworkStats subtract(NetworkStats networkStats, NetworkStats networkStats2, NonMonotonicObserver<C> nonMonotonicObserver, C c) {
        return subtract(networkStats, networkStats2, nonMonotonicObserver, c, null);
    }

    public static <C> NetworkStats subtract(NetworkStats networkStats, NetworkStats networkStats2, NonMonotonicObserver<C> nonMonotonicObserver, C c, NetworkStats networkStats3) {
        NetworkStats networkStats4;
        long j;
        NetworkStats networkStats5 = networkStats2;
        long j2 = networkStats.elapsedRealtime - networkStats5.elapsedRealtime;
        if (j2 < 0) {
            if (nonMonotonicObserver != null) {
                nonMonotonicObserver.foundNonMonotonic(networkStats, -1, networkStats2, -1, c);
            }
            j2 = 0;
        }
        Entry entry = new Entry();
        if (networkStats3 != null && networkStats3.capacity >= networkStats.size) {
            networkStats3.size = 0;
            networkStats3.elapsedRealtime = j2;
            networkStats4 = networkStats3;
        } else {
            networkStats4 = new NetworkStats(j2, networkStats.size);
        }
        int i = 0;
        while (i < networkStats.size) {
            entry.iface = networkStats.iface[i];
            entry.uid = networkStats.uid[i];
            entry.set = networkStats.set[i];
            entry.tag = networkStats.tag[i];
            entry.metered = networkStats.metered[i];
            entry.roaming = networkStats.roaming[i];
            entry.defaultNetwork = networkStats.defaultNetwork[i];
            entry.rxBytes = networkStats.rxBytes[i];
            entry.rxPackets = networkStats.rxPackets[i];
            entry.txBytes = networkStats.txBytes[i];
            entry.txPackets = networkStats.txPackets[i];
            entry.operations = networkStats.operations[i];
            NetworkStats networkStats6 = networkStats5;
            int iFindIndexHinted = networkStats2.findIndexHinted(entry.iface, entry.uid, entry.set, entry.tag, entry.metered, entry.roaming, entry.defaultNetwork, i);
            if (iFindIndexHinted != -1) {
                entry.rxBytes -= networkStats6.rxBytes[iFindIndexHinted];
                entry.rxPackets -= networkStats6.rxPackets[iFindIndexHinted];
                entry.txBytes -= networkStats6.txBytes[iFindIndexHinted];
                entry.txPackets -= networkStats6.txPackets[iFindIndexHinted];
                entry.operations -= networkStats6.operations[iFindIndexHinted];
            }
            if (entry.isNegative()) {
                if (nonMonotonicObserver != null) {
                    nonMonotonicObserver.foundNonMonotonic(networkStats, i, networkStats2, iFindIndexHinted, c);
                }
                j = 0;
                entry.rxBytes = Math.max(entry.rxBytes, 0L);
                entry.rxPackets = Math.max(entry.rxPackets, 0L);
                entry.txBytes = Math.max(entry.txBytes, 0L);
                entry.txPackets = Math.max(entry.txPackets, 0L);
                entry.operations = Math.max(entry.operations, 0L);
            } else {
                i = i;
                networkStats4 = networkStats4;
                entry = entry;
                j = 0;
            }
            networkStats4.addValues(entry);
            networkStats5 = networkStats2;
            entry = entry;
            networkStats4 = networkStats4;
            i++;
        }
        return networkStats4;
    }

    public static void apply464xlatAdjustments(NetworkStats networkStats, NetworkStats networkStats2, Map<String, String> map) {
        NetworkStats networkStats3 = new NetworkStats(0L, map.size());
        Entry entry = new Entry(IFACE_ALL, 0, 0, 0, 0, 0, 0, 0L, 0L, 0L, 0L, 0L);
        Entry values = null;
        for (int i = 0; i < networkStats2.size; i++) {
            values = networkStats2.getValues(i, values);
            if (values.iface != null && values.iface.startsWith(CLATD_INTERFACE_PREFIX)) {
                String str = map.get(values.iface);
                if (str != null) {
                    entry.iface = str;
                    entry.rxBytes = -(values.rxBytes + (values.rxPackets * 20));
                    entry.txBytes = -(values.txBytes + (values.txPackets * 20));
                    entry.rxPackets = -values.rxPackets;
                    entry.txPackets = -values.txPackets;
                    networkStats3.combineValues(entry);
                    values.rxBytes += values.rxPackets * 20;
                    values.txBytes += values.txPackets * 20;
                    networkStats2.setValues(i, values);
                }
            }
        }
        networkStats.combineAllValues(networkStats3);
    }

    public void apply464xlatAdjustments(Map<String, String> map) {
        apply464xlatAdjustments(this, this, map);
    }

    public NetworkStats groupedByIface() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        entry.uid = -1;
        entry.set = -1;
        entry.tag = 0;
        entry.metered = -1;
        entry.roaming = -1;
        entry.defaultNetwork = -1;
        entry.operations = 0L;
        for (int i = 0; i < this.size; i++) {
            if (this.tag[i] == 0) {
                entry.iface = this.iface[i];
                entry.rxBytes = this.rxBytes[i];
                entry.rxPackets = this.rxPackets[i];
                entry.txBytes = this.txBytes[i];
                entry.txPackets = this.txPackets[i];
                networkStats.combineValues(entry);
            }
        }
        return networkStats;
    }

    public NetworkStats groupedByUid() {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        entry.iface = IFACE_ALL;
        entry.set = -1;
        entry.tag = 0;
        entry.metered = -1;
        entry.roaming = -1;
        entry.defaultNetwork = -1;
        for (int i = 0; i < this.size; i++) {
            if (this.tag[i] == 0) {
                entry.uid = this.uid[i];
                entry.rxBytes = this.rxBytes[i];
                entry.rxPackets = this.rxPackets[i];
                entry.txBytes = this.txBytes[i];
                entry.txPackets = this.txPackets[i];
                entry.operations = this.operations[i];
                networkStats.combineValues(entry);
            }
        }
        return networkStats;
    }

    public NetworkStats withoutUids(int[] iArr) {
        NetworkStats networkStats = new NetworkStats(this.elapsedRealtime, 10);
        Entry entry = new Entry();
        for (int i = 0; i < this.size; i++) {
            entry = getValues(i, entry);
            if (!ArrayUtils.contains(iArr, entry.uid)) {
                networkStats.addValues(entry);
            }
        }
        return networkStats;
    }

    public void filter(int i, String[] strArr, int i2) {
        if (i == -1 && i2 == -1 && strArr == INTERFACES_ALL) {
            return;
        }
        Entry entry = new Entry();
        int i3 = 0;
        for (int i4 = 0; i4 < this.size; i4++) {
            entry = getValues(i4, entry);
            if ((i == -1 || i == entry.uid) && (i2 == -1 || i2 == entry.tag) && (strArr == INTERFACES_ALL || ArrayUtils.contains(strArr, entry.iface))) {
                setValues(i3, entry);
                i3++;
            }
        }
        this.size = i3;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("NetworkStats: elapsedRealtime=");
        printWriter.println(this.elapsedRealtime);
        for (int i = 0; i < this.size; i++) {
            printWriter.print(str);
            printWriter.print("  [");
            printWriter.print(i);
            printWriter.print("]");
            printWriter.print(" iface=");
            printWriter.print(this.iface[i]);
            printWriter.print(" uid=");
            printWriter.print(this.uid[i]);
            printWriter.print(" set=");
            printWriter.print(setToString(this.set[i]));
            printWriter.print(" tag=");
            printWriter.print(tagToString(this.tag[i]));
            printWriter.print(" metered=");
            printWriter.print(meteredToString(this.metered[i]));
            printWriter.print(" roaming=");
            printWriter.print(roamingToString(this.roaming[i]));
            printWriter.print(" defaultNetwork=");
            printWriter.print(defaultNetworkToString(this.defaultNetwork[i]));
            printWriter.print(" rxBytes=");
            printWriter.print(this.rxBytes[i]);
            printWriter.print(" rxPackets=");
            printWriter.print(this.rxPackets[i]);
            printWriter.print(" txBytes=");
            printWriter.print(this.txBytes[i]);
            printWriter.print(" txPackets=");
            printWriter.print(this.txPackets[i]);
            printWriter.print(" operations=");
            printWriter.println(this.operations[i]);
        }
    }

    public static String tagToString(int i) {
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public String toString() {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        dump("", new PrintWriter(charArrayWriter));
        return charArrayWriter.toString();
    }

    public boolean migrateTun(int i, String str, String str2) {
        Entry entry = new Entry();
        Entry entry2 = new Entry();
        tunAdjustmentInit(i, str, str2, entry, entry2);
        Entry entryTunGetPool = tunGetPool(entry, entry2);
        if (entryTunGetPool.isEmpty()) {
            return true;
        }
        Entry entryAddTrafficToApplications = addTrafficToApplications(i, str, str2, entry, entryTunGetPool);
        deductTrafficFromVpnApp(i, str2, entryAddTrafficToApplications);
        if (entryAddTrafficToApplications.isEmpty()) {
            return true;
        }
        Slog.wtf(TAG, "Failed to deduct underlying network traffic from VPN package. Moved=" + entryAddTrafficToApplications);
        return false;
    }

    private void tunAdjustmentInit(int i, String str, String str2, Entry entry, Entry entry2) {
        Entry entry3 = new Entry();
        for (int i2 = 0; i2 < this.size; i2++) {
            getValues(i2, entry3);
            if (entry3.uid == -1) {
                throw new IllegalStateException("Cannot adjust VPN accounting on an iface aggregated NetworkStats.");
            }
            if (entry3.set == 1001 || entry3.set == 1002) {
                throw new IllegalStateException("Cannot adjust VPN accounting on a NetworkStats containing SET_DBG_VPN_*");
            }
            if (entry3.uid == i && entry3.tag == 0 && Objects.equals(str2, entry3.iface)) {
                entry2.add(entry3);
            }
            if (entry3.uid != i && entry3.tag == 0 && Objects.equals(str, entry3.iface)) {
                entry.add(entry3);
            }
        }
    }

    private static Entry tunGetPool(Entry entry, Entry entry2) {
        Entry entry3 = new Entry();
        entry3.rxBytes = Math.min(entry.rxBytes, entry2.rxBytes);
        entry3.rxPackets = Math.min(entry.rxPackets, entry2.rxPackets);
        entry3.txBytes = Math.min(entry.txBytes, entry2.txBytes);
        entry3.txPackets = Math.min(entry.txPackets, entry2.txPackets);
        entry3.operations = Math.min(entry.operations, entry2.operations);
        return entry3;
    }

    private Entry addTrafficToApplications(int i, String str, String str2, Entry entry, Entry entry2) {
        Entry entry3 = new Entry();
        Entry entry4 = new Entry();
        entry4.iface = str2;
        for (int i2 = 0; i2 < this.size; i2++) {
            if (Objects.equals(this.iface[i2], str) && this.uid[i2] != i) {
                if (entry.rxBytes > 0) {
                    entry4.rxBytes = (entry2.rxBytes * this.rxBytes[i2]) / entry.rxBytes;
                } else {
                    entry4.rxBytes = 0L;
                }
                if (entry.rxPackets > 0) {
                    entry4.rxPackets = (entry2.rxPackets * this.rxPackets[i2]) / entry.rxPackets;
                } else {
                    entry4.rxPackets = 0L;
                }
                if (entry.txBytes > 0) {
                    entry4.txBytes = (entry2.txBytes * this.txBytes[i2]) / entry.txBytes;
                } else {
                    entry4.txBytes = 0L;
                }
                if (entry.txPackets > 0) {
                    entry4.txPackets = (entry2.txPackets * this.txPackets[i2]) / entry.txPackets;
                } else {
                    entry4.txPackets = 0L;
                }
                if (entry.operations > 0) {
                    entry4.operations = (entry2.operations * this.operations[i2]) / entry.operations;
                } else {
                    entry4.operations = 0L;
                }
                entry4.uid = this.uid[i2];
                entry4.tag = this.tag[i2];
                entry4.set = this.set[i2];
                entry4.metered = this.metered[i2];
                entry4.roaming = this.roaming[i2];
                entry4.defaultNetwork = this.defaultNetwork[i2];
                combineValues(entry4);
                if (this.tag[i2] == 0) {
                    entry3.add(entry4);
                    entry4.set = 1001;
                    combineValues(entry4);
                }
            }
        }
        return entry3;
    }

    private void deductTrafficFromVpnApp(int i, String str, Entry entry) {
        entry.uid = i;
        entry.set = 1002;
        entry.tag = 0;
        entry.iface = str;
        entry.metered = -1;
        entry.roaming = -1;
        entry.defaultNetwork = -1;
        combineValues(entry);
        int iFindIndex = findIndex(str, i, 0, 0, 0, 0, 0);
        if (iFindIndex != -1) {
            tunSubtract(iFindIndex, this, entry);
        }
        int iFindIndex2 = findIndex(str, i, 1, 0, 0, 0, 0);
        if (iFindIndex2 != -1) {
            tunSubtract(iFindIndex2, this, entry);
        }
    }

    private static void tunSubtract(int i, NetworkStats networkStats, Entry entry) {
        long jMin = Math.min(networkStats.rxBytes[i], entry.rxBytes);
        long[] jArr = networkStats.rxBytes;
        jArr[i] = jArr[i] - jMin;
        entry.rxBytes -= jMin;
        long jMin2 = Math.min(networkStats.rxPackets[i], entry.rxPackets);
        long[] jArr2 = networkStats.rxPackets;
        jArr2[i] = jArr2[i] - jMin2;
        entry.rxPackets -= jMin2;
        long jMin3 = Math.min(networkStats.txBytes[i], entry.txBytes);
        long[] jArr3 = networkStats.txBytes;
        jArr3[i] = jArr3[i] - jMin3;
        entry.txBytes -= jMin3;
        long jMin4 = Math.min(networkStats.txPackets[i], entry.txPackets);
        long[] jArr4 = networkStats.txPackets;
        jArr4[i] = jArr4[i] - jMin4;
        entry.txPackets -= jMin4;
    }
}
