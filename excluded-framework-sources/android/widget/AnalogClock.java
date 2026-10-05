package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.R;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
@Deprecated
public class AnalogClock extends View {
    private boolean mAttached;
    private Time mCalendar;
    private boolean mChanged;
    private Drawable mDial;
    private int mDialHeight;
    private int mDialWidth;
    private float mHour;
    private Drawable mHourHand;
    private final BroadcastReceiver mIntentReceiver;
    private Drawable mMinuteHand;
    private float mMinutes;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AnalogClock(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AnalogClock(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: android.widget.AnalogClock.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    String stringExtra = intent.getStringExtra("time-zone");
                    AnalogClock.this.mCalendar = new Time(TimeZone.getTimeZone(stringExtra).getID());
                }
                AnalogClock.this.onTimeChanged();
                AnalogClock.this.invalidate();
            }
        };
        context.getResources();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AnalogClock, i, i2);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        this.mDial = drawable;
        if (drawable == null) {
            this.mDial = context.getDrawable(R.drawable.clock_dial);
        }
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(1);
        this.mHourHand = drawable2;
        if (drawable2 == null) {
            this.mHourHand = context.getDrawable(R.drawable.clock_hand_hour);
        }
        Drawable drawable3 = typedArrayObtainStyledAttributes.getDrawable(2);
        this.mMinuteHand = drawable3;
        if (drawable3 == null) {
            this.mMinuteHand = context.getDrawable(R.drawable.clock_hand_minute);
        }
        this.mCalendar = new Time();
        this.mDialWidth = this.mDial.getIntrinsicWidth();
        this.mDialHeight = this.mDial.getIntrinsicHeight();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), intentFilter, null, getHandler());
        }
        this.mCalendar = new Time();
        onTimeChanged();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            getContext().unregisterReceiver(this.mIntentReceiver);
            this.mAttached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        float f = 1.0f;
        float f2 = (mode == 0 || size >= (i4 = this.mDialWidth)) ? 1.0f : size / i4;
        if (mode2 != 0 && size2 < (i3 = this.mDialHeight)) {
            f = size2 / i3;
        }
        float fMin = Math.min(f2, f);
        setMeasuredDimension(resolveSizeAndState((int) (this.mDialWidth * fMin), i, 0), resolveSizeAndState((int) (this.mDialHeight * fMin), i2, 0));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mChanged = true;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean z = this.mChanged;
        boolean z2 = false;
        if (z) {
            this.mChanged = false;
        }
        int i = this.mRight - this.mLeft;
        int i2 = this.mBottom - this.mTop;
        int i3 = i / 2;
        int i4 = i2 / 2;
        Drawable drawable = this.mDial;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (i < intrinsicWidth || i2 < intrinsicHeight) {
            z2 = true;
            float fMin = Math.min(i / intrinsicWidth, i2 / intrinsicHeight);
            canvas.save();
            canvas.scale(fMin, fMin, i3, i4);
        }
        if (z) {
            int i5 = intrinsicWidth / 2;
            int i6 = intrinsicHeight / 2;
            drawable.setBounds(i3 - i5, i4 - i6, i5 + i3, i6 + i4);
        }
        drawable.draw(canvas);
        canvas.save();
        float f = i3;
        float f2 = i4;
        canvas.rotate((this.mHour / 12.0f) * 360.0f, f, f2);
        Drawable drawable2 = this.mHourHand;
        if (z) {
            int intrinsicWidth2 = drawable2.getIntrinsicWidth() / 2;
            int intrinsicHeight2 = drawable2.getIntrinsicHeight() / 2;
            drawable2.setBounds(i3 - intrinsicWidth2, i4 - intrinsicHeight2, intrinsicWidth2 + i3, intrinsicHeight2 + i4);
        }
        drawable2.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.rotate((this.mMinutes / 60.0f) * 360.0f, f, f2);
        Drawable drawable3 = this.mMinuteHand;
        if (z) {
            int intrinsicWidth3 = drawable3.getIntrinsicWidth() / 2;
            int intrinsicHeight3 = drawable3.getIntrinsicHeight() / 2;
            drawable3.setBounds(i3 - intrinsicWidth3, i4 - intrinsicHeight3, i3 + intrinsicWidth3, i4 + intrinsicHeight3);
        }
        drawable3.draw(canvas);
        canvas.restore();
        if (z2) {
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        this.mCalendar.setToNow();
        int i = this.mCalendar.hour;
        float f = this.mCalendar.minute + (this.mCalendar.second / 60.0f);
        this.mMinutes = f;
        this.mHour = i + (f / 60.0f);
        this.mChanged = true;
        updateContentDescription(this.mCalendar);
    }

    private void updateContentDescription(Time time) {
        setContentDescription(DateUtils.formatDateTime(this.mContext, time.toMillis(false), 129));
    }
}
