package com.android.internal.midi;

import android.media.midi.MidiReceiver;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class MidiFramer extends MidiReceiver {
    public String TAG = "MidiFramer";
    private byte[] mBuffer = new byte[3];
    private int mCount;
    private boolean mInSysEx;
    private int mNeeded;
    private MidiReceiver mReceiver;
    private byte mRunningStatus;

    public MidiFramer(MidiReceiver midiReceiver) {
        this.mReceiver = midiReceiver;
    }

    public static String formatMidiData(byte[] bArr, int i, int i2) {
        String str = "MIDI+" + i + " : ";
        for (int i3 = 0; i3 < i2; i3++) {
            str = str + String.format("0x%02X, ", Byte.valueOf(bArr[i + i3]));
        }
        return str;
    }

    @Override // android.media.midi.MidiReceiver
    public void onSend(byte[] bArr, int i, int i2, long j) throws IOException {
        int i3 = this.mInSysEx ? i : -1;
        int i4 = i;
        for (int i5 = 0; i5 < i2; i5++) {
            byte b = bArr[i4];
            int i6 = b & 255;
            if (i6 >= 128) {
                if (i6 < 240) {
                    this.mRunningStatus = b;
                    this.mCount = 1;
                    this.mNeeded = MidiConstants.getBytesPerMessage(b) - 1;
                } else if (i6 >= 248) {
                    if (this.mInSysEx) {
                        this.mReceiver.send(bArr, i3, i4 - i3, j);
                        i3 = i4 + 1;
                    }
                    this.mReceiver.send(bArr, i4, 1, j);
                    i3 = i3;
                } else if (i6 == 240) {
                    this.mInSysEx = true;
                    i3 = i4;
                } else if (i6 == 247) {
                    if (this.mInSysEx) {
                        this.mReceiver.send(bArr, i3, (i4 - i3) + 1, j);
                        this.mInSysEx = false;
                        i3 = -1;
                    }
                } else {
                    this.mBuffer[0] = b;
                    this.mRunningStatus = (byte) 0;
                    this.mCount = 1;
                    this.mNeeded = MidiConstants.getBytesPerMessage(b) - 1;
                }
            } else if (!this.mInSysEx) {
                byte[] bArr2 = this.mBuffer;
                int i7 = this.mCount;
                this.mCount = i7 + 1;
                bArr2[i7] = b;
                int i8 = this.mNeeded - 1;
                this.mNeeded = i8;
                if (i8 == 0) {
                    byte b2 = this.mRunningStatus;
                    if (b2 != 0) {
                        bArr2[0] = b2;
                    }
                    this.mReceiver.send(this.mBuffer, 0, this.mCount, j);
                    this.mNeeded = MidiConstants.getBytesPerMessage(this.mBuffer[0]) - 1;
                    this.mCount = 1;
                }
            }
            i4++;
        }
        if (i3 < 0 || i3 >= i4) {
            return;
        }
        this.mReceiver.send(bArr, i3, i4 - i3, j);
    }
}
