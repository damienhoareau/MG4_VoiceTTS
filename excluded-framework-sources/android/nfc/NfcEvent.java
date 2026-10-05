package android.nfc;

import com.android.internal.midi.MidiConstants;

/* JADX INFO: loaded from: classes2.dex */
public final class NfcEvent {
    public final NfcAdapter nfcAdapter;
    public final int peerLlcpMajorVersion;
    public final int peerLlcpMinorVersion;

    NfcEvent(NfcAdapter nfcAdapter, byte b) {
        this.nfcAdapter = nfcAdapter;
        this.peerLlcpMajorVersion = (b & 240) >> 4;
        this.peerLlcpMinorVersion = b & MidiConstants.STATUS_CHANNEL_MASK;
    }
}
