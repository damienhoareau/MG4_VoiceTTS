package android.security;

/* JADX INFO: loaded from: classes2.dex */
public class FrameworkNetworkSecurityPolicy extends libcore.net.NetworkSecurityPolicy {
    private final boolean mCleartextTrafficPermitted;

    public boolean isCertificateTransparencyVerificationRequired(String str) {
        return false;
    }

    public FrameworkNetworkSecurityPolicy(boolean z) {
        this.mCleartextTrafficPermitted = z;
    }

    public boolean isCleartextTrafficPermitted() {
        return this.mCleartextTrafficPermitted;
    }

    public boolean isCleartextTrafficPermitted(String str) {
        return isCleartextTrafficPermitted();
    }
}
