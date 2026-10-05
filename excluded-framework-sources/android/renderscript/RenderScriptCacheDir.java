package android.renderscript;

import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class RenderScriptCacheDir {
    static File mCacheDir;

    public static void setupDiskCache(File file) {
        mCacheDir = file;
    }
}
