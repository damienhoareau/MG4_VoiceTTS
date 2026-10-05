package android.app.assist;

import android.app.Activity;
import android.app.slice.Slice;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.BadParcelableException;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PooledStringReader;
import android.os.PooledStringWriter;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.SettingsStringUtil;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TimedRemoteCaller;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewStructure;
import android.view.WindowManagerGlobal;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AssistStructure implements Parcelable {
    public static final Parcelable.Creator<AssistStructure> CREATOR = new Parcelable.Creator<AssistStructure>() { // from class: android.app.assist.AssistStructure.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssistStructure createFromParcel(Parcel parcel) {
            return new AssistStructure(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AssistStructure[] newArray(int i) {
            return new AssistStructure[i];
        }
    };
    static final boolean DEBUG_PARCEL = false;
    static final boolean DEBUG_PARCEL_CHILDREN = false;
    static final boolean DEBUG_PARCEL_TREE = false;
    static final String DESCRIPTOR = "android.app.AssistStructure";
    static final String TAG = "AssistStructure";
    static final int TRANSACTION_XFER = 2;
    static final int VALIDATE_VIEW_TOKEN = 572662306;
    static final int VALIDATE_WINDOW_TOKEN = 286331153;
    private long mAcquisitionEndTime;
    private long mAcquisitionStartTime;
    ComponentName mActivityComponent;
    private int mFlags;
    boolean mHaveData;
    private boolean mIsHomeActivity;
    final ArrayList<ViewNodeBuilder> mPendingAsyncChildren;
    IBinder mReceiveChannel;
    boolean mSanitizeOnWrite;
    SendChannel mSendChannel;
    Rect mTmpRect;
    final ArrayList<WindowNode> mWindowNodes;

    public static class AutofillOverlay {
        public boolean focused;
        public AutofillValue value;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setAcquisitionStartTime(long j) {
        this.mAcquisitionStartTime = j;
    }

    public void setAcquisitionEndTime(long j) {
        this.mAcquisitionEndTime = j;
    }

    public void setHomeActivity(boolean z) {
        this.mIsHomeActivity = z;
    }

    public long getAcquisitionStartTime() {
        ensureData();
        return this.mAcquisitionStartTime;
    }

    public long getAcquisitionEndTime() {
        ensureData();
        return this.mAcquisitionEndTime;
    }

    static final class SendChannel extends Binder {
        volatile AssistStructure mAssistStructure;

        SendChannel(AssistStructure assistStructure) {
            this.mAssistStructure = assistStructure;
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 2) {
                AssistStructure assistStructure = this.mAssistStructure;
                if (assistStructure == null) {
                    return true;
                }
                parcel.enforceInterface(AssistStructure.DESCRIPTOR);
                IBinder strongBinder = parcel.readStrongBinder();
                if (strongBinder != null) {
                    if (strongBinder instanceof ParcelTransferWriter) {
                        ((ParcelTransferWriter) strongBinder).writeToParcel(assistStructure, parcel2);
                        return true;
                    }
                    Log.w(AssistStructure.TAG, "Caller supplied bad token type: " + strongBinder);
                    return true;
                }
                new ParcelTransferWriter(assistStructure, parcel2).writeToParcel(assistStructure, parcel2);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }
    }

    static final class ViewStackEntry {
        int curChild;
        ViewNode node;
        int numChildren;

        ViewStackEntry() {
        }
    }

    static final class ParcelTransferWriter extends Binder {
        ViewStackEntry mCurViewStackEntry;
        int mCurViewStackPos;
        int mCurWindow;
        int mNumWindows;
        int mNumWrittenViews;
        int mNumWrittenWindows;
        final boolean mSanitizeOnWrite;
        final boolean mWriteStructure;
        final ArrayList<ViewStackEntry> mViewStack = new ArrayList<>();
        final float[] mTmpMatrix = new float[9];

        ParcelTransferWriter(AssistStructure assistStructure, Parcel parcel) {
            this.mSanitizeOnWrite = assistStructure.mSanitizeOnWrite;
            this.mWriteStructure = assistStructure.waitForReady();
            ComponentName.writeToParcel(assistStructure.mActivityComponent, parcel);
            parcel.writeInt(assistStructure.mFlags);
            parcel.writeLong(assistStructure.mAcquisitionStartTime);
            parcel.writeLong(assistStructure.mAcquisitionEndTime);
            int size = assistStructure.mWindowNodes.size();
            this.mNumWindows = size;
            if (this.mWriteStructure && size > 0) {
                parcel.writeInt(size);
            } else {
                parcel.writeInt(0);
            }
        }

        void writeToParcel(AssistStructure assistStructure, Parcel parcel) {
            int iDataPosition = parcel.dataPosition();
            this.mNumWrittenWindows = 0;
            this.mNumWrittenViews = 0;
            boolean zWriteToParcelInner = writeToParcelInner(assistStructure, parcel);
            StringBuilder sb = new StringBuilder();
            sb.append("Flattened ");
            sb.append(zWriteToParcelInner ? Slice.HINT_PARTIAL : "final");
            sb.append(" assist data: ");
            sb.append(parcel.dataPosition() - iDataPosition);
            sb.append(" bytes, containing ");
            sb.append(this.mNumWrittenWindows);
            sb.append(" windows, ");
            sb.append(this.mNumWrittenViews);
            sb.append(" views");
            Log.i(AssistStructure.TAG, sb.toString());
        }

        boolean writeToParcelInner(AssistStructure assistStructure, Parcel parcel) {
            if (this.mNumWindows == 0) {
                return false;
            }
            PooledStringWriter pooledStringWriter = new PooledStringWriter(parcel);
            while (writeNextEntryToParcel(assistStructure, parcel, pooledStringWriter)) {
                if (parcel.dataSize() > 65536) {
                    parcel.writeInt(0);
                    parcel.writeStrongBinder(this);
                    pooledStringWriter.finish();
                    return true;
                }
            }
            pooledStringWriter.finish();
            this.mViewStack.clear();
            return false;
        }

        void pushViewStackEntry(ViewNode viewNode, int i) {
            ViewStackEntry viewStackEntry;
            if (i >= this.mViewStack.size()) {
                viewStackEntry = new ViewStackEntry();
                this.mViewStack.add(viewStackEntry);
            } else {
                viewStackEntry = this.mViewStack.get(i);
            }
            viewStackEntry.node = viewNode;
            viewStackEntry.numChildren = viewNode.getChildCount();
            viewStackEntry.curChild = 0;
            this.mCurViewStackEntry = viewStackEntry;
        }

        void writeView(ViewNode viewNode, Parcel parcel, PooledStringWriter pooledStringWriter, int i) {
            parcel.writeInt(AssistStructure.VALIDATE_VIEW_TOKEN);
            int iWriteSelfToParcel = viewNode.writeSelfToParcel(parcel, pooledStringWriter, this.mSanitizeOnWrite, this.mTmpMatrix);
            this.mNumWrittenViews++;
            if ((iWriteSelfToParcel & 1048576) != 0) {
                parcel.writeInt(viewNode.mChildren.length);
                int i2 = this.mCurViewStackPos + 1;
                this.mCurViewStackPos = i2;
                pushViewStackEntry(viewNode, i2);
            }
        }

        boolean writeNextEntryToParcel(AssistStructure assistStructure, Parcel parcel, PooledStringWriter pooledStringWriter) {
            ViewStackEntry viewStackEntry;
            ViewStackEntry viewStackEntry2 = this.mCurViewStackEntry;
            if (viewStackEntry2 != null) {
                if (viewStackEntry2.curChild < this.mCurViewStackEntry.numChildren) {
                    ViewNode viewNode = this.mCurViewStackEntry.node.mChildren[this.mCurViewStackEntry.curChild];
                    this.mCurViewStackEntry.curChild++;
                    writeView(viewNode, parcel, pooledStringWriter, 1);
                    return true;
                }
                do {
                    int i = this.mCurViewStackPos - 1;
                    this.mCurViewStackPos = i;
                    if (i < 0) {
                        this.mCurViewStackEntry = null;
                        break;
                    }
                    viewStackEntry = this.mViewStack.get(i);
                    this.mCurViewStackEntry = viewStackEntry;
                } while (viewStackEntry.curChild >= this.mCurViewStackEntry.numChildren);
                return true;
            }
            int i2 = this.mCurWindow;
            if (i2 >= this.mNumWindows) {
                return false;
            }
            WindowNode windowNode = assistStructure.mWindowNodes.get(i2);
            this.mCurWindow++;
            parcel.writeInt(AssistStructure.VALIDATE_WINDOW_TOKEN);
            windowNode.writeSelfToParcel(parcel, pooledStringWriter, this.mTmpMatrix);
            this.mNumWrittenWindows++;
            ViewNode viewNode2 = windowNode.mRoot;
            this.mCurViewStackPos = 0;
            writeView(viewNode2, parcel, pooledStringWriter, 0);
            return true;
        }
    }

    final class ParcelTransferReader {
        private final IBinder mChannel;
        private Parcel mCurParcel;
        int mNumReadViews;
        int mNumReadWindows;
        PooledStringReader mStringReader;
        final float[] mTmpMatrix = new float[9];
        private IBinder mTransferToken;

        ParcelTransferReader(IBinder iBinder) {
            this.mChannel = iBinder;
        }

        void go() {
            fetchData();
            AssistStructure.this.mActivityComponent = ComponentName.readFromParcel(this.mCurParcel);
            AssistStructure.this.mFlags = this.mCurParcel.readInt();
            AssistStructure.this.mAcquisitionStartTime = this.mCurParcel.readLong();
            AssistStructure.this.mAcquisitionEndTime = this.mCurParcel.readLong();
            int i = this.mCurParcel.readInt();
            if (i > 0) {
                this.mStringReader = new PooledStringReader(this.mCurParcel);
                for (int i2 = 0; i2 < i; i2++) {
                    AssistStructure.this.mWindowNodes.add(new WindowNode(this));
                }
            }
            this.mCurParcel.recycle();
            this.mCurParcel = null;
        }

        Parcel readParcel(int i, int i2) {
            int i3 = this.mCurParcel.readInt();
            if (i3 != 0) {
                if (i3 != i) {
                    throw new BadParcelableException("Got token " + Integer.toHexString(i3) + ", expected token " + Integer.toHexString(i));
                }
                return this.mCurParcel;
            }
            IBinder strongBinder = this.mCurParcel.readStrongBinder();
            this.mTransferToken = strongBinder;
            if (strongBinder == null) {
                throw new IllegalStateException("Reached end of partial data without transfer token");
            }
            fetchData();
            this.mStringReader = new PooledStringReader(this.mCurParcel);
            this.mCurParcel.readInt();
            return this.mCurParcel;
        }

        private void fetchData() {
            Parcel parcelObtain = Parcel.obtain();
            try {
                parcelObtain.writeInterfaceToken(AssistStructure.DESCRIPTOR);
                parcelObtain.writeStrongBinder(this.mTransferToken);
                if (this.mCurParcel != null) {
                    this.mCurParcel.recycle();
                }
                Parcel parcelObtain2 = Parcel.obtain();
                this.mCurParcel = parcelObtain2;
                try {
                    this.mChannel.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain.recycle();
                    this.mNumReadViews = 0;
                    this.mNumReadWindows = 0;
                } catch (RemoteException e) {
                    Log.w(AssistStructure.TAG, "Failure reading AssistStructure data", e);
                    throw new IllegalStateException("Failure reading AssistStructure data: " + e);
                }
            } catch (Throwable th) {
                parcelObtain.recycle();
                throw th;
            }
        }
    }

    static final class ViewNodeText {
        String mHint;
        int[] mLineBaselines;
        int[] mLineCharOffsets;
        CharSequence mText;
        int mTextBackgroundColor;
        int mTextColor;
        int mTextSelectionEnd;
        int mTextSelectionStart;
        float mTextSize;
        int mTextStyle;

        ViewNodeText() {
            this.mTextColor = 1;
            this.mTextBackgroundColor = 1;
        }

        boolean isSimple() {
            return this.mTextBackgroundColor == 1 && this.mTextSelectionStart == 0 && this.mTextSelectionEnd == 0 && this.mLineCharOffsets == null && this.mLineBaselines == null && this.mHint == null;
        }

        ViewNodeText(Parcel parcel, boolean z) {
            this.mTextColor = 1;
            this.mTextBackgroundColor = 1;
            this.mText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.mTextSize = parcel.readFloat();
            this.mTextStyle = parcel.readInt();
            this.mTextColor = parcel.readInt();
            if (z) {
                return;
            }
            this.mTextBackgroundColor = parcel.readInt();
            this.mTextSelectionStart = parcel.readInt();
            this.mTextSelectionEnd = parcel.readInt();
            this.mLineCharOffsets = parcel.createIntArray();
            this.mLineBaselines = parcel.createIntArray();
            this.mHint = parcel.readString();
        }

        void writeToParcel(Parcel parcel, boolean z, boolean z2) {
            TextUtils.writeToParcel(z2 ? this.mText : "", parcel, 0);
            parcel.writeFloat(this.mTextSize);
            parcel.writeInt(this.mTextStyle);
            parcel.writeInt(this.mTextColor);
            if (z) {
                return;
            }
            parcel.writeInt(this.mTextBackgroundColor);
            parcel.writeInt(this.mTextSelectionStart);
            parcel.writeInt(this.mTextSelectionEnd);
            parcel.writeIntArray(this.mLineCharOffsets);
            parcel.writeIntArray(this.mLineBaselines);
            parcel.writeString(this.mHint);
        }
    }

    public static class WindowNode {
        final int mDisplayId;
        final int mHeight;
        final ViewNode mRoot;
        final CharSequence mTitle;
        final int mWidth;
        final int mX;
        final int mY;

        WindowNode(AssistStructure assistStructure, ViewRootImpl viewRootImpl, boolean z, int i) {
            View view = viewRootImpl.getView();
            Rect rect = new Rect();
            view.getBoundsOnScreen(rect);
            this.mX = rect.left - view.getLeft();
            this.mY = rect.top - view.getTop();
            this.mWidth = rect.width();
            this.mHeight = rect.height();
            this.mTitle = viewRootImpl.getTitle();
            this.mDisplayId = viewRootImpl.getDisplayId();
            this.mRoot = new ViewNode();
            ViewNodeBuilder viewNodeBuilder = new ViewNodeBuilder(assistStructure, this.mRoot, false);
            if ((viewRootImpl.getWindowFlags() & 8192) != 0) {
                if (z) {
                    view.onProvideAutofillStructure(viewNodeBuilder, resolveViewAutofillFlags(view.getContext(), i));
                } else {
                    view.onProvideStructure(viewNodeBuilder);
                    viewNodeBuilder.setAssistBlocked(true);
                    return;
                }
            }
            if (z) {
                view.dispatchProvideAutofillStructure(viewNodeBuilder, resolveViewAutofillFlags(view.getContext(), i));
            } else {
                view.dispatchProvideStructure(viewNodeBuilder);
            }
        }

        WindowNode(ParcelTransferReader parcelTransferReader) {
            Parcel parcel = parcelTransferReader.readParcel(AssistStructure.VALIDATE_WINDOW_TOKEN, 0);
            parcelTransferReader.mNumReadWindows++;
            this.mX = parcel.readInt();
            this.mY = parcel.readInt();
            this.mWidth = parcel.readInt();
            this.mHeight = parcel.readInt();
            this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.mDisplayId = parcel.readInt();
            this.mRoot = new ViewNode(parcelTransferReader, 0);
        }

        int resolveViewAutofillFlags(Context context, int i) {
            return ((i & 1) != 0 || context.isAutofillCompatibilityEnabled()) ? 1 : 0;
        }

        void writeSelfToParcel(Parcel parcel, PooledStringWriter pooledStringWriter, float[] fArr) {
            parcel.writeInt(this.mX);
            parcel.writeInt(this.mY);
            parcel.writeInt(this.mWidth);
            parcel.writeInt(this.mHeight);
            TextUtils.writeToParcel(this.mTitle, parcel, 0);
            parcel.writeInt(this.mDisplayId);
        }

        public int getLeft() {
            return this.mX;
        }

        public int getTop() {
            return this.mY;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public int getDisplayId() {
            return this.mDisplayId;
        }

        public ViewNode getRootViewNode() {
            return this.mRoot;
        }
    }

    public static class ViewNode {
        static final int FLAGS_ACCESSIBILITY_FOCUSED = 4096;
        static final int FLAGS_ACTIVATED = 8192;
        static final int FLAGS_ALL_CONTROL = -1048576;
        static final int FLAGS_ASSIST_BLOCKED = 128;
        static final int FLAGS_CHECKABLE = 256;
        static final int FLAGS_CHECKED = 512;
        static final int FLAGS_CLICKABLE = 1024;
        static final int FLAGS_CONTEXT_CLICKABLE = 16384;
        static final int FLAGS_DISABLED = 1;
        static final int FLAGS_FOCUSABLE = 16;
        static final int FLAGS_FOCUSED = 32;
        static final int FLAGS_HAS_ALPHA = 536870912;
        static final int FLAGS_HAS_AUTOFILL_DATA = Integer.MIN_VALUE;
        static final int FLAGS_HAS_CHILDREN = 1048576;
        static final int FLAGS_HAS_COMPLEX_TEXT = 8388608;
        static final int FLAGS_HAS_CONTENT_DESCRIPTION = 33554432;
        static final int FLAGS_HAS_ELEVATION = 268435456;
        static final int FLAGS_HAS_EXTRAS = 4194304;
        static final int FLAGS_HAS_ID = 2097152;
        static final int FLAGS_HAS_INPUT_TYPE = 262144;
        static final int FLAGS_HAS_LARGE_COORDS = 67108864;
        static final int FLAGS_HAS_LOCALE_LIST = 65536;
        static final int FLAGS_HAS_MATRIX = 1073741824;
        static final int FLAGS_HAS_SCROLL = 134217728;
        static final int FLAGS_HAS_TEXT = 16777216;
        static final int FLAGS_HAS_URL = 524288;
        static final int FLAGS_LONG_CLICKABLE = 2048;
        static final int FLAGS_OPAQUE = 32768;
        static final int FLAGS_SELECTED = 64;
        static final int FLAGS_VISIBILITY_MASK = 12;
        public static final int TEXT_COLOR_UNDEFINED = 1;
        public static final int TEXT_STYLE_BOLD = 1;
        public static final int TEXT_STYLE_ITALIC = 2;
        public static final int TEXT_STYLE_STRIKE_THRU = 8;
        public static final int TEXT_STYLE_UNDERLINE = 4;
        float mAlpha;
        String[] mAutofillHints;
        AutofillId mAutofillId;
        CharSequence[] mAutofillOptions;
        AutofillOverlay mAutofillOverlay;
        int mAutofillType;
        AutofillValue mAutofillValue;
        ViewNode[] mChildren;
        String mClassName;
        CharSequence mContentDescription;
        float mElevation;
        Bundle mExtras;
        int mFlags;
        int mHeight;
        ViewStructure.HtmlInfo mHtmlInfo;
        int mId;
        String mIdEntry;
        String mIdPackage;
        String mIdType;
        int mImportantForAutofill;
        int mInputType;
        LocaleList mLocaleList;
        Matrix mMatrix;
        int mMaxEms;
        int mMaxLength;
        int mMinEms;
        boolean mSanitized;
        int mScrollX;
        int mScrollY;
        ViewNodeText mText;
        String mTextIdEntry;
        String mWebDomain;
        String mWebScheme;
        int mWidth;
        int mX;
        int mY;

        ViewNode() {
            this.mId = -1;
            this.mAutofillType = 0;
            this.mMinEms = -1;
            this.mMaxEms = -1;
            this.mMaxLength = -1;
            this.mAlpha = 1.0f;
        }

        ViewNode(ParcelTransferReader parcelTransferReader, int i) {
            this.mId = -1;
            this.mAutofillType = 0;
            this.mMinEms = -1;
            this.mMaxEms = -1;
            this.mMaxLength = -1;
            this.mAlpha = 1.0f;
            Parcel parcel = parcelTransferReader.readParcel(AssistStructure.VALIDATE_VIEW_TOKEN, i);
            parcelTransferReader.mNumReadViews++;
            PooledStringReader pooledStringReader = parcelTransferReader.mStringReader;
            this.mClassName = pooledStringReader.readString();
            int i2 = parcel.readInt();
            this.mFlags = i2;
            if ((2097152 & i2) != 0) {
                int i3 = parcel.readInt();
                this.mId = i3;
                if (i3 != -1) {
                    String string = pooledStringReader.readString();
                    this.mIdEntry = string;
                    if (string != null) {
                        this.mIdType = pooledStringReader.readString();
                        this.mIdPackage = pooledStringReader.readString();
                    }
                }
            }
            if ((Integer.MIN_VALUE & i2) != 0) {
                this.mSanitized = parcel.readInt() == 1;
                this.mAutofillId = (AutofillId) parcel.readParcelable(null);
                this.mAutofillType = parcel.readInt();
                this.mAutofillHints = parcel.readStringArray();
                this.mAutofillValue = (AutofillValue) parcel.readParcelable(null);
                this.mAutofillOptions = parcel.readCharSequenceArray();
                Object parcelable = parcel.readParcelable(null);
                if (parcelable instanceof ViewStructure.HtmlInfo) {
                    this.mHtmlInfo = (ViewStructure.HtmlInfo) parcelable;
                }
                this.mMinEms = parcel.readInt();
                this.mMaxEms = parcel.readInt();
                this.mMaxLength = parcel.readInt();
                this.mTextIdEntry = pooledStringReader.readString();
                this.mImportantForAutofill = parcel.readInt();
            }
            if ((67108864 & i2) != 0) {
                this.mX = parcel.readInt();
                this.mY = parcel.readInt();
                this.mWidth = parcel.readInt();
                this.mHeight = parcel.readInt();
            } else {
                int i4 = parcel.readInt();
                this.mX = i4 & 32767;
                this.mY = (i4 >> 16) & 32767;
                int i5 = parcel.readInt();
                this.mWidth = i5 & 32767;
                this.mHeight = (i5 >> 16) & 32767;
            }
            if ((134217728 & i2) != 0) {
                this.mScrollX = parcel.readInt();
                this.mScrollY = parcel.readInt();
            }
            if ((1073741824 & i2) != 0) {
                this.mMatrix = new Matrix();
                parcel.readFloatArray(parcelTransferReader.mTmpMatrix);
                this.mMatrix.setValues(parcelTransferReader.mTmpMatrix);
            }
            if ((268435456 & i2) != 0) {
                this.mElevation = parcel.readFloat();
            }
            if ((536870912 & i2) != 0) {
                this.mAlpha = parcel.readFloat();
            }
            if ((33554432 & i2) != 0) {
                this.mContentDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            }
            if ((16777216 & i2) != 0) {
                this.mText = new ViewNodeText(parcel, (8388608 & i2) == 0);
            }
            if ((262144 & i2) != 0) {
                this.mInputType = parcel.readInt();
            }
            if ((524288 & i2) != 0) {
                this.mWebScheme = parcel.readString();
                this.mWebDomain = parcel.readString();
            }
            if ((65536 & i2) != 0) {
                this.mLocaleList = (LocaleList) parcel.readParcelable(null);
            }
            if ((4194304 & i2) != 0) {
                this.mExtras = parcel.readBundle();
            }
            if ((1048576 & i2) != 0) {
                int i6 = parcel.readInt();
                this.mChildren = new ViewNode[i6];
                for (int i7 = 0; i7 < i6; i7++) {
                    this.mChildren[i7] = new ViewNode(parcelTransferReader, i + 1);
                }
            }
        }

        /* JADX WARN: Code duplicated, block: B:22:0x0041  */
        /* JADX WARN: Multi-variable type inference failed */
        int writeSelfToParcel(Parcel parcel, PooledStringWriter pooledStringWriter, boolean z, float[] fArr) {
            boolean z2;
            AutofillValue autofillValue;
            int i = this.mFlags & 1048575;
            if (this.mId != -1) {
                i |= 2097152;
            }
            if (this.mAutofillId != null) {
                i |= Integer.MIN_VALUE;
            }
            if ((this.mX & (-32768)) == 0 && (this.mY & (-32768)) == 0) {
                if (((this.mWidth & (-32768)) != 0) | ((this.mHeight & (-32768)) != 0)) {
                    i |= 67108864;
                }
            } else {
                i |= 67108864;
            }
            if (this.mScrollX != 0 || this.mScrollY != 0) {
                i |= 134217728;
            }
            if (this.mMatrix != null) {
                i |= 1073741824;
            }
            if (this.mElevation != 0.0f) {
                i |= 268435456;
            }
            if (this.mAlpha != 1.0f) {
                i |= 536870912;
            }
            if (this.mContentDescription != null) {
                i |= 33554432;
            }
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                i |= 16777216;
                if (!viewNodeText.isSimple()) {
                    i |= 8388608;
                }
            }
            if (this.mInputType != 0) {
                i |= 262144;
            }
            if (this.mWebScheme != null || this.mWebDomain != null) {
                i |= 524288;
            }
            if (this.mLocaleList != null) {
                i |= 65536;
            }
            if (this.mExtras != null) {
                i |= 4194304;
            }
            if (this.mChildren != null) {
                i |= 1048576;
            }
            pooledStringWriter.writeString(this.mClassName);
            int i2 = i & Integer.MIN_VALUE;
            int i3 = (i2 == 0 || (!this.mSanitized && z)) ? i : i & (-513);
            AutofillOverlay autofillOverlay = this.mAutofillOverlay;
            if (autofillOverlay != null) {
                i3 = autofillOverlay.focused ? i3 | 32 : i3 & (-33);
            }
            parcel.writeInt(i3);
            if ((2097152 & i) != 0) {
                parcel.writeInt(this.mId);
                if (this.mId != -1) {
                    pooledStringWriter.writeString(this.mIdEntry);
                    if (this.mIdEntry != null) {
                        pooledStringWriter.writeString(this.mIdType);
                        pooledStringWriter.writeString(this.mIdPackage);
                    }
                }
            }
            if (i2 != 0) {
                z2 = this.mSanitized || !z;
                parcel.writeInt(this.mSanitized ? 1 : 0);
                parcel.writeParcelable(this.mAutofillId, 0);
                parcel.writeInt(this.mAutofillType);
                parcel.writeStringArray(this.mAutofillHints);
                if (z2) {
                    autofillValue = this.mAutofillValue;
                } else {
                    AutofillOverlay autofillOverlay2 = this.mAutofillOverlay;
                    autofillValue = (autofillOverlay2 == null || autofillOverlay2.value == null) ? null : this.mAutofillOverlay.value;
                }
                parcel.writeParcelable(autofillValue, 0);
                parcel.writeCharSequenceArray(this.mAutofillOptions);
                ViewStructure.HtmlInfo htmlInfo = this.mHtmlInfo;
                if (htmlInfo instanceof Parcelable) {
                    parcel.writeParcelable((Parcelable) htmlInfo, 0);
                } else {
                    parcel.writeParcelable(null, 0);
                }
                parcel.writeInt(this.mMinEms);
                parcel.writeInt(this.mMaxEms);
                parcel.writeInt(this.mMaxLength);
                pooledStringWriter.writeString(this.mTextIdEntry);
                parcel.writeInt(this.mImportantForAutofill);
            } else {
                z2 = true;
            }
            if ((i & 67108864) != 0) {
                parcel.writeInt(this.mX);
                parcel.writeInt(this.mY);
                parcel.writeInt(this.mWidth);
                parcel.writeInt(this.mHeight);
            } else {
                parcel.writeInt((this.mY << 16) | this.mX);
                parcel.writeInt((this.mHeight << 16) | this.mWidth);
            }
            if ((i & 134217728) != 0) {
                parcel.writeInt(this.mScrollX);
                parcel.writeInt(this.mScrollY);
            }
            if ((i & 1073741824) != 0) {
                this.mMatrix.getValues(fArr);
                parcel.writeFloatArray(fArr);
            }
            if ((i & 268435456) != 0) {
                parcel.writeFloat(this.mElevation);
            }
            if ((i & 536870912) != 0) {
                parcel.writeFloat(this.mAlpha);
            }
            if ((i & 33554432) != 0) {
                TextUtils.writeToParcel(this.mContentDescription, parcel, 0);
            }
            if ((i & 16777216) != 0) {
                this.mText.writeToParcel(parcel, (i & 8388608) == 0, z2);
            }
            if ((i & 262144) != 0) {
                parcel.writeInt(this.mInputType);
            }
            if ((i & 524288) != 0) {
                parcel.writeString(this.mWebScheme);
                parcel.writeString(this.mWebDomain);
            }
            if ((i & 65536) != 0) {
                parcel.writeParcelable(this.mLocaleList, 0);
            }
            if ((i & 4194304) != 0) {
                parcel.writeBundle(this.mExtras);
            }
            return i;
        }

        public int getId() {
            return this.mId;
        }

        public String getIdPackage() {
            return this.mIdPackage;
        }

        public String getIdType() {
            return this.mIdType;
        }

        public String getIdEntry() {
            return this.mIdEntry;
        }

        public AutofillId getAutofillId() {
            return this.mAutofillId;
        }

        public int getAutofillType() {
            return this.mAutofillType;
        }

        public String[] getAutofillHints() {
            return this.mAutofillHints;
        }

        public AutofillValue getAutofillValue() {
            return this.mAutofillValue;
        }

        public void setAutofillOverlay(AutofillOverlay autofillOverlay) {
            this.mAutofillOverlay = autofillOverlay;
        }

        public CharSequence[] getAutofillOptions() {
            return this.mAutofillOptions;
        }

        public int getInputType() {
            return this.mInputType;
        }

        public boolean isSanitized() {
            return this.mSanitized;
        }

        public void updateAutofillValue(AutofillValue autofillValue) {
            this.mAutofillValue = autofillValue;
            if (autofillValue.isText()) {
                if (this.mText == null) {
                    this.mText = new ViewNodeText();
                }
                this.mText.mText = autofillValue.getTextValue();
            }
        }

        public int getLeft() {
            return this.mX;
        }

        public int getTop() {
            return this.mY;
        }

        public int getScrollX() {
            return this.mScrollX;
        }

        public int getScrollY() {
            return this.mScrollY;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public Matrix getTransformation() {
            return this.mMatrix;
        }

        public float getElevation() {
            return this.mElevation;
        }

        public float getAlpha() {
            return this.mAlpha;
        }

        public int getVisibility() {
            return this.mFlags & 12;
        }

        public boolean isAssistBlocked() {
            return (this.mFlags & 128) != 0;
        }

        public boolean isEnabled() {
            return (this.mFlags & 1) == 0;
        }

        public boolean isClickable() {
            return (this.mFlags & 1024) != 0;
        }

        public boolean isFocusable() {
            return (this.mFlags & 16) != 0;
        }

        public boolean isFocused() {
            return (this.mFlags & 32) != 0;
        }

        public boolean isAccessibilityFocused() {
            return (this.mFlags & 4096) != 0;
        }

        public boolean isCheckable() {
            return (this.mFlags & 256) != 0;
        }

        public boolean isChecked() {
            return (this.mFlags & 512) != 0;
        }

        public boolean isSelected() {
            return (this.mFlags & 64) != 0;
        }

        public boolean isActivated() {
            return (this.mFlags & 8192) != 0;
        }

        public boolean isOpaque() {
            return (this.mFlags & 32768) != 0;
        }

        public boolean isLongClickable() {
            return (this.mFlags & 2048) != 0;
        }

        public boolean isContextClickable() {
            return (this.mFlags & 16384) != 0;
        }

        public String getClassName() {
            return this.mClassName;
        }

        public CharSequence getContentDescription() {
            return this.mContentDescription;
        }

        public String getWebDomain() {
            return this.mWebDomain;
        }

        public void setWebDomain(String str) {
            if (str == null) {
                return;
            }
            Uri uri = Uri.parse(str);
            if (uri == null) {
                Log.w(AssistStructure.TAG, "Failed to parse web domain");
            } else {
                this.mWebScheme = uri.getScheme();
                this.mWebDomain = uri.getHost();
            }
        }

        public String getWebScheme() {
            return this.mWebScheme;
        }

        public ViewStructure.HtmlInfo getHtmlInfo() {
            return this.mHtmlInfo;
        }

        public LocaleList getLocaleList() {
            return this.mLocaleList;
        }

        public CharSequence getText() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mText;
            }
            return null;
        }

        public int getTextSelectionStart() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextSelectionStart;
            }
            return -1;
        }

        public int getTextSelectionEnd() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextSelectionEnd;
            }
            return -1;
        }

        public int getTextColor() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextColor;
            }
            return 1;
        }

        public int getTextBackgroundColor() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextBackgroundColor;
            }
            return 1;
        }

        public float getTextSize() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextSize;
            }
            return 0.0f;
        }

        public int getTextStyle() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mTextStyle;
            }
            return 0;
        }

        public int[] getTextLineCharOffsets() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mLineCharOffsets;
            }
            return null;
        }

        public int[] getTextLineBaselines() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mLineBaselines;
            }
            return null;
        }

        public String getTextIdEntry() {
            return this.mTextIdEntry;
        }

        public String getHint() {
            ViewNodeText viewNodeText = this.mText;
            if (viewNodeText != null) {
                return viewNodeText.mHint;
            }
            return null;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public int getChildCount() {
            ViewNode[] viewNodeArr = this.mChildren;
            if (viewNodeArr != null) {
                return viewNodeArr.length;
            }
            return 0;
        }

        public ViewNode getChildAt(int i) {
            return this.mChildren[i];
        }

        public int getMinTextEms() {
            return this.mMinEms;
        }

        public int getMaxTextEms() {
            return this.mMaxEms;
        }

        public int getMaxTextLength() {
            return this.mMaxLength;
        }

        public int getImportantForAutofill() {
            return this.mImportantForAutofill;
        }
    }

    static class ViewNodeBuilder extends ViewStructure {
        final AssistStructure mAssist;
        final boolean mAsync;
        final ViewNode mNode;

        ViewNodeBuilder(AssistStructure assistStructure, ViewNode viewNode, boolean z) {
            this.mAssist = assistStructure;
            this.mNode = viewNode;
            this.mAsync = z;
        }

        @Override // android.view.ViewStructure
        public void setId(int i, String str, String str2, String str3) {
            this.mNode.mId = i;
            this.mNode.mIdPackage = str;
            this.mNode.mIdType = str2;
            this.mNode.mIdEntry = str3;
        }

        @Override // android.view.ViewStructure
        public void setDimens(int i, int i2, int i3, int i4, int i5, int i6) {
            this.mNode.mX = i;
            this.mNode.mY = i2;
            this.mNode.mScrollX = i3;
            this.mNode.mScrollY = i4;
            this.mNode.mWidth = i5;
            this.mNode.mHeight = i6;
        }

        @Override // android.view.ViewStructure
        public void setTransformation(Matrix matrix) {
            if (matrix == null) {
                this.mNode.mMatrix = null;
            } else {
                this.mNode.mMatrix = new Matrix(matrix);
            }
        }

        @Override // android.view.ViewStructure
        public void setElevation(float f) {
            this.mNode.mElevation = f;
        }

        @Override // android.view.ViewStructure
        public void setAlpha(float f) {
            this.mNode.mAlpha = f;
        }

        @Override // android.view.ViewStructure
        public void setVisibility(int i) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = i | (viewNode.mFlags & (-13));
        }

        @Override // android.view.ViewStructure
        public void setAssistBlocked(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 128 : 0) | (viewNode.mFlags & (-129));
        }

        @Override // android.view.ViewStructure
        public void setEnabled(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (!z ? 1 : 0) | (viewNode.mFlags & (-2));
        }

        @Override // android.view.ViewStructure
        public void setClickable(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 1024 : 0) | (viewNode.mFlags & (-1025));
        }

        @Override // android.view.ViewStructure
        public void setLongClickable(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 2048 : 0) | (viewNode.mFlags & (-2049));
        }

        @Override // android.view.ViewStructure
        public void setContextClickable(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 16384 : 0) | (viewNode.mFlags & (-16385));
        }

        @Override // android.view.ViewStructure
        public void setFocusable(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 16 : 0) | (viewNode.mFlags & (-17));
        }

        @Override // android.view.ViewStructure
        public void setFocused(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 32 : 0) | (viewNode.mFlags & (-33));
        }

        @Override // android.view.ViewStructure
        public void setAccessibilityFocused(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 4096 : 0) | (viewNode.mFlags & (-4097));
        }

        @Override // android.view.ViewStructure
        public void setCheckable(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 256 : 0) | (viewNode.mFlags & (-257));
        }

        @Override // android.view.ViewStructure
        public void setChecked(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 512 : 0) | (viewNode.mFlags & (-513));
        }

        @Override // android.view.ViewStructure
        public void setSelected(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 64 : 0) | (viewNode.mFlags & (-65));
        }

        @Override // android.view.ViewStructure
        public void setActivated(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 8192 : 0) | (viewNode.mFlags & (-8193));
        }

        @Override // android.view.ViewStructure
        public void setOpaque(boolean z) {
            ViewNode viewNode = this.mNode;
            viewNode.mFlags = (z ? 32768 : 0) | (viewNode.mFlags & (-32769));
        }

        @Override // android.view.ViewStructure
        public void setClassName(String str) {
            this.mNode.mClassName = str;
        }

        @Override // android.view.ViewStructure
        public void setContentDescription(CharSequence charSequence) {
            this.mNode.mContentDescription = charSequence;
        }

        private final ViewNodeText getNodeText() {
            if (this.mNode.mText != null) {
                return this.mNode.mText;
            }
            this.mNode.mText = new ViewNodeText();
            return this.mNode.mText;
        }

        @Override // android.view.ViewStructure
        public void setText(CharSequence charSequence) {
            ViewNodeText nodeText = getNodeText();
            nodeText.mText = TextUtils.trimNoCopySpans(charSequence);
            nodeText.mTextSelectionEnd = -1;
            nodeText.mTextSelectionStart = -1;
        }

        @Override // android.view.ViewStructure
        public void setText(CharSequence charSequence, int i, int i2) {
            ViewNodeText nodeText = getNodeText();
            nodeText.mText = TextUtils.trimNoCopySpans(charSequence);
            nodeText.mTextSelectionStart = i;
            nodeText.mTextSelectionEnd = i2;
        }

        @Override // android.view.ViewStructure
        public void setTextStyle(float f, int i, int i2, int i3) {
            ViewNodeText nodeText = getNodeText();
            nodeText.mTextColor = i;
            nodeText.mTextBackgroundColor = i2;
            nodeText.mTextSize = f;
            nodeText.mTextStyle = i3;
        }

        @Override // android.view.ViewStructure
        public void setTextLines(int[] iArr, int[] iArr2) {
            ViewNodeText nodeText = getNodeText();
            nodeText.mLineCharOffsets = iArr;
            nodeText.mLineBaselines = iArr2;
        }

        @Override // android.view.ViewStructure
        public void setTextIdEntry(String str) {
            this.mNode.mTextIdEntry = (String) Preconditions.checkNotNull(str);
        }

        @Override // android.view.ViewStructure
        public void setHint(CharSequence charSequence) {
            getNodeText().mHint = charSequence != null ? charSequence.toString() : null;
        }

        @Override // android.view.ViewStructure
        public CharSequence getText() {
            if (this.mNode.mText != null) {
                return this.mNode.mText.mText;
            }
            return null;
        }

        @Override // android.view.ViewStructure
        public int getTextSelectionStart() {
            if (this.mNode.mText != null) {
                return this.mNode.mText.mTextSelectionStart;
            }
            return -1;
        }

        @Override // android.view.ViewStructure
        public int getTextSelectionEnd() {
            if (this.mNode.mText != null) {
                return this.mNode.mText.mTextSelectionEnd;
            }
            return -1;
        }

        @Override // android.view.ViewStructure
        public CharSequence getHint() {
            if (this.mNode.mText != null) {
                return this.mNode.mText.mHint;
            }
            return null;
        }

        @Override // android.view.ViewStructure
        public Bundle getExtras() {
            if (this.mNode.mExtras != null) {
                return this.mNode.mExtras;
            }
            this.mNode.mExtras = new Bundle();
            return this.mNode.mExtras;
        }

        @Override // android.view.ViewStructure
        public boolean hasExtras() {
            return this.mNode.mExtras != null;
        }

        @Override // android.view.ViewStructure
        public void setChildCount(int i) {
            this.mNode.mChildren = new ViewNode[i];
        }

        @Override // android.view.ViewStructure
        public int addChildCount(int i) {
            if (this.mNode.mChildren == null) {
                setChildCount(i);
                return 0;
            }
            int length = this.mNode.mChildren.length;
            ViewNode[] viewNodeArr = new ViewNode[i + length];
            System.arraycopy(this.mNode.mChildren, 0, viewNodeArr, 0, length);
            this.mNode.mChildren = viewNodeArr;
            return length;
        }

        @Override // android.view.ViewStructure
        public int getChildCount() {
            if (this.mNode.mChildren != null) {
                return this.mNode.mChildren.length;
            }
            return 0;
        }

        @Override // android.view.ViewStructure
        public ViewStructure newChild(int i) {
            ViewNode viewNode = new ViewNode();
            this.mNode.mChildren[i] = viewNode;
            return new ViewNodeBuilder(this.mAssist, viewNode, false);
        }

        @Override // android.view.ViewStructure
        public ViewStructure asyncNewChild(int i) {
            ViewNodeBuilder viewNodeBuilder;
            synchronized (this.mAssist) {
                ViewNode viewNode = new ViewNode();
                this.mNode.mChildren[i] = viewNode;
                viewNodeBuilder = new ViewNodeBuilder(this.mAssist, viewNode, true);
                this.mAssist.mPendingAsyncChildren.add(viewNodeBuilder);
            }
            return viewNodeBuilder;
        }

        @Override // android.view.ViewStructure
        public void asyncCommit() {
            synchronized (this.mAssist) {
                if (!this.mAsync) {
                    throw new IllegalStateException("Child " + this + " was not created with ViewStructure.asyncNewChild");
                }
                if (!this.mAssist.mPendingAsyncChildren.remove(this)) {
                    throw new IllegalStateException("Child " + this + " already committed");
                }
                this.mAssist.notifyAll();
            }
        }

        @Override // android.view.ViewStructure
        public Rect getTempRect() {
            return this.mAssist.mTmpRect;
        }

        @Override // android.view.ViewStructure
        public void setAutofillId(AutofillId autofillId) {
            this.mNode.mAutofillId = autofillId;
        }

        @Override // android.view.ViewStructure
        public void setAutofillId(AutofillId autofillId, int i) {
            this.mNode.mAutofillId = new AutofillId(autofillId, i);
        }

        @Override // android.view.ViewStructure
        public AutofillId getAutofillId() {
            return this.mNode.mAutofillId;
        }

        @Override // android.view.ViewStructure
        public void setAutofillType(int i) {
            this.mNode.mAutofillType = i;
        }

        @Override // android.view.ViewStructure
        public void setAutofillHints(String[] strArr) {
            this.mNode.mAutofillHints = strArr;
        }

        @Override // android.view.ViewStructure
        public void setAutofillValue(AutofillValue autofillValue) {
            this.mNode.mAutofillValue = autofillValue;
        }

        @Override // android.view.ViewStructure
        public void setAutofillOptions(CharSequence[] charSequenceArr) {
            this.mNode.mAutofillOptions = charSequenceArr;
        }

        @Override // android.view.ViewStructure
        public void setImportantForAutofill(int i) {
            this.mNode.mImportantForAutofill = i;
        }

        @Override // android.view.ViewStructure
        public void setInputType(int i) {
            this.mNode.mInputType = i;
        }

        @Override // android.view.ViewStructure
        public void setMinTextEms(int i) {
            this.mNode.mMinEms = i;
        }

        @Override // android.view.ViewStructure
        public void setMaxTextEms(int i) {
            this.mNode.mMaxEms = i;
        }

        @Override // android.view.ViewStructure
        public void setMaxTextLength(int i) {
            this.mNode.mMaxLength = i;
        }

        @Override // android.view.ViewStructure
        public void setDataIsSensitive(boolean z) {
            this.mNode.mSanitized = !z;
        }

        @Override // android.view.ViewStructure
        public void setWebDomain(String str) {
            this.mNode.setWebDomain(str);
        }

        @Override // android.view.ViewStructure
        public void setLocaleList(LocaleList localeList) {
            this.mNode.mLocaleList = localeList;
        }

        @Override // android.view.ViewStructure
        public ViewStructure.HtmlInfo.Builder newHtmlInfoBuilder(String str) {
            return new HtmlInfoNodeBuilder(str);
        }

        @Override // android.view.ViewStructure
        public void setHtmlInfo(ViewStructure.HtmlInfo htmlInfo) {
            this.mNode.mHtmlInfo = htmlInfo;
        }
    }

    private static final class HtmlInfoNode extends ViewStructure.HtmlInfo implements Parcelable {
        public static final Parcelable.Creator<HtmlInfoNode> CREATOR = new Parcelable.Creator<HtmlInfoNode>() { // from class: android.app.assist.AssistStructure.HtmlInfoNode.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public HtmlInfoNode createFromParcel(Parcel parcel) {
                HtmlInfoNodeBuilder htmlInfoNodeBuilder = new HtmlInfoNodeBuilder(parcel.readString());
                String[] stringArray = parcel.readStringArray();
                String[] stringArray2 = parcel.readStringArray();
                if (stringArray != null && stringArray2 != null) {
                    if (stringArray.length != stringArray2.length) {
                        Log.w(AssistStructure.TAG, "HtmlInfo attributes mismatch: names=" + stringArray.length + ", values=" + stringArray2.length);
                    } else {
                        for (int i = 0; i < stringArray.length; i++) {
                            htmlInfoNodeBuilder.addAttribute(stringArray[i], stringArray2[i]);
                        }
                    }
                }
                return htmlInfoNodeBuilder.build();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public HtmlInfoNode[] newArray(int i) {
                return new HtmlInfoNode[i];
            }
        };
        private ArrayList<Pair<String, String>> mAttributes;
        private final String[] mNames;
        private final String mTag;
        private final String[] mValues;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        private HtmlInfoNode(HtmlInfoNodeBuilder htmlInfoNodeBuilder) {
            this.mTag = htmlInfoNodeBuilder.mTag;
            if (htmlInfoNodeBuilder.mNames == null) {
                this.mNames = null;
                this.mValues = null;
            } else {
                this.mNames = new String[htmlInfoNodeBuilder.mNames.size()];
                this.mValues = new String[htmlInfoNodeBuilder.mValues.size()];
                htmlInfoNodeBuilder.mNames.toArray(this.mNames);
                htmlInfoNodeBuilder.mValues.toArray(this.mValues);
            }
        }

        @Override // android.view.ViewStructure.HtmlInfo
        public String getTag() {
            return this.mTag;
        }

        @Override // android.view.ViewStructure.HtmlInfo
        public List<Pair<String, String>> getAttributes() {
            if (this.mAttributes == null && this.mNames != null) {
                this.mAttributes = new ArrayList<>(this.mNames.length);
                for (int i = 0; i < this.mNames.length; i++) {
                    this.mAttributes.add(i, new Pair<>(this.mNames[i], this.mValues[i]));
                }
            }
            return this.mAttributes;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mTag);
            parcel.writeStringArray(this.mNames);
            parcel.writeStringArray(this.mValues);
        }
    }

    private static final class HtmlInfoNodeBuilder extends ViewStructure.HtmlInfo.Builder {
        private ArrayList<String> mNames;
        private final String mTag;
        private ArrayList<String> mValues;

        HtmlInfoNodeBuilder(String str) {
            this.mTag = str;
        }

        @Override // android.view.ViewStructure.HtmlInfo.Builder
        public ViewStructure.HtmlInfo.Builder addAttribute(String str, String str2) {
            if (this.mNames == null) {
                this.mNames = new ArrayList<>();
                this.mValues = new ArrayList<>();
            }
            this.mNames.add(str);
            this.mValues.add(str2);
            return this;
        }

        @Override // android.view.ViewStructure.HtmlInfo.Builder
        public HtmlInfoNode build() {
            return new HtmlInfoNode(this);
        }
    }

    public AssistStructure(Activity activity, boolean z, int i) {
        this.mWindowNodes = new ArrayList<>();
        this.mPendingAsyncChildren = new ArrayList<>();
        this.mTmpRect = new Rect();
        this.mSanitizeOnWrite = false;
        this.mHaveData = true;
        this.mActivityComponent = activity.getComponentName();
        this.mFlags = i;
        ArrayList<ViewRootImpl> rootViews = WindowManagerGlobal.getInstance().getRootViews(activity.getActivityToken());
        for (int i2 = 0; i2 < rootViews.size(); i2++) {
            ViewRootImpl viewRootImpl = rootViews.get(i2);
            if (viewRootImpl.getView() == null) {
                Log.w(TAG, "Skipping window with dettached view: " + ((Object) viewRootImpl.getTitle()));
            } else {
                this.mWindowNodes.add(new WindowNode(this, viewRootImpl, z, i));
            }
        }
    }

    public AssistStructure() {
        this.mWindowNodes = new ArrayList<>();
        this.mPendingAsyncChildren = new ArrayList<>();
        this.mTmpRect = new Rect();
        this.mSanitizeOnWrite = false;
        this.mHaveData = true;
        this.mActivityComponent = null;
        this.mFlags = 0;
    }

    public AssistStructure(Parcel parcel) {
        this.mWindowNodes = new ArrayList<>();
        this.mPendingAsyncChildren = new ArrayList<>();
        this.mTmpRect = new Rect();
        this.mSanitizeOnWrite = false;
        this.mIsHomeActivity = parcel.readInt() == 1;
        this.mReceiveChannel = parcel.readStrongBinder();
    }

    public void sanitizeForParceling(boolean z) {
        this.mSanitizeOnWrite = z;
    }

    public void dump(boolean z) {
        if (this.mActivityComponent == null) {
            Log.i(TAG, "dump(): calling ensureData() first");
            ensureData();
        }
        Log.i(TAG, "Activity: " + this.mActivityComponent.flattenToShortString());
        Log.i(TAG, "Sanitize on write: " + this.mSanitizeOnWrite);
        Log.i(TAG, "Flags: " + this.mFlags);
        int windowNodeCount = getWindowNodeCount();
        for (int i = 0; i < windowNodeCount; i++) {
            WindowNode windowNodeAt = getWindowNodeAt(i);
            Log.i(TAG, "Window #" + i + " [" + windowNodeAt.getLeft() + "," + windowNodeAt.getTop() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + windowNodeAt.getWidth() + "x" + windowNodeAt.getHeight() + "] " + ((Object) windowNodeAt.getTitle()));
            dump("  ", windowNodeAt.getRootViewNode(), z);
        }
    }

    void dump(String str, ViewNode viewNode, boolean z) {
        Log.i(TAG, str + "View [" + viewNode.getLeft() + "," + viewNode.getTop() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + viewNode.getWidth() + "x" + viewNode.getHeight() + "] " + viewNode.getClassName());
        int id = viewNode.getId();
        if (id != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("  ID: #");
            sb.append(Integer.toHexString(id));
            String idEntry = viewNode.getIdEntry();
            if (idEntry != null) {
                String idType = viewNode.getIdType();
                String idPackage = viewNode.getIdPackage();
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                sb.append(idPackage);
                sb.append(SettingsStringUtil.DELIMITER);
                sb.append(idType);
                sb.append("/");
                sb.append(idEntry);
            }
            Log.i(TAG, sb.toString());
        }
        int scrollX = viewNode.getScrollX();
        int scrollY = viewNode.getScrollY();
        if (scrollX != 0 || scrollY != 0) {
            Log.i(TAG, str + "  Scroll: " + scrollX + "," + scrollY);
        }
        Matrix transformation = viewNode.getTransformation();
        if (transformation != null) {
            Log.i(TAG, str + "  Transformation: " + transformation);
        }
        float elevation = viewNode.getElevation();
        if (elevation != 0.0f) {
            Log.i(TAG, str + "  Elevation: " + elevation);
        }
        if (viewNode.getAlpha() != 0.0f) {
            Log.i(TAG, str + "  Alpha: " + elevation);
        }
        CharSequence contentDescription = viewNode.getContentDescription();
        if (contentDescription != null) {
            Log.i(TAG, str + "  Content description: " + ((Object) contentDescription));
        }
        CharSequence text = viewNode.getText();
        if (text != null) {
            Log.i(TAG, str + "  Text (sel " + viewNode.getTextSelectionStart() + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + viewNode.getTextSelectionEnd() + "): " + ((viewNode.isSanitized() || z) ? text.toString() : "REDACTED[" + text.length() + " chars]"));
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append("  Text size: ");
            sb2.append(viewNode.getTextSize());
            sb2.append(" , style: #");
            sb2.append(viewNode.getTextStyle());
            Log.i(TAG, sb2.toString());
            Log.i(TAG, str + "  Text color fg: #" + Integer.toHexString(viewNode.getTextColor()) + ", bg: #" + Integer.toHexString(viewNode.getTextBackgroundColor()));
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append("  Input type: ");
            sb3.append(viewNode.getInputType());
            Log.i(TAG, sb3.toString());
            Log.i(TAG, str + "  Resource id: " + viewNode.getTextIdEntry());
        }
        String webDomain = viewNode.getWebDomain();
        if (webDomain != null) {
            Log.i(TAG, str + "  Web domain: " + webDomain);
        }
        ViewStructure.HtmlInfo htmlInfo = viewNode.getHtmlInfo();
        if (htmlInfo != null) {
            Log.i(TAG, str + "  HtmlInfo: tag=" + htmlInfo.getTag() + ", attr=" + htmlInfo.getAttributes());
        }
        LocaleList localeList = viewNode.getLocaleList();
        if (localeList != null) {
            Log.i(TAG, str + "  LocaleList: " + localeList);
        }
        String hint = viewNode.getHint();
        if (hint != null) {
            Log.i(TAG, str + "  Hint: " + hint);
        }
        Bundle extras = viewNode.getExtras();
        if (extras != null) {
            Log.i(TAG, str + "  Extras: " + extras);
        }
        if (viewNode.isAssistBlocked()) {
            Log.i(TAG, str + "  BLOCKED");
        }
        AutofillId autofillId = viewNode.getAutofillId();
        if (autofillId == null) {
            Log.i(TAG, str + " NO autofill ID");
        } else {
            Log.i(TAG, str + "Autofill info: id= " + autofillId + ", type=" + viewNode.getAutofillType() + ", options=" + Arrays.toString(viewNode.getAutofillOptions()) + ", hints=" + Arrays.toString(viewNode.getAutofillHints()) + ", value=" + viewNode.getAutofillValue() + ", sanitized=" + viewNode.isSanitized() + ", importantFor=" + viewNode.getImportantForAutofill());
        }
        int childCount = viewNode.getChildCount();
        if (childCount > 0) {
            Log.i(TAG, str + "  Children:");
            String str2 = str + "    ";
            for (int i = 0; i < childCount; i++) {
                dump(str2, viewNode.getChildAt(i), z);
            }
        }
    }

    public ComponentName getActivityComponent() {
        ensureData();
        return this.mActivityComponent;
    }

    public void setActivityComponent(ComponentName componentName) {
        ensureData();
        this.mActivityComponent = componentName;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isHomeActivity() {
        return this.mIsHomeActivity;
    }

    public int getWindowNodeCount() {
        ensureData();
        return this.mWindowNodes.size();
    }

    public WindowNode getWindowNodeAt(int i) {
        ensureData();
        return this.mWindowNodes.get(i);
    }

    public void ensureDataForAutofill() {
        if (this.mHaveData) {
            return;
        }
        this.mHaveData = true;
        Binder.allowBlocking(this.mReceiveChannel);
        try {
            new ParcelTransferReader(this.mReceiveChannel).go();
        } finally {
            Binder.defaultBlocking(this.mReceiveChannel);
        }
    }

    public void ensureData() {
        if (this.mHaveData) {
            return;
        }
        this.mHaveData = true;
        new ParcelTransferReader(this.mReceiveChannel).go();
    }

    boolean waitForReady() {
        boolean z;
        synchronized (this) {
            long jUptimeMillis = SystemClock.uptimeMillis() + TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS;
            while (this.mPendingAsyncChildren.size() > 0) {
                long jUptimeMillis2 = SystemClock.uptimeMillis();
                if (jUptimeMillis2 >= jUptimeMillis) {
                    break;
                }
                try {
                    wait(jUptimeMillis - jUptimeMillis2);
                } catch (InterruptedException unused) {
                }
            }
            if (this.mPendingAsyncChildren.size() > 0) {
                Log.w(TAG, "Skipping assist structure, waiting too long for async children (have " + this.mPendingAsyncChildren.size() + " remaining");
                z = true;
            } else {
                z = false;
            }
        }
        return !z;
    }

    public void clearSendChannel() {
        SendChannel sendChannel = this.mSendChannel;
        if (sendChannel != null) {
            sendChannel.mAssistStructure = null;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mIsHomeActivity ? 1 : 0);
        if (this.mHaveData) {
            if (this.mSendChannel == null) {
                this.mSendChannel = new SendChannel(this);
            }
            parcel.writeStrongBinder(this.mSendChannel);
            return;
        }
        parcel.writeStrongBinder(this.mReceiveChannel);
    }
}
