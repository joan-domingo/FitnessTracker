package cat.xojan.fittracker.workout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cat.xojan.fittracker.R;

/**
 * Created by Joan on 14/12/2014.
 */
public class WorkoutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        TextView chronoView = (TextView)view.findViewById(R.id.workout_chronometer);
        TextView distanceView = (TextView)view.findViewById(R.id.workout_distance);
        Button startButton = (Button) view.findViewById(R.id.workout_button_start);
        Button lapButton = (Button) view.findViewById(R.id.workout_button_lap);
        Button pauseButton = (Button) view.findViewById(R.id.workout_button_pause);
        Button resumeButton = (Button) view.findViewById(R.id.workout_button_resume);
        Button finishButton = (Button) view.findViewById(R.id.workout_button_finish);

        //init controllers


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start button
            }
        });

        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lap button
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause button
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resume button
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish button
            }
        });

        return view;
    }
}
