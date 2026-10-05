package com.saicmotor.voicetts.sherpa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

/**
 * Repond a android.speech.tts.engine.GET_SAMPLE_TEXT (bouton "Ecouter un
 * exemple" de Parametres > Synthese vocale). Langue demandee dans l'extra
 * "language" (ISO3, convention historique de ce contrat -- voir
 * GetSampleText.kt de SherpaTTS, meme pattern).
 */
public class GetSampleTextActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String lang = getIntent().getStringExtra("language");
        String text = sampleFor(lang);
        Intent result = new Intent();
        if (text != null) {
            result.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, text);
            setResult(TextToSpeech.LANG_AVAILABLE, result);
        } else {
            setResult(TextToSpeech.LANG_NOT_SUPPORTED, result);
        }
        finish();
    }

    private static String sampleFor(String iso3) {
        if (iso3 == null) return null;
        switch (iso3) {
            case "eng":
                return "This is the sherpa onnx text to speech engine.";
            case "fra":
                return "Ceci est le moteur de synthèse vocale sherpa-onnx.";
            case "deu":
                return "Dies ist die sherpa-onnx Sprachsynthese-Engine.";
            case "spa":
                return "Este es el motor de síntesis de voz sherpa-onnx.";
            default:
                return null;
        }
    }
}
