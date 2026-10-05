package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class TechListParcel implements Parcelable {
    public static final Parcelable.Creator<TechListParcel> CREATOR = new Parcelable.Creator<TechListParcel>() { // from class: android.nfc.TechListParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TechListParcel createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            String[][] strArr = new String[i][];
            for (int i2 = 0; i2 < i; i2++) {
                strArr[i2] = parcel.readStringArray();
            }
            return new TechListParcel(strArr);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TechListParcel[] newArray(int i) {
            return new TechListParcel[i];
        }
    };
    private String[][] mTechLists;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TechListParcel(String[]... strArr) {
        this.mTechLists = strArr;
    }

    public String[][] getTechLists() {
        return this.mTechLists;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        int length = this.mTechLists.length;
        parcel.writeInt(length);
        for (int i2 = 0; i2 < length; i2++) {
            parcel.writeStringArray(this.mTechLists[i2]);
        }
    }
}
