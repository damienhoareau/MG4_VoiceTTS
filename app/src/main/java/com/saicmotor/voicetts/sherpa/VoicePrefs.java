package com.saicmotor.voicetts.sherpa;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Memorise la voix choisie par langue (ecran de reglages de MainActivity,
 * voir docs/TTS_ENGINE.md). Null = pas de choix -> SherpaVoiceEngine utilise
 * la voix par defaut embarquee dans les assets (comportement hors-ligne de
 * base, jamais casse par ce mecanisme).
 */
public class VoicePrefs {

    private static final String PREFS_NAME = "sherpa_voice_prefs";

    private final SharedPreferences prefs;

    public VoicePrefs(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getSelectedVoiceId(String lang) {
        return prefs.getString("voice_" + lang, null);
    }

    public void setSelectedVoiceId(String lang, String voiceId) {
        prefs.edit().putString("voice_" + lang, voiceId).apply();
    }

    public void clearSelectedVoiceId(String lang) {
        prefs.edit().remove("voice_" + lang).apply();
    }
}
