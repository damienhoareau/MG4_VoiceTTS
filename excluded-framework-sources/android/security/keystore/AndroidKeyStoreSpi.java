package android.security.keystore;

import android.security.Credentials;
import android.security.GateKeeper;
import android.security.KeyStore;
import android.security.KeyStoreParameter;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterDefs;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.crypto.SecretKey;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStoreSpi extends KeyStoreSpi {
    public static final String NAME = "AndroidKeyStore";
    private KeyStore mKeyStore;
    private int mUid = -1;

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        String str2 = Credentials.USER_PRIVATE_KEY + str;
        if (!this.mKeyStore.contains(str2, this.mUid)) {
            str2 = Credentials.USER_SECRET_KEY + str;
            if (!this.mKeyStore.contains(str2, this.mUid)) {
                return null;
            }
        }
        return AndroidKeyStoreProvider.loadAndroidKeyStoreKeyFromKeystore(this.mKeyStore, str2, this.mUid);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        Certificate[] certificateArr;
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        X509Certificate x509Certificate = (X509Certificate) engineGetCertificate(str);
        if (x509Certificate == null) {
            return null;
        }
        byte[] bArr = this.mKeyStore.get(Credentials.CA_CERTIFICATE + str, this.mUid);
        int i = 1;
        if (bArr != null) {
            Collection<X509Certificate> certificates = toCertificates(bArr);
            certificateArr = new Certificate[certificates.size() + 1];
            Iterator<X509Certificate> it = certificates.iterator();
            while (it.hasNext()) {
                certificateArr[i] = it.next();
                i++;
            }
        } else {
            certificateArr = new Certificate[1];
        }
        certificateArr[0] = x509Certificate;
        return certificateArr;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        byte[] bArr = this.mKeyStore.get(Credentials.USER_CERTIFICATE + str, this.mUid);
        if (bArr != null) {
            return getCertificateForPrivateKeyEntry(str, bArr);
        }
        byte[] bArr2 = this.mKeyStore.get(Credentials.CA_CERTIFICATE + str, this.mUid);
        if (bArr2 != null) {
            return getCertificateForTrustedCertificateEntry(bArr2);
        }
        return null;
    }

    private Certificate getCertificateForTrustedCertificateEntry(byte[] bArr) {
        return toCertificate(bArr);
    }

    private Certificate getCertificateForPrivateKeyEntry(String str, byte[] bArr) {
        X509Certificate certificate = toCertificate(bArr);
        if (certificate == null) {
            return null;
        }
        String str2 = Credentials.USER_PRIVATE_KEY + str;
        return this.mKeyStore.contains(str2, this.mUid) ? wrapIntoKeyStoreCertificate(str2, this.mUid, certificate) : certificate;
    }

    private static KeyStoreX509Certificate wrapIntoKeyStoreCertificate(String str, int i, X509Certificate x509Certificate) {
        if (x509Certificate != null) {
            return new KeyStoreX509Certificate(str, i, x509Certificate);
        }
        return null;
    }

    private static X509Certificate toCertificate(byte[] bArr) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificate in keystore", e);
            return null;
        }
    }

    private static Collection<X509Certificate> toCertificates(byte[] bArr) {
        try {
            return CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(bArr));
        } catch (CertificateException e) {
            Log.w("AndroidKeyStore", "Couldn't parse certificates in keystore", e);
            return new ArrayList();
        }
    }

    private Date getModificationDate(String str) {
        long j = this.mKeyStore.getmtime(str, this.mUid);
        if (j == -1) {
            return null;
        }
        return new Date(j);
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        Date modificationDate = getModificationDate(Credentials.USER_PRIVATE_KEY + str);
        if (modificationDate != null) {
            return modificationDate;
        }
        Date modificationDate2 = getModificationDate(Credentials.USER_SECRET_KEY + str);
        if (modificationDate2 != null) {
            return modificationDate2;
        }
        Date modificationDate3 = getModificationDate(Credentials.USER_CERTIFICATE + str);
        if (modificationDate3 != null) {
            return modificationDate3;
        }
        return getModificationDate(Credentials.CA_CERTIFICATE + str);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        if (cArr != null && cArr.length > 0) {
            throw new KeyStoreException("entries cannot be protected with passwords");
        }
        if (key instanceof PrivateKey) {
            setPrivateKeyEntry(str, (PrivateKey) key, certificateArr, null);
        } else {
            if (key instanceof SecretKey) {
                setSecretKeyEntry(str, (SecretKey) key, null);
                return;
            }
            throw new KeyStoreException("Only PrivateKey and SecretKey are supported");
        }
    }

    private static KeyProtection getLegacyKeyProtectionParameter(PrivateKey privateKey) throws KeyStoreException {
        KeyProtection.Builder builder;
        String algorithm = privateKey.getAlgorithm();
        if (KeyProperties.KEY_ALGORITHM_EC.equalsIgnoreCase(algorithm)) {
            builder = new KeyProtection.Builder(12);
            builder.setDigests(KeyProperties.DIGEST_NONE, KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA224, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
        } else if (KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(algorithm)) {
            builder = new KeyProtection.Builder(15);
            builder.setDigests(KeyProperties.DIGEST_NONE, KeyProperties.DIGEST_MD5, KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA224, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA512);
            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE, KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1, KeyProperties.ENCRYPTION_PADDING_RSA_OAEP);
            builder.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1, KeyProperties.SIGNATURE_PADDING_RSA_PSS);
            builder.setRandomizedEncryptionRequired(false);
        } else {
            throw new KeyStoreException("Unsupported key algorithm: " + algorithm);
        }
        builder.setUserAuthenticationRequired(false);
        return builder.build();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v21, types: [android.security.KeyStore] */
    /* JADX WARN: Type inference failed for: r0v24, types: [android.security.KeyStore] */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r15v0 */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v2, types: [int] */
    /* JADX WARN: Type inference failed for: r15v3 */
    /* JADX WARN: Type inference failed for: r18v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r19v0, types: [java.security.PrivateKey] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void setPrivateKeyEntry(String str, PrivateKey privateKey, Certificate[] certificateArr, java.security.KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        KeyProtection legacyKeyProtectionParameter;
        ?? IsEncryptionRequired;
        byte[] bArr;
        int i;
        byte[] bArr2;
        int i2;
        int i3 = 0;
        if (protectionParameter == null) {
            legacyKeyProtectionParameter = getLegacyKeyProtectionParameter(privateKey);
            IsEncryptionRequired = 0;
        } else if (protectionParameter instanceof KeyStoreParameter) {
            KeyProtection legacyKeyProtectionParameter2 = getLegacyKeyProtectionParameter(privateKey);
            IsEncryptionRequired = ((KeyStoreParameter) protectionParameter).isEncryptionRequired();
            legacyKeyProtectionParameter = legacyKeyProtectionParameter2;
        } else if (protectionParameter instanceof KeyProtection) {
            legacyKeyProtectionParameter = (KeyProtection) protectionParameter;
            char c = legacyKeyProtectionParameter.isCriticalToDeviceEncryption() ? '\b' : (char) 0;
            int i4 = c;
            if (legacyKeyProtectionParameter.isStrongBoxBacked()) {
                i4 = c | 16;
            }
            IsEncryptionRequired = i4;
        } else {
            throw new KeyStoreException("Unsupported protection parameter class:" + protectionParameter.getClass().getName() + ". Supported: " + KeyProtection.class.getName() + ", " + KeyStoreParameter.class.getName());
        }
        if (certificateArr == null || certificateArr.length == 0) {
            throw new KeyStoreException("Must supply at least one Certificate with PrivateKey");
        }
        int length = certificateArr.length;
        X509Certificate[] x509CertificateArr = new X509Certificate[length];
        for (int i5 = 0; i5 < certificateArr.length; i5++) {
            if (!"X.509".equals(certificateArr[i5].getType())) {
                throw new KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i5);
            }
            if (!(certificateArr[i5] instanceof X509Certificate)) {
                throw new KeyStoreException("Certificates must be in X.509 format: invalid cert #" + i5);
            }
            x509CertificateArr[i5] = (X509Certificate) certificateArr[i5];
        }
        try {
            byte[] encoded = x509CertificateArr[0].getEncoded();
            KeymasterArguments keymasterArguments = null;
            if (certificateArr.length > 1) {
                int i6 = length - 1;
                byte[][] bArr3 = new byte[i6][];
                int i7 = 0;
                int length2 = 0;
                while (i7 < i6) {
                    int i8 = i7 + 1;
                    try {
                        bArr3[i7] = x509CertificateArr[i8].getEncoded();
                        length2 += bArr3[i7].length;
                        i7 = i8;
                    } catch (CertificateEncodingException e) {
                        throw new KeyStoreException("Failed to encode certificate #" + i7, e);
                    }
                }
                byte[] bArr4 = new byte[length2];
                int i9 = 0;
                for (int i10 = 0; i10 < i6; i10++) {
                    int length3 = bArr3[i10].length;
                    System.arraycopy(bArr3[i10], 0, bArr4, i9, length3);
                    i9 += length3;
                    bArr3[i10] = null;
                }
                bArr = bArr4;
            } else {
                bArr = null;
            }
            String alias = privateKey instanceof AndroidKeyStorePrivateKey ? ((AndroidKeyStoreKey) privateKey).getAlias() : null;
            if (alias != null && alias.startsWith(Credentials.USER_PRIVATE_KEY)) {
                String strSubstring = alias.substring(8);
                if (!str.equals(strSubstring)) {
                    throw new KeyStoreException("Can only replace keys with same alias: " + ((String) str) + " != " + strSubstring);
                }
                bArr2 = null;
                i = 1;
            } else {
                String format = privateKey.getFormat();
                if (format == null || !"PKCS#8".equals(format)) {
                    throw new KeyStoreException("Unsupported private key export format: " + format + ". Only private keys which export their key material in PKCS#8 format are supported.");
                }
                byte[] encoded2 = privateKey.getEncoded();
                if (encoded2 == null) {
                    throw new KeyStoreException("Private key did not export any key material");
                }
                KeymasterArguments keymasterArguments2 = new KeymasterArguments();
                try {
                    keymasterArguments2.addEnum(KeymasterDefs.KM_TAG_ALGORITHM, KeyProperties.KeyAlgorithm.toKeymasterAsymmetricKeyAlgorithm(privateKey.getAlgorithm()));
                    int purposes = legacyKeyProtectionParameter.getPurposes();
                    keymasterArguments2.addEnums(KeymasterDefs.KM_TAG_PURPOSE, KeyProperties.Purpose.allToKeymaster(purposes));
                    if (legacyKeyProtectionParameter.isDigestsSpecified()) {
                        keymasterArguments2.addEnums(KeymasterDefs.KM_TAG_DIGEST, KeyProperties.Digest.allToKeymaster(legacyKeyProtectionParameter.getDigests()));
                    }
                    keymasterArguments2.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, KeyProperties.BlockMode.allToKeymaster(legacyKeyProtectionParameter.getBlockModes()));
                    int[] iArrAllToKeymaster = KeyProperties.EncryptionPadding.allToKeymaster(legacyKeyProtectionParameter.getEncryptionPaddings());
                    i = 1;
                    if ((purposes & 1) != 0 && legacyKeyProtectionParameter.isRandomizedEncryptionRequired()) {
                        int length4 = iArrAllToKeymaster.length;
                        while (i3 < length4) {
                            int i11 = iArrAllToKeymaster[i3];
                            if (!KeymasterUtils.isKeymasterPaddingSchemeIndCpaCompatibleWithAsymmetricCrypto(i11)) {
                                throw new KeyStoreException("Randomized encryption (IND-CPA) required but is violated by encryption padding mode: " + KeyProperties.EncryptionPadding.fromKeymaster(i11) + ". See KeyProtection documentation.");
                            }
                            i3++;
                        }
                    }
                    keymasterArguments2.addEnums(KeymasterDefs.KM_TAG_PADDING, iArrAllToKeymaster);
                    keymasterArguments2.addEnums(KeymasterDefs.KM_TAG_PADDING, KeyProperties.SignaturePadding.allToKeymaster(legacyKeyProtectionParameter.getSignaturePaddings()));
                    KeymasterUtils.addUserAuthArgs(keymasterArguments2, legacyKeyProtectionParameter);
                    keymasterArguments2.addDateIfNotNull(KeymasterDefs.KM_TAG_ACTIVE_DATETIME, legacyKeyProtectionParameter.getKeyValidityStart());
                    keymasterArguments2.addDateIfNotNull(KeymasterDefs.KM_TAG_ORIGINATION_EXPIRE_DATETIME, legacyKeyProtectionParameter.getKeyValidityForOriginationEnd());
                    keymasterArguments2.addDateIfNotNull(KeymasterDefs.KM_TAG_USAGE_EXPIRE_DATETIME, legacyKeyProtectionParameter.getKeyValidityForConsumptionEnd());
                    bArr2 = encoded2;
                    keymasterArguments = keymasterArguments2;
                    i3 = 1;
                } catch (IllegalArgumentException | IllegalStateException e2) {
                    throw new KeyStoreException(e2);
                }
            }
            try {
                if (i3 != 0) {
                    Credentials.deleteAllTypesForAlias(this.mKeyStore, str, this.mUid);
                    KeyCharacteristics keyCharacteristics = new KeyCharacteristics();
                    i2 = i;
                    int iImportKey = this.mKeyStore.importKey(Credentials.USER_PRIVATE_KEY + ((String) str), keymasterArguments, 1, bArr2, this.mUid, IsEncryptionRequired == true ? 1 : 0, keyCharacteristics);
                    if (iImportKey != i2) {
                        throw new KeyStoreException("Failed to store private key", KeyStore.getKeyStoreException(iImportKey));
                    }
                } else {
                    i2 = i;
                    Credentials.deleteCertificateTypesForAlias(this.mKeyStore, str, this.mUid);
                    Credentials.deleteLegacyKeyForAlias(this.mKeyStore, str, this.mUid);
                }
                int iInsert = this.mKeyStore.insert(Credentials.USER_CERTIFICATE + ((String) str), encoded, this.mUid, IsEncryptionRequired);
                if (iInsert != i2) {
                    throw new KeyStoreException("Failed to store certificate #0", KeyStore.getKeyStoreException(iInsert));
                }
                int iInsert2 = this.mKeyStore.insert(Credentials.CA_CERTIFICATE + ((String) str), bArr, this.mUid, IsEncryptionRequired);
                if (iInsert2 != i2) {
                    throw new KeyStoreException("Failed to store certificate chain", KeyStore.getKeyStoreException(iInsert2));
                }
            } catch (Throwable th) {
                if (i3 != 0) {
                    Credentials.deleteAllTypesForAlias(this.mKeyStore, str, this.mUid);
                } else {
                    Credentials.deleteCertificateTypesForAlias(this.mKeyStore, str, this.mUid);
                    Credentials.deleteLegacyKeyForAlias(this.mKeyStore, str, this.mUid);
                }
                throw th;
            }
        } catch (CertificateEncodingException e3) {
            throw new KeyStoreException("Failed to encode certificate #0", e3);
        }
    }

    private void setSecretKeyEntry(String str, SecretKey secretKey, java.security.KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        int[] iArrAllToKeymaster;
        if (protectionParameter != null && !(protectionParameter instanceof KeyProtection)) {
            throw new KeyStoreException("Unsupported protection parameter class: " + protectionParameter.getClass().getName() + ". Supported: " + KeyProtection.class.getName());
        }
        KeyProtection keyProtection = (KeyProtection) protectionParameter;
        boolean z = secretKey instanceof AndroidKeyStoreSecretKey;
        String str2 = Credentials.USER_PRIVATE_KEY;
        if (z) {
            String alias = ((AndroidKeyStoreSecretKey) secretKey).getAlias();
            if (alias == null) {
                throw new KeyStoreException("KeyStore-backed secret key does not have an alias");
            }
            if (!alias.startsWith(Credentials.USER_PRIVATE_KEY)) {
                str2 = Credentials.USER_SECRET_KEY;
                if (!alias.startsWith(Credentials.USER_SECRET_KEY)) {
                    throw new KeyStoreException("KeyStore-backed secret key has invalid alias: " + alias);
                }
            }
            String strSubstring = alias.substring(str2.length());
            if (str.equals(strSubstring)) {
                if (keyProtection != null) {
                    throw new KeyStoreException("Modifying KeyStore-backed key using protection parameters not supported");
                }
                return;
            }
            throw new KeyStoreException("Can only replace KeyStore-backed keys with same alias: " + str + " != " + strSubstring);
        }
        if (keyProtection == null) {
            throw new KeyStoreException("Protection parameters must be specified when importing a symmetric key");
        }
        String format = secretKey.getFormat();
        if (format == null) {
            throw new KeyStoreException("Only secret keys that export their key material are supported");
        }
        if (!"RAW".equals(format)) {
            throw new KeyStoreException("Unsupported secret key material export format: " + format);
        }
        byte[] encoded = secretKey.getEncoded();
        if (encoded == null) {
            throw new KeyStoreException("Key did not export its key material despite supporting RAW format export");
        }
        KeymasterArguments keymasterArguments = new KeymasterArguments();
        try {
            int keymasterSecretKeyAlgorithm = KeyProperties.KeyAlgorithm.toKeymasterSecretKeyAlgorithm(secretKey.getAlgorithm());
            keymasterArguments.addEnum(KeymasterDefs.KM_TAG_ALGORITHM, keymasterSecretKeyAlgorithm);
            if (keymasterSecretKeyAlgorithm == 128) {
                int keymasterDigest = KeyProperties.KeyAlgorithm.toKeymasterDigest(secretKey.getAlgorithm());
                if (keymasterDigest == -1) {
                    throw new ProviderException("HMAC key algorithm digest unknown for key algorithm " + secretKey.getAlgorithm());
                }
                iArrAllToKeymaster = new int[]{keymasterDigest};
                if (keyProtection.isDigestsSpecified()) {
                    int[] iArrAllToKeymaster2 = KeyProperties.Digest.allToKeymaster(keyProtection.getDigests());
                    if (iArrAllToKeymaster2.length != 1 || iArrAllToKeymaster2[0] != keymasterDigest) {
                        throw new KeyStoreException("Unsupported digests specification: " + Arrays.asList(keyProtection.getDigests()) + ". Only " + KeyProperties.Digest.fromKeymaster(keymasterDigest) + " supported for HMAC key algorithm " + secretKey.getAlgorithm());
                    }
                }
            } else if (keyProtection.isDigestsSpecified()) {
                iArrAllToKeymaster = KeyProperties.Digest.allToKeymaster(keyProtection.getDigests());
            } else {
                iArrAllToKeymaster = EmptyArray.INT;
            }
            keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, iArrAllToKeymaster);
            int purposes = keyProtection.getPurposes();
            int[] iArrAllToKeymaster3 = KeyProperties.BlockMode.allToKeymaster(keyProtection.getBlockModes());
            int i = purposes & 1;
            if (i != 0 && keyProtection.isRandomizedEncryptionRequired()) {
                for (int i2 : iArrAllToKeymaster3) {
                    if (!KeymasterUtils.isKeymasterBlockModeIndCpaCompatibleWithSymmetricCrypto(i2)) {
                        throw new KeyStoreException("Randomized encryption (IND-CPA) required but may be violated by block mode: " + KeyProperties.BlockMode.fromKeymaster(i2) + ". See KeyProtection documentation.");
                    }
                }
            }
            keymasterArguments.addEnums(KeymasterDefs.KM_TAG_PURPOSE, KeyProperties.Purpose.allToKeymaster(purposes));
            keymasterArguments.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, iArrAllToKeymaster3);
            if (keyProtection.getSignaturePaddings().length > 0) {
                throw new KeyStoreException("Signature paddings not supported for symmetric keys");
            }
            keymasterArguments.addEnums(KeymasterDefs.KM_TAG_PADDING, KeyProperties.EncryptionPadding.allToKeymaster(keyProtection.getEncryptionPaddings()));
            KeymasterUtils.addUserAuthArgs(keymasterArguments, keyProtection);
            KeymasterUtils.addMinMacLengthAuthorizationIfNecessary(keymasterArguments, keymasterSecretKeyAlgorithm, iArrAllToKeymaster3, iArrAllToKeymaster);
            keymasterArguments.addDateIfNotNull(KeymasterDefs.KM_TAG_ACTIVE_DATETIME, keyProtection.getKeyValidityStart());
            keymasterArguments.addDateIfNotNull(KeymasterDefs.KM_TAG_ORIGINATION_EXPIRE_DATETIME, keyProtection.getKeyValidityForOriginationEnd());
            keymasterArguments.addDateIfNotNull(KeymasterDefs.KM_TAG_USAGE_EXPIRE_DATETIME, keyProtection.getKeyValidityForConsumptionEnd());
            if (i != 0 && !keyProtection.isRandomizedEncryptionRequired()) {
                keymasterArguments.addBoolean(KeymasterDefs.KM_TAG_CALLER_NONCE);
            }
            int i3 = keyProtection.isCriticalToDeviceEncryption() ? 8 : 0;
            int i4 = keyProtection.isStrongBoxBacked() ? i3 | 16 : i3;
            Credentials.deleteAllTypesForAlias(this.mKeyStore, str, this.mUid);
            int iImportKey = this.mKeyStore.importKey(Credentials.USER_PRIVATE_KEY + str, keymasterArguments, 3, encoded, this.mUid, i4, new KeyCharacteristics());
            if (iImportKey == 1) {
                return;
            }
            throw new KeyStoreException("Failed to import secret key. Keystore error code: " + iImportKey);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new KeyStoreException(e);
        }
    }

    private void setWrappedKeyEntry(String str, WrappedKeyEntry wrappedKeyEntry, java.security.KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        if (protectionParameter != null) {
            throw new KeyStoreException("Protection parameters are specified inside wrapped keys");
        }
        byte[] bArr = new byte[32];
        KeymasterArguments keymasterArguments = new KeymasterArguments();
        String[] strArrSplit = wrappedKeyEntry.getTransformation().split("/");
        String str2 = strArrSplit[0];
        if (KeyProperties.KEY_ALGORITHM_RSA.equalsIgnoreCase(str2) || KeyProperties.KEY_ALGORITHM_EC.equalsIgnoreCase(str2)) {
            keymasterArguments.addEnum(KeymasterDefs.KM_TAG_ALGORITHM, 1);
        }
        if (strArrSplit.length > 1) {
            String str3 = strArrSplit[1];
            if (KeyProperties.BLOCK_MODE_ECB.equalsIgnoreCase(str3)) {
                keymasterArguments.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, 1);
            } else if (KeyProperties.BLOCK_MODE_CBC.equalsIgnoreCase(str3)) {
                keymasterArguments.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, 2);
            } else if (KeyProperties.BLOCK_MODE_CTR.equalsIgnoreCase(str3)) {
                keymasterArguments.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, 3);
            } else if (KeyProperties.BLOCK_MODE_GCM.equalsIgnoreCase(str3)) {
                keymasterArguments.addEnums(KeymasterDefs.KM_TAG_BLOCK_MODE, 32);
            }
        }
        if (strArrSplit.length > 2) {
            String str4 = strArrSplit[2];
            if (!KeyProperties.ENCRYPTION_PADDING_NONE.equalsIgnoreCase(str4)) {
                if (KeyProperties.ENCRYPTION_PADDING_PKCS7.equalsIgnoreCase(str4)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_PADDING, 64);
                } else if (KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1.equalsIgnoreCase(str4)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_PADDING, 4);
                } else if (KeyProperties.ENCRYPTION_PADDING_RSA_OAEP.equalsIgnoreCase(str4)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_PADDING, 2);
                }
            }
        }
        KeyGenParameterSpec keyGenParameterSpec = (KeyGenParameterSpec) wrappedKeyEntry.getAlgorithmParameterSpec();
        if (keyGenParameterSpec.isDigestsSpecified()) {
            String str5 = keyGenParameterSpec.getDigests()[0];
            if (!KeyProperties.DIGEST_NONE.equalsIgnoreCase(str5)) {
                if (KeyProperties.DIGEST_MD5.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 1);
                } else if (KeyProperties.DIGEST_SHA1.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 2);
                } else if (KeyProperties.DIGEST_SHA224.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 3);
                } else if (KeyProperties.DIGEST_SHA256.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 4);
                } else if (KeyProperties.DIGEST_SHA384.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 5);
                } else if (KeyProperties.DIGEST_SHA512.equalsIgnoreCase(str5)) {
                    keymasterArguments.addEnums(KeymasterDefs.KM_TAG_DIGEST, 6);
                }
            }
        }
        int iImportWrappedKey = this.mKeyStore.importWrappedKey(Credentials.USER_SECRET_KEY + str, wrappedKeyEntry.getWrappedKeyBytes(), Credentials.USER_PRIVATE_KEY + wrappedKeyEntry.getWrappingKeyAlias(), bArr, keymasterArguments, GateKeeper.getSecureUserId(), 0L, this.mUid, new KeyCharacteristics());
        if (iImportWrappedKey == -100) {
            throw new SecureKeyImportUnavailableException("Could not import wrapped key");
        }
        if (iImportWrappedKey == 1) {
            return;
        }
        throw new KeyStoreException("Failed to import wrapped key. Keystore error code: " + iImportWrappedKey);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        throw new KeyStoreException("Operation not supported because key encoding is unknown");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        if (isKeyEntry(str)) {
            throw new KeyStoreException("Entry exists and is not a trusted certificate");
        }
        if (certificate == null) {
            throw new NullPointerException("cert == null");
        }
        try {
            byte[] encoded = certificate.getEncoded();
            if (!this.mKeyStore.put(Credentials.CA_CERTIFICATE + str, encoded, this.mUid, 0)) {
                throw new KeyStoreException("Couldn't insert certificate; is KeyStore initialized?");
            }
        } catch (CertificateEncodingException e) {
            throw new KeyStoreException(e);
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws KeyStoreException {
        if (Credentials.deleteAllTypesForAlias(this.mKeyStore, str, this.mUid)) {
            return;
        }
        throw new KeyStoreException("Failed to delete entry: " + str);
    }

    private Set<String> getUniqueAliases() {
        String[] list = this.mKeyStore.list("", this.mUid);
        if (list == null) {
            return new HashSet();
        }
        HashSet hashSet = new HashSet(list.length);
        for (String str : list) {
            int iIndexOf = str.indexOf(95);
            if (iIndexOf == -1 || str.length() <= iIndexOf) {
                Log.e("AndroidKeyStore", "invalid alias: " + str);
            } else {
                hashSet.add(new String(str.substring(iIndexOf + 1)));
            }
        }
        return hashSet;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(getUniqueAliases());
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        if (!this.mKeyStore.contains(Credentials.USER_PRIVATE_KEY + str, this.mUid)) {
            if (!this.mKeyStore.contains(Credentials.USER_SECRET_KEY + str, this.mUid)) {
                if (!this.mKeyStore.contains(Credentials.USER_CERTIFICATE + str, this.mUid)) {
                    if (!this.mKeyStore.contains(Credentials.CA_CERTIFICATE + str, this.mUid)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return getUniqueAliases().size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return isKeyEntry(str);
    }

    private boolean isKeyEntry(String str) {
        if (!this.mKeyStore.contains(Credentials.USER_PRIVATE_KEY + str, this.mUid)) {
            if (!this.mKeyStore.contains(Credentials.USER_SECRET_KEY + str, this.mUid)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCertificateEntry(String str) {
        if (str == null) {
            throw new NullPointerException("alias == null");
        }
        return this.mKeyStore.contains(Credentials.CA_CERTIFICATE + str, this.mUid);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        return !isKeyEntry(str) && isCertificateEntry(str);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        if (certificate == null || !"X.509".equalsIgnoreCase(certificate.getType())) {
            return null;
        }
        try {
            byte[] encoded = certificate.getEncoded();
            if (encoded == null) {
                return null;
            }
            HashSet hashSet = new HashSet();
            String[] list = this.mKeyStore.list(Credentials.USER_CERTIFICATE, this.mUid);
            if (list != null) {
                for (String str : list) {
                    byte[] bArr = this.mKeyStore.get(Credentials.USER_CERTIFICATE + str, this.mUid);
                    if (bArr != null) {
                        hashSet.add(str);
                        if (Arrays.equals(bArr, encoded)) {
                            return str;
                        }
                    }
                }
            }
            String[] list2 = this.mKeyStore.list(Credentials.CA_CERTIFICATE, this.mUid);
            if (list != null) {
                for (String str2 : list2) {
                    if (!hashSet.contains(str2)) {
                        byte[] bArr2 = this.mKeyStore.get(Credentials.CA_CERTIFICATE + str2, this.mUid);
                        if (bArr2 != null && Arrays.equals(bArr2, encoded)) {
                            return str2;
                        }
                    }
                }
            }
            return null;
        } catch (CertificateEncodingException unused) {
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream inputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (inputStream != null) {
            throw new IllegalArgumentException("InputStream not supported");
        }
        if (cArr != null) {
            throw new IllegalArgumentException("password not supported");
        }
        this.mKeyStore = KeyStore.getInstance();
        this.mUid = -1;
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(java.security.KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        int uid;
        if (loadStoreParameter == null) {
            uid = -1;
        } else if (loadStoreParameter instanceof AndroidKeyStoreLoadStoreParameter) {
            uid = ((AndroidKeyStoreLoadStoreParameter) loadStoreParameter).getUid();
        } else {
            throw new IllegalArgumentException("Unsupported param type: " + loadStoreParameter.getClass());
        }
        this.mKeyStore = KeyStore.getInstance();
        this.mUid = uid;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetEntry(String str, java.security.KeyStore.Entry entry, java.security.KeyStore.ProtectionParameter protectionParameter) throws KeyStoreException {
        if (entry == null) {
            throw new KeyStoreException("entry == null");
        }
        Credentials.deleteAllTypesForAlias(this.mKeyStore, str, this.mUid);
        if (entry instanceof java.security.KeyStore.TrustedCertificateEntry) {
            engineSetCertificateEntry(str, ((java.security.KeyStore.TrustedCertificateEntry) entry).getTrustedCertificate());
            return;
        }
        if (entry instanceof java.security.KeyStore.PrivateKeyEntry) {
            java.security.KeyStore.PrivateKeyEntry privateKeyEntry = (java.security.KeyStore.PrivateKeyEntry) entry;
            setPrivateKeyEntry(str, privateKeyEntry.getPrivateKey(), privateKeyEntry.getCertificateChain(), protectionParameter);
        } else if (entry instanceof java.security.KeyStore.SecretKeyEntry) {
            setSecretKeyEntry(str, ((java.security.KeyStore.SecretKeyEntry) entry).getSecretKey(), protectionParameter);
        } else {
            if (entry instanceof WrappedKeyEntry) {
                setWrappedKeyEntry(str, (WrappedKeyEntry) entry, protectionParameter);
                return;
            }
            throw new KeyStoreException("Entry must be a PrivateKeyEntry, SecretKeyEntry or TrustedCertificateEntry; was " + entry);
        }
    }

    static class KeyStoreX509Certificate extends DelegatingX509Certificate {
        private final String mPrivateKeyAlias;
        private final int mPrivateKeyUid;

        KeyStoreX509Certificate(String str, int i, X509Certificate x509Certificate) {
            super(x509Certificate);
            this.mPrivateKeyAlias = str;
            this.mPrivateKeyUid = i;
        }

        @Override // android.security.keystore.DelegatingX509Certificate, java.security.cert.Certificate
        public PublicKey getPublicKey() {
            PublicKey publicKey = super.getPublicKey();
            return AndroidKeyStoreProvider.getAndroidKeyStorePublicKey(this.mPrivateKeyAlias, this.mPrivateKeyUid, publicKey.getAlgorithm(), publicKey.getEncoded());
        }
    }
}
