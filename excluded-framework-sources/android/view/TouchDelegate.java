package android.view;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes2.dex */
public class TouchDelegate {
    public static final int ABOVE = 1;
    public static final int BELOW = 2;
    public static final int TO_LEFT = 4;
    public static final int TO_RIGHT = 8;
    private Rect mBounds;
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private int mSlop;
    private Rect mSlopBounds;

    public TouchDelegate(Rect rect, View view) {
        this.mBounds = rect;
        this.mSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        Rect rect2 = new Rect(rect);
        this.mSlopBounds = rect2;
        int i = this.mSlop;
        rect2.inset(-i, -i);
        this.mDelegateView = view;
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0027  */
    /* JADX WARN: Code duplicated, block: B:16:0x002b  */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean zContains;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int actionMasked = motionEvent.getActionMasked();
        boolean zContains2 = true;
        if (actionMasked == 0) {
            zContains = this.mBounds.contains(x, y);
            this.mDelegateTargeted = zContains;
        } else if (actionMasked == 1 || actionMasked == 2) {
            boolean z = this.mDelegateTargeted;
            zContains2 = z ? this.mSlopBounds.contains(x, y) : true;
            zContains = z;
        } else if (actionMasked == 3) {
            zContains = this.mDelegateTargeted;
            this.mDelegateTargeted = false;
        } else if (actionMasked == 5 || actionMasked == 6) {
            boolean z2 = this.mDelegateTargeted;
            if (z2) {
            }
            zContains = z2;
        } else {
            zContains = false;
        }
        if (!zContains) {
            return false;
        }
        View view = this.mDelegateView;
        if (zContains2) {
            motionEvent.setLocation(view.getWidth() / 2, view.getHeight() / 2);
        } else {
            float f = -(this.mSlop * 2);
            motionEvent.setLocation(f, f);
        }
        return view.dispatchTouchEvent(motionEvent);
    }
}
