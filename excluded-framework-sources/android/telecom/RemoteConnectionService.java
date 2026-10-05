package android.telecom;

import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telecom.Logging.Session;
import com.android.internal.telecom.IConnectionService;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
final class RemoteConnectionService {
    private final ConnectionService mOurConnectionServiceImpl;
    private final IConnectionService mOutgoingConnectionServiceRpc;
    private static final RemoteConnection NULL_CONNECTION = new RemoteConnection(WifiEnterpriseConfig.EMPTY_VALUE, null, (ConnectionRequest) null);
    private static final RemoteConference NULL_CONFERENCE = new RemoteConference(WifiEnterpriseConfig.EMPTY_VALUE, null);
    private final IConnectionServiceAdapter mServantDelegate = new IConnectionServiceAdapter() { // from class: android.telecom.RemoteConnectionService.1
        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionServiceFocusReleased(Session.Info info) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPhoneAccountChanged(String str, PhoneAccountHandle phoneAccountHandle, Session.Info info) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryRemoteConnectionServices(RemoteServiceCallback remoteServiceCallback, Session.Info info) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceMergeFailed(String str, Session.Info info) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConnectionComplete(String str, ConnectionRequest connectionRequest, ParcelableConnection parcelableConnection, Session.Info info) {
            RemoteConnection remoteConnectionFindConnectionForAction = RemoteConnectionService.this.findConnectionForAction(str, "handleCreateConnectionSuccessful");
            if (remoteConnectionFindConnectionForAction == RemoteConnectionService.NULL_CONNECTION || !RemoteConnectionService.this.mPendingConnections.contains(remoteConnectionFindConnectionForAction)) {
                return;
            }
            RemoteConnectionService.this.mPendingConnections.remove(remoteConnectionFindConnectionForAction);
            remoteConnectionFindConnectionForAction.setConnectionCapabilities(parcelableConnection.getConnectionCapabilities());
            remoteConnectionFindConnectionForAction.setConnectionProperties(parcelableConnection.getConnectionProperties());
            if (parcelableConnection.getHandle() != null || parcelableConnection.getState() != 6) {
                remoteConnectionFindConnectionForAction.setAddress(parcelableConnection.getHandle(), parcelableConnection.getHandlePresentation());
            }
            if (parcelableConnection.getCallerDisplayName() != null || parcelableConnection.getState() != 6) {
                remoteConnectionFindConnectionForAction.setCallerDisplayName(parcelableConnection.getCallerDisplayName(), parcelableConnection.getCallerDisplayNamePresentation());
            }
            if (parcelableConnection.getState() == 6) {
                remoteConnectionFindConnectionForAction.setDisconnected(parcelableConnection.getDisconnectCause());
            } else {
                remoteConnectionFindConnectionForAction.setState(parcelableConnection.getState());
            }
            ArrayList arrayList = new ArrayList();
            for (String str2 : parcelableConnection.getConferenceableConnectionIds()) {
                if (RemoteConnectionService.this.mConnectionById.containsKey(str2)) {
                    arrayList.add((RemoteConnection) RemoteConnectionService.this.mConnectionById.get(str2));
                }
            }
            remoteConnectionFindConnectionForAction.setConferenceableConnections(arrayList);
            remoteConnectionFindConnectionForAction.setVideoState(parcelableConnection.getVideoState());
            if (remoteConnectionFindConnectionForAction.getState() == 6) {
                remoteConnectionFindConnectionForAction.setDestroyed();
            }
            remoteConnectionFindConnectionForAction.setStatusHints(parcelableConnection.getStatusHints());
            remoteConnectionFindConnectionForAction.setIsVoipAudioMode(parcelableConnection.getIsVoipAudioMode());
            remoteConnectionFindConnectionForAction.setRingbackRequested(parcelableConnection.isRingbackRequested());
            remoteConnectionFindConnectionForAction.putExtras(parcelableConnection.getExtras());
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setActive(String str, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setActive").setState(4);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setActive").setState(4);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRinging(String str, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setRinging").setState(2);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDialing(String str, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setDialing").setState(3);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setPulling(String str, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setPulling").setState(7);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDisconnected(String str, DisconnectCause disconnectCause, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setDisconnected").setDisconnected(disconnectCause);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setDisconnected").setDisconnected(disconnectCause);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setOnHold(String str, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setOnHold").setState(5);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setOnHold").setState(5);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRingbackRequested(String str, boolean z, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setRingbackRequested").setRingbackRequested(z);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionCapabilities(String str, int i, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setConnectionCapabilities").setConnectionCapabilities(i);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setConnectionCapabilities").setConnectionCapabilities(i);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionProperties(String str, int i, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setConnectionProperties").setConnectionProperties(i);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setConnectionProperties").setConnectionProperties(i);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsConferenced(String str, String str2, Session.Info info) {
            RemoteConnection remoteConnectionFindConnectionForAction = RemoteConnectionService.this.findConnectionForAction(str, "setIsConferenced");
            if (remoteConnectionFindConnectionForAction != RemoteConnectionService.NULL_CONNECTION) {
                if (str2 != null) {
                    RemoteConference remoteConferenceFindConferenceForAction = RemoteConnectionService.this.findConferenceForAction(str2, "setIsConferenced");
                    if (remoteConferenceFindConferenceForAction != RemoteConnectionService.NULL_CONFERENCE) {
                        remoteConferenceFindConferenceForAction.addConnection(remoteConnectionFindConnectionForAction);
                        return;
                    }
                    return;
                }
                if (remoteConnectionFindConnectionForAction.getConference() != null) {
                    remoteConnectionFindConnectionForAction.getConference().removeConnection(remoteConnectionFindConnectionForAction);
                }
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addConferenceCall(final String str, ParcelableConference parcelableConference, Session.Info info) {
            RemoteConference remoteConference = new RemoteConference(str, RemoteConnectionService.this.mOutgoingConnectionServiceRpc);
            Iterator<String> it = parcelableConference.getConnectionIds().iterator();
            while (it.hasNext()) {
                RemoteConnection remoteConnection = (RemoteConnection) RemoteConnectionService.this.mConnectionById.get(it.next());
                if (remoteConnection != null) {
                    remoteConference.addConnection(remoteConnection);
                }
            }
            if (remoteConference.getConnections().size() == 0) {
                Log.d(this, "addConferenceCall - skipping", new Object[0]);
                return;
            }
            remoteConference.setState(parcelableConference.getState());
            remoteConference.setConnectionCapabilities(parcelableConference.getConnectionCapabilities());
            remoteConference.setConnectionProperties(parcelableConference.getConnectionProperties());
            remoteConference.putExtras(parcelableConference.getExtras());
            RemoteConnectionService.this.mConferenceById.put(str, remoteConference);
            Bundle bundle = new Bundle();
            bundle.putString(Connection.EXTRA_ORIGINAL_CONNECTION_ID, str);
            remoteConference.putExtras(bundle);
            remoteConference.registerCallback(new RemoteConference.Callback() { // from class: android.telecom.RemoteConnectionService.1.1
                @Override // android.telecom.RemoteConference.Callback
                public void onDestroyed(RemoteConference remoteConference2) {
                    RemoteConnectionService.this.mConferenceById.remove(str);
                    RemoteConnectionService.this.maybeDisconnectAdapter();
                }
            });
            RemoteConnectionService.this.mOurConnectionServiceImpl.addRemoteConference(remoteConference);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeCall(String str, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "removeCall").setDestroyed();
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "removeCall").setDestroyed();
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialWait(String str, String str2, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "onPostDialWait").setPostDialWait(str2);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialChar(String str, char c, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "onPostDialChar").onPostDialChar(c);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoProvider(String str, IVideoProvider iVideoProvider, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setVideoProvider").setVideoProvider(iVideoProvider != null ? new RemoteConnection.VideoProvider(iVideoProvider, RemoteConnectionService.this.mOurConnectionServiceImpl.getApplicationContext().getOpPackageName(), RemoteConnectionService.this.mOurConnectionServiceImpl.getApplicationInfo().targetSdkVersion) : null);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoState(String str, int i, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setVideoState").setVideoState(i);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsVoipAudioMode(String str, boolean z, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setIsVoipAudioMode").setIsVoipAudioMode(z);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setStatusHints(String str, StatusHints statusHints, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setStatusHints").setStatusHints(statusHints);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setAddress(String str, Uri uri, int i, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setAddress").setAddress(uri, i);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setCallerDisplayName(String str, String str2, int i, Session.Info info) {
            RemoteConnectionService.this.findConnectionForAction(str, "setCallerDisplayName").setCallerDisplayName(str2, i);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            throw new UnsupportedOperationException();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setConferenceableConnections(String str, List<String> list, Session.Info info) {
            ArrayList arrayList = new ArrayList();
            for (String str2 : list) {
                if (RemoteConnectionService.this.mConnectionById.containsKey(str2)) {
                    arrayList.add((RemoteConnection) RemoteConnectionService.this.mConnectionById.get(str2));
                }
            }
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "setConferenceableConnections").setConferenceableConnections(arrayList);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "setConferenceableConnections").setConferenceableConnections(arrayList);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addExistingConnection(final String str, ParcelableConnection parcelableConnection, Session.Info info) {
            RemoteConnection remoteConnection = new RemoteConnection(str, RemoteConnectionService.this.mOutgoingConnectionServiceRpc, parcelableConnection, RemoteConnectionService.this.mOurConnectionServiceImpl.getApplicationContext().getOpPackageName(), RemoteConnectionService.this.mOurConnectionServiceImpl.getApplicationInfo().targetSdkVersion);
            RemoteConnectionService.this.mConnectionById.put(str, remoteConnection);
            remoteConnection.registerCallback(new RemoteConnection.Callback() { // from class: android.telecom.RemoteConnectionService.1.2
                @Override // android.telecom.RemoteConnection.Callback
                public void onDestroyed(RemoteConnection remoteConnection2) {
                    RemoteConnectionService.this.mConnectionById.remove(str);
                    RemoteConnectionService.this.maybeDisconnectAdapter();
                }
            });
            RemoteConnectionService.this.mOurConnectionServiceImpl.addRemoteExistingConnection(remoteConnection);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void putExtras(String str, Bundle bundle, Session.Info info) {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "putExtras").putExtras(bundle);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "putExtras").putExtras(bundle);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeExtras(String str, List<String> list, Session.Info info) {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "removeExtra").removeExtras(list);
            } else {
                RemoteConnectionService.this.findConferenceForAction(str, "removeExtra").removeExtras(list);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setAudioRoute(String str, int i, String str2, Session.Info info) {
            RemoteConnectionService.this.hasConnection(str);
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionEvent(String str, String str2, Bundle bundle, Session.Info info) {
            if (RemoteConnectionService.this.mConnectionById.containsKey(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "onConnectionEvent").onConnectionEvent(str2, bundle);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationSuccess(String str, Session.Info info) throws RemoteException {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "onRttInitiationSuccess").onRttInitiationSuccess();
            } else {
                Log.w(this, "onRttInitiationSuccess called on a remote conference", new Object[0]);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationFailure(String str, int i, Session.Info info) throws RemoteException {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "onRttInitiationFailure").onRttInitiationFailure(i);
            } else {
                Log.w(this, "onRttInitiationFailure called on a remote conference", new Object[0]);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttSessionRemotelyTerminated(String str, Session.Info info) throws RemoteException {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "onRttSessionRemotelyTerminated").onRttSessionRemotelyTerminated();
            } else {
                Log.w(this, "onRttSessionRemotelyTerminated called on a remote conference", new Object[0]);
            }
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRemoteRttRequest(String str, Session.Info info) throws RemoteException {
            if (RemoteConnectionService.this.hasConnection(str)) {
                RemoteConnectionService.this.findConnectionForAction(str, "onRemoteRttRequest").onRemoteRttRequest();
            } else {
                Log.w(this, "onRemoteRttRequest called on a remote conference", new Object[0]);
            }
        }
    };
    private final ConnectionServiceAdapterServant mServant = new ConnectionServiceAdapterServant(this.mServantDelegate);
    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.telecom.RemoteConnectionService.2
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Iterator it = RemoteConnectionService.this.mConnectionById.values().iterator();
            while (it.hasNext()) {
                ((RemoteConnection) it.next()).setDestroyed();
            }
            Iterator it2 = RemoteConnectionService.this.mConferenceById.values().iterator();
            while (it2.hasNext()) {
                ((RemoteConference) it2.next()).setDestroyed();
            }
            RemoteConnectionService.this.mConnectionById.clear();
            RemoteConnectionService.this.mConferenceById.clear();
            RemoteConnectionService.this.mPendingConnections.clear();
            RemoteConnectionService.this.mOutgoingConnectionServiceRpc.asBinder().unlinkToDeath(RemoteConnectionService.this.mDeathRecipient, 0);
        }
    };
    private final Map<String, RemoteConnection> mConnectionById = new HashMap();
    private final Map<String, RemoteConference> mConferenceById = new HashMap();
    private final Set<RemoteConnection> mPendingConnections = new HashSet();

    RemoteConnectionService(IConnectionService iConnectionService, ConnectionService connectionService) throws RemoteException {
        this.mOutgoingConnectionServiceRpc = iConnectionService;
        iConnectionService.asBinder().linkToDeath(this.mDeathRecipient, 0);
        this.mOurConnectionServiceImpl = connectionService;
    }

    public String toString() {
        return "[RemoteCS - " + this.mOutgoingConnectionServiceRpc.asBinder().toString() + "]";
    }

    final RemoteConnection createRemoteConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest, boolean z) {
        final String string = UUID.randomUUID().toString();
        ConnectionRequest connectionRequestBuild = new ConnectionRequest.Builder().setAccountHandle(connectionRequest.getAccountHandle()).setAddress(connectionRequest.getAddress()).setExtras(connectionRequest.getExtras()).setVideoState(connectionRequest.getVideoState()).setRttPipeFromInCall(connectionRequest.getRttPipeFromInCall()).setRttPipeToInCall(connectionRequest.getRttPipeToInCall()).build();
        try {
            if (this.mConnectionById.isEmpty()) {
                this.mOutgoingConnectionServiceRpc.addConnectionServiceAdapter(this.mServant.getStub(), null);
            }
            RemoteConnection remoteConnection = new RemoteConnection(string, this.mOutgoingConnectionServiceRpc, connectionRequestBuild);
            this.mPendingConnections.add(remoteConnection);
            this.mConnectionById.put(string, remoteConnection);
            this.mOutgoingConnectionServiceRpc.createConnection(phoneAccountHandle, string, connectionRequestBuild, z, false, null);
            remoteConnection.registerCallback(new RemoteConnection.Callback() { // from class: android.telecom.RemoteConnectionService.3
                @Override // android.telecom.RemoteConnection.Callback
                public void onDestroyed(RemoteConnection remoteConnection2) {
                    RemoteConnectionService.this.mConnectionById.remove(string);
                    RemoteConnectionService.this.maybeDisconnectAdapter();
                }
            });
            return remoteConnection;
        } catch (RemoteException e) {
            return RemoteConnection.failure(new DisconnectCause(1, e.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasConnection(String str) {
        return this.mConnectionById.containsKey(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RemoteConnection findConnectionForAction(String str, String str2) {
        if (this.mConnectionById.containsKey(str)) {
            return this.mConnectionById.get(str);
        }
        Log.w(this, "%s - Cannot find Connection %s", str2, str);
        return NULL_CONNECTION;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RemoteConference findConferenceForAction(String str, String str2) {
        if (this.mConferenceById.containsKey(str)) {
            return this.mConferenceById.get(str);
        }
        Log.w(this, "%s - Cannot find Conference %s", str2, str);
        return NULL_CONFERENCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeDisconnectAdapter() {
        if (this.mConnectionById.isEmpty() && this.mConferenceById.isEmpty()) {
            try {
                this.mOutgoingConnectionServiceRpc.removeConnectionServiceAdapter(this.mServant.getStub(), null);
            } catch (RemoteException unused) {
            }
        }
    }
}
