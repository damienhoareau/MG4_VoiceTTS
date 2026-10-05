package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class TextureView extends View {
    private static final String LOG_TAG = "TextureView";
    private Canvas mCanvas;
    private boolean mHadSurface;
    private TextureLayer mLayer;
    private SurfaceTextureListener mListener;
    private final Object[] mLock;
    private final Matrix mMatrix;
    private boolean mMatrixChanged;
    private long mNativeWindow;
    private final Object[] mNativeWindowLock;
    private boolean mOpaque;
    private int mSaveCount;
    private SurfaceTexture mSurface;
    private boolean mUpdateLayer;
    private final SurfaceTexture.OnFrameAvailableListener mUpdateListener;
    private boolean mUpdateSurface;

    public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2);

        boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);
    }

    private native void nCreateNativeWindow(SurfaceTexture surfaceTexture);

    private native void nDestroyNativeWindow();

    private static native boolean nLockCanvas(long j, Canvas canvas, Rect rect);

    private static native void nUnlockCanvasAndPost(long j, Canvas canvas);

    @Override // android.view.View
    public void buildLayer() {
    }

    @Override // android.view.View
    public int getLayerType() {
        return 2;
    }

    @Override // android.view.View
    protected final void onDraw(Canvas canvas) {
    }

    public TextureView(Context context) {
        super(context);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.updateLayer();
                TextureView.this.invalidate();
            }
        };
    }

    public TextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.updateLayer();
                TextureView.this.invalidate();
            }
        };
    }

    public TextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.updateLayer();
                TextureView.this.invalidate();
            }
        };
    }

    public TextureView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.updateLayer();
                TextureView.this.invalidate();
            }
        };
    }

    @Override // android.view.View
    public boolean isOpaque() {
        return this.mOpaque;
    }

    public void setOpaque(boolean z) {
        if (z != this.mOpaque) {
            this.mOpaque = z;
            if (this.mLayer != null) {
                updateLayerAndInvalidate();
            }
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.w(LOG_TAG, "A TextureView or a subclass can only be used with hardware acceleration enabled.");
        }
        if (this.mHadSurface) {
            invalidate(true);
            this.mHadSurface = false;
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindowInternal() {
        destroyHardwareLayer();
        releaseSurfaceTexture();
        super.onDetachedFromWindowInternal();
    }

    @Override // android.view.View
    protected void destroyHardwareResources() {
        super.destroyHardwareResources();
        destroyHardwareLayer();
    }

    private void destroyHardwareLayer() {
        TextureLayer textureLayer = this.mLayer;
        if (textureLayer != null) {
            textureLayer.detachSurfaceTexture();
            this.mLayer.destroy();
            this.mLayer = null;
            this.mMatrixChanged = true;
        }
    }

    private void releaseSurfaceTexture() {
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            boolean zOnSurfaceTextureDestroyed = surfaceTextureListener != null ? surfaceTextureListener.onSurfaceTextureDestroyed(surfaceTexture) : true;
            synchronized (this.mNativeWindowLock) {
                nDestroyNativeWindow();
            }
            if (zOnSurfaceTextureDestroyed) {
                this.mSurface.release();
            }
            this.mSurface = null;
            this.mHadSurface = true;
        }
    }

    @Override // android.view.View
    public void setLayerType(int i, Paint paint) {
        setLayerPaint(paint);
    }

    @Override // android.view.View
    public void setLayerPaint(Paint paint) {
        if (paint != this.mLayerPaint) {
            this.mLayerPaint = paint;
            invalidate();
        }
    }

    @Override // android.view.View
    public void setForeground(Drawable drawable) {
        if (drawable != null && !sTextureViewIgnoresDrawableSetters) {
            throw new UnsupportedOperationException("TextureView doesn't support displaying a foreground drawable");
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        if (drawable != null && !sTextureViewIgnoresDrawableSetters) {
            throw new UnsupportedOperationException("TextureView doesn't support displaying a background drawable");
        }
    }

    @Override // android.view.View
    public final void draw(Canvas canvas) {
        this.mPrivateFlags = (this.mPrivateFlags & (-6291457)) | 32;
        if (canvas.isHardwareAccelerated()) {
            DisplayListCanvas displayListCanvas = (DisplayListCanvas) canvas;
            TextureLayer textureLayer = getTextureLayer();
            if (textureLayer != null) {
                applyUpdate();
                applyTransformMatrix();
                this.mLayer.setLayerPaint(this.mLayerPaint);
                displayListCanvas.drawTextureLayer(textureLayer);
            }
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(getWidth(), getHeight());
            updateLayer();
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            if (surfaceTextureListener != null) {
                surfaceTextureListener.onSurfaceTextureSizeChanged(this.mSurface, getWidth(), getHeight());
            }
        }
    }

    TextureLayer getTextureLayer() {
        if (this.mLayer == null) {
            if (this.mAttachInfo == null || this.mAttachInfo.mThreadedRenderer == null) {
                return null;
            }
            this.mLayer = this.mAttachInfo.mThreadedRenderer.createTextureLayer();
            boolean z = this.mSurface == null;
            if (z) {
                SurfaceTexture surfaceTexture = new SurfaceTexture(false);
                this.mSurface = surfaceTexture;
                nCreateNativeWindow(surfaceTexture);
            }
            this.mLayer.setSurfaceTexture(this.mSurface);
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            if (surfaceTextureListener != null && z) {
                surfaceTextureListener.onSurfaceTextureAvailable(this.mSurface, getWidth(), getHeight());
            }
            this.mLayer.setLayerPaint(this.mLayerPaint);
        }
        if (this.mUpdateSurface) {
            this.mUpdateSurface = false;
            updateLayer();
            this.mMatrixChanged = true;
            this.mLayer.setSurfaceTexture(this.mSurface);
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
        }
        return this.mLayer;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            if (i == 0) {
                if (this.mLayer != null) {
                    surfaceTexture.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
                }
                updateLayerAndInvalidate();
                return;
            }
            surfaceTexture.setOnFrameAvailableListener(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLayer() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
    }

    private void updateLayerAndInvalidate() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
        invalidate();
    }

    private void applyUpdate() {
        if (this.mLayer == null) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mUpdateLayer) {
                this.mUpdateLayer = false;
                this.mLayer.prepare(getWidth(), getHeight(), this.mOpaque);
                this.mLayer.updateSurfaceTexture();
                SurfaceTextureListener surfaceTextureListener = this.mListener;
                if (surfaceTextureListener != null) {
                    surfaceTextureListener.onSurfaceTextureUpdated(this.mSurface);
                }
            }
        }
    }

    public void setTransform(Matrix matrix) {
        this.mMatrix.set(matrix);
        this.mMatrixChanged = true;
        invalidateParentIfNeeded();
    }

    public Matrix getTransform(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.set(this.mMatrix);
        return matrix;
    }

    private void applyTransformMatrix() {
        TextureLayer textureLayer;
        if (!this.mMatrixChanged || (textureLayer = this.mLayer) == null) {
            return;
        }
        textureLayer.setTransform(this.mMatrix);
        this.mMatrixChanged = false;
    }

    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }

    public Bitmap getBitmap(int i, int i2) {
        if (!isAvailable() || i <= 0 || i2 <= 0) {
            return null;
        }
        return getBitmap(Bitmap.createBitmap(getResources().getDisplayMetrics(), i, i2, Bitmap.Config.ARGB_8888));
    }

    public Bitmap getBitmap(Bitmap bitmap) {
        if (bitmap != null && isAvailable()) {
            applyUpdate();
            applyTransformMatrix();
            if (this.mLayer == null && this.mUpdateSurface) {
                getTextureLayer();
            }
            TextureLayer textureLayer = this.mLayer;
            if (textureLayer != null) {
                textureLayer.copyInto(bitmap);
            }
        }
        return bitmap;
    }

    public boolean isAvailable() {
        return this.mSurface != null;
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect rect) {
        if (!isAvailable()) {
            return null;
        }
        if (this.mCanvas == null) {
            this.mCanvas = new Canvas();
        }
        synchronized (this.mNativeWindowLock) {
            if (!nLockCanvas(this.mNativeWindow, this.mCanvas, rect)) {
                return null;
            }
            this.mSaveCount = this.mCanvas.save();
            return this.mCanvas;
        }
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        Canvas canvas2 = this.mCanvas;
        if (canvas2 == null || canvas != canvas2) {
            return;
        }
        canvas.restoreToCount(this.mSaveCount);
        this.mSaveCount = 0;
        synchronized (this.mNativeWindowLock) {
            nUnlockCanvasAndPost(this.mNativeWindow, this.mCanvas);
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurface;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new NullPointerException("surfaceTexture must not be null");
        }
        if (surfaceTexture == this.mSurface) {
            throw new IllegalArgumentException("Trying to setSurfaceTexture to the same SurfaceTexture that's already set.");
        }
        if (surfaceTexture.isReleased()) {
            throw new IllegalArgumentException("Cannot setSurfaceTexture to a released SurfaceTexture");
        }
        if (this.mSurface != null) {
            nDestroyNativeWindow();
            this.mSurface.release();
        }
        this.mSurface = surfaceTexture;
        nCreateNativeWindow(surfaceTexture);
        if ((this.mViewFlags & 12) == 0 && this.mLayer != null) {
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
        }
        this.mUpdateSurface = true;
        invalidateParentIfNeeded();
    }

    public SurfaceTextureListener getSurfaceTextureListener() {
        return this.mListener;
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        this.mListener = surfaceTextureListener;
    }
}
