package com.saicmotor.tts.tester;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.saicmotor.voicetts.IPromptCallBack;
import com.saicmotor.voicetts.ITtsService;

/**
 * Se lie au service AIDL de l'app systeme installee (com.saicmotor.voicetts)
 * -- exactement comme en voiture : parle dans la langue actuellement reglee
 * dans le menu "Voix" du vehicule, via le pipeline complet (TtsService ->
 * SherpaVoiceEngine, voir docs/TTS_ENGINE.md), pas seulement la synthese.
 */
public class SystemServiceClient {

    private static final String TAG = "TtsTester";
    private static final String TARGET_PACKAGE = "com.saicmotor.voicetts";
    private static final String TARGET_SERVICE = "com.saicmotor.voicetts.TtsService";

    private final Context appContext;
    private volatile ITtsService service;
    private ServiceConnection connection;

    public SystemServiceClient(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public interface ConnectCallback {
        void onResult(boolean connected, String message);
    }

    public interface SpeakStatus {
        void onSuccess();
        void onError();
        void onCompleted();
    }

    public boolean isConnected() {
        return service != null;
    }

    public void connect(ConnectCallback callback) {
        if (service != null) {
            callback.onResult(true, "Déjà connecté à " + TARGET_PACKAGE);
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(TARGET_PACKAGE, TARGET_SERVICE));
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = ITtsService.Stub.asInterface(binder);
                Log.i(TAG, "connected to " + TARGET_SERVICE);
                callback.onResult(true, "Connecté à " + TARGET_PACKAGE);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }
        };
        boolean bound;
        try {
            bound = appContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            callback.onResult(false, "Bind refusé: " + e.getMessage());
            return;
        }
        if (!bound) {
            callback.onResult(
                    false,
                    TARGET_PACKAGE + " introuvable (app système pas installée ?)");
        }
    }

    public boolean speak(String text, SpeakStatus status) {
        ITtsService s = service;
        if (s == null) return false;
        try {
            s.promptCommonWordsWithStatus(text, true, "tts-tester", new IPromptCallBack.Stub() {
                @Override
                public void onSuccess() {
                    status.onSuccess();
                }

                @Override
                public void onError() {
                    status.onError();
                }

                @Override
                public void onSpeakCompleted() {
                    status.onCompleted();
                }
            });
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "promptCommonWordsWithStatus failed", e);
            return false;
        }
    }

    public void stop() {
        ITtsService s = service;
        if (s == null) return;
        try {
            s.stopPrompt();
        } catch (RemoteException e) {
            Log.e(TAG, "stopPrompt failed", e);
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                appContext.unbindService(connection);
            } catch (IllegalArgumentException ignored) {
                // Not bound (connect() never succeeded).
            }
            connection = null;
        }
        service = null;
    }
}
