package android.app;

import android.content.ComponentName;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public abstract class TaskStackListener extends ITaskStackListener.Stub {
    @Override // android.app.ITaskStackListener
    public void onActivityDismissingDockedStack() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityForcedResizable(String str, int i, int i2) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityLaunchOnSecondaryDisplayFailed() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityPinned(String str, int i, int i2, int i3) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityRequestedOrientationChanged(int i, int i2) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onActivityUnpinned() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onPinnedActivityRestartAttempt(boolean z) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onPinnedStackAnimationEnded() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onPinnedStackAnimationStarted() throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskCreated(int i, ComponentName componentName) throws RemoteException {
    }

    public void onTaskDescriptionChanged(int i, ActivityManager.TaskDescription taskDescription) throws RemoteException {
    }

    public void onTaskMovedToFront(int i) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskProfileLocked(int i, int i2) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskRemovalStarted(int i) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskRemoved(int i) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskSnapshotChanged(int i, ActivityManager.TaskSnapshot taskSnapshot) throws RemoteException {
    }

    @Override // android.app.ITaskStackListener
    public void onTaskStackChanged() throws RemoteException {
    }
}
