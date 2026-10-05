package android.app.backup;

import android.annotation.SystemApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class RestoreSession {
    static final String TAG = "RestoreSession";
    IRestoreSession mBinder;
    final Context mContext;
    RestoreObserverWrapper mObserver = null;

    public int getAvailableRestoreSets(RestoreObserver restoreObserver, BackupManagerMonitor backupManagerMonitor) {
        try {
            return this.mBinder.getAvailableRestoreSets(new RestoreObserverWrapper(this.mContext, restoreObserver), backupManagerMonitor == null ? null : new BackupManagerMonitorWrapper(backupManagerMonitor));
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't contact server to get available sets");
            return -1;
        }
    }

    public int getAvailableRestoreSets(RestoreObserver restoreObserver) {
        return getAvailableRestoreSets(restoreObserver, null);
    }

    public int restoreAll(long j, RestoreObserver restoreObserver, BackupManagerMonitor backupManagerMonitor) {
        if (this.mObserver != null) {
            Log.d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, restoreObserver);
        try {
            return this.mBinder.restoreAll(j, this.mObserver, backupManagerMonitor == null ? null : new BackupManagerMonitorWrapper(backupManagerMonitor));
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't contact server to restore");
            return -1;
        }
    }

    public int restoreAll(long j, RestoreObserver restoreObserver) {
        return restoreAll(j, restoreObserver, null);
    }

    public int restoreSome(long j, RestoreObserver restoreObserver, BackupManagerMonitor backupManagerMonitor, String[] strArr) {
        if (this.mObserver != null) {
            Log.d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, restoreObserver);
        try {
            return this.mBinder.restoreSome(j, this.mObserver, backupManagerMonitor == null ? null : new BackupManagerMonitorWrapper(backupManagerMonitor), strArr);
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't contact server to restore packages");
            return -1;
        }
    }

    public int restoreSome(long j, RestoreObserver restoreObserver, String[] strArr) {
        return restoreSome(j, restoreObserver, null, strArr);
    }

    public int restorePackage(String str, RestoreObserver restoreObserver, BackupManagerMonitor backupManagerMonitor) {
        if (this.mObserver != null) {
            Log.d(TAG, "restorePackage() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, restoreObserver);
        try {
            return this.mBinder.restorePackage(str, this.mObserver, backupManagerMonitor == null ? null : new BackupManagerMonitorWrapper(backupManagerMonitor));
        } catch (RemoteException unused) {
            Log.d(TAG, "Can't contact server to restore package");
            return -1;
        }
    }

    public int restorePackage(String str, RestoreObserver restoreObserver) {
        return restorePackage(str, restoreObserver, null);
    }

    public void endRestoreSession() {
        try {
            try {
                this.mBinder.endRestoreSession();
            } catch (RemoteException unused) {
                Log.d(TAG, "Can't contact server to get available sets");
            }
        } finally {
            this.mBinder = null;
        }
    }

    RestoreSession(Context context, IRestoreSession iRestoreSession) {
        this.mContext = context;
        this.mBinder = iRestoreSession;
    }

    private class RestoreObserverWrapper extends IRestoreObserver.Stub {
        static final int MSG_RESTORE_FINISHED = 3;
        static final int MSG_RESTORE_SETS_AVAILABLE = 4;
        static final int MSG_RESTORE_STARTING = 1;
        static final int MSG_UPDATE = 2;
        final RestoreObserver mAppObserver;
        final Handler mHandler;

        RestoreObserverWrapper(Context context, RestoreObserver restoreObserver) {
            this.mHandler = new Handler(context.getMainLooper()) { // from class: android.app.backup.RestoreSession.RestoreObserverWrapper.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    int i = message.what;
                    if (i == 1) {
                        RestoreObserverWrapper.this.mAppObserver.restoreStarting(message.arg1);
                        return;
                    }
                    if (i == 2) {
                        RestoreObserverWrapper.this.mAppObserver.onUpdate(message.arg1, (String) message.obj);
                    } else if (i == 3) {
                        RestoreObserverWrapper.this.mAppObserver.restoreFinished(message.arg1);
                    } else {
                        if (i != 4) {
                            return;
                        }
                        RestoreObserverWrapper.this.mAppObserver.restoreSetsAvailable((RestoreSet[]) message.obj);
                    }
                }
            };
            this.mAppObserver = restoreObserver;
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreSetsAvailable(RestoreSet[] restoreSetArr) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(4, restoreSetArr));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreStarting(int i) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, i, 0));
        }

        @Override // android.app.backup.IRestoreObserver
        public void onUpdate(int i, String str) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, i, 0, str));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreFinished(int i) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(3, i, 0));
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
