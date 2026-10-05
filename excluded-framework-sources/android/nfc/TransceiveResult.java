package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public final class TransceiveResult implements Parcelable {
    public static final Parcelable.Creator<TransceiveResult> CREATOR = new Parcelable.Creator<TransceiveResult>() { // from class: android.nfc.TransceiveResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TransceiveResult createFromParcel(Parcel parcel) {
            byte[] bArr;
            int i = parcel.readInt();
            if (i == 0) {
                bArr = new byte[parcel.readInt()];
                parcel.readByteArray(bArr);
            } else {
                bArr = null;
            }
            return new TransceiveResult(i, bArr);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TransceiveResult[] newArray(int i) {
            return new TransceiveResult[i];
        }
    };
    public static final int RESULT_EXCEEDED_LENGTH = 3;
    public static final int RESULT_FAILURE = 1;
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_TAGLOST = 2;
    final byte[] mResponseData;
    final int mResult;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TransceiveResult(int i, byte[] bArr) {
        this.mResult = i;
        this.mResponseData = bArr;
    }

    public byte[] getResponseOrThrow() throws IOException {
        int i = this.mResult;
        if (i == 0) {
            return this.mResponseData;
        }
        if (i == 2) {
            throw new TagLostException("Tag was lost.");
        }
        if (i == 3) {
            throw new IOException("Transceive length exceeds supported maximum");
        }
        throw new IOException("Transceive failed");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mResult);
        if (this.mResult == 0) {
            parcel.writeInt(this.mResponseData.length);
            parcel.writeByteArray(this.mResponseData);
        }
    }
}
