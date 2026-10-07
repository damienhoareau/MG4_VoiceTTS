package com.saicmotor.voicetts.sherpa;

import android.media.AudioFormat;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Enregistre com.saicmotor.voicetts comme moteur TTS systeme standard
 * (selectionnable dans Parametres > Synthese vocale > Moteur prefere), pour
 * que n'importe quelle app utilisant android.speech.tts.TextToSpeech -- pas
 * seulement notre propre AIDL ITtsService -- profite du meme moteur offline.
 * Inspire de l'implementation de SherpaTTS
 * (github.com/woheller69/ttsengine, com/k2fsa/sherpa/onnx/tts/engine/TtsService.kt),
 * adapte en Java et branche sur SherpaVoiceEngine/VoicePrefs (meme moteur,
 * memes voix, que notre propre TtsService AIDL -- voir docs/TTS_ENGINE.md).
 *
 * Contrairement a SherpaTTS, pas de generateWithCallback() (incompatible
 * Java) : le texte est decoupe sur la ponctuation et synthetise segment par
 * segment (SherpaVoiceEngine.streamSegments), chaque segment etant renvoye a
 * SynthesisCallback en morceaux bornes par maxBufferSize des qu'il est pret,
 * pendant que le suivant se calcule.
 */
public class SherpaTextToSpeechService extends TextToSpeechService {

    private static final String TAG = "SherpaTtsService";

    private SherpaVoiceEngine engine;

    @Override
    public void onCreate() {
        super.onCreate();
        engine = new SherpaVoiceEngine(getApplicationContext());
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        if (engine != null) {
            engine.release();
        }
        super.onDestroy();
    }

    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {
        return SherpaVoiceEngine.langForIso3(lang) != null
                ? TextToSpeech.LANG_AVAILABLE
                : TextToSpeech.LANG_NOT_SUPPORTED;
    }

    @Override
    protected String[] onGetLanguage() {
        return new String[]{lastLang != null ? lastLang : "eng", "", ""};
    }

    private volatile String lastLang;

    @Override
    protected int onLoadLanguage(String lang, String country, String variant) {
        String code = SherpaVoiceEngine.langForIso3(lang);
        if (code == null) {
            return TextToSpeech.LANG_NOT_SUPPORTED;
        }
        lastLang = lang;
        return TextToSpeech.LANG_AVAILABLE;
    }

    @Override
    protected void onStop() {
        if (engine != null) {
            engine.stop();
        }
    }

    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        String code = SherpaVoiceEngine.langForIso3(request.getLanguage());
        if (code == null) {
            callback.error(TextToSpeech.ERROR_NOT_INSTALLED_YET);
            return;
        }
        String text = request.getCharSequenceText().toString();
        if (text.trim().isEmpty()) {
            callback.start(22050, AudioFormat.ENCODING_PCM_16BIT, 1);
            callback.done();
            return;
        }

        // Streaming par segments : callback.start() des qu'on connait la frequence
        // (sans synthetiser), puis chaque segment est envoye au fur et a mesure.
        try {
            callback.start(engine.sampleRate(code), AudioFormat.ENCODING_PCM_16BIT, 1);
            final int maxBuffer = callback.getMaxBufferSize();
            engine.streamSegments(text, code, samples -> {
                byte[] pcm16 = floatToPcm16(samples);
                int offset = 0;
                while (offset < pcm16.length) {
                    int len = Math.min(maxBuffer, pcm16.length - offset);
                    callback.audioAvailable(pcm16, offset, len);
                    offset += len;
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "synthesize('" + code + "') failed", t);
            callback.error();
            return;
        }
        callback.done();
    }

    private static byte[] floatToPcm16(float[] samples) {
        byte[] out = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            int s = (int) (samples[i] * 32767f);
            if (s > 32767) s = 32767;
            if (s < -32768) s = -32768;
            out[2 * i] = (byte) s;
            out[2 * i + 1] = (byte) (s >> 8);
        }
        return out;
    }
}
