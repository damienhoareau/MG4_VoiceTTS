package android.app.timezone;

/* JADX INFO: loaded from: classes.dex */
final class Utils {
    private Utils() {
    }

    static int validateVersion(String str, int i) {
        if (i >= 0 && i <= 999) {
            return i;
        }
        throw new IllegalArgumentException("Invalid " + str + " version=" + i);
    }

    static String validateRulesVersion(String str, String str2) {
        validateNotNull(str, str2);
        if (!str2.isEmpty()) {
            return str2;
        }
        throw new IllegalArgumentException(str + " must not be empty");
    }

    static <T> T validateNotNull(String str, T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str + " == null");
    }

    static <T> T validateConditionalNull(boolean z, String str, T t) {
        if (z) {
            return (T) validateNotNull(str, t);
        }
        return (T) validateNull(str, t);
    }

    static <T> T validateNull(String str, T t) {
        if (t == null) {
            return null;
        }
        throw new IllegalArgumentException(str + " != null");
    }
}
