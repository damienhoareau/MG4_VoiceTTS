package android.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes2.dex */
public final class ViewTreeObserver {
    private static boolean sIllegalOnDrawModificationIsFatal;
    private boolean mAlive = true;
    private boolean mInDispatchOnDraw;
    private CopyOnWriteArray<OnComputeInternalInsetsListener> mOnComputeInternalInsetsListeners;
    private ArrayList<OnDrawListener> mOnDrawListeners;
    private CopyOnWriteArrayList<OnEnterAnimationCompleteListener> mOnEnterAnimationCompleteListeners;
    private CopyOnWriteArrayList<OnGlobalFocusChangeListener> mOnGlobalFocusListeners;
    private CopyOnWriteArray<OnGlobalLayoutListener> mOnGlobalLayoutListeners;
    private CopyOnWriteArray<OnPreDrawListener> mOnPreDrawListeners;
    private CopyOnWriteArray<OnScrollChangedListener> mOnScrollChangedListeners;
    private CopyOnWriteArrayList<OnTouchModeChangeListener> mOnTouchModeChangeListeners;
    private CopyOnWriteArrayList<OnWindowAttachListener> mOnWindowAttachListeners;
    private CopyOnWriteArrayList<OnWindowFocusChangeListener> mOnWindowFocusListeners;
    private CopyOnWriteArray<OnWindowShownListener> mOnWindowShownListeners;
    private boolean mWindowShown;

    public interface OnComputeInternalInsetsListener {
        void onComputeInternalInsets(InternalInsetsInfo internalInsetsInfo);
    }

    public interface OnDrawListener {
        void onDraw();
    }

    public interface OnEnterAnimationCompleteListener {
        void onEnterAnimationComplete();
    }

    public interface OnGlobalFocusChangeListener {
        void onGlobalFocusChanged(View view, View view2);
    }

    public interface OnGlobalLayoutListener {
        void onGlobalLayout();
    }

    public interface OnPreDrawListener {
        boolean onPreDraw();
    }

    public interface OnScrollChangedListener {
        void onScrollChanged();
    }

    public interface OnTouchModeChangeListener {
        void onTouchModeChanged(boolean z);
    }

    public interface OnWindowAttachListener {
        void onWindowAttached();

        void onWindowDetached();
    }

    public interface OnWindowFocusChangeListener {
        void onWindowFocusChanged(boolean z);
    }

    public interface OnWindowShownListener {
        void onWindowShown();
    }

    public static final class InternalInsetsInfo {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;
        int mTouchableInsets;
        public final Rect contentInsets = new Rect();
        public final Rect visibleInsets = new Rect();
        public final Region touchableRegion = new Region();

        public void setTouchableInsets(int i) {
            this.mTouchableInsets = i;
        }

        void reset() {
            this.contentInsets.setEmpty();
            this.visibleInsets.setEmpty();
            this.touchableRegion.setEmpty();
            this.mTouchableInsets = 0;
        }

        boolean isEmpty() {
            return this.contentInsets.isEmpty() && this.visibleInsets.isEmpty() && this.touchableRegion.isEmpty() && this.mTouchableInsets == 0;
        }

        public int hashCode() {
            return (((((this.contentInsets.hashCode() * 31) + this.visibleInsets.hashCode()) * 31) + this.touchableRegion.hashCode()) * 31) + this.mTouchableInsets;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            InternalInsetsInfo internalInsetsInfo = (InternalInsetsInfo) obj;
            return this.mTouchableInsets == internalInsetsInfo.mTouchableInsets && this.contentInsets.equals(internalInsetsInfo.contentInsets) && this.visibleInsets.equals(internalInsetsInfo.visibleInsets) && this.touchableRegion.equals(internalInsetsInfo.touchableRegion);
        }

        void set(InternalInsetsInfo internalInsetsInfo) {
            this.contentInsets.set(internalInsetsInfo.contentInsets);
            this.visibleInsets.set(internalInsetsInfo.visibleInsets);
            this.touchableRegion.set(internalInsetsInfo.touchableRegion);
            this.mTouchableInsets = internalInsetsInfo.mTouchableInsets;
        }
    }

    ViewTreeObserver(Context context) {
        sIllegalOnDrawModificationIsFatal = context.getApplicationInfo().targetSdkVersion >= 26;
    }

    void merge(ViewTreeObserver viewTreeObserver) {
        CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList = viewTreeObserver.mOnWindowAttachListeners;
        if (copyOnWriteArrayList != null) {
            CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList2 = this.mOnWindowAttachListeners;
            if (copyOnWriteArrayList2 != null) {
                copyOnWriteArrayList2.addAll(copyOnWriteArrayList);
            } else {
                this.mOnWindowAttachListeners = copyOnWriteArrayList;
            }
        }
        CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList3 = viewTreeObserver.mOnWindowFocusListeners;
        if (copyOnWriteArrayList3 != null) {
            CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList4 = this.mOnWindowFocusListeners;
            if (copyOnWriteArrayList4 != null) {
                copyOnWriteArrayList4.addAll(copyOnWriteArrayList3);
            } else {
                this.mOnWindowFocusListeners = copyOnWriteArrayList3;
            }
        }
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList5 = viewTreeObserver.mOnGlobalFocusListeners;
        if (copyOnWriteArrayList5 != null) {
            CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList6 = this.mOnGlobalFocusListeners;
            if (copyOnWriteArrayList6 != null) {
                copyOnWriteArrayList6.addAll(copyOnWriteArrayList5);
            } else {
                this.mOnGlobalFocusListeners = copyOnWriteArrayList5;
            }
        }
        CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray = viewTreeObserver.mOnGlobalLayoutListeners;
        if (copyOnWriteArray != null) {
            CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray2 = this.mOnGlobalLayoutListeners;
            if (copyOnWriteArray2 != null) {
                copyOnWriteArray2.addAll(copyOnWriteArray);
            } else {
                this.mOnGlobalLayoutListeners = copyOnWriteArray;
            }
        }
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray3 = viewTreeObserver.mOnPreDrawListeners;
        if (copyOnWriteArray3 != null) {
            CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray4 = this.mOnPreDrawListeners;
            if (copyOnWriteArray4 != null) {
                copyOnWriteArray4.addAll(copyOnWriteArray3);
            } else {
                this.mOnPreDrawListeners = copyOnWriteArray3;
            }
        }
        ArrayList<OnDrawListener> arrayList = viewTreeObserver.mOnDrawListeners;
        if (arrayList != null) {
            ArrayList<OnDrawListener> arrayList2 = this.mOnDrawListeners;
            if (arrayList2 != null) {
                arrayList2.addAll(arrayList);
            } else {
                this.mOnDrawListeners = arrayList;
            }
        }
        CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList7 = viewTreeObserver.mOnTouchModeChangeListeners;
        if (copyOnWriteArrayList7 != null) {
            CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList8 = this.mOnTouchModeChangeListeners;
            if (copyOnWriteArrayList8 != null) {
                copyOnWriteArrayList8.addAll(copyOnWriteArrayList7);
            } else {
                this.mOnTouchModeChangeListeners = copyOnWriteArrayList7;
            }
        }
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray5 = viewTreeObserver.mOnComputeInternalInsetsListeners;
        if (copyOnWriteArray5 != null) {
            CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray6 = this.mOnComputeInternalInsetsListeners;
            if (copyOnWriteArray6 != null) {
                copyOnWriteArray6.addAll(copyOnWriteArray5);
            } else {
                this.mOnComputeInternalInsetsListeners = copyOnWriteArray5;
            }
        }
        CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray7 = viewTreeObserver.mOnScrollChangedListeners;
        if (copyOnWriteArray7 != null) {
            CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray8 = this.mOnScrollChangedListeners;
            if (copyOnWriteArray8 != null) {
                copyOnWriteArray8.addAll(copyOnWriteArray7);
            } else {
                this.mOnScrollChangedListeners = copyOnWriteArray7;
            }
        }
        CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray9 = viewTreeObserver.mOnWindowShownListeners;
        if (copyOnWriteArray9 != null) {
            CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray10 = this.mOnWindowShownListeners;
            if (copyOnWriteArray10 != null) {
                copyOnWriteArray10.addAll(copyOnWriteArray9);
            } else {
                this.mOnWindowShownListeners = copyOnWriteArray9;
            }
        }
        viewTreeObserver.kill();
    }

    public void addOnWindowAttachListener(OnWindowAttachListener onWindowAttachListener) {
        checkIsAlive();
        if (this.mOnWindowAttachListeners == null) {
            this.mOnWindowAttachListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowAttachListeners.add(onWindowAttachListener);
    }

    public void removeOnWindowAttachListener(OnWindowAttachListener onWindowAttachListener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList = this.mOnWindowAttachListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(onWindowAttachListener);
    }

    public void addOnWindowFocusChangeListener(OnWindowFocusChangeListener onWindowFocusChangeListener) {
        checkIsAlive();
        if (this.mOnWindowFocusListeners == null) {
            this.mOnWindowFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnWindowFocusListeners.add(onWindowFocusChangeListener);
    }

    public void removeOnWindowFocusChangeListener(OnWindowFocusChangeListener onWindowFocusChangeListener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList = this.mOnWindowFocusListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(onWindowFocusChangeListener);
    }

    public void addOnGlobalFocusChangeListener(OnGlobalFocusChangeListener onGlobalFocusChangeListener) {
        checkIsAlive();
        if (this.mOnGlobalFocusListeners == null) {
            this.mOnGlobalFocusListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnGlobalFocusListeners.add(onGlobalFocusChangeListener);
    }

    public void removeOnGlobalFocusChangeListener(OnGlobalFocusChangeListener onGlobalFocusChangeListener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList = this.mOnGlobalFocusListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(onGlobalFocusChangeListener);
    }

    public void addOnGlobalLayoutListener(OnGlobalLayoutListener onGlobalLayoutListener) {
        checkIsAlive();
        if (this.mOnGlobalLayoutListeners == null) {
            this.mOnGlobalLayoutListeners = new CopyOnWriteArray<>();
        }
        this.mOnGlobalLayoutListeners.add(onGlobalLayoutListener);
    }

    @Deprecated
    public void removeGlobalOnLayoutListener(OnGlobalLayoutListener onGlobalLayoutListener) {
        removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener onGlobalLayoutListener) {
        checkIsAlive();
        CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray = this.mOnGlobalLayoutListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(onGlobalLayoutListener);
    }

    public void addOnPreDrawListener(OnPreDrawListener onPreDrawListener) {
        checkIsAlive();
        if (this.mOnPreDrawListeners == null) {
            this.mOnPreDrawListeners = new CopyOnWriteArray<>();
        }
        this.mOnPreDrawListeners.add(onPreDrawListener);
    }

    public void removeOnPreDrawListener(OnPreDrawListener onPreDrawListener) {
        checkIsAlive();
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray = this.mOnPreDrawListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(onPreDrawListener);
    }

    public void addOnWindowShownListener(OnWindowShownListener onWindowShownListener) {
        checkIsAlive();
        if (this.mOnWindowShownListeners == null) {
            this.mOnWindowShownListeners = new CopyOnWriteArray<>();
        }
        this.mOnWindowShownListeners.add(onWindowShownListener);
        if (this.mWindowShown) {
            onWindowShownListener.onWindowShown();
        }
    }

    public void removeOnWindowShownListener(OnWindowShownListener onWindowShownListener) {
        checkIsAlive();
        CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray = this.mOnWindowShownListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(onWindowShownListener);
    }

    public void addOnDrawListener(OnDrawListener onDrawListener) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            this.mOnDrawListeners = new ArrayList<>();
        }
        if (this.mInDispatchOnDraw) {
            IllegalStateException illegalStateException = new IllegalStateException("Cannot call addOnDrawListener inside of onDraw");
            if (sIllegalOnDrawModificationIsFatal) {
                throw illegalStateException;
            }
            Log.e("ViewTreeObserver", illegalStateException.getMessage(), illegalStateException);
        }
        this.mOnDrawListeners.add(onDrawListener);
    }

    public void removeOnDrawListener(OnDrawListener onDrawListener) {
        checkIsAlive();
        if (this.mOnDrawListeners == null) {
            return;
        }
        if (this.mInDispatchOnDraw) {
            IllegalStateException illegalStateException = new IllegalStateException("Cannot call removeOnDrawListener inside of onDraw");
            if (sIllegalOnDrawModificationIsFatal) {
                throw illegalStateException;
            }
            Log.e("ViewTreeObserver", illegalStateException.getMessage(), illegalStateException);
        }
        this.mOnDrawListeners.remove(onDrawListener);
    }

    public void addOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        checkIsAlive();
        if (this.mOnScrollChangedListeners == null) {
            this.mOnScrollChangedListeners = new CopyOnWriteArray<>();
        }
        this.mOnScrollChangedListeners.add(onScrollChangedListener);
    }

    public void removeOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        checkIsAlive();
        CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray = this.mOnScrollChangedListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(onScrollChangedListener);
    }

    public void addOnTouchModeChangeListener(OnTouchModeChangeListener onTouchModeChangeListener) {
        checkIsAlive();
        if (this.mOnTouchModeChangeListeners == null) {
            this.mOnTouchModeChangeListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnTouchModeChangeListeners.add(onTouchModeChangeListener);
    }

    public void removeOnTouchModeChangeListener(OnTouchModeChangeListener onTouchModeChangeListener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList = this.mOnTouchModeChangeListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(onTouchModeChangeListener);
    }

    public void addOnComputeInternalInsetsListener(OnComputeInternalInsetsListener onComputeInternalInsetsListener) {
        checkIsAlive();
        if (this.mOnComputeInternalInsetsListeners == null) {
            this.mOnComputeInternalInsetsListeners = new CopyOnWriteArray<>();
        }
        this.mOnComputeInternalInsetsListeners.add(onComputeInternalInsetsListener);
    }

    public void removeOnComputeInternalInsetsListener(OnComputeInternalInsetsListener onComputeInternalInsetsListener) {
        checkIsAlive();
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray = this.mOnComputeInternalInsetsListeners;
        if (copyOnWriteArray == null) {
            return;
        }
        copyOnWriteArray.remove(onComputeInternalInsetsListener);
    }

    public void addOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener onEnterAnimationCompleteListener) {
        checkIsAlive();
        if (this.mOnEnterAnimationCompleteListeners == null) {
            this.mOnEnterAnimationCompleteListeners = new CopyOnWriteArrayList<>();
        }
        this.mOnEnterAnimationCompleteListeners.add(onEnterAnimationCompleteListener);
    }

    public void removeOnEnterAnimationCompleteListener(OnEnterAnimationCompleteListener onEnterAnimationCompleteListener) {
        checkIsAlive();
        CopyOnWriteArrayList<OnEnterAnimationCompleteListener> copyOnWriteArrayList = this.mOnEnterAnimationCompleteListeners;
        if (copyOnWriteArrayList == null) {
            return;
        }
        copyOnWriteArrayList.remove(onEnterAnimationCompleteListener);
    }

    private void checkIsAlive() {
        if (!this.mAlive) {
            throw new IllegalStateException("This ViewTreeObserver is not alive, call getViewTreeObserver() again");
        }
    }

    public boolean isAlive() {
        return this.mAlive;
    }

    private void kill() {
        this.mAlive = false;
    }

    final void dispatchOnWindowAttachedChange(boolean z) {
        CopyOnWriteArrayList<OnWindowAttachListener> copyOnWriteArrayList = this.mOnWindowAttachListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.size() <= 0) {
            return;
        }
        for (OnWindowAttachListener onWindowAttachListener : copyOnWriteArrayList) {
            if (z) {
                onWindowAttachListener.onWindowAttached();
            } else {
                onWindowAttachListener.onWindowDetached();
            }
        }
    }

    final void dispatchOnWindowFocusChange(boolean z) {
        CopyOnWriteArrayList<OnWindowFocusChangeListener> copyOnWriteArrayList = this.mOnWindowFocusListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.size() <= 0) {
            return;
        }
        Iterator<OnWindowFocusChangeListener> it = copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            it.next().onWindowFocusChanged(z);
        }
    }

    final void dispatchOnGlobalFocusChange(View view, View view2) {
        CopyOnWriteArrayList<OnGlobalFocusChangeListener> copyOnWriteArrayList = this.mOnGlobalFocusListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.size() <= 0) {
            return;
        }
        Iterator<OnGlobalFocusChangeListener> it = copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            it.next().onGlobalFocusChanged(view, view2);
        }
    }

    public final void dispatchOnGlobalLayout() {
        CopyOnWriteArray<OnGlobalLayoutListener> copyOnWriteArray = this.mOnGlobalLayoutListeners;
        if (copyOnWriteArray == null || copyOnWriteArray.size() <= 0) {
            return;
        }
        CopyOnWriteArray.Access<OnGlobalLayoutListener> accessStart = copyOnWriteArray.start();
        try {
            int size = accessStart.size();
            for (int i = 0; i < size; i++) {
                accessStart.get(i).onGlobalLayout();
            }
            copyOnWriteArray.end();
        } catch (Throwable th) {
            copyOnWriteArray.end();
            throw th;
        }
    }

    final boolean hasOnPreDrawListeners() {
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray = this.mOnPreDrawListeners;
        return copyOnWriteArray != null && copyOnWriteArray.size() > 0;
    }

    public final boolean dispatchOnPreDraw() {
        CopyOnWriteArray<OnPreDrawListener> copyOnWriteArray = this.mOnPreDrawListeners;
        if (copyOnWriteArray == null || copyOnWriteArray.size() <= 0) {
            return false;
        }
        CopyOnWriteArray.Access<OnPreDrawListener> accessStart = copyOnWriteArray.start();
        try {
            int size = accessStart.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                z |= !accessStart.get(i).onPreDraw();
            }
            copyOnWriteArray.end();
            return z;
        } catch (Throwable th) {
            copyOnWriteArray.end();
            throw th;
        }
    }

    public final void dispatchOnWindowShown() {
        this.mWindowShown = true;
        CopyOnWriteArray<OnWindowShownListener> copyOnWriteArray = this.mOnWindowShownListeners;
        if (copyOnWriteArray == null || copyOnWriteArray.size() <= 0) {
            return;
        }
        CopyOnWriteArray.Access<OnWindowShownListener> accessStart = copyOnWriteArray.start();
        try {
            int size = accessStart.size();
            for (int i = 0; i < size; i++) {
                accessStart.get(i).onWindowShown();
            }
            copyOnWriteArray.end();
        } catch (Throwable th) {
            copyOnWriteArray.end();
            throw th;
        }
    }

    public final void dispatchOnDraw() {
        ArrayList<OnDrawListener> arrayList = this.mOnDrawListeners;
        if (arrayList != null) {
            this.mInDispatchOnDraw = true;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i).onDraw();
            }
            this.mInDispatchOnDraw = false;
        }
    }

    final void dispatchOnTouchModeChanged(boolean z) {
        CopyOnWriteArrayList<OnTouchModeChangeListener> copyOnWriteArrayList = this.mOnTouchModeChangeListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.size() <= 0) {
            return;
        }
        Iterator<OnTouchModeChangeListener> it = copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            it.next().onTouchModeChanged(z);
        }
    }

    final void dispatchOnScrollChanged() {
        CopyOnWriteArray<OnScrollChangedListener> copyOnWriteArray = this.mOnScrollChangedListeners;
        if (copyOnWriteArray == null || copyOnWriteArray.size() <= 0) {
            return;
        }
        CopyOnWriteArray.Access<OnScrollChangedListener> accessStart = copyOnWriteArray.start();
        try {
            int size = accessStart.size();
            for (int i = 0; i < size; i++) {
                accessStart.get(i).onScrollChanged();
            }
            copyOnWriteArray.end();
        } catch (Throwable th) {
            copyOnWriteArray.end();
            throw th;
        }
    }

    final boolean hasComputeInternalInsetsListeners() {
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray = this.mOnComputeInternalInsetsListeners;
        return copyOnWriteArray != null && copyOnWriteArray.size() > 0;
    }

    final void dispatchOnComputeInternalInsets(InternalInsetsInfo internalInsetsInfo) {
        CopyOnWriteArray<OnComputeInternalInsetsListener> copyOnWriteArray = this.mOnComputeInternalInsetsListeners;
        if (copyOnWriteArray == null || copyOnWriteArray.size() <= 0) {
            return;
        }
        CopyOnWriteArray.Access<OnComputeInternalInsetsListener> accessStart = copyOnWriteArray.start();
        try {
            int size = accessStart.size();
            for (int i = 0; i < size; i++) {
                accessStart.get(i).onComputeInternalInsets(internalInsetsInfo);
            }
            copyOnWriteArray.end();
        } catch (Throwable th) {
            copyOnWriteArray.end();
            throw th;
        }
    }

    public final void dispatchOnEnterAnimationComplete() {
        CopyOnWriteArrayList<OnEnterAnimationCompleteListener> copyOnWriteArrayList = this.mOnEnterAnimationCompleteListeners;
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.isEmpty()) {
            return;
        }
        Iterator<OnEnterAnimationCompleteListener> it = copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            it.next().onEnterAnimationComplete();
        }
    }

    static class CopyOnWriteArray<T> {
        private ArrayList<T> mDataCopy;
        private boolean mStart;
        private ArrayList<T> mData = new ArrayList<>();
        private final Access<T> mAccess = new Access<>();

        static class Access<T> {
            private ArrayList<T> mData;
            private int mSize;

            Access() {
            }

            T get(int i) {
                return this.mData.get(i);
            }

            int size() {
                return this.mSize;
            }
        }

        CopyOnWriteArray() {
        }

        private ArrayList<T> getArray() {
            if (this.mStart) {
                if (this.mDataCopy == null) {
                    this.mDataCopy = new ArrayList<>(this.mData);
                }
                return this.mDataCopy;
            }
            return this.mData;
        }

        Access<T> start() {
            if (this.mStart) {
                throw new IllegalStateException("Iteration already started");
            }
            this.mStart = true;
            this.mDataCopy = null;
            ((Access) this.mAccess).mData = this.mData;
            ((Access) this.mAccess).mSize = this.mData.size();
            return this.mAccess;
        }

        void end() {
            if (!this.mStart) {
                throw new IllegalStateException("Iteration not started");
            }
            this.mStart = false;
            ArrayList<T> arrayList = this.mDataCopy;
            if (arrayList != null) {
                this.mData = arrayList;
                ((Access) this.mAccess).mData.clear();
                ((Access) this.mAccess).mSize = 0;
            }
            this.mDataCopy = null;
        }

        int size() {
            return getArray().size();
        }

        void add(T t) {
            getArray().add(t);
        }

        void addAll(CopyOnWriteArray<T> copyOnWriteArray) {
            getArray().addAll(copyOnWriteArray.mData);
        }

        void remove(T t) {
            getArray().remove(t);
        }

        void clear() {
            getArray().clear();
        }
    }
}
