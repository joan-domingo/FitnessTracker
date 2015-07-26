package cat.xojan.fittracker.ui.activity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.PutDataRequest;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.modules.WorkoutModule;
import cat.xojan.fittracker.ui.controller.DistanceController;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.fragment.FragmentStartWorkout;
import cat.xojan.fittracker.ui.fragment.WorkoutFragment;
import cat.xojan.fittracker.ui.presenter.SessionDataPresenter;

public class WorkoutActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        WorkoutFragment.TrackingStateListener,
        FragmentStartWorkout.WorkoutStartListener {

    private static final String TAG = WorkoutActivity.class.getSimpleName();

    private static final long UPDATE_INTERVAL_MS = 3 * 1000;
    private static final long FASTEST_INTERVAL_MS = 3 * 1000;

    @Bind(R.id.text)
    TextView mTextView;

    private GoogleApiClient mGoogleApiClient;
    private boolean isFirstLocation = true;
    private boolean mIsTracking = false;
    private DistanceController mDistanceController;
    private FitnessController mFitnessController;
    private Location mOldLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        if (!hasGps()) {
            mTextView.setText(R.string.gps_not_found);
        } else {
            mTextView.setText(R.string.waiting_gps);
        }
        String activityType = getIntent().getStringExtra("EXTRA_ACTIVITY_TYPE");
        FitnessController.getInstance().setFitnessActivity(activityType);

        // Build a new GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mDistanceController = DistanceController.getInstance();
        mFitnessController = FitnessController.getInstance();

        mFitnessController = FitnessController.getInstance();
        mFitnessController.init();
    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google client connected");
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(status -> {
                    if (status.getStatus().isSuccess()) {
                        Log.d(TAG, "Successfully requested location updates");
                    } else {
                        Log.e(TAG,
                                "Failed in requesting location updates, "
                                        + "status code: "
                                        + status.getStatusCode()
                                        + ", message: "
                                        + status.getStatusMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // Disconnect from Google Play Services when the Activity stops
    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection to location client suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "connection to location client failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isFirstLocation) {
            mTextView.setVisibility(View.GONE);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new FragmentStartWorkout())
                    .commit();
            isFirstLocation = false;
        } else {
            updateTrack(location);
        }

        mOldLocation = location;
    }

    public void updateTrack(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (mIsTracking) {
            mDistanceController.updateDistance(getOldPosition(mOldLocation), currentPosition);
            mFitnessController.storeLocation(location);
        }
    }

    private LatLng getOldPosition(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void isTracking(boolean isTracking) {
         mIsTracking = isTracking;
    }

    @Override
    public void removeLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void notifyWorkoutStart() {
        //first location
        mFitnessController.storeLocation(mOldLocation);
        //start tracking
        mIsTracking = true;
    }

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new WorkoutModule(this));
    }
}
