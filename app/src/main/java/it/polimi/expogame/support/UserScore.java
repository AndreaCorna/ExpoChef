package it.polimi.expogame.support;

/**
 * Created by andrea on 22/02/15.
 */
public class UserScore {

    private int currentScore;
    private static UserScore istance;

    private UserScore(){
        setCurrentScore();
    }

    public static UserScore getIstance(){
        if(istance == null){
            istance = new UserScore();
        }
        return istance;
    }

    public int getCurrentScore(){
        return this.currentScore;
    }

    public boolean enoughScore(int requiredScore){
        return (requiredScore <=  currentScore) ? true : false;
    }

    //TODO choose where save the information
    private void setCurrentScore(){
        this.currentScore = 1;
    }

    //TODO choose when save this value on the memory
}
