package com.saicmotor.voicetts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class LaunchUtils {
    private static Map<String, String> mActivityMap = new HashMap();

    public static String getAppLauncherActivity(String str, Context context) {
        String str2 = mActivityMap.get(str);
        if (str2 != null) {
            return str2;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN, (Uri) null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        if (listQueryIntentActivities == null || listQueryIntentActivities.size() <= 0) {
            return str2;
        }
        String str3 = listQueryIntentActivities.get(0).activityInfo.name;
        mActivityMap.put(str, str3);
        return str3;
    }

    public static String getAppService(String str, Context context) {
        Intent intent = new Intent();
        intent.setPackage(str);
        List<ResolveInfo> listQueryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        if (listQueryIntentServices == null || listQueryIntentServices.size() <= 0) {
            return null;
        }
        return listQueryIntentServices.get(0).serviceInfo.name;
    }
}
