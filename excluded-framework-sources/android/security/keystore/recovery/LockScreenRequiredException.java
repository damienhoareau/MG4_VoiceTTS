package android.security.keystore.recovery;

import android.annotation.SystemApi;
import java.security.GeneralSecurityException;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class LockScreenRequiredException extends GeneralSecurityException {
    public LockScreenRequiredException(String str) {
        super(str);
    }
}
