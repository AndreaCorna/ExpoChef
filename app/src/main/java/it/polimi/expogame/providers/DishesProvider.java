package it.polimi.expogame.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import it.polimi.expogame.database.ExpoGameDbHelper;

public class DishesProvider extends ContentProvider {

    static final String AUTHORITY = "it.polimi.expogame.dishesprovider";
    private static final String BASE_PATH = "dishes";
    private static final int DISHES = 10;
    private static final int DISH_ID = 20;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/dishes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/dish";


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, DISHES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DISH_ID);
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
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
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
