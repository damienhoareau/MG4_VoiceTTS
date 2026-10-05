package android.os;

import android.annotation.SystemApi;
import android.content.Context;
import android.util.Slog;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class IncidentManager {
    private static final String TAG = "IncidentManager";
    private final Context mContext;
    private IIncidentManager mService;

    public IncidentManager(Context context) {
        this.mContext = context;
    }

    public void reportIncident(IncidentReportArgs incidentReportArgs) {
        reportIncidentInternal(incidentReportArgs);
    }

    private class IncidentdDeathRecipient implements IBinder.DeathRecipient {
        private IncidentdDeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (this) {
                IncidentManager.this.mService = null;
            }
        }
    }

    private void reportIncidentInternal(IncidentReportArgs incidentReportArgs) {
        try {
            IIncidentManager iIncidentManagerLocked = getIIncidentManagerLocked();
            if (iIncidentManagerLocked == null) {
                Slog.e(TAG, "reportIncident can't find incident binder service");
            } else {
                iIncidentManagerLocked.reportIncident(incidentReportArgs);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "reportIncident failed", e);
        }
    }

    private IIncidentManager getIIncidentManagerLocked() throws RemoteException {
        IIncidentManager iIncidentManager = this.mService;
        if (iIncidentManager != null) {
            return iIncidentManager;
        }
        synchronized (this) {
            if (this.mService != null) {
                return this.mService;
            }
            IIncidentManager iIncidentManagerAsInterface = IIncidentManager.Stub.asInterface(ServiceManager.getService(Context.INCIDENT_SERVICE));
            this.mService = iIncidentManagerAsInterface;
            if (iIncidentManagerAsInterface != null) {
                iIncidentManagerAsInterface.asBinder().linkToDeath(new IncidentdDeathRecipient(), 0);
            }
            return this.mService;
        }
    }
}
