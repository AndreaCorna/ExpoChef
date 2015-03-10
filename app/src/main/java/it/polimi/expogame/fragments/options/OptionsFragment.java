package it.polimi.expogame.fragments.options;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import  android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import it.polimi.expogame.R;


public class OptionsFragment extends Fragment {



    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();

        return fragment;
    }

    public OptionsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("expochef", Context.MODE_PRIVATE);
        CheckBox tutorialBox = (CheckBox)view.findViewById(R.id.checkBox_option_tutorial);
        tutorialBox.setChecked(prefs.getBoolean("firstTimeCook",true));
        tutorialBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("expochef", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("firstTimeCook",((CheckBox)v).isChecked()).commit();
                prefs.edit().putBoolean("firstTimeWorld",((CheckBox)v).isChecked()).commit();
                prefs.edit().putBoolean("firstTimeZone",((CheckBox)v).isChecked()).commit();


            }
        });

        CheckBox musicBox = (CheckBox)view.findViewById(R.id.checkBox_option_music);
        musicBox.setChecked(prefs.getBoolean("musicActivated",true));
        musicBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("expochef", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("musicActivated",((CheckBox)v).isChecked()).commit();
            }
        });

        CheckBox audioBox = (CheckBox)view.findViewById(R.id.checkBox_option_audio);
        audioBox.setChecked(prefs.getBoolean("audioActivated",true));
        audioBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("expochef", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("audioActivated",((CheckBox)v).isChecked()).commit();
            }
        });

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
