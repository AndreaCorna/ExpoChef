package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.database.objects.Ingredient;
import it.polimi.expogame.database.tables.IngredientTable;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.adapters.ImageAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link it.polimi.expogame.fragments.cook.IngredientFragment.OnIngredientSelectedListener} interface
 * to handle interaction events.
 * Use the {@link IngredientFragment} factory method to
 * create an instance of this fragment.
 */
public class IngredientFragment extends Fragment {

    private GridView gridview;
    private LinearLayout linearLayout;
    private ArrayList<Ingredient> listIngredientsSelected;
    private ImageAdapter imageAdapter;
    private ArrayList<Ingredient> ingredientsUnlocked;



    private OnIngredientSelectedListener onIngredientSelectedListener;



    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting reference of the ViewPager element in the view
        ingredientsUnlocked = new ArrayList<Ingredient>();
        Log.d("Ingredients","Called onCreate");






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ricettario_layout, container, false);

        linearLayout = (LinearLayout)view.findViewById(R.id.ingredients_layout);
        gridview = (GridView) view.findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //override in order to change background of an item when is selected and add/remove it from the
            //arraylist we want to pass to cook fragment
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Ingredient ingredient = (Ingredient)gridview.getAdapter().getItem(position);
                if(!listIngredientsSelected.contains(ingredient)){
                    view.setBackgroundColor(Color.LTGRAY);
                    listIngredientsSelected.add(ingredient);
                    onIngredientSelectedListener.addIngredientSelected(ingredient);
                    ((ImageAdapter)gridview.getAdapter()).setSelected(ingredient.getName(),true);
                }else{
                    view.setBackgroundColor(303030);
                    onIngredientSelectedListener.removeIngredient(ingredient);
                    listIngredientsSelected.remove(ingredient);
                    ((ImageAdapter)gridview.getAdapter()).setSelected(ingredient.getName(),false);

                }
            }
        });
        //loading unlocked ingredients in the Cook Options Fragment
        initializeIngredientsGrid();
        Log.d("Ingredients","Called onCreateView");

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Method which provide the list of unlocked ingredients by calling the content provider
     */
    private  void loadUnlockedIngredients() {
        ingredientsUnlocked.clear();

        ContentResolver cr = getActivity().getContentResolver();

        String selection = IngredientTable.COLUMN_UNLOCKED + " = ?";

        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = cr.query(IngredientsProvider.CONTENT_URI,
                new String[]{},
                selection,
                selectionArgs,
                null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_NAME));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_IMAGEURL));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_CATEGORY));
            int unlocked = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_UNLOCKED));
            boolean unblocked;
            if (unlocked == 0) {
                unblocked = false;
            } else {
                unblocked = true;
            }
            Ingredient ingredient = new Ingredient(getActivity(),name, imageUrl, category, unblocked);
            ingredientsUnlocked.add(ingredient);


        }
        cursor.close();


    }

    /**
     * Initialize the gridView with the ingredients loaded from the content Provider
     */
    public void initializeIngredientsGrid(){
        loadUnlockedIngredients();
        for(Ingredient ing : ingredientsUnlocked){
            Log.d("Ingredients","Loaded "+ing.getName());

        }
        imageAdapter = new ImageAdapter(getActivity(),ingredientsUnlocked);
        gridview.setAdapter(imageAdapter);
        listIngredientsSelected = new ArrayList<Ingredient>();
    }

    public void updateIngredientsGrid(){
        loadUnlockedIngredients();
        imageAdapter.setIngredients(ingredientsUnlocked);
        gridview.setAdapter(null);
        gridview.setAdapter(imageAdapter);
        gridview.invalidateViews();
    }

    public void clearIngredients(){
        listIngredientsSelected.clear();
    }

    public void resetSliderView(){
        ((ImageAdapter)gridview.getAdapter()).resetAllSelection();
        gridview.invalidateViews();


    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onIngredientSelectedListener = (OnIngredientSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnIngredientSelectedListener");
        }



    }

    @Override
    public void onDetach() {
        super.onDetach();
        onIngredientSelectedListener = null;
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
    public interface OnIngredientSelectedListener {
        // TODO: Update argument type and name
        public void addIngredientSelected(Ingredient ingredient);
        public void removeIngredient(Ingredient ingredient);

    }

}
