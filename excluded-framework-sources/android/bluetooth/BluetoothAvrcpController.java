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
public final class BluetoothAvrcpController implements BluetoothProfile {
    public static final String ACTION_ADDRESSED_PLAYER_CHANGED = "android.bluetooth.avrcp-controller.profile.action.ADDRESSED_PLAYER_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.avrcp-controller.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYER_SETTING = "android.bluetooth.avrcp-controller.profile.action.PLAYER_SETTING";
    public static final String ACTION_UIDS_EVENT = "android.bluetooth.avrcp-controller.profile.action.UIDS_EVENT";
    public static final int BTRC_FEAT_ABSOLUTE_VOLUME = 2;
    public static final int BTRC_FEAT_APP_SETTING = 16;
    public static final int BTRC_FEAT_BROWSE = 4;
    public static final int BTRC_FEAT_COVER_ART = 8;
    public static final int BTRC_FEAT_METADATA = 1;
    public static final int BTRC_FEAT_NONE = 0;
    private static final boolean DBG = true;
    public static final String EXTRA_ADDRESSED_PLAYER_ID = "android.bluetooth.avrcp-controller.profile.extra.ADDRESSED_PLAYER_ID";
    public static final String EXTRA_PLAYER_SETTING = "android.bluetooth.avrcp-controller.profile.extra.PLAYER_SETTING";
    private static final String TAG = "BluetoothAvrcpController";
    private static final boolean VDBG = true;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothAvrcpController.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothAvrcpController.TAG, "onBluetoothStateChange: up=" + z);
            if (z) {
                synchronized (BluetoothAvrcpController.this.mConnection) {
                    try {
                        if (BluetoothAvrcpController.this.mService == null) {
                            Log.d(BluetoothAvrcpController.TAG, "Binding service...");
                            BluetoothAvrcpController.this.doBind();
                        }
                    } catch (Exception e) {
                        Log.e(BluetoothAvrcpController.TAG, "", e);
                    }
                }
                return;
            }
            Log.d(BluetoothAvrcpController.TAG, "Unbinding service...");
            synchronized (BluetoothAvrcpController.this.mConnection) {
                try {
                    BluetoothAvrcpController.this.mService = null;
                    BluetoothAvrcpController.this.mContext.unbindService(BluetoothAvrcpController.this.mConnection);
                } catch (Exception e2) {
                    Log.e(BluetoothAvrcpController.TAG, "", e2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothAvrcpController.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(BluetoothAvrcpController.TAG, "Proxy object connected");
            BluetoothAvrcpController.this.mService = IBluetoothAvrcpController.Stub.asInterface(Binder.allowBlocking(iBinder));
            if (BluetoothAvrcpController.this.mServiceListener != null) {
                BluetoothAvrcpController.this.mServiceListener.onServiceConnected(12, BluetoothAvrcpController.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(BluetoothAvrcpController.TAG, "Proxy object disconnected");
            BluetoothAvrcpController.this.mService = null;
            if (BluetoothAvrcpController.this.mServiceListener != null) {
                BluetoothAvrcpController.this.mServiceListener.onServiceDisconnected(12);
            }
        }
    };
    private Context mContext;
    private volatile IBluetoothAvrcpController mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothAvrcpController(Context context, BluetoothProfile.ServiceListener serviceListener) {
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
        Intent intent = new Intent(IBluetoothAvrcpController.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth AVRCP Controller Service with " + intent);
        return false;
    }

    void close() {
        this.mServiceListener = null;
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
    }

    public void finalize() {
        close();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices()");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController != null && isEnabled()) {
            try {
                return iBluetoothAvrcpController.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothAvrcpController == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        log("getDevicesMatchingStates()");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController != null && isEnabled()) {
            try {
                return iBluetoothAvrcpController.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothAvrcpController == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        log("getState(" + bluetoothDevice + ")");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothAvrcpController == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return 0;
        }
        try {
            return iBluetoothAvrcpController.getConnectionState(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return 0;
        }
    }

    public BluetoothAvrcpPlayerSettings getPlayerSettings(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "getPlayerSettings");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController == null || !isEnabled()) {
            return null;
        }
        try {
            return iBluetoothAvrcpController.getPlayerSettings(bluetoothDevice);
        } catch (RemoteException e) {
            Log.e(TAG, "Error talking to BT service in getMetadata() " + e);
            return null;
        }
    }

    public boolean setPlayerApplicationSetting(BluetoothAvrcpPlayerSettings bluetoothAvrcpPlayerSettings) {
        Log.d(TAG, "setPlayerApplicationSetting");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController == null || !isEnabled()) {
            if (iBluetoothAvrcpController == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            return iBluetoothAvrcpController.setPlayerApplicationSetting(bluetoothAvrcpPlayerSettings);
        } catch (RemoteException e) {
            Log.e(TAG, "Error talking to BT service in setPlayerApplicationSetting() " + e);
            return false;
        }
    }

    public void sendGroupNavigationCmd(BluetoothDevice bluetoothDevice, int i, int i2) {
        Log.d(TAG, "sendGroupNavigationCmd dev = " + bluetoothDevice + " key " + i + " State = " + i2);
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController == null || !isEnabled()) {
            if (iBluetoothAvrcpController == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
        } else {
            try {
                iBluetoothAvrcpController.sendGroupNavigationCmd(bluetoothDevice, i, i2);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in sendGroupNavigationCmd()", e);
            }
        }
    }

    public int getSupportedFeatures(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "getSupportedFeatures dev = " + bluetoothDevice);
        if (this.mService != null && isEnabled()) {
            try {
                Log.d(TAG, "getSupportedFeatures  = " + this.mService.getSupportedFeatures(bluetoothDevice));
                return this.mService.getSupportedFeatures(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in getSupportedFeatures()", e);
                return 0;
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public void startFetchingAlbumArt(String str, int i, int i2, long j) {
        Log.d(TAG, "startFetchingAlbumArt dev ");
        IBluetoothAvrcpController iBluetoothAvrcpController = this.mService;
        if (iBluetoothAvrcpController == null || !isEnabled()) {
            if (iBluetoothAvrcpController == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
        } else {
            try {
                iBluetoothAvrcpController.startFetchingAlbumArt(str, i, i2, j);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in startFetchingAlbumArt()", e);
            }
        }
    }

    private boolean isEnabled() {
        return this.mAdapter.getState() == 12;
    }

    private static boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }

    private static void log(String str) {
        Log.d(TAG, str);
    }
}
