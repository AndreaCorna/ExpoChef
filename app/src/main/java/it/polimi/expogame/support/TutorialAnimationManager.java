package it.polimi.expogame.support;

import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import it.polimi.expogame.R;


/**
 * This class can be used to manage an animation for the tutorial.
 */
public class TutorialAnimationManager {

    private TextView textSpeakMascot;
    private ImageView mascot;
    private Point screenSize;
    private String screenType;
    private boolean inZoneFragment;

    private Handler startHandler = new Handler();
    private Handler nextHandler = new Handler();
    private ArrayList<String> tutorialStrings;
    private Animation enterAnimation;

    private static final long UPDATE_INTERVAL = 2500;

    public TutorialAnimationManager(TextView textSpeakMascot, ImageView mascot, Point screenSize, ArrayList<String> tutorialStrings,String screenType,boolean inZoneFragment){
        this.textSpeakMascot = textSpeakMascot;
        this.mascot = mascot;
        this.screenSize = screenSize;
        this.tutorialStrings = tutorialStrings;
        this.screenType = screenType;
        this.inZoneFragment = inZoneFragment;
        setupStartAnimation();
    }

    /**
     * The method starts the animation over the image view field set in the object with the constructor.
     */
    public void startEnterAnimation(){
        mascot.startAnimation(enterAnimation);
    }

    /**
     * The method is used to configure the parameters of the animation
     */
    private void setupStartAnimation(){
        final int width;

        if(screenType.equals("phone") || inZoneFragment){
            width = screenSize.x;
        }else{
            width = (screenSize.x)/2;
        }
        final float to =  ((float)width)/3;
        enterAnimation = new TranslateAnimation(width, to,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        enterAnimation.setDuration(3000);  // animation duration
        enterAnimation.setFillAfter(true);
        enterAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mascot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startHandler.postDelayed(new UpdateTextRunnable(tutorialStrings),UPDATE_INTERVAL);
                if(!screenType.equals("phone")) {
                    textSpeakMascot.setX(to - textSpeakMascot.getWidth() / 2);
                }
                textSpeakMascot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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
                textSpeakMascot.setText(texts.remove(0));
                nextHandler.postDelayed(new UpdateTextRunnable(texts), UPDATE_INTERVAL);
            }else{
                texts.clear();
                tutorialStrings.clear();
                //else start animation out
                textSpeakMascot.setVisibility(View.INVISIBLE);
                int widthToStart;

                if(screenType.equals("phone") || inZoneFragment){
                    widthToStart = screenSize.x;
                }else{
                    widthToStart = (screenSize.x)/2;
                }
                float to =  ((float)widthToStart)/3;
                TranslateAnimation outAnimation = new TranslateAnimation(to, screenSize.x,
                        0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                outAnimation.setDuration(3000);  // animation duration
                outAnimation.setFillAfter(true);
                mascot.startAnimation(outAnimation);
            }
        }
    }
}
