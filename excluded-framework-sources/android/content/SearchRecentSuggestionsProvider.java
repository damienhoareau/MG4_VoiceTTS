package android.content;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class SearchRecentSuggestionsProvider extends ContentProvider {
    public static final int DATABASE_MODE_2LINES = 2;
    public static final int DATABASE_MODE_QUERIES = 1;
    private static final int DATABASE_VERSION = 512;
    private static final String NULL_COLUMN = "query";
    private static final String ORDER_BY = "date DESC";
    private static final String TAG = "SuggestionsProvider";
    private static final int URI_MATCH_SUGGEST = 1;
    private static final String sDatabaseName = "suggestions.db";
    private static final String sSuggestions = "suggestions";
    private String mAuthority;
    private int mMode;
    private SQLiteOpenHelper mOpenHelper;
    private String mSuggestSuggestionClause;
    private String[] mSuggestionProjection;
    private Uri mSuggestionsUri;
    private boolean mTwoLineDisplay;
    private UriMatcher mUriMatcher;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private int mNewVersion;

        public DatabaseHelper(Context context, int i) {
            super(context, SearchRecentSuggestionsProvider.sDatabaseName, (SQLiteDatabase.CursorFactory) null, i);
            this.mNewVersion = i;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE suggestions (_id INTEGER PRIMARY KEY,display1 TEXT UNIQUE ON CONFLICT REPLACE");
            if ((this.mNewVersion & 2) != 0) {
                sb.append(",display2 TEXT");
            }
            sb.append(",query TEXT,date LONG);");
            sQLiteDatabase.execSQL(sb.toString());
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Log.w(SearchRecentSuggestionsProvider.TAG, "Upgrading database from version " + i + " to " + i2 + ", which will destroy all old data");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS suggestions");
            onCreate(sQLiteDatabase);
        }
    }

    protected void setupSuggestions(String str, int i) {
        if (TextUtils.isEmpty(str) || (i & 1) == 0) {
            throw new IllegalArgumentException();
        }
        this.mTwoLineDisplay = (i & 2) != 0;
        this.mAuthority = new String(str);
        this.mMode = i;
        this.mSuggestionsUri = Uri.parse("content://" + this.mAuthority + "/suggestions");
        UriMatcher uriMatcher = new UriMatcher(-1);
        this.mUriMatcher = uriMatcher;
        uriMatcher.addURI(this.mAuthority, SearchManager.SUGGEST_URI_PATH_QUERY, 1);
        if (this.mTwoLineDisplay) {
            this.mSuggestSuggestionClause = "display1 LIKE ? OR display2 LIKE ?";
            this.mSuggestionProjection = new String[]{"0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "display2 AS suggest_text_2", "query AS suggest_intent_query", "_id"};
        } else {
            this.mSuggestSuggestionClause = "display1 LIKE ?";
            this.mSuggestionProjection = new String[]{"0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "query AS suggest_intent_query", "_id"};
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (uri.getPathSegments().size() != 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        if (uri.getPathSegments().get(0).equals("suggestions")) {
            int iDelete = writableDatabase.delete("suggestions", str, strArr);
            getContext().getContentResolver().notifyChange(uri, null);
            return iDelete;
        }
        throw new IllegalArgumentException("Unknown Uri");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (this.mUriMatcher.match(uri) == 1) {
            return SearchManager.SUGGEST_MIME_TYPE;
        }
        int size = uri.getPathSegments().size();
        if (size >= 1 && uri.getPathSegments().get(0).equals("suggestions")) {
            if (size == 1) {
                return "vnd.android.cursor.dir/suggestion";
            }
            if (size == 2) {
                return "vnd.android.cursor.item/suggestion";
            }
        }
        throw new IllegalArgumentException("Unknown Uri");
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0044 A[PHI: r4
  0x0044: PHI (r4v1 long) = (r4v0 long), (r4v0 long), (r4v3 long) binds: [B:5:0x002a, B:6:0x002c, B:8:0x0037] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri uriWithAppendedPath;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        int size = uri.getPathSegments().size();
        if (size < 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        long jInsert = -1;
        if (uri.getPathSegments().get(0).equals("suggestions") && size == 1) {
            jInsert = writableDatabase.insert("suggestions", "query", contentValues);
            if (jInsert > 0) {
                uriWithAppendedPath = Uri.withAppendedPath(this.mSuggestionsUri, String.valueOf(jInsert));
            } else {
                uriWithAppendedPath = null;
            }
        } else {
            uriWithAppendedPath = null;
        }
        if (jInsert < 0) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(uriWithAppendedPath, null);
        return uriWithAppendedPath;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        int i;
        if (this.mAuthority == null || (i = this.mMode) == 0) {
            throw new IllegalArgumentException("Provider not configured");
        }
        this.mOpenHelper = new DatabaseHelper(getContext(), i + 512);
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3;
        String[] strArr3;
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        String[] strArr4 = null;
        if (this.mUriMatcher.match(uri) == 1) {
            if (TextUtils.isEmpty(strArr2[0])) {
                str3 = null;
                strArr3 = null;
            } else {
                String str4 = "%" + strArr2[0] + "%";
                String[] strArr5 = this.mTwoLineDisplay ? new String[]{str4, str4} : new String[]{str4};
                str3 = this.mSuggestSuggestionClause;
                strArr3 = strArr5;
            }
            Cursor cursorQuery = readableDatabase.query("suggestions", this.mSuggestionProjection, str3, strArr3, null, null, "date DESC", null);
            cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
            return cursorQuery;
        }
        int size = uri.getPathSegments().size();
        if (size != 1 && size != 2) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        String str5 = uri.getPathSegments().get(0);
        if (!str5.equals("suggestions")) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        if (strArr != null && strArr.length > 0) {
            strArr4 = new String[strArr.length + 1];
            System.arraycopy(strArr, 0, strArr4, 0, strArr.length);
            strArr4[strArr.length] = "_id AS _id";
        }
        String[] strArr6 = strArr4;
        StringBuilder sb = new StringBuilder(256);
        if (size == 2) {
            sb.append("(_id = ");
            sb.append(uri.getPathSegments().get(1));
            sb.append(")");
        }
        if (str != null && str.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append('(');
            sb.append(str);
            sb.append(')');
        }
        Cursor cursorQuery2 = readableDatabase.query(str5, strArr6, sb.toString(), strArr2, null, null, str2, null);
        cursorQuery2.setNotificationUri(getContext().getContentResolver(), uri);
        return cursorQuery2;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
