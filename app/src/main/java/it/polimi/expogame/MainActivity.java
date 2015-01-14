package it.polimi.expogame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;



import it.polimi.expogame.database.ExpoGameDbHelper;
import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.info.DetailsFragment;
import it.polimi.expogame.fragments.info.InfoFragment;
import it.polimi.expogame.fragments.info.WorldFragment;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.support.Dish;



public class MainActivity extends ActionBarActivity implements WorldFragment.OnDishSelectedListener, WorldFragment.LoadListZoneInterface{
    private static final String TAG = "MAIN ACTIVITY";
    private ViewPager viewPager = null;

    private CustomPagerAdapter customPagerAdapter;

    private DrawerLayout mDrawerLayout;
    private ListView optionsListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting reference of the ViewPager element in the view

        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        //creating the adapter to attach to the viewPager
        customPagerAdapter = new CustomPagerAdapter(fragmentManager);

        viewPager.setAdapter(customPagerAdapter);
        testDB();



        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        optionsListView = (ListView)findViewById(R.id.list_slidermenu);

        optionsListView.setOnItemClickListener(new SlideMenuClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

    }


    //TODO  Delete this method is just a test for checking if the database is working
    public  void testDB(){
        String[] allColumns = {MascotsTable.COLUMN_CATEGORY,MascotsTable.COLUMN_NAME};
        ExpoGameDbHelper myhelper = ExpoGameDbHelper.getInstance(this);
        SQLiteDatabase database = myhelper.getWritableDatabase();
        Cursor cursor = database.query(ExpoGameDbHelper.TABLE_MASCOTS,allColumns,null,null,null,null,null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Log.d(TAG, "Category:  " + cursor.getString(0));
            Log.d(TAG,"Name:  "+cursor.getString(1));

            cursor.moveToNext();
        }
        cursor.close();
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
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(optionsListView);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDishSelected(Dish dish) {
        DetailsFragment detailsFragment = new DetailsFragment(dish);

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.root_frame, detailsFragment, DetailsFragment.Tag);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if(getFragmentManager().findFragmentByTag(this.TAG) == null){
            trans.addToBackStack(WorldFragment.TAG);
        }

        trans.commit();
    }

    //implementation of interface in order to set dynamically the adapter of zones
    @Override
    public void setAdapter(ArrayAdapter adapter) {
        optionsListView.setAdapter(adapter);
        invalidateOptionsMenu();
    }

    /*Private class in order to manage the click on the slide bar*/
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            Log.d(TAG, "dididididididiid " + optionsListView.getAdapter().getItem(position));
            displayView(optionsListView.getAdapter().getItem(position).toString().toLowerCase());

        }
    }

    public void displayView(String position){
        getSupportFragmentManager().beginTransaction().replace(R.id.root_frame,new InfoFragment()).commit();
        mDrawerLayout.closeDrawers();
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
