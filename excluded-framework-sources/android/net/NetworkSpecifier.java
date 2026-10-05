package android.net;

/* JADX INFO: loaded from: classes.dex */
public abstract class NetworkSpecifier {
    public void assertValidFromUid(int i) {
    }

    public abstract boolean satisfiedBy(NetworkSpecifier networkSpecifier);
}
