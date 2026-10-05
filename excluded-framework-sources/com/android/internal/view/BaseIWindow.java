package com.android.internal.view;

import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.MergedConfiguration;
import android.view.DisplayCutout;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.IWindowSession;
import com.android.internal.os.IResultReceiver;

/* JADX INFO: loaded from: classes3.dex */
public class BaseIWindow extends IWindow.Stub {
    public int mSeq;
    private IWindowSession mSession;

    @Override // android.view.IWindow
    public void closeSystemDialogs(String str) {
    }

    public void dispatchAppVisibility(boolean z) {
    }

    @Override // android.view.IWindow
    public void dispatchGetNewSurface() {
    }

    @Override // android.view.IWindow
    public void dispatchPointerCaptureChanged(boolean z) {
    }

    @Override // android.view.IWindow
    public void dispatchWindowShown() {
    }

    @Override // android.view.IWindow
    public void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) {
    }

    public void moved(int i, int i2) {
    }

    @Override // android.view.IWindow
    public void requestAppKeyboardShortcuts(IResultReceiver iResultReceiver, int i) {
    }

    @Override // android.view.IWindow
    public void windowFocusChanged(boolean z, boolean z2) {
    }

    public void setSession(IWindowSession iWindowSession) {
        this.mSession = iWindowSession;
    }

    public void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, Rect rect6, boolean z, MergedConfiguration mergedConfiguration, Rect rect7, boolean z2, boolean z3, int i, DisplayCutout.ParcelableWrapper parcelableWrapper) {
        if (z) {
            try {
                this.mSession.finishDrawing(this);
            } catch (RemoteException unused) {
            }
        }
    }

    public void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            try {
                this.mSession.wallpaperOffsetsComplete(asBinder());
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.view.IWindow
    public void dispatchDragEvent(DragEvent dragEvent) {
        if (dragEvent.getAction() == 3) {
            try {
                this.mSession.reportDropResult(this, false);
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.view.IWindow
    public void updatePointerIcon(float f, float f2) {
        InputManager.getInstance().setPointerIconType(1);
    }

    @Override // android.view.IWindow
    public void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) {
        this.mSeq = i;
    }

    public void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        if (z) {
            try {
                this.mSession.wallpaperCommandComplete(asBinder(), null);
            } catch (RemoteException unused) {
            }
        }
    }
}
