package android.util.apk;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.DirectByteBuffer;

/* JADX INFO: loaded from: classes2.dex */
class MemoryMappedFileDataSource implements DataSource {
    private static final long MEMORY_PAGE_SIZE_BYTES = Os.sysconf(OsConstants._SC_PAGESIZE);
    private final FileDescriptor mFd;
    private final long mFilePosition;
    private final long mSize;

    MemoryMappedFileDataSource(FileDescriptor fileDescriptor, long j, long j2) {
        this.mFd = fileDescriptor;
        this.mFilePosition = j;
        this.mSize = j2;
    }

    @Override // android.util.apk.DataSource
    public long size() {
        return this.mSize;
    }

    @Override // android.util.apk.DataSource
    public void feedIntoDataDigester(DataDigester dataDigester, long j, int i) throws Throwable {
        long jMmap;
        long j2;
        long j3 = this.mFilePosition + j;
        long j4 = MEMORY_PAGE_SIZE_BYTES;
        long j5 = (j3 / j4) * j4;
        int i2 = (int) (j3 - j5);
        long j6 = i + i2;
        try {
            try {
                jMmap = Os.mmap(0L, j6, OsConstants.PROT_READ, OsConstants.MAP_SHARED | OsConstants.MAP_POPULATE, this.mFd, j5);
                try {
                    j2 = j6;
                    try {
                        dataDigester.consume(new DirectByteBuffer(i, jMmap + ((long) i2), this.mFd, (Runnable) null, true));
                        if (jMmap != 0) {
                            try {
                                Os.munmap(jMmap, j2);
                            } catch (ErrnoException unused) {
                            }
                        }
                    } catch (ErrnoException e) {
                        e = e;
                        throw new IOException("Failed to mmap " + j2 + " bytes", e);
                    }
                } catch (ErrnoException e2) {
                    e = e2;
                    j2 = j6;
                } catch (Throwable th) {
                    th = th;
                    if (jMmap != 0) {
                        try {
                            Os.munmap(jMmap, j6);
                        } catch (ErrnoException unused2) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (ErrnoException e3) {
            e = e3;
            j2 = j6;
        } catch (Throwable th3) {
            th = th3;
            jMmap = 0;
        }
    }
}
