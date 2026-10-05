package android.service.notification;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class NotificationStats implements Parcelable {
    public static final Parcelable.Creator<NotificationStats> CREATOR = new Parcelable.Creator<NotificationStats>() { // from class: android.service.notification.NotificationStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationStats createFromParcel(Parcel parcel) {
            return new NotificationStats(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationStats[] newArray(int i) {
            return new NotificationStats[i];
        }
    };
    public static final int DISMISSAL_AOD = 2;
    public static final int DISMISSAL_NOT_DISMISSED = -1;
    public static final int DISMISSAL_OTHER = 0;
    public static final int DISMISSAL_PEEK = 1;
    public static final int DISMISSAL_SHADE = 3;
    private boolean mDirectReplied;
    private int mDismissalSurface;
    private boolean mExpanded;
    private boolean mInteracted;
    private boolean mSeen;
    private boolean mSnoozed;
    private boolean mViewedSettings;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DismissalSurface {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NotificationStats() {
        this.mDismissalSurface = -1;
    }

    protected NotificationStats(Parcel parcel) {
        this.mDismissalSurface = -1;
        this.mSeen = parcel.readByte() != 0;
        this.mExpanded = parcel.readByte() != 0;
        this.mDirectReplied = parcel.readByte() != 0;
        this.mSnoozed = parcel.readByte() != 0;
        this.mViewedSettings = parcel.readByte() != 0;
        this.mInteracted = parcel.readByte() != 0;
        this.mDismissalSurface = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mSeen ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.mExpanded ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.mDirectReplied ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.mSnoozed ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.mViewedSettings ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.mInteracted ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.mDismissalSurface);
    }

    public boolean hasSeen() {
        return this.mSeen;
    }

    public void setSeen() {
        this.mSeen = true;
    }

    public boolean hasExpanded() {
        return this.mExpanded;
    }

    public void setExpanded() {
        this.mExpanded = true;
        this.mInteracted = true;
    }

    public boolean hasDirectReplied() {
        return this.mDirectReplied;
    }

    public void setDirectReplied() {
        this.mDirectReplied = true;
        this.mInteracted = true;
    }

    public boolean hasSnoozed() {
        return this.mSnoozed;
    }

    public void setSnoozed() {
        this.mSnoozed = true;
        this.mInteracted = true;
    }

    public boolean hasViewedSettings() {
        return this.mViewedSettings;
    }

    public void setViewedSettings() {
        this.mViewedSettings = true;
        this.mInteracted = true;
    }

    public boolean hasInteracted() {
        return this.mInteracted;
    }

    public int getDismissalSurface() {
        return this.mDismissalSurface;
    }

    public void setDismissalSurface(int i) {
        this.mDismissalSurface = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NotificationStats notificationStats = (NotificationStats) obj;
        return this.mSeen == notificationStats.mSeen && this.mExpanded == notificationStats.mExpanded && this.mDirectReplied == notificationStats.mDirectReplied && this.mSnoozed == notificationStats.mSnoozed && this.mViewedSettings == notificationStats.mViewedSettings && this.mInteracted == notificationStats.mInteracted && this.mDismissalSurface == notificationStats.mDismissalSurface;
    }

    public int hashCode() {
        return ((((((((((((this.mSeen ? 1 : 0) * 31) + (this.mExpanded ? 1 : 0)) * 31) + (this.mDirectReplied ? 1 : 0)) * 31) + (this.mSnoozed ? 1 : 0)) * 31) + (this.mViewedSettings ? 1 : 0)) * 31) + (this.mInteracted ? 1 : 0)) * 31) + this.mDismissalSurface;
    }

    public String toString() {
        return "NotificationStats{mSeen=" + this.mSeen + ", mExpanded=" + this.mExpanded + ", mDirectReplied=" + this.mDirectReplied + ", mSnoozed=" + this.mSnoozed + ", mViewedSettings=" + this.mViewedSettings + ", mInteracted=" + this.mInteracted + ", mDismissalSurface=" + this.mDismissalSurface + '}';
    }
}
