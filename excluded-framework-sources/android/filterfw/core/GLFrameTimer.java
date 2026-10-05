package android.filterfw.core;

/* JADX INFO: compiled from: GLFrame.java */
/* JADX INFO: loaded from: classes.dex */
class GLFrameTimer {
    private static StopWatchMap mTimer;

    GLFrameTimer() {
    }

    public static StopWatchMap get() {
        if (mTimer == null) {
            mTimer = new StopWatchMap();
        }
        return mTimer;
    }
}
