package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public final class ConnectionRequest implements Parcelable {
    public static final Parcelable.Creator<ConnectionRequest> CREATOR = new Parcelable.Creator<ConnectionRequest>() { // from class: android.telecom.ConnectionRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConnectionRequest createFromParcel(Parcel parcel) {
            return new ConnectionRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConnectionRequest[] newArray(int i) {
            return new ConnectionRequest[i];
        }
    };
    private final PhoneAccountHandle mAccountHandle;
    private final Uri mAddress;
    private final Bundle mExtras;
    private final ParcelFileDescriptor mRttPipeFromInCall;
    private final ParcelFileDescriptor mRttPipeToInCall;
    private Connection.RttTextStream mRttTextStream;
    private final boolean mShouldShowIncomingCallUi;
    private final String mTelecomCallId;
    private final int mVideoState;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static final class Builder {
        private PhoneAccountHandle mAccountHandle;
        private Uri mAddress;
        private Bundle mExtras;
        private ParcelFileDescriptor mRttPipeFromInCall;
        private ParcelFileDescriptor mRttPipeToInCall;
        private String mTelecomCallId;
        private int mVideoState = 0;
        private boolean mShouldShowIncomingCallUi = false;

        public Builder setAccountHandle(PhoneAccountHandle phoneAccountHandle) {
            this.mAccountHandle = phoneAccountHandle;
            return this;
        }

        public Builder setAddress(Uri uri) {
            this.mAddress = uri;
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.mExtras = bundle;
            return this;
        }

        public Builder setVideoState(int i) {
            this.mVideoState = i;
            return this;
        }

        public Builder setTelecomCallId(String str) {
            this.mTelecomCallId = str;
            return this;
        }

        public Builder setShouldShowIncomingCallUi(boolean z) {
            this.mShouldShowIncomingCallUi = z;
            return this;
        }

        public Builder setRttPipeFromInCall(ParcelFileDescriptor parcelFileDescriptor) {
            this.mRttPipeFromInCall = parcelFileDescriptor;
            return this;
        }

        public Builder setRttPipeToInCall(ParcelFileDescriptor parcelFileDescriptor) {
            this.mRttPipeToInCall = parcelFileDescriptor;
            return this;
        }

        public ConnectionRequest build() {
            return new ConnectionRequest(this.mAccountHandle, this.mAddress, this.mExtras, this.mVideoState, this.mTelecomCallId, this.mShouldShowIncomingCallUi, this.mRttPipeFromInCall, this.mRttPipeToInCall);
        }
    }

    public ConnectionRequest(PhoneAccountHandle phoneAccountHandle, Uri uri, Bundle bundle) {
        this(phoneAccountHandle, uri, bundle, 0, null, false, null, null);
    }

    public ConnectionRequest(PhoneAccountHandle phoneAccountHandle, Uri uri, Bundle bundle, int i) {
        this(phoneAccountHandle, uri, bundle, i, null, false, null, null);
    }

    public ConnectionRequest(PhoneAccountHandle phoneAccountHandle, Uri uri, Bundle bundle, int i, String str, boolean z) {
        this(phoneAccountHandle, uri, bundle, i, str, z, null, null);
    }

    private ConnectionRequest(PhoneAccountHandle phoneAccountHandle, Uri uri, Bundle bundle, int i, String str, boolean z, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2) {
        this.mAccountHandle = phoneAccountHandle;
        this.mAddress = uri;
        this.mExtras = bundle;
        this.mVideoState = i;
        this.mTelecomCallId = str;
        this.mShouldShowIncomingCallUi = z;
        this.mRttPipeFromInCall = parcelFileDescriptor;
        this.mRttPipeToInCall = parcelFileDescriptor2;
    }

    private ConnectionRequest(Parcel parcel) {
        this.mAccountHandle = (PhoneAccountHandle) parcel.readParcelable(getClass().getClassLoader());
        this.mAddress = (Uri) parcel.readParcelable(getClass().getClassLoader());
        this.mExtras = (Bundle) parcel.readParcelable(getClass().getClassLoader());
        this.mVideoState = parcel.readInt();
        this.mTelecomCallId = parcel.readString();
        this.mShouldShowIncomingCallUi = parcel.readInt() == 1;
        this.mRttPipeFromInCall = (ParcelFileDescriptor) parcel.readParcelable(getClass().getClassLoader());
        this.mRttPipeToInCall = (ParcelFileDescriptor) parcel.readParcelable(getClass().getClassLoader());
    }

    public PhoneAccountHandle getAccountHandle() {
        return this.mAccountHandle;
    }

    public Uri getAddress() {
        return this.mAddress;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public String getTelecomCallId() {
        return this.mTelecomCallId;
    }

    public boolean shouldShowIncomingCallUi() {
        return this.mShouldShowIncomingCallUi;
    }

    public ParcelFileDescriptor getRttPipeToInCall() {
        return this.mRttPipeToInCall;
    }

    public ParcelFileDescriptor getRttPipeFromInCall() {
        return this.mRttPipeFromInCall;
    }

    public Connection.RttTextStream getRttTextStream() {
        if (!isRequestingRtt()) {
            return null;
        }
        if (this.mRttTextStream == null) {
            this.mRttTextStream = new Connection.RttTextStream(this.mRttPipeToInCall, this.mRttPipeFromInCall);
        }
        return this.mRttTextStream;
    }

    public boolean isRequestingRtt() {
        return (this.mRttPipeFromInCall == null || this.mRttPipeToInCall == null) ? false : true;
    }

    public String toString() {
        Object logSafePhoneNumber;
        Object[] objArr = new Object[2];
        Uri uri = this.mAddress;
        if (uri == null) {
            logSafePhoneNumber = Uri.EMPTY;
        } else {
            logSafePhoneNumber = Connection.toLogSafePhoneNumber(uri.toString());
        }
        objArr[0] = logSafePhoneNumber;
        Object obj = this.mExtras;
        if (obj == null) {
            obj = "";
        }
        objArr[1] = obj;
        return String.format("ConnectionRequest %s %s", objArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mAccountHandle, 0);
        parcel.writeParcelable(this.mAddress, 0);
        parcel.writeParcelable(this.mExtras, 0);
        parcel.writeInt(this.mVideoState);
        parcel.writeString(this.mTelecomCallId);
        parcel.writeInt(this.mShouldShowIncomingCallUi ? 1 : 0);
        parcel.writeParcelable(this.mRttPipeFromInCall, 0);
        parcel.writeParcelable(this.mRttPipeToInCall, 0);
    }
}
