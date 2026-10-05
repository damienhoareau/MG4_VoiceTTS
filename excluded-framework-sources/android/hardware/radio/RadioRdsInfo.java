package android.hardware.radio;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class RadioRdsInfo {

    public static final class MainInfo implements Parcelable {
        public static final Parcelable.Creator<MainInfo> CREATOR = new Parcelable.Creator<MainInfo>() { // from class: android.hardware.radio.RadioRdsInfo.MainInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MainInfo createFromParcel(Parcel parcel) {
                return new MainInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MainInfo[] newArray(int i) {
                return new MainInfo[i];
            }
        };
        private final int mFrequency;
        private final int mPiCode;
        private final char[] mPsName;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public MainInfo(int i, int i2, char[] cArr) {
            this.mFrequency = i;
            this.mPiCode = i2;
            this.mPsName = cArr;
        }

        public int getFrequency() {
            return this.mFrequency;
        }

        public int getPiCode() {
            return this.mPiCode;
        }

        public char[] getPsName() {
            return this.mPsName;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mFrequency), Integer.valueOf(this.mPiCode), this.mPsName);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFrequency);
            parcel.writeInt(this.mPiCode);
            parcel.writeInt(this.mPsName.length);
            parcel.writeCharArray(this.mPsName);
        }

        public String toString() {
            return "RadioRdsInfo.MainInfo [Frequency=" + this.mFrequency + ", PI Code=" + this.mPiCode + ", PsName=" + String.valueOf(this.mPsName) + "]";
        }

        protected MainInfo(Parcel parcel) {
            this.mFrequency = parcel.readInt();
            this.mPiCode = parcel.readInt();
            char[] cArr = new char[parcel.readInt()];
            this.mPsName = cArr;
            parcel.readCharArray(cArr);
        }
    }

    public static final class TAInfo implements Parcelable {
        public static final Parcelable.Creator<TAInfo> CREATOR = new Parcelable.Creator<TAInfo>() { // from class: android.hardware.radio.RadioRdsInfo.TAInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TAInfo createFromParcel(Parcel parcel) {
                return new TAInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TAInfo[] newArray(int i) {
                return new TAInfo[i];
            }
        };
        private final int mFrequency;
        private final char[] mPsName;
        private final boolean mPtyWarning;
        private final boolean mTaAssert;
        private final boolean mTaFlag;
        private final boolean mTpFlag;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public TAInfo(boolean z, boolean z2, boolean z3, boolean z4, int i, char[] cArr) {
            this.mPtyWarning = z;
            this.mTaAssert = z2;
            this.mTaFlag = z3;
            this.mTpFlag = z4;
            this.mFrequency = i;
            this.mPsName = cArr;
        }

        public boolean getPtyWarningStatus() {
            return this.mPtyWarning;
        }

        public boolean getTaAssertStatus() {
            return this.mTaAssert;
        }

        public boolean getTaFlagStatus() {
            return this.mTaFlag;
        }

        public boolean getTpFlagStatus() {
            return this.mTpFlag;
        }

        public int getFrequency() {
            return this.mFrequency;
        }

        public char[] getPsName() {
            return this.mPsName;
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(this.mPtyWarning), Boolean.valueOf(this.mTaAssert), Boolean.valueOf(this.mTaFlag), Boolean.valueOf(this.mTpFlag), Integer.valueOf(this.mFrequency), this.mPsName);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte(this.mPtyWarning ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mTaAssert ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mTaFlag ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mTpFlag ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.mFrequency);
            parcel.writeInt(this.mPsName.length);
            parcel.writeCharArray(this.mPsName);
        }

        public String toString() {
            return "RadioRdsInfo.TAInfo [PtyWarning=" + this.mPtyWarning + ", TaAssert=" + this.mTaAssert + ", TaFlag=" + this.mTaFlag + ", TpFlag=" + this.mTpFlag + ", Frequency=" + this.mFrequency + ", PsName=" + String.valueOf(this.mPsName) + "]";
        }

        protected TAInfo(Parcel parcel) {
            this.mPtyWarning = parcel.readByte() == 1;
            this.mTaAssert = parcel.readByte() == 1;
            this.mTaFlag = parcel.readByte() == 1;
            this.mTpFlag = parcel.readByte() == 1;
            this.mFrequency = parcel.readInt();
            char[] cArr = new char[parcel.readInt()];
            this.mPsName = cArr;
            parcel.readCharArray(cArr);
        }
    }

    public static final class CTInfo implements Parcelable {
        public static final Parcelable.Creator<CTInfo> CREATOR = new Parcelable.Creator<CTInfo>() { // from class: android.hardware.radio.RadioRdsInfo.CTInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CTInfo createFromParcel(Parcel parcel) {
                return new CTInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public CTInfo[] newArray(int i) {
                return new CTInfo[i];
            }
        };
        private final int mDay;
        private final int mHour;
        private final int mMinute;
        private final int mMonth;
        private final int mYear;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public CTInfo(int i, int i2, int i3, int i4, int i5) {
            this.mYear = i;
            this.mMonth = i2;
            this.mDay = i3;
            this.mHour = i4;
            this.mMinute = i5;
        }

        public int getYear() {
            return this.mYear;
        }

        public int getMonth() {
            return this.mMonth;
        }

        public int getDay() {
            return this.mDay;
        }

        public int getHour() {
            return this.mHour;
        }

        public int getMinute() {
            return this.mMinute;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mYear), Integer.valueOf(this.mMonth), Integer.valueOf(this.mDay), Integer.valueOf(this.mHour), Integer.valueOf(this.mMinute));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mYear);
            parcel.writeInt(this.mMonth);
            parcel.writeInt(this.mDay);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
        }

        public String toString() {
            return "RadioRdsInfo.CTInfo [Year=" + this.mYear + ", Month=" + this.mMonth + ", Day=" + this.mDay + ", Hour=" + this.mHour + ", Minute=" + this.mMinute + "]";
        }

        protected CTInfo(Parcel parcel) {
            this.mYear = parcel.readInt();
            this.mMonth = parcel.readInt();
            this.mDay = parcel.readInt();
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
        }
    }
}
