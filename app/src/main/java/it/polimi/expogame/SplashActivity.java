package it.polimi.expogame;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.providers.MascotsProvider;
import it.polimi.expogame.support.Mascotte;


/**
 * ( ENTRY POINT OF APPLICATION )
 *
 * If a connection is up and running, SplashActivity will update the coordinates of our mascotte
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

            //TODO add a gif that show a loading
        }

        @Override
        protected Void doInBackground(Void...params) {

            //if the client has got a valid connection perform the update from the server
            if(isConnected()) {

                InputStream is = null;
                String json="";
                HttpGet request = new HttpGet();
                URI website = null;

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

                //set the timeout to 5 seconds for the http connection
                HttpClient httpclient = new DefaultHttpClient(httpParams);

                ContentResolver cr;

                ArrayList <Mascotte> remoteMascottes = new ArrayList <Mascotte>();

                try {
                    website = new URI("http://192.168.1.37:3000/api/mascots");
                } catch (URISyntaxException e) {
                    Log.w("ExpoGame", "Wrong/Malformed URI");
                    e.printStackTrace();
                }

                request.setURI(website);


                try {
                    HttpResponse response = httpclient.execute(request);
                    is = response.getEntity().getContent();

                    if (is != null)
                        json = convertInputStreamToString(is);
                    else
                        json = "";

                } catch (Exception e) { //if connection fails game over
                    Log.w("ExpoGame", "Something went wrong during connection with server");
                    e.printStackTrace();
                    return null;
                }

                try {

                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    for(int i=0;i<jsonArray.length();i++)
                       {
                           JSONObject jsonMascotte = jsonArray.getJSONObject(i);
                           Mascotte m = new Mascotte(jsonMascotte.getString("name"),jsonMascotte.getString("longitude"),
                                                    jsonMascotte.getString("latitude"));
                           remoteMascottes.add(m);
                       }
                } catch (JSONException e) {
                    Log.w("ExpoGame","Error during the JSON handling");
                    return null;
                }

                    //Let's update the mascots coordinates with the new remotly acquired
                    //( if they are not changed let's make the update anyway )
                    cr  = getContentResolver();

                    for(Mascotte m : remoteMascottes)
                        {
                            String where = MascotsTable.COLUMN_NAME + " = ?";
                            String[] name = new String[]{m.getName()};

                            ContentValues values = new ContentValues();

                            values.put(MascotsTable.COLUMN_LATITUDE,m.getLat());
                            values.put(MascotsTable.COLUMN_LONGITUDE,m.getLongi());

                            cr.update(MascotsProvider.CONTENT_URI,values,where,name);
                        }

                    // DEBUG testing if coordinates are changed
                    /*
                    Cursor c = cr.query( MascotsProvider.CONTENT_URI,
                            new String[]{MascotsTable.COLUMN_NAME,MascotsTable.COLUMN_LONGITUDE},
                            null,
                            null,
                            null);
                    remoteMascottes.clear();
                    while (c.moveToNext())
                    {
                        Mascotte m = new Mascotte(c.getString(0),""+c.getFloat(1),"");
                        remoteMascottes.add(m);

                    }

                    for(Mascotte m : remoteMascottes)
                    {
                        Log.w("ExpoGameUpdate", ""+m.getName());
                        Log.w("ExpoGameUpdate",""+m.getLongi());
                    }

                    c.close();
                    */
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing updating db
            // will close this activity and launch main activity
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            // close this activity
            finish();
        }

        /**
         * Convert the input stream from the server in a String in order
         * to assign lately this string to a JSON obj
         * @param is the input stream received from our server
         * @return the String equivalent to the content of the server response, that will
         *         be our JSON
         * @throws IOException
         */
        private String convertInputStreamToString(InputStream is) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(is));
            String line = "";

            StringBuilder sb = new StringBuilder(); //Java like :)

            while((line = bufferedReader.readLine()) != null)
                sb.append(line);
            is.close();
            return sb.toString();
        }

        /**
         * Helper method in order to check if the client is connected or not
         * to a valid wifi/3g
         * @return true if the client has got a valid connection
         */
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



