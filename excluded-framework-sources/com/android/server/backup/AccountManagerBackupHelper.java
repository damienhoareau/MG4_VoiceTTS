package com.android.server.backup;

import android.accounts.AccountManagerInternal;
import android.app.backup.BlobBackupHelper;
import android.util.Slog;
import com.android.server.LocalServices;

/* JADX INFO: loaded from: classes3.dex */
public class AccountManagerBackupHelper extends BlobBackupHelper {
    private static final boolean DEBUG = false;
    private static final String KEY_ACCOUNT_ACCESS_GRANTS = "account_access_grants";
    private static final int STATE_VERSION = 1;
    private static final String TAG = "AccountsBackup";

    public AccountManagerBackupHelper() {
        super(1, KEY_ACCOUNT_ACCESS_GRANTS);
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String str) {
        AccountManagerInternal accountManagerInternal = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
        byte b = -1;
        try {
            if (str.hashCode() == 1544100736 && str.equals(KEY_ACCOUNT_ACCESS_GRANTS)) {
                b = 0;
            }
            if (b == 0) {
                return accountManagerInternal.backupAccountAccessPermissions(0);
            }
            Slog.w(TAG, "Unexpected backup key " + str);
            return new byte[0];
        } catch (Exception unused) {
            Slog.e(TAG, "Unable to store payload " + str);
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String str, byte[] bArr) {
        AccountManagerInternal accountManagerInternal = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
        byte b = -1;
        try {
            if (str.hashCode() == 1544100736 && str.equals(KEY_ACCOUNT_ACCESS_GRANTS)) {
                b = 0;
            }
            if (b == 0) {
                accountManagerInternal.restoreAccountAccessPermissions(bArr, 0);
                return;
            }
            Slog.w(TAG, "Unexpected restore key " + str);
        } catch (Exception unused) {
            Slog.w(TAG, "Unable to restore key " + str);
        }
    }
}
