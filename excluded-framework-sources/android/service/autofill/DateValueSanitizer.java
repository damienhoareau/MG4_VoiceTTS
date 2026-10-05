package android.service.autofill;

import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import com.android.internal.util.Preconditions;
import java.util.Date;

/* JADX INFO: loaded from: classes2.dex */
public final class DateValueSanitizer extends InternalSanitizer implements Sanitizer, Parcelable {
    public static final Parcelable.Creator<DateValueSanitizer> CREATOR = new Parcelable.Creator<DateValueSanitizer>() { // from class: android.service.autofill.DateValueSanitizer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateValueSanitizer createFromParcel(Parcel parcel) {
            return new DateValueSanitizer((DateFormat) parcel.readSerializable());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DateValueSanitizer[] newArray(int i) {
            return new DateValueSanitizer[i];
        }
    };
    private static final String TAG = "DateValueSanitizer";
    private final DateFormat mDateFormat;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DateValueSanitizer(DateFormat dateFormat) {
        this.mDateFormat = (DateFormat) Preconditions.checkNotNull(dateFormat);
    }

    @Override // android.service.autofill.InternalSanitizer
    public AutofillValue sanitize(AutofillValue autofillValue) {
        if (autofillValue == null) {
            Log.w(TAG, "sanitize() called with null value");
            return null;
        }
        if (!autofillValue.isDate()) {
            if (Helper.sDebug) {
                Log.d(TAG, autofillValue + " is not a date");
            }
            return null;
        }
        try {
            Date date = new Date(autofillValue.getDateValue());
            String str = this.mDateFormat.format(date);
            if (Helper.sDebug) {
                Log.d(TAG, "Transformed " + date + " to " + str);
            }
            Date date2 = this.mDateFormat.parse(str);
            if (Helper.sDebug) {
                Log.d(TAG, "Sanitized to " + date2);
            }
            return AutofillValue.forDate(date2.getTime());
        } catch (Exception e) {
            Log.w(TAG, "Could not apply " + this.mDateFormat + " to " + autofillValue + ": " + e);
            return null;
        }
    }

    public String toString() {
        if (!Helper.sDebug) {
            return super.toString();
        }
        return "DateValueSanitizer: [dateFormat=" + this.mDateFormat + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this.mDateFormat);
    }
}
