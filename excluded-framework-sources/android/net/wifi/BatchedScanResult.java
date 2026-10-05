package android.net.wifi;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public class BatchedScanResult implements Parcelable {
    public static final Parcelable.Creator<BatchedScanResult> CREATOR = new Parcelable.Creator<BatchedScanResult>() { // from class: android.net.wifi.BatchedScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanResult createFromParcel(Parcel parcel) {
            BatchedScanResult batchedScanResult = new BatchedScanResult();
            batchedScanResult.truncated = parcel.readInt() == 1;
            int i = parcel.readInt();
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    return batchedScanResult;
                }
                batchedScanResult.scanResults.add(ScanResult.CREATOR.createFromParcel(parcel));
                i = i2;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatchedScanResult[] newArray(int i) {
            return new BatchedScanResult[i];
        }
    };
    private static final String TAG = "BatchedScanResult";
    public final List<ScanResult> scanResults = new ArrayList();
    public boolean truncated;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BatchedScanResult() {
    }

    public BatchedScanResult(BatchedScanResult batchedScanResult) {
        this.truncated = batchedScanResult.truncated;
        Iterator<ScanResult> it = batchedScanResult.scanResults.iterator();
        while (it.hasNext()) {
            this.scanResults.add(new ScanResult(it.next()));
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BatchedScanResult: ");
        stringBuffer.append("truncated: ");
        stringBuffer.append(String.valueOf(this.truncated));
        stringBuffer.append("scanResults: [");
        for (ScanResult scanResult : this.scanResults) {
            stringBuffer.append(" <");
            stringBuffer.append(scanResult.toString());
            stringBuffer.append("> ");
        }
        stringBuffer.append(" ]");
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.truncated ? 1 : 0);
        parcel.writeInt(this.scanResults.size());
        Iterator<ScanResult> it = this.scanResults.iterator();
        while (it.hasNext()) {
            it.next().writeToParcel(parcel, i);
        }
    }
}
