package com.android.internal.telephony.uicc;

import android.app.backup.FullBackup;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.telephony.Rlog;
import android.text.format.DateFormat;
import com.android.internal.R;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.GsmAlphabet;
import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: classes3.dex */
public class IccUtils {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    static final String LOG_TAG = "IccUtils";

    private static int bitToRGB(int i) {
        return i == 1 ? -1 : -16777216;
    }

    public static int cdmaBcdByteToInt(byte b) {
        int i = (b & 240) <= 144 ? ((b >> 4) & 15) * 10 : 0;
        int i2 = b & MidiConstants.STATUS_CHANNEL_MASK;
        return i2 <= 9 ? i + i2 : i;
    }

    private static byte charToByte(char c) {
        int i;
        if (c >= '0' && c <= '9') {
            i = c - '0';
        } else if (c >= 'A' && c <= 'F') {
            i = c - '7';
        } else {
            if (c < 'a' || c > 'f') {
                return (byte) 0;
            }
            i = c - 'W';
        }
        return (byte) i;
    }

    public static byte countTrailingZeros(byte b) {
        if (b == 0) {
            return (byte) 8;
        }
        int i = b & 255;
        byte b2 = (i & 15) != 0 ? (byte) 3 : (byte) 7;
        if ((i & 51) != 0) {
            b2 = (byte) (b2 - 2);
        }
        return (i & 85) != 0 ? (byte) (b2 - 1) : b2;
    }

    public static int gsmBcdByteToInt(byte b) {
        int i = (b & 240) <= 144 ? (b >> 4) & 15 : 0;
        int i2 = b & MidiConstants.STATUS_CHANNEL_MASK;
        return i2 <= 9 ? i + (i2 * 10) : i;
    }

    public static String bcdToString(byte[] bArr, int i, int i2) {
        int i3;
        StringBuilder sb = new StringBuilder(i2 * 2);
        for (int i4 = i; i4 < i + i2 && (i3 = bArr[i4] & MidiConstants.STATUS_CHANNEL_MASK) <= 9; i4++) {
            sb.append((char) (i3 + 48));
            int i5 = (bArr[i4] >> 4) & 15;
            if (i5 != 15) {
                if (i5 > 9) {
                    break;
                }
                sb.append((char) (i5 + 48));
            }
        }
        return sb.toString();
    }

    public static String bcdToString(byte[] bArr) {
        return bcdToString(bArr, 0, bArr.length);
    }

    public static byte[] bcdToBytes(String str) {
        byte[] bArr = new byte[(str.length() + 1) / 2];
        bcdToBytes(str, bArr);
        return bArr;
    }

    public static void bcdToBytes(String str, byte[] bArr) {
        if (str.length() % 2 != 0) {
            str = str + "0";
        }
        int iMin = Math.min(bArr.length * 2, str.length());
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = i + 1;
            if (i3 >= iMin) {
                return;
            }
            bArr[i2] = (byte) ((charToByte(str.charAt(i3)) << 4) | charToByte(str.charAt(i)));
            i += 2;
            i2++;
        }
    }

    public static String bcdPlmnToString(byte[] bArr, int i) {
        if (i + 3 > bArr.length) {
            return null;
        }
        int i2 = i + 0;
        int i3 = bArr[i2] << 4;
        int i4 = i + 1;
        int i5 = i + 2;
        String strBytesToHexString = bytesToHexString(new byte[]{(byte) (((bArr[i2] >> 4) & 15) | i3), (byte) ((bArr[i4] << 4) | (bArr[i5] & MidiConstants.STATUS_CHANNEL_MASK)), (byte) (((bArr[i4] >> 4) & 15) | (bArr[i5] & 240))});
        return strBytesToHexString.contains(FullBackup.FILES_TREE_TOKEN) ? strBytesToHexString.replaceAll(FullBackup.FILES_TREE_TOKEN, "") : strBytesToHexString;
    }

    public static String bchToString(byte[] bArr, int i, int i2) {
        StringBuilder sb = new StringBuilder(i2 * 2);
        for (int i3 = i; i3 < i + i2; i3++) {
            sb.append(HEX_CHARS[bArr[i3] & MidiConstants.STATUS_CHANNEL_MASK]);
            sb.append(HEX_CHARS[(bArr[i3] >> 4) & 15]);
        }
        return sb.toString();
    }

    public static String cdmaBcdToString(byte[] bArr, int i, int i2) {
        StringBuilder sb = new StringBuilder(i2);
        int i3 = 0;
        while (i3 < i2) {
            int i4 = bArr[i] & MidiConstants.STATUS_CHANNEL_MASK;
            if (i4 > 9) {
                i4 = 0;
            }
            sb.append((char) (i4 + 48));
            int i5 = i3 + 1;
            if (i5 == i2) {
                break;
            }
            int i6 = (bArr[i] >> 4) & 15;
            if (i6 > 9) {
                i6 = 0;
            }
            sb.append((char) (i6 + 48));
            i3 = i5 + 1;
            i++;
        }
        return sb.toString();
    }

    public static String adnStringFieldToString(byte[] bArr, int i, int i2) {
        int i3;
        char c;
        String string = "";
        if (i2 == 0) {
            return "";
        }
        boolean z = true;
        if (i2 >= 1 && bArr[i] == -128) {
            String str = null;
            try {
                str = new String(bArr, i + 1, ((i2 - 1) / 2) * 2, "utf-16be");
            } catch (UnsupportedEncodingException e) {
                Rlog.e(LOG_TAG, "implausible UnsupportedEncodingException", e);
            }
            if (str != null) {
                int length = str.length();
                while (length > 0 && str.charAt(length - 1) == 65535) {
                    length--;
                }
                return str.substring(0, length);
            }
        }
        if (i2 >= 3 && bArr[i] == -127) {
            i3 = bArr[i + 1] & 255;
            int i4 = i2 - 3;
            if (i3 > i4) {
                i3 = i4;
            }
            c = (char) ((bArr[i + 2] & 255) << 7);
            i += 3;
        } else if (i2 < 4 || bArr[i] != -126) {
            z = false;
            i3 = 0;
            c = 0;
        } else {
            i3 = bArr[i + 1] & 255;
            int i5 = i2 - 4;
            if (i3 > i5) {
                i3 = i5;
            }
            c = (char) (((bArr[i + 2] & 255) << 8) | (bArr[i + 3] & 255));
            i += 4;
        }
        if (z) {
            StringBuilder sb = new StringBuilder();
            while (i3 > 0) {
                if (bArr[i] < 0) {
                    sb.append((char) ((bArr[i] & 127) + c));
                    i++;
                    i3--;
                }
                int i6 = 0;
                while (i6 < i3 && bArr[i + i6] >= 0) {
                    i6++;
                }
                sb.append(GsmAlphabet.gsm8BitUnpackedToString(bArr, i, i6));
                i += i6;
                i3 -= i6;
            }
            return sb.toString();
        }
        try {
            string = Resources.getSystem().getString(R.string.gsm_alphabet_default_charset);
        } catch (Resources.NotFoundException unused) {
        }
        return GsmAlphabet.gsm8BitUnpackedToString(bArr, i, i2, string.trim());
    }

    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        char c2 = DateFormat.CAPITAL_AM_PM;
        if (c < 'A' || c > 'F') {
            c2 = DateFormat.AM_PM;
            if (c < 'a' || c > 'f') {
                throw new RuntimeException("invalid hex char '" + c + "'");
            }
        }
        return (c - c2) + 10;
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((hexCharToInt(str.charAt(i)) << 4) | hexCharToInt(str.charAt(i + 1)));
        }
        return bArr;
    }

    public static String bytesToHexString(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            sb.append(HEX_CHARS[(bArr[i] >> 4) & 15]);
            sb.append(HEX_CHARS[bArr[i] & MidiConstants.STATUS_CHANNEL_MASK]);
        }
        return sb.toString();
    }

    public static String networkNameToString(byte[] bArr, int i, int i2) {
        String strGsm7BitPackedToString = "";
        if ((bArr[i] & 128) == 128 && i2 >= 1) {
            int i3 = (bArr[i] >>> 4) & 7;
            if (i3 == 0) {
                int i4 = i + 1;
                strGsm7BitPackedToString = GsmAlphabet.gsm7BitPackedToString(bArr, i4, (((i2 - 1) * 8) - (bArr[i] & 7)) / 7);
            } else if (i3 == 1) {
                try {
                    strGsm7BitPackedToString = new String(bArr, i + 1, i2 - 1, "utf-16");
                } catch (UnsupportedEncodingException e) {
                    Rlog.e(LOG_TAG, "implausible UnsupportedEncodingException", e);
                }
            }
            byte b = bArr[i];
        }
        return strGsm7BitPackedToString;
    }

    public static Bitmap parseToBnW(byte[] bArr, int i) {
        int i2 = 0;
        int i3 = bArr[0] & 255;
        int i4 = bArr[1] & 255;
        int i5 = i3 * i4;
        int[] iArr = new int[i5];
        int i6 = 7;
        int i7 = 2;
        byte b = 0;
        while (i2 < i5) {
            if (i2 % 8 == 0) {
                int i8 = i7 + 1;
                byte b2 = bArr[i7];
                i6 = 7;
                i7 = i8;
                b = b2;
            }
            iArr[i2] = bitToRGB((b >> i6) & 1);
            i2++;
            i6--;
        }
        if (i2 != i5) {
            Rlog.e(LOG_TAG, "parse end and size error");
        }
        return Bitmap.createBitmap(iArr, i3, i4, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap parseToRGB(byte[] bArr, int i, boolean z) {
        int[] iArrMapToNon2OrderBitColor;
        int i2 = bArr[0] & 255;
        int i3 = bArr[1] & 255;
        int i4 = bArr[2] & 255;
        int i5 = bArr[3] & 255;
        int[] clut = getCLUT(bArr, ((bArr[4] & 255) << 8) | (bArr[5] & 255), i5);
        if (true == z) {
            clut[i5 - 1] = 0;
        }
        if (8 % i4 == 0) {
            iArrMapToNon2OrderBitColor = mapTo2OrderBitColor(bArr, 6, i2 * i3, clut, i4);
        } else {
            iArrMapToNon2OrderBitColor = mapToNon2OrderBitColor(bArr, 6, i2 * i3, clut, i4);
        }
        return Bitmap.createBitmap(iArrMapToNon2OrderBitColor, i2, i3, Bitmap.Config.RGB_565);
    }

    /* JADX WARN: Code duplicated, block: B:13:0x001d  */
    private static int[] mapTo2OrderBitColor(byte[] bArr, int i, int i2, int[] iArr, int i3) {
        int i4;
        if (8 % i3 != 0) {
            Rlog.e(LOG_TAG, "not event number of color");
            return mapToNon2OrderBitColor(bArr, i, i2, iArr, i3);
        }
        if (i3 == 1) {
            i4 = 1;
        } else if (i3 == 2) {
            i4 = 3;
        } else if (i3 == 4) {
            i4 = 15;
        } else if (i3 != 8) {
            i4 = 1;
        } else {
            i4 = 255;
        }
        int[] iArr2 = new int[i2];
        int i5 = 8 / i3;
        int i6 = 0;
        while (i6 < i2) {
            int i7 = i + 1;
            int i8 = bArr[i];
            int i9 = 0;
            while (i9 < i5) {
                iArr2[i6] = iArr[(i8 >> (((i5 - i9) - 1) * i3)) & i4];
                i9++;
                i6++;
            }
            i = i7;
        }
        return iArr2;
    }

    private static int[] mapToNon2OrderBitColor(byte[] bArr, int i, int i2, int[] iArr, int i3) {
        if (8 % i3 == 0) {
            Rlog.e(LOG_TAG, "not odd number of color");
            return mapTo2OrderBitColor(bArr, i, i2, iArr, i3);
        }
        return new int[i2];
    }

    private static int[] getCLUT(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            return null;
        }
        int[] iArr = new int[i2];
        int i3 = (i2 * 3) + i;
        int i4 = 0;
        while (true) {
            int i5 = i4 + 1;
            int i6 = i + 1;
            int i7 = i6 + 1;
            int i8 = ((bArr[i] & 255) << 16) | (-16777216) | ((bArr[i6] & 255) << 8);
            int i9 = i7 + 1;
            iArr[i4] = i8 | (bArr[i7] & 255);
            if (i9 >= i3) {
                return iArr;
            }
            i4 = i5;
            i = i9;
        }
    }

    public static String getDecimalSubstring(String str) {
        int i = 0;
        while (i < str.length() && Character.isDigit(str.charAt(i))) {
            i++;
        }
        return str.substring(0, i);
    }

    public static int bytesToInt(byte[] bArr, int i, int i2) {
        if (i2 > 4) {
            throw new IllegalArgumentException("length must be <= 4 (only 32-bit integer supported): " + i2);
        }
        if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
            throw new IndexOutOfBoundsException("Out of the bounds: src=[" + bArr.length + "], offset=" + i + ", length=" + i2);
        }
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            i3 = (i3 << 8) | (bArr[i + i4] & 255);
        }
        if (i3 >= 0) {
            return i3;
        }
        throw new IllegalArgumentException("src cannot be parsed as a positive integer: " + i3);
    }

    public static long bytesToRawLong(byte[] bArr, int i, int i2) {
        if (i2 > 8) {
            throw new IllegalArgumentException("length must be <= 8 (only 64-bit long supported): " + i2);
        }
        if (i >= 0 && i2 >= 0 && i + i2 <= bArr.length) {
            long j = 0;
            for (int i3 = 0; i3 < i2; i3++) {
                j = (j << 8) | ((long) (bArr[i + i3] & 255));
            }
            return j;
        }
        throw new IndexOutOfBoundsException("Out of the bounds: src=[" + bArr.length + "], offset=" + i + ", length=" + i2);
    }

    public static byte[] unsignedIntToBytes(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + i);
        }
        byte[] bArr = new byte[byteNumForUnsignedInt(i)];
        unsignedIntToBytes(i, bArr, 0);
        return bArr;
    }

    public static byte[] signedIntToBytes(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + i);
        }
        byte[] bArr = new byte[byteNumForSignedInt(i)];
        signedIntToBytes(i, bArr, 0);
        return bArr;
    }

    public static int unsignedIntToBytes(int i, byte[] bArr, int i2) {
        return intToBytes(i, bArr, i2, false);
    }

    public static int signedIntToBytes(int i, byte[] bArr, int i2) {
        return intToBytes(i, bArr, i2, true);
    }

    public static int byteNumForUnsignedInt(int i) {
        return byteNumForInt(i, false);
    }

    public static int byteNumForSignedInt(int i) {
        return byteNumForInt(i, true);
    }

    private static int intToBytes(int i, byte[] bArr, int i2, boolean z) {
        int iByteNumForInt = byteNumForInt(i, z);
        if (i2 < 0 || i2 + iByteNumForInt > bArr.length) {
            throw new IndexOutOfBoundsException("Not enough space to write. Required bytes: " + iByteNumForInt);
        }
        int i3 = iByteNumForInt - 1;
        while (i3 >= 0) {
            bArr[i2 + i3] = (byte) (i & 255);
            i3--;
            i >>>= 8;
        }
        return iByteNumForInt;
    }

    private static int byteNumForInt(int i, boolean z) {
        if (i < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + i);
        }
        if (z) {
            if (i <= 127) {
                return 1;
            }
            if (i <= 32767) {
                return 2;
            }
            return i <= 8388607 ? 3 : 4;
        }
        if (i <= 255) {
            return 1;
        }
        if (i <= 65535) {
            return 2;
        }
        return i <= 16777215 ? 3 : 4;
    }

    public static String byteToHex(byte b) {
        char[] cArr = HEX_CHARS;
        return new String(new char[]{cArr[(b & 255) >>> 4], cArr[b & MidiConstants.STATUS_CHANNEL_MASK]});
    }

    public static String stripTrailingFs(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("(?i)f*$", "");
    }
}
