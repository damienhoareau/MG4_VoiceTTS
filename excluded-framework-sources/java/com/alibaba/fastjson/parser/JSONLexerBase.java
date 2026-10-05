package com.alibaba.fastjson.parser;

import android.media.AudioSystem;
import android.text.format.DateFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.Closeable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/* JADX INFO: loaded from: classes3.dex */
public abstract class JSONLexerBase implements JSONLexer, Closeable {
    protected static final int INT_MULTMIN_RADIX_TEN = -214748364;
    protected static final long MULTMIN_RADIX_TEN = -922337203685477580L;
    protected int bp;
    protected char ch;
    protected int eofPos;
    protected int features;
    protected boolean hasSpecial;
    protected int np;
    protected int pos;
    protected char[] sbuf;
    protected int sp;
    protected String stringDefaultValue;
    protected int token;
    private static final ThreadLocal<char[]> SBUF_LOCAL = new ThreadLocal<>();
    protected static final char[] typeFieldName = ("\"" + JSON.DEFAULT_TYPE_KEY + "\":\"").toCharArray();
    protected static final int[] digits = new int[103];
    protected Calendar calendar = null;
    protected TimeZone timeZone = JSON.defaultTimeZone;
    protected Locale locale = JSON.defaultLocale;
    public int matchStat = 0;
    protected int nanos = 0;

    public static boolean isWhitespace(char c) {
        return c <= ' ' && (c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b');
    }

    public abstract String addSymbol(int i, int i2, int i3, SymbolTable symbolTable);

    protected abstract void arrayCopy(int i, char[] cArr, int i2, int i3);

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public abstract byte[] bytesValue();

    protected abstract boolean charArrayCompare(char[] cArr);

    public abstract char charAt(int i);

    protected abstract void copyTo(int i, int i2, char[] cArr);

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public abstract BigDecimal decimalValue();

    public abstract int indexOf(char c, int i);

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public String info() {
        return "";
    }

    public abstract boolean isEOF();

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public abstract char next();

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public abstract String numberString();

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public String scanTypeName(SymbolTable symbolTable) {
        return null;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public abstract String stringVal();

    public abstract String subString(int i, int i2);

    protected abstract char[] sub_chars(int i, int i2);

    protected void lexError(String str, Object... objArr) {
        this.token = 1;
    }

    static {
        for (int i = 48; i <= 57; i++) {
            digits[i] = i - 48;
        }
        for (int i2 = 97; i2 <= 102; i2++) {
            digits[i2] = (i2 - 97) + 10;
        }
        for (int i3 = 65; i3 <= 70; i3++) {
            digits[i3] = (i3 - 65) + 10;
        }
    }

    public JSONLexerBase(int i) {
        this.stringDefaultValue = null;
        this.features = i;
        if ((i & Feature.InitStringFieldAsEmpty.mask) != 0) {
            this.stringDefaultValue = "";
        }
        char[] cArr = SBUF_LOCAL.get();
        this.sbuf = cArr;
        if (cArr == null) {
            this.sbuf = new char[512];
        }
    }

    public final int matchStat() {
        return this.matchStat;
    }

    public void setToken(int i) {
        this.token = i;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void nextToken() {
        this.sp = 0;
        while (true) {
            this.pos = this.bp;
            char c = this.ch;
            if (c == '/') {
                skipComment();
            } else {
                if (c == '\"') {
                    scanString();
                    return;
                }
                if (c == ',') {
                    next();
                    this.token = 16;
                    return;
                }
                if (c >= '0' && c <= '9') {
                    scanNumber();
                    return;
                }
                char c2 = this.ch;
                if (c2 == '-') {
                    scanNumber();
                    return;
                }
                switch (c2) {
                    case '\b':
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                        next();
                        break;
                    case '\'':
                        if (!isEnabled(Feature.AllowSingleQuotes)) {
                            throw new JSONException("Feature.AllowSingleQuotes is false");
                        }
                        scanStringSingleQuote();
                        return;
                    case '(':
                        next();
                        this.token = 10;
                        return;
                    case ')':
                        next();
                        this.token = 11;
                        return;
                    case '+':
                        next();
                        scanNumber();
                        return;
                    case '.':
                        next();
                        this.token = 25;
                        return;
                    case ':':
                        next();
                        this.token = 17;
                        return;
                    case ';':
                        next();
                        this.token = 24;
                        return;
                    case 'N':
                    case 'S':
                    case 'T':
                    case 'u':
                        scanIdent();
                        return;
                    case '[':
                        next();
                        this.token = 14;
                        return;
                    case ']':
                        next();
                        this.token = 15;
                        return;
                    case 'f':
                        scanFalse();
                        return;
                    case 'n':
                        scanNullOrNew();
                        return;
                    case 't':
                        scanTrue();
                        return;
                    case 'x':
                        scanHex();
                        return;
                    case '{':
                        next();
                        this.token = 12;
                        return;
                    case '}':
                        next();
                        this.token = 13;
                        return;
                    default:
                        if (isEOF()) {
                            if (this.token == 20) {
                                throw new JSONException("EOF error");
                            }
                            this.token = 20;
                            int i = this.bp;
                            this.pos = i;
                            this.eofPos = i;
                            return;
                        }
                        char c3 = this.ch;
                        if (c3 <= 31 || c3 == 127) {
                            next();
                        } else {
                            lexError("illegal.char", String.valueOf((int) c3));
                            next();
                            return;
                        }
                        break;
                        break;
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:110:0x007b A[SYNTHETIC] */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void nextToken(int i) {
        this.sp = 0;
        while (true) {
            if (i == 2) {
                char c = this.ch;
                if (c >= '0' && c <= '9') {
                    this.pos = this.bp;
                    scanNumber();
                    return;
                }
                char c2 = this.ch;
                if (c2 == '\"') {
                    this.pos = this.bp;
                    scanString();
                    return;
                } else if (c2 == '[') {
                    this.token = 14;
                    next();
                    return;
                } else if (c2 == '{') {
                    this.token = 12;
                    next();
                    return;
                }
            } else if (i == 4) {
                char c3 = this.ch;
                if (c3 == '\"') {
                    this.pos = this.bp;
                    scanString();
                    return;
                }
                if (c3 >= '0' && c3 <= '9') {
                    this.pos = this.bp;
                    scanNumber();
                    return;
                }
                char c4 = this.ch;
                if (c4 == '[') {
                    this.token = 14;
                    next();
                    return;
                } else if (c4 == '{') {
                    this.token = 12;
                    next();
                    return;
                }
            } else if (i == 12) {
                char c5 = this.ch;
                if (c5 == '{') {
                    this.token = 12;
                    next();
                    return;
                } else if (c5 == '[') {
                    this.token = 14;
                    next();
                    return;
                }
            } else {
                if (i == 18) {
                    nextIdent();
                    return;
                }
                if (i != 20) {
                    switch (i) {
                        case 14:
                            char c6 = this.ch;
                            if (c6 == '[') {
                                this.token = 14;
                                next();
                            } else if (c6 == '{') {
                                this.token = 12;
                                next();
                            }
                            break;
                        case 15:
                            if (this.ch == ']') {
                                this.token = 15;
                                next();
                            }
                            if (this.ch == 26) {
                                this.token = 20;
                            }
                            break;
                        case 16:
                            char c7 = this.ch;
                            if (c7 == ',') {
                                this.token = 16;
                                next();
                            } else if (c7 == '}') {
                                this.token = 13;
                                next();
                            } else if (c7 == ']') {
                                this.token = 15;
                                next();
                            } else if (c7 == 26) {
                                this.token = 20;
                            } else if (c7 == 'n') {
                                scanNullOrNew(false);
                            }
                            break;
                    }
                    return;
                }
                if (this.ch == 26) {
                    this.token = 20;
                    return;
                }
            }
            char c8 = this.ch;
            if (c8 == ' ' || c8 == '\n' || c8 == '\r' || c8 == '\t' || c8 == '\f' || c8 == '\b') {
                next();
            } else {
                nextToken();
                return;
            }
        }
    }

    public final void nextIdent() {
        while (isWhitespace(this.ch)) {
            next();
        }
        char c = this.ch;
        if (c == '_' || c == '$' || Character.isLetter(c)) {
            scanIdent();
        } else {
            nextToken();
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void nextTokenWithColon() {
        nextTokenWithChar(':');
    }

    public final void nextTokenWithChar(char c) {
        this.sp = 0;
        while (true) {
            char c2 = this.ch;
            if (c2 == c) {
                next();
                nextToken();
                return;
            }
            if (c2 == ' ' || c2 == '\n' || c2 == '\r' || c2 == '\t' || c2 == '\f' || c2 == '\b') {
                next();
            } else {
                throw new JSONException("not match " + c + " - " + this.ch + ", info : " + info());
            }
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final int token() {
        return this.token;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final String tokenName() {
        return JSONToken.name(this.token);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final int pos() {
        return this.pos;
    }

    public final String stringDefaultValue() {
        return this.stringDefaultValue;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final Number integerValue() throws NumberFormatException {
        long j;
        long j2;
        boolean z = false;
        if (this.np == -1) {
            this.np = 0;
        }
        int i = this.np;
        int i2 = this.sp + i;
        char c = ' ';
        char cCharAt = charAt(i2 - 1);
        if (cCharAt == 'B') {
            i2--;
            c = 'B';
        } else if (cCharAt == 'L') {
            i2--;
            c = 'L';
        } else if (cCharAt == 'S') {
            i2--;
            c = 'S';
        }
        if (charAt(this.np) == '-') {
            j = Long.MIN_VALUE;
            i++;
            z = true;
        } else {
            j = -9223372036854775807L;
        }
        long j3 = MULTMIN_RADIX_TEN;
        if (i < i2) {
            j2 = -(charAt(i) - '0');
            i++;
        } else {
            j2 = 0;
        }
        while (i < i2) {
            int i3 = i + 1;
            int iCharAt = charAt(i) - '0';
            if (j2 < j3) {
                return new BigInteger(numberString(), 10);
            }
            long j4 = j2 * 10;
            long j5 = iCharAt;
            if (j4 < j + j5) {
                return new BigInteger(numberString(), 10);
            }
            j2 = j4 - j5;
            i = i3;
            j3 = MULTMIN_RADIX_TEN;
        }
        if (!z) {
            long j6 = -j2;
            if (j6 > 2147483647L || c == 'L') {
                return Long.valueOf(j6);
            }
            if (c == 'S') {
                return Short.valueOf((short) j6);
            }
            if (c == 'B') {
                return Byte.valueOf((byte) j6);
            }
            return Integer.valueOf((int) j6);
        }
        if (i <= this.np + 1) {
            throw new JSONException("illegal number format : " + numberString());
        }
        if (j2 < -2147483648L || c == 'L') {
            return Long.valueOf(j2);
        }
        if (c == 'S') {
            return Short.valueOf((short) j2);
        }
        if (c == 'B') {
            return Byte.valueOf((byte) j2);
        }
        return Integer.valueOf((int) j2);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void nextTokenWithColon(int i) {
        nextTokenWithChar(':');
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public float floatValue() {
        char cCharAt;
        String strNumberString = numberString();
        float f = Float.parseFloat(strNumberString);
        if ((f != 0.0f && f != Float.POSITIVE_INFINITY) || (cCharAt = strNumberString.charAt(0)) <= '0' || cCharAt > '9') {
            return f;
        }
        throw new JSONException("float overflow : " + strNumberString);
    }

    public double doubleValue() {
        return Double.parseDouble(numberString());
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public void config(Feature feature, boolean z) {
        int iConfig = Feature.config(this.features, feature, z);
        this.features = iConfig;
        if ((iConfig & Feature.InitStringFieldAsEmpty.mask) != 0) {
            this.stringDefaultValue = "";
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final boolean isEnabled(Feature feature) {
        return isEnabled(feature.mask);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final boolean isEnabled(int i) {
        return (i & this.features) != 0;
    }

    public final boolean isEnabled(int i, int i2) {
        return ((this.features & i2) == 0 && (i & i2) == 0) ? false : true;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final char getCurrent() {
        return this.ch;
    }

    protected void skipComment() {
        char c;
        next();
        char c2 = this.ch;
        if (c2 == '/') {
            do {
                next();
                c = this.ch;
                if (c == '\n') {
                    next();
                    return;
                }
            } while (c != 26);
            return;
        }
        if (c2 == '*') {
            next();
            while (true) {
                char c3 = this.ch;
                if (c3 == 26) {
                    return;
                }
                if (c3 == '*') {
                    next();
                    if (this.ch == '/') {
                        next();
                        return;
                    }
                } else {
                    next();
                }
            }
        } else {
            throw new JSONException("invalid comment");
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final String scanSymbol(SymbolTable symbolTable) {
        skipWhitespace();
        char c = this.ch;
        if (c == '\"') {
            return scanSymbol(symbolTable, '\"');
        }
        if (c == '\'') {
            if (!isEnabled(Feature.AllowSingleQuotes)) {
                throw new JSONException("syntax error");
            }
            return scanSymbol(symbolTable, DateFormat.QUOTE);
        }
        if (c == '}') {
            next();
            this.token = 13;
            return null;
        }
        if (c == ',') {
            next();
            this.token = 16;
            return null;
        }
        if (c == 26) {
            this.token = 20;
            return null;
        }
        if (!isEnabled(Feature.AllowUnQuotedFieldNames)) {
            throw new JSONException("syntax error");
        }
        return scanSymbolUnQuoted(symbolTable);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final String scanSymbol(SymbolTable symbolTable, char c) {
        String strAddSymbol;
        this.np = this.bp;
        this.sp = 0;
        boolean z = false;
        int i = 0;
        while (true) {
            char next = next();
            if (next == c) {
                this.token = 4;
                if (!z) {
                    int i2 = this.np;
                    strAddSymbol = addSymbol(i2 == -1 ? 0 : i2 + 1, this.sp, i, symbolTable);
                } else {
                    strAddSymbol = symbolTable.addSymbol(this.sbuf, 0, this.sp, i);
                }
                this.sp = 0;
                next();
                return strAddSymbol;
            }
            if (next == 26) {
                throw new JSONException("unclosed.str");
            }
            if (next == '\\') {
                if (!z) {
                    int i3 = this.sp;
                    char[] cArr = this.sbuf;
                    if (i3 >= cArr.length) {
                        int length = cArr.length * 2;
                        if (i3 <= length) {
                            i3 = length;
                        }
                        char[] cArr2 = new char[i3];
                        char[] cArr3 = this.sbuf;
                        System.arraycopy(cArr3, 0, cArr2, 0, cArr3.length);
                        this.sbuf = cArr2;
                    }
                    arrayCopy(this.np + 1, this.sbuf, 0, this.sp);
                    z = true;
                }
                char next2 = next();
                if (next2 == '\"') {
                    i = (i * 31) + 34;
                    putChar('\"');
                } else if (next2 != '\'') {
                    if (next2 != 'F') {
                        if (next2 == '\\') {
                            i = (i * 31) + 92;
                            putChar('\\');
                        } else if (next2 == 'b') {
                            i = (i * 31) + 8;
                            putChar('\b');
                        } else if (next2 != 'f') {
                            if (next2 == 'n') {
                                i = (i * 31) + 10;
                                putChar('\n');
                            } else if (next2 == 'r') {
                                i = (i * 31) + 13;
                                putChar('\r');
                            } else if (next2 != 'x') {
                                switch (next2) {
                                    case '/':
                                        i = (i * 31) + 47;
                                        putChar('/');
                                        break;
                                    case '0':
                                        i = (i * 31) + next2;
                                        putChar((char) 0);
                                        break;
                                    case '1':
                                        i = (i * 31) + next2;
                                        putChar((char) 1);
                                        break;
                                    case '2':
                                        i = (i * 31) + next2;
                                        putChar((char) 2);
                                        break;
                                    case '3':
                                        i = (i * 31) + next2;
                                        putChar((char) 3);
                                        break;
                                    case '4':
                                        i = (i * 31) + next2;
                                        putChar((char) 4);
                                        break;
                                    case '5':
                                        i = (i * 31) + next2;
                                        putChar((char) 5);
                                        break;
                                    case '6':
                                        i = (i * 31) + next2;
                                        putChar((char) 6);
                                        break;
                                    case '7':
                                        i = (i * 31) + next2;
                                        putChar((char) 7);
                                        break;
                                    default:
                                        switch (next2) {
                                            case 't':
                                                i = (i * 31) + 9;
                                                putChar('\t');
                                                break;
                                            case 'u':
                                                int i4 = Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16);
                                                i = (i * 31) + i4;
                                                putChar((char) i4);
                                                break;
                                            case 'v':
                                                i = (i * 31) + 11;
                                                putChar((char) 11);
                                                break;
                                            default:
                                                this.ch = next2;
                                                throw new JSONException("unclosed.str.lit");
                                        }
                                        break;
                                }
                            } else {
                                char next3 = next();
                                this.ch = next3;
                                char next4 = next();
                                this.ch = next4;
                                int[] iArr = digits;
                                char c2 = (char) ((iArr[next3] * 16) + iArr[next4]);
                                i = (i * 31) + c2;
                                putChar(c2);
                            }
                        }
                    }
                    i = (i * 31) + 12;
                    putChar('\f');
                } else {
                    i = (i * 31) + 39;
                    putChar(DateFormat.QUOTE);
                }
            } else {
                i = (i * 31) + next;
                if (!z) {
                    this.sp++;
                } else {
                    int i5 = this.sp;
                    char[] cArr4 = this.sbuf;
                    if (i5 == cArr4.length) {
                        putChar(next);
                    } else {
                        this.sp = i5 + 1;
                        cArr4[i5] = next;
                    }
                }
            }
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void resetStringPosition() {
        this.sp = 0;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final String scanSymbolUnQuoted(SymbolTable symbolTable) {
        if (this.token == 1 && this.pos == 0 && this.bp == 1) {
            this.bp = 0;
        }
        boolean[] zArr = IOUtils.firstIdentifierFlags;
        int i = this.ch;
        if (!(i >= zArr.length || zArr[i])) {
            throw new JSONException("illegal identifier : " + this.ch + info());
        }
        boolean[] zArr2 = IOUtils.identifierFlags;
        this.np = this.bp;
        this.sp = 1;
        while (true) {
            char next = next();
            if (next < zArr2.length && !zArr2[next]) {
                break;
            }
            i = (i * 31) + next;
            this.sp++;
        }
        this.ch = charAt(this.bp);
        this.token = 18;
        if (this.sp == 4 && i == 3392903 && charAt(this.np) == 'n' && charAt(this.np + 1) == 'u' && charAt(this.np + 2) == 'l' && charAt(this.np + 3) == 'l') {
            return null;
        }
        if (symbolTable == null) {
            return subString(this.np, this.sp);
        }
        return addSymbol(this.np, this.sp, i, symbolTable);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void scanString() {
        this.np = this.bp;
        this.hasSpecial = false;
        while (true) {
            char next = next();
            if (next == '\"') {
                this.token = 4;
                this.ch = next();
                return;
            }
            if (next != 26) {
                boolean z = true;
                if (next == '\\') {
                    if (!this.hasSpecial) {
                        this.hasSpecial = true;
                        int i = this.sp;
                        char[] cArr = this.sbuf;
                        if (i >= cArr.length) {
                            int length = cArr.length * 2;
                            if (i <= length) {
                                i = length;
                            }
                            char[] cArr2 = new char[i];
                            char[] cArr3 = this.sbuf;
                            System.arraycopy(cArr3, 0, cArr2, 0, cArr3.length);
                            this.sbuf = cArr2;
                        }
                        copyTo(this.np + 1, this.sp, this.sbuf);
                    }
                    char next2 = next();
                    if (next2 == '\"') {
                        putChar('\"');
                    } else if (next2 != '\'') {
                        if (next2 != 'F') {
                            if (next2 == '\\') {
                                putChar('\\');
                            } else if (next2 == 'b') {
                                putChar('\b');
                            } else if (next2 != 'f') {
                                if (next2 == 'n') {
                                    putChar('\n');
                                } else if (next2 == 'r') {
                                    putChar('\r');
                                } else if (next2 != 'x') {
                                    switch (next2) {
                                        case '/':
                                            putChar('/');
                                            break;
                                        case '0':
                                            putChar((char) 0);
                                            break;
                                        case '1':
                                            putChar((char) 1);
                                            break;
                                        case '2':
                                            putChar((char) 2);
                                            break;
                                        case '3':
                                            putChar((char) 3);
                                            break;
                                        case '4':
                                            putChar((char) 4);
                                            break;
                                        case '5':
                                            putChar((char) 5);
                                            break;
                                        case '6':
                                            putChar((char) 6);
                                            break;
                                        case '7':
                                            putChar((char) 7);
                                            break;
                                        default:
                                            switch (next2) {
                                                case 't':
                                                    putChar('\t');
                                                    break;
                                                case 'u':
                                                    putChar((char) Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16));
                                                    break;
                                                case 'v':
                                                    putChar((char) 11);
                                                    break;
                                                default:
                                                    this.ch = next2;
                                                    throw new JSONException("unclosed string : " + next2);
                                            }
                                            break;
                                    }
                                } else {
                                    char next3 = next();
                                    char next4 = next();
                                    boolean z2 = (next3 >= '0' && next3 <= '9') || (next3 >= 'a' && next3 <= 'f') || (next3 >= 'A' && next3 <= 'F');
                                    if ((next4 < '0' || next4 > '9') && ((next4 < 'a' || next4 > 'f') && (next4 < 'A' || next4 > 'F'))) {
                                        z = false;
                                    }
                                    if (!z2 || !z) {
                                        throw new JSONException("invalid escape character \\x" + next3 + next4);
                                    }
                                    int[] iArr = digits;
                                    putChar((char) ((iArr[next3] * 16) + iArr[next4]));
                                }
                            }
                        }
                        putChar('\f');
                    } else {
                        putChar(DateFormat.QUOTE);
                    }
                } else if (!this.hasSpecial) {
                    this.sp++;
                } else {
                    int i2 = this.sp;
                    char[] cArr4 = this.sbuf;
                    if (i2 == cArr4.length) {
                        putChar(next);
                    } else {
                        this.sp = i2 + 1;
                        cArr4[i2] = next;
                    }
                }
            } else if (!isEOF()) {
                putChar(JSONLexer.EOI);
            } else {
                throw new JSONException("unclosed string : " + next);
            }
        }
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public Locale getLocale() {
        return this.locale;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final int intValue() {
        int i;
        boolean z;
        int i2 = 0;
        if (this.np == -1) {
            this.np = 0;
        }
        int i3 = this.np;
        int i4 = this.sp + i3;
        if (charAt(i3) == '-') {
            i = Integer.MIN_VALUE;
            i3++;
            z = true;
        } else {
            i = AudioSystem.DEVICE_IN_COMMUNICATION;
            z = false;
        }
        if (i3 < i4) {
            i2 = -(charAt(i3) - '0');
            i3++;
        }
        while (i3 < i4) {
            int i5 = i3 + 1;
            char cCharAt = charAt(i3);
            if (cCharAt == 'L' || cCharAt == 'S' || cCharAt == 'B') {
                i3 = i5;
                break;
            }
            int i6 = cCharAt - '0';
            if (i2 < -214748364) {
                throw new NumberFormatException(numberString());
            }
            int i7 = i2 * 10;
            if (i7 < i + i6) {
                throw new NumberFormatException(numberString());
            }
            i2 = i7 - i6;
            i3 = i5;
        }
        if (!z) {
            return -i2;
        }
        if (i3 > this.np + 1) {
            return i2;
        }
        throw new NumberFormatException(numberString());
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        char[] cArr = this.sbuf;
        if (cArr.length <= 8192) {
            SBUF_LOCAL.set(cArr);
        }
        this.sbuf = null;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final boolean isRef() {
        return this.sp == 4 && charAt(this.np + 1) == '$' && charAt(this.np + 2) == 'r' && charAt(this.np + 3) == 'e' && charAt(this.np + 4) == 'f';
    }

    public final int scanType(String str) {
        this.matchStat = 0;
        if (!charArrayCompare(typeFieldName)) {
            return -2;
        }
        int length = this.bp + typeFieldName.length;
        int length2 = str.length();
        for (int i = 0; i < length2; i++) {
            if (str.charAt(i) != charAt(length + i)) {
                return -1;
            }
        }
        int i2 = length + length2;
        if (charAt(i2) != '\"') {
            return -1;
        }
        int i3 = i2 + 1;
        char cCharAt = charAt(i3);
        this.ch = cCharAt;
        if (cCharAt == ',') {
            int i4 = i3 + 1;
            this.ch = charAt(i4);
            this.bp = i4;
            this.token = 16;
            return 3;
        }
        if (cCharAt == '}') {
            i3++;
            char cCharAt2 = charAt(i3);
            this.ch = cCharAt2;
            if (cCharAt2 == ',') {
                this.token = 16;
                i3++;
                this.ch = charAt(i3);
            } else if (cCharAt2 == ']') {
                this.token = 15;
                i3++;
                this.ch = charAt(i3);
            } else if (cCharAt2 == '}') {
                this.token = 13;
                i3++;
                this.ch = charAt(i3);
            } else {
                if (cCharAt2 != 26) {
                    return -1;
                }
                this.token = 20;
            }
            this.matchStat = 4;
        }
        this.bp = i3;
        return this.matchStat;
    }

    public final boolean matchField(char[] cArr) {
        while (!charArrayCompare(cArr)) {
            if (!isWhitespace(this.ch)) {
                return false;
            }
            next();
        }
        int length = this.bp + cArr.length;
        this.bp = length;
        char cCharAt = charAt(length);
        this.ch = cCharAt;
        if (cCharAt == '{') {
            next();
            this.token = 12;
        } else if (cCharAt == '[') {
            next();
            this.token = 14;
        } else if (cCharAt == 'S' && charAt(this.bp + 1) == 'e' && charAt(this.bp + 2) == 't' && charAt(this.bp + 3) == '[') {
            int i = this.bp + 3;
            this.bp = i;
            this.ch = charAt(i);
            this.token = 21;
        } else {
            nextToken();
        }
        return true;
    }

    public int matchField(long j) {
        throw new UnsupportedOperationException();
    }

    public boolean seekArrayToItem(int i) {
        throw new UnsupportedOperationException();
    }

    public int seekObjectToField(long j, boolean z) {
        throw new UnsupportedOperationException();
    }

    public int seekObjectToField(long[] jArr) {
        throw new UnsupportedOperationException();
    }

    public int seekObjectToFieldDeepScan(long j) {
        throw new UnsupportedOperationException();
    }

    public void skipObject() {
        throw new UnsupportedOperationException();
    }

    public void skipObject(boolean z) {
        throw new UnsupportedOperationException();
    }

    public void skipArray() {
        throw new UnsupportedOperationException();
    }

    public String scanFieldString(char[] cArr) {
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return stringDefaultValue();
        }
        int length = cArr.length;
        int i = length + 1;
        if (charAt(this.bp + length) != '\"') {
            this.matchStat = -1;
            return stringDefaultValue();
        }
        int iIndexOf = indexOf('\"', this.bp + cArr.length + 1);
        if (iIndexOf == -1) {
            throw new JSONException("unclosed str");
        }
        int length2 = this.bp + cArr.length + 1;
        String strSubString = subString(length2, iIndexOf - length2);
        if (strSubString.indexOf(92) != -1) {
            while (true) {
                int i2 = 0;
                for (int i3 = iIndexOf - 1; i3 >= 0 && charAt(i3) == '\\'; i3--) {
                    i2++;
                }
                if (i2 % 2 == 0) {
                    break;
                }
                iIndexOf = indexOf('\"', iIndexOf + 1);
            }
            int i4 = this.bp;
            int length3 = iIndexOf - ((cArr.length + i4) + 1);
            strSubString = readString(sub_chars(i4 + cArr.length + 1, length3), length3);
        }
        int i5 = this.bp;
        int length4 = i + (iIndexOf - ((cArr.length + i5) + 1)) + 1;
        int i6 = length4 + 1;
        char cCharAt = charAt(i5 + length4);
        if (cCharAt == ',') {
            int i7 = this.bp + i6;
            this.bp = i7;
            this.ch = charAt(i7);
            this.matchStat = 3;
            return strSubString;
        }
        if (cCharAt == '}') {
            int i8 = i6 + 1;
            char cCharAt2 = charAt(this.bp + i6);
            if (cCharAt2 == ',') {
                this.token = 16;
                int i9 = this.bp + i8;
                this.bp = i9;
                this.ch = charAt(i9);
            } else if (cCharAt2 == ']') {
                this.token = 15;
                int i10 = this.bp + i8;
                this.bp = i10;
                this.ch = charAt(i10);
            } else if (cCharAt2 == '}') {
                this.token = 13;
                int i11 = this.bp + i8;
                this.bp = i11;
                this.ch = charAt(i11);
            } else if (cCharAt2 == 26) {
                this.token = 20;
                this.bp += i8 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return stringDefaultValue();
            }
            this.matchStat = 4;
            return strSubString;
        }
        this.matchStat = -1;
        return stringDefaultValue();
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public String scanString(char c) {
        this.matchStat = 0;
        char cCharAt = charAt(this.bp + 0);
        if (cCharAt == 'n') {
            if (charAt(this.bp + 1) == 'u' && charAt(this.bp + 1 + 1) == 'l' && charAt(this.bp + 1 + 2) == 'l') {
                if (charAt(this.bp + 4) == c) {
                    int i = this.bp + 5;
                    this.bp = i;
                    this.ch = charAt(i);
                    this.matchStat = 3;
                    return null;
                }
                this.matchStat = -1;
                return null;
            }
            this.matchStat = -1;
            return null;
        }
        int i2 = 1;
        while (cCharAt != '\"') {
            if (isWhitespace(cCharAt)) {
                cCharAt = charAt(this.bp + i2);
                i2++;
            } else {
                this.matchStat = -1;
                return stringDefaultValue();
            }
        }
        int i3 = this.bp + i2;
        int iIndexOf = indexOf('\"', i3);
        if (iIndexOf == -1) {
            throw new JSONException("unclosed str");
        }
        String strSubString = subString(this.bp + i2, iIndexOf - i3);
        if (strSubString.indexOf(92) != -1) {
            while (true) {
                int i4 = 0;
                for (int i5 = iIndexOf - 1; i5 >= 0 && charAt(i5) == '\\'; i5--) {
                    i4++;
                }
                if (i4 % 2 == 0) {
                    break;
                }
                iIndexOf = indexOf('\"', iIndexOf + 1);
            }
            int i6 = iIndexOf - i3;
            strSubString = readString(sub_chars(this.bp + 1, i6), i6);
        }
        int i7 = i2 + (iIndexOf - i3) + 1;
        int i8 = i7 + 1;
        char cCharAt2 = charAt(this.bp + i7);
        while (cCharAt2 != c) {
            if (!isWhitespace(cCharAt2)) {
                if (cCharAt2 == ']') {
                    int i9 = this.bp + i8;
                    this.bp = i9;
                    this.ch = charAt(i9);
                    this.matchStat = -1;
                }
                return strSubString;
            }
            cCharAt2 = charAt(this.bp + i8);
            i8++;
        }
        int i10 = this.bp + i8;
        this.bp = i10;
        this.ch = charAt(i10);
        this.matchStat = 3;
        this.token = 16;
        return strSubString;
    }

    public long scanFieldSymbol(char[] cArr) {
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return 0L;
        }
        int length = cArr.length;
        int i = length + 1;
        if (charAt(this.bp + length) != '\"') {
            this.matchStat = -1;
            return 0L;
        }
        long j = TypeUtils.fnv1a_64_magic_hashcode;
        while (true) {
            int i2 = i + 1;
            char cCharAt = charAt(this.bp + i);
            if (cCharAt == '\"') {
                int i3 = i2 + 1;
                char cCharAt2 = charAt(this.bp + i2);
                if (cCharAt2 == ',') {
                    int i4 = this.bp + i3;
                    this.bp = i4;
                    this.ch = charAt(i4);
                    this.matchStat = 3;
                    return j;
                }
                if (cCharAt2 == '}') {
                    int i5 = i3 + 1;
                    char cCharAt3 = charAt(this.bp + i3);
                    if (cCharAt3 == ',') {
                        this.token = 16;
                        int i6 = this.bp + i5;
                        this.bp = i6;
                        this.ch = charAt(i6);
                    } else if (cCharAt3 == ']') {
                        this.token = 15;
                        int i7 = this.bp + i5;
                        this.bp = i7;
                        this.ch = charAt(i7);
                    } else if (cCharAt3 == '}') {
                        this.token = 13;
                        int i8 = this.bp + i5;
                        this.bp = i8;
                        this.ch = charAt(i8);
                    } else if (cCharAt3 == 26) {
                        this.token = 20;
                        this.bp += i5 - 1;
                        this.ch = JSONLexer.EOI;
                    } else {
                        this.matchStat = -1;
                        return 0L;
                    }
                    this.matchStat = 4;
                    return j;
                }
                this.matchStat = -1;
                return 0L;
            }
            j = (j ^ ((long) cCharAt)) * TypeUtils.fnv1a_64_magic_prime;
            if (cCharAt == '\\') {
                this.matchStat = -1;
                return 0L;
            }
            i = i2;
        }
    }

    public long scanEnumSymbol(char[] cArr) {
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return 0L;
        }
        int length = cArr.length;
        int i = length + 1;
        if (charAt(this.bp + length) != '\"') {
            this.matchStat = -1;
            return 0L;
        }
        long j = TypeUtils.fnv1a_64_magic_hashcode;
        while (true) {
            int i2 = i + 1;
            char cCharAt = charAt(this.bp + i);
            if (cCharAt == '\"') {
                int i3 = i2 + 1;
                char cCharAt2 = charAt(this.bp + i2);
                if (cCharAt2 == ',') {
                    int i4 = this.bp + i3;
                    this.bp = i4;
                    this.ch = charAt(i4);
                    this.matchStat = 3;
                    return j;
                }
                if (cCharAt2 == '}') {
                    int i5 = i3 + 1;
                    char cCharAt3 = charAt(this.bp + i3);
                    if (cCharAt3 == ',') {
                        this.token = 16;
                        int i6 = this.bp + i5;
                        this.bp = i6;
                        this.ch = charAt(i6);
                    } else if (cCharAt3 == ']') {
                        this.token = 15;
                        int i7 = this.bp + i5;
                        this.bp = i7;
                        this.ch = charAt(i7);
                    } else if (cCharAt3 == '}') {
                        this.token = 13;
                        int i8 = this.bp + i5;
                        this.bp = i8;
                        this.ch = charAt(i8);
                    } else if (cCharAt3 == 26) {
                        this.token = 20;
                        this.bp += i5 - 1;
                        this.ch = JSONLexer.EOI;
                    } else {
                        this.matchStat = -1;
                        return 0L;
                    }
                    this.matchStat = 4;
                    return j;
                }
                this.matchStat = -1;
                return 0L;
            }
            j = (j ^ ((long) ((cCharAt < 'A' || cCharAt > 'Z') ? cCharAt : cCharAt + ' '))) * TypeUtils.fnv1a_64_magic_prime;
            if (cCharAt == '\\') {
                this.matchStat = -1;
                return 0L;
            }
            i = i2;
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public Enum<?> scanEnum(Class<?> cls, SymbolTable symbolTable, char c) {
        String strScanSymbolWithSeperator = scanSymbolWithSeperator(symbolTable, c);
        if (strScanSymbolWithSeperator == null) {
            return null;
        }
        return Enum.valueOf(cls, strScanSymbolWithSeperator);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public String scanSymbolWithSeperator(SymbolTable symbolTable, char c) {
        int i = 0;
        this.matchStat = 0;
        char cCharAt = charAt(this.bp + 0);
        if (cCharAt == 'n') {
            if (charAt(this.bp + 1) == 'u' && charAt(this.bp + 1 + 1) == 'l' && charAt(this.bp + 1 + 2) == 'l') {
                if (charAt(this.bp + 4) == c) {
                    int i2 = this.bp + 5;
                    this.bp = i2;
                    this.ch = charAt(i2);
                    this.matchStat = 3;
                    return null;
                }
                this.matchStat = -1;
                return null;
            }
            this.matchStat = -1;
            return null;
        }
        if (cCharAt != '\"') {
            this.matchStat = -1;
            return null;
        }
        int i3 = 1;
        while (true) {
            int i4 = i3 + 1;
            char cCharAt2 = charAt(this.bp + i3);
            if (cCharAt2 == '\"') {
                int i5 = this.bp;
                int i6 = i5 + 0 + 1;
                String strAddSymbol = addSymbol(i6, ((i5 + i4) - i6) - 1, i, symbolTable);
                int i7 = i4 + 1;
                char cCharAt3 = charAt(this.bp + i4);
                while (cCharAt3 != c) {
                    if (isWhitespace(cCharAt3)) {
                        cCharAt3 = charAt(this.bp + i7);
                        i7++;
                    } else {
                        this.matchStat = -1;
                        return strAddSymbol;
                    }
                }
                int i8 = this.bp + i7;
                this.bp = i8;
                this.ch = charAt(i8);
                this.matchStat = 3;
                return strAddSymbol;
            }
            i = (i * 31) + cCharAt2;
            if (cCharAt2 == '\\') {
                this.matchStat = -1;
                return null;
            }
            i3 = i4;
        }
    }

    public Collection<String> newCollectionByType(Class<?> cls) {
        if (cls.isAssignableFrom(HashSet.class)) {
            return new HashSet();
        }
        if (cls.isAssignableFrom(ArrayList.class)) {
            return new ArrayList();
        }
        if (cls.isAssignableFrom(LinkedList.class)) {
            return new LinkedList();
        }
        try {
            return (Collection) cls.newInstance();
        } catch (Exception e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    public Collection<String> scanFieldStringArray(char[] cArr, Class<?> cls) {
        char cCharAt;
        int i;
        char cCharAt2;
        int i2;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        Collection<String> collectionNewCollectionByType = newCollectionByType(cls);
        int length = cArr.length;
        int i3 = length + 1;
        if (charAt(this.bp + length) != '[') {
            this.matchStat = -1;
            return null;
        }
        int i4 = i3 + 1;
        char cCharAt3 = charAt(this.bp + i3);
        while (true) {
            if (cCharAt3 == '\"') {
                int iIndexOf = indexOf('\"', this.bp + i4);
                if (iIndexOf == -1) {
                    throw new JSONException("unclosed str");
                }
                int i5 = this.bp + i4;
                String strSubString = subString(i5, iIndexOf - i5);
                if (strSubString.indexOf(92) != -1) {
                    while (true) {
                        int i6 = 0;
                        for (int i7 = iIndexOf - 1; i7 >= 0 && charAt(i7) == '\\'; i7--) {
                            i6++;
                        }
                        if (i6 % 2 == 0) {
                            break;
                        }
                        iIndexOf = indexOf('\"', iIndexOf + 1);
                    }
                    int i8 = this.bp;
                    int i9 = iIndexOf - (i8 + i4);
                    strSubString = readString(sub_chars(i8 + i4, i9), i9);
                }
                int i10 = this.bp;
                int i11 = i4 + (iIndexOf - (i10 + i4)) + 1;
                i2 = i11 + 1;
                cCharAt2 = charAt(i10 + i11);
                collectionNewCollectionByType.add(strSubString);
            } else if (cCharAt3 == 'n' && charAt(this.bp + i4) == 'u' && charAt(this.bp + i4 + 1) == 'l' && charAt(this.bp + i4 + 2) == 'l') {
                int i12 = i4 + 3;
                int i13 = i12 + 1;
                cCharAt2 = charAt(this.bp + i12);
                collectionNewCollectionByType.add(null);
                i2 = i13;
            } else {
                if (cCharAt3 == ']' && collectionNewCollectionByType.size() == 0) {
                    cCharAt = charAt(this.bp + i4);
                    i = i4 + 1;
                    break;
                }
                throw new JSONException("illega str");
            }
            if (cCharAt2 != ',') {
                if (cCharAt2 == ']') {
                    i = i2 + 1;
                    cCharAt = charAt(this.bp + i2);
                    break;
                }
                this.matchStat = -1;
                return null;
            }
            int i14 = i2 + 1;
            cCharAt3 = charAt(this.bp + i2);
            i4 = i14;
        }
        if (cCharAt == ',') {
            int i15 = this.bp + i;
            this.bp = i15;
            this.ch = charAt(i15);
            this.matchStat = 3;
            return collectionNewCollectionByType;
        }
        if (cCharAt == '}') {
            int i16 = i + 1;
            char cCharAt4 = charAt(this.bp + i);
            if (cCharAt4 == ',') {
                this.token = 16;
                int i17 = this.bp + i16;
                this.bp = i17;
                this.ch = charAt(i17);
            } else if (cCharAt4 == ']') {
                this.token = 15;
                int i18 = this.bp + i16;
                this.bp = i18;
                this.ch = charAt(i18);
            } else if (cCharAt4 == '}') {
                this.token = 13;
                int i19 = this.bp + i16;
                this.bp = i19;
                this.ch = charAt(i19);
            } else if (cCharAt4 == 26) {
                this.bp += i16 - 1;
                this.token = 20;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return collectionNewCollectionByType;
        }
        this.matchStat = -1;
        return null;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public void scanStringArray(Collection<String> collection, char c) {
        int i;
        char cCharAt;
        int i2;
        char cCharAt2;
        this.matchStat = 0;
        char cCharAt3 = charAt(this.bp + 0);
        char c2 = 'u';
        char c3 = 'n';
        if (cCharAt3 == 'n' && charAt(this.bp + 1) == 'u' && charAt(this.bp + 1 + 1) == 'l' && charAt(this.bp + 1 + 2) == 'l' && charAt(this.bp + 1 + 3) == c) {
            int i3 = this.bp + 5;
            this.bp = i3;
            this.ch = charAt(i3);
            this.matchStat = 5;
            return;
        }
        if (cCharAt3 != '[') {
            this.matchStat = -1;
            return;
        }
        char cCharAt4 = charAt(this.bp + 1);
        int i4 = 2;
        while (true) {
            if (cCharAt4 == c3 && charAt(this.bp + i4) == c2 && charAt(this.bp + i4 + 1) == 'l' && charAt(this.bp + i4 + 2) == 'l') {
                int i5 = i4 + 3;
                i = i5 + 1;
                cCharAt = charAt(this.bp + i5);
                collection.add(null);
            } else {
                if (cCharAt4 == ']' && collection.size() == 0) {
                    i2 = i4 + 1;
                    cCharAt2 = charAt(this.bp + i4);
                    break;
                }
                if (cCharAt4 != '\"') {
                    this.matchStat = -1;
                    return;
                }
                int i6 = this.bp + i4;
                int iIndexOf = indexOf('\"', i6);
                if (iIndexOf == -1) {
                    throw new JSONException("unclosed str");
                }
                String strSubString = subString(this.bp + i4, iIndexOf - i6);
                if (strSubString.indexOf(92) != -1) {
                    while (true) {
                        int i7 = 0;
                        for (int i8 = iIndexOf - 1; i8 >= 0 && charAt(i8) == '\\'; i8--) {
                            i7++;
                        }
                        if (i7 % 2 == 0) {
                            break;
                        } else {
                            iIndexOf = indexOf('\"', iIndexOf + 1);
                        }
                    }
                    int i9 = iIndexOf - i6;
                    strSubString = readString(sub_chars(this.bp + i4, i9), i9);
                }
                int i10 = this.bp;
                int i11 = i4 + (iIndexOf - (i10 + i4)) + 1;
                i = i11 + 1;
                cCharAt = charAt(i10 + i11);
                collection.add(strSubString);
            }
            if (cCharAt != ',') {
                if (cCharAt == ']') {
                    i2 = i + 1;
                    cCharAt2 = charAt(this.bp + i);
                    break;
                } else {
                    this.matchStat = -1;
                    return;
                }
            }
            i4 = i + 1;
            cCharAt4 = charAt(this.bp + i);
            c2 = 'u';
            c3 = 'n';
        }
        if (cCharAt2 == c) {
            int i12 = this.bp + i2;
            this.bp = i12;
            this.ch = charAt(i12);
            this.matchStat = 3;
            return;
        }
        this.matchStat = -1;
    }

    public int scanFieldInt(char[] cArr) {
        int i;
        char cCharAt;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return 0;
        }
        int length = cArr.length;
        int i2 = length + 1;
        char cCharAt2 = charAt(this.bp + length);
        boolean z = cCharAt2 == '-';
        if (z) {
            cCharAt2 = charAt(this.bp + i2);
            i2++;
        }
        if (cCharAt2 < '0' || cCharAt2 > '9') {
            this.matchStat = -1;
            return 0;
        }
        int i3 = cCharAt2 - '0';
        while (true) {
            i = i2 + 1;
            cCharAt = charAt(this.bp + i2);
            if (cCharAt < '0' || cCharAt > '9') {
                break;
            }
            i3 = (i3 * 10) + (cCharAt - '0');
            i2 = i;
        }
        if (cCharAt == '.') {
            this.matchStat = -1;
            return 0;
        }
        if ((i3 < 0 || i > cArr.length + 14) && !(i3 == Integer.MIN_VALUE && i == 17 && z)) {
            this.matchStat = -1;
            return 0;
        }
        if (cCharAt == ',') {
            int i4 = this.bp + i;
            this.bp = i4;
            this.ch = charAt(i4);
            this.matchStat = 3;
            this.token = 16;
            return z ? -i3 : i3;
        }
        if (cCharAt == '}') {
            int i5 = i + 1;
            char cCharAt3 = charAt(this.bp + i);
            if (cCharAt3 == ',') {
                this.token = 16;
                int i6 = this.bp + i5;
                this.bp = i6;
                this.ch = charAt(i6);
            } else if (cCharAt3 == ']') {
                this.token = 15;
                int i7 = this.bp + i5;
                this.bp = i7;
                this.ch = charAt(i7);
            } else if (cCharAt3 == '}') {
                this.token = 13;
                int i8 = this.bp + i5;
                this.bp = i8;
                this.ch = charAt(i8);
            } else if (cCharAt3 == 26) {
                this.token = 20;
                this.bp += i5 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return 0;
            }
            this.matchStat = 4;
            return z ? -i3 : i3;
        }
        this.matchStat = -1;
        return 0;
    }

    public final int[] scanFieldIntArray(char[] cArr) {
        boolean z;
        int i;
        char cCharAt;
        int i2;
        int i3;
        char cCharAt2;
        this.matchStat = 0;
        int[] iArr = null;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        int length = cArr.length;
        int i4 = length + 1;
        if (charAt(this.bp + length) != '[') {
            this.matchStat = -2;
            return null;
        }
        int i5 = i4 + 1;
        char cCharAt3 = charAt(this.bp + i4);
        int[] iArr2 = new int[16];
        if (cCharAt3 == ']') {
            i3 = i5 + 1;
            cCharAt2 = charAt(this.bp + i5);
            i2 = 0;
        } else {
            int i6 = 0;
            while (true) {
                if (cCharAt3 == '-') {
                    cCharAt3 = charAt(this.bp + i5);
                    i5++;
                    z = true;
                } else {
                    z = false;
                }
                if (cCharAt3 >= '0' && cCharAt3 <= '9') {
                    int i7 = cCharAt3 - '0';
                    while (true) {
                        i = i5 + 1;
                        cCharAt = charAt(this.bp + i5);
                        if (cCharAt < '0' || cCharAt > '9') {
                            break;
                        }
                        i7 = (i7 * 10) + (cCharAt - '0');
                        i5 = i;
                    }
                    if (i6 >= iArr2.length) {
                        int[] iArr3 = new int[(iArr2.length * 3) / 2];
                        System.arraycopy(iArr2, 0, iArr3, 0, i6);
                        iArr2 = iArr3;
                    }
                    i2 = i6 + 1;
                    if (z) {
                        i7 = -i7;
                    }
                    iArr2[i6] = i7;
                    if (cCharAt == ',') {
                        char cCharAt4 = charAt(this.bp + i);
                        i++;
                        cCharAt = cCharAt4;
                    } else if (cCharAt == ']') {
                        i3 = i + 1;
                        cCharAt2 = charAt(this.bp + i);
                        break;
                    }
                    i6 = i2;
                    iArr = null;
                    cCharAt3 = cCharAt;
                    i5 = i;
                } else {
                    int[] iArr4 = iArr;
                    this.matchStat = -1;
                    return iArr4;
                }
            }
        }
        if (i2 != iArr2.length) {
            int[] iArr5 = new int[i2];
            System.arraycopy(iArr2, 0, iArr5, 0, i2);
            iArr2 = iArr5;
        }
        if (cCharAt2 == ',') {
            this.bp += i3 - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return iArr2;
        }
        if (cCharAt2 == '}') {
            int i8 = i3 + 1;
            char cCharAt5 = charAt(this.bp + i3);
            if (cCharAt5 == ',') {
                this.token = 16;
                this.bp += i8 - 1;
                next();
            } else if (cCharAt5 == ']') {
                this.token = 15;
                this.bp += i8 - 1;
                next();
            } else if (cCharAt5 == '}') {
                this.token = 13;
                this.bp += i8 - 1;
                next();
            } else if (cCharAt5 == 26) {
                this.bp += i8 - 1;
                this.token = 20;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return iArr2;
        }
        this.matchStat = -1;
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:37:0x00ab  */
    /* JADX WARN: Code duplicated, block: B:39:0x00b1  */
    /* JADX WARN: Code duplicated, block: B:45:0x00bc A[SYNTHETIC] */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public boolean scanBoolean(char c) {
        boolean z = false;
        this.matchStat = 0;
        char cCharAt = charAt(this.bp + 0);
        int i = 5;
        if (cCharAt == 't') {
            if (charAt(this.bp + 1) == 'r' && charAt(this.bp + 1 + 1) == 'u' && charAt(this.bp + 1 + 2) == 'e') {
                cCharAt = charAt(this.bp + 4);
            } else {
                this.matchStat = -1;
                return false;
            }
        } else {
            if (cCharAt == 'f') {
                if (charAt(this.bp + 1) == 'a' && charAt(this.bp + 1 + 1) == 'l' && charAt(this.bp + 1 + 2) == 's' && charAt(this.bp + 1 + 3) == 'e') {
                    cCharAt = charAt(this.bp + 5);
                    i = 6;
                } else {
                    this.matchStat = -1;
                    return false;
                }
            } else if (cCharAt == '1') {
                cCharAt = charAt(this.bp + 1);
                i = 2;
            } else if (cCharAt == '0') {
                cCharAt = charAt(this.bp + 1);
                i = 2;
            } else {
                i = 1;
            }
            while (cCharAt != c) {
                if (isWhitespace(cCharAt)) {
                    int i2 = i + 1;
                    cCharAt = charAt(this.bp + i);
                    i = i2;
                } else {
                    this.matchStat = -1;
                    return z;
                }
            }
            int i3 = this.bp + i;
            this.bp = i3;
            this.ch = charAt(i3);
            this.matchStat = 3;
            return z;
        }
        z = true;
        while (cCharAt != c) {
            if (isWhitespace(cCharAt)) {
                int i4 = i + 1;
                cCharAt = charAt(this.bp + i);
                i = i4;
            } else {
                this.matchStat = -1;
                return z;
            }
        }
        int i5 = this.bp + i;
        this.bp = i5;
        this.ch = charAt(i5);
        this.matchStat = 3;
        return z;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public int scanInt(char c) {
        int i;
        int i2;
        char cCharAt;
        this.matchStat = 0;
        char cCharAt2 = charAt(this.bp + 0);
        boolean z = cCharAt2 == '\"';
        if (z) {
            cCharAt2 = charAt(this.bp + 1);
            i = 2;
        } else {
            i = 1;
        }
        boolean z2 = cCharAt2 == '-';
        if (z2) {
            cCharAt2 = charAt(this.bp + i);
            i++;
        }
        if (cCharAt2 >= '0' && cCharAt2 <= '9') {
            int i3 = cCharAt2 - '0';
            while (true) {
                i2 = i + 1;
                cCharAt = charAt(this.bp + i);
                if (cCharAt < '0' || cCharAt > '9') {
                    break;
                }
                i3 = (i3 * 10) + (cCharAt - '0');
                i = i2;
            }
            if (cCharAt == '.') {
                this.matchStat = -1;
                return 0;
            }
            if (i3 < 0) {
                this.matchStat = -1;
                return 0;
            }
            while (cCharAt != c) {
                if (isWhitespace(cCharAt)) {
                    char cCharAt3 = charAt(this.bp + i2);
                    i2++;
                    cCharAt = cCharAt3;
                } else {
                    this.matchStat = -1;
                    return z2 ? -i3 : i3;
                }
            }
            int i4 = this.bp + i2;
            this.bp = i4;
            this.ch = charAt(i4);
            this.matchStat = 3;
            this.token = 16;
            return z2 ? -i3 : i3;
        }
        if (cCharAt2 == 'n' && charAt(this.bp + i) == 'u' && charAt(this.bp + i + 1) == 'l' && charAt(this.bp + i + 2) == 'l') {
            this.matchStat = 5;
            int i5 = i + 3;
            int i6 = i5 + 1;
            char cCharAt4 = charAt(this.bp + i5);
            if (z && cCharAt4 == '\"') {
                int i7 = i6 + 1;
                cCharAt4 = charAt(this.bp + i6);
                i6 = i7;
            }
            while (cCharAt4 != ',') {
                if (cCharAt4 == ']') {
                    int i8 = this.bp + i6;
                    this.bp = i8;
                    this.ch = charAt(i8);
                    this.matchStat = 5;
                    this.token = 15;
                    return 0;
                }
                if (isWhitespace(cCharAt4)) {
                    int i9 = i6 + 1;
                    cCharAt4 = charAt(this.bp + i6);
                    i6 = i9;
                } else {
                    this.matchStat = -1;
                    return 0;
                }
            }
            int i10 = this.bp + i6;
            this.bp = i10;
            this.ch = charAt(i10);
            this.matchStat = 5;
            this.token = 16;
            return 0;
        }
        this.matchStat = -1;
        return 0;
    }

    public boolean scanFieldBoolean(char[] cArr) {
        boolean z;
        int i;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return false;
        }
        int length = cArr.length;
        int i2 = length + 1;
        char cCharAt = charAt(this.bp + length);
        if (cCharAt == 't') {
            int i3 = i2 + 1;
            if (charAt(this.bp + i2) != 'r') {
                this.matchStat = -1;
                return false;
            }
            int i4 = i3 + 1;
            if (charAt(this.bp + i3) != 'u') {
                this.matchStat = -1;
                return false;
            }
            i = i4 + 1;
            if (charAt(this.bp + i4) != 'e') {
                this.matchStat = -1;
                return false;
            }
            z = true;
        } else if (cCharAt == 'f') {
            int i5 = i2 + 1;
            if (charAt(this.bp + i2) != 'a') {
                this.matchStat = -1;
                return false;
            }
            int i6 = i5 + 1;
            if (charAt(this.bp + i5) != 'l') {
                this.matchStat = -1;
                return false;
            }
            int i7 = i6 + 1;
            if (charAt(this.bp + i6) != 's') {
                this.matchStat = -1;
                return false;
            }
            int i8 = i7 + 1;
            if (charAt(this.bp + i7) != 'e') {
                this.matchStat = -1;
                return false;
            }
            z = false;
            i = i8;
        } else {
            this.matchStat = -1;
            return false;
        }
        int i9 = i + 1;
        char cCharAt2 = charAt(this.bp + i);
        if (cCharAt2 == ',') {
            int i10 = this.bp + i9;
            this.bp = i10;
            this.ch = charAt(i10);
            this.matchStat = 3;
            this.token = 16;
            return z;
        }
        if (cCharAt2 == '}') {
            int i11 = i9 + 1;
            char cCharAt3 = charAt(this.bp + i9);
            if (cCharAt3 == ',') {
                this.token = 16;
                int i12 = this.bp + i11;
                this.bp = i12;
                this.ch = charAt(i12);
            } else if (cCharAt3 == ']') {
                this.token = 15;
                int i13 = this.bp + i11;
                this.bp = i13;
                this.ch = charAt(i13);
            } else if (cCharAt3 == '}') {
                this.token = 13;
                int i14 = this.bp + i11;
                this.bp = i14;
                this.ch = charAt(i14);
            } else if (cCharAt3 == 26) {
                this.token = 20;
                this.bp += i11 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return false;
            }
            this.matchStat = 4;
            return z;
        }
        this.matchStat = -1;
        return false;
    }

    public long scanFieldLong(char[] cArr) {
        boolean z;
        int i;
        char cCharAt;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return 0L;
        }
        int length = cArr.length;
        int i2 = length + 1;
        char cCharAt2 = charAt(this.bp + length);
        if (cCharAt2 == '-') {
            cCharAt2 = charAt(this.bp + i2);
            i2++;
            z = true;
        } else {
            z = false;
        }
        if (cCharAt2 < '0' || cCharAt2 > '9') {
            this.matchStat = -1;
            return 0L;
        }
        long j = cCharAt2 - '0';
        while (true) {
            i = i2 + 1;
            cCharAt = charAt(this.bp + i2);
            if (cCharAt < '0' || cCharAt > '9') {
                break;
            }
            j = (j * 10) + ((long) (cCharAt - '0'));
            i2 = i;
        }
        if (cCharAt == '.') {
            this.matchStat = -1;
            return 0L;
        }
        if (!(i - cArr.length < 21 && (j >= 0 || (j == Long.MIN_VALUE && z)))) {
            this.matchStat = -1;
            return 0L;
        }
        if (cCharAt == ',') {
            int i3 = this.bp + i;
            this.bp = i3;
            this.ch = charAt(i3);
            this.matchStat = 3;
            this.token = 16;
            return z ? -j : j;
        }
        if (cCharAt == '}') {
            int i4 = i + 1;
            char cCharAt3 = charAt(this.bp + i);
            if (cCharAt3 == ',') {
                this.token = 16;
                int i5 = this.bp + i4;
                this.bp = i5;
                this.ch = charAt(i5);
            } else if (cCharAt3 == ']') {
                this.token = 15;
                int i6 = this.bp + i4;
                this.bp = i6;
                this.ch = charAt(i6);
            } else if (cCharAt3 == '}') {
                this.token = 13;
                int i7 = this.bp + i4;
                this.bp = i7;
                this.ch = charAt(i7);
            } else if (cCharAt3 == 26) {
                this.token = 20;
                this.bp += i4 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return 0L;
            }
            this.matchStat = 4;
            return z ? -j : j;
        }
        this.matchStat = -1;
        return 0L;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public long scanLong(char c) {
        int i;
        int i2;
        char cCharAt;
        this.matchStat = 0;
        char cCharAt2 = charAt(this.bp + 0);
        boolean z = cCharAt2 == '\"';
        if (z) {
            cCharAt2 = charAt(this.bp + 1);
            i = 2;
        } else {
            i = 1;
        }
        boolean z2 = cCharAt2 == '-';
        if (z2) {
            cCharAt2 = charAt(this.bp + i);
            i++;
        }
        if (cCharAt2 >= '0' && cCharAt2 <= '9') {
            long j = cCharAt2 - '0';
            while (true) {
                i2 = i + 1;
                cCharAt = charAt(this.bp + i);
                if (cCharAt < '0' || cCharAt > '9') {
                    break;
                }
                j = (j * 10) + ((long) (cCharAt - '0'));
                i = i2;
            }
            if (cCharAt == '.') {
                this.matchStat = -1;
                return 0L;
            }
            if (!(j >= 0 || (j == Long.MIN_VALUE && z2))) {
                throw new NumberFormatException(subString(this.bp, i2 - 1));
            }
            if (z) {
                if (cCharAt != '\"') {
                    this.matchStat = -1;
                    return 0L;
                }
                cCharAt = charAt(this.bp + i2);
                i2++;
            }
            while (cCharAt != c) {
                if (isWhitespace(cCharAt)) {
                    cCharAt = charAt(this.bp + i2);
                    i2++;
                } else {
                    this.matchStat = -1;
                    return j;
                }
            }
            int i3 = this.bp + i2;
            this.bp = i3;
            this.ch = charAt(i3);
            this.matchStat = 3;
            this.token = 16;
            return z2 ? -j : j;
        }
        if (cCharAt2 == 'n' && charAt(this.bp + i) == 'u' && charAt(this.bp + i + 1) == 'l' && charAt(this.bp + i + 2) == 'l') {
            this.matchStat = 5;
            int i4 = i + 3;
            int i5 = i4 + 1;
            char cCharAt3 = charAt(this.bp + i4);
            if (z && cCharAt3 == '\"') {
                int i6 = i5 + 1;
                cCharAt3 = charAt(this.bp + i5);
                i5 = i6;
            }
            while (cCharAt3 != ',') {
                if (cCharAt3 == ']') {
                    int i7 = this.bp + i5;
                    this.bp = i7;
                    this.ch = charAt(i7);
                    this.matchStat = 5;
                    this.token = 15;
                    return 0L;
                }
                if (isWhitespace(cCharAt3)) {
                    int i8 = i5 + 1;
                    cCharAt3 = charAt(this.bp + i5);
                    i5 = i8;
                } else {
                    this.matchStat = -1;
                    return 0L;
                }
            }
            int i9 = this.bp + i5;
            this.bp = i9;
            this.ch = charAt(i9);
            this.matchStat = 5;
            this.token = 16;
            return 0L;
        }
        this.matchStat = -1;
        return 0L;
    }

    public final float scanFieldFloat(char[] cArr) {
        int i;
        char cCharAt;
        boolean z;
        long j;
        int length;
        int i2;
        float f;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return 0.0f;
        }
        int length2 = cArr.length;
        int i3 = length2 + 1;
        char cCharAt2 = charAt(this.bp + length2);
        boolean z2 = cCharAt2 == '\"';
        if (z2) {
            cCharAt2 = charAt(this.bp + i3);
            i3++;
        }
        boolean z3 = cCharAt2 == '-';
        if (z3) {
            cCharAt2 = charAt(this.bp + i3);
            i3++;
        }
        if (cCharAt2 >= '0') {
            char c = '9';
            if (cCharAt2 <= '9') {
                long j2 = cCharAt2 - '0';
                while (true) {
                    i = i3 + 1;
                    cCharAt = charAt(this.bp + i3);
                    if (cCharAt < '0' || cCharAt > '9') {
                        break;
                    }
                    j2 = (j2 * 10) + ((long) (cCharAt - '0'));
                    i3 = i;
                }
                if (cCharAt == '.') {
                    int i4 = i + 1;
                    char cCharAt3 = charAt(this.bp + i);
                    if (cCharAt3 < '0' || cCharAt3 > '9') {
                        this.matchStat = -1;
                        return 0.0f;
                    }
                    z = z2;
                    j2 = (j2 * 10) + ((long) (cCharAt3 - '0'));
                    j = 10;
                    while (true) {
                        i = i4 + 1;
                        cCharAt = charAt(this.bp + i4);
                        if (cCharAt < '0' || cCharAt > c) {
                            break;
                        }
                        j2 = (j2 * 10) + ((long) (cCharAt - '0'));
                        j *= 10;
                        i4 = i;
                        c = '9';
                    }
                } else {
                    z = z2;
                    j = 1;
                }
                boolean z4 = cCharAt == 'e' || cCharAt == 'E';
                if (z4) {
                    int i5 = i + 1;
                    cCharAt = charAt(this.bp + i);
                    if (cCharAt == '+' || cCharAt == '-') {
                        int i6 = i5 + 1;
                        cCharAt = charAt(this.bp + i5);
                        i = i6;
                    } else {
                        i = i5;
                    }
                    while (cCharAt >= '0' && cCharAt <= '9') {
                        int i7 = i + 1;
                        cCharAt = charAt(this.bp + i);
                        i = i7;
                    }
                }
                if (!z) {
                    int i8 = this.bp;
                    length = cArr.length + i8;
                    i2 = ((i8 + i) - length) - 1;
                } else {
                    if (cCharAt != '\"') {
                        this.matchStat = -1;
                        return 0.0f;
                    }
                    int i9 = i + 1;
                    cCharAt = charAt(this.bp + i);
                    int i10 = this.bp;
                    length = cArr.length + i10 + 1;
                    i2 = ((i10 + i9) - length) - 2;
                    i = i9;
                }
                if (z4 || i2 >= 17) {
                    f = Float.parseFloat(subString(length, i2));
                } else {
                    f = (float) (j2 / j);
                    if (z3) {
                        f = -f;
                    }
                }
                if (cCharAt == ',') {
                    int i11 = this.bp + i;
                    this.bp = i11;
                    this.ch = charAt(i11);
                    this.matchStat = 3;
                    this.token = 16;
                    return f;
                }
                if (cCharAt == '}') {
                    int i12 = i + 1;
                    char cCharAt4 = charAt(this.bp + i);
                    if (cCharAt4 == ',') {
                        this.token = 16;
                        int i13 = this.bp + i12;
                        this.bp = i13;
                        this.ch = charAt(i13);
                    } else if (cCharAt4 == ']') {
                        this.token = 15;
                        int i14 = this.bp + i12;
                        this.bp = i14;
                        this.ch = charAt(i14);
                    } else if (cCharAt4 == '}') {
                        this.token = 13;
                        int i15 = this.bp + i12;
                        this.bp = i15;
                        this.ch = charAt(i15);
                    } else if (cCharAt4 == 26) {
                        this.bp += i12 - 1;
                        this.token = 20;
                        this.ch = JSONLexer.EOI;
                    } else {
                        this.matchStat = -1;
                        return 0.0f;
                    }
                    this.matchStat = 4;
                    return f;
                }
                this.matchStat = -1;
                return 0.0f;
            }
        }
        boolean z5 = z2;
        if (cCharAt2 == 'n' && charAt(this.bp + i3) == 'u' && charAt(this.bp + i3 + 1) == 'l' && charAt(this.bp + i3 + 2) == 'l') {
            this.matchStat = 5;
            int i16 = i3 + 3;
            int i17 = i16 + 1;
            char cCharAt5 = charAt(this.bp + i16);
            if (z5 && cCharAt5 == '\"') {
                cCharAt5 = charAt(this.bp + i17);
                i17++;
            }
            while (cCharAt5 != ',') {
                if (cCharAt5 == '}') {
                    int i18 = this.bp + i17;
                    this.bp = i18;
                    this.ch = charAt(i18);
                    this.matchStat = 5;
                    this.token = 13;
                    return 0.0f;
                }
                if (isWhitespace(cCharAt5)) {
                    cCharAt5 = charAt(this.bp + i17);
                    i17++;
                } else {
                    this.matchStat = -1;
                    return 0.0f;
                }
            }
            int i19 = this.bp + i17;
            this.bp = i19;
            this.ch = charAt(i19);
            this.matchStat = 5;
            this.token = 16;
            return 0.0f;
        }
        this.matchStat = -1;
        return 0.0f;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final float scanFloat(char c) {
        int i;
        int i2;
        char cCharAt;
        int i3;
        int i4;
        float f;
        this.matchStat = 0;
        char cCharAt2 = charAt(this.bp + 0);
        boolean z = cCharAt2 == '\"';
        if (z) {
            cCharAt2 = charAt(this.bp + 1);
            i = 2;
        } else {
            i = 1;
        }
        boolean z2 = cCharAt2 == '-';
        if (z2) {
            cCharAt2 = charAt(this.bp + i);
            i++;
        }
        if (cCharAt2 < '0' || cCharAt2 > '9') {
            if (cCharAt2 == 'n' && charAt(this.bp + i) == 'u' && charAt(this.bp + i + 1) == 'l' && charAt(this.bp + i + 2) == 'l') {
                this.matchStat = 5;
                int i5 = i + 3;
                int i6 = i5 + 1;
                char cCharAt3 = charAt(this.bp + i5);
                if (z && cCharAt3 == '\"') {
                    cCharAt3 = charAt(this.bp + i6);
                    i6++;
                }
                while (cCharAt3 != ',') {
                    if (cCharAt3 == ']') {
                        int i7 = this.bp + i6;
                        this.bp = i7;
                        this.ch = charAt(i7);
                        this.matchStat = 5;
                        this.token = 15;
                        return 0.0f;
                    }
                    if (isWhitespace(cCharAt3)) {
                        cCharAt3 = charAt(this.bp + i6);
                        i6++;
                    } else {
                        this.matchStat = -1;
                        return 0.0f;
                    }
                }
                int i8 = this.bp + i6;
                this.bp = i8;
                this.ch = charAt(i8);
                this.matchStat = 5;
                this.token = 16;
                return 0.0f;
            }
            this.matchStat = -1;
            return 0.0f;
        }
        long j = cCharAt2 - '0';
        while (true) {
            i2 = i + 1;
            cCharAt = charAt(this.bp + i);
            if (cCharAt < '0' || cCharAt > '9') {
                break;
            }
            j = (j * 10) + ((long) (cCharAt - '0'));
            i = i2;
        }
        long j2 = 1;
        if (cCharAt == '.') {
            int i9 = i2 + 1;
            char cCharAt4 = charAt(this.bp + i2);
            if (cCharAt4 < '0' || cCharAt4 > '9') {
                this.matchStat = -1;
                return 0.0f;
            }
            j = (j * 10) + ((long) (cCharAt4 - '0'));
            j2 = 10;
            while (true) {
                i2 = i9 + 1;
                cCharAt = charAt(this.bp + i9);
                if (cCharAt < '0' || cCharAt > '9') {
                    break;
                }
                j = (j * 10) + ((long) (cCharAt - '0'));
                j2 *= 10;
                i9 = i2;
            }
        }
        long j3 = j2;
        boolean z3 = cCharAt == 'e' || cCharAt == 'E';
        if (z3) {
            int i10 = i2 + 1;
            char cCharAt5 = charAt(this.bp + i2);
            if (cCharAt5 == '+' || cCharAt5 == '-') {
                int i11 = i10 + 1;
                cCharAt = charAt(this.bp + i10);
                i2 = i11;
            } else {
                i2 = i10;
                cCharAt = cCharAt5;
            }
            while (cCharAt >= '0' && cCharAt <= '9') {
                int i12 = i2 + 1;
                cCharAt = charAt(this.bp + i2);
                i2 = i12;
            }
        }
        if (!z) {
            i3 = this.bp;
            i4 = ((i3 + i2) - i3) - 1;
        } else {
            if (cCharAt != '\"') {
                this.matchStat = -1;
                return 0.0f;
            }
            int i13 = i2 + 1;
            cCharAt = charAt(this.bp + i2);
            int i14 = this.bp;
            i3 = i14 + 1;
            i4 = ((i14 + i13) - i3) - 2;
            i2 = i13;
        }
        if (z3 || i4 >= 17) {
            f = Float.parseFloat(subString(i3, i4));
        } else {
            f = (float) (j / j3);
            if (z2) {
                f = -f;
            }
        }
        if (cCharAt == c) {
            int i15 = this.bp + i2;
            this.bp = i15;
            this.ch = charAt(i15);
            this.matchStat = 3;
            this.token = 16;
            return f;
        }
        this.matchStat = -1;
        return f;
    }

    /* JADX WARN: Code duplicated, block: B:56:0x00c7 A[ADDED_TO_REGION] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:57:0x00c9 -> B:53:0x00b7). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public double scanDouble(char r21) {
        /*
            Method dump skipped, instruction units count: 437
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexerBase.scanDouble(char):double");
    }

    /* JADX WARN: Code duplicated, block: B:53:0x00ad A[ADDED_TO_REGION] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:54:0x00af -> B:50:0x009d). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public java.math.BigDecimal scanDecimal(char r19) {
        /*
            Method dump skipped, instruction units count: 487
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexerBase.scanDecimal(char):java.math.BigDecimal");
    }

    public final float[] scanFieldFloatArray(char[] cArr) {
        int i;
        char cCharAt;
        int i2;
        float f;
        float[] fArr;
        char c;
        boolean z;
        char cCharAt2;
        boolean z2 = false;
        this.matchStat = 0;
        float[] fArr2 = null;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        int length = cArr.length;
        int i3 = length + 1;
        if (charAt(this.bp + length) != '[') {
            this.matchStat = -2;
            return null;
        }
        int i4 = i3 + 1;
        char cCharAt3 = charAt(this.bp + i3);
        float[] fArr3 = new float[16];
        int i5 = 0;
        while (true) {
            int i6 = (this.bp + i4) - 1;
            boolean z3 = cCharAt3 == '-' ? true : z2;
            if (z3) {
                cCharAt3 = charAt(this.bp + i4);
                i4++;
            }
            if (cCharAt3 < '0' || cCharAt3 > '9') {
                break;
            }
            int i7 = cCharAt3 - '0';
            while (true) {
                i = i4 + 1;
                cCharAt = charAt(this.bp + i4);
                if (cCharAt < '0' || cCharAt > '9') {
                    break;
                }
                i7 = (i7 * 10) + (cCharAt - '0');
                i4 = i;
            }
            if (cCharAt == '.' ? true : z2) {
                int i8 = i + 1;
                char cCharAt4 = charAt(this.bp + i);
                if (cCharAt4 < '0' || cCharAt4 > '9') {
                    this.matchStat = -1;
                    return fArr2;
                }
                i7 = (i7 * 10) + (cCharAt4 - '0');
                int i9 = 10;
                while (true) {
                    i = i8 + 1;
                    cCharAt2 = charAt(this.bp + i8);
                    if (cCharAt2 < '0' || cCharAt2 > '9') {
                        break;
                    }
                    i7 = (i7 * 10) + (cCharAt2 - '0');
                    i9 *= 10;
                    i8 = i;
                }
                int i10 = i9;
                cCharAt = cCharAt2;
                i2 = i10;
            } else {
                i2 = 1;
            }
            boolean z4 = cCharAt == 'e' || cCharAt == 'E';
            if (z4) {
                int i11 = i + 1;
                cCharAt = charAt(this.bp + i);
                if (cCharAt == '+' || cCharAt == '-') {
                    int i12 = i11 + 1;
                    cCharAt = charAt(this.bp + i11);
                    i = i12;
                } else {
                    i = i11;
                }
                while (cCharAt >= '0' && cCharAt <= '9') {
                    int i13 = i + 1;
                    cCharAt = charAt(this.bp + i);
                    i = i13;
                }
            }
            int i14 = ((this.bp + i) - i6) - 1;
            if (z4 || i14 >= 10) {
                f = Float.parseFloat(subString(i6, i14));
            } else {
                f = i7 / i2;
                if (z3) {
                    f = -f;
                }
            }
            if (i5 >= fArr3.length) {
                float[] fArr4 = new float[(fArr3.length * 3) / 2];
                System.arraycopy(fArr3, 0, fArr4, 0, i5);
                fArr3 = fArr4;
            }
            int i15 = i5 + 1;
            fArr3[i5] = f;
            if (cCharAt == ',') {
                i4 = i + 1;
                c = 16;
                z = false;
                cCharAt3 = charAt(this.bp + i);
                fArr = null;
            } else {
                if (cCharAt == ']') {
                    int i16 = i + 1;
                    char cCharAt5 = charAt(this.bp + i);
                    if (i15 != fArr3.length) {
                        float[] fArr5 = new float[i15];
                        System.arraycopy(fArr3, 0, fArr5, 0, i15);
                        fArr3 = fArr5;
                    }
                    if (cCharAt5 == ',') {
                        this.bp += i16 - 1;
                        next();
                        this.matchStat = 3;
                        this.token = 16;
                        return fArr3;
                    }
                    if (cCharAt5 == '}') {
                        int i17 = i16 + 1;
                        char cCharAt6 = charAt(this.bp + i16);
                        if (cCharAt6 == ',') {
                            this.token = 16;
                            this.bp += i17 - 1;
                            next();
                        } else if (cCharAt6 == ']') {
                            this.token = 15;
                            this.bp += i17 - 1;
                            next();
                        } else if (cCharAt6 == '}') {
                            this.token = 13;
                            this.bp += i17 - 1;
                            next();
                        } else if (cCharAt6 == 26) {
                            this.bp += i17 - 1;
                            this.token = 20;
                            this.ch = JSONLexer.EOI;
                        } else {
                            this.matchStat = -1;
                            return null;
                        }
                        this.matchStat = 4;
                        return fArr3;
                    }
                    this.matchStat = -1;
                    return null;
                }
                fArr = null;
                c = 16;
                z = false;
                cCharAt3 = cCharAt;
                i4 = i;
            }
            i5 = i15;
            fArr2 = fArr;
            z2 = z;
        }
        float[] fArr6 = fArr2;
        this.matchStat = -1;
        return fArr6;
    }

    public final float[][] scanFieldFloatArray2(char[] cArr) {
        int i;
        char cCharAt;
        int i2;
        int i3;
        float f;
        int i4;
        int i5;
        char cCharAt2;
        int i6 = 0;
        this.matchStat = 0;
        float[][] fArr = null;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return (float[][]) null;
        }
        int length = cArr.length;
        int i7 = length + 1;
        char c = '[';
        if (charAt(this.bp + length) != '[') {
            this.matchStat = -2;
            return (float[][]) null;
        }
        int i8 = i7 + 1;
        char cCharAt3 = charAt(this.bp + i7);
        int i9 = 16;
        float[][] fArr2 = new float[16][];
        int i10 = 0;
        while (cCharAt3 == c) {
            int i11 = i8 + 1;
            char cCharAt4 = charAt(this.bp + i8);
            float[] fArr3 = new float[i9];
            int i12 = i6;
            while (true) {
                int i13 = (this.bp + i11) - 1;
                int i14 = cCharAt4 == '-' ? 1 : i6;
                if (i14 != 0) {
                    cCharAt4 = charAt(this.bp + i11);
                    i11++;
                }
                if (cCharAt4 >= '0' && cCharAt4 <= '9') {
                    int i15 = cCharAt4 - '0';
                    while (true) {
                        i = i11 + 1;
                        cCharAt = charAt(this.bp + i11);
                        if (cCharAt < '0' || cCharAt > '9') {
                            break;
                        }
                        i15 = (i15 * 10) + (cCharAt - '0');
                        i11 = i;
                    }
                    if (cCharAt == '.') {
                        int i16 = i + 1;
                        char cCharAt5 = charAt(this.bp + i);
                        if (cCharAt5 < '0' || cCharAt5 > '9') {
                            this.matchStat = -1;
                            return fArr;
                        }
                        int i17 = (i15 * 10) + (cCharAt5 - '0');
                        int i18 = 10;
                        while (true) {
                            i = i16 + 1;
                            cCharAt2 = charAt(this.bp + i16);
                            if (cCharAt2 < '0' || cCharAt2 > '9') {
                                break;
                            }
                            i17 = (i17 * 10) + (cCharAt2 - '0');
                            i18 *= 10;
                            i16 = i;
                        }
                        int i19 = i17;
                        i3 = i18;
                        cCharAt = cCharAt2;
                        i2 = i19;
                    } else {
                        i2 = i15;
                        i3 = 1;
                    }
                    boolean z = cCharAt == 'e' || cCharAt == 'E';
                    if (z) {
                        int i20 = i + 1;
                        cCharAt = charAt(this.bp + i);
                        if (cCharAt == '+' || cCharAt == '-') {
                            int i21 = i20 + 1;
                            cCharAt = charAt(this.bp + i20);
                            i = i21;
                        } else {
                            i = i20;
                        }
                        while (cCharAt >= '0' && cCharAt <= '9') {
                            int i22 = i + 1;
                            cCharAt = charAt(this.bp + i);
                            i = i22;
                        }
                    }
                    int i23 = ((this.bp + i) - i13) - 1;
                    if (z || i23 >= 10) {
                        f = Float.parseFloat(subString(i13, i23));
                    } else {
                        f = i2 / i3;
                        if (i14 != 0) {
                            f = -f;
                        }
                    }
                    if (i12 >= fArr3.length) {
                        float[] fArr4 = new float[(fArr3.length * 3) / 2];
                        System.arraycopy(fArr3, 0, fArr4, 0, i12);
                        fArr3 = fArr4;
                    }
                    i4 = i12 + 1;
                    fArr3[i12] = f;
                    if (cCharAt == ',') {
                        cCharAt = charAt(this.bp + i);
                        i11 = i + 1;
                    } else {
                        if (cCharAt == ']') {
                            break;
                        }
                        i11 = i;
                    }
                    i12 = i4;
                    fArr = null;
                    cCharAt4 = cCharAt;
                    i6 = 0;
                } else {
                    this.matchStat = -1;
                    return (float[][]) null;
                }
            }
            int i24 = i + 1;
            char cCharAt6 = charAt(this.bp + i);
            if (i4 != fArr3.length) {
                float[] fArr5 = new float[i4];
                i5 = 0;
                System.arraycopy(fArr3, 0, fArr5, 0, i4);
                fArr3 = fArr5;
            } else {
                i5 = 0;
            }
            if (i10 >= fArr2.length) {
                float[][] fArr6 = new float[(fArr2.length * 3) / 2][];
                System.arraycopy(fArr3, i5, fArr6, i5, i4);
                fArr2 = fArr6;
            }
            int i25 = i10 + 1;
            fArr2[i10] = fArr3;
            if (cCharAt6 == ',') {
                i8 = i24 + 1;
                cCharAt3 = charAt(this.bp + i24);
            } else {
                if (cCharAt6 == ']') {
                    i8 = i24 + 1;
                    cCharAt3 = charAt(this.bp + i24);
                    i10 = i25;
                    break;
                }
                cCharAt3 = cCharAt6;
                i8 = i24;
            }
            i10 = i25;
            i6 = 0;
            fArr = null;
            c = '[';
            i9 = 16;
        }
        if (i10 != fArr2.length) {
            float[][] fArr7 = new float[i10][];
            System.arraycopy(fArr2, 0, fArr7, 0, i10);
            fArr2 = fArr7;
        }
        if (cCharAt3 == ',') {
            this.bp += i8 - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return fArr2;
        }
        if (cCharAt3 == '}') {
            int i26 = i8 + 1;
            char cCharAt7 = charAt(this.bp + i8);
            if (cCharAt7 == ',') {
                this.token = 16;
                this.bp += i26 - 1;
                next();
            } else if (cCharAt7 == ']') {
                this.token = 15;
                this.bp += i26 - 1;
                next();
            } else if (cCharAt7 == '}') {
                this.token = 13;
                this.bp += i26 - 1;
                next();
            } else if (cCharAt7 == 26) {
                this.bp += i26 - 1;
                this.token = 20;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return (float[][]) null;
            }
            this.matchStat = 4;
            return fArr2;
        }
        this.matchStat = -1;
        return (float[][]) null;
    }

    /* JADX WARN: Code duplicated, block: B:58:0x00dc A[ADDED_TO_REGION] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:59:0x00de -> B:55:0x00ca). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public final double scanFieldDouble(char[] r24) {
        /*
            Method dump skipped, instruction units count: 564
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexerBase.scanFieldDouble(char[]):double");
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00b8 A[ADDED_TO_REGION] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:56:0x00ba -> B:52:0x00a8). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public java.math.BigDecimal scanFieldDecimal(char[] r18) {
        /*
            Method dump skipped, instruction units count: 513
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexerBase.scanFieldDecimal(char[]):java.math.BigDecimal");
    }

    public BigInteger scanFieldBigInteger(char[] cArr) {
        int i;
        char cCharAt;
        boolean z;
        int length;
        int i2;
        BigInteger bigInteger;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        int length2 = cArr.length;
        int i3 = length2 + 1;
        char cCharAt2 = charAt(this.bp + length2);
        boolean z2 = cCharAt2 == '\"';
        if (z2) {
            cCharAt2 = charAt(this.bp + i3);
            i3++;
        }
        boolean z3 = cCharAt2 == '-';
        if (z3) {
            cCharAt2 = charAt(this.bp + i3);
            i3++;
        }
        if (cCharAt2 >= '0') {
            char c = '9';
            if (cCharAt2 <= '9') {
                long j = cCharAt2 - '0';
                while (true) {
                    i = i3 + 1;
                    cCharAt = charAt(this.bp + i3);
                    if (cCharAt < '0' || cCharAt > c) {
                        z = false;
                        break;
                    }
                    long j2 = (10 * j) + ((long) (cCharAt - '0'));
                    if (j2 < j) {
                        z = true;
                        break;
                    }
                    j = j2;
                    i3 = i;
                    c = '9';
                }
                if (!z2) {
                    int i4 = this.bp;
                    length = cArr.length + i4;
                    i2 = ((i4 + i) - length) - 1;
                } else {
                    if (cCharAt != '\"') {
                        this.matchStat = -1;
                        return null;
                    }
                    int i5 = i + 1;
                    cCharAt = charAt(this.bp + i);
                    int i6 = this.bp;
                    length = cArr.length + i6 + 1;
                    i2 = ((i6 + i5) - length) - 2;
                    i = i5;
                }
                if (!z && (i2 < 20 || (z3 && i2 < 21))) {
                    if (z3) {
                        j = -j;
                    }
                    bigInteger = BigInteger.valueOf(j);
                } else {
                    if (i2 > 65535) {
                        throw new JSONException("scanInteger overflow");
                    }
                    bigInteger = new BigInteger(subString(length, i2), 10);
                }
                if (cCharAt == ',') {
                    int i7 = this.bp + i;
                    this.bp = i7;
                    this.ch = charAt(i7);
                    this.matchStat = 3;
                    this.token = 16;
                    return bigInteger;
                }
                if (cCharAt == '}') {
                    int i8 = i + 1;
                    char cCharAt3 = charAt(this.bp + i);
                    if (cCharAt3 == ',') {
                        this.token = 16;
                        int i9 = this.bp + i8;
                        this.bp = i9;
                        this.ch = charAt(i9);
                    } else if (cCharAt3 == ']') {
                        this.token = 15;
                        int i10 = this.bp + i8;
                        this.bp = i10;
                        this.ch = charAt(i10);
                    } else if (cCharAt3 == '}') {
                        this.token = 13;
                        int i11 = this.bp + i8;
                        this.bp = i11;
                        this.ch = charAt(i11);
                    } else if (cCharAt3 == 26) {
                        this.token = 20;
                        this.bp += i8 - 1;
                        this.ch = JSONLexer.EOI;
                    } else {
                        this.matchStat = -1;
                        return null;
                    }
                    this.matchStat = 4;
                    return bigInteger;
                }
                this.matchStat = -1;
                return null;
            }
        }
        if (cCharAt2 == 'n' && charAt(this.bp + i3) == 'u' && charAt(this.bp + i3 + 1) == 'l' && charAt(this.bp + i3 + 2) == 'l') {
            this.matchStat = 5;
            int i12 = i3 + 3;
            int i13 = i12 + 1;
            char cCharAt4 = charAt(this.bp + i12);
            if (z2 && cCharAt4 == '\"') {
                cCharAt4 = charAt(this.bp + i13);
                i13++;
            }
            while (cCharAt4 != ',') {
                if (cCharAt4 == '}') {
                    int i14 = this.bp + i13;
                    this.bp = i14;
                    this.ch = charAt(i14);
                    this.matchStat = 5;
                    this.token = 13;
                    return null;
                }
                if (isWhitespace(cCharAt4)) {
                    cCharAt4 = charAt(this.bp + i13);
                    i13++;
                } else {
                    this.matchStat = -1;
                    return null;
                }
            }
            int i15 = this.bp + i13;
            this.bp = i15;
            this.ch = charAt(i15);
            this.matchStat = 5;
            this.token = 16;
            return null;
        }
        this.matchStat = -1;
        return null;
    }

    public Date scanFieldDate(char[] cArr) {
        int i;
        long j;
        Date date;
        int i2;
        char cCharAt;
        boolean z = false;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        int length = cArr.length;
        int i3 = length + 1;
        char cCharAt2 = charAt(this.bp + length);
        if (cCharAt2 == '\"') {
            int iIndexOf = indexOf('\"', this.bp + cArr.length + 1);
            if (iIndexOf == -1) {
                throw new JSONException("unclosed str");
            }
            int length2 = this.bp + cArr.length + 1;
            String strSubString = subString(length2, iIndexOf - length2);
            if (strSubString.indexOf(92) != -1) {
                while (true) {
                    int i4 = 0;
                    for (int i5 = iIndexOf - 1; i5 >= 0 && charAt(i5) == '\\'; i5--) {
                        i4++;
                    }
                    if (i4 % 2 == 0) {
                        break;
                    }
                    iIndexOf = indexOf('\"', iIndexOf + 1);
                }
                int i6 = this.bp;
                int length3 = iIndexOf - ((cArr.length + i6) + 1);
                strSubString = readString(sub_chars(i6 + cArr.length + 1, length3), length3);
            }
            int i7 = this.bp;
            int length4 = i3 + (iIndexOf - ((cArr.length + i7) + 1)) + 1;
            i = length4 + 1;
            cCharAt2 = charAt(i7 + length4);
            JSONScanner jSONScanner = new JSONScanner(strSubString);
            try {
                if (jSONScanner.scanISO8601DateIfMatch(false)) {
                    date = jSONScanner.getCalendar().getTime();
                    jSONScanner.close();
                } else {
                    this.matchStat = -1;
                    jSONScanner.close();
                    return null;
                }
            } catch (Throwable th) {
                jSONScanner.close();
                throw th;
            }
        } else {
            if (cCharAt2 != '-' && (cCharAt2 < '0' || cCharAt2 > '9')) {
                this.matchStat = -1;
                return null;
            }
            if (cCharAt2 == '-') {
                cCharAt2 = charAt(this.bp + i3);
                i3++;
                z = true;
            }
            if (cCharAt2 < '0' || cCharAt2 > '9') {
                i = i3;
                j = 0;
            } else {
                j = cCharAt2 - '0';
                while (true) {
                    i2 = i3 + 1;
                    cCharAt = charAt(this.bp + i3);
                    if (cCharAt < '0' || cCharAt > '9') {
                        break;
                    }
                    j = (j * 10) + ((long) (cCharAt - '0'));
                    i3 = i2;
                }
                cCharAt2 = cCharAt;
                i = i2;
            }
            if (j < 0) {
                this.matchStat = -1;
                return null;
            }
            if (z) {
                j = -j;
            }
            date = new Date(j);
        }
        if (cCharAt2 == ',') {
            int i8 = this.bp + i;
            this.bp = i8;
            this.ch = charAt(i8);
            this.matchStat = 3;
            return date;
        }
        if (cCharAt2 == '}') {
            int i9 = i + 1;
            char cCharAt3 = charAt(this.bp + i);
            if (cCharAt3 == ',') {
                this.token = 16;
                int i10 = this.bp + i9;
                this.bp = i10;
                this.ch = charAt(i10);
            } else if (cCharAt3 == ']') {
                this.token = 15;
                int i11 = this.bp + i9;
                this.bp = i11;
                this.ch = charAt(i11);
            } else if (cCharAt3 == '}') {
                this.token = 13;
                int i12 = this.bp + i9;
                this.bp = i12;
                this.ch = charAt(i12);
            } else if (cCharAt3 == 26) {
                this.token = 20;
                this.bp += i9 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return date;
        }
        this.matchStat = -1;
        return null;
    }

    public Date scanDate(char c) {
        long j;
        int i;
        Date date;
        boolean z = false;
        this.matchStat = 0;
        char cCharAt = charAt(this.bp + 0);
        int i2 = 5;
        if (cCharAt == '\"') {
            int iIndexOf = indexOf('\"', this.bp + 1);
            if (iIndexOf == -1) {
                throw new JSONException("unclosed str");
            }
            int i3 = this.bp + 1;
            String strSubString = subString(i3, iIndexOf - i3);
            if (strSubString.indexOf(92) != -1) {
                while (true) {
                    int i4 = 0;
                    for (int i5 = iIndexOf - 1; i5 >= 0 && charAt(i5) == '\\'; i5--) {
                        i4++;
                    }
                    if (i4 % 2 == 0) {
                        break;
                    }
                    iIndexOf = indexOf('\"', iIndexOf + 1);
                }
                int i6 = this.bp;
                int i7 = iIndexOf - (i6 + 1);
                strSubString = readString(sub_chars(i6 + 1, i7), i7);
            }
            int i8 = this.bp;
            int i9 = (iIndexOf - (i8 + 1)) + 1 + 1;
            int i10 = i9 + 1;
            cCharAt = charAt(i8 + i9);
            JSONScanner jSONScanner = new JSONScanner(strSubString);
            try {
                if (jSONScanner.scanISO8601DateIfMatch(false)) {
                    date = jSONScanner.getCalendar().getTime();
                    jSONScanner.close();
                    i2 = i10;
                } else {
                    this.matchStat = -1;
                    jSONScanner.close();
                    return null;
                }
            } catch (Throwable th) {
                jSONScanner.close();
                throw th;
            }
        } else {
            char c2 = '9';
            int i11 = 2;
            if (cCharAt == '-' || (cCharAt >= '0' && cCharAt <= '9')) {
                if (cCharAt == '-') {
                    cCharAt = charAt(this.bp + 1);
                    z = true;
                } else {
                    i11 = 1;
                }
                if (cCharAt < '0' || cCharAt > '9') {
                    j = 0;
                    i = i11;
                } else {
                    j = cCharAt - '0';
                    while (true) {
                        i = i11 + 1;
                        cCharAt = charAt(this.bp + i11);
                        if (cCharAt < '0' || cCharAt > c2) {
                            break;
                        }
                        j = (j * 10) + ((long) (cCharAt - '0'));
                        i11 = i;
                        c2 = '9';
                    }
                }
                if (j < 0) {
                    this.matchStat = -1;
                    return null;
                }
                if (z) {
                    j = -j;
                }
                date = new Date(j);
                i2 = i;
            } else if (cCharAt == 'n' && charAt(this.bp + 1) == 'u' && charAt(this.bp + 1 + 1) == 'l' && charAt(this.bp + 1 + 2) == 'l') {
                this.matchStat = 5;
                cCharAt = charAt(this.bp + 4);
                date = null;
            } else {
                this.matchStat = -1;
                return null;
            }
        }
        if (cCharAt == ',') {
            int i12 = this.bp + i2;
            this.bp = i12;
            this.ch = charAt(i12);
            this.matchStat = 3;
            this.token = 16;
            return date;
        }
        if (cCharAt == ']') {
            int i13 = i2 + 1;
            char cCharAt2 = charAt(this.bp + i2);
            if (cCharAt2 == ',') {
                this.token = 16;
                int i14 = this.bp + i13;
                this.bp = i14;
                this.ch = charAt(i14);
            } else if (cCharAt2 == ']') {
                this.token = 15;
                int i15 = this.bp + i13;
                this.bp = i15;
                this.ch = charAt(i15);
            } else if (cCharAt2 == '}') {
                this.token = 13;
                int i16 = this.bp + i13;
                this.bp = i16;
                this.ch = charAt(i16);
            } else if (cCharAt2 == 26) {
                this.token = 20;
                this.bp += i13 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return date;
        }
        this.matchStat = -1;
        return null;
    }

    public UUID scanFieldUUID(char[] cArr) {
        char cCharAt;
        int i;
        UUID uuid;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        this.matchStat = 0;
        if (!charArrayCompare(cArr)) {
            this.matchStat = -2;
            return null;
        }
        int length = cArr.length;
        int i15 = length + 1;
        char cCharAt2 = charAt(this.bp + length);
        char c = 4;
        if (cCharAt2 == '\"') {
            int iIndexOf = indexOf('\"', this.bp + cArr.length + 1);
            if (iIndexOf == -1) {
                throw new JSONException("unclosed str");
            }
            int length2 = this.bp + cArr.length + 1;
            int i16 = iIndexOf - length2;
            char c2 = 'F';
            char c3 = 'f';
            char c4 = DateFormat.CAPITAL_AM_PM;
            char c5 = '0';
            if (i16 == 36) {
                int i17 = 0;
                long j = 0;
                while (i17 < 8) {
                    char cCharAt3 = charAt(length2 + i17);
                    if (cCharAt3 < '0' || cCharAt3 > '9') {
                        if (cCharAt3 >= 'a' && cCharAt3 <= 'f') {
                            i13 = cCharAt3 - 'a';
                        } else {
                            if (cCharAt3 < 'A' || cCharAt3 > c2) {
                                this.matchStat = -2;
                                return null;
                            }
                            i13 = cCharAt3 - 'A';
                        }
                        i14 = i13 + 10;
                    } else {
                        i14 = cCharAt3 - '0';
                    }
                    j = (j << 4) | ((long) i14);
                    i17++;
                    iIndexOf = iIndexOf;
                    c2 = 'F';
                }
                int i18 = iIndexOf;
                int i19 = 9;
                int i20 = 13;
                while (i19 < i20) {
                    char cCharAt4 = charAt(length2 + i19);
                    if (cCharAt4 < '0' || cCharAt4 > '9') {
                        if (cCharAt4 >= 'a' && cCharAt4 <= 'f') {
                            i11 = cCharAt4 - 'a';
                        } else {
                            if (cCharAt4 < c4 || cCharAt4 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i11 = cCharAt4 - 'A';
                        }
                        i12 = i11 + 10;
                    } else {
                        i12 = cCharAt4 - '0';
                    }
                    j = (j << c) | ((long) i12);
                    i19++;
                    i20 = 13;
                    c4 = DateFormat.CAPITAL_AM_PM;
                    c = 4;
                }
                long j2 = j;
                for (int i21 = 14; i21 < 18; i21++) {
                    char cCharAt5 = charAt(length2 + i21);
                    if (cCharAt5 < '0' || cCharAt5 > '9') {
                        if (cCharAt5 >= 'a' && cCharAt5 <= 'f') {
                            i9 = cCharAt5 - 'a';
                        } else {
                            if (cCharAt5 < 'A' || cCharAt5 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i9 = cCharAt5 - 'A';
                        }
                        i10 = i9 + 10;
                    } else {
                        i10 = cCharAt5 - '0';
                    }
                    j2 = (j2 << 4) | ((long) i10);
                }
                long j3 = 0;
                for (int i22 = 19; i22 < 23; i22++) {
                    char cCharAt6 = charAt(length2 + i22);
                    if (cCharAt6 < '0' || cCharAt6 > '9') {
                        if (cCharAt6 >= 'a' && cCharAt6 <= 'f') {
                            i7 = cCharAt6 - 'a';
                        } else {
                            if (cCharAt6 < 'A' || cCharAt6 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i7 = cCharAt6 - 'A';
                        }
                        i8 = i7 + 10;
                    } else {
                        i8 = cCharAt6 - '0';
                    }
                    j3 = (j3 << 4) | ((long) i8);
                }
                int i23 = 24;
                long j4 = j3;
                int i24 = 36;
                while (i23 < i24) {
                    char cCharAt7 = charAt(length2 + i23);
                    if (cCharAt7 < c5 || cCharAt7 > '9') {
                        if (cCharAt7 >= 'a' && cCharAt7 <= c3) {
                            i5 = cCharAt7 - 'a';
                        } else {
                            if (cCharAt7 < 'A' || cCharAt7 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i5 = cCharAt7 - 'A';
                        }
                        i6 = i5 + 10;
                    } else {
                        i6 = cCharAt7 - '0';
                    }
                    j4 = (j4 << 4) | ((long) i6);
                    i23++;
                    i15 = i15;
                    i24 = 36;
                    c5 = '0';
                    c3 = 'f';
                }
                uuid = new UUID(j2, j4);
                int i25 = this.bp;
                int length3 = i15 + (i18 - ((cArr.length + i25) + 1)) + 1;
                i = length3 + 1;
                cCharAt = charAt(i25 + length3);
            } else {
                if (i16 == 32) {
                    long j5 = 0;
                    for (int i26 = 0; i26 < 16; i26++) {
                        char cCharAt8 = charAt(length2 + i26);
                        if (cCharAt8 < '0' || cCharAt8 > '9') {
                            if (cCharAt8 >= 'a' && cCharAt8 <= 'f') {
                                i3 = cCharAt8 - 'a';
                            } else {
                                if (cCharAt8 < 'A' || cCharAt8 > 'F') {
                                    this.matchStat = -2;
                                    return null;
                                }
                                i3 = cCharAt8 - 'A';
                            }
                            i4 = i3 + 10;
                        } else {
                            i4 = cCharAt8 - '0';
                        }
                        j5 = (j5 << 4) | ((long) i4);
                    }
                    int i27 = 16;
                    long j6 = 0;
                    for (int i28 = 32; i27 < i28; i28 = 32) {
                        char cCharAt9 = charAt(length2 + i27);
                        if (cCharAt9 < '0' || cCharAt9 > '9') {
                            if (cCharAt9 >= 'a' && cCharAt9 <= 'f') {
                                i2 = (cCharAt9 - 'a') + 10;
                            } else {
                                if (cCharAt9 < 'A' || cCharAt9 > 'F') {
                                    this.matchStat = -2;
                                    return null;
                                }
                                i2 = (cCharAt9 - 'A') + 10;
                            }
                            j6 = (j6 << 4) | ((long) i2);
                            i27++;
                        } else {
                            i2 = cCharAt9 - '0';
                        }
                        j6 = (j6 << 4) | ((long) i2);
                        i27++;
                    }
                    uuid = new UUID(j5, j6);
                    int i29 = this.bp;
                    int length4 = i15 + (iIndexOf - ((cArr.length + i29) + 1)) + 1;
                    i = length4 + 1;
                    cCharAt = charAt(i29 + length4);
                } else {
                    this.matchStat = -1;
                    return null;
                }
            }
        } else {
            if (cCharAt2 == 'n') {
                int i30 = i15 + 1;
                if (charAt(this.bp + i15) == 'u') {
                    int i31 = i30 + 1;
                    if (charAt(this.bp + i30) == 'l') {
                        int i32 = i31 + 1;
                        if (charAt(this.bp + i31) == 'l') {
                            cCharAt = charAt(this.bp + i32);
                            i = i32 + 1;
                            uuid = null;
                        }
                    }
                }
            }
            this.matchStat = -1;
            return null;
        }
        if (cCharAt == ',') {
            int i33 = this.bp + i;
            this.bp = i33;
            this.ch = charAt(i33);
            this.matchStat = 3;
            return uuid;
        }
        if (cCharAt == '}') {
            int i34 = i + 1;
            char cCharAt10 = charAt(this.bp + i);
            if (cCharAt10 == ',') {
                this.token = 16;
                int i35 = this.bp + i34;
                this.bp = i35;
                this.ch = charAt(i35);
            } else if (cCharAt10 == ']') {
                this.token = 15;
                int i36 = this.bp + i34;
                this.bp = i36;
                this.ch = charAt(i36);
            } else if (cCharAt10 == '}') {
                this.token = 13;
                int i37 = this.bp + i34;
                this.bp = i37;
                this.ch = charAt(i37);
            } else if (cCharAt10 == 26) {
                this.token = 20;
                this.bp += i34 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return uuid;
        }
        this.matchStat = -1;
        return null;
    }

    public UUID scanUUID(char c) {
        int i;
        char cCharAt;
        UUID uuid;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        this.matchStat = 0;
        char cCharAt2 = charAt(this.bp + 0);
        int i15 = 13;
        char c2 = 4;
        if (cCharAt2 == '\"') {
            int iIndexOf = indexOf('\"', this.bp + 1);
            if (iIndexOf == -1) {
                throw new JSONException("unclosed str");
            }
            int i16 = this.bp + 1;
            int i17 = iIndexOf - i16;
            char c3 = 'f';
            char c4 = DateFormat.CAPITAL_AM_PM;
            char c5 = DateFormat.AM_PM;
            if (i17 == 36) {
                int i18 = 0;
                long j = 0;
                while (i18 < 8) {
                    char cCharAt3 = charAt(i16 + i18);
                    if (cCharAt3 < '0' || cCharAt3 > '9') {
                        if (cCharAt3 >= 'a' && cCharAt3 <= c3) {
                            i13 = cCharAt3 - 'a';
                        } else {
                            if (cCharAt3 < 'A' || cCharAt3 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i13 = cCharAt3 - 'A';
                        }
                        i14 = i13 + 10;
                    } else {
                        i14 = cCharAt3 - '0';
                    }
                    j = (j << 4) | ((long) i14);
                    i18++;
                    c3 = 'f';
                }
                int i19 = 9;
                while (i19 < i15) {
                    char cCharAt4 = charAt(i16 + i19);
                    if (cCharAt4 < '0' || cCharAt4 > '9') {
                        if (cCharAt4 >= 'a' && cCharAt4 <= 'f') {
                            i11 = cCharAt4 - 'a';
                        } else {
                            if (cCharAt4 < c4 || cCharAt4 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i11 = cCharAt4 - 'A';
                        }
                        i12 = i11 + 10;
                    } else {
                        i12 = cCharAt4 - '0';
                    }
                    j = (j << 4) | ((long) i12);
                    i19++;
                    i15 = 13;
                    c4 = DateFormat.CAPITAL_AM_PM;
                }
                long j2 = j;
                for (int i20 = 14; i20 < 18; i20++) {
                    char cCharAt5 = charAt(i16 + i20);
                    if (cCharAt5 < '0' || cCharAt5 > '9') {
                        if (cCharAt5 >= 'a' && cCharAt5 <= 'f') {
                            i9 = cCharAt5 - 'a';
                        } else {
                            if (cCharAt5 < 'A' || cCharAt5 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i9 = cCharAt5 - 'A';
                        }
                        i10 = i9 + 10;
                    } else {
                        i10 = cCharAt5 - '0';
                    }
                    j2 = (j2 << 4) | ((long) i10);
                }
                int i21 = 19;
                long j3 = 0;
                while (i21 < 23) {
                    char cCharAt6 = charAt(i16 + i21);
                    if (cCharAt6 < '0' || cCharAt6 > '9') {
                        if (cCharAt6 >= c5 && cCharAt6 <= 'f') {
                            i7 = cCharAt6 - 'a';
                        } else {
                            if (cCharAt6 < 'A' || cCharAt6 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i7 = cCharAt6 - 'A';
                        }
                        i8 = i7 + 10;
                    } else {
                        i8 = cCharAt6 - '0';
                    }
                    j3 = (j3 << c2) | ((long) i8);
                    i21++;
                    c5 = DateFormat.AM_PM;
                    c2 = 4;
                }
                long j4 = j3;
                for (int i22 = 24; i22 < 36; i22++) {
                    char cCharAt7 = charAt(i16 + i22);
                    if (cCharAt7 < '0' || cCharAt7 > '9') {
                        if (cCharAt7 >= 'a' && cCharAt7 <= 'f') {
                            i5 = cCharAt7 - 'a';
                        } else {
                            if (cCharAt7 < 'A' || cCharAt7 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i5 = cCharAt7 - 'A';
                        }
                        i6 = i5 + 10;
                    } else {
                        i6 = cCharAt7 - '0';
                    }
                    j4 = (j4 << 4) | ((long) i6);
                }
                uuid = new UUID(j2, j4);
                int i23 = this.bp;
                int i24 = 1 + (iIndexOf - (i23 + 1)) + 1;
                i = i24 + 1;
                cCharAt = charAt(i23 + i24);
            } else {
                if (i17 == 32) {
                    long j5 = 0;
                    for (int i25 = 0; i25 < 16; i25++) {
                        char cCharAt8 = charAt(i16 + i25);
                        if (cCharAt8 < '0' || cCharAt8 > '9') {
                            if (cCharAt8 >= 'a' && cCharAt8 <= 'f') {
                                i3 = cCharAt8 - 'a';
                            } else {
                                if (cCharAt8 < 'A' || cCharAt8 > 'F') {
                                    this.matchStat = -2;
                                    return null;
                                }
                                i3 = cCharAt8 - 'A';
                            }
                            i4 = i3 + 10;
                        } else {
                            i4 = cCharAt8 - '0';
                        }
                        j5 = (j5 << 4) | ((long) i4);
                    }
                    int i26 = 16;
                    long j6 = 0;
                    for (int i27 = 32; i26 < i27; i27 = 32) {
                        char cCharAt9 = charAt(i16 + i26);
                        if (cCharAt9 >= '0' && cCharAt9 <= '9') {
                            i2 = cCharAt9 - '0';
                        } else if (cCharAt9 >= 'a' && cCharAt9 <= 'f') {
                            i2 = (cCharAt9 - 'a') + 10;
                        } else {
                            if (cCharAt9 < 'A' || cCharAt9 > 'F') {
                                this.matchStat = -2;
                                return null;
                            }
                            i2 = (cCharAt9 - 'A') + 10;
                        }
                        j6 = (j6 << 4) | ((long) i2);
                        i26++;
                    }
                    uuid = new UUID(j5, j6);
                    int i28 = this.bp;
                    int i29 = 1 + (iIndexOf - (i28 + 1)) + 1;
                    i = i29 + 1;
                    cCharAt = charAt(i28 + i29);
                } else {
                    this.matchStat = -1;
                    return null;
                }
            }
        } else if (cCharAt2 == 'n' && charAt(this.bp + 1) == 'u' && charAt(this.bp + 2) == 'l' && charAt(this.bp + 3) == 'l') {
            i = 5;
            cCharAt = charAt(this.bp + 4);
            uuid = null;
        } else {
            this.matchStat = -1;
            return null;
        }
        if (cCharAt == ',') {
            int i30 = this.bp + i;
            this.bp = i30;
            this.ch = charAt(i30);
            this.matchStat = 3;
            return uuid;
        }
        if (cCharAt == ']') {
            int i31 = i + 1;
            char cCharAt10 = charAt(this.bp + i);
            if (cCharAt10 == ',') {
                this.token = 16;
                int i32 = this.bp + i31;
                this.bp = i32;
                this.ch = charAt(i32);
            } else if (cCharAt10 == ']') {
                this.token = 15;
                int i33 = this.bp + i31;
                this.bp = i33;
                this.ch = charAt(i33);
            } else if (cCharAt10 == '}') {
                this.token = 13;
                int i34 = this.bp + i31;
                this.bp = i34;
                this.ch = charAt(i34);
            } else if (cCharAt10 == 26) {
                this.token = 20;
                this.bp += i31 - 1;
                this.ch = JSONLexer.EOI;
            } else {
                this.matchStat = -1;
                return null;
            }
            this.matchStat = 4;
            return uuid;
        }
        this.matchStat = -1;
        return null;
    }

    public final void scanTrue() {
        if (this.ch != 't') {
            throw new JSONException("error parse true");
        }
        next();
        if (this.ch != 'r') {
            throw new JSONException("error parse true");
        }
        next();
        if (this.ch != 'u') {
            throw new JSONException("error parse true");
        }
        next();
        if (this.ch != 'e') {
            throw new JSONException("error parse true");
        }
        next();
        char c = this.ch;
        if (c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == 26 || c == '\f' || c == '\b' || c == ':' || c == '/') {
            this.token = 6;
            return;
        }
        throw new JSONException("scan true error");
    }

    public final void scanNullOrNew() {
        scanNullOrNew(true);
    }

    public final void scanNullOrNew(boolean z) {
        char c;
        if (this.ch != 'n') {
            throw new JSONException("error parse null or new");
        }
        next();
        char c2 = this.ch;
        if (c2 != 'u') {
            if (c2 != 'e') {
                throw new JSONException("error parse new");
            }
            next();
            if (this.ch != 'w') {
                throw new JSONException("error parse new");
            }
            next();
            char c3 = this.ch;
            if (c3 == ' ' || c3 == ',' || c3 == '}' || c3 == ']' || c3 == '\n' || c3 == '\r' || c3 == '\t' || c3 == 26 || c3 == '\f' || c3 == '\b') {
                this.token = 9;
                return;
            }
            throw new JSONException("scan new error");
        }
        next();
        if (this.ch != 'l') {
            throw new JSONException("error parse null");
        }
        next();
        if (this.ch != 'l') {
            throw new JSONException("error parse null");
        }
        next();
        char c4 = this.ch;
        if (c4 == ' ' || c4 == ',' || c4 == '}' || c4 == ']' || c4 == '\n' || c4 == '\r' || c4 == '\t' || c4 == 26 || ((c4 == ':' && z) || (c = this.ch) == '\f' || c == '\b')) {
            this.token = 8;
            return;
        }
        throw new JSONException("scan null error");
    }

    public final void scanFalse() {
        if (this.ch != 'f') {
            throw new JSONException("error parse false");
        }
        next();
        if (this.ch != 'a') {
            throw new JSONException("error parse false");
        }
        next();
        if (this.ch != 'l') {
            throw new JSONException("error parse false");
        }
        next();
        if (this.ch != 's') {
            throw new JSONException("error parse false");
        }
        next();
        if (this.ch != 'e') {
            throw new JSONException("error parse false");
        }
        next();
        char c = this.ch;
        if (c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == 26 || c == '\f' || c == '\b' || c == ':' || c == '/') {
            this.token = 7;
            return;
        }
        throw new JSONException("scan false error");
    }

    public final void scanIdent() {
        this.np = this.bp - 1;
        this.hasSpecial = false;
        do {
            this.sp++;
            next();
        } while (Character.isLetterOrDigit(this.ch));
        String strStringVal = stringVal();
        if ("null".equalsIgnoreCase(strStringVal)) {
            this.token = 8;
            return;
        }
        if ("new".equals(strStringVal)) {
            this.token = 9;
            return;
        }
        if ("true".equals(strStringVal)) {
            this.token = 6;
            return;
        }
        if ("false".equals(strStringVal)) {
            this.token = 7;
            return;
        }
        if ("undefined".equals(strStringVal)) {
            this.token = 23;
            return;
        }
        if ("Set".equals(strStringVal)) {
            this.token = 21;
        } else if ("TreeSet".equals(strStringVal)) {
            this.token = 22;
        } else {
            this.token = 18;
        }
    }

    /* JADX WARN: Code duplicated, block: B:46:0x00e8  */
    public static String readString(char[] cArr, int i) {
        int i2;
        char[] cArr2 = new char[i];
        int i3 = 0;
        int i4 = 0;
        while (i3 < i) {
            char c = cArr[i3];
            if (c != '\\') {
                cArr2[i4] = c;
                i4++;
            } else {
                i3++;
                char c2 = cArr[i3];
                if (c2 == '\"') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\"';
                } else if (c2 == '\'') {
                    i2 = i4 + 1;
                    cArr2[i4] = DateFormat.QUOTE;
                } else if (c2 == 'F') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\f';
                } else if (c2 == '\\') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\\';
                } else if (c2 == 'b') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\b';
                } else if (c2 == 'f') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\f';
                } else if (c2 == 'n') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\n';
                } else if (c2 == 'r') {
                    i2 = i4 + 1;
                    cArr2[i4] = '\r';
                } else if (c2 != 'x') {
                    switch (c2) {
                        case '/':
                            i2 = i4 + 1;
                            cArr2[i4] = '/';
                            break;
                        case '0':
                            i2 = i4 + 1;
                            cArr2[i4] = 0;
                            break;
                        case '1':
                            i2 = i4 + 1;
                            cArr2[i4] = 1;
                            break;
                        case '2':
                            i2 = i4 + 1;
                            cArr2[i4] = 2;
                            break;
                        case '3':
                            i2 = i4 + 1;
                            cArr2[i4] = 3;
                            break;
                        case '4':
                            i2 = i4 + 1;
                            cArr2[i4] = 4;
                            break;
                        case '5':
                            i2 = i4 + 1;
                            cArr2[i4] = 5;
                            break;
                        case '6':
                            i2 = i4 + 1;
                            cArr2[i4] = 6;
                            break;
                        case '7':
                            i2 = i4 + 1;
                            cArr2[i4] = 7;
                            break;
                        default:
                            switch (c2) {
                                case 't':
                                    i2 = i4 + 1;
                                    cArr2[i4] = '\t';
                                    break;
                                case 'u':
                                    i2 = i4 + 1;
                                    int i5 = i3 + 1;
                                    int i6 = i5 + 1;
                                    int i7 = i6 + 1;
                                    i3 = i7 + 1;
                                    cArr2[i4] = (char) Integer.parseInt(new String(new char[]{cArr[i5], cArr[i6], cArr[i7], cArr[i3]}), 16);
                                    break;
                                case 'v':
                                    i2 = i4 + 1;
                                    cArr2[i4] = 11;
                                    break;
                                default:
                                    throw new JSONException("unclosed.str.lit");
                            }
                            break;
                    }
                } else {
                    i2 = i4 + 1;
                    int[] iArr = digits;
                    int i8 = i3 + 1;
                    int i9 = iArr[cArr[i8]] * 16;
                    i3 = i8 + 1;
                    cArr2[i4] = (char) (i9 + iArr[cArr[i3]]);
                }
                i4 = i2;
            }
            i3++;
        }
        return new String(cArr2, 0, i4);
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public boolean isBlankInput() {
        int i = 0;
        while (true) {
            char cCharAt = charAt(i);
            if (cCharAt == 26) {
                this.token = 20;
                return true;
            }
            if (!isWhitespace(cCharAt)) {
                return false;
            }
            i++;
        }
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void skipWhitespace() {
        while (true) {
            char c = this.ch;
            if (c > '/') {
                return;
            }
            if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f' || c == '\b') {
                next();
            } else if (c != '/') {
                return;
            } else {
                skipComment();
            }
        }
    }

    private void scanStringSingleQuote() {
        this.np = this.bp;
        this.hasSpecial = false;
        while (true) {
            char next = next();
            if (next == '\'') {
                this.token = 4;
                next();
                return;
            }
            if (next != 26) {
                boolean z = true;
                if (next == '\\') {
                    if (!this.hasSpecial) {
                        this.hasSpecial = true;
                        int i = this.sp;
                        char[] cArr = this.sbuf;
                        if (i > cArr.length) {
                            char[] cArr2 = new char[i * 2];
                            System.arraycopy(cArr, 0, cArr2, 0, cArr.length);
                            this.sbuf = cArr2;
                        }
                        copyTo(this.np + 1, this.sp, this.sbuf);
                    }
                    char next2 = next();
                    if (next2 == '\"') {
                        putChar('\"');
                    } else if (next2 != '\'') {
                        if (next2 != 'F') {
                            if (next2 == '\\') {
                                putChar('\\');
                            } else if (next2 == 'b') {
                                putChar('\b');
                            } else if (next2 != 'f') {
                                if (next2 == 'n') {
                                    putChar('\n');
                                } else if (next2 == 'r') {
                                    putChar('\r');
                                } else if (next2 != 'x') {
                                    switch (next2) {
                                        case '/':
                                            putChar('/');
                                            break;
                                        case '0':
                                            putChar((char) 0);
                                            break;
                                        case '1':
                                            putChar((char) 1);
                                            break;
                                        case '2':
                                            putChar((char) 2);
                                            break;
                                        case '3':
                                            putChar((char) 3);
                                            break;
                                        case '4':
                                            putChar((char) 4);
                                            break;
                                        case '5':
                                            putChar((char) 5);
                                            break;
                                        case '6':
                                            putChar((char) 6);
                                            break;
                                        case '7':
                                            putChar((char) 7);
                                            break;
                                        default:
                                            switch (next2) {
                                                case 't':
                                                    putChar('\t');
                                                    break;
                                                case 'u':
                                                    putChar((char) Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16));
                                                    break;
                                                case 'v':
                                                    putChar((char) 11);
                                                    break;
                                                default:
                                                    this.ch = next2;
                                                    throw new JSONException("unclosed single-quote string");
                                            }
                                            break;
                                    }
                                } else {
                                    char next3 = next();
                                    char next4 = next();
                                    boolean z2 = (next3 >= '0' && next3 <= '9') || (next3 >= 'a' && next3 <= 'f') || (next3 >= 'A' && next3 <= 'F');
                                    if ((next4 < '0' || next4 > '9') && ((next4 < 'a' || next4 > 'f') && (next4 < 'A' || next4 > 'F'))) {
                                        z = false;
                                    }
                                    if (!z2 || !z) {
                                        throw new JSONException("invalid escape character \\x" + next3 + next4);
                                    }
                                    int[] iArr = digits;
                                    putChar((char) ((iArr[next3] * 16) + iArr[next4]));
                                }
                            }
                        }
                        putChar('\f');
                    } else {
                        putChar(DateFormat.QUOTE);
                    }
                } else if (!this.hasSpecial) {
                    this.sp++;
                } else {
                    int i2 = this.sp;
                    char[] cArr3 = this.sbuf;
                    if (i2 == cArr3.length) {
                        putChar(next);
                    } else {
                        this.sp = i2 + 1;
                        cArr3[i2] = next;
                    }
                }
            } else if (!isEOF()) {
                putChar(JSONLexer.EOI);
            } else {
                throw new JSONException("unclosed single-quote string");
            }
        }
    }

    protected final void putChar(char c) {
        int i = this.sp;
        char[] cArr = this.sbuf;
        if (i >= cArr.length) {
            int length = cArr.length * 2;
            if (length < i) {
                length = i + 1;
            }
            char[] cArr2 = new char[length];
            char[] cArr3 = this.sbuf;
            System.arraycopy(cArr3, 0, cArr2, 0, cArr3.length);
            this.sbuf = cArr2;
        }
        char[] cArr4 = this.sbuf;
        int i2 = this.sp;
        this.sp = i2 + 1;
        cArr4[i2] = c;
    }

    public final void scanHex() {
        char next;
        if (this.ch != 'x') {
            throw new JSONException("illegal state. " + this.ch);
        }
        next();
        if (this.ch != '\'') {
            throw new JSONException("illegal state. " + this.ch);
        }
        this.np = this.bp;
        next();
        if (this.ch == '\'') {
            next();
            this.token = 26;
            return;
        }
        while (true) {
            next = next();
            if ((next < '0' || next > '9') && (next < 'A' || next > 'F')) {
                break;
            } else {
                this.sp++;
            }
        }
        if (next == '\'') {
            this.sp++;
            next();
            this.token = 26;
        } else {
            throw new JSONException("illegal state. " + next);
        }
    }

    /* JADX WARN: Code duplicated, block: B:53:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:54:0x00cb  */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final void scanNumber() {
        this.np = this.bp;
        boolean z = true;
        if (this.ch == '-') {
            this.sp++;
            next();
        }
        while (true) {
            char c = this.ch;
            if (c < '0' || c > '9') {
                break;
            }
            this.sp++;
            next();
        }
        boolean z2 = false;
        if (this.ch == '.') {
            this.sp++;
            next();
            while (true) {
                char c2 = this.ch;
                if (c2 < '0' || c2 > '9') {
                    break;
                }
                this.sp++;
                next();
            }
            z2 = true;
        }
        int i = this.sp;
        if (i > 65535) {
            throw new JSONException("scanNumber overflow");
        }
        char c3 = this.ch;
        if (c3 != 'L' && c3 != 'S' && c3 != 'B') {
            if (c3 == 'F' || c3 == 'D') {
                this.sp = i + 1;
                next();
            } else if (c3 == 'e' || c3 == 'E') {
                this.sp++;
                next();
                char c4 = this.ch;
                if (c4 == '+' || c4 == '-') {
                    this.sp++;
                    next();
                }
                while (true) {
                    char c5 = this.ch;
                    if (c5 < '0' || c5 > '9') {
                        break;
                    }
                    this.sp++;
                    next();
                }
                char c6 = this.ch;
                if (c6 == 'D' || c6 == 'F') {
                    this.sp++;
                    next();
                }
            }
            if (z) {
                this.token = 3;
            } else {
                this.token = 2;
            }
        }
        this.sp = i + 1;
        next();
        z = z2;
        if (z) {
            this.token = 3;
        } else {
            this.token = 2;
        }
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0038  */
    /* JADX WARN: Code duplicated, block: B:33:0x0074  */
    /* JADX WARN: Code duplicated, block: B:35:0x0079 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:36:0x007a  */
    /* JADX WARN: Code duplicated, block: B:38:0x0084  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:26:0x005b -> B:12:0x0032). Please report as a decompilation issue!!! */
    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final long longValue() throws NumberFormatException {
        long j;
        long j2;
        int i;
        char cCharAt;
        boolean z = false;
        if (this.np == -1) {
            this.np = 0;
        }
        int i2 = this.np;
        int i3 = this.sp + i2;
        if (charAt(i2) == '-') {
            j = Long.MIN_VALUE;
            i2++;
            z = true;
        } else {
            j = -9223372036854775807L;
        }
        if (i2 >= i3) {
            j2 = 0;
            if (i2 < i3) {
                i = i2 + 1;
                cCharAt = charAt(i2);
                if (cCharAt != 'L' || cCharAt == 'S' || cCharAt == 'B') {
                    i2 = i;
                } else {
                    int i4 = cCharAt - '0';
                    if (j2 < MULTMIN_RADIX_TEN) {
                        throw new NumberFormatException(numberString());
                    }
                    long j3 = j2 * 10;
                    long j4 = i4;
                    if (j3 < j + j4) {
                        throw new NumberFormatException(numberString());
                    }
                    j2 = j3 - j4;
                }
            }
            if (z) {
                return -j2;
            }
            if (i2 > this.np + 1) {
                return j2;
            }
            throw new NumberFormatException(numberString());
        }
        i = i2 + 1;
        j2 = -(charAt(i2) - '0');
        i2 = i;
        if (i2 < i3) {
            i = i2 + 1;
            cCharAt = charAt(i2);
            if (cCharAt != 'L') {
            }
            i2 = i;
        }
        if (z) {
            return -j2;
        }
        if (i2 > this.np + 1) {
            return j2;
        }
        throw new NumberFormatException(numberString());
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public final Number decimalValue(boolean z) {
        char cCharAt = charAt((this.np + this.sp) - 1);
        try {
            if (cCharAt == 'F') {
                return Float.valueOf(Float.parseFloat(numberString()));
            }
            if (cCharAt == 'D') {
                return Double.valueOf(Double.parseDouble(numberString()));
            }
            if (z) {
                return decimalValue();
            }
            return Double.valueOf(doubleValue());
        } catch (NumberFormatException e) {
            throw new JSONException(e.getMessage() + ", " + info());
        }
    }

    public String[] scanFieldStringArray(char[] cArr, int i, SymbolTable symbolTable) {
        throw new UnsupportedOperationException();
    }

    public boolean matchField2(char[] cArr) {
        throw new UnsupportedOperationException();
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public int getFeatures() {
        return this.features;
    }

    @Override // com.alibaba.fastjson.parser.JSONLexer
    public void setFeatures(int i) {
        this.features = i;
    }
}
