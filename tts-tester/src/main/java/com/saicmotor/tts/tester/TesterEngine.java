package com.saicmotor.tts.tester;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import com.k2fsa.sherpa.onnx.GeneratedAudio;
import com.k2fsa.sherpa.onnx.OfflineTts;
import com.k2fsa.sherpa.onnx.OfflineTtsConfig;
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig;
import com.k2fsa.sherpa.onnx.OfflineTtsVitsModelConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Moteur de synthese direct pour {@code tts-tester} : meme bibliotheque que
 * {@code com.saicmotor.voicetts.sherpa.SherpaVoiceEngine} (voir
 * docs/TTS_ENGINE.md). Les voix sont telechargees a la demande par
 * {@link VoiceDownloader} (dans le dossier prive de l'app -- fichiers ecrits
 * par l'app elle-meme, donc sans le probleme d'isolation FUSE rencontre avec
 * des fichiers pousses par adb) ; seul espeak-ng-data (18 Mo, partage, fixe)
 * est embarque en asset et extrait au premier besoin.
 */
public class TesterEngine {

    private static final String TAG = "TtsTester";

    public static final String[] LANGS = {"en", "fr", "de", "es"};
    public static final String[] LANG_LABELS = {"English", "Français", "Deutsch", "Español"};

    private final Context appContext;
    private final Object lock = new Object();
    private final Map<String, OfflineTts> engines = new HashMap<>();
    private volatile AudioTrack audioTrack;
    private volatile boolean cancelled;

    public TesterEngine(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static class Result {
        public final boolean ok;
        public final String message;

        Result(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }
    }

    private File sherpaDir() {
        return new File(appContext.getExternalFilesDir(null), "sherpa");
    }

    private File ensureDataDir() {
        File dst = new File(sherpaDir(), "espeak-ng-data");
        if (!new File(dst, "phontab").isFile()) {
            Log.i(TAG, "extracting espeak-ng-data...");
            copyAssetDir("sherpa/espeak-ng-data", dst);
        }
        return dst;
    }

    private void copyAssetDir(String assetPath, File dst) {
        AssetManager assets = appContext.getAssets();
        try {
            String[] children = assets.list(assetPath);
            if (children == null || children.length == 0) {
                copyAssetFile(assets, assetPath, dst);
                return;
            }
            dst.mkdirs();
            for (String child : children) {
                copyAssetDir(assetPath + "/" + child, new File(dst, child));
            }
        } catch (IOException e) {
            Log.e(TAG, "copyAssetDir failed for " + assetPath, e);
        }
    }

    private void copyAssetFile(AssetManager assets, String assetPath, File dst) {
        try (InputStream in = assets.open(assetPath)) {
            dst.getParentFile().mkdirs();
            try (OutputStream out = new java.io.FileOutputStream(dst)) {
                byte[] buffer = new byte[1 << 16];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "copyAssetFile failed for " + assetPath, e);
        }
    }

    private OfflineTts ensureEngine(String lang, String voiceId) {
        synchronized (lock) {
            String key = lang + "/" + voiceId;
            OfflineTts cached = engines.get(key);
            if (cached != null) {
                return cached;
            }
            File voiceDir = new File(sherpaDir(), "voices/" + lang + "/" + voiceId);
            File modelFile = new File(voiceDir, "model.onnx");
            if (!modelFile.isFile()) {
                throw new IllegalStateException(
                        "Voix '" + voiceId + "' non téléchargée -- utilisez \"Gérer les langues\".");
            }
            File dataDir = ensureDataDir();

            long t0 = System.nanoTime();
            OfflineTtsVitsModelConfig vits = new OfflineTtsVitsModelConfig();
            vits.setModel(modelFile.getAbsolutePath());
            vits.setTokens(new File(voiceDir, "tokens.txt").getAbsolutePath());
            vits.setDataDir(dataDir.getAbsolutePath());

            OfflineTtsModelConfig modelConfig = new OfflineTtsModelConfig();
            modelConfig.setVits(vits);
            modelConfig.setNumThreads(2);
            modelConfig.setProvider("cpu");
            modelConfig.setDebug(false);

            OfflineTtsConfig config = new OfflineTtsConfig();
            config.setModel(modelConfig);

            OfflineTts tts = new OfflineTts(null, config);
            engines.put(key, tts);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            Log.i(TAG, "engine '" + key + "' ready in " + ms + "ms, sampleRate=" + tts.sampleRate());
            return tts;
        }
    }

    private AudioTrack ensureAudioTrack(int sampleRate) {
        AudioTrack track = audioTrack;
        if (track != null && track.getState() == AudioTrack.STATE_INITIALIZED
                && track.getSampleRate() == sampleRate) {
            return track;
        }
        if (track != null) {
            track.release();
        }
        int minBuf = AudioTrack.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT);
        track = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(Math.max(minBuf, 1) * 3)
                .build();
        audioTrack = track;
        return track;
    }

    /** Bloquant : charge le moteur si besoin, synthetise, joue, attend la fin. */
    public Result speak(String lang, String voiceId, String text) {
        cancelled = false;
        long t0 = System.nanoTime();
        OfflineTts tts;
        try {
            tts = ensureEngine(lang, voiceId);
        } catch (Throwable t) {
            Log.e(TAG, "ensureEngine('" + lang + "/" + voiceId + "') failed", t);
            return new Result(false, "Échec chargement moteur: " + t.getMessage());
        }
        AudioTrack track = ensureAudioTrack(tts.sampleRate());
        track.play();
        try {
            GeneratedAudio audio = tts.generate(text, 0, 1.0f);
            float[] samples = audio.getSamples();
            float peak = 0f;
            for (float s : samples) {
                float a = Math.abs(s);
                if (a > peak) peak = a;
            }
            if (!cancelled && samples.length > 0) {
                track.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            return new Result(true, "OK en " + ms + "ms (" + samples.length + " samples, peak="
                    + String.format("%.4f", peak) + ")");
        } catch (Throwable t) {
            Log.e(TAG, "synthesis failed for lang=" + lang, t);
            return new Result(false, "Erreur: " + t.getMessage());
        }
    }

    /** Barge-in : coupe la lecture en cours (ne peut pas interrompre generate() lui-meme). */
    public void stop() {
        cancelled = true;
        AudioTrack track = audioTrack;
        if (track != null) {
            try {
                track.pause();
                track.flush();
            } catch (IllegalStateException ignored) {
                // Deja arretee/relachee.
            }
        }
    }

    public void release() {
        stop();
        AudioTrack track = audioTrack;
        audioTrack = null;
        if (track != null) {
            track.release();
        }
        synchronized (lock) {
            for (OfflineTts tts : engines.values()) {
                tts.release();
            }
            engines.clear();
        }
    }
}
