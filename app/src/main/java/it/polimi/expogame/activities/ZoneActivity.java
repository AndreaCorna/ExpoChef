package it.polimi.expogame.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import it.polimi.expogame.R;
import it.polimi.expogame.database.tables.IngredientsInDishes;
import it.polimi.expogame.fragments.EmptyFragment;
import it.polimi.expogame.fragments.info.HintFragmentDialog;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.fragments.info.ZoneFragment;
import it.polimi.expogame.providers.DishesProvider;
import it.polimi.expogame.support.MusicPlayerManager;
import it.polimi.expogame.support.UserScore;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;

/**
 * Activity used to show the list of dishes related to a zone
 */
public class ZoneActivity extends ActionBarActivity implements HintFragmentDialog.OnHintUnlockedListener{
    ZoneFragment fragment;

    //need to update the view of the score when hint is used
    private UserScore score;
    private MenuItem scoreView;
    private boolean audioActivated;
    private boolean playMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String zone = getIntent().getStringExtra("zone");
        String translation = getIntent().getStringExtra("translation");
        String screenType = getString(R.string.screen_type);

        setContentView(R.layout.activity_zone);
        fragment = new ZoneFragment(zone);

        //initialize score object
        score = UserScore.getInstance(getApplicationContext());

        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            if (screenType.equals("tablet")) {
                //MEGLIO SEMPRE METTERE IL TAG PERCHÈ CON QUELLO SI PUÒ OTTENERE LA REFERENCE DEL FRAGMENT
                trans.add(R.id.zoneFragment, new ZoneFragment(zone),ZoneFragment.TAG);
                trans.add(R.id.detailFragment, new EmptyFragment(),EmptyFragment.TAG);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                trans.add(R.id.container, new ZoneFragment(zone),ZoneFragment.TAG);


            }
            trans.commit();

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(Character.toString(translation.charAt(0)).toUpperCase()+translation.substring(1));
        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        audioActivated = prefs.getBoolean("musicActivated",true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_zone, menu);
        scoreView = menu.findItem(R.id.action_score);
        scoreView.setTitle(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "score_label")+score.getCurrentScore());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_score:
                break;
            default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void hintUnlocked(String nameDish, String nameIngredient) {
        setIngredientSuggested(nameDish,nameIngredient);
        scoreView.setTitle(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "score_label")+score.getCurrentScore());
    }

    private void setIngredientSuggested(String nameDish, String nameIngredient){
        Uri uri = Uri.parse(DishesProvider.CONTENT_URI + "/ingredients");
        String where = IngredientsInDishes.COLUMN_ID_DISH + " = ? AND "+IngredientsInDishes.COLUMN_ID_INGREDIENT + " = ?";
        String[] selectionArgs = new String[]{nameDish,nameIngredient};

        ContentValues values = new ContentValues();
        values.put(IngredientsInDishes.COLUMN_HINT_GIVEN, 1);

        getContentResolver().update(uri, values, where, selectionArgs);
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(audioActivated && !MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().startPlayer();
        }
        playMusic = false;
    }

    /*
    * Stop the soundtrack once leave from app
    * */
    @Override
    protected void onStop(){
        Log.d("MM", "call onStop "+playMusic);
        super.onStop();
        if(audioActivated &&
                MusicPlayerManager.getInstance().isPlaying() && !playMusic){
            MusicPlayerManager.getInstance().pausePlayer();
            //this.soundtrackPlayer.release();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playMusic = true;

    }

    public void setChildrenActivityLaunched(){
        playMusic = true;
    }





}
