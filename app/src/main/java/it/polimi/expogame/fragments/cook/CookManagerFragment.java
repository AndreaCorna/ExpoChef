package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.expogame.R;
import it.polimi.expogame.support.Dish;
import it.polimi.expogame.support.ImageAdapter;
import it.polimi.expogame.support.ImageAdapterDraggable;
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
        imageAdapter = new ImageAdapterDraggable(getActivity(),ingredientsSelected);
        gridView.setAdapter(imageAdapter);
        // Inflate the layout for this fragment
        gridView.setOnDragListener(new MyDragListener());
        FrameLayout l = (FrameLayout)currentView.findViewById(R.id.external);
        l.setOnDragListener(new MyDragListener());
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

    private String hashListIngredientsToCombine(){
        String stringToHash = "";
        for(String ingredient:ingredientsToCombine){
            stringToHash +=ingredient;
        }
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(stringToHash.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        return hashtext;
    }


    private class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            Log.d("drag", v.getClass().toString());
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("try",event.getLocalState() + " "+ v);
                    View view1 = (View) event.getLocalState();
                    if(view1.getParent() == v){
                        float X = event.getX();
                        float Y = event.getY();

                        View view = (View) event.getLocalState();
                        view.setX(X);
                        view.setY(Y);
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }else{

                        // Dropped, reassign View to ViewGroup
                        View view = (View) event.getLocalState();
                        Log.d("prima di if",event.getLocalState().toString());

                        Ingredient ingredient = (Ingredient)view.getTag(R.id.tag_object);
                        Log.d("ingredient",ingredient.toString());
                        Log.d("parent", view.getParent().toString());
                        if(view.getParent().getClass().equals(GridView.class)){
                            Log.d("ingridview","sono in grid view");
                            gridView.removeViewInLayout(view);
                            ingredientsSelected.remove(ingredient);
                            ViewGroup container = (ViewGroup) v;
                            container.addView(view);
                            view.setVisibility(View.VISIBLE);
                        }else{
                            //metodo di dio
                            ViewGroup owner = (ViewGroup) view.getParent();
                            Log.d("mi sposto dal framelayout",view.getParent().toString());
                            owner.removeViewInLayout(view);
                            ingredientsSelected.add(ingredient);
                        }

                        imageAdapter.notifyDataSetChanged();
                        getView().invalidate();
                        break;

                    }

                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    }

}
