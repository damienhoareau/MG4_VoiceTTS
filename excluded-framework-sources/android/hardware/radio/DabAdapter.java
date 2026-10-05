package android.hardware.radio;

import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
class DabAdapter extends DabTuner {
    private static final String TAG = "BroadcastRadio.DabAdapter";
    private int mBand;
    private final IDab mDab;
    private boolean mIsClosed = false;

    DabAdapter(IDab iDab, int i) {
        if (iDab == null) {
            throw null;
        }
        this.mDab = iDab;
        this.mBand = i;
    }

    @Override // android.hardware.radio.DabTuner
    public void close() {
        synchronized (this.mDab) {
            if (this.mIsClosed) {
                Log.v(TAG, "Dab is already closed");
                return;
            }
            this.mIsClosed = true;
            try {
                this.mDab.close();
            } catch (RemoteException e) {
                Log.e(TAG, "Exception trying to close dab", e);
            }
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void switchSource(int i) {
        try {
            this.mDab.switchSource(i);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't switch to dab band", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void tune(int i, long j, int i2, int i3) {
        try {
            this.mDab.tune(i, j, i2, i3);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't tune dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void step(int i) {
        try {
            IDab iDab = this.mDab;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            iDab.step(z);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't step dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void scan(int i) {
        try {
            IDab iDab = this.mDab;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            iDab.scan(z);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't scan dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void serviceFollow(boolean z) {
        try {
            this.mDab.serviceFollow(z);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't serviceFollow dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void setAnnouncement(RadioDabInfo.AnnouncementInfo announcementInfo) {
        try {
            this.mDab.setAnnouncement(announcementInfo);
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't setAnnouncement dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void stopAnnouncement() {
        try {
            this.mDab.stopAnnouncement();
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't stopAnnouncement dab station", e2);
        }
    }

    @Override // android.hardware.radio.DabTuner
    public void queryDabStationList() {
        try {
            this.mDab.queryDabStationList();
        } catch (RemoteException e) {
            Log.e(TAG, "service died", e);
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Can't queryDabStationList dab station", e2);
        }
    }
}
