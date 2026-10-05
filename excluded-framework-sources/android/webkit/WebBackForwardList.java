package android.webkit;

import java.io.Serializable;

/* JADX INFO: loaded from: classes2.dex */
public abstract class WebBackForwardList implements Cloneable, Serializable {
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public abstract WebBackForwardList m37clone();

    public abstract int getCurrentIndex();

    public abstract WebHistoryItem getCurrentItem();

    public abstract WebHistoryItem getItemAtIndex(int i);

    public abstract int getSize();
}
