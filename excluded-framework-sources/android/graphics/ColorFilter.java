package android.graphics;

import libcore.util.NativeAllocationRegistry;

/* JADX INFO: loaded from: classes.dex */
public class ColorFilter {
    private Runnable mCleaner;
    private long mNativeInstance;

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    long createNativeInstance() {
        return 0L;
    }

    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(ColorFilter.class.getClassLoader(), ColorFilter.nativeGetFinalizer(), 50);

        private NoImagePreloadHolder() {
        }
    }

    @Deprecated
    public ColorFilter() {
    }

    void discardNativeInstance() {
        if (this.mNativeInstance != 0) {
            this.mCleaner.run();
            this.mCleaner = null;
            this.mNativeInstance = 0L;
        }
    }

    public long getNativeInstance() {
        if (this.mNativeInstance == 0) {
            long jCreateNativeInstance = createNativeInstance();
            this.mNativeInstance = jCreateNativeInstance;
            if (jCreateNativeInstance != 0) {
                this.mCleaner = NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, this.mNativeInstance);
            }
        }
        return this.mNativeInstance;
    }
}
