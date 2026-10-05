package android.text.format;

import android.content.res.Resources;
import com.android.internal.R;
import java.nio.CharBuffer;
import java.util.Locale;
import libcore.icu.LocaleData;
import libcore.util.ZoneInfo;

/* JADX INFO: loaded from: classes2.dex */
class TimeFormatter {
    private static final int DAYSPERLYEAR = 366;
    private static final int DAYSPERNYEAR = 365;
    private static final int DAYSPERWEEK = 7;
    private static final int FORCE_LOWER_CASE = -1;
    private static final int HOURSPERDAY = 24;
    private static final int MINSPERHOUR = 60;
    private static final int MONSPERYEAR = 12;
    private static final int SECSPERMIN = 60;
    private static String sDateOnlyFormat;
    private static String sDateTimeFormat;
    private static Locale sLocale;
    private static LocaleData sLocaleData;
    private static String sTimeOnlyFormat;
    private final String dateOnlyFormat;
    private final String dateTimeFormat;
    private final LocaleData localeData;
    private java.util.Formatter numberFormatter;
    private StringBuilder outputBuilder;
    private final String timeOnlyFormat;

    private static boolean brokenIsLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean brokenIsUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static char brokenToLower(char c) {
        return (c < 'A' || c > 'Z') ? c : (char) ((c - DateFormat.CAPITAL_AM_PM) + 97);
    }

    private static char brokenToUpper(char c) {
        return (c < 'a' || c > 'z') ? c : (char) ((c - DateFormat.AM_PM) + 65);
    }

    private static String getFormat(int i, String str, String str2, String str3, String str4) {
        if (i == 45) {
            return str3;
        }
        if (i != 48) {
            return i != 95 ? str : str2;
        }
        return str4;
    }

    public TimeFormatter() {
        synchronized (TimeFormatter.class) {
            Locale locale = Locale.getDefault();
            if (sLocale == null || !locale.equals(sLocale)) {
                sLocale = locale;
                sLocaleData = LocaleData.get(locale);
                Resources system = Resources.getSystem();
                sTimeOnlyFormat = system.getString(R.string.time_of_day);
                sDateOnlyFormat = system.getString(R.string.month_day_year);
                sDateTimeFormat = system.getString(R.string.date_and_time);
            }
            this.dateTimeFormat = sDateTimeFormat;
            this.timeOnlyFormat = sTimeOnlyFormat;
            this.dateOnlyFormat = sDateOnlyFormat;
            this.localeData = sLocaleData;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public String format(String str, ZoneInfo.WallTime wallTime, ZoneInfo zoneInfo) {
        try {
            StringBuilder sb = new StringBuilder();
            this.outputBuilder = sb;
            this.numberFormatter = new java.util.Formatter(sb, Locale.US);
            formatInternal(str, wallTime, zoneInfo);
            String string = sb.toString();
            if (this.localeData.zeroDigit != '0') {
                string = localizeDigits(string);
            }
            return string;
        } finally {
            this.outputBuilder = null;
            this.numberFormatter = null;
        }
    }

    private String localizeDigits(String str) {
        int length = str.length();
        int i = this.localeData.zeroDigit - '0';
        StringBuilder sb = new StringBuilder(length);
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt >= '0' && cCharAt <= '9') {
                cCharAt = (char) (cCharAt + i);
            }
            sb.append(cCharAt);
        }
        return sb.toString();
    }

    private void formatInternal(String str, ZoneInfo.WallTime wallTime, ZoneInfo zoneInfo) {
        CharBuffer charBufferWrap = CharBuffer.wrap(str);
        while (charBufferWrap.remaining() > 0) {
            if (charBufferWrap.get(charBufferWrap.position()) == '%' ? handleToken(charBufferWrap, wallTime, zoneInfo) : true) {
                this.outputBuilder.append(charBufferWrap.get(charBufferWrap.position()));
            }
            charBufferWrap.position(charBufferWrap.position() + 1);
        }
    }

    private boolean handleToken(CharBuffer charBuffer, ZoneInfo.WallTime wallTime, ZoneInfo zoneInfo) {
        int i;
        char c;
        char c2 = 0;
        while (true) {
            if (charBuffer.remaining() <= 1) {
                return true;
            }
            charBuffer.position(charBuffer.position() + 1);
            char c3 = charBuffer.get(charBuffer.position());
            if (c3 != '#') {
                if (c3 == '+') {
                    formatInternal("%a %b %e %H:%M:%S %Z %Y", wallTime, zoneInfo);
                    return false;
                }
                if (c3 != '-' && c3 != '0') {
                    if (c3 == 'M') {
                        this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getMinute()));
                        return false;
                    }
                    if (c3 == 'p') {
                        modifyAndAppend(wallTime.getHour() >= 12 ? this.localeData.amPm[1] : this.localeData.amPm[0], c2);
                        return false;
                    }
                    if (c3 == 'O') {
                        continue;
                    } else {
                        if (c3 == 'P') {
                            modifyAndAppend(wallTime.getHour() >= 12 ? this.localeData.amPm[1] : this.localeData.amPm[0], -1);
                            return false;
                        }
                        if (c3 != '^' && c3 != '_') {
                            if (c3 != 'g') {
                                String str = "?";
                                if (c3 != 'h') {
                                    switch (c3) {
                                        case 'A':
                                            if (wallTime.getWeekDay() >= 0 && wallTime.getWeekDay() < 7) {
                                                str = this.localeData.longWeekdayNames[wallTime.getWeekDay() + 1];
                                            }
                                            modifyAndAppend(str, c2);
                                            return false;
                                        case 'B':
                                            if (c2 == '-') {
                                                if (wallTime.getMonth() >= 0 && wallTime.getMonth() < 12) {
                                                    str = this.localeData.longStandAloneMonthNames[wallTime.getMonth()];
                                                }
                                                modifyAndAppend(str, c2);
                                            } else {
                                                if (wallTime.getMonth() >= 0 && wallTime.getMonth() < 12) {
                                                    str = this.localeData.longMonthNames[wallTime.getMonth()];
                                                }
                                                modifyAndAppend(str, c2);
                                            }
                                            return false;
                                        case 'C':
                                            outputYear(wallTime.getYear(), true, false, c2);
                                            return false;
                                        case 'D':
                                            formatInternal("%m/%d/%y", wallTime, zoneInfo);
                                            return false;
                                        case 'E':
                                            break;
                                        case 'F':
                                            formatInternal("%Y-%m-%d", wallTime, zoneInfo);
                                            return false;
                                        case 'G':
                                            break;
                                        case 'H':
                                            this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getHour()));
                                            return false;
                                        case 'I':
                                            this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getHour() % 12 != 0 ? wallTime.getHour() % 12 : 12));
                                            return false;
                                        default:
                                            switch (c3) {
                                                case 'R':
                                                    formatInternal(DateUtils.HOUR_MINUTE_24, wallTime, zoneInfo);
                                                    return false;
                                                case 'S':
                                                    this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getSecond()));
                                                    return false;
                                                case 'T':
                                                    formatInternal("%H:%M:%S", wallTime, zoneInfo);
                                                    return false;
                                                case 'U':
                                                    this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(((wallTime.getYearDay() + 7) - wallTime.getWeekDay()) / 7));
                                                    return false;
                                                case 'V':
                                                    break;
                                                case 'W':
                                                    this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(((wallTime.getYearDay() + 7) - (wallTime.getWeekDay() != 0 ? wallTime.getWeekDay() - 1 : 6)) / 7));
                                                    return false;
                                                case 'X':
                                                    formatInternal(this.timeOnlyFormat, wallTime, zoneInfo);
                                                    return false;
                                                case 'Y':
                                                    outputYear(wallTime.getYear(), true, true, c2);
                                                    return false;
                                                case 'Z':
                                                    if (wallTime.getIsDst() < 0) {
                                                        return false;
                                                    }
                                                    modifyAndAppend(zoneInfo.getDisplayName(wallTime.getIsDst() != 0, 0), c2);
                                                    return false;
                                                default:
                                                    switch (c3) {
                                                        case 'a':
                                                            if (wallTime.getWeekDay() >= 0 && wallTime.getWeekDay() < 7) {
                                                                str = this.localeData.shortWeekdayNames[wallTime.getWeekDay() + 1];
                                                            }
                                                            modifyAndAppend(str, c2);
                                                            return false;
                                                        case 'b':
                                                            break;
                                                        case 'c':
                                                            formatInternal(this.dateTimeFormat, wallTime, zoneInfo);
                                                            return false;
                                                        case 'd':
                                                            this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getMonthDay()));
                                                            return false;
                                                        case 'e':
                                                            this.numberFormatter.format(getFormat(c2, "%2d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getMonthDay()));
                                                            return false;
                                                        default:
                                                            switch (c3) {
                                                                case 'j':
                                                                    this.numberFormatter.format(getFormat(c2, "%03d", "%3d", "%d", "%03d"), Integer.valueOf(wallTime.getYearDay() + 1));
                                                                    return false;
                                                                case 'k':
                                                                    this.numberFormatter.format(getFormat(c2, "%2d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getHour()));
                                                                    return false;
                                                                case 'l':
                                                                    this.numberFormatter.format(getFormat(c2, "%2d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getHour() % 12 != 0 ? wallTime.getHour() % 12 : 12));
                                                                    return false;
                                                                case 'm':
                                                                    this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(wallTime.getMonth() + 1));
                                                                    return false;
                                                                case 'n':
                                                                    this.outputBuilder.append('\n');
                                                                    return false;
                                                                default:
                                                                    switch (c3) {
                                                                        case 'r':
                                                                            formatInternal("%I:%M:%S %p", wallTime, zoneInfo);
                                                                            return false;
                                                                        case 's':
                                                                            this.outputBuilder.append(Integer.toString(wallTime.mktime(zoneInfo)));
                                                                            return false;
                                                                        case 't':
                                                                            this.outputBuilder.append('\t');
                                                                            return false;
                                                                        case 'u':
                                                                            this.numberFormatter.format("%d", Integer.valueOf(wallTime.getWeekDay() != 0 ? wallTime.getWeekDay() : 7));
                                                                            return false;
                                                                        case 'v':
                                                                            formatInternal("%e-%b-%Y", wallTime, zoneInfo);
                                                                            return false;
                                                                        case 'w':
                                                                            this.numberFormatter.format("%d", Integer.valueOf(wallTime.getWeekDay()));
                                                                            return false;
                                                                        case 'x':
                                                                            formatInternal(this.dateOnlyFormat, wallTime, zoneInfo);
                                                                            return false;
                                                                        case 'y':
                                                                            outputYear(wallTime.getYear(), false, true, c2);
                                                                            return false;
                                                                        case 'z':
                                                                            if (wallTime.getIsDst() < 0) {
                                                                                return false;
                                                                            }
                                                                            int gmtOffset = wallTime.getGmtOffset();
                                                                            if (gmtOffset < 0) {
                                                                                gmtOffset = -gmtOffset;
                                                                                c = '-';
                                                                            } else {
                                                                                c = '+';
                                                                            }
                                                                            this.outputBuilder.append(c);
                                                                            int i2 = gmtOffset / 60;
                                                                            this.numberFormatter.format(getFormat(c2, "%04d", "%4d", "%d", "%04d"), Integer.valueOf(((i2 / 60) * 100) + (i2 % 60)));
                                                                            return false;
                                                                        default:
                                                                            return true;
                                                                    }
                                                            }
                                                    }
                                                    break;
                                            }
                                            break;
                                    }
                                }
                                if (wallTime.getMonth() >= 0 && wallTime.getMonth() < 12) {
                                    str = this.localeData.shortMonthNames[wallTime.getMonth()];
                                }
                                modifyAndAppend(str, c2);
                                return false;
                            }
                            int year = wallTime.getYear();
                            int yearDay = wallTime.getYearDay();
                            int weekDay = wallTime.getWeekDay();
                            while (true) {
                                int i3 = isLeap(year) ? 366 : 365;
                                int i4 = (((yearDay + 11) - weekDay) % 7) - 3;
                                int i5 = i4 - (i3 % 7);
                                if (i5 < -3) {
                                    i5 += 7;
                                }
                                if (yearDay >= i5 + i3) {
                                    year++;
                                    i = 1;
                                } else if (yearDay >= i4) {
                                    i = ((yearDay - i4) / 7) + 1;
                                } else {
                                    year--;
                                    yearDay += isLeap(year) ? 366 : 365;
                                }
                            }
                            if (c3 == 'V') {
                                this.numberFormatter.format(getFormat(c2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(i));
                            } else if (c3 == 'g') {
                                outputYear(year, false, true, c2);
                            } else {
                                outputYear(year, true, true, c2);
                            }
                            return false;
                        }
                    }
                }
            }
            c2 = c3;
        }
    }

    private void modifyAndAppend(CharSequence charSequence, int i) {
        int i2 = 0;
        if (i == -1) {
            while (i2 < charSequence.length()) {
                this.outputBuilder.append(brokenToLower(charSequence.charAt(i2)));
                i2++;
            }
            return;
        }
        if (i != 35) {
            if (i == 94) {
                while (i2 < charSequence.length()) {
                    this.outputBuilder.append(brokenToUpper(charSequence.charAt(i2)));
                    i2++;
                }
                return;
            }
            this.outputBuilder.append(charSequence);
            return;
        }
        while (i2 < charSequence.length()) {
            char cCharAt = charSequence.charAt(i2);
            if (brokenIsUpper(cCharAt)) {
                cCharAt = brokenToLower(cCharAt);
            } else if (brokenIsLower(cCharAt)) {
                cCharAt = brokenToUpper(cCharAt);
            }
            this.outputBuilder.append(cCharAt);
            i2++;
        }
    }

    private void outputYear(int i, boolean z, boolean z2, int i2) {
        int i3 = i % 100;
        int i4 = (i / 100) + (i3 / 100);
        int i5 = i3 % 100;
        if (i5 < 0 && i4 > 0) {
            i5 += 100;
            i4--;
        } else if (i4 < 0 && i5 > 0) {
            i5 -= 100;
            i4++;
        }
        if (z) {
            if (i4 == 0 && i5 < 0) {
                this.outputBuilder.append("-0");
            } else {
                this.numberFormatter.format(getFormat(i2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(i4));
            }
        }
        if (z2) {
            if (i5 < 0) {
                i5 = -i5;
            }
            this.numberFormatter.format(getFormat(i2, "%02d", "%2d", "%d", "%02d"), Integer.valueOf(i5));
        }
    }

    private static boolean isLeap(int i) {
        return i % 4 == 0 && (i % 100 != 0 || i % 400 == 0);
    }
}
