package android.hardware.camera2.params;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public final class VendorTagDescriptor implements Parcelable {
    public static final Parcelable.Creator<VendorTagDescriptor> CREATOR = new Parcelable.Creator<VendorTagDescriptor>() { // from class: android.hardware.camera2.params.VendorTagDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VendorTagDescriptor createFromParcel(Parcel parcel) {
            try {
                return new VendorTagDescriptor(parcel);
            } catch (Exception e) {
                Log.e(VendorTagDescriptor.TAG, "Exception creating VendorTagDescriptor from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VendorTagDescriptor[] newArray(int i) {
            return new VendorTagDescriptor[i];
        }
    };
    private static final String TAG = "VendorTagDescriptor";

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private VendorTagDescriptor(Parcel parcel) {
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (parcel == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
    }
}
