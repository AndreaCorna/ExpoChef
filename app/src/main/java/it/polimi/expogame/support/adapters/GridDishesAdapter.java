package it.polimi.expogame.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;

/**
 * Created by andrea on 25/01/15.
 */
public class GridDishesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GridDishItem> dishesItem;

    public GridDishesAdapter(Context context, ArrayList<GridDishItem> dishesItem){
        this.context = context;
        this.dishesItem = dishesItem;
    }

    @Override
    public int getCount() {
        return dishesItem.size();
    }

    @Override
    public Object getItem(int position) {
        return dishesItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dishesItem.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.zone_grid, parent, false);

        }
        convertView.setEnabled(dishesItem.get(position).isCreated());
        TextView title = (TextView)convertView.findViewById(R.id.text_zone);

        title.setText(dishesItem.get(position).getName());
        ImageView image = (ImageView)convertView.findViewById(R.id.picture_zone);
        image.setImageResource(dishesItem.get(position).getImageId());

        RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.grid_zone_item_layout);
        if(!dishesItem.get(position).isCreated()){
            layout.setAlpha((float)0.5);
            layout.setEnabled(false);
        }


        return convertView;
    }



}
