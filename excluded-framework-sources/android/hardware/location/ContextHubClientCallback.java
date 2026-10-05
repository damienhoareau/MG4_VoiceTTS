package android.hardware.location;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class ContextHubClientCallback {
    public void onHubReset(ContextHubClient contextHubClient) {
    }

    public void onMessageFromNanoApp(ContextHubClient contextHubClient, NanoAppMessage nanoAppMessage) {
    }

    public void onNanoAppAborted(ContextHubClient contextHubClient, long j, int i) {
    }

    public void onNanoAppDisabled(ContextHubClient contextHubClient, long j) {
    }

    public void onNanoAppEnabled(ContextHubClient contextHubClient, long j) {
    }

    public void onNanoAppLoaded(ContextHubClient contextHubClient, long j) {
    }

    public void onNanoAppUnloaded(ContextHubClient contextHubClient, long j) {
    }
}
