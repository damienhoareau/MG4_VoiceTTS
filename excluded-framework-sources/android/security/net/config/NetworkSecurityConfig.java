package android.security.net.config;

import android.content.pm.ApplicationInfo;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public final class NetworkSecurityConfig {
    public static final boolean DEFAULT_CLEARTEXT_TRAFFIC_PERMITTED = true;
    public static final boolean DEFAULT_HSTS_ENFORCED = false;
    private Set<TrustAnchor> mAnchors;
    private final Object mAnchorsLock;
    private final List<CertificatesEntryRef> mCertificatesEntryRefs;
    private final boolean mCleartextTrafficPermitted;
    private final boolean mHstsEnforced;
    private final PinSet mPins;
    private NetworkSecurityTrustManager mTrustManager;
    private final Object mTrustManagerLock;

    private NetworkSecurityConfig(boolean z, boolean z2, PinSet pinSet, List<CertificatesEntryRef> list) {
        this.mAnchorsLock = new Object();
        this.mTrustManagerLock = new Object();
        this.mCleartextTrafficPermitted = z;
        this.mHstsEnforced = z2;
        this.mPins = pinSet;
        this.mCertificatesEntryRefs = list;
        Collections.sort(list, new Comparator<CertificatesEntryRef>() { // from class: android.security.net.config.NetworkSecurityConfig.1
            @Override // java.util.Comparator
            public int compare(CertificatesEntryRef certificatesEntryRef, CertificatesEntryRef certificatesEntryRef2) {
                if (certificatesEntryRef.overridesPins()) {
                    return certificatesEntryRef2.overridesPins() ? 0 : -1;
                }
                return certificatesEntryRef2.overridesPins() ? 1 : 0;
            }
        });
    }

    public Set<TrustAnchor> getTrustAnchors() {
        synchronized (this.mAnchorsLock) {
            if (this.mAnchors != null) {
                return this.mAnchors;
            }
            ArrayMap arrayMap = new ArrayMap();
            Iterator<CertificatesEntryRef> it = this.mCertificatesEntryRefs.iterator();
            while (it.hasNext()) {
                for (TrustAnchor trustAnchor : it.next().getTrustAnchors()) {
                    X509Certificate x509Certificate = trustAnchor.certificate;
                    if (!arrayMap.containsKey(x509Certificate)) {
                        arrayMap.put(x509Certificate, trustAnchor);
                    }
                }
            }
            ArraySet arraySet = new ArraySet(arrayMap.size());
            arraySet.addAll((Collection) arrayMap.values());
            this.mAnchors = arraySet;
            return arraySet;
        }
    }

    public boolean isCleartextTrafficPermitted() {
        return this.mCleartextTrafficPermitted;
    }

    public boolean isHstsEnforced() {
        return this.mHstsEnforced;
    }

    public PinSet getPins() {
        return this.mPins;
    }

    public NetworkSecurityTrustManager getTrustManager() {
        NetworkSecurityTrustManager networkSecurityTrustManager;
        synchronized (this.mTrustManagerLock) {
            if (this.mTrustManager == null) {
                this.mTrustManager = new NetworkSecurityTrustManager(this);
            }
            networkSecurityTrustManager = this.mTrustManager;
        }
        return networkSecurityTrustManager;
    }

    public TrustAnchor findTrustAnchorBySubjectAndPublicKey(X509Certificate x509Certificate) {
        Iterator<CertificatesEntryRef> it = this.mCertificatesEntryRefs.iterator();
        while (it.hasNext()) {
            TrustAnchor trustAnchorFindBySubjectAndPublicKey = it.next().findBySubjectAndPublicKey(x509Certificate);
            if (trustAnchorFindBySubjectAndPublicKey != null) {
                return trustAnchorFindBySubjectAndPublicKey;
            }
        }
        return null;
    }

    public TrustAnchor findTrustAnchorByIssuerAndSignature(X509Certificate x509Certificate) {
        Iterator<CertificatesEntryRef> it = this.mCertificatesEntryRefs.iterator();
        while (it.hasNext()) {
            TrustAnchor trustAnchorFindByIssuerAndSignature = it.next().findByIssuerAndSignature(x509Certificate);
            if (trustAnchorFindByIssuerAndSignature != null) {
                return trustAnchorFindByIssuerAndSignature;
            }
        }
        return null;
    }

    public Set<X509Certificate> findAllCertificatesByIssuerAndSignature(X509Certificate x509Certificate) {
        ArraySet arraySet = new ArraySet();
        Iterator<CertificatesEntryRef> it = this.mCertificatesEntryRefs.iterator();
        while (it.hasNext()) {
            arraySet.addAll(it.next().findAllCertificatesByIssuerAndSignature(x509Certificate));
        }
        return arraySet;
    }

    public void handleTrustStorageUpdate() {
        synchronized (this.mAnchorsLock) {
            this.mAnchors = null;
            Iterator<CertificatesEntryRef> it = this.mCertificatesEntryRefs.iterator();
            while (it.hasNext()) {
                it.next().handleTrustStorageUpdate();
            }
        }
        getTrustManager().handleTrustStorageUpdate();
    }

    public static Builder getDefaultBuilder(ApplicationInfo applicationInfo) {
        Builder builderAddCertificatesEntryRef = new Builder().setHstsEnforced(false).addCertificatesEntryRef(new CertificatesEntryRef(SystemCertificateSource.getInstance(), false));
        builderAddCertificatesEntryRef.setCleartextTrafficPermitted(applicationInfo.targetSdkVersion < 28 && applicationInfo.targetSandboxVersion < 2);
        if (applicationInfo.targetSdkVersion <= 23 && !applicationInfo.isPrivilegedApp()) {
            builderAddCertificatesEntryRef.addCertificatesEntryRef(new CertificatesEntryRef(UserCertificateSource.getInstance(), false));
        }
        return builderAddCertificatesEntryRef;
    }

    public static final class Builder {
        private List<CertificatesEntryRef> mCertificatesEntryRefs;
        private Builder mParentBuilder;
        private PinSet mPinSet;
        private boolean mCleartextTrafficPermitted = true;
        private boolean mHstsEnforced = false;
        private boolean mCleartextTrafficPermittedSet = false;
        private boolean mHstsEnforcedSet = false;

        public Builder setParent(Builder builder) {
            for (Builder parent = builder; parent != null; parent = parent.getParent()) {
                if (parent == this) {
                    throw new IllegalArgumentException("Loops are not allowed in Builder parents");
                }
            }
            this.mParentBuilder = builder;
            return this;
        }

        public Builder getParent() {
            return this.mParentBuilder;
        }

        public Builder setPinSet(PinSet pinSet) {
            this.mPinSet = pinSet;
            return this;
        }

        private PinSet getEffectivePinSet() {
            PinSet pinSet = this.mPinSet;
            if (pinSet != null) {
                return pinSet;
            }
            Builder builder = this.mParentBuilder;
            if (builder != null) {
                return builder.getEffectivePinSet();
            }
            return PinSet.EMPTY_PINSET;
        }

        public Builder setCleartextTrafficPermitted(boolean z) {
            this.mCleartextTrafficPermitted = z;
            this.mCleartextTrafficPermittedSet = true;
            return this;
        }

        private boolean getEffectiveCleartextTrafficPermitted() {
            if (this.mCleartextTrafficPermittedSet) {
                return this.mCleartextTrafficPermitted;
            }
            Builder builder = this.mParentBuilder;
            if (builder != null) {
                return builder.getEffectiveCleartextTrafficPermitted();
            }
            return true;
        }

        public Builder setHstsEnforced(boolean z) {
            this.mHstsEnforced = z;
            this.mHstsEnforcedSet = true;
            return this;
        }

        private boolean getEffectiveHstsEnforced() {
            if (this.mHstsEnforcedSet) {
                return this.mHstsEnforced;
            }
            Builder builder = this.mParentBuilder;
            if (builder != null) {
                return builder.getEffectiveHstsEnforced();
            }
            return false;
        }

        public Builder addCertificatesEntryRef(CertificatesEntryRef certificatesEntryRef) {
            if (this.mCertificatesEntryRefs == null) {
                this.mCertificatesEntryRefs = new ArrayList();
            }
            this.mCertificatesEntryRefs.add(certificatesEntryRef);
            return this;
        }

        public Builder addCertificatesEntryRefs(Collection<? extends CertificatesEntryRef> collection) {
            if (this.mCertificatesEntryRefs == null) {
                this.mCertificatesEntryRefs = new ArrayList();
            }
            this.mCertificatesEntryRefs.addAll(collection);
            return this;
        }

        private List<CertificatesEntryRef> getEffectiveCertificatesEntryRefs() {
            List<CertificatesEntryRef> list = this.mCertificatesEntryRefs;
            if (list != null) {
                return list;
            }
            Builder builder = this.mParentBuilder;
            if (builder != null) {
                return builder.getEffectiveCertificatesEntryRefs();
            }
            return Collections.emptyList();
        }

        public boolean hasCertificatesEntryRefs() {
            return this.mCertificatesEntryRefs != null;
        }

        List<CertificatesEntryRef> getCertificatesEntryRefs() {
            return this.mCertificatesEntryRefs;
        }

        public NetworkSecurityConfig build() {
            return new NetworkSecurityConfig(getEffectiveCleartextTrafficPermitted(), getEffectiveHstsEnforced(), getEffectivePinSet(), getEffectiveCertificatesEntryRefs());
        }
    }
}
