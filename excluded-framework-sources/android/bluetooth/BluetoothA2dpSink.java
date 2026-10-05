package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.ims.ImsConferenceState;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothA2dpSink implements BluetoothProfile {
    public static final String ACTION_AUDIO_CONFIG_CHANGED = "android.bluetooth.a2dp-sink.profile.action.AUDIO_CONFIG_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.PLAYING_STATE_CHANGED";
    private static final boolean DBG = true;
    public static final String EXTRA_AUDIO_CONFIG = "android.bluetooth.a2dp-sink.profile.extra.AUDIO_CONFIG";
    public static final int STATE_NOT_PLAYING = 11;
    public static final int STATE_PLAYING = 10;
    private static final String TAG = "BluetoothA2dpSink";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothA2dpSink.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothA2dpSink.TAG, "onBluetoothStateChange: up=" + z);
            if (!z) {
                synchronized (BluetoothA2dpSink.this.mConnection) {
                    try {
                        BluetoothA2dpSink.this.mService = null;
                        BluetoothA2dpSink.this.mContext.unbindService(BluetoothA2dpSink.this.mConnection);
                    } catch (Exception e) {
                        Log.e(BluetoothA2dpSink.TAG, "", e);
                    }
                }
                return;
            }
            synchronized (BluetoothA2dpSink.this.mConnection) {
                try {
                    if (BluetoothA2dpSink.this.mService == null) {
                        BluetoothA2dpSink.this.doBind();
                    }
                } catch (Exception e2) {
                    Log.e(BluetoothA2dpSink.TAG, "", e2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothA2dpSink.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(BluetoothA2dpSink.TAG, "Proxy object connected");
            BluetoothA2dpSink.this.mService = IBluetoothA2dpSink.Stub.asInterface(Binder.allowBlocking(iBinder));
            if (BluetoothA2dpSink.this.mServiceListener != null) {
                BluetoothA2dpSink.this.mServiceListener.onServiceConnected(11, BluetoothA2dpSink.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(BluetoothA2dpSink.TAG, "Proxy object disconnected");
            BluetoothA2dpSink.this.mService = null;
            if (BluetoothA2dpSink.this.mServiceListener != null) {
                BluetoothA2dpSink.this.mServiceListener.onServiceDisconnected(11);
            }
        }
    };
    private Context mContext;
    private volatile IBluetoothA2dpSink mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothA2dpSink(Context context, BluetoothProfile.ServiceListener serviceListener) {
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
        Intent intent = new Intent(IBluetoothA2dpSink.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth A2DP Service with " + intent);
        return false;
    }

    void close() {
        Log.w(TAG, "close");
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

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ")");
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            return iBluetoothA2dpSink.connect(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")");
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            return iBluetoothA2dpSink.disconnect(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink != null && isEnabled()) {
            try {
                return iBluetoothA2dpSink.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothA2dpSink == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink != null && isEnabled()) {
            try {
                return iBluetoothA2dpSink.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothA2dpSink == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return 0;
        }
        try {
            return iBluetoothA2dpSink.getConnectionState(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return 0;
        }
    }

    public BluetoothAudioConfig getAudioConfig(BluetoothDevice bluetoothDevice) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return null;
        }
        try {
            return iBluetoothA2dpSink.getAudioConfig(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return null;
        }
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        log("setPriority(" + bluetoothDevice + ", " + i + ")");
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            return iBluetoothA2dpSink.setPriority(bluetoothDevice, 100);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return 0;
        }
        try {
            return iBluetoothA2dpSink.getPriority(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return 0;
        }
    }

    public boolean isA2dpPlaying(BluetoothDevice bluetoothDevice) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled() || !isValidDevice(bluetoothDevice)) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
            return false;
        }
        try {
            return iBluetoothA2dpSink.isA2dpPlaying(bluetoothDevice);
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    public void startA2dpRender(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "startA2dpRender dev = " + bluetoothDevice);
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled()) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
        } else {
            try {
                iBluetoothA2dpSink.startA2dpRender(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in startA2dpRender()", e);
            }
        }
    }

    public void stopA2dpRender(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "stopA2dpRender dev = " + bluetoothDevice);
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        if (iBluetoothA2dpSink == null || !isEnabled()) {
            if (iBluetoothA2dpSink == null) {
                Log.w(TAG, "Proxy not attached to service");
            }
        } else {
            try {
                iBluetoothA2dpSink.stopA2dpRender(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in stopA2dpRender()", e);
            }
        }
    }

    public boolean AutoRejectConn(boolean z) {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        Log.d(TAG, "AutoRejectConn(" + z + ")");
        if (iBluetoothA2dpSink != null && isEnabled()) {
            try {
                return iBluetoothA2dpSink.AutoRejectConn(z);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean getAutoRejectConnStatus() {
        IBluetoothA2dpSink iBluetoothA2dpSink = this.mService;
        Log.d(TAG, "getAutoRejectConnStatus()");
        if (iBluetoothA2dpSink != null && isEnabled()) {
            try {
                return iBluetoothA2dpSink.getAutoRejectConnStatus();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public static String stateToString(int i) {
        if (i == 0) {
            return ImsConferenceState.STATUS_DISCONNECTED;
        }
        if (i == 1) {
            return "connecting";
        }
        if (i == 2) {
            return "connected";
        }
        if (i == 3) {
            return ImsConferenceState.STATUS_DISCONNECTING;
        }
        if (i == 10) {
            return "playing";
        }
        if (i == 11) {
            return "not playing";
        }
        return "<unknown state " + i + ">";
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
