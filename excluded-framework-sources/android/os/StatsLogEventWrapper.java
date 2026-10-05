package android.os;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes2.dex */
public final class StatsLogEventWrapper implements Parcelable {
    public static final Parcelable.Creator<StatsLogEventWrapper> CREATOR = new Parcelable.Creator<StatsLogEventWrapper>() { // from class: android.os.StatsLogEventWrapper.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatsLogEventWrapper createFromParcel(Parcel parcel) {
            return new StatsLogEventWrapper(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatsLogEventWrapper[] newArray(int i) {
            return new StatsLogEventWrapper[i];
        }
    };
    private static final int EVENT_TYPE_FLOAT = 4;
    private static final int EVENT_TYPE_INT = 0;
    private static final int EVENT_TYPE_LIST = 3;
    private static final int EVENT_TYPE_LONG = 1;
    private static final int EVENT_TYPE_STRING = 2;
    private static final int STATS_BUFFER_TAG_ID = 1937006964;
    private ByteArrayOutputStream mStorage;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
    }

    public StatsLogEventWrapper(long j, int i, int i2) {
        this.mStorage = new ByteArrayOutputStream();
        write4Bytes(STATS_BUFFER_TAG_ID);
        this.mStorage.write(3);
        this.mStorage.write(i2 + 2);
        writeLong(j);
        writeInt(i);
    }

    private void write4Bytes(int i) {
        this.mStorage.write(i);
        this.mStorage.write(i >>> 8);
        this.mStorage.write(i >>> 16);
        this.mStorage.write(i >>> 24);
    }

    private void write8Bytes(long j) {
        write4Bytes((int) ((-1) & j));
        write4Bytes((int) (j >>> 32));
    }

    public void writeInt(int i) {
        this.mStorage.write(0);
        write4Bytes(i);
    }

    public void writeLong(long j) {
        this.mStorage.write(1);
        write8Bytes(j);
    }

    public void writeFloat(float f) {
        int iFloatToIntBits = Float.floatToIntBits(f);
        this.mStorage.write(4);
        write4Bytes(iFloatToIntBits);
    }

    public void writeString(String str) {
        this.mStorage.write(2);
        write4Bytes(str.length());
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        this.mStorage.write(bytes, 0, bytes.length);
    }

    private StatsLogEventWrapper(Parcel parcel) {
        this.mStorage = new ByteArrayOutputStream();
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mStorage.write(10);
        parcel.writeByteArray(this.mStorage.toByteArray());
    }
}
