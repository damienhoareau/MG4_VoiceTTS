package android.hardware.vibrator.V1_0;

import android.app.CarConfigManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class Effect {
    public static final int CLICK = 0;
    public static final int DOUBLE_CLICK = 1;

    public static final String toString(int i) {
        if (i == 0) {
            return "CLICK";
        }
        if (i == 1) {
            return "DOUBLE_CLICK";
        }
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("CLICK");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("DOUBLE_CLICK");
        } else {
            i2 = 0;
        }
        if (i != i2) {
            arrayList.add(CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
