package android.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.TtmlUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class ImageView extends View {
    private static final String LOG_TAG = "ImageView";
    private static boolean sCompatAdjustViewBounds;
    private static boolean sCompatDone;
    private static boolean sCompatDrawableVisibilityDispatch;
    private static boolean sCompatUseCorrectStreamDensity;
    private boolean mAdjustViewBounds;
    private int mAlpha;
    private int mBaseline;
    private boolean mBaselineAlignBottom;
    private ColorFilter mColorFilter;
    private boolean mColorMod;
    private boolean mCropToPadding;
    private Matrix mDrawMatrix;
    private Drawable mDrawable;
    private int mDrawableHeight;
    private ColorStateList mDrawableTintList;
    private PorterDuff.Mode mDrawableTintMode;
    private int mDrawableWidth;
    private boolean mHasColorFilter;
    private boolean mHasDrawableTint;
    private boolean mHasDrawableTintMode;
    private boolean mHaveFrame;
    private int mLevel;
    private Matrix mMatrix;
    private int mMaxHeight;
    private int mMaxWidth;
    private boolean mMergeState;
    private BitmapDrawable mRecycleableBitmapDrawable;
    private int mResource;
    private ScaleType mScaleType;
    private int[] mState;
    private final RectF mTempDst;
    private final RectF mTempSrc;
    private Uri mUri;
    private final int mViewAlphaScale;
    private Xfermode mXfermode;
    private static final ScaleType[] sScaleTypeArray = {ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE};
    private static final Matrix.ScaleToFit[] sS2FArray = {Matrix.ScaleToFit.FILL, Matrix.ScaleToFit.START, Matrix.ScaleToFit.CENTER, Matrix.ScaleToFit.END};

    public ImageView(Context context) {
        super(context);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mColorFilter = null;
        this.mHasColorFilter = false;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mRecycleableBitmapDrawable = null;
        this.mDrawableTintList = null;
        this.mDrawableTintMode = null;
        this.mHasDrawableTint = false;
        this.mHasDrawableTintMode = false;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        initImageView();
    }

    public ImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ImageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mColorFilter = null;
        this.mHasColorFilter = false;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mRecycleableBitmapDrawable = null;
        this.mDrawableTintList = null;
        this.mDrawableTintMode = null;
        this.mHasDrawableTint = false;
        this.mHasDrawableTintMode = false;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        initImageView();
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(2);
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ImageView, i, i2);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        if (drawable != null) {
            setImageDrawable(drawable);
        }
        this.mBaselineAlignBottom = typedArrayObtainStyledAttributes.getBoolean(6, false);
        this.mBaseline = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, -1);
        setAdjustViewBounds(typedArrayObtainStyledAttributes.getBoolean(2, false));
        setMaxWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(3, Integer.MAX_VALUE));
        setMaxHeight(typedArrayObtainStyledAttributes.getDimensionPixelSize(4, Integer.MAX_VALUE));
        int i3 = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (i3 >= 0) {
            setScaleType(sScaleTypeArray[i3]);
        }
        if (typedArrayObtainStyledAttributes.hasValue(5)) {
            this.mDrawableTintList = typedArrayObtainStyledAttributes.getColorStateList(5);
            this.mHasDrawableTint = true;
            this.mDrawableTintMode = PorterDuff.Mode.SRC_ATOP;
            this.mHasDrawableTintMode = true;
        }
        if (typedArrayObtainStyledAttributes.hasValue(9)) {
            this.mDrawableTintMode = Drawable.parseTintMode(typedArrayObtainStyledAttributes.getInt(9, -1), this.mDrawableTintMode);
            this.mHasDrawableTintMode = true;
        }
        applyImageTint();
        int i4 = typedArrayObtainStyledAttributes.getInt(10, 255);
        if (i4 != 255) {
            setImageAlpha(i4);
        }
        this.mCropToPadding = typedArrayObtainStyledAttributes.getBoolean(7, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void initImageView() {
        this.mMatrix = new Matrix();
        this.mScaleType = ScaleType.FIT_CENTER;
        if (sCompatDone) {
            return;
        }
        int i = this.mContext.getApplicationInfo().targetSdkVersion;
        sCompatAdjustViewBounds = i <= 17;
        sCompatUseCorrectStreamDensity = i > 23;
        sCompatDrawableVisibilityDispatch = i < 24;
        sCompatDone = true;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return this.mDrawable == drawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.mDrawable) {
            if (drawable != null) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if (intrinsicWidth != this.mDrawableWidth || intrinsicHeight != this.mDrawableHeight) {
                    this.mDrawableWidth = intrinsicWidth;
                    this.mDrawableHeight = intrinsicHeight;
                    configureBounds();
                }
            }
            invalidate();
            return;
        }
        super.invalidateDrawable(drawable);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return (getBackground() == null || getBackground().getCurrent() == null) ? false : true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEventInternal(accessibilityEvent);
        CharSequence contentDescription = getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            return;
        }
        accessibilityEvent.getText().add(contentDescription);
    }

    public boolean getAdjustViewBounds() {
        return this.mAdjustViewBounds;
    }

    @RemotableViewMethod
    public void setAdjustViewBounds(boolean z) {
        this.mAdjustViewBounds = z;
        if (z) {
            setScaleType(ScaleType.FIT_CENTER);
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    @RemotableViewMethod
    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    @RemotableViewMethod
    public void setMaxHeight(int i) {
        this.mMaxHeight = i;
    }

    public Drawable getDrawable() {
        if (this.mDrawable == this.mRecycleableBitmapDrawable) {
            this.mRecycleableBitmapDrawable = null;
        }
        return this.mDrawable;
    }

    private class ImageDrawableCallback implements Runnable {
        private final Drawable drawable;
        private final int resource;
        private final Uri uri;

        ImageDrawableCallback(Drawable drawable, Uri uri, int i) {
            this.drawable = drawable;
            this.uri = uri;
            this.resource = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            ImageView.this.setImageDrawable(this.drawable);
            ImageView.this.mUri = this.uri;
            ImageView.this.mResource = this.resource;
        }
    }

    @RemotableViewMethod(asyncImpl = "setImageResourceAsync")
    public void setImageResource(int i) {
        int i2 = this.mDrawableWidth;
        int i3 = this.mDrawableHeight;
        updateDrawable(null);
        this.mResource = i;
        this.mUri = null;
        resolveUri();
        if (i2 != this.mDrawableWidth || i3 != this.mDrawableHeight) {
            requestLayout();
        }
        invalidate();
    }

    public Runnable setImageResourceAsync(int i) {
        Drawable drawable;
        if (i != 0) {
            try {
                drawable = getContext().getDrawable(i);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Unable to find resource: " + i, e);
                i = 0;
                drawable = null;
            }
        } else {
            drawable = null;
        }
        return new ImageDrawableCallback(drawable, null, i);
    }

    @RemotableViewMethod(asyncImpl = "setImageURIAsync")
    public void setImageURI(Uri uri) {
        if (this.mResource == 0) {
            Uri uri2 = this.mUri;
            if (uri2 == uri) {
                return;
            }
            if (uri != null && uri2 != null && uri.equals(uri2)) {
                return;
            }
        }
        updateDrawable(null);
        this.mResource = 0;
        this.mUri = uri;
        int i = this.mDrawableWidth;
        int i2 = this.mDrawableHeight;
        resolveUri();
        if (i != this.mDrawableWidth || i2 != this.mDrawableHeight) {
            requestLayout();
        }
        invalidate();
    }

    public Runnable setImageURIAsync(Uri uri) {
        Uri uri2;
        if (this.mResource == 0 && ((uri2 = this.mUri) == uri || (uri != null && uri2 != null && uri.equals(uri2)))) {
            return null;
        }
        Drawable drawableFromUri = uri == null ? null : getDrawableFromUri(uri);
        if (drawableFromUri == null) {
            uri = null;
        }
        return new ImageDrawableCallback(drawableFromUri, uri, 0);
    }

    public void setImageDrawable(Drawable drawable) {
        if (this.mDrawable != drawable) {
            this.mResource = 0;
            this.mUri = null;
            int i = this.mDrawableWidth;
            int i2 = this.mDrawableHeight;
            updateDrawable(drawable);
            if (i != this.mDrawableWidth || i2 != this.mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }

    @RemotableViewMethod(asyncImpl = "setImageIconAsync")
    public void setImageIcon(Icon icon) {
        setImageDrawable(icon == null ? null : icon.loadDrawable(this.mContext));
    }

    public Runnable setImageIconAsync(Icon icon) {
        return new ImageDrawableCallback(icon == null ? null : icon.loadDrawable(this.mContext), null, 0);
    }

    public void setImageTintList(ColorStateList colorStateList) {
        this.mDrawableTintList = colorStateList;
        this.mHasDrawableTint = true;
        applyImageTint();
    }

    public ColorStateList getImageTintList() {
        return this.mDrawableTintList;
    }

    public void setImageTintMode(PorterDuff.Mode mode) {
        this.mDrawableTintMode = mode;
        this.mHasDrawableTintMode = true;
        applyImageTint();
    }

    public PorterDuff.Mode getImageTintMode() {
        return this.mDrawableTintMode;
    }

    private void applyImageTint() {
        if (this.mDrawable != null) {
            if (this.mHasDrawableTint || this.mHasDrawableTintMode) {
                Drawable drawableMutate = this.mDrawable.mutate();
                this.mDrawable = drawableMutate;
                if (this.mHasDrawableTint) {
                    drawableMutate.setTintList(this.mDrawableTintList);
                }
                if (this.mHasDrawableTintMode) {
                    this.mDrawable.setTintMode(this.mDrawableTintMode);
                }
                if (this.mDrawable.isStateful()) {
                    this.mDrawable.setState(getDrawableState());
                }
            }
        }
    }

    @RemotableViewMethod
    public void setImageBitmap(Bitmap bitmap) {
        this.mDrawable = null;
        BitmapDrawable bitmapDrawable = this.mRecycleableBitmapDrawable;
        if (bitmapDrawable == null) {
            this.mRecycleableBitmapDrawable = new BitmapDrawable(this.mContext.getResources(), bitmap);
        } else {
            bitmapDrawable.setBitmap(bitmap);
        }
        setImageDrawable(this.mRecycleableBitmapDrawable);
    }

    public void setImageState(int[] iArr, boolean z) {
        this.mState = iArr;
        this.mMergeState = z;
        if (this.mDrawable != null) {
            refreshDrawableState();
            resizeFromDrawable();
        }
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        super.setSelected(z);
        resizeFromDrawable();
    }

    @RemotableViewMethod
    public void setImageLevel(int i) {
        this.mLevel = i;
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setLevel(i);
            resizeFromDrawable();
        }
    }

    public enum ScaleType {
        MATRIX(0),
        FIT_XY(1),
        FIT_START(2),
        FIT_CENTER(3),
        FIT_END(4),
        CENTER(5),
        CENTER_CROP(6),
        CENTER_INSIDE(7);

        final int nativeInt;

        ScaleType(int i) {
            this.nativeInt = i;
        }
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw null;
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            requestLayout();
            invalidate();
        }
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public Matrix getImageMatrix() {
        Matrix matrix = this.mDrawMatrix;
        return matrix == null ? new Matrix(Matrix.IDENTITY_MATRIX) : matrix;
    }

    public void setImageMatrix(Matrix matrix) {
        if (matrix != null && matrix.isIdentity()) {
            matrix = null;
        }
        if ((matrix != null || this.mMatrix.isIdentity()) && (matrix == null || this.mMatrix.equals(matrix))) {
            return;
        }
        this.mMatrix.set(matrix);
        configureBounds();
        invalidate();
    }

    public boolean getCropToPadding() {
        return this.mCropToPadding;
    }

    public void setCropToPadding(boolean z) {
        if (this.mCropToPadding != z) {
            this.mCropToPadding = z;
            requestLayout();
            invalidate();
        }
    }

    private void resolveUri() {
        if (this.mDrawable == null && getResources() != null) {
            Drawable drawable = null;
            if (this.mResource != 0) {
                try {
                    drawable = this.mContext.getDrawable(this.mResource);
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Unable to find resource: " + this.mResource, e);
                    this.mResource = 0;
                }
            } else {
                Uri uri = this.mUri;
                if (uri == null) {
                    return;
                }
                Drawable drawableFromUri = getDrawableFromUri(uri);
                if (drawableFromUri == null) {
                    Log.w(LOG_TAG, "resolveUri failed on bad bitmap uri: " + this.mUri);
                    this.mUri = null;
                }
                drawable = drawableFromUri;
            }
            updateDrawable(drawable);
        }
    }

    private Drawable getDrawableFromUri(Uri uri) {
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            try {
                ContentResolver.OpenResourceIdResult resourceId = this.mContext.getContentResolver().getResourceId(uri);
                return resourceId.r.getDrawable(resourceId.id, this.mContext.getTheme());
            } catch (Exception e) {
                Log.w(LOG_TAG, "Unable to open content: " + uri, e);
            }
        } else if ("content".equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                return ImageDecoder.decodeDrawable(ImageDecoder.createSource(this.mContext.getContentResolver(), uri, sCompatUseCorrectStreamDensity ? getResources() : null), new ImageDecoder.OnHeaderDecodedListener() { // from class: android.widget.-$$Lambda$ImageView$GWf2-Z-LHjSbTbrF-I3WzfR0LeM
                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                        imageDecoder.setAllocator(1);
                    }
                });
            } catch (IOException e2) {
                Log.w(LOG_TAG, "Unable to open content: " + uri, e2);
            }
        } else {
            return Drawable.createFromPath(uri.toString());
        }
        return null;
    }

    @Override // android.view.View
    public int[] onCreateDrawableState(int i) {
        int[] iArr = this.mState;
        if (iArr == null) {
            return super.onCreateDrawableState(i);
        }
        return !this.mMergeState ? iArr : mergeDrawableStates(super.onCreateDrawableState(i + iArr.length), this.mState);
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x005d, code lost:
    
        r3 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDrawable(android.graphics.drawable.Drawable r6) {
        /*
            r5 = this;
            android.graphics.drawable.BitmapDrawable r0 = r5.mRecycleableBitmapDrawable
            r1 = 0
            if (r6 == r0) goto La
            if (r0 == 0) goto La
            r0.setBitmap(r1)
        La:
            android.graphics.drawable.Drawable r0 = r5.mDrawable
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L31
            if (r0 != r6) goto L14
            r0 = r2
            goto L15
        L14:
            r0 = r3
        L15:
            android.graphics.drawable.Drawable r4 = r5.mDrawable
            r4.setCallback(r1)
            android.graphics.drawable.Drawable r1 = r5.mDrawable
            r5.unscheduleDrawable(r1)
            boolean r1 = android.widget.ImageView.sCompatDrawableVisibilityDispatch
            if (r1 != 0) goto L32
            if (r0 != 0) goto L32
            boolean r1 = r5.isAttachedToWindow()
            if (r1 == 0) goto L32
            android.graphics.drawable.Drawable r1 = r5.mDrawable
            r1.setVisible(r3, r3)
            goto L32
        L31:
            r0 = r3
        L32:
            r5.mDrawable = r6
            if (r6 == 0) goto L90
            r6.setCallback(r5)
            int r1 = r5.getLayoutDirection()
            r6.setLayoutDirection(r1)
            boolean r1 = r6.isStateful()
            if (r1 == 0) goto L4d
            int[] r1 = r5.getDrawableState()
            r6.setState(r1)
        L4d:
            if (r0 == 0) goto L53
            boolean r0 = android.widget.ImageView.sCompatDrawableVisibilityDispatch
            if (r0 == 0) goto L75
        L53:
            boolean r0 = android.widget.ImageView.sCompatDrawableVisibilityDispatch
            if (r0 == 0) goto L5f
            int r0 = r5.getVisibility()
            if (r0 != 0) goto L72
        L5d:
            r3 = r2
            goto L72
        L5f:
            boolean r0 = r5.isAttachedToWindow()
            if (r0 == 0) goto L72
            int r0 = r5.getWindowVisibility()
            if (r0 != 0) goto L72
            boolean r0 = r5.isShown()
            if (r0 == 0) goto L72
            goto L5d
        L72:
            r6.setVisible(r3, r2)
        L75:
            int r0 = r5.mLevel
            r6.setLevel(r0)
            int r0 = r6.getIntrinsicWidth()
            r5.mDrawableWidth = r0
            int r6 = r6.getIntrinsicHeight()
            r5.mDrawableHeight = r6
            r5.applyImageTint()
            r5.applyColorMod()
            r5.configureBounds()
            goto L95
        L90:
            r6 = -1
            r5.mDrawableHeight = r6
            r5.mDrawableWidth = r6
        L95:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ImageView.updateDrawable(android.graphics.drawable.Drawable):void");
    }

    private void resizeFromDrawable() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            if (intrinsicWidth < 0) {
                intrinsicWidth = this.mDrawableWidth;
            }
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicHeight < 0) {
                intrinsicHeight = this.mDrawableHeight;
            }
            if (intrinsicWidth == this.mDrawableWidth && intrinsicHeight == this.mDrawableHeight) {
                return;
            }
            this.mDrawableWidth = intrinsicWidth;
            this.mDrawableHeight = intrinsicHeight;
            requestLayout();
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setLayoutDirection(i);
        }
    }

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType scaleType) {
        return sS2FArray[scaleType.nativeInt - 1];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code duplicated, block: B:38:0x00b2 A[PHI: r5
  0x00b2: PHI (r5v9 int) = (r5v7 int), (r5v11 int) binds: [B:30:0x009a, B:36:0x00ad] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:48:0x00d0 A[PHI: r5 r9
  0x00d0: PHI (r5v8 int) = (r5v7 int), (r5v7 int), (r5v10 int), (r5v10 int), (r5v10 int) binds: [B:27:0x0080, B:29:0x0098, B:39:0x00b3, B:40:0x00b5, B:46:0x00cb] A[DONT_GENERATE, DONT_INLINE]
  0x00d0: PHI (r9v7 int) = (r9v6 int), (r9v6 int), (r9v6 int), (r9v6 int), (r9v8 int) binds: [B:27:0x0080, B:29:0x0098, B:39:0x00b3, B:40:0x00b5, B:46:0x00cb] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        float f;
        boolean z;
        boolean z2;
        int iResolveSizeAndState;
        int iResolveSizeAndState2;
        boolean z3;
        resolveUri();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.mDrawable == null) {
            this.mDrawableWidth = -1;
            this.mDrawableHeight = -1;
            f = 0.0f;
            z = false;
            z2 = false;
            i3 = 0;
            i4 = 0;
        } else {
            i3 = this.mDrawableWidth;
            i4 = this.mDrawableHeight;
            if (i3 <= 0) {
                i3 = 1;
            }
            if (i4 <= 0) {
                i4 = 1;
            }
            if (this.mAdjustViewBounds) {
                z = mode != 1073741824;
                z2 = mode2 != 1073741824;
                f = i3 / i4;
            } else {
                f = 0.0f;
                z = false;
                z2 = false;
            }
        }
        int i5 = this.mPaddingLeft;
        int i6 = this.mPaddingRight;
        int i7 = this.mPaddingTop;
        int i8 = this.mPaddingBottom;
        if (z || z2) {
            int iResolveAdjustedSize = resolveAdjustedSize(i3 + i5 + i6, this.mMaxWidth, i);
            int iResolveAdjustedSize2 = resolveAdjustedSize(i4 + i7 + i8, this.mMaxHeight, i2);
            if (f != 0.0f) {
                float f2 = (iResolveAdjustedSize2 - i7) - i8;
                if (Math.abs((((iResolveAdjustedSize - i5) - i6) / f2) - f) <= 1.0E-7d) {
                    iResolveSizeAndState = iResolveAdjustedSize;
                    iResolveSizeAndState2 = iResolveAdjustedSize2;
                } else {
                    if (z) {
                        int i9 = ((int) (f2 * f)) + i5 + i6;
                        if (!z2 && !sCompatAdjustViewBounds) {
                            iResolveAdjustedSize = resolveAdjustedSize(i9, this.mMaxWidth, i);
                        }
                        if (i9 <= iResolveAdjustedSize) {
                            iResolveAdjustedSize = i9;
                            z3 = true;
                        } else {
                            z3 = false;
                        }
                    } else {
                        z3 = false;
                    }
                    if (z3 || !z2) {
                        iResolveSizeAndState = iResolveAdjustedSize;
                        iResolveSizeAndState2 = iResolveAdjustedSize2;
                    } else {
                        int i10 = ((int) (((iResolveAdjustedSize - i5) - i6) / f)) + i7 + i8;
                        if (!z && !sCompatAdjustViewBounds) {
                            iResolveAdjustedSize2 = resolveAdjustedSize(i10, this.mMaxHeight, i2);
                        }
                        if (i10 <= iResolveAdjustedSize2) {
                            iResolveSizeAndState2 = i10;
                            iResolveSizeAndState = iResolveAdjustedSize;
                        } else {
                            iResolveSizeAndState = iResolveAdjustedSize;
                            iResolveSizeAndState2 = iResolveAdjustedSize2;
                        }
                    }
                }
            } else {
                iResolveSizeAndState = iResolveAdjustedSize;
                iResolveSizeAndState2 = iResolveAdjustedSize2;
            }
        } else {
            int iMax = Math.max(i3 + i5 + i6, getSuggestedMinimumWidth());
            int iMax2 = Math.max(i4 + i7 + i8, getSuggestedMinimumHeight());
            iResolveSizeAndState = resolveSizeAndState(iMax, i, 0);
            iResolveSizeAndState2 = resolveSizeAndState(iMax2, i2, 0);
        }
        setMeasuredDimension(iResolveSizeAndState, iResolveSizeAndState2);
    }

    private int resolveAdjustedSize(int i, int i2, int i3) {
        int mode = View.MeasureSpec.getMode(i3);
        int size = View.MeasureSpec.getSize(i3);
        if (mode == Integer.MIN_VALUE) {
            return Math.min(Math.min(i, size), i2);
        }
        if (mode != 0) {
            return mode != 1073741824 ? i : size;
        }
        return Math.min(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        this.mHaveFrame = true;
        configureBounds();
        return frame;
    }

    private void configureBounds() {
        float f;
        float f2;
        if (this.mDrawable == null || !this.mHaveFrame) {
            return;
        }
        int i = this.mDrawableWidth;
        int i2 = this.mDrawableHeight;
        int width = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
        int height = (getHeight() - this.mPaddingTop) - this.mPaddingBottom;
        boolean z = (i < 0 || width == i) && (i2 < 0 || height == i2);
        if (i <= 0 || i2 <= 0 || ScaleType.FIT_XY == this.mScaleType) {
            this.mDrawable.setBounds(0, 0, width, height);
            this.mDrawMatrix = null;
            return;
        }
        this.mDrawable.setBounds(0, 0, i, i2);
        if (ScaleType.MATRIX == this.mScaleType) {
            if (this.mMatrix.isIdentity()) {
                this.mDrawMatrix = null;
                return;
            } else {
                this.mDrawMatrix = this.mMatrix;
                return;
            }
        }
        if (z) {
            this.mDrawMatrix = null;
            return;
        }
        if (ScaleType.CENTER == this.mScaleType) {
            Matrix matrix = this.mMatrix;
            this.mDrawMatrix = matrix;
            matrix.setTranslate(Math.round((width - i) * 0.5f), Math.round((height - i2) * 0.5f));
            return;
        }
        float f3 = 0.0f;
        if (ScaleType.CENTER_CROP == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            if (i * height > width * i2) {
                f2 = height / i2;
                float f4 = (width - (i * f2)) * 0.5f;
                f = 0.0f;
                f3 = f4;
            } else {
                float f5 = width / i;
                f = (height - (i2 * f5)) * 0.5f;
                f2 = f5;
            }
            this.mDrawMatrix.setScale(f2, f2);
            this.mDrawMatrix.postTranslate(Math.round(f3), Math.round(f));
            return;
        }
        if (ScaleType.CENTER_INSIDE == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            float fMin = (i > width || i2 > height) ? Math.min(width / i, height / i2) : 1.0f;
            float fRound = Math.round((width - (i * fMin)) * 0.5f);
            float fRound2 = Math.round((height - (i2 * fMin)) * 0.5f);
            this.mDrawMatrix.setScale(fMin, fMin);
            this.mDrawMatrix.postTranslate(fRound, fRound2);
            return;
        }
        this.mTempSrc.set(0.0f, 0.0f, i, i2);
        this.mTempDst.set(0.0f, 0.0f, width, height);
        Matrix matrix2 = this.mMatrix;
        this.mDrawMatrix = matrix2;
        matrix2.setRectToRect(this.mTempSrc, this.mTempDst, scaleTypeToScaleToFit(this.mScaleType));
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mDrawable;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setHotspot(f, f2);
        }
    }

    public void animateTransform(Matrix matrix) {
        Drawable drawable = this.mDrawable;
        if (drawable == null) {
            return;
        }
        if (matrix == null) {
            drawable.setBounds(0, 0, getWidth(), getHeight());
        } else {
            drawable.setBounds(0, 0, this.mDrawableWidth, this.mDrawableHeight);
            if (this.mDrawMatrix == null) {
                this.mDrawMatrix = new Matrix();
            }
            this.mDrawMatrix.set(matrix);
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawable == null || this.mDrawableWidth == 0 || this.mDrawableHeight == 0) {
            return;
        }
        if (this.mDrawMatrix == null && this.mPaddingTop == 0 && this.mPaddingLeft == 0) {
            this.mDrawable.draw(canvas);
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        if (this.mCropToPadding) {
            int i = this.mScrollX;
            int i2 = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + i, this.mPaddingTop + i2, ((i + this.mRight) - this.mLeft) - this.mPaddingRight, ((i2 + this.mBottom) - this.mTop) - this.mPaddingBottom);
        }
        canvas.translate(this.mPaddingLeft, this.mPaddingTop);
        Matrix matrix = this.mDrawMatrix;
        if (matrix != null) {
            canvas.concat(matrix);
        }
        this.mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override // android.view.View
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public int getBaseline() {
        if (this.mBaselineAlignBottom) {
            return getMeasuredHeight();
        }
        return this.mBaseline;
    }

    public void setBaseline(int i) {
        if (this.mBaseline != i) {
            this.mBaseline = i;
            requestLayout();
        }
    }

    public void setBaselineAlignBottom(boolean z) {
        if (this.mBaselineAlignBottom != z) {
            this.mBaselineAlignBottom = z;
            requestLayout();
        }
    }

    public boolean getBaselineAlignBottom() {
        return this.mBaselineAlignBottom;
    }

    public final void setColorFilter(int i, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(i, mode));
    }

    @RemotableViewMethod
    public final void setColorFilter(int i) {
        setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    }

    public final void clearColorFilter() {
        setColorFilter((ColorFilter) null);
    }

    public final void setXfermode(Xfermode xfermode) {
        if (this.mXfermode != xfermode) {
            this.mXfermode = xfermode;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mColorFilter != colorFilter) {
            this.mColorFilter = colorFilter;
            this.mHasColorFilter = true;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public int getImageAlpha() {
        return this.mAlpha;
    }

    @RemotableViewMethod
    public void setImageAlpha(int i) {
        setAlpha(i);
    }

    @RemotableViewMethod
    @Deprecated
    public void setAlpha(int i) {
        int i2 = i & 255;
        if (this.mAlpha != i2) {
            this.mAlpha = i2;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    private void applyColorMod() {
        Drawable drawable = this.mDrawable;
        if (drawable == null || !this.mColorMod) {
            return;
        }
        Drawable drawableMutate = drawable.mutate();
        this.mDrawable = drawableMutate;
        if (this.mHasColorFilter) {
            drawableMutate.setColorFilter(this.mColorFilter);
        }
        this.mDrawable.setXfermode(this.mXfermode);
        this.mDrawable.setAlpha((this.mAlpha * 256) >> 8);
    }

    @Override // android.view.View
    public boolean isOpaque() {
        Drawable drawable;
        return super.isOpaque() || ((drawable = this.mDrawable) != null && this.mXfermode == null && drawable.getOpacity() == -1 && ((this.mAlpha * 256) >> 8) == 255 && isFilledByImage());
    }

    private boolean isFilledByImage() {
        Drawable drawable = this.mDrawable;
        if (drawable == null) {
            return false;
        }
        Rect bounds = drawable.getBounds();
        Matrix matrix = this.mDrawMatrix;
        if (matrix == null) {
            return bounds.left <= 0 && bounds.top <= 0 && bounds.right >= getWidth() && bounds.bottom >= getHeight();
        }
        if (!matrix.rectStaysRect()) {
            return false;
        }
        RectF rectF = this.mTempSrc;
        RectF rectF2 = this.mTempDst;
        rectF.set(bounds);
        matrix.mapRect(rectF2, rectF);
        return rectF2.left <= 0.0f && rectF2.top <= 0.0f && rectF2.right >= ((float) getWidth()) && rectF2.bottom >= ((float) getHeight());
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        Drawable drawable = this.mDrawable;
        if (drawable == null || sCompatDrawableVisibilityDispatch) {
            return;
        }
        drawable.setVisible(z, false);
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int i) {
        super.setVisibility(i);
        Drawable drawable = this.mDrawable;
        if (drawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        drawable.setVisible(i == 0, false);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Drawable drawable = this.mDrawable;
        if (drawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        drawable.setVisible(getVisibility() == 0, false);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Drawable drawable = this.mDrawable;
        if (drawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        drawable.setVisible(false, false);
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return ImageView.class.getName();
    }

    @Override // android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("layout:baseline", getBaseline());
    }

    @Override // android.view.View
    public boolean isDefaultFocusHighlightNeeded(Drawable drawable, Drawable drawable2) {
        Drawable drawable3 = this.mDrawable;
        return super.isDefaultFocusHighlightNeeded(drawable, drawable2) && (drawable3 == null || !drawable3.isStateful() || !this.mDrawable.hasFocusStateSpecified());
    }
}
