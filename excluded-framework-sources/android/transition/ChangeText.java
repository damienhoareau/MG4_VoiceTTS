package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class ChangeText extends Transition {
    public static final int CHANGE_BEHAVIOR_IN = 2;
    public static final int CHANGE_BEHAVIOR_KEEP = 0;
    public static final int CHANGE_BEHAVIOR_OUT = 1;
    public static final int CHANGE_BEHAVIOR_OUT_IN = 3;
    private static final String LOG_TAG = "TextChange";
    private static final String PROPNAME_TEXT_COLOR = "android:textchange:textColor";
    private int mChangeBehavior = 0;
    private static final String PROPNAME_TEXT = "android:textchange:text";
    private static final String PROPNAME_TEXT_SELECTION_START = "android:textchange:textSelectionStart";
    private static final String PROPNAME_TEXT_SELECTION_END = "android:textchange:textSelectionEnd";
    private static final String[] sTransitionProperties = {PROPNAME_TEXT, PROPNAME_TEXT_SELECTION_START, PROPNAME_TEXT_SELECTION_END};

    public ChangeText setChangeBehavior(int i) {
        if (i >= 0 && i <= 3) {
            this.mChangeBehavior = i;
        }
        return this;
    }

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public int getChangeBehavior() {
        return this.mChangeBehavior;
    }

    private void captureValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof TextView) {
            TextView textView = (TextView) transitionValues.view;
            transitionValues.values.put(PROPNAME_TEXT, textView.getText());
            if (textView instanceof EditText) {
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_START, Integer.valueOf(textView.getSelectionStart()));
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_END, Integer.valueOf(textView.getSelectionEnd()));
            }
            if (this.mChangeBehavior > 0) {
                transitionValues.values.put(PROPNAME_TEXT_COLOR, Integer.valueOf(textView.getCurrentTextColor()));
            }
        }
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int iIntValue;
        int i;
        int iIntValue2;
        int i2;
        int i3;
        ValueAnimator valueAnimatorOfFloat;
        ValueAnimator valueAnimatorOfInt;
        final int i4;
        Animator animator;
        final int i5;
        if (transitionValues == null || transitionValues2 == null || !(transitionValues.view instanceof TextView) || !(transitionValues2.view instanceof TextView)) {
            return null;
        }
        final TextView textView = (TextView) transitionValues2.view;
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        CharSequence charSequence = map.get(PROPNAME_TEXT) != null ? (CharSequence) map.get(PROPNAME_TEXT) : "";
        CharSequence charSequence2 = map2.get(PROPNAME_TEXT) != null ? (CharSequence) map2.get(PROPNAME_TEXT) : "";
        boolean z = textView instanceof EditText;
        if (z) {
            int iIntValue3 = map.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) map.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            iIntValue = map.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) map.get(PROPNAME_TEXT_SELECTION_END)).intValue() : iIntValue3;
            int iIntValue4 = map2.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) map2.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            i = iIntValue4;
            iIntValue2 = map2.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) map2.get(PROPNAME_TEXT_SELECTION_END)).intValue() : iIntValue4;
            i2 = iIntValue3;
        } else {
            iIntValue = -1;
            i = -1;
            iIntValue2 = -1;
            i2 = -1;
        }
        if (charSequence.equals(charSequence2)) {
            return null;
        }
        if (this.mChangeBehavior != 2) {
            textView.setText(charSequence);
            if (z) {
                setSelection((EditText) textView, i2, iIntValue);
            }
        }
        if (this.mChangeBehavior == 0) {
            valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            final CharSequence charSequence3 = charSequence;
            final CharSequence charSequence4 = charSequence2;
            final int i6 = i;
            final int i7 = iIntValue2;
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    if (charSequence3.equals(textView.getText())) {
                        textView.setText(charSequence4);
                        TextView textView2 = textView;
                        if (textView2 instanceof EditText) {
                            ChangeText.this.setSelection((EditText) textView2, i6, i7);
                        }
                    }
                }
            });
            i3 = iIntValue;
            charSequence = charSequence;
            i2 = i2;
            i5 = 0;
        } else {
            i3 = iIntValue;
            final int iIntValue5 = ((Integer) map.get(PROPNAME_TEXT_COLOR)).intValue();
            final int iIntValue6 = ((Integer) map2.get(PROPNAME_TEXT_COLOR)).intValue();
            int i8 = this.mChangeBehavior;
            if (i8 == 3 || i8 == 1) {
                ValueAnimator valueAnimatorOfInt2 = ValueAnimator.ofInt(Color.alpha(iIntValue5), 0);
                valueAnimatorOfInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        textView.setTextColor((((Integer) valueAnimator.getAnimatedValue()).intValue() << 24) | (iIntValue5 & 16777215));
                    }
                });
                final CharSequence charSequence5 = charSequence;
                final CharSequence charSequence6 = charSequence2;
                final int i9 = i;
                final int i10 = iIntValue2;
                valueAnimatorOfInt2.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        if (charSequence5.equals(textView.getText())) {
                            textView.setText(charSequence6);
                            TextView textView2 = textView;
                            if (textView2 instanceof EditText) {
                                ChangeText.this.setSelection((EditText) textView2, i9, i10);
                            }
                        }
                        textView.setTextColor(iIntValue6);
                    }
                });
                valueAnimatorOfFloat = valueAnimatorOfInt2;
            } else {
                valueAnimatorOfFloat = null;
            }
            int i11 = this.mChangeBehavior;
            if (i11 == 3 || i11 == 2) {
                valueAnimatorOfInt = ValueAnimator.ofInt(0, Color.alpha(iIntValue6));
                i4 = iIntValue6;
                valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        textView.setTextColor((((Integer) valueAnimator.getAnimatedValue()).intValue() << 24) | (i4 & 16777215));
                    }
                });
                valueAnimatorOfInt.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator2) {
                        textView.setTextColor(i4);
                    }
                });
            } else {
                i4 = iIntValue6;
                valueAnimatorOfInt = null;
            }
            if (valueAnimatorOfFloat != null && valueAnimatorOfInt != null) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(valueAnimatorOfFloat, valueAnimatorOfInt);
                animator = animatorSet;
            } else if (valueAnimatorOfFloat != null) {
                i5 = i4;
            } else {
                animator = valueAnimatorOfInt;
            }
            i5 = i4;
            final CharSequence charSequence7 = charSequence2;
            final int i12 = i;
            final int i13 = iIntValue2;
            final CharSequence charSequence8 = charSequence;
            final int i14 = i2;
            final int i15 = i3;
            addListener(new TransitionListenerAdapter() { // from class: android.transition.ChangeText.6
                int mPausedColor = 0;

                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionPause(Transition transition) {
                    if (ChangeText.this.mChangeBehavior != 2) {
                        textView.setText(charSequence7);
                        TextView textView2 = textView;
                        if (textView2 instanceof EditText) {
                            ChangeText.this.setSelection((EditText) textView2, i12, i13);
                        }
                    }
                    if (ChangeText.this.mChangeBehavior > 0) {
                        this.mPausedColor = textView.getCurrentTextColor();
                        textView.setTextColor(i5);
                    }
                }

                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionResume(Transition transition) {
                    if (ChangeText.this.mChangeBehavior != 2) {
                        textView.setText(charSequence8);
                        TextView textView2 = textView;
                        if (textView2 instanceof EditText) {
                            ChangeText.this.setSelection((EditText) textView2, i14, i15);
                        }
                    }
                    if (ChangeText.this.mChangeBehavior > 0) {
                        textView.setTextColor(this.mPausedColor);
                    }
                }

                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                }
            });
            return animator;
        }
        animator = valueAnimatorOfFloat;
        final CharSequence charSequence9 = charSequence2;
        final int i16 = i;
        final int i17 = iIntValue2;
        final CharSequence charSequence10 = charSequence;
        final int i18 = i2;
        final int i19 = i3;
        addListener(new TransitionListenerAdapter() { // from class: android.transition.ChangeText.6
            int mPausedColor = 0;

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionPause(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    textView.setText(charSequence9);
                    TextView textView2 = textView;
                    if (textView2 instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView2, i16, i17);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    this.mPausedColor = textView.getCurrentTextColor();
                    textView.setTextColor(i5);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionResume(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    textView.setText(charSequence10);
                    TextView textView2 = textView;
                    if (textView2 instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView2, i18, i19);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    textView.setTextColor(this.mPausedColor);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }
        });
        return animator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelection(EditText editText, int i, int i2) {
        if (i < 0 || i2 < 0) {
            return;
        }
        editText.setSelection(i, i2);
    }
}
