package android.net.wifi;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class WifiNetworkConnectionStatistics implements Parcelable {
    public static final Parcelable.Creator<WifiNetworkConnectionStatistics> CREATOR = new Parcelable.Creator<WifiNetworkConnectionStatistics>() { // from class: android.net.wifi.WifiNetworkConnectionStatistics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiNetworkConnectionStatistics createFromParcel(Parcel parcel) {
            return new WifiNetworkConnectionStatistics(parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiNetworkConnectionStatistics[] newArray(int i) {
            return new WifiNetworkConnectionStatistics[i];
        }
    };
    private static final String TAG = "WifiNetworkConnnectionStatistics";
    public int numConnection;
    public int numUsage;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiNetworkConnectionStatistics(int i, int i2) {
        this.numConnection = i;
        this.numUsage = i2;
    }

    public WifiNetworkConnectionStatistics() {
    }

    public String toString() {
        return "c=" + this.numConnection + " u=" + this.numUsage;
    }

    public WifiNetworkConnectionStatistics(WifiNetworkConnectionStatistics wifiNetworkConnectionStatistics) {
        this.numConnection = wifiNetworkConnectionStatistics.numConnection;
        this.numUsage = wifiNetworkConnectionStatistics.numUsage;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.numConnection);
        parcel.writeInt(this.numUsage);
    }
}
