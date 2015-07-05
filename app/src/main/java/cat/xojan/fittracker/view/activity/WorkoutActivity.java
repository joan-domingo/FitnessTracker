package cat.xojan.fittracker.view.activity;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.daggermodules.WorkoutModule;
import cat.xojan.fittracker.view.fragment.WorkoutMapFragment;
import cat.xojan.fittracker.view.listener.LocationUpdateListener;
import cat.xojan.fittracker.view.listener.RemoveLocationUpdateListener;
import cat.xojan.fittracker.view.presenter.WorkoutPresenter;

public class WorkoutActivity extends BaseActivity
        implements LocationListener,
        RemoveLocationUpdateListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3; // 3 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3; // 3 seconds

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    LocationManager mLocationManager;
    @Inject
    WorkoutPresenter mWorkoutPresenter;
    private LocationUpdateListener mLocationUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpLocationListener();
        setUpView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enable_gps);
            builder.setMessage(R.string.enable_gps_desc);
            builder.create().show();
        }
    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new WorkoutModule(this));
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mWorkoutPresenter.getIsFirstLocation()) {
            Log.i(Constant.TAG, "Got First Location");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            mWorkoutPresenter.gotFirstLocation();
            notifyFirstLocationToFragment(location);
        } else {
            notifyUpdateLocationToFragment(location);
        }
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

    private void setUpView() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new WorkoutMapFragment(),
                        WorkoutMapFragment.WORKOUT_FRAGMENT_TAG)
                .commit();
    }

    private void setUpLocationListener() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void notifyFirstLocationToFragment(Location location) {
        mLocationUpdateListener =
                (LocationUpdateListener) getSupportFragmentManager()
                .findFragmentByTag(WorkoutMapFragment.WORKOUT_FRAGMENT_TAG);
        mLocationUpdateListener.onFirstLocationUpdate(location);
    }

    private void notifyUpdateLocationToFragment(Location location) {
        mLocationUpdateListener.onLocationUpdate(location);
    }

    @Override
    public void notifyRemoveLocationUpdate() {
        mLocationManager.removeUpdates(this);
    }
}
