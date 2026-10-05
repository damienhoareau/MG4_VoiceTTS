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
public class ResumeActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<ResumeActivityItem> CREATOR = new Parcelable.Creator<ResumeActivityItem>() { // from class: android.app.servertransaction.ResumeActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResumeActivityItem createFromParcel(Parcel parcel) {
            return new ResumeActivityItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResumeActivityItem[] newArray(int i) {
            return new ResumeActivityItem[i];
        }
    };
    private static final String TAG = "ResumeActivityItem";
    private boolean mIsForward;
    private int mProcState;
    private boolean mUpdateProcState;

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 3;
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void preExecute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder) {
        if (this.mUpdateProcState) {
            clientTransactionHandler.updateProcessState(this.mProcState, false);
        }
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityResume");
        clientTransactionHandler.handleResumeActivity(iBinder, true, this.mIsForward, "RESUME_ACTIVITY");
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        try {
            ActivityManager.getService().activityResumed(iBinder);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private ResumeActivityItem() {
    }

    public static ResumeActivityItem obtain(int i, boolean z) {
        ResumeActivityItem resumeActivityItem = (ResumeActivityItem) ObjectPool.obtain(ResumeActivityItem.class);
        if (resumeActivityItem == null) {
            resumeActivityItem = new ResumeActivityItem();
        }
        resumeActivityItem.mProcState = i;
        resumeActivityItem.mUpdateProcState = true;
        resumeActivityItem.mIsForward = z;
        return resumeActivityItem;
    }

    public static ResumeActivityItem obtain(boolean z) {
        ResumeActivityItem resumeActivityItem = (ResumeActivityItem) ObjectPool.obtain(ResumeActivityItem.class);
        if (resumeActivityItem == null) {
            resumeActivityItem = new ResumeActivityItem();
        }
        resumeActivityItem.mProcState = -1;
        resumeActivityItem.mUpdateProcState = false;
        resumeActivityItem.mIsForward = z;
        return resumeActivityItem;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mProcState = -1;
        this.mUpdateProcState = false;
        this.mIsForward = false;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mProcState);
        parcel.writeBoolean(this.mUpdateProcState);
        parcel.writeBoolean(this.mIsForward);
    }

    private ResumeActivityItem(Parcel parcel) {
        this.mProcState = parcel.readInt();
        this.mUpdateProcState = parcel.readBoolean();
        this.mIsForward = parcel.readBoolean();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ResumeActivityItem resumeActivityItem = (ResumeActivityItem) obj;
        return this.mProcState == resumeActivityItem.mProcState && this.mUpdateProcState == resumeActivityItem.mUpdateProcState && this.mIsForward == resumeActivityItem.mIsForward;
    }

    public int hashCode() {
        return ((((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + this.mProcState) * 31) + (this.mUpdateProcState ? 1 : 0)) * 31) + (this.mIsForward ? 1 : 0);
    }

    public String toString() {
        return "ResumeActivityItem{procState=" + this.mProcState + ",updateProcState=" + this.mUpdateProcState + ",isForward=" + this.mIsForward + "}";
    }
}
