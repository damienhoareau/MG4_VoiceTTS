package android.media.midi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class MidiDeviceService extends Service {
    public static final String SERVICE_INTERFACE = "android.media.midi.MidiDeviceService";
    private static final String TAG = "MidiDeviceService";
    private final MidiDeviceServer.Callback mCallback = new MidiDeviceServer.Callback() { // from class: android.media.midi.MidiDeviceService.1
        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onDeviceStatusChanged(MidiDeviceServer midiDeviceServer, MidiDeviceStatus midiDeviceStatus) {
            MidiDeviceService.this.onDeviceStatusChanged(midiDeviceStatus);
        }

        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onClose() {
            MidiDeviceService.this.onClose();
        }
    };
    private MidiDeviceInfo mDeviceInfo;
    private IMidiManager mMidiManager;
    private MidiDeviceServer mServer;

    public void onClose() {
    }

    public void onDeviceStatusChanged(MidiDeviceStatus midiDeviceStatus) {
    }

    public abstract MidiReceiver[] onGetInputPortReceivers();

    @Override // android.app.Service
    public void onCreate() {
        MidiDeviceServer midiDeviceServer;
        IMidiManager iMidiManagerAsInterface = IMidiManager.Stub.asInterface(ServiceManager.getService("midi"));
        this.mMidiManager = iMidiManagerAsInterface;
        try {
            MidiDeviceInfo serviceDeviceInfo = iMidiManagerAsInterface.getServiceDeviceInfo(getPackageName(), getClass().getName());
            if (serviceDeviceInfo == null) {
                Log.e(TAG, "Could not find MidiDeviceInfo for MidiDeviceService " + this);
                return;
            }
            this.mDeviceInfo = serviceDeviceInfo;
            MidiReceiver[] midiReceiverArrOnGetInputPortReceivers = onGetInputPortReceivers();
            if (midiReceiverArrOnGetInputPortReceivers == null) {
                midiReceiverArrOnGetInputPortReceivers = new MidiReceiver[0];
            }
            midiDeviceServer = new MidiDeviceServer(this.mMidiManager, midiReceiverArrOnGetInputPortReceivers, serviceDeviceInfo, this.mCallback);
            this.mServer = midiDeviceServer;
        } catch (RemoteException unused) {
            Log.e(TAG, "RemoteException in IMidiManager.getServiceDeviceInfo");
            midiDeviceServer = null;
        }
    }

    public final MidiReceiver[] getOutputPortReceivers() {
        MidiDeviceServer midiDeviceServer = this.mServer;
        if (midiDeviceServer == null) {
            return null;
        }
        return midiDeviceServer.getOutputPortReceivers();
    }

    public final MidiDeviceInfo getDeviceInfo() {
        return this.mDeviceInfo;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        MidiDeviceServer midiDeviceServer;
        if (!SERVICE_INTERFACE.equals(intent.getAction()) || (midiDeviceServer = this.mServer) == null) {
            return null;
        }
        return midiDeviceServer.getBinderInterface().asBinder();
    }
}
