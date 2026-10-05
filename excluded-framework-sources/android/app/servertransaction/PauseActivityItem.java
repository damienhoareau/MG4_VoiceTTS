package android.app.servertransaction;

import android.app.ActivityManager;
import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.Trace;
import com.android.internal.logging.nano.MetricsProto;

/* JADX INFO: loaded from: classes.dex */
public class PauseActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<PauseActivityItem> CREATOR = new Parcelable.Creator<PauseActivityItem>() { // from class: android.app.servertransaction.PauseActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PauseActivityItem createFromParcel(Parcel parcel) {
            return new PauseActivityItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PauseActivityItem[] newArray(int i) {
            return new PauseActivityItem[i];
        }
    };
    private static final String TAG = "PauseActivityItem";
    private int mConfigChanges;
    private boolean mDontReport;
    private boolean mFinished;
    private boolean mUserLeaving;

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 4;
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityPause");
        clientTransactionHandler.handlePauseActivity(iBinder, this.mFinished, this.mUserLeaving, this.mConfigChanges, pendingTransactionActions, "PAUSE_ACTIVITY_ITEM");
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        if (this.mDontReport) {
            return;
        }
        try {
            ActivityManager.getService().activityPaused(iBinder);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private PauseActivityItem() {
    }

    public static PauseActivityItem obtain(boolean z, boolean z2, int i, boolean z3) {
        PauseActivityItem pauseActivityItem = (PauseActivityItem) ObjectPool.obtain(PauseActivityItem.class);
        if (pauseActivityItem == null) {
            pauseActivityItem = new PauseActivityItem();
        }
        pauseActivityItem.mFinished = z;
        pauseActivityItem.mUserLeaving = z2;
        pauseActivityItem.mConfigChanges = i;
        pauseActivityItem.mDontReport = z3;
        return pauseActivityItem;
    }

    public static PauseActivityItem obtain() {
        PauseActivityItem pauseActivityItem = (PauseActivityItem) ObjectPool.obtain(PauseActivityItem.class);
        if (pauseActivityItem == null) {
            pauseActivityItem = new PauseActivityItem();
        }
        pauseActivityItem.mFinished = false;
        pauseActivityItem.mUserLeaving = false;
        pauseActivityItem.mConfigChanges = 0;
        pauseActivityItem.mDontReport = true;
        return pauseActivityItem;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mFinished = false;
        this.mUserLeaving = false;
        this.mConfigChanges = 0;
        this.mDontReport = false;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mFinished);
        parcel.writeBoolean(this.mUserLeaving);
        parcel.writeInt(this.mConfigChanges);
        parcel.writeBoolean(this.mDontReport);
    }

    private PauseActivityItem(Parcel parcel) {
        this.mFinished = parcel.readBoolean();
        this.mUserLeaving = parcel.readBoolean();
        this.mConfigChanges = parcel.readInt();
        this.mDontReport = parcel.readBoolean();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PauseActivityItem pauseActivityItem = (PauseActivityItem) obj;
        return this.mFinished == pauseActivityItem.mFinished && this.mUserLeaving == pauseActivityItem.mUserLeaving && this.mConfigChanges == pauseActivityItem.mConfigChanges && this.mDontReport == pauseActivityItem.mDontReport;
    }

    public int hashCode() {
        return ((((((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mFinished ? 1 : 0)) * 31) + (this.mUserLeaving ? 1 : 0)) * 31) + this.mConfigChanges) * 31) + (this.mDontReport ? 1 : 0);
    }

    public String toString() {
        return "PauseActivityItem{finished=" + this.mFinished + ",userLeaving=" + this.mUserLeaving + ",configChanges=" + this.mConfigChanges + ",dontReport=" + this.mDontReport + "}";
    }
}
