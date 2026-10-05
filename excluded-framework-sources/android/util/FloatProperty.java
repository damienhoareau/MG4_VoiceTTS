package android.util;

/* JADX INFO: loaded from: classes2.dex */
public abstract class FloatProperty<T> extends Property<T, Float> {
    public abstract void setValue(T t, float f);

    public FloatProperty(String str) {
        super(Float.class, str);
    }

    @Override // android.util.Property
    public final void set(T t, Float f) {
        setValue(t, f.floatValue());
    }
}
