package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class KeyAttestationApplicationId implements Parcelable {
    public static final Parcelable.Creator<KeyAttestationApplicationId> CREATOR = new Parcelable.Creator<KeyAttestationApplicationId>() { // from class: android.security.keymaster.KeyAttestationApplicationId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyAttestationApplicationId createFromParcel(Parcel parcel) {
            return new KeyAttestationApplicationId(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyAttestationApplicationId[] newArray(int i) {
            return new KeyAttestationApplicationId[i];
        }
    };
    private final KeyAttestationPackageInfo[] mAttestationPackageInfos;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public KeyAttestationApplicationId(KeyAttestationPackageInfo[] keyAttestationPackageInfoArr) {
        this.mAttestationPackageInfos = keyAttestationPackageInfoArr;
    }

    public KeyAttestationPackageInfo[] getAttestationPackageInfos() {
        return this.mAttestationPackageInfos;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedArray(this.mAttestationPackageInfos, i);
    }

    KeyAttestationApplicationId(Parcel parcel) {
        this.mAttestationPackageInfos = (KeyAttestationPackageInfo[]) parcel.createTypedArray(KeyAttestationPackageInfo.CREATOR);
    }
}
