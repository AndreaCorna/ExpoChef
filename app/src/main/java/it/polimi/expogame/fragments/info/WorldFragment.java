package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.ZoneActivity;
import it.polimi.expogame.database.tables.ZonesTable;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.TutorialAnimationManager;
import it.polimi.expogame.support.adapters.GridZonesAdapter;
import it.polimi.expogame.support.adapters.GridZoneItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WorldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorldFragment extends Fragment  {


    public static final String TAG = "WorldFragment";


    private GridView gridZones;
    private ArrayList<GridZoneItem> listZones;
    private GridZonesAdapter adapterGridZones;

    private ImageView cookerFish;
    private TranslateAnimation enterAnimation;
    private TextView textSpeakMascotte;
    private FrameLayout frameLayout;
    private ArrayList<String> tutorialStrings;


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


        gridZones = (GridView)view.findViewById(R.id.grid_zone);
        gridZones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String zone = ((GridZoneItem)gridZones.getAdapter().getItem(position)).getName();
                String translation = ((GridZoneItem)gridZones.getAdapter().getItem(position)).getTranslation();
                loadDishesByZone(zone,translation);
            }
        });


        loadZones();

        textSpeakMascotte = (TextView)view.findViewById(R.id.speak_tutorial_world);

        frameLayout = (FrameLayout)view.findViewById(R.id.layout_world_fragment);


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

    public void startAnimation(){

        cookerFish = new ImageView(getActivity().getApplicationContext());
        cookerFish.setImageDrawable(getResources().getDrawable(R.drawable.cooker));
        cookerFish.setVisibility(View.INVISIBLE);
        loadTutorialStrings();
        TutorialAnimationManager manager = new TutorialAnimationManager(textSpeakMascotte, cookerFish,getDimensionScreen(), frameLayout, tutorialStrings);
        manager.startEnterAnimation();
    }

    /**
     * load zone with the content provider and set the list adapter of the listview
     */
    private void loadZones() {


        Uri uri = Uri.parse(DishesProvider.CONTENT_URI+"/zones");
        String[] projection = {ZonesTable.COLUMN_ZONE,ZonesTable.COLUMN_IMAGE};
        Cursor cursor = getActivity().getContentResolver().query(uri,projection,null,null,null);
        if(cursor != null){
            Log.d(TAG,""+cursor.getCount());
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                String zone = cursor.getString(cursor.getColumnIndexOrThrow(ZonesTable.COLUMN_ZONE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(ZonesTable.COLUMN_IMAGE));
                listZones.add(new GridZoneItem(getActivity(),zone,image));
                Log.d(TAG,zone);
                cursor.moveToNext();
            }

        }else{
            Log.d(TAG,"cursor load zones world fragment is null");

        }

        cursor.close();


        adapterGridZones = new GridZonesAdapter(getActivity(),listZones);
        gridZones.setAdapter(adapterGridZones);

    }

    /**
     * Start the activity to show the dishes of one zone. The name of zone is passde with and intent extra
     */
    private void loadDishesByZone(String zone, String translation){

        Intent intent = new Intent(getActivity().getApplicationContext(), ZoneActivity.class);
        intent.putExtra("zone",zone);
        intent.putExtra("translation",translation);
        startActivity(intent);



    }


    /**
     * get the dimension of the scree
     * @return point with dimension
     */
    private Point getDimensionScreen(){
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * load text for tutorial
     */
    private void loadTutorialStrings(){

        tutorialStrings = new ArrayList<String>();
        String[] parts = getActivity().getResources().getStringArray(R.array.tutorial_text_world);
        for(String item:parts){
            tutorialStrings.add(item);
        }


    }





}
