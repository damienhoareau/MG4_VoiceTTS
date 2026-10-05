package android.companion;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OneTimeUseBuilder;
import java.util.Objects;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class WifiDeviceFilter implements DeviceFilter<ScanResult> {
    public static final Parcelable.Creator<WifiDeviceFilter> CREATOR = new Parcelable.Creator<WifiDeviceFilter>() { // from class: android.companion.WifiDeviceFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDeviceFilter createFromParcel(Parcel parcel) {
            return new WifiDeviceFilter(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDeviceFilter[] newArray(int i) {
            return new WifiDeviceFilter[i];
        }
    };
    private final Pattern mNamePattern;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.companion.DeviceFilter
    public int getMediumType() {
        return 2;
    }

    private WifiDeviceFilter(Pattern pattern) {
        this.mNamePattern = pattern;
    }

    private WifiDeviceFilter(Parcel parcel) {
        this(BluetoothDeviceFilterUtils.patternFromString(parcel.readString()));
    }

    public Pattern getNamePattern() {
        return this.mNamePattern;
    }

    @Override // android.companion.DeviceFilter
    public boolean matches(ScanResult scanResult) {
        return BluetoothDeviceFilterUtils.matchesName(getNamePattern(), scanResult);
    }

    @Override // android.companion.DeviceFilter
    public String getDeviceDisplayName(ScanResult scanResult) {
        return BluetoothDeviceFilterUtils.getDeviceDisplayNameInternal(scanResult);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.mNamePattern, ((WifiDeviceFilter) obj).mNamePattern);
    }

    public int hashCode() {
        return Objects.hash(this.mNamePattern);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BluetoothDeviceFilterUtils.patternToString(getNamePattern()));
    }

    public static final class Builder extends OneTimeUseBuilder<WifiDeviceFilter> {
        private Pattern mNamePattern;

        public Builder setNamePattern(Pattern pattern) {
            checkNotUsed();
            this.mNamePattern = pattern;
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.OneTimeUseBuilder
        public WifiDeviceFilter build() {
            markUsed();
            return new WifiDeviceFilter(this.mNamePattern);
        }
    }
}
