package android.net.wifi.p2p;

import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WpsInfo;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class WifiP2pConfig implements Parcelable {
    public static final Parcelable.Creator<WifiP2pConfig> CREATOR = new Parcelable.Creator<WifiP2pConfig>() { // from class: android.net.wifi.p2p.WifiP2pConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfig createFromParcel(Parcel parcel) {
            WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
            wifiP2pConfig.deviceAddress = parcel.readString();
            wifiP2pConfig.wps = (WpsInfo) parcel.readParcelable(null);
            wifiP2pConfig.groupOwnerIntent = parcel.readInt();
            wifiP2pConfig.netId = parcel.readInt();
            return wifiP2pConfig;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfig[] newArray(int i) {
            return new WifiP2pConfig[i];
        }
    };
    public static final int MAX_GROUP_OWNER_INTENT = 15;
    public static final int MIN_GROUP_OWNER_INTENT = 0;
    public String deviceAddress;
    public int groupOwnerIntent;
    public int netId;
    public WpsInfo wps;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiP2pConfig() {
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        WpsInfo wpsInfo = new WpsInfo();
        this.wps = wpsInfo;
        wpsInfo.setup = 0;
    }

    public void invalidate() {
        this.deviceAddress = "";
    }

    public WifiP2pConfig(String str) throws IllegalArgumentException {
        int i;
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        String[] strArrSplit = str.split(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        if (strArrSplit.length < 2 || !strArrSplit[0].equals("P2P-GO-NEG-REQUEST")) {
            throw new IllegalArgumentException("Malformed supplicant event");
        }
        this.deviceAddress = strArrSplit[1];
        this.wps = new WpsInfo();
        if (strArrSplit.length > 2) {
            try {
                i = Integer.parseInt(strArrSplit[2].split("=")[1]);
            } catch (NumberFormatException unused) {
                i = 0;
            }
            if (i == 1) {
                this.wps.setup = 1;
                return;
            }
            if (i == 4) {
                this.wps.setup = 0;
            } else if (i == 5) {
                this.wps.setup = 2;
            } else {
                this.wps.setup = 0;
            }
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n address: ");
        stringBuffer.append(this.deviceAddress);
        stringBuffer.append("\n wps: ");
        stringBuffer.append(this.wps);
        stringBuffer.append("\n groupOwnerIntent: ");
        stringBuffer.append(this.groupOwnerIntent);
        stringBuffer.append("\n persist: ");
        stringBuffer.append(this.netId);
        return stringBuffer.toString();
    }

    public WifiP2pConfig(WifiP2pConfig wifiP2pConfig) {
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        if (wifiP2pConfig != null) {
            this.deviceAddress = wifiP2pConfig.deviceAddress;
            this.wps = new WpsInfo(wifiP2pConfig.wps);
            this.groupOwnerIntent = wifiP2pConfig.groupOwnerIntent;
            this.netId = wifiP2pConfig.netId;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.deviceAddress);
        parcel.writeParcelable(this.wps, i);
        parcel.writeInt(this.groupOwnerIntent);
        parcel.writeInt(this.netId);
    }
}
