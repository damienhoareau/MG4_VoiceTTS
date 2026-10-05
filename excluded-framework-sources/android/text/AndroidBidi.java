package android.text;

import android.icu.lang.UCharacter;
import android.icu.text.Bidi;
import android.icu.text.BidiClassifier;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidBidi {
    private static final EmojiBidiOverride sEmojiBidiOverride = new EmojiBidiOverride();

    public static class EmojiBidiOverride extends BidiClassifier {
        private static final int NO_OVERRIDE = UCharacter.getIntPropertyMaxValue(4096) + 1;

        public EmojiBidiOverride() {
            super(null);
        }

        @Override // android.icu.text.BidiClassifier
        public int classify(int i) {
            if (Emoji.isNewEmoji(i)) {
                return 10;
            }
            return NO_OVERRIDE;
        }
    }

    public static int bidi(int i, char[] cArr, byte[] bArr) {
        byte b;
        if (cArr == null || bArr == null) {
            throw null;
        }
        int length = cArr.length;
        if (bArr.length < length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == -2) {
            b = 127;
        } else if (i != -1) {
            b = (i == 1 || i != 2) ? (byte) 0 : (byte) 126;
        } else {
            b = 1;
        }
        Bidi bidi = new Bidi(length, 0);
        bidi.setCustomClassifier(sEmojiBidiOverride);
        bidi.setPara(cArr, b, (byte[]) null);
        for (int i2 = 0; i2 < length; i2++) {
            bArr[i2] = bidi.getLevelAt(i2);
        }
        return (bidi.getParaLevel() & 1) == 0 ? 1 : -1;
    }

    public static Layout.Directions directions(int i, byte[] bArr, int i2, char[] cArr, int i3, int i4) {
        int i5;
        if (i4 == 0) {
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        int i6 = i == 1 ? 0 : 1;
        int i7 = bArr[i2];
        int i8 = i2 + i4;
        int i9 = 1;
        int i10 = i7;
        for (int i11 = i2 + 1; i11 < i8; i11++) {
            int i12 = bArr[i11];
            if (i12 != i10) {
                i9++;
                i10 = i12;
            }
        }
        if ((i10 & 1) != (i6 & 1)) {
            int i13 = i4;
            while (true) {
                i13--;
                if (i13 < 0) {
                    break;
                }
                char c = cArr[i3 + i13];
                if (c == '\n') {
                    i13--;
                    break;
                }
                if (c != ' ' && c != '\t') {
                    break;
                }
            }
            i5 = i13 + 1;
            if (i5 != i4) {
                i9++;
            }
        } else {
            i5 = i4;
        }
        if (i9 == 1 && i7 == i6) {
            if ((i7 & 1) != 0) {
                return Layout.DIRS_ALL_RIGHT_TO_LEFT;
            }
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        int i14 = i9 * 2;
        int[] iArr = new int[i14];
        int i15 = i2 + i5;
        int i16 = i2;
        int i17 = i16;
        int i18 = 1;
        int i19 = i7;
        int i20 = i7 << 26;
        int i21 = i19;
        while (i16 < i15) {
            int i22 = bArr[i16];
            if (i22 != i7) {
                if (i22 > i19) {
                    i19 = i22;
                } else if (i22 < i21) {
                    i21 = i22;
                }
                int i23 = i18 + 1;
                iArr[i18] = i20 | (i16 - i17);
                i18 = i23 + 1;
                iArr[i23] = i16 - i2;
                i20 = i22 << 26;
                i17 = i16;
                i7 = i22;
            }
            i16++;
        }
        iArr[i18] = (i15 - i17) | i20;
        if (i5 < i4) {
            int i24 = i18 + 1;
            iArr[i24] = i5;
            iArr[i24 + 1] = (i4 - i5) | (i6 << 26);
        }
        if ((i21 & 1) != i6 ? i9 > 1 : i19 > (i21 = i21 + 1)) {
            for (int i25 = i19 - 1; i25 >= i21; i25--) {
                int i26 = 0;
                while (i26 < i14) {
                    if (bArr[iArr[i26]] >= i25) {
                        int i27 = i26 + 2;
                        while (i27 < i14 && bArr[iArr[i27]] >= i25) {
                            i27 += 2;
                        }
                        for (int i28 = i27 - 2; i26 < i28; i28 -= 2) {
                            int i29 = iArr[i26];
                            iArr[i26] = iArr[i28];
                            iArr[i28] = i29;
                            int i30 = i26 + 1;
                            int i31 = iArr[i30];
                            int i32 = i28 + 1;
                            iArr[i30] = iArr[i32];
                            iArr[i32] = i31;
                            i26 += 2;
                        }
                        i26 = i27 + 2;
                    }
                    i26 += 2;
                }
            }
        }
        return new Layout.Directions(iArr);
    }
}
