package com.saicmotor.voicetts.sherpa;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Telecharge des voix Piper depuis HuggingFace (depot miroir csukuangfj)
 * dans le dossier prive de l'app -- voir docs/TTS_ENGINE.md. Copie de
 * {@code com.saicmotor.tts.tester.VoiceDownloader} (meme catalogue) ; dupliquee
 * plutot que partagee car :app et :tts-tester sont deux modules application
 * independants, pas de module bibliotheque commun pour l'instant.
 *
 * Stocke sous sherpa/voices/<lang>/<voiceId>/, distinct du chemin plat
 * sherpa/voices/<lang>/ qu'utilise la voix par defaut embarquee en asset
 * (SherpaVoiceEngine.ensureVoiceDir) -- les deux coexistent sans collision.
 */
public class VoiceDownloader {

    private static final String TAG = "VoiceDownloader";

    public static class VoiceOption {
        public final String id;
        public final String repo;
        public final String stem;
        public final String label;

        VoiceOption(String id, String repo, String stem, String label) {
            this.id = id;
            this.repo = repo;
            this.stem = stem;
            this.label = label;
        }
    }

    private static final Map<String, List<VoiceOption>> CATALOG = new LinkedHashMap<>();

    private static void add(String lang, String id, String repo, String stem, String label) {
        CATALOG.computeIfAbsent(lang, k -> new ArrayList<>()).add(new VoiceOption(id, repo, stem, label));
    }

    static {
        add("en", "lessac-medium", "csukuangfj/vits-piper-en_US-lessac-medium", "en_US-lessac-medium", "Lessac (US, medium)");
        add("en", "amy-medium", "csukuangfj/vits-piper-en_US-amy-medium", "en_US-amy-medium", "Amy (US, medium)");
        add("en", "ryan-high", "csukuangfj/vits-piper-en_US-ryan-high", "en_US-ryan-high", "Ryan (US, high)");
        add("en", "libritts_r-medium", "csukuangfj/vits-piper-en_US-libritts_r-medium", "en_US-libritts_r-medium", "LibriTTS-R (US, medium)");
        add("en", "alan-medium", "csukuangfj/vits-piper-en_GB-alan-medium", "en_GB-alan-medium", "Alan (GB, medium)");
        add("en", "jenny_dioco-medium", "csukuangfj/vits-piper-en_GB-jenny_dioco-medium", "en_GB-jenny_dioco-medium", "Jenny (GB, medium)");
        add("en", "southern_english_female-medium", "csukuangfj/vits-piper-en_GB-southern_english_female-medium", "en_GB-southern_english_female-medium", "Southern English Female (GB, medium)");
        add("en", "alba-medium", "csukuangfj/vits-piper-en_GB-alba-medium", "en_GB-alba-medium", "Alba (GB, medium)");

        add("fr", "siwis-medium", "csukuangfj/vits-piper-fr_FR-siwis-medium", "fr_FR-siwis-medium", "Siwis (medium)");
        add("fr", "siwis-low", "csukuangfj/vits-piper-fr_FR-siwis-low", "fr_FR-siwis-low", "Siwis (low)");
        add("fr", "upmc-medium", "csukuangfj/vits-piper-fr_FR-upmc-medium", "fr_FR-upmc-medium", "UPMC (medium)");
        add("fr", "tom-medium", "csukuangfj/vits-piper-fr_FR-tom-medium", "fr_FR-tom-medium", "Tom (medium)");
        add("fr", "mls-medium", "csukuangfj/vits-piper-fr_FR-mls-medium", "fr_FR-mls-medium", "MLS (medium)");

        add("de", "thorsten-medium", "csukuangfj/vits-piper-de_DE-thorsten-medium", "de_DE-thorsten-medium", "Thorsten (medium)");
        add("de", "thorsten-high", "csukuangfj/vits-piper-de_DE-thorsten-high", "de_DE-thorsten-high", "Thorsten (high)");
        add("de", "thorsten-low", "csukuangfj/vits-piper-de_DE-thorsten-low", "de_DE-thorsten-low", "Thorsten (low)");
        add("de", "kerstin-low", "csukuangfj/vits-piper-de_DE-kerstin-low", "de_DE-kerstin-low", "Kerstin (low)");
        add("de", "mls-medium", "csukuangfj/vits-piper-de_DE-mls-medium", "de_DE-mls-medium", "MLS (medium)");

        add("es", "davefx-medium", "csukuangfj/vits-piper-es_ES-davefx-medium", "es_ES-davefx-medium", "Davefx (ES, medium)");
        add("es", "sharvard-medium", "csukuangfj/vits-piper-es_ES-sharvard-medium", "es_ES-sharvard-medium", "Sharvard (ES, medium)");
        add("es", "mls_9972-low", "csukuangfj/vits-piper-es_ES-mls_9972-low", "es_ES-mls_9972-low", "MLS 9972 (ES, low)");
        add("es", "mls_10246-low", "csukuangfj/vits-piper-es_ES-mls_10246-low", "es_ES-mls_10246-low", "MLS 10246 (ES, low)");
        add("es", "ald-medium", "csukuangfj/vits-piper-es_MX-ald-medium", "es_MX-ald-medium", "Ald (MX, medium)");
    }

    public static List<VoiceOption> voicesFor(String lang) {
        return CATALOG.getOrDefault(lang, new ArrayList<>());
    }

    public interface Progress {
        void onProgress(float fraction);
        void onDone(boolean ok, String message);
    }

    private final Context appContext;

    public VoiceDownloader(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public File voiceDir(String lang, String voiceId) {
        return new File(appContext.getNoBackupFilesDir(), "sherpa/voices/" + lang + "/" + voiceId);
    }

    public boolean isDownloaded(String lang, String voiceId) {
        return new File(voiceDir(lang, voiceId), "model.onnx").isFile();
    }

    /** Bloquant : a appeler depuis un thread de fond. */
    public void download(String lang, String voiceId, Progress progress) {
        VoiceOption voice = null;
        for (VoiceOption v : voicesFor(lang)) {
            if (v.id.equals(voiceId)) {
                voice = v;
                break;
            }
        }
        if (voice == null) {
            progress.onDone(false, "Voix inconnue: " + lang + "/" + voiceId);
            return;
        }
        File dir = voiceDir(lang, voiceId);
        dir.mkdirs();
        File modelTmp = new File(dir, "model.onnx.part");
        File model = new File(dir, "model.onnx");
        File tokens = new File(dir, "tokens.txt");
        try {
            downloadFile(
                    "https://huggingface.co/" + voice.repo + "/resolve/main/" + voice.stem + ".onnx",
                    modelTmp, progress);
            if (!modelTmp.renameTo(model)) {
                throw new IOException("impossible de renommer " + modelTmp + " -> " + model);
            }
            downloadFile(
                    "https://huggingface.co/" + voice.repo + "/resolve/main/tokens.txt",
                    tokens, null);
            progress.onDone(true, "Voix '" + voice.label + "' téléchargée.");
        } catch (Exception e) {
            Log.e(TAG, "download('" + lang + "/" + voiceId + "') failed", e);
            progress.onDone(false, "Échec: " + e.getMessage());
        }
    }

    private void downloadFile(String url, File dst, Progress progress) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(30_000);
        conn.setReadTimeout(60_000);
        try {
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code + " pour " + url);
            }
            long total = conn.getContentLengthLong();
            long done = 0;
            try (InputStream in = conn.getInputStream();
                 OutputStream out = new FileOutputStream(dst)) {
                byte[] buffer = new byte[1 << 16];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    done += read;
                    if (progress != null && total > 0) {
                        progress.onProgress((float) done / total);
                    }
                }
            }
        } finally {
            conn.disconnect();
        }
    }
}
