package android.database.sqlite;

/* JADX INFO: loaded from: classes.dex */
public interface SQLiteTransactionListener {
    void onBegin();

    void onCommit();

    void onRollback();
}
