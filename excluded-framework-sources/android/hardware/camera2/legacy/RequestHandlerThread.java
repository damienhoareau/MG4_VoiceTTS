package android.hardware.camera2.legacy;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.MessageQueue;

/* JADX INFO: loaded from: classes.dex */
public class RequestHandlerThread extends HandlerThread {
    public static final int MSG_POKE_IDLE_HANDLER = -1;
    private Handler.Callback mCallback;
    private volatile Handler mHandler;
    private final ConditionVariable mIdle;
    private final MessageQueue.IdleHandler mIdleHandler;
    private final ConditionVariable mStarted;

    public RequestHandlerThread(String str, Handler.Callback callback) {
        super(str, 10);
        this.mStarted = new ConditionVariable(false);
        this.mIdle = new ConditionVariable(true);
        this.mIdleHandler = new MessageQueue.IdleHandler() { // from class: android.hardware.camera2.legacy.RequestHandlerThread.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                RequestHandlerThread.this.mIdle.open();
                return false;
            }
        };
        this.mCallback = callback;
    }

    @Override // android.os.HandlerThread
    protected void onLooperPrepared() {
        this.mHandler = new Handler(getLooper(), this.mCallback);
        this.mStarted.open();
    }

    public void waitUntilStarted() {
        this.mStarted.block();
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public Handler waitAndGetHandler() {
        waitUntilStarted();
        return getHandler();
    }

    public boolean hasAnyMessages(int[] iArr) {
        synchronized (this.mHandler.getLooper().getQueue()) {
            for (int i : iArr) {
                if (this.mHandler.hasMessages(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void removeMessages(int[] iArr) {
        synchronized (this.mHandler.getLooper().getQueue()) {
            for (int i : iArr) {
                this.mHandler.removeMessages(i);
            }
        }
    }

    public void waitUntilIdle() {
        Handler handlerWaitAndGetHandler = waitAndGetHandler();
        MessageQueue queue = handlerWaitAndGetHandler.getLooper().getQueue();
        if (queue.isIdle()) {
            return;
        }
        this.mIdle.close();
        queue.addIdleHandler(this.mIdleHandler);
        handlerWaitAndGetHandler.sendEmptyMessage(-1);
        if (queue.isIdle()) {
            return;
        }
        this.mIdle.block();
    }
}
