package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.polimi.expogame.R;
import it.polimi.expogame.database.objects.Hint;
import it.polimi.expogame.database.tables.IngredientTable;
import it.polimi.expogame.database.tables.IngredientsInDishes;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.converters.ConverterImageNameToDrawableId;
import it.polimi.expogame.database.objects.Dish;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment{


    //private OnFragmentInteractionListener mListener;
    public static final String Tag = "DetailsFragment";
    private Dish dish;
    private static final HashMap<Integer,Integer> difficultyStarsMap;
    static{
        difficultyStarsMap = new HashMap<>();
        difficultyStarsMap.put(1,R.drawable.stars1);
        difficultyStarsMap.put(2,R.drawable.stars2);
        difficultyStarsMap.put(3,R.drawable.stars3);
        difficultyStarsMap.put(4,R.drawable.stars4);
        difficultyStarsMap.put(5,R.drawable.stars5);

    }


    public static DetailsFragment newInstance( Dish dish) {
        DetailsFragment fragment = new DetailsFragment(dish);

        return fragment;
    }

    public DetailsFragment(){

    }

    public DetailsFragment(Dish dish) {
        this.dish = dish;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //enable go back button on activity action bar
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        if(dish != null) {
            TextView nameDish = (TextView) view.findViewById(R.id.nameDishLabel);
            nameDish.setText(dish.getName().toUpperCase());
            TextView descriptionDish = (TextView) view.findViewById(R.id.description_dish);
            descriptionDish.setText(dish.getDescription());
            TextView curiosityDish = (TextView) view.findViewById(R.id.curiosity_dish);
            curiosityDish.setText(dish.getCuriosity());
            TextView nationality = (TextView) view.findViewById(R.id.nationality_dish);
            nationality.setText(dish.getTranslationNationality());
            ImageView difficultyStars = (ImageView) view.findViewById(R.id.imageDifficulty);
            Log.d(Tag, "dish " + dish.getName() + "   " + dish.getDifficulty());
            difficultyStars.setImageResource(difficultyStarsMap.get(dish.getDifficulty()));
            //setting image of the nationality
            ImageView nationalityImage = (ImageView) view.findViewById(R.id.imageFlag);
            String nationalityImageFile = dish.getNationality().replaceAll(" ", "_").toLowerCase() + ".png";
            Log.d(Tag, nationalityImageFile);

            int nationalImageId = ConverterImageNameToDrawableId.convertImageNameToDrawable(getActivity(), nationalityImageFile);
            nationalityImage.setImageDrawable(getResources().getDrawable(nationalImageId));

            final ImageView imageDish = (ImageView) view.findViewById(R.id.imageDish);
            int imageDishId = ConverterImageNameToDrawableId.convertImageNameToDrawable(getActivity(), dish.getImageUrl());
            imageDish.setImageDrawable(getResources().getDrawable(imageDishId));
            imageDish.setTag(new Integer(imageDishId));


            LinearLayout receipe = (LinearLayout) view.findViewById(R.id.ingredients_list_images);

            ArrayList<String> ingredients = new ArrayList<String>();

            //now let's put in the GridLayout all the ImageView relatives to the ingredients of the dish

            String name = dish.getName();

            Uri uri = Uri.parse(DishesProvider.CONTENT_URI + "/ingredients");
            String selection = IngredientsInDishes.COLUMN_ID_DISH + " = ?";

            String[] selectionArgs = new String[]{name};

            Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null) {
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    String ingredient = cursor.getString(cursor.getColumnIndexOrThrow(IngredientsInDishes.COLUMN_ID_INGREDIENT));
                    Log.w("ExpoChef","in detailsfragment trovo" + ingredient);
                    ingredients.add(loadDetailsIngredient(ingredient));
                    cursor.moveToNext();
                }
                cursor.close();
            }

            for (String s : ingredients) {

                ImageView image = new ImageView(getActivity().getApplicationContext());
                int imageIngredientsId = ConverterImageNameToDrawableId.convertImageNameToDrawable(getActivity(), s);
                image.setImageDrawable(getResources().getDrawable(imageIngredientsId));
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                layout.setMargins(10,0,10,0);
                image.setLayoutParams(layout);
                receipe.addView(image);

            }


        }
        return view;
    }


    private String loadDetailsIngredient(String name){


        String imageUrl="";
        String selection = IngredientTable.COLUMN_NAME + " = ?";

        String[] selectionArgs = new String[]{name};

        Cursor cursor = getActivity().getContentResolver().query(IngredientsProvider.CONTENT_URI,null,selection,selectionArgs,null);

        if (cursor != null){
            cursor.moveToFirst();
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_IMAGEURL));
        }
        cursor.close();

        return imageUrl;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



}
