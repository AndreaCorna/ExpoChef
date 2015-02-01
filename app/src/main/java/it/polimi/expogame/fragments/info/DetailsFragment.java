package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import it.polimi.expogame.R;
import it.polimi.expogame.support.ConverterImageNameToDrawableId;
import it.polimi.expogame.support.ConverterStringToStringXml;
import it.polimi.expogame.support.Dish;


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
            Log.d(Tag,"dish "+dish.getName()+"   "+dish.getDifficulty());
            difficultyStars.setImageResource(difficultyStarsMap.get(dish.getDifficulty()));
            //setting image of the nationality
            ImageView nationalityImage = (ImageView) view.findViewById(R.id.imageFlag);
            String nationalityImageFile = dish.getNationality().replaceAll(" ", "_").toLowerCase()+".png";
            Log.d(Tag,nationalityImageFile);

            int nationalImageId = ConverterImageNameToDrawableId.convertImageNameToDrawable(getActivity(),nationalityImageFile);
            nationalityImage.setImageDrawable(getResources().getDrawable(nationalImageId));

            final ImageView imageDish = (ImageView) view.findViewById(R.id.imageDish);
            int imageDishId = ConverterImageNameToDrawableId.convertImageNameToDrawable(getActivity(),dish.getImageUrl());
            imageDish.setImageDrawable(getResources().getDrawable(imageDishId));
            imageDish.setTag(new Integer(imageDishId));



        }

        return view;
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
