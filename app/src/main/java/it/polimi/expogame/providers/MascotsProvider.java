package it.polimi.expogame.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.Arrays;
import java.util.HashSet;

import it.polimi.expogame.database.ExpoGameDbHelper;
import it.polimi.expogame.database.tables.MascotsTable;

public class MascotsProvider extends ContentProvider {

    public static final String AUTHORITY = "it.polimi.expogame.mascotsprovider";
    private static final String BASE_PATH = "mascots";
    private static final int MASCOTS = 10;
    private static final int MASCOT_ID = 20;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mascots";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mascot";


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, MASCOTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MASCOT_ID);
    }

    private ExpoGameDbHelper database;


    public MascotsProvider() {
    }



    @Override
    public String getType(Uri uri) {

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MASCOTS:
                return CONTENT_TYPE;
            case MASCOT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {

        this.database = ExpoGameDbHelper.getInstance(this.getContext());

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(database.TABLE_MASCOTS);

        SQLiteDatabase db = this.database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = this.database.getWritableDatabase();
        int rowsUpdated = 0;
        rowsUpdated = db.update(database.TABLE_MASCOTS, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;

    }



    //---UTILITY------------------------------------------------------

    /**
     * Method exploited to check if all the requested columns exists
     * in the table
     * @param projection are the columns that the client want to retreive
     */
    private void checkColumns(String[] projection) {

        String[] available = { MascotsTable.COLUMN_NAME,  MascotsTable.COLUMN_LATITUDE, MascotsTable.COLUMN_LONGITUDE, MascotsTable.COLUMN_CATEGORY , MascotsTable.COLUMN_CAPTURED, MascotsTable.COLUMN_MODEL};

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    //-----------------------------------------------------------------


    //---UNUSEFUL---------------------------------------------------------

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //----------------------------------------------------------------------

}
