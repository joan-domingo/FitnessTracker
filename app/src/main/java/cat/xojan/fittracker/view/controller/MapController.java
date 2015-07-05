package cat.xojan.fittracker.view.controller;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import cat.xojan.fittracker.R;

public class MapController {

    private final FitnessController fitController;
    private final LocationManager mLocationManager;
    private final DistanceController distanceController;
    private final SpeedController speedController;

    private GoogleMap mMap;
    private LatLngBounds.Builder mBoundsBuilder;
    private boolean isTracking;
    private boolean isPaused;
    private LatLng oldPosition;
    private final Activity mFragmentActivity;
    private int mLapIndex;

    public MapController(Activity activity, GoogleMap map, FitnessController fitnessController,
                         LocationManager locationManager, DistanceController distanceController,
                         SpeedController speedController) {

        mFragmentActivity = activity;
        fitController = fitnessController;
        mLocationManager = locationManager;
        this.distanceController = distanceController;
        this.speedController = speedController;

        mMap = map;
        initMap();
    }

    public void initMap() {
        isPaused = false;
        isTracking = false;
        mLapIndex = 0;

        //init google map
        mMap.clear();
        mMap.setPadding(40, 280, 40, 120);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public void gotFirstLocation(Location location) {
        oldPosition = new LatLng(location.getLatitude(), location.getLongitude());
        updateMap(location.getLatitude(), location.getLongitude());
    }

    public void updateTrack(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (isTracking) {
            //create polyline with last location
            addMapPolyline(new PolylineOptions()
                    .geodesic(true)
                    .add(oldPosition)
                    .add(currentPosition)
                    .width(6)
                    .color(Color.BLACK));

            distanceController.updateDistance(oldPosition, currentPosition);
            speedController.updateSpeed();
            fitController.storeLocation(location);
            speedController.storeSpeed(oldPosition, currentPosition);
        }
        if (isTracking || isPaused) {
            mBoundsBuilder.include(currentPosition);
        }
        oldPosition = currentPosition;
    }

    private void addMapPolyline(PolylineOptions polylineOptions) {
        mMap.addPolyline(polylineOptions);
    }

    public void updateMap(double latitude, double longitude) {
        if (isTracking || isPaused) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 0));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    public void start() {
        //init google maps settings
        mBoundsBuilder = new LatLngBounds.Builder();
        addStartMarker();
    }

    private void addStartMarker() {
        isTracking = true;
        LatLng position = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(position)
                .title(String.valueOf(mFragmentActivity.getText(R.string.start))));

        mBoundsBuilder.include(position);
        oldPosition = position;
        fitController.storeLocation(getCurrentLocation());
    }

    private void addMapMarker(MarkerOptions markerOptions) {
        mMap.addMarker(markerOptions);
    }

    public void lap() {
        addLapMarker();
        fitController.storeLocation(getCurrentLocation());
    }

    private void addLapMarker() {
        mLapIndex++;
        LatLng position = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(position)
                .title(mFragmentActivity.getText(R.string.lap).toString() + " " + mLapIndex));
        mBoundsBuilder.include(position);
    }

    public void pause() {
        isPaused = true;
        isTracking = false;
        speedController.updateSpeed();
        addFinishMarker();
    }

    private void addFinishMarker() {
        LatLng position = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(position)
                .title(String.valueOf(mFragmentActivity.getText(R.string.finish))));
        mBoundsBuilder.include(position);
    }

    public void resume() {
        isPaused = false;
        isTracking = true;
        addStartMarker();
    }

    private Location getCurrentLocation() {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public LatLng getLastPosition() {
        return new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
    }
}
