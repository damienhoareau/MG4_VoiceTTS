package android.media;

import android.app.backup.FullBackup;
import android.app.job.JobInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MiniThumbFile {
    public static final int BYTES_PER_MINTHUMB = 10000;
    private static final int HEADER_SIZE = 13;
    private static final int MINI_THUMB_DATA_FILE_VERSION = 4;
    private static final String TAG = "MiniThumbFile";
    private static final Hashtable<String, MiniThumbFile> sThumbFiles = new Hashtable<>();
    private FileChannel mChannel;
    private RandomAccessFile mMiniThumbFile;
    private Uri mUri;
    private ByteBuffer mBuffer = ByteBuffer.allocateDirect(10000);
    private ByteBuffer mEmptyBuffer = ByteBuffer.allocateDirect(10000);

    public static synchronized void reset() {
        Iterator<MiniThumbFile> it = sThumbFiles.values().iterator();
        while (it.hasNext()) {
            it.next().deactivate();
        }
        sThumbFiles.clear();
    }

    public static synchronized MiniThumbFile instance(Uri uri) {
        MiniThumbFile miniThumbFile;
        String str = uri.getPathSegments().get(1);
        miniThumbFile = sThumbFiles.get(str);
        if (miniThumbFile == null) {
            miniThumbFile = new MiniThumbFile(Uri.parse("content://media/external/" + str + "/media"));
            sThumbFiles.put(str, miniThumbFile);
        }
        return miniThumbFile;
    }

    private String randomAccessFilePath(int i) {
        return (Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails") + "/.thumbdata" + i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + this.mUri.hashCode();
    }

    private void removeOldFile() {
        File file = new File(randomAccessFilePath(3));
        if (file.exists()) {
            try {
                file.delete();
            } catch (SecurityException unused) {
            }
        }
    }

    private RandomAccessFile miniThumbDataFile() {
        if (this.mMiniThumbFile == null) {
            removeOldFile();
            String strRandomAccessFilePath = randomAccessFilePath(4);
            File parentFile = new File(strRandomAccessFilePath).getParentFile();
            if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
                Log.e(TAG, "Unable to create .thumbnails directory " + parentFile.toString());
            }
            File file = new File(strRandomAccessFilePath);
            try {
                try {
                    this.mMiniThumbFile = new RandomAccessFile(file, "rw");
                } catch (IOException unused) {
                }
            } catch (IOException unused2) {
                this.mMiniThumbFile = new RandomAccessFile(file, FullBackup.ROOT_TREE_TOKEN);
            }
            RandomAccessFile randomAccessFile = this.mMiniThumbFile;
            if (randomAccessFile != null) {
                this.mChannel = randomAccessFile.getChannel();
            }
        }
        return this.mMiniThumbFile;
    }

    private MiniThumbFile(Uri uri) {
        this.mUri = uri;
    }

    public synchronized void deactivate() {
        if (this.mMiniThumbFile != null) {
            try {
                this.mMiniThumbFile.close();
                this.mMiniThumbFile = null;
            } catch (IOException unused) {
            }
        }
    }

    public synchronized long getMagic(long j) {
        if (miniThumbDataFile() != null) {
            long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
            FileLock fileLockLock = null;
            try {
                try {
                    this.mBuffer.clear();
                    this.mBuffer.limit(9);
                    fileLockLock = this.mChannel.lock(j2, 9L, true);
                    if (this.mChannel.read(this.mBuffer, j2) == 9) {
                        this.mBuffer.position(0);
                        if (this.mBuffer.get() == 1) {
                            long j3 = this.mBuffer.getLong();
                            if (fileLockLock != null) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused) {
                                }
                            }
                            return j3;
                        }
                    }
                    if (fileLockLock != null) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused2) {
                        }
                    }
                } catch (IOException e) {
                    Log.v(TAG, "Got exception checking file magic: ", e);
                    if (fileLockLock != null) {
                    }
                } catch (RuntimeException e2) {
                    Log.e(TAG, "Got exception when reading magic, id = " + j + ", disk full or mount read-only? " + e2.getClass());
                    if (fileLockLock != null) {
                    }
                }
            } catch (Throwable th) {
                if (fileLockLock != null) {
                    try {
                        fileLockLock.release();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        }
        return 0L;
    }

    public synchronized void eraseMiniThumb(long j) {
        if (miniThumbDataFile() != null) {
            long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
            FileLock fileLockLock = null;
            try {
                try {
                    this.mBuffer.clear();
                    this.mBuffer.limit(9);
                    fileLockLock = this.mChannel.lock(j2, JobInfo.MIN_BACKOFF_MILLIS, false);
                    if (this.mChannel.read(this.mBuffer, j2) == 9) {
                        this.mBuffer.position(0);
                        if (this.mBuffer.get() == 1) {
                            if (this.mBuffer.getLong() == 0) {
                                Log.i(TAG, "no thumbnail for id " + j);
                                if (fileLockLock != null) {
                                    try {
                                        fileLockLock.release();
                                    } catch (IOException unused) {
                                    }
                                }
                                return;
                            }
                            this.mChannel.write(this.mEmptyBuffer, j2);
                        }
                    }
                    if (fileLockLock != null) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused2) {
                        }
                    }
                } catch (Throwable th) {
                    if (0 != 0) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused3) {
                        }
                    }
                    throw th;
                }
            } catch (IOException e) {
                Log.v(TAG, "Got exception checking file magic: ", e);
                if (0 != 0) {
                }
            } catch (RuntimeException e2) {
                Log.e(TAG, "Got exception when reading magic, id = " + j + ", disk full or mount read-only? " + e2.getClass());
                if (0 != 0) {
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:38:0x006f A[EXC_TOP_SPLITTER, PHI: r8
  0x006f: PHI (r8v4 java.nio.channels.FileLock) = (r8v2 java.nio.channels.FileLock), (r8v5 java.nio.channels.FileLock) binds: [B:20:0x006d, B:29:0x0096] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    public synchronized void saveMiniThumbToFile(byte[] bArr, long j, long j2) throws IOException {
        if (miniThumbDataFile() == null) {
            return;
        }
        long j3 = JobInfo.MIN_BACKOFF_MILLIS * j;
        FileLock fileLockLock = null;
        try {
            if (bArr != null) {
                try {
                    if (bArr.length > 9987) {
                        return;
                    }
                    this.mBuffer.clear();
                    this.mBuffer.put((byte) 1);
                    this.mBuffer.putLong(j2);
                    this.mBuffer.putInt(bArr.length);
                    this.mBuffer.put(bArr);
                    this.mBuffer.flip();
                    fileLockLock = this.mChannel.lock(j3, JobInfo.MIN_BACKOFF_MILLIS, false);
                    this.mChannel.write(this.mBuffer, j3);
                    if (fileLockLock != null) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused) {
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "couldn't save mini thumbnail data for " + j + "; ", e);
                    throw e;
                } catch (RuntimeException e2) {
                    Log.e(TAG, "couldn't save mini thumbnail data for " + j + "; disk full or mount read-only? " + e2.getClass());
                    if (fileLockLock != null) {
                        fileLockLock.release();
                    }
                }
            } else if (fileLockLock != null) {
                fileLockLock.release();
            }
        } catch (Throwable th) {
            if (fileLockLock != null) {
                try {
                    fileLockLock.release();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:59:0x0060 A[EXC_TOP_SPLITTER, PHI: r0
  0x0060: PHI (r0v5 java.nio.channels.FileLock) = (r0v3 java.nio.channels.FileLock), (r0v4 java.nio.channels.FileLock), (r0v7 java.nio.channels.FileLock) binds: [B:39:0x0090, B:43:0x00b1, B:26:0x005e] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:61:0x00ba A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    public synchronized byte[] getMiniThumbFromFile(long j, byte[] bArr) {
        FileLock fileLockLock;
        RandomAccessFile randomAccessFileMiniThumbDataFile = miniThumbDataFile();
        FileLock fileLock = 0;
        if (randomAccessFileMiniThumbDataFile == null) {
            return null;
        }
        long j2 = JobInfo.MIN_BACKOFF_MILLIS * j;
        try {
            try {
                this.mBuffer.clear();
                fileLockLock = this.mChannel.lock(j2, JobInfo.MIN_BACKOFF_MILLIS, true);
                try {
                    int i = this.mChannel.read(this.mBuffer, j2);
                    if (i > 13) {
                        this.mBuffer.position(0);
                        byte b = this.mBuffer.get();
                        long j3 = this.mBuffer.getLong();
                        int i2 = this.mBuffer.getInt();
                        if (i >= i2 + 13 && i2 != 0 && j3 != 0 && b == 1 && bArr.length >= i2) {
                            this.mBuffer.get(bArr, 0, i2);
                            if (fileLockLock != null) {
                                try {
                                    fileLockLock.release();
                                } catch (IOException unused) {
                                }
                            }
                            return bArr;
                        }
                    }
                    if (fileLockLock != null) {
                        try {
                            fileLockLock.release();
                        } catch (IOException unused2) {
                        }
                    }
                } catch (IOException e) {
                    e = e;
                    Log.w(TAG, "got exception when reading thumbnail id=" + j + ", exception: " + e);
                    if (fileLockLock != null) {
                        fileLockLock.release();
                    }
                } catch (RuntimeException e2) {
                    e = e2;
                    Log.e(TAG, "Got exception when reading thumbnail, id = " + j + ", disk full or mount read-only? " + e.getClass());
                    if (fileLockLock != null) {
                        fileLockLock.release();
                    }
                }
            } catch (Throwable th) {
                th = th;
                fileLock = randomAccessFileMiniThumbDataFile;
                if (fileLock != 0) {
                    try {
                        fileLock.release();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            fileLockLock = null;
        } catch (RuntimeException e4) {
            e = e4;
            fileLockLock = null;
        } catch (Throwable th2) {
            th = th2;
            if (fileLock != 0) {
                fileLock.release();
            }
            throw th;
        }
        return null;
    }
}
