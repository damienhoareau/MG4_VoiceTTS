package com.saicmotor.tts.tester;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Outil de comparaison autonome, lance a la main sur le vehicule (ou
 * l'emulateur) -- voir docs/TTS_ENGINE.md. Deux chemins independants pour
 * entendre le meme texte tape :
 *
 *  - "moteur direct" : sherpa-onnx appele directement par cette app
 *    (TesterEngine), sans passer par com.saicmotor.voicetts -- isole la
 *    synthese elle-meme (qualite/vitesse) de tout le reste du pipeline.
 *    Langue + voix choisies via deux Spinners (plusieurs voix Piper par
 *    langue, voir VoiceDownloader) ; telechargees a la demande, pas d'adb push.
 *  - "service systeme" : passe par l'app systeme installee via AIDL
 *    (SystemServiceClient) -- teste le pipeline complet (TtsService,
 *    focus audio, file d'attente), dans la langue actuellement reglee
 *    dans le menu "Voix" du vehicule.
 */
public class MainActivity extends Activity {

    private Spinner langSpinner;
    private Spinner voiceSpinner;
    private TextView voiceStatus;
    private Button btnDownload;
    private EditText editText;
    private Button btnDirect;
    private Button btnSystem;
    private Button btnStop;
    private TextView statusText;

    private TesterEngine engine;
    private SystemServiceClient systemClient;
    private VoiceDownloader downloader;

    private List<VoiceDownloader.VoiceOption> currentVoices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        langSpinner = findViewById(R.id.lang_spinner);
        voiceSpinner = findViewById(R.id.voice_spinner);
        voiceStatus = findViewById(R.id.voice_status);
        btnDownload = findViewById(R.id.btn_download);
        editText = findViewById(R.id.edit_text);
        btnDirect = findViewById(R.id.btn_speak_direct);
        btnSystem = findViewById(R.id.btn_speak_system);
        btnStop = findViewById(R.id.btn_stop);
        statusText = findViewById(R.id.status_text);

        engine = new TesterEngine(this);
        downloader = new VoiceDownloader(this);
        systemClient = new SystemServiceClient(this);
        systemClient.connect((connected, message) -> runOnUiThread(() -> {
            if (!connected) {
                setStatus("Service système: " + message);
            }
        }));

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, TesterEngine.LANG_LABELS);
        langSpinner.setAdapter(langAdapter);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateVoices(TesterEngine.LANGS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        voiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshVoiceStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        populateVoices(TesterEngine.LANGS[0]);

        btnDownload.setOnClickListener(v -> downloadSelectedVoice());
        btnDirect.setOnClickListener(v -> speakDirect());
        btnSystem.setOnClickListener(v -> speakViaSystem());
        btnStop.setOnClickListener(v -> stopAll());
    }

    private void populateVoices(String lang) {
        currentVoices = VoiceDownloader.voicesFor(lang);
        List<String> labels = new ArrayList<>();
        for (VoiceDownloader.VoiceOption v : currentVoices) {
            labels.add(v.label);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, labels);
        voiceSpinner.setAdapter(adapter);
        refreshVoiceStatus();
    }

    private String selectedLang() {
        return TesterEngine.LANGS[langSpinner.getSelectedItemPosition()];
    }

    private VoiceDownloader.VoiceOption selectedVoice() {
        int pos = voiceSpinner.getSelectedItemPosition();
        if (pos < 0 || pos >= currentVoices.size()) return null;
        return currentVoices.get(pos);
    }

    private void refreshVoiceStatus() {
        VoiceDownloader.VoiceOption voice = selectedVoice();
        if (voice == null) {
            voiceStatus.setText("?");
            return;
        }
        boolean present = downloader.isDownloaded(selectedLang(), voice.id);
        voiceStatus.setText(voice.label + " — " + (present ? "téléchargée" : "absente"));
        btnDownload.setText(present ? "Re-télécharger" : "Télécharger");
    }

    private void downloadSelectedVoice() {
        String lang = selectedLang();
        VoiceDownloader.VoiceOption voice = selectedVoice();
        if (voice == null) return;
        btnDownload.setEnabled(false);
        voiceStatus.setText(voice.label + " — téléchargement… 0%");
        new Thread(() -> downloader.download(lang, voice.id, new VoiceDownloader.Progress() {
            @Override
            public void onProgress(float fraction) {
                runOnUiThread(() -> voiceStatus.setText(
                        voice.label + " — téléchargement… " + Math.round(fraction * 100) + "%"));
            }

            @Override
            public void onDone(boolean ok, String message) {
                runOnUiThread(() -> {
                    setStatus(message);
                    btnDownload.setEnabled(true);
                    refreshVoiceStatus();
                });
            }
        }), "voice-download").start();
    }

    private void speakDirect() {
        String text = currentText();
        if (text == null) return;
        String lang = selectedLang();
        VoiceDownloader.VoiceOption voice = selectedVoice();
        if (voice == null) return;
        if (!downloader.isDownloaded(lang, voice.id)) {
            setStatus("Voix '" + voice.label + "' non téléchargée — utilisez le bouton Télécharger ci-dessus.");
            return;
        }
        setBusy(true);
        setStatus("Moteur direct: chargement / synthèse (" + voice.label + ")…");
        new Thread(() -> {
            TesterEngine.Result r = engine.speak(lang, voice.id, text);
            runOnUiThread(() -> {
                setStatus("Moteur direct (" + voice.label + "): " + r.message);
                setBusy(false);
            });
        }, "tester-speak").start();
    }

    private void speakViaSystem() {
        String text = currentText();
        if (text == null) return;
        setBusy(true);
        if (!systemClient.isConnected()) {
            setStatus("Service système: connexion…");
            systemClient.connect((connected, message) -> runOnUiThread(() -> {
                if (connected) {
                    sendToSystem(text);
                } else {
                    setStatus("Service système: " + message);
                    setBusy(false);
                }
            }));
        } else {
            sendToSystem(text);
        }
    }

    private void sendToSystem(String text) {
        setStatus("Service système: envoi…");
        boolean sent = systemClient.speak(text, new SystemServiceClient.SpeakStatus() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> setStatus("Service système: lecture en cours…"));
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    setStatus("Service système: erreur (focus audio refusé ?)");
                    setBusy(false);
                });
            }

            @Override
            public void onCompleted() {
                runOnUiThread(() -> {
                    setStatus("Service système: terminé.");
                    setBusy(false);
                });
            }
        });
        if (!sent) {
            setStatus("Service système: appel échoué (service non lié).");
            setBusy(false);
        }
    }

    private void stopAll() {
        engine.stop();
        systemClient.stop();
        setStatus("Stop demandé.");
        setBusy(false);
    }

    private String currentText() {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            setStatus("Entrez un texte d'abord.");
            return null;
        }
        return text;
    }

    private void setBusy(boolean b) {
        btnDirect.setEnabled(!b);
        btnSystem.setEnabled(!b);
    }

    private void setStatus(String s) {
        statusText.setText(s);
    }

    @Override
    protected void onDestroy() {
        engine.release();
        systemClient.disconnect();
        super.onDestroy();
    }
}
