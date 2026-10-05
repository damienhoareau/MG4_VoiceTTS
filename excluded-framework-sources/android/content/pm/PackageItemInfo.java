package android.content.pm;

import android.annotation.SystemApi;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Printer;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Collator;
import java.util.BitSet;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class PackageItemInfo {
    public static final int DUMP_FLAG_ALL = 3;
    public static final int DUMP_FLAG_APPLICATION = 2;
    public static final int DUMP_FLAG_DETAILS = 1;
    private static final int LINE_FEED_CODE_POINT = 10;
    private static final float MAX_LABEL_SIZE_PX = 500.0f;
    private static final int MAX_SAFE_LABEL_LENGTH = 50000;
    private static final int NBSP_CODE_POINT = 160;
    public static final int SAFE_LABEL_FLAG_FIRST_LINE = 4;
    public static final int SAFE_LABEL_FLAG_SINGLE_LINE = 2;
    public static final int SAFE_LABEL_FLAG_TRIM = 1;
    private static volatile boolean sForceSafeLabels = false;
    public int banner;
    public int icon;
    public int labelRes;
    public int logo;
    public Bundle metaData;
    public String name;
    public CharSequence nonLocalizedLabel;
    public String packageName;
    public int showUserIcon;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SafeLabelFlags {
    }

    protected void dumpBack(Printer printer, String str) {
    }

    protected ApplicationInfo getApplicationInfo() {
        return null;
    }

    protected Drawable loadDefaultBanner(PackageManager packageManager) {
        return null;
    }

    protected Drawable loadDefaultLogo(PackageManager packageManager) {
        return null;
    }

    public static void setForceSafeLabels(boolean z) {
        sForceSafeLabels = z;
    }

    public PackageItemInfo() {
        this.showUserIcon = -10000;
    }

    public PackageItemInfo(PackageItemInfo packageItemInfo) {
        String str = packageItemInfo.name;
        this.name = str;
        if (str != null) {
            this.name = str.trim();
        }
        this.packageName = packageItemInfo.packageName;
        this.labelRes = packageItemInfo.labelRes;
        CharSequence charSequence = packageItemInfo.nonLocalizedLabel;
        this.nonLocalizedLabel = charSequence;
        if (charSequence != null) {
            this.nonLocalizedLabel = charSequence.toString().trim();
        }
        this.icon = packageItemInfo.icon;
        this.banner = packageItemInfo.banner;
        this.logo = packageItemInfo.logo;
        this.metaData = packageItemInfo.metaData;
        this.showUserIcon = packageItemInfo.showUserIcon;
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        if (sForceSafeLabels) {
            return loadSafeLabel(packageManager);
        }
        return loadUnsafeLabel(packageManager);
    }

    public CharSequence loadUnsafeLabel(PackageManager packageManager) {
        CharSequence text;
        CharSequence charSequence = this.nonLocalizedLabel;
        if (charSequence != null) {
            return charSequence;
        }
        int i = this.labelRes;
        if (i != 0 && (text = packageManager.getText(this.packageName, i, getApplicationInfo())) != null) {
            return text.toString().trim();
        }
        String str = this.name;
        return str != null ? str : this.packageName;
    }

    @SystemApi
    public CharSequence loadSafeLabel(PackageManager packageManager) {
        String string = Html.fromHtml(loadUnsafeLabel(packageManager).toString()).toString();
        int iMin = Math.min(string.length(), 50000);
        StringBuffer stringBuffer = new StringBuffer(iMin);
        int i = 0;
        while (i < iMin) {
            int iCodePointAt = string.codePointAt(i);
            int type = Character.getType(iCodePointAt);
            if (type == 13 || type == 15 || type == 14) {
                string.substring(0, i);
                break;
            }
            int iCharCount = Character.charCount(iCodePointAt);
            if (type == 12) {
                stringBuffer.append(' ');
            } else {
                stringBuffer.append(string.charAt(i));
                if (iCharCount == 2) {
                    stringBuffer.append(string.charAt(i + 1));
                }
            }
            i += iCharCount;
        }
        String strTrim = stringBuffer.toString().trim();
        if (strTrim.isEmpty()) {
            return this.packageName;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(42.0f);
        return TextUtils.ellipsize(strTrim, textPaint, MAX_LABEL_SIZE_PX, TextUtils.TruncateAt.END);
    }

    private static boolean isNewline(int i) {
        int type = Character.getType(i);
        return type == 14 || type == 13 || i == 10;
    }

    private static boolean isWhiteSpace(int i) {
        return Character.isWhitespace(i) || i == 160;
    }

    private static class StringWithRemovedChars {
        private final String mOriginal;
        private BitSet mRemovedChars;

        StringWithRemovedChars(String str) {
            this.mOriginal = str;
        }

        void removeRange(int i, int i2) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(i, i2);
        }

        void removeAllCharBefore(int i) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(0, i);
        }

        void removeAllCharAfter(int i) {
            if (this.mRemovedChars == null) {
                this.mRemovedChars = new BitSet(this.mOriginal.length());
            }
            this.mRemovedChars.set(i, this.mOriginal.length());
        }

        public String toString() {
            if (this.mRemovedChars == null) {
                return this.mOriginal;
            }
            StringBuilder sb = new StringBuilder(this.mOriginal.length());
            for (int i = 0; i < this.mOriginal.length(); i++) {
                if (!this.mRemovedChars.get(i)) {
                    sb.append(this.mOriginal.charAt(i));
                }
            }
            return sb.toString();
        }

        int length() {
            return this.mOriginal.length();
        }

        boolean isRemoved(int i) {
            BitSet bitSet = this.mRemovedChars;
            return bitSet != null && bitSet.get(i);
        }

        int codePointAt(int i) {
            return this.mOriginal.codePointAt(i);
        }
    }

    public CharSequence loadSafeLabel(PackageManager packageManager, float f, int i) {
        boolean z = true;
        boolean z2 = (i & 4) != 0;
        boolean z3 = (i & 2) != 0;
        boolean z4 = (i & 1) != 0;
        Preconditions.checkNotNull(packageManager);
        Preconditions.checkArgument(f >= 0.0f);
        Preconditions.checkFlagsArgument(i, 7);
        if (z2 && z3) {
            z = false;
        }
        Preconditions.checkArgument(z, "Cannot set SAFE_LABEL_FLAG_SINGLE_LINE and SAFE_LABEL_FLAG_FIRST_LINE at the same time");
        StringWithRemovedChars stringWithRemovedChars = new StringWithRemovedChars(Html.fromHtml(loadUnsafeLabel(packageManager).toString()).toString());
        int length = stringWithRemovedChars.length();
        int i2 = -1;
        int i3 = -1;
        int i4 = 0;
        while (i4 < length) {
            int iCodePointAt = stringWithRemovedChars.codePointAt(i4);
            int type = Character.getType(iCodePointAt);
            int iCharCount = Character.charCount(iCodePointAt);
            boolean zIsNewline = isNewline(iCodePointAt);
            if (i4 > 50000 || (z2 && zIsNewline)) {
                stringWithRemovedChars.removeAllCharAfter(i4);
                break;
            }
            if (z3 && zIsNewline) {
                stringWithRemovedChars.removeRange(i4, i4 + iCharCount);
            } else if (type == 15 && !zIsNewline) {
                stringWithRemovedChars.removeRange(i4, i4 + iCharCount);
            } else if (z4 && !isWhiteSpace(iCodePointAt)) {
                if (i2 == -1) {
                    i2 = i4;
                }
                i3 = i4 + iCharCount;
            }
            i4 += iCharCount;
        }
        if (z4) {
            if (i2 == -1) {
                stringWithRemovedChars.removeAllCharAfter(0);
            } else {
                if (i2 > 0) {
                    stringWithRemovedChars.removeAllCharBefore(i2);
                }
                if (i3 < length) {
                    stringWithRemovedChars.removeAllCharAfter(i3);
                }
            }
        }
        if (f == 0.0f) {
            return stringWithRemovedChars.toString();
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(42.0f);
        return TextUtils.ellipsize(stringWithRemovedChars.toString(), textPaint, f, TextUtils.TruncateAt.END);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return packageManager.loadItemIcon(this, getApplicationInfo());
    }

    public Drawable loadUnbadgedIcon(PackageManager packageManager) {
        return packageManager.loadUnbadgedItemIcon(this, getApplicationInfo());
    }

    public Drawable loadBanner(PackageManager packageManager) {
        Drawable drawable;
        int i = this.banner;
        return (i == 0 || (drawable = packageManager.getDrawable(this.packageName, i, getApplicationInfo())) == null) ? loadDefaultBanner(packageManager) : drawable;
    }

    public Drawable loadDefaultIcon(PackageManager packageManager) {
        return packageManager.getDefaultActivityIcon();
    }

    public Drawable loadLogo(PackageManager packageManager) {
        Drawable drawable;
        int i = this.logo;
        return (i == 0 || (drawable = packageManager.getDrawable(this.packageName, i, getApplicationInfo())) == null) ? loadDefaultLogo(packageManager) : drawable;
    }

    public XmlResourceParser loadXmlMetaData(PackageManager packageManager, String str) {
        int i;
        Bundle bundle = this.metaData;
        if (bundle == null || (i = bundle.getInt(str)) == 0) {
            return null;
        }
        return packageManager.getXml(this.packageName, i, getApplicationInfo());
    }

    protected void dumpFront(Printer printer, String str) {
        if (this.name != null) {
            printer.println(str + "name=" + this.name);
        }
        printer.println(str + "packageName=" + this.packageName);
        if (this.labelRes == 0 && this.nonLocalizedLabel == null && this.icon == 0 && this.banner == 0) {
            return;
        }
        printer.println(str + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + ((Object) this.nonLocalizedLabel) + " icon=0x" + Integer.toHexString(this.icon) + " banner=0x" + Integer.toHexString(this.banner));
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, parcel, i);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.logo);
        parcel.writeBundle(this.metaData);
        parcel.writeInt(this.banner);
        parcel.writeInt(this.showUserIcon);
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        String str = this.name;
        if (str != null) {
            protoOutputStream.write(1138166333441L, str);
        }
        protoOutputStream.write(1138166333442L, this.packageName);
        if (this.labelRes != 0 || this.nonLocalizedLabel != null || this.icon != 0 || this.banner != 0) {
            protoOutputStream.write(1120986464259L, this.labelRes);
            protoOutputStream.write(1138166333444L, this.nonLocalizedLabel.toString());
            protoOutputStream.write(1120986464261L, this.icon);
            protoOutputStream.write(1120986464262L, this.banner);
        }
        protoOutputStream.end(jStart);
    }

    protected PackageItemInfo(Parcel parcel) {
        this.name = parcel.readString();
        this.packageName = parcel.readString();
        this.labelRes = parcel.readInt();
        this.nonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.icon = parcel.readInt();
        this.logo = parcel.readInt();
        this.metaData = parcel.readBundle();
        this.banner = parcel.readInt();
        this.showUserIcon = parcel.readInt();
    }

    public static class DisplayNameComparator implements Comparator<PackageItemInfo> {
        private PackageManager mPM;
        private final Collator sCollator = Collator.getInstance();

        public DisplayNameComparator(PackageManager packageManager) {
            this.mPM = packageManager;
        }

        @Override // java.util.Comparator
        public final int compare(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
            CharSequence charSequenceLoadLabel = packageItemInfo.loadLabel(this.mPM);
            if (charSequenceLoadLabel == null) {
                charSequenceLoadLabel = packageItemInfo.name;
            }
            CharSequence charSequenceLoadLabel2 = packageItemInfo2.loadLabel(this.mPM);
            if (charSequenceLoadLabel2 == null) {
                charSequenceLoadLabel2 = packageItemInfo2.name;
            }
            return this.sCollator.compare(charSequenceLoadLabel.toString(), charSequenceLoadLabel2.toString());
        }
    }
}
