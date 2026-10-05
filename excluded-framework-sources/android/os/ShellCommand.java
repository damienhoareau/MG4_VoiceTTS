package android.os;

import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.FastPrintWriter;
import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ShellCommand {
    static final boolean DEBUG = false;
    static final String TAG = "ShellCommand";
    private int mArgPos;
    private String[] mArgs;
    private String mCmd;
    private String mCurArgData;
    private FileDescriptor mErr;
    private FastPrintWriter mErrPrintWriter;
    private FileOutputStream mFileErr;
    private FileInputStream mFileIn;
    private FileOutputStream mFileOut;
    private FileDescriptor mIn;
    private InputStream mInputStream;
    private FileDescriptor mOut;
    private FastPrintWriter mOutPrintWriter;
    private ResultReceiver mResultReceiver;
    private ShellCallback mShellCallback;
    private Binder mTarget;

    public abstract int onCommand(String str);

    public abstract void onHelp();

    public void init(Binder binder, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, int i) {
        this.mTarget = binder;
        this.mIn = fileDescriptor;
        this.mOut = fileDescriptor2;
        this.mErr = fileDescriptor3;
        this.mArgs = strArr;
        this.mShellCallback = shellCallback;
        this.mResultReceiver = null;
        this.mCmd = null;
        this.mArgPos = i;
        this.mCurArgData = null;
        this.mFileIn = null;
        this.mFileOut = null;
        this.mFileErr = null;
        this.mOutPrintWriter = null;
        this.mErrPrintWriter = null;
        this.mInputStream = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x003a, code lost:
    
        if (r0 != null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x003c, code lost:
    
        r0.send(r1, null);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int exec(android.os.Binder r13, java.io.FileDescriptor r14, java.io.FileDescriptor r15, java.io.FileDescriptor r16, java.lang.String[] r17, android.os.ShellCallback r18, android.os.ResultReceiver r19) {
        /*
            r12 = this;
            r9 = r12
            r0 = r17
            r1 = 0
            r10 = 0
            if (r0 == 0) goto L10
            int r2 = r0.length
            if (r2 <= 0) goto L10
            r1 = r0[r1]
            r2 = 1
            r11 = r1
            r8 = r2
            goto L12
        L10:
            r8 = r1
            r11 = r10
        L12:
            r1 = r12
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = r16
            r6 = r17
            r7 = r18
            r1.init(r2, r3, r4, r5, r6, r7, r8)
            r9.mCmd = r11
            r0 = r19
            r9.mResultReceiver = r0
            r1 = -1
            int r1 = r12.onCommand(r11)     // Catch: java.lang.Throwable -> L40 java.lang.SecurityException -> L64
            com.android.internal.util.FastPrintWriter r0 = r9.mOutPrintWriter
            if (r0 == 0) goto L31
            r0.flush()
        L31:
            com.android.internal.util.FastPrintWriter r0 = r9.mErrPrintWriter
            if (r0 == 0) goto L38
            r0.flush()
        L38:
            android.os.ResultReceiver r0 = r9.mResultReceiver
            if (r0 == 0) goto L9b
        L3c:
            r0.send(r1, r10)
            goto L9b
        L40:
            r0 = move-exception
            r2 = r0
            java.io.PrintWriter r0 = r12.getErrPrintWriter()     // Catch: java.lang.Throwable -> L9c
            r0.println()     // Catch: java.lang.Throwable -> L9c
            java.lang.String r3 = "Exception occurred while executing:"
            r0.println(r3)     // Catch: java.lang.Throwable -> L9c
            r2.printStackTrace(r0)     // Catch: java.lang.Throwable -> L9c
            com.android.internal.util.FastPrintWriter r0 = r9.mOutPrintWriter
            if (r0 == 0) goto L58
            r0.flush()
        L58:
            com.android.internal.util.FastPrintWriter r0 = r9.mErrPrintWriter
            if (r0 == 0) goto L5f
            r0.flush()
        L5f:
            android.os.ResultReceiver r0 = r9.mResultReceiver
            if (r0 == 0) goto L9b
            goto L3c
        L64:
            r0 = move-exception
            r2 = r0
            java.io.PrintWriter r0 = r12.getErrPrintWriter()     // Catch: java.lang.Throwable -> L9c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9c
            r3.<init>()     // Catch: java.lang.Throwable -> L9c
            java.lang.String r4 = "Security exception: "
            r3.append(r4)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r4 = r2.getMessage()     // Catch: java.lang.Throwable -> L9c
            r3.append(r4)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L9c
            r0.println(r3)     // Catch: java.lang.Throwable -> L9c
            r0.println()     // Catch: java.lang.Throwable -> L9c
            r2.printStackTrace(r0)     // Catch: java.lang.Throwable -> L9c
            com.android.internal.util.FastPrintWriter r0 = r9.mOutPrintWriter
            if (r0 == 0) goto L8f
            r0.flush()
        L8f:
            com.android.internal.util.FastPrintWriter r0 = r9.mErrPrintWriter
            if (r0 == 0) goto L96
            r0.flush()
        L96:
            android.os.ResultReceiver r0 = r9.mResultReceiver
            if (r0 == 0) goto L9b
            goto L3c
        L9b:
            return r1
        L9c:
            r0 = move-exception
            com.android.internal.util.FastPrintWriter r2 = r9.mOutPrintWriter
            if (r2 == 0) goto La4
            r2.flush()
        La4:
            com.android.internal.util.FastPrintWriter r2 = r9.mErrPrintWriter
            if (r2 == 0) goto Lab
            r2.flush()
        Lab:
            android.os.ResultReceiver r2 = r9.mResultReceiver
            if (r2 == 0) goto Lb2
            r2.send(r1, r10)
        Lb2:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.ShellCommand.exec(android.os.Binder, java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):int");
    }

    public ResultReceiver adoptResultReceiver() {
        ResultReceiver resultReceiver = this.mResultReceiver;
        this.mResultReceiver = null;
        return resultReceiver;
    }

    public FileDescriptor getOutFileDescriptor() {
        return this.mOut;
    }

    public OutputStream getRawOutputStream() {
        if (this.mFileOut == null) {
            this.mFileOut = new FileOutputStream(this.mOut);
        }
        return this.mFileOut;
    }

    public PrintWriter getOutPrintWriter() {
        if (this.mOutPrintWriter == null) {
            this.mOutPrintWriter = new FastPrintWriter(getRawOutputStream());
        }
        return this.mOutPrintWriter;
    }

    public FileDescriptor getErrFileDescriptor() {
        return this.mErr;
    }

    public OutputStream getRawErrorStream() {
        if (this.mFileErr == null) {
            this.mFileErr = new FileOutputStream(this.mErr);
        }
        return this.mFileErr;
    }

    public PrintWriter getErrPrintWriter() {
        if (this.mErr == null) {
            return getOutPrintWriter();
        }
        if (this.mErrPrintWriter == null) {
            this.mErrPrintWriter = new FastPrintWriter(getRawErrorStream());
        }
        return this.mErrPrintWriter;
    }

    public FileDescriptor getInFileDescriptor() {
        return this.mIn;
    }

    public InputStream getRawInputStream() {
        if (this.mFileIn == null) {
            this.mFileIn = new FileInputStream(this.mIn);
        }
        return this.mFileIn;
    }

    public InputStream getBufferedInputStream() {
        if (this.mInputStream == null) {
            this.mInputStream = new BufferedInputStream(getRawInputStream());
        }
        return this.mInputStream;
    }

    public ParcelFileDescriptor openFileForSystem(String str, String str2) {
        try {
            ParcelFileDescriptor parcelFileDescriptorOpenFile = getShellCallback().openFile(str, "u:r:system_server:s0", str2);
            if (parcelFileDescriptorOpenFile != null) {
                return parcelFileDescriptorOpenFile;
            }
        } catch (RuntimeException e) {
            getErrPrintWriter().println("Failure opening file: " + e.getMessage());
        }
        getErrPrintWriter().println("Error: Unable to open file: " + str);
        getErrPrintWriter().println("Consider using a file under /data/local/tmp/");
        return null;
    }

    public String getNextOption() {
        if (this.mCurArgData != null) {
            throw new IllegalArgumentException("No argument expected after \"" + this.mArgs[this.mArgPos - 1] + "\"");
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String str = strArr[i];
        if (!str.startsWith(NativeLibraryHelper.CLEAR_ABI_OVERRIDE)) {
            return null;
        }
        this.mArgPos++;
        if (str.equals("--")) {
            return null;
        }
        if (str.length() > 1 && str.charAt(1) != '-') {
            if (str.length() > 2) {
                this.mCurArgData = str.substring(2);
                return str.substring(0, 2);
            }
            this.mCurArgData = null;
            return str;
        }
        this.mCurArgData = null;
        return str;
    }

    public String getNextArg() {
        String str = this.mCurArgData;
        if (str != null) {
            this.mCurArgData = null;
            return str;
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        this.mArgPos = i + 1;
        return strArr[i];
    }

    public String peekNextArg() {
        String str = this.mCurArgData;
        if (str != null) {
            return str;
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i < strArr.length) {
            return strArr[i];
        }
        return null;
    }

    public String getNextArgRequired() {
        String nextArg = getNextArg();
        if (nextArg != null) {
            return nextArg;
        }
        throw new IllegalArgumentException("Argument expected after \"" + this.mArgs[this.mArgPos - 1] + "\"");
    }

    public ShellCallback getShellCallback() {
        return this.mShellCallback;
    }

    public int handleDefaultCommands(String str) {
        if ("dump".equals(str)) {
            String[] strArr = this.mArgs;
            String[] strArr2 = new String[strArr.length - 1];
            System.arraycopy(strArr, 1, strArr2, 0, strArr.length - 1);
            this.mTarget.doDump(this.mOut, getOutPrintWriter(), strArr2);
            return 0;
        }
        if (str == null || "help".equals(str) || "-h".equals(str)) {
            onHelp();
            return -1;
        }
        getOutPrintWriter().println("Unknown command: " + str);
        return -1;
    }
}
