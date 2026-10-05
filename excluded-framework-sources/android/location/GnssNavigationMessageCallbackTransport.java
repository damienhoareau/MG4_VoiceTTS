package android.location;

import android.content.Context;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
class GnssNavigationMessageCallbackTransport extends LocalListenerHelper<GnssNavigationMessage.Callback> {
    private final IGnssNavigationMessageListener mListenerTransport;
    private final ILocationManager mLocationManager;

    public GnssNavigationMessageCallbackTransport(Context context, ILocationManager iLocationManager) {
        super(context, "GnssNavigationMessageCallbackTransport");
        this.mListenerTransport = new ListenerTransport();
        this.mLocationManager = iLocationManager;
    }

    @Override // android.location.LocalListenerHelper
    protected boolean registerWithServer() throws RemoteException {
        return this.mLocationManager.addGnssNavigationMessageListener(this.mListenerTransport, getContext().getPackageName());
    }

    @Override // android.location.LocalListenerHelper
    protected void unregisterFromServer() throws RemoteException {
        this.mLocationManager.removeGnssNavigationMessageListener(this.mListenerTransport);
    }

    private class ListenerTransport extends IGnssNavigationMessageListener.Stub {
        private ListenerTransport() {
        }

        @Override // android.location.IGnssNavigationMessageListener
        public void onGnssNavigationMessageReceived(final GnssNavigationMessage gnssNavigationMessage) {
            GnssNavigationMessageCallbackTransport.this.foreach(new LocalListenerHelper.ListenerOperation<GnssNavigationMessage.Callback>() { // from class: android.location.GnssNavigationMessageCallbackTransport.ListenerTransport.1
                @Override // android.location.LocalListenerHelper.ListenerOperation
                public void execute(GnssNavigationMessage.Callback callback) throws RemoteException {
                    callback.onGnssNavigationMessageReceived(gnssNavigationMessage);
                }
            });
        }

        @Override // android.location.IGnssNavigationMessageListener
        public void onStatusChanged(final int i) {
            GnssNavigationMessageCallbackTransport.this.foreach(new LocalListenerHelper.ListenerOperation<GnssNavigationMessage.Callback>() { // from class: android.location.GnssNavigationMessageCallbackTransport.ListenerTransport.2
                @Override // android.location.LocalListenerHelper.ListenerOperation
                public void execute(GnssNavigationMessage.Callback callback) throws RemoteException {
                    callback.onStatusChanged(i);
                }
            });
        }
    }
}
