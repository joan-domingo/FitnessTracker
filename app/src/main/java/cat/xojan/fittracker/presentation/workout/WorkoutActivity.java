package cat.xojan.fittracker.presentation.workout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.injection.component.DaggerWorkoutComponent;
import cat.xojan.fittracker.injection.component.WorkoutComponent;
import cat.xojan.fittracker.injection.module.WorkoutModule;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Created by Joan on 10/02/2016.
 */
public class WorkoutActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String FITNESS_ACTIVITY = "fitness_activity";

    private WorkoutComponent mComponent;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeInjector();
        ButterKnife.bind(this);

        setTitle(getIntent().getExtras().getString(FITNESS_ACTIVITY));

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(false);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    //expanded
                    LatLng sydney = new LatLng(-33.867, 151.206);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 6));

                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //collapsed
                    LatLng sydney = new LatLng(-33.867, 151.206);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 6));
                }
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mGoogleMap = map;
        LatLng sydney = new LatLng(-33.867, 151.206);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

    }
}