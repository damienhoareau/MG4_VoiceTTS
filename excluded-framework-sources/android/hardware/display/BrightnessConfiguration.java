package android.hardware.display;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class BrightnessConfiguration implements Parcelable {
    public static final Parcelable.Creator<BrightnessConfiguration> CREATOR = new Parcelable.Creator<BrightnessConfiguration>() { // from class: android.hardware.display.BrightnessConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BrightnessConfiguration createFromParcel(Parcel parcel) {
            Builder builder = new Builder(parcel.createFloatArray(), parcel.createFloatArray());
            builder.setDescription(parcel.readString());
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BrightnessConfiguration[] newArray(int i) {
            return new BrightnessConfiguration[i];
        }
    };
    private final String mDescription;
    private final float[] mLux;
    private final float[] mNits;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private BrightnessConfiguration(float[] fArr, float[] fArr2, String str) {
        this.mLux = fArr;
        this.mNits = fArr2;
        this.mDescription = str;
    }

    public Pair<float[], float[]> getCurve() {
        float[] fArr = this.mLux;
        float[] fArrCopyOf = Arrays.copyOf(fArr, fArr.length);
        float[] fArr2 = this.mNits;
        return Pair.create(fArrCopyOf, Arrays.copyOf(fArr2, fArr2.length));
    }

    public String getDescription() {
        return this.mDescription;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloatArray(this.mLux);
        parcel.writeFloatArray(this.mNits);
        parcel.writeString(this.mDescription);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("BrightnessConfiguration{[");
        int length = this.mLux.length;
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("(");
            sb.append(this.mLux[i]);
            sb.append(", ");
            sb.append(this.mNits[i]);
            sb.append(")");
        }
        sb.append("], '");
        String str = this.mDescription;
        if (str != null) {
            sb.append(str);
        }
        sb.append("'}");
        return sb.toString();
    }

    public int hashCode() {
        int iHashCode = ((Arrays.hashCode(this.mLux) + 31) * 31) + Arrays.hashCode(this.mNits);
        String str = this.mDescription;
        return str != null ? (iHashCode * 31) + str.hashCode() : iHashCode;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BrightnessConfiguration)) {
            return false;
        }
        BrightnessConfiguration brightnessConfiguration = (BrightnessConfiguration) obj;
        return Arrays.equals(this.mLux, brightnessConfiguration.mLux) && Arrays.equals(this.mNits, brightnessConfiguration.mNits) && Objects.equals(this.mDescription, brightnessConfiguration.mDescription);
    }

    public static class Builder {
        private float[] mCurveLux;
        private float[] mCurveNits;
        private String mDescription;

        public Builder() {
        }

        public Builder(float[] fArr, float[] fArr2) {
            setCurve(fArr, fArr2);
        }

        public Builder setCurve(float[] fArr, float[] fArr2) {
            Preconditions.checkNotNull(fArr);
            Preconditions.checkNotNull(fArr2);
            if (fArr.length == 0 || fArr2.length == 0) {
                throw new IllegalArgumentException("Lux and nits arrays must not be empty");
            }
            if (fArr.length != fArr2.length) {
                throw new IllegalArgumentException("Lux and nits arrays must be the same length");
            }
            if (fArr[0] != 0.0f) {
                throw new IllegalArgumentException("Initial control point must be for 0 lux");
            }
            Preconditions.checkArrayElementsInRange(fArr, 0.0f, Float.MAX_VALUE, "lux");
            Preconditions.checkArrayElementsInRange(fArr2, 0.0f, Float.MAX_VALUE, "nits");
            checkMonotonic(fArr, true, "lux");
            checkMonotonic(fArr2, false, "nits");
            this.mCurveLux = fArr;
            this.mCurveNits = fArr2;
            return this;
        }

        public Builder setDescription(String str) {
            this.mDescription = str;
            return this;
        }

        public BrightnessConfiguration build() {
            if (this.mCurveLux == null || this.mCurveNits == null) {
                throw new IllegalStateException("A curve must be set!");
            }
            return new BrightnessConfiguration(this.mCurveLux, this.mCurveNits, this.mDescription);
        }

        private static void checkMonotonic(float[] fArr, boolean z, String str) {
            if (fArr.length <= 1) {
                return;
            }
            float f = fArr[0];
            for (int i = 1; i < fArr.length; i++) {
                if (f > fArr[i] || (f == fArr[i] && z)) {
                    throw new IllegalArgumentException(str + " values must be " + (z ? "strictly increasing" : "monotonic"));
                }
                f = fArr[i];
            }
        }
    }
}
