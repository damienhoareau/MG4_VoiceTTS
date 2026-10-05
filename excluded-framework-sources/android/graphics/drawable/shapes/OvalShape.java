package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;

/* JADX INFO: loaded from: classes.dex */
public class OvalShape extends RectShape {
    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawOval(rect(), paint);
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    public void getOutline(Outline outline) {
        RectF rectFRect = rect();
        outline.setOval((int) Math.ceil(rectFRect.left), (int) Math.ceil(rectFRect.top), (int) Math.floor(rectFRect.right), (int) Math.floor(rectFRect.bottom));
    }

    @Override // android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
    /* JADX INFO: renamed from: clone */
    public OvalShape mo20clone() throws CloneNotSupportedException {
        return (OvalShape) super.mo20clone();
    }
}
