package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSessionListener;
import android.telephony.ims.ImsStreamMediaProfile;
import android.telephony.ims.ImsVideoCallProvider;
import android.telephony.ims.aidl.IImsCallSessionListener;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsVideoCallProvider;
import com.android.internal.telephony.IccCardConstants;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class ImsCallSessionImplBase implements AutoCloseable {
    public static final int USSD_MODE_NOTIFY = 0;
    public static final int USSD_MODE_REQUEST = 1;
    private IImsCallSession mServiceImpl = new IImsCallSession.Stub() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase.1
        @Override // com.android.ims.internal.IImsCallSession
        public void close() {
            ImsCallSessionImplBase.this.close();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public String getCallId() {
            return ImsCallSessionImplBase.this.getCallId();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getCallProfile() {
            return ImsCallSessionImplBase.this.getCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getLocalCallProfile() {
            return ImsCallSessionImplBase.this.getLocalCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getRemoteCallProfile() {
            return ImsCallSessionImplBase.this.getRemoteCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public String getProperty(String str) {
            return ImsCallSessionImplBase.this.getProperty(str);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public int getState() {
            return ImsCallSessionImplBase.this.getState();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public boolean isInCall() {
            return ImsCallSessionImplBase.this.isInCall();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void setListener(IImsCallSessionListener iImsCallSessionListener) {
            ImsCallSessionImplBase.this.setListener(new ImsCallSessionListener(iImsCallSessionListener));
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void setMute(boolean z) {
            ImsCallSessionImplBase.this.setMute(z);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void start(String str, ImsCallProfile imsCallProfile) {
            ImsCallSessionImplBase.this.start(str, imsCallProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void startConference(String[] strArr, ImsCallProfile imsCallProfile) throws RemoteException {
            ImsCallSessionImplBase.this.startConference(strArr, imsCallProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void accept(int i, ImsStreamMediaProfile imsStreamMediaProfile) {
            ImsCallSessionImplBase.this.accept(i, imsStreamMediaProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void deflect(String str) {
            ImsCallSessionImplBase.this.deflect(str);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void reject(int i) {
            ImsCallSessionImplBase.this.reject(i);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void terminate(int i) {
            ImsCallSessionImplBase.this.terminate(i);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void hold(ImsStreamMediaProfile imsStreamMediaProfile) {
            ImsCallSessionImplBase.this.hold(imsStreamMediaProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void resume(ImsStreamMediaProfile imsStreamMediaProfile) {
            ImsCallSessionImplBase.this.resume(imsStreamMediaProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void merge() {
            ImsCallSessionImplBase.this.merge();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void update(int i, ImsStreamMediaProfile imsStreamMediaProfile) {
            ImsCallSessionImplBase.this.update(i, imsStreamMediaProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void extendToConference(String[] strArr) {
            ImsCallSessionImplBase.this.extendToConference(strArr);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void inviteParticipants(String[] strArr) {
            ImsCallSessionImplBase.this.inviteParticipants(strArr);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void removeParticipants(String[] strArr) {
            ImsCallSessionImplBase.this.removeParticipants(strArr);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendDtmf(char c, Message message) {
            ImsCallSessionImplBase.this.sendDtmf(c, message);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void startDtmf(char c) {
            ImsCallSessionImplBase.this.startDtmf(c);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void stopDtmf() {
            ImsCallSessionImplBase.this.stopDtmf();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendUssd(String str) {
            ImsCallSessionImplBase.this.sendUssd(str);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public IImsVideoCallProvider getVideoCallProvider() {
            return ImsCallSessionImplBase.this.getVideoCallProvider();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public boolean isMultiparty() {
            return ImsCallSessionImplBase.this.isMultiparty();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttModifyRequest(ImsCallProfile imsCallProfile) {
            ImsCallSessionImplBase.this.sendRttModifyRequest(imsCallProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttModifyResponse(boolean z) {
            ImsCallSessionImplBase.this.sendRttModifyResponse(z);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttMessage(String str) {
            ImsCallSessionImplBase.this.sendRttMessage(str);
        }
    };

    public void accept(int i, ImsStreamMediaProfile imsStreamMediaProfile) {
    }

    @Override // java.lang.AutoCloseable
    public void close() {
    }

    public void deflect(String str) {
    }

    public void extendToConference(String[] strArr) {
    }

    public String getCallId() {
        return null;
    }

    public ImsCallProfile getCallProfile() {
        return null;
    }

    public ImsVideoCallProvider getImsVideoCallProvider() {
        return null;
    }

    public ImsCallProfile getLocalCallProfile() {
        return null;
    }

    public String getProperty(String str) {
        return null;
    }

    public ImsCallProfile getRemoteCallProfile() {
        return null;
    }

    public int getState() {
        return -1;
    }

    public void hold(ImsStreamMediaProfile imsStreamMediaProfile) {
    }

    public void inviteParticipants(String[] strArr) {
    }

    public boolean isInCall() {
        return false;
    }

    public boolean isMultiparty() {
        return false;
    }

    public void merge() {
    }

    public void reject(int i) {
    }

    public void removeParticipants(String[] strArr) {
    }

    public void resume(ImsStreamMediaProfile imsStreamMediaProfile) {
    }

    public void sendDtmf(char c, Message message) {
    }

    public void sendRttMessage(String str) {
    }

    public void sendRttModifyRequest(ImsCallProfile imsCallProfile) {
    }

    public void sendRttModifyResponse(boolean z) {
    }

    public void sendUssd(String str) {
    }

    public void setListener(ImsCallSessionListener imsCallSessionListener) {
    }

    public void setMute(boolean z) {
    }

    public void start(String str, ImsCallProfile imsCallProfile) {
    }

    public void startConference(String[] strArr, ImsCallProfile imsCallProfile) {
    }

    public void startDtmf(char c) {
    }

    public void stopDtmf() {
    }

    public void terminate(int i) {
    }

    public void update(int i, ImsStreamMediaProfile imsStreamMediaProfile) {
    }

    public static class State {
        public static final int ESTABLISHED = 4;
        public static final int ESTABLISHING = 3;
        public static final int IDLE = 0;
        public static final int INITIATED = 1;
        public static final int INVALID = -1;
        public static final int NEGOTIATING = 2;
        public static final int REESTABLISHING = 6;
        public static final int RENEGOTIATING = 5;
        public static final int TERMINATED = 8;
        public static final int TERMINATING = 7;

        public static String toString(int i) {
            switch (i) {
                case 0:
                    return "IDLE";
                case 1:
                    return "INITIATED";
                case 2:
                    return "NEGOTIATING";
                case 3:
                    return "ESTABLISHING";
                case 4:
                    return "ESTABLISHED";
                case 5:
                    return "RENEGOTIATING";
                case 6:
                    return "REESTABLISHING";
                case 7:
                    return "TERMINATING";
                case 8:
                    return "TERMINATED";
                default:
                    return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
            }
        }

        private State() {
        }
    }

    public final void setListener(IImsCallSessionListener iImsCallSessionListener) throws RemoteException {
        setListener(new ImsCallSessionListener(iImsCallSessionListener));
    }

    public IImsVideoCallProvider getVideoCallProvider() {
        ImsVideoCallProvider imsVideoCallProvider = getImsVideoCallProvider();
        if (imsVideoCallProvider != null) {
            return imsVideoCallProvider.getInterface();
        }
        return null;
    }

    public IImsCallSession getServiceImpl() {
        return this.mServiceImpl;
    }

    public void setServiceImpl(IImsCallSession iImsCallSession) {
        this.mServiceImpl = iImsCallSession;
    }
}
