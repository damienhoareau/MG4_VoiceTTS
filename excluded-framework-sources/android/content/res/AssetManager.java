package android.content.res;

import android.content.pm.ActivityInfo;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.ParcelFileDescriptor;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import com.android.internal.util.Preconditions;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class AssetManager implements AutoCloseable {
    public static final int ACCESS_BUFFER = 3;
    public static final int ACCESS_RANDOM = 1;
    public static final int ACCESS_STREAMING = 2;
    public static final int ACCESS_UNKNOWN = 0;
    private static final boolean DEBUG_REFS = false;
    private static final String FRAMEWORK_APK_PATH = "/system/framework/framework-res.apk";
    private static final String TAG = "AssetManager";
    private static ArraySet<ApkAssets> sSystemApkAssetsSet;
    private ApkAssets[] mApkAssets;
    private int mNumRefs;
    private long mObject;
    private final long[] mOffsets;
    private boolean mOpen;
    private HashMap<Long, RuntimeException> mRefStacks;
    private final TypedValue mValue;
    private static final Object sSync = new Object();
    private static final ApkAssets[] sEmptyApkAssets = new ApkAssets[0];
    static AssetManager sSystem = null;
    private static ApkAssets[] sSystemApkAssets = new ApkAssets[0];

    public static native String getAssetAllocations();

    public static native int getGlobalAssetCount();

    public static native int getGlobalAssetManagerCount();

    private void invalidateCachesLocked(int i) {
    }

    private static native void nativeApplyStyle(long j, long j2, int i, int i2, long j3, int[] iArr, long j4, long j5);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeAssetDestroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetRemainingLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetRead(long j, byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetReadChar(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetSeek(long j, long j2, int i);

    private static native long nativeCreate();

    private static native void nativeDestroy(long j);

    private static native SparseArray<String> nativeGetAssignedPackageIdentifiers(long j);

    private static native String[] nativeGetLocales(long j, boolean z);

    private static native int nativeGetResourceArray(long j, int i, int[] iArr);

    private static native int nativeGetResourceArraySize(long j, int i);

    private static native int nativeGetResourceBagValue(long j, int i, int i2, TypedValue typedValue);

    private static native String nativeGetResourceEntryName(long j, int i);

    private static native int nativeGetResourceIdentifier(long j, String str, String str2, String str3);

    private static native int[] nativeGetResourceIntArray(long j, int i);

    private static native String nativeGetResourceName(long j, int i);

    private static native String nativeGetResourcePackageName(long j, int i);

    private static native String[] nativeGetResourceStringArray(long j, int i);

    private static native int[] nativeGetResourceStringArrayInfo(long j, int i);

    private static native String nativeGetResourceTypeName(long j, int i);

    private static native int nativeGetResourceValue(long j, int i, short s, TypedValue typedValue, boolean z);

    private static native Configuration[] nativeGetSizeConfigurations(long j);

    private static native int[] nativeGetStyleAttributes(long j, int i);

    private static native String[] nativeList(long j, String str) throws IOException;

    private static native long nativeOpenAsset(long j, String str, int i);

    private static native ParcelFileDescriptor nativeOpenAssetFd(long j, String str, long[] jArr) throws IOException;

    private static native long nativeOpenNonAsset(long j, int i, String str, int i2);

    private static native ParcelFileDescriptor nativeOpenNonAssetFd(long j, int i, String str, long[] jArr) throws IOException;

    private static native long nativeOpenXmlAsset(long j, int i, String str);

    private static native boolean nativeResolveAttrs(long j, long j2, int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4);

    private static native boolean nativeRetrieveAttributes(long j, long j2, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetApkAssets(long j, ApkAssets[] apkAssetsArr, boolean z);

    private static native void nativeSetConfiguration(long j, int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17);

    private static native void nativeThemeApplyStyle(long j, long j2, int i, boolean z);

    static native void nativeThemeClear(long j);

    static native void nativeThemeCopy(long j, long j2);

    private static native long nativeThemeCreate(long j);

    private static native void nativeThemeDestroy(long j);

    private static native void nativeThemeDump(long j, long j2, int i, String str, String str2);

    private static native int nativeThemeGetAttributeValue(long j, long j2, int i, TypedValue typedValue, boolean z);

    static native int nativeThemeGetChangingConfigurations(long j);

    private static native void nativeVerifySystemIdmaps();

    public static class Builder {
        private ArrayList<ApkAssets> mUserApkAssets = new ArrayList<>();

        public Builder addApkAssets(ApkAssets apkAssets) {
            this.mUserApkAssets.add(apkAssets);
            return this;
        }

        public AssetManager build() {
            ApkAssets[] apkAssets = AssetManager.getSystem().getApkAssets();
            ApkAssets[] apkAssetsArr = new ApkAssets[apkAssets.length + this.mUserApkAssets.size()];
            boolean z = false;
            System.arraycopy(apkAssets, 0, apkAssetsArr, 0, apkAssets.length);
            int size = this.mUserApkAssets.size();
            for (int i = 0; i < size; i++) {
                apkAssetsArr[apkAssets.length + i] = this.mUserApkAssets.get(i);
            }
            AssetManager assetManager = new AssetManager(z);
            assetManager.mApkAssets = apkAssetsArr;
            AssetManager.nativeSetApkAssets(assetManager.mObject, apkAssetsArr, false);
            return assetManager;
        }
    }

    public AssetManager() {
        ApkAssets[] apkAssetsArr;
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked();
            apkAssetsArr = sSystemApkAssets;
        }
        this.mObject = nativeCreate();
        setApkAssets(apkAssetsArr, false);
    }

    private AssetManager(boolean z) {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        this.mObject = nativeCreate();
    }

    private static void createSystemAssetsInZygoteLocked() {
        if (sSystem != null) {
            return;
        }
        nativeVerifySystemIdmaps();
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(ApkAssets.loadFromPath(FRAMEWORK_APK_PATH, true));
            loadStaticRuntimeOverlays(arrayList);
            sSystemApkAssetsSet = new ArraySet<>(arrayList);
            sSystemApkAssets = (ApkAssets[]) arrayList.toArray(new ApkAssets[arrayList.size()]);
            AssetManager assetManager = new AssetManager(true);
            sSystem = assetManager;
            assetManager.setApkAssets(sSystemApkAssets, false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create system AssetManager", e);
        }
    }

    private static void loadStaticRuntimeOverlays(ArrayList<ApkAssets> arrayList) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream("/data/resource-cache/overlays.list");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                try {
                    FileLock fileLockLock = fileInputStream.getChannel().lock(0L, Long.MAX_VALUE, true);
                    while (true) {
                        try {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            } else {
                                arrayList.add(ApkAssets.loadOverlayFromPath(line.split(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER)[1], true));
                            }
                        } catch (Throwable th) {
                            try {
                                throw th;
                            } catch (Throwable th2) {
                                if (fileLockLock != null) {
                                    $closeResource(th, fileLockLock);
                                }
                                throw th2;
                            }
                        }
                        try {
                            throw th;
                        } catch (Throwable th3) {
                            $closeResource(th, bufferedReader);
                            throw th3;
                        }
                    }
                    if (fileLockLock != null) {
                        $closeResource(null, fileLockLock);
                    }
                    $closeResource(null, bufferedReader);
                    IoUtils.closeQuietly(fileInputStream);
                } catch (Throwable th4) {
                    throw th4;
                }
            } catch (Throwable th5) {
                IoUtils.closeQuietly(fileInputStream);
                throw th5;
            }
        } catch (FileNotFoundException unused) {
            Log.i(TAG, "no overlays.list file found");
        }
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    public static AssetManager getSystem() {
        AssetManager assetManager;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked();
            assetManager = sSystem;
        }
        return assetManager;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decRefsLocked(hashCode());
            }
        }
    }

    public void setApkAssets(ApkAssets[] apkAssetsArr, boolean z) {
        Preconditions.checkNotNull(apkAssetsArr, "apkAssets");
        ApkAssets[] apkAssetsArr2 = sSystemApkAssets;
        int length = apkAssetsArr2.length + apkAssetsArr.length;
        ApkAssets[] apkAssetsArr3 = new ApkAssets[length];
        System.arraycopy(apkAssetsArr2, 0, apkAssetsArr3, 0, apkAssetsArr2.length);
        int length2 = sSystemApkAssets.length;
        for (ApkAssets apkAssets : apkAssetsArr) {
            if (!sSystemApkAssetsSet.contains(apkAssets)) {
                apkAssetsArr3[length2] = apkAssets;
                length2++;
            }
        }
        if (length2 != length) {
            apkAssetsArr3 = (ApkAssets[]) Arrays.copyOf(apkAssetsArr3, length2);
        }
        synchronized (this) {
            ensureOpenLocked();
            this.mApkAssets = apkAssetsArr3;
            nativeSetApkAssets(this.mObject, apkAssetsArr3, z);
            if (z) {
                invalidateCachesLocked(-1);
            }
        }
    }

    public ApkAssets[] getApkAssets() {
        synchronized (this) {
            if (this.mOpen) {
                return this.mApkAssets;
            }
            return sEmptyApkAssets;
        }
    }

    public int findCookieForPath(String str) {
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureValidLocked();
            int length = this.mApkAssets.length;
            for (int i = 0; i < length; i++) {
                if (str.equals(this.mApkAssets[i].getAssetPath())) {
                    return i + 1;
                }
            }
            return 0;
        }
    }

    @Deprecated
    public int addAssetPath(String str) {
        return addAssetPathInternal(str, false, false);
    }

    @Deprecated
    public int addAssetPathAsSharedLibrary(String str) {
        return addAssetPathInternal(str, false, true);
    }

    @Deprecated
    public int addOverlayPath(String str) {
        return addAssetPathInternal(str, true, false);
    }

    private int addAssetPathInternal(String str, boolean z, boolean z2) {
        ApkAssets apkAssetsLoadFromPath;
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureOpenLocked();
            int length = this.mApkAssets.length;
            for (int i = 0; i < length; i++) {
                if (this.mApkAssets[i].getAssetPath().equals(str)) {
                    return i + 1;
                }
            }
            try {
                if (z) {
                    apkAssetsLoadFromPath = ApkAssets.loadOverlayFromPath("/data/resource-cache/" + str.substring(1).replace('/', '@') + "@idmap", false);
                } else {
                    apkAssetsLoadFromPath = ApkAssets.loadFromPath(str, false, z2);
                }
                int i2 = length + 1;
                ApkAssets[] apkAssetsArr = (ApkAssets[]) Arrays.copyOf(this.mApkAssets, i2);
                this.mApkAssets = apkAssetsArr;
                apkAssetsArr[length] = apkAssetsLoadFromPath;
                nativeSetApkAssets(this.mObject, apkAssetsArr, true);
                invalidateCachesLocked(-1);
                return i2;
            } catch (IOException unused) {
                return 0;
            }
        }
    }

    private void ensureValidLocked() {
        if (this.mObject == 0) {
            throw new RuntimeException("AssetManager has been destroyed");
        }
    }

    private void ensureOpenLocked() {
        if (!this.mOpen) {
            throw new RuntimeException("AssetManager has been closed");
        }
    }

    boolean getResourceValue(int i, int i2, TypedValue typedValue, boolean z) {
        Preconditions.checkNotNull(typedValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int iNativeGetResourceValue = nativeGetResourceValue(this.mObject, i, (short) i2, typedValue, z);
            if (iNativeGetResourceValue <= 0) {
                return false;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                typedValue.string = this.mApkAssets[iNativeGetResourceValue - 1].getStringFromPool(typedValue.data);
            }
            return true;
        }
    }

    CharSequence getResourceText(int i) {
        synchronized (this) {
            TypedValue typedValue = this.mValue;
            if (!getResourceValue(i, 0, typedValue, true)) {
                return null;
            }
            return typedValue.coerceToString();
        }
    }

    CharSequence getResourceBagText(int i, int i2) {
        synchronized (this) {
            ensureValidLocked();
            TypedValue typedValue = this.mValue;
            int iNativeGetResourceBagValue = nativeGetResourceBagValue(this.mObject, i, i2, typedValue);
            if (iNativeGetResourceBagValue <= 0) {
                return null;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                return this.mApkAssets[iNativeGetResourceBagValue - 1].getStringFromPool(typedValue.data);
            }
            return typedValue.coerceToString();
        }
    }

    int getResourceArraySize(int i) {
        int iNativeGetResourceArraySize;
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceArraySize = nativeGetResourceArraySize(this.mObject, i);
        }
        return iNativeGetResourceArraySize;
    }

    int getResourceArray(int i, int[] iArr) {
        int iNativeGetResourceArray;
        Preconditions.checkNotNull(iArr, "outData");
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceArray = nativeGetResourceArray(this.mObject, i, iArr);
        }
        return iNativeGetResourceArray;
    }

    String[] getResourceStringArray(int i) {
        String[] strArrNativeGetResourceStringArray;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetResourceStringArray = nativeGetResourceStringArray(this.mObject, i);
        }
        return strArrNativeGetResourceStringArray;
    }

    CharSequence[] getResourceTextArray(int i) {
        synchronized (this) {
            ensureValidLocked();
            int[] iArrNativeGetResourceStringArrayInfo = nativeGetResourceStringArrayInfo(this.mObject, i);
            if (iArrNativeGetResourceStringArrayInfo == null) {
                return null;
            }
            int length = iArrNativeGetResourceStringArrayInfo.length;
            CharSequence[] charSequenceArr = new CharSequence[length / 2];
            int i2 = 0;
            int i3 = 0;
            while (i2 < length) {
                int i4 = iArrNativeGetResourceStringArrayInfo[i2];
                int i5 = iArrNativeGetResourceStringArrayInfo[i2 + 1];
                charSequenceArr[i3] = (i5 < 0 || i4 <= 0) ? null : this.mApkAssets[i4 - 1].getStringFromPool(i5);
                i2 += 2;
                i3++;
            }
            return charSequenceArr;
        }
    }

    int[] getResourceIntArray(int i) {
        int[] iArrNativeGetResourceIntArray;
        synchronized (this) {
            ensureValidLocked();
            iArrNativeGetResourceIntArray = nativeGetResourceIntArray(this.mObject, i);
        }
        return iArrNativeGetResourceIntArray;
    }

    int[] getStyleAttributes(int i) {
        int[] iArrNativeGetStyleAttributes;
        synchronized (this) {
            ensureValidLocked();
            iArrNativeGetStyleAttributes = nativeGetStyleAttributes(this.mObject, i);
        }
        return iArrNativeGetStyleAttributes;
    }

    boolean getThemeValue(long j, int i, TypedValue typedValue, boolean z) {
        Preconditions.checkNotNull(typedValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int iNativeThemeGetAttributeValue = nativeThemeGetAttributeValue(this.mObject, j, i, typedValue, z);
            if (iNativeThemeGetAttributeValue <= 0) {
                return false;
            }
            typedValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(typedValue.changingConfigurations);
            if (typedValue.type == 3) {
                typedValue.string = this.mApkAssets[iNativeThemeGetAttributeValue - 1].getStringFromPool(typedValue.data);
            }
            return true;
        }
    }

    void dumpTheme(long j, int i, String str, String str2) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeDump(this.mObject, j, i, str, str2);
        }
    }

    String getResourceName(int i) {
        String strNativeGetResourceName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceName = nativeGetResourceName(this.mObject, i);
        }
        return strNativeGetResourceName;
    }

    String getResourcePackageName(int i) {
        String strNativeGetResourcePackageName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourcePackageName = nativeGetResourcePackageName(this.mObject, i);
        }
        return strNativeGetResourcePackageName;
    }

    String getResourceTypeName(int i) {
        String strNativeGetResourceTypeName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceTypeName = nativeGetResourceTypeName(this.mObject, i);
        }
        return strNativeGetResourceTypeName;
    }

    String getResourceEntryName(int i) {
        String strNativeGetResourceEntryName;
        synchronized (this) {
            ensureValidLocked();
            strNativeGetResourceEntryName = nativeGetResourceEntryName(this.mObject, i);
        }
        return strNativeGetResourceEntryName;
    }

    int getResourceIdentifier(String str, String str2, String str3) {
        int iNativeGetResourceIdentifier;
        synchronized (this) {
            ensureValidLocked();
            iNativeGetResourceIdentifier = nativeGetResourceIdentifier(this.mObject, str, str2, str3);
        }
        return iNativeGetResourceIdentifier;
    }

    CharSequence getPooledStringForCookie(int i, int i2) {
        return getApkAssets()[i - 1].getStringFromPool(i2);
    }

    public InputStream open(String str) throws IOException {
        return open(str, 2);
    }

    public InputStream open(String str, int i) throws IOException {
        AssetInputStream assetInputStream;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenAsset = nativeOpenAsset(this.mObject, str, i);
            if (jNativeOpenAsset == 0) {
                throw new FileNotFoundException("Asset file: " + str);
            }
            assetInputStream = new AssetInputStream(jNativeOpenAsset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public AssetFileDescriptor openFd(String str) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor parcelFileDescriptorNativeOpenAssetFd = nativeOpenAssetFd(this.mObject, str, this.mOffsets);
            if (parcelFileDescriptorNativeOpenAssetFd == null) {
                throw new FileNotFoundException("Asset file: " + str);
            }
            assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptorNativeOpenAssetFd, this.mOffsets[0], this.mOffsets[1]);
        }
        return assetFileDescriptor;
    }

    public String[] list(String str) throws IOException {
        String[] strArrNativeList;
        Preconditions.checkNotNull(str, "path");
        synchronized (this) {
            ensureValidLocked();
            strArrNativeList = nativeList(this.mObject, str);
        }
        return strArrNativeList;
    }

    public InputStream openNonAsset(String str) throws IOException {
        return openNonAsset(0, str, 2);
    }

    public InputStream openNonAsset(String str, int i) throws IOException {
        return openNonAsset(0, str, i);
    }

    public InputStream openNonAsset(int i, String str) throws IOException {
        return openNonAsset(i, str, 2);
    }

    public InputStream openNonAsset(int i, String str, int i2) throws IOException {
        AssetInputStream assetInputStream;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenNonAsset = nativeOpenNonAsset(this.mObject, i, str, i2);
            if (jNativeOpenNonAsset == 0) {
                throw new FileNotFoundException("Asset absolute file: " + str);
            }
            assetInputStream = new AssetInputStream(jNativeOpenNonAsset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public AssetFileDescriptor openNonAssetFd(String str) throws IOException {
        return openNonAssetFd(0, str);
    }

    public AssetFileDescriptor openNonAssetFd(int i, String str) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor parcelFileDescriptorNativeOpenNonAssetFd = nativeOpenNonAssetFd(this.mObject, i, str, this.mOffsets);
            if (parcelFileDescriptorNativeOpenNonAssetFd == null) {
                throw new FileNotFoundException("Asset absolute file: " + str);
            }
            assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptorNativeOpenNonAssetFd, this.mOffsets[0], this.mOffsets[1]);
        }
        return assetFileDescriptor;
    }

    public XmlResourceParser openXmlResourceParser(String str) throws IOException {
        return openXmlResourceParser(0, str);
    }

    public XmlResourceParser openXmlResourceParser(int i, String str) throws Exception {
        XmlBlock xmlBlockOpenXmlBlockAsset = openXmlBlockAsset(i, str);
        try {
            XmlResourceParser xmlResourceParserNewParser = xmlBlockOpenXmlBlockAsset.newParser();
            if (xmlResourceParserNewParser == null) {
                throw new AssertionError("block.newParser() returned a null parser");
            }
            if (xmlBlockOpenXmlBlockAsset != null) {
                $closeResource(null, xmlBlockOpenXmlBlockAsset);
            }
            return xmlResourceParserNewParser;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (xmlBlockOpenXmlBlockAsset != null) {
                    $closeResource(th, xmlBlockOpenXmlBlockAsset);
                }
                throw th2;
            }
        }
    }

    XmlBlock openXmlBlockAsset(String str) throws IOException {
        return openXmlBlockAsset(0, str);
    }

    XmlBlock openXmlBlockAsset(int i, String str) throws IOException {
        XmlBlock xmlBlock;
        Preconditions.checkNotNull(str, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long jNativeOpenXmlAsset = nativeOpenXmlAsset(this.mObject, i, str);
            if (jNativeOpenXmlAsset == 0) {
                throw new FileNotFoundException("Asset XML file: " + str);
            }
            xmlBlock = new XmlBlock(this, jNativeOpenXmlAsset);
            incRefsLocked(xmlBlock.hashCode());
        }
        return xmlBlock;
    }

    void xmlBlockGone(int i) {
        synchronized (this) {
            decRefsLocked(i);
        }
    }

    void applyStyle(long j, int i, int i2, XmlBlock.Parser parser, int[] iArr, long j2, long j3) {
        Preconditions.checkNotNull(iArr, "inAttrs");
        synchronized (this) {
            ensureValidLocked();
            nativeApplyStyle(this.mObject, j, i, i2, parser != null ? parser.mParseState : 0L, iArr, j2, j3);
        }
    }

    boolean resolveAttrs(long j, int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        boolean zNativeResolveAttrs;
        Preconditions.checkNotNull(iArr2, "inAttrs");
        Preconditions.checkNotNull(iArr3, "outValues");
        Preconditions.checkNotNull(iArr4, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            zNativeResolveAttrs = nativeResolveAttrs(this.mObject, j, i, i2, iArr, iArr2, iArr3, iArr4);
        }
        return zNativeResolveAttrs;
    }

    boolean retrieveAttributes(XmlBlock.Parser parser, int[] iArr, int[] iArr2, int[] iArr3) {
        boolean zNativeRetrieveAttributes;
        Preconditions.checkNotNull(parser, "parser");
        Preconditions.checkNotNull(iArr, "inAttrs");
        Preconditions.checkNotNull(iArr2, "outValues");
        Preconditions.checkNotNull(iArr3, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            zNativeRetrieveAttributes = nativeRetrieveAttributes(this.mObject, parser.mParseState, iArr, iArr2, iArr3);
        }
        return zNativeRetrieveAttributes;
    }

    long createTheme() {
        long jNativeThemeCreate;
        synchronized (this) {
            ensureValidLocked();
            jNativeThemeCreate = nativeThemeCreate(this.mObject);
            incRefsLocked(jNativeThemeCreate);
        }
        return jNativeThemeCreate;
    }

    void releaseTheme(long j) {
        synchronized (this) {
            nativeThemeDestroy(j);
            decRefsLocked(j);
        }
    }

    void applyStyleToTheme(long j, int i, boolean z) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeApplyStyle(this.mObject, j, i, z);
        }
    }

    protected void finalize() throws Throwable {
        long j = this.mObject;
        if (j != 0) {
            nativeDestroy(j);
        }
    }

    public final class AssetInputStream extends InputStream {
        private long mAssetNativePtr;
        private long mLength;
        private long mMarkPos;

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        public final int getAssetInt() {
            throw new UnsupportedOperationException();
        }

        public final long getNativeAsset() {
            return this.mAssetNativePtr;
        }

        private AssetInputStream(long j) {
            this.mAssetNativePtr = j;
            this.mLength = AssetManager.nativeAssetGetLength(j);
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            ensureOpen();
            return AssetManager.nativeAssetReadChar(this.mAssetNativePtr);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr) throws IOException {
            ensureOpen();
            Preconditions.checkNotNull(bArr, "b");
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) throws IOException {
            ensureOpen();
            Preconditions.checkNotNull(bArr, "b");
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, bArr, i, i2);
        }

        @Override // java.io.InputStream
        public final long skip(long j) throws IOException {
            ensureOpen();
            long jNativeAssetSeek = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
            long j2 = jNativeAssetSeek + j;
            long j3 = this.mLength;
            if (j2 > j3) {
                j = j3 - jNativeAssetSeek;
            }
            if (j > 0) {
                AssetManager.nativeAssetSeek(this.mAssetNativePtr, j, 0);
            }
            return j;
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            ensureOpen();
            long jNativeAssetGetRemainingLength = AssetManager.nativeAssetGetRemainingLength(this.mAssetNativePtr);
            if (jNativeAssetGetRemainingLength > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) jNativeAssetGetRemainingLength;
        }

        @Override // java.io.InputStream
        public final void mark(int i) {
            ensureOpen();
            this.mMarkPos = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
        }

        @Override // java.io.InputStream
        public final void reset() throws IOException {
            ensureOpen();
            AssetManager.nativeAssetSeek(this.mAssetNativePtr, this.mMarkPos, -1);
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public final void close() throws IOException {
            long j = this.mAssetNativePtr;
            if (j != 0) {
                AssetManager.nativeAssetDestroy(j);
                this.mAssetNativePtr = 0L;
                synchronized (AssetManager.this) {
                    AssetManager.this.decRefsLocked(hashCode());
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        private void ensureOpen() {
            if (this.mAssetNativePtr == 0) {
                throw new IllegalStateException("AssetInputStream is closed");
            }
        }
    }

    public boolean isUpToDate() {
        for (ApkAssets apkAssets : getApkAssets()) {
            if (!apkAssets.isUpToDate()) {
                return false;
            }
        }
        return true;
    }

    public String[] getLocales() {
        String[] strArrNativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetLocales = nativeGetLocales(this.mObject, false);
        }
        return strArrNativeGetLocales;
    }

    public String[] getNonSystemLocales() {
        String[] strArrNativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            strArrNativeGetLocales = nativeGetLocales(this.mObject, true);
        }
        return strArrNativeGetLocales;
    }

    Configuration[] getSizeConfigurations() {
        Configuration[] configurationArrNativeGetSizeConfigurations;
        synchronized (this) {
            ensureValidLocked();
            configurationArrNativeGetSizeConfigurations = nativeGetSizeConfigurations(this.mObject);
        }
        return configurationArrNativeGetSizeConfigurations;
    }

    public void setConfiguration(int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17) {
        synchronized (this) {
            ensureValidLocked();
            nativeSetConfiguration(this.mObject, i, i2, str, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17);
        }
    }

    public SparseArray<String> getAssignedPackageIdentifiers() {
        SparseArray<String> sparseArrayNativeGetAssignedPackageIdentifiers;
        synchronized (this) {
            ensureValidLocked();
            sparseArrayNativeGetAssignedPackageIdentifiers = nativeGetAssignedPackageIdentifiers(this.mObject);
        }
        return sparseArrayNativeGetAssignedPackageIdentifiers;
    }

    private void incRefsLocked(long j) {
        this.mNumRefs++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decRefsLocked(long j) {
        int i = this.mNumRefs - 1;
        this.mNumRefs = i;
        if (i == 0) {
            long j2 = this.mObject;
            if (j2 != 0) {
                nativeDestroy(j2);
                this.mObject = 0L;
                this.mApkAssets = sEmptyApkAssets;
            }
        }
    }
}
