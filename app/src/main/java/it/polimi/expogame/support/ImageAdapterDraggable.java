package it.polimi.expogame.support;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;

/**
 * Created by andrea on 23/01/15.
 */
public class ImageAdapterDraggable extends BaseAdapter {

    public ImageAdapterDraggable(Context c, ArrayList<Ingredient> ingredients) {

        inflater = LayoutInflater.from(c);
        mContext = c;
        this.ingredients = ingredients;

    }

    protected Context mContext;
    protected LayoutInflater inflater;


    // references to our ingredients
    protected ArrayList<Ingredient> ingredients;


    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
        notifyDataSetChanged();
    }

    public void removeIngredient(Ingredient ingredient){
        ingredients.remove(ingredient);
        notifyDataSetChanged();
    }

    public void setIngredients(ArrayList<Ingredient> ingredients){
        this.ingredients = ingredients;
    }

    public int getCount() {
        return ingredients.size();
    }

    public Object getItem(int position) {
        return ingredients.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View v = convertView;
        ImageView picture;
        TextView name;
        ViewHolder vh = null;
        if (v == null) {  // if it's not recycled, initialize some attributes
            v = inflater.inflate(R.layout.grid_item, parent, false);
            Log.d("GETVIEW",ingredients.get(position).getName()+ " "+position);

            //object tag in order to keep track of all information of item view
            vh = new ViewHolder();
            vh.setPicture((SquareImageView)v.findViewById(R.id.picture));
            vh.setText((TextView)v.findViewById(R.id.text));
            vh.setIngredient(ingredients.get(position));
            v.setTag(vh);
        }
        else{
            //set holder information with previous setted
            vh = (ViewHolder)v.getTag();
        }
        picture = (ImageView)v.findViewById(R.id.picture);
        name = (TextView)v.findViewById(R.id.text);


        //setting the view elements with the actual content on the ingredient in holder object
        //in this way not a problem if change order of arraylist
        picture.setImageResource(vh.getIngredient().getDrawableImage());
        name.setText(vh.getIngredient().getName());
        v.setOnTouchListener(new MyTouchListener());

        return v;
    }




    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view,0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

}
