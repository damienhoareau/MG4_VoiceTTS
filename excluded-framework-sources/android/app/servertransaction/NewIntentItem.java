package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Trace;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.logging.nano.MetricsProto;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class NewIntentItem extends ClientTransactionItem {
    public static final Parcelable.Creator<NewIntentItem> CREATOR = new Parcelable.Creator<NewIntentItem>() { // from class: android.app.servertransaction.NewIntentItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NewIntentItem createFromParcel(Parcel parcel) {
            return new NewIntentItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NewIntentItem[] newArray(int i) {
            return new NewIntentItem[i];
        }
    };
    private List<ReferrerIntent> mIntents;
    private boolean mPause;

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        Trace.traceBegin(64L, "activityNewIntent");
        clientTransactionHandler.handleNewIntent(iBinder, this.mIntents, this.mPause);
        Trace.traceEnd(64L);
    }

    private NewIntentItem() {
    }

    public static NewIntentItem obtain(List<ReferrerIntent> list, boolean z) {
        NewIntentItem newIntentItem = (NewIntentItem) ObjectPool.obtain(NewIntentItem.class);
        if (newIntentItem == null) {
            newIntentItem = new NewIntentItem();
        }
        newIntentItem.mIntents = list;
        newIntentItem.mPause = z;
        return newIntentItem;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mIntents = null;
        this.mPause = false;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mPause);
        parcel.writeTypedList(this.mIntents, i);
    }

    private NewIntentItem(Parcel parcel) {
        this.mPause = parcel.readBoolean();
        this.mIntents = parcel.createTypedArrayList(ReferrerIntent.CREATOR);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NewIntentItem newIntentItem = (NewIntentItem) obj;
        return this.mPause == newIntentItem.mPause && Objects.equals(this.mIntents, newIntentItem.mIntents);
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mPause ? 1 : 0)) * 31) + this.mIntents.hashCode();
    }

    public String toString() {
        return "NewIntentItem{pause=" + this.mPause + ",intents=" + this.mIntents + "}";
    }
}
