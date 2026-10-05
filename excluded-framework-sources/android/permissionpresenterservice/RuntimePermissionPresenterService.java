package android.permissionpresenterservice;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.permission.IRuntimePermissionPresenter;
import android.content.pm.permission.RuntimePermissionPresentationInfo;
import android.content.pm.permission.RuntimePermissionPresenter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallback;
import com.android.internal.os.SomeArgs;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public abstract class RuntimePermissionPresenterService extends Service {
    public static final String SERVICE_INTERFACE = "android.permissionpresenterservice.RuntimePermissionPresenterService";
    private Handler mHandler;

    public abstract List<RuntimePermissionPresentationInfo> onGetAppPermissions(String str);

    public abstract void onRevokeRuntimePermission(String str, String str2);

    @Override // android.content.ContextWrapper
    public final void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        this.mHandler = new MyHandler(context.getMainLooper());
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IRuntimePermissionPresenter.Stub() { // from class: android.permissionpresenterservice.RuntimePermissionPresenterService.1
            @Override // android.content.pm.permission.IRuntimePermissionPresenter
            public void getAppPermissions(String str, RemoteCallback remoteCallback) {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = remoteCallback;
                RuntimePermissionPresenterService.this.mHandler.obtainMessage(1, someArgsObtain).sendToTarget();
            }

            @Override // android.content.pm.permission.IRuntimePermissionPresenter
            public void revokeRuntimePermission(String str, String str2) {
                SomeArgs someArgsObtain = SomeArgs.obtain();
                someArgsObtain.arg1 = str;
                someArgsObtain.arg2 = str2;
                RuntimePermissionPresenterService.this.mHandler.obtainMessage(3, someArgsObtain).sendToTarget();
            }
        };
    }

    private final class MyHandler extends Handler {
        public static final int MSG_GET_APPS_USING_PERMISSIONS = 2;
        public static final int MSG_GET_APP_PERMISSIONS = 1;
        public static final int MSG_REVOKE_APP_PERMISSION = 3;

        public MyHandler(Looper looper) {
            super(looper, null, false);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 3) {
                    return;
                }
                SomeArgs someArgs = (SomeArgs) message.obj;
                String str = (String) someArgs.arg1;
                String str2 = (String) someArgs.arg2;
                someArgs.recycle();
                RuntimePermissionPresenterService.this.onRevokeRuntimePermission(str, str2);
                return;
            }
            SomeArgs someArgs2 = (SomeArgs) message.obj;
            String str3 = (String) someArgs2.arg1;
            RemoteCallback remoteCallback = (RemoteCallback) someArgs2.arg2;
            someArgs2.recycle();
            List<RuntimePermissionPresentationInfo> listOnGetAppPermissions = RuntimePermissionPresenterService.this.onGetAppPermissions(str3);
            if (listOnGetAppPermissions != null && !listOnGetAppPermissions.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putParcelableList(RuntimePermissionPresenter.KEY_RESULT, listOnGetAppPermissions);
                remoteCallback.sendResult(bundle);
                return;
            }
            remoteCallback.sendResult(null);
        }
    }
}
