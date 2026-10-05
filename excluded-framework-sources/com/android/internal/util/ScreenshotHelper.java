package com.android.internal.util;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

/* JADX INFO: loaded from: classes3.dex */
public class ScreenshotHelper {
    private static final String SYSUI_PACKAGE = "com.android.systemui";
    private static final String SYSUI_SCREENSHOT_ERROR_RECEIVER = "com.android.systemui.screenshot.ScreenshotServiceErrorReceiver";
    private static final String SYSUI_SCREENSHOT_SERVICE = "com.android.systemui.screenshot.TakeScreenshotService";
    private static final String TAG = "ScreenshotHelper";
    private final Context mContext;
    private final int SCREENSHOT_TIMEOUT_MS = 10000;
    private final Object mScreenshotLock = new Object();
    private ServiceConnection mScreenshotConnection = null;

    public ScreenshotHelper(Context context) {
        this.mContext = context;
    }

    public void takeScreenshot(final int i, final boolean z, final boolean z2, final Handler handler) {
        synchronized (this.mScreenshotLock) {
            if (this.mScreenshotConnection != null) {
                return;
            }
            ComponentName componentName = new ComponentName(SYSUI_PACKAGE, SYSUI_SCREENSHOT_SERVICE);
            Intent intent = new Intent();
            final Runnable runnable = new Runnable() { // from class: com.android.internal.util.ScreenshotHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (ScreenshotHelper.this.mScreenshotLock) {
                        if (ScreenshotHelper.this.mScreenshotConnection != null) {
                            ScreenshotHelper.this.mContext.unbindService(ScreenshotHelper.this.mScreenshotConnection);
                            ScreenshotHelper.this.mScreenshotConnection = null;
                            ScreenshotHelper.this.notifyScreenshotError();
                        }
                    }
                }
            };
            intent.setComponent(componentName);
            ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.android.internal.util.ScreenshotHelper.2
                @Override // android.content.ServiceConnection
                public void onServiceConnected(ComponentName componentName2, IBinder iBinder) {
                    synchronized (ScreenshotHelper.this.mScreenshotLock) {
                        if (ScreenshotHelper.this.mScreenshotConnection != this) {
                            return;
                        }
                        Messenger messenger = new Messenger(iBinder);
                        Message messageObtain = Message.obtain((Handler) null, i);
                        messageObtain.replyTo = new Messenger(new Handler(handler.getLooper()) { // from class: com.android.internal.util.ScreenshotHelper.2.1
                            @Override // android.os.Handler
                            public void handleMessage(Message message) {
                                synchronized (ScreenshotHelper.this.mScreenshotLock) {
                                    if (ScreenshotHelper.this.mScreenshotConnection == this) {
                                        ScreenshotHelper.this.mContext.unbindService(ScreenshotHelper.this.mScreenshotConnection);
                                        ScreenshotHelper.this.mScreenshotConnection = null;
                                        handler.removeCallbacks(runnable);
                                    }
                                }
                            }
                        });
                        int i2 = 1;
                        messageObtain.arg1 = z ? 1 : 0;
                        if (!z2) {
                            i2 = 0;
                        }
                        messageObtain.arg2 = i2;
                        try {
                            messenger.send(messageObtain);
                        } catch (RemoteException e) {
                            Log.e(ScreenshotHelper.TAG, "Couldn't take screenshot: " + e);
                        }
                    }
                }

                @Override // android.content.ServiceConnection
                public void onServiceDisconnected(ComponentName componentName2) {
                    synchronized (ScreenshotHelper.this.mScreenshotLock) {
                        if (ScreenshotHelper.this.mScreenshotConnection != null) {
                            ScreenshotHelper.this.mContext.unbindService(ScreenshotHelper.this.mScreenshotConnection);
                            ScreenshotHelper.this.mScreenshotConnection = null;
                            handler.removeCallbacks(runnable);
                            ScreenshotHelper.this.notifyScreenshotError();
                        }
                    }
                }
            };
            if (this.mContext.bindServiceAsUser(intent, serviceConnection, 33554433, UserHandle.CURRENT)) {
                this.mScreenshotConnection = serviceConnection;
                handler.postDelayed(runnable, JobInfo.MIN_BACKOFF_MILLIS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyScreenshotError() {
        ComponentName componentName = new ComponentName(SYSUI_PACKAGE, SYSUI_SCREENSHOT_ERROR_RECEIVER);
        Intent intent = new Intent(Intent.ACTION_USER_PRESENT);
        intent.setComponent(componentName);
        intent.addFlags(335544320);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }
}
