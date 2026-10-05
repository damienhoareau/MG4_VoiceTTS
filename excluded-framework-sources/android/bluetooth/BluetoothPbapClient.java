package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothPbapClient implements BluetoothProfile {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PHONEBOOK_DOWNLOAD_STATE_CHANGED = "android.bluetooth.pbapclient.profile.action.PHONEBOOK_DOWNLOAD_STATE_CHANGED";
    public static final String ACTION_PHONEBOOK_GET_DOWNLOAD_SIZE_CHANGED = "android.bluetooth.pbapclient.profile.action.PHONEBOOK_GET_DOWNLOAD_SIZE_CHANGED";
    public static final String CCH_PATH = "telecom/cch.vcf";
    private static final boolean DBG = true;
    public static final int DOWNLOAD_COMPLETED = 1;
    public static final int DOWNLOAD_FAILED = 2;
    public static final int DOWNLOAD_IN_PROGRESS = 0;
    public static final String EXTRA_DOWNLOAD_RESULT = "android.bluetooth.pbapclient.profile.extra.DOWNLOAD_RESULT";
    public static final String EXTRA_DOWNLOAD_STATE = "android.bluetooth.pbapclient.profile.extra.DOWNLOAD_STATE";
    public static final String EXTRA_PHONEBOOK_ACCOUNT = "android.bluetooth.pbapclient.profile.extra.PHONEBOOK_ACCOUNT";
    public static final String EXTRA_PHONEBOOK_PATH = "android.bluetooth.pbapclient.profile.extra.PHONEBOOK_PATH";
    public static final String EXTRA_PHONEBOOK_SIZE = "android.bluetooth.pbapclient.profile.extra.PHONEBOOK_SIZE";
    public static final String FAV_PATH = "telecom/fav.vcf";
    public static final String ICH_PATH = "telecom/ich.vcf";
    public static final String MCH_PATH = "telecom/mch.vcf";
    public static final String OCH_PATH = "telecom/och.vcf";
    private static final long PBAP_FILTER_ADR = 32;
    private static final long PBAP_FILTER_EMAIL = 256;
    private static final long PBAP_FILTER_FN = 2;
    private static final long PBAP_FILTER_N = 4;
    private static final long PBAP_FILTER_NICKNAME = 8388608;
    private static final long PBAP_FILTER_PHOTO = 8;
    private static final long PBAP_FILTER_TEL = 128;
    private static final long PBAP_FILTER_VERSION = 1;
    public static final String PB_PATH = "telecom/pb.vcf";
    public static final int RESULT_CANCELED = 2;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_INVALID_PARAMETER = 3;
    public static final int RESULT_PHONEBOOK_DOWNLOADING = 2;
    public static final int RESULT_SEND_PHONEBOOK_DOWNLOAD_ERROR = 0;
    public static final int RESULT_SEND_PHONEBOOK_DOWNLOAD_SUCCESS = 1;
    public static final int RESULT_SUCCESS = 1;
    public static final String SIM1_CCH_PATH = "SIM1/telecom/cch.vcf";
    public static final String SIM1_ICH_PATH = "SIM1/telecom/ich.vcf";
    public static final String SIM1_MCH_PATH = "SIM1/telecom/mch.vcf";
    public static final String SIM1_OCH_PATH = "SIM1/telecom/och.vcf";
    public static final String SIM1_PB_PATH = "SIM1/telecom/pb.vcf";
    public static final int STATE_ERROR = -1;
    private static final String TAG = "BluetoothPbapClient";
    private static final boolean VDBG = true;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothPbapClient.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothPbapClient.TAG, "onBluetoothStateChange: PBAP CLIENT up=" + z);
            if (z) {
                synchronized (BluetoothPbapClient.this.mConnection) {
                    try {
                        if (BluetoothPbapClient.this.mService == null) {
                            Log.d(BluetoothPbapClient.TAG, "Binding service...");
                            BluetoothPbapClient.this.doBind();
                        }
                    } catch (Exception e) {
                        Log.e(BluetoothPbapClient.TAG, "", e);
                    }
                }
                return;
            }
            Log.d(BluetoothPbapClient.TAG, "Unbinding service...");
            synchronized (BluetoothPbapClient.this.mConnection) {
                try {
                    BluetoothPbapClient.this.mService = null;
                    BluetoothPbapClient.this.mContext.unbindService(BluetoothPbapClient.this.mConnection);
                } catch (Exception e2) {
                    Log.e(BluetoothPbapClient.TAG, "", e2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothPbapClient.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothPbapClient.log("Proxy object connected");
            BluetoothPbapClient.this.mService = IBluetoothPbapClient.Stub.asInterface(Binder.allowBlocking(iBinder));
            if (BluetoothPbapClient.this.mServiceListener != null) {
                BluetoothPbapClient.this.mServiceListener.onServiceConnected(17, BluetoothPbapClient.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            BluetoothPbapClient.log("Proxy object disconnected");
            BluetoothPbapClient.this.mService = null;
            if (BluetoothPbapClient.this.mServiceListener != null) {
                BluetoothPbapClient.this.mServiceListener.onServiceDisconnected(17);
            }
        }
    };
    private final Context mContext;
    private volatile IBluetoothPbapClient mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothPbapClient(Context context, BluetoothProfile.ServiceListener serviceListener) {
        Log.d(TAG, "Create BluetoothPbapClient proxy object");
        this.mContext = context;
        this.mServiceListener = serviceListener;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAdapter = defaultAdapter;
        IBluetoothManager bluetoothManager = defaultAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.registerStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        doBind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean doBind() {
        Intent intent = new Intent(IBluetoothPbapClient.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth PBAP Client Service with " + intent);
        return false;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x001a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:32:0x001f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public synchronized void close() {
        IBluetoothManager bluetoothManager = this.mAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
            synchronized (this.mConnection) {
                if (this.mService != null) {
                    try {
                        this.mService = null;
                        this.mContext.unbindService(this.mConnection);
                    } catch (Exception e2) {
                        Log.e(TAG, "", e2);
                    }
                }
            }
            this.mServiceListener = null;
        } else {
            synchronized (this.mConnection) {
                if (this.mService != null) {
                    this.mService = null;
                    this.mContext.unbindService(this.mConnection);
                }
                this.mServiceListener = null;
            }
        }
        throw th;
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ") for PBAP Client.");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.connect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")" + new Exception());
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothPbapClient == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            iBluetoothPbapClient.disconnect(bluetoothDevice);
            return true;
        } catch (RemoteException unused) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices()");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled()) {
            try {
                return iBluetoothPbapClient.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        log("getDevicesMatchingStates()");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled()) {
            try {
                return iBluetoothPbapClient.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        log("getConnectionState(" + bluetoothDevice + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.getConnectionState(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public int downloadPhoneBook(BluetoothDevice bluetoothDevice) {
        log("downloadPhoneBook(" + bluetoothDevice + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.downloadPhoneBook(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public int downloadCallLog(BluetoothDevice bluetoothDevice) {
        log("downloadCallLog(" + bluetoothDevice + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.downloadCallLog(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public int downloadFavoriteContacts(BluetoothDevice bluetoothDevice) {
        log("downloadFavoriteContacts(" + bluetoothDevice + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.downloadFavoriteContacts(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Log.d(TAG, str);
    }

    private boolean isEnabled() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.getState() == 12) {
            return true;
        }
        log("Bluetooth is Not enabled");
        return false;
    }

    private static boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        log("setPriority(" + bluetoothDevice + ", " + i + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothPbapClient == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        if (i != 0 && i != 100) {
            return false;
        }
        try {
            return iBluetoothPbapClient.setPriority(bluetoothDevice, i);
        } catch (RemoteException unused) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        log("getPriority(" + bluetoothDevice + ")");
        IBluetoothPbapClient iBluetoothPbapClient = this.mService;
        if (iBluetoothPbapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothPbapClient.getPriority(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothPbapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }
}
