package it.polimi.expogame.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polimi.expogame.R;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.cook.IngredientFragment;
import it.polimi.expogame.fragments.info.WorldFragment;


public class CookTabletFragment extends Fragment {



    public static CookTabletFragment newInstance() {
        CookTabletFragment fragment = new CookTabletFragment();
        return fragment;
    }

    public CookTabletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cook_tablet, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.ingredient_tablet_frame, new IngredientFragment());
        transaction.replace(R.id.cook_tablet_frame, new CookManagerFragment());

        transaction.commit();

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
