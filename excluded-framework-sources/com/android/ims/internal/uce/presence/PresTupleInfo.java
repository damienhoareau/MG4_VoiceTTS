package com.android.ims.internal.uce.presence;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public class PresTupleInfo implements Parcelable {
    public static final Parcelable.Creator<PresTupleInfo> CREATOR = new Parcelable.Creator<PresTupleInfo>() { // from class: com.android.ims.internal.uce.presence.PresTupleInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PresTupleInfo createFromParcel(Parcel parcel) {
            return new PresTupleInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PresTupleInfo[] newArray(int i) {
            return new PresTupleInfo[i];
        }
    };
    private String mContactUri;
    private String mFeatureTag;
    private String mTimestamp;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getFeatureTag() {
        return this.mFeatureTag;
    }

    public void setFeatureTag(String str) {
        this.mFeatureTag = str;
    }

    public String getContactUri() {
        return this.mContactUri;
    }

    public void setContactUri(String str) {
        this.mContactUri = str;
    }

    public String getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(String str) {
        this.mTimestamp = str;
    }

    public PresTupleInfo() {
        this.mFeatureTag = "";
        this.mContactUri = "";
        this.mTimestamp = "";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mFeatureTag);
        parcel.writeString(this.mContactUri);
        parcel.writeString(this.mTimestamp);
    }

    private PresTupleInfo(Parcel parcel) {
        this.mFeatureTag = "";
        this.mContactUri = "";
        this.mTimestamp = "";
        readFromParcel(parcel);
    }

    public void readFromParcel(Parcel parcel) {
        this.mFeatureTag = parcel.readString();
        this.mContactUri = parcel.readString();
        this.mTimestamp = parcel.readString();
    }
}
