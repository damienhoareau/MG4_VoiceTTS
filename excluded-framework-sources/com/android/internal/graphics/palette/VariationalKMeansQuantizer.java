package com.android.internal.graphics.palette;

import com.android.internal.graphics.ColorUtils;
import com.android.internal.ml.clustering.KMeans;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* JADX INFO: loaded from: classes3.dex */
public class VariationalKMeansQuantizer implements Quantizer {
    private static final boolean DEBUG = false;
    private static final String TAG = "KMeansQuantizer";
    private final int mInitializations;
    private final KMeans mKMeans;
    private final float mMinClusterSqDistance;
    private List<Palette.Swatch> mQuantizedColors;

    public VariationalKMeansQuantizer() {
        this(0.25f);
    }

    public VariationalKMeansQuantizer(float f) {
        this(f, 1);
    }

    public VariationalKMeansQuantizer(float f, int i) {
        this.mKMeans = new KMeans(new Random(0L), 30, 0.0f);
        this.mMinClusterSqDistance = f * f;
        this.mInitializations = i;
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public void quantize(int[] iArr, int i, Palette.Filter[] filterArr) {
        int i2;
        float[] fArr = {0.0f, 0.0f, 0.0f};
        float[][] fArr2 = (float[][]) Array.newInstance((Class<?>) float.class, iArr.length, 3);
        for (int i3 = 0; i3 < iArr.length; i3++) {
            ColorUtils.colorToHSL(iArr[i3], fArr);
            fArr2[i3][0] = fArr[0] / 360.0f;
            fArr2[i3][1] = fArr[1];
            fArr2[i3][2] = fArr[2];
        }
        List<KMeans.Mean> optimalKMeans = getOptimalKMeans(i, fArr2);
        int i4 = 0;
        while (i4 < optimalKMeans.size()) {
            KMeans.Mean mean = optimalKMeans.get(i4);
            float[] centroid = mean.getCentroid();
            i4++;
            int i5 = i4;
            while (i5 < optimalKMeans.size()) {
                KMeans.Mean mean2 = optimalKMeans.get(i5);
                float[] centroid2 = mean2.getCentroid();
                if (KMeans.sqDistance(centroid, centroid2) < this.mMinClusterSqDistance) {
                    optimalKMeans.remove(mean2);
                    mean.getItems().addAll(mean2.getItems());
                    int i6 = 0;
                    while (i6 < centroid.length) {
                        centroid[i6] = (float) (((double) centroid[i6]) + (((double) (centroid2[i6] - centroid[i6])) / 2.0d));
                        i6++;
                        i4 = i4;
                    }
                    i2 = i4;
                    i5--;
                } else {
                    i2 = i4;
                }
                i5++;
                i4 = i2;
            }
        }
        this.mQuantizedColors = new ArrayList();
        for (KMeans.Mean mean3 : optimalKMeans) {
            if (mean3.getItems().size() != 0) {
                float[] centroid3 = mean3.getCentroid();
                this.mQuantizedColors.add(new Palette.Swatch(new float[]{centroid3[0] * 360.0f, centroid3[1], centroid3[2]}, mean3.getItems().size()));
            }
        }
    }

    private List<KMeans.Mean> getOptimalKMeans(int i, float[][] fArr) {
        List<KMeans.Mean> list = null;
        double d = -1.7976931348623157E308d;
        for (int i2 = this.mInitializations; i2 > 0; i2--) {
            List<KMeans.Mean> listPredict = this.mKMeans.predict(i, fArr);
            double dScore = KMeans.score(listPredict);
            if (list == null || dScore > d) {
                list = listPredict;
                d = dScore;
            }
        }
        return list;
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public List<Palette.Swatch> getQuantizedColors() {
        return this.mQuantizedColors;
    }
}
