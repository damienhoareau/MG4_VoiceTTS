package com.android.internal.telephony.gsm;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.telephony.SmsCbLocation;
import android.telephony.SmsCbMessage;
import android.util.Pair;
import com.android.internal.R;
import com.android.internal.telephony.GsmAlphabet;
import com.saicmotor.speech.loader.LanguagePropsType;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes3.dex */
public class GsmSmsCbMessage {
    private static final char CARRIAGE_RETURN = '\r';
    private static final String[] LANGUAGE_CODES_GROUP_0 = {"de", "en", "it", "fr", "es", "nl", "sv", "da", "pt", "fi", "no", "el", "tr", "hu", "pl", null};
    private static final String[] LANGUAGE_CODES_GROUP_2 = {"cs", "he", LanguagePropsType.ALABO, "ru", "is", null, null, null, null, null, null, null, null, null, null, null};
    private static final int PDU_BODY_PAGE_LENGTH = 82;

    private GsmSmsCbMessage() {
    }

    private static String getEtwsPrimaryMessage(Context context, int i) {
        Resources resources = context.getResources();
        if (i == 0) {
            return resources.getString(R.string.etws_primary_default_message_earthquake);
        }
        if (i == 1) {
            return resources.getString(R.string.etws_primary_default_message_tsunami);
        }
        if (i == 2) {
            return resources.getString(R.string.etws_primary_default_message_earthquake_and_tsunami);
        }
        if (i != 3) {
            return i != 4 ? "" : resources.getString(R.string.etws_primary_default_message_others);
        }
        return resources.getString(R.string.etws_primary_default_message_test);
    }

    public static SmsCbMessage createSmsCbMessage(Context context, SmsCbHeader smsCbHeader, SmsCbLocation smsCbLocation, byte[][] bArr) throws IllegalArgumentException {
        if (smsCbHeader.isEtwsPrimaryNotification()) {
            return new SmsCbMessage(1, smsCbHeader.getGeographicalScope(), smsCbHeader.getSerialNumber(), smsCbLocation, smsCbHeader.getServiceCategory(), null, getEtwsPrimaryMessage(context, smsCbHeader.getEtwsInfo().getWarningType()), 3, smsCbHeader.getEtwsInfo(), smsCbHeader.getCmasInfo());
        }
        StringBuilder sb = new StringBuilder();
        String str = null;
        for (byte[] bArr2 : bArr) {
            Pair<String, String> body = parseBody(smsCbHeader, bArr2);
            str = body.first;
            sb.append(body.second);
        }
        return new SmsCbMessage(1, smsCbHeader.getGeographicalScope(), smsCbHeader.getSerialNumber(), smsCbLocation, smsCbHeader.getServiceCategory(), str, sb.toString(), smsCbHeader.isEmergencyMessage() ? 3 : 0, smsCbHeader.getEtwsInfo(), smsCbHeader.getCmasInfo());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:15:0x0027  */
    /* JADX WARN: Code duplicated, block: B:22:0x003e A[PHI: r6 r7
  0x003e: PHI (r6v1 int) = (r6v0 int), (r6v0 int), (r6v0 int), (r6v0 int), (r6v0 int), (r6v6 int) binds: [B:25:0x0044, B:8:0x0019, B:21:0x0039, B:16:0x002a, B:12:0x0022, B:14:0x0025] A[DONT_GENERATE, DONT_INLINE]
  0x003e: PHI (r7v1 java.lang.String) = 
  (r7v0 java.lang.String)
  (r7v0 java.lang.String)
  (r7v3 java.lang.String)
  (r7v4 java.lang.String)
  (r7v0 java.lang.String)
  (r7v0 java.lang.String)
 binds: [B:25:0x0044, B:8:0x0019, B:21:0x0039, B:16:0x002a, B:12:0x0022, B:14:0x0025] A[DONT_GENERATE, DONT_INLINE]] */
    private static Pair<String, String> parseBody(SmsCbHeader smsCbHeader, byte[] bArr) {
        boolean z;
        String str;
        int dataCodingScheme = smsCbHeader.getDataCodingScheme();
        int i = (dataCodingScheme & 240) >> 4;
        if (i != 9 && i != 14) {
            int i2 = 1;
            String str2 = null;
            if (i != 15) {
                switch (i) {
                    case 0:
                        str2 = LANGUAGE_CODES_GROUP_0[dataCodingScheme & 15];
                        z = false;
                        str = str2;
                        break;
                    case 1:
                        if ((dataCodingScheme & 15) == 1) {
                            z = true;
                            str = null;
                            i2 = 3;
                        } else {
                            z = true;
                            str = str2;
                        }
                        break;
                    case 2:
                        str2 = LANGUAGE_CODES_GROUP_2[dataCodingScheme & 15];
                        z = false;
                        str = str2;
                        break;
                    case 3:
                    default:
                        z = false;
                        str = str2;
                        break;
                    case 4:
                    case 5:
                        int i3 = (dataCodingScheme & 12) >> 2;
                        if (i3 != 1) {
                            if (i3 == 2) {
                                i2 = 3;
                            }
                            z = false;
                        } else {
                            z = false;
                            i2 = 2;
                        }
                        str = str2;
                        break;
                    case 6:
                    case 7:
                        break;
                }
            } else {
                if (((dataCodingScheme & 4) >> 2) == 1) {
                    z = false;
                    i2 = 2;
                } else {
                    z = false;
                }
                str = str2;
            }
            if (smsCbHeader.isUmtsFormat()) {
                byte b = bArr[6];
                if (bArr.length < (b * 83) + 7) {
                    throw new IllegalArgumentException("Pdu length " + bArr.length + " does not match " + ((int) b) + " pages");
                }
                StringBuilder sb = new StringBuilder();
                for (int i4 = 0; i4 < b; i4++) {
                    int i5 = (i4 * 83) + 7;
                    byte b2 = bArr[i5 + 82];
                    if (b2 > 82) {
                        throw new IllegalArgumentException("Page length " + ((int) b2) + " exceeds maximum value 82");
                    }
                    Pair<String, String> pairUnpackBody = unpackBody(bArr, i2, i5, b2, z, str);
                    str = pairUnpackBody.first;
                    sb.append(pairUnpackBody.second);
                }
                return new Pair<>(str, sb.toString());
            }
            return unpackBody(bArr, i2, 6, bArr.length - 6, z, str);
        }
        throw new IllegalArgumentException("Unsupported GSM dataCodingScheme " + dataCodingScheme);
    }

    private static Pair<String, String> unpackBody(byte[] bArr, int i, int i2, int i3, boolean z, String str) {
        String strGsm7BitPackedToString;
        int i4;
        if (i == 1) {
            strGsm7BitPackedToString = GsmAlphabet.gsm7BitPackedToString(bArr, i2, (i3 * 8) / 7);
            if (z && strGsm7BitPackedToString != null && strGsm7BitPackedToString.length() > 2) {
                str = strGsm7BitPackedToString.substring(0, 2);
                strGsm7BitPackedToString = strGsm7BitPackedToString.substring(3);
            }
        } else if (i != 3) {
            strGsm7BitPackedToString = null;
        } else {
            if (z && bArr.length >= (i4 = i2 + 2)) {
                str = GsmAlphabet.gsm7BitPackedToString(bArr, i2, 2);
                i3 -= 2;
                i2 = i4;
            }
            try {
                strGsm7BitPackedToString = new String(bArr, i2, i3 & Configuration.DENSITY_DPI_ANY, "utf-16");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Error decoding UTF-16 message", e);
            }
        }
        if (strGsm7BitPackedToString != null) {
            for (int length = strGsm7BitPackedToString.length() - 1; length >= 0; length--) {
                if (strGsm7BitPackedToString.charAt(length) != '\r') {
                    strGsm7BitPackedToString = strGsm7BitPackedToString.substring(0, length + 1);
                    break;
                }
            }
        } else {
            strGsm7BitPackedToString = "";
        }
        return new Pair<>(str, strGsm7BitPackedToString);
    }
}
