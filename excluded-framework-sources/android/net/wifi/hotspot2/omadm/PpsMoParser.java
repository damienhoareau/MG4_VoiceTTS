package android.net.wifi.hotspot2.omadm;

import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.pps.Credential;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.net.wifi.hotspot2.pps.Policy;
import android.net.wifi.hotspot2.pps.UpdateParameter;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public final class PpsMoParser {
    private static final String NODE_AAA_SERVER_TRUST_ROOT = "AAAServerTrustRoot";
    private static final String NODE_ABLE_TO_SHARE = "AbleToShare";
    private static final String NODE_CERTIFICATE_TYPE = "CertificateType";
    private static final String NODE_CERT_SHA256_FINGERPRINT = "CertSHA256Fingerprint";
    private static final String NODE_CERT_URL = "CertURL";
    private static final String NODE_CHECK_AAA_SERVER_CERT_STATUS = "CheckAAAServerCertStatus";
    private static final String NODE_COUNTRY = "Country";
    private static final String NODE_CREATION_DATE = "CreationDate";
    private static final String NODE_CREDENTIAL = "Credential";
    private static final String NODE_CREDENTIAL_PRIORITY = "CredentialPriority";
    private static final String NODE_DATA_LIMIT = "DataLimit";
    private static final String NODE_DIGITAL_CERTIFICATE = "DigitalCertificate";
    private static final String NODE_DOWNLINK_BANDWIDTH = "DLBandwidth";
    private static final String NODE_EAP_METHOD = "EAPMethod";
    private static final String NODE_EAP_TYPE = "EAPType";
    private static final String NODE_EXPIRATION_DATE = "ExpirationDate";
    private static final String NODE_EXTENSION = "Extension";
    private static final String NODE_FQDN = "FQDN";
    private static final String NODE_FQDN_MATCH = "FQDN_Match";
    private static final String NODE_FRIENDLY_NAME = "FriendlyName";
    private static final String NODE_HESSID = "HESSID";
    private static final String NODE_HOMESP = "HomeSP";
    private static final String NODE_HOME_OI = "HomeOI";
    private static final String NODE_HOME_OI_LIST = "HomeOIList";
    private static final String NODE_HOME_OI_REQUIRED = "HomeOIRequired";
    private static final String NODE_ICON_URL = "IconURL";
    private static final String NODE_INNER_EAP_TYPE = "InnerEAPType";
    private static final String NODE_INNER_METHOD = "InnerMethod";
    private static final String NODE_INNER_VENDOR_ID = "InnerVendorID";
    private static final String NODE_INNER_VENDOR_TYPE = "InnerVendorType";
    private static final String NODE_IP_PROTOCOL = "IPProtocol";
    private static final String NODE_MACHINE_MANAGED = "MachineManaged";
    private static final String NODE_MAXIMUM_BSS_LOAD_VALUE = "MaximumBSSLoadValue";
    private static final String NODE_MIN_BACKHAUL_THRESHOLD = "MinBackhaulThreshold";
    private static final String NODE_NETWORK_ID = "NetworkID";
    private static final String NODE_NETWORK_TYPE = "NetworkType";
    private static final String NODE_OTHER = "Other";
    private static final String NODE_OTHER_HOME_PARTNERS = "OtherHomePartners";
    private static final String NODE_PASSWORD = "Password";
    private static final String NODE_PER_PROVIDER_SUBSCRIPTION = "PerProviderSubscription";
    private static final String NODE_POLICY = "Policy";
    private static final String NODE_POLICY_UPDATE = "PolicyUpdate";
    private static final String NODE_PORT_NUMBER = "PortNumber";
    private static final String NODE_PREFERRED_ROAMING_PARTNER_LIST = "PreferredRoamingPartnerList";
    private static final String NODE_PRIORITY = "Priority";
    private static final String NODE_REALM = "Realm";
    private static final String NODE_REQUIRED_PROTO_PORT_TUPLE = "RequiredProtoPortTuple";
    private static final String NODE_RESTRICTION = "Restriction";
    private static final String NODE_ROAMING_CONSORTIUM_OI = "RoamingConsortiumOI";
    private static final String NODE_SIM = "SIM";
    private static final String NODE_SIM_IMSI = "IMSI";
    private static final String NODE_SOFT_TOKEN_APP = "SoftTokenApp";
    private static final String NODE_SP_EXCLUSION_LIST = "SPExclusionList";
    private static final String NODE_SSID = "SSID";
    private static final String NODE_START_DATE = "StartDate";
    private static final String NODE_SUBSCRIPTION_PARAMETER = "SubscriptionParameter";
    private static final String NODE_SUBSCRIPTION_UPDATE = "SubscriptionUpdate";
    private static final String NODE_TIME_LIMIT = "TimeLimit";
    private static final String NODE_TRUST_ROOT = "TrustRoot";
    private static final String NODE_TYPE_OF_SUBSCRIPTION = "TypeOfSubscription";
    private static final String NODE_UPDATE_IDENTIFIER = "UpdateIdentifier";
    private static final String NODE_UPDATE_INTERVAL = "UpdateInterval";
    private static final String NODE_UPDATE_METHOD = "UpdateMethod";
    private static final String NODE_UPLINK_BANDWIDTH = "ULBandwidth";
    private static final String NODE_URI = "URI";
    private static final String NODE_USAGE_LIMITS = "UsageLimits";
    private static final String NODE_USAGE_TIME_PERIOD = "UsageTimePeriod";
    private static final String NODE_USERNAME = "Username";
    private static final String NODE_USERNAME_PASSWORD = "UsernamePassword";
    private static final String NODE_VENDOR_ID = "VendorId";
    private static final String NODE_VENDOR_TYPE = "VendorType";
    private static final String PPS_MO_URN = "urn:wfa:mo:hotspot2dot0-perprovidersubscription:1.0";
    private static final String TAG = "PpsMoParser";
    private static final String TAG_DDF_NAME = "DDFName";
    private static final String TAG_MANAGEMENT_TREE = "MgmtTree";
    private static final String TAG_NODE = "Node";
    private static final String TAG_NODE_NAME = "NodeName";
    private static final String TAG_RT_PROPERTIES = "RTProperties";
    private static final String TAG_TYPE = "Type";
    private static final String TAG_VALUE = "Value";
    private static final String TAG_VER_DTD = "VerDTD";

    private static class ParsingException extends Exception {
        public ParsingException(String str) {
            super(str);
        }
    }

    private static abstract class PPSNode {
        private final String mName;

        public abstract List<PPSNode> getChildren();

        public abstract String getValue();

        public abstract boolean isLeaf();

        public PPSNode(String str) {
            this.mName = str;
        }

        public String getName() {
            return this.mName;
        }
    }

    private static class LeafNode extends PPSNode {
        private final String mValue;

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public List<PPSNode> getChildren() {
            return null;
        }

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public boolean isLeaf() {
            return true;
        }

        public LeafNode(String str, String str2) {
            super(str);
            this.mValue = str2;
        }

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public String getValue() {
            return this.mValue;
        }
    }

    private static class InternalNode extends PPSNode {
        private final List<PPSNode> mChildren;

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public String getValue() {
            return null;
        }

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public boolean isLeaf() {
            return false;
        }

        public InternalNode(String str, List<PPSNode> list) {
            super(str);
            this.mChildren = list;
        }

        @Override // android.net.wifi.hotspot2.omadm.PpsMoParser.PPSNode
        public List<PPSNode> getChildren() {
            return this.mChildren;
        }
    }

    public static PasspointConfiguration parseMoText(String str) {
        try {
            XMLNode xMLNode = new XMLParser().parse(str);
            if (xMLNode == null) {
                return null;
            }
            if (xMLNode.getTag() != TAG_MANAGEMENT_TREE) {
                Log.e(TAG, "Root is not a MgmtTree");
                return null;
            }
            PasspointConfiguration ppsNode = null;
            String text = null;
            for (XMLNode xMLNode2 : xMLNode.getChildren()) {
                String tag = xMLNode2.getTag();
                byte b = -1;
                int iHashCode = tag.hashCode();
                if (iHashCode != -1736120495) {
                    if (iHashCode == 2433570 && tag.equals(TAG_NODE)) {
                        b = 1;
                    }
                } else if (tag.equals(TAG_VER_DTD)) {
                    b = 0;
                }
                if (b == 0) {
                    if (text != null) {
                        Log.e(TAG, "Duplicate VerDTD element");
                        return null;
                    }
                    text = xMLNode2.getText();
                } else {
                    if (b != 1) {
                        Log.e(TAG, "Unknown node: " + xMLNode2.getTag());
                        return null;
                    }
                    if (ppsNode != null) {
                        Log.e(TAG, "Unexpected multiple Node element under MgmtTree");
                        return null;
                    }
                    try {
                        ppsNode = parsePpsNode(xMLNode2);
                    } catch (ParsingException e) {
                        Log.e(TAG, e.getMessage());
                        return null;
                    }
                }
            }
            return ppsNode;
        } catch (IOException | SAXException unused) {
            return null;
        }
    }

    private static PasspointConfiguration parsePpsNode(XMLNode xMLNode) throws ParsingException {
        PasspointConfiguration ppsInstance = null;
        String text = null;
        int integer = Integer.MIN_VALUE;
        for (XMLNode xMLNode2 : xMLNode.getChildren()) {
            String tag = xMLNode2.getTag();
            byte b = -1;
            int iHashCode = tag.hashCode();
            if (iHashCode != -1852765931) {
                if (iHashCode != 2433570) {
                    if (iHashCode == 1187524557 && tag.equals(TAG_NODE_NAME)) {
                        b = 0;
                    }
                } else if (tag.equals(TAG_NODE)) {
                    b = 1;
                }
            } else if (tag.equals(TAG_RT_PROPERTIES)) {
                b = 2;
            }
            if (b == 0) {
                if (text != null) {
                    throw new ParsingException("Duplicate NodeName: " + xMLNode2.getText());
                }
                text = xMLNode2.getText();
                if (!TextUtils.equals(text, NODE_PER_PROVIDER_SUBSCRIPTION)) {
                    throw new ParsingException("Unexpected NodeName: " + text);
                }
            } else if (b == 1) {
                PPSNode pPSNodeBuildPpsNode = buildPpsNode(xMLNode2);
                if (TextUtils.equals(pPSNodeBuildPpsNode.getName(), NODE_UPDATE_IDENTIFIER)) {
                    if (integer != Integer.MIN_VALUE) {
                        throw new ParsingException("Multiple node for UpdateIdentifier");
                    }
                    integer = parseInteger(getPpsNodeValue(pPSNodeBuildPpsNode));
                } else {
                    if (ppsInstance != null) {
                        throw new ParsingException("Multiple PPS instance");
                    }
                    ppsInstance = parsePpsInstance(pPSNodeBuildPpsNode);
                }
            } else if (b == 2) {
                String urn = parseUrn(xMLNode2);
                if (!TextUtils.equals(urn, PPS_MO_URN)) {
                    throw new ParsingException("Unknown URN: " + urn);
                }
            } else {
                throw new ParsingException("Unknown tag under PPS node: " + xMLNode2.getTag());
            }
        }
        if (ppsInstance != null && integer != Integer.MIN_VALUE) {
            ppsInstance.setUpdateIdentifier(integer);
        }
        return ppsInstance;
    }

    private static String parseUrn(XMLNode xMLNode) throws ParsingException {
        if (xMLNode.getChildren().size() != 1) {
            throw new ParsingException("Expect RTPProperties node to only have one child");
        }
        XMLNode xMLNode2 = xMLNode.getChildren().get(0);
        if (xMLNode2.getChildren().size() != 1) {
            throw new ParsingException("Expect Type node to only have one child");
        }
        if (!TextUtils.equals(xMLNode2.getTag(), TAG_TYPE)) {
            throw new ParsingException("Unexpected tag for Type: " + xMLNode2.getTag());
        }
        XMLNode xMLNode3 = xMLNode2.getChildren().get(0);
        if (!xMLNode3.getChildren().isEmpty()) {
            throw new ParsingException("Expect DDFName node to have no child");
        }
        if (!TextUtils.equals(xMLNode3.getTag(), TAG_DDF_NAME)) {
            throw new ParsingException("Unexpected tag for DDFName: " + xMLNode3.getTag());
        }
        return xMLNode3.getText();
    }

    private static PPSNode buildPpsNode(XMLNode xMLNode) throws ParsingException {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        String text = null;
        String text2 = null;
        for (XMLNode xMLNode2 : xMLNode.getChildren()) {
            String tag = xMLNode2.getTag();
            if (TextUtils.equals(tag, TAG_NODE_NAME)) {
                if (text != null) {
                    throw new ParsingException("Duplicate NodeName node");
                }
                text = xMLNode2.getText();
            } else if (TextUtils.equals(tag, TAG_NODE)) {
                PPSNode pPSNodeBuildPpsNode = buildPpsNode(xMLNode2);
                if (hashSet.contains(pPSNodeBuildPpsNode.getName())) {
                    throw new ParsingException("Duplicate node: " + pPSNodeBuildPpsNode.getName());
                }
                hashSet.add(pPSNodeBuildPpsNode.getName());
                arrayList.add(pPSNodeBuildPpsNode);
            } else {
                if (!TextUtils.equals(tag, TAG_VALUE)) {
                    throw new ParsingException("Unknown tag: " + tag);
                }
                if (text2 != null) {
                    throw new ParsingException("Duplicate Value node");
                }
                text2 = xMLNode2.getText();
            }
        }
        if (text == null) {
            throw new ParsingException("Invalid node: missing NodeName");
        }
        if (text2 == null && arrayList.size() == 0) {
            throw new ParsingException("Invalid node: " + text + " missing both value and children");
        }
        if (text2 == null || arrayList.size() <= 0) {
            if (text2 != null) {
                return new LeafNode(text, text2);
            }
            return new InternalNode(text, arrayList);
        }
        throw new ParsingException("Invalid node: " + text + " contained both value and children");
    }

    private static String getPpsNodeValue(PPSNode pPSNode) throws ParsingException {
        if (!pPSNode.isLeaf()) {
            throw new ParsingException("Cannot get value from a non-leaf node: " + pPSNode.getName());
        }
        return pPSNode.getValue();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:49:0x007e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:50:0x00a2 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x00af A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:52:0x00b4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:53:0x00bd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:54:0x00c6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:55:0x00cf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:56:0x00d8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:57:0x0099 A[SYNTHETIC] */
    private static PasspointConfiguration parsePpsInstance(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for PPS instance");
        }
        PasspointConfiguration passpointConfiguration = new PasspointConfiguration();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            byte b = -1;
            switch (pPSNode2.getName()) {
                case "HomeSP":
                    b = 0;
                case "Policy":
                    b = 2;
                case "SubscriptionParameter":
                    b = 5;
                case "SubscriptionUpdate":
                    b = 4;
                case "AAAServerTrustRoot":
                    b = 3;
                case "Credential":
                    b = 1;
                case "Extension":
                    b = 7;
                case "CredentialPriority":
                    b = 6;
                default:
                    switch (b) {
                        case 0:
                            passpointConfiguration.setHomeSp(parseHomeSP(pPSNode2));
                            break;
                        case 1:
                            passpointConfiguration.setCredential(parseCredential(pPSNode2));
                            break;
                        case 2:
                            passpointConfiguration.setPolicy(parsePolicy(pPSNode2));
                            break;
                        case 3:
                            passpointConfiguration.setTrustRootCertList(parseAAAServerTrustRootList(pPSNode2));
                            break;
                        case 4:
                            passpointConfiguration.setSubscriptionUpdate(parseUpdateParameter(pPSNode2));
                            break;
                        case 5:
                            parseSubscriptionParameter(pPSNode2, passpointConfiguration);
                            break;
                        case 6:
                            passpointConfiguration.setCredentialPriority(parseInteger(getPpsNodeValue(pPSNode2)));
                            break;
                        case 7:
                            Log.d(TAG, "Ignore Extension node for vendor specific information");
                            break;
                        default:
                            throw new ParsingException("Unknown node: " + pPSNode2.getName());
                    }
            }
        }
        return passpointConfiguration;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:45:0x0074 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:46:0x0098 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:47:0x00b4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:48:0x00bd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x00c6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:50:0x00d3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x00dc A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:52:0x008f A[SYNTHETIC] */
    private static HomeSp parseHomeSP(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for HomeSP");
        }
        HomeSp homeSp = new HomeSp();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            byte b = -1;
            switch (pPSNode2.getName()) {
                case "HomeOIList":
                    b = 5;
                case "IconURL":
                    b = 3;
                case "NetworkID":
                    b = 4;
                case "FQDN":
                    b = 0;
                case "RoamingConsortiumOI":
                    b = 2;
                case "FriendlyName":
                    b = 1;
                case "OtherHomePartners":
                    b = 6;
                default:
                    switch (b) {
                        case 0:
                            homeSp.setFqdn(getPpsNodeValue(pPSNode2));
                            break;
                        case 1:
                            homeSp.setFriendlyName(getPpsNodeValue(pPSNode2));
                            break;
                        case 2:
                            homeSp.setRoamingConsortiumOis(parseRoamingConsortiumOI(getPpsNodeValue(pPSNode2)));
                            break;
                        case 3:
                            homeSp.setIconUrl(getPpsNodeValue(pPSNode2));
                            break;
                        case 4:
                            homeSp.setHomeNetworkIds(parseNetworkIds(pPSNode2));
                            break;
                        case 5:
                            Pair<List<Long>, List<Long>> homeOIList = parseHomeOIList(pPSNode2);
                            homeSp.setMatchAllOis(convertFromLongList(homeOIList.first));
                            homeSp.setMatchAnyOis(convertFromLongList(homeOIList.second));
                            break;
                        case 6:
                            homeSp.setOtherHomePartners(parseOtherHomePartners(pPSNode2));
                            break;
                        default:
                            throw new ParsingException("Unknown node under HomeSP: " + pPSNode2.getName());
                    }
            }
        }
        return homeSp;
    }

    private static long[] parseRoamingConsortiumOI(String str) throws ParsingException {
        String[] strArrSplit = str.split(",");
        long[] jArr = new long[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            jArr[i] = parseLong(strArrSplit[i], 16);
        }
        return jArr;
    }

    private static Map<String, Long> parseNetworkIds(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for NetworkID");
        }
        HashMap map = new HashMap();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            Pair<String, Long> networkIdInstance = parseNetworkIdInstance(it.next());
            map.put(networkIdInstance.first, networkIdInstance.second);
        }
        return map;
    }

    private static Pair<String, Long> parseNetworkIdInstance(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for NetworkID instance");
        }
        String ppsNodeValue = null;
        Long lValueOf = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != 2554747) {
                if (iHashCode == 2127576568 && name.equals(NODE_HESSID)) {
                    b = 1;
                }
            } else if (name.equals(NODE_SSID)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else if (b == 1) {
                lValueOf = Long.valueOf(parseLong(getPpsNodeValue(pPSNode2), 16));
            } else {
                throw new ParsingException("Unknown node under NetworkID instance: " + pPSNode2.getName());
            }
        }
        if (ppsNodeValue == null) {
            throw new ParsingException("NetworkID instance missing SSID");
        }
        return new Pair<>(ppsNodeValue, lValueOf);
    }

    private static Pair<List<Long>, List<Long>> parseHomeOIList(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for HomeOIList");
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            Pair<Long, Boolean> homeOIInstance = parseHomeOIInstance(it.next());
            if (homeOIInstance.second.booleanValue()) {
                arrayList.add(homeOIInstance.first);
            } else {
                arrayList2.add(homeOIInstance.first);
            }
        }
        return new Pair<>(arrayList, arrayList2);
    }

    private static Pair<Long, Boolean> parseHomeOIInstance(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for HomeOI instance");
        }
        Long lValueOf = null;
        Boolean boolValueOf = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -2127810791) {
                if (iHashCode == -1935174184 && name.equals(NODE_HOME_OI_REQUIRED)) {
                    b = 1;
                }
            } else if (name.equals(NODE_HOME_OI)) {
                b = 0;
            }
            if (b == 0) {
                try {
                    lValueOf = Long.valueOf(getPpsNodeValue(pPSNode2), 16);
                } catch (NumberFormatException unused) {
                    throw new ParsingException("Invalid HomeOI: " + getPpsNodeValue(pPSNode2));
                }
            } else if (b == 1) {
                boolValueOf = Boolean.valueOf(getPpsNodeValue(pPSNode2));
            } else {
                throw new ParsingException("Unknown node under NetworkID instance: " + pPSNode2.getName());
            }
        }
        if (lValueOf == null) {
            throw new ParsingException("HomeOI instance missing OI field");
        }
        if (boolValueOf == null) {
            throw new ParsingException("HomeOI instance missing required field");
        }
        return new Pair<>(lValueOf, boolValueOf);
    }

    private static String[] parseOtherHomePartners(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for OtherHomePartners");
        }
        ArrayList arrayList = new ArrayList();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            arrayList.add(parseOtherHomePartnerInstance(it.next()));
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private static String parseOtherHomePartnerInstance(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for OtherHomePartner instance");
        }
        String ppsNodeValue = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            if (name.hashCode() == 2165397 && name.equals(NODE_FQDN)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else {
                throw new ParsingException("Unknown node under OtherHomePartner instance: " + pPSNode2.getName());
            }
        }
        if (ppsNodeValue != null) {
            return ppsNodeValue;
        }
        throw new ParsingException("OtherHomePartner instance missing FQDN field");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:45:0x0074 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:46:0x0098 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:47:0x00a5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:48:0x00ae A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x00b7 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:50:0x00c0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x00cd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:52:0x008f A[SYNTHETIC] */
    private static Credential parseCredential(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for HomeSP");
        }
        Credential credential = new Credential();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            byte b = -1;
            switch (pPSNode2.getName()) {
                case "ExpirationDate":
                    b = 1;
                case "DigitalCertificate":
                    b = 3;
                case "SIM":
                    b = 6;
                case "Realm":
                    b = 4;
                case "UsernamePassword":
                    b = 2;
                case "CheckAAAServerCertStatus":
                    b = 5;
                case "CreationDate":
                    b = 0;
                default:
                    switch (b) {
                        case 0:
                            credential.setCreationTimeInMillis(parseDate(getPpsNodeValue(pPSNode2)));
                            break;
                        case 1:
                            credential.setExpirationTimeInMillis(parseDate(getPpsNodeValue(pPSNode2)));
                            break;
                        case 2:
                            credential.setUserCredential(parseUserCredential(pPSNode2));
                            break;
                        case 3:
                            credential.setCertCredential(parseCertificateCredential(pPSNode2));
                            break;
                        case 4:
                            credential.setRealm(getPpsNodeValue(pPSNode2));
                            break;
                        case 5:
                            credential.setCheckAaaServerCertStatus(Boolean.parseBoolean(getPpsNodeValue(pPSNode2)));
                            break;
                        case 6:
                            credential.setSimCredential(parseSimCredential(pPSNode2));
                            break;
                        default:
                            throw new ParsingException("Unknown node under Credential: " + pPSNode2.getName());
                    }
            }
        }
        return credential;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static Credential.UserCredential parseUserCredential(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for UsernamePassword");
        }
        Credential.UserCredential userCredential = new Credential.UserCredential();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            switch (name.hashCode()) {
                case -201069322:
                    if (name.equals(NODE_USERNAME)) {
                        b = 0;
                    }
                    break;
                case -123996342:
                    if (name.equals(NODE_ABLE_TO_SHARE)) {
                        b = 4;
                    }
                    break;
                case 1045832056:
                    if (name.equals(NODE_MACHINE_MANAGED)) {
                        b = 2;
                    }
                    break;
                case 1281629883:
                    if (name.equals(NODE_PASSWORD)) {
                        b = 1;
                    }
                    break;
                case 1410776018:
                    if (name.equals(NODE_SOFT_TOKEN_APP)) {
                        b = 3;
                    }
                    break;
                case 1740345653:
                    if (name.equals(NODE_EAP_METHOD)) {
                        b = 5;
                    }
                    break;
            }
            if (b == 0) {
                userCredential.setUsername(getPpsNodeValue(pPSNode2));
            } else if (b == 1) {
                userCredential.setPassword(getPpsNodeValue(pPSNode2));
            } else if (b == 2) {
                userCredential.setMachineManaged(Boolean.parseBoolean(getPpsNodeValue(pPSNode2)));
            } else if (b == 3) {
                userCredential.setSoftTokenApp(getPpsNodeValue(pPSNode2));
            } else if (b == 4) {
                userCredential.setAbleToShare(Boolean.parseBoolean(getPpsNodeValue(pPSNode2)));
            } else if (b == 5) {
                parseEAPMethod(pPSNode2, userCredential);
            } else {
                throw new ParsingException("Unknown node under UsernamPassword: " + pPSNode2.getName());
            }
        }
        return userCredential;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:41:0x006f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:42:0x00a6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:43:0x00af A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:44:0x008a A[SYNTHETIC] */
    private static void parseEAPMethod(PPSNode pPSNode, Credential.UserCredential userCredential) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for EAPMethod");
        }
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            byte b = -1;
            switch (pPSNode2.getName()) {
                case "VendorId":
                    b = 2;
                case "InnerEAPType":
                    b = 4;
                case "VendorType":
                    b = 3;
                case "EAPType":
                    b = 0;
                case "InnerVendorType":
                    b = 6;
                case "InnerMethod":
                    b = 1;
                case "InnerVendorID":
                    b = 5;
                default:
                    switch (b) {
                        case 0:
                            userCredential.setEapType(parseInteger(getPpsNodeValue(pPSNode2)));
                            break;
                        case 1:
                            userCredential.setNonEapInnerMethod(getPpsNodeValue(pPSNode2));
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            Log.d(TAG, "Ignore unsupported EAP method parameter: " + pPSNode2.getName());
                            break;
                        default:
                            throw new ParsingException("Unknown node under EAPMethod: " + pPSNode2.getName());
                    }
            }
        }
    }

    private static Credential.CertificateCredential parseCertificateCredential(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for DigitalCertificate");
        }
        Credential.CertificateCredential certificateCredential = new Credential.CertificateCredential();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -1914611375) {
                if (iHashCode == -285451687 && name.equals(NODE_CERT_SHA256_FINGERPRINT)) {
                    b = 1;
                }
            } else if (name.equals(NODE_CERTIFICATE_TYPE)) {
                b = 0;
            }
            if (b == 0) {
                certificateCredential.setCertType(getPpsNodeValue(pPSNode2));
            } else if (b == 1) {
                certificateCredential.setCertSha256Fingerprint(parseHexString(getPpsNodeValue(pPSNode2)));
            } else {
                throw new ParsingException("Unknown node under DigitalCertificate: " + pPSNode2.getName());
            }
        }
        return certificateCredential;
    }

    private static Credential.SimCredential parseSimCredential(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for SIM");
        }
        Credential.SimCredential simCredential = new Credential.SimCredential();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -1249356658) {
                if (iHashCode == 2251386 && name.equals("IMSI")) {
                    b = 0;
                }
            } else if (name.equals(NODE_EAP_TYPE)) {
                b = 1;
            }
            if (b == 0) {
                simCredential.setImsi(getPpsNodeValue(pPSNode2));
            } else if (b == 1) {
                simCredential.setEapType(parseInteger(getPpsNodeValue(pPSNode2)));
            } else {
                throw new ParsingException("Unknown node under SIM: " + pPSNode2.getName());
            }
        }
        return simCredential;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static Policy parsePolicy(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for Policy");
        }
        Policy policy = new Policy();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            switch (name.hashCode()) {
                case -1710886725:
                    if (name.equals(NODE_POLICY_UPDATE)) {
                        b = 2;
                    }
                    break;
                case -281271454:
                    if (name.equals(NODE_MIN_BACKHAUL_THRESHOLD)) {
                        b = 1;
                    }
                    break;
                case -166875607:
                    if (name.equals(NODE_MAXIMUM_BSS_LOAD_VALUE)) {
                        b = 5;
                    }
                    break;
                case 586018863:
                    if (name.equals(NODE_SP_EXCLUSION_LIST)) {
                        b = 3;
                    }
                    break;
                case 783647838:
                    if (name.equals(NODE_REQUIRED_PROTO_PORT_TUPLE)) {
                        b = 4;
                    }
                    break;
                case 1337803246:
                    if (name.equals(NODE_PREFERRED_ROAMING_PARTNER_LIST)) {
                        b = 0;
                    }
                    break;
            }
            if (b == 0) {
                policy.setPreferredRoamingPartnerList(parsePreferredRoamingPartnerList(pPSNode2));
            } else if (b == 1) {
                parseMinBackhaulThreshold(pPSNode2, policy);
            } else if (b == 2) {
                policy.setPolicyUpdate(parseUpdateParameter(pPSNode2));
            } else if (b == 3) {
                policy.setExcludedSsidList(parseSpExclusionList(pPSNode2));
            } else if (b == 4) {
                policy.setRequiredProtoPortMap(parseRequiredProtoPortTuple(pPSNode2));
            } else if (b == 5) {
                policy.setMaximumBssLoadValue(parseInteger(getPpsNodeValue(pPSNode2)));
            } else {
                throw new ParsingException("Unknown node under Policy: " + pPSNode2.getName());
            }
        }
        return policy;
    }

    private static List<Policy.RoamingPartner> parsePreferredRoamingPartnerList(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for PreferredRoamingPartnerList");
        }
        ArrayList arrayList = new ArrayList();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            arrayList.add(parsePreferredRoamingPartner(it.next()));
        }
        return arrayList;
    }

    private static Policy.RoamingPartner parsePreferredRoamingPartner(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for PreferredRoamingPartner instance");
        }
        Policy.RoamingPartner roamingPartner = new Policy.RoamingPartner();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -1672482954) {
                if (iHashCode != -1100816956) {
                    if (iHashCode == 305746811 && name.equals(NODE_FQDN_MATCH)) {
                        b = 0;
                    }
                } else if (name.equals(NODE_PRIORITY)) {
                    b = 1;
                }
            } else if (name.equals(NODE_COUNTRY)) {
                b = 2;
            }
            if (b == 0) {
                String ppsNodeValue = getPpsNodeValue(pPSNode2);
                String[] strArrSplit = ppsNodeValue.split(",");
                if (strArrSplit.length != 2) {
                    throw new ParsingException("Invalid FQDN_Match: " + ppsNodeValue);
                }
                roamingPartner.setFqdn(strArrSplit[0]);
                if (TextUtils.equals(strArrSplit[1], "exactMatch")) {
                    roamingPartner.setFqdnExactMatch(true);
                } else if (TextUtils.equals(strArrSplit[1], "includeSubdomains")) {
                    roamingPartner.setFqdnExactMatch(false);
                } else {
                    throw new ParsingException("Invalid FQDN_Match: " + ppsNodeValue);
                }
            } else if (b == 1) {
                roamingPartner.setPriority(parseInteger(getPpsNodeValue(pPSNode2)));
            } else if (b == 2) {
                roamingPartner.setCountries(getPpsNodeValue(pPSNode2));
            } else {
                throw new ParsingException("Unknown node under PreferredRoamingPartnerList instance " + pPSNode2.getName());
            }
        }
        return roamingPartner;
    }

    private static void parseMinBackhaulThreshold(PPSNode pPSNode, Policy policy) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for MinBackhaulThreshold");
        }
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            parseMinBackhaulThresholdInstance(it.next(), policy);
        }
    }

    private static void parseMinBackhaulThresholdInstance(PPSNode pPSNode, Policy policy) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for MinBackhaulThreshold instance");
        }
        String ppsNodeValue = null;
        long j = Long.MIN_VALUE;
        long j2 = Long.MIN_VALUE;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -272744856) {
                if (iHashCode != -133967910) {
                    if (iHashCode == 349434121 && name.equals(NODE_DOWNLINK_BANDWIDTH)) {
                        b = 1;
                    }
                } else if (name.equals(NODE_UPLINK_BANDWIDTH)) {
                    b = 2;
                }
            } else if (name.equals(NODE_NETWORK_TYPE)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else if (b == 1) {
                j = parseLong(getPpsNodeValue(pPSNode2), 10);
            } else if (b == 2) {
                j2 = parseLong(getPpsNodeValue(pPSNode2), 10);
            } else {
                throw new ParsingException("Unknown node under MinBackhaulThreshold instance " + pPSNode2.getName());
            }
        }
        if (ppsNodeValue == null) {
            throw new ParsingException("Missing NetworkType field");
        }
        if (TextUtils.equals(ppsNodeValue, CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME)) {
            policy.setMinHomeDownlinkBandwidth(j);
            policy.setMinHomeUplinkBandwidth(j2);
        } else if (TextUtils.equals(ppsNodeValue, "roaming")) {
            policy.setMinRoamingDownlinkBandwidth(j);
            policy.setMinRoamingUplinkBandwidth(j2);
        } else {
            throw new ParsingException("Invalid network type: " + ppsNodeValue);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:45:0x0074 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:46:0x00ab A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:47:0x00bf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:48:0x00d3 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x00dc A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:50:0x00e5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:51:0x00ee A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:52:0x008f A[SYNTHETIC] */
    private static UpdateParameter parseUpdateParameter(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for Update Parameters");
        }
        UpdateParameter updateParameter = new UpdateParameter();
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            byte b = -1;
            switch (pPSNode2.getName()) {
                case "UpdateMethod":
                    b = 1;
                case "TrustRoot":
                    b = 5;
                case "URI":
                    b = 3;
                case "Other":
                    b = 6;
                case "Restriction":
                    b = 2;
                case "UpdateInterval":
                    b = 0;
                case "UsernamePassword":
                    b = 4;
                default:
                    switch (b) {
                        case 0:
                            updateParameter.setUpdateIntervalInMinutes(parseLong(getPpsNodeValue(pPSNode2), 10));
                            break;
                        case 1:
                            updateParameter.setUpdateMethod(getPpsNodeValue(pPSNode2));
                            break;
                        case 2:
                            updateParameter.setRestriction(getPpsNodeValue(pPSNode2));
                            break;
                        case 3:
                            updateParameter.setServerUri(getPpsNodeValue(pPSNode2));
                            break;
                        case 4:
                            Pair<String, String> updateUserCredential = parseUpdateUserCredential(pPSNode2);
                            updateParameter.setUsername(updateUserCredential.first);
                            updateParameter.setBase64EncodedPassword(updateUserCredential.second);
                            break;
                        case 5:
                            Pair<String, byte[]> trustRoot = parseTrustRoot(pPSNode2);
                            updateParameter.setTrustRootCertUrl(trustRoot.first);
                            updateParameter.setTrustRootCertSha256Fingerprint(trustRoot.second);
                            break;
                        case 6:
                            Log.d(TAG, "Ignore unsupported paramter: " + pPSNode2.getName());
                            break;
                        default:
                            throw new ParsingException("Unknown node under Update Parameters: " + pPSNode2.getName());
                    }
            }
        }
        return updateParameter;
    }

    private static Pair<String, String> parseUpdateUserCredential(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for UsernamePassword");
        }
        String ppsNodeValue = null;
        String ppsNodeValue2 = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -201069322) {
                if (iHashCode == 1281629883 && name.equals(NODE_PASSWORD)) {
                    b = 1;
                }
            } else if (name.equals(NODE_USERNAME)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else if (b == 1) {
                ppsNodeValue2 = getPpsNodeValue(pPSNode2);
            } else {
                throw new ParsingException("Unknown node under UsernamePassword: " + pPSNode2.getName());
            }
        }
        return Pair.create(ppsNodeValue, ppsNodeValue2);
    }

    private static Pair<String, byte[]> parseTrustRoot(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for TrustRoot");
        }
        String ppsNodeValue = null;
        byte[] hexString = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -1961397109) {
                if (iHashCode == -285451687 && name.equals(NODE_CERT_SHA256_FINGERPRINT)) {
                    b = 1;
                }
            } else if (name.equals(NODE_CERT_URL)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else if (b == 1) {
                hexString = parseHexString(getPpsNodeValue(pPSNode2));
            } else {
                throw new ParsingException("Unknown node under TrustRoot: " + pPSNode2.getName());
            }
        }
        return Pair.create(ppsNodeValue, hexString);
    }

    private static String[] parseSpExclusionList(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for SPExclusionList");
        }
        ArrayList arrayList = new ArrayList();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            arrayList.add(parseSpExclusionInstance(it.next()));
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private static String parseSpExclusionInstance(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for SPExclusion instance");
        }
        String ppsNodeValue = null;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            if (name.hashCode() == 2554747 && name.equals(NODE_SSID)) {
                b = 0;
            }
            if (b == 0) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else {
                throw new ParsingException("Unknown node under SPExclusion instance");
            }
        }
        return ppsNodeValue;
    }

    private static Map<Integer, String> parseRequiredProtoPortTuple(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for RequiredProtoPortTuple");
        }
        HashMap map = new HashMap();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            Pair<Integer, String> protoPortTuple = parseProtoPortTuple(it.next());
            map.put(protoPortTuple.first, protoPortTuple.second);
        }
        return map;
    }

    private static Pair<Integer, String> parseProtoPortTuple(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for RequiredProtoPortTuple instance");
        }
        String ppsNodeValue = null;
        int integer = Integer.MIN_VALUE;
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            int iHashCode = name.hashCode();
            if (iHashCode != -952572705) {
                if (iHashCode == 1727403850 && name.equals(NODE_PORT_NUMBER)) {
                    b = 1;
                }
            } else if (name.equals(NODE_IP_PROTOCOL)) {
                b = 0;
            }
            if (b == 0) {
                integer = parseInteger(getPpsNodeValue(pPSNode2));
            } else if (b == 1) {
                ppsNodeValue = getPpsNodeValue(pPSNode2);
            } else {
                throw new ParsingException("Unknown node under RequiredProtoPortTuple instance" + pPSNode2.getName());
            }
        }
        if (integer == Integer.MIN_VALUE) {
            throw new ParsingException("Missing IPProtocol field");
        }
        if (ppsNodeValue == null) {
            throw new ParsingException("Missing PortNumber field");
        }
        return Pair.create(Integer.valueOf(integer), ppsNodeValue);
    }

    private static Map<String, byte[]> parseAAAServerTrustRootList(PPSNode pPSNode) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for AAAServerTrustRoot");
        }
        HashMap map = new HashMap();
        Iterator<PPSNode> it = pPSNode.getChildren().iterator();
        while (it.hasNext()) {
            Pair<String, byte[]> trustRoot = parseTrustRoot(it.next());
            map.put(trustRoot.first, trustRoot.second);
        }
        return map;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static void parseSubscriptionParameter(PPSNode pPSNode, PasspointConfiguration passpointConfiguration) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for SubscriptionParameter");
        }
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            switch (name.hashCode()) {
                case -1930116871:
                    if (name.equals(NODE_USAGE_LIMITS)) {
                        b = 3;
                    }
                    break;
                case -1670804707:
                    if (name.equals(NODE_EXPIRATION_DATE)) {
                        b = 1;
                    }
                    break;
                case -1655596402:
                    if (name.equals(NODE_TYPE_OF_SUBSCRIPTION)) {
                        b = 2;
                    }
                    break;
                case 1749851981:
                    if (name.equals(NODE_CREATION_DATE)) {
                        b = 0;
                    }
                    break;
            }
            if (b == 0) {
                passpointConfiguration.setSubscriptionCreationTimeInMillis(parseDate(getPpsNodeValue(pPSNode2)));
            } else if (b == 1) {
                passpointConfiguration.setSubscriptionExpirationTimeInMillis(parseDate(getPpsNodeValue(pPSNode2)));
            } else if (b == 2) {
                passpointConfiguration.setSubscriptionType(getPpsNodeValue(pPSNode2));
            } else if (b == 3) {
                parseUsageLimits(pPSNode2, passpointConfiguration);
            } else {
                throw new ParsingException("Unknown node under SubscriptionParameter" + pPSNode2.getName());
            }
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static void parseUsageLimits(PPSNode pPSNode, PasspointConfiguration passpointConfiguration) throws ParsingException {
        if (pPSNode.isLeaf()) {
            throw new ParsingException("Leaf node not expected for UsageLimits");
        }
        for (PPSNode pPSNode2 : pPSNode.getChildren()) {
            String name = pPSNode2.getName();
            byte b = -1;
            switch (name.hashCode()) {
                case -125810928:
                    if (name.equals(NODE_START_DATE)) {
                        b = 1;
                    }
                    break;
                case 587064143:
                    if (name.equals(NODE_USAGE_TIME_PERIOD)) {
                        b = 3;
                    }
                    break;
                case 1622722065:
                    if (name.equals(NODE_DATA_LIMIT)) {
                        b = 0;
                    }
                    break;
                case 2022760654:
                    if (name.equals(NODE_TIME_LIMIT)) {
                        b = 2;
                    }
                    break;
            }
            if (b == 0) {
                passpointConfiguration.setUsageLimitDataLimit(parseLong(getPpsNodeValue(pPSNode2), 10));
            } else if (b == 1) {
                passpointConfiguration.setUsageLimitStartTimeInMillis(parseDate(getPpsNodeValue(pPSNode2)));
            } else if (b == 2) {
                passpointConfiguration.setUsageLimitTimeLimitInMinutes(parseLong(getPpsNodeValue(pPSNode2), 10));
            } else if (b == 3) {
                passpointConfiguration.setUsageLimitUsageTimePeriodInMinutes(parseLong(getPpsNodeValue(pPSNode2), 10));
            } else {
                throw new ParsingException("Unknown node under UsageLimits" + pPSNode2.getName());
            }
        }
    }

    private static byte[] parseHexString(String str) throws ParsingException {
        if ((str.length() & 1) == 1) {
            throw new ParsingException("Odd length hex string: " + str.length());
        }
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            try {
                bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
            } catch (NumberFormatException unused) {
                throw new ParsingException("Invalid hex string: " + str);
            }
        }
        return bArr;
    }

    private static long parseDate(String str) throws ParsingException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(str).getTime();
        } catch (ParseException unused) {
            throw new ParsingException("Badly formatted time: " + str);
        }
    }

    private static int parseInteger(String str) throws ParsingException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            throw new ParsingException("Invalid integer value: " + str);
        }
    }

    private static long parseLong(String str, int i) throws ParsingException {
        try {
            return Long.parseLong(str, i);
        } catch (NumberFormatException unused) {
            throw new ParsingException("Invalid long integer value: " + str);
        }
    }

    private static long[] convertFromLongList(List<Long> list) {
        Long[] lArr = (Long[]) list.toArray(new Long[list.size()]);
        long[] jArr = new long[lArr.length];
        for (int i = 0; i < lArr.length; i++) {
            jArr[i] = lArr[i].longValue();
        }
        return jArr;
    }
}
