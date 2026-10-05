package com.android.internal.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.input.InputManager;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManagerPolicyConstants;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class PointerLocationView extends View implements InputManager.InputDeviceListener, WindowManagerPolicyConstants.PointerEventListener {
    private static final String ALT_STRATEGY_PROPERY_KEY = "debug.velocitytracker.alt";
    private static final String TAG = "Pointer";
    private final int ESTIMATE_FUTURE_POINTS;
    private final float ESTIMATE_INTERVAL;
    private final int ESTIMATE_PAST_POINTS;
    private int mActivePointerId;
    private final VelocityTracker mAltVelocity;
    private boolean mCurDown;
    private int mCurNumPointers;
    private final Paint mCurrentPointPaint;
    private int mHeaderBottom;
    private final InputManager mIm;
    private int mMaxNumPointers;
    private final Paint mPaint;
    private final Paint mPathPaint;
    private final ArrayList<PointerState> mPointers;
    private boolean mPrintCoords;
    private RectF mReusableOvalRect;
    private final Paint mTargetPaint;
    private final MotionEvent.PointerCoords mTempCoords;
    private final FasterStringBuilder mText;
    private final Paint mTextBackgroundPaint;
    private final Paint mTextLevelPaint;
    private final Paint.FontMetricsInt mTextMetrics;
    private final Paint mTextPaint;
    private final ViewConfiguration mVC;
    private final VelocityTracker mVelocity;

    public static class PointerState {
        private float mAltXVelocity;
        private float mAltYVelocity;
        private float mBoundingBottom;
        private float mBoundingLeft;
        private float mBoundingRight;
        private float mBoundingTop;
        private boolean mCurDown;
        private boolean mHasBoundingBox;
        private int mToolType;
        private int mTraceCount;
        private float mXVelocity;
        private float mYVelocity;
        private float[] mTraceX = new float[32];
        private float[] mTraceY = new float[32];
        private boolean[] mTraceCurrent = new boolean[32];
        private MotionEvent.PointerCoords mCoords = new MotionEvent.PointerCoords();
        private VelocityTracker.Estimator mEstimator = new VelocityTracker.Estimator();
        private VelocityTracker.Estimator mAltEstimator = new VelocityTracker.Estimator();

        static /* synthetic */ int access$000(PointerState pointerState) {
            return pointerState.mTraceCount;
        }

        static /* synthetic */ boolean access$100(PointerState pointerState) {
            return pointerState.mCurDown;
        }

        static /* synthetic */ float access$1000(PointerState pointerState) {
            return pointerState.mAltXVelocity;
        }

        static /* synthetic */ float access$1100(PointerState pointerState) {
            return pointerState.mAltYVelocity;
        }

        static /* synthetic */ int access$1200(PointerState pointerState) {
            return pointerState.mToolType;
        }

        static /* synthetic */ boolean access$1300(PointerState pointerState) {
            return pointerState.mHasBoundingBox;
        }

        static /* synthetic */ float access$1400(PointerState pointerState) {
            return pointerState.mBoundingLeft;
        }

        static /* synthetic */ float access$1500(PointerState pointerState) {
            return pointerState.mBoundingTop;
        }

        static /* synthetic */ float access$1600(PointerState pointerState) {
            return pointerState.mBoundingRight;
        }

        static /* synthetic */ float access$1700(PointerState pointerState) {
            return pointerState.mBoundingBottom;
        }

        static /* synthetic */ MotionEvent.PointerCoords access$200(PointerState pointerState) {
            return pointerState.mCoords;
        }

        static /* synthetic */ float[] access$300(PointerState pointerState) {
            return pointerState.mTraceX;
        }

        static /* synthetic */ float[] access$400(PointerState pointerState) {
            return pointerState.mTraceY;
        }

        static /* synthetic */ float access$500(PointerState pointerState) {
            return pointerState.mXVelocity;
        }

        static /* synthetic */ float access$600(PointerState pointerState) {
            return pointerState.mYVelocity;
        }

        static /* synthetic */ boolean[] access$700(PointerState pointerState) {
            return pointerState.mTraceCurrent;
        }

        static /* synthetic */ VelocityTracker.Estimator access$800(PointerState pointerState) {
            return pointerState.mEstimator;
        }

        static /* synthetic */ VelocityTracker.Estimator access$900(PointerState pointerState) {
            return pointerState.mAltEstimator;
        }

        public void clearTrace() {
            this.mTraceCount = 0;
        }

        public void addTrace(float f, float f2, boolean z) {
            float[] fArr = this.mTraceX;
            int length = fArr.length;
            int i = this.mTraceCount;
            if (i == length) {
                int i2 = length * 2;
                float[] fArr2 = new float[i2];
                System.arraycopy(fArr, 0, fArr2, 0, i);
                this.mTraceX = fArr2;
                float[] fArr3 = new float[i2];
                System.arraycopy(this.mTraceY, 0, fArr3, 0, this.mTraceCount);
                this.mTraceY = fArr3;
                boolean[] zArr = new boolean[i2];
                System.arraycopy(this.mTraceCurrent, 0, zArr, 0, this.mTraceCount);
                this.mTraceCurrent = zArr;
            }
            float[] fArr4 = this.mTraceX;
            int i3 = this.mTraceCount;
            fArr4[i3] = f;
            this.mTraceY[i3] = f2;
            this.mTraceCurrent[i3] = z;
            this.mTraceCount = i3 + 1;
        }
    }

    public PointerLocationView(Context context) {
        super(context);
        this.ESTIMATE_PAST_POINTS = 4;
        this.ESTIMATE_FUTURE_POINTS = 2;
        this.ESTIMATE_INTERVAL = 0.02f;
        this.mTextMetrics = new Paint.FontMetricsInt();
        this.mPointers = new ArrayList<>();
        this.mTempCoords = new MotionEvent.PointerCoords();
        this.mText = new FasterStringBuilder();
        this.mPrintCoords = true;
        this.mReusableOvalRect = new RectF();
        setFocusableInTouchMode(true);
        this.mIm = (InputManager) context.getSystemService(InputManager.class);
        this.mVC = ViewConfiguration.get(context);
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setAntiAlias(true);
        this.mTextPaint.setTextSize(getResources().getDisplayMetrics().density * 10.0f);
        this.mTextPaint.setARGB(255, 0, 0, 0);
        Paint paint2 = new Paint();
        this.mTextBackgroundPaint = paint2;
        paint2.setAntiAlias(false);
        this.mTextBackgroundPaint.setARGB(128, 255, 255, 255);
        Paint paint3 = new Paint();
        this.mTextLevelPaint = paint3;
        paint3.setAntiAlias(false);
        this.mTextLevelPaint.setARGB(192, 255, 0, 0);
        Paint paint4 = new Paint();
        this.mPaint = paint4;
        paint4.setAntiAlias(true);
        this.mPaint.setARGB(255, 255, 255, 255);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(2.0f);
        Paint paint5 = new Paint();
        this.mCurrentPointPaint = paint5;
        paint5.setAntiAlias(true);
        this.mCurrentPointPaint.setARGB(255, 255, 0, 0);
        this.mCurrentPointPaint.setStyle(Paint.Style.STROKE);
        this.mCurrentPointPaint.setStrokeWidth(2.0f);
        Paint paint6 = new Paint();
        this.mTargetPaint = paint6;
        paint6.setAntiAlias(false);
        this.mTargetPaint.setARGB(255, 0, 0, 192);
        Paint paint7 = new Paint();
        this.mPathPaint = paint7;
        paint7.setAntiAlias(false);
        this.mPathPaint.setARGB(255, 0, 96, 255);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPointers.add(new PointerState());
        this.mActivePointerId = 0;
        this.mVelocity = VelocityTracker.obtain();
        String str = SystemProperties.get(ALT_STRATEGY_PROPERY_KEY);
        if (str.length() != 0) {
            Log.d(TAG, "Comparing default velocity tracker strategy with " + str);
            this.mAltVelocity = VelocityTracker.obtain(str);
            return;
        }
        this.mAltVelocity = null;
    }

    public void setPrintCoords(boolean z) {
        this.mPrintCoords = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mTextPaint.getFontMetricsInt(this.mTextMetrics);
        this.mHeaderBottom = (-this.mTextMetrics.ascent) + this.mTextMetrics.descent + 2;
    }

    private void drawOval(Canvas canvas, float f, float f2, float f3, float f4, float f5, Paint paint) {
        canvas.save(1);
        canvas.rotate((float) (((double) (f5 * 180.0f)) / 3.141592653589793d), f, f2);
        float f6 = f4 / 2.0f;
        this.mReusableOvalRect.left = f - f6;
        this.mReusableOvalRect.right = f + f6;
        float f7 = f3 / 2.0f;
        this.mReusableOvalRect.top = f2 - f7;
        this.mReusableOvalRect.bottom = f2 + f7;
        canvas.drawOval(this.mReusableOvalRect, paint);
        canvas.restore();
    }

    /* JADX WARN: Failed to calculate best type for var: r0v161 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v161 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v162 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v162 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v163 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v163 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v172 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v172 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v173 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v173 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v174 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v174 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r10v10 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v10 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r10v9 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v9 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r10v9 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v9 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r23v0 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r23v0 ??, new type: android.graphics.Canvas
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r3v27 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v27 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r3v29 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v29 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r7v9 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v9 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to set immutable type for var: r23v0 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r23v0 ??, new type: android.graphics.Canvas
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnSame(TypeUpdate.java:73)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setImmutableType(TypeInferenceVisitor.java:111)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:102)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:102)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /*  JADX ERROR: Types fix failed
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v9 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
        */
    @Override // android.view.View
    protected void onDraw(android.graphics.Canvas r23) {
        /*
            Method dump skipped, instruction units count: 1361
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.widget.PointerLocationView.onDraw(android.graphics.Canvas):void");
    }

    private void logMotionEvent(String str, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int historySize = motionEvent.getHistorySize();
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < historySize; i++) {
            for (int i2 = 0; i2 < pointerCount; i2++) {
                int pointerId = motionEvent.getPointerId(i2);
                motionEvent.getHistoricalPointerCoords(i2, i, this.mTempCoords);
                logCoords(str, action, i2, this.mTempCoords, pointerId, motionEvent);
            }
        }
        for (int i3 = 0; i3 < pointerCount; i3++) {
            int pointerId2 = motionEvent.getPointerId(i3);
            motionEvent.getPointerCoords(i3, this.mTempCoords);
            logCoords(str, action, i3, this.mTempCoords, pointerId2, motionEvent);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:18:0x003b  */
    private void logCoords(String str, int i, int i2, MotionEvent.PointerCoords pointerCoords, int i3, MotionEvent motionEvent) {
        int toolType = motionEvent.getToolType(i2);
        int buttonState = motionEvent.getButtonState();
        String string = "UP";
        switch (i & 255) {
            case 0:
                string = "DOWN";
                break;
            case 1:
                break;
            case 2:
                string = "MOVE";
                break;
            case 3:
                string = "CANCEL";
                break;
            case 4:
                string = "OUTSIDE";
                break;
            case 5:
                if (i2 != ((i & 65280) >> 8)) {
                    string = "MOVE";
                } else {
                    string = "DOWN";
                }
                break;
            case 6:
                if (i2 != ((i & 65280) >> 8)) {
                    string = "MOVE";
                }
                break;
            case 7:
                string = "HOVER MOVE";
                break;
            case 8:
                string = "SCROLL";
                break;
            case 9:
                string = "HOVER ENTER";
                break;
            case 10:
                string = "HOVER EXIT";
                break;
            default:
                string = Integer.toString(i);
                break;
        }
        Log.i(TAG, this.mText.clear().append(str).append(" id ").append(i3 + 1).append(": ").append(string).append(" (").append(pointerCoords.x, 3).append(", ").append(pointerCoords.y, 3).append(") Pressure=").append(pointerCoords.pressure, 3).append(" Size=").append(pointerCoords.size, 3).append(" TouchMajor=").append(pointerCoords.touchMajor, 3).append(" TouchMinor=").append(pointerCoords.touchMinor, 3).append(" ToolMajor=").append(pointerCoords.toolMajor, 3).append(" ToolMinor=").append(pointerCoords.toolMinor, 3).append(" Orientation=").append((float) (((double) (pointerCoords.orientation * 180.0f)) / 3.141592653589793d), 1).append("deg").append(" Tilt=").append((float) (((double) (pointerCoords.getAxisValue(25) * 180.0f)) / 3.141592653589793d), 1).append("deg").append(" Distance=").append(pointerCoords.getAxisValue(24), 1).append(" VScroll=").append(pointerCoords.getAxisValue(9), 1).append(" HScroll=").append(pointerCoords.getAxisValue(10), 1).append(" BoundingBox=[(").append(motionEvent.getAxisValue(32), 3).append(", ").append(motionEvent.getAxisValue(33), 3).append(")").append(", (").append(motionEvent.getAxisValue(34), 3).append(", ").append(motionEvent.getAxisValue(35), 3).append(")]").append(" ToolType=").append(MotionEvent.toolTypeToString(toolType)).append(" ButtonState=").append(MotionEvent.buttonStateToString(buttonState)).toString());
    }

    /* JADX WARN: Code duplicated, block: B:77:0x01e3  */
    @Override // android.view.WindowManagerPolicyConstants.PointerEventListener
    public void onPointerEvent(MotionEvent motionEvent) {
        char c;
        MotionEvent.PointerCoords pointerCoords;
        int action = motionEvent.getAction();
        int size = this.mPointers.size();
        if (action == 0 || (action & 255) == 5) {
            int i = (action & 65280) >> 8;
            if (action == 0) {
                for (int i2 = 0; i2 < size; i2++) {
                    PointerState pointerState = this.mPointers.get(i2);
                    pointerState.clearTrace();
                    pointerState.mCurDown = false;
                }
                this.mCurDown = true;
                this.mCurNumPointers = 0;
                this.mMaxNumPointers = 0;
                this.mVelocity.clear();
                VelocityTracker velocityTracker = this.mAltVelocity;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            }
            int i3 = this.mCurNumPointers + 1;
            this.mCurNumPointers = i3;
            if (this.mMaxNumPointers < i3) {
                this.mMaxNumPointers = i3;
            }
            int pointerId = motionEvent.getPointerId(i);
            while (size <= pointerId) {
                this.mPointers.add(new PointerState());
                size++;
            }
            int i4 = this.mActivePointerId;
            if (i4 < 0 || !this.mPointers.get(i4).mCurDown) {
                this.mActivePointerId = pointerId;
            }
            PointerState pointerState2 = this.mPointers.get(pointerId);
            pointerState2.mCurDown = true;
            InputDevice device = InputDevice.getDevice(motionEvent.getDeviceId());
            pointerState2.mHasBoundingBox = (device == null || device.getMotionRange(32) == null) ? false : true;
        }
        int i5 = size;
        int pointerCount = motionEvent.getPointerCount();
        this.mVelocity.addMovement(motionEvent);
        this.mVelocity.computeCurrentVelocity(1);
        VelocityTracker velocityTracker2 = this.mAltVelocity;
        if (velocityTracker2 != null) {
            velocityTracker2.addMovement(motionEvent);
            this.mAltVelocity.computeCurrentVelocity(1);
        }
        int historySize = motionEvent.getHistorySize();
        int i6 = 0;
        while (i6 < historySize) {
            int i7 = 0;
            while (i7 < pointerCount) {
                int pointerId2 = motionEvent.getPointerId(i7);
                PointerState pointerState3 = this.mCurDown ? this.mPointers.get(pointerId2) : null;
                MotionEvent.PointerCoords pointerCoords2 = pointerState3 != null ? pointerState3.mCoords : this.mTempCoords;
                motionEvent.getHistoricalPointerCoords(i7, i6, pointerCoords2);
                if (this.mPrintCoords) {
                    pointerCoords = pointerCoords2;
                    logCoords(TAG, action, i7, pointerCoords, pointerId2, motionEvent);
                } else {
                    pointerCoords = pointerCoords2;
                }
                if (pointerState3 != 0) {
                    MotionEvent.PointerCoords pointerCoords3 = pointerCoords;
                    pointerState3.addTrace(pointerCoords3.x, pointerCoords3.y, false);
                }
                i7++;
                historySize = historySize;
                i6 = i6;
            }
            i6++;
        }
        for (int i8 = 0; i8 < pointerCount; i8++) {
            int pointerId3 = motionEvent.getPointerId(i8);
            PointerState pointerState4 = this.mCurDown ? this.mPointers.get(pointerId3) : null;
            MotionEvent.PointerCoords pointerCoords4 = pointerState4 != null ? pointerState4.mCoords : this.mTempCoords;
            motionEvent.getPointerCoords(i8, pointerCoords4);
            if (this.mPrintCoords) {
                logCoords(TAG, action, i8, pointerCoords4, pointerId3, motionEvent);
            }
            if (pointerState4 != 0) {
                pointerState4.addTrace(pointerCoords4.x, pointerCoords4.y, true);
                pointerState4.mXVelocity = this.mVelocity.getXVelocity(pointerId3);
                pointerState4.mYVelocity = this.mVelocity.getYVelocity(pointerId3);
                this.mVelocity.getEstimator(pointerId3, pointerState4.mEstimator);
                VelocityTracker velocityTracker3 = this.mAltVelocity;
                if (velocityTracker3 != null) {
                    pointerState4.mAltXVelocity = velocityTracker3.getXVelocity(pointerId3);
                    pointerState4.mAltYVelocity = this.mAltVelocity.getYVelocity(pointerId3);
                    this.mAltVelocity.getEstimator(pointerId3, pointerState4.mAltEstimator);
                }
                pointerState4.mToolType = motionEvent.getToolType(i8);
                if (pointerState4.mHasBoundingBox) {
                    c = ' ';
                    pointerState4.mBoundingLeft = motionEvent.getAxisValue(32, i8);
                    pointerState4.mBoundingTop = motionEvent.getAxisValue(33, i8);
                    pointerState4.mBoundingRight = motionEvent.getAxisValue(34, i8);
                    pointerState4.mBoundingBottom = motionEvent.getAxisValue(35, i8);
                } else {
                    c = ' ';
                }
            } else {
                c = ' ';
            }
        }
        if (action == 1 || action == 3 || (action & 255) == 6) {
            int i9 = (65280 & action) >> 8;
            int pointerId4 = motionEvent.getPointerId(i9);
            if (pointerId4 >= i5) {
                Slog.wtf(TAG, "Got pointer ID out of bounds: id=" + pointerId4 + " arraysize=" + i5 + " pointerindex=" + i9 + " action=0x" + Integer.toHexString(action));
                return;
            }
            PointerState pointerState5 = this.mPointers.get(pointerId4);
            pointerState5.mCurDown = false;
            if (action == 1 || action == 3) {
                this.mCurDown = false;
                this.mCurNumPointers = 0;
            } else {
                this.mCurNumPointers--;
                if (this.mActivePointerId == pointerId4) {
                    this.mActivePointerId = motionEvent.getPointerId(i9 != 0 ? 0 : 1);
                }
                pointerState5.addTrace(Float.NaN, Float.NaN, false);
            }
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        onPointerEvent(motionEvent);
        if (motionEvent.getAction() != 0 || isFocused()) {
            return true;
        }
        requestFocus();
        return true;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        int source = motionEvent.getSource();
        if ((source & 2) != 0) {
            onPointerEvent(motionEvent);
            return true;
        }
        if ((source & 16) != 0) {
            logMotionEvent("Joystick", motionEvent);
            return true;
        }
        if ((source & 8) != 0) {
            logMotionEvent("Position", motionEvent);
            return true;
        }
        logMotionEvent("Generic", motionEvent);
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (shouldLogKey(i)) {
            int repeatCount = keyEvent.getRepeatCount();
            if (repeatCount == 0) {
                Log.i(TAG, "Key Down: " + keyEvent);
                return true;
            }
            Log.i(TAG, "Key Repeat #" + repeatCount + ": " + keyEvent);
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (shouldLogKey(i)) {
            Log.i(TAG, "Key Up: " + keyEvent);
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    private static boolean shouldLogKey(int i) {
        switch (i) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return KeyEvent.isGamepadButton(i) || KeyEvent.isModifierKey(i);
        }
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        logMotionEvent("Trackball", motionEvent);
        return true;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIm.registerInputDeviceListener(this, getHandler());
        logInputDevices();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIm.unregisterInputDeviceListener(this);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        logInputDeviceState(i, "Device Added");
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        logInputDeviceState(i, "Device Changed");
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        logInputDeviceState(i, "Device Removed");
    }

    private void logInputDevices() {
        for (int i : InputDevice.getDeviceIds()) {
            logInputDeviceState(i, "Device Enumerated");
        }
    }

    private void logInputDeviceState(int i, String str) {
        InputDevice inputDevice = this.mIm.getInputDevice(i);
        if (inputDevice != null) {
            Log.i(TAG, str + ": " + inputDevice);
            return;
        }
        Log.i(TAG, str + ": " + i);
    }

    private static final class FasterStringBuilder {
        private char[] mChars = new char[64];
        private int mLength;

        public FasterStringBuilder clear() {
            this.mLength = 0;
            return this;
        }

        public FasterStringBuilder append(String str) {
            int length = str.length();
            str.getChars(0, length, this.mChars, reserve(length));
            this.mLength += length;
            return this;
        }

        public FasterStringBuilder append(int i) {
            return append(i, 0);
        }

        public FasterStringBuilder append(int i, int i2) {
            boolean z = i < 0;
            if (z && (i = -i) < 0) {
                append("-2147483648");
                return this;
            }
            int iReserve = reserve(11);
            char[] cArr = this.mChars;
            if (i == 0) {
                cArr[iReserve] = '0';
                this.mLength++;
                return this;
            }
            if (z) {
                cArr[iReserve] = '-';
                iReserve++;
            }
            int i3 = 1000000000;
            int i4 = 10;
            while (i < i3) {
                i3 /= 10;
                i4--;
                if (i4 < i2) {
                    cArr[iReserve] = '0';
                    iReserve++;
                }
            }
            while (true) {
                int i5 = i / i3;
                i -= i5 * i3;
                i3 /= 10;
                int i6 = iReserve + 1;
                cArr[iReserve] = (char) (i5 + 48);
                if (i3 == 0) {
                    this.mLength = i6;
                    return this;
                }
                iReserve = i6;
            }
        }

        public FasterStringBuilder append(float f, int i) {
            int i2 = 1;
            for (int i3 = 0; i3 < i; i3++) {
                i2 *= 10;
            }
            float f2 = i2;
            float fRint = (float) (Math.rint(f * f2) / ((double) i2));
            append((int) fRint);
            if (i != 0) {
                append(".");
                double dAbs = Math.abs(fRint);
                append((int) (((float) (dAbs - Math.floor(dAbs))) * f2), i);
            }
            return this;
        }

        public String toString() {
            return new String(this.mChars, 0, this.mLength);
        }

        private int reserve(int i) {
            int i2 = this.mLength;
            int i3 = i + i2;
            char[] cArr = this.mChars;
            int length = cArr.length;
            if (i3 > length) {
                char[] cArr2 = new char[length * 2];
                System.arraycopy(cArr, 0, cArr2, 0, i2);
                this.mChars = cArr2;
            }
            return i2;
        }
    }
}
