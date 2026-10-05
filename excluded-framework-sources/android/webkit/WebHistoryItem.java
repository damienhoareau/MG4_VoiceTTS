package android.webkit;

import android.annotation.SystemApi;
import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes2.dex */
public abstract class WebHistoryItem implements Cloneable {
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public abstract WebHistoryItem m38clone();

    public abstract Bitmap getFavicon();

    @SystemApi
    @Deprecated
    public abstract int getId();

    public abstract String getOriginalUrl();

    public abstract String getTitle();

    public abstract String getUrl();
}
