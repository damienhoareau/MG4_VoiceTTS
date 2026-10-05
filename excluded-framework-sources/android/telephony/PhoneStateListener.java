package android.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.internal.telephony.IPhoneStateListener;
import java.lang.ref.WeakReference;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class PhoneStateListener {
    private static final boolean DBG = false;
    public static final int LISTEN_CALL_FORWARDING_INDICATOR = 8;
    public static final int LISTEN_CALL_STATE = 32;
    public static final int LISTEN_CALL_STATUS = 393216;
    public static final int LISTEN_CARRIER_NETWORK_CHANGE = 65536;
    public static final int LISTEN_CELL_INFO = 1024;
    public static final int LISTEN_CELL_LOCATION = 16;
    public static final int LISTEN_DATA_ACTIVATION_STATE = 262144;
    public static final int LISTEN_DATA_ACTIVITY = 128;

    @Deprecated
    public static final int LISTEN_DATA_CONNECTION_REAL_TIME_INFO = 8192;
    public static final int LISTEN_DATA_CONNECTION_STATE = 64;
    public static final int LISTEN_MESSAGE_WAITING_INDICATOR = 4;
    public static final int LISTEN_NONE = 0;

    @Deprecated
    public static final int LISTEN_OEM_HOOK_RAW_EVENT = 32768;
    public static final int LISTEN_OTASP_CHANGED = 512;
    public static final int LISTEN_PHYSICAL_CHANNEL_CONFIGURATION = 1048576;
    public static final int LISTEN_PRECISE_CALL_STATE = 2048;
    public static final int LISTEN_PRECISE_DATA_CONNECTION_STATE = 4096;
    public static final int LISTEN_SERVICE_STATE = 1;

    @Deprecated
    public static final int LISTEN_SIGNAL_STRENGTH = 2;
    public static final int LISTEN_SIGNAL_STRENGTHS = 256;
    public static final int LISTEN_USER_MOBILE_DATA_STATE = 524288;
    public static final int LISTEN_VOICE_ACTIVATION_STATE = 131072;
    public static final int LISTEN_VOLTE_STATE = 16384;
    private static final String LOG_TAG = "PhoneStateListener";
    IPhoneStateListener callback;
    private final Handler mHandler;
    protected Integer mSubId;

    public void onCallForwardingIndicatorChanged(boolean z) {
    }

    public void onCallStateChanged(int i, String str) {
    }

    public void onCallStatusChanged(int i, int i2, String str) {
    }

    public void onCarrierNetworkChange(boolean z) {
    }

    public void onCellInfoChanged(List<CellInfo> list) {
    }

    public void onCellLocationChanged(CellLocation cellLocation) {
    }

    public void onDataActivationStateChanged(int i) {
    }

    public void onDataActivity(int i) {
    }

    public void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo dataConnectionRealTimeInfo) {
    }

    public void onDataConnectionStateChanged(int i) {
    }

    public void onDataConnectionStateChanged(int i, int i2) {
    }

    public void onMessageWaitingIndicatorChanged(boolean z) {
    }

    public void onOemHookRawEvent(byte[] bArr) {
    }

    public void onOtaspChanged(int i) {
    }

    public void onPhysicalChannelConfigurationChanged(List<PhysicalChannelConfig> list) {
    }

    public void onPreciseCallStateChanged(PreciseCallState preciseCallState) {
    }

    public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
    }

    public void onServiceStateChanged(ServiceState serviceState) {
    }

    @Deprecated
    public void onSignalStrengthChanged(int i) {
    }

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    }

    public void onUserMobileDataStateChanged(boolean z) {
    }

    public void onVoLteServiceStateChanged(VoLteServiceState voLteServiceState) {
    }

    public void onVoiceActivationStateChanged(int i) {
    }

    public PhoneStateListener() {
        this(null, Looper.myLooper());
    }

    public PhoneStateListener(Looper looper) {
        this(null, looper);
    }

    public PhoneStateListener(Integer num) {
        this(num, Looper.myLooper());
    }

    public PhoneStateListener(Integer num, Looper looper) {
        this.callback = new IPhoneStateListenerStub(this);
        this.mSubId = num;
        this.mHandler = new Handler(looper) { // from class: android.telephony.PhoneStateListener.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    PhoneStateListener.this.onServiceStateChanged((ServiceState) message.obj);
                }
                if (i == 2) {
                    PhoneStateListener.this.onSignalStrengthChanged(message.arg1);
                    return;
                }
                switch (i) {
                    case 4:
                        PhoneStateListener.this.onMessageWaitingIndicatorChanged(message.arg1 != 0);
                        break;
                    case 8:
                        PhoneStateListener.this.onCallForwardingIndicatorChanged(message.arg1 != 0);
                        break;
                    case 16:
                        PhoneStateListener.this.onCellLocationChanged((CellLocation) message.obj);
                        break;
                    case 32:
                        PhoneStateListener.this.onCallStateChanged(message.arg1, (String) message.obj);
                        break;
                    case 64:
                        PhoneStateListener.this.onDataConnectionStateChanged(message.arg1, message.arg2);
                        PhoneStateListener.this.onDataConnectionStateChanged(message.arg1);
                        break;
                    case 128:
                        PhoneStateListener.this.onDataActivity(message.arg1);
                        break;
                    case 256:
                        PhoneStateListener.this.onSignalStrengthsChanged((SignalStrength) message.obj);
                        break;
                    case 512:
                        PhoneStateListener.this.onOtaspChanged(message.arg1);
                        break;
                    case 1024:
                        PhoneStateListener.this.onCellInfoChanged((List) message.obj);
                        break;
                    case 2048:
                        PhoneStateListener.this.onPreciseCallStateChanged((PreciseCallState) message.obj);
                        break;
                    case 4096:
                        PhoneStateListener.this.onPreciseDataConnectionStateChanged((PreciseDataConnectionState) message.obj);
                        break;
                    case 8192:
                        PhoneStateListener.this.onDataConnectionRealTimeInfoChanged((DataConnectionRealTimeInfo) message.obj);
                        break;
                    case 16384:
                        PhoneStateListener.this.onVoLteServiceStateChanged((VoLteServiceState) message.obj);
                        break;
                    case 32768:
                        PhoneStateListener.this.onOemHookRawEvent((byte[]) message.obj);
                        break;
                    case 65536:
                        PhoneStateListener.this.onCarrierNetworkChange(((Boolean) message.obj).booleanValue());
                        break;
                    case 131072:
                        PhoneStateListener.this.onVoiceActivationStateChanged(((Integer) message.obj).intValue());
                        break;
                    case 262144:
                        PhoneStateListener.this.onDataActivationStateChanged(((Integer) message.obj).intValue());
                        break;
                    case 393216:
                        PhoneStateListener.this.onCallStatusChanged(message.arg1, message.arg2, (String) message.obj);
                        break;
                    case 524288:
                        PhoneStateListener.this.onUserMobileDataStateChanged(((Boolean) message.obj).booleanValue());
                        break;
                    case 1048576:
                        PhoneStateListener.this.onPhysicalChannelConfigurationChanged((List) message.obj);
                        break;
                }
            }
        };
    }

    private static class IPhoneStateListenerStub extends IPhoneStateListener.Stub {
        private WeakReference<PhoneStateListener> mPhoneStateListenerWeakRef;

        public IPhoneStateListenerStub(PhoneStateListener phoneStateListener) {
            this.mPhoneStateListenerWeakRef = new WeakReference<>(phoneStateListener);
        }

        private void send(int i, int i2, int i3, Object obj) {
            PhoneStateListener phoneStateListener = this.mPhoneStateListenerWeakRef.get();
            if (phoneStateListener != null) {
                Message.obtain(phoneStateListener.mHandler, i, i2, i3, obj).sendToTarget();
            }
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            send(1, 0, 0, serviceState);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthChanged(int i) {
            send(2, i, 0, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onMessageWaitingIndicatorChanged(boolean z) {
            send(4, z ? 1 : 0, 0, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallForwardingIndicatorChanged(boolean z) {
            send(8, z ? 1 : 0, 0, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellLocationChanged(Bundle bundle) {
            send(16, 0, 0, CellLocation.newFromBundle(bundle));
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallStateChanged(int i, String str) {
            send(32, i, 0, str);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallStatusChanged(int i, int i2, String str) {
            send(393216, i, i2, str);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataConnectionStateChanged(int i, int i2) {
            send(64, i, i2, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataActivity(int i) {
            send(128, i, 0, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            send(256, 0, 0, signalStrength);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOtaspChanged(int i) {
            send(512, i, 0, null);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellInfoChanged(List<CellInfo> list) {
            send(1024, 0, 0, list);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPreciseCallStateChanged(PreciseCallState preciseCallState) {
            send(2048, 0, 0, preciseCallState);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
            send(4096, 0, 0, preciseDataConnectionState);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo dataConnectionRealTimeInfo) {
            send(8192, 0, 0, dataConnectionRealTimeInfo);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onVoLteServiceStateChanged(VoLteServiceState voLteServiceState) {
            send(16384, 0, 0, voLteServiceState);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onVoiceActivationStateChanged(int i) {
            send(131072, 0, 0, Integer.valueOf(i));
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataActivationStateChanged(int i) {
            send(262144, 0, 0, Integer.valueOf(i));
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onUserMobileDataStateChanged(boolean z) {
            send(524288, 0, 0, Boolean.valueOf(z));
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOemHookRawEvent(byte[] bArr) {
            send(32768, 0, 0, bArr);
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCarrierNetworkChange(boolean z) {
            send(65536, 0, 0, Boolean.valueOf(z));
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPhysicalChannelConfigurationChanged(List<PhysicalChannelConfig> list) {
            send(1048576, 0, 0, list);
        }
    }

    private void log(String str) {
        Rlog.d(LOG_TAG, str);
    }
}
