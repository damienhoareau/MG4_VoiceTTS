package android.content;

import android.accounts.Account;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Log;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractThreadedSyncAdapter {
    private static final boolean ENABLE_LOG;

    @Deprecated
    public static final int LOG_SYNC_DETAILS = 2743;
    private static final String TAG = "SyncAdapter";
    private boolean mAllowParallelSyncs;
    private final boolean mAutoInitialize;
    private final Context mContext;
    private final ISyncAdapterImpl mISyncAdapterImpl;
    private final AtomicInteger mNumSyncStarts;
    private final Object mSyncThreadLock;
    private final HashMap<Account, SyncThread> mSyncThreads;

    public abstract void onPerformSync(Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult);

    public void onSecurityException(Account account, Bundle bundle, String str, SyncResult syncResult) {
    }

    public boolean onUnsyncableAccount() {
        return true;
    }

    static {
        ENABLE_LOG = Build.IS_DEBUGGABLE && Log.isLoggable(TAG, 3);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean z) {
        this(context, z, false);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean z, boolean z2) {
        this.mSyncThreads = new HashMap<>();
        this.mSyncThreadLock = new Object();
        this.mContext = context;
        this.mISyncAdapterImpl = new ISyncAdapterImpl();
        this.mNumSyncStarts = new AtomicInteger(0);
        this.mAutoInitialize = z;
        this.mAllowParallelSyncs = z2;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Account toSyncKey(Account account) {
        if (this.mAllowParallelSyncs) {
            return account;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ISyncAdapterImpl extends ISyncAdapter.Stub {
        private ISyncAdapterImpl() {
        }

        @Override // android.content.ISyncAdapter
        public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback iSyncAdapterUnsyncableAccountCallback) {
            Handler.getMain().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.content.-$$Lambda$AbstractThreadedSyncAdapter$ISyncAdapterImpl$L6ZtOCe8gjKwJj0908ytPlrD8Rc
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((AbstractThreadedSyncAdapter) obj).handleOnUnsyncableAccount((ISyncAdapterUnsyncableAccountCallback) obj2);
                }
            }, AbstractThreadedSyncAdapter.this, iSyncAdapterUnsyncableAccountCallback));
        }

        @Override // android.content.ISyncAdapter
        public void startSync(ISyncContext iSyncContext, String str, Account account, Bundle bundle) {
            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                if (bundle != null) {
                    bundle.size();
                }
                Log.d(AbstractThreadedSyncAdapter.TAG, "startSync() start " + str + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + account + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + bundle);
            }
            try {
                try {
                    SyncContext syncContext = new SyncContext(iSyncContext);
                    Account syncKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        boolean z = false;
                        if (!AbstractThreadedSyncAdapter.this.mSyncThreads.containsKey(syncKey)) {
                            if (!AbstractThreadedSyncAdapter.this.mAutoInitialize || bundle == null || !bundle.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
                                SyncThread syncThread = new SyncThread("SyncAdapterThread-" + AbstractThreadedSyncAdapter.this.mNumSyncStarts.incrementAndGet(), syncContext, str, account, bundle);
                                AbstractThreadedSyncAdapter.this.mSyncThreads.put(syncKey, syncThread);
                                syncThread.start();
                            } else {
                                try {
                                    if (ContentResolver.getIsSyncable(account, str) < 0) {
                                        ContentResolver.setIsSyncable(account, str, 1);
                                    }
                                    syncContext.onFinished(new SyncResult());
                                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                        Log.d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                                        return;
                                    }
                                    return;
                                } catch (Throwable th) {
                                    syncContext.onFinished(new SyncResult());
                                    throw th;
                                }
                            }
                        } else {
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.d(AbstractThreadedSyncAdapter.TAG, "  alreadyInProgress");
                            }
                            z = true;
                        }
                        if (z) {
                            syncContext.onFinished(SyncResult.ALREADY_IN_PROGRESS);
                        }
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                        }
                    }
                } catch (Throwable th2) {
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                    }
                    throw th2;
                }
            } catch (Error | RuntimeException e) {
                if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                    Log.d(AbstractThreadedSyncAdapter.TAG, "startSync() caught exception", e);
                }
                throw e;
            }
        }

        @Override // android.content.ISyncAdapter
        public void cancelSync(ISyncContext iSyncContext) {
            SyncThread syncThread = null;
            try {
                try {
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        for (SyncThread syncThread2 : AbstractThreadedSyncAdapter.this.mSyncThreads.values()) {
                            if (syncThread2.mSyncContext.getSyncContextBinder() == iSyncContext.asBinder()) {
                                syncThread = syncThread2;
                                break;
                            }
                        }
                    }
                    if (syncThread != null) {
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "cancelSync() " + syncThread.mAuthority + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + syncThread.mAccount);
                        }
                        if (AbstractThreadedSyncAdapter.this.mAllowParallelSyncs) {
                            AbstractThreadedSyncAdapter.this.onSyncCanceled(syncThread);
                        } else {
                            AbstractThreadedSyncAdapter.this.onSyncCanceled();
                        }
                    } else if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.w(AbstractThreadedSyncAdapter.TAG, "cancelSync() unknown context");
                    }
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.d(AbstractThreadedSyncAdapter.TAG, "cancelSync() finishing");
                    }
                } catch (Throwable th) {
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.d(AbstractThreadedSyncAdapter.TAG, "cancelSync() finishing");
                    }
                    throw th;
                }
            } catch (Error | RuntimeException e) {
                if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                    Log.d(AbstractThreadedSyncAdapter.TAG, "cancelSync() caught exception", e);
                }
                throw e;
            }
        }
    }

    private class SyncThread extends Thread {
        private final Account mAccount;
        private final String mAuthority;
        private final Bundle mExtras;
        private final SyncContext mSyncContext;
        private final Account mThreadsKey;

        private SyncThread(String str, SyncContext syncContext, String str2, Account account, Bundle bundle) {
            super(str);
            this.mSyncContext = syncContext;
            this.mAuthority = str2;
            this.mAccount = account;
            this.mExtras = bundle;
            this.mThreadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
        }

        /* JADX WARN: Code duplicated, block: B:66:0x00ea A[Catch: all -> 0x00df, TryCatch #11 {all -> 0x00df, blocks: (B:6:0x0020, B:8:0x0026, B:10:0x002c, B:25:0x0065, B:27:0x006b, B:28:0x0072, B:64:0x00e4, B:66:0x00ea, B:67:0x00f1, B:69:0x00f3, B:71:0x00f9, B:72:0x0100), top: B:115:0x0020 }] */
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws Throwable {
            Process.setThreadPriority(10);
            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                Log.d(AbstractThreadedSyncAdapter.TAG, "Thread started");
            }
            Trace.traceBegin(128L, this.mAuthority);
            SyncResult syncResult = new SyncResult();
            ContentProviderClient contentProviderClient = null;
            try {
                try {
                    if (isCanceled()) {
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "Already canceled");
                        }
                        Trace.traceEnd(128L);
                        if (!isCanceled()) {
                            this.mSyncContext.onFinished(syncResult);
                        }
                        synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                            AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                        }
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                            return;
                        }
                        return;
                    }
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.d(AbstractThreadedSyncAdapter.TAG, "Calling onPerformSync...");
                    }
                    ContentProviderClient contentProviderClientAcquireContentProviderClient = AbstractThreadedSyncAdapter.this.mContext.getContentResolver().acquireContentProviderClient(this.mAuthority);
                    try {
                        if (contentProviderClientAcquireContentProviderClient != null) {
                            AbstractThreadedSyncAdapter.this.onPerformSync(this.mAccount, this.mExtras, this.mAuthority, contentProviderClientAcquireContentProviderClient, syncResult);
                        } else {
                            syncResult.databaseError = true;
                        }
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "onPerformSync done");
                        }
                        Trace.traceEnd(128L);
                        if (contentProviderClientAcquireContentProviderClient != null) {
                            contentProviderClientAcquireContentProviderClient.release();
                        }
                        if (!isCanceled()) {
                            this.mSyncContext.onFinished(syncResult);
                        }
                        synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                            AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                        }
                        if (!AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            return;
                        }
                    } catch (Error e) {
                        e = e;
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "caught exception", e);
                        }
                        throw e;
                    } catch (SecurityException e2) {
                        e = e2;
                        contentProviderClient = contentProviderClientAcquireContentProviderClient;
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "SecurityException", e);
                        }
                        AbstractThreadedSyncAdapter.this.onSecurityException(this.mAccount, this.mExtras, this.mAuthority, syncResult);
                        syncResult.databaseError = true;
                        Trace.traceEnd(128L);
                        if (contentProviderClient != null) {
                            contentProviderClient.release();
                        }
                        if (!isCanceled()) {
                            this.mSyncContext.onFinished(syncResult);
                        }
                        synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                            AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                        }
                        if (!AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            return;
                        }
                    } catch (RuntimeException e3) {
                        e = e3;
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "caught exception", e);
                        }
                        throw e;
                    } catch (Throwable th) {
                        th = th;
                        contentProviderClient = contentProviderClientAcquireContentProviderClient;
                        Trace.traceEnd(128L);
                        if (contentProviderClient != null) {
                            contentProviderClient.release();
                        }
                        if (!isCanceled()) {
                            this.mSyncContext.onFinished(syncResult);
                        }
                        synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                            AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                        }
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                        }
                        throw th;
                    }
                    Log.d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Error e4) {
                e = e4;
            } catch (SecurityException e5) {
                e = e5;
            } catch (RuntimeException e6) {
                e = e6;
            }
        }

        private boolean isCanceled() {
            return Thread.currentThread().isInterrupted();
        }
    }

    public final IBinder getSyncAdapterBinder() {
        return this.mISyncAdapterImpl.asBinder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback iSyncAdapterUnsyncableAccountCallback) {
        boolean zOnUnsyncableAccount;
        try {
            zOnUnsyncableAccount = onUnsyncableAccount();
        } catch (RuntimeException e) {
            Log.e(TAG, "Exception while calling onUnsyncableAccount, assuming 'true'", e);
            zOnUnsyncableAccount = true;
        }
        try {
            iSyncAdapterUnsyncableAccountCallback.onUnsyncableAccountDone(zOnUnsyncableAccount);
        } catch (RemoteException e2) {
            Log.e(TAG, "Could not report result of onUnsyncableAccount", e2);
        }
    }

    public void onSyncCanceled() {
        SyncThread syncThread;
        synchronized (this.mSyncThreadLock) {
            syncThread = this.mSyncThreads.get(null);
        }
        if (syncThread != null) {
            syncThread.interrupt();
        }
    }

    public void onSyncCanceled(Thread thread) {
        thread.interrupt();
    }
}
