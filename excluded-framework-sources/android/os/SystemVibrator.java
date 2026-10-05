package android.os;

import android.content.Context;
import android.media.AudioAttributes;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class SystemVibrator extends Vibrator {
    private static final String TAG = "Vibrator";
    private final IVibratorService mService;
    private final Binder mToken;

    public SystemVibrator() {
        this.mToken = new Binder();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    public SystemVibrator(Context context) {
        super(context);
        this.mToken = new Binder();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return false;
        }
        try {
            return iVibratorService.hasVibrator();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public boolean hasAmplitudeControl() {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to check amplitude control; no vibrator service.");
            return false;
        }
        try {
            return iVibratorService.hasAmplitudeControl();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, VibrationEffect vibrationEffect, AudioAttributes audioAttributes) {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return;
        }
        try {
            iVibratorService.vibrate(i, str, vibrationEffect, usageForAttributes(audioAttributes), this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to vibrate.", e);
        }
    }

    private static int usageForAttributes(AudioAttributes audioAttributes) {
        if (audioAttributes != null) {
            return audioAttributes.getUsage();
        }
        return 0;
    }

    @Override // android.os.Vibrator
    public void cancel() {
        IVibratorService iVibratorService = this.mService;
        if (iVibratorService == null) {
            return;
        }
        try {
            iVibratorService.cancelVibrate(this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to cancel vibration.", e);
        }
    }
}
