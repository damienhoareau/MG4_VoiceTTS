package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Trace;
import com.android.internal.logging.nano.MetricsProto;

/* JADX INFO: loaded from: classes.dex */
public class DestroyActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<DestroyActivityItem> CREATOR = new Parcelable.Creator<DestroyActivityItem>() { // from class: android.app.servertransaction.DestroyActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DestroyActivityItem createFromParcel(Parcel parcel) {
            return new DestroyActivityItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DestroyActivityItem[] newArray(int i) {
            return new DestroyActivityItem[i];
        }
    };
    private int mConfigChanges;
    private boolean mFinished;

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 6;
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityDestroy");
        clientTransactionHandler.handleDestroyActivity(iBinder, this.mFinished, this.mConfigChanges, false, "DestroyActivityItem");
        Trace.traceEnd(64L);
    }

    private DestroyActivityItem() {
    }

    public static DestroyActivityItem obtain(boolean z, int i) {
        DestroyActivityItem destroyActivityItem = (DestroyActivityItem) ObjectPool.obtain(DestroyActivityItem.class);
        if (destroyActivityItem == null) {
            destroyActivityItem = new DestroyActivityItem();
        }
        destroyActivityItem.mFinished = z;
        destroyActivityItem.mConfigChanges = i;
        return destroyActivityItem;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mFinished = false;
        this.mConfigChanges = 0;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mFinished);
        parcel.writeInt(this.mConfigChanges);
    }

    private DestroyActivityItem(Parcel parcel) {
        this.mFinished = parcel.readBoolean();
        this.mConfigChanges = parcel.readInt();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DestroyActivityItem destroyActivityItem = (DestroyActivityItem) obj;
        return this.mFinished == destroyActivityItem.mFinished && this.mConfigChanges == destroyActivityItem.mConfigChanges;
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mFinished ? 1 : 0)) * 31) + this.mConfigChanges;
    }

    public String toString() {
        return "DestroyActivityItem{finished=" + this.mFinished + ",mConfigChanges=" + this.mConfigChanges + "}";
    }
}
