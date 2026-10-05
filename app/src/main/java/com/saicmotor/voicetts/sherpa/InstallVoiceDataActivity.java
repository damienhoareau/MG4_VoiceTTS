package com.saicmotor.voicetts.sherpa;

import android.app.Activity;
import android.os.Bundle;

/**
 * Repond a android.speech.tts.engine.INSTALL_TTS_DATA. Rien a installer via
 * ce chemin standard : les voix se gerent dans MainActivity (telechargement,
 * voir docs/TTS_ENGINE.md) ou sont deja presentes par defaut.
 */
public class InstallVoiceDataActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
