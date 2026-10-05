package android.hardware.location;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class NanoAppState implements Parcelable {
    public static final Parcelable.Creator<NanoAppState> CREATOR = new Parcelable.Creator<NanoAppState>() { // from class: android.hardware.location.NanoAppState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NanoAppState createFromParcel(Parcel parcel) {
            return new NanoAppState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NanoAppState[] newArray(int i) {
            return new NanoAppState[i];
        }
    };
    private boolean mIsEnabled;
    private long mNanoAppId;
    private int mNanoAppVersion;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NanoAppState(long j, int i, boolean z) {
        this.mNanoAppId = j;
        this.mNanoAppVersion = i;
        this.mIsEnabled = z;
    }

    public long getNanoAppId() {
        return this.mNanoAppId;
    }

    public long getNanoAppVersion() {
        return this.mNanoAppVersion;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    private NanoAppState(Parcel parcel) {
        this.mNanoAppId = parcel.readLong();
        this.mNanoAppVersion = parcel.readInt();
        this.mIsEnabled = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mNanoAppId);
        parcel.writeInt(this.mNanoAppVersion);
        parcel.writeInt(this.mIsEnabled ? 1 : 0);
    }
}
