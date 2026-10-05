package android.media;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class BufferingParams implements Parcelable {
    private static final int BUFFERING_NO_MARK = -1;
    public static final Parcelable.Creator<BufferingParams> CREATOR = new Parcelable.Creator<BufferingParams>() { // from class: android.media.BufferingParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BufferingParams createFromParcel(Parcel parcel) {
            return new BufferingParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BufferingParams[] newArray(int i) {
            return new BufferingParams[i];
        }
    };
    private int mInitialMarkMs;
    private int mResumePlaybackMarkMs;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private BufferingParams() {
        this.mInitialMarkMs = -1;
        this.mResumePlaybackMarkMs = -1;
    }

    public int getInitialMarkMs() {
        return this.mInitialMarkMs;
    }

    public int getResumePlaybackMarkMs() {
        return this.mResumePlaybackMarkMs;
    }

    public static class Builder {
        private int mInitialMarkMs;
        private int mResumePlaybackMarkMs;

        public Builder() {
            this.mInitialMarkMs = -1;
            this.mResumePlaybackMarkMs = -1;
        }

        public Builder(BufferingParams bufferingParams) {
            this.mInitialMarkMs = -1;
            this.mResumePlaybackMarkMs = -1;
            this.mInitialMarkMs = bufferingParams.mInitialMarkMs;
            this.mResumePlaybackMarkMs = bufferingParams.mResumePlaybackMarkMs;
        }

        public BufferingParams build() {
            BufferingParams bufferingParams = new BufferingParams();
            bufferingParams.mInitialMarkMs = this.mInitialMarkMs;
            bufferingParams.mResumePlaybackMarkMs = this.mResumePlaybackMarkMs;
            return bufferingParams;
        }

        public Builder setInitialMarkMs(int i) {
            this.mInitialMarkMs = i;
            return this;
        }

        public Builder setResumePlaybackMarkMs(int i) {
            this.mResumePlaybackMarkMs = i;
            return this;
        }
    }

    private BufferingParams(Parcel parcel) {
        this.mInitialMarkMs = -1;
        this.mResumePlaybackMarkMs = -1;
        this.mInitialMarkMs = parcel.readInt();
        this.mResumePlaybackMarkMs = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mInitialMarkMs);
        parcel.writeInt(this.mResumePlaybackMarkMs);
    }
}
