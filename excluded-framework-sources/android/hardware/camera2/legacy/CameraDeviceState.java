package android.hardware.camera2.legacy;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.os.Handler;
import android.util.Log;
import com.android.internal.telephony.IccCardConstants;

/* JADX INFO: loaded from: classes.dex */
public class CameraDeviceState {
    private static final boolean DEBUG = false;
    public static final int NO_CAPTURE_ERROR = -1;
    private static final int STATE_CAPTURING = 4;
    private static final int STATE_CONFIGURING = 2;
    private static final int STATE_ERROR = 0;
    private static final int STATE_IDLE = 3;
    private static final int STATE_UNCONFIGURED = 1;
    private static final String TAG = "CameraDeviceState";
    private static final String[] sStateNames = {"ERROR", "UNCONFIGURED", "CONFIGURING", "IDLE", "CAPTURING"};
    private int mCurrentState = 1;
    private int mCurrentError = -1;
    private RequestHolder mCurrentRequest = null;
    private Handler mCurrentHandler = null;
    private CameraDeviceStateListener mCurrentListener = null;

    public interface CameraDeviceStateListener {
        void onBusy();

        void onCaptureResult(CameraMetadataNative cameraMetadataNative, RequestHolder requestHolder);

        void onCaptureStarted(RequestHolder requestHolder, long j);

        void onConfiguring();

        void onError(int i, Object obj, RequestHolder requestHolder);

        void onIdle();

        void onRepeatingRequestError(long j, int i);

        void onRequestQueueEmpty();
    }

    public synchronized void setError(int i) {
        this.mCurrentError = i;
        doStateTransition(0);
    }

    public synchronized boolean setConfiguring() {
        doStateTransition(2);
        return this.mCurrentError == -1;
    }

    public synchronized boolean setIdle() {
        doStateTransition(3);
        return this.mCurrentError == -1;
    }

    public synchronized boolean setCaptureStart(RequestHolder requestHolder, long j, int i) {
        this.mCurrentRequest = requestHolder;
        doStateTransition(4, j, i);
        return this.mCurrentError == -1;
    }

    public synchronized boolean setCaptureResult(final RequestHolder requestHolder, final CameraMetadataNative cameraMetadataNative, final int i, final Object obj) {
        if (this.mCurrentState != 4) {
            Log.e(TAG, "Cannot receive result while in state: " + this.mCurrentState);
            this.mCurrentError = 1;
            doStateTransition(0);
            return this.mCurrentError == -1;
        }
        if (this.mCurrentHandler != null && this.mCurrentListener != null) {
            if (i != -1) {
                this.mCurrentHandler.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceState.this.mCurrentListener.onError(i, obj, requestHolder);
                    }
                });
            } else {
                this.mCurrentHandler.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceState.this.mCurrentListener.onCaptureResult(cameraMetadataNative, requestHolder);
                    }
                });
            }
        }
        return this.mCurrentError == -1;
    }

    public synchronized boolean setCaptureResult(RequestHolder requestHolder, CameraMetadataNative cameraMetadataNative) {
        return setCaptureResult(requestHolder, cameraMetadataNative, -1, null);
    }

    public synchronized void setRepeatingRequestError(final long j, final int i) {
        this.mCurrentHandler.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.3
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceState.this.mCurrentListener.onRepeatingRequestError(j, i);
            }
        });
    }

    public synchronized void setRequestQueueEmpty() {
        this.mCurrentHandler.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.4
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceState.this.mCurrentListener.onRequestQueueEmpty();
            }
        });
    }

    public synchronized void setCameraDeviceCallbacks(Handler handler, CameraDeviceStateListener cameraDeviceStateListener) {
        this.mCurrentHandler = handler;
        this.mCurrentListener = cameraDeviceStateListener;
    }

    private void doStateTransition(int i) {
        doStateTransition(i, 0L, -1);
    }

    /* JADX WARN: Code duplicated, block: B:8:0x0010  */
    private void doStateTransition(int i, final long j, final int i2) {
        Handler handler;
        Handler handler2;
        Handler handler3;
        Handler handler4;
        String str;
        if (i != this.mCurrentState) {
            if (i >= 0) {
                String[] strArr = sStateNames;
                if (i < strArr.length) {
                    str = strArr[i];
                } else {
                    str = IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
                }
            } else {
                str = IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
            }
            Log.i(TAG, "Legacy camera service transitioning to state " + str);
        }
        if (i != 0 && i != 3 && this.mCurrentState != i && (handler4 = this.mCurrentHandler) != null && this.mCurrentListener != null) {
            handler4.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.5
                @Override // java.lang.Runnable
                public void run() {
                    CameraDeviceState.this.mCurrentListener.onBusy();
                }
            });
        }
        if (i == 0) {
            if (this.mCurrentState != 0 && (handler = this.mCurrentHandler) != null && this.mCurrentListener != null) {
                handler.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.6
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceState.this.mCurrentListener.onError(CameraDeviceState.this.mCurrentError, null, CameraDeviceState.this.mCurrentRequest);
                    }
                });
            }
            this.mCurrentState = 0;
            return;
        }
        if (i == 2) {
            int i3 = this.mCurrentState;
            if (i3 != 1 && i3 != 3) {
                Log.e(TAG, "Cannot call configure while in state: " + this.mCurrentState);
                this.mCurrentError = 1;
                doStateTransition(0);
                return;
            }
            if (this.mCurrentState != 2 && (handler2 = this.mCurrentHandler) != null && this.mCurrentListener != null) {
                handler2.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.7
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceState.this.mCurrentListener.onConfiguring();
                    }
                });
            }
            this.mCurrentState = 2;
            return;
        }
        if (i == 3) {
            int i4 = this.mCurrentState;
            if (i4 == 3) {
                return;
            }
            if (i4 != 2 && i4 != 4) {
                Log.e(TAG, "Cannot call idle while in state: " + this.mCurrentState);
                this.mCurrentError = 1;
                doStateTransition(0);
                return;
            }
            if (this.mCurrentState != 3 && (handler3 = this.mCurrentHandler) != null && this.mCurrentListener != null) {
                handler3.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.8
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceState.this.mCurrentListener.onIdle();
                    }
                });
            }
            this.mCurrentState = 3;
            return;
        }
        if (i == 4) {
            int i5 = this.mCurrentState;
            if (i5 != 3 && i5 != 4) {
                Log.e(TAG, "Cannot call capture while in state: " + this.mCurrentState);
                this.mCurrentError = 1;
                doStateTransition(0);
                return;
            }
            Handler handler5 = this.mCurrentHandler;
            if (handler5 != null && this.mCurrentListener != null) {
                if (i2 != -1) {
                    handler5.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.9
                        @Override // java.lang.Runnable
                        public void run() {
                            CameraDeviceState.this.mCurrentListener.onError(i2, null, CameraDeviceState.this.mCurrentRequest);
                        }
                    });
                } else {
                    handler5.post(new Runnable() { // from class: android.hardware.camera2.legacy.CameraDeviceState.10
                        @Override // java.lang.Runnable
                        public void run() {
                            CameraDeviceState.this.mCurrentListener.onCaptureStarted(CameraDeviceState.this.mCurrentRequest, j);
                        }
                    });
                }
            }
            this.mCurrentState = 4;
            return;
        }
        throw new IllegalStateException("Transition to unknown state: " + i);
    }
}
