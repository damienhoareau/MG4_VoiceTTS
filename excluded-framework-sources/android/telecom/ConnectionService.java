package android.telecom;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.telecom.Logging.Runnable;
import android.telecom.Logging.Session;
import android.telephony.ims.ImsCallProfile;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IConnectionService;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ConnectionService extends Service {
    public static final String EXTRA_IS_HANDOVER = "android.telecom.extra.IS_HANDOVER";
    private static final int MSG_ABORT = 3;
    private static final int MSG_ADD_CONNECTION_SERVICE_ADAPTER = 1;
    private static final int MSG_ANSWER = 4;
    private static final int MSG_ANSWER_VIDEO = 17;
    private static final int MSG_CONFERENCE = 12;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_GAINED = 31;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_LOST = 30;
    private static final int MSG_CREATE_CONNECTION = 2;
    private static final int MSG_CREATE_CONNECTION_COMPLETE = 29;
    private static final int MSG_CREATE_CONNECTION_FAILED = 25;
    private static final int MSG_DEFLECT = 34;
    private static final int MSG_DISCONNECT = 6;
    private static final int MSG_HANDOVER_COMPLETE = 33;
    private static final int MSG_HANDOVER_FAILED = 32;
    private static final int MSG_HOLD = 7;
    private static final int MSG_MERGE_CONFERENCE = 18;
    private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 9;
    private static final int MSG_ON_EXTRAS_CHANGED = 24;
    private static final int MSG_ON_POST_DIAL_CONTINUE = 14;
    private static final int MSG_ON_START_RTT = 26;
    private static final int MSG_ON_STOP_RTT = 27;
    private static final int MSG_PLAY_DTMF_TONE = 10;
    private static final int MSG_PULL_EXTERNAL_CALL = 22;
    private static final int MSG_REJECT = 5;
    private static final int MSG_REJECT_WITH_MESSAGE = 20;
    private static final int MSG_REMOVE_CONNECTION_SERVICE_ADAPTER = 16;
    private static final int MSG_RTT_UPGRADE_RESPONSE = 28;
    private static final int MSG_SEND_CALL_EVENT = 23;
    private static final int MSG_SILENCE = 21;
    private static final int MSG_SPLIT_FROM_CONFERENCE = 13;
    private static final int MSG_STOP_DTMF_TONE = 11;
    private static final int MSG_SWAP_CONFERENCE = 19;
    private static final int MSG_UNHOLD = 8;
    private static final boolean PII_DEBUG = Log.isLoggable(3);
    public static final String SERVICE_INTERFACE = "android.telecom.ConnectionService";
    private static final String SESSION_ABORT = "CS.ab";
    private static final String SESSION_ADD_CS_ADAPTER = "CS.aCSA";
    private static final String SESSION_ANSWER = "CS.an";
    private static final String SESSION_ANSWER_VIDEO = "CS.anV";
    private static final String SESSION_CALL_AUDIO_SC = "CS.cASC";
    private static final String SESSION_CONFERENCE = "CS.c";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_GAINED = "CS.cSFG";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_LOST = "CS.cSFL";
    private static final String SESSION_CREATE_CONN = "CS.crCo";
    private static final String SESSION_CREATE_CONN_COMPLETE = "CS.crCoC";
    private static final String SESSION_CREATE_CONN_FAILED = "CS.crCoF";
    private static final String SESSION_DEFLECT = "CS.def";
    private static final String SESSION_DISCONNECT = "CS.d";
    private static final String SESSION_EXTRAS_CHANGED = "CS.oEC";
    private static final String SESSION_HANDLER = "H.";
    private static final String SESSION_HANDOVER_COMPLETE = "CS.hC";
    private static final String SESSION_HANDOVER_FAILED = "CS.haF";
    private static final String SESSION_HOLD = "CS.h";
    private static final String SESSION_MERGE_CONFERENCE = "CS.mC";
    private static final String SESSION_PLAY_DTMF = "CS.pDT";
    private static final String SESSION_POST_DIAL_CONT = "CS.oPDC";
    private static final String SESSION_PULL_EXTERNAL_CALL = "CS.pEC";
    private static final String SESSION_REJECT = "CS.r";
    private static final String SESSION_REJECT_MESSAGE = "CS.rWM";
    private static final String SESSION_REMOVE_CS_ADAPTER = "CS.rCSA";
    private static final String SESSION_RTT_UPGRADE_RESPONSE = "CS.rTRUR";
    private static final String SESSION_SEND_CALL_EVENT = "CS.sCE";
    private static final String SESSION_SILENCE = "CS.s";
    private static final String SESSION_SPLIT_CONFERENCE = "CS.sFC";
    private static final String SESSION_START_RTT = "CS.+RTT";
    private static final String SESSION_STOP_DTMF = "CS.sDT";
    private static final String SESSION_STOP_RTT = "CS.-RTT";
    private static final String SESSION_SWAP_CONFERENCE = "CS.sC";
    private static final String SESSION_UNHOLD = "CS.u";
    private static final String SESSION_UPDATE_RTT_PIPES = "CS.uRTT";
    private static Connection sNullConnection;
    private Conference sNullConference;
    private final Map<String, Connection> mConnectionById = new ConcurrentHashMap();
    private final Map<Connection, String> mIdByConnection = new ConcurrentHashMap();
    private final Map<String, Conference> mConferenceById = new ConcurrentHashMap();
    private final Map<Conference, String> mIdByConference = new ConcurrentHashMap();
    private final RemoteConnectionManager mRemoteConnectionManager = new RemoteConnectionManager(this);
    private final List<Runnable> mPreInitializationConnectionRequests = new ArrayList();
    private final ConnectionServiceAdapter mAdapter = new ConnectionServiceAdapter();
    private boolean mAreAccountsInitialized = false;
    private Object mIdSyncRoot = new Object();
    private int mId = 0;
    private final IBinder mBinder = new IConnectionService.Stub() { // from class: android.telecom.ConnectionService.1
        @Override // com.android.internal.telecom.IConnectionService
        public void addConnectionServiceAdapter(IConnectionServiceAdapter iConnectionServiceAdapter, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_ADD_CS_ADAPTER);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = iConnectionServiceAdapter;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(1, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void removeConnectionServiceAdapter(IConnectionServiceAdapter iConnectionServiceAdapter, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_REMOVE_CS_ADAPTER);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = iConnectionServiceAdapter;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(16, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnection(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, boolean z2, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_CREATE_CONN);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = phoneAccountHandle;
                someArgsObtain.arg2 = str;
                someArgsObtain.arg3 = connectionRequest;
                someArgsObtain.arg4 = Log.createSubsession();
                int i = 1;
                someArgsObtain.argi1 = z ? 1 : 0;
                if (!z2) {
                    i = 0;
                }
                someArgsObtain.argi2 = i;
                ConnectionService.this.mHandler.obtainMessage(2, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionComplete(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_CREATE_CONN_COMPLETE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(29, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionFailed(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_CREATE_CONN_FAILED);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = connectionRequest;
                someArgsObtain.arg3 = Log.createSubsession();
                someArgsObtain.arg4 = phoneAccountHandle;
                someArgsObtain.argi1 = z ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(25, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverFailed(String str, ConnectionRequest connectionRequest, int i, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_HANDOVER_FAILED);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = connectionRequest;
                someArgsObtain.arg3 = Log.createSubsession();
                someArgsObtain.arg4 = Integer.valueOf(i);
                ConnectionService.this.mHandler.obtainMessage(32, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverComplete(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_HANDOVER_COMPLETE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(33, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void abort(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_ABORT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(3, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answerVideo(String str, int i, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_ANSWER_VIDEO);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                someArgsObtain.argi1 = i;
                ConnectionService.this.mHandler.obtainMessage(17, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answer(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_ANSWER);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(4, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void deflect(String str, Uri uri, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_DEFLECT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = uri;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(34, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void reject(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_REJECT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(5, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void rejectWithMessage(String str, String str2, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_REJECT_MESSAGE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = str2;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(20, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void silence(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_SILENCE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(21, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void disconnect(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_DISCONNECT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(6, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void hold(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_HOLD);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(7, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void unhold(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_UNHOLD);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(8, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallAudioStateChanged(String str, CallAudioState callAudioState, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_CALL_AUDIO_SC);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = callAudioState;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(9, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void playDtmfTone(String str, char c, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_PLAY_DTMF);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = Character.valueOf(c);
                someArgsObtain.arg2 = str;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(10, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopDtmfTone(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_STOP_DTMF);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(11, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void conference(String str, String str2, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_CONFERENCE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = str2;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(12, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void splitFromConference(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_SPLIT_CONFERENCE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(13, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void mergeConference(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_MERGE_CONFERENCE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(18, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void swapConference(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_SWAP_CONFERENCE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(19, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onPostDialContinue(String str, boolean z, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_POST_DIAL_CONT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                someArgsObtain.argi1 = z ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(14, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void pullExternalCall(String str, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_PULL_EXTERNAL_CALL);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(22, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void sendCallEvent(String str, String str2, Bundle bundle, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_SEND_CALL_EVENT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = str2;
                someArgsObtain.arg3 = bundle;
                someArgsObtain.arg4 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(23, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onExtrasChanged(String str, Bundle bundle, Session.Info info) {
            Log.startSession(info, ConnectionService.SESSION_EXTRAS_CHANGED);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = bundle;
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(24, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void startRtt(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, Session.Info info) throws RemoteException {
            Log.startSession(info, ConnectionService.SESSION_START_RTT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = new Connection.RttTextStream(parcelFileDescriptor2, parcelFileDescriptor);
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(26, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopRtt(String str, Session.Info info) throws RemoteException {
            Log.startSession(info, ConnectionService.SESSION_STOP_RTT);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(27, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void respondToRttUpgradeRequest(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, Session.Info info) throws RemoteException {
            Log.startSession(info, ConnectionService.SESSION_RTT_UPGRADE_RESPONSE);
            try {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                if (parcelFileDescriptor2 == null || parcelFileDescriptor == null) {
                    someArgsObtain.arg2 = null;
                } else {
                    someArgsObtain.arg2 = new Connection.RttTextStream(parcelFileDescriptor2, parcelFileDescriptor);
                }
                someArgsObtain.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(28, someArgsObtain).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusLost(Session.Info info) throws RemoteException {
            Log.startSession(info, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_LOST);
            try {
                ConnectionService.this.mHandler.obtainMessage(30).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusGained(Session.Info info) throws RemoteException {
            Log.startSession(info, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_GAINED);
            try {
                ConnectionService.this.mHandler.obtainMessage(31).sendToTarget();
            } finally {
                Log.endSession();
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.telecom.ConnectionService.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    try {
                        IConnectionServiceAdapter iConnectionServiceAdapter = (IConnectionServiceAdapter) someArgs.arg1;
                        Log.continueSession((Session) someArgs.arg2, "H.CS.aCSA");
                        ConnectionService.this.mAdapter.addAdapter(iConnectionServiceAdapter);
                        ConnectionService.this.onAdapterAttached();
                        return;
                    } finally {
                        someArgs.recycle();
                        Log.endSession();
                    }
                case 2:
                    SomeArgs someArgs2 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs2.arg4, "H.CS.crCo");
                    try {
                        final PhoneAccountHandle phoneAccountHandle = (PhoneAccountHandle) someArgs2.arg1;
                        final String str = (String) someArgs2.arg2;
                        final ConnectionRequest connectionRequest = (ConnectionRequest) someArgs2.arg3;
                        boolean z = someArgs2.argi1 == 1;
                        boolean z2 = someArgs2.argi2 == 1;
                        if (ConnectionService.this.mAreAccountsInitialized) {
                            ConnectionService.this.createConnection(phoneAccountHandle, str, connectionRequest, z, z2);
                        } else {
                            Log.d(this, "Enqueueing pre-init request %s", str);
                            final boolean z3 = z;
                            final boolean z4 = z2;
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCo.pICR", null) { // from class: android.telecom.ConnectionService.2.1
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConnection(phoneAccountHandle, str, connectionRequest, z3, z4);
                                }
                            }.prepare());
                        }
                        return;
                    } finally {
                        someArgs2.recycle();
                        Log.endSession();
                    }
                case 3:
                    SomeArgs someArgs3 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs3.arg2, "H.CS.ab");
                    try {
                        ConnectionService.this.abort((String) someArgs3.arg1);
                        return;
                    } finally {
                        someArgs3.recycle();
                        Log.endSession();
                    }
                case 4:
                    SomeArgs someArgs4 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs4.arg2, "H.CS.an");
                    try {
                        ConnectionService.this.answer((String) someArgs4.arg1);
                        return;
                    } finally {
                        someArgs4.recycle();
                        Log.endSession();
                    }
                case 5:
                    SomeArgs someArgs5 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs5.arg2, "H.CS.r");
                    try {
                        ConnectionService.this.reject((String) someArgs5.arg1);
                        return;
                    } finally {
                        someArgs5.recycle();
                        Log.endSession();
                    }
                case 6:
                    SomeArgs someArgs6 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs6.arg2, "H.CS.d");
                    try {
                        ConnectionService.this.disconnect((String) someArgs6.arg1);
                        return;
                    } finally {
                        someArgs6.recycle();
                        Log.endSession();
                    }
                case 7:
                    SomeArgs someArgs7 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs7.arg2, "H.CS.r");
                    try {
                        ConnectionService.this.hold((String) someArgs7.arg1);
                        return;
                    } finally {
                        someArgs7.recycle();
                        Log.endSession();
                    }
                case 8:
                    SomeArgs someArgs8 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs8.arg2, "H.CS.u");
                    try {
                        ConnectionService.this.unhold((String) someArgs8.arg1);
                        return;
                    } finally {
                        someArgs8.recycle();
                        Log.endSession();
                    }
                case 9:
                    SomeArgs someArgs9 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs9.arg3, "H.CS.cASC");
                    try {
                        ConnectionService.this.onCallAudioStateChanged((String) someArgs9.arg1, new CallAudioState((CallAudioState) someArgs9.arg2));
                        return;
                    } finally {
                        someArgs9.recycle();
                        Log.endSession();
                    }
                case 10:
                    SomeArgs someArgs10 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs10.arg3, "H.CS.pDT");
                        ConnectionService.this.playDtmfTone((String) someArgs10.arg2, ((Character) someArgs10.arg1).charValue());
                        return;
                    } finally {
                        someArgs10.recycle();
                        Log.endSession();
                    }
                case 11:
                    SomeArgs someArgs11 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs11.arg2, "H.CS.sDT");
                        ConnectionService.this.stopDtmfTone((String) someArgs11.arg1);
                        return;
                    } finally {
                        someArgs11.recycle();
                        Log.endSession();
                    }
                case 12:
                    SomeArgs someArgs12 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs12.arg3, "H.CS.c");
                        ConnectionService.this.conference((String) someArgs12.arg1, (String) someArgs12.arg2);
                        return;
                    } finally {
                        someArgs12.recycle();
                        Log.endSession();
                    }
                case 13:
                    SomeArgs someArgs13 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs13.arg2, "H.CS.sFC");
                        ConnectionService.this.splitFromConference((String) someArgs13.arg1);
                        return;
                    } finally {
                        someArgs13.recycle();
                        Log.endSession();
                    }
                case 14:
                    SomeArgs someArgs14 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs14.arg2, "H.CS.oPDC");
                        ConnectionService.this.onPostDialContinue((String) someArgs14.arg1, someArgs14.argi1 == 1);
                        return;
                    } finally {
                        someArgs14.recycle();
                        Log.endSession();
                    }
                case 15:
                default:
                    return;
                case 16:
                    SomeArgs someArgs15 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs15.arg2, "H.CS.rCSA");
                        ConnectionService.this.mAdapter.removeAdapter((IConnectionServiceAdapter) someArgs15.arg1);
                        return;
                    } finally {
                        someArgs15.recycle();
                        Log.endSession();
                    }
                case 17:
                    SomeArgs someArgs16 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs16.arg2, "H.CS.anV");
                    try {
                        ConnectionService.this.answerVideo((String) someArgs16.arg1, someArgs16.argi1);
                        return;
                    } finally {
                        someArgs16.recycle();
                        Log.endSession();
                    }
                case 18:
                    SomeArgs someArgs17 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs17.arg2, "H.CS.mC");
                        ConnectionService.this.mergeConference((String) someArgs17.arg1);
                        return;
                    } finally {
                        someArgs17.recycle();
                        Log.endSession();
                    }
                case 19:
                    SomeArgs someArgs18 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs18.arg2, "H.CS.sC");
                        ConnectionService.this.swapConference((String) someArgs18.arg1);
                        return;
                    } finally {
                        someArgs18.recycle();
                        Log.endSession();
                    }
                case 20:
                    SomeArgs someArgs19 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs19.arg3, "H.CS.rWM");
                    try {
                        ConnectionService.this.reject((String) someArgs19.arg1, (String) someArgs19.arg2);
                        return;
                    } finally {
                        someArgs19.recycle();
                        Log.endSession();
                    }
                case 21:
                    SomeArgs someArgs20 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs20.arg2, "H.CS.s");
                    try {
                        ConnectionService.this.silence((String) someArgs20.arg1);
                        return;
                    } finally {
                        someArgs20.recycle();
                        Log.endSession();
                    }
                case 22:
                    SomeArgs someArgs21 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs21.arg2, "H.CS.pEC");
                        ConnectionService.this.pullExternalCall((String) someArgs21.arg1);
                        return;
                    } finally {
                        someArgs21.recycle();
                        Log.endSession();
                    }
                case 23:
                    SomeArgs someArgs22 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs22.arg4, "H.CS.sCE");
                        ConnectionService.this.sendCallEvent((String) someArgs22.arg1, (String) someArgs22.arg2, (Bundle) someArgs22.arg3);
                        return;
                    } finally {
                        someArgs22.recycle();
                        Log.endSession();
                    }
                case 24:
                    SomeArgs someArgs23 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs23.arg3, "H.CS.oEC");
                        ConnectionService.this.handleExtrasChanged((String) someArgs23.arg1, (Bundle) someArgs23.arg2);
                        return;
                    } finally {
                        someArgs23.recycle();
                        Log.endSession();
                    }
                case 25:
                    SomeArgs someArgs24 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs24.arg3, "H.CS.crCoF");
                    try {
                        final String str2 = (String) someArgs24.arg1;
                        final ConnectionRequest connectionRequest2 = (ConnectionRequest) someArgs24.arg2;
                        final boolean z5 = someArgs24.argi1 == 1;
                        final PhoneAccountHandle phoneAccountHandle2 = (PhoneAccountHandle) someArgs24.arg4;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.d(this, "Enqueueing pre-init request %s", str2);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoF.pICR", null) { // from class: android.telecom.ConnectionService.2.3
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConnectionFailed(phoneAccountHandle2, str2, connectionRequest2, z5);
                                }
                            }.prepare());
                        } else {
                            Log.i(this, "createConnectionFailed %s", str2);
                            ConnectionService.this.createConnectionFailed(phoneAccountHandle2, str2, connectionRequest2, z5);
                        }
                        return;
                    } finally {
                        someArgs24.recycle();
                        Log.endSession();
                    }
                case 26:
                    SomeArgs someArgs25 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs25.arg3, "H.CS.+RTT");
                        ConnectionService.this.startRtt((String) someArgs25.arg1, (Connection.RttTextStream) someArgs25.arg2);
                        return;
                    } finally {
                        someArgs25.recycle();
                        Log.endSession();
                    }
                case 27:
                    SomeArgs someArgs26 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs26.arg2, "H.CS.-RTT");
                        ConnectionService.this.stopRtt((String) someArgs26.arg1);
                        return;
                    } finally {
                        someArgs26.recycle();
                        Log.endSession();
                    }
                case 28:
                    SomeArgs someArgs27 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs27.arg3, "H.CS.rTRUR");
                        ConnectionService.this.handleRttUpgradeResponse((String) someArgs27.arg1, (Connection.RttTextStream) someArgs27.arg2);
                        return;
                    } finally {
                        someArgs27.recycle();
                        Log.endSession();
                    }
                case 29:
                    SomeArgs someArgs28 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs28.arg2, "H.CS.crCoC");
                    try {
                        final String str3 = (String) someArgs28.arg1;
                        if (ConnectionService.this.mAreAccountsInitialized) {
                            ConnectionService.this.notifyCreateConnectionComplete(str3);
                        } else {
                            Log.d(this, "Enqueueing pre-init request %s", str3);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoC.pICR", null) { // from class: android.telecom.ConnectionService.2.2
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.notifyCreateConnectionComplete(str3);
                                }
                            }.prepare());
                        }
                        return;
                    } finally {
                        someArgs28.recycle();
                        Log.endSession();
                    }
                case 30:
                    ConnectionService.this.onConnectionServiceFocusLost();
                    return;
                case 31:
                    ConnectionService.this.onConnectionServiceFocusGained();
                    return;
                case 32:
                    SomeArgs someArgs29 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs29.arg3, "H.CS.haF");
                    try {
                        final String str4 = (String) someArgs29.arg1;
                        final ConnectionRequest connectionRequest3 = (ConnectionRequest) someArgs29.arg2;
                        final int iIntValue = ((Integer) someArgs29.arg4).intValue();
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.d(this, "Enqueueing pre-init request %s", str4);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.haF.pICR", null) { // from class: android.telecom.ConnectionService.2.4
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.handoverFailed(str4, connectionRequest3, iIntValue);
                                }
                            }.prepare());
                        } else {
                            Log.i(this, "createConnectionFailed %s", str4);
                            ConnectionService.this.handoverFailed(str4, connectionRequest3, iIntValue);
                        }
                        return;
                    } finally {
                        someArgs29.recycle();
                        Log.endSession();
                    }
                case 33:
                    SomeArgs someArgs30 = (SomeArgs) message.obj;
                    try {
                        Log.continueSession((Session) someArgs30.arg2, "H.CS.hC");
                        ConnectionService.this.notifyHandoverComplete((String) someArgs30.arg1);
                        return;
                    } finally {
                        someArgs30.recycle();
                        Log.endSession();
                    }
                case 34:
                    SomeArgs someArgs31 = (SomeArgs) message.obj;
                    Log.continueSession((Session) someArgs31.arg3, "H.CS.def");
                    try {
                        ConnectionService.this.deflect((String) someArgs31.arg1, (Uri) someArgs31.arg2);
                        return;
                    } finally {
                        someArgs31.recycle();
                        Log.endSession();
                    }
            }
        }
    };
    private final Conference.Listener mConferenceListener = new Conference.Listener() { // from class: android.telecom.ConnectionService.3
        @Override // android.telecom.Conference.Listener
        public void onConnectionAdded(Conference conference, Connection connection) {
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionRemoved(Conference conference, Connection connection) {
        }

        @Override // android.telecom.Conference.Listener
        public void onStateChanged(Conference conference, int i, int i2) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            if (i2 == 4) {
                ConnectionService.this.mAdapter.setActive(str);
            } else {
                if (i2 != 5) {
                    return;
                }
                ConnectionService.this.mAdapter.setOnHold(str);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onDisconnected(Conference conference, DisconnectCause disconnectCause) {
            ConnectionService.this.mAdapter.setDisconnected((String) ConnectionService.this.mIdByConference.get(conference), disconnectCause);
        }

        @Override // android.telecom.Conference.Listener
        public void onConferenceableConnectionsChanged(Conference conference, List<Connection> list) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConference.get(conference), ConnectionService.this.createConnectionIdList(list));
        }

        @Override // android.telecom.Conference.Listener
        public void onDestroyed(Conference conference) {
            ConnectionService.this.removeConference(conference);
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionCapabilitiesChanged(Conference conference, int i) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d(this, "call capabilities: conference: %s", Connection.capabilitiesToString(i));
            ConnectionService.this.mAdapter.setConnectionCapabilities(str, i);
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionPropertiesChanged(Conference conference, int i) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d(this, "call capabilities: conference: %s", Connection.propertiesToString(i));
            ConnectionService.this.mAdapter.setConnectionProperties(str, i);
        }

        @Override // android.telecom.Conference.Listener
        public void onVideoStateChanged(Conference conference, int i) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d(this, "onVideoStateChanged set video state %d", Integer.valueOf(i));
            ConnectionService.this.mAdapter.setVideoState(str, i);
        }

        @Override // android.telecom.Conference.Listener
        public void onVideoProviderChanged(Conference conference, Connection.VideoProvider videoProvider) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", conference, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(str, videoProvider);
        }

        @Override // android.telecom.Conference.Listener
        public void onStatusHintsChanged(Conference conference, StatusHints statusHints) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            if (str != null) {
                ConnectionService.this.mAdapter.setStatusHints(str, statusHints);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onExtrasChanged(Conference conference, Bundle bundle) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            if (str != null) {
                ConnectionService.this.mAdapter.putExtras(str, bundle);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onExtrasRemoved(Conference conference, List<String> list) {
            String str = (String) ConnectionService.this.mIdByConference.get(conference);
            if (str != null) {
                ConnectionService.this.mAdapter.removeExtras(str, list);
            }
        }
    };
    private final Connection.Listener mConnectionListener = new Connection.Listener() { // from class: android.telecom.ConnectionService.4
        @Override // android.telecom.Connection.Listener
        public void onStateChanged(Connection connection, int i) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter set state %s %s", str, Connection.stateToString(i));
            if (i == 2) {
                ConnectionService.this.mAdapter.setRinging(str);
                return;
            }
            if (i == 3) {
                ConnectionService.this.mAdapter.setDialing(str);
                return;
            }
            if (i == 4) {
                ConnectionService.this.mAdapter.setActive(str);
            } else if (i == 5) {
                ConnectionService.this.mAdapter.setOnHold(str);
            } else {
                if (i != 7) {
                    return;
                }
                ConnectionService.this.mAdapter.setPulling(str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onDisconnected(Connection connection, DisconnectCause disconnectCause) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter set disconnected %s", disconnectCause);
            ConnectionService.this.mAdapter.setDisconnected(str, disconnectCause);
        }

        @Override // android.telecom.Connection.Listener
        public void onVideoStateChanged(Connection connection, int i) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter set video state %d", Integer.valueOf(i));
            ConnectionService.this.mAdapter.setVideoState(str, i);
        }

        @Override // android.telecom.Connection.Listener
        public void onAddressChanged(Connection connection, Uri uri, int i) {
            ConnectionService.this.mAdapter.setAddress((String) ConnectionService.this.mIdByConnection.get(connection), uri, i);
        }

        @Override // android.telecom.Connection.Listener
        public void onCallerDisplayNameChanged(Connection connection, String str, int i) {
            ConnectionService.this.mAdapter.setCallerDisplayName((String) ConnectionService.this.mIdByConnection.get(connection), str, i);
        }

        @Override // android.telecom.Connection.Listener
        public void onDestroyed(Connection connection) {
            ConnectionService.this.removeConnection(connection);
        }

        @Override // android.telecom.Connection.Listener
        public void onPostDialWait(Connection connection, String str) {
            String str2 = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter onPostDialWait %s, %s", connection, str);
            ConnectionService.this.mAdapter.onPostDialWait(str2, str);
        }

        @Override // android.telecom.Connection.Listener
        public void onPostDialChar(Connection connection, char c) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter onPostDialChar %s, %s", connection, Character.valueOf(c));
            ConnectionService.this.mAdapter.onPostDialChar(str, c);
        }

        @Override // android.telecom.Connection.Listener
        public void onRingbackRequested(Connection connection, boolean z) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "Adapter onRingback %b", Boolean.valueOf(z));
            ConnectionService.this.mAdapter.setRingbackRequested(str, z);
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionCapabilitiesChanged(Connection connection, int i) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "capabilities: parcelableconnection: %s", Connection.capabilitiesToString(i));
            ConnectionService.this.mAdapter.setConnectionCapabilities(str, i);
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionPropertiesChanged(Connection connection, int i) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "properties: parcelableconnection: %s", Connection.propertiesToString(i));
            ConnectionService.this.mAdapter.setConnectionProperties(str, i);
        }

        @Override // android.telecom.Connection.Listener
        public void onVideoProviderChanged(Connection connection, Connection.VideoProvider videoProvider) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            Log.d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", connection, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(str, videoProvider);
        }

        @Override // android.telecom.Connection.Listener
        public void onAudioModeIsVoipChanged(Connection connection, boolean z) {
            ConnectionService.this.mAdapter.setIsVoipAudioMode((String) ConnectionService.this.mIdByConnection.get(connection), z);
        }

        @Override // android.telecom.Connection.Listener
        public void onStatusHintsChanged(Connection connection, StatusHints statusHints) {
            ConnectionService.this.mAdapter.setStatusHints((String) ConnectionService.this.mIdByConnection.get(connection), statusHints);
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceablesChanged(Connection connection, List<Conferenceable> list) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConnection.get(connection), ConnectionService.this.createIdList(list));
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceChanged(Connection connection, Conference conference) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.setIsConferenced(str, conference != null ? (String) ConnectionService.this.mIdByConference.get(conference) : null);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceMergeFailed(Connection connection) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onConferenceMergeFailed(str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onExtrasChanged(Connection connection, Bundle bundle) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.putExtras(str, bundle);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onExtrasRemoved(Connection connection, List<String> list) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.removeExtras(str, list);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionEvent(Connection connection, String str, Bundle bundle) {
            String str2 = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str2 != null) {
                ConnectionService.this.mAdapter.onConnectionEvent(str2, str, bundle);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onAudioRouteChanged(Connection connection, int i, String str) {
            String str2 = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str2 != null) {
                ConnectionService.this.mAdapter.setAudioRoute(str2, i, str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttInitiationSuccess(Connection connection) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onRttInitiationSuccess(str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttInitiationFailure(Connection connection, int i) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onRttInitiationFailure(str, i);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttSessionRemotelyTerminated(Connection connection) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onRttSessionRemotelyTerminated(str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRemoteRttRequest(Connection connection) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onRemoteRttRequest(str);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onPhoneAccountChanged(Connection connection, PhoneAccountHandle phoneAccountHandle) {
            String str = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (str != null) {
                ConnectionService.this.mAdapter.onPhoneAccountChanged(str, phoneAccountHandle);
            }
        }
    };

    public void onConference(Connection connection, Connection connection2) {
    }

    public void onConferenceAdded(Conference conference) {
    }

    public void onConferenceRemoved(Conference conference) {
    }

    public void onConnectionAdded(Connection connection) {
    }

    public void onConnectionRemoved(Connection connection) {
    }

    public void onConnectionServiceFocusGained() {
    }

    public void onConnectionServiceFocusLost() {
    }

    public void onCreateConnectionComplete(Connection connection) {
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return null;
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
    }

    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return null;
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return null;
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
    }

    public Connection onCreateOutgoingHandoverConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return null;
    }

    public Connection onCreateUnknownConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return null;
    }

    public void onHandoverFailed(ConnectionRequest connectionRequest, int i) {
    }

    public void onRemoteConferenceAdded(RemoteConference remoteConference) {
    }

    public void onRemoteExistingConnectionAdded(RemoteConnection remoteConnection) {
    }

    public void triggerConferenceRecalculate() {
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        endAllConnections();
        return super.onUnbind(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConnection(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, boolean z2) {
        Connection connectionOnCreateOutgoingConnection;
        boolean z3 = connectionRequest.getExtras() != null && connectionRequest.getExtras().getBoolean("android.telecom.extra.IS_HANDOVER", false);
        boolean z4 = connectionRequest.getExtras() != null && connectionRequest.getExtras().getBoolean(TelecomManager.EXTRA_IS_HANDOVER_CONNECTION, false);
        Log.d(this, "createConnection, callManagerAccount: %s, callId: %s, request: %s, isIncoming: %b, isUnknown: %b, isLegacyHandover: %b, isHandover: %b", phoneAccountHandle, str, connectionRequest, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4));
        if (z4) {
            PhoneAccountHandle phoneAccountHandle2 = connectionRequest.getExtras() != null ? (PhoneAccountHandle) connectionRequest.getExtras().getParcelable(TelecomManager.EXTRA_HANDOVER_FROM_PHONE_ACCOUNT) : null;
            if (!z) {
                connectionOnCreateOutgoingConnection = onCreateOutgoingHandoverConnection(phoneAccountHandle2, connectionRequest);
            } else {
                connectionOnCreateOutgoingConnection = onCreateIncomingHandoverConnection(phoneAccountHandle2, connectionRequest);
            }
        } else if (z2) {
            connectionOnCreateOutgoingConnection = onCreateUnknownConnection(phoneAccountHandle, connectionRequest);
        } else if (z) {
            connectionOnCreateOutgoingConnection = onCreateIncomingConnection(phoneAccountHandle, connectionRequest);
        } else {
            connectionOnCreateOutgoingConnection = onCreateOutgoingConnection(phoneAccountHandle, connectionRequest);
        }
        Log.d(this, "createConnection, connection: %s", connectionOnCreateOutgoingConnection);
        if (connectionOnCreateOutgoingConnection == null) {
            Log.i(this, "createConnection, implementation returned null connection.", new Object[0]);
            connectionOnCreateOutgoingConnection = Connection.createFailedConnection(new DisconnectCause(1, "IMPL_RETURNED_NULL_CONNECTION"));
        }
        connectionOnCreateOutgoingConnection.setTelecomCallId(str);
        if (connectionOnCreateOutgoingConnection.getState() != 6) {
            addConnection(connectionRequest.getAccountHandle(), str, connectionOnCreateOutgoingConnection);
        }
        Uri address = connectionOnCreateOutgoingConnection.getAddress();
        Log.v(this, "createConnection, number: %s, state: %s, capabilities: %s, properties: %s", Connection.toLogSafePhoneNumber(address == null ? "null" : address.getSchemeSpecificPart()), Connection.stateToString(connectionOnCreateOutgoingConnection.getState()), Connection.capabilitiesToString(connectionOnCreateOutgoingConnection.getConnectionCapabilities()), Connection.propertiesToString(connectionOnCreateOutgoingConnection.getConnectionProperties()));
        Log.d(this, "createConnection, calling handleCreateConnectionSuccessful %s", str);
        this.mAdapter.handleCreateConnectionComplete(str, connectionRequest, new ParcelableConnection(connectionRequest.getAccountHandle(), connectionOnCreateOutgoingConnection.getState(), connectionOnCreateOutgoingConnection.getConnectionCapabilities(), connectionOnCreateOutgoingConnection.getConnectionProperties(), connectionOnCreateOutgoingConnection.getSupportedAudioRoutes(), connectionOnCreateOutgoingConnection.getAddress(), connectionOnCreateOutgoingConnection.getAddressPresentation(), connectionOnCreateOutgoingConnection.getCallerDisplayName(), connectionOnCreateOutgoingConnection.getCallerDisplayNamePresentation(), connectionOnCreateOutgoingConnection.getVideoProvider() != null ? connectionOnCreateOutgoingConnection.getVideoProvider().getInterface() : null, connectionOnCreateOutgoingConnection.getVideoState(), connectionOnCreateOutgoingConnection.isRingbackRequested(), connectionOnCreateOutgoingConnection.getAudioModeIsVoip(), connectionOnCreateOutgoingConnection.getConnectTimeMillis(), connectionOnCreateOutgoingConnection.getConnectElapsedTimeMillis(), connectionOnCreateOutgoingConnection.getStatusHints(), connectionOnCreateOutgoingConnection.getDisconnectCause(), createIdList(connectionOnCreateOutgoingConnection.getConferenceables()), connectionOnCreateOutgoingConnection.getExtras()));
        if (z && connectionRequest.shouldShowIncomingCallUi() && (connectionOnCreateOutgoingConnection.getConnectionProperties() & 128) == 128) {
            connectionOnCreateOutgoingConnection.onShowIncomingCallUi();
        }
        if (z2) {
            triggerConferenceRecalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConnectionFailed(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z) {
        Log.i(this, "createConnectionFailed %s", str);
        if (z) {
            onCreateIncomingConnectionFailed(phoneAccountHandle, connectionRequest);
        } else {
            onCreateOutgoingConnectionFailed(phoneAccountHandle, connectionRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handoverFailed(String str, ConnectionRequest connectionRequest, int i) {
        Log.i(this, "handoverFailed %s", str);
        onHandoverFailed(connectionRequest, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCreateConnectionComplete(String str) {
        Log.i(this, "notifyCreateConnectionComplete %s", str);
        if (str == null) {
            Log.w(this, "notifyCreateConnectionComplete: callId is null.", new Object[0]);
        } else {
            onCreateConnectionComplete(findConnectionForAction(str, "notifyCreateConnectionComplete"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abort(String str) {
        Log.d(this, "abort %s", str);
        findConnectionForAction(str, "abort").onAbort();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void answerVideo(String str, int i) {
        Log.d(this, "answerVideo %s", str);
        findConnectionForAction(str, "answer").onAnswer(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void answer(String str) {
        Log.d(this, "answer %s", str);
        findConnectionForAction(str, "answer").onAnswer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deflect(String str, Uri uri) {
        Log.d(this, "deflect %s", str);
        findConnectionForAction(str, "deflect").onDeflect(uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reject(String str) {
        Log.d(this, "reject %s", str);
        findConnectionForAction(str, "reject").onReject();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reject(String str, String str2) {
        Log.d(this, "reject %s with message", str);
        findConnectionForAction(str, "reject").onReject(str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void silence(String str) {
        Log.d(this, "silence %s", str);
        findConnectionForAction(str, "silence").onSilence();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnect(String str) {
        Log.d(this, "disconnect %s", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "disconnect").onDisconnect();
        } else {
            findConferenceForAction(str, "disconnect").onDisconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hold(String str) {
        Log.d(this, "hold %s", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "hold").onHold();
        } else {
            findConferenceForAction(str, "hold").onHold();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unhold(String str) {
        Log.d(this, "unhold %s", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "unhold").onUnhold();
        } else {
            findConferenceForAction(str, "unhold").onUnhold();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallAudioStateChanged(String str, CallAudioState callAudioState) {
        Log.d(this, "onAudioStateChanged %s %s", str, callAudioState);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "onCallAudioStateChanged").setCallAudioState(callAudioState);
        } else {
            findConferenceForAction(str, "onCallAudioStateChanged").setCallAudioState(callAudioState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playDtmfTone(String str, char c) {
        Log.d(this, "playDtmfTone %s %c", str, Character.valueOf(c));
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "playDtmfTone").onPlayDtmfTone(c);
        } else {
            findConferenceForAction(str, "playDtmfTone").onPlayDtmfTone(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopDtmfTone(String str) {
        Log.d(this, "stopDtmfTone %s", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "stopDtmfTone").onStopDtmfTone();
        } else {
            findConferenceForAction(str, "stopDtmfTone").onStopDtmfTone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void conference(String str, String str2) {
        Log.d(this, "conference %s, %s", str, str2);
        Connection connectionFindConnectionForAction = findConnectionForAction(str2, ImsCallProfile.EXTRA_CONFERENCE);
        Conference nullConference = getNullConference();
        if (connectionFindConnectionForAction == getNullConnection() && (nullConference = findConferenceForAction(str2, ImsCallProfile.EXTRA_CONFERENCE)) == getNullConference()) {
            Log.w(this, "Connection2 or Conference2 missing in conference request %s.", str2);
            return;
        }
        Connection connectionFindConnectionForAction2 = findConnectionForAction(str, ImsCallProfile.EXTRA_CONFERENCE);
        if (connectionFindConnectionForAction2 == getNullConnection()) {
            Conference conferenceFindConferenceForAction = findConferenceForAction(str, "addConnection");
            if (conferenceFindConferenceForAction == getNullConference()) {
                Log.w(this, "Connection1 or Conference1 missing in conference request %s.", str);
                return;
            } else if (connectionFindConnectionForAction != getNullConnection()) {
                conferenceFindConferenceForAction.onMerge(connectionFindConnectionForAction);
                return;
            } else {
                Log.wtf(this, "There can only be one conference and an attempt was made to merge two conferences.", new Object[0]);
                return;
            }
        }
        if (nullConference != getNullConference()) {
            nullConference.onMerge(connectionFindConnectionForAction2);
        } else {
            onConference(connectionFindConnectionForAction2, connectionFindConnectionForAction);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void splitFromConference(String str) {
        Log.d(this, "splitFromConference(%s)", str);
        Connection connectionFindConnectionForAction = findConnectionForAction(str, "splitFromConference");
        if (connectionFindConnectionForAction == getNullConnection()) {
            Log.w(this, "Connection missing in conference request %s.", str);
            return;
        }
        Conference conference = connectionFindConnectionForAction.getConference();
        if (conference != null) {
            conference.onSeparate(connectionFindConnectionForAction);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeConference(String str) {
        Log.d(this, "mergeConference(%s)", str);
        Conference conferenceFindConferenceForAction = findConferenceForAction(str, "mergeConference");
        if (conferenceFindConferenceForAction != null) {
            conferenceFindConferenceForAction.onMerge();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swapConference(String str) {
        Log.d(this, "swapConference(%s)", str);
        Conference conferenceFindConferenceForAction = findConferenceForAction(str, "swapConference");
        if (conferenceFindConferenceForAction != null) {
            conferenceFindConferenceForAction.onSwap();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pullExternalCall(String str) {
        Log.d(this, "pullExternalCall(%s)", str);
        Connection connectionFindConnectionForAction = findConnectionForAction(str, "pullExternalCall");
        if (connectionFindConnectionForAction != null) {
            connectionFindConnectionForAction.onPullExternalCall();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCallEvent(String str, String str2, Bundle bundle) {
        Log.d(this, "sendCallEvent(%s, %s)", str, str2);
        Connection connectionFindConnectionForAction = findConnectionForAction(str, "sendCallEvent");
        if (connectionFindConnectionForAction != null) {
            connectionFindConnectionForAction.onCallEvent(str2, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyHandoverComplete(String str) {
        Log.d(this, "notifyHandoverComplete(%s)", str);
        Connection connectionFindConnectionForAction = findConnectionForAction(str, "notifyHandoverComplete");
        if (connectionFindConnectionForAction != null) {
            connectionFindConnectionForAction.onHandoverComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleExtrasChanged(String str, Bundle bundle) {
        Log.d(this, "handleExtrasChanged(%s, %s)", str, bundle);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "handleExtrasChanged").handleExtrasChanged(bundle);
        } else if (this.mConferenceById.containsKey(str)) {
            findConferenceForAction(str, "handleExtrasChanged").handleExtrasChanged(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRtt(String str, Connection.RttTextStream rttTextStream) {
        Log.d(this, "startRtt(%s)", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "startRtt").onStartRtt(rttTextStream);
        } else if (this.mConferenceById.containsKey(str)) {
            Log.w(this, "startRtt called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopRtt(String str) {
        Log.d(this, "stopRtt(%s)", str);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "stopRtt").onStopRtt();
        } else if (this.mConferenceById.containsKey(str)) {
            Log.w(this, "stopRtt called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRttUpgradeResponse(String str, Connection.RttTextStream rttTextStream) {
        Object[] objArr = new Object[2];
        objArr[0] = str;
        objArr[1] = Boolean.valueOf(rttTextStream == null);
        Log.d(this, "handleRttUpgradeResponse(%s, %s)", objArr);
        if (this.mConnectionById.containsKey(str)) {
            findConnectionForAction(str, "handleRttUpgradeResponse").handleRttUpgradeResponse(rttTextStream);
        } else if (this.mConferenceById.containsKey(str)) {
            Log.w(this, "handleRttUpgradeResponse called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPostDialContinue(String str, boolean z) {
        Log.d(this, "onPostDialContinue(%s)", str);
        findConnectionForAction(str, "stopDtmfTone").onPostDialContinue(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAdapterAttached() {
        if (this.mAreAccountsInitialized) {
            return;
        }
        this.mAdapter.queryRemoteConnectionServices(new RemoteServiceCallback.Stub() { // from class: android.telecom.ConnectionService.5
            @Override // com.android.internal.telecom.RemoteServiceCallback
            public void onResult(final List<ComponentName> list, final List<IBinder> list2) {
                ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oR", null) { // from class: android.telecom.ConnectionService.5.1
                    @Override // android.telecom.Logging.Runnable
                    public void loggedRun() {
                        for (int i = 0; i < list.size() && i < list2.size(); i++) {
                            ConnectionService.this.mRemoteConnectionManager.addConnectionService((ComponentName) list.get(i), IConnectionService.Stub.asInterface((IBinder) list2.get(i)));
                        }
                        ConnectionService.this.onAccountsInitialized();
                        Log.d(this, "remote connection services found: " + list2, new Object[0]);
                    }
                }.prepare());
            }

            @Override // com.android.internal.telecom.RemoteServiceCallback
            public void onError() {
                ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oE", null) { // from class: android.telecom.ConnectionService.5.2
                    @Override // android.telecom.Logging.Runnable
                    public void loggedRun() {
                        ConnectionService.this.mAreAccountsInitialized = true;
                    }
                }.prepare());
            }
        });
    }

    public final RemoteConnection createRemoteIncomingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return this.mRemoteConnectionManager.createRemoteConnection(phoneAccountHandle, connectionRequest, true);
    }

    public final RemoteConnection createRemoteOutgoingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        return this.mRemoteConnectionManager.createRemoteConnection(phoneAccountHandle, connectionRequest, false);
    }

    public final void conferenceRemoteConnections(RemoteConnection remoteConnection, RemoteConnection remoteConnection2) {
        this.mRemoteConnectionManager.conferenceRemoteConnections(remoteConnection, remoteConnection2);
    }

    public final void addConference(Conference conference) {
        Log.d(this, "addConference: conference=%s", conference);
        String strAddConferenceInternal = addConferenceInternal(conference);
        if (strAddConferenceInternal != null) {
            ArrayList arrayList = new ArrayList(2);
            for (Connection connection : conference.getConnections()) {
                if (this.mIdByConnection.containsKey(connection)) {
                    arrayList.add(this.mIdByConnection.get(connection));
                }
            }
            conference.setTelecomCallId(strAddConferenceInternal);
            this.mAdapter.addConferenceCall(strAddConferenceInternal, new ParcelableConference(conference.getPhoneAccountHandle(), conference.getState(), conference.getConnectionCapabilities(), conference.getConnectionProperties(), arrayList, conference.getVideoProvider() == null ? null : conference.getVideoProvider().getInterface(), conference.getVideoState(), conference.getConnectTimeMillis(), conference.getConnectionStartElapsedRealTime(), conference.getStatusHints(), conference.getExtras()));
            this.mAdapter.setVideoProvider(strAddConferenceInternal, conference.getVideoProvider());
            this.mAdapter.setVideoState(strAddConferenceInternal, conference.getVideoState());
            Iterator<Connection> it = conference.getConnections().iterator();
            while (it.hasNext()) {
                String str = this.mIdByConnection.get(it.next());
                if (str != null) {
                    this.mAdapter.setIsConferenced(str, strAddConferenceInternal);
                }
            }
            onConferenceAdded(conference);
        }
    }

    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection) {
        addExistingConnection(phoneAccountHandle, connection, null);
    }

    public final void connectionServiceFocusReleased() {
        this.mAdapter.onConnectionServiceFocusReleased();
    }

    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection, Conference conference) {
        String strAddExistingConnectionInternal = addExistingConnectionInternal(phoneAccountHandle, connection);
        if (strAddExistingConnectionInternal != null) {
            this.mAdapter.addExistingConnection(strAddExistingConnectionInternal, new ParcelableConnection(phoneAccountHandle, connection.getState(), connection.getConnectionCapabilities(), connection.getConnectionProperties(), connection.getSupportedAudioRoutes(), connection.getAddress(), connection.getAddressPresentation(), connection.getCallerDisplayName(), connection.getCallerDisplayNamePresentation(), connection.getVideoProvider() != null ? connection.getVideoProvider().getInterface() : null, connection.getVideoState(), connection.isRingbackRequested(), connection.getAudioModeIsVoip(), connection.getConnectTimeMillis(), connection.getConnectElapsedTimeMillis(), connection.getStatusHints(), connection.getDisconnectCause(), new ArrayList(0), connection.getExtras(), conference != null ? this.mIdByConference.get(conference) : null));
        }
    }

    public final Collection<Connection> getAllConnections() {
        return this.mConnectionById.values();
    }

    public final Collection<Conference> getAllConferences() {
        return this.mConferenceById.values();
    }

    public boolean containsConference(Conference conference) {
        return this.mIdByConference.containsKey(conference);
    }

    void addRemoteConference(RemoteConference remoteConference) {
        onRemoteConferenceAdded(remoteConference);
    }

    void addRemoteExistingConnection(RemoteConnection remoteConnection) {
        onRemoteExistingConnectionAdded(remoteConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccountsInitialized() {
        this.mAreAccountsInitialized = true;
        Iterator<Runnable> it = this.mPreInitializationConnectionRequests.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mPreInitializationConnectionRequests.clear();
    }

    private String addExistingConnectionInternal(PhoneAccountHandle phoneAccountHandle, Connection connection) {
        String string;
        if (connection.getExtras() != null && connection.getExtras().containsKey(Connection.EXTRA_ORIGINAL_CONNECTION_ID)) {
            string = connection.getExtras().getString(Connection.EXTRA_ORIGINAL_CONNECTION_ID);
            Log.d(this, "addExistingConnectionInternal - conn %s reusing original id %s", connection.getTelecomCallId(), string);
        } else if (phoneAccountHandle == null) {
            string = UUID.randomUUID().toString();
        } else {
            string = phoneAccountHandle.getComponentName().getClassName() + "@" + getNextCallId();
        }
        addConnection(phoneAccountHandle, string, connection);
        return string;
    }

    private void addConnection(PhoneAccountHandle phoneAccountHandle, String str, Connection connection) {
        connection.setTelecomCallId(str);
        this.mConnectionById.put(str, connection);
        this.mIdByConnection.put(connection, str);
        connection.addConnectionListener(this.mConnectionListener);
        connection.setConnectionService(this);
        connection.setPhoneAccountHandle(phoneAccountHandle);
        onConnectionAdded(connection);
    }

    protected void removeConnection(Connection connection) {
        connection.unsetConnectionService(this);
        connection.removeConnectionListener(this.mConnectionListener);
        String str = this.mIdByConnection.get(connection);
        if (str != null) {
            this.mConnectionById.remove(str);
            this.mIdByConnection.remove(connection);
            this.mAdapter.removeCall(str);
            onConnectionRemoved(connection);
        }
    }

    private String addConferenceInternal(Conference conference) {
        String string;
        if (conference.getExtras() == null || !conference.getExtras().containsKey(Connection.EXTRA_ORIGINAL_CONNECTION_ID)) {
            string = null;
        } else {
            string = conference.getExtras().getString(Connection.EXTRA_ORIGINAL_CONNECTION_ID);
            Log.d(this, "addConferenceInternal: conf %s reusing original id %s", conference.getTelecomCallId(), string);
        }
        if (this.mIdByConference.containsKey(conference)) {
            Log.w(this, "Re-adding an existing conference: %s.", conference);
        } else if (conference != null) {
            if (string == null) {
                string = UUID.randomUUID().toString();
            }
            this.mConferenceById.put(string, conference);
            this.mIdByConference.put(conference, string);
            conference.addListener(this.mConferenceListener);
            return string;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeConference(Conference conference) {
        if (this.mIdByConference.containsKey(conference)) {
            conference.removeListener(this.mConferenceListener);
            String str = this.mIdByConference.get(conference);
            this.mConferenceById.remove(str);
            this.mIdByConference.remove(conference);
            this.mAdapter.removeCall(str);
            onConferenceRemoved(conference);
        }
    }

    private Connection findConnectionForAction(String str, String str2) {
        if (str != null && this.mConnectionById.containsKey(str)) {
            return this.mConnectionById.get(str);
        }
        Log.w(this, "%s - Cannot find Connection %s", str2, str);
        return getNullConnection();
    }

    static synchronized Connection getNullConnection() {
        if (sNullConnection == null) {
            sNullConnection = new Connection() { // from class: android.telecom.ConnectionService.6
            };
        }
        return sNullConnection;
    }

    private Conference findConferenceForAction(String str, String str2) {
        if (this.mConferenceById.containsKey(str)) {
            return this.mConferenceById.get(str);
        }
        Log.w(this, "%s - Cannot find conference %s", str2, str);
        return getNullConference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> createConnectionIdList(List<Connection> list) {
        ArrayList arrayList = new ArrayList();
        for (Connection connection : list) {
            if (this.mIdByConnection.containsKey(connection)) {
                arrayList.add(this.mIdByConnection.get(connection));
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> createIdList(List<Conferenceable> list) {
        ArrayList arrayList = new ArrayList();
        for (Conferenceable conferenceable : list) {
            if (conferenceable instanceof Connection) {
                Connection connection = (Connection) conferenceable;
                if (this.mIdByConnection.containsKey(connection)) {
                    arrayList.add(this.mIdByConnection.get(connection));
                }
            } else if (conferenceable instanceof Conference) {
                Conference conference = (Conference) conferenceable;
                if (this.mIdByConference.containsKey(conference)) {
                    arrayList.add(this.mIdByConference.get(conference));
                }
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    private Conference getNullConference() {
        if (this.sNullConference == null) {
            this.sNullConference = new Conference(null) { // from class: android.telecom.ConnectionService.7
            };
        }
        return this.sNullConference;
    }

    private void endAllConnections() {
        for (Connection connection : this.mIdByConnection.keySet()) {
            if (connection.getConference() == null) {
                connection.onDisconnect();
            }
        }
        Iterator<Conference> it = this.mIdByConference.keySet().iterator();
        while (it.hasNext()) {
            it.next().onDisconnect();
        }
    }

    private int getNextCallId() {
        int i;
        synchronized (this.mIdSyncRoot) {
            i = this.mId + 1;
            this.mId = i;
        }
        return i;
    }
}
