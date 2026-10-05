package com.saicmotor.voicetts;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes3.dex */
public class CommonUtils {
    public static boolean isEC32Series() {
        String propSysRow = getPropSysRow();
        if (TextUtils.isEmpty(propSysRow) || propSysRow == null) {
            return false;
        }
        return propSysRow.contains("EC32");
    }

    public static boolean isTTSeries() {
        String propSysRow = getPropSysRow();
        if (TextUtils.isEmpty(propSysRow) || propSysRow == null) {
            return false;
        }
        return propSysRow.contains("TT");
    }

    public static String getPropSysRow() {
        return getPropByKey("ro.product.carmode");
    }

    public static String getPropByKey(String str) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", String.class, String.class).invoke(cls, str, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isCRVesrion() {
        String propSysRow = getPropSysRow();
        return propSysRow.contains("CR") || propSysRow.contains("Color Radio") || propSysRow.contains("ColorRadio");
    }

    public static boolean isUseNaviLangBySystemLocale(Context context) {
        // CarConfigManager / Context.CAR_CONFIG_SERVICE sont des API cachees (@hide), pas
        // disponibles au moment de la compilation avec le SDK public. Meme pattern par
        // reflexion que getPropByKey() ci-dessus pour marcher quand meme a l'execution
        // si la classe existe reellement sur l'appareil.
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Object carConfigManager = context.getSystemService("car_config");
                if (carConfigManager != null) {
                    try {
                        int carModel = (int) carConfigManager.getClass().getMethod("getCarModel").invoke(carConfigManager);
                        Log.i("", "CarConfigManager,getCarModelValueByCarConfig = " + carModel);
                        if (carModel == 132) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        String propSysRow = getPropSysRow();
        return propSysRow != null && propSysRow.contains("EH32 MCE MY24");
    }
}
