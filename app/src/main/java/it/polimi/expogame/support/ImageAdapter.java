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
    private Context mContext;
    private LayoutInflater inflater;


    // references to our images
    private ArrayList<Integer> mThumbIds;
    private ArrayList<Ingredient> ingredients;


    public ImageAdapter(Context c, ArrayList<Ingredient> ingredients) {
        inflater = LayoutInflater.from(c);
        mContext = c;
        mThumbIds = new ArrayList<Integer>();
        this.ingredients = ingredients;
        for (Ingredient ingredient : ingredients) {

            String imageUrl = ingredient.getImageUrl();
            //Retreive the image of the ingredient and add their id
            //to the mThumbIds array

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



    }

    @Override
    public void notifyDataSetChanged (){
        mThumbIds.clear();
        for (Ingredient ingredient : ingredients) {

            String imageUrl = ingredient.getImageUrl();
            //Retreive the image of the ingredient and add their id
            //to the mThumbIds array

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

        super.notifyDataSetChanged();

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
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }
        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        Ingredient ingredient = ingredients.get(position);
        int  image = mThumbIds.get(position);

        picture.setImageResource(image);
        name.setText(ingredient.getName());

      //  imageView.setImageResource(mThumbIds.get(position).intValue());

        return v;
    }


}