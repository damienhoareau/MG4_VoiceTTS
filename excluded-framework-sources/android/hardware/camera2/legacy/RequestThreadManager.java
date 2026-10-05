package android.hardware.camera2.legacy;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.MutableLong;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class RequestThreadManager {
    private static final float ASPECT_RATIO_TOLERANCE = 0.01f;
    private static final boolean DEBUG = false;
    private static final int JPEG_FRAME_TIMEOUT = 4000;
    private static final int MAX_IN_FLIGHT_REQUESTS = 2;
    private static final int MSG_CLEANUP = 3;
    private static final int MSG_CONFIGURE_OUTPUTS = 1;
    private static final int MSG_SUBMIT_CAPTURE_REQUEST = 2;
    private static final int PREVIEW_FRAME_TIMEOUT = 1000;
    private static final int REQUEST_COMPLETE_TIMEOUT = 4000;
    private static final boolean USE_BLOB_FORMAT_OVERRIDE = true;
    private static final boolean VERBOSE = false;
    private final String TAG;
    private Camera mCamera;
    private final int mCameraId;
    private final CaptureCollector mCaptureCollector;
    private final CameraCharacteristics mCharacteristics;
    private final CameraDeviceState mDeviceState;
    private Surface mDummySurface;
    private SurfaceTexture mDummyTexture;
    private final LegacyFaceDetectMapper mFaceDetectMapper;
    private final LegacyFocusStateMapper mFocusStateMapper;
    private GLThreadManager mGLThreadManager;
    private Size mIntermediateBufferSize;
    private Camera.Parameters mParams;
    private SurfaceTexture mPreviewTexture;
    private final RequestHandlerThread mRequestThread;
    private boolean mPreviewRunning = false;
    private final List<Surface> mPreviewOutputs = new ArrayList();
    private final List<Surface> mCallbackOutputs = new ArrayList();
    private final List<Long> mJpegSurfaceIds = new ArrayList();
    private final RequestQueue mRequestQueue = new RequestQueue(this.mJpegSurfaceIds);
    private LegacyRequest mLastRequest = null;
    private final Object mIdleLock = new Object();
    private final FpsCounter mPrevCounter = new FpsCounter("Incoming Preview");
    private final FpsCounter mRequestCounter = new FpsCounter("Incoming Requests");
    private final AtomicBoolean mQuit = new AtomicBoolean(false);
    private final Camera.ErrorCallback mErrorCallback = new Camera.ErrorCallback() { // from class: android.hardware.camera2.legacy.RequestThreadManager.1
        @Override // android.hardware.Camera.ErrorCallback
        public void onError(int i, Camera camera) {
            if (i == 2) {
                RequestThreadManager.this.flush();
                RequestThreadManager.this.mDeviceState.setError(0);
                return;
            }
            if (i != 3) {
                Log.e(RequestThreadManager.this.TAG, "Received error " + i + " from the Camera1 ErrorCallback");
                RequestThreadManager.this.mDeviceState.setError(1);
                return;
            }
            RequestThreadManager.this.flush();
            RequestThreadManager.this.mDeviceState.setError(6);
        }
    };
    private final ConditionVariable mReceivedJpeg = new ConditionVariable(false);
    private final Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() { // from class: android.hardware.camera2.legacy.RequestThreadManager.2
        @Override // android.hardware.Camera.PictureCallback
        public void onPictureTaken(byte[] bArr, Camera camera) {
            Log.i(RequestThreadManager.this.TAG, "Received jpeg.");
            Pair<RequestHolder, Long> pairJpegProduced = RequestThreadManager.this.mCaptureCollector.jpegProduced();
            if (pairJpegProduced == null || pairJpegProduced.first == null) {
                Log.e(RequestThreadManager.this.TAG, "Dropping jpeg frame.");
                return;
            }
            RequestHolder requestHolder = pairJpegProduced.first;
            long jLongValue = pairJpegProduced.second.longValue();
            for (Surface surface : requestHolder.getHolderTargets()) {
                try {
                    if (LegacyCameraDevice.containsSurfaceId(surface, RequestThreadManager.this.mJpegSurfaceIds)) {
                        Log.i(RequestThreadManager.this.TAG, "Producing jpeg buffer...");
                        int length = (bArr.length + LegacyCameraDevice.nativeGetJpegFooterSize() + 3) & (-4);
                        LegacyCameraDevice.setNextTimestamp(surface, jLongValue);
                        LegacyCameraDevice.setSurfaceFormat(surface, 1);
                        int iCeil = (((int) Math.ceil(Math.sqrt(length))) + 15) & (-16);
                        LegacyCameraDevice.setSurfaceDimens(surface, iCeil, iCeil);
                        LegacyCameraDevice.produceFrame(surface, bArr, iCeil, iCeil, 33);
                    }
                } catch (LegacyExceptionUtils.BufferQueueAbandonedException e) {
                    Log.w(RequestThreadManager.this.TAG, "Surface abandoned, dropping frame. ", e);
                }
            }
            RequestThreadManager.this.mReceivedJpeg.open();
        }
    };
    private final Camera.ShutterCallback mJpegShutterCallback = new Camera.ShutterCallback() { // from class: android.hardware.camera2.legacy.RequestThreadManager.3
        @Override // android.hardware.Camera.ShutterCallback
        public void onShutter() {
            RequestThreadManager.this.mCaptureCollector.jpegCaptured(SystemClock.elapsedRealtimeNanos());
        }
    };
    private final SurfaceTexture.OnFrameAvailableListener mPreviewCallback = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.hardware.camera2.legacy.RequestThreadManager.4
        @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            RequestThreadManager.this.mGLThreadManager.queueNewFrame();
        }
    };
    private final Handler.Callback mRequestHandlerCb = new Handler.Callback() { // from class: android.hardware.camera2.legacy.RequestThreadManager.5
        private boolean mCleanup = false;
        private final LegacyResultMapper mMapper = new LegacyResultMapper();

        /* JADX WARN: Code duplicated, block: B:102:0x033c  */
        /* JADX WARN: Code duplicated, block: B:154:0x020a A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:96:0x02ed A[Catch: InterruptedException -> 0x033f, TRY_LEAVE, TryCatch #8 {InterruptedException -> 0x033f, blocks: (B:94:0x02da, B:96:0x02ed), top: B:141:0x02da }] */
        /* JADX WARN: Code duplicated, block: B:99:0x032d  */
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i;
            int i2;
            int i3;
            MutableLong mutableLong;
            CameraMetadataNative cameraMetadataNativeCachedConvertResultMetadata;
            int i4 = 1;
            if (this.mCleanup) {
                return true;
            }
            int i5 = message.what;
            if (i5 != -1) {
                if (i5 == 1) {
                    ConfigureHolder configureHolder = (ConfigureHolder) message.obj;
                    int size = configureHolder.surfaces != null ? configureHolder.surfaces.size() : 0;
                    Log.i(RequestThreadManager.this.TAG, "Configure outputs: " + size + " surfaces configured.");
                    try {
                        if (!RequestThreadManager.this.mCaptureCollector.waitForEmpty(4000L, TimeUnit.MILLISECONDS)) {
                            Log.e(RequestThreadManager.this.TAG, "Timed out while queueing configure request.");
                            RequestThreadManager.this.mCaptureCollector.failAll();
                        }
                        RequestThreadManager.this.configureOutputs(configureHolder.surfaces);
                        configureHolder.condition.open();
                    } catch (InterruptedException unused) {
                        Log.e(RequestThreadManager.this.TAG, "Interrupted while waiting for requests to complete.");
                        RequestThreadManager.this.mDeviceState.setError(1);
                        return true;
                    }
                } else if (i5 == 2) {
                    Handler handler = RequestThreadManager.this.mRequestThread.getHandler();
                    RequestQueue.RequestQueueEntry next = RequestThreadManager.this.mRequestQueue.getNext();
                    if (next == null) {
                        try {
                            if (!RequestThreadManager.this.mCaptureCollector.waitForEmpty(4000L, TimeUnit.MILLISECONDS)) {
                                Log.e(RequestThreadManager.this.TAG, "Timed out while waiting for prior requests to complete.");
                                RequestThreadManager.this.mCaptureCollector.failAll();
                            }
                            synchronized (RequestThreadManager.this.mIdleLock) {
                                RequestQueue.RequestQueueEntry next2 = RequestThreadManager.this.mRequestQueue.getNext();
                                if (next2 == null) {
                                    RequestThreadManager.this.mDeviceState.setIdle();
                                } else {
                                    next = next2;
                                }
                            }
                        } catch (InterruptedException e) {
                            Log.e(RequestThreadManager.this.TAG, "Interrupted while waiting for requests to complete: ", e);
                            RequestThreadManager.this.mDeviceState.setError(1);
                        }
                    }
                    if (next != null) {
                        handler.sendEmptyMessage(2);
                        if (next.isQueueEmpty()) {
                            RequestThreadManager.this.mDeviceState.setRequestQueueEmpty();
                        }
                    }
                    BurstHolder burstHolder = next.getBurstHolder();
                    boolean z = false;
                    for (RequestHolder requestHolder : burstHolder.produceRequestHolders(next.getFrameNumber().longValue())) {
                        CaptureRequest request = requestHolder.getRequest();
                        if (RequestThreadManager.this.mLastRequest == null || RequestThreadManager.this.mLastRequest.captureRequest != request) {
                            LegacyRequest legacyRequest = new LegacyRequest(RequestThreadManager.this.mCharacteristics, request, ParameterUtils.convertSize(RequestThreadManager.this.mParams.getPreviewSize()), RequestThreadManager.this.mParams);
                            LegacyMetadataMapper.convertRequestMetadata(legacyRequest);
                            if (RequestThreadManager.this.mParams.same(legacyRequest.parameters)) {
                                i = 0;
                            } else {
                                try {
                                    RequestThreadManager.this.mCamera.setParameters(legacyRequest.parameters);
                                    RequestThreadManager.this.mParams = legacyRequest.parameters;
                                    i = i4;
                                } catch (RuntimeException e2) {
                                    Log.e(RequestThreadManager.this.TAG, "Exception while setting camera parameters: ", e2);
                                    requestHolder.failRequest();
                                    RequestThreadManager.this.mDeviceState.setCaptureStart(requestHolder, 0L, 3);
                                }
                            }
                            RequestThreadManager.this.mLastRequest = legacyRequest;
                        } else {
                            i = 0;
                        }
                        try {
                            try {
                                int i6 = i;
                                try {
                                    if (!RequestThreadManager.this.mCaptureCollector.queueRequest(requestHolder, RequestThreadManager.this.mLastRequest, 4000L, TimeUnit.MILLISECONDS)) {
                                        Log.e(RequestThreadManager.this.TAG, "Timed out while queueing capture request.");
                                        requestHolder.failRequest();
                                        RequestThreadManager.this.mDeviceState.setCaptureStart(requestHolder, 0L, 3);
                                    } else {
                                        if (requestHolder.hasPreviewTargets()) {
                                            RequestThreadManager.this.doPreviewCapture(requestHolder);
                                        }
                                        if (requestHolder.hasJpegTargets()) {
                                            while (!RequestThreadManager.this.mCaptureCollector.waitForPreviewsEmpty(1000L, TimeUnit.MILLISECONDS)) {
                                                Log.e(RequestThreadManager.this.TAG, "Timed out while waiting for preview requests to complete.");
                                                RequestThreadManager.this.mCaptureCollector.failNextPreview();
                                            }
                                            RequestThreadManager.this.mReceivedJpeg.close();
                                            RequestThreadManager.this.doJpegCapturePrepare(requestHolder);
                                        }
                                        RequestThreadManager.this.mFaceDetectMapper.processFaceDetectMode(request, RequestThreadManager.this.mParams);
                                        RequestThreadManager.this.mFocusStateMapper.processRequestTriggers(request, RequestThreadManager.this.mParams);
                                        if (requestHolder.hasJpegTargets()) {
                                            RequestThreadManager.this.doJpegCapture(requestHolder);
                                            if (!RequestThreadManager.this.mReceivedJpeg.block(4000L)) {
                                                Log.e(RequestThreadManager.this.TAG, "Hit timeout for jpeg callback!");
                                                RequestThreadManager.this.mCaptureCollector.failNextJpeg();
                                            }
                                        }
                                        if (i6 != 0) {
                                            try {
                                                RequestThreadManager.this.mParams = RequestThreadManager.this.mCamera.getParameters();
                                                RequestThreadManager.this.mLastRequest.setParameters(RequestThreadManager.this.mParams);
                                                mutableLong = new MutableLong(0L);
                                                try {
                                                    if (!RequestThreadManager.this.mCaptureCollector.waitForRequestCompleted(requestHolder, 4000L, TimeUnit.MILLISECONDS, mutableLong)) {
                                                        Log.e(RequestThreadManager.this.TAG, "Timed out while waiting for request to complete.");
                                                        RequestThreadManager.this.mCaptureCollector.failAll();
                                                    }
                                                    cameraMetadataNativeCachedConvertResultMetadata = this.mMapper.cachedConvertResultMetadata(RequestThreadManager.this.mLastRequest, mutableLong.value);
                                                    RequestThreadManager.this.mFocusStateMapper.mapResultTriggers(cameraMetadataNativeCachedConvertResultMetadata);
                                                    RequestThreadManager.this.mFaceDetectMapper.mapResultFaces(cameraMetadataNativeCachedConvertResultMetadata, RequestThreadManager.this.mLastRequest);
                                                    if (!requestHolder.requestFailed()) {
                                                        RequestThreadManager.this.mDeviceState.setCaptureResult(requestHolder, cameraMetadataNativeCachedConvertResultMetadata);
                                                    }
                                                    if (requestHolder.isOutputAbandoned()) {
                                                        z = true;
                                                    }
                                                } catch (InterruptedException e3) {
                                                    Log.e(RequestThreadManager.this.TAG, "Interrupted waiting for request completion: ", e3);
                                                    RequestThreadManager.this.mDeviceState.setError(1);
                                                }
                                            } catch (RuntimeException e4) {
                                                Log.e(RequestThreadManager.this.TAG, "Received device exception: ", e4);
                                                RequestThreadManager.this.mDeviceState.setError(1);
                                            }
                                        } else {
                                            mutableLong = new MutableLong(0L);
                                            if (!RequestThreadManager.this.mCaptureCollector.waitForRequestCompleted(requestHolder, 4000L, TimeUnit.MILLISECONDS, mutableLong)) {
                                                Log.e(RequestThreadManager.this.TAG, "Timed out while waiting for request to complete.");
                                                RequestThreadManager.this.mCaptureCollector.failAll();
                                            }
                                            cameraMetadataNativeCachedConvertResultMetadata = this.mMapper.cachedConvertResultMetadata(RequestThreadManager.this.mLastRequest, mutableLong.value);
                                            RequestThreadManager.this.mFocusStateMapper.mapResultTriggers(cameraMetadataNativeCachedConvertResultMetadata);
                                            RequestThreadManager.this.mFaceDetectMapper.mapResultFaces(cameraMetadataNativeCachedConvertResultMetadata, RequestThreadManager.this.mLastRequest);
                                            if (!requestHolder.requestFailed()) {
                                                RequestThreadManager.this.mDeviceState.setCaptureResult(requestHolder, cameraMetadataNativeCachedConvertResultMetadata);
                                            }
                                            if (requestHolder.isOutputAbandoned()) {
                                                z = true;
                                            }
                                        }
                                    }
                                    i4 = 1;
                                } catch (IOException e5) {
                                    e = e5;
                                    i3 = 1;
                                    Log.e(RequestThreadManager.this.TAG, "Received device exception during capture call: ", e);
                                    RequestThreadManager.this.mDeviceState.setError(i3);
                                } catch (InterruptedException e6) {
                                    e = e6;
                                    i2 = 1;
                                    Log.e(RequestThreadManager.this.TAG, "Interrupted during capture: ", e);
                                    RequestThreadManager.this.mDeviceState.setError(i2);
                                }
                            } catch (RuntimeException e7) {
                                Log.e(RequestThreadManager.this.TAG, "Received device exception during capture call: ", e7);
                                RequestThreadManager.this.mDeviceState.setError(1);
                            }
                        } catch (IOException e8) {
                            e = e8;
                            i3 = i4;
                        } catch (InterruptedException e9) {
                            e = e9;
                            i2 = i4;
                        }
                    }
                    if (z && burstHolder.isRepeating()) {
                        RequestThreadManager.this.mDeviceState.setRepeatingRequestError(RequestThreadManager.this.cancelRepeating(burstHolder.getRequestId()), burstHolder.getRequestId());
                    }
                } else if (i5 == 3) {
                    this.mCleanup = true;
                    try {
                        if (!RequestThreadManager.this.mCaptureCollector.waitForEmpty(4000L, TimeUnit.MILLISECONDS)) {
                            Log.e(RequestThreadManager.this.TAG, "Timed out while queueing cleanup request.");
                            RequestThreadManager.this.mCaptureCollector.failAll();
                        }
                    } catch (InterruptedException e10) {
                        Log.e(RequestThreadManager.this.TAG, "Interrupted while waiting for requests to complete: ", e10);
                        RequestThreadManager.this.mDeviceState.setError(1);
                    }
                    if (RequestThreadManager.this.mGLThreadManager != null) {
                        RequestThreadManager.this.mGLThreadManager.quit();
                        RequestThreadManager.this.mGLThreadManager = null;
                    }
                    if (RequestThreadManager.this.mCamera != null) {
                        RequestThreadManager.this.mCamera.release();
                        RequestThreadManager.this.mCamera = null;
                    }
                    RequestThreadManager requestThreadManager = RequestThreadManager.this;
                    requestThreadManager.resetJpegSurfaceFormats(requestThreadManager.mCallbackOutputs);
                } else {
                    throw new AssertionError("Unhandled message " + message.what + " on RequestThread.");
                }
                return true;
            }
            return true;
        }
    };

    private static class ConfigureHolder {
        public final ConditionVariable condition;
        public final Collection<Pair<Surface, Size>> surfaces;

        public ConfigureHolder(ConditionVariable conditionVariable, Collection<Pair<Surface, Size>> collection) {
            this.condition = conditionVariable;
            this.surfaces = collection;
        }
    }

    public static class FpsCounter {
        private static final long NANO_PER_SECOND = 1000000000;
        private static final String TAG = "FpsCounter";
        private final String mStreamType;
        private int mFrameCount = 0;
        private long mLastTime = 0;
        private long mLastPrintTime = 0;
        private double mLastFps = 0.0d;

        public FpsCounter(String str) {
            this.mStreamType = str;
        }

        public synchronized void countFrame() {
            this.mFrameCount++;
            long jElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
            if (this.mLastTime == 0) {
                this.mLastTime = jElapsedRealtimeNanos;
            }
            if (jElapsedRealtimeNanos > this.mLastTime + 1000000000) {
                this.mLastFps = ((double) this.mFrameCount) * (1.0E9d / (jElapsedRealtimeNanos - this.mLastTime));
                this.mFrameCount = 0;
                this.mLastTime = jElapsedRealtimeNanos;
            }
        }

        public synchronized double checkFps() {
            return this.mLastFps;
        }

        public synchronized void staggeredLog() {
            if (this.mLastTime > this.mLastPrintTime + 5000000000L) {
                this.mLastPrintTime = this.mLastTime;
                Log.d(TAG, "FPS for " + this.mStreamType + " stream: " + this.mLastFps);
            }
        }

        public synchronized void countAndLog() {
            countFrame();
            staggeredLog();
        }
    }

    private void createDummySurface() {
        if (this.mDummyTexture == null || this.mDummySurface == null) {
            SurfaceTexture surfaceTexture = new SurfaceTexture(0);
            this.mDummyTexture = surfaceTexture;
            surfaceTexture.setDefaultBufferSize(640, 480);
            this.mDummySurface = new Surface(this.mDummyTexture);
        }
    }

    private void stopPreview() {
        if (this.mPreviewRunning) {
            this.mCamera.stopPreview();
            this.mPreviewRunning = false;
        }
    }

    private void startPreview() {
        if (this.mPreviewRunning) {
            return;
        }
        this.mCamera.startPreview();
        this.mPreviewRunning = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doJpegCapturePrepare(RequestHolder requestHolder) throws IOException {
        if (this.mPreviewRunning) {
            return;
        }
        createDummySurface();
        this.mCamera.setPreviewTexture(this.mDummyTexture);
        startPreview();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doJpegCapture(RequestHolder requestHolder) {
        this.mCamera.takePicture(this.mJpegShutterCallback, null, this.mJpegCallback);
        this.mPreviewRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPreviewCapture(RequestHolder requestHolder) throws IOException {
        if (this.mPreviewRunning) {
            return;
        }
        SurfaceTexture surfaceTexture = this.mPreviewTexture;
        if (surfaceTexture == null) {
            throw new IllegalStateException("Preview capture called with no preview surfaces configured.");
        }
        surfaceTexture.setDefaultBufferSize(this.mIntermediateBufferSize.getWidth(), this.mIntermediateBufferSize.getHeight());
        this.mCamera.setPreviewTexture(this.mPreviewTexture);
        startPreview();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configureOutputs(Collection<Pair<Surface, Size>> collection) {
        try {
            stopPreview();
            try {
                this.mCamera.setPreviewTexture(null);
            } catch (IOException e) {
                Log.w(this.TAG, "Failed to clear prior SurfaceTexture, may cause GL deadlock: ", e);
            } catch (RuntimeException e2) {
                Log.e(this.TAG, "Received device exception in configure call: ", e2);
                this.mDeviceState.setError(1);
                return;
            }
            GLThreadManager gLThreadManager = this.mGLThreadManager;
            if (gLThreadManager != null) {
                gLThreadManager.waitUntilStarted();
                this.mGLThreadManager.ignoreNewFrames();
                this.mGLThreadManager.waitUntilIdle();
            }
            resetJpegSurfaceFormats(this.mCallbackOutputs);
            Iterator<Surface> it = this.mCallbackOutputs.iterator();
            while (it.hasNext()) {
                try {
                    LegacyCameraDevice.disconnectSurface(it.next());
                } catch (LegacyExceptionUtils.BufferQueueAbandonedException e3) {
                    Log.w(this.TAG, "Surface abandoned, skipping...", e3);
                }
            }
            this.mPreviewOutputs.clear();
            this.mCallbackOutputs.clear();
            this.mJpegSurfaceIds.clear();
            this.mPreviewTexture = null;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int iIntValue = ((Integer) this.mCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue();
            int iIntValue2 = ((Integer) this.mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
            if (collection != null) {
                for (Pair<Surface, Size> pair : collection) {
                    Surface surface = pair.first;
                    Size size = pair.second;
                    try {
                        int iDetectSurfaceType = LegacyCameraDevice.detectSurfaceType(surface);
                        LegacyCameraDevice.setSurfaceOrientation(surface, iIntValue, iIntValue2);
                        if (iDetectSurfaceType == 33) {
                            LegacyCameraDevice.setSurfaceFormat(surface, 1);
                            this.mJpegSurfaceIds.add(Long.valueOf(LegacyCameraDevice.getSurfaceId(surface)));
                            this.mCallbackOutputs.add(surface);
                            arrayList2.add(size);
                            LegacyCameraDevice.connectSurface(surface);
                        } else {
                            LegacyCameraDevice.setScalingMode(surface, 1);
                            this.mPreviewOutputs.add(surface);
                            arrayList.add(size);
                        }
                    } catch (LegacyExceptionUtils.BufferQueueAbandonedException e4) {
                        Log.w(this.TAG, "Surface abandoned, skipping...", e4);
                    }
                }
            }
            try {
                Camera.Parameters parameters = this.mCamera.getParameters();
                this.mParams = parameters;
                int[] photoPreviewFpsRange = getPhotoPreviewFpsRange(parameters.getSupportedPreviewFpsRange());
                this.mParams.setPreviewFpsRange(photoPreviewFpsRange[0], photoPreviewFpsRange[1]);
                Size sizeCalculatePictureSize = calculatePictureSize(this.mCallbackOutputs, arrayList2, this.mParams);
                if (arrayList.size() > 0) {
                    Size sizeFindLargestByArea = android.hardware.camera2.utils.SizeAreaComparator.findLargestByArea(arrayList);
                    Size largestSupportedJpegSizeByArea = ParameterUtils.getLargestSupportedJpegSizeByArea(this.mParams);
                    if (sizeCalculatePictureSize != null) {
                        largestSupportedJpegSizeByArea = sizeCalculatePictureSize;
                    }
                    List<Size> listConvertSizeList = ParameterUtils.convertSizeList(this.mParams.getSupportedPreviewSizes());
                    long height = ((long) sizeFindLargestByArea.getHeight()) * ((long) sizeFindLargestByArea.getWidth());
                    Size sizeFindLargestByArea2 = android.hardware.camera2.utils.SizeAreaComparator.findLargestByArea(listConvertSizeList);
                    for (Size size2 : listConvertSizeList) {
                        long width = size2.getWidth() * size2.getHeight();
                        long width2 = sizeFindLargestByArea2.getWidth() * sizeFindLargestByArea2.getHeight();
                        if (checkAspectRatiosMatch(largestSupportedJpegSizeByArea, size2) && width < width2 && width >= height) {
                            sizeFindLargestByArea2 = size2;
                        }
                    }
                    this.mIntermediateBufferSize = sizeFindLargestByArea2;
                    this.mParams.setPreviewSize(sizeFindLargestByArea2.getWidth(), this.mIntermediateBufferSize.getHeight());
                } else {
                    this.mIntermediateBufferSize = null;
                }
                if (sizeCalculatePictureSize != null) {
                    Log.i(this.TAG, "configureOutputs - set take picture size to " + sizeCalculatePictureSize);
                    this.mParams.setPictureSize(sizeCalculatePictureSize.getWidth(), sizeCalculatePictureSize.getHeight());
                }
                if (this.mGLThreadManager == null) {
                    GLThreadManager gLThreadManager2 = new GLThreadManager(this.mCameraId, iIntValue, this.mDeviceState);
                    this.mGLThreadManager = gLThreadManager2;
                    gLThreadManager2.start();
                }
                this.mGLThreadManager.waitUntilStarted();
                ArrayList arrayList3 = new ArrayList();
                Iterator it2 = arrayList.iterator();
                Iterator<Surface> it3 = this.mPreviewOutputs.iterator();
                while (it3.hasNext()) {
                    arrayList3.add(new Pair(it3.next(), (Size) it2.next()));
                }
                this.mGLThreadManager.setConfigurationAndWait(arrayList3, this.mCaptureCollector);
                Iterator<Surface> it4 = this.mPreviewOutputs.iterator();
                while (it4.hasNext()) {
                    try {
                        LegacyCameraDevice.setSurfaceOrientation(it4.next(), iIntValue, iIntValue2);
                    } catch (LegacyExceptionUtils.BufferQueueAbandonedException e5) {
                        Log.e(this.TAG, "Surface abandoned, skipping setSurfaceOrientation()", e5);
                    }
                }
                this.mGLThreadManager.allowNewFrames();
                SurfaceTexture currentSurfaceTexture = this.mGLThreadManager.getCurrentSurfaceTexture();
                this.mPreviewTexture = currentSurfaceTexture;
                if (currentSurfaceTexture != null) {
                    currentSurfaceTexture.setOnFrameAvailableListener(this.mPreviewCallback);
                }
                try {
                    this.mCamera.setParameters(this.mParams);
                } catch (RuntimeException e6) {
                    Log.e(this.TAG, "Received device exception while configuring: ", e6);
                    this.mDeviceState.setError(1);
                }
            } catch (RuntimeException e7) {
                Log.e(this.TAG, "Received device exception: ", e7);
                this.mDeviceState.setError(1);
            }
        } catch (RuntimeException e8) {
            Log.e(this.TAG, "Received device exception in configure call: ", e8);
            this.mDeviceState.setError(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetJpegSurfaceFormats(Collection<Surface> collection) {
        if (collection == null) {
            return;
        }
        for (Surface surface : collection) {
            if (surface == null || !surface.isValid()) {
                Log.w(this.TAG, "Jpeg surface is invalid, skipping...");
            } else {
                try {
                    LegacyCameraDevice.setSurfaceFormat(surface, 33);
                } catch (LegacyExceptionUtils.BufferQueueAbandonedException e) {
                    Log.w(this.TAG, "Surface abandoned, skipping...", e);
                }
            }
        }
    }

    private Size calculatePictureSize(List<Surface> list, List<Size> list2, Camera.Parameters parameters) {
        if (list.size() != list2.size()) {
            throw new IllegalStateException("Input collections must be same length");
        }
        ArrayList<Size> arrayList = new ArrayList();
        Iterator<Size> it = list2.iterator();
        for (Surface surface : list) {
            Size next = it.next();
            if (LegacyCameraDevice.containsSurfaceId(surface, this.mJpegSurfaceIds)) {
                arrayList.add(next);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        int width = -1;
        int height = -1;
        for (Size size : arrayList) {
            if (size.getWidth() > width) {
                width = size.getWidth();
            }
            if (size.getHeight() > height) {
                height = size.getHeight();
            }
        }
        Size size2 = new Size(width, height);
        List<Size> listConvertSizeList = ParameterUtils.convertSizeList(parameters.getSupportedPictureSizes());
        ArrayList arrayList2 = new ArrayList();
        for (Size size3 : listConvertSizeList) {
            if (size3.getWidth() >= width && size3.getHeight() >= height) {
                arrayList2.add(size3);
            }
        }
        if (arrayList2.isEmpty()) {
            throw new AssertionError("Could not find any supported JPEG sizes large enough to fit " + size2);
        }
        Size size4 = (Size) Collections.min(arrayList2, new android.hardware.camera2.utils.SizeAreaComparator());
        if (!size4.equals(size2)) {
            Log.w(this.TAG, String.format("configureOutputs - Will need to crop picture %s into smallest bound size %s", size4, size2));
        }
        return size4;
    }

    private static boolean checkAspectRatiosMatch(Size size, Size size2) {
        return Math.abs((((float) size.getWidth()) / ((float) size.getHeight())) - (((float) size2.getWidth()) / ((float) size2.getHeight()))) < ASPECT_RATIO_TOLERANCE;
    }

    private int[] getPhotoPreviewFpsRange(List<int[]> list) {
        if (list.size() == 0) {
            Log.e(this.TAG, "No supported frame rates returned!");
            return null;
        }
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (int[] iArr : list) {
            int i5 = iArr[0];
            int i6 = iArr[1];
            if (i6 > i2 || (i6 == i2 && i5 > i3)) {
                i = i4;
                i2 = i6;
                i3 = i5;
            }
            i4++;
        }
        return list.get(i);
    }

    public RequestThreadManager(int i, Camera camera, CameraCharacteristics cameraCharacteristics, CameraDeviceState cameraDeviceState) {
        this.mCamera = (Camera) Preconditions.checkNotNull(camera, "camera must not be null");
        this.mCameraId = i;
        this.mCharacteristics = (CameraCharacteristics) Preconditions.checkNotNull(cameraCharacteristics, "characteristics must not be null");
        String str = String.format("RequestThread-%d", Integer.valueOf(i));
        this.TAG = str;
        this.mDeviceState = (CameraDeviceState) Preconditions.checkNotNull(cameraDeviceState, "deviceState must not be null");
        this.mFocusStateMapper = new LegacyFocusStateMapper(this.mCamera);
        this.mFaceDetectMapper = new LegacyFaceDetectMapper(this.mCamera, this.mCharacteristics);
        this.mCaptureCollector = new CaptureCollector(2, this.mDeviceState);
        this.mRequestThread = new RequestHandlerThread(str, this.mRequestHandlerCb);
        this.mCamera.setDetailedErrorCallback(this.mErrorCallback);
    }

    public void start() {
        this.mRequestThread.start();
    }

    public long flush() {
        Log.i(this.TAG, "Flushing all pending requests.");
        long jStopRepeating = this.mRequestQueue.stopRepeating();
        this.mCaptureCollector.failAll();
        return jStopRepeating;
    }

    public void quit() {
        if (this.mQuit.getAndSet(true)) {
            return;
        }
        Handler handlerWaitAndGetHandler = this.mRequestThread.waitAndGetHandler();
        handlerWaitAndGetHandler.sendMessageAtFrontOfQueue(handlerWaitAndGetHandler.obtainMessage(3));
        this.mRequestThread.quitSafely();
        try {
            this.mRequestThread.join();
        } catch (InterruptedException unused) {
            Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", this.mRequestThread.getName(), Long.valueOf(this.mRequestThread.getId())));
        }
    }

    public SubmitInfo submitCaptureRequests(CaptureRequest[] captureRequestArr, boolean z) {
        SubmitInfo submitInfoSubmit;
        Handler handlerWaitAndGetHandler = this.mRequestThread.waitAndGetHandler();
        synchronized (this.mIdleLock) {
            submitInfoSubmit = this.mRequestQueue.submit(captureRequestArr, z);
            handlerWaitAndGetHandler.sendEmptyMessage(2);
        }
        return submitInfoSubmit;
    }

    public long cancelRepeating(int i) {
        return this.mRequestQueue.stopRepeating(i);
    }

    public void configure(Collection<Pair<Surface, Size>> collection) {
        Handler handlerWaitAndGetHandler = this.mRequestThread.waitAndGetHandler();
        ConditionVariable conditionVariable = new ConditionVariable(false);
        handlerWaitAndGetHandler.sendMessage(handlerWaitAndGetHandler.obtainMessage(1, 0, 0, new ConfigureHolder(conditionVariable, collection)));
        conditionVariable.block();
    }
}
