package com.android.internal.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.CellInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class NetworkScanResult implements Parcelable {
    public static final Parcelable.Creator<NetworkScanResult> CREATOR = new Parcelable.Creator<NetworkScanResult>() { // from class: com.android.internal.telephony.NetworkScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkScanResult createFromParcel(Parcel parcel) {
            return new NetworkScanResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkScanResult[] newArray(int i) {
            return new NetworkScanResult[i];
        }
    };
    public static final int SCAN_STATUS_COMPLETE = 2;
    public static final int SCAN_STATUS_PARTIAL = 1;
    public List<CellInfo> networkInfos;
    public int scanError;
    public int scanStatus;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NetworkScanResult(int i, int i2, List<CellInfo> list) {
        this.scanStatus = i;
        this.scanError = i2;
        this.networkInfos = list;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.scanStatus);
        parcel.writeInt(this.scanError);
        parcel.writeParcelableList(this.networkInfos, i);
    }

    private NetworkScanResult(Parcel parcel) {
        this.scanStatus = parcel.readInt();
        this.scanError = parcel.readInt();
        ArrayList arrayList = new ArrayList();
        parcel.readParcelableList(arrayList, Object.class.getClassLoader());
        this.networkInfos = arrayList;
    }

    public boolean equals(Object obj) {
        try {
            NetworkScanResult networkScanResult = (NetworkScanResult) obj;
            return obj != null && this.scanStatus == networkScanResult.scanStatus && this.scanError == networkScanResult.scanError && this.networkInfos.equals(networkScanResult.networkInfos);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return (this.scanStatus * 31) + (this.scanError * 23) + (Objects.hashCode(this.networkInfos) * 37);
    }
}
