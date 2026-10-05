package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.telephony.ims.ImsConferenceState;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothA2dp implements BluetoothProfile {
    public static final String ACTION_ACTIVE_DEVICE_CHANGED = "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED";
    public static final String ACTION_AVRCP_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.AVRCP_CONNECTION_STATE_CHANGED";
    public static final String ACTION_CODEC_CONFIG_CHANGED = "android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED";
    private static final boolean DBG = true;
    public static final int OPTIONAL_CODECS_NOT_SUPPORTED = 0;
    public static final int OPTIONAL_CODECS_PREF_DISABLED = 0;
    public static final int OPTIONAL_CODECS_PREF_ENABLED = 1;
    public static final int OPTIONAL_CODECS_PREF_UNKNOWN = -1;
    public static final int OPTIONAL_CODECS_SUPPORTED = 1;
    public static final int OPTIONAL_CODECS_SUPPORT_UNKNOWN = -1;
    public static final int STATE_NOT_PLAYING = 11;
    public static final int STATE_PLAYING = 10;
    private static final String TAG = "BluetoothA2dp";
    private static final boolean VDBG = false;
    private BluetoothAdapter mAdapter;
    private Context mContext;
    private IBluetoothA2dp mService;
    private BluetoothProfile.ServiceListener mServiceListener;
    private final ReentrantReadWriteLock mServiceLock = new ReentrantReadWriteLock();
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothA2dp.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothA2dp.TAG, "onBluetoothStateChange: up=" + z);
            try {
                try {
                    if (!z) {
                        try {
                            BluetoothA2dp.this.mServiceLock.writeLock().lock();
                            if (BluetoothA2dp.this.mService != null) {
                                BluetoothA2dp.this.mService = null;
                                BluetoothA2dp.this.mContext.unbindService(BluetoothA2dp.this.mConnection);
                            }
                        } catch (Exception e) {
                            Log.e(BluetoothA2dp.TAG, "", e);
                        }
                        return;
                    }
                    try {
                        BluetoothA2dp.this.mServiceLock.readLock().lock();
                        if (BluetoothA2dp.this.mService == null) {
                            BluetoothA2dp.this.doBind();
                        }
                    } catch (Exception e2) {
                        Log.e(BluetoothA2dp.TAG, "", e2);
                    }
                } finally {
                    BluetoothA2dp.this.mServiceLock.readLock().unlock();
                }
            } finally {
                BluetoothA2dp.this.mServiceLock.writeLock().unlock();
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothA2dp.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(BluetoothA2dp.TAG, "Proxy object connected");
            try {
                BluetoothA2dp.this.mServiceLock.writeLock().lock();
                BluetoothA2dp.this.mService = IBluetoothA2dp.Stub.asInterface(Binder.allowBlocking(iBinder));
                BluetoothA2dp.this.mServiceLock.writeLock().unlock();
                if (BluetoothA2dp.this.mServiceListener != null) {
                    BluetoothA2dp.this.mServiceListener.onServiceConnected(2, BluetoothA2dp.this);
                }
            } catch (Throwable th) {
                BluetoothA2dp.this.mServiceLock.writeLock().unlock();
                throw th;
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(BluetoothA2dp.TAG, "Proxy object disconnected");
            try {
                BluetoothA2dp.this.mServiceLock.writeLock().lock();
                BluetoothA2dp.this.mService = null;
                BluetoothA2dp.this.mServiceLock.writeLock().unlock();
                if (BluetoothA2dp.this.mServiceListener != null) {
                    BluetoothA2dp.this.mServiceListener.onServiceDisconnected(2);
                }
            } catch (Throwable th) {
                BluetoothA2dp.this.mServiceLock.writeLock().unlock();
                throw th;
            }
        }
    };

    public void finalize() {
    }

    BluetoothA2dp(Context context, BluetoothProfile.ServiceListener serviceListener) {
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
        Intent intent = new Intent(IBluetoothA2dp.class.getName());
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
        this.mServiceListener = null;
        IBluetoothManager bluetoothManager = this.mAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        try {
            try {
                this.mServiceLock.writeLock().lock();
                if (this.mService != null) {
                    this.mService = null;
                    this.mContext.unbindService(this.mConnection);
                }
            } finally {
                this.mServiceLock.writeLock().unlock();
            }
        } catch (Exception e2) {
            Log.e(TAG, "", e2);
        }
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.connect(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return false;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.disconnect(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return false;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        List<BluetoothDevice> arrayList;
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    arrayList = this.mService.getConnectedDevices();
                    return arrayList;
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return new ArrayList();
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                arrayList = new ArrayList<>();
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        List<BluetoothDevice> arrayList;
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    arrayList = this.mService.getDevicesMatchingConnectionStates(iArr);
                } else {
                    if (this.mService == null) {
                        Log.w(TAG, "Proxy not attached to service");
                    }
                    arrayList = new ArrayList<>();
                }
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                arrayList = new ArrayList<>();
            }
            return arrayList;
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.getConnectionState(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return 0;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean setActiveDevice(BluetoothDevice bluetoothDevice) {
        log("setActiveDevice(" + bluetoothDevice + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && (bluetoothDevice == null || isValidDevice(bluetoothDevice))) {
                    return this.mService.setActiveDevice(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return false;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public BluetoothDevice getActiveDevice() {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    return this.mService.getActiveDevice();
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return null;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        log("setPriority(" + bluetoothDevice + ", " + i + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    if (i == 0 || i == 100) {
                        return this.mService.setPriority(bluetoothDevice, i);
                    }
                } else if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
            return false;
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.getPriority(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return 0;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean isAvrcpAbsoluteVolumeSupported() {
        Log.d(TAG, "isAvrcpAbsoluteVolumeSupported");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    return this.mService.isAvrcpAbsoluteVolumeSupported();
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in isAvrcpAbsoluteVolumeSupported()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public void setAvrcpAbsoluteVolume(int i) {
        Log.d(TAG, "setAvrcpAbsoluteVolume");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    this.mService.setAvrcpAbsoluteVolume(i);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in setAvrcpAbsoluteVolume()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean isA2dpPlaying(BluetoothDevice bluetoothDevice) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.isA2dpPlaying(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return false;
            } catch (RemoteException unused) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public boolean shouldSendVolumeKeys(BluetoothDevice bluetoothDevice) {
        ParcelUuid[] uuids;
        if (!isEnabled() || !isValidDevice(bluetoothDevice) || (uuids = bluetoothDevice.getUuids()) == null) {
            return false;
        }
        for (ParcelUuid parcelUuid : uuids) {
            if (BluetoothUuid.isAvrcpTarget(parcelUuid)) {
                return true;
            }
        }
        return false;
    }

    public BluetoothCodecStatus getCodecStatus(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "getCodecStatus(" + bluetoothDevice + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    return this.mService.getCodecStatus(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return null;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in getCodecStatus()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public void setCodecConfigPreference(BluetoothDevice bluetoothDevice, BluetoothCodecConfig bluetoothCodecConfig) {
        Log.d(TAG, "setCodecConfigPreference(" + bluetoothDevice + ")");
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    this.mService.setCodecConfigPreference(bluetoothDevice, bluetoothCodecConfig);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in setCodecConfigPreference()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public void enableOptionalCodecs(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "enableOptionalCodecs(" + bluetoothDevice + ")");
        enableDisableOptionalCodecs(bluetoothDevice, true);
    }

    public void disableOptionalCodecs(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "disableOptionalCodecs(" + bluetoothDevice + ")");
        enableDisableOptionalCodecs(bluetoothDevice, false);
    }

    private void enableDisableOptionalCodecs(BluetoothDevice bluetoothDevice, boolean z) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled()) {
                    if (z) {
                        this.mService.enableOptionalCodecs(bluetoothDevice);
                    } else {
                        this.mService.disableOptionalCodecs(bluetoothDevice);
                    }
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in enableDisableOptionalCodecs()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public int supportsOptionalCodecs(BluetoothDevice bluetoothDevice) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.supportsOptionalCodecs(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return -1;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in getSupportsOptionalCodecs()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public int getOptionalCodecsEnabled(BluetoothDevice bluetoothDevice) {
        try {
            try {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    return this.mService.getOptionalCodecsEnabled(bluetoothDevice);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
                return -1;
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in getSupportsOptionalCodecs()", e);
            }
        } finally {
            this.mServiceLock.readLock().unlock();
        }
    }

    public void setOptionalCodecsEnabled(BluetoothDevice bluetoothDevice, int i) {
        try {
            if (i != -1 && i != 0 && i != 1) {
                Log.e(TAG, "Invalid value passed to setOptionalCodecsEnabled: " + i);
            } else {
                this.mServiceLock.readLock().lock();
                if (this.mService != null && isEnabled() && isValidDevice(bluetoothDevice)) {
                    this.mService.setOptionalCodecsEnabled(bluetoothDevice, i);
                }
                if (this.mService == null) {
                    Log.w(TAG, "Proxy not attached to service");
                }
            }
        } catch (RemoteException unused) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
        } finally {
            this.mServiceLock.readLock().unlock();
        }
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

    private boolean isValidDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && BluetoothAdapter.checkBluetoothAddress(bluetoothDevice.getAddress());
    }

    private static void log(String str) {
        Log.d(TAG, str);
    }
}
