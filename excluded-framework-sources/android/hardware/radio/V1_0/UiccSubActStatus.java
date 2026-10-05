package android.hardware.radio.V1_0;

import android.app.CarConfigManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class UiccSubActStatus {
    public static final int ACTIVATE = 1;
    public static final int DEACTIVATE = 0;

    public static final String toString(int i) {
        if (i == 0) {
            return "DEACTIVATE";
        }
        if (i == 1) {
            return "ACTIVATE";
        }
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("DEACTIVATE");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("ACTIVATE");
        } else {
            i2 = 0;
        }
        if (i != i2) {
            arrayList.add(CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
