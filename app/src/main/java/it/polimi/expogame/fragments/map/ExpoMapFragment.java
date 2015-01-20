package it.polimi.expogame.fragments.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import it.polimi.expogame.R;
import it.polimi.expogame.database.ExpoGameDbHelper;
import it.polimi.expogame.database.MascotsTable;
import it.polimi.expogame.providers.MascotsProvider;
import it.polimi.expogame.support.Mascotte;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ExpoMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpoMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {


    private static final String TAG = "ExpoMapFragment";
    private View view;


    //Map Settings
    private float minZoom = 0;
    private final LatLng INIT_POSITION = new LatLng(45.519899, 9.101893);
    //Cisano Bergamasco
    //private final LatLng INIT_POSITION = new LatLng(45.738317, 9.476013);

    private GoogleMap googleMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExpoMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpoMapFragment newInstance() {
        ExpoMapFragment fragment = new ExpoMapFragment();
        return fragment;
    }

    public ExpoMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override
    public void onResume() {
        super.onResume();
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings

        if (!enabled) {
            buildAlertMessageNoGps();
        }
    }

    /**
     * Message box to ask for GPS
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        /* If you place a mapfragment inside a fragment, it crashes when the fragment is
     * loaded a 2nd time. Below solution was found at http://stackoverflow.com/questions/14083950/duplicate-id-tag-null-or-parent-id-with-another-fragment-for-com-google-androi
     */
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            // Inflate the layout for this fragment.
            view = inflater.inflate(R.layout.fragment_expo_map, container, false);
        } catch (InflateException e) {
            // Map is already there, just return view as it is.
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"-------------Map Ready-------------");

        initializeMap(googleMap);


        setupOverlay(googleMap);


        addMarkers(googleMap);

        googleMap.setMyLocationEnabled(true);

    }


    /**
     * Setup th Map type the camera position and the allowed gestures
     * @param googleMap
     */
    private void initializeMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //setting up map type and camera position
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        setUpCameraPosition(googleMap);

        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        googleMap.setOnCameraChangeListener(this);
    }

    /**
     * Take the user back to the map when tryes to zoom out
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {


        if (cameraPosition.zoom < minZoom) {
            setUpCameraPosition(googleMap);
        }

    }

    /**
     * Move camera to the initPosition and with the other camera settings
     * @param googleMap
     */
    private void setUpCameraPosition(GoogleMap googleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(INIT_POSITION)      // Sets the center of the map to Expo site
                .zoom(15)                   // Sets the zoom
                .bearing(120)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



    /**
     * Creates the Overlay for the Expo area
     * @param googleMap
     */
    private void setupOverlay(GoogleMap googleMap) {
        //setting up ground overlay
        GroundOverlayOptions expoMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.expomapv2))
                .position(INIT_POSITION, 2150f, 1050f)
                .bearing(27)
                .zIndex(2f);

        // Add an overlay to the map, retaining a handle to the GroundOverlay object.
        GroundOverlay imageOverlay = googleMap.addGroundOverlay(expoMap);

        //grey background overlay
        GroundOverlayOptions greyBack = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.greyback))
                .position(INIT_POSITION, 4000f, 4000f)
                .bearing(27)
                .transparency(0.7f)
                .zIndex(1f);
        GroundOverlay backOverlay = googleMap.addGroundOverlay(greyBack);
    }

    private void addMarkers(GoogleMap googleMap) {

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(MascotsProvider.CONTENT_URI,
                new String[]{MascotsTable.COLUMN_NAME,MascotsTable.COLUMN_CATEGORY,MascotsTable.COLUMN_LATITUDE,MascotsTable.COLUMN_LONGITUDE},
                null,null,null);
        //adding the markers loaded from the content provider
        while(cursor.moveToNext()){
            float lat = cursor.getFloat(cursor.getColumnIndexOrThrow(MascotsTable.COLUMN_LATITUDE));
            float lng = cursor.getFloat(cursor.getColumnIndexOrThrow(MascotsTable.COLUMN_LONGITUDE));

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(cursor.getString(cursor.getColumnIndexOrThrow(MascotsTable.COLUMN_NAME)))
                    .snippet(cursor.getString(cursor.getColumnIndexOrThrow(MascotsTable.COLUMN_CATEGORY))));
            
        }

        //Stub to position a mascot on the expo area
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(45.518824, 9.106110))
                .title("Tommy Tomato")
                .snippet("Verdura"));

    }



}
