package it.polimi.expogame.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;

/**
 * Adapter for grid of zones in world fragment
 */
public class GridZonesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GridZoneItem> gridZoneItems;

    public GridZonesAdapter(Context context, ArrayList<GridZoneItem> gridZoneItems){
        this.context = context;
        this.gridZoneItems = gridZoneItems;
    }

    @Override
    public int getCount() {
        return gridZoneItems.size();
    }

    @Override
    public Object getItem(int position) {
        return gridZoneItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.zone_grid, parent, false);
        }

        TextView title = (TextView)convertView.findViewById(R.id.text_zone);

        title.setText(gridZoneItems.get(position).getTranslation());
        ImageView image = (ImageView)convertView.findViewById(R.id.picture_zone);
        image.setImageResource(gridZoneItems.get(position).getIdDrawable());

        return convertView;

    }
}
