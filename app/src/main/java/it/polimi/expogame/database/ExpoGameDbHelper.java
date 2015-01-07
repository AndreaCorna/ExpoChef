package it.polimi.expogame.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorenzo on 06/01/15.
 */
public class ExpoGameDbHelper extends SQLiteAssetHelper {
    private static final String TAG = "DBHELPER";
    private static final String DATABASE_NAME = "locals";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_MASCOTS = "mascots";


    public ExpoGameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



}
