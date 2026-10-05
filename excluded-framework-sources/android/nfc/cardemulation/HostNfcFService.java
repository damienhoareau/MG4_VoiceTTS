package android.nfc.cardemulation;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public abstract class HostNfcFService extends Service {
    public static final int DEACTIVATION_LINK_LOSS = 0;
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSENGER = "messenger";
    public static final int MSG_COMMAND_PACKET = 0;
    public static final int MSG_DEACTIVATED = 2;
    public static final int MSG_RESPONSE_PACKET = 1;
    public static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.HOST_NFCF_SERVICE";
    public static final String SERVICE_META_DATA = "android.nfc.cardemulation.host_nfcf_service";
    static final String TAG = "NfcFService";
    Messenger mNfcService = null;
    final Messenger mMessenger = new Messenger(new MsgHandler());

    public abstract void onDeactivated(int i);

    public abstract byte[] processNfcFPacket(byte[] bArr, Bundle bundle);

    final class MsgHandler extends Handler {
        MsgHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        HostNfcFService.this.mNfcService = null;
                        HostNfcFService.this.onDeactivated(message.arg1);
                        return;
                    } else {
                        super.handleMessage(message);
                        return;
                    }
                }
                if (HostNfcFService.this.mNfcService == null) {
                    Log.e(HostNfcFService.TAG, "Response not sent; service was deactivated.");
                    return;
                }
                try {
                    message.replyTo = HostNfcFService.this.mMessenger;
                    HostNfcFService.this.mNfcService.send(message);
                    return;
                } catch (RemoteException unused) {
                    Log.e(HostNfcFService.TAG, "RemoteException calling into NfcService.");
                    return;
                }
            }
            Bundle data = message.getData();
            if (data == null) {
                return;
            }
            if (HostNfcFService.this.mNfcService == null) {
                HostNfcFService.this.mNfcService = message.replyTo;
            }
            byte[] byteArray = data.getByteArray("data");
            if (byteArray != null) {
                byte[] bArrProcessNfcFPacket = HostNfcFService.this.processNfcFPacket(byteArray, null);
                if (bArrProcessNfcFPacket != null) {
                    if (HostNfcFService.this.mNfcService == null) {
                        Log.e(HostNfcFService.TAG, "Response not sent; service was deactivated.");
                        return;
                    }
                    Message messageObtain = Message.obtain((Handler) null, 1);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("data", bArrProcessNfcFPacket);
                    messageObtain.setData(bundle);
                    messageObtain.replyTo = HostNfcFService.this.mMessenger;
                    try {
                        HostNfcFService.this.mNfcService.send(messageObtain);
                        return;
                    } catch (RemoteException unused2) {
                        Log.e("TAG", "Response not sent; RemoteException calling into NfcService.");
                        return;
                    }
                }
                return;
            }
            Log.e(HostNfcFService.TAG, "Received MSG_COMMAND_PACKET without data.");
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }

    public final void sendResponsePacket(byte[] bArr) {
        Message messageObtain = Message.obtain((Handler) null, 1);
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", bArr);
        messageObtain.setData(bundle);
        try {
            this.mMessenger.send(messageObtain);
        } catch (RemoteException unused) {
            Log.e("TAG", "Local messenger has died.");
        }
    }
}
