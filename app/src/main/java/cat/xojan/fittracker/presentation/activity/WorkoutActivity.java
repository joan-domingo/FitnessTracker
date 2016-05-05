package cat.xojan.fittracker.presentation.activity;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.controller.FitnessController;

public class WorkoutActivity extends BaseActivity
        implements LocationListener{
    private static final String TAG = WorkoutActivity.class.getSimpleName();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 3; // 3 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3; // 3 seconds

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    LocationManager mLocationManager;
    @Inject
    FitnessController mFitnessController;

    private boolean mIsFirstLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_workout_test);

        mIsFirstLocation = true;
        setUpLocationListener();
        setUpView();

        mFitnessController.setFitnessActivity((String) getIntent().getExtras()
                .get(WorkoutActivity.FITNESS_ACTIVITY));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enable_gps);
            builder.setMessage(R.string.enable_gps_desc);
            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsFirstLocation) {
            Log.i(TAG, "Got First Location");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            mIsFirstLocation = false;
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
        /*getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new WorkoutMapFragment(),
                        WorkoutMapFragment.WORKOUT_FRAGMENT_TAG)
                .commit();*/
    }

    private void setUpLocationListener() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public GoogleApiClient getFitnessClient() {
        return null /*getGoogleApiClient()*/;
    }
}
