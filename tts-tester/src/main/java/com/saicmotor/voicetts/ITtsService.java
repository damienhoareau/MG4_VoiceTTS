package com.saicmotor.voicetts;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes3.dex */
public interface ITtsService extends IInterface {

    public static class Default implements ITtsService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWords(String str, boolean z, String str2) throws RemoteException {
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsByLang(String str, int i, boolean z, String str2) throws RemoteException {
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsByLangWithStatus(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void promptCommonWordsWithStatus(String str, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
        }

        @Override // com.saicmotor.voicetts.ITtsService
        public void stopPrompt() throws RemoteException {
        }
    }

    void promptCommonWords(String str, boolean z, String str2) throws RemoteException;

    void promptCommonWordsByLang(String str, int i, boolean z, String str2) throws RemoteException;

    void promptCommonWordsByLangWithStatus(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException;

    void promptCommonWordsWithStatus(String str, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException;

    void stopPrompt() throws RemoteException;

    public static abstract class Stub extends Binder implements ITtsService {
        private static final String DESCRIPTOR = "com.saicmotor.voicetts.ITtsService";
        static final int TRANSACTION_promptCommonWords = 1;
        static final int TRANSACTION_promptCommonWordsByLang = 4;
        static final int TRANSACTION_promptCommonWordsByLangWithStatus = 5;
        static final int TRANSACTION_promptCommonWordsWithStatus = 2;
        static final int TRANSACTION_stopPrompt = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITtsService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ITtsService)) {
                return (ITtsService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                promptCommonWords(parcel.readString(), parcel.readInt() != 0, parcel.readString());
                parcel2.writeNoException();
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                promptCommonWordsWithStatus(parcel.readString(), parcel.readInt() != 0, parcel.readString(), IPromptCallBack.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                stopPrompt();
                parcel2.writeNoException();
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                promptCommonWordsByLang(parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readString());
                parcel2.writeNoException();
                return true;
            }
            if (i != 5) {
                if (i == 1598968902) {
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel.enforceInterface(DESCRIPTOR);
            promptCommonWordsByLangWithStatus(parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readString(), IPromptCallBack.Stub.asInterface(parcel.readStrongBinder()));
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy implements ITtsService {
            public static ITtsService sDefaultImpl;
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

            @Override // com.saicmotor.voicetts.ITtsService
            public void promptCommonWords(String str, boolean z, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeString(str2);
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().promptCommonWords(str, z, str2);
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.saicmotor.voicetts.ITtsService
            public void promptCommonWordsWithStatus(String str, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeStrongBinder(iPromptCallBack != null ? iPromptCallBack.asBinder() : null);
                    if (!this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().promptCommonWordsWithStatus(str, z, str2, iPromptCallBack);
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.saicmotor.voicetts.ITtsService
            public void stopPrompt() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().stopPrompt();
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.saicmotor.voicetts.ITtsService
            public void promptCommonWordsByLang(String str, int i, boolean z, String str2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeString(str2);
                    if (!this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().promptCommonWordsByLang(str, i, z, str2);
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.saicmotor.voicetts.ITtsService
            public void promptCommonWordsByLangWithStatus(String str, int i, boolean z, String str2, IPromptCallBack iPromptCallBack) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeStrongBinder(iPromptCallBack != null ? iPromptCallBack.asBinder() : null);
                    if (!this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().promptCommonWordsByLangWithStatus(str, i, z, str2, iPromptCallBack);
                    } else {
                        parcelObtain2.readException();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ITtsService iTtsService) {
            if (Proxy.sDefaultImpl != null || iTtsService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iTtsService;
            return true;
        }

        public static ITtsService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
