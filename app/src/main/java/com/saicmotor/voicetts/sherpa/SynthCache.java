package com.saicmotor.voicetts.sherpa;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Cache disque LRU des syntheses deja calculees : une annonce recurrente
 * ("Dans 300 metres, ...") est rejouee sans aucun calcul. Un fichier par
 * enonce, PCM 16 bits little-endian brut, nom = hash(voix + texte).
 *
 * LRU = date de modification du fichier (rafraichie a chaque lecture), purge
 * des plus anciens au-dela de {@code maxBytes}. Ecriture atomique (tmp +
 * rename). Aucune methode ne leve : un cache en echec doit rester invisible.
 */
final class SynthCache {

    private static final String TAG = "SynthCache";

    private final File dir;
    private final long maxBytes;

    SynthCache(File dir, long maxBytes) {
        this.dir = dir;
        this.maxBytes = maxBytes;
    }

    /** voiceKey identifie voix + modele (langue incluse), text l'enonce complet. */
    static String key(String voiceKey, String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(voiceKey.getBytes(StandardCharsets.UTF_8));
            md.update((byte) 0);
            md.update(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /** Echantillons float [-1, 1], ou null si absent / illisible. */
    float[] get(String key) {
        if (key == null) {
            return null;
        }
        File f = new File(dir, key + ".pcm");
        if (!f.isFile()) {
            return null;
        }
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] raw = new byte[(int) f.length()];
            int off = 0;
            while (off < raw.length) {
                int n = in.read(raw, off, raw.length - off);
                if (n < 0) {
                    break;
                }
                off += n;
            }
            if (off != raw.length || raw.length < 2) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
            float[] out = new float[raw.length / 2];
            for (int i = 0; i < out.length; i++) {
                out[i] = bb.getShort() / 32768f;
            }
            //noinspection ResultOfMethodCallIgnored
            f.setLastModified(System.currentTimeMillis());
            return out;
        } catch (Throwable t) {
            Log.w(TAG, "get failed: " + key, t);
            return null;
        }
    }

    void put(String key, float[] samples) {
        if (key == null || samples.length == 0) {
            return;
        }
        synchronized (this) {
            File tmp = new File(dir, key + ".tmp");
            try {
                if (!dir.isDirectory() && !dir.mkdirs()) {
                    return;
                }
                ByteBuffer bb = ByteBuffer.allocate(samples.length * 2).order(ByteOrder.LITTLE_ENDIAN);
                for (float s : samples) {
                    float c = Math.max(-1f, Math.min(1f, s));
                    bb.putShort((short) Math.round(c * 32767f));
                }
                try (FileOutputStream out = new FileOutputStream(tmp)) {
                    out.write(bb.array());
                }
                File dst = new File(dir, key + ".pcm");
                if (!tmp.renameTo(dst)) {
                    //noinspection ResultOfMethodCallIgnored
                    tmp.delete();
                    return;
                }
                trim();
            } catch (IOException | RuntimeException e) {
                Log.w(TAG, "put failed: " + key, e);
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
            }
        }
    }

    private void trim() {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".pcm"));
        if (files == null) {
            return;
        }
        long total = 0;
        for (File f : files) {
            total += f.length();
        }
        if (total <= maxBytes) {
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        for (File f : files) {
            if (total <= maxBytes) {
                break;
            }
            long len = f.length();
            if (f.delete()) {
                total -= len;
            }
        }
    }
}
