package android.graphics.drawable.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/* JADX INFO: loaded from: classes.dex */
public class PathShape extends Shape {
    private Path mPath;
    private float mScaleX;
    private float mScaleY;
    private final float mStdHeight;
    private final float mStdWidth;

    public PathShape(Path path, float f, float f2) {
        this.mPath = path;
        this.mStdWidth = f;
        this.mStdHeight = f2;
    }

    @Override // android.graphics.drawable.shapes.Shape
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY);
        canvas.drawPath(this.mPath, paint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.shapes.Shape
    protected void onResize(float f, float f2) {
        this.mScaleX = f / this.mStdWidth;
        this.mScaleY = f2 / this.mStdHeight;
    }

    @Override // android.graphics.drawable.shapes.Shape
    /* JADX INFO: renamed from: clone */
    public PathShape mo20clone() throws CloneNotSupportedException {
        PathShape pathShape = (PathShape) super.mo20clone();
        pathShape.mPath = new Path(this.mPath);
        return pathShape;
    }
}
