package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.expogame.R;
import it.polimi.expogame.support.Dish;
import it.polimi.expogame.support.ImageAdapterDraggable;
import it.polimi.expogame.support.Ingredient;
import it.polimi.expogame.support.ViewHolder;


public class CookManagerFragment extends Fragment implements  CookFragment.OnDishCreatedListener, IngredientFragment.OnIngredientSelectedListener{


    private ArrayList<Ingredient> ingredientsSelected;
    private GridView gridView;
    private ImageAdapterDraggable imageAdapter;
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

        gridView.setAdapter(null);
        gridView.setAdapter(new ImageAdapterDraggable(getActivity(),ingredientsSelected));

        gridView.invalidateViews();
        Log.d("NUMBER",""+gridView.getAdapter().getViewTypeCount()+ " "+imageAdapter.getCount());

    }

    public void removeIngredient(Ingredient ingredient){
        this.ingredientsSelected.remove(ingredient);

        gridView.setAdapter(null);
        gridView.setAdapter(new ImageAdapterDraggable(getActivity(),ingredientsSelected));
        gridView.invalidateViews();

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
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:{

                    float X = 200;
                    float Y = 200;

                    View view = (View) event.getLocalState();
                    view.setX(X);
                    view.setY(Y);
                    view.setVisibility(View.VISIBLE);

                }
                    break;
                case DragEvent.ACTION_DROP:
                    Log.d("try",event.getLocalState() + " "+ v);
                    View view1 = (View) event.getLocalState();
                    if(view1.getParent() == v){
                        float X = event.getX();
                        float Y = event.getY();

                        if(Y<100) Y=150;
                        if(X>900) X=150;

                        View view = (View) event.getLocalState();
                        view.setX(X);
                        view.setY(Y);
                        view.setVisibility(View.VISIBLE);



                        Log.w("DEBUGGING"," x is "+X + " and y is " + Y);
                        return true;
                    }else{
                        View view = (View) event.getLocalState();
                        int width = view.getWidth();
                        int height = view.getHeight();
                        //get the object tag that have all the information like ingredient
                        //object, name and picture of ingredient(definition in getView ImageAdapterDraggable)
                        ViewHolder holder = (ViewHolder)view.getTag();
                        Ingredient ingredient = holder.getIngredient();

                        Log.d("COOKMANAGER","NAME INGREDIENT "+ingredient.getName().toString()+" ");
                        if(view.getParent().getClass().equals(GridView.class)){
                            Log.d("ingridview","sono in grid view");
                            //add name of ingredient to ingredients selected to cook together
                            ingredientsToCombine.add(holder.getText().getText().toString());
                            Log.d("NAME", ingredientsToCombine.toString());
                            //remove ingredient from the adapter in order to change view and remove
                            //view from the grip
                            imageAdapter.removeIngredient(ingredient);
                            //remove ingredient from selected list in order to pass the correct
                            //llist to the new adapter
                            ingredientsSelected.remove(ingredient);
                            Log.d("REVOME", ingredientsSelected.toString());
                            //recreate adapter
                            gridView.setAdapter(null);
                            gridView.setAdapter(new ImageAdapterDraggable(getActivity(),ingredientsSelected));

                            //insert view item in cook zone
                            ViewGroup container = (ViewGroup) v;
                            container.addView(view);
                            view.setVisibility(View.VISIBLE);
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
                            params.height = height;
                            params.width = width;
                            float X = event.getX();
                            float Y = event.getY();
                            view.setX(X);
                            view.setY(Y);
                            view.setLayoutParams(params);
                            Log.w("DEBUGGING"," x is "+X + " and y is " + Y);


                        }else{
                            //remove item from cook zone
                            ViewGroup owner = (ViewGroup) view.getParent();
                            Log.d("mi sposto dal framelayout",view.getParent().toString());
                            owner.removeViewInLayout(view);
                            //remove name from ingredient to cook
                            ingredientsToCombine.remove(ingredient.getName());
                            //add ingredient to selected
                            ingredientsSelected.add(ingredient);

                            //recreate adapter
                            gridView.setAdapter(null);
                            gridView.setAdapter(new ImageAdapterDraggable(getActivity(),ingredientsSelected));

                        }


                        gridView.invalidateViews();
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
