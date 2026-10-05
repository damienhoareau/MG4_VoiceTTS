package android.app.timezone;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.TimeZoneRulesDataContract;
import android.text.format.DateFormat;

/* JADX INFO: loaded from: classes.dex */
public final class DistroRulesVersion implements Parcelable {
    public static final Parcelable.Creator<DistroRulesVersion> CREATOR = new Parcelable.Creator<DistroRulesVersion>() { // from class: android.app.timezone.DistroRulesVersion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DistroRulesVersion createFromParcel(Parcel parcel) {
            return new DistroRulesVersion(parcel.readString(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DistroRulesVersion[] newArray(int i) {
            return new DistroRulesVersion[i];
        }
    };
    private final int mRevision;
    private final String mRulesVersion;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DistroRulesVersion(String str, int i) {
        this.mRulesVersion = Utils.validateRulesVersion("rulesVersion", str);
        this.mRevision = Utils.validateVersion(TimeZoneRulesDataContract.Operation.COLUMN_REVISION, i);
    }

    public String getRulesVersion() {
        return this.mRulesVersion;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public boolean isOlderThan(DistroRulesVersion distroRulesVersion) {
        int iCompareTo = this.mRulesVersion.compareTo(distroRulesVersion.mRulesVersion);
        if (iCompareTo < 0) {
            return true;
        }
        return iCompareTo <= 0 && this.mRevision < distroRulesVersion.mRevision;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mRulesVersion);
        parcel.writeInt(this.mRevision);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DistroRulesVersion distroRulesVersion = (DistroRulesVersion) obj;
        if (this.mRevision != distroRulesVersion.mRevision) {
            return false;
        }
        return this.mRulesVersion.equals(distroRulesVersion.mRulesVersion);
    }

    public int hashCode() {
        return (this.mRulesVersion.hashCode() * 31) + this.mRevision;
    }

    public String toString() {
        return "DistroRulesVersion{mRulesVersion='" + this.mRulesVersion + DateFormat.QUOTE + ", mRevision='" + this.mRevision + DateFormat.QUOTE + '}';
    }

    public String toDumpString() {
        return this.mRulesVersion + "," + this.mRevision;
    }
}
