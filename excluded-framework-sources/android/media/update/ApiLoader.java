package android.media.update;

/* JADX INFO: loaded from: classes.dex */
public final class ApiLoader {
    private ApiLoader() {
    }

    public static StaticProvider getProvider() {
        throw new RuntimeException("Use MediaSession/Browser instead of hidden MediaSession2/Browser2 APIs.");
    }
}
