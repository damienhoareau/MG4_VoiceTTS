package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class ModemActivityInfo implements Parcelable {
    public static final Parcelable.Creator<ModemActivityInfo> CREATOR = new Parcelable.Creator<ModemActivityInfo>() { // from class: android.telephony.ModemActivityInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ModemActivityInfo createFromParcel(Parcel parcel) {
            long j = parcel.readLong();
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            int[] iArr = new int[5];
            for (int i3 = 0; i3 < 5; i3++) {
                iArr[i3] = parcel.readInt();
            }
            return new ModemActivityInfo(j, i, i2, iArr, parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ModemActivityInfo[] newArray(int i) {
            return new ModemActivityInfo[i];
        }
    };
    public static final int TX_POWER_LEVELS = 5;
    private int mEnergyUsed;
    private int mIdleTimeMs;
    private int mRxTimeMs;
    private int mSleepTimeMs;
    private long mTimestamp;
    private int[] mTxTimeMs;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ModemActivityInfo(long j, int i, int i2, int[] iArr, int i3, int i4) {
        int[] iArr2 = new int[5];
        this.mTxTimeMs = iArr2;
        this.mTimestamp = j;
        this.mSleepTimeMs = i;
        this.mIdleTimeMs = i2;
        if (iArr != null) {
            System.arraycopy(iArr, 0, iArr2, 0, Math.min(iArr.length, 5));
        }
        this.mRxTimeMs = i3;
        this.mEnergyUsed = i4;
    }

    public String toString() {
        return "ModemActivityInfo{ mTimestamp=" + this.mTimestamp + " mSleepTimeMs=" + this.mSleepTimeMs + " mIdleTimeMs=" + this.mIdleTimeMs + " mTxTimeMs[]=" + Arrays.toString(this.mTxTimeMs) + " mRxTimeMs=" + this.mRxTimeMs + " mEnergyUsed=" + this.mEnergyUsed + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mTimestamp);
        parcel.writeInt(this.mSleepTimeMs);
        parcel.writeInt(this.mIdleTimeMs);
        for (int i2 = 0; i2 < 5; i2++) {
            parcel.writeInt(this.mTxTimeMs[i2]);
        }
        parcel.writeInt(this.mRxTimeMs);
        parcel.writeInt(this.mEnergyUsed);
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(long j) {
        this.mTimestamp = j;
    }

    public int[] getTxTimeMillis() {
        return this.mTxTimeMs;
    }

    public void setTxTimeMillis(int[] iArr) {
        this.mTxTimeMs = iArr;
    }

    public int getSleepTimeMillis() {
        return this.mSleepTimeMs;
    }

    public void setSleepTimeMillis(int i) {
        this.mSleepTimeMs = i;
    }

    public int getIdleTimeMillis() {
        return this.mIdleTimeMs;
    }

    public void setIdleTimeMillis(int i) {
        this.mIdleTimeMs = i;
    }

    public int getRxTimeMillis() {
        return this.mRxTimeMs;
    }

    public void setRxTimeMillis(int i) {
        this.mRxTimeMs = i;
    }

    public int getEnergyUsed() {
        return this.mEnergyUsed;
    }

    public void setEnergyUsed(int i) {
        this.mEnergyUsed = i;
    }

    public boolean isValid() {
        for (int i : getTxTimeMillis()) {
            if (i < 0) {
                return false;
            }
        }
        return getIdleTimeMillis() >= 0 && getSleepTimeMillis() >= 0 && getRxTimeMillis() >= 0 && getEnergyUsed() >= 0 && !isEmpty();
    }

    private boolean isEmpty() {
        for (int i : getTxTimeMillis()) {
            if (i != 0) {
                return false;
            }
        }
        return getIdleTimeMillis() == 0 && getSleepTimeMillis() == 0 && getRxTimeMillis() == 0 && getEnergyUsed() == 0;
    }
}
