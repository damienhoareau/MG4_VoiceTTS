package android.telephony.ims;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class ImsSsInfo implements Parcelable {
    public static final Parcelable.Creator<ImsSsInfo> CREATOR = new Parcelable.Creator<ImsSsInfo>() { // from class: android.telephony.ims.ImsSsInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ImsSsInfo createFromParcel(Parcel parcel) {
            return new ImsSsInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ImsSsInfo[] newArray(int i) {
            return new ImsSsInfo[i];
        }
    };
    public static final int DISABLED = 0;
    public static final int ENABLED = 1;
    public static final int NOT_REGISTERED = -1;
    public String mIcbNum;
    public int mStatus;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ImsSsInfo() {
    }

    public ImsSsInfo(int i, String str) {
        this.mStatus = i;
        this.mIcbNum = str;
    }

    private ImsSsInfo(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mStatus);
        parcel.writeString(this.mIcbNum);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", Status: ");
        sb.append(this.mStatus == 0 ? "disabled" : "enabled");
        return sb.toString();
    }

    private void readFromParcel(Parcel parcel) {
        this.mStatus = parcel.readInt();
        this.mIcbNum = parcel.readString();
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String getIcbNum() {
        return this.mIcbNum;
    }
}
