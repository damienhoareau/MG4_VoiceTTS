package android.os;

import android.content.Context;
import android.net.Uri;
import android.util.MathUtils;
import com.android.internal.R;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public abstract class VibrationEffect implements Parcelable {
    public static final int DEFAULT_AMPLITUDE = -1;
    public static final int EFFECT_CLICK = 0;
    public static final int EFFECT_DOUBLE_CLICK = 1;
    public static final int EFFECT_HEAVY_CLICK = 5;
    public static final int EFFECT_POP = 4;
    public static final int EFFECT_THUD = 3;
    public static final int EFFECT_TICK = 2;
    public static final int MAX_AMPLITUDE = 255;
    private static final int PARCEL_TOKEN_EFFECT = 3;
    private static final int PARCEL_TOKEN_ONE_SHOT = 1;
    private static final int PARCEL_TOKEN_WAVEFORM = 2;
    public static final int[] RINGTONES = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static final Parcelable.Creator<VibrationEffect> CREATOR = new Parcelable.Creator<VibrationEffect>() { // from class: android.os.VibrationEffect.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VibrationEffect createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            if (i == 1) {
                return new OneShot(parcel);
            }
            if (i == 2) {
                return new Waveform(parcel);
            }
            if (i == 3) {
                return new Prebaked(parcel);
            }
            throw new IllegalStateException("Unexpected vibration event type token in parcel.");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VibrationEffect[] newArray(int i) {
            return new VibrationEffect[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public abstract long getDuration();

    public abstract void validate();

    public static VibrationEffect createOneShot(long j, int i) {
        OneShot oneShot = new OneShot(j, i);
        oneShot.validate();
        return oneShot;
    }

    public static VibrationEffect createWaveform(long[] jArr, int i) {
        int[] iArr = new int[jArr.length];
        for (int i2 = 0; i2 < jArr.length / 2; i2++) {
            iArr[(i2 * 2) + 1] = -1;
        }
        return createWaveform(jArr, iArr, i);
    }

    public static VibrationEffect createWaveform(long[] jArr, int[] iArr, int i) {
        Waveform waveform = new Waveform(jArr, iArr, i);
        waveform.validate();
        return waveform;
    }

    public static VibrationEffect get(int i) {
        return get(i, true);
    }

    public static VibrationEffect get(int i, boolean z) {
        Prebaked prebaked = new Prebaked(i, z);
        prebaked.validate();
        return prebaked;
    }

    public static VibrationEffect get(Uri uri, Context context) {
        Uri uriUncanonicalize;
        String[] stringArray = context.getResources().getStringArray(R.array.config_ringtoneEffectUris);
        for (int i = 0; i < stringArray.length && i < RINGTONES.length; i++) {
            if (stringArray[i] != null && (uriUncanonicalize = context.getContentResolver().uncanonicalize(Uri.parse(stringArray[i]))) != null && uriUncanonicalize.equals(uri)) {
                return get(RINGTONES[i]);
            }
        }
        return null;
    }

    protected static int scale(int i, float f, int i2) {
        return (int) (MathUtils.pow(i / 255.0f, f) * i2);
    }

    public static class OneShot extends VibrationEffect implements Parcelable {
        public static final Parcelable.Creator<OneShot> CREATOR = new Parcelable.Creator<OneShot>() { // from class: android.os.VibrationEffect.OneShot.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OneShot createFromParcel(Parcel parcel) {
                parcel.readInt();
                return new OneShot(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OneShot[] newArray(int i) {
                return new OneShot[i];
            }
        };
        private final int mAmplitude;
        private final long mDuration;

        public OneShot(Parcel parcel) {
            this.mDuration = parcel.readLong();
            this.mAmplitude = parcel.readInt();
        }

        public OneShot(long j, int i) {
            this.mDuration = j;
            this.mAmplitude = i;
        }

        @Override // android.os.VibrationEffect
        public long getDuration() {
            return this.mDuration;
        }

        public int getAmplitude() {
            return this.mAmplitude;
        }

        public VibrationEffect scale(float f, int i) {
            return new OneShot(this.mDuration, scale(this.mAmplitude, f, i));
        }

        public OneShot resolve(int i) {
            if (i > 255 || i < 0) {
                throw new IllegalArgumentException("Amplitude is negative or greater than MAX_AMPLITUDE");
            }
            return this.mAmplitude == -1 ? new OneShot(this.mDuration, i) : this;
        }

        @Override // android.os.VibrationEffect
        public void validate() {
            int i = this.mAmplitude;
            if (i < -1 || i == 0 || i > 255) {
                throw new IllegalArgumentException("amplitude must either be DEFAULT_AMPLITUDE, or between 1 and 255 inclusive (amplitude=" + this.mAmplitude + ")");
            }
            if (this.mDuration > 0) {
                return;
            }
            throw new IllegalArgumentException("duration must be positive (duration=" + this.mDuration + ")");
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof OneShot)) {
                return false;
            }
            OneShot oneShot = (OneShot) obj;
            return oneShot.mDuration == this.mDuration && oneShot.mAmplitude == this.mAmplitude;
        }

        public int hashCode() {
            return (((int) this.mDuration) * 37) + 17 + (this.mAmplitude * 37);
        }

        public String toString() {
            return "OneShot{mDuration=" + this.mDuration + ", mAmplitude=" + this.mAmplitude + "}";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(1);
            parcel.writeLong(this.mDuration);
            parcel.writeInt(this.mAmplitude);
        }
    }

    public static class Waveform extends VibrationEffect implements Parcelable {
        public static final Parcelable.Creator<Waveform> CREATOR = new Parcelable.Creator<Waveform>() { // from class: android.os.VibrationEffect.Waveform.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Waveform createFromParcel(Parcel parcel) {
                parcel.readInt();
                return new Waveform(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Waveform[] newArray(int i) {
                return new Waveform[i];
            }
        };
        private final int[] mAmplitudes;
        private final int mRepeat;
        private final long[] mTimings;

        public Waveform(Parcel parcel) {
            this(parcel.createLongArray(), parcel.createIntArray(), parcel.readInt());
        }

        public Waveform(long[] jArr, int[] iArr, int i) {
            long[] jArr2 = new long[jArr.length];
            this.mTimings = jArr2;
            System.arraycopy(jArr, 0, jArr2, 0, jArr.length);
            int[] iArr2 = new int[iArr.length];
            this.mAmplitudes = iArr2;
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            this.mRepeat = i;
        }

        public long[] getTimings() {
            return this.mTimings;
        }

        public int[] getAmplitudes() {
            return this.mAmplitudes;
        }

        public int getRepeatIndex() {
            return this.mRepeat;
        }

        @Override // android.os.VibrationEffect
        public long getDuration() {
            if (this.mRepeat >= 0) {
                return Long.MAX_VALUE;
            }
            long j = 0;
            for (long j2 : this.mTimings) {
                j += j2;
            }
            return j;
        }

        public VibrationEffect scale(float f, int i) {
            if (f == 1.0f && i == 255) {
                return new Waveform(this.mTimings, this.mAmplitudes, this.mRepeat);
            }
            int[] iArr = this.mAmplitudes;
            int[] iArrCopyOf = Arrays.copyOf(iArr, iArr.length);
            for (int i2 = 0; i2 < iArrCopyOf.length; i2++) {
                iArrCopyOf[i2] = scale(iArrCopyOf[i2], f, i);
            }
            return new Waveform(this.mTimings, iArrCopyOf, this.mRepeat);
        }

        public Waveform resolve(int i) {
            if (i > 255 || i < 0) {
                throw new IllegalArgumentException("Amplitude is negative or greater than MAX_AMPLITUDE");
            }
            int[] iArr = this.mAmplitudes;
            int[] iArrCopyOf = Arrays.copyOf(iArr, iArr.length);
            for (int i2 = 0; i2 < iArrCopyOf.length; i2++) {
                if (iArrCopyOf[i2] == -1) {
                    iArrCopyOf[i2] = i;
                }
            }
            return new Waveform(this.mTimings, iArrCopyOf, this.mRepeat);
        }

        @Override // android.os.VibrationEffect
        public void validate() {
            long[] jArr = this.mTimings;
            if (jArr.length != this.mAmplitudes.length) {
                throw new IllegalArgumentException("timing and amplitude arrays must be of equal length (timings.length=" + this.mTimings.length + ", amplitudes.length=" + this.mAmplitudes.length + ")");
            }
            if (!hasNonZeroEntry(jArr)) {
                throw new IllegalArgumentException("at least one timing must be non-zero (timings=" + Arrays.toString(this.mTimings) + ")");
            }
            for (long j : this.mTimings) {
                if (j < 0) {
                    throw new IllegalArgumentException("timings must all be >= 0 (timings=" + Arrays.toString(this.mTimings) + ")");
                }
            }
            for (int i : this.mAmplitudes) {
                if (i < -1 || i > 255) {
                    throw new IllegalArgumentException("amplitudes must all be DEFAULT_AMPLITUDE or between 0 and 255 (amplitudes=" + Arrays.toString(this.mAmplitudes) + ")");
                }
            }
            int i2 = this.mRepeat;
            if (i2 < -1 || i2 >= this.mTimings.length) {
                throw new IllegalArgumentException("repeat index must be within the bounds of the timings array (timings.length=" + this.mTimings.length + ", index=" + this.mRepeat + ")");
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Waveform)) {
                return false;
            }
            Waveform waveform = (Waveform) obj;
            return Arrays.equals(this.mTimings, waveform.mTimings) && Arrays.equals(this.mAmplitudes, waveform.mAmplitudes) && this.mRepeat == waveform.mRepeat;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.mTimings) * 37) + 17 + (Arrays.hashCode(this.mAmplitudes) * 37) + (this.mRepeat * 37);
        }

        public String toString() {
            return "Waveform{mTimings=" + Arrays.toString(this.mTimings) + ", mAmplitudes=" + Arrays.toString(this.mAmplitudes) + ", mRepeat=" + this.mRepeat + "}";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(2);
            parcel.writeLongArray(this.mTimings);
            parcel.writeIntArray(this.mAmplitudes);
            parcel.writeInt(this.mRepeat);
        }

        private static boolean hasNonZeroEntry(long[] jArr) {
            for (long j : jArr) {
                if (j != 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Prebaked extends VibrationEffect implements Parcelable {
        public static final Parcelable.Creator<Prebaked> CREATOR = new Parcelable.Creator<Prebaked>() { // from class: android.os.VibrationEffect.Prebaked.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Prebaked createFromParcel(Parcel parcel) {
                parcel.readInt();
                return new Prebaked(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Prebaked[] newArray(int i) {
                return new Prebaked[i];
            }
        };
        private final int mEffectId;
        private int mEffectStrength;
        private final boolean mFallback;

        private static boolean isValidEffectStrength(int i) {
            return i == 0 || i == 1 || i == 2;
        }

        @Override // android.os.VibrationEffect
        public long getDuration() {
            return -1L;
        }

        public Prebaked(Parcel parcel) {
            this(parcel.readInt(), parcel.readByte() != 0);
            this.mEffectStrength = parcel.readInt();
        }

        public Prebaked(int i, boolean z) {
            this.mEffectId = i;
            this.mFallback = z;
            this.mEffectStrength = 1;
        }

        public int getId() {
            return this.mEffectId;
        }

        public boolean shouldFallback() {
            return this.mFallback;
        }

        public void setEffectStrength(int i) {
            if (!isValidEffectStrength(i)) {
                throw new IllegalArgumentException("Invalid effect strength: " + i);
            }
            this.mEffectStrength = i;
        }

        public int getEffectStrength() {
            return this.mEffectStrength;
        }

        @Override // android.os.VibrationEffect
        public void validate() {
            int i = this.mEffectId;
            if (i != 0 && i != 1 && i != 2 && i != 3 && i != 4 && i != 5 && (i < RINGTONES[0] || this.mEffectId > RINGTONES[RINGTONES.length - 1])) {
                throw new IllegalArgumentException("Unknown prebaked effect type (value=" + this.mEffectId + ")");
            }
            if (isValidEffectStrength(this.mEffectStrength)) {
                return;
            }
            throw new IllegalArgumentException("Unknown prebaked effect strength (value=" + this.mEffectStrength + ")");
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Prebaked)) {
                return false;
            }
            Prebaked prebaked = (Prebaked) obj;
            return this.mEffectId == prebaked.mEffectId && this.mFallback == prebaked.mFallback && this.mEffectStrength == prebaked.mEffectStrength;
        }

        public int hashCode() {
            return (this.mEffectId * 37) + 17 + (this.mEffectStrength * 37);
        }

        public String toString() {
            return "Prebaked{mEffectId=" + this.mEffectId + ", mEffectStrength=" + this.mEffectStrength + ", mFallback=" + this.mFallback + "}";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(3);
            parcel.writeInt(this.mEffectId);
            parcel.writeByte(this.mFallback ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.mEffectStrength);
        }
    }
}
