package android.hardware;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IRl78Service extends IInterface {
    int Rl78_connect() throws RemoteException;

    void Rl78_disconnect() throws RemoteException;

    void eol_check_consistency(byte[] bArr) throws RemoteException;

    byte[] eol_get_consistency_result() throws RemoteException;

    byte[] eol_tp_calibrate() throws RemoteException;

    byte[] eol_tp_getver() throws RemoteException;

    byte[] getRl78PN(int i) throws RemoteException;

    byte[] getTpPN(int i) throws RemoteException;

    byte[] getVer() throws RemoteException;

    public static abstract class Stub extends Binder implements IRl78Service {
        private static final String DESCRIPTOR = "android.hardware.IRl78Service";
        static final int TRANSACTION_Rl78_connect = 1;
        static final int TRANSACTION_Rl78_disconnect = 4;
        static final int TRANSACTION_eol_check_consistency = 6;
        static final int TRANSACTION_eol_get_consistency_result = 5;
        static final int TRANSACTION_eol_tp_calibrate = 7;
        static final int TRANSACTION_eol_tp_getver = 8;
        static final int TRANSACTION_getRl78PN = 3;
        static final int TRANSACTION_getTpPN = 9;
        static final int TRANSACTION_getVer = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRl78Service asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IRl78Service)) {
                return (IRl78Service) iInterfaceQueryLocalInterface;
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
                    int iRl78_connect = Rl78_connect();
                    parcel2.writeNoException();
                    parcel2.writeInt(iRl78_connect);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] ver = getVer();
                    parcel2.writeNoException();
                    parcel2.writeByteArray(ver);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] rl78PN = getRl78PN(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(rl78PN);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    Rl78_disconnect();
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] bArrEol_get_consistency_result = eol_get_consistency_result();
                    parcel2.writeNoException();
                    parcel2.writeByteArray(bArrEol_get_consistency_result);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    eol_check_consistency(parcel.createByteArray());
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] bArrEol_tp_calibrate = eol_tp_calibrate();
                    parcel2.writeNoException();
                    parcel2.writeByteArray(bArrEol_tp_calibrate);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] bArrEol_tp_getver = eol_tp_getver();
                    parcel2.writeNoException();
                    parcel2.writeByteArray(bArrEol_tp_getver);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] tpPN = getTpPN(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(tpPN);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IRl78Service {
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

            @Override // android.hardware.IRl78Service
            public int Rl78_connect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] getVer() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] getRl78PN(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public void Rl78_disconnect() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] eol_get_consistency_result() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public void eol_check_consistency(byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] eol_tp_calibrate() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] eol_tp_getver() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.hardware.IRl78Service
            public byte[] getTpPN(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
