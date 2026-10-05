package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telecom.Logging.Session;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class ConnectionServiceAdapterServant {
    private static final int MSG_ADD_CONFERENCE_CALL = 10;
    private static final int MSG_ADD_EXISTING_CONNECTION = 21;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_RELEASED = 35;
    private static final int MSG_HANDLE_CREATE_CONNECTION_COMPLETE = 1;
    private static final int MSG_ON_CONNECTION_EVENT = 26;
    private static final int MSG_ON_POST_DIAL_CHAR = 22;
    private static final int MSG_ON_POST_DIAL_WAIT = 12;
    private static final int MSG_ON_RTT_INITIATION_FAILURE = 31;
    private static final int MSG_ON_RTT_INITIATION_SUCCESS = 30;
    private static final int MSG_ON_RTT_REMOTELY_TERMINATED = 32;
    private static final int MSG_ON_RTT_UPGRADE_REQUEST = 33;
    private static final int MSG_PUT_EXTRAS = 24;
    private static final int MSG_QUERY_REMOTE_CALL_SERVICES = 13;
    private static final int MSG_REMOVE_CALL = 11;
    private static final int MSG_REMOVE_EXTRAS = 25;
    private static final int MSG_SET_ACTIVE = 2;
    private static final int MSG_SET_ADDRESS = 18;
    private static final int MSG_SET_AUDIO_ROUTE = 29;
    private static final int MSG_SET_CALLER_DISPLAY_NAME = 19;
    private static final int MSG_SET_CONFERENCEABLE_CONNECTIONS = 20;
    private static final int MSG_SET_CONFERENCE_MERGE_FAILED = 23;
    private static final int MSG_SET_CONNECTION_CAPABILITIES = 8;
    private static final int MSG_SET_CONNECTION_PROPERTIES = 27;
    private static final int MSG_SET_DIALING = 4;
    private static final int MSG_SET_DISCONNECTED = 5;
    private static final int MSG_SET_IS_CONFERENCED = 9;
    private static final int MSG_SET_IS_VOIP_AUDIO_MODE = 16;
    private static final int MSG_SET_ON_HOLD = 6;
    private static final int MSG_SET_PHONE_ACCOUNT_CHANGED = 34;
    private static final int MSG_SET_PULLING = 28;
    private static final int MSG_SET_RINGBACK_REQUESTED = 7;
    private static final int MSG_SET_RINGING = 3;
    private static final int MSG_SET_STATUS_HINTS = 17;
    private static final int MSG_SET_VIDEO_CALL_PROVIDER = 15;
    private static final int MSG_SET_VIDEO_STATE = 14;
    private final IConnectionServiceAdapter mDelegate;
    private final Handler mHandler = new Handler() { // from class: android.telecom.ConnectionServiceAdapterServant.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                internalHandleMessage(message);
            } catch (RemoteException unused) {
            }
        }

        private void internalHandleMessage(Message message) throws RemoteException {
            switch (message.what) {
                case 1:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.handleCreateConnectionComplete((String) someArgs.arg1, (ConnectionRequest) someArgs.arg2, (ParcelableConnection) someArgs.arg3, null);
                        return;
                    } finally {
                        someArgs.recycle();
                    }
                case 2:
                    ConnectionServiceAdapterServant.this.mDelegate.setActive((String) message.obj, null);
                    return;
                case 3:
                    ConnectionServiceAdapterServant.this.mDelegate.setRinging((String) message.obj, null);
                    return;
                case 4:
                    ConnectionServiceAdapterServant.this.mDelegate.setDialing((String) message.obj, null);
                    return;
                case 5:
                    SomeArgs someArgs2 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setDisconnected((String) someArgs2.arg1, (DisconnectCause) someArgs2.arg2, null);
                        return;
                    } finally {
                        someArgs2.recycle();
                    }
                case 6:
                    ConnectionServiceAdapterServant.this.mDelegate.setOnHold((String) message.obj, null);
                    return;
                case 7:
                    ConnectionServiceAdapterServant.this.mDelegate.setRingbackRequested((String) message.obj, message.arg1 == 1, null);
                    return;
                case 8:
                    ConnectionServiceAdapterServant.this.mDelegate.setConnectionCapabilities((String) message.obj, message.arg1, null);
                    return;
                case 9:
                    SomeArgs someArgs3 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setIsConferenced((String) someArgs3.arg1, (String) someArgs3.arg2, null);
                        return;
                    } finally {
                        someArgs3.recycle();
                    }
                case 10:
                    SomeArgs someArgs4 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.addConferenceCall((String) someArgs4.arg1, (ParcelableConference) someArgs4.arg2, null);
                        return;
                    } finally {
                        someArgs4.recycle();
                    }
                case 11:
                    ConnectionServiceAdapterServant.this.mDelegate.removeCall((String) message.obj, null);
                    return;
                case 12:
                    SomeArgs someArgs5 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPostDialWait((String) someArgs5.arg1, (String) someArgs5.arg2, null);
                        return;
                    } finally {
                        someArgs5.recycle();
                    }
                case 13:
                    ConnectionServiceAdapterServant.this.mDelegate.queryRemoteConnectionServices((RemoteServiceCallback) message.obj, null);
                    return;
                case 14:
                    ConnectionServiceAdapterServant.this.mDelegate.setVideoState((String) message.obj, message.arg1, null);
                    return;
                case 15:
                    SomeArgs someArgs6 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setVideoProvider((String) someArgs6.arg1, (IVideoProvider) someArgs6.arg2, null);
                        return;
                    } finally {
                        someArgs6.recycle();
                    }
                case 16:
                    ConnectionServiceAdapterServant.this.mDelegate.setIsVoipAudioMode((String) message.obj, message.arg1 == 1, null);
                    return;
                case 17:
                    SomeArgs someArgs7 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setStatusHints((String) someArgs7.arg1, (StatusHints) someArgs7.arg2, null);
                        return;
                    } finally {
                        someArgs7.recycle();
                    }
                case 18:
                    SomeArgs someArgs8 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setAddress((String) someArgs8.arg1, (Uri) someArgs8.arg2, someArgs8.argi1, null);
                        return;
                    } finally {
                        someArgs8.recycle();
                    }
                case 19:
                    SomeArgs someArgs9 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setCallerDisplayName((String) someArgs9.arg1, (String) someArgs9.arg2, someArgs9.argi1, null);
                        return;
                    } finally {
                        someArgs9.recycle();
                    }
                case 20:
                    SomeArgs someArgs10 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setConferenceableConnections((String) someArgs10.arg1, (List) someArgs10.arg2, null);
                        return;
                    } finally {
                        someArgs10.recycle();
                    }
                case 21:
                    SomeArgs someArgs11 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.addExistingConnection((String) someArgs11.arg1, (ParcelableConnection) someArgs11.arg2, null);
                        return;
                    } finally {
                        someArgs11.recycle();
                    }
                case 22:
                    SomeArgs someArgs12 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPostDialChar((String) someArgs12.arg1, (char) someArgs12.argi1, null);
                        return;
                    } finally {
                        someArgs12.recycle();
                    }
                case 23:
                    SomeArgs someArgs13 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setConferenceMergeFailed((String) someArgs13.arg1, null);
                        return;
                    } finally {
                        someArgs13.recycle();
                    }
                case 24:
                    SomeArgs someArgs14 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.putExtras((String) someArgs14.arg1, (Bundle) someArgs14.arg2, null);
                        return;
                    } finally {
                        someArgs14.recycle();
                    }
                case 25:
                    SomeArgs someArgs15 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.removeExtras((String) someArgs15.arg1, (List) someArgs15.arg2, null);
                        return;
                    } finally {
                        someArgs15.recycle();
                    }
                case 26:
                    SomeArgs someArgs16 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onConnectionEvent((String) someArgs16.arg1, (String) someArgs16.arg2, (Bundle) someArgs16.arg3, null);
                        return;
                    } finally {
                        someArgs16.recycle();
                    }
                case 27:
                    ConnectionServiceAdapterServant.this.mDelegate.setConnectionProperties((String) message.obj, message.arg1, null);
                    return;
                case 28:
                    ConnectionServiceAdapterServant.this.mDelegate.setPulling((String) message.obj, null);
                    return;
                case 29:
                    SomeArgs someArgs17 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setAudioRoute((String) someArgs17.arg1, someArgs17.argi1, (String) someArgs17.arg2, (Session.Info) someArgs17.arg3);
                        return;
                    } finally {
                        someArgs17.recycle();
                    }
                case 30:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttInitiationSuccess((String) message.obj, null);
                    return;
                case 31:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttInitiationFailure((String) message.obj, message.arg1, null);
                    return;
                case 32:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttSessionRemotelyTerminated((String) message.obj, null);
                    return;
                case 33:
                    ConnectionServiceAdapterServant.this.mDelegate.onRemoteRttRequest((String) message.obj, null);
                    return;
                case 34:
                    SomeArgs someArgs18 = (SomeArgs) message.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPhoneAccountChanged((String) someArgs18.arg1, (PhoneAccountHandle) someArgs18.arg2, null);
                        return;
                    } finally {
                        someArgs18.recycle();
                    }
                case 35:
                    ConnectionServiceAdapterServant.this.mDelegate.onConnectionServiceFocusReleased(null);
                    return;
                default:
                    return;
            }
        }
    };
    private final IConnectionServiceAdapter mStub = new IConnectionServiceAdapter.Stub() { // from class: android.telecom.ConnectionServiceAdapterServant.2
        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConnectionComplete(String str, ConnectionRequest connectionRequest, ParcelableConnection parcelableConnection, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = connectionRequest;
            someArgsObtain.arg3 = parcelableConnection;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(1, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setActive(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(2, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRinging(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(3, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDialing(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(4, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setPulling(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(28, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDisconnected(String str, DisconnectCause disconnectCause, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = disconnectCause;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(5, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setOnHold(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(6, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRingbackRequested(String str, boolean z, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(7, z ? 1 : 0, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionCapabilities(String str, int i, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(8, i, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionProperties(String str, int i, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(27, i, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceMergeFailed(String str, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(23, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsConferenced(String str, String str2, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = str2;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(9, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addConferenceCall(String str, ParcelableConference parcelableConference, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = parcelableConference;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(10, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeCall(String str, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(11, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialWait(String str, String str2, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = str2;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(12, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialChar(String str, char c, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.argi1 = c;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(22, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryRemoteConnectionServices(RemoteServiceCallback remoteServiceCallback, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(13, remoteServiceCallback).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoState(String str, int i, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(14, i, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoProvider(String str, IVideoProvider iVideoProvider, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = iVideoProvider;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(15, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setIsVoipAudioMode(String str, boolean z, Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(16, z ? 1 : 0, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setStatusHints(String str, StatusHints statusHints, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = statusHints;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(17, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setAddress(String str, Uri uri, int i, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = uri;
            someArgsObtain.argi1 = i;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(18, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setCallerDisplayName(String str, String str2, int i, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = str2;
            someArgsObtain.argi1 = i;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(19, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setConferenceableConnections(String str, List<String> list, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = list;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(20, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void addExistingConnection(String str, ParcelableConnection parcelableConnection, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = parcelableConnection;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(21, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void putExtras(String str, Bundle bundle, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = bundle;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(24, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void removeExtras(String str, List<String> list, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = list;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(25, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setAudioRoute(String str, int i, String str2, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.argi1 = i;
            someArgsObtain.arg2 = str2;
            someArgsObtain.arg3 = info;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(29, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void onConnectionEvent(String str, String str2, Bundle bundle, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = str2;
            someArgsObtain.arg3 = bundle;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(26, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationSuccess(String str, Session.Info info) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(30, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationFailure(String str, int i, Session.Info info) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(31, i, 0, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttSessionRemotelyTerminated(String str, Session.Info info) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(32, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRemoteRttRequest(String str, Session.Info info) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(33, str).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPhoneAccountChanged(String str, PhoneAccountHandle phoneAccountHandle, Session.Info info) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = str;
            someArgsObtain.arg2 = phoneAccountHandle;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(34, someArgsObtain).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionServiceFocusReleased(Session.Info info) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(35).sendToTarget();
        }
    };

    public ConnectionServiceAdapterServant(IConnectionServiceAdapter iConnectionServiceAdapter) {
        this.mDelegate = iConnectionServiceAdapter;
    }

    public IConnectionServiceAdapter getStub() {
        return this.mStub;
    }
}
