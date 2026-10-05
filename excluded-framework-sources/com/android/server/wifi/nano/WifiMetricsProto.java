package com.android.server.wifi.nano;

import android.app.admin.DevicePolicyManager;
import android.util.DisplayMetrics;
import com.android.framework.protobuf.nano.CodedInputByteBufferNano;
import com.android.framework.protobuf.nano.CodedOutputByteBufferNano;
import com.android.framework.protobuf.nano.InternalNano;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.framework.protobuf.nano.WireFormatNano;
import com.android.internal.logging.nano.MetricsProto;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public interface WifiMetricsProto {

    public static final class WifiLog extends MessageNano {
        public static final int FAILURE_WIFI_DISABLED = 4;
        public static final int SCAN_FAILURE_INTERRUPTED = 2;
        public static final int SCAN_FAILURE_INVALID_CONFIGURATION = 3;
        public static final int SCAN_SUCCESS = 1;
        public static final int SCAN_UNKNOWN = 0;
        public static final int WIFI_ASSOCIATED = 3;
        public static final int WIFI_DISABLED = 1;
        public static final int WIFI_DISCONNECTED = 2;
        public static final int WIFI_UNKNOWN = 0;
        private static volatile WifiLog[] _emptyArray;
        public AlertReasonCount[] alertReasonCount;
        public NumConnectableNetworksBucket[] availableOpenBssidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableOpenOrSavedBssidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableOpenOrSavedSsidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableOpenSsidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableSavedBssidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableSavedPasspointProviderBssidsInScanHistogram;
        public NumConnectableNetworksBucket[] availableSavedPasspointProviderProfilesInScanHistogram;
        public NumConnectableNetworksBucket[] availableSavedSsidsInScanHistogram;
        public WifiSystemStateEntry[] backgroundScanRequestState;
        public ScanReturnEntry[] backgroundScanReturnEntries;
        public ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationActionCount;
        public ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationCount;
        public ConnectionEvent[] connectionEvent;
        public int fullBandAllSingleScanListenerResults;
        public boolean isLocationEnabled;
        public boolean isMacRandomizationOn;
        public boolean isScanningAlwaysEnabled;
        public boolean isWifiNetworksAvailableNotificationOn;
        public int numBackgroundScans;
        public int numClientInterfaceDown;
        public int numConnectivityOneshotScans;
        public int numConnectivityWatchdogBackgroundBad;
        public int numConnectivityWatchdogBackgroundGood;
        public int numConnectivityWatchdogPnoBad;
        public int numConnectivityWatchdogPnoGood;
        public int numEmptyScanResults;
        public int numEnterpriseNetworkScanResults;
        public int numEnterpriseNetworks;
        public int numExternalAppOneshotScanRequests;
        public int numExternalBackgroundAppOneshotScanRequestsThrottled;
        public int numExternalForegroundAppOneshotScanRequestsThrottled;
        public int numHalCrashes;
        public int numHiddenNetworkScanResults;
        public int numHiddenNetworks;
        public int numHostapdCrashes;
        public int numHotspot2R1NetworkScanResults;
        public int numHotspot2R2NetworkScanResults;
        public int numLastResortWatchdogAvailableNetworksTotal;
        public int numLastResortWatchdogBadAssociationNetworksTotal;
        public int numLastResortWatchdogBadAuthenticationNetworksTotal;
        public int numLastResortWatchdogBadDhcpNetworksTotal;
        public int numLastResortWatchdogBadOtherNetworksTotal;
        public int numLastResortWatchdogSuccesses;
        public int numLastResortWatchdogTriggers;
        public int numLastResortWatchdogTriggersWithBadAssociation;
        public int numLastResortWatchdogTriggersWithBadAuthentication;
        public int numLastResortWatchdogTriggersWithBadDhcp;
        public int numLastResortWatchdogTriggersWithBadOther;
        public int numNetworksAddedByApps;
        public int numNetworksAddedByUser;
        public int numNonEmptyScanResults;
        public int numOneshotHasDfsChannelScans;
        public int numOneshotScans;
        public int numOpenNetworkConnectMessageFailedToSend;
        public int numOpenNetworkRecommendationUpdates;
        public int numOpenNetworkScanResults;
        public int numOpenNetworks;
        public int numPasspointNetworks;
        public int numPasspointProviderInstallSuccess;
        public int numPasspointProviderInstallation;
        public int numPasspointProviderUninstallSuccess;
        public int numPasspointProviderUninstallation;
        public int numPasspointProviders;
        public int numPasspointProvidersSuccessfullyConnected;
        public int numPersonalNetworkScanResults;
        public int numPersonalNetworks;
        public int numRadioModeChangeToDbs;
        public int numRadioModeChangeToMcc;
        public int numRadioModeChangeToSbs;
        public int numRadioModeChangeToScc;
        public int numSavedNetworks;
        public int numScans;
        public int numSetupClientInterfaceFailureDueToHal;
        public int numSetupClientInterfaceFailureDueToSupplicant;
        public int numSetupClientInterfaceFailureDueToWificond;
        public int numSetupSoftApInterfaceFailureDueToHal;
        public int numSetupSoftApInterfaceFailureDueToHostapd;
        public int numSetupSoftApInterfaceFailureDueToWificond;
        public int numSoftApInterfaceDown;
        public int numSoftApUserBandPreferenceUnsatisfied;
        public int numSupplicantCrashes;
        public int numTotalScanResults;
        public int numWifiToggledViaAirplane;
        public int numWifiToggledViaSettings;
        public int numWificondCrashes;
        public NumConnectableNetworksBucket[] observed80211McSupportingApsInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR1ApsInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR1ApsPerEssInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR1EssInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR2ApsInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR2ApsPerEssInScanHistogram;
        public NumConnectableNetworksBucket[] observedHotspotR2EssInScanHistogram;
        public int openNetworkRecommenderBlacklistSize;
        public int partialAllSingleScanListenerResults;
        public PnoScanMetrics pnoScanMetrics;
        public int recordDurationSec;
        public RssiPollCount[] rssiPollDeltaCount;
        public RssiPollCount[] rssiPollRssiCount;
        public ScanReturnEntry[] scanReturnEntries;
        public String scoreExperimentId;
        public SoftApConnectedClientsEvent[] softApConnectedClientsEventsLocalOnly;
        public SoftApConnectedClientsEvent[] softApConnectedClientsEventsTethered;
        public SoftApDurationBucket[] softApDuration;
        public SoftApReturnCodeCount[] softApReturnCode;
        public StaEvent[] staEventList;
        public NumConnectableNetworksBucket[] totalBssidsInScanHistogram;
        public NumConnectableNetworksBucket[] totalSsidsInScanHistogram;
        public long watchdogTotalConnectionFailureCountAfterTrigger;
        public long watchdogTriggerToConnectionSuccessDurationMs;
        public WifiAwareLog wifiAwareLog;
        public WifiPowerStats wifiPowerStats;
        public WifiRttLog wifiRttLog;
        public WifiScoreCount[] wifiScoreCount;
        public WifiSystemStateEntry[] wifiSystemStateEntries;
        public WifiWakeStats wifiWakeStats;
        public WpsMetrics wpsMetrics;

        public static final class ScanReturnEntry extends MessageNano {
            private static volatile ScanReturnEntry[] _emptyArray;
            public int scanResultsCount;
            public int scanReturnCode;

            public static ScanReturnEntry[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new ScanReturnEntry[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public ScanReturnEntry() {
                clear();
            }

            public ScanReturnEntry clear() {
                this.scanReturnCode = 0;
                this.scanResultsCount = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.scanReturnCode;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.scanResultsCount;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.scanReturnCode;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.scanResultsCount;
                return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public ScanReturnEntry mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int int32 = codedInputByteBufferNano.readInt32();
                        if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3 || int32 == 4) {
                            this.scanReturnCode = int32;
                        }
                    } else if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.scanResultsCount = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static ScanReturnEntry parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (ScanReturnEntry) MessageNano.mergeFrom(new ScanReturnEntry(), bArr);
            }

            public static ScanReturnEntry parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new ScanReturnEntry().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static final class WifiSystemStateEntry extends MessageNano {
            private static volatile WifiSystemStateEntry[] _emptyArray;
            public boolean isScreenOn;
            public int wifiState;
            public int wifiStateCount;

            public static WifiSystemStateEntry[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new WifiSystemStateEntry[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public WifiSystemStateEntry() {
                clear();
            }

            public WifiSystemStateEntry clear() {
                this.wifiState = 0;
                this.wifiStateCount = 0;
                this.isScreenOn = false;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.wifiState;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.wifiStateCount;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                boolean z = this.isScreenOn;
                if (z) {
                    codedOutputByteBufferNano.writeBool(3, z);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.wifiState;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.wifiStateCount;
                if (i2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                boolean z = this.isScreenOn;
                return z ? iComputeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(3, z) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public WifiSystemStateEntry mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int int32 = codedInputByteBufferNano.readInt32();
                        if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                            this.wifiState = int32;
                        }
                    } else if (tag == 16) {
                        this.wifiStateCount = codedInputByteBufferNano.readInt32();
                    } else if (tag != 24) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.isScreenOn = codedInputByteBufferNano.readBool();
                    }
                }
            }

            public static WifiSystemStateEntry parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (WifiSystemStateEntry) MessageNano.mergeFrom(new WifiSystemStateEntry(), bArr);
            }

            public static WifiSystemStateEntry parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new WifiSystemStateEntry().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static WifiLog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiLog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiLog() {
            clear();
        }

        public WifiLog clear() {
            this.connectionEvent = ConnectionEvent.emptyArray();
            this.numSavedNetworks = 0;
            this.numOpenNetworks = 0;
            this.numPersonalNetworks = 0;
            this.numEnterpriseNetworks = 0;
            this.isLocationEnabled = false;
            this.isScanningAlwaysEnabled = false;
            this.numWifiToggledViaSettings = 0;
            this.numWifiToggledViaAirplane = 0;
            this.numNetworksAddedByUser = 0;
            this.numNetworksAddedByApps = 0;
            this.numEmptyScanResults = 0;
            this.numNonEmptyScanResults = 0;
            this.numOneshotScans = 0;
            this.numBackgroundScans = 0;
            this.scanReturnEntries = ScanReturnEntry.emptyArray();
            this.wifiSystemStateEntries = WifiSystemStateEntry.emptyArray();
            this.backgroundScanReturnEntries = ScanReturnEntry.emptyArray();
            this.backgroundScanRequestState = WifiSystemStateEntry.emptyArray();
            this.numLastResortWatchdogTriggers = 0;
            this.numLastResortWatchdogBadAssociationNetworksTotal = 0;
            this.numLastResortWatchdogBadAuthenticationNetworksTotal = 0;
            this.numLastResortWatchdogBadDhcpNetworksTotal = 0;
            this.numLastResortWatchdogBadOtherNetworksTotal = 0;
            this.numLastResortWatchdogAvailableNetworksTotal = 0;
            this.numLastResortWatchdogTriggersWithBadAssociation = 0;
            this.numLastResortWatchdogTriggersWithBadAuthentication = 0;
            this.numLastResortWatchdogTriggersWithBadDhcp = 0;
            this.numLastResortWatchdogTriggersWithBadOther = 0;
            this.numConnectivityWatchdogPnoGood = 0;
            this.numConnectivityWatchdogPnoBad = 0;
            this.numConnectivityWatchdogBackgroundGood = 0;
            this.numConnectivityWatchdogBackgroundBad = 0;
            this.recordDurationSec = 0;
            this.rssiPollRssiCount = RssiPollCount.emptyArray();
            this.numLastResortWatchdogSuccesses = 0;
            this.numHiddenNetworks = 0;
            this.numPasspointNetworks = 0;
            this.numTotalScanResults = 0;
            this.numOpenNetworkScanResults = 0;
            this.numPersonalNetworkScanResults = 0;
            this.numEnterpriseNetworkScanResults = 0;
            this.numHiddenNetworkScanResults = 0;
            this.numHotspot2R1NetworkScanResults = 0;
            this.numHotspot2R2NetworkScanResults = 0;
            this.numScans = 0;
            this.alertReasonCount = AlertReasonCount.emptyArray();
            this.wifiScoreCount = WifiScoreCount.emptyArray();
            this.softApDuration = SoftApDurationBucket.emptyArray();
            this.softApReturnCode = SoftApReturnCodeCount.emptyArray();
            this.rssiPollDeltaCount = RssiPollCount.emptyArray();
            this.staEventList = StaEvent.emptyArray();
            this.numHalCrashes = 0;
            this.numWificondCrashes = 0;
            this.numSetupClientInterfaceFailureDueToHal = 0;
            this.numSetupClientInterfaceFailureDueToWificond = 0;
            this.wifiAwareLog = null;
            this.numPasspointProviders = 0;
            this.numPasspointProviderInstallation = 0;
            this.numPasspointProviderInstallSuccess = 0;
            this.numPasspointProviderUninstallation = 0;
            this.numPasspointProviderUninstallSuccess = 0;
            this.numPasspointProvidersSuccessfullyConnected = 0;
            this.totalSsidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.totalBssidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableOpenSsidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableOpenBssidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableSavedSsidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableSavedBssidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableOpenOrSavedSsidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableOpenOrSavedBssidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableSavedPasspointProviderProfilesInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.availableSavedPasspointProviderBssidsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.fullBandAllSingleScanListenerResults = 0;
            this.partialAllSingleScanListenerResults = 0;
            this.pnoScanMetrics = null;
            this.connectToNetworkNotificationCount = ConnectToNetworkNotificationAndActionCount.emptyArray();
            this.connectToNetworkNotificationActionCount = ConnectToNetworkNotificationAndActionCount.emptyArray();
            this.openNetworkRecommenderBlacklistSize = 0;
            this.isWifiNetworksAvailableNotificationOn = false;
            this.numOpenNetworkRecommendationUpdates = 0;
            this.numOpenNetworkConnectMessageFailedToSend = 0;
            this.observedHotspotR1ApsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.observedHotspotR2ApsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.observedHotspotR1EssInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.observedHotspotR2EssInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.observedHotspotR1ApsPerEssInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.observedHotspotR2ApsPerEssInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.softApConnectedClientsEventsTethered = SoftApConnectedClientsEvent.emptyArray();
            this.softApConnectedClientsEventsLocalOnly = SoftApConnectedClientsEvent.emptyArray();
            this.wpsMetrics = null;
            this.wifiPowerStats = null;
            this.numConnectivityOneshotScans = 0;
            this.wifiWakeStats = null;
            this.observed80211McSupportingApsInScanHistogram = NumConnectableNetworksBucket.emptyArray();
            this.numSupplicantCrashes = 0;
            this.numHostapdCrashes = 0;
            this.numSetupClientInterfaceFailureDueToSupplicant = 0;
            this.numSetupSoftApInterfaceFailureDueToHal = 0;
            this.numSetupSoftApInterfaceFailureDueToWificond = 0;
            this.numSetupSoftApInterfaceFailureDueToHostapd = 0;
            this.numClientInterfaceDown = 0;
            this.numSoftApInterfaceDown = 0;
            this.numExternalAppOneshotScanRequests = 0;
            this.numExternalForegroundAppOneshotScanRequestsThrottled = 0;
            this.numExternalBackgroundAppOneshotScanRequestsThrottled = 0;
            this.watchdogTriggerToConnectionSuccessDurationMs = -1L;
            this.watchdogTotalConnectionFailureCountAfterTrigger = 0L;
            this.numOneshotHasDfsChannelScans = 0;
            this.wifiRttLog = null;
            this.isMacRandomizationOn = false;
            this.numRadioModeChangeToMcc = 0;
            this.numRadioModeChangeToScc = 0;
            this.numRadioModeChangeToSbs = 0;
            this.numRadioModeChangeToDbs = 0;
            this.numSoftApUserBandPreferenceUnsatisfied = 0;
            this.scoreExperimentId = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            ConnectionEvent[] connectionEventArr = this.connectionEvent;
            int i = 0;
            if (connectionEventArr != null && connectionEventArr.length > 0) {
                int i2 = 0;
                while (true) {
                    ConnectionEvent[] connectionEventArr2 = this.connectionEvent;
                    if (i2 >= connectionEventArr2.length) {
                        break;
                    }
                    ConnectionEvent connectionEvent = connectionEventArr2[i2];
                    if (connectionEvent != null) {
                        codedOutputByteBufferNano.writeMessage(1, connectionEvent);
                    }
                    i2++;
                }
            }
            int i3 = this.numSavedNetworks;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i3);
            }
            int i4 = this.numOpenNetworks;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i4);
            }
            int i5 = this.numPersonalNetworks;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i5);
            }
            int i6 = this.numEnterpriseNetworks;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i6);
            }
            boolean z = this.isLocationEnabled;
            if (z) {
                codedOutputByteBufferNano.writeBool(6, z);
            }
            boolean z2 = this.isScanningAlwaysEnabled;
            if (z2) {
                codedOutputByteBufferNano.writeBool(7, z2);
            }
            int i7 = this.numWifiToggledViaSettings;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i7);
            }
            int i8 = this.numWifiToggledViaAirplane;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(9, i8);
            }
            int i9 = this.numNetworksAddedByUser;
            if (i9 != 0) {
                codedOutputByteBufferNano.writeInt32(10, i9);
            }
            int i10 = this.numNetworksAddedByApps;
            if (i10 != 0) {
                codedOutputByteBufferNano.writeInt32(11, i10);
            }
            int i11 = this.numEmptyScanResults;
            if (i11 != 0) {
                codedOutputByteBufferNano.writeInt32(12, i11);
            }
            int i12 = this.numNonEmptyScanResults;
            if (i12 != 0) {
                codedOutputByteBufferNano.writeInt32(13, i12);
            }
            int i13 = this.numOneshotScans;
            if (i13 != 0) {
                codedOutputByteBufferNano.writeInt32(14, i13);
            }
            int i14 = this.numBackgroundScans;
            if (i14 != 0) {
                codedOutputByteBufferNano.writeInt32(15, i14);
            }
            ScanReturnEntry[] scanReturnEntryArr = this.scanReturnEntries;
            if (scanReturnEntryArr != null && scanReturnEntryArr.length > 0) {
                int i15 = 0;
                while (true) {
                    ScanReturnEntry[] scanReturnEntryArr2 = this.scanReturnEntries;
                    if (i15 >= scanReturnEntryArr2.length) {
                        break;
                    }
                    ScanReturnEntry scanReturnEntry = scanReturnEntryArr2[i15];
                    if (scanReturnEntry != null) {
                        codedOutputByteBufferNano.writeMessage(16, scanReturnEntry);
                    }
                    i15++;
                }
            }
            WifiSystemStateEntry[] wifiSystemStateEntryArr = this.wifiSystemStateEntries;
            if (wifiSystemStateEntryArr != null && wifiSystemStateEntryArr.length > 0) {
                int i16 = 0;
                while (true) {
                    WifiSystemStateEntry[] wifiSystemStateEntryArr2 = this.wifiSystemStateEntries;
                    if (i16 >= wifiSystemStateEntryArr2.length) {
                        break;
                    }
                    WifiSystemStateEntry wifiSystemStateEntry = wifiSystemStateEntryArr2[i16];
                    if (wifiSystemStateEntry != null) {
                        codedOutputByteBufferNano.writeMessage(17, wifiSystemStateEntry);
                    }
                    i16++;
                }
            }
            ScanReturnEntry[] scanReturnEntryArr3 = this.backgroundScanReturnEntries;
            if (scanReturnEntryArr3 != null && scanReturnEntryArr3.length > 0) {
                int i17 = 0;
                while (true) {
                    ScanReturnEntry[] scanReturnEntryArr4 = this.backgroundScanReturnEntries;
                    if (i17 >= scanReturnEntryArr4.length) {
                        break;
                    }
                    ScanReturnEntry scanReturnEntry2 = scanReturnEntryArr4[i17];
                    if (scanReturnEntry2 != null) {
                        codedOutputByteBufferNano.writeMessage(18, scanReturnEntry2);
                    }
                    i17++;
                }
            }
            WifiSystemStateEntry[] wifiSystemStateEntryArr3 = this.backgroundScanRequestState;
            if (wifiSystemStateEntryArr3 != null && wifiSystemStateEntryArr3.length > 0) {
                int i18 = 0;
                while (true) {
                    WifiSystemStateEntry[] wifiSystemStateEntryArr4 = this.backgroundScanRequestState;
                    if (i18 >= wifiSystemStateEntryArr4.length) {
                        break;
                    }
                    WifiSystemStateEntry wifiSystemStateEntry2 = wifiSystemStateEntryArr4[i18];
                    if (wifiSystemStateEntry2 != null) {
                        codedOutputByteBufferNano.writeMessage(19, wifiSystemStateEntry2);
                    }
                    i18++;
                }
            }
            int i19 = this.numLastResortWatchdogTriggers;
            if (i19 != 0) {
                codedOutputByteBufferNano.writeInt32(20, i19);
            }
            int i20 = this.numLastResortWatchdogBadAssociationNetworksTotal;
            if (i20 != 0) {
                codedOutputByteBufferNano.writeInt32(21, i20);
            }
            int i21 = this.numLastResortWatchdogBadAuthenticationNetworksTotal;
            if (i21 != 0) {
                codedOutputByteBufferNano.writeInt32(22, i21);
            }
            int i22 = this.numLastResortWatchdogBadDhcpNetworksTotal;
            if (i22 != 0) {
                codedOutputByteBufferNano.writeInt32(23, i22);
            }
            int i23 = this.numLastResortWatchdogBadOtherNetworksTotal;
            if (i23 != 0) {
                codedOutputByteBufferNano.writeInt32(24, i23);
            }
            int i24 = this.numLastResortWatchdogAvailableNetworksTotal;
            if (i24 != 0) {
                codedOutputByteBufferNano.writeInt32(25, i24);
            }
            int i25 = this.numLastResortWatchdogTriggersWithBadAssociation;
            if (i25 != 0) {
                codedOutputByteBufferNano.writeInt32(26, i25);
            }
            int i26 = this.numLastResortWatchdogTriggersWithBadAuthentication;
            if (i26 != 0) {
                codedOutputByteBufferNano.writeInt32(27, i26);
            }
            int i27 = this.numLastResortWatchdogTriggersWithBadDhcp;
            if (i27 != 0) {
                codedOutputByteBufferNano.writeInt32(28, i27);
            }
            int i28 = this.numLastResortWatchdogTriggersWithBadOther;
            if (i28 != 0) {
                codedOutputByteBufferNano.writeInt32(29, i28);
            }
            int i29 = this.numConnectivityWatchdogPnoGood;
            if (i29 != 0) {
                codedOutputByteBufferNano.writeInt32(30, i29);
            }
            int i30 = this.numConnectivityWatchdogPnoBad;
            if (i30 != 0) {
                codedOutputByteBufferNano.writeInt32(31, i30);
            }
            int i31 = this.numConnectivityWatchdogBackgroundGood;
            if (i31 != 0) {
                codedOutputByteBufferNano.writeInt32(32, i31);
            }
            int i32 = this.numConnectivityWatchdogBackgroundBad;
            if (i32 != 0) {
                codedOutputByteBufferNano.writeInt32(33, i32);
            }
            int i33 = this.recordDurationSec;
            if (i33 != 0) {
                codedOutputByteBufferNano.writeInt32(34, i33);
            }
            RssiPollCount[] rssiPollCountArr = this.rssiPollRssiCount;
            if (rssiPollCountArr != null && rssiPollCountArr.length > 0) {
                int i34 = 0;
                while (true) {
                    RssiPollCount[] rssiPollCountArr2 = this.rssiPollRssiCount;
                    if (i34 >= rssiPollCountArr2.length) {
                        break;
                    }
                    RssiPollCount rssiPollCount = rssiPollCountArr2[i34];
                    if (rssiPollCount != null) {
                        codedOutputByteBufferNano.writeMessage(35, rssiPollCount);
                    }
                    i34++;
                }
            }
            int i35 = this.numLastResortWatchdogSuccesses;
            if (i35 != 0) {
                codedOutputByteBufferNano.writeInt32(36, i35);
            }
            int i36 = this.numHiddenNetworks;
            if (i36 != 0) {
                codedOutputByteBufferNano.writeInt32(37, i36);
            }
            int i37 = this.numPasspointNetworks;
            if (i37 != 0) {
                codedOutputByteBufferNano.writeInt32(38, i37);
            }
            int i38 = this.numTotalScanResults;
            if (i38 != 0) {
                codedOutputByteBufferNano.writeInt32(39, i38);
            }
            int i39 = this.numOpenNetworkScanResults;
            if (i39 != 0) {
                codedOutputByteBufferNano.writeInt32(40, i39);
            }
            int i40 = this.numPersonalNetworkScanResults;
            if (i40 != 0) {
                codedOutputByteBufferNano.writeInt32(41, i40);
            }
            int i41 = this.numEnterpriseNetworkScanResults;
            if (i41 != 0) {
                codedOutputByteBufferNano.writeInt32(42, i41);
            }
            int i42 = this.numHiddenNetworkScanResults;
            if (i42 != 0) {
                codedOutputByteBufferNano.writeInt32(43, i42);
            }
            int i43 = this.numHotspot2R1NetworkScanResults;
            if (i43 != 0) {
                codedOutputByteBufferNano.writeInt32(44, i43);
            }
            int i44 = this.numHotspot2R2NetworkScanResults;
            if (i44 != 0) {
                codedOutputByteBufferNano.writeInt32(45, i44);
            }
            int i45 = this.numScans;
            if (i45 != 0) {
                codedOutputByteBufferNano.writeInt32(46, i45);
            }
            AlertReasonCount[] alertReasonCountArr = this.alertReasonCount;
            if (alertReasonCountArr != null && alertReasonCountArr.length > 0) {
                int i46 = 0;
                while (true) {
                    AlertReasonCount[] alertReasonCountArr2 = this.alertReasonCount;
                    if (i46 >= alertReasonCountArr2.length) {
                        break;
                    }
                    AlertReasonCount alertReasonCount = alertReasonCountArr2[i46];
                    if (alertReasonCount != null) {
                        codedOutputByteBufferNano.writeMessage(47, alertReasonCount);
                    }
                    i46++;
                }
            }
            WifiScoreCount[] wifiScoreCountArr = this.wifiScoreCount;
            if (wifiScoreCountArr != null && wifiScoreCountArr.length > 0) {
                int i47 = 0;
                while (true) {
                    WifiScoreCount[] wifiScoreCountArr2 = this.wifiScoreCount;
                    if (i47 >= wifiScoreCountArr2.length) {
                        break;
                    }
                    WifiScoreCount wifiScoreCount = wifiScoreCountArr2[i47];
                    if (wifiScoreCount != null) {
                        codedOutputByteBufferNano.writeMessage(48, wifiScoreCount);
                    }
                    i47++;
                }
            }
            SoftApDurationBucket[] softApDurationBucketArr = this.softApDuration;
            if (softApDurationBucketArr != null && softApDurationBucketArr.length > 0) {
                int i48 = 0;
                while (true) {
                    SoftApDurationBucket[] softApDurationBucketArr2 = this.softApDuration;
                    if (i48 >= softApDurationBucketArr2.length) {
                        break;
                    }
                    SoftApDurationBucket softApDurationBucket = softApDurationBucketArr2[i48];
                    if (softApDurationBucket != null) {
                        codedOutputByteBufferNano.writeMessage(49, softApDurationBucket);
                    }
                    i48++;
                }
            }
            SoftApReturnCodeCount[] softApReturnCodeCountArr = this.softApReturnCode;
            if (softApReturnCodeCountArr != null && softApReturnCodeCountArr.length > 0) {
                int i49 = 0;
                while (true) {
                    SoftApReturnCodeCount[] softApReturnCodeCountArr2 = this.softApReturnCode;
                    if (i49 >= softApReturnCodeCountArr2.length) {
                        break;
                    }
                    SoftApReturnCodeCount softApReturnCodeCount = softApReturnCodeCountArr2[i49];
                    if (softApReturnCodeCount != null) {
                        codedOutputByteBufferNano.writeMessage(50, softApReturnCodeCount);
                    }
                    i49++;
                }
            }
            RssiPollCount[] rssiPollCountArr3 = this.rssiPollDeltaCount;
            if (rssiPollCountArr3 != null && rssiPollCountArr3.length > 0) {
                int i50 = 0;
                while (true) {
                    RssiPollCount[] rssiPollCountArr4 = this.rssiPollDeltaCount;
                    if (i50 >= rssiPollCountArr4.length) {
                        break;
                    }
                    RssiPollCount rssiPollCount2 = rssiPollCountArr4[i50];
                    if (rssiPollCount2 != null) {
                        codedOutputByteBufferNano.writeMessage(51, rssiPollCount2);
                    }
                    i50++;
                }
            }
            StaEvent[] staEventArr = this.staEventList;
            if (staEventArr != null && staEventArr.length > 0) {
                int i51 = 0;
                while (true) {
                    StaEvent[] staEventArr2 = this.staEventList;
                    if (i51 >= staEventArr2.length) {
                        break;
                    }
                    StaEvent staEvent = staEventArr2[i51];
                    if (staEvent != null) {
                        codedOutputByteBufferNano.writeMessage(52, staEvent);
                    }
                    i51++;
                }
            }
            int i52 = this.numHalCrashes;
            if (i52 != 0) {
                codedOutputByteBufferNano.writeInt32(53, i52);
            }
            int i53 = this.numWificondCrashes;
            if (i53 != 0) {
                codedOutputByteBufferNano.writeInt32(54, i53);
            }
            int i54 = this.numSetupClientInterfaceFailureDueToHal;
            if (i54 != 0) {
                codedOutputByteBufferNano.writeInt32(55, i54);
            }
            int i55 = this.numSetupClientInterfaceFailureDueToWificond;
            if (i55 != 0) {
                codedOutputByteBufferNano.writeInt32(56, i55);
            }
            WifiAwareLog wifiAwareLog = this.wifiAwareLog;
            if (wifiAwareLog != null) {
                codedOutputByteBufferNano.writeMessage(57, wifiAwareLog);
            }
            int i56 = this.numPasspointProviders;
            if (i56 != 0) {
                codedOutputByteBufferNano.writeInt32(58, i56);
            }
            int i57 = this.numPasspointProviderInstallation;
            if (i57 != 0) {
                codedOutputByteBufferNano.writeInt32(59, i57);
            }
            int i58 = this.numPasspointProviderInstallSuccess;
            if (i58 != 0) {
                codedOutputByteBufferNano.writeInt32(60, i58);
            }
            int i59 = this.numPasspointProviderUninstallation;
            if (i59 != 0) {
                codedOutputByteBufferNano.writeInt32(61, i59);
            }
            int i60 = this.numPasspointProviderUninstallSuccess;
            if (i60 != 0) {
                codedOutputByteBufferNano.writeInt32(62, i60);
            }
            int i61 = this.numPasspointProvidersSuccessfullyConnected;
            if (i61 != 0) {
                codedOutputByteBufferNano.writeInt32(63, i61);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr = this.totalSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr != null && numConnectableNetworksBucketArr.length > 0) {
                int i62 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr2 = this.totalSsidsInScanHistogram;
                    if (i62 >= numConnectableNetworksBucketArr2.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket = numConnectableNetworksBucketArr2[i62];
                    if (numConnectableNetworksBucket != null) {
                        codedOutputByteBufferNano.writeMessage(64, numConnectableNetworksBucket);
                    }
                    i62++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr3 = this.totalBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr3 != null && numConnectableNetworksBucketArr3.length > 0) {
                int i63 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr4 = this.totalBssidsInScanHistogram;
                    if (i63 >= numConnectableNetworksBucketArr4.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket2 = numConnectableNetworksBucketArr4[i63];
                    if (numConnectableNetworksBucket2 != null) {
                        codedOutputByteBufferNano.writeMessage(65, numConnectableNetworksBucket2);
                    }
                    i63++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr5 = this.availableOpenSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr5 != null && numConnectableNetworksBucketArr5.length > 0) {
                int i64 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr6 = this.availableOpenSsidsInScanHistogram;
                    if (i64 >= numConnectableNetworksBucketArr6.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket3 = numConnectableNetworksBucketArr6[i64];
                    if (numConnectableNetworksBucket3 != null) {
                        codedOutputByteBufferNano.writeMessage(66, numConnectableNetworksBucket3);
                    }
                    i64++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr7 = this.availableOpenBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr7 != null && numConnectableNetworksBucketArr7.length > 0) {
                int i65 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr8 = this.availableOpenBssidsInScanHistogram;
                    if (i65 >= numConnectableNetworksBucketArr8.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket4 = numConnectableNetworksBucketArr8[i65];
                    if (numConnectableNetworksBucket4 != null) {
                        codedOutputByteBufferNano.writeMessage(67, numConnectableNetworksBucket4);
                    }
                    i65++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr9 = this.availableSavedSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr9 != null && numConnectableNetworksBucketArr9.length > 0) {
                int i66 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr10 = this.availableSavedSsidsInScanHistogram;
                    if (i66 >= numConnectableNetworksBucketArr10.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket5 = numConnectableNetworksBucketArr10[i66];
                    if (numConnectableNetworksBucket5 != null) {
                        codedOutputByteBufferNano.writeMessage(68, numConnectableNetworksBucket5);
                    }
                    i66++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr11 = this.availableSavedBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr11 != null && numConnectableNetworksBucketArr11.length > 0) {
                int i67 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr12 = this.availableSavedBssidsInScanHistogram;
                    if (i67 >= numConnectableNetworksBucketArr12.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket6 = numConnectableNetworksBucketArr12[i67];
                    if (numConnectableNetworksBucket6 != null) {
                        codedOutputByteBufferNano.writeMessage(69, numConnectableNetworksBucket6);
                    }
                    i67++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr13 = this.availableOpenOrSavedSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr13 != null && numConnectableNetworksBucketArr13.length > 0) {
                int i68 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr14 = this.availableOpenOrSavedSsidsInScanHistogram;
                    if (i68 >= numConnectableNetworksBucketArr14.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket7 = numConnectableNetworksBucketArr14[i68];
                    if (numConnectableNetworksBucket7 != null) {
                        codedOutputByteBufferNano.writeMessage(70, numConnectableNetworksBucket7);
                    }
                    i68++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr15 = this.availableOpenOrSavedBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr15 != null && numConnectableNetworksBucketArr15.length > 0) {
                int i69 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr16 = this.availableOpenOrSavedBssidsInScanHistogram;
                    if (i69 >= numConnectableNetworksBucketArr16.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket8 = numConnectableNetworksBucketArr16[i69];
                    if (numConnectableNetworksBucket8 != null) {
                        codedOutputByteBufferNano.writeMessage(71, numConnectableNetworksBucket8);
                    }
                    i69++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr17 = this.availableSavedPasspointProviderProfilesInScanHistogram;
            if (numConnectableNetworksBucketArr17 != null && numConnectableNetworksBucketArr17.length > 0) {
                int i70 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr18 = this.availableSavedPasspointProviderProfilesInScanHistogram;
                    if (i70 >= numConnectableNetworksBucketArr18.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket9 = numConnectableNetworksBucketArr18[i70];
                    if (numConnectableNetworksBucket9 != null) {
                        codedOutputByteBufferNano.writeMessage(72, numConnectableNetworksBucket9);
                    }
                    i70++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr19 = this.availableSavedPasspointProviderBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr19 != null && numConnectableNetworksBucketArr19.length > 0) {
                int i71 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr20 = this.availableSavedPasspointProviderBssidsInScanHistogram;
                    if (i71 >= numConnectableNetworksBucketArr20.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket10 = numConnectableNetworksBucketArr20[i71];
                    if (numConnectableNetworksBucket10 != null) {
                        codedOutputByteBufferNano.writeMessage(73, numConnectableNetworksBucket10);
                    }
                    i71++;
                }
            }
            int i72 = this.fullBandAllSingleScanListenerResults;
            if (i72 != 0) {
                codedOutputByteBufferNano.writeInt32(74, i72);
            }
            int i73 = this.partialAllSingleScanListenerResults;
            if (i73 != 0) {
                codedOutputByteBufferNano.writeInt32(75, i73);
            }
            PnoScanMetrics pnoScanMetrics = this.pnoScanMetrics;
            if (pnoScanMetrics != null) {
                codedOutputByteBufferNano.writeMessage(76, pnoScanMetrics);
            }
            ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr = this.connectToNetworkNotificationCount;
            if (connectToNetworkNotificationAndActionCountArr != null && connectToNetworkNotificationAndActionCountArr.length > 0) {
                int i74 = 0;
                while (true) {
                    ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr2 = this.connectToNetworkNotificationCount;
                    if (i74 >= connectToNetworkNotificationAndActionCountArr2.length) {
                        break;
                    }
                    ConnectToNetworkNotificationAndActionCount connectToNetworkNotificationAndActionCount = connectToNetworkNotificationAndActionCountArr2[i74];
                    if (connectToNetworkNotificationAndActionCount != null) {
                        codedOutputByteBufferNano.writeMessage(77, connectToNetworkNotificationAndActionCount);
                    }
                    i74++;
                }
            }
            ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr3 = this.connectToNetworkNotificationActionCount;
            if (connectToNetworkNotificationAndActionCountArr3 != null && connectToNetworkNotificationAndActionCountArr3.length > 0) {
                int i75 = 0;
                while (true) {
                    ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr4 = this.connectToNetworkNotificationActionCount;
                    if (i75 >= connectToNetworkNotificationAndActionCountArr4.length) {
                        break;
                    }
                    ConnectToNetworkNotificationAndActionCount connectToNetworkNotificationAndActionCount2 = connectToNetworkNotificationAndActionCountArr4[i75];
                    if (connectToNetworkNotificationAndActionCount2 != null) {
                        codedOutputByteBufferNano.writeMessage(78, connectToNetworkNotificationAndActionCount2);
                    }
                    i75++;
                }
            }
            int i76 = this.openNetworkRecommenderBlacklistSize;
            if (i76 != 0) {
                codedOutputByteBufferNano.writeInt32(79, i76);
            }
            boolean z3 = this.isWifiNetworksAvailableNotificationOn;
            if (z3) {
                codedOutputByteBufferNano.writeBool(80, z3);
            }
            int i77 = this.numOpenNetworkRecommendationUpdates;
            if (i77 != 0) {
                codedOutputByteBufferNano.writeInt32(81, i77);
            }
            int i78 = this.numOpenNetworkConnectMessageFailedToSend;
            if (i78 != 0) {
                codedOutputByteBufferNano.writeInt32(82, i78);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr21 = this.observedHotspotR1ApsInScanHistogram;
            if (numConnectableNetworksBucketArr21 != null && numConnectableNetworksBucketArr21.length > 0) {
                int i79 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr22 = this.observedHotspotR1ApsInScanHistogram;
                    if (i79 >= numConnectableNetworksBucketArr22.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket11 = numConnectableNetworksBucketArr22[i79];
                    if (numConnectableNetworksBucket11 != null) {
                        codedOutputByteBufferNano.writeMessage(83, numConnectableNetworksBucket11);
                    }
                    i79++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr23 = this.observedHotspotR2ApsInScanHistogram;
            if (numConnectableNetworksBucketArr23 != null && numConnectableNetworksBucketArr23.length > 0) {
                int i80 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr24 = this.observedHotspotR2ApsInScanHistogram;
                    if (i80 >= numConnectableNetworksBucketArr24.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket12 = numConnectableNetworksBucketArr24[i80];
                    if (numConnectableNetworksBucket12 != null) {
                        codedOutputByteBufferNano.writeMessage(84, numConnectableNetworksBucket12);
                    }
                    i80++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr25 = this.observedHotspotR1EssInScanHistogram;
            if (numConnectableNetworksBucketArr25 != null && numConnectableNetworksBucketArr25.length > 0) {
                int i81 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr26 = this.observedHotspotR1EssInScanHistogram;
                    if (i81 >= numConnectableNetworksBucketArr26.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket13 = numConnectableNetworksBucketArr26[i81];
                    if (numConnectableNetworksBucket13 != null) {
                        codedOutputByteBufferNano.writeMessage(85, numConnectableNetworksBucket13);
                    }
                    i81++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr27 = this.observedHotspotR2EssInScanHistogram;
            if (numConnectableNetworksBucketArr27 != null && numConnectableNetworksBucketArr27.length > 0) {
                int i82 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr28 = this.observedHotspotR2EssInScanHistogram;
                    if (i82 >= numConnectableNetworksBucketArr28.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket14 = numConnectableNetworksBucketArr28[i82];
                    if (numConnectableNetworksBucket14 != null) {
                        codedOutputByteBufferNano.writeMessage(86, numConnectableNetworksBucket14);
                    }
                    i82++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr29 = this.observedHotspotR1ApsPerEssInScanHistogram;
            if (numConnectableNetworksBucketArr29 != null && numConnectableNetworksBucketArr29.length > 0) {
                int i83 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr30 = this.observedHotspotR1ApsPerEssInScanHistogram;
                    if (i83 >= numConnectableNetworksBucketArr30.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket15 = numConnectableNetworksBucketArr30[i83];
                    if (numConnectableNetworksBucket15 != null) {
                        codedOutputByteBufferNano.writeMessage(87, numConnectableNetworksBucket15);
                    }
                    i83++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr31 = this.observedHotspotR2ApsPerEssInScanHistogram;
            if (numConnectableNetworksBucketArr31 != null && numConnectableNetworksBucketArr31.length > 0) {
                int i84 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr32 = this.observedHotspotR2ApsPerEssInScanHistogram;
                    if (i84 >= numConnectableNetworksBucketArr32.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket16 = numConnectableNetworksBucketArr32[i84];
                    if (numConnectableNetworksBucket16 != null) {
                        codedOutputByteBufferNano.writeMessage(88, numConnectableNetworksBucket16);
                    }
                    i84++;
                }
            }
            SoftApConnectedClientsEvent[] softApConnectedClientsEventArr = this.softApConnectedClientsEventsTethered;
            if (softApConnectedClientsEventArr != null && softApConnectedClientsEventArr.length > 0) {
                int i85 = 0;
                while (true) {
                    SoftApConnectedClientsEvent[] softApConnectedClientsEventArr2 = this.softApConnectedClientsEventsTethered;
                    if (i85 >= softApConnectedClientsEventArr2.length) {
                        break;
                    }
                    SoftApConnectedClientsEvent softApConnectedClientsEvent = softApConnectedClientsEventArr2[i85];
                    if (softApConnectedClientsEvent != null) {
                        codedOutputByteBufferNano.writeMessage(89, softApConnectedClientsEvent);
                    }
                    i85++;
                }
            }
            SoftApConnectedClientsEvent[] softApConnectedClientsEventArr3 = this.softApConnectedClientsEventsLocalOnly;
            if (softApConnectedClientsEventArr3 != null && softApConnectedClientsEventArr3.length > 0) {
                int i86 = 0;
                while (true) {
                    SoftApConnectedClientsEvent[] softApConnectedClientsEventArr4 = this.softApConnectedClientsEventsLocalOnly;
                    if (i86 >= softApConnectedClientsEventArr4.length) {
                        break;
                    }
                    SoftApConnectedClientsEvent softApConnectedClientsEvent2 = softApConnectedClientsEventArr4[i86];
                    if (softApConnectedClientsEvent2 != null) {
                        codedOutputByteBufferNano.writeMessage(90, softApConnectedClientsEvent2);
                    }
                    i86++;
                }
            }
            WpsMetrics wpsMetrics = this.wpsMetrics;
            if (wpsMetrics != null) {
                codedOutputByteBufferNano.writeMessage(91, wpsMetrics);
            }
            WifiPowerStats wifiPowerStats = this.wifiPowerStats;
            if (wifiPowerStats != null) {
                codedOutputByteBufferNano.writeMessage(92, wifiPowerStats);
            }
            int i87 = this.numConnectivityOneshotScans;
            if (i87 != 0) {
                codedOutputByteBufferNano.writeInt32(93, i87);
            }
            WifiWakeStats wifiWakeStats = this.wifiWakeStats;
            if (wifiWakeStats != null) {
                codedOutputByteBufferNano.writeMessage(94, wifiWakeStats);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr33 = this.observed80211McSupportingApsInScanHistogram;
            if (numConnectableNetworksBucketArr33 != null && numConnectableNetworksBucketArr33.length > 0) {
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr34 = this.observed80211McSupportingApsInScanHistogram;
                    if (i >= numConnectableNetworksBucketArr34.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket17 = numConnectableNetworksBucketArr34[i];
                    if (numConnectableNetworksBucket17 != null) {
                        codedOutputByteBufferNano.writeMessage(95, numConnectableNetworksBucket17);
                    }
                    i++;
                }
            }
            int i88 = this.numSupplicantCrashes;
            if (i88 != 0) {
                codedOutputByteBufferNano.writeInt32(96, i88);
            }
            int i89 = this.numHostapdCrashes;
            if (i89 != 0) {
                codedOutputByteBufferNano.writeInt32(97, i89);
            }
            int i90 = this.numSetupClientInterfaceFailureDueToSupplicant;
            if (i90 != 0) {
                codedOutputByteBufferNano.writeInt32(98, i90);
            }
            int i91 = this.numSetupSoftApInterfaceFailureDueToHal;
            if (i91 != 0) {
                codedOutputByteBufferNano.writeInt32(99, i91);
            }
            int i92 = this.numSetupSoftApInterfaceFailureDueToWificond;
            if (i92 != 0) {
                codedOutputByteBufferNano.writeInt32(100, i92);
            }
            int i93 = this.numSetupSoftApInterfaceFailureDueToHostapd;
            if (i93 != 0) {
                codedOutputByteBufferNano.writeInt32(101, i93);
            }
            int i94 = this.numClientInterfaceDown;
            if (i94 != 0) {
                codedOutputByteBufferNano.writeInt32(102, i94);
            }
            int i95 = this.numSoftApInterfaceDown;
            if (i95 != 0) {
                codedOutputByteBufferNano.writeInt32(103, i95);
            }
            int i96 = this.numExternalAppOneshotScanRequests;
            if (i96 != 0) {
                codedOutputByteBufferNano.writeInt32(104, i96);
            }
            int i97 = this.numExternalForegroundAppOneshotScanRequestsThrottled;
            if (i97 != 0) {
                codedOutputByteBufferNano.writeInt32(105, i97);
            }
            int i98 = this.numExternalBackgroundAppOneshotScanRequestsThrottled;
            if (i98 != 0) {
                codedOutputByteBufferNano.writeInt32(106, i98);
            }
            long j = this.watchdogTriggerToConnectionSuccessDurationMs;
            if (j != -1) {
                codedOutputByteBufferNano.writeInt64(107, j);
            }
            long j2 = this.watchdogTotalConnectionFailureCountAfterTrigger;
            if (j2 != 0) {
                codedOutputByteBufferNano.writeInt64(108, j2);
            }
            int i99 = this.numOneshotHasDfsChannelScans;
            if (i99 != 0) {
                codedOutputByteBufferNano.writeInt32(109, i99);
            }
            WifiRttLog wifiRttLog = this.wifiRttLog;
            if (wifiRttLog != null) {
                codedOutputByteBufferNano.writeMessage(110, wifiRttLog);
            }
            boolean z4 = this.isMacRandomizationOn;
            if (z4) {
                codedOutputByteBufferNano.writeBool(111, z4);
            }
            int i100 = this.numRadioModeChangeToMcc;
            if (i100 != 0) {
                codedOutputByteBufferNano.writeInt32(112, i100);
            }
            int i101 = this.numRadioModeChangeToScc;
            if (i101 != 0) {
                codedOutputByteBufferNano.writeInt32(113, i101);
            }
            int i102 = this.numRadioModeChangeToSbs;
            if (i102 != 0) {
                codedOutputByteBufferNano.writeInt32(114, i102);
            }
            int i103 = this.numRadioModeChangeToDbs;
            if (i103 != 0) {
                codedOutputByteBufferNano.writeInt32(115, i103);
            }
            int i104 = this.numSoftApUserBandPreferenceUnsatisfied;
            if (i104 != 0) {
                codedOutputByteBufferNano.writeInt32(116, i104);
            }
            if (!this.scoreExperimentId.equals("")) {
                codedOutputByteBufferNano.writeString(117, this.scoreExperimentId);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            ConnectionEvent[] connectionEventArr = this.connectionEvent;
            int i = 0;
            if (connectionEventArr != null && connectionEventArr.length > 0) {
                int i2 = 0;
                while (true) {
                    ConnectionEvent[] connectionEventArr2 = this.connectionEvent;
                    if (i2 >= connectionEventArr2.length) {
                        break;
                    }
                    ConnectionEvent connectionEvent = connectionEventArr2[i2];
                    if (connectionEvent != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, connectionEvent);
                    }
                    i2++;
                }
            }
            int i3 = this.numSavedNetworks;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i3);
            }
            int i4 = this.numOpenNetworks;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i4);
            }
            int i5 = this.numPersonalNetworks;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i5);
            }
            int i6 = this.numEnterpriseNetworks;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i6);
            }
            boolean z = this.isLocationEnabled;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z);
            }
            boolean z2 = this.isScanningAlwaysEnabled;
            if (z2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
            }
            int i7 = this.numWifiToggledViaSettings;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i7);
            }
            int i8 = this.numWifiToggledViaAirplane;
            if (i8 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i8);
            }
            int i9 = this.numNetworksAddedByUser;
            if (i9 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i9);
            }
            int i10 = this.numNetworksAddedByApps;
            if (i10 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i10);
            }
            int i11 = this.numEmptyScanResults;
            if (i11 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, i11);
            }
            int i12 = this.numNonEmptyScanResults;
            if (i12 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i12);
            }
            int i13 = this.numOneshotScans;
            if (i13 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i13);
            }
            int i14 = this.numBackgroundScans;
            if (i14 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i14);
            }
            ScanReturnEntry[] scanReturnEntryArr = this.scanReturnEntries;
            if (scanReturnEntryArr != null && scanReturnEntryArr.length > 0) {
                int i15 = 0;
                while (true) {
                    ScanReturnEntry[] scanReturnEntryArr2 = this.scanReturnEntries;
                    if (i15 >= scanReturnEntryArr2.length) {
                        break;
                    }
                    ScanReturnEntry scanReturnEntry = scanReturnEntryArr2[i15];
                    if (scanReturnEntry != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(16, scanReturnEntry);
                    }
                    i15++;
                }
            }
            WifiSystemStateEntry[] wifiSystemStateEntryArr = this.wifiSystemStateEntries;
            if (wifiSystemStateEntryArr != null && wifiSystemStateEntryArr.length > 0) {
                int i16 = 0;
                while (true) {
                    WifiSystemStateEntry[] wifiSystemStateEntryArr2 = this.wifiSystemStateEntries;
                    if (i16 >= wifiSystemStateEntryArr2.length) {
                        break;
                    }
                    WifiSystemStateEntry wifiSystemStateEntry = wifiSystemStateEntryArr2[i16];
                    if (wifiSystemStateEntry != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(17, wifiSystemStateEntry);
                    }
                    i16++;
                }
            }
            ScanReturnEntry[] scanReturnEntryArr3 = this.backgroundScanReturnEntries;
            if (scanReturnEntryArr3 != null && scanReturnEntryArr3.length > 0) {
                int i17 = 0;
                while (true) {
                    ScanReturnEntry[] scanReturnEntryArr4 = this.backgroundScanReturnEntries;
                    if (i17 >= scanReturnEntryArr4.length) {
                        break;
                    }
                    ScanReturnEntry scanReturnEntry2 = scanReturnEntryArr4[i17];
                    if (scanReturnEntry2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(18, scanReturnEntry2);
                    }
                    i17++;
                }
            }
            WifiSystemStateEntry[] wifiSystemStateEntryArr3 = this.backgroundScanRequestState;
            if (wifiSystemStateEntryArr3 != null && wifiSystemStateEntryArr3.length > 0) {
                int i18 = 0;
                while (true) {
                    WifiSystemStateEntry[] wifiSystemStateEntryArr4 = this.backgroundScanRequestState;
                    if (i18 >= wifiSystemStateEntryArr4.length) {
                        break;
                    }
                    WifiSystemStateEntry wifiSystemStateEntry2 = wifiSystemStateEntryArr4[i18];
                    if (wifiSystemStateEntry2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(19, wifiSystemStateEntry2);
                    }
                    i18++;
                }
            }
            int i19 = this.numLastResortWatchdogTriggers;
            if (i19 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i19);
            }
            int i20 = this.numLastResortWatchdogBadAssociationNetworksTotal;
            if (i20 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(21, i20);
            }
            int i21 = this.numLastResortWatchdogBadAuthenticationNetworksTotal;
            if (i21 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(22, i21);
            }
            int i22 = this.numLastResortWatchdogBadDhcpNetworksTotal;
            if (i22 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(23, i22);
            }
            int i23 = this.numLastResortWatchdogBadOtherNetworksTotal;
            if (i23 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(24, i23);
            }
            int i24 = this.numLastResortWatchdogAvailableNetworksTotal;
            if (i24 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(25, i24);
            }
            int i25 = this.numLastResortWatchdogTriggersWithBadAssociation;
            if (i25 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(26, i25);
            }
            int i26 = this.numLastResortWatchdogTriggersWithBadAuthentication;
            if (i26 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(27, i26);
            }
            int i27 = this.numLastResortWatchdogTriggersWithBadDhcp;
            if (i27 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(28, i27);
            }
            int i28 = this.numLastResortWatchdogTriggersWithBadOther;
            if (i28 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(29, i28);
            }
            int i29 = this.numConnectivityWatchdogPnoGood;
            if (i29 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(30, i29);
            }
            int i30 = this.numConnectivityWatchdogPnoBad;
            if (i30 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(31, i30);
            }
            int i31 = this.numConnectivityWatchdogBackgroundGood;
            if (i31 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(32, i31);
            }
            int i32 = this.numConnectivityWatchdogBackgroundBad;
            if (i32 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(33, i32);
            }
            int i33 = this.recordDurationSec;
            if (i33 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(34, i33);
            }
            RssiPollCount[] rssiPollCountArr = this.rssiPollRssiCount;
            if (rssiPollCountArr != null && rssiPollCountArr.length > 0) {
                int i34 = 0;
                while (true) {
                    RssiPollCount[] rssiPollCountArr2 = this.rssiPollRssiCount;
                    if (i34 >= rssiPollCountArr2.length) {
                        break;
                    }
                    RssiPollCount rssiPollCount = rssiPollCountArr2[i34];
                    if (rssiPollCount != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(35, rssiPollCount);
                    }
                    i34++;
                }
            }
            int i35 = this.numLastResortWatchdogSuccesses;
            if (i35 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(36, i35);
            }
            int i36 = this.numHiddenNetworks;
            if (i36 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(37, i36);
            }
            int i37 = this.numPasspointNetworks;
            if (i37 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(38, i37);
            }
            int i38 = this.numTotalScanResults;
            if (i38 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(39, i38);
            }
            int i39 = this.numOpenNetworkScanResults;
            if (i39 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(40, i39);
            }
            int i40 = this.numPersonalNetworkScanResults;
            if (i40 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(41, i40);
            }
            int i41 = this.numEnterpriseNetworkScanResults;
            if (i41 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(42, i41);
            }
            int i42 = this.numHiddenNetworkScanResults;
            if (i42 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(43, i42);
            }
            int i43 = this.numHotspot2R1NetworkScanResults;
            if (i43 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(44, i43);
            }
            int i44 = this.numHotspot2R2NetworkScanResults;
            if (i44 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(45, i44);
            }
            int i45 = this.numScans;
            if (i45 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(46, i45);
            }
            AlertReasonCount[] alertReasonCountArr = this.alertReasonCount;
            if (alertReasonCountArr != null && alertReasonCountArr.length > 0) {
                int i46 = 0;
                while (true) {
                    AlertReasonCount[] alertReasonCountArr2 = this.alertReasonCount;
                    if (i46 >= alertReasonCountArr2.length) {
                        break;
                    }
                    AlertReasonCount alertReasonCount = alertReasonCountArr2[i46];
                    if (alertReasonCount != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(47, alertReasonCount);
                    }
                    i46++;
                }
            }
            WifiScoreCount[] wifiScoreCountArr = this.wifiScoreCount;
            if (wifiScoreCountArr != null && wifiScoreCountArr.length > 0) {
                int i47 = 0;
                while (true) {
                    WifiScoreCount[] wifiScoreCountArr2 = this.wifiScoreCount;
                    if (i47 >= wifiScoreCountArr2.length) {
                        break;
                    }
                    WifiScoreCount wifiScoreCount = wifiScoreCountArr2[i47];
                    if (wifiScoreCount != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(48, wifiScoreCount);
                    }
                    i47++;
                }
            }
            SoftApDurationBucket[] softApDurationBucketArr = this.softApDuration;
            if (softApDurationBucketArr != null && softApDurationBucketArr.length > 0) {
                int i48 = 0;
                while (true) {
                    SoftApDurationBucket[] softApDurationBucketArr2 = this.softApDuration;
                    if (i48 >= softApDurationBucketArr2.length) {
                        break;
                    }
                    SoftApDurationBucket softApDurationBucket = softApDurationBucketArr2[i48];
                    if (softApDurationBucket != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(49, softApDurationBucket);
                    }
                    i48++;
                }
            }
            SoftApReturnCodeCount[] softApReturnCodeCountArr = this.softApReturnCode;
            if (softApReturnCodeCountArr != null && softApReturnCodeCountArr.length > 0) {
                int i49 = 0;
                while (true) {
                    SoftApReturnCodeCount[] softApReturnCodeCountArr2 = this.softApReturnCode;
                    if (i49 >= softApReturnCodeCountArr2.length) {
                        break;
                    }
                    SoftApReturnCodeCount softApReturnCodeCount = softApReturnCodeCountArr2[i49];
                    if (softApReturnCodeCount != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(50, softApReturnCodeCount);
                    }
                    i49++;
                }
            }
            RssiPollCount[] rssiPollCountArr3 = this.rssiPollDeltaCount;
            if (rssiPollCountArr3 != null && rssiPollCountArr3.length > 0) {
                int i50 = 0;
                while (true) {
                    RssiPollCount[] rssiPollCountArr4 = this.rssiPollDeltaCount;
                    if (i50 >= rssiPollCountArr4.length) {
                        break;
                    }
                    RssiPollCount rssiPollCount2 = rssiPollCountArr4[i50];
                    if (rssiPollCount2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(51, rssiPollCount2);
                    }
                    i50++;
                }
            }
            StaEvent[] staEventArr = this.staEventList;
            if (staEventArr != null && staEventArr.length > 0) {
                int i51 = 0;
                while (true) {
                    StaEvent[] staEventArr2 = this.staEventList;
                    if (i51 >= staEventArr2.length) {
                        break;
                    }
                    StaEvent staEvent = staEventArr2[i51];
                    if (staEvent != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(52, staEvent);
                    }
                    i51++;
                }
            }
            int i52 = this.numHalCrashes;
            if (i52 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(53, i52);
            }
            int i53 = this.numWificondCrashes;
            if (i53 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(54, i53);
            }
            int i54 = this.numSetupClientInterfaceFailureDueToHal;
            if (i54 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(55, i54);
            }
            int i55 = this.numSetupClientInterfaceFailureDueToWificond;
            if (i55 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(56, i55);
            }
            WifiAwareLog wifiAwareLog = this.wifiAwareLog;
            if (wifiAwareLog != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(57, wifiAwareLog);
            }
            int i56 = this.numPasspointProviders;
            if (i56 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(58, i56);
            }
            int i57 = this.numPasspointProviderInstallation;
            if (i57 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(59, i57);
            }
            int i58 = this.numPasspointProviderInstallSuccess;
            if (i58 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(60, i58);
            }
            int i59 = this.numPasspointProviderUninstallation;
            if (i59 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(61, i59);
            }
            int i60 = this.numPasspointProviderUninstallSuccess;
            if (i60 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(62, i60);
            }
            int i61 = this.numPasspointProvidersSuccessfullyConnected;
            if (i61 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(63, i61);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr = this.totalSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr != null && numConnectableNetworksBucketArr.length > 0) {
                int i62 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr2 = this.totalSsidsInScanHistogram;
                    if (i62 >= numConnectableNetworksBucketArr2.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket = numConnectableNetworksBucketArr2[i62];
                    if (numConnectableNetworksBucket != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(64, numConnectableNetworksBucket);
                    }
                    i62++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr3 = this.totalBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr3 != null && numConnectableNetworksBucketArr3.length > 0) {
                int i63 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr4 = this.totalBssidsInScanHistogram;
                    if (i63 >= numConnectableNetworksBucketArr4.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket2 = numConnectableNetworksBucketArr4[i63];
                    if (numConnectableNetworksBucket2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(65, numConnectableNetworksBucket2);
                    }
                    i63++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr5 = this.availableOpenSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr5 != null && numConnectableNetworksBucketArr5.length > 0) {
                int i64 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr6 = this.availableOpenSsidsInScanHistogram;
                    if (i64 >= numConnectableNetworksBucketArr6.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket3 = numConnectableNetworksBucketArr6[i64];
                    if (numConnectableNetworksBucket3 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(66, numConnectableNetworksBucket3);
                    }
                    i64++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr7 = this.availableOpenBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr7 != null && numConnectableNetworksBucketArr7.length > 0) {
                int i65 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr8 = this.availableOpenBssidsInScanHistogram;
                    if (i65 >= numConnectableNetworksBucketArr8.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket4 = numConnectableNetworksBucketArr8[i65];
                    if (numConnectableNetworksBucket4 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(67, numConnectableNetworksBucket4);
                    }
                    i65++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr9 = this.availableSavedSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr9 != null && numConnectableNetworksBucketArr9.length > 0) {
                int i66 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr10 = this.availableSavedSsidsInScanHistogram;
                    if (i66 >= numConnectableNetworksBucketArr10.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket5 = numConnectableNetworksBucketArr10[i66];
                    if (numConnectableNetworksBucket5 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(68, numConnectableNetworksBucket5);
                    }
                    i66++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr11 = this.availableSavedBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr11 != null && numConnectableNetworksBucketArr11.length > 0) {
                int i67 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr12 = this.availableSavedBssidsInScanHistogram;
                    if (i67 >= numConnectableNetworksBucketArr12.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket6 = numConnectableNetworksBucketArr12[i67];
                    if (numConnectableNetworksBucket6 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(69, numConnectableNetworksBucket6);
                    }
                    i67++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr13 = this.availableOpenOrSavedSsidsInScanHistogram;
            if (numConnectableNetworksBucketArr13 != null && numConnectableNetworksBucketArr13.length > 0) {
                int i68 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr14 = this.availableOpenOrSavedSsidsInScanHistogram;
                    if (i68 >= numConnectableNetworksBucketArr14.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket7 = numConnectableNetworksBucketArr14[i68];
                    if (numConnectableNetworksBucket7 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(70, numConnectableNetworksBucket7);
                    }
                    i68++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr15 = this.availableOpenOrSavedBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr15 != null && numConnectableNetworksBucketArr15.length > 0) {
                int i69 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr16 = this.availableOpenOrSavedBssidsInScanHistogram;
                    if (i69 >= numConnectableNetworksBucketArr16.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket8 = numConnectableNetworksBucketArr16[i69];
                    if (numConnectableNetworksBucket8 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(71, numConnectableNetworksBucket8);
                    }
                    i69++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr17 = this.availableSavedPasspointProviderProfilesInScanHistogram;
            if (numConnectableNetworksBucketArr17 != null && numConnectableNetworksBucketArr17.length > 0) {
                int i70 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr18 = this.availableSavedPasspointProviderProfilesInScanHistogram;
                    if (i70 >= numConnectableNetworksBucketArr18.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket9 = numConnectableNetworksBucketArr18[i70];
                    if (numConnectableNetworksBucket9 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(72, numConnectableNetworksBucket9);
                    }
                    i70++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr19 = this.availableSavedPasspointProviderBssidsInScanHistogram;
            if (numConnectableNetworksBucketArr19 != null && numConnectableNetworksBucketArr19.length > 0) {
                int i71 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr20 = this.availableSavedPasspointProviderBssidsInScanHistogram;
                    if (i71 >= numConnectableNetworksBucketArr20.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket10 = numConnectableNetworksBucketArr20[i71];
                    if (numConnectableNetworksBucket10 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(73, numConnectableNetworksBucket10);
                    }
                    i71++;
                }
            }
            int i72 = this.fullBandAllSingleScanListenerResults;
            if (i72 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(74, i72);
            }
            int i73 = this.partialAllSingleScanListenerResults;
            if (i73 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(75, i73);
            }
            PnoScanMetrics pnoScanMetrics = this.pnoScanMetrics;
            if (pnoScanMetrics != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(76, pnoScanMetrics);
            }
            ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr = this.connectToNetworkNotificationCount;
            if (connectToNetworkNotificationAndActionCountArr != null && connectToNetworkNotificationAndActionCountArr.length > 0) {
                int i74 = 0;
                while (true) {
                    ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr2 = this.connectToNetworkNotificationCount;
                    if (i74 >= connectToNetworkNotificationAndActionCountArr2.length) {
                        break;
                    }
                    ConnectToNetworkNotificationAndActionCount connectToNetworkNotificationAndActionCount = connectToNetworkNotificationAndActionCountArr2[i74];
                    if (connectToNetworkNotificationAndActionCount != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(77, connectToNetworkNotificationAndActionCount);
                    }
                    i74++;
                }
            }
            ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr3 = this.connectToNetworkNotificationActionCount;
            if (connectToNetworkNotificationAndActionCountArr3 != null && connectToNetworkNotificationAndActionCountArr3.length > 0) {
                int i75 = 0;
                while (true) {
                    ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr4 = this.connectToNetworkNotificationActionCount;
                    if (i75 >= connectToNetworkNotificationAndActionCountArr4.length) {
                        break;
                    }
                    ConnectToNetworkNotificationAndActionCount connectToNetworkNotificationAndActionCount2 = connectToNetworkNotificationAndActionCountArr4[i75];
                    if (connectToNetworkNotificationAndActionCount2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(78, connectToNetworkNotificationAndActionCount2);
                    }
                    i75++;
                }
            }
            int i76 = this.openNetworkRecommenderBlacklistSize;
            if (i76 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(79, i76);
            }
            boolean z3 = this.isWifiNetworksAvailableNotificationOn;
            if (z3) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(80, z3);
            }
            int i77 = this.numOpenNetworkRecommendationUpdates;
            if (i77 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(81, i77);
            }
            int i78 = this.numOpenNetworkConnectMessageFailedToSend;
            if (i78 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(82, i78);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr21 = this.observedHotspotR1ApsInScanHistogram;
            if (numConnectableNetworksBucketArr21 != null && numConnectableNetworksBucketArr21.length > 0) {
                int i79 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr22 = this.observedHotspotR1ApsInScanHistogram;
                    if (i79 >= numConnectableNetworksBucketArr22.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket11 = numConnectableNetworksBucketArr22[i79];
                    if (numConnectableNetworksBucket11 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(83, numConnectableNetworksBucket11);
                    }
                    i79++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr23 = this.observedHotspotR2ApsInScanHistogram;
            if (numConnectableNetworksBucketArr23 != null && numConnectableNetworksBucketArr23.length > 0) {
                int i80 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr24 = this.observedHotspotR2ApsInScanHistogram;
                    if (i80 >= numConnectableNetworksBucketArr24.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket12 = numConnectableNetworksBucketArr24[i80];
                    if (numConnectableNetworksBucket12 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(84, numConnectableNetworksBucket12);
                    }
                    i80++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr25 = this.observedHotspotR1EssInScanHistogram;
            if (numConnectableNetworksBucketArr25 != null && numConnectableNetworksBucketArr25.length > 0) {
                int i81 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr26 = this.observedHotspotR1EssInScanHistogram;
                    if (i81 >= numConnectableNetworksBucketArr26.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket13 = numConnectableNetworksBucketArr26[i81];
                    if (numConnectableNetworksBucket13 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(85, numConnectableNetworksBucket13);
                    }
                    i81++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr27 = this.observedHotspotR2EssInScanHistogram;
            if (numConnectableNetworksBucketArr27 != null && numConnectableNetworksBucketArr27.length > 0) {
                int i82 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr28 = this.observedHotspotR2EssInScanHistogram;
                    if (i82 >= numConnectableNetworksBucketArr28.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket14 = numConnectableNetworksBucketArr28[i82];
                    if (numConnectableNetworksBucket14 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(86, numConnectableNetworksBucket14);
                    }
                    i82++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr29 = this.observedHotspotR1ApsPerEssInScanHistogram;
            if (numConnectableNetworksBucketArr29 != null && numConnectableNetworksBucketArr29.length > 0) {
                int i83 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr30 = this.observedHotspotR1ApsPerEssInScanHistogram;
                    if (i83 >= numConnectableNetworksBucketArr30.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket15 = numConnectableNetworksBucketArr30[i83];
                    if (numConnectableNetworksBucket15 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(87, numConnectableNetworksBucket15);
                    }
                    i83++;
                }
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr31 = this.observedHotspotR2ApsPerEssInScanHistogram;
            if (numConnectableNetworksBucketArr31 != null && numConnectableNetworksBucketArr31.length > 0) {
                int i84 = 0;
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr32 = this.observedHotspotR2ApsPerEssInScanHistogram;
                    if (i84 >= numConnectableNetworksBucketArr32.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket16 = numConnectableNetworksBucketArr32[i84];
                    if (numConnectableNetworksBucket16 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(88, numConnectableNetworksBucket16);
                    }
                    i84++;
                }
            }
            SoftApConnectedClientsEvent[] softApConnectedClientsEventArr = this.softApConnectedClientsEventsTethered;
            if (softApConnectedClientsEventArr != null && softApConnectedClientsEventArr.length > 0) {
                int i85 = 0;
                while (true) {
                    SoftApConnectedClientsEvent[] softApConnectedClientsEventArr2 = this.softApConnectedClientsEventsTethered;
                    if (i85 >= softApConnectedClientsEventArr2.length) {
                        break;
                    }
                    SoftApConnectedClientsEvent softApConnectedClientsEvent = softApConnectedClientsEventArr2[i85];
                    if (softApConnectedClientsEvent != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(89, softApConnectedClientsEvent);
                    }
                    i85++;
                }
            }
            SoftApConnectedClientsEvent[] softApConnectedClientsEventArr3 = this.softApConnectedClientsEventsLocalOnly;
            if (softApConnectedClientsEventArr3 != null && softApConnectedClientsEventArr3.length > 0) {
                int i86 = 0;
                while (true) {
                    SoftApConnectedClientsEvent[] softApConnectedClientsEventArr4 = this.softApConnectedClientsEventsLocalOnly;
                    if (i86 >= softApConnectedClientsEventArr4.length) {
                        break;
                    }
                    SoftApConnectedClientsEvent softApConnectedClientsEvent2 = softApConnectedClientsEventArr4[i86];
                    if (softApConnectedClientsEvent2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(90, softApConnectedClientsEvent2);
                    }
                    i86++;
                }
            }
            WpsMetrics wpsMetrics = this.wpsMetrics;
            if (wpsMetrics != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(91, wpsMetrics);
            }
            WifiPowerStats wifiPowerStats = this.wifiPowerStats;
            if (wifiPowerStats != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(92, wifiPowerStats);
            }
            int i87 = this.numConnectivityOneshotScans;
            if (i87 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(93, i87);
            }
            WifiWakeStats wifiWakeStats = this.wifiWakeStats;
            if (wifiWakeStats != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(94, wifiWakeStats);
            }
            NumConnectableNetworksBucket[] numConnectableNetworksBucketArr33 = this.observed80211McSupportingApsInScanHistogram;
            if (numConnectableNetworksBucketArr33 != null && numConnectableNetworksBucketArr33.length > 0) {
                while (true) {
                    NumConnectableNetworksBucket[] numConnectableNetworksBucketArr34 = this.observed80211McSupportingApsInScanHistogram;
                    if (i >= numConnectableNetworksBucketArr34.length) {
                        break;
                    }
                    NumConnectableNetworksBucket numConnectableNetworksBucket17 = numConnectableNetworksBucketArr34[i];
                    if (numConnectableNetworksBucket17 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(95, numConnectableNetworksBucket17);
                    }
                    i++;
                }
            }
            int i88 = this.numSupplicantCrashes;
            if (i88 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(96, i88);
            }
            int i89 = this.numHostapdCrashes;
            if (i89 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(97, i89);
            }
            int i90 = this.numSetupClientInterfaceFailureDueToSupplicant;
            if (i90 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(98, i90);
            }
            int i91 = this.numSetupSoftApInterfaceFailureDueToHal;
            if (i91 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(99, i91);
            }
            int i92 = this.numSetupSoftApInterfaceFailureDueToWificond;
            if (i92 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(100, i92);
            }
            int i93 = this.numSetupSoftApInterfaceFailureDueToHostapd;
            if (i93 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(101, i93);
            }
            int i94 = this.numClientInterfaceDown;
            if (i94 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(102, i94);
            }
            int i95 = this.numSoftApInterfaceDown;
            if (i95 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(103, i95);
            }
            int i96 = this.numExternalAppOneshotScanRequests;
            if (i96 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(104, i96);
            }
            int i97 = this.numExternalForegroundAppOneshotScanRequestsThrottled;
            if (i97 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(105, i97);
            }
            int i98 = this.numExternalBackgroundAppOneshotScanRequestsThrottled;
            if (i98 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(106, i98);
            }
            long j = this.watchdogTriggerToConnectionSuccessDurationMs;
            if (j != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(107, j);
            }
            long j2 = this.watchdogTotalConnectionFailureCountAfterTrigger;
            if (j2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(108, j2);
            }
            int i99 = this.numOneshotHasDfsChannelScans;
            if (i99 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(109, i99);
            }
            WifiRttLog wifiRttLog = this.wifiRttLog;
            if (wifiRttLog != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(110, wifiRttLog);
            }
            boolean z4 = this.isMacRandomizationOn;
            if (z4) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(111, z4);
            }
            int i100 = this.numRadioModeChangeToMcc;
            if (i100 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(112, i100);
            }
            int i101 = this.numRadioModeChangeToScc;
            if (i101 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(113, i101);
            }
            int i102 = this.numRadioModeChangeToSbs;
            if (i102 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(114, i102);
            }
            int i103 = this.numRadioModeChangeToDbs;
            if (i103 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(115, i103);
            }
            int i104 = this.numSoftApUserBandPreferenceUnsatisfied;
            if (i104 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(116, i104);
            }
            return !this.scoreExperimentId.equals("") ? iComputeSerializedSize + CodedOutputByteBufferNano.computeStringSize(117, this.scoreExperimentId) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiLog mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                        ConnectionEvent[] connectionEventArr = this.connectionEvent;
                        int length = connectionEventArr == null ? 0 : connectionEventArr.length;
                        int i = repeatedFieldArrayLength + length;
                        ConnectionEvent[] connectionEventArr2 = new ConnectionEvent[i];
                        if (length != 0) {
                            System.arraycopy(this.connectionEvent, 0, connectionEventArr2, 0, length);
                        }
                        while (length < i - 1) {
                            connectionEventArr2[length] = new ConnectionEvent();
                            codedInputByteBufferNano.readMessage(connectionEventArr2[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        connectionEventArr2[length] = new ConnectionEvent();
                        codedInputByteBufferNano.readMessage(connectionEventArr2[length]);
                        this.connectionEvent = connectionEventArr2;
                        break;
                    case 16:
                        this.numSavedNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 24:
                        this.numOpenNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 32:
                        this.numPersonalNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 40:
                        this.numEnterpriseNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 48:
                        this.isLocationEnabled = codedInputByteBufferNano.readBool();
                        break;
                    case 56:
                        this.isScanningAlwaysEnabled = codedInputByteBufferNano.readBool();
                        break;
                    case 64:
                        this.numWifiToggledViaSettings = codedInputByteBufferNano.readInt32();
                        break;
                    case 72:
                        this.numWifiToggledViaAirplane = codedInputByteBufferNano.readInt32();
                        break;
                    case 80:
                        this.numNetworksAddedByUser = codedInputByteBufferNano.readInt32();
                        break;
                    case 88:
                        this.numNetworksAddedByApps = codedInputByteBufferNano.readInt32();
                        break;
                    case 96:
                        this.numEmptyScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 104:
                        this.numNonEmptyScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 112:
                        this.numOneshotScans = codedInputByteBufferNano.readInt32();
                        break;
                    case 120:
                        this.numBackgroundScans = codedInputByteBufferNano.readInt32();
                        break;
                    case 130:
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 130);
                        ScanReturnEntry[] scanReturnEntryArr = this.scanReturnEntries;
                        int length2 = scanReturnEntryArr == null ? 0 : scanReturnEntryArr.length;
                        int i2 = repeatedFieldArrayLength2 + length2;
                        ScanReturnEntry[] scanReturnEntryArr2 = new ScanReturnEntry[i2];
                        if (length2 != 0) {
                            System.arraycopy(this.scanReturnEntries, 0, scanReturnEntryArr2, 0, length2);
                        }
                        while (length2 < i2 - 1) {
                            scanReturnEntryArr2[length2] = new ScanReturnEntry();
                            codedInputByteBufferNano.readMessage(scanReturnEntryArr2[length2]);
                            codedInputByteBufferNano.readTag();
                            length2++;
                        }
                        scanReturnEntryArr2[length2] = new ScanReturnEntry();
                        codedInputByteBufferNano.readMessage(scanReturnEntryArr2[length2]);
                        this.scanReturnEntries = scanReturnEntryArr2;
                        break;
                    case 138:
                        int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 138);
                        WifiSystemStateEntry[] wifiSystemStateEntryArr = this.wifiSystemStateEntries;
                        int length3 = wifiSystemStateEntryArr == null ? 0 : wifiSystemStateEntryArr.length;
                        int i3 = repeatedFieldArrayLength3 + length3;
                        WifiSystemStateEntry[] wifiSystemStateEntryArr2 = new WifiSystemStateEntry[i3];
                        if (length3 != 0) {
                            System.arraycopy(this.wifiSystemStateEntries, 0, wifiSystemStateEntryArr2, 0, length3);
                        }
                        while (length3 < i3 - 1) {
                            wifiSystemStateEntryArr2[length3] = new WifiSystemStateEntry();
                            codedInputByteBufferNano.readMessage(wifiSystemStateEntryArr2[length3]);
                            codedInputByteBufferNano.readTag();
                            length3++;
                        }
                        wifiSystemStateEntryArr2[length3] = new WifiSystemStateEntry();
                        codedInputByteBufferNano.readMessage(wifiSystemStateEntryArr2[length3]);
                        this.wifiSystemStateEntries = wifiSystemStateEntryArr2;
                        break;
                    case 146:
                        int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 146);
                        ScanReturnEntry[] scanReturnEntryArr3 = this.backgroundScanReturnEntries;
                        int length4 = scanReturnEntryArr3 == null ? 0 : scanReturnEntryArr3.length;
                        int i4 = repeatedFieldArrayLength4 + length4;
                        ScanReturnEntry[] scanReturnEntryArr4 = new ScanReturnEntry[i4];
                        if (length4 != 0) {
                            System.arraycopy(this.backgroundScanReturnEntries, 0, scanReturnEntryArr4, 0, length4);
                        }
                        while (length4 < i4 - 1) {
                            scanReturnEntryArr4[length4] = new ScanReturnEntry();
                            codedInputByteBufferNano.readMessage(scanReturnEntryArr4[length4]);
                            codedInputByteBufferNano.readTag();
                            length4++;
                        }
                        scanReturnEntryArr4[length4] = new ScanReturnEntry();
                        codedInputByteBufferNano.readMessage(scanReturnEntryArr4[length4]);
                        this.backgroundScanReturnEntries = scanReturnEntryArr4;
                        break;
                    case 154:
                        int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 154);
                        WifiSystemStateEntry[] wifiSystemStateEntryArr3 = this.backgroundScanRequestState;
                        int length5 = wifiSystemStateEntryArr3 == null ? 0 : wifiSystemStateEntryArr3.length;
                        int i5 = repeatedFieldArrayLength5 + length5;
                        WifiSystemStateEntry[] wifiSystemStateEntryArr4 = new WifiSystemStateEntry[i5];
                        if (length5 != 0) {
                            System.arraycopy(this.backgroundScanRequestState, 0, wifiSystemStateEntryArr4, 0, length5);
                        }
                        while (length5 < i5 - 1) {
                            wifiSystemStateEntryArr4[length5] = new WifiSystemStateEntry();
                            codedInputByteBufferNano.readMessage(wifiSystemStateEntryArr4[length5]);
                            codedInputByteBufferNano.readTag();
                            length5++;
                        }
                        wifiSystemStateEntryArr4[length5] = new WifiSystemStateEntry();
                        codedInputByteBufferNano.readMessage(wifiSystemStateEntryArr4[length5]);
                        this.backgroundScanRequestState = wifiSystemStateEntryArr4;
                        break;
                    case 160:
                        this.numLastResortWatchdogTriggers = codedInputByteBufferNano.readInt32();
                        break;
                    case 168:
                        this.numLastResortWatchdogBadAssociationNetworksTotal = codedInputByteBufferNano.readInt32();
                        break;
                    case 176:
                        this.numLastResortWatchdogBadAuthenticationNetworksTotal = codedInputByteBufferNano.readInt32();
                        break;
                    case 184:
                        this.numLastResortWatchdogBadDhcpNetworksTotal = codedInputByteBufferNano.readInt32();
                        break;
                    case 192:
                        this.numLastResortWatchdogBadOtherNetworksTotal = codedInputByteBufferNano.readInt32();
                        break;
                    case 200:
                        this.numLastResortWatchdogAvailableNetworksTotal = codedInputByteBufferNano.readInt32();
                        break;
                    case 208:
                        this.numLastResortWatchdogTriggersWithBadAssociation = codedInputByteBufferNano.readInt32();
                        break;
                    case 216:
                        this.numLastResortWatchdogTriggersWithBadAuthentication = codedInputByteBufferNano.readInt32();
                        break;
                    case 224:
                        this.numLastResortWatchdogTriggersWithBadDhcp = codedInputByteBufferNano.readInt32();
                        break;
                    case 232:
                        this.numLastResortWatchdogTriggersWithBadOther = codedInputByteBufferNano.readInt32();
                        break;
                    case 240:
                        this.numConnectivityWatchdogPnoGood = codedInputByteBufferNano.readInt32();
                        break;
                    case 248:
                        this.numConnectivityWatchdogPnoBad = codedInputByteBufferNano.readInt32();
                        break;
                    case 256:
                        this.numConnectivityWatchdogBackgroundGood = codedInputByteBufferNano.readInt32();
                        break;
                    case 264:
                        this.numConnectivityWatchdogBackgroundBad = codedInputByteBufferNano.readInt32();
                        break;
                    case 272:
                        this.recordDurationSec = codedInputByteBufferNano.readInt32();
                        break;
                    case 282:
                        int repeatedFieldArrayLength6 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 282);
                        RssiPollCount[] rssiPollCountArr = this.rssiPollRssiCount;
                        int length6 = rssiPollCountArr == null ? 0 : rssiPollCountArr.length;
                        int i6 = repeatedFieldArrayLength6 + length6;
                        RssiPollCount[] rssiPollCountArr2 = new RssiPollCount[i6];
                        if (length6 != 0) {
                            System.arraycopy(this.rssiPollRssiCount, 0, rssiPollCountArr2, 0, length6);
                        }
                        while (length6 < i6 - 1) {
                            rssiPollCountArr2[length6] = new RssiPollCount();
                            codedInputByteBufferNano.readMessage(rssiPollCountArr2[length6]);
                            codedInputByteBufferNano.readTag();
                            length6++;
                        }
                        rssiPollCountArr2[length6] = new RssiPollCount();
                        codedInputByteBufferNano.readMessage(rssiPollCountArr2[length6]);
                        this.rssiPollRssiCount = rssiPollCountArr2;
                        break;
                    case 288:
                        this.numLastResortWatchdogSuccesses = codedInputByteBufferNano.readInt32();
                        break;
                    case 296:
                        this.numHiddenNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 304:
                        this.numPasspointNetworks = codedInputByteBufferNano.readInt32();
                        break;
                    case 312:
                        this.numTotalScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 320:
                        this.numOpenNetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 328:
                        this.numPersonalNetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 336:
                        this.numEnterpriseNetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.USER_LOCALE_LIST /* 344 */:
                        this.numHiddenNetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 352:
                        this.numHotspot2R1NetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 360:
                        this.numHotspot2R2NetworkScanResults = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.SUW_ACCESSIBILITY_TOGGLE_SCREEN_MAGNIFICATION /* 368 */:
                        this.numScans = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.SETTINGS_CONDITION_BACKGROUND_DATA /* 378 */:
                        int repeatedFieldArrayLength7 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.SETTINGS_CONDITION_BACKGROUND_DATA);
                        AlertReasonCount[] alertReasonCountArr = this.alertReasonCount;
                        int length7 = alertReasonCountArr == null ? 0 : alertReasonCountArr.length;
                        int i7 = repeatedFieldArrayLength7 + length7;
                        AlertReasonCount[] alertReasonCountArr2 = new AlertReasonCount[i7];
                        if (length7 != 0) {
                            System.arraycopy(this.alertReasonCount, 0, alertReasonCountArr2, 0, length7);
                        }
                        while (length7 < i7 - 1) {
                            alertReasonCountArr2[length7] = new AlertReasonCount();
                            codedInputByteBufferNano.readMessage(alertReasonCountArr2[length7]);
                            codedInputByteBufferNano.readTag();
                            length7++;
                        }
                        alertReasonCountArr2[length7] = new AlertReasonCount();
                        codedInputByteBufferNano.readMessage(alertReasonCountArr2[length7]);
                        this.alertReasonCount = alertReasonCountArr2;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_SETTINGS_SUGGESTION /* 386 */:
                        int repeatedFieldArrayLength8 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_SETTINGS_SUGGESTION);
                        WifiScoreCount[] wifiScoreCountArr = this.wifiScoreCount;
                        int length8 = wifiScoreCountArr == null ? 0 : wifiScoreCountArr.length;
                        int i8 = repeatedFieldArrayLength8 + length8;
                        WifiScoreCount[] wifiScoreCountArr2 = new WifiScoreCount[i8];
                        if (length8 != 0) {
                            System.arraycopy(this.wifiScoreCount, 0, wifiScoreCountArr2, 0, length8);
                        }
                        while (length8 < i8 - 1) {
                            wifiScoreCountArr2[length8] = new WifiScoreCount();
                            codedInputByteBufferNano.readMessage(wifiScoreCountArr2[length8]);
                            codedInputByteBufferNano.readTag();
                            length8++;
                        }
                        wifiScoreCountArr2[length8] = new WifiScoreCount();
                        codedInputByteBufferNano.readMessage(wifiScoreCountArr2[length8]);
                        this.wifiScoreCount = wifiScoreCountArr2;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_DATA_SAVER_MODE /* 394 */:
                        int repeatedFieldArrayLength9 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_DATA_SAVER_MODE);
                        SoftApDurationBucket[] softApDurationBucketArr = this.softApDuration;
                        int length9 = softApDurationBucketArr == null ? 0 : softApDurationBucketArr.length;
                        int i9 = repeatedFieldArrayLength9 + length9;
                        SoftApDurationBucket[] softApDurationBucketArr2 = new SoftApDurationBucket[i9];
                        if (length9 != 0) {
                            System.arraycopy(this.softApDuration, 0, softApDurationBucketArr2, 0, length9);
                        }
                        while (length9 < i9 - 1) {
                            softApDurationBucketArr2[length9] = new SoftApDurationBucket();
                            codedInputByteBufferNano.readMessage(softApDurationBucketArr2[length9]);
                            codedInputByteBufferNano.readTag();
                            length9++;
                        }
                        softApDurationBucketArr2[length9] = new SoftApDurationBucket();
                        codedInputByteBufferNano.readMessage(softApDurationBucketArr2[length9]);
                        this.softApDuration = softApDurationBucketArr2;
                        break;
                    case 402:
                        int repeatedFieldArrayLength10 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 402);
                        SoftApReturnCodeCount[] softApReturnCodeCountArr = this.softApReturnCode;
                        int length10 = softApReturnCodeCountArr == null ? 0 : softApReturnCodeCountArr.length;
                        int i10 = repeatedFieldArrayLength10 + length10;
                        SoftApReturnCodeCount[] softApReturnCodeCountArr2 = new SoftApReturnCodeCount[i10];
                        if (length10 != 0) {
                            System.arraycopy(this.softApReturnCode, 0, softApReturnCodeCountArr2, 0, length10);
                        }
                        while (length10 < i10 - 1) {
                            softApReturnCodeCountArr2[length10] = new SoftApReturnCodeCount();
                            codedInputByteBufferNano.readMessage(softApReturnCodeCountArr2[length10]);
                            codedInputByteBufferNano.readTag();
                            length10++;
                        }
                        softApReturnCodeCountArr2[length10] = new SoftApReturnCodeCount();
                        codedInputByteBufferNano.readMessage(softApReturnCodeCountArr2[length10]);
                        this.softApReturnCode = softApReturnCodeCountArr2;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_NOTIFICATION_GROUP_GESTURE_EXPANDER /* 410 */:
                        int repeatedFieldArrayLength11 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_NOTIFICATION_GROUP_GESTURE_EXPANDER);
                        RssiPollCount[] rssiPollCountArr3 = this.rssiPollDeltaCount;
                        int length11 = rssiPollCountArr3 == null ? 0 : rssiPollCountArr3.length;
                        int i11 = repeatedFieldArrayLength11 + length11;
                        RssiPollCount[] rssiPollCountArr4 = new RssiPollCount[i11];
                        if (length11 != 0) {
                            System.arraycopy(this.rssiPollDeltaCount, 0, rssiPollCountArr4, 0, length11);
                        }
                        while (length11 < i11 - 1) {
                            rssiPollCountArr4[length11] = new RssiPollCount();
                            codedInputByteBufferNano.readMessage(rssiPollCountArr4[length11]);
                            codedInputByteBufferNano.readTag();
                            length11++;
                        }
                        rssiPollCountArr4[length11] = new RssiPollCount();
                        codedInputByteBufferNano.readMessage(rssiPollCountArr4[length11]);
                        this.rssiPollDeltaCount = rssiPollCountArr4;
                        break;
                    case 418:
                        int repeatedFieldArrayLength12 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 418);
                        StaEvent[] staEventArr = this.staEventList;
                        int length12 = staEventArr == null ? 0 : staEventArr.length;
                        int i12 = repeatedFieldArrayLength12 + length12;
                        StaEvent[] staEventArr2 = new StaEvent[i12];
                        if (length12 != 0) {
                            System.arraycopy(this.staEventList, 0, staEventArr2, 0, length12);
                        }
                        while (length12 < i12 - 1) {
                            staEventArr2[length12] = new StaEvent();
                            codedInputByteBufferNano.readMessage(staEventArr2[length12]);
                            codedInputByteBufferNano.readTag();
                            length12++;
                        }
                        staEventArr2[length12] = new StaEvent();
                        codedInputByteBufferNano.readMessage(staEventArr2[length12]);
                        this.staEventList = staEventArr2;
                        break;
                    case 424:
                        this.numHalCrashes = codedInputByteBufferNano.readInt32();
                        break;
                    case DevicePolicyManager.PROFILE_KEYGUARD_FEATURES_AFFECT_OWNER /* 432 */:
                        this.numWificondCrashes = codedInputByteBufferNano.readInt32();
                        break;
                    case DisplayMetrics.DENSITY_440 /* 440 */:
                        this.numSetupClientInterfaceFailureDueToHal = codedInputByteBufferNano.readInt32();
                        break;
                    case 448:
                        this.numSetupClientInterfaceFailureDueToWificond = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.STORAGE_MANAGER_SETTINGS /* 458 */:
                        if (this.wifiAwareLog == null) {
                            this.wifiAwareLog = new WifiAwareLog();
                        }
                        codedInputByteBufferNano.readMessage(this.wifiAwareLog);
                        break;
                    case MetricsProto.MetricsEvent.ACTION_DELETION_APPS_COLLAPSED /* 464 */:
                        this.numPasspointProviders = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_DELETION_HELPER_DOWNLOADS_DELETION_FAIL /* 472 */:
                        this.numPasspointProviderInstallation = codedInputByteBufferNano.readInt32();
                        break;
                    case 480:
                        this.numPasspointProviderInstallSuccess = codedInputByteBufferNano.readInt32();
                        break;
                    case 488:
                        this.numPasspointProviderUninstallation = codedInputByteBufferNano.readInt32();
                        break;
                    case 496:
                        this.numPasspointProviderUninstallSuccess = codedInputByteBufferNano.readInt32();
                        break;
                    case 504:
                        this.numPasspointProvidersSuccessfullyConnected = codedInputByteBufferNano.readInt32();
                        break;
                    case 514:
                        int repeatedFieldArrayLength13 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 514);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr = this.totalSsidsInScanHistogram;
                        int length13 = numConnectableNetworksBucketArr == null ? 0 : numConnectableNetworksBucketArr.length;
                        int i13 = repeatedFieldArrayLength13 + length13;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr2 = new NumConnectableNetworksBucket[i13];
                        if (length13 != 0) {
                            System.arraycopy(this.totalSsidsInScanHistogram, 0, numConnectableNetworksBucketArr2, 0, length13);
                        }
                        while (length13 < i13 - 1) {
                            numConnectableNetworksBucketArr2[length13] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr2[length13]);
                            codedInputByteBufferNano.readTag();
                            length13++;
                        }
                        numConnectableNetworksBucketArr2[length13] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr2[length13]);
                        this.totalSsidsInScanHistogram = numConnectableNetworksBucketArr2;
                        break;
                    case 522:
                        int repeatedFieldArrayLength14 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 522);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr3 = this.totalBssidsInScanHistogram;
                        int length14 = numConnectableNetworksBucketArr3 == null ? 0 : numConnectableNetworksBucketArr3.length;
                        int i14 = repeatedFieldArrayLength14 + length14;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr4 = new NumConnectableNetworksBucket[i14];
                        if (length14 != 0) {
                            System.arraycopy(this.totalBssidsInScanHistogram, 0, numConnectableNetworksBucketArr4, 0, length14);
                        }
                        while (length14 < i14 - 1) {
                            numConnectableNetworksBucketArr4[length14] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr4[length14]);
                            codedInputByteBufferNano.readTag();
                            length14++;
                        }
                        numConnectableNetworksBucketArr4[length14] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr4[length14]);
                        this.totalBssidsInScanHistogram = numConnectableNetworksBucketArr4;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_APN_EDITOR_ERROR /* 530 */:
                        int repeatedFieldArrayLength15 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_APN_EDITOR_ERROR);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr5 = this.availableOpenSsidsInScanHistogram;
                        int length15 = numConnectableNetworksBucketArr5 == null ? 0 : numConnectableNetworksBucketArr5.length;
                        int i15 = repeatedFieldArrayLength15 + length15;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr6 = new NumConnectableNetworksBucket[i15];
                        if (length15 != 0) {
                            System.arraycopy(this.availableOpenSsidsInScanHistogram, 0, numConnectableNetworksBucketArr6, 0, length15);
                        }
                        while (length15 < i15 - 1) {
                            numConnectableNetworksBucketArr6[length15] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr6[length15]);
                            codedInputByteBufferNano.readTag();
                            length15++;
                        }
                        numConnectableNetworksBucketArr6[length15] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr6[length15]);
                        this.availableOpenSsidsInScanHistogram = numConnectableNetworksBucketArr6;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_BLUETOOTH_RENAME /* 538 */:
                        int repeatedFieldArrayLength16 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_BLUETOOTH_RENAME);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr7 = this.availableOpenBssidsInScanHistogram;
                        int length16 = numConnectableNetworksBucketArr7 == null ? 0 : numConnectableNetworksBucketArr7.length;
                        int i16 = repeatedFieldArrayLength16 + length16;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr8 = new NumConnectableNetworksBucket[i16];
                        if (length16 != 0) {
                            System.arraycopy(this.availableOpenBssidsInScanHistogram, 0, numConnectableNetworksBucketArr8, 0, length16);
                        }
                        while (length16 < i16 - 1) {
                            numConnectableNetworksBucketArr8[length16] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr8[length16]);
                            codedInputByteBufferNano.readTag();
                            length16++;
                        }
                        numConnectableNetworksBucketArr8[length16] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr8[length16]);
                        this.availableOpenBssidsInScanHistogram = numConnectableNetworksBucketArr8;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_VPN_APP_CONFIG /* 546 */:
                        int repeatedFieldArrayLength17 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_VPN_APP_CONFIG);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr9 = this.availableSavedSsidsInScanHistogram;
                        int length17 = numConnectableNetworksBucketArr9 == null ? 0 : numConnectableNetworksBucketArr9.length;
                        int i17 = repeatedFieldArrayLength17 + length17;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr10 = new NumConnectableNetworksBucket[i17];
                        if (length17 != 0) {
                            System.arraycopy(this.availableSavedSsidsInScanHistogram, 0, numConnectableNetworksBucketArr10, 0, length17);
                        }
                        while (length17 < i17 - 1) {
                            numConnectableNetworksBucketArr10[length17] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr10[length17]);
                            codedInputByteBufferNano.readTag();
                            length17++;
                        }
                        numConnectableNetworksBucketArr10[length17] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr10[length17]);
                        this.availableSavedSsidsInScanHistogram = numConnectableNetworksBucketArr10;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_ZEN_ACCESS_GRANT /* 554 */:
                        int repeatedFieldArrayLength18 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_ZEN_ACCESS_GRANT);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr11 = this.availableSavedBssidsInScanHistogram;
                        int length18 = numConnectableNetworksBucketArr11 == null ? 0 : numConnectableNetworksBucketArr11.length;
                        int i18 = repeatedFieldArrayLength18 + length18;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr12 = new NumConnectableNetworksBucket[i18];
                        if (length18 != 0) {
                            System.arraycopy(this.availableSavedBssidsInScanHistogram, 0, numConnectableNetworksBucketArr12, 0, length18);
                        }
                        while (length18 < i18 - 1) {
                            numConnectableNetworksBucketArr12[length18] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr12[length18]);
                            codedInputByteBufferNano.readTag();
                            length18++;
                        }
                        numConnectableNetworksBucketArr12[length18] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr12[length18]);
                        this.availableSavedBssidsInScanHistogram = numConnectableNetworksBucketArr12;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_VOLUME_UNMOUNT /* 562 */:
                        int repeatedFieldArrayLength19 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_VOLUME_UNMOUNT);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr13 = this.availableOpenOrSavedSsidsInScanHistogram;
                        int length19 = numConnectableNetworksBucketArr13 == null ? 0 : numConnectableNetworksBucketArr13.length;
                        int i19 = repeatedFieldArrayLength19 + length19;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr14 = new NumConnectableNetworksBucket[i19];
                        if (length19 != 0) {
                            System.arraycopy(this.availableOpenOrSavedSsidsInScanHistogram, 0, numConnectableNetworksBucketArr14, 0, length19);
                        }
                        while (length19 < i19 - 1) {
                            numConnectableNetworksBucketArr14[length19] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr14[length19]);
                            codedInputByteBufferNano.readTag();
                            length19++;
                        }
                        numConnectableNetworksBucketArr14[length19] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr14[length19]);
                        this.availableOpenOrSavedSsidsInScanHistogram = numConnectableNetworksBucketArr14;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_FINGERPINT_EDIT /* 570 */:
                        int repeatedFieldArrayLength20 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_FINGERPINT_EDIT);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr15 = this.availableOpenOrSavedBssidsInScanHistogram;
                        int length20 = numConnectableNetworksBucketArr15 == null ? 0 : numConnectableNetworksBucketArr15.length;
                        int i20 = repeatedFieldArrayLength20 + length20;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr16 = new NumConnectableNetworksBucket[i20];
                        if (length20 != 0) {
                            System.arraycopy(this.availableOpenOrSavedBssidsInScanHistogram, 0, numConnectableNetworksBucketArr16, 0, length20);
                        }
                        while (length20 < i20 - 1) {
                            numConnectableNetworksBucketArr16[length20] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr16[length20]);
                            codedInputByteBufferNano.readTag();
                            length20++;
                        }
                        numConnectableNetworksBucketArr16[length20] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr16[length20]);
                        this.availableOpenOrSavedBssidsInScanHistogram = numConnectableNetworksBucketArr16;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_WIFI_P2P_DELETE_GROUP /* 578 */:
                        int repeatedFieldArrayLength21 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_WIFI_P2P_DELETE_GROUP);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr17 = this.availableSavedPasspointProviderProfilesInScanHistogram;
                        int length21 = numConnectableNetworksBucketArr17 == null ? 0 : numConnectableNetworksBucketArr17.length;
                        int i21 = repeatedFieldArrayLength21 + length21;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr18 = new NumConnectableNetworksBucket[i21];
                        if (length21 != 0) {
                            System.arraycopy(this.availableSavedPasspointProviderProfilesInScanHistogram, 0, numConnectableNetworksBucketArr18, 0, length21);
                        }
                        while (length21 < i21 - 1) {
                            numConnectableNetworksBucketArr18[length21] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr18[length21]);
                            codedInputByteBufferNano.readTag();
                            length21++;
                        }
                        numConnectableNetworksBucketArr18[length21] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr18[length21]);
                        this.availableSavedPasspointProviderProfilesInScanHistogram = numConnectableNetworksBucketArr18;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_ACCOUNT_SYNC_FAILED_REMOVAL /* 586 */:
                        int repeatedFieldArrayLength22 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.DIALOG_ACCOUNT_SYNC_FAILED_REMOVAL);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr19 = this.availableSavedPasspointProviderBssidsInScanHistogram;
                        int length22 = numConnectableNetworksBucketArr19 == null ? 0 : numConnectableNetworksBucketArr19.length;
                        int i22 = repeatedFieldArrayLength22 + length22;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr20 = new NumConnectableNetworksBucket[i22];
                        if (length22 != 0) {
                            System.arraycopy(this.availableSavedPasspointProviderBssidsInScanHistogram, 0, numConnectableNetworksBucketArr20, 0, length22);
                        }
                        while (length22 < i22 - 1) {
                            numConnectableNetworksBucketArr20[length22] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr20[length22]);
                            codedInputByteBufferNano.readTag();
                            length22++;
                        }
                        numConnectableNetworksBucketArr20[length22] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr20[length22]);
                        this.availableSavedPasspointProviderBssidsInScanHistogram = numConnectableNetworksBucketArr20;
                        break;
                    case MetricsProto.MetricsEvent.DIALOG_USER_ENABLE_CALLING /* 592 */:
                        this.fullBandAllSingleScanListenerResults = codedInputByteBufferNano.readInt32();
                        break;
                    case 600:
                        this.partialAllSingleScanListenerResults = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.PROVISIONING_NETWORK_TYPE /* 610 */:
                        if (this.pnoScanMetrics == null) {
                            this.pnoScanMetrics = new PnoScanMetrics();
                        }
                        codedInputByteBufferNano.readMessage(this.pnoScanMetrics);
                        break;
                    case MetricsProto.MetricsEvent.PROVISIONING_ENTRY_POINT_TRUSTED_SOURCE /* 618 */:
                        int repeatedFieldArrayLength23 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.PROVISIONING_ENTRY_POINT_TRUSTED_SOURCE);
                        ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr = this.connectToNetworkNotificationCount;
                        int length23 = connectToNetworkNotificationAndActionCountArr == null ? 0 : connectToNetworkNotificationAndActionCountArr.length;
                        int i23 = repeatedFieldArrayLength23 + length23;
                        ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr2 = new ConnectToNetworkNotificationAndActionCount[i23];
                        if (length23 != 0) {
                            System.arraycopy(this.connectToNetworkNotificationCount, 0, connectToNetworkNotificationAndActionCountArr2, 0, length23);
                        }
                        while (length23 < i23 - 1) {
                            connectToNetworkNotificationAndActionCountArr2[length23] = new ConnectToNetworkNotificationAndActionCount();
                            codedInputByteBufferNano.readMessage(connectToNetworkNotificationAndActionCountArr2[length23]);
                            codedInputByteBufferNano.readTag();
                            length23++;
                        }
                        connectToNetworkNotificationAndActionCountArr2[length23] = new ConnectToNetworkNotificationAndActionCount();
                        codedInputByteBufferNano.readMessage(connectToNetworkNotificationAndActionCountArr2[length23]);
                        this.connectToNetworkNotificationCount = connectToNetworkNotificationAndActionCountArr2;
                        break;
                    case MetricsProto.MetricsEvent.PROVISIONING_COPY_ACCOUNT_STATUS /* 626 */:
                        int repeatedFieldArrayLength24 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.PROVISIONING_COPY_ACCOUNT_STATUS);
                        ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr3 = this.connectToNetworkNotificationActionCount;
                        int length24 = connectToNetworkNotificationAndActionCountArr3 == null ? 0 : connectToNetworkNotificationAndActionCountArr3.length;
                        int i24 = repeatedFieldArrayLength24 + length24;
                        ConnectToNetworkNotificationAndActionCount[] connectToNetworkNotificationAndActionCountArr4 = new ConnectToNetworkNotificationAndActionCount[i24];
                        if (length24 != 0) {
                            System.arraycopy(this.connectToNetworkNotificationActionCount, 0, connectToNetworkNotificationAndActionCountArr4, 0, length24);
                        }
                        while (length24 < i24 - 1) {
                            connectToNetworkNotificationAndActionCountArr4[length24] = new ConnectToNetworkNotificationAndActionCount();
                            codedInputByteBufferNano.readMessage(connectToNetworkNotificationAndActionCountArr4[length24]);
                            codedInputByteBufferNano.readTag();
                            length24++;
                        }
                        connectToNetworkNotificationAndActionCountArr4[length24] = new ConnectToNetworkNotificationAndActionCount();
                        codedInputByteBufferNano.readMessage(connectToNetworkNotificationAndActionCountArr4[length24]);
                        this.connectToNetworkNotificationActionCount = connectToNetworkNotificationAndActionCountArr4;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_UNKNOWN /* 632 */:
                        this.openNetworkRecommenderBlacklistSize = codedInputByteBufferNano.readInt32();
                        break;
                    case 640:
                        this.isWifiNetworksAvailableNotificationOn = codedInputByteBufferNano.readBool();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_READ_CONTACTS /* 648 */:
                        this.numOpenNetworkRecommendationUpdates = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_GET_ACCOUNTS /* 656 */:
                        this.numOpenNetworkConnectMessageFailedToSend = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_RECORD_AUDIO /* 666 */:
                        int repeatedFieldArrayLength25 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_RECORD_AUDIO);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr21 = this.observedHotspotR1ApsInScanHistogram;
                        int length25 = numConnectableNetworksBucketArr21 == null ? 0 : numConnectableNetworksBucketArr21.length;
                        int i25 = repeatedFieldArrayLength25 + length25;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr22 = new NumConnectableNetworksBucket[i25];
                        if (length25 != 0) {
                            System.arraycopy(this.observedHotspotR1ApsInScanHistogram, 0, numConnectableNetworksBucketArr22, 0, length25);
                        }
                        while (length25 < i25 - 1) {
                            numConnectableNetworksBucketArr22[length25] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr22[length25]);
                            codedInputByteBufferNano.readTag();
                            length25++;
                        }
                        numConnectableNetworksBucketArr22[length25] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr22[length25]);
                        this.observedHotspotR1ApsInScanHistogram = numConnectableNetworksBucketArr22;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_CALL_PHONE /* 674 */:
                        int repeatedFieldArrayLength26 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_CALL_PHONE);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr23 = this.observedHotspotR2ApsInScanHistogram;
                        int length26 = numConnectableNetworksBucketArr23 == null ? 0 : numConnectableNetworksBucketArr23.length;
                        int i26 = repeatedFieldArrayLength26 + length26;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr24 = new NumConnectableNetworksBucket[i26];
                        if (length26 != 0) {
                            System.arraycopy(this.observedHotspotR2ApsInScanHistogram, 0, numConnectableNetworksBucketArr24, 0, length26);
                        }
                        while (length26 < i26 - 1) {
                            numConnectableNetworksBucketArr24[length26] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr24[length26]);
                            codedInputByteBufferNano.readTag();
                            length26++;
                        }
                        numConnectableNetworksBucketArr24[length26] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr24[length26]);
                        this.observedHotspotR2ApsInScanHistogram = numConnectableNetworksBucketArr24;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_WRITE_CALL_LOG /* 682 */:
                        int repeatedFieldArrayLength27 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_WRITE_CALL_LOG);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr25 = this.observedHotspotR1EssInScanHistogram;
                        int length27 = numConnectableNetworksBucketArr25 == null ? 0 : numConnectableNetworksBucketArr25.length;
                        int i27 = repeatedFieldArrayLength27 + length27;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr26 = new NumConnectableNetworksBucket[i27];
                        if (length27 != 0) {
                            System.arraycopy(this.observedHotspotR1EssInScanHistogram, 0, numConnectableNetworksBucketArr26, 0, length27);
                        }
                        while (length27 < i27 - 1) {
                            numConnectableNetworksBucketArr26[length27] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr26[length27]);
                            codedInputByteBufferNano.readTag();
                            length27++;
                        }
                        numConnectableNetworksBucketArr26[length27] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr26[length27]);
                        this.observedHotspotR1EssInScanHistogram = numConnectableNetworksBucketArr26;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_USE_SIP /* 690 */:
                        int repeatedFieldArrayLength28 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_USE_SIP);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr27 = this.observedHotspotR2EssInScanHistogram;
                        int length28 = numConnectableNetworksBucketArr27 == null ? 0 : numConnectableNetworksBucketArr27.length;
                        int i28 = repeatedFieldArrayLength28 + length28;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr28 = new NumConnectableNetworksBucket[i28];
                        if (length28 != 0) {
                            System.arraycopy(this.observedHotspotR2EssInScanHistogram, 0, numConnectableNetworksBucketArr28, 0, length28);
                        }
                        while (length28 < i28 - 1) {
                            numConnectableNetworksBucketArr28[length28] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr28[length28]);
                            codedInputByteBufferNano.readTag();
                            length28++;
                        }
                        numConnectableNetworksBucketArr28[length28] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr28[length28]);
                        this.observedHotspotR2EssInScanHistogram = numConnectableNetworksBucketArr28;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_READ_CELL_BROADCASTS /* 698 */:
                        int repeatedFieldArrayLength29 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_READ_CELL_BROADCASTS);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr29 = this.observedHotspotR1ApsPerEssInScanHistogram;
                        int length29 = numConnectableNetworksBucketArr29 == null ? 0 : numConnectableNetworksBucketArr29.length;
                        int i29 = repeatedFieldArrayLength29 + length29;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr30 = new NumConnectableNetworksBucket[i29];
                        if (length29 != 0) {
                            System.arraycopy(this.observedHotspotR1ApsPerEssInScanHistogram, 0, numConnectableNetworksBucketArr30, 0, length29);
                        }
                        while (length29 < i29 - 1) {
                            numConnectableNetworksBucketArr30[length29] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr30[length29]);
                            codedInputByteBufferNano.readTag();
                            length29++;
                        }
                        numConnectableNetworksBucketArr30[length29] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr30[length29]);
                        this.observedHotspotR1ApsPerEssInScanHistogram = numConnectableNetworksBucketArr30;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_SEND_SMS /* 706 */:
                        int repeatedFieldArrayLength30 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_SEND_SMS);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr31 = this.observedHotspotR2ApsPerEssInScanHistogram;
                        int length30 = numConnectableNetworksBucketArr31 == null ? 0 : numConnectableNetworksBucketArr31.length;
                        int i30 = repeatedFieldArrayLength30 + length30;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr32 = new NumConnectableNetworksBucket[i30];
                        if (length30 != 0) {
                            System.arraycopy(this.observedHotspotR2ApsPerEssInScanHistogram, 0, numConnectableNetworksBucketArr32, 0, length30);
                        }
                        while (length30 < i30 - 1) {
                            numConnectableNetworksBucketArr32[length30] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr32[length30]);
                            codedInputByteBufferNano.readTag();
                            length30++;
                        }
                        numConnectableNetworksBucketArr32[length30] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr32[length30]);
                        this.observedHotspotR2ApsPerEssInScanHistogram = numConnectableNetworksBucketArr32;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_READ_SMS /* 714 */:
                        int repeatedFieldArrayLength31 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_READ_SMS);
                        SoftApConnectedClientsEvent[] softApConnectedClientsEventArr = this.softApConnectedClientsEventsTethered;
                        int length31 = softApConnectedClientsEventArr == null ? 0 : softApConnectedClientsEventArr.length;
                        int i31 = repeatedFieldArrayLength31 + length31;
                        SoftApConnectedClientsEvent[] softApConnectedClientsEventArr2 = new SoftApConnectedClientsEvent[i31];
                        if (length31 != 0) {
                            System.arraycopy(this.softApConnectedClientsEventsTethered, 0, softApConnectedClientsEventArr2, 0, length31);
                        }
                        while (length31 < i31 - 1) {
                            softApConnectedClientsEventArr2[length31] = new SoftApConnectedClientsEvent();
                            codedInputByteBufferNano.readMessage(softApConnectedClientsEventArr2[length31]);
                            codedInputByteBufferNano.readTag();
                            length31++;
                        }
                        softApConnectedClientsEventArr2[length31] = new SoftApConnectedClientsEvent();
                        codedInputByteBufferNano.readMessage(softApConnectedClientsEventArr2[length31]);
                        this.softApConnectedClientsEventsTethered = softApConnectedClientsEventArr2;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_RECEIVE_MMS /* 722 */:
                        int repeatedFieldArrayLength32 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_RECEIVE_MMS);
                        SoftApConnectedClientsEvent[] softApConnectedClientsEventArr3 = this.softApConnectedClientsEventsLocalOnly;
                        int length32 = softApConnectedClientsEventArr3 == null ? 0 : softApConnectedClientsEventArr3.length;
                        int i32 = repeatedFieldArrayLength32 + length32;
                        SoftApConnectedClientsEvent[] softApConnectedClientsEventArr4 = new SoftApConnectedClientsEvent[i32];
                        if (length32 != 0) {
                            System.arraycopy(this.softApConnectedClientsEventsLocalOnly, 0, softApConnectedClientsEventArr4, 0, length32);
                        }
                        while (length32 < i32 - 1) {
                            softApConnectedClientsEventArr4[length32] = new SoftApConnectedClientsEvent();
                            codedInputByteBufferNano.readMessage(softApConnectedClientsEventArr4[length32]);
                            codedInputByteBufferNano.readTag();
                            length32++;
                        }
                        softApConnectedClientsEventArr4[length32] = new SoftApConnectedClientsEvent();
                        codedInputByteBufferNano.readMessage(softApConnectedClientsEventArr4[length32]);
                        this.softApConnectedClientsEventsLocalOnly = softApConnectedClientsEventArr4;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE /* 730 */:
                        if (this.wpsMetrics == null) {
                            this.wpsMetrics = new WpsMetrics();
                        }
                        codedInputByteBufferNano.readMessage(this.wpsMetrics);
                        break;
                    case MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_READ_PHONE_NUMBERS /* 738 */:
                        if (this.wifiPowerStats == null) {
                            this.wifiPowerStats = new WifiPowerStats();
                        }
                        codedInputByteBufferNano.readMessage(this.wifiPowerStats);
                        break;
                    case MetricsProto.MetricsEvent.SETTINGS_SYSTEM_CATEGORY /* 744 */:
                        this.numConnectivityOneshotScans = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.SETTINGS_GESTURE_DOUBLE_TAP_SCREEN /* 754 */:
                        if (this.wifiWakeStats == null) {
                            this.wifiWakeStats = new WifiWakeStats();
                        }
                        codedInputByteBufferNano.readMessage(this.wifiWakeStats);
                        break;
                    case MetricsProto.MetricsEvent.ACTION_LEAVE_SEARCH_RESULT_WITHOUT_QUERY /* 762 */:
                        int repeatedFieldArrayLength33 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.ACTION_LEAVE_SEARCH_RESULT_WITHOUT_QUERY);
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr33 = this.observed80211McSupportingApsInScanHistogram;
                        int length33 = numConnectableNetworksBucketArr33 == null ? 0 : numConnectableNetworksBucketArr33.length;
                        int i33 = repeatedFieldArrayLength33 + length33;
                        NumConnectableNetworksBucket[] numConnectableNetworksBucketArr34 = new NumConnectableNetworksBucket[i33];
                        if (length33 != 0) {
                            System.arraycopy(this.observed80211McSupportingApsInScanHistogram, 0, numConnectableNetworksBucketArr34, 0, length33);
                        }
                        while (length33 < i33 - 1) {
                            numConnectableNetworksBucketArr34[length33] = new NumConnectableNetworksBucket();
                            codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr34[length33]);
                            codedInputByteBufferNano.readTag();
                            length33++;
                        }
                        numConnectableNetworksBucketArr34[length33] = new NumConnectableNetworksBucket();
                        codedInputByteBufferNano.readMessage(numConnectableNetworksBucketArr34[length33]);
                        this.observed80211McSupportingApsInScanHistogram = numConnectableNetworksBucketArr34;
                        break;
                    case 768:
                        this.numSupplicantCrashes = codedInputByteBufferNano.readInt32();
                        break;
                    case 776:
                        this.numHostapdCrashes = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.APP_SPECIAL_PERMISSION_USAGE_VIEW_DENY /* 784 */:
                        this.numSetupClientInterfaceFailureDueToSupplicant = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.DEFAULT_AUTOFILL_PICKER /* 792 */:
                        this.numSetupSoftApInterfaceFailureDueToHal = codedInputByteBufferNano.readInt32();
                        break;
                    case 800:
                        this.numSetupSoftApInterfaceFailureDueToWificond = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.MANAGE_EXTERNAL_SOURCES /* 808 */:
                        this.numSetupSoftApInterfaceFailureDueToHostapd = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_THEME /* 816 */:
                        this.numClientInterfaceDown = codedInputByteBufferNano.readInt32();
                        break;
                    case 824:
                        this.numSoftApInterfaceDown = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.NOTIFICATION_SNOOZED_CRITERIA /* 832 */:
                        this.numExternalAppOneshotScanRequests = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.STORAGE_FREE_UP_SPACE_NOW /* 840 */:
                        this.numExternalForegroundAppOneshotScanRequestsThrottled = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.FIELD_SETTINGS_BUILD_NUMBER_DEVELOPER_MODE_ENABLED /* 848 */:
                        this.numExternalBackgroundAppOneshotScanRequestsThrottled = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_NOTIFICATION_CHANNEL /* 856 */:
                        this.watchdogTriggerToConnectionSuccessDurationMs = codedInputByteBufferNano.readInt64();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_GET_CONTACT /* 864 */:
                        this.watchdogTotalConnectionFailureCountAfterTrigger = codedInputByteBufferNano.readInt64();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_SETTINGS_UNINSTALL_APP /* 872 */:
                        this.numOneshotHasDfsChannelScans = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.SETTINGS_LOCK_SCREEN_PREFERENCES /* 882 */:
                        if (this.wifiRttLog == null) {
                            this.wifiRttLog = new WifiRttLog();
                        }
                        codedInputByteBufferNano.readMessage(this.wifiRttLog);
                        break;
                    case MetricsProto.MetricsEvent.ACTION_APPOP_GRANT_SYSTEM_ALERT_WINDOW /* 888 */:
                        this.isMacRandomizationOn = codedInputByteBufferNano.readBool();
                        break;
                    case 896:
                        this.numRadioModeChangeToMcc = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.APP_TRANSITION_CALLING_PACKAGE_NAME /* 904 */:
                        this.numRadioModeChangeToScc = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.AUTOFILL_AUTHENTICATED /* 912 */:
                        this.numRadioModeChangeToSbs = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.METRICS_CHECKPOINT /* 920 */:
                        this.numRadioModeChangeToDbs = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.FIELD_QS_VALUE /* 928 */:
                        this.numSoftApUserBandPreferenceUnsatisfied = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ENTERPRISE_PRIVACY_INSTALLED_APPS /* 938 */:
                        this.scoreExperimentId = codedInputByteBufferNano.readString();
                        break;
                    default:
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }

        public static WifiLog parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiLog) MessageNano.mergeFrom(new WifiLog(), bArr);
        }

        public static WifiLog parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiLog().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class RouterFingerPrint extends MessageNano {
        public static final int AUTH_ENTERPRISE = 3;
        public static final int AUTH_OPEN = 1;
        public static final int AUTH_PERSONAL = 2;
        public static final int AUTH_UNKNOWN = 0;
        public static final int ROAM_TYPE_DBDC = 3;
        public static final int ROAM_TYPE_ENTERPRISE = 2;
        public static final int ROAM_TYPE_NONE = 1;
        public static final int ROAM_TYPE_UNKNOWN = 0;
        public static final int ROUTER_TECH_A = 1;
        public static final int ROUTER_TECH_AC = 5;
        public static final int ROUTER_TECH_B = 2;
        public static final int ROUTER_TECH_G = 3;
        public static final int ROUTER_TECH_N = 4;
        public static final int ROUTER_TECH_OTHER = 6;
        public static final int ROUTER_TECH_UNKNOWN = 0;
        private static volatile RouterFingerPrint[] _emptyArray;
        public int authentication;
        public int channelInfo;
        public int dtim;
        public boolean hidden;
        public boolean passpoint;
        public int roamType;
        public int routerTechnology;
        public boolean supportsIpv6;

        public static RouterFingerPrint[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new RouterFingerPrint[0];
                    }
                }
            }
            return _emptyArray;
        }

        public RouterFingerPrint() {
            clear();
        }

        public RouterFingerPrint clear() {
            this.roamType = 0;
            this.channelInfo = 0;
            this.dtim = 0;
            this.authentication = 0;
            this.hidden = false;
            this.routerTechnology = 0;
            this.supportsIpv6 = false;
            this.passpoint = false;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.roamType;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.channelInfo;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.dtim;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            int i4 = this.authentication;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i4);
            }
            boolean z = this.hidden;
            if (z) {
                codedOutputByteBufferNano.writeBool(5, z);
            }
            int i5 = this.routerTechnology;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(6, i5);
            }
            boolean z2 = this.supportsIpv6;
            if (z2) {
                codedOutputByteBufferNano.writeBool(7, z2);
            }
            boolean z3 = this.passpoint;
            if (z3) {
                codedOutputByteBufferNano.writeBool(8, z3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.roamType;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.channelInfo;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.dtim;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.authentication;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            boolean z = this.hidden;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
            }
            int i5 = this.routerTechnology;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
            }
            boolean z2 = this.supportsIpv6;
            if (z2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
            }
            boolean z3 = this.passpoint;
            return z3 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(8, z3) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public RouterFingerPrint mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                        this.roamType = int32;
                    }
                } else if (tag == 16) {
                    this.channelInfo = codedInputByteBufferNano.readInt32();
                } else if (tag == 24) {
                    this.dtim = codedInputByteBufferNano.readInt32();
                } else if (tag == 32) {
                    int int33 = codedInputByteBufferNano.readInt32();
                    if (int33 == 0 || int33 == 1 || int33 == 2 || int33 == 3) {
                        this.authentication = int33;
                    }
                } else if (tag == 40) {
                    this.hidden = codedInputByteBufferNano.readBool();
                } else if (tag == 48) {
                    int int34 = codedInputByteBufferNano.readInt32();
                    switch (int34) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.routerTechnology = int34;
                            break;
                    }
                } else if (tag == 56) {
                    this.supportsIpv6 = codedInputByteBufferNano.readBool();
                } else if (tag != 64) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.passpoint = codedInputByteBufferNano.readBool();
                }
            }
        }

        public static RouterFingerPrint parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (RouterFingerPrint) MessageNano.mergeFrom(new RouterFingerPrint(), bArr);
        }

        public static RouterFingerPrint parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new RouterFingerPrint().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class ConnectionEvent extends MessageNano {
        public static final int HLF_DHCP = 2;
        public static final int HLF_NONE = 1;
        public static final int HLF_NO_INTERNET = 3;
        public static final int HLF_UNKNOWN = 0;
        public static final int HLF_UNWANTED = 4;
        public static final int ROAM_DBDC = 2;
        public static final int ROAM_ENTERPRISE = 3;
        public static final int ROAM_NONE = 1;
        public static final int ROAM_UNKNOWN = 0;
        public static final int ROAM_UNRELATED = 5;
        public static final int ROAM_USER_SELECTED = 4;
        private static volatile ConnectionEvent[] _emptyArray;
        public boolean automaticBugReportTaken;
        public int connectionResult;
        public int connectivityLevelFailureCode;
        public int durationTakenToConnectMillis;
        public int level2FailureCode;
        public int roamType;
        public RouterFingerPrint routerFingerprint;
        public int signalStrength;
        public long startTimeMillis;

        public static ConnectionEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ConnectionEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ConnectionEvent() {
            clear();
        }

        public ConnectionEvent clear() {
            this.startTimeMillis = 0L;
            this.durationTakenToConnectMillis = 0;
            this.routerFingerprint = null;
            this.signalStrength = 0;
            this.roamType = 0;
            this.connectionResult = 0;
            this.level2FailureCode = 0;
            this.connectivityLevelFailureCode = 0;
            this.automaticBugReportTaken = false;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            long j = this.startTimeMillis;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(1, j);
            }
            int i = this.durationTakenToConnectMillis;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(2, i);
            }
            RouterFingerPrint routerFingerPrint = this.routerFingerprint;
            if (routerFingerPrint != null) {
                codedOutputByteBufferNano.writeMessage(3, routerFingerPrint);
            }
            int i2 = this.signalStrength;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i2);
            }
            int i3 = this.roamType;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i3);
            }
            int i4 = this.connectionResult;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(6, i4);
            }
            int i5 = this.level2FailureCode;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(7, i5);
            }
            int i6 = this.connectivityLevelFailureCode;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i6);
            }
            boolean z = this.automaticBugReportTaken;
            if (z) {
                codedOutputByteBufferNano.writeBool(9, z);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            long j = this.startTimeMillis;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
            }
            int i = this.durationTakenToConnectMillis;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            RouterFingerPrint routerFingerPrint = this.routerFingerprint;
            if (routerFingerPrint != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, routerFingerPrint);
            }
            int i2 = this.signalStrength;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i2);
            }
            int i3 = this.roamType;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i3);
            }
            int i4 = this.connectionResult;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i4);
            }
            int i5 = this.level2FailureCode;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i5);
            }
            int i6 = this.connectivityLevelFailureCode;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i6);
            }
            boolean z = this.automaticBugReportTaken;
            return z ? iComputeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(9, z) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public ConnectionEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.startTimeMillis = codedInputByteBufferNano.readInt64();
                } else if (tag == 16) {
                    this.durationTakenToConnectMillis = codedInputByteBufferNano.readInt32();
                } else if (tag == 26) {
                    if (this.routerFingerprint == null) {
                        this.routerFingerprint = new RouterFingerPrint();
                    }
                    codedInputByteBufferNano.readMessage(this.routerFingerprint);
                } else if (tag == 32) {
                    this.signalStrength = codedInputByteBufferNano.readInt32();
                } else if (tag == 40) {
                    int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3 || int32 == 4 || int32 == 5) {
                        this.roamType = int32;
                    }
                } else if (tag == 48) {
                    this.connectionResult = codedInputByteBufferNano.readInt32();
                } else if (tag == 56) {
                    this.level2FailureCode = codedInputByteBufferNano.readInt32();
                } else if (tag == 64) {
                    int int33 = codedInputByteBufferNano.readInt32();
                    if (int33 == 0 || int33 == 1 || int33 == 2 || int33 == 3 || int33 == 4) {
                        this.connectivityLevelFailureCode = int33;
                    }
                } else if (tag != 72) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.automaticBugReportTaken = codedInputByteBufferNano.readBool();
                }
            }
        }

        public static ConnectionEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (ConnectionEvent) MessageNano.mergeFrom(new ConnectionEvent(), bArr);
        }

        public static ConnectionEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new ConnectionEvent().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class RssiPollCount extends MessageNano {
        private static volatile RssiPollCount[] _emptyArray;
        public int count;
        public int frequency;
        public int rssi;

        public static RssiPollCount[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new RssiPollCount[0];
                    }
                }
            }
            return _emptyArray;
        }

        public RssiPollCount() {
            clear();
        }

        public RssiPollCount clear() {
            this.rssi = 0;
            this.count = 0;
            this.frequency = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.rssi;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.frequency;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.rssi;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.frequency;
            return i3 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public RssiPollCount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.rssi = codedInputByteBufferNano.readInt32();
                } else if (tag == 16) {
                    this.count = codedInputByteBufferNano.readInt32();
                } else if (tag != 24) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.frequency = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static RssiPollCount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (RssiPollCount) MessageNano.mergeFrom(new RssiPollCount(), bArr);
        }

        public static RssiPollCount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new RssiPollCount().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class AlertReasonCount extends MessageNano {
        private static volatile AlertReasonCount[] _emptyArray;
        public int count;
        public int reason;

        public static AlertReasonCount[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new AlertReasonCount[0];
                    }
                }
            }
            return _emptyArray;
        }

        public AlertReasonCount() {
            clear();
        }

        public AlertReasonCount clear() {
            this.reason = 0;
            this.count = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.reason;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.reason;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.count;
            return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public AlertReasonCount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.reason = codedInputByteBufferNano.readInt32();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.count = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static AlertReasonCount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (AlertReasonCount) MessageNano.mergeFrom(new AlertReasonCount(), bArr);
        }

        public static AlertReasonCount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new AlertReasonCount().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WifiScoreCount extends MessageNano {
        private static volatile WifiScoreCount[] _emptyArray;
        public int count;
        public int score;

        public static WifiScoreCount[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiScoreCount[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiScoreCount() {
            clear();
        }

        public WifiScoreCount clear() {
            this.score = 0;
            this.count = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.score;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.score;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.count;
            return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiScoreCount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.score = codedInputByteBufferNano.readInt32();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.count = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static WifiScoreCount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiScoreCount) MessageNano.mergeFrom(new WifiScoreCount(), bArr);
        }

        public static WifiScoreCount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiScoreCount().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class SoftApDurationBucket extends MessageNano {
        private static volatile SoftApDurationBucket[] _emptyArray;
        public int bucketSizeSec;
        public int count;
        public int durationSec;

        public static SoftApDurationBucket[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SoftApDurationBucket[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SoftApDurationBucket() {
            clear();
        }

        public SoftApDurationBucket clear() {
            this.durationSec = 0;
            this.bucketSizeSec = 0;
            this.count = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.durationSec;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.bucketSizeSec;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.count;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.durationSec;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.bucketSizeSec;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.count;
            return i3 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public SoftApDurationBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.durationSec = codedInputByteBufferNano.readInt32();
                } else if (tag == 16) {
                    this.bucketSizeSec = codedInputByteBufferNano.readInt32();
                } else if (tag != 24) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.count = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static SoftApDurationBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (SoftApDurationBucket) MessageNano.mergeFrom(new SoftApDurationBucket(), bArr);
        }

        public static SoftApDurationBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new SoftApDurationBucket().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class SoftApReturnCodeCount extends MessageNano {
        public static final int SOFT_AP_FAILED_GENERAL_ERROR = 2;
        public static final int SOFT_AP_FAILED_NO_CHANNEL = 3;
        public static final int SOFT_AP_RETURN_CODE_UNKNOWN = 0;
        public static final int SOFT_AP_STARTED_SUCCESSFULLY = 1;
        private static volatile SoftApReturnCodeCount[] _emptyArray;
        public int count;
        public int returnCode;
        public int startResult;

        public static SoftApReturnCodeCount[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SoftApReturnCodeCount[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SoftApReturnCodeCount() {
            clear();
        }

        public SoftApReturnCodeCount clear() {
            this.returnCode = 0;
            this.count = 0;
            this.startResult = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.returnCode;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.startResult;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.returnCode;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.startResult;
            return i3 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public SoftApReturnCodeCount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.returnCode = codedInputByteBufferNano.readInt32();
                } else if (tag == 16) {
                    this.count = codedInputByteBufferNano.readInt32();
                } else if (tag != 24) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                        this.startResult = int32;
                    }
                }
            }
        }

        public static SoftApReturnCodeCount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (SoftApReturnCodeCount) MessageNano.mergeFrom(new SoftApReturnCodeCount(), bArr);
        }

        public static SoftApReturnCodeCount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new SoftApReturnCodeCount().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class StaEvent extends MessageNano {
        public static final int AUTH_FAILURE_EAP_FAILURE = 4;
        public static final int AUTH_FAILURE_NONE = 1;
        public static final int AUTH_FAILURE_TIMEOUT = 2;
        public static final int AUTH_FAILURE_UNKNOWN = 0;
        public static final int AUTH_FAILURE_WRONG_PSWD = 3;
        public static final int DISCONNECT_API = 1;
        public static final int DISCONNECT_GENERIC = 2;
        public static final int DISCONNECT_P2P_DISCONNECT_WIFI_REQUEST = 5;
        public static final int DISCONNECT_RESET_SIM_NETWORKS = 6;
        public static final int DISCONNECT_ROAM_WATCHDOG_TIMER = 4;
        public static final int DISCONNECT_UNKNOWN = 0;
        public static final int DISCONNECT_UNWANTED = 3;
        public static final int STATE_ASSOCIATED = 6;
        public static final int STATE_ASSOCIATING = 5;
        public static final int STATE_AUTHENTICATING = 4;
        public static final int STATE_COMPLETED = 9;
        public static final int STATE_DISCONNECTED = 0;
        public static final int STATE_DORMANT = 10;
        public static final int STATE_FOUR_WAY_HANDSHAKE = 7;
        public static final int STATE_GROUP_HANDSHAKE = 8;
        public static final int STATE_INACTIVE = 2;
        public static final int STATE_INTERFACE_DISABLED = 1;
        public static final int STATE_INVALID = 12;
        public static final int STATE_SCANNING = 3;
        public static final int STATE_UNINITIALIZED = 11;
        public static final int TYPE_ASSOCIATION_REJECTION_EVENT = 1;
        public static final int TYPE_AUTHENTICATION_FAILURE_EVENT = 2;
        public static final int TYPE_CMD_ASSOCIATED_BSSID = 6;
        public static final int TYPE_CMD_IP_CONFIGURATION_LOST = 8;
        public static final int TYPE_CMD_IP_CONFIGURATION_SUCCESSFUL = 7;
        public static final int TYPE_CMD_IP_REACHABILITY_LOST = 9;
        public static final int TYPE_CMD_START_CONNECT = 11;
        public static final int TYPE_CMD_START_ROAM = 12;
        public static final int TYPE_CMD_TARGET_BSSID = 10;
        public static final int TYPE_CONNECT_NETWORK = 13;
        public static final int TYPE_FRAMEWORK_DISCONNECT = 15;
        public static final int TYPE_MAC_CHANGE = 17;
        public static final int TYPE_NETWORK_AGENT_VALID_NETWORK = 14;
        public static final int TYPE_NETWORK_CONNECTION_EVENT = 3;
        public static final int TYPE_NETWORK_DISCONNECTION_EVENT = 4;
        public static final int TYPE_SCORE_BREACH = 16;
        public static final int TYPE_SUPPLICANT_STATE_CHANGE_EVENT = 5;
        public static final int TYPE_UNKNOWN = 0;
        private static volatile StaEvent[] _emptyArray;
        public boolean associationTimedOut;
        public int authFailureReason;
        public ConfigInfo configInfo;
        public int frameworkDisconnectReason;
        public int lastFreq;
        public int lastLinkSpeed;
        public int lastRssi;
        public int lastScore;
        public boolean localGen;
        public int reason;
        public long startTimeMillis;
        public int status;
        public int supplicantStateChangesBitmask;
        public int type;

        public static final class ConfigInfo extends MessageNano {
            private static volatile ConfigInfo[] _emptyArray;
            public int allowedAuthAlgorithms;
            public int allowedGroupCiphers;
            public int allowedKeyManagement;
            public int allowedPairwiseCiphers;
            public int allowedProtocols;
            public boolean hasEverConnected;
            public boolean hiddenSsid;
            public boolean isEphemeral;
            public boolean isPasspoint;
            public int scanFreq;
            public int scanRssi;

            public static ConfigInfo[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new ConfigInfo[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public ConfigInfo() {
                clear();
            }

            public ConfigInfo clear() {
                this.allowedKeyManagement = 0;
                this.allowedProtocols = 0;
                this.allowedAuthAlgorithms = 0;
                this.allowedPairwiseCiphers = 0;
                this.allowedGroupCiphers = 0;
                this.hiddenSsid = false;
                this.isPasspoint = false;
                this.isEphemeral = false;
                this.hasEverConnected = false;
                this.scanRssi = -127;
                this.scanFreq = -1;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.allowedKeyManagement;
                if (i != 0) {
                    codedOutputByteBufferNano.writeUInt32(1, i);
                }
                int i2 = this.allowedProtocols;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeUInt32(2, i2);
                }
                int i3 = this.allowedAuthAlgorithms;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeUInt32(3, i3);
                }
                int i4 = this.allowedPairwiseCiphers;
                if (i4 != 0) {
                    codedOutputByteBufferNano.writeUInt32(4, i4);
                }
                int i5 = this.allowedGroupCiphers;
                if (i5 != 0) {
                    codedOutputByteBufferNano.writeUInt32(5, i5);
                }
                boolean z = this.hiddenSsid;
                if (z) {
                    codedOutputByteBufferNano.writeBool(6, z);
                }
                boolean z2 = this.isPasspoint;
                if (z2) {
                    codedOutputByteBufferNano.writeBool(7, z2);
                }
                boolean z3 = this.isEphemeral;
                if (z3) {
                    codedOutputByteBufferNano.writeBool(8, z3);
                }
                boolean z4 = this.hasEverConnected;
                if (z4) {
                    codedOutputByteBufferNano.writeBool(9, z4);
                }
                int i6 = this.scanRssi;
                if (i6 != -127) {
                    codedOutputByteBufferNano.writeInt32(10, i6);
                }
                int i7 = this.scanFreq;
                if (i7 != -1) {
                    codedOutputByteBufferNano.writeInt32(11, i7);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.allowedKeyManagement;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(1, i);
                }
                int i2 = this.allowedProtocols;
                if (i2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(2, i2);
                }
                int i3 = this.allowedAuthAlgorithms;
                if (i3 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(3, i3);
                }
                int i4 = this.allowedPairwiseCiphers;
                if (i4 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(4, i4);
                }
                int i5 = this.allowedGroupCiphers;
                if (i5 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(5, i5);
                }
                boolean z = this.hiddenSsid;
                if (z) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z);
                }
                boolean z2 = this.isPasspoint;
                if (z2) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z2);
                }
                boolean z3 = this.isEphemeral;
                if (z3) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(8, z3);
                }
                boolean z4 = this.hasEverConnected;
                if (z4) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(9, z4);
                }
                int i6 = this.scanRssi;
                if (i6 != -127) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i6);
                }
                int i7 = this.scanFreq;
                return i7 != -1 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(11, i7) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public ConfigInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    switch (tag) {
                        case 0:
                            return this;
                        case 8:
                            this.allowedKeyManagement = codedInputByteBufferNano.readUInt32();
                            break;
                        case 16:
                            this.allowedProtocols = codedInputByteBufferNano.readUInt32();
                            break;
                        case 24:
                            this.allowedAuthAlgorithms = codedInputByteBufferNano.readUInt32();
                            break;
                        case 32:
                            this.allowedPairwiseCiphers = codedInputByteBufferNano.readUInt32();
                            break;
                        case 40:
                            this.allowedGroupCiphers = codedInputByteBufferNano.readUInt32();
                            break;
                        case 48:
                            this.hiddenSsid = codedInputByteBufferNano.readBool();
                            break;
                        case 56:
                            this.isPasspoint = codedInputByteBufferNano.readBool();
                            break;
                        case 64:
                            this.isEphemeral = codedInputByteBufferNano.readBool();
                            break;
                        case 72:
                            this.hasEverConnected = codedInputByteBufferNano.readBool();
                            break;
                        case 80:
                            this.scanRssi = codedInputByteBufferNano.readInt32();
                            break;
                        case 88:
                            this.scanFreq = codedInputByteBufferNano.readInt32();
                            break;
                        default:
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                            break;
                            break;
                    }
                }
            }

            public static ConfigInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (ConfigInfo) MessageNano.mergeFrom(new ConfigInfo(), bArr);
            }

            public static ConfigInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new ConfigInfo().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static StaEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new StaEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public StaEvent() {
            clear();
        }

        public StaEvent clear() {
            this.type = 0;
            this.reason = -1;
            this.status = -1;
            this.localGen = false;
            this.configInfo = null;
            this.lastRssi = -127;
            this.lastLinkSpeed = -1;
            this.lastFreq = -1;
            this.supplicantStateChangesBitmask = 0;
            this.startTimeMillis = 0L;
            this.frameworkDisconnectReason = 0;
            this.associationTimedOut = false;
            this.authFailureReason = 0;
            this.lastScore = -1;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.type;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.reason;
            if (i2 != -1) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.status;
            if (i3 != -1) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            boolean z = this.localGen;
            if (z) {
                codedOutputByteBufferNano.writeBool(4, z);
            }
            ConfigInfo configInfo = this.configInfo;
            if (configInfo != null) {
                codedOutputByteBufferNano.writeMessage(5, configInfo);
            }
            int i4 = this.lastRssi;
            if (i4 != -127) {
                codedOutputByteBufferNano.writeInt32(6, i4);
            }
            int i5 = this.lastLinkSpeed;
            if (i5 != -1) {
                codedOutputByteBufferNano.writeInt32(7, i5);
            }
            int i6 = this.lastFreq;
            if (i6 != -1) {
                codedOutputByteBufferNano.writeInt32(8, i6);
            }
            int i7 = this.supplicantStateChangesBitmask;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeUInt32(9, i7);
            }
            long j = this.startTimeMillis;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(10, j);
            }
            int i8 = this.frameworkDisconnectReason;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(11, i8);
            }
            boolean z2 = this.associationTimedOut;
            if (z2) {
                codedOutputByteBufferNano.writeBool(12, z2);
            }
            int i9 = this.authFailureReason;
            if (i9 != 0) {
                codedOutputByteBufferNano.writeInt32(13, i9);
            }
            int i10 = this.lastScore;
            if (i10 != -1) {
                codedOutputByteBufferNano.writeInt32(14, i10);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.reason;
            if (i2 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.status;
            if (i3 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            boolean z = this.localGen;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(4, z);
            }
            ConfigInfo configInfo = this.configInfo;
            if (configInfo != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, configInfo);
            }
            int i4 = this.lastRssi;
            if (i4 != -127) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i4);
            }
            int i5 = this.lastLinkSpeed;
            if (i5 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i5);
            }
            int i6 = this.lastFreq;
            if (i6 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i6);
            }
            int i7 = this.supplicantStateChangesBitmask;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(9, i7);
            }
            long j = this.startTimeMillis;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(10, j);
            }
            int i8 = this.frameworkDisconnectReason;
            if (i8 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i8);
            }
            boolean z2 = this.associationTimedOut;
            if (z2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(12, z2);
            }
            int i9 = this.authFailureReason;
            if (i9 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i9);
            }
            int i10 = this.lastScore;
            return i10 != -1 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(14, i10) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public StaEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                this.type = int32;
                                break;
                        }
                        break;
                    case 16:
                        this.reason = codedInputByteBufferNano.readInt32();
                        break;
                    case 24:
                        this.status = codedInputByteBufferNano.readInt32();
                        break;
                    case 32:
                        this.localGen = codedInputByteBufferNano.readBool();
                        break;
                    case 42:
                        if (this.configInfo == null) {
                            this.configInfo = new ConfigInfo();
                        }
                        codedInputByteBufferNano.readMessage(this.configInfo);
                        break;
                    case 48:
                        this.lastRssi = codedInputByteBufferNano.readInt32();
                        break;
                    case 56:
                        this.lastLinkSpeed = codedInputByteBufferNano.readInt32();
                        break;
                    case 64:
                        this.lastFreq = codedInputByteBufferNano.readInt32();
                        break;
                    case 72:
                        this.supplicantStateChangesBitmask = codedInputByteBufferNano.readUInt32();
                        break;
                    case 80:
                        this.startTimeMillis = codedInputByteBufferNano.readInt64();
                        break;
                    case 88:
                        int int33 = codedInputByteBufferNano.readInt32();
                        switch (int33) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.frameworkDisconnectReason = int33;
                                break;
                        }
                        break;
                    case 96:
                        this.associationTimedOut = codedInputByteBufferNano.readBool();
                        break;
                    case 104:
                        int int34 = codedInputByteBufferNano.readInt32();
                        if (int34 == 0 || int34 == 1 || int34 == 2 || int34 == 3 || int34 == 4) {
                            this.authFailureReason = int34;
                        }
                        break;
                    case 112:
                        this.lastScore = codedInputByteBufferNano.readInt32();
                        break;
                    default:
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }

        public static StaEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (StaEvent) MessageNano.mergeFrom(new StaEvent(), bArr);
        }

        public static StaEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new StaEvent().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WifiAwareLog extends MessageNano {
        public static final int ALREADY_ENABLED = 11;
        public static final int FOLLOWUP_TX_QUEUE_FULL = 12;
        public static final int INTERNAL_FAILURE = 2;
        public static final int INVALID_ARGS = 6;
        public static final int INVALID_NDP_ID = 8;
        public static final int INVALID_PEER_ID = 7;
        public static final int INVALID_SESSION_ID = 4;
        public static final int NAN_NOT_ALLOWED = 9;
        public static final int NO_OTA_ACK = 10;
        public static final int NO_RESOURCES_AVAILABLE = 5;
        public static final int PROTOCOL_FAILURE = 3;
        public static final int SUCCESS = 1;
        public static final int UNKNOWN = 0;
        public static final int UNKNOWN_HAL_STATUS = 14;
        public static final int UNSUPPORTED_CONCURRENCY_NAN_DISABLED = 13;
        private static volatile WifiAwareLog[] _emptyArray;
        public long availableTimeMs;
        public long enabledTimeMs;
        public HistogramBucket[] histogramAttachDurationMs;
        public NanStatusHistogramBucket[] histogramAttachSessionStatus;
        public HistogramBucket[] histogramAwareAvailableDurationMs;
        public HistogramBucket[] histogramAwareEnabledDurationMs;
        public HistogramBucket[] histogramNdpCreationTimeMs;
        public HistogramBucket[] histogramNdpSessionDataUsageMb;
        public HistogramBucket[] histogramNdpSessionDurationMs;
        public HistogramBucket[] histogramPublishSessionDurationMs;
        public NanStatusHistogramBucket[] histogramPublishStatus;
        public NanStatusHistogramBucket[] histogramRequestNdpOobStatus;
        public NanStatusHistogramBucket[] histogramRequestNdpStatus;
        public HistogramBucket[] histogramSubscribeGeofenceMax;
        public HistogramBucket[] histogramSubscribeGeofenceMin;
        public HistogramBucket[] histogramSubscribeSessionDurationMs;
        public NanStatusHistogramBucket[] histogramSubscribeStatus;
        public int maxConcurrentAttachSessionsInApp;
        public int maxConcurrentDiscoverySessionsInApp;
        public int maxConcurrentDiscoverySessionsInSystem;
        public int maxConcurrentNdiInApp;
        public int maxConcurrentNdiInSystem;
        public int maxConcurrentNdpInApp;
        public int maxConcurrentNdpInSystem;
        public int maxConcurrentNdpPerNdi;
        public int maxConcurrentPublishInApp;
        public int maxConcurrentPublishInSystem;
        public int maxConcurrentPublishWithRangingInApp;
        public int maxConcurrentPublishWithRangingInSystem;
        public int maxConcurrentSecureNdpInApp;
        public int maxConcurrentSecureNdpInSystem;
        public int maxConcurrentSubscribeInApp;
        public int maxConcurrentSubscribeInSystem;
        public int maxConcurrentSubscribeWithRangingInApp;
        public int maxConcurrentSubscribeWithRangingInSystem;
        public long ndpCreationTimeMsMax;
        public long ndpCreationTimeMsMin;
        public long ndpCreationTimeMsNumSamples;
        public long ndpCreationTimeMsSum;
        public long ndpCreationTimeMsSumOfSq;
        public int numApps;
        public int numAppsUsingIdentityCallback;
        public int numAppsWithDiscoverySessionFailureOutOfResources;
        public int numMatchesWithRanging;
        public int numMatchesWithoutRangingForRangingEnabledSubscribes;
        public int numSubscribesWithRanging;

        public static final class HistogramBucket extends MessageNano {
            private static volatile HistogramBucket[] _emptyArray;
            public int count;
            public long end;
            public long start;

            public static HistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new HistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public HistogramBucket() {
                clear();
            }

            public HistogramBucket clear() {
                this.start = 0L;
                this.end = 0L;
                this.count = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                long j = this.start;
                if (j != 0) {
                    codedOutputByteBufferNano.writeInt64(1, j);
                }
                long j2 = this.end;
                if (j2 != 0) {
                    codedOutputByteBufferNano.writeInt64(2, j2);
                }
                int i = this.count;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                long j = this.start;
                if (j != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
                }
                long j2 = this.end;
                if (j2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, j2);
                }
                int i = this.count;
                return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public HistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        this.start = codedInputByteBufferNano.readInt64();
                    } else if (tag == 16) {
                        this.end = codedInputByteBufferNano.readInt64();
                    } else if (tag != 24) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.count = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static HistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (HistogramBucket) MessageNano.mergeFrom(new HistogramBucket(), bArr);
            }

            public static HistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static final class NanStatusHistogramBucket extends MessageNano {
            private static volatile NanStatusHistogramBucket[] _emptyArray;
            public int count;
            public int nanStatusType;

            public static NanStatusHistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new NanStatusHistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public NanStatusHistogramBucket() {
                clear();
            }

            public NanStatusHistogramBucket clear() {
                this.nanStatusType = 0;
                this.count = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.nanStatusType;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.count;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.nanStatusType;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.count;
                return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public NanStatusHistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                                this.nanStatusType = int32;
                                break;
                        }
                    } else if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.count = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static NanStatusHistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (NanStatusHistogramBucket) MessageNano.mergeFrom(new NanStatusHistogramBucket(), bArr);
            }

            public static NanStatusHistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new NanStatusHistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static WifiAwareLog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiAwareLog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiAwareLog() {
            clear();
        }

        public WifiAwareLog clear() {
            this.numApps = 0;
            this.numAppsUsingIdentityCallback = 0;
            this.maxConcurrentAttachSessionsInApp = 0;
            this.histogramAttachSessionStatus = NanStatusHistogramBucket.emptyArray();
            this.maxConcurrentPublishInApp = 0;
            this.maxConcurrentSubscribeInApp = 0;
            this.maxConcurrentDiscoverySessionsInApp = 0;
            this.maxConcurrentPublishInSystem = 0;
            this.maxConcurrentSubscribeInSystem = 0;
            this.maxConcurrentDiscoverySessionsInSystem = 0;
            this.histogramPublishStatus = NanStatusHistogramBucket.emptyArray();
            this.histogramSubscribeStatus = NanStatusHistogramBucket.emptyArray();
            this.numAppsWithDiscoverySessionFailureOutOfResources = 0;
            this.histogramRequestNdpStatus = NanStatusHistogramBucket.emptyArray();
            this.histogramRequestNdpOobStatus = NanStatusHistogramBucket.emptyArray();
            this.maxConcurrentNdiInApp = 0;
            this.maxConcurrentNdiInSystem = 0;
            this.maxConcurrentNdpInApp = 0;
            this.maxConcurrentNdpInSystem = 0;
            this.maxConcurrentSecureNdpInApp = 0;
            this.maxConcurrentSecureNdpInSystem = 0;
            this.maxConcurrentNdpPerNdi = 0;
            this.histogramAwareAvailableDurationMs = HistogramBucket.emptyArray();
            this.histogramAwareEnabledDurationMs = HistogramBucket.emptyArray();
            this.histogramAttachDurationMs = HistogramBucket.emptyArray();
            this.histogramPublishSessionDurationMs = HistogramBucket.emptyArray();
            this.histogramSubscribeSessionDurationMs = HistogramBucket.emptyArray();
            this.histogramNdpSessionDurationMs = HistogramBucket.emptyArray();
            this.histogramNdpSessionDataUsageMb = HistogramBucket.emptyArray();
            this.histogramNdpCreationTimeMs = HistogramBucket.emptyArray();
            this.ndpCreationTimeMsMin = 0L;
            this.ndpCreationTimeMsMax = 0L;
            this.ndpCreationTimeMsSum = 0L;
            this.ndpCreationTimeMsSumOfSq = 0L;
            this.ndpCreationTimeMsNumSamples = 0L;
            this.availableTimeMs = 0L;
            this.enabledTimeMs = 0L;
            this.maxConcurrentPublishWithRangingInApp = 0;
            this.maxConcurrentSubscribeWithRangingInApp = 0;
            this.maxConcurrentPublishWithRangingInSystem = 0;
            this.maxConcurrentSubscribeWithRangingInSystem = 0;
            this.histogramSubscribeGeofenceMin = HistogramBucket.emptyArray();
            this.histogramSubscribeGeofenceMax = HistogramBucket.emptyArray();
            this.numSubscribesWithRanging = 0;
            this.numMatchesWithRanging = 0;
            this.numMatchesWithoutRangingForRangingEnabledSubscribes = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numApps;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.numAppsUsingIdentityCallback;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.maxConcurrentAttachSessionsInApp;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr = this.histogramAttachSessionStatus;
            int i4 = 0;
            if (nanStatusHistogramBucketArr != null && nanStatusHistogramBucketArr.length > 0) {
                int i5 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr2 = this.histogramAttachSessionStatus;
                    if (i5 >= nanStatusHistogramBucketArr2.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket = nanStatusHistogramBucketArr2[i5];
                    if (nanStatusHistogramBucket != null) {
                        codedOutputByteBufferNano.writeMessage(4, nanStatusHistogramBucket);
                    }
                    i5++;
                }
            }
            int i6 = this.maxConcurrentPublishInApp;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i6);
            }
            int i7 = this.maxConcurrentSubscribeInApp;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeInt32(6, i7);
            }
            int i8 = this.maxConcurrentDiscoverySessionsInApp;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(7, i8);
            }
            int i9 = this.maxConcurrentPublishInSystem;
            if (i9 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i9);
            }
            int i10 = this.maxConcurrentSubscribeInSystem;
            if (i10 != 0) {
                codedOutputByteBufferNano.writeInt32(9, i10);
            }
            int i11 = this.maxConcurrentDiscoverySessionsInSystem;
            if (i11 != 0) {
                codedOutputByteBufferNano.writeInt32(10, i11);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr3 = this.histogramPublishStatus;
            if (nanStatusHistogramBucketArr3 != null && nanStatusHistogramBucketArr3.length > 0) {
                int i12 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr4 = this.histogramPublishStatus;
                    if (i12 >= nanStatusHistogramBucketArr4.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket2 = nanStatusHistogramBucketArr4[i12];
                    if (nanStatusHistogramBucket2 != null) {
                        codedOutputByteBufferNano.writeMessage(11, nanStatusHistogramBucket2);
                    }
                    i12++;
                }
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr5 = this.histogramSubscribeStatus;
            if (nanStatusHistogramBucketArr5 != null && nanStatusHistogramBucketArr5.length > 0) {
                int i13 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr6 = this.histogramSubscribeStatus;
                    if (i13 >= nanStatusHistogramBucketArr6.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket3 = nanStatusHistogramBucketArr6[i13];
                    if (nanStatusHistogramBucket3 != null) {
                        codedOutputByteBufferNano.writeMessage(12, nanStatusHistogramBucket3);
                    }
                    i13++;
                }
            }
            int i14 = this.numAppsWithDiscoverySessionFailureOutOfResources;
            if (i14 != 0) {
                codedOutputByteBufferNano.writeInt32(13, i14);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr7 = this.histogramRequestNdpStatus;
            if (nanStatusHistogramBucketArr7 != null && nanStatusHistogramBucketArr7.length > 0) {
                int i15 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr8 = this.histogramRequestNdpStatus;
                    if (i15 >= nanStatusHistogramBucketArr8.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket4 = nanStatusHistogramBucketArr8[i15];
                    if (nanStatusHistogramBucket4 != null) {
                        codedOutputByteBufferNano.writeMessage(14, nanStatusHistogramBucket4);
                    }
                    i15++;
                }
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr9 = this.histogramRequestNdpOobStatus;
            if (nanStatusHistogramBucketArr9 != null && nanStatusHistogramBucketArr9.length > 0) {
                int i16 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr10 = this.histogramRequestNdpOobStatus;
                    if (i16 >= nanStatusHistogramBucketArr10.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket5 = nanStatusHistogramBucketArr10[i16];
                    if (nanStatusHistogramBucket5 != null) {
                        codedOutputByteBufferNano.writeMessage(15, nanStatusHistogramBucket5);
                    }
                    i16++;
                }
            }
            int i17 = this.maxConcurrentNdiInApp;
            if (i17 != 0) {
                codedOutputByteBufferNano.writeInt32(19, i17);
            }
            int i18 = this.maxConcurrentNdiInSystem;
            if (i18 != 0) {
                codedOutputByteBufferNano.writeInt32(20, i18);
            }
            int i19 = this.maxConcurrentNdpInApp;
            if (i19 != 0) {
                codedOutputByteBufferNano.writeInt32(21, i19);
            }
            int i20 = this.maxConcurrentNdpInSystem;
            if (i20 != 0) {
                codedOutputByteBufferNano.writeInt32(22, i20);
            }
            int i21 = this.maxConcurrentSecureNdpInApp;
            if (i21 != 0) {
                codedOutputByteBufferNano.writeInt32(23, i21);
            }
            int i22 = this.maxConcurrentSecureNdpInSystem;
            if (i22 != 0) {
                codedOutputByteBufferNano.writeInt32(24, i22);
            }
            int i23 = this.maxConcurrentNdpPerNdi;
            if (i23 != 0) {
                codedOutputByteBufferNano.writeInt32(25, i23);
            }
            HistogramBucket[] histogramBucketArr = this.histogramAwareAvailableDurationMs;
            if (histogramBucketArr != null && histogramBucketArr.length > 0) {
                int i24 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr2 = this.histogramAwareAvailableDurationMs;
                    if (i24 >= histogramBucketArr2.length) {
                        break;
                    }
                    HistogramBucket histogramBucket = histogramBucketArr2[i24];
                    if (histogramBucket != null) {
                        codedOutputByteBufferNano.writeMessage(26, histogramBucket);
                    }
                    i24++;
                }
            }
            HistogramBucket[] histogramBucketArr3 = this.histogramAwareEnabledDurationMs;
            if (histogramBucketArr3 != null && histogramBucketArr3.length > 0) {
                int i25 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr4 = this.histogramAwareEnabledDurationMs;
                    if (i25 >= histogramBucketArr4.length) {
                        break;
                    }
                    HistogramBucket histogramBucket2 = histogramBucketArr4[i25];
                    if (histogramBucket2 != null) {
                        codedOutputByteBufferNano.writeMessage(27, histogramBucket2);
                    }
                    i25++;
                }
            }
            HistogramBucket[] histogramBucketArr5 = this.histogramAttachDurationMs;
            if (histogramBucketArr5 != null && histogramBucketArr5.length > 0) {
                int i26 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr6 = this.histogramAttachDurationMs;
                    if (i26 >= histogramBucketArr6.length) {
                        break;
                    }
                    HistogramBucket histogramBucket3 = histogramBucketArr6[i26];
                    if (histogramBucket3 != null) {
                        codedOutputByteBufferNano.writeMessage(28, histogramBucket3);
                    }
                    i26++;
                }
            }
            HistogramBucket[] histogramBucketArr7 = this.histogramPublishSessionDurationMs;
            if (histogramBucketArr7 != null && histogramBucketArr7.length > 0) {
                int i27 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr8 = this.histogramPublishSessionDurationMs;
                    if (i27 >= histogramBucketArr8.length) {
                        break;
                    }
                    HistogramBucket histogramBucket4 = histogramBucketArr8[i27];
                    if (histogramBucket4 != null) {
                        codedOutputByteBufferNano.writeMessage(29, histogramBucket4);
                    }
                    i27++;
                }
            }
            HistogramBucket[] histogramBucketArr9 = this.histogramSubscribeSessionDurationMs;
            if (histogramBucketArr9 != null && histogramBucketArr9.length > 0) {
                int i28 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr10 = this.histogramSubscribeSessionDurationMs;
                    if (i28 >= histogramBucketArr10.length) {
                        break;
                    }
                    HistogramBucket histogramBucket5 = histogramBucketArr10[i28];
                    if (histogramBucket5 != null) {
                        codedOutputByteBufferNano.writeMessage(30, histogramBucket5);
                    }
                    i28++;
                }
            }
            HistogramBucket[] histogramBucketArr11 = this.histogramNdpSessionDurationMs;
            if (histogramBucketArr11 != null && histogramBucketArr11.length > 0) {
                int i29 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr12 = this.histogramNdpSessionDurationMs;
                    if (i29 >= histogramBucketArr12.length) {
                        break;
                    }
                    HistogramBucket histogramBucket6 = histogramBucketArr12[i29];
                    if (histogramBucket6 != null) {
                        codedOutputByteBufferNano.writeMessage(31, histogramBucket6);
                    }
                    i29++;
                }
            }
            HistogramBucket[] histogramBucketArr13 = this.histogramNdpSessionDataUsageMb;
            if (histogramBucketArr13 != null && histogramBucketArr13.length > 0) {
                int i30 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr14 = this.histogramNdpSessionDataUsageMb;
                    if (i30 >= histogramBucketArr14.length) {
                        break;
                    }
                    HistogramBucket histogramBucket7 = histogramBucketArr14[i30];
                    if (histogramBucket7 != null) {
                        codedOutputByteBufferNano.writeMessage(32, histogramBucket7);
                    }
                    i30++;
                }
            }
            HistogramBucket[] histogramBucketArr15 = this.histogramNdpCreationTimeMs;
            if (histogramBucketArr15 != null && histogramBucketArr15.length > 0) {
                int i31 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr16 = this.histogramNdpCreationTimeMs;
                    if (i31 >= histogramBucketArr16.length) {
                        break;
                    }
                    HistogramBucket histogramBucket8 = histogramBucketArr16[i31];
                    if (histogramBucket8 != null) {
                        codedOutputByteBufferNano.writeMessage(33, histogramBucket8);
                    }
                    i31++;
                }
            }
            long j = this.ndpCreationTimeMsMin;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(34, j);
            }
            long j2 = this.ndpCreationTimeMsMax;
            if (j2 != 0) {
                codedOutputByteBufferNano.writeInt64(35, j2);
            }
            long j3 = this.ndpCreationTimeMsSum;
            if (j3 != 0) {
                codedOutputByteBufferNano.writeInt64(36, j3);
            }
            long j4 = this.ndpCreationTimeMsSumOfSq;
            if (j4 != 0) {
                codedOutputByteBufferNano.writeInt64(37, j4);
            }
            long j5 = this.ndpCreationTimeMsNumSamples;
            if (j5 != 0) {
                codedOutputByteBufferNano.writeInt64(38, j5);
            }
            long j6 = this.availableTimeMs;
            if (j6 != 0) {
                codedOutputByteBufferNano.writeInt64(39, j6);
            }
            long j7 = this.enabledTimeMs;
            if (j7 != 0) {
                codedOutputByteBufferNano.writeInt64(40, j7);
            }
            int i32 = this.maxConcurrentPublishWithRangingInApp;
            if (i32 != 0) {
                codedOutputByteBufferNano.writeInt32(41, i32);
            }
            int i33 = this.maxConcurrentSubscribeWithRangingInApp;
            if (i33 != 0) {
                codedOutputByteBufferNano.writeInt32(42, i33);
            }
            int i34 = this.maxConcurrentPublishWithRangingInSystem;
            if (i34 != 0) {
                codedOutputByteBufferNano.writeInt32(43, i34);
            }
            int i35 = this.maxConcurrentSubscribeWithRangingInSystem;
            if (i35 != 0) {
                codedOutputByteBufferNano.writeInt32(44, i35);
            }
            HistogramBucket[] histogramBucketArr17 = this.histogramSubscribeGeofenceMin;
            if (histogramBucketArr17 != null && histogramBucketArr17.length > 0) {
                int i36 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr18 = this.histogramSubscribeGeofenceMin;
                    if (i36 >= histogramBucketArr18.length) {
                        break;
                    }
                    HistogramBucket histogramBucket9 = histogramBucketArr18[i36];
                    if (histogramBucket9 != null) {
                        codedOutputByteBufferNano.writeMessage(45, histogramBucket9);
                    }
                    i36++;
                }
            }
            HistogramBucket[] histogramBucketArr19 = this.histogramSubscribeGeofenceMax;
            if (histogramBucketArr19 != null && histogramBucketArr19.length > 0) {
                while (true) {
                    HistogramBucket[] histogramBucketArr20 = this.histogramSubscribeGeofenceMax;
                    if (i4 >= histogramBucketArr20.length) {
                        break;
                    }
                    HistogramBucket histogramBucket10 = histogramBucketArr20[i4];
                    if (histogramBucket10 != null) {
                        codedOutputByteBufferNano.writeMessage(46, histogramBucket10);
                    }
                    i4++;
                }
            }
            int i37 = this.numSubscribesWithRanging;
            if (i37 != 0) {
                codedOutputByteBufferNano.writeInt32(47, i37);
            }
            int i38 = this.numMatchesWithRanging;
            if (i38 != 0) {
                codedOutputByteBufferNano.writeInt32(48, i38);
            }
            int i39 = this.numMatchesWithoutRangingForRangingEnabledSubscribes;
            if (i39 != 0) {
                codedOutputByteBufferNano.writeInt32(49, i39);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numApps;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.numAppsUsingIdentityCallback;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.maxConcurrentAttachSessionsInApp;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr = this.histogramAttachSessionStatus;
            int i4 = 0;
            if (nanStatusHistogramBucketArr != null && nanStatusHistogramBucketArr.length > 0) {
                int i5 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr2 = this.histogramAttachSessionStatus;
                    if (i5 >= nanStatusHistogramBucketArr2.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket = nanStatusHistogramBucketArr2[i5];
                    if (nanStatusHistogramBucket != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, nanStatusHistogramBucket);
                    }
                    i5++;
                }
            }
            int i6 = this.maxConcurrentPublishInApp;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i6);
            }
            int i7 = this.maxConcurrentSubscribeInApp;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i7);
            }
            int i8 = this.maxConcurrentDiscoverySessionsInApp;
            if (i8 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i8);
            }
            int i9 = this.maxConcurrentPublishInSystem;
            if (i9 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i9);
            }
            int i10 = this.maxConcurrentSubscribeInSystem;
            if (i10 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i10);
            }
            int i11 = this.maxConcurrentDiscoverySessionsInSystem;
            if (i11 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i11);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr3 = this.histogramPublishStatus;
            if (nanStatusHistogramBucketArr3 != null && nanStatusHistogramBucketArr3.length > 0) {
                int i12 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr4 = this.histogramPublishStatus;
                    if (i12 >= nanStatusHistogramBucketArr4.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket2 = nanStatusHistogramBucketArr4[i12];
                    if (nanStatusHistogramBucket2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, nanStatusHistogramBucket2);
                    }
                    i12++;
                }
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr5 = this.histogramSubscribeStatus;
            if (nanStatusHistogramBucketArr5 != null && nanStatusHistogramBucketArr5.length > 0) {
                int i13 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr6 = this.histogramSubscribeStatus;
                    if (i13 >= nanStatusHistogramBucketArr6.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket3 = nanStatusHistogramBucketArr6[i13];
                    if (nanStatusHistogramBucket3 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, nanStatusHistogramBucket3);
                    }
                    i13++;
                }
            }
            int i14 = this.numAppsWithDiscoverySessionFailureOutOfResources;
            if (i14 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i14);
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr7 = this.histogramRequestNdpStatus;
            if (nanStatusHistogramBucketArr7 != null && nanStatusHistogramBucketArr7.length > 0) {
                int i15 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr8 = this.histogramRequestNdpStatus;
                    if (i15 >= nanStatusHistogramBucketArr8.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket4 = nanStatusHistogramBucketArr8[i15];
                    if (nanStatusHistogramBucket4 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(14, nanStatusHistogramBucket4);
                    }
                    i15++;
                }
            }
            NanStatusHistogramBucket[] nanStatusHistogramBucketArr9 = this.histogramRequestNdpOobStatus;
            if (nanStatusHistogramBucketArr9 != null && nanStatusHistogramBucketArr9.length > 0) {
                int i16 = 0;
                while (true) {
                    NanStatusHistogramBucket[] nanStatusHistogramBucketArr10 = this.histogramRequestNdpOobStatus;
                    if (i16 >= nanStatusHistogramBucketArr10.length) {
                        break;
                    }
                    NanStatusHistogramBucket nanStatusHistogramBucket5 = nanStatusHistogramBucketArr10[i16];
                    if (nanStatusHistogramBucket5 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(15, nanStatusHistogramBucket5);
                    }
                    i16++;
                }
            }
            int i17 = this.maxConcurrentNdiInApp;
            if (i17 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(19, i17);
            }
            int i18 = this.maxConcurrentNdiInSystem;
            if (i18 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i18);
            }
            int i19 = this.maxConcurrentNdpInApp;
            if (i19 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(21, i19);
            }
            int i20 = this.maxConcurrentNdpInSystem;
            if (i20 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(22, i20);
            }
            int i21 = this.maxConcurrentSecureNdpInApp;
            if (i21 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(23, i21);
            }
            int i22 = this.maxConcurrentSecureNdpInSystem;
            if (i22 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(24, i22);
            }
            int i23 = this.maxConcurrentNdpPerNdi;
            if (i23 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(25, i23);
            }
            HistogramBucket[] histogramBucketArr = this.histogramAwareAvailableDurationMs;
            if (histogramBucketArr != null && histogramBucketArr.length > 0) {
                int i24 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr2 = this.histogramAwareAvailableDurationMs;
                    if (i24 >= histogramBucketArr2.length) {
                        break;
                    }
                    HistogramBucket histogramBucket = histogramBucketArr2[i24];
                    if (histogramBucket != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(26, histogramBucket);
                    }
                    i24++;
                }
            }
            HistogramBucket[] histogramBucketArr3 = this.histogramAwareEnabledDurationMs;
            if (histogramBucketArr3 != null && histogramBucketArr3.length > 0) {
                int i25 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr4 = this.histogramAwareEnabledDurationMs;
                    if (i25 >= histogramBucketArr4.length) {
                        break;
                    }
                    HistogramBucket histogramBucket2 = histogramBucketArr4[i25];
                    if (histogramBucket2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(27, histogramBucket2);
                    }
                    i25++;
                }
            }
            HistogramBucket[] histogramBucketArr5 = this.histogramAttachDurationMs;
            if (histogramBucketArr5 != null && histogramBucketArr5.length > 0) {
                int i26 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr6 = this.histogramAttachDurationMs;
                    if (i26 >= histogramBucketArr6.length) {
                        break;
                    }
                    HistogramBucket histogramBucket3 = histogramBucketArr6[i26];
                    if (histogramBucket3 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(28, histogramBucket3);
                    }
                    i26++;
                }
            }
            HistogramBucket[] histogramBucketArr7 = this.histogramPublishSessionDurationMs;
            if (histogramBucketArr7 != null && histogramBucketArr7.length > 0) {
                int i27 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr8 = this.histogramPublishSessionDurationMs;
                    if (i27 >= histogramBucketArr8.length) {
                        break;
                    }
                    HistogramBucket histogramBucket4 = histogramBucketArr8[i27];
                    if (histogramBucket4 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(29, histogramBucket4);
                    }
                    i27++;
                }
            }
            HistogramBucket[] histogramBucketArr9 = this.histogramSubscribeSessionDurationMs;
            if (histogramBucketArr9 != null && histogramBucketArr9.length > 0) {
                int i28 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr10 = this.histogramSubscribeSessionDurationMs;
                    if (i28 >= histogramBucketArr10.length) {
                        break;
                    }
                    HistogramBucket histogramBucket5 = histogramBucketArr10[i28];
                    if (histogramBucket5 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(30, histogramBucket5);
                    }
                    i28++;
                }
            }
            HistogramBucket[] histogramBucketArr11 = this.histogramNdpSessionDurationMs;
            if (histogramBucketArr11 != null && histogramBucketArr11.length > 0) {
                int i29 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr12 = this.histogramNdpSessionDurationMs;
                    if (i29 >= histogramBucketArr12.length) {
                        break;
                    }
                    HistogramBucket histogramBucket6 = histogramBucketArr12[i29];
                    if (histogramBucket6 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(31, histogramBucket6);
                    }
                    i29++;
                }
            }
            HistogramBucket[] histogramBucketArr13 = this.histogramNdpSessionDataUsageMb;
            if (histogramBucketArr13 != null && histogramBucketArr13.length > 0) {
                int i30 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr14 = this.histogramNdpSessionDataUsageMb;
                    if (i30 >= histogramBucketArr14.length) {
                        break;
                    }
                    HistogramBucket histogramBucket7 = histogramBucketArr14[i30];
                    if (histogramBucket7 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(32, histogramBucket7);
                    }
                    i30++;
                }
            }
            HistogramBucket[] histogramBucketArr15 = this.histogramNdpCreationTimeMs;
            if (histogramBucketArr15 != null && histogramBucketArr15.length > 0) {
                int i31 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr16 = this.histogramNdpCreationTimeMs;
                    if (i31 >= histogramBucketArr16.length) {
                        break;
                    }
                    HistogramBucket histogramBucket8 = histogramBucketArr16[i31];
                    if (histogramBucket8 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(33, histogramBucket8);
                    }
                    i31++;
                }
            }
            long j = this.ndpCreationTimeMsMin;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(34, j);
            }
            long j2 = this.ndpCreationTimeMsMax;
            if (j2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(35, j2);
            }
            long j3 = this.ndpCreationTimeMsSum;
            if (j3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(36, j3);
            }
            long j4 = this.ndpCreationTimeMsSumOfSq;
            if (j4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(37, j4);
            }
            long j5 = this.ndpCreationTimeMsNumSamples;
            if (j5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(38, j5);
            }
            long j6 = this.availableTimeMs;
            if (j6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(39, j6);
            }
            long j7 = this.enabledTimeMs;
            if (j7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(40, j7);
            }
            int i32 = this.maxConcurrentPublishWithRangingInApp;
            if (i32 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(41, i32);
            }
            int i33 = this.maxConcurrentSubscribeWithRangingInApp;
            if (i33 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(42, i33);
            }
            int i34 = this.maxConcurrentPublishWithRangingInSystem;
            if (i34 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(43, i34);
            }
            int i35 = this.maxConcurrentSubscribeWithRangingInSystem;
            if (i35 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(44, i35);
            }
            HistogramBucket[] histogramBucketArr17 = this.histogramSubscribeGeofenceMin;
            if (histogramBucketArr17 != null && histogramBucketArr17.length > 0) {
                int i36 = 0;
                while (true) {
                    HistogramBucket[] histogramBucketArr18 = this.histogramSubscribeGeofenceMin;
                    if (i36 >= histogramBucketArr18.length) {
                        break;
                    }
                    HistogramBucket histogramBucket9 = histogramBucketArr18[i36];
                    if (histogramBucket9 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(45, histogramBucket9);
                    }
                    i36++;
                }
            }
            HistogramBucket[] histogramBucketArr19 = this.histogramSubscribeGeofenceMax;
            if (histogramBucketArr19 != null && histogramBucketArr19.length > 0) {
                while (true) {
                    HistogramBucket[] histogramBucketArr20 = this.histogramSubscribeGeofenceMax;
                    if (i4 >= histogramBucketArr20.length) {
                        break;
                    }
                    HistogramBucket histogramBucket10 = histogramBucketArr20[i4];
                    if (histogramBucket10 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(46, histogramBucket10);
                    }
                    i4++;
                }
            }
            int i37 = this.numSubscribesWithRanging;
            if (i37 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(47, i37);
            }
            int i38 = this.numMatchesWithRanging;
            if (i38 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(48, i38);
            }
            int i39 = this.numMatchesWithoutRangingForRangingEnabledSubscribes;
            return i39 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(49, i39) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiAwareLog mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.numApps = codedInputByteBufferNano.readInt32();
                        break;
                    case 16:
                        this.numAppsUsingIdentityCallback = codedInputByteBufferNano.readInt32();
                        break;
                    case 24:
                        this.maxConcurrentAttachSessionsInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 34:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr = this.histogramAttachSessionStatus;
                        int length = nanStatusHistogramBucketArr == null ? 0 : nanStatusHistogramBucketArr.length;
                        int i = repeatedFieldArrayLength + length;
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr2 = new NanStatusHistogramBucket[i];
                        if (length != 0) {
                            System.arraycopy(this.histogramAttachSessionStatus, 0, nanStatusHistogramBucketArr2, 0, length);
                        }
                        while (length < i - 1) {
                            nanStatusHistogramBucketArr2[length] = new NanStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr2[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        nanStatusHistogramBucketArr2[length] = new NanStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr2[length]);
                        this.histogramAttachSessionStatus = nanStatusHistogramBucketArr2;
                        break;
                    case 40:
                        this.maxConcurrentPublishInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 48:
                        this.maxConcurrentSubscribeInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 56:
                        this.maxConcurrentDiscoverySessionsInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 64:
                        this.maxConcurrentPublishInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 72:
                        this.maxConcurrentSubscribeInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 80:
                        this.maxConcurrentDiscoverySessionsInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 90:
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 90);
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr3 = this.histogramPublishStatus;
                        int length2 = nanStatusHistogramBucketArr3 == null ? 0 : nanStatusHistogramBucketArr3.length;
                        int i2 = repeatedFieldArrayLength2 + length2;
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr4 = new NanStatusHistogramBucket[i2];
                        if (length2 != 0) {
                            System.arraycopy(this.histogramPublishStatus, 0, nanStatusHistogramBucketArr4, 0, length2);
                        }
                        while (length2 < i2 - 1) {
                            nanStatusHistogramBucketArr4[length2] = new NanStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr4[length2]);
                            codedInputByteBufferNano.readTag();
                            length2++;
                        }
                        nanStatusHistogramBucketArr4[length2] = new NanStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr4[length2]);
                        this.histogramPublishStatus = nanStatusHistogramBucketArr4;
                        break;
                    case 98:
                        int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr5 = this.histogramSubscribeStatus;
                        int length3 = nanStatusHistogramBucketArr5 == null ? 0 : nanStatusHistogramBucketArr5.length;
                        int i3 = repeatedFieldArrayLength3 + length3;
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr6 = new NanStatusHistogramBucket[i3];
                        if (length3 != 0) {
                            System.arraycopy(this.histogramSubscribeStatus, 0, nanStatusHistogramBucketArr6, 0, length3);
                        }
                        while (length3 < i3 - 1) {
                            nanStatusHistogramBucketArr6[length3] = new NanStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr6[length3]);
                            codedInputByteBufferNano.readTag();
                            length3++;
                        }
                        nanStatusHistogramBucketArr6[length3] = new NanStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr6[length3]);
                        this.histogramSubscribeStatus = nanStatusHistogramBucketArr6;
                        break;
                    case 104:
                        this.numAppsWithDiscoverySessionFailureOutOfResources = codedInputByteBufferNano.readInt32();
                        break;
                    case 114:
                        int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 114);
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr7 = this.histogramRequestNdpStatus;
                        int length4 = nanStatusHistogramBucketArr7 == null ? 0 : nanStatusHistogramBucketArr7.length;
                        int i4 = repeatedFieldArrayLength4 + length4;
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr8 = new NanStatusHistogramBucket[i4];
                        if (length4 != 0) {
                            System.arraycopy(this.histogramRequestNdpStatus, 0, nanStatusHistogramBucketArr8, 0, length4);
                        }
                        while (length4 < i4 - 1) {
                            nanStatusHistogramBucketArr8[length4] = new NanStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr8[length4]);
                            codedInputByteBufferNano.readTag();
                            length4++;
                        }
                        nanStatusHistogramBucketArr8[length4] = new NanStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr8[length4]);
                        this.histogramRequestNdpStatus = nanStatusHistogramBucketArr8;
                        break;
                    case 122:
                        int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 122);
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr9 = this.histogramRequestNdpOobStatus;
                        int length5 = nanStatusHistogramBucketArr9 == null ? 0 : nanStatusHistogramBucketArr9.length;
                        int i5 = repeatedFieldArrayLength5 + length5;
                        NanStatusHistogramBucket[] nanStatusHistogramBucketArr10 = new NanStatusHistogramBucket[i5];
                        if (length5 != 0) {
                            System.arraycopy(this.histogramRequestNdpOobStatus, 0, nanStatusHistogramBucketArr10, 0, length5);
                        }
                        while (length5 < i5 - 1) {
                            nanStatusHistogramBucketArr10[length5] = new NanStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr10[length5]);
                            codedInputByteBufferNano.readTag();
                            length5++;
                        }
                        nanStatusHistogramBucketArr10[length5] = new NanStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(nanStatusHistogramBucketArr10[length5]);
                        this.histogramRequestNdpOobStatus = nanStatusHistogramBucketArr10;
                        break;
                    case 152:
                        this.maxConcurrentNdiInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 160:
                        this.maxConcurrentNdiInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 168:
                        this.maxConcurrentNdpInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 176:
                        this.maxConcurrentNdpInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 184:
                        this.maxConcurrentSecureNdpInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 192:
                        this.maxConcurrentSecureNdpInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 200:
                        this.maxConcurrentNdpPerNdi = codedInputByteBufferNano.readInt32();
                        break;
                    case 210:
                        int repeatedFieldArrayLength6 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 210);
                        HistogramBucket[] histogramBucketArr = this.histogramAwareAvailableDurationMs;
                        int length6 = histogramBucketArr == null ? 0 : histogramBucketArr.length;
                        int i6 = repeatedFieldArrayLength6 + length6;
                        HistogramBucket[] histogramBucketArr2 = new HistogramBucket[i6];
                        if (length6 != 0) {
                            System.arraycopy(this.histogramAwareAvailableDurationMs, 0, histogramBucketArr2, 0, length6);
                        }
                        while (length6 < i6 - 1) {
                            histogramBucketArr2[length6] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr2[length6]);
                            codedInputByteBufferNano.readTag();
                            length6++;
                        }
                        histogramBucketArr2[length6] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr2[length6]);
                        this.histogramAwareAvailableDurationMs = histogramBucketArr2;
                        break;
                    case 218:
                        int repeatedFieldArrayLength7 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 218);
                        HistogramBucket[] histogramBucketArr3 = this.histogramAwareEnabledDurationMs;
                        int length7 = histogramBucketArr3 == null ? 0 : histogramBucketArr3.length;
                        int i7 = repeatedFieldArrayLength7 + length7;
                        HistogramBucket[] histogramBucketArr4 = new HistogramBucket[i7];
                        if (length7 != 0) {
                            System.arraycopy(this.histogramAwareEnabledDurationMs, 0, histogramBucketArr4, 0, length7);
                        }
                        while (length7 < i7 - 1) {
                            histogramBucketArr4[length7] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr4[length7]);
                            codedInputByteBufferNano.readTag();
                            length7++;
                        }
                        histogramBucketArr4[length7] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr4[length7]);
                        this.histogramAwareEnabledDurationMs = histogramBucketArr4;
                        break;
                    case 226:
                        int repeatedFieldArrayLength8 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 226);
                        HistogramBucket[] histogramBucketArr5 = this.histogramAttachDurationMs;
                        int length8 = histogramBucketArr5 == null ? 0 : histogramBucketArr5.length;
                        int i8 = repeatedFieldArrayLength8 + length8;
                        HistogramBucket[] histogramBucketArr6 = new HistogramBucket[i8];
                        if (length8 != 0) {
                            System.arraycopy(this.histogramAttachDurationMs, 0, histogramBucketArr6, 0, length8);
                        }
                        while (length8 < i8 - 1) {
                            histogramBucketArr6[length8] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr6[length8]);
                            codedInputByteBufferNano.readTag();
                            length8++;
                        }
                        histogramBucketArr6[length8] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr6[length8]);
                        this.histogramAttachDurationMs = histogramBucketArr6;
                        break;
                    case 234:
                        int repeatedFieldArrayLength9 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 234);
                        HistogramBucket[] histogramBucketArr7 = this.histogramPublishSessionDurationMs;
                        int length9 = histogramBucketArr7 == null ? 0 : histogramBucketArr7.length;
                        int i9 = repeatedFieldArrayLength9 + length9;
                        HistogramBucket[] histogramBucketArr8 = new HistogramBucket[i9];
                        if (length9 != 0) {
                            System.arraycopy(this.histogramPublishSessionDurationMs, 0, histogramBucketArr8, 0, length9);
                        }
                        while (length9 < i9 - 1) {
                            histogramBucketArr8[length9] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr8[length9]);
                            codedInputByteBufferNano.readTag();
                            length9++;
                        }
                        histogramBucketArr8[length9] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr8[length9]);
                        this.histogramPublishSessionDurationMs = histogramBucketArr8;
                        break;
                    case 242:
                        int repeatedFieldArrayLength10 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 242);
                        HistogramBucket[] histogramBucketArr9 = this.histogramSubscribeSessionDurationMs;
                        int length10 = histogramBucketArr9 == null ? 0 : histogramBucketArr9.length;
                        int i10 = repeatedFieldArrayLength10 + length10;
                        HistogramBucket[] histogramBucketArr10 = new HistogramBucket[i10];
                        if (length10 != 0) {
                            System.arraycopy(this.histogramSubscribeSessionDurationMs, 0, histogramBucketArr10, 0, length10);
                        }
                        while (length10 < i10 - 1) {
                            histogramBucketArr10[length10] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr10[length10]);
                            codedInputByteBufferNano.readTag();
                            length10++;
                        }
                        histogramBucketArr10[length10] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr10[length10]);
                        this.histogramSubscribeSessionDurationMs = histogramBucketArr10;
                        break;
                    case 250:
                        int repeatedFieldArrayLength11 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 250);
                        HistogramBucket[] histogramBucketArr11 = this.histogramNdpSessionDurationMs;
                        int length11 = histogramBucketArr11 == null ? 0 : histogramBucketArr11.length;
                        int i11 = repeatedFieldArrayLength11 + length11;
                        HistogramBucket[] histogramBucketArr12 = new HistogramBucket[i11];
                        if (length11 != 0) {
                            System.arraycopy(this.histogramNdpSessionDurationMs, 0, histogramBucketArr12, 0, length11);
                        }
                        while (length11 < i11 - 1) {
                            histogramBucketArr12[length11] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr12[length11]);
                            codedInputByteBufferNano.readTag();
                            length11++;
                        }
                        histogramBucketArr12[length11] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr12[length11]);
                        this.histogramNdpSessionDurationMs = histogramBucketArr12;
                        break;
                    case 258:
                        int repeatedFieldArrayLength12 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 258);
                        HistogramBucket[] histogramBucketArr13 = this.histogramNdpSessionDataUsageMb;
                        int length12 = histogramBucketArr13 == null ? 0 : histogramBucketArr13.length;
                        int i12 = repeatedFieldArrayLength12 + length12;
                        HistogramBucket[] histogramBucketArr14 = new HistogramBucket[i12];
                        if (length12 != 0) {
                            System.arraycopy(this.histogramNdpSessionDataUsageMb, 0, histogramBucketArr14, 0, length12);
                        }
                        while (length12 < i12 - 1) {
                            histogramBucketArr14[length12] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr14[length12]);
                            codedInputByteBufferNano.readTag();
                            length12++;
                        }
                        histogramBucketArr14[length12] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr14[length12]);
                        this.histogramNdpSessionDataUsageMb = histogramBucketArr14;
                        break;
                    case 266:
                        int repeatedFieldArrayLength13 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 266);
                        HistogramBucket[] histogramBucketArr15 = this.histogramNdpCreationTimeMs;
                        int length13 = histogramBucketArr15 == null ? 0 : histogramBucketArr15.length;
                        int i13 = repeatedFieldArrayLength13 + length13;
                        HistogramBucket[] histogramBucketArr16 = new HistogramBucket[i13];
                        if (length13 != 0) {
                            System.arraycopy(this.histogramNdpCreationTimeMs, 0, histogramBucketArr16, 0, length13);
                        }
                        while (length13 < i13 - 1) {
                            histogramBucketArr16[length13] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr16[length13]);
                            codedInputByteBufferNano.readTag();
                            length13++;
                        }
                        histogramBucketArr16[length13] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr16[length13]);
                        this.histogramNdpCreationTimeMs = histogramBucketArr16;
                        break;
                    case 272:
                        this.ndpCreationTimeMsMin = codedInputByteBufferNano.readInt64();
                        break;
                    case 280:
                        this.ndpCreationTimeMsMax = codedInputByteBufferNano.readInt64();
                        break;
                    case 288:
                        this.ndpCreationTimeMsSum = codedInputByteBufferNano.readInt64();
                        break;
                    case 296:
                        this.ndpCreationTimeMsSumOfSq = codedInputByteBufferNano.readInt64();
                        break;
                    case 304:
                        this.ndpCreationTimeMsNumSamples = codedInputByteBufferNano.readInt64();
                        break;
                    case 312:
                        this.availableTimeMs = codedInputByteBufferNano.readInt64();
                        break;
                    case 320:
                        this.enabledTimeMs = codedInputByteBufferNano.readInt64();
                        break;
                    case 328:
                        this.maxConcurrentPublishWithRangingInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case 336:
                        this.maxConcurrentSubscribeWithRangingInApp = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.USER_LOCALE_LIST /* 344 */:
                        this.maxConcurrentPublishWithRangingInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 352:
                        this.maxConcurrentSubscribeWithRangingInSystem = codedInputByteBufferNano.readInt32();
                        break;
                    case 362:
                        int repeatedFieldArrayLength14 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 362);
                        HistogramBucket[] histogramBucketArr17 = this.histogramSubscribeGeofenceMin;
                        int length14 = histogramBucketArr17 == null ? 0 : histogramBucketArr17.length;
                        int i14 = repeatedFieldArrayLength14 + length14;
                        HistogramBucket[] histogramBucketArr18 = new HistogramBucket[i14];
                        if (length14 != 0) {
                            System.arraycopy(this.histogramSubscribeGeofenceMin, 0, histogramBucketArr18, 0, length14);
                        }
                        while (length14 < i14 - 1) {
                            histogramBucketArr18[length14] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr18[length14]);
                            codedInputByteBufferNano.readTag();
                            length14++;
                        }
                        histogramBucketArr18[length14] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr18[length14]);
                        this.histogramSubscribeGeofenceMin = histogramBucketArr18;
                        break;
                    case MetricsProto.MetricsEvent.SUW_ACCESSIBILITY_DISPLAY_SIZE /* 370 */:
                        int repeatedFieldArrayLength15 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, MetricsProto.MetricsEvent.SUW_ACCESSIBILITY_DISPLAY_SIZE);
                        HistogramBucket[] histogramBucketArr19 = this.histogramSubscribeGeofenceMax;
                        int length15 = histogramBucketArr19 == null ? 0 : histogramBucketArr19.length;
                        int i15 = repeatedFieldArrayLength15 + length15;
                        HistogramBucket[] histogramBucketArr20 = new HistogramBucket[i15];
                        if (length15 != 0) {
                            System.arraycopy(this.histogramSubscribeGeofenceMax, 0, histogramBucketArr20, 0, length15);
                        }
                        while (length15 < i15 - 1) {
                            histogramBucketArr20[length15] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr20[length15]);
                            codedInputByteBufferNano.readTag();
                            length15++;
                        }
                        histogramBucketArr20[length15] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr20[length15]);
                        this.histogramSubscribeGeofenceMax = histogramBucketArr20;
                        break;
                    case MetricsProto.MetricsEvent.ACTION_SETTINGS_CONDITION_BUTTON /* 376 */:
                        this.numSubscribesWithRanging = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.ACTION_SHOW_SETTINGS_SUGGESTION /* 384 */:
                        this.numMatchesWithRanging = codedInputByteBufferNano.readInt32();
                        break;
                    case MetricsProto.MetricsEvent.TUNER_POWER_NOTIFICATION_CONTROLS /* 392 */:
                        this.numMatchesWithoutRangingForRangingEnabledSubscribes = codedInputByteBufferNano.readInt32();
                        break;
                    default:
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }

        public static WifiAwareLog parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiAwareLog) MessageNano.mergeFrom(new WifiAwareLog(), bArr);
        }

        public static WifiAwareLog parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiAwareLog().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class NumConnectableNetworksBucket extends MessageNano {
        private static volatile NumConnectableNetworksBucket[] _emptyArray;
        public int count;
        public int numConnectableNetworks;

        public static NumConnectableNetworksBucket[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NumConnectableNetworksBucket[0];
                    }
                }
            }
            return _emptyArray;
        }

        public NumConnectableNetworksBucket() {
            clear();
        }

        public NumConnectableNetworksBucket clear() {
            this.numConnectableNetworks = 0;
            this.count = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numConnectableNetworks;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.count;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numConnectableNetworks;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.count;
            return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public NumConnectableNetworksBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.numConnectableNetworks = codedInputByteBufferNano.readInt32();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.count = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static NumConnectableNetworksBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (NumConnectableNetworksBucket) MessageNano.mergeFrom(new NumConnectableNetworksBucket(), bArr);
        }

        public static NumConnectableNetworksBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new NumConnectableNetworksBucket().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class PnoScanMetrics extends MessageNano {
        private static volatile PnoScanMetrics[] _emptyArray;
        public int numPnoFoundNetworkEvents;
        public int numPnoScanAttempts;
        public int numPnoScanFailed;
        public int numPnoScanFailedOverOffload;
        public int numPnoScanStartedOverOffload;

        public static PnoScanMetrics[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PnoScanMetrics[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PnoScanMetrics() {
            clear();
        }

        public PnoScanMetrics clear() {
            this.numPnoScanAttempts = 0;
            this.numPnoScanFailed = 0;
            this.numPnoScanStartedOverOffload = 0;
            this.numPnoScanFailedOverOffload = 0;
            this.numPnoFoundNetworkEvents = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numPnoScanAttempts;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.numPnoScanFailed;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.numPnoScanStartedOverOffload;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            int i4 = this.numPnoScanFailedOverOffload;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i4);
            }
            int i5 = this.numPnoFoundNetworkEvents;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i5);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numPnoScanAttempts;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.numPnoScanFailed;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.numPnoScanStartedOverOffload;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.numPnoScanFailedOverOffload;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.numPnoFoundNetworkEvents;
            return i5 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(5, i5) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public PnoScanMetrics mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.numPnoScanAttempts = codedInputByteBufferNano.readInt32();
                } else if (tag == 16) {
                    this.numPnoScanFailed = codedInputByteBufferNano.readInt32();
                } else if (tag == 24) {
                    this.numPnoScanStartedOverOffload = codedInputByteBufferNano.readInt32();
                } else if (tag == 32) {
                    this.numPnoScanFailedOverOffload = codedInputByteBufferNano.readInt32();
                } else if (tag != 40) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.numPnoFoundNetworkEvents = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static PnoScanMetrics parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (PnoScanMetrics) MessageNano.mergeFrom(new PnoScanMetrics(), bArr);
        }

        public static PnoScanMetrics parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new PnoScanMetrics().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class ConnectToNetworkNotificationAndActionCount extends MessageNano {
        public static final int ACTION_CONNECT_TO_NETWORK = 2;
        public static final int ACTION_PICK_WIFI_NETWORK = 3;
        public static final int ACTION_PICK_WIFI_NETWORK_AFTER_CONNECT_FAILURE = 4;
        public static final int ACTION_UNKNOWN = 0;
        public static final int ACTION_USER_DISMISSED_NOTIFICATION = 1;
        public static final int NOTIFICATION_CONNECTED_TO_NETWORK = 3;
        public static final int NOTIFICATION_CONNECTING_TO_NETWORK = 2;
        public static final int NOTIFICATION_FAILED_TO_CONNECT = 4;
        public static final int NOTIFICATION_RECOMMEND_NETWORK = 1;
        public static final int NOTIFICATION_UNKNOWN = 0;
        public static final int RECOMMENDER_OPEN = 1;
        public static final int RECOMMENDER_UNKNOWN = 0;
        private static volatile ConnectToNetworkNotificationAndActionCount[] _emptyArray;
        public int action;
        public int count;
        public int notification;
        public int recommender;

        public static ConnectToNetworkNotificationAndActionCount[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ConnectToNetworkNotificationAndActionCount[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ConnectToNetworkNotificationAndActionCount() {
            clear();
        }

        public ConnectToNetworkNotificationAndActionCount clear() {
            this.notification = 0;
            this.action = 0;
            this.recommender = 0;
            this.count = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.notification;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.action;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.recommender;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            int i4 = this.count;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i4);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.notification;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.action;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.recommender;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.count;
            return i4 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, i4) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public ConnectToNetworkNotificationAndActionCount mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3 || int32 == 4) {
                        this.notification = int32;
                    }
                } else if (tag == 16) {
                    int int33 = codedInputByteBufferNano.readInt32();
                    if (int33 == 0 || int33 == 1 || int33 == 2 || int33 == 3 || int33 == 4) {
                        this.action = int33;
                    }
                } else if (tag == 24) {
                    int int34 = codedInputByteBufferNano.readInt32();
                    if (int34 == 0 || int34 == 1) {
                        this.recommender = int34;
                    }
                } else if (tag != 32) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.count = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static ConnectToNetworkNotificationAndActionCount parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (ConnectToNetworkNotificationAndActionCount) MessageNano.mergeFrom(new ConnectToNetworkNotificationAndActionCount(), bArr);
        }

        public static ConnectToNetworkNotificationAndActionCount parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new ConnectToNetworkNotificationAndActionCount().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class SoftApConnectedClientsEvent extends MessageNano {
        public static final int BANDWIDTH_160 = 6;
        public static final int BANDWIDTH_20 = 2;
        public static final int BANDWIDTH_20_NOHT = 1;
        public static final int BANDWIDTH_40 = 3;
        public static final int BANDWIDTH_80 = 4;
        public static final int BANDWIDTH_80P80 = 5;
        public static final int BANDWIDTH_INVALID = 0;
        public static final int NUM_CLIENTS_CHANGED = 2;
        public static final int SOFT_AP_DOWN = 1;
        public static final int SOFT_AP_UP = 0;
        private static volatile SoftApConnectedClientsEvent[] _emptyArray;
        public int channelBandwidth;
        public int channelFrequency;
        public int eventType;
        public int numConnectedClients;
        public long timeStampMillis;

        public static SoftApConnectedClientsEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SoftApConnectedClientsEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SoftApConnectedClientsEvent() {
            clear();
        }

        public SoftApConnectedClientsEvent clear() {
            this.eventType = 0;
            this.timeStampMillis = 0L;
            this.numConnectedClients = 0;
            this.channelFrequency = 0;
            this.channelBandwidth = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.eventType;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            long j = this.timeStampMillis;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(2, j);
            }
            int i2 = this.numConnectedClients;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i2);
            }
            int i3 = this.channelFrequency;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i3);
            }
            int i4 = this.channelBandwidth;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i4);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.eventType;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            long j = this.timeStampMillis;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, j);
            }
            int i2 = this.numConnectedClients;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            int i3 = this.channelFrequency;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i3);
            }
            int i4 = this.channelBandwidth;
            return i4 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(5, i4) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public SoftApConnectedClientsEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int int32 = codedInputByteBufferNano.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2) {
                        this.eventType = int32;
                    }
                } else if (tag == 16) {
                    this.timeStampMillis = codedInputByteBufferNano.readInt64();
                } else if (tag == 24) {
                    this.numConnectedClients = codedInputByteBufferNano.readInt32();
                } else if (tag == 32) {
                    this.channelFrequency = codedInputByteBufferNano.readInt32();
                } else if (tag != 40) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    int int33 = codedInputByteBufferNano.readInt32();
                    switch (int33) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.channelBandwidth = int33;
                            break;
                    }
                }
            }
        }

        public static SoftApConnectedClientsEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (SoftApConnectedClientsEvent) MessageNano.mergeFrom(new SoftApConnectedClientsEvent(), bArr);
        }

        public static SoftApConnectedClientsEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new SoftApConnectedClientsEvent().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WpsMetrics extends MessageNano {
        private static volatile WpsMetrics[] _emptyArray;
        public int numWpsAttempts;
        public int numWpsCancellation;
        public int numWpsOtherConnectionFailure;
        public int numWpsOverlapFailure;
        public int numWpsStartFailure;
        public int numWpsSuccess;
        public int numWpsSupplicantFailure;
        public int numWpsTimeoutFailure;

        public static WpsMetrics[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WpsMetrics[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WpsMetrics() {
            clear();
        }

        public WpsMetrics clear() {
            this.numWpsAttempts = 0;
            this.numWpsSuccess = 0;
            this.numWpsStartFailure = 0;
            this.numWpsOverlapFailure = 0;
            this.numWpsTimeoutFailure = 0;
            this.numWpsOtherConnectionFailure = 0;
            this.numWpsSupplicantFailure = 0;
            this.numWpsCancellation = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numWpsAttempts;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.numWpsSuccess;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            int i3 = this.numWpsStartFailure;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            int i4 = this.numWpsOverlapFailure;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i4);
            }
            int i5 = this.numWpsTimeoutFailure;
            if (i5 != 0) {
                codedOutputByteBufferNano.writeInt32(5, i5);
            }
            int i6 = this.numWpsOtherConnectionFailure;
            if (i6 != 0) {
                codedOutputByteBufferNano.writeInt32(6, i6);
            }
            int i7 = this.numWpsSupplicantFailure;
            if (i7 != 0) {
                codedOutputByteBufferNano.writeInt32(7, i7);
            }
            int i8 = this.numWpsCancellation;
            if (i8 != 0) {
                codedOutputByteBufferNano.writeInt32(8, i8);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numWpsAttempts;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.numWpsSuccess;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.numWpsStartFailure;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.numWpsOverlapFailure;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.numWpsTimeoutFailure;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
            }
            int i6 = this.numWpsOtherConnectionFailure;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
            }
            int i7 = this.numWpsSupplicantFailure;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i7);
            }
            int i8 = this.numWpsCancellation;
            return i8 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(8, i8) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WpsMetrics mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.numWpsAttempts = codedInputByteBufferNano.readInt32();
                } else if (tag == 16) {
                    this.numWpsSuccess = codedInputByteBufferNano.readInt32();
                } else if (tag == 24) {
                    this.numWpsStartFailure = codedInputByteBufferNano.readInt32();
                } else if (tag == 32) {
                    this.numWpsOverlapFailure = codedInputByteBufferNano.readInt32();
                } else if (tag == 40) {
                    this.numWpsTimeoutFailure = codedInputByteBufferNano.readInt32();
                } else if (tag == 48) {
                    this.numWpsOtherConnectionFailure = codedInputByteBufferNano.readInt32();
                } else if (tag == 56) {
                    this.numWpsSupplicantFailure = codedInputByteBufferNano.readInt32();
                } else if (tag != 64) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.numWpsCancellation = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static WpsMetrics parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WpsMetrics) MessageNano.mergeFrom(new WpsMetrics(), bArr);
        }

        public static WpsMetrics parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WpsMetrics().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WifiPowerStats extends MessageNano {
        private static volatile WifiPowerStats[] _emptyArray;
        public double energyConsumedMah;
        public long idleTimeMs;
        public long loggingDurationMs;
        public long rxTimeMs;
        public long txTimeMs;

        public static WifiPowerStats[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiPowerStats[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiPowerStats() {
            clear();
        }

        public WifiPowerStats clear() {
            this.loggingDurationMs = 0L;
            this.energyConsumedMah = 0.0d;
            this.idleTimeMs = 0L;
            this.rxTimeMs = 0L;
            this.txTimeMs = 0L;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            long j = this.loggingDurationMs;
            if (j != 0) {
                codedOutputByteBufferNano.writeInt64(1, j);
            }
            if (Double.doubleToLongBits(this.energyConsumedMah) != Double.doubleToLongBits(0.0d)) {
                codedOutputByteBufferNano.writeDouble(2, this.energyConsumedMah);
            }
            long j2 = this.idleTimeMs;
            if (j2 != 0) {
                codedOutputByteBufferNano.writeInt64(3, j2);
            }
            long j3 = this.rxTimeMs;
            if (j3 != 0) {
                codedOutputByteBufferNano.writeInt64(4, j3);
            }
            long j4 = this.txTimeMs;
            if (j4 != 0) {
                codedOutputByteBufferNano.writeInt64(5, j4);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            long j = this.loggingDurationMs;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
            }
            if (Double.doubleToLongBits(this.energyConsumedMah) != Double.doubleToLongBits(0.0d)) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeDoubleSize(2, this.energyConsumedMah);
            }
            long j2 = this.idleTimeMs;
            if (j2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, j2);
            }
            long j3 = this.rxTimeMs;
            if (j3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, j3);
            }
            long j4 = this.txTimeMs;
            return j4 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(5, j4) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiPowerStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.loggingDurationMs = codedInputByteBufferNano.readInt64();
                } else if (tag == 17) {
                    this.energyConsumedMah = codedInputByteBufferNano.readDouble();
                } else if (tag == 24) {
                    this.idleTimeMs = codedInputByteBufferNano.readInt64();
                } else if (tag == 32) {
                    this.rxTimeMs = codedInputByteBufferNano.readInt64();
                } else if (tag != 40) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.txTimeMs = codedInputByteBufferNano.readInt64();
                }
            }
        }

        public static WifiPowerStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiPowerStats) MessageNano.mergeFrom(new WifiPowerStats(), bArr);
        }

        public static WifiPowerStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiPowerStats().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WifiWakeStats extends MessageNano {
        private static volatile WifiWakeStats[] _emptyArray;
        public int numIgnoredStarts;
        public int numSessions;
        public int numWakeups;
        public Session[] sessions;

        public static final class Session extends MessageNano {
            private static volatile Session[] _emptyArray;
            public Event initializeEvent;
            public int lockedNetworksAtInitialize;
            public int lockedNetworksAtStart;
            public Event resetEvent;
            public long startTimeMillis;
            public Event unlockEvent;
            public Event wakeupEvent;

            public static final class Event extends MessageNano {
                private static volatile Event[] _emptyArray;
                public int elapsedScans;
                public long elapsedTimeMillis;

                public static Event[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Event[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public Event() {
                    clear();
                }

                public Event clear() {
                    this.elapsedTimeMillis = 0L;
                    this.elapsedScans = 0;
                    this.cachedSize = -1;
                    return this;
                }

                @Override // com.android.framework.protobuf.nano.MessageNano
                public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                    long j = this.elapsedTimeMillis;
                    if (j != 0) {
                        codedOutputByteBufferNano.writeInt64(1, j);
                    }
                    int i = this.elapsedScans;
                    if (i != 0) {
                        codedOutputByteBufferNano.writeInt32(2, i);
                    }
                    super.writeTo(codedOutputByteBufferNano);
                }

                @Override // com.android.framework.protobuf.nano.MessageNano
                protected int computeSerializedSize() {
                    int iComputeSerializedSize = super.computeSerializedSize();
                    long j = this.elapsedTimeMillis;
                    if (j != 0) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
                    }
                    int i = this.elapsedScans;
                    return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i) : iComputeSerializedSize;
                }

                @Override // com.android.framework.protobuf.nano.MessageNano
                public Event mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    while (true) {
                        int tag = codedInputByteBufferNano.readTag();
                        if (tag == 0) {
                            return this;
                        }
                        if (tag == 8) {
                            this.elapsedTimeMillis = codedInputByteBufferNano.readInt64();
                        } else if (tag != 16) {
                            if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                                return this;
                            }
                        } else {
                            this.elapsedScans = codedInputByteBufferNano.readInt32();
                        }
                    }
                }

                public static Event parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                    return (Event) MessageNano.mergeFrom(new Event(), bArr);
                }

                public static Event parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                    return new Event().mergeFrom(codedInputByteBufferNano);
                }
            }

            public static Session[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Session[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public Session() {
                clear();
            }

            public Session clear() {
                this.startTimeMillis = 0L;
                this.lockedNetworksAtStart = 0;
                this.lockedNetworksAtInitialize = 0;
                this.initializeEvent = null;
                this.unlockEvent = null;
                this.wakeupEvent = null;
                this.resetEvent = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                long j = this.startTimeMillis;
                if (j != 0) {
                    codedOutputByteBufferNano.writeInt64(1, j);
                }
                int i = this.lockedNetworksAtStart;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i);
                }
                Event event = this.unlockEvent;
                if (event != null) {
                    codedOutputByteBufferNano.writeMessage(3, event);
                }
                Event event2 = this.wakeupEvent;
                if (event2 != null) {
                    codedOutputByteBufferNano.writeMessage(4, event2);
                }
                Event event3 = this.resetEvent;
                if (event3 != null) {
                    codedOutputByteBufferNano.writeMessage(5, event3);
                }
                int i2 = this.lockedNetworksAtInitialize;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(6, i2);
                }
                Event event4 = this.initializeEvent;
                if (event4 != null) {
                    codedOutputByteBufferNano.writeMessage(7, event4);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                long j = this.startTimeMillis;
                if (j != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
                }
                int i = this.lockedNetworksAtStart;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i);
                }
                Event event = this.unlockEvent;
                if (event != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, event);
                }
                Event event2 = this.wakeupEvent;
                if (event2 != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, event2);
                }
                Event event3 = this.resetEvent;
                if (event3 != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, event3);
                }
                int i2 = this.lockedNetworksAtInitialize;
                if (i2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i2);
                }
                Event event4 = this.initializeEvent;
                return event4 != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(7, event4) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public Session mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        this.startTimeMillis = codedInputByteBufferNano.readInt64();
                    } else if (tag == 16) {
                        this.lockedNetworksAtStart = codedInputByteBufferNano.readInt32();
                    } else if (tag == 26) {
                        if (this.unlockEvent == null) {
                            this.unlockEvent = new Event();
                        }
                        codedInputByteBufferNano.readMessage(this.unlockEvent);
                    } else if (tag == 34) {
                        if (this.wakeupEvent == null) {
                            this.wakeupEvent = new Event();
                        }
                        codedInputByteBufferNano.readMessage(this.wakeupEvent);
                    } else if (tag == 42) {
                        if (this.resetEvent == null) {
                            this.resetEvent = new Event();
                        }
                        codedInputByteBufferNano.readMessage(this.resetEvent);
                    } else if (tag == 48) {
                        this.lockedNetworksAtInitialize = codedInputByteBufferNano.readInt32();
                    } else if (tag != 58) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        if (this.initializeEvent == null) {
                            this.initializeEvent = new Event();
                        }
                        codedInputByteBufferNano.readMessage(this.initializeEvent);
                    }
                }
            }

            public static Session parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (Session) MessageNano.mergeFrom(new Session(), bArr);
            }

            public static Session parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new Session().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static WifiWakeStats[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiWakeStats[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiWakeStats() {
            clear();
        }

        public WifiWakeStats clear() {
            this.numSessions = 0;
            this.sessions = Session.emptyArray();
            this.numIgnoredStarts = 0;
            this.numWakeups = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numSessions;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            Session[] sessionArr = this.sessions;
            if (sessionArr != null && sessionArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Session[] sessionArr2 = this.sessions;
                    if (i2 >= sessionArr2.length) {
                        break;
                    }
                    Session session = sessionArr2[i2];
                    if (session != null) {
                        codedOutputByteBufferNano.writeMessage(2, session);
                    }
                    i2++;
                }
            }
            int i3 = this.numIgnoredStarts;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i3);
            }
            int i4 = this.numWakeups;
            if (i4 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i4);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numSessions;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            Session[] sessionArr = this.sessions;
            if (sessionArr != null && sessionArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Session[] sessionArr2 = this.sessions;
                    if (i2 >= sessionArr2.length) {
                        break;
                    }
                    Session session = sessionArr2[i2];
                    if (session != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, session);
                    }
                    i2++;
                }
            }
            int i3 = this.numIgnoredStarts;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.numWakeups;
            return i4 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, i4) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiWakeStats mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.numSessions = codedInputByteBufferNano.readInt32();
                } else if (tag == 18) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    Session[] sessionArr = this.sessions;
                    int length = sessionArr == null ? 0 : sessionArr.length;
                    int i = repeatedFieldArrayLength + length;
                    Session[] sessionArr2 = new Session[i];
                    if (length != 0) {
                        System.arraycopy(this.sessions, 0, sessionArr2, 0, length);
                    }
                    while (length < i - 1) {
                        sessionArr2[length] = new Session();
                        codedInputByteBufferNano.readMessage(sessionArr2[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    sessionArr2[length] = new Session();
                    codedInputByteBufferNano.readMessage(sessionArr2[length]);
                    this.sessions = sessionArr2;
                } else if (tag == 24) {
                    this.numIgnoredStarts = codedInputByteBufferNano.readInt32();
                } else if (tag != 32) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    this.numWakeups = codedInputByteBufferNano.readInt32();
                }
            }
        }

        public static WifiWakeStats parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiWakeStats) MessageNano.mergeFrom(new WifiWakeStats(), bArr);
        }

        public static WifiWakeStats parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiWakeStats().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class WifiRttLog extends MessageNano {
        public static final int ABORTED = 9;
        public static final int FAILURE = 2;
        public static final int FAIL_AP_ON_DIFF_CHANNEL = 7;
        public static final int FAIL_BUSY_TRY_LATER = 13;
        public static final int FAIL_FTM_PARAM_OVERRIDE = 16;
        public static final int FAIL_INVALID_TS = 10;
        public static final int FAIL_NOT_SCHEDULED_YET = 5;
        public static final int FAIL_NO_CAPABILITY = 8;
        public static final int FAIL_NO_RSP = 3;
        public static final int FAIL_PROTOCOL = 11;
        public static final int FAIL_REJECTED = 4;
        public static final int FAIL_SCHEDULE = 12;
        public static final int FAIL_TM_TIMEOUT = 6;
        public static final int INVALID_REQ = 14;
        public static final int MISSING_RESULT = 17;
        public static final int NO_WIFI = 15;
        public static final int OVERALL_AWARE_TRANSLATION_FAILURE = 7;
        public static final int OVERALL_FAIL = 2;
        public static final int OVERALL_HAL_FAILURE = 6;
        public static final int OVERALL_LOCATION_PERMISSION_MISSING = 8;
        public static final int OVERALL_RTT_NOT_AVAILABLE = 3;
        public static final int OVERALL_SUCCESS = 1;
        public static final int OVERALL_THROTTLE = 5;
        public static final int OVERALL_TIMEOUT = 4;
        public static final int OVERALL_UNKNOWN = 0;
        public static final int SUCCESS = 1;
        public static final int UNKNOWN = 0;
        private static volatile WifiRttLog[] _emptyArray;
        public RttOverallStatusHistogramBucket[] histogramOverallStatus;
        public int numRequests;
        public RttToPeerLog rttToAp;
        public RttToPeerLog rttToAware;

        public static final class RttToPeerLog extends MessageNano {
            private static volatile RttToPeerLog[] _emptyArray;
            public HistogramBucket[] histogramDistance;
            public RttIndividualStatusHistogramBucket[] histogramIndividualStatus;
            public HistogramBucket[] histogramNumPeersPerRequest;
            public HistogramBucket[] histogramNumRequestsPerApp;
            public HistogramBucket[] histogramRequestIntervalMs;
            public int numApps;
            public int numIndividualRequests;
            public int numRequests;

            public static RttToPeerLog[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new RttToPeerLog[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public RttToPeerLog() {
                clear();
            }

            public RttToPeerLog clear() {
                this.numRequests = 0;
                this.numIndividualRequests = 0;
                this.numApps = 0;
                this.histogramNumRequestsPerApp = HistogramBucket.emptyArray();
                this.histogramNumPeersPerRequest = HistogramBucket.emptyArray();
                this.histogramIndividualStatus = RttIndividualStatusHistogramBucket.emptyArray();
                this.histogramDistance = HistogramBucket.emptyArray();
                this.histogramRequestIntervalMs = HistogramBucket.emptyArray();
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.numRequests;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.numIndividualRequests;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                int i3 = this.numApps;
                if (i3 != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i3);
                }
                HistogramBucket[] histogramBucketArr = this.histogramNumRequestsPerApp;
                int i4 = 0;
                if (histogramBucketArr != null && histogramBucketArr.length > 0) {
                    int i5 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr2 = this.histogramNumRequestsPerApp;
                        if (i5 >= histogramBucketArr2.length) {
                            break;
                        }
                        HistogramBucket histogramBucket = histogramBucketArr2[i5];
                        if (histogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(4, histogramBucket);
                        }
                        i5++;
                    }
                }
                HistogramBucket[] histogramBucketArr3 = this.histogramNumPeersPerRequest;
                if (histogramBucketArr3 != null && histogramBucketArr3.length > 0) {
                    int i6 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr4 = this.histogramNumPeersPerRequest;
                        if (i6 >= histogramBucketArr4.length) {
                            break;
                        }
                        HistogramBucket histogramBucket2 = histogramBucketArr4[i6];
                        if (histogramBucket2 != null) {
                            codedOutputByteBufferNano.writeMessage(5, histogramBucket2);
                        }
                        i6++;
                    }
                }
                RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr = this.histogramIndividualStatus;
                if (rttIndividualStatusHistogramBucketArr != null && rttIndividualStatusHistogramBucketArr.length > 0) {
                    int i7 = 0;
                    while (true) {
                        RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr2 = this.histogramIndividualStatus;
                        if (i7 >= rttIndividualStatusHistogramBucketArr2.length) {
                            break;
                        }
                        RttIndividualStatusHistogramBucket rttIndividualStatusHistogramBucket = rttIndividualStatusHistogramBucketArr2[i7];
                        if (rttIndividualStatusHistogramBucket != null) {
                            codedOutputByteBufferNano.writeMessage(6, rttIndividualStatusHistogramBucket);
                        }
                        i7++;
                    }
                }
                HistogramBucket[] histogramBucketArr5 = this.histogramDistance;
                if (histogramBucketArr5 != null && histogramBucketArr5.length > 0) {
                    int i8 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr6 = this.histogramDistance;
                        if (i8 >= histogramBucketArr6.length) {
                            break;
                        }
                        HistogramBucket histogramBucket3 = histogramBucketArr6[i8];
                        if (histogramBucket3 != null) {
                            codedOutputByteBufferNano.writeMessage(7, histogramBucket3);
                        }
                        i8++;
                    }
                }
                HistogramBucket[] histogramBucketArr7 = this.histogramRequestIntervalMs;
                if (histogramBucketArr7 != null && histogramBucketArr7.length > 0) {
                    while (true) {
                        HistogramBucket[] histogramBucketArr8 = this.histogramRequestIntervalMs;
                        if (i4 >= histogramBucketArr8.length) {
                            break;
                        }
                        HistogramBucket histogramBucket4 = histogramBucketArr8[i4];
                        if (histogramBucket4 != null) {
                            codedOutputByteBufferNano.writeMessage(8, histogramBucket4);
                        }
                        i4++;
                    }
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.numRequests;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.numIndividualRequests;
                if (i2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
                }
                int i3 = this.numApps;
                if (i3 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
                }
                HistogramBucket[] histogramBucketArr = this.histogramNumRequestsPerApp;
                int i4 = 0;
                if (histogramBucketArr != null && histogramBucketArr.length > 0) {
                    int i5 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr2 = this.histogramNumRequestsPerApp;
                        if (i5 >= histogramBucketArr2.length) {
                            break;
                        }
                        HistogramBucket histogramBucket = histogramBucketArr2[i5];
                        if (histogramBucket != null) {
                            iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, histogramBucket);
                        }
                        i5++;
                    }
                }
                HistogramBucket[] histogramBucketArr3 = this.histogramNumPeersPerRequest;
                if (histogramBucketArr3 != null && histogramBucketArr3.length > 0) {
                    int i6 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr4 = this.histogramNumPeersPerRequest;
                        if (i6 >= histogramBucketArr4.length) {
                            break;
                        }
                        HistogramBucket histogramBucket2 = histogramBucketArr4[i6];
                        if (histogramBucket2 != null) {
                            iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, histogramBucket2);
                        }
                        i6++;
                    }
                }
                RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr = this.histogramIndividualStatus;
                if (rttIndividualStatusHistogramBucketArr != null && rttIndividualStatusHistogramBucketArr.length > 0) {
                    int i7 = 0;
                    while (true) {
                        RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr2 = this.histogramIndividualStatus;
                        if (i7 >= rttIndividualStatusHistogramBucketArr2.length) {
                            break;
                        }
                        RttIndividualStatusHistogramBucket rttIndividualStatusHistogramBucket = rttIndividualStatusHistogramBucketArr2[i7];
                        if (rttIndividualStatusHistogramBucket != null) {
                            iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, rttIndividualStatusHistogramBucket);
                        }
                        i7++;
                    }
                }
                HistogramBucket[] histogramBucketArr5 = this.histogramDistance;
                if (histogramBucketArr5 != null && histogramBucketArr5.length > 0) {
                    int i8 = 0;
                    while (true) {
                        HistogramBucket[] histogramBucketArr6 = this.histogramDistance;
                        if (i8 >= histogramBucketArr6.length) {
                            break;
                        }
                        HistogramBucket histogramBucket3 = histogramBucketArr6[i8];
                        if (histogramBucket3 != null) {
                            iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, histogramBucket3);
                        }
                        i8++;
                    }
                }
                HistogramBucket[] histogramBucketArr7 = this.histogramRequestIntervalMs;
                if (histogramBucketArr7 != null && histogramBucketArr7.length > 0) {
                    while (true) {
                        HistogramBucket[] histogramBucketArr8 = this.histogramRequestIntervalMs;
                        if (i4 >= histogramBucketArr8.length) {
                            break;
                        }
                        HistogramBucket histogramBucket4 = histogramBucketArr8[i4];
                        if (histogramBucket4 != null) {
                            iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, histogramBucket4);
                        }
                        i4++;
                    }
                }
                return iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public RttToPeerLog mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        this.numRequests = codedInputByteBufferNano.readInt32();
                    } else if (tag == 16) {
                        this.numIndividualRequests = codedInputByteBufferNano.readInt32();
                    } else if (tag == 24) {
                        this.numApps = codedInputByteBufferNano.readInt32();
                    } else if (tag == 34) {
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                        HistogramBucket[] histogramBucketArr = this.histogramNumRequestsPerApp;
                        int length = histogramBucketArr == null ? 0 : histogramBucketArr.length;
                        int i = repeatedFieldArrayLength + length;
                        HistogramBucket[] histogramBucketArr2 = new HistogramBucket[i];
                        if (length != 0) {
                            System.arraycopy(this.histogramNumRequestsPerApp, 0, histogramBucketArr2, 0, length);
                        }
                        while (length < i - 1) {
                            histogramBucketArr2[length] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr2[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        histogramBucketArr2[length] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr2[length]);
                        this.histogramNumRequestsPerApp = histogramBucketArr2;
                    } else if (tag == 42) {
                        int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                        HistogramBucket[] histogramBucketArr3 = this.histogramNumPeersPerRequest;
                        int length2 = histogramBucketArr3 == null ? 0 : histogramBucketArr3.length;
                        int i2 = repeatedFieldArrayLength2 + length2;
                        HistogramBucket[] histogramBucketArr4 = new HistogramBucket[i2];
                        if (length2 != 0) {
                            System.arraycopy(this.histogramNumPeersPerRequest, 0, histogramBucketArr4, 0, length2);
                        }
                        while (length2 < i2 - 1) {
                            histogramBucketArr4[length2] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr4[length2]);
                            codedInputByteBufferNano.readTag();
                            length2++;
                        }
                        histogramBucketArr4[length2] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr4[length2]);
                        this.histogramNumPeersPerRequest = histogramBucketArr4;
                    } else if (tag == 50) {
                        int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                        RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr = this.histogramIndividualStatus;
                        int length3 = rttIndividualStatusHistogramBucketArr == null ? 0 : rttIndividualStatusHistogramBucketArr.length;
                        int i3 = repeatedFieldArrayLength3 + length3;
                        RttIndividualStatusHistogramBucket[] rttIndividualStatusHistogramBucketArr2 = new RttIndividualStatusHistogramBucket[i3];
                        if (length3 != 0) {
                            System.arraycopy(this.histogramIndividualStatus, 0, rttIndividualStatusHistogramBucketArr2, 0, length3);
                        }
                        while (length3 < i3 - 1) {
                            rttIndividualStatusHistogramBucketArr2[length3] = new RttIndividualStatusHistogramBucket();
                            codedInputByteBufferNano.readMessage(rttIndividualStatusHistogramBucketArr2[length3]);
                            codedInputByteBufferNano.readTag();
                            length3++;
                        }
                        rttIndividualStatusHistogramBucketArr2[length3] = new RttIndividualStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(rttIndividualStatusHistogramBucketArr2[length3]);
                        this.histogramIndividualStatus = rttIndividualStatusHistogramBucketArr2;
                    } else if (tag == 58) {
                        int repeatedFieldArrayLength4 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 58);
                        HistogramBucket[] histogramBucketArr5 = this.histogramDistance;
                        int length4 = histogramBucketArr5 == null ? 0 : histogramBucketArr5.length;
                        int i4 = repeatedFieldArrayLength4 + length4;
                        HistogramBucket[] histogramBucketArr6 = new HistogramBucket[i4];
                        if (length4 != 0) {
                            System.arraycopy(this.histogramDistance, 0, histogramBucketArr6, 0, length4);
                        }
                        while (length4 < i4 - 1) {
                            histogramBucketArr6[length4] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr6[length4]);
                            codedInputByteBufferNano.readTag();
                            length4++;
                        }
                        histogramBucketArr6[length4] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr6[length4]);
                        this.histogramDistance = histogramBucketArr6;
                    } else if (tag != 66) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        int repeatedFieldArrayLength5 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 66);
                        HistogramBucket[] histogramBucketArr7 = this.histogramRequestIntervalMs;
                        int length5 = histogramBucketArr7 == null ? 0 : histogramBucketArr7.length;
                        int i5 = repeatedFieldArrayLength5 + length5;
                        HistogramBucket[] histogramBucketArr8 = new HistogramBucket[i5];
                        if (length5 != 0) {
                            System.arraycopy(this.histogramRequestIntervalMs, 0, histogramBucketArr8, 0, length5);
                        }
                        while (length5 < i5 - 1) {
                            histogramBucketArr8[length5] = new HistogramBucket();
                            codedInputByteBufferNano.readMessage(histogramBucketArr8[length5]);
                            codedInputByteBufferNano.readTag();
                            length5++;
                        }
                        histogramBucketArr8[length5] = new HistogramBucket();
                        codedInputByteBufferNano.readMessage(histogramBucketArr8[length5]);
                        this.histogramRequestIntervalMs = histogramBucketArr8;
                    }
                }
            }

            public static RttToPeerLog parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (RttToPeerLog) MessageNano.mergeFrom(new RttToPeerLog(), bArr);
            }

            public static RttToPeerLog parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new RttToPeerLog().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static final class HistogramBucket extends MessageNano {
            private static volatile HistogramBucket[] _emptyArray;
            public int count;
            public long end;
            public long start;

            public static HistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new HistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public HistogramBucket() {
                clear();
            }

            public HistogramBucket clear() {
                this.start = 0L;
                this.end = 0L;
                this.count = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                long j = this.start;
                if (j != 0) {
                    codedOutputByteBufferNano.writeInt64(1, j);
                }
                long j2 = this.end;
                if (j2 != 0) {
                    codedOutputByteBufferNano.writeInt64(2, j2);
                }
                int i = this.count;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(3, i);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                long j = this.start;
                if (j != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
                }
                long j2 = this.end;
                if (j2 != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, j2);
                }
                int i = this.count;
                return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public HistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        this.start = codedInputByteBufferNano.readInt64();
                    } else if (tag == 16) {
                        this.end = codedInputByteBufferNano.readInt64();
                    } else if (tag != 24) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.count = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static HistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (HistogramBucket) MessageNano.mergeFrom(new HistogramBucket(), bArr);
            }

            public static HistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new HistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static final class RttOverallStatusHistogramBucket extends MessageNano {
            private static volatile RttOverallStatusHistogramBucket[] _emptyArray;
            public int count;
            public int statusType;

            public static RttOverallStatusHistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new RttOverallStatusHistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public RttOverallStatusHistogramBucket() {
                clear();
            }

            public RttOverallStatusHistogramBucket clear() {
                this.statusType = 0;
                this.count = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.statusType;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.count;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.statusType;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.count;
                return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public RttOverallStatusHistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                this.statusType = int32;
                                break;
                        }
                    } else if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.count = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static RttOverallStatusHistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (RttOverallStatusHistogramBucket) MessageNano.mergeFrom(new RttOverallStatusHistogramBucket(), bArr);
            }

            public static RttOverallStatusHistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new RttOverallStatusHistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static final class RttIndividualStatusHistogramBucket extends MessageNano {
            private static volatile RttIndividualStatusHistogramBucket[] _emptyArray;
            public int count;
            public int statusType;

            public static RttIndividualStatusHistogramBucket[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new RttIndividualStatusHistogramBucket[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public RttIndividualStatusHistogramBucket() {
                clear();
            }

            public RttIndividualStatusHistogramBucket clear() {
                this.statusType = 0;
                this.count = 0;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                int i = this.statusType;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(1, i);
                }
                int i2 = this.count;
                if (i2 != 0) {
                    codedOutputByteBufferNano.writeInt32(2, i2);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            protected int computeSerializedSize() {
                int iComputeSerializedSize = super.computeSerializedSize();
                int i = this.statusType;
                if (i != 0) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                int i2 = this.count;
                return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
            }

            @Override // com.android.framework.protobuf.nano.MessageNano
            public RttIndividualStatusHistogramBucket mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int tag = codedInputByteBufferNano.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int int32 = codedInputByteBufferNano.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                this.statusType = int32;
                                break;
                        }
                    } else if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                            return this;
                        }
                    } else {
                        this.count = codedInputByteBufferNano.readInt32();
                    }
                }
            }

            public static RttIndividualStatusHistogramBucket parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
                return (RttIndividualStatusHistogramBucket) MessageNano.mergeFrom(new RttIndividualStatusHistogramBucket(), bArr);
            }

            public static RttIndividualStatusHistogramBucket parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                return new RttIndividualStatusHistogramBucket().mergeFrom(codedInputByteBufferNano);
            }
        }

        public static WifiRttLog[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WifiRttLog[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WifiRttLog() {
            clear();
        }

        public WifiRttLog clear() {
            this.numRequests = 0;
            this.histogramOverallStatus = RttOverallStatusHistogramBucket.emptyArray();
            this.rttToAp = null;
            this.rttToAware = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.numRequests;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr = this.histogramOverallStatus;
            if (rttOverallStatusHistogramBucketArr != null && rttOverallStatusHistogramBucketArr.length > 0) {
                int i2 = 0;
                while (true) {
                    RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr2 = this.histogramOverallStatus;
                    if (i2 >= rttOverallStatusHistogramBucketArr2.length) {
                        break;
                    }
                    RttOverallStatusHistogramBucket rttOverallStatusHistogramBucket = rttOverallStatusHistogramBucketArr2[i2];
                    if (rttOverallStatusHistogramBucket != null) {
                        codedOutputByteBufferNano.writeMessage(2, rttOverallStatusHistogramBucket);
                    }
                    i2++;
                }
            }
            RttToPeerLog rttToPeerLog = this.rttToAp;
            if (rttToPeerLog != null) {
                codedOutputByteBufferNano.writeMessage(3, rttToPeerLog);
            }
            RttToPeerLog rttToPeerLog2 = this.rttToAware;
            if (rttToPeerLog2 != null) {
                codedOutputByteBufferNano.writeMessage(4, rttToPeerLog2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.numRequests;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr = this.histogramOverallStatus;
            if (rttOverallStatusHistogramBucketArr != null && rttOverallStatusHistogramBucketArr.length > 0) {
                int i2 = 0;
                while (true) {
                    RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr2 = this.histogramOverallStatus;
                    if (i2 >= rttOverallStatusHistogramBucketArr2.length) {
                        break;
                    }
                    RttOverallStatusHistogramBucket rttOverallStatusHistogramBucket = rttOverallStatusHistogramBucketArr2[i2];
                    if (rttOverallStatusHistogramBucket != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, rttOverallStatusHistogramBucket);
                    }
                    i2++;
                }
            }
            RttToPeerLog rttToPeerLog = this.rttToAp;
            if (rttToPeerLog != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, rttToPeerLog);
            }
            RttToPeerLog rttToPeerLog2 = this.rttToAware;
            return rttToPeerLog2 != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, rttToPeerLog2) : iComputeSerializedSize;
        }

        @Override // com.android.framework.protobuf.nano.MessageNano
        public WifiRttLog mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int tag = codedInputByteBufferNano.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.numRequests = codedInputByteBufferNano.readInt32();
                } else if (tag == 18) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr = this.histogramOverallStatus;
                    int length = rttOverallStatusHistogramBucketArr == null ? 0 : rttOverallStatusHistogramBucketArr.length;
                    int i = repeatedFieldArrayLength + length;
                    RttOverallStatusHistogramBucket[] rttOverallStatusHistogramBucketArr2 = new RttOverallStatusHistogramBucket[i];
                    if (length != 0) {
                        System.arraycopy(this.histogramOverallStatus, 0, rttOverallStatusHistogramBucketArr2, 0, length);
                    }
                    while (length < i - 1) {
                        rttOverallStatusHistogramBucketArr2[length] = new RttOverallStatusHistogramBucket();
                        codedInputByteBufferNano.readMessage(rttOverallStatusHistogramBucketArr2[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    rttOverallStatusHistogramBucketArr2[length] = new RttOverallStatusHistogramBucket();
                    codedInputByteBufferNano.readMessage(rttOverallStatusHistogramBucketArr2[length]);
                    this.histogramOverallStatus = rttOverallStatusHistogramBucketArr2;
                } else if (tag == 26) {
                    if (this.rttToAp == null) {
                        this.rttToAp = new RttToPeerLog();
                    }
                    codedInputByteBufferNano.readMessage(this.rttToAp);
                } else if (tag != 34) {
                    if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, tag)) {
                        return this;
                    }
                } else {
                    if (this.rttToAware == null) {
                        this.rttToAware = new RttToPeerLog();
                    }
                    codedInputByteBufferNano.readMessage(this.rttToAware);
                }
            }
        }

        public static WifiRttLog parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (WifiRttLog) MessageNano.mergeFrom(new WifiRttLog(), bArr);
        }

        public static WifiRttLog parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new WifiRttLog().mergeFrom(codedInputByteBufferNano);
        }
    }
}
