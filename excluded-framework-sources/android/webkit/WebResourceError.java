package android.webkit;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes2.dex */
public abstract class WebResourceError {
    public abstract CharSequence getDescription();

    public abstract int getErrorCode();

    @SystemApi
    public WebResourceError() {
    }
}
