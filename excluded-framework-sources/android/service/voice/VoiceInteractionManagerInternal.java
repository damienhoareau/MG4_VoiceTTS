package android.service.voice;

import android.os.Bundle;
import android.os.IBinder;

/* JADX INFO: loaded from: classes2.dex */
public abstract class VoiceInteractionManagerInternal {
    public abstract void startLocalVoiceInteraction(IBinder iBinder, Bundle bundle);

    public abstract void stopLocalVoiceInteraction(IBinder iBinder);

    public abstract boolean supportsLocalVoiceInteraction();
}
