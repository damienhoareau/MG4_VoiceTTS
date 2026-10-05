package android.app.servertransaction;

import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public abstract class ClientTransactionItem implements BaseClientRequest, Parcelable {
    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getPostExecutionState() {
        return -1;
    }
}
