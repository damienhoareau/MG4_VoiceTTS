package com.android.internal.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.service.gatekeeper.GateKeeperResponse;
import android.util.Slog;

/* JADX INFO: loaded from: classes3.dex */
public final class VerifyCredentialResponse implements Parcelable {
    public static final int RESPONSE_ERROR = -1;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_RETRY = 1;
    private static final String TAG = "VerifyCredentialResponse";
    private byte[] mPayload;
    private int mResponseCode;
    private int mTimeout;
    public static final VerifyCredentialResponse OK = new VerifyCredentialResponse();
    public static final VerifyCredentialResponse ERROR = new VerifyCredentialResponse(-1, 0, null);
    public static final Parcelable.Creator<VerifyCredentialResponse> CREATOR = new Parcelable.Creator<VerifyCredentialResponse>() { // from class: com.android.internal.widget.VerifyCredentialResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Parcelable.Creator
        public VerifyCredentialResponse createFromParcel(Parcel parcel) {
            int i;
            int i2 = parcel.readInt();
            VerifyCredentialResponse verifyCredentialResponse = new VerifyCredentialResponse(i2, 0, null);
            if (i2 == 1) {
                verifyCredentialResponse.setTimeout(parcel.readInt());
            } else if (i2 == 0 && (i = parcel.readInt()) > 0) {
                byte[] bArr = new byte[i];
                parcel.readByteArray(bArr);
                verifyCredentialResponse.setPayload(bArr);
            }
            return verifyCredentialResponse;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerifyCredentialResponse[] newArray(int i) {
            return new VerifyCredentialResponse[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public VerifyCredentialResponse() {
        this.mResponseCode = 0;
        this.mPayload = null;
    }

    public VerifyCredentialResponse(byte[] bArr) {
        this.mPayload = bArr;
        this.mResponseCode = 0;
    }

    public VerifyCredentialResponse(int i) {
        this.mTimeout = i;
        this.mResponseCode = 1;
        this.mPayload = null;
    }

    private VerifyCredentialResponse(int i, int i2, byte[] bArr) {
        this.mResponseCode = i;
        this.mTimeout = i2;
        this.mPayload = bArr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mResponseCode);
        int i2 = this.mResponseCode;
        if (i2 == 1) {
            parcel.writeInt(this.mTimeout);
            return;
        }
        if (i2 == 0) {
            byte[] bArr = this.mPayload;
            if (bArr != null) {
                parcel.writeInt(bArr.length);
                parcel.writeByteArray(this.mPayload);
            } else {
                parcel.writeInt(0);
            }
        }
    }

    public byte[] getPayload() {
        return this.mPayload;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimeout(int i) {
        this.mTimeout = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPayload(byte[] bArr) {
        this.mPayload = bArr;
    }

    public VerifyCredentialResponse stripPayload() {
        return new VerifyCredentialResponse(this.mResponseCode, this.mTimeout, new byte[0]);
    }

    public static VerifyCredentialResponse fromGateKeeperResponse(GateKeeperResponse gateKeeperResponse) {
        int responseCode = gateKeeperResponse.getResponseCode();
        if (responseCode == 1) {
            return new VerifyCredentialResponse(gateKeeperResponse.getTimeout());
        }
        if (responseCode == 0) {
            byte[] payload = gateKeeperResponse.getPayload();
            if (payload == null) {
                Slog.e(TAG, "verifyChallenge response had no associated payload");
                return ERROR;
            }
            return new VerifyCredentialResponse(payload);
        }
        return ERROR;
    }
}
