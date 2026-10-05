package android.media.midi;

import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.midi.MidiDispatcher;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class MidiOutputPort extends MidiSender implements Closeable {
    private static final String TAG = "MidiOutputPort";
    private IMidiDeviceServer mDeviceServer;
    private final MidiDispatcher mDispatcher;
    private final CloseGuard mGuard;
    private final FileInputStream mInputStream;
    private boolean mIsClosed;
    private final int mPortNumber;
    private final Thread mThread;
    private final IBinder mToken;

    MidiOutputPort(IMidiDeviceServer iMidiDeviceServer, IBinder iBinder, FileDescriptor fileDescriptor, int i) {
        this.mDispatcher = new MidiDispatcher();
        this.mGuard = CloseGuard.get();
        this.mThread = new Thread() { // from class: android.media.midi.MidiOutputPort.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                byte[] bArr = new byte[1024];
                while (true) {
                    try {
                        try {
                            int i2 = MidiOutputPort.this.mInputStream.read(bArr);
                            if (i2 < 0) {
                                break;
                            }
                            int packetType = MidiPortImpl.getPacketType(bArr, i2);
                            if (packetType == 1) {
                                MidiOutputPort.this.mDispatcher.send(bArr, MidiPortImpl.getDataOffset(bArr, i2), MidiPortImpl.getDataSize(bArr, i2), MidiPortImpl.getPacketTimestamp(bArr, i2));
                            } else if (packetType == 2) {
                                MidiOutputPort.this.mDispatcher.flush();
                            } else {
                                Log.e(MidiOutputPort.TAG, "Unknown packet type " + packetType);
                            }
                        } catch (IOException e) {
                            Log.e(MidiOutputPort.TAG, "read failed", e);
                        }
                    } catch (Throwable th) {
                        IoUtils.closeQuietly(MidiOutputPort.this.mInputStream);
                        throw th;
                    }
                }
                IoUtils.closeQuietly(MidiOutputPort.this.mInputStream);
            }
        };
        this.mDeviceServer = iMidiDeviceServer;
        this.mToken = iBinder;
        this.mPortNumber = i;
        this.mInputStream = new ParcelFileDescriptor.AutoCloseInputStream(new ParcelFileDescriptor(fileDescriptor));
        this.mThread.start();
        this.mGuard.open("close");
    }

    MidiOutputPort(FileDescriptor fileDescriptor, int i) {
        this(null, null, fileDescriptor, i);
    }

    public final int getPortNumber() {
        return this.mPortNumber;
    }

    @Override // android.media.midi.MidiSender
    public void onConnect(MidiReceiver midiReceiver) {
        this.mDispatcher.getSender().connect(midiReceiver);
    }

    @Override // android.media.midi.MidiSender
    public void onDisconnect(MidiReceiver midiReceiver) {
        this.mDispatcher.getSender().disconnect(midiReceiver);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this.mGuard) {
            if (this.mIsClosed) {
                return;
            }
            this.mGuard.close();
            this.mInputStream.close();
            if (this.mDeviceServer != null) {
                try {
                    this.mDeviceServer.closePort(this.mToken);
                } catch (RemoteException unused) {
                    Log.e(TAG, "RemoteException in MidiOutputPort.close()");
                }
            }
            this.mIsClosed = true;
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mGuard != null) {
                this.mGuard.warnIfOpen();
            }
            this.mDeviceServer = null;
            close();
        } finally {
            super.finalize();
        }
    }
}
