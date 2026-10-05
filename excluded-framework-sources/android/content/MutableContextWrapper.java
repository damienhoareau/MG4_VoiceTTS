package android.content;

/* JADX INFO: loaded from: classes.dex */
public class MutableContextWrapper extends ContextWrapper {
    public MutableContextWrapper(Context context) {
        super(context);
    }

    public void setBaseContext(Context context) {
        this.mBase = context;
    }
}
