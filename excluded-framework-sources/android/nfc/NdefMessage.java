package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public final class NdefMessage implements Parcelable {
    public static final Parcelable.Creator<NdefMessage> CREATOR = new Parcelable.Creator<NdefMessage>() { // from class: android.nfc.NdefMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NdefMessage createFromParcel(Parcel parcel) {
            NdefRecord[] ndefRecordArr = new NdefRecord[parcel.readInt()];
            parcel.readTypedArray(ndefRecordArr, NdefRecord.CREATOR);
            return new NdefMessage(ndefRecordArr);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NdefMessage[] newArray(int i) {
            return new NdefMessage[i];
        }
    };
    private final NdefRecord[] mRecords;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NdefMessage(byte[] bArr) throws FormatException {
        if (bArr == null) {
            throw new NullPointerException("data is null");
        }
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
        this.mRecords = NdefRecord.parse(byteBufferWrap, false);
        if (byteBufferWrap.remaining() > 0) {
            throw new FormatException("trailing data");
        }
    }

    public NdefMessage(NdefRecord ndefRecord, NdefRecord... ndefRecordArr) {
        if (ndefRecord == null) {
            throw new NullPointerException("record cannot be null");
        }
        for (NdefRecord ndefRecord2 : ndefRecordArr) {
            if (ndefRecord2 == null) {
                throw new NullPointerException("record cannot be null");
            }
        }
        NdefRecord[] ndefRecordArr2 = new NdefRecord[ndefRecordArr.length + 1];
        this.mRecords = ndefRecordArr2;
        ndefRecordArr2[0] = ndefRecord;
        System.arraycopy(ndefRecordArr, 0, ndefRecordArr2, 1, ndefRecordArr.length);
    }

    public NdefMessage(NdefRecord[] ndefRecordArr) {
        if (ndefRecordArr.length < 1) {
            throw new IllegalArgumentException("must have at least one record");
        }
        for (NdefRecord ndefRecord : ndefRecordArr) {
            if (ndefRecord == null) {
                throw new NullPointerException("records cannot contain null");
            }
        }
        this.mRecords = ndefRecordArr;
    }

    public NdefRecord[] getRecords() {
        return this.mRecords;
    }

    public int getByteArrayLength() {
        int byteLength = 0;
        for (NdefRecord ndefRecord : this.mRecords) {
            byteLength += ndefRecord.getByteLength();
        }
        return byteLength;
    }

    public byte[] toByteArray() {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(getByteArrayLength());
        int i = 0;
        while (i < this.mRecords.length) {
            boolean z = true;
            boolean z2 = i == 0;
            if (i != this.mRecords.length - 1) {
                z = false;
            }
            this.mRecords[i].writeToByteBuffer(byteBufferAllocate, z2, z);
            i++;
        }
        return byteBufferAllocate.array();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRecords.length);
        parcel.writeTypedArray(this.mRecords, i);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mRecords);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            return Arrays.equals(this.mRecords, ((NdefMessage) obj).mRecords);
        }
        return false;
    }

    public String toString() {
        return "NdefMessage " + Arrays.toString(this.mRecords);
    }
}
