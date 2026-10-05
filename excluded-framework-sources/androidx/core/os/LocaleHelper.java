package androidx.core.os;

import android.telecom.Logging.Session;
import com.android.internal.content.NativeLibraryHelper;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
final class LocaleHelper {
    static Locale forLanguageTag(String str) {
        if (str.contains(NativeLibraryHelper.CLEAR_ABI_OVERRIDE)) {
            String[] strArrSplit = str.split(NativeLibraryHelper.CLEAR_ABI_OVERRIDE, -1);
            if (strArrSplit.length > 2) {
                return new Locale(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
            }
            if (strArrSplit.length > 1) {
                return new Locale(strArrSplit[0], strArrSplit[1]);
            }
            if (strArrSplit.length == 1) {
                return new Locale(strArrSplit[0]);
            }
        } else if (str.contains(Session.SESSION_SEPARATION_CHAR_CHILD)) {
            String[] strArrSplit2 = str.split(Session.SESSION_SEPARATION_CHAR_CHILD, -1);
            if (strArrSplit2.length > 2) {
                return new Locale(strArrSplit2[0], strArrSplit2[1], strArrSplit2[2]);
            }
            if (strArrSplit2.length > 1) {
                return new Locale(strArrSplit2[0], strArrSplit2[1]);
            }
            if (strArrSplit2.length == 1) {
                return new Locale(strArrSplit2[0]);
            }
        } else {
            return new Locale(str);
        }
        throw new IllegalArgumentException("Can not parse language tag: [" + str + "]");
    }

    static String toLanguageTag(Locale locale) {
        StringBuilder sb = new StringBuilder();
        sb.append(locale.getLanguage());
        String country = locale.getCountry();
        if (country != null && !country.isEmpty()) {
            sb.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            sb.append(locale.getCountry());
        }
        return sb.toString();
    }

    private LocaleHelper() {
    }
}
