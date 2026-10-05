package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Trace;
import com.android.internal.logging.nano.MetricsProto;

/* JADX INFO: loaded from: classes.dex */
public class StopActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<StopActivityItem> CREATOR = new Parcelable.Creator<StopActivityItem>() { // from class: android.app.servertransaction.StopActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StopActivityItem createFromParcel(Parcel parcel) {
            return new StopActivityItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StopActivityItem[] newArray(int i) {
            return new StopActivityItem[i];
        }
    };
    private static final String TAG = "StopActivityItem";
    private int mConfigChanges;
    private boolean mShowWindow;

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 5;
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityStop");
        clientTransactionHandler.handleStopActivity(iBinder, this.mShowWindow, this.mConfigChanges, pendingTransactionActions, true, "STOP_ACTIVITY_ITEM");
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        clientTransactionHandler.reportStop(pendingTransactionActions);
    }

    private StopActivityItem() {
    }

    public static StopActivityItem obtain(boolean z, int i) {
        StopActivityItem stopActivityItem = (StopActivityItem) ObjectPool.obtain(StopActivityItem.class);
        if (stopActivityItem == null) {
            stopActivityItem = new StopActivityItem();
        }
        stopActivityItem.mShowWindow = z;
        stopActivityItem.mConfigChanges = i;
        return stopActivityItem;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mShowWindow = false;
        this.mConfigChanges = 0;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mShowWindow);
        parcel.writeInt(this.mConfigChanges);
    }

    private StopActivityItem(Parcel parcel) {
        this.mShowWindow = parcel.readBoolean();
        this.mConfigChanges = parcel.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StopActivityItem stopActivityItem = (StopActivityItem) obj;
        return this.mShowWindow == stopActivityItem.mShowWindow && this.mConfigChanges == stopActivityItem.mConfigChanges;
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mShowWindow ? 1 : 0)) * 31) + this.mConfigChanges;
    }

    public String toString() {
        return "StopActivityItem{showWindow=" + this.mShowWindow + ",configChanges=" + this.mConfigChanges + "}";
    }
}
