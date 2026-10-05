package android.content.pm;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class VerificationParams implements Parcelable {
    public static final Parcelable.Creator<VerificationParams> CREATOR = new Parcelable.Creator<VerificationParams>() { // from class: android.content.pm.VerificationParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams createFromParcel(Parcel parcel) {
            return new VerificationParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams[] newArray(int i) {
            return new VerificationParams[i];
        }
    };
    public static final int NO_UID = -1;
    private static final String TO_STRING_PREFIX = "VerificationParams{";
    private int mInstallerUid;
    private final Uri mOriginatingURI;
    private final int mOriginatingUid;
    private final Uri mReferrer;
    private final Uri mVerificationURI;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public VerificationParams(Uri uri, Uri uri2, Uri uri3, int i) {
        this.mVerificationURI = uri;
        this.mOriginatingURI = uri2;
        this.mReferrer = uri3;
        this.mOriginatingUid = i;
        this.mInstallerUid = -1;
    }

    public Uri getVerificationURI() {
        return this.mVerificationURI;
    }

    public Uri getOriginatingURI() {
        return this.mOriginatingURI;
    }

    public Uri getReferrer() {
        return this.mReferrer;
    }

    public int getOriginatingUid() {
        return this.mOriginatingUid;
    }

    public int getInstallerUid() {
        return this.mInstallerUid;
    }

    public void setInstallerUid(int i) {
        this.mInstallerUid = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VerificationParams)) {
            return false;
        }
        VerificationParams verificationParams = (VerificationParams) obj;
        Uri uri = this.mVerificationURI;
        if (uri == null) {
            if (verificationParams.mVerificationURI != null) {
                return false;
            }
        } else if (!uri.equals(verificationParams.mVerificationURI)) {
            return false;
        }
        Uri uri2 = this.mOriginatingURI;
        if (uri2 == null) {
            if (verificationParams.mOriginatingURI != null) {
                return false;
            }
        } else if (!uri2.equals(verificationParams.mOriginatingURI)) {
            return false;
        }
        Uri uri3 = this.mReferrer;
        if (uri3 == null) {
            if (verificationParams.mReferrer != null) {
                return false;
            }
        } else if (!uri3.equals(verificationParams.mReferrer)) {
            return false;
        }
        return this.mOriginatingUid == verificationParams.mOriginatingUid && this.mInstallerUid == verificationParams.mInstallerUid;
    }

    public int hashCode() {
        Uri uri = this.mVerificationURI;
        int iHashCode = ((uri == null ? 1 : uri.hashCode()) * 5) + 3;
        Uri uri2 = this.mOriginatingURI;
        int iHashCode2 = iHashCode + ((uri2 == null ? 1 : uri2.hashCode()) * 7);
        Uri uri3 = this.mReferrer;
        return iHashCode2 + ((uri3 != null ? uri3.hashCode() : 1) * 11) + (this.mOriginatingUid * 13) + (this.mInstallerUid * 17);
    }

    public String toString() {
        return TO_STRING_PREFIX + "mVerificationURI=" + this.mVerificationURI.toString() + ",mOriginatingURI=" + this.mOriginatingURI.toString() + ",mReferrer=" + this.mReferrer.toString() + ",mOriginatingUid=" + this.mOriginatingUid + ",mInstallerUid=" + this.mInstallerUid + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mVerificationURI, 0);
        parcel.writeParcelable(this.mOriginatingURI, 0);
        parcel.writeParcelable(this.mReferrer, 0);
        parcel.writeInt(this.mOriginatingUid);
        parcel.writeInt(this.mInstallerUid);
    }

    private VerificationParams(Parcel parcel) {
        this.mVerificationURI = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingURI = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mReferrer = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingUid = parcel.readInt();
        this.mInstallerUid = parcel.readInt();
    }
}
