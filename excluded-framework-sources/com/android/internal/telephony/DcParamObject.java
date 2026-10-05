package com.android.internal.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public class DcParamObject implements Parcelable {
    public static final Parcelable.Creator<DcParamObject> CREATOR = new Parcelable.Creator<DcParamObject>() { // from class: com.android.internal.telephony.DcParamObject.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DcParamObject createFromParcel(Parcel parcel) {
            return new DcParamObject(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DcParamObject[] newArray(int i) {
            return new DcParamObject[i];
        }
    };
    private int mSubId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DcParamObject(int i) {
        this.mSubId = i;
    }

    public DcParamObject(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSubId);
    }

    private void readFromParcel(Parcel parcel) {
        this.mSubId = parcel.readInt();
    }

    public int getSubId() {
        return this.mSubId;
    }
}
