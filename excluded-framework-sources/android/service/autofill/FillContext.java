package android.service.autofill;

import android.app.assist.AssistStructure;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.SparseIntArray;
import android.view.autofill.AutofillId;
import android.view.autofill.Helper;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes2.dex */
public final class FillContext implements Parcelable {
    public static final Parcelable.Creator<FillContext> CREATOR = new Parcelable.Creator<FillContext>() { // from class: android.service.autofill.FillContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FillContext createFromParcel(Parcel parcel) {
            return new FillContext(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FillContext[] newArray(int i) {
            return new FillContext[i];
        }
    };
    private final int mRequestId;
    private final AssistStructure mStructure;
    private ArrayMap<AutofillId, AssistStructure.ViewNode> mViewNodeLookupTable;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public FillContext(int i, AssistStructure assistStructure) {
        this.mRequestId = i;
        this.mStructure = assistStructure;
    }

    private FillContext(Parcel parcel) {
        this(parcel.readInt(), (AssistStructure) parcel.readParcelable(null));
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public AssistStructure getStructure() {
        return this.mStructure;
    }

    public String toString() {
        if (!Helper.sDebug) {
            return super.toString();
        }
        return "FillContext [reqId=" + this.mRequestId + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRequestId);
        parcel.writeParcelable(this.mStructure, i);
    }

    public AssistStructure.ViewNode[] findViewNodesByAutofillIds(AutofillId[] autofillIdArr) {
        LinkedList linkedList = new LinkedList();
        AssistStructure.ViewNode[] viewNodeArr = new AssistStructure.ViewNode[autofillIdArr.length];
        SparseIntArray sparseIntArray = new SparseIntArray(autofillIdArr.length);
        for (int i = 0; i < autofillIdArr.length; i++) {
            ArrayMap<AutofillId, AssistStructure.ViewNode> arrayMap = this.mViewNodeLookupTable;
            if (arrayMap != null) {
                int iIndexOfKey = arrayMap.indexOfKey(autofillIdArr[i]);
                if (iIndexOfKey >= 0) {
                    viewNodeArr[i] = this.mViewNodeLookupTable.valueAt(iIndexOfKey);
                } else {
                    sparseIntArray.put(i, 0);
                }
            } else {
                sparseIntArray.put(i, 0);
            }
        }
        int windowNodeCount = this.mStructure.getWindowNodeCount();
        for (int i2 = 0; i2 < windowNodeCount; i2++) {
            linkedList.add(this.mStructure.getWindowNodeAt(i2).getRootViewNode());
        }
        while (sparseIntArray.size() > 0 && !linkedList.isEmpty()) {
            AssistStructure.ViewNode viewNode = (AssistStructure.ViewNode) linkedList.removeFirst();
            for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
                int iKeyAt = sparseIntArray.keyAt(i3);
                AutofillId autofillId = autofillIdArr[iKeyAt];
                if (autofillId.equals(viewNode.getAutofillId())) {
                    viewNodeArr[iKeyAt] = viewNode;
                    if (this.mViewNodeLookupTable == null) {
                        this.mViewNodeLookupTable = new ArrayMap<>(autofillIdArr.length);
                    }
                    this.mViewNodeLookupTable.put(autofillId, viewNode);
                    sparseIntArray.removeAt(i3);
                    break;
                }
            }
            for (int i4 = 0; i4 < viewNode.getChildCount(); i4++) {
                linkedList.addLast(viewNode.getChildAt(i4));
            }
        }
        for (int i5 = 0; i5 < sparseIntArray.size(); i5++) {
            if (this.mViewNodeLookupTable == null) {
                this.mViewNodeLookupTable = new ArrayMap<>(sparseIntArray.size());
            }
            this.mViewNodeLookupTable.put(autofillIdArr[sparseIntArray.keyAt(i5)], null);
        }
        return viewNodeArr;
    }
}
