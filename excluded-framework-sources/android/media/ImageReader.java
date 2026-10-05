package android.media;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.HardwareBuffer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import dalvik.system.VMRuntime;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes.dex */
public class ImageReader implements AutoCloseable {
    private static final int ACQUIRE_MAX_IMAGES = 2;
    private static final int ACQUIRE_NO_BUFS = 1;
    private static final int ACQUIRE_SUCCESS = 0;
    private static final long BUFFER_USAGE_UNKNOWN = 0;
    private int mEstimatedNativeAllocBytes;
    private final int mFormat;
    private final int mHeight;
    private boolean mIsReaderValid;
    private OnImageAvailableListener mListener;
    private ListenerHandler mListenerHandler;
    private final int mMaxImages;
    private long mNativeContext;
    private final int mNumPlanes;
    private final Surface mSurface;
    private final int mWidth;
    private final Object mListenerLock = new Object();
    private final Object mCloseLock = new Object();
    private List<Image> mAcquiredImages = new CopyOnWriteArrayList();

    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader);
    }

    private static native void nativeClassInit();

    private native synchronized void nativeClose();

    private native synchronized int nativeDetachImage(Image image);

    private native synchronized void nativeDiscardFreeBuffers();

    private native synchronized Surface nativeGetSurface();

    private native synchronized int nativeImageSetup(Image image);

    private native synchronized void nativeInit(Object obj, int i, int i2, int i3, int i4, long j);

    private native synchronized void nativeReleaseImage(Image image);

    public static ImageReader newInstance(int i, int i2, int i3, int i4) {
        return new ImageReader(i, i2, i3, i4, 0L);
    }

    public static ImageReader newInstance(int i, int i2, int i3, int i4, long j) {
        if (!isFormatUsageCombinationAllowed(i3, j)) {
            throw new IllegalArgumentException("Format usage combination is not supported: format = " + i3 + ", usage = " + j);
        }
        return new ImageReader(i, i2, i3, i4, j);
    }

    protected ImageReader(int i, int i2, int i3, int i4, long j) {
        this.mIsReaderValid = false;
        this.mWidth = i;
        this.mHeight = i2;
        this.mFormat = i3;
        this.mMaxImages = i4;
        if (i < 1 || i2 < 1) {
            throw new IllegalArgumentException("The image dimensions must be positive");
        }
        if (i4 < 1) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        }
        if (i3 == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        }
        this.mNumPlanes = ImageUtils.getNumPlanesForFormat(i3);
        nativeInit(new WeakReference(this), i, i2, i3, i4, j);
        this.mSurface = nativeGetSurface();
        this.mIsReaderValid = true;
        this.mEstimatedNativeAllocBytes = ImageUtils.getEstimatedNativeAllocBytes(i, i2, i3, 1);
        VMRuntime.getRuntime().registerNativeAllocation(this.mEstimatedNativeAllocBytes);
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getImageFormat() {
        return this.mFormat;
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public Image acquireLatestImage() {
        Image imageAcquireNextImage = acquireNextImage();
        if (imageAcquireNextImage == null) {
            return null;
        }
        while (true) {
            try {
                Image imageAcquireNextImageNoThrowISE = acquireNextImageNoThrowISE();
                if (imageAcquireNextImageNoThrowISE == null) {
                    return imageAcquireNextImage;
                }
                imageAcquireNextImage.close();
                imageAcquireNextImage = imageAcquireNextImageNoThrowISE;
            } catch (Throwable th) {
                if (imageAcquireNextImage != null) {
                    imageAcquireNextImage.close();
                }
                throw th;
            }
        }
    }

    public Image acquireNextImageNoThrowISE() {
        SurfaceImage surfaceImage = new SurfaceImage(this.mFormat);
        if (acquireNextSurfaceImage(surfaceImage) == 0) {
            return surfaceImage;
        }
        return null;
    }

    private int acquireNextSurfaceImage(SurfaceImage surfaceImage) {
        int iNativeImageSetup;
        synchronized (this.mCloseLock) {
            iNativeImageSetup = this.mIsReaderValid ? nativeImageSetup(surfaceImage) : 1;
            if (iNativeImageSetup == 0) {
                surfaceImage.mIsImageValid = true;
            } else if (iNativeImageSetup != 1 && iNativeImageSetup != 2) {
                throw new AssertionError("Unknown nativeImageSetup return code " + iNativeImageSetup);
            }
            if (iNativeImageSetup == 0) {
                this.mAcquiredImages.add(surfaceImage);
            }
        }
        return iNativeImageSetup;
    }

    public Image acquireNextImage() {
        SurfaceImage surfaceImage = new SurfaceImage(this.mFormat);
        int iAcquireNextSurfaceImage = acquireNextSurfaceImage(surfaceImage);
        if (iAcquireNextSurfaceImage == 0) {
            return surfaceImage;
        }
        if (iAcquireNextSurfaceImage == 1) {
            return null;
        }
        if (iAcquireNextSurfaceImage == 2) {
            throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", Integer.valueOf(this.mMaxImages)));
        }
        throw new AssertionError("Unknown nativeImageSetup return code " + iAcquireNextSurfaceImage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImage(Image image) {
        if (!(image instanceof SurfaceImage)) {
            throw new IllegalArgumentException("This image was not produced by an ImageReader");
        }
        SurfaceImage surfaceImage = (SurfaceImage) image;
        if (surfaceImage.mIsImageValid) {
            if (surfaceImage.getReader() != this || !this.mAcquiredImages.contains(image)) {
                throw new IllegalArgumentException("This image was not produced by this ImageReader");
            }
            surfaceImage.clearSurfacePlanes();
            nativeReleaseImage(image);
            surfaceImage.mIsImageValid = false;
            this.mAcquiredImages.remove(image);
        }
    }

    public void setOnImageAvailableListener(OnImageAvailableListener onImageAvailableListener, Handler handler) {
        synchronized (this.mListenerLock) {
            try {
                if (onImageAvailableListener != null) {
                    Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                    if (looper == null) {
                        throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                    }
                    if (this.mListenerHandler == null || this.mListenerHandler.getLooper() != looper) {
                        this.mListenerHandler = new ListenerHandler(looper);
                    }
                    this.mListener = onImageAvailableListener;
                } else {
                    this.mListener = null;
                    this.mListenerHandler = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        setOnImageAvailableListener(null, null);
        Surface surface = this.mSurface;
        if (surface != null) {
            surface.release();
        }
        synchronized (this.mCloseLock) {
            this.mIsReaderValid = false;
            Iterator<Image> it = this.mAcquiredImages.iterator();
            while (it.hasNext()) {
                it.next().close();
            }
            this.mAcquiredImages.clear();
            nativeClose();
            if (this.mEstimatedNativeAllocBytes > 0) {
                VMRuntime.getRuntime().registerNativeFree(this.mEstimatedNativeAllocBytes);
                this.mEstimatedNativeAllocBytes = 0;
            }
        }
    }

    public void discardFreeBuffers() {
        synchronized (this.mCloseLock) {
            nativeDiscardFreeBuffers();
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    void detachImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("input image must not be null");
        }
        if (!isImageOwnedbyMe(image)) {
            throw new IllegalArgumentException("Trying to detach an image that is not owned by this ImageReader");
        }
        SurfaceImage surfaceImage = (SurfaceImage) image;
        surfaceImage.throwISEIfImageIsInvalid();
        if (surfaceImage.isAttachable()) {
            throw new IllegalStateException("Image was already detached from this ImageReader");
        }
        nativeDetachImage(image);
        surfaceImage.clearSurfacePlanes();
        surfaceImage.mPlanes = null;
        surfaceImage.setDetached(true);
    }

    private boolean isImageOwnedbyMe(Image image) {
        return (image instanceof SurfaceImage) && ((SurfaceImage) image).getReader() == this;
    }

    private static boolean isFormatUsageCombinationAllowed(int i, long j) {
        return (ImageFormat.isPublicFormat(i) || PixelFormat.isPublicFormat(i)) && j != 0;
    }

    private static void postEventFromNative(Object obj) {
        ListenerHandler listenerHandler;
        ImageReader imageReader = (ImageReader) ((WeakReference) obj).get();
        if (imageReader == null) {
            return;
        }
        synchronized (imageReader.mListenerLock) {
            listenerHandler = imageReader.mListenerHandler;
        }
        if (listenerHandler != null) {
            listenerHandler.sendEmptyMessage(0);
        }
    }

    private final class ListenerHandler extends Handler {
        public ListenerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            OnImageAvailableListener onImageAvailableListener;
            boolean z;
            synchronized (ImageReader.this.mListenerLock) {
                onImageAvailableListener = ImageReader.this.mListener;
            }
            synchronized (ImageReader.this.mCloseLock) {
                z = ImageReader.this.mIsReaderValid;
            }
            if (onImageAvailableListener == null || !z) {
                return;
            }
            onImageAvailableListener.onImageAvailable(ImageReader.this);
        }
    }

    private class SurfaceImage extends Image {
        private int mFormat;
        private AtomicBoolean mIsDetached = new AtomicBoolean(false);
        private long mNativeBuffer;
        private SurfacePlane[] mPlanes;
        private int mScalingMode;
        private long mTimestamp;
        private int mTransform;

        private native synchronized SurfacePlane[] nativeCreatePlanes(int i, int i2);

        private native synchronized int nativeGetFormat(int i);

        private native synchronized HardwareBuffer nativeGetHardwareBuffer();

        private native synchronized int nativeGetHeight();

        private native synchronized int nativeGetWidth();

        public SurfaceImage(int i) {
            this.mFormat = 0;
            this.mFormat = i;
        }

        @Override // android.media.Image, java.lang.AutoCloseable
        public void close() {
            ImageReader.this.releaseImage(this);
        }

        public ImageReader getReader() {
            return ImageReader.this;
        }

        @Override // android.media.Image
        public int getFormat() {
            throwISEIfImageIsInvalid();
            int imageFormat = ImageReader.this.getImageFormat();
            if (imageFormat != 34) {
                imageFormat = nativeGetFormat(imageFormat);
            }
            this.mFormat = imageFormat;
            return imageFormat;
        }

        @Override // android.media.Image
        public int getWidth() {
            throwISEIfImageIsInvalid();
            int format = getFormat();
            if (format == 36 || format == 256 || format == 257) {
                return ImageReader.this.getWidth();
            }
            return nativeGetWidth();
        }

        @Override // android.media.Image
        public int getHeight() {
            throwISEIfImageIsInvalid();
            int format = getFormat();
            if (format == 36 || format == 256 || format == 257) {
                return ImageReader.this.getHeight();
            }
            return nativeGetHeight();
        }

        @Override // android.media.Image
        public long getTimestamp() {
            throwISEIfImageIsInvalid();
            return this.mTimestamp;
        }

        @Override // android.media.Image
        public int getTransform() {
            throwISEIfImageIsInvalid();
            return this.mTransform;
        }

        @Override // android.media.Image
        public int getScalingMode() {
            throwISEIfImageIsInvalid();
            return this.mScalingMode;
        }

        @Override // android.media.Image
        public HardwareBuffer getHardwareBuffer() {
            throwISEIfImageIsInvalid();
            return nativeGetHardwareBuffer();
        }

        @Override // android.media.Image
        public void setTimestamp(long j) {
            throwISEIfImageIsInvalid();
            this.mTimestamp = j;
        }

        @Override // android.media.Image
        public Image.Plane[] getPlanes() {
            throwISEIfImageIsInvalid();
            if (this.mPlanes == null) {
                this.mPlanes = nativeCreatePlanes(ImageReader.this.mNumPlanes, ImageReader.this.mFormat);
            }
            return (Image.Plane[]) this.mPlanes.clone();
        }

        protected final void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }

        @Override // android.media.Image
        boolean isAttachable() {
            throwISEIfImageIsInvalid();
            return this.mIsDetached.get();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.Image
        public ImageReader getOwner() {
            throwISEIfImageIsInvalid();
            return ImageReader.this;
        }

        @Override // android.media.Image
        long getNativeContext() {
            throwISEIfImageIsInvalid();
            return this.mNativeBuffer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDetached(boolean z) {
            throwISEIfImageIsInvalid();
            this.mIsDetached.getAndSet(z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSurfacePlanes() {
            if (!this.mIsImageValid || this.mPlanes == null) {
                return;
            }
            int i = 0;
            while (true) {
                SurfacePlane[] surfacePlaneArr = this.mPlanes;
                if (i >= surfacePlaneArr.length) {
                    return;
                }
                if (surfacePlaneArr[i] != null) {
                    surfacePlaneArr[i].clearBuffer();
                    this.mPlanes[i] = null;
                }
                i++;
            }
        }

        private class SurfacePlane extends Image.Plane {
            private ByteBuffer mBuffer;
            private final int mPixelStride;
            private final int mRowStride;

            private SurfacePlane(int i, int i2, ByteBuffer byteBuffer) {
                this.mRowStride = i;
                this.mPixelStride = i2;
                this.mBuffer = byteBuffer;
                byteBuffer.order(ByteOrder.nativeOrder());
            }

            @Override // android.media.Image.Plane
            public ByteBuffer getBuffer() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                return this.mBuffer;
            }

            @Override // android.media.Image.Plane
            public int getPixelStride() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                if (ImageReader.this.mFormat == 36) {
                    throw new UnsupportedOperationException("getPixelStride is not supported for RAW_PRIVATE plane");
                }
                return this.mPixelStride;
            }

            @Override // android.media.Image.Plane
            public int getRowStride() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                if (ImageReader.this.mFormat == 36) {
                    throw new UnsupportedOperationException("getRowStride is not supported for RAW_PRIVATE plane");
                }
                return this.mRowStride;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void clearBuffer() {
                ByteBuffer byteBuffer = this.mBuffer;
                if (byteBuffer == null) {
                    return;
                }
                if (byteBuffer.isDirect()) {
                    NioUtils.freeDirectBuffer(this.mBuffer);
                }
                this.mBuffer = null;
            }
        }
    }

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}
