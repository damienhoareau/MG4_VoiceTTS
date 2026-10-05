package com.saicmotor.voicetts;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.saicmotor.speech.loader.ConfigLoader;
import com.saicmotor.speech.loader.EngineType;
import com.saicmotor.speech.loader.LanguagePropsType;
import java.util.Properties;

/* JADX INFO: loaded from: classes3.dex */
public class TTSConfigLoadHelper {
    public static final String ConfigKey = "Config";
    public static final String LangKey = "Lang";
    public static final String ServiceKey = "ServiceEngine";
    private static PlatformConfig platformConfig;

    public static PlatformConfig loadTTSLangConfig(Context context) {
        PlatformConfig platformConfig2 = platformConfig;
        if (platformConfig2 != null) {
            return platformConfig2;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigKey, 0);
        Log.d("TTSConfigLoadHelper", "local config is null, load platform config");
        Properties propertiesLoadPlatformPropsConfig = ConfigLoader.loadPlatformPropsConfig();
        String property = propertiesLoadPlatformPropsConfig.getProperty(com.saicmotor.speech.loader.Constants.ProjectNameKey, "");
        String property2 = propertiesLoadPlatformPropsConfig.getProperty(com.saicmotor.speech.loader.Constants.ProjectLangKey, LanguagePropsType.ENGLISH_IND);
        String property3 = propertiesLoadPlatformPropsConfig.getProperty(com.saicmotor.speech.loader.Constants.TTSEngineKey, EngineType.NUANCE_FULL);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString(LangKey, property2);
        editorEdit.putString(com.saicmotor.speech.loader.Constants.TTSEngineKey, property3);
        editorEdit.commit();
        String string = sharedPreferences.getString(LangKey, "");
        String string2 = sharedPreferences.getString(com.saicmotor.speech.loader.Constants.TTSEngineKey, "");
        PlatformConfig platformConfig3 = new PlatformConfig();
        platformConfig = platformConfig3;
        platformConfig3.setProjectName(property);
        platformConfig.setLang(string);
        platformConfig.setEngine(string2);
        return platformConfig;
    }
}
