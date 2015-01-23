package it.polimi.expogame.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import it.polimi.expogame.R;


/**
 * Adapter for load image in the grid view
 */
public class ImageAdapter extends BaseAdapter {
    protected Context mContext;
    protected LayoutInflater inflater;


    // references to our ingredients
    protected ArrayList<Ingredient> ingredients;


    public ImageAdapter(Context c, ArrayList<Ingredient> ingredients) {
        inflater = LayoutInflater.from(c);
        mContext = c;
        this.ingredients = ingredients;




    }


    public int getCount() {
        return ingredients.size();
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
        View v = convertView;
        ImageView picture;
        TextView name;

        if (v == null) {  // if it's not recycled, initialize some attributes
            v = inflater.inflate(R.layout.grid_item, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));  //adding tag to the view elements
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }
        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        Ingredient ingredient = ingredients.get(position);

        //setting the view elements with the actual content on the ingredient at position: "position"
        picture.setImageResource(ingredient.getDrawableImage());
        name.setText(ingredient.getName());


        return v;
    }


}