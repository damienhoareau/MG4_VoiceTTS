package com.saicmotor.voicetts.sherpa;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;
import android.util.Pair;

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
 * Synthese vocale offline via sherpa-onnx (Apache-2.0, k2-fsa) + voix Piper
 * (VITS, embarquees en assets, voir docs/TTS_ENGINE.md). Embarque directement
 * dans l'app -- contrairement a l'etape intermediaire qui passait par l'app
 * SherpaTTS separee (org.woheller69.ttsengine, GPLv3) et l'API Android
 * TextToSpeech : ici on appelle sherpa-onnx en direct, une seule app a
 * installer sur le vehicule.
 *
 * Meme forme que l'ancien PocketVoiceEngine (que ce fichier remplace) : un
 * moteur par langue, charge paresseusement et garde en cache, lecture via
 * AudioTrack float, stop() coupe au prochain chunk.
 */
public class SherpaVoiceEngine {

    private static final String TAG = "SherpaVoiceEngine";

    public static final String LANG_EN = "en";
    public static final String LANG_FR = "fr";
    public static final String LANG_DE = "de";
    public static final String LANG_ES = "es";

    private final Context appContext;
    private final Object initLock = new Object();
    private final Map<String, OfflineTts> engines = new HashMap<>();
    private volatile File dataDir;
    private volatile AudioTrack audioTrack;
    private volatile boolean cancelled;

    public SherpaVoiceEngine(Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * SysLang.EXT_<lang3>_<PAYS3> -> code de langue 2 lettres (correspond aux
     * 4 langues reellement proposees par le menu "Voix" du vehicule, voir
     * docs/POCKET_TTS.md). Retombe sur l'anglais sinon.
     */
    public static String langFor(String sysLang) {
        if (sysLang == null) {
            return LANG_EN;
        }
        if (sysLang.contains("fre") || sysLang.contains("fra")) {
            return LANG_FR;
        }
        if (sysLang.contains("deu")) {
            return LANG_DE;
        }
        if (sysLang.contains("spa")) {
            return LANG_ES;
        }
        return LANG_EN;
    }

    /**
     * Code ISO3 tel que recu par {@link SherpaTextToSpeechService} (Android
     * convertit lui-meme le Locale demande en ISO3 avant d'appeler le moteur,
     * voir docs/TTS_ENGINE.md) -> notre code 2 lettres. Null si non supporte,
     * contrairement a {@link #langFor} qui retombe toujours sur l'anglais :
     * ici il faut pouvoir repondre LANG_NOT_SUPPORTED honnetement.
     */
    public static String langForIso3(String iso3) {
        if (iso3 == null) {
            return null;
        }
        switch (iso3) {
            case "eng":
                return LANG_EN;
            case "fra":
                return LANG_FR;
            case "deu":
                return LANG_DE;
            case "spa":
                return LANG_ES;
            default:
                return null;
        }
    }

    private File ensureDataDir() {
        File dir = this.dataDir;
        if (dir != null) {
            return dir;
        }
        synchronized (initLock) {
            if (this.dataDir != null) {
                return this.dataDir;
            }
            File dst = new File(appContext.getNoBackupFilesDir(), "sherpa/espeak-ng-data");
            if (!new File(dst, "phontab").isFile()) {
                Log.i(TAG, "extracting espeak-ng-data...");
                copyAssetDir("sherpa/espeak-ng-data", dst);
            }
            this.dataDir = dst;
            return dst;
        }
    }

    /**
     * Voix choisie par l'utilisateur (ecran de reglages, voir VoicePrefs) si
     * elle a ete telechargee, sinon la voix par defaut embarquee en asset.
     * Garantit que l'app reste utilisable hors-ligne des l'installation :
     * ce mecanisme ne peut jamais faire manquer la voix de base.
     */
    private File ensureVoiceDir(String lang) {
        VoicePrefs prefs = new VoicePrefs(appContext);
        String selected = prefs.getSelectedVoiceId(lang);
        if (selected != null) {
            VoiceDownloader downloader = new VoiceDownloader(appContext);
            if (downloader.isDownloaded(lang, selected)) {
                return downloader.voiceDir(lang, selected);
            }
            Log.w(TAG, "voix choisie '" + selected + "' pour '" + lang
                    + "' absente du disque, retour a la voix par defaut embarquee");
        }
        File dst = new File(appContext.getNoBackupFilesDir(), "sherpa/voices/" + lang);
        if (!new File(dst, "model.onnx").isFile()) {
            Log.i(TAG, "extracting voice '" + lang + "'...");
            copyAssetDir("sherpa/voices/" + lang, dst);
        }
        return dst;
    }

    /** Copie recursive d'un sous-dossier d'assets vers un dossier reel sur disque
     *  (sherpa-onnx/espeak-ng font de vrais appels fopen(), pas d'acces via
     *  AssetManager -- il faut des fichiers reels). */
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

    /**
     * Charge le moteur d'une langue sans rien synthetiser -- a appeler au
     * demarrage (voir TtsService.onCreate) pour que la premiere vraie phrase
     * n'attende pas le chargement du modele (plusieurs secondes, voir
     * docs/TTS_ENGINE.md). Pas d'exception propagee : un echec ici (pas
     * bloquant, l'engine se chargera simplement a la demande comme avant)
     * ne doit jamais empecher le service de demarrer.
     */
    public void preload(String lang) {
        try {
            ensureEngine(lang);
        } catch (Throwable t) {
            Log.w(TAG, "preload('" + lang + "') failed, will retry lazily on first use", t);
        }
    }

    private OfflineTts ensureEngine(String lang) {
        synchronized (initLock) {
            OfflineTts cached = engines.get(lang);
            if (cached != null) {
                return cached;
            }
            long t0 = System.nanoTime();
            File voiceDir = ensureVoiceDir(lang);
            File data = ensureDataDir();

            OfflineTtsVitsModelConfig vits = new OfflineTtsVitsModelConfig();
            vits.setModel(new File(voiceDir, "model.onnx").getAbsolutePath());
            vits.setTokens(new File(voiceDir, "tokens.txt").getAbsolutePath());
            vits.setDataDir(data.getAbsolutePath());

            OfflineTtsModelConfig modelConfig = new OfflineTtsModelConfig();
            modelConfig.setVits(vits);
            modelConfig.setNumThreads(2);
            modelConfig.setProvider("cpu");
            modelConfig.setDebug(false);

            OfflineTtsConfig config = new OfflineTtsConfig();
            config.setModel(modelConfig);

            OfflineTts tts = new OfflineTts(null, config);
            engines.put(lang, tts);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            Log.i(TAG, "engine '" + lang + "' ready in " + ms + "ms, sampleRate=" + tts.sampleRate());
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

    /** Resultat brut d'une synthese : echantillons float [-1, 1] + leur frequence. */
    public static class SynthAudio {
        public final float[] samples;
        public final int sampleRate;

        public SynthAudio(float[] samples, int sampleRate) {
            this.samples = samples;
            this.sampleRate = sampleRate;
        }
    }

    /**
     * Bloquant : charge le moteur si besoin et synthetise, sans jouer le
     * resultat -- utilise par {@link #speakAndWait} (AudioTrack) et par
     * {@link SherpaTextToSpeechService} (SynthesisCallback), qui ont chacun
     * leur propre sortie.
     */
    public SynthAudio synthesize(String lang, String text) {
        OfflineTts tts = ensureEngine(lang);
        // generateWithCallback() attend une interface Kotlin specialisee
        // (invoke([F)Ljava/lang/Integer;) qu'un lambda/reference de methode Java
        // ne peut pas satisfaire (NoSuchMethodError a l'appel JNI, verifie) --
        // seul un ::ref Kotlin genere le bridge JNI attendu (confirme dans le
        // TtsService.kt amont de SherpaTTS, qui a le meme commentaire). Piper/VITS
        // n'est de toute facon pas autoregressif comme l'etait Pocket TTS --
        // generate() (un seul passage, rapide) suffit.
        GeneratedAudio audio = tts.generate(text, 0, 1.0f);
        return new SynthAudio(audio.getSamples(), tts.sampleRate());
    }

    /** Bloquant : charge le moteur si besoin, synthetise, joue, attend la fin. */
    public void speakAndWait(String text, String lang) {
        cancelled = false;
        SynthAudio audio;
        try {
            audio = synthesize(lang, text);
        } catch (Throwable t) {
            Log.e(TAG, "synthesize('" + lang + "') failed", t);
            return;
        }
        AudioTrack track = ensureAudioTrack(audio.sampleRate);
        track.play();
        if (!cancelled && audio.samples.length > 0) {
            track.write(audio.samples, 0, audio.samples.length, AudioTrack.WRITE_BLOCKING);
        }
    }

    /** Barge-in : coupe la synthese/lecture en cours au prochain chunk. */
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
        synchronized (initLock) {
            for (OfflineTts tts : engines.values()) {
                tts.release();
            }
            engines.clear();
        }
    }
}
