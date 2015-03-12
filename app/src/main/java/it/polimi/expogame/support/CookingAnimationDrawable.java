package it.polimi.expogame.support;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;


/**
 * Created by fabio on 12/03/15.
 */
public abstract class CookingAnimationDrawable extends AnimationDrawable {


    Handler animationHandler;
    /*
    * The constructor add each frame with it own duration to
    * this DrawableAnimation object
    * */
    public CookingAnimationDrawable(AnimationDrawable AnimDrawable) {
        for (int i = 0; i < AnimDrawable.getNumberOfFrames(); i++) {
            this.addFrame(AnimDrawable.getFrame(i), AnimDrawable.getDuration(i));
        }
    }



    @Override
    public void start() {
        super.start();
        /*
         * Call super.start() to call the base class start animation method.
         * Then add a handler to call onAnimationFinish() when the total
         * duration for the animation has passed
         */
        animationHandler = new Handler();
        animationHandler.postDelayed(new Runnable() {

            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());

    }

    /**
     * Gets the total duration of all frames.
     * @return The total duration.
     */
    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            iDuration += this.getDuration(i);
        }

        return iDuration;
    }

    /**
     * Called when the animation finishes.
     */
    protected abstract void onAnimationFinish();

}
