package android.hardware.display;

import android.view.Display;
import android.view.Surface;

/* JADX INFO: loaded from: classes.dex */
public final class VirtualDisplay {
    private final Display mDisplay;
    private final DisplayManagerGlobal mGlobal;
    private Surface mSurface;
    private IVirtualDisplayCallback mToken;

    public static abstract class Callback {
        public void onPaused() {
        }

        public void onResumed() {
        }

        public void onStopped() {
        }
    }

    VirtualDisplay(DisplayManagerGlobal displayManagerGlobal, Display display, IVirtualDisplayCallback iVirtualDisplayCallback, Surface surface) {
        this.mGlobal = displayManagerGlobal;
        this.mDisplay = display;
        this.mToken = iVirtualDisplayCallback;
        this.mSurface = surface;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public void setSurface(Surface surface) {
        if (this.mSurface != surface) {
            this.mGlobal.setVirtualDisplaySurface(this.mToken, surface);
            this.mSurface = surface;
        }
    }

    public void resize(int i, int i2, int i3) {
        this.mGlobal.resizeVirtualDisplay(this.mToken, i, i2, i3);
    }

    public void release() {
        IVirtualDisplayCallback iVirtualDisplayCallback = this.mToken;
        if (iVirtualDisplayCallback != null) {
            this.mGlobal.releaseVirtualDisplay(iVirtualDisplayCallback);
            this.mToken = null;
        }
    }

    public String toString() {
        return "VirtualDisplay{display=" + this.mDisplay + ", token=" + this.mToken + ", surface=" + this.mSurface + "}";
    }
}
