package android.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class UsbScannerManager {
    private static final String TAG = "UsbScannerManager";
    private Context mContext;
    private IUsbScannerEventListener mListener;
    private MtpScannerConnection mMtpConnection;
    private UsbScannerConnection mUsbConnection;

    public UsbScannerManager(Context context) {
        this.mContext = context;
        this.mUsbConnection = new UsbScannerConnection(this.mContext);
        this.mMtpConnection = new MtpScannerConnection(this.mContext);
    }

    public void connect() {
        if (!this.mUsbConnection.isConnected()) {
            this.mUsbConnection.connect();
        }
        if (this.mMtpConnection.isConnected()) {
            return;
        }
        this.mMtpConnection.connect();
    }

    public void disconnect() {
        if (this.mUsbConnection.isConnected()) {
            this.mUsbConnection.disconnect();
        }
        if (this.mMtpConnection.isConnected()) {
            this.mMtpConnection.disconnect();
        }
    }

    public void registerListener(IUsbScannerEventListener iUsbScannerEventListener) {
        Log.d(TAG, "registerListener");
        this.mListener = iUsbScannerEventListener;
        connect();
    }

    public void unregisterListener(IUsbScannerEventListener iUsbScannerEventListener) {
        Log.d(TAG, "unregisterListener");
        this.mListener = iUsbScannerEventListener;
        disconnect();
    }

    public void setPriority(int i, int i2, int i3) {
        Log.d(TAG, "setPriority, type " + i + "scanType " + i3);
        if (i != 1) {
            return;
        }
        this.mUsbConnection.setPriority(i, i2, i3);
    }

    public int getPriority(int i, int i2) {
        Log.d(TAG, "getPriority, type " + i + "id " + i2);
        if (i != 1) {
            return 0;
        }
        return this.mUsbConnection.getPriority(i, i2);
    }

    public int getScanStatus(int i, int i2) {
        Log.d(TAG, "getScanStatus, type " + i + " id " + i2);
        if (i != 1) {
            return 32;
        }
        return this.mUsbConnection.getScanStatus(i, i2);
    }

    public int getSpecificScanStatus(int i, int i2, int i3) {
        Log.d(TAG, "getSpecificScanStatus, type " + i + " id " + i2 + " portId " + i3);
        if (i != 1) {
            return 32;
        }
        return this.mUsbConnection.getSpecificScanStatus(i, i2, i3);
    }

    public class UsbScannerConnection implements ServiceConnection {
        private boolean mConnected;
        private Context mContext;
        private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.media.UsbScannerManager.UsbScannerConnection.1
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                Log.d(UsbScannerManager.TAG, "onBindingDied(): ");
                if (UsbScannerConnection.this.mUsbService == null) {
                    return;
                }
                synchronized (this) {
                    UsbScannerConnection.this.mUsbService.asBinder().unlinkToDeath(UsbScannerConnection.this.mDeathRecipient, 0);
                    UsbScannerConnection.this.disconnect();
                    UsbScannerConnection.this.connect();
                }
            }
        };
        private IUsbScannerService mUsbService;

        UsbScannerConnection(Context context) {
            this.mContext = context;
        }

        public void connect() {
            synchronized (this) {
                if (!this.mConnected) {
                    Intent intent = new Intent(IUsbScannerService.class.getName());
                    intent.setComponent(new ComponentName("com.android.providers.media", "com.android.providers.media.UsbScannerService"));
                    this.mContext.bindService(intent, this, 1);
                }
            }
        }

        public void disconnect() {
            synchronized (this) {
                if (this.mConnected) {
                    try {
                        try {
                            unregisterListener(UsbScannerManager.this.mListener);
                            this.mContext.unbindService(this);
                            this.mUsbService = null;
                        } catch (IllegalArgumentException e) {
                            Log.e(UsbScannerManager.TAG, "exception in unbindService " + e);
                            this.mUsbService = null;
                        }
                        this.mConnected = false;
                    } catch (Throwable th) {
                        this.mUsbService = null;
                        this.mConnected = false;
                        throw th;
                    }
                }
            }
        }

        public void registerListener(IUsbScannerEventListener iUsbScannerEventListener) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "registerListener(), null service");
                return;
            }
            try {
                this.mUsbService.registerListener(iUsbScannerEventListener);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in registerListener " + e);
            }
        }

        public void unregisterListener(IUsbScannerEventListener iUsbScannerEventListener) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "unregisterListener(), null service");
                return;
            }
            try {
                this.mUsbService.unregisterListener(iUsbScannerEventListener);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in unregisterListener " + e);
            }
        }

        public void setPriority(int i, int i2, int i3) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "can't set priority, null service");
                return;
            }
            try {
                this.mUsbService.setPriority(i, i2, i3);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in setPriority " + e);
            }
        }

        public int getPriority(int i, int i2) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "can't get priority, null service");
                return 0;
            }
            try {
                return this.mUsbService.getPriority(i, i2);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in getPriority " + e);
                return 0;
            }
        }

        public int getScanStatus(int i, int i2) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "can't get scan status(), null service");
                return 32;
            }
            try {
                return this.mUsbService.getScanStatus(i, i2);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in getScanStatus " + e);
                return 32;
            }
        }

        public int getSpecificScanStatus(int i, int i2, int i3) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "can't get scan status(), null service");
                return 32;
            }
            try {
                return this.mUsbService.getSpecificScanStatus(i, i2, i3);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in getScanStatus " + e);
                return 32;
            }
        }

        public synchronized boolean isConnected() {
            return this.mUsbService != null && this.mConnected;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(UsbScannerManager.TAG, "onServiceConnected(): " + componentName);
            synchronized (this) {
                try {
                    iBinder.linkToDeath(this.mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                this.mConnected = true;
                this.mUsbService = IUsbScannerService.Stub.asInterface(iBinder);
                registerListener(UsbScannerManager.this.mListener);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(UsbScannerManager.TAG, "onServiceDisconnected(): " + componentName);
        }
    }

    public class MtpScannerConnection implements ServiceConnection {
        private boolean mConnected;
        private Context mContext;
        private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.media.UsbScannerManager.MtpScannerConnection.1
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                Log.d(UsbScannerManager.TAG, "onBindingDied(): ");
                if (MtpScannerConnection.this.mMtpService == null) {
                    return;
                }
                synchronized (this) {
                    MtpScannerConnection.this.mMtpService.asBinder().unlinkToDeath(MtpScannerConnection.this.mDeathRecipient, 0);
                    MtpScannerConnection.this.disconnect();
                    MtpScannerConnection.this.connect();
                }
            }
        };
        private IMtpScannerService mMtpService;

        MtpScannerConnection(Context context) {
            this.mContext = context;
        }

        public void connect() {
            synchronized (this) {
                if (!this.mConnected) {
                    Intent intent = new Intent(IMtpScannerService.class.getName());
                    intent.setComponent(new ComponentName("com.android.mtp", "com.android.mtp.MtpScannerService"));
                    this.mContext.bindService(intent, this, 1);
                }
            }
        }

        public void disconnect() {
            synchronized (this) {
                if (this.mConnected) {
                    try {
                        try {
                            this.mContext.unbindService(this);
                            this.mMtpService = null;
                        } catch (IllegalArgumentException e) {
                            Log.e(UsbScannerManager.TAG, "exception in unbindService " + e);
                            this.mMtpService = null;
                        }
                        this.mConnected = false;
                    } catch (Throwable th) {
                        this.mMtpService = null;
                        this.mConnected = false;
                        throw th;
                    }
                }
            }
        }

        public void registerListener(IUsbScannerEventListener iUsbScannerEventListener) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "registerListener(), null service");
                return;
            }
            try {
                this.mMtpService.registerListener(iUsbScannerEventListener);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in registerListener " + e);
            }
        }

        public void unregisterListener(IUsbScannerEventListener iUsbScannerEventListener) {
            if (!this.mConnected) {
                Log.e(UsbScannerManager.TAG, "unregisterListener(), null service");
                return;
            }
            try {
                this.mMtpService.unregisterListener(iUsbScannerEventListener);
            } catch (RemoteException e) {
                Log.e(UsbScannerManager.TAG, "exception in unregisterListener " + e);
            }
        }

        public synchronized boolean isConnected() {
            return this.mMtpService != null && this.mConnected;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(UsbScannerManager.TAG, "onServiceConnected(): " + componentName);
            synchronized (this) {
                try {
                    iBinder.linkToDeath(this.mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                this.mConnected = true;
                this.mMtpService = IMtpScannerService.Stub.asInterface(iBinder);
                registerListener(UsbScannerManager.this.mListener);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(UsbScannerManager.TAG, "onServiceDisconnected(): " + componentName);
        }
    }
}
