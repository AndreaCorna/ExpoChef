package it.polimi.expogame.fragments.info;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

import it.polimi.expogame.R;
import it.polimi.expogame.support.Hint;


public class HintFragmentDialog extends DialogFragment {

    String dishName;
    ArrayList<Hint> hintArrayList;
    private static final String TAG = "HintFragmentDialog";
    private OnHintUnlockedListener hintUnlockedListener;


    public  interface OnHintUnlockedListener{
        public void hintUnlocked(String nameDish, String nameIngredient);
    }


    public HintFragmentDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hint_dialog, container);

        //setting dish name in dialog
        dishName = getArguments().getString("dish_name");
        getDialog().setTitle(getArguments().getString("dish_name"));
        //setting dish image in dialog
        ImageView dishImage= (ImageView)view.findViewById(R.id.image_dish_dialog);
        dishImage.setImageResource(getArguments().getInt("dish_image"));

        Log.d(TAG," on CREATE HintFragmentDialog");

        hintArrayList = getArguments().getParcelableArrayList("hints");

       // Collections.sort(hintArrayList);

        //adding hint image in dialog
        for (int i=0;i<hintArrayList.size()-1;i++) {
            Hint current_hint = hintArrayList.get(i);
            LinearLayout hintsBoxLayout = (LinearLayout) view.findViewById(R.id.hints_box);
            ImageView imageView = new ImageView(getActivity().getApplicationContext());
            imageView.setTag(current_hint.getName());
            //adding style to images hints
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            layout.setMargins(20,0,20,0);
            imageView.setLayoutParams(layout);
            //check if already suggested
            if (current_hint.alreadySuggested()){
                imageView.setImageResource(current_hint.getDrawableImage());

            }
            else{
                imageView.setImageResource(R.drawable.question_mark);
            }
            hintsBoxLayout.addView(imageView);
        }

        Button hintButton = (Button)view.findViewById(R.id.show_hint_button);
        hintButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              for (int i=0; i<hintArrayList.size()-1;i++){
                                                   Hint hint = hintArrayList.get(i);
                                                  if (!hint.alreadySuggested()){
                                                      hintUnlockedListener.hintUnlocked(dishName,hint.getName());
                                                      ((ImageView)getView().findViewWithTag(hint.getName())).setImageResource(hint.getDrawableImage());
                                                  }
                                              }
                                          }
                                      }
        );


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null) {
            return;
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        int dialogWidth = width*3/4;
        int dialogHeight =  height*3/4;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hintUnlockedListener = (OnHintUnlockedListener)getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        hintUnlockedListener = null;
    }
}