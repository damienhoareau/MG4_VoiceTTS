package android.hardware.radio;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class RadioSignal implements Parcelable {
    public static final Parcelable.Creator<RadioSignal> CREATOR = new Parcelable.Creator<RadioSignal>() { // from class: android.hardware.radio.RadioSignal.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RadioSignal createFromParcel(Parcel parcel) {
            return new RadioSignal(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RadioSignal[] newArray(int i) {
            return new RadioSignal[i];
        }
    };
    private final int agc;
    private final int bandwidth;
    private final int modulation;
    private final int offset;
    private final int quality;
    private final int signalStrength;
    private final boolean stereo;
    private final int usn;
    private final int wam;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public RadioSignal(int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z, int i8) {
        this.signalStrength = i;
        this.usn = i2;
        this.wam = i3;
        this.offset = i4;
        this.bandwidth = i5;
        this.modulation = i6;
        this.quality = i7;
        this.stereo = z;
        this.agc = i8;
    }

    public int getSignalStrength() {
        return this.signalStrength;
    }

    public int getUsn() {
        return this.usn;
    }

    public int getWam() {
        return this.wam;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getBandwidth() {
        return this.bandwidth;
    }

    public int getModulation() {
        return this.modulation;
    }

    public int getQuality() {
        return this.quality;
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public int getagc() {
        return this.agc;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.signalStrength);
        parcel.writeInt(this.usn);
        parcel.writeInt(this.wam);
        parcel.writeInt(this.offset);
        parcel.writeInt(this.bandwidth);
        parcel.writeInt(this.modulation);
        parcel.writeInt(this.quality);
        parcel.writeByte(this.stereo ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.agc);
    }

    public String toString() {
        return "RadioSignal [signalStrength=" + this.signalStrength + ", usn=" + this.usn + ", wam=" + this.wam + "]";
    }

    protected RadioSignal(Parcel parcel) {
        this.signalStrength = parcel.readInt();
        this.usn = parcel.readInt();
        this.wam = parcel.readInt();
        this.offset = parcel.readInt();
        this.bandwidth = parcel.readInt();
        this.modulation = parcel.readInt();
        this.quality = parcel.readInt();
        this.stereo = parcel.readByte() != 0;
        this.agc = parcel.readInt();
    }
}
