package android.text.style;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public class ImageSpan extends DynamicDrawableSpan {
    private Uri mContentUri;
    private Context mContext;
    private Drawable mDrawable;
    private int mResourceId;
    private String mSource;

    @Deprecated
    public ImageSpan(Bitmap bitmap) {
        this((Context) null, bitmap, 0);
    }

    @Deprecated
    public ImageSpan(Bitmap bitmap, int i) {
        this((Context) null, bitmap, i);
    }

    public ImageSpan(Context context, Bitmap bitmap) {
        this(context, bitmap, 0);
    }

    public ImageSpan(Context context, Bitmap bitmap, int i) {
        BitmapDrawable bitmapDrawable;
        super(i);
        this.mContext = context;
        if (context != null) {
            bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        } else {
            bitmapDrawable = new BitmapDrawable(bitmap);
        }
        this.mDrawable = bitmapDrawable;
        int intrinsicWidth = bitmapDrawable.getIntrinsicWidth();
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        this.mDrawable.setBounds(0, 0, intrinsicWidth <= 0 ? 0 : intrinsicWidth, intrinsicHeight <= 0 ? 0 : intrinsicHeight);
    }

    public ImageSpan(Drawable drawable) {
        this(drawable, 0);
    }

    public ImageSpan(Drawable drawable, int i) {
        super(i);
        this.mDrawable = drawable;
    }

    public ImageSpan(Drawable drawable, String str) {
        this(drawable, str, 0);
    }

    public ImageSpan(Drawable drawable, String str, int i) {
        super(i);
        this.mDrawable = drawable;
        this.mSource = str;
    }

    public ImageSpan(Context context, Uri uri) {
        this(context, uri, 0);
    }

    public ImageSpan(Context context, Uri uri, int i) {
        super(i);
        this.mContext = context;
        this.mContentUri = uri;
        this.mSource = uri.toString();
    }

    public ImageSpan(Context context, int i) {
        this(context, i, 0);
    }

    public ImageSpan(Context context, int i, int i2) {
        super(i2);
        this.mContext = context;
        this.mResourceId = i;
    }

    @Override // android.text.style.DynamicDrawableSpan
    public Drawable getDrawable() throws Throwable {
        Drawable drawable;
        Drawable drawable2 = this.mDrawable;
        if (drawable2 != null) {
            return drawable2;
        }
        BitmapDrawable bitmapDrawable = null;
        if (this.mContentUri != null) {
            try {
                InputStream inputStreamOpenInputStream = this.mContext.getContentResolver().openInputStream(this.mContentUri);
                BitmapDrawable bitmapDrawable2 = new BitmapDrawable(this.mContext.getResources(), BitmapFactory.decodeStream(inputStreamOpenInputStream));
                try {
                    bitmapDrawable2.setBounds(0, 0, bitmapDrawable2.getIntrinsicWidth(), bitmapDrawable2.getIntrinsicHeight());
                    inputStreamOpenInputStream.close();
                    return bitmapDrawable2;
                } catch (Exception e) {
                    e = e;
                    bitmapDrawable = bitmapDrawable2;
                    Log.e("ImageSpan", "Failed to loaded content " + this.mContentUri, e);
                    return bitmapDrawable;
                }
            } catch (Exception e2) {
                e = e2;
            }
        } else {
            try {
                drawable = this.mContext.getDrawable(this.mResourceId);
                try {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Exception unused) {
                    Log.e("ImageSpan", "Unable to find resource: " + this.mResourceId);
                    return drawable;
                }
            } catch (Exception unused2) {
                drawable = null;
            }
        }
    }

    public String getSource() {
        return this.mSource;
    }
}
