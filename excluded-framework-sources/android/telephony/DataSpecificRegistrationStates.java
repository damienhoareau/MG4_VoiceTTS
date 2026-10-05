package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class DataSpecificRegistrationStates implements Parcelable {
    public static final Parcelable.Creator<DataSpecificRegistrationStates> CREATOR = new Parcelable.Creator<DataSpecificRegistrationStates>() { // from class: android.telephony.DataSpecificRegistrationStates.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataSpecificRegistrationStates createFromParcel(Parcel parcel) {
            return new DataSpecificRegistrationStates(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataSpecificRegistrationStates[] newArray(int i) {
            return new DataSpecificRegistrationStates[i];
        }
    };
    public final int maxDataCalls;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    DataSpecificRegistrationStates(int i) {
        this.maxDataCalls = i;
    }

    private DataSpecificRegistrationStates(Parcel parcel) {
        this.maxDataCalls = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.maxDataCalls);
    }

    public String toString() {
        return "DataSpecificRegistrationStates { mMaxDataCalls=" + this.maxDataCalls + "}";
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.maxDataCalls));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && (obj instanceof DataSpecificRegistrationStates) && this.maxDataCalls == ((DataSpecificRegistrationStates) obj).maxDataCalls;
    }
}
