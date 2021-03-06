package cat.xojan.fittracker.presentation.workout;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.util.LocationFetcher;
import cat.xojan.fittracker.util.LocationUtils;
import cat.xojan.fittracker.util.Utils;

/**
 * Map presenter.
 */
public class MapPresenter implements BasePresenter, LocationFetcher.LocationChangedListener {

    private final Context mContext;
    private final List<Location> mLocationList;
    private Listener mListener;
    private LatLngBounds.Builder mBoundsBuilder;
    private double mWorkoutDistance;
    private int mPadding;

    interface Listener {
        void startWorkout();
        void onDistanceChanged(double distance);
    }

    public static final float MAP_ZOOM = 13;

    private final LocationFetcher mLocationFetcher;
    private GoogleMap mMap;
    private Location mLocation;

    @Inject
    public MapPresenter(LocationFetcher locationFetcher, Context context) {
        mLocationFetcher = locationFetcher;
        mLocationFetcher.setLocationListener(this);
        mContext = context;
        mLocationList = new ArrayList<Location>();
    }

    public void init(GoogleMap map, Listener listener) {
        mMap = map;
        mListener = listener;
        mLocationFetcher.start();

        initMap();
    }

    public void goToLocation(Location location) {
        mLocation = location;
        LatLng latLng = LocationUtils.locationToLatLng(location);
        mBoundsBuilder.include(latLng);
        if (mBoundsBuilder != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), mPadding));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM));
        }
    }

    public void goToLastLocation(int padding) {
        mPadding = - padding / 2;
        if (mLocation != null) {
            goToLocation(mLocation);
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        mListener = null;
        mLocationFetcher.stop();
    }

    public List<Location> stop() {
        mLocationFetcher.stop();
        addLastMarker(LocationUtils.locationToLatLng(mLocation));
        return mLocationList;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationList.add(location);
        LatLng currentPosition = LocationUtils.locationToLatLng(location);
        if (mLocation == null) {
            mListener.startWorkout();
            mBoundsBuilder = new LatLngBounds.Builder();
            addFirstMarker(currentPosition);
        } else {
            LatLng oldPosition = LocationUtils.locationToLatLng(mLocation);
            //create polyline with last location
            addMapPolyline(new PolylineOptions()
                    .geodesic(true)
                    .add(oldPosition)
                    .add(currentPosition)
                    .width(6)
                    .color(Color.BLACK));

            mWorkoutDistance = mWorkoutDistance + (float) SphericalUtil
                    .computeDistanceBetween(oldPosition, currentPosition); //return meters
            mListener.onDistanceChanged(mWorkoutDistance);
        }
        goToLocation(location);
    }

    private void addFirstMarker(LatLng position) {
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(position));
    }

    private void addLastMarker(LatLng position) {
        addMapMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(position));
    }

    private void addMapMarker(MarkerOptions markerOptions) {
        mMap.addMarker(markerOptions);
    }

    private void initMap() {
        mMap.clear();
        mMap.setPadding(40, 40, 40, Utils.dpToPixel(100, mContext));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void addMapPolyline(PolylineOptions polylineOptions) {
        mMap.addPolyline(polylineOptions);
    }
}
