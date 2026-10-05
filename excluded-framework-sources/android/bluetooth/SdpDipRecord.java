package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SdpDipRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpDipRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpDipRecord createFromParcel(Parcel parcel) {
            return new SdpDipRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpDipRecord[] newArray(int i) {
            return new SdpDipRecord[i];
        }
    };
    private final String mClientExecutableUrl;
    private final String mDocumentationUrl;
    private final boolean mPrimaryRecord;
    private final int mProductId;
    private final String mServiceDescription;
    private final int mSpecificationId;
    private final int mVendorId;
    private final int mVendorIdSource;
    private final int mVersion;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SdpDipRecord(int i, int i2, int i3, int i4, int i5, boolean z, String str, String str2, String str3) {
        this.mSpecificationId = i;
        this.mVendorId = i2;
        this.mVendorIdSource = i3;
        this.mProductId = i4;
        this.mVersion = i5;
        this.mPrimaryRecord = z;
        this.mClientExecutableUrl = str;
        this.mServiceDescription = str2;
        this.mDocumentationUrl = str3;
    }

    public SdpDipRecord(Parcel parcel) {
        this.mSpecificationId = parcel.readInt();
        this.mVendorId = parcel.readInt();
        this.mVendorIdSource = parcel.readInt();
        this.mProductId = parcel.readInt();
        this.mVersion = parcel.readInt();
        this.mPrimaryRecord = parcel.readBoolean();
        this.mClientExecutableUrl = parcel.readString();
        this.mServiceDescription = parcel.readString();
        this.mDocumentationUrl = parcel.readString();
    }

    public int getSpecificationId() {
        return this.mSpecificationId;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getVendorIdSource() {
        return this.mVendorIdSource;
    }

    public int getProductId() {
        return this.mProductId;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public boolean getPrimaryRecord() {
        return this.mPrimaryRecord;
    }

    public String getClientExecutableUrl() {
        return this.mClientExecutableUrl;
    }

    public String getServiceDescription() {
        return this.mServiceDescription;
    }

    public String getDocumentationUrl() {
        return this.mDocumentationUrl;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSpecificationId);
        parcel.writeInt(this.mVendorId);
        parcel.writeInt(this.mVendorIdSource);
        parcel.writeInt(this.mProductId);
        parcel.writeInt(this.mVersion);
        parcel.writeBoolean(this.mPrimaryRecord);
        parcel.writeString(this.mClientExecutableUrl);
        parcel.writeString(this.mServiceDescription);
        parcel.writeString(this.mDocumentationUrl);
    }
}
