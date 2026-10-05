package android.hardware.camera2.legacy;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.utils.ParamsUtils;
import android.util.Log;
import android.util.Size;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class LegacyFaceDetectMapper {
    private static final boolean DEBUG = false;
    private static String TAG = "LegacyFaceDetectMapper";
    private final Camera mCamera;
    private final boolean mFaceDetectSupported;
    private Camera.Face[] mFaces;
    private Camera.Face[] mFacesPrev;
    private boolean mFaceDetectEnabled = false;
    private boolean mFaceDetectScenePriority = false;
    private boolean mFaceDetectReporting = false;
    private final Object mLock = new Object();

    public LegacyFaceDetectMapper(Camera camera, CameraCharacteristics cameraCharacteristics) {
        this.mCamera = (Camera) Preconditions.checkNotNull(camera, "camera must not be null");
        Preconditions.checkNotNull(cameraCharacteristics, "characteristics must not be null");
        boolean zContains = ArrayUtils.contains((int[]) cameraCharacteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES), 1);
        this.mFaceDetectSupported = zContains;
        if (zContains) {
            this.mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() { // from class: android.hardware.camera2.legacy.LegacyFaceDetectMapper.1
                @Override // android.hardware.Camera.FaceDetectionListener
                public void onFaceDetection(Camera.Face[] faceArr, Camera camera2) {
                    int length = faceArr == null ? 0 : faceArr.length;
                    synchronized (LegacyFaceDetectMapper.this.mLock) {
                        if (LegacyFaceDetectMapper.this.mFaceDetectEnabled) {
                            LegacyFaceDetectMapper.this.mFaces = faceArr;
                        } else if (length > 0) {
                            Log.d(LegacyFaceDetectMapper.TAG, "onFaceDetection - Ignored some incoming faces sinceface detection was disabled");
                        }
                    }
                }
            });
        }
    }

    public void processFaceDetectMode(CaptureRequest captureRequest, Camera.Parameters parameters) {
        Preconditions.checkNotNull(captureRequest, "captureRequest must not be null");
        int iIntValue = ((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.STATISTICS_FACE_DETECT_MODE, 0)).intValue();
        if (iIntValue == 0 || this.mFaceDetectSupported) {
            int iIntValue2 = ((Integer) ParamsUtils.getOrDefault(captureRequest, CaptureRequest.CONTROL_SCENE_MODE, 0)).intValue();
            if (iIntValue2 == 1 && !this.mFaceDetectSupported) {
                Log.w(TAG, "processFaceDetectMode - ignoring control.sceneMode == FACE_PRIORITY; face detection is not available");
                return;
            }
            if (iIntValue != 0 && iIntValue != 1) {
                if (iIntValue == 2) {
                    Log.w(TAG, "processFaceDetectMode - statistics.faceDetectMode == FULL unsupported, downgrading to SIMPLE");
                } else {
                    Log.w(TAG, "processFaceDetectMode - ignoring unknown statistics.faceDetectMode = " + iIntValue);
                    return;
                }
            }
            boolean z = iIntValue != 0 || iIntValue2 == 1;
            synchronized (this.mLock) {
                if (z != this.mFaceDetectEnabled) {
                    if (z) {
                        this.mCamera.startFaceDetection();
                    } else {
                        this.mCamera.stopFaceDetection();
                        this.mFaces = null;
                    }
                    this.mFaceDetectEnabled = z;
                    this.mFaceDetectScenePriority = iIntValue2 == 1;
                    this.mFaceDetectReporting = iIntValue != 0;
                }
            }
            return;
        }
        Log.w(TAG, "processFaceDetectMode - Ignoring statistics.faceDetectMode; face detection is not available");
    }

    public void mapResultFaces(CameraMetadataNative cameraMetadataNative, LegacyRequest legacyRequest) {
        int i;
        Camera.Face[] faceArr;
        boolean z;
        Preconditions.checkNotNull(cameraMetadataNative, "result must not be null");
        Preconditions.checkNotNull(legacyRequest, "legacyRequest must not be null");
        synchronized (this.mLock) {
            i = this.mFaceDetectReporting ? 1 : 0;
            faceArr = this.mFaceDetectReporting ? this.mFaces : null;
            z = this.mFaceDetectScenePriority;
            this.mFacesPrev = faceArr;
        }
        CameraCharacteristics cameraCharacteristics = legacyRequest.characteristics;
        CaptureRequest captureRequest = legacyRequest.captureRequest;
        Size size = legacyRequest.previewSize;
        Camera.Parameters parameters = legacyRequest.parameters;
        Rect rect = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        ParameterUtils.ZoomData zoomDataConvertScalerCropRegion = ParameterUtils.convertScalerCropRegion(rect, (Rect) captureRequest.get(CaptureRequest.SCALER_CROP_REGION), size, parameters);
        ArrayList arrayList = new ArrayList();
        if (faceArr != null) {
            for (Camera.Face face : faceArr) {
                if (face != null) {
                    arrayList.add(ParameterUtils.convertFaceFromLegacy(face, rect, zoomDataConvertScalerCropRegion));
                } else {
                    Log.w(TAG, "mapResultFaces - read NULL face from camera1 device");
                }
            }
        }
        cameraMetadataNative.set(CaptureResult.STATISTICS_FACES, (Face[]) arrayList.toArray(new Face[0]));
        cameraMetadataNative.set(CaptureResult.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(i));
        if (z) {
            cameraMetadataNative.set((CaptureResult.Key<int>) CaptureResult.CONTROL_SCENE_MODE, 1);
        }
    }
}
