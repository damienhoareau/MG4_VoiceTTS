package android.view.textclassifier;

import android.content.Context;
import android.metrics.LogMaker;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.Preconditions;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

/* JADX INFO: loaded from: classes2.dex */
public final class SelectionSessionLogger {
    static final String CLASSIFIER_ID = "androidtc";
    private static final boolean DEBUG_LOG_ENABLED = false;
    private static final int ENTITY_TYPE = 1254;
    private static final int EVENT_END = 1251;
    private static final int EVENT_START = 1250;
    private static final int INDEX = 1120;
    private static final String LOG_TAG = "SelectionSessionLogger";
    private static final int MODEL_NAME = 1256;
    private static final int PREV_EVENT_DELTA = 1118;
    private static final int SESSION_ID = 1119;
    private static final int SMART_END = 1253;
    private static final int SMART_START = 1252;
    private static final int START_EVENT_DELTA = 1117;
    private static final String UNKNOWN = "unknown";
    private static final int WIDGET_TYPE = 1255;
    private static final int WIDGET_VERSION = 1262;
    private static final String ZERO = "0";
    private final MetricsLogger mMetricsLogger;

    private static void debugLog(LogMaker logMaker) {
    }

    private static String getLogSubTypeString(int i) {
        if (i != 1) {
            return i != 2 ? "unknown" : "LINK";
        }
        return "MANUAL";
    }

    private static String getLogTypeString(int i) {
        switch (i) {
            case 1101:
                return "SELECTION_STARTED";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_MODIFY /* 1102 */:
                return "SELECTION_MODIFIED";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SELECT_ALL /* 1103 */:
                return "SELECT_ALL";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_RESET /* 1104 */:
                return "RESET";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_SINGLE /* 1105 */:
                return "SMART_SELECTION_SINGLE";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_MULTI /* 1106 */:
                return "SMART_SELECTION_MULTI";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_AUTO /* 1107 */:
                return "AUTO_SELECTION";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_OVERTYPE /* 1108 */:
                return "OVERTYPE";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_COPY /* 1109 */:
                return "COPY";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_PASTE /* 1110 */:
                return "PASTE";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_CUT /* 1111 */:
                return "CUT";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SHARE /* 1112 */:
                return "SHARE";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_SHARE /* 1113 */:
                return "SMART_SHARE";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_DRAG /* 1114 */:
                return "DRAG";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_ABANDON /* 1115 */:
                return "ABANDON";
            case MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_OTHER /* 1116 */:
                return "OTHER";
            default:
                return "unknown";
        }
    }

    public SelectionSessionLogger() {
        this.mMetricsLogger = new MetricsLogger();
    }

    public SelectionSessionLogger(MetricsLogger metricsLogger) {
        this.mMetricsLogger = (MetricsLogger) Preconditions.checkNotNull(metricsLogger);
    }

    public void writeEvent(SelectionEvent selectionEvent) {
        Preconditions.checkNotNull(selectionEvent);
        LogMaker logMakerAddTaggedData = new LogMaker(1100).setType(getLogType(selectionEvent)).setSubtype(getLogSubType(selectionEvent)).setPackageName(selectionEvent.getPackageName()).addTaggedData(1117, Long.valueOf(selectionEvent.getDurationSinceSessionStart())).addTaggedData(1118, Long.valueOf(selectionEvent.getDurationSincePreviousEvent())).addTaggedData(1120, Integer.valueOf(selectionEvent.getEventIndex())).addTaggedData(1255, selectionEvent.getWidgetType()).addTaggedData(1262, selectionEvent.getWidgetVersion()).addTaggedData(1256, SignatureParser.getModelName(selectionEvent.getResultId())).addTaggedData(1254, selectionEvent.getEntityType()).addTaggedData(1252, Integer.valueOf(selectionEvent.getSmartStart())).addTaggedData(1253, Integer.valueOf(selectionEvent.getSmartEnd())).addTaggedData(1250, Integer.valueOf(selectionEvent.getStart())).addTaggedData(1251, Integer.valueOf(selectionEvent.getEnd()));
        if (selectionEvent.getSessionId() != null) {
            logMakerAddTaggedData.addTaggedData(1119, selectionEvent.getSessionId().flattenToString());
        }
        this.mMetricsLogger.write(logMakerAddTaggedData);
        debugLog(logMakerAddTaggedData);
    }

    private static int getLogType(SelectionEvent selectionEvent) {
        int eventType = selectionEvent.getEventType();
        if (eventType == 1) {
            return 1101;
        }
        if (eventType == 2) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_MODIFY;
        }
        if (eventType == 3) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_SINGLE;
        }
        if (eventType == 4) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_MULTI;
        }
        if (eventType == 5) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_AUTO;
        }
        if (eventType == 200) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SELECT_ALL;
        }
        if (eventType == 201) {
            return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_RESET;
        }
        switch (eventType) {
            case 100:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_OVERTYPE;
            case 101:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_COPY;
            case 102:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_PASTE;
            case 103:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_CUT;
            case 104:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SHARE;
            case 105:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_SMART_SHARE;
            case 106:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_DRAG;
            case 107:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_ABANDON;
            case 108:
                return MetricsProto.MetricsEvent.ACTION_TEXT_SELECTION_OTHER;
            default:
                return 0;
        }
    }

    private static int getLogSubType(SelectionEvent selectionEvent) {
        int invocationMethod = selectionEvent.getInvocationMethod();
        int i = 1;
        if (invocationMethod != 1) {
            i = 2;
            if (invocationMethod != 2) {
                return 0;
            }
        }
        return i;
    }

    public static BreakIterator getTokenIterator(Locale locale) {
        return BreakIterator.getWordInstance((Locale) Preconditions.checkNotNull(locale));
    }

    public static String createId(String str, int i, int i2, Context context, int i3, List<Locale> list) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(list);
        StringJoiner stringJoiner = new StringJoiner(",");
        Iterator<Locale> it = list.iterator();
        while (it.hasNext()) {
            stringJoiner.add(it.next().toLanguageTag());
        }
        return SignatureParser.createSignature("androidtc", String.format(Locale.US, "%s_v%d", stringJoiner.toString(), Integer.valueOf(i3)), Objects.hash(str, Integer.valueOf(i), Integer.valueOf(i2), context.getPackageName()));
    }

    public static final class SignatureParser {
        static String createSignature(String str, String str2, int i) {
            return String.format(Locale.US, "%s|%s|%d", str, str2, Integer.valueOf(i));
        }

        static String getClassifierId(String str) {
            int iIndexOf;
            return (str != null && (iIndexOf = str.indexOf("|")) >= 0) ? str.substring(0, iIndexOf) : "";
        }

        static String getModelName(String str) {
            if (str == null) {
                return "";
            }
            int iIndexOf = str.indexOf("|") + 1;
            int iIndexOf2 = str.indexOf("|", iIndexOf);
            return (iIndexOf < 1 || iIndexOf2 < iIndexOf) ? "" : str.substring(iIndexOf, iIndexOf2);
        }

        static int getHash(String str) {
            int iIndexOf;
            if (str != null && (iIndexOf = str.indexOf("|", str.indexOf("|"))) > 0) {
                return Integer.parseInt(str.substring(iIndexOf));
            }
            return 0;
        }
    }
}
