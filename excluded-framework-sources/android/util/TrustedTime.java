package android.util;

/* JADX INFO: loaded from: classes2.dex */
public interface TrustedTime {
    long currentTimeMillis();

    boolean forceRefresh();

    long getCacheAge();

    long getCacheCertainty();

    boolean hasCache();
}
