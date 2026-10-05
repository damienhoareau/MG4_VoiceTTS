package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.WordIterator;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.LruCache;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SpellChecker implements SpellCheckerSession.SpellCheckerSessionListener {
    public static final int AVERAGE_WORD_LENGTH = 7;
    private static final boolean DBG = false;
    public static final int MAX_NUMBER_OF_WORDS = 50;
    private static final int MIN_SENTENCE_LENGTH = 50;
    private static final int SPELL_PAUSE_DURATION = 400;
    private static final int SUGGESTION_SPAN_CACHE_SIZE = 10;
    private static final String TAG = SpellChecker.class.getSimpleName();
    private static final int USE_SPAN_RANGE = -1;
    public static final int WORD_ITERATOR_INTERVAL = 350;
    final int mCookie;
    private Locale mCurrentLocale;
    private int[] mIds;
    private boolean mIsSentenceSpellCheckSupported;
    private int mLength;
    private SpellCheckSpan[] mSpellCheckSpans;
    SpellCheckerSession mSpellCheckerSession;
    private Runnable mSpellRunnable;
    private TextServicesManager mTextServicesManager;
    private final TextView mTextView;
    private WordIterator mWordIterator;
    private SpellParser[] mSpellParsers = new SpellParser[0];
    private int mSpanSequenceCounter = 0;
    private final LruCache<Long, SuggestionSpan> mSuggestionSpanCache = new LruCache<>(10);

    public SpellChecker(TextView textView) {
        this.mTextView = textView;
        int[] iArrNewUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(1);
        this.mIds = iArrNewUnpaddedIntArray;
        this.mSpellCheckSpans = new SpellCheckSpan[iArrNewUnpaddedIntArray.length];
        setLocale(this.mTextView.getSpellCheckerLocale());
        this.mCookie = hashCode();
    }

    private void resetSession() {
        closeSession();
        TextServicesManager textServicesManager = (TextServicesManager) this.mTextView.getContext().getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        this.mTextServicesManager = textServicesManager;
        if (!textServicesManager.isSpellCheckerEnabled() || this.mCurrentLocale == null || this.mTextServicesManager.getCurrentSpellCheckerSubtype(true) == null) {
            this.mSpellCheckerSession = null;
        } else {
            this.mSpellCheckerSession = this.mTextServicesManager.newSpellCheckerSession(null, this.mCurrentLocale, this, false);
            this.mIsSentenceSpellCheckSupported = true;
        }
        for (int i = 0; i < this.mLength; i++) {
            this.mIds[i] = -1;
        }
        this.mLength = 0;
        TextView textView = this.mTextView;
        textView.removeMisspelledSpans((Editable) textView.getText());
        this.mSuggestionSpanCache.evictAll();
    }

    private void setLocale(Locale locale) {
        this.mCurrentLocale = locale;
        resetSession();
        if (locale != null) {
            this.mWordIterator = new WordIterator(locale);
        }
        this.mTextView.onLocaleChanged();
    }

    private boolean isSessionActive() {
        return this.mSpellCheckerSession != null;
    }

    public void closeSession() {
        SpellCheckerSession spellCheckerSession = this.mSpellCheckerSession;
        if (spellCheckerSession != null) {
            spellCheckerSession.close();
        }
        int length = this.mSpellParsers.length;
        for (int i = 0; i < length; i++) {
            this.mSpellParsers[i].stop();
        }
        Runnable runnable = this.mSpellRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
    }

    private int nextSpellCheckSpanIndex() {
        int i = 0;
        while (true) {
            int i2 = this.mLength;
            if (i < i2) {
                if (this.mIds[i] < 0) {
                    return i;
                }
                i++;
            } else {
                this.mIds = GrowingArrayUtils.append(this.mIds, i2, 0);
                this.mSpellCheckSpans = (SpellCheckSpan[]) GrowingArrayUtils.append(this.mSpellCheckSpans, this.mLength, new SpellCheckSpan());
                int i3 = this.mLength + 1;
                this.mLength = i3;
                return i3 - 1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addSpellCheckSpan(Editable editable, int i, int i2) {
        int iNextSpellCheckSpanIndex = nextSpellCheckSpanIndex();
        SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[iNextSpellCheckSpanIndex];
        editable.setSpan(spellCheckSpan, i, i2, 33);
        spellCheckSpan.setSpellCheckInProgress(false);
        int[] iArr = this.mIds;
        int i3 = this.mSpanSequenceCounter;
        this.mSpanSequenceCounter = i3 + 1;
        iArr[iNextSpellCheckSpanIndex] = i3;
    }

    public void onSpellCheckSpanRemoved(SpellCheckSpan spellCheckSpan) {
        for (int i = 0; i < this.mLength; i++) {
            if (this.mSpellCheckSpans[i] == spellCheckSpan) {
                this.mIds[i] = -1;
                return;
            }
        }
    }

    public void onSelectionChanged() {
        spellCheck();
    }

    public void spellCheck(int i, int i2) {
        Locale locale;
        Locale spellCheckerLocale = this.mTextView.getSpellCheckerLocale();
        boolean zIsSessionActive = isSessionActive();
        if (spellCheckerLocale == null || (locale = this.mCurrentLocale) == null || !locale.equals(spellCheckerLocale)) {
            setLocale(spellCheckerLocale);
            i2 = this.mTextView.getText().length();
            i = 0;
        } else if (zIsSessionActive != this.mTextServicesManager.isSpellCheckerEnabled()) {
            resetSession();
        }
        if (zIsSessionActive) {
            int length = this.mSpellParsers.length;
            for (int i3 = 0; i3 < length; i3++) {
                SpellParser spellParser = this.mSpellParsers[i3];
                if (spellParser.isFinished()) {
                    spellParser.parse(i, i2);
                    return;
                }
            }
            SpellParser[] spellParserArr = new SpellParser[length + 1];
            System.arraycopy(this.mSpellParsers, 0, spellParserArr, 0, length);
            this.mSpellParsers = spellParserArr;
            SpellParser spellParser2 = new SpellParser();
            this.mSpellParsers[length] = spellParser2;
            spellParser2.parse(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void spellCheck() {
        if (this.mSpellCheckerSession == null) {
            return;
        }
        Editable editable = (Editable) this.mTextView.getText();
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        int i = this.mLength;
        TextInfo[] textInfoArr = new TextInfo[i];
        int i2 = 0;
        for (int i3 = 0; i3 < this.mLength; i3++) {
            SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[i3];
            if (this.mIds[i3] >= 0 && !spellCheckSpan.isSpellCheckInProgress()) {
                int spanStart = editable.getSpanStart(spellCheckSpan);
                int spanEnd = editable.getSpanEnd(spellCheckSpan);
                int i4 = spanEnd + 1;
                boolean z = !(selectionStart == i4 && WordIterator.isMidWordPunctuation(this.mCurrentLocale, Character.codePointBefore(editable, i4))) && (!this.mIsSentenceSpellCheckSupported ? !(selectionEnd < spanStart || selectionStart > spanEnd) : !(selectionEnd <= spanStart || selectionStart > spanEnd));
                if (spanStart >= 0 && spanEnd > spanStart && z) {
                    spellCheckSpan.setSpellCheckInProgress(true);
                    textInfoArr[i2] = new TextInfo(editable, spanStart, spanEnd, this.mCookie, this.mIds[i3]);
                    i2++;
                }
            }
        }
        if (i2 > 0) {
            if (i2 < i) {
                TextInfo[] textInfoArr2 = new TextInfo[i2];
                System.arraycopy(textInfoArr, 0, textInfoArr2, 0, i2);
                textInfoArr = textInfoArr2;
            }
            if (this.mIsSentenceSpellCheckSupported) {
                this.mSpellCheckerSession.getSentenceSuggestions(textInfoArr, 5);
            } else {
                this.mSpellCheckerSession.getSuggestions(textInfoArr, 5, false);
            }
        }
    }

    private SpellCheckSpan onGetSuggestionsInternal(SuggestionsInfo suggestionsInfo, int i, int i2) {
        int i3;
        int i4;
        if (suggestionsInfo != null && suggestionsInfo.getCookie() == this.mCookie) {
            Editable editable = (Editable) this.mTextView.getText();
            int sequence = suggestionsInfo.getSequence();
            for (int i5 = 0; i5 < this.mLength; i5++) {
                if (sequence == this.mIds[i5]) {
                    int suggestionsAttributes = suggestionsInfo.getSuggestionsAttributes();
                    boolean z = (suggestionsAttributes & 1) > 0;
                    boolean z2 = (suggestionsAttributes & 2) > 0;
                    SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[i5];
                    if (!z && z2) {
                        createMisspelledSuggestionSpan(editable, suggestionsInfo, spellCheckSpan, i, i2);
                    } else if (this.mIsSentenceSpellCheckSupported) {
                        int spanStart = editable.getSpanStart(spellCheckSpan);
                        int spanEnd = editable.getSpanEnd(spellCheckSpan);
                        if (i == -1 || i2 == -1) {
                            i3 = spanStart;
                            i4 = spanEnd;
                        } else {
                            i3 = i + spanStart;
                            i4 = i2 + i3;
                        }
                        if (spanStart >= 0 && spanEnd > spanStart && i4 > i3) {
                            Long lValueOf = Long.valueOf(TextUtils.packRangeInLong(i3, i4));
                            Object obj = (SuggestionSpan) this.mSuggestionSpanCache.get(lValueOf);
                            if (obj != null) {
                                editable.removeSpan(obj);
                                this.mSuggestionSpanCache.remove(lValueOf);
                            }
                        }
                    }
                    return spellCheckSpan;
                }
            }
        }
        return null;
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr) {
        Editable editable = (Editable) this.mTextView.getText();
        for (SuggestionsInfo suggestionsInfo : suggestionsInfoArr) {
            SpellCheckSpan spellCheckSpanOnGetSuggestionsInternal = onGetSuggestionsInternal(suggestionsInfo, -1, -1);
            if (spellCheckSpanOnGetSuggestionsInternal != null) {
                editable.removeSpan(spellCheckSpanOnGetSuggestionsInternal);
            }
        }
        scheduleNewSpellCheck();
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr) {
        Editable editable = (Editable) this.mTextView.getText();
        for (SentenceSuggestionsInfo sentenceSuggestionsInfo : sentenceSuggestionsInfoArr) {
            if (sentenceSuggestionsInfo != null) {
                SpellCheckSpan spellCheckSpan = null;
                for (int i = 0; i < sentenceSuggestionsInfo.getSuggestionsCount(); i++) {
                    SuggestionsInfo suggestionsInfoAt = sentenceSuggestionsInfo.getSuggestionsInfoAt(i);
                    if (suggestionsInfoAt != null) {
                        SpellCheckSpan spellCheckSpanOnGetSuggestionsInternal = onGetSuggestionsInternal(suggestionsInfoAt, sentenceSuggestionsInfo.getOffsetAt(i), sentenceSuggestionsInfo.getLengthAt(i));
                        if (spellCheckSpan == null && spellCheckSpanOnGetSuggestionsInternal != null) {
                            spellCheckSpan = spellCheckSpanOnGetSuggestionsInternal;
                        }
                    }
                }
                if (spellCheckSpan != null) {
                    editable.removeSpan(spellCheckSpan);
                }
            }
        }
        scheduleNewSpellCheck();
    }

    private void scheduleNewSpellCheck() {
        Runnable runnable = this.mSpellRunnable;
        if (runnable == null) {
            this.mSpellRunnable = new Runnable() { // from class: android.widget.SpellChecker.1
                @Override // java.lang.Runnable
                public void run() {
                    int length = SpellChecker.this.mSpellParsers.length;
                    for (int i = 0; i < length; i++) {
                        SpellParser spellParser = SpellChecker.this.mSpellParsers[i];
                        if (!spellParser.isFinished()) {
                            spellParser.parse();
                            return;
                        }
                    }
                }
            };
        } else {
            this.mTextView.removeCallbacks(runnable);
        }
        this.mTextView.postDelayed(this.mSpellRunnable, 400L);
    }

    private void createMisspelledSuggestionSpan(Editable editable, SuggestionsInfo suggestionsInfo, SpellCheckSpan spellCheckSpan, int i, int i2) {
        String[] strArr;
        int spanStart = editable.getSpanStart(spellCheckSpan);
        int spanEnd = editable.getSpanEnd(spellCheckSpan);
        if (spanStart < 0 || spanEnd <= spanStart) {
            return;
        }
        if (i != -1 && i2 != -1) {
            spanStart += i;
            spanEnd = spanStart + i2;
        }
        int suggestionsCount = suggestionsInfo.getSuggestionsCount();
        if (suggestionsCount > 0) {
            strArr = new String[suggestionsCount];
            for (int i3 = 0; i3 < suggestionsCount; i3++) {
                strArr[i3] = suggestionsInfo.getSuggestionAt(i3);
            }
        } else {
            strArr = (String[]) ArrayUtils.emptyArray(String.class);
        }
        SuggestionSpan suggestionSpan = new SuggestionSpan(this.mTextView.getContext(), strArr, 3);
        if (this.mIsSentenceSpellCheckSupported) {
            Long lValueOf = Long.valueOf(TextUtils.packRangeInLong(spanStart, spanEnd));
            SuggestionSpan suggestionSpan2 = this.mSuggestionSpanCache.get(lValueOf);
            if (suggestionSpan2 != null) {
                editable.removeSpan(suggestionSpan2);
            }
            this.mSuggestionSpanCache.put(lValueOf, suggestionSpan);
        }
        editable.setSpan(suggestionSpan, spanStart, spanEnd, 33);
        this.mTextView.invalidateRegion(spanStart, spanEnd, false);
    }

    private class SpellParser {
        private Object mRange;

        private SpellParser() {
            this.mRange = new Object();
        }

        public void parse(int i, int i2) {
            int length = SpellChecker.this.mTextView.length();
            if (i2 > length) {
                Log.w(SpellChecker.TAG, "Parse invalid region, from " + i + " to " + i2);
                i2 = length;
            }
            if (i2 > i) {
                setRangeSpan((Editable) SpellChecker.this.mTextView.getText(), i, i2);
                parse();
            }
        }

        public boolean isFinished() {
            return ((Editable) SpellChecker.this.mTextView.getText()).getSpanStart(this.mRange) < 0;
        }

        public void stop() {
            removeRangeSpan((Editable) SpellChecker.this.mTextView.getText());
        }

        private void setRangeSpan(Editable editable, int i, int i2) {
            editable.setSpan(this.mRange, i, i2, 33);
        }

        private void removeRangeSpan(Editable editable) {
            editable.removeSpan(this.mRange);
        }

        public void parse() {
            int spanStart;
            int end;
            int iPreceding;
            boolean z;
            Editable editable = (Editable) SpellChecker.this.mTextView.getText();
            int i = 50;
            boolean z2 = false;
            if (SpellChecker.this.mIsSentenceSpellCheckSupported) {
                spanStart = Math.max(0, editable.getSpanStart(this.mRange) - 50);
            } else {
                spanStart = editable.getSpanStart(this.mRange);
            }
            int spanEnd = editable.getSpanEnd(this.mRange);
            int iMin = Math.min(spanEnd, spanStart + 350);
            SpellChecker.this.mWordIterator.setCharSequence(editable, spanStart, iMin);
            int iPreceding2 = SpellChecker.this.mWordIterator.preceding(spanStart);
            if (iPreceding2 == -1) {
                end = SpellChecker.this.mWordIterator.following(spanStart);
                if (end != -1) {
                    iPreceding2 = SpellChecker.this.mWordIterator.getBeginning(end);
                }
            } else {
                end = SpellChecker.this.mWordIterator.getEnd(iPreceding2);
            }
            if (end == -1) {
                removeRangeSpan(editable);
                return;
            }
            int i2 = spanStart - 1;
            int i3 = spanEnd + 1;
            SpellCheckSpan[] spellCheckSpanArr = (SpellCheckSpan[]) editable.getSpans(i2, i3, SpellCheckSpan.class);
            SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) editable.getSpans(i2, i3, SuggestionSpan.class);
            if (!SpellChecker.this.mIsSentenceSpellCheckSupported) {
                int i4 = 0;
                while (true) {
                    if (iPreceding2 <= spanEnd) {
                        if (end >= spanStart && end > iPreceding2) {
                            if (i4 >= i) {
                                iPreceding = iPreceding2;
                                z2 = true;
                                break;
                            }
                            if (iPreceding2 < spanStart && end > spanStart) {
                                removeSpansAt(editable, spanStart, spellCheckSpanArr);
                                removeSpansAt(editable, spanStart, suggestionSpanArr);
                            }
                            if (iPreceding2 < spanEnd && end > spanEnd) {
                                removeSpansAt(editable, spanEnd, spellCheckSpanArr);
                                removeSpansAt(editable, spanEnd, suggestionSpanArr);
                            }
                            if (end != spanStart) {
                                z = true;
                                break;
                            }
                            int i5 = 0;
                            while (true) {
                                if (i5 >= spellCheckSpanArr.length) {
                                    z = true;
                                    break;
                                } else {
                                    if (editable.getSpanEnd(spellCheckSpanArr[i5]) == spanStart) {
                                        z = false;
                                        break;
                                    }
                                    i5++;
                                }
                            }
                            if (iPreceding2 == spanEnd) {
                                for (SpellCheckSpan spellCheckSpan : spellCheckSpanArr) {
                                    if (editable.getSpanStart(spellCheckSpan) == spanEnd) {
                                        z = false;
                                        break;
                                    }
                                }
                            }
                            if (z) {
                                SpellChecker.this.addSpellCheckSpan(editable, iPreceding2, end);
                            }
                            i4++;
                        }
                        int iFollowing = SpellChecker.this.mWordIterator.following(end);
                        if (iMin >= spanEnd || (iFollowing != -1 && iFollowing < iMin)) {
                            end = iFollowing;
                        } else {
                            int iMin2 = Math.min(spanEnd, end + 350);
                            SpellChecker.this.mWordIterator.setCharSequence(editable, end, iMin2);
                            end = SpellChecker.this.mWordIterator.following(end);
                            iMin = iMin2;
                        }
                        if (end != -1 && (iPreceding2 = SpellChecker.this.mWordIterator.getBeginning(end)) != -1) {
                            i = 50;
                        }
                    }
                    iPreceding = iPreceding2;
                    break;
                }
            } else {
                boolean z3 = iMin < spanEnd;
                iPreceding = SpellChecker.this.mWordIterator.preceding(iMin);
                boolean z4 = iPreceding != -1;
                if (z4) {
                    iPreceding = SpellChecker.this.mWordIterator.getEnd(iPreceding);
                    z4 = iPreceding != -1;
                }
                if (!z4) {
                    removeRangeSpan(editable);
                    return;
                }
                int i6 = 0;
                while (true) {
                    if (i6 >= SpellChecker.this.mLength) {
                        z2 = true;
                        break;
                    }
                    SpellCheckSpan spellCheckSpan2 = SpellChecker.this.mSpellCheckSpans[i6];
                    if (SpellChecker.this.mIds[i6] >= 0 && !spellCheckSpan2.isSpellCheckInProgress()) {
                        int spanStart2 = editable.getSpanStart(spellCheckSpan2);
                        int spanEnd2 = editable.getSpanEnd(spellCheckSpan2);
                        if (spanEnd2 >= iPreceding2 && iPreceding >= spanStart2) {
                            if (spanStart2 <= iPreceding2 && iPreceding <= spanEnd2) {
                                break;
                            }
                            editable.removeSpan(spellCheckSpan2);
                            iPreceding2 = Math.min(spanStart2, iPreceding2);
                            iPreceding = Math.max(spanEnd2, iPreceding);
                        }
                    }
                    i6++;
                }
                if (iPreceding >= spanStart) {
                    if (iPreceding <= iPreceding2) {
                        Log.w(SpellChecker.TAG, "Trying to spellcheck invalid region, from " + spanStart + " to " + spanEnd);
                    } else if (z2) {
                        SpellChecker.this.addSpellCheckSpan(editable, iPreceding2, iPreceding);
                    }
                }
                z2 = z3;
            }
            if (z2 && iPreceding != -1 && iPreceding <= spanEnd) {
                setRangeSpan(editable, iPreceding, spanEnd);
            } else {
                removeRangeSpan(editable);
            }
            SpellChecker.this.spellCheck();
        }

        private <T> void removeSpansAt(Editable editable, int i, T[] tArr) {
            for (T t : tArr) {
                if (editable.getSpanStart(t) <= i && editable.getSpanEnd(t) >= i) {
                    editable.removeSpan(t);
                }
            }
        }
    }

    public static boolean haveWordBoundariesChanged(Editable editable, int i, int i2, int i3, int i4) {
        if (i4 != i && i3 != i2) {
            return true;
        }
        if (i4 == i && i < editable.length()) {
            return Character.isLetterOrDigit(Character.codePointAt(editable, i));
        }
        if (i3 != i2 || i2 <= 0) {
            return false;
        }
        return Character.isLetterOrDigit(Character.codePointBefore(editable, i2));
    }
}
