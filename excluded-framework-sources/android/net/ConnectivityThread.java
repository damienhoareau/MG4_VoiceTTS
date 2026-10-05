package android.net;

import android.os.HandlerThread;
import android.os.Looper;

/* JADX INFO: loaded from: classes.dex */
public final class ConnectivityThread extends HandlerThread {

    private static class Singleton {
        private static final ConnectivityThread INSTANCE = ConnectivityThread.createInstance();

        private Singleton() {
        }
    }

    private ConnectivityThread() {
        super("ConnectivityThread");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ConnectivityThread createInstance() {
        ConnectivityThread connectivityThread = new ConnectivityThread();
        connectivityThread.start();
        return connectivityThread;
    }

    public static ConnectivityThread get() {
        return Singleton.INSTANCE;
    }

    public static Looper getInstanceLooper() {
        return Singleton.INSTANCE.getLooper();
    }
}
