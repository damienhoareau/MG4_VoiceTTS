package com.saicmotor.speech.loader;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;

/* JADX INFO: loaded from: classes3.dex */
public class ConfigLoader {
    private static final String ConfigFileName = "config.properties";
    public static final String ConfigPath = "/vr/speech/config.properties";
    public static final String TempConfigDir = "/sdcard";
    public static final String TempConfigPath = "/sdcard/config.properties";

    public static Properties loadPlatformPropsConfig() {
        Properties properties = new Properties();
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(ConfigPath));
            properties.load(bufferedInputStream);
            for (String str : properties.stringPropertyNames()) {
                // SettingsStringUtil.DELIMITER (API cachee) remplace par "=" -- purement cosmetique (log).
                Log.v("ConfigLoader", str + "=" + properties.getProperty(str));
            }
            bufferedInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
