package cat.xojan.fittracker.presentation.workout;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
        MapPresenter.Listener{

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    MapPresenter mMapPresenter;
    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;
    //@Bind(R.id.fab)
    //FloatingActionButton mButton;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;

    private WorkoutComponent mComponent;
    private ActivityType mActivityType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        setSupportActionBar(mToolbar);

        initializeInjector();
        ButterKnife.bind(this);

        mActivityType = (ActivityType) getIntent().getExtras().get(FITNESS_ACTIVITY);
        setTitle(mActivityType.name().toLowerCase());

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mMapPresenter.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                        || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
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

    }

    @Override
    public void onDistanceChanged(double distance) {

    }

    private class StopWorkoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            List<Location> locationList = mMapPresenter.stop();
            //((WorkoutFragment) getCurrentFragment()).stopWorkout(locationList);
            //mAppBar.setExpanded(false);
        }
    }

}
