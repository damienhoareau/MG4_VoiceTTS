package android.service.autofill;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.autofill.Helper;
import com.android.internal.util.Preconditions;

/* JADX INFO: loaded from: classes2.dex */
final class NegationValidator extends InternalValidator {
    public static final Parcelable.Creator<NegationValidator> CREATOR = new Parcelable.Creator<NegationValidator>() { // from class: android.service.autofill.NegationValidator.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NegationValidator createFromParcel(Parcel parcel) {
            return new NegationValidator((InternalValidator) parcel.readParcelable(null));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NegationValidator[] newArray(int i) {
            return new NegationValidator[i];
        }
    };
    private final InternalValidator mValidator;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    NegationValidator(InternalValidator internalValidator) {
        this.mValidator = (InternalValidator) Preconditions.checkNotNull(internalValidator);
    }

    @Override // android.service.autofill.InternalValidator
    public boolean isValid(ValueFinder valueFinder) {
        return !this.mValidator.isValid(valueFinder);
    }

    public String toString() {
        if (!Helper.sDebug) {
            return super.toString();
        }
        return "NegationValidator: [validator=" + this.mValidator + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mValidator, i);
    }
}
