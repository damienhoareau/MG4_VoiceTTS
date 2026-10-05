package com.saicmotor.voicetts;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.saicmotor.voicetts.sherpa.SherpaVoiceEngine;
import com.saicmotor.voicetts.sherpa.VoiceDownloader;
import com.saicmotor.voicetts.sherpa.VoicePrefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Ecran de reglages de la voix (voir docs/TTS_ENGINE.md) + point d'entree
 * historique qui demarre TtsService. Choisir/telecharger une voix ici est
 * optionnel : l'app reste utilisable hors-ligne des l'installation grace a
 * la voix par defaut embarquee en asset (VoicePrefs n'a d'effet que si une
 * voix a ete explicitement choisie ET telechargee, voir SherpaVoiceEngine).
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] LANGS = {"en", "fr", "de", "es"};
    private static final String[] LANG_LABELS = {"English", "Français", "Deutsch", "Español"};

    private Spinner langSpinner;
    private Spinner voiceSpinner;
    private TextView voiceStatus;
    private Button btnDownload;
    private Button btnUseVoice;
    private Button btnUseDefault;
    private EditText editText;
    private Button btnPlay;
    private TextView playStatus;
    private Button button;

    private VoiceDownloader downloader;
    private VoicePrefs voicePrefs;
    private SherpaVoiceEngine sherpaEngine;
    private List<VoiceDownloader.VoiceOption> currentVoices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        downloader = new VoiceDownloader(this);
        voicePrefs = new VoicePrefs(this);
        sherpaEngine = new SherpaVoiceEngine(this);

        langSpinner = findViewById(R.id.lang_spinner);
        voiceSpinner = findViewById(R.id.voice_spinner);
        voiceStatus = findViewById(R.id.voice_status);
        btnDownload = findViewById(R.id.btn_download);
        btnUseVoice = findViewById(R.id.btn_use_voice);
        btnUseDefault = findViewById(R.id.btn_use_default);
        editText = findViewById(R.id.edit_text);
        btnPlay = findViewById(R.id.btn_play);
        playStatus = findViewById(R.id.play_status);

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, LANG_LABELS);
        langSpinner.setAdapter(langAdapter);
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateVoices(LANGS[position]);
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
        populateVoices(LANGS[0]);

        btnDownload.setOnClickListener(v -> downloadSelectedVoice());
        btnUseVoice.setOnClickListener(v -> useSelectedVoice());
        btnUseDefault.setOnClickListener(v -> useDefaultVoice());
        btnPlay.setOnClickListener(v -> playText());

        // La demande de permission stockage/reseau au lancement a ete retiree :
        // elle n'existait que pour les ressources de l'ancien moteur iFlytek
        // (/vr/speech/Res/xTTSRes sur le stockage externe).
        Button button = (Button) findViewById(R.id.btn_start_service);
        this.button = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.saicmotor.voicetts.MainActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d("MainActivity", "start service...");
                Intent intent = new Intent(MainActivity.this, (Class<?>) TtsService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    MainActivity.this.startForegroundService(intent);
                } else {
                    MainActivity.this.startService(intent);
                }
            }
        });
        Intent intent = new Intent(this, (Class<?>) TtsService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        // Ne plus finish() ici : cet ecran sert maintenant aussi de reglages
        // de voix (voir ci-dessus), il doit rester ouvert.
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
        return LANGS[langSpinner.getSelectedItemPosition()];
    }

    private VoiceDownloader.VoiceOption selectedVoice() {
        int pos = voiceSpinner.getSelectedItemPosition();
        if (pos < 0 || pos >= currentVoices.size()) return null;
        return currentVoices.get(pos);
    }

    private void refreshVoiceStatus() {
        String lang = selectedLang();
        VoiceDownloader.VoiceOption voice = selectedVoice();
        if (voice == null) {
            voiceStatus.setText("?");
            return;
        }
        boolean downloaded = downloader.isDownloaded(lang, voice.id);
        String active = voicePrefs.getSelectedVoiceId(lang);
        boolean isActive = voice.id.equals(active);
        StringBuilder sb = new StringBuilder();
        sb.append(voice.label).append(" — ").append(downloaded ? "téléchargée" : "non téléchargée");
        if (isActive) {
            sb.append(" — ACTIVE pour ").append(lang);
        } else if (active == null) {
            sb.append(" (voix active pour ").append(lang).append(" : par défaut embarquée)");
        }
        voiceStatus.setText(sb.toString());
        btnDownload.setText(downloaded ? "Re-télécharger" : "Télécharger");
        btnUseVoice.setEnabled(downloaded && !isActive);
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
                    btnDownload.setEnabled(true);
                    refreshVoiceStatus();
                });
            }
        }), "voice-download").start();
    }

    private void useSelectedVoice() {
        String lang = selectedLang();
        VoiceDownloader.VoiceOption voice = selectedVoice();
        if (voice == null || !downloader.isDownloaded(lang, voice.id)) return;
        voicePrefs.setSelectedVoiceId(lang, voice.id);
        refreshVoiceStatus();
    }

    private void useDefaultVoice() {
        voicePrefs.clearSelectedVoiceId(selectedLang());
        refreshVoiceStatus();
    }

    /**
     * Teste la synthese avec la langue choisie ci-dessus, via la voix
     * REELLEMENT active (VoicePrefs, par defaut embarquee si rien de choisi) --
     * donc exactement ce que la voiture entendrait, pas juste un apercu de
     * la voix selectionnee dans la liste.
     */
    private void playText() {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            playStatus.setText("Entrez un texte d'abord.");
            return;
        }
        String lang = selectedLang();
        btnPlay.setEnabled(false);
        playStatus.setText("Synthèse en cours (" + lang + ")…");
        new Thread(() -> {
            long t0 = System.nanoTime();
            sherpaEngine.speakAndWait(text, lang);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            runOnUiThread(() -> {
                playStatus.setText("Terminé en " + ms + "ms.");
                btnPlay.setEnabled(true);
            });
        }, "play-test").start();
    }

    @Override
    protected void onDestroy() {
        sherpaEngine.release();
        super.onDestroy();
    }
}
