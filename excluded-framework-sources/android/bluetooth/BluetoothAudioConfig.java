package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class BluetoothAudioConfig implements Parcelable {
    public static final Parcelable.Creator<BluetoothAudioConfig> CREATOR = new Parcelable.Creator<BluetoothAudioConfig>() { // from class: android.bluetooth.BluetoothAudioConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothAudioConfig createFromParcel(Parcel parcel) {
            return new BluetoothAudioConfig(parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothAudioConfig[] newArray(int i) {
            return new BluetoothAudioConfig[i];
        }
    };
    private final int mAudioFormat;
    private final int mChannelConfig;
    private final int mSampleRate;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BluetoothAudioConfig(int i, int i2, int i3) {
        this.mSampleRate = i;
        this.mChannelConfig = i2;
        this.mAudioFormat = i3;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BluetoothAudioConfig)) {
            return false;
        }
        BluetoothAudioConfig bluetoothAudioConfig = (BluetoothAudioConfig) obj;
        return bluetoothAudioConfig.mSampleRate == this.mSampleRate && bluetoothAudioConfig.mChannelConfig == this.mChannelConfig && bluetoothAudioConfig.mAudioFormat == this.mAudioFormat;
    }

    public int hashCode() {
        return this.mSampleRate | (this.mChannelConfig << 24) | (this.mAudioFormat << 28);
    }

    public String toString() {
        return "{mSampleRate:" + this.mSampleRate + ",mChannelConfig:" + this.mChannelConfig + ",mAudioFormat:" + this.mAudioFormat + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSampleRate);
        parcel.writeInt(this.mChannelConfig);
        parcel.writeInt(this.mAudioFormat);
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getChannelConfig() {
        return this.mChannelConfig;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }
}
