package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import com.android.internal.telecom.IInCallAdapter;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class InCallAdapter {
    private final IInCallAdapter mAdapter;

    public InCallAdapter(IInCallAdapter iInCallAdapter) {
        this.mAdapter = iInCallAdapter;
    }

    public void answerCall(String str, int i) {
        try {
            this.mAdapter.answerCall(str, i);
        } catch (RemoteException unused) {
        }
    }

    public void deflectCall(String str, Uri uri) {
        try {
            this.mAdapter.deflectCall(str, uri);
        } catch (RemoteException unused) {
        }
    }

    public void rejectCall(String str, boolean z, String str2) {
        try {
            this.mAdapter.rejectCall(str, z, str2);
        } catch (RemoteException unused) {
        }
    }

    public void disconnectCall(String str) {
        try {
            this.mAdapter.disconnectCall(str);
        } catch (RemoteException unused) {
        }
    }

    public void holdCall(String str) {
        try {
            this.mAdapter.holdCall(str);
        } catch (RemoteException unused) {
        }
    }

    public void unholdCall(String str) {
        try {
            this.mAdapter.unholdCall(str);
        } catch (RemoteException unused) {
        }
    }

    public void mute(boolean z) {
        try {
            this.mAdapter.mute(z);
        } catch (RemoteException unused) {
        }
    }

    public void setAudioRoute(int i) {
        try {
            this.mAdapter.setAudioRoute(i, null);
        } catch (RemoteException unused) {
        }
    }

    public void requestBluetoothAudio(String str) {
        try {
            this.mAdapter.setAudioRoute(2, str);
        } catch (RemoteException unused) {
        }
    }

    public void playDtmfTone(String str, char c) {
        try {
            this.mAdapter.playDtmfTone(str, c);
        } catch (RemoteException unused) {
        }
    }

    public void stopDtmfTone(String str) {
        try {
            this.mAdapter.stopDtmfTone(str);
        } catch (RemoteException unused) {
        }
    }

    public void postDialContinue(String str, boolean z) {
        try {
            this.mAdapter.postDialContinue(str, z);
        } catch (RemoteException unused) {
        }
    }

    public void phoneAccountSelected(String str, PhoneAccountHandle phoneAccountHandle, boolean z) {
        try {
            this.mAdapter.phoneAccountSelected(str, phoneAccountHandle, z);
        } catch (RemoteException unused) {
        }
    }

    public void conference(String str, String str2) {
        try {
            this.mAdapter.conference(str, str2);
        } catch (RemoteException unused) {
        }
    }

    public void splitFromConference(String str) {
        try {
            this.mAdapter.splitFromConference(str);
        } catch (RemoteException unused) {
        }
    }

    public void mergeConference(String str) {
        try {
            this.mAdapter.mergeConference(str);
        } catch (RemoteException unused) {
        }
    }

    public void swapConference(String str) {
        try {
            this.mAdapter.swapConference(str);
        } catch (RemoteException unused) {
        }
    }

    public void pullExternalCall(String str) {
        try {
            this.mAdapter.pullExternalCall(str);
        } catch (RemoteException unused) {
        }
    }

    public void sendCallEvent(String str, String str2, int i, Bundle bundle) {
        try {
            this.mAdapter.sendCallEvent(str, str2, i, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void putExtras(String str, Bundle bundle) {
        try {
            this.mAdapter.putExtras(str, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void putExtra(String str, String str2, boolean z) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(str2, z);
            this.mAdapter.putExtras(str, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void putExtra(String str, String str2, int i) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt(str2, i);
            this.mAdapter.putExtras(str, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void putExtra(String str, String str2, String str3) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(str2, str3);
            this.mAdapter.putExtras(str, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void removeExtras(String str, List<String> list) {
        try {
            this.mAdapter.removeExtras(str, list);
        } catch (RemoteException unused) {
        }
    }

    public void turnProximitySensorOn() {
        try {
            this.mAdapter.turnOnProximitySensor();
        } catch (RemoteException unused) {
        }
    }

    public void turnProximitySensorOff(boolean z) {
        try {
            this.mAdapter.turnOffProximitySensor(z);
        } catch (RemoteException unused) {
        }
    }

    public void sendRttRequest(String str) {
        try {
            this.mAdapter.sendRttRequest(str);
        } catch (RemoteException unused) {
        }
    }

    public void respondToRttRequest(String str, int i, boolean z) {
        try {
            this.mAdapter.respondToRttRequest(str, i, z);
        } catch (RemoteException unused) {
        }
    }

    public void stopRtt(String str) {
        try {
            this.mAdapter.stopRtt(str);
        } catch (RemoteException unused) {
        }
    }

    public void setRttMode(String str, int i) {
        try {
            this.mAdapter.setRttMode(str, i);
        } catch (RemoteException unused) {
        }
    }

    public void handoverTo(String str, PhoneAccountHandle phoneAccountHandle, int i, Bundle bundle) {
        try {
            this.mAdapter.handoverTo(str, phoneAccountHandle, i, bundle);
        } catch (RemoteException unused) {
        }
    }
}
