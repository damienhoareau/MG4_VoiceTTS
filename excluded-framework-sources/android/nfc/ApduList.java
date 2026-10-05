package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ApduList implements Parcelable {
    public static final Parcelable.Creator<ApduList> CREATOR = new Parcelable.Creator<ApduList>() { // from class: android.nfc.ApduList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduList createFromParcel(Parcel parcel) {
            return new ApduList(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduList[] newArray(int i) {
            return new ApduList[i];
        }
    };
    private ArrayList<byte[]> commands;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ApduList() {
        this.commands = new ArrayList<>();
    }

    public void add(byte[] bArr) {
        this.commands.add(bArr);
    }

    public List<byte[]> get() {
        return this.commands;
    }

    private ApduList(Parcel parcel) {
        this.commands = new ArrayList<>();
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            byte[] bArr = new byte[parcel.readInt()];
            parcel.readByteArray(bArr);
            this.commands.add(bArr);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.commands.size());
        for (byte[] bArr : this.commands) {
            parcel.writeInt(bArr.length);
            parcel.writeByteArray(bArr);
        }
    }
}
