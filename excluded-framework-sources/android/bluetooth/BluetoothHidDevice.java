package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothHidDevice implements BluetoothProfile {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED";
    public static final byte ERROR_RSP_INVALID_PARAM = 4;
    public static final byte ERROR_RSP_INVALID_RPT_ID = 2;
    public static final byte ERROR_RSP_NOT_READY = 1;
    public static final byte ERROR_RSP_SUCCESS = 0;
    public static final byte ERROR_RSP_UNKNOWN = 14;
    public static final byte ERROR_RSP_UNSUPPORTED_REQ = 3;
    public static final byte PROTOCOL_BOOT_MODE = 0;
    public static final byte PROTOCOL_REPORT_MODE = 1;
    public static final byte REPORT_TYPE_FEATURE = 3;
    public static final byte REPORT_TYPE_INPUT = 1;
    public static final byte REPORT_TYPE_OUTPUT = 2;
    public static final byte SUBCLASS1_COMBO = -64;
    public static final byte SUBCLASS1_KEYBOARD = 64;
    public static final byte SUBCLASS1_MOUSE = -128;
    public static final byte SUBCLASS1_NONE = 0;
    public static final byte SUBCLASS2_CARD_READER = 6;
    public static final byte SUBCLASS2_DIGITIZER_TABLET = 5;
    public static final byte SUBCLASS2_GAMEPAD = 2;
    public static final byte SUBCLASS2_JOYSTICK = 1;
    public static final byte SUBCLASS2_REMOTE_CONTROL = 3;
    public static final byte SUBCLASS2_SENSING_DEVICE = 4;
    public static final byte SUBCLASS2_UNCATEGORIZED = 0;
    private static final String TAG = BluetoothHidDevice.class.getSimpleName();
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothHidDevice.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothHidDevice.TAG, "onBluetoothStateChange: up=" + z);
            synchronized (BluetoothHidDevice.this.mConnection) {
                if (z) {
                    try {
                        try {
                            if (BluetoothHidDevice.this.mService == null) {
                                Log.d(BluetoothHidDevice.TAG, "Binding HID Device service...");
                                BluetoothHidDevice.this.doBind();
                            }
                        } catch (IllegalStateException e) {
                            Log.e(BluetoothHidDevice.TAG, "onBluetoothStateChange: could not bind to HID Dev service: ", e);
                        }
                    } catch (SecurityException e2) {
                        Log.e(BluetoothHidDevice.TAG, "onBluetoothStateChange: could not bind to HID Dev service: ", e2);
                    }
                } else {
                    Log.d(BluetoothHidDevice.TAG, "Unbinding service...");
                    BluetoothHidDevice.this.doUnbind();
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothHidDevice.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(BluetoothHidDevice.TAG, "onServiceConnected()");
            BluetoothHidDevice.this.mService = IBluetoothHidDevice.Stub.asInterface(iBinder);
            if (BluetoothHidDevice.this.mServiceListener != null) {
                BluetoothHidDevice.this.mServiceListener.onServiceConnected(19, BluetoothHidDevice.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(BluetoothHidDevice.TAG, "onServiceDisconnected()");
            BluetoothHidDevice.this.mService = null;
            if (BluetoothHidDevice.this.mServiceListener != null) {
                BluetoothHidDevice.this.mServiceListener.onServiceDisconnected(19);
            }
        }
    };
    private Context mContext;
    private volatile IBluetoothHidDevice mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    public static abstract class Callback {
        private static final String TAG = "BluetoothHidDevCallback";

        public void onAppStatusChanged(BluetoothDevice bluetoothDevice, boolean z) {
            Log.d(TAG, "onAppStatusChanged: pluggedDevice=" + bluetoothDevice + " registered=" + z);
        }

        public void onConnectionStateChanged(BluetoothDevice bluetoothDevice, int i) {
            Log.d(TAG, "onConnectionStateChanged: device=" + bluetoothDevice + " state=" + i);
        }

        public void onGetReport(BluetoothDevice bluetoothDevice, byte b, byte b2, int i) {
            Log.d(TAG, "onGetReport: device=" + bluetoothDevice + " type=" + ((int) b) + " id=" + ((int) b2) + " bufferSize=" + i);
        }

        public void onSetReport(BluetoothDevice bluetoothDevice, byte b, byte b2, byte[] bArr) {
            Log.d(TAG, "onSetReport: device=" + bluetoothDevice + " type=" + ((int) b) + " id=" + ((int) b2));
        }

        public void onSetProtocol(BluetoothDevice bluetoothDevice, byte b) {
            Log.d(TAG, "onSetProtocol: device=" + bluetoothDevice + " protocol=" + ((int) b));
        }

        public void onInterruptData(BluetoothDevice bluetoothDevice, byte b, byte[] bArr) {
            Log.d(TAG, "onInterruptData: device=" + bluetoothDevice + " reportId=" + ((int) b));
        }

        public void onVirtualCableUnplug(BluetoothDevice bluetoothDevice) {
            Log.d(TAG, "onVirtualCableUnplug: device=" + bluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CallbackWrapper extends IBluetoothHidDeviceCallback.Stub {
        private final Callback mCallback;
        private final Executor mExecutor;

        CallbackWrapper(Executor executor, Callback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onAppStatusChanged(final BluetoothDevice bluetoothDevice, final boolean z) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$NFluHjT4zTfYBRXClu_2k6mPKFI
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAppStatusChanged$0$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, z);
                }
            });
        }

        public /* synthetic */ void lambda$onAppStatusChanged$0$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, boolean z) {
            this.mCallback.onAppStatusChanged(bluetoothDevice, z);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onConnectionStateChanged(final BluetoothDevice bluetoothDevice, final int i) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$qtStwQVkGfOs2iJIiePWqJJpi0w
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onConnectionStateChanged$1$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, i);
                }
            });
        }

        public /* synthetic */ void lambda$onConnectionStateChanged$1$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, int i) {
            this.mCallback.onConnectionStateChanged(bluetoothDevice, i);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onGetReport(final BluetoothDevice bluetoothDevice, final byte b, final byte b2, final int i) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$Eyz_qG6mvTlh6a8Bp41ZoEJzQCQ
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onGetReport$2$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, b, b2, i);
                }
            });
        }

        public /* synthetic */ void lambda$onGetReport$2$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, byte b, byte b2, int i) {
            this.mCallback.onGetReport(bluetoothDevice, b, b2, i);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onSetReport(final BluetoothDevice bluetoothDevice, final byte b, final byte b2, final byte[] bArr) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$3bTGVlfKj7Y0SZdifW_Ya2myDKs
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSetReport$3$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, b, b2, bArr);
                }
            });
        }

        public /* synthetic */ void lambda$onSetReport$3$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, byte b, byte b2, byte[] bArr) {
            this.mCallback.onSetReport(bluetoothDevice, b, b2, bArr);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onSetProtocol(final BluetoothDevice bluetoothDevice, final byte b) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$ypkr5GGxsAkGSBiLjIRwg-PzqCM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onSetProtocol$4$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, b);
                }
            });
        }

        public /* synthetic */ void lambda$onSetProtocol$4$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, byte b) {
            this.mCallback.onSetProtocol(bluetoothDevice, b);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onInterruptData(final BluetoothDevice bluetoothDevice, final byte b, final byte[] bArr) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$xW99-tc95OmGApoKnpQ9q1TXb9k
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onInterruptData$5$BluetoothHidDevice$CallbackWrapper(bluetoothDevice, b, bArr);
                }
            });
        }

        public /* synthetic */ void lambda$onInterruptData$5$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice, byte b, byte[] bArr) {
            this.mCallback.onInterruptData(bluetoothDevice, b, bArr);
        }

        @Override // android.bluetooth.IBluetoothHidDeviceCallback
        public void onVirtualCableUnplug(final BluetoothDevice bluetoothDevice) {
            clearCallingIdentity();
            this.mExecutor.execute(new Runnable() { // from class: android.bluetooth.-$$Lambda$BluetoothHidDevice$CallbackWrapper$jiodzbAJAcleQCwlDcBjvDddELM
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onVirtualCableUnplug$6$BluetoothHidDevice$CallbackWrapper(bluetoothDevice);
                }
            });
        }

        public /* synthetic */ void lambda$onVirtualCableUnplug$6$BluetoothHidDevice$CallbackWrapper(BluetoothDevice bluetoothDevice) {
            this.mCallback.onVirtualCableUnplug(bluetoothDevice);
        }
    }

    BluetoothHidDevice(Context context, BluetoothProfile.ServiceListener serviceListener) {
        this.mContext = context;
        this.mServiceListener = serviceListener;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAdapter = defaultAdapter;
        IBluetoothManager bluetoothManager = defaultAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.registerStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        doBind();
    }

    boolean doBind() {
        Intent intent = new Intent(IBluetoothHidDevice.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                Log.d(TAG, "Bound to HID Device Service");
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth HID Device Service with " + intent);
        return false;
    }

    void doUnbind() {
        if (this.mService != null) {
            this.mService = null;
            try {
                this.mContext.unbindService(this.mConnection);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Unable to unbind HidDevService", e);
            }
        }
    }

    void close() {
        IBluetoothManager bluetoothManager = this.mAdapter.getBluetoothManager();
        if (bluetoothManager != null) {
            try {
                bluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        synchronized (this.mConnection) {
            doUnbind();
        }
        this.mServiceListener = null;
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.getConnectedDevices();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.getConnectionState(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return 0;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        return 0;
    }

    public boolean registerApp(BluetoothHidDeviceAppSdpSettings bluetoothHidDeviceAppSdpSettings, BluetoothHidDeviceAppQosSettings bluetoothHidDeviceAppQosSettings, BluetoothHidDeviceAppQosSettings bluetoothHidDeviceAppQosSettings2, Executor executor, Callback callback) {
        if (bluetoothHidDeviceAppSdpSettings == null) {
            throw new IllegalArgumentException("sdp parameter cannot be null");
        }
        if (executor == null) {
            throw new IllegalArgumentException("executor parameter cannot be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback parameter cannot be null");
        }
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.registerApp(bluetoothHidDeviceAppSdpSettings, bluetoothHidDeviceAppQosSettings, bluetoothHidDeviceAppQosSettings2, new CallbackWrapper(executor, callback));
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean unregisterApp() {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.unregisterApp();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean sendReport(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.sendReport(bluetoothDevice, i, bArr);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean replyReport(BluetoothDevice bluetoothDevice, byte b, byte b2, byte[] bArr) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.replyReport(bluetoothDevice, b, b2, bArr);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean reportError(BluetoothDevice bluetoothDevice, byte b) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.reportError(bluetoothDevice, b);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public String getUserAppName() {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.getUserAppName();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return "";
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        return "";
    }

    public boolean connect(BluetoothDevice bluetoothDevice) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.connect(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        IBluetoothHidDevice iBluetoothHidDevice = this.mService;
        if (iBluetoothHidDevice != null) {
            try {
                return iBluetoothHidDevice.disconnect(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }
}
