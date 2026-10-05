package android.speech.tts;

/* JADX INFO: loaded from: classes2.dex */
abstract class AbstractSynthesisCallback implements SynthesisCallback {
    protected final boolean mClientIsUsingV2;

    abstract void stop();

    AbstractSynthesisCallback(boolean z) {
        this.mClientIsUsingV2 = z;
    }

    int errorCodeOnStop() {
        return this.mClientIsUsingV2 ? -2 : -1;
    }
}
