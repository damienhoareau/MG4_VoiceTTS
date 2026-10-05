package android.net.wifi.aware;

import android.content.Context;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class WifiAwareUtils {
    public static void validateServiceName(byte[] bArr) throws IllegalArgumentException {
        if (bArr == null) {
            throw new IllegalArgumentException("Invalid service name - null");
        }
        if (bArr.length < 1 || bArr.length > 255) {
            throw new IllegalArgumentException("Invalid service name length - must be between 1 and 255 bytes (UTF-8 encoding)");
        }
        for (byte b : bArr) {
            if ((b & 128) == 0 && ((b < 48 || b > 57) && ((b < 97 || b > 122) && ((b < 65 || b > 90) && b != 45 && b != 46)))) {
                throw new IllegalArgumentException("Invalid service name - illegal characters, allowed = (0-9, a-z,A-Z, -, .)");
            }
        }
    }

    public static boolean validatePassphrase(String str) {
        return str != null && str.length() >= 8 && str.length() <= 63;
    }

    public static boolean validatePmk(byte[] bArr) {
        return bArr != null && bArr.length == 32;
    }

    public static boolean isLegacyVersion(Context context, int i) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getOpPackageName(), 0).targetSdkVersion < i;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }
}
