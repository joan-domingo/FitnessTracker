package cat.xojan.fittracker.util;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Joan on 30/03/2016.
 */
public class LocationFetcher {

    private final LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private FirstLocationListener mFirstLocationListener;

    public LocationFetcher(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void start() {
        if (mLocation == null) {
            mFirstLocationListener = new FirstLocationListener();
            mLocationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    mFirstLocationListener,
                    null);
        } else {
            listenToLocationUpdates();
        }
    }

    public void stop() {
        if (mLocationListener == null) {
            mLocationManager.removeUpdates(mFirstLocationListener);
        } else {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    @Nullable
    public Location getLocation() {
        return mLocation;
    }

    /**
     * Listen to first location update.
     */
    private class FirstLocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            listenToLocationUpdates();
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
    }

    private void listenToLocationUpdates() {
        mLocationListener = new LocationListener();
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                3,
                mLocationListener);
    }


    /**
     * Location listener.
     */
    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
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
    }
}
