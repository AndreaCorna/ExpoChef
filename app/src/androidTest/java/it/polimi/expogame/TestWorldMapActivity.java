package it.polimi.expogame;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import it.polimi.expogame.activities.WorldMapActivity;
import it.polimi.expogame.support.ConverterStringToStringXml;

/**
 * Created by andrea on 09/02/15.
 */
public class TestWorldMapActivity extends ActivityInstrumentationTestCase2<WorldMapActivity> {


    private WorldMapActivity activity;


    public TestWorldMapActivity() {
        super(WorldMapActivity.class);
    }


    public TestWorldMapActivity(Class<WorldMapActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();

    }

    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("worldmap activity is null", activity);
    }

    public void testActivityTitle() {
        //It is good practice to read the string from your resources in order to not break
        //multiple tests when a string changes.
        final String expected = ConverterStringToStringXml.getStringFromXml(activity.getApplicationContext(),"map_pager_label");
        final String actual = activity.getTitle().toString();
        assertEquals("title world map activity contains wrong text", expected, actual);
    }
}
