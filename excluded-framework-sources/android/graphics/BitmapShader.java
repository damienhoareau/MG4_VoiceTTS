package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class BitmapShader extends Shader {
    public Bitmap mBitmap;
    private int mTileX;
    private int mTileY;

    private static native long nativeCreate(long j, Bitmap bitmap, int i, int i2);

    public BitmapShader(Bitmap bitmap, Shader.TileMode tileMode, Shader.TileMode tileMode2) {
        this(bitmap, tileMode.nativeInt, tileMode2.nativeInt);
    }

    private BitmapShader(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap must be non-null");
        }
        if (bitmap == this.mBitmap && i == this.mTileX && i2 == this.mTileY) {
            return;
        }
        this.mBitmap = bitmap;
        this.mTileX = i;
        this.mTileY = i2;
    }

    @Override // android.graphics.Shader
    long createNativeInstance(long j) {
        return nativeCreate(j, this.mBitmap, this.mTileX, this.mTileY);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        BitmapShader bitmapShader = new BitmapShader(this.mBitmap, this.mTileX, this.mTileY);
        copyLocalMatrix(bitmapShader);
        return bitmapShader;
    }
}
