package android.content.res;

import android.app.WindowConfiguration;
import android.app.slice.Slice;
import android.content.ConfigurationProto;
import android.content.ResourcesConfigurationProto;
import android.hardware.Camera;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.proto.ProtoOutputStream;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public final class Configuration implements Parcelable, Comparable<Configuration> {
    public static final int ASSETS_SEQ_UNDEFINED = 0;
    public static final int COLOR_MODE_HDR_MASK = 12;
    public static final int COLOR_MODE_HDR_NO = 4;
    public static final int COLOR_MODE_HDR_SHIFT = 2;
    public static final int COLOR_MODE_HDR_UNDEFINED = 0;
    public static final int COLOR_MODE_HDR_YES = 8;
    public static final int COLOR_MODE_UNDEFINED = 0;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_MASK = 3;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_NO = 1;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_UNDEFINED = 0;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT_YES = 2;
    public static final int DENSITY_DPI_ANY = 65534;
    public static final int DENSITY_DPI_NONE = 65535;
    public static final int DENSITY_DPI_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_NO = 1;
    public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARDHIDDEN_NO = 1;
    public static final int KEYBOARDHIDDEN_SOFT = 3;
    public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int KEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARD_12KEY = 3;
    public static final int KEYBOARD_NOKEYS = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_UNDEFINED = 0;
    public static final int MNC_ZERO = 65535;
    public static final int NATIVE_CONFIG_COLOR_MODE = 65536;
    public static final int NATIVE_CONFIG_DENSITY = 256;
    public static final int NATIVE_CONFIG_KEYBOARD = 16;
    public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
    public static final int NATIVE_CONFIG_LOCALE = 4;
    public static final int NATIVE_CONFIG_MCC = 1;
    public static final int NATIVE_CONFIG_MNC = 2;
    public static final int NATIVE_CONFIG_NAVIGATION = 64;
    public static final int NATIVE_CONFIG_ORIENTATION = 128;
    public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
    public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
    public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
    public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
    public static final int NATIVE_CONFIG_UI_MODE = 4096;
    public static final int NATIVE_CONFIG_VERSION = 1024;
    public static final int NAVIGATIONHIDDEN_NO = 1;
    public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
    public static final int NAVIGATIONHIDDEN_YES = 2;
    public static final int NAVIGATION_DPAD = 2;
    public static final int NAVIGATION_NONAV = 1;
    public static final int NAVIGATION_TRACKBALL = 3;
    public static final int NAVIGATION_UNDEFINED = 0;
    public static final int NAVIGATION_WHEEL = 4;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;

    @Deprecated
    public static final int ORIENTATION_SQUARE = 3;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_MASK = 48;
    public static final int SCREENLAYOUT_LONG_NO = 16;
    public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_YES = 32;
    public static final int SCREENLAYOUT_ROUND_MASK = 768;
    public static final int SCREENLAYOUT_ROUND_NO = 256;
    public static final int SCREENLAYOUT_ROUND_SHIFT = 8;
    public static final int SCREENLAYOUT_ROUND_UNDEFINED = 0;
    public static final int SCREENLAYOUT_ROUND_YES = 512;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_MASK = 15;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public static final int SCREENLAYOUT_UNDEFINED = 0;
    public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
    public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int TOUCHSCREEN_FINGER = 3;
    public static final int TOUCHSCREEN_NOTOUCH = 1;

    @Deprecated
    public static final int TOUCHSCREEN_STYLUS = 2;
    public static final int TOUCHSCREEN_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_YES = 32;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_MASK = 15;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_VR_HEADSET = 7;
    public static final int UI_MODE_TYPE_WATCH = 6;
    private static final String XML_ATTR_APP_BOUNDS = "app_bounds";
    private static final String XML_ATTR_COLOR_MODE = "clrMod";
    private static final String XML_ATTR_DENSITY = "density";
    private static final String XML_ATTR_FONT_SCALE = "fs";
    private static final String XML_ATTR_HARD_KEYBOARD_HIDDEN = "hardKeyHid";
    private static final String XML_ATTR_KEYBOARD = "key";
    private static final String XML_ATTR_KEYBOARD_HIDDEN = "keyHid";
    private static final String XML_ATTR_LOCALES = "locales";
    private static final String XML_ATTR_MCC = "mcc";
    private static final String XML_ATTR_MNC = "mnc";
    private static final String XML_ATTR_NAVIGATION = "nav";
    private static final String XML_ATTR_NAVIGATION_HIDDEN = "navHid";
    private static final String XML_ATTR_ORIENTATION = "ori";
    private static final String XML_ATTR_ROTATION = "rot";
    private static final String XML_ATTR_SCREEN_HEIGHT = "height";
    private static final String XML_ATTR_SCREEN_LAYOUT = "scrLay";
    private static final String XML_ATTR_SCREEN_WIDTH = "width";
    private static final String XML_ATTR_SMALLEST_WIDTH = "sw";
    private static final String XML_ATTR_TOUCHSCREEN = "touch";
    private static final String XML_ATTR_UI_MODE = "ui";
    public int assetsSeq;
    public int colorMode;
    public int compatScreenHeightDp;
    public int compatScreenWidthDp;
    public int compatSmallestScreenWidthDp;
    public int densityDpi;
    public float fontScale;
    public int hardKeyboardHidden;
    public int keyboard;
    public int keyboardHidden;

    @Deprecated
    public Locale locale;
    private LocaleList mLocaleList;
    public int mcc;
    public int mnc;
    public int navigation;
    public int navigationHidden;
    public int orientation;
    public int screenHeightDp;
    public int screenLayout;
    public int screenWidthDp;
    public int seq;
    public int smallestScreenWidthDp;
    public int touchscreen;
    public int uiMode;
    public boolean userSetLocale;
    public final WindowConfiguration windowConfiguration;
    public static final Configuration EMPTY = new Configuration();
    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() { // from class: android.content.res.Configuration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration createFromParcel(Parcel parcel) {
            return new Configuration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration[] newArray(int i) {
            return new Configuration[i];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    public @interface NativeConfig {
    }

    private static int getScreenLayoutNoDirection(int i) {
        return i & (-193);
    }

    public static boolean needNewResources(int i, int i2) {
        return (i & ((i2 | Integer.MIN_VALUE) | 1073741824)) != 0;
    }

    public static int resetScreenLayout(int i) {
        return (i & (-268435520)) | 36;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static int reduceScreenLayout(int i, int i2, int i3) {
        int i4;
        boolean z;
        boolean z2 = false;
        int i5 = 1;
        if (i2 < 470) {
            z = false;
        } else {
            if (i2 < 960 || i3 < 720) {
                i4 = (i2 < 640 || i3 < 480) ? 2 : 3;
            } else {
                i4 = 4;
            }
            z = i3 > 321 || i2 > 570;
            z2 = (i2 * 3) / 5 >= i3 - 1;
            i5 = i4;
        }
        if (!z2) {
            i = (i & (-49)) | 16;
        }
        if (z) {
            i |= 268435456;
        }
        return i5 < (i & 15) ? (i & (-16)) | i5 : i;
    }

    public static String configurationDiffToString(int i) {
        ArrayList arrayList = new ArrayList();
        if ((i & 1) != 0) {
            arrayList.add("CONFIG_MCC");
        }
        if ((i & 2) != 0) {
            arrayList.add("CONFIG_MNC");
        }
        if ((i & 4) != 0) {
            arrayList.add("CONFIG_LOCALE");
        }
        if ((i & 8) != 0) {
            arrayList.add("CONFIG_TOUCHSCREEN");
        }
        if ((i & 16) != 0) {
            arrayList.add("CONFIG_KEYBOARD");
        }
        if ((i & 32) != 0) {
            arrayList.add("CONFIG_KEYBOARD_HIDDEN");
        }
        if ((i & 64) != 0) {
            arrayList.add("CONFIG_NAVIGATION");
        }
        if ((i & 128) != 0) {
            arrayList.add("CONFIG_ORIENTATION");
        }
        if ((i & 256) != 0) {
            arrayList.add("CONFIG_SCREEN_LAYOUT");
        }
        if ((i & 16384) != 0) {
            arrayList.add("CONFIG_COLOR_MODE");
        }
        if ((i & 512) != 0) {
            arrayList.add("CONFIG_UI_MODE");
        }
        if ((i & 1024) != 0) {
            arrayList.add("CONFIG_SCREEN_SIZE");
        }
        if ((i & 2048) != 0) {
            arrayList.add("CONFIG_SMALLEST_SCREEN_SIZE");
        }
        if ((i & 8192) != 0) {
            arrayList.add("CONFIG_LAYOUT_DIRECTION");
        }
        if ((1073741824 & i) != 0) {
            arrayList.add("CONFIG_FONT_SCALE");
        }
        if ((i & Integer.MIN_VALUE) != 0) {
            arrayList.add("CONFIG_ASSETS_PATHS");
        }
        StringBuilder sb = new StringBuilder("{");
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            sb.append((String) arrayList.get(i2));
            if (i2 != size - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean isLayoutSizeAtLeast(int i) {
        int i2 = this.screenLayout & 15;
        return i2 != 0 && i2 >= i;
    }

    public Configuration() {
        this.windowConfiguration = new WindowConfiguration();
        unset();
    }

    public Configuration(Configuration configuration) {
        this.windowConfiguration = new WindowConfiguration();
        setTo(configuration);
    }

    private void fixUpLocaleList() {
        Locale locale;
        if ((this.locale != null || this.mLocaleList.isEmpty()) && ((locale = this.locale) == null || locale.equals(this.mLocaleList.get(0)))) {
            return;
        }
        this.mLocaleList = this.locale == null ? LocaleList.getEmptyLocaleList() : new LocaleList(this.locale);
    }

    public void setTo(Configuration configuration) {
        this.fontScale = configuration.fontScale;
        this.mcc = configuration.mcc;
        this.mnc = configuration.mnc;
        Locale locale = configuration.locale;
        this.locale = locale == null ? null : (Locale) locale.clone();
        configuration.fixUpLocaleList();
        this.mLocaleList = configuration.mLocaleList;
        this.userSetLocale = configuration.userSetLocale;
        this.touchscreen = configuration.touchscreen;
        this.keyboard = configuration.keyboard;
        this.keyboardHidden = configuration.keyboardHidden;
        this.hardKeyboardHidden = configuration.hardKeyboardHidden;
        this.navigation = configuration.navigation;
        this.navigationHidden = configuration.navigationHidden;
        this.orientation = configuration.orientation;
        this.screenLayout = configuration.screenLayout;
        this.colorMode = configuration.colorMode;
        this.uiMode = configuration.uiMode;
        this.screenWidthDp = configuration.screenWidthDp;
        this.screenHeightDp = configuration.screenHeightDp;
        this.smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        this.densityDpi = configuration.densityDpi;
        this.compatScreenWidthDp = configuration.compatScreenWidthDp;
        this.compatScreenHeightDp = configuration.compatScreenHeightDp;
        this.compatSmallestScreenWidthDp = configuration.compatSmallestScreenWidthDp;
        this.assetsSeq = configuration.assetsSeq;
        this.seq = configuration.seq;
        this.windowConfiguration.setTo(configuration.windowConfiguration);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append(this.fontScale);
        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        int i = this.mcc;
        if (i != 0) {
            sb.append(i);
            sb.append("mcc");
        } else {
            sb.append("?mcc");
        }
        int i2 = this.mnc;
        if (i2 != 0) {
            sb.append(i2);
            sb.append("mnc");
        } else {
            sb.append("?mnc");
        }
        fixUpLocaleList();
        if (!this.mLocaleList.isEmpty()) {
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            sb.append(this.mLocaleList);
        } else {
            sb.append(" ?localeList");
        }
        int i3 = this.screenLayout & 192;
        if (i3 == 0) {
            sb.append(" ?layoutDir");
        } else if (i3 == 64) {
            sb.append(" ldltr");
        } else if (i3 == 128) {
            sb.append(" ldrtl");
        } else {
            sb.append(" layoutDir=");
            sb.append(i3 >> 6);
        }
        if (this.smallestScreenWidthDp != 0) {
            sb.append(" sw");
            sb.append(this.smallestScreenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?swdp");
        }
        if (this.screenWidthDp != 0) {
            sb.append(" w");
            sb.append(this.screenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?wdp");
        }
        if (this.screenHeightDp != 0) {
            sb.append(" h");
            sb.append(this.screenHeightDp);
            sb.append("dp");
        } else {
            sb.append(" ?hdp");
        }
        if (this.densityDpi != 0) {
            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
            sb.append(this.densityDpi);
            sb.append("dpi");
        } else {
            sb.append(" ?density");
        }
        int i4 = this.screenLayout & 15;
        if (i4 == 0) {
            sb.append(" ?lsize");
        } else if (i4 == 1) {
            sb.append(" smll");
        } else if (i4 == 2) {
            sb.append(" nrml");
        } else if (i4 == 3) {
            sb.append(" lrg");
        } else if (i4 == 4) {
            sb.append(" xlrg");
        } else {
            sb.append(" layoutSize=");
            sb.append(this.screenLayout & 15);
        }
        int i5 = this.screenLayout & 48;
        if (i5 == 0) {
            sb.append(" ?long");
        } else if (i5 != 16) {
            if (i5 == 32) {
                sb.append(" long");
            } else {
                sb.append(" layoutLong=");
                sb.append(this.screenLayout & 48);
            }
        }
        int i6 = this.colorMode & 12;
        if (i6 == 0) {
            sb.append(" ?ldr");
        } else if (i6 != 4) {
            if (i6 == 8) {
                sb.append(" hdr");
            } else {
                sb.append(" dynamicRange=");
                sb.append(this.colorMode & 12);
            }
        }
        int i7 = this.colorMode & 3;
        if (i7 == 0) {
            sb.append(" ?wideColorGamut");
        } else if (i7 != 1) {
            if (i7 == 2) {
                sb.append(" widecg");
            } else {
                sb.append(" wideColorGamut=");
                sb.append(this.colorMode & 3);
            }
        }
        int i8 = this.orientation;
        if (i8 == 0) {
            sb.append(" ?orien");
        } else if (i8 == 1) {
            sb.append(" port");
        } else if (i8 == 2) {
            sb.append(" land");
        } else {
            sb.append(" orien=");
            sb.append(this.orientation);
        }
        switch (this.uiMode & 15) {
            case 0:
                sb.append(" ?uimode");
                break;
            case 1:
                break;
            case 2:
                sb.append(" desk");
                break;
            case 3:
                sb.append(" car");
                break;
            case 4:
                sb.append(" television");
                break;
            case 5:
                sb.append(" appliance");
                break;
            case 6:
                sb.append(" watch");
                break;
            case 7:
                sb.append(" vrheadset");
                break;
            default:
                sb.append(" uimode=");
                sb.append(this.uiMode & 15);
                break;
        }
        int i9 = this.uiMode & 48;
        if (i9 == 0) {
            sb.append(" ?night");
        } else if (i9 != 16) {
            if (i9 == 32) {
                sb.append(" night");
            } else {
                sb.append(" night=");
                sb.append(this.uiMode & 48);
            }
        }
        int i10 = this.touchscreen;
        if (i10 == 0) {
            sb.append(" ?touch");
        } else if (i10 == 1) {
            sb.append(" -touch");
        } else if (i10 == 2) {
            sb.append(" stylus");
        } else if (i10 == 3) {
            sb.append(" finger");
        } else {
            sb.append(" touch=");
            sb.append(this.touchscreen);
        }
        int i11 = this.keyboard;
        if (i11 == 0) {
            sb.append(" ?keyb");
        } else if (i11 == 1) {
            sb.append(" -keyb");
        } else if (i11 == 2) {
            sb.append(" qwerty");
        } else if (i11 == 3) {
            sb.append(" 12key");
        } else {
            sb.append(" keys=");
            sb.append(this.keyboard);
        }
        int i12 = this.keyboardHidden;
        if (i12 == 0) {
            sb.append("/?");
        } else if (i12 == 1) {
            sb.append("/v");
        } else if (i12 == 2) {
            sb.append("/h");
        } else if (i12 == 3) {
            sb.append("/s");
        } else {
            sb.append("/");
            sb.append(this.keyboardHidden);
        }
        int i13 = this.hardKeyboardHidden;
        if (i13 == 0) {
            sb.append("/?");
        } else if (i13 == 1) {
            sb.append("/v");
        } else if (i13 == 2) {
            sb.append("/h");
        } else {
            sb.append("/");
            sb.append(this.hardKeyboardHidden);
        }
        int i14 = this.navigation;
        if (i14 == 0) {
            sb.append(" ?nav");
        } else if (i14 == 1) {
            sb.append(" -nav");
        } else if (i14 == 2) {
            sb.append(" dpad");
        } else if (i14 == 3) {
            sb.append(" tball");
        } else if (i14 == 4) {
            sb.append(" wheel");
        } else {
            sb.append(" nav=");
            sb.append(this.navigation);
        }
        int i15 = this.navigationHidden;
        if (i15 == 0) {
            sb.append("/?");
        } else if (i15 == 1) {
            sb.append("/v");
        } else if (i15 == 2) {
            sb.append("/h");
        } else {
            sb.append("/");
            sb.append(this.navigationHidden);
        }
        sb.append(" winConfig=");
        sb.append(this.windowConfiguration);
        if (this.assetsSeq != 0) {
            sb.append(" as.");
            sb.append(this.assetsSeq);
        }
        if (this.seq != 0) {
            sb.append(" s.");
            sb.append(this.seq);
        }
        sb.append('}');
        return sb.toString();
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1108101562369L, this.fontScale);
        protoOutputStream.write(1155346202626L, this.mcc);
        protoOutputStream.write(1155346202627L, this.mnc);
        this.mLocaleList.writeToProto(protoOutputStream, 2246267895812L);
        protoOutputStream.write(1155346202629L, this.screenLayout);
        protoOutputStream.write(1155346202630L, this.colorMode);
        protoOutputStream.write(ConfigurationProto.TOUCHSCREEN, this.touchscreen);
        protoOutputStream.write(1155346202632L, this.keyboard);
        protoOutputStream.write(ConfigurationProto.KEYBOARD_HIDDEN, this.keyboardHidden);
        protoOutputStream.write(ConfigurationProto.HARD_KEYBOARD_HIDDEN, this.hardKeyboardHidden);
        protoOutputStream.write(ConfigurationProto.NAVIGATION, this.navigation);
        protoOutputStream.write(ConfigurationProto.NAVIGATION_HIDDEN, this.navigationHidden);
        protoOutputStream.write(ConfigurationProto.ORIENTATION, this.orientation);
        protoOutputStream.write(ConfigurationProto.UI_MODE, this.uiMode);
        protoOutputStream.write(ConfigurationProto.SCREEN_WIDTH_DP, this.screenWidthDp);
        protoOutputStream.write(ConfigurationProto.SCREEN_HEIGHT_DP, this.screenHeightDp);
        protoOutputStream.write(ConfigurationProto.SMALLEST_SCREEN_WIDTH_DP, this.smallestScreenWidthDp);
        protoOutputStream.write(ConfigurationProto.DENSITY_DPI, this.densityDpi);
        this.windowConfiguration.writeToProto(protoOutputStream, 1146756268051L);
        protoOutputStream.end(jStart);
    }

    public void writeResConfigToProto(ProtoOutputStream protoOutputStream, long j, DisplayMetrics displayMetrics) {
        int i;
        int i2;
        if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
            i = displayMetrics.widthPixels;
            i2 = displayMetrics.heightPixels;
        } else {
            i = displayMetrics.heightPixels;
            i2 = displayMetrics.widthPixels;
        }
        long jStart = protoOutputStream.start(j);
        writeToProto(protoOutputStream, 1146756268033L);
        protoOutputStream.write(1155346202626L, Build.VERSION.RESOURCES_SDK_INT);
        protoOutputStream.write(1155346202627L, i);
        protoOutputStream.write(ResourcesConfigurationProto.SCREEN_HEIGHT_PX, i2);
        protoOutputStream.end(jStart);
    }

    public static String uiModeToString(int i) {
        switch (i) {
            case 0:
                return "UI_MODE_TYPE_UNDEFINED";
            case 1:
                return "UI_MODE_TYPE_NORMAL";
            case 2:
                return "UI_MODE_TYPE_DESK";
            case 3:
                return "UI_MODE_TYPE_CAR";
            case 4:
                return "UI_MODE_TYPE_TELEVISION";
            case 5:
                return "UI_MODE_TYPE_APPLIANCE";
            case 6:
                return "UI_MODE_TYPE_WATCH";
            case 7:
                return "UI_MODE_TYPE_VR_HEADSET";
            default:
                return Integer.toString(i);
        }
    }

    public void setToDefaults() {
        this.fontScale = 1.0f;
        this.mnc = 0;
        this.mcc = 0;
        this.mLocaleList = LocaleList.getEmptyLocaleList();
        this.locale = null;
        this.userSetLocale = false;
        this.touchscreen = 0;
        this.keyboard = 0;
        this.keyboardHidden = 0;
        this.hardKeyboardHidden = 0;
        this.navigation = 0;
        this.navigationHidden = 0;
        this.orientation = 0;
        this.screenLayout = 0;
        this.colorMode = 0;
        this.uiMode = 0;
        this.compatScreenWidthDp = 0;
        this.screenWidthDp = 0;
        this.compatScreenHeightDp = 0;
        this.screenHeightDp = 0;
        this.compatSmallestScreenWidthDp = 0;
        this.smallestScreenWidthDp = 0;
        this.densityDpi = 0;
        this.assetsSeq = 0;
        this.seq = 0;
        this.windowConfiguration.setToDefaults();
    }

    public void unset() {
        setToDefaults();
        this.fontScale = 0.0f;
    }

    @Deprecated
    public void makeDefault() {
        setToDefaults();
    }

    public int updateFrom(Configuration configuration) {
        int i;
        int i2;
        float f = configuration.fontScale;
        if (f <= 0.0f || this.fontScale == f) {
            i = 0;
        } else {
            i = 1073741824;
            this.fontScale = f;
        }
        int i3 = configuration.mcc;
        if (i3 != 0 && this.mcc != i3) {
            i |= 1;
            this.mcc = i3;
        }
        int i4 = configuration.mnc;
        if (i4 != 0 && this.mnc != i4) {
            i |= 2;
            this.mnc = i4;
        }
        fixUpLocaleList();
        configuration.fixUpLocaleList();
        if (!configuration.mLocaleList.isEmpty() && !this.mLocaleList.equals(configuration.mLocaleList)) {
            i |= 4;
            this.mLocaleList = configuration.mLocaleList;
            if (!configuration.locale.equals(this.locale)) {
                Locale locale = (Locale) configuration.locale.clone();
                this.locale = locale;
                i |= 8192;
                setLayoutDirection(locale);
            }
        }
        int i5 = configuration.screenLayout & 192;
        if (i5 != 0) {
            int i6 = this.screenLayout;
            if (i5 != (i6 & 192)) {
                this.screenLayout = i5 | (i6 & (-193));
                i |= 8192;
            }
        }
        if (configuration.userSetLocale && (!this.userSetLocale || (i & 4) != 0)) {
            i |= 4;
            this.userSetLocale = true;
        }
        int i7 = configuration.touchscreen;
        if (i7 != 0 && this.touchscreen != i7) {
            i |= 8;
            this.touchscreen = i7;
        }
        int i8 = configuration.keyboard;
        if (i8 != 0 && this.keyboard != i8) {
            i |= 16;
            this.keyboard = i8;
        }
        int i9 = configuration.keyboardHidden;
        if (i9 != 0 && this.keyboardHidden != i9) {
            i |= 32;
            this.keyboardHidden = i9;
        }
        int i10 = configuration.hardKeyboardHidden;
        if (i10 != 0 && this.hardKeyboardHidden != i10) {
            i |= 32;
            this.hardKeyboardHidden = i10;
        }
        int i11 = configuration.navigation;
        if (i11 != 0 && this.navigation != i11) {
            i |= 64;
            this.navigation = i11;
        }
        int i12 = configuration.navigationHidden;
        if (i12 != 0 && this.navigationHidden != i12) {
            i |= 32;
            this.navigationHidden = i12;
        }
        int i13 = configuration.orientation;
        if (i13 != 0 && this.orientation != i13) {
            i |= 128;
            this.orientation = i13;
        }
        int i14 = configuration.screenLayout;
        if ((i14 & 15) != 0) {
            int i15 = i14 & 15;
            int i16 = this.screenLayout;
            if (i15 != (i16 & 15)) {
                i |= 256;
                this.screenLayout = (i14 & 15) | (i16 & (-16));
            }
        }
        int i17 = configuration.screenLayout;
        if ((i17 & 48) != 0) {
            int i18 = i17 & 48;
            int i19 = this.screenLayout;
            if (i18 != (i19 & 48)) {
                i |= 256;
                this.screenLayout = (i17 & 48) | (i19 & (-49));
            }
        }
        int i20 = configuration.screenLayout;
        if ((i20 & 768) != 0) {
            int i21 = i20 & 768;
            int i22 = this.screenLayout;
            if (i21 != (i22 & 768)) {
                i |= 256;
                this.screenLayout = (i20 & 768) | (i22 & (-769));
            }
        }
        int i23 = configuration.screenLayout;
        int i24 = i23 & 268435456;
        int i25 = this.screenLayout;
        if (i24 != (i25 & 268435456) && i23 != 0) {
            i |= 256;
            this.screenLayout = (i23 & 268435456) | ((-268435457) & i25);
        }
        int i26 = configuration.colorMode;
        if ((i26 & 3) != 0) {
            int i27 = i26 & 3;
            int i28 = this.colorMode;
            if (i27 != (i28 & 3)) {
                i |= 16384;
                this.colorMode = (i26 & 3) | (i28 & (-4));
            }
        }
        int i29 = configuration.colorMode;
        if ((i29 & 12) != 0) {
            int i30 = i29 & 12;
            int i31 = this.colorMode;
            if (i30 != (i31 & 12)) {
                i |= 16384;
                this.colorMode = (i29 & 12) | (i31 & (-13));
            }
        }
        int i32 = configuration.uiMode;
        if (i32 != 0 && (i2 = this.uiMode) != i32) {
            i |= 512;
            if ((i32 & 15) != 0) {
                this.uiMode = (i32 & 15) | (i2 & (-16));
            }
            int i33 = configuration.uiMode;
            if ((i33 & 48) != 0) {
                this.uiMode = (i33 & 48) | (this.uiMode & (-49));
            }
        }
        int i34 = configuration.screenWidthDp;
        if (i34 != 0 && this.screenWidthDp != i34) {
            i |= 1024;
            this.screenWidthDp = i34;
        }
        int i35 = configuration.screenHeightDp;
        if (i35 != 0 && this.screenHeightDp != i35) {
            i |= 1024;
            this.screenHeightDp = i35;
        }
        int i36 = configuration.smallestScreenWidthDp;
        if (i36 != 0 && this.smallestScreenWidthDp != i36) {
            i |= 2048;
            this.smallestScreenWidthDp = i36;
        }
        int i37 = configuration.densityDpi;
        if (i37 != 0 && this.densityDpi != i37) {
            i |= 4096;
            this.densityDpi = i37;
        }
        int i38 = configuration.compatScreenWidthDp;
        if (i38 != 0) {
            this.compatScreenWidthDp = i38;
        }
        int i39 = configuration.compatScreenHeightDp;
        if (i39 != 0) {
            this.compatScreenHeightDp = i39;
        }
        int i40 = configuration.compatSmallestScreenWidthDp;
        if (i40 != 0) {
            this.compatSmallestScreenWidthDp = i40;
        }
        int i41 = configuration.assetsSeq;
        if (i41 != 0 && i41 != this.assetsSeq) {
            i |= Integer.MIN_VALUE;
            this.assetsSeq = i41;
        }
        int i42 = configuration.seq;
        if (i42 != 0) {
            this.seq = i42;
        }
        return this.windowConfiguration.updateFrom(configuration.windowConfiguration) != 0 ? i | 536870912 : i;
    }

    public int diff(Configuration configuration) {
        return diff(configuration, false, false);
    }

    public int diffPublicOnly(Configuration configuration) {
        return diff(configuration, false, true);
    }

    public int diff(Configuration configuration, boolean z, boolean z2) {
        int i = ((z || configuration.fontScale > 0.0f) && this.fontScale != configuration.fontScale) ? 1073741824 : 0;
        if ((z || configuration.mcc != 0) && this.mcc != configuration.mcc) {
            i |= 1;
        }
        if ((z || configuration.mnc != 0) && this.mnc != configuration.mnc) {
            i |= 2;
        }
        fixUpLocaleList();
        configuration.fixUpLocaleList();
        if ((z || !configuration.mLocaleList.isEmpty()) && !this.mLocaleList.equals(configuration.mLocaleList)) {
            i = i | 4 | 8192;
        }
        int i2 = configuration.screenLayout & 192;
        if ((z || i2 != 0) && i2 != (this.screenLayout & 192)) {
            i |= 8192;
        }
        if ((z || configuration.touchscreen != 0) && this.touchscreen != configuration.touchscreen) {
            i |= 8;
        }
        if ((z || configuration.keyboard != 0) && this.keyboard != configuration.keyboard) {
            i |= 16;
        }
        if ((z || configuration.keyboardHidden != 0) && this.keyboardHidden != configuration.keyboardHidden) {
            i |= 32;
        }
        if ((z || configuration.hardKeyboardHidden != 0) && this.hardKeyboardHidden != configuration.hardKeyboardHidden) {
            i |= 32;
        }
        if ((z || configuration.navigation != 0) && this.navigation != configuration.navigation) {
            i |= 64;
        }
        if ((z || configuration.navigationHidden != 0) && this.navigationHidden != configuration.navigationHidden) {
            i |= 32;
        }
        if ((z || configuration.orientation != 0) && this.orientation != configuration.orientation) {
            i |= 128;
        }
        if ((z || getScreenLayoutNoDirection(configuration.screenLayout) != 0) && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(configuration.screenLayout)) {
            i |= 256;
        }
        if ((z || (configuration.colorMode & 12) != 0) && (this.colorMode & 12) != (configuration.colorMode & 12)) {
            i |= 16384;
        }
        if ((z || (configuration.colorMode & 3) != 0) && (this.colorMode & 3) != (configuration.colorMode & 3)) {
            i |= 16384;
        }
        if ((z || configuration.uiMode != 0) && this.uiMode != configuration.uiMode) {
            i |= 512;
        }
        if ((z || configuration.screenWidthDp != 0) && this.screenWidthDp != configuration.screenWidthDp) {
            i |= 1024;
        }
        if ((z || configuration.screenHeightDp != 0) && this.screenHeightDp != configuration.screenHeightDp) {
            i |= 1024;
        }
        if ((z || configuration.smallestScreenWidthDp != 0) && this.smallestScreenWidthDp != configuration.smallestScreenWidthDp) {
            i |= 2048;
        }
        if ((z || configuration.densityDpi != 0) && this.densityDpi != configuration.densityDpi) {
            i |= 4096;
        }
        if ((z || configuration.assetsSeq != 0) && this.assetsSeq != configuration.assetsSeq) {
            i |= Integer.MIN_VALUE;
        }
        return (z2 || this.windowConfiguration.diff(configuration.windowConfiguration, z) == 0) ? i : i | 536870912;
    }

    public boolean isOtherSeqNewer(Configuration configuration) {
        int i;
        if (configuration == null) {
            return false;
        }
        int i2 = configuration.seq;
        if (i2 == 0 || (i = this.seq) == 0) {
            return true;
        }
        int i3 = i2 - i;
        return i3 <= 65536 && i3 > 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.fontScale);
        parcel.writeInt(this.mcc);
        parcel.writeInt(this.mnc);
        fixUpLocaleList();
        parcel.writeParcelable(this.mLocaleList, i);
        if (this.userSetLocale) {
            parcel.writeInt(1);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.touchscreen);
        parcel.writeInt(this.keyboard);
        parcel.writeInt(this.keyboardHidden);
        parcel.writeInt(this.hardKeyboardHidden);
        parcel.writeInt(this.navigation);
        parcel.writeInt(this.navigationHidden);
        parcel.writeInt(this.orientation);
        parcel.writeInt(this.screenLayout);
        parcel.writeInt(this.colorMode);
        parcel.writeInt(this.uiMode);
        parcel.writeInt(this.screenWidthDp);
        parcel.writeInt(this.screenHeightDp);
        parcel.writeInt(this.smallestScreenWidthDp);
        parcel.writeInt(this.densityDpi);
        parcel.writeInt(this.compatScreenWidthDp);
        parcel.writeInt(this.compatScreenHeightDp);
        parcel.writeInt(this.compatSmallestScreenWidthDp);
        parcel.writeValue(this.windowConfiguration);
        parcel.writeInt(this.assetsSeq);
        parcel.writeInt(this.seq);
    }

    public void readFromParcel(Parcel parcel) {
        this.fontScale = parcel.readFloat();
        this.mcc = parcel.readInt();
        this.mnc = parcel.readInt();
        LocaleList localeList = (LocaleList) parcel.readParcelable(LocaleList.class.getClassLoader());
        this.mLocaleList = localeList;
        this.locale = localeList.get(0);
        this.userSetLocale = parcel.readInt() == 1;
        this.touchscreen = parcel.readInt();
        this.keyboard = parcel.readInt();
        this.keyboardHidden = parcel.readInt();
        this.hardKeyboardHidden = parcel.readInt();
        this.navigation = parcel.readInt();
        this.navigationHidden = parcel.readInt();
        this.orientation = parcel.readInt();
        this.screenLayout = parcel.readInt();
        this.colorMode = parcel.readInt();
        this.uiMode = parcel.readInt();
        this.screenWidthDp = parcel.readInt();
        this.screenHeightDp = parcel.readInt();
        this.smallestScreenWidthDp = parcel.readInt();
        this.densityDpi = parcel.readInt();
        this.compatScreenWidthDp = parcel.readInt();
        this.compatScreenHeightDp = parcel.readInt();
        this.compatSmallestScreenWidthDp = parcel.readInt();
        this.windowConfiguration.setTo((WindowConfiguration) parcel.readValue(null));
        this.assetsSeq = parcel.readInt();
        this.seq = parcel.readInt();
    }

    private Configuration(Parcel parcel) {
        this.windowConfiguration = new WindowConfiguration();
        readFromParcel(parcel);
    }

    @Override // java.lang.Comparable
    public int compareTo(Configuration configuration) {
        float f = this.fontScale;
        float f2 = configuration.fontScale;
        if (f < f2) {
            return -1;
        }
        if (f > f2) {
            return 1;
        }
        int i = this.mcc - configuration.mcc;
        if (i != 0) {
            return i;
        }
        int i2 = this.mnc - configuration.mnc;
        if (i2 != 0) {
            return i2;
        }
        fixUpLocaleList();
        configuration.fixUpLocaleList();
        if (this.mLocaleList.isEmpty()) {
            if (!configuration.mLocaleList.isEmpty()) {
                return 1;
            }
        } else {
            if (configuration.mLocaleList.isEmpty()) {
                return -1;
            }
            int iMin = Math.min(this.mLocaleList.size(), configuration.mLocaleList.size());
            for (int i3 = 0; i3 < iMin; i3++) {
                Locale locale = this.mLocaleList.get(i3);
                Locale locale2 = configuration.mLocaleList.get(i3);
                int iCompareTo = locale.getLanguage().compareTo(locale2.getLanguage());
                if (iCompareTo != 0) {
                    return iCompareTo;
                }
                int iCompareTo2 = locale.getCountry().compareTo(locale2.getCountry());
                if (iCompareTo2 != 0) {
                    return iCompareTo2;
                }
                int iCompareTo3 = locale.getVariant().compareTo(locale2.getVariant());
                if (iCompareTo3 != 0) {
                    return iCompareTo3;
                }
                int iCompareTo4 = locale.toLanguageTag().compareTo(locale2.toLanguageTag());
                if (iCompareTo4 != 0) {
                    return iCompareTo4;
                }
            }
            int size = this.mLocaleList.size() - configuration.mLocaleList.size();
            if (size != 0) {
                return size;
            }
        }
        int i4 = this.touchscreen - configuration.touchscreen;
        if (i4 != 0) {
            return i4;
        }
        int i5 = this.keyboard - configuration.keyboard;
        if (i5 != 0) {
            return i5;
        }
        int i6 = this.keyboardHidden - configuration.keyboardHidden;
        if (i6 != 0) {
            return i6;
        }
        int i7 = this.hardKeyboardHidden - configuration.hardKeyboardHidden;
        if (i7 != 0) {
            return i7;
        }
        int i8 = this.navigation - configuration.navigation;
        if (i8 != 0) {
            return i8;
        }
        int i9 = this.navigationHidden - configuration.navigationHidden;
        if (i9 != 0) {
            return i9;
        }
        int i10 = this.orientation - configuration.orientation;
        if (i10 != 0) {
            return i10;
        }
        int i11 = this.colorMode - configuration.colorMode;
        if (i11 != 0) {
            return i11;
        }
        int i12 = this.screenLayout - configuration.screenLayout;
        if (i12 != 0) {
            return i12;
        }
        int i13 = this.uiMode - configuration.uiMode;
        if (i13 != 0) {
            return i13;
        }
        int i14 = this.screenWidthDp - configuration.screenWidthDp;
        if (i14 != 0) {
            return i14;
        }
        int i15 = this.screenHeightDp - configuration.screenHeightDp;
        if (i15 != 0) {
            return i15;
        }
        int i16 = this.smallestScreenWidthDp - configuration.smallestScreenWidthDp;
        if (i16 != 0) {
            return i16;
        }
        int i17 = this.densityDpi - configuration.densityDpi;
        if (i17 != 0) {
            return i17;
        }
        int i18 = this.assetsSeq - configuration.assetsSeq;
        if (i18 != 0) {
            return i18;
        }
        int iCompareTo5 = this.windowConfiguration.compareTo(configuration.windowConfiguration);
        if (iCompareTo5 != 0) {
        }
        return iCompareTo5;
    }

    public boolean equals(Configuration configuration) {
        if (configuration == null) {
            return false;
        }
        return configuration == this || compareTo(configuration) == 0;
    }

    public boolean equals(Object obj) {
        try {
            return equals((Configuration) obj);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return ((((((((((((((((((((((((((((((((((((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + Float.floatToIntBits(this.fontScale)) * 31) + this.mcc) * 31) + this.mnc) * 31) + this.mLocaleList.hashCode()) * 31) + this.touchscreen) * 31) + this.keyboard) * 31) + this.keyboardHidden) * 31) + this.hardKeyboardHidden) * 31) + this.navigation) * 31) + this.navigationHidden) * 31) + this.orientation) * 31) + this.screenLayout) * 31) + this.colorMode) * 31) + this.uiMode) * 31) + this.screenWidthDp) * 31) + this.screenHeightDp) * 31) + this.smallestScreenWidthDp) * 31) + this.densityDpi) * 31) + this.assetsSeq;
    }

    public LocaleList getLocales() {
        fixUpLocaleList();
        return this.mLocaleList;
    }

    public void setLocales(LocaleList localeList) {
        if (localeList == null) {
            localeList = LocaleList.getEmptyLocaleList();
        }
        this.mLocaleList = localeList;
        Locale locale = localeList.get(0);
        this.locale = locale;
        setLayoutDirection(locale);
    }

    public void setLocale(Locale locale) {
        setLocales(locale == null ? LocaleList.getEmptyLocaleList() : new LocaleList(locale));
    }

    public void clearLocales() {
        this.mLocaleList = LocaleList.getEmptyLocaleList();
        this.locale = null;
    }

    public int getLayoutDirection() {
        return (this.screenLayout & 192) == 128 ? 1 : 0;
    }

    public void setLayoutDirection(Locale locale) {
        this.screenLayout = ((TextUtils.getLayoutDirectionFromLocale(locale) + 1) << 6) | (this.screenLayout & (-193));
    }

    public boolean isScreenRound() {
        return (this.screenLayout & 768) == 512;
    }

    public boolean isScreenWideColorGamut() {
        return (this.colorMode & 3) == 2;
    }

    public boolean isScreenHdr() {
        return (this.colorMode & 12) == 8;
    }

    public static String localesToResourceQualifier(LocaleList localeList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < localeList.size(); i++) {
            Locale locale = localeList.get(i);
            int length = locale.getLanguage().length();
            if (length != 0) {
                int length2 = locale.getScript().length();
                int length3 = locale.getCountry().length();
                int length4 = locale.getVariant().length();
                if (sb.length() != 0) {
                    sb.append(",");
                }
                if (length == 2 && length2 == 0 && ((length3 == 0 || length3 == 2) && length4 == 0)) {
                    sb.append(locale.getLanguage());
                    if (length3 == 2) {
                        sb.append("-r");
                        sb.append(locale.getCountry());
                    }
                } else {
                    sb.append("b+");
                    sb.append(locale.getLanguage());
                    if (length2 != 0) {
                        sb.append("+");
                        sb.append(locale.getScript());
                    }
                    if (length3 != 0) {
                        sb.append("+");
                        sb.append(locale.getCountry());
                    }
                    if (length4 != 0) {
                        sb.append("+");
                        sb.append(locale.getVariant());
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String resourceQualifierString(Configuration configuration) {
        return resourceQualifierString(configuration, null);
    }

    public static String resourceQualifierString(Configuration configuration, DisplayMetrics displayMetrics) {
        int i;
        int i2;
        ArrayList arrayList = new ArrayList();
        if (configuration.mcc != 0) {
            arrayList.add("mcc" + configuration.mcc);
            if (configuration.mnc != 0) {
                arrayList.add("mnc" + configuration.mnc);
            }
        }
        if (!configuration.mLocaleList.isEmpty()) {
            String strLocalesToResourceQualifier = localesToResourceQualifier(configuration.mLocaleList);
            if (!strLocalesToResourceQualifier.isEmpty()) {
                arrayList.add(strLocalesToResourceQualifier);
            }
        }
        int i3 = configuration.screenLayout & 192;
        if (i3 == 64) {
            arrayList.add("ldltr");
        } else if (i3 == 128) {
            arrayList.add("ldrtl");
        }
        if (configuration.smallestScreenWidthDp != 0) {
            arrayList.add(XML_ATTR_SMALLEST_WIDTH + configuration.smallestScreenWidthDp + "dp");
        }
        if (configuration.screenWidthDp != 0) {
            arrayList.add("w" + configuration.screenWidthDp + "dp");
        }
        if (configuration.screenHeightDp != 0) {
            arrayList.add("h" + configuration.screenHeightDp + "dp");
        }
        int i4 = configuration.screenLayout & 15;
        if (i4 == 1) {
            arrayList.add("small");
        } else if (i4 == 2) {
            arrayList.add("normal");
        } else if (i4 == 3) {
            arrayList.add(Slice.HINT_LARGE);
        } else if (i4 == 4) {
            arrayList.add("xlarge");
        }
        int i5 = configuration.screenLayout & 48;
        if (i5 == 16) {
            arrayList.add("notlong");
        } else if (i5 == 32) {
            arrayList.add("long");
        }
        int i6 = configuration.screenLayout & 768;
        if (i6 == 256) {
            arrayList.add("notround");
        } else if (i6 == 512) {
            arrayList.add("round");
        }
        int i7 = configuration.colorMode & 12;
        if (i7 == 4) {
            arrayList.add("lowdr");
        } else if (i7 == 8) {
            arrayList.add("highdr");
        }
        int i8 = configuration.colorMode & 3;
        if (i8 == 1) {
            arrayList.add("nowidecg");
        } else if (i8 == 2) {
            arrayList.add("widecg");
        }
        int i9 = configuration.orientation;
        if (i9 == 1) {
            arrayList.add("port");
        } else if (i9 == 2) {
            arrayList.add("land");
        }
        switch (configuration.uiMode & 15) {
            case 2:
                arrayList.add("desk");
                break;
            case 3:
                arrayList.add("car");
                break;
            case 4:
                arrayList.add("television");
                break;
            case 5:
                arrayList.add("appliance");
                break;
            case 6:
                arrayList.add("watch");
                break;
            case 7:
                arrayList.add("vrheadset");
                break;
        }
        int i10 = configuration.uiMode & 48;
        if (i10 == 16) {
            arrayList.add("notnight");
        } else if (i10 == 32) {
            arrayList.add(Camera.Parameters.SCENE_MODE_NIGHT);
        }
        int i11 = configuration.densityDpi;
        if (i11 != 0) {
            if (i11 == 120) {
                arrayList.add("ldpi");
            } else if (i11 == 160) {
                arrayList.add("mdpi");
            } else if (i11 == 213) {
                arrayList.add("tvdpi");
            } else if (i11 == 240) {
                arrayList.add("hdpi");
            } else if (i11 == 320) {
                arrayList.add("xhdpi");
            } else if (i11 == 480) {
                arrayList.add("xxhdpi");
            } else if (i11 == 640) {
                arrayList.add("xxxhdpi");
            } else {
                switch (i11) {
                    case DENSITY_DPI_ANY /* 65534 */:
                        arrayList.add("anydpi");
                        break;
                    case 65535:
                        arrayList.add("nodpi");
                        break;
                    default:
                        arrayList.add(configuration.densityDpi + "dpi");
                        break;
                }
            }
        }
        int i12 = configuration.touchscreen;
        if (i12 == 1) {
            arrayList.add("notouch");
        } else if (i12 == 3) {
            arrayList.add("finger");
        }
        int i13 = configuration.keyboardHidden;
        if (i13 == 1) {
            arrayList.add("keysexposed");
        } else if (i13 == 2) {
            arrayList.add("keyshidden");
        } else if (i13 == 3) {
            arrayList.add("keyssoft");
        }
        int i14 = configuration.keyboard;
        if (i14 == 1) {
            arrayList.add("nokeys");
        } else if (i14 == 2) {
            arrayList.add("qwerty");
        } else if (i14 == 3) {
            arrayList.add("12key");
        }
        int i15 = configuration.navigationHidden;
        if (i15 == 1) {
            arrayList.add("navexposed");
        } else if (i15 == 2) {
            arrayList.add("navhidden");
        }
        int i16 = configuration.navigation;
        if (i16 == 1) {
            arrayList.add("nonav");
        } else if (i16 == 2) {
            arrayList.add("dpad");
        } else if (i16 == 3) {
            arrayList.add("trackball");
        } else if (i16 == 4) {
            arrayList.add("wheel");
        }
        if (displayMetrics != null) {
            if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
                i = displayMetrics.widthPixels;
                i2 = displayMetrics.heightPixels;
            } else {
                i = displayMetrics.heightPixels;
                i2 = displayMetrics.widthPixels;
            }
            arrayList.add(i + "x" + i2);
        }
        arrayList.add(Telephony.BaseMmsColumns.MMS_VERSION + Build.VERSION.RESOURCES_SDK_INT);
        return TextUtils.join(NativeLibraryHelper.CLEAR_ABI_OVERRIDE, arrayList);
    }

    public static Configuration generateDelta(Configuration configuration, Configuration configuration2) {
        Configuration configuration3 = new Configuration();
        float f = configuration.fontScale;
        float f2 = configuration2.fontScale;
        if (f != f2) {
            configuration3.fontScale = f2;
        }
        int i = configuration.mcc;
        int i2 = configuration2.mcc;
        if (i != i2) {
            configuration3.mcc = i2;
        }
        int i3 = configuration.mnc;
        int i4 = configuration2.mnc;
        if (i3 != i4) {
            configuration3.mnc = i4;
        }
        configuration.fixUpLocaleList();
        configuration2.fixUpLocaleList();
        if (!configuration.mLocaleList.equals(configuration2.mLocaleList)) {
            configuration3.mLocaleList = configuration2.mLocaleList;
            configuration3.locale = configuration2.locale;
        }
        int i5 = configuration.touchscreen;
        int i6 = configuration2.touchscreen;
        if (i5 != i6) {
            configuration3.touchscreen = i6;
        }
        int i7 = configuration.keyboard;
        int i8 = configuration2.keyboard;
        if (i7 != i8) {
            configuration3.keyboard = i8;
        }
        int i9 = configuration.keyboardHidden;
        int i10 = configuration2.keyboardHidden;
        if (i9 != i10) {
            configuration3.keyboardHidden = i10;
        }
        int i11 = configuration.navigation;
        int i12 = configuration2.navigation;
        if (i11 != i12) {
            configuration3.navigation = i12;
        }
        int i13 = configuration.navigationHidden;
        int i14 = configuration2.navigationHidden;
        if (i13 != i14) {
            configuration3.navigationHidden = i14;
        }
        int i15 = configuration.orientation;
        int i16 = configuration2.orientation;
        if (i15 != i16) {
            configuration3.orientation = i16;
        }
        int i17 = configuration.screenLayout & 15;
        int i18 = configuration2.screenLayout;
        if (i17 != (i18 & 15)) {
            configuration3.screenLayout |= i18 & 15;
        }
        int i19 = configuration.screenLayout & 192;
        int i20 = configuration2.screenLayout;
        if (i19 != (i20 & 192)) {
            configuration3.screenLayout |= i20 & 192;
        }
        int i21 = configuration.screenLayout & 48;
        int i22 = configuration2.screenLayout;
        if (i21 != (i22 & 48)) {
            configuration3.screenLayout |= i22 & 48;
        }
        int i23 = configuration.screenLayout & 768;
        int i24 = configuration2.screenLayout;
        if (i23 != (i24 & 768)) {
            configuration3.screenLayout |= i24 & 768;
        }
        int i25 = configuration.colorMode & 3;
        int i26 = configuration2.colorMode;
        if (i25 != (i26 & 3)) {
            configuration3.colorMode |= i26 & 3;
        }
        int i27 = configuration.colorMode & 12;
        int i28 = configuration2.colorMode;
        if (i27 != (i28 & 12)) {
            configuration3.colorMode |= i28 & 12;
        }
        int i29 = configuration.uiMode & 15;
        int i30 = configuration2.uiMode;
        if (i29 != (i30 & 15)) {
            configuration3.uiMode |= i30 & 15;
        }
        int i31 = configuration.uiMode & 48;
        int i32 = configuration2.uiMode;
        if (i31 != (i32 & 48)) {
            configuration3.uiMode |= i32 & 48;
        }
        int i33 = configuration.screenWidthDp;
        int i34 = configuration2.screenWidthDp;
        if (i33 != i34) {
            configuration3.screenWidthDp = i34;
        }
        int i35 = configuration.screenHeightDp;
        int i36 = configuration2.screenHeightDp;
        if (i35 != i36) {
            configuration3.screenHeightDp = i36;
        }
        int i37 = configuration.smallestScreenWidthDp;
        int i38 = configuration2.smallestScreenWidthDp;
        if (i37 != i38) {
            configuration3.smallestScreenWidthDp = i38;
        }
        int i39 = configuration.densityDpi;
        int i40 = configuration2.densityDpi;
        if (i39 != i40) {
            configuration3.densityDpi = i40;
        }
        int i41 = configuration.assetsSeq;
        int i42 = configuration2.assetsSeq;
        if (i41 != i42) {
            configuration3.assetsSeq = i42;
        }
        if (!configuration.windowConfiguration.equals(configuration2.windowConfiguration)) {
            configuration3.windowConfiguration.setTo(configuration2.windowConfiguration);
        }
        return configuration3;
    }

    public static void readXmlAttrs(XmlPullParser xmlPullParser, Configuration configuration) throws XmlPullParserException, IOException {
        configuration.fontScale = Float.intBitsToFloat(XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_FONT_SCALE, 0));
        configuration.mcc = XmlUtils.readIntAttribute(xmlPullParser, "mcc", 0);
        configuration.mnc = XmlUtils.readIntAttribute(xmlPullParser, "mnc", 0);
        LocaleList localeListForLanguageTags = LocaleList.forLanguageTags(XmlUtils.readStringAttribute(xmlPullParser, XML_ATTR_LOCALES));
        configuration.mLocaleList = localeListForLanguageTags;
        configuration.locale = localeListForLanguageTags.get(0);
        configuration.touchscreen = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_TOUCHSCREEN, 0);
        configuration.keyboard = XmlUtils.readIntAttribute(xmlPullParser, "key", 0);
        configuration.keyboardHidden = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_KEYBOARD_HIDDEN, 0);
        configuration.hardKeyboardHidden = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_HARD_KEYBOARD_HIDDEN, 0);
        configuration.navigation = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_NAVIGATION, 0);
        configuration.navigationHidden = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_NAVIGATION_HIDDEN, 0);
        configuration.orientation = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_ORIENTATION, 0);
        configuration.screenLayout = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_SCREEN_LAYOUT, 0);
        configuration.colorMode = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_COLOR_MODE, 0);
        configuration.uiMode = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_UI_MODE, 0);
        configuration.screenWidthDp = XmlUtils.readIntAttribute(xmlPullParser, "width", 0);
        configuration.screenHeightDp = XmlUtils.readIntAttribute(xmlPullParser, "height", 0);
        configuration.smallestScreenWidthDp = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_SMALLEST_WIDTH, 0);
        configuration.densityDpi = XmlUtils.readIntAttribute(xmlPullParser, XML_ATTR_DENSITY, 0);
    }

    public static void writeXmlAttrs(XmlSerializer xmlSerializer, Configuration configuration) throws IOException {
        XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_FONT_SCALE, Float.floatToIntBits(configuration.fontScale));
        int i = configuration.mcc;
        if (i != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, "mcc", i);
        }
        int i2 = configuration.mnc;
        if (i2 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, "mnc", i2);
        }
        configuration.fixUpLocaleList();
        if (!configuration.mLocaleList.isEmpty()) {
            XmlUtils.writeStringAttribute(xmlSerializer, XML_ATTR_LOCALES, configuration.mLocaleList.toLanguageTags());
        }
        int i3 = configuration.touchscreen;
        if (i3 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_TOUCHSCREEN, i3);
        }
        int i4 = configuration.keyboard;
        if (i4 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, "key", i4);
        }
        int i5 = configuration.keyboardHidden;
        if (i5 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_KEYBOARD_HIDDEN, i5);
        }
        int i6 = configuration.hardKeyboardHidden;
        if (i6 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_HARD_KEYBOARD_HIDDEN, i6);
        }
        int i7 = configuration.navigation;
        if (i7 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_NAVIGATION, i7);
        }
        int i8 = configuration.navigationHidden;
        if (i8 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_NAVIGATION_HIDDEN, i8);
        }
        int i9 = configuration.orientation;
        if (i9 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_ORIENTATION, i9);
        }
        int i10 = configuration.screenLayout;
        if (i10 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_SCREEN_LAYOUT, i10);
        }
        int i11 = configuration.colorMode;
        if (i11 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_COLOR_MODE, i11);
        }
        int i12 = configuration.uiMode;
        if (i12 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_UI_MODE, i12);
        }
        int i13 = configuration.screenWidthDp;
        if (i13 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, "width", i13);
        }
        int i14 = configuration.screenHeightDp;
        if (i14 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, "height", i14);
        }
        int i15 = configuration.smallestScreenWidthDp;
        if (i15 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_SMALLEST_WIDTH, i15);
        }
        int i16 = configuration.densityDpi;
        if (i16 != 0) {
            XmlUtils.writeIntAttribute(xmlSerializer, XML_ATTR_DENSITY, i16);
        }
    }
}
