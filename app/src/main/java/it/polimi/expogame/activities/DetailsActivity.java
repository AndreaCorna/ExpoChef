package it.polimi.expogame.activities;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import it.polimi.expogame.R;
import it.polimi.expogame.fragments.info.DetailsFragment;
import it.polimi.expogame.support.Dish;

/**
 * This class implements the activity to show details about a dish unlocked by user
 */
public class DetailsActivity extends ActionBarActivity {

    public static final String TAG ="Details Activity";

    @Override
    /**
     * Load all information from the intent pass by zonefragment to create a dish object
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        long id = getIntent().getLongExtra("idDish",0);
        String name = getIntent().getStringExtra("nameDish");
        String nationality = getIntent().getStringExtra("nationalityDish");
        String imageUrl = getIntent().getStringExtra("imageUrlDish");
        String description = getIntent().getStringExtra("descriptionDish");
        String zone = getIntent().getStringExtra("zoneDish");
        boolean created = getIntent().getBooleanExtra("createdDish",false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment(new Dish(id, name, nationality, imageUrl, description, zone, created)))
                    .commit();
        }
        setTitle(getTitle()+" "+name);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();

        return super.onOptionsItemSelected(item);
    }


}
