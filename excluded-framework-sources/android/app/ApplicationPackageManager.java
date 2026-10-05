package android.app;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.ComponentInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IDexModuleRegisterCallback;
import android.content.pm.IOnPermissionsChangeListener;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstantAppInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.KeySet;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.content.pm.VersionedPackage;
import android.content.pm.dex.ArtManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.ArrayMap;
import android.util.IconDrawableFactory;
import android.util.LauncherIcons;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import com.android.internal.util.UserIcons;
import dalvik.system.VMRuntime;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes.dex */
public class ApplicationPackageManager extends PackageManager {
    private static final boolean DEBUG_ICONS = false;
    private static final int DEFAULT_EPHEMERAL_COOKIE_MAX_SIZE_BYTES = 16384;
    private static final String TAG = "ApplicationPackageManager";
    private static final int sDefaultFlags = 1024;
    private ArtManager mArtManager;
    private final ContextImpl mContext;
    private PackageInstaller mInstaller;
    private final IPackageManager mPM;
    private String mPermissionsControllerPackageName;
    private UserManager mUserManager;
    public static final int[] CORP_BADGE_LABEL_RES_ID = {R.string.managed_profile_label_badge, R.string.managed_profile_label_badge_2, R.string.managed_profile_label_badge_3};
    private static final Object sSync = new Object();
    private static ArrayMap<ResourceName, WeakReference<Drawable.ConstantState>> sIconCache = new ArrayMap<>();
    private static ArrayMap<ResourceName, WeakReference<CharSequence>> sStringCache = new ArrayMap<>();
    private final Object mLock = new Object();
    private final ArrayList<MoveCallbackDelegate> mDelegates = new ArrayList<>();
    volatile int mCachedSafeMode = -1;
    private final Map<PackageManager.OnPermissionsChangedListener, IOnPermissionsChangeListener> mPermissionListeners = new ArrayMap();

    UserManager getUserManager() {
        UserManager userManager;
        synchronized (this.mLock) {
            if (this.mUserManager == null) {
                this.mUserManager = UserManager.get(this.mContext);
            }
            userManager = this.mUserManager;
        }
        return userManager;
    }

    @Override // android.content.pm.PackageManager
    public int getUserId() {
        return this.mContext.getUserId();
    }

    @Override // android.content.pm.PackageManager
    public PackageInfo getPackageInfo(String str, int i) throws PackageManager.NameNotFoundException {
        return getPackageInfoAsUser(str, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int i) throws PackageManager.NameNotFoundException {
        try {
            PackageInfo packageInfoVersioned = this.mPM.getPackageInfoVersioned(versionedPackage, i, this.mContext.getUserId());
            if (packageInfoVersioned != null) {
                return packageInfoVersioned;
            }
            throw new PackageManager.NameNotFoundException(versionedPackage.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public PackageInfo getPackageInfoAsUser(String str, int i, int i2) throws PackageManager.NameNotFoundException {
        try {
            PackageInfo packageInfo = this.mPM.getPackageInfo(str, i, i2);
            if (packageInfo != null) {
                return packageInfo;
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] currentToCanonicalPackageNames(String[] strArr) {
        try {
            return this.mPM.currentToCanonicalPackageNames(strArr);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] canonicalToCurrentPackageNames(String[] strArr) {
        try {
            return this.mPM.canonicalToCurrentPackageNames(strArr);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public Intent getLaunchIntentForPackage(String str) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_INFO);
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentActivities = queryIntentActivities(intent, 0);
        if (listQueryIntentActivities == null || listQueryIntentActivities.size() <= 0) {
            intent.removeCategory(Intent.CATEGORY_INFO);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(str);
            listQueryIntentActivities = queryIntentActivities(intent, 0);
        }
        if (listQueryIntentActivities == null || listQueryIntentActivities.size() <= 0) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setFlags(268435456);
        intent2.setClassName(listQueryIntentActivities.get(0).activityInfo.packageName, listQueryIntentActivities.get(0).activityInfo.name);
        return intent2;
    }

    @Override // android.content.pm.PackageManager
    public Intent getLeanbackLaunchIntentForPackage(String str) {
        return getLaunchIntentForPackageAndCategory(str, "android.intent.category.LEANBACK_LAUNCHER");
    }

    @Override // android.content.pm.PackageManager
    public Intent getCarLaunchIntentForPackage(String str) {
        return getLaunchIntentForPackageAndCategory(str, Intent.CATEGORY_CAR_LAUNCHER);
    }

    private Intent getLaunchIntentForPackageAndCategory(String str, String str2) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(str2);
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentActivities = queryIntentActivities(intent, 0);
        if (listQueryIntentActivities == null || listQueryIntentActivities.size() <= 0) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setFlags(268435456);
        intent2.setClassName(listQueryIntentActivities.get(0).activityInfo.packageName, listQueryIntentActivities.get(0).activityInfo.name);
        return intent2;
    }

    @Override // android.content.pm.PackageManager
    public int[] getPackageGids(String str) throws PackageManager.NameNotFoundException {
        return getPackageGids(str, 0);
    }

    @Override // android.content.pm.PackageManager
    public int[] getPackageGids(String str, int i) throws PackageManager.NameNotFoundException {
        try {
            int[] packageGids = this.mPM.getPackageGids(str, i, this.mContext.getUserId());
            if (packageGids != null) {
                return packageGids;
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getPackageUid(String str, int i) throws PackageManager.NameNotFoundException {
        return getPackageUidAsUser(str, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public int getPackageUidAsUser(String str, int i) throws PackageManager.NameNotFoundException {
        return getPackageUidAsUser(str, 0, i);
    }

    @Override // android.content.pm.PackageManager
    public int getPackageUidAsUser(String str, int i, int i2) throws PackageManager.NameNotFoundException {
        try {
            int packageUid = this.mPM.getPackageUid(str, i, i2);
            if (packageUid >= 0) {
                return packageUid;
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public PermissionInfo getPermissionInfo(String str, int i) throws PackageManager.NameNotFoundException {
        try {
            PermissionInfo permissionInfo = this.mPM.getPermissionInfo(str, this.mContext.getOpPackageName(), i);
            if (permissionInfo != null) {
                return permissionInfo;
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws PackageManager.NameNotFoundException {
        List<PermissionInfo> list;
        try {
            ParceledListSlice parceledListSliceQueryPermissionsByGroup = this.mPM.queryPermissionsByGroup(str, i);
            if (parceledListSliceQueryPermissionsByGroup == null || (list = parceledListSliceQueryPermissionsByGroup.getList()) == null) {
                throw new PackageManager.NameNotFoundException(str);
            }
            return list;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPermissionReviewModeEnabled() {
        return this.mContext.getResources().getBoolean(R.bool.config_permissionReviewRequired);
    }

    @Override // android.content.pm.PackageManager
    public PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws PackageManager.NameNotFoundException {
        try {
            PermissionGroupInfo permissionGroupInfo = this.mPM.getPermissionGroupInfo(str, i);
            if (permissionGroupInfo != null) {
                return permissionGroupInfo;
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        try {
            ParceledListSlice allPermissionGroups = this.mPM.getAllPermissionGroups(i);
            if (allPermissionGroups == null) {
                return Collections.emptyList();
            }
            return allPermissionGroups.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ApplicationInfo getApplicationInfo(String str, int i) throws PackageManager.NameNotFoundException {
        return getApplicationInfoAsUser(str, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ApplicationInfo getApplicationInfoAsUser(String str, int i, int i2) throws PackageManager.NameNotFoundException {
        try {
            ApplicationInfo applicationInfo = this.mPM.getApplicationInfo(str, i, i2);
            if (applicationInfo != null) {
                return maybeAdjustApplicationInfo(applicationInfo);
            }
            throw new PackageManager.NameNotFoundException(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static ApplicationInfo maybeAdjustApplicationInfo(ApplicationInfo applicationInfo) {
        if (applicationInfo.primaryCpuAbi != null && applicationInfo.secondaryCpuAbi != null) {
            String strVmInstructionSet = VMRuntime.getRuntime().vmInstructionSet();
            String instructionSet = VMRuntime.getInstructionSet(applicationInfo.secondaryCpuAbi);
            String str = SystemProperties.get("ro.dalvik.vm.isa." + instructionSet);
            if (!str.isEmpty()) {
                instructionSet = str;
            }
            if (strVmInstructionSet.equals(instructionSet)) {
                ApplicationInfo applicationInfo2 = new ApplicationInfo(applicationInfo);
                applicationInfo2.nativeLibraryDir = applicationInfo.secondaryNativeLibraryDir;
                return applicationInfo2;
            }
        }
        return applicationInfo;
    }

    @Override // android.content.pm.PackageManager
    public ActivityInfo getActivityInfo(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        try {
            ActivityInfo activityInfo = this.mPM.getActivityInfo(componentName, i, this.mContext.getUserId());
            if (activityInfo != null) {
                return activityInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ActivityInfo getReceiverInfo(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        try {
            ActivityInfo receiverInfo = this.mPM.getReceiverInfo(componentName, i, this.mContext.getUserId());
            if (receiverInfo != null) {
                return receiverInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ServiceInfo getServiceInfo(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo serviceInfo = this.mPM.getServiceInfo(componentName, i, this.mContext.getUserId());
            if (serviceInfo != null) {
                return serviceInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ProviderInfo getProviderInfo(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        try {
            ProviderInfo providerInfo = this.mPM.getProviderInfo(componentName, i, this.mContext.getUserId());
            if (providerInfo != null) {
                return providerInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] getSystemSharedLibraryNames() {
        try {
            return this.mPM.getSystemSharedLibraryNames();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<SharedLibraryInfo> getSharedLibraries(int i) {
        return getSharedLibrariesAsUser(i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<SharedLibraryInfo> getSharedLibrariesAsUser(int i, int i2) {
        try {
            ParceledListSlice sharedLibraries = this.mPM.getSharedLibraries(this.mContext.getOpPackageName(), i, i2);
            if (sharedLibraries == null) {
                return Collections.emptyList();
            }
            return sharedLibraries.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getServicesSystemSharedLibraryPackageName() {
        try {
            return this.mPM.getServicesSystemSharedLibraryPackageName();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getSharedSystemSharedLibraryPackageName() {
        try {
            return this.mPM.getSharedSystemSharedLibraryPackageName();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ChangedPackages getChangedPackages(int i) {
        try {
            return this.mPM.getChangedPackages(i, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public FeatureInfo[] getSystemAvailableFeatures() {
        try {
            ParceledListSlice systemAvailableFeatures = this.mPM.getSystemAvailableFeatures();
            if (systemAvailableFeatures == null) {
                return new FeatureInfo[0];
            }
            List list = systemAvailableFeatures.getList();
            int size = list.size();
            FeatureInfo[] featureInfoArr = new FeatureInfo[size];
            for (int i = 0; i < size; i++) {
                featureInfoArr[i] = (FeatureInfo) list.get(i);
            }
            return featureInfoArr;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean hasSystemFeature(String str) {
        return hasSystemFeature(str, 0);
    }

    @Override // android.content.pm.PackageManager
    public boolean hasSystemFeature(String str, int i) {
        try {
            return this.mPM.hasSystemFeature(str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkPermission(String str, String str2) {
        try {
            return this.mPM.checkPermission(str, str2, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPermissionRevokedByPolicy(String str, String str2) {
        try {
            return this.mPM.isPermissionRevokedByPolicy(str, str2, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getPermissionControllerPackageName() {
        String str;
        synchronized (this.mLock) {
            if (this.mPermissionsControllerPackageName == null) {
                try {
                    this.mPermissionsControllerPackageName = this.mPM.getPermissionControllerPackageName();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            str = this.mPermissionsControllerPackageName;
        }
        return str;
    }

    @Override // android.content.pm.PackageManager
    public boolean addPermission(PermissionInfo permissionInfo) {
        try {
            return this.mPM.addPermission(permissionInfo);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean addPermissionAsync(PermissionInfo permissionInfo) {
        try {
            return this.mPM.addPermissionAsync(permissionInfo);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void removePermission(String str) {
        try {
            this.mPM.removePermission(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void grantRuntimePermission(String str, String str2, UserHandle userHandle) {
        try {
            this.mPM.grantRuntimePermission(str, str2, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void revokeRuntimePermission(String str, String str2, UserHandle userHandle) {
        try {
            this.mPM.revokeRuntimePermission(str, str2, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getPermissionFlags(String str, String str2, UserHandle userHandle) {
        try {
            return this.mPM.getPermissionFlags(str, str2, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void updatePermissionFlags(String str, String str2, int i, int i2, UserHandle userHandle) {
        try {
            this.mPM.updatePermissionFlags(str, str2, i, i2, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean shouldShowRequestPermissionRationale(String str) {
        try {
            return this.mPM.shouldShowRequestPermissionRationale(str, this.mContext.getPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkSignatures(String str, String str2) {
        try {
            return this.mPM.checkSignatures(str, str2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkSignatures(int i, int i2) {
        try {
            return this.mPM.checkUidSignatures(i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean hasSigningCertificate(String str, byte[] bArr, int i) {
        try {
            return this.mPM.hasSigningCertificate(str, bArr, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean hasSigningCertificate(int i, byte[] bArr, int i2) {
        try {
            return this.mPM.hasUidSigningCertificate(i, bArr, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] getPackagesForUid(int i) {
        try {
            return this.mPM.getPackagesForUid(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getNameForUid(int i) {
        try {
            return this.mPM.getNameForUid(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] getNamesForUids(int[] iArr) {
        try {
            return this.mPM.getNamesForUids(iArr);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getUidForSharedUser(String str) throws PackageManager.NameNotFoundException {
        try {
            int uidForSharedUser = this.mPM.getUidForSharedUser(str);
            if (uidForSharedUser != -1) {
                return uidForSharedUser;
            }
            throw new PackageManager.NameNotFoundException("No shared userid for user:" + str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getInstalledPackages(int i) {
        return getInstalledPackagesAsUser(i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getInstalledPackagesAsUser(int i, int i2) {
        try {
            ParceledListSlice installedPackages = this.mPM.getInstalledPackages(i, i2);
            if (installedPackages == null) {
                return Collections.emptyList();
            }
            return installedPackages.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getPackagesHoldingPermissions(String[] strArr, int i) {
        try {
            ParceledListSlice packagesHoldingPermissions = this.mPM.getPackagesHoldingPermissions(strArr, i, this.mContext.getUserId());
            if (packagesHoldingPermissions == null) {
                return Collections.emptyList();
            }
            return packagesHoldingPermissions.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ApplicationInfo> getInstalledApplications(int i) {
        return getInstalledApplicationsAsUser(i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ApplicationInfo> getInstalledApplicationsAsUser(int i, int i2) {
        try {
            ParceledListSlice installedApplications = this.mPM.getInstalledApplications(i, i2);
            if (installedApplications == null) {
                return Collections.emptyList();
            }
            return installedApplications.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<InstantAppInfo> getInstantApps() {
        try {
            ParceledListSlice instantApps = this.mPM.getInstantApps(this.mContext.getUserId());
            if (instantApps != null) {
                return instantApps.getList();
            }
            return Collections.emptyList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public Drawable getInstantAppIcon(String str) {
        try {
            Bitmap instantAppIcon = this.mPM.getInstantAppIcon(str, this.mContext.getUserId());
            if (instantAppIcon != null) {
                return new BitmapDrawable((Resources) null, instantAppIcon);
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isInstantApp() {
        return isInstantApp(this.mContext.getPackageName());
    }

    @Override // android.content.pm.PackageManager
    public boolean isInstantApp(String str) {
        try {
            return this.mPM.isInstantApp(str, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getInstantAppCookieMaxBytes() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.EPHEMERAL_COOKIE_MAX_SIZE_BYTES, 16384);
    }

    @Override // android.content.pm.PackageManager
    public int getInstantAppCookieMaxSize() {
        return getInstantAppCookieMaxBytes();
    }

    @Override // android.content.pm.PackageManager
    public byte[] getInstantAppCookie() {
        try {
            byte[] instantAppCookie = this.mPM.getInstantAppCookie(this.mContext.getPackageName(), this.mContext.getUserId());
            return instantAppCookie != null ? instantAppCookie : EmptyArray.BYTE;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearInstantAppCookie() {
        updateInstantAppCookie(null);
    }

    @Override // android.content.pm.PackageManager
    public void updateInstantAppCookie(byte[] bArr) {
        if (bArr != null && bArr.length > getInstantAppCookieMaxBytes()) {
            throw new IllegalArgumentException("instant cookie longer than " + getInstantAppCookieMaxBytes());
        }
        try {
            this.mPM.setInstantAppCookie(this.mContext.getPackageName(), bArr, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean setInstantAppCookie(byte[] bArr) {
        try {
            return this.mPM.setInstantAppCookie(this.mContext.getPackageName(), bArr, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveActivity(Intent intent, int i) {
        return resolveActivityAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveActivityAsUser(Intent intent, int i, int i2) {
        try {
            return this.mPM.resolveIntent(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivities(Intent intent, int i) {
        return queryIntentActivitiesAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent, int i, int i2) {
        try {
            ParceledListSlice parceledListSliceQueryIntentActivities = this.mPM.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
            if (parceledListSliceQueryIntentActivities == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryIntentActivities.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, Intent intent, int i) {
        String strResolveTypeIfNeeded;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String[] strArr = null;
        if (intentArr != null) {
            int length = intentArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                Intent intent2 = intentArr[i2];
                if (intent2 != null && (strResolveTypeIfNeeded = intent2.resolveTypeIfNeeded(contentResolver)) != null) {
                    if (strArr == null) {
                        strArr = new String[length];
                    }
                    strArr[i2] = strResolveTypeIfNeeded;
                }
            }
        }
        try {
            ParceledListSlice parceledListSliceQueryIntentActivityOptions = this.mPM.queryIntentActivityOptions(componentName, intentArr, strArr, intent, intent.resolveTypeIfNeeded(contentResolver), i, this.mContext.getUserId());
            if (parceledListSliceQueryIntentActivityOptions == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryIntentActivityOptions.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryBroadcastReceiversAsUser(Intent intent, int i, int i2) {
        try {
            ParceledListSlice parceledListSliceQueryIntentReceivers = this.mPM.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
            if (parceledListSliceQueryIntentReceivers == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryIntentReceivers.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int i) {
        return queryBroadcastReceiversAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveServiceAsUser(Intent intent, int i, int i2) {
        try {
            return this.mPM.resolveService(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveService(Intent intent, int i) {
        return resolveServiceAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentServicesAsUser(Intent intent, int i, int i2) {
        try {
            ParceledListSlice parceledListSliceQueryIntentServices = this.mPM.queryIntentServices(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
            if (parceledListSliceQueryIntentServices == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryIntentServices.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentServices(Intent intent, int i) {
        return queryIntentServicesAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentContentProvidersAsUser(Intent intent, int i, int i2) {
        try {
            ParceledListSlice parceledListSliceQueryIntentContentProviders = this.mPM.queryIntentContentProviders(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i, i2);
            if (parceledListSliceQueryIntentContentProviders == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryIntentContentProviders.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int i) {
        return queryIntentContentProvidersAsUser(intent, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ProviderInfo resolveContentProvider(String str, int i) {
        return resolveContentProviderAsUser(str, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ProviderInfo resolveContentProviderAsUser(String str, int i, int i2) {
        try {
            return this.mPM.resolveContentProvider(str, i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ProviderInfo> queryContentProviders(String str, int i, int i2) {
        return queryContentProviders(str, i, i2, null);
    }

    @Override // android.content.pm.PackageManager
    public List<ProviderInfo> queryContentProviders(String str, int i, int i2, String str2) {
        try {
            ParceledListSlice parceledListSliceQueryContentProviders = this.mPM.queryContentProviders(str, i, i2, str2);
            return parceledListSliceQueryContentProviders != null ? parceledListSliceQueryContentProviders.getList() : Collections.emptyList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public InstrumentationInfo getInstrumentationInfo(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        try {
            InstrumentationInfo instrumentationInfo = this.mPM.getInstrumentationInfo(componentName, i);
            if (instrumentationInfo != null) {
                return instrumentationInfo;
            }
            throw new PackageManager.NameNotFoundException(componentName.toString());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<InstrumentationInfo> queryInstrumentation(String str, int i) {
        try {
            ParceledListSlice parceledListSliceQueryInstrumentation = this.mPM.queryInstrumentation(str, i);
            if (parceledListSliceQueryInstrumentation == null) {
                return Collections.emptyList();
            }
            return parceledListSliceQueryInstrumentation.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public Drawable getDrawable(String str, int i, ApplicationInfo applicationInfo) {
        ResourceName resourceName = new ResourceName(str, i);
        Drawable cachedIcon = getCachedIcon(resourceName);
        if (cachedIcon != null) {
            return cachedIcon;
        }
        if (applicationInfo == null) {
            try {
                applicationInfo = getApplicationInfo(str, 1024);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        if (i != 0) {
            try {
                Drawable drawable = getResourcesForApplication(applicationInfo).getDrawable(i, null);
                if (drawable != null) {
                    putCachedIcon(resourceName, drawable);
                }
                return drawable;
            } catch (PackageManager.NameNotFoundException unused2) {
                Log.w("PackageManager", "Failure retrieving resources for " + applicationInfo.packageName);
            } catch (Resources.NotFoundException e) {
                Log.w("PackageManager", "Failure retrieving resources for " + applicationInfo.packageName + ": " + e.getMessage());
            } catch (Exception e2) {
                Log.w("PackageManager", "Failure retrieving icon 0x" + Integer.toHexString(i) + " in package " + str, e2);
            }
        }
        return null;
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityIcon(ComponentName componentName) throws PackageManager.NameNotFoundException {
        return getActivityInfo(componentName, 1024).loadIcon(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityIcon(Intent intent) throws PackageManager.NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityIcon(intent.getComponent());
        }
        ResolveInfo resolveInfoResolveActivity = resolveActivity(intent, 65536);
        if (resolveInfoResolveActivity != null) {
            return resolveInfoResolveActivity.activityInfo.loadIcon(this);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getDefaultActivityIcon() {
        return Resources.getSystem().getDrawable(17301651);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationIcon(ApplicationInfo applicationInfo) {
        return applicationInfo.loadIcon(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationIcon(String str) throws PackageManager.NameNotFoundException {
        return getApplicationIcon(getApplicationInfo(str, 1024));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityBanner(ComponentName componentName) throws PackageManager.NameNotFoundException {
        return getActivityInfo(componentName, 1024).loadBanner(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityBanner(Intent intent) throws PackageManager.NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityBanner(intent.getComponent());
        }
        ResolveInfo resolveInfoResolveActivity = resolveActivity(intent, 65536);
        if (resolveInfoResolveActivity != null) {
            return resolveInfoResolveActivity.activityInfo.loadBanner(this);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationBanner(ApplicationInfo applicationInfo) {
        return applicationInfo.loadBanner(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationBanner(String str) throws PackageManager.NameNotFoundException {
        return getApplicationBanner(getApplicationInfo(str, 1024));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityLogo(ComponentName componentName) throws PackageManager.NameNotFoundException {
        return getActivityInfo(componentName, 1024).loadLogo(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityLogo(Intent intent) throws PackageManager.NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityLogo(intent.getComponent());
        }
        ResolveInfo resolveInfoResolveActivity = resolveActivity(intent, 65536);
        if (resolveInfoResolveActivity != null) {
            return resolveInfoResolveActivity.activityInfo.loadLogo(this);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationLogo(ApplicationInfo applicationInfo) {
        return applicationInfo.loadLogo(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationLogo(String str) throws PackageManager.NameNotFoundException {
        return getApplicationLogo(getApplicationInfo(str, 1024));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getUserBadgedIcon(Drawable drawable, UserHandle userHandle) {
        return !isManagedProfile(userHandle.getIdentifier()) ? drawable : getBadgedDrawable(drawable, new LauncherIcons(this.mContext).getBadgeDrawable(R.drawable.ic_corp_icon_badge_case, getUserBadgeColor(userHandle)), null, true);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle userHandle, Rect rect, int i) {
        Drawable userBadgeForDensity = getUserBadgeForDensity(userHandle, i);
        return userBadgeForDensity == null ? drawable : getBadgedDrawable(drawable, userBadgeForDensity, rect, true);
    }

    private int getUserBadgeColor(UserHandle userHandle) {
        return IconDrawableFactory.getUserBadgeColor(getUserManager(), userHandle.getIdentifier());
    }

    @Override // android.content.pm.PackageManager
    public Drawable getUserBadgeForDensity(UserHandle userHandle, int i) {
        Drawable managedProfileIconForDensity = getManagedProfileIconForDensity(userHandle, R.drawable.ic_corp_badge_color, i);
        if (managedProfileIconForDensity == null) {
            return null;
        }
        Drawable drawableForDensity = getDrawableForDensity(R.drawable.ic_corp_badge_case, i);
        drawableForDensity.setTint(getUserBadgeColor(userHandle));
        return new LayerDrawable(new Drawable[]{managedProfileIconForDensity, drawableForDensity});
    }

    @Override // android.content.pm.PackageManager
    public Drawable getUserBadgeForDensityNoBackground(UserHandle userHandle, int i) {
        Drawable managedProfileIconForDensity = getManagedProfileIconForDensity(userHandle, R.drawable.ic_corp_badge_no_background, i);
        if (managedProfileIconForDensity != null) {
            managedProfileIconForDensity.setTint(getUserBadgeColor(userHandle));
        }
        return managedProfileIconForDensity;
    }

    private Drawable getDrawableForDensity(int i, int i2) {
        if (i2 <= 0) {
            i2 = this.mContext.getResources().getDisplayMetrics().densityDpi;
        }
        return Resources.getSystem().getDrawableForDensity(i, i2);
    }

    private Drawable getManagedProfileIconForDensity(UserHandle userHandle, int i, int i2) {
        if (isManagedProfile(userHandle.getIdentifier())) {
            return getDrawableForDensity(i, i2);
        }
        return null;
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getUserBadgedLabel(CharSequence charSequence, UserHandle userHandle) {
        if (!isManagedProfile(userHandle.getIdentifier())) {
            return charSequence;
        }
        int managedProfileBadge = getUserManager().getManagedProfileBadge(userHandle.getIdentifier());
        int[] iArr = CORP_BADGE_LABEL_RES_ID;
        return Resources.getSystem().getString(iArr[managedProfileBadge % iArr.length], charSequence);
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForActivity(ComponentName componentName) throws PackageManager.NameNotFoundException {
        return getResourcesForApplication(getActivityInfo(componentName, 1024).applicationInfo);
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplication(ApplicationInfo applicationInfo) throws PackageManager.NameNotFoundException {
        if (applicationInfo.packageName.equals(StorageManager.UUID_SYSTEM)) {
            return this.mContext.mMainThread.getSystemUiContext().getResources();
        }
        boolean z = applicationInfo.uid == Process.myUid();
        Resources topLevelResources = this.mContext.mMainThread.getTopLevelResources(z ? applicationInfo.sourceDir : applicationInfo.publicSourceDir, z ? applicationInfo.splitSourceDirs : applicationInfo.splitPublicSourceDirs, applicationInfo.resourceDirs, applicationInfo.sharedLibraryFiles, 0, this.mContext.mPackageInfo);
        if (topLevelResources != null) {
            return topLevelResources;
        }
        throw new PackageManager.NameNotFoundException("Unable to open " + applicationInfo.publicSourceDir);
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplication(String str) throws PackageManager.NameNotFoundException {
        return getResourcesForApplication(getApplicationInfo(str, 1024));
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplicationAsUser(String str, int i) throws PackageManager.NameNotFoundException {
        if (i < 0) {
            throw new IllegalArgumentException("Call does not support special user #" + i);
        }
        if (StorageManager.UUID_SYSTEM.equals(str)) {
            return this.mContext.mMainThread.getSystemUiContext().getResources();
        }
        try {
            ApplicationInfo applicationInfo = this.mPM.getApplicationInfo(str, 1024, i);
            if (applicationInfo != null) {
                return getResourcesForApplication(applicationInfo);
            }
            throw new PackageManager.NameNotFoundException("Package " + str + " doesn't exist");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isSafeMode() {
        try {
            if (this.mCachedSafeMode < 0) {
                this.mCachedSafeMode = this.mPM.isSafeMode() ? 1 : 0;
            }
            return this.mCachedSafeMode != 0;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void addOnPermissionsChangeListener(PackageManager.OnPermissionsChangedListener onPermissionsChangedListener) {
        synchronized (this.mPermissionListeners) {
            if (this.mPermissionListeners.get(onPermissionsChangedListener) != null) {
                return;
            }
            OnPermissionsChangeListenerDelegate onPermissionsChangeListenerDelegate = new OnPermissionsChangeListenerDelegate(onPermissionsChangedListener, Looper.getMainLooper());
            try {
                this.mPM.addOnPermissionsChangeListener(onPermissionsChangeListenerDelegate);
                this.mPermissionListeners.put(onPermissionsChangedListener, onPermissionsChangeListenerDelegate);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Override // android.content.pm.PackageManager
    public void removeOnPermissionsChangeListener(PackageManager.OnPermissionsChangedListener onPermissionsChangedListener) {
        synchronized (this.mPermissionListeners) {
            IOnPermissionsChangeListener iOnPermissionsChangeListener = this.mPermissionListeners.get(onPermissionsChangedListener);
            if (iOnPermissionsChangeListener != null) {
                try {
                    this.mPM.removeOnPermissionsChangeListener(iOnPermissionsChangeListener);
                    this.mPermissionListeners.remove(onPermissionsChangedListener);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    static void configurationChanged() {
        synchronized (sSync) {
            sIconCache.clear();
            sStringCache.clear();
        }
    }

    protected ApplicationPackageManager(ContextImpl contextImpl, IPackageManager iPackageManager) {
        this.mContext = contextImpl;
        this.mPM = iPackageManager;
    }

    private Drawable getCachedIcon(ResourceName resourceName) {
        synchronized (sSync) {
            WeakReference<Drawable.ConstantState> weakReference = sIconCache.get(resourceName);
            if (weakReference != null) {
                Drawable.ConstantState constantState = weakReference.get();
                if (constantState != null) {
                    return constantState.newDrawable();
                }
                sIconCache.remove(resourceName);
            }
            return null;
        }
    }

    private void putCachedIcon(ResourceName resourceName, Drawable drawable) {
        synchronized (sSync) {
            sIconCache.put(resourceName, new WeakReference<>(drawable.getConstantState()));
        }
    }

    static void handlePackageBroadcast(int i, String[] strArr, boolean z) {
        boolean z2 = i == 1;
        if (strArr == null || strArr.length <= 0) {
            return;
        }
        boolean z3 = false;
        for (String str : strArr) {
            synchronized (sSync) {
                for (int size = sIconCache.size() - 1; size >= 0; size--) {
                    if (sIconCache.keyAt(size).packageName.equals(str)) {
                        sIconCache.removeAt(size);
                        z3 = true;
                    }
                }
                for (int size2 = sStringCache.size() - 1; size2 >= 0; size2--) {
                    if (sStringCache.keyAt(size2).packageName.equals(str)) {
                        sStringCache.removeAt(size2);
                        z3 = true;
                    }
                }
            }
        }
        if (z3 || z) {
            if (z2) {
                Runtime.getRuntime().gc();
            } else {
                ActivityThread.currentActivityThread().scheduleGcIdler();
            }
        }
    }

    private static final class ResourceName {
        final int iconId;
        final String packageName;

        ResourceName(String str, int i) {
            this.packageName = str;
            this.iconId = i;
        }

        ResourceName(ApplicationInfo applicationInfo, int i) {
            this(applicationInfo.packageName, i);
        }

        ResourceName(ComponentInfo componentInfo, int i) {
            this(componentInfo.applicationInfo.packageName, i);
        }

        ResourceName(ResolveInfo resolveInfo, int i) {
            this(resolveInfo.activityInfo.applicationInfo.packageName, i);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ResourceName resourceName = (ResourceName) obj;
            if (this.iconId != resourceName.iconId) {
                return false;
            }
            String str = this.packageName;
            if (str != null) {
                if (str.equals(resourceName.packageName)) {
                    return true;
                }
            } else if (resourceName.packageName == null) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.packageName.hashCode() * 31) + this.iconId;
        }

        public String toString() {
            return "{ResourceName " + this.packageName + " / " + this.iconId + "}";
        }
    }

    private CharSequence getCachedString(ResourceName resourceName) {
        synchronized (sSync) {
            WeakReference<CharSequence> weakReference = sStringCache.get(resourceName);
            if (weakReference != null) {
                CharSequence charSequence = weakReference.get();
                if (charSequence != null) {
                    return charSequence;
                }
                sStringCache.remove(resourceName);
            }
            return null;
        }
    }

    private void putCachedString(ResourceName resourceName, CharSequence charSequence) {
        synchronized (sSync) {
            sStringCache.put(resourceName, new WeakReference<>(charSequence));
        }
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getText(String str, int i, ApplicationInfo applicationInfo) {
        ResourceName resourceName = new ResourceName(str, i);
        CharSequence cachedString = getCachedString(resourceName);
        if (cachedString != null) {
            return cachedString;
        }
        if (applicationInfo == null) {
            try {
                applicationInfo = getApplicationInfo(str, 1024);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        try {
            CharSequence text = getResourcesForApplication(applicationInfo).getText(i);
            putCachedString(resourceName, text);
            return text;
        } catch (PackageManager.NameNotFoundException unused2) {
            Log.w("PackageManager", "Failure retrieving resources for " + applicationInfo.packageName);
            return null;
        } catch (RuntimeException e) {
            Log.w("PackageManager", "Failure retrieving text 0x" + Integer.toHexString(i) + " in package " + str, e);
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public XmlResourceParser getXml(String str, int i, ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            try {
                applicationInfo = getApplicationInfo(str, 1024);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        try {
            return getResourcesForApplication(applicationInfo).getXml(i);
        } catch (PackageManager.NameNotFoundException unused2) {
            Log.w("PackageManager", "Failure retrieving resources for " + applicationInfo.packageName);
            return null;
        } catch (RuntimeException e) {
            Log.w("PackageManager", "Failure retrieving xml 0x" + Integer.toHexString(i) + " in package " + str, e);
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getApplicationLabel(ApplicationInfo applicationInfo) {
        return applicationInfo.loadLabel(this);
    }

    @Override // android.content.pm.PackageManager
    public int installExistingPackage(String str) throws PackageManager.NameNotFoundException {
        return installExistingPackage(str, 0);
    }

    @Override // android.content.pm.PackageManager
    public int installExistingPackage(String str, int i) throws PackageManager.NameNotFoundException {
        return installExistingPackageAsUser(str, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public int installExistingPackageAsUser(String str, int i) throws PackageManager.NameNotFoundException {
        return installExistingPackageAsUser(str, 0, i);
    }

    private int installExistingPackageAsUser(String str, int i, int i2) throws PackageManager.NameNotFoundException {
        try {
            int iInstallExistingPackageAsUser = this.mPM.installExistingPackageAsUser(str, i2, 0, i);
            if (iInstallExistingPackageAsUser != -3) {
                return iInstallExistingPackageAsUser;
            }
            throw new PackageManager.NameNotFoundException("Package " + str + " doesn't exist");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void verifyPendingInstall(int i, int i2) {
        try {
            this.mPM.verifyPendingInstall(i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void extendVerificationTimeout(int i, int i2, long j) {
        try {
            this.mPM.extendVerificationTimeout(i, i2, j);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void verifyIntentFilter(int i, int i2, List<String> list) {
        try {
            this.mPM.verifyIntentFilter(i, i2, list);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getIntentVerificationStatusAsUser(String str, int i) {
        try {
            return this.mPM.getIntentVerificationStatus(str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean updateIntentVerificationStatusAsUser(String str, int i, int i2) {
        try {
            return this.mPM.updateIntentVerificationStatus(str, i, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<IntentFilterVerificationInfo> getIntentFilterVerifications(String str) {
        try {
            ParceledListSlice intentFilterVerifications = this.mPM.getIntentFilterVerifications(str);
            if (intentFilterVerifications == null) {
                return Collections.emptyList();
            }
            return intentFilterVerifications.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public List<IntentFilter> getAllIntentFilters(String str) {
        try {
            ParceledListSlice allIntentFilters = this.mPM.getAllIntentFilters(str);
            if (allIntentFilters == null) {
                return Collections.emptyList();
            }
            return allIntentFilters.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getDefaultBrowserPackageNameAsUser(int i) {
        try {
            return this.mPM.getDefaultBrowserPackageName(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean setDefaultBrowserPackageNameAsUser(String str, int i) {
        try {
            return this.mPM.setDefaultBrowserPackageName(str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void setInstallerPackageName(String str, String str2) {
        try {
            this.mPM.setInstallerPackageName(str, str2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void setUpdateAvailable(String str, boolean z) {
        try {
            this.mPM.setUpdateAvailable(str, z);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getInstallerPackageName(String str) {
        try {
            return this.mPM.getInstallerPackageName(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getMoveStatus(int i) {
        try {
            return this.mPM.getMoveStatus(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void registerMoveCallback(PackageManager.MoveCallback moveCallback, Handler handler) {
        synchronized (this.mDelegates) {
            MoveCallbackDelegate moveCallbackDelegate = new MoveCallbackDelegate(moveCallback, handler.getLooper());
            try {
                this.mPM.registerMoveCallback(moveCallbackDelegate);
                this.mDelegates.add(moveCallbackDelegate);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Override // android.content.pm.PackageManager
    public void unregisterMoveCallback(PackageManager.MoveCallback moveCallback) {
        synchronized (this.mDelegates) {
            Iterator<MoveCallbackDelegate> it = this.mDelegates.iterator();
            while (it.hasNext()) {
                MoveCallbackDelegate next = it.next();
                if (next.mCallback == moveCallback) {
                    try {
                        this.mPM.unregisterMoveCallback(next);
                        it.remove();
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    @Override // android.content.pm.PackageManager
    public int movePackage(String str, VolumeInfo volumeInfo) {
        String str2;
        try {
            if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(volumeInfo.id)) {
                str2 = StorageManager.UUID_PRIVATE_INTERNAL;
            } else {
                str2 = volumeInfo.isPrimaryPhysical() ? StorageManager.UUID_PRIMARY_PHYSICAL : (String) Preconditions.checkNotNull(volumeInfo.fsUuid);
            }
            return this.mPM.movePackage(str, str2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public VolumeInfo getPackageCurrentVolume(ApplicationInfo applicationInfo) {
        return getPackageCurrentVolume(applicationInfo, (StorageManager) this.mContext.getSystemService(StorageManager.class));
    }

    protected VolumeInfo getPackageCurrentVolume(ApplicationInfo applicationInfo, StorageManager storageManager) {
        if (applicationInfo.isInternal()) {
            return storageManager.findVolumeById(VolumeInfo.ID_PRIVATE_INTERNAL);
        }
        if (applicationInfo.isExternalAsec()) {
            return storageManager.getPrimaryPhysicalVolume();
        }
        return storageManager.findVolumeByUuid(applicationInfo.volumeUuid);
    }

    @Override // android.content.pm.PackageManager
    public List<VolumeInfo> getPackageCandidateVolumes(ApplicationInfo applicationInfo) {
        return getPackageCandidateVolumes(applicationInfo, (StorageManager) this.mContext.getSystemService(StorageManager.class), this.mPM);
    }

    protected List<VolumeInfo> getPackageCandidateVolumes(ApplicationInfo applicationInfo, StorageManager storageManager, IPackageManager iPackageManager) {
        VolumeInfo packageCurrentVolume = getPackageCurrentVolume(applicationInfo, storageManager);
        List<VolumeInfo> volumes = storageManager.getVolumes();
        ArrayList arrayList = new ArrayList();
        for (VolumeInfo volumeInfo : volumes) {
            if (Objects.equals(volumeInfo, packageCurrentVolume) || isPackageCandidateVolume(this.mContext, applicationInfo, volumeInfo, iPackageManager)) {
                arrayList.add(volumeInfo);
            }
        }
        return arrayList;
    }

    protected boolean isForceAllowOnExternal(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.FORCE_ALLOW_ON_EXTERNAL, 0) != 0;
    }

    protected boolean isAllow3rdPartyOnInternal(Context context) {
        return context.getResources().getBoolean(R.bool.config_allow3rdPartyAppOnInternal);
    }

    private boolean isPackageCandidateVolume(ContextImpl contextImpl, ApplicationInfo applicationInfo, VolumeInfo volumeInfo, IPackageManager iPackageManager) {
        boolean zIsForceAllowOnExternal = isForceAllowOnExternal(contextImpl);
        if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(volumeInfo.getId())) {
            return applicationInfo.isSystemApp() || isAllow3rdPartyOnInternal(contextImpl);
        }
        if (applicationInfo.isSystemApp()) {
            return false;
        }
        if ((!zIsForceAllowOnExternal && (applicationInfo.installLocation == 1 || applicationInfo.installLocation == -1)) || !volumeInfo.isMountedWritable()) {
            return false;
        }
        if (volumeInfo.isPrimaryPhysical()) {
            return applicationInfo.isInternal();
        }
        try {
            return !iPackageManager.isPackageDeviceAdminOnAnyUser(applicationInfo.packageName) && volumeInfo.getType() == 1;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int movePrimaryStorage(VolumeInfo volumeInfo) {
        String str;
        try {
            if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(volumeInfo.id)) {
                str = StorageManager.UUID_PRIVATE_INTERNAL;
            } else {
                str = volumeInfo.isPrimaryPhysical() ? StorageManager.UUID_PRIMARY_PHYSICAL : (String) Preconditions.checkNotNull(volumeInfo.fsUuid);
            }
            return this.mPM.movePrimaryStorage(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public VolumeInfo getPrimaryStorageCurrentVolume() {
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        return storageManager.findVolumeByQualifiedUuid(storageManager.getPrimaryStorageUuid());
    }

    @Override // android.content.pm.PackageManager
    public List<VolumeInfo> getPrimaryStorageCandidateVolumes() {
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        VolumeInfo primaryStorageCurrentVolume = getPrimaryStorageCurrentVolume();
        List<VolumeInfo> volumes = storageManager.getVolumes();
        ArrayList arrayList = new ArrayList();
        if (Objects.equals(StorageManager.UUID_PRIMARY_PHYSICAL, storageManager.getPrimaryStorageUuid()) && primaryStorageCurrentVolume != null) {
            arrayList.add(primaryStorageCurrentVolume);
        } else {
            for (VolumeInfo volumeInfo : volumes) {
                if (Objects.equals(volumeInfo, primaryStorageCurrentVolume) || isPrimaryStorageCandidateVolume(volumeInfo)) {
                    arrayList.add(volumeInfo);
                }
            }
        }
        return arrayList;
    }

    private static boolean isPrimaryStorageCandidateVolume(VolumeInfo volumeInfo) {
        if (VolumeInfo.ID_PRIVATE_INTERNAL.equals(volumeInfo.getId())) {
            return true;
        }
        return volumeInfo.isMountedWritable() && volumeInfo.getType() == 1;
    }

    @Override // android.content.pm.PackageManager
    public void deletePackage(String str, IPackageDeleteObserver iPackageDeleteObserver, int i) {
        deletePackageAsUser(str, iPackageDeleteObserver, i, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public void deletePackageAsUser(String str, IPackageDeleteObserver iPackageDeleteObserver, int i, int i2) {
        try {
            this.mPM.deletePackageAsUser(str, -1, iPackageDeleteObserver, i2, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver) {
        try {
            this.mPM.clearApplicationUserData(str, iPackageDataObserver, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void deleteApplicationCacheFiles(String str, IPackageDataObserver iPackageDataObserver) {
        try {
            this.mPM.deleteApplicationCacheFiles(str, iPackageDataObserver);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void deleteApplicationCacheFilesAsUser(String str, int i, IPackageDataObserver iPackageDataObserver) {
        try {
            this.mPM.deleteApplicationCacheFilesAsUser(str, i, iPackageDataObserver);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void freeStorageAndNotify(String str, long j, IPackageDataObserver iPackageDataObserver) {
        try {
            this.mPM.freeStorageAndNotify(str, j, 0, iPackageDataObserver);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void freeStorage(String str, long j, IntentSender intentSender) {
        try {
            this.mPM.freeStorage(str, j, 0, intentSender);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] setPackagesSuspended(String[] strArr, boolean z, PersistableBundle persistableBundle, PersistableBundle persistableBundle2, String str) {
        try {
            return this.mPM.setPackagesSuspendedAsUser(strArr, z, persistableBundle, persistableBundle2, str, this.mContext.getOpPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public Bundle getSuspendedPackageAppExtras() {
        try {
            PersistableBundle suspendedPackageAppExtras = this.mPM.getSuspendedPackageAppExtras(this.mContext.getOpPackageName(), this.mContext.getUserId());
            if (suspendedPackageAppExtras != null) {
                return new Bundle(suspendedPackageAppExtras.deepCopy());
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPackageSuspendedForUser(String str, int i) {
        try {
            return this.mPM.isPackageSuspendedForUser(str, i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPackageSuspended(String str) throws PackageManager.NameNotFoundException {
        try {
            return isPackageSuspendedForUser(str, this.mContext.getUserId());
        } catch (IllegalArgumentException unused) {
            throw new PackageManager.NameNotFoundException(str);
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPackageSuspended() {
        return isPackageSuspendedForUser(this.mContext.getOpPackageName(), this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public void setApplicationCategoryHint(String str, int i) {
        try {
            this.mPM.setApplicationCategoryHint(str, i, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void getPackageSizeInfoAsUser(String str, int i, IPackageStatsObserver iPackageStatsObserver) {
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 26) {
            throw new UnsupportedOperationException("Shame on you for calling the hidden API getPackageSizeInfoAsUser(). Shame!");
        }
        if (iPackageStatsObserver != null) {
            Log.d(TAG, "Shame on you for calling the hidden API getPackageSizeInfoAsUser(). Shame!");
            try {
                iPackageStatsObserver.onGetStatsCompleted(null, false);
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // android.content.pm.PackageManager
    public void addPackageToPreferred(String str) {
        Log.w(TAG, "addPackageToPreferred() is a no-op");
    }

    @Override // android.content.pm.PackageManager
    public void removePackageFromPreferred(String str) {
        Log.w(TAG, "removePackageFromPreferred() is a no-op");
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getPreferredPackages(int i) {
        Log.w(TAG, "getPreferredPackages() is a no-op");
        return Collections.emptyList();
    }

    @Override // android.content.pm.PackageManager
    public void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName) {
        try {
            this.mPM.addPreferredActivity(intentFilter, i, componentNameArr, componentName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void addPreferredActivityAsUser(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) {
        try {
            this.mPM.addPreferredActivity(intentFilter, i, componentNameArr, componentName, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName) {
        try {
            this.mPM.replacePreferredActivity(intentFilter, i, componentNameArr, componentName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void replacePreferredActivityAsUser(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) {
        try {
            this.mPM.replacePreferredActivity(intentFilter, i, componentNameArr, componentName, i2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearPackagePreferredActivities(String str) {
        try {
            this.mPM.clearPackagePreferredActivities(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str) {
        try {
            return this.mPM.getPreferredActivities(list, list2, str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public ComponentName getHomeActivities(List<ResolveInfo> list) {
        try {
            return this.mPM.getHomeActivities(list);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void setComponentEnabledSetting(ComponentName componentName, int i, int i2) {
        try {
            this.mPM.setComponentEnabledSetting(componentName, i, i2, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getComponentEnabledSetting(ComponentName componentName) {
        try {
            return this.mPM.getComponentEnabledSetting(componentName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void setApplicationEnabledSetting(String str, int i, int i2) {
        try {
            this.mPM.setApplicationEnabledSetting(str, i, i2, this.mContext.getUserId(), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public int getApplicationEnabledSetting(String str) {
        try {
            return this.mPM.getApplicationEnabledSetting(str, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void flushPackageRestrictionsAsUser(int i) {
        try {
            this.mPM.flushPackageRestrictionsAsUser(i);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean setApplicationHiddenSettingAsUser(String str, boolean z, UserHandle userHandle) {
        try {
            return this.mPM.setApplicationHiddenSettingAsUser(str, z, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean getApplicationHiddenSettingAsUser(String str, UserHandle userHandle) {
        try {
            return this.mPM.getApplicationHiddenSettingAsUser(str, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public KeySet getKeySetByAlias(String str, String str2) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(str2);
        try {
            return this.mPM.getKeySetByAlias(str, str2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public KeySet getSigningKeySet(String str) {
        Preconditions.checkNotNull(str);
        try {
            return this.mPM.getSigningKeySet(str);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isSignedBy(String str, KeySet keySet) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(keySet);
        try {
            return this.mPM.isPackageSignedByKeySet(str, keySet);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isSignedByExactly(String str, KeySet keySet) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(keySet);
        try {
            return this.mPM.isPackageSignedByKeySetExactly(str, keySet);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public VerifierDeviceIdentity getVerifierDeviceIdentity() {
        try {
            return this.mPM.getVerifierDeviceIdentity();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isUpgrade() {
        try {
            return this.mPM.isUpgrade();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public PackageInstaller getPackageInstaller() {
        PackageInstaller packageInstaller;
        synchronized (this.mLock) {
            if (this.mInstaller == null) {
                try {
                    this.mInstaller = new PackageInstaller(this.mPM.getPackageInstaller(), this.mContext.getPackageName(), this.mContext.getUserId());
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            packageInstaller = this.mInstaller;
        }
        return packageInstaller;
    }

    @Override // android.content.pm.PackageManager
    public boolean isPackageAvailable(String str) {
        try {
            return this.mPM.isPackageAvailable(str, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void addCrossProfileIntentFilter(IntentFilter intentFilter, int i, int i2, int i3) {
        try {
            this.mPM.addCrossProfileIntentFilter(intentFilter, this.mContext.getOpPackageName(), i, i2, i3);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearCrossProfileIntentFilters(int i) {
        try {
            this.mPM.clearCrossProfileIntentFilters(i, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.content.pm.PackageManager
    public Drawable loadItemIcon(PackageItemInfo packageItemInfo, ApplicationInfo applicationInfo) {
        Drawable drawableLoadUnbadgedItemIcon = loadUnbadgedItemIcon(packageItemInfo, applicationInfo);
        return packageItemInfo.showUserIcon != -10000 ? drawableLoadUnbadgedItemIcon : getUserBadgedIcon(drawableLoadUnbadgedItemIcon, new UserHandle(this.mContext.getUserId()));
    }

    @Override // android.content.pm.PackageManager
    public Drawable loadUnbadgedItemIcon(PackageItemInfo packageItemInfo, ApplicationInfo applicationInfo) {
        if (packageItemInfo.showUserIcon != -10000) {
            Bitmap userIcon = getUserManager().getUserIcon(packageItemInfo.showUserIcon);
            if (userIcon == null) {
                return UserIcons.getDefaultUserIcon(this.mContext.getResources(), packageItemInfo.showUserIcon, false);
            }
            return new BitmapDrawable(userIcon);
        }
        Drawable drawable = packageItemInfo.packageName != null ? getDrawable(packageItemInfo.packageName, packageItemInfo.icon, applicationInfo) : null;
        return drawable == null ? packageItemInfo.loadDefaultIcon(this) : drawable;
    }

    private Drawable getBadgedDrawable(Drawable drawable, Drawable drawable2, Rect rect, boolean z) {
        Bitmap bitmapCreateBitmap;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        boolean z2 = z && (drawable instanceof BitmapDrawable) && ((BitmapDrawable) drawable).getBitmap().isMutable();
        if (z2) {
            bitmapCreateBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmapCreateBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        if (!z2) {
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            drawable.draw(canvas);
        }
        if (rect != null) {
            if (rect.left < 0 || rect.top < 0 || rect.width() > intrinsicWidth || rect.height() > intrinsicHeight) {
                throw new IllegalArgumentException("Badge location " + rect + " not in badged drawable bounds " + new Rect(0, 0, intrinsicWidth, intrinsicHeight));
            }
            drawable2.setBounds(0, 0, rect.width(), rect.height());
            canvas.save();
            canvas.translate(rect.left, rect.top);
            drawable2.draw(canvas);
            canvas.restore();
        } else {
            drawable2.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            drawable2.draw(canvas);
        }
        if (z2) {
            return drawable;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mContext.getResources(), bitmapCreateBitmap);
        if (drawable instanceof BitmapDrawable) {
            bitmapDrawable.setTargetDensity(((BitmapDrawable) drawable).getBitmap().getDensity());
        }
        return bitmapDrawable;
    }

    private boolean isManagedProfile(int i) {
        return getUserManager().isManagedProfile(i);
    }

    @Override // android.content.pm.PackageManager
    public int getInstallReason(String str, UserHandle userHandle) {
        try {
            return this.mPM.getInstallReason(str, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static class MoveCallbackDelegate extends IPackageMoveObserver.Stub implements Handler.Callback {
        private static final int MSG_CREATED = 1;
        private static final int MSG_STATUS_CHANGED = 2;
        final PackageManager.MoveCallback mCallback;
        final Handler mHandler;

        public MoveCallbackDelegate(PackageManager.MoveCallback moveCallback, Looper looper) {
            this.mCallback = moveCallback;
            this.mHandler = new Handler(looper, this);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                SomeArgs someArgs = (SomeArgs) message.obj;
                this.mCallback.onCreated(someArgs.argi1, (Bundle) someArgs.arg2);
                someArgs.recycle();
                return true;
            }
            if (i != 2) {
                return false;
            }
            SomeArgs someArgs2 = (SomeArgs) message.obj;
            this.mCallback.onStatusChanged(someArgs2.argi1, someArgs2.argi2, ((Long) someArgs2.arg3).longValue());
            someArgs2.recycle();
            return true;
        }

        @Override // android.content.pm.IPackageMoveObserver
        public void onCreated(int i, Bundle bundle) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.argi1 = i;
            someArgsObtain.arg2 = bundle;
            this.mHandler.obtainMessage(1, someArgsObtain).sendToTarget();
        }

        @Override // android.content.pm.IPackageMoveObserver
        public void onStatusChanged(int i, int i2, long j) {
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.argi1 = i;
            someArgsObtain.argi2 = i2;
            someArgsObtain.arg3 = Long.valueOf(j);
            this.mHandler.obtainMessage(2, someArgsObtain).sendToTarget();
        }
    }

    public class OnPermissionsChangeListenerDelegate extends IOnPermissionsChangeListener.Stub implements Handler.Callback {
        private static final int MSG_PERMISSIONS_CHANGED = 1;
        private final Handler mHandler;
        private final PackageManager.OnPermissionsChangedListener mListener;

        public OnPermissionsChangeListenerDelegate(PackageManager.OnPermissionsChangedListener onPermissionsChangedListener, Looper looper) {
            this.mListener = onPermissionsChangedListener;
            this.mHandler = new Handler(looper, this);
        }

        @Override // android.content.pm.IOnPermissionsChangeListener
        public void onPermissionsChanged(int i) {
            this.mHandler.obtainMessage(1, i, 0).sendToTarget();
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            this.mListener.onPermissionsChanged(message.arg1);
            return true;
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean canRequestPackageInstalls() {
        try {
            return this.mPM.canRequestPackageInstalls(this.mContext.getPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public ComponentName getInstantAppResolverSettingsComponent() {
        try {
            return this.mPM.getInstantAppResolverSettingsComponent();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public ComponentName getInstantAppInstallerComponent() {
        try {
            return this.mPM.getInstantAppInstallerComponent();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public String getInstantAppAndroidId(String str, UserHandle userHandle) {
        try {
            return this.mPM.getInstantAppAndroidId(str, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    private static class DexModuleRegisterResult {
        final String dexModulePath;
        final String message;
        final boolean success;

        private DexModuleRegisterResult(String str, boolean z, String str2) {
            this.dexModulePath = str;
            this.success = z;
            this.message = str2;
        }
    }

    private static class DexModuleRegisterCallbackDelegate extends IDexModuleRegisterCallback.Stub implements Handler.Callback {
        private static final int MSG_DEX_MODULE_REGISTERED = 1;
        private final PackageManager.DexModuleRegisterCallback callback;
        private final Handler mHandler = new Handler(Looper.getMainLooper(), this);

        DexModuleRegisterCallbackDelegate(PackageManager.DexModuleRegisterCallback dexModuleRegisterCallback) {
            this.callback = dexModuleRegisterCallback;
        }

        @Override // android.content.pm.IDexModuleRegisterCallback
        public void onDexModuleRegistered(String str, boolean z, String str2) throws RemoteException {
            this.mHandler.obtainMessage(1, new DexModuleRegisterResult(str, z, str2)).sendToTarget();
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            DexModuleRegisterResult dexModuleRegisterResult = (DexModuleRegisterResult) message.obj;
            this.callback.onDexModuleRegistered(dexModuleRegisterResult.dexModulePath, dexModuleRegisterResult.success, dexModuleRegisterResult.message);
            return true;
        }
    }

    @Override // android.content.pm.PackageManager
    public void registerDexModule(String str, PackageManager.DexModuleRegisterCallback dexModuleRegisterCallback) {
        try {
            try {
                this.mPM.registerDexModule(this.mContext.getPackageName(), str, (Os.stat(str).st_mode & OsConstants.S_IROTH) != 0, dexModuleRegisterCallback != null ? new DexModuleRegisterCallbackDelegate(dexModuleRegisterCallback) : null);
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        } catch (ErrnoException e2) {
            dexModuleRegisterCallback.onDexModuleRegistered(str, false, "Could not get stat the module file: " + e2.getMessage());
        }
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getHarmfulAppWarning(String str) {
        try {
            return this.mPM.getHarmfulAppWarning(str, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public void setHarmfulAppWarning(String str, CharSequence charSequence) {
        try {
            this.mPM.setHarmfulAppWarning(str, charSequence, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public ArtManager getArtManager() {
        ArtManager artManager;
        synchronized (this.mLock) {
            if (this.mArtManager == null) {
                try {
                    this.mArtManager = new ArtManager(this.mContext, this.mPM.getArtManager());
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            artManager = this.mArtManager;
        }
        return artManager;
    }

    @Override // android.content.pm.PackageManager
    public String getSystemTextClassifierPackageName() {
        try {
            return this.mPM.getSystemTextClassifierPackageName();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isPackageStateProtected(String str, int i) {
        try {
            return this.mPM.isPackageStateProtected(str, i);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }
}
