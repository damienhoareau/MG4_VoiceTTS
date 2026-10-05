package android.app.servertransaction;

import android.app.ActivityThread;
import android.util.IntArray;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TransactionExecutorHelper {
    private static final int DESTRUCTION_PENALTY = 10;
    private static final int[] ON_RESUME_PRE_EXCUTION_STATES = {2, 4};
    private IntArray mLifecycleSequence = new IntArray(6);

    /* JADX WARN: Code duplicated, block: B:33:0x0056  */
    /* JADX WARN: Code duplicated, block: B:36:0x005a A[LOOP:3: B:34:0x0057->B:36:0x005a, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:39:0x0065 A[LOOP:4: B:38:0x0063->B:39:0x0065, LOOP_END] */
    public IntArray getLifecyclePath(int i, int i2, boolean z) {
        int i3;
        int i4;
        if (i == -1 || i2 == -1) {
            throw new IllegalArgumentException("Can't resolve lifecycle path for undefined state");
        }
        if (i == 7 || i2 == 7) {
            throw new IllegalArgumentException("Can't start or finish in intermittent RESTART state");
        }
        if (i2 == 0 && i != i2) {
            throw new IllegalArgumentException("Can only start in pre-onCreate state");
        }
        this.mLifecycleSequence.clear();
        if (i2 >= i) {
            for (int i5 = i + 1; i5 <= i2; i5++) {
                this.mLifecycleSequence.add(i5);
            }
        } else if (i == 4 && i2 == 3) {
            this.mLifecycleSequence.add(3);
        } else if (i <= 5) {
            if (i2 >= 2) {
                for (int i6 = i + 1; i6 <= 5; i6++) {
                    this.mLifecycleSequence.add(i6);
                }
                this.mLifecycleSequence.add(7);
                for (int i7 = 2; i7 <= i2; i7++) {
                    this.mLifecycleSequence.add(i7);
                }
            } else {
                for (i3 = i + 1; i3 <= 6; i3++) {
                    this.mLifecycleSequence.add(i3);
                }
                for (i4 = 1; i4 <= i2; i4++) {
                    this.mLifecycleSequence.add(i4);
                }
            }
        } else {
            while (i3 <= 6) {
                this.mLifecycleSequence.add(i3);
            }
            while (i4 <= i2) {
                this.mLifecycleSequence.add(i4);
            }
        }
        if (z && this.mLifecycleSequence.size() != 0) {
            IntArray intArray = this.mLifecycleSequence;
            intArray.remove(intArray.size() - 1);
        }
        return this.mLifecycleSequence;
    }

    public int getClosestPreExecutionState(ActivityThread.ActivityClientRecord activityClientRecord, int i) {
        if (i == -1) {
            return -1;
        }
        if (i == 3) {
            return getClosestOfStates(activityClientRecord, ON_RESUME_PRE_EXCUTION_STATES);
        }
        throw new UnsupportedOperationException("Pre-execution states for state: " + i + " is not supported.");
    }

    public int getClosestOfStates(ActivityThread.ActivityClientRecord activityClientRecord, int[] iArr) {
        int i = -1;
        if (iArr != null && iArr.length != 0) {
            int lifecycleState = activityClientRecord.getLifecycleState();
            int i2 = Integer.MAX_VALUE;
            for (int i3 = 0; i3 < iArr.length; i3++) {
                getLifecyclePath(lifecycleState, iArr[i3], false);
                int size = this.mLifecycleSequence.size();
                if (pathInvolvesDestruction(this.mLifecycleSequence)) {
                    size += 10;
                }
                if (i2 > size) {
                    i = iArr[i3];
                    i2 = size;
                }
            }
        }
        return i;
    }

    public static ActivityLifecycleItem getLifecycleRequestForCurrentState(ActivityThread.ActivityClientRecord activityClientRecord) {
        int lifecycleState = activityClientRecord.getLifecycleState();
        if (lifecycleState == 4) {
            return PauseActivityItem.obtain();
        }
        if (lifecycleState == 5) {
            return StopActivityItem.obtain(activityClientRecord.isVisibleFromServer(), 0);
        }
        return ResumeActivityItem.obtain(false);
    }

    private static boolean pathInvolvesDestruction(IntArray intArray) {
        int size = intArray.size();
        for (int i = 0; i < size; i++) {
            if (intArray.get(i) == 6) {
                return true;
            }
        }
        return false;
    }

    static int lastCallbackRequestingState(ClientTransaction clientTransaction) {
        List<ClientTransactionItem> callbacks = clientTransaction.getCallbacks();
        if (callbacks == null || callbacks.size() == 0) {
            return -1;
        }
        int i = -1;
        int i2 = -1;
        for (int size = callbacks.size() - 1; size >= 0; size--) {
            int postExecutionState = callbacks.get(size).getPostExecutionState();
            if (postExecutionState != -1) {
                if (i != -1 && i != postExecutionState) {
                    break;
                }
                i2 = size;
                i = postExecutionState;
            }
        }
        return i2;
    }
}
