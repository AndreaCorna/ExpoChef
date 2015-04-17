package it.polimi.expogame.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import it.polimi.expogame.R;
import it.polimi.expogame.database.tables.IngredientTable;
import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.cook.IngredientFragment;
import it.polimi.expogame.fragments.info.WorldFragment;
import it.polimi.expogame.fragments.options.OptionsFragment;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.support.MusicPlayerManager;
import it.polimi.expogame.support.UserScore;
import it.polimi.expogame.support.adapters.CustomPagerAdapter;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;
import it.polimi.expogame.support.adapters.ImageAdapter;
import it.polimi.expogame.database.objects.Ingredient;


public class MainActivity extends ActionBarActivity  implements IngredientFragment.OnIngredientSelectedListener{
    private static final String TAG = "MAIN ACTIVITY";
    private ViewPager viewPager = null;

    private CustomPagerAdapter customPagerAdapter;

   // private MediaPlayer soundtrackPlayer;
    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final int CAPTURE_ACTIVITY_RESULT = 10;
    private static final int CAPTURE_ACTIVITY_LAUNCH = 20;
    private static final int MAP_ACTIVITY_LAUNCH = 30;
    private boolean audioActivated;
    private boolean onBackButtonPressed;
    private boolean childrenActivityLaunched;

    private LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout)findViewById(R.id.ingredients_layout);





        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(1);
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
                    startCookAnimation();


                }else{
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);

                    startWorldAnimation();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        //creating the adapter to attach to the viewPager
        customPagerAdapter = new CustomPagerAdapter(fragmentManager,getApplicationContext());

        viewPager.setAdapter(customPagerAdapter);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.icon_ingredients));

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.cookbutton, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility

        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(getString(R.string.app_name));
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
                getIngredientFragmentIstance().resetSliderView();
                getIngredientFragmentIstance().clearIngredients();
            }

            public void onDrawerOpened(View drawerView) {
                String title = customPagerAdapter.getPageTitle(viewPager.getCurrentItem()).toString().toLowerCase();
                title = Character.toString(title.charAt(0)).toUpperCase()+title.substring(1);
                getSupportActionBar().setTitle(title+" "+getString(R.string.string_options));
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        audioActivated = prefs.getBoolean("musicActivated",true);
        MusicPlayerManager.initialize(getApplicationContext(),R.raw.soundtrack,AudioManager.STREAM_MUSIC,true,0.5f,0.5f);

        if(audioActivated){
            MusicPlayerManager.getInstance().startPlayer();
        }
      //  homePressed = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
        * Reactivate the soundtrack once returned in the view
        * for example when you are in ArActivity and you want to
        * return in the cook activity.
        * */
    @Override
    protected void onResume(){
       super.onResume();
       if(audioActivated && !MusicPlayerManager.getInstance().isPlaying()){
           MusicPlayerManager.getInstance().startPlayer();
       }
       onBackButtonPressed = false;
       childrenActivityLaunched = false;

    }

    /*
    * Stop the soundtrack once leave from app
    * */
    @Override
    protected void onStop(){
        Log.d("EXIT","call onStop "+childrenActivityLaunched);
        super.onStop();
        if(audioActivated &&
                MusicPlayerManager.getInstance().isPlaying() &&
                (onBackButtonPressed || !childrenActivityLaunched)){
            MusicPlayerManager.getInstance().pausePlayer();
            //this.soundtrackPlayer.release();
        }
    }

    public void setChildrenActivityLaunched(){
        childrenActivityLaunched = true;
    }

    /*
  * Restart the soundtrack when return in the application
  * */
    @Override
    protected void onRestart(){
        super.onRestart();

        switch (viewPager.getCurrentItem()){
            case CustomPagerAdapter.COOK_FRAGMENT_INDEX:
                startCookAnimation();
                break;
            case CustomPagerAdapter.WORLD_FRAGMENT_INDEX:
                startWorldAnimation();
                break;

        }
        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        audioActivated = prefs.getBoolean("musicActivated",true);
        if(audioActivated && !MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().startPlayer();
        }
        //this.soundtrackPlayer.start();

    }

    private void startCookAnimation(){
        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("firstTimeCook",true);
        if(isFirstTime){
            getCookManagerFragmentIstance().startAnimation();
            prefs.edit().putBoolean("firstTimeCook",false).commit();

        }
    }

    private void startWorldAnimation(){
        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("firstTimeWorld",true);
        if(isFirstTime){
            getWorldFragmentIstance().startAnimation();
            prefs.edit().putBoolean("firstTimeWorld",false).commit();

        }

    }

    //override method in order to control stop and play of music
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackButtonPressed = true;

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
                //check if gps is on, if it is not ask for activation, result check in onactivityresult
                if(isLocationServiceActive()) {
                    childrenActivityLaunched = true;
                    launchMapActivity();
                }else {
                    buildAlertMessageNoGps(R.id.action_start_map);
                }
                break;
            case R.id.action_start_capture:
                if(isLocationServiceActive()) {
                    childrenActivityLaunched = true;
                    launchCaptureActivity();
                }else {
                    buildAlertMessageNoGps(R.id.action_start_capture);
                }
                break;
            case R.id.options:
                childrenActivityLaunched = true;
                launchOptionsActivity();

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(linearLayout);
   //     scoreView = (TextView) menu.findItem(R.id.action_score).getActionView();
 //       scoreView.setText(score.getCurrentScore());
        return super.onPrepareOptionsMenu(menu);
    }





    /*method called with done button slider in order to load ingredients selected n fragment cook*/
    public void chooseDone(View view){

        mDrawerLayout.closeDrawers();
        getIngredientFragmentIstance().resetSliderView();
        getIngredientFragmentIstance().clearIngredients();
    }

    private IngredientFragment getIngredientFragmentIstance(){
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for(Fragment fragment:list){
            if(fragment.getClass().equals(IngredientFragment.class)){
                return (IngredientFragment)fragment;
            }
        }
        return null;
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

    private WorldFragment getWorldFragmentIstance(){
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for(Fragment fragment:list){
            if(fragment.getClass().equals(WorldFragment.class)){
                return (WorldFragment)fragment;
            }
        }
        return null;
    }




    private void launchCaptureActivity(){
        if(audioActivated && MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().pausePlayer();
        }

        Intent i = new Intent(this,ARActivity.class);
        startActivityForResult(i, CAPTURE_ACTIVITY_RESULT);
    }

    private void launchMapActivity(){
        if(audioActivated && MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().pausePlayer();
        }
        Intent i = new Intent(this,WorldMapActivity.class);
        startActivity(i);
    }

    /**
     *
      * Use to control the result of the activities launched by this activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            //return from ar activity: load new ingredient
            case CAPTURE_ACTIVITY_RESULT:
                //check if a mascotte was unlocked, if true refresh ingredients on slider
                if(resultCode == RESULT_OK && data.getExtras().getBoolean("captured")){
                    getIngredientFragmentIstance().updateIngredientsGrid();

                }
                break;

            case MAP_ACTIVITY_LAUNCH:
                Log.d("RESULT","map");

                if(isLocationServiceActive()){
                    launchMapActivity();
                }
                break;

            case CAPTURE_ACTIVITY_LAUNCH:
                Log.d("RESULT","ar");
                if(isLocationServiceActive()){
                    launchCaptureActivity();
                }
                break;
        }

    }

    private boolean isLocationServiceActive(){
        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    /**
     * Message box to ask for GPS
     */
    private void buildAlertMessageNoGps(int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "message_gps_activation"))
                .setCancelable(false)
                .setNegativeButton(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "no_answer"), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        switch (requestCode){
            case R.id.action_start_capture:
                builder.setPositiveButton(ConverterStringToStringXml.getStringFromXml(getApplicationContext(),"yes_answer"), new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog, final int requestCode) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),CAPTURE_ACTIVITY_LAUNCH);

                    }
                });
                break;
            case R.id.action_start_map:
                builder.setPositiveButton(ConverterStringToStringXml.getStringFromXml(getApplicationContext(),"yes_answer"), new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog, final int requestCode) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),MAP_ACTIVITY_LAUNCH);

                    }
                });
                break;
        }
        final AlertDialog alert = builder.create();
        alert.show();
    }
/*
     //public MediaPlayer getSoundtrackPlayer() {
        return soundtrackPlayer;
    }*/


    private void launchOptionsActivity(){
        if(audioActivated && MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().pausePlayer();
        }
        Intent intent = new Intent(this,OptionsActivity.class);
        startActivity(intent);
    }

    @Override
    public void addIngredientSelected(Ingredient ingredient) {
        getCookManagerFragmentIstance().addIngredientSelected(ingredient);
    }

    @Override
    public void removeIngredient(Ingredient ingredient) {
        getCookManagerFragmentIstance().removeIngredient(ingredient);

    }
}





