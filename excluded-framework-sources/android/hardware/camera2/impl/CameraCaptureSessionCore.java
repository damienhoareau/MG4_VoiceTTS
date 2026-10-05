package android.hardware.camera2.impl;

/* JADX INFO: loaded from: classes.dex */
public interface CameraCaptureSessionCore {
    CameraDeviceImpl.StateCallbackKK getDeviceStateCallback();

    boolean isAborting();

    void replaceSessionClose();
}
