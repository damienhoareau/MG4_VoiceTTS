package android.view;

import android.os.Looper;

/* JADX INFO: loaded from: classes2.dex */
public class BatchedInputEventReceiver extends InputEventReceiver {
    private final BatchedInputRunnable mBatchedInputRunnable;
    private boolean mBatchedInputScheduled;
    Choreographer mChoreographer;

    public BatchedInputEventReceiver(InputChannel inputChannel, Looper looper, Choreographer choreographer) {
        super(inputChannel, looper);
        this.mBatchedInputRunnable = new BatchedInputRunnable();
        this.mChoreographer = choreographer;
    }

    @Override // android.view.InputEventReceiver
    public void onBatchedInputEventPending() {
        scheduleBatchedInput();
    }

    @Override // android.view.InputEventReceiver
    public void dispose() {
        unscheduleBatchedInput();
        super.dispose();
    }

    void doConsumeBatchedInput(long j) {
        if (this.mBatchedInputScheduled) {
            this.mBatchedInputScheduled = false;
            if (!consumeBatchedInputEvents(j) || j == -1) {
                return;
            }
            scheduleBatchedInput();
        }
    }

    private void scheduleBatchedInput() {
        if (this.mBatchedInputScheduled) {
            return;
        }
        this.mBatchedInputScheduled = true;
        this.mChoreographer.postCallback(0, this.mBatchedInputRunnable, null);
    }

    private void unscheduleBatchedInput() {
        if (this.mBatchedInputScheduled) {
            this.mBatchedInputScheduled = false;
            this.mChoreographer.removeCallbacks(0, this.mBatchedInputRunnable, null);
        }
    }

    private final class BatchedInputRunnable implements Runnable {
        private BatchedInputRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            BatchedInputEventReceiver batchedInputEventReceiver = BatchedInputEventReceiver.this;
            batchedInputEventReceiver.doConsumeBatchedInput(batchedInputEventReceiver.mChoreographer.getFrameTimeNanos());
        }
    }
}
