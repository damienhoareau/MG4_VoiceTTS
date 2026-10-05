package android.view;

/* JADX INFO: loaded from: classes2.dex */
public class SearchEvent {
    private InputDevice mInputDevice;

    public SearchEvent(InputDevice inputDevice) {
        this.mInputDevice = inputDevice;
    }

    public InputDevice getInputDevice() {
        return this.mInputDevice;
    }
}
