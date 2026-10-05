package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CellIdentity implements Parcelable {
    public static final Parcelable.Creator<CellIdentity> CREATOR = new Parcelable.Creator<CellIdentity>() { // from class: android.telephony.CellIdentity.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentity createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            if (i == 1) {
                return CellIdentityGsm.createFromParcelBody(parcel);
            }
            if (i == 2) {
                return CellIdentityCdma.createFromParcelBody(parcel);
            }
            if (i == 3) {
                return CellIdentityLte.createFromParcelBody(parcel);
            }
            if (i == 4) {
                return CellIdentityWcdma.createFromParcelBody(parcel);
            }
            if (i == 5) {
                return CellIdentityTdscdma.createFromParcelBody(parcel);
            }
            throw new IllegalArgumentException("Bad Cell identity Parcel");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentity[] newArray(int i) {
            return new CellIdentity[i];
        }
    };
    public static final int INVALID_CHANNEL_NUMBER = -1;
    public static final int TYPE_CDMA = 2;
    public static final int TYPE_GSM = 1;
    public static final int TYPE_LTE = 3;
    public static final int TYPE_TDSCDMA = 5;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WCDMA = 4;
    protected final String mAlphaLong;
    protected final String mAlphaShort;
    protected final String mMccStr;
    protected final String mMncStr;
    protected final String mTag;
    protected final int mType;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getChannelNumber() {
        return -1;
    }

    protected CellIdentity(String str, int i, String str2, String str3, String str4, String str5) {
        this.mTag = str;
        this.mType = i;
        if (str2 == null || str2.matches("^[0-9]{3}$")) {
            this.mMccStr = str2;
        } else if (str2.isEmpty() || str2.equals(String.valueOf(Integer.MAX_VALUE))) {
            this.mMccStr = null;
        } else {
            this.mMccStr = null;
            log("invalid MCC format: " + str2);
        }
        if (str3 == null || str3.matches("^[0-9]{2,3}$")) {
            this.mMncStr = str3;
        } else if (str3.isEmpty() || str3.equals(String.valueOf(Integer.MAX_VALUE))) {
            this.mMncStr = null;
        } else {
            this.mMncStr = null;
            log("invalid MNC format: " + str3);
        }
        this.mAlphaLong = str4;
        this.mAlphaShort = str5;
    }

    public int getType() {
        return this.mType;
    }

    public CharSequence getOperatorAlphaLong() {
        return this.mAlphaLong;
    }

    public CharSequence getOperatorAlphaShort() {
        return this.mAlphaShort;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CellIdentity)) {
            return false;
        }
        CellIdentity cellIdentity = (CellIdentity) obj;
        return TextUtils.equals(this.mAlphaLong, cellIdentity.mAlphaLong) && TextUtils.equals(this.mAlphaShort, cellIdentity.mAlphaShort);
    }

    public int hashCode() {
        return Objects.hash(this.mAlphaLong, this.mAlphaShort, this.mMccStr, this.mMncStr, Integer.valueOf(this.mType));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(i);
        parcel.writeString(this.mMccStr);
        parcel.writeString(this.mMncStr);
        parcel.writeString(this.mAlphaLong);
        parcel.writeString(this.mAlphaShort);
    }

    protected CellIdentity(String str, int i, Parcel parcel) {
        this(str, i, parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
    }

    protected void log(String str) {
        Rlog.w(this.mTag, str);
    }
}
