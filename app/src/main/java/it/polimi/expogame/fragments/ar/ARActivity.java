package it.polimi.expogame.fragments.ar;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.util.Log;
import android.view.View;
import android.os.Vibrator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

//occhio a non importare il location.LocationListener vecchio
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.metaio.cloud.plugin.util.MetaioCloudUtils;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.Camera;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.SensorValues;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import it.polimi.expogame.R;
import it.polimi.expogame.database.IngredientTable;
import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.providers.IngredientsProvider;
import it.polimi.expogame.providers.MascotsProvider;
import it.polimi.expogame.support.ConverterStringToStringXml;
import it.polimi.expogame.support.Mascotte;

public class ARActivity extends ARViewActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener

{
    private ContentResolver cr;
    private static final String TAG="ARActivity";

    //----GPS and LOCATION SERVICE OBJ-------------
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //---------------------------------------

    //----MASCOTS MODELS OBJECT--------------
    private ArrayList<IGeometry> MascotsList;
    private ArrayList<Mascotte> Mascots;
    private final int range = 800; //this is the range in which you can see a mascot


    //----------------------------------------

    //Radar object displayed in the metaio view
    private IRadar mRadar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.w("ExpoGame", "Spawning ARactivity");
        MascotsList = new ArrayList<IGeometry>();

        // Set GPS tracking configuration
        boolean result = metaioSDK.setTrackingConfiguration("GPS", false);
        MetaioDebug.log("Tracking data loaded: " + result);
        try {
            // Extract all assets and overwrite existing files if debug build
            AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
        } catch (IOException e) {
            MetaioDebug.printStackTrace(Log.ERROR, e);

        }

        //Retreive the mascots from the content providers
        cr = getContentResolver();
        Mascots = new ArrayList<Mascotte>();

        Cursor c = cr.query(MascotsProvider.CONTENT_URI,
                new String[]{MascotsTable.COLUMN_NAME, MascotsTable.COLUMN_LATITUDE, MascotsTable.COLUMN_LONGITUDE,MascotsTable.COLUMN_CAPTURED,MascotsTable.COLUMN_MODEL},
                null,
                null,
                null);

        while (c.moveToNext()) {
            Mascotte m = new Mascotte(c.getString(0), c.getString(1), c.getString(2));
            m.setCaptured(c.getInt(3));
            m.setModel(c.getString(4));
            //Log.w("MetaioACTIVITY", "generated mascotte with lati" + m.getLat());
            Mascots.add(m);
        }

        LocationManager service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            buildAlertMessageNoGps();
        }

        c.close();

    }

    @Override
    protected void onResume() {
        //create the client to connect to the service
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Connect the client.
        mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart(); //call onStart of superclass
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect(); //disconnect from the service
        super.onStop();
    }

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        mGoogleApiClient.disconnect();
        super.onDestroy();


            finish();

    }

    /**
     * Message box to ask for GPS
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "message_gps_activation"))
                .setCancelable(false)
                .setPositiveButton(ConverterStringToStringXml.getStringFromXml(getApplicationContext(),"yes_answer"), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton(ConverterStringToStringXml.getStringFromXml(getApplicationContext(), "no_answer"), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public void onDrawFrame() {
        if (metaioSDK != null && mSensors != null) {
            SensorValues sensorValues = mSensors.getSensorValues();

            float heading = 0.0f;
            if (sensorValues.hasAttitude()) {
                float m[] = new float[9];
                sensorValues.getAttitude().getRotationMatrix(m);

                Vector3d v = new Vector3d(m[6], m[7], m[8]);
                v.normalize();

                heading = (float) (-Math.atan2(v.getY(), v.getX()) - Math.PI / 2.0);
            }

            IGeometry geos[] = new IGeometry[MascotsList.size()];
            for (int i = 0; i < MascotsList.size(); i++) {
                geos[i] = MascotsList.get(i);
            }

            Rotation rot = new Rotation((float) (Math.PI / 2.0), 0.0f, -heading);
            
            
            for (IGeometry geo : geos) {
                if (geo != null) {
                    geo.setRotation(rot);
                }
            }


            /*for (IGeometry geo : geos) {
                Log.d("ROTATION",""+geo.getRotation().getAxisAngle().getX());
                if (geo != null) {
                    Rotation rot;
                    Vector3d vector = new Vector3d();
                    //geo.getRotation().setFromEulerAngleDegrees(new Vector3d(90,0,90));
                    //rot = new Rotation((float) (Math.PI / 2), 0.0f,  (float) (Math.PI / 2));

                    rot = new Rotation((float) (Math.PI / 2), 0.0f,  0);

                    geo.setRotation(rot);
                }
            }*/
        }

        super.onDrawFrame();
    }

    /**
     * Listener for the button that permits to close the metaio view
     * (the small 'x' in the right corner of the screen
     *
     * @param v
     */
    public void onButtonClick(View v) {
        finish();
    }

    /**
     * inflating the layout of the metaioView
     *
     * @return
     */
    @Override
    protected int getGUILayout() {
        return R.layout.tutorial_location_based_ar;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
        return null;
    }

    /**
     * This method is called from the onSurfaceCreated in the ARViewActivity of MetaioSDK,
     * basically after the configuration of the metaio arview it ask us to load content
     * on the screen
     */
    @Override
    protected void loadContents() {

        // Clamp geometries' Z position to range [5000;200000] no matter how close or far they are
        // away.
        // This influences minimum and maximum scaling of the geometries (easier for development).
        metaioSDK.setLLAObjectRenderingLimits(5, 200);

        // Set render frustum accordingly
        metaioSDK.setRendererClippingPlaneLimits(10, 220000);

        // create radar
        mRadar = metaioSDK.createRadar();

        mRadar.setBackgroundTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
                "radar.png"));
        mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
                "yellow.png"));
        mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);

        for (Mascotte mascotte : Mascots) {

            //Link a LLACoordinates to an IGeometry and check if it will be displayed or not due to
            //the player position ( by switching the .setVisible() propriety  )
            final IGeometry NewMascot = createPOIGeometry(new LLACoordinate(Float.parseFloat(mascotte.getLat()),
                    Float.parseFloat(mascotte.getLongi()),
                    0, 0),mascotte.getModel());

            Log.d("fdsafd",NewMascot.toString());
            //MascotList contains all the IGeometries generated from the mascots java objects
            //retreived from the MascotsProvider
            MascotsList.add(NewMascot);

            //Add the IGeometry created on the radar ( it will be displayed or not due to
            //the createPoiGeometry functions that check the distance between the mascot's coord
            //and the player position )
            mRadar.add(NewMascot);

            NewMascot.setName(mascotte.getName());
            Log.w("MetaioACTIVITY", "Create mascot" + mascotte.getName());

            //Let's turn red the mascots captured yet
            if(mascotte.getCaptured()==1)
            {

                //Update the SurfaceView with a red point
                mSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mRadar.setObjectTexture(NewMascot, AssetsManager.getAssetPathAsFile(getApplicationContext(),
                                "red.png"));
                    }
                });
            }
        }
    }

    //-----UTILITY------------------------------------------------------------------------------

    /**
     * Create Igeometry object based on coordinates of mascots and theri obj model
     *
     * @param lla are the coordinates lati+longi of the mascot
     * @return the IGeometry 3d object that will be displayed on the ARView
     */
    private IGeometry createPOIGeometry(LLACoordinate lla, String modelUrl) {
        final File path =
                AssetsManager.getAssetPathAsFile(getApplicationContext(),
                        modelUrl); //model url is type of "jalapeno.obj" and are in assets with obj+mtl

        if (path != null) {
            IGeometry geo = metaioSDK.createGeometry(path);
            geo.setTranslationLLA(lla);
            geo.setLLALimitsEnabled(true);
            geo.setScale(250);

            //Log.w("MetaioACTIVITY", "mascots coord" + lla.getLatitude() + lla.getLongitude());
            //Log.w("MetaioACTIVITY","your coord"+ mSensors.getLocation().getLatitude()+ mSensors.getLocation().getLongitude());

            LLACoordinate location = geo.getTranslationLLA();

            float distance = (float) MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());

            if (distance < range) //CHECK THE PLAYER AND MASCOT POSITION DISTANCE
            {
                geo.setVisible(true);
                Log.w("MetaioACTIVITY", "True for a mascot");
            } else {
                geo.setVisible(false);
                Log.w("MetaioACTIVITY", "False for a mascot");
            }
            return geo;
        } else {
            MetaioDebug.log(Log.ERROR, "Missing files for POI geometry");
            return null;
        }
    }

    @Override
    protected void startCamera()
    {

/*
        Camera cam = new Camera();
        cam.setYuvPipeline(Boolean.FALSE); //disable it to avoid green camera display on some devices(?)
        cam.setFacing(Camera.FACE_BACK);
        //cam.setDownsample(1);
        cam.setIndex(0);
        //cam.setFlip(Camera.FLIP_BOTH);
        cam.setFlip(Camera.FLIP_NONE);

        metaioSDK.startCamera(cam);


        //Problematic GPU VideoCore IV ( galaxy core G350)
        //Adreno 200 ( from metaio helpdesk )

        //clear the camera cash before start it
        //( http://stackoverflow.com/questions/4856955/how-to-programatically-clear-application-data )
*/
        super.startCamera();
    }

    //-----------------------------------------------------------------------------------------------

    /*
    * Method called when a geometry is touched on the
    * metaio view, it sets the mascotte as captured ( if it was not yet )
    * and unlocks all ingredients associated to it
    * */
    @Override
    protected void onGeometryTouched(final IGeometry geometry) {
        MetaioDebug.log("Geometry selected: " + geometry);


        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        int captured=0;

        for(Mascotte m : Mascots){
            Log.w("METAIOACTIVITY",""+m.getName()+m.getCaptured());
            if(geometry.getName().equals(m.getName())) //let's find the mascotte touched
              {
                  captured = m.getCaptured();
                  if(captured==0)
                  {

                    Log.w("METAIOACTIVITY","updating catptured routine");
                    m.setCaptured(1);

                    //Let's update also the mascot content provider
                    //with the captured=1 value
                    //MASCOTS PROVIDER UPDATING
                    //-----------------------------------------------------
                    String where = MascotsTable.COLUMN_NAME + " = ?";
                    String[] name = new String[]{m.getName()};

                    ContentValues values = new ContentValues();

                    values.put(MascotsTable.COLUMN_CAPTURED,1);
                    cr.update(MascotsProvider.CONTENT_URI,values,where,name);
                    //-----------------------------------------------------


                    //Update the SurfaceView with a red point
                    mSurfaceView.queueEvent(new Runnable() {
                          @Override
                          public void run() {
                              mRadar.setObjectTexture(geometry, AssetsManager.getAssetPathAsFile(getApplicationContext(),
                                      "red.png"));
                          }
                      });

                    //Let's unlock all the ingredients associated to the mascotte
                    //INGREDIENTS PROVIDER UPDATING
                    //-----------------------------------------------------
                    where = IngredientTable.COLUMN_CATEGORY + " = ?";
                    name = new String[]{m.getName()};

                    values = new ContentValues();
                    values.put(IngredientTable.COLUMN_UNLOCKED, 1);

                    cr.update(IngredientsProvider.CONTENT_URI, values, where, name);
                    //-----------------------------------------------------
                  }
                  break;
              }
            continue;
        }
    }

    /**
     * This function update the SurfaceView of Metaio to show the characters
     * when you are at 'range' meters from them or hide when you are further.
     * This function is declared in the superclass of this class and implemented here,
     * it is called when
     */
    @Override
    protected void updateView() {
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                for (IGeometry mascotte : MascotsList) {
                    LLACoordinate location = mascotte.getTranslationLLA();
                    float distance = (float) MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());

                    if (distance < range) {
                        mascotte.setVisible(true);
                    } else
                        mascotte.setVisible(false);
                }
            }
        });
    }

    //----LOCATION SERVICE METHODS-----------------------------------------------------

    /**
     * Method called when we are connected to the location service
     */
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(500); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        // do nothing for now
    }


    @Override
    public void onLocationChanged(Location location) {

        updateView(); // when the location change let's update the view!

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //--------------------------------------------------------------------------------
}


