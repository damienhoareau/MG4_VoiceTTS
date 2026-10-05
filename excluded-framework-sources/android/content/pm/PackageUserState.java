package android.content.pm;

import android.os.BaseBundle;
import android.os.PersistableBundle;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class PackageUserState {
    public int appLinkGeneration;
    public int categoryHint;
    public long ceDataInode;
    public String dialogMessage;
    public ArraySet<String> disabledComponents;
    public int domainVerificationStatus;
    public int enabled;
    public ArraySet<String> enabledComponents;
    public String harmfulAppWarning;
    public boolean hidden;
    public int installReason;
    public boolean installed;
    public boolean instantApp;
    public String lastDisableAppCaller;
    public boolean notLaunched;
    public String[] overlayPaths;
    public boolean stopped;
    public boolean suspended;
    public PersistableBundle suspendedAppExtras;
    public PersistableBundle suspendedLauncherExtras;
    public String suspendingPackage;
    public boolean virtualPreload;

    public PackageUserState() {
        this.categoryHint = -1;
        this.installed = true;
        this.hidden = false;
        this.suspended = false;
        this.enabled = 0;
        this.domainVerificationStatus = 0;
        this.installReason = 0;
    }

    public PackageUserState(PackageUserState packageUserState) {
        this.categoryHint = -1;
        this.ceDataInode = packageUserState.ceDataInode;
        this.installed = packageUserState.installed;
        this.stopped = packageUserState.stopped;
        this.notLaunched = packageUserState.notLaunched;
        this.hidden = packageUserState.hidden;
        this.suspended = packageUserState.suspended;
        this.suspendingPackage = packageUserState.suspendingPackage;
        this.dialogMessage = packageUserState.dialogMessage;
        this.suspendedAppExtras = packageUserState.suspendedAppExtras;
        this.suspendedLauncherExtras = packageUserState.suspendedLauncherExtras;
        this.instantApp = packageUserState.instantApp;
        this.virtualPreload = packageUserState.virtualPreload;
        this.enabled = packageUserState.enabled;
        this.lastDisableAppCaller = packageUserState.lastDisableAppCaller;
        this.domainVerificationStatus = packageUserState.domainVerificationStatus;
        this.appLinkGeneration = packageUserState.appLinkGeneration;
        this.categoryHint = packageUserState.categoryHint;
        this.installReason = packageUserState.installReason;
        this.disabledComponents = ArrayUtils.cloneOrNull(packageUserState.disabledComponents);
        this.enabledComponents = ArrayUtils.cloneOrNull(packageUserState.enabledComponents);
        String[] strArr = packageUserState.overlayPaths;
        this.overlayPaths = strArr == null ? null : (String[]) Arrays.copyOf(strArr, strArr.length);
        this.harmfulAppWarning = packageUserState.harmfulAppWarning;
    }

    public boolean isAvailable(int i) {
        boolean z = (4194304 & i) != 0;
        boolean z2 = (i & 8192) != 0;
        if (z) {
            return true;
        }
        return this.installed && (!this.hidden || z2);
    }

    public boolean isMatch(ComponentInfo componentInfo, int i) {
        boolean zIsSystemApp = componentInfo.applicationInfo.isSystemApp();
        boolean z = (4202496 & i) != 0;
        if ((!isAvailable(i) && (!zIsSystemApp || !z)) || !isEnabled(componentInfo, i)) {
            return false;
        }
        if ((1048576 & i) == 0 || zIsSystemApp) {
            return ((262144 & i) != 0 && !componentInfo.directBootAware) || ((i & 524288) != 0 && componentInfo.directBootAware);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0019, code lost:
    
        if ((r6 & 32768) == 0) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isEnabled(android.content.pm.ComponentInfo r5, int r6) {
        /*
            r4 = this;
            r0 = r6 & 512(0x200, float:7.17E-43)
            r1 = 1
            if (r0 == 0) goto L6
            return r1
        L6:
            int r0 = r4.enabled
            r2 = 0
            if (r0 == 0) goto L1c
            r3 = 2
            if (r0 == r3) goto L1b
            r3 = 3
            if (r0 == r3) goto L1b
            r3 = 4
            if (r0 == r3) goto L15
            goto L23
        L15:
            r0 = 32768(0x8000, float:4.5918E-41)
            r6 = r6 & r0
            if (r6 != 0) goto L1c
        L1b:
            return r2
        L1c:
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo
            boolean r6 = r6.enabled
            if (r6 != 0) goto L23
            return r2
        L23:
            android.util.ArraySet<java.lang.String> r6 = r4.enabledComponents
            java.lang.String r0 = r5.name
            boolean r6 = com.android.internal.util.ArrayUtils.contains(r6, r0)
            if (r6 == 0) goto L2e
            return r1
        L2e:
            android.util.ArraySet<java.lang.String> r6 = r4.disabledComponents
            java.lang.String r0 = r5.name
            boolean r6 = com.android.internal.util.ArrayUtils.contains(r6, r0)
            if (r6 == 0) goto L39
            return r2
        L39:
            boolean r5 = r5.enabled
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageUserState.isEnabled(android.content.pm.ComponentInfo, int):boolean");
    }

    public final boolean equals(Object obj) {
        boolean z;
        String str;
        String str2;
        String str3;
        if (!(obj instanceof PackageUserState)) {
            return false;
        }
        PackageUserState packageUserState = (PackageUserState) obj;
        if (this.ceDataInode != packageUserState.ceDataInode || this.installed != packageUserState.installed || this.stopped != packageUserState.stopped || this.notLaunched != packageUserState.notLaunched || this.hidden != packageUserState.hidden || (z = this.suspended) != packageUserState.suspended) {
            return false;
        }
        if ((z && ((str3 = this.suspendingPackage) == null || !str3.equals(packageUserState.suspendingPackage) || !Objects.equals(this.dialogMessage, packageUserState.dialogMessage) || !BaseBundle.kindofEquals(this.suspendedAppExtras, packageUserState.suspendedAppExtras) || !BaseBundle.kindofEquals(this.suspendedLauncherExtras, packageUserState.suspendedLauncherExtras))) || this.instantApp != packageUserState.instantApp || this.virtualPreload != packageUserState.virtualPreload || this.enabled != packageUserState.enabled) {
            return false;
        }
        if ((this.lastDisableAppCaller == null && packageUserState.lastDisableAppCaller != null) || (((str = this.lastDisableAppCaller) != null && !str.equals(packageUserState.lastDisableAppCaller)) || this.domainVerificationStatus != packageUserState.domainVerificationStatus || this.appLinkGeneration != packageUserState.appLinkGeneration || this.categoryHint != packageUserState.categoryHint || this.installReason != packageUserState.installReason)) {
            return false;
        }
        if ((this.disabledComponents == null && packageUserState.disabledComponents != null) || (this.disabledComponents != null && packageUserState.disabledComponents == null)) {
            return false;
        }
        ArraySet<String> arraySet = this.disabledComponents;
        if (arraySet != null) {
            if (arraySet.size() != packageUserState.disabledComponents.size()) {
                return false;
            }
            for (int size = this.disabledComponents.size() - 1; size >= 0; size--) {
                if (!packageUserState.disabledComponents.contains(this.disabledComponents.valueAt(size))) {
                    return false;
                }
            }
        }
        if ((this.enabledComponents == null && packageUserState.enabledComponents != null) || (this.enabledComponents != null && packageUserState.enabledComponents == null)) {
            return false;
        }
        ArraySet<String> arraySet2 = this.enabledComponents;
        if (arraySet2 != null) {
            if (arraySet2.size() != packageUserState.enabledComponents.size()) {
                return false;
            }
            for (int size2 = this.enabledComponents.size() - 1; size2 >= 0; size2--) {
                if (!packageUserState.enabledComponents.contains(this.enabledComponents.valueAt(size2))) {
                    return false;
                }
            }
        }
        return (this.harmfulAppWarning != null || packageUserState.harmfulAppWarning == null) && ((str2 = this.harmfulAppWarning) == null || str2.equals(packageUserState.harmfulAppWarning));
    }
}
