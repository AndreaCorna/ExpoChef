package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.DetailsActivity;
import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.Dish;
import it.polimi.expogame.support.GridDishItem;
import it.polimi.expogame.support.GridDishesAdapter;

/**
 * Fragment in order to show dishes of a specific zone
 */
public class ZoneFragment extends Fragment implements  AdapterView.OnItemClickListener{

    public static final String TAG = "ZoneFragment";


    private String zone;
    //private ListView listItems;
   // private SimpleCursorAdapter listAdapter;

    private GridDishesAdapter gridAdapter;
    private ArrayList<GridDishItem> gridDishItems;
    private GridView gridView;


    public static ZoneFragment newInstance() {
        ZoneFragment fragment = new ZoneFragment();

        return fragment;
    }

    public ZoneFragment() {
        gridDishItems = new ArrayList<GridDishItem>();
    }

    public ZoneFragment(String zone) {

        this.zone = zone.toLowerCase();
        gridDishItems = new ArrayList<GridDishItem>();
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
        //listItems = (ListView) view.findViewById(R.id.list_dishes_zone_fragment);
        //listItems.setOnItemClickListener(this);
        gridView = (GridView)view.findViewById(R.id.grid_dish);
        gridView.setOnItemClickListener(this);
        loadDishesByZone(zone);
        //listItems.setAdapter(listAdapter);

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
        if(((GridDishItem)gridView.getAdapter().getItem(position)).isCreated()){
            loadDishClicked(id);

        }
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

        String[] columns = new String[] { DishesTable.COLUMN_ID,DishesTable.COLUMN_NAME, DishesTable.COLUMN_CREATED ,DishesTable.COLUMN_IMAGE};

        int[] to = new int[] { R.id.name_dish };
        if(cursor != null){
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NAME));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_IMAGE));
                int created = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CREATED));
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ID));
                boolean isCreated = false;
                if(created == 1){
                    isCreated = true;
                }
                Log.d("CREATE",name + " "+ imageUrl);
                gridDishItems.add(new GridDishItem(getActivity(),id,name,imageUrl,isCreated));
                cursor.moveToNext();
            }
        }
        gridAdapter = new GridDishesAdapter(getActivity().getApplicationContext(),gridDishItems);
        gridView.setAdapter(gridAdapter);

        /*listAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.list_dishes_item,cursor,columns,to){
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
            /*@Override
            public View getView(int position, View convertView, ViewGroup parent){
                final View row = super.getView(position, convertView, parent);
                Cursor cursor = this.getCursor();
                cursor.moveToPosition(position);
                //check is dish is unlocked
                int created = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CREATED));
                if (created == 0){
                    row.setBackgroundResource(android.R.color.darker_gray);
                }

                //load image of dish
                Context context = getActivity().getApplicationContext();

                String image = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_IMAGE));
                int index = image.indexOf(".");
                String imageUrl = null;
                //delete extension of file from name if exist
                if (index > 0)
                    imageUrl = image.substring(0, index);
                //get the id
                int id = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
                if(id==0){
                    id = -1;
                }
                //load image id
                ImageView imageView = (ImageView)row.findViewById(R.id.image_dish);
                imageView.setImageResource(id);
                return row;
            }


        };*/


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
