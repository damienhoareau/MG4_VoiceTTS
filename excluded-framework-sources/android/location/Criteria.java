package android.location;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class Criteria implements Parcelable {
    public static final int ACCURACY_COARSE = 2;
    public static final int ACCURACY_FINE = 1;
    public static final int ACCURACY_HIGH = 3;
    public static final int ACCURACY_LOW = 1;
    public static final int ACCURACY_MEDIUM = 2;
    public static final Parcelable.Creator<Criteria> CREATOR = new Parcelable.Creator<Criteria>() { // from class: android.location.Criteria.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Criteria createFromParcel(Parcel parcel) {
            Criteria criteria = new Criteria();
            criteria.mHorizontalAccuracy = parcel.readInt();
            criteria.mVerticalAccuracy = parcel.readInt();
            criteria.mSpeedAccuracy = parcel.readInt();
            criteria.mBearingAccuracy = parcel.readInt();
            criteria.mPowerRequirement = parcel.readInt();
            criteria.mAltitudeRequired = parcel.readInt() != 0;
            criteria.mBearingRequired = parcel.readInt() != 0;
            criteria.mSpeedRequired = parcel.readInt() != 0;
            criteria.mCostAllowed = parcel.readInt() != 0;
            return criteria;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Criteria[] newArray(int i) {
            return new Criteria[i];
        }
    };
    public static final int NO_REQUIREMENT = 0;
    public static final int POWER_HIGH = 3;
    public static final int POWER_LOW = 1;
    public static final int POWER_MEDIUM = 2;
    private boolean mAltitudeRequired;
    private int mBearingAccuracy;
    private boolean mBearingRequired;
    private boolean mCostAllowed;
    private int mHorizontalAccuracy;
    private int mPowerRequirement;
    private int mSpeedAccuracy;
    private boolean mSpeedRequired;
    private int mVerticalAccuracy;

    private static String accuracyToString(int i) {
        if (i == 0) {
            return "---";
        }
        if (i == 1) {
            return "LOW";
        }
        if (i != 2) {
            return i != 3 ? "???" : "HIGH";
        }
        return "MEDIUM";
    }

    private static String powerToString(int i) {
        if (i == 0) {
            return "NO_REQ";
        }
        if (i == 1) {
            return "LOW";
        }
        if (i != 2) {
            return i != 3 ? "???" : "HIGH";
        }
        return "MEDIUM";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Criteria() {
        this.mHorizontalAccuracy = 0;
        this.mVerticalAccuracy = 0;
        this.mSpeedAccuracy = 0;
        this.mBearingAccuracy = 0;
        this.mPowerRequirement = 0;
        this.mAltitudeRequired = false;
        this.mBearingRequired = false;
        this.mSpeedRequired = false;
        this.mCostAllowed = false;
    }

    public Criteria(Criteria criteria) {
        this.mHorizontalAccuracy = 0;
        this.mVerticalAccuracy = 0;
        this.mSpeedAccuracy = 0;
        this.mBearingAccuracy = 0;
        this.mPowerRequirement = 0;
        this.mAltitudeRequired = false;
        this.mBearingRequired = false;
        this.mSpeedRequired = false;
        this.mCostAllowed = false;
        this.mHorizontalAccuracy = criteria.mHorizontalAccuracy;
        this.mVerticalAccuracy = criteria.mVerticalAccuracy;
        this.mSpeedAccuracy = criteria.mSpeedAccuracy;
        this.mBearingAccuracy = criteria.mBearingAccuracy;
        this.mPowerRequirement = criteria.mPowerRequirement;
        this.mAltitudeRequired = criteria.mAltitudeRequired;
        this.mBearingRequired = criteria.mBearingRequired;
        this.mSpeedRequired = criteria.mSpeedRequired;
        this.mCostAllowed = criteria.mCostAllowed;
    }

    public void setHorizontalAccuracy(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("accuracy=" + i);
        }
        this.mHorizontalAccuracy = i;
    }

    public int getHorizontalAccuracy() {
        return this.mHorizontalAccuracy;
    }

    public void setVerticalAccuracy(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("accuracy=" + i);
        }
        this.mVerticalAccuracy = i;
    }

    public int getVerticalAccuracy() {
        return this.mVerticalAccuracy;
    }

    public void setSpeedAccuracy(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("accuracy=" + i);
        }
        this.mSpeedAccuracy = i;
    }

    public int getSpeedAccuracy() {
        return this.mSpeedAccuracy;
    }

    public void setBearingAccuracy(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("accuracy=" + i);
        }
        this.mBearingAccuracy = i;
    }

    public int getBearingAccuracy() {
        return this.mBearingAccuracy;
    }

    public void setAccuracy(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("accuracy=" + i);
        }
        if (i == 1) {
            this.mHorizontalAccuracy = 3;
        } else {
            this.mHorizontalAccuracy = 1;
        }
    }

    public int getAccuracy() {
        return this.mHorizontalAccuracy >= 3 ? 1 : 2;
    }

    public void setPowerRequirement(int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("level=" + i);
        }
        this.mPowerRequirement = i;
    }

    public int getPowerRequirement() {
        return this.mPowerRequirement;
    }

    public void setCostAllowed(boolean z) {
        this.mCostAllowed = z;
    }

    public boolean isCostAllowed() {
        return this.mCostAllowed;
    }

    public void setAltitudeRequired(boolean z) {
        this.mAltitudeRequired = z;
    }

    public boolean isAltitudeRequired() {
        return this.mAltitudeRequired;
    }

    public void setSpeedRequired(boolean z) {
        this.mSpeedRequired = z;
    }

    public boolean isSpeedRequired() {
        return this.mSpeedRequired;
    }

    public void setBearingRequired(boolean z) {
        this.mBearingRequired = z;
    }

    public boolean isBearingRequired() {
        return this.mBearingRequired;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mHorizontalAccuracy);
        parcel.writeInt(this.mVerticalAccuracy);
        parcel.writeInt(this.mSpeedAccuracy);
        parcel.writeInt(this.mBearingAccuracy);
        parcel.writeInt(this.mPowerRequirement);
        parcel.writeInt(this.mAltitudeRequired ? 1 : 0);
        parcel.writeInt(this.mBearingRequired ? 1 : 0);
        parcel.writeInt(this.mSpeedRequired ? 1 : 0);
        parcel.writeInt(this.mCostAllowed ? 1 : 0);
    }

    public String toString() {
        return "Criteria[power=" + powerToString(this.mPowerRequirement) + " acc=" + accuracyToString(this.mHorizontalAccuracy) + ']';
    }
}
