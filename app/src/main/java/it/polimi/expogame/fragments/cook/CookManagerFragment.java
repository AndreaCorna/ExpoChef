package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.DetailsActivity;
import it.polimi.expogame.database.DishesTable;
import it.polimi.expogame.providers.DishesProvider;
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

        Button cookButton = (Button)currentView.findViewById(R.id.cook_button);
        cookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //on click check if a dish will be unlocked
            public void onClick(View v) {
                reorderListIngredientsToCombine();
                String hash = hashListIngredientsToCombine();
                checkNewDishUnlocked(hash);
            }
        });
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
        imageAdapter.setIngredients(ingredientsSelected);
        gridView.setAdapter(imageAdapter);

        gridView.invalidateViews();
        Log.d("NUMBER",""+gridView.getAdapter().getViewTypeCount()+ " "+imageAdapter.getCount());

    }

    public void removeIngredient(Ingredient ingredient){
        this.ingredientsSelected.remove(ingredient);

        gridView.setAdapter(null);
        imageAdapter.setIngredients(ingredientsSelected);
        gridView.setAdapter(imageAdapter);
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

    //get the hash of list ingredients to cook
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
        Log.d("HASH",hashtext);
        return hashtext;
    }

    //check if a dish is unlock show details activity and update db
    private void checkNewDishUnlocked(String hash){

        String selection = DishesTable.COLUMN_HASHINGREDIENTS + " = ?";
        String[] selectionArgs = new String[]{hash};
        Cursor cursor = getActivity().getContentResolver().query(DishesProvider.CONTENT_URI,null,selection,selectionArgs,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                cursor.moveToFirst();
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DishesTable.COLUMN_ID));
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
                //update db
                String where = DishesTable.COLUMN_NAME + " = ?";
                String[] names = new String[]{name};

                ContentValues values = new ContentValues();

                values.put(DishesTable.COLUMN_CREATED,1);
                getActivity().getContentResolver().update(DishesProvider.CONTENT_URI,values,where,names);

                //show details
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



    private class MyDragListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED: {

                float X = 200;
                float Y = 200;

                View view = (View) event.getLocalState();
                view.setX(X);
                view.setY(Y);
                view.setVisibility(View.VISIBLE);

            }
            break;
            case DragEvent.ACTION_DROP:
                Log.d("try", event.getLocalState() + " " + v);
                View view1 = (View) event.getLocalState();
                if (view1.getParent() == v) {
                    float X = event.getX();
                    float Y = event.getY();

                    if (Y < 100 || Y > 600) Y = 150;
                    if (X > 900 || X < 40) X = 150;

                    View view = (View) event.getLocalState();
                    view.setX(X);
                    view.setY(Y);
                    view.setVisibility(View.VISIBLE);

                    Log.w("DEBUGGING", " x is " + X + " and y is " + Y);
                    return true;
                } else {
                    View view = (View) event.getLocalState();
                    int width = view.getWidth();
                    int height = view.getHeight();
                    //get the object tag that have all the information like ingredient
                    //object, name and picture of ingredient(definition in getView ImageAdapterDraggable)
                    ViewHolder holder = (ViewHolder) view.getTag();
                    Ingredient ingredient = holder.getIngredient();

                    Log.d("COOKMANAGER", "NAME INGREDIENT " + ingredient.getName().toString() + " ");
                    if (view.getParent().getClass().equals(GridView.class)) {
                        Log.d("ingridview", "sono in grid view");
                        //add name of ingredient to ingredients selected to cook together
                        ingredientsToCombine.add(holder.getText().getText().toString());
                        Log.d("NAME", ingredientsToCombine.toString());
                        //remove ingredient from selected list in order to pass the correct
                        //llist to the new adapter
                        ingredientsSelected.remove(ingredient);
                        Log.d("REVOME", ingredientsSelected.toString());

                        //refresh adapter
                        gridView.setAdapter(null);
                        imageAdapter.setIngredients(ingredientsSelected);
                        gridView.setAdapter(imageAdapter);

                        //insert view item in cook zone
                        ViewGroup container = (ViewGroup) v;
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.height = height;
                        params.width = width;
                        float X = event.getX();
                        float Y = event.getY();

                        if (Y < 100 || Y > 600) Y = 150;
                        if (X > 900 || X < 40) X = 150;

                        view.setX(X);
                        view.setY(Y);
                        view.setLayoutParams(params);
                        Log.w("DEBUGGING", " x is " + X + " and y is " + Y);


                    } else {
                        //remove item from cook zone
                        ViewGroup owner = (ViewGroup) view.getParent();
                        Log.d("mi sposto dal framelayout", view.getParent().toString());
                        owner.removeViewInLayout(view);
                        //remove name from ingredient to cook
                        ingredientsToCombine.remove(ingredient.getName());
                        Log.d("NAME", ingredientsToCombine.toString());

                        //add ingredient to selected
                        ingredientsSelected.add(ingredient);

                        //refresh adapter
                        gridView.setAdapter(null);
                        imageAdapter.setIngredients(ingredientsSelected);
                        gridView.setAdapter(imageAdapter);

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
