package android.hardware.radio.V1_0;

import android.app.CarConfigManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class SubscriptionType {
    public static final int SUBSCRIPTION_1 = 0;
    public static final int SUBSCRIPTION_2 = 1;
    public static final int SUBSCRIPTION_3 = 2;

    public static final String toString(int i) {
        if (i == 0) {
            return "SUBSCRIPTION_1";
        }
        if (i == 1) {
            return "SUBSCRIPTION_2";
        }
        if (i == 2) {
            return "SUBSCRIPTION_3";
        }
        return CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i);
    }

    public static final String dumpBitfield(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("SUBSCRIPTION_1");
        int i2 = 1;
        if ((i & 1) == 1) {
            arrayList.add("SUBSCRIPTION_2");
        } else {
            i2 = 0;
        }
        if ((i & 2) == 2) {
            arrayList.add("SUBSCRIPTION_3");
            i2 |= 2;
        }
        if (i != i2) {
            arrayList.add(CarConfigManager.HEX_VALUE_DEFAULT + Integer.toHexString(i & (~i2)));
        }
        return String.join(" | ", arrayList);
    }
}
