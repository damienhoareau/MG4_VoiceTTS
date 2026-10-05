package android.security.net.config;

import android.util.Pair;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public interface ConfigSource {
    NetworkSecurityConfig getDefaultConfig();

    Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs();
}
