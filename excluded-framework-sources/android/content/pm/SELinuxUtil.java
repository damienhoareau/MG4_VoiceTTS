package android.content.pm;

/* JADX INFO: loaded from: classes.dex */
public final class SELinuxUtil {
    public static final String COMPLETE_STR = ":complete";
    private static final String INSTANT_APP_STR = ":ephemeralapp";

    public static String assignSeinfoUser(PackageUserState packageUserState) {
        return packageUserState.instantApp ? ":ephemeralapp:complete" : COMPLETE_STR;
    }
}
