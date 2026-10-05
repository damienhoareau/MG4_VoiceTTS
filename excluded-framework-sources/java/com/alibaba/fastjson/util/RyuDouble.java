package com.alibaba.fastjson.util;

import android.app.job.JobInfo;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.util.TimeUtils;
import java.lang.reflect.Array;
import java.math.BigInteger;

/* JADX INFO: loaded from: classes3.dex */
public final class RyuDouble {
    private static final int[][] POW5_SPLIT = (int[][]) Array.newInstance((Class<?>) int.class, 326, 4);
    private static final int[][] POW5_INV_SPLIT = (int[][]) Array.newInstance((Class<?>) int.class, 291, 4);

    static {
        BigInteger bigIntegerSubtract = BigInteger.ONE.shiftLeft(31).subtract(BigInteger.ONE);
        BigInteger bigIntegerSubtract2 = BigInteger.ONE.shiftLeft(31).subtract(BigInteger.ONE);
        int i = 0;
        while (i < 326) {
            BigInteger bigIntegerPow = BigInteger.valueOf(5L).pow(i);
            int iBitLength = bigIntegerPow.bitLength();
            int i2 = i == 0 ? 1 : (int) ((((((long) i) * 23219280) + 10000000) - 1) / 10000000);
            if (i2 != iBitLength) {
                throw new IllegalStateException(iBitLength + " != " + i2);
            }
            if (i < POW5_SPLIT.length) {
                for (int i3 = 0; i3 < 4; i3++) {
                    POW5_SPLIT[i][i3] = bigIntegerPow.shiftRight((iBitLength - 121) + ((3 - i3) * 31)).and(bigIntegerSubtract).intValue();
                }
            }
            if (i < POW5_INV_SPLIT.length) {
                BigInteger bigIntegerAdd = BigInteger.ONE.shiftLeft(iBitLength + 121).divide(bigIntegerPow).add(BigInteger.ONE);
                for (int i4 = 0; i4 < 4; i4++) {
                    if (i4 == 0) {
                        POW5_INV_SPLIT[i][i4] = bigIntegerAdd.shiftRight((3 - i4) * 31).intValue();
                    } else {
                        POW5_INV_SPLIT[i][i4] = bigIntegerAdd.shiftRight((3 - i4) * 31).and(bigIntegerSubtract2).intValue();
                    }
                }
            }
            i++;
        }
    }

    public static String toString(double d) {
        char[] cArr = new char[24];
        return new String(cArr, 0, toString(d, cArr, 0));
    }

    public static int toString(double d, char[] cArr, int i) {
        int i2;
        boolean z;
        boolean z2;
        long j;
        int i3;
        long j2;
        int i4;
        int i5;
        int i6;
        long j3;
        long j4;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        if (!Double.isNaN(d)) {
            if (d == Double.POSITIVE_INFINITY) {
                int i17 = i + 1;
                cArr[i] = 'I';
                int i18 = i17 + 1;
                cArr[i17] = 'n';
                int i19 = i18 + 1;
                cArr[i18] = 'f';
                int i20 = i19 + 1;
                cArr[i19] = 'i';
                int i21 = i20 + 1;
                cArr[i20] = 'n';
                int i22 = i21 + 1;
                cArr[i21] = 'i';
                int i23 = i22 + 1;
                cArr[i22] = 't';
                i11 = i23 + 1;
                cArr[i23] = 'y';
            } else if (d == Double.NEGATIVE_INFINITY) {
                int i24 = i + 1;
                cArr[i] = '-';
                int i25 = i24 + 1;
                cArr[i24] = 'I';
                int i26 = i25 + 1;
                cArr[i25] = 'n';
                int i27 = i26 + 1;
                cArr[i26] = 'f';
                int i28 = i27 + 1;
                cArr[i27] = 'i';
                int i29 = i28 + 1;
                cArr[i28] = 'n';
                int i30 = i29 + 1;
                cArr[i29] = 'i';
                int i31 = i30 + 1;
                cArr[i30] = 't';
                i16 = i31 + 1;
                cArr[i31] = 'y';
            } else {
                long jDoubleToLongBits = Double.doubleToLongBits(d);
                if (jDoubleToLongBits == 0) {
                    int i32 = i + 1;
                    cArr[i] = '0';
                    int i33 = i32 + 1;
                    cArr[i32] = '.';
                    i16 = i33 + 1;
                    cArr[i33] = '0';
                } else if (jDoubleToLongBits == Long.MIN_VALUE) {
                    int i34 = i + 1;
                    cArr[i] = '-';
                    int i35 = i34 + 1;
                    cArr[i34] = '0';
                    int i36 = i35 + 1;
                    cArr[i35] = '.';
                    i11 = i36 + 1;
                    cArr[i36] = '0';
                } else {
                    int i37 = (int) ((jDoubleToLongBits >>> 52) & 2047);
                    long j5 = jDoubleToLongBits & 4503599627370495L;
                    if (i37 == 0) {
                        i2 = -1074;
                    } else {
                        i2 = (i37 - 1023) - 52;
                        j5 |= 4503599627370496L;
                    }
                    boolean z3 = jDoubleToLongBits < 0;
                    boolean z4 = (j5 & 1) == 0;
                    long j6 = 4 * j5;
                    long j7 = j6 + 2;
                    int i38 = (j5 != 4503599627370496L || i37 <= 1) ? 1 : 0;
                    long j8 = (j6 - 1) - ((long) i38);
                    int i39 = i2 - 2;
                    int i40 = 3;
                    if (i39 >= 0) {
                        int iMax = Math.max(0, ((int) ((((long) i39) * 3010299) / 10000000)) - 1);
                        int i41 = ((((-i39) + iMax) + (((iMax == 0 ? 1 : (int) ((((((long) iMax) * 23219280) + 10000000) - 1) / 10000000)) + 122) - 1)) - 93) - 21;
                        if (i41 < 0) {
                            throw new IllegalArgumentException("" + i41);
                        }
                        int[] iArr = POW5_INV_SPLIT[iMax];
                        long j9 = j6 >>> 31;
                        long j10 = j6 & 2147483647L;
                        long j11 = ((long) iArr[0]) * j9;
                        z = z3;
                        z2 = z4;
                        long j12 = ((((((((((((j10 * ((long) iArr[3])) >>> 31) + (((long) iArr[2]) * j10)) + (j9 * ((long) iArr[3]))) >>> 31) + (((long) iArr[1]) * j10)) + (((long) iArr[2]) * j9)) >>> 31) + (((long) iArr[0]) * j10)) + (((long) iArr[1]) * j9)) >>> 21) + (j11 << 10)) >>> i41;
                        long j13 = j7 >>> 31;
                        long j14 = j7 & 2147483647L;
                        long j15 = ((long) iArr[0]) * j13;
                        long j16 = ((((((((((((j14 * ((long) iArr[3])) >>> 31) + (((long) iArr[2]) * j14)) + (j13 * ((long) iArr[3]))) >>> 31) + (((long) iArr[1]) * j14)) + (((long) iArr[2]) * j13)) >>> 31) + (((long) iArr[0]) * j14)) + (((long) iArr[1]) * j13)) >>> 21) + (j15 << 10)) >>> i41;
                        long j17 = j8 >>> 31;
                        long j18 = j8 & 2147483647L;
                        long j19 = ((long) iArr[0]) * j17;
                        j3 = j16;
                        j2 = ((((((((((((j18 * ((long) iArr[3])) >>> 31) + (((long) iArr[2]) * j18)) + (j17 * ((long) iArr[3]))) >>> 31) + (((long) iArr[1]) * j18)) + (((long) iArr[2]) * j17)) >>> 31) + (((long) iArr[0]) * j18)) + (((long) iArr[1]) * j17)) >>> 21) + (j19 << 10)) >>> i41;
                        if (iMax <= 21) {
                            long j20 = j6 % 5;
                            if (j20 == 0) {
                                if (j20 != 0) {
                                    i15 = 0;
                                } else if (j6 % 25 != 0) {
                                    i15 = 1;
                                } else if (j6 % 125 != 0) {
                                    i15 = 2;
                                } else if (j6 % 625 != 0) {
                                    i15 = 3;
                                } else {
                                    long j21 = j6 / 625;
                                    i15 = 4;
                                    for (long j22 = 0; j21 > j22 && j21 % 5 == j22; j22 = 0) {
                                        j21 /= 5;
                                        i15++;
                                    }
                                }
                                i5 = i15 >= iMax ? 1 : 0;
                                i12 = 0;
                            } else {
                                if (z2) {
                                    if (j8 % 5 != 0) {
                                        i14 = 0;
                                    } else if (j8 % 25 != 0) {
                                        i14 = 1;
                                    } else if (j8 % 125 != 0) {
                                        i14 = 2;
                                    } else if (j8 % 625 != 0) {
                                        i14 = 3;
                                    } else {
                                        long j23 = j8 / 625;
                                        i14 = 4;
                                        for (long j24 = 0; j23 > j24 && j23 % 5 == j24; j24 = 0) {
                                            j23 /= 5;
                                            i14++;
                                        }
                                    }
                                    i12 = i14 >= iMax ? 1 : 0;
                                    i5 = 0;
                                } else {
                                    if (j7 % 5 != 0) {
                                        i13 = 0;
                                    } else if (j7 % 25 != 0) {
                                        i13 = 1;
                                    } else if (j7 % 125 != 0) {
                                        i13 = 2;
                                    } else if (j7 % 625 != 0) {
                                        i13 = 3;
                                    } else {
                                        long j25 = j7 / 625;
                                        i13 = 4;
                                        for (long j26 = 0; j25 > j26 && j25 % 5 == j26; j26 = 0) {
                                            j25 /= 5;
                                            i13++;
                                        }
                                    }
                                    if (i13 >= iMax) {
                                        j3--;
                                    }
                                }
                                i5 = 0;
                            }
                        } else {
                            i5 = 0;
                        }
                        i6 = i12;
                        j = j12;
                        i4 = iMax;
                        i3 = 0;
                    } else {
                        z = z3;
                        z2 = z4;
                        int i42 = -i39;
                        int iMax2 = Math.max(0, ((int) ((((long) i42) * 6989700) / 10000000)) - 1);
                        int i43 = i42 - iMax2;
                        int i44 = ((iMax2 - ((i43 == 0 ? 1 : (int) ((((((long) i43) * 23219280) + 10000000) - 1) / 10000000)) - 121)) - 93) - 21;
                        if (i44 < 0) {
                            throw new IllegalArgumentException("" + i44);
                        }
                        int[] iArr2 = POW5_SPLIT[i43];
                        long j27 = j6 >>> 31;
                        long j28 = j6 & 2147483647L;
                        long j29 = ((long) iArr2[0]) * j27;
                        int i45 = i38;
                        long j30 = ((((((((((((j28 * ((long) iArr2[3])) >>> 31) + (((long) iArr2[2]) * j28)) + (j27 * ((long) iArr2[3]))) >>> 31) + (((long) iArr2[1]) * j28)) + (((long) iArr2[2]) * j27)) >>> 31) + (((long) iArr2[0]) * j28)) + (((long) iArr2[1]) * j27)) >>> 21) + (j29 << 10)) >>> i44;
                        long j31 = j7 >>> 31;
                        long j32 = j7 & 2147483647L;
                        long j33 = ((long) iArr2[0]) * j31;
                        j = j30;
                        long j34 = ((((((((((((j32 * ((long) iArr2[3])) >>> 31) + (((long) iArr2[2]) * j32)) + (j31 * ((long) iArr2[3]))) >>> 31) + (((long) iArr2[1]) * j32)) + (((long) iArr2[2]) * j31)) >>> 31) + (((long) iArr2[0]) * j32)) + (((long) iArr2[1]) * j31)) >>> 21) + (j33 << 10)) >>> i44;
                        long j35 = j8 >>> 31;
                        long j36 = j8 & 2147483647L;
                        i3 = 0;
                        long j37 = ((long) iArr2[0]) * j35;
                        j2 = ((((((((((((j36 * ((long) iArr2[3])) >>> 31) + (((long) iArr2[2]) * j36)) + (j35 * ((long) iArr2[3]))) >>> 31) + (((long) iArr2[1]) * j36)) + (((long) iArr2[2]) * j35)) >>> 31) + (((long) iArr2[0]) * j36)) + (((long) iArr2[1]) * j35)) >>> 21) + (j37 << 10)) >>> i44;
                        i4 = iMax2 + i39;
                        i5 = 1;
                        if (iMax2 > 1) {
                            if (iMax2 < 63) {
                                i5 = (j6 & ((1 << (iMax2 - 1)) - 1)) == 0 ? 1 : 0;
                                i6 = 0;
                            } else {
                                i5 = 0;
                                i6 = 0;
                            }
                            j3 = j34;
                        } else if (z2) {
                            j3 = j34;
                            i6 = i45 == 1 ? 1 : 0;
                        } else {
                            j3 = j34 - 1;
                            i6 = 0;
                        }
                    }
                    if (j3 >= 1000000000000000000L) {
                        i40 = 19;
                    } else if (j3 >= 100000000000000000L) {
                        i40 = 18;
                    } else if (j3 >= 10000000000000000L) {
                        i40 = 17;
                    } else if (j3 >= 1000000000000000L) {
                        i40 = 16;
                    } else if (j3 >= 100000000000000L) {
                        i40 = 15;
                    } else if (j3 >= 10000000000000L) {
                        i40 = 14;
                    } else if (j3 >= 1000000000000L) {
                        i40 = 13;
                    } else if (j3 >= 100000000000L) {
                        i40 = 12;
                    } else if (j3 >= 10000000000L) {
                        i40 = 11;
                    } else if (j3 >= 1000000000) {
                        i40 = 10;
                    } else if (j3 >= 100000000) {
                        i40 = 9;
                    } else if (j3 >= 10000000) {
                        i40 = 8;
                    } else if (j3 >= TimeUtils.NANOS_PER_MS) {
                        i40 = 7;
                    } else if (j3 >= 100000) {
                        i40 = 6;
                    } else if (j3 >= JobInfo.MIN_BACKOFF_MILLIS) {
                        i40 = 5;
                    } else if (j3 >= 1000) {
                        i40 = 4;
                    } else if (j3 < 100) {
                        i40 = j3 >= 10 ? 2 : 1;
                    }
                    int i46 = (i4 + i40) - 1;
                    int i47 = (i46 < -3 || i46 >= 7) ? 1 : i3;
                    if (i6 == 0 && i5 == 0) {
                        int i48 = i3;
                        i7 = i48;
                        while (true) {
                            long j38 = j3 / 10;
                            long j39 = j2 / 10;
                            if (j38 <= j39 || (j3 < 100 && i47 != 0)) {
                                break;
                            }
                            i48 = (int) (j % 10);
                            j /= 10;
                            i7++;
                            j3 = j38;
                            j2 = j39;
                        }
                        j4 = j + ((long) ((j == j2 || i48 >= 5) ? 1 : i3));
                    } else {
                        int i49 = i3;
                        int i50 = i49;
                        while (true) {
                            long j40 = j3 / 10;
                            long j41 = j2 / 10;
                            if (j40 <= j41 || (j3 < 100 && i47 != 0)) {
                                break;
                            }
                            i6 &= j2 % 10 == 0 ? 1 : i3;
                            i5 &= i49 == 0 ? 1 : i3;
                            i49 = (int) (j % 10);
                            j /= 10;
                            i50++;
                            j3 = j40;
                            j2 = j41;
                        }
                        if (i6 != 0 && z2) {
                            while (j2 % 10 == 0 && (j3 >= 100 || i47 == 0)) {
                                i5 &= i49 == 0 ? 1 : i3;
                                i49 = (int) (j % 10);
                                j3 /= 10;
                                j /= 10;
                                j2 /= 10;
                                i50++;
                            }
                        }
                        if (i5 != 0 && i49 == 5 && j % 2 == 0) {
                            i49 = 4;
                        }
                        j4 = j + ((long) (((j != j2 || (i6 != 0 && z2)) && i49 < 5) ? i3 : 1));
                        i7 = i50;
                    }
                    int i51 = i40 - i7;
                    if (z) {
                        i8 = i + 1;
                        cArr[i] = '-';
                    } else {
                        i8 = i;
                    }
                    if (i47 == 0) {
                        char c = '0';
                        if (i46 < 0) {
                            int i52 = i8 + 1;
                            cArr[i8] = '0';
                            int i53 = i52 + 1;
                            cArr[i52] = '.';
                            int i54 = -1;
                            while (i54 > i46) {
                                cArr[i53] = c;
                                i54--;
                                i53++;
                                c = '0';
                            }
                            i9 = i53;
                            while (i3 < i51) {
                                cArr[((i53 + i51) - i3) - 1] = (char) ((j4 % 10) + 48);
                                j4 /= 10;
                                i9++;
                                i3++;
                            }
                        } else {
                            int i55 = i46 + 1;
                            if (i55 >= i51) {
                                while (i3 < i51) {
                                    cArr[((i8 + i51) - i3) - 1] = (char) ((j4 % 10) + 48);
                                    j4 /= 10;
                                    i3++;
                                }
                                int i56 = i8 + i51;
                                while (i51 < i55) {
                                    cArr[i56] = '0';
                                    i51++;
                                    i56++;
                                }
                                int i57 = i56 + 1;
                                cArr[i56] = '.';
                                cArr[i57] = '0';
                                i9 = i57 + 1;
                            } else {
                                int i58 = i8 + 1;
                                while (i3 < i51) {
                                    if ((i51 - i3) - 1 == i46) {
                                        cArr[((i58 + i51) - i3) - 1] = '.';
                                        i58--;
                                    }
                                    cArr[((i58 + i51) - i3) - 1] = (char) ((j4 % 10) + 48);
                                    j4 /= 10;
                                    i3++;
                                }
                                i9 = i8 + i51 + 1;
                            }
                        }
                        return i9 - i;
                    }
                    while (i3 < i51 - 1) {
                        int i59 = (int) (j4 % 10);
                        j4 /= 10;
                        cArr[(i8 + i51) - i3] = (char) (i59 + 48);
                        i3++;
                    }
                    cArr[i8] = (char) ((j4 % 10) + 48);
                    cArr[i8 + 1] = '.';
                    int i60 = i8 + i51 + 1;
                    if (i51 == 1) {
                        cArr[i60] = '0';
                        i60++;
                    }
                    int i61 = i60 + 1;
                    cArr[i60] = DateFormat.DAY;
                    if (i46 < 0) {
                        cArr[i61] = '-';
                        i46 = -i46;
                        i61++;
                    }
                    if (i46 >= 100) {
                        int i62 = i61 + 1;
                        i10 = 48;
                        cArr[i61] = (char) ((i46 / 100) + 48);
                        i46 %= 100;
                        i61 = i62 + 1;
                        cArr[i62] = (char) ((i46 / 10) + 48);
                    } else {
                        i10 = 48;
                        if (i46 >= 10) {
                            cArr[i61] = (char) ((i46 / 10) + 48);
                            i61++;
                        }
                    }
                    i11 = i61 + 1;
                    cArr[i61] = (char) ((i46 % 10) + i10);
                }
            }
            return i11 - i;
        }
        int i63 = i + 1;
        cArr[i] = PhoneNumberUtils.WILD;
        int i64 = i63 + 1;
        cArr[i63] = DateFormat.AM_PM;
        i16 = i64 + 1;
        cArr[i64] = PhoneNumberUtils.WILD;
        return i16 - i;
    }
}
