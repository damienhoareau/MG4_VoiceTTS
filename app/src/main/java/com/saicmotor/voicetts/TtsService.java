package com.saicmotor.voicetts;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothHidDevice;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.saicmotor.speech.loader.EngineType;
import com.saicmotor.speech.loader.LanguagePropsType;
import com.saicmotor.voicetts.log.Logger;
import com.saicmotor.voicetts.sherpa.SherpaVoiceEngine;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes3.dex */
public class TtsService extends Service {
    private static final String AROUND_PACKAGE = "com.saicmotor.hmi.aroundview";
    public static final Logger LOG = new Logger(TtsService.class);
    private static final int MAX_QUEUE_SIZE = 3;
    private static final String NAV_MAP_PACKAGE = "com.saicmotor.navigation";
    private static final String NNG_MAP_PACKAGE = "com.nng.igo.primong";
    private static final String TELENAV_MAP_PACKAGE = "com.telenav.app.arp";
    private static final String TTS_GREETING_SOURCE_ID = "";
    private static final String TTS_SECURITY_SOURCE_ID = "";
    private AudioFocusRequest mAudioFocusRequest;
    private AudioManager mAudioManager;
    private String mCurLang;
    private String mCurLangForEngine;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    // Moteur de synthese : sherpa-onnx + voix Piper, embarque directement
    // (voir docs/TTS_ENGINE.md pour l'historique -- Pocket TTS puis l'API
    // Android TextToSpeech routee vers l'app SherpaTTS separee ont ete
    // essayes et abandonnes avant celui-ci).
    SherpaVoiceEngine sherpaVoiceEngine;
    PlatformConfig platformConfig;
    private volatile Queue<PromptData> promptQueue;
    private final String TAG = TtsService.class.getSimpleName();
    private volatile Boolean isSpeaking = false;
    private int TIMEOUT_TTS_WAIT = 10000;
    private int ttsSpeed = 100;
    private int ttsPitch = 100;
    private volatile TTSStatus ttsStatus = TTSStatus.None;
    private PromptData currentPromptData = null;
    private volatile boolean hasRequestAudioFocus = false;
    private String engineType = EngineType.MG_VOICE;
    private ContentObserver contentObserver = new ContentObserver(null) { // from class: com.saicmotor.voicetts.TtsService.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            // L'ancien moteur iFlytek necessitait un re-init ici car son activation
            // dependait du VIN/SN. Pocket TTS n'a pas de licence liee au vehicule :
            // rien a refaire.
            TtsService.LOG.i("contentObserver onChange: vrVinNumber/vrSNNumber changed, no-op for Pocket TTS.");
        }
    };
    private final ITtsService.Stub mBinder = new ITtsService.Stub() { // from class: com.saicmotor.voicetts.TtsService.2
        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWords(String str, boolean z, String str2) throws RemoteException {
            TtsService.this.speakTtsContent(str, -1, z, str2, null);
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsWithStatus(String str, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
            TtsService.this.speakTtsContent(str, -1, z, str2, iPromptCallBack);
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void stopPrompt() {
            if (TtsService.this.currentPromptData == null || !TtsService.this.currentPromptData.getSpeakText().contains("obey traffic laws")) {
                TtsService.this.privStopPrompt();
            } else {
                TtsService.LOG.e("dont stop prompt for [obey traffic laws]");
            }
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsByLang(String str, int i, boolean z, String str2) throws RemoteException {
            TtsService.LOG.i("promptCommonWordsByLang content:" + str + ",lang" + i + ",shouldAudioFocus" + z);
            TtsService.this.speakTtsContent(str, i, z, str2, null);
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsByLangWithStatus(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
            TtsService.LOG.i("promptCommonWordsByLangWithStatus content:" + str + ",lang" + i + ",shouldAudioFocus" + z + ",iPromptCallBack" + iPromptCallBack);
            TtsService.this.speakTtsContent(str, i, z, str2, iPromptCallBack);
        }
    };

    private enum TTSStatus {
        None,
        Start_Prompt,
        Break_Prompt,
        End_Prompt
    }

    public TtsService() {
        Log.i(this.TAG, "====SaicService====");
    }

    private boolean isFromMap(String str) {
        boolean z = false;
        if (!TextUtils.isEmpty(str) && (TELENAV_MAP_PACKAGE.equals(str) || NNG_MAP_PACKAGE.equals(str) || NAV_MAP_PACKAGE.equals(str) || str.toLowerCase().contains("mmi") || str.toLowerCase().contains("nng"))) {
            z = true;
        }
        Log.i(this.TAG, "isFromMap sourceId=" + str + ",result=" + z);
        return z;
    }

    private boolean requestFocusSuccessFul(String str) {
        Bundle bundle = new Bundle();
        int i = 2;
        int i2 = 12;
        if (isFromMap(str)) {
            i = 3;
        } else if (CommonUtils.isEC32Series()) {
            i2 = 11;
        } else {
            // AudioAttributes.KEY_CAR_SOURCE_TYPE est une API cachee (@hide) : lue par
            // reflexion pour ne pas deviner sa valeur reelle et rester fonctionnelle a
            // l'execution si le champ existe vraiment sur cette build.
            try {
                String keyCarSourceType = (String) AudioAttributes.class.getField("KEY_CAR_SOURCE_TYPE").get(null);
                bundle.putInt(keyCarSourceType, 30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(this.TAG, "requestFocus Usage: " + i2 + ",focusGain: " + i);
        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder().setUsage(i2);
        try {
            // AudioAttributes.Builder.addBundle(Bundle) est aussi une API cachee.
            audioAttributesBuilder.getClass().getMethod("addBundle", Bundle.class).invoke(audioAttributesBuilder, bundle);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        AudioFocusRequest audioFocusRequestBuild = new AudioFocusRequest.Builder(i).setAudioAttributes(audioAttributesBuilder.build()).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener(this.mOnAudioFocusChangeListener).build();
        this.mAudioFocusRequest = audioFocusRequestBuild;
        int iRequestAudioFocus = this.mAudioManager.requestAudioFocus(audioFocusRequestBuild);
        Log.d(this.TAG, ">>>> requestFocusSuccessFul: " + iRequestAudioFocus);
        return iRequestAudioFocus == 1;
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0043  */
    private void setCurLang(String str) {
        byte b;
        int iHashCode = str.hashCode();
        if (iHashCode != 3700) {
            if (iHashCode != 96599000) {
                if (iHashCode != 96599167) {
                    if (iHashCode == 96599241 && str.equals(LanguagePropsType.ENGLISH_IND)) {
                        b = 1;
                    } else {
                        b = -1;
                    }
                } else if (str.equals(LanguagePropsType.ENGLISH_GBR)) {
                    b = 2;
                } else {
                    b = -1;
                }
            } else if (str.equals(LanguagePropsType.ENGLISH_AUS)) {
                b = 0;
            } else {
                b = -1;
            }
        } else if (str.equals(LanguagePropsType.THAI)) {
            b = 3;
        } else {
            b = -1;
        }
        if (b == 0) {
            this.mCurLang = SysLang.EXT_eng_AUS;
            return;
        }
        if (b == 1) {
            this.mCurLang = SysLang.EXT_eng_IND;
            return;
        }
        if (b == 2) {
            this.mCurLang = SysLang.EXT_eng_GBR;
        } else if (b == 3) {
            this.mCurLang = SysLang.EXT_tha_THA;
        } else {
            this.mCurLang = SysLang.EXT_eng_IND;
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i(this.TAG, "onStartCommand, SaicService onStartCommand");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("saic", "SaicService", 3);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            startForeground(1, new Notification.Builder(this).setChannelId("saic").setSmallIcon(17301509).build());
        }
        return 1;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.i(this.TAG, "====onBind==== 绑定成功");
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d(this.TAG, "====onDestroy====");
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            AudioFocusRequest audioFocusRequest = this.mAudioFocusRequest;
            if (audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            }
            AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = this.mOnAudioFocusChangeListener;
            if (onAudioFocusChangeListener != null) {
                this.mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
            }
        }
        SherpaVoiceEngine sherpaVoiceEngine = this.sherpaVoiceEngine;
        if (sherpaVoiceEngine != null) {
            sherpaVoiceEngine.release();
        }
        if (this.contentObserver != null) {
            getContentResolver().unregisterContentObserver(this.contentObserver);
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void speakTtsContent(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) {
        final PromptData promptData;
        if (i == 0 || i == -1) {
            promptData = new PromptData(str, z, str2, iPromptCallBack);
        } else {
            promptData = new PromptData(str, i, z, str2, iPromptCallBack);
        }
        if (this.promptQueue.size() <= 3) {
            Log.v(this.TAG, "speakTtsContent add content [ " + str + " ] to queue. sourceId:" + str2 + ", shouldAudioFocus:" + z);
            this.promptQueue.add(promptData);
        }
        new Thread(new Runnable() { // from class: com.saicmotor.voicetts.TtsService.3
            @Override // java.lang.Runnable
            public void run() {
                TtsService.this.doSpeakInThread(promptData);
                TtsService.this.hasRequestAudioFocus = false;
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSpeakInThread(PromptData promptData) {
        synchronized (this.isSpeaking) {
            if (this.isSpeaking.booleanValue()) {
                return;
            }
            this.isSpeaking = true;
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (promptData.isShouldAudioFocus() && !this.hasRequestAudioFocus) {
                Log.d(this.TAG, "doSpeakInThread before, requestAudioFocusRequest");
                if (!requestFocusSuccessFul(promptData.getSourceId())) {
                    if (!this.promptQueue.isEmpty()) {
                        this.promptQueue.clear();
                    }
                    this.isSpeaking = false;
                    if (promptData.getiPromptCallBack() != null) {
                        try {
                            Log.v(this.TAG, "iPromptCallBack.onError()");
                            promptData.getiPromptCallBack().onError();
                            return;
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    }
                    return;
                }
                this.hasRequestAudioFocus = true;
            }
            this.currentPromptData = promptData;
            Log.i(this.TAG, "content====" + promptData.getSpeakText());
            this.isSpeaking = true;
            Log.v(this.TAG, "start prompt...");
            if (promptData.getiPromptCallBack() != null) {
                try {
                    Log.v(this.TAG, "iPromptCallBack.onSuccess()");
                    promptData.getiPromptCallBack().onSuccess();
                } catch (RemoteException e3) {
                    e3.printStackTrace();
                }
            }
            if (isFromMap(promptData.getSourceId())) {
                this.mCurLangForEngine = getCurLangForMap(promptData.getSourceId());
            } else {
                this.mCurLangForEngine = getCurLangForTTS(promptData.getSourceId());
            }
            LOG.i("mCurLangForEngine:" + this.mCurLangForEngine);
            this.ttsStatus = TTSStatus.Start_Prompt;
            this.sherpaVoiceEngine.speakAndWait(
                    promptData.getSpeakText(), SherpaVoiceEngine.langFor(this.mCurLangForEngine));
            this.ttsStatus = TTSStatus.None;
            Log.v(this.TAG, "end prompt...");
            if (promptData.getiPromptCallBack() != null) {
                try {
                    Log.v(this.TAG, "iPromptCallBack.onSpeakCompleted()");
                    promptData.getiPromptCallBack().onSpeakCompleted();
                } catch (RemoteException e5) {
                    e5.printStackTrace();
                }
            }
            if (!this.promptQueue.isEmpty()) {
                this.promptQueue.remove();
            }
            this.isSpeaking = false;
            this.ttsStatus = TTSStatus.None;
            if (promptData.isShouldAudioFocus()) {
                Log.d(this.TAG, "doSpeakInThread finish, >>>>abandonAudioFocusRequest");
                this.mAudioManager.abandonAudioFocusRequest(this.mAudioFocusRequest);
            }
            this.currentPromptData = null;
            while (!this.promptQueue.isEmpty()) {
                Log.v(this.TAG, "str queue size:====" + this.promptQueue.size());
                PromptData promptDataPeek = this.promptQueue.peek();
                if (promptDataPeek != null) {
                    Log.v(this.TAG, "peekStr:====" + promptDataPeek.getSpeakText());
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e6) {
                        e6.printStackTrace();
                    }
                    doSpeakInThread(promptDataPeek);
                }
            }
        }
    }

    private String getCurLang() {
        return Settings.System.getString(getApplicationContext().getContentResolver(), SaicmotorVoice.SAICMOTOR_VOICE_CURRENT_LANGUAGE);
    }

    private String getCurLangForMap(String str) {
        if (NAV_MAP_PACKAGE.equals(str) || CommonUtils.isUseNaviLangBySystemLocale(getApplicationContext())) {
            return getNaviLangFromSystemLocalLangFor14Lang();
        }
        if (this.platformConfig.getProjectName().endsWith("TT") || CommonUtils.isTTSeries()) {
            return getApplicationContext().getResources().getConfiguration().locale.getLanguage().contains(LanguagePropsType.THAI) ? SysLang.EXT_tha_THA : SysLang.EXT_eng_GBR;
        }
        String curLang = getCurLang();
        Log.d(this.TAG, "doSpeakInThread: CurLang: " + curLang);
        if (TextUtils.isEmpty(curLang)) {
            curLang = SaicmotorVoice.VOICE_LANGUAGE_ENG_UK;
        }
        switch (curLang) {
            case "ENG-UK":
                return SysLang.NAVI_eng_GBR;
            case "ENG-IN":
                return SysLang.NAVI_eng_IND;
            case "ENG-AUS":
                return SysLang.NAVI_eng_AUS;
            case "GER-EU":
                return SysLang.NAVI_deu_DEU;
            case "FRE-EU":
                return SysLang.NAVI_fre_FRA;
            case "SPN-EU":
            case "SPN-SA":
                return SysLang.NAVI_spa_ESP;
            case "Thai-Thai":
                return SysLang.NAVI_tha_THA;
            default:
                return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:29:0x007c  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private String getNaviLangFromSystemLocalLangFor4Lang() {
        byte b;
        String language = getResources().getConfiguration().locale.getLanguage();
        LOG.i("system lang:" + language);
        switch (language.hashCode()) {
            case 3201:
                if (!language.equals("de")) {
                    b = -1;
                } else {
                    b = 5;
                }
                break;
            case 3241:
                if (!language.equals("en")) {
                    b = -1;
                } else {
                    b = 0;
                }
                break;
            case 3246:
                if (!language.equals("es")) {
                    b = -1;
                } else {
                    b = 7;
                }
                break;
            case 3276:
                if (!language.equals("fr")) {
                    b = -1;
                } else {
                    b = 6;
                }
                break;
            case 96599042:
                if (!language.equals("en-ca")) {
                    b = -1;
                } else {
                    b = 3;
                }
                break;
            case 96599167:
                if (!language.equals(LanguagePropsType.ENGLISH_GBR)) {
                    b = -1;
                } else {
                    b = 2;
                }
                break;
            case 96599232:
                if (!language.equals("en-ie")) {
                    b = -1;
                } else {
                    b = 4;
                }
                break;
            case 96599618:
                if (!language.equals("en-us")) {
                    b = -1;
                } else {
                    b = 1;
                }
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return SysLang.NAVI_eng_GBR;
            case 5:
                return SysLang.NAVI_deu_DEU;
            case 6:
                return SysLang.NAVI_fre_FRA;
            case 7:
                return SysLang.NAVI_spa_ESP;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:65:0x010b  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private String getNaviLangFromSystemLocalLangFor14Lang() {
        byte b;
        String language = getResources().getConfiguration().locale.getLanguage();
        LOG.i("system lang:" + language);
        switch (language.hashCode()) {
            case 3197:
                if (!language.equals("da")) {
                    b = -1;
                } else {
                    b = 9;
                }
                break;
            case 3201:
                if (!language.equals("de")) {
                    b = -1;
                } else {
                    b = 5;
                }
                break;
            case 3239:
                if (!language.equals("el")) {
                    b = -1;
                } else {
                    b = 15;  // MidiConstants.STATUS_CHANNEL_MASK (API cachee) = 0x0F = 15, valeur de case attendue ici
                }
                break;
            case 3241:
                if (!language.equals("en")) {
                    b = -1;
                } else {
                    b = 0;
                }
                break;
            case 3246:
                if (!language.equals("es")) {
                    b = -1;
                } else {
                    b = 7;
                }
                break;
            case 3267:
                if (!language.equals("fi")) {
                    b = -1;
                } else {
                    b = 16;  // WifiScanner...FLAG_SAME_NETWORK (API cachee) -- valeur de case attendue ici (cf. switch(b) -> NAVI_fin_FIN)
                }
                break;
            case 3276:
                if (!language.equals("fr")) {
                    b = -1;
                } else {
                    b = 6;
                }
                break;
            case 3371:
                if (!language.equals("it")) {
                    b = -1;
                } else {
                    b = 13;
                }
                break;
            case 3508:
                if (!language.equals("nb")) {  // FullBackup.NO_BACKUP_TREE_TOKEN (API cachee) a pour hashCode 3508 == "nb".hashCode() (norvegien bokmal)
                    b = -1;
                } else {
                    b = 10;
                }
                break;
            case 3518:
                if (!language.equals("nl")) {
                    b = -1;
                } else {
                    b = 8;
                }
                break;
            case 3521:
                if (!language.equals("no")) {
                    b = -1;
                } else {
                    b = 11;
                }
                break;
            case 3580:
                if (!language.equals("pl")) {
                    b = -1;
                } else {
                    b = 18;
                }
                break;
            case 3588:
                if (!language.equals("pt")) {
                    b = -1;
                } else {
                    b = BluetoothHidDevice.ERROR_RSP_UNKNOWN;
                }
                break;
            case 3683:
                if (!language.equals("sv")) {
                    b = -1;
                } else {
                    b = 12;
                }
                break;
            case 3700:
                if (!language.equals(LanguagePropsType.THAI)) {
                    b = -1;
                } else {
                    b = 19;
                }
                break;
            case 3710:
                if (!language.equals("tr")) {
                    b = -1;
                } else {
                    b = 17;
                }
                break;
            case 96599042:
                if (!language.equals("en-ca")) {
                    b = -1;
                } else {
                    b = 3;
                }
                break;
            case 96599167:
                if (!language.equals(LanguagePropsType.ENGLISH_GBR)) {
                    b = -1;
                } else {
                    b = 2;
                }
                break;
            case 96599232:
                if (!language.equals("en-ie")) {
                    b = -1;
                } else {
                    b = 4;
                }
                break;
            case 96599618:
                if (!language.equals("en-us")) {
                    b = -1;
                } else {
                    b = 1;
                }
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            default:
                return SysLang.NAVI_eng_GBR;
            case 5:
                return SysLang.NAVI_deu_DEU;
            case 6:
                return SysLang.NAVI_fre_FRA;
            case 7:
                return SysLang.NAVI_spa_ESP;
            case 8:
                return SysLang.NAVI_dut_NLD;
            case 9:
                return SysLang.NAVI_dan_DNK;
            case 10:
            case 11:
                return SysLang.NAVI_nor_NOR;
            case 12:
                return SysLang.NAVI_swe_SWE;
            case 13:
                return SysLang.NAVI_ita_ITA;
            case 14:
                return SysLang.NAVI_por_PRT;
            case 15:
                return SysLang.NAVI_gre_GRC;
            case 16:
                return SysLang.NAVI_fin_FIN;
            case 17:
                return SysLang.NAVI_tur_TUR;
            case 18:
                return SysLang.NAVI_pol_POL;
            case 19:
                return SysLang.NAVI_tha_THA;
        }
    }

    private String getCurLangForTTS(String str) {
        String curLang = getCurLang();
        Log.d(this.TAG, "doSpeakInThread: CurLang: " + curLang);
        if (TextUtils.isEmpty(curLang)) {
            curLang = SaicmotorVoice.VOICE_LANGUAGE_ENG_UK;
        }
        switch (curLang) {
            case "ENG-UK":
                return SysLang.EXT_eng_GBR;
            case "ENG-IN":
                return SysLang.EXT_eng_IND;
            case "ENG-AUS":
                return SysLang.EXT_eng_AUS;
            case "GER-EU":
                return SysLang.EXT_deu_DEU;
            case "FRE-EU":
                return SysLang.EXT_fre_FRA;
            case "Thai-Thai":
                return SysLang.EXT_tha_THA;
            case "SPN-EU":
            case "SPN-SA":
                return SysLang.EXT_spa_ESP;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void privStopPrompt() {
        Log.d(this.TAG, "====stopPrompt====");
        if (!this.promptQueue.isEmpty()) {
            this.promptQueue.clear();
        }
        SherpaVoiceEngine sherpaVoiceEngine = this.sherpaVoiceEngine;
        if (sherpaVoiceEngine != null) {
            sherpaVoiceEngine.stop();
        }
    }

    private class PromptData {
        private IPromptCallBack iPromptCallBack;
        private int lang;
        private boolean shouldAudioFocus;
        private String sourceId;
        private String speakText;

        public PromptData() {
            this.lang = 0;
        }

        public PromptData(String str, boolean z, String str2) {
            this.lang = 0;
            this.speakText = str;
            this.shouldAudioFocus = z;
            this.sourceId = str2;
        }

        public PromptData(String str, boolean z, String str2, IPromptCallBack iPromptCallBack) {
            this.lang = 0;
            this.speakText = str;
            this.shouldAudioFocus = z;
            this.sourceId = str2;
            this.iPromptCallBack = iPromptCallBack;
        }

        public PromptData(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) {
            this.lang = 0;
            this.speakText = str;
            this.shouldAudioFocus = z;
            this.sourceId = str2;
            this.lang = i;
            this.iPromptCallBack = iPromptCallBack;
        }

        public String getSpeakText() {
            return this.speakText;
        }

        public void setSpeakText(String str) {
            this.speakText = str;
        }

        public boolean isShouldAudioFocus() {
            return this.shouldAudioFocus;
        }

        public void setShouldAudioFocus(boolean z) {
            this.shouldAudioFocus = z;
        }

        public int getLang() {
            return this.lang;
        }

        public void setLang(int i) {
            this.lang = i;
        }

        public String getSourceId() {
            return this.sourceId;
        }

        public void setSourceId(String str) {
            this.sourceId = str;
        }

        public IPromptCallBack getiPromptCallBack() {
            return this.iPromptCallBack;
        }

        public void setiPromptCallBack(IPromptCallBack iPromptCallBack) {
            this.iPromptCallBack = iPromptCallBack;
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(this.TAG, "====onCreate====");
        getContentResolver().registerContentObserver(Settings.System.getUriFor("vrVinNumber"), true, this.contentObserver);
        getContentResolver().registerContentObserver(Settings.System.getUriFor("vrSNNumber"), true, this.contentObserver);
        PlatformConfig platformConfigLoadTTSLangConfig = TTSConfigLoadHelper.loadTTSLangConfig(this);
        this.platformConfig = platformConfigLoadTTSLangConfig;
        setCurLang(platformConfigLoadTTSLangConfig.getLang());
        // Tous les moteurs (iFlytek/MG_VOICE, DragonDrive/NUANCE_FULL, NUANCE_HALF)
        // sont remplaces par sherpa-onnx + voix Piper (offline, voir
        // docs/TTS_ENGINE.md), quel que soit ce que platformConfig.getEngine()
        // renvoie pour ce vehicule/marche.
        this.engineType = EngineType.MG_VOICE;
        Log.d(this.TAG, "mCurLang config:" + this.mCurLang);
        Log.d(this.TAG, "engineType config:" + this.engineType + " (-> sherpa-onnx)");
        this.sherpaVoiceEngine = new SherpaVoiceEngine(getApplicationContext());
        // Precharge la langue couramment reglee en tache de fond : le chargement
        // d'un modele prend plusieurs secondes (voir docs/TTS_ENGINE.md), sans ca
        // le tout premier prompt apres demarrage du vehicule attendrait ce delai.
        // getCurLangForTTS(String) ignore son argument (lit Settings.System),
        // utilisable ici sans PromptData reel. N'empeche jamais le service de
        // demarrer : simple optimisation, l'engine se chargerait de toute facon
        // a la demande si ce thread n'a pas fini a temps.
        final SherpaVoiceEngine engineToPreload = this.sherpaVoiceEngine;
        new Thread(() -> {
            String sysLang = getCurLangForTTS(null);
            String lang = SherpaVoiceEngine.langFor(sysLang);
            Log.i(this.TAG, "preloading TTS engine for '" + lang + "'...");
            engineToPreload.preload(lang);
        }, "tts-preload").start();
        this.promptQueue = new LinkedBlockingQueue();
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.saicmotor.voicetts.TtsService.4
            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public void onAudioFocusChange(int i) {
                Log.i(TtsService.this.TAG, "TTS Service onAudioFocusChange: " + i);
                if (i == -2) {
                    TtsService.this.privStopPrompt();
                    if (TtsService.this.mAudioFocusRequest != null) {
                        TtsService.this.mAudioManager.abandonAudioFocusRequest(TtsService.this.mAudioFocusRequest);
                        return;
                    }
                    return;
                }
                if (i == -3) {
                    TtsService.this.privStopPrompt();
                    if (TtsService.this.mAudioFocusRequest != null) {
                        TtsService.this.mAudioManager.abandonAudioFocusRequest(TtsService.this.mAudioFocusRequest);
                        return;
                    }
                    return;
                }
                if (i == 1 || i == 2) {
                    return;
                }
                if (i == -1 || i == -2) {
                    TtsService.this.privStopPrompt();
                    if (TtsService.this.mAudioFocusRequest != null) {
                        TtsService.this.mAudioManager.abandonAudioFocusRequest(TtsService.this.mAudioFocusRequest);
                    }
                }
            }
        };
    }

}
