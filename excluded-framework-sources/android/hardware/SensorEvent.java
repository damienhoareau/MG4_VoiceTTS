package android.hardware;

/* JADX INFO: loaded from: classes.dex */
public class SensorEvent {
    public int accuracy;
    public Sensor sensor;
    public long timestamp;
    public final float[] values;

    SensorEvent(int i) {
        this.values = new float[i];
    }
}
