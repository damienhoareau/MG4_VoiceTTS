package android.text.method;

import android.graphics.Paint;
import android.icu.lang.UCharacter;
import android.mtp.MtpConstants;
import android.text.Editable;
import android.text.Emoji;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BaseKeyListener extends MetaKeyKeyListener implements KeyListener {
    private static final int CARRIAGE_RETURN = 13;
    private static final int LINE_FEED = 10;
    static final Object OLD_SEL_START = new NoCopySpan.Concrete();
    static Paint sCachedPaint = null;
    private final Object mLock = new Object();

    public boolean backspace(View view, Editable editable, int i, KeyEvent keyEvent) {
        return backspaceOrForwardDelete(view, editable, i, keyEvent, false);
    }

    public boolean forwardDelete(View view, Editable editable, int i, KeyEvent keyEvent) {
        return backspaceOrForwardDelete(view, editable, i, keyEvent, true);
    }

    private static boolean isVariationSelector(int i) {
        return UCharacter.hasBinaryProperty(i, 36);
    }

    private static int adjustReplacementSpan(CharSequence charSequence, int i, boolean z) {
        if (!(charSequence instanceof Spanned)) {
            return i;
        }
        Spanned spanned = (Spanned) charSequence;
        ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i, i, ReplacementSpan.class);
        for (int i2 = 0; i2 < replacementSpanArr.length; i2++) {
            int spanStart = spanned.getSpanStart(replacementSpanArr[i2]);
            int spanEnd = spanned.getSpanEnd(replacementSpanArr[i2]);
            if (spanStart < i && spanEnd > i) {
                if (!z) {
                    spanStart = spanEnd;
                }
                i = spanStart;
            }
        }
        return i;
    }

    /* JADX WARN: Code duplicated, block: B:42:0x00af A[PHI: r4 r5
  0x00af: PHI (r4v7 int) = (r4v2 int), (r4v4 int), (r4v6 int), (r4v8 int) binds: [B:86:0x013c, B:41:0x00aa, B:31:0x008a, B:27:0x0070] A[DONT_GENERATE, DONT_INLINE]
  0x00af: PHI (r5v5 int) = (r5v1 int), (r5v1 int), (r5v1 int), (r5v8 int) binds: [B:86:0x013c, B:41:0x00aa, B:31:0x008a, B:27:0x0070] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:69:0x0110 A[PHI: r4
  0x0110: PHI (r4v12 int) = 
  (r4v2 int)
  (r4v1 int)
  (r4v3 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v1 int)
  (r4v13 int)
  (r4v15 int)
 binds: [B:89:0x0142, B:67:0x010c, B:68:0x010e, B:65:0x0104, B:59:0x00ed, B:56:0x00e1, B:49:0x00c7, B:44:0x00b6, B:46:0x00bc, B:37:0x009e, B:34:0x0092, B:26:0x006e, B:23:0x0062, B:19:0x0057, B:16:0x004d, B:17:0x0050] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:81:0x0130 A[PHI: r4
  0x0130: PHI (r4v5 int) = (r4v2 int), (r4v6 int) binds: [B:80:0x012e, B:31:0x008a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:92:0x0148 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:96:0x014a A[SYNTHETIC] */
    private static int getOffsetForBackspaceKey(CharSequence charSequence, int i) {
        int iCharCount;
        int iCharCount2;
        if (i <= 1) {
            return 0;
        }
        int iCharCount3 = i;
        int i2 = 0;
        int iCharCount4 = 0;
        int iCharCount5 = 0;
        do {
            int iCodePointBefore = Character.codePointBefore(charSequence, iCharCount3);
            iCharCount3 -= Character.charCount(iCodePointBefore);
            switch (i2) {
                case 0:
                    iCharCount4 = Character.charCount(iCodePointBefore);
                    if (iCodePointBefore == 10) {
                        i2 = 1;
                    } else if (isVariationSelector(iCodePointBefore)) {
                        i2 = 6;
                    } else if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                        i2 = 10;
                    } else if (Emoji.isEmojiModifier(iCodePointBefore)) {
                        i2 = 4;
                    } else if (iCodePointBefore == Emoji.COMBINING_ENCLOSING_KEYCAP) {
                        i2 = 2;
                    } else if (Emoji.isEmoji(iCodePointBefore)) {
                        i2 = 7;
                    } else if (iCodePointBefore == Emoji.CANCEL_TAG) {
                        i2 = 12;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 1:
                    if (iCodePointBefore == 13) {
                        iCharCount4++;
                    }
                    i2 = 13;
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 2:
                    if (isVariationSelector(iCodePointBefore)) {
                        iCharCount5 = Character.charCount(iCodePointBefore);
                        i2 = 3;
                    } else {
                        if (Emoji.isKeycapBase(iCodePointBefore)) {
                            iCharCount = Character.charCount(iCodePointBefore);
                            iCharCount4 += iCharCount;
                        }
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 3:
                    if (Emoji.isKeycapBase(iCodePointBefore)) {
                        iCharCount2 = Character.charCount(iCodePointBefore);
                        iCharCount = iCharCount2 + iCharCount5;
                        iCharCount4 += iCharCount;
                    }
                    i2 = 13;
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 4:
                    if (isVariationSelector(iCodePointBefore)) {
                        iCharCount5 = Character.charCount(iCodePointBefore);
                        i2 = 5;
                    } else {
                        if (Emoji.isEmojiModifierBase(iCodePointBefore)) {
                            iCharCount = Character.charCount(iCodePointBefore);
                            iCharCount4 += iCharCount;
                        }
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 5:
                    if (Emoji.isEmojiModifierBase(iCodePointBefore)) {
                        iCharCount2 = Character.charCount(iCodePointBefore);
                        iCharCount = iCharCount2 + iCharCount5;
                        iCharCount4 += iCharCount;
                    }
                    i2 = 13;
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 6:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount4 += Character.charCount(iCodePointBefore);
                        i2 = 7;
                        if (iCharCount3 > 0) {
                        }
                        return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                    }
                    if (!isVariationSelector(iCodePointBefore) && UCharacter.getCombiningClass(iCodePointBefore) == 0) {
                        iCharCount = Character.charCount(iCodePointBefore);
                        iCharCount4 += iCharCount;
                    }
                    i2 = 13;
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 7:
                    if (iCodePointBefore == Emoji.ZERO_WIDTH_JOINER) {
                        i2 = 8;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 8:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount4 += Character.charCount(iCodePointBefore) + 1;
                        if (Emoji.isEmojiModifier(iCodePointBefore)) {
                            i2 = 4;
                        } else {
                            i2 = 7;
                        }
                    } else if (isVariationSelector(iCodePointBefore)) {
                        iCharCount5 = Character.charCount(iCodePointBefore);
                        i2 = 9;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 9:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount4 += iCharCount5 + 1 + Character.charCount(iCodePointBefore);
                        iCharCount5 = 0;
                        i2 = 7;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 10:
                    if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                        iCharCount4 += 2;
                        i2 = 11;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 11:
                    if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                        iCharCount4 -= 2;
                        i2 = 10;
                    } else {
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                case 12:
                    if (Emoji.isTagSpecChar(iCodePointBefore)) {
                        iCharCount4 += 2;
                    } else {
                        if (Emoji.isEmoji(iCodePointBefore)) {
                            iCharCount = Character.charCount(iCodePointBefore);
                            iCharCount4 += iCharCount;
                        } else {
                            iCharCount4 = 2;
                        }
                        i2 = 13;
                    }
                    if (iCharCount3 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount4, true);
                default:
                    throw new IllegalArgumentException("state " + i2 + " is unknown");
            }
        } while (i2 != 13);
        return adjustReplacementSpan(charSequence, i - iCharCount4, true);
    }

    private static int getOffsetForForwardDeleteKey(CharSequence charSequence, int i, Paint paint) {
        int length = charSequence.length();
        return i >= length + (-1) ? length : adjustReplacementSpan(charSequence, paint.getTextRunCursor(charSequence, i, length, 0, i, 0), false);
    }

    private boolean backspaceOrForwardDelete(View view, Editable editable, int i, KeyEvent keyEvent, boolean z) {
        int offsetForBackspaceKey;
        Paint paint;
        Paint paint2;
        if (!KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState() & (-28916))) {
            return false;
        }
        if (deleteSelection(view, editable)) {
            return true;
        }
        boolean z2 = (keyEvent.getMetaState() & 4096) != 0;
        boolean z3 = getMetaState(editable, 1, keyEvent) == 1;
        boolean z4 = getMetaState(editable, 2, keyEvent) == 1;
        if (z2) {
            if (z4 || z3) {
                return false;
            }
            return deleteUntilWordBoundary(view, editable, z);
        }
        if (z4 && deleteLine(view, editable)) {
            return true;
        }
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (z) {
            if (view instanceof TextView) {
                paint2 = ((TextView) view).getPaint();
            } else {
                synchronized (this.mLock) {
                    if (sCachedPaint == null) {
                        sCachedPaint = new Paint();
                    }
                    paint = sCachedPaint;
                }
                paint2 = paint;
            }
            offsetForBackspaceKey = getOffsetForForwardDeleteKey(editable, selectionEnd, paint2);
        } else {
            offsetForBackspaceKey = getOffsetForBackspaceKey(editable, selectionEnd);
        }
        if (selectionEnd == offsetForBackspaceKey) {
            return false;
        }
        editable.delete(Math.min(selectionEnd, offsetForBackspaceKey), Math.max(selectionEnd, offsetForBackspaceKey));
        return true;
    }

    private boolean deleteUntilWordBoundary(View view, Editable editable, boolean z) {
        int length;
        int selectionStart = Selection.getSelectionStart(editable);
        if (selectionStart != Selection.getSelectionEnd(editable)) {
            return false;
        }
        if ((!z && selectionStart == 0) || (z && selectionStart == editable.length())) {
            return false;
        }
        WordIterator wordIterator = view instanceof TextView ? ((TextView) view).getWordIterator() : null;
        if (wordIterator == null) {
            wordIterator = new WordIterator();
        }
        if (z) {
            wordIterator.setCharSequence(editable, selectionStart, editable.length());
            int iFollowing = wordIterator.following(selectionStart);
            length = iFollowing == -1 ? editable.length() : iFollowing;
        } else {
            wordIterator.setCharSequence(editable, 0, selectionStart);
            int iPreceding = wordIterator.preceding(selectionStart);
            if (iPreceding == -1) {
                length = selectionStart;
                selectionStart = 0;
            } else {
                length = selectionStart;
                selectionStart = iPreceding;
            }
        }
        editable.delete(selectionStart, length);
        return true;
    }

    private boolean deleteSelection(View view, Editable editable) {
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionEnd < selectionStart) {
            selectionEnd = selectionStart;
            selectionStart = selectionEnd;
        }
        if (selectionStart == selectionEnd) {
            return false;
        }
        editable.delete(selectionStart, selectionEnd);
        return true;
    }

    private boolean deleteLine(View view, Editable editable) {
        Layout layout;
        int lineForOffset;
        int lineStart;
        int lineEnd;
        if (!(view instanceof TextView) || (layout = ((TextView) view).getLayout()) == null || (lineEnd = layout.getLineEnd(lineForOffset)) == (lineStart = layout.getLineStart((lineForOffset = layout.getLineForOffset(Selection.getSelectionStart(editable)))))) {
            return false;
        }
        editable.delete(lineStart, lineEnd);
        return true;
    }

    /* JADX INFO: renamed from: android.text.method.BaseKeyListener$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$text$method$TextKeyListener$Capitalize;

        static {
            int[] iArr = new int[TextKeyListener.Capitalize.values().length];
            $SwitchMap$android$text$method$TextKeyListener$Capitalize = iArr;
            try {
                iArr[TextKeyListener.Capitalize.CHARACTERS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$text$method$TextKeyListener$Capitalize[TextKeyListener.Capitalize.WORDS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$text$method$TextKeyListener$Capitalize[TextKeyListener.Capitalize.SENTENCES.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    static int makeTextContentType(TextKeyListener.Capitalize capitalize, boolean z) {
        int i = AnonymousClass1.$SwitchMap$android$text$method$TextKeyListener$Capitalize[capitalize.ordinal()];
        int i2 = 1;
        if (i == 1) {
            i2 = 4097;
        } else if (i == 2) {
            i2 = MtpConstants.RESPONSE_OK;
        } else if (i == 3) {
            i2 = 16385;
        }
        return z ? i2 | 32768 : i2;
    }

    @Override // android.text.method.MetaKeyKeyListener, android.text.method.KeyListener
    public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
        boolean zBackspace;
        if (i == 67) {
            zBackspace = backspace(view, editable, i, keyEvent);
        } else {
            zBackspace = i != 112 ? false : forwardDelete(view, editable, i, keyEvent);
        }
        if (zBackspace) {
            adjustMetaAfterKeypress(editable);
            return true;
        }
        return super.onKeyDown(view, editable, i, keyEvent);
    }

    @Override // android.text.method.KeyListener
    public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 2 || keyEvent.getKeyCode() != 0) {
            return false;
        }
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionEnd < selectionStart) {
            selectionEnd = selectionStart;
            selectionStart = selectionEnd;
        }
        String characters = keyEvent.getCharacters();
        if (characters == null) {
            return false;
        }
        editable.replace(selectionStart, selectionEnd, characters);
        return true;
    }
}
