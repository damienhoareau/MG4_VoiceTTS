package android.util;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DebugUtils {
    public static boolean isObjectSelected(Object obj) {
        Method declaredMethod;
        String str = System.getenv("ANDROID_OBJECT_FILTER");
        if (str == null || str.length() <= 0) {
            return false;
        }
        String[] strArrSplit = str.split("@");
        if (!obj.getClass().getSimpleName().matches(strArrSplit[0])) {
            return false;
        }
        boolean zMatches = false;
        for (int i = 1; i < strArrSplit.length; i++) {
            String[] strArrSplit2 = strArrSplit[i].split("=");
            Class<?> cls = obj.getClass();
            Class<?> cls2 = cls;
            while (true) {
                try {
                    declaredMethod = cls2.getDeclaredMethod("get" + strArrSplit2[0].substring(0, 1).toUpperCase(Locale.ROOT) + strArrSplit2[0].substring(1), (Class[]) null);
                    Class<? super Object> superclass = cls.getSuperclass();
                    if (superclass == null || declaredMethod != null) {
                        break;
                        break;
                    }
                    cls2 = superclass;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
            if (declaredMethod != null) {
                Object objInvoke = declaredMethod.invoke(obj, (Object[]) null);
                zMatches |= (objInvoke != null ? objInvoke.toString() : "null").matches(strArrSplit2[1]);
            }
        }
        return zMatches;
    }

    public static void buildShortClassTag(Object obj, StringBuilder sb) {
        int iLastIndexOf;
        if (obj == null) {
            sb.append("null");
            return;
        }
        String simpleName = obj.getClass().getSimpleName();
        if ((simpleName == null || simpleName.isEmpty()) && (iLastIndexOf = (simpleName = obj.getClass().getName()).lastIndexOf(46)) > 0) {
            simpleName = simpleName.substring(iLastIndexOf + 1);
        }
        sb.append(simpleName);
        sb.append('{');
        sb.append(Integer.toHexString(System.identityHashCode(obj)));
    }

    public static void printSizeValue(PrintWriter printWriter, long j) {
        String str;
        String str2;
        float f = j;
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "KB";
        } else {
            str = "";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "MB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "GB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "TB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "PB";
        }
        if (f < 1.0f) {
            str2 = String.format("%.2f", Float.valueOf(f));
        } else if (f < 10.0f) {
            str2 = String.format("%.1f", Float.valueOf(f));
        } else {
            str2 = f < 100.0f ? String.format("%.0f", Float.valueOf(f)) : String.format("%.0f", Float.valueOf(f));
        }
        printWriter.print(str2);
        printWriter.print(str);
    }

    public static String sizeValueToString(long j, StringBuilder sb) {
        String str;
        String str2;
        if (sb == null) {
            sb = new StringBuilder(32);
        }
        float f = j;
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "KB";
        } else {
            str = "";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "MB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "GB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "TB";
        }
        if (f > 900.0f) {
            f /= 1024.0f;
            str = "PB";
        }
        if (f < 1.0f) {
            str2 = String.format("%.2f", Float.valueOf(f));
        } else if (f < 10.0f) {
            str2 = String.format("%.1f", Float.valueOf(f));
        } else {
            str2 = f < 100.0f ? String.format("%.0f", Float.valueOf(f)) : String.format("%.0f", Float.valueOf(f));
        }
        sb.append(str2);
        sb.append(str);
        return sb.toString();
    }

    public static String valueToString(Class<?> cls, String str, int i) {
        Field[] declaredFields = cls.getDeclaredFields();
        int length = declaredFields.length;
        for (int i2 = 0; i2 < length; i2++) {
            Field field = declaredFields[i2];
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getType().equals(Integer.TYPE) && field.getName().startsWith(str)) {
                try {
                    if (i == field.getInt(null)) {
                        return constNameWithoutPrefix(str, field);
                    }
                    continue;
                } catch (IllegalAccessException unused) {
                    continue;
                }
            }
        }
        return Integer.toString(i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v10, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v8 */
    /* JADX WARN: Type inference failed for: r8v9 */
    public static String flagsToString(Class<?> cls, String str, int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        boolean z = i == 0;
        Field[] declaredFields = cls.getDeclaredFields();
        int length = declaredFields.length;
        ?? ConstNameWithoutPrefix = declaredFields;
        while (i2 < length) {
            Field field = ConstNameWithoutPrefix[i2];
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && field.getType().equals(Integer.TYPE) && field.getName().startsWith(str)) {
                try {
                    int i3 = field.getInt(null);
                    if (i3 == 0 && z) {
                        ConstNameWithoutPrefix = constNameWithoutPrefix(str, field);
                        return ConstNameWithoutPrefix;
                    }
                    if ((i & i3) != 0) {
                        i &= ~i3;
                        sb.append(constNameWithoutPrefix(str, field));
                        sb.append('|');
                    }
                } catch (IllegalAccessException unused) {
                    continue;
                }
            }
            i2++;
            ConstNameWithoutPrefix = ConstNameWithoutPrefix;
        }
        if (i != 0 || sb.length() == 0) {
            sb.append(Integer.toHexString(i));
        } else {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String constNameWithoutPrefix(String str, Field field) {
        return field.getName().substring(str.length());
    }
}
