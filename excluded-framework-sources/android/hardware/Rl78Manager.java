package android.hardware;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;

/* JADX INFO: loaded from: classes.dex */
public class Rl78Manager {
    private static final String TAG = "Rl78Manager";
    private boolean Debug = false;
    private final Context mContext;
    private final IRl78Service mService;

    public Rl78Manager(Context context, IRl78Service iRl78Service) {
        Slog.e(TAG, "Rl78Manager###");
        this.mContext = context;
        this.mService = iRl78Service;
    }

    public int Rl78_connect() {
        try {
            return this.mService.Rl78_connect();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] getVer() {
        try {
            byte[] ver = this.mService.getVer();
            Slog.e(TAG, "###getVer version = " + new String(ver));
            return ver;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] getRl78PN(int i) {
        try {
            byte[] rl78PN = this.mService.getRl78PN(i);
            Slog.e(TAG, "###getRl78PN version = " + new String(rl78PN));
            return rl78PN;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void Rl78_disconnect() {
        try {
            this.mService.Rl78_disconnect();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] eol_get_consistency_result() {
        try {
            byte[] bArrEol_get_consistency_result = this.mService.eol_get_consistency_result();
            Slog.e(TAG, "###eol_get_consistency_result result = " + new String(bArrEol_get_consistency_result));
            return bArrEol_get_consistency_result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void eol_check_consistency(byte[] bArr) {
        try {
            this.mService.eol_check_consistency(bArr);
            Slog.e(TAG, "###eol_check_consistency");
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] eol_tp_calibrate() {
        try {
            byte[] bArrEol_tp_calibrate = this.mService.eol_tp_calibrate();
            Slog.e(TAG, "###eol_tp_calibrate result = " + new String(bArrEol_tp_calibrate));
            return bArrEol_tp_calibrate;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] getTpPN(int i) {
        try {
            byte[] tpPN = this.mService.getTpPN(i);
            Slog.e(TAG, "###getTpPN displayid = " + i + " result=" + new String(tpPN));
            return tpPN;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] eol_tp_getver() {
        try {
            byte[] bArrEol_tp_getver = this.mService.eol_tp_getver();
            Slog.e(TAG, "###eol_tp_getver result = " + new String(bArrEol_tp_getver));
            return bArrEol_tp_getver;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] eol_tp_getveronly() throws Exception {
        int i;
        int i2;
        byte[] bArrEol_tp_getver = eol_tp_getver();
        if (bArrEol_tp_getver.length <= 0) {
            throw new Exception("eol_tp_getver error! no version return!");
        }
        String str = SystemProperties.get("ro.product.name");
        if (this.Debug) {
            Slog.d(TAG, "getTouchVersionOnly allVersion len = " + bArrEol_tp_getver.length + ", allVersion = " + new String(bArrEol_tp_getver));
            StringBuilder sb = new StringBuilder();
            sb.append("getTouchVersionOnly car_mode_prop = ");
            sb.append(str);
            Slog.d(TAG, sb.toString());
        }
        if (str.equals("mt2712_saic_as32") || str.equals("mt2712_saic_eh32") || str.equals("mt2712_saic_oimt")) {
            i = 13;
            i2 = 1;
        } else if (str.equals("mt2712_saic_as33") || str.equals("mt2712_saic_mzs3e")) {
            i = 6;
            i2 = 3;
        } else {
            i = 20;
            i2 = 2;
        }
        if (i == 0 || i2 == 0) {
            throw new Exception("getVersionError, verLen is null");
        }
        if (i2 == 1) {
            byte[] bArr = new byte[i];
            for (int i3 = 21; i3 < 34; i3++) {
                bArr[i3 - 21] = bArrEol_tp_getver[i3];
            }
            if (this.Debug) {
                Slog.d(TAG, "getTouchVersionOnly touchVersionCase = " + i2);
                Slog.d(TAG, "getTouchVersionOnly verLenResult = " + new String(bArr));
            }
            Slog.e(TAG, "###eol_tp_getveronly result = " + new String(bArr));
            return bArr;
        }
        int i4 = 0;
        if (i2 != 2) {
            if (i2 == 3) {
                byte[] bArr2 = new byte[i];
                int i5 = 75;
                while (i5 < 78) {
                    bArr2[i4] = bArrEol_tp_getver[i5];
                    i5++;
                    i4++;
                }
                int i6 = 105;
                while (i6 < 108) {
                    bArr2[i4] = bArrEol_tp_getver[i6];
                    i6++;
                    i4++;
                }
                if (this.Debug) {
                    Slog.d(TAG, "getTouchVersionOnly touchVersionCase = " + i2);
                    Slog.d(TAG, "getTouchVersionOnly verLenResult = " + new String(bArr2));
                }
                Slog.e(TAG, "###eol_tp_getveronly result = " + new String(bArr2));
                return bArr2;
            }
            throw new Exception("no case error! ");
        }
        if (bArrEol_tp_getver.length < 187) {
            throw new Exception("version case 2 no enouth length error!");
        }
        byte[] bArr3 = new byte[i];
        for (int i7 = 115; i7 < 154; i7++) {
            if ((bArrEol_tp_getver[i7] != 48 || bArrEol_tp_getver[i7 + 1] != 120) && ((bArrEol_tp_getver[i7] != 120 || bArrEol_tp_getver[i7 - 1] != 48) && bArrEol_tp_getver[i7] != 32)) {
                bArr3[i4] = bArrEol_tp_getver[i7];
                i4++;
            }
        }
        int i8 = 183;
        while (i8 < 187) {
            bArr3[i4] = bArrEol_tp_getver[i8];
            i8++;
            i4++;
        }
        if (this.Debug) {
            Slog.d(TAG, "getTouchVersionOnly touchVersionCase = " + i2);
            Slog.d(TAG, "getTouchVersionOnly verLenResult = " + new String(bArr3));
        }
        Slog.e(TAG, "###eol_tp_getveronly result = " + new String(bArr3));
        return bArr3;
    }
}
