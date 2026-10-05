package android.hardware.camera2.utils;

import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class TaskSingleDrainer {
    private final Object mSingleTask = new Object();
    private final TaskDrainer<Object> mTaskDrainer;

    public TaskSingleDrainer(Executor executor, TaskDrainer.DrainListener drainListener) {
        this.mTaskDrainer = new TaskDrainer<>(executor, drainListener);
    }

    public TaskSingleDrainer(Executor executor, TaskDrainer.DrainListener drainListener, String str) {
        this.mTaskDrainer = new TaskDrainer<>(executor, drainListener, str);
    }

    public void taskStarted() {
        this.mTaskDrainer.taskStarted(this.mSingleTask);
    }

    public void beginDrain() {
        this.mTaskDrainer.beginDrain();
    }

    public void taskFinished() {
        this.mTaskDrainer.taskFinished(this.mSingleTask);
    }
}
