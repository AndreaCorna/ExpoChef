package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.FacebookShareActivity;
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
            nameDish.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TextView descriptionDish = (TextView) view.findViewById(R.id.descriptionDishLabel);
            descriptionDish.setText(dish.getDescription());
            TextView nationality = (TextView) view.findViewById(R.id.nationality_dish);
            nationality.setText(dish.getNationality());
            final ImageView imageDish = (ImageView) view.findViewById(R.id.imageDish);

            //Start retrieve the drawable id of the dish image using its name
            Context context = getActivity().getApplicationContext();
            int index = dish.getImageUrl().indexOf(".");
            String imageUrl = null;
            //delete extension of file from name if exist
            if (index > 0)
                imageUrl = dish.getImageUrl().substring(0, index);
            //get the id
            int id = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

            if(id != 0){
                //if found, set the image in the details fragment and add tag in order to keep track of the information
                //it must be used when you want to post image on facebook
                imageDish.setImageDrawable(getResources().getDrawable(id));
                imageDish.setTag(new Integer(id));
            }else{
                //resource not found, use a default one
                imageDish.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
                imageDish.setTag(R.drawable.cancel);
            }

            Button button = (Button)view.findViewById(R.id.shareButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(),FacebookShareActivity.class);
                    intent.putExtra("name", dish.getName());
                    Object tag = imageDish.getTag();
                    int id = tag == null ? -1 : Integer.parseInt(tag.toString());
                    intent.putExtra("image",id);
                    startActivity(intent);
                }
            });
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
