package android.telephony;

import android.content.pm.PackageManager;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class SignalStrength implements Parcelable {
    private static final boolean DBG = false;
    public static final int INVALID = Integer.MAX_VALUE;
    private static final String LOG_TAG = "SignalStrength";
    private static final int LTE_RSRP_THRESHOLDS_NUM = 4;
    private static final int MAX_LTE_RSRP = -44;
    private static final int MAX_WCDMA_RSCP = -24;
    private static final String MEASUMENT_TYPE_RSCP = "rscp";
    private static final int MIN_LTE_RSRP = -140;
    private static final int MIN_WCDMA_RSCP = -120;
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    private static final int WCDMA_RSCP_THRESHOLDS_NUM = 4;
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;
    private int mGsmBitErrorRate;
    private int mGsmSignalStrength;
    private boolean mIsGsm;
    private int mLteCqi;
    private int mLteRsrp;
    private int mLteRsrpBoost;
    private int[] mLteRsrpThresholds;
    private int mLteRsrq;
    private int mLteRssnr;
    private int mLteSignalStrength;
    private int mTdScdmaRscp;
    private boolean mUseOnlyRsrpForLteLevel;
    private String mWcdmaDefaultSignalMeasurement;
    private int mWcdmaRscp;
    private int mWcdmaRscpAsu;
    private int[] mWcdmaRscpThresholds;
    private int mWcdmaSignalStrength;
    public static final String[] SIGNAL_STRENGTH_NAMES = {"none", "poor", "moderate", "good", "great"};
    public static final Parcelable.Creator<SignalStrength> CREATOR = new Parcelable.Creator() { // from class: android.telephony.SignalStrength.1
        @Override // android.os.Parcelable.Creator
        public SignalStrength createFromParcel(Parcel parcel) {
            return new SignalStrength(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SignalStrength[] newArray(int i) {
            return new SignalStrength[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static SignalStrength newFromBundle(Bundle bundle) {
        SignalStrength signalStrength = new SignalStrength();
        signalStrength.setFromNotifierBundle(bundle);
        return signalStrength;
    }

    public SignalStrength() {
        this(true);
    }

    public SignalStrength(boolean z) {
        this.mLteRsrpThresholds = new int[4];
        this.mWcdmaRscpThresholds = new int[4];
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = Integer.MAX_VALUE;
        this.mLteRsrq = Integer.MAX_VALUE;
        this.mLteRssnr = Integer.MAX_VALUE;
        this.mLteCqi = Integer.MAX_VALUE;
        this.mTdScdmaRscp = Integer.MAX_VALUE;
        this.mWcdmaSignalStrength = 99;
        this.mWcdmaRscp = Integer.MAX_VALUE;
        this.mWcdmaRscpAsu = 255;
        this.mLteRsrpBoost = 0;
        this.mIsGsm = z;
        this.mUseOnlyRsrpForLteLevel = false;
        this.mWcdmaDefaultSignalMeasurement = "";
        setLteRsrpThresholds(getDefaultLteRsrpThresholds());
        setWcdmaRscpThresholds(getDefaultWcdmaRscpThresholds());
    }

    public SignalStrength(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, boolean z, boolean z2, String str) {
        this.mLteRsrpThresholds = new int[4];
        this.mWcdmaRscpThresholds = new int[4];
        this.mGsmSignalStrength = i;
        this.mGsmBitErrorRate = i2;
        this.mCdmaDbm = i3;
        this.mCdmaEcio = i4;
        this.mEvdoDbm = i5;
        this.mEvdoEcio = i6;
        this.mEvdoSnr = i7;
        this.mLteSignalStrength = i8;
        this.mLteRsrp = i9;
        this.mLteRsrq = i10;
        this.mLteRssnr = i11;
        this.mLteCqi = i12;
        this.mTdScdmaRscp = Integer.MAX_VALUE;
        this.mWcdmaSignalStrength = i14;
        this.mWcdmaRscpAsu = i15;
        this.mWcdmaRscp = i15 + MIN_WCDMA_RSCP;
        this.mLteRsrpBoost = i16;
        this.mIsGsm = z;
        this.mUseOnlyRsrpForLteLevel = z2;
        this.mWcdmaDefaultSignalMeasurement = str;
        setLteRsrpThresholds(getDefaultLteRsrpThresholds());
        setWcdmaRscpThresholds(getDefaultWcdmaRscpThresholds());
    }

    public SignalStrength(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13) {
        this(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, 99, Integer.MAX_VALUE, 0, true, false, "");
    }

    public SignalStrength(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15) {
        this(i, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, 0, true, false, "");
    }

    public SignalStrength(SignalStrength signalStrength) {
        this.mLteRsrpThresholds = new int[4];
        this.mWcdmaRscpThresholds = new int[4];
        copyFrom(signalStrength);
    }

    protected void copyFrom(SignalStrength signalStrength) {
        this.mGsmSignalStrength = signalStrength.mGsmSignalStrength;
        this.mGsmBitErrorRate = signalStrength.mGsmBitErrorRate;
        this.mCdmaDbm = signalStrength.mCdmaDbm;
        this.mCdmaEcio = signalStrength.mCdmaEcio;
        this.mEvdoDbm = signalStrength.mEvdoDbm;
        this.mEvdoEcio = signalStrength.mEvdoEcio;
        this.mEvdoSnr = signalStrength.mEvdoSnr;
        this.mLteSignalStrength = signalStrength.mLteSignalStrength;
        this.mLteRsrp = signalStrength.mLteRsrp;
        this.mLteRsrq = signalStrength.mLteRsrq;
        this.mLteRssnr = signalStrength.mLteRssnr;
        this.mLteCqi = signalStrength.mLteCqi;
        this.mTdScdmaRscp = signalStrength.mTdScdmaRscp;
        this.mWcdmaSignalStrength = signalStrength.mWcdmaSignalStrength;
        this.mWcdmaRscpAsu = signalStrength.mWcdmaRscpAsu;
        this.mWcdmaRscp = signalStrength.mWcdmaRscp;
        this.mLteRsrpBoost = signalStrength.mLteRsrpBoost;
        this.mIsGsm = signalStrength.mIsGsm;
        this.mUseOnlyRsrpForLteLevel = signalStrength.mUseOnlyRsrpForLteLevel;
        this.mWcdmaDefaultSignalMeasurement = signalStrength.mWcdmaDefaultSignalMeasurement;
        setLteRsrpThresholds(signalStrength.mLteRsrpThresholds);
        setWcdmaRscpThresholds(signalStrength.mWcdmaRscpThresholds);
    }

    public SignalStrength(Parcel parcel) {
        this.mLteRsrpThresholds = new int[4];
        this.mWcdmaRscpThresholds = new int[4];
        this.mGsmSignalStrength = parcel.readInt();
        this.mGsmBitErrorRate = parcel.readInt();
        this.mCdmaDbm = parcel.readInt();
        this.mCdmaEcio = parcel.readInt();
        this.mEvdoDbm = parcel.readInt();
        this.mEvdoEcio = parcel.readInt();
        this.mEvdoSnr = parcel.readInt();
        this.mLteSignalStrength = parcel.readInt();
        this.mLteRsrp = parcel.readInt();
        this.mLteRsrq = parcel.readInt();
        this.mLteRssnr = parcel.readInt();
        this.mLteCqi = parcel.readInt();
        this.mTdScdmaRscp = parcel.readInt();
        this.mWcdmaSignalStrength = parcel.readInt();
        this.mWcdmaRscpAsu = parcel.readInt();
        this.mWcdmaRscp = parcel.readInt();
        this.mLteRsrpBoost = parcel.readInt();
        this.mIsGsm = parcel.readBoolean();
        this.mUseOnlyRsrpForLteLevel = parcel.readBoolean();
        this.mWcdmaDefaultSignalMeasurement = parcel.readString();
        parcel.readIntArray(this.mLteRsrpThresholds);
        parcel.readIntArray(this.mWcdmaRscpThresholds);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mGsmSignalStrength);
        parcel.writeInt(this.mGsmBitErrorRate);
        parcel.writeInt(this.mCdmaDbm);
        parcel.writeInt(this.mCdmaEcio);
        parcel.writeInt(this.mEvdoDbm);
        parcel.writeInt(this.mEvdoEcio);
        parcel.writeInt(this.mEvdoSnr);
        parcel.writeInt(this.mLteSignalStrength);
        parcel.writeInt(this.mLteRsrp);
        parcel.writeInt(this.mLteRsrq);
        parcel.writeInt(this.mLteRssnr);
        parcel.writeInt(this.mLteCqi);
        parcel.writeInt(this.mTdScdmaRscp);
        parcel.writeInt(this.mWcdmaSignalStrength);
        parcel.writeInt(this.mWcdmaRscpAsu);
        parcel.writeInt(this.mWcdmaRscp);
        parcel.writeInt(this.mLteRsrpBoost);
        parcel.writeBoolean(this.mIsGsm);
        parcel.writeBoolean(this.mUseOnlyRsrpForLteLevel);
        parcel.writeString(this.mWcdmaDefaultSignalMeasurement);
        parcel.writeIntArray(this.mLteRsrpThresholds);
        parcel.writeIntArray(this.mWcdmaRscpThresholds);
    }

    public void validateInput() {
        int i = this.mGsmSignalStrength;
        if (i < 0) {
            i = 99;
        }
        this.mGsmSignalStrength = i;
        int i2 = this.mWcdmaSignalStrength;
        if (i2 < 0) {
            i2 = 99;
        }
        this.mWcdmaSignalStrength = i2;
        int i3 = this.mLteSignalStrength;
        this.mLteSignalStrength = i3 >= 0 ? i3 : 99;
        int i4 = this.mWcdmaRscpAsu;
        int i5 = i4 + MIN_WCDMA_RSCP;
        int i6 = MIN_WCDMA_RSCP;
        if (i5 < MIN_WCDMA_RSCP || i4 + MIN_WCDMA_RSCP > -24) {
            i4 = 255;
        }
        this.mWcdmaRscpAsu = i4;
        int i7 = this.mWcdmaRscp;
        int i8 = Integer.MAX_VALUE;
        if (i7 < MIN_WCDMA_RSCP || i7 > -24) {
            i7 = Integer.MAX_VALUE;
        }
        this.mWcdmaRscp = i7;
        int i9 = this.mCdmaDbm;
        this.mCdmaDbm = i9 > 0 ? -i9 : MIN_WCDMA_RSCP;
        int i10 = this.mCdmaEcio;
        this.mCdmaEcio = i10 >= 0 ? -i10 : -160;
        int i11 = this.mEvdoDbm;
        if (i11 > 0) {
            i6 = -i11;
        }
        this.mEvdoDbm = i6;
        int i12 = this.mEvdoEcio;
        this.mEvdoEcio = i12 >= 0 ? -i12 : -160;
        int i13 = this.mEvdoSnr;
        if (i13 < 0 || i13 > 8) {
            i13 = -1;
        }
        this.mEvdoSnr = i13;
        int i14 = this.mLteRsrp;
        this.mLteRsrp = ((-i14) < MIN_LTE_RSRP || (-i14) > -44) ? Integer.MAX_VALUE : -i14;
        int i15 = this.mLteRsrq;
        this.mLteRsrq = (i15 < 3 || i15 > 20) ? Integer.MAX_VALUE : -i15;
        int i16 = this.mLteRssnr;
        if (i16 < -200 || i16 > 300) {
            i16 = Integer.MAX_VALUE;
        }
        this.mLteRssnr = i16;
        int i17 = this.mTdScdmaRscp;
        if (i17 >= 0 && i17 <= 96) {
            i8 = i17 + MIN_WCDMA_RSCP;
        }
        this.mTdScdmaRscp = i8;
    }

    public void fixType() {
        this.mIsGsm = getCdmaRelatedSignalStrength() == 0;
    }

    public void setGsm(boolean z) {
        this.mIsGsm = z;
    }

    public void setUseOnlyRsrpForLteLevel(boolean z) {
        this.mUseOnlyRsrpForLteLevel = z;
    }

    public void setWcdmaDefaultSignalMeasurement(String str) {
        this.mWcdmaDefaultSignalMeasurement = str;
    }

    public void setLteRsrpBoost(int i) {
        this.mLteRsrpBoost = i;
    }

    public void setLteRsrpThresholds(int[] iArr) {
        if (iArr == null || iArr.length != 4) {
            Log.wtf(LOG_TAG, "setLteRsrpThresholds - lteRsrpThresholds is invalid.");
        } else {
            System.arraycopy(iArr, 0, this.mLteRsrpThresholds, 0, 4);
        }
    }

    public int getGsmSignalStrength() {
        return this.mGsmSignalStrength;
    }

    public int getGsmBitErrorRate() {
        return this.mGsmBitErrorRate;
    }

    public void setWcdmaRscpThresholds(int[] iArr) {
        if (iArr == null || iArr.length != 4) {
            Log.wtf(LOG_TAG, "setWcdmaRscpThresholds - wcdmaRscpThresholds is invalid.");
        } else {
            System.arraycopy(iArr, 0, this.mWcdmaRscpThresholds, 0, 4);
        }
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public int getLteSignalStrength() {
        return this.mLteSignalStrength;
    }

    public int getLteRsrp() {
        return this.mLteRsrp;
    }

    public int getLteRsrq() {
        return this.mLteRsrq;
    }

    public int getLteRssnr() {
        return this.mLteRssnr;
    }

    public int getLteCqi() {
        return this.mLteCqi;
    }

    public int getLteRsrpBoost() {
        return this.mLteRsrpBoost;
    }

    public int getLevel() {
        return this.mIsGsm ? getGsmRelatedSignalStrength() : getCdmaRelatedSignalStrength();
    }

    public int getAsuLevel() {
        if (this.mIsGsm) {
            if (this.mLteRsrp != Integer.MAX_VALUE) {
                return getLteAsuLevel();
            }
            if (this.mTdScdmaRscp != Integer.MAX_VALUE) {
                return getTdScdmaAsuLevel();
            }
            if (this.mWcdmaRscp != Integer.MAX_VALUE) {
                return getWcdmaAsuLevel();
            }
            return getGsmAsuLevel();
        }
        int cdmaAsuLevel = getCdmaAsuLevel();
        int evdoAsuLevel = getEvdoAsuLevel();
        if (evdoAsuLevel == 0) {
            return cdmaAsuLevel;
        }
        return (cdmaAsuLevel != 0 && cdmaAsuLevel < evdoAsuLevel) ? cdmaAsuLevel : evdoAsuLevel;
    }

    public int getDbm() {
        if (isGsm()) {
            int lteDbm = getLteDbm();
            if (lteDbm != Integer.MAX_VALUE) {
                return lteDbm;
            }
            if (getTdScdmaLevel() == 0) {
                if (getWcdmaDbm() == Integer.MAX_VALUE) {
                    return getGsmDbm();
                }
                return getWcdmaDbm();
            }
            return getTdScdmaDbm();
        }
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        if (evdoDbm == MIN_WCDMA_RSCP) {
            return cdmaDbm;
        }
        return (cdmaDbm != MIN_WCDMA_RSCP && cdmaDbm < evdoDbm) ? cdmaDbm : evdoDbm;
    }

    public int getGsmDbm() {
        int gsmSignalStrength = getGsmSignalStrength();
        if (gsmSignalStrength == 99) {
            gsmSignalStrength = -1;
        }
        if (gsmSignalStrength != -1) {
            return (gsmSignalStrength * 2) + PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS;
        }
        return -1;
    }

    public int getGsmLevel() {
        int gsmSignalStrength = getGsmSignalStrength();
        if (gsmSignalStrength <= 2 || gsmSignalStrength == 99) {
            return 0;
        }
        if (gsmSignalStrength >= 12) {
            return 4;
        }
        if (gsmSignalStrength >= 8) {
            return 3;
        }
        return gsmSignalStrength >= 5 ? 2 : 1;
    }

    public int getGsmAsuLevel() {
        return getGsmSignalStrength();
    }

    public int getCdmaLevel() {
        int i;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i2 = 1;
        if (cdmaDbm >= -75) {
            i = 4;
        } else if (cdmaDbm >= -85) {
            i = 3;
        } else if (cdmaDbm >= -95) {
            i = 2;
        } else {
            i = cdmaDbm >= -100 ? 1 : 0;
        }
        if (cdmaEcio >= -90) {
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

    public int getCdmaAsuLevel() {
        int i;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        int i2 = 1;
        if (cdmaDbm >= -75) {
            i = 16;
        } else if (cdmaDbm >= -82) {
            i = 8;
        } else if (cdmaDbm >= -90) {
            i = 4;
        } else if (cdmaDbm >= -95) {
            i = 2;
        } else {
            i = cdmaDbm >= -100 ? 1 : 99;
        }
        if (cdmaEcio >= -90) {
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

    public int getEvdoLevel() {
        int i;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        int i2 = 0;
        if (evdoDbm >= -65) {
            i = 4;
        } else if (evdoDbm >= -75) {
            i = 3;
        } else if (evdoDbm >= -90) {
            i = 2;
        } else {
            i = evdoDbm >= -105 ? 1 : 0;
        }
        if (evdoSnr >= 7) {
            i2 = 4;
        } else if (evdoSnr >= 5) {
            i2 = 3;
        } else if (evdoSnr >= 3) {
            i2 = 2;
        } else if (evdoSnr >= 1) {
            i2 = 1;
        }
        return i < i2 ? i : i2;
    }

    public int getEvdoAsuLevel() {
        int i;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        int i2 = 99;
        if (evdoDbm >= -65) {
            i = 16;
        } else if (evdoDbm >= -75) {
            i = 8;
        } else if (evdoDbm >= -85) {
            i = 4;
        } else if (evdoDbm >= -95) {
            i = 2;
        } else {
            i = evdoDbm >= -105 ? 1 : 99;
        }
        if (evdoSnr >= 7) {
            i2 = 16;
        } else if (evdoSnr >= 6) {
            i2 = 8;
        } else if (evdoSnr >= 5) {
            i2 = 4;
        } else if (evdoSnr >= 3) {
            i2 = 2;
        } else if (evdoSnr >= 1) {
            i2 = 1;
        }
        return i < i2 ? i : i2;
    }

    public int getLteDbm() {
        return this.mLteRsrp;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x0076  */
    public int getLteLevel() {
        int i;
        int i2;
        int i3 = this.mLteRsrp;
        if (i3 > -44 || i3 < MIN_LTE_RSRP) {
            if (this.mLteRsrp != Integer.MAX_VALUE) {
                Log.wtf(LOG_TAG, "getLteLevel - invalid lte rsrp: mLteRsrp=" + this.mLteRsrp);
            }
            i = -1;
        } else {
            int[] iArr = this.mLteRsrpThresholds;
            int i4 = iArr[3];
            int i5 = this.mLteRsrpBoost;
            if (i3 >= i4 - i5) {
                i = 4;
            } else if (i3 >= iArr[2] - i5) {
                i = 3;
            } else if (i3 >= iArr[1] - i5) {
                i = 2;
            } else {
                i = i3 >= iArr[0] - i5 ? 1 : 0;
            }
        }
        if (useOnlyRsrpForLteLevel()) {
            log("getLTELevel - rsrp = " + i);
            if (i != -1) {
                return i;
            }
        }
        int i6 = this.mLteRssnr;
        if (i6 > 300) {
            i2 = -1;
        } else if (i6 >= 130) {
            i2 = 4;
        } else if (i6 >= 45) {
            i2 = 3;
        } else if (i6 >= 10) {
            i2 = 2;
        } else if (i6 >= -30) {
            i2 = 1;
        } else if (i6 >= -200) {
            i2 = 0;
        } else {
            i2 = -1;
        }
        if (i2 != -1 && i != -1) {
            return i < i2 ? i : i2;
        }
        if (i2 != -1) {
            return i2;
        }
        if (i != -1) {
            return i;
        }
        int i7 = this.mLteSignalStrength;
        if (i7 <= 63) {
            if (i7 >= 12) {
                return 4;
            }
            if (i7 >= 8) {
                return 3;
            }
            if (i7 >= 5) {
                return 2;
            }
            if (i7 >= 0) {
                return 1;
            }
        }
        return 0;
    }

    public int getLteAsuLevel() {
        int lteDbm = getLteDbm();
        if (lteDbm == Integer.MAX_VALUE) {
            return 255;
        }
        return lteDbm + 140;
    }

    public boolean isGsm() {
        return this.mIsGsm;
    }

    public boolean useOnlyRsrpForLteLevel() {
        return this.mUseOnlyRsrpForLteLevel;
    }

    public int getTdScdmaDbm() {
        return this.mTdScdmaRscp;
    }

    public int getTdScdmaLevel() {
        int tdScdmaDbm = getTdScdmaDbm();
        if (tdScdmaDbm > -25 || tdScdmaDbm == Integer.MAX_VALUE) {
            return 0;
        }
        if (tdScdmaDbm >= -49) {
            return 4;
        }
        if (tdScdmaDbm >= -73) {
            return 3;
        }
        if (tdScdmaDbm >= -97) {
            return 2;
        }
        return tdScdmaDbm >= -110 ? 1 : 0;
    }

    public int getTdScdmaAsuLevel() {
        int tdScdmaDbm = getTdScdmaDbm();
        if (tdScdmaDbm == Integer.MAX_VALUE) {
            return 255;
        }
        return tdScdmaDbm + 120;
    }

    public int getWcdmaRscp() {
        return this.mWcdmaRscp;
    }

    public int getWcdmaAsuLevel() {
        int wcdmaDbm = getWcdmaDbm();
        if (wcdmaDbm == Integer.MAX_VALUE) {
            return 255;
        }
        return wcdmaDbm + 120;
    }

    public int getWcdmaDbm() {
        return this.mWcdmaRscp;
    }

    public int getWcdmaLevel() {
        String str = this.mWcdmaDefaultSignalMeasurement;
        if (str == null) {
            Log.wtf(LOG_TAG, "getWcdmaLevel - WCDMA default signal measurement is invalid.");
            return 0;
        }
        byte b = -1;
        if (str.hashCode() == 3509870 && str.equals(MEASUMENT_TYPE_RSCP)) {
            b = 0;
        }
        if (b == 0) {
            int i = this.mWcdmaRscp;
            if (i < MIN_WCDMA_RSCP || i > -24) {
                if (this.mWcdmaRscp == Integer.MAX_VALUE) {
                    return 0;
                }
                Log.wtf(LOG_TAG, "getWcdmaLevel - invalid WCDMA RSCP: mWcdmaRscp=" + this.mWcdmaRscp);
                return 0;
            }
            int[] iArr = this.mWcdmaRscpThresholds;
            if (i < iArr[3]) {
                if (i < iArr[2]) {
                    if (i < iArr[1]) {
                        if (i < iArr[0]) {
                            return 0;
                        }
                        return 1;
                    }
                    return 2;
                }
                return 3;
            }
            return 4;
        }
        int i2 = this.mWcdmaSignalStrength;
        if (i2 < 0 || i2 > 31) {
            if (this.mWcdmaSignalStrength == 99) {
                return 0;
            }
            Log.wtf(LOG_TAG, "getWcdmaLevel - invalid WCDMA RSSI: mWcdmaSignalStrength=" + this.mWcdmaSignalStrength);
            return 0;
        }
        if (i2 < 18) {
            if (i2 < 13) {
                if (i2 < 8) {
                    if (i2 < 3) {
                        return 0;
                    }
                    return 1;
                }
                return 2;
            }
            return 3;
        }
        return 4;
    }

    public int hashCode() {
        return (this.mGsmSignalStrength * 31) + (this.mGsmBitErrorRate * 31) + (this.mCdmaDbm * 31) + (this.mCdmaEcio * 31) + (this.mEvdoDbm * 31) + (this.mEvdoEcio * 31) + (this.mEvdoSnr * 31) + (this.mLteSignalStrength * 31) + (this.mLteRsrp * 31) + (this.mLteRsrq * 31) + (this.mLteRssnr * 31) + (this.mLteCqi * 31) + (this.mLteRsrpBoost * 31) + (this.mTdScdmaRscp * 31) + (this.mWcdmaSignalStrength * 31) + (this.mWcdmaRscpAsu * 31) + (this.mWcdmaRscp * 31) + (this.mIsGsm ? 1 : 0) + (this.mUseOnlyRsrpForLteLevel ? 1 : 0) + Objects.hashCode(this.mWcdmaDefaultSignalMeasurement) + Arrays.hashCode(this.mLteRsrpThresholds) + Arrays.hashCode(this.mWcdmaRscpThresholds);
    }

    public boolean equals(Object obj) {
        try {
            SignalStrength signalStrength = (SignalStrength) obj;
            return obj != null && this.mGsmSignalStrength == signalStrength.mGsmSignalStrength && this.mGsmBitErrorRate == signalStrength.mGsmBitErrorRate && this.mCdmaDbm == signalStrength.mCdmaDbm && this.mCdmaEcio == signalStrength.mCdmaEcio && this.mEvdoDbm == signalStrength.mEvdoDbm && this.mEvdoEcio == signalStrength.mEvdoEcio && this.mEvdoSnr == signalStrength.mEvdoSnr && this.mLteSignalStrength == signalStrength.mLteSignalStrength && this.mLteRsrp == signalStrength.mLteRsrp && this.mLteRsrq == signalStrength.mLteRsrq && this.mLteRssnr == signalStrength.mLteRssnr && this.mLteCqi == signalStrength.mLteCqi && this.mLteRsrpBoost == signalStrength.mLteRsrpBoost && this.mTdScdmaRscp == signalStrength.mTdScdmaRscp && this.mWcdmaSignalStrength == signalStrength.mWcdmaSignalStrength && this.mWcdmaRscpAsu == signalStrength.mWcdmaRscpAsu && this.mWcdmaRscp == signalStrength.mWcdmaRscp && this.mIsGsm == signalStrength.mIsGsm && this.mUseOnlyRsrpForLteLevel == signalStrength.mUseOnlyRsrpForLteLevel && Objects.equals(this.mWcdmaDefaultSignalMeasurement, signalStrength.mWcdmaDefaultSignalMeasurement) && Arrays.equals(this.mLteRsrpThresholds, signalStrength.mLteRsrpThresholds) && Arrays.equals(this.mWcdmaRscpThresholds, signalStrength.mWcdmaRscpThresholds);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SignalStrength: ");
        sb.append(this.mGsmSignalStrength);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mGsmBitErrorRate);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mCdmaDbm);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mCdmaEcio);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mEvdoDbm);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mEvdoEcio);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mEvdoSnr);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteSignalStrength);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteRsrp);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteRsrq);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteRssnr);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteCqi);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mLteRsrpBoost);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mTdScdmaRscp);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mWcdmaSignalStrength);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mWcdmaRscpAsu);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mWcdmaRscp);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mIsGsm ? "gsm|lte" : "cdma");
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mUseOnlyRsrpForLteLevel ? "use_only_rsrp_for_lte_level" : "use_rsrp_and_rssnr_for_lte_level");
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(this.mWcdmaDefaultSignalMeasurement);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(Arrays.toString(this.mLteRsrpThresholds));
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        sb.append(Arrays.toString(this.mWcdmaRscpThresholds));
        return sb.toString();
    }

    private int getGsmRelatedSignalStrength() {
        int lteLevel = getLteLevel();
        if (lteLevel != 0) {
            return lteLevel;
        }
        int tdScdmaLevel = getTdScdmaLevel();
        if (tdScdmaLevel != 0) {
            return tdScdmaLevel;
        }
        int wcdmaLevel = getWcdmaLevel();
        return wcdmaLevel == 0 ? getGsmLevel() : wcdmaLevel;
    }

    private int getCdmaRelatedSignalStrength() {
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        if (evdoLevel == 0) {
            return cdmaLevel;
        }
        return (cdmaLevel != 0 && cdmaLevel < evdoLevel) ? cdmaLevel : evdoLevel;
    }

    private void setFromNotifierBundle(Bundle bundle) {
        this.mGsmSignalStrength = bundle.getInt("GsmSignalStrength");
        this.mGsmBitErrorRate = bundle.getInt("GsmBitErrorRate");
        this.mCdmaDbm = bundle.getInt("CdmaDbm");
        this.mCdmaEcio = bundle.getInt("CdmaEcio");
        this.mEvdoDbm = bundle.getInt("EvdoDbm");
        this.mEvdoEcio = bundle.getInt("EvdoEcio");
        this.mEvdoSnr = bundle.getInt("EvdoSnr");
        this.mLteSignalStrength = bundle.getInt("LteSignalStrength");
        this.mLteRsrp = bundle.getInt("LteRsrp");
        this.mLteRsrq = bundle.getInt("LteRsrq");
        this.mLteRssnr = bundle.getInt("LteRssnr");
        this.mLteCqi = bundle.getInt("LteCqi");
        this.mLteRsrpBoost = bundle.getInt("LteRsrpBoost");
        this.mTdScdmaRscp = bundle.getInt("TdScdma");
        this.mWcdmaSignalStrength = bundle.getInt("WcdmaSignalStrength");
        this.mWcdmaRscpAsu = bundle.getInt("WcdmaRscpAsu");
        this.mWcdmaRscp = bundle.getInt("WcdmaRscp");
        this.mIsGsm = bundle.getBoolean("IsGsm");
        this.mUseOnlyRsrpForLteLevel = bundle.getBoolean("UseOnlyRsrpForLteLevel");
        this.mWcdmaDefaultSignalMeasurement = bundle.getString("WcdmaDefaultSignalMeasurement");
        ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("lteRsrpThresholds");
        for (int i = 0; i < integerArrayList.size(); i++) {
            this.mLteRsrpThresholds[i] = integerArrayList.get(i).intValue();
        }
        ArrayList<Integer> integerArrayList2 = bundle.getIntegerArrayList("wcdmaRscpThresholds");
        for (int i2 = 0; i2 < integerArrayList2.size(); i2++) {
            this.mWcdmaRscpThresholds[i2] = integerArrayList2.get(i2).intValue();
        }
    }

    public void fillInNotifierBundle(Bundle bundle) {
        bundle.putInt("GsmSignalStrength", this.mGsmSignalStrength);
        bundle.putInt("GsmBitErrorRate", this.mGsmBitErrorRate);
        bundle.putInt("CdmaDbm", this.mCdmaDbm);
        bundle.putInt("CdmaEcio", this.mCdmaEcio);
        bundle.putInt("EvdoDbm", this.mEvdoDbm);
        bundle.putInt("EvdoEcio", this.mEvdoEcio);
        bundle.putInt("EvdoSnr", this.mEvdoSnr);
        bundle.putInt("LteSignalStrength", this.mLteSignalStrength);
        bundle.putInt("LteRsrp", this.mLteRsrp);
        bundle.putInt("LteRsrq", this.mLteRsrq);
        bundle.putInt("LteRssnr", this.mLteRssnr);
        bundle.putInt("LteCqi", this.mLteCqi);
        bundle.putInt("LteRsrpBoost", this.mLteRsrpBoost);
        bundle.putInt("TdScdma", this.mTdScdmaRscp);
        bundle.putInt("WcdmaSignalStrength", this.mWcdmaSignalStrength);
        bundle.putInt("WcdmaRscpAsu", this.mWcdmaRscpAsu);
        bundle.putInt("WcdmaRscp", this.mWcdmaRscp);
        bundle.putBoolean("IsGsm", this.mIsGsm);
        bundle.putBoolean("UseOnlyRsrpForLteLevel", this.mUseOnlyRsrpForLteLevel);
        bundle.putString("WcdmaDefaultSignalMeasurement", this.mWcdmaDefaultSignalMeasurement);
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i : this.mLteRsrpThresholds) {
            arrayList.add(Integer.valueOf(i));
        }
        bundle.putIntegerArrayList("lteRsrpThresholds", arrayList);
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        for (int i2 : this.mWcdmaRscpThresholds) {
            arrayList2.add(Integer.valueOf(i2));
        }
        bundle.putIntegerArrayList("wcdmaRscpThresholds", arrayList2);
    }

    private int[] getDefaultLteRsrpThresholds() {
        return CarrierConfigManager.getDefaultConfig().getIntArray(CarrierConfigManager.KEY_LTE_RSRP_THRESHOLDS_INT_ARRAY);
    }

    private int[] getDefaultWcdmaRscpThresholds() {
        return CarrierConfigManager.getDefaultConfig().getIntArray(CarrierConfigManager.KEY_WCDMA_RSCP_THRESHOLDS_INT_ARRAY);
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }
}
