package it.polimi.expogame.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by andrea on 22/02/15.
 */
public class UserScore {

    public final static  int hintCost=100;
    public final static  int dishPrize=200;

    private static Context context;
    private static UserScore istance;
    private static final String TAG = "USERSCORE";

    private UserScore(){


    }

    public static UserScore getInstance(Context context){
        if(istance == null){
            istance = new UserScore();
            UserScore.context=context;
        }
        return istance;
    }

    public int getCurrentScore(){

        SharedPreferences prefs = context.getSharedPreferences("expochef", Context.MODE_PRIVATE);
        int score = prefs.getInt("score", 400); //0 is the default value

        return score;

    }


    public boolean removePoints(int points){
        int currentScore = getCurrentScore();
        if (currentScore < points ){
            Log.d(TAG,"Not enough points");
            return false; //not enough points
        }
        else{
            Log.d(TAG,"Removing Points ");

            currentScore -= points;
            setSharedPreferencesScore(currentScore);
            return true;

        }

    }

    public void addPoints(int points){
        int currentScore = getCurrentScore();
        currentScore += points;
        setSharedPreferencesScore(currentScore);



    }

    private void setSharedPreferencesScore(int score){
        SharedPreferences prefs = context.getSharedPreferences("expochef", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("score", score);
        editor.commit();
    }

}
