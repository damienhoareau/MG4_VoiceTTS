package com.android.internal.app.procstats;

import android.accounts.GrantCredentialsPermissionActivity;
import android.app.backup.FullBackup;
import android.content.Context;
import android.hardware.Camera;
import android.media.TtmlUtils;
import android.os.UserHandle;
import android.provider.CalendarContract;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.app.DumpHeapActivity;
import com.android.internal.content.NativeLibraryHelper;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public final class DumpUtils {
    static final String CSV_SEP = "\t";
    public static final String[] STATE_NAMES = {"Persist", "Top", "ImpFg", "ImpBg", "Backup", "Service", "ServRst", "Receivr", "HeavyWt", "Home", "LastAct", "CchAct", "CchCAct", "CchEmty"};
    public static final String[] STATE_NAMES_CSV = {"pers", "top", "impfg", "impbg", Context.BACKUP_SERVICE, "service", "service-rs", "receiver", "heavy", CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME, "lastact", "cch-activity", "cch-aclient", "cch-empty"};
    static final String[] STATE_TAGS = {TtmlUtils.TAG_P, "t", FullBackup.FILES_TREE_TOKEN, "b", "u", "s", "x", FullBackup.ROOT_TREE_TOKEN, "w", "h", "l", FullBackup.APK_TREE_TOKEN, FullBackup.CACHE_TREE_TOKEN, "e"};
    static final int[] STATE_PROTO_ENUMS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    public static final String[] ADJ_SCREEN_NAMES_CSV = {"off", Camera.Parameters.FLASH_MODE_ON};
    public static final String[] ADJ_MEM_NAMES_CSV = {"norm", "mod", "low", "crit"};
    static final String[] ADJ_SCREEN_TAGS = {"0", "1"};
    static final int[] ADJ_SCREEN_PROTO_ENUMS = {1, 2};
    static final String[] ADJ_MEM_TAGS = {"n", "m", "l", FullBackup.CACHE_TREE_TOKEN};
    static final int[] ADJ_MEM_PROTO_ENUMS = {1, 2, 3, 4};

    private DumpUtils() {
    }

    public static void printScreenLabel(PrintWriter printWriter, int i) {
        if (i == -1) {
            printWriter.print("     ");
            return;
        }
        if (i == 0) {
            printWriter.print("SOff/");
        } else if (i == 4) {
            printWriter.print("SOn /");
        } else {
            printWriter.print("????/");
        }
    }

    public static void printScreenLabelCsv(PrintWriter printWriter, int i) {
        if (i != -1) {
            if (i == 0) {
                printWriter.print(ADJ_SCREEN_NAMES_CSV[0]);
            } else if (i == 4) {
                printWriter.print(ADJ_SCREEN_NAMES_CSV[1]);
            } else {
                printWriter.print("???");
            }
        }
    }

    public static void printMemLabel(PrintWriter printWriter, int i, char c) {
        if (i == -1) {
            printWriter.print("    ");
            if (c != 0) {
                printWriter.print(' ');
                return;
            }
            return;
        }
        if (i == 0) {
            printWriter.print("Norm");
            if (c != 0) {
                printWriter.print(c);
                return;
            }
            return;
        }
        if (i == 1) {
            printWriter.print("Mod ");
            if (c != 0) {
                printWriter.print(c);
                return;
            }
            return;
        }
        if (i == 2) {
            printWriter.print("Low ");
            if (c != 0) {
                printWriter.print(c);
                return;
            }
            return;
        }
        if (i == 3) {
            printWriter.print("Crit");
            if (c != 0) {
                printWriter.print(c);
                return;
            }
            return;
        }
        printWriter.print("????");
        if (c != 0) {
            printWriter.print(c);
        }
    }

    public static void printMemLabelCsv(PrintWriter printWriter, int i) {
        if (i >= 0) {
            if (i <= 3) {
                printWriter.print(ADJ_MEM_NAMES_CSV[i]);
            } else {
                printWriter.print("???");
            }
        }
    }

    public static void printPercent(PrintWriter printWriter, double d) {
        double d2 = d * 100.0d;
        if (d2 < 1.0d) {
            printWriter.print(String.format("%.2f", Double.valueOf(d2)));
        } else if (d2 < 10.0d) {
            printWriter.print(String.format("%.1f", Double.valueOf(d2)));
        } else {
            printWriter.print(String.format("%.0f", Double.valueOf(d2)));
        }
        printWriter.print("%");
    }

    public static void printProcStateTag(PrintWriter printWriter, int i) {
        printArrayEntry(printWriter, STATE_TAGS, printArrayEntry(printWriter, ADJ_MEM_TAGS, printArrayEntry(printWriter, ADJ_SCREEN_TAGS, i, 56), 14), 1);
    }

    public static void printProcStateTagProto(ProtoOutputStream protoOutputStream, long j, long j2, long j3, int i) {
        printProto(protoOutputStream, j3, STATE_PROTO_ENUMS, printProto(protoOutputStream, j2, ADJ_MEM_PROTO_ENUMS, printProto(protoOutputStream, j, ADJ_SCREEN_PROTO_ENUMS, i, 56), 14), 1);
    }

    public static void printAdjTag(PrintWriter printWriter, int i) {
        printArrayEntry(printWriter, ADJ_MEM_TAGS, printArrayEntry(printWriter, ADJ_SCREEN_TAGS, i, 4), 1);
    }

    public static void printProcStateTagAndValue(PrintWriter printWriter, int i, long j) {
        printWriter.print(',');
        printProcStateTag(printWriter, i);
        printWriter.print(':');
        printWriter.print(j);
    }

    public static void printAdjTagAndValue(PrintWriter printWriter, int i, long j) {
        printWriter.print(',');
        printAdjTag(printWriter, i);
        printWriter.print(':');
        printWriter.print(j);
    }

    public static long dumpSingleTime(PrintWriter printWriter, String str, long[] jArr, int i, long j, long j2) {
        int i2 = 0;
        long j3 = 0;
        int i3 = -1;
        while (i2 < 8) {
            int i4 = 0;
            int i5 = -1;
            while (i4 < 4) {
                int i6 = i4 + i2;
                long j4 = jArr[i6];
                String str2 = "";
                if (i == i6) {
                    j4 += j2 - j;
                    if (printWriter != null) {
                        str2 = " (running)";
                    }
                }
                if (j4 != 0) {
                    if (printWriter != null) {
                        printWriter.print(str);
                        printScreenLabel(printWriter, i3 != i2 ? i2 : -1);
                        printMemLabel(printWriter, i5 != i4 ? i4 : -1, (char) 0);
                        printWriter.print(": ");
                        TimeUtils.formatDuration(j4, printWriter);
                        printWriter.println(str2);
                        i3 = i2;
                        i5 = i4;
                    }
                    j3 += j4;
                }
                i4++;
            }
            i2 += 4;
        }
        if (j3 != 0 && printWriter != null) {
            printWriter.print(str);
            printWriter.print("    TOTAL: ");
            TimeUtils.formatDuration(j3, printWriter);
            printWriter.println();
        }
        return j3;
    }

    public static void dumpAdjTimesCheckin(PrintWriter printWriter, String str, long[] jArr, int i, long j, long j2) {
        for (int i2 = 0; i2 < 8; i2 += 4) {
            for (int i3 = 0; i3 < 4; i3++) {
                int i4 = i3 + i2;
                long j3 = jArr[i4];
                if (i == i4) {
                    j3 += j2 - j;
                }
                if (j3 != 0) {
                    printAdjTagAndValue(printWriter, i4, j3);
                }
            }
        }
    }

    private static void dumpStateHeadersCsv(PrintWriter printWriter, String str, int[] iArr, int[] iArr2, int[] iArr3) {
        boolean z;
        int length = iArr != null ? iArr.length : 1;
        int length2 = iArr2 != null ? iArr2.length : 1;
        int length3 = iArr3 != null ? iArr3.length : 1;
        for (int i = 0; i < length; i++) {
            for (int i2 = 0; i2 < length2; i2++) {
                for (int i3 = 0; i3 < length3; i3++) {
                    printWriter.print(str);
                    if (iArr == null || iArr.length <= 1) {
                        z = false;
                    } else {
                        printScreenLabelCsv(printWriter, iArr[i]);
                        z = true;
                    }
                    if (iArr2 != null && iArr2.length > 1) {
                        if (z) {
                            printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        printMemLabelCsv(printWriter, iArr2[i2]);
                        z = true;
                    }
                    if (iArr3 != null && iArr3.length > 1) {
                        if (z) {
                            printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        printWriter.print(STATE_NAMES_CSV[iArr3[i3]]);
                    }
                }
            }
        }
    }

    public static void dumpProcessSummaryLocked(PrintWriter printWriter, String str, ArrayList<ProcessState> arrayList, int[] iArr, int[] iArr2, int[] iArr3, long j, long j2) {
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            arrayList.get(size).dumpSummary(printWriter, str, iArr, iArr2, iArr3, j, j2);
        }
    }

    public static void dumpProcessListCsv(PrintWriter printWriter, ArrayList<ProcessState> arrayList, boolean z, int[] iArr, boolean z2, int[] iArr2, boolean z3, int[] iArr3, long j) {
        printWriter.print(DumpHeapActivity.KEY_PROCESS);
        printWriter.print(CSV_SEP);
        printWriter.print(GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
        printWriter.print(CSV_SEP);
        printWriter.print("vers");
        dumpStateHeadersCsv(printWriter, CSV_SEP, z ? iArr : null, z2 ? iArr2 : null, z3 ? iArr3 : null);
        printWriter.println();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ProcessState processState = arrayList.get(size);
            printWriter.print(processState.getName());
            printWriter.print(CSV_SEP);
            UserHandle.formatUid(printWriter, processState.getUid());
            printWriter.print(CSV_SEP);
            printWriter.print(processState.getVersion());
            processState.dumpCsv(printWriter, z, iArr, z2, iArr2, z3, iArr3, j);
            printWriter.println();
        }
    }

    public static int printArrayEntry(PrintWriter printWriter, String[] strArr, int i, int i2) {
        int i3 = i / i2;
        if (i3 >= 0 && i3 < strArr.length) {
            printWriter.print(strArr[i3]);
        } else {
            printWriter.print('?');
        }
        return i - (i3 * i2);
    }

    public static int printProto(ProtoOutputStream protoOutputStream, long j, int[] iArr, int i, int i2) {
        int i3 = i / i2;
        if (i3 >= 0 && i3 < iArr.length) {
            protoOutputStream.write(j, iArr[i3]);
        }
        return i - (i3 * i2);
    }

    public static String collapseString(String str, String str2) {
        if (str2.startsWith(str)) {
            int length = str2.length();
            int length2 = str.length();
            if (length == length2) {
                return "";
            }
            if (length >= length2 && str2.charAt(length2) == '.') {
                return str2.substring(length2);
            }
        }
        return str2;
    }
}
