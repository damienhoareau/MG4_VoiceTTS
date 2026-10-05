package com.android.ims;

/* JADX INFO: loaded from: classes3.dex */
public class ImsException extends Exception {
    private int mCode;

    public ImsException() {
    }

    public ImsException(String str, int i) {
        super(str + "(" + i + ")");
        this.mCode = i;
    }

    public ImsException(String str, Throwable th, int i) {
        super(str, th);
        this.mCode = i;
    }

    public int getCode() {
        return this.mCode;
    }
}
