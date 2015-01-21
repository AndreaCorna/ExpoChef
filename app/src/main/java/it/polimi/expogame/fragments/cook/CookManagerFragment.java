package it.polimi.expogame.fragments.cook;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.annotation.Inherited;
import java.util.ArrayList;

import it.polimi.expogame.R;
import it.polimi.expogame.support.Dish;
import it.polimi.expogame.support.ImageAdapter;
import it.polimi.expogame.support.Ingredient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CookManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookManagerFragment extends Fragment implements  CookFragment.OnDishCreatedListener, IngredientFragment.OnIngredientSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Ingredient> ingredientsSelected;
    private GridView gridView;
    private ImageAdapter imageAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CookManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CookManagerFragment newInstance(String param1, String param2) {
        CookManagerFragment fragment = new CookManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CookManagerFragment() {
        ingredientsSelected = new ArrayList<Ingredient>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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

}
