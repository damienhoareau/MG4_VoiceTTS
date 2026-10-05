package android.view;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.view.SurfaceCallbackHelper;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes2.dex */
public class SurfaceView extends View implements ViewRootImpl.WindowStoppedCallback {
    private static final boolean DEBUG = false;
    private static final String TAG = "SurfaceView";
    private boolean mAttachedToWindow;
    final ArrayList<SurfaceHolder.Callback> mCallbacks;
    final Configuration mConfiguration;
    SurfaceControl mDeferredDestroySurfaceControl;
    boolean mDrawFinished;
    private final ViewTreeObserver.OnPreDrawListener mDrawListener;
    boolean mDrawingStopped;
    int mFormat;
    private boolean mGlobalListenersAdded;
    boolean mHaveFrame;
    boolean mIsCreating;
    long mLastLockTime;
    int mLastSurfaceHeight;
    int mLastSurfaceWidth;
    boolean mLastWindowVisibility;
    final int[] mLocation;
    private int mPendingReportDraws;
    private Rect mRTLastReportedPosition;
    int mRequestedFormat;
    int mRequestedHeight;
    boolean mRequestedVisible;
    int mRequestedWidth;
    private volatile boolean mRtHandlingPositionUpdates;
    private SurfaceControl.Transaction mRtTransaction;
    final Rect mScreenRect;
    private final ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    int mSubLayer;
    final Surface mSurface;
    SurfaceControlWithBackground mSurfaceControl;
    boolean mSurfaceCreated;
    private int mSurfaceFlags;
    final Rect mSurfaceFrame;
    int mSurfaceHeight;
    private final SurfaceHolder mSurfaceHolder;
    final ReentrantLock mSurfaceLock;
    SurfaceSession mSurfaceSession;
    int mSurfaceWidth;
    final Rect mTmpRect;
    private CompatibilityInfo.Translator mTranslator;
    boolean mViewVisibility;
    boolean mVisible;
    int mWindowSpaceLeft;
    int mWindowSpaceTop;
    boolean mWindowStopped;
    boolean mWindowVisibility;

    protected void applyChildSurfaceTransaction_renderWorker(SurfaceControl.Transaction transaction, Surface surface, long j) {
    }

    public SurfaceView(Context context) {
        this(context, null);
    }

    public SurfaceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SurfaceView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SurfaceView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCallbacks = new ArrayList<>();
        this.mLocation = new int[2];
        this.mSurfaceLock = new ReentrantLock();
        this.mSurface = new Surface();
        this.mDrawingStopped = true;
        this.mDrawFinished = false;
        this.mScreenRect = new Rect();
        this.mTmpRect = new Rect();
        this.mConfiguration = new Configuration();
        this.mSubLayer = -2;
        this.mIsCreating = false;
        this.mRtHandlingPositionUpdates = false;
        this.mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.view.SurfaceView.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                SurfaceView.this.updateSurface();
            }
        };
        this.mDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.SurfaceView.2
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mHaveFrame = surfaceView.getWidth() > 0 && SurfaceView.this.getHeight() > 0;
                SurfaceView.this.updateSurface();
                return true;
            }
        };
        this.mRequestedVisible = false;
        this.mWindowVisibility = false;
        this.mLastWindowVisibility = false;
        this.mViewVisibility = false;
        this.mWindowStopped = false;
        this.mRequestedWidth = -1;
        this.mRequestedHeight = -1;
        this.mRequestedFormat = 4;
        this.mHaveFrame = false;
        this.mSurfaceCreated = false;
        this.mLastLockTime = 0L;
        this.mVisible = false;
        this.mWindowSpaceLeft = -1;
        this.mWindowSpaceTop = -1;
        this.mSurfaceWidth = -1;
        this.mSurfaceHeight = -1;
        this.mFormat = -1;
        this.mSurfaceFrame = new Rect();
        this.mLastSurfaceWidth = -1;
        this.mLastSurfaceHeight = -1;
        this.mSurfaceFlags = 4;
        this.mRtTransaction = new SurfaceControl.Transaction();
        this.mRTLastReportedPosition = new Rect();
        this.mSurfaceHolder = new AnonymousClass3();
        this.mRenderNode.requestPositionUpdates(this);
        setWillNotDraw(true);
    }

    public SurfaceHolder getHolder() {
        return this.mSurfaceHolder;
    }

    private void updateRequestedVisibility() {
        this.mRequestedVisible = this.mViewVisibility && this.mWindowVisibility && !this.mWindowStopped;
    }

    @Override // android.view.ViewRootImpl.WindowStoppedCallback
    public void windowStopped(boolean z) {
        this.mWindowStopped = z;
        updateRequestedVisibility();
        updateSurface();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewRootImpl().addWindowStoppedCallback(this);
        this.mWindowStopped = false;
        this.mViewVisibility = getVisibility() == 0;
        updateRequestedVisibility();
        this.mAttachedToWindow = true;
        this.mParent.requestTransparentRegion(this);
        if (this.mGlobalListenersAdded) {
            return;
        }
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnScrollChangedListener(this.mScrollChangedListener);
        viewTreeObserver.addOnPreDrawListener(this.mDrawListener);
        this.mGlobalListenersAdded = true;
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mWindowVisibility = i == 0;
        updateRequestedVisibility();
        updateSurface();
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        boolean z = i == 0;
        this.mViewVisibility = z;
        boolean z2 = this.mWindowVisibility && z && !this.mWindowStopped;
        if (z2 != this.mRequestedVisible) {
            requestLayout();
        }
        this.mRequestedVisible = z2;
        updateSurface();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: performDrawFinished, reason: merged with bridge method [inline-methods] */
    public void lambda$onDrawFinished$0$SurfaceView() {
        if (this.mPendingReportDraws > 0) {
            this.mDrawFinished = true;
            if (this.mAttachedToWindow) {
                notifyDrawFinished();
                invalidate();
                return;
            }
            return;
        }
        Log.e(TAG, System.identityHashCode(this) + "finished drawing but no pending report draw (extra call to draw completion runnable?)");
    }

    void notifyDrawFinished() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.pendingDrawFinished();
        }
        this.mPendingReportDraws--;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.removeWindowStoppedCallback(this);
        }
        this.mAttachedToWindow = false;
        if (this.mGlobalListenersAdded) {
            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
            viewTreeObserver.removeOnScrollChangedListener(this.mScrollChangedListener);
            viewTreeObserver.removeOnPreDrawListener(this.mDrawListener);
            this.mGlobalListenersAdded = false;
        }
        while (this.mPendingReportDraws > 0) {
            notifyDrawFinished();
        }
        this.mRequestedVisible = false;
        updateSurface();
        SurfaceControlWithBackground surfaceControlWithBackground = this.mSurfaceControl;
        if (surfaceControlWithBackground != null) {
            surfaceControlWithBackground.destroy();
        }
        this.mSurfaceControl = null;
        this.mHaveFrame = false;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int defaultSize;
        int defaultSize2;
        int i3 = this.mRequestedWidth;
        if (i3 >= 0) {
            defaultSize = resolveSizeAndState(i3, i, 0);
        } else {
            defaultSize = getDefaultSize(0, i);
        }
        int i4 = this.mRequestedHeight;
        if (i4 >= 0) {
            defaultSize2 = resolveSizeAndState(i4, i2, 0);
        } else {
            defaultSize2 = getDefaultSize(0, i2);
        }
        setMeasuredDimension(defaultSize, defaultSize2);
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        updateSurface();
        return frame;
    }

    @Override // android.view.View
    public boolean gatherTransparentRegion(Region region) {
        if (isAboveParent() || !this.mDrawFinished) {
            return super.gatherTransparentRegion(region);
        }
        boolean zGatherTransparentRegion = true;
        if ((this.mPrivateFlags & 128) == 0) {
            zGatherTransparentRegion = super.gatherTransparentRegion(region);
        } else if (region != null) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                getLocationInWindow(this.mLocation);
                int[] iArr = this.mLocation;
                int i = iArr[0];
                int i2 = iArr[1];
                region.op(i, i2, i + width, i2 + height, Region.Op.UNION);
            }
        }
        if (PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
            return false;
        }
        return zGatherTransparentRegion;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mDrawFinished && !isAboveParent() && (this.mPrivateFlags & 128) == 0) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        super.draw(canvas);
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mDrawFinished && !isAboveParent() && (this.mPrivateFlags & 128) == 128) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        super.dispatchDraw(canvas);
    }

    public void setZOrderMediaOverlay(boolean z) {
        this.mSubLayer = z ? -1 : -2;
    }

    public void setZOrderOnTop(boolean z) {
        if (z) {
            this.mSubLayer = 1;
        } else {
            this.mSubLayer = -2;
        }
    }

    public void setSecure(boolean z) {
        if (z) {
            this.mSurfaceFlags |= 128;
        } else {
            this.mSurfaceFlags &= -129;
        }
    }

    private void updateOpaqueFlag() {
        if (!PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
            this.mSurfaceFlags |= 1024;
        } else {
            this.mSurfaceFlags &= -1025;
        }
    }

    private Rect getParentSurfaceInsets() {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null) {
            return null;
        }
        return viewRootImpl.mWindowAttributes.surfaceInsets;
    }

    /* JADX WARN: Code duplicated, block: B:142:0x0288  */
    /* JADX WARN: Code duplicated, block: B:157:0x02c7  */
    protected void updateSurface() {
        ViewRootImpl viewRootImpl;
        boolean z;
        SurfaceHolder.Callback[] surfaceCallbacks;
        if (!this.mHaveFrame || (viewRootImpl = getViewRootImpl()) == null || viewRootImpl.mSurface == null || !viewRootImpl.mSurface.isValid()) {
            return;
        }
        CompatibilityInfo.Translator translator = viewRootImpl.mTranslator;
        this.mTranslator = translator;
        if (translator != null) {
            this.mSurface.setCompatibilityTranslator(translator);
        }
        int width = this.mRequestedWidth;
        if (width <= 0) {
            width = getWidth();
        }
        int height = this.mRequestedHeight;
        if (height <= 0) {
            height = getHeight();
        }
        boolean z2 = this.mFormat != this.mRequestedFormat;
        boolean z3 = this.mVisible != this.mRequestedVisible;
        boolean z4 = (this.mSurfaceControl == null || z2 || z3) && this.mRequestedVisible;
        boolean z5 = (this.mSurfaceWidth == width && this.mSurfaceHeight == height) ? false : true;
        boolean z6 = this.mWindowVisibility != this.mLastWindowVisibility;
        if (z4 || z2 || z5 || z3 || z6) {
            getLocationInWindow(this.mLocation);
            try {
                boolean z7 = this.mRequestedVisible;
                this.mVisible = z7;
                int i = this.mLocation[0];
                this.mWindowSpaceLeft = i;
                this.mWindowSpaceTop = this.mLocation[1];
                this.mSurfaceWidth = width;
                this.mSurfaceHeight = height;
                this.mFormat = this.mRequestedFormat;
                this.mLastWindowVisibility = this.mWindowVisibility;
                this.mScreenRect.left = i;
                this.mScreenRect.top = this.mWindowSpaceTop;
                this.mScreenRect.right = this.mWindowSpaceLeft + getWidth();
                this.mScreenRect.bottom = this.mWindowSpaceTop + getHeight();
                if (this.mTranslator != null) {
                    this.mTranslator.translateRectInAppWindowToScreen(this.mScreenRect);
                }
                Rect parentSurfaceInsets = getParentSurfaceInsets();
                this.mScreenRect.offset(parentSurfaceInsets.left, parentSurfaceInsets.top);
                try {
                    if (z4) {
                        this.mSurfaceSession = new SurfaceSession(viewRootImpl.mSurface);
                        this.mDeferredDestroySurfaceControl = this.mSurfaceControl;
                        updateOpaqueFlag();
                        this.mSurfaceControl = new SurfaceControlWithBackground("SurfaceView - " + viewRootImpl.getTitle().toString(), (this.mSurfaceFlags & 1024) != 0, new SurfaceControl.Builder(this.mSurfaceSession).setSize(this.mSurfaceWidth, this.mSurfaceHeight).setFormat(this.mFormat).setFlags(this.mSurfaceFlags));
                    } else if (this.mSurfaceControl == null) {
                        return;
                    }
                    this.mSurfaceLock.lock();
                    try {
                        this.mDrawingStopped = !z7;
                        SurfaceControl.openTransaction();
                        try {
                            this.mSurfaceControl.setLayer(this.mSubLayer);
                            if (this.mViewVisibility) {
                                this.mSurfaceControl.show();
                            } else {
                                this.mSurfaceControl.hide();
                            }
                            if (z5 || z4 || !this.mRtHandlingPositionUpdates) {
                                this.mSurfaceControl.setPosition(this.mScreenRect.left, this.mScreenRect.top);
                                this.mSurfaceControl.setMatrix(this.mScreenRect.width() / this.mSurfaceWidth, 0.0f, 0.0f, this.mScreenRect.height() / this.mSurfaceHeight);
                            }
                            if (z5) {
                                this.mSurfaceControl.setSize(this.mSurfaceWidth, this.mSurfaceHeight);
                            }
                            SurfaceControl.closeTransaction();
                            boolean z8 = z5 || z4;
                            this.mSurfaceFrame.left = 0;
                            this.mSurfaceFrame.top = 0;
                            if (this.mTranslator == null) {
                                this.mSurfaceFrame.right = this.mSurfaceWidth;
                                this.mSurfaceFrame.bottom = this.mSurfaceHeight;
                            } else {
                                float f = this.mTranslator.applicationInvertedScale;
                                this.mSurfaceFrame.right = (int) ((this.mSurfaceWidth * f) + 0.5f);
                                this.mSurfaceFrame.bottom = (int) ((this.mSurfaceHeight * f) + 0.5f);
                            }
                            int i2 = this.mSurfaceFrame.right;
                            int i3 = this.mSurfaceFrame.bottom;
                            boolean z9 = (this.mLastSurfaceWidth == i2 && this.mLastSurfaceHeight == i3) ? false : true;
                            this.mLastSurfaceWidth = i2;
                            this.mLastSurfaceHeight = i3;
                            this.mSurfaceLock.unlock();
                            if (z7) {
                                try {
                                    if (this.mDrawFinished) {
                                        z = false;
                                    } else {
                                        z = true;
                                    }
                                } finally {
                                    this.mIsCreating = false;
                                    if (this.mSurfaceControl != null && !this.mSurfaceCreated) {
                                        this.mSurface.release();
                                        this.mSurfaceControl.destroy();
                                        this.mSurfaceControl = null;
                                    }
                                }
                            } else {
                                z = false;
                            }
                            boolean z10 = z8 | z;
                            if (!this.mSurfaceCreated || (!z4 && (z7 || !z3))) {
                                surfaceCallbacks = null;
                            } else {
                                this.mSurfaceCreated = false;
                                if (this.mSurface.isValid()) {
                                    SurfaceHolder.Callback[] surfaceCallbacks2 = getSurfaceCallbacks();
                                    int length = surfaceCallbacks2.length;
                                    int i4 = 0;
                                    while (i4 < length) {
                                        surfaceCallbacks2[i4].surfaceDestroyed(this.mSurfaceHolder);
                                        i4++;
                                        surfaceCallbacks2 = surfaceCallbacks2;
                                    }
                                    SurfaceHolder.Callback[] callbackArr = surfaceCallbacks2;
                                    if (this.mSurface.isValid()) {
                                        this.mSurface.forceScopedDisconnect();
                                    }
                                    surfaceCallbacks = callbackArr;
                                } else {
                                    surfaceCallbacks = null;
                                }
                            }
                            if (z4) {
                                this.mSurface.copyFrom(this.mSurfaceControl);
                            }
                            if (z5 && getContext().getApplicationInfo().targetSdkVersion < 26) {
                                this.mSurface.createFrom(this.mSurfaceControl);
                            }
                            if (z7 && this.mSurface.isValid()) {
                                if (!this.mSurfaceCreated && (z4 || z3)) {
                                    this.mSurfaceCreated = true;
                                    this.mIsCreating = true;
                                    if (surfaceCallbacks == null) {
                                        surfaceCallbacks = getSurfaceCallbacks();
                                    }
                                    for (SurfaceHolder.Callback callback : surfaceCallbacks) {
                                        callback.surfaceCreated(this.mSurfaceHolder);
                                    }
                                }
                                if (z4 || z2 || z5 || z3 || z9) {
                                    if (surfaceCallbacks == null) {
                                        surfaceCallbacks = getSurfaceCallbacks();
                                    }
                                    for (SurfaceHolder.Callback callback2 : surfaceCallbacks) {
                                        callback2.surfaceChanged(this.mSurfaceHolder, this.mFormat, width, height);
                                    }
                                }
                                if (z10) {
                                    if (surfaceCallbacks == null) {
                                        surfaceCallbacks = getSurfaceCallbacks();
                                    }
                                    this.mPendingReportDraws++;
                                    viewRootImpl.drawPending();
                                    new SurfaceCallbackHelper(new Runnable() { // from class: android.view.-$$Lambda$SurfaceView$SyyzxOgxKwZMRgiiTGcRYbOU5JY
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            this.f$0.onDrawFinished();
                                        }
                                    }).dispatchSurfaceRedrawNeededAsync(this.mSurfaceHolder, surfaceCallbacks);
                                }
                            }
                            boolean z11 = false;
                        } catch (Throwable th) {
                            SurfaceControl.closeTransaction();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        this.mSurfaceLock.unlock();
                        throw th2;
                    }
                } catch (Exception e) {
                    e = e;
                    Log.e(TAG, "Exception configuring surface", e);
                }
            } catch (Exception e2) {
                e = e2;
            }
        } else {
            getLocationInSurface(this.mLocation);
            int i5 = this.mWindowSpaceLeft;
            int[] iArr = this.mLocation;
            boolean z12 = (i5 == iArr[0] && this.mWindowSpaceTop == iArr[1]) ? false : true;
            boolean z13 = (getWidth() == this.mScreenRect.width() && getHeight() == this.mScreenRect.height()) ? false : true;
            if (z12 || z13) {
                int[] iArr2 = this.mLocation;
                this.mWindowSpaceLeft = iArr2[0];
                this.mWindowSpaceTop = iArr2[1];
                iArr2[0] = getWidth();
                this.mLocation[1] = getHeight();
                Rect rect = this.mScreenRect;
                int i6 = this.mWindowSpaceLeft;
                int i7 = this.mWindowSpaceTop;
                int[] iArr3 = this.mLocation;
                rect.set(i6, i7, iArr3[0] + i6, iArr3[1] + i7);
                CompatibilityInfo.Translator translator2 = this.mTranslator;
                if (translator2 != null) {
                    translator2.translateRectInAppWindowToScreen(this.mScreenRect);
                }
                if (this.mSurfaceControl == null) {
                    return;
                }
                if (isHardwareAccelerated() && this.mRtHandlingPositionUpdates) {
                    return;
                }
                try {
                    setParentSpaceRectangle(this.mScreenRect, -1L);
                } catch (Exception e3) {
                    Log.e(TAG, "Exception configuring surface", e3);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDrawFinished() {
        SurfaceControl surfaceControl = this.mDeferredDestroySurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.destroy();
            this.mDeferredDestroySurfaceControl = null;
        }
        runOnUiThread(new Runnable() { // from class: android.view.-$$Lambda$SurfaceView$Cs7TGTdA1lXf9qW8VOJAfEsMjdk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDrawFinished$0$SurfaceView();
            }
        });
    }

    private void applySurfaceTransforms(SurfaceControl surfaceControl, Rect rect, long j) {
        if (j > 0) {
            this.mRtTransaction.deferTransactionUntilSurface(surfaceControl, getViewRootImpl().mSurface, j);
        }
        this.mRtTransaction.setPosition(surfaceControl, rect.left, rect.top);
        this.mRtTransaction.setMatrix(surfaceControl, rect.width() / this.mSurfaceWidth, 0.0f, 0.0f, rect.height() / this.mSurfaceHeight);
    }

    private void setParentSpaceRectangle(Rect rect, long j) {
        ViewRootImpl viewRootImpl = getViewRootImpl();
        applySurfaceTransforms(this.mSurfaceControl, rect, j);
        applySurfaceTransforms(this.mSurfaceControl.mBackgroundControl, rect, j);
        applyChildSurfaceTransaction_renderWorker(this.mRtTransaction, viewRootImpl.mSurface, j);
        this.mRtTransaction.apply();
    }

    public final void updateSurfacePosition_renderWorker(long j, int i, int i2, int i3, int i4) {
        if (this.mSurfaceControl == null) {
            return;
        }
        this.mRtHandlingPositionUpdates = true;
        if (this.mRTLastReportedPosition.left == i && this.mRTLastReportedPosition.top == i2 && this.mRTLastReportedPosition.right == i3 && this.mRTLastReportedPosition.bottom == i4) {
            return;
        }
        try {
            this.mRTLastReportedPosition.set(i, i2, i3, i4);
            setParentSpaceRectangle(this.mRTLastReportedPosition, j);
        } catch (Exception e) {
            Log.e(TAG, "Exception from repositionChild", e);
        }
    }

    public final void surfacePositionLost_uiRtSync(long j) {
        this.mRTLastReportedPosition.setEmpty();
        if (this.mSurfaceControl != null && this.mRtHandlingPositionUpdates) {
            this.mRtHandlingPositionUpdates = false;
            if (this.mScreenRect.isEmpty() || this.mScreenRect.equals(this.mRTLastReportedPosition)) {
                return;
            }
            try {
                setParentSpaceRectangle(this.mScreenRect, j);
            } catch (Exception e) {
                Log.e(TAG, "Exception configuring surface", e);
            }
        }
    }

    private SurfaceHolder.Callback[] getSurfaceCallbacks() {
        SurfaceHolder.Callback[] callbackArr;
        synchronized (this.mCallbacks) {
            callbackArr = new SurfaceHolder.Callback[this.mCallbacks.size()];
            this.mCallbacks.toArray(callbackArr);
        }
        return callbackArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runOnUiThread(Runnable runnable) {
        Handler handler = getHandler();
        if (handler != null && handler.getLooper() != Looper.myLooper()) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public boolean isFixedSize() {
        return (this.mRequestedWidth == -1 && this.mRequestedHeight == -1) ? false : true;
    }

    private boolean isAboveParent() {
        return this.mSubLayer >= 0;
    }

    public void setResizeBackgroundColor(int i) {
        this.mSurfaceControl.setBackgroundColor(i);
    }

    /* JADX INFO: renamed from: android.view.SurfaceView$3, reason: invalid class name */
    class AnonymousClass3 implements SurfaceHolder {
        private static final String LOG_TAG = "SurfaceHolder";

        @Override // android.view.SurfaceHolder
        @Deprecated
        public void setType(int i) {
        }

        AnonymousClass3() {
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return SurfaceView.this.mIsCreating;
        }

        @Override // android.view.SurfaceHolder
        public void addCallback(SurfaceHolder.Callback callback) {
            synchronized (SurfaceView.this.mCallbacks) {
                if (!SurfaceView.this.mCallbacks.contains(callback)) {
                    SurfaceView.this.mCallbacks.add(callback);
                }
            }
        }

        @Override // android.view.SurfaceHolder
        public void removeCallback(SurfaceHolder.Callback callback) {
            synchronized (SurfaceView.this.mCallbacks) {
                SurfaceView.this.mCallbacks.remove(callback);
            }
        }

        @Override // android.view.SurfaceHolder
        public void setFixedSize(int i, int i2) {
            if (SurfaceView.this.mRequestedWidth == i && SurfaceView.this.mRequestedHeight == i2) {
                return;
            }
            SurfaceView.this.mRequestedWidth = i;
            SurfaceView.this.mRequestedHeight = i2;
            SurfaceView.this.requestLayout();
        }

        @Override // android.view.SurfaceHolder
        public void setSizeFromLayout() {
            if (SurfaceView.this.mRequestedWidth == -1 && SurfaceView.this.mRequestedHeight == -1) {
                return;
            }
            SurfaceView surfaceView = SurfaceView.this;
            surfaceView.mRequestedHeight = -1;
            surfaceView.mRequestedWidth = -1;
            SurfaceView.this.requestLayout();
        }

        @Override // android.view.SurfaceHolder
        public void setFormat(int i) {
            if (i == -1) {
                i = 4;
            }
            SurfaceView.this.mRequestedFormat = i;
            if (SurfaceView.this.mSurfaceControl != null) {
                SurfaceView.this.updateSurface();
            }
        }

        public /* synthetic */ void lambda$setKeepScreenOn$0$SurfaceView$3(boolean z) {
            SurfaceView.this.setKeepScreenOn(z);
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(final boolean z) {
            SurfaceView.this.runOnUiThread(new Runnable() { // from class: android.view.-$$Lambda$SurfaceView$3$XvaZSTTyv1kHN4GtX5NDdmQTRp8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setKeepScreenOn$0$SurfaceView$3(z);
                }
            });
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas() {
            return internalLockCanvas(null, false);
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas(Rect rect) {
            return internalLockCanvas(rect, false);
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockHardwareCanvas() {
            return internalLockCanvas(null, true);
        }

        private Canvas internalLockCanvas(Rect rect, boolean z) {
            Canvas canvasLockCanvas;
            SurfaceView.this.mSurfaceLock.lock();
            if (SurfaceView.this.mDrawingStopped || SurfaceView.this.mSurfaceControl == null) {
                canvasLockCanvas = null;
            } else {
                try {
                    if (z) {
                        canvasLockCanvas = SurfaceView.this.mSurface.lockHardwareCanvas();
                    } else {
                        canvasLockCanvas = SurfaceView.this.mSurface.lockCanvas(rect);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exception locking surface", e);
                    canvasLockCanvas = null;
                }
            }
            if (canvasLockCanvas != null) {
                SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
                return canvasLockCanvas;
            }
            long jUptimeMillis = SystemClock.uptimeMillis();
            long j = SurfaceView.this.mLastLockTime + 100;
            if (j > jUptimeMillis) {
                try {
                    Thread.sleep(j - jUptimeMillis);
                } catch (InterruptedException unused) {
                }
                jUptimeMillis = SystemClock.uptimeMillis();
            }
            SurfaceView.this.mLastLockTime = jUptimeMillis;
            SurfaceView.this.mSurfaceLock.unlock();
            return null;
        }

        @Override // android.view.SurfaceHolder
        public void unlockCanvasAndPost(Canvas canvas) {
            SurfaceView.this.mSurface.unlockCanvasAndPost(canvas);
            SurfaceView.this.mSurfaceLock.unlock();
        }

        @Override // android.view.SurfaceHolder
        public Surface getSurface() {
            return SurfaceView.this.mSurface;
        }

        @Override // android.view.SurfaceHolder
        public Rect getSurfaceFrame() {
            return SurfaceView.this.mSurfaceFrame;
        }
    }

    class SurfaceControlWithBackground extends SurfaceControl {
        SurfaceControl mBackgroundControl;
        private boolean mOpaque;
        public boolean mVisible;

        public SurfaceControlWithBackground(String str, boolean z, SurfaceControl.Builder builder) throws Exception {
            super(builder.setName(str).build());
            this.mOpaque = true;
            this.mVisible = false;
            this.mBackgroundControl = builder.setName("Background for -" + str).setFormat(1024).setColorLayer(true).build();
            this.mOpaque = z;
        }

        @Override // android.view.SurfaceControl
        public void setAlpha(float f) {
            super.setAlpha(f);
            this.mBackgroundControl.setAlpha(f);
        }

        @Override // android.view.SurfaceControl
        public void setLayer(int i) {
            super.setLayer(i);
            this.mBackgroundControl.setLayer(-3);
        }

        @Override // android.view.SurfaceControl
        public void setPosition(float f, float f2) {
            super.setPosition(f, f2);
            this.mBackgroundControl.setPosition(f, f2);
        }

        @Override // android.view.SurfaceControl
        public void setSize(int i, int i2) {
            super.setSize(i, i2);
            this.mBackgroundControl.setSize(i, i2);
        }

        @Override // android.view.SurfaceControl
        public void setWindowCrop(Rect rect) {
            super.setWindowCrop(rect);
            this.mBackgroundControl.setWindowCrop(rect);
        }

        @Override // android.view.SurfaceControl
        public void setFinalCrop(Rect rect) {
            super.setFinalCrop(rect);
            this.mBackgroundControl.setFinalCrop(rect);
        }

        @Override // android.view.SurfaceControl
        public void setLayerStack(int i) {
            super.setLayerStack(i);
            this.mBackgroundControl.setLayerStack(i);
        }

        @Override // android.view.SurfaceControl
        public void setOpaque(boolean z) {
            super.setOpaque(z);
            this.mOpaque = z;
            updateBackgroundVisibility();
        }

        @Override // android.view.SurfaceControl
        public void setSecure(boolean z) {
            super.setSecure(z);
        }

        @Override // android.view.SurfaceControl
        public void setMatrix(float f, float f2, float f3, float f4) {
            super.setMatrix(f, f2, f3, f4);
            this.mBackgroundControl.setMatrix(f, f2, f3, f4);
        }

        @Override // android.view.SurfaceControl
        public void hide() {
            super.hide();
            this.mVisible = false;
            updateBackgroundVisibility();
        }

        @Override // android.view.SurfaceControl
        public void show() {
            super.show();
            this.mVisible = true;
            updateBackgroundVisibility();
        }

        @Override // android.view.SurfaceControl
        public void destroy() {
            super.destroy();
            this.mBackgroundControl.destroy();
        }

        @Override // android.view.SurfaceControl
        public void release() {
            super.release();
            this.mBackgroundControl.release();
        }

        @Override // android.view.SurfaceControl
        public void setTransparentRegionHint(Region region) {
            super.setTransparentRegionHint(region);
            this.mBackgroundControl.setTransparentRegionHint(region);
        }

        @Override // android.view.SurfaceControl
        public void deferTransactionUntil(IBinder iBinder, long j) {
            super.deferTransactionUntil(iBinder, j);
            this.mBackgroundControl.deferTransactionUntil(iBinder, j);
        }

        @Override // android.view.SurfaceControl
        public void deferTransactionUntil(Surface surface, long j) {
            super.deferTransactionUntil(surface, j);
            this.mBackgroundControl.deferTransactionUntil(surface, j);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setBackgroundColor(int i) {
            float[] fArr = {Color.red(i) / 255.0f, Color.green(i) / 255.0f, Color.blue(i) / 255.0f};
            SurfaceControl.openTransaction();
            try {
                this.mBackgroundControl.setColor(fArr);
            } finally {
                SurfaceControl.closeTransaction();
            }
        }

        void updateBackgroundVisibility() {
            if (this.mOpaque && this.mVisible) {
                this.mBackgroundControl.show();
            } else {
                this.mBackgroundControl.hide();
            }
        }
    }
}
