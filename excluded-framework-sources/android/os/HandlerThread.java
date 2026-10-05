package android.os;

/* JADX INFO: loaded from: classes2.dex */
public class HandlerThread extends Thread {
    private Handler mHandler;
    Looper mLooper;
    int mPriority;
    int mTid;

    protected void onLooperPrepared() {
    }

    public HandlerThread(String str) {
        super(str);
        this.mTid = -1;
        this.mPriority = 0;
    }

    public HandlerThread(String str, int i) {
        super(str);
        this.mTid = -1;
        this.mPriority = i;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            this.mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(this.mPriority);
        onLooperPrepared();
        Looper.loop();
        this.mTid = -1;
    }

    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            while (isAlive() && this.mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        return this.mLooper;
    }

    public Handler getThreadHandler() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(getLooper());
        }
        return this.mHandler;
    }

    public boolean quit() {
        Looper looper = getLooper();
        if (looper == null) {
            return false;
        }
        looper.quit();
        return true;
    }

    public boolean quitSafely() {
        Looper looper = getLooper();
        if (looper == null) {
            return false;
        }
        looper.quitSafely();
        return true;
    }

    public int getThreadId() {
        return this.mTid;
    }
}
