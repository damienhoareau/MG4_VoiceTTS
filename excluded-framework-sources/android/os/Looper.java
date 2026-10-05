package android.os;

import android.net.wifi.WifiEnterpriseConfig;
import android.util.Log;
import android.util.Printer;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public final class Looper {
    private static final String TAG = "Looper";
    private static Looper sMainLooper;
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private Printer mLogging;
    final MessageQueue mQueue;
    private long mSlowDeliveryThresholdMs;
    private long mSlowDispatchThresholdMs;
    final Thread mThread = Thread.currentThread();
    private long mTraceTag;

    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean z) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(z));
    }

    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    public static Looper getMainLooper() {
        Looper looper;
        synchronized (Looper.class) {
            looper = sMainLooper;
        }
        return looper;
    }

    /* JADX WARN: Code duplicated, block: B:54:0x0116  */
    public static void loop() {
        long j;
        long j2;
        Looper looper;
        MessageQueue messageQueue;
        String str;
        String str2;
        Printer printer;
        Looper looperMyLooper = myLooper();
        if (looperMyLooper == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        MessageQueue messageQueue2 = looperMyLooper.mQueue;
        Binder.clearCallingIdentity();
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        int i = SystemProperties.getInt("log.looper." + Process.myUid() + "." + Thread.currentThread().getName() + ".slow", 0);
        boolean z = false;
        while (true) {
            Message next = messageQueue2.next();
            if (next == null) {
                return;
            }
            Printer printer2 = looperMyLooper.mLogging;
            if (printer2 != null) {
                printer2.println(">>>>> Dispatching to " + next.target + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + next.callback + ": " + next.what);
            }
            long j3 = looperMyLooper.mTraceTag;
            long j4 = looperMyLooper.mSlowDispatchThresholdMs;
            long j5 = looperMyLooper.mSlowDeliveryThresholdMs;
            if (i > 0) {
                j2 = i;
                j = j2;
            } else {
                j = j4;
                j2 = j5;
            }
            boolean z2 = true;
            boolean z3 = z;
            boolean z4 = j2 > 0 && next.when > 0;
            boolean z5 = j > 0;
            boolean z6 = z4 || z5;
            if (j3 != 0 && Trace.isTagEnabled(j3)) {
                Trace.traceBegin(j3, next.target.getTraceName(next));
            }
            long jUptimeMillis = z6 ? SystemClock.uptimeMillis() : 0L;
            try {
                next.target.dispatchMessage(next);
                long jUptimeMillis2 = z5 ? SystemClock.uptimeMillis() : 0L;
                if (j3 != 0) {
                    Trace.traceEnd(j3);
                }
                if (!z4) {
                    looper = looperMyLooper;
                    messageQueue = messageQueue2;
                    str = TAG;
                    str2 = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    printer = printer2;
                    z2 = z3;
                } else if (!z3) {
                    long j6 = next.when;
                    looper = looperMyLooper;
                    str = TAG;
                    messageQueue = messageQueue2;
                    str2 = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    printer = printer2;
                    if (!showSlowLog(j2, j6, jUptimeMillis, "delivery", next)) {
                        z2 = z3;
                    }
                } else if (jUptimeMillis - next.when <= 10) {
                    Slog.w(TAG, "Drained");
                    looper = looperMyLooper;
                    messageQueue = messageQueue2;
                    str = TAG;
                    str2 = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    printer = printer2;
                    z2 = false;
                } else {
                    looper = looperMyLooper;
                    messageQueue = messageQueue2;
                    str = TAG;
                    str2 = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                    printer = printer2;
                    z2 = z3;
                }
                if (z5) {
                    showSlowLog(j, jUptimeMillis, jUptimeMillis2, "dispatch", next);
                }
                if (printer != null) {
                    printer.println("<<<<< Finished to " + next.target + str2 + next.callback);
                }
                long jClearCallingIdentity2 = Binder.clearCallingIdentity();
                if (jClearCallingIdentity != jClearCallingIdentity2) {
                    Log.wtf(str, "Thread identity changed from 0x" + Long.toHexString(jClearCallingIdentity) + " to 0x" + Long.toHexString(jClearCallingIdentity2) + " while dispatching to " + next.target.getClass().getName() + str2 + next.callback + " what=" + next.what);
                }
                next.recycleUnchecked();
                z = z2;
                looperMyLooper = looper;
                messageQueue2 = messageQueue;
            } catch (Throwable th) {
                if (j3 != 0) {
                    Trace.traceEnd(j3);
                }
                throw th;
            }
        }
    }

    private static boolean showSlowLog(long j, long j2, long j3, String str, Message message) {
        long j4 = j3 - j2;
        if (j4 < j) {
            return false;
        }
        Slog.w(TAG, "Slow " + str + " took " + j4 + "ms " + Thread.currentThread().getName() + " h=" + message.target.getClass().getName() + " c=" + message.callback + " m=" + message.what);
        return true;
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        return myLooper().mQueue;
    }

    private Looper(boolean z) {
        this.mQueue = new MessageQueue(z);
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == this.mThread;
    }

    public void setMessageLogging(Printer printer) {
        this.mLogging = printer;
    }

    public void setTraceTag(long j) {
        this.mTraceTag = j;
    }

    public void setSlowLogThresholdMs(long j, long j2) {
        this.mSlowDispatchThresholdMs = j;
        this.mSlowDeliveryThresholdMs = j2;
    }

    public void quit() {
        this.mQueue.quit(false);
    }

    public void quitSafely() {
        this.mQueue.quit(true);
    }

    public Thread getThread() {
        return this.mThread;
    }

    public MessageQueue getQueue() {
        return this.mQueue;
    }

    public void dump(Printer printer, String str) {
        printer.println(str + toString());
        this.mQueue.dump(printer, str + "  ", null);
    }

    public void dump(Printer printer, String str, Handler handler) {
        printer.println(str + toString());
        this.mQueue.dump(printer, str + "  ", handler);
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mThread.getName());
        protoOutputStream.write(1112396529666L, this.mThread.getId());
        this.mQueue.writeToProto(protoOutputStream, 1146756268035L);
        protoOutputStream.end(jStart);
    }

    public String toString() {
        return "Looper (" + this.mThread.getName() + ", tid " + this.mThread.getId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
