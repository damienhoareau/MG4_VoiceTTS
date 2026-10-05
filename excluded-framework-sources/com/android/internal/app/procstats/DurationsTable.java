package com.android.internal.app.procstats;

/* JADX INFO: loaded from: classes3.dex */
public class DurationsTable extends SparseMappingTable.Table {
    public DurationsTable(SparseMappingTable sparseMappingTable) {
        super(sparseMappingTable);
    }

    public void addDurations(DurationsTable durationsTable) {
        int keyCount = durationsTable.getKeyCount();
        for (int i = 0; i < keyCount; i++) {
            int keyAt = durationsTable.getKeyAt(i);
            addDuration(SparseMappingTable.getIdFromKey(keyAt), durationsTable.getValue(keyAt));
        }
    }

    public void addDuration(int i, long j) {
        int orAddKey = getOrAddKey((byte) i, 1);
        setValue(orAddKey, getValue(orAddKey) + j);
    }
}
