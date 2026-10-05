package com.android.internal.graphics.palette;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public interface Quantizer {
    List<Palette.Swatch> getQuantizedColors();

    void quantize(int[] iArr, int i, Palette.Filter[] filterArr);
}
