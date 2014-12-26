package it.polimi.expogame;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * If a connection is up and running, SplashActivity ( entry point of app ) will update the coordinates of our mascotte
 * by fetching the JSON returned from the web-app.
 *
 * Done that it will launch the MainActivity of the application.
 *
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new fetchData().execute();
    }

    //types of AsyncTask are Params, Progress, Result
    private class fetchData extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.w("ExpoGame", "OnPreExecute");
            // before start to fetch from server, it will be nice to show a loading gif here
        }

        @Override
        protected Void doInBackground(Void...params) {

            InputStream is = null;
            String json="";

            //if the client has got a valid connection perform the update from the server
            if(isConnected()) {
                HttpGet request = new HttpGet();
                URI website = null;
                HttpClient httpclient = new DefaultHttpClient();

                try {
                    website = new URI("http://bullcantshit.noip.me/jsontest.html");
                } catch (URISyntaxException e) {
                    Log.w("ExpoGame", "Wrong URI");
                    e.printStackTrace();
                }

                request.setURI(website);

                try {
                    HttpResponse response = httpclient.execute(request);
                    is = response.getEntity().getContent();

                    if (is != null)
                        json = convertInputStreamToString(is);
                    else
                        json = "wtf";


                } catch (Exception e) {
                    Log.w("ExpoGame", "Something went wrong during connection with server");
                    e.printStackTrace();
                }

                try {

                    JSONObject jsonObject = new JSONObject(json);
                    Log.w("ExpoGame", "JSON created!");
                    String s = jsonObject.get("glossary").toString();
                    Log.w("ExpoGame", "test field " + s);

                } catch (Exception e) {

                }
            }

            //TODO: Here if we have got a valid JSON let's parse it and update the db

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing updating db
            // will close this activity and lauch main activity
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            // close this activity
            finish();
        }

        private String convertInputStreamToString(InputStream is) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(is));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            is.close();
            return result;
        }

        private boolean isConnected(){
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;
        }

    }
}



