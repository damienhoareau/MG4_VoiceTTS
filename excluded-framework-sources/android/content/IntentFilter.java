package android.content;

import android.annotation.SystemApi;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import android.util.Printer;
import android.util.proto.ProtoOutputStream;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public class IntentFilter implements Parcelable {
    private static final String ACTION_STR = "action";
    private static final String AGLOB_STR = "aglob";
    private static final String AUTH_STR = "auth";
    private static final String AUTO_VERIFY_STR = "autoVerify";
    private static final String CAT_STR = "cat";
    public static final Parcelable.Creator<IntentFilter> CREATOR = new Parcelable.Creator<IntentFilter>() { // from class: android.content.IntentFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentFilter createFromParcel(Parcel parcel) {
            return new IntentFilter(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentFilter[] newArray(int i) {
            return new IntentFilter[i];
        }
    };
    private static final String HOST_STR = "host";
    private static final String LITERAL_STR = "literal";
    public static final int MATCH_ADJUSTMENT_MASK = 65535;
    public static final int MATCH_ADJUSTMENT_NORMAL = 32768;
    public static final int MATCH_CATEGORY_EMPTY = 1048576;
    public static final int MATCH_CATEGORY_HOST = 3145728;
    public static final int MATCH_CATEGORY_MASK = 268369920;
    public static final int MATCH_CATEGORY_PATH = 5242880;
    public static final int MATCH_CATEGORY_PORT = 4194304;
    public static final int MATCH_CATEGORY_SCHEME = 2097152;
    public static final int MATCH_CATEGORY_SCHEME_SPECIFIC_PART = 5767168;
    public static final int MATCH_CATEGORY_TYPE = 6291456;
    private static final String NAME_STR = "name";
    public static final int NO_MATCH_ACTION = -3;
    public static final int NO_MATCH_CATEGORY = -4;
    public static final int NO_MATCH_DATA = -2;
    public static final int NO_MATCH_TYPE = -1;
    private static final String PATH_STR = "path";
    private static final String PORT_STR = "port";
    private static final String PREFIX_STR = "prefix";
    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";
    private static final String SCHEME_STR = "scheme";
    private static final String SGLOB_STR = "sglob";
    private static final String SSP_STR = "ssp";
    private static final int STATE_NEED_VERIFY = 16;
    private static final int STATE_NEED_VERIFY_CHECKED = 256;
    private static final int STATE_VERIFIED = 4096;
    private static final int STATE_VERIFY_AUTO = 1;
    public static final int SYSTEM_HIGH_PRIORITY = 1000;
    public static final int SYSTEM_LOW_PRIORITY = -1000;
    private static final String TYPE_STR = "type";
    public static final int VISIBILITY_EXPLICIT = 1;
    public static final int VISIBILITY_IMPLICIT = 2;
    public static final int VISIBILITY_NONE = 0;
    private final ArrayList<String> mActions;
    private ArrayList<String> mCategories;
    private ArrayList<AuthorityEntry> mDataAuthorities;
    private ArrayList<PatternMatcher> mDataPaths;
    private ArrayList<PatternMatcher> mDataSchemeSpecificParts;
    private ArrayList<String> mDataSchemes;
    private ArrayList<String> mDataTypes;
    private boolean mHasPartialTypes;
    private int mInstantAppVisibility;
    private int mOrder;
    private int mPriority;
    private int mVerifyState;

    @Retention(RetentionPolicy.SOURCE)
    public @interface InstantAppVisibility {
    }

    public boolean debugCheck() {
        return true;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    private static int findStringInSet(String[] strArr, String str, int[] iArr, int i) {
        if (strArr == null) {
            return -1;
        }
        int i2 = iArr[i];
        for (int i3 = 0; i3 < i2; i3++) {
            if (strArr[i3].equals(str)) {
                return i3;
            }
        }
        return -1;
    }

    private static String[] addStringToSet(String[] strArr, String str, int[] iArr, int i) {
        if (findStringInSet(strArr, str, iArr, i) >= 0) {
            return strArr;
        }
        if (strArr == null) {
            String[] strArr2 = new String[2];
            strArr2[0] = str;
            iArr[i] = 1;
            return strArr2;
        }
        int i2 = iArr[i];
        if (i2 < strArr.length) {
            strArr[i2] = str;
            iArr[i] = i2 + 1;
            return strArr;
        }
        String[] strArr3 = new String[((i2 * 3) / 2) + 2];
        System.arraycopy(strArr, 0, strArr3, 0, i2);
        strArr3[i2] = str;
        iArr[i] = i2 + 1;
        return strArr3;
    }

    private static String[] removeStringFromSet(String[] strArr, String str, int[] iArr, int i) {
        int iFindStringInSet = findStringInSet(strArr, str, iArr, i);
        if (iFindStringInSet < 0) {
            return strArr;
        }
        int i2 = iArr[i];
        if (i2 > strArr.length / 4) {
            int i3 = iFindStringInSet + 1;
            int i4 = i2 - i3;
            if (i4 > 0) {
                System.arraycopy(strArr, i3, strArr, iFindStringInSet, i4);
            }
            int i5 = i2 - 1;
            strArr[i5] = null;
            iArr[i] = i5;
            return strArr;
        }
        String[] strArr2 = new String[strArr.length / 3];
        if (iFindStringInSet > 0) {
            System.arraycopy(strArr, 0, strArr2, 0, iFindStringInSet);
        }
        int i6 = iFindStringInSet + 1;
        if (i6 < i2) {
            System.arraycopy(strArr, i6, strArr2, iFindStringInSet, i2 - i6);
        }
        return strArr2;
    }

    public static class MalformedMimeTypeException extends AndroidException {
        public MalformedMimeTypeException() {
        }

        public MalformedMimeTypeException(String str) {
            super(str);
        }
    }

    public static IntentFilter create(String str, String str2) {
        try {
            return new IntentFilter(str, str2);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Bad MIME type", e);
        }
    }

    public IntentFilter() {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
    }

    public IntentFilter(String str) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
        addAction(str);
    }

    public IntentFilter(String str, String str2) throws MalformedMimeTypeException {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = 0;
        this.mActions = new ArrayList<>();
        addAction(str);
        addDataType(str2);
    }

    public IntentFilter(IntentFilter intentFilter) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        this.mPriority = intentFilter.mPriority;
        this.mOrder = intentFilter.mOrder;
        this.mActions = new ArrayList<>(intentFilter.mActions);
        if (intentFilter.mCategories != null) {
            this.mCategories = new ArrayList<>(intentFilter.mCategories);
        }
        if (intentFilter.mDataTypes != null) {
            this.mDataTypes = new ArrayList<>(intentFilter.mDataTypes);
        }
        if (intentFilter.mDataSchemes != null) {
            this.mDataSchemes = new ArrayList<>(intentFilter.mDataSchemes);
        }
        if (intentFilter.mDataSchemeSpecificParts != null) {
            this.mDataSchemeSpecificParts = new ArrayList<>(intentFilter.mDataSchemeSpecificParts);
        }
        if (intentFilter.mDataAuthorities != null) {
            this.mDataAuthorities = new ArrayList<>(intentFilter.mDataAuthorities);
        }
        if (intentFilter.mDataPaths != null) {
            this.mDataPaths = new ArrayList<>(intentFilter.mDataPaths);
        }
        this.mHasPartialTypes = intentFilter.mHasPartialTypes;
        this.mVerifyState = intentFilter.mVerifyState;
        this.mInstantAppVisibility = intentFilter.mInstantAppVisibility;
    }

    public final void setPriority(int i) {
        this.mPriority = i;
    }

    public final int getPriority() {
        return this.mPriority;
    }

    @SystemApi
    public final void setOrder(int i) {
        this.mOrder = i;
    }

    @SystemApi
    public final int getOrder() {
        return this.mOrder;
    }

    public final void setAutoVerify(boolean z) {
        int i = this.mVerifyState & (-2);
        this.mVerifyState = i;
        if (z) {
            this.mVerifyState = i | 1;
        }
    }

    public final boolean getAutoVerify() {
        return (this.mVerifyState & 1) == 1;
    }

    public final boolean handleAllWebDataURI() {
        return hasCategory(Intent.CATEGORY_APP_BROWSER) || (handlesWebUris(false) && countDataAuthorities() == 0);
    }

    public final boolean handlesWebUris(boolean z) {
        ArrayList<String> arrayList;
        if (!hasAction("android.intent.action.VIEW") || !hasCategory(Intent.CATEGORY_BROWSABLE) || (arrayList = this.mDataSchemes) == null || arrayList.size() == 0) {
            return false;
        }
        int size = this.mDataSchemes.size();
        for (int i = 0; i < size; i++) {
            String str = this.mDataSchemes.get(i);
            boolean z2 = SCHEME_HTTP.equals(str) || SCHEME_HTTPS.equals(str);
            if (z) {
                if (!z2) {
                    return false;
                }
            } else if (z2) {
                return true;
            }
        }
        return z;
    }

    public final boolean needsVerification() {
        return getAutoVerify() && handlesWebUris(true);
    }

    public final boolean isVerified() {
        int i = this.mVerifyState;
        return (i & 256) == 256 && (i & 16) == 16;
    }

    public void setVerified(boolean z) {
        int i = this.mVerifyState | 256;
        this.mVerifyState = i;
        int i2 = i & (-4097);
        this.mVerifyState = i2;
        if (z) {
            this.mVerifyState = i2 | 4096;
        }
    }

    public void setVisibilityToInstantApp(int i) {
        this.mInstantAppVisibility = i;
    }

    public int getVisibilityToInstantApp() {
        return this.mInstantAppVisibility;
    }

    public boolean isVisibleToInstantApp() {
        return this.mInstantAppVisibility != 0;
    }

    public boolean isExplicitlyVisibleToInstantApp() {
        return this.mInstantAppVisibility == 1;
    }

    public boolean isImplicitlyVisibleToInstantApp() {
        return this.mInstantAppVisibility == 2;
    }

    public final void addAction(String str) {
        if (this.mActions.contains(str)) {
            return;
        }
        this.mActions.add(str.intern());
    }

    public final int countActions() {
        return this.mActions.size();
    }

    public final String getAction(int i) {
        return this.mActions.get(i);
    }

    public final boolean hasAction(String str) {
        return str != null && this.mActions.contains(str);
    }

    public final boolean matchAction(String str) {
        return hasAction(str);
    }

    public final Iterator<String> actionsIterator() {
        ArrayList<String> arrayList = this.mActions;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final void addDataType(String str) throws MalformedMimeTypeException {
        int i;
        int iIndexOf = str.indexOf(47);
        int length = str.length();
        if (iIndexOf > 0 && length >= (i = iIndexOf + 2)) {
            if (this.mDataTypes == null) {
                this.mDataTypes = new ArrayList<>();
            }
            if (length == i && str.charAt(iIndexOf + 1) == '*') {
                String strSubstring = str.substring(0, iIndexOf);
                if (!this.mDataTypes.contains(strSubstring)) {
                    this.mDataTypes.add(strSubstring.intern());
                }
                this.mHasPartialTypes = true;
                return;
            }
            if (this.mDataTypes.contains(str)) {
                return;
            }
            this.mDataTypes.add(str.intern());
            return;
        }
        throw new MalformedMimeTypeException(str);
    }

    public final boolean hasDataType(String str) {
        return this.mDataTypes != null && findMimeType(str);
    }

    public final boolean hasExactDataType(String str) {
        ArrayList<String> arrayList = this.mDataTypes;
        return arrayList != null && arrayList.contains(str);
    }

    public final int countDataTypes() {
        ArrayList<String> arrayList = this.mDataTypes;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final String getDataType(int i) {
        return this.mDataTypes.get(i);
    }

    public final Iterator<String> typesIterator() {
        ArrayList<String> arrayList = this.mDataTypes;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final void addDataScheme(String str) {
        if (this.mDataSchemes == null) {
            this.mDataSchemes = new ArrayList<>();
        }
        if (this.mDataSchemes.contains(str)) {
            return;
        }
        this.mDataSchemes.add(str.intern());
    }

    public final int countDataSchemes() {
        ArrayList<String> arrayList = this.mDataSchemes;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final String getDataScheme(int i) {
        return this.mDataSchemes.get(i);
    }

    public final boolean hasDataScheme(String str) {
        ArrayList<String> arrayList = this.mDataSchemes;
        return arrayList != null && arrayList.contains(str);
    }

    public final Iterator<String> schemesIterator() {
        ArrayList<String> arrayList = this.mDataSchemes;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public static final class AuthorityEntry {
        private final String mHost;
        private final String mOrigHost;
        private final int mPort;
        private final boolean mWild;

        public AuthorityEntry(String str, String str2) {
            this.mOrigHost = str;
            boolean z = false;
            if (str.length() > 0 && str.charAt(0) == '*') {
                z = true;
            }
            this.mWild = z;
            this.mHost = z ? str.substring(1).intern() : str;
            this.mPort = str2 != null ? Integer.parseInt(str2) : -1;
        }

        AuthorityEntry(Parcel parcel) {
            this.mOrigHost = parcel.readString();
            this.mHost = parcel.readString();
            this.mWild = parcel.readInt() != 0;
            this.mPort = parcel.readInt();
        }

        void writeToParcel(Parcel parcel) {
            parcel.writeString(this.mOrigHost);
            parcel.writeString(this.mHost);
            parcel.writeInt(this.mWild ? 1 : 0);
            parcel.writeInt(this.mPort);
        }

        void writeToProto(ProtoOutputStream protoOutputStream, long j) {
            long jStart = protoOutputStream.start(j);
            protoOutputStream.write(1138166333441L, this.mHost);
            protoOutputStream.write(1133871366146L, this.mWild);
            protoOutputStream.write(1120986464259L, this.mPort);
            protoOutputStream.end(jStart);
        }

        public String getHost() {
            return this.mOrigHost;
        }

        public int getPort() {
            return this.mPort;
        }

        public boolean match(AuthorityEntry authorityEntry) {
            return this.mWild == authorityEntry.mWild && this.mHost.equals(authorityEntry.mHost) && this.mPort == authorityEntry.mPort;
        }

        public boolean equals(Object obj) {
            if (obj instanceof AuthorityEntry) {
                return match((AuthorityEntry) obj);
            }
            return false;
        }

        public int match(Uri uri) {
            String host = uri.getHost();
            if (host == null) {
                return -2;
            }
            if (this.mWild) {
                if (host.length() < this.mHost.length()) {
                    return -2;
                }
                host = host.substring(host.length() - this.mHost.length());
            }
            if (host.compareToIgnoreCase(this.mHost) != 0) {
                return -2;
            }
            int i = this.mPort;
            if (i >= 0) {
                return i != uri.getPort() ? -2 : 4194304;
            }
            return IntentFilter.MATCH_CATEGORY_HOST;
        }
    }

    public final void addDataSchemeSpecificPart(String str, int i) {
        addDataSchemeSpecificPart(new PatternMatcher(str, i));
    }

    public final void addDataSchemeSpecificPart(PatternMatcher patternMatcher) {
        if (this.mDataSchemeSpecificParts == null) {
            this.mDataSchemeSpecificParts = new ArrayList<>();
        }
        this.mDataSchemeSpecificParts.add(patternMatcher);
    }

    public final int countDataSchemeSpecificParts() {
        ArrayList<PatternMatcher> arrayList = this.mDataSchemeSpecificParts;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final PatternMatcher getDataSchemeSpecificPart(int i) {
        return this.mDataSchemeSpecificParts.get(i);
    }

    public final boolean hasDataSchemeSpecificPart(String str) {
        ArrayList<PatternMatcher> arrayList = this.mDataSchemeSpecificParts;
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.mDataSchemeSpecificParts.get(i).match(str)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasDataSchemeSpecificPart(PatternMatcher patternMatcher) {
        ArrayList<PatternMatcher> arrayList = this.mDataSchemeSpecificParts;
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            PatternMatcher patternMatcher2 = this.mDataSchemeSpecificParts.get(i);
            if (patternMatcher2.getType() == patternMatcher.getType() && patternMatcher2.getPath().equals(patternMatcher.getPath())) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> schemeSpecificPartsIterator() {
        ArrayList<PatternMatcher> arrayList = this.mDataSchemeSpecificParts;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final void addDataAuthority(String str, String str2) {
        if (str2 != null) {
            str2 = str2.intern();
        }
        addDataAuthority(new AuthorityEntry(str.intern(), str2));
    }

    public final void addDataAuthority(AuthorityEntry authorityEntry) {
        if (this.mDataAuthorities == null) {
            this.mDataAuthorities = new ArrayList<>();
        }
        this.mDataAuthorities.add(authorityEntry);
    }

    public final int countDataAuthorities() {
        ArrayList<AuthorityEntry> arrayList = this.mDataAuthorities;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final AuthorityEntry getDataAuthority(int i) {
        return this.mDataAuthorities.get(i);
    }

    public final boolean hasDataAuthority(Uri uri) {
        return matchDataAuthority(uri) >= 0;
    }

    public final boolean hasDataAuthority(AuthorityEntry authorityEntry) {
        ArrayList<AuthorityEntry> arrayList = this.mDataAuthorities;
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.mDataAuthorities.get(i).match(authorityEntry)) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<AuthorityEntry> authoritiesIterator() {
        ArrayList<AuthorityEntry> arrayList = this.mDataAuthorities;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final void addDataPath(String str, int i) {
        addDataPath(new PatternMatcher(str.intern(), i));
    }

    public final void addDataPath(PatternMatcher patternMatcher) {
        if (this.mDataPaths == null) {
            this.mDataPaths = new ArrayList<>();
        }
        this.mDataPaths.add(patternMatcher);
    }

    public final int countDataPaths() {
        ArrayList<PatternMatcher> arrayList = this.mDataPaths;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final PatternMatcher getDataPath(int i) {
        return this.mDataPaths.get(i);
    }

    public final boolean hasDataPath(String str) {
        ArrayList<PatternMatcher> arrayList = this.mDataPaths;
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.mDataPaths.get(i).match(str)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasDataPath(PatternMatcher patternMatcher) {
        ArrayList<PatternMatcher> arrayList = this.mDataPaths;
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            PatternMatcher patternMatcher2 = this.mDataPaths.get(i);
            if (patternMatcher2.getType() == patternMatcher.getType() && patternMatcher2.getPath().equals(patternMatcher.getPath())) {
                return true;
            }
        }
        return false;
    }

    public final Iterator<PatternMatcher> pathsIterator() {
        ArrayList<PatternMatcher> arrayList = this.mDataPaths;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final int matchDataAuthority(Uri uri) {
        ArrayList<AuthorityEntry> arrayList = this.mDataAuthorities;
        if (arrayList != null && uri != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                int iMatch = this.mDataAuthorities.get(i).match(uri);
                if (iMatch >= 0) {
                    return iMatch;
                }
            }
        }
        return -2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0055, code lost:
    
        if (r6 == (-2)) goto L36;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final int matchData(java.lang.String r5, java.lang.String r6, android.net.Uri r7) {
        /*
            r4 = this;
            java.util.ArrayList<java.lang.String> r0 = r4.mDataTypes
            java.util.ArrayList<java.lang.String> r1 = r4.mDataSchemes
            r2 = -2
            if (r0 != 0) goto L11
            if (r1 != 0) goto L11
            if (r5 != 0) goto L10
            if (r7 != 0) goto L10
            r2 = 1081344(0x108000, float:1.515286E-39)
        L10:
            return r2
        L11:
            java.lang.String r3 = ""
            if (r1 == 0) goto L58
            if (r6 == 0) goto L18
            goto L19
        L18:
            r6 = r3
        L19:
            boolean r6 = r1.contains(r6)
            if (r6 == 0) goto L57
            r6 = 2097152(0x200000, float:2.938736E-39)
            java.util.ArrayList<android.os.PatternMatcher> r1 = r4.mDataSchemeSpecificParts
            r3 = 5767168(0x580000, float:8.081524E-39)
            if (r1 == 0) goto L36
            if (r7 == 0) goto L36
            java.lang.String r6 = r7.getSchemeSpecificPart()
            boolean r6 = r4.hasDataSchemeSpecificPart(r6)
            if (r6 == 0) goto L35
            r6 = r3
            goto L36
        L35:
            r6 = r2
        L36:
            if (r6 == r3) goto L55
            java.util.ArrayList<android.content.IntentFilter$AuthorityEntry> r1 = r4.mDataAuthorities
            if (r1 == 0) goto L55
            int r6 = r4.matchDataAuthority(r7)
            if (r6 < 0) goto L54
            java.util.ArrayList<android.os.PatternMatcher> r1 = r4.mDataPaths
            if (r1 != 0) goto L47
            goto L55
        L47:
            java.lang.String r6 = r7.getPath()
            boolean r6 = r4.hasDataPath(r6)
            if (r6 == 0) goto L54
            r6 = 5242880(0x500000, float:7.34684E-39)
            goto L55
        L54:
            return r2
        L55:
            if (r6 != r2) goto L73
        L57:
            return r2
        L58:
            if (r6 == 0) goto L71
            boolean r7 = r3.equals(r6)
            if (r7 != 0) goto L71
            java.lang.String r7 = "content"
            boolean r7 = r7.equals(r6)
            if (r7 != 0) goto L71
            java.lang.String r7 = "file"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L71
            return r2
        L71:
            r6 = 1048576(0x100000, float:1.469368E-39)
        L73:
            r7 = -1
            if (r0 == 0) goto L80
            boolean r5 = r4.findMimeType(r5)
            if (r5 == 0) goto L7f
            r6 = 6291456(0x600000, float:8.816208E-39)
            goto L83
        L7f:
            return r7
        L80:
            if (r5 == 0) goto L83
            return r7
        L83:
            r5 = 32768(0x8000, float:4.5918E-41)
            int r6 = r6 + r5
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.IntentFilter.matchData(java.lang.String, java.lang.String, android.net.Uri):int");
    }

    public final void addCategory(String str) {
        if (this.mCategories == null) {
            this.mCategories = new ArrayList<>();
        }
        if (this.mCategories.contains(str)) {
            return;
        }
        this.mCategories.add(str.intern());
    }

    public final int countCategories() {
        ArrayList<String> arrayList = this.mCategories;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public final String getCategory(int i) {
        return this.mCategories.get(i);
    }

    public final boolean hasCategory(String str) {
        ArrayList<String> arrayList = this.mCategories;
        return arrayList != null && arrayList.contains(str);
    }

    public final Iterator<String> categoriesIterator() {
        ArrayList<String> arrayList = this.mCategories;
        if (arrayList != null) {
            return arrayList.iterator();
        }
        return null;
    }

    public final String matchCategories(Set<String> set) {
        if (set == null) {
            return null;
        }
        Iterator<String> it = set.iterator();
        if (this.mCategories == null) {
            if (it.hasNext()) {
                return it.next();
            }
            return null;
        }
        while (it.hasNext()) {
            String next = it.next();
            if (!this.mCategories.contains(next)) {
                return next;
            }
        }
        return null;
    }

    public final int match(ContentResolver contentResolver, Intent intent, boolean z, String str) {
        return match(intent.getAction(), z ? intent.resolveType(contentResolver) : intent.getType(), intent.getScheme(), intent.getData(), intent.getCategories(), str);
    }

    public final int match(String str, String str2, String str3, Uri uri, Set<String> set, String str4) {
        if (str != null && !matchAction(str)) {
            return -3;
        }
        int iMatchData = matchData(str2, str3, uri);
        if (iMatchData >= 0 && matchCategories(set) != null) {
            return -4;
        }
        return iMatchData;
    }

    public void writeToXml(XmlSerializer xmlSerializer) throws IOException {
        if (getAutoVerify()) {
            xmlSerializer.attribute(null, AUTO_VERIFY_STR, Boolean.toString(true));
        }
        int iCountActions = countActions();
        for (int i = 0; i < iCountActions; i++) {
            xmlSerializer.startTag(null, "action");
            xmlSerializer.attribute(null, "name", this.mActions.get(i));
            xmlSerializer.endTag(null, "action");
        }
        int iCountCategories = countCategories();
        for (int i2 = 0; i2 < iCountCategories; i2++) {
            xmlSerializer.startTag(null, CAT_STR);
            xmlSerializer.attribute(null, "name", this.mCategories.get(i2));
            xmlSerializer.endTag(null, CAT_STR);
        }
        int iCountDataTypes = countDataTypes();
        for (int i3 = 0; i3 < iCountDataTypes; i3++) {
            xmlSerializer.startTag(null, "type");
            String str = this.mDataTypes.get(i3);
            if (str.indexOf(47) < 0) {
                str = str + "/*";
            }
            xmlSerializer.attribute(null, "name", str);
            xmlSerializer.endTag(null, "type");
        }
        int iCountDataSchemes = countDataSchemes();
        for (int i4 = 0; i4 < iCountDataSchemes; i4++) {
            xmlSerializer.startTag(null, SCHEME_STR);
            xmlSerializer.attribute(null, "name", this.mDataSchemes.get(i4));
            xmlSerializer.endTag(null, SCHEME_STR);
        }
        int iCountDataSchemeSpecificParts = countDataSchemeSpecificParts();
        for (int i5 = 0; i5 < iCountDataSchemeSpecificParts; i5++) {
            xmlSerializer.startTag(null, SSP_STR);
            PatternMatcher patternMatcher = this.mDataSchemeSpecificParts.get(i5);
            int type = patternMatcher.getType();
            if (type == 0) {
                xmlSerializer.attribute(null, LITERAL_STR, patternMatcher.getPath());
            } else if (type == 1) {
                xmlSerializer.attribute(null, PREFIX_STR, patternMatcher.getPath());
            } else if (type == 2) {
                xmlSerializer.attribute(null, SGLOB_STR, patternMatcher.getPath());
            } else if (type == 3) {
                xmlSerializer.attribute(null, AGLOB_STR, patternMatcher.getPath());
            }
            xmlSerializer.endTag(null, SSP_STR);
        }
        int iCountDataAuthorities = countDataAuthorities();
        for (int i6 = 0; i6 < iCountDataAuthorities; i6++) {
            xmlSerializer.startTag(null, AUTH_STR);
            AuthorityEntry authorityEntry = this.mDataAuthorities.get(i6);
            xmlSerializer.attribute(null, HOST_STR, authorityEntry.getHost());
            if (authorityEntry.getPort() >= 0) {
                xmlSerializer.attribute(null, "port", Integer.toString(authorityEntry.getPort()));
            }
            xmlSerializer.endTag(null, AUTH_STR);
        }
        int iCountDataPaths = countDataPaths();
        for (int i7 = 0; i7 < iCountDataPaths; i7++) {
            xmlSerializer.startTag(null, PATH_STR);
            PatternMatcher patternMatcher2 = this.mDataPaths.get(i7);
            int type2 = patternMatcher2.getType();
            if (type2 == 0) {
                xmlSerializer.attribute(null, LITERAL_STR, patternMatcher2.getPath());
            } else if (type2 == 1) {
                xmlSerializer.attribute(null, PREFIX_STR, patternMatcher2.getPath());
            } else if (type2 == 2) {
                xmlSerializer.attribute(null, SGLOB_STR, patternMatcher2.getPath());
            } else if (type2 == 3) {
                xmlSerializer.attribute(null, AGLOB_STR, patternMatcher2.getPath());
            }
            xmlSerializer.endTag(null, PATH_STR);
        }
    }

    public void readFromXml(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        String attributeValue = xmlPullParser.getAttributeValue(null, AUTO_VERIFY_STR);
        setAutoVerify(TextUtils.isEmpty(attributeValue) ? false : Boolean.getBoolean(attributeValue));
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                String name = xmlPullParser.getName();
                if (name.equals("action")) {
                    String attributeValue2 = xmlPullParser.getAttributeValue(null, "name");
                    if (attributeValue2 != null) {
                        addAction(attributeValue2);
                    }
                } else if (name.equals(CAT_STR)) {
                    String attributeValue3 = xmlPullParser.getAttributeValue(null, "name");
                    if (attributeValue3 != null) {
                        addCategory(attributeValue3);
                    }
                } else if (name.equals("type")) {
                    String attributeValue4 = xmlPullParser.getAttributeValue(null, "name");
                    if (attributeValue4 != null) {
                        try {
                            addDataType(attributeValue4);
                        } catch (MalformedMimeTypeException unused) {
                        }
                    }
                } else if (name.equals(SCHEME_STR)) {
                    String attributeValue5 = xmlPullParser.getAttributeValue(null, "name");
                    if (attributeValue5 != null) {
                        addDataScheme(attributeValue5);
                    }
                } else if (name.equals(SSP_STR)) {
                    String attributeValue6 = xmlPullParser.getAttributeValue(null, LITERAL_STR);
                    if (attributeValue6 != null) {
                        addDataSchemeSpecificPart(attributeValue6, 0);
                    } else {
                        String attributeValue7 = xmlPullParser.getAttributeValue(null, PREFIX_STR);
                        if (attributeValue7 != null) {
                            addDataSchemeSpecificPart(attributeValue7, 1);
                        } else {
                            String attributeValue8 = xmlPullParser.getAttributeValue(null, SGLOB_STR);
                            if (attributeValue8 != null) {
                                addDataSchemeSpecificPart(attributeValue8, 2);
                            } else {
                                String attributeValue9 = xmlPullParser.getAttributeValue(null, AGLOB_STR);
                                if (attributeValue9 != null) {
                                    addDataSchemeSpecificPart(attributeValue9, 3);
                                }
                            }
                        }
                    }
                } else if (name.equals(AUTH_STR)) {
                    String attributeValue10 = xmlPullParser.getAttributeValue(null, HOST_STR);
                    String attributeValue11 = xmlPullParser.getAttributeValue(null, "port");
                    if (attributeValue10 != null) {
                        addDataAuthority(attributeValue10, attributeValue11);
                    }
                } else if (name.equals(PATH_STR)) {
                    String attributeValue12 = xmlPullParser.getAttributeValue(null, LITERAL_STR);
                    if (attributeValue12 != null) {
                        addDataPath(attributeValue12, 0);
                    } else {
                        String attributeValue13 = xmlPullParser.getAttributeValue(null, PREFIX_STR);
                        if (attributeValue13 != null) {
                            addDataPath(attributeValue13, 1);
                        } else {
                            String attributeValue14 = xmlPullParser.getAttributeValue(null, SGLOB_STR);
                            if (attributeValue14 != null) {
                                addDataPath(attributeValue14, 2);
                            } else {
                                String attributeValue15 = xmlPullParser.getAttributeValue(null, AGLOB_STR);
                                if (attributeValue15 != null) {
                                    addDataPath(attributeValue15, 3);
                                }
                            }
                        }
                    }
                } else {
                    Log.w("IntentFilter", "Unknown tag parsing IntentFilter: " + name);
                }
                XmlUtils.skipCurrentTag(xmlPullParser);
            }
        }
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        if (this.mActions.size() > 0) {
            Iterator<String> it = this.mActions.iterator();
            while (it.hasNext()) {
                protoOutputStream.write(2237677961217L, it.next());
            }
        }
        ArrayList<String> arrayList = this.mCategories;
        if (arrayList != null) {
            Iterator<String> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                protoOutputStream.write(2237677961218L, it2.next());
            }
        }
        ArrayList<String> arrayList2 = this.mDataSchemes;
        if (arrayList2 != null) {
            Iterator<String> it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                protoOutputStream.write(2237677961219L, it3.next());
            }
        }
        ArrayList<PatternMatcher> arrayList3 = this.mDataSchemeSpecificParts;
        if (arrayList3 != null) {
            Iterator<PatternMatcher> it4 = arrayList3.iterator();
            while (it4.hasNext()) {
                it4.next().writeToProto(protoOutputStream, 2246267895812L);
            }
        }
        ArrayList<AuthorityEntry> arrayList4 = this.mDataAuthorities;
        if (arrayList4 != null) {
            Iterator<AuthorityEntry> it5 = arrayList4.iterator();
            while (it5.hasNext()) {
                it5.next().writeToProto(protoOutputStream, 2246267895813L);
            }
        }
        ArrayList<PatternMatcher> arrayList5 = this.mDataPaths;
        if (arrayList5 != null) {
            Iterator<PatternMatcher> it6 = arrayList5.iterator();
            while (it6.hasNext()) {
                it6.next().writeToProto(protoOutputStream, 2246267895814L);
            }
        }
        ArrayList<String> arrayList6 = this.mDataTypes;
        if (arrayList6 != null) {
            Iterator<String> it7 = arrayList6.iterator();
            while (it7.hasNext()) {
                protoOutputStream.write(2237677961223L, it7.next());
            }
        }
        if (this.mPriority != 0 || this.mHasPartialTypes) {
            protoOutputStream.write(1120986464264L, this.mPriority);
            protoOutputStream.write(1133871366153L, this.mHasPartialTypes);
        }
        protoOutputStream.write(1133871366154L, getAutoVerify());
        protoOutputStream.end(jStart);
    }

    public void dump(Printer printer, String str) {
        StringBuilder sb = new StringBuilder(256);
        if (this.mActions.size() > 0) {
            Iterator<String> it = this.mActions.iterator();
            while (it.hasNext()) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Action: \"");
                sb.append(it.next());
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        ArrayList<String> arrayList = this.mCategories;
        if (arrayList != null) {
            Iterator<String> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Category: \"");
                sb.append(it2.next());
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        ArrayList<String> arrayList2 = this.mDataSchemes;
        if (arrayList2 != null) {
            Iterator<String> it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Scheme: \"");
                sb.append(it3.next());
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        ArrayList<PatternMatcher> arrayList3 = this.mDataSchemeSpecificParts;
        if (arrayList3 != null) {
            for (PatternMatcher patternMatcher : arrayList3) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Ssp: \"");
                sb.append(patternMatcher);
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        ArrayList<AuthorityEntry> arrayList4 = this.mDataAuthorities;
        if (arrayList4 != null) {
            for (AuthorityEntry authorityEntry : arrayList4) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Authority: \"");
                sb.append(authorityEntry.mHost);
                sb.append("\": ");
                sb.append(authorityEntry.mPort);
                if (authorityEntry.mWild) {
                    sb.append(" WILD");
                }
                printer.println(sb.toString());
            }
        }
        ArrayList<PatternMatcher> arrayList5 = this.mDataPaths;
        if (arrayList5 != null) {
            for (PatternMatcher patternMatcher2 : arrayList5) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Path: \"");
                sb.append(patternMatcher2);
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        ArrayList<String> arrayList6 = this.mDataTypes;
        if (arrayList6 != null) {
            Iterator<String> it4 = arrayList6.iterator();
            while (it4.hasNext()) {
                sb.setLength(0);
                sb.append(str);
                sb.append("Type: \"");
                sb.append(it4.next());
                sb.append("\"");
                printer.println(sb.toString());
            }
        }
        if (this.mPriority != 0 || this.mOrder != 0 || this.mHasPartialTypes) {
            sb.setLength(0);
            sb.append(str);
            sb.append("mPriority=");
            sb.append(this.mPriority);
            sb.append(", mOrder=");
            sb.append(this.mOrder);
            sb.append(", mHasPartialTypes=");
            sb.append(this.mHasPartialTypes);
            printer.println(sb.toString());
        }
        if (getAutoVerify()) {
            sb.setLength(0);
            sb.append(str);
            sb.append("AutoVerify=");
            sb.append(getAutoVerify());
            printer.println(sb.toString());
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(this.mActions);
        if (this.mCategories != null) {
            parcel.writeInt(1);
            parcel.writeStringList(this.mCategories);
        } else {
            parcel.writeInt(0);
        }
        if (this.mDataSchemes != null) {
            parcel.writeInt(1);
            parcel.writeStringList(this.mDataSchemes);
        } else {
            parcel.writeInt(0);
        }
        if (this.mDataTypes != null) {
            parcel.writeInt(1);
            parcel.writeStringList(this.mDataTypes);
        } else {
            parcel.writeInt(0);
        }
        ArrayList<PatternMatcher> arrayList = this.mDataSchemeSpecificParts;
        if (arrayList != null) {
            int size = arrayList.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.mDataSchemeSpecificParts.get(i2).writeToParcel(parcel, i);
            }
        } else {
            parcel.writeInt(0);
        }
        ArrayList<AuthorityEntry> arrayList2 = this.mDataAuthorities;
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            parcel.writeInt(size2);
            for (int i3 = 0; i3 < size2; i3++) {
                this.mDataAuthorities.get(i3).writeToParcel(parcel);
            }
        } else {
            parcel.writeInt(0);
        }
        ArrayList<PatternMatcher> arrayList3 = this.mDataPaths;
        if (arrayList3 != null) {
            int size3 = arrayList3.size();
            parcel.writeInt(size3);
            for (int i4 = 0; i4 < size3; i4++) {
                this.mDataPaths.get(i4).writeToParcel(parcel, i);
            }
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mPriority);
        parcel.writeInt(this.mHasPartialTypes ? 1 : 0);
        parcel.writeInt(getAutoVerify() ? 1 : 0);
        parcel.writeInt(this.mInstantAppVisibility);
        parcel.writeInt(this.mOrder);
    }

    public IntentFilter(Parcel parcel) {
        this.mCategories = null;
        this.mDataSchemes = null;
        this.mDataSchemeSpecificParts = null;
        this.mDataAuthorities = null;
        this.mDataPaths = null;
        this.mDataTypes = null;
        this.mHasPartialTypes = false;
        ArrayList<String> arrayList = new ArrayList<>();
        this.mActions = arrayList;
        parcel.readStringList(arrayList);
        if (parcel.readInt() != 0) {
            ArrayList<String> arrayList2 = new ArrayList<>();
            this.mCategories = arrayList2;
            parcel.readStringList(arrayList2);
        }
        if (parcel.readInt() != 0) {
            ArrayList<String> arrayList3 = new ArrayList<>();
            this.mDataSchemes = arrayList3;
            parcel.readStringList(arrayList3);
        }
        if (parcel.readInt() != 0) {
            ArrayList<String> arrayList4 = new ArrayList<>();
            this.mDataTypes = arrayList4;
            parcel.readStringList(arrayList4);
        }
        int i = parcel.readInt();
        if (i > 0) {
            this.mDataSchemeSpecificParts = new ArrayList<>(i);
            for (int i2 = 0; i2 < i; i2++) {
                this.mDataSchemeSpecificParts.add(new PatternMatcher(parcel));
            }
        }
        int i3 = parcel.readInt();
        if (i3 > 0) {
            this.mDataAuthorities = new ArrayList<>(i3);
            for (int i4 = 0; i4 < i3; i4++) {
                this.mDataAuthorities.add(new AuthorityEntry(parcel));
            }
        }
        int i5 = parcel.readInt();
        if (i5 > 0) {
            this.mDataPaths = new ArrayList<>(i5);
            for (int i6 = 0; i6 < i5; i6++) {
                this.mDataPaths.add(new PatternMatcher(parcel));
            }
        }
        this.mPriority = parcel.readInt();
        this.mHasPartialTypes = parcel.readInt() > 0;
        setAutoVerify(parcel.readInt() > 0);
        setVisibilityToInstantApp(parcel.readInt());
        this.mOrder = parcel.readInt();
    }

    private final boolean findMimeType(String str) {
        ArrayList<String> arrayList = this.mDataTypes;
        if (str == null) {
            return false;
        }
        if (arrayList.contains(str)) {
            return true;
        }
        int length = str.length();
        if (length == 3 && str.equals("*/*")) {
            return !arrayList.isEmpty();
        }
        if (this.mHasPartialTypes && arrayList.contains(PhoneConstants.APN_TYPE_ALL)) {
            return true;
        }
        int iIndexOf = str.indexOf(47);
        if (iIndexOf > 0) {
            if (this.mHasPartialTypes && arrayList.contains(str.substring(0, iIndexOf))) {
                return true;
            }
            if (length == iIndexOf + 2) {
                int i = iIndexOf + 1;
                if (str.charAt(i) == '*') {
                    int size = arrayList.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        if (str.regionMatches(0, arrayList.get(i2), 0, i)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<String> getHostsList() {
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<AuthorityEntry> itAuthoritiesIterator = authoritiesIterator();
        if (itAuthoritiesIterator != null) {
            while (itAuthoritiesIterator.hasNext()) {
                arrayList.add(itAuthoritiesIterator.next().getHost());
            }
        }
        return arrayList;
    }

    public String[] getHosts() {
        ArrayList<String> hostsList = getHostsList();
        return (String[]) hostsList.toArray(new String[hostsList.size()]);
    }
}
