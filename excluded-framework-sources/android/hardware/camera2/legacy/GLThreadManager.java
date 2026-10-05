package android.hardware.camera2.legacy;

import android.graphics.SurfaceTexture;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Collection;

/* JADX INFO: loaded from: classes.dex */
public class GLThreadManager {
    private static final boolean DEBUG = false;
    private static final int MSG_ALLOW_FRAMES = 5;
    private static final int MSG_CLEANUP = 3;
    private static final int MSG_DROP_FRAMES = 4;
    private static final int MSG_NEW_CONFIGURATION = 1;
    private static final int MSG_NEW_FRAME = 2;
    private final String TAG;
    private CaptureCollector mCaptureCollector;
    private final CameraDeviceState mDeviceState;
    private final RequestHandlerThread mGLHandlerThread;
    private final SurfaceTextureRenderer mTextureRenderer;
    private final RequestThreadManager.FpsCounter mPrevCounter = new RequestThreadManager.FpsCounter("GL Preview Producer");
    private final Handler.Callback mGLHandlerCb = new Handler.Callback() { // from class: android.hardware.camera2.legacy.GLThreadManager.1
        private boolean mCleanup = false;
        private boolean mConfigured = false;
        private boolean mDroppingFrames = false;

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (this.mCleanup) {
                return true;
            }
            try {
                int i = message.what;
                if (i != -1) {
                    if (i == 1) {
                        ConfigureHolder configureHolder = (ConfigureHolder) message.obj;
                        GLThreadManager.this.mTextureRenderer.cleanupEGLContext();
                        GLThreadManager.this.mTextureRenderer.configureSurfaces(configureHolder.surfaces);
                        GLThreadManager.this.mCaptureCollector = (CaptureCollector) Preconditions.checkNotNull(configureHolder.collector);
                        configureHolder.condition.open();
                        this.mConfigured = true;
                    } else if (i != 2) {
                        if (i == 3) {
                            GLThreadManager.this.mTextureRenderer.cleanupEGLContext();
                            this.mCleanup = true;
                            this.mConfigured = false;
                        } else if (i == 4) {
                            this.mDroppingFrames = true;
                        } else if (i != 5) {
                            Log.e(GLThreadManager.this.TAG, "Unhandled message " + message.what + " on GLThread.");
                        } else {
                            this.mDroppingFrames = false;
                        }
                    } else if (this.mDroppingFrames) {
                        Log.w(GLThreadManager.this.TAG, "Ignoring frame.");
                    } else {
                        if (!this.mConfigured) {
                            Log.e(GLThreadManager.this.TAG, "Dropping frame, EGL context not configured!");
                        }
                        GLThreadManager.this.mTextureRenderer.drawIntoSurfaces(GLThreadManager.this.mCaptureCollector);
                    }
                }
            } catch (Exception e) {
                Log.e(GLThreadManager.this.TAG, "Received exception on GL render thread: ", e);
                GLThreadManager.this.mDeviceState.setError(1);
            }
            return true;
        }
    };

    private static class ConfigureHolder {
        public final CaptureCollector collector;
        public final ConditionVariable condition;
        public final Collection<Pair<Surface, Size>> surfaces;

        public ConfigureHolder(ConditionVariable conditionVariable, Collection<Pair<Surface, Size>> collection, CaptureCollector captureCollector) {
            this.condition = conditionVariable;
            this.surfaces = collection;
            this.collector = captureCollector;
        }
    }

    public GLThreadManager(int i, int i2, CameraDeviceState cameraDeviceState) {
        this.mTextureRenderer = new SurfaceTextureRenderer(i2);
        this.TAG = String.format("CameraDeviceGLThread-%d", Integer.valueOf(i));
        this.mGLHandlerThread = new RequestHandlerThread(this.TAG, this.mGLHandlerCb);
        this.mDeviceState = cameraDeviceState;
    }

    public void start() {
        this.mGLHandlerThread.start();
    }

    public void waitUntilStarted() {
        this.mGLHandlerThread.waitUntilStarted();
    }

    public void quit() {
        Handler handler = this.mGLHandlerThread.getHandler();
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(3));
        this.mGLHandlerThread.quitSafely();
        try {
            this.mGLHandlerThread.join();
        } catch (InterruptedException unused) {
            Log.e(this.TAG, String.format("Thread %s (%d) interrupted while quitting.", this.mGLHandlerThread.getName(), Long.valueOf(this.mGLHandlerThread.getId())));
        }
    }

    public void queueNewFrame() {
        Handler handler = this.mGLHandlerThread.getHandler();
        if (!handler.hasMessages(2)) {
            handler.sendMessage(handler.obtainMessage(2));
        } else {
            Log.e(this.TAG, "GLThread dropping frame.  Not consuming frames quickly enough!");
        }
    }

    public void setConfigurationAndWait(Collection<Pair<Surface, Size>> collection, CaptureCollector captureCollector) {
        Preconditions.checkNotNull(captureCollector, "collector must not be null");
        Handler handler = this.mGLHandlerThread.getHandler();
        ConditionVariable conditionVariable = new ConditionVariable(false);
        handler.sendMessage(handler.obtainMessage(1, 0, 0, new ConfigureHolder(conditionVariable, collection, captureCollector)));
        conditionVariable.block();
    }

    public SurfaceTexture getCurrentSurfaceTexture() {
        return this.mTextureRenderer.getSurfaceTexture();
    }

    public void ignoreNewFrames() {
        this.mGLHandlerThread.getHandler().sendEmptyMessage(4);
    }

    public void waitUntilIdle() {
        this.mGLHandlerThread.waitUntilIdle();
    }

    public void allowNewFrames() {
        this.mGLHandlerThread.getHandler().sendEmptyMessage(5);
    }
}
