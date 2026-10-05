package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class HdmiPortInfo implements Parcelable {
    public static final Parcelable.Creator<HdmiPortInfo> CREATOR = new Parcelable.Creator<HdmiPortInfo>() { // from class: android.hardware.hdmi.HdmiPortInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HdmiPortInfo createFromParcel(Parcel parcel) {
            return new HdmiPortInfo(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() == 1, parcel.readInt() == 1, parcel.readInt() == 1);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HdmiPortInfo[] newArray(int i) {
            return new HdmiPortInfo[i];
        }
    };
    public static final int PORT_INPUT = 0;
    public static final int PORT_OUTPUT = 1;
    private final int mAddress;
    private final boolean mArcSupported;
    private final boolean mCecSupported;
    private final int mId;
    private final boolean mMhlSupported;
    private final int mType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public HdmiPortInfo(int i, int i2, int i3, boolean z, boolean z2, boolean z3) {
        this.mId = i;
        this.mType = i2;
        this.mAddress = i3;
        this.mCecSupported = z;
        this.mArcSupported = z3;
        this.mMhlSupported = z2;
    }

    public int getId() {
        return this.mId;
    }

    public int getType() {
        return this.mType;
    }

    public int getAddress() {
        return this.mAddress;
    }

    public boolean isCecSupported() {
        return this.mCecSupported;
    }

    public boolean isMhlSupported() {
        return this.mMhlSupported;
    }

    public boolean isArcSupported() {
        return this.mArcSupported;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mId);
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mAddress);
        parcel.writeInt(this.mCecSupported ? 1 : 0);
        parcel.writeInt(this.mArcSupported ? 1 : 0);
        parcel.writeInt(this.mMhlSupported ? 1 : 0);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("port_id: ");
        stringBuffer.append(this.mId);
        stringBuffer.append(", ");
        stringBuffer.append("address: ");
        stringBuffer.append(String.format("0x%04x", Integer.valueOf(this.mAddress)));
        stringBuffer.append(", ");
        stringBuffer.append("cec: ");
        stringBuffer.append(this.mCecSupported);
        stringBuffer.append(", ");
        stringBuffer.append("arc: ");
        stringBuffer.append(this.mArcSupported);
        stringBuffer.append(", ");
        stringBuffer.append("mhl: ");
        stringBuffer.append(this.mMhlSupported);
        return stringBuffer.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HdmiPortInfo)) {
            return false;
        }
        HdmiPortInfo hdmiPortInfo = (HdmiPortInfo) obj;
        return this.mId == hdmiPortInfo.mId && this.mType == hdmiPortInfo.mType && this.mAddress == hdmiPortInfo.mAddress && this.mCecSupported == hdmiPortInfo.mCecSupported && this.mArcSupported == hdmiPortInfo.mArcSupported && this.mMhlSupported == hdmiPortInfo.mMhlSupported;
    }
}
