package android.hardware.radio;

import android.annotation.SystemApi;
import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public abstract class DabTuner {
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_UP = 0;
    public static final int ERROR_CANCELLED = 2;
    public static final int ERROR_HARDWARE_FAILURE = 0;
    public static final int ERROR_SERVER_DIED = 1;

    public static abstract class Callback {
        public void onDabAnnouncementsStatusChanged(RadioDabInfo.AnnouncementStatusInfo announcementStatusInfo) {
        }

        public void onDabCTChanged(RadioDabInfo.CTInfo cTInfo) {
        }

        public void onDabDLSChanged(char[] cArr) {
        }

        public void onDabDLSPlusChanged(RadioDabInfo.DLPlusInfo dLPlusInfo) {
        }

        public void onDabEPGChanged(RadioDabInfo.EPGInfo ePGInfo) {
        }

        public void onDabMainInfoChanged(RadioDabInfo.MainInfo mainInfo) {
        }

        public void onDabServiceFollowingNotify(int i) {
        }

        public void onDabServiceInformationListChanged(RadioDabInfo.ServiceInformationList serviceInformationList) {
        }

        public void onDabServiceLogoChanged(RadioDabInfo.ServiceLogoInfo serviceLogoInfo) {
        }

        public void onDabSlideShowChanged(Bitmap bitmap) {
        }

        public void onDabStationListInfoChanged(RadioDabInfo.StationListInfo stationListInfo) {
        }

        public void onError(int i) {
        }
    }

    public abstract void close();

    public abstract void queryDabStationList();

    public abstract void scan(int i);

    public abstract void serviceFollow(boolean z);

    public abstract void setAnnouncement(RadioDabInfo.AnnouncementInfo announcementInfo);

    public abstract void step(int i);

    public abstract void stopAnnouncement();

    public abstract void switchSource(int i);

    public abstract void tune(int i, long j, int i2, int i3);
}
