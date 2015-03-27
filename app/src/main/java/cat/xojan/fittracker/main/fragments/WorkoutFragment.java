package cat.xojan.fittracker.main.fragments;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cat.xojan.fittracker.BaseFragment;
import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.controllers.DistanceController;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.main.controllers.MapController;
import cat.xojan.fittracker.main.controllers.NotificationController;
import cat.xojan.fittracker.main.controllers.SpeedController;
import cat.xojan.fittracker.main.controllers.TimeController;

public class WorkoutFragment extends BaseFragment {

    @Inject FitnessController fitController;
    @Inject MapController mapController;
    @Inject NotificationController notController;
    @Inject SessionListFragment sessionListFragment;
    @Inject ResultFragment resultFragment;
    @Inject LocationManager mLocationManager;
    @Inject DistanceController distanceController;
    @Inject TimeController timeController;
    @Inject SpeedController speedController;

    @InjectView(R.id.fragment_workout_toolbar) Toolbar toolbar;
    @InjectView(R.id.workout_chronometer) Chronometer chronometer;
    @InjectView(R.id.workout_distance) TextView distanceView;
    @InjectView(R.id.workout_pace) TextView paceView;
    @InjectView(R.id.workout_speed) TextView speedView;
    @InjectView(R.id.waiting_gps_bar) LinearLayout gpsBar;
    @InjectView(R.id.start_bar) LinearLayout startBar;
    @InjectView(R.id.lap_pause_bar) LinearLayout lapPauseBar;
    @InjectView(R.id.resume_finish_bar) LinearLayout resumeFinishBar;

    private LocationListener mFirstLocationListener;
    private LocationListener mLocationListener;

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
        mLocationManager.removeUpdates(mLocationListener);
        //change buttons visibility
        resumeFinishBar.setVisibility(View.GONE);
        //show results
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, resultFragment, Constant.RESULT_FRAGMENT_TAG)
                .commit();

        timeController.finish();
    }

    @OnClick(R.id.workout_button_exit)
    public void onClickExit(Button exitButton) {
        exit();
        notController.dismissNotification();
        //exit
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sessionListFragment)
                .commit();
    }

    @OnClick(R.id.workout_button_exit_gps)
    public void onClickExitGPS(Button exitGPSButton) {
        exit();
        notController.dismissNotification();
        //exit
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, sessionListFragment)
                .commit();
    }


    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        ButterKnife.inject(this, view);
        gpsBar.setVisibility(View.VISIBLE);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

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

        getFirstLocation();
    }

    private void getFirstLocation() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                mFirstLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i(Constant.TAG, "Got First Location");
                mLocationManager.removeUpdates(mFirstLocationListener);
                mapController.gotFirstLocation(location);
                startBar.setVisibility(View.VISIBLE);
                gpsBar.setVisibility(View.GONE);
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

    private void getLocationUpdates() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3,
                mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                mapController.updateTrack(location);
                mapController.updateMap(location.getLatitude(), location.getLongitude());
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

    @Override
    public void onResume() {
        super.onResume();

        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.enable_gps);
            builder.setMessage(R.string.enable_gps_desc);
            builder.create().show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);
        MenuItem attributions = menu.findItem(R.id.action_attributions);
        attributions.setVisible(false);
        MenuItem music = menu.findItem(R.id.action_music);
        music.setVisible(true);
    }

    private void exit() {
        if (mLocationListener != null)
            mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.removeUpdates(mFirstLocationListener);
    }
}