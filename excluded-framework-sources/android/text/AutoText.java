package android.text;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.provider.UserDictionary;
import android.view.View;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public class AutoText {
    private static final int DEFAULT = 14337;
    private static final int INCREMENT = 1024;
    private static final int RIGHT = 9300;
    private static final int TRIE_C = 0;
    private static final int TRIE_CHILD = 2;
    private static final int TRIE_NEXT = 3;
    private static final char TRIE_NULL = 65535;
    private static final int TRIE_OFF = 1;
    private static final int TRIE_ROOT = 0;
    private static final int TRIE_SIZEOF = 4;
    private static AutoText sInstance = new AutoText(Resources.getSystem());
    private static Object sLock = new Object();
    private Locale mLocale;
    private int mSize;
    private String mText;
    private char[] mTrie;
    private char mTrieUsed;

    private AutoText(Resources resources) {
        this.mLocale = resources.getConfiguration().locale;
        init(resources);
    }

    private static AutoText getInstance(View view) {
        AutoText autoText;
        Resources resources = view.getContext().getResources();
        Locale locale = resources.getConfiguration().locale;
        synchronized (sLock) {
            autoText = sInstance;
            if (!locale.equals(autoText.mLocale)) {
                autoText = new AutoText(resources);
                sInstance = autoText;
            }
        }
        return autoText;
    }

    public static String get(CharSequence charSequence, int i, int i2, View view) {
        return getInstance(view).lookup(charSequence, i, i2);
    }

    public static int getSize(View view) {
        return getInstance(view).getSize();
    }

    private int getSize() {
        return this.mSize;
    }

    private String lookup(CharSequence charSequence, int i, int i2) {
        char c = this.mTrie[0];
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            while (c != 65535) {
                char[] cArr = this.mTrie;
                if (cCharAt != cArr[c + 0]) {
                    c = cArr[c + 3];
                } else {
                    if (i == i2 - 1) {
                        int i3 = c + 1;
                        if (cArr[i3] != 65535) {
                            char c2 = cArr[i3];
                            char cCharAt2 = this.mText.charAt(c2);
                            int i4 = c2 + 1;
                            return this.mText.substring(i4, cCharAt2 + i4);
                        }
                    }
                    c = this.mTrie[c + 2];
                    break;
                }
            }
            if (c == 65535) {
                return null;
            }
            i++;
        }
        return null;
    }

    private void init(Resources resources) {
        char length;
        XmlResourceParser xml = resources.getXml(R.xml.autotext);
        StringBuilder sb = new StringBuilder(RIGHT);
        char[] cArr = new char[14337];
        this.mTrie = cArr;
        cArr[0] = TRIE_NULL;
        this.mTrieUsed = (char) 1;
        try {
            try {
                try {
                    XmlUtils.beginDocument(xml, "words");
                    while (true) {
                        XmlUtils.nextElement(xml);
                        String name = xml.getName();
                        if (name == null || !name.equals(UserDictionary.Words.WORD)) {
                            break;
                            break;
                        }
                        String attributeValue = xml.getAttributeValue(null, "src");
                        if (xml.next() == 4) {
                            String text = xml.getText();
                            if (text.equals("")) {
                                length = 0;
                            } else {
                                length = (char) sb.length();
                                sb.append((char) text.length());
                                sb.append(text);
                            }
                            add(attributeValue, length);
                        }
                    }
                    resources.flushLayoutCache();
                    xml.close();
                    this.mText = sb.toString();
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }

    private void add(String str, char c) {
        boolean z;
        int length = str.length();
        this.mSize++;
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            while (true) {
                char[] cArr = this.mTrie;
                if (cArr[i] == 65535) {
                    z = false;
                    break;
                }
                if (cCharAt == cArr[cArr[i] + 0]) {
                    if (i2 == length - 1) {
                        cArr[cArr[i] + 1] = c;
                        return;
                    } else {
                        i = cArr[i] + 2;
                        z = true;
                        break;
                    }
                }
                i = cArr[i] + 3;
            }
            if (!z) {
                char cNewTrieNode = newTrieNode();
                char[] cArr2 = this.mTrie;
                cArr2[i] = cNewTrieNode;
                cArr2[cArr2[i] + 0] = cCharAt;
                cArr2[cArr2[i] + 1] = TRIE_NULL;
                cArr2[cArr2[i] + 3] = TRIE_NULL;
                cArr2[cArr2[i] + 2] = TRIE_NULL;
                if (i2 == length - 1) {
                    cArr2[cArr2[i] + 1] = c;
                    return;
                }
                i = cArr2[i] + 2;
            }
        }
    }

    private char newTrieNode() {
        int i = this.mTrieUsed + 4;
        char[] cArr = this.mTrie;
        if (i > cArr.length) {
            char[] cArr2 = new char[cArr.length + 1024];
            System.arraycopy(cArr, 0, cArr2, 0, cArr.length);
            this.mTrie = cArr2;
        }
        char c = this.mTrieUsed;
        this.mTrieUsed = (char) (c + 4);
        return c;
    }
}
