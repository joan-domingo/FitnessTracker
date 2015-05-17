package cat.xojan.fittracker.workout;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.workout.controller.DistanceController;
import cat.xojan.fittracker.workout.controller.FitnessController;
import cat.xojan.fittracker.workout.controller.TimeController;

public class WorkoutFragment extends Fragment implements LocationListener {

    private static final long UPDATE_INTERVAL_MS = 3 * 1000;
    private static final long FASTEST_INTERVAL_MS = 3 * 1000;

    @InjectView(R.id.chrono)
    Chronometer mChronometerView;

    @InjectView(R.id.distance)
    TextView mDistanceView;

    @InjectView(R.id.bar_lap_pause)
    LinearLayout mLapPauseView;

    @InjectView(R.id.bar_resume_finish)
    LinearLayout mResumeFinishView;

    private TimeController mTimeController;
    private FitnessController mFitnessController;
    private DistanceController mDistanceController;
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "WorkoutFragment";
    private boolean isTracking = false;
    private LatLng mOldPosition;

    @OnClick(R.id.button_lap)
    public void onLapButtonClicked(Button lapButton){
        mTimeController.lapFinish();
        mFitnessController.saveSegment(false);
        mTimeController.lapStart();
        mDistanceController.lap();
    }

    @OnClick(R.id.button_pause)
    public void onPauseButtonClicked(Button lapButton){
        showLapPause(false);
        isTracking = false;

        mTimeController.pause();
        mFitnessController.saveSegment(false);
    }

    @OnClick(R.id.button_resume)
    public void onResumeClicked(Button resumeButton) {
        showLapPause(true);
        isTracking = true;

        mTimeController.resume();
        mFitnessController.saveSegment(true);
        mDistanceController.resume();
    }

    @OnClick(R.id.button_finish)
    public void onFinishButtonClicked(Button finishButton){
        //remove location listener
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        //show results
        getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ResultFragment())
                .commit();

        mTimeController.finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        ButterKnife.inject(this, view);

        mTimeController = TimeController.getInstance();
        mTimeController.initChronometer(mChronometerView);
        showLapPause(true);

        mDistanceController = DistanceController.getInstance();
        mDistanceController.init(getActivity(), mDistanceView);

        mFitnessController = FitnessController.getInstance();
        mFitnessController.init(getActivity());

        //first location
        mGoogleApiClient = FitnessController.getClient();
        Location firstLocation = mDistanceController.getFirstLocation();
        mFitnessController.storeLocation(firstLocation);
        mOldPosition = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());

        return view;
    }

    private void showLapPause(boolean b) {
        if (b) {
            mLapPauseView.setVisibility(View.VISIBLE);
            mResumeFinishView.setVisibility(View.GONE);
        } else {
            mLapPauseView.setVisibility(View.GONE);
            mResumeFinishView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateTrack(location);
    }

    public void updateTrack(Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (isTracking) {
            mDistanceController.updateDistance(mOldPosition, currentPosition);
            mFitnessController.storeLocation(location);
        }
        mOldPosition = currentPosition;
    }
}
