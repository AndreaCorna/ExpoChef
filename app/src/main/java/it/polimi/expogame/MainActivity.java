package it.polimi.expogame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import it.polimi.expogame.database.ExpoGameDbHelper;
import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.info.DetailsFragment;
import it.polimi.expogame.fragments.info.InfoFragment;
import it.polimi.expogame.fragments.info.WorldFragment;
import it.polimi.expogame.fragments.map.ExpoMapFragment;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.support.Dish;


public class MainActivity extends FragmentActivity implements WorldFragment.OnDishSelectedListener{
    private static final String TAG = "MAIN ACTIVITY";
    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting reference of the ViewPager element in the view
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        //creating the adapter to attach to the viewPager
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(fragmentManager);
        viewPager.setAdapter(customPagerAdapter);
        testDB();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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


}


class CustomPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_NUMBER =4;


    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override

    public Fragment getItem(int position) {
        Fragment fragment = null;
        //http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        //better way to implement but other problems because: 'never hold a reference to a Fragment outside of the Adapter'
        //switch link the tab number with the fragment which has to be used
        switch (position){
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
