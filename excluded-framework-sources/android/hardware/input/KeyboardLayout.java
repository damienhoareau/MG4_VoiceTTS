package android.hardware.input;

import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class KeyboardLayout implements Parcelable, Comparable<KeyboardLayout> {
    public static final Parcelable.Creator<KeyboardLayout> CREATOR = new Parcelable.Creator<KeyboardLayout>() { // from class: android.hardware.input.KeyboardLayout.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout createFromParcel(Parcel parcel) {
            return new KeyboardLayout(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout[] newArray(int i) {
            return new KeyboardLayout[i];
        }
    };
    private final String mCollection;
    private final String mDescriptor;
    private final String mLabel;
    private final LocaleList mLocales;
    private final int mPriority;
    private final int mProductId;
    private final int mVendorId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public KeyboardLayout(String str, String str2, String str3, int i, LocaleList localeList, int i2, int i3) {
        this.mDescriptor = str;
        this.mLabel = str2;
        this.mCollection = str3;
        this.mPriority = i;
        this.mLocales = localeList;
        this.mVendorId = i2;
        this.mProductId = i3;
    }

    private KeyboardLayout(Parcel parcel) {
        this.mDescriptor = parcel.readString();
        this.mLabel = parcel.readString();
        this.mCollection = parcel.readString();
        this.mPriority = parcel.readInt();
        this.mLocales = LocaleList.CREATOR.createFromParcel(parcel);
        this.mVendorId = parcel.readInt();
        this.mProductId = parcel.readInt();
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getCollection() {
        return this.mCollection;
    }

    public LocaleList getLocales() {
        return this.mLocales;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getProductId() {
        return this.mProductId;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mDescriptor);
        parcel.writeString(this.mLabel);
        parcel.writeString(this.mCollection);
        parcel.writeInt(this.mPriority);
        this.mLocales.writeToParcel(parcel, 0);
        parcel.writeInt(this.mVendorId);
        parcel.writeInt(this.mProductId);
    }

    @Override // java.lang.Comparable
    public int compareTo(KeyboardLayout keyboardLayout) {
        int iCompare = Integer.compare(keyboardLayout.mPriority, this.mPriority);
        if (iCompare == 0) {
            iCompare = this.mLabel.compareToIgnoreCase(keyboardLayout.mLabel);
        }
        return iCompare == 0 ? this.mCollection.compareToIgnoreCase(keyboardLayout.mCollection) : iCompare;
    }

    public String toString() {
        if (this.mCollection.isEmpty()) {
            return this.mLabel;
        }
        return this.mLabel + " - " + this.mCollection;
    }
}
