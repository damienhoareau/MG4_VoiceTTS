package com.android.internal.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.Preconditions;
import java.util.HashSet;

/* JADX INFO: loaded from: classes3.dex */
public abstract class PackageMonitor extends BroadcastReceiver {
    public static final int PACKAGE_PERMANENT_CHANGE = 3;
    public static final int PACKAGE_TEMPORARY_CHANGE = 2;
    public static final int PACKAGE_UNCHANGED = 0;
    public static final int PACKAGE_UPDATING = 1;
    String[] mAppearingPackages;
    int mChangeType;
    String[] mDisappearingPackages;
    String[] mModifiedComponents;
    String[] mModifiedPackages;
    Context mRegisteredContext;
    Handler mRegisteredHandler;
    boolean mSomePackagesChanged;
    static final IntentFilter sPackageFilt = new IntentFilter();
    static final IntentFilter sNonDataFilt = new IntentFilter();
    static final IntentFilter sExternalFilt = new IntentFilter();
    final HashSet<String> mUpdatingPackages = new HashSet<>();
    int mChangeUserId = -10000;
    String[] mTempArray = new String[1];

    public void onBeginPackageChanges() {
    }

    public void onFinishPackageChanges() {
    }

    public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
        return false;
    }

    public void onHandleUserStop(Intent intent, int i) {
    }

    public void onPackageAdded(String str, int i) {
    }

    public void onPackageAppeared(String str, int i) {
    }

    public void onPackageDataCleared(String str, int i) {
    }

    public void onPackageDisappeared(String str, int i) {
    }

    public void onPackageModified(String str) {
    }

    public void onPackageRemoved(String str, int i) {
    }

    public void onPackageRemovedAllUsers(String str, int i) {
    }

    public void onPackageUpdateFinished(String str, int i) {
    }

    public void onPackageUpdateStarted(String str, int i) {
    }

    public void onPackagesAvailable(String[] strArr) {
    }

    public void onPackagesSuspended(String[] strArr) {
    }

    public void onPackagesUnavailable(String[] strArr) {
    }

    public void onPackagesUnsuspended(String[] strArr) {
    }

    public void onSomePackagesChanged() {
    }

    public void onUidRemoved(int i) {
    }

    static {
        sPackageFilt.addAction(Intent.ACTION_PACKAGE_ADDED);
        sPackageFilt.addAction(Intent.ACTION_PACKAGE_REMOVED);
        sPackageFilt.addAction(Intent.ACTION_PACKAGE_CHANGED);
        sPackageFilt.addAction(Intent.ACTION_QUERY_PACKAGE_RESTART);
        sPackageFilt.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        sPackageFilt.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        sPackageFilt.addDataScheme("package");
        sNonDataFilt.addAction(Intent.ACTION_UID_REMOVED);
        sNonDataFilt.addAction(Intent.ACTION_USER_STOPPED);
        sNonDataFilt.addAction(Intent.ACTION_PACKAGES_SUSPENDED);
        sNonDataFilt.addAction(Intent.ACTION_PACKAGES_UNSUSPENDED);
        sExternalFilt.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sExternalFilt.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
    }

    public void register(Context context, Looper looper, boolean z) {
        register(context, looper, (UserHandle) null, z);
    }

    public void register(Context context, Looper looper, UserHandle userHandle, boolean z) {
        register(context, userHandle, z, looper == null ? BackgroundThread.getHandler() : new Handler(looper));
    }

    public void register(Context context, UserHandle userHandle, boolean z, Handler handler) {
        if (this.mRegisteredContext != null) {
            throw new IllegalStateException("Already registered");
        }
        this.mRegisteredContext = context;
        Handler handler2 = (Handler) Preconditions.checkNotNull(handler);
        this.mRegisteredHandler = handler2;
        if (userHandle != null) {
            context.registerReceiverAsUser(this, userHandle, sPackageFilt, null, handler2);
            context.registerReceiverAsUser(this, userHandle, sNonDataFilt, null, this.mRegisteredHandler);
            if (z) {
                context.registerReceiverAsUser(this, userHandle, sExternalFilt, null, this.mRegisteredHandler);
                return;
            }
            return;
        }
        context.registerReceiver(this, sPackageFilt, null, handler2);
        context.registerReceiver(this, sNonDataFilt, null, this.mRegisteredHandler);
        if (z) {
            context.registerReceiver(this, sExternalFilt, null, this.mRegisteredHandler);
        }
    }

    public Handler getRegisteredHandler() {
        return this.mRegisteredHandler;
    }

    public void unregister() {
        Context context = this.mRegisteredContext;
        if (context == null) {
            throw new IllegalStateException("Not registered");
        }
        context.unregisterReceiver(this);
        this.mRegisteredContext = null;
    }

    boolean isPackageUpdating(String str) {
        boolean zContains;
        synchronized (this.mUpdatingPackages) {
            zContains = this.mUpdatingPackages.contains(str);
        }
        return zContains;
    }

    public boolean onPackageChanged(String str, int i, String[] strArr) {
        if (strArr != null) {
            for (String str2 : strArr) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onPackagesSuspended(String[] strArr, Bundle bundle) {
        onPackagesSuspended(strArr);
    }

    public boolean didSomePackagesChange() {
        return this.mSomePackagesChanged;
    }

    public int isPackageAppearing(String str) {
        String[] strArr = this.mAppearingPackages;
        if (strArr == null) {
            return 0;
        }
        for (int length = strArr.length - 1; length >= 0; length--) {
            if (str.equals(this.mAppearingPackages[length])) {
                return this.mChangeType;
            }
        }
        return 0;
    }

    public boolean anyPackagesAppearing() {
        return this.mAppearingPackages != null;
    }

    public int isPackageDisappearing(String str) {
        String[] strArr = this.mDisappearingPackages;
        if (strArr == null) {
            return 0;
        }
        for (int length = strArr.length - 1; length >= 0; length--) {
            if (str.equals(this.mDisappearingPackages[length])) {
                return this.mChangeType;
            }
        }
        return 0;
    }

    public boolean anyPackagesDisappearing() {
        return this.mDisappearingPackages != null;
    }

    public boolean isReplacing() {
        return this.mChangeType == 1;
    }

    public boolean isPackageModified(String str) {
        String[] strArr = this.mModifiedPackages;
        if (strArr == null) {
            return false;
        }
        for (int length = strArr.length - 1; length >= 0; length--) {
            if (str.equals(this.mModifiedPackages[length])) {
                return true;
            }
        }
        return false;
    }

    public boolean isComponentModified(String str) {
        String[] strArr;
        if (str != null && (strArr = this.mModifiedComponents) != null) {
            for (int length = strArr.length - 1; length >= 0; length--) {
                if (str.equals(this.mModifiedComponents[length])) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getChangingUserId() {
        return this.mChangeUserId;
    }

    String getPackageName(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            return data.getSchemeSpecificPart();
        }
        return null;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
        this.mChangeUserId = intExtra;
        if (intExtra == -10000) {
            Slog.w("PackageMonitor", "Intent broadcast does not contain user handle: " + intent);
            return;
        }
        onBeginPackageChanges();
        this.mAppearingPackages = null;
        this.mDisappearingPackages = null;
        int i = 0;
        this.mSomePackagesChanged = false;
        this.mModifiedComponents = null;
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String packageName = getPackageName(intent);
            int intExtra2 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            this.mSomePackagesChanged = true;
            if (packageName != null) {
                String[] strArr = this.mTempArray;
                this.mAppearingPackages = strArr;
                strArr[0] = packageName;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    this.mModifiedPackages = this.mTempArray;
                    this.mChangeType = 1;
                    onPackageUpdateFinished(packageName, intExtra2);
                    onPackageModified(packageName);
                } else {
                    this.mChangeType = 3;
                    onPackageAdded(packageName, intExtra2);
                }
                onPackageAppeared(packageName, this.mChangeType);
                if (this.mChangeType == 1) {
                    synchronized (this.mUpdatingPackages) {
                        this.mUpdatingPackages.remove(packageName);
                    }
                }
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String packageName2 = getPackageName(intent);
            int intExtra3 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (packageName2 != null) {
                String[] strArr2 = this.mTempArray;
                this.mDisappearingPackages = strArr2;
                strArr2[0] = packageName2;
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    this.mChangeType = 1;
                    synchronized (this.mUpdatingPackages) {
                    }
                    onPackageUpdateStarted(packageName2, intExtra3);
                } else {
                    this.mChangeType = 3;
                    this.mSomePackagesChanged = true;
                    onPackageRemoved(packageName2, intExtra3);
                    if (intent.getBooleanExtra(Intent.EXTRA_REMOVED_FOR_ALL_USERS, false)) {
                        onPackageRemovedAllUsers(packageName2, intExtra3);
                    }
                }
                onPackageDisappeared(packageName2, this.mChangeType);
            }
        } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            String packageName3 = getPackageName(intent);
            int intExtra4 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            String[] stringArrayExtra = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
            this.mModifiedComponents = stringArrayExtra;
            if (packageName3 != null) {
                String[] strArr3 = this.mTempArray;
                this.mModifiedPackages = strArr3;
                strArr3[0] = packageName3;
                this.mChangeType = 3;
                if (onPackageChanged(packageName3, intExtra4, stringArrayExtra)) {
                    this.mSomePackagesChanged = true;
                }
                onPackageModified(packageName3);
            }
        } else if (Intent.ACTION_PACKAGE_DATA_CLEARED.equals(action)) {
            String packageName4 = getPackageName(intent);
            int intExtra5 = intent.getIntExtra(Intent.EXTRA_UID, 0);
            if (packageName4 != null) {
                onPackageDataCleared(packageName4, intExtra5);
            }
        } else {
            if (Intent.ACTION_QUERY_PACKAGE_RESTART.equals(action)) {
                String[] stringArrayExtra2 = intent.getStringArrayExtra(Intent.EXTRA_PACKAGES);
                this.mDisappearingPackages = stringArrayExtra2;
                this.mChangeType = 2;
                if (onHandleForceStop(intent, stringArrayExtra2, intent.getIntExtra(Intent.EXTRA_UID, 0), false)) {
                    setResultCode(-1);
                }
            } else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
                String[] strArr4 = {getPackageName(intent)};
                this.mDisappearingPackages = strArr4;
                this.mChangeType = 2;
                onHandleForceStop(intent, strArr4, intent.getIntExtra(Intent.EXTRA_UID, 0), true);
            } else if (Intent.ACTION_UID_REMOVED.equals(action)) {
                onUidRemoved(intent.getIntExtra(Intent.EXTRA_UID, 0));
            } else if (Intent.ACTION_USER_STOPPED.equals(action)) {
                if (intent.hasExtra(Intent.EXTRA_USER_HANDLE)) {
                    onHandleUserStop(intent, intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                }
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
                String[] stringArrayExtra3 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                this.mAppearingPackages = stringArrayExtra3;
                this.mChangeType = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) ? 1 : 2;
                this.mSomePackagesChanged = true;
                if (stringArrayExtra3 != null) {
                    onPackagesAvailable(stringArrayExtra3);
                    while (i < stringArrayExtra3.length) {
                        onPackageAppeared(stringArrayExtra3[i], this.mChangeType);
                        i++;
                    }
                }
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                String[] stringArrayExtra4 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                this.mDisappearingPackages = stringArrayExtra4;
                this.mChangeType = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) ? 1 : 2;
                this.mSomePackagesChanged = true;
                if (stringArrayExtra4 != null) {
                    onPackagesUnavailable(stringArrayExtra4);
                    while (i < stringArrayExtra4.length) {
                        onPackageDisappeared(stringArrayExtra4[i], this.mChangeType);
                        i++;
                    }
                }
            } else if (Intent.ACTION_PACKAGES_SUSPENDED.equals(action)) {
                String[] stringArrayExtra5 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                Bundle bundleExtra = intent.getBundleExtra(Intent.EXTRA_LAUNCHER_EXTRAS);
                this.mSomePackagesChanged = true;
                onPackagesSuspended(stringArrayExtra5, bundleExtra);
            } else if (Intent.ACTION_PACKAGES_UNSUSPENDED.equals(action)) {
                String[] stringArrayExtra6 = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                this.mSomePackagesChanged = true;
                onPackagesUnsuspended(stringArrayExtra6);
            }
        }
        if (this.mSomePackagesChanged) {
            onSomePackagesChanged();
        }
        onFinishPackageChanges();
        this.mChangeUserId = -10000;
    }
}
