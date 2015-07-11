package cat.xojan.fittracker.ui.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.ui.controller.DistanceController;
import cat.xojan.fittracker.ui.controller.FitnessController;
import cat.xojan.fittracker.ui.controller.MapController;
import cat.xojan.fittracker.ui.controller.NotificationController;
import cat.xojan.fittracker.ui.controller.SpeedController;
import cat.xojan.fittracker.ui.controller.TimeController;
import cat.xojan.fittracker.ui.listener.LocationUpdateListener;
import cat.xojan.fittracker.ui.listener.RemoveLocationUpdateListener;

public class WorkoutMapFragment extends BaseFragment implements LocationUpdateListener{

    public static final String WORKOUT_FRAGMENT_TAG = "workoutFragment";

    @Inject
    NotificationController notController;
    @Inject
    MapController mapController;
    @Inject
    DistanceController distanceController;
    @Inject
    TimeController timeController;
    @Inject
    SpeedController speedController;
    @Inject
    FitnessController fitController;

    @Bind(R.id.fragment_workout_toolbar)
    Toolbar toolbar;
    @Bind(R.id.waiting_gps_bar)
    LinearLayout gpsBar;
    @Bind(R.id.start_bar)
    LinearLayout startBar;
    @Bind(R.id.workout_chronometer)
    Chronometer chronometer;
    @Bind(R.id.workout_distance)
    TextView distanceView;
    @Bind(R.id.workout_pace)
    TextView paceView;
    @Bind(R.id.workout_speed)
    TextView speedView;
    @Bind(R.id.lap_pause_bar)
    LinearLayout lapPauseBar;
    @Bind(R.id.resume_finish_bar)
    LinearLayout resumeFinishBar;

    private RemoveLocationUpdateListener mListener;

    @OnClick(R.id.workout_button_start)
    public void onClickStart(Button startButton) {
        lapPauseBar.setVisibility(View.VISIBLE);
        startBar.setVisibility(View.GONE);

        fitController.start();
        timeController.start();
        mapController.start();
    }

    @OnClick(R.id.workout_button_lap)
    public void onClickLap(Button lapButton) {
        timeController.lapFinish();
        mapController.lap();
        fitController.saveSegment(false);
        timeController.lapStart();
        distanceController.lap();
        speedController.reset();
    }

    @OnClick(R.id.workout_button_pause)
    public void onClickPause(Button pauseButton) {
        resumeFinishBar.setVisibility(View.VISIBLE);
        lapPauseBar.setVisibility(View.GONE);

        mapController.pause();
        timeController.pause();
        fitController.saveSegment(false);
    }

    @OnClick(R.id.workout_button_resume)
    public void onClickResume(Button resumeButton) {
        lapPauseBar.setVisibility(View.VISIBLE);
        resumeFinishBar.setVisibility(View.GONE);

        timeController.resume();
        fitController.saveSegment(true);
        mapController.resume();
        speedController.reset();
        distanceController.resume();
    }

    @OnClick(R.id.workout_button_finish)
    public void onClickFinish(Button finishButton) {
        notController.dismissNotification();
        //remove location listener
        mListener.notifyRemoveLocationUpdate();
        //change buttons visibility
        resumeFinishBar.setVisibility(View.GONE);
        //show results
        showResult();

        timeController.finish();
        mapController.initMap();
    }

    @OnClick(R.id.workout_button_exit)
    public void onClickExit(Button exitButton) {
        exit();
    }

    @OnClick(R.id.workout_button_exit_gps)
    public void onClickExitGPS(Button exitGPSButton) {
        exit();
    }

    private static View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RemoveLocationUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RemoveLocationUpdateListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_workout, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }

        ButterKnife.bind(this, view);
        setUpView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notController.showNotification();

        //init controllers
        timeController.init(chronometer);
        distanceController.init(distanceView);
        speedController.init(paceView, speedView);
    }

    @Override
    public void onFirstLocationUpdate(Location location) {
        startBar.setVisibility(View.VISIBLE);
        gpsBar.setVisibility(View.GONE);
    }

    @Override
    public void onLocationUpdate(Location location) {
        mapController.updateTrack(location);
        mapController.updateMap(location.getLatitude(), location.getLongitude());
    }

    private void setUpView() {
        toolbar.setVisibility(View.GONE);
        gpsBar.setVisibility(View.VISIBLE);
    }

    private void exit() {
        mListener.notifyRemoveLocationUpdate();
        mapController.initMap();
        notController.dismissNotification();
        getActivity().finish();
    }

    private void showResult() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ResultFragment(), ResultFragment.TAG)
                .commit();
    }
}
