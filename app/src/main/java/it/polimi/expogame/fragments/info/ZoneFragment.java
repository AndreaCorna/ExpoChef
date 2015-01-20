package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.DetailsActivity;
import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.Dish;

/**
 * Fragment in order to show dishes of a specific zone
 */
public class ZoneFragment extends Fragment implements  AdapterView.OnItemClickListener{

    public static final String TAG = "ZoneFragment";


    private String zone;
    private ListView listItems;
    private SimpleCursorAdapter listAdapter;



    public static ZoneFragment newInstance() {
        ZoneFragment fragment = new ZoneFragment();

        return fragment;
    }

    public ZoneFragment() {
        // Required empty public constructor
    }

    public ZoneFragment(String zone) {
        this.zone = zone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_zone, container, false);
        listItems = (ListView) view.findViewById(R.id.list_dishes_zone_fragment);
        listItems.setOnItemClickListener(this);
        loadDishesByZone(zone);
        listItems.setAdapter(listAdapter);

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadDishClicked(id);
    }

    /**
     * Load dishes of the zone passed as parameter and set up of adapter for the list view
     * @param zone
     */
    private void loadDishesByZone(String zone){
        String selection = DishesTable.COLUMN_ZONE + " = ?";

        String[] selectionArgs = new String[]{zone};
        Log.d(TAG, selectionArgs[0].toString());
        Cursor cursor = getActivity().getContentResolver().query(DishesProvider.CONTENT_URI,null,selection,selectionArgs,null);

        String[] columns = new String[] { DishesTable.COLUMN_NAME, DishesTable.COLUMN_NATIONALITY };

        int[] to = new int[] { R.id.name_dish, R.id.country_dish };

        listAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.list_dishes_item,cursor,columns,to){
            //Override of method in order to disable dishes that are not created yet
            @Override
            public boolean isEnabled(int position){
                Cursor cursor = this.getCursor();
                cursor.moveToPosition(position);
                int created = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CREATED));
                if(created == 1){
                    return true;
                }else{
                    return false;
                }

            }
            /*Override this method in order to change color of row of dish*/
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                final View row = super.getView(position, convertView, parent);
                Cursor cursor = this.getCursor();
                cursor.moveToPosition(position);
                int created = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CREATED));
                if (created == 0){
                    row.setBackgroundResource(android.R.color.darker_gray);
                }
                else{
                    row.setBackgroundResource(android.R.color.background_light);
                }
                return row;
            }


        };


    }

    /**
     * load all information of the dish indicated with id and put all in the intent extras in order to open the activity
     * with all details
     * @param id
     */
    private void loadDishClicked(long id){
        Uri uri = Uri.parse(DishesProvider.CONTENT_URI+"/"+id);
        String[] projection = new String[]{};
        Cursor cursor = getActivity().getContentResolver().query(uri,projection,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NAME));
            String nationality = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NATIONALITY));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_IMAGE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_DESCRIPTION));
            String zone = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ZONE));
            int created = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CREATED));
            boolean createdDish = false;
            if(created == 1){
                createdDish = true;
            }


            Intent intent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
            intent.putExtra("idDish",id);
            intent.putExtra("nameDish",name);
            intent.putExtra("nationalityDish",nationality);
            intent.putExtra("imageUrlDish",imageUrl);
            intent.putExtra("descriptionDish",description);
            intent.putExtra("zoneDish",zone);
            intent.putExtra("createdDish",createdDish);

            startActivity(intent);
        }

    }


}
