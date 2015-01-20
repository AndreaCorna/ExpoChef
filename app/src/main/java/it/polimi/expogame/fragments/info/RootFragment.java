package it.polimi.expogame.fragments.info;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polimi.expogame.R;

/**
 * Root fragment in order to create transaction inside the fragment from info fragment to details fragment
 * because we use a viewpager adapter.
 */
public class RootFragment extends Fragment {

    public static final String TAG = "RootFragment";


    public RootFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_root,container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
        transaction.replace(R.id.root_frame, new WorldFragment());

        transaction.commit();

        return view;
    }



}
