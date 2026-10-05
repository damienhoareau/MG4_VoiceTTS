package okhttp3;

import java.io.IOException;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public interface Authenticator {
    public static final Authenticator NONE = new Authenticator() { // from class: okhttp3.-$$Lambda$Authenticator$xBBU2iHkJpDKH0vhaB2vteUyEoc
        @Override // okhttp3.Authenticator
        public final Request authenticate(Route route, Response response) {
            return Authenticator.lambda$static$0(route, response);
        }
    };

    static /* synthetic */ Request lambda$static$0(Route route, Response response) throws IOException {
        return null;
    }

    @Nullable
    Request authenticate(@Nullable Route route, Response response) throws IOException;
}
