package com.android.internal.util;

/* JADX INFO: loaded from: classes3.dex */
public class FastMath {
    public static int round(float f) {
        return (int) ((((long) (f * 1.6777216E7f)) + 8388608) >> 24);
    }
}
