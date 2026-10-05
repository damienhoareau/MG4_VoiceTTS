package android.os;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public abstract class UpdateEngineCallback {
    @SystemApi
    public abstract void onPayloadApplicationComplete(int i);

    @SystemApi
    public abstract void onStatusUpdate(int i, float f);
}
