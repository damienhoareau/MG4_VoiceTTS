package android.security.net.config;

import android.content.Context;
import android.util.ArraySet;
import com.android.org.conscrypt.TrustedCertificateIndex;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes2.dex */
public class ResourceCertificateSource implements CertificateSource {
    private Set<X509Certificate> mCertificates;
    private Context mContext;
    private TrustedCertificateIndex mIndex;
    private final Object mLock = new Object();
    private final int mResourceId;

    @Override // android.security.net.config.CertificateSource
    public void handleTrustStorageUpdate() {
    }

    public ResourceCertificateSource(int i, Context context) {
        this.mResourceId = i;
        this.mContext = context;
    }

    private void ensureInitialized() {
        synchronized (this.mLock) {
            if (this.mCertificates != null) {
                return;
            }
            ArraySet arraySet = new ArraySet();
            InputStream inputStream = null;
            try {
                try {
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    InputStream inputStreamOpenRawResource = this.mContext.getResources().openRawResource(this.mResourceId);
                    try {
                        Collection<? extends Certificate> collectionGenerateCertificates = certificateFactory.generateCertificates(inputStreamOpenRawResource);
                        IoUtils.closeQuietly(inputStreamOpenRawResource);
                        TrustedCertificateIndex trustedCertificateIndex = new TrustedCertificateIndex();
                        for (Certificate certificate : collectionGenerateCertificates) {
                            arraySet.add((X509Certificate) certificate);
                            trustedCertificateIndex.index((X509Certificate) certificate);
                        }
                        this.mCertificates = arraySet;
                        this.mIndex = trustedCertificateIndex;
                        this.mContext = null;
                    } catch (CertificateException e) {
                        e = e;
                        inputStream = inputStreamOpenRawResource;
                        throw new RuntimeException("Failed to load trust anchors from id " + this.mResourceId, e);
                    } catch (Throwable th) {
                        th = th;
                        inputStream = inputStreamOpenRawResource;
                        IoUtils.closeQuietly(inputStream);
                        throw th;
                    }
                } catch (CertificateException e2) {
                    e = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> getCertificates() {
        ensureInitialized();
        return this.mCertificates;
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findBySubjectAndPublicKey(X509Certificate x509Certificate) {
        ensureInitialized();
        java.security.cert.TrustAnchor trustAnchorFindBySubjectAndPublicKey = this.mIndex.findBySubjectAndPublicKey(x509Certificate);
        if (trustAnchorFindBySubjectAndPublicKey == null) {
            return null;
        }
        return trustAnchorFindBySubjectAndPublicKey.getTrustedCert();
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findByIssuerAndSignature(X509Certificate x509Certificate) {
        ensureInitialized();
        java.security.cert.TrustAnchor trustAnchorFindByIssuerAndSignature = this.mIndex.findByIssuerAndSignature(x509Certificate);
        if (trustAnchorFindByIssuerAndSignature == null) {
            return null;
        }
        return trustAnchorFindByIssuerAndSignature.getTrustedCert();
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> findAllByIssuerAndSignature(X509Certificate x509Certificate) {
        ensureInitialized();
        Set setFindAllByIssuerAndSignature = this.mIndex.findAllByIssuerAndSignature(x509Certificate);
        if (setFindAllByIssuerAndSignature.isEmpty()) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet(setFindAllByIssuerAndSignature.size());
        Iterator it = setFindAllByIssuerAndSignature.iterator();
        while (it.hasNext()) {
            arraySet.add(((java.security.cert.TrustAnchor) it.next()).getTrustedCert());
        }
        return arraySet;
    }
}
