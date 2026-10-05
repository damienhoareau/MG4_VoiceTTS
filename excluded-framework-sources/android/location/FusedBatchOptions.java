package android.location;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class FusedBatchOptions implements Parcelable {
    public static final Parcelable.Creator<FusedBatchOptions> CREATOR = new Parcelable.Creator<FusedBatchOptions>() { // from class: android.location.FusedBatchOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FusedBatchOptions createFromParcel(Parcel parcel) {
            FusedBatchOptions fusedBatchOptions = new FusedBatchOptions();
            fusedBatchOptions.setMaxPowerAllocationInMW(parcel.readDouble());
            fusedBatchOptions.setPeriodInNS(parcel.readLong());
            fusedBatchOptions.setSourceToUse(parcel.readInt());
            fusedBatchOptions.setFlag(parcel.readInt());
            fusedBatchOptions.setSmallestDisplacementMeters(parcel.readFloat());
            return fusedBatchOptions;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FusedBatchOptions[] newArray(int i) {
            return new FusedBatchOptions[i];
        }
    };
    private volatile long mPeriodInNS = 0;
    private volatile int mSourcesToUse = 0;
    private volatile int mFlags = 0;
    private volatile double mMaxPowerAllocationInMW = 0.0d;
    private volatile float mSmallestDisplacementMeters = 0.0f;

    public static final class BatchFlags {
        public static int CALLBACK_ON_LOCATION_FIX = 2;
        public static int WAKEUP_ON_FIFO_FULL = 1;
    }

    public static final class SourceTechnologies {
        public static int BLUETOOTH = 16;
        public static int CELL = 8;
        public static int GNSS = 1;
        public static int SENSORS = 4;
        public static int WIFI = 2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setMaxPowerAllocationInMW(double d) {
        this.mMaxPowerAllocationInMW = d;
    }

    public double getMaxPowerAllocationInMW() {
        return this.mMaxPowerAllocationInMW;
    }

    public void setPeriodInNS(long j) {
        this.mPeriodInNS = j;
    }

    public long getPeriodInNS() {
        return this.mPeriodInNS;
    }

    public void setSmallestDisplacementMeters(float f) {
        this.mSmallestDisplacementMeters = f;
    }

    public float getSmallestDisplacementMeters() {
        return this.mSmallestDisplacementMeters;
    }

    public void setSourceToUse(int i) {
        this.mSourcesToUse = i | this.mSourcesToUse;
    }

    public void resetSourceToUse(int i) {
        this.mSourcesToUse = (~i) & this.mSourcesToUse;
    }

    public boolean isSourceToUseSet(int i) {
        return (i & this.mSourcesToUse) != 0;
    }

    public int getSourcesToUse() {
        return this.mSourcesToUse;
    }

    public void setFlag(int i) {
        this.mFlags = i | this.mFlags;
    }

    public void resetFlag(int i) {
        this.mFlags = (~i) & this.mFlags;
    }

    public boolean isFlagSet(int i) {
        return (i & this.mFlags) != 0;
    }

    public int getFlags() {
        return this.mFlags;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.mMaxPowerAllocationInMW);
        parcel.writeLong(this.mPeriodInNS);
        parcel.writeInt(this.mSourcesToUse);
        parcel.writeInt(this.mFlags);
        parcel.writeFloat(this.mSmallestDisplacementMeters);
    }
}
