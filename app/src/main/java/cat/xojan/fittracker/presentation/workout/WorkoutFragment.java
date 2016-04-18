package cat.xojan.fittracker.presentation.workout;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.presentation.BaseFragment;
import cat.xojan.fittracker.util.Utils;


public class WorkoutFragment extends BaseFragment {

    @Inject
    WorkoutPresenter mPresenter;

    @Bind(R.id.chronometer)
    Chronometer mChrono;
    @Bind(R.id.distance)
    TextView mDistanceView;
    @Bind(R.id.workout_main_data)
    View mMainDataView;
    @Bind(R.id.searching_location)
    View mSearchLocationView;
    @Bind(R.id.save)
    Button mSaveButton;
    private double mDistance;
    private String mActivityType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(WorkoutComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workout_fragment, container, false);
        ButterKnife.bind(this, view);

        mChrono.setText("00:00:00");
        mMainDataView.setVisibility(View.INVISIBLE);
        mSearchLocationView.setVisibility(View.VISIBLE);
        mSaveButton.setOnClickListener(new SaveWorkoutClickListener());

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.destroy();
    }

    public void startWorkout(String activityType) {
        mActivityType = activityType;
        mChrono.setOnChronometerTickListener(new ChronometerTickListener());
        mChrono.setBase(SystemClock.elapsedRealtime());
        mChrono.start();

        mMainDataView.setVisibility(View.VISIBLE);
        mSearchLocationView.setVisibility(View.GONE);
        mPresenter.startWorkout();
    }

    public void stopWorkout() {
        mChrono.stop();
        mSaveButton.setVisibility(View.VISIBLE);
        mPresenter.stopWorkout();
    }

    public void updateDistance(double distance) {
        mDistance = distance;
        mDistanceView.setText(Utils.getRightDistance(distance, getActivity()));
    }

    private class ChronometerTickListener implements Chronometer.OnChronometerTickListener {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long t = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int) (t / 3600000);
            int m = (int) (t - h * 3600000) / 60000;
            int s = (int) (t - h * 3600000 - m * 60000) / 1000;
            String hh = h < 10 ? "0" + h : h + "";
            String mm = m < 10 ? "0" + m : m + "";
            String ss = s < 10 ? "0" + s : s + "";
            chronometer.setText(hh + ":" + mm + ":" + ss);
        }
    }

    private class SaveWorkoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mPresenter.saveWorkout((long) mDistance, mActivityType);
        }
    }
}
