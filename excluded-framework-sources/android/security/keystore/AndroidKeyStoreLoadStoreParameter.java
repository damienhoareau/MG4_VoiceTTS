package android.security.keystore;

import java.security.KeyStore;

/* JADX INFO: loaded from: classes2.dex */
class AndroidKeyStoreLoadStoreParameter implements KeyStore.LoadStoreParameter {
    private final int mUid;

    @Override // java.security.KeyStore.LoadStoreParameter
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return null;
    }

    AndroidKeyStoreLoadStoreParameter(int i) {
        this.mUid = i;
    }

    int getUid() {
        return this.mUid;
    }
}
