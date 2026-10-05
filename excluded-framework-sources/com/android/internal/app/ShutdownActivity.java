package com.android.internal.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;

/* JADX INFO: loaded from: classes3.dex */
public class ShutdownActivity extends Activity {
    private static final String TAG = "ShutdownActivity";
    private boolean mConfirm;
    private boolean mReboot;
    private boolean mUserRequested;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        this.mReboot = Intent.ACTION_REBOOT.equals(intent.getAction());
        this.mConfirm = intent.getBooleanExtra(Intent.EXTRA_KEY_CONFIRM, false);
        boolean booleanExtra = intent.getBooleanExtra(Intent.EXTRA_USER_REQUESTED_SHUTDOWN, false);
        this.mUserRequested = booleanExtra;
        final String stringExtra = booleanExtra ? PowerManager.SHUTDOWN_USER_REQUESTED : intent.getStringExtra(Intent.EXTRA_REASON);
        String str = "onCreate(): confirm=" + this.mConfirm;
        String str2 = TAG;
        Slog.i(TAG, str);
        Thread thread = new Thread(str2) { // from class: com.android.internal.app.ShutdownActivity.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                IPowerManager iPowerManagerAsInterface = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
                try {
                    if (ShutdownActivity.this.mReboot) {
                        iPowerManagerAsInterface.reboot(ShutdownActivity.this.mConfirm, null, false);
                    } else {
                        iPowerManagerAsInterface.shutdown(ShutdownActivity.this.mConfirm, stringExtra, false);
                    }
                } catch (RemoteException unused) {
                }
            }
        };
        thread.start();
        finish();
        try {
            thread.join();
        } catch (InterruptedException unused) {
        }
    }
}
