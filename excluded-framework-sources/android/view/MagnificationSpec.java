package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pools;

/* JADX INFO: loaded from: classes2.dex */
public class MagnificationSpec implements Parcelable {
    private static final int MAX_POOL_SIZE = 20;
    public float offsetX;
    public float offsetY;
    public float scale = 1.0f;
    private static final Pools.SynchronizedPool<MagnificationSpec> sPool = new Pools.SynchronizedPool<>(20);
    public static final Parcelable.Creator<MagnificationSpec> CREATOR = new Parcelable.Creator<MagnificationSpec>() { // from class: android.view.MagnificationSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MagnificationSpec[] newArray(int i) {
            return new MagnificationSpec[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MagnificationSpec createFromParcel(Parcel parcel) {
            MagnificationSpec magnificationSpecObtain = MagnificationSpec.obtain();
            magnificationSpecObtain.initFromParcel(parcel);
            return magnificationSpecObtain;
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private MagnificationSpec() {
    }

    public void initialize(float f, float f2, float f3) {
        if (f < 1.0f) {
            throw new IllegalArgumentException("Scale must be greater than or equal to one!");
        }
        this.scale = f;
        this.offsetX = f2;
        this.offsetY = f3;
    }

    public boolean isNop() {
        return this.scale == 1.0f && this.offsetX == 0.0f && this.offsetY == 0.0f;
    }

    public static MagnificationSpec obtain(MagnificationSpec magnificationSpec) {
        MagnificationSpec magnificationSpecObtain = obtain();
        magnificationSpecObtain.scale = magnificationSpec.scale;
        magnificationSpecObtain.offsetX = magnificationSpec.offsetX;
        magnificationSpecObtain.offsetY = magnificationSpec.offsetY;
        return magnificationSpecObtain;
    }

    public static MagnificationSpec obtain() {
        MagnificationSpec magnificationSpecAcquire = sPool.acquire();
        return magnificationSpecAcquire != null ? magnificationSpecAcquire : new MagnificationSpec();
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    public void clear() {
        this.scale = 1.0f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
    }

    public void setTo(MagnificationSpec magnificationSpec) {
        this.scale = magnificationSpec.scale;
        this.offsetX = magnificationSpec.offsetX;
        this.offsetY = magnificationSpec.offsetY;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.scale);
        parcel.writeFloat(this.offsetX);
        parcel.writeFloat(this.offsetY);
        recycle();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MagnificationSpec magnificationSpec = (MagnificationSpec) obj;
        return this.scale == magnificationSpec.scale && this.offsetX == magnificationSpec.offsetX && this.offsetY == magnificationSpec.offsetY;
    }

    public int hashCode() {
        float f = this.scale;
        int iFloatToIntBits = (f != 0.0f ? Float.floatToIntBits(f) : 0) * 31;
        float f2 = this.offsetX;
        int iFloatToIntBits2 = (iFloatToIntBits + (f2 != 0.0f ? Float.floatToIntBits(f2) : 0)) * 31;
        float f3 = this.offsetY;
        return iFloatToIntBits2 + (f3 != 0.0f ? Float.floatToIntBits(f3) : 0);
    }

    public String toString() {
        return "<scale:" + Float.toString(this.scale) + ",offsetX:" + Float.toString(this.offsetX) + ",offsetY:" + Float.toString(this.offsetY) + ">";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.scale = parcel.readFloat();
        this.offsetX = parcel.readFloat();
        this.offsetY = parcel.readFloat();
    }
}
