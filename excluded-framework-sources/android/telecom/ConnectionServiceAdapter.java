package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes2.dex */
final class ConnectionServiceAdapter implements IBinder.DeathRecipient {
    private final Set<IConnectionServiceAdapter> mAdapters = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9f, 1));

    ConnectionServiceAdapter() {
    }

    void addAdapter(IConnectionServiceAdapter iConnectionServiceAdapter) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            if (it.next().asBinder() == iConnectionServiceAdapter.asBinder()) {
                Log.w(this, "Ignoring duplicate adapter addition.", new Object[0]);
                return;
            }
        }
        if (this.mAdapters.add(iConnectionServiceAdapter)) {
            try {
                iConnectionServiceAdapter.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
                this.mAdapters.remove(iConnectionServiceAdapter);
            }
        }
    }

    void removeAdapter(IConnectionServiceAdapter iConnectionServiceAdapter) {
        if (iConnectionServiceAdapter != null) {
            for (IConnectionServiceAdapter iConnectionServiceAdapter2 : this.mAdapters) {
                if (iConnectionServiceAdapter2.asBinder() == iConnectionServiceAdapter.asBinder() && this.mAdapters.remove(iConnectionServiceAdapter2)) {
                    iConnectionServiceAdapter.asBinder().unlinkToDeath(this, 0);
                    return;
                }
            }
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            IConnectionServiceAdapter next = it.next();
            if (!next.asBinder().isBinderAlive()) {
                it.remove();
                next.asBinder().unlinkToDeath(this, 0);
            }
        }
    }

    void handleCreateConnectionComplete(String str, ConnectionRequest connectionRequest, ParcelableConnection parcelableConnection) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().handleCreateConnectionComplete(str, connectionRequest, parcelableConnection, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setActive(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setActive(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setRinging(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setRinging(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setDialing(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setDialing(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setPulling(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setPulling(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setDisconnected(String str, DisconnectCause disconnectCause) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setDisconnected(str, disconnectCause, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setOnHold(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setOnHold(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setRingbackRequested(String str, boolean z) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setRingbackRequested(str, z, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setConnectionCapabilities(String str, int i) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setConnectionCapabilities(str, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setConnectionProperties(String str, int i) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setConnectionProperties(str, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setIsConferenced(String str, String str2) {
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Log.d(this, "sending connection %s with conference %s", str, str2);
                iConnectionServiceAdapter.setIsConferenced(str, str2, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onConferenceMergeFailed(String str) {
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Log.d(this, "merge failed for call %s", str);
                iConnectionServiceAdapter.setConferenceMergeFailed(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void removeCall(String str) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().removeCall(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onPostDialWait(String str, String str2) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onPostDialWait(str, str2, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onPostDialChar(String str, char c) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onPostDialChar(str, c, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void addConferenceCall(String str, ParcelableConference parcelableConference) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().addConferenceCall(str, parcelableConference, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void queryRemoteConnectionServices(RemoteServiceCallback remoteServiceCallback) {
        if (this.mAdapters.size() == 1) {
            try {
                this.mAdapters.iterator().next().queryRemoteConnectionServices(remoteServiceCallback, Log.getExternalSession());
            } catch (RemoteException e) {
                Log.e(this, e, "Exception trying to query for remote CSs", new Object[0]);
            }
        }
    }

    void setVideoProvider(String str, Connection.VideoProvider videoProvider) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setVideoProvider(str, videoProvider == null ? null : videoProvider.getInterface(), Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setIsVoipAudioMode(String str, boolean z) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setIsVoipAudioMode(str, z, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setStatusHints(String str, StatusHints statusHints) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setStatusHints(str, statusHints, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setAddress(String str, Uri uri, int i) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setAddress(str, uri, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setCallerDisplayName(String str, String str2, int i) {
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setCallerDisplayName(str, str2, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setVideoState(String str, int i) {
        Log.v(this, "setVideoState: %d", Integer.valueOf(i));
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setVideoState(str, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setConferenceableConnections(String str, List<String> list) {
        Log.v(this, "setConferenceableConnections: %s, %s", str, list);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setConferenceableConnections(str, list, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void addExistingConnection(String str, ParcelableConnection parcelableConnection) {
        Log.v(this, "addExistingConnection: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().addExistingConnection(str, parcelableConnection, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void putExtras(String str, Bundle bundle) {
        Log.v(this, "putExtras: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().putExtras(str, bundle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void putExtra(String str, String str2, boolean z) {
        Log.v(this, "putExtra: %s %s=%b", str, str2, Boolean.valueOf(z));
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Bundle bundle = new Bundle();
                bundle.putBoolean(str2, z);
                iConnectionServiceAdapter.putExtras(str, bundle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void putExtra(String str, String str2, int i) {
        Log.v(this, "putExtra: %s %s=%d", str, str2, Integer.valueOf(i));
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Bundle bundle = new Bundle();
                bundle.putInt(str2, i);
                iConnectionServiceAdapter.putExtras(str, bundle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void putExtra(String str, String str2, String str3) {
        Log.v(this, "putExtra: %s %s=%s", str, str2, str3);
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString(str2, str3);
                iConnectionServiceAdapter.putExtras(str, bundle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void removeExtras(String str, List<String> list) {
        Log.v(this, "removeExtras: %s %s", str, list);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().removeExtras(str, list, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void setAudioRoute(String str, int i, String str2) {
        Log.v(this, "setAudioRoute: %s %s %s", str, CallAudioState.audioRouteToString(i), str2);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().setAudioRoute(str, i, str2, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onConnectionEvent(String str, String str2, Bundle bundle) {
        Log.v(this, "onConnectionEvent: %s", str2);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onConnectionEvent(str, str2, bundle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onRttInitiationSuccess(String str) {
        Log.v(this, "onRttInitiationSuccess: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onRttInitiationSuccess(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onRttInitiationFailure(String str, int i) {
        Log.v(this, "onRttInitiationFailure: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onRttInitiationFailure(str, i, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onRttSessionRemotelyTerminated(String str) {
        Log.v(this, "onRttSessionRemotelyTerminated: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onRttSessionRemotelyTerminated(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onRemoteRttRequest(String str) {
        Log.v(this, "onRemoteRttRequest: %s", str);
        Iterator<IConnectionServiceAdapter> it = this.mAdapters.iterator();
        while (it.hasNext()) {
            try {
                it.next().onRemoteRttRequest(str, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onPhoneAccountChanged(String str, PhoneAccountHandle phoneAccountHandle) {
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Log.d(this, "onPhoneAccountChanged %s", str);
                iConnectionServiceAdapter.onPhoneAccountChanged(str, phoneAccountHandle, Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }

    void onConnectionServiceFocusReleased() {
        for (IConnectionServiceAdapter iConnectionServiceAdapter : this.mAdapters) {
            try {
                Log.d(this, "onConnectionServiceFocusReleased", new Object[0]);
                iConnectionServiceAdapter.onConnectionServiceFocusReleased(Log.getExternalSession());
            } catch (RemoteException unused) {
            }
        }
    }
}
