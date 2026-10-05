package android.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public abstract class OrientationEventListener {
    private static final boolean DEBUG = false;
    public static final int ORIENTATION_UNKNOWN = -1;
    private static final String TAG = "OrientationEventListener";
    private static final boolean localLOGV = false;
    private boolean mEnabled;
    private OrientationListener mOldListener;
    private int mOrientation;
    private int mRate;
    private Sensor mSensor;
    private SensorEventListener mSensorEventListener;
    private SensorManager mSensorManager;

    public abstract void onOrientationChanged(int i);

    public OrientationEventListener(Context context) {
        this(context, 3);
    }

    public OrientationEventListener(Context context, int i) {
        this.mOrientation = -1;
        this.mEnabled = false;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.mSensorManager = sensorManager;
        this.mRate = i;
        Sensor defaultSensor = sensorManager.getDefaultSensor(1);
        this.mSensor = defaultSensor;
        if (defaultSensor != null) {
            this.mSensorEventListener = new SensorEventListenerImpl();
        }
    }

    void registerListener(OrientationListener orientationListener) {
        this.mOldListener = orientationListener;
    }

    public void enable() {
        Sensor sensor = this.mSensor;
        if (sensor == null) {
            Log.w(TAG, "Cannot detect sensors. Not enabled");
        } else {
            if (this.mEnabled) {
                return;
            }
            this.mSensorManager.registerListener(this.mSensorEventListener, sensor, this.mRate);
            this.mEnabled = true;
        }
    }

    public void disable() {
        if (this.mSensor == null) {
            Log.w(TAG, "Cannot detect sensors. Invalid disable");
        } else if (this.mEnabled) {
            this.mSensorManager.unregisterListener(this.mSensorEventListener);
            this.mEnabled = false;
        }
    }

    class SensorEventListenerImpl implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        SensorEventListenerImpl() {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            int iRound;
            float[] fArr = sensorEvent.values;
            float f = -fArr[0];
            float f2 = -fArr[1];
            float f3 = -fArr[2];
            if (((f * f) + (f2 * f2)) * 4.0f >= f3 * f3) {
                iRound = 90 - Math.round(((float) Math.atan2(-f2, f)) * 57.29578f);
                while (iRound >= 360) {
                    iRound -= 360;
                }
                while (iRound < 0) {
                    iRound += 360;
                }
            } else {
                iRound = -1;
            }
            if (OrientationEventListener.this.mOldListener != null) {
                OrientationEventListener.this.mOldListener.onSensorChanged(1, sensorEvent.values);
            }
            if (iRound != OrientationEventListener.this.mOrientation) {
                OrientationEventListener.this.mOrientation = iRound;
                OrientationEventListener.this.onOrientationChanged(iRound);
            }
        }
    }

    public boolean canDetectOrientation() {
        return this.mSensor != null;
    }
}
