package android.database;

import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class CursorJoiner implements Iterator<Result>, Iterable<Result> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private int[] mColumnsLeft;
    private int[] mColumnsRight;
    private Result mCompareResult;
    private boolean mCompareResultIsValid;
    private Cursor mCursorLeft;
    private Cursor mCursorRight;
    private String[] mValues;

    public enum Result {
        RIGHT,
        LEFT,
        BOTH
    }

    @Override // java.lang.Iterable
    public Iterator<Result> iterator() {
        return this;
    }

    public CursorJoiner(Cursor cursor, String[] strArr, Cursor cursor2, String[] strArr2) {
        if (strArr.length != strArr2.length) {
            throw new IllegalArgumentException("you must have the same number of columns on the left and right, " + strArr.length + " != " + strArr2.length);
        }
        this.mCursorLeft = cursor;
        this.mCursorRight = cursor2;
        cursor.moveToFirst();
        this.mCursorRight.moveToFirst();
        this.mCompareResultIsValid = false;
        this.mColumnsLeft = buildColumnIndiciesArray(cursor, strArr);
        this.mColumnsRight = buildColumnIndiciesArray(cursor2, strArr2);
        this.mValues = new String[this.mColumnsLeft.length * 2];
    }

    private int[] buildColumnIndiciesArray(Cursor cursor, String[] strArr) {
        int[] iArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            iArr[i] = cursor.getColumnIndexOrThrow(strArr[i]);
        }
        return iArr;
    }

    /* JADX INFO: renamed from: android.database.CursorJoiner$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$database$CursorJoiner$Result;

        static {
            int[] iArr = new int[Result.values().length];
            $SwitchMap$android$database$CursorJoiner$Result = iArr;
            try {
                iArr[Result.BOTH.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[Result.LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$database$CursorJoiner$Result[Result.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (!this.mCompareResultIsValid) {
            return (this.mCursorLeft.isAfterLast() && this.mCursorRight.isAfterLast()) ? false : true;
        }
        int i = AnonymousClass1.$SwitchMap$android$database$CursorJoiner$Result[this.mCompareResult.ordinal()];
        if (i == 1) {
            return (this.mCursorLeft.isLast() && this.mCursorRight.isLast()) ? false : true;
        }
        if (i == 2) {
            return (this.mCursorLeft.isLast() && this.mCursorRight.isAfterLast()) ? false : true;
        }
        if (i == 3) {
            return (this.mCursorLeft.isAfterLast() && this.mCursorRight.isLast()) ? false : true;
        }
        throw new IllegalStateException("bad value for mCompareResult, " + this.mCompareResult);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public Result next() {
        if (!hasNext()) {
            throw new IllegalStateException("you must only call next() when hasNext() is true");
        }
        incrementCursors();
        boolean z = !this.mCursorLeft.isAfterLast();
        boolean z2 = !this.mCursorRight.isAfterLast();
        if (z && z2) {
            populateValues(this.mValues, this.mCursorLeft, this.mColumnsLeft, 0);
            populateValues(this.mValues, this.mCursorRight, this.mColumnsRight, 1);
            int iCompareStrings = compareStrings(this.mValues);
            if (iCompareStrings == -1) {
                this.mCompareResult = Result.LEFT;
            } else if (iCompareStrings == 0) {
                this.mCompareResult = Result.BOTH;
            } else if (iCompareStrings == 1) {
                this.mCompareResult = Result.RIGHT;
            }
        } else if (z) {
            this.mCompareResult = Result.LEFT;
        } else {
            this.mCompareResult = Result.RIGHT;
        }
        this.mCompareResultIsValid = true;
        return this.mCompareResult;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    private static void populateValues(String[] strArr, Cursor cursor, int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            strArr[(i2 * 2) + i] = cursor.getString(iArr[i2]);
        }
    }

    private void incrementCursors() {
        if (this.mCompareResultIsValid) {
            int i = AnonymousClass1.$SwitchMap$android$database$CursorJoiner$Result[this.mCompareResult.ordinal()];
            if (i == 1) {
                this.mCursorLeft.moveToNext();
                this.mCursorRight.moveToNext();
            } else if (i == 2) {
                this.mCursorLeft.moveToNext();
            } else if (i == 3) {
                this.mCursorRight.moveToNext();
            }
            this.mCompareResultIsValid = false;
        }
    }

    private static int compareStrings(String... strArr) {
        if (strArr.length % 2 != 0) {
            throw new IllegalArgumentException("you must specify an even number of values");
        }
        for (int i = 0; i < strArr.length; i += 2) {
            if (strArr[i] == null) {
                if (strArr[i + 1] != null) {
                    return -1;
                }
            } else {
                int i2 = i + 1;
                if (strArr[i2] == null) {
                    return 1;
                }
                int iCompareTo = strArr[i].compareTo(strArr[i2]);
                if (iCompareTo != 0) {
                    return iCompareTo < 0 ? -1 : 1;
                }
            }
        }
        return 0;
    }
}
