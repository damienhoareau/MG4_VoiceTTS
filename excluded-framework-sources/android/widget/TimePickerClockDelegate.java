package android.widget;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.text.DecimalFormatSymbols;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcelable;
import android.provider.SettingsStringUtil;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import com.android.internal.widget.NumericTextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
class TimePickerClockDelegate extends TimePicker.AbstractTimePickerDelegate {
    private static final int AM = 0;
    private static final long DELAY_COMMIT_MILLIS = 2000;
    private static final int FROM_EXTERNAL_API = 0;
    private static final int FROM_INPUT_PICKER = 2;
    private static final int FROM_RADIAL_PICKER = 1;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final int HOUR_INDEX = 0;
    private static final int MINUTE_INDEX = 1;
    private static final int PM = 1;
    private boolean mAllowAutoAdvance;
    private final RadioButton mAmLabel;
    private final View mAmPmLayout;
    private final View.OnClickListener mClickListener;
    private final Runnable mCommitHour;
    private final Runnable mCommitMinute;
    private int mCurrentHour;
    private int mCurrentMinute;
    private final NumericTextView.OnValueChangedListener mDigitEnteredListener;
    private final View.OnFocusChangeListener mFocusListener;
    private boolean mHourFormatShowLeadingZero;
    private boolean mHourFormatStartsAtZero;
    private final NumericTextView mHourView;
    private boolean mIs24Hour;
    private boolean mIsAmPmAtLeft;
    private boolean mIsAmPmAtTop;
    private boolean mIsEnabled;
    private boolean mLastAnnouncedIsHour;
    private CharSequence mLastAnnouncedText;
    private final NumericTextView mMinuteView;
    private final RadialTimePickerView.OnValueSelectedListener mOnValueSelectedListener;
    private final TextInputTimePickerView.OnValueTypedListener mOnValueTypedListener;
    private final RadioButton mPmLabel;
    private boolean mRadialPickerModeEnabled;
    private final View mRadialTimePickerHeader;
    private final ImageButton mRadialTimePickerModeButton;
    private final String mRadialTimePickerModeEnabledDescription;
    private final RadialTimePickerView mRadialTimePickerView;
    private final String mSelectHours;
    private final String mSelectMinutes;
    private final TextView mSeparatorView;
    private final Calendar mTempCalendar;
    private final View mTextInputPickerHeader;
    private final String mTextInputPickerModeEnabledDescription;
    private final TextInputTimePickerView mTextInputPickerView;
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private static final int[] ATTRS_DISABLED_ALPHA = {16842803};

    @Retention(RetentionPolicy.SOURCE)
    private @interface ChangeSource {
    }

    private int multiplyAlphaComponent(int i, float f) {
        return (((int) ((((i >> 24) & 255) * f) + 0.5f)) << 24) | (16777215 & i);
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getBaseline() {
        return -1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public TimePickerClockDelegate(TimePicker timePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(timePicker, context);
        this.mRadialPickerModeEnabled = true;
        this.mIsEnabled = true;
        this.mIsAmPmAtLeft = false;
        this.mIsAmPmAtTop = false;
        this.mOnValueSelectedListener = new RadialTimePickerView.OnValueSelectedListener() { // from class: android.widget.TimePickerClockDelegate.2
            @Override // android.widget.RadialTimePickerView.OnValueSelectedListener
            public void onValueSelected(int i3, int i4, boolean z) {
                boolean z2 = false;
                if (i3 == 0) {
                    boolean z3 = TimePickerClockDelegate.this.getHour() != i4;
                    boolean z4 = TimePickerClockDelegate.this.mAllowAutoAdvance && z;
                    TimePickerClockDelegate.this.setHourInternal(i4, 1, !z4, true);
                    if (z4) {
                        TimePickerClockDelegate.this.setCurrentItemShowing(1, true, false);
                        int localizedHour = TimePickerClockDelegate.this.getLocalizedHour(i4);
                        TimePickerClockDelegate.this.mDelegator.announceForAccessibility(localizedHour + ". " + TimePickerClockDelegate.this.mSelectMinutes);
                    }
                    z2 = z3;
                } else if (i3 == 1) {
                    z2 = TimePickerClockDelegate.this.getMinute() != i4;
                    TimePickerClockDelegate.this.setMinuteInternal(i4, 1, true);
                }
                if (TimePickerClockDelegate.this.mOnTimeChangedListener == null || !z2) {
                    return;
                }
                TimePickerClockDelegate.this.mOnTimeChangedListener.onTimeChanged(TimePickerClockDelegate.this.mDelegator, TimePickerClockDelegate.this.getHour(), TimePickerClockDelegate.this.getMinute());
            }
        };
        this.mOnValueTypedListener = new TextInputTimePickerView.OnValueTypedListener() { // from class: android.widget.TimePickerClockDelegate.3
            @Override // android.widget.TextInputTimePickerView.OnValueTypedListener
            public void onValueChanged(int i3, int i4) {
                if (i3 == 0) {
                    TimePickerClockDelegate.this.setHourInternal(i4, 2, false, true);
                } else if (i3 == 1) {
                    TimePickerClockDelegate.this.setMinuteInternal(i4, 2, true);
                } else {
                    if (i3 != 2) {
                        return;
                    }
                    TimePickerClockDelegate.this.setAmOrPm(i4);
                }
            }
        };
        this.mDigitEnteredListener = new NumericTextView.OnValueChangedListener() { // from class: android.widget.TimePickerClockDelegate.4
            @Override // com.android.internal.widget.NumericTextView.OnValueChangedListener
            public void onValueChanged(NumericTextView numericTextView, int i3, boolean z, boolean z2) {
                Runnable runnable;
                NumericTextView numericTextView2 = null;
                if (numericTextView == TimePickerClockDelegate.this.mHourView) {
                    runnable = TimePickerClockDelegate.this.mCommitHour;
                    if (numericTextView.isFocused()) {
                        numericTextView2 = TimePickerClockDelegate.this.mMinuteView;
                    }
                } else if (numericTextView != TimePickerClockDelegate.this.mMinuteView) {
                    return;
                } else {
                    runnable = TimePickerClockDelegate.this.mCommitMinute;
                }
                numericTextView.removeCallbacks(runnable);
                if (z) {
                    if (z2) {
                        runnable.run();
                        if (numericTextView2 != null) {
                            numericTextView2.requestFocus();
                            return;
                        }
                        return;
                    }
                    numericTextView.postDelayed(runnable, TimePickerClockDelegate.DELAY_COMMIT_MILLIS);
                }
            }
        };
        this.mCommitHour = new Runnable() { // from class: android.widget.TimePickerClockDelegate.5
            @Override // java.lang.Runnable
            public void run() {
                TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
                timePickerClockDelegate.setHour(timePickerClockDelegate.mHourView.getValue());
            }
        };
        this.mCommitMinute = new Runnable() { // from class: android.widget.TimePickerClockDelegate.6
            @Override // java.lang.Runnable
            public void run() {
                TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
                timePickerClockDelegate.setMinute(timePickerClockDelegate.mMinuteView.getValue());
            }
        };
        this.mFocusListener = new View.OnFocusChangeListener() { // from class: android.widget.TimePickerClockDelegate.7
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    switch (view.getId()) {
                        case R.id.am_label /* 16908713 */:
                            TimePickerClockDelegate.this.setAmOrPm(0);
                            break;
                        case R.id.hours /* 16908954 */:
                            TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
                            break;
                        case R.id.minutes /* 16909071 */:
                            TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
                            break;
                        case R.id.pm_label /* 16909188 */:
                            TimePickerClockDelegate.this.setAmOrPm(1);
                            break;
                        default:
                            return;
                    }
                    TimePickerClockDelegate.this.tryVibrate();
                }
            }
        };
        this.mClickListener = new View.OnClickListener() { // from class: android.widget.TimePickerClockDelegate.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.am_label /* 16908713 */:
                        TimePickerClockDelegate.this.setAmOrPm(0);
                        break;
                    case R.id.hours /* 16908954 */:
                        TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
                        break;
                    case R.id.minutes /* 16909071 */:
                        TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
                        break;
                    case R.id.pm_label /* 16909188 */:
                        TimePickerClockDelegate.this.setAmOrPm(1);
                        break;
                    default:
                        return;
                }
                TimePickerClockDelegate.this.tryVibrate();
            }
        };
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.TimePicker, i, i2);
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resources resources = this.mContext.getResources();
        this.mSelectHours = resources.getString(R.string.select_hours);
        this.mSelectMinutes = resources.getString(R.string.select_minutes);
        View viewInflate = layoutInflater.inflate(typedArrayObtainStyledAttributes.getResourceId(12, R.layout.time_picker_material), timePicker);
        viewInflate.setSaveFromParentEnabled(false);
        View viewFindViewById = viewInflate.findViewById(R.id.time_header);
        this.mRadialTimePickerHeader = viewFindViewById;
        ColorStateList colorStateList = null;
        viewFindViewById.setOnTouchListener(new NearestTouchDelegate());
        NumericTextView numericTextView = (NumericTextView) viewInflate.findViewById(R.id.hours);
        this.mHourView = numericTextView;
        numericTextView.setOnClickListener(this.mClickListener);
        this.mHourView.setOnFocusChangeListener(this.mFocusListener);
        this.mHourView.setOnDigitEnteredListener(this.mDigitEnteredListener);
        this.mHourView.setAccessibilityDelegate(new ClickActionDelegate(context, R.string.select_hours));
        this.mSeparatorView = (TextView) viewInflate.findViewById(R.id.separator);
        NumericTextView numericTextView2 = (NumericTextView) viewInflate.findViewById(R.id.minutes);
        this.mMinuteView = numericTextView2;
        numericTextView2.setOnClickListener(this.mClickListener);
        this.mMinuteView.setOnFocusChangeListener(this.mFocusListener);
        this.mMinuteView.setOnDigitEnteredListener(this.mDigitEnteredListener);
        this.mMinuteView.setAccessibilityDelegate(new ClickActionDelegate(context, R.string.select_minutes));
        this.mMinuteView.setRange(0, 59);
        View viewFindViewById2 = viewInflate.findViewById(R.id.ampm_layout);
        this.mAmPmLayout = viewFindViewById2;
        viewFindViewById2.setOnTouchListener(new NearestTouchDelegate());
        String[] amPmStrings = TimePicker.getAmPmStrings(context);
        RadioButton radioButton = (RadioButton) this.mAmPmLayout.findViewById(R.id.am_label);
        this.mAmLabel = radioButton;
        radioButton.setText(obtainVerbatim(amPmStrings[0]));
        this.mAmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mAmLabel);
        RadioButton radioButton2 = (RadioButton) this.mAmPmLayout.findViewById(R.id.pm_label);
        this.mPmLabel = radioButton2;
        radioButton2.setText(obtainVerbatim(amPmStrings[1]));
        this.mPmLabel.setOnClickListener(this.mClickListener);
        ensureMinimumTextWidth(this.mPmLabel);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(1, 0);
        if (resourceId != 0) {
            TypedArray typedArrayObtainStyledAttributes2 = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, resourceId);
            colorStateList = applyLegacyColorFixes(typedArrayObtainStyledAttributes2.getColorStateList(0));
            typedArrayObtainStyledAttributes2.recycle();
        }
        colorStateList = colorStateList == null ? typedArrayObtainStyledAttributes.getColorStateList(11) : colorStateList;
        this.mTextInputPickerHeader = viewInflate.findViewById(R.id.input_header);
        if (colorStateList != null) {
            this.mHourView.setTextColor(colorStateList);
            this.mSeparatorView.setTextColor(colorStateList);
            this.mMinuteView.setTextColor(colorStateList);
            this.mAmLabel.setTextColor(colorStateList);
            this.mPmLabel.setTextColor(colorStateList);
        }
        if (typedArrayObtainStyledAttributes.hasValueOrEmpty(0)) {
            this.mRadialTimePickerHeader.setBackground(typedArrayObtainStyledAttributes.getDrawable(0));
            this.mTextInputPickerHeader.setBackground(typedArrayObtainStyledAttributes.getDrawable(0));
        }
        typedArrayObtainStyledAttributes.recycle();
        RadialTimePickerView radialTimePickerView = (RadialTimePickerView) viewInflate.findViewById(R.id.radial_picker);
        this.mRadialTimePickerView = radialTimePickerView;
        radialTimePickerView.applyAttributes(attributeSet, i, i2);
        this.mRadialTimePickerView.setOnValueSelectedListener(this.mOnValueSelectedListener);
        TextInputTimePickerView textInputTimePickerView = (TextInputTimePickerView) viewInflate.findViewById(R.id.input_mode);
        this.mTextInputPickerView = textInputTimePickerView;
        textInputTimePickerView.setListener(this.mOnValueTypedListener);
        ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.toggle_mode);
        this.mRadialTimePickerModeButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: android.widget.TimePickerClockDelegate.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TimePickerClockDelegate.this.toggleRadialPickerMode();
            }
        });
        this.mRadialTimePickerModeEnabledDescription = context.getResources().getString(R.string.time_picker_radial_mode_description);
        this.mTextInputPickerModeEnabledDescription = context.getResources().getString(R.string.time_picker_text_input_mode_description);
        this.mAllowAutoAdvance = true;
        updateHourFormat();
        Calendar calendar = Calendar.getInstance(this.mLocale);
        this.mTempCalendar = calendar;
        initialize(calendar.get(11), this.mTempCalendar.get(12), this.mIs24Hour, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleRadialPickerMode() {
        if (this.mRadialPickerModeEnabled) {
            this.mRadialTimePickerView.setVisibility(8);
            this.mRadialTimePickerHeader.setVisibility(8);
            this.mTextInputPickerHeader.setVisibility(0);
            this.mTextInputPickerView.setVisibility(0);
            this.mRadialTimePickerModeButton.setImageResource(R.drawable.btn_clock_material);
            this.mRadialTimePickerModeButton.setContentDescription(this.mRadialTimePickerModeEnabledDescription);
            this.mRadialPickerModeEnabled = false;
            return;
        }
        this.mRadialTimePickerView.setVisibility(0);
        this.mRadialTimePickerHeader.setVisibility(0);
        this.mTextInputPickerHeader.setVisibility(8);
        this.mTextInputPickerView.setVisibility(8);
        this.mRadialTimePickerModeButton.setImageResource(R.drawable.btn_keyboard_key_material);
        this.mRadialTimePickerModeButton.setContentDescription(this.mTextInputPickerModeEnabledDescription);
        updateTextInputPicker();
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        }
        this.mRadialPickerModeEnabled = true;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean validateInput() {
        return this.mTextInputPickerView.validateInput();
    }

    private static void ensureMinimumTextWidth(TextView textView) {
        textView.measure(0, 0);
        int measuredWidth = textView.getMeasuredWidth();
        textView.setMinWidth(measuredWidth);
        textView.setMinimumWidth(measuredWidth);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateHourFormat() {
        boolean z;
        char cCharAt;
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24Hour ? "Hm" : "hm");
        int length = bestDateTimePattern.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                cCharAt = 0;
                break;
            }
            cCharAt = bestDateTimePattern.charAt(i);
            if (cCharAt == 'H' || cCharAt == 'h' || cCharAt == 'K' || cCharAt == 'k') {
                int i2 = i + 1;
                if (i2 >= length || cCharAt != bestDateTimePattern.charAt(i2)) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            }
            i++;
        }
        this.mHourFormatShowLeadingZero = z;
        boolean z2 = (cCharAt == 'K' || cCharAt == 'H') ? 1 : 0;
        this.mHourFormatStartsAtZero = z2;
        int i3 = !z2;
        this.mHourView.setRange(i3, (this.mIs24Hour ? 23 : 11) + i3);
        this.mHourView.setShowLeadingZeroes(this.mHourFormatShowLeadingZero);
        String[] digitStrings = DecimalFormatSymbols.getInstance(this.mLocale).getDigitStrings();
        int iMax = 0;
        for (int i4 = 0; i4 < 10; i4++) {
            iMax = Math.max(iMax, digitStrings[i4].length());
        }
        this.mTextInputPickerView.setHourFormat(iMax * 2);
    }

    static final CharSequence obtainVerbatim(String str) {
        return new SpannableStringBuilder().append(str, new TtsSpan.VerbatimBuilder(str).build(), 0);
    }

    private ColorStateList applyLegacyColorFixes(ColorStateList colorStateList) {
        int defaultColor;
        int iMultiplyAlphaComponent;
        if (colorStateList == null || colorStateList.hasState(16843518)) {
            return colorStateList;
        }
        if (colorStateList.hasState(16842913)) {
            defaultColor = colorStateList.getColorForState(StateSet.get(10), 0);
            iMultiplyAlphaComponent = colorStateList.getColorForState(StateSet.get(8), 0);
        } else {
            defaultColor = colorStateList.getDefaultColor();
            iMultiplyAlphaComponent = multiplyAlphaComponent(defaultColor, this.mContext.obtainStyledAttributes(ATTRS_DISABLED_ALPHA).getFloat(0, 0.3f));
        }
        if (defaultColor == 0 || iMultiplyAlphaComponent == 0) {
            return null;
        }
        return new ColorStateList(new int[][]{new int[]{16843518}, new int[0]}, new int[]{defaultColor, iMultiplyAlphaComponent});
    }

    private static class ClickActionDelegate extends View.AccessibilityDelegate {
        private final AccessibilityNodeInfo.AccessibilityAction mClickAction;

        public ClickActionDelegate(Context context, int i) {
            this.mClickAction = new AccessibilityNodeInfo.AccessibilityAction(16, context.getString(i));
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            accessibilityNodeInfo.addAction(this.mClickAction);
        }
    }

    private void initialize(int i, int i2, boolean z, int i3) {
        this.mCurrentHour = i;
        this.mCurrentMinute = i2;
        this.mIs24Hour = z;
        updateUI(i3);
    }

    private void updateUI(int i) {
        updateHeaderAmPm();
        updateHeaderHour(this.mCurrentHour, false);
        updateHeaderSeparator();
        updateHeaderMinute(this.mCurrentMinute, false);
        updateRadialPicker(i);
        updateTextInputPicker();
        this.mDelegator.invalidate();
    }

    private void updateTextInputPicker() {
        this.mTextInputPickerView.updateTextInputValues(getLocalizedHour(this.mCurrentHour), this.mCurrentMinute, this.mCurrentHour < 12 ? 0 : 1, this.mIs24Hour, this.mHourFormatStartsAtZero);
    }

    private void updateRadialPicker(int i) {
        this.mRadialTimePickerView.initialize(this.mCurrentHour, this.mCurrentMinute, this.mIs24Hour);
        setCurrentItemShowing(i, false, true);
    }

    private void updateHeaderAmPm() {
        if (this.mIs24Hour) {
            this.mAmPmLayout.setVisibility(8);
        } else {
            setAmPmStart(DateFormat.getBestDateTimePattern(this.mLocale, "hm").startsWith(FullBackup.APK_TREE_TOKEN));
            updateAmPmLabelStates(this.mCurrentHour < 12 ? 0 : 1);
        }
    }

    private void setAmPmStart(boolean z) {
        int rule;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mAmPmLayout.getLayoutParams();
        if (layoutParams.getRule(1) != 0 || layoutParams.getRule(0) != 0) {
            if (TextUtils.getLayoutDirectionFromLocale(this.mLocale) != 0) {
                z = !z;
            }
            if (this.mIsAmPmAtLeft == z) {
                return;
            }
            if (z) {
                layoutParams.removeRule(1);
                layoutParams.addRule(0, this.mHourView.getId());
            } else {
                layoutParams.removeRule(0);
                layoutParams.addRule(1, this.mMinuteView.getId());
            }
            this.mIsAmPmAtLeft = z;
        } else if (layoutParams.getRule(3) != 0 || layoutParams.getRule(2) != 0) {
            if (this.mIsAmPmAtTop == z) {
                return;
            }
            if (z) {
                rule = layoutParams.getRule(3);
                layoutParams.removeRule(3);
                layoutParams.addRule(2, rule);
            } else {
                rule = layoutParams.getRule(2);
                layoutParams.removeRule(2);
                layoutParams.addRule(3, rule);
            }
            View viewFindViewById = this.mRadialTimePickerHeader.findViewById(rule);
            viewFindViewById.setPadding(viewFindViewById.getPaddingLeft(), viewFindViewById.getPaddingBottom(), viewFindViewById.getPaddingRight(), viewFindViewById.getPaddingTop());
            this.mIsAmPmAtTop = z;
        }
        this.mAmPmLayout.setLayoutParams(layoutParams);
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setDate(int i, int i2) {
        setHourInternal(i, 0, true, false);
        setMinuteInternal(i2, 0, false);
        onTimeChanged();
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setHour(int i) {
        setHourInternal(i, 0, true, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHourInternal(int i, int i2, boolean z, boolean z2) {
        if (this.mCurrentHour == i) {
            return;
        }
        resetAutofilledValue();
        this.mCurrentHour = i;
        updateHeaderHour(i, z);
        updateHeaderAmPm();
        if (i2 != 1) {
            this.mRadialTimePickerView.setCurrentHour(i);
            this.mRadialTimePickerView.setAmOrPm(i < 12 ? 0 : 1);
        }
        if (i2 != 2) {
            updateTextInputPicker();
        }
        this.mDelegator.invalidate();
        if (z2) {
            onTimeChanged();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getHour() {
        int currentHour = this.mRadialTimePickerView.getCurrentHour();
        if (this.mIs24Hour) {
            return currentHour;
        }
        if (this.mRadialTimePickerView.getAmOrPm() == 1) {
            return (currentHour % 12) + 12;
        }
        return currentHour % 12;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setMinute(int i) {
        setMinuteInternal(i, 0, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMinuteInternal(int i, int i2, boolean z) {
        if (this.mCurrentMinute == i) {
            return;
        }
        resetAutofilledValue();
        this.mCurrentMinute = i;
        updateHeaderMinute(i, true);
        if (i2 != 1) {
            this.mRadialTimePickerView.setCurrentMinute(i);
        }
        if (i2 != 2) {
            updateTextInputPicker();
        }
        this.mDelegator.invalidate();
        if (z) {
            onTimeChanged();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getMinute() {
        return this.mRadialTimePickerView.getCurrentMinute();
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setIs24Hour(boolean z) {
        if (this.mIs24Hour != z) {
            this.mIs24Hour = z;
            this.mCurrentHour = getHour();
            updateHourFormat();
            updateUI(this.mRadialTimePickerView.getCurrentItemShowing());
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean is24Hour() {
        return this.mIs24Hour;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setEnabled(boolean z) {
        this.mHourView.setEnabled(z);
        this.mMinuteView.setEnabled(z);
        this.mAmLabel.setEnabled(z);
        this.mPmLabel.setEnabled(z);
        this.mRadialTimePickerView.setEnabled(z);
        this.mIsEnabled = z;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new TimePicker.AbstractTimePickerDelegate.SavedState(parcelable, getHour(), getMinute(), is24Hour(), getCurrentItemShowing());
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof TimePicker.AbstractTimePickerDelegate.SavedState) {
            TimePicker.AbstractTimePickerDelegate.SavedState savedState = (TimePicker.AbstractTimePickerDelegate.SavedState) parcelable;
            initialize(savedState.getHour(), savedState.getMinute(), savedState.is24HourMode(), savedState.getCurrentItemShowing());
            this.mRadialTimePickerView.invalidate();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int i = this.mIs24Hour ? 129 : 65;
        this.mTempCalendar.set(11, getHour());
        this.mTempCalendar.set(12, getMinute());
        String dateTime = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), i);
        String str = this.mRadialTimePickerView.getCurrentItemShowing() == 0 ? this.mSelectHours : this.mSelectMinutes;
        accessibilityEvent.getText().add(dateTime + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + str);
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getHourView() {
        return this.mHourView;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getMinuteView() {
        return this.mMinuteView;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getAmView() {
        return this.mAmLabel;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getPmView() {
        return this.mPmLabel;
    }

    private int getCurrentItemShowing() {
        return this.mRadialTimePickerView.getCurrentItemShowing();
    }

    private void onTimeChanged() {
        this.mDelegator.sendAccessibilityEvent(4);
        if (this.mOnTimeChangedListener != null) {
            this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
        if (this.mAutoFillChangeListener != null) {
            this.mAutoFillChangeListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryVibrate() {
        this.mDelegator.performHapticFeedback(4);
    }

    private void updateAmPmLabelStates(int i) {
        boolean z = i == 0;
        this.mAmLabel.setActivated(z);
        this.mAmLabel.setChecked(z);
        boolean z2 = i == 1;
        this.mPmLabel.setActivated(z2);
        this.mPmLabel.setChecked(z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getLocalizedHour(int i) {
        if (!this.mIs24Hour) {
            i %= 12;
        }
        if (this.mHourFormatStartsAtZero || i != 0) {
            return i;
        }
        return this.mIs24Hour ? 24 : 12;
    }

    private void updateHeaderHour(int i, boolean z) {
        this.mHourView.setValue(getLocalizedHour(i));
        if (z) {
            tryAnnounceForAccessibility(this.mHourView.getText(), true);
        }
    }

    private void updateHeaderMinute(int i, boolean z) {
        this.mMinuteView.setValue(i);
        if (z) {
            tryAnnounceForAccessibility(this.mMinuteView.getText(), false);
        }
    }

    private void updateHeaderSeparator() {
        String hourMinSeparatorFromPattern = getHourMinSeparatorFromPattern(DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24Hour ? "Hm" : "hm"));
        this.mSeparatorView.setText(hourMinSeparatorFromPattern);
        this.mTextInputPickerView.updateSeparator(hourMinSeparatorFromPattern);
    }

    private static String getHourMinSeparatorFromPattern(String str) {
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt != ' ') {
                if (cCharAt == '\'') {
                    if (z) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str.substring(i));
                        return spannableStringBuilder.subSequence(0, DateFormat.appendQuotedText(spannableStringBuilder, 0)).toString();
                    }
                } else if (cCharAt == 'H' || cCharAt == 'K' || cCharAt == 'h' || cCharAt == 'k') {
                    z = true;
                } else if (z) {
                    return Character.toString(str.charAt(i));
                }
            }
        }
        return SettingsStringUtil.DELIMITER;
    }

    private static int lastIndexOfAny(String str, char[] cArr) {
        if (cArr.length <= 0) {
            return -1;
        }
        for (int length = str.length() - 1; length >= 0; length--) {
            char cCharAt = str.charAt(length);
            for (char c : cArr) {
                if (cCharAt == c) {
                    return length;
                }
            }
        }
        return -1;
    }

    private void tryAnnounceForAccessibility(CharSequence charSequence, boolean z) {
        if (this.mLastAnnouncedIsHour == z && charSequence.equals(this.mLastAnnouncedText)) {
            return;
        }
        this.mDelegator.announceForAccessibility(charSequence);
        this.mLastAnnouncedText = charSequence;
        this.mLastAnnouncedIsHour = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentItemShowing(int i, boolean z, boolean z2) {
        this.mRadialTimePickerView.setCurrentItemShowing(i, z);
        if (i == 0) {
            if (z2) {
                this.mDelegator.announceForAccessibility(this.mSelectHours);
            }
        } else if (z2) {
            this.mDelegator.announceForAccessibility(this.mSelectMinutes);
        }
        this.mHourView.setActivated(i == 0);
        this.mMinuteView.setActivated(i == 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAmOrPm(int i) {
        updateAmPmLabelStates(i);
        if (this.mRadialTimePickerView.setAmOrPm(i)) {
            this.mCurrentHour = getHour();
            updateTextInputPicker();
            if (this.mOnTimeChangedListener != null) {
                this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
            }
        }
    }

    private static class NearestTouchDelegate implements View.OnTouchListener {
        private View mInitialTouchTarget;

        private NearestTouchDelegate() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                if (view instanceof ViewGroup) {
                    this.mInitialTouchTarget = findNearestChild((ViewGroup) view, (int) motionEvent.getX(), (int) motionEvent.getY());
                } else {
                    this.mInitialTouchTarget = null;
                }
            }
            View view2 = this.mInitialTouchTarget;
            if (view2 == null) {
                return false;
            }
            float scrollX = view.getScrollX() - view2.getLeft();
            float scrollY = view.getScrollY() - view2.getTop();
            motionEvent.offsetLocation(scrollX, scrollY);
            boolean zDispatchTouchEvent = view2.dispatchTouchEvent(motionEvent);
            motionEvent.offsetLocation(-scrollX, -scrollY);
            if (actionMasked == 1 || actionMasked == 3) {
                this.mInitialTouchTarget = null;
            }
            return zDispatchTouchEvent;
        }

        private View findNearestChild(ViewGroup viewGroup, int i, int i2) {
            int childCount = viewGroup.getChildCount();
            View view = null;
            int i3 = Integer.MAX_VALUE;
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = viewGroup.getChildAt(i4);
                int left = i - (childAt.getLeft() + (childAt.getWidth() / 2));
                int top = i2 - (childAt.getTop() + (childAt.getHeight() / 2));
                int i5 = (left * left) + (top * top);
                if (i3 > i5) {
                    view = childAt;
                    i3 = i5;
                }
            }
            return view;
        }
    }
}
