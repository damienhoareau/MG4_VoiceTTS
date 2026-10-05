package android.app.backup;

import android.Manifest;
import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;

/* JADX INFO: loaded from: classes.dex */
public class BackupManager {

    @SystemApi
    public static final int ERROR_AGENT_FAILURE = -1003;

    @SystemApi
    public static final int ERROR_BACKUP_CANCELLED = -2003;

    @SystemApi
    public static final int ERROR_BACKUP_NOT_ALLOWED = -2001;

    @SystemApi
    public static final int ERROR_PACKAGE_NOT_FOUND = -2002;

    @SystemApi
    public static final int ERROR_TRANSPORT_ABORTED = -1000;

    @SystemApi
    public static final int ERROR_TRANSPORT_INVALID = -2;

    @SystemApi
    public static final int ERROR_TRANSPORT_PACKAGE_REJECTED = -1002;

    @SystemApi
    public static final int ERROR_TRANSPORT_QUOTA_EXCEEDED = -1005;

    @SystemApi
    public static final int ERROR_TRANSPORT_UNAVAILABLE = -1;
    public static final String EXTRA_BACKUP_SERVICES_AVAILABLE = "backup_services_available";

    @SystemApi
    public static final int FLAG_NON_INCREMENTAL_BACKUP = 1;

    @SystemApi
    public static final String PACKAGE_MANAGER_SENTINEL = "@pm@";

    @SystemApi
    public static final int SUCCESS = 0;
    private static final String TAG = "BackupManager";
    private static IBackupManager sService;
    private Context mContext;

    private static void checkServiceBinder() {
        if (sService == null) {
            sService = IBackupManager.Stub.asInterface(ServiceManager.getService(Context.BACKUP_SERVICE));
        }
    }

    public BackupManager(Context context) {
        this.mContext = context;
    }

    public void dataChanged() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.dataChanged(this.mContext.getPackageName());
            } catch (RemoteException unused) {
                Log.d(TAG, "dataChanged() couldn't connect");
            }
        }
    }

    public static void dataChanged(String str) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.dataChanged(str);
            } catch (RemoteException unused) {
                Log.e(TAG, "dataChanged(pkg) couldn't connect");
            }
        }
    }

    @Deprecated
    public int requestRestore(RestoreObserver restoreObserver) {
        return requestRestore(restoreObserver, null);
    }

    @SystemApi
    @Deprecated
    public int requestRestore(RestoreObserver restoreObserver, BackupManagerMonitor backupManagerMonitor) {
        Log.w(TAG, "requestRestore(): Since Android P app can no longer request restoring of its backup.");
        return -1;
    }

    @SystemApi
    public RestoreSession beginRestoreSession() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            IRestoreSession iRestoreSessionBeginRestoreSession = iBackupManager.beginRestoreSession(null, null);
            if (iRestoreSessionBeginRestoreSession != null) {
                return new RestoreSession(this.mContext, iRestoreSessionBeginRestoreSession);
            }
            return null;
        } catch (RemoteException unused) {
            Log.e(TAG, "beginRestoreSession() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public void setBackupEnabled(boolean z) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.setBackupEnabled(z);
            } catch (RemoteException unused) {
                Log.e(TAG, "setBackupEnabled() couldn't connect");
            }
        }
    }

    @SystemApi
    public boolean isBackupEnabled() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return false;
        }
        try {
            return iBackupManager.isBackupEnabled();
        } catch (RemoteException unused) {
            Log.e(TAG, "isBackupEnabled() couldn't connect");
            return false;
        }
    }

    @SystemApi
    public boolean isBackupServiceActive(UserHandle userHandle) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BACKUP, "isBackupServiceActive");
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return false;
        }
        try {
            return iBackupManager.isBackupServiceActive(userHandle.getIdentifier());
        } catch (RemoteException unused) {
            Log.e(TAG, "isBackupEnabled() couldn't connect");
            return false;
        }
    }

    @SystemApi
    public void setAutoRestore(boolean z) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.setAutoRestore(z);
            } catch (RemoteException unused) {
                Log.e(TAG, "setAutoRestore() couldn't connect");
            }
        }
    }

    @SystemApi
    public String getCurrentTransport() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.getCurrentTransport();
        } catch (RemoteException unused) {
            Log.e(TAG, "getCurrentTransport() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public String[] listAllTransports() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.listAllTransports();
        } catch (RemoteException unused) {
            Log.e(TAG, "listAllTransports() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public void updateTransportAttributes(ComponentName componentName, String str, Intent intent, String str2, Intent intent2, String str3) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.updateTransportAttributes(componentName, str, intent, str2, intent2, str3);
            } catch (RemoteException unused) {
                Log.e(TAG, "describeTransport() couldn't connect");
            }
        }
    }

    @SystemApi
    @Deprecated
    public String selectBackupTransport(String str) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.selectBackupTransport(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "selectBackupTransport() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public void selectBackupTransport(ComponentName componentName, SelectBackupTransportCallback selectBackupTransportCallback) {
        SelectTransportListenerWrapper selectTransportListenerWrapper;
        checkServiceBinder();
        if (sService != null) {
            if (selectBackupTransportCallback == null) {
                selectTransportListenerWrapper = null;
            } else {
                try {
                    selectTransportListenerWrapper = new SelectTransportListenerWrapper(this.mContext, selectBackupTransportCallback);
                } catch (RemoteException unused) {
                    Log.e(TAG, "selectBackupTransportAsync() couldn't connect");
                    return;
                }
            }
            sService.selectBackupTransportAsync(componentName, selectTransportListenerWrapper);
        }
    }

    @SystemApi
    public void backupNow() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.backupNow();
            } catch (RemoteException unused) {
                Log.e(TAG, "backupNow() couldn't connect");
            }
        }
    }

    @SystemApi
    public long getAvailableRestoreToken(String str) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return 0L;
        }
        try {
            return iBackupManager.getAvailableRestoreToken(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getAvailableRestoreToken() couldn't connect");
            return 0L;
        }
    }

    @SystemApi
    public boolean isAppEligibleForBackup(String str) {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return false;
        }
        try {
            return iBackupManager.isAppEligibleForBackup(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "isAppEligibleForBackup(pkg) couldn't connect");
            return false;
        }
    }

    @SystemApi
    public int requestBackup(String[] strArr, BackupObserver backupObserver) {
        return requestBackup(strArr, backupObserver, null, 0);
    }

    @SystemApi
    public int requestBackup(String[] strArr, BackupObserver backupObserver, BackupManagerMonitor backupManagerMonitor, int i) {
        BackupObserverWrapper backupObserverWrapper;
        checkServiceBinder();
        if (sService == null) {
            return -1;
        }
        BackupManagerMonitorWrapper backupManagerMonitorWrapper = null;
        if (backupObserver == null) {
            backupObserverWrapper = null;
        } else {
            try {
                backupObserverWrapper = new BackupObserverWrapper(this.mContext, backupObserver);
            } catch (RemoteException unused) {
                Log.e(TAG, "requestBackup() couldn't connect");
                return -1;
            }
        }
        if (backupManagerMonitor != null) {
            backupManagerMonitorWrapper = new BackupManagerMonitorWrapper(backupManagerMonitor);
        }
        return sService.requestBackup(strArr, backupObserverWrapper, backupManagerMonitorWrapper, i);
    }

    @SystemApi
    public void cancelBackups() {
        checkServiceBinder();
        IBackupManager iBackupManager = sService;
        if (iBackupManager != null) {
            try {
                iBackupManager.cancelBackups();
            } catch (RemoteException unused) {
                Log.e(TAG, "cancelBackups() couldn't connect.");
            }
        }
    }

    @SystemApi
    public Intent getConfigurationIntent(String str) {
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.getConfigurationIntent(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getConfigurationIntent() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public String getDestinationString(String str) {
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.getDestinationString(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getDestinationString() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public Intent getDataManagementIntent(String str) {
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.getDataManagementIntent(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getDataManagementIntent() couldn't connect");
            return null;
        }
    }

    @SystemApi
    public String getDataManagementLabel(String str) {
        IBackupManager iBackupManager = sService;
        if (iBackupManager == null) {
            return null;
        }
        try {
            return iBackupManager.getDataManagementLabel(str);
        } catch (RemoteException unused) {
            Log.e(TAG, "getDataManagementLabel() couldn't connect");
            return null;
        }
    }

    private class BackupObserverWrapper extends IBackupObserver.Stub {
        static final int MSG_FINISHED = 3;
        static final int MSG_RESULT = 2;
        static final int MSG_UPDATE = 1;
        final Handler mHandler;
        final BackupObserver mObserver;

        BackupObserverWrapper(Context context, BackupObserver backupObserver) {
            this.mHandler = new Handler(context.getMainLooper()) { // from class: android.app.backup.BackupManager.BackupObserverWrapper.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    int i = message.what;
                    if (i == 1) {
                        Pair pair = (Pair) message.obj;
                        BackupObserverWrapper.this.mObserver.onUpdate((String) pair.first, (BackupProgress) pair.second);
                    } else {
                        if (i == 2) {
                            BackupObserverWrapper.this.mObserver.onResult((String) message.obj, message.arg1);
                            return;
                        }
                        if (i == 3) {
                            BackupObserverWrapper.this.mObserver.backupFinished(message.arg1);
                            return;
                        }
                        Log.w(BackupManager.TAG, "Unknown message: " + message);
                    }
                }
            };
            this.mObserver = backupObserver;
        }

        @Override // android.app.backup.IBackupObserver
        public void onUpdate(String str, BackupProgress backupProgress) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, Pair.create(str, backupProgress)));
        }

        @Override // android.app.backup.IBackupObserver
        public void onResult(String str, int i) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, i, 0, str));
        }

        @Override // android.app.backup.IBackupObserver
        public void backupFinished(int i) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(3, i, 0));
        }
    }

    private class SelectTransportListenerWrapper extends ISelectBackupTransportCallback.Stub {
        private final Handler mHandler;
        private final SelectBackupTransportCallback mListener;

        SelectTransportListenerWrapper(Context context, SelectBackupTransportCallback selectBackupTransportCallback) {
            this.mHandler = new Handler(context.getMainLooper());
            this.mListener = selectBackupTransportCallback;
        }

        @Override // android.app.backup.ISelectBackupTransportCallback
        public void onSuccess(final String str) {
            this.mHandler.post(new Runnable() { // from class: android.app.backup.BackupManager.SelectTransportListenerWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    SelectTransportListenerWrapper.this.mListener.onSuccess(str);
                }
            });
        }

        @Override // android.app.backup.ISelectBackupTransportCallback
        public void onFailure(final int i) {
            this.mHandler.post(new Runnable() { // from class: android.app.backup.BackupManager.SelectTransportListenerWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    SelectTransportListenerWrapper.this.mListener.onFailure(i);
                }
            });
        }
    }

    private class BackupManagerMonitorWrapper extends IBackupManagerMonitor.Stub {
        final BackupManagerMonitor mMonitor;

        BackupManagerMonitorWrapper(BackupManagerMonitor backupManagerMonitor) {
            this.mMonitor = backupManagerMonitor;
        }

        @Override // android.app.backup.IBackupManagerMonitor
        public void onEvent(Bundle bundle) throws RemoteException {
            this.mMonitor.onEvent(bundle);
        }
    }
}
