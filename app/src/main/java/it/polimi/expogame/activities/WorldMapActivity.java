package it.polimi.expogame.activities;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.polimi.expogame.R;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.fragments.options.OptionsFragment;
import it.polimi.expogame.support.UserScore;
import it.polimi.expogame.support.adapters.CustomPagerAdapter;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;

public class WorldMapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_map);

        if (savedInstanceState == null) {

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

            if (getString(R.string.screen_type).equals("phone")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            trans.add(R.id.container, new ExpoMapFragment());

            trans.commit();

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ConverterStringToStringXml.getStringFromXml(getApplicationContext(),"map_pager_label"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_world_map, menu);
        menu.findItem(R.id.action_score).setTitle(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "score_label")+String.valueOf(UserScore.getInstance(getApplicationContext()).getCurrentScore()));
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
    /*Override of the method in order to call finish and so release all
    * activity resources
    * */
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

}
