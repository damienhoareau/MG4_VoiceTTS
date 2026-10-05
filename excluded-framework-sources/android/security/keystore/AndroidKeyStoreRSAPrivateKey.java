package android.security.keystore;

import java.math.BigInteger;
import java.security.interfaces.RSAKey;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStoreRSAPrivateKey extends AndroidKeyStorePrivateKey implements RSAKey {
    private final BigInteger mModulus;

    public AndroidKeyStoreRSAPrivateKey(String str, int i, BigInteger bigInteger) {
        super(str, i, KeyProperties.KEY_ALGORITHM_RSA);
        this.mModulus = bigInteger;
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.mModulus;
    }
}
