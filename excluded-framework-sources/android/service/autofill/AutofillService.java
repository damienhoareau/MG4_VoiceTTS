package android.service.autofill;

import android.app.Service;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.autofill.AutofillManager;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AutofillService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.autofill.AutofillService";
    public static final String SERVICE_META_DATA = "android.autofill";
    private static final String TAG = "AutofillService";
    private Handler mHandler;
    private final IAutoFillService mInterface = new IAutoFillService.Stub() { // from class: android.service.autofill.AutofillService.1
        @Override // android.service.autofill.IAutoFillService
        public void onConnectedStateChanged(boolean z) {
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(z ? new Consumer() { // from class: android.service.autofill.-$$Lambda$amIBeR2CTPTUHkT8htLcarZmUYc
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AutofillService) obj).onConnected();
                }
            } : new Consumer() { // from class: android.service.autofill.-$$Lambda$eWz26esczusoIA84WEwFlxQuDGQ
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AutofillService) obj).onDisconnected();
                }
            }, AutofillService.this));
        }

        @Override // android.service.autofill.IAutoFillService
        public void onFillRequest(FillRequest fillRequest, IFillCallback iFillCallback) {
            ICancellationSignal iCancellationSignalCreateTransport = CancellationSignal.createTransport();
            try {
                iFillCallback.onCancellable(iCancellationSignalCreateTransport);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.autofill.-$$Lambda$I0gCKFrBTO70VZfSZTq2fj-wyG8
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((AutofillService) obj).onFillRequest((FillRequest) obj2, (CancellationSignal) obj3, (FillCallback) obj4);
                }
            }, AutofillService.this, fillRequest, CancellationSignal.fromTransport(iCancellationSignalCreateTransport), new FillCallback(iFillCallback, fillRequest.getId())));
        }

        @Override // android.service.autofill.IAutoFillService
        public void onSaveRequest(SaveRequest saveRequest, ISaveCallback iSaveCallback) {
            AutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.autofill.-$$Lambda$KrOZIsyY-3lh3prHWFldsWopHBw
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AutofillService) obj).onSaveRequest((SaveRequest) obj2, (SaveCallback) obj3);
                }
            }, AutofillService.this, saveRequest, new SaveCallback(iSaveCallback)));
        }
    };

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public abstract void onFillRequest(FillRequest fillRequest, CancellationSignal cancellationSignal, FillCallback fillCallback);

    public abstract void onSaveRequest(SaveRequest saveRequest, SaveCallback saveCallback);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Log.w(TAG, "Tried to bind to wrong intent: " + intent);
        return null;
    }

    public final FillEventHistory getFillEventHistory() {
        AutofillManager autofillManager = (AutofillManager) getSystemService(AutofillManager.class);
        if (autofillManager == null) {
            return null;
        }
        return autofillManager.getFillEventHistory();
    }
}
