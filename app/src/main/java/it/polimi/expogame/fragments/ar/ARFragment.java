package it.polimi.expogame.fragments.ar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import it.polimi.expogame.R;
import it.polimi.expogame.activities.ARActivity;


/**
 * @degrigis
 *This fragment is merely a launcher for the Metaio view.
 * It implements View.OnClickListener because I want to implements the
 * callback method for the button here and not in the MainActivity.
 *
 */
public class ARFragment extends Fragment implements View.OnClickListener {

    private ProgressBar spinner;

    public ARFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ar, container, false);
        spinner = (ProgressBar)view.findViewById(R.id.loading_spinner);

        //Retrieve the button and register this fragment as the listener for him
        ImageView upButton = (ImageView)view.findViewById(R.id.buttonStartMetaio);
        upButton.setOnClickListener(this);


        return view;  // Inflate the layout for this fragment

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * Listener for the button "Catch it!" that will
     * spawn the Metaio activity.
     */
    @Override
    public void onClick(View v) {
        new launchMetaio().execute(this.getActivity());
    }

    @Override
    public void onResume(){

        super.onResume();
        spinner.setVisibility(View.INVISIBLE);

    }


    private class launchMetaio extends AsyncTask<Activity,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Activity... params) {

            Intent i = new Intent(params[0],ARActivity.class);
            startActivity(i);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    }
