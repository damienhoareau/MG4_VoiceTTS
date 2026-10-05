package com.android.internal.app;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/* JADX INFO: loaded from: classes3.dex */
public class PlatLogoActivity extends Activity {
    TimeAnimator anim;
    PBackground bg;
    FrameLayout layout;

    private class PBackground extends Drawable {
        private int darkest;
        private float dp;
        private float maxRadius;
        private float offset;
        private int[] palette;
        private float radius;
        private float x;
        private float y;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public PBackground() {
            randomizePalette();
        }

        public void setRadius(float f) {
            this.radius = Math.max(this.dp * 48.0f, f);
        }

        public void setPosition(float f, float f2) {
            this.x = f;
            this.y = f2;
        }

        public void setOffset(float f) {
            this.offset = f;
        }

        public float lum(int i) {
            return (((Color.red(i) * 299.0f) + (Color.green(i) * 587.0f)) + (Color.blue(i) * 114.0f)) / 1000.0f;
        }

        public void randomizePalette() {
            int iRandom = ((int) (Math.random() * 2.0d)) + 2;
            float[] fArr = {((float) Math.random()) * 360.0f, 1.0f, 1.0f};
            this.palette = new int[iRandom];
            this.darkest = 0;
            for (int i = 0; i < iRandom; i++) {
                this.palette[i] = Color.HSVToColor(fArr);
                fArr[0] = fArr[0] + (360.0f / iRandom);
                if (lum(this.palette[i]) < lum(this.palette[this.darkest])) {
                    this.darkest = i;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i2 : this.palette) {
                sb.append(String.format("#%08x ", Integer.valueOf(i2)));
            }
            Log.v("PlatLogoActivity", "color palette: " + ((Object) sb));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.dp == 0.0f) {
                this.dp = PlatLogoActivity.this.getResources().getDisplayMetrics().density;
            }
            float width = canvas.getWidth();
            float height = canvas.getHeight();
            float f = 2.0f;
            if (this.radius == 0.0f) {
                setPosition(width / 2.0f, height / 2.0f);
                setRadius(width / 6.0f);
            }
            float f2 = this.radius * 0.667f;
            Paint paint = new Paint();
            paint.setStrokeCap(Paint.Cap.BUTT);
            canvas.translate(this.x, this.y);
            Path path = new Path();
            path.moveTo(-this.radius, height);
            path.lineTo(-this.radius, 0.0f);
            float f3 = this.radius;
            path.arcTo(-f3, -f3, f3, f3, -180.0f, 270.0f, false);
            float f4 = this.radius;
            path.lineTo(-f4, f4);
            float fMax = Math.max(canvas.getWidth(), canvas.getHeight()) * 1.414f;
            paint.setStyle(Paint.Style.FILL);
            float fSin = fMax;
            int i = 0;
            while (true) {
                float f5 = f2 * f;
                if (fSin > (this.radius * f) + f5) {
                    int[] iArr = this.palette;
                    paint.setColor(iArr[i % iArr.length] | (-16777216));
                    float f6 = (-fSin) / f;
                    float f7 = fSin / f;
                    canvas.drawOval(f6, f6, f7, f7, paint);
                    fSin = (float) (((double) fSin) - (((double) f2) * (Math.sin(((i / 20.0f) + this.offset) * 3.14159f) + 1.100000023841858d)));
                    i++;
                    path = path;
                    height = height;
                    f = 2.0f;
                } else {
                    Path path2 = path;
                    int[] iArr2 = this.palette;
                    paint.setColor(iArr2[(this.darkest + 1) % iArr2.length] | (-16777216));
                    float f8 = this.radius;
                    canvas.drawOval(-f8, -f8, f8, f8, paint);
                    path2.reset();
                    path2.moveTo(-this.radius, height);
                    path2.lineTo(-this.radius, 0.0f);
                    float f9 = this.radius;
                    path2.arcTo(-f9, -f9, f9, f9, -180.0f, 270.0f, false);
                    float f10 = this.radius;
                    path2.lineTo((-f10) + f2, f10);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(f5);
                    paint.setColor(this.palette[this.darkest]);
                    canvas.drawPath(path2, paint);
                    paint.setStrokeWidth(f2);
                    paint.setColor(-1);
                    canvas.drawPath(path2, paint);
                    return;
                }
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FrameLayout frameLayout = new FrameLayout(this);
        this.layout = frameLayout;
        setContentView(frameLayout);
        PBackground pBackground = new PBackground();
        this.bg = pBackground;
        this.layout.setBackground(pBackground);
        this.layout.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.internal.app.PlatLogoActivity.1
            final MotionEvent.PointerCoords pc0 = new MotionEvent.PointerCoords();
            final MotionEvent.PointerCoords pc1 = new MotionEvent.PointerCoords();

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if ((actionMasked == 0 || actionMasked == 2) && motionEvent.getPointerCount() > 1) {
                    motionEvent.getPointerCoords(0, this.pc0);
                    motionEvent.getPointerCoords(1, this.pc1);
                    PlatLogoActivity.this.bg.setRadius(((float) Math.hypot(this.pc0.x - this.pc1.x, this.pc0.y - this.pc1.y)) / 2.0f);
                }
                return true;
            }
        });
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        this.bg.randomizePalette();
        TimeAnimator timeAnimator = new TimeAnimator();
        this.anim = timeAnimator;
        timeAnimator.setTimeListener(new TimeAnimator.TimeListener() { // from class: com.android.internal.app.PlatLogoActivity.2
            @Override // android.animation.TimeAnimator.TimeListener
            public void onTimeUpdate(TimeAnimator timeAnimator2, long j, long j2) {
                PlatLogoActivity.this.bg.setOffset(j / 60000.0f);
                PlatLogoActivity.this.bg.invalidateSelf();
            }
        });
        this.anim.start();
    }

    @Override // android.app.Activity
    public void onStop() {
        TimeAnimator timeAnimator = this.anim;
        if (timeAnimator != null) {
            timeAnimator.cancel();
            this.anim = null;
        }
        super.onStop();
    }
}
