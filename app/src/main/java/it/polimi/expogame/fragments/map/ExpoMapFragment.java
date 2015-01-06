package it.polimi.expogame.fragments.map;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ExpoMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpoMapFragment extends Fragment implements OnMapReadyCallback {


    private static final String TAG = "ExpoMapFragment";
    private View view;
    //
     private final LatLng INIT_POSITION = new LatLng(45.519899, 9.101893);

    //private final LatLng INIT_POSITION = new LatLng(45.519939, 9.101604);

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

    }


    /**
     * Setup th Map type the camera position and the allowed gestures
     * @param googleMap
     */
    private void initializeMap(GoogleMap googleMap) {
        //setting up map type and camera position
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(INIT_POSITION)      // Sets the center of the map to Expo site
                .zoom(15)                   // Sets the zoom
                .bearing(120)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.getUiSettings().setAllGesturesEnabled(false);
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
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(45.518824, 9.106110))
                .title("Marker"));
    }


}
