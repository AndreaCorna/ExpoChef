package it.polimi.expogame.support.converters;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by fabio on 08/03/15.
 */
public class CookingAnimationManager {


    private GridView cookerView;
    private View ingredientToMove;
    private Point screenSize;
    private Animation enterAnimation;


    private static final long UPDATE_INTERVAL = 2500;

    public CookingAnimationManager(Point screenSize , View ingredientToMove, GridView cookerView){

        this.screenSize = screenSize;
        this.ingredientToMove = ingredientToMove;
        this.cookerView = cookerView;

        setupStartAnimation();
    }

    public void startEnterAnimation(){

    }


    public void startMoveIngredients(){




    }



    private void setupStartAnimation(){
        int width = screenSize.x;
        float to =  ((float)width)/2.9f;

        float yfrom = screenSize.y/3.5f;
        float yto = ( yfrom/1.35f);

        enterAnimation = new TranslateAnimation(width, to,
               yto,yto);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)


        enterAnimation.setDuration(2000);  // animation duration
        enterAnimation.setFillAfter(true);
        enterAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
