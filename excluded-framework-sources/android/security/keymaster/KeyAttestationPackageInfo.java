package android.security.keymaster;

import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class KeyAttestationPackageInfo implements Parcelable {
    public static final Parcelable.Creator<KeyAttestationPackageInfo> CREATOR = new Parcelable.Creator<KeyAttestationPackageInfo>() { // from class: android.security.keymaster.KeyAttestationPackageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyAttestationPackageInfo createFromParcel(Parcel parcel) {
            return new KeyAttestationPackageInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyAttestationPackageInfo[] newArray(int i) {
            return new KeyAttestationPackageInfo[i];
        }
    };
    private final String mPackageName;
    private final Signature[] mPackageSignatures;
    private final long mPackageVersionCode;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public KeyAttestationPackageInfo(String str, long j, Signature[] signatureArr) {
        this.mPackageName = str;
        this.mPackageVersionCode = j;
        this.mPackageSignatures = signatureArr;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public long getPackageVersionCode() {
        return this.mPackageVersionCode;
    }

    public Signature[] getPackageSignatures() {
        return this.mPackageSignatures;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPackageName);
        parcel.writeLong(this.mPackageVersionCode);
        parcel.writeTypedArray(this.mPackageSignatures, i);
    }

    private KeyAttestationPackageInfo(Parcel parcel) {
        this.mPackageName = parcel.readString();
        this.mPackageVersionCode = parcel.readLong();
        this.mPackageSignatures = (Signature[]) parcel.createTypedArray(Signature.CREATOR);
    }
}
