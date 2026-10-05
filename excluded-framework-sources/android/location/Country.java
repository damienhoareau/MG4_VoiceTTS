package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Country implements Parcelable {
    public static final int COUNTRY_SOURCE_LOCALE = 3;
    public static final int COUNTRY_SOURCE_LOCATION = 1;
    public static final int COUNTRY_SOURCE_NETWORK = 0;
    public static final int COUNTRY_SOURCE_SIM = 2;
    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() { // from class: android.location.Country.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Country createFromParcel(Parcel parcel) {
            return new Country(parcel.readString(), parcel.readInt(), parcel.readLong());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Country[] newArray(int i) {
            return new Country[i];
        }
    };
    private final String mCountryIso;
    private int mHashCode;
    private final int mSource;
    private final long mTimestamp;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Country(String str, int i) {
        if (str == null || i < 0 || i > 3) {
            throw new IllegalArgumentException();
        }
        this.mCountryIso = str.toUpperCase(Locale.US);
        this.mSource = i;
        this.mTimestamp = SystemClock.elapsedRealtime();
    }

    private Country(String str, int i, long j) {
        if (str == null || i < 0 || i > 3) {
            throw new IllegalArgumentException();
        }
        this.mCountryIso = str.toUpperCase(Locale.US);
        this.mSource = i;
        this.mTimestamp = j;
    }

    public Country(Country country) {
        this.mCountryIso = country.mCountryIso;
        this.mSource = country.mSource;
        this.mTimestamp = country.mTimestamp;
    }

    public final String getCountryIso() {
        return this.mCountryIso;
    }

    public final int getSource() {
        return this.mSource;
    }

    public final long getTimestamp() {
        return this.mTimestamp;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mCountryIso);
        parcel.writeInt(this.mSource);
        parcel.writeLong(this.mTimestamp);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Country)) {
            return false;
        }
        Country country = (Country) obj;
        return this.mCountryIso.equals(country.getCountryIso()) && this.mSource == country.getSource();
    }

    public int hashCode() {
        if (this.mHashCode == 0) {
            this.mHashCode = ((221 + this.mCountryIso.hashCode()) * 13) + this.mSource;
        }
        return this.mHashCode;
    }

    public boolean equalsIgnoreSource(Country country) {
        return country != null && this.mCountryIso.equals(country.getCountryIso());
    }

    public String toString() {
        return "Country {ISO=" + this.mCountryIso + ", source=" + this.mSource + ", time=" + this.mTimestamp + "}";
    }
}
