package com.android.internal.os;

import android.os.Binder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: loaded from: classes3.dex */
public class BinderCallsStats {
    private static final int CALL_SESSIONS_POOL_SIZE = 100;
    private static final BinderCallsStats sInstance = new BinderCallsStats();
    private final Queue<CallSession> mCallSessionsPool;
    private volatile boolean mDetailedTracking;
    private final Object mLock;
    private long mStartTime;
    private final SparseArray<UidEntry> mUidEntries;

    public static class CallSession {
        CallStat mCallStat = new CallStat();
        int mCallingUId;
        long mStarted;
    }

    private BinderCallsStats() {
        this.mDetailedTracking = false;
        this.mUidEntries = new SparseArray<>();
        this.mCallSessionsPool = new ConcurrentLinkedQueue();
        this.mLock = new Object();
        this.mStartTime = System.currentTimeMillis();
    }

    public BinderCallsStats(boolean z) {
        this.mDetailedTracking = false;
        this.mUidEntries = new SparseArray<>();
        this.mCallSessionsPool = new ConcurrentLinkedQueue();
        this.mLock = new Object();
        this.mStartTime = System.currentTimeMillis();
        this.mDetailedTracking = z;
    }

    public CallSession callStarted(Binder binder, int i) {
        return callStarted(binder.getClass().getName(), i);
    }

    private CallSession callStarted(String str, int i) {
        CallSession callSessionPoll = this.mCallSessionsPool.poll();
        if (callSessionPoll == null) {
            callSessionPoll = new CallSession();
        }
        callSessionPoll.mCallStat.className = str;
        callSessionPoll.mCallStat.msg = i;
        callSessionPoll.mStarted = getThreadTimeMicro();
        return callSessionPoll;
    }

    public void callEnded(CallSession callSession) {
        Preconditions.checkNotNull(callSession);
        long threadTimeMicro = this.mDetailedTracking ? getThreadTimeMicro() - callSession.mStarted : 1L;
        callSession.mCallingUId = Binder.getCallingUid();
        synchronized (this.mLock) {
            UidEntry uidEntry = this.mUidEntries.get(callSession.mCallingUId);
            if (uidEntry == null) {
                uidEntry = new UidEntry(callSession.mCallingUId);
                this.mUidEntries.put(callSession.mCallingUId, uidEntry);
            }
            if (this.mDetailedTracking) {
                CallStat callStat = uidEntry.mCallStats.get(callSession.mCallStat);
                if (callStat == null) {
                    callStat = new CallStat(callSession.mCallStat.className, callSession.mCallStat.msg);
                    uidEntry.mCallStats.put(callStat, callStat);
                }
                callStat.callCount++;
                callStat.time += threadTimeMicro;
            }
            uidEntry.time += threadTimeMicro;
            uidEntry.callCount++;
        }
        if (this.mCallSessionsPool.size() < 100) {
            this.mCallSessionsPool.add(callSession);
        }
    }

    public void dump(PrintWriter printWriter) {
        long j;
        long j2;
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        printWriter.print("Start time: ");
        printWriter.println(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mStartTime));
        int size = this.mUidEntries.size();
        ArrayList<UidEntry> arrayList = new ArrayList();
        synchronized (this.mLock) {
            j = 0;
            long j3 = 0;
            int i = 0;
            while (i < size) {
                UidEntry uidEntryValueAt = this.mUidEntries.valueAt(i);
                arrayList.add(uidEntryValueAt);
                long j4 = j + uidEntryValueAt.time;
                Long l = (Long) map.get(Integer.valueOf(uidEntryValueAt.uid));
                long j5 = j3;
                map.put(Integer.valueOf(uidEntryValueAt.uid), Long.valueOf(l == null ? uidEntryValueAt.time : l.longValue() + uidEntryValueAt.time));
                Long l2 = (Long) map2.get(Integer.valueOf(uidEntryValueAt.uid));
                map2.put(Integer.valueOf(uidEntryValueAt.uid), Long.valueOf(l2 == null ? uidEntryValueAt.callCount : l2.longValue() + uidEntryValueAt.callCount));
                j3 = j5 + uidEntryValueAt.callCount;
                i++;
                j = j4;
            }
            j2 = j3;
        }
        double d = 100.0d;
        char c = 1;
        if (this.mDetailedTracking) {
            printWriter.println("Raw data (uid,call_desc,time):");
            arrayList.sort(new Comparator() { // from class: com.android.internal.os.-$$Lambda$BinderCallsStats$JdIS98lVGLAIfkEkC976rVyBc_U
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return BinderCallsStats.lambda$dump$0((BinderCallsStats.UidEntry) obj, (BinderCallsStats.UidEntry) obj2);
                }
            });
            StringBuilder sb = new StringBuilder();
            for (UidEntry uidEntry : arrayList) {
                ArrayList<CallStat> arrayList2 = new ArrayList(uidEntry.mCallStats.keySet());
                arrayList2.sort(new Comparator() { // from class: com.android.internal.os.-$$Lambda$BinderCallsStats$8JB19VSNkNr7RqU7ZTJ6NGkFXVU
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return BinderCallsStats.lambda$dump$1((BinderCallsStats.CallStat) obj, (BinderCallsStats.CallStat) obj2);
                    }
                });
                for (CallStat callStat : arrayList2) {
                    sb.setLength(0);
                    sb.append("    ");
                    sb.append(uidEntry.uid);
                    sb.append(",");
                    sb.append(callStat);
                    sb.append(',');
                    sb.append(callStat.time);
                    printWriter.println(sb);
                    j = j;
                }
            }
            long j6 = j;
            printWriter.println();
            printWriter.println("Per UID Summary(UID: time, % of total_time, calls_count):");
            ArrayList<Map.Entry> arrayList3 = new ArrayList(map.entrySet());
            arrayList3.sort(new Comparator() { // from class: com.android.internal.os.-$$Lambda$BinderCallsStats$BeSOWJ8AoyB7S9CtX-6IPAXHyNQ
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ((Long) ((Map.Entry) obj2).getValue()).compareTo((Long) ((Map.Entry) obj).getValue());
                }
            });
            for (Map.Entry entry : arrayList3) {
                printWriter.println(String.format("  %7d: %11d %3.0f%% %8d", entry.getKey(), entry.getValue(), Double.valueOf((((Long) entry.getValue()).longValue() * d) / j6), (Long) map2.get(entry.getKey())));
                d = 100.0d;
            }
            printWriter.println();
            printWriter.println(String.format("  Summary: total_time=%d, calls_count=%d, avg_call_time=%.0f", Long.valueOf(j6), Long.valueOf(j2), Double.valueOf(j6 / j2)));
            return;
        }
        long j7 = j;
        printWriter.println("Per UID Summary(UID: calls_count, % of total calls_count):");
        ArrayList<Map.Entry> arrayList4 = new ArrayList(map.entrySet());
        arrayList4.sort(new Comparator() { // from class: com.android.internal.os.-$$Lambda$BinderCallsStats$jhdszMKzG9FSuIQ4Vz9B0exXKPk
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((Long) ((Map.Entry) obj2).getValue()).compareTo((Long) ((Map.Entry) obj).getValue());
            }
        });
        for (Map.Entry entry2 : arrayList4) {
            Long l3 = (Long) map2.get(entry2.getKey());
            Object[] objArr = new Object[3];
            objArr[0] = entry2.getKey();
            objArr[c] = l3;
            objArr[2] = Double.valueOf((((Long) entry2.getValue()).longValue() * 100.0d) / j7);
            printWriter.println(String.format("    %7d: %8d %3.0f%%", objArr));
            c = 1;
        }
    }

    static /* synthetic */ int lambda$dump$0(UidEntry uidEntry, UidEntry uidEntry2) {
        if (uidEntry.time < uidEntry2.time) {
            return 1;
        }
        return uidEntry.time > uidEntry2.time ? -1 : 0;
    }

    static /* synthetic */ int lambda$dump$1(CallStat callStat, CallStat callStat2) {
        if (callStat.time < callStat2.time) {
            return 1;
        }
        return callStat.time > callStat2.time ? -1 : 0;
    }

    private long getThreadTimeMicro() {
        if (this.mDetailedTracking) {
            return SystemClock.currentThreadTimeMicro();
        }
        return 0L;
    }

    public static BinderCallsStats getInstance() {
        return sInstance;
    }

    public void setDetailedTracking(boolean z) {
        if (z != this.mDetailedTracking) {
            reset();
            this.mDetailedTracking = z;
        }
    }

    public void reset() {
        synchronized (this.mLock) {
            this.mUidEntries.clear();
            this.mStartTime = System.currentTimeMillis();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class CallStat {
        long callCount;
        String className;
        int msg;
        long time;

        CallStat() {
        }

        CallStat(String str, int i) {
            this.className = str;
            this.msg = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            CallStat callStat = (CallStat) obj;
            return this.msg == callStat.msg && this.className.equals(callStat.className);
        }

        public int hashCode() {
            return (this.className.hashCode() * 31) + this.msg;
        }

        public String toString() {
            return this.className + "/" + this.msg;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class UidEntry {
        long callCount;
        Map<CallStat, CallStat> mCallStats = new ArrayMap();
        long time;
        int uid;

        UidEntry(int i) {
            this.uid = i;
        }

        public String toString() {
            return "UidEntry{time=" + this.time + ", callCount=" + this.callCount + ", mCallStats=" + this.mCallStats + '}';
        }

        public boolean equals(Object obj) {
            return this == obj || this.uid == ((UidEntry) obj).uid;
        }

        public int hashCode() {
            return this.uid;
        }
    }
}
