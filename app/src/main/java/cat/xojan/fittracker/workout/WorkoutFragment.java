package cat.xojan.fittracker.workout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;

public class WorkoutFragment extends Fragment {

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
        TextView elevationGainView = (TextView) view.findViewById(R.id.workout_elevation_gain);

        //init controllers
        MapController.getInstance().init(map, getActivity(), view);
        TimeController.getInstance().init(chronometer);

        DistanceController.getInstance().init(distanceView, getActivity());
        ElevationController.getInstance().init(elevationGainView, getActivity());
        SpeedController.getInstance().init(paceView, speedView, getActivity());

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start button
                MapController.getInstance().start();
                TimeController.getInstance().start();
                FitnessController.getInstance().start();
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lap button
                MapController.getInstance().lap();
                TimeController.getInstance().lapFinish();
                FitnessController.getInstance().saveSegment();
                TimeController.getInstance().lapStart();
                DistanceController.getInstance().lap();
                ElevationController.getInstance().lap();
                //SpeedController.getInstance().lap();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause button
                MapController.getInstance().pause();
                TimeController.getInstance().pause();
                FitnessController.getInstance().saveSegment();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resume button
                MapController.getInstance().resume();
                TimeController.getInstance().resume();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish button
                MapController.getInstance().finish();
                TimeController.getInstance().finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapController.getInstance().exit();
                //exit
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit();
            }
        });

        exitGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapController.getInstance().exit();
                //exit
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit();
            }
        });

        return view;
    }
}