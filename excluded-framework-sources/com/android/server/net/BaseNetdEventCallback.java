package com.android.server.net;

import android.net.INetdEventCallback;

/* JADX INFO: loaded from: classes3.dex */
public class BaseNetdEventCallback extends INetdEventCallback.Stub {
    @Override // android.net.INetdEventCallback
    public void onConnectEvent(String str, int i, long j, int i2) {
    }

    @Override // android.net.INetdEventCallback
    public void onDnsEvent(String str, String[] strArr, int i, long j, int i2) {
    }

    @Override // android.net.INetdEventCallback
    public void onPrivateDnsValidationEvent(int i, String str, String str2, boolean z) {
    }
}
