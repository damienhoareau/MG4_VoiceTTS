package android.companion;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OneTimeUseBuilder;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class AssociationRequest implements Parcelable {
    public static final Parcelable.Creator<AssociationRequest> CREATOR = new Parcelable.Creator<AssociationRequest>() { // from class: android.companion.AssociationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssociationRequest createFromParcel(Parcel parcel) {
            return new AssociationRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssociationRequest[] newArray(int i) {
            return new AssociationRequest[i];
        }
    };
    private final List<DeviceFilter<?>> mDeviceFilters;
    private final boolean mSingleDevice;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private AssociationRequest(boolean z, List<DeviceFilter<?>> list) {
        this.mSingleDevice = z;
        this.mDeviceFilters = CollectionUtils.emptyIfNull(list);
    }

    private AssociationRequest(Parcel parcel) {
        this(parcel.readByte() != 0, (List<DeviceFilter<?>>) parcel.readParcelableList(new ArrayList(), AssociationRequest.class.getClassLoader()));
    }

    public boolean isSingleDevice() {
        return this.mSingleDevice;
    }

    public List<DeviceFilter<?>> getDeviceFilters() {
        return this.mDeviceFilters;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AssociationRequest associationRequest = (AssociationRequest) obj;
        return this.mSingleDevice == associationRequest.mSingleDevice && Objects.equals(this.mDeviceFilters, associationRequest.mDeviceFilters);
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mSingleDevice), this.mDeviceFilters);
    }

    public String toString() {
        return "AssociationRequest{mSingleDevice=" + this.mSingleDevice + ", mDeviceFilters=" + this.mDeviceFilters + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mSingleDevice ? (byte) 1 : (byte) 0);
        parcel.writeParcelableList(this.mDeviceFilters, i);
    }

    public static final class Builder extends OneTimeUseBuilder<AssociationRequest> {
        private boolean mSingleDevice = false;
        private ArrayList<DeviceFilter<?>> mDeviceFilters = null;

        public Builder setSingleDevice(boolean z) {
            checkNotUsed();
            this.mSingleDevice = z;
            return this;
        }

        public Builder addDeviceFilter(DeviceFilter<?> deviceFilter) {
            checkNotUsed();
            if (deviceFilter != null) {
                this.mDeviceFilters = ArrayUtils.add(this.mDeviceFilters, deviceFilter);
            }
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.OneTimeUseBuilder
        public AssociationRequest build() {
            markUsed();
            return new AssociationRequest(this.mSingleDevice, this.mDeviceFilters);
        }
    }
}
