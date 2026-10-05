package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;

/* JADX INFO: loaded from: classes.dex */
public class ArcShape extends RectShape {
    private final float mStartAngle;
    private final float mSweepAngle;

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void getOutline(Outline outline) {
    }

    public ArcShape(float f, float f2) {
        this.mStartAngle = f;
        this.mSweepAngle = f2;
    }

    public final float getStartAngle() {
        return this.mStartAngle;
    }

    public final float getSweepAngle() {
        return this.mSweepAngle;
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawArc(rect(), this.mStartAngle, this.mSweepAngle, true, paint);
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    /* JADX INFO: renamed from: clone */
    public ArcShape mo20clone() throws CloneNotSupportedException {
        return (ArcShape) super.mo20clone();
    }
}
