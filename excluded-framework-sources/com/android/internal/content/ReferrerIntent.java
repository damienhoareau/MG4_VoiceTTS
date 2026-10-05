package com.android.internal.content;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.logging.nano.MetricsProto;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public class ReferrerIntent extends Intent {
    public static final Parcelable.Creator<ReferrerIntent> CREATOR = new Parcelable.Creator<ReferrerIntent>() { // from class: com.android.internal.content.ReferrerIntent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ReferrerIntent createFromParcel(Parcel parcel) {
            return new ReferrerIntent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ReferrerIntent[] newArray(int i) {
            return new ReferrerIntent[i];
        }
    };
    public final String mReferrer;

    public ReferrerIntent(Intent intent, String str) {
        super(intent);
        this.mReferrer = str;
    }

    @Override // android.content.Intent, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.mReferrer);
    }

    ReferrerIntent(Parcel parcel) {
        readFromParcel(parcel);
        this.mReferrer = parcel.readString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ReferrerIntent)) {
            return false;
        }
        ReferrerIntent referrerIntent = (ReferrerIntent) obj;
        return filterEquals(referrerIntent) && Objects.equals(this.mReferrer, referrerIntent.mReferrer);
    }

    public int hashCode() {
        return ((MetricsProto.MetricsEvent.DIALOG_SUPPORT_PHONE + filterHashCode()) * 31) + Objects.hashCode(this.mReferrer);
    }
}
