package android.media.update;

/* JADX INFO: loaded from: classes.dex */
@FunctionalInterface
public interface ProviderCreator<T, U> {
    U createProvider(T t);
}
