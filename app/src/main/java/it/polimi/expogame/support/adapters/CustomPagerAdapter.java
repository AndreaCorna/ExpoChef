package it.polimi.expogame.support.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.polimi.expogame.fragments.cook.CookManagerFragment;
import it.polimi.expogame.fragments.info.RootFragment;
import it.polimi.expogame.support.converters.ConverterStringToStringXml;

/**
 * Created by andrea on 20/02/15.
 */
public class CustomPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_NUMBER = 2;
    private Context context;
    public static final int COOK_FRAGMENT_INDEX = 0;
    public static final int WORLD_FRAGMENT_INDEX = 1;


    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    public CustomPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }


    @Override

    public Fragment getItem(int position) {
        Fragment fragment = null;
        //http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        //better way to implement but other problems because: 'never hold a reference to a Fragment outside of the Adapter'
        //switch link the tab number with the fragment which has to be used
        switch (position) {

            case COOK_FRAGMENT_INDEX:
                fragment = new CookManagerFragment();
                break;
            case WORLD_FRAGMENT_INDEX:
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

            case COOK_FRAGMENT_INDEX:
                title = ConverterStringToStringXml.getStringFromXml(context, "cook_pager_label");
                break;
            case WORLD_FRAGMENT_INDEX:
                title = ConverterStringToStringXml.getStringFromXml(context,"info_pager_label");


        }
        return title;
    }
}
