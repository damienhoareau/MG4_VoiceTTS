package android.hardware.radio.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class OperatorInfo {
    public String alphaLong = new String();
    public String alphaShort = new String();
    public String operatorNumeric = new String();
    public int status;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != OperatorInfo.class) {
            return false;
        }
        OperatorInfo operatorInfo = (OperatorInfo) obj;
        return HidlSupport.deepEquals(this.alphaLong, operatorInfo.alphaLong) && HidlSupport.deepEquals(this.alphaShort, operatorInfo.alphaShort) && HidlSupport.deepEquals(this.operatorNumeric, operatorInfo.operatorNumeric) && this.status == operatorInfo.status;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.alphaLong)), Integer.valueOf(HidlSupport.deepHashCode(this.alphaShort)), Integer.valueOf(HidlSupport.deepHashCode(this.operatorNumeric)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.status))));
    }

    public final String toString() {
        return "{.alphaLong = " + this.alphaLong + ", .alphaShort = " + this.alphaShort + ", .operatorNumeric = " + this.operatorNumeric + ", .status = " + OperatorStatus.toString(this.status) + "}";
    }

    public final void readFromParcel(HwParcel hwParcel) {
        readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(56L), 0L);
    }

    public static final ArrayList<OperatorInfo> readVectorFromParcel(HwParcel hwParcel) {
        ArrayList<OperatorInfo> arrayList = new ArrayList<>();
        HwBlob buffer = hwParcel.readBuffer(16L);
        int int32 = buffer.getInt32(8L);
        HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 56, buffer.handle(), 0L, true);
        arrayList.clear();
        for (int i = 0; i < int32; i++) {
            OperatorInfo operatorInfo = new OperatorInfo();
            operatorInfo.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 56);
            arrayList.add(operatorInfo);
        }
        return arrayList;
    }

    public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
        long j2 = j + 0;
        String string = hwBlob.getString(j2);
        this.alphaLong = string;
        hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
        long j3 = j + 16;
        String string2 = hwBlob.getString(j3);
        this.alphaShort = string2;
        hwParcel.readEmbeddedBuffer(string2.getBytes().length + 1, hwBlob.handle(), j3 + 0, false);
        long j4 = j + 32;
        String string3 = hwBlob.getString(j4);
        this.operatorNumeric = string3;
        hwParcel.readEmbeddedBuffer(string3.getBytes().length + 1, hwBlob.handle(), j4 + 0, false);
        this.status = hwBlob.getInt32(j + 48);
    }

    public final void writeToParcel(HwParcel hwParcel) {
        HwBlob hwBlob = new HwBlob(56);
        writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }

    public static final void writeVectorToParcel(HwParcel hwParcel, ArrayList<OperatorInfo> arrayList) {
        HwBlob hwBlob = new HwBlob(16);
        int size = arrayList.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        HwBlob hwBlob2 = new HwBlob(size * 56);
        for (int i = 0; i < size; i++) {
            arrayList.get(i).writeEmbeddedToBlob(hwBlob2, i * 56);
        }
        hwBlob.putBlob(0L, hwBlob2);
        hwParcel.writeBuffer(hwBlob);
    }

    public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
        hwBlob.putString(0 + j, this.alphaLong);
        hwBlob.putString(16 + j, this.alphaShort);
        hwBlob.putString(32 + j, this.operatorNumeric);
        hwBlob.putInt32(j + 48, this.status);
    }
}
