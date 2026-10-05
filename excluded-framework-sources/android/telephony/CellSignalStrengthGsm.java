package android.telephony;

import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public final class CellSignalStrengthGsm extends CellSignalStrength implements Parcelable {
    public static final Parcelable.Creator<CellSignalStrengthGsm> CREATOR = new Parcelable.Creator<CellSignalStrengthGsm>() { // from class: android.telephony.CellSignalStrengthGsm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthGsm createFromParcel(Parcel parcel) {
            return new CellSignalStrengthGsm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthGsm[] newArray(int i) {
            return new CellSignalStrengthGsm[i];
        }
    };
    private static final boolean DBG = false;
    private static final int GSM_SIGNAL_STRENGTH_GOOD = 8;
    private static final int GSM_SIGNAL_STRENGTH_GREAT = 12;
    private static final int GSM_SIGNAL_STRENGTH_MODERATE = 5;
    private static final String LOG_TAG = "CellSignalStrengthGsm";
    private int mBitErrorRate;
    private int mSignalStrength;
    private int mTimingAdvance;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CellSignalStrengthGsm() {
        setDefaultValues();
    }

    public CellSignalStrengthGsm(int i, int i2) {
        this(i, i2, Integer.MAX_VALUE);
    }

    public CellSignalStrengthGsm(int i, int i2, int i3) {
        this.mSignalStrength = i;
        this.mBitErrorRate = i2;
        this.mTimingAdvance = i3;
    }

    public CellSignalStrengthGsm(CellSignalStrengthGsm cellSignalStrengthGsm) {
        copyFrom(cellSignalStrengthGsm);
    }

    protected void copyFrom(CellSignalStrengthGsm cellSignalStrengthGsm) {
        this.mSignalStrength = cellSignalStrengthGsm.mSignalStrength;
        this.mBitErrorRate = cellSignalStrengthGsm.mBitErrorRate;
        this.mTimingAdvance = cellSignalStrengthGsm.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthGsm copy() {
        return new CellSignalStrengthGsm(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mSignalStrength = Integer.MAX_VALUE;
        this.mBitErrorRate = Integer.MAX_VALUE;
        this.mTimingAdvance = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int i = this.mSignalStrength;
        if (i <= 2 || i == 99) {
            return 0;
        }
        if (i >= 12) {
            return 4;
        }
        if (i >= 8) {
            return 3;
        }
        return i >= 5 ? 2 : 1;
    }

    public int getTimingAdvance() {
        return this.mTimingAdvance;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int i = this.mSignalStrength;
        if (i == 99) {
            i = Integer.MAX_VALUE;
        }
        if (i != Integer.MAX_VALUE) {
            return (i * 2) + PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS;
        }
        return Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        return this.mSignalStrength;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSignalStrength), Integer.valueOf(this.mBitErrorRate), Integer.valueOf(this.mTimingAdvance));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object obj) {
        try {
            CellSignalStrengthGsm cellSignalStrengthGsm = (CellSignalStrengthGsm) obj;
            return obj != null && this.mSignalStrength == cellSignalStrengthGsm.mSignalStrength && this.mBitErrorRate == cellSignalStrengthGsm.mBitErrorRate && cellSignalStrengthGsm.mTimingAdvance == this.mTimingAdvance;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthGsm: ss=" + this.mSignalStrength + " ber=" + this.mBitErrorRate + " mTa=" + this.mTimingAdvance;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSignalStrength);
        parcel.writeInt(this.mBitErrorRate);
        parcel.writeInt(this.mTimingAdvance);
    }

    private CellSignalStrengthGsm(Parcel parcel) {
        this.mSignalStrength = parcel.readInt();
        this.mBitErrorRate = parcel.readInt();
        this.mTimingAdvance = parcel.readInt();
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
