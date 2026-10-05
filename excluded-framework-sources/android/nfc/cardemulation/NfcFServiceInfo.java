package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public final class NfcFServiceInfo implements Parcelable {
    public static final Parcelable.Creator<NfcFServiceInfo> CREATOR = new Parcelable.Creator<NfcFServiceInfo>() { // from class: android.nfc.cardemulation.NfcFServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NfcFServiceInfo createFromParcel(Parcel parcel) {
            return new NfcFServiceInfo(ResolveInfo.CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readString(), parcel.readInt() != 0 ? parcel.readString() : null, parcel.readString(), parcel.readInt() != 0 ? parcel.readString() : null, parcel.readInt(), parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NfcFServiceInfo[] newArray(int i) {
            return new NfcFServiceInfo[i];
        }
    };
    private static final String DEFAULT_T3T_PMM = "FFFFFFFFFFFFFFFF";
    static final String TAG = "NfcFServiceInfo";
    final String mDescription;
    String mDynamicNfcid2;
    String mDynamicSystemCode;
    final String mNfcid2;
    final ResolveInfo mService;
    final String mSystemCode;
    final String mT3tPmm;
    final int mUid;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NfcFServiceInfo(ResolveInfo resolveInfo, String str, String str2, String str3, String str4, String str5, int i, String str6) {
        this.mService = resolveInfo;
        this.mDescription = str;
        this.mSystemCode = str2;
        this.mDynamicSystemCode = str3;
        this.mNfcid2 = str4;
        this.mDynamicNfcid2 = str5;
        this.mUid = i;
        this.mT3tPmm = str6;
    }

    /* JADX WARN: Code duplicated, block: B:76:0x0168  */
    public NfcFServiceInfo(PackageManager packageManager, ResolveInfo resolveInfo) throws Throwable {
        XmlResourceParser xmlResourceParser;
        int i;
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        try {
            XmlResourceParser xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, HostNfcFService.SERVICE_META_DATA);
            try {
                if (xmlResourceParserLoadXmlMetaData == null) {
                    throw new XmlPullParserException("No android.nfc.cardemulation.host_nfcf_service meta-data");
                }
                int eventType = xmlResourceParserLoadXmlMetaData.getEventType();
                while (true) {
                    i = 1;
                    if (eventType == 2 || eventType == 1) {
                        break;
                    } else {
                        eventType = xmlResourceParserLoadXmlMetaData.next();
                    }
                }
                if (!"host-nfcf-service".equals(xmlResourceParserLoadXmlMetaData.getName())) {
                    throw new XmlPullParserException("Meta-data does not start with <host-nfcf-service> tag");
                }
                Resources resourcesForApplication = packageManager.getResourcesForApplication(serviceInfo.applicationInfo);
                AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData);
                TypedArray typedArrayObtainAttributes = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.HostNfcFService);
                this.mService = resolveInfo;
                this.mDescription = typedArrayObtainAttributes.getString(0);
                this.mDynamicSystemCode = null;
                this.mDynamicNfcid2 = null;
                typedArrayObtainAttributes.recycle();
                int depth = xmlResourceParserLoadXmlMetaData.getDepth();
                String upperCase = null;
                String upperCase2 = null;
                String upperCase3 = null;
                while (true) {
                    int next = xmlResourceParserLoadXmlMetaData.next();
                    if ((next == 3 && xmlResourceParserLoadXmlMetaData.getDepth() <= depth) || next == i) {
                        break;
                    }
                    String name = xmlResourceParserLoadXmlMetaData.getName();
                    if (next == 2 && "system-code-filter".equals(name) && upperCase == null) {
                        TypedArray typedArrayObtainAttributes2 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.SystemCodeFilter);
                        upperCase = typedArrayObtainAttributes2.getString(0).toUpperCase();
                        if (!NfcFCardEmulation.isValidSystemCode(upperCase) && !upperCase.equalsIgnoreCase(WifiEnterpriseConfig.EMPTY_VALUE)) {
                            Log.e(TAG, "Invalid System Code: " + upperCase);
                            upperCase = null;
                        }
                        typedArrayObtainAttributes2.recycle();
                    } else if (next == 2 && "nfcid2-filter".equals(name) && upperCase2 == null) {
                        TypedArray typedArrayObtainAttributes3 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.Nfcid2Filter);
                        upperCase2 = typedArrayObtainAttributes3.getString(0).toUpperCase();
                        if (!upperCase2.equalsIgnoreCase("RANDOM") && !upperCase2.equalsIgnoreCase(WifiEnterpriseConfig.EMPTY_VALUE) && !NfcFCardEmulation.isValidNfcid2(upperCase2)) {
                            Log.e(TAG, "Invalid NFCID2: " + upperCase2);
                            upperCase2 = null;
                        }
                        typedArrayObtainAttributes3.recycle();
                    } else if (next == 2 && name.equals("t3tPmm-filter") && upperCase3 == null) {
                        TypedArray typedArrayObtainAttributes4 = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.T3tPmmFilter);
                        upperCase3 = typedArrayObtainAttributes4.getString(0).toUpperCase();
                        typedArrayObtainAttributes4.recycle();
                    }
                    i = 1;
                }
                this.mSystemCode = upperCase == null ? WifiEnterpriseConfig.EMPTY_VALUE : upperCase;
                this.mNfcid2 = upperCase2 == null ? WifiEnterpriseConfig.EMPTY_VALUE : upperCase2;
                this.mT3tPmm = upperCase3 == null ? DEFAULT_T3T_PMM : upperCase3;
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
                this.mUid = serviceInfo.applicationInfo.uid;
            } catch (PackageManager.NameNotFoundException unused) {
                xmlResourceParser = xmlResourceParserLoadXmlMetaData;
                try {
                    throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
                } catch (Throwable th) {
                    th = th;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                xmlResourceParser = xmlResourceParserLoadXmlMetaData;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw th;
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            xmlResourceParser = null;
        } catch (Throwable th3) {
            th = th3;
            xmlResourceParser = null;
        }
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getSystemCode() {
        String str = this.mDynamicSystemCode;
        return str == null ? this.mSystemCode : str;
    }

    public void setOrReplaceDynamicSystemCode(String str) {
        this.mDynamicSystemCode = str;
    }

    public String getNfcid2() {
        String str = this.mDynamicNfcid2;
        return str == null ? this.mNfcid2 : str;
    }

    public void setOrReplaceDynamicNfcid2(String str) {
        this.mDynamicNfcid2 = str;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public int getUid() {
        return this.mUid;
    }

    public String getT3tPmm() {
        return this.mT3tPmm;
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mService.loadLabel(packageManager);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NfcFService: ");
        sb.append(getComponent());
        sb.append(", description: " + this.mDescription);
        sb.append(", System Code: " + this.mSystemCode);
        if (this.mDynamicSystemCode != null) {
            sb.append(", dynamic System Code: " + this.mDynamicSystemCode);
        }
        sb.append(", NFCID2: " + this.mNfcid2);
        if (this.mDynamicNfcid2 != null) {
            sb.append(", dynamic NFCID2: " + this.mDynamicNfcid2);
        }
        sb.append(", T3T PMM:" + this.mT3tPmm);
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NfcFServiceInfo)) {
            return false;
        }
        NfcFServiceInfo nfcFServiceInfo = (NfcFServiceInfo) obj;
        return nfcFServiceInfo.getComponent().equals(getComponent()) && nfcFServiceInfo.mSystemCode.equalsIgnoreCase(this.mSystemCode) && nfcFServiceInfo.mNfcid2.equalsIgnoreCase(this.mNfcid2) && nfcFServiceInfo.mT3tPmm.equalsIgnoreCase(this.mT3tPmm);
    }

    public int hashCode() {
        return getComponent().hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mService.writeToParcel(parcel, i);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mSystemCode);
        parcel.writeInt(this.mDynamicSystemCode != null ? 1 : 0);
        String str = this.mDynamicSystemCode;
        if (str != null) {
            parcel.writeString(str);
        }
        parcel.writeString(this.mNfcid2);
        parcel.writeInt(this.mDynamicNfcid2 == null ? 0 : 1);
        String str2 = this.mDynamicNfcid2;
        if (str2 != null) {
            parcel.writeString(str2);
        }
        parcel.writeInt(this.mUid);
        parcel.writeString(this.mT3tPmm);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("    " + getComponent() + " (Description: " + getDescription() + ")");
        StringBuilder sb = new StringBuilder();
        sb.append("    System Code: ");
        sb.append(getSystemCode());
        printWriter.println(sb.toString());
        printWriter.println("    NFCID2: " + getNfcid2());
        printWriter.println("    T3tPmm: " + getT3tPmm());
    }
}
