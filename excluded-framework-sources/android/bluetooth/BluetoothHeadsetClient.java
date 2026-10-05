package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothHeadsetClient implements BluetoothProfile {
    public static final String ACTION_AG_EVENT = "android.bluetooth.headsetclient.profile.action.AG_EVENT";
    public static final String ACTION_AUDIO_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.AUDIO_STATE_CHANGED";
    public static final String ACTION_CALL_CHANGED = "android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED";
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_IN_BAND_RING_EVENT = "android.bluetooth.headsetclient.profile.action.IN_BAND_RING";
    public static final String ACTION_LAST_VTAG = "android.bluetooth.headsetclient.profile.action.LAST_VTAG";
    public static final String ACTION_MIC_VOLUME_CHANGED = "android.bluetooth.headsetclient.profile.action.MIC_VOLUME_CHANGED";
    public static final String ACTION_RESULT = "android.bluetooth.headsetclient.profile.action.RESULT";
    public static final int ACTION_RESULT_ERROR = 1;
    public static final int ACTION_RESULT_ERROR_BLACKLISTED = 6;
    public static final int ACTION_RESULT_ERROR_BUSY = 3;
    public static final int ACTION_RESULT_ERROR_CME = 7;
    public static final int ACTION_RESULT_ERROR_DELAYED = 5;
    public static final int ACTION_RESULT_ERROR_NO_ANSWER = 4;
    public static final int ACTION_RESULT_ERROR_NO_CARRIER = 2;
    public static final int ACTION_RESULT_OK = 0;
    public static final String ACTION_SPK_VOLUME_CHANGED = "android.bluetooth.headsetclient.profile.action.SPK_VOLUME_CHANGED";
    public static final int CALL_ACCEPT_HOLD = 1;
    public static final int CALL_ACCEPT_NONE = 0;
    public static final int CALL_ACCEPT_TERMINATE = 2;
    public static final int CALL_DIAL_ERROR = 3;
    public static final int CALL_DIAL_TIMEOUT = 2;
    public static final int CALL_NORMAL = 0;
    public static final int CALL_NO_RESPONSE = 1;
    public static final int CME_CORPORATE_PERSONALIZATION_PIN_REQUIRED = 46;
    public static final int CME_CORPORATE_PERSONALIZATION_PUK_REQUIRED = 47;
    public static final int CME_DIAL_STRING_TOO_LONG = 26;
    public static final int CME_EAP_NOT_SUPPORTED = 49;
    public static final int CME_EMERGENCY_SERVICE_ONLY = 32;
    public static final int CME_HIDDEN_KEY_REQUIRED = 48;
    public static final int CME_INCORRECT_PARAMETERS = 50;
    public static final int CME_INCORRECT_PASSWORD = 16;
    public static final int CME_INVALID_CHARACTER_IN_DIAL_STRING = 27;
    public static final int CME_INVALID_CHARACTER_IN_TEXT_STRING = 25;
    public static final int CME_INVALID_INDEX = 21;
    public static final int CME_MEMORY_FAILURE = 23;
    public static final int CME_MEMORY_FULL = 20;
    public static final int CME_NETWORK_PERSONALIZATION_PIN_REQUIRED = 40;
    public static final int CME_NETWORK_PERSONALIZATION_PUK_REQUIRED = 41;
    public static final int CME_NETWORK_SUBSET_PERSONALIZATION_PIN_REQUIRED = 42;
    public static final int CME_NETWORK_SUBSET_PERSONALIZATION_PUK_REQUIRED = 43;
    public static final int CME_NETWORK_TIMEOUT = 31;
    public static final int CME_NOT_FOUND = 22;
    public static final int CME_NOT_SUPPORTED_FOR_VOIP = 34;
    public static final int CME_NO_CONNECTION_TO_PHONE = 1;
    public static final int CME_NO_NETWORK_SERVICE = 30;
    public static final int CME_NO_SIMULTANOUS_VOIP_CS_CALLS = 33;
    public static final int CME_OPERATION_NOT_ALLOWED = 3;
    public static final int CME_OPERATION_NOT_SUPPORTED = 4;
    public static final int CME_PHFSIM_PIN_REQUIRED = 6;
    public static final int CME_PHFSIM_PUK_REQUIRED = 7;
    public static final int CME_PHONE_FAILURE = 0;
    public static final int CME_PHSIM_PIN_REQUIRED = 5;
    public static final int CME_SERVICE_PROVIDER_PERSONALIZATION_PIN_REQUIRED = 44;
    public static final int CME_SERVICE_PROVIDER_PERSONALIZATION_PUK_REQUIRED = 45;
    public static final int CME_SIM_BUSY = 14;
    public static final int CME_SIM_FAILURE = 13;
    public static final int CME_SIM_NOT_INSERTED = 10;
    public static final int CME_SIM_PIN2_REQUIRED = 17;
    public static final int CME_SIM_PIN_REQUIRED = 11;
    public static final int CME_SIM_PUK2_REQUIRED = 18;
    public static final int CME_SIM_PUK_REQUIRED = 12;
    public static final int CME_SIM_WRONG = 15;
    public static final int CME_SIP_RESPONSE_CODE = 35;
    public static final int CME_TEXT_STRING_TOO_LONG = 24;
    private static final boolean DBG = true;
    public static final String EXTRA_AG_FEATURE_3WAY_CALLING = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_3WAY_CALLING";
    public static final String EXTRA_AG_FEATURE_ACCEPT_HELD_OR_WAITING_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ACCEPT_HELD_OR_WAITING_CALL";
    public static final String EXTRA_AG_FEATURE_ATTACH_NUMBER_TO_VT = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ATTACH_NUMBER_TO_VT";
    public static final String EXTRA_AG_FEATURE_ECC = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_ECC";
    public static final String EXTRA_AG_FEATURE_MERGE = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_MERGE";
    public static final String EXTRA_AG_FEATURE_MERGE_AND_DETACH = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_MERGE_AND_DETACH";
    public static final String EXTRA_AG_FEATURE_REJECT_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_REJECT_CALL";
    public static final String EXTRA_AG_FEATURE_RELEASE_AND_ACCEPT = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RELEASE_AND_ACCEPT";
    public static final String EXTRA_AG_FEATURE_RELEASE_HELD_OR_WAITING_CALL = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RELEASE_HELD_OR_WAITING_CALL";
    public static final String EXTRA_AG_FEATURE_RESPONSE_AND_HOLD = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_RESPONSE_AND_HOLD";
    public static final String EXTRA_AG_FEATURE_VOICE_RECOGNITION = "android.bluetooth.headsetclient.extra.EXTRA_AG_FEATURE_VOICE_RECOGNITION";
    public static final String EXTRA_AUDIO_WBS = "android.bluetooth.headsetclient.extra.AUDIO_WBS";
    public static final String EXTRA_BATTERY_LEVEL = "android.bluetooth.headsetclient.extra.BATTERY_LEVEL";
    public static final String EXTRA_CALL = "android.bluetooth.headsetclient.extra.CALL";
    public static final String EXTRA_CALL_ERROR_CODE = "android.bluetooth.headsetclient.extra.CALL_ERROR_CODE";
    public static final String EXTRA_CME_CODE = "android.bluetooth.headsetclient.extra.CME_CODE";
    public static final String EXTRA_IN_BAND_RING = "android.bluetooth.headsetclient.extra.IN_BAND_RING";
    public static final String EXTRA_MANF_ID = "android.bluetooth.headsetclient.extra.MANF_ID";
    public static final String EXTRA_MANF_MODEL = "android.bluetooth.headsetclient.extra.MANF_MODEL";
    public static final String EXTRA_MIC_VOLUME = "android.bluetooth.headsetclient.extra.MIC_VOLUME";
    public static final String EXTRA_NETWORK_ROAMING = "android.bluetooth.headsetclient.extra.NETWORK_ROAMING";
    public static final String EXTRA_NETWORK_SIGNAL_STRENGTH = "android.bluetooth.headsetclient.extra.NETWORK_SIGNAL_STRENGTH";
    public static final String EXTRA_NETWORK_STATUS = "android.bluetooth.headsetclient.extra.NETWORK_STATUS";
    public static final String EXTRA_NUMBER = "android.bluetooth.headsetclient.extra.NUMBER";
    public static final String EXTRA_OPERATOR_NAME = "android.bluetooth.headsetclient.extra.OPERATOR_NAME";
    public static final String EXTRA_RESULT_CODE = "android.bluetooth.headsetclient.extra.RESULT_CODE";
    public static final String EXTRA_SPK_VOLUME = "android.bluetooth.headsetclient.extra.SPK_VOLUME";
    public static final String EXTRA_SUBSCRIBER_INFO = "android.bluetooth.headsetclient.extra.SUBSCRIBER_INFO";
    public static final String EXTRA_VOICE_RECOGNITION = "android.bluetooth.headsetclient.extra.VOICE_RECOGNITION";
    public static final int STATE_AUDIO_CONNECTED = 2;
    public static final int STATE_AUDIO_CONNECTING = 1;
    public static final int STATE_AUDIO_DISCONNECTED = 0;
    public static final int STATE_AUDIO_DISCONNECTING = 3;
    private static final String TAG = "BluetoothHeadsetClient";
    private static final boolean VDBG = true;
    private BluetoothAdapter mAdapter;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothHeadsetClient.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean z) {
            Log.d(BluetoothHeadsetClient.TAG, "onBluetoothStateChange: up=" + z);
            if (z) {
                synchronized (BluetoothHeadsetClient.this.mConnection) {
                    try {
                        if (BluetoothHeadsetClient.this.mService == null) {
                            Log.d(BluetoothHeadsetClient.TAG, "Binding service...");
                            new Intent(IBluetoothHeadsetClient.class.getName());
                            BluetoothHeadsetClient.this.doBind();
                        }
                    } catch (Exception e) {
                        Log.e(BluetoothHeadsetClient.TAG, "", e);
                    }
                }
                return;
            }
            Log.d(BluetoothHeadsetClient.TAG, "Unbinding service...");
            synchronized (BluetoothHeadsetClient.this.mConnection) {
                try {
                    BluetoothHeadsetClient.this.mService = null;
                    BluetoothHeadsetClient.this.mContext.unbindService(BluetoothHeadsetClient.this.mConnection);
                } catch (Exception e2) {
                    Log.e(BluetoothHeadsetClient.TAG, "", e2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothHeadsetClient.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(BluetoothHeadsetClient.TAG, "Proxy object connected");
            BluetoothHeadsetClient.this.mService = IBluetoothHeadsetClient.Stub.asInterface(Binder.allowBlocking(iBinder));
            if (BluetoothHeadsetClient.this.mServiceListener != null) {
                BluetoothHeadsetClient.this.mServiceListener.onServiceConnected(16, BluetoothHeadsetClient.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(BluetoothHeadsetClient.TAG, "Proxy object disconnected");
            BluetoothHeadsetClient.this.mService = null;
            if (BluetoothHeadsetClient.this.mServiceListener != null) {
                BluetoothHeadsetClient.this.mServiceListener.onServiceDisconnected(16);
            }
        }
    };
    private Context mContext;
    private volatile IBluetoothHeadsetClient mService;
    private BluetoothProfile.ServiceListener mServiceListener;

    BluetoothHeadsetClient(Context context, BluetoothProfile.ServiceListener serviceListener) {
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
        Intent intent = new Intent(IBluetoothHeadsetClient.class.getName());
        ComponentName componentNameResolveSystemService = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(componentNameResolveSystemService);
        if (componentNameResolveSystemService != null) {
            Context context = this.mContext;
            if (context.bindServiceAsUser(intent, this.mConnection, 0, context.getUser())) {
                return true;
            }
        }
        Log.e(TAG, "Could not bind to Bluetooth Headset Client Service with " + intent);
        return false;
    }

    void close() {
        log("close()");
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

    public boolean connect(BluetoothDevice bluetoothDevice) {
        log("connect(" + bluetoothDevice + ")");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.connect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public boolean disconnect(BluetoothDevice bluetoothDevice) {
        log("disconnect(" + bluetoothDevice + ")");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.disconnect(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getConnectedDevices();
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] iArr) {
        log("getDevicesMatchingStates()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getDevicesMatchingConnectionStates(iArr);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice bluetoothDevice) {
        log("getConnectionState(" + bluetoothDevice + ")");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.getConnectionState(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public boolean setPriority(BluetoothDevice bluetoothDevice, int i) {
        log("setPriority(" + bluetoothDevice + ", " + i + ")");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.setPriority(bluetoothDevice, 100);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return false;
    }

    public int getPriority(BluetoothDevice bluetoothDevice) {
        log("getPriority(" + bluetoothDevice + ")");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.getPriority(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return 0;
    }

    public boolean startVoiceRecognition(BluetoothDevice bluetoothDevice) {
        log("startVoiceRecognition()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.startVoiceRecognition(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean stopVoiceRecognition(BluetoothDevice bluetoothDevice) {
        log("stopVoiceRecognition()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.stopVoiceRecognition(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public List<BluetoothHeadsetClientCall> getCurrentCalls(BluetoothDevice bluetoothDevice) {
        log("getCurrentCalls()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.getCurrentCalls(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return null;
        }
        Log.w(TAG, "Proxy not attached to service");
        return null;
    }

    public Bundle getCurrentAgEvents(BluetoothDevice bluetoothDevice) {
        log("getCurrentCalls()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.getCurrentAgEvents(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return null;
        }
        Log.w(TAG, "Proxy not attached to service");
        return null;
    }

    public boolean startHfpRender(BluetoothDevice bluetoothDevice) {
        log("startHfpRender()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.startHfpRender(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean stopHfpRender(BluetoothDevice bluetoothDevice) {
        log("stopHfpRender()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.stopHfpRender(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean acceptCall(BluetoothDevice bluetoothDevice, int i) {
        log("acceptCall()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.acceptCall(bluetoothDevice, i);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean holdCall(BluetoothDevice bluetoothDevice) {
        log("holdCall()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.holdCall(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean rejectCall(BluetoothDevice bluetoothDevice) {
        log("rejectCall()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.rejectCall(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean terminateCall(BluetoothDevice bluetoothDevice, BluetoothHeadsetClientCall bluetoothHeadsetClientCall) {
        log("terminateCall()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.terminateCall(bluetoothDevice, bluetoothHeadsetClientCall);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean enterPrivateMode(BluetoothDevice bluetoothDevice, int i) {
        log("enterPrivateMode()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.enterPrivateMode(bluetoothDevice, i);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean explicitCallTransfer(BluetoothDevice bluetoothDevice) {
        log("explicitCallTransfer()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.explicitCallTransfer(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public BluetoothHeadsetClientCall dial(BluetoothDevice bluetoothDevice, String str) {
        log("dial()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.dial(bluetoothDevice, str);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return null;
        }
        Log.w(TAG, "Proxy not attached to service");
        return null;
    }

    public boolean dialMemory(BluetoothDevice bluetoothDevice, int i) {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        Log.d(TAG, "safirod added for AOSP addition function,reqHfpMemoryDial() " + i);
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.dialMemory(bluetoothDevice, i);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean sendDTMF(BluetoothDevice bluetoothDevice, byte b) {
        log("sendDTMF()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.sendDTMF(bluetoothDevice, b);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public boolean getLastVoiceTagNumber(BluetoothDevice bluetoothDevice) {
        log("getLastVoiceTagNumber()");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled() && isValidDevice(bluetoothDevice)) {
            try {
                return iBluetoothHeadsetClient.getLastVoiceTagNumber(bluetoothDevice);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient != null) {
            return false;
        }
        Log.w(TAG, "Proxy not attached to service");
        return false;
    }

    public int getAudioState(BluetoothDevice bluetoothDevice) {
        log("getAudioState");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getAudioState(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return 0;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return 0;
    }

    public void setAudioRouteAllowed(BluetoothDevice bluetoothDevice, boolean z) {
        log("setAudioRouteAllowed");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                iBluetoothHeadsetClient.setAudioRouteAllowed(bluetoothDevice, z);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
    }

    public boolean getAudioRouteAllowed(BluetoothDevice bluetoothDevice) {
        log("getAudioRouteAllowed");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getAudioRouteAllowed(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean setInBandRIngAllowed(BluetoothDevice bluetoothDevice, boolean z) {
        log("setInBandRIngAllowed");
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.setInBandRIngAllowed(bluetoothDevice, z);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean connectAudio(BluetoothDevice bluetoothDevice) {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.connectAudio(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean disconnectAudio(BluetoothDevice bluetoothDevice) {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.disconnectAudio(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public Bundle getCurrentAgFeatures(BluetoothDevice bluetoothDevice) {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getCurrentAgFeatures(bluetoothDevice);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return null;
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

    public void phoneFocusUpdate(int i) {
        Log.i(TAG, "phoneFocusUpdate() focusState" + i);
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                iBluetoothHeadsetClient.phoneFocusUpdate(i);
            } catch (RemoteException unused) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (iBluetoothHeadsetClient == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
    }

    public boolean AutoRejectConn(boolean z) {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        Log.d(TAG, "AutoRejectConn(" + z + ")");
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.AutoRejectConn(z);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean getAutoRejectConnStatus() {
        IBluetoothHeadsetClient iBluetoothHeadsetClient = this.mService;
        Log.d(TAG, "getAutoRejectConnStatus()");
        if (iBluetoothHeadsetClient != null && isEnabled()) {
            try {
                return iBluetoothHeadsetClient.getAutoRejectConnStatus();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }
}
