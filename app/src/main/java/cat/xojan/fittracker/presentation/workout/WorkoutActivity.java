package cat.xojan.fittracker.presentation.workout;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.domain.ActivityType;
import cat.xojan.fittracker.injection.component.DaggerWorkoutComponent;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.util.LocationFetcher;
import cat.xojan.fittracker.util.LocationUtils;


public class WorkoutActivity extends BaseActivity implements
        OnMapReadyCallback,
        LocationFetcher.LocationChangedListener {

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    @Inject
    LocationFetcher mLocationFetcher;

    private WorkoutComponent mComponent;
    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeInjector();
        ButterKnife.bind(this);

        ActivityType activityType = (ActivityType) getIntent().getExtras().get(FITNESS_ACTIVITY);
        setTitle(activityType.name());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationFetcher.setLocationListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
        mMap = map;
        Location location = mLocationFetcher.getLocation();
        if (location != null) {
            goToLocation(location);
        } else {
            mLocationFetcher.start();
        }
    }

    private void goToLocation(Location location) {
        LatLng latLng = LocationUtils.locationToLatLng(location);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onLocationChanged(Location location) {
        goToLocation(location);
    }
}
