package it.polimi.expogame.fragments.info;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import it.polimi.expogame.R;
import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.Dish;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link it.polimi.expogame.fragments.info.WorldFragment.OnDishSelectedListener} interface
 * to handle interaction events.
 * Use the {@link WorldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorldFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "WorldFragment";

    private OnDishSelectedListener dishSelectedListener;

    private ListView listZones;
    private ArrayAdapter<String> listAdapterZones;

    private ListView listDishes;
    private ArrayAdapter<String> listAdapterDishes;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorldFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorldFragment newInstance(String param1, String param2) {
        WorldFragment fragment = new WorldFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WorldFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_world, container, false);
        listZones = (ListView) view.findViewById(R.id.listZone);
        listZones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String zone = listAdapterZones.getItem(position);
                TextView label = (TextView)getView().findViewById(R.id.rowTextView);
                label.setText("List dishes zone " + zone);
                listZones.setVisibility(View.INVISIBLE);
                loadDishesByZone(zone);

            }
        });
        listDishes = (ListView)view.findViewById(R.id.listDishesOfZone);
        listDishes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dishSelectedListener != null) {
                    Dish dish = new Dish();
                    dishSelectedListener.onDishSelected(dish);
                }
            }
        });
        listDishes.setVisibility(View.INVISIBLE);

        final Button goBackButton = (Button)view.findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView label = (TextView)getView().findViewById(R.id.rowTextView);
                label.setText("List Zones");
                listDishes.setVisibility(View.INVISIBLE);
                listZones.setVisibility(View.VISIBLE);
                goBackButton.setVisibility(View.INVISIBLE);
            }
        });

        loadZones();


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Dish dish) {
        if (dishSelectedListener != null) {
            dishSelectedListener.onDishSelected(dish);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dishSelectedListener = (OnDishSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnIngredientSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dishSelectedListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDishSelectedListener {
        // TODO: Update argument type and name
        public void onDishSelected(Dish dish);
    }


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
                zoneList.add(zone);
                Log.d(TAG,zone);
                cursor.moveToNext();
            }

        }else{
            Log.d(TAG,"cursor load zones world fragment is null");

        }
        listAdapterZones = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simplerow, zoneList);
        listZones.setAdapter(listAdapterZones);
    }

    private void loadDishesByZone(String zone){
        ArrayList<String> dishesList = new ArrayList<String>();

        String[] projection = {DishesTable.COLUMN_NAME};
        String selection = DishesTable.COLUMN_ZONE + " = ?";

        String[] selectionArgs = new String[]{zone};
        Log.d(TAG,selectionArgs[0].toString());
        Cursor cursor = getActivity().getContentResolver().query(DishesProvider.CONTENT_URI,projection,selection,selectionArgs,null);
        if(cursor != null){
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                String dish = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NAME));
                dishesList.add(dish);
                Log.d(TAG,dish);
                cursor.moveToNext();
            }
        }
        listAdapterDishes = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simplerow, dishesList);
        listDishes.setAdapter(listAdapterDishes);
        listDishes.setVisibility(View.VISIBLE);
        Button goBackButton = (Button)getView().findViewById(R.id.goBackButton);
        goBackButton.setVisibility(View.VISIBLE);

    }
}
