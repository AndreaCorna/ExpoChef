package it.polimi.expogame.activities;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.polimi.expogame.R;
import it.polimi.expogame.database.objects.Dish;
import it.polimi.expogame.fragments.info.DetailsFragment;
import it.polimi.expogame.fragments.options.OptionsFragment;

public class OptionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        if (savedInstanceState == null) {

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.container, new OptionsFragment());

            if (getString(R.string.screen_type).equals("phone")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            trans.commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }
}
