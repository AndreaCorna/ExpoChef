package it.polimi.expogame.support;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.polimi.expogame.R;


/**
 * Adapter for load image in the grid view
 */
public class ImageAdapter extends BaseAdapter {
    protected Context mContext;
    protected LayoutInflater inflater;


    // references to our ingredients
    protected ArrayList<Ingredient> ingredients;
    //hash table in order to update correctly view of gridview on scrolling
    private HashMap<String,Boolean> selectedHashTable;

    public ImageAdapter(Context c, ArrayList<Ingredient> ingredients) {
        inflater = LayoutInflater.from(c);
        mContext = c;
        this.ingredients = ingredients;
        selectedHashTable = new HashMap<>();
        for(Ingredient ingredient:ingredients){
           selectedHashTable.put(ingredient.getName(),new Boolean(false));
        }



    }

    public void setIngredients(ArrayList<Ingredient> ingredients){
        this.ingredients = ingredients;
        selectedHashTable.clear();
        for(Ingredient ingredient:ingredients){
            selectedHashTable.put(ingredient.getName(),new Boolean(false));
        }
    }

    public void setSelected(String name,boolean isSelected){
        selectedHashTable.put(name,new Boolean(isSelected));
    }

    public void resetAllSelection(){
        for(String key:selectedHashTable.keySet()){
            selectedHashTable.put(key,new Boolean(false));
        }
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
        if(selectedHashTable.get(ingredient.getName())){
            v.setBackgroundColor(Color.LTGRAY);

        }else{
            v.setBackgroundColor(303030);

        }


        return v;
    }


}