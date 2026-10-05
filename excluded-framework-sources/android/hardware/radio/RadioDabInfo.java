package android.hardware.radio;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class RadioDabInfo {

    public static final class MainInfo implements Parcelable {
        public static final Parcelable.Creator<MainInfo> CREATOR = new Parcelable.Creator<MainInfo>() { // from class: android.hardware.radio.RadioDabInfo.MainInfo.1
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
        private final int mEnsembleId;
        private final int mFrequency;
        private final int mPty;
        private final int mSecondaryServiceId;
        private final long mServiceId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public MainInfo(int i, int i2, long j, int i3, int i4) {
            this.mFrequency = i;
            this.mEnsembleId = i2;
            this.mServiceId = j;
            this.mPty = i3;
            this.mSecondaryServiceId = i4;
        }

        public int getFrequency() {
            return this.mFrequency;
        }

        public int getEnsembleId() {
            return this.mEnsembleId;
        }

        public long getServiceId() {
            return this.mServiceId;
        }

        public int getPty() {
            return this.mPty;
        }

        public int getSecondaryServiceId() {
            return this.mSecondaryServiceId;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mFrequency), Integer.valueOf(this.mEnsembleId), Long.valueOf(this.mServiceId), Integer.valueOf(this.mPty), Integer.valueOf(this.mSecondaryServiceId));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFrequency);
            parcel.writeInt(this.mEnsembleId);
            parcel.writeLong(this.mServiceId);
            parcel.writeInt(this.mPty);
            parcel.writeInt(this.mSecondaryServiceId);
        }

        public String toString() {
            return "RadioDabInfo.MainInfo[Frequency=" + this.mFrequency + ", Ensemble Id=" + this.mEnsembleId + ", Service Id=" + this.mServiceId + ", Pty=" + this.mPty + ", Secondary Service Id=" + this.mSecondaryServiceId + "]";
        }

        protected MainInfo(Parcel parcel) {
            this.mFrequency = parcel.readInt();
            this.mEnsembleId = parcel.readInt();
            this.mServiceId = parcel.readLong();
            this.mPty = parcel.readInt();
            this.mSecondaryServiceId = parcel.readInt();
        }
    }

    public static final class StationListInfo implements Parcelable {
        public static final Parcelable.Creator<StationListInfo> CREATOR = new Parcelable.Creator<StationListInfo>() { // from class: android.hardware.radio.RadioDabInfo.StationListInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StationListInfo createFromParcel(Parcel parcel) {
                return new StationListInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StationListInfo[] newArray(int i) {
                return new StationListInfo[i];
            }
        };
        private final int mListNum;
        private StationInfo[] mStationList;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public StationListInfo(int i, StationInfo[] stationInfoArr) {
            this.mListNum = i;
            this.mStationList = stationInfoArr;
        }

        public int getListNum() {
            return this.mListNum;
        }

        public StationInfo[] getStationListInfo() {
            return this.mStationList;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mListNum), this.mStationList);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mListNum);
            parcel.writeParcelableArray(this.mStationList, i);
        }

        public String toString() {
            return "RadioDabInfo.StationListInfo[List Num=" + this.mListNum + ", Station List=" + Arrays.toString(this.mStationList) + "]";
        }

        protected StationListInfo(Parcel parcel) {
            this.mListNum = parcel.readInt();
            Parcelable[] parcelableArray = parcel.readParcelableArray(StationInfo.class.getClassLoader());
            this.mStationList = new StationInfo[parcelableArray.length];
            for (int i = 0; i < parcelableArray.length; i++) {
                this.mStationList[i] = (StationInfo) parcelableArray[i];
            }
        }
    }

    public static final class StationInfo implements Parcelable {
        public static final Parcelable.Creator<StationInfo> CREATOR = new Parcelable.Creator<StationInfo>() { // from class: android.hardware.radio.RadioDabInfo.StationInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StationInfo createFromParcel(Parcel parcel) {
                return new StationInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public StationInfo[] newArray(int i) {
                return new StationInfo[i];
            }
        };
        private final int mEnsembleId;
        private final char[] mEnsembleName;
        private final int mFrequencyIndex;
        private final int mPty;
        private final long mServiceId;
        private final char[] mServiceName;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public StationInfo(int i, char[] cArr, long j, char[] cArr2, int i2, int i3) {
            this.mPty = i;
            this.mServiceName = cArr;
            this.mServiceId = j;
            this.mEnsembleName = cArr2;
            this.mEnsembleId = i2;
            this.mFrequencyIndex = i3;
        }

        public int getPty() {
            return this.mPty;
        }

        public char[] getServiceName() {
            return this.mServiceName;
        }

        public long getServiceId() {
            return this.mServiceId;
        }

        public char[] getEnsembleName() {
            return this.mEnsembleName;
        }

        public int getEnsembleId() {
            return this.mEnsembleId;
        }

        public int getFrequencyIndex() {
            return this.mFrequencyIndex;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mPty), this.mServiceName, Long.valueOf(this.mServiceId), this.mEnsembleName, Integer.valueOf(this.mEnsembleId), Integer.valueOf(this.mFrequencyIndex));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mPty);
            parcel.writeInt(this.mServiceName.length);
            parcel.writeCharArray(this.mServiceName);
            parcel.writeLong(this.mServiceId);
            parcel.writeInt(this.mEnsembleName.length);
            parcel.writeCharArray(this.mEnsembleName);
            parcel.writeInt(this.mEnsembleId);
            parcel.writeInt(this.mFrequencyIndex);
        }

        public String toString() {
            return "RadioDabInfo.StationInfo[Pty=" + this.mPty + ", Service Name=" + String.valueOf(this.mServiceName) + ", Service Id=" + this.mServiceId + ", Ensemble Name=" + String.valueOf(this.mEnsembleName) + ", Ensemble Id=" + this.mEnsembleId + ", Frequency Index=" + this.mFrequencyIndex + "]";
        }

        protected StationInfo(Parcel parcel) {
            this.mPty = parcel.readInt();
            char[] cArr = new char[parcel.readInt()];
            this.mServiceName = cArr;
            parcel.readCharArray(cArr);
            this.mServiceId = parcel.readLong();
            char[] cArr2 = new char[parcel.readInt()];
            this.mEnsembleName = cArr2;
            parcel.readCharArray(cArr2);
            this.mEnsembleId = parcel.readInt();
            this.mFrequencyIndex = parcel.readInt();
        }
    }

    public static final class ServiceInformationList implements Parcelable {
        public static final Parcelable.Creator<ServiceInformationList> CREATOR = new Parcelable.Creator<ServiceInformationList>() { // from class: android.hardware.radio.RadioDabInfo.ServiceInformationList.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceInformationList createFromParcel(Parcel parcel) {
                return new ServiceInformationList(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceInformationList[] newArray(int i) {
                return new ServiceInformationList[i];
            }
        };
        private final int mListNum;
        private ServiceInformation[] mServiceInformations;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ServiceInformationList(int i, ServiceInformation[] serviceInformationArr) {
            this.mListNum = i;
            this.mServiceInformations = serviceInformationArr;
        }

        public int getListNum() {
            return this.mListNum;
        }

        public ServiceInformation[] getServiceInformationList() {
            return this.mServiceInformations;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mListNum), this.mServiceInformations);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mListNum);
            parcel.writeParcelableArray(this.mServiceInformations, i);
        }

        public String toString() {
            return "RadioDabInfo.ServiceInformationList[List Num=" + this.mListNum + ", mServiceInformations List=" + Arrays.toString(this.mServiceInformations) + "]";
        }

        protected ServiceInformationList(Parcel parcel) {
            this.mListNum = parcel.readInt();
            Parcelable[] parcelableArray = parcel.readParcelableArray(ServiceInformation.class.getClassLoader());
            this.mServiceInformations = new ServiceInformation[parcelableArray.length];
            for (int i = 0; i < parcelableArray.length; i++) {
                this.mServiceInformations[i] = (ServiceInformation) parcelableArray[i];
            }
        }
    }

    public static final class LogoInfo implements Parcelable {
        public static final Parcelable.Creator<LogoInfo> CREATOR = new Parcelable.Creator<LogoInfo>() { // from class: android.hardware.radio.RadioDabInfo.LogoInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LogoInfo createFromParcel(Parcel parcel) {
                return new LogoInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LogoInfo[] newArray(int i) {
                return new LogoInfo[i];
            }
        };
        private final int mHeight;
        private final String mLogoType;
        private final String mLogoUrl;
        private final int mWidth;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public LogoInfo(int i, int i2, String str, String str2) {
            this.mWidth = i;
            this.mHeight = i2;
            this.mLogoType = str;
            this.mLogoUrl = str2;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public String getLogoUrl() {
            return this.mLogoUrl;
        }

        public String getLogoType() {
            return this.mLogoType;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), this.mLogoType, this.mLogoUrl);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mWidth);
            parcel.writeInt(this.mHeight);
            parcel.writeString(this.mLogoType);
            parcel.writeString(this.mLogoUrl);
        }

        public String toString() {
            return "LogoInfo[mWidth=" + this.mWidth + ", mHeight=" + this.mHeight + ", mLogoType=" + this.mLogoType + ", mLogoUrl=" + this.mLogoUrl + "]";
        }

        protected LogoInfo(Parcel parcel) {
            this.mWidth = parcel.readInt();
            this.mHeight = parcel.readInt();
            this.mLogoType = parcel.readString();
            this.mLogoUrl = parcel.readString();
        }
    }

    public static final class ServiceInformation implements Parcelable {
        public static final Parcelable.Creator<ServiceInformation> CREATOR = new Parcelable.Creator<ServiceInformation>() { // from class: android.hardware.radio.RadioDabInfo.ServiceInformation.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceInformation createFromParcel(Parcel parcel) {
                return new ServiceInformation(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceInformation[] newArray(int i) {
                return new ServiceInformation[i];
            }
        };
        private final int mEnsembleId;
        private final int mFrequency;
        private final LogoInfo[] mLogoInfos;
        private final int mServiceId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ServiceInformation(int i, int i2, int i3, LogoInfo[] logoInfoArr) {
            this.mEnsembleId = i;
            this.mServiceId = i2;
            this.mFrequency = i3;
            this.mLogoInfos = logoInfoArr;
        }

        public int getEnsembleId() {
            return this.mEnsembleId;
        }

        public int getServiceId() {
            return this.mServiceId;
        }

        public LogoInfo[] getLogoInfos() {
            return this.mLogoInfos;
        }

        public int getFrequency() {
            return this.mFrequency;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mEnsembleId), Integer.valueOf(this.mServiceId), Integer.valueOf(this.mFrequency), this.mLogoInfos);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mEnsembleId);
            parcel.writeInt(this.mServiceId);
            parcel.writeInt(this.mFrequency);
            parcel.writeParcelableArray(this.mLogoInfos, i);
        }

        public String toString() {
            return "RadioDabInfo.ServiceInformation[mEnsembleId=" + this.mEnsembleId + ", mServiceId=" + this.mServiceId + ", mFrequency=" + this.mFrequency + ",mLogoInfos=" + Arrays.toString(this.mLogoInfos) + "]";
        }

        protected ServiceInformation(Parcel parcel) {
            this.mEnsembleId = parcel.readInt();
            this.mServiceId = parcel.readInt();
            this.mFrequency = parcel.readInt();
            Parcelable[] parcelableArray = parcel.readParcelableArray(LogoInfo.class.getClassLoader());
            this.mLogoInfos = new LogoInfo[parcelableArray.length];
            for (int i = 0; i < parcelableArray.length; i++) {
                this.mLogoInfos[i] = (LogoInfo) parcelableArray[i];
            }
        }
    }

    public static class AnnouncementInfo implements Parcelable {
        public static final Parcelable.Creator<AnnouncementInfo> CREATOR = new Parcelable.Creator<AnnouncementInfo>() { // from class: android.hardware.radio.RadioDabInfo.AnnouncementInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AnnouncementInfo createFromParcel(Parcel parcel) {
                return new AnnouncementInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AnnouncementInfo[] newArray(int i) {
                return new AnnouncementInfo[i];
            }
        };
        private boolean mAlarm;
        private boolean mAreaWeatherFlash;
        private boolean mEventAnnouncement;
        private boolean mFinancialReport;
        private boolean mNewsFlash;
        private boolean mProgrammeInfomation;
        private boolean mRoadTrafficFlash;
        private boolean mSpecialEvent;
        private boolean mSportReport;
        private boolean mTransportFlash;
        private boolean mWarning;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public AnnouncementInfo() {
            this.mSpecialEvent = true;
            this.mEventAnnouncement = true;
            this.mAreaWeatherFlash = true;
            this.mNewsFlash = true;
            this.mWarning = true;
            this.mTransportFlash = true;
            this.mRoadTrafficFlash = true;
            this.mAlarm = true;
            this.mProgrammeInfomation = true;
            this.mSportReport = true;
            this.mFinancialReport = true;
        }

        public AnnouncementInfo(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11) {
            this.mSpecialEvent = z;
            this.mEventAnnouncement = z2;
            this.mAreaWeatherFlash = z3;
            this.mNewsFlash = z4;
            this.mWarning = z5;
            this.mTransportFlash = z6;
            this.mRoadTrafficFlash = z7;
            this.mAlarm = z8;
            this.mProgrammeInfomation = z9;
            this.mSportReport = z10;
            this.mFinancialReport = z11;
        }

        private AnnouncementInfo(Parcel parcel) {
            this.mSpecialEvent = parcel.readByte() == 1;
            this.mEventAnnouncement = parcel.readByte() == 1;
            this.mAreaWeatherFlash = parcel.readByte() == 1;
            this.mNewsFlash = parcel.readByte() == 1;
            this.mWarning = parcel.readByte() == 1;
            this.mTransportFlash = parcel.readByte() == 1;
            this.mRoadTrafficFlash = parcel.readByte() == 1;
            this.mAlarm = parcel.readByte() == 1;
            this.mProgrammeInfomation = parcel.readByte() == 1;
            this.mSportReport = parcel.readByte() == 1;
            this.mFinancialReport = parcel.readByte() == 1;
        }

        public boolean getSpecialEvent() {
            return this.mSpecialEvent;
        }

        public boolean getEventAnnouncement() {
            return this.mEventAnnouncement;
        }

        public boolean getAreaWeatherFlash() {
            return this.mAreaWeatherFlash;
        }

        public boolean getNewsFlash() {
            return this.mNewsFlash;
        }

        public boolean getWarning() {
            return this.mWarning;
        }

        public boolean getTransportFlash() {
            return this.mTransportFlash;
        }

        public boolean getRoadTrafficFlash() {
            return this.mRoadTrafficFlash;
        }

        public boolean getAlarm() {
            return this.mAlarm;
        }

        public boolean getProgrammeInfomation() {
            return this.mProgrammeInfomation;
        }

        public boolean getSportReport() {
            return this.mSportReport;
        }

        public boolean getFinancialReport() {
            return this.mFinancialReport;
        }

        public void setSpecialEvent(boolean z) {
            this.mSpecialEvent = z;
        }

        public void setEventAnnouncement(boolean z) {
            this.mEventAnnouncement = z;
        }

        public void setAreaWeatherFlash(boolean z) {
            this.mAreaWeatherFlash = z;
        }

        public void setNewsFlash(boolean z) {
            this.mNewsFlash = z;
        }

        public void setWarning(boolean z) {
            this.mWarning = z;
        }

        public void setTransportFlash(boolean z) {
            this.mTransportFlash = z;
        }

        public void setRoadTrafficFlash(boolean z) {
            this.mRoadTrafficFlash = z;
        }

        public void setAlarm(boolean z) {
            this.mAlarm = z;
        }

        public void setProgrammeInfomation(boolean z) {
            this.mProgrammeInfomation = z;
        }

        public void setSportReport(boolean z) {
            this.mSportReport = z;
        }

        public void setFinancialReport(boolean z) {
            this.mFinancialReport = z;
        }

        public void clearAllStatus() {
            this.mSpecialEvent = false;
            this.mEventAnnouncement = false;
            this.mAreaWeatherFlash = false;
            this.mNewsFlash = false;
            this.mWarning = false;
            this.mTransportFlash = false;
            this.mRoadTrafficFlash = false;
            this.mAlarm = false;
            this.mProgrammeInfomation = false;
            this.mSportReport = false;
            this.mFinancialReport = false;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte(this.mSpecialEvent ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mEventAnnouncement ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mAreaWeatherFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mNewsFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mWarning ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mTransportFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mRoadTrafficFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mAlarm ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mProgrammeInfomation ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mSportReport ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mFinancialReport ? (byte) 1 : (byte) 0);
        }

        public String toString() {
            return "AnnouncementInfo [SpecialEvent=" + this.mSpecialEvent + ", EventAnnouncement=" + this.mEventAnnouncement + ", AreaWeatherFlash=" + this.mAreaWeatherFlash + ", NewsFlash=" + this.mNewsFlash + ", Warning=" + this.mWarning + "  TransportFlash=" + this.mTransportFlash + ", RoadTrafficFlash=" + this.mRoadTrafficFlash + ", Alarm=" + this.mAlarm + ", ProgrammeInfomation=" + this.mProgrammeInfomation + ", SportReport=" + this.mSportReport + ", FinancialReport=" + this.mFinancialReport + "]";
        }

        public int hashCode() {
            return (((((((((((((((((((((super.hashCode() * 31) + (this.mSpecialEvent ? 1 : 0)) * 31) + (this.mEventAnnouncement ? 1 : 0)) * 31) + (this.mAreaWeatherFlash ? 1 : 0)) * 31) + (this.mNewsFlash ? 1 : 0)) * 31) + (this.mWarning ? 1 : 0)) * 31) + (this.mTransportFlash ? 1 : 0)) * 31) + (this.mRoadTrafficFlash ? 1 : 0)) * 31) + (this.mAlarm ? 1 : 0)) * 31) + (this.mProgrammeInfomation ? 1 : 0)) * 31) + (this.mSportReport ? 1 : 0)) * 31) + (this.mFinancialReport ? 1 : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj) || !(obj instanceof AnnouncementInfo)) {
                return false;
            }
            AnnouncementInfo announcementInfo = (AnnouncementInfo) obj;
            return this.mSpecialEvent == announcementInfo.mSpecialEvent && this.mEventAnnouncement == announcementInfo.mEventAnnouncement && this.mAreaWeatherFlash == announcementInfo.mAreaWeatherFlash && this.mNewsFlash == announcementInfo.mNewsFlash && this.mWarning == announcementInfo.mWarning && this.mTransportFlash == announcementInfo.mTransportFlash && this.mRoadTrafficFlash == announcementInfo.mRoadTrafficFlash && this.mAlarm == announcementInfo.mAlarm && this.mProgrammeInfomation == announcementInfo.mProgrammeInfomation && this.mSportReport == announcementInfo.mSportReport && this.mFinancialReport == announcementInfo.mFinancialReport;
        }
    }

    public static class AnnouncementStatusInfo implements Parcelable {
        public static final Parcelable.Creator<AnnouncementStatusInfo> CREATOR = new Parcelable.Creator<AnnouncementStatusInfo>() { // from class: android.hardware.radio.RadioDabInfo.AnnouncementStatusInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AnnouncementStatusInfo createFromParcel(Parcel parcel) {
                return new AnnouncementStatusInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AnnouncementStatusInfo[] newArray(int i) {
                return new AnnouncementStatusInfo[i];
            }
        };
        private boolean mAlarm;
        private boolean mAreaWeatherFlash;
        private char[] mEnsembleLabel;
        private char[] mEnsembleShortFlag;
        private boolean mEventAnnouncement;
        private boolean mFinancialReport;
        private boolean mNewsFlash;
        private boolean mProgrammeInfomation;
        private boolean mRoadTrafficFlash;
        private char[] mServiceLabel;
        private char[] mServiceShortFlag;
        private boolean mSpecialEvent;
        private boolean mSportReport;
        private boolean mTransportFlash;
        private boolean mWarning;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public AnnouncementStatusInfo(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, char[] cArr, char[] cArr2, char[] cArr3, char[] cArr4) {
            this.mSpecialEvent = z;
            this.mEventAnnouncement = z2;
            this.mAreaWeatherFlash = z3;
            this.mNewsFlash = z4;
            this.mWarning = z5;
            this.mTransportFlash = z6;
            this.mRoadTrafficFlash = z7;
            this.mAlarm = z8;
            this.mProgrammeInfomation = z9;
            this.mSportReport = z10;
            this.mFinancialReport = z11;
            this.mServiceLabel = cArr;
            this.mServiceShortFlag = cArr2;
            this.mEnsembleLabel = cArr3;
            this.mEnsembleShortFlag = cArr4;
        }

        private AnnouncementStatusInfo(Parcel parcel) {
            this.mSpecialEvent = parcel.readByte() == 1;
            this.mEventAnnouncement = parcel.readByte() == 1;
            this.mAreaWeatherFlash = parcel.readByte() == 1;
            this.mNewsFlash = parcel.readByte() == 1;
            this.mWarning = parcel.readByte() == 1;
            this.mTransportFlash = parcel.readByte() == 1;
            this.mRoadTrafficFlash = parcel.readByte() == 1;
            this.mAlarm = parcel.readByte() == 1;
            this.mProgrammeInfomation = parcel.readByte() == 1;
            this.mSportReport = parcel.readByte() == 1;
            this.mFinancialReport = parcel.readByte() == 1;
            char[] cArr = new char[parcel.readInt()];
            this.mServiceLabel = cArr;
            parcel.readCharArray(cArr);
            char[] cArr2 = new char[parcel.readInt()];
            this.mServiceShortFlag = cArr2;
            parcel.readCharArray(cArr2);
            char[] cArr3 = new char[parcel.readInt()];
            this.mEnsembleLabel = cArr3;
            parcel.readCharArray(cArr3);
            char[] cArr4 = new char[parcel.readInt()];
            this.mEnsembleShortFlag = cArr4;
            parcel.readCharArray(cArr4);
        }

        public boolean getSpecialEvent() {
            return this.mSpecialEvent;
        }

        public boolean getEventAnnouncement() {
            return this.mEventAnnouncement;
        }

        public boolean getAreaWeatherFlash() {
            return this.mAreaWeatherFlash;
        }

        public boolean getNewsFlash() {
            return this.mNewsFlash;
        }

        public boolean getWarning() {
            return this.mWarning;
        }

        public boolean getTransportFlash() {
            return this.mTransportFlash;
        }

        public boolean getRoadTrafficFlash() {
            return this.mRoadTrafficFlash;
        }

        public boolean getAlarm() {
            return this.mAlarm;
        }

        public boolean getProgrammeInfomation() {
            return this.mProgrammeInfomation;
        }

        public boolean getSportReport() {
            return this.mSportReport;
        }

        public boolean getFinancialReport() {
            return this.mFinancialReport;
        }

        public void setSpecialEvent(boolean z) {
            this.mSpecialEvent = z;
        }

        public void setEventAnnouncement(boolean z) {
            this.mEventAnnouncement = z;
        }

        public void setAreaWeatherFlash(boolean z) {
            this.mAreaWeatherFlash = z;
        }

        public void setNewsFlash(boolean z) {
            this.mNewsFlash = z;
        }

        public void setWarning(boolean z) {
            this.mWarning = z;
        }

        public void setTransportFlash(boolean z) {
            this.mTransportFlash = z;
        }

        public void setRoadTrafficFlash(boolean z) {
            this.mRoadTrafficFlash = z;
        }

        public void setAlarm(boolean z) {
            this.mAlarm = z;
        }

        public void setProgrammeInfomation(boolean z) {
            this.mProgrammeInfomation = z;
        }

        public void setSportReport(boolean z) {
            this.mSportReport = z;
        }

        public void setFinancialReport(boolean z) {
            this.mFinancialReport = z;
        }

        public char[] getServiceLabel() {
            return this.mServiceLabel;
        }

        public char[] getServiceShortFlag() {
            return this.mServiceShortFlag;
        }

        public char[] getEnsembleLabel() {
            return this.mEnsembleLabel;
        }

        public char[] getEnsembleShortFlag() {
            return this.mEnsembleShortFlag;
        }

        public void setServiceLabel(char[] cArr) {
            this.mServiceLabel = cArr;
        }

        public void setServiceShortFlag(char[] cArr) {
            this.mServiceShortFlag = cArr;
        }

        public void setEnsembleLabel(char[] cArr) {
            this.mEnsembleLabel = cArr;
        }

        public void setEnsembleShortFlag(char[] cArr) {
            this.mEnsembleShortFlag = cArr;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte(this.mSpecialEvent ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mEventAnnouncement ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mAreaWeatherFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mNewsFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mWarning ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mTransportFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mRoadTrafficFlash ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mAlarm ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mProgrammeInfomation ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mSportReport ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.mFinancialReport ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.mServiceLabel.length);
            parcel.writeCharArray(this.mServiceLabel);
            parcel.writeInt(this.mServiceShortFlag.length);
            parcel.writeCharArray(this.mServiceShortFlag);
            parcel.writeInt(this.mEnsembleLabel.length);
            parcel.writeCharArray(this.mEnsembleLabel);
            parcel.writeInt(this.mEnsembleShortFlag.length);
            parcel.writeCharArray(this.mEnsembleShortFlag);
        }

        public String toString() {
            return "AnnouncementStatusInfo [SpecialEvent=" + this.mSpecialEvent + ", EventAnnouncement=" + this.mEventAnnouncement + ", AreaWeatherFlash=" + this.mAreaWeatherFlash + ", NewsFlash=" + this.mNewsFlash + ", Warning=" + this.mWarning + "  TransportFlash=" + this.mTransportFlash + ", RoadTrafficFlash=" + this.mRoadTrafficFlash + ", Alarm=" + this.mAlarm + ", ProgrammeInfomation=" + this.mProgrammeInfomation + ", SportReport=" + this.mSportReport + ", FinancialReport=" + this.mFinancialReport + ", mServiceLabel=" + String.valueOf(this.mServiceLabel) + ", mServiceShortFlag=" + String.valueOf(this.mServiceShortFlag) + ", mEnsembleLabel=" + String.valueOf(this.mEnsembleLabel) + ", mEnsembleShortFlag=" + String.valueOf(this.mEnsembleShortFlag) + "]";
        }
    }

    public static final class DLPlusInfo implements Parcelable {
        public static final int ALBUM_TYPE = 2;
        public static final int ARTIST_TYPE = 4;
        public static final int BAND_TYPE = 9;
        public static final int COMMENT_TYPE = 10;
        public static final int COMPOSER_TYPE = 8;
        public static final int COMPOSITION_TYPE = 5;
        public static final int CONDUCTOR_TYPE = 7;
        public static final Parcelable.Creator<DLPlusInfo> CREATOR = new Parcelable.Creator<DLPlusInfo>() { // from class: android.hardware.radio.RadioDabInfo.DLPlusInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DLPlusInfo createFromParcel(Parcel parcel) {
                return new DLPlusInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DLPlusInfo[] newArray(int i) {
                return new DLPlusInfo[i];
            }
        };
        public static final int DUMMY_TYPE = 0;
        public static final int GENRE_TYPE = 11;
        public static final int MOVEMENT_TYPE = 6;
        public static final int TITLE_TYPE = 1;
        public static final int TRACKNUMBER_TYPE = 3;
        private final char[] mDLPlus;
        private final int mType;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public DLPlusInfo(int i, char[] cArr) {
            this.mType = i;
            this.mDLPlus = cArr;
        }

        public int getType() {
            return this.mType;
        }

        public char[] getDLPlus() {
            return this.mDLPlus;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mType), this.mDLPlus);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mType);
            parcel.writeInt(this.mDLPlus.length);
            parcel.writeCharArray(this.mDLPlus);
        }

        public String toString() {
            return "RadioDabInfo.DLPlusInfo [Type=" + this.mType + "  DLPlus=" + String.valueOf(this.mDLPlus) + "]";
        }

        protected DLPlusInfo(Parcel parcel) {
            this.mType = parcel.readInt();
            char[] cArr = new char[parcel.readInt()];
            this.mDLPlus = cArr;
            parcel.readCharArray(cArr);
        }
    }

    public static final class EPGInfo implements Parcelable {
        public static final Parcelable.Creator<EPGInfo> CREATOR = new Parcelable.Creator<EPGInfo>() { // from class: android.hardware.radio.RadioDabInfo.EPGInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public EPGInfo createFromParcel(Parcel parcel) {
                return new EPGInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public EPGInfo[] newArray(int i) {
                return new EPGInfo[i];
            }
        };
        private final long mDuration;
        private final long mEnsembleID;
        private final String mProgrammeName;
        private final long mServiceID;
        private final String mTime;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public EPGInfo(long j, long j2, String str, String str2, long j3) {
            this.mEnsembleID = j;
            this.mServiceID = j2;
            this.mTime = str;
            this.mProgrammeName = str2;
            this.mDuration = j3;
        }

        public long getmEnsembleID() {
            return this.mEnsembleID;
        }

        public long getServiceID() {
            return this.mServiceID;
        }

        public String getTime() {
            return this.mTime;
        }

        public String getProgrammeName() {
            return this.mProgrammeName;
        }

        public long getDuration() {
            return this.mDuration;
        }

        public int hashCode() {
            return Objects.hash(this.mTime, this.mProgrammeName);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.mEnsembleID);
            parcel.writeLong(this.mServiceID);
            parcel.writeString(this.mTime);
            parcel.writeString(this.mProgrammeName);
            parcel.writeLong(this.mDuration);
        }

        public String toString() {
            return "RadioDabInfo.EPGInfo [EnsembleId=" + this.mEnsembleID + "  ServiceId=" + this.mServiceID + "  Time=" + this.mTime + "  ProgrammeName=" + this.mProgrammeName + "  Duration=" + this.mDuration + "]";
        }

        protected EPGInfo(Parcel parcel) {
            this.mEnsembleID = parcel.readLong();
            this.mServiceID = parcel.readLong();
            this.mTime = parcel.readString();
            this.mProgrammeName = parcel.readString();
            this.mDuration = parcel.readLong();
        }
    }

    public static final class SlideShowInfo implements Parcelable {
        public static final Parcelable.Creator<SlideShowInfo> CREATOR = new Parcelable.Creator<SlideShowInfo>() { // from class: android.hardware.radio.RadioDabInfo.SlideShowInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SlideShowInfo createFromParcel(Parcel parcel) {
                return new SlideShowInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SlideShowInfo[] newArray(int i) {
                return new SlideShowInfo[i];
            }
        };
        private final byte[] mBuffer;
        private final int mLen;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public SlideShowInfo(int i, byte[] bArr) {
            this.mLen = i;
            this.mBuffer = bArr;
        }

        public int getLen() {
            return this.mLen;
        }

        public byte[] getBuffer() {
            return this.mBuffer;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mLen), this.mBuffer);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mLen);
            parcel.writeInt(this.mBuffer.length);
            parcel.writeByteArray(this.mBuffer);
        }

        public String toString() {
            return "RadioDabInfo.SlideShow [Len=" + this.mLen + "]";
        }

        protected SlideShowInfo(Parcel parcel) {
            this.mLen = parcel.readInt();
            byte[] bArr = new byte[parcel.readInt()];
            this.mBuffer = bArr;
            parcel.readByteArray(bArr);
        }
    }

    public static final class ServiceLogoInfo implements Parcelable {
        public static final Parcelable.Creator<ServiceLogoInfo> CREATOR = new Parcelable.Creator<ServiceLogoInfo>() { // from class: android.hardware.radio.RadioDabInfo.ServiceLogoInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceLogoInfo createFromParcel(Parcel parcel) {
                return new ServiceLogoInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ServiceLogoInfo[] newArray(int i) {
                return new ServiceLogoInfo[i];
            }
        };
        private final byte[] mBuffer;
        private final int mLen;
        private final char[] mUrl;
        private final int mUrlLen;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ServiceLogoInfo(int i, char[] cArr, int i2, byte[] bArr) {
            this.mUrlLen = i;
            this.mUrl = cArr;
            this.mLen = i2;
            this.mBuffer = bArr;
        }

        public int getUrlLen() {
            return this.mUrlLen;
        }

        public char[] getUrl() {
            return this.mUrl;
        }

        public int getLen() {
            return this.mLen;
        }

        public byte[] getBuffer() {
            return this.mBuffer;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mUrlLen), this.mUrl, Integer.valueOf(this.mLen), this.mBuffer);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mUrlLen);
            parcel.writeInt(this.mUrl.length);
            parcel.writeCharArray(this.mUrl);
            parcel.writeInt(this.mLen);
            parcel.writeInt(this.mBuffer.length);
            parcel.writeByteArray(this.mBuffer);
        }

        public String toString() {
            return "RadioDabInfo.ServiceLogoInfo [url=" + String.valueOf(this.mUrl) + " [Len=" + this.mLen + "]";
        }

        protected ServiceLogoInfo(Parcel parcel) {
            this.mUrlLen = parcel.readInt();
            char[] cArr = new char[parcel.readInt()];
            this.mUrl = cArr;
            parcel.readCharArray(cArr);
            this.mLen = parcel.readInt();
            byte[] bArr = new byte[parcel.readInt()];
            this.mBuffer = bArr;
            parcel.readByteArray(bArr);
        }
    }

    public static final class CTInfo implements Parcelable {
        public static final Parcelable.Creator<CTInfo> CREATOR = new Parcelable.Creator<CTInfo>() { // from class: android.hardware.radio.RadioDabInfo.CTInfo.1
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
        private final int mSecond;
        private final int mYear;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public CTInfo(int i, int i2, int i3, int i4, int i5, int i6) {
            this.mYear = i;
            this.mMonth = i2;
            this.mDay = i3;
            this.mHour = i4;
            this.mMinute = i5;
            this.mSecond = i6;
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

        public int getSecond() {
            return this.mSecond;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mYear), Integer.valueOf(this.mMonth), Integer.valueOf(this.mDay), Integer.valueOf(this.mHour), Integer.valueOf(this.mMinute), Integer.valueOf(this.mSecond));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mYear);
            parcel.writeInt(this.mMonth);
            parcel.writeInt(this.mDay);
            parcel.writeInt(this.mHour);
            parcel.writeInt(this.mMinute);
            parcel.writeInt(this.mSecond);
        }

        public String toString() {
            return "RadioDabInfo.CTInfo [Year=" + this.mYear + ", Month=" + this.mMonth + ", Day=" + this.mDay + ", Hour=" + this.mHour + ", Minute=" + this.mMinute + ", mSecond=" + this.mSecond + "]";
        }

        protected CTInfo(Parcel parcel) {
            this.mYear = parcel.readInt();
            this.mMonth = parcel.readInt();
            this.mDay = parcel.readInt();
            this.mHour = parcel.readInt();
            this.mMinute = parcel.readInt();
            this.mSecond = parcel.readInt();
        }
    }
}
