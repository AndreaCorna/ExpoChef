package it.polimi.expogame.support;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.database.IngredientTable;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.Ingredient;

/**
 * Adapter for load image in the grid view
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;


    // references to our images
    private ArrayList<Integer> mThumbIds;
    private ArrayList<Ingredient> ingredients;


    public ImageAdapter(Context c) {
        mContext = c;
        mThumbIds = new ArrayList<Integer>();
        ingredients = new ArrayList<Ingredient>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(IngredientsProvider.CONTENT_URI,
                new String[]{},
                null,
                null,
                null);

        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_NAME));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_IMAGEURL));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_CATEGORY));
            int unlocked = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_UNLOCKED));
            boolean unblocked;
            if(unlocked == 0){
                unblocked = false;
            }else{
                unblocked = true;
            }
            Ingredient ingredient = new Ingredient(name,imageUrl,category,unblocked);
            ingredients.add(ingredient);
            int index = imageUrl.indexOf(".");
            String urlImage = null;
            //delete extension of file from name if exist
            if (index > 0)
                urlImage = imageUrl.substring(0, index);
            //get the id
            int id = mContext.getResources().getIdentifier(urlImage, "drawable", mContext.getPackageName());
            if (id == 0){
                id = R.drawable.ic_launcher;
            }
            mThumbIds.add(id);


        }

        cursor.close();

    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return ingredients.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds.get(position).intValue());

        return imageView;
    }


}