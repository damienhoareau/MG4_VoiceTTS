package android.hardware.radio;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IDabCallback extends IInterface {
    void onDabAnnouncementsStatusChanged(RadioDabInfo.AnnouncementStatusInfo announcementStatusInfo) throws RemoteException;

    void onDabCTChanged(RadioDabInfo.CTInfo cTInfo) throws RemoteException;

    void onDabDLSChanged(char[] cArr) throws RemoteException;

    void onDabDLSPlusChanged(RadioDabInfo.DLPlusInfo dLPlusInfo) throws RemoteException;

    void onDabEPGChanged(RadioDabInfo.EPGInfo ePGInfo) throws RemoteException;

    void onDabMainInfoChanged(RadioDabInfo.MainInfo mainInfo) throws RemoteException;

    void onDabServiceFollowingNotify(int i) throws RemoteException;

    void onDabServiceInformationListChanged(RadioDabInfo.ServiceInformationList serviceInformationList) throws RemoteException;

    void onDabServiceLogoChanged(RadioDabInfo.ServiceLogoInfo serviceLogoInfo) throws RemoteException;

    void onDabSlideShowChanged(RadioDabInfo.SlideShowInfo slideShowInfo) throws RemoteException;

    void onDabStationListInfoChanged(RadioDabInfo.StationListInfo stationListInfo) throws RemoteException;

    void onError(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IDabCallback {
        private static final String DESCRIPTOR = "android.hardware.radio.IDabCallback";
        static final int TRANSACTION_onDabAnnouncementsStatusChanged = 10;
        static final int TRANSACTION_onDabCTChanged = 8;
        static final int TRANSACTION_onDabDLSChanged = 4;
        static final int TRANSACTION_onDabDLSPlusChanged = 5;
        static final int TRANSACTION_onDabEPGChanged = 7;
        static final int TRANSACTION_onDabMainInfoChanged = 2;
        static final int TRANSACTION_onDabServiceFollowingNotify = 9;
        static final int TRANSACTION_onDabServiceInformationListChanged = 12;
        static final int TRANSACTION_onDabServiceLogoChanged = 11;
        static final int TRANSACTION_onDabSlideShowChanged = 6;
        static final int TRANSACTION_onDabStationListInfoChanged = 3;
        static final int TRANSACTION_onError = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDabCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IDabCallback)) {
                return (IDabCallback) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    onError(parcel.readInt());
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabMainInfoChanged(parcel.readInt() != 0 ? RadioDabInfo.MainInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabStationListInfoChanged(parcel.readInt() != 0 ? RadioDabInfo.StationListInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabDLSChanged(parcel.createCharArray());
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabDLSPlusChanged(parcel.readInt() != 0 ? RadioDabInfo.DLPlusInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabSlideShowChanged(parcel.readInt() != 0 ? RadioDabInfo.SlideShowInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabEPGChanged(parcel.readInt() != 0 ? RadioDabInfo.EPGInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabCTChanged(parcel.readInt() != 0 ? RadioDabInfo.CTInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabServiceFollowingNotify(parcel.readInt());
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabAnnouncementsStatusChanged(parcel.readInt() != 0 ? RadioDabInfo.AnnouncementStatusInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabServiceLogoChanged(parcel.readInt() != 0 ? RadioDabInfo.ServiceLogoInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    onDabServiceInformationListChanged(parcel.readInt() != 0 ? RadioDabInfo.ServiceInformationList.CREATOR.createFromParcel(parcel) : null);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IDabCallback {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.hardware.radio.IDabCallback
            public void onError(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabMainInfoChanged(RadioDabInfo.MainInfo mainInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mainInfo != null) {
                        parcelObtain.writeInt(1);
                        mainInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabStationListInfoChanged(RadioDabInfo.StationListInfo stationListInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (stationListInfo != null) {
                        parcelObtain.writeInt(1);
                        stationListInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabDLSChanged(char[] cArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeCharArray(cArr);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabDLSPlusChanged(RadioDabInfo.DLPlusInfo dLPlusInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (dLPlusInfo != null) {
                        parcelObtain.writeInt(1);
                        dLPlusInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabSlideShowChanged(RadioDabInfo.SlideShowInfo slideShowInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (slideShowInfo != null) {
                        parcelObtain.writeInt(1);
                        slideShowInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabEPGChanged(RadioDabInfo.EPGInfo ePGInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ePGInfo != null) {
                        parcelObtain.writeInt(1);
                        ePGInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabCTChanged(RadioDabInfo.CTInfo cTInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cTInfo != null) {
                        parcelObtain.writeInt(1);
                        cTInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabServiceFollowingNotify(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabAnnouncementsStatusChanged(RadioDabInfo.AnnouncementStatusInfo announcementStatusInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (announcementStatusInfo != null) {
                        parcelObtain.writeInt(1);
                        announcementStatusInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(10, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabServiceLogoChanged(RadioDabInfo.ServiceLogoInfo serviceLogoInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (serviceLogoInfo != null) {
                        parcelObtain.writeInt(1);
                        serviceLogoInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.radio.IDabCallback
            public void onDabServiceInformationListChanged(RadioDabInfo.ServiceInformationList serviceInformationList) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (serviceInformationList != null) {
                        parcelObtain.writeInt(1);
                        serviceInformationList.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.mRemote.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
