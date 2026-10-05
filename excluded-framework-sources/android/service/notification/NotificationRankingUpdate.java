package android.service.notification;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class NotificationRankingUpdate implements Parcelable {
    public static final Parcelable.Creator<NotificationRankingUpdate> CREATOR = new Parcelable.Creator<NotificationRankingUpdate>() { // from class: android.service.notification.NotificationRankingUpdate.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationRankingUpdate createFromParcel(Parcel parcel) {
            return new NotificationRankingUpdate(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NotificationRankingUpdate[] newArray(int i) {
            return new NotificationRankingUpdate[i];
        }
    };
    private final Bundle mChannels;
    private final Bundle mHidden;
    private final int[] mImportance;
    private final Bundle mImportanceExplanation;
    private final String[] mInterceptedKeys;
    private final String[] mKeys;
    private final Bundle mOverrideGroupKeys;
    private final Bundle mOverridePeople;
    private final Bundle mShowBadge;
    private final Bundle mSnoozeCriteria;
    private final Bundle mSuppressedVisualEffects;
    private final Bundle mUserSentiment;
    private final Bundle mVisibilityOverrides;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NotificationRankingUpdate(String[] strArr, String[] strArr2, Bundle bundle, Bundle bundle2, int[] iArr, Bundle bundle3, Bundle bundle4, Bundle bundle5, Bundle bundle6, Bundle bundle7, Bundle bundle8, Bundle bundle9, Bundle bundle10) {
        this.mKeys = strArr;
        this.mInterceptedKeys = strArr2;
        this.mVisibilityOverrides = bundle;
        this.mSuppressedVisualEffects = bundle2;
        this.mImportance = iArr;
        this.mImportanceExplanation = bundle3;
        this.mOverrideGroupKeys = bundle4;
        this.mChannels = bundle5;
        this.mOverridePeople = bundle6;
        this.mSnoozeCriteria = bundle7;
        this.mShowBadge = bundle8;
        this.mUserSentiment = bundle9;
        this.mHidden = bundle10;
    }

    public NotificationRankingUpdate(Parcel parcel) {
        this.mKeys = parcel.readStringArray();
        this.mInterceptedKeys = parcel.readStringArray();
        this.mVisibilityOverrides = parcel.readBundle();
        this.mSuppressedVisualEffects = parcel.readBundle();
        int[] iArr = new int[this.mKeys.length];
        this.mImportance = iArr;
        parcel.readIntArray(iArr);
        this.mImportanceExplanation = parcel.readBundle();
        this.mOverrideGroupKeys = parcel.readBundle();
        this.mChannels = parcel.readBundle();
        this.mOverridePeople = parcel.readBundle();
        this.mSnoozeCriteria = parcel.readBundle();
        this.mShowBadge = parcel.readBundle();
        this.mUserSentiment = parcel.readBundle();
        this.mHidden = parcel.readBundle();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(this.mKeys);
        parcel.writeStringArray(this.mInterceptedKeys);
        parcel.writeBundle(this.mVisibilityOverrides);
        parcel.writeBundle(this.mSuppressedVisualEffects);
        parcel.writeIntArray(this.mImportance);
        parcel.writeBundle(this.mImportanceExplanation);
        parcel.writeBundle(this.mOverrideGroupKeys);
        parcel.writeBundle(this.mChannels);
        parcel.writeBundle(this.mOverridePeople);
        parcel.writeBundle(this.mSnoozeCriteria);
        parcel.writeBundle(this.mShowBadge);
        parcel.writeBundle(this.mUserSentiment);
        parcel.writeBundle(this.mHidden);
    }

    public String[] getOrderedKeys() {
        return this.mKeys;
    }

    public String[] getInterceptedKeys() {
        return this.mInterceptedKeys;
    }

    public Bundle getVisibilityOverrides() {
        return this.mVisibilityOverrides;
    }

    public Bundle getSuppressedVisualEffects() {
        return this.mSuppressedVisualEffects;
    }

    public int[] getImportance() {
        return this.mImportance;
    }

    public Bundle getImportanceExplanation() {
        return this.mImportanceExplanation;
    }

    public Bundle getOverrideGroupKeys() {
        return this.mOverrideGroupKeys;
    }

    public Bundle getChannels() {
        return this.mChannels;
    }

    public Bundle getOverridePeople() {
        return this.mOverridePeople;
    }

    public Bundle getSnoozeCriteria() {
        return this.mSnoozeCriteria;
    }

    public Bundle getShowBadge() {
        return this.mShowBadge;
    }

    public Bundle getUserSentiment() {
        return this.mUserSentiment;
    }

    public Bundle getHidden() {
        return this.mHidden;
    }
}
