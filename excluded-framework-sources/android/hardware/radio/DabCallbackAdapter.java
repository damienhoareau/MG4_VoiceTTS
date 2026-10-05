package android.hardware.radio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
class DabCallbackAdapter extends IDabCallback.Stub {
    private static final String TAG = "DabCallbackAdapter";
    private final DabTuner.Callback mCallback;
    private final Handler mHandler;

    DabCallbackAdapter(DabTuner.Callback callback, Handler handler) {
        this.mCallback = callback;
        if (handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = handler;
        }
    }

    public /* synthetic */ void lambda$onDabMainInfoChanged$0$DabCallbackAdapter(RadioDabInfo.MainInfo mainInfo) {
        this.mCallback.onDabMainInfoChanged(mainInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabMainInfoChanged(final RadioDabInfo.MainInfo mainInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$ZyybVWdMnM1ipURQPoRSLnLhxDk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabMainInfoChanged$0$DabCallbackAdapter(mainInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onDabStationListInfoChanged$1$DabCallbackAdapter(RadioDabInfo.StationListInfo stationListInfo) {
        this.mCallback.onDabStationListInfoChanged(stationListInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabStationListInfoChanged(final RadioDabInfo.StationListInfo stationListInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$jLoj7SyVz8YQWPYACIwMwvv6veQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabStationListInfoChanged$1$DabCallbackAdapter(stationListInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onDabDLSChanged$2$DabCallbackAdapter(char[] cArr) {
        this.mCallback.onDabDLSChanged(cArr);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabDLSChanged(final char[] cArr) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$4m9OIDOFBOCz6EMN1TfEy_v1SDA
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabDLSChanged$2$DabCallbackAdapter(cArr);
            }
        });
    }

    public /* synthetic */ void lambda$onDabDLSPlusChanged$3$DabCallbackAdapter(RadioDabInfo.DLPlusInfo dLPlusInfo) {
        this.mCallback.onDabDLSPlusChanged(dLPlusInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabDLSPlusChanged(final RadioDabInfo.DLPlusInfo dLPlusInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$0LZNowoNxn_AEIEVww0V7fIBdi0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabDLSPlusChanged$3$DabCallbackAdapter(dLPlusInfo);
            }
        });
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabSlideShowChanged(RadioDabInfo.SlideShowInfo slideShowInfo) {
        final Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(slideShowInfo.getBuffer(), 0, slideShowInfo.getBuffer().length);
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$Ynk18imJXSYYjjDH-IKIHYgxTIE
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabSlideShowChanged$4$DabCallbackAdapter(bitmapDecodeByteArray);
            }
        });
    }

    public /* synthetic */ void lambda$onDabSlideShowChanged$4$DabCallbackAdapter(Bitmap bitmap) {
        this.mCallback.onDabSlideShowChanged(bitmap);
    }

    public /* synthetic */ void lambda$onDabEPGChanged$5$DabCallbackAdapter(RadioDabInfo.EPGInfo ePGInfo) {
        this.mCallback.onDabEPGChanged(ePGInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabEPGChanged(final RadioDabInfo.EPGInfo ePGInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$BfBxS1jIB69x9hWdHjuub2cf_Ew
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabEPGChanged$5$DabCallbackAdapter(ePGInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onError$6$DabCallbackAdapter(int i) {
        this.mCallback.onError(i);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onError(final int i) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$ULq2V-u7nWDKFojqBcYPHK0FvOs
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onError$6$DabCallbackAdapter(i);
            }
        });
    }

    public /* synthetic */ void lambda$onDabCTChanged$7$DabCallbackAdapter(RadioDabInfo.CTInfo cTInfo) {
        this.mCallback.onDabCTChanged(cTInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabCTChanged(final RadioDabInfo.CTInfo cTInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$zlgiKVF9sfUy0usfM2-s7uNIHic
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabCTChanged$7$DabCallbackAdapter(cTInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onDabServiceFollowingNotify$8$DabCallbackAdapter(int i) {
        this.mCallback.onDabServiceFollowingNotify(i);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabServiceFollowingNotify(final int i) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$gbHpaVVjEuua75KHl4PGEn6L0Xs
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabServiceFollowingNotify$8$DabCallbackAdapter(i);
            }
        });
    }

    public /* synthetic */ void lambda$onDabAnnouncementsStatusChanged$9$DabCallbackAdapter(RadioDabInfo.AnnouncementStatusInfo announcementStatusInfo) {
        this.mCallback.onDabAnnouncementsStatusChanged(announcementStatusInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabAnnouncementsStatusChanged(final RadioDabInfo.AnnouncementStatusInfo announcementStatusInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$rZFM2jZjrCsI3kDOdBOZkOpDsMw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabAnnouncementsStatusChanged$9$DabCallbackAdapter(announcementStatusInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onDabServiceLogoChanged$10$DabCallbackAdapter(RadioDabInfo.ServiceLogoInfo serviceLogoInfo) {
        this.mCallback.onDabServiceLogoChanged(serviceLogoInfo);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabServiceLogoChanged(final RadioDabInfo.ServiceLogoInfo serviceLogoInfo) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$Z6WKewkTIR8gnGTv8wAyHjOI3i8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabServiceLogoChanged$10$DabCallbackAdapter(serviceLogoInfo);
            }
        });
    }

    public /* synthetic */ void lambda$onDabServiceInformationListChanged$11$DabCallbackAdapter(RadioDabInfo.ServiceInformationList serviceInformationList) {
        this.mCallback.onDabServiceInformationListChanged(serviceInformationList);
    }

    @Override // android.hardware.radio.IDabCallback
    public void onDabServiceInformationListChanged(final RadioDabInfo.ServiceInformationList serviceInformationList) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$DabCallbackAdapter$PdRotI5BpK0Ly-8Ee39sdhaxobY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDabServiceInformationListChanged$11$DabCallbackAdapter(serviceInformationList);
            }
        });
    }
}
