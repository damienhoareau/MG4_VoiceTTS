package android.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.graphics.palette.Palette;
import com.android.internal.graphics.palette.VariationalKMeansQuantizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public final class WallpaperColors implements Parcelable {
    private static final float BRIGHT_IMAGE_MEAN_LUMINANCE = 0.75f;
    public static final Parcelable.Creator<WallpaperColors> CREATOR = new Parcelable.Creator<WallpaperColors>() { // from class: android.app.WallpaperColors.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WallpaperColors createFromParcel(Parcel parcel) {
            return new WallpaperColors(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WallpaperColors[] newArray(int i) {
            return new WallpaperColors[i];
        }
    };
    private static final float DARK_PIXEL_LUMINANCE = 0.45f;
    private static final float DARK_THEME_MEAN_LUMINANCE = 0.25f;
    public static final int HINT_FROM_BITMAP = 4;
    public static final int HINT_SUPPORTS_DARK_TEXT = 1;
    public static final int HINT_SUPPORTS_DARK_THEME = 2;
    private static final int MAX_BITMAP_SIZE = 112;
    private static final float MAX_DARK_AREA = 0.05f;
    private static final int MAX_WALLPAPER_EXTRACTION_AREA = 12544;
    private static final float MIN_COLOR_OCCURRENCE = 0.05f;
    private int mColorHints;
    private final ArrayList<Color> mMainColors;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WallpaperColors(Parcel parcel) {
        this.mMainColors = new ArrayList<>();
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            this.mMainColors.add(Color.valueOf(parcel.readInt()));
        }
        this.mColorHints = parcel.readInt();
    }

    public static WallpaperColors fromDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null");
        }
        Rect rectCopyBounds = drawable.copyBounds();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            intrinsicWidth = 112;
            intrinsicHeight = 112;
        }
        Size sizeCalculateOptimalSize = calculateOptimalSize(intrinsicWidth, intrinsicHeight);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(sizeCalculateOptimalSize.getWidth(), sizeCalculateOptimalSize.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight());
        drawable.draw(canvas);
        WallpaperColors wallpaperColorsFromBitmap = fromBitmap(bitmapCreateBitmap);
        bitmapCreateBitmap.recycle();
        drawable.setBounds(rectCopyBounds);
        return wallpaperColorsFromBitmap;
    }

    public static WallpaperColors fromBitmap(Bitmap bitmap) {
        boolean z;
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap can't be null");
        }
        if (bitmap.getWidth() * bitmap.getHeight() > MAX_WALLPAPER_EXTRACTION_AREA) {
            Size sizeCalculateOptimalSize = calculateOptimalSize(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, sizeCalculateOptimalSize.getWidth(), sizeCalculateOptimalSize.getHeight(), true);
            z = true;
        } else {
            z = false;
        }
        ArrayList arrayList = new ArrayList(Palette.from(bitmap).setQuantizer(new VariationalKMeansQuantizer()).maximumColorCount(5).clearFilters().resizeBitmapArea(MAX_WALLPAPER_EXTRACTION_AREA).generate().getSwatches());
        final float width = bitmap.getWidth() * bitmap.getHeight() * 0.05f;
        arrayList.removeIf(new Predicate() { // from class: android.app.-$$Lambda$WallpaperColors$8R5kfKKLfHjpw_QXmD1mWOKwJxc
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WallpaperColors.lambda$fromBitmap$0(width, (Palette.Swatch) obj);
            }
        });
        arrayList.sort(new Comparator() { // from class: android.app.-$$Lambda$WallpaperColors$MQFGJ9EZ9CDeGbIhMufJKqru3IE
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return WallpaperColors.lambda$fromBitmap$1((Palette.Swatch) obj, (Palette.Swatch) obj2);
            }
        });
        int size = arrayList.size();
        Color color = null;
        Color color2 = null;
        Color color3 = null;
        for (int i = 0; i < size; i++) {
            Color colorValueOf = Color.valueOf(((Palette.Swatch) arrayList.get(i)).getRgb());
            if (i == 0) {
                color = colorValueOf;
            } else if (i == 1) {
                color2 = colorValueOf;
            } else {
                if (i != 2) {
                    break;
                }
                color3 = colorValueOf;
            }
        }
        int iCalculateDarkHints = calculateDarkHints(bitmap);
        if (z) {
            bitmap.recycle();
        }
        return new WallpaperColors(color, color2, color3, iCalculateDarkHints | 4);
    }

    static /* synthetic */ boolean lambda$fromBitmap$0(float f, Palette.Swatch swatch) {
        return ((float) swatch.getPopulation()) < f;
    }

    static /* synthetic */ int lambda$fromBitmap$1(Palette.Swatch swatch, Palette.Swatch swatch2) {
        return swatch2.getPopulation() - swatch.getPopulation();
    }

    public WallpaperColors(Color color, Color color2, Color color3) {
        this(color, color2, color3, 0);
    }

    public WallpaperColors(Color color, Color color2, Color color3, int i) {
        if (color == null) {
            throw new IllegalArgumentException("Primary color should never be null.");
        }
        ArrayList<Color> arrayList = new ArrayList<>(3);
        this.mMainColors = arrayList;
        arrayList.add(color);
        if (color2 != null) {
            this.mMainColors.add(color2);
        }
        if (color3 != null) {
            if (color2 == null) {
                throw new IllegalArgumentException("tertiaryColor can't be specified when secondaryColor is null");
            }
            this.mMainColors.add(color3);
        }
        this.mColorHints = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        List<Color> mainColors = getMainColors();
        int size = mainColors.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeInt(mainColors.get(i2).toArgb());
        }
        parcel.writeInt(this.mColorHints);
    }

    public Color getPrimaryColor() {
        return this.mMainColors.get(0);
    }

    public Color getSecondaryColor() {
        if (this.mMainColors.size() < 2) {
            return null;
        }
        return this.mMainColors.get(1);
    }

    public Color getTertiaryColor() {
        if (this.mMainColors.size() < 3) {
            return null;
        }
        return this.mMainColors.get(2);
    }

    public List<Color> getMainColors() {
        return Collections.unmodifiableList(this.mMainColors);
    }

    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WallpaperColors wallpaperColors = (WallpaperColors) obj;
        return this.mMainColors.equals(wallpaperColors.mMainColors) && this.mColorHints == wallpaperColors.mColorHints;
    }

    public int hashCode() {
        return (this.mMainColors.hashCode() * 31) + this.mColorHints;
    }

    public int getColorHints() {
        return this.mColorHints;
    }

    public void setColorHints(int i) {
        this.mColorHints = i;
    }

    private static int calculateDarkHints(Bitmap bitmap) {
        int i = 0;
        if (bitmap == null) {
            return 0;
        }
        int width = bitmap.getWidth() * bitmap.getHeight();
        int[] iArr = new int[width];
        double d = 0.0d;
        int i2 = (int) (width * 0.05f);
        bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        float[] fArr = new float[3];
        int i3 = 0;
        for (int i4 = 0; i4 < width; i4++) {
            ColorUtils.colorToHSL(iArr[i4], fArr);
            float f = fArr[2];
            int iAlpha = Color.alpha(iArr[i4]);
            if (f < DARK_PIXEL_LUMINANCE && iAlpha != 0) {
                i3++;
            }
            d += (double) f;
        }
        double d2 = d / ((double) width);
        if (d2 > 0.75d && i3 < i2) {
            i = 1;
        }
        return d2 < 0.25d ? i | 2 : i;
    }

    private static Size calculateOptimalSize(int i, int i2) {
        int i3 = i * i2;
        double dSqrt = i3 > MAX_WALLPAPER_EXTRACTION_AREA ? Math.sqrt(12544.0d / ((double) i3)) : 1.0d;
        int i4 = (int) (((double) i) * dSqrt);
        int i5 = (int) (((double) i2) * dSqrt);
        if (i4 == 0) {
            i4 = 1;
        }
        if (i5 == 0) {
            i5 = 1;
        }
        return new Size(i4, i5);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mMainColors.size(); i++) {
            sb.append(Integer.toHexString(this.mMainColors.get(i).toArgb()));
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        }
        return "[WallpaperColors: " + sb.toString() + "h: " + this.mColorHints + "]";
    }
}
