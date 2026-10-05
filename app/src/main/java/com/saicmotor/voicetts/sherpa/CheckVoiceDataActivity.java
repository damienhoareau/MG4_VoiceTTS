package com.saicmotor.voicetts.sherpa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;

/**
 * Repond a android.speech.tts.engine.CHECK_TTS_DATA (Parametres Android
 * l'appelle pour savoir si ce moteur a des donnees de voix utilisables).
 * Toujours PASS : chaque langue a au moins sa voix par defaut embarquee en
 * asset, jamais besoin d'installation separee (voir docs/TTS_ENGINE.md).
 */
public class CheckVoiceDataActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> available = new ArrayList<>();
        available.add("eng");
        available.add("fra");
        available.add("deu");
        available.add("spa");
        Intent result = new Intent();
        result.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available);
        result.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, new ArrayList<>());
        setResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS, result);
        finish();
    }
}
