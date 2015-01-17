package it.polimi.expogame.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.polimi.expogame.R;



public class FacebookShareActivity extends Activity {

    private static final String PERMISSION = "publish_actions";
    private static final String baseUrl = "android.resource://it.polimi.expogame/";

    private Bundle extrasIntent;
    public ImageView previewImage;
    public TextView postTextView;
    private UiLifecycleHelper uiHelper;
    private Boolean mPendingAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_share);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        mPendingAction = false;

        // Used to print the hash key
        //getHashKey();
        Intent intent = getIntent();
        extrasIntent = intent.getExtras();

        postTextView = (TextView)findViewById(R.id.postText);
        postTextView.setText(getResources().getString(R.string.happy_message_facebook) +" "+ extrasIntent.getString("name"));
        previewImage = (ImageView)findViewById(R.id.imagePreview_container);
        previewImage.setImageURI(Uri.parse(baseUrl + extrasIntent.getInt("image")));


    }

    private void getHashKey(){
        // Used for test purposes.
        // Need to set this in facebook https://developers.facebook.com/apps/ under key hashes in the android platform.
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getString(R.string.app_package_name), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures)
            {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");

                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                } catch (NoSuchAlgorithmException nsa)
                {
                    Log.d("exception" , "No algorithmn");
                    Assert.assertTrue(false);
                }
            }
        } catch (PackageManager.NameNotFoundException nnfe)
        {
            Log.d("exception" , "Name not found");
            Assert.assertNull("Name not found", nnfe);
        }
    }

    public void postButtonPressed(View view){
        if (isOnline()) {
            Toast.makeText(this, getResources().getString(R.string.sharing_facebook_toast), Toast.LENGTH_SHORT).show();
            mPendingAction = true;
            Session session = Session.getActiveSession();
            if (session == null) {
                session = new Session(getApplicationContext());
                Session.OpenRequest openSessionRequest = new Session.OpenRequest(this);
            }
            else{
                if (session.getState().equals(SessionState.CREATED)){
                    // Session is not opened or closed, session is created but not opened.
                    session = new Session(this);
                    Session.setActiveSession(session);
                    session.openForPublish(new Session.OpenRequest(this).setCallback(callback).setPermissions(PERMISSION));
                }
                else{
                    onSessionStateChange(session, session.getState(), null);
                }
            }
            Session.setActiveSession(session);
        }else{
            Toast.makeText(this, getResources().getString(R.string.sharing_facebook_toast_no_internet), Toast.LENGTH_SHORT).show();

        }

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception){
        if(exception != null){
            // Handle exception here.
            Log.v("Facebook CALLBACK", "Facebook login error " + exception);
            return;
        }
        if (state != null && state.isOpened()) {

            if (session.isPermissionGranted(PERMISSION)){
                if (mPendingAction){
                    // Session ready to make requests.
                    postImageToFacebook();
                    mPendingAction = false;
                }
            }
            else{
                // Get the permissions if we don't have them.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
            }
        }
        else if (state.isClosed()){
            // Session logged out.
            return;
        }
    }


    private void postImageToFacebook() {
        Session session = Session.getActiveSession();
        final String extraText = postTextView.getText().toString();
        if (session.isPermissionGranted("publish_actions")){
            Bundle param = new Bundle();

            // Add the image
            try {
                Drawable myDrawable = getResources().getDrawable(extrasIntent.getInt("image"));
                Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArrayData = stream.toByteArray();
                param.putByteArray("picture", byteArrayData);
            } catch (Exception ioe) {
                // The image that was send through is now not there?
                Assert.assertTrue(false);
            }

            // Add the caption
            param.putString("message", extraText);
            Request request = new Request(session,"me/photos", param, HttpMethod.POST, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    addNotification(getString(R.string.photo_post), response.getGraphObject(), response.getError());

                }
            }, null);
            RequestAsyncTask asyncTask = new RequestAsyncTask(request);
            asyncTask.execute();
            finish();
        }
    }

    private void addNotification(String message, GraphObject result, FacebookRequestError error){
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_facebook_activity)
                        .setContentTitle(title)
                        .setContentText(alertMessage);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 0;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception){
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private  boolean isOnline() {
        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
