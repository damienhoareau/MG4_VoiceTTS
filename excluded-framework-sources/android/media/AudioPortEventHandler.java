package android.media;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class AudioPortEventHandler {
    private static final int AUDIOPORT_EVENT_NEW_LISTENER = 4;
    private static final int AUDIOPORT_EVENT_PATCH_LIST_UPDATED = 2;
    private static final int AUDIOPORT_EVENT_PORT_LIST_UPDATED = 1;
    private static final int AUDIOPORT_EVENT_SERVICE_DIED = 3;
    private static final long RESCHEDULE_MESSAGE_DELAY_MS = 100;
    private static final String TAG = "AudioPortEventHandler";
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private long mJniCallback;
    private final ArrayList<AudioManager.OnAudioPortUpdateListener> mListeners = new ArrayList<>();

    private native void native_finalize();

    private native void native_setup(Object obj);

    AudioPortEventHandler() {
    }

    void init() {
        synchronized (this) {
            if (this.mHandler != null) {
                return;
            }
            HandlerThread handlerThread = new HandlerThread(TAG);
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            if (this.mHandlerThread.getLooper() != null) {
                this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: android.media.AudioPortEventHandler.1
                    /* JADX WARN: Code duplicated, block: B:38:0x0091 A[LOOP:2: B:36:0x008b->B:38:0x0091, LOOP_END] */
                    /* JADX WARN: Code duplicated, block: B:55:? A[RETURN, SYNTHETIC] */
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        ArrayList arrayList;
                        AudioPort[] audioPortArr;
                        int i;
                        synchronized (this) {
                            if (message.what != 4) {
                                arrayList = AudioPortEventHandler.this.mListeners;
                            } else {
                                arrayList = new ArrayList();
                                if (AudioPortEventHandler.this.mListeners.contains(message.obj)) {
                                    arrayList.add((AudioManager.OnAudioPortUpdateListener) message.obj);
                                }
                            }
                        }
                        if (message.what == 1 || message.what == 2 || message.what == 3) {
                            AudioManager.resetAudioPortGeneration();
                        }
                        if (arrayList.isEmpty()) {
                            return;
                        }
                        ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        if (message.what != 3 && AudioManager.updateAudioPortCache(arrayList2, arrayList3, null) != 0) {
                            sendMessageDelayed(obtainMessage(message.what, message.obj), AudioPortEventHandler.RESCHEDULE_MESSAGE_DELAY_MS);
                            return;
                        }
                        int i2 = message.what;
                        int i3 = 0;
                        if (i2 == 1) {
                            audioPortArr = (AudioPort[]) arrayList2.toArray(new AudioPort[0]);
                            for (i = 0; i < arrayList.size(); i++) {
                                ((AudioManager.OnAudioPortUpdateListener) arrayList.get(i)).onAudioPortListUpdate(audioPortArr);
                            }
                            if (message.what == 1) {
                                return;
                            }
                        } else if (i2 != 2) {
                            if (i2 != 3) {
                                if (i2 != 4) {
                                    return;
                                }
                                audioPortArr = (AudioPort[]) arrayList2.toArray(new AudioPort[0]);
                                while (i < arrayList.size()) {
                                    ((AudioManager.OnAudioPortUpdateListener) arrayList.get(i)).onAudioPortListUpdate(audioPortArr);
                                }
                                if (message.what == 1) {
                                    return;
                                }
                            } else {
                                while (i3 < arrayList.size()) {
                                    ((AudioManager.OnAudioPortUpdateListener) arrayList.get(i3)).onServiceDied();
                                    i3++;
                                }
                                return;
                            }
                        }
                        AudioPatch[] audioPatchArr = (AudioPatch[]) arrayList3.toArray(new AudioPatch[0]);
                        while (i3 < arrayList.size()) {
                            ((AudioManager.OnAudioPortUpdateListener) arrayList.get(i3)).onAudioPatchListUpdate(audioPatchArr);
                            i3++;
                        }
                    }
                };
                native_setup(new WeakReference(this));
            } else {
                this.mHandler = null;
            }
        }
    }

    protected void finalize() {
        native_finalize();
        if (this.mHandlerThread.isAlive()) {
            this.mHandlerThread.quit();
        }
    }

    void registerListener(AudioManager.OnAudioPortUpdateListener onAudioPortUpdateListener) {
        synchronized (this) {
            this.mListeners.add(onAudioPortUpdateListener);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            this.mHandler.sendMessage(handler.obtainMessage(4, 0, 0, onAudioPortUpdateListener));
        }
    }

    void unregisterListener(AudioManager.OnAudioPortUpdateListener onAudioPortUpdateListener) {
        synchronized (this) {
            this.mListeners.remove(onAudioPortUpdateListener);
        }
    }

    Handler handler() {
        return this.mHandler;
    }

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        Handler handler;
        AudioPortEventHandler audioPortEventHandler = (AudioPortEventHandler) ((WeakReference) obj).get();
        if (audioPortEventHandler == null || audioPortEventHandler == null || (handler = audioPortEventHandler.handler()) == null) {
            return;
        }
        Message messageObtainMessage = handler.obtainMessage(i, i2, i3, obj2);
        if (i != 4) {
            handler.removeMessages(i);
        }
        handler.sendMessage(messageObtainMessage);
    }
}
