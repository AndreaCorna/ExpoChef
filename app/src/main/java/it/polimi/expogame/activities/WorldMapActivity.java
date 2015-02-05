package it.polimi.expogame.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.polimi.expogame.R;
import it.polimi.expogame.fragments.info.WorldFragment;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.support.ConverterStringToStringXml;

public class WorldMapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_map);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ExpoMapFragment())
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ConverterStringToStringXml.getStringFromXml(getApplicationContext(),"map_pager_label"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_world_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();

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
