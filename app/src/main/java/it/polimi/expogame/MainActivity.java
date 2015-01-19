package it.polimi.expogame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.ArrayList;

import it.polimi.expogame.database.ExpoGameDbHelper;
import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.info.ZoneFragment;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.support.Ingredient;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MAIN ACTIVITY";
    private ViewPager viewPager = null;

    private CustomPagerAdapter customPagerAdapter;

    private DrawerLayout mDrawerLayout;
    private GridView gridview;
    private LinearLayout linearLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<Ingredient> listIngredientsSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting reference of the ViewPager element in the view
        linearLayout = (LinearLayout)findViewById(R.id.ingredients_layout);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!view.isSelected()){
                    view.setSelected(true);
                    view.setBackgroundColor(getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));
                    listIngredientsSelected.add((Ingredient)gridview.getAdapter().getItem(position));
                }else{
                    view.setSelected(false);
                    view.setBackgroundColor(getResources().getColor(R.color.abc_background_cache_hint_selector_material_light));

                    listIngredientsSelected.remove((Ingredient) gridview.getAdapter().getItem(position));
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(customPagerAdapter.getItem(position).getClass().equals(CookManagerFragment.class)){

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }else{
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        //creating the adapter to attach to the viewPager
        customPagerAdapter = new CustomPagerAdapter(fragmentManager);

        viewPager.setAdapter(customPagerAdapter);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility

        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(getString(R.string.app_name));
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                String title = customPagerAdapter.getPageTitle(viewPager.getCurrentItem()).toString().toLowerCase();
                title = Character.toString(title.charAt(0)).toUpperCase()+title.substring(1);
                getSupportActionBar().setTitle(title+" "+getString(R.string.string_options));
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        listIngredientsSelected = new ArrayList<Ingredient>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(linearLayout);
        return super.onPrepareOptionsMenu(menu);
    }



    //implementation of interface in order to set dynamically the adapter of zones
    /*@Override
    public void setAdapter(ArrayAdapter adapter) {
        optionsListView.setAdapter(adapter);
        invalidateOptionsMenu();
    }*/

    /*Private class in order to manage the click on the slide bar*/
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            Log.d(TAG, "dididididididiid " + gridview.getAdapter().getItem(position));

        }
    }







}


class CustomPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_NUMBER = 4;


    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override

    public Fragment getItem(int position) {
        Fragment fragment = null;
        //http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        //better way to implement but other problems because: 'never hold a reference to a Fragment outside of the Adapter'
        //switch link the tab number with the fragment which has to be used
        switch (position) {
            case 0:
                fragment = new ExpoMapFragment();
                break;
            case 1:
                fragment = new ARFragment();
                break;
            case 2:
                fragment = new CookManagerFragment();
                break;
            case 3:
                fragment = new RootFragment();


                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_NUMBER;
    }

    @Override
    /*
        define the titles for each tab
     */
    public CharSequence getPageTitle(int position) {
        String title = new String();
        switch (position) {
            case 0:
                title = "MAP";
                break;
            case 1:
                title = "CATCH";

                break;
            case 2:
                title = "COOK";
                break;
            case 3:
                title = "INFO";


        }
        return title;
    }
}


