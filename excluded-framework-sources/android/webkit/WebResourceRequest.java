package android.webkit;

import android.net.Uri;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public interface WebResourceRequest {
    String getMethod();

    Map<String, String> getRequestHeaders();

    Uri getUrl();

    boolean hasGesture();

    boolean isForMainFrame();

    boolean isRedirect();
}
