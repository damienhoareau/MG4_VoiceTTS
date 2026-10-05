package android.security.keystore;

import android.security.Credentials;
import android.security.GateKeeper;
import android.security.KeyStore;
import android.security.keymaster.KeyCharacteristics;
import android.security.keymaster.KeymasterDefs;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStoreSecretKeyFactorySpi extends SecretKeyFactorySpi {
    private final KeyStore mKeyStore = KeyStore.getInstance();

    /* JADX WARN: Multi-variable type inference failed */
    @Override // javax.crypto.SecretKeyFactorySpi
    protected KeySpec engineGetKeySpec(SecretKey secretKey, Class cls) throws InvalidKeySpecException {
        if (cls == null) {
            throw new InvalidKeySpecException("keySpecClass == null");
        }
        if (!(secretKey instanceof AndroidKeyStoreSecretKey)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Only Android KeyStore secret keys supported: ");
            sb.append(secretKey != 0 ? secretKey.getClass().getName() : "null");
            throw new InvalidKeySpecException(sb.toString());
        }
        if (SecretKeySpec.class.isAssignableFrom(cls)) {
            throw new InvalidKeySpecException("Key material export of Android KeyStore keys is not supported");
        }
        if (!KeyInfo.class.equals(cls)) {
            throw new InvalidKeySpecException("Unsupported key spec: " + cls.getName());
        }
        AndroidKeyStoreKey androidKeyStoreKey = (AndroidKeyStoreKey) secretKey;
        String alias = androidKeyStoreKey.getAlias();
        if (alias.startsWith(Credentials.USER_PRIVATE_KEY) || alias.startsWith(Credentials.USER_SECRET_KEY)) {
            String strSubstring = alias.substring(8);
            return getKeyInfo(this.mKeyStore, strSubstring, alias, androidKeyStoreKey.getUid());
        }
        throw new InvalidKeySpecException("Invalid key alias: " + alias);
    }

    static KeyInfo getKeyInfo(KeyStore keyStore, String str, String str2, int i) {
        int iFromKeymaster;
        boolean z;
        KeyCharacteristics keyCharacteristics = new KeyCharacteristics();
        int keyCharacteristics2 = keyStore.getKeyCharacteristics(str2, null, null, i, keyCharacteristics);
        if (keyCharacteristics2 != 1) {
            throw new ProviderException("Failed to obtain information about key. Keystore error: " + keyCharacteristics2);
        }
        try {
            if (keyCharacteristics.hwEnforced.containsTag(KeymasterDefs.KM_TAG_ORIGIN)) {
                iFromKeymaster = KeyProperties.Origin.fromKeymaster(keyCharacteristics.hwEnforced.getEnum(KeymasterDefs.KM_TAG_ORIGIN, -1));
                z = true;
            } else if (keyCharacteristics.swEnforced.containsTag(KeymasterDefs.KM_TAG_ORIGIN)) {
                iFromKeymaster = KeyProperties.Origin.fromKeymaster(keyCharacteristics.swEnforced.getEnum(KeymasterDefs.KM_TAG_ORIGIN, -1));
                z = false;
            } else {
                throw new ProviderException("Key origin not available");
            }
            long unsignedInt = keyCharacteristics.getUnsignedInt(KeymasterDefs.KM_TAG_KEY_SIZE, -1L);
            if (unsignedInt == -1) {
                throw new ProviderException("Key size not available");
            }
            if (unsignedInt > 2147483647L) {
                throw new ProviderException("Key too large: " + unsignedInt + " bits");
            }
            int i2 = (int) unsignedInt;
            int iAllFromKeymaster = KeyProperties.Purpose.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_PURPOSE));
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterator<Integer> it = keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_PADDING).iterator();
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                try {
                    try {
                        arrayList.add(KeyProperties.EncryptionPadding.fromKeymaster(iIntValue));
                    } catch (IllegalArgumentException unused) {
                        arrayList2.add(KeyProperties.SignaturePadding.fromKeymaster(iIntValue));
                    }
                } catch (IllegalArgumentException unused2) {
                    throw new ProviderException("Unsupported encryption padding: " + iIntValue);
                }
            }
            String[] strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
            String[] strArr2 = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
            String[] strArrAllFromKeymaster = KeyProperties.Digest.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_DIGEST));
            String[] strArrAllFromKeymaster2 = KeyProperties.BlockMode.allFromKeymaster(keyCharacteristics.getEnums(KeymasterDefs.KM_TAG_BLOCK_MODE));
            int i3 = keyCharacteristics.swEnforced.getEnum(KeymasterDefs.KM_TAG_USER_AUTH_TYPE, 0);
            int i4 = keyCharacteristics.hwEnforced.getEnum(KeymasterDefs.KM_TAG_USER_AUTH_TYPE, 0);
            List<BigInteger> unsignedLongs = keyCharacteristics.getUnsignedLongs(KeymasterDefs.KM_TAG_USER_SECURE_ID);
            Date date = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_ACTIVE_DATETIME);
            Date date2 = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_ORIGINATION_EXPIRE_DATETIME);
            Date date3 = keyCharacteristics.getDate(KeymasterDefs.KM_TAG_USAGE_EXPIRE_DATETIME);
            boolean z2 = !keyCharacteristics.getBoolean(KeymasterDefs.KM_TAG_NO_AUTH_REQUIRED);
            long unsignedInt2 = keyCharacteristics.getUnsignedInt(KeymasterDefs.KM_TAG_AUTH_TIMEOUT, -1L);
            if (unsignedInt2 > 2147483647L) {
                throw new ProviderException("User authentication timeout validity too long: " + unsignedInt2 + " seconds");
            }
            return new KeyInfo(str, z, iFromKeymaster, i2, date, date2, date3, iAllFromKeymaster, strArr, strArr2, strArrAllFromKeymaster, strArrAllFromKeymaster2, z2, (int) unsignedInt2, z2 && i4 != 0 && i3 == 0, keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_ALLOW_WHILE_ON_BODY), keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_TRUSTED_USER_PRESENCE_REQUIRED), (!(i3 == 2 || i4 == 2) || unsignedLongs == null || unsignedLongs.isEmpty() || unsignedLongs.contains(getGateKeeperSecureUserId())) ? false : true, keyCharacteristics.hwEnforced.getBoolean(KeymasterDefs.KM_TAG_TRUSTED_CONFIRMATION_REQUIRED));
        } catch (IllegalArgumentException e) {
            throw new ProviderException("Unsupported key characteristic", e);
        }
    }

    private static BigInteger getGateKeeperSecureUserId() throws ProviderException {
        try {
            return BigInteger.valueOf(GateKeeper.getSecureUserId());
        } catch (IllegalStateException e) {
            throw new ProviderException("Failed to get GateKeeper secure user ID", e);
        }
    }

    @Override // javax.crypto.SecretKeyFactorySpi
    protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        throw new InvalidKeySpecException("To generate secret key in Android Keystore, use KeyGenerator initialized with " + KeyGenParameterSpec.class.getName());
    }

    @Override // javax.crypto.SecretKeyFactorySpi
    protected SecretKey engineTranslateKey(SecretKey secretKey) throws InvalidKeyException {
        if (secretKey == null) {
            throw new InvalidKeyException("key == null");
        }
        if (secretKey instanceof AndroidKeyStoreSecretKey) {
            return secretKey;
        }
        throw new InvalidKeyException("To import a secret key into Android Keystore, use KeyStore.setEntry");
    }
}
