package android.app.backup;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public abstract class BackupObserver {
    public void backupFinished(int i) {
    }

    public void onResult(String str, int i) {
    }

    public void onUpdate(String str, BackupProgress backupProgress) {
    }
}
