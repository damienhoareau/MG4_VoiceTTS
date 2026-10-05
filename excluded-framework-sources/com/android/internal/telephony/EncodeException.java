package com.android.internal.telephony;

/* JADX INFO: loaded from: classes3.dex */
public class EncodeException extends Exception {
    public EncodeException() {
    }

    public EncodeException(String str) {
        super(str);
    }

    public EncodeException(char c) {
        super("Unencodable char: '" + c + "'");
    }
}
