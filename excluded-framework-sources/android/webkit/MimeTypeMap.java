package android.webkit;

import android.content.ClipDescription;
import android.text.TextUtils;
import java.util.regex.Pattern;
import libcore.net.MimeUtils;

/* JADX INFO: loaded from: classes2.dex */
public class MimeTypeMap {
    private static final MimeTypeMap sMimeTypeMap = new MimeTypeMap();

    private MimeTypeMap() {
    }

    public static String getFileExtensionFromUrl(String str) {
        int iLastIndexOf;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int iLastIndexOf2 = str.lastIndexOf(35);
        if (iLastIndexOf2 > 0) {
            str = str.substring(0, iLastIndexOf2);
        }
        int iLastIndexOf3 = str.lastIndexOf(63);
        if (iLastIndexOf3 > 0) {
            str = str.substring(0, iLastIndexOf3);
        }
        int iLastIndexOf4 = str.lastIndexOf(47);
        if (iLastIndexOf4 >= 0) {
            str = str.substring(iLastIndexOf4 + 1);
        }
        return (str.isEmpty() || !Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", str) || (iLastIndexOf = str.lastIndexOf(46)) < 0) ? "" : str.substring(iLastIndexOf + 1);
    }

    public boolean hasMimeType(String str) {
        return MimeUtils.hasMimeType(str);
    }

    public String getMimeTypeFromExtension(String str) {
        return MimeUtils.guessMimeTypeFromExtension(str);
    }

    private static String mimeTypeFromExtension(String str) {
        return MimeUtils.guessMimeTypeFromExtension(str);
    }

    public boolean hasExtension(String str) {
        return MimeUtils.hasExtension(str);
    }

    public String getExtensionFromMimeType(String str) {
        return MimeUtils.guessExtensionFromMimeType(str);
    }

    String remapGenericMimeType(String str, String str2, String str3) {
        if (ClipDescription.MIMETYPE_TEXT_PLAIN.equals(str) || "application/octet-stream".equals(str)) {
            String contentDisposition = str3 != null ? URLUtil.parseContentDisposition(str3) : null;
            if (contentDisposition != null) {
                str2 = contentDisposition;
            }
            String mimeTypeFromExtension = getMimeTypeFromExtension(getFileExtensionFromUrl(str2));
            return mimeTypeFromExtension != null ? mimeTypeFromExtension : str;
        }
        if ("text/vnd.wap.wml".equals(str)) {
            return ClipDescription.MIMETYPE_TEXT_PLAIN;
        }
        return "application/vnd.wap.xhtml+xml".equals(str) ? "application/xhtml+xml" : str;
    }

    public static MimeTypeMap getSingleton() {
        return sMimeTypeMap;
    }
}
