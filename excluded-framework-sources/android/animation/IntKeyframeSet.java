package android.animation;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class IntKeyframeSet extends KeyframeSet implements Keyframes.IntKeyframes {
    public IntKeyframeSet(Keyframe.IntKeyframe... intKeyframeArr) {
        super(intKeyframeArr);
    }

    @Override // android.animation.KeyframeSet, android.animation.Keyframes
    public Object getValue(float f) {
        return Integer.valueOf(getIntValue(f));
    }

    @Override // android.animation.KeyframeSet
    /* JADX INFO: renamed from: clone */
    public IntKeyframeSet mo2clone() {
        List<Keyframe> list = this.mKeyframes;
        int size = this.mKeyframes.size();
        Keyframe.IntKeyframe[] intKeyframeArr = new Keyframe.IntKeyframe[size];
        for (int i = 0; i < size; i++) {
            intKeyframeArr[i] = (Keyframe.IntKeyframe) list.get(i).mo3clone();
        }
        return new IntKeyframeSet(intKeyframeArr);
    }

    @Override // android.animation.Keyframes.IntKeyframes
    public int getIntValue(float f) {
        if (f <= 0.0f) {
            Keyframe.IntKeyframe intKeyframe = (Keyframe.IntKeyframe) this.mKeyframes.get(0);
            Keyframe.IntKeyframe intKeyframe2 = (Keyframe.IntKeyframe) this.mKeyframes.get(1);
            int intValue = intKeyframe.getIntValue();
            int intValue2 = intKeyframe2.getIntValue();
            float fraction = intKeyframe.getFraction();
            float fraction2 = intKeyframe2.getFraction();
            TimeInterpolator interpolator = intKeyframe2.getInterpolator();
            if (interpolator != null) {
                f = interpolator.getInterpolation(f);
            }
            float f2 = (f - fraction) / (fraction2 - fraction);
            return this.mEvaluator == null ? intValue + ((int) (f2 * (intValue2 - intValue))) : ((Number) this.mEvaluator.evaluate(f2, Integer.valueOf(intValue), Integer.valueOf(intValue2))).intValue();
        }
        if (f >= 1.0f) {
            Keyframe.IntKeyframe intKeyframe3 = (Keyframe.IntKeyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            Keyframe.IntKeyframe intKeyframe4 = (Keyframe.IntKeyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
            int intValue3 = intKeyframe3.getIntValue();
            int intValue4 = intKeyframe4.getIntValue();
            float fraction3 = intKeyframe3.getFraction();
            float fraction4 = intKeyframe4.getFraction();
            TimeInterpolator interpolator2 = intKeyframe4.getInterpolator();
            if (interpolator2 != null) {
                f = interpolator2.getInterpolation(f);
            }
            float f3 = (f - fraction3) / (fraction4 - fraction3);
            return this.mEvaluator == null ? intValue3 + ((int) (f3 * (intValue4 - intValue3))) : ((Number) this.mEvaluator.evaluate(f3, Integer.valueOf(intValue3), Integer.valueOf(intValue4))).intValue();
        }
        Keyframe.IntKeyframe intKeyframe5 = (Keyframe.IntKeyframe) this.mKeyframes.get(0);
        int i = 1;
        while (i < this.mNumKeyframes) {
            Keyframe.IntKeyframe intKeyframe6 = (Keyframe.IntKeyframe) this.mKeyframes.get(i);
            if (f < intKeyframe6.getFraction()) {
                TimeInterpolator interpolator3 = intKeyframe6.getInterpolator();
                float fraction5 = (f - intKeyframe5.getFraction()) / (intKeyframe6.getFraction() - intKeyframe5.getFraction());
                int intValue5 = intKeyframe5.getIntValue();
                int intValue6 = intKeyframe6.getIntValue();
                if (interpolator3 != null) {
                    fraction5 = interpolator3.getInterpolation(fraction5);
                }
                return this.mEvaluator == null ? intValue5 + ((int) (fraction5 * (intValue6 - intValue5))) : ((Number) this.mEvaluator.evaluate(fraction5, Integer.valueOf(intValue5), Integer.valueOf(intValue6))).intValue();
            }
            i++;
            intKeyframe5 = intKeyframe6;
        }
        return ((Number) this.mKeyframes.get(this.mNumKeyframes - 1).getValue()).intValue();
    }

    @Override // android.animation.KeyframeSet, android.animation.Keyframes
    public Class getType() {
        return Integer.class;
    }
}
