package it.polimi.expogame.fragments.ar;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

//occhio a non importare il location.LocationListener vecchio
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.metaio.cloud.plugin.util.MetaioCloudUtils;
import com.metaio.sdk.ARELInterpreterAndroidJava;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.AnnotatedGeometriesGroupCallback;
import com.metaio.sdk.jni.EGEOMETRY_FOCUS_STATE;
import com.metaio.sdk.jni.IAnnotatedGeometriesGroup;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.IRadar;
import com.metaio.sdk.jni.ImageStruct;
import com.metaio.sdk.jni.LLACoordinate;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.SensorValues;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

import it.polimi.expogame.R;

public class ARActivity extends ARViewActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener

{
    private IAnnotatedGeometriesGroup mAnnotatedGeometriesGroup;

    private MyAnnotatedGeometriesGroupCallback mAnnotatedGeometriesGroupCallback;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    /**
     * Geometries
     */
    private ArrayList <IGeometry> MascotteList;
    private IGeometry mRomeGeo;


    private IRadar mRadar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.w("ExpoGame", "Spawning ARactivity");

        // Set GPS tracking configuration
        boolean result = metaioSDK.setTrackingConfiguration("GPS", false);
        MetaioDebug.log("Tracking data loaded: " + result);
        try
        {
            // Extract all assets and overwrite existing files if debug build
            AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
        }
        catch (IOException e)
        {
            MetaioDebug.printStackTrace(Log.ERROR, e);

        }


    }

    @Override
    protected void onResume()
    {
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
    protected void onStart()
    {
        super.onStart(); //call onStart of superclass
    }

    @Override
    protected void onStop()
    {
        mGoogleApiClient.disconnect(); //disconnect from the service
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        // Break circular reference of Java objects
        if (mAnnotatedGeometriesGroup != null)
        {
            mAnnotatedGeometriesGroup.registerCallback(null);
        }

        if (mAnnotatedGeometriesGroupCallback != null)
        {
            mAnnotatedGeometriesGroupCallback.delete();
            mAnnotatedGeometriesGroupCallback = null;
        }

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onDrawFrame()
    {
        if (metaioSDK != null && mSensors != null)
        {
            SensorValues sensorValues = mSensors.getSensorValues();

            float heading = 0.0f;
            if (sensorValues.hasAttitude())
            {
                float m[] = new float[9];
                sensorValues.getAttitude().getRotationMatrix(m);

                Vector3d v = new Vector3d(m[6], m[7], m[8]);
                v.normalize();

                heading = (float)(-Math.atan2(v.getY(), v.getX()) - Math.PI / 2.0);
            }

            IGeometry geos[] = new IGeometry[] {mRomeGeo};
            Rotation rot = new Rotation((float)(Math.PI / 2.0), 0.0f, -heading);
            for (IGeometry geo : geos)
            {
                if (geo != null)
                {
                    geo.setRotation(rot);
                }
            }
        }

        super.onDrawFrame();
    }

    public void onButtonClick(View v)
    {
        finish();
    }

    @Override
    protected int getGUILayout()
    {
        return R.layout.tutorial_location_based_ar;
    }

    @Override
    protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
    {
        return null;
    }

    @Override
    protected void loadContents()
    {
        mAnnotatedGeometriesGroup = metaioSDK.createAnnotatedGeometriesGroup();
        mAnnotatedGeometriesGroupCallback = new MyAnnotatedGeometriesGroupCallback();
        mAnnotatedGeometriesGroup.registerCallback(mAnnotatedGeometriesGroupCallback);

        // Clamp geometries' Z position to range [5000;200000] no matter how close or far they are
        // away.
        // This influences minimum and maximum scaling of the geometries (easier for development).
        metaioSDK.setLLAObjectRenderingLimits(5, 200);

        // Set render frustum accordingly
        metaioSDK.setRendererClippingPlaneLimits(10, 220000);

        // let's create LLA objects for known cities
        LLACoordinate rome = new LLACoordinate(45.738374, 9.479915, 0, 0);

        // create radar
        mRadar = metaioSDK.createRadar();

        mRadar.setBackgroundTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
                "radar.png"));
        mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
                "yellow.png"));
        mRadar.setRelativeToScreen(IGeometry.ANCHOR_TL);


        mRomeGeo = createPOIGeometry(rome); //Crea il pippottino da associare alle coordinate di rome

        mRadar.add(mRomeGeo); //view point on the radar
        mAnnotatedGeometriesGroup.addGeometry(mRomeGeo, "Pippottino0"); //view the character


    }

    private IGeometry createPOIGeometry(LLACoordinate lla)
    {
        final File path =
                AssetsManager.getAssetPathAsFile(getApplicationContext(),
                        "jalapeno.obj");

        if (path != null)
        {
            IGeometry geo = metaioSDK.createGeometry(path);
            geo.setTranslationLLA(lla);
            geo.setLLALimitsEnabled(true);
            geo.setScale(50);
            LLACoordinate location = geo.getTranslationLLA();
            float distance = (float)MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());
            if(distance < 20)
            {
                geo.setVisible(true);
            }
            else
                geo.setVisible(false); // test to hide the character if I am far from it
            return geo;
        }
        else
        {
            MetaioDebug.log(Log.ERROR, "Missing files for POI geometry");
            return null;
        }
    }

    @Override
    protected void onGeometryTouched(final IGeometry geometry)
    {
        MetaioDebug.log("Geometry selected: " + geometry);

        mSurfaceView.queueEvent(new Runnable()
        {

            @Override
            public void run()
            {
                mRadar.setObjectsDefaultTexture(AssetsManager.getAssetPathAsFile(getApplicationContext(),
                        "yellow.png"));
                mRadar.setObjectTexture(geometry, AssetsManager.getAssetPathAsFile(getApplicationContext(),
                        "red.png"));
                mAnnotatedGeometriesGroup.setSelectedGeometry(geometry);
            }
        });

        // TODO extract the mascotte clicked and unlock all the ingredient in the db associated to it
    }

    //added by degrigis START ----------------------------------------------------------------------

    /**
     * this function (should) update the SurfaceView of metaio to show the characters
     * when you are at 2m from them or hide when you are further .
     * For now it's called when an user tap  the screen ( registered as callback
     * under the OnConfigurationChange method of OnTouch() )
     */
    @Override
    protected void updateView()
    {

        final int range=20;

        mSurfaceView.queueEvent(new Runnable()
        {

            @Override
            public void run()
            {
                LLACoordinate location = mRomeGeo.getTranslationLLA();
                float distance = (float)MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());

                if(distance < range)
                {
                    mRomeGeo.setVisible(true);
                }
                else
                    mRomeGeo.setVisible(false); // test to hide the character if I am far from it
            }
        });
    }

    //Method called when we are connected to the location service
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest,this);

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
        // do nothing for now

    }


    //added by degrigis END ----------------------------------------------------------------------


    final class MyAnnotatedGeometriesGroupCallback extends AnnotatedGeometriesGroupCallback
    {
        Bitmap mAnnotationBackground, mEmptyStarImage, mFullStarImage;
        int mAnnotationBackgroundIndex;
        ImageStruct texture;
        String[] textureHash = new String[1];
        TextPaint mPaint;
        Lock geometryLock;


        Bitmap inOutCachedBitmaps[] = new Bitmap[] {mAnnotationBackground, mEmptyStarImage, mFullStarImage};
        int inOutCachedAnnotationBackgroundIndex[] = new int[] {mAnnotationBackgroundIndex};

        public MyAnnotatedGeometriesGroupCallback()
        {
            mPaint = new TextPaint();
            mPaint.setFilterBitmap(true); // enable dithering
            mPaint.setAntiAlias(true); // enable anti-aliasing
        }

        @Override
        //Questa funzione gestisce la targhettina con le stelle e la distanza
        public IGeometry loadUpdatedAnnotation(IGeometry geometry, Object userData, IGeometry existingAnnotation)
        {
            if (userData == null)
            {
                return null;
            }

            if (existingAnnotation != null)
            {
                // We don't update the annotation if e.g. distance has changed
                return existingAnnotation;
            }

            String title = (String)userData; // as passed to addGeometry
            LLACoordinate location = geometry.getTranslationLLA();
            float distance = (float)MetaioCloudUtils.getDistanceBetweenTwoCoordinates(location, mSensors.getLocation());


            Bitmap thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            try
            {
                texture =
                        ARELInterpreterAndroidJava.getAnnotationImageForPOI(title, title, distance, "5", thumbnail,
                                null,
                                metaioSDK.getRenderSize(), ARActivity.this,
                                mPaint, inOutCachedBitmaps, inOutCachedAnnotationBackgroundIndex, textureHash);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (thumbnail != null)
                    thumbnail.recycle();
                thumbnail = null;
            }

            mAnnotationBackground = inOutCachedBitmaps[0];
            mEmptyStarImage = inOutCachedBitmaps[1];
            mFullStarImage = inOutCachedBitmaps[2];
            mAnnotationBackgroundIndex = inOutCachedAnnotationBackgroundIndex[0];

            IGeometry resultGeometry = null;

            if (texture != null)
            {
                if (geometryLock != null)
                {
                    geometryLock.lock();
                }

                try
                {
                    // Use texture "hash" to ensure that SDK loads new texture if texture changed
                    resultGeometry = metaioSDK.createGeometryFromImage(textureHash[0], texture, true, false);
                }
                finally
                {
                    if (geometryLock != null)
                    {
                        geometryLock.unlock();
                    }
                }
            }

            return resultGeometry;

        }

        @Override
        public void onFocusStateChanged(IGeometry geometry, Object userData, EGEOMETRY_FOCUS_STATE oldState,
                                        EGEOMETRY_FOCUS_STATE newState)
        {
            MetaioDebug.log("onFocusStateChanged for " + (String)userData + ", " + oldState + "->" + newState);
        }
    }
}


