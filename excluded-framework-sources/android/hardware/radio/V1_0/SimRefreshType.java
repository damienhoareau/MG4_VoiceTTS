package android.hardware.radio.V1_0;

import android.app.CarConfigManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class SimRefreshType {
    public static final int SIM_FILE_UPDATE = 0;
    public static final int SIM_INIT = 1;
    public static final int SIM_RESET = 2;

    public static final String toString(int i) {
        if (i == 0) {
            return "SIM_FILE_UPDATE";
        }
        if (i == 1) {
            return "SIM_INIT";
        }
        if (i == 2) {
            return "SIM_RESET";
        }
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("SIM_FILE_UPDATE");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("SIM_INIT");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("SIM_RESET");
            i2 |= 2;
        }
        if (i != i2) {
            arrayList.add(CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
