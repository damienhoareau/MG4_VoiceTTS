package android.util;

/* JADX INFO: loaded from: classes2.dex */
public final class ByteStringUtils {
    private static final char[] HEX_LOWERCASE_ARRAY = "0123456789abcdef".toCharArray();
    private static final char[] HEX_UPPERCASE_ARRAY = "0123456789ABCDEF".toCharArray();

    private ByteStringUtils() {
    }

    public static String toHexString(byte[] bArr) {
        if (bArr == null || bArr.length == 0 || bArr.length % 2 != 0) {
            return null;
        }
        int length = bArr.length;
        char[] cArr = new char[length * 2];
        for (int i = 0; i < length; i++) {
            int i2 = bArr[i] & 255;
            int i3 = i * 2;
            char[] cArr2 = HEX_UPPERCASE_ARRAY;
            cArr[i3] = cArr2[i2 >>> 4];
            cArr[i3 + 1] = cArr2[i2 & 15];
        }
        return new String(cArr);
    }

    public static byte[] fromHexToByteArray(String str) {
        if (str == null || str.length() == 0 || str.length() % 2 != 0) {
            return null;
        }
        char[] charArray = str.toCharArray();
        int length = charArray.length / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((getIndex(charArray[i2 + 1]) & 15) | ((getIndex(charArray[i2]) << 4) & 240));
        }
        return bArr;
    }

    private static int getIndex(char c) {
        int i = 0;
        while (true) {
            char[] cArr = HEX_UPPERCASE_ARRAY;
            if (i >= cArr.length) {
                return -1;
            }
            if (cArr[i] == c || HEX_LOWERCASE_ARRAY[i] == c) {
                return i;
            }
            i++;
        }
    }
}
