package android.media;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class UsbScannerResult implements Parcelable {
    public static final Parcelable.Creator<UsbScannerResult> CREATOR = new Parcelable.Creator<UsbScannerResult>() { // from class: android.media.UsbScannerResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbScannerResult createFromParcel(Parcel parcel) {
            return new UsbScannerResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbScannerResult[] newArray(int i) {
            return new UsbScannerResult[i];
        }
    };
    private static final String TAG = "UsbScannerResult";
    private int mAudioCount;
    private int mImageCount;
    private int mVideoCount;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UsbScannerResult() {
        this.mAudioCount = 0;
        this.mVideoCount = 0;
        this.mImageCount = 0;
    }

    public UsbScannerResult(int i, int i2, int i3) {
        this.mAudioCount = i;
        this.mVideoCount = i2;
        this.mImageCount = i3;
    }

    protected UsbScannerResult(Parcel parcel) {
        this.mAudioCount = parcel.readInt();
        this.mVideoCount = parcel.readInt();
        this.mImageCount = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mAudioCount);
        parcel.writeInt(this.mVideoCount);
        parcel.writeInt(this.mImageCount);
    }

    public int getAudioCount() {
        return this.mAudioCount;
    }

    public void setAudioCount(int i) {
        this.mAudioCount = i;
    }

    public int getVideoCount() {
        return this.mVideoCount;
    }

    public void setVideoCount(int i) {
        this.mVideoCount = i;
    }

    public int getImageCount() {
        return this.mImageCount;
    }

    public void setImageCount(int i) {
        this.mImageCount = i;
    }

    public String toString() {
        return "UsbScannerResult :  audioCount = " + getAudioCount() + " videoCount = " + getVideoCount() + " imageCount = " + getImageCount();
    }
}
