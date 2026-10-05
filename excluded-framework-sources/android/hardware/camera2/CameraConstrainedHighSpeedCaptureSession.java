package android.hardware.camera2;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class CameraConstrainedHighSpeedCaptureSession extends CameraCaptureSession {
    public abstract List<CaptureRequest> createHighSpeedRequestList(CaptureRequest captureRequest) throws CameraAccessException;
}
