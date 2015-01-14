package it.polimi.expogame.support;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;

/**
 * Created by andrea on 13/01/15.
 */
public class ListZonesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ZoneListItem> zoneListItems;

    public ListZonesAdapter(Context context, ArrayList<ZoneListItem> zoneListItems){
        this.context = context;
        this.zoneListItems = zoneListItems;
    }

    @Override
    public int getCount() {
        return zoneListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return zoneListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_zone_list_item, null);
        }

        TextView title = (TextView)convertView.findViewById(R.id.title_zone_list_item);

        title.setText(zoneListItems.get(position).getTitle());

        return convertView;

    }
}
