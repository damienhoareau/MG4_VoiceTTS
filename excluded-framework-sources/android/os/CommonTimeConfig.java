package android.os;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes2.dex */
public class CommonTimeConfig {
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -4;
    public static final int ERROR_DEAD_OBJECT = -7;
    public static final long INVALID_GROUP_ID = -1;
    private static final int METHOD_FORCE_NETWORKLESS_MASTER_MODE = 17;
    private static final int METHOD_GET_AUTO_DISABLE = 15;
    private static final int METHOD_GET_CLIENT_SYNC_INTERVAL = 11;
    private static final int METHOD_GET_INTERFACE_BINDING = 7;
    private static final int METHOD_GET_MASTER_ANNOUNCE_INTERVAL = 9;
    private static final int METHOD_GET_MASTER_ELECTION_ENDPOINT = 3;
    private static final int METHOD_GET_MASTER_ELECTION_GROUP_ID = 5;
    private static final int METHOD_GET_MASTER_ELECTION_PRIORITY = 1;
    private static final int METHOD_GET_PANIC_THRESHOLD = 13;
    private static final int METHOD_SET_AUTO_DISABLE = 16;
    private static final int METHOD_SET_CLIENT_SYNC_INTERVAL = 12;
    private static final int METHOD_SET_INTERFACE_BINDING = 8;
    private static final int METHOD_SET_MASTER_ANNOUNCE_INTERVAL = 10;
    private static final int METHOD_SET_MASTER_ELECTION_ENDPOINT = 4;
    private static final int METHOD_SET_MASTER_ELECTION_GROUP_ID = 6;
    private static final int METHOD_SET_MASTER_ELECTION_PRIORITY = 2;
    private static final int METHOD_SET_PANIC_THRESHOLD = 14;
    public static final String SERVICE_NAME = "common_time.config";
    public static final int SUCCESS = 0;
    private String mInterfaceDesc;
    private IBinder mRemote;
    private CommonTimeUtils mUtils;
    private final Object mListenerLock = new Object();
    private OnServerDiedListener mServerDiedListener = null;
    private IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient() { // from class: android.os.CommonTimeConfig.1
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (CommonTimeConfig.this.mListenerLock) {
                if (CommonTimeConfig.this.mServerDiedListener != null) {
                    CommonTimeConfig.this.mServerDiedListener.onServerDied();
                }
            }
        }
    };

    public interface OnServerDiedListener {
        void onServerDied();
    }

    public CommonTimeConfig() throws RemoteException {
        this.mRemote = null;
        this.mInterfaceDesc = "";
        IBinder service = ServiceManager.getService(SERVICE_NAME);
        this.mRemote = service;
        if (service == null) {
            throw new RemoteException();
        }
        this.mInterfaceDesc = service.getInterfaceDescriptor();
        this.mUtils = new CommonTimeUtils(this.mRemote, this.mInterfaceDesc);
        this.mRemote.linkToDeath(this.mDeathHandler, 0);
    }

    public static CommonTimeConfig create() {
        try {
            return new CommonTimeConfig();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public void release() {
        IBinder iBinder = this.mRemote;
        if (iBinder != null) {
            try {
                iBinder.unlinkToDeath(this.mDeathHandler, 0);
            } catch (NoSuchElementException unused) {
            }
            this.mRemote = null;
        }
        this.mUtils = null;
    }

    public byte getMasterElectionPriority() throws RemoteException {
        throwOnDeadServer();
        return (byte) this.mUtils.transactGetInt(1, -1);
    }

    public int setMasterElectionPriority(byte b) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetInt(2, b);
    }

    public InetSocketAddress getMasterElectionEndpoint() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetSockaddr(3);
    }

    public int setMasterElectionEndpoint(InetSocketAddress inetSocketAddress) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetSockaddr(4, inetSocketAddress);
    }

    public long getMasterElectionGroupId() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetLong(5, -1L);
    }

    public int setMasterElectionGroupId(long j) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetLong(6, j);
    }

    public String getInterfaceBinding() throws RemoteException {
        throwOnDeadServer();
        String strTransactGetString = this.mUtils.transactGetString(7, null);
        if (strTransactGetString == null || strTransactGetString.length() != 0) {
            return strTransactGetString;
        }
        return null;
    }

    public int setNetworkBinding(String str) {
        if (checkDeadServer()) {
            return -7;
        }
        CommonTimeUtils commonTimeUtils = this.mUtils;
        if (str == null) {
            str = "";
        }
        return commonTimeUtils.transactSetString(8, str);
    }

    public int getMasterAnnounceInterval() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetInt(9, -1);
    }

    public int setMasterAnnounceInterval(int i) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetInt(10, i);
    }

    public int getClientSyncInterval() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetInt(11, -1);
    }

    public int setClientSyncInterval(int i) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetInt(12, i);
    }

    public int getPanicThreshold() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetInt(13, -1);
    }

    public int setPanicThreshold(int i) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetInt(14, i);
    }

    public boolean getAutoDisable() throws RemoteException {
        throwOnDeadServer();
        return 1 == this.mUtils.transactGetInt(15, 1);
    }

    public int setAutoDisable(boolean z) {
        if (checkDeadServer()) {
            return -7;
        }
        return this.mUtils.transactSetInt(16, z ? 1 : 0);
    }

    public int forceNetworklessMasterMode() {
        int i;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(this.mInterfaceDesc);
            this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
            i = parcelObtain2.readInt();
        } catch (RemoteException unused) {
            i = -7;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
        return i;
    }

    public void setServerDiedListener(OnServerDiedListener onServerDiedListener) {
        synchronized (this.mListenerLock) {
            this.mServerDiedListener = onServerDiedListener;
        }
    }

    protected void finalize() throws Throwable {
        release();
    }

    private boolean checkDeadServer() {
        return this.mRemote == null || this.mUtils == null;
    }

    private void throwOnDeadServer() throws RemoteException {
        if (checkDeadServer()) {
            throw new RemoteException();
        }
    }
}
