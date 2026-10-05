package android.hardware;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class CameraStatus implements Parcelable {
    public static final Parcelable.Creator<CameraStatus> CREATOR = new Parcelable.Creator<CameraStatus>() { // from class: android.hardware.CameraStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CameraStatus createFromParcel(Parcel parcel) {
            CameraStatus cameraStatus = new CameraStatus();
            cameraStatus.readFromParcel(parcel);
            return cameraStatus;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CameraStatus[] newArray(int i) {
            return new CameraStatus[i];
        }
    };
    public String cameraId;
    public int status;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.cameraId);
        parcel.writeInt(this.status);
    }

    public void readFromParcel(Parcel parcel) {
        this.cameraId = parcel.readString();
        this.status = parcel.readInt();
    }
}
