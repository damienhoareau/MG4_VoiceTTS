package android.util;

/* JADX INFO: loaded from: classes2.dex */
public class StringBuilderPrinter implements Printer {
    private final StringBuilder mBuilder;

    public StringBuilderPrinter(StringBuilder sb) {
        this.mBuilder = sb;
    }

    @Override // android.util.Printer
    public void println(String str) {
        this.mBuilder.append(str);
        int length = str.length();
        if (length <= 0 || str.charAt(length - 1) != '\n') {
            this.mBuilder.append('\n');
        }
    }
}
