package cat.xojan.fittracker.presentation.sessiondetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
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
import cat.xojan.fittracker.data.entity.Location;
import cat.xojan.fittracker.data.entity.Workout;
import cat.xojan.fittracker.injection.HasComponent;
import cat.xojan.fittracker.injection.component.DaggerSessionDetailsComponent;
import cat.xojan.fittracker.injection.component.SessionDetailsComponent;
import cat.xojan.fittracker.injection.module.SessionDetailsModule;
import cat.xojan.fittracker.presentation.BaseActivity;

/**
 * Workout details.
 */
public class SessionDetailsActivity extends BaseActivity implements
        HasComponent,
        SessionDetailsPresenter.ViewListener,
        OnMapReadyCallback {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_ACTIVITY = "extra_activity";
    public static final String EXTRA_ID = "extra_id";

    @Inject
    SessionDetailsPresenter mPresenter;

    @Bind(R.id.text)
    TextView mTitle;
    @Bind(R.id.activity)
    ImageView mFitnessActivity;

    private SessionDetailsComponent mComponent;
    private Workout mWorkout;
    private List<Location> mLocations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        initializeInjector();
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        mTitle.setText(extras.getString(EXTRA_TITLE));
        mFitnessActivity.setBackground(getResources()
                .getDrawable(ActivityType.toDrawable(extras.getString(EXTRA_ACTIVITY))));

        mPresenter.listenToUpdates(this);
        mPresenter.loadSessionData(extras.getLong(EXTRA_ID));

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_delete:
                mPresenter.deleteSession(mWorkout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object getComponent() {
        return mComponent;
    }

    @Override
    public void updateData(Workout workout) {
        mWorkout = workout;
        mLocations = workout.getLocations();
        displayWorkoutData(workout);
    }

    @Override
    public void onWorkoutDeleted() {
        finish();
    }

    private void initializeInjector() {
        mComponent = DaggerSessionDetailsComponent.builder()
                .appComponent(getApplicationComponent())
                .baseActivityModule(getActivityModule())
                .sessionDetailsModule(new SessionDetailsModule())
                .build();
        mComponent.inject(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.clear();
        map.setPadding(40, 80, 40, 0);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        mPresenter.paintMap(map, mLocations);
    }

    private void displayWorkoutData(Workout workout) {
    }
}
