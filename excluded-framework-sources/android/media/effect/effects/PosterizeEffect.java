package android.media.effect.effects;

import android.app.slice.SliceItem;
import android.filterpacks.imageproc.PosterizeFilter;
import android.media.effect.EffectContext;
import android.media.effect.SingleFilterEffect;

/* JADX INFO: loaded from: classes.dex */
public class PosterizeEffect extends SingleFilterEffect {
    public PosterizeEffect(EffectContext effectContext, String str) {
        super(effectContext, str, PosterizeFilter.class, SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE, new Object[0]);
    }
}
