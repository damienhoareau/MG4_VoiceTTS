package android.security.keystore.recovery;

import android.annotation.SystemApi;
import java.security.GeneralSecurityException;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class DecryptionFailedException extends GeneralSecurityException {
    public DecryptionFailedException(String str) {
        super(str);
    }
}
