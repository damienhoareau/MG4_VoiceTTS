package android.location;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.security.InvalidParameterException;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class GpsNavigationMessageEvent implements Parcelable {
    public static final Parcelable.Creator<GpsNavigationMessageEvent> CREATOR = new Parcelable.Creator<GpsNavigationMessageEvent>() { // from class: android.location.GpsNavigationMessageEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GpsNavigationMessageEvent createFromParcel(Parcel parcel) {
            return new GpsNavigationMessageEvent((GpsNavigationMessage) parcel.readParcelable(getClass().getClassLoader()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GpsNavigationMessageEvent[] newArray(int i) {
            return new GpsNavigationMessageEvent[i];
        }
    };
    public static int STATUS_GPS_LOCATION_DISABLED = 2;
    public static int STATUS_NOT_SUPPORTED = 0;
    public static int STATUS_READY = 1;
    private final GpsNavigationMessage mNavigationMessage;

    @SystemApi
    public interface Listener {
        void onGpsNavigationMessageReceived(GpsNavigationMessageEvent gpsNavigationMessageEvent);

        void onStatusChanged(int i);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public GpsNavigationMessageEvent(GpsNavigationMessage gpsNavigationMessage) {
        if (gpsNavigationMessage == null) {
            throw new InvalidParameterException("Parameter 'message' must not be null.");
        }
        this.mNavigationMessage = gpsNavigationMessage;
    }

    public GpsNavigationMessage getNavigationMessage() {
        return this.mNavigationMessage;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mNavigationMessage, i);
    }

    public String toString() {
        return "[ GpsNavigationMessageEvent:\n\n" + this.mNavigationMessage.toString() + "\n]";
    }
}
