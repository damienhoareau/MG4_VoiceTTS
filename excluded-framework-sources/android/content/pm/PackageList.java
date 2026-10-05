package android.content.pm;

import com.android.server.LocalServices;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PackageList implements PackageManagerInternal.PackageListObserver, AutoCloseable {
    private final List<String> mPackageNames;
    private final PackageManagerInternal.PackageListObserver mWrappedObserver;

    public PackageList(List<String> list, PackageManagerInternal.PackageListObserver packageListObserver) {
        this.mPackageNames = list;
        this.mWrappedObserver = packageListObserver;
    }

    @Override // android.content.pm.PackageManagerInternal.PackageListObserver
    public void onPackageAdded(String str) {
        PackageManagerInternal.PackageListObserver packageListObserver = this.mWrappedObserver;
        if (packageListObserver != null) {
            packageListObserver.onPackageAdded(str);
        }
    }

    @Override // android.content.pm.PackageManagerInternal.PackageListObserver
    public void onPackageRemoved(String str) {
        PackageManagerInternal.PackageListObserver packageListObserver = this.mWrappedObserver;
        if (packageListObserver != null) {
            packageListObserver.onPackageRemoved(str);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).removePackageListObserver(this);
    }

    public List<String> getPackageNames() {
        return this.mPackageNames;
    }
}
