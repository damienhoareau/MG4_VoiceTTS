package android.util;

import java.io.PrintStream;

/* JADX INFO: loaded from: classes2.dex */
public class PrintStreamPrinter implements Printer {
    private final PrintStream mPS;

    public PrintStreamPrinter(PrintStream printStream) {
        this.mPS = printStream;
    }

    @Override // android.util.Printer
    public void println(String str) {
        this.mPS.println(str);
    }
}
