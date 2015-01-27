package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView cookerFish;
    private TranslateAnimation enterAnimation;
    private TextView textSpeakMascotte;
    Handler startHandler = new Handler();
    Handler nextHandler = new Handler();
    private ArrayList<String> tutorialStrings;

    private static final long UPDATE_INTERVAL = 2000;


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
        //
        textSpeakMascotte = (TextView)currentView.findViewById(R.id.speak);
        textSpeakMascotte.setVisibility(View.INVISIBLE);
        //set animation 
        animationCooker(currentView);
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
                getActivity().getContentResolver().update(DishesProvider.CONTENT_URI, values, where, names);
                resetSelectionsIngredients();

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
            }else{
                Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.message_toast_cook),Toast.LENGTH_LONG).show();
            }
        }

    }

    //if a dish is created, refresh  combined
    private void resetSelectionsIngredients(){
        FrameLayout layout = (FrameLayout)getView().findViewById(R.id.external);
        for(int i=0;i<layout.getChildCount();){
            if(layout.getChildAt(i).getClass().equals(RelativeLayout.class)){
                layout.removeViewAt(i);
            }else{
                i++;
            }
        }
        ingredientsToCombine.clear();

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

    /**
     * set animation for the mascotte tutorial
     * @param view
     */
    private void animationCooker(View view){
        cookerFish = new ImageView(getActivity().getApplicationContext());
        cookerFish.setImageDrawable(getResources().getDrawable(R.drawable.cooker));
        cookerFish.setVisibility(View.INVISIBLE);


        Point size = getDimensionScreen();
        int width = size.x;
        float to =  ((float)width)/3;
        enterAnimation = new TranslateAnimation(width, to,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        enterAnimation.setDuration(3000);  // animation duration
        enterAnimation.setFillAfter(true);
        enterAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cookerFish.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadTutorialStrings();
                startHandler.postDelayed(new UpdateTextRunnable(tutorialStrings),UPDATE_INTERVAL);
                textSpeakMascotte.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        FrameLayout layout = (FrameLayout)view.findViewById(R.id.cook_manager_fragment);
        layout.addView(cookerFish);
    }

    public void startAnimation(){
        cookerFish.startAnimation(enterAnimation);
    }

    /**
     * Private class in order to update the text for the tutorial
     */
    private class UpdateTextRunnable implements Runnable{

        private ArrayList<String> texts;

        public UpdateTextRunnable(ArrayList<String> texts){
            this.texts = texts;
        }

        @Override
        public void run() {
            //if i have some text to show update
            if(texts.size() >0) {
                textSpeakMascotte.setText(texts.remove(0));
                nextHandler.postDelayed(new UpdateTextRunnable(texts), UPDATE_INTERVAL);
            }else{
                //else start animation out
                textSpeakMascotte.setVisibility(View.INVISIBLE);
                Point size = getDimensionScreen();
                int width = size.x;
                float to =  ((float)width)/3;
                TranslateAnimation outAnimation = new TranslateAnimation(to, width,
                        0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                outAnimation.setDuration(3000);  // animation duration
                outAnimation.setFillAfter(true);
                cookerFish.startAnimation(outAnimation);
            }
        }
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
        tutorialStrings.add("ciao");
        tutorialStrings.add("come");
        tutorialStrings.add("va");
    }


}
