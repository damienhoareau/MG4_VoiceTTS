package android.hardware.radio;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class RadioUpgradeInfo {

    public static final class UpgradeInfo implements Parcelable {
        public static final Parcelable.Creator<UpgradeInfo> CREATOR = new Parcelable.Creator<UpgradeInfo>() { // from class: android.hardware.radio.RadioUpgradeInfo.UpgradeInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public UpgradeInfo createFromParcel(Parcel parcel) {
                return new UpgradeInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public UpgradeInfo[] newArray(int i) {
                return new UpgradeInfo[i];
            }
        };
        private final int mPrecent;
        private final int mStatus;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public UpgradeInfo(int i, int i2) {
            this.mStatus = i;
            this.mPrecent = i2;
        }

        public int getStatus() {
            return this.mStatus;
        }

        public int getPrecent() {
            return this.mPrecent;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mStatus), Integer.valueOf(this.mPrecent));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mStatus);
            parcel.writeInt(this.mPrecent);
        }

        public String toString() {
            return "RadioUpgradeInfo.UpgradeInfo [Status=" + this.mStatus + ", Precent=" + this.mPrecent + "]";
        }

        protected UpgradeInfo(Parcel parcel) {
            this.mStatus = parcel.readInt();
            this.mPrecent = parcel.readInt();
        }
    }
}
