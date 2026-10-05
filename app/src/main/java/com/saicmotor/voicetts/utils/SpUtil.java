package com.saicmotor.voicetts.utils;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes3.dex */
public class SpUtil {
    public static final String NOWAKEUP_COMMAND_ENABLE_KEY = "NOWAKEUP_COMMAND_ENABLE";
    public static final String ONESHOT_ENABLE_KEY = "ONESHOT_ENABLE";
    private static final String SP_NAME = "vr_settings";
    public static final String WAKEUP_WORDS_ENABLE_KEY = "WAKEUP_WORDS_ENABLE";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences hmSpref;
    private static SpUtil spUtil;

    private SpUtil(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
        hmSpref = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    public void putString(String str, String str2) {
        editor.putString(str, str2);
        editor.commit();
    }

    public String getString(String str) {
        return hmSpref.getString(str, "");
    }

    public void putBoolean(String str, boolean z) {
        editor.putBoolean(str, z);
        editor.commit();
    }

    public boolean getBoolean(String str) {
        return hmSpref.getBoolean(str, true);
    }

    public void putInteger(String str, int i) {
        editor.putInt(str, i);
        editor.commit();
    }

    public int getInteger(String str) {
        return hmSpref.getInt(str, 0);
    }
}
