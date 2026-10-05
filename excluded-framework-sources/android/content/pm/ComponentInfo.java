package android.content.pm;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Printer;

/* JADX INFO: loaded from: classes.dex */
public class ComponentInfo extends PackageItemInfo {
    public ApplicationInfo applicationInfo;
    public int descriptionRes;
    public boolean directBootAware;
    public boolean enabled;

    @Deprecated
    public boolean encryptionAware;
    public boolean exported;
    public String processName;
    public String splitName;

    public ComponentInfo() {
        this.enabled = true;
        this.exported = false;
        this.directBootAware = false;
        this.encryptionAware = false;
    }

    public ComponentInfo(ComponentInfo componentInfo) {
        super(componentInfo);
        this.enabled = true;
        this.exported = false;
        this.directBootAware = false;
        this.encryptionAware = false;
        this.applicationInfo = componentInfo.applicationInfo;
        this.processName = componentInfo.processName;
        this.splitName = componentInfo.splitName;
        this.descriptionRes = componentInfo.descriptionRes;
        this.enabled = componentInfo.enabled;
        this.exported = componentInfo.exported;
        boolean z = componentInfo.directBootAware;
        this.directBootAware = z;
        this.encryptionAware = z;
    }

    @Override // android.content.pm.PackageItemInfo
    public CharSequence loadUnsafeLabel(PackageManager packageManager) {
        CharSequence text;
        CharSequence text2;
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        ApplicationInfo applicationInfo = this.applicationInfo;
        if (this.labelRes != 0 && (text2 = packageManager.getText(this.packageName, this.labelRes, applicationInfo)) != null) {
            return text2;
        }
        if (applicationInfo.nonLocalizedLabel != null) {
            return applicationInfo.nonLocalizedLabel;
        }
        return (applicationInfo.labelRes == 0 || (text = packageManager.getText(this.packageName, applicationInfo.labelRes, applicationInfo)) == null) ? this.name : text;
    }

    public boolean isEnabled() {
        return this.enabled && this.applicationInfo.enabled;
    }

    public final int getIconResource() {
        return this.icon != 0 ? this.icon : this.applicationInfo.icon;
    }

    public final int getLogoResource() {
        return this.logo != 0 ? this.logo : this.applicationInfo.logo;
    }

    public final int getBannerResource() {
        return this.banner != 0 ? this.banner : this.applicationInfo.banner;
    }

    public ComponentName getComponentName() {
        return new ComponentName(this.packageName, this.name);
    }

    @Override // android.content.pm.PackageItemInfo
    protected void dumpFront(Printer printer, String str) {
        super.dumpFront(printer, str);
        if (this.processName != null && !this.packageName.equals(this.processName)) {
            printer.println(str + "processName=" + this.processName);
        }
        if (this.splitName != null) {
            printer.println(str + "splitName=" + this.splitName);
        }
        printer.println(str + "enabled=" + this.enabled + " exported=" + this.exported + " directBootAware=" + this.directBootAware);
        if (this.descriptionRes != 0) {
            printer.println(str + "description=" + this.descriptionRes);
        }
    }

    @Override // android.content.pm.PackageItemInfo
    protected void dumpBack(Printer printer, String str) {
        dumpBack(printer, str, 3);
    }

    void dumpBack(Printer printer, String str, int i) {
        if ((i & 2) != 0) {
            if (this.applicationInfo != null) {
                printer.println(str + "ApplicationInfo:");
                this.applicationInfo.dump(printer, str + "  ", i);
            } else {
                printer.println(str + "ApplicationInfo: null");
            }
        }
        super.dumpBack(printer, str);
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        if ((i & 2) != 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            this.applicationInfo.writeToParcel(parcel, i);
        }
        parcel.writeString(this.processName);
        parcel.writeString(this.splitName);
        parcel.writeInt(this.descriptionRes);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.exported ? 1 : 0);
        parcel.writeInt(this.directBootAware ? 1 : 0);
    }

    protected ComponentInfo(Parcel parcel) {
        super(parcel);
        this.enabled = true;
        this.exported = false;
        this.directBootAware = false;
        this.encryptionAware = false;
        if (parcel.readInt() != 0) {
            this.applicationInfo = ApplicationInfo.CREATOR.createFromParcel(parcel);
        }
        this.processName = parcel.readString();
        this.splitName = parcel.readString();
        this.descriptionRes = parcel.readInt();
        this.enabled = parcel.readInt() != 0;
        this.exported = parcel.readInt() != 0;
        boolean z = parcel.readInt() != 0;
        this.directBootAware = z;
        this.encryptionAware = z;
    }

    @Override // android.content.pm.PackageItemInfo
    public Drawable loadDefaultIcon(PackageManager packageManager) {
        return this.applicationInfo.loadIcon(packageManager);
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultBanner(PackageManager packageManager) {
        return this.applicationInfo.loadBanner(packageManager);
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultLogo(PackageManager packageManager) {
        return this.applicationInfo.loadLogo(packageManager);
    }

    @Override // android.content.pm.PackageItemInfo
    protected ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }
}
