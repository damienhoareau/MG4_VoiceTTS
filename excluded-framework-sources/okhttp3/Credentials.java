package okhttp3;

import android.provider.SettingsStringUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import okio.ByteString;

/* JADX INFO: loaded from: classes3.dex */
public final class Credentials {
    private Credentials() {
    }

    public static String basic(String str, String str2) {
        return basic(str, str2, StandardCharsets.ISO_8859_1);
    }

    public static String basic(String str, String str2, Charset charset) {
        return "Basic " + ByteString.encodeString(str + SettingsStringUtil.DELIMITER + str2, charset).base64();
    }
}
