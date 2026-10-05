package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public final class CellSignalStrengthCdma extends CellSignalStrength implements Parcelable {
    public static final Parcelable.Creator<CellSignalStrengthCdma> CREATOR = new Parcelable.Creator<CellSignalStrengthCdma>() { // from class: android.telephony.CellSignalStrengthCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma createFromParcel(Parcel parcel) {
            return new CellSignalStrengthCdma(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma[] newArray(int i) {
            return new CellSignalStrengthCdma[i];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellSignalStrengthCdma";
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CellSignalStrengthCdma() {
        setDefaultValues();
    }

    public CellSignalStrengthCdma(int i, int i2, int i3, int i4, int i5) {
        this.mCdmaDbm = (i <= 0 || i >= 120) ? Integer.MAX_VALUE : -i;
        this.mCdmaEcio = (i2 <= 0 || i2 >= 160) ? Integer.MAX_VALUE : -i2;
        this.mEvdoDbm = (i3 <= 0 || i3 >= 120) ? Integer.MAX_VALUE : -i3;
        this.mEvdoEcio = (i4 <= 0 || i4 >= 160) ? Integer.MAX_VALUE : -i4;
        this.mEvdoSnr = (i5 <= 0 || i5 > 8) ? Integer.MAX_VALUE : i5;
    }

    public CellSignalStrengthCdma(CellSignalStrengthCdma cellSignalStrengthCdma) {
        copyFrom(cellSignalStrengthCdma);
    }

    protected void copyFrom(CellSignalStrengthCdma cellSignalStrengthCdma) {
        this.mCdmaDbm = cellSignalStrengthCdma.mCdmaDbm;
        this.mCdmaEcio = cellSignalStrengthCdma.mCdmaEcio;
        this.mEvdoDbm = cellSignalStrengthCdma.mEvdoDbm;
        this.mEvdoEcio = cellSignalStrengthCdma.mEvdoEcio;
        this.mEvdoSnr = cellSignalStrengthCdma.mEvdoSnr;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthCdma copy() {
        return new CellSignalStrengthCdma(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mCdmaDbm = Integer.MAX_VALUE;
        this.mCdmaEcio = Integer.MAX_VALUE;
        this.mEvdoDbm = Integer.MAX_VALUE;
        this.mEvdoEcio = Integer.MAX_VALUE;
        this.mEvdoSnr = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        if (evdoLevel == 0) {
            return getCdmaLevel();
        }
        if (cdmaLevel == 0) {
            return getEvdoLevel();
        }
        return cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0037  */
    /* JADX WARN: Code duplicated, block: B:4:0x001a  */
    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int i;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i2 = 1;
        if (cdmaDbm == Integer.MAX_VALUE) {
            i = 99;
        } else if (cdmaDbm >= -75) {
            i = 16;
        } else if (cdmaDbm >= -82) {
            i = 8;
        } else if (cdmaDbm >= -90) {
            i = 4;
        } else if (cdmaDbm >= -95) {
            i = 2;
        } else if (cdmaDbm >= -100) {
            i = 1;
        } else {
            i = 99;
        }
        if (cdmaEcio == Integer.MAX_VALUE) {
            i2 = 99;
        } else if (cdmaEcio >= -90) {
            i2 = 16;
        } else if (cdmaEcio >= -100) {
            i2 = 8;
        } else if (cdmaEcio >= -115) {
            i2 = 4;
        } else if (cdmaEcio >= -130) {
            i2 = 2;
        } else if (cdmaEcio < -150) {
            i2 = 99;
        }
        return i < i2 ? i : i2;
    }

    /* JADX WARN: Code duplicated, block: B:18:0x002d  */
    /* JADX WARN: Code duplicated, block: B:4:0x0012  */
    public int getCdmaLevel() {
        int i;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i2 = 1;
        if (cdmaDbm == Integer.MAX_VALUE) {
            i = 0;
        } else if (cdmaDbm >= -75) {
            i = 4;
        } else if (cdmaDbm >= -85) {
            i = 3;
        } else if (cdmaDbm >= -95) {
            i = 2;
        } else if (cdmaDbm >= -100) {
            i = 1;
        } else {
            i = 0;
        }
        if (cdmaEcio == Integer.MAX_VALUE) {
            i2 = 0;
        } else if (cdmaEcio >= -90) {
            i2 = 4;
        } else if (cdmaEcio >= -110) {
            i2 = 3;
        } else if (cdmaEcio >= -130) {
            i2 = 2;
        } else if (cdmaEcio < -150) {
            i2 = 0;
        }
        return i < i2 ? i : i2;
    }

    /* JADX WARN: Code duplicated, block: B:18:0x002d  */
    /* JADX WARN: Code duplicated, block: B:4:0x0012  */
    public int getEvdoLevel() {
        int i;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        int i2 = 2;
        if (evdoDbm == Integer.MAX_VALUE) {
            i = 0;
        } else if (evdoDbm >= -65) {
            i = 4;
        } else if (evdoDbm >= -75) {
            i = 3;
        } else if (evdoDbm >= -90) {
            i = 2;
        } else if (evdoDbm >= -105) {
            i = 1;
        } else {
            i = 0;
        }
        if (evdoSnr == Integer.MAX_VALUE) {
            i2 = 0;
        } else if (evdoSnr >= 7) {
            i2 = 4;
        } else if (evdoSnr >= 5) {
            i2 = 3;
        } else if (evdoSnr < 3) {
            if (evdoSnr >= 1) {
                i2 = 1;
            } else {
                i2 = 0;
            }
        }
        return i < i2 ? i : i2;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        return cdmaDbm < evdoDbm ? cdmaDbm : evdoDbm;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public void setCdmaDbm(int i) {
        this.mCdmaDbm = i;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public void setCdmaEcio(int i) {
        this.mCdmaEcio = i;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public void setEvdoDbm(int i) {
        this.mEvdoDbm = i;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public void setEvdoEcio(int i) {
        this.mEvdoEcio = i;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public void setEvdoSnr(int i) {
        this.mEvdoSnr = i;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mCdmaDbm), Integer.valueOf(this.mCdmaEcio), Integer.valueOf(this.mEvdoDbm), Integer.valueOf(this.mEvdoEcio), Integer.valueOf(this.mEvdoSnr));
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object obj) {
        try {
            CellSignalStrengthCdma cellSignalStrengthCdma = (CellSignalStrengthCdma) obj;
            return obj != null && this.mCdmaDbm == cellSignalStrengthCdma.mCdmaDbm && this.mCdmaEcio == cellSignalStrengthCdma.mCdmaEcio && this.mEvdoDbm == cellSignalStrengthCdma.mEvdoDbm && this.mEvdoEcio == cellSignalStrengthCdma.mEvdoEcio && this.mEvdoSnr == cellSignalStrengthCdma.mEvdoSnr;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthCdma: cdmaDbm=" + this.mCdmaDbm + " cdmaEcio=" + this.mCdmaEcio + " evdoDbm=" + this.mEvdoDbm + " evdoEcio=" + this.mEvdoEcio + " evdoSnr=" + this.mEvdoSnr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mCdmaDbm);
        parcel.writeInt(this.mCdmaEcio);
        parcel.writeInt(this.mEvdoDbm);
        parcel.writeInt(this.mEvdoEcio);
        parcel.writeInt(this.mEvdoSnr);
    }

    private CellSignalStrengthCdma(Parcel parcel) {
        this.mCdmaDbm = parcel.readInt();
        this.mCdmaEcio = parcel.readInt();
        this.mEvdoDbm = parcel.readInt();
        this.mEvdoEcio = parcel.readInt();
        this.mEvdoSnr = parcel.readInt();
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
