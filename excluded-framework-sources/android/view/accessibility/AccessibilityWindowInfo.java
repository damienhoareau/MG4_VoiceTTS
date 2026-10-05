package android.view.accessibility;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.LongArray;
import android.util.Pools;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public final class AccessibilityWindowInfo implements Parcelable {
    public static final int ACTIVE_WINDOW_ID = Integer.MAX_VALUE;
    public static final int ANY_WINDOW_ID = -2;
    private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 4;
    private static final int BOOLEAN_PROPERTY_ACTIVE = 1;
    private static final int BOOLEAN_PROPERTY_FOCUSED = 2;
    private static final int BOOLEAN_PROPERTY_PICTURE_IN_PICTURE = 8;
    private static final boolean DEBUG = false;
    private static final int MAX_POOL_SIZE = 10;
    public static final int PICTURE_IN_PICTURE_ACTION_REPLACER_WINDOW_ID = -3;
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
    public static final int TYPE_SYSTEM = 3;
    public static final int UNDEFINED_WINDOW_ID = -1;
    private static AtomicInteger sNumInstancesInUse;
    private int mBooleanProperties;
    private LongArray mChildIds;
    private CharSequence mTitle;
    private static final Pools.SynchronizedPool<AccessibilityWindowInfo> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<AccessibilityWindowInfo> CREATOR = new Parcelable.Creator<AccessibilityWindowInfo>() { // from class: android.view.accessibility.AccessibilityWindowInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityWindowInfo createFromParcel(Parcel parcel) {
            AccessibilityWindowInfo accessibilityWindowInfoObtain = AccessibilityWindowInfo.obtain();
            accessibilityWindowInfoObtain.initFromParcel(parcel);
            return accessibilityWindowInfoObtain;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityWindowInfo[] newArray(int i) {
            return new AccessibilityWindowInfo[i];
        }
    };
    private int mType = -1;
    private int mLayer = -1;
    private int mId = -1;
    private int mParentId = -1;
    private final Rect mBoundsInScreen = new Rect();
    private long mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
    private int mConnectionId = -1;

    private static String typeToString(int i) {
        if (i == 1) {
            return "TYPE_APPLICATION";
        }
        if (i == 2) {
            return "TYPE_INPUT_METHOD";
        }
        if (i == 3) {
            return "TYPE_SYSTEM";
        }
        if (i != 4) {
            return i != 5 ? "<UNKNOWN>" : "TYPE_SPLIT_SCREEN_DIVIDER";
        }
        return "TYPE_ACCESSIBILITY_OVERLAY";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private AccessibilityWindowInfo() {
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int i) {
        this.mType = i;
    }

    public int getLayer() {
        return this.mLayer;
    }

    public void setLayer(int i) {
        this.mLayer = i;
    }

    public AccessibilityNodeInfo getRoot() {
        if (this.mConnectionId == -1) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mId, AccessibilityNodeInfo.ROOT_NODE_ID, true, 4, null);
    }

    public void setAnchorId(long j) {
        this.mAnchorId = j;
    }

    public AccessibilityNodeInfo getAnchor() {
        if (this.mConnectionId == -1 || this.mAnchorId == AccessibilityNodeInfo.UNDEFINED_NODE_ID || this.mParentId == -1) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mParentId, this.mAnchorId, true, 0, null);
    }

    public void setPictureInPicture(boolean z) {
        setBooleanProperty(8, z);
    }

    public boolean isInPictureInPictureMode() {
        return getBooleanProperty(8);
    }

    public AccessibilityWindowInfo getParent() {
        if (this.mConnectionId == -1 || this.mParentId == -1) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().getWindow(this.mConnectionId, this.mParentId);
    }

    public void setParentId(int i) {
        this.mParentId = i;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int i) {
        this.mId = i;
    }

    public void setConnectionId(int i) {
        this.mConnectionId = i;
    }

    public void getBoundsInScreen(Rect rect) {
        rect.set(this.mBoundsInScreen);
    }

    public void setBoundsInScreen(Rect rect) {
        this.mBoundsInScreen.set(rect);
    }

    public boolean isActive() {
        return getBooleanProperty(1);
    }

    public void setActive(boolean z) {
        setBooleanProperty(1, z);
    }

    public boolean isFocused() {
        return getBooleanProperty(2);
    }

    public void setFocused(boolean z) {
        setBooleanProperty(2, z);
    }

    public boolean isAccessibilityFocused() {
        return getBooleanProperty(4);
    }

    public void setAccessibilityFocused(boolean z) {
        setBooleanProperty(4, z);
    }

    public int getChildCount() {
        LongArray longArray = this.mChildIds;
        if (longArray != null) {
            return longArray.size();
        }
        return 0;
    }

    public AccessibilityWindowInfo getChild(int i) {
        LongArray longArray = this.mChildIds;
        if (longArray == null) {
            throw new IndexOutOfBoundsException();
        }
        if (this.mConnectionId == -1) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().getWindow(this.mConnectionId, (int) longArray.get(i));
    }

    public void addChild(int i) {
        if (this.mChildIds == null) {
            this.mChildIds = new LongArray();
        }
        this.mChildIds.add(i);
    }

    public static AccessibilityWindowInfo obtain() {
        AccessibilityWindowInfo accessibilityWindowInfoAcquire = sPool.acquire();
        if (accessibilityWindowInfoAcquire == null) {
            accessibilityWindowInfoAcquire = new AccessibilityWindowInfo();
        }
        AtomicInteger atomicInteger = sNumInstancesInUse;
        if (atomicInteger != null) {
            atomicInteger.incrementAndGet();
        }
        return accessibilityWindowInfoAcquire;
    }

    public static AccessibilityWindowInfo obtain(AccessibilityWindowInfo accessibilityWindowInfo) {
        AccessibilityWindowInfo accessibilityWindowInfoObtain = obtain();
        accessibilityWindowInfoObtain.mType = accessibilityWindowInfo.mType;
        accessibilityWindowInfoObtain.mLayer = accessibilityWindowInfo.mLayer;
        accessibilityWindowInfoObtain.mBooleanProperties = accessibilityWindowInfo.mBooleanProperties;
        accessibilityWindowInfoObtain.mId = accessibilityWindowInfo.mId;
        accessibilityWindowInfoObtain.mParentId = accessibilityWindowInfo.mParentId;
        accessibilityWindowInfoObtain.mBoundsInScreen.set(accessibilityWindowInfo.mBoundsInScreen);
        accessibilityWindowInfoObtain.mTitle = accessibilityWindowInfo.mTitle;
        accessibilityWindowInfoObtain.mAnchorId = accessibilityWindowInfo.mAnchorId;
        LongArray longArray = accessibilityWindowInfo.mChildIds;
        if (longArray != null && longArray.size() > 0) {
            LongArray longArray2 = accessibilityWindowInfoObtain.mChildIds;
            if (longArray2 == null) {
                accessibilityWindowInfoObtain.mChildIds = accessibilityWindowInfo.mChildIds.m29clone();
            } else {
                longArray2.addAll(accessibilityWindowInfo.mChildIds);
            }
        }
        accessibilityWindowInfoObtain.mConnectionId = accessibilityWindowInfo.mConnectionId;
        return accessibilityWindowInfoObtain;
    }

    public static void setNumInstancesInUseCounter(AtomicInteger atomicInteger) {
        if (sNumInstancesInUse != null) {
            sNumInstancesInUse = atomicInteger;
        }
    }

    public void recycle() {
        clear();
        sPool.release(this);
        AtomicInteger atomicInteger = sNumInstancesInUse;
        if (atomicInteger != null) {
            atomicInteger.decrementAndGet();
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mLayer);
        parcel.writeInt(this.mBooleanProperties);
        parcel.writeInt(this.mId);
        parcel.writeInt(this.mParentId);
        this.mBoundsInScreen.writeToParcel(parcel, i);
        parcel.writeCharSequence(this.mTitle);
        parcel.writeLong(this.mAnchorId);
        LongArray longArray = this.mChildIds;
        if (longArray == null) {
            parcel.writeInt(0);
        } else {
            int size = longArray.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeInt((int) longArray.get(i2));
            }
        }
        parcel.writeInt(this.mConnectionId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.mType = parcel.readInt();
        this.mLayer = parcel.readInt();
        this.mBooleanProperties = parcel.readInt();
        this.mId = parcel.readInt();
        this.mParentId = parcel.readInt();
        this.mBoundsInScreen.readFromParcel(parcel);
        this.mTitle = parcel.readCharSequence();
        this.mAnchorId = parcel.readLong();
        int i = parcel.readInt();
        if (i > 0) {
            if (this.mChildIds == null) {
                this.mChildIds = new LongArray(i);
            }
            for (int i2 = 0; i2 < i; i2++) {
                this.mChildIds.add(parcel.readInt());
            }
        }
        this.mConnectionId = parcel.readInt();
    }

    public int hashCode() {
        return this.mId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mId == ((AccessibilityWindowInfo) obj).mId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessibilityWindowInfo[");
        sb.append("title=");
        sb.append(this.mTitle);
        sb.append(", id=");
        sb.append(this.mId);
        sb.append(", type=");
        sb.append(typeToString(this.mType));
        sb.append(", layer=");
        sb.append(this.mLayer);
        sb.append(", bounds=");
        sb.append(this.mBoundsInScreen);
        sb.append(", focused=");
        sb.append(isFocused());
        sb.append(", active=");
        sb.append(isActive());
        sb.append(", pictureInPicture=");
        sb.append(isInPictureInPictureMode());
        sb.append(", hasParent=");
        sb.append(this.mParentId != -1);
        sb.append(", isAnchored=");
        sb.append(this.mAnchorId != AccessibilityNodeInfo.UNDEFINED_NODE_ID);
        sb.append(", hasChildren=");
        LongArray longArray = this.mChildIds;
        sb.append(longArray != null && longArray.size() > 0);
        sb.append(']');
        return sb.toString();
    }

    private void clear() {
        this.mType = -1;
        this.mLayer = -1;
        this.mBooleanProperties = 0;
        this.mId = -1;
        this.mParentId = -1;
        this.mBoundsInScreen.setEmpty();
        LongArray longArray = this.mChildIds;
        if (longArray != null) {
            longArray.clear();
        }
        this.mConnectionId = -1;
        this.mAnchorId = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
        this.mTitle = null;
    }

    private boolean getBooleanProperty(int i) {
        return (i & this.mBooleanProperties) != 0;
    }

    private void setBooleanProperty(int i, boolean z) {
        if (z) {
            this.mBooleanProperties = i | this.mBooleanProperties;
        } else {
            this.mBooleanProperties = (~i) & this.mBooleanProperties;
        }
    }

    public boolean changed(AccessibilityWindowInfo accessibilityWindowInfo) {
        if (accessibilityWindowInfo.mId != this.mId) {
            throw new IllegalArgumentException("Not same window.");
        }
        if (accessibilityWindowInfo.mType != this.mType) {
            throw new IllegalArgumentException("Not same type.");
        }
        if (!this.mBoundsInScreen.equals(accessibilityWindowInfo.mBoundsInScreen) || this.mLayer != accessibilityWindowInfo.mLayer || this.mBooleanProperties != accessibilityWindowInfo.mBooleanProperties || this.mParentId != accessibilityWindowInfo.mParentId) {
            return true;
        }
        LongArray longArray = this.mChildIds;
        if (longArray == null) {
            return accessibilityWindowInfo.mChildIds != null;
        }
        return !longArray.equals(accessibilityWindowInfo.mChildIds);
    }

    public int differenceFrom(AccessibilityWindowInfo accessibilityWindowInfo) {
        if (accessibilityWindowInfo.mId != this.mId) {
            throw new IllegalArgumentException("Not same window.");
        }
        if (accessibilityWindowInfo.mType != this.mType) {
            throw new IllegalArgumentException("Not same type.");
        }
        int i = TextUtils.equals(this.mTitle, accessibilityWindowInfo.mTitle) ? 0 : 4;
        if (!this.mBoundsInScreen.equals(accessibilityWindowInfo.mBoundsInScreen)) {
            i |= 8;
        }
        if (this.mLayer != accessibilityWindowInfo.mLayer) {
            i |= 16;
        }
        if (getBooleanProperty(1) != accessibilityWindowInfo.getBooleanProperty(1)) {
            i |= 32;
        }
        if (getBooleanProperty(2) != accessibilityWindowInfo.getBooleanProperty(2)) {
            i |= 64;
        }
        if (getBooleanProperty(4) != accessibilityWindowInfo.getBooleanProperty(4)) {
            i |= 128;
        }
        if (getBooleanProperty(8) != accessibilityWindowInfo.getBooleanProperty(8)) {
            i |= 1024;
        }
        if (this.mParentId != accessibilityWindowInfo.mParentId) {
            i |= 256;
        }
        return !Objects.equals(this.mChildIds, accessibilityWindowInfo.mChildIds) ? i | 512 : i;
    }
}
