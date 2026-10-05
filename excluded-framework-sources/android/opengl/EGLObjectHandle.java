package android.opengl;

import com.android.internal.logging.nano.MetricsProto;

/* JADX INFO: loaded from: classes2.dex */
public abstract class EGLObjectHandle {
    private final long mHandle;

    @Deprecated
    protected EGLObjectHandle(int i) {
        this.mHandle = i;
    }

    protected EGLObjectHandle(long j) {
        this.mHandle = j;
    }

    @Deprecated
    public int getHandle() {
        long j = this.mHandle;
        if ((4294967295L & j) == j) {
            return (int) j;
        }
        throw new UnsupportedOperationException();
    }

    public long getNativeHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        long j = this.mHandle;
        return MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + ((int) (j ^ (j >>> 32)));
    }
}
