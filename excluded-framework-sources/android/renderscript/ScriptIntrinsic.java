package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ScriptIntrinsic extends Script {
    ScriptIntrinsic(long j, RenderScript renderScript) {
        super(j, renderScript);
        if (j == 0) {
            throw new RSRuntimeException("Loading of ScriptIntrinsic failed.");
        }
    }
}
