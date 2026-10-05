package android.nfc.tech;

import android.nfc.FormatException;
import android.nfc.INfcTag;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
public final class NdefFormatable extends BasicTagTechnology {
    private static final String TAG = "NFC";

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

    public static NdefFormatable get(Tag tag) {
        if (!tag.hasTech(7)) {
            return null;
        }
        try {
            return new NdefFormatable(tag);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public NdefFormatable(Tag tag) throws RemoteException {
        super(tag, 7);
    }

    public void format(NdefMessage ndefMessage) throws IOException, FormatException {
        format(ndefMessage, false);
    }

    public void formatReadOnly(NdefMessage ndefMessage) throws IOException, FormatException {
        format(ndefMessage, true);
    }

    void format(NdefMessage ndefMessage, boolean z) throws IOException, FormatException {
        checkConnected();
        try {
            int serviceHandle = this.mTag.getServiceHandle();
            INfcTag tagService = this.mTag.getTagService();
            int ndef = tagService.formatNdef(serviceHandle, MifareClassic.KEY_DEFAULT);
            if (ndef == -8) {
                throw new FormatException();
            }
            if (ndef == -1) {
                throw new IOException();
            }
            if (ndef != 0) {
                throw new IOException();
            }
            if (!tagService.isNdef(serviceHandle)) {
                throw new IOException();
            }
            if (ndefMessage != null) {
                int iNdefWrite = tagService.ndefWrite(serviceHandle, ndefMessage);
                if (iNdefWrite == -8) {
                    throw new FormatException();
                }
                if (iNdefWrite == -1) {
                    throw new IOException();
                }
                if (iNdefWrite != 0) {
                    throw new IOException();
                }
            }
            if (z) {
                int iNdefMakeReadOnly = tagService.ndefMakeReadOnly(serviceHandle);
                if (iNdefMakeReadOnly == -8) {
                    throw new IOException();
                }
                if (iNdefMakeReadOnly == -1) {
                    throw new IOException();
                }
                if (iNdefMakeReadOnly != 0) {
                    throw new IOException();
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "NFC service dead", e);
        }
    }
}
