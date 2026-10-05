package android.se.omapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
public final class SEService {
    public static final int IO_ERROR = 1;
    public static final int NO_SUCH_ELEMENT_ERROR = 2;
    private static final String TAG = "OMAPI.SEService";
    private ServiceConnection mConnection;
    private final Context mContext;
    private volatile ISecureElementService mSecureElementService;
    private SEListener mSEListener = new SEListener();
    private final Object mLock = new Object();
    private final HashMap<String, Reader> mReaders = new HashMap<>();

    public interface OnConnectedListener {
        void onConnected();
    }

    public String getVersion() {
        return "3.3";
    }

    private class SEListener extends ISecureElementListener.Stub {
        public Executor mExecutor;
        public OnConnectedListener mListener;

        @Override // android.se.omapi.ISecureElementListener.Stub, android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        private SEListener() {
            this.mListener = null;
            this.mExecutor = null;
        }

        public void onConnected() {
            Executor executor;
            if (this.mListener == null || (executor = this.mExecutor) == null) {
                return;
            }
            executor.execute(new Runnable() { // from class: android.se.omapi.SEService.SEListener.1
                @Override // java.lang.Runnable
                public void run() {
                    SEListener.this.mListener.onConnected();
                }
            });
        }
    }

    public SEService(Context context, Executor executor, OnConnectedListener onConnectedListener) {
        if (context == null || onConnectedListener == null || executor == null) {
            throw new NullPointerException("Arguments must not be null");
        }
        this.mContext = context;
        this.mSEListener.mListener = onConnectedListener;
        this.mSEListener.mExecutor = executor;
        this.mConnection = new ServiceConnection() { // from class: android.se.omapi.SEService.1
            @Override // android.content.ServiceConnection
            public synchronized void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SEService.this.mSecureElementService = ISecureElementService.Stub.asInterface(iBinder);
                if (SEService.this.mSEListener != null) {
                    SEService.this.mSEListener.onConnected();
                }
                Log.i(SEService.TAG, "Service onServiceConnected");
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                SEService.this.mSecureElementService = null;
                Log.i(SEService.TAG, "Service onServiceDisconnected");
            }
        };
        Intent intent = new Intent(ISecureElementService.class.getName());
        intent.setClassName("com.android.se", "com.android.se.SecureElementService");
        if (this.mContext.bindService(intent, this.mConnection, 1)) {
            Log.i(TAG, "bindService successful");
        }
    }

    public boolean isConnected() {
        return this.mSecureElementService != null;
    }

    public Reader[] getReaders() {
        int i;
        Exception e;
        if (this.mSecureElementService == null) {
            throw new IllegalStateException("service not connected to system");
        }
        try {
            String[] readers = this.mSecureElementService.getReaders();
            Reader[] readerArr = new Reader[readers.length];
            int i2 = 0;
            for (String str : readers) {
                if (this.mReaders.get(str) == null) {
                    try {
                        this.mReaders.put(str, new Reader(this, str, getReader(str)));
                        i = i2 + 1;
                        try {
                            readerArr[i2] = this.mReaders.get(str);
                        } catch (Exception e2) {
                            e = e2;
                            Log.e(TAG, "Error adding Reader: " + str, e);
                        }
                    } catch (Exception e3) {
                        i = i2;
                        e = e3;
                    }
                } else {
                    i = i2 + 1;
                    readerArr[i2] = this.mReaders.get(str);
                }
                i2 = i;
            }
            return readerArr;
        } catch (RemoteException e4) {
            throw new RuntimeException(e4);
        }
    }

    public void shutdown() {
        synchronized (this.mLock) {
            if (this.mSecureElementService != null) {
                Iterator<Reader> it = this.mReaders.values().iterator();
                while (it.hasNext()) {
                    try {
                        it.next().closeSessions();
                    } catch (Exception unused) {
                    }
                }
            }
            try {
                this.mContext.unbindService(this.mConnection);
            } catch (IllegalArgumentException unused2) {
            }
            this.mSecureElementService = null;
        }
    }

    ISecureElementListener getListener() {
        return this.mSEListener;
    }

    private ISecureElementReader getReader(String str) {
        try {
            return this.mSecureElementService.getReader(str);
        } catch (RemoteException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
