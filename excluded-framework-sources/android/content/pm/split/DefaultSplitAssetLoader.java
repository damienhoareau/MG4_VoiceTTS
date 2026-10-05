package android.content.pm.split;

import android.content.pm.PackageParser;
import android.content.res.ApkAssets;
import android.content.res.AssetManager;
import android.os.Build;
import com.android.internal.util.ArrayUtils;
import java.io.IOException;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public class DefaultSplitAssetLoader implements SplitAssetLoader {
    private final String mBaseCodePath;
    private AssetManager mCachedAssetManager;
    private final int mFlags;
    private final String[] mSplitCodePaths;

    public DefaultSplitAssetLoader(PackageParser.PackageLite packageLite, int i) {
        this.mBaseCodePath = packageLite.baseCodePath;
        this.mSplitCodePaths = packageLite.splitCodePaths;
        this.mFlags = i;
    }

    private static ApkAssets loadApkAssets(String str, int i) throws PackageParser.PackageParserException {
        if ((i & 1) != 0 && !PackageParser.isApkPath(str)) {
            throw new PackageParser.PackageParserException(-100, "Invalid package file: " + str);
        }
        try {
            return ApkAssets.loadFromPath(str);
        } catch (IOException e) {
            throw new PackageParser.PackageParserException(-2, "Failed to load APK at path " + str, e);
        }
    }

    @Override // android.content.pm.split.SplitAssetLoader
    public AssetManager getBaseAssetManager() throws PackageParser.PackageParserException {
        AssetManager assetManager = this.mCachedAssetManager;
        if (assetManager != null) {
            return assetManager;
        }
        String[] strArr = this.mSplitCodePaths;
        int i = 1;
        ApkAssets[] apkAssetsArr = new ApkAssets[(strArr != null ? strArr.length : 0) + 1];
        apkAssetsArr[0] = loadApkAssets(this.mBaseCodePath, this.mFlags);
        if (!ArrayUtils.isEmpty(this.mSplitCodePaths)) {
            String[] strArr2 = this.mSplitCodePaths;
            int length = strArr2.length;
            int i2 = 0;
            while (i2 < length) {
                apkAssetsArr[i] = loadApkAssets(strArr2[i2], this.mFlags);
                i2++;
                i++;
            }
        }
        AssetManager assetManager2 = new AssetManager();
        assetManager2.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Build.VERSION.RESOURCES_SDK_INT);
        assetManager2.setApkAssets(apkAssetsArr, false);
        this.mCachedAssetManager = assetManager2;
        return assetManager2;
    }

    @Override // android.content.pm.split.SplitAssetLoader
    public AssetManager getSplitAssetManager(int i) throws PackageParser.PackageParserException {
        return getBaseAssetManager();
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        IoUtils.closeQuietly(this.mCachedAssetManager);
    }
}
