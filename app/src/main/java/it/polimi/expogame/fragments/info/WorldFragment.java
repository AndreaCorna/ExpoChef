package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.ZoneActivity;
import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.GridZonesAdapter;
import it.polimi.expogame.support.GridZoneItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WorldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorldFragment extends Fragment  {


    public static final String TAG = "WorldFragment";


    //private ListView listItems;
    //private ArrayAdapter<String> listAdapterZones;
    private GridView gridZones;
    private ArrayList<GridZoneItem> listZones;
    private GridZonesAdapter adapterGridZones;


    public static WorldFragment newInstance() {
        WorldFragment fragment = new WorldFragment();
        return fragment;
    }

    public WorldFragment() {
        listZones = new ArrayList<GridZoneItem>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_world, container, false);
        /*listItems = (ListView) view.findViewById(R.id.listItem);
        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String zone = listAdapterZones.getItem(position);
                loadDishesByZone(zone);
            }
        });*/

        gridZones = (GridView)view.findViewById(R.id.grid_zone);
        gridZones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String zone = ((GridZoneItem)gridZones.getAdapter().getItem(position)).getName();
                loadDishesByZone(zone);
            }
        });


        loadZones();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * load zone with the content provider and set the list adapter of the listview
     */
    private void loadZones() {

        ArrayList<String> zoneList = new ArrayList<String>();


        Uri uri = Uri.parse(DishesProvider.CONTENT_URI+"/zones");
        String[] projection = {DishesTable.COLUMN_ZONE};
        Cursor cursor = getActivity().getContentResolver().query(uri,projection,null,null,null);
        if(cursor != null){
            Log.d(TAG,""+cursor.getCount());
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                String zone = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ZONE));
                //zoneList.add(Character.toString(zone.charAt(0)).toUpperCase()+zone.substring(1));
                listZones.add(new GridZoneItem(getActivity(),Character.toString(zone.charAt(0)).toUpperCase()+zone.substring(1),"cancel.png"));
                Log.d(TAG,zone);
                cursor.moveToNext();
            }

        }else{
            Log.d(TAG,"cursor load zones world fragment is null");

        }

        cursor.close();

        //listAdapterZones = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.zone_item, zoneList);
        //listItems.setAdapter(listAdapterZones);
        for(String name:zoneList){
            listZones.add(new GridZoneItem(getActivity(),name,"cancel.png"));
        }
        adapterGridZones = new GridZonesAdapter(getActivity(),listZones);
        gridZones.setAdapter(adapterGridZones);

    }

    /**
     * Start the activity to show the dishes of one zone. The name of zone is passde with and intent extra
     */
    private void loadDishesByZone(String zone){

        Intent intent = new Intent(getActivity().getApplicationContext(), ZoneActivity.class);
        intent.putExtra("zone",zone);
        startActivity(intent);



    }


}
