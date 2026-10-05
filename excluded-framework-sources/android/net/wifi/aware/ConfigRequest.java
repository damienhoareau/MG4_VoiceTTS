package android.net.wifi.aware;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.logging.nano.MetricsProto;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class ConfigRequest implements Parcelable {
    public static final int CLUSTER_ID_MAX = 65535;
    public static final int CLUSTER_ID_MIN = 0;
    public static final Parcelable.Creator<ConfigRequest> CREATOR = new Parcelable.Creator<ConfigRequest>() { // from class: android.net.wifi.aware.ConfigRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConfigRequest[] newArray(int i) {
            return new ConfigRequest[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConfigRequest createFromParcel(Parcel parcel) {
            return new ConfigRequest(parcel.readInt() != 0, parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.createIntArray());
        }
    };
    public static final int DW_DISABLE = 0;
    public static final int DW_INTERVAL_NOT_INIT = -1;
    public static final int NAN_BAND_24GHZ = 0;
    public static final int NAN_BAND_5GHZ = 1;
    public final int mClusterHigh;
    public final int mClusterLow;
    public final int[] mDiscoveryWindowInterval;
    public final int mMasterPreference;
    public final boolean mSupport5gBand;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private ConfigRequest(boolean z, int i, int i2, int i3, int[] iArr) {
        this.mSupport5gBand = z;
        this.mMasterPreference = i;
        this.mClusterLow = i2;
        this.mClusterHigh = i3;
        this.mDiscoveryWindowInterval = iArr;
    }

    public String toString() {
        return "ConfigRequest [mSupport5gBand=" + this.mSupport5gBand + ", mMasterPreference=" + this.mMasterPreference + ", mClusterLow=" + this.mClusterLow + ", mClusterHigh=" + this.mClusterHigh + ", mDiscoveryWindowInterval=" + Arrays.toString(this.mDiscoveryWindowInterval) + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSupport5gBand ? 1 : 0);
        parcel.writeInt(this.mMasterPreference);
        parcel.writeInt(this.mClusterLow);
        parcel.writeInt(this.mClusterHigh);
        parcel.writeIntArray(this.mDiscoveryWindowInterval);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConfigRequest)) {
            return false;
        }
        ConfigRequest configRequest = (ConfigRequest) obj;
        return this.mSupport5gBand == configRequest.mSupport5gBand && this.mMasterPreference == configRequest.mMasterPreference && this.mClusterLow == configRequest.mClusterLow && this.mClusterHigh == configRequest.mClusterHigh && Arrays.equals(this.mDiscoveryWindowInterval, configRequest.mDiscoveryWindowInterval);
    }

    public int hashCode() {
        return ((((((((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + (this.mSupport5gBand ? 1 : 0)) * 31) + this.mMasterPreference) * 31) + this.mClusterLow) * 31) + this.mClusterHigh) * 31) + Arrays.hashCode(this.mDiscoveryWindowInterval);
    }

    public void validate() throws IllegalArgumentException {
        int i = this.mMasterPreference;
        if (i < 0) {
            throw new IllegalArgumentException("Master Preference specification must be non-negative");
        }
        if (i == 1 || i == 255 || i > 255) {
            throw new IllegalArgumentException("Master Preference specification must not exceed 255 or use 1 or 255 (reserved values)");
        }
        int i2 = this.mClusterLow;
        if (i2 < 0) {
            throw new IllegalArgumentException("Cluster specification must be non-negative");
        }
        if (i2 > 65535) {
            throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
        }
        int i3 = this.mClusterHigh;
        if (i3 < 0) {
            throw new IllegalArgumentException("Cluster specification must be non-negative");
        }
        if (i3 > 65535) {
            throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
        }
        if (i2 > i3) {
            throw new IllegalArgumentException("Invalid argument combination - must have Cluster Low <= Cluster High");
        }
        int[] iArr = this.mDiscoveryWindowInterval;
        if (iArr.length != 2) {
            throw new IllegalArgumentException("Invalid discovery window interval: must have 2 elements (2.4 & 5");
        }
        if (iArr[0] != -1 && (iArr[0] < 1 || iArr[0] > 5)) {
            throw new IllegalArgumentException("Invalid discovery window interval for 2.4GHz: valid is UNSET or [1,5]");
        }
        int[] iArr2 = this.mDiscoveryWindowInterval;
        if (iArr2[1] != -1) {
            if (iArr2[1] < 0 || iArr2[1] > 5) {
                throw new IllegalArgumentException("Invalid discovery window interval for 5GHz: valid is UNSET or [0,5]");
            }
        }
    }

    public static final class Builder {
        private boolean mSupport5gBand = false;
        private int mMasterPreference = 0;
        private int mClusterLow = 0;
        private int mClusterHigh = 65535;
        private int[] mDiscoveryWindowInterval = {-1, -1};

        public Builder setSupport5gBand(boolean z) {
            this.mSupport5gBand = z;
            return this;
        }

        public Builder setMasterPreference(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Master Preference specification must be non-negative");
            }
            if (i == 1 || i == 255 || i > 255) {
                throw new IllegalArgumentException("Master Preference specification must not exceed 255 or use 1 or 255 (reserved values)");
            }
            this.mMasterPreference = i;
            return this;
        }

        public Builder setClusterLow(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Cluster specification must be non-negative");
            }
            if (i > 65535) {
                throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
            }
            this.mClusterLow = i;
            return this;
        }

        public Builder setClusterHigh(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Cluster specification must be non-negative");
            }
            if (i > 65535) {
                throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
            }
            this.mClusterHigh = i;
            return this;
        }

        public Builder setDiscoveryWindowInterval(int i, int i2) {
            if (i != 0 && i != 1) {
                throw new IllegalArgumentException("Invalid band value");
            }
            if ((i == 0 && (i2 < 1 || i2 > 5)) || (i == 1 && (i2 < 0 || i2 > 5))) {
                throw new IllegalArgumentException("Invalid interval value: 2.4 GHz [1,5] or 5GHz [0,5]");
            }
            this.mDiscoveryWindowInterval[i] = i2;
            return this;
        }

        public ConfigRequest build() {
            if (this.mClusterLow > this.mClusterHigh) {
                throw new IllegalArgumentException("Invalid argument combination - must have Cluster Low <= Cluster High");
            }
            return new ConfigRequest(this.mSupport5gBand, this.mMasterPreference, this.mClusterLow, this.mClusterHigh, this.mDiscoveryWindowInterval);
        }
    }
}
