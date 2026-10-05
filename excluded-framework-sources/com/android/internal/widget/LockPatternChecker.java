package com.android.internal.widget;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class LockPatternChecker {

    public interface OnCheckCallback {
        default void onCancelled() {
        }

        void onChecked(boolean z, int i);

        default void onEarlyMatched() {
        }
    }

    public interface OnVerifyCallback {
        void onVerified(byte[] bArr, int i);
    }

    public static AsyncTask<?, ?, ?> verifyPattern(final LockPatternUtils lockPatternUtils, final List<LockPatternView.Cell> list, final long j, final int i, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.internal.widget.LockPatternChecker.1
            private int mThrottleTimeout;
            private List<LockPatternView.Cell> patternCopy;

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.patternCopy = new ArrayList(list);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    return lockPatternUtils.verifyPattern(this.patternCopy, j, i);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> checkPattern(final LockPatternUtils lockPatternUtils, final List<LockPatternView.Cell> list, final int i, final OnCheckCallback onCheckCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.internal.widget.LockPatternChecker.2
            private int mThrottleTimeout;
            private List<LockPatternView.Cell> patternCopy;

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.patternCopy = new ArrayList(list);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    LockPatternUtils lockPatternUtils2 = lockPatternUtils;
                    List<LockPatternView.Cell> list2 = this.patternCopy;
                    int i2 = i;
                    OnCheckCallback onCheckCallback2 = onCheckCallback;
                    Objects.requireNonNull(onCheckCallback2);
                    return Boolean.valueOf(lockPatternUtils2.checkPattern(list2, i2, new $$Lambda$TTC7hNz7BTsLwhNRb2L5kl7mdU(onCheckCallback2)));
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return false;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckCallback.onChecked(bool.booleanValue(), this.mThrottleTimeout);
            }

            @Override // android.os.AsyncTask
            protected void onCancelled() {
                onCheckCallback.onCancelled();
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> verifyPassword(final LockPatternUtils lockPatternUtils, final String str, final long j, final int i, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.internal.widget.LockPatternChecker.3
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    return lockPatternUtils.verifyPassword(str, j, i);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> verifyTiedProfileChallenge(final LockPatternUtils lockPatternUtils, final String str, final boolean z, final long j, final int i, final OnVerifyCallback onVerifyCallback) {
        AsyncTask<Void, Void, byte[]> asyncTask = new AsyncTask<Void, Void, byte[]>() { // from class: com.android.internal.widget.LockPatternChecker.4
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public byte[] doInBackground(Void... voidArr) {
                try {
                    return lockPatternUtils.verifyTiedProfileChallenge(str, z, j, i);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(byte[] bArr) {
                onVerifyCallback.onVerified(bArr, this.mThrottleTimeout);
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }

    public static AsyncTask<?, ?, ?> checkPassword(final LockPatternUtils lockPatternUtils, final String str, final int i, final OnCheckCallback onCheckCallback) {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() { // from class: com.android.internal.widget.LockPatternChecker.5
            private int mThrottleTimeout;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    LockPatternUtils lockPatternUtils2 = lockPatternUtils;
                    String str2 = str;
                    int i2 = i;
                    OnCheckCallback onCheckCallback2 = onCheckCallback;
                    Objects.requireNonNull(onCheckCallback2);
                    return Boolean.valueOf(lockPatternUtils2.checkPassword(str2, i2, new $$Lambda$TTC7hNz7BTsLwhNRb2L5kl7mdU(onCheckCallback2)));
                } catch (LockPatternUtils.RequestThrottledException e) {
                    this.mThrottleTimeout = e.getTimeoutMs();
                    return false;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                onCheckCallback.onChecked(bool.booleanValue(), this.mThrottleTimeout);
            }

            @Override // android.os.AsyncTask
            protected void onCancelled() {
                onCheckCallback.onCancelled();
            }
        };
        asyncTask.execute(new Void[0]);
        return asyncTask;
    }
}
