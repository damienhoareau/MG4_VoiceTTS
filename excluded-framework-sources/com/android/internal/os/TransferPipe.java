package com.android.internal.os;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
public class TransferPipe implements Runnable, Closeable {
    static final boolean DEBUG = false;
    static final long DEFAULT_TIMEOUT = 5000;
    static final String TAG = "TransferPipe";
    String mBufferPrefix;
    boolean mComplete;
    long mEndTime;
    String mFailure;
    final ParcelFileDescriptor[] mFds;
    FileDescriptor mOutFd;
    final Thread mThread;

    interface Caller {
        void go(IInterface iInterface, FileDescriptor fileDescriptor, String str, String[] strArr) throws RemoteException;
    }

    public TransferPipe() throws IOException {
        this(null);
    }

    public TransferPipe(String str) throws IOException {
        this(str, TAG);
    }

    protected TransferPipe(String str, String str2) throws IOException {
        this.mThread = new Thread(this, str2);
        this.mFds = ParcelFileDescriptor.createPipe();
        this.mBufferPrefix = str;
    }

    ParcelFileDescriptor getReadFd() {
        return this.mFds[0];
    }

    public ParcelFileDescriptor getWriteFd() {
        return this.mFds[1];
    }

    public void setBufferPrefix(String str) {
        this.mBufferPrefix = str;
    }

    public static void dumpAsync(IBinder iBinder, FileDescriptor fileDescriptor, String[] strArr) throws Exception {
        goDump(iBinder, fileDescriptor, strArr);
    }

    public static byte[] dumpAsync(IBinder iBinder, String... strArr) throws IOException, RemoteException {
        ParcelFileDescriptor[] parcelFileDescriptorArrCreatePipe = ParcelFileDescriptor.createPipe();
        try {
            dumpAsync(iBinder, parcelFileDescriptorArrCreatePipe[1].getFileDescriptor(), strArr);
            parcelFileDescriptorArrCreatePipe[1].close();
            parcelFileDescriptorArrCreatePipe[1] = null;
            byte[] bArr = new byte[4096];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptorArrCreatePipe[0].getFileDescriptor());
                while (true) {
                    try {
                        int i = fileInputStream.read(bArr);
                        if (i != -1) {
                            byteArrayOutputStream.write(bArr, 0, i);
                        } else {
                            $closeResource(null, fileInputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            $closeResource(null, byteArrayOutputStream);
                            parcelFileDescriptorArrCreatePipe[0].close();
                            IoUtils.closeQuietly(parcelFileDescriptorArrCreatePipe[1]);
                            return byteArray;
                        }
                    } catch (Throwable th) {
                        try {
                            throw th;
                        } catch (Throwable th2) {
                            $closeResource(th, fileInputStream);
                            throw th2;
                        }
                    }
                    try {
                        throw th;
                    } catch (Throwable th3) {
                        $closeResource(th, byteArrayOutputStream);
                        throw th3;
                    }
                }
            } catch (Throwable th4) {
                throw th4;
            }
        } catch (Throwable th5) {
            parcelFileDescriptorArrCreatePipe[0].close();
            IoUtils.closeQuietly(parcelFileDescriptorArrCreatePipe[1]);
            throw th5;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    static void go(Caller caller, IInterface iInterface, FileDescriptor fileDescriptor, String str, String[] strArr) throws Exception {
        go(caller, iInterface, fileDescriptor, str, strArr, 5000L);
    }

    static void go(Caller caller, IInterface iInterface, FileDescriptor fileDescriptor, String str, String[] strArr, long j) throws Exception {
        if (iInterface.asBinder() instanceof Binder) {
            try {
                caller.go(iInterface, fileDescriptor, str, strArr);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        TransferPipe transferPipe = new TransferPipe();
        try {
            caller.go(iInterface, transferPipe.getWriteFd().getFileDescriptor(), str, strArr);
            transferPipe.go(fileDescriptor, j);
            $closeResource(null, transferPipe);
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                $closeResource(th, transferPipe);
                throw th2;
            }
        }
    }

    static void goDump(IBinder iBinder, FileDescriptor fileDescriptor, String[] strArr) throws Exception {
        goDump(iBinder, fileDescriptor, strArr, 5000L);
    }

    static void goDump(IBinder iBinder, FileDescriptor fileDescriptor, String[] strArr, long j) throws Exception {
        if (iBinder instanceof Binder) {
            try {
                iBinder.dump(fileDescriptor, strArr);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        TransferPipe transferPipe = new TransferPipe();
        try {
            iBinder.dumpAsync(transferPipe.getWriteFd().getFileDescriptor(), strArr);
            transferPipe.go(fileDescriptor, j);
            $closeResource(null, transferPipe);
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                $closeResource(th, transferPipe);
                throw th2;
            }
        }
    }

    public void go(FileDescriptor fileDescriptor) throws IOException {
        go(fileDescriptor, 5000L);
    }

    public void go(FileDescriptor fileDescriptor, long j) throws IOException {
        try {
            synchronized (this) {
                this.mOutFd = fileDescriptor;
                this.mEndTime = SystemClock.uptimeMillis() + j;
                closeFd(1);
                this.mThread.start();
                while (this.mFailure == null && !this.mComplete) {
                    long jUptimeMillis = this.mEndTime - SystemClock.uptimeMillis();
                    if (jUptimeMillis <= 0) {
                        this.mThread.interrupt();
                        throw new IOException("Timeout");
                    }
                    try {
                        wait(jUptimeMillis);
                    } catch (InterruptedException unused) {
                    }
                }
                if (this.mFailure != null) {
                    throw new IOException(this.mFailure);
                }
            }
            kill();
        } catch (Throwable th) {
            kill();
            throw th;
        }
    }

    void closeFd(int i) {
        ParcelFileDescriptor[] parcelFileDescriptorArr = this.mFds;
        if (parcelFileDescriptorArr[i] != null) {
            try {
                parcelFileDescriptorArr[i].close();
            } catch (IOException unused) {
            }
            this.mFds[i] = null;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        kill();
    }

    public void kill() {
        synchronized (this) {
            closeFd(0);
            closeFd(1);
        }
    }

    protected OutputStream getNewOutputStream() {
        return new FileOutputStream(this.mOutFd);
    }

    @Override // java.lang.Runnable
    public void run() {
        byte[] bArr = new byte[1024];
        synchronized (this) {
            ParcelFileDescriptor readFd = getReadFd();
            if (readFd == null) {
                Slog.w(TAG, "Pipe has been closed...");
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(readFd.getFileDescriptor());
            OutputStream newOutputStream = getNewOutputStream();
            String str = this.mBufferPrefix;
            byte[] bytes = str != null ? str.getBytes() : null;
            boolean z = true;
            while (true) {
                try {
                    int i = fileInputStream.read(bArr);
                    if (i <= 0) {
                        this.mThread.isInterrupted();
                        synchronized (this) {
                            this.mComplete = true;
                            notifyAll();
                        }
                        return;
                    }
                    if (bytes == null) {
                        newOutputStream.write(bArr, 0, i);
                    } else {
                        int i2 = 0;
                        int i3 = 0;
                        while (i2 < i) {
                            if (bArr[i2] != 10) {
                                if (i2 > i3) {
                                    newOutputStream.write(bArr, i3, i2 - i3);
                                }
                                if (z) {
                                    newOutputStream.write(bytes);
                                    z = false;
                                }
                                int i4 = i2;
                                do {
                                    i4++;
                                    if (i4 >= i) {
                                        break;
                                    }
                                } while (bArr[i4] != 10);
                                if (i4 < i) {
                                    z = true;
                                }
                                i3 = i2;
                                i2 = i4;
                            }
                            i2++;
                        }
                        if (i > i3) {
                            newOutputStream.write(bArr, i3, i - i3);
                        }
                    }
                } catch (IOException e) {
                    synchronized (this) {
                        this.mFailure = e.toString();
                        notifyAll();
                        return;
                    }
                }
            }
        }
    }
}
