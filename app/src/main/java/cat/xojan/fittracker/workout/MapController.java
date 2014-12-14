package cat.xojan.fittracker.workout;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;

/**
 * Created by Joan on 14/12/2014.
 */
public class MapController {

    private LocationManager mLocationManager;
    private GoogleMap mMap;
    private LocationListener mFirstLocationListener;
    private LocationListener mLocationListener;
    private LatLngBounds.Builder mBoundsBuilder;
    private boolean isTracking;
    private boolean isPaused;
    private LatLng oldPosition;
    private View mView;
    private FragmentActivity mFragmentActivity;
    private int mLapIndex;

    private static MapController instance = null;

    public MapController() {}

    public static MapController getInstance() {
        if(instance == null) {
            instance = new MapController();
        }
        return instance;
    }

    public void init(GoogleMap map, FragmentActivity activity, View view) {
        //init variables
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        isPaused = false;
        isTracking = false;
        mView = view;
        mLapIndex = 0;

        //init google map
        mMap = map;
        mMap.setPadding(20, 330, 20, 150);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //get first location
        getFirstLocation();
    }

    private void getFirstLocation() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mFirstLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i(Constant.TAG, "Got First Location");
                updateMap(location);
                showStartButton();
                getLocationUpdates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void showStartButton() {
        mView.findViewById(R.id.start_bar).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.waiting_gps_bar).setVisibility(View.GONE);
    }

    private void getLocationUpdates() {
        mLocationManager.removeUpdates(mFirstLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateTrack(location);
                updateMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void updateTrack(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (isTracking) {
            //create polyline with last location
            mMap.addPolyline(new PolylineOptions()
                    .geodesic(true)
                    .add(oldPosition)
                    .add(currentPosition)
                    .width(6)
                    .color(Color.BLACK));

            DistanceController.getInstance().updateDistance(oldPosition, currentPosition);
        }
        if (isTracking || isPaused) {
            mBoundsBuilder.include(currentPosition);
        }
        oldPosition = currentPosition;
    }

    private void updateMap(Location location) {
        if (isTracking || isPaused) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 0));
        } else {
            mLocationManager.removeUpdates(mFirstLocationListener);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
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
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(position)
                    .title(String.valueOf(mFragmentActivity.getText(R.string.start))));
            mBoundsBuilder.include(position);
            oldPosition = position;
        }
    }

    private LatLng getCurrentPosition() {
        LatLng currentPosition = null;
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            //TODO: gps not enabled
        } else {
            Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        return currentPosition;
    }

    public void lap() {
        addLapMarker();
    }

    private void addLapMarker() {
        mLapIndex++;
        LatLng position = getCurrentPosition();
        if (position != null) {
            mMap.addMarker(new MarkerOptions()
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
            mMap.addMarker(new MarkerOptions()
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
        //stop listening location
        mLocationManager.removeUpdates(mLocationListener);

        //change buttons visibility
        mView.findViewById(R.id.resume_finish_bar).setVisibility(View.GONE);

        //show results
        //TODO
    }
}
