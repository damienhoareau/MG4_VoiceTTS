package com.saicmotor.voicetts;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public final class ActivityMonitor {
    private static final boolean DBG = false;
    private static final int NUM_MAX_TASK_TO_FETCH = 10;
    private static final String TAG = "ActivityMonitor";
    private ActivityLaunchListener mActivityLaunchListener;
    private final IActivityManager mAm;
    private final Context mContext;
    private final ActivityMonitorHandler mHandler;
    private final HandlerThread mMonitorHandlerThread;
    private final ProcessObserver mProcessObserver;
    private final TaskListener mTaskListener;
    private final SparseArray<TopTaskInfoContainer> mTopTasks = new SparseArray<>();
    private final Map<Integer, Set<Integer>> mForegroundUidPids = new ArrayMap();
    private final List<TopTaskInfoContainer> mTasksToDispatch = new LinkedList();
    String preTopAppPkgName = null;
    private int mFocusedStackId = -1;

    public interface ActivityLaunchListener {
        void onActivityLaunch(TopTaskInfoContainer topTaskInfoContainer, int i, int i2);

        void onGetTopAppPackageName(String str);
    }

    public ActivityManager.StackInfo getFocusedStackForTopActivity(ComponentName componentName) {
        return null;
    }

    public ActivityMonitor(Context context) {
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mMonitorHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new ActivityMonitorHandler(this.mMonitorHandlerThread.getLooper());
        this.mProcessObserver = new ProcessObserver();
        this.mTaskListener = new TaskListener();
        IActivityManager service = ActivityManager.getService();
        this.mAm = service;
        try {
            service.registerProcessObserver(this.mProcessObserver);
            this.mAm.registerTaskStackListener(this.mTaskListener);
        } catch (RemoteException e) {
            Log.e(TAG, "cannot register activity monitoring", e);
            throw new RuntimeException(e);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        updateTasks();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("*SystemActivityMonitoringService*");
        printWriter.println(" Top Tasks:");
        synchronized (this) {
            for (int i = 0; i < this.mTopTasks.size(); i++) {
                TopTaskInfoContainer topTaskInfoContainerValueAt = this.mTopTasks.valueAt(i);
                if (topTaskInfoContainerValueAt != null) {
                    printWriter.println(topTaskInfoContainerValueAt);
                }
            }
            printWriter.println(" Foregroud uid-pids:");
            for (Integer num : this.mForegroundUidPids.keySet()) {
                Set<Integer> set = this.mForegroundUidPids.get(num);
                if (set != null) {
                    printWriter.println("uid:" + num + ", pids:" + Arrays.toString(set.toArray()));
                }
            }
            printWriter.println(" focused stack:" + this.mFocusedStackId);
        }
    }

    public void blockActivity(TopTaskInfoContainer topTaskInfoContainer, Intent intent) {
        this.mHandler.requestBlockActivity(topTaskInfoContainer, intent);
    }

    public List<TopTaskInfoContainer> getTopTasks() {
        LinkedList linkedList = new LinkedList();
        synchronized (this) {
            for (int i = 0; i < this.mTopTasks.size(); i++) {
                linkedList.add(this.mTopTasks.valueAt(i));
            }
        }
        return linkedList;
    }

    public boolean isInForeground(int i, int i2) {
        synchronized (this) {
            Set<Integer> set = this.mForegroundUidPids.get(Integer.valueOf(i2));
            if (set == null) {
                return false;
            }
            return set.contains(Integer.valueOf(i));
        }
    }

    public void registerActivityLaunchListener(ActivityLaunchListener activityLaunchListener) {
        synchronized (this) {
            this.mActivityLaunchListener = activityLaunchListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTopAppPkgName() {
        ComponentName componentName;
        String packageName = null;
        try {
            ActivityManager.StackInfo focusedStackInfo = this.mAm.getFocusedStackInfo();
            if (focusedStackInfo != null && (componentName = focusedStackInfo.topActivity) != null) {
                packageName = componentName.getPackageName();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            ActivityLaunchListener activityLaunchListener = this.mActivityLaunchListener;
            if (activityLaunchListener != null) {
                if (!TextUtils.isEmpty(packageName) && !packageName.equals(this.preTopAppPkgName)) {
                    activityLaunchListener.onGetTopAppPackageName(packageName);
                }
                this.preTopAppPkgName = packageName;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTasks() {
        ActivityLaunchListener activityLaunchListener;
        try {
            List<ActivityManager.StackInfo> allStackInfos = this.mAm.getAllStackInfos();
            this.mTasksToDispatch.clear();
            synchronized (this) {
                activityLaunchListener = this.mActivityLaunchListener;
                for (ActivityManager.StackInfo stackInfo : allStackInfos) {
                    int i = stackInfo.stackId;
                    if (stackInfo.taskNames.length == 0 || !stackInfo.visible) {
                        this.mTopTasks.remove(i);
                    } else {
                        TopTaskInfoContainer topTaskInfoContainer = new TopTaskInfoContainer(stackInfo.topActivity, stackInfo.taskIds[stackInfo.taskIds.length - 1], stackInfo);
                        TopTaskInfoContainer topTaskInfoContainer2 = this.mTopTasks.get(i);
                        if ((-1 == i && topTaskInfoContainer2 == null) || ((-1 == i && !topTaskInfoContainer2.isMatching(topTaskInfoContainer)) || (-1 == i && -1 != this.mFocusedStackId))) {
                            this.mTopTasks.put(i, topTaskInfoContainer);
                            this.mTasksToDispatch.add(topTaskInfoContainer);
                        }
                    }
                }
                this.mFocusedStackId = -1;
            }
            if (activityLaunchListener != null) {
                Iterator<TopTaskInfoContainer> it = this.mTasksToDispatch.iterator();
                if (it.hasNext()) {
                    TopTaskInfoContainer next = it.next();
                    activityLaunchListener.onActivityLaunch(next, this.mTasksToDispatch.size(), this.mTasksToDispatch.indexOf(next));
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "cannot getTasks", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleForegroundActivitiesChanged(int i, int i2, boolean z) {
        synchronized (this) {
            try {
                if (z) {
                    Set<Integer> arraySet = this.mForegroundUidPids.get(Integer.valueOf(i2));
                    if (arraySet == null) {
                        arraySet = new ArraySet<>();
                        this.mForegroundUidPids.put(Integer.valueOf(i2), arraySet);
                    }
                    arraySet.add(Integer.valueOf(i));
                } else {
                    doHandlePidGoneLocked(i, i2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleProcessDied(int i, int i2) {
        synchronized (this) {
            doHandlePidGoneLocked(i, i2);
        }
    }

    private void doHandlePidGoneLocked(int i, int i2) {
        Set<Integer> set = this.mForegroundUidPids.get(Integer.valueOf(i2));
        if (set != null) {
            set.remove(Integer.valueOf(i));
            if (set.isEmpty()) {
                this.mForegroundUidPids.remove(Integer.valueOf(i2));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBlockActivity(TopTaskInfoContainer topTaskInfoContainer, Intent intent) {
        Log.i(TAG, String.format("stopping activity %s with taskid:%d", topTaskInfoContainer.topActivity, Integer.valueOf(topTaskInfoContainer.taskId)));
        Intent intent2 = new Intent();
        intent2.setComponent(new ComponentName("com.saicmotor.launcher", LaunchUtils.getAppLauncherActivity("com.saicmotor.launcher", this.mContext)));
        this.mContext.startActivity(intent2);
        intent.addFlags(268435456);
        this.mContext.startActivityAsUser(intent, new UserHandle(topTaskInfoContainer.stackInfo.userId));
        findTaskAndGrantFocus(intent.getComponent());
        try {
            this.mAm.removeTask(topTaskInfoContainer.taskId);
        } catch (RemoteException e) {
            Log.w(TAG, "cannot remove task:" + topTaskInfoContainer.taskId, e);
        }
    }

    private void findTaskAndGrantFocus(ComponentName componentName) {
        try {
            for (ActivityManager.StackInfo stackInfo : this.mAm.getAllStackInfos()) {
                if (stackInfo.taskNames.length != 0 && componentName.equals(ComponentName.unflattenFromString(stackInfo.taskNames[stackInfo.taskNames.length - 1]))) {
                    try {
                        this.mAm.setFocusedStack(stackInfo.stackId);
                        return;
                    } catch (RemoteException e) {
                        Log.e(TAG, "cannot setFocusedStack to stack:" + stackInfo.stackId, e);
                        return;
                    }
                }
            }
            Log.i(TAG, "cannot give focus, cannot find Activity:" + componentName);
        } catch (RemoteException e2) {
            Log.e(TAG, "cannot getTasks", e2);
        }
    }

    public static class TopTaskInfoContainer {
        public final ActivityManager.StackInfo stackInfo;
        public final int taskId;
        public final ComponentName topActivity;

        private TopTaskInfoContainer(ComponentName componentName, int i, ActivityManager.StackInfo stackInfo) {
            this.topActivity = componentName;
            this.taskId = i;
            this.stackInfo = stackInfo;
        }

        public boolean isMatching(TopTaskInfoContainer topTaskInfoContainer) {
            return topTaskInfoContainer != null && Objects.equals(this.topActivity, topTaskInfoContainer.topActivity) && this.taskId == topTaskInfoContainer.taskId && this.stackInfo.userId == topTaskInfoContainer.stackInfo.userId;
        }

        public String toString() {
            return String.format("TaskInfoContainer [topActivity=%s, taskId=%d, stackId=%d, userId=%d", this.topActivity, Integer.valueOf(this.taskId), Integer.valueOf(this.stackInfo.stackId), Integer.valueOf(this.stackInfo.userId));
        }
    }

    private class ProcessObserver extends IProcessObserver.Stub {
        private ProcessObserver() {
        }

        @Override // android.app.IProcessObserver
        public void onForegroundActivitiesChanged(int i, int i2, boolean z) {
            ActivityMonitor.this.mHandler.requestForegroundActivitiesChanged(i, i2, z);
        }

        @Override // android.app.IProcessObserver
        public void onProcessDied(int i, int i2) {
            ActivityMonitor.this.mHandler.requestProcessDied(i, i2);
        }
    }

    private class TaskListener extends TaskStackListener {
        private TaskListener() {
        }

        @Override // android.app.TaskStackListener, android.app.ITaskStackListener
        public void onTaskStackChanged() {
            ActivityMonitor.this.mHandler.requestUpdatingTask();
        }
    }

    private class ActivityMonitorHandler extends Handler {
        private static final int MSG_BLOCK_ACTIVITY = 3;
        private static final int MSG_FOREGROUND_ACTIVITIES_CHANGED = 1;
        private static final int MSG_PROCESS_DIED = 2;
        private static final int MSG_UPDATE_TASKS = 0;

        private ActivityMonitorHandler(Looper looper) {
            super(looper);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void requestUpdatingTask() {
            sendMessage(obtainMessage(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void requestForegroundActivitiesChanged(int i, int i2, boolean z) {
            sendMessage(obtainMessage(1, i, i2, Boolean.valueOf(z)));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void requestProcessDied(int i, int i2) {
            sendMessage(obtainMessage(2, i, i2));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void requestBlockActivity(TopTaskInfoContainer topTaskInfoContainer, Intent intent) {
            sendMessage(obtainMessage(3, new Pair(topTaskInfoContainer, intent)));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                ActivityMonitor.this.updateTasks();
                return;
            }
            if (i == 1) {
                ActivityMonitor.this.updateTopAppPkgName();
                ActivityMonitor.this.handleForegroundActivitiesChanged(message.arg1, message.arg2, ((Boolean) message.obj).booleanValue());
                ActivityMonitor.this.updateTasks();
            } else if (i == 2) {
                ActivityMonitor.this.handleProcessDied(message.arg1, message.arg2);
            } else {
                if (i != 3) {
                    return;
                }
                Pair pair = (Pair) message.obj;
                ActivityMonitor.this.handleBlockActivity((TopTaskInfoContainer) pair.first, (Intent) pair.second);
            }
        }
    }
}
