package android.hardware;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface SensorListener {
    void onAccuracyChanged(int i, int i2);

    void onSensorChanged(int i, float[] fArr);
}
