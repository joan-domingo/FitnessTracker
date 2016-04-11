package cat.xojan.fittracker.presentation.workout;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import cat.xojan.fittracker.presentation.BasePresenter;
import cat.xojan.fittracker.util.LocationUtils;

/**
 * Map presenter.
 */
public class MapPresenter implements BasePresenter {

    public static final float MAP_ZOOM = 13;

    private GoogleMap mMap;
    private Location mLocation;

    public void setUp(GoogleMap map) {
        mMap = map;
    }

    public void goToLocation(Location location) {
        mLocation = location;
        LatLng latLng = LocationUtils.locationToLatLng(location);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM));
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    public void goToLastLocation() {
        goToLocation(mLocation);
    }
}
