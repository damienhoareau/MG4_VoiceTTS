package android.speech.tts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
public interface SynthesisCallback {

    @Retention(RetentionPolicy.SOURCE)
    public @interface SupportedAudioFormat {
    }

    int audioAvailable(byte[] bArr, int i, int i2);

    int done();

    void error();

    void error(int i);

    int getMaxBufferSize();

    boolean hasFinished();

    boolean hasStarted();

    default void rangeStart(int i, int i2, int i3) {
    }

    int start(int i, int i2, int i3);
}
