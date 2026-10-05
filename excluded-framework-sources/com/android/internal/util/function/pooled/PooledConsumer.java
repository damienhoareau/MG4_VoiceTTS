package com.android.internal.util.function.pooled;

import java.util.function.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public interface PooledConsumer<T> extends PooledLambda, Consumer<T> {
    PooledConsumer<T> recycleOnUse();
}
