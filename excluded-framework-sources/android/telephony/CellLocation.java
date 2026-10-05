package android.telephony;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.android.internal.telephony.ITelephony;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CellLocation {
    public abstract void fillInNotifierBundle(Bundle bundle);

    public abstract boolean isEmpty();

    public abstract void setStateInvalid();

    public static void requestLocationUpdate() {
        try {
            ITelephony iTelephonyAsInterface = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
            if (iTelephonyAsInterface != null) {
                iTelephonyAsInterface.updateServiceLocation();
            }
        } catch (RemoteException unused) {
        }
    }

    public static CellLocation newFromBundle(Bundle bundle) {
        int currentPhoneType = TelephonyManager.getDefault().getCurrentPhoneType();
        if (currentPhoneType == 1) {
            return new GsmCellLocation(bundle);
        }
        if (currentPhoneType != 2) {
            return null;
        }
        return new CdmaCellLocation(bundle);
    }

    public static CellLocation getEmpty() {
        int currentPhoneType = TelephonyManager.getDefault().getCurrentPhoneType();
        if (currentPhoneType == 1) {
            return new GsmCellLocation();
        }
        if (currentPhoneType != 2) {
            return null;
        }
        return new CdmaCellLocation();
    }
}
