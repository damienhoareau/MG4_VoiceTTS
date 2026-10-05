package android.widget;

import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.icu.util.Calendar;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import libcore.icu.DateUtilsBridge;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class DateTimeView extends TextView {
    private static final int SHOW_MONTH_DAY_YEAR = 1;
    private static final int SHOW_TIME = 0;
    private static final ThreadLocal<ReceiverInfo> sReceiverInfo = new ThreadLocal<>();
    int mLastDisplay;
    DateFormat mLastFormat;
    private String mNowText;
    private boolean mShowRelativeTime;
    Date mTime;
    long mTimeMillis;
    private long mUpdateTimeMillis;

    public DateTimeView(Context context) {
        this(context, null);
    }

    public DateTimeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLastDisplay = -1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DateTimeView, 0, 0);
        int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            if (typedArrayObtainStyledAttributes.getIndex(i) == 0) {
                setShowRelativeTime(typedArrayObtainStyledAttributes.getBoolean(i, false));
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ReceiverInfo receiverInfo = sReceiverInfo.get();
        if (receiverInfo == null) {
            receiverInfo = new ReceiverInfo();
            sReceiverInfo.set(receiverInfo);
        }
        receiverInfo.addView(this);
        if (this.mShowRelativeTime) {
            update();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ReceiverInfo receiverInfo = sReceiverInfo.get();
        if (receiverInfo != null) {
            receiverInfo.removeView(this);
        }
    }

    @RemotableViewMethod
    public void setTime(long j) {
        Time time = new Time();
        time.set(j);
        this.mTimeMillis = time.toMillis(false);
        this.mTime = new Date(time.year - 1900, time.month, time.monthDay, time.hour, time.minute, 0);
        update();
    }

    @RemotableViewMethod
    public void setShowRelativeTime(boolean z) {
        this.mShowRelativeTime = z;
        updateNowText();
        update();
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int i) {
        boolean z = i != 8 && getVisibility() == 8;
        super.setVisibility(i);
        if (z) {
            update();
        }
    }

    void update() {
        DateFormat timeFormat;
        if (this.mTime == null || getVisibility() == 8) {
            return;
        }
        if (this.mShowRelativeTime) {
            updateRelativeTime();
            return;
        }
        Time time = new Time();
        time.set(this.mTimeMillis);
        int i = 0;
        time.second = 0;
        time.hour -= 12;
        long millis = time.toMillis(false);
        time.hour += 12;
        long millis2 = time.toMillis(false);
        time.hour = 0;
        time.minute = 0;
        long millis3 = time.toMillis(false);
        time.monthDay++;
        long millis4 = time.toMillis(false);
        time.set(System.currentTimeMillis());
        time.second = 0;
        long jNormalize = time.normalize(false);
        if ((jNormalize < millis3 || jNormalize >= millis4) && (jNormalize < millis || jNormalize >= millis2)) {
            i = 1;
        }
        if (i != this.mLastDisplay || (timeFormat = this.mLastFormat) == null) {
            if (i == 0) {
                timeFormat = getTimeFormat();
            } else if (i == 1) {
                timeFormat = DateFormat.getDateInstance(3);
            } else {
                throw new RuntimeException("unknown display value: " + i);
            }
            this.mLastFormat = timeFormat;
        }
        setText(timeFormat.format(this.mTime));
        if (i == 0) {
            if (millis2 <= millis4) {
                millis2 = millis4;
            }
            this.mUpdateTimeMillis = millis2;
        } else {
            if (this.mTimeMillis < jNormalize) {
                this.mUpdateTimeMillis = 0L;
                return;
            }
            if (millis >= millis3) {
                millis = millis3;
            }
            this.mUpdateTimeMillis = millis;
        }
    }

    private void updateRelativeTime() {
        int iMax;
        String str;
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jAbs = Math.abs(jCurrentTimeMillis - this.mTimeMillis);
        boolean z = jCurrentTimeMillis >= this.mTimeMillis;
        long j = DateUtils.MINUTE_IN_MILLIS;
        if (jAbs < DateUtils.MINUTE_IN_MILLIS) {
            setText(this.mNowText);
            this.mUpdateTimeMillis = this.mTimeMillis + DateUtils.MINUTE_IN_MILLIS + 1;
            return;
        }
        if (jAbs < 3600000) {
            iMax = (int) (jAbs / DateUtils.MINUTE_IN_MILLIS);
            str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_minutes_shortest : R.plurals.duration_minutes_shortest_future, iMax), Integer.valueOf(iMax));
        } else {
            j = 86400000;
            if (jAbs < 86400000) {
                iMax = (int) (jAbs / 3600000);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_hours_shortest : R.plurals.duration_hours_shortest_future, iMax), Integer.valueOf(iMax));
                j = 3600000;
            } else if (jAbs < DateUtils.YEAR_IN_MILLIS) {
                TimeZone timeZone = TimeZone.getDefault();
                iMax = Math.max(Math.abs(dayDistance(timeZone, this.mTimeMillis, jCurrentTimeMillis)), 1);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_days_shortest : R.plurals.duration_days_shortest_future, iMax), Integer.valueOf(iMax));
                if (z || iMax != 1) {
                    this.mUpdateTimeMillis = computeNextMidnight(timeZone);
                    j = -1;
                }
            } else {
                iMax = (int) (jAbs / DateUtils.YEAR_IN_MILLIS);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_years_shortest : R.plurals.duration_years_shortest_future, iMax), Integer.valueOf(iMax));
                j = 31449600000L;
            }
        }
        if (j != -1) {
            if (z) {
                this.mUpdateTimeMillis = this.mTimeMillis + (j * ((long) (iMax + 1))) + 1;
            } else {
                this.mUpdateTimeMillis = (this.mTimeMillis - (j * ((long) iMax))) + 1;
            }
        }
        setText(str);
    }

    private long computeNextMidnight(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(DateUtilsBridge.icuTimeZone(timeZone));
        calendar.add(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateNowText();
        update();
    }

    private void updateNowText() {
        if (this.mShowRelativeTime) {
            this.mNowText = getContext().getResources().getString(R.string.now_string_shortest);
        }
    }

    private static int dayDistance(TimeZone timeZone, long j, long j2) {
        return Time.getJulianDay(j2, timeZone.getOffset(j2) / 1000) - Time.getJulianDay(j, timeZone.getOffset(j) / 1000);
    }

    private DateFormat getTimeFormat() {
        return android.text.format.DateFormat.getTimeFormat(getContext());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearFormatAndUpdate() {
        this.mLastFormat = null;
        update();
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        String str;
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (this.mShowRelativeTime) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            long jAbs = Math.abs(jCurrentTimeMillis - this.mTimeMillis);
            boolean z = jCurrentTimeMillis >= this.mTimeMillis;
            if (jAbs < DateUtils.MINUTE_IN_MILLIS) {
                str = this.mNowText;
            } else if (jAbs < 3600000) {
                int i = (int) (jAbs / DateUtils.MINUTE_IN_MILLIS);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_minutes_relative : R.plurals.duration_minutes_relative_future, i), Integer.valueOf(i));
            } else if (jAbs < 86400000) {
                int i2 = (int) (jAbs / 3600000);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_hours_relative : R.plurals.duration_hours_relative_future, i2), Integer.valueOf(i2));
            } else if (jAbs < DateUtils.YEAR_IN_MILLIS) {
                int iMax = Math.max(Math.abs(dayDistance(TimeZone.getDefault(), this.mTimeMillis, jCurrentTimeMillis)), 1);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_days_relative : R.plurals.duration_days_relative_future, iMax), Integer.valueOf(iMax));
            } else {
                int i3 = (int) (jAbs / DateUtils.YEAR_IN_MILLIS);
                str = String.format(getContext().getResources().getQuantityString(z ? R.plurals.duration_years_relative : R.plurals.duration_years_relative_future, i3), Integer.valueOf(i3));
            }
            accessibilityNodeInfo.setText(str);
        }
    }

    public static void setReceiverHandler(Handler handler) {
        ReceiverInfo receiverInfo = sReceiverInfo.get();
        if (receiverInfo == null) {
            receiverInfo = new ReceiverInfo();
            sReceiverInfo.set(receiverInfo);
        }
        receiverInfo.setHandler(handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class ReceiverInfo {
        private final ArrayList<DateTimeView> mAttachedViews;
        private Handler mHandler;
        private final ContentObserver mObserver;
        private final BroadcastReceiver mReceiver;

        private ReceiverInfo() {
            this.mAttachedViews = new ArrayList<>();
            this.mReceiver = new BroadcastReceiver() { // from class: android.widget.DateTimeView.ReceiverInfo.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (!Intent.ACTION_TIME_TICK.equals(intent.getAction()) || System.currentTimeMillis() >= ReceiverInfo.this.getSoonestUpdateTime()) {
                        ReceiverInfo.this.updateAll();
                    }
                }
            };
            this.mObserver = new ContentObserver(new Handler()) { // from class: android.widget.DateTimeView.ReceiverInfo.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    ReceiverInfo.this.updateAll();
                }
            };
            this.mHandler = new Handler();
        }

        public void addView(DateTimeView dateTimeView) {
            synchronized (this.mAttachedViews) {
                boolean zIsEmpty = this.mAttachedViews.isEmpty();
                this.mAttachedViews.add(dateTimeView);
                if (zIsEmpty) {
                    register(getApplicationContextIfAvailable(dateTimeView.getContext()));
                }
            }
        }

        public void removeView(DateTimeView dateTimeView) {
            synchronized (this.mAttachedViews) {
                if (this.mAttachedViews.remove(dateTimeView) && this.mAttachedViews.isEmpty()) {
                    unregister(getApplicationContextIfAvailable(dateTimeView.getContext()));
                }
            }
        }

        void updateAll() {
            synchronized (this.mAttachedViews) {
                int size = this.mAttachedViews.size();
                for (int i = 0; i < size; i++) {
                    final DateTimeView dateTimeView = this.mAttachedViews.get(i);
                    dateTimeView.post(new Runnable() { // from class: android.widget.-$$Lambda$DateTimeView$ReceiverInfo$AVLnX7U5lTcE9jLnlKKNAT1GUeI
                        @Override // java.lang.Runnable
                        public final void run() {
                            dateTimeView.clearFormatAndUpdate();
                        }
                    });
                }
            }
        }

        long getSoonestUpdateTime() {
            long j;
            synchronized (this.mAttachedViews) {
                int size = this.mAttachedViews.size();
                j = Long.MAX_VALUE;
                for (int i = 0; i < size; i++) {
                    long j2 = this.mAttachedViews.get(i).mUpdateTimeMillis;
                    if (j2 < j) {
                        j = j2;
                    }
                }
            }
            return j;
        }

        static final Context getApplicationContextIfAvailable(Context context) {
            Context applicationContext = context.getApplicationContext();
            return applicationContext != null ? applicationContext : ActivityThread.currentApplication().getApplicationContext();
        }

        void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            context.registerReceiver(this.mReceiver, intentFilter, null, this.mHandler);
        }

        void unregister(Context context) {
            context.unregisterReceiver(this.mReceiver);
        }

        public void setHandler(Handler handler) {
            this.mHandler = handler;
            synchronized (this.mAttachedViews) {
                if (!this.mAttachedViews.isEmpty()) {
                    unregister(this.mAttachedViews.get(0).getContext());
                    register(this.mAttachedViews.get(0).getContext());
                }
            }
        }
    }
}
