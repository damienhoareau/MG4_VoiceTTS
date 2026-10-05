package android.hardware.camera2.legacy;

import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.ParamsUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class LegacyFocusStateMapper {
    private static final boolean DEBUG = false;
    private static String TAG = "LegacyFocusStateMapper";
    private final Camera mCamera;
    private int mAfStatePrevious = 0;
    private String mAfModePrevious = null;
    private final Object mLock = new Object();
    private int mAfRun = 0;
    private int mAfState = 0;

    public LegacyFocusStateMapper(Camera camera) {
        this.mCamera = (Camera) Preconditions.checkNotNull(camera, "camera must not be null");
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public void processRequestTriggers(CaptureRequest captureRequest, Camera.Parameters parameters) {
        final int i;
        byte b;
        final int i2;
        Preconditions.checkNotNull(captureRequest, "captureRequest must not be null");
        int i3 = 0;
        int iIntValue = ((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.CONTROL_AF_TRIGGER, 0)).intValue();
        final String focusMode = parameters.getFocusMode();
        if (!Objects.equals(this.mAfModePrevious, focusMode)) {
            synchronized (this.mLock) {
                this.mAfRun++;
                this.mAfState = 0;
            }
            this.mCamera.cancelAutoFocus();
        }
        this.mAfModePrevious = focusMode;
        synchronized (this.mLock) {
            i = this.mAfRun;
        }
        Camera.AutoFocusMoveCallback autoFocusMoveCallback = new Camera.AutoFocusMoveCallback() { // from class: android.hardware.camera2.legacy.LegacyFocusStateMapper.1
            @Override // android.hardware.Camera.AutoFocusMoveCallback
            public void onAutoFocusMoving(boolean z, Camera camera) {
                synchronized (LegacyFocusStateMapper.this.mLock) {
                    if (i != LegacyFocusStateMapper.this.mAfRun) {
                        Log.d(LegacyFocusStateMapper.TAG, "onAutoFocusMoving - ignoring move callbacks from old af run" + i);
                        return;
                    }
                    int i4 = z ? 1 : 2;
                    String str = focusMode;
                    byte b2 = -1;
                    int iHashCode = str.hashCode();
                    if (iHashCode != -194628547) {
                        if (iHashCode == 910005312 && str.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            b2 = 0;
                        }
                    } else if (str.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        b2 = 1;
                    }
                    if (b2 != 0 && b2 != 1) {
                        Log.w(LegacyFocusStateMapper.TAG, "onAutoFocus - got unexpected onAutoFocus in mode " + focusMode);
                    }
                    LegacyFocusStateMapper.this.mAfState = i4;
                }
            }
        };
        byte b2 = -1;
        switch (focusMode) {
            case "continuous-video":
                b = 3;
                break;
            case "auto":
                b = 0;
                break;
            case "macro":
                b = 1;
                break;
            case "continuous-picture":
                b = 2;
                break;
            default:
                b = -1;
                break;
        }
        if (b == 0 || b == 1 || b == 2 || b == 3) {
            this.mCamera.setAutoFocusMoveCallback(autoFocusMoveCallback);
        }
        if (iIntValue != 0) {
            if (iIntValue != 1) {
                if (iIntValue == 2) {
                    synchronized (this.mLock) {
                        synchronized (this.mLock) {
                            this.mAfRun++;
                            this.mAfState = 0;
                        }
                        this.mCamera.cancelAutoFocus();
                    }
                    return;
                }
                Log.w(TAG, "processRequestTriggers - ignoring unknown control.afTrigger = " + iIntValue);
                return;
            }
            switch (focusMode.hashCode()) {
                case -194628547:
                    if (focusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        b2 = 3;
                    }
                    break;
                case 3005871:
                    if (focusMode.equals("auto")) {
                        b2 = 0;
                    }
                    break;
                case 103652300:
                    if (focusMode.equals(Camera.Parameters.FOCUS_MODE_MACRO)) {
                        b2 = 1;
                    }
                    break;
                case 910005312:
                    if (focusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        b2 = 2;
                    }
                    break;
            }
            if (b2 == 0 || b2 == 1) {
                i3 = 3;
            } else if (b2 == 2 || b2 == 3) {
                i3 = 1;
            }
            synchronized (this.mLock) {
                i2 = this.mAfRun + 1;
                this.mAfRun = i2;
                this.mAfState = i3;
            }
            if (i3 == 0) {
                return;
            }
            this.mCamera.autoFocus(new Camera.AutoFocusCallback() { // from class: android.hardware.camera2.legacy.LegacyFocusStateMapper.2
                /* JADX WARN: Code duplicated, block: B:27:0x006d  */
                /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
                @Override // android.hardware.Camera.AutoFocusCallback
                public void onAutoFocus(boolean z, Camera camera) {
                    synchronized (LegacyFocusStateMapper.this.mLock) {
                        int i4 = LegacyFocusStateMapper.this.mAfRun;
                        byte b3 = 0;
                        if (i4 != i2) {
                            Log.d(LegacyFocusStateMapper.TAG, String.format("onAutoFocus - ignoring AF callback (old run %d, new run %d)", Integer.valueOf(i2), Integer.valueOf(i4)));
                            return;
                        }
                        int i5 = z ? 4 : 5;
                        String str = focusMode;
                        switch (str.hashCode()) {
                            case -194628547:
                                if (!str.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                                    b3 = -1;
                                } else {
                                    b3 = 2;
                                }
                                break;
                            case 3005871:
                                if (!str.equals("auto")) {
                                    b3 = -1;
                                }
                                break;
                            case 103652300:
                                if (!str.equals(Camera.Parameters.FOCUS_MODE_MACRO)) {
                                    b3 = -1;
                                } else {
                                    b3 = 3;
                                }
                                break;
                            case 910005312:
                                if (!str.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    b3 = -1;
                                } else {
                                    b3 = 1;
                                }
                                break;
                            default:
                                b3 = -1;
                                break;
                        }
                        if (b3 != 0 && b3 != 1 && b3 != 2 && b3 != 3) {
                            Log.w(LegacyFocusStateMapper.TAG, "onAutoFocus - got unexpected onAutoFocus in mode " + focusMode);
                        }
                        LegacyFocusStateMapper.this.mAfState = i5;
                    }
                }
            });
        }
    }

    public void mapResultTriggers(CameraMetadataNative cameraMetadataNative) {
        int i;
        Preconditions.checkNotNull(cameraMetadataNative, "result must not be null");
        synchronized (this.mLock) {
            i = this.mAfState;
        }
        cameraMetadataNative.set(CaptureResult.CONTROL_AF_STATE, Integer.valueOf(i));
        this.mAfStatePrevious = i;
    }

    private static String afStateToString(int i) {
        switch (i) {
            case 0:
                return "INACTIVE";
            case 1:
                return "PASSIVE_SCAN";
            case 2:
                return "PASSIVE_FOCUSED";
            case 3:
                return "ACTIVE_SCAN";
            case 4:
                return "FOCUSED_LOCKED";
            case 5:
                return "NOT_FOCUSED_LOCKED";
            case 6:
                return "PASSIVE_UNFOCUSED";
            default:
                return "UNKNOWN(" + i + ")";
        }
    }
}
