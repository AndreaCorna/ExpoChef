package it.polimi.expogame;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.polimi.expogame.fragments.ar.ARFragment;
import it.polimi.expogame.fragments.cook.CookFragment;
import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.info.InfoFragment;
import it.polimi.expogame.fragments.map.MapFragment;


public class MainActivity extends FragmentActivity {

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
}


class CustomPagerAdapter extends FragmentPagerAdapter{

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
                fragment = new MapFragment();
                break;
            case 1:
                fragment = new ARFragment();
                break;
            case 2:
                fragment = new CookManagerFragment();
                break;

            case 3:
                fragment = new InfoFragment();
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
