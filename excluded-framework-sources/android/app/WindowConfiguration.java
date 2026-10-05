package android.app;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.proto.ProtoOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class WindowConfiguration implements Parcelable, Comparable<WindowConfiguration> {
    public static final int ACTIVITY_TYPE_ASSISTANT = 4;
    public static final int ACTIVITY_TYPE_HOME = 2;
    public static final int ACTIVITY_TYPE_RECENTS = 3;
    public static final int ACTIVITY_TYPE_STANDARD = 1;
    public static final int ACTIVITY_TYPE_UNDEFINED = 0;
    public static final Parcelable.Creator<WindowConfiguration> CREATOR = new Parcelable.Creator<WindowConfiguration>() { // from class: android.app.WindowConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WindowConfiguration createFromParcel(Parcel parcel) {
            return new WindowConfiguration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WindowConfiguration[] newArray(int i) {
            return new WindowConfiguration[i];
        }
    };
    public static final int PINNED_WINDOWING_MODE_ELEVATION_IN_DIP = 5;
    public static final int WINDOWING_MODE_FREEFORM = 5;
    public static final int WINDOWING_MODE_FULLSCREEN = 1;
    public static final int WINDOWING_MODE_FULLSCREEN_OR_SPLIT_SCREEN_SECONDARY = 4;
    public static final int WINDOWING_MODE_PINNED = 2;
    public static final int WINDOWING_MODE_SPLIT_SCREEN_PRIMARY = 3;
    public static final int WINDOWING_MODE_SPLIT_SCREEN_SECONDARY = 4;
    public static final int WINDOWING_MODE_UNDEFINED = 0;
    public static final int WINDOW_CONFIG_ACTIVITY_TYPE = 8;
    public static final int WINDOW_CONFIG_APP_BOUNDS = 2;
    public static final int WINDOW_CONFIG_BOUNDS = 1;
    public static final int WINDOW_CONFIG_WINDOWING_MODE = 4;
    private int mActivityType;
    private Rect mAppBounds;
    private Rect mBounds;
    private int mWindowingMode;

    public @interface ActivityType {
    }

    public @interface WindowConfig {
    }

    public @interface WindowingMode {
    }

    public static boolean isFloating(int i) {
        return i == 5 || i == 2;
    }

    public static boolean supportSplitScreenWindowingMode(int i) {
        return i != 4;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WindowConfiguration() {
        this.mBounds = new Rect();
        unset();
    }

    public WindowConfiguration(WindowConfiguration windowConfiguration) {
        this.mBounds = new Rect();
        setTo(windowConfiguration);
    }

    private WindowConfiguration(Parcel parcel) {
        this.mBounds = new Rect();
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mBounds, i);
        parcel.writeParcelable(this.mAppBounds, i);
        parcel.writeInt(this.mWindowingMode);
        parcel.writeInt(this.mActivityType);
    }

    private void readFromParcel(Parcel parcel) {
        this.mBounds = (Rect) parcel.readParcelable(Rect.class.getClassLoader());
        this.mAppBounds = (Rect) parcel.readParcelable(Rect.class.getClassLoader());
        this.mWindowingMode = parcel.readInt();
        this.mActivityType = parcel.readInt();
    }

    public void setBounds(Rect rect) {
        if (rect == null) {
            this.mBounds.setEmpty();
        } else {
            this.mBounds.set(rect);
        }
    }

    public void setAppBounds(Rect rect) {
        if (rect == null) {
            this.mAppBounds = null;
        } else {
            setAppBounds(rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    public void setAppBounds(int i, int i2, int i3, int i4) {
        if (this.mAppBounds == null) {
            this.mAppBounds = new Rect();
        }
        this.mAppBounds.set(i, i2, i3, i4);
    }

    public Rect getAppBounds() {
        return this.mAppBounds;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    public void setWindowingMode(int i) {
        this.mWindowingMode = i;
    }

    public int getWindowingMode() {
        return this.mWindowingMode;
    }

    public void setActivityType(int i) {
        if (this.mActivityType == i) {
            return;
        }
        if (ActivityThread.isSystem() && this.mActivityType != 0 && i != 0) {
            throw new IllegalStateException("Can't change activity type once set: " + this + " activityType=" + activityTypeToString(i));
        }
        this.mActivityType = i;
    }

    public int getActivityType() {
        return this.mActivityType;
    }

    public void setTo(WindowConfiguration windowConfiguration) {
        setBounds(windowConfiguration.mBounds);
        setAppBounds(windowConfiguration.mAppBounds);
        setWindowingMode(windowConfiguration.mWindowingMode);
        setActivityType(windowConfiguration.mActivityType);
    }

    public void unset() {
        setToDefaults();
    }

    public void setToDefaults() {
        setAppBounds(null);
        setBounds(null);
        setWindowingMode(0);
        setActivityType(0);
    }

    public int updateFrom(WindowConfiguration windowConfiguration) {
        int i;
        if (windowConfiguration.mBounds.isEmpty() || windowConfiguration.mBounds.equals(this.mBounds)) {
            i = 0;
        } else {
            i = 1;
            setBounds(windowConfiguration.mBounds);
        }
        Rect rect = windowConfiguration.mAppBounds;
        if (rect != null && !rect.equals(this.mAppBounds)) {
            i |= 2;
            setAppBounds(windowConfiguration.mAppBounds);
        }
        int i2 = windowConfiguration.mWindowingMode;
        if (i2 != 0 && this.mWindowingMode != i2) {
            i |= 4;
            setWindowingMode(i2);
        }
        int i3 = windowConfiguration.mActivityType;
        if (i3 == 0 || this.mActivityType == i3) {
            return i;
        }
        int i4 = i | 8;
        setActivityType(i3);
        return i4;
    }

    public long diff(WindowConfiguration windowConfiguration, boolean z) {
        Rect rect;
        Rect rect2;
        long j = !this.mBounds.equals(windowConfiguration.mBounds) ? 1L : 0L;
        if ((z || windowConfiguration.mAppBounds != null) && (rect = this.mAppBounds) != (rect2 = windowConfiguration.mAppBounds) && (rect == null || !rect.equals(rect2))) {
            j |= 2;
        }
        if ((z || windowConfiguration.mWindowingMode != 0) && this.mWindowingMode != windowConfiguration.mWindowingMode) {
            j |= 4;
        }
        return ((z || windowConfiguration.mActivityType != 0) && this.mActivityType != windowConfiguration.mActivityType) ? j | 8 : j;
    }

    @Override // java.lang.Comparable
    public int compareTo(WindowConfiguration windowConfiguration) {
        if (this.mAppBounds == null && windowConfiguration.mAppBounds != null) {
            return 1;
        }
        if (this.mAppBounds != null && windowConfiguration.mAppBounds == null) {
            return -1;
        }
        Rect rect = this.mAppBounds;
        if (rect != null && windowConfiguration.mAppBounds != null) {
            int i = rect.left - windowConfiguration.mAppBounds.left;
            if (i != 0) {
                return i;
            }
            int i2 = this.mAppBounds.top - windowConfiguration.mAppBounds.top;
            if (i2 != 0) {
                return i2;
            }
            int i3 = this.mAppBounds.right - windowConfiguration.mAppBounds.right;
            if (i3 != 0) {
                return i3;
            }
            int i4 = this.mAppBounds.bottom - windowConfiguration.mAppBounds.bottom;
            if (i4 != 0) {
                return i4;
            }
        }
        int i5 = this.mBounds.left - windowConfiguration.mBounds.left;
        if (i5 != 0) {
            return i5;
        }
        int i6 = this.mBounds.top - windowConfiguration.mBounds.top;
        if (i6 != 0) {
            return i6;
        }
        int i7 = this.mBounds.right - windowConfiguration.mBounds.right;
        if (i7 != 0) {
            return i7;
        }
        int i8 = this.mBounds.bottom - windowConfiguration.mBounds.bottom;
        if (i8 != 0) {
            return i8;
        }
        int i9 = this.mWindowingMode - windowConfiguration.mWindowingMode;
        if (i9 != 0) {
            return i9;
        }
        int i10 = this.mActivityType - windowConfiguration.mActivityType;
        if (i10 != 0) {
        }
        return i10;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return (obj instanceof WindowConfiguration) && compareTo((WindowConfiguration) obj) == 0;
    }

    public int hashCode() {
        Rect rect = this.mAppBounds;
        return ((((((rect != null ? 0 + rect.hashCode() : 0) * 31) + this.mBounds.hashCode()) * 31) + this.mWindowingMode) * 31) + this.mActivityType;
    }

    public String toString() {
        return "{ mBounds=" + this.mBounds + " mAppBounds=" + this.mAppBounds + " mWindowingMode=" + windowingModeToString(this.mWindowingMode) + " mActivityType=" + activityTypeToString(this.mActivityType) + "}";
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        Rect rect = this.mAppBounds;
        if (rect != null) {
            rect.writeToProto(protoOutputStream, 1146756268033L);
        }
        protoOutputStream.write(1120986464258L, this.mWindowingMode);
        protoOutputStream.write(1120986464259L, this.mActivityType);
        protoOutputStream.end(jStart);
    }

    public boolean hasWindowShadow() {
        return tasksAreFloating();
    }

    public boolean hasWindowDecorCaption() {
        return this.mWindowingMode == 5;
    }

    public boolean canResizeTask() {
        return this.mWindowingMode == 5;
    }

    public boolean persistTaskBounds() {
        return this.mWindowingMode == 5;
    }

    public boolean tasksAreFloating() {
        return isFloating(this.mWindowingMode);
    }

    public boolean canReceiveKeys() {
        return this.mWindowingMode != 2;
    }

    public boolean isAlwaysOnTop() {
        return this.mWindowingMode == 2;
    }

    public boolean keepVisibleDeadAppWindowOnScreen() {
        return this.mWindowingMode != 2;
    }

    public boolean useWindowFrameForBackdrop() {
        int i = this.mWindowingMode;
        return i == 5 || i == 2;
    }

    public boolean windowsAreScaleable() {
        return this.mWindowingMode == 2;
    }

    public boolean hasMovementAnimations() {
        return this.mWindowingMode != 2;
    }

    public boolean supportSplitScreenWindowingMode() {
        return supportSplitScreenWindowingMode(this.mActivityType);
    }

    public static String windowingModeToString(int i) {
        if (i == 0) {
            return "undefined";
        }
        if (i == 1) {
            return "fullscreen";
        }
        if (i == 2) {
            return ContactsContract.ContactOptionsColumns.PINNED;
        }
        if (i == 3) {
            return "split-screen-primary";
        }
        if (i != 4) {
            return i != 5 ? String.valueOf(i) : "freeform";
        }
        return "split-screen-secondary";
    }

    public static String activityTypeToString(int i) {
        if (i == 0) {
            return "undefined";
        }
        if (i == 1) {
            return "standard";
        }
        if (i == 2) {
            return CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME;
        }
        if (i != 3) {
            return i != 4 ? String.valueOf(i) : Settings.Secure.ASSISTANT;
        }
        return "recents";
    }
}
