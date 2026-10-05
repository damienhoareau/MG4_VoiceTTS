package android.media;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class AudioBehavior implements Parcelable {
    public static final Parcelable.Creator<AudioBehavior> CREATOR = new Parcelable.Creator<AudioBehavior>() { // from class: android.media.AudioBehavior.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioBehavior createFromParcel(Parcel parcel) {
            AudioBehavior audioBehavior = new AudioBehavior();
            audioBehavior.mUsage = parcel.readInt();
            audioBehavior.isMedia = parcel.readByte() == 1;
            audioBehavior.isMixer = parcel.readByte() == 1;
            audioBehavior.canBeDuck = parcel.readByte() == 1;
            audioBehavior.isVR = parcel.readByte() == 1;
            audioBehavior.isPower = parcel.readByte() == 1;
            audioBehavior.isBTPhone = parcel.readByte() == 1;
            audioBehavior.isECallPhone = parcel.readByte() == 1;
            audioBehavior.isReverse = parcel.readByte() == 1;
            audioBehavior.isTA = parcel.readByte() == 1;
            return audioBehavior;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioBehavior[] newArray(int i) {
            return new AudioBehavior[i];
        }
    };
    public int mUsage = 0;
    public boolean isMedia = false;
    public boolean isMixer = false;
    public boolean isVR = false;
    public boolean isPower = false;
    public boolean isBTPhone = false;
    public boolean isECallPhone = false;
    public boolean canBeDuck = false;
    public boolean isReverse = false;
    public boolean isTA = false;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mUsage);
        parcel.writeByte(this.isMedia ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isMixer ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.canBeDuck ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isVR ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isPower ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isBTPhone ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isECallPhone ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isReverse ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isTA ? (byte) 1 : (byte) 0);
    }

    public boolean isMixerSource() {
        return this.isMixer;
    }

    public boolean isMediaSource() {
        return this.isMedia;
    }

    public boolean isVRSource() {
        return this.isVR;
    }

    public boolean isPowerSource() {
        return this.isPower;
    }

    public boolean isBTPhoneSource() {
        return this.isBTPhone;
    }

    public boolean isECallPhoneSource() {
        return this.isECallPhone;
    }

    public boolean isReverseSource() {
        return this.isReverse;
    }

    public boolean isTASource() {
        return this.isTA;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[usage:");
        sb.append(this.mUsage);
        sb.append(" isMedia:");
        sb.append(this.isMedia ? "true" : "false");
        sb.append(" isMixer:");
        sb.append(this.isMixer ? "true" : "false");
        sb.append(" isVR:");
        sb.append(this.isVR ? "true" : "false");
        sb.append(" isPower:");
        sb.append(this.isPower ? "true" : "false");
        sb.append(" isBTPhone:");
        sb.append(this.isBTPhone ? "true" : "false");
        sb.append(" isECallPhone:");
        sb.append(this.isECallPhone ? "true" : "false");
        sb.append(" canBeDuck:");
        sb.append(this.canBeDuck ? "true" : "false");
        sb.append(" isReverse:");
        sb.append(this.isReverse ? "true" : "false");
        sb.append(" isTA:");
        sb.append(this.isTA ? "true" : "false]");
        return sb.toString();
    }
}
