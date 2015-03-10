package cat.xojan.fittracker.main.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import javax.inject.Inject;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.main.controllers.FitnessController;
import cat.xojan.fittracker.main.fragments.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.MapController;
import cat.xojan.fittracker.workout.NotificationController;
import cat.xojan.fittracker.workout.SpeedController;
import cat.xojan.fittracker.workout.TimeController;

public class WorkoutFragment extends Fragment {

    @Inject FitnessController fitController;
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
        /* map is already there, just return view as it is */
        }
        NotificationController.getInstance().showNotification(getActivity());
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_workout_toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        GoogleMap map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.workout_map)).getMap();
        Chronometer chronometer = (Chronometer)view.findViewById(R.id.workout_chronometer);
        TextView distanceView = (TextView)view.findViewById(R.id.workout_distance);
        Button startButton = (Button) view.findViewById(R.id.workout_button_start);
        Button lapButton = (Button) view.findViewById(R.id.workout_button_lap);
        Button pauseButton = (Button) view.findViewById(R.id.workout_button_pause);
        Button resumeButton = (Button) view.findViewById(R.id.workout_button_resume);
        Button finishButton = (Button) view.findViewById(R.id.workout_button_finish);
        Button exitButton = (Button) view.findViewById(R.id.workout_button_exit);
        Button exitGPSButton = (Button) view.findViewById(R.id.workout_button_exit_gps);
        TextView paceView = (TextView) view.findViewById(R.id.workout_pace);
        TextView speedView = (TextView) view.findViewById(R.id.workout_speed);

        //init controllers
        MapController.getInstance().init(map, getActivity(), view);
        TimeController.getInstance().init(chronometer);

        DistanceController.getInstance().init(distanceView, getActivity());
        SpeedController.getInstance().init(paceView, speedView, getActivity());

        startButton.setOnClickListener(v -> {
            //start button
            fitController.start();
            TimeController.getInstance().start();
            MapController.getInstance().start();
        });

        lapButton.setOnClickListener(v -> {
            //lap button
            TimeController.getInstance().lapFinish();
            MapController.getInstance().lap();
            fitController.saveSegment(false);
            TimeController.getInstance().lapStart();
            DistanceController.getInstance().lap();
            SpeedController.getInstance().reset();
        });

        pauseButton.setOnClickListener(v -> {
            //pause button
            MapController.getInstance().pause();
            TimeController.getInstance().pause();
            fitController.saveSegment(false);
        });

        resumeButton.setOnClickListener(v -> {
            //resume button
            TimeController.getInstance().resume();
            fitController.saveSegment(true);
            MapController.getInstance().resume();
            SpeedController.getInstance().reset();
            DistanceController.getInstance().resume();

        });

        finishButton.setOnClickListener(v -> {
            //finish button
            NotificationController.getInstance().dismissNotification(getActivity());
            MapController.getInstance().finish();
            TimeController.getInstance().finish();
        });

        exitButton.setOnClickListener(v -> {
            MapController.getInstance().exit();
            NotificationController.getInstance().dismissNotification(getActivity());
            //exit
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SessionListFragment())
                    .commit();
        });

        exitGPSButton.setOnClickListener(v -> {
            MapController.getInstance().exit();
            NotificationController.getInstance().dismissNotification(getActivity());
            //exit
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SessionListFragment())
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
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
}