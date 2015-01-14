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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ZoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneFragment extends Fragment implements  AdapterView.OnItemClickListener{

    public static final String TAG = "ZoneFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String zone;
    private ListView listItems;
    private SimpleCursorAdapter listAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoneFragment newInstance(String param1, String param2) {
        ZoneFragment fragment = new ZoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_zone, container, false);
        listItems = (ListView) view.findViewById(R.id.list_dishes_zone_fragment);
        listItems.setOnItemClickListener(this);
        loadDishesByZone(zone);
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
        };
        listItems.setAdapter(listAdapter);
    }

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

            //dishSelectedListener.onDishSelected(new Dish(id,name,nationality,imageUrl,description,zone,createdDish));

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
