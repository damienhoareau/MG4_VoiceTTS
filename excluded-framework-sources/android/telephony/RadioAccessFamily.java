package android.telephony;

import android.bluetooth.BluetoothHidDevice;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.RILConstants;

/* JADX INFO: loaded from: classes2.dex */
public class RadioAccessFamily implements Parcelable {
    private static final int CDMA = 112;
    public static final Parcelable.Creator<RadioAccessFamily> CREATOR = new Parcelable.Creator<RadioAccessFamily>() { // from class: android.telephony.RadioAccessFamily.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RadioAccessFamily createFromParcel(Parcel parcel) {
            return new RadioAccessFamily(parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RadioAccessFamily[] newArray(int i) {
            return new RadioAccessFamily[i];
        }
    };
    private static final int EVDO = 12672;
    private static final int GSM = 65542;
    private static final int HS = 36352;
    private static final int LTE = 540672;
    public static final int RAF_1xRTT = 64;
    public static final int RAF_EDGE = 4;
    public static final int RAF_EHRPD = 8192;
    public static final int RAF_EVDO_0 = 128;
    public static final int RAF_EVDO_A = 256;
    public static final int RAF_EVDO_B = 4096;
    public static final int RAF_GPRS = 2;
    public static final int RAF_GSM = 65536;
    public static final int RAF_HSDPA = 512;
    public static final int RAF_HSPA = 2048;
    public static final int RAF_HSPAP = 32768;
    public static final int RAF_HSUPA = 1024;
    public static final int RAF_IS95A = 16;
    public static final int RAF_IS95B = 32;
    public static final int RAF_LTE = 16384;
    public static final int RAF_LTE_CA = 524288;
    public static final int RAF_TD_SCDMA = 131072;
    public static final int RAF_UMTS = 8;
    public static final int RAF_UNKNOWN = 1;
    private static final int WCDMA = 36360;
    private int mPhoneId;
    private int mRadioAccessFamily;

    private static int getAdjustedRaf(int i) {
        if ((i & GSM) > 0) {
            i |= GSM;
        }
        if ((i & WCDMA) > 0) {
            i |= WCDMA;
        }
        if ((i & 112) > 0) {
            i |= 112;
        }
        if ((i & EVDO) > 0) {
            i |= EVDO;
        }
        return (i & 540672) > 0 ? i | 540672 : i;
    }

    public static int getHighestRafCapability(int i) {
        if ((540672 & i) > 0) {
            return 3;
        }
        if ((49024 | (WCDMA & i)) > 0) {
            return 2;
        }
        return ((i & 112) | GSM) > 0 ? 1 : 0;
    }

    public static int getRafFromNetworkType(int i) {
        switch (i) {
            case 0:
            case 3:
                return 101902;
            case 1:
                return GSM;
            case 2:
                return WCDMA;
            case 4:
                return 12784;
            case 5:
                return 112;
            case 6:
                return EVDO;
            case 7:
                return 114686;
            case 8:
                return 553456;
            case 9:
                return 642574;
            case 10:
                return 655358;
            case 11:
                return 540672;
            case 12:
                return 577032;
            case 13:
                return 131072;
            case 14:
                return 167432;
            case 15:
                return 671744;
            case 16:
                return 196614;
            case 17:
                return 737286;
            case 18:
                return 232974;
            case 19:
                return 708104;
            case 20:
                return 773646;
            case 21:
                return 245758;
            case 22:
                return 786430;
            default:
                return 1;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public RadioAccessFamily(int i, int i2) {
        this.mPhoneId = i;
        this.mRadioAccessFamily = i2;
    }

    public int getPhoneId() {
        return this.mPhoneId;
    }

    public int getRadioAccessFamily() {
        return this.mRadioAccessFamily;
    }

    public String toString() {
        return "{ mPhoneId = " + this.mPhoneId + ", mRadioAccessFamily = " + this.mRadioAccessFamily + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mPhoneId);
        parcel.writeInt(this.mRadioAccessFamily);
    }

    public static int getNetworkTypeFromRaf(int i) {
        switch (getAdjustedRaf(i)) {
            case 112:
                return 5;
            case EVDO /* 12672 */:
                return 6;
            case 12784:
                return 4;
            case WCDMA /* 36360 */:
                return 2;
            case GSM /* 65542 */:
                return 1;
            case 101902:
                return 0;
            case 114686:
                return 7;
            case 131072:
                return 13;
            case 167432:
                return 14;
            case 196614:
                return 16;
            case 232974:
                return 18;
            case 245758:
                return 21;
            case 540672:
                return 11;
            case 553456:
                return 8;
            case 577032:
                return 12;
            case 642574:
                return 9;
            case 655358:
                return 10;
            case 671744:
                return 15;
            case 708104:
                return 19;
            case 737286:
                return 17;
            case 773646:
                return 20;
            case 786430:
                return 22;
            default:
                return RILConstants.PREFERRED_NETWORK_MODE;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:71:0x0105  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static int singleRafTypeFromString(String str) {
        byte b;
        switch (str.hashCode()) {
            case -2039427040:
                if (!str.equals("LTE_CA")) {
                    b = -1;
                } else {
                    b = 21;
                }
                break;
            case -908593671:
                if (!str.equals("TD_SCDMA")) {
                    b = -1;
                } else {
                    b = 16;
                }
                break;
            case 2315:
                if (!str.equals("HS")) {
                    b = -1;
                } else {
                    b = 17;
                }
                break;
            case 70881:
                if (!str.equals("GSM")) {
                    b = -1;
                } else {
                    b = MidiConstants.STATUS_CHANNEL_MASK;
                }
                break;
            case 75709:
                if (!str.equals("LTE")) {
                    b = -1;
                } else {
                    b = 13;
                }
                break;
            case 2063797:
                if (!str.equals("CDMA")) {
                    b = -1;
                } else {
                    b = 18;
                }
                break;
            case 2123197:
                if (!str.equals("EDGE")) {
                    b = -1;
                } else {
                    b = 1;
                }
                break;
            case 2140412:
                if (!str.equals("EVDO")) {
                    b = -1;
                } else {
                    b = 19;
                }
                break;
            case 2194666:
                if (!str.equals("GPRS")) {
                    b = -1;
                } else {
                    b = 0;
                }
                break;
            case 2227260:
                if (!str.equals("HSPA")) {
                    b = -1;
                } else {
                    b = 10;
                }
                break;
            case 2608919:
                if (!str.equals("UMTS")) {
                    b = -1;
                } else {
                    b = 2;
                }
                break;
            case 47955627:
                if (!str.equals("1XRTT")) {
                    b = -1;
                } else {
                    b = 5;
                }
                break;
            case 65949251:
                if (!str.equals("EHRPD")) {
                    b = -1;
                } else {
                    b = 12;
                }
                break;
            case 69034058:
                if (!str.equals("HSDPA")) {
                    b = -1;
                } else {
                    b = 8;
                }
                break;
            case 69045140:
                if (!str.equals("HSPAP")) {
                    b = -1;
                } else {
                    b = BluetoothHidDevice.ERROR_RSP_UNKNOWN;
                }
                break;
            case 69050395:
                if (!str.equals("HSUPA")) {
                    b = -1;
                } else {
                    b = 9;
                }
                break;
            case 69946171:
                if (!str.equals("IS95A")) {
                    b = -1;
                } else {
                    b = 3;
                }
                break;
            case 69946172:
                if (!str.equals("IS95B")) {
                    b = -1;
                } else {
                    b = 4;
                }
                break;
            case 82410124:
                if (!str.equals("WCDMA")) {
                    b = -1;
                } else {
                    b = 20;
                }
                break;
            case 2056938925:
                if (!str.equals("EVDO_0")) {
                    b = -1;
                } else {
                    b = 6;
                }
                break;
            case 2056938942:
                if (!str.equals("EVDO_A")) {
                    b = -1;
                } else {
                    b = 7;
                }
                break;
            case 2056938943:
                if (!str.equals("EVDO_B")) {
                    b = -1;
                } else {
                    b = 11;
                }
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                return 2;
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 16;
            case 4:
                return 32;
            case 5:
                return 64;
            case 6:
                return 128;
            case 7:
                return 256;
            case 8:
                return 512;
            case 9:
                return 1024;
            case 10:
                return 2048;
            case 11:
                return 4096;
            case 12:
                return 8192;
            case 13:
                return 16384;
            case 14:
                return 32768;
            case 15:
                return 65536;
            case 16:
                return 131072;
            case 17:
                return HS;
            case 18:
                return 112;
            case 19:
                return EVDO;
            case 20:
                return WCDMA;
            case 21:
                return 524288;
            default:
                return 1;
        }
    }

    public static int rafTypeFromString(String str) {
        int i = 0;
        for (String str2 : str.toUpperCase().split("\\|")) {
            int iSingleRafTypeFromString = singleRafTypeFromString(str2.trim());
            if (iSingleRafTypeFromString == 1) {
                return iSingleRafTypeFromString;
            }
            i |= iSingleRafTypeFromString;
        }
        return i;
    }
}
