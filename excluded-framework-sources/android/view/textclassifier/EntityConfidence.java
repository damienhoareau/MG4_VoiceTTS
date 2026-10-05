package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class EntityConfidence implements Parcelable {
    public static final Parcelable.Creator<EntityConfidence> CREATOR = new Parcelable.Creator<EntityConfidence>() { // from class: android.view.textclassifier.EntityConfidence.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EntityConfidence createFromParcel(Parcel parcel) {
            return new EntityConfidence(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EntityConfidence[] newArray(int i) {
            return new EntityConfidence[i];
        }
    };
    private final ArrayMap<String, Float> mEntityConfidence;
    private final ArrayList<String> mSortedEntities;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    EntityConfidence() {
        this.mEntityConfidence = new ArrayMap<>();
        this.mSortedEntities = new ArrayList<>();
    }

    EntityConfidence(EntityConfidence entityConfidence) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mSortedEntities = new ArrayList<>();
        Preconditions.checkNotNull(entityConfidence);
        this.mEntityConfidence.putAll((ArrayMap<? extends String, ? extends Float>) entityConfidence.mEntityConfidence);
        this.mSortedEntities.addAll(entityConfidence.mSortedEntities);
    }

    EntityConfidence(Map<String, Float> map) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mSortedEntities = new ArrayList<>();
        Preconditions.checkNotNull(map);
        this.mEntityConfidence.ensureCapacity(map.size());
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            if (entry.getValue().floatValue() > 0.0f) {
                this.mEntityConfidence.put(entry.getKey(), Float.valueOf(Math.min(1.0f, entry.getValue().floatValue())));
            }
        }
        resetSortedEntitiesFromMap();
    }

    public List<String> getEntities() {
        return Collections.unmodifiableList(this.mSortedEntities);
    }

    public float getConfidenceScore(String str) {
        if (this.mEntityConfidence.containsKey(str)) {
            return this.mEntityConfidence.get(str).floatValue();
        }
        return 0.0f;
    }

    public String toString() {
        return this.mEntityConfidence.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mEntityConfidence.size());
        for (Map.Entry<String, Float> entry : this.mEntityConfidence.entrySet()) {
            parcel.writeString(entry.getKey());
            parcel.writeFloat(entry.getValue().floatValue());
        }
    }

    private EntityConfidence(Parcel parcel) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mSortedEntities = new ArrayList<>();
        int i = parcel.readInt();
        this.mEntityConfidence.ensureCapacity(i);
        for (int i2 = 0; i2 < i; i2++) {
            this.mEntityConfidence.put(parcel.readString(), Float.valueOf(parcel.readFloat()));
        }
        resetSortedEntitiesFromMap();
    }

    private void resetSortedEntitiesFromMap() {
        this.mSortedEntities.clear();
        this.mSortedEntities.ensureCapacity(this.mEntityConfidence.size());
        this.mSortedEntities.addAll(this.mEntityConfidence.keySet());
        this.mSortedEntities.sort(new Comparator() { // from class: android.view.textclassifier.-$$Lambda$EntityConfidence$YPh8hwgSYYK8OyQ1kFlQngc71Q0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return this.f$0.lambda$resetSortedEntitiesFromMap$0$EntityConfidence((String) obj, (String) obj2);
            }
        });
    }

    public /* synthetic */ int lambda$resetSortedEntitiesFromMap$0$EntityConfidence(String str, String str2) {
        return Float.compare(this.mEntityConfidence.get(str2).floatValue(), this.mEntityConfidence.get(str).floatValue());
    }
}
