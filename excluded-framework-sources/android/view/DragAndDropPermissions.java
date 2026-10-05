package android.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.android.internal.view.IDragAndDropPermissions;

/* JADX INFO: loaded from: classes2.dex */
public final class DragAndDropPermissions implements Parcelable {
    public static final Parcelable.Creator<DragAndDropPermissions> CREATOR = new Parcelable.Creator<DragAndDropPermissions>() { // from class: android.view.DragAndDropPermissions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DragAndDropPermissions createFromParcel(Parcel parcel) {
            return new DragAndDropPermissions(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DragAndDropPermissions[] newArray(int i) {
            return new DragAndDropPermissions[i];
        }
    };
    private final IDragAndDropPermissions mDragAndDropPermissions;
    private IBinder mTransientToken;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static DragAndDropPermissions obtain(DragEvent dragEvent) {
        if (dragEvent.getDragAndDropPermissions() == null) {
            return null;
        }
        return new DragAndDropPermissions(dragEvent.getDragAndDropPermissions());
    }

    private DragAndDropPermissions(IDragAndDropPermissions iDragAndDropPermissions) {
        this.mDragAndDropPermissions = iDragAndDropPermissions;
    }

    public boolean take(IBinder iBinder) {
        try {
            this.mDragAndDropPermissions.take(iBinder);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean takeTransient() {
        try {
            Binder binder = new Binder();
            this.mTransientToken = binder;
            this.mDragAndDropPermissions.takeTransient(binder);
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void release() {
        try {
            this.mDragAndDropPermissions.release();
            this.mTransientToken = null;
        } catch (RemoteException unused) {
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongInterface(this.mDragAndDropPermissions);
        parcel.writeStrongBinder(this.mTransientToken);
    }

    private DragAndDropPermissions(Parcel parcel) {
        this.mDragAndDropPermissions = IDragAndDropPermissions.Stub.asInterface(parcel.readStrongBinder());
        this.mTransientToken = parcel.readStrongBinder();
    }
}
