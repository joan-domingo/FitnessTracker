package cat.xojan.fittracker.presentation.workout;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.data.entity.ActivityType;
import cat.xojan.fittracker.injection.HasComponent;
import cat.xojan.fittracker.injection.component.DaggerWorkoutComponent;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.BaseActivity;

public class WorkoutActivity extends BaseActivity implements
        HasComponent,
        OnMapReadyCallback,
        MapPresenter.Listener,
        WorkoutPresenter.WorkoutPresenterListener {

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    MapPresenter mMapPresenter;
    @Inject
    WorkoutPresenter mWorkoutPresenter;

    @Bind(R.id.action_button)
    Button mButton;
    @Bind(R.id.chronometer)
    Chronometer mChronometer;
    @Bind(R.id.distance)
    TextView mDistanceView;
    @Bind(R.id.loading_container)
    View mLoadingView;

    private WorkoutComponent mComponent;
    private ActivityType mActivityType;
    private List<Location> mLocationList;
    private double mDistance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeInjector();
        ButterKnife.bind(this);

        mActivityType = (ActivityType) getIntent().getExtras().get(FITNESS_ACTIVITY);
        setTitle(mActivityType.name().toLowerCase());

        mButton.setOnClickListener(new StopWorkoutClickListener());
        mButton.setVisibility(View.GONE);
        mChronometer.setText("00:00:00");

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        mWorkoutPresenter.setupListener(this);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mMapPresenter.destroy();
        mWorkoutPresenter.destroy();
    }

    private void initializeInjector() {
        mComponent = DaggerWorkoutComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .workoutModule(new WorkoutModule(this))
                .build();
        mComponent.inject(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMapPresenter.init(map, this);
    }

    @Override
    public Object getComponent() {
        return mComponent;
    }

    @Override
    public void startWorkout() {
        mButton.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);

        mChronometer.setOnChronometerTickListener(new ChronometerTickListener());
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mWorkoutPresenter.startWorkout();
    }

    @Override
    public void onDistanceChanged(double distance) {
        mDistance = distance;
        mWorkoutPresenter.updateDistanceView(distance, mDistanceView);
    }

    @Override
    public void finishWorkout() {
        finish();
    }

    private class StopWorkoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mLocationList = mMapPresenter.stop();
            mChronometer.stop();
            mWorkoutPresenter.stopWorkout();

            mWorkoutPresenter.saveWorkout((long) mDistance, mActivityType.name(), mLocationList);
        }
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
}
