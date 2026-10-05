package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.BitUtils;

/* JADX INFO: loaded from: classes.dex */
public final class ConnectivityMetricsEvent implements Parcelable {
    public static final Parcelable.Creator<ConnectivityMetricsEvent> CREATOR = new Parcelable.Creator<ConnectivityMetricsEvent>() { // from class: android.net.ConnectivityMetricsEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConnectivityMetricsEvent createFromParcel(Parcel parcel) {
            return new ConnectivityMetricsEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConnectivityMetricsEvent[] newArray(int i) {
            return new ConnectivityMetricsEvent[i];
        }
    };
    public Parcelable data;
    public String ifname;
    public int netId;
    public long timestamp;
    public long transports;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ConnectivityMetricsEvent() {
    }

    private ConnectivityMetricsEvent(Parcel parcel) {
        this.timestamp = parcel.readLong();
        this.transports = parcel.readLong();
        this.netId = parcel.readInt();
        this.ifname = parcel.readString();
        this.data = parcel.readParcelable(null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.timestamp);
        parcel.writeLong(this.transports);
        parcel.writeInt(this.netId);
        parcel.writeString(this.ifname);
        parcel.writeParcelable(this.data, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ConnectivityMetricsEvent(");
        sb.append(String.format("%tT.%tL", Long.valueOf(this.timestamp), Long.valueOf(this.timestamp)));
        if (this.netId != 0) {
            sb.append(", ");
            sb.append("netId=");
            sb.append(this.netId);
        }
        if (this.ifname != null) {
            sb.append(", ");
            sb.append(this.ifname);
        }
        for (int i : BitUtils.unpackBits(this.transports)) {
            sb.append(", ");
            sb.append(NetworkCapabilities.transportNameOf(i));
        }
        sb.append("): ");
        sb.append(this.data.toString());
        return sb.toString();
    }
}
