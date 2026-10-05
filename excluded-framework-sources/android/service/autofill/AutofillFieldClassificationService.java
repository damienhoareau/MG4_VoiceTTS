package android.service.autofill;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.util.Log;
import android.view.autofill.AutofillValue;
import com.android.internal.util.function.HexConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public abstract class AutofillFieldClassificationService extends Service {
    public static final String EXTRA_SCORES = "scores";
    public static final String SERVICE_INTERFACE = "android.service.autofill.AutofillFieldClassificationService";
    public static final String SERVICE_META_DATA_KEY_AVAILABLE_ALGORITHMS = "android.autofill.field_classification.available_algorithms";
    public static final String SERVICE_META_DATA_KEY_DEFAULT_ALGORITHM = "android.autofill.field_classification.default_algorithm";
    private static final String TAG = "AutofillFieldClassificationService";
    private final Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
    private AutofillFieldClassificationServiceWrapper mWrapper;

    /* JADX INFO: Access modifiers changed from: private */
    public void getScores(RemoteCallback remoteCallback, String str, Bundle bundle, List<AutofillValue> list, String[] strArr) {
        Bundle bundle2 = new Bundle();
        float[][] fArrOnGetScores = onGetScores(str, bundle, list, Arrays.asList(strArr));
        if (fArrOnGetScores != null) {
            bundle2.putParcelable(EXTRA_SCORES, new Scores(fArrOnGetScores));
        }
        remoteCallback.sendResult(bundle2);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWrapper = new AutofillFieldClassificationServiceWrapper();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    @SystemApi
    public float[][] onGetScores(String str, Bundle bundle, List<AutofillValue> list, List<String> list2) {
        Log.e(TAG, "service implementation (" + getClass() + " does not implement onGetScore()");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class AutofillFieldClassificationServiceWrapper extends IAutofillFieldClassificationService.Stub {
        private AutofillFieldClassificationServiceWrapper() {
        }

        @Override // android.service.autofill.IAutofillFieldClassificationService
        public void getScores(RemoteCallback remoteCallback, String str, Bundle bundle, List<AutofillValue> list, String[] strArr) throws RemoteException {
            AutofillFieldClassificationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new HexConsumer() { // from class: android.service.autofill.-$$Lambda$AutofillFieldClassificationService$AutofillFieldClassificationServiceWrapper$LVFO8nQdiSarBMY_Qsf1G30GEZQ
                @Override // com.android.internal.util.function.HexConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                    ((AutofillFieldClassificationService) obj).getScores((RemoteCallback) obj2, (String) obj3, (Bundle) obj4, (List) obj5, (String[]) obj6);
                }
            }, AutofillFieldClassificationService.this, remoteCallback, str, bundle, list, strArr));
        }
    }

    public static final class Scores implements Parcelable {
        public static final Parcelable.Creator<Scores> CREATOR = new Parcelable.Creator<Scores>() { // from class: android.service.autofill.AutofillFieldClassificationService.Scores.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Scores createFromParcel(Parcel parcel) {
                return new Scores(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Scores[] newArray(int i) {
                return new Scores[i];
            }
        };
        public final float[][] scores;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        private Scores(Parcel parcel) {
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            this.scores = (float[][]) Array.newInstance((Class<?>) float.class, i, i2);
            for (int i3 = 0; i3 < i; i3++) {
                for (int i4 = 0; i4 < i2; i4++) {
                    this.scores[i3][i4] = parcel.readFloat();
                }
            }
        }

        private Scores(float[][] fArr) {
            this.scores = fArr;
        }

        public String toString() {
            float[][] fArr = this.scores;
            int length = fArr.length;
            int length2 = length > 0 ? fArr[0].length : 0;
            StringBuilder sb = new StringBuilder("Scores [");
            sb.append(length);
            sb.append("x");
            sb.append(length2);
            sb.append("] ");
            for (int i = 0; i < length; i++) {
                sb.append(i);
                sb.append(": ");
                sb.append(Arrays.toString(this.scores[i]));
                sb.append(' ');
            }
            return sb.toString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            float[][] fArr = this.scores;
            int length = fArr.length;
            int length2 = fArr[0].length;
            parcel.writeInt(length);
            parcel.writeInt(length2);
            for (int i2 = 0; i2 < length; i2++) {
                for (int i3 = 0; i3 < length2; i3++) {
                    parcel.writeFloat(this.scores[i2][i3]);
                }
            }
        }
    }
}
