package android.nfc.tech;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public final class NfcA extends BasicTagTechnology {
    public static final String EXTRA_ATQA = "atqa";
    public static final String EXTRA_SAK = "sak";
    private static final String TAG = "NFC";
    private byte[] mAtqa;
    private short mSak;

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void connect() throws IOException {
        super.connect();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ Tag getTag() {
        return super.getTag();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ boolean isConnected() {
        return super.isConnected();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void reconnect() throws IOException {
        super.reconnect();
    }

    public static NfcA get(Tag tag) {
        if (!tag.hasTech(1)) {
            return null;
        }
        try {
            return new NfcA(tag);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NfcA(Tag tag) throws RemoteException {
        super(tag, 1);
        Bundle techExtras = tag.getTechExtras(1);
        this.mSak = techExtras.getShort(EXTRA_SAK);
        this.mAtqa = techExtras.getByteArray(EXTRA_ATQA);
    }

    public byte[] getAtqa() {
        return this.mAtqa;
    }

    public short getSak() {
        return this.mSak;
    }

    public byte[] transceive(byte[] bArr) throws IOException {
        return transceive(bArr, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }

    public void setTimeout(int i) {
        try {
            if (this.mTag.getTagService().setTimeout(1, i) == 0) {
            } else {
                throw new IllegalArgumentException("The supplied timeout is not valid");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "NFC service dead", e);
        }
    }

    public int getTimeout() {
        try {
            return this.mTag.getTagService().getTimeout(1);
        } catch (RemoteException e) {
            Log.e(TAG, "NFC service dead", e);
            return 0;
        }
    }
}
