package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SdpRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpRecord createFromParcel(Parcel parcel) {
            return new SdpRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpRecord[] newArray(int i) {
            return new SdpRecord[i];
        }
    };
    private final byte[] mRawData;
    private final int mRawSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "BluetoothSdpRecord [rawData=" + Arrays.toString(this.mRawData) + ", rawSize=" + this.mRawSize + "]";
    }

    public SdpRecord(int i, byte[] bArr) {
        this.mRawData = bArr;
        this.mRawSize = i;
    }

    public SdpRecord(Parcel parcel) {
        int i = parcel.readInt();
        this.mRawSize = i;
        byte[] bArr = new byte[i];
        this.mRawData = bArr;
        parcel.readByteArray(bArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRawSize);
        parcel.writeByteArray(this.mRawData);
    }

    public byte[] getRawData() {
        return this.mRawData;
    }

    public int getRawSize() {
        return this.mRawSize;
    }
}
