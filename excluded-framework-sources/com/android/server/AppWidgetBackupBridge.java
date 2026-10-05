package com.android.server;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class AppWidgetBackupBridge {
    private static WidgetBackupProvider sAppWidgetService;

    public static void register(WidgetBackupProvider widgetBackupProvider) {
        sAppWidgetService = widgetBackupProvider;
    }

    public static List<String> getWidgetParticipants(int i) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            return widgetBackupProvider.getWidgetParticipants(i);
        }
        return null;
    }

    public static byte[] getWidgetState(String str, int i) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            return widgetBackupProvider.getWidgetState(str, i);
        }
        return null;
    }

    public static void restoreStarting(int i) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.restoreStarting(i);
        }
    }

    public static void restoreWidgetState(String str, byte[] bArr, int i) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.restoreWidgetState(str, bArr, i);
        }
    }

    public static void restoreFinished(int i) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.restoreFinished(i);
        }
    }
}
