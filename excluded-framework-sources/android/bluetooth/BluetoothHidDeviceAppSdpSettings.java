package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothHidDeviceAppSdpSettings implements Parcelable {
    public static final Parcelable.Creator<BluetoothHidDeviceAppSdpSettings> CREATOR = new Parcelable.Creator<BluetoothHidDeviceAppSdpSettings>() { // from class: android.bluetooth.BluetoothHidDeviceAppSdpSettings.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothHidDeviceAppSdpSettings createFromParcel(Parcel parcel) {
            return new BluetoothHidDeviceAppSdpSettings(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readByte(), parcel.createByteArray());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothHidDeviceAppSdpSettings[] newArray(int i) {
            return new BluetoothHidDeviceAppSdpSettings[i];
        }
    };
    private final String mDescription;
    private final byte[] mDescriptors;
    private final String mName;
    private final String mProvider;
    private final byte mSubclass;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BluetoothHidDeviceAppSdpSettings(String str, String str2, String str3, byte b, byte[] bArr) {
        this.mName = str;
        this.mDescription = str2;
        this.mProvider = str3;
        this.mSubclass = b;
        this.mDescriptors = (byte[]) bArr.clone();
    }

    public String getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getProvider() {
        return this.mProvider;
    }

    public byte getSubclass() {
        return this.mSubclass;
    }

    public byte[] getDescriptors() {
        return this.mDescriptors;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mProvider);
        parcel.writeByte(this.mSubclass);
        parcel.writeByteArray(this.mDescriptors);
    }
}
