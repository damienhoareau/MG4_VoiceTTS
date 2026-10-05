package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.logging.nano.MetricsProto;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class PipModeChangeItem extends ClientTransactionItem {
    public static final Parcelable.Creator<PipModeChangeItem> CREATOR = new Parcelable.Creator<PipModeChangeItem>() { // from class: android.app.servertransaction.PipModeChangeItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PipModeChangeItem createFromParcel(Parcel parcel) {
            return new PipModeChangeItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PipModeChangeItem[] newArray(int i) {
            return new PipModeChangeItem[i];
        }
    };
    private boolean mIsInPipMode;
    private Configuration mOverrideConfig;

    @Override // android.app.servertransaction.BaseClientRequest
    public void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        clientTransactionHandler.handlePictureInPictureModeChanged(iBinder, this.mIsInPipMode, this.mOverrideConfig);
    }

    private PipModeChangeItem() {
    }

    public static PipModeChangeItem obtain(boolean z, Configuration configuration) {
        PipModeChangeItem pipModeChangeItem = (PipModeChangeItem) ObjectPool.obtain(PipModeChangeItem.class);
        if (pipModeChangeItem == null) {
            pipModeChangeItem = new PipModeChangeItem();
        }
        pipModeChangeItem.mIsInPipMode = z;
        pipModeChangeItem.mOverrideConfig = configuration;
        return pipModeChangeItem;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mIsInPipMode = false;
        this.mOverrideConfig = null;
        ObjectPool.recycle(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.mIsInPipMode);
        parcel.writeTypedObject(this.mOverrideConfig, i);
    }

    private PipModeChangeItem(Parcel parcel) {
        this.mIsInPipMode = parcel.readBoolean();
        this.mOverrideConfig = (Configuration) parcel.readTypedObject(Configuration.CREATOR);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PipModeChangeItem pipModeChangeItem = (PipModeChangeItem) obj;
        return this.mIsInPipMode == pipModeChangeItem.mIsInPipMode && Objects.equals(this.mOverrideConfig, pipModeChangeItem.mOverrideConfig);
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mIsInPipMode ? 1 : 0)) * 31) + this.mOverrideConfig.hashCode();
    }

    public String toString() {
        return "PipModeChangeItem{isInPipMode=" + this.mIsInPipMode + ",overrideConfig=" + this.mOverrideConfig + "}";
    }
}
