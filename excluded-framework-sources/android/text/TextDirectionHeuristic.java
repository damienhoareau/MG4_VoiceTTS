package android.text;

/* JADX INFO: loaded from: classes2.dex */
public interface TextDirectionHeuristic {
    boolean isRtl(CharSequence charSequence, int i, int i2);

    boolean isRtl(char[] cArr, int i, int i2);
}
