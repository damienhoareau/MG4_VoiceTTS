package androidx.appcompat.view.menu;

/* JADX INFO: loaded from: classes2.dex */
class BaseWrapper<T> {
    final T mWrappedObject;

    BaseWrapper(T t) {
        if (t == null) {
            throw new IllegalArgumentException("Wrapped Object can not be null.");
        }
        this.mWrappedObject = t;
    }

    public T getWrappedObject() {
        return this.mWrappedObject;
    }
}
