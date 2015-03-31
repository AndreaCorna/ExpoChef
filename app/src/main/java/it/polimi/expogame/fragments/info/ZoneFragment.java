package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.DetailsActivity;
import it.polimi.expogame.activities.MainActivity;
import it.polimi.expogame.database.tables.DishesTable;
import it.polimi.expogame.database.tables.IngredientTable;
import it.polimi.expogame.database.tables.IngredientsInDishes;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.TutorialAnimationManager;
import it.polimi.expogame.support.adapters.GridDishItem;
import it.polimi.expogame.support.adapters.GridDishesAdapter;
import it.polimi.expogame.database.objects.Hint;

/**
 * Fragment in order to show dishes of a specific zone
 */
public class ZoneFragment extends Fragment implements  AdapterView.OnItemClickListener{

    public static final String TAG = "ZoneFragment";


    private String zone;
    private GridDishesAdapter gridAdapter;
    private ArrayList<GridDishItem> gridDishItems;
    private GridView gridView;
    private ArrayList<Hint> hintIngredients;

    private ImageView cookerFish;
    private TextView textSpeakMascotte;
    private FrameLayout frameLayout;
    private ArrayList<String> tutorialStrings;


    public static ZoneFragment newInstance() {
        ZoneFragment fragment = new ZoneFragment();

        return fragment;
    }

    public ZoneFragment() {

        gridDishItems = new ArrayList<GridDishItem>();
        hintIngredients = new ArrayList<Hint>();
    }

    public ZoneFragment(String zone) {

        this.zone = zone.toLowerCase();
        gridDishItems = new ArrayList<GridDishItem>();
        hintIngredients = new ArrayList<Hint>();
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
        gridView = (GridView)view.findViewById(R.id.grid_dish);
        gridView.setOnItemClickListener(this);
        loadDishesByZone(zone);
        frameLayout = (FrameLayout)view.findViewById(R.id.zone_fragment_layout);
        textSpeakMascotte = (TextView)view.findViewById(R.id.speak_tutorial_zone);
        cookerFish = (ImageView)view.findViewById(R.id.cooker_image);
        SharedPreferences prefs = getActivity().getSharedPreferences("expochef", Context.MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("firstTimeZone",true);
        if(isFirstTime){
           startAnimation();
            prefs.edit().putBoolean("firstTimeZone",false).commit();

        }

        return view;
    }

    private void startAnimation(){
        //cookerFish = new ImageView(getActivity().getApplicationContext());
        textSpeakMascotte.setText(R.string.start_text_tutorial_zone);
        cookerFish.setImageDrawable(getResources().getDrawable(R.drawable.cooker));
        cookerFish.setVisibility(View.INVISIBLE);
        loadTutorialStrings();
        TutorialAnimationManager manager = new TutorialAnimationManager(textSpeakMascotte, cookerFish,getDimensionScreen(), tutorialStrings);
        manager.startEnterAnimation();
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

    private void showHintDialog(GridDishItem dish) {
        HintFragmentDialog hintFragmentDialog = new HintFragmentDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("hints", hintIngredients);
        args.putString("dish_name", dish.getName());
        args.putInt("dish_image",dish.getImageId());

        hintFragmentDialog.setArguments(args);


        hintFragmentDialog.show(getActivity().getSupportFragmentManager(), "fragment_hint_dialog");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(((GridDishItem)gridView.getAdapter().getItem(position)).isCreated()){
            loadDishClicked(id);

        }else{
            hintIngredients.clear();
            LoadHints(((GridDishItem)gridView.getAdapter().getItem(position)).getName());
            showHintDialog((GridDishItem)gridView.getAdapter().getItem(position));
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

            cursor.close();
        }
        gridAdapter = new GridDishesAdapter(getActivity().getApplicationContext(),gridDishItems);
        gridView.setAdapter(gridAdapter);



    }

    /**
     * load all information of the dish indicated with id and put all in the intent extras in order to open the activity
     * with all details
     * @param id
     */
    private void loadDishClicked(long id){
        Uri uri = Uri.parse(DishesProvider.CONTENT_URI+"/"+id);
        Cursor cursor = getActivity().getContentResolver().query(uri,new String[]{},null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            String id_dish = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NAME));
            String nationality = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_NATIONALITY));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_IMAGE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_DESCRIPTION));
            String zone = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ZONE));
            String curiosity = cursor.getString(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_CURIOSITY));
            int difficulty = cursor.getInt(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_DIFFICULTY));
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
            intent.putExtra("curiosityDish",curiosity);
            intent.putExtra("difficultyDish",difficulty);

            startActivity(intent);
        }

    }

    //load ingredients for hint
    private void LoadHints(String name){

        Uri uri = Uri.parse(DishesProvider.CONTENT_URI+"/ingredients");
        String selection = IngredientsInDishes.COLUMN_ID_DISH + " = ?";

        String[] selectionArgs = new String[]{name};

        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,selectionArgs,null);

        if(cursor != null){
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){
                String ingredient = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsInDishes.COLUMN_ID_INGREDIENT));
                int hintGivenField = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientsInDishes.COLUMN_HINT_GIVEN));
                boolean hintGiven = false;
                if(hintGivenField == 1){
                    hintGiven = true;
                }
                loadDetailsIngredient(ingredient, hintGiven);
                cursor.moveToNext();

            }

            cursor.close();
        }

    }

    //load details of the single ingredient
    private void loadDetailsIngredient(String name,boolean hintGiven){
        String selection = IngredientTable.COLUMN_NAME + " = ?";

        String[] selectionArgs = new String[]{name};

        Cursor cursor = getActivity().getContentResolver().query(IngredientsProvider.CONTENT_URI,null,selection,selectionArgs,null);

        if (cursor != null){
            cursor.moveToFirst();
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_IMAGEURL));

            Hint hint = new Hint(getActivity().getApplicationContext(),name,imageUrl,hintGiven);
            hintIngredients.add(hint);
            Log.d("MAIN","Ho caricato "+name);

        }
        cursor.close();
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
        String[] parts = getActivity().getResources().getStringArray(R.array.tutorial_text_zone);
        for(String item:parts){
            tutorialStrings.add(item);
        }


    }


}
