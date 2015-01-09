package cat.xojan.fittracker.workout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.result.ResultFragment;

public class MapController {

    private static GoogleMap mMap;
    private static LatLngBounds.Builder mBoundsBuilder;
    private static boolean isTracking;
    private static boolean isPaused;
    private static LatLng oldPosition;
    private static View mView;
    private FragmentActivity mFragmentActivity;
    private int mLapIndex;
    private List<MarkerOptions> mMarkerList;
    private static MapController instance = null;
    private static List<PolylineOptions> mPolylines;
    private static boolean mFirstLocation;

    public MapController() {}

    public static MapController getInstance() {
        if(instance == null) {
            instance = new MapController();
        }
        return instance;
    }

    public static Handler getHandler() {
        return handler;
    }

    private static float mCurrentLatitude;
    private static float mCurrentLongitude;
    private static float mCurrentAltitude;
    private static float mCurrentAccuracy;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            mCurrentLatitude = bundle.getFloat(Constant.BUNDLE_LATITUDE);
            mCurrentLongitude = bundle.getFloat(Constant.BUNDLE_LONGITUDE);
            mCurrentAltitude = bundle.getFloat(Constant.BUNDLE_ALTITUDE);
            mCurrentAccuracy = bundle.getFloat(Constant.BUNDLE_ACCURACY);

            if (mFirstLocation) {
                Log.i(Constant.TAG, "Got First Location");
                oldPosition = new LatLng(mCurrentLatitude, mCurrentLongitude);
                updateMap(mCurrentLatitude, mCurrentLongitude);
                showStartButton();
                mFirstLocation = false;
            } else {
                updateTrack(mCurrentLatitude, mCurrentLongitude, mCurrentAltitude, mCurrentAccuracy);
                updateMap(mCurrentLatitude, mCurrentLongitude);
            }
        }
    };

    public void init(GoogleMap map, FragmentActivity activity, View view) {
        //init variables
        mFragmentActivity = activity;
        isPaused = false;
        isTracking = false;
        mView = view;
        mLapIndex = 0;
        mMarkerList = new ArrayList<>();
        mPolylines = new ArrayList<>();
        mFirstLocation = true;

        //init google map
        mMap = map;
        mMap.clear();
        mMap.setPadding(40, 280, 40, 120);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //init buttons
        mView.findViewById(R.id.waiting_gps_bar).setVisibility(View.VISIBLE);

        //register location listener
        FitnessController.getInstance().registerListener();
    }

    private static void showStartButton() {
        mView.findViewById(R.id.start_bar).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.waiting_gps_bar).setVisibility(View.GONE);
    }

    private static void updateTrack(double latitude, double longitude, double altitude, double accuracy) {
        LatLng currentPosition = new LatLng(latitude, longitude);
        if (isTracking) {
            //create polyline with last location
            addMapPolyline(new PolylineOptions()
                    .geodesic(true)
                    .add(oldPosition)
                    .add(currentPosition)
                    .width(6)
                    .color(Color.BLACK));

            DistanceController.getInstance().updateDistance(oldPosition, currentPosition);
            ElevationController.getInstance().updateElevationGain(altitude);
            SpeedController.getInstance().updateSpeed();
            FitnessController.getInstance().storeLocation(latitude, longitude, altitude, accuracy);
        }
        if (isTracking || isPaused) {
            mBoundsBuilder.include(currentPosition);
        }
        oldPosition = currentPosition;
    }

    private static void addMapPolyline(PolylineOptions polylineOptions) {
        mMap.addPolyline(polylineOptions);
        mPolylines.add(polylineOptions);
    }

    private static void updateMap(double latitude, double longitude) {
        if (isTracking || isPaused) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 0));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    public void start() {
        //change buttons visibility
        mView.findViewById(R.id.lap_pause_bar).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.start_bar).setVisibility(View.GONE);

        //init google maps settings
        mBoundsBuilder = new LatLngBounds.Builder();
        addStartMarker();
    }

    private void addStartMarker() {
        isTracking = true;
        LatLng position = getCurrentPosition();
        if (position != null) {
            addMapMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(position)
                    .title(String.valueOf(mFragmentActivity.getText(R.string.start))));
            mBoundsBuilder.include(position);
            oldPosition = position;
        }
        FitnessController.getInstance().storeLocation(mCurrentLatitude, mCurrentLongitude, mCurrentAltitude, mCurrentAccuracy);
        ElevationController.getInstance().setFirstAltitude(mCurrentAltitude);
    }

    private LatLng getCurrentPosition() {
        return new LatLng(mCurrentLatitude, mCurrentLongitude);
    }

    private void addMapMarker(MarkerOptions markerOptions) {
        mMap.addMarker(markerOptions);
        mMarkerList.add(markerOptions);
    }

    public void lap() {
        addLapMarker();
    }

    private void addLapMarker() {
        mLapIndex++;
        LatLng position = getCurrentPosition();
        if (position != null) {
            addMapMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(position)
                    .title(mFragmentActivity.getText(R.string.lap).toString() + " " + mLapIndex));
            mBoundsBuilder.include(position);
        }
    }

    public void pause() {
        isPaused = true;
        isTracking = false;

        //change buttons visibility
        mView.findViewById(R.id.resume_finish_bar).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.lap_pause_bar).setVisibility(View.GONE);

        addFinishMarker();
    }

    private void addFinishMarker() {
        LatLng position = getCurrentPosition();
        if (position != null) {
            addMapMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(position)
                    .title(String.valueOf(mFragmentActivity.getText(R.string.finish))));
            mBoundsBuilder.include(position);
        }
    }

    public void resume() {
        isPaused = false;
        isTracking = true;

        //change buttons visibility
        mView.findViewById(R.id.lap_pause_bar).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.resume_finish_bar).setVisibility(View.GONE);

        addStartMarker();
    }

    public void finish() {
        //remove location listener
        FitnessController.getInstance().removeListener();

        //change buttons visibility
        mView.findViewById(R.id.resume_finish_bar).setVisibility(View.GONE);

        //show results
        mFragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ResultFragment(), Constant.RESULT_FRAGMENT_TAG)
                .commit();
    }

    public void exit() {
    }

    public LatLngBounds getBounds() {
        return mBoundsBuilder.build();
    }

    public List<MarkerOptions> getMarkers() {
        return mMarkerList;
    }

    public List<PolylineOptions> getPolylines() {
        return mPolylines;
    }

    public void addKmMarker(String unitCounter) {
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(getCurrentPosition())
                .title(unitCounter));
    }

    public LatLng getLastLocation() {
        return getCurrentPosition();
    }
}
