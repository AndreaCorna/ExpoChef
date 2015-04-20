package it.polimi.expogame.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.plus.PlusShare;

import it.polimi.expogame.R;
import it.polimi.expogame.fragments.EmptyFragment;
import it.polimi.expogame.fragments.info.DetailsFragment;
import it.polimi.expogame.fragments.info.ZoneFragment;
import it.polimi.expogame.support.MusicPlayerManager;
import it.polimi.expogame.support.converters.ConverterImageNameToDrawableId;
import it.polimi.expogame.database.objects.Dish;

/**
 * This class implements the activity to show details about a dish unlocked by user
 */
public class DetailsActivity extends ActionBarActivity{

    public static final String TAG ="Details Activity";
    private PostObject objectToPost;
    private boolean audioActivated;
    private boolean onBackButtonPressed;

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
        String curiosity = getIntent().getStringExtra("curiosityDish");
        Integer difficulty = getIntent().getIntExtra("difficultyDish", 0);

        boolean created = getIntent().getBooleanExtra("createdDish",false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .commit();
        }

        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            if (getString(R.string.screen_type).equals("phone")) {
                //MEGLIO SEMPRE METTERE IL TAG PERCHÈ CON QUELLO SI PUÒ OTTENERE LA REFERENCE DEL FRAGMENT
                trans.add(R.id.container, new DetailsFragment(new Dish(getApplicationContext(),id, name, nationality, imageUrl, description, zone, created,null,curiosity,difficulty)));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
            else {
                trans.add(R.id.container, new DetailsFragment(new Dish(getApplicationContext(),id, name, nationality, imageUrl, description, zone, created,null,curiosity,difficulty)));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            trans.commit();

        }
        setTitle(getTitle()+" "+name);
        objectToPost = new PostObject(getApplicationContext(),name,imageUrl);
        SharedPreferences prefs = getSharedPreferences("expochef", Context.MODE_PRIVATE);
        audioActivated = prefs.getBoolean("musicActivated",true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_facebook:
                launchPostActivity();
                return true;
            case R.id.action_google_plus:
                Intent shareIntent = new PlusShare.Builder(this)
                        .setType("text/plain")
                        .setText(getResources().getString(R.string.happy_message_facebook) +" "+objectToPost.getName())
                        .getIntent();

                startActivityForResult(shareIntent, 0);
                return true;
            default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    private void launchPostActivity(){
        Intent intent = new Intent(getApplicationContext(),FacebookShareActivity.class);
        intent.putExtra("name", objectToPost.getName());
        intent.putExtra("image",objectToPost.getImageId());
        startActivity(intent);
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(audioActivated && !MusicPlayerManager.getInstance().isPlaying()){
            MusicPlayerManager.getInstance().startPlayer();
        }
        onBackButtonPressed = false;
    }

    /*
    * Stop the soundtrack once leave from app
    * */
    @Override
    protected void onStop(){
        Log.d("EXIT", "call onStop");
        super.onStop();
        if(audioActivated &&
                MusicPlayerManager.getInstance().isPlaying() && !onBackButtonPressed){
            MusicPlayerManager.getInstance().pausePlayer();
            //this.soundtrackPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackButtonPressed = true;

    }


    private class PostObject{
        private String name;
        private int imageId;

        public PostObject(Context context,String name,String imageUrl){
            this.name = name;
            this.imageId = ConverterImageNameToDrawableId.convertImageNameToDrawable(context,imageUrl);
        }

        public String getName() {
            return name;
        }

        public int getImageId() {
            return imageId;
        }
    }


}
