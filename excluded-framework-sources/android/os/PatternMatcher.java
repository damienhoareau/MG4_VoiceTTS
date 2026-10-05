package android.os;

import android.util.proto.ProtoOutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class PatternMatcher implements Parcelable {
    private static final int MAX_PATTERN_STORAGE = 2048;
    private static final int NO_MATCH = -1;
    private static final int PARSED_MODIFIER_ONE_OR_MORE = -8;
    private static final int PARSED_MODIFIER_RANGE_START = -5;
    private static final int PARSED_MODIFIER_RANGE_STOP = -6;
    private static final int PARSED_MODIFIER_ZERO_OR_MORE = -7;
    private static final int PARSED_TOKEN_CHAR_ANY = -4;
    private static final int PARSED_TOKEN_CHAR_SET_INVERSE_START = -2;
    private static final int PARSED_TOKEN_CHAR_SET_START = -1;
    private static final int PARSED_TOKEN_CHAR_SET_STOP = -3;
    public static final int PATTERN_ADVANCED_GLOB = 3;
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    private static final String TAG = "PatternMatcher";
    private static final int TOKEN_TYPE_ANY = 1;
    private static final int TOKEN_TYPE_INVERSE_SET = 3;
    private static final int TOKEN_TYPE_LITERAL = 0;
    private static final int TOKEN_TYPE_SET = 2;
    private final int[] mParsedPattern;
    private final String mPattern;
    private final int mType;
    private static final int[] sParsedPatternScratch = new int[2048];
    public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator<PatternMatcher>() { // from class: android.os.PatternMatcher.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher createFromParcel(Parcel parcel) {
            return new PatternMatcher(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher[] newArray(int i) {
            return new PatternMatcher[i];
        }
    };

    private static boolean isParsedModifier(int i) {
        return i == -8 || i == -7 || i == -6 || i == -5;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PatternMatcher(String str, int i) {
        this.mPattern = str;
        this.mType = i;
        if (i == 3) {
            this.mParsedPattern = parseAndVerifyAdvancedPattern(str);
        } else {
            this.mParsedPattern = null;
        }
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(str, this.mPattern, this.mParsedPattern, this.mType);
    }

    public String toString() {
        String str;
        int i = this.mType;
        if (i == 0) {
            str = "LITERAL: ";
        } else if (i == 1) {
            str = "PREFIX: ";
        } else if (i != 2) {
            str = i != 3 ? "? " : "ADVANCED: ";
        } else {
            str = "GLOB: ";
        }
        return "PatternMatcher{" + str + this.mPattern + "}";
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mPattern);
        protoOutputStream.write(1159641169922L, this.mType);
        protoOutputStream.end(jStart);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPattern);
        parcel.writeInt(this.mType);
        parcel.writeIntArray(this.mParsedPattern);
    }

    public PatternMatcher(Parcel parcel) {
        this.mPattern = parcel.readString();
        this.mType = parcel.readInt();
        this.mParsedPattern = parcel.createIntArray();
    }

    static boolean matchPattern(String str, String str2, int[] iArr, int i) {
        if (str == null) {
            return false;
        }
        if (i == 0) {
            return str2.equals(str);
        }
        if (i == 1) {
            return str.startsWith(str2);
        }
        if (i == 2) {
            return matchGlobPattern(str2, str);
        }
        if (i == 3) {
            return matchAdvancedPattern(iArr, str);
        }
        return false;
    }

    static boolean matchGlobPattern(String str, String str2) {
        int length = str.length();
        if (length <= 0) {
            return str2.length() <= 0;
        }
        int length2 = str2.length();
        char cCharAt = str.charAt(0);
        int i = 0;
        int i2 = 0;
        while (i < length && i2 < length2) {
            i++;
            char cCharAt2 = i < length ? str.charAt(i) : (char) 0;
            boolean z = cCharAt == '\\';
            if (z) {
                i++;
                char c = cCharAt2;
                cCharAt2 = i < length ? str.charAt(i) : (char) 0;
                cCharAt = c;
            }
            if (cCharAt2 == '*') {
                if (z || cCharAt != '.') {
                    while (str2.charAt(i2) == cCharAt && (i2 = i2 + 1) < length2) {
                    }
                    i++;
                    cCharAt = i < length ? str.charAt(i) : (char) 0;
                } else {
                    if (i >= length - 1) {
                        return true;
                    }
                    int i3 = i + 1;
                    char cCharAt3 = str.charAt(i3);
                    if (cCharAt3 == '\\') {
                        i3++;
                        cCharAt3 = i3 < length ? str.charAt(i3) : (char) 0;
                    }
                    while (str2.charAt(i2) != cCharAt3 && (i2 = i2 + 1) < length2) {
                    }
                    if (i2 == length2) {
                        return false;
                    }
                    i = i3 + 1;
                    cCharAt = i < length ? str.charAt(i) : (char) 0;
                    i2++;
                }
            } else {
                if (cCharAt != '.' && str2.charAt(i2) != cCharAt) {
                    return false;
                }
                i2++;
                cCharAt = cCharAt2;
            }
        }
        if (i < length || i2 < length2) {
            return i == length + (-2) && str.charAt(i) == '.' && str.charAt(i + 1) == '*';
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:100:0x01a7 A[Catch: all -> 0x01cb, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:113:0x0155 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:128:0x0143 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:129:0x0107 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:130:0x01a5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:131:0x019d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:132:0x018c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x0104 A[PHI: r4 r6 r7
  0x0104: PHI (r4v2 int) = (r4v1 int), (r4v1 int), (r4v1 int), (r4v1 int), (r4v19 int), (r4v1 int) binds: [B:59:0x00e4, B:52:0x00c4, B:49:0x00b8, B:42:0x0094, B:46:0x00a4, B:40:0x0086] A[DONT_GENERATE, DONT_INLINE]
  0x0104: PHI (r6v3 int) = (r6v1 int), (r6v1 int), (r6v1 int), (r6v1 int), (r6v12 int), (r6v1 int) binds: [B:59:0x00e4, B:52:0x00c4, B:49:0x00b8, B:42:0x0094, B:46:0x00a4, B:40:0x0086] A[DONT_GENERATE, DONT_INLINE]
  0x0104: PHI (r7v2 boolean) = (r7v1 boolean), (r7v1 boolean), (r7v1 boolean), (r7v1 boolean), (r7v9 boolean), (r7v1 boolean) binds: [B:59:0x00e4, B:52:0x00c4, B:49:0x00b8, B:42:0x0094, B:46:0x00a4, B:40:0x0086] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:69:0x0109 A[Catch: all -> 0x01cb, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:71:0x0113 A[Catch: all -> 0x01cb, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:73:0x0117 A[Catch: all -> 0x01cb, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:75:0x0121 A[Catch: all -> 0x01cb, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:79:0x0141 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:82:0x0149 A[Catch: all -> 0x01cb, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:8:0x0014, B:18:0x002e, B:69:0x0109, B:101:0x01ae, B:71:0x0113, B:73:0x0117, B:75:0x0121, B:77:0x0129, B:78:0x0134, B:80:0x0143, B:82:0x0149, B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170, B:95:0x0195, B:96:0x019c, B:97:0x019d, B:98:0x01a4, B:100:0x01a7, B:22:0x0037, B:25:0x0041, B:26:0x004e, B:27:0x0055, B:28:0x0056, B:30:0x005a, B:31:0x005f, B:32:0x0066, B:35:0x006a, B:37:0x0074, B:39:0x0082, B:38:0x007c, B:41:0x0088, B:44:0x0098, B:46:0x00a4, B:47:0x00b0, B:48:0x00b7, B:50:0x00ba, B:54:0x00c8, B:56:0x00d4, B:57:0x00dc, B:58:0x00e3, B:61:0x00e8, B:63:0x00f4, B:64:0x00fc, B:65:0x0103, B:102:0x01b1, B:103:0x01b8, B:105:0x01bb, B:108:0x01c3, B:109:0x01ca), top: B:115:0x0003, inners: #0 }] */
    /* JADX WARN: Code duplicated, block: B:85:0x015b A[Catch: NumberFormatException -> 0x0194, all -> 0x01cb, TryCatch #0 {NumberFormatException -> 0x0194, blocks: (B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170), top: B:113:0x0155, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:87:0x016a  */
    /* JADX WARN: Code duplicated, block: B:89:0x0170 A[Catch: NumberFormatException -> 0x0194, all -> 0x01cb, TryCatch #0 {NumberFormatException -> 0x0194, blocks: (B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170), top: B:113:0x0155, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:91:0x017d A[Catch: NumberFormatException -> 0x0194, all -> 0x01cb, TryCatch #0 {NumberFormatException -> 0x0194, blocks: (B:84:0x0155, B:91:0x017d, B:92:0x018c, B:93:0x0193, B:85:0x015b, B:89:0x0170), top: B:113:0x0155, outer: #1 }] */
    static synchronized int[] parseAndVerifyAdvancedPattern(String str) {
        int i;
        int i2;
        boolean z;
        int i3;
        int i4;
        int i5;
        int iIndexOf;
        String strSubstring;
        int iIndexOf2;
        int i6;
        int i7;
        int i8;
        int i9;
        int length = str.length();
        int i10 = 0;
        boolean z2 = false;
        i = 0;
        boolean z3 = false;
        boolean z4 = false;
        while (i10 < length) {
            if (i > 2045) {
                throw new IllegalArgumentException("Pattern is too large!");
            }
            char cCharAt = str.charAt(i10);
            if (cCharAt == '*') {
                if (z2) {
                    z = false;
                } else {
                    if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                        throw new IllegalArgumentException("Modifier must follow a token.");
                    }
                    i2 = i + 1;
                    sParsedPatternScratch[i] = -7;
                    z = false;
                    i = i2;
                }
                if (z2) {
                    if (z4) {
                        i4 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        z4 = false;
                    } else {
                        i3 = i10 + 2;
                        if (i3 < length) {
                            i5 = i10 + 1;
                            if (str.charAt(i5) != '-') {
                            }
                        }
                        int i11 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        i4 = i11 + 1;
                        sParsedPatternScratch[i11] = cCharAt;
                    }
                    i = i4;
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i10);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i10, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i6 = Integer.parseInt(strSubstring);
                        i7 = i6;
                    } else {
                        int i12 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i8 = Integer.MAX_VALUE;
                        } else {
                            i8 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                        i7 = i8;
                        i6 = i12;
                    }
                    if (i6 > i7) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i13 = i + 1;
                    sParsedPatternScratch[i] = i6;
                    i = i13 + 1;
                    sParsedPatternScratch[i13] = i7;
                    i10 = iIndexOf;
                } else if (z) {
                    sParsedPatternScratch[i] = cCharAt;
                    i++;
                }
                i10++;
            } else if (cCharAt != '+') {
                if (cCharAt == '.') {
                    if (!z2) {
                        i2 = i + 1;
                        sParsedPatternScratch[i] = -4;
                        z = false;
                        i = i2;
                    }
                    if (z2) {
                        if (z4) {
                            i4 = i + 1;
                            sParsedPatternScratch[i] = cCharAt;
                            z4 = false;
                        } else {
                            i3 = i10 + 2;
                            if (i3 < length) {
                                i5 = i10 + 1;
                                if (str.charAt(i5) != '-') {
                                }
                            }
                            int i14 = i + 1;
                            sParsedPatternScratch[i] = cCharAt;
                            i4 = i14 + 1;
                            sParsedPatternScratch[i14] = cCharAt;
                        }
                        i = i4;
                    } else if (z3) {
                        iIndexOf = str.indexOf(125, i10);
                        if (iIndexOf < 0) {
                            throw new IllegalArgumentException("Range not ended with '}'");
                        }
                        strSubstring = str.substring(i10, iIndexOf);
                        iIndexOf2 = strSubstring.indexOf(44);
                        if (iIndexOf2 < 0) {
                            i6 = Integer.parseInt(strSubstring);
                            i7 = i6;
                        } else {
                            int i15 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                            if (iIndexOf2 == strSubstring.length() - 1) {
                                i8 = Integer.MAX_VALUE;
                            } else {
                                i8 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                            }
                            i7 = i8;
                            i6 = i15;
                        }
                        if (i6 > i7) {
                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                        }
                        int i16 = i + 1;
                        sParsedPatternScratch[i] = i6;
                        i = i16 + 1;
                        sParsedPatternScratch[i16] = i7;
                        i10 = iIndexOf;
                    } else if (z) {
                        sParsedPatternScratch[i] = cCharAt;
                        i++;
                    }
                    i10++;
                } else if (cCharAt != '{') {
                    if (cCharAt != '}') {
                        switch (cCharAt) {
                            case '[':
                                if (!z2) {
                                    int i17 = i10 + 1;
                                    if (str.charAt(i17) == '^') {
                                        i9 = i + 1;
                                        sParsedPatternScratch[i] = -2;
                                        i10 = i17;
                                    } else {
                                        i9 = i + 1;
                                        sParsedPatternScratch[i] = -1;
                                    }
                                    i = i9;
                                    i10++;
                                    z2 = true;
                                } else {
                                    z = true;
                                }
                                break;
                            case '\\':
                                i10++;
                                if (i10 >= length) {
                                    throw new IllegalArgumentException("Escape found at end of pattern!");
                                }
                                cCharAt = str.charAt(i10);
                                z = true;
                                break;
                                break;
                            case ']':
                                if (!z2) {
                                    z = true;
                                } else {
                                    int i18 = sParsedPatternScratch[i - 1];
                                    if (i18 == -1 || i18 == -2) {
                                        throw new IllegalArgumentException("You must define characters in a set.");
                                    }
                                    sParsedPatternScratch[i] = -3;
                                    z2 = false;
                                    z = false;
                                    i++;
                                    z4 = false;
                                }
                                break;
                            default:
                                z = true;
                                break;
                        }
                    } else if (z3) {
                        sParsedPatternScratch[i] = -6;
                        z3 = false;
                        i++;
                        z = false;
                    }
                    if (z2) {
                        if (z4) {
                            i4 = i + 1;
                            sParsedPatternScratch[i] = cCharAt;
                            z4 = false;
                        } else {
                            i3 = i10 + 2;
                            if (i3 < length) {
                                i5 = i10 + 1;
                                if (str.charAt(i5) != '-' && str.charAt(i3) != ']') {
                                    sParsedPatternScratch[i] = cCharAt;
                                    i++;
                                    i10 = i5;
                                    z4 = true;
                                }
                            }
                            int i19 = i + 1;
                            sParsedPatternScratch[i] = cCharAt;
                            i4 = i19 + 1;
                            sParsedPatternScratch[i19] = cCharAt;
                        }
                        i = i4;
                    } else if (z3) {
                        iIndexOf = str.indexOf(125, i10);
                        if (iIndexOf < 0) {
                            throw new IllegalArgumentException("Range not ended with '}'");
                        }
                        strSubstring = str.substring(i10, iIndexOf);
                        iIndexOf2 = strSubstring.indexOf(44);
                        if (iIndexOf2 < 0) {
                            try {
                                i6 = Integer.parseInt(strSubstring);
                                i7 = i6;
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Range number format incorrect", e);
                            }
                        } else {
                            int i110 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                            if (iIndexOf2 == strSubstring.length() - 1) {
                                i8 = Integer.MAX_VALUE;
                            } else {
                                i8 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                            }
                            i7 = i8;
                            i6 = i110;
                        }
                        if (i6 > i7) {
                            throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                        }
                        int i111 = i + 1;
                        sParsedPatternScratch[i] = i6;
                        i = i111 + 1;
                        sParsedPatternScratch[i111] = i7;
                        i10 = iIndexOf;
                    } else if (z) {
                        sParsedPatternScratch[i] = cCharAt;
                        i++;
                    }
                    i10++;
                } else if (!z2) {
                    if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                        throw new IllegalArgumentException("Modifier must follow a token.");
                    }
                    sParsedPatternScratch[i] = -5;
                    i10++;
                    z3 = true;
                    i++;
                }
                z = false;
                if (z2) {
                    if (z4) {
                        i4 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        z4 = false;
                    } else {
                        i3 = i10 + 2;
                        if (i3 < length) {
                            i5 = i10 + 1;
                            if (str.charAt(i5) != '-') {
                            }
                        }
                        int i112 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        i4 = i112 + 1;
                        sParsedPatternScratch[i112] = cCharAt;
                    }
                    i = i4;
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i10);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i10, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i6 = Integer.parseInt(strSubstring);
                        i7 = i6;
                    } else {
                        int i113 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i8 = Integer.MAX_VALUE;
                        } else {
                            i8 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                        i7 = i8;
                        i6 = i113;
                    }
                    if (i6 > i7) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i114 = i + 1;
                    sParsedPatternScratch[i] = i6;
                    i = i114 + 1;
                    sParsedPatternScratch[i114] = i7;
                    i10 = iIndexOf;
                } else if (z) {
                    sParsedPatternScratch[i] = cCharAt;
                    i++;
                }
                i10++;
            } else {
                if (z2) {
                    z = false;
                } else {
                    if (i == 0 || isParsedModifier(sParsedPatternScratch[i - 1])) {
                        throw new IllegalArgumentException("Modifier must follow a token.");
                    }
                    i2 = i + 1;
                    sParsedPatternScratch[i] = -8;
                    z = false;
                    i = i2;
                }
                if (z2) {
                    if (z4) {
                        i4 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        z4 = false;
                    } else {
                        i3 = i10 + 2;
                        if (i3 < length) {
                            i5 = i10 + 1;
                            if (str.charAt(i5) != '-') {
                            }
                        }
                        int i115 = i + 1;
                        sParsedPatternScratch[i] = cCharAt;
                        i4 = i115 + 1;
                        sParsedPatternScratch[i115] = cCharAt;
                    }
                    i = i4;
                } else if (z3) {
                    iIndexOf = str.indexOf(125, i10);
                    if (iIndexOf < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    strSubstring = str.substring(i10, iIndexOf);
                    iIndexOf2 = strSubstring.indexOf(44);
                    if (iIndexOf2 < 0) {
                        i6 = Integer.parseInt(strSubstring);
                        i7 = i6;
                    } else {
                        int i116 = Integer.parseInt(strSubstring.substring(0, iIndexOf2));
                        if (iIndexOf2 == strSubstring.length() - 1) {
                            i8 = Integer.MAX_VALUE;
                        } else {
                            i8 = Integer.parseInt(strSubstring.substring(iIndexOf2 + 1));
                        }
                        i7 = i8;
                        i6 = i116;
                    }
                    if (i6 > i7) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int i117 = i + 1;
                    sParsedPatternScratch[i] = i6;
                    i = i117 + 1;
                    sParsedPatternScratch[i117] = i7;
                    i10 = iIndexOf;
                } else if (z) {
                    sParsedPatternScratch[i] = cCharAt;
                    i++;
                }
                i10++;
            }
        }
        if (z2) {
            throw new IllegalArgumentException("Set was not terminated!");
        }
        return Arrays.copyOf(sParsedPatternScratch, i);
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0047  */
    /* JADX WARN: Code duplicated, block: B:26:0x004c  */
    /* JADX WARN: Code duplicated, block: B:28:0x0051  */
    /* JADX WARN: Code duplicated, block: B:30:0x0054  */
    /* JADX WARN: Code duplicated, block: B:33:0x0058  */
    /* JADX WARN: Code duplicated, block: B:34:0x0065  */
    /* JADX WARN: Code duplicated, block: B:35:0x006c  */
    static boolean matchAdvancedPattern(int[] iArr, String str) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int iMatchChars;
        int length = iArr.length;
        int length2 = str.length();
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        int i13 = 0;
        while (i10 < length) {
            int i14 = iArr[i10];
            if (i14 != -4) {
                if (i14 == -2 || i14 == -1) {
                    int i15 = i14 == -1 ? 2 : 3;
                    int i16 = i10 + 1;
                    do {
                        i10++;
                        if (i10 >= length) {
                            break;
                        }
                    } while (iArr[i10] != -3);
                    int i17 = i10 - 1;
                    i = i10 + 1;
                    i2 = i16;
                    i3 = i17;
                    i4 = i15;
                } else {
                    i5 = i10 + 1;
                    i2 = i10;
                    i3 = i12;
                    i4 = 0;
                }
                if (i5 < length) {
                    i6 = iArr[i5];
                    if (i6 != -8) {
                        i7 = Integer.MAX_VALUE;
                        i8 = i5 + 1;
                        i9 = 1;
                    } else if (i6 != -7) {
                        i7 = Integer.MAX_VALUE;
                        i8 = i5 + 1;
                        i9 = 0;
                    } else if (i6 != -5) {
                        i8 = i5;
                        i9 = 1;
                        i7 = 1;
                    } else {
                        int i18 = i5 + 1;
                        int i19 = iArr[i18];
                        int i20 = i18 + 1;
                        i9 = i19;
                        i8 = i20 + 2;
                        i7 = iArr[i20];
                    }
                } else {
                    i8 = i5;
                    i9 = 1;
                    i7 = 1;
                }
                if (i9 <= i7 || (iMatchChars = matchChars(str, i13, length2, i4, i9, i7, iArr, i2, i3)) == -1) {
                    return false;
                }
                i13 += iMatchChars;
                i11 = i2;
                i12 = i3;
                i10 = i8;
            } else {
                i = i10 + 1;
                i2 = i11;
                i3 = i12;
                i4 = 1;
            }
            i5 = i;
            if (i5 < length) {
                i6 = iArr[i5];
                if (i6 != -8) {
                    i7 = Integer.MAX_VALUE;
                    i8 = i5 + 1;
                    i9 = 1;
                } else if (i6 != -7) {
                    i7 = Integer.MAX_VALUE;
                    i8 = i5 + 1;
                    i9 = 0;
                } else if (i6 != -5) {
                    i8 = i5;
                    i9 = 1;
                    i7 = 1;
                } else {
                    int i110 = i5 + 1;
                    int i111 = iArr[i110];
                    int i21 = i110 + 1;
                    i9 = i111;
                    i8 = i21 + 2;
                    i7 = iArr[i21];
                }
            } else {
                i8 = i5;
                i9 = 1;
                i7 = 1;
            }
            if (i9 <= i7) {
                return false;
            }
            i13 += iMatchChars;
            i11 = i2;
            i12 = i3;
            i10 = i8;
        }
        return i10 >= length && i13 >= length2;
    }

    private static int matchChars(String str, int i, int i2, int i3, int i4, int i5, int[] iArr, int i6, int i7) {
        int i8 = 0;
        while (i8 < i5 && matchChar(str, i + i8, i2, i3, iArr, i6, i7)) {
            i8++;
        }
        if (i8 < i4) {
            return -1;
        }
        return i8;
    }

    private static boolean matchChar(String str, int i, int i2, int i3, int[] iArr, int i4, int i5) {
        if (i >= i2) {
            return false;
        }
        if (i3 == 0) {
            return str.charAt(i) == iArr[i4];
        }
        if (i3 == 1) {
            return true;
        }
        if (i3 == 2) {
            while (i4 < i5) {
                char cCharAt = str.charAt(i);
                if (cCharAt >= iArr[i4] && cCharAt <= iArr[i4 + 1]) {
                    return true;
                }
                i4 += 2;
            }
            return false;
        }
        if (i3 != 3) {
            return false;
        }
        while (i4 < i5) {
            char cCharAt2 = str.charAt(i);
            if (cCharAt2 >= iArr[i4] && cCharAt2 <= iArr[i4 + 1]) {
                return false;
            }
            i4 += 2;
        }
        return true;
    }
}
