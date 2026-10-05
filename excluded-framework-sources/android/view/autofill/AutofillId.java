package android.view.autofill;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public final class AutofillId implements Parcelable {
    public static final Parcelable.Creator<AutofillId> CREATOR = new Parcelable.Creator<AutofillId>() { // from class: android.view.autofill.AutofillId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AutofillId createFromParcel(Parcel parcel) {
            return new AutofillId(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AutofillId[] newArray(int i) {
            return new AutofillId[i];
        }
    };
    private final int mViewId;
    private final boolean mVirtual;
    private final int mVirtualId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AutofillId(int i) {
        this.mVirtual = false;
        this.mViewId = i;
        this.mVirtualId = -1;
    }

    public AutofillId(AutofillId autofillId, int i) {
        this.mVirtual = true;
        this.mViewId = autofillId.mViewId;
        this.mVirtualId = i;
    }

    public AutofillId(int i, int i2) {
        this.mVirtual = true;
        this.mViewId = i;
        this.mVirtualId = i2;
    }

    public int getViewId() {
        return this.mViewId;
    }

    public int getVirtualChildId() {
        return this.mVirtualId;
    }

    public boolean isVirtual() {
        return this.mVirtual;
    }

    public int hashCode() {
        return ((this.mViewId + 31) * 31) + this.mVirtualId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AutofillId autofillId = (AutofillId) obj;
        return this.mViewId == autofillId.mViewId && this.mVirtualId == autofillId.mVirtualId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mViewId);
        if (this.mVirtual) {
            sb.append(':');
            sb.append(this.mVirtualId);
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mViewId);
        parcel.writeInt(this.mVirtual ? 1 : 0);
        parcel.writeInt(this.mVirtualId);
    }

    private AutofillId(Parcel parcel) {
        this.mViewId = parcel.readInt();
        this.mVirtual = parcel.readInt() == 1;
        this.mVirtualId = parcel.readInt();
    }
}
