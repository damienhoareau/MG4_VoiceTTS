package android.app;

import android.annotation.SystemApi;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class BroadcastOptions {
    static final String KEY_DONT_SEND_TO_RESTRICTED_APPS = "android:broadcast.dontSendToRestrictedApps";
    static final String KEY_MAX_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.maxManifestReceiverApiLevel";
    static final String KEY_MIN_MANIFEST_RECEIVER_API_LEVEL = "android:broadcast.minManifestReceiverApiLevel";
    static final String KEY_TEMPORARY_APP_WHITELIST_DURATION = "android:broadcast.temporaryAppWhitelistDuration";
    private boolean mDontSendToRestrictedApps;
    private int mMaxManifestReceiverApiLevel;
    private int mMinManifestReceiverApiLevel;
    private long mTemporaryAppWhitelistDuration;

    public static BroadcastOptions makeBasic() {
        return new BroadcastOptions();
    }

    private BroadcastOptions() {
        this.mMinManifestReceiverApiLevel = 0;
        this.mMaxManifestReceiverApiLevel = 10000;
        this.mDontSendToRestrictedApps = false;
    }

    public BroadcastOptions(Bundle bundle) {
        this.mMinManifestReceiverApiLevel = 0;
        this.mMaxManifestReceiverApiLevel = 10000;
        this.mDontSendToRestrictedApps = false;
        this.mTemporaryAppWhitelistDuration = bundle.getLong(KEY_TEMPORARY_APP_WHITELIST_DURATION);
        this.mMinManifestReceiverApiLevel = bundle.getInt(KEY_MIN_MANIFEST_RECEIVER_API_LEVEL, 0);
        this.mMaxManifestReceiverApiLevel = bundle.getInt(KEY_MAX_MANIFEST_RECEIVER_API_LEVEL, 10000);
        this.mDontSendToRestrictedApps = bundle.getBoolean(KEY_DONT_SEND_TO_RESTRICTED_APPS, false);
    }

    public void setTemporaryAppWhitelistDuration(long j) {
        this.mTemporaryAppWhitelistDuration = j;
    }

    public long getTemporaryAppWhitelistDuration() {
        return this.mTemporaryAppWhitelistDuration;
    }

    public void setMinManifestReceiverApiLevel(int i) {
        this.mMinManifestReceiverApiLevel = i;
    }

    public int getMinManifestReceiverApiLevel() {
        return this.mMinManifestReceiverApiLevel;
    }

    public void setMaxManifestReceiverApiLevel(int i) {
        this.mMaxManifestReceiverApiLevel = i;
    }

    public int getMaxManifestReceiverApiLevel() {
        return this.mMaxManifestReceiverApiLevel;
    }

    public void setDontSendToRestrictedApps(boolean z) {
        this.mDontSendToRestrictedApps = z;
    }

    public boolean isDontSendToRestrictedApps() {
        return this.mDontSendToRestrictedApps;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        long j = this.mTemporaryAppWhitelistDuration;
        if (j > 0) {
            bundle.putLong(KEY_TEMPORARY_APP_WHITELIST_DURATION, j);
        }
        int i = this.mMinManifestReceiverApiLevel;
        if (i != 0) {
            bundle.putInt(KEY_MIN_MANIFEST_RECEIVER_API_LEVEL, i);
        }
        int i2 = this.mMaxManifestReceiverApiLevel;
        if (i2 != 10000) {
            bundle.putInt(KEY_MAX_MANIFEST_RECEIVER_API_LEVEL, i2);
        }
        if (this.mDontSendToRestrictedApps) {
            bundle.putBoolean(KEY_DONT_SEND_TO_RESTRICTED_APPS, true);
        }
        if (bundle.isEmpty()) {
            return null;
        }
        return bundle;
    }
}
