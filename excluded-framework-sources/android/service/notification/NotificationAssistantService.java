package android.service.notification;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public abstract class NotificationAssistantService extends NotificationListenerService {
    public static final String SERVICE_INTERFACE = "android.service.notification.NotificationAssistantService";
    private static final String TAG = "NotificationAssistants";
    protected Handler mHandler;

    public abstract Adjustment onNotificationEnqueued(StatusBarNotification statusBarNotification);

    public abstract void onNotificationSnoozedUntilContext(StatusBarNotification statusBarNotification, String str);

    @Override // android.service.notification.NotificationListenerService, android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        this.mHandler = new MyHandler(getContext().getMainLooper());
    }

    @Override // android.service.notification.NotificationListenerService, android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new NotificationAssistantServiceWrapper();
        }
        return this.mWrapper;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, NotificationStats notificationStats, int i) {
        onNotificationRemoved(statusBarNotification, rankingMap, i);
    }

    public final void adjustNotification(Adjustment adjustment) {
        if (isBound()) {
            try {
                getNotificationInterface().applyAdjustmentFromAssistant(this.mWrapper, adjustment);
            } catch (RemoteException e) {
                Log.v(TAG, "Unable to contact notification manager", e);
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public final void adjustNotifications(List<Adjustment> list) {
        if (isBound()) {
            try {
                getNotificationInterface().applyAdjustmentsFromAssistant(this.mWrapper, list);
            } catch (RemoteException e) {
                Log.v(TAG, "Unable to contact notification manager", e);
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public final void unsnoozeNotification(String str) {
        if (isBound()) {
            try {
                getNotificationInterface().unsnoozeNotificationFromAssistant(this.mWrapper, str);
            } catch (RemoteException e) {
                Log.v(TAG, "Unable to contact notification manager", e);
            }
        }
    }

    private class NotificationAssistantServiceWrapper extends NotificationListenerService.NotificationListenerWrapper {
        private NotificationAssistantServiceWrapper() {
            super();
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationEnqueued(IStatusBarNotificationHolder iStatusBarNotificationHolder) {
            try {
                StatusBarNotification statusBarNotification = iStatusBarNotificationHolder.get();
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = statusBarNotification;
                NotificationAssistantService.this.mHandler.obtainMessage(1, someArgsObtain).sendToTarget();
            } catch (RemoteException e) {
                Log.w(NotificationAssistantService.TAG, "onNotificationEnqueued: Error receiving StatusBarNotification", e);
            }
        }

        @Override // android.service.notification.NotificationListenerService.NotificationListenerWrapper, android.service.notification.INotificationListener
        public void onNotificationSnoozedUntilContext(IStatusBarNotificationHolder iStatusBarNotificationHolder, String str) throws RemoteException {
            try {
                StatusBarNotification statusBarNotification = iStatusBarNotificationHolder.get();
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = statusBarNotification;
                someArgsObtain.arg2 = str;
                NotificationAssistantService.this.mHandler.obtainMessage(2, someArgsObtain).sendToTarget();
            } catch (RemoteException e) {
                Log.w(NotificationAssistantService.TAG, "onNotificationSnoozed: Error receiving StatusBarNotification", e);
            }
        }
    }

    private final class MyHandler extends Handler {
        public static final int MSG_ON_NOTIFICATION_ENQUEUED = 1;
        public static final int MSG_ON_NOTIFICATION_SNOOZED = 2;

        public MyHandler(Looper looper) {
            super(looper, null, false);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 2) {
                    return;
                }
                SomeArgs someArgs = (SomeArgs) message.obj;
                StatusBarNotification statusBarNotification = (StatusBarNotification) someArgs.arg1;
                String str = (String) someArgs.arg2;
                someArgs.recycle();
                NotificationAssistantService.this.onNotificationSnoozedUntilContext(statusBarNotification, str);
                return;
            }
            SomeArgs someArgs2 = (SomeArgs) message.obj;
            StatusBarNotification statusBarNotification2 = (StatusBarNotification) someArgs2.arg1;
            someArgs2.recycle();
            Adjustment adjustmentOnNotificationEnqueued = NotificationAssistantService.this.onNotificationEnqueued(statusBarNotification2);
            if (adjustmentOnNotificationEnqueued == null || !NotificationAssistantService.this.isBound()) {
                return;
            }
            try {
                NotificationAssistantService.this.getNotificationInterface().applyEnqueuedAdjustmentFromAssistant(NotificationAssistantService.this.mWrapper, adjustmentOnNotificationEnqueued);
            } catch (RemoteException e) {
                Log.v(NotificationAssistantService.TAG, "Unable to contact notification manager", e);
                throw e.rethrowFromSystemServer();
            }
        }
    }
}
