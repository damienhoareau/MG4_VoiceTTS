package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SdpA2dpRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpA2dpRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpA2dpRecord createFromParcel(Parcel parcel) {
            return new SdpA2dpRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpA2dpRecord[] newArray(int i) {
            return new SdpA2dpRecord[i];
        }
    };
    private final int mL2capPsm;
    private final int mProfileVersion;
    private final int mRfcommChannelNumber;
    private final String mServiceName;
    private final int mSupportedFeatures;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SdpA2dpRecord(int i, int i2, int i3, int i4, String str) {
        this.mL2capPsm = i;
        this.mRfcommChannelNumber = i2;
        this.mProfileVersion = i3;
        this.mSupportedFeatures = i4;
        this.mServiceName = str;
    }

    public SdpA2dpRecord(Parcel parcel) {
        this.mRfcommChannelNumber = parcel.readInt();
        this.mL2capPsm = parcel.readInt();
        this.mProfileVersion = parcel.readInt();
        this.mSupportedFeatures = parcel.readInt();
        this.mServiceName = parcel.readString();
    }

    public int getL2capPsm() {
        return this.mL2capPsm;
    }

    public int getRfcommChannelNumber() {
        return this.mRfcommChannelNumber;
    }

    public int getSupportedFeatures() {
        return this.mSupportedFeatures;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public int getProfileVersion() {
        return this.mProfileVersion;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRfcommChannelNumber);
        parcel.writeInt(this.mL2capPsm);
        parcel.writeInt(this.mProfileVersion);
        parcel.writeInt(this.mSupportedFeatures);
        parcel.writeString(this.mServiceName);
    }

    public String toString() {
        String str = "Bluetooth A2dp SDP Record:\n";
        if (this.mRfcommChannelNumber != -1) {
            str = "Bluetooth A2dp SDP Record:\nRFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
        }
        if (this.mL2capPsm != -1) {
            str = str + "L2CAP PSM: " + this.mL2capPsm + "\n";
        }
        if (this.mProfileVersion != -1) {
            str = str + "profile version: " + this.mProfileVersion + "\n";
        }
        if (this.mServiceName != null) {
            str = str + "Service Name: " + this.mServiceName + "\n";
        }
        if (this.mSupportedFeatures == -1) {
            return str;
        }
        return str + "Supported features: " + this.mSupportedFeatures + "\n";
    }
}
