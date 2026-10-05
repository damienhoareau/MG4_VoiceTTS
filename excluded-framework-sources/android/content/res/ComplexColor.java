package android.content.res;

/* JADX INFO: loaded from: classes.dex */
public abstract class ComplexColor {
    private int mChangingConfigurations;

    public abstract boolean canApplyTheme();

    public abstract ConstantState<ComplexColor> getConstantState();

    public abstract int getDefaultColor();

    public boolean isStateful() {
        return false;
    }

    public abstract ComplexColor obtainForTheme(Resources.Theme theme);

    final void setBaseChangingConfigurations(int i) {
        this.mChangingConfigurations = i;
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }
}
