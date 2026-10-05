package com.saicmotor.voicetts.log;

import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LoggerBase {
    private final String mPrefix;
    private final String mTag;

    protected abstract String getTag();

    public LoggerBase(Class<?> cls) {
        this(cls.getSimpleName());
    }

    public LoggerBase(String str) {
        String tag = getTag();
        this.mTag = tag;
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalStateException("Tag must be not null or empty");
        }
        if (this.mTag.length() > 23) {
            throw new IllegalStateException("Tag must be 23 characters or less");
        }
        this.mPrefix = "[" + str + "] ";
    }

    public void v(String str) {
        if (isV()) {
            Log.v(this.mTag, this.mPrefix.concat(str));
        }
    }

    public void v(String str, Throwable th) {
        if (isV()) {
            Log.v(this.mTag, this.mPrefix.concat(str), th);
        }
    }

    public void d(String str) {
        if (isD()) {
            Log.d(this.mTag, this.mPrefix.concat(str));
        }
    }

    public void d(String str, Throwable th) {
        if (isD()) {
            Log.d(this.mTag, this.mPrefix.concat(str), th);
        }
    }

    public void i(String str) {
        if (isI()) {
            Log.i(this.mTag, this.mPrefix.concat(str));
        }
    }

    public void i(String str, Throwable th) {
        if (isI()) {
            Log.i(this.mTag, this.mPrefix.concat(str), th);
        }
    }

    public void w(String str) {
        Log.w(this.mTag, this.mPrefix.concat(str));
    }

    public void w(String str, Throwable th) {
        Log.w(this.mTag, this.mPrefix.concat(str), th);
    }

    public void e(String str) {
        Log.e(this.mTag, this.mPrefix.concat(str));
    }

    public void e(String str, Throwable th) {
        Log.e(this.mTag, this.mPrefix.concat(str), th);
    }

    public void wtf(String str) {
        Log.wtf(this.mTag, this.mPrefix.concat(str));
    }

    public void wtf(String str, Throwable th) {
        Log.wtf(this.mTag, this.mPrefix.concat(str), th);
    }

    private boolean isV() {
        return Log.isLoggable(this.mTag, 2);
    }

    private boolean isD() {
        return Log.isLoggable(this.mTag, 3);
    }

    private boolean isI() {
        return Log.isLoggable(this.mTag, 4);
    }
}
