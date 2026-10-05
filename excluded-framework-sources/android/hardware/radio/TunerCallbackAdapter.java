package android.hardware.radio;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class TunerCallbackAdapter extends ITunerCallback.Stub {
    private static final String TAG = "BroadcastRadio.TunerCallbackAdapter";
    private final RadioTuner.Callback mCallback;
    RadioManager.ProgramInfo mCurrentProgramInfo;
    private final Handler mHandler;
    List<RadioManager.ProgramInfo> mLastCompleteList;
    ProgramList mProgramList;
    private final Object mLock = new Object();
    boolean mIsAntennaConnected = true;
    private boolean mDelayedCompleteCallback = false;

    TunerCallbackAdapter(RadioTuner.Callback callback, Handler handler) {
        this.mCallback = callback;
        if (handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = handler;
        }
    }

    void close() {
        synchronized (this.mLock) {
            if (this.mProgramList != null) {
                this.mProgramList.close();
            }
        }
    }

    void setProgramListObserver(final ProgramList programList, final ProgramList.OnCloseListener onCloseListener) {
        Objects.requireNonNull(onCloseListener);
        synchronized (this.mLock) {
            if (this.mProgramList != null) {
                Log.w(TAG, "Previous program list observer wasn't properly closed, closing it...");
                this.mProgramList.close();
            }
            this.mProgramList = programList;
            if (programList == null) {
                return;
            }
            programList.setOnCloseListener(new ProgramList.OnCloseListener() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$Hl80-0ppQ17uTjZuGamwBQMrO6Y
                @Override // android.hardware.radio.ProgramList.OnCloseListener
                public final void onClose() {
                    this.f$0.lambda$setProgramListObserver$0$TunerCallbackAdapter(programList, onCloseListener);
                }
            });
            programList.addOnCompleteListener(new ProgramList.OnCompleteListener() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$V-mJUy8dIlOVjsZ1ckkgn490jFI
                @Override // android.hardware.radio.ProgramList.OnCompleteListener
                public final void onComplete() {
                    this.f$0.lambda$setProgramListObserver$1$TunerCallbackAdapter(programList);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setProgramListObserver$0$TunerCallbackAdapter(ProgramList programList, ProgramList.OnCloseListener onCloseListener) {
        synchronized (this.mLock) {
            if (this.mProgramList != programList) {
                return;
            }
            this.mProgramList = null;
            this.mLastCompleteList = null;
            onCloseListener.onClose();
        }
    }

    public /* synthetic */ void lambda$setProgramListObserver$1$TunerCallbackAdapter(ProgramList programList) {
        synchronized (this.mLock) {
            if (this.mProgramList != programList) {
                return;
            }
            this.mLastCompleteList = programList.toList();
            if (this.mDelayedCompleteCallback) {
                Log.d(TAG, "Sending delayed onBackgroundScanComplete callback");
                sendBackgroundScanCompleteLocked();
            }
        }
    }

    List<RadioManager.ProgramInfo> getLastCompleteList() {
        List<RadioManager.ProgramInfo> list;
        synchronized (this.mLock) {
            list = this.mLastCompleteList;
        }
        return list;
    }

    void clearLastCompleteList() {
        synchronized (this.mLock) {
            this.mLastCompleteList = null;
        }
    }

    RadioManager.ProgramInfo getCurrentProgramInformation() {
        RadioManager.ProgramInfo programInfo;
        synchronized (this.mLock) {
            programInfo = this.mCurrentProgramInfo;
        }
        return programInfo;
    }

    boolean isAntennaConnected() {
        return this.mIsAntennaConnected;
    }

    public /* synthetic */ void lambda$onError$2$TunerCallbackAdapter(int i) {
        this.mCallback.onError(i);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onError(final int i) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$jl29exheqPoYrltfLs9fLsjsI1A
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onError$2$TunerCallbackAdapter(i);
            }
        });
    }

    public /* synthetic */ void lambda$onTuneFailed$3$TunerCallbackAdapter(int i, ProgramSelector programSelector) {
        this.mCallback.onTuneFailed(i, programSelector);
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0024  */
    /* JADX WARN: Instruction removed from duplicated block: B:16:0x0024, please report this as an issue */
    @Override // android.hardware.radio.ITunerCallback
    public void onTuneFailed(final int i, final ProgramSelector programSelector) {
        final int i2;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$Hj_P___HTEx_8p7qvYVPXmhwu7w
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTuneFailed$3$TunerCallbackAdapter(i, programSelector);
            }
        });
        if (i == Integer.MIN_VALUE || i == -38) {
            Log.i(TAG, "Got an error with no mapping to the legacy API (" + i + "), doing a best-effort conversion to ERROR_SCAN_TIMEOUT");
            i2 = 3;
        } else {
            if (i != -32) {
                if (i == -22 || i == -19) {
                    Log.i(TAG, "Got an error with no mapping to the legacy API (" + i + "), doing a best-effort conversion to ERROR_SCAN_TIMEOUT");
                } else if (i != -1) {
                }
                i2 = 3;
            }
            i2 = 1;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$HcS5_voI1xju970_jCP6Iz0LgPE
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTuneFailed$4$TunerCallbackAdapter(i2);
            }
        });
    }

    public /* synthetic */ void lambda$onTuneFailed$4$TunerCallbackAdapter(int i) {
        this.mCallback.onError(i);
    }

    public /* synthetic */ void lambda$onConfigurationChanged$5$TunerCallbackAdapter(RadioManager.BandConfig bandConfig) {
        this.mCallback.onConfigurationChanged(bandConfig);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onConfigurationChanged(final RadioManager.BandConfig bandConfig) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$B4BuskgdSatf-Xt5wzgLniEltQk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onConfigurationChanged$5$TunerCallbackAdapter(bandConfig);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onCurrentProgramInfoChanged(final RadioManager.ProgramInfo programInfo) {
        if (programInfo == null) {
            Log.e(TAG, "ProgramInfo must not be null");
            return;
        }
        synchronized (this.mLock) {
            this.mCurrentProgramInfo = programInfo;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$RSNrzX5-O3nayC2_jg0kAR6KkKY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onCurrentProgramInfoChanged$6$TunerCallbackAdapter(programInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onCurrentProgramInfoChanged$6$TunerCallbackAdapter(RadioManager.ProgramInfo programInfo) {
        this.mCallback.onProgramInfoChanged(programInfo);
        RadioMetadata metadata = programInfo.getMetadata();
        if (metadata != null) {
            this.mCallback.onMetadataChanged(metadata);
        }
    }

    public /* synthetic */ void lambda$onTrafficAnnouncement$7$TunerCallbackAdapter(boolean z) {
        this.mCallback.onTrafficAnnouncement(z);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onTrafficAnnouncement(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$tiaoLZrR2K56rYeqHvSRh5lRdBI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTrafficAnnouncement$7$TunerCallbackAdapter(z);
            }
        });
    }

    public /* synthetic */ void lambda$onEmergencyAnnouncement$8$TunerCallbackAdapter(boolean z) {
        this.mCallback.onEmergencyAnnouncement(z);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onEmergencyAnnouncement(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$ZwPm3xxjeLvbP12KweyzqFJVnj4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onEmergencyAnnouncement$8$TunerCallbackAdapter(z);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onAntennaState(final boolean z) {
        this.mIsAntennaConnected = z;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$dR-VQmFrL_tBD2wpNvborTd8W08
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAntennaState$9$TunerCallbackAdapter(z);
            }
        });
    }

    public /* synthetic */ void lambda$onAntennaState$9$TunerCallbackAdapter(boolean z) {
        this.mCallback.onAntennaState(z);
    }

    public /* synthetic */ void lambda$onBackgroundScanAvailabilityChange$10$TunerCallbackAdapter(boolean z) {
        this.mCallback.onBackgroundScanAvailabilityChange(z);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanAvailabilityChange(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$4zf9n0sz_rU8z6a9GJmRInWrYkQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onBackgroundScanAvailabilityChange$10$TunerCallbackAdapter(z);
            }
        });
    }

    private void sendBackgroundScanCompleteLocked() {
        this.mDelayedCompleteCallback = false;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$xIUT1Qu5TkA83V8ttYy1zv-JuFo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendBackgroundScanCompleteLocked$11$TunerCallbackAdapter();
            }
        });
    }

    public /* synthetic */ void lambda$sendBackgroundScanCompleteLocked$11$TunerCallbackAdapter() {
        this.mCallback.onBackgroundScanComplete();
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanComplete() {
        synchronized (this.mLock) {
            if (this.mLastCompleteList == null) {
                Log.i(TAG, "Got onBackgroundScanComplete callback, but the program list didn't get through yet. Delaying it...");
                this.mDelayedCompleteCallback = true;
            } else {
                sendBackgroundScanCompleteLocked();
            }
        }
    }

    public /* synthetic */ void lambda$onProgramListChanged$12$TunerCallbackAdapter() {
        this.mCallback.onProgramListChanged();
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListChanged() {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$UsmGhKordXy4lhCylRP0mm2NcYc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onProgramListChanged$12$TunerCallbackAdapter();
            }
        });
    }

    public /* synthetic */ void lambda$onRdsMainInfoChanged$13$TunerCallbackAdapter(RadioRdsInfo.MainInfo mainInfo) {
        this.mCallback.onRdsMainInfoChanged(mainInfo);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRdsMainInfoChanged(final RadioRdsInfo.MainInfo mainInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$qKYuWY41kxggYqSaQGIqw0gB_gY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRdsMainInfoChanged$13$TunerCallbackAdapter(mainInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onRdsTAInfoChanged$14$TunerCallbackAdapter(RadioRdsInfo.TAInfo tAInfo) {
        this.mCallback.onRdsTAInfoChanged(tAInfo);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRdsTAInfoChanged(final RadioRdsInfo.TAInfo tAInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$AUJhfGXasIYPZIhfgyTgGSS9IE4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRdsTAInfoChanged$14$TunerCallbackAdapter(tAInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onRdsCTInfoChanged$15$TunerCallbackAdapter(RadioRdsInfo.CTInfo cTInfo) {
        this.mCallback.onRdsCTInfoChanged(cTInfo);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRdsCTInfoChanged(final RadioRdsInfo.CTInfo cTInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$5aqgx1Eze1nrTKNmpYsnBqeuAeA
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRdsCTInfoChanged$15$TunerCallbackAdapter(cTInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onRdsRTTextChanged$16$TunerCallbackAdapter(String str) {
        this.mCallback.onRdsRTTextChanged(str);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRdsRTTextChanged(final String str) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$kv3HIZo9tW-AOW4ZMHcwxFmZMSc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRdsRTTextChanged$16$TunerCallbackAdapter(str);
            }
        });
    }

    public /* synthetic */ void lambda$onRadioConfigChanged$17$TunerCallbackAdapter(RadioManager.RdsConfig rdsConfig) {
        this.mCallback.onRadioConfigChanged(rdsConfig);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRadioConfigChanged(final RadioManager.RdsConfig rdsConfig) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$jGyVTIMMTBBtunmR0gCh_QkZW5g
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRadioConfigChanged$17$TunerCallbackAdapter(rdsConfig);
            }
        });
    }

    public /* synthetic */ void lambda$onSignalInfoChanged$18$TunerCallbackAdapter(RadioSignal radioSignal) {
        this.mCallback.onSignalInfoChanged(radioSignal);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onSignalInfoChanged(final RadioSignal radioSignal) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$vDgUUMaqAX10op12SIAYXqgxvms
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onSignalInfoChanged$18$TunerCallbackAdapter(radioSignal);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListUpdated(ProgramList.Chunk chunk) {
        synchronized (this.mLock) {
            if (this.mProgramList == null) {
                return;
            }
            this.mProgramList.apply((ProgramList.Chunk) Objects.requireNonNull(chunk));
        }
    }

    public /* synthetic */ void lambda$onParametersUpdated$19$TunerCallbackAdapter(Map map) {
        this.mCallback.onParametersUpdated(map);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onParametersUpdated(final Map map) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$A6W9xS1UA1juj2gBBQ6_RixLDXQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onParametersUpdated$19$TunerCallbackAdapter(map);
            }
        });
    }

    public /* synthetic */ void lambda$onRadioFrequenceRangeComplete$20$TunerCallbackAdapter(int i, boolean z) {
        this.mCallback.onRadioFrequenceRangeComplete(i, z);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onRadioFrequenceRangeComplete(final int i, final boolean z) throws RemoteException {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$BcZ9d7HHtzoTyWcn7PKPSV_nR1E
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onRadioFrequenceRangeComplete$20$TunerCallbackAdapter(i, z);
            }
        });
    }
}
