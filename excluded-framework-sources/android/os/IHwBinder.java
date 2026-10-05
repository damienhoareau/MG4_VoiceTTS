package android.os;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public interface IHwBinder {

    @SystemApi
    public interface DeathRecipient {
        @SystemApi
        void serviceDied(long j);
    }

    @SystemApi
    boolean linkToDeath(DeathRecipient deathRecipient, long j);

    @SystemApi
    IHwInterface queryLocalInterface(String str);

    @SystemApi
    void transact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException;

    @SystemApi
    boolean unlinkToDeath(DeathRecipient deathRecipient);
}
