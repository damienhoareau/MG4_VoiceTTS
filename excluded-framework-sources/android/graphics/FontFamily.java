package android.graphics;

import android.content.res.AssetManager;
import android.graphics.fonts.FontVariationAxis;
import android.text.TextUtils;
import android.util.Log;
import dalvik.annotation.optimization.CriticalNative;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import libcore.util.NativeAllocationRegistry;

/* JADX INFO: loaded from: classes.dex */
public class FontFamily {
    private static String TAG = "FontFamily";
    private static final NativeAllocationRegistry sBuilderRegistry = new NativeAllocationRegistry(FontFamily.class.getClassLoader(), nGetBuilderReleaseFunc(), 64);
    private static final NativeAllocationRegistry sFamilyRegistry = new NativeAllocationRegistry(FontFamily.class.getClassLoader(), nGetFamilyReleaseFunc(), 64);
    private long mBuilderPtr;
    private Runnable mNativeBuilderCleaner;
    public long mNativePtr;

    @CriticalNative
    private static native void nAddAxisValue(long j, int i, float f);

    private static native boolean nAddFont(long j, ByteBuffer byteBuffer, int i, int i2, int i3);

    private static native boolean nAddFontFromAssetManager(long j, AssetManager assetManager, String str, int i, boolean z, int i2, int i3, int i4);

    private static native boolean nAddFontWeightStyle(long j, ByteBuffer byteBuffer, int i, int i2, int i3);

    @CriticalNative
    private static native long nCreateFamily(long j);

    @CriticalNative
    private static native long nGetBuilderReleaseFunc();

    @CriticalNative
    private static native long nGetFamilyReleaseFunc();

    private static native long nInitBuilder(String str, int i);

    public FontFamily() {
        long jNInitBuilder = nInitBuilder(null, 0);
        this.mBuilderPtr = jNInitBuilder;
        this.mNativeBuilderCleaner = sBuilderRegistry.registerNativeAllocation(this, jNInitBuilder);
    }

    public FontFamily(String[] strArr, int i) {
        String strJoin;
        if (strArr == null || strArr.length == 0) {
            strJoin = null;
        } else if (strArr.length == 1) {
            strJoin = strArr[0];
        } else {
            strJoin = TextUtils.join(",", strArr);
        }
        long jNInitBuilder = nInitBuilder(strJoin, i);
        this.mBuilderPtr = jNInitBuilder;
        this.mNativeBuilderCleaner = sBuilderRegistry.registerNativeAllocation(this, jNInitBuilder);
    }

    public boolean freeze() {
        long j = this.mBuilderPtr;
        if (j == 0) {
            throw new IllegalStateException("This FontFamily is already frozen");
        }
        this.mNativePtr = nCreateFamily(j);
        this.mNativeBuilderCleaner.run();
        this.mBuilderPtr = 0L;
        long j2 = this.mNativePtr;
        if (j2 != 0) {
            sFamilyRegistry.registerNativeAllocation(this, j2);
        }
        return this.mNativePtr != 0;
    }

    public void abortCreation() {
        if (this.mBuilderPtr == 0) {
            throw new IllegalStateException("This FontFamily is already frozen or abandoned");
        }
        this.mNativeBuilderCleaner.run();
        this.mBuilderPtr = 0L;
    }

    public boolean addFont(String str, int i, FontVariationAxis[] fontVariationAxisArr, int i2, int i3) {
        if (this.mBuilderPtr == 0) {
            throw new IllegalStateException("Unable to call addFont after freezing.");
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            try {
                FileChannel channel = fileInputStream.getChannel();
                MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
                if (fontVariationAxisArr != null) {
                    for (FontVariationAxis fontVariationAxis : fontVariationAxisArr) {
                        nAddAxisValue(this.mBuilderPtr, fontVariationAxis.getOpenTypeTagValue(), fontVariationAxis.getStyleValue());
                    }
                }
                boolean zNAddFont = nAddFont(this.mBuilderPtr, map, i, i2, i3);
                fileInputStream.close();
                return zNAddFont;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    try {
                        fileInputStream.close();
                        throw th2;
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                        throw th2;
                    }
                }
            }
        } catch (IOException unused) {
            Log.e(TAG, "Error mapping font file " + str);
            return false;
        }
    }

    public boolean addFontFromBuffer(ByteBuffer byteBuffer, int i, FontVariationAxis[] fontVariationAxisArr, int i2, int i3) {
        if (this.mBuilderPtr == 0) {
            throw new IllegalStateException("Unable to call addFontWeightStyle after freezing.");
        }
        if (fontVariationAxisArr != null) {
            for (FontVariationAxis fontVariationAxis : fontVariationAxisArr) {
                nAddAxisValue(this.mBuilderPtr, fontVariationAxis.getOpenTypeTagValue(), fontVariationAxis.getStyleValue());
            }
        }
        return nAddFontWeightStyle(this.mBuilderPtr, byteBuffer, i, i2, i3);
    }

    public boolean addFontFromAssetManager(AssetManager assetManager, String str, int i, boolean z, int i2, int i3, int i4, FontVariationAxis[] fontVariationAxisArr) {
        if (this.mBuilderPtr == 0) {
            throw new IllegalStateException("Unable to call addFontFromAsset after freezing.");
        }
        if (fontVariationAxisArr != null) {
            for (FontVariationAxis fontVariationAxis : fontVariationAxisArr) {
                nAddAxisValue(this.mBuilderPtr, fontVariationAxis.getOpenTypeTagValue(), fontVariationAxis.getStyleValue());
            }
        }
        return nAddFontFromAssetManager(this.mBuilderPtr, assetManager, str, i, z, i2, i3, i4);
    }

    private static boolean nAddFont(long j, ByteBuffer byteBuffer, int i) {
        return nAddFont(j, byteBuffer, i, -1, -1);
    }
}
