package android.net.wifi.aware;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import libcore.util.HexEncoding;

/* JADX INFO: loaded from: classes.dex */
public final class PublishConfig implements Parcelable {
    public static final Parcelable.Creator<PublishConfig> CREATOR = new Parcelable.Creator<PublishConfig>() { // from class: android.net.wifi.aware.PublishConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PublishConfig[] newArray(int i) {
            return new PublishConfig[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PublishConfig createFromParcel(Parcel parcel) {
            return new PublishConfig(parcel.createByteArray(), parcel.createByteArray(), parcel.createByteArray(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0);
        }
    };
    public static final int PUBLISH_TYPE_SOLICITED = 1;
    public static final int PUBLISH_TYPE_UNSOLICITED = 0;
    public final boolean mEnableRanging;
    public final boolean mEnableTerminateNotification;
    public final byte[] mMatchFilter;
    public final int mPublishType;
    public final byte[] mServiceName;
    public final byte[] mServiceSpecificInfo;
    public final int mTtlSec;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PublishTypes {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PublishConfig(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, boolean z, boolean z2) {
        this.mServiceName = bArr;
        this.mServiceSpecificInfo = bArr2;
        this.mMatchFilter = bArr3;
        this.mPublishType = i;
        this.mTtlSec = i2;
        this.mEnableTerminateNotification = z;
        this.mEnableRanging = z2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PublishConfig [mServiceName='");
        byte[] bArr = this.mServiceName;
        sb.append(bArr == null ? "<null>" : String.valueOf(HexEncoding.encode(bArr)));
        sb.append(", mServiceName.length=");
        byte[] bArr2 = this.mServiceName;
        sb.append(bArr2 == null ? 0 : bArr2.length);
        sb.append(", mServiceSpecificInfo='");
        byte[] bArr3 = this.mServiceSpecificInfo;
        sb.append(bArr3 != null ? String.valueOf(HexEncoding.encode(bArr3)) : "<null>");
        sb.append(", mServiceSpecificInfo.length=");
        byte[] bArr4 = this.mServiceSpecificInfo;
        sb.append(bArr4 == null ? 0 : bArr4.length);
        sb.append(", mMatchFilter=");
        sb.append(new TlvBufferUtils.TlvIterable(0, 1, this.mMatchFilter).toString());
        sb.append(", mMatchFilter.length=");
        byte[] bArr5 = this.mMatchFilter;
        sb.append(bArr5 != null ? bArr5.length : 0);
        sb.append(", mPublishType=");
        sb.append(this.mPublishType);
        sb.append(", mTtlSec=");
        sb.append(this.mTtlSec);
        sb.append(", mEnableTerminateNotification=");
        sb.append(this.mEnableTerminateNotification);
        sb.append(", mEnableRanging=");
        sb.append(this.mEnableRanging);
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mServiceName);
        parcel.writeByteArray(this.mServiceSpecificInfo);
        parcel.writeByteArray(this.mMatchFilter);
        parcel.writeInt(this.mPublishType);
        parcel.writeInt(this.mTtlSec);
        parcel.writeInt(this.mEnableTerminateNotification ? 1 : 0);
        parcel.writeInt(this.mEnableRanging ? 1 : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PublishConfig)) {
            return false;
        }
        PublishConfig publishConfig = (PublishConfig) obj;
        return Arrays.equals(this.mServiceName, publishConfig.mServiceName) && Arrays.equals(this.mServiceSpecificInfo, publishConfig.mServiceSpecificInfo) && Arrays.equals(this.mMatchFilter, publishConfig.mMatchFilter) && this.mPublishType == publishConfig.mPublishType && this.mTtlSec == publishConfig.mTtlSec && this.mEnableTerminateNotification == publishConfig.mEnableTerminateNotification && this.mEnableRanging == publishConfig.mEnableRanging;
    }

    public int hashCode() {
        return Objects.hash(this.mServiceName, this.mServiceSpecificInfo, this.mMatchFilter, Integer.valueOf(this.mPublishType), Integer.valueOf(this.mTtlSec), Boolean.valueOf(this.mEnableTerminateNotification), Boolean.valueOf(this.mEnableRanging));
    }

    public void assertValid(Characteristics characteristics, boolean z) throws IllegalArgumentException {
        byte[] bArr;
        byte[] bArr2;
        WifiAwareUtils.validateServiceName(this.mServiceName);
        if (!TlvBufferUtils.isValid(this.mMatchFilter, 0, 1)) {
            throw new IllegalArgumentException("Invalid txFilter configuration - LV fields do not match up to length");
        }
        int i = this.mPublishType;
        if (i < 0 || i > 1) {
            throw new IllegalArgumentException("Invalid publishType - " + this.mPublishType);
        }
        if (this.mTtlSec < 0) {
            throw new IllegalArgumentException("Invalid ttlSec - must be non-negative");
        }
        if (characteristics != null) {
            int maxServiceNameLength = characteristics.getMaxServiceNameLength();
            if (maxServiceNameLength != 0 && this.mServiceName.length > maxServiceNameLength) {
                throw new IllegalArgumentException("Service name longer than supported by device characteristics");
            }
            int maxServiceSpecificInfoLength = characteristics.getMaxServiceSpecificInfoLength();
            if (maxServiceSpecificInfoLength != 0 && (bArr2 = this.mServiceSpecificInfo) != null && bArr2.length > maxServiceSpecificInfoLength) {
                throw new IllegalArgumentException("Service specific info longer than supported by device characteristics");
            }
            int maxMatchFilterLength = characteristics.getMaxMatchFilterLength();
            if (maxMatchFilterLength != 0 && (bArr = this.mMatchFilter) != null && bArr.length > maxMatchFilterLength) {
                throw new IllegalArgumentException("Match filter longer than supported by device characteristics");
            }
        }
        if (!z && this.mEnableRanging) {
            throw new IllegalArgumentException("Ranging is not supported");
        }
    }

    public static final class Builder {
        private byte[] mMatchFilter;
        private byte[] mServiceName;
        private byte[] mServiceSpecificInfo;
        private int mPublishType = 0;
        private int mTtlSec = 0;
        private boolean mEnableTerminateNotification = true;
        private boolean mEnableRanging = false;

        public Builder setServiceName(String str) {
            if (str == null) {
                throw new IllegalArgumentException("Invalid service name - must be non-null");
            }
            this.mServiceName = str.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        public Builder setServiceSpecificInfo(byte[] bArr) {
            this.mServiceSpecificInfo = bArr;
            return this;
        }

        public Builder setMatchFilter(List<byte[]> list) {
            this.mMatchFilter = new TlvBufferUtils.TlvConstructor(0, 1).allocateAndPut(list).getArray();
            return this;
        }

        public Builder setPublishType(int i) {
            if (i < 0 || i > 1) {
                throw new IllegalArgumentException("Invalid publishType - " + i);
            }
            this.mPublishType = i;
            return this;
        }

        public Builder setTtlSec(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid ttlSec - must be non-negative");
            }
            this.mTtlSec = i;
            return this;
        }

        public Builder setTerminateNotificationEnabled(boolean z) {
            this.mEnableTerminateNotification = z;
            return this;
        }

        public Builder setRangingEnabled(boolean z) {
            this.mEnableRanging = z;
            return this;
        }

        public PublishConfig build() {
            return new PublishConfig(this.mServiceName, this.mServiceSpecificInfo, this.mMatchFilter, this.mPublishType, this.mTtlSec, this.mEnableTerminateNotification, this.mEnableRanging);
        }
    }
}
