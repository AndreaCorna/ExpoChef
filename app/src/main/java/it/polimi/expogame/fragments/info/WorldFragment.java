package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
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
public class WorldFragment extends Fragment implements ListView.OnItemClickListener {
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
    private ArrayAdapter<String> listAdapter;


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
        listZones.setOnItemClickListener(this);
        Button buttonInfo = (Button) view.findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dishSelectedListener != null) {
                    dishSelectedListener.onDishSelected(new Dish());
                }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (dishSelectedListener != null) {
            //TODO retrieve dish information
            Dish dish = new Dish();
            dishSelectedListener.onDishSelected(dish);
        }
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
        listAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.simplerow, zoneList);
        listZones.setAdapter(listAdapter);
    }

}
