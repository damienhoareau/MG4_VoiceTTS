package android.hardware.soundtrigger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class SoundTriggerModule {
    private static final int EVENT_RECOGNITION = 1;
    private static final int EVENT_SERVICE_DIED = 2;
    private static final int EVENT_SERVICE_STATE_CHANGE = 4;
    private static final int EVENT_SOUNDMODEL = 3;
    private NativeEventHandlerDelegate mEventHandlerDelegate;
    private int mId;
    private long mNativeContext;

    private native void native_finalize();

    private native void native_setup(Object obj);

    public native void detach();

    public native int loadSoundModel(SoundTrigger.SoundModel soundModel, int[] iArr);

    public native int startRecognition(int i, SoundTrigger.RecognitionConfig recognitionConfig);

    public native int stopRecognition(int i);

    public native int unloadSoundModel(int i);

    SoundTriggerModule(int i, SoundTrigger.StatusListener statusListener, Handler handler) {
        this.mId = i;
        this.mEventHandlerDelegate = new NativeEventHandlerDelegate(statusListener, handler);
        native_setup(new WeakReference(this));
    }

    protected void finalize() {
        native_finalize();
    }

    private class NativeEventHandlerDelegate {
        private final Handler mHandler;

        NativeEventHandlerDelegate(final SoundTrigger.StatusListener statusListener, Handler handler) {
            Looper mainLooper;
            if (handler != null) {
                mainLooper = handler.getLooper();
            } else {
                mainLooper = Looper.getMainLooper();
            }
            if (mainLooper != null) {
                this.mHandler = new Handler(mainLooper) { // from class: android.hardware.soundtrigger.SoundTriggerModule.NativeEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        SoundTrigger.StatusListener statusListener2;
                        int i = message.what;
                        if (i == 1) {
                            SoundTrigger.StatusListener statusListener3 = statusListener;
                            if (statusListener3 != null) {
                                statusListener3.onRecognition((SoundTrigger.RecognitionEvent) message.obj);
                                return;
                            }
                            return;
                        }
                        if (i == 2) {
                            SoundTrigger.StatusListener statusListener4 = statusListener;
                            if (statusListener4 != null) {
                                statusListener4.onServiceDied();
                                return;
                            }
                            return;
                        }
                        if (i != 3) {
                            if (i == 4 && (statusListener2 = statusListener) != null) {
                                statusListener2.onServiceStateChange(message.arg1);
                                return;
                            }
                            return;
                        }
                        SoundTrigger.StatusListener statusListener5 = statusListener;
                        if (statusListener5 != null) {
                            statusListener5.onSoundModelUpdate((SoundTrigger.SoundModelEvent) message.obj);
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler handler() {
            return this.mHandler;
        }
    }

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        NativeEventHandlerDelegate nativeEventHandlerDelegate;
        Handler handler;
        SoundTriggerModule soundTriggerModule = (SoundTriggerModule) ((WeakReference) obj).get();
        if (soundTriggerModule == null || (nativeEventHandlerDelegate = soundTriggerModule.mEventHandlerDelegate) == null || (handler = nativeEventHandlerDelegate.handler()) == null) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(i, i2, i3, obj2));
    }
}
