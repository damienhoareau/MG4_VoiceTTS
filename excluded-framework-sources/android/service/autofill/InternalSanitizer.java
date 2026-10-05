package android.service.autofill;

import android.os.Parcelable;
import android.view.autofill.AutofillValue;

/* JADX INFO: loaded from: classes2.dex */
public abstract class InternalSanitizer implements Sanitizer, Parcelable {
    public abstract AutofillValue sanitize(AutofillValue autofillValue);
}
