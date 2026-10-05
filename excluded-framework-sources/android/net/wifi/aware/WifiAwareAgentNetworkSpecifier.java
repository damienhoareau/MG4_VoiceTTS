package android.net.wifi.aware;

import android.net.NetworkSpecifier;
import android.os.Parcel;
import android.os.Parcelable;
import android.security.keystore.KeyProperties;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;
import libcore.util.HexEncoding;

/* JADX INFO: loaded from: classes.dex */
public class WifiAwareAgentNetworkSpecifier extends NetworkSpecifier implements Parcelable {
    public static final Parcelable.Creator<WifiAwareAgentNetworkSpecifier> CREATOR = new Parcelable.Creator<WifiAwareAgentNetworkSpecifier>() { // from class: android.net.wifi.aware.WifiAwareAgentNetworkSpecifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiAwareAgentNetworkSpecifier createFromParcel(Parcel parcel) {
            WifiAwareAgentNetworkSpecifier wifiAwareAgentNetworkSpecifier = new WifiAwareAgentNetworkSpecifier();
            for (Object obj : parcel.readArray(null)) {
                wifiAwareAgentNetworkSpecifier.mNetworkSpecifiers.add((ByteArrayWrapper) obj);
            }
            return wifiAwareAgentNetworkSpecifier;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiAwareAgentNetworkSpecifier[] newArray(int i) {
            return new WifiAwareAgentNetworkSpecifier[i];
        }
    };
    private static final String TAG = "WifiAwareAgentNs";
    private static final boolean VDBG = false;
    private MessageDigest mDigester;
    private Set<ByteArrayWrapper> mNetworkSpecifiers = new HashSet();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiAwareAgentNetworkSpecifier() {
    }

    public WifiAwareAgentNetworkSpecifier(WifiAwareNetworkSpecifier wifiAwareNetworkSpecifier) {
        initialize();
        this.mNetworkSpecifiers.add(convert(wifiAwareNetworkSpecifier));
    }

    public WifiAwareAgentNetworkSpecifier(WifiAwareNetworkSpecifier[] wifiAwareNetworkSpecifierArr) {
        initialize();
        for (WifiAwareNetworkSpecifier wifiAwareNetworkSpecifier : wifiAwareNetworkSpecifierArr) {
            this.mNetworkSpecifiers.add(convert(wifiAwareNetworkSpecifier));
        }
    }

    public boolean isEmpty() {
        return this.mNetworkSpecifiers.isEmpty();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeArray(this.mNetworkSpecifiers.toArray());
    }

    public int hashCode() {
        return this.mNetworkSpecifiers.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WifiAwareAgentNetworkSpecifier) {
            return this.mNetworkSpecifiers.equals(((WifiAwareAgentNetworkSpecifier) obj).mNetworkSpecifiers);
        }
        return false;
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        Iterator<ByteArrayWrapper> it = this.mNetworkSpecifiers.iterator();
        while (it.hasNext()) {
            stringJoiner.add(it.next().toString());
        }
        return stringJoiner.toString();
    }

    @Override // android.net.NetworkSpecifier
    public boolean satisfiedBy(NetworkSpecifier networkSpecifier) {
        if (!(networkSpecifier instanceof WifiAwareAgentNetworkSpecifier)) {
            return false;
        }
        WifiAwareAgentNetworkSpecifier wifiAwareAgentNetworkSpecifier = (WifiAwareAgentNetworkSpecifier) networkSpecifier;
        Iterator<ByteArrayWrapper> it = this.mNetworkSpecifiers.iterator();
        while (it.hasNext()) {
            if (!wifiAwareAgentNetworkSpecifier.mNetworkSpecifiers.contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    public boolean satisfiesAwareNetworkSpecifier(WifiAwareNetworkSpecifier wifiAwareNetworkSpecifier) {
        return this.mNetworkSpecifiers.contains(convert(wifiAwareNetworkSpecifier));
    }

    @Override // android.net.NetworkSpecifier
    public void assertValidFromUid(int i) {
        throw new SecurityException("WifiAwareAgentNetworkSpecifier should not be used in network requests");
    }

    private void initialize() {
        try {
            this.mDigester = MessageDigest.getInstance(KeyProperties.DIGEST_SHA256);
        } catch (NoSuchAlgorithmException unused) {
            Log.e(TAG, "Can not instantiate a SHA-256 digester!? Will match nothing.");
        }
    }

    private ByteArrayWrapper convert(WifiAwareNetworkSpecifier wifiAwareNetworkSpecifier) {
        if (this.mDigester == null) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        wifiAwareNetworkSpecifier.writeToParcel(parcelObtain, 0);
        byte[] bArrMarshall = parcelObtain.marshall();
        this.mDigester.reset();
        this.mDigester.update(bArrMarshall);
        return new ByteArrayWrapper(this.mDigester.digest());
    }

    private static class ByteArrayWrapper implements Parcelable {
        public static final Parcelable.Creator<ByteArrayWrapper> CREATOR = new Parcelable.Creator<ByteArrayWrapper>() { // from class: android.net.wifi.aware.WifiAwareAgentNetworkSpecifier.ByteArrayWrapper.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ByteArrayWrapper createFromParcel(Parcel parcel) {
                return new ByteArrayWrapper(parcel.readBlob());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ByteArrayWrapper[] newArray(int i) {
                return new ByteArrayWrapper[i];
            }
        };
        private byte[] mData;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        ByteArrayWrapper(byte[] bArr) {
            this.mData = bArr;
        }

        public int hashCode() {
            return Arrays.hashCode(this.mData);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ByteArrayWrapper) {
                return Arrays.equals(((ByteArrayWrapper) obj).mData, this.mData);
            }
            return false;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeBlob(this.mData);
        }

        public String toString() {
            return new String(HexEncoding.encode(this.mData));
        }
    }
}
