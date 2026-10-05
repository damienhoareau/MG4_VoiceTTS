package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;

/* JADX INFO: loaded from: classes.dex */
public class RectShape extends Shape {
    private RectF mRect = new RectF();

    @Override // android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(this.mRect, paint);
    }

    @Override // android.graphics.drawable.shapes.Shape
    public void getOutline(Outline outline) {
        RectF rectFRect = rect();
        outline.setRect((int) Math.ceil(rectFRect.left), (int) Math.ceil(rectFRect.top), (int) Math.floor(rectFRect.right), (int) Math.floor(rectFRect.bottom));
    }

    @Override // android.graphics.drawable.shapes.Shape
    protected void onResize(float f, float f2) {
        this.mRect.set(0.0f, 0.0f, f, f2);
    }

    protected final RectF rect() {
        return this.mRect;
    }

    @Override // android.graphics.drawable.shapes.Shape
    /* JADX INFO: renamed from: clone */
    public RectShape mo20clone() throws CloneNotSupportedException {
        RectShape rectShape = (RectShape) super.mo20clone();
        rectShape.mRect = new RectF(this.mRect);
        return rectShape;
    }
}
