package android.telephony.ims.feature;

import android.annotation.SystemApi;
import android.telephony.ims.aidl.IImsRcsFeature;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public class RcsFeature extends ImsFeature {
    private final IImsRcsFeature mImsRcsBinder = new IImsRcsFeature.Stub() { // from class: android.telephony.ims.feature.RcsFeature.1
    };

    @Override // android.telephony.ims.feature.ImsFeature
    public void changeEnabledCapabilities(CapabilityChangeRequest capabilityChangeRequest, ImsFeature.CapabilityCallbackProxy capabilityCallbackProxy) {
    }

    @Override // android.telephony.ims.feature.ImsFeature
    public void onFeatureReady() {
    }

    @Override // android.telephony.ims.feature.ImsFeature
    public void onFeatureRemoved() {
    }

    @Override // android.telephony.ims.feature.ImsFeature
    public final IImsRcsFeature getBinder() {
        return this.mImsRcsBinder;
    }
}
