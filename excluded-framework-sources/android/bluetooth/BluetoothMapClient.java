package android.bluetooth;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothMapClient implements BluetoothProfile {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_EXT_INSTANCE_INFORMATION = "android.bluetooth.mapmce.profile.action.ext.INSTANCE_INFORMATION";
    public static final String ACTION_EXT_MESSAGE_DELETED_STATUS_CHANGED = "android.bluetooth.mapmce.profile.action.ext.MESSAGE_DELETED_STATUS_CHANGED";
    public static final String ACTION_EXT_MESSAGE_READ_STATUS_CHANGED = "android.bluetooth.mapmce.profile.action.ext.MESSAGE_READ_STATUS_CHANGED";
    public static final String ACTION_MESSAGE_DELIVERED_SUCCESSFULLY = "android.bluetooth.mapmce.profile.action.MESSAGE_DELIVERED_SUCCESSFULLY";
    public static final String ACTION_MESSAGE_RECEIVED = "android.bluetooth.mapmce.profile.action.MESSAGE_RECEIVED";
    public static final String ACTION_MESSAGE_SENT_SUCCESSFULLY = "android.bluetooth.mapmce.profile.action.MESSAGE_SENT_SUCCESSFULLY";
    public static final int DELETED = 1;
    public static final String EXTRA_FOLDER = "android.bluetooth.mapmce.profile.extra.FOLDER";
    public static final String EXTRA_INSTANCE_ID = "android.bluetooth.mapmce.profile.extra.INSTANCE_ID";
    public static final String EXTRA_INSTANCE_NAME = "android.bluetooth.mapmce.profile.extra.INSTANCE_NAME";
    public static final String EXTRA_MESSAGE_HANDLE = "android.bluetooth.mapmce.profile.extra.MESSAGE_HANDLE";
    public static final String EXTRA_OWNER_UCI = "android.bluetooth.mapmce.profile.extra.OWNER_UCI";
    public static final String EXTRA_READ_STATUS = "android.bluetooth.mapmce.profile.extra.READ_STATUS";
    public static final String EXTRA_RECIPIENT_CONTACT_NAME = "android.bluetooth.mapmce.profile.extra.RECIPIENT_CONTACT_NAME";
    public static final String EXTRA_RECIPIENT_CONTACT_URI = "android.bluetooth.mapmce.profile.extra.RECIPIENT_CONTACT_URI";
    public static final String EXTRA_SENDER_CONTACT_NAME = "android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_NAME";
    public static final String EXTRA_SENDER_CONTACT_URI = "android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_URI";
    public static final String EXTRA_SUPPORTED_TYPE = "android.bluetooth.mapmce.profile.extra.SUPPORTED_TYPE";
    public static final String EXTRA_TYPE = "android.bluetooth.mapmce.profile.extra.TYPE";
    public static final int READ = 0;
    public static final int RESULT_CANCELED = 2;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int STATE_ERROR = -1;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothMapClient.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            if (BluetoothMapClient.DBG) {
                Log.d(BluetoothMapClient.TAG, "onBluetoothStateChange: up=" + z);
            }
            if (!z) {
                if (BluetoothMapClient.VDBG) {
                    Log.d(BluetoothMapClient.TAG, "Unbinding service...");
                }
                synchronized (BluetoothMapClient.this.mConnection) {
                    try {
                        BluetoothMapClient.this.mService = null;
                        BluetoothMapClient.this.mContext.unbindService(BluetoothMapClient.this.mConnection);
                    } catch (Exception e) {
                        Log.e(BluetoothMapClient.TAG, "", e);
                    }
                }
                return;
            }
            synchronized (BluetoothMapClient.this.mConnection) {
                try {
                    if (BluetoothMapClient.this.mService == null) {
                        if (BluetoothMapClient.VDBG) {
                            Log.d(BluetoothMapClient.TAG, "Binding service...");
                        }
                        BluetoothMapClient.this.doBind();
                    }
                } catch (Exception e2) {
                    Log.e(BluetoothMapClient.TAG, "", e2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothMapClient.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (BluetoothMapClient.DBG) {
                Log.d(BluetoothMapClient.TAG, "Proxy object connected");
            }
            BluetoothMapClient.this.mService = IBluetoothMapClient.Stub.asInterface(iBinder);
            if (BluetoothMapClient.this.mServiceListener != null) {
                BluetoothMapClient.this.mServiceListener.onServiceConnected(18, BluetoothMapClient.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (BluetoothMapClient.DBG) {
                Log.d(BluetoothMapClient.TAG, "Proxy object disconnected");
            }
            BluetoothMapClient.this.mService = null;
            if (BluetoothMapClient.this.mServiceListener != null) {
                BluetoothMapClient.this.mServiceListener.onServiceDisconnected(18);
            }
        }
    };
    private final Context mContext;
    private volatile IBluetoothMapClient mService;
    private BluetoothProfile.ServiceListener mServiceListener;
    private static final String TAG = "BluetoothMapClient";
    private static final boolean DBG = Log.isLoggable(TAG, 3);
    private static final boolean VDBG = Log.isLoggable(TAG, 2);

    BluetoothMapClient(Context context, BluetoothProfile.ServiceListener serviceListener) {
        if (DBG) {
            Log.d(TAG, "Create BluetoothMapClient proxy object");
        }
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

    boolean doBind() {
        Intent intent = new Intent(IBluetoothMapClient.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth MAP MCE Service with " + intent);
        return false;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public void close() {
        IBluetoothManager bluetoothManager = this.mAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
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
    }

    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        if (VDBG) {
            Log.d(TAG, "isConnected(" + bluetoothDevice + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null) {
            try {
                return iBluetoothMapClient.isConnected(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        if (!DBG) {
            return false;
        }
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        if (DBG) {
            Log.d(TAG, "connect(" + bluetoothDevice + ")for MAPS MCE");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null) {
            try {
                return iBluetoothMapClient.connect(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        if (!DBG) {
            return false;
        }
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        if (DBG) {
            Log.d(TAG, "disconnect(" + bluetoothDevice + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothMapClient.disconnect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothMapClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        if (DBG) {
            Log.d(TAG, "getConnectedDevices()");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled()) {
            try {
                return iBluetoothMapClient.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothMapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        if (DBG) {
            Log.d(TAG, "getDevicesMatchingStates()");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled()) {
            try {
                return iBluetoothMapClient.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothMapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        if (DBG) {
            Log.d(TAG, "getConnectionState(" + bluetoothDevice + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothMapClient.getConnectionState(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothMapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        if (DBG) {
            Log.d(TAG, "setPriority(" + bluetoothDevice + ", " + i + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothMapClient == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        if (i != 0 && i != 100) {
            return false;
        }
        try {
            return iBluetoothMapClient.setPriority(bluetoothDevice, i);
        } catch (RemoteException unused) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        if (VDBG) {
            Log.d(TAG, "getPriority(" + bluetoothDevice + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothMapClient.getPriority(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothMapClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public boolean sendMessage(BluetoothDevice bluetoothDevice, Uri[] uriArr, String str, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        if (DBG) {
            Log.d(TAG, "sendMessage(" + bluetoothDevice + ", " + uriArr + ", " + str);
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothMapClient.sendMessage(bluetoothDevice, uriArr, str, pendingIntent, pendingIntent2);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        return false;
    }

    public boolean getUnreadMessages(BluetoothDevice bluetoothDevice) {
        if (DBG) {
            Log.d(TAG, "getUnreadMessages(" + bluetoothDevice + ")");
        }
        IBluetoothMapClient iBluetoothMapClient = this.mService;
        if (iBluetoothMapClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothMapClient.getUnreadMessages(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        return false;
    }

    private boolean isEnabled() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.getState() == 12) {
            return true;
        }
        if (!DBG) {
            return false;
        }
        Log.d(TAG, "Bluetooth is Not enabled");
        return false;
    }

    private static boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }
}
