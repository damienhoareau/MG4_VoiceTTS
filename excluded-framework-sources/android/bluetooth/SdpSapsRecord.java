package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SdpSapsRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpSapsRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpSapsRecord createFromParcel(Parcel parcel) {
            return new SdpSapsRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpRecord[] newArray(int i) {
            return new SdpRecord[i];
        }
    };
    private final int mProfileVersion;
    private final int mRfcommChannelNumber;
    private final String mServiceName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SdpSapsRecord(int i, int i2, String str) {
        this.mRfcommChannelNumber = i;
        this.mProfileVersion = i2;
        this.mServiceName = str;
    }

    public SdpSapsRecord(Parcel parcel) {
        this.mRfcommChannelNumber = parcel.readInt();
        this.mProfileVersion = parcel.readInt();
        this.mServiceName = parcel.readString();
    }

    public int getRfcommCannelNumber() {
        return this.mRfcommChannelNumber;
    }

    public int getProfileVersion() {
        return this.mProfileVersion;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRfcommChannelNumber);
        parcel.writeInt(this.mProfileVersion);
        parcel.writeString(this.mServiceName);
    }

    public String toString() {
        String str = "Bluetooth MAS SDP Record:\n";
        if (this.mRfcommChannelNumber != -1) {
            str = "Bluetooth MAS SDP Record:\nRFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
        }
        if (this.mServiceName != null) {
            str = str + "Service Name: " + this.mServiceName + "\n";
        }
        if (this.mProfileVersion == -1) {
            return str;
        }
        return str + "Profile version: " + this.mProfileVersion + "\n";
    }
}
