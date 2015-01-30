package it.polimi.expogame.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import it.polimi.expogame.R;
import it.polimi.expogame.database.IngredientTable;
import it.polimi.expogame.fragments.ar.ARActivity;
import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.ImageAdapter;
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
    private ImageAdapter imageAdapter;
    private ArrayList<Ingredient> ingredientsUnlocked;
    private ArrayList<View> selectedViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting reference of the ViewPager element in the view
        ingredientsUnlocked = new ArrayList<Ingredient>();
        linearLayout = (LinearLayout)findViewById(R.id.ingredients_layout);
        selectedViews = new ArrayList<View>();

         //loading unlocked ingredients in the Cook Options Fragment
        loadUnlockedIngredients();
        gridview = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this,ingredientsUnlocked);
        gridview.setAdapter(imageAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //override in order to change background of an item when is selected and add/remove it from the
            //arraylist we want to pass to cook fragment
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Ingredient ingredient = (Ingredient)gridview.getAdapter().getItem(position);
                if(!listIngredientsSelected.contains(ingredient)){
                    view.setBackgroundColor(Color.LTGRAY);
                    listIngredientsSelected.add(ingredient);
                    getCookManagerFragmentIstance().addIngredientSelected(ingredient);
                    selectedViews.add(view);
                    ((ImageAdapter)gridview.getAdapter()).setSelected(ingredient.getName(),true);
                }else{
                    view.setBackgroundColor(303030);
                    getCookManagerFragmentIstance().removeIngredient(ingredient);
                    listIngredientsSelected.remove(ingredient);
                    selectedViews.remove(view);
                    ((ImageAdapter)gridview.getAdapter()).setSelected(ingredient.getName(),false);

                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //override in order to unlock slider when we are in the cook fragment
            //also used to enable and disable back button on actionbar
            @Override
            public void onPageSelected(int position) {
                if(customPagerAdapter.getItem(position).getClass().equals(ARFragment.class)) {
                    //screen can turn down
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if(customPagerAdapter.getItem(position).getClass().equals(CookManagerFragment.class)){

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    loadUnlockedIngredients();
                    imageAdapter.setIngredients(ingredientsUnlocked);
                    gridview.setAdapter(null);
                    gridview.setAdapter(imageAdapter);
                    //check if is the first time
                    SharedPreferences prefs = getSharedPreferences("expogame", MODE_PRIVATE);
                    boolean isFirstTime = prefs.getBoolean("firstTime",true);
                    if(isFirstTime){
                        //first time start animation and save that no more i have to show it
                        getCookManagerFragmentIstance().startAnimation();
                        prefs.edit().putBoolean("firstTime",false).commit();

                    }

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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.icon_ingredients));

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility

        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(getString(R.string.app_name));
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
                resetSliderView();
                listIngredientsSelected.clear();
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

    /**
     * Method which provide the list of unlocked ingredients by calling the content provider
     */
    private void loadUnlockedIngredients() {
        ingredientsUnlocked.clear();

        ContentResolver cr = this.getContentResolver();

        String selection = IngredientTable.COLUMN_UNLOCKED + " = ?";

        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = cr.query(IngredientsProvider.CONTENT_URI,
                new String[]{},
                selection,
                selectionArgs,
                null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_NAME));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_IMAGEURL));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_CATEGORY));
            int unlocked = cursor.getInt(cursor.getColumnIndexOrThrow(IngredientTable.COLUMN_UNLOCKED));
            boolean unblocked;
            if (unlocked == 0) {
                unblocked = false;
            } else {
                unblocked = true;
            }
            Ingredient ingredient = new Ingredient(this,name, imageUrl, category, unblocked);
            ingredientsUnlocked.add(ingredient);

            Log.d("MAIN","Ho caricato "+name);

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
            case R.id.action_start_map:
                //launchMapActivity();
                break;
            case R.id.action_start_capture:
                launchCaptureActivity();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(linearLayout);
        return super.onPrepareOptionsMenu(menu);
    }





    /*method called with done button slider in order to load ingredients selected n fragment cook*/
    public void chooseDone(View view){

        mDrawerLayout.closeDrawers();
        resetSliderView();
        listIngredientsSelected.clear();
    }

    private CookManagerFragment getCookManagerFragmentIstance(){
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for(Fragment fragment:list){
            if(fragment.getClass().equals(CookManagerFragment.class)){
                return (CookManagerFragment)fragment;
            }
        }
        return null;
    }

    private void resetSliderView(){

        for(View view:selectedViews){
            view.setBackgroundColor(303030);
            ((ImageAdapter)gridview.getAdapter()).resetAllSelection();

        }
        selectedViews.clear();
    }

    private void launchCaptureActivity(){
        Intent i = new Intent(this,ARActivity.class);
        startActivity(i);
    }


}


class CustomPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_NUMBER = 2;


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
            /*case 0:
                fragment = new ExpoMapFragment();
                break;
            case 1:
                fragment = new ARFragment();

                break;*/
            case 0:
                fragment = new CookManagerFragment();
                break;
            case 1:
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
           /* case 0:
                title = "MAP";
                break;
            case 1:
                title = "CATCH";

                break;*/
            case 0:
                title = "COOK";
                break;
            case 1:
                title = "INFO";


        }
        return title;
    }
}


