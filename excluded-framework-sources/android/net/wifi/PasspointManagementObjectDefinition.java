package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class PasspointManagementObjectDefinition implements Parcelable {
    public static final Parcelable.Creator<PasspointManagementObjectDefinition> CREATOR = new Parcelable.Creator<PasspointManagementObjectDefinition>() { // from class: android.net.wifi.PasspointManagementObjectDefinition.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PasspointManagementObjectDefinition createFromParcel(Parcel parcel) {
            return new PasspointManagementObjectDefinition(parcel.readString(), parcel.readString(), parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PasspointManagementObjectDefinition[] newArray(int i) {
            return new PasspointManagementObjectDefinition[i];
        }
    };
    private final String mBaseUri;
    private final String mMoTree;
    private final String mUrn;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PasspointManagementObjectDefinition(String str, String str2, String str3) {
        this.mBaseUri = str;
        this.mUrn = str2;
        this.mMoTree = str3;
    }

    public String getBaseUri() {
        return this.mBaseUri;
    }

    public String getUrn() {
        return this.mUrn;
    }

    public String getMoTree() {
        return this.mMoTree;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mBaseUri);
        parcel.writeString(this.mUrn);
        parcel.writeString(this.mMoTree);
    }
}
