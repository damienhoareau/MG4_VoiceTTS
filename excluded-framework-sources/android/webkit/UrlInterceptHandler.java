package android.webkit;

import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public interface UrlInterceptHandler {
    @Deprecated
    PluginData getPluginData(String str, Map<String, String> map);

    @Deprecated
    CacheManager.CacheResult service(String str, Map<String, String> map);
}
