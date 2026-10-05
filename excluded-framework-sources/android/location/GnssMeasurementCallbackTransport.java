package android.location;

import android.content.Context;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
class GnssMeasurementCallbackTransport extends LocalListenerHelper<GnssMeasurementsEvent.Callback> {
    private final IGnssMeasurementsListener mListenerTransport;
    private final ILocationManager mLocationManager;

    public GnssMeasurementCallbackTransport(Context context, ILocationManager iLocationManager) {
        super(context, "GnssMeasurementListenerTransport");
        this.mListenerTransport = new ListenerTransport();
        this.mLocationManager = iLocationManager;
    }

    @Override // android.location.LocalListenerHelper
    protected boolean registerWithServer() throws RemoteException {
        return this.mLocationManager.addGnssMeasurementsListener(this.mListenerTransport, getContext().getPackageName());
    }

    @Override // android.location.LocalListenerHelper
    protected void unregisterFromServer() throws RemoteException {
        this.mLocationManager.removeGnssMeasurementsListener(this.mListenerTransport);
    }

    private class ListenerTransport extends IGnssMeasurementsListener.Stub {
        private ListenerTransport() {
        }

        @Override // android.location.IGnssMeasurementsListener
        public void onGnssMeasurementsReceived(final GnssMeasurementsEvent gnssMeasurementsEvent) {
            GnssMeasurementCallbackTransport.this.foreach(new LocalListenerHelper.ListenerOperation<GnssMeasurementsEvent.Callback>() { // from class: android.location.GnssMeasurementCallbackTransport.ListenerTransport.1
                @Override // android.location.LocalListenerHelper.ListenerOperation
                public void execute(GnssMeasurementsEvent.Callback callback) throws RemoteException {
                    callback.onGnssMeasurementsReceived(gnssMeasurementsEvent);
                }
            });
        }

        @Override // android.location.IGnssMeasurementsListener
        public void onStatusChanged(final int i) {
            GnssMeasurementCallbackTransport.this.foreach(new LocalListenerHelper.ListenerOperation<GnssMeasurementsEvent.Callback>() { // from class: android.location.GnssMeasurementCallbackTransport.ListenerTransport.2
                @Override // android.location.LocalListenerHelper.ListenerOperation
                public void execute(GnssMeasurementsEvent.Callback callback) throws RemoteException {
                    callback.onStatusChanged(i);
                }
            });
        }
    }
}
