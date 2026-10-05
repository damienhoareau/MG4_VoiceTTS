package android.text.method;

import com.android.internal.util.ArrayUtils;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DateKeyListener extends NumberKeyListener {
    private static final String SYMBOLS_TO_IGNORE = "yMLd";
    private final char[] mCharacters;
    private final boolean mNeedsAdvancedInput;
    private static final String[] SKELETONS = {"yMd", "yM", "Md"};

    @Deprecated
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', '.'};
    private static final Object sLock = new Object();
    private static final HashMap<Locale, DateKeyListener> sInstanceCache = new HashMap<>();

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return this.mNeedsAdvancedInput ? 1 : 20;
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return this.mCharacters;
    }

    @Deprecated
    public DateKeyListener() {
        this(null);
    }

    public DateKeyListener(Locale locale) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (NumberKeyListener.addDigits(linkedHashSet, locale) && NumberKeyListener.addFormatCharsFromSkeletons(linkedHashSet, locale, SKELETONS, SYMBOLS_TO_IGNORE)) {
            char[] cArrCollectionToArray = NumberKeyListener.collectionToArray(linkedHashSet);
            this.mCharacters = cArrCollectionToArray;
            this.mNeedsAdvancedInput = !ArrayUtils.containsAll(CHARACTERS, cArrCollectionToArray);
        } else {
            this.mCharacters = CHARACTERS;
            this.mNeedsAdvancedInput = false;
        }
    }

    @Deprecated
    public static DateKeyListener getInstance() {
        return getInstance(null);
    }

    public static DateKeyListener getInstance(Locale locale) {
        DateKeyListener dateKeyListener;
        synchronized (sLock) {
            dateKeyListener = sInstanceCache.get(locale);
            if (dateKeyListener == null) {
                dateKeyListener = new DateKeyListener(locale);
                sInstanceCache.put(locale, dateKeyListener);
            }
        }
        return dateKeyListener;
    }
}
