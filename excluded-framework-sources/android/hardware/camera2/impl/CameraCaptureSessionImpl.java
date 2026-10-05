package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.TaskDrainer;
import android.hardware.camera2.utils.TaskSingleDrainer;
import android.os.Binder;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class CameraCaptureSessionImpl extends CameraCaptureSession implements CameraCaptureSessionCore {
    private static final boolean DEBUG = true;
    private static final String TAG = "CameraCaptureSession";
    private final TaskSingleDrainer mAbortDrainer;
    private volatile boolean mAborting;
    private boolean mClosed;
    private final boolean mConfigureSuccess;
    private final Executor mDeviceExecutor;
    private final CameraDeviceImpl mDeviceImpl;
    private final int mId;
    private final String mIdString;
    private final TaskSingleDrainer mIdleDrainer;
    private final Surface mInput;
    private final TaskDrainer<Integer> mSequenceDrainer;
    private boolean mSkipUnconfigure = false;
    private final CameraCaptureSession.StateCallback mStateCallback;
    private final Executor mStateExecutor;

    CameraCaptureSessionImpl(int i, Surface surface, CameraCaptureSession.StateCallback stateCallback, Executor executor, CameraDeviceImpl cameraDeviceImpl, Executor executor2, boolean z) {
        this.mClosed = false;
        if (stateCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        this.mId = i;
        this.mIdString = String.format("Session %d: ", Integer.valueOf(i));
        this.mInput = surface;
        Executor executor3 = (Executor) Preconditions.checkNotNull(executor, "stateExecutor must not be null");
        this.mStateExecutor = executor3;
        this.mStateCallback = createUserStateCallbackProxy(executor3, stateCallback);
        this.mDeviceExecutor = (Executor) Preconditions.checkNotNull(executor2, "deviceStateExecutor must not be null");
        this.mDeviceImpl = (CameraDeviceImpl) Preconditions.checkNotNull(cameraDeviceImpl, "deviceImpl must not be null");
        AnonymousClass1 anonymousClass1 = null;
        this.mSequenceDrainer = new TaskDrainer<>(this.mDeviceExecutor, new SequenceDrainListener(this, anonymousClass1), "seq");
        this.mIdleDrainer = new TaskSingleDrainer(this.mDeviceExecutor, new IdleDrainListener(this, anonymousClass1), "idle");
        this.mAbortDrainer = new TaskSingleDrainer(this.mDeviceExecutor, new AbortDrainListener(this, anonymousClass1), "abort");
        if (z) {
            this.mStateCallback.onConfigured(this);
            Log.v(TAG, this.mIdString + "Created session successfully");
            this.mConfigureSuccess = true;
            return;
        }
        this.mStateCallback.onConfigureFailed(this);
        this.mClosed = true;
        Log.e(TAG, this.mIdString + "Failed to create capture session; configuration failed");
        this.mConfigureSuccess = false;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraDevice getDevice() {
        return this.mDeviceImpl;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(Surface surface) throws CameraAccessException {
        Log.d(TAG, "prepare: surface = " + surface);
        this.mDeviceImpl.prepare(surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(int i, Surface surface) throws CameraAccessException {
        Log.d(TAG, "prepare: surface = " + surface + ", maxCount = " + i);
        this.mDeviceImpl.prepare(i, surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void tearDown(Surface surface) throws CameraAccessException {
        Log.d(TAG, "tearDown: surface = " + surface);
        this.mDeviceImpl.tearDown(surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void finalizeOutputConfigurations(List<OutputConfiguration> list) throws CameraAccessException {
        Log.d(TAG, "finalizeOutputConfigurations");
        this.mDeviceImpl.finalizeOutputConfigs(list);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int capture(CaptureRequest captureRequest, CameraCaptureSession.CaptureCallback captureCallback, Handler handler) throws CameraAccessException {
        int iAddPendingSequence;
        checkCaptureRequest(captureRequest);
        Log.d(TAG, "capture: request = " + captureRequest.toString());
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Handler handlerCheckHandler = CameraDeviceImpl.checkHandler(handler, captureCallback);
            Log.v(TAG, this.mIdString + "capture - request " + captureRequest + ", callback " + captureCallback + " handler " + handlerCheckHandler);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.capture(captureRequest, createCaptureCallbackProxy(handlerCheckHandler, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureSingleRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException {
        int iAddPendingSequence;
        Log.d(TAG, "captureSingleRequest: ");
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (captureCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCaptureRequest(captureRequest);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Executor executorCheckExecutor = CameraDeviceImpl.checkExecutor(executor, captureCallback);
            Log.v(TAG, this.mIdString + "capture - request " + captureRequest + ", callback " + captureCallback + " executor " + executorCheckExecutor);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.capture(captureRequest, createCaptureCallbackProxyWithExecutor(executorCheckExecutor, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    private void checkCaptureRequest(CaptureRequest captureRequest) {
        if (captureRequest == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (captureRequest.isReprocess() && !isReprocessable()) {
            throw new IllegalArgumentException("this capture session cannot handle reprocess requests");
        }
        if (captureRequest.isReprocess() && captureRequest.getReprocessableSessionId() != this.mId) {
            throw new IllegalArgumentException("capture request was created for another session");
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurst(List<CaptureRequest> list, CameraCaptureSession.CaptureCallback captureCallback, Handler handler) throws CameraAccessException {
        int iAddPendingSequence;
        checkCaptureRequests(list);
        Log.d(TAG, "captureBurst: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Handler handlerCheckHandler = CameraDeviceImpl.checkHandler(handler, captureCallback);
            Log.v(TAG, this.mIdString + "captureBurst - requests " + Arrays.toString((CaptureRequest[]) list.toArray(new CaptureRequest[0])) + ", callback " + captureCallback + " handler " + handlerCheckHandler);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.captureBurst(list, createCaptureCallbackProxy(handlerCheckHandler, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException {
        int iAddPendingSequence;
        Log.d(TAG, "captureBurstRequests: ");
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (captureCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCaptureRequests(list);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Executor executorCheckExecutor = CameraDeviceImpl.checkExecutor(executor, captureCallback);
            Log.v(TAG, this.mIdString + "captureBurst - requests " + Arrays.toString((CaptureRequest[]) list.toArray(new CaptureRequest[0])) + ", callback " + captureCallback + " executor " + executorCheckExecutor);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.captureBurst(list, createCaptureCallbackProxyWithExecutor(executorCheckExecutor, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    private void checkCaptureRequests(List<CaptureRequest> list) {
        if (list == null) {
            throw new IllegalArgumentException("Requests must not be null");
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Requests must have at least one element");
        }
        for (CaptureRequest captureRequest : list) {
            if (captureRequest.isReprocess()) {
                if (!isReprocessable()) {
                    throw new IllegalArgumentException("This capture session cannot handle reprocess requests");
                }
                if (captureRequest.getReprocessableSessionId() != this.mId) {
                    throw new IllegalArgumentException("Capture request was created for another session");
                }
            }
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingRequest(CaptureRequest captureRequest, CameraCaptureSession.CaptureCallback captureCallback, Handler handler) throws CameraAccessException {
        int iAddPendingSequence;
        checkRepeatingRequest(captureRequest);
        Log.d(TAG, "setRepeatingRequest: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Handler handlerCheckHandler = CameraDeviceImpl.checkHandler(handler, captureCallback);
            Log.v(TAG, this.mIdString + "setRepeatingRequest - request " + captureRequest + ", callback " + captureCallback + " handler " + handlerCheckHandler);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingRequest(captureRequest, createCaptureCallbackProxy(handlerCheckHandler, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setSingleRepeatingRequest(CaptureRequest captureRequest, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException {
        int iAddPendingSequence;
        Log.d(TAG, "setSingleRepeatingRequest: ");
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (captureCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkRepeatingRequest(captureRequest);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Executor executorCheckExecutor = CameraDeviceImpl.checkExecutor(executor, captureCallback);
            Log.v(TAG, this.mIdString + "setRepeatingRequest - request " + captureRequest + ", callback " + captureCallback + " executor " + executorCheckExecutor);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingRequest(captureRequest, createCaptureCallbackProxyWithExecutor(executorCheckExecutor, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    private void checkRepeatingRequest(CaptureRequest captureRequest) {
        if (captureRequest == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (captureRequest.isReprocess()) {
            throw new IllegalArgumentException("repeating reprocess requests are not supported");
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurst(List<CaptureRequest> list, CameraCaptureSession.CaptureCallback captureCallback, Handler handler) throws CameraAccessException {
        int iAddPendingSequence;
        checkRepeatingRequests(list);
        Log.d(TAG, "setRepeatingBurst: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Handler handlerCheckHandler = CameraDeviceImpl.checkHandler(handler, captureCallback);
            Log.v(TAG, this.mIdString + "setRepeatingBurst - requests " + Arrays.toString((CaptureRequest[]) list.toArray(new CaptureRequest[0])) + ", callback " + captureCallback + " handler" + handlerCheckHandler);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingBurst(list, createCaptureCallbackProxy(handlerCheckHandler, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurstRequests(List<CaptureRequest> list, Executor executor, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException {
        int iAddPendingSequence;
        Log.d(TAG, "setRepeatingBurstRequests: ");
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (captureCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkRepeatingRequests(list);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Executor executorCheckExecutor = CameraDeviceImpl.checkExecutor(executor, captureCallback);
            Log.v(TAG, this.mIdString + "setRepeatingBurst - requests " + Arrays.toString((CaptureRequest[]) list.toArray(new CaptureRequest[0])) + ", callback " + captureCallback + " executor" + executorCheckExecutor);
            iAddPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingBurst(list, createCaptureCallbackProxyWithExecutor(executorCheckExecutor, captureCallback), this.mDeviceExecutor));
        }
        return iAddPendingSequence;
    }

    private void checkRepeatingRequests(List<CaptureRequest> list) {
        if (list == null) {
            throw new IllegalArgumentException("requests must not be null");
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("requests must have at least one element");
        }
        Iterator<CaptureRequest> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().isReprocess()) {
                throw new IllegalArgumentException("repeating reprocess burst requests are not supported");
            }
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void stopRepeating() throws CameraAccessException {
        Log.d(TAG, "stopRepeating: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Log.v(TAG, this.mIdString + "stopRepeating");
            this.mDeviceImpl.stopRepeating();
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void abortCaptures() throws CameraAccessException {
        Log.d(TAG, "abortCaptures: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Log.v(TAG, this.mIdString + "abortCaptures");
            if (this.mAborting) {
                Log.w(TAG, this.mIdString + "abortCaptures - Session is already aborting; doing nothing");
                return;
            }
            this.mAborting = true;
            this.mAbortDrainer.taskStarted();
            this.mDeviceImpl.flush();
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void updateOutputConfiguration(OutputConfiguration outputConfiguration) throws CameraAccessException {
        Log.d(TAG, "updateOutputConfiguration: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            Log.v(TAG, this.mIdString + "updateOutputConfiguration");
            this.mDeviceImpl.updateOutputConfiguration(outputConfiguration);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean isReprocessable() {
        return this.mInput != null;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public Surface getInputSurface() {
        return this.mInput;
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public void replaceSessionClose() {
        Log.d(TAG, "replaceSessionClose: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            Log.v(TAG, this.mIdString + "replaceSessionClose");
            this.mSkipUnconfigure = true;
            close();
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession, java.lang.AutoCloseable
    public void close() {
        Log.d(TAG, "close: ");
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            if (this.mClosed) {
                Log.v(TAG, this.mIdString + "close - reentering");
                return;
            }
            Log.v(TAG, this.mIdString + "close - first time");
            this.mClosed = true;
            try {
                this.mDeviceImpl.stopRepeating();
            } catch (CameraAccessException e) {
                Log.e(TAG, this.mIdString + "Exception while stopping repeating: ", e);
            } catch (IllegalStateException unused) {
                this.mStateCallback.onClosed(this);
                return;
            }
            this.mSequenceDrainer.beginDrain();
            Surface surface = this.mInput;
            if (surface != null) {
                surface.release();
            }
        }
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public boolean isAborting() {
        return this.mAborting;
    }

    private CameraCaptureSession.StateCallback createUserStateCallbackProxy(Executor executor, CameraCaptureSession.StateCallback stateCallback) {
        return new CallbackProxies.SessionStateCallbackProxy(executor, stateCallback);
    }

    private CameraDeviceImpl.CaptureCallback createCaptureCallbackProxy(Handler handler, CameraCaptureSession.CaptureCallback captureCallback) {
        return createCaptureCallbackProxyWithExecutor(captureCallback != null ? CameraDeviceImpl.checkAndWrapHandler(handler) : null, captureCallback);
    }

    /* JADX INFO: renamed from: android.hardware.camera2.impl.CameraCaptureSessionImpl$1, reason: invalid class name */
    class AnonymousClass1 implements CameraDeviceImpl.CaptureCallback {
        final /* synthetic */ CameraCaptureSession.CaptureCallback val$callback;
        final /* synthetic */ Executor val$executor;

        AnonymousClass1(CameraCaptureSession.CaptureCallback captureCallback, Executor executor) {
            this.val$callback = captureCallback;
            this.val$executor = executor;
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureStarted(CameraDevice cameraDevice, final CaptureRequest captureRequest, final long j, final long j2) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$uPVvNnGFdZcxxscdYQ5erNgaRWA
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCaptureStarted$0$CameraCaptureSessionImpl$1(captureCallback, captureRequest, j, j2);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCaptureStarted$0$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, long j, long j2) {
            captureCallback.onCaptureStarted(CameraCaptureSessionImpl.this, captureRequest, j, j2);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCapturePartial(CameraDevice cameraDevice, final CaptureRequest captureRequest, final CaptureResult captureResult) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$HRzGZkXU2X5JDcudK0jcqdLZzV8
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCapturePartial$1$CameraCaptureSessionImpl$1(captureCallback, captureRequest, captureResult);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCapturePartial$1$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, CaptureResult captureResult) {
            captureCallback.onCapturePartial(CameraCaptureSessionImpl.this, captureRequest, captureResult);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureProgressed(CameraDevice cameraDevice, final CaptureRequest captureRequest, final CaptureResult captureResult) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$7mSdNTTAoYA0D3ITDxzDJKGykz0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCaptureProgressed$2$CameraCaptureSessionImpl$1(captureCallback, captureRequest, captureResult);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCaptureProgressed$2$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, CaptureResult captureResult) {
            captureCallback.onCaptureProgressed(CameraCaptureSessionImpl.this, captureRequest, captureResult);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureCompleted(CameraDevice cameraDevice, final CaptureRequest captureRequest, final TotalCaptureResult totalCaptureResult) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$OA1Yz_YgzMO8qcV8esRjyt7ykp4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCaptureCompleted$3$CameraCaptureSessionImpl$1(captureCallback, captureRequest, totalCaptureResult);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCaptureCompleted$3$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
            captureCallback.onCaptureCompleted(CameraCaptureSessionImpl.this, captureRequest, totalCaptureResult);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureFailed(CameraDevice cameraDevice, final CaptureRequest captureRequest, final CaptureFailure captureFailure) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$VsKq1alEqL3XH-hLTWXgi7fSF3s
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCaptureFailed$4$CameraCaptureSessionImpl$1(captureCallback, captureRequest, captureFailure);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCaptureFailed$4$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, CaptureFailure captureFailure) {
            captureCallback.onCaptureFailed(CameraCaptureSessionImpl.this, captureRequest, captureFailure);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureSequenceCompleted(CameraDevice cameraDevice, final int i, final long j) {
            if (this.val$callback != null && this.val$executor != null) {
                long jClearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$KZ4tthx5TnA5BizPVljsPqqdHck
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onCaptureSequenceCompleted$5$CameraCaptureSessionImpl$1(captureCallback, i, j);
                        }
                    });
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    throw th;
                }
            }
            CameraCaptureSessionImpl.this.finishPendingSequence(i);
        }

        public /* synthetic */ void lambda$onCaptureSequenceCompleted$5$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, int i, long j) {
            captureCallback.onCaptureSequenceCompleted(CameraCaptureSessionImpl.this, i, j);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureSequenceAborted(CameraDevice cameraDevice, final int i) {
            if (this.val$callback != null && this.val$executor != null) {
                long jClearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$TIJELOXvjSbPh6mpBLfBJ5ciNic
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onCaptureSequenceAborted$6$CameraCaptureSessionImpl$1(captureCallback, i);
                        }
                    });
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                    throw th;
                }
            }
            CameraCaptureSessionImpl.this.finishPendingSequence(i);
        }

        public /* synthetic */ void lambda$onCaptureSequenceAborted$6$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, int i) {
            captureCallback.onCaptureSequenceAborted(CameraCaptureSessionImpl.this, i);
        }

        @Override // android.hardware.camera2.impl.CameraDeviceImpl.CaptureCallback
        public void onCaptureBufferLost(CameraDevice cameraDevice, final CaptureRequest captureRequest, final Surface surface, final long j) {
            if (this.val$callback == null || this.val$executor == null) {
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CameraCaptureSessionImpl$1$VuYVXvwmJMkbTnKaOD-h-DOjJpE
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onCaptureBufferLost$7$CameraCaptureSessionImpl$1(captureCallback, captureRequest, surface, j);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public /* synthetic */ void lambda$onCaptureBufferLost$7$CameraCaptureSessionImpl$1(CameraCaptureSession.CaptureCallback captureCallback, CaptureRequest captureRequest, Surface surface, long j) {
            captureCallback.onCaptureBufferLost(CameraCaptureSessionImpl.this, captureRequest, surface, j);
        }
    }

    private CameraDeviceImpl.CaptureCallback createCaptureCallbackProxyWithExecutor(Executor executor, CameraCaptureSession.CaptureCallback captureCallback) {
        return new AnonymousClass1(captureCallback, executor);
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public CameraDeviceImpl.StateCallbackKK getDeviceStateCallback() {
        final Object obj = this.mDeviceImpl.mInterfaceLock;
        return new CameraDeviceImpl.StateCallbackKK() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl.2
            private boolean mBusy = false;
            private boolean mActive = false;

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onOpened(CameraDevice cameraDevice) {
                throw new AssertionError("Camera must already be open before creating a session");
            }

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onDisconnected(CameraDevice cameraDevice) {
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onDisconnected");
                CameraCaptureSessionImpl.this.close();
            }

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onError(CameraDevice cameraDevice, int i) {
                Log.wtf(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Got device error " + i);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onActive(CameraDevice cameraDevice) {
                CameraCaptureSessionImpl.this.mIdleDrainer.taskStarted();
                this.mActive = true;
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onActive");
                CameraCaptureSessionImpl.this.mStateCallback.onActive(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onIdle(CameraDevice cameraDevice) {
                boolean z;
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onIdle");
                synchronized (obj) {
                    z = CameraCaptureSessionImpl.this.mAborting;
                }
                if (this.mBusy && z) {
                    CameraCaptureSessionImpl.this.mAbortDrainer.taskFinished();
                    synchronized (obj) {
                        CameraCaptureSessionImpl.this.mAborting = false;
                    }
                }
                if (this.mActive) {
                    CameraCaptureSessionImpl.this.mIdleDrainer.taskFinished();
                }
                this.mBusy = false;
                this.mActive = false;
                CameraCaptureSessionImpl.this.mStateCallback.onReady(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onBusy(CameraDevice cameraDevice) {
                this.mBusy = true;
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onBusy");
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onUnconfigured(CameraDevice cameraDevice) {
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onUnconfigured");
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onRequestQueueEmpty() {
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onRequestQueueEmpty");
                CameraCaptureSessionImpl.this.mStateCallback.onCaptureQueueEmpty(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onSurfacePrepared(Surface surface) {
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onSurfacePrepared");
                CameraCaptureSessionImpl.this.mStateCallback.onSurfacePrepared(this, surface);
            }
        };
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void checkNotClosed() {
        if (this.mClosed) {
            throw new IllegalStateException("Session has been closed; further changes are illegal.");
        }
    }

    private int addPendingSequence(int i) {
        this.mSequenceDrainer.taskStarted(Integer.valueOf(i));
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishPendingSequence(int i) {
        try {
            this.mSequenceDrainer.taskFinished(Integer.valueOf(i));
        } catch (IllegalStateException e) {
            Log.w(TAG, e.getMessage());
        }
    }

    private class SequenceDrainListener implements TaskDrainer.DrainListener {
        private SequenceDrainListener() {
        }

        /* synthetic */ SequenceDrainListener(CameraCaptureSessionImpl cameraCaptureSessionImpl, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onSequenceDrained");
            CameraCaptureSessionImpl.this.mStateCallback.onClosed(CameraCaptureSessionImpl.this);
            if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                return;
            }
            CameraCaptureSessionImpl.this.mAbortDrainer.beginDrain();
        }
    }

    private class AbortDrainListener implements TaskDrainer.DrainListener {
        private AbortDrainListener() {
        }

        /* synthetic */ AbortDrainListener(CameraCaptureSessionImpl cameraCaptureSessionImpl, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onAbortDrained");
            synchronized (CameraCaptureSessionImpl.this.mDeviceImpl.mInterfaceLock) {
                if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                    return;
                }
                CameraCaptureSessionImpl.this.mIdleDrainer.beginDrain();
            }
        }
    }

    private class IdleDrainListener implements TaskDrainer.DrainListener {
        private IdleDrainListener() {
        }

        /* synthetic */ IdleDrainListener(CameraCaptureSessionImpl cameraCaptureSessionImpl, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "onIdleDrained");
            synchronized (CameraCaptureSessionImpl.this.mDeviceImpl.mInterfaceLock) {
                Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Session drain complete, skip unconfigure: " + CameraCaptureSessionImpl.this.mSkipUnconfigure);
                if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                    return;
                }
                try {
                    CameraCaptureSessionImpl.this.mDeviceImpl.configureStreamsChecked(null, null, 0, null);
                } catch (CameraAccessException e) {
                    Log.e(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Exception while unconfiguring outputs: ", e);
                } catch (IllegalStateException unused) {
                    Log.v(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Camera was already closed or busy, skipping unconfigure");
                }
            }
        }
    }
}
