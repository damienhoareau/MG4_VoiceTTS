package android.net.wifi;

import android.annotation.SystemApi;
import android.net.IpConfiguration;
import android.net.MacAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Telephony;
import android.security.keystore.KeyProperties;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import android.util.BackupUtils;
import android.util.Log;
import android.util.TimeUtils;
import com.android.internal.content.NativeLibraryHelper;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class WifiConfiguration implements Parcelable {
    public static final int AP_BAND_2GHZ = 0;
    public static final int AP_BAND_5GHZ = 1;
    public static final int AP_BAND_ANY = -1;
    private static final int BACKUP_VERSION = 2;
    public static final int HOME_NETWORK_RSSI_BOOST = 5;
    public static final int INVALID_NETWORK_ID = -1;
    public static final int LOCAL_ONLY_NETWORK_ID = -2;
    private static final int MAXIMUM_RANDOM_MAC_GENERATION_RETRY = 3;
    public static final int METERED_OVERRIDE_METERED = 1;
    public static final int METERED_OVERRIDE_NONE = 0;
    public static final int METERED_OVERRIDE_NOT_METERED = 2;
    private static final String TAG = "WifiConfiguration";
    public static final int UNKNOWN_UID = -1;
    public static final int USER_APPROVED = 1;
    public static final int USER_BANNED = 2;
    public static final int USER_PENDING = 3;
    public static final int USER_UNSPECIFIED = 0;
    public static final String bssidVarName = "bssid";
    public static final String hiddenSSIDVarName = "scan_ssid";
    public static final String pmfVarName = "ieee80211w";
    public static final String priorityVarName = "priority";
    public static final String pskVarName = "psk";
    public static final String ssidVarName = "ssid";
    public static final String updateIdentiferVarName = "update_identifier";

    @Deprecated
    public static final String wepTxKeyIdxVarName = "wep_tx_keyidx";
    public String BSSID;
    public String FQDN;
    public String SSID;
    public BitSet allowedAuthAlgorithms;
    public BitSet allowedGroupCiphers;
    public BitSet allowedKeyManagement;
    public BitSet allowedPairwiseCiphers;
    public BitSet allowedProtocols;
    public int apBand;
    public int apChannel;
    public String creationTime;

    @SystemApi
    public String creatorName;

    @SystemApi
    public int creatorUid;
    public String defaultGwMacAddress;
    public String dhcpServer;
    public boolean didSelfAdd;
    public int dtimInterval;
    public WifiEnterpriseConfig enterpriseConfig;
    public boolean ephemeral;
    public boolean hiddenSSID;
    public boolean isHomeProviderNetwork;
    public boolean isLegacyPasspointConfig;
    public int lastConnectUid;
    public long lastConnected;
    public long lastDisconnected;

    @SystemApi
    public String lastUpdateName;

    @SystemApi
    public int lastUpdateUid;
    public HashMap<String, Integer> linkedConfigurations;
    String mCachedConfigKey;
    private IpConfiguration mIpConfiguration;
    private NetworkSelectionStatus mNetworkSelectionStatus;
    private String mPasspointManagementObjectTree;
    private MacAddress mRandomizedMacAddress;

    @SystemApi
    public boolean meteredHint;
    public int meteredOverride;
    public int networkId;
    public boolean noInternetAccessExpected;

    @SystemApi
    public int numAssociation;
    public int numNoInternetAccessReports;

    @SystemApi
    public int numScorerOverride;

    @SystemApi
    public int numScorerOverrideAndSwitchedNetwork;
    public String peerWifiConfiguration;
    public String preSharedKey;

    @Deprecated
    public int priority;
    public String providerFriendlyName;
    public final RecentFailure recentFailure;
    public boolean requirePMF;
    public long[] roamingConsortiumIds;
    public boolean selfAdded;
    public boolean shared;
    public int status;
    public String updateIdentifier;
    public String updateTime;

    @SystemApi
    public boolean useExternalScores;
    public int userApproved;
    public boolean validatedInternetAccess;

    @Deprecated
    public String[] wepKeys;

    @Deprecated
    public int wepTxKeyIndex;

    @Deprecated
    public static final String[] wepKeyVarNames = {"wep_key0", "wep_key1", "wep_key2", "wep_key3"};
    public static int INVALID_RSSI = -127;
    public static final Parcelable.Creator<WifiConfiguration> CREATOR = new Parcelable.Creator<WifiConfiguration>() { // from class: android.net.wifi.WifiConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration createFromParcel(Parcel parcel) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.networkId = parcel.readInt();
            wifiConfiguration.status = parcel.readInt();
            wifiConfiguration.mNetworkSelectionStatus.readFromParcel(parcel);
            wifiConfiguration.SSID = parcel.readString();
            wifiConfiguration.BSSID = parcel.readString();
            wifiConfiguration.apBand = parcel.readInt();
            wifiConfiguration.apChannel = parcel.readInt();
            wifiConfiguration.FQDN = parcel.readString();
            wifiConfiguration.providerFriendlyName = parcel.readString();
            wifiConfiguration.isHomeProviderNetwork = parcel.readInt() != 0;
            int i = parcel.readInt();
            wifiConfiguration.roamingConsortiumIds = new long[i];
            for (int i2 = 0; i2 < i; i2++) {
                wifiConfiguration.roamingConsortiumIds[i2] = parcel.readLong();
            }
            wifiConfiguration.preSharedKey = parcel.readString();
            for (int i3 = 0; i3 < wifiConfiguration.wepKeys.length; i3++) {
                wifiConfiguration.wepKeys[i3] = parcel.readString();
            }
            wifiConfiguration.wepTxKeyIndex = parcel.readInt();
            wifiConfiguration.priority = parcel.readInt();
            wifiConfiguration.hiddenSSID = parcel.readInt() != 0;
            wifiConfiguration.requirePMF = parcel.readInt() != 0;
            wifiConfiguration.updateIdentifier = parcel.readString();
            wifiConfiguration.allowedKeyManagement = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedProtocols = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedAuthAlgorithms = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedPairwiseCiphers = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.allowedGroupCiphers = WifiConfiguration.readBitSet(parcel);
            wifiConfiguration.enterpriseConfig = (WifiEnterpriseConfig) parcel.readParcelable(null);
            wifiConfiguration.setIpConfiguration((IpConfiguration) parcel.readParcelable(null));
            wifiConfiguration.dhcpServer = parcel.readString();
            wifiConfiguration.defaultGwMacAddress = parcel.readString();
            wifiConfiguration.selfAdded = parcel.readInt() != 0;
            wifiConfiguration.didSelfAdd = parcel.readInt() != 0;
            wifiConfiguration.validatedInternetAccess = parcel.readInt() != 0;
            wifiConfiguration.isLegacyPasspointConfig = parcel.readInt() != 0;
            wifiConfiguration.ephemeral = parcel.readInt() != 0;
            wifiConfiguration.meteredHint = parcel.readInt() != 0;
            wifiConfiguration.meteredOverride = parcel.readInt();
            wifiConfiguration.useExternalScores = parcel.readInt() != 0;
            wifiConfiguration.creatorUid = parcel.readInt();
            wifiConfiguration.lastConnectUid = parcel.readInt();
            wifiConfiguration.lastUpdateUid = parcel.readInt();
            wifiConfiguration.creatorName = parcel.readString();
            wifiConfiguration.lastUpdateName = parcel.readString();
            wifiConfiguration.numScorerOverride = parcel.readInt();
            wifiConfiguration.numScorerOverrideAndSwitchedNetwork = parcel.readInt();
            wifiConfiguration.numAssociation = parcel.readInt();
            wifiConfiguration.userApproved = parcel.readInt();
            wifiConfiguration.numNoInternetAccessReports = parcel.readInt();
            wifiConfiguration.noInternetAccessExpected = parcel.readInt() != 0;
            wifiConfiguration.shared = parcel.readInt() != 0;
            wifiConfiguration.mPasspointManagementObjectTree = parcel.readString();
            wifiConfiguration.recentFailure.setAssociationStatus(parcel.readInt());
            wifiConfiguration.mRandomizedMacAddress = (MacAddress) parcel.readParcelable(null);
            return wifiConfiguration;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration[] newArray(int i) {
            return new WifiConfiguration[i];
        }
    };

    public static String userApprovedAsString(int i) {
        if (i == 0) {
            return "USER_UNSPECIFIED";
        }
        if (i != 1) {
            return i != 2 ? "INVALID" : "USER_BANNED";
        }
        return "USER_APPROVED";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class KeyMgmt {
        public static final int FT_EAP = 7;
        public static final int FT_PSK = 6;
        public static final int IEEE8021X = 3;
        public static final int NONE = 0;
        public static final int OSEN = 5;

        @SystemApi
        public static final int WPA2_PSK = 4;
        public static final int WPA_EAP = 2;
        public static final int WPA_PSK = 1;
        public static final String[] strings = {KeyProperties.DIGEST_NONE, "WPA_PSK", "WPA_EAP", "IEEE8021X", "WPA2_PSK", "OSEN", "FT_PSK", "FT_EAP"};
        public static final String varName = "key_mgmt";

        private KeyMgmt() {
        }
    }

    public static class Protocol {
        public static final int OSEN = 2;
        public static final int RSN = 1;

        @Deprecated
        public static final int WPA = 0;
        public static final String[] strings = {"WPA", "RSN", "OSEN"};
        public static final String varName = "proto";

        private Protocol() {
        }
    }

    public static class AuthAlgorithm {
        public static final int LEAP = 2;
        public static final int OPEN = 0;

        @Deprecated
        public static final int SHARED = 1;
        public static final String[] strings = {"OPEN", "SHARED", "LEAP"};
        public static final String varName = "auth_alg";

        private AuthAlgorithm() {
        }
    }

    public static class PairwiseCipher {
        public static final int CCMP = 2;
        public static final int NONE = 0;

        @Deprecated
        public static final int TKIP = 1;
        public static final String[] strings = {KeyProperties.DIGEST_NONE, "TKIP", "CCMP"};
        public static final String varName = "pairwise";

        private PairwiseCipher() {
        }
    }

    public static class GroupCipher {
        public static final int CCMP = 3;
        public static final int GTK_NOT_USED = 4;
        public static final int TKIP = 2;

        @Deprecated
        public static final int WEP104 = 1;

        @Deprecated
        public static final int WEP40 = 0;
        public static final String[] strings = {"WEP40", "WEP104", "TKIP", "CCMP", "GTK_NOT_USED"};
        public static final String varName = "group";

        private GroupCipher() {
        }
    }

    public static class Status {
        public static final int CURRENT = 0;
        public static final int DISABLED = 1;
        public static final int ENABLED = 2;
        public static final String[] strings = {Telephony.Carriers.CURRENT, "disabled", "enabled"};

        private Status() {
        }
    }

    @SystemApi
    public boolean hasNoInternetAccess() {
        return this.numNoInternetAccessReports > 0 && !this.validatedInternetAccess;
    }

    @SystemApi
    public boolean isNoInternetAccessExpected() {
        return this.noInternetAccessExpected;
    }

    @SystemApi
    public boolean isEphemeral() {
        return this.ephemeral;
    }

    public static boolean isMetered(WifiConfiguration wifiConfiguration, WifiInfo wifiInfo) {
        boolean z = wifiInfo != null && wifiInfo.getMeteredHint();
        if (wifiConfiguration != null && wifiConfiguration.meteredHint) {
            z = true;
        }
        boolean z2 = (wifiConfiguration == null || wifiConfiguration.meteredOverride != 1) ? z : true;
        if (wifiConfiguration == null || wifiConfiguration.meteredOverride != 2) {
            return z2;
        }
        return false;
    }

    public boolean isOpenNetwork() {
        boolean z;
        int iCardinality = this.allowedKeyManagement.cardinality();
        boolean z2 = iCardinality == 0 || (iCardinality == 1 && this.allowedKeyManagement.get(0));
        if (this.wepKeys == null) {
            z = true;
            break;
        }
        int i = 0;
        while (true) {
            String[] strArr = this.wepKeys;
            if (i >= strArr.length) {
                z = true;
                break;
            }
            if (strArr[i] != null) {
                z = false;
                break;
            }
            i++;
        }
        return z2 && z;
    }

    public static boolean isValidMacAddressForRandomization(MacAddress macAddress) {
        return (macAddress == null || macAddress.isMulticastAddress() || !macAddress.isLocallyAssigned() || MacAddress.fromString("02:00:00:00:00:00").equals(macAddress)) ? false : true;
    }

    public MacAddress getOrCreateRandomizedMacAddress() {
        for (int i = 0; !isValidMacAddressForRandomization(this.mRandomizedMacAddress) && i < 3; i++) {
            this.mRandomizedMacAddress = MacAddress.createRandomUnicastAddress();
        }
        if (!isValidMacAddressForRandomization(this.mRandomizedMacAddress)) {
            this.mRandomizedMacAddress = MacAddress.fromString("02:00:00:00:00:00");
        }
        return this.mRandomizedMacAddress;
    }

    public MacAddress getRandomizedMacAddress() {
        return this.mRandomizedMacAddress;
    }

    public void setRandomizedMacAddress(MacAddress macAddress) {
        if (macAddress == null) {
            Log.e(TAG, "setRandomizedMacAddress received null MacAddress.");
        } else {
            this.mRandomizedMacAddress = macAddress;
        }
    }

    public static class NetworkSelectionStatus {
        private static final int CONNECT_CHOICE_EXISTS = 1;
        private static final int CONNECT_CHOICE_NOT_EXISTS = -1;
        public static final int DISABLED_ASSOCIATION_REJECTION = 2;
        public static final int DISABLED_AUTHENTICATION_FAILURE = 3;
        public static final int DISABLED_AUTHENTICATION_NO_CREDENTIALS = 9;
        public static final int DISABLED_BAD_LINK = 1;
        public static final int DISABLED_BY_WIFI_MANAGER = 11;
        public static final int DISABLED_BY_WRONG_PASSWORD = 13;
        public static final int DISABLED_DHCP_FAILURE = 4;
        public static final int DISABLED_DNS_FAILURE = 5;
        public static final int DISABLED_DUE_TO_USER_SWITCH = 12;
        public static final int DISABLED_NO_INTERNET_PERMANENT = 10;
        public static final int DISABLED_NO_INTERNET_TEMPORARY = 6;
        public static final int DISABLED_TLS_VERSION_MISMATCH = 8;
        public static final int DISABLED_WPS_START = 7;
        public static final long INVALID_NETWORK_SELECTION_DISABLE_TIMESTAMP = -1;
        public static final int NETWORK_SELECTION_DISABLED_MAX = 14;
        public static final int NETWORK_SELECTION_DISABLED_STARTING_INDEX = 1;
        public static final int NETWORK_SELECTION_ENABLE = 0;
        public static final int NETWORK_SELECTION_ENABLED = 0;
        public static final int NETWORK_SELECTION_PERMANENTLY_DISABLED = 2;
        public static final int NETWORK_SELECTION_STATUS_MAX = 3;
        public static final int NETWORK_SELECTION_TEMPORARY_DISABLED = 1;
        private ScanResult mCandidate;
        private int mCandidateScore;
        private String mConnectChoice;
        private String mNetworkSelectionBSSID;
        private int mNetworkSelectionDisableReason;
        private boolean mNotRecommended;
        private boolean mSeenInLastQualifiedNetworkSelection;
        private int mStatus;
        public static final String[] QUALITY_NETWORK_SELECTION_STATUS = {"NETWORK_SELECTION_ENABLED", "NETWORK_SELECTION_TEMPORARY_DISABLED", "NETWORK_SELECTION_PERMANENTLY_DISABLED"};
        public static final String[] QUALITY_NETWORK_SELECTION_DISABLE_REASON = {"NETWORK_SELECTION_ENABLE", "NETWORK_SELECTION_DISABLED_BAD_LINK", "NETWORK_SELECTION_DISABLED_ASSOCIATION_REJECTION ", "NETWORK_SELECTION_DISABLED_AUTHENTICATION_FAILURE", "NETWORK_SELECTION_DISABLED_DHCP_FAILURE", "NETWORK_SELECTION_DISABLED_DNS_FAILURE", "NETWORK_SELECTION_DISABLED_NO_INTERNET_TEMPORARY", "NETWORK_SELECTION_DISABLED_WPS_START", "NETWORK_SELECTION_DISABLED_TLS_VERSION", "NETWORK_SELECTION_DISABLED_AUTHENTICATION_NO_CREDENTIALS", "NETWORK_SELECTION_DISABLED_NO_INTERNET_PERMANENT", "NETWORK_SELECTION_DISABLED_BY_WIFI_MANAGER", "NETWORK_SELECTION_DISABLED_BY_USER_SWITCH", "NETWORK_SELECTION_DISABLED_BY_WRONG_PASSWORD"};
        private long mTemporarilyDisabledTimestamp = -1;
        private int[] mNetworkSeclectionDisableCounter = new int[14];
        private long mConnectChoiceTimestamp = -1;
        private boolean mHasEverConnected = false;

        public void setNotRecommended(boolean z) {
            this.mNotRecommended = z;
        }

        public boolean isNotRecommended() {
            return this.mNotRecommended;
        }

        public void setSeenInLastQualifiedNetworkSelection(boolean z) {
            this.mSeenInLastQualifiedNetworkSelection = z;
        }

        public boolean getSeenInLastQualifiedNetworkSelection() {
            return this.mSeenInLastQualifiedNetworkSelection;
        }

        public void setCandidate(ScanResult scanResult) {
            this.mCandidate = scanResult;
        }

        public ScanResult getCandidate() {
            return this.mCandidate;
        }

        public void setCandidateScore(int i) {
            this.mCandidateScore = i;
        }

        public int getCandidateScore() {
            return this.mCandidateScore;
        }

        public String getConnectChoice() {
            return this.mConnectChoice;
        }

        public void setConnectChoice(String str) {
            this.mConnectChoice = str;
        }

        public long getConnectChoiceTimestamp() {
            return this.mConnectChoiceTimestamp;
        }

        public void setConnectChoiceTimestamp(long j) {
            this.mConnectChoiceTimestamp = j;
        }

        public String getNetworkStatusString() {
            return QUALITY_NETWORK_SELECTION_STATUS[this.mStatus];
        }

        public void setHasEverConnected(boolean z) {
            this.mHasEverConnected = z;
        }

        public boolean getHasEverConnected() {
            return this.mHasEverConnected;
        }

        public static String getNetworkDisableReasonString(int i) {
            if (i < 0 || i >= 14) {
                return null;
            }
            return QUALITY_NETWORK_SELECTION_DISABLE_REASON[i];
        }

        public String getNetworkDisableReasonString() {
            return QUALITY_NETWORK_SELECTION_DISABLE_REASON[this.mNetworkSelectionDisableReason];
        }

        public int getNetworkSelectionStatus() {
            return this.mStatus;
        }

        public boolean isNetworkEnabled() {
            return this.mStatus == 0;
        }

        public boolean isNetworkTemporaryDisabled() {
            return this.mStatus == 1;
        }

        public boolean isNetworkPermanentlyDisabled() {
            return this.mStatus == 2;
        }

        public void setNetworkSelectionStatus(int i) {
            if (i < 0 || i >= 3) {
                return;
            }
            this.mStatus = i;
        }

        public int getNetworkSelectionDisableReason() {
            return this.mNetworkSelectionDisableReason;
        }

        public void setNetworkSelectionDisableReason(int i) {
            if (i >= 0 && i < 14) {
                this.mNetworkSelectionDisableReason = i;
                return;
            }
            throw new IllegalArgumentException("Illegal reason value: " + i);
        }

        public boolean isDisabledByReason(int i) {
            return this.mNetworkSelectionDisableReason == i;
        }

        public void setDisableTime(long j) {
            this.mTemporarilyDisabledTimestamp = j;
        }

        public long getDisableTime() {
            return this.mTemporarilyDisabledTimestamp;
        }

        public int getDisableReasonCounter(int i) {
            if (i >= 0 && i < 14) {
                return this.mNetworkSeclectionDisableCounter[i];
            }
            throw new IllegalArgumentException("Illegal reason value: " + i);
        }

        public void setDisableReasonCounter(int i, int i2) {
            if (i >= 0 && i < 14) {
                this.mNetworkSeclectionDisableCounter[i] = i2;
                return;
            }
            throw new IllegalArgumentException("Illegal reason value: " + i);
        }

        public void incrementDisableReasonCounter(int i) {
            if (i >= 0 && i < 14) {
                int[] iArr = this.mNetworkSeclectionDisableCounter;
                iArr[i] = iArr[i] + 1;
            } else {
                throw new IllegalArgumentException("Illegal reason value: " + i);
            }
        }

        public void clearDisableReasonCounter(int i) {
            if (i >= 0 && i < 14) {
                this.mNetworkSeclectionDisableCounter[i] = 0;
                return;
            }
            throw new IllegalArgumentException("Illegal reason value: " + i);
        }

        public void clearDisableReasonCounter() {
            Arrays.fill(this.mNetworkSeclectionDisableCounter, 0);
        }

        public String getNetworkSelectionBSSID() {
            return this.mNetworkSelectionBSSID;
        }

        public void setNetworkSelectionBSSID(String str) {
            this.mNetworkSelectionBSSID = str;
        }

        public void copy(NetworkSelectionStatus networkSelectionStatus) {
            this.mStatus = networkSelectionStatus.mStatus;
            this.mNetworkSelectionDisableReason = networkSelectionStatus.mNetworkSelectionDisableReason;
            for (int i = 0; i < 14; i++) {
                this.mNetworkSeclectionDisableCounter[i] = networkSelectionStatus.mNetworkSeclectionDisableCounter[i];
            }
            this.mTemporarilyDisabledTimestamp = networkSelectionStatus.mTemporarilyDisabledTimestamp;
            this.mNetworkSelectionBSSID = networkSelectionStatus.mNetworkSelectionBSSID;
            setSeenInLastQualifiedNetworkSelection(networkSelectionStatus.getSeenInLastQualifiedNetworkSelection());
            setCandidate(networkSelectionStatus.getCandidate());
            setCandidateScore(networkSelectionStatus.getCandidateScore());
            setConnectChoice(networkSelectionStatus.getConnectChoice());
            setConnectChoiceTimestamp(networkSelectionStatus.getConnectChoiceTimestamp());
            setHasEverConnected(networkSelectionStatus.getHasEverConnected());
            setNotRecommended(networkSelectionStatus.isNotRecommended());
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(getNetworkSelectionStatus());
            parcel.writeInt(getNetworkSelectionDisableReason());
            for (int i = 0; i < 14; i++) {
                parcel.writeInt(getDisableReasonCounter(i));
            }
            parcel.writeLong(getDisableTime());
            parcel.writeString(getNetworkSelectionBSSID());
            if (getConnectChoice() != null) {
                parcel.writeInt(1);
                parcel.writeString(getConnectChoice());
                parcel.writeLong(getConnectChoiceTimestamp());
            } else {
                parcel.writeInt(-1);
            }
            parcel.writeInt(getHasEverConnected() ? 1 : 0);
            parcel.writeInt(isNotRecommended() ? 1 : 0);
        }

        public void readFromParcel(Parcel parcel) {
            setNetworkSelectionStatus(parcel.readInt());
            setNetworkSelectionDisableReason(parcel.readInt());
            for (int i = 0; i < 14; i++) {
                setDisableReasonCounter(i, parcel.readInt());
            }
            setDisableTime(parcel.readLong());
            setNetworkSelectionBSSID(parcel.readString());
            if (parcel.readInt() == 1) {
                setConnectChoice(parcel.readString());
                setConnectChoiceTimestamp(parcel.readLong());
            } else {
                setConnectChoice(null);
                setConnectChoiceTimestamp(-1L);
            }
            setHasEverConnected(parcel.readInt() != 0);
            setNotRecommended(parcel.readInt() != 0);
        }
    }

    public static class RecentFailure {
        public static final int NONE = 0;
        public static final int STATUS_AP_UNABLE_TO_HANDLE_NEW_STA = 17;
        private int mAssociationStatus = 0;

        public void setAssociationStatus(int i) {
            this.mAssociationStatus = i;
        }

        public void clear() {
            this.mAssociationStatus = 0;
        }

        public int getAssociationStatus() {
            return this.mAssociationStatus;
        }
    }

    public NetworkSelectionStatus getNetworkSelectionStatus() {
        return this.mNetworkSelectionStatus;
    }

    public void setNetworkSelectionStatus(NetworkSelectionStatus networkSelectionStatus) {
        this.mNetworkSelectionStatus = networkSelectionStatus;
    }

    public WifiConfiguration() {
        this.apBand = 0;
        this.apChannel = 0;
        this.dtimInterval = 0;
        this.isLegacyPasspointConfig = false;
        this.userApproved = 0;
        this.meteredOverride = 0;
        this.mNetworkSelectionStatus = new NetworkSelectionStatus();
        this.recentFailure = new RecentFailure();
        this.networkId = -1;
        this.SSID = null;
        this.BSSID = null;
        this.FQDN = null;
        this.roamingConsortiumIds = new long[0];
        this.priority = 0;
        this.hiddenSSID = false;
        this.allowedKeyManagement = new BitSet();
        this.allowedProtocols = new BitSet();
        this.allowedAuthAlgorithms = new BitSet();
        this.allowedPairwiseCiphers = new BitSet();
        this.allowedGroupCiphers = new BitSet();
        this.wepKeys = new String[4];
        int i = 0;
        while (true) {
            String[] strArr = this.wepKeys;
            if (i < strArr.length) {
                strArr[i] = null;
                i++;
            } else {
                this.enterpriseConfig = new WifiEnterpriseConfig();
                this.selfAdded = false;
                this.didSelfAdd = false;
                this.ephemeral = false;
                this.meteredHint = false;
                this.meteredOverride = 0;
                this.useExternalScores = false;
                this.validatedInternetAccess = false;
                this.mIpConfiguration = new IpConfiguration();
                this.lastUpdateUid = -1;
                this.creatorUid = -1;
                this.shared = true;
                this.dtimInterval = 0;
                this.mRandomizedMacAddress = MacAddress.fromString("02:00:00:00:00:00");
                return;
            }
        }
    }

    public boolean isPasspoint() {
        WifiEnterpriseConfig wifiEnterpriseConfig;
        return (TextUtils.isEmpty(this.FQDN) || TextUtils.isEmpty(this.providerFriendlyName) || (wifiEnterpriseConfig = this.enterpriseConfig) == null || wifiEnterpriseConfig.getEapMethod() == -1) ? false : true;
    }

    public boolean isLinked(WifiConfiguration wifiConfiguration) {
        HashMap<String, Integer> map;
        return (wifiConfiguration == null || (map = wifiConfiguration.linkedConfigurations) == null || this.linkedConfigurations == null || map.get(configKey()) == null || this.linkedConfigurations.get(wifiConfiguration.configKey()) == null) ? false : true;
    }

    public boolean isEnterprise() {
        WifiEnterpriseConfig wifiEnterpriseConfig;
        return ((!this.allowedKeyManagement.get(2) && !this.allowedKeyManagement.get(3)) || (wifiEnterpriseConfig = this.enterpriseConfig) == null || wifiEnterpriseConfig.getEapMethod() == -1) ? false : true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = this.status;
        if (i == 0) {
            sb.append("* ");
        } else if (i == 1) {
            sb.append("- DSBLE ");
        }
        sb.append("ID: ");
        sb.append(this.networkId);
        sb.append(" SSID: ");
        sb.append(this.SSID);
        sb.append(" PROVIDER-NAME: ");
        sb.append(this.providerFriendlyName);
        sb.append(" BSSID: ");
        sb.append(this.BSSID);
        sb.append(" FQDN: ");
        sb.append(this.FQDN);
        sb.append(" PRIO: ");
        sb.append(this.priority);
        sb.append(" HIDDEN: ");
        sb.append(this.hiddenSSID);
        sb.append('\n');
        sb.append(" NetworkSelectionStatus ");
        sb.append(this.mNetworkSelectionStatus.getNetworkStatusString() + "\n");
        if (this.mNetworkSelectionStatus.getNetworkSelectionDisableReason() > 0) {
            sb.append(" mNetworkSelectionDisableReason ");
            sb.append(this.mNetworkSelectionStatus.getNetworkDisableReasonString() + "\n");
            int i2 = 0;
            while (true) {
                NetworkSelectionStatus networkSelectionStatus = this.mNetworkSelectionStatus;
                if (i2 >= 14) {
                    break;
                }
                if (networkSelectionStatus.getDisableReasonCounter(i2) != 0) {
                    sb.append(NetworkSelectionStatus.getNetworkDisableReasonString(i2) + " counter:" + this.mNetworkSelectionStatus.getDisableReasonCounter(i2) + "\n");
                }
                i2++;
            }
        }
        if (this.mNetworkSelectionStatus.getConnectChoice() != null) {
            sb.append(" connect choice: ");
            sb.append(this.mNetworkSelectionStatus.getConnectChoice());
            sb.append(" connect choice set time: ");
            sb.append(TimeUtils.logTimeOfDay(this.mNetworkSelectionStatus.getConnectChoiceTimestamp()));
        }
        sb.append(" hasEverConnected: ");
        sb.append(this.mNetworkSelectionStatus.getHasEverConnected());
        sb.append("\n");
        if (this.numAssociation > 0) {
            sb.append(" numAssociation ");
            sb.append(this.numAssociation);
            sb.append("\n");
        }
        if (this.numNoInternetAccessReports > 0) {
            sb.append(" numNoInternetAccessReports ");
            sb.append(this.numNoInternetAccessReports);
            sb.append("\n");
        }
        if (this.updateTime != null) {
            sb.append(" update ");
            sb.append(this.updateTime);
            sb.append("\n");
        }
        if (this.creationTime != null) {
            sb.append(" creation ");
            sb.append(this.creationTime);
            sb.append("\n");
        }
        if (this.didSelfAdd) {
            sb.append(" didSelfAdd");
        }
        if (this.selfAdded) {
            sb.append(" selfAdded");
        }
        if (this.validatedInternetAccess) {
            sb.append(" validatedInternetAccess");
        }
        if (this.ephemeral) {
            sb.append(" ephemeral");
        }
        if (this.meteredHint) {
            sb.append(" meteredHint");
        }
        if (this.useExternalScores) {
            sb.append(" useExternalScores");
        }
        if (this.didSelfAdd || this.selfAdded || this.validatedInternetAccess || this.ephemeral || this.meteredHint || this.useExternalScores) {
            sb.append("\n");
        }
        if (this.meteredOverride != 0) {
            sb.append(" meteredOverride ");
            sb.append(this.meteredOverride);
            sb.append("\n");
        }
        sb.append(" KeyMgmt:");
        for (int i3 = 0; i3 < this.allowedKeyManagement.size(); i3++) {
            if (this.allowedKeyManagement.get(i3)) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                if (i3 < KeyMgmt.strings.length) {
                    sb.append(KeyMgmt.strings[i3]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append(" Protocols:");
        for (int i4 = 0; i4 < this.allowedProtocols.size(); i4++) {
            if (this.allowedProtocols.get(i4)) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                if (i4 < Protocol.strings.length) {
                    sb.append(Protocol.strings[i4]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" AuthAlgorithms:");
        for (int i5 = 0; i5 < this.allowedAuthAlgorithms.size(); i5++) {
            if (this.allowedAuthAlgorithms.get(i5)) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                if (i5 < AuthAlgorithm.strings.length) {
                    sb.append(AuthAlgorithm.strings[i5]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" PairwiseCiphers:");
        for (int i6 = 0; i6 < this.allowedPairwiseCiphers.size(); i6++) {
            if (this.allowedPairwiseCiphers.get(i6)) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                if (i6 < PairwiseCipher.strings.length) {
                    sb.append(PairwiseCipher.strings[i6]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" GroupCiphers:");
        for (int i7 = 0; i7 < this.allowedGroupCiphers.size(); i7++) {
            if (this.allowedGroupCiphers.get(i7)) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                if (i7 < GroupCipher.strings.length) {
                    sb.append(GroupCipher.strings[i7]);
                } else {
                    sb.append("??");
                }
            }
        }
        sb.append('\n');
        sb.append(" PSK: ");
        if (this.preSharedKey != null) {
            sb.append('*');
        }
        sb.append("\nEnterprise config:\n");
        sb.append(this.enterpriseConfig);
        sb.append("IP config:\n");
        sb.append(this.mIpConfiguration.toString());
        if (this.mNetworkSelectionStatus.getNetworkSelectionBSSID() != null) {
            sb.append(" networkSelectionBSSID=" + this.mNetworkSelectionStatus.getNetworkSelectionBSSID());
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mNetworkSelectionStatus.getDisableTime() != -1) {
            sb.append('\n');
            long disableTime = jElapsedRealtime - this.mNetworkSelectionStatus.getDisableTime();
            if (disableTime <= 0) {
                sb.append(" blackListed since <incorrect>");
            } else {
                sb.append(" blackListed: ");
                sb.append(Long.toString(disableTime / 1000));
                sb.append("sec ");
            }
        }
        if (this.creatorUid != 0) {
            sb.append(" cuid=" + this.creatorUid);
        }
        if (this.creatorName != null) {
            sb.append(" cname=" + this.creatorName);
        }
        if (this.lastUpdateUid != 0) {
            sb.append(" luid=" + this.lastUpdateUid);
        }
        if (this.lastUpdateName != null) {
            sb.append(" lname=" + this.lastUpdateName);
        }
        sb.append(" lcuid=" + this.lastConnectUid);
        sb.append(" userApproved=" + userApprovedAsString(this.userApproved));
        sb.append(" noInternetAccessExpected=" + this.noInternetAccessExpected);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        if (this.lastConnected != 0) {
            sb.append('\n');
            sb.append("lastConnected: ");
            sb.append(TimeUtils.logTimeOfDay(this.lastConnected));
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        }
        sb.append('\n');
        HashMap<String, Integer> map = this.linkedConfigurations;
        if (map != null) {
            for (String str : map.keySet()) {
                sb.append(" linked: ");
                sb.append(str);
                sb.append('\n');
            }
        }
        sb.append("recentFailure: ");
        sb.append("Association Rejection code: ");
        sb.append(this.recentFailure.getAssociationStatus());
        sb.append("\n");
        return sb.toString();
    }

    public String getPrintableSsid() {
        String str = this.SSID;
        if (str == null) {
            return "";
        }
        int length = str.length();
        if (length > 2 && this.SSID.charAt(0) == '\"') {
            int i = length - 1;
            if (this.SSID.charAt(i) == '\"') {
                return this.SSID.substring(1, i);
            }
        }
        if (length > 3 && this.SSID.charAt(0) == 'P' && this.SSID.charAt(1) == '\"') {
            int i2 = length - 1;
            if (this.SSID.charAt(i2) == '\"') {
                return WifiSsid.createFromAsciiEncoded(this.SSID.substring(2, i2)).toString();
            }
        }
        return this.SSID;
    }

    public String getKeyIdForCredentials(WifiConfiguration wifiConfiguration) {
        try {
            if (TextUtils.isEmpty(this.SSID)) {
                this.SSID = wifiConfiguration.SSID;
            }
            if (this.allowedKeyManagement.cardinality() == 0) {
                this.allowedKeyManagement = wifiConfiguration.allowedKeyManagement;
            }
            String str = this.allowedKeyManagement.get(2) ? KeyMgmt.strings[2] : null;
            if (this.allowedKeyManagement.get(5)) {
                str = KeyMgmt.strings[5];
            }
            if (this.allowedKeyManagement.get(3)) {
                str = str + KeyMgmt.strings[3];
            }
            if (TextUtils.isEmpty(str)) {
                throw new IllegalStateException("Not an EAP network");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(trimStringForKeyId(this.SSID));
            sb.append(Session.SESSION_SEPARATION_CHAR_CHILD);
            sb.append(str);
            sb.append(Session.SESSION_SEPARATION_CHAR_CHILD);
            sb.append(trimStringForKeyId(this.enterpriseConfig.getKeyId(wifiConfiguration != null ? wifiConfiguration.enterpriseConfig : null)));
            return sb.toString();
        } catch (NullPointerException unused) {
            throw new IllegalStateException("Invalid config details");
        }
    }

    private String trimStringForKeyId(String str) {
        return str.replace("\"", "").replace(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER, "");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BitSet readBitSet(Parcel parcel) {
        int i = parcel.readInt();
        BitSet bitSet = new BitSet();
        for (int i2 = 0; i2 < i; i2++) {
            bitSet.set(parcel.readInt());
        }
        return bitSet;
    }

    private static void writeBitSet(Parcel parcel, BitSet bitSet) {
        parcel.writeInt(bitSet.cardinality());
        int iNextSetBit = -1;
        while (true) {
            iNextSetBit = bitSet.nextSetBit(iNextSetBit + 1);
            if (iNextSetBit == -1) {
                return;
            } else {
                parcel.writeInt(iNextSetBit);
            }
        }
    }

    public int getAuthType() {
        if (this.allowedKeyManagement.cardinality() > 1) {
            throw new IllegalStateException("More than one auth type set");
        }
        if (this.allowedKeyManagement.get(1)) {
            return 1;
        }
        if (this.allowedKeyManagement.get(4)) {
            return 4;
        }
        if (this.allowedKeyManagement.get(2)) {
            return 2;
        }
        return this.allowedKeyManagement.get(3) ? 3 : 0;
    }

    public String configKey(boolean z) {
        String str;
        String str2;
        if (z && (str2 = this.mCachedConfigKey) != null) {
            return str2;
        }
        if (this.providerFriendlyName != null) {
            String str3 = this.FQDN + KeyMgmt.strings[2];
            if (this.shared) {
                return str3;
            }
            return str3 + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + Integer.toString(UserHandle.getUserId(this.creatorUid));
        }
        if (this.allowedKeyManagement.get(1)) {
            str = this.SSID + KeyMgmt.strings[1];
        } else if (this.allowedKeyManagement.get(2) || this.allowedKeyManagement.get(3)) {
            str = this.SSID + KeyMgmt.strings[2];
        } else if (this.wepKeys[0] != null) {
            str = this.SSID + "WEP";
        } else {
            str = this.SSID + KeyMgmt.strings[0];
        }
        if (!this.shared) {
            str = str + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + Integer.toString(UserHandle.getUserId(this.creatorUid));
        }
        this.mCachedConfigKey = str;
        return str;
    }

    public String configKey() {
        return configKey(false);
    }

    public IpConfiguration getIpConfiguration() {
        return this.mIpConfiguration;
    }

    public void setIpConfiguration(IpConfiguration ipConfiguration) {
        if (ipConfiguration == null) {
            ipConfiguration = new IpConfiguration();
        }
        this.mIpConfiguration = ipConfiguration;
    }

    public StaticIpConfiguration getStaticIpConfiguration() {
        return this.mIpConfiguration.getStaticIpConfiguration();
    }

    public void setStaticIpConfiguration(StaticIpConfiguration staticIpConfiguration) {
        this.mIpConfiguration.setStaticIpConfiguration(staticIpConfiguration);
    }

    public IpConfiguration.IpAssignment getIpAssignment() {
        return this.mIpConfiguration.ipAssignment;
    }

    public void setIpAssignment(IpConfiguration.IpAssignment ipAssignment) {
        this.mIpConfiguration.ipAssignment = ipAssignment;
    }

    public IpConfiguration.ProxySettings getProxySettings() {
        return this.mIpConfiguration.proxySettings;
    }

    public void setProxySettings(IpConfiguration.ProxySettings proxySettings) {
        this.mIpConfiguration.proxySettings = proxySettings;
    }

    public ProxyInfo getHttpProxy() {
        if (this.mIpConfiguration.proxySettings == IpConfiguration.ProxySettings.NONE) {
            return null;
        }
        return new ProxyInfo(this.mIpConfiguration.httpProxy);
    }

    public void setHttpProxy(ProxyInfo proxyInfo) {
        IpConfiguration.ProxySettings proxySettings;
        ProxyInfo proxyInfo2;
        if (proxyInfo == null) {
            this.mIpConfiguration.setProxySettings(IpConfiguration.ProxySettings.NONE);
            this.mIpConfiguration.setHttpProxy(null);
            return;
        }
        if (!Uri.EMPTY.equals(proxyInfo.getPacFileUrl())) {
            proxySettings = IpConfiguration.ProxySettings.PAC;
            proxyInfo2 = new ProxyInfo(proxyInfo.getPacFileUrl(), proxyInfo.getPort());
        } else {
            proxySettings = IpConfiguration.ProxySettings.STATIC;
            proxyInfo2 = new ProxyInfo(proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getExclusionListAsString());
        }
        if (!proxyInfo2.isValid()) {
            throw new IllegalArgumentException("Invalid ProxyInfo: " + proxyInfo2.toString());
        }
        this.mIpConfiguration.setProxySettings(proxySettings);
        this.mIpConfiguration.setHttpProxy(proxyInfo2);
    }

    public void setProxy(IpConfiguration.ProxySettings proxySettings, ProxyInfo proxyInfo) {
        this.mIpConfiguration.proxySettings = proxySettings;
        this.mIpConfiguration.httpProxy = proxyInfo;
    }

    public void setPasspointManagementObjectTree(String str) {
        this.mPasspointManagementObjectTree = str;
    }

    public String getMoTree() {
        return this.mPasspointManagementObjectTree;
    }

    public WifiConfiguration(WifiConfiguration wifiConfiguration) {
        int i = 0;
        this.apBand = 0;
        this.apChannel = 0;
        this.dtimInterval = 0;
        this.isLegacyPasspointConfig = false;
        this.userApproved = 0;
        this.meteredOverride = 0;
        this.mNetworkSelectionStatus = new NetworkSelectionStatus();
        this.recentFailure = new RecentFailure();
        if (wifiConfiguration != null) {
            this.networkId = wifiConfiguration.networkId;
            this.status = wifiConfiguration.status;
            this.SSID = wifiConfiguration.SSID;
            this.BSSID = wifiConfiguration.BSSID;
            this.FQDN = wifiConfiguration.FQDN;
            this.roamingConsortiumIds = (long[]) wifiConfiguration.roamingConsortiumIds.clone();
            this.providerFriendlyName = wifiConfiguration.providerFriendlyName;
            this.isHomeProviderNetwork = wifiConfiguration.isHomeProviderNetwork;
            this.preSharedKey = wifiConfiguration.preSharedKey;
            this.mNetworkSelectionStatus.copy(wifiConfiguration.getNetworkSelectionStatus());
            this.apBand = wifiConfiguration.apBand;
            this.apChannel = wifiConfiguration.apChannel;
            this.wepKeys = new String[4];
            while (true) {
                String[] strArr = this.wepKeys;
                if (i >= strArr.length) {
                    break;
                }
                strArr[i] = wifiConfiguration.wepKeys[i];
                i++;
            }
            this.wepTxKeyIndex = wifiConfiguration.wepTxKeyIndex;
            this.priority = wifiConfiguration.priority;
            this.hiddenSSID = wifiConfiguration.hiddenSSID;
            this.allowedKeyManagement = (BitSet) wifiConfiguration.allowedKeyManagement.clone();
            this.allowedProtocols = (BitSet) wifiConfiguration.allowedProtocols.clone();
            this.allowedAuthAlgorithms = (BitSet) wifiConfiguration.allowedAuthAlgorithms.clone();
            this.allowedPairwiseCiphers = (BitSet) wifiConfiguration.allowedPairwiseCiphers.clone();
            this.allowedGroupCiphers = (BitSet) wifiConfiguration.allowedGroupCiphers.clone();
            this.enterpriseConfig = new WifiEnterpriseConfig(wifiConfiguration.enterpriseConfig);
            this.defaultGwMacAddress = wifiConfiguration.defaultGwMacAddress;
            this.mIpConfiguration = new IpConfiguration(wifiConfiguration.mIpConfiguration);
            HashMap<String, Integer> map = wifiConfiguration.linkedConfigurations;
            if (map != null && map.size() > 0) {
                HashMap<String, Integer> map2 = new HashMap<>();
                this.linkedConfigurations = map2;
                map2.putAll(wifiConfiguration.linkedConfigurations);
            }
            this.mCachedConfigKey = null;
            this.selfAdded = wifiConfiguration.selfAdded;
            this.validatedInternetAccess = wifiConfiguration.validatedInternetAccess;
            this.isLegacyPasspointConfig = wifiConfiguration.isLegacyPasspointConfig;
            this.ephemeral = wifiConfiguration.ephemeral;
            this.meteredHint = wifiConfiguration.meteredHint;
            this.meteredOverride = wifiConfiguration.meteredOverride;
            this.useExternalScores = wifiConfiguration.useExternalScores;
            this.didSelfAdd = wifiConfiguration.didSelfAdd;
            this.lastConnectUid = wifiConfiguration.lastConnectUid;
            this.lastUpdateUid = wifiConfiguration.lastUpdateUid;
            this.creatorUid = wifiConfiguration.creatorUid;
            this.creatorName = wifiConfiguration.creatorName;
            this.lastUpdateName = wifiConfiguration.lastUpdateName;
            this.peerWifiConfiguration = wifiConfiguration.peerWifiConfiguration;
            this.lastConnected = wifiConfiguration.lastConnected;
            this.lastDisconnected = wifiConfiguration.lastDisconnected;
            this.numScorerOverride = wifiConfiguration.numScorerOverride;
            this.numScorerOverrideAndSwitchedNetwork = wifiConfiguration.numScorerOverrideAndSwitchedNetwork;
            this.numAssociation = wifiConfiguration.numAssociation;
            this.userApproved = wifiConfiguration.userApproved;
            this.numNoInternetAccessReports = wifiConfiguration.numNoInternetAccessReports;
            this.noInternetAccessExpected = wifiConfiguration.noInternetAccessExpected;
            this.creationTime = wifiConfiguration.creationTime;
            this.updateTime = wifiConfiguration.updateTime;
            this.shared = wifiConfiguration.shared;
            this.recentFailure.setAssociationStatus(wifiConfiguration.recentFailure.getAssociationStatus());
            this.mRandomizedMacAddress = wifiConfiguration.mRandomizedMacAddress;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.networkId);
        parcel.writeInt(this.status);
        this.mNetworkSelectionStatus.writeToParcel(parcel);
        parcel.writeString(this.SSID);
        parcel.writeString(this.BSSID);
        parcel.writeInt(this.apBand);
        parcel.writeInt(this.apChannel);
        parcel.writeString(this.FQDN);
        parcel.writeString(this.providerFriendlyName);
        parcel.writeInt(this.isHomeProviderNetwork ? 1 : 0);
        parcel.writeInt(this.roamingConsortiumIds.length);
        for (long j : this.roamingConsortiumIds) {
            parcel.writeLong(j);
        }
        parcel.writeString(this.preSharedKey);
        for (String str : this.wepKeys) {
            parcel.writeString(str);
        }
        parcel.writeInt(this.wepTxKeyIndex);
        parcel.writeInt(this.priority);
        parcel.writeInt(this.hiddenSSID ? 1 : 0);
        parcel.writeInt(this.requirePMF ? 1 : 0);
        parcel.writeString(this.updateIdentifier);
        writeBitSet(parcel, this.allowedKeyManagement);
        writeBitSet(parcel, this.allowedProtocols);
        writeBitSet(parcel, this.allowedAuthAlgorithms);
        writeBitSet(parcel, this.allowedPairwiseCiphers);
        writeBitSet(parcel, this.allowedGroupCiphers);
        parcel.writeParcelable(this.enterpriseConfig, i);
        parcel.writeParcelable(this.mIpConfiguration, i);
        parcel.writeString(this.dhcpServer);
        parcel.writeString(this.defaultGwMacAddress);
        parcel.writeInt(this.selfAdded ? 1 : 0);
        parcel.writeInt(this.didSelfAdd ? 1 : 0);
        parcel.writeInt(this.validatedInternetAccess ? 1 : 0);
        parcel.writeInt(this.isLegacyPasspointConfig ? 1 : 0);
        parcel.writeInt(this.ephemeral ? 1 : 0);
        parcel.writeInt(this.meteredHint ? 1 : 0);
        parcel.writeInt(this.meteredOverride);
        parcel.writeInt(this.useExternalScores ? 1 : 0);
        parcel.writeInt(this.creatorUid);
        parcel.writeInt(this.lastConnectUid);
        parcel.writeInt(this.lastUpdateUid);
        parcel.writeString(this.creatorName);
        parcel.writeString(this.lastUpdateName);
        parcel.writeInt(this.numScorerOverride);
        parcel.writeInt(this.numScorerOverrideAndSwitchedNetwork);
        parcel.writeInt(this.numAssociation);
        parcel.writeInt(this.userApproved);
        parcel.writeInt(this.numNoInternetAccessReports);
        parcel.writeInt(this.noInternetAccessExpected ? 1 : 0);
        parcel.writeInt(this.shared ? 1 : 0);
        parcel.writeString(this.mPasspointManagementObjectTree);
        parcel.writeInt(this.recentFailure.getAssociationStatus());
        parcel.writeParcelable(this.mRandomizedMacAddress, i);
    }

    public byte[] getBytesForBackup() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(2);
        BackupUtils.writeString(dataOutputStream, this.SSID);
        dataOutputStream.writeInt(this.apBand);
        dataOutputStream.writeInt(this.apChannel);
        BackupUtils.writeString(dataOutputStream, this.preSharedKey);
        dataOutputStream.writeInt(getAuthType());
        return byteArrayOutputStream.toByteArray();
    }

    public static WifiConfiguration getWifiConfigFromBackup(DataInputStream dataInputStream) throws BackupUtils.BadVersionException, IOException {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        int i = dataInputStream.readInt();
        if (i < 1 || i > 2) {
            throw new BackupUtils.BadVersionException("Unknown Backup Serialization Version");
        }
        if (i == 1) {
            return null;
        }
        wifiConfiguration.SSID = BackupUtils.readString(dataInputStream);
        wifiConfiguration.apBand = dataInputStream.readInt();
        wifiConfiguration.apChannel = dataInputStream.readInt();
        wifiConfiguration.preSharedKey = BackupUtils.readString(dataInputStream);
        wifiConfiguration.allowedKeyManagement.set(dataInputStream.readInt());
        return wifiConfiguration;
    }
}
