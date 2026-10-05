package android.util;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class MemoryIntArray implements Parcelable, Closeable {
    public static final Parcelable.Creator<MemoryIntArray> CREATOR = new Parcelable.Creator<MemoryIntArray>() { // from class: android.util.MemoryIntArray.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MemoryIntArray createFromParcel(Parcel parcel) {
            try {
                return new MemoryIntArray(parcel);
            } catch (IOException unused) {
                throw new IllegalArgumentException("Error unparceling MemoryIntArray");
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MemoryIntArray[] newArray(int i) {
            return new MemoryIntArray[i];
        }
    };
    private static final int MAX_SIZE = 1024;
    private static final String TAG = "MemoryIntArray";
    private final dalvik.system.CloseGuard mCloseGuard;
    private int mFd;
    private final boolean mIsOwner;
    private final long mMemoryAddr;

    public static int getMaxSize() {
        return 1024;
    }

    private native void nativeClose(int i, long j, boolean z);

    private native int nativeCreate(String str, int i);

    private native int nativeGet(int i, long j, int i2);

    private native long nativeOpen(int i, boolean z);

    private native void nativeSet(int i, long j, int i2, int i3);

    private native int nativeSize(int i);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 1;
    }

    public MemoryIntArray(int i) throws IOException {
        this.mCloseGuard = dalvik.system.CloseGuard.get();
        this.mFd = -1;
        if (i > 1024) {
            throw new IllegalArgumentException("Max size is 1024");
        }
        this.mIsOwner = true;
        int iNativeCreate = nativeCreate(UUID.randomUUID().toString(), i);
        this.mFd = iNativeCreate;
        this.mMemoryAddr = nativeOpen(iNativeCreate, this.mIsOwner);
        this.mCloseGuard.open("close");
    }

    private MemoryIntArray(Parcel parcel) throws IOException {
        this.mCloseGuard = dalvik.system.CloseGuard.get();
        this.mFd = -1;
        this.mIsOwner = false;
        ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) parcel.readParcelable(null);
        if (parcelFileDescriptor == null) {
            throw new IOException("No backing file descriptor");
        }
        int iDetachFd = parcelFileDescriptor.detachFd();
        this.mFd = iDetachFd;
        this.mMemoryAddr = nativeOpen(iDetachFd, this.mIsOwner);
        this.mCloseGuard.open("close");
    }

    public boolean isWritable() {
        enforceNotClosed();
        return this.mIsOwner;
    }

    public int get(int i) throws IOException {
        enforceNotClosed();
        enforceValidIndex(i);
        return nativeGet(this.mFd, this.mMemoryAddr, i);
    }

    public void set(int i, int i2) throws IOException {
        enforceNotClosed();
        enforceWritable();
        enforceValidIndex(i);
        nativeSet(this.mFd, this.mMemoryAddr, i, i2);
    }

    public int size() throws IOException {
        enforceNotClosed();
        return nativeSize(this.mFd);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (isClosed()) {
            return;
        }
        nativeClose(this.mFd, this.mMemoryAddr, this.mIsOwner);
        this.mFd = -1;
        this.mCloseGuard.close();
    }

    public boolean isClosed() {
        return this.mFd == -1;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            IoUtils.closeQuietly(this);
        } finally {
            super.finalize();
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        ParcelFileDescriptor parcelFileDescriptorAdoptFd = ParcelFileDescriptor.adoptFd(this.mFd);
        try {
            parcel.writeParcelable(parcelFileDescriptorAdoptFd, i & (-2));
        } finally {
            parcelFileDescriptorAdoptFd.detachFd();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return getClass() == obj.getClass() && this.mFd == ((MemoryIntArray) obj).mFd;
    }

    public int hashCode() {
        return this.mFd;
    }

    private void enforceNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cannot interact with a closed instance");
        }
    }

    private void enforceValidIndex(int i) throws IOException {
        int size = size();
        if (i < 0 || i > size - 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append(" not between 0 and ");
            sb.append(size - 1);
            throw new IndexOutOfBoundsException(sb.toString());
        }
    }

    private void enforceWritable() {
        if (!isWritable()) {
            throw new UnsupportedOperationException("array is not writable");
        }
    }
}
