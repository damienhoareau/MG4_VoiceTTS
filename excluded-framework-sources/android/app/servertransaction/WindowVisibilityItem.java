package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Trace;

/* JADX INFO: loaded from: classes.dex */
public class WindowVisibilityItem extends ClientTransactionItem {
    public static final Parcelable.Creator<WindowVisibilityItem> CREATOR = new Parcelable.Creator<WindowVisibilityItem>() { // from class: android.app.servertransaction.WindowVisibilityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WindowVisibilityItem createFromParcel(Parcel parcel) {
            return new WindowVisibilityItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WindowVisibilityItem[] newArray(int i) {
            return new WindowVisibilityItem[i];
        }
    };
    private boolean mShowWindow;

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityShowWindow");
        clientTransactionHandler.handleWindowVisibility(iBinder, this.mShowWindow);
        Trace.traceEnd(64L);
    }

    private WindowVisibilityItem() {
    }

    public static WindowVisibilityItem obtain(boolean z) {
        WindowVisibilityItem windowVisibilityItem = (WindowVisibilityItem) ObjectPool.obtain(WindowVisibilityItem.class);
        if (windowVisibilityItem == null) {
            windowVisibilityItem = new WindowVisibilityItem();
        }
        windowVisibilityItem.mShowWindow = z;
        return windowVisibilityItem;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mShowWindow = false;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mShowWindow);
    }

    private WindowVisibilityItem(Parcel parcel) {
        this.mShowWindow = parcel.readBoolean();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mShowWindow == ((WindowVisibilityItem) obj).mShowWindow;
    }

    public int hashCode() {
        return ((this.mShowWindow ? 1 : 0) * 31) + 17;
    }

    public String toString() {
        return "WindowVisibilityItem{showWindow=" + this.mShowWindow + "}";
    }
}
