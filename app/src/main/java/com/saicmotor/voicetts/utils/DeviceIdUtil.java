package com.saicmotor.voicetts.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.saicmotor.voicetts.log.Logger;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/* JADX INFO: loaded from: classes3.dex */
public class DeviceIdUtil {
    public static final Logger LOG = new Logger(DeviceIdUtil.class);

    public static String getDeviceId(Context context) {
        String deviceIdByUUID = getDeviceIdByUUID(context);
        LOG.i("getDeviceId()===" + deviceIdByUUID);
        return deviceIdByUUID;
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0086  */
    private static String getDeviceIdByUUID(Context context) {
        String strBytesToHex;
        String string = Settings.System.getString(context.getContentResolver(), "deviceId");
        if (string != null && !"".equals(string)) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        String imei = getIMEI(context);
        String androidId = getAndroidId(context);
        String serial = getSERIAL();
        String strReplace = getDeviceUUID().replace("-", "");
        if (imei != null && imei.length() > 0) {
            sb.append(imei);
            sb.append("|");
        }
        if (androidId != null && androidId.length() > 0) {
            sb.append(androidId);
            sb.append("|");
        }
        if (serial != null && serial.length() > 0) {
            sb.append(serial);
            sb.append("|");
        }
        if (strReplace != null && strReplace.length() > 0) {
            sb.append(strReplace);
        }
        if (sb.length() > 0) {
            try {
                strBytesToHex = bytesToHex(getHashByString(sb.toString()));
                if (strBytesToHex == null || strBytesToHex.length() <= 0) {
                    strBytesToHex = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                strBytesToHex = "";
            }
        } else {
            strBytesToHex = "";
        }
        if (strBytesToHex == null || "".equals(strBytesToHex)) {
            strBytesToHex = UUID.randomUUID().toString().replace("-", "");
        }
        SpUtil.getInstance(context).putString("deviceId", strBytesToHex);
        Settings.System.putString(context.getContentResolver(), "deviceId", strBytesToHex);
        return strBytesToHex;
    }

    private static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            return telephonyManager != null ? telephonyManager.getImei() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getSERIAL() {
        try {
            return Build.getSerial();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getDeviceUUID() {
        try {
            return new UUID(("3883756" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.DEVICE.length() % 10) + (Build.HARDWARE.length() % 10) + (Build.ID.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10) + (Build.getSerial().length() % 10)).hashCode(), Build.getSerial().hashCode()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] getHashByString(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception unused) {
            return "".getBytes();
        }
    }

    private static String bytesToHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                sb.append("0");
            }
            sb.append(hexString);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }
}
