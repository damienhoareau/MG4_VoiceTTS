package com.google.android.collect;

import android.util.ArrayMap;
import java.util.HashMap;

/* JADX INFO: loaded from: classes3.dex */
public class Maps {
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> ArrayMap<K, V> newArrayMap() {
        return new ArrayMap<>();
    }
}
