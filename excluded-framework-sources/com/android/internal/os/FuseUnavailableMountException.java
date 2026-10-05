package com.android.internal.os;

/* JADX INFO: loaded from: classes3.dex */
public class FuseUnavailableMountException extends Exception {
    public FuseUnavailableMountException(int i) {
        super("AppFuse mount point " + i + " is unavailable");
    }
}
