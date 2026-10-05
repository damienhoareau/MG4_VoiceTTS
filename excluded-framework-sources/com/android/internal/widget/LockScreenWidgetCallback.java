package com.android.internal.widget;

import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public interface LockScreenWidgetCallback {
    boolean isVisible(View view);

    void requestHide(View view);

    void requestShow(View view);

    void userActivity(View view);
}
