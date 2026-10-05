package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class IpConfiguration implements Parcelable {
    public static final Parcelable.Creator<IpConfiguration> CREATOR = new Parcelable.Creator<IpConfiguration>() { // from class: android.net.IpConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpConfiguration createFromParcel(Parcel parcel) {
            IpConfiguration ipConfiguration = new IpConfiguration();
            ipConfiguration.ipAssignment = IpAssignment.valueOf(parcel.readString());
            ipConfiguration.proxySettings = ProxySettings.valueOf(parcel.readString());
            ipConfiguration.staticIpConfiguration = (StaticIpConfiguration) parcel.readParcelable(null);
            ipConfiguration.httpProxy = (ProxyInfo) parcel.readParcelable(null);
            return ipConfiguration;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpConfiguration[] newArray(int i) {
            return new IpConfiguration[i];
        }
    };
    private static final String TAG = "IpConfiguration";
    public ProxyInfo httpProxy;
    public IpAssignment ipAssignment;
    public ProxySettings proxySettings;
    public StaticIpConfiguration staticIpConfiguration;

    public enum IpAssignment {
        STATIC,
        DHCP,
        UNASSIGNED
    }

    public enum ProxySettings {
        NONE,
        STATIC,
        UNASSIGNED,
        PAC
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private void init(IpAssignment ipAssignment, ProxySettings proxySettings, StaticIpConfiguration staticIpConfiguration, ProxyInfo proxyInfo) {
        this.ipAssignment = ipAssignment;
        this.proxySettings = proxySettings;
        this.staticIpConfiguration = staticIpConfiguration == null ? null : new StaticIpConfiguration(staticIpConfiguration);
        this.httpProxy = proxyInfo != null ? new ProxyInfo(proxyInfo) : null;
    }

    public IpConfiguration() {
        init(IpAssignment.UNASSIGNED, ProxySettings.UNASSIGNED, null, null);
    }

    public IpConfiguration(IpAssignment ipAssignment, ProxySettings proxySettings, StaticIpConfiguration staticIpConfiguration, ProxyInfo proxyInfo) {
        init(ipAssignment, proxySettings, staticIpConfiguration, proxyInfo);
    }

    public IpConfiguration(IpConfiguration ipConfiguration) {
        this();
        if (ipConfiguration != null) {
            init(ipConfiguration.ipAssignment, ipConfiguration.proxySettings, ipConfiguration.staticIpConfiguration, ipConfiguration.httpProxy);
        }
    }

    public IpAssignment getIpAssignment() {
        return this.ipAssignment;
    }

    public void setIpAssignment(IpAssignment ipAssignment) {
        this.ipAssignment = ipAssignment;
    }

    public StaticIpConfiguration getStaticIpConfiguration() {
        return this.staticIpConfiguration;
    }

    public void setStaticIpConfiguration(StaticIpConfiguration staticIpConfiguration) {
        this.staticIpConfiguration = staticIpConfiguration;
    }

    public ProxySettings getProxySettings() {
        return this.proxySettings;
    }

    public void setProxySettings(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    public ProxyInfo getHttpProxy() {
        return this.httpProxy;
    }

    public void setHttpProxy(ProxyInfo proxyInfo) {
        this.httpProxy = proxyInfo;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IP assignment: " + this.ipAssignment.toString());
        sb.append("\n");
        if (this.staticIpConfiguration != null) {
            sb.append("Static configuration: " + this.staticIpConfiguration.toString());
            sb.append("\n");
        }
        sb.append("Proxy settings: " + this.proxySettings.toString());
        sb.append("\n");
        if (this.httpProxy != null) {
            sb.append("HTTP proxy: " + this.httpProxy.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IpConfiguration)) {
            return false;
        }
        IpConfiguration ipConfiguration = (IpConfiguration) obj;
        return this.ipAssignment == ipConfiguration.ipAssignment && this.proxySettings == ipConfiguration.proxySettings && Objects.equals(this.staticIpConfiguration, ipConfiguration.staticIpConfiguration) && Objects.equals(this.httpProxy, ipConfiguration.httpProxy);
    }

    public int hashCode() {
        StaticIpConfiguration staticIpConfiguration = this.staticIpConfiguration;
        return (staticIpConfiguration != null ? staticIpConfiguration.hashCode() : 0) + 13 + (this.ipAssignment.ordinal() * 17) + (this.proxySettings.ordinal() * 47) + (this.httpProxy.hashCode() * 83);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ipAssignment.name());
        parcel.writeString(this.proxySettings.name());
        parcel.writeParcelable(this.staticIpConfiguration, i);
        parcel.writeParcelable(this.httpProxy, i);
    }
}
