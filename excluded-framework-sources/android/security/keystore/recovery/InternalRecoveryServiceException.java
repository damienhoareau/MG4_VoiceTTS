package android.security.keystore.recovery;

import android.annotation.SystemApi;
import java.security.GeneralSecurityException;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class InternalRecoveryServiceException extends GeneralSecurityException {
    public InternalRecoveryServiceException(String str) {
        super(str);
    }

    public InternalRecoveryServiceException(String str, Throwable th) {
        super(str, th);
    }
}
