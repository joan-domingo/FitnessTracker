package cat.xojan.fittracker.presentation.workout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.injection.HasComponent;
import cat.xojan.fittracker.injection.component.DaggerWorkoutComponent;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.BaseActivity;


public class WorkoutActivity extends BaseActivity implements HasComponent,
        OnMapReadyCallback,
        MapPresenter.Listener{

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    MapPresenter mMapPresenter;
    @Bind(R.id.appbar)
    AppBarLayout mAppBar;
    @Bind(R.id.fab)
    FloatingActionButton mButton;

    private WorkoutComponent mComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeInjector();
        ButterKnife.bind(this);

        ActivityType activityType = (ActivityType) getIntent().getExtras().get(FITNESS_ACTIVITY);
        setTitle(activityType.name().toLowerCase());

        addFragment(R.id.fragment_container, new WorkoutFragment());
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        mAppBar.setExpanded(false);

        mButton.setVisibility(View.GONE);
        mButton.setOnClickListener(new StopWorkoutClickListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mMapPresenter.destroy();
    }

    private void initializeInjector() {
        mComponent = DaggerWorkoutComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .workoutModule(new WorkoutModule())
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
        mAppBar.setExpanded(true);
        ((WorkoutFragment) getCurrentFragment()).startWorkout();
        mButton.setVisibility(View.VISIBLE);
    }

    private class StopWorkoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((WorkoutFragment) getCurrentFragment()).stopWorkout();
            mAppBar.setExpanded(false);
        }
    }
}
