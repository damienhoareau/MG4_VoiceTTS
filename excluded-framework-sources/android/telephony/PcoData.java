package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class PcoData implements Parcelable {
    public static final Parcelable.Creator<PcoData> CREATOR = new Parcelable.Creator() { // from class: android.telephony.PcoData.1
        @Override // android.os.Parcelable.Creator
        public PcoData createFromParcel(Parcel parcel) {
            return new PcoData(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PcoData[] newArray(int i) {
            return new PcoData[i];
        }
    };
    public final String bearerProto;
    public final int cid;
    public final byte[] contents;
    public final int pcoId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PcoData(int i, String str, int i2, byte[] bArr) {
        this.cid = i;
        this.bearerProto = str;
        this.pcoId = i2;
        this.contents = bArr;
    }

    public PcoData(Parcel parcel) {
        this.cid = parcel.readInt();
        this.bearerProto = parcel.readString();
        this.pcoId = parcel.readInt();
        this.contents = parcel.createByteArray();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.cid);
        parcel.writeString(this.bearerProto);
        parcel.writeInt(this.pcoId);
        parcel.writeByteArray(this.contents);
    }

    public String toString() {
        return "PcoData(" + this.cid + ", " + this.bearerProto + ", " + this.pcoId + ", contents[" + this.contents.length + "])";
    }
}
