package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SdpPseRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpPseRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpPseRecord createFromParcel(Parcel parcel) {
            return new SdpPseRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpPseRecord[] newArray(int i) {
            return new SdpPseRecord[i];
        }
    };
    private final int mL2capPsm;
    private final int mProfileVersion;
    private final int mRfcommChannelNumber;
    private final String mServiceName;
    private final int mSupportedFeatures;
    private final int mSupportedRepositories;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SdpPseRecord(int i, int i2, int i3, int i4, int i5, String str) {
        this.mL2capPsm = i;
        this.mRfcommChannelNumber = i2;
        this.mProfileVersion = i3;
        this.mSupportedFeatures = i4;
        this.mSupportedRepositories = i5;
        this.mServiceName = str;
    }

    public SdpPseRecord(Parcel parcel) {
        this.mRfcommChannelNumber = parcel.readInt();
        this.mL2capPsm = parcel.readInt();
        this.mProfileVersion = parcel.readInt();
        this.mSupportedFeatures = parcel.readInt();
        this.mSupportedRepositories = parcel.readInt();
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

    public int getSupportedRepositories() {
        return this.mSupportedRepositories;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRfcommChannelNumber);
        parcel.writeInt(this.mL2capPsm);
        parcel.writeInt(this.mProfileVersion);
        parcel.writeInt(this.mSupportedFeatures);
        parcel.writeInt(this.mSupportedRepositories);
        parcel.writeString(this.mServiceName);
    }

    public String toString() {
        String str = "Bluetooth MNS SDP Record:\n";
        if (this.mRfcommChannelNumber != -1) {
            str = "Bluetooth MNS SDP Record:\nRFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
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
        if (this.mSupportedFeatures != -1) {
            str = str + "Supported features: " + this.mSupportedFeatures + "\n";
        }
        if (this.mSupportedRepositories == -1) {
            return str;
        }
        return str + "Supported repositories: " + this.mSupportedRepositories + "\n";
    }
}
