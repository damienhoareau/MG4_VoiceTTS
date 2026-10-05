package android.app.timezone;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class DistroFormatVersion implements Parcelable {
    public static final Parcelable.Creator<DistroFormatVersion> CREATOR = new Parcelable.Creator<DistroFormatVersion>() { // from class: android.app.timezone.DistroFormatVersion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DistroFormatVersion createFromParcel(Parcel parcel) {
            return new DistroFormatVersion(parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DistroFormatVersion[] newArray(int i) {
            return new DistroFormatVersion[i];
        }
    };
    private final int mMajorVersion;
    private final int mMinorVersion;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DistroFormatVersion(int i, int i2) {
        this.mMajorVersion = Utils.validateVersion("major", i);
        this.mMinorVersion = Utils.validateVersion("minor", i2);
    }

    public int getMajorVersion() {
        return this.mMajorVersion;
    }

    public int getMinorVersion() {
        return this.mMinorVersion;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mMajorVersion);
        parcel.writeInt(this.mMinorVersion);
    }

    public boolean supports(DistroFormatVersion distroFormatVersion) {
        return this.mMajorVersion == distroFormatVersion.mMajorVersion && this.mMinorVersion <= distroFormatVersion.mMinorVersion;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DistroFormatVersion distroFormatVersion = (DistroFormatVersion) obj;
        return this.mMajorVersion == distroFormatVersion.mMajorVersion && this.mMinorVersion == distroFormatVersion.mMinorVersion;
    }

    public int hashCode() {
        return (this.mMajorVersion * 31) + this.mMinorVersion;
    }

    public String toString() {
        return "DistroFormatVersion{mMajorVersion=" + this.mMajorVersion + ", mMinorVersion=" + this.mMinorVersion + '}';
    }
}
