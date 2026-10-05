package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.MathUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.CharArrayWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.Random;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes.dex */
public class NetworkStatsHistory implements Parcelable {
    public static final Parcelable.Creator<NetworkStatsHistory> CREATOR = new Parcelable.Creator<NetworkStatsHistory>() { // from class: android.net.NetworkStatsHistory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStatsHistory createFromParcel(Parcel parcel) {
            return new NetworkStatsHistory(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkStatsHistory[] newArray(int i) {
            return new NetworkStatsHistory[i];
        }
    };
    public static final int FIELD_ACTIVE_TIME = 1;
    public static final int FIELD_ALL = -1;
    public static final int FIELD_OPERATIONS = 32;
    public static final int FIELD_RX_BYTES = 2;
    public static final int FIELD_RX_PACKETS = 4;
    public static final int FIELD_TX_BYTES = 8;
    public static final int FIELD_TX_PACKETS = 16;
    private static final int VERSION_ADD_ACTIVE = 3;
    private static final int VERSION_ADD_PACKETS = 2;
    private static final int VERSION_INIT = 1;
    private long[] activeTime;
    private int bucketCount;
    private long bucketDuration;
    private long[] bucketStart;
    private long[] operations;
    private long[] rxBytes;
    private long[] rxPackets;
    private long totalBytes;
    private long[] txBytes;
    private long[] txPackets;

    public static class Entry {
        public static final long UNKNOWN = -1;
        public long activeTime;
        public long bucketDuration;
        public long bucketStart;
        public long operations;
        public long rxBytes;
        public long rxPackets;
        public long txBytes;
        public long txPackets;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NetworkStatsHistory(long j) {
        this(j, 10, -1);
    }

    public NetworkStatsHistory(long j, int i) {
        this(j, i, -1);
    }

    public NetworkStatsHistory(long j, int i, int i2) {
        this.bucketDuration = j;
        this.bucketStart = new long[i];
        if ((i2 & 1) != 0) {
            this.activeTime = new long[i];
        }
        if ((i2 & 2) != 0) {
            this.rxBytes = new long[i];
        }
        if ((i2 & 4) != 0) {
            this.rxPackets = new long[i];
        }
        if ((i2 & 8) != 0) {
            this.txBytes = new long[i];
        }
        if ((i2 & 16) != 0) {
            this.txPackets = new long[i];
        }
        if ((i2 & 32) != 0) {
            this.operations = new long[i];
        }
        this.bucketCount = 0;
        this.totalBytes = 0L;
    }

    public NetworkStatsHistory(NetworkStatsHistory networkStatsHistory, long j) {
        this(j, networkStatsHistory.estimateResizeBuckets(j));
        recordEntireHistory(networkStatsHistory);
    }

    public NetworkStatsHistory(Parcel parcel) {
        this.bucketDuration = parcel.readLong();
        this.bucketStart = ParcelUtils.readLongArray(parcel);
        this.activeTime = ParcelUtils.readLongArray(parcel);
        this.rxBytes = ParcelUtils.readLongArray(parcel);
        this.rxPackets = ParcelUtils.readLongArray(parcel);
        this.txBytes = ParcelUtils.readLongArray(parcel);
        this.txPackets = ParcelUtils.readLongArray(parcel);
        this.operations = ParcelUtils.readLongArray(parcel);
        this.bucketCount = this.bucketStart.length;
        this.totalBytes = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.bucketDuration);
        ParcelUtils.writeLongArray(parcel, this.bucketStart, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.activeTime, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.rxBytes, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.rxPackets, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.txBytes, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.txPackets, this.bucketCount);
        ParcelUtils.writeLongArray(parcel, this.operations, this.bucketCount);
        parcel.writeLong(this.totalBytes);
    }

    public NetworkStatsHistory(DataInputStream dataInputStream) throws IOException {
        long[] varLongArray;
        int i = dataInputStream.readInt();
        if (i == 1) {
            this.bucketDuration = dataInputStream.readLong();
            this.bucketStart = DataStreamUtils.readFullLongArray(dataInputStream);
            this.rxBytes = DataStreamUtils.readFullLongArray(dataInputStream);
            this.rxPackets = new long[this.bucketStart.length];
            this.txBytes = DataStreamUtils.readFullLongArray(dataInputStream);
            long[] jArr = this.bucketStart;
            this.txPackets = new long[jArr.length];
            this.operations = new long[jArr.length];
            this.bucketCount = jArr.length;
            this.totalBytes = ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes);
        } else if (i == 2 || i == 3) {
            this.bucketDuration = dataInputStream.readLong();
            long[] varLongArray2 = DataStreamUtils.readVarLongArray(dataInputStream);
            this.bucketStart = varLongArray2;
            if (i >= 3) {
                varLongArray = DataStreamUtils.readVarLongArray(dataInputStream);
            } else {
                varLongArray = new long[varLongArray2.length];
            }
            this.activeTime = varLongArray;
            this.rxBytes = DataStreamUtils.readVarLongArray(dataInputStream);
            this.rxPackets = DataStreamUtils.readVarLongArray(dataInputStream);
            this.txBytes = DataStreamUtils.readVarLongArray(dataInputStream);
            this.txPackets = DataStreamUtils.readVarLongArray(dataInputStream);
            this.operations = DataStreamUtils.readVarLongArray(dataInputStream);
            this.bucketCount = this.bucketStart.length;
            this.totalBytes = ArrayUtils.total(this.rxBytes) + ArrayUtils.total(this.txBytes);
        } else {
            throw new ProtocolException("unexpected version: " + i);
        }
        int length = this.bucketStart.length;
        int i2 = this.bucketCount;
        if (length != i2 || this.rxBytes.length != i2 || this.rxPackets.length != i2 || this.txBytes.length != i2 || this.txPackets.length != i2 || this.operations.length != i2) {
            throw new ProtocolException("Mismatched history lengths");
        }
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(3);
        dataOutputStream.writeLong(this.bucketDuration);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.bucketStart, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.activeTime, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.rxBytes, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.rxPackets, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.txBytes, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.txPackets, this.bucketCount);
        DataStreamUtils.writeVarLongArray(dataOutputStream, this.operations, this.bucketCount);
    }

    public int size() {
        return this.bucketCount;
    }

    public long getBucketDuration() {
        return this.bucketDuration;
    }

    public long getStart() {
        if (this.bucketCount > 0) {
            return this.bucketStart[0];
        }
        return Long.MAX_VALUE;
    }

    public long getEnd() {
        int i = this.bucketCount;
        if (i > 0) {
            return this.bucketStart[i - 1] + this.bucketDuration;
        }
        return Long.MIN_VALUE;
    }

    public long getTotalBytes() {
        return this.totalBytes;
    }

    public int getIndexBefore(long j) {
        int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j);
        return MathUtils.constrain(iBinarySearch < 0 ? (~iBinarySearch) - 1 : iBinarySearch - 1, 0, this.bucketCount - 1);
    }

    public int getIndexAfter(long j) {
        int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j);
        return MathUtils.constrain(iBinarySearch < 0 ? ~iBinarySearch : iBinarySearch + 1, 0, this.bucketCount - 1);
    }

    public Entry getValues(int i, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        entry.bucketStart = this.bucketStart[i];
        entry.bucketDuration = this.bucketDuration;
        entry.activeTime = getLong(this.activeTime, i, -1L);
        entry.rxBytes = getLong(this.rxBytes, i, -1L);
        entry.rxPackets = getLong(this.rxPackets, i, -1L);
        entry.txBytes = getLong(this.txBytes, i, -1L);
        entry.txPackets = getLong(this.txPackets, i, -1L);
        entry.operations = getLong(this.operations, i, -1L);
        return entry;
    }

    public void setValues(int i, Entry entry) {
        long[] jArr = this.rxBytes;
        if (jArr != null) {
            this.totalBytes -= jArr[i];
        }
        long[] jArr2 = this.txBytes;
        if (jArr2 != null) {
            this.totalBytes -= jArr2[i];
        }
        this.bucketStart[i] = entry.bucketStart;
        setLong(this.activeTime, i, entry.activeTime);
        setLong(this.rxBytes, i, entry.rxBytes);
        setLong(this.rxPackets, i, entry.rxPackets);
        setLong(this.txBytes, i, entry.txBytes);
        setLong(this.txPackets, i, entry.txPackets);
        setLong(this.operations, i, entry.operations);
        long[] jArr3 = this.rxBytes;
        if (jArr3 != null) {
            this.totalBytes += jArr3[i];
        }
        long[] jArr4 = this.txBytes;
        if (jArr4 != null) {
            this.totalBytes += jArr4[i];
        }
    }

    @Deprecated
    public void recordData(long j, long j2, long j3, long j4) {
        recordData(j, j2, new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, j3, 0L, j4, 0L, 0L));
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0050  */
    public void recordData(long j, long j2, NetworkStats.Entry entry) {
        long j3 = j;
        long j4 = j2;
        long j5 = entry.rxBytes;
        long j6 = entry.rxPackets;
        long j7 = entry.txBytes;
        long j8 = entry.txPackets;
        long j9 = entry.operations;
        if (entry.isNegative()) {
            throw new IllegalArgumentException("tried recording negative data");
        }
        if (entry.isEmpty()) {
            return;
        }
        ensureBuckets(j, j2);
        long j10 = j4 - j3;
        int indexAfter = getIndexAfter(j4);
        while (indexAfter >= 0) {
            long j11 = j9;
            long j12 = j8;
            long j13 = this.bucketStart[indexAfter];
            long j14 = this.bucketDuration + j13;
            if (j14 < j3) {
                break;
            }
            if (j13 > j4) {
                j9 = j11;
                j8 = j12;
            } else {
                long jMin = Math.min(j14, j4) - Math.max(j13, j3);
                if (jMin <= 0) {
                    j9 = j11;
                    j8 = j12;
                } else {
                    long j15 = (j5 * jMin) / j10;
                    long j16 = (j6 * jMin) / j10;
                    long j17 = (j7 * jMin) / j10;
                    long j18 = j7;
                    long j19 = (j12 * jMin) / j10;
                    long j20 = (j11 * jMin) / j10;
                    addLong(this.activeTime, indexAfter, jMin);
                    addLong(this.rxBytes, indexAfter, j15);
                    j5 -= j15;
                    addLong(this.rxPackets, indexAfter, j16);
                    j6 -= j16;
                    addLong(this.txBytes, indexAfter, j17);
                    j7 = j18 - j17;
                    addLong(this.txPackets, indexAfter, j19);
                    j8 = j12 - j19;
                    addLong(this.operations, indexAfter, j20);
                    j10 -= jMin;
                    j9 = j11 - j20;
                }
            }
            indexAfter--;
            j3 = j;
            j4 = j2;
        }
        this.totalBytes += entry.rxBytes + entry.txBytes;
    }

    public void recordEntireHistory(NetworkStatsHistory networkStatsHistory) {
        recordHistory(networkStatsHistory, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public void recordHistory(NetworkStatsHistory networkStatsHistory, long j, long j2) {
        NetworkStats.Entry entry;
        NetworkStats.Entry entry2 = entry;
        NetworkStats.Entry entry3 = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        int i = 0;
        while (i < networkStatsHistory.bucketCount) {
            long j3 = networkStatsHistory.bucketStart[i];
            long j4 = networkStatsHistory.bucketDuration + j3;
            if (j3 < j || j4 > j2) {
                entry = entry2;
            } else {
                entry = entry2;
                entry.rxBytes = getLong(networkStatsHistory.rxBytes, i, 0L);
                entry.rxPackets = getLong(networkStatsHistory.rxPackets, i, 0L);
                entry.txBytes = getLong(networkStatsHistory.txBytes, i, 0L);
                entry.txPackets = getLong(networkStatsHistory.txPackets, i, 0L);
                entry.operations = getLong(networkStatsHistory.operations, i, 0L);
                recordData(j3, j4, entry);
            }
            i++;
            entry2 = entry;
        }
    }

    private void ensureBuckets(long j, long j2) {
        long j3 = this.bucketDuration;
        long j4 = j - (j % j3);
        long j5 = j2 + ((j3 - (j2 % j3)) % j3);
        while (j4 < j5) {
            int iBinarySearch = Arrays.binarySearch(this.bucketStart, 0, this.bucketCount, j4);
            if (iBinarySearch < 0) {
                insertBucket(~iBinarySearch, j4);
            }
            j4 += this.bucketDuration;
        }
    }

    private void insertBucket(int i, long j) {
        int i2 = this.bucketCount;
        long[] jArr = this.bucketStart;
        if (i2 >= jArr.length) {
            int iMax = (Math.max(jArr.length, 10) * 3) / 2;
            this.bucketStart = Arrays.copyOf(this.bucketStart, iMax);
            long[] jArr2 = this.activeTime;
            if (jArr2 != null) {
                this.activeTime = Arrays.copyOf(jArr2, iMax);
            }
            long[] jArr3 = this.rxBytes;
            if (jArr3 != null) {
                this.rxBytes = Arrays.copyOf(jArr3, iMax);
            }
            long[] jArr4 = this.rxPackets;
            if (jArr4 != null) {
                this.rxPackets = Arrays.copyOf(jArr4, iMax);
            }
            long[] jArr5 = this.txBytes;
            if (jArr5 != null) {
                this.txBytes = Arrays.copyOf(jArr5, iMax);
            }
            long[] jArr6 = this.txPackets;
            if (jArr6 != null) {
                this.txPackets = Arrays.copyOf(jArr6, iMax);
            }
            long[] jArr7 = this.operations;
            if (jArr7 != null) {
                this.operations = Arrays.copyOf(jArr7, iMax);
            }
        }
        int i3 = this.bucketCount;
        if (i < i3) {
            int i4 = i + 1;
            int i5 = i3 - i;
            long[] jArr8 = this.bucketStart;
            System.arraycopy(jArr8, i, jArr8, i4, i5);
            long[] jArr9 = this.activeTime;
            if (jArr9 != null) {
                System.arraycopy(jArr9, i, jArr9, i4, i5);
            }
            long[] jArr10 = this.rxBytes;
            if (jArr10 != null) {
                System.arraycopy(jArr10, i, jArr10, i4, i5);
            }
            long[] jArr11 = this.rxPackets;
            if (jArr11 != null) {
                System.arraycopy(jArr11, i, jArr11, i4, i5);
            }
            long[] jArr12 = this.txBytes;
            if (jArr12 != null) {
                System.arraycopy(jArr12, i, jArr12, i4, i5);
            }
            long[] jArr13 = this.txPackets;
            if (jArr13 != null) {
                System.arraycopy(jArr13, i, jArr13, i4, i5);
            }
            long[] jArr14 = this.operations;
            if (jArr14 != null) {
                System.arraycopy(jArr14, i, jArr14, i4, i5);
            }
        }
        this.bucketStart[i] = j;
        setLong(this.activeTime, i, 0L);
        setLong(this.rxBytes, i, 0L);
        setLong(this.rxPackets, i, 0L);
        setLong(this.txBytes, i, 0L);
        setLong(this.txPackets, i, 0L);
        setLong(this.operations, i, 0L);
        this.bucketCount++;
    }

    public void clear() {
        this.bucketStart = EmptyArray.LONG;
        if (this.activeTime != null) {
            this.activeTime = EmptyArray.LONG;
        }
        if (this.rxBytes != null) {
            this.rxBytes = EmptyArray.LONG;
        }
        if (this.rxPackets != null) {
            this.rxPackets = EmptyArray.LONG;
        }
        if (this.txBytes != null) {
            this.txBytes = EmptyArray.LONG;
        }
        if (this.txPackets != null) {
            this.txPackets = EmptyArray.LONG;
        }
        if (this.operations != null) {
            this.operations = EmptyArray.LONG;
        }
        this.bucketCount = 0;
        this.totalBytes = 0L;
    }

    @Deprecated
    public void removeBucketsBefore(long j) {
        int i = 0;
        while (i < this.bucketCount && this.bucketStart[i] + this.bucketDuration <= j) {
            i++;
        }
        if (i > 0) {
            long[] jArr = this.bucketStart;
            int length = jArr.length;
            this.bucketStart = Arrays.copyOfRange(jArr, i, length);
            long[] jArr2 = this.activeTime;
            if (jArr2 != null) {
                this.activeTime = Arrays.copyOfRange(jArr2, i, length);
            }
            long[] jArr3 = this.rxBytes;
            if (jArr3 != null) {
                this.rxBytes = Arrays.copyOfRange(jArr3, i, length);
            }
            long[] jArr4 = this.rxPackets;
            if (jArr4 != null) {
                this.rxPackets = Arrays.copyOfRange(jArr4, i, length);
            }
            long[] jArr5 = this.txBytes;
            if (jArr5 != null) {
                this.txBytes = Arrays.copyOfRange(jArr5, i, length);
            }
            long[] jArr6 = this.txPackets;
            if (jArr6 != null) {
                this.txPackets = Arrays.copyOfRange(jArr6, i, length);
            }
            long[] jArr7 = this.operations;
            if (jArr7 != null) {
                this.operations = Arrays.copyOfRange(jArr7, i, length);
            }
            this.bucketCount -= i;
        }
    }

    public Entry getValues(long j, long j2, Entry entry) {
        return getValues(j, j2, Long.MAX_VALUE, entry);
    }

    public Entry getValues(long j, long j2, long j3, Entry entry) {
        long j4;
        Entry entry2 = entry != null ? entry : new Entry();
        entry2.bucketDuration = j2 - j;
        entry2.bucketStart = j;
        long j5 = 0;
        entry2.activeTime = this.activeTime != null ? 0L : -1L;
        entry2.rxBytes = this.rxBytes != null ? 0L : -1L;
        entry2.rxPackets = this.rxPackets != null ? 0L : -1L;
        entry2.txBytes = this.txBytes != null ? 0L : -1L;
        entry2.txPackets = this.txPackets != null ? 0L : -1L;
        entry2.operations = this.operations != null ? 0L : -1L;
        int indexAfter = getIndexAfter(j2);
        while (indexAfter >= 0) {
            long j6 = this.bucketStart[indexAfter];
            long j7 = this.bucketDuration + j6;
            if (j7 <= j) {
                break;
            }
            if (j6 < j2) {
                if (j6 < j3 && j7 > j3) {
                    j4 = this.bucketDuration;
                } else {
                    if (j7 >= j2) {
                        j7 = j2;
                    }
                    if (j6 <= j) {
                        j6 = j;
                    }
                    j4 = j7 - j6;
                }
                if (j4 > j5) {
                    if (this.activeTime != null) {
                        entry2.activeTime += (this.activeTime[indexAfter] * j4) / this.bucketDuration;
                    }
                    if (this.rxBytes != null) {
                        entry2.rxBytes += (this.rxBytes[indexAfter] * j4) / this.bucketDuration;
                    }
                    if (this.rxPackets != null) {
                        entry2.rxPackets += (this.rxPackets[indexAfter] * j4) / this.bucketDuration;
                    }
                    if (this.txBytes != null) {
                        entry2.txBytes += (this.txBytes[indexAfter] * j4) / this.bucketDuration;
                    }
                    if (this.txPackets != null) {
                        entry2.txPackets += (this.txPackets[indexAfter] * j4) / this.bucketDuration;
                    }
                    if (this.operations != null) {
                        entry2.operations += (this.operations[indexAfter] * j4) / this.bucketDuration;
                    }
                }
            }
            indexAfter--;
            j5 = 0;
        }
        return entry2;
    }

    @Deprecated
    public void generateRandom(long j, long j2, long j3) {
        Random random = new Random();
        float fNextFloat = random.nextFloat();
        float f = j3;
        long j4 = (long) (f * fNextFloat);
        long j5 = (long) (f * (1.0f - fNextFloat));
        generateRandom(j, j2, j4, j4 / 1024, j5, j5 / 1024, j4 / 2048, random);
    }

    @Deprecated
    public void generateRandom(long j, long j2, long j3, long j4, long j5, long j6, long j7, Random random) {
        ensureBuckets(j, j2);
        NetworkStats.Entry entry = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, 0, 0, 0L, 0L, 0L, 0L, 0L);
        long j8 = j3;
        long j9 = j4;
        long j10 = j5;
        long j11 = j6;
        long j12 = j7;
        while (true) {
            if (j8 <= 1024 && j9 <= 128 && j10 <= 1024 && j11 <= 128 && j12 <= 32) {
                return;
            }
            long jRandomLong = randomLong(random, j, j2);
            long jRandomLong2 = randomLong(random, 0L, (j2 - jRandomLong) / 2) + jRandomLong;
            entry.rxBytes = randomLong(random, 0L, j8);
            entry.rxPackets = randomLong(random, 0L, j9);
            entry.txBytes = randomLong(random, 0L, j10);
            entry.txPackets = randomLong(random, 0L, j11);
            entry.operations = randomLong(random, 0L, j12);
            j8 -= entry.rxBytes;
            j9 -= entry.rxPackets;
            j10 -= entry.txBytes;
            j11 -= entry.txPackets;
            j12 -= entry.operations;
            recordData(jRandomLong, jRandomLong2, entry);
        }
    }

    public static long randomLong(Random random, long j, long j2) {
        return (long) (j + (random.nextFloat() * (j2 - j)));
    }

    public boolean intersects(long j, long j2) {
        long start = getStart();
        long end = getEnd();
        if (j >= start && j <= end) {
            return true;
        }
        if (j2 >= start && j2 <= end) {
            return true;
        }
        if (start < j || start > j2) {
            return end >= j && end <= j2;
        }
        return true;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, boolean z) {
        indentingPrintWriter.print("NetworkStatsHistory: bucketDuration=");
        indentingPrintWriter.println(this.bucketDuration / 1000);
        indentingPrintWriter.increaseIndent();
        int iMax = z ? 0 : Math.max(0, this.bucketCount - 32);
        if (iMax > 0) {
            indentingPrintWriter.print("(omitting ");
            indentingPrintWriter.print(iMax);
            indentingPrintWriter.println(" buckets)");
        }
        while (iMax < this.bucketCount) {
            indentingPrintWriter.print("st=");
            indentingPrintWriter.print(this.bucketStart[iMax] / 1000);
            if (this.rxBytes != null) {
                indentingPrintWriter.print(" rb=");
                indentingPrintWriter.print(this.rxBytes[iMax]);
            }
            if (this.rxPackets != null) {
                indentingPrintWriter.print(" rp=");
                indentingPrintWriter.print(this.rxPackets[iMax]);
            }
            if (this.txBytes != null) {
                indentingPrintWriter.print(" tb=");
                indentingPrintWriter.print(this.txBytes[iMax]);
            }
            if (this.txPackets != null) {
                indentingPrintWriter.print(" tp=");
                indentingPrintWriter.print(this.txPackets[iMax]);
            }
            if (this.operations != null) {
                indentingPrintWriter.print(" op=");
                indentingPrintWriter.print(this.operations[iMax]);
            }
            indentingPrintWriter.println();
            iMax++;
        }
        indentingPrintWriter.decreaseIndent();
    }

    public void dumpCheckin(PrintWriter printWriter) {
        printWriter.print("d,");
        printWriter.print(this.bucketDuration / 1000);
        printWriter.println();
        for (int i = 0; i < this.bucketCount; i++) {
            printWriter.print("b,");
            printWriter.print(this.bucketStart[i] / 1000);
            printWriter.print(',');
            long[] jArr = this.rxBytes;
            if (jArr != null) {
                printWriter.print(jArr[i]);
            } else {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            printWriter.print(',');
            long[] jArr2 = this.rxPackets;
            if (jArr2 != null) {
                printWriter.print(jArr2[i]);
            } else {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            printWriter.print(',');
            long[] jArr3 = this.txBytes;
            if (jArr3 != null) {
                printWriter.print(jArr3[i]);
            } else {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            printWriter.print(',');
            long[] jArr4 = this.txPackets;
            if (jArr4 != null) {
                printWriter.print(jArr4[i]);
            } else {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            printWriter.print(',');
            long[] jArr5 = this.operations;
            if (jArr5 != null) {
                printWriter.print(jArr5[i]);
            } else {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            printWriter.println();
        }
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1112396529665L, this.bucketDuration);
        for (int i = 0; i < this.bucketCount; i++) {
            long jStart2 = protoOutputStream.start(2246267895810L);
            protoOutputStream.write(1112396529665L, this.bucketStart[i]);
            writeToProto(protoOutputStream, 1112396529666L, this.rxBytes, i);
            writeToProto(protoOutputStream, 1112396529667L, this.rxPackets, i);
            writeToProto(protoOutputStream, 1112396529668L, this.txBytes, i);
            writeToProto(protoOutputStream, 1112396529669L, this.txPackets, i);
            writeToProto(protoOutputStream, 1112396529670L, this.operations, i);
            protoOutputStream.end(jStart2);
        }
        protoOutputStream.end(jStart);
    }

    private static void writeToProto(ProtoOutputStream protoOutputStream, long j, long[] jArr, int i) {
        if (jArr != null) {
            protoOutputStream.write(j, jArr[i]);
        }
    }

    public String toString() {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        dump(new IndentingPrintWriter(charArrayWriter, "  "), false);
        return charArrayWriter.toString();
    }

    private static long getLong(long[] jArr, int i, long j) {
        return jArr != null ? jArr[i] : j;
    }

    private static void setLong(long[] jArr, int i, long j) {
        if (jArr != null) {
            jArr[i] = j;
        }
    }

    private static void addLong(long[] jArr, int i, long j) {
        if (jArr != null) {
            jArr[i] = jArr[i] + j;
        }
    }

    public int estimateResizeBuckets(long j) {
        return (int) ((((long) size()) * getBucketDuration()) / j);
    }

    public static class DataStreamUtils {
        @Deprecated
        public static long[] readFullLongArray(DataInputStream dataInputStream) throws IOException {
            int i = dataInputStream.readInt();
            if (i < 0) {
                throw new ProtocolException("negative array size");
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = dataInputStream.readLong();
            }
            return jArr;
        }

        public static long readVarLong(DataInputStream dataInputStream) throws IOException {
            long j = 0;
            for (int i = 0; i < 64; i += 7) {
                byte b = dataInputStream.readByte();
                j |= ((long) (b & 127)) << i;
                if ((b & 128) == 0) {
                    return j;
                }
            }
            throw new ProtocolException("malformed long");
        }

        public static void writeVarLong(DataOutputStream dataOutputStream, long j) throws IOException {
            while (((-128) & j) != 0) {
                dataOutputStream.writeByte((((int) j) & 127) | 128);
                j >>>= 7;
            }
            dataOutputStream.writeByte((int) j);
        }

        public static long[] readVarLongArray(DataInputStream dataInputStream) throws IOException {
            int i = dataInputStream.readInt();
            if (i == -1) {
                return null;
            }
            if (i < 0) {
                throw new ProtocolException("negative array size");
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = readVarLong(dataInputStream);
            }
            return jArr;
        }

        public static void writeVarLongArray(DataOutputStream dataOutputStream, long[] jArr, int i) throws IOException {
            if (jArr == null) {
                dataOutputStream.writeInt(-1);
                return;
            }
            if (i > jArr.length) {
                throw new IllegalArgumentException("size larger than length");
            }
            dataOutputStream.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                writeVarLong(dataOutputStream, jArr[i2]);
            }
        }
    }

    public static class ParcelUtils {
        public static long[] readLongArray(Parcel parcel) {
            int i = parcel.readInt();
            if (i == -1) {
                return null;
            }
            long[] jArr = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                jArr[i2] = parcel.readLong();
            }
            return jArr;
        }

        public static void writeLongArray(Parcel parcel, long[] jArr, int i) {
            if (jArr == null) {
                parcel.writeInt(-1);
                return;
            }
            if (i > jArr.length) {
                throw new IllegalArgumentException("size larger than length");
            }
            parcel.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                parcel.writeLong(jArr[i2]);
            }
        }
    }
}
