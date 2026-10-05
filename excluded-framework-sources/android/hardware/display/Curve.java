package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class Curve implements Parcelable {
    public static final Parcelable.Creator<Curve> CREATOR = new Parcelable.Creator<Curve>() { // from class: android.hardware.display.Curve.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Curve createFromParcel(Parcel parcel) {
            return new Curve(parcel.createFloatArray(), parcel.createFloatArray());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Curve[] newArray(int i) {
            return new Curve[i];
        }
    };
    private final float[] mX;
    private final float[] mY;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Curve(float[] fArr, float[] fArr2) {
        this.mX = fArr;
        this.mY = fArr2;
    }

    public float[] getX() {
        return this.mX;
    }

    public float[] getY() {
        return this.mY;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloatArray(this.mX);
        parcel.writeFloatArray(this.mY);
    }
}
