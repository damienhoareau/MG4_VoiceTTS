package android.content.pm;

import android.annotation.SystemApi;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class PermissionInfo extends PackageItemInfo implements Parcelable {
    public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator<PermissionInfo>() { // from class: android.content.pm.PermissionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo createFromParcel(Parcel parcel) {
            return new PermissionInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo[] newArray(int i) {
            return new PermissionInfo[i];
        }
    };
    public static final int FLAG_COSTS_MONEY = 1;
    public static final int FLAG_INSTALLED = 1073741824;

    @SystemApi
    public static final int FLAG_REMOVED = 2;
    public static final int PROTECTION_DANGEROUS = 1;
    public static final int PROTECTION_FLAG_APPOP = 64;
    public static final int PROTECTION_FLAG_DEVELOPMENT = 32;
    public static final int PROTECTION_FLAG_INSTALLER = 256;
    public static final int PROTECTION_FLAG_INSTANT = 4096;

    @SystemApi
    public static final int PROTECTION_FLAG_OEM = 16384;
    public static final int PROTECTION_FLAG_PRE23 = 128;
    public static final int PROTECTION_FLAG_PREINSTALLED = 1024;
    public static final int PROTECTION_FLAG_PRIVILEGED = 16;
    public static final int PROTECTION_FLAG_RUNTIME_ONLY = 8192;
    public static final int PROTECTION_FLAG_SETUP = 2048;

    @Deprecated
    public static final int PROTECTION_FLAG_SYSTEM = 16;

    @SystemApi
    public static final int PROTECTION_FLAG_SYSTEM_TEXT_CLASSIFIER = 65536;
    public static final int PROTECTION_FLAG_VENDOR_PRIVILEGED = 32768;
    public static final int PROTECTION_FLAG_VERIFIER = 512;

    @Deprecated
    public static final int PROTECTION_MASK_BASE = 15;

    @Deprecated
    public static final int PROTECTION_MASK_FLAGS = 65520;
    public static final int PROTECTION_NORMAL = 0;
    public static final int PROTECTION_SIGNATURE = 2;

    @Deprecated
    public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;
    public int descriptionRes;
    public int flags;
    public String group;
    public CharSequence nonLocalizedDescription;

    @Deprecated
    public int protectionLevel;

    @SystemApi
    public int requestRes;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Protection {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProtectionFlags {
    }

    public static int fixProtectionLevel(int i) {
        if (i == 3) {
            i = 18;
        }
        return ((32768 & i) == 0 || (i & 16) != 0) ? i : i & (-32769);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static String protectionToString(int i) {
        String str;
        int i2 = i & 15;
        if (i2 == 0) {
            str = "normal";
        } else if (i2 == 1) {
            str = "dangerous";
        } else if (i2 != 2) {
            str = i2 != 3 ? "????" : "signatureOrSystem";
        } else {
            str = "signature";
        }
        if ((i & 16) != 0) {
            str = str + "|privileged";
        }
        if ((i & 32) != 0) {
            str = str + "|development";
        }
        if ((i & 64) != 0) {
            str = str + "|appop";
        }
        if ((i & 128) != 0) {
            str = str + "|pre23";
        }
        if ((i & 256) != 0) {
            str = str + "|installer";
        }
        if ((i & 512) != 0) {
            str = str + "|verifier";
        }
        if ((i & 1024) != 0) {
            str = str + "|preinstalled";
        }
        if ((i & 2048) != 0) {
            str = str + "|setup";
        }
        if ((i & 4096) != 0) {
            str = str + "|instant";
        }
        if ((i & 8192) != 0) {
            str = str + "|runtime";
        }
        if ((i & 16384) != 0) {
            str = str + "|oem";
        }
        if ((32768 & i) != 0) {
            str = str + "|vendorPrivileged";
        }
        if ((i & 65536) == 0) {
            return str;
        }
        return str + "|textClassifier";
    }

    public PermissionInfo() {
    }

    public PermissionInfo(PermissionInfo permissionInfo) {
        super(permissionInfo);
        this.protectionLevel = permissionInfo.protectionLevel;
        this.flags = permissionInfo.flags;
        this.group = permissionInfo.group;
        this.descriptionRes = permissionInfo.descriptionRes;
        this.requestRes = permissionInfo.requestRes;
        this.nonLocalizedDescription = permissionInfo.nonLocalizedDescription;
    }

    public CharSequence loadDescription(PackageManager packageManager) {
        CharSequence text;
        CharSequence charSequence = this.nonLocalizedDescription;
        if (charSequence != null) {
            return charSequence;
        }
        if (this.descriptionRes == 0 || (text = packageManager.getText(this.packageName, this.descriptionRes, null)) == null) {
            return null;
        }
        return text;
    }

    public int getProtection() {
        return this.protectionLevel & 15;
    }

    public int getProtectionFlags() {
        return this.protectionLevel & (-16);
    }

    public String toString() {
        return "PermissionInfo{" + Integer.toHexString(System.identityHashCode(this)) + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + this.name + "}";
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.protectionLevel);
        parcel.writeInt(this.flags);
        parcel.writeString(this.group);
        parcel.writeInt(this.descriptionRes);
        parcel.writeInt(this.requestRes);
        TextUtils.writeToParcel(this.nonLocalizedDescription, parcel, i);
    }

    public int calculateFootprint() {
        int length = this.name.length();
        if (this.nonLocalizedLabel != null) {
            length += this.nonLocalizedLabel.length();
        }
        CharSequence charSequence = this.nonLocalizedDescription;
        return charSequence != null ? length + charSequence.length() : length;
    }

    public boolean isAppOp() {
        return (this.protectionLevel & 64) != 0;
    }

    private PermissionInfo(Parcel parcel) {
        super(parcel);
        this.protectionLevel = parcel.readInt();
        this.flags = parcel.readInt();
        this.group = parcel.readString();
        this.descriptionRes = parcel.readInt();
        this.requestRes = parcel.readInt();
        this.nonLocalizedDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
    }
}
