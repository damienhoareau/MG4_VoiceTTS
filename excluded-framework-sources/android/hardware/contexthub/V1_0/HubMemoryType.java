package android.hardware.contexthub.V1_0;

import android.app.CarConfigManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class HubMemoryType {
    public static final int MAIN = 0;
    public static final int SECONDARY = 1;
    public static final int TCM = 2;

    public static final String toString(int i) {
        if (i == 0) {
            return "MAIN";
        }
        if (i == 1) {
            return "SECONDARY";
        }
        if (i == 2) {
            return "TCM";
        }
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("MAIN");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("SECONDARY");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("TCM");
            i2 |= 2;
        }
        if (i != i2) {
            arrayList.add(CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
