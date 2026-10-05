package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class UidTraffic implements Cloneable, Parcelable {
    public static final Parcelable.Creator<UidTraffic> CREATOR = new Parcelable.Creator<UidTraffic>() { // from class: android.bluetooth.UidTraffic.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UidTraffic createFromParcel(Parcel parcel) {
            return new UidTraffic(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UidTraffic[] newArray(int i) {
            return new UidTraffic[i];
        }
    };
    private final int mAppUid;
    private long mRxBytes;
    private long mTxBytes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UidTraffic(int i) {
        this.mAppUid = i;
    }

    public UidTraffic(int i, long j, long j2) {
        this.mAppUid = i;
        this.mRxBytes = j;
        this.mTxBytes = j2;
    }

    UidTraffic(Parcel parcel) {
        this.mAppUid = parcel.readInt();
        this.mRxBytes = parcel.readLong();
        this.mTxBytes = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mAppUid);
        parcel.writeLong(this.mRxBytes);
        parcel.writeLong(this.mTxBytes);
    }

    public void setRxBytes(long j) {
        this.mRxBytes = j;
    }

    public void setTxBytes(long j) {
        this.mTxBytes = j;
    }

    public void addRxBytes(long j) {
        this.mRxBytes += j;
    }

    public void addTxBytes(long j) {
        this.mTxBytes += j;
    }

    public int getUid() {
        return this.mAppUid;
    }

    public long getRxBytes() {
        return this.mRxBytes;
    }

    public long getTxBytes() {
        return this.mTxBytes;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public UidTraffic m16clone() {
        return new UidTraffic(this.mAppUid, this.mRxBytes, this.mTxBytes);
    }

    public String toString() {
        return "UidTraffic{mAppUid=" + this.mAppUid + ", mRxBytes=" + this.mRxBytes + ", mTxBytes=" + this.mTxBytes + '}';
    }
}
