package android.content.pm;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class PackageBackwardCompatibility extends PackageSharedLibraryUpdater {
    private static final PackageBackwardCompatibility INSTANCE;
    private static final String TAG = PackageBackwardCompatibility.class.getSimpleName();
    private final boolean mBootClassPathContainsATB;
    private final boolean mBootClassPathContainsOAHL;
    private final PackageSharedLibraryUpdater[] mPackageUpdaters;

    static {
        ArrayList arrayList = new ArrayList();
        boolean z = !addOptionalUpdater(arrayList, "android.content.pm.OrgApacheHttpLegacyUpdater", new Supplier() { // from class: android.content.pm.-$$Lambda$FMztmpMwSp3D3ge8Zxr31di8ZBg
            @Override // java.util.function.Supplier
            public final Object get() {
                return new PackageBackwardCompatibility.RemoveUnnecessaryOrgApacheHttpLegacyLibrary();
            }
        });
        arrayList.add(new AndroidTestRunnerSplitUpdater());
        INSTANCE = new PackageBackwardCompatibility(z, !addOptionalUpdater(arrayList, "android.content.pm.AndroidTestBaseUpdater", new Supplier() { // from class: android.content.pm.-$$Lambda$jpya2qgMDDEok2GAoKRDqPM5lIE
            @Override // java.util.function.Supplier
            public final Object get() {
                return new PackageBackwardCompatibility.RemoveUnnecessaryAndroidTestBaseLibrary();
            }
        }), (PackageSharedLibraryUpdater[]) arrayList.toArray(new PackageSharedLibraryUpdater[0]));
    }

    private static boolean addOptionalUpdater(List<PackageSharedLibraryUpdater> list, String str, Supplier<PackageSharedLibraryUpdater> supplier) {
        Class clsAsSubclass;
        PackageSharedLibraryUpdater packageSharedLibraryUpdater;
        try {
            clsAsSubclass = PackageBackwardCompatibility.class.getClassLoader().loadClass(str).asSubclass(PackageSharedLibraryUpdater.class);
            Log.i(TAG, "Loaded " + str);
        } catch (ClassNotFoundException unused) {
            Log.i(TAG, "Could not find " + str + ", ignoring");
            clsAsSubclass = null;
        }
        boolean z = false;
        if (clsAsSubclass == null) {
            packageSharedLibraryUpdater = supplier.get();
        } else {
            try {
                z = true;
                packageSharedLibraryUpdater = (PackageSharedLibraryUpdater) clsAsSubclass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not create instance of " + str, e);
            }
        }
        list.add(packageSharedLibraryUpdater);
        return z;
    }

    public static PackageSharedLibraryUpdater getInstance() {
        return INSTANCE;
    }

    public PackageBackwardCompatibility(boolean z, boolean z2, PackageSharedLibraryUpdater[] packageSharedLibraryUpdaterArr) {
        this.mBootClassPathContainsOAHL = z;
        this.mBootClassPathContainsATB = z2;
        this.mPackageUpdaters = packageSharedLibraryUpdaterArr;
    }

    public static void modifySharedLibraries(PackageParser.Package r1) {
        INSTANCE.updatePackage(r1);
    }

    @Override // android.content.pm.PackageSharedLibraryUpdater
    public void updatePackage(PackageParser.Package r5) {
        for (PackageSharedLibraryUpdater packageSharedLibraryUpdater : this.mPackageUpdaters) {
            packageSharedLibraryUpdater.updatePackage(r5);
        }
    }

    public static boolean bootClassPathContainsOAHL() {
        return INSTANCE.mBootClassPathContainsOAHL;
    }

    public static boolean bootClassPathContainsATB() {
        return INSTANCE.mBootClassPathContainsATB;
    }

    public static class AndroidTestRunnerSplitUpdater extends PackageSharedLibraryUpdater {
        @Override // android.content.pm.PackageSharedLibraryUpdater
        public void updatePackage(PackageParser.Package r3) {
            prefixImplicitDependency(r3, "android.test.runner", "android.test.mock");
        }
    }

    public static class RemoveUnnecessaryOrgApacheHttpLegacyLibrary extends PackageSharedLibraryUpdater {
        @Override // android.content.pm.PackageSharedLibraryUpdater
        public void updatePackage(PackageParser.Package r2) {
            removeLibrary(r2, "org.apache.http.legacy");
        }
    }

    public static class RemoveUnnecessaryAndroidTestBaseLibrary extends PackageSharedLibraryUpdater {
        @Override // android.content.pm.PackageSharedLibraryUpdater
        public void updatePackage(PackageParser.Package r2) {
            removeLibrary(r2, "android.test.base");
        }
    }
}
