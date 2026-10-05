package android.text.style;

/* JADX INFO: loaded from: classes2.dex */
public interface TabStopSpan extends ParagraphStyle {
    int getTabStop();

    public static class Standard implements TabStopSpan {
        private int mTabOffset;

        public Standard(int i) {
            this.mTabOffset = i;
        }

        @Override // android.text.style.TabStopSpan
        public int getTabStop() {
            return this.mTabOffset;
        }
    }
}
