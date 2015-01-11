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
 * Class as a singleton
 */
public class ExpoGameDbHelper extends SQLiteAssetHelper {

    private static final String TAG = "DBHELPER";
    private static final String DATABASE_NAME = "locals.sqlite";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_MASCOTS = MascotsTable.TABLE_NAME;
    public static final String TABLE_INGREDIENTS = IngredientTable.TABLE_NAME;
    public static final String TABLE_DISHES = DishesTable.TABLE_NAME;


    private static ExpoGameDbHelper instance;

    private ExpoGameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    public static ExpoGameDbHelper getInstance(Context context){
        if(instance==null)
            instance = new ExpoGameDbHelper(context.getApplicationContext());  //avoid Activity context leakage :)
        return instance;
    }


}
