package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceSpecificRegistrationStates implements Parcelable {
    public static final Parcelable.Creator<VoiceSpecificRegistrationStates> CREATOR = new Parcelable.Creator<VoiceSpecificRegistrationStates>() { // from class: android.telephony.VoiceSpecificRegistrationStates.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VoiceSpecificRegistrationStates createFromParcel(Parcel parcel) {
            return new VoiceSpecificRegistrationStates(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VoiceSpecificRegistrationStates[] newArray(int i) {
            return new VoiceSpecificRegistrationStates[i];
        }
    };
    public final boolean cssSupported;
    public final int defaultRoamingIndicator;
    public final int roamingIndicator;
    public final int systemIsInPrl;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    VoiceSpecificRegistrationStates(boolean z, int i, int i2, int i3) {
        this.cssSupported = z;
        this.roamingIndicator = i;
        this.systemIsInPrl = i2;
        this.defaultRoamingIndicator = i3;
    }

    private VoiceSpecificRegistrationStates(Parcel parcel) {
        this.cssSupported = parcel.readBoolean();
        this.roamingIndicator = parcel.readInt();
        this.systemIsInPrl = parcel.readInt();
        this.defaultRoamingIndicator = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this.cssSupported);
        parcel.writeInt(this.roamingIndicator);
        parcel.writeInt(this.systemIsInPrl);
        parcel.writeInt(this.defaultRoamingIndicator);
    }

    public String toString() {
        return "VoiceSpecificRegistrationStates { mCssSupported=" + this.cssSupported + " mRoamingIndicator=" + this.roamingIndicator + " mSystemIsInPrl=" + this.systemIsInPrl + " mDefaultRoamingIndicator=" + this.defaultRoamingIndicator + "}";
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.cssSupported), Integer.valueOf(this.roamingIndicator), Integer.valueOf(this.systemIsInPrl), Integer.valueOf(this.defaultRoamingIndicator));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof VoiceSpecificRegistrationStates)) {
            return false;
        }
        VoiceSpecificRegistrationStates voiceSpecificRegistrationStates = (VoiceSpecificRegistrationStates) obj;
        return this.cssSupported == voiceSpecificRegistrationStates.cssSupported && this.roamingIndicator == voiceSpecificRegistrationStates.roamingIndicator && this.systemIsInPrl == voiceSpecificRegistrationStates.systemIsInPrl && this.defaultRoamingIndicator == voiceSpecificRegistrationStates.defaultRoamingIndicator;
    }
}
