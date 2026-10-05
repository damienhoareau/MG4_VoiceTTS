package android.os;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ServiceManagerNative extends Binder implements IServiceManager {
    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    public static IServiceManager asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IServiceManager iServiceManager = (IServiceManager) iBinder.queryLocalInterface(IServiceManager.descriptor);
        return iServiceManager != null ? iServiceManager : new ServiceManagerProxy(iBinder);
    }

    public ServiceManagerNative() {
        attachInterface(this, IServiceManager.descriptor);
    }

    @Override // android.os.Binder
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        try {
            if (i == 1) {
                parcel.enforceInterface(IServiceManager.descriptor);
                parcel2.writeStrongBinder(getService(parcel.readString()));
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(IServiceManager.descriptor);
                parcel2.writeStrongBinder(checkService(parcel.readString()));
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(IServiceManager.descriptor);
                addService(parcel.readString(), parcel.readStrongBinder(), parcel.readInt() != 0, parcel.readInt());
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(IServiceManager.descriptor);
                parcel2.writeStringArray(listServices(parcel.readInt()));
                return true;
            }
            if (i == 6) {
                parcel.enforceInterface(IServiceManager.descriptor);
                setPermissionController(IPermissionController.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            return false;
        } catch (RemoteException unused) {
        }
    }
}
