package it.polimi.expogame.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.database.ExpoGameDbHelper;

public class DishesProvider extends ContentProvider {
    private static final String TAG ="DishesProvider";
    static final String AUTHORITY = "it.polimi.expogame.dishesprovider";
    private static final String BASE_PATH = "dishes";
    private static final int DISHES = 10;
    private static final int DISH_ID = 20;
    private static final int ZONES = 30;
    private static final int INGREDIENTS = 40;


    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/dishes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/dish";
    public static final String CONTENT_ZONES = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/zones";
    public static final String CONTENT_INGREDIENTS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/ingredients";


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, DISHES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DISH_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/ingredients", INGREDIENTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/zones", ZONES);

    }

    private ExpoGameDbHelper database;


    public DishesProvider() {

    }

    @Override
    public String getType(Uri uri) {

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DISHES:
                return CONTENT_TYPE;
            case DISH_ID:
                return CONTENT_ITEM_TYPE;
            case ZONES:
                return CONTENT_ZONES;
            case INGREDIENTS:
                return CONTENT_INGREDIENTS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }

    @Override
    public boolean onCreate() {

        this.database = ExpoGameDbHelper.getInstance(this.getContext());

        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(ExpoGameDbHelper.TABLE_DISHES);
        Log.d(TAG, "After set tables");

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case DISHES:
                break;
            case DISH_ID:
                queryBuilder.appendWhere(DishesTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case ZONES:
                queryBuilder.setTables(ExpoGameDbHelper.TABLE_ZONES);
                break;
            case INGREDIENTS:
                queryBuilder.setTables(ExpoGameDbHelper.TABLE_INGREDIENTS_IN_DISHES);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = this.database.getWritableDatabase();
        Log.d(TAG, db.toString());
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {


        SQLiteDatabase db = this.database.getWritableDatabase();
        int rowsUpdated = 0;
        int uriType = sURIMatcher.match(uri);
        String databaseTable;

        switch (uriType) {
            case DISHES:
                databaseTable = database.TABLE_DISHES;
                break;
            case INGREDIENTS:
                databaseTable = database.TABLE_INGREDIENTS_IN_DISHES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        rowsUpdated = db.update(databaseTable, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;


    }


    private void checkColumns(String[] projection){
        String[] possibleColumns = {DishesTable.COLUMN_CREATED, DishesTable.COLUMN_DESCRIPTION,
                DishesTable.COLUMN_IMAGE, DishesTable.COLUMN_NAME, DishesTable.COLUMN_NATIONALITY,
                DishesTable.COLUMN_ZONE};

        if(projection != null){
            HashSet<String> requested = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> available = new HashSet<String>(Arrays.asList(possibleColumns));
            if(!available.containsAll(requested)){
                throw new IllegalArgumentException("Passed column error");
            }
        }
    }



    //----UNUSEFUL-------------------------------------------

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
