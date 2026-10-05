package android.widget;

import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.R;
import com.android.server.SystemConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
public class Toast {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    static final String TAG = "Toast";
    static final boolean localLOGV = false;
    static String mVehicleVariant;
    private static INotificationManager sService;
    final Context mContext;
    int mDuration;
    View mNextView;
    final TN mTN;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public Toast(Context context) {
        this(context, null);
    }

    public Toast(Context context, Looper looper) {
        this.mContext = context;
        TN tn = new TN(context.getPackageName(), looper);
        this.mTN = tn;
        tn.mY = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);
        this.mTN.mGravity = context.getResources().getInteger(R.integer.config_toastDefaultGravity);
        String str = SystemProperties.get("ro.product.name", "");
        mVehicleVariant = str;
        if (str.equals("mt2712_saic_ec32")) {
            this.mTN.mGravity = 17;
        }
    }

    public void show() {
        if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        INotificationManager service = getService();
        String opPackageName = this.mContext.getOpPackageName();
        TN tn = this.mTN;
        tn.mNextView = this.mNextView;
        try {
            service.enqueueToast(opPackageName, tn, this.mDuration);
        } catch (RemoteException unused) {
        }
    }

    public void cancel() {
        this.mTN.cancel();
    }

    public void setView(View view) {
        this.mNextView = view;
    }

    public View getView() {
        return this.mNextView;
    }

    public void setDuration(int i) {
        this.mDuration = i;
        this.mTN.mDuration = i;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float f, float f2) {
        this.mTN.mHorizontalMargin = f;
        this.mTN.mVerticalMargin = f2;
    }

    public float getHorizontalMargin() {
        return this.mTN.mHorizontalMargin;
    }

    public float getVerticalMargin() {
        return this.mTN.mVerticalMargin;
    }

    public void setGravity(int i, int i2, int i3) {
        this.mTN.mGravity = i;
        this.mTN.mX = i2;
        this.mTN.mY = i3;
    }

    public int getGravity() {
        return this.mTN.mGravity;
    }

    public int getXOffset() {
        return this.mTN.mX;
    }

    public int getYOffset() {
        return this.mTN.mY;
    }

    public WindowManager.LayoutParams getWindowParams() {
        return this.mTN.mParams;
    }

    public static Toast makeText(Context context, CharSequence charSequence, int i) {
        return makeText(context, null, charSequence, i);
    }

    public static Toast makeText(Context context, Looper looper, CharSequence charSequence, int i) {
        Toast toast = new Toast(context, looper);
        View viewInflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.transient_notification, (ViewGroup) null);
        ((TextView) viewInflate.findViewById(16908299)).setText(charSequence);
        if (SystemConfig.getInstance().isSecondDisplayApp("mt2712_saic_ec32", context.getPackageName())) {
            Context applicationContext = viewInflate.getContext().getApplicationContext();
            if (applicationContext == null) {
                applicationContext = viewInflate.getContext();
            }
            applicationContext.updateDisplay(1);
            viewInflate.setBackgroundResource(R.drawable.bg_toast_r_screen_c);
        } else if (mVehicleVariant.equals("mt2712_saic_ec32")) {
            viewInflate.setBackgroundResource(R.drawable.bg_toast_l_screen_a);
        }
        toast.mNextView = viewInflate;
        toast.mDuration = i;
        return toast;
    }

    public static Toast makeText(Context context, int i, int i2) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(i), i2);
    }

    public void setText(int i) {
        setText(this.mContext.getText(i));
    }

    public void setText(CharSequence charSequence) {
        View view = this.mNextView;
        if (view == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView textView = (TextView) view.findViewById(16908299);
        if (textView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        textView.setText(charSequence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static INotificationManager getService() {
        INotificationManager iNotificationManager = sService;
        if (iNotificationManager != null) {
            return iNotificationManager;
        }
        INotificationManager iNotificationManagerAsInterface = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        sService = iNotificationManagerAsInterface;
        return iNotificationManagerAsInterface;
    }

    private static class TN extends ITransientNotification.Stub {
        private static final int CANCEL = 2;
        private static final int HIDE = 1;
        static final long LONG_DURATION_TIMEOUT = 7000;
        static final long SHORT_DURATION_TIMEOUT = 4000;
        private static final int SHOW = 0;
        int mDuration;
        int mGravity;
        final Handler mHandler;
        float mHorizontalMargin;
        View mNextView;
        String mPackageName;
        private final WindowManager.LayoutParams mParams;
        float mVerticalMargin;
        View mView;
        WindowManager mWM;
        int mX;
        int mY;

        TN(String str, Looper looper) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.mParams = layoutParams;
            layoutParams.height = -2;
            layoutParams.width = -2;
            layoutParams.format = -3;
            layoutParams.windowAnimations = 16973828;
            layoutParams.type = 2005;
            layoutParams.setTitle(Toast.TAG);
            layoutParams.flags = 152;
            this.mPackageName = str;
            if (looper == null && (looper = Looper.myLooper()) == null) {
                throw new RuntimeException("Can't toast on a thread that has not called Looper.prepare()");
            }
            this.mHandler = new Handler(looper, null) { // from class: android.widget.Toast.TN.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    int i = message.what;
                    if (i == 0) {
                        TN.this.handleShow((IBinder) message.obj);
                    } else if (i == 1) {
                        TN.this.handleHide();
                        TN.this.mNextView = null;
                    } else {
                        if (i != 2) {
                            return;
                        }
                        TN.this.handleHide();
                        TN.this.mNextView = null;
                        try {
                            Toast.getService().cancelToast(TN.this.mPackageName, TN.this);
                        } catch (RemoteException unused) {
                        }
                    }
                }
            };
        }

        @Override // android.app.ITransientNotification
        public void show(IBinder iBinder) {
            this.mHandler.obtainMessage(0, iBinder).sendToTarget();
        }

        @Override // android.app.ITransientNotification
        public void hide() {
            this.mHandler.obtainMessage(1).sendToTarget();
        }

        public void cancel() {
            this.mHandler.obtainMessage(2).sendToTarget();
        }

        public void handleShow(IBinder iBinder) {
            if (this.mHandler.hasMessages(2) || this.mHandler.hasMessages(1) || this.mView == this.mNextView) {
                return;
            }
            handleHide();
            View view = this.mNextView;
            this.mView = view;
            Context applicationContext = view.getContext().getApplicationContext();
            String opPackageName = this.mView.getContext().getOpPackageName();
            if (applicationContext == null) {
                applicationContext = this.mView.getContext();
            }
            this.mWM = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
            int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, this.mView.getContext().getResources().getConfiguration().getLayoutDirection());
            this.mParams.gravity = absoluteGravity;
            if ((absoluteGravity & 7) == 7) {
                this.mParams.horizontalWeight = 1.0f;
            }
            if ((absoluteGravity & 112) == 112) {
                this.mParams.verticalWeight = 1.0f;
            }
            this.mParams.x = this.mX;
            this.mParams.y = this.mY;
            if (Toast.mVehicleVariant.equals("mt2712_saic_ec32")) {
                this.mParams.x = 0;
                this.mParams.y = -280;
            }
            this.mParams.verticalMargin = this.mVerticalMargin;
            this.mParams.horizontalMargin = this.mHorizontalMargin;
            this.mParams.packageName = opPackageName;
            this.mParams.hideTimeoutMilliseconds = this.mDuration == 1 ? LONG_DURATION_TIMEOUT : SHORT_DURATION_TIMEOUT;
            this.mParams.token = iBinder;
            if (this.mView.getParent() != null) {
                this.mWM.removeView(this.mView);
            }
            try {
                this.mWM.addView(this.mView, this.mParams);
                trySendAccessibilityEvent();
            } catch (WindowManager.BadTokenException unused) {
            }
        }

        private void trySendAccessibilityEvent() {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mView.getContext());
            if (accessibilityManager.isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(64);
                accessibilityEventObtain.setClassName(getClass().getName());
                accessibilityEventObtain.setPackageName(this.mView.getContext().getPackageName());
                this.mView.dispatchPopulateAccessibilityEvent(accessibilityEventObtain);
                accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
            }
        }

        public void handleHide() {
            View view = this.mView;
            if (view != null) {
                if (view.getParent() != null) {
                    this.mWM.removeViewImmediate(this.mView);
                }
                try {
                    Toast.getService().finishToken(this.mPackageName, this);
                } catch (RemoteException unused) {
                }
                this.mView = null;
            }
        }
    }
}
