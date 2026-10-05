package android.security.keystore;

import java.security.PublicKey;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStorePublicKey extends AndroidKeyStoreKey implements PublicKey {
    private final byte[] mEncoded;

    @Override // android.security.keystore.AndroidKeyStoreKey, java.security.Key
    public String getFormat() {
        return "X.509";
    }

    public AndroidKeyStorePublicKey(String str, int i, String str2, byte[] bArr) {
        super(str, i, str2);
        this.mEncoded = ArrayUtils.cloneIfNotEmpty(bArr);
    }

    @Override // android.security.keystore.AndroidKeyStoreKey, java.security.Key
    public byte[] getEncoded() {
        return ArrayUtils.cloneIfNotEmpty(this.mEncoded);
    }

    @Override // android.security.keystore.AndroidKeyStoreKey
    public int hashCode() {
        return (super.hashCode() * 31) + Arrays.hashCode(this.mEncoded);
    }

    @Override // android.security.keystore.AndroidKeyStoreKey
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return super.equals(obj) && getClass() == obj.getClass() && Arrays.equals(this.mEncoded, ((AndroidKeyStorePublicKey) obj).mEncoded);
    }
}
