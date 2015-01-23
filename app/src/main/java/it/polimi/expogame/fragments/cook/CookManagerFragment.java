package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.expogame.R;
import it.polimi.expogame.support.Dish;
import it.polimi.expogame.support.ImageAdapter;
import it.polimi.expogame.support.Ingredient;


public class CookManagerFragment extends Fragment implements  CookFragment.OnDishCreatedListener, IngredientFragment.OnIngredientSelectedListener{


    private ArrayList<Ingredient> ingredientsSelected;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<String> ingredientsToCombine;


    public static CookManagerFragment newInstance() {
        CookManagerFragment fragment = new CookManagerFragment();

        return fragment;
    }

    public CookManagerFragment() {

        ingredientsSelected = new ArrayList<Ingredient>();
        ingredientsToCombine = new ArrayList<String>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View currentView = inflater.inflate(R.layout.fragment_cook_manager, container, false);
        gridView = (GridView) currentView.findViewById(R.id.ingredient_table);
        imageAdapter = new ImageAdapter(getActivity(),ingredientsSelected);
        gridView.setAdapter(imageAdapter);
        // Inflate the layout for this fragment
        return currentView;
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
    public void onDishCreated(Dish dish) {

    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {

    }


    public void addIngredientSelected(Ingredient ingredient){
        this.ingredientsSelected.add(ingredient);

        imageAdapter.notifyDataSetChanged();
        getView().invalidate();


    }

    public void removeIngredient(Ingredient ingredient){
        this.ingredientsSelected.remove(ingredient);

        imageAdapter.notifyDataSetChanged();
        getView().invalidate();

    }

    

    //method used to sort the arraylist of name of ingredients
    private void reorderListIngredientsToCombine(){
        Collections.sort(ingredientsToCombine, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

}
