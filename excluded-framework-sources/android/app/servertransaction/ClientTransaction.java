package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.app.IApplicationThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.android.internal.logging.nano.MetricsProto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class ClientTransaction implements Parcelable, ObjectPoolItem {
    public static final Parcelable.Creator<ClientTransaction> CREATOR = new Parcelable.Creator<ClientTransaction>() { // from class: android.app.servertransaction.ClientTransaction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClientTransaction createFromParcel(Parcel parcel) {
            return new ClientTransaction(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClientTransaction[] newArray(int i) {
            return new ClientTransaction[i];
        }
    };
    private List<ClientTransactionItem> mActivityCallbacks;
    private IBinder mActivityToken;
    private IApplicationThread mClient;
    private ActivityLifecycleItem mLifecycleStateRequest;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public IApplicationThread getClient() {
        return this.mClient;
    }

    public void addCallback(ClientTransactionItem clientTransactionItem) {
        if (this.mActivityCallbacks == null) {
            this.mActivityCallbacks = new ArrayList();
        }
        this.mActivityCallbacks.add(clientTransactionItem);
    }

    List<ClientTransactionItem> getCallbacks() {
        return this.mActivityCallbacks;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public ActivityLifecycleItem getLifecycleStateRequest() {
        return this.mLifecycleStateRequest;
    }

    public void setLifecycleStateRequest(ActivityLifecycleItem activityLifecycleItem) {
        this.mLifecycleStateRequest = activityLifecycleItem;
    }

    public void preExecute(ClientTransactionHandler clientTransactionHandler) {
        List<ClientTransactionItem> list = this.mActivityCallbacks;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                this.mActivityCallbacks.get(i).preExecute(clientTransactionHandler, this.mActivityToken);
            }
        }
        ActivityLifecycleItem activityLifecycleItem = this.mLifecycleStateRequest;
        if (activityLifecycleItem != null) {
            activityLifecycleItem.preExecute(clientTransactionHandler, this.mActivityToken);
        }
    }

    public void schedule() throws RemoteException {
        this.mClient.scheduleTransaction(this);
    }

    private ClientTransaction() {
    }

    public static ClientTransaction obtain(IApplicationThread iApplicationThread, IBinder iBinder) {
        ClientTransaction clientTransaction = (ClientTransaction) ObjectPool.obtain(ClientTransaction.class);
        if (clientTransaction == null) {
            clientTransaction = new ClientTransaction();
        }
        clientTransaction.mClient = iApplicationThread;
        clientTransaction.mActivityToken = iBinder;
        return clientTransaction;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        List<ClientTransactionItem> list = this.mActivityCallbacks;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                this.mActivityCallbacks.get(i).recycle();
            }
            this.mActivityCallbacks.clear();
        }
        ActivityLifecycleItem activityLifecycleItem = this.mLifecycleStateRequest;
        if (activityLifecycleItem != null) {
            activityLifecycleItem.recycle();
            this.mLifecycleStateRequest = null;
        }
        this.mClient = null;
        this.mActivityToken = null;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.mClient.asBinder());
        boolean z = this.mActivityToken != null;
        parcel.writeBoolean(z);
        if (z) {
            parcel.writeStrongBinder(this.mActivityToken);
        }
        parcel.writeParcelable(this.mLifecycleStateRequest, i);
        boolean z2 = this.mActivityCallbacks != null;
        parcel.writeBoolean(z2);
        if (z2) {
            parcel.writeParcelableList(this.mActivityCallbacks, i);
        }
    }

    private ClientTransaction(Parcel parcel) {
        this.mClient = (IApplicationThread) parcel.readStrongBinder();
        if (parcel.readBoolean()) {
            this.mActivityToken = parcel.readStrongBinder();
        }
        this.mLifecycleStateRequest = (ActivityLifecycleItem) parcel.readParcelable(getClass().getClassLoader());
        if (parcel.readBoolean()) {
            ArrayList arrayList = new ArrayList();
            this.mActivityCallbacks = arrayList;
            parcel.readParcelableList(arrayList, getClass().getClassLoader());
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClientTransaction clientTransaction = (ClientTransaction) obj;
        return Objects.equals(this.mActivityCallbacks, clientTransaction.mActivityCallbacks) && Objects.equals(this.mLifecycleStateRequest, clientTransaction.mLifecycleStateRequest) && this.mClient == clientTransaction.mClient && this.mActivityToken == clientTransaction.mActivityToken;
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + Objects.hashCode(this.mActivityCallbacks)) * 31) + Objects.hashCode(this.mLifecycleStateRequest);
    }
}
