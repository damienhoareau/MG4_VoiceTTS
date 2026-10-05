package android.security.keystore;

import java.security.Key;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStoreKey implements Key {
    private final String mAlgorithm;
    private final String mAlias;
    private final int mUid;

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    public AndroidKeyStoreKey(String str, int i, String str2) {
        this.mAlias = str;
        this.mUid = i;
        this.mAlgorithm = str2;
    }

    String getAlias() {
        return this.mAlias;
    }

    int getUid() {
        return this.mUid;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.mAlgorithm;
    }

    public int hashCode() {
        String str = this.mAlgorithm;
        int iHashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
        String str2 = this.mAlias;
        return ((iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + this.mUid;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AndroidKeyStoreKey androidKeyStoreKey = (AndroidKeyStoreKey) obj;
        String str = this.mAlgorithm;
        if (str == null) {
            if (androidKeyStoreKey.mAlgorithm != null) {
                return false;
            }
        } else if (!str.equals(androidKeyStoreKey.mAlgorithm)) {
            return false;
        }
        String str2 = this.mAlias;
        if (str2 == null) {
            if (androidKeyStoreKey.mAlias != null) {
                return false;
            }
        } else if (!str2.equals(androidKeyStoreKey.mAlias)) {
            return false;
        }
        return this.mUid == androidKeyStoreKey.mUid;
    }
}
