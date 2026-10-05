package android.net;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public class CaptivePortal implements Parcelable {
    public static final int APP_RETURN_DISMISSED = 0;
    public static final int APP_RETURN_UNWANTED = 1;
    public static final int APP_RETURN_WANTED_AS_IS = 2;
    public static final Parcelable.Creator<CaptivePortal> CREATOR = new Parcelable.Creator<CaptivePortal>() { // from class: android.net.CaptivePortal.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CaptivePortal createFromParcel(Parcel parcel) {
            return new CaptivePortal(parcel.readStrongBinder());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CaptivePortal[] newArray(int i) {
            return new CaptivePortal[i];
        }
    };
    private final IBinder mBinder;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CaptivePortal(IBinder iBinder) {
        this.mBinder = iBinder;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mBinder);
    }

    public void reportCaptivePortalDismissed() {
        try {
            ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(0);
        } catch (RemoteException unused) {
        }
    }

    public void ignoreNetwork() {
        try {
            ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(1);
        } catch (RemoteException unused) {
        }
    }

    public void useNetwork() {
        try {
            ICaptivePortal.Stub.asInterface(this.mBinder).appResponse(2);
        } catch (RemoteException unused) {
        }
    }
}
