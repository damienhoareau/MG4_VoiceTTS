package android.net.lowpan;

import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class LowpanChannelInfo implements Parcelable {
    public static final Parcelable.Creator<LowpanChannelInfo> CREATOR = new Parcelable.Creator<LowpanChannelInfo>() { // from class: android.net.lowpan.LowpanChannelInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LowpanChannelInfo createFromParcel(Parcel parcel) {
            LowpanChannelInfo lowpanChannelInfo = new LowpanChannelInfo();
            lowpanChannelInfo.mIndex = parcel.readInt();
            lowpanChannelInfo.mName = parcel.readString();
            lowpanChannelInfo.mSpectrumCenterFrequency = parcel.readFloat();
            lowpanChannelInfo.mSpectrumBandwidth = parcel.readFloat();
            lowpanChannelInfo.mMaxTransmitPower = parcel.readInt();
            lowpanChannelInfo.mIsMaskedByRegulatoryDomain = parcel.readBoolean();
            return lowpanChannelInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LowpanChannelInfo[] newArray(int i) {
            return new LowpanChannelInfo[i];
        }
    };
    public static final float UNKNOWN_BANDWIDTH = 0.0f;
    public static final float UNKNOWN_FREQUENCY = 0.0f;
    public static final int UNKNOWN_POWER = Integer.MAX_VALUE;
    private int mIndex;
    private boolean mIsMaskedByRegulatoryDomain;
    private int mMaxTransmitPower;
    private String mName;
    private float mSpectrumBandwidth;
    private float mSpectrumCenterFrequency;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX WARN: Code duplicated, block: B:4:0x0008  */
    public static LowpanChannelInfo getChannelInfoForIeee802154Page0(int i) {
        LowpanChannelInfo lowpanChannelInfo = new LowpanChannelInfo();
        if (i < 0) {
            lowpanChannelInfo = null;
        } else if (i == 0) {
            lowpanChannelInfo.mSpectrumCenterFrequency = 8.683E8f;
            lowpanChannelInfo.mSpectrumBandwidth = 600000.0f;
        } else if (i < 11) {
            lowpanChannelInfo.mSpectrumCenterFrequency = (i * 2000000.0f) + 9.04E8f;
            lowpanChannelInfo.mSpectrumBandwidth = 0.0f;
        } else if (i < 26) {
            lowpanChannelInfo.mSpectrumCenterFrequency = (i * 5000000.0f) + 2.3499999E9f;
            lowpanChannelInfo.mSpectrumBandwidth = 2000000.0f;
        } else {
            lowpanChannelInfo = null;
        }
        lowpanChannelInfo.mName = Integer.toString(i);
        return lowpanChannelInfo;
    }

    private LowpanChannelInfo() {
        this.mIndex = 0;
        this.mName = null;
        this.mSpectrumCenterFrequency = 0.0f;
        this.mSpectrumBandwidth = 0.0f;
        this.mMaxTransmitPower = Integer.MAX_VALUE;
        this.mIsMaskedByRegulatoryDomain = false;
    }

    private LowpanChannelInfo(int i, String str, float f, float f2) {
        this.mIndex = 0;
        this.mName = null;
        this.mSpectrumCenterFrequency = 0.0f;
        this.mSpectrumBandwidth = 0.0f;
        this.mMaxTransmitPower = Integer.MAX_VALUE;
        this.mIsMaskedByRegulatoryDomain = false;
        this.mIndex = i;
        this.mName = str;
        this.mSpectrumCenterFrequency = f;
        this.mSpectrumBandwidth = f2;
    }

    public String getName() {
        return this.mName;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public int getMaxTransmitPower() {
        return this.mMaxTransmitPower;
    }

    public boolean isMaskedByRegulatoryDomain() {
        return this.mIsMaskedByRegulatoryDomain;
    }

    public float getSpectrumCenterFrequency() {
        return this.mSpectrumCenterFrequency;
    }

    public float getSpectrumBandwidth() {
        return this.mSpectrumBandwidth;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Channel ");
        stringBuffer.append(this.mIndex);
        String str = this.mName;
        if (str != null && !str.equals(Integer.toString(this.mIndex))) {
            stringBuffer.append(" (");
            stringBuffer.append(this.mName);
            stringBuffer.append(")");
        }
        float f = this.mSpectrumCenterFrequency;
        if (f > 0.0f) {
            if (f > 1.0E9f) {
                stringBuffer.append(", SpectrumCenterFrequency: ");
                stringBuffer.append(this.mSpectrumCenterFrequency / 1.0E9f);
                stringBuffer.append("GHz");
            } else if (f > 1000000.0f) {
                stringBuffer.append(", SpectrumCenterFrequency: ");
                stringBuffer.append(this.mSpectrumCenterFrequency / 1000000.0f);
                stringBuffer.append(WifiInfo.FREQUENCY_UNITS);
            } else {
                stringBuffer.append(", SpectrumCenterFrequency: ");
                stringBuffer.append(this.mSpectrumCenterFrequency / 1000.0f);
                stringBuffer.append("kHz");
            }
        }
        float f2 = this.mSpectrumBandwidth;
        if (f2 > 0.0f) {
            if (f2 > 1.0E9f) {
                stringBuffer.append(", SpectrumBandwidth: ");
                stringBuffer.append(this.mSpectrumBandwidth / 1.0E9f);
                stringBuffer.append("GHz");
            } else if (f2 > 1000000.0f) {
                stringBuffer.append(", SpectrumBandwidth: ");
                stringBuffer.append(this.mSpectrumBandwidth / 1000000.0f);
                stringBuffer.append(WifiInfo.FREQUENCY_UNITS);
            } else {
                stringBuffer.append(", SpectrumBandwidth: ");
                stringBuffer.append(this.mSpectrumBandwidth / 1000.0f);
                stringBuffer.append("kHz");
            }
        }
        if (this.mMaxTransmitPower != Integer.MAX_VALUE) {
            stringBuffer.append(", MaxTransmitPower: ");
            stringBuffer.append(this.mMaxTransmitPower);
            stringBuffer.append("dBm");
        }
        return stringBuffer.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LowpanChannelInfo)) {
            return false;
        }
        LowpanChannelInfo lowpanChannelInfo = (LowpanChannelInfo) obj;
        return Objects.equals(this.mName, lowpanChannelInfo.mName) && this.mIndex == lowpanChannelInfo.mIndex && this.mIsMaskedByRegulatoryDomain == lowpanChannelInfo.mIsMaskedByRegulatoryDomain && this.mSpectrumCenterFrequency == lowpanChannelInfo.mSpectrumCenterFrequency && this.mSpectrumBandwidth == lowpanChannelInfo.mSpectrumBandwidth && this.mMaxTransmitPower == lowpanChannelInfo.mMaxTransmitPower;
    }

    public int hashCode() {
        return Objects.hash(this.mName, Integer.valueOf(this.mIndex), Boolean.valueOf(this.mIsMaskedByRegulatoryDomain), Float.valueOf(this.mSpectrumCenterFrequency), Float.valueOf(this.mSpectrumBandwidth), Integer.valueOf(this.mMaxTransmitPower));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mIndex);
        parcel.writeString(this.mName);
        parcel.writeFloat(this.mSpectrumCenterFrequency);
        parcel.writeFloat(this.mSpectrumBandwidth);
        parcel.writeInt(this.mMaxTransmitPower);
        parcel.writeBoolean(this.mIsMaskedByRegulatoryDomain);
    }
}
