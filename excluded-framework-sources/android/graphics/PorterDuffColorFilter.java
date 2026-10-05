package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PorterDuffColorFilter extends ColorFilter {
    private int mColor;
    private PorterDuff.Mode mMode;

    private static native long native_CreatePorterDuffFilter(int i, int i2);

    public PorterDuffColorFilter(int i, PorterDuff.Mode mode) {
        this.mColor = i;
        this.mMode = mode;
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int i) {
        if (this.mColor != i) {
            this.mColor = i;
            discardNativeInstance();
        }
    }

    public PorterDuff.Mode getMode() {
        return this.mMode;
    }

    public void setMode(PorterDuff.Mode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode must be non-null");
        }
        this.mMode = mode;
        discardNativeInstance();
    }

    @Override // android.graphics.ColorFilter
    long createNativeInstance() {
        return native_CreatePorterDuffFilter(this.mColor, this.mMode.nativeInt);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PorterDuffColorFilter porterDuffColorFilter = (PorterDuffColorFilter) obj;
        return this.mColor == porterDuffColorFilter.mColor && this.mMode.nativeInt == porterDuffColorFilter.mMode.nativeInt;
    }

    public int hashCode() {
        return (this.mMode.hashCode() * 31) + this.mColor;
    }
}
